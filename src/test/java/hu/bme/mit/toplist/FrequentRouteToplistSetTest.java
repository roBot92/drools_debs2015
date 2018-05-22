package hu.bme.mit.toplist;

import java.util.Calendar;
import java.util.Date;

import org.junit.Test;

import hu.bme.mit.entities.Route;
import hu.bme.mit.positioning.Cell;
import static org.junit.Assert.*;
/**
 * A {@link hu.bme.mit.toplist.FrequentRoutesToplistSet} osztályhoz készült egységtesztelõ osztály.
 * @author Rózsavölgyi Botond
 *
 */
public class FrequentRouteToplistSetTest extends ToplistSetInterfaceTest {

	protected FrequentRoutesToplistSet frToplist;


	@Override
	public void setUp() {
		frToplist = new FrequentRoutesToplistSet();
		super.toplist = frToplist;		 
	}

	@Override
	protected Object setUpElementWithDelay(long delay, Cell... cells) {
		
		Route route = new Route(cells[0], cells[1], new Date(), 1);
		route.setDelay(delay);
		return route;
	}

	@Override
	protected void addElementToList(Object o) {
		frToplist.add((Route)o);
		
	}

	@Override
	protected Object getElementOfToplistSet(int index) {
		return frToplist.get(index);
	}

	@Override
	public String toStringOfElement(Object o, boolean withoutDelay) {
		return withoutDelay?((Route) o).toStringWithoutDelay() : ((Route) o).toString(); 
	}

	@Override
	public long getInsertedForDelayOfElement(Cell... cells) {
		Route r = frToplist.getRoute(cells[0], cells[1]);
		return r == null? -1:r.getInsertedForDelay();
	}

	@Override
	public void testClear() {
		Cell startingCell = super.getRandomCell();
		Cell endingCell = super.getRandomCell();		
		frToplist.add((Route)setUpElementWithDelay(1000, startingCell, endingCell));		
		assertTrue("size == 1",frToplist.getSetSize() == 1);		
		frToplist.clear();		
		assertTrue("size == 0",frToplist.getSetSize() == 0 && frToplist.getRoute(startingCell, endingCell) == null);		
	}
	
	/**
	 * Ellenõrzi, hogy null hozzáadása esetén nem kerül semmi a toplistába.
	 */
	@Test
	public void testAddNull(){
		boolean result = frToplist.add(null);
		assertFalse(result);
		assertTrue(frToplist.getSetSize() == 0);
	}
	
	/**
	 * Ellenõrzi, hogy az érvényes hozzáadott elem bekerül a toplistába.
	 */
	@Test
	public void testAddValid(){
		Route r = setUpRoute();
		boolean result = frToplist.add(r);
		assertTrue(result);
		assertTrue(frToplist.get(0) == r && frToplist.getSetSize() == 1);
	}
	/**
	 * Ellenõrzi, hogy a 0 frequency értékû elem bekerül a Map-be, de a toplistába nem.
	 */
	@Test
	public void testAddZeroFrequency(){
		Route r = setUpRoute();
		r.setFrequency(0);
		boolean result = frToplist.add(r);
		assertTrue(result);
		assertTrue( frToplist.getSetSize() == 0 && frToplist.getRoute(r.getPickup_cell(), r.getDropoff_cell()) == r);
	}
	
	/**
	 * Ellenõrzi, hogy a null lastDropoffTime értékû elem bekerül a Map-be, de a toplistába nem.
	 */
	public void testAddZeroDate(){
		Route r = setUpRoute();
		r.setLastDropoffTime(null);
		boolean result = frToplist.add(r);
		assertTrue(result);
		assertTrue( frToplist.getSetSize() == 0 && frToplist.getRoute(r.getPickup_cell(), r.getDropoff_cell()) == r);
	}
	
	/**
	 * Ellenõrzi, hogy a null lastDropoffTime értékû és 0 frequency értékû elem bekerül a Map-be, de a toplistába nem.
	 */
	public void testAddZeroDateAndZeroFrequency(){
		Route r = setUpRoute();
		r.setLastDropoffTime(null);
		r.setFrequency(0);
		boolean result = frToplist.add(r);
		assertTrue(result);
		assertTrue( frToplist.getSetSize() == 0 && frToplist.getRoute(r.getPickup_cell(), r.getDropoff_cell()) == r);
	}
	
	private Route setUpRoute(){
		return setUpRoute(null,null,null,null);
	}
	private Route setUpRoute(Cell startingCell, Cell endingCell, Date dropoffDate, Long frequency){
		if(startingCell == null){
			startingCell = getRandomCell();
		}
		if(endingCell == null){
			endingCell = getRandomCell();
		}
		if(dropoffDate == null){
			dropoffDate = new Date();
		}
		if(frequency == null){
			frequency = Long.valueOf(1);
		}
		return new Route(startingCell, endingCell, dropoffDate, frequency);
	}
	/**
	 * Ellenõrzi az index alapján lekérõ metódust, létezõ, és nem létezõ indexre.
	 */
	@Test
	public void testGet(){
		Route r1 = setUpRoute();
		Route r2 = setUpRoute();
		r1.setFrequency(2);
		
		frToplist.add(r1);
		frToplist.add(r2);
		
		assertTrue("1",frToplist.get(0) == r1);
		assertTrue("2", frToplist.get(1) == r2);
		assertTrue("3", frToplist.get(3) == null);
	}
	
	/**
	 * Ellenõrzi a {@link hu.bme.mit.toplist.FrequentRoutesToplistSet#size()} és a {@link hu.bme.mit.toplist.FrequentRoutesToplistSet#getSetSize()} metódusokat egyforma, és különbözõ értékû eredményre.
	 */
	@Test
	public void testSize(){
		assertTrue("1",frToplist.size() == 0 && frToplist.getSetSize() == frToplist.size());
		
		for(int i = 0; i < 10; i++){
			frToplist.add(setUpRoute());
		}
		assertTrue("2",frToplist.size() == 10 && frToplist.getSetSize() == frToplist.size());
		
		for(int i = 0; i < 10; i++){
			frToplist.add(setUpRoute());
		}
		
		assertTrue("3",frToplist.size() == 10 && frToplist.getSetSize() == 20);
	}
	
	/**
	 * Ellenõrzi az objektum alapján törlést és a {@link hu.bme.mit.toplist.FrequentRoutesToplistSet#contains(Route)} függvényt létezõ és nem létezõ elemre.
	 */
	@Test
	public void testRemoveAndContains(){
		Route r = setUpRoute();
		boolean result = frToplist.remove(r);
		assertFalse("1", result || frToplist.contains(r));
		
		frToplist.add(r);
		assertTrue("2",frToplist.get(0) == r && frToplist.size() == 1 && frToplist.contains(r));
		
		result = frToplist.remove(r);
		assertTrue("3",frToplist.size() == 0 && !frToplist.contains(r) && result);
		
	}
	/**
	 * Ellenõrzi a törlést cella alapján létezõ és nem létezõ elemre.
	 */
	@Test
	public void testRemoveByCell(){
		Route r = setUpRoute();
		Route resultRoute = frToplist.remove(r.getPickup_cell(), r.getDropoff_cell());
		assertFalse("1", resultRoute != null || frToplist.contains(r));
		
		frToplist.add(r);
		assertTrue("2",frToplist.get(0) == r && frToplist.size() == 1 && frToplist.contains(r));
		
		resultRoute = frToplist.remove(r.getPickup_cell(), r.getDropoff_cell());
		assertTrue("3",frToplist.size() == 0 && resultRoute == r);
	}
	/**
	 * A {@link hu.bme.mit.toplist.FrequentRoutesToplistSet#refreshRoute(Cell, Cell, Date, long)} metódust ellenõrzi új Route objektummal.
	 */
	@Test
	public void testRefreshRouteWithNewRoute(){
		Route r = setUpRoute();
		frToplist.refreshRoute(r.getPickup_cell(), r.getDropoff_cell(), r.getLastDropoffTime(), r.getFrequency());
		
		assertTrue(frToplist.size() == 1 && frToplist.get(0).equals(r));
	}
	
	/**
	 * A {@link hu.bme.mit.toplist.FrequentRoutesToplistSet#refreshRoute(Cell, Cell, Date, long)} metódust ellenõrzi már a listában lévõ Route objektummal.
	 */
	@Test
	public void testRefreshRouteWithExistingRoute(){
		Route r = setUpRoute();
		frToplist.refreshRoute(r.getPickup_cell(), r.getDropoff_cell(), r.getLastDropoffTime(), r.getFrequency());
		
		Date newDropoffTime = new Date();
		long newFrequency = 5;
		
		frToplist.refreshRoute(r.getPickup_cell(), r.getDropoff_cell(), newDropoffTime, newFrequency);
		
		r.setLastDropoffTime(newDropoffTime);
		r.setFrequency(newFrequency);
		assertTrue(frToplist.size() == 1 && frToplist.get(0).equals(r));
	}
	/**
	 * A {@link hu.bme.mit.toplist.FrequentRoutesToplistSet#refreshRoute(Cell, Cell, Date, long)} metódust ellenõrzi új Route objektummal, érvénytelen adatokkal (frequency értéke 0 vagy a beillesztési idõ null)
	 */
	@Test
	public void testRefreshRouteWithNewRouteInvalid(){
		Route r = setUpRoute();
		r.setLastDropoffTime(null);
		
		frToplist.refreshRoute(r.getPickup_cell(), r.getDropoff_cell(), r.getLastDropoffTime(), r.getFrequency());
		
		assertTrue("1", frToplist.size() == 0 && frToplist.getRoute(r.getPickup_cell(), r.getDropoff_cell()).equals(r));
		
		r.setLastDropoffTime(new Date());
		r.setFrequency(0);
		frToplist.refreshRoute(r.getPickup_cell(), r.getDropoff_cell(), r.getLastDropoffTime(), r.getFrequency());
		assertTrue("2", frToplist.size() == 0 && frToplist.getRoute(r.getPickup_cell(), r.getDropoff_cell()).equals(r));
		
		r.setLastDropoffTime(null);
		
		assertTrue("3", frToplist.size() == 0 && frToplist.getRoute(r.getPickup_cell(), r.getDropoff_cell()).equals(r));
	}
	/**
	 * A {@link hu.bme.mit.toplist.FrequentRoutesToplistSet#refreshRoute(Cell, Cell, Date, long)}metódust ellenõrzi már létezõ Route objektummal, érvénytelen adatokkal (frequency értéke 0 vagy a beillesztési idõ null)
	 */
	@Test
	public void testRefreshRouteWithExistingRouteInvalidData(){
		Route r = setUpRoute();
		frToplist.add(r);
		assertTrue("1", frToplist.size() == 1 && frToplist.get(0).equals(r));
		
		r.setLastDropoffTime(null);
		frToplist.refreshRoute(r.getPickup_cell(), r.getDropoff_cell(), r.getLastDropoffTime(), r.getFrequency());
		
		assertTrue("2", frToplist.size() == 0 && frToplist.getRoute(r.getPickup_cell(), r.getDropoff_cell()).equals(r));
		r.setLastDropoffTime(new Date());
		r.setFrequency(0);
		
		frToplist.refreshRoute(r.getPickup_cell(), r.getDropoff_cell(), r.getLastDropoffTime(), r.getFrequency());
		assertTrue("3", frToplist.size() == 0 && frToplist.getRoute(r.getPickup_cell(), r.getDropoff_cell()).equals(r));
		
		r.setLastDropoffTime(null);
		frToplist.refreshRoute(r.getPickup_cell(), r.getDropoff_cell(), r.getLastDropoffTime(), r.getFrequency());
		
		assertTrue("4", frToplist.size() == 0 && frToplist.getRoute(r.getPickup_cell(), r.getDropoff_cell()).equals(r));
		
		
	}
	
	/**
	 * A {@link hu.bme.mit.toplist.FrequentRoutesToplistSet#decreaseRouteFrequency(Cell, Cell)} metódust ellenõrzi nem létezõ Route objektummal.
	 */
	@Test
	public void testDecreaseRouteFrequencyNotExistingRoute(){
		Cell[] cells= new Cell[]{getRandomCell(), getRandomCell()};
		frToplist.decreaseRouteFrequency(cells[0], cells[1]);
		assertTrue(frToplist.getSetSize() == 0 && frToplist.getRoute(cells[0], cells[1]) == null);
	}
	
	/**
	 * A {@link hu.bme.mit.toplist.FrequentRoutesToplistSet#decreaseRouteFrequency(Cell, Cell)} metódust ellenõrzi létezõ Route objektummal.
	 */
	@Test
	public void testDecreaseRouteFrequencyExistingRoute(){
		Route r = setUpRoute();
		r.setFrequency(2);
		frToplist.add(r);
		assertTrue("1", frToplist.size() == 1 && frToplist.get(0).equals(r));
		
		frToplist.decreaseRouteFrequency(r.getPickup_cell(), r.getDropoff_cell());
		r.decreaseFrequency();
		assertTrue("2", frToplist.size() == 1 && frToplist.get(0).equals(r));
		
		frToplist.decreaseRouteFrequency(r.getPickup_cell(), r.getDropoff_cell());
		
		assertTrue("3", frToplist.size() == 0 && frToplist.getRoute(r.getPickup_cell(), r.getDropoff_cell()) == r);
		
	}
	
	/**
	 * A {@link hu.bme.mit.toplist.FrequentRoutesToplistSet#contains(Route)} metódust ellenõrzi null értékkel.
	 */
	@Test
	public void testContainsNull(){
		assertFalse("1", frToplist.contains(null));
		Route r = setUpRoute();
		frToplist.add(r);
		assertFalse("2", frToplist.contains(null));
		
		
	}
	
	/**
	 * Az {@link hu.bme.mit.toplist.FrequentRoutesToplistSet#increaseRouteFrequency(Cell, Cell, Date, long)} metódust ellenõrzi létezõ Route objektummal.
	 */
	@Test
	public void testIncreaseFrequencyWithExistingRoute(){
		Route r = setUpRoute();
		frToplist.add(r);
		
		long frequency = r.getFrequency();
		Date date = new Date();
		frToplist.increaseRouteFrequency(r.getPickup_cell(), r.getDropoff_cell(), date, 100);
		
		assertTrue("1", r.getFrequency() == frequency+1 && r.getLastDropoffTime() == date && r.getInsertedForDelay() == 100);
	}
	
	/**
	 * Az {@link hu.bme.mit.toplist.FrequentRoutesToplistSet#increaseRouteFrequency(Cell, Cell, Date, long)} metódust ellenõrzi nem létezõ route objektummal.
	 */
	@Test
	public void testIncreaseFrequencyWithoutExistingRoute(){
		Route r = setUpRoute();
		r.setFrequency(1);
		frToplist.increaseRouteFrequency(r.getPickup_cell(), r.getDropoff_cell(), r.getLastDropoffTime(), 100);
		
		assertTrue("1", frToplist.size() == 1 && frToplist.get(0).equals(r));
	}

	@Override
	public void testRefreshDelayTimes() {
		frToplist.refreshDelayTimes();
		assertTrue("1", frToplist.size() == 0);
		
		
		Route r1 = setUpRoute();
		r1.setDelay(-1);
		Route r2 = setUpRoute();
		r2.setDelay(Long.MAX_VALUE);
		
		long insertTime = System.currentTimeMillis();
		
		r1.setInsertedForDelay(insertTime);
		frToplist.add(r1);
		frToplist.add(r2);
		
		frToplist.refreshDelayTimes();
		
		long delay = r1.getDelay();
		
		assertTrue("1",r2.getDelay() == Long.MAX_VALUE);
		assertTrue("2", delay >=0 && delay <= System.currentTimeMillis() - insertTime);
		
	}
	
	/**
	 * Ellenõrzi a frequency mezõ alapján a rendezést két elem esetén.
	 */
	@Test
	public void testOrderingByFrequencyWithTwoElements(){
		Route r1 = setUpRoute();
		Route r2 = setUpRoute();
		
		r1.setFrequency(2);
		r2.setFrequency(3);
		
		frToplist.add(r1);
		assertTrue("1",frToplist.size() == 1 && frToplist.get(0) == r1);
		
		frToplist.add(r2);
		assertTrue("2",frToplist.size() == 2 && frToplist.get(0) == r2 && frToplist.get(1) == r1);
		
		frToplist.increaseRouteFrequency(r1.getPickup_cell(), r1.getDropoff_cell(), r1.getLastDropoffTime(), -1);
		frToplist.increaseRouteFrequency(r1.getPickup_cell(), r1.getDropoff_cell(), r1.getLastDropoffTime(), -1);
		assertTrue("3",frToplist.size() == 2 && frToplist.get(0) == r1 && frToplist.get(1) == r2);
		frToplist.remove(r1);
		assertTrue("4",frToplist.size() == 1 && frToplist.get(0) == r2);		
	}
	/**
	 * Ellenõrzi a frequency mezõ alapján a rendezést 20 elem esetén.
	 */
	@Test
	public void testOrderingByFrequencyWith20Elements(){
		
		for(int i = 0; i < 20; i++){
			Route r = setUpRoute();
			r.setFrequency((long)(Math.random()*1000l+1));
			frToplist.add(r);
		}
		assertTrue("1",frToplist.size() == FrequentRoutesToplistSet.MAX_ELEMENT_NUMBER && frToplist.getSetSize() == 20);
		
		long frequency = Long.MAX_VALUE;
		for(int i = 0; i < 20; i++){
			assertTrue(i+". iteration", frequency >= frToplist.get(i).getFrequency());
			frequency = frToplist.get(i).getFrequency();
		}
		
		Route routeOfMinFreq = frToplist.get(19);
		long diffBetweenMinAndMax = frToplist.get(0).getFrequency() - routeOfMinFreq.getFrequency();
		
		for(int i = 0; i< diffBetweenMinAndMax-1;i++){
			frToplist.increaseRouteFrequency(routeOfMinFreq.getPickup_cell(), routeOfMinFreq.getDropoff_cell(), routeOfMinFreq.getLastDropoffTime(),-1);
		}
		assertFalse("2",frToplist.get(0) == routeOfMinFreq);
		frToplist.increaseRouteFrequency(routeOfMinFreq.getPickup_cell(), routeOfMinFreq.getDropoff_cell(), routeOfMinFreq.getLastDropoffTime(),-1);
		frToplist.increaseRouteFrequency(routeOfMinFreq.getPickup_cell(), routeOfMinFreq.getDropoff_cell(), routeOfMinFreq.getLastDropoffTime(),-1);
		assertTrue("3",frToplist.get(0) == routeOfMinFreq);
	}
	/**
	 * Ellenõrzi a lastDropoffTime mezõ szerinti rendezést két elem esetén.
	 */
	@Test
	public void testOrderingByLastDropoffTimeWithTwoElements(){
		Route r1 = setUpRoute();
		Route r2 = setUpRoute();
		Calendar calendar = Calendar.getInstance();
		r1.setLastDropoffTime(calendar.getTime());
		calendar.add(Calendar.SECOND, 1);
		r2.setLastDropoffTime(calendar.getTime());
		
		
		frToplist.add(r1);
		assertTrue("1",frToplist.size() == 1 && frToplist.get(0) == r1);
		
		frToplist.add(r2);
		assertTrue("2",frToplist.size() == 2 && frToplist.get(0) == r2 && frToplist.get(1) == r1);
		
		
		calendar.add(Calendar.SECOND, 1);
		frToplist.remove(r1);
		r1.setLastDropoffTime(calendar.getTime());
		frToplist.add(r1);
		
		assertTrue("3",frToplist.size() == 2 && frToplist.get(0) == r1 && frToplist.get(1) == r2);
		frToplist.remove(r1);
		assertTrue("4",frToplist.size() == 1 && frToplist.get(0) == r2);		
	}
	
	/**
	 * Ellenõrzi a lastDropoffTime mezõ szerinti rendezést 20 elem esetén.
	 */
	@Test
	public void testOrderingByLastDropoffTimeWith20Elements(){
		Calendar calendar = Calendar.getInstance();
		for(int i = 0; i < 20; i++){
			Route r = setUpRoute();
			r.setLastDropoffTime(calendar.getTime());
			frToplist.add(r);
			
			calendar.add(Calendar.SECOND, (int) (Math.random()*100l-50));
		}
		assertTrue("1",frToplist.size() == FrequentRoutesToplistSet.MAX_ELEMENT_NUMBER && frToplist.getSetSize() == 20);
		
		Date date = frToplist.get(0).getLastDropoffTime();
		for(int i = 0; i < 20; i++){
			assertTrue(i+". iteration", date.getTime() >= frToplist.get(i).getLastDropoffTime().getTime());
			date = frToplist.get(i).getLastDropoffTime();
		}
		
		Route routeOfMinDate = frToplist.get(19);
		long diffBetweenMinAndMaxInSec = frToplist.get(0).getLastDropoffTime().getTime() - routeOfMinDate.getLastDropoffTime().getTime();
		
		calendar.setTime(routeOfMinDate.getLastDropoffTime());
		for(int i = 0; i< diffBetweenMinAndMaxInSec-1000;i+=1000){
			calendar.add(Calendar.SECOND, 1);
			frToplist.remove(routeOfMinDate);
			routeOfMinDate.setLastDropoffTime(calendar.getTime());
			frToplist.add(routeOfMinDate);
		}
		assertFalse("2",frToplist.get(0) == routeOfMinDate);
		calendar.add(Calendar.SECOND, 1);
		frToplist.remove(routeOfMinDate);
		routeOfMinDate.setLastDropoffTime(new Date(calendar.getTimeInMillis()+1000));
		frToplist.add(routeOfMinDate);
		
		assertTrue("3",frToplist.get(0) == routeOfMinDate);	
	}
	
	/**
	 * Ellenõrzi, hogy a frequency és lastDropoffTime különbözõség esetén a frequency az elsõdleges.
	 */
	@Test
	public void testOrderingByFrequencyAndLastDropoffTimeWithTwoElements(){
		Route r1 = setUpRoute();
		Route r2 = setUpRoute();
		r1.setFrequency(2);
		r2.setFrequency(2);
		
		Calendar calendar = Calendar.getInstance();
		r1.setLastDropoffTime(calendar.getTime());
		calendar.add(Calendar.SECOND, 1);
		r2.setLastDropoffTime(calendar.getTime());
		
		frToplist.add(r1);
		frToplist.add(r2);
		
		assertTrue("1",frToplist.get(0) == r2 && frToplist.get(1) == r1);
		
		frToplist.increaseRouteFrequency(r1.getPickup_cell(), r1.getDropoff_cell(), r1.getLastDropoffTime(), -1);
		assertTrue("2",frToplist.get(0) == r1 && frToplist.get(1) == r2);
		
		frToplist.increaseRouteFrequency(r2.getPickup_cell(), r2.getDropoff_cell(), r2.getLastDropoffTime(), -1);
		assertTrue("3",frToplist.get(0) == r2 && frToplist.get(1) == r1);
		
		
		
	}
}
