package hu.bme.mit.utility;

import java.io.File;
import java.io.FileNotFoundException;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

import hu.bme.mit.entities.TaxiLog;
import hu.bme.mit.positioning.Cell;
import hu.bme.mit.positioning.CellHelper;
import hu.bme.mit.positioning.Coordinate;

public class DataFileParser implements AutoCloseable{

	private static final Logger LOGGER = Logger.getLogger(DataFileParser.class.getName());

	private static long START_TIME_IN_MILLISECONDS = 0;
	public static SimpleDateFormat SIMPLE_DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	// private String fileName;
	private String delimiter;
	private int columncount;
	private CellHelper cellHelper;
	private Scanner scanner;
	private String nextMinFirstLine;
	private String fileName;
	
	private static long CURRENT_TIME=0;
	
	public static long getCURRENT_TIME() {
		return CURRENT_TIME;
	}

	//debug
	private long parsed = 0;

	public DataFileParser(String fileName, String delimiter, int columncount, CellHelper cellHelper) throws FileNotFoundException {
		// this.fileName = fileName;
		this.delimiter = delimiter;
		this.columncount = columncount;
		this.cellHelper = cellHelper;
		this.fileName = fileName;
		this.scanner = new Scanner(new File(fileName));
		this.scanner.useDelimiter(delimiter);
		LOGGER.setLevel(Level.SEVERE);
	}

	public List<TaxiLog> parseNextLinesFromCSVGroupedByDropoffDate() {

		String nextLine = nextMinFirstLine;
		if (scanner == null || !scanner.hasNextLine()) {
			closeScanner();
			return null;
		}

		if(nextLine == null){
			nextLine = scanner.nextLine();
		}
		List<TaxiLog> resultList = new ArrayList<TaxiLog>(Arrays.asList(parseNextLine(nextLine.split(delimiter))));
		Date currentDate = resultList.get(0).getDropoff_datetime();

		while (scanner.hasNextLine()) {
			nextLine = scanner.nextLine();
			TaxiLog taxilog = parseNextLine(nextLine.split(delimiter));
			if (taxilog != null && currentDate.before(taxilog.getDropoff_datetime())) {
				nextMinFirstLine = nextLine;
				break;
			}
			if(taxilog != null){
				resultList.add(taxilog);
			}
			
			
		}

		LOGGER.info("LastPArsedLine:"+parsed);
		
		if(!resultList.isEmpty()) {
			CURRENT_TIME = resultList.get(0).getDropoff_datetime().getTime();
		}
		return resultList;

	}

	public static List<TaxiLog> parseCSVIntoTaxiLogList(String fileName, String delimiter, int columncount,
			int rowcount, CellHelper chelper, int startindex) throws FileNotFoundException {

		START_TIME_IN_MILLISECONDS = 0;

		Scanner scanner = new Scanner(new File(fileName));
		scanner.useDelimiter(delimiter);

		for (int i = 0; i < startindex; i++) {
			scanner.nextLine();
		}
		List<TaxiLog> resultList = new ArrayList<TaxiLog>();

		Calendar calendar;
		Date date = null;

		long counter = 0;

		while (scanner.hasNextLine() && (rowcount < 0 ? true : counter <= rowcount)) {
			String[] line = scanner.nextLine().split(delimiter);

			if (line.length != columncount) {
				LOGGER.warning("Too " + (line.length > columncount ? "many" : "few") + " columns! Line: " + counter);
			} else {
				TaxiLog taxiLog = new TaxiLog();
				taxiLog.setMedallion(line[0]);
				taxiLog.setHack_license(line[1]);

				try {
					date = SIMPLE_DATE_FORMAT.parse(line[2]);
					calendar = Calendar.getInstance();
					calendar.setTime(date);
					taxiLog.setPickup_datetime(calendar.getTime());
				} catch (ParseException e) {
					LOGGER.warning("Wrong pickup dateformat. Line: " + counter);
				}

				try {
					date = SIMPLE_DATE_FORMAT.parse(line[3]);
					calendar = Calendar.getInstance();
					calendar.setTime(date);
					taxiLog.setDropoff_datetime(calendar.getTime());

					if (START_TIME_IN_MILLISECONDS == 0) {
						START_TIME_IN_MILLISECONDS = calendar.getTimeInMillis();
					}

				} catch (ParseException e) {
					LOGGER.warning("Wrong dropoff dateformat. Line: " + counter);
				}

				taxiLog.setTrip_time_in_secs(Long.valueOf(line[4]));
				taxiLog.setTrip_distance(Double.valueOf(line[5]));
				taxiLog.setPickup_coordinate(new Coordinate(new BigDecimal(line[6]), new BigDecimal(line[7])));
				taxiLog.setDropoff_coordinate(new Coordinate(new BigDecimal(line[8]), new BigDecimal(line[9])));
				taxiLog.setPayment_type(line[10]);
				taxiLog.setFare_amount(new BigDecimal(line[11]));
				taxiLog.setSurcharge(new BigDecimal(line[12]));
				taxiLog.setMta_tax(new BigDecimal(line[13]));
				taxiLog.setTip_amount(new BigDecimal(line[14]));
				taxiLog.setTolls_amount(new BigDecimal(line[15]));
				taxiLog.setTotal_amount(new BigDecimal(line[16]));

				Cell pickupCell = chelper.getCell(taxiLog.getPickup_coordinate());
				Cell dropoffCell = chelper.getCell(taxiLog.getDropoff_coordinate());

				taxiLog.setPickup_cell(pickupCell);
				taxiLog.setDropoff_cell(dropoffCell);

				resultList.add(taxiLog);
				counter++;

			}

		}

		scanner.close();

		LOGGER.info(counter + " records parsed.");

		return resultList;
	}

	public static List<TaxiLog> parseCSVIntoTaxiLogList(String fileName, String delimiter, int columncount,
			CellHelper chelper) throws FileNotFoundException {

		return parseCSVIntoTaxiLogList(fileName, delimiter, columncount, -1, chelper);

	}

	public static List<TaxiLog> parseCSVIntoTaxiLogList(String fileName, String delimiter, int columncount,
			int rowcount, CellHelper chelper) throws FileNotFoundException {
		return parseCSVIntoTaxiLogList(fileName, delimiter, columncount, rowcount, chelper, 0);
	}

	
	public void reopenScanner(String fileName) throws FileNotFoundException {
		closeScanner();

		scanner = new Scanner(new File(fileName));
	}

	private TaxiLog parseNextLine(String[] line) {
		parsed++;
		Calendar calendar;
		Date date = null;

		if (line.length != columncount) {
			LOGGER.warning("Too " + (line.length > columncount ? "many" : "few") + " columns! HackLicense:" + line[1]
					+ " Dropoff datetime:" + line[3]);
			return null;
		} else if(hasBlankMember(line)){
			LOGGER.info("Insufficient record skipped. Dropoff datetime:" + line[3] + "\t" + "HackLicense:" + line[1]);
			return null;
		} else{
			TaxiLog taxiLog = new TaxiLog();
			taxiLog.setMedallion(line[0]);
			taxiLog.setHack_license(line[1]);

			try {
				date = SIMPLE_DATE_FORMAT.parse(line[2]);
				calendar = Calendar.getInstance();
				calendar.setTime(date);
				taxiLog.setPickup_datetime(calendar.getTime());
			} catch (ParseException e) {
				LOGGER.warning("Wrong pickup dateformat. HackLicense:" + line[1] + " Dropoff datetime:" + line[3]);
				return null;
			}

			try {
				date = SIMPLE_DATE_FORMAT.parse(line[3]);
				calendar = Calendar.getInstance();
				calendar.setTime(date);
				taxiLog.setDropoff_datetime(calendar.getTime());

				if (START_TIME_IN_MILLISECONDS == 0) {
					START_TIME_IN_MILLISECONDS = calendar.getTimeInMillis();
				}

			} catch (ParseException e) {
				LOGGER.warning("Wrong dropoff dateformat. HackLicense:" + line[1] + " Dropoff datetime:" + line[3]);
				return null;
			}

			taxiLog.setTrip_time_in_secs(Long.valueOf(line[4]));
			taxiLog.setTrip_distance(Double.valueOf(line[5]));
			taxiLog.setPickup_coordinate(new Coordinate(new BigDecimal(line[6]), new BigDecimal(line[7])));
			taxiLog.setDropoff_coordinate(new Coordinate(new BigDecimal(line[8]), new BigDecimal(line[9])));
			taxiLog.setPayment_type(line[10]);
			taxiLog.setFare_amount(new BigDecimal(line[11]));
			taxiLog.setSurcharge(new BigDecimal(line[12]));
			taxiLog.setMta_tax(new BigDecimal(line[13]));
			taxiLog.setTip_amount(new BigDecimal(line[14]));
			taxiLog.setTolls_amount(new BigDecimal(line[15]));
			taxiLog.setTotal_amount(new BigDecimal(line[16]));

			Cell pickupCell = cellHelper.getCell(taxiLog.getPickup_coordinate());
			Cell dropoffCell = cellHelper.getCell(taxiLog.getDropoff_coordinate());

			taxiLog.setPickup_cell(pickupCell);
			taxiLog.setDropoff_cell(dropoffCell);

			return taxiLog;

		}
	}

	public boolean hasNextLine(){
		if(scanner == null){
			return false;
		}
		return scanner.hasNextLine();
	}
	public void closeScanner() {
		if (scanner != null) {
			scanner.close();
			nextMinFirstLine = null;
			parsed = 0;
		}
	}
	
	public void setLoggerLevel(Level level) {
		LOGGER.setLevel(level);
	}

	@Override
	public void close() {
		if(scanner != null) {
			scanner.close();
		}
		
	}

	@Override
	public Object clone() throws CloneNotSupportedException {
		try {
			return new DataFileParser(fileName, delimiter, columncount, cellHelper);
		} catch (FileNotFoundException e) {
			LOGGER.log(Level.SEVERE, fileName + " not found.", e);
			return null;
		}
	}
	
	private static boolean hasBlankMember(String[] stringArray){
		for(String s:stringArray){
			if(s == null || s.trim().isEmpty()){
				return true;
			}
		}
		
		return false;
	}
	

}
