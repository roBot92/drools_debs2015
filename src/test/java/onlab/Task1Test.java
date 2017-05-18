package onlab;

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

import onlab.positioning.Cell;
import onlab.event.Route;
import onlab.event.TaxiLog;
import onlab.event.Tick;
import onlab.utility.FrequentRoutesToplistSet;


public class Task1Test {

	private FrequentRoutesToplistSet<Route> toplist;
	private KieSession kSession;
	private SessionPseudoClock clock;
	private static List<Cell> cells;
	private static List<TaxiLog> route1tlogs;
	private static List<TaxiLog> route2tlogs;
	private static List<TaxiLog> route3tlogs;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		// calendar = Calendar.getInstance();
		cells = Arrays.asList(new Cell(1, 1), new Cell(1, 2), new Cell(2, 1), new Cell(2, 2), new Cell(3, 1),
				new Cell(3, 2));

		route1tlogs = Arrays.asList(
				setUpTaxilog(cells.get(0),
						cells.get(1)/* , getZeroTimeCalendar() */),
				setUpTaxilog(cells.get(0),
						cells.get(1)/* , getZeroTimeCalendar() */),
				setUpTaxilog(cells.get(0),
						cells.get(1)/* , getZeroTimeCalendar() */),
				setUpTaxilog(cells.get(0),
						cells.get(1)/* , getZeroTimeCalendar() */));

		route2tlogs = Arrays.asList(
				setUpTaxilog(cells.get(2),
						cells.get(3)/* , getZeroTimeCalendar() */),
				setUpTaxilog(cells.get(2),
						cells.get(3)/* , getZeroTimeCalendar() */),
				setUpTaxilog(cells.get(2),
						cells.get(3)/* , getZeroTimeCalendar() */),
				setUpTaxilog(cells.get(2),
						cells.get(3)/* , getZeroTimeCalendar() */));

		route3tlogs = Arrays.asList(
				setUpTaxilog(cells.get(4),
						cells.get(5)/* , getZeroTimeCalendar() */),
				setUpTaxilog(cells.get(4),
						cells.get(5)/* , getZeroTimeCalendar() */),
				setUpTaxilog(cells.get(4),
						cells.get(5)/* , getZeroTimeCalendar() */),
				setUpTaxilog(cells.get(4),
						cells.get(5)/* , getZeroTimeCalendar() */));

	}

	@Before
	public void setUp() throws Exception {
		toplist = new FrequentRoutesToplistSet<Route>();
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
		assertTrue("check1", check);

		kSession.insert(tlog1);
	}

	@Test
	public void testAgeing() {

		// First minute +1 route1, +1 route2, +1 route3
		kSession.insert(route1tlogs.get(0));
		kSession.insert(route2tlogs.get(0));
		kSession.insert(route3tlogs.get(0));
		kSession.fireAllRules();
		// System.out.println(toplist);

		// Second minute, +1 route1, +1 route2
		clock.advanceTime(60, TimeUnit.SECONDS);

		route1tlogs.get(1).setDropoff_datetime(new Date(clock.getCurrentTime()));
		kSession.insert(route1tlogs.get(1));
		route2tlogs.get(1).setDropoff_datetime(new Date(clock.getCurrentTime()));
		kSession.insert(route2tlogs.get(1));
		kSession.fireAllRules();
		// System.out.println(toplist);

		// Third minute +1 route1
		clock.advanceTime(60, TimeUnit.SECONDS);
		route1tlogs.get(2).setDropoff_datetime(new Date(clock.getCurrentTime()));
		kSession.insert(route1tlogs.get(2));
		kSession.fireAllRules();
		// System.out.println(toplist);

		// Fourth minute, +1 route3
		clock.advanceTime(60, TimeUnit.SECONDS);
		route3tlogs.get(1).setDropoff_datetime(new Date(clock.getCurrentTime()));
		kSession.insert(route3tlogs.get(1));
		kSession.fireAllRules();

		// System.out.println(toplist);

		clock.advanceTime(30, TimeUnit.MINUTES);
		// clock.advanceTime(1, TimeUnit.SECONDS);

		kSession.insert(new Tick(clock.getCurrentTime()));
		kSession.fireAllRules();

		assertTrue(true);
		// System.out.println(toplist);

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
		clock.advanceTime(15*60+1, TimeUnit.SECONDS);
		kSession.insert(new Tick(clock.getCurrentTime()));
		kSession.fireAllRules();
		assertTrue("check3", toplist.size() == 1 && kSession.getQueryResults("taxis").size() == 1
				&& kSession.getQueryResults("routes").size() == 1 && toplist.contains(route));
	}

	private Date getZeroTimeCalendar() {
		Calendar cal = Calendar.getInstance();
		cal.setTimeInMillis(0);
		return cal.getTime();
	}

}
