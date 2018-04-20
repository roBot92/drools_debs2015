package hu.bme.mit.drools;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.math.BigDecimal;
import java.text.ParseException;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.drools.core.time.SessionPseudoClock;
import org.kie.api.KieServices;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.KieSessionConfiguration;
import org.kie.api.runtime.conf.ClockTypeOption;

import hu.bme.mit.entities.TaxiLog;
import hu.bme.mit.entities.Tick;
import hu.bme.mit.positioning.*;
import hu.bme.mit.toplist.FrequentRoutesToplistSet;
import hu.bme.mit.toplist.ProfitableAreaToplistSet;
import hu.bme.mit.toplist.ToplistSetInterface;
import hu.bme.mit.utility.DataFileParser;
import hu.bme.mit.utility.ExecutionSetup;
import hu.bme.mit.utility.PrintHelper;

public class DroolsMain {

	

	public static void main(String[] args) throws FileNotFoundException, ParseException {

		 runTask1();
		//runTask2();
	}

	public static void runTask1() {
		//runTask(new FrequentRoutesToplistSet(), "mostFrequentRoutes", task1MemoryMeasuringResultFileName,
		//		MEMORY_MEASURING_MODE, 1);
		
		runTask(new FrequentRoutesToplistSet(), "mostFrequentRoutes", ExecutionSetup.task1TimeMeasuringResultFileName,
				ExecutionSetup.TIME_MEASURING_MODE, 1);
		
		runTask(new FrequentRoutesToplistSet(), "mostFrequentRoutes", ExecutionSetup.task1ResultToCompareFileName,
				ExecutionSetup.OUTPUT_COOMPARING_MODE, 1);

		
	}

	public static void runTask2() {

		runTask(new ProfitableAreaToplistSet(), "mostProfitableAreas", ExecutionSetup.task2ResultToCompareFileName,
				ExecutionSetup.OUTPUT_COOMPARING_MODE, 2);
		runTask(new ProfitableAreaToplistSet(), "mostProfitableAreas", ExecutionSetup.task2TimeMeasuringResultFileName,
				ExecutionSetup.TIME_MEASURING_MODE, 2);
		runTask(new ProfitableAreaToplistSet(), "mostProfitableAreas", ExecutionSetup.task2MemoryMeasuringResultFileName,
				ExecutionSetup.MEMORY_MEASURING_MODE, 2);
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
		CellHelper chelper = new CellHelper(ExecutionSetup.FIRST_CELL_X, ExecutionSetup.FIRST_CELL_Y, ExecutionSetup.SHIFT_X.divide(BigDecimal.valueOf(divisor)),
				ExecutionSetup.SHIFT_Y.divide(BigDecimal.valueOf(divisor)), 300 * divisor);
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
		
		
		
		try (DataFileParser dataFileParser = new DataFileParser(ExecutionSetup.DATA_FILE_URL, ExecutionSetup.DELIMITER, ExecutionSetup.columncount, chelper)) {

			KieSession kSession = initializeSession();

			// Hozzáadjuk globális változóként a toplistát
			kSession.setGlobal(globalToplistVariableName, toplist);

			SessionPseudoClock clock = kSession.getSessionClock();

			taxiLogs = dataFileParser.parseNextLinesFromCSVGroupedByDropoffDate();
			long currentTime = DataFileParser.getCURRENT_TIME();
			long startingTime = DataFileParser.getCURRENT_TIME();

			clock.advanceTime(startingTime, TimeUnit.MILLISECONDS);

			long counter = 0;
			PrintHelper.restartCurrentTime();
			String previousToplistWithoutDelay = null;

			// A megadott ideig dolgoz fel a teszt
			System.gc();
			try {
				Thread.sleep(10000);
			} catch (InterruptedException e1) {
				e1.printStackTrace();
				return;
			}
			while (currentTime - startingTime <= ExecutionSetup.TEST_INTERVAL_IN_IN_MS) {
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
				previousToplistWithoutDelay = PrintHelper.handlePrintActions(toplist, runningMode, previousToplistWithoutDelay,
						resultFileWriter, currentTime, counter, startingTime, ExecutionSetup.BENCHMARK_FREQUENCY_IN_MS, runtime);


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

}
