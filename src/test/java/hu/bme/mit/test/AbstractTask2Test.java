package hu.bme.mit.test;

import static org.junit.Assert.assertTrue;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

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
	public void testOverFlowingAndAging() {
		List<TaxiLog> tlogs = Arrays.asList(
				// 20-19-18...
				setUpTaxilog(cells.get(0), cells.get(10), BigDecimal.TEN, BigDecimal.TEN, "1"),
				setUpTaxilog(cells.get(1), cells.get(9), BigDecimal.valueOf(9), BigDecimal.TEN, "2"),
				setUpTaxilog(cells.get(2), cells.get(8), BigDecimal.valueOf(8), BigDecimal.TEN, "3"),
				setUpTaxilog(cells.get(3), cells.get(7), BigDecimal.valueOf(7), BigDecimal.TEN, "4"),
				setUpTaxilog(cells.get(4), cells.get(6), BigDecimal.valueOf(6), BigDecimal.TEN, "5"),
				setUpTaxilog(cells.get(5), cells.get(5), BigDecimal.valueOf(5), BigDecimal.TEN, "6"),
				setUpTaxilog(cells.get(6), cells.get(4), BigDecimal.valueOf(4), BigDecimal.TEN, "7"),
				setUpTaxilog(cells.get(7), cells.get(3), BigDecimal.valueOf(3), BigDecimal.TEN, "8"),
				setUpTaxilog(cells.get(8), cells.get(2), BigDecimal.valueOf(2), BigDecimal.TEN, "9"),
				setUpTaxilog(cells.get(9), cells.get(1), BigDecimal.ONE, BigDecimal.TEN, "10"),
				setUpTaxilog(cells.get(10), cells.get(0), BigDecimal.ZERO, BigDecimal.TEN, "11"));

		List<AreaWithProfit> areaList = new ArrayList<AreaWithProfit>();
		rollPseudoClock(0);
		for (int i = 0; i < tlogs.size(); i++) {
			tlogs.get(i).setDropoff_datetime(calendar.getTime());
			insertTaxiLogs(Arrays.asList(tlogs.get(i)));
			fireRules();
			if(i < tlogs.size() - 1){
				rollPseudoClock(60*1000);
			}
			
			AreaWithProfit area = new AreaWithProfit(tlogs.get(i).getPickup_cell(), BigDecimal.valueOf(20 - i),
					tlogs.get(i).getDropoff_datetime());
			areaList.add(area);

		}
		
		// Ez kifejezetten gyönyörû, de mivel az adott cellára beérkezõ taxi is
		// frissíti a cellát, ez fog történni.
		for (int i = -5; i < tlogs.size() - 5; i++) {
			areaList.get(i + 5).setLastInserted(calendar.getTime());
			calendar.add(Calendar.MINUTE,(i < 0 ? -1 : 1));
		}

		// check 0-9
		for (int i = 0; i < 10; i++) {
			assertTrue("check" + i, areaList.get(i).valueEquals(toplist.get(i)));
		}

	
		rollPseudoClock(4 * 60 * 1000);
		insertTaxiLogs(Collections.emptyList());
		fireRules();

		// check 10-19
		for (int i = 0; i < 10; i++) {
			assertTrue("check" + (10 + i), areaList.get(i).valueEquals(toplist.get(i)));
		}

	
		rollPseudoClock(1000);
		insertTaxiLogs(Collections.emptyList());
		fireRules();

		// check 20-29
		for (int i = 0; i < 10; i++) {
			assertTrue("check" + (20 + i), areaList.get(i + 1).valueEquals(toplist.get(i)));
		}

	/*	for (int i = 0; i < 10; i++) {
			clock.advanceTime(1, TimeUnit.MINUTES);
			kSession.insert(new Tick(clock.getCurrentTime(), System.currentTimeMillis()));
			kSession.fireAllRules();
			for (int j = 0; j < toplist.size(); j++) {
				assertTrue("check" + (30 + (i * 10 + j)),
						areaList.get(j + i+2).valueEquals(toplist.get(j)) && toplist.size() == 9 - i);
			}
		}*/

	}



}
