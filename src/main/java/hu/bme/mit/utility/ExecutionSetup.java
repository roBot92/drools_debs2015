package hu.bme.mit.utility;

import java.math.BigDecimal;

public final class ExecutionSetup {
	public static final String DATA_FILE_URL = "F:\\\\sorted_data.csv";
	public static final String DELIMITER = ",";
	public static final int columncount = 17;
	public static final BigDecimal FIRST_CELL_X = BigDecimal.valueOf(-74.913585);
	public static final BigDecimal FIRST_CELL_Y = BigDecimal.valueOf(41.474937);
	public static final BigDecimal SHIFT_Y = BigDecimal.valueOf(0.004491556);
	public static final BigDecimal SHIFT_X = BigDecimal.valueOf(0.005986);
	public static final long TEST_INTERVAL_IN_IN_MS = 31*24 * 60 * 60 * 1000l;
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
}
