package onlab.main;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
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
import onlab.utility.ToplistSetInterface;

public class DebsMain {

	public static String DATA_FILE_URL = "F:\\\\dev\\sorted_data.csv";
	public static String DELIMITER = ",";
	public static int columncount = 17;
	public static BigDecimal FIRST_CELL_X = BigDecimal.valueOf(-74.913585);
	public static BigDecimal FIRST_CELL_Y = BigDecimal.valueOf(41.474937);
	public static BigDecimal SHIFT_Y = BigDecimal.valueOf(0.004491556);
	public static BigDecimal SHIFT_X = BigDecimal.valueOf(0.005986);
	public static long TEST_INTERVAL_IN_IN_MS = 60 * 60 * 1000;
	public static String measuringResultFileName = "measuringResultsTask1.txt";
	public static String resultToCompareFileName = "comparableResultsTask1.txt";

	public static void main(String[] args) throws FileNotFoundException, ParseException {

		
		//runTask1(false);
	runTask2(false);
	}

	public static void runTask1(boolean measuringMode) {
		runTask(new FrequentRoutesToplistSet(),"mostFrequentRoutes", measuringMode,1);
	}

	public static void runTask2(boolean measuringMode) {
		runTask(new ProfitableAreaToplistSet(),"mostProfitableAreas", measuringMode,2);
	}

	public static KieSession initializeSession() {
		KieServices ks = KieServices.Factory.get();
		KieContainer kContainer = ks.getKieClasspathContainer();
		KieSessionConfiguration config = ks.newKieSessionConfiguration();
		config.setOption(ClockTypeOption.get("pseudo"));
		return kContainer.newKieSession("ksession-rules", config);
	}
	
	public static void runTask(ToplistSetInterface toplist, String globalToplistVariableName, boolean measuringMode, int divisor){

		List<TaxiLog> taxiLogs = null;
		CellHelper chelper = new CellHelper(FIRST_CELL_X, FIRST_CELL_Y, SHIFT_X.divide(BigDecimal.valueOf(divisor)), SHIFT_Y.divide(BigDecimal.valueOf(divisor)), 300*divisor);
		Runtime runtime = Runtime.getRuntime();
		File measuringResults = null;
		BufferedWriter measuringResultsWriter = null;

		File resultsToCompare = null;
		BufferedWriter resultsToCompareWriter = null;

		if (measuringMode) {
			measuringResults = new File(measuringResultFileName);
			if (measuringResults.exists()) {
				measuringResults.delete();
			}
			try {
				measuringResultsWriter = new BufferedWriter(new FileWriter(measuringResults));
			} catch (IOException e1) {
				e1.printStackTrace();
				return;
			}
		} else {
			resultsToCompare = new File(resultToCompareFileName);
			if (resultsToCompare.exists()) {
				resultsToCompare.delete();
			}
			try {
				resultsToCompareWriter = new BufferedWriter(new FileWriter(resultsToCompare));
			} catch (IOException e1) {
				e1.printStackTrace();
				return;
			}
		}

		try (DataFileParser dataFileParser = new DataFileParser(DATA_FILE_URL, DELIMITER, columncount, chelper)) {


			KieSession kSession = initializeSession();

			// Adding global toplist to the session
			kSession.setGlobal(globalToplistVariableName, toplist);

			SessionPseudoClock clock = kSession.getSessionClock();

			taxiLogs = dataFileParser.parseNextLinesFromCSVGroupedByDropoffDate();
			long currentTime = DataFileParser.getCURRENT_TIME();
			long startingTime = DataFileParser.getCURRENT_TIME();

			clock.advanceTime(startingTime, TimeUnit.MILLISECONDS);

			long counter = taxiLogs.size();
			long previousTime = System.currentTimeMillis();
			String previousToplistWithoutDelay = null;
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
				String toplistStringWithoutDelay = toplist.toStringWithoutDelay();
				// Mindig kiírjuk, ha volt változás
				if (!toplistStringWithoutDelay.equals(previousToplistWithoutDelay)) {
					if (measuringMode) {
						System.out.println(toplist);
					//	System.out
					//			.println("current time:" + new Date(clock.getCurrentTime()) + " processed:" + counter);
						previousToplistWithoutDelay = toplistStringWithoutDelay;
					} else {
						resultsToCompareWriter.write(toplistStringWithoutDelay);
						resultsToCompareWriter.write("\n");
						resultsToCompareWriter.write(
								"current time:" + new Date(clock.getCurrentTime()) + " processed:" + counter + "\n");
						previousToplistWithoutDelay = toplistStringWithoutDelay;
					}

				}
				// Óránként megmérjük, hogy mennyit dolgoztunk fel, és mennyi
				// idõ alatt.
				if (measuringMode && (currentTime - startingTime) % (1000 * 60 * 60) == 0) {
					try {
						// CSV formátum: aktuális idõpont;eddig feldolgozott sorok száma; elõzõ kiírás óta eltelt idõ;aktuális felhasznált JVM memóra;
						//aktuális átlagos késleltetés; aktuális legkisebb késleltetés;aktuális legnagyobb késleltetés
						measuringResultsWriter.write(new Date(clock.getCurrentTime()) + ";" + counter
								+ ((System.currentTimeMillis() - previousTime) / 1000 + ";"
										+ ((double) runtime.totalMemory()) / 1000000 + ";"
										+ toplist.getAverageDelay() + ";" + toplist.getMinDelay()
										+ ";" + toplist.getMaxDelay() + "\n"));
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

					previousTime = System.currentTimeMillis();
				}
				currentTime += 1000;
				clock.advanceTime(1, TimeUnit.SECONDS);
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (measuringResultsWriter != null) {
					measuringResultsWriter.close();
				}
				if (resultsToCompareWriter != null) {
					resultsToCompareWriter.close();
				}

			} catch (IOException e) {
				e.printStackTrace();
				return;
			}
		}
	
	}
}
