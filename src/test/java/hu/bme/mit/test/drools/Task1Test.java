package hu.bme.mit.test.drools;

import static org.junit.Assert.*;

import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.drools.core.time.SessionPseudoClock;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.kie.api.KieServices;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.KieSessionConfiguration;
import org.kie.api.runtime.conf.ClockTypeOption;

import hu.bme.mit.entities.Route;
import hu.bme.mit.entities.TaxiLog;
import hu.bme.mit.entities.Tick;
import hu.bme.mit.positioning.Cell;
import hu.bme.mit.toplist.FrequentRoutesToplistSet;

public class Task1Test {

	private FrequentRoutesToplistSet toplist;
	private KieSession kSession;
	private SessionPseudoClock clock;
	private static List<Cell> cells;
	private static List<TaxiLog> route1tlogs;
	private static List<TaxiLog> route2tlogs;
	private static List<TaxiLog> route3tlogs;
/*
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		// calendar = Calendar.getInstance();
		cells = Arrays.asList(new Cell(1, 1), new Cell(1, 2), new Cell(2, 1), new Cell(2, 2), new Cell(3, 1),
				new Cell(3, 2));

		route1tlogs = Arrays.asList(setUpTaxilog(cells.get(0), cells.get(1)), setUpTaxilog(cells.get(0), cells.get(1)),
				setUpTaxilog(cells.get(0), cells.get(1)), setUpTaxilog(cells.get(0), cells.get(1)));

		route2tlogs = Arrays.asList(setUpTaxilog(cells.get(2), cells.get(3)), setUpTaxilog(cells.get(2), cells.get(3)),
				setUpTaxilog(cells.get(2), cells.get(3)), setUpTaxilog(cells.get(2), cells.get(3)));

		route3tlogs = Arrays.asList(setUpTaxilog(cells.get(4), cells.get(5)), setUpTaxilog(cells.get(4), cells.get(5)),
				setUpTaxilog(cells.get(4), cells.get(5)), setUpTaxilog(cells.get(4), cells.get(5)));

	}

	@Before
	public void setUp() throws Exception {
		toplist = new FrequentRoutesToplistSet();
		KieServices ks = KieServices.Factory.get();
		KieContainer kContainer = ks.getKieClasspathContainer();
		KieSessionConfiguration config = ks.newKieSessionConfiguration();

		config.setOption(ClockTypeOption.get("pseudo"));
		kSession = kContainer.newKieSession("ksession-rules", config);
		clock = kSession.getSessionClock();
		// calendar =
		// Calendar.getInstance().setTimeInMillis(clock.getCurrentTime());
		kSession.setGlobal("mostFrequentRoutes", toplist);
		// calendar = Calendar.getInstance();
		// calendar.setTimeInMillis(0);

		for (int i = 0; i < 4; i++) {
			route1tlogs.get(i).setProcessed(false);
			route1tlogs.get(i).setDropoff_datetime(getZeroTimeCalendar());
			route2tlogs.get(i).setProcessed(false);
			route2tlogs.get(i).setDropoff_datetime(getZeroTimeCalendar());
			route3tlogs.get(i).setProcessed(false);
			route3tlogs.get(i).setDropoff_datetime(getZeroTimeCalendar());
		}

	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void test_insertOneTaxiLog() {

		TaxiLog tlog1 = route1tlogs.get(0);
		Route route = new Route(tlog1.getPickup_cell(), tlog1.getDropoff_cell(), tlog1.getDropoff_datetime(), 1);

		kSession.insert(tlog1);
		kSession.fireAllRules();

		boolean check = route.equals(toplist.get(0)) && toplist.size() == 1;
		toplist.refreshInsertedForDelay(1, tlog1.getPickup_cell());
		assertTrue(check);
	}

	@Test
	public void test_sortByFrequency() {
		TaxiLog tlog1 = route1tlogs.get(0);
		TaxiLog tlog2 = route2tlogs.get(0);
		TaxiLog tlog3 = route1tlogs.get(1);

		Route route1 = new Route(tlog1.getPickup_cell(), tlog1.getDropoff_cell(), tlog1.getDropoff_datetime(), 1);
		Route route2 = new Route(tlog2.getPickup_cell(), tlog2.getDropoff_cell(), tlog2.getDropoff_datetime(), 1);
		Route route3 = new Route(tlog3.getPickup_cell(), tlog3.getDropoff_cell(), tlog3.getDropoff_datetime(), 2);
		kSession.insert(tlog1);
		kSession.insert(tlog2);
		kSession.fireAllRules();

		boolean check = toplist.size() == 2 && route2.equals(toplist.get(0)) && route1.equals(toplist.get(1));
		assertTrue("check1", check);

		kSession.insert(tlog3);
		kSession.fireAllRules();
		check = toplist.size() == 2 && route2.equals(toplist.get(1)) && route3.equals(toplist.get(0));
		assertTrue("check2", check);

		// kSession.insert(tlog1);
	}

	@Test
	public void test_Ageing() {

		// First minute +1 route1, +1 route2, +1 route3
		kSession.insert(route1tlogs.get(0));
		kSession.insert(route2tlogs.get(0));
		kSession.insert(route3tlogs.get(0));
		kSession.fireAllRules();

		Route route1 = new Route(route1tlogs.get(0).getPickup_cell(), route1tlogs.get(0).getDropoff_cell(),
				route1tlogs.get(0).getDropoff_datetime(), 1);
		Route route2 = new Route(route2tlogs.get(0).getPickup_cell(), route2tlogs.get(0).getDropoff_cell(),
				route2tlogs.get(0).getDropoff_datetime(), 1);
		Route route3 = new Route(route3tlogs.get(0).getPickup_cell(), route3tlogs.get(0).getDropoff_cell(),
				route3tlogs.get(0).getDropoff_datetime(), 1);
		assertTrue("check1", toplist.size() == 3 && toplist.get(0).valueEquals(route3)
				&& toplist.get(1).valueEquals(route2) && toplist.get(2).valueEquals(route1));

		// Second minute, +1 route1, +1 route2
		clock.advanceTime(60, TimeUnit.SECONDS);
		kSession.insert(new Tick(clock.getCurrentTime()));

		route1tlogs.get(1).setDropoff_datetime(new Date(clock.getCurrentTime()));
		kSession.insert(route1tlogs.get(1));
		route2tlogs.get(1).setDropoff_datetime(new Date(clock.getCurrentTime()));
		kSession.insert(route2tlogs.get(1));
		kSession.fireAllRules();

		route1.setFrequency(2);
		route1.setLastDropoffTime(new Date(clock.getCurrentTime()));
		route2.setFrequency(2);
		route2.setLastDropoffTime(new Date(clock.getCurrentTime()));

		assertTrue("check2", toplist.size() == 3 && toplist.get(0).valueEquals(route2)
				&& toplist.get(1).valueEquals(route1) && toplist.get(2).valueEquals(route3));

		// Third minute +1 route1
		clock.advanceTime(60, TimeUnit.SECONDS);
		kSession.insert(new Tick(clock.getCurrentTime()));
		route1tlogs.get(2).setDropoff_datetime(new Date(clock.getCurrentTime()));
		kSession.insert(route1tlogs.get(2));
		kSession.fireAllRules();
		route1.setFrequency(3);
		route1.setLastDropoffTime(new Date(clock.getCurrentTime()));
		kSession.insert(route1tlogs.get(2));
		assertTrue("check3", toplist.size() == 3 && toplist.get(0).valueEquals(route1)
				&& toplist.get(1).valueEquals(route2) && toplist.get(2).valueEquals(route3));

		clock.advanceTime(28, TimeUnit.MINUTES);
		kSession.insert(new Tick(clock.getCurrentTime()));
		kSession.fireAllRules();
		assertTrue("check4", toplist.size() == 3 && toplist.get(0).valueEquals(route1)
				&& toplist.get(1).valueEquals(route2) && toplist.get(2).valueEquals(route3));

		clock.advanceTime(1, TimeUnit.SECONDS);
		kSession.insert(new Tick(clock.getCurrentTime()));
		kSession.fireAllRules();
		route1.setFrequency(2);
		route2.setFrequency(1);
		assertTrue("check5",
				toplist.size() == 2 && toplist.get(0).valueEquals(route1) && toplist.get(1).valueEquals(route2));

		clock.advanceTime(1, TimeUnit.MINUTES);
		kSession.insert(new Tick(clock.getCurrentTime()));
		kSession.fireAllRules();
		route1.setFrequency(1);

		assertTrue("check6", toplist.size() == 1 && toplist.get(0).valueEquals(route1));

		clock.advanceTime(1, TimeUnit.MINUTES);
		kSession.insert(new Tick(clock.getCurrentTime()));
		kSession.fireAllRules();
		assertTrue("check7", toplist.size() == 0);

	}

	private static TaxiLog setUpTaxilog(Cell startCell, Cell endCell) {
		TaxiLog tlog = new TaxiLog();
		Calendar zeroCalendar = Calendar.getInstance();
		tlog.setPickup_cell(startCell);
		tlog.setDropoff_cell(endCell);

		zeroCalendar.setTimeInMillis(0);
		tlog.setDropoff_datetime(zeroCalendar.getTime());
		return tlog;
	}

	@Test
	public void test_slidingOut() {
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

		for (int i = 0; i < tlogs.size() - 1; i++) {
			kSession.insert(tlogs.get(i));
		}
		kSession.fireAllRules();
		// System.out.println("sliding first: "+toplist);

		assertTrue("check1", toplist.size() == 10 && kSession.getQueryResults("taxis").size() == 20
				&& kSession.getQueryResults("routes").size() == 10);
		clock.advanceTime(15, TimeUnit.MINUTES);
		kSession.insert(new Tick(clock.getCurrentTime()));
		tlogs.get(20).setDropoff_datetime(new Date(clock.getCurrentTime()));
		kSession.insert(tlogs.get(20));
		kSession.fireAllRules();

		Route route = new Route(cells.get(2), cells.get(5), tlogs.get(20).getDropoff_datetime(), -1);

		assertTrue("check2", toplist.size() == 10 && kSession.getQueryResults("taxis").size() == 21
				&& kSession.getQueryResults("routes").size() == 11 && !toplist.contains(route));
		clock.advanceTime(15 * 60 + 1, TimeUnit.SECONDS);
		kSession.insert(new Tick(clock.getCurrentTime()));
		kSession.fireAllRules();
		assertTrue("check3", toplist.size() == 1 && kSession.getQueryResults("taxis").size() == 1
				&& kSession.getQueryResults("routes").size() == 1 && toplist.contains(route));
	}

	@Test
	public void test_inserttionAndDeletionAtTheSameTimeWithoutOverlap() {
		List<TaxiLog> tlogs = Arrays.asList(setUpTaxilog(cells.get(0), cells.get(1)),
				setUpTaxilog(cells.get(0), cells.get(1)), setUpTaxilog(cells.get(0), cells.get(1)));
		Route route = new Route(tlogs.get(0).getPickup_cell(), tlogs.get(0).getDropoff_cell(),
				tlogs.get(0).getDropoff_datetime(), 2);
		kSession.insert(tlogs.get(0));
		kSession.insert(tlogs.get(1));
		kSession.insert(new Tick(clock.getCurrentTime()));
		kSession.fireAllRules();
		assertTrue("check1", toplist.size() == 1 && toplist.get(0).valueEquals(route));

		clock.advanceTime(30, TimeUnit.MINUTES);

		kSession.insert(new Tick(clock.getCurrentTime()));
		assertTrue("check2", toplist.size() == 1 && toplist.get(0).valueEquals(route));

		clock.advanceTime(1, TimeUnit.SECONDS);
		tlogs.get(2).setDropoff_datetime(new Date(clock.getCurrentTime()));
		kSession.insert(tlogs.get(2));
		kSession.insert(new Tick(clock.getCurrentTime()));
		kSession.fireAllRules();

		route.setFrequency(1);
		route.setLastDropoffTime(tlogs.get(2).getDropoff_datetime());
		assertTrue("check3", toplist.size() == 1 && toplist.get(0).valueEquals(route));

	}

	@Test
	public void test_inserttionAndDeletionAtTheSameTimeWithOverlap() {
		List<TaxiLog> tlogs = Arrays.asList(setUpTaxilog(cells.get(0), cells.get(1)),
				setUpTaxilog(cells.get(0), cells.get(1)), setUpTaxilog(cells.get(0), cells.get(1)));
		Route route = new Route(tlogs.get(0).getPickup_cell(), tlogs.get(0).getDropoff_cell(),
				tlogs.get(0).getDropoff_datetime(), 1);
		kSession.insert(tlogs.get(0));

		kSession.insert(new Tick(clock.getCurrentTime()));
		kSession.fireAllRules();
		assertTrue("check1", toplist.size() == 1 && toplist.get(0).valueEquals(route));

		clock.advanceTime(1, TimeUnit.SECONDS);
		tlogs.get(1).setDropoff_datetime(new Date(clock.getCurrentTime()));
		route.setFrequency(2);
		route.setLastDropoffTime(new Date(clock.getCurrentTime()));
		kSession.insert(tlogs.get(1));
		kSession.insert(new Tick(clock.getCurrentTime()));
		kSession.fireAllRules();
		assertTrue("check2", toplist.size() == 1 && toplist.get(0).valueEquals(route));

		clock.advanceTime(30, TimeUnit.MINUTES);
		kSession.insert(new Tick(clock.getCurrentTime()));
		tlogs.get(2).setDropoff_datetime(new Date(clock.getCurrentTime()));
		kSession.insert(tlogs.get(2));
		kSession.fireAllRules();

		route.setLastDropoffTime(new Date(clock.getCurrentTime()));
		assertTrue("check3", toplist.size() == 1 && toplist.get(0).valueEquals(route));

		clock.advanceTime(1, TimeUnit.SECONDS);
		kSession.insert(new Tick(clock.getCurrentTime()));
		kSession.fireAllRules();
		route.setFrequency(1);
		assertTrue("check3", toplist.size() == 1 && toplist.get(0).valueEquals(route));

	}

	private Date getZeroTimeCalendar() {
		Calendar cal = Calendar.getInstance();
		cal.setTimeInMillis(0);
		return cal.getTime();
	}
*/
}
