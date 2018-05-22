package hu.bme.mit.test.drools;

import java.util.Calendar;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.drools.core.time.SessionPseudoClock;
import org.junit.Before;
import org.kie.api.KieServices;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.KieSessionConfiguration;
import org.kie.api.runtime.conf.ClockTypeOption;

import hu.bme.mit.entities.TaxiLog;
import hu.bme.mit.entities.Tick;
import hu.bme.mit.test.AbstractTask2Test;

public class DroolsTask2Test extends AbstractTask2Test{

	private KieSession kSession;
	private SessionPseudoClock clock;
	

	@Before
	public void setUp() throws Exception {
		super.setUp();
		KieServices ks = KieServices.Factory.get();
		KieContainer kContainer = ks.getKieClasspathContainer();
		KieSessionConfiguration config = ks.newKieSessionConfiguration();

		config.setOption(ClockTypeOption.get("pseudo"));
		kSession = kContainer.newKieSession("ksession-rules", config);
		clock = kSession.getSessionClock();

		kSession.setGlobal("mostProfitableAreas", toplist);

	}


	@Override
	protected void insertTaxiLogs(List<TaxiLog> taxiLogs) {
		for(TaxiLog tlog:taxiLogs){
			kSession.insert(tlog);
		}		
	}


	@Override
	protected void rollPseudoClock(long time) {
		clock.advanceTime(time, TimeUnit.MILLISECONDS);
		calendar.add(Calendar.MILLISECOND, (int)time);	
		kSession.insert(new Tick(clock.getCurrentTime()));
	}


	@Override
	protected void fireRules() {
		kSession.fireAllRules();
	}


	
	
	
	
}
