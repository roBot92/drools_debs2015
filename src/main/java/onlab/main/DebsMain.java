package onlab.main;

import java.io.FileNotFoundException;
import java.math.BigDecimal;
import java.text.ParseException;
import java.util.Date;
import java.util.List;
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
	public static long TEST_INTERVAL_IN_IN_MS = 1 * 60 * 60 * 1000;

	public static void main(String[] args) throws FileNotFoundException, ParseException {

		//runTask1();
		 runTask2();
	}

	

	public static void runTask1() {
		List<TaxiLog> taxiLogs = null;
		CellHelper chelper = new CellHelper(FIRST_CELL_X, FIRST_CELL_Y, SHIFT_X.divide(BigDecimal.valueOf(2)),
				SHIFT_Y.divide(BigDecimal.valueOf(2)), 600);

		try (DataFileParser dataFileParser = new DataFileParser(DATA_FILE_URL, DELIMITER, columncount, chelper)) {

			FrequentRoutesToplistSet mostFrequentRoutes = new FrequentRoutesToplistSet();

			KieSession kSession = initializeSession();

			// Adding global toplist to the session
			kSession.setGlobal("mostFrequentRoutes", mostFrequentRoutes);


			SessionPseudoClock clock = kSession.getSessionClock();
			

			taxiLogs = dataFileParser.parseNextLinesFromCSVGroupedByDropoffDate();
			long currentTime = DataFileParser.getCURRENT_TIME();
			long startingTime = DataFileParser.getCURRENT_TIME();
			
			clock.advanceTime(startingTime, TimeUnit.MILLISECONDS);
			
			long counter = taxiLogs.size();
			while (currentTime - startingTime <= TEST_INTERVAL_IN_IN_MS) {
				kSession.insert(new Tick(currentTime));
				if (currentTime >= DataFileParser.getCURRENT_TIME()) {
					for (TaxiLog tlog : taxiLogs) {
						tlog.setInserted(System.currentTimeMillis());
						kSession.insert(tlog);
						counter++;
					}
					taxiLogs = dataFileParser.parseNextLinesFromCSVGroupedByDropoffDate();
					// System.out.println(freqRouteToplist);
				}
				
				kSession.fireAllRules();
				
				if ((currentTime - startingTime) % (1000 * 60 * 60) == 0) {
					System.out.println(mostFrequentRoutes);
					System.out.println("current time:" + new Date(clock.getCurrentTime()) + " processed:" + counter);
				}
				currentTime += 1000;
				clock.advanceTime(1, TimeUnit.SECONDS);
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}

	public static void runTask2() {
		List<TaxiLog> taxiLogs = null;
		CellHelper chelper = new CellHelper(FIRST_CELL_X, FIRST_CELL_Y, SHIFT_X,
				SHIFT_Y, 300);

		try (DataFileParser dataFileParser = new DataFileParser(DATA_FILE_URL, DELIMITER, columncount, chelper)) {

			ProfitableAreaToplistSet mostProfitableAreas = new ProfitableAreaToplistSet();

			KieSession kSession = initializeSession();

			// Adding global toplist to the session
			kSession.setGlobal("mostProfitableAreas", mostProfitableAreas);


			SessionPseudoClock clock = kSession.getSessionClock();
			

			taxiLogs = dataFileParser.parseNextLinesFromCSVGroupedByDropoffDate();
			long currentTime = DataFileParser.getCURRENT_TIME();
			long startingTime = DataFileParser.getCURRENT_TIME();
			
			clock.advanceTime(startingTime, TimeUnit.MILLISECONDS);
			
			long counter = taxiLogs.size();
			while (currentTime - startingTime <= TEST_INTERVAL_IN_IN_MS) {
				kSession.insert(new Tick(currentTime));
				if (currentTime >= DataFileParser.getCURRENT_TIME()) {
					for (TaxiLog tlog : taxiLogs) {
						tlog.setInserted(System.currentTimeMillis());
						kSession.insert(tlog);
						counter++;
					}
					taxiLogs = dataFileParser.parseNextLinesFromCSVGroupedByDropoffDate();
				}
				
				kSession.fireAllRules();
				
				if ((currentTime - startingTime) % (1000 * 60 * 60) == 0) {
					System.out.println(mostProfitableAreas);
					System.out.println("current time:" + new Date(clock.getCurrentTime()) + " processed:" + counter);
				}
				currentTime += 1000;
				clock.advanceTime(1, TimeUnit.SECONDS);
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}

	public static KieSession initializeSession() {
		KieServices ks = KieServices.Factory.get();
		KieContainer kContainer = ks.getKieClasspathContainer();
		KieSessionConfiguration config = ks.newKieSessionConfiguration();
		config.setOption(ClockTypeOption.get("pseudo"));
		return kContainer.newKieSession("ksession-rules", config);
	}
}
