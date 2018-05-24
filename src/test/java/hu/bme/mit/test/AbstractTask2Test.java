package hu.bme.mit.test;

import static org.junit.Assert.assertTrue;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

import org.drools.core.base.ValueType.BigDecimalValueType;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import edu.emory.mathcs.backport.java.util.Collections;
import hu.bme.mit.entities.AreaWithProfit;
import hu.bme.mit.entities.TaxiLog;
import hu.bme.mit.positioning.Cell;
import hu.bme.mit.toplist.ProfitableAreaToplistSet;

public abstract class AbstractTask2Test {
	protected ProfitableAreaToplistSet toplist;
	protected Calendar calendar;

	protected static List<Cell> cells;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		cells = Arrays.asList(new Cell(1, 1), new Cell(1, 2), new Cell(1, 3), new Cell(2, 1), new Cell(2, 2),
				new Cell(2, 3), new Cell(3, 1), new Cell(3, 2), new Cell(3, 3), new Cell(4, 1), new Cell(4, 2));

	}

	protected static TaxiLog setUpTaxilog(Cell startCell, Cell endCell) {
		TaxiLog tlog = new TaxiLog();
		Calendar zeroCalendar = Calendar.getInstance();
		tlog.setPickup_cell(startCell);
		tlog.setDropoff_cell(endCell);

		zeroCalendar.setTimeInMillis(0);
		tlog.setPickup_datetime(zeroCalendar.getTime());
		tlog.setDropoff_datetime(zeroCalendar.getTime());
		return tlog;
	}

	@Before
	public void setUp() throws Exception {
		toplist = new ProfitableAreaToplistSet();
		calendar = getZeroTimeCalendar();
	}

	protected static TaxiLog setUpTaxilog(Cell startCell, Cell endCell, BigDecimal fare, BigDecimal tip,
			String hack_License) {
		TaxiLog tlog = new TaxiLog();
		Calendar zeroCalendar = Calendar.getInstance();
		tlog.setPickup_cell(startCell);
		tlog.setDropoff_cell(endCell);

		zeroCalendar.setTimeInMillis(0);
		tlog.setDropoff_datetime(zeroCalendar.getTime());
		tlog.setPickup_datetime(zeroCalendar.getTime());

		tlog.setFare_amount(fare);
		tlog.setTip_amount(tip);

		tlog.setHack_license(hack_License);
		return tlog;
	}

	protected Calendar getZeroTimeCalendar() {
		Calendar cal = Calendar.getInstance();
		cal.setTimeInMillis(0);
		return cal;
	}

	protected abstract void insertTaxiLogs(List<TaxiLog> taxiLogs);

	protected abstract void rollPseudoClock(long time);

	protected abstract void fireRules();

	@Test
	public void testInsertForOneArea() {
		TaxiLog tlog1 = setUpTaxilog(cells.get(0), cells.get(1), BigDecimal.ONE, BigDecimal.ONE, "1");
		TaxiLog tlog2 = setUpTaxilog(cells.get(0), cells.get(1), BigDecimal.ONE, BigDecimal.TEN, "2");

		AreaWithProfit area = new AreaWithProfit(cells.get(0), BigDecimal.valueOf(2), calendar.getTime());

		rollPseudoClock(0);
		insertTaxiLogs(Arrays.asList(tlog1));
		fireRules();

		assertTrue("check1", area.valueEquals(toplist.get(0)));

		rollPseudoClock(60 * 1000);
		tlog2.setDropoff_datetime(calendar.getTime());
		area.setLastInserted(calendar.getTime());
		area.setMedianProfitIndex(BigDecimal.valueOf(6.5));

		insertTaxiLogs(Arrays.asList(tlog2));
		fireRules();

		assertTrue("check2", area.valueEquals(toplist.get(0)) && toplist.size() == 1);

	}

	@Test
	public void testInsertForTwoArea() {
		List<TaxiLog> tlogs = Arrays.asList(
				setUpTaxilog(cells.get(0), cells.get(1), BigDecimal.ONE, BigDecimal.ONE, "1"),
				setUpTaxilog(cells.get(0), cells.get(1), BigDecimal.ONE, BigDecimal.TEN, "2"),
				setUpTaxilog(cells.get(0), cells.get(1), BigDecimal.TEN, BigDecimal.TEN, "3"),
				setUpTaxilog(cells.get(1), cells.get(0), BigDecimal.ONE, BigDecimal.ONE, "4"),
				setUpTaxilog(cells.get(1), cells.get(0), BigDecimal.ONE, BigDecimal.ONE, "5"),
				setUpTaxilog(cells.get(1), cells.get(0), BigDecimal.TEN, BigDecimal.TEN, "6"));
		BigDecimal emptyTaxes = BigDecimal.valueOf(3);
		BigDecimal medianProfit1 = BigDecimal.valueOf(11).divide(emptyTaxes, 2, BigDecimal.ROUND_HALF_UP);
		BigDecimal medianProfit2 = BigDecimal.valueOf(2).divide(emptyTaxes, 2, BigDecimal.ROUND_HALF_UP);
		AreaWithProfit area1 = new AreaWithProfit(cells.get(0), medianProfit1, calendar.getTime());
		AreaWithProfit area2 = new AreaWithProfit(cells.get(1), medianProfit2, calendar.getTime());

		rollPseudoClock(0);
		insertTaxiLogs(tlogs);
		fireRules();

		assertTrue(toplist.size() == 2 && area1.valueEquals(toplist.get(0)) && area2.valueEquals(toplist.get(1)));

	}
	
	@Test
	public void testLessEmptyTaxiByNewDrive() {
		List<TaxiLog> tlogs = Arrays.asList(
				setUpTaxilog(cells.get(0), cells.get(1), BigDecimal.ONE, BigDecimal.ONE, "1"),
				setUpTaxilog(cells.get(0), cells.get(1), BigDecimal.ONE, BigDecimal.TEN, "2"),
				setUpTaxilog(cells.get(1), cells.get(0), BigDecimal.ONE, BigDecimal.ONE, "3"),
				setUpTaxilog(cells.get(1), cells.get(0), BigDecimal.ONE, BigDecimal.ONE, "4"),
				setUpTaxilog(cells.get(0), cells.get(1), BigDecimal.TEN, BigDecimal.TEN, "3"));
		AreaWithProfit area1 = new AreaWithProfit(cells.get(0), BigDecimal.valueOf(3.25),calendar.getTime());
		AreaWithProfit area2 = new AreaWithProfit(cells.get(1), BigDecimal.valueOf(1),calendar.getTime());

		insertTaxiLogs(tlogs.subList(0, 4));
		rollPseudoClock(0);
		fireRules();
		assertTrue("firstCheck",
				toplist.size() == 2 && area1.valueEquals(toplist.get(0)) && area2.valueEquals(toplist.get(1)));

		
		rollPseudoClock(10*60*1000);
		tlogs.get(4).setDropoff_datetime(calendar.getTime());
		insertTaxiLogs(Arrays.asList(tlogs.get(4)));
		fireRules();

		area1.setLastInserted(calendar.getTime());
		area1.setMedianProfitIndex(BigDecimal.valueOf(11));

		area2.setLastInserted(calendar.getTime());
		area2.setMedianProfit(BigDecimal.valueOf(2d / 3d).setScale(2, RoundingMode.HALF_UP));
		assertTrue("secondCheck",
				toplist.size() == 2 && area1.valueEquals(toplist.get(0)) && area2.valueEquals(toplist.get(1)));
	}
	@SuppressWarnings("unchecked")
	@Test
	public void testMedianChangingAfter15Minutes() {
		TaxiLog tlog1 = setUpTaxilog(cells.get(0), cells.get(1), BigDecimal.ONE, BigDecimal.ONE, "1");
		tlog1.setDropoff_datetime(calendar.getTime());

		rollPseudoClock(0);
		insertTaxiLogs(Arrays.asList(tlog1));
		fireRules();

		AreaWithProfit area = new AreaWithProfit(cells.get(0), BigDecimal.valueOf(2), calendar.getTime());
		assertTrue("check1", toplist.size() == 1 && toplist.get(0).valueEquals(area));

		
		rollPseudoClock(60*1000);
		TaxiLog tlog2 = setUpTaxilog(cells.get(0), cells.get(1), BigDecimal.ONE, BigDecimal.TEN, "2");
		tlog2.setDropoff_datetime(calendar.getTime());
		insertTaxiLogs(Arrays.asList(tlog2));
		fireRules();
		area.setMedianProfitIndex(BigDecimal.valueOf(6.5));
		area.setLastInserted(calendar.getTime());

		assertTrue("check2", area.valueEquals(toplist.get(0)) && toplist.size() == 1);

		rollPseudoClock(14*60*1000);
		insertTaxiLogs(Collections.emptyList());
		fireRules();
		assertTrue("check3", area.valueEquals(toplist.get(0)) && toplist.size() == 1);

		rollPseudoClock(1000);
		insertTaxiLogs(Collections.emptyList());
		fireRules();
		area.setMedianProfitIndex(BigDecimal.valueOf(11));
		assertTrue("check4", area.valueEquals(toplist.get(0)) && toplist.size() == 1);

		rollPseudoClock(59*1000);
		insertTaxiLogs(Collections.emptyList());
		fireRules();
		assertTrue("check5", area.valueEquals(toplist.get(0)) && toplist.size() == 1);

		rollPseudoClock(1000);
		insertTaxiLogs(Collections.emptyList());
		fireRules();
		assertTrue("check6", toplist.isEmpty());
	}
	@SuppressWarnings("unchecked")
	@Test
	public void testTaxiCountChangingAfter30Minutes() {
		TaxiLog oldTlog1 = setUpTaxilog(cells.get(1), cells.get(0), BigDecimal.ONE, BigDecimal.ONE, "1");		
		rollPseudoClock(0);
		insertTaxiLogs(Arrays.asList(oldTlog1));
		fireRules();
		assertTrue("1", toplist.size() == 1 && toplist.get(0).getMedianProfitIndex().compareTo(BigDecimal.valueOf(2)) == 0);
		
		//+1 minutes
		rollPseudoClock(60*1000);
		TaxiLog oldTlog2 = setUpTaxilog(cells.get(1), cells.get(0), BigDecimal.ONE, BigDecimal.ONE, "2");
		TaxiLog oldTlog3 = setUpTaxilog(cells.get(1), cells.get(0), BigDecimal.ONE, BigDecimal.ONE, "3");
		oldTlog2.setDropoff_datetime(calendar.getTime());
		oldTlog3.setDropoff_datetime(calendar.getTime());
		
		insertTaxiLogs(Arrays.asList(oldTlog2,oldTlog3));
		fireRules();
		assertTrue("2", toplist.size() == 1 && toplist.get(0).getMedianProfitIndex().compareTo(BigDecimal.valueOf(2)) == 0);
		
		//+20 min
		rollPseudoClock(20*60*1000);
		insertTaxiLogs(Collections.emptyList());
		fireRules();
		
		assertTrue("3", toplist.size() == 0);
		
		TaxiLog newTlog = setUpTaxilog(cells.get(0), cells.get(1), BigDecimal.TEN, BigDecimal.valueOf(2), "4");
		newTlog.setDropoff_datetime(calendar.getTime());
		rollPseudoClock(0);
		insertTaxiLogs(Arrays.asList(newTlog));
		fireRules();
		
		AreaWithProfit area = new AreaWithProfit(cells.get(0), BigDecimal.valueOf(4), newTlog.getDropoff_datetime());
		//A három korábban beillesztett miatt a medián profitindexe 12/3 lesz.
		assertTrue("4",toplist.size() == 1 && toplist.get(0).valueEquals(area));
		
		//+9 min
		rollPseudoClock(9*60*1000);
		insertTaxiLogs(Collections.emptyList());
		fireRules();
		//A helyzet itt még változatlan
		assertTrue("4",toplist.size() == 1 && toplist.get(0).valueEquals(area));
		
		//+1 sec itt már kiesett az elsõ fuvar a területrõl.
		rollPseudoClock(1000);
		insertTaxiLogs(Collections.emptyList());
		fireRules();
		area.setMedianProfitIndex(BigDecimal.valueOf(6));
		assertTrue("5",toplist.size() == 1 && toplist.get(0).valueEquals(area));
		
		//+1 min itt már kiesett a másik kettõ is.
		rollPseudoClock(60*1000);
		insertTaxiLogs(Collections.emptyList());
		fireRules();
		area.setMedianProfitIndex(BigDecimal.valueOf(12));
		assertTrue("6",toplist.size() == 1 && toplist.get(0).valueEquals(area) && toplist.get(0).getCountOfTaxes() == 0);
	}
	
	@SuppressWarnings("unchecked")
	@Test
	public void testInsertionAndDeletionAtTheSameTimeWithoutOverlap(){
		TaxiLog tlog1 = setUpTaxilog(cells.get(0), cells.get(1), BigDecimal.TEN, BigDecimal.TEN, "1");
		AreaWithProfit area = new AreaWithProfit(tlog1.getPickup_cell(),BigDecimal.valueOf(20), calendar.getTime()); 
		rollPseudoClock(0);
		insertTaxiLogs(Arrays.asList(tlog1));
		fireRules();
		assertTrue("1", toplist.size() == 1 && toplist.get(0).valueEquals(area));
		
		//+15 min
		rollPseudoClock(15*60*1000);
		insertTaxiLogs(Collections.emptyList());
		fireRules();
		assertTrue("2", toplist.size() == 1 && toplist.get(0).valueEquals(area));
		
		//+1 sec
		TaxiLog tlog2 = setUpTaxilog(cells.get(0), cells.get(1), BigDecimal.ONE, BigDecimal.ONE, "2");
		rollPseudoClock(1000);
		tlog2.setDropoff_datetime(calendar.getTime());
		insertTaxiLogs(Arrays.asList(tlog2));
		fireRules();
		
		area.setLastInserted(calendar.getTime());
		area.setMedianProfitIndex(BigDecimal.valueOf(2));
		assertTrue("3", toplist.size() == 1 && toplist.get(0).valueEquals(area));
		
		
		
	}
	@Test
	public void testInsertionAndDeletionAtTheSameTimeWithOverlap(){
		TaxiLog tlog1 = setUpTaxilog(cells.get(0), cells.get(1), BigDecimal.ONE, BigDecimal.ONE, "1");
		rollPseudoClock(0);
		AreaWithProfit area = new AreaWithProfit(tlog1.getPickup_cell(),BigDecimal.valueOf(2), calendar.getTime()); 		
		insertTaxiLogs(Arrays.asList(tlog1));
		fireRules();
		assertTrue("1", toplist.size() == 1 && toplist.get(0).valueEquals(area));
		
		//+15 min
		rollPseudoClock(15*60*1000);
		TaxiLog tlog2 = setUpTaxilog(cells.get(0), cells.get(1), BigDecimal.TEN, BigDecimal.TEN, "2");
		tlog2.setDropoff_datetime(calendar.getTime());
		insertTaxiLogs(Arrays.asList(tlog2));
		fireRules();
		area.setLastInserted(calendar.getTime());
		area.setMedianProfitIndex(BigDecimal.valueOf(11));
		assertTrue("2", toplist.size() == 1 && toplist.get(0).valueEquals(area));
		
		//+1 sec
		TaxiLog tlog3 = setUpTaxilog(cells.get(0), cells.get(1), BigDecimal.valueOf(5), BigDecimal.valueOf(5), "3");
		rollPseudoClock(1000);
		tlog3.setDropoff_datetime(calendar.getTime());
		insertTaxiLogs(Arrays.asList(tlog3));
		fireRules();
		
		area.setLastInserted(calendar.getTime());
		area.setMedianProfitIndex(BigDecimal.valueOf(15));
		assertTrue("3", toplist.size() == 1 && toplist.get(0).valueEquals(area));
		
		
		
	}



}
