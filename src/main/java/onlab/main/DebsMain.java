package onlab.main;

import java.io.FileNotFoundException;
import java.math.BigDecimal;
import java.util.List;
import java.util.SortedSet;
import java.util.concurrent.TimeUnit;

import org.drools.core.time.SessionPseudoClock;
import org.kie.api.KieServices;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.KieSessionConfiguration;
import org.kie.api.runtime.conf.ClockTypeOption;
import onlab.event.TaxiLog;
import onlab.event.Tick;
import onlab.positioning.*;
import onlab.utility.FrequentRoutesToplistSet;
import onlab.utility.ProfitableAreaToplistSet;

@SuppressWarnings("restriction")
public class DebsMain {

	private static String DATA_FILE_URL = "C:\\Users\\Boti\\onlab\\onlab_tavasz\\src\\main\\resources\\data\\sorted_data.csv";
	private static String DELIMITER = ",";
	private static int columncount = 17;
	private static BigDecimal FIRST_CELL_X = BigDecimal.valueOf(-74.913585);
	private static BigDecimal FIRST_CELL_Y = BigDecimal.valueOf(41.474937);
	private static BigDecimal SHIFT_Y = BigDecimal.valueOf(0.004491556);
	private static BigDecimal SHIFT_X = BigDecimal.valueOf(0.005986);

	public static void main(String[] args) throws FileNotFoundException {
		List<TaxiLog> taxiLogs = null;
		CellHelper chelper = new CellHelper(FIRST_CELL_X, FIRST_CELL_Y, SHIFT_X.divide(BigDecimal.valueOf(2)),
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
		SortedSet<AreaWithProfit> mostProfitableAreas = new ProfitableAreaToplistSet<AreaWithProfit>();

		// Adding global toplists to the session
		kSession.setGlobal("mostFrequentRoutes", mostFrequentRoutes);
		kSession.setGlobal("mostProfitableAreas", mostProfitableAreas);

		taxiLogs = dataFileParser.parseNextLinesFromCSVGroupedByDropoffDate();
		
		long previousTimeInMillis = taxiLogs.get(0).getDropoff_datetime().getTime();;

		SessionPseudoClock clock = kSession.getSessionClock();
		clock.advanceTime(previousTimeInMillis, TimeUnit.MILLISECONDS);

		for (int i = 0; i < 10; i++) {
			taxiLogs = dataFileParser.parseNextLinesFromCSVGroupedByDropoffDate();
			long currentTimeInMillis = taxiLogs.get(0).getDropoff_datetime().getTime();
			stepXSeconds(kSession, clock, (currentTimeInMillis-previousTimeInMillis) / 1000);
			for (TaxiLog tlog : taxiLogs) {
				kSession.insert(tlog);
			}
			kSession.fireAllRules();
			previousTimeInMillis = currentTimeInMillis;
			
			
		}

		/*
		 * for(long i = 0 ; i < 31*60 ; i++){ clock.advanceTime(1,
		 * TimeUnit.SECONDS); kSession.insert(new
		 * Tick(start_time_in_milliseconds + i*1000));
		 * 
		 * kSession.fireAllRules(); }
		 */

		// clock.advanceTime(1, TimeUnit.DAYS);

		/*
		 * for (Route r : mostFrequentRoutes) { System.out.println(r); }
		 */
		
		System.out.println(mostProfitableAreas);

	}

	private static void stepXSeconds(KieSession kSession, SessionPseudoClock clock, long sec) {
		kSession.insert(new Tick(clock.getCurrentTime()));
		for (long i = 0; i < sec-1; i++) {
			clock.advanceTime(1, TimeUnit.SECONDS);
			kSession.insert(new Tick(clock.getCurrentTime()));
			kSession.fireAllRules();
		}
	}
}
