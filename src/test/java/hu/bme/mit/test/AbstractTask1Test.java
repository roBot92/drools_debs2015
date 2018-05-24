package hu.bme.mit.test;

import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import edu.emory.mathcs.backport.java.util.Collections;
import hu.bme.mit.entities.Route;
import hu.bme.mit.entities.TaxiLog;
import hu.bme.mit.positioning.Cell;
import hu.bme.mit.toplist.FrequentRoutesToplistSet;

public abstract class AbstractTask1Test {
	protected static List<Cell> cells;
	protected static List<TaxiLog> route1tlogs;
	protected static List<TaxiLog> route2tlogs;
	protected static List<TaxiLog> route3tlogs;
	protected FrequentRoutesToplistSet toplist;
	protected Calendar calendar;
	
	protected long currentTimeInMillis = 0;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {

		cells = Arrays.asList(new Cell(1, 1), new Cell(1, 2), new Cell(2, 1), new Cell(2, 2), new Cell(3, 1),
				new Cell(3, 2));

		route1tlogs = Arrays.asList(setUpTaxilog(cells.get(0), cells.get(1)), setUpTaxilog(cells.get(0), cells.get(1)),
				setUpTaxilog(cells.get(0), cells.get(1)), setUpTaxilog(cells.get(0), cells.get(1)));

		route2tlogs = Arrays.asList(setUpTaxilog(cells.get(2), cells.get(3)), setUpTaxilog(cells.get(2), cells.get(3)),
				setUpTaxilog(cells.get(2), cells.get(3)), setUpTaxilog(cells.get(2), cells.get(3)));

		route3tlogs = Arrays.asList(setUpTaxilog(cells.get(4), cells.get(5)), setUpTaxilog(cells.get(4), cells.get(5)),
				setUpTaxilog(cells.get(4), cells.get(5)), setUpTaxilog(cells.get(4), cells.get(5)));

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
		toplist = new FrequentRoutesToplistSet();
		calendar = getZeroTimeCalendar();
		for (int i = 0; i < 4; i++) {
			route1tlogs.get(i).setProcessed(false);
			route1tlogs.get(i).setDropoff_datetime(calendar.getTime());
			route2tlogs.get(i).setProcessed(false);
			route2tlogs.get(i).setDropoff_datetime(calendar.getTime());
			route3tlogs.get(i).setProcessed(false);
			route3tlogs.get(i).setDropoff_datetime(calendar.getTime());
		}

	}

	protected Calendar getZeroTimeCalendar() {
		Calendar cal = Calendar.getInstance();
		cal.setTimeInMillis(0);
		return cal;
	}

	protected boolean containsFirstNElements(int n, Route r, FrequentRoutesToplistSet set) {
		for (int i = 0; i < n; i++) {
			if (r.equals(set.get(i))) {
				return true;
			}
		}
		return false;
	}
	
	@Test
	public void testInsertOneTaxiLog() {

		TaxiLog tlog1 = route1tlogs.get(0);
		Route route = new Route(tlog1.getPickup_cell(), tlog1.getDropoff_cell(), tlog1.getDropoff_datetime(), 1);

		insertTaxiLogs(Arrays.asList(tlog1));
		rollPseudoClock(0);
		fireRules();

		boolean check = route.valueEquals(toplist.get(0)) && toplist.size() == 1;
		assertTrue(check);
	}
	@Test
	public void testSortByFrequency() {
		TaxiLog tlog1 = route1tlogs.get(0);
		TaxiLog tlog2 = route2tlogs.get(0);
		TaxiLog tlog3 = route1tlogs.get(1);

		Route route1 = new Route(tlog1.getPickup_cell(), tlog1.getDropoff_cell(), tlog1.getDropoff_datetime(), 1);
		Route route2 = new Route(tlog2.getPickup_cell(), tlog2.getDropoff_cell(), tlog2.getDropoff_datetime(), 1);
		Route route3 = new Route(tlog3.getPickup_cell(), tlog3.getDropoff_cell(), tlog3.getDropoff_datetime(), 2);
		//kSession.insert(tlog1);
		//kSession.insert(tlog2);
		insertTaxiLogs(Arrays.asList(tlog1, tlog2));
		rollPseudoClock(0);
		fireRules();

		boolean check = toplist.size() == 2 && route2.equals(toplist.get(0)) && route1.equals(toplist.get(1));
		assertTrue("check1", check);

		insertTaxiLogs(Arrays.asList(tlog3));
		rollPseudoClock(0);
		fireRules();
		check = toplist.size() == 2 && route2.equals(toplist.get(1)) && route3.equals(toplist.get(0));
		assertTrue("check2", check);

	}
	
	@SuppressWarnings("unchecked")
	@Test
	public void testAgeing() {

		// First minute +1 route1, +1 route2, +1 route3
		insertTaxiLogs(Arrays.asList(route1tlogs.get(0), route2tlogs.get(0),route3tlogs.get(0)));
		rollPseudoClock(0);
		fireRules();

		Route route1 = new Route(route1tlogs.get(0).getPickup_cell(), route1tlogs.get(0).getDropoff_cell(),
				route1tlogs.get(0).getDropoff_datetime(), 1);
		Route route2 = new Route(route2tlogs.get(0).getPickup_cell(), route2tlogs.get(0).getDropoff_cell(),
				route2tlogs.get(0).getDropoff_datetime(), 1);
		Route route3 = new Route(route3tlogs.get(0).getPickup_cell(), route3tlogs.get(0).getDropoff_cell(),
				route3tlogs.get(0).getDropoff_datetime(), 1);
		assertTrue("check1", toplist.size() == 3 && toplist.get(0).valueEquals(route3)
				&& toplist.get(1).valueEquals(route2) && toplist.get(2).valueEquals(route1));

		// Second minute, +1 route1, +1 route2
		rollPseudoClock(60*1000);

		route1tlogs.get(1).setDropoff_datetime(calendar.getTime());
		
		route2tlogs.get(1).setDropoff_datetime(calendar.getTime());
		
		insertTaxiLogs(Arrays.asList(route1tlogs.get(1), route2tlogs.get(1)));
		fireRules();

		route1.setFrequency(2);
		route1.setLastDropoffTime(calendar.getTime());
		route2.setFrequency(2);
		route2.setLastDropoffTime(calendar.getTime());

		assertTrue("check2", toplist.size() == 3 && toplist.get(0).valueEquals(route2)
				&& toplist.get(1).valueEquals(route1) && toplist.get(2).valueEquals(route3));

		// Third minute +1 route1
		rollPseudoClock(60*1000);
		route1tlogs.get(2).setDropoff_datetime(calendar.getTime());
		insertTaxiLogs(Arrays.asList(route1tlogs.get(2)));
		fireRules();
		
		route1.setFrequency(3);
		route1.setLastDropoffTime(calendar.getTime());
		
		assertTrue("check3", toplist.size() == 3 && toplist.get(0).valueEquals(route1)
				&& toplist.get(1).valueEquals(route2) && toplist.get(2).valueEquals(route3));

		
		rollPseudoClock(28*60*1000);
		insertTaxiLogs(Collections.emptyList());
		fireRules();
		
		
		assertTrue("check4", toplist.size() == 3 && toplist.get(0).valueEquals(route1)
				&& toplist.get(1).valueEquals(route2) && toplist.get(2).valueEquals(route3));

		insertTaxiLogs(Collections.emptyList());
		rollPseudoClock(1000);
		fireRules();
		route1.setFrequency(2);
		route2.setFrequency(1);
		assertTrue("check5",
				toplist.size() == 2 && toplist.get(0).valueEquals(route1) && toplist.get(1).valueEquals(route2));

		insertTaxiLogs(Collections.emptyList());
		rollPseudoClock(60*1000);
		fireRules();
		route1.setFrequency(1);

		assertTrue("check6", toplist.size() == 1 && toplist.get(0).valueEquals(route1));

		insertTaxiLogs(Collections.emptyList());
		rollPseudoClock(60*1000);
		fireRules();
		route1.setFrequency(1);
		assertTrue("check7", toplist.size() == 0);

	}
	
	@SuppressWarnings("unchecked")
	@Test
	public void testSlidingOut() {
		List<TaxiLog> tlogs = Arrays.asList(setUpTaxilog(cells.get(0), cells.get(1)),
				setUpTaxilog(cells.get(0), cells.get(1)), setUpTaxilog(cells.get(1), cells.get(1)),
				setUpTaxilog(cells.get(1), cells.get(1)), setUpTaxilog(cells.get(1), cells.get(2)),
				setUpTaxilog(cells.get(1), cells.get(2)), setUpTaxilog(cells.get(1), cells.get(3)),
				setUpTaxilog(cells.get(1), cells.get(3)), setUpTaxilog(cells.get(1), cells.get(4)),
				setUpTaxilog(cells.get(1), cells.get(4)), setUpTaxilog(cells.get(1), cells.get(5)),
				setUpTaxilog(cells.get(1), cells.get(5)), setUpTaxilog(cells.get(2), cells.get(1)),
				setUpTaxilog(cells.get(2), cells.get(1)), setUpTaxilog(cells.get(2), cells.get(2)),
				setUpTaxilog(cells.get(2), cells.get(2)), setUpTaxilog(cells.get(2), cells.get(3)),
				setUpTaxilog(cells.get(2), cells.get(3)), setUpTaxilog(cells.get(2), cells.get(4)),
				setUpTaxilog(cells.get(2), cells.get(4)), setUpTaxilog(cells.get(2), cells.get(5)));

		
		insertTaxiLogs(tlogs.subList(0, 20));
		rollPseudoClock(0);
		fireRules();

		assertTrue("check1", toplist.size() == 10 && toplist.getSetSize() == 10);
		rollPseudoClock(15*60*1000);

		tlogs.get(20).setDropoff_datetime(calendar.getTime());
		insertTaxiLogs(Arrays.asList(tlogs.get(20)));
		fireRules();

		Route route = new Route(cells.get(2), cells.get(5), tlogs.get(20).getDropoff_datetime(), 1);

		assertTrue("check2",
				toplist.size() == 10 && toplist.getSetSize() == 11 && !containsFirstNElements(FrequentRoutesToplistSet.MAX_ELEMENT_NUMBER, route, toplist));
		rollPseudoClock((15 * 60 + 1)*1000);
		insertTaxiLogs(Collections.emptyList());
		fireRules();
		assertTrue("check3",
				toplist.size() == 1 && toplist.size() == 1	&& containsFirstNElements(FrequentRoutesToplistSet.MAX_ELEMENT_NUMBER, route, toplist));
	}
	
	@SuppressWarnings("unchecked")
	@Test
	public void testInserttionAndDeletionAtTheSameTimeWithoutOverlap() {
		List<TaxiLog> tlogs = Arrays.asList(setUpTaxilog(cells.get(0), cells.get(1)),
				setUpTaxilog(cells.get(0), cells.get(1)), setUpTaxilog(cells.get(0), cells.get(1)));
		Route route = new Route(tlogs.get(0).getPickup_cell(), tlogs.get(0).getDropoff_cell(),
				tlogs.get(0).getDropoff_datetime(), 2);
		insertTaxiLogs(Arrays.asList(tlogs.get(0), tlogs.get(1)));
		rollPseudoClock(0);
		fireRules();
		assertTrue("check1", toplist.size() == 1 && toplist.get(0).valueEquals(route));


		rollPseudoClock(30*60*1000);
		insertTaxiLogs(Collections.emptyList());
		fireRules();
		
		assertTrue("check2", toplist.size() == 1 && toplist.get(0).valueEquals(route));

		
		rollPseudoClock(1000);
		tlogs.get(2).setDropoff_datetime(calendar.getTime());
		insertTaxiLogs(Arrays.asList(tlogs.get(2)));
		fireRules();

		route.setFrequency(1);
		route.setLastDropoffTime(tlogs.get(2).getDropoff_datetime());
		assertTrue("check3", toplist.size() == 1 && toplist.get(0).valueEquals(route));

	}
	
	@SuppressWarnings("unchecked")
	@Test
	public void testInserttionAndDeletionAtTheSameTimeWithOverlap() {
		List<TaxiLog> tlogs = Arrays.asList(setUpTaxilog(cells.get(0), cells.get(1)),
				setUpTaxilog(cells.get(0), cells.get(1)), setUpTaxilog(cells.get(0), cells.get(1)));
		Route route = new Route(tlogs.get(0).getPickup_cell(), tlogs.get(0).getDropoff_cell(),
				tlogs.get(0).getDropoff_datetime(), 1);
		insertTaxiLogs(Arrays.asList(tlogs.get(0)));
		rollPseudoClock(0);
		fireRules();
		
		assertTrue("check1", toplist.size() == 1 && toplist.get(0).valueEquals(route));

		
		rollPseudoClock(1000);
		tlogs.get(1).setDropoff_datetime(calendar.getTime());
		route.setFrequency(2);
		route.setLastDropoffTime(calendar.getTime());
		insertTaxiLogs(Arrays.asList(tlogs.get(1)));
		fireRules();
		assertTrue("check2", toplist.size() == 1 && toplist.get(0).valueEquals(route));

		
		rollPseudoClock(30*60*1000);
		
		tlogs.get(2).setDropoff_datetime(calendar.getTime());
		insertTaxiLogs(Arrays.asList(tlogs.get(2)));
		fireRules();

		route.setLastDropoffTime(calendar.getTime());
		assertTrue("check3", toplist.size() == 1 && toplist.get(0).valueEquals(route));

		rollPseudoClock(1000);
		insertTaxiLogs(Collections.emptyList());
		fireRules();
		
		route.setFrequency(1);
		assertTrue("check3", toplist.size() == 1 && toplist.get(0).valueEquals(route));

	}
	
	protected  abstract void insertTaxiLogs(List<TaxiLog> taxiLogs);
	protected abstract void rollPseudoClock(long time);
	protected abstract void fireRules();

}
