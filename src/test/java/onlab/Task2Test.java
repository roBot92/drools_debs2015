package onlab;

import static org.junit.Assert.*;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.drools.core.time.SessionPseudoClock;
//import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.kie.api.KieServices;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.KieSessionConfiguration;
import org.kie.api.runtime.conf.ClockTypeOption;

import onlab.event.AreaWithProfit;
import onlab.event.TaxiLog;
import onlab.event.Tick;
import onlab.positioning.Cell;
import onlab.utility.ProfitableAreaToplistSet;

public class Task2Test {

	private ProfitableAreaToplistSet<AreaWithProfit> toplist;
	private KieSession kSession;
	private SessionPseudoClock clock;
	private static List<Cell> cells;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		cells = Arrays.asList(new Cell(1, 1), new Cell(1, 2), new Cell(1, 3), new Cell(2, 1), new Cell(2, 2),
				new Cell(2, 3), new Cell(3, 1), new Cell(3, 2), new Cell(3, 3), new Cell(4, 1), new Cell(4, 2));

	}

	@Before
	public void setUp() throws Exception {
		toplist = new ProfitableAreaToplistSet<AreaWithProfit>();
		KieServices ks = KieServices.Factory.get();
		KieContainer kContainer = ks.getKieClasspathContainer();
		KieSessionConfiguration config = ks.newKieSessionConfiguration();

		config.setOption(ClockTypeOption.get("pseudo"));

		kSession = kContainer.newKieSession("ksession-rules", config);
		clock = kSession.getSessionClock();
		// calendar =
		// Calendar.getInstance().setTimeInMillis(clock.getCurrentTime());

		kSession.setGlobal("mostProfitableAreas", toplist);
		// calendar = Calendar.getInstance();
		// calendar.setTimeInMillis(0);

	}

	@Test
	public void insertForOneArea_Test() {
		TaxiLog tlog1 = setUpTaxilog(cells.get(0), cells.get(1), BigDecimal.ONE, BigDecimal.ONE, "1");
		TaxiLog tlog2 = setUpTaxilog(cells.get(0), cells.get(1), BigDecimal.ONE, BigDecimal.TEN, "2");

		AreaWithProfit area = new AreaWithProfit(cells.get(0), BigDecimal.valueOf(2), new Date(clock.getCurrentTime()));

		kSession.insert(tlog1);
		kSession.insert(new Tick(clock.getCurrentTime()));
		kSession.fireAllRules();

		assertTrue("check1", area.equals(toplist.get(0)) && toplist.size() == 1);

		clock.advanceTime(60, TimeUnit.SECONDS);
		tlog2.setDropoff_datetime(new Date(clock.getCurrentTime()));
		area.setLastInserted(new Date(clock.getCurrentTime()));
		area.setMedianProfitIndex(BigDecimal.valueOf(6.5));

		kSession.insert(tlog2);
		kSession.fireAllRules();

		assertTrue("check2", area.equals(toplist.get(0)) && toplist.size() == 1);

	}

	@Test
	public void insertForTwoArea_Test() {
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
		AreaWithProfit area1 = new AreaWithProfit(cells.get(0), medianProfit1, new Date(clock.getCurrentTime()));
		AreaWithProfit area2 = new AreaWithProfit(cells.get(1), medianProfit2, new Date(clock.getCurrentTime()));
		for (TaxiLog tlog : tlogs) {
			kSession.insert(tlog);
		}

		kSession.fireAllRules();

		assertTrue(toplist.size() == 2 && area1.equals(toplist.get(0)) && area2.equals(toplist.get(1)));

	}

	@Test
	public void lessEmptyTaxiByNewDrive_Test() {
		List<TaxiLog> tlogs = Arrays.asList(
				setUpTaxilog(cells.get(0), cells.get(1), BigDecimal.ONE, BigDecimal.ONE, "1"),
				setUpTaxilog(cells.get(0), cells.get(1), BigDecimal.ONE, BigDecimal.TEN, "2"),
				setUpTaxilog(cells.get(1), cells.get(0), BigDecimal.ONE, BigDecimal.ONE, "3"),
				setUpTaxilog(cells.get(1), cells.get(0), BigDecimal.ONE, BigDecimal.ONE, "4"),
				setUpTaxilog(cells.get(0), cells.get(2), BigDecimal.TEN, BigDecimal.TEN, "3"));
		AreaWithProfit area1 = new AreaWithProfit(cells.get(0), BigDecimal.valueOf(3.25),
				new Date(clock.getCurrentTime()));
		AreaWithProfit area2 = new AreaWithProfit(cells.get(1), BigDecimal.valueOf(1),
				new Date(clock.getCurrentTime()));

		kSession.insert(tlogs.get(0));
		kSession.insert(tlogs.get(1));
		kSession.insert(tlogs.get(2));
		kSession.insert(tlogs.get(3));

		kSession.fireAllRules();
		assertTrue("firstCheck", toplist.size() == 2 && area1.equals(toplist.get(0)) && area2.equals(toplist.get(1)));

		clock.advanceTime(10, TimeUnit.MINUTES);
		tlogs.get(4).setDropoff_datetime(new Date(clock.getCurrentTime()));
		kSession.insert(tlogs.get(4));

		kSession.fireAllRules();

		area1.setLastInserted(new Date(clock.getCurrentTime()));
		area1.setMedianProfitIndex(BigDecimal.valueOf(11));
		assertTrue("secondCheck", toplist.size() == 2 && area1.equals(toplist.get(0)) && area2.equals(toplist.get(1)));
	}

	@Test
	public void medianChangingAfter15Minutes_Test() {
		TaxiLog tlog1 = setUpTaxilog(cells.get(0), cells.get(1), BigDecimal.ONE, BigDecimal.ONE, "1");

		kSession.insert(tlog1);
		kSession.fireAllRules();
		TaxiLog tlog2 = setUpTaxilog(cells.get(0), cells.get(1), BigDecimal.ONE, BigDecimal.TEN, "2");

		clock.advanceTime(1, TimeUnit.MINUTES);
		tlog2.setPickup_datetime(new Date(clock.getCurrentTime()));
		clock.advanceTime(1, TimeUnit.MINUTES);
		tlog2.setDropoff_datetime(new Date(clock.getCurrentTime()));
		kSession.insert(tlog2);
		kSession.insert(new Tick(clock.getCurrentTime()));
		kSession.fireAllRules();

		AreaWithProfit area = new AreaWithProfit(cells.get(0), BigDecimal.valueOf(6.5),
				new Date(clock.getCurrentTime()));

		assertTrue("check1", area.equals(toplist.get(0)) && toplist.size() == 1);

		clock.advanceTime(15, TimeUnit.MINUTES);
		kSession.insert(new Tick(clock.getCurrentTime()));
		kSession.fireAllRules();

		area.setMedianProfitIndex(BigDecimal.valueOf(11));

		assertTrue("check2", area.equals(toplist.get(0)) && toplist.size() == 1);

		clock.advanceTime(61, TimeUnit.SECONDS);
		kSession.insert(new Tick(clock.getCurrentTime()));
		kSession.fireAllRules();

		assertTrue("check3", toplist.isEmpty());
	}
	
	@Test
	public void overFlowingAndAging_test() {
		List<TaxiLog> tlogs = Arrays.asList(
				setUpTaxilog(cells.get(0), cells.get(10), BigDecimal.TEN, BigDecimal.ONE, "1"),
				setUpTaxilog(cells.get(1), cells.get(9), BigDecimal.ONE, BigDecimal.TEN, "2"),
				setUpTaxilog(cells.get(2), cells.get(8), BigDecimal.TEN, BigDecimal.TEN, "3"),
				setUpTaxilog(cells.get(3), cells.get(7), BigDecimal.ONE, BigDecimal.TEN, "4"),
				setUpTaxilog(cells.get(4), cells.get(6), BigDecimal.TEN, BigDecimal.ONE, "5"),
				setUpTaxilog(cells.get(5), cells.get(5), BigDecimal.TEN, BigDecimal.TEN, "6"),
				setUpTaxilog(cells.get(6), cells.get(4), BigDecimal.TEN, BigDecimal.TEN, "7"),
				setUpTaxilog(cells.get(7), cells.get(3), BigDecimal.TEN, BigDecimal.TEN, "8"),
				setUpTaxilog(cells.get(8), cells.get(2), BigDecimal.TEN, BigDecimal.TEN, "9"),
				setUpTaxilog(cells.get(9), cells.get(0), BigDecimal.TEN, BigDecimal.TEN, "10"),
				setUpTaxilog(cells.get(10), cells.get(0), BigDecimal.ONE, BigDecimal.ONE, "11"));

		clock.advanceTime(1, TimeUnit.SECONDS);

		for (int i = 0; i < 11; i++) {
			tlogs.get(i).setDropoff_datetime(new Date(clock.getCurrentTime()));
			tlogs.get(i).setInserted(System.currentTimeMillis());
			kSession.insert(tlogs.get(i));
			clock.advanceTime(1, TimeUnit.MINUTES);
			kSession.fireAllRules();

		}
		
		/*clock.advanceTime(1, TimeUnit.MINUTES);
		kSession.insert(new Tick(clock.getCurrentTime()));
		kSession.fireAllRules();*/
		
		clock.advanceTime(4, TimeUnit.MINUTES);
		kSession.insert(new Tick(clock.getCurrentTime(), System.currentTimeMillis()));
		kSession.fireAllRules();
		
		System.out.println(toplist);
		
		for(int i = 0 ; i < 15; i++){
			clock.advanceTime(1, TimeUnit.MINUTES);
			kSession.insert(new Tick(clock.getCurrentTime(), System.currentTimeMillis()));
			kSession.fireAllRules();
			System.out.println(toplist);
		}
		
		/*QueryResults qres = kSession.getQueryResults( "taxis" );
		TaxiLog tlog = setUpTaxilog(cells.get(0), cells.get(10), BigDecimal.ONE, BigDecimal.ONE, "12");
		tlog.setPickup_datetime(new Date(clock.getCurrentTime()));
		clock.advanceTime(1, TimeUnit.MINUTES);
		tlog.setDropoff_datetime(new Date(clock.getCurrentTime()));
		
		kSession.insert(new Tick(clock.getCurrentTime()));
		kSession.insert(tlog);
		kSession.fireAllRules();
		
		System.out.println(toplist);*/

		/*clock.advanceTime(12, TimeUnit.MINUTES);
		//debug
		
		kSession.insert(new Tick(clock.getCurrentTime()));
		kSession.fireAllRules();
		QueryResults qres = kSession.getQueryResults( "areas" );
		System.out.println(toplist);*/

	}

	private static TaxiLog setUpTaxilog(Cell startCell, Cell endCell, BigDecimal fare, BigDecimal tip,
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
}
