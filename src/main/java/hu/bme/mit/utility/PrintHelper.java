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
			// Egys�gid�nk�nt megm�rj�k, hogy mennyit dolgoztunk fel, �s
			// mennyi
			// id� alatt, vagy megm�rj�k az aktu�lis lefoglalt mem�ri�t egy
			// kis v�rakoz�s ut�n
			if ((runningMode == ExecutionSetup.TIME_MEASURING_MODE || runningMode == ExecutionSetup.MEMORY_MEASURING_MODE)
					&& (currentTime - startingTime) % benchmarkFrequency == 0) {

				// CSV form�tum id�m�r�s eset�n: aktu�lis id�pont;eddig
				// feldolgozott
				// sorok sz�ma; el�z� ki�r�s �ta eltelt id�;
				// aktu�lis �tlagos k�sleltet�s; aktu�lis legkisebb
				// k�sleltet�s;aktu�lis legnagyobb k�sleltet�s
				if (runningMode == ExecutionSetup.TIME_MEASURING_MODE) {
					resultFileWriter.write(DataFileParser.SIMPLE_DATE_FORMAT.format(new Date(currentTime)) + ";" + counter
							+ ";" + (System.currentTimeMillis() - previousTime) + ";" + toplist.getAverageDelay() + ";"
							+ toplist.getMinDelay() + ";" + toplist.getMaxDelay());
					previousTime = System.currentTimeMillis();
				} else {
					// CSV form�tum mem�riam�r�s eset�n: aktu�lis
					// id�pont;eddig feldolgozott sorok sz�ma;
					// aktu�lis felhaszn�lt mem�ria
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
