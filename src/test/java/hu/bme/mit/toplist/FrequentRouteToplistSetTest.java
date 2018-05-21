package hu.bme.mit.toplist;

import java.util.Date;

import org.junit.Test;

import hu.bme.mit.entities.Route;
import hu.bme.mit.positioning.Cell;
import static org.junit.Assert.*;
public class FrequentRouteToplistSetTest extends ToplistSetInterfaceTest {

	protected FrequentRoutesToplistSet frToplist;


	@Override
	public void setUp() {
		frToplist = new FrequentRoutesToplistSet();
		super.toplist = frToplist;		 
	}

	@Override
	public Object setUpElementWithDelay(long delay, Cell... cells) {
		
		Route route = new Route(cells[0], cells[1], new Date(), 1);
		route.setDelay(delay);
		return route;
	}

	@Override
	public void addElementToList(Object o) {
		frToplist.add((Route)o);
		
	}

	@Override
	public Object getElementOfToplistSet(int index) {
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
	
	@Test
	public void testRemoveByCell(){
		
	}
	


}
