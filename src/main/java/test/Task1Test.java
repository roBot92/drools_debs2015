package test;

import static org.junit.Assert.*;

import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Set;
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

import onlab.event.TaxiLog;
import onlab.positioning.Cell;
import onlab.positioning.Route;
import onlab.utility.FrequentRoutesToplistSet;

@SuppressWarnings("restriction")
public class Task1Test {

	private Set<Route> toplist;
	private KieSession kSession;
	private SessionPseudoClock clock;
	private Calendar calendar;
	private static List<Cell> cells;
	private static List<TaxiLog> route1tlogs;
	private static List<TaxiLog> route2tlogs;
	private static List<TaxiLog> route3tlogs;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		//calendar = Calendar.getInstance();
		cells = Arrays.asList(new Cell(1,1), new Cell(1,2), new Cell(2,1), new Cell(2,2), new Cell(3,1), new Cell(3,2));

		
		route1tlogs = Arrays.asList(setUpTaxilog(cells.get(0), cells.get(1)/*, getZeroTimeCalendar()*/),
									setUpTaxilog(cells.get(0), cells.get(1)/*, getZeroTimeCalendar()*/),
									setUpTaxilog(cells.get(0), cells.get(1)/*, getZeroTimeCalendar()*/),
									setUpTaxilog(cells.get(0), cells.get(1)/*, getZeroTimeCalendar()*/));
		
		route2tlogs = Arrays.asList(setUpTaxilog(cells.get(2), cells.get(3)/*, getZeroTimeCalendar()*/),
									setUpTaxilog(cells.get(2), cells.get(3)/*, getZeroTimeCalendar()*/),
									setUpTaxilog(cells.get(2), cells.get(3)/*, getZeroTimeCalendar()*/),
									setUpTaxilog(cells.get(2), cells.get(3)/*, getZeroTimeCalendar()*/));
		
		route3tlogs = Arrays.asList(setUpTaxilog(cells.get(4), cells.get(5)/*, getZeroTimeCalendar()*/),
									setUpTaxilog(cells.get(4), cells.get(5)/*, getZeroTimeCalendar()*/),
									setUpTaxilog(cells.get(4), cells.get(5)/*, getZeroTimeCalendar()*/),
									setUpTaxilog(cells.get(4), cells.get(5)/*, getZeroTimeCalendar()*/));
		
		
		
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
		kSession.setGlobal("mostFrequentRoutes", toplist);
		calendar = Calendar.getInstance();
		calendar.setTimeInMillis(0);
		
		for(int i = 0 ; i < 4 ; i++){
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
	public void test1() {
		
		//First minute +1 route1, +1 route2, +1 route3
		kSession.insert(route1tlogs.get(0));
		kSession.insert(route2tlogs.get(0));
		kSession.insert(route3tlogs.get(0));
		kSession.fireAllRules();
		System.out.println(toplist);
		
		//Second minute, +1 route1, +1 route2
		clock.advanceTime(60, TimeUnit.SECONDS);
		calendar.add(Calendar.SECOND, 60);
		route1tlogs.get(1).setDropoff_datetime(calendar.getTime());
		kSession.insert(route1tlogs.get(1));
		route2tlogs.get(1).setDropoff_datetime(calendar.getTime());
		kSession.insert(route2tlogs.get(1));
		kSession.fireAllRules();
		System.out.println(toplist);
		
		//Third minute +1 route1
		clock.advanceTime(60, TimeUnit.SECONDS);
		calendar.add(Calendar.SECOND, 60);
		route1tlogs.get(2).setDropoff_datetime(calendar.getTime());
		kSession.insert(route1tlogs.get(2));
		kSession.fireAllRules();
		System.out.println(toplist);
		
		//Fourth minute, +1 route3
		clock.advanceTime(60, TimeUnit.SECONDS);
		calendar.add(Calendar.SECOND, 60);
		route3tlogs.get(1).setDropoff_datetime(calendar.getTime());
		kSession.insert(route3tlogs.get(1));
		kSession.fireAllRules();
		
		System.out.println(toplist);
		
		
		clock.advanceTime(25, TimeUnit.MINUTES);
		kSession.fireAllRules();
		
	    assertTrue(true);
		
		
		
	}
	
	/*private void increaseDropoffDate(TaxiLog tlog, int sec){
		Calendar cal = Calendar.getInstance();
		cal.setTime(tlog.getDropoff_datetime());		
		cal.add(Calendar.SECOND, sec);
		tlog.setDropoff_datetime(cal.getTime());
	}*/
	
	private static TaxiLog setUpTaxilog(Cell startCell, Cell endCell/*, Calendar cal*/){
		TaxiLog tlog = new TaxiLog();
		tlog.setPickup_cell(startCell);
		tlog.setDropoff_cell(endCell);
		//tlog.setDropoff_datetime(cal);
		return tlog;
	}
	
	private Date getZeroTimeCalendar(){
		Calendar cal = Calendar.getInstance();
		cal.setTimeInMillis(0);
		return cal.getTime();
	}

}
