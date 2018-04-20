package hu.bme.mit.utility;

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.Date;

import hu.bme.mit.toplist.ToplistSetInterface;

public class PrintHelper {
	private static long previousTime = 0;
	
	
		public static String handlePrintActions(final ToplistSetInterface toplist, final int runningMode,
				String previousToplistWithoutDelay, BufferedWriter resultFileWriter, final long currentTime, long counter,
				final long startingTime, final long benchmarkFrequency, Runtime runtime) throws IOException {

			if (previousTime == 0) {
				previousTime = System.currentTimeMillis();
				if (runningMode == ExecutionSetup.TIME_MEASURING_MODE) {
					resultFileWriter.write("Starting time: " + DataFileParser.SIMPLE_DATE_FORMAT.format(new Date(System.currentTimeMillis())));
					resultFileWriter.newLine();
				}

			}
			String toplistStringWithoutDelay = toplist.toStringWithoutDelay();
			if (!toplistStringWithoutDelay.equals(previousToplistWithoutDelay)) {
				if (runningMode == ExecutionSetup.TIME_MEASURING_MODE || runningMode == ExecutionSetup.MEMORY_MEASURING_MODE) {
					toplist.refreshDelayTimes();
					System.out.println(toplist);
					previousToplistWithoutDelay = toplistStringWithoutDelay;
				} else {
					resultFileWriter.write(toplistStringWithoutDelay);
					resultFileWriter.newLine();
					resultFileWriter.write("current time:" + DataFileParser.SIMPLE_DATE_FORMAT.format(new Date(currentTime))
							+ " processed:" + counter);
					resultFileWriter.newLine();
					previousToplistWithoutDelay = toplistStringWithoutDelay;
					if((currentTime - startingTime) % (benchmarkFrequency * 10) == 0){
						resultFileWriter.flush();
					}
				}

			}
			// Egységidõnként megmérjük, hogy mennyit dolgoztunk fel, és
			// mennyi
			// idõ alatt, vagy megmérjük az aktuális lefoglalt memóriát egy
			// kis várakozás után
			if ((runningMode == ExecutionSetup.TIME_MEASURING_MODE || runningMode == ExecutionSetup.MEMORY_MEASURING_MODE)
					&& (currentTime - startingTime) % benchmarkFrequency == 0) {

				// CSV formátum idõmérés esetén: aktuális idõpont;eddig
				// feldolgozott
				// sorok száma; elõzõ kiírás óta eltelt idõ;
				// aktuális átlagos késleltetés; aktuális legkisebb
				// késleltetés;aktuális legnagyobb késleltetés
				if (runningMode == ExecutionSetup.TIME_MEASURING_MODE) {
					resultFileWriter.write(DataFileParser.SIMPLE_DATE_FORMAT.format(new Date(currentTime)) + ";" + counter
							+ ";" + (System.currentTimeMillis() - previousTime) + ";" + toplist.getAverageDelay() + ";"
							+ toplist.getMinDelay() + ";" + toplist.getMaxDelay());
					previousTime = System.currentTimeMillis();
				} else {
					// CSV formátum memóriamérés esetén: aktuális
					// idõpont;eddig feldolgozott sorok száma;
					// aktuális felhasznált memória
					resultFileWriter.flush();
					System.gc();
					try {
						Thread.sleep(ExecutionSetup.SLEEP_TIME_IN_MS);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					resultFileWriter.write(DataFileParser.SIMPLE_DATE_FORMAT.format(new Date(currentTime)) + ";" + counter
							+ ";" + (runtime.totalMemory() - runtime.freeMemory()));
				}

				resultFileWriter.newLine();
			}
			return previousToplistWithoutDelay;
		}

		public static void restartCurrentTime() {
			previousTime = 0;
		}
}
