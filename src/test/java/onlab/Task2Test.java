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
import onlab.positioning.Cell;
import onlab.utility.ProfitableAreaToplistSet;

@SuppressWarnings("restriction")
public class Task2Test {

	private ProfitableAreaToplistSet<AreaWithProfit> toplist;
	private KieSession kSession;
	private SessionPseudoClock clock;
	private static List<Cell> cells;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		// calendar = Calendar.getInstance();
		cells = Arrays.asList(new Cell(1, 1), new Cell(1, 2), new Cell(2, 1), new Cell(2, 2), new Cell(3, 1),
				new Cell(3, 2));

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
		kSession.fireAllRules();

		assertTrue("check1", area.equals(toplist.get(0)) && toplist.size() == 1);

		clock.advanceTime(60, TimeUnit.SECONDS);
		tlog2.setDropoff_datetime(new Date(clock.getCurrentTime()));
		area.setLastInserted(new Date(clock.getCurrentTime()));
		area.setMedianProfit(BigDecimal.valueOf(6.5));

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

	private static TaxiLog setUpTaxilog(Cell startCell, Cell endCell, BigDecimal fare, BigDecimal tip,
			String hack_License) {
		TaxiLog tlog = new TaxiLog();
		Calendar zeroCalendar = Calendar.getInstance();
		tlog.setPickup_cell(startCell);
		tlog.setDropoff_cell(endCell);

		zeroCalendar.setTimeInMillis(0);
		tlog.setDropoff_datetime(zeroCalendar.getTime());

		tlog.setFare_amount(fare);
		tlog.setTip_amount(tip);

		tlog.setHack_license(hack_License);
		return tlog;
	}
}
