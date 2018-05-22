package hu.bme.mit.toplist;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

import org.junit.Before;
import org.junit.Test;

import hu.bme.mit.positioning.Cell;
import hu.bme.mit.toplist.ToplistSetInterface;

public abstract class ToplistSetInterfaceTest {

	protected ToplistSetInterface toplist;
	

	@Before
	public abstract void setUp();

	@Test
	public final void testGetAverageDelayWithEmptySet() {
		assertTrue("Average  0", toplist.getAverageDelay() == 0);
	}

	/**
	 * Átlagos késleltetés lekérése egy elem esetén.
	 */
	@Test
	public void testGetAverageDelayWithOneElement() {
		long expectedDelay = (long) (Math.random() * 1000f);
		Object element = setUpElementWithDelay(expectedDelay, getRandomCell(), getRandomCell());
		addElementToList(element);
		assertTrue("Average  " + expectedDelay, toplist.getAverageDelay() == expectedDelay);
	}

	/**
	 * Átlagos késleltetés 5 elem esetén, tehát félig telített listával.
	 */
	@Test
	public void testGetAverageDelayWithFiveElements() {
		long[] elements = new long[5];
		for (int i = 0; i < elements.length; i++) {
			elements[i] = (long) (Math.random() * 1000f);
			Object o = setUpElementWithDelay(elements[i], getRandomCell(), getRandomCell());
			addElementToList(o);
		}
		long expectedDelay = getAverage(elements);
		assertTrue("Average  " + expectedDelay, toplist.getAverageDelay() == expectedDelay);

	}

	/**
	 * Átlagos késleltetés lekérése 20 elembõl, vagyis a kétszer annyi elembõl,
	 * mint amennyit megjelenítünk.
	 */
	@Test
	public void testGetAverageDelayWithTwentyElements() {
		long[] elements = new long[20];
		for (int i = 0; i < elements.length; i++) {
			elements[i] = (long) (Math.random() * 1000f);
			Object o = setUpElementWithDelay(elements[i], getRandomCell(), getRandomCell());
			addElementToList(o);
		}
		long expectedDelay = getAverage(elements);
		assertTrue("Average  " + expectedDelay, toplist.getAverageDelay() == expectedDelay);

	}

	/**
	 * Adott elemet a toplistába helyezõ absztrakt függvény.
	 * 
	 * @param o
	 *            - a toplista elem
	 */
	protected abstract void addElementToList(Object o);

	/**
	 * Adott toplista elemet paraméterben kapott késleltetéssel létrehozó
	 * metódus.
	 * 
	 * @param delay
	 *            - a késleltetés
	 * @param cells
	 *            - az elemet azonosító cellák.
	 * @return - a létrehozott listaelem
	 */
	protected abstract Object setUpElementWithDelay(long delay, Cell... cells);

	private long getAverage(long[] elements) {
		long sum = 0;
		for (long element : elements) {
			sum += element;
		}

		return elements.length == 0 ? 0 : sum / elements.length;
	}

	protected Cell getRandomCell() {
		return new Cell((int) (Math.random() * 1000f), (int) (Math.random() * 1000f));
	}

	/**
	 * Üres toplista esetén teszteli, hogy a minimális késleltetés elkérésére a Long.MAX_VALUE-t kapjuk.
	 */
	@Test
	public void testGetMinDelayWithEmptySet() {
		assertTrue("Min delay  Long.MAX", toplist.getMinDelay() == Long.MAX_VALUE);

	}
	
	/**
	 * A minimális késleltetés elkérését ellenõrzi egy toplista elem esetén.
	 */
	@Test
	public void testGetMinDelayWithOneElement() {
		long expectedDelay = (long) (Math.random() * 1000f);
		Object element = setUpElementWithDelay(expectedDelay, getRandomCell(), getRandomCell());
		addElementToList(element);
		assertTrue("Minimum  " + expectedDelay, toplist.getMinDelay() == expectedDelay);

	}
	/**
	 * A minimális késleltetés elkérését ellenõrzi félig feltöltött (vagyis 5 elem) esetén.
	 */
	@Test
	public void testGetMinDelayWithFiveElements() {
		long[] elements = new long[5];
		for (int i = 0; i < elements.length; i++) {
			elements[i] = (long) (Math.random() * 1000f);
			Object o = setUpElementWithDelay(elements[i], getRandomCell(), getRandomCell());
			addElementToList(o);
		}
		long expectedDelay = getMin(elements);
		assertTrue("Minimum  " + expectedDelay, toplist.getMinDelay() == expectedDelay);

	}
	/**
	 *  A minimális késleltetés elkérését ellenõrzi 20 elem (vagyis kétszer annyi elem, mint amit megjelenítünk) esetén.
	 */
	@Test
	public void testGetMinDelayWithTwentyElements() {
		long[] elements = new long[20];
		for (int i = 0; i < elements.length; i++) {
			elements[i] = (long) (Math.random() * 1000f);
			Object o = setUpElementWithDelay(elements[i], getRandomCell(), getRandomCell());
			addElementToList(o);
		}
		long expectedDelay = getMin(elements);
		assertTrue("Minimum  " + expectedDelay, toplist.getMinDelay() == expectedDelay);

	}
	
	/**
	 * Üres toplista esetén teszteli, hogy a maximális késleltetés elkérésére az érték 0.
	 */
	@Test
	public void testGetMaxDelayWithEmptySet() {
		assertTrue("Max delay  0", toplist.getMaxDelay() == 0);

	}
	/**
	 * A maximális késleltetés elkérését ellenõrzi egy toplista elem esetén.
	 */
	@Test
	public void testGetMaxDelayWithOneElement() {
		long expectedDelay = (long) (Math.random() * 1000f);
		Object element = setUpElementWithDelay(expectedDelay, getRandomCell(), getRandomCell());
		addElementToList(element);
		assertTrue("Average  " + expectedDelay, toplist.getMaxDelay() == expectedDelay);

	}
	
	/**
	 * A maximális késleltetés elkérését ellenõrzi félig feltöltött (vagyis 5 elem) esetén.
	 */
	@Test
	public void testGetMaxDelayWithFiveElements() {
		long[] elements = new long[5];
		for (int i = 0; i < elements.length; i++) {
			elements[i] = (long) (Math.random() * 1000f);
			Object o = setUpElementWithDelay(elements[i], getRandomCell(), getRandomCell());
			addElementToList(o);
		}
		long expectedDelay = getMax(elements);
		assertTrue("Max  " + expectedDelay, toplist.getMaxDelay() == expectedDelay);

	}
	/**
	 *  A maximális késleltetés elkérését ellenõrzi 20 elem (vagyis kétszer annyi elem, mint amit megjelenítünk) esetén.
	 */
	@Test
	public void testGetMaxDelayWithTwentyElements() {
		long[] elements = new long[20];
		for (int i = 0; i < elements.length; i++) {
			elements[i] = (long) (Math.random() * 1000f);
			Object o = setUpElementWithDelay(elements[i], getRandomCell(), getRandomCell());
			addElementToList(o);
		}
		long expectedDelay = getMax(elements);
		assertTrue("Max  " + expectedDelay, toplist.getMaxDelay() == expectedDelay);

	}
	private long getMin(long[] elements){
		long min = Long.MAX_VALUE;
		for(long element:elements){
			if(element < min){
				min = element;
			}
		}
		
		return min;
	}
	private long getMax(long[] elements){
		long max = 0;
		for(long element:elements){
			if(element > max){
				max = element;
			}
		}
		
		return max;
	}
	
	/**
	 * A toplista sztring reprezentációját ellenõrzi késleltetés nélkül, ha nincs elem (vagyis minden sor NULL).
	 */
	@Test
	public void testToStringWithoutDelayOnEmptySet(){
		testToStringFuntionOnEmptySet(toplist-> toplist.toStringWithoutDelay());
	}
	/**
	 *  A toplista sztring reprezentációját ellenõrzi késleltetés nélkül, öt elem esetén (vagyis az elsõ 5 helyen elem, utána 5 NULL).
	 */
	@Test
	public void testToStringWithoutDelayWithFiveElements(){
		testToStringFuntionWithFiveElements(toplist-> toplist.toStringWithoutDelay(), true);
	}
	/**
	 * A toplista sztring reprezentációját ellenõrzi késleltetés nélkül, öt elem esetén (vagyis az elsõ 5 helyen elem, utána 5 NULL).
	 */
	@Test
	public void testToStringWithoutDelayWithTwentyElements(){
		testToStringFunctionWithTwentyElements(toplist-> toplist.toStringWithoutDelay(),true);
	}
	
	/**
	 * A toplista sztring reprezentációját ellenõrzi késleltetéssel, ha nincs elem (vagyis minden sor NULL).
	 */
	@Test
	public void testToStringOnEmptySet(){
		testToStringFuntionOnEmptySet(toplist-> toplist.toString());
	}
	/**
	 *  A toplista sztring reprezentációját ellenõrzi késleltetéssel, öt elem esetén (vagyis az elsõ 5 helyen elem, utána 5 NULL).
	 */
	@Test
	public void testToStringWithFiveElements(){
		testToStringFuntionWithFiveElements(toplist-> toplist.toString(), false);
	}
	/**
	 * A toplista sztring reprezentációját ellenõrzi késleltetéssel, öt elem esetén (vagyis az elsõ 5 helyen elem, utána 5 NULL).
	 */
	@Test
	public void testToStringWithTwentyElements(){
		testToStringFunctionWithTwentyElements(toplist-> toplist.toString(), false);
	}
	
	
	
	private void testToStringFuntionOnEmptySet(Function<ToplistSetInterface, String> tester){
		StringBuilder result = new StringBuilder("");
		for(int i = 1;i <= 10; i++){
			result.append(i);
			result.append("NULL\n");
		}
		assertTrue(tester.apply(toplist).equals(result.toString()));
	}
	
	private void testToStringFuntionWithFiveElements(Function<ToplistSetInterface, String> tester, boolean withoutDelay){
		StringBuilder result = new StringBuilder("");
		
		for(int i = 1;i <= 5; i++){
			addElementToList(setUpElementWithDelay(Long.MAX_VALUE, getRandomCell(), getRandomCell()));
		}
		
		for(int i = 0; i < 5; i++){
			result.append(i+1);
			result.append(toStringOfElement(getElementOfToplistSet(i), withoutDelay));
			result.append("\n");
		}
		
		for(int i = 6; i<= 10; i++){
			result.append(i);
			result.append("NULL\n");
		}
		
		assertTrue(tester.apply(toplist).equals(result.toString()));
	}
	
	private void testToStringFunctionWithTwentyElements(Function<ToplistSetInterface, String> tester, boolean withoutDelay){
		StringBuilder result = new StringBuilder("");
		
		for(int i = 1;i <= 20; i++){
			addElementToList(setUpElementWithDelay(Long.MAX_VALUE, getRandomCell(), getRandomCell()));
		}
		
		for(int i = 0; i < 10; i++){
			result.append(i+1);
			result.append(toStringOfElement(getElementOfToplistSet(i), withoutDelay));
			result.append("\n");
		}
		
		assertTrue(tester.apply(toplist).equals(result.toString()));
	}
	
	protected abstract Object getElementOfToplistSet(int index);
	protected abstract String toStringOfElement(Object o, boolean withoutDelay);
	protected abstract long getInsertedForDelayOfElement(Cell...cells);
	
	/**
	 * Ellenõrzi a toplista elemek insertedForDelay mezõinek frissítését 20 elemre (vagyis kétszer annyira, mint amennyit megjelenítünk).
	 */
	@Test
	public void testRefreshInsertedForDelayOnTwentyElement(){
		List<Object> elements = new ArrayList<Object>();
		List<Cell[]> cells = new ArrayList<Cell[]>();
		for(int i = 0; i < 20;i++){
			cells.add(new Cell[]{getRandomCell(), getRandomCell()});
			elements.add(setUpElementWithDelay(-1, cells.get(i)));
			addElementToList(elements.get(i));
		}
		List<Long> insertedForDelayList = new ArrayList<Long>();
		for(int i = 0; i < 20;i++){
			insertedForDelayList.add((long) (Math.random() * 1000f));
			toplist.refreshInsertedForDelay(insertedForDelayList.get(i), cells.get(i));
		}
		
		for(int i = 0; i < 20; i++){
			assertTrue(Integer.valueOf(i).toString(),insertedForDelayList.get(i) == getInsertedForDelayOfElement(cells.get(i)));
		}
		
		
	}
	
	/**
	 * Ellenõrzi a toplista elemek insertedForDelay mezõinek frissítését, ha a toplistában nincsen elem (ebben az esetben nem történik semmi).
	 */
	@Test
	public void testRefreshInsertedForDelayWithoutElement(){
		Cell[] cells = new Cell[]{getRandomCell(), getRandomCell()};
		toplist.refreshInsertedForDelay(1000, cells);
		assertTrue(toplist.getMinDelay() == Long.MAX_VALUE && getInsertedForDelayOfElement(cells) == -1);
	}
	
	/**
	 * A minimális késleltetés elkérését ellenõrzi, ha van elem, de -1 értékû késleltetéssel.
	 */
	@Test
	public void testGetMinDelayWithMinusDelay(){
		addElementToList(setUpElementWithDelay(-1, new Cell[]{getRandomCell(), getRandomCell()}));
		assertTrue(toplist.getMinDelay() == Long.MAX_VALUE);
	}
	
	/**
	 * A toplisták teljes törlését ellenõrzi.
	 */
	@Test
	public abstract void testClear();
	
	/**
	 * Ellenõrzi a késleltetési idõk frissítését, ha a delay -1, és ha nem. Elõbbi esetben kell frissíteni.
	 */
	@Test
	public abstract void testRefreshDelayTimes();

}
