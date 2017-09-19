package onlab.main;

import java.io.FileNotFoundException;
import java.math.BigDecimal;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.SortedSet;
import java.util.concurrent.TimeUnit;

import org.drools.core.time.SessionPseudoClock;
import org.kie.api.KieServices;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.KieSessionConfiguration;
import org.kie.api.runtime.conf.ClockTypeOption;

import onlab.event.AreaWithProfit;
import onlab.event.Route;
import onlab.event.TaxiLog;
import onlab.event.Tick;
import onlab.positioning.*;
import onlab.utility.DataFileParser;
import onlab.utility.FrequentRoutesToplistSet;
import onlab.utility.ProfitableAreaToplistSet;


public class DebsMain {

	public static String DATA_FILE_URL = "C:\\Users\\Boti\\git\\onlab_tavasz\\src\\main\\resources\\onlab\\sorted_data.csv";
	public static String DELIMITER = ",";
	public static int columncount = 17;
	public static BigDecimal FIRST_CELL_X = BigDecimal.valueOf(-74.913585);
	public static BigDecimal FIRST_CELL_Y = BigDecimal.valueOf(41.474937);
	public static BigDecimal SHIFT_Y = BigDecimal.valueOf(0.004491556);
	public static BigDecimal SHIFT_X = BigDecimal.valueOf(0.005986);

	public static void main(String[] args) throws FileNotFoundException, ParseException {
		List<TaxiLog> taxiLogs = null;
		CellHelper chelper = new CellHelper(FIRST_CELL_X, FIRST_CELL_Y,
				SHIFT_X.divide(BigDecimal.valueOf(2)),
				SHIFT_Y.divide(BigDecimal.valueOf(2)), 600);

		/*
		 * try { taxiLogs =
		 * DataFileParser.parseCSVIntoTaxiLogList(DATA_FILE_URL, DELIMITER,
		 * columncount, 7500, chelper);
		 * 
		 * } catch (FileNotFoundException e) {
		 * 
		 * e.printStackTrace(); throw e; }
		 */

		DataFileParser dataFileParser = new DataFileParser(DATA_FILE_URL, DELIMITER, columncount, chelper);

		KieServices ks = KieServices.Factory.get();
		KieContainer kContainer = ks.getKieClasspathContainer();
		KieSessionConfiguration config = ks.newKieSessionConfiguration();
		config.setOption(ClockTypeOption.get("pseudo"));
		KieSession kSession = kContainer.newKieSession("ksession-rules", config);

		// Task1
		SortedSet<Route> mostFrequentRoutes = new FrequentRoutesToplistSet<Route>();
		// Task2
		ProfitableAreaToplistSet<AreaWithProfit> mostProfitableAreas = new ProfitableAreaToplistSet<AreaWithProfit>();

		// Adding global toplists to the session
		kSession.setGlobal("mostFrequentRoutes", mostFrequentRoutes);
		kSession.setGlobal("mostProfitableAreas", mostProfitableAreas);

		taxiLogs = dataFileParser.parseNextLinesFromCSVGroupedByDropoffDate();

		long previousTimeInMillis = taxiLogs.get(0).getDropoff_datetime().getTime();
		

		SessionPseudoClock clock = kSession.getSessionClock();
		clock.advanceTime(previousTimeInMillis, TimeUnit.MILLISECONDS);

		// For measuring
		long linecounter = 0;
		List<Long> averages = new ArrayList<Long>();
		List<Long> timeDifferencesForAverages = new ArrayList<Long>();
		timeDifferencesForAverages.add(previousTimeInMillis);
		
		
		long realTime = System.currentTimeMillis();

		while (dataFileParser.hasNextLine() && linecounter < 200000) {

			taxiLogs = dataFileParser.parseNextLinesFromCSVGroupedByDropoffDate();

			long currentTimeInMillis = taxiLogs.get(0).getDropoff_datetime().getTime();
			
			stepXSeconds(kSession, clock, (currentTimeInMillis - previousTimeInMillis) / 1000 +1);

			kSession.fireAllRules();
			for (TaxiLog tlog : taxiLogs) {
				tlog.setInserted(System.currentTimeMillis());
				kSession.insert(tlog);
				linecounter++;
				kSession.fireAllRules();
				if (linecounter % 1000 == 0) {
					averages.add(System.currentTimeMillis() - realTime);
					timeDifferencesForAverages.add(tlog.getDropoff_datetime().getTime());
					realTime = System.currentTimeMillis();
				}
			}

			kSession.fireAllRules();

			previousTimeInMillis = currentTimeInMillis;
			System.out.println(mostProfitableAreas);
			
			

		}
		for (int i = 0; i < averages.size(); i++) {
			System.out.println((i * 1000) + ": " + averages.get(i) / 1000 + "sec to parse "
					+ (timeDifferencesForAverages.get(i + 1) - timeDifferencesForAverages.get(i)) / 1000 + " sec");
		}



		System.out.println(mostProfitableAreas);

	}

	private static void stepXSeconds(KieSession kSession, SessionPseudoClock clock, long sec) {
		kSession.insert(new Tick(clock.getCurrentTime()));
		for (long i = 0; i < sec - 1; i++) {
			clock.advanceTime(1, TimeUnit.SECONDS);
			kSession.insert(new Tick(clock.getCurrentTime(), System.currentTimeMillis()));
			kSession.fireAllRules();
		}
	}
}
