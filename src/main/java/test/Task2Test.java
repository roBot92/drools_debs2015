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
public class Task2Test {

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
		
		TaxiLog tlog1 = new TaxiLog();
		tlog1.setFare_amount(BigDecimal.TEN);
		tlog1.setTip_amount(BigDecimal.ONE);
		tlog1.setPickup_cell(cells.get(0));
		tlog1.setDropoff_cell(cells.get(0));
		tlog1.setPickup_datetime(calendar);
		increaseCalendar(calendar, 60);
		tlog1.setDropoff_datetime(calendar);
		
	}
	
	private void increaseCalendar(Calendar calendar, long sec){
		calendar.setTimeInMillis(calendar.getTimeInMillis()+sec*1000);
	}

}
