package test;

import static org.junit.Assert.*;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.Set;

import org.drools.core.time.SessionPseudoClock;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.kie.api.KieServices;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.KieSessionConfiguration;
import org.kie.api.runtime.conf.ClockTypeOption;

import onlab.event.TaxiLog;
import onlab.positioning.AreaWithProfit;
import onlab.positioning.Cell;
import onlab.utility.ProfitableAreaToplistSet;

@SuppressWarnings("restriction")
public class Task1Test {

	private Set<AreaWithProfit> toplist;
	private KieSession kSession;
	private SessionPseudoClock clock;
	private List<Cell> cells;
	private Calendar calendar;
	

	@Before
	public void setUp() throws Exception {
		toplist = new ProfitableAreaToplistSet<AreaWithProfit>();
		KieServices ks = KieServices.Factory.get();
		KieContainer kContainer = ks.getKieClasspathContainer();
		KieSessionConfiguration config = ks.newKieSessionConfiguration();

		config.setOption(ClockTypeOption.get("pseudo"));
		kSession = kContainer.newKieSession("ksession-rules", config);
		clock = kSession.getSessionClock();
		kSession.setGlobal("mostProfitableAreas", toplist);
		calendar = Calendar.getInstance();
		calendar.setTimeInMillis(0);
		
		cells = Arrays.asList(new Cell(1,1), new Cell(1,2), new Cell(1,3), new Cell(2,1), new Cell(2,2), new Cell(2,3));

	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void test() {
		
		TaxiLog tlog1 = setUpTaxilog(cells.get(0), cells.get(1), calendar);
		increaseCalendar(calendar, 60);
		TaxiLog tlog2 = setUpTaxilog(cells.get(0), cells.get(1), calendar);
		
		
	}
	
	private void increaseCalendar(Calendar calendar, long sec){
		calendar.setTimeInMillis(calendar.getTimeInMillis()+sec*1000);
	}
	
	private TaxiLog setUpTaxilog(Cell startCell, Cell endCell, Calendar cal){
		TaxiLog tlog = new TaxiLog();
		tlog.setPickup_cell(startCell);
		tlog.setDropoff_cell(endCell);
		tlog.setDropoff_datetime(cal);
		return tlog;
	}

}
