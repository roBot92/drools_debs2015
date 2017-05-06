package onlab;

import static org.junit.Assert.*;

import java.util.Arrays;
import java.util.List;

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

import onlab.positioning.Cell;
import onlab.positioning.Route;
import onlab.utility.FrequentRoutesToplistSet;

@SuppressWarnings("restriction")
public class Task2Test { 

	private FrequentRoutesToplistSet<Route> toplist;
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

	}

		@Test
		public void sample_Test(){
			assertTrue(true);
		}


}
