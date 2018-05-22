package hu.bme.mit.toplist;

import java.util.Calendar;
import java.util.Date;

import org.junit.Test;

import hu.bme.mit.entities.Route;
import hu.bme.mit.positioning.Cell;
import static org.junit.Assert.*;
/**
 * A {@link hu.bme.mit.toplist.FrequentRoutesToplistSet} oszt�lyhoz k�sz�lt egys�gtesztel� oszt�ly.
 * @author R�zsav�lgyi Botond
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
	 * Ellen�rzi, hogy null hozz�ad�sa eset�n nem ker�l semmi a toplist�ba.
	 */
	@Test
	public void testAddNull(){
		boolean result = frToplist.add(null);
		assertFalse(result);
		assertTrue(frToplist.getSetSize() == 0);
	}
	
	/**
	 * Ellen�rzi, hogy az �rv�nyes hozz�adott elem beker�l a toplist�ba.
	 */
	@Test
	public void testAddValid(){
		Route r = setUpRoute();
		boolean result = frToplist.add(r);
		assertTrue(result);
		assertTrue(frToplist.get(0) == r && frToplist.getSetSize() == 1);
	}
	/**
	 * Ellen�rzi, hogy a 0 frequency �rt�k� elem beker�l a Map-be, de a toplist�ba nem.
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
	 * Ellen�rzi, hogy a null lastDropoffTime �rt�k� elem beker�l a Map-be, de a toplist�ba nem.
	 */
	public void testAddZeroDate(){
		Route r = setUpRoute();
		r.setLastDropoffTime(null);
		boolean result = frToplist.add(r);
		assertTrue(result);
		assertTrue( frToplist.getSetSize() == 0 && frToplist.getRoute(r.getPickup_cell(), r.getDropoff_cell()) == r);
	}
	
	/**
	 * Ellen�rzi, hogy a null lastDropoffTime �rt�k� �s 0 frequency �rt�k� elem beker�l a Map-be, de a toplist�ba nem.
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
	 * Ellen�rzi az index alapj�n lek�r� met�dust, l�tez�, �s nem l�tez� indexre.
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
	 * Ellen�rzi a {@link hu.bme.mit.toplist.FrequentRoutesToplistSet#size()} �s a {@link hu.bme.mit.toplist.FrequentRoutesToplistSet#getSetSize()} met�dusokat egyforma, �s k�l�nb�z� �rt�k� eredm�nyre.
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
	 * Ellen�rzi az objektum alapj�n t�rl�st �s a {@link hu.bme.mit.toplist.FrequentRoutesToplistSet#contains(Route)} f�ggv�nyt l�tez� �s nem l�tez� elemre.
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
	 * Ellen�rzi a t�rl�st cella alapj�n l�tez� �s nem l�tez� elemre.
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
	 * A {@link hu.bme.mit.toplist.FrequentRoutesToplistSet#refreshRoute(Cell, Cell, Date, long)} met�dust ellen�rzi �j Route objektummal.
	 */
	@Test
	public void testRefreshRouteWithNewRoute(){
		Route r = setUpRoute();
		frToplist.refreshRoute(r.getPickup_cell(), r.getDropoff_cell(), r.getLastDropoffTime(), r.getFrequency());
		
		assertTrue(frToplist.size() == 1 && frToplist.get(0).equals(r));
	}
	
	/**
	 * A {@link hu.bme.mit.toplist.FrequentRoutesToplistSet#refreshRoute(Cell, Cell, Date, long)} met�dust ellen�rzi m�r a list�ban l�v� Route objektummal.
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
	 * A {@link hu.bme.mit.toplist.FrequentRoutesToplistSet#refreshRoute(Cell, Cell, Date, long)} met�dust ellen�rzi �j Route objektummal, �rv�nytelen adatokkal (frequency �rt�ke 0 vagy a beilleszt�si id� null)
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
	 * A {@link hu.bme.mit.toplist.FrequentRoutesToplistSet#refreshRoute(Cell, Cell, Date, long)}met�dust ellen�rzi m�r l�tez� Route objektummal, �rv�nytelen adatokkal (frequency �rt�ke 0 vagy a beilleszt�si id� null)
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
	 * A {@link hu.bme.mit.toplist.FrequentRoutesToplistSet#decreaseRouteFrequency(Cell, Cell)} met�dust ellen�rzi nem l�tez� Route objektummal.
	 */
	@Test
	public void testDecreaseRouteFrequencyNotExistingRoute(){
		Cell[] cells= new Cell[]{getRandomCell(), getRandomCell()};
		frToplist.decreaseRouteFrequency(cells[0], cells[1]);
		assertTrue(frToplist.getSetSize() == 0 && frToplist.getRoute(cells[0], cells[1]) == null);
	}
	
	/**
	 * A {@link hu.bme.mit.toplist.FrequentRoutesToplistSet#decreaseRouteFrequency(Cell, Cell)} met�dust ellen�rzi l�tez� Route objektummal.
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
	 * A {@link hu.bme.mit.toplist.FrequentRoutesToplistSet#contains(Route)} met�dust ellen�rzi null �rt�kkel.
	 */
	@Test
	public void testContainsNull(){
		assertFalse("1", frToplist.contains(null));
		Route r = setUpRoute();
		frToplist.add(r);
		assertFalse("2", frToplist.contains(null));
		
		
	}
	
	/**
	 * Az {@link hu.bme.mit.toplist.FrequentRoutesToplistSet#increaseRouteFrequency(Cell, Cell, Date, long)} met�dust ellen�rzi l�tez� Route objektummal.
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
	 * Az {@link hu.bme.mit.toplist.FrequentRoutesToplistSet#increaseRouteFrequency(Cell, Cell, Date, long)} met�dust ellen�rzi nem l�tez� route objektummal.
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
	 * Ellen�rzi a frequency mez� alapj�n a rendez�st k�t elem eset�n.
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
	 * Ellen�rzi a frequency mez� alapj�n a rendez�st 20 elem eset�n.
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
	 * Ellen�rzi a lastDropoffTime mez� szerinti rendez�st k�t elem eset�n.
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
	 * Ellen�rzi a lastDropoffTime mez� szerinti rendez�st 20 elem eset�n.
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
	 * Ellen�rzi, hogy a frequency �s lastDropoffTime k�l�nb�z�s�g eset�n a frequency az els�dleges.
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
