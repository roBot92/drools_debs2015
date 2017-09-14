package onlab.utility;

import static org.junit.Assert.*;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import onlab.event.TaxiLog;
import onlab.positioning.CellHelper;

public class DataFileParserTest {

	public static File TEST_CSV = new File("testCSV.csv");
	public static SimpleDateFormat DATEFORMAT = DataFileParser.SIMPLE_DATE_FORMAT;
	public static String MEDALLION = "medallion";
	public static String HACK_LICENSE = "hack_license";
	private static Date PICKUP_DATETIME = new Date();
	private static Date DROPOFF_DATETIME = (Date) PICKUP_DATETIME.clone();
	private static String TRIP_TIME_IN_SECS = "0";
	private static String TRIP_DISTANCE = "0";
	private static String PICKUP_X_COORDINATE = "0";
	private static String PICKUP_Y_COORDINATE = "1";
	private static String DROPOFF_X_COORDINATE = "2";
	private static String DROPOFF_Y_COORDINATE = "3";
	private static String PAYMENT_TYPE = "CSH";
	private static String FARE_AMOUNT = "1";
	private static String SURCHARGE = "2";
	private static String MTA_TAX = "3";
	private static String TIP_AMOUNT = "4";
	private static String TOLLS_AMOUNT = "5";
	private static String TOTAL_AMOUNT = "6";
	private static String SEPARATOR = ";";
	private static int COLUMNCOUNT = 17;
	public static DataFileParser parser;
	private static CellHelper cellHelper;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {

		try (BufferedWriter writer = new BufferedWriter(new FileWriter(TEST_CSV))) {
			Calendar dropoff = Calendar.getInstance();
			dropoff.setTime(DROPOFF_DATETIME);
			for (int i = 0; i < 4; i++) {

				for (int j = 0; j <= i; j++) {

					StringBuilder sb = new StringBuilder(MEDALLION + SEPARATOR);
					sb.append(HACK_LICENSE + SEPARATOR);
					sb.append(DATEFORMAT.format(PICKUP_DATETIME) + SEPARATOR);
					sb.append(DATEFORMAT.format(dropoff.getTime()) + SEPARATOR);
					sb.append(TRIP_TIME_IN_SECS + SEPARATOR);
					sb.append(TRIP_DISTANCE + SEPARATOR);
					sb.append(PICKUP_X_COORDINATE + SEPARATOR);
					sb.append(PICKUP_Y_COORDINATE + SEPARATOR);
					sb.append(DROPOFF_X_COORDINATE + SEPARATOR);
					sb.append(DROPOFF_Y_COORDINATE + SEPARATOR);
					sb.append(PAYMENT_TYPE + SEPARATOR);
					sb.append(FARE_AMOUNT + SEPARATOR);
					sb.append(SURCHARGE + SEPARATOR);
					sb.append(MTA_TAX + SEPARATOR);
					sb.append(TIP_AMOUNT + SEPARATOR);
					sb.append(TOLLS_AMOUNT + SEPARATOR);
					sb.append(TOTAL_AMOUNT);

					writer.write(sb.toString());
					writer.newLine();

				}

				dropoff.add(Calendar.MINUTE, 1);

			}
		}

		cellHelper = new CellHelper(BigDecimal.valueOf(-5), BigDecimal.valueOf(5), BigDecimal.ONE,
				BigDecimal.valueOf(-1), 10);
		parser = new DataFileParser(TEST_CSV.getName(), SEPARATOR, COLUMNCOUNT, cellHelper);

		parser.setLoggerLevel(Level.SEVERE);
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {

		TEST_CSV.delete();
	}

	@Before
	public void setUp() throws FileNotFoundException {
		parser.reopenScanner(TEST_CSV.getName());
	}

	@After
	public void tearDown() {
		parser.closeScanner();
	}

	@Test
	public void testParseNextLinesFromCSVGroupedByDropoffDateLineCount() {

		for (int i = 0; i < 4; i++) {
			List<TaxiLog> res = parser.parseNextLinesFromCSVGroupedByDropoffDate();
			assertTrue("Result size: " + i, res.size() == i + 1);
		}

		assertEquals(parser.parseNextLinesFromCSVGroupedByDropoffDate(), null);

		// Többszöri meghívásra sem fut hibára, nem parszol többet.
		int random = (int) (Math.random() % 5);

		for (int i = 0; i < random; i++) {
			assertEquals(parser.parseNextLinesFromCSVGroupedByDropoffDate(), null);
		}
	}

	@Test
	public void testParseCSVIntoTaxiLogListStringStringIntIntCellHelperInt() throws FileNotFoundException {
		List<TaxiLog> result = DataFileParser.parseCSVIntoTaxiLogList(TEST_CSV.getName(), SEPARATOR, COLUMNCOUNT, cellHelper);
		
		
		assertTrue("Result size: 10", result.size() == 10);
		
		
		
		
		
	}
}
