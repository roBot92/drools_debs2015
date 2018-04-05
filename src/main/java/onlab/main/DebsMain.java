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

	public static final String DATA_FILE_URL = "F:\\\\dev\\sorted_data.csv";
	public static final String DELIMITER = ",";
	public static final int columncount = 17;
	public static final BigDecimal FIRST_CELL_X = BigDecimal.valueOf(-74.913585);
	public static final BigDecimal FIRST_CELL_Y = BigDecimal.valueOf(41.474937);
	public static final BigDecimal SHIFT_Y = BigDecimal.valueOf(0.004491556);
	public static final BigDecimal SHIFT_X = BigDecimal.valueOf(0.005986);
	public static final long TEST_INTERVAL_IN_IN_MS = 1 * 60 * 60 * 1000;
	public static final long BENCHMARK_FREQUENCY_IN_MS = 1000 * 60;
	public static final long SLEEP_TIME_IN_MS = 100;
	public static final int TIME_MEASURING_MODE = 1;
	public static final int MEMORY_MEASURING_MODE = 2;
	public static final int OUTPUT_COOMPARING_MODE = 3;

	public static String task1TimeMeasuringResultFileName = "timeMeasuringResultsTask1.csv";
	public static String task1MemoryMeasuringResultFileName = "memoryMeasuringResultsTask1.csv";
	public static String task1ResultToCompareFileName = "comparableResultsTask1.csv";

	public static String task2TimeMeasuringResultFileName = "timeMeasuringResultsTask2.csv";
	public static String task2MemoryMeasuringResultFileName = "memoryMeasuringResultsTask2.csv";
	public static String task2ResultToCompareFileName = "comparableResultsTask2.csv";

	public static void main(String[] args) throws FileNotFoundException, ParseException {

		//runTask1();
		 runTask2();
	}

	public static void runTask1() {
		runTask(new FrequentRoutesToplistSet(), "mostFrequentRoutes", task1ResultToCompareFileName, OUTPUT_COOMPARING_MODE, 1);
		runTask(new FrequentRoutesToplistSet(), "mostFrequentRoutes", task1TimeMeasuringResultFileName,	TIME_MEASURING_MODE, 1);

		runTask(new FrequentRoutesToplistSet(), "mostFrequentRoutes", task1MemoryMeasuringResultFileName,MEMORY_MEASURING_MODE, 1);
	}

	public static void runTask2() {

		runTask(new ProfitableAreaToplistSet(), "mostProfitableAreas", task2ResultToCompareFileName,
				OUTPUT_COOMPARING_MODE, 2);
	//	runTask(new ProfitableAreaToplistSet(), "mostProfitableAreas", task2TimeMeasuringResultFileName,
	//			TIME_MEASURING_MODE, 2);
	//	runTask(new ProfitableAreaToplistSet(), "mostProfitableAreas", task2MemoryMeasuringResultFileName,
	//			MEMORY_MEASURING_MODE, 2);
	}

	public static KieSession initializeSession() {
		KieServices ks = KieServices.Factory.get();
		KieContainer kContainer = ks.getKieClasspathContainer();
		KieSessionConfiguration config = ks.newKieSessionConfiguration();
		config.setOption(ClockTypeOption.get("pseudo"));
		return kContainer.newKieSession("ksession-rules", config);
	}

	public static void runTask(ToplistSetInterface toplist, String globalToplistVariableName, String fileName,
			int runningMode, int divisor) {

		List<TaxiLog> taxiLogs = null;
		CellHelper chelper = new CellHelper(FIRST_CELL_X, FIRST_CELL_Y, SHIFT_X.divide(BigDecimal.valueOf(divisor)),
				SHIFT_Y.divide(BigDecimal.valueOf(divisor)), 300 * divisor);
		Runtime runtime = Runtime.getRuntime();
		File resultFile = null;
		BufferedWriter resultFileWriter = null;

		resultFile = new File(fileName);
		if (resultFile.exists()) {
			resultFile.delete();
		}
		try {
			resultFileWriter = new BufferedWriter(new FileWriter(resultFile));
		} catch (IOException e1) {
			e1.printStackTrace();
			return;
		}

		try (DataFileParser dataFileParser = new DataFileParser(DATA_FILE_URL, DELIMITER, columncount, chelper)) {

			KieSession kSession = initializeSession();

			// Hozzáadjuk globális változóként a toplistát
			kSession.setGlobal(globalToplistVariableName, toplist);

			SessionPseudoClock clock = kSession.getSessionClock();

			taxiLogs = dataFileParser.parseNextLinesFromCSVGroupedByDropoffDate();
			long currentTime = DataFileParser.getCURRENT_TIME();
			long startingTime = DataFileParser.getCURRENT_TIME();

			clock.advanceTime(startingTime, TimeUnit.MILLISECONDS);

			long counter = taxiLogs.size();
			long previousTime = System.currentTimeMillis();
			String previousToplistWithoutDelay = null;

			// A megadott ideig dolgoz fel a teszt
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
				handlePrintActions(toplist, runningMode, previousToplistWithoutDelay, resultFileWriter, currentTime,
						counter, startingTime, BENCHMARK_FREQUENCY_IN_MS, previousTime, runtime);

				previousTime = System.currentTimeMillis();

				currentTime += 1000;
				clock.advanceTime(1, TimeUnit.SECONDS);
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (resultFileWriter != null) {
					resultFileWriter.close();
				}

			} catch (IOException e) {
				e.printStackTrace();
				return;
			}
		}

	}

	// Ezt használjuk a másik két feladatban is.
	public static void handlePrintActions(final ToplistSetInterface toplist, final int runningMode,
			String previousToplistWithoutDelay, BufferedWriter resultFileWriter, final long currentTime, long counter,
			final long startingTime, final long benchmarkFrequency, long previousTime, Runtime runtime)
			throws IOException {
		String toplistStringWithoutDelay = toplist.toStringWithoutDelay();
		if (!toplistStringWithoutDelay.equals(previousToplistWithoutDelay)) {
			if (runningMode == TIME_MEASURING_MODE || runningMode == MEMORY_MEASURING_MODE) {
				toplist.refreshDelayTimes();
				System.out.println(toplist);
				previousToplistWithoutDelay = toplistStringWithoutDelay;
			} else {
				resultFileWriter.write(toplistStringWithoutDelay);
				resultFileWriter.newLine();
				resultFileWriter.write("current time:" + new Date(currentTime) + " processed:" + counter);
				resultFileWriter.newLine();
				previousToplistWithoutDelay = toplistStringWithoutDelay;
			}

		}
		// Egységidõnként megmérjük, hogy mennyit dolgoztunk fel, és
		// mennyi
		// idõ alatt, vagy megmérjük az aktuális lefoglalt memóriát egy
		// kis várakozás után
		if ((runningMode == TIME_MEASURING_MODE || runningMode == MEMORY_MEASURING_MODE)
				&& (currentTime - startingTime) % benchmarkFrequency == 0) {

			// CSV formátum idõmérés esetén: aktuális idõpont;eddig
			// feldolgozott
			// sorok száma; elõzõ kiírás óta eltelt idõ;
			// aktuális átlagos késleltetés; aktuális legkisebb
			// késleltetés;aktuális legnagyobb késleltetés
			if (runningMode == TIME_MEASURING_MODE) {
				resultFileWriter.write(DataFileParser.SIMPLE_DATE_FORMAT.format(new Date(currentTime)) + ";" + counter
						+ ";" + ((System.currentTimeMillis() - previousTime) / 1000 + ";" + toplist.getAverageDelay()
								+ ";" + toplist.getMinDelay() + ";" + toplist.getMaxDelay()));
			} else {
				// CSV formátum memóriamérés esetén: aktuális
				// idõpont;eddig feldolgozott sorok száma;
				// aktuális felhasznált memória
				System.gc();
				try {
					Thread.sleep(SLEEP_TIME_IN_MS);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				resultFileWriter.write(DataFileParser.SIMPLE_DATE_FORMAT.format(new Date(currentTime)) + ";" + counter
						+ ";" + (runtime.totalMemory() - runtime.freeMemory()));
			}

			resultFileWriter.newLine();
		}
	}
}
