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
	 * �tlagos k�sleltet�s lek�r�se egy elem eset�n.
	 */
	@Test
	public void testGetAverageDelayWithOneElement() {
		long expectedDelay = (long) (Math.random() * 1000f);
		Object element = setUpElementWithDelay(expectedDelay, getRandomCell(), getRandomCell());
		addElementToList(element);
		assertTrue("Average  " + expectedDelay, toplist.getAverageDelay() == expectedDelay);
	}

	/**
	 * �tlagos k�sleltet�s 5 elem eset�n, teh�t f�lig tel�tett list�val.
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
	 * �tlagos k�sleltet�s lek�r�se 20 elemb�l, vagyis a k�tszer annyi elemb�l,
	 * mint amennyit megjelen�t�nk.
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
	 * Adott elemet a toplist�ba helyez� absztrakt f�ggv�ny.
	 * 
	 * @param o
	 *            - a toplista elem
	 */
	protected abstract void addElementToList(Object o);

	/**
	 * Adott toplista elemet param�terben kapott k�sleltet�ssel l�trehoz�
	 * met�dus.
	 * 
	 * @param delay
	 *            - a k�sleltet�s
	 * @param cells
	 *            - az elemet azonos�t� cell�k.
	 * @return - a l�trehozott listaelem
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
	 * �res toplista eset�n teszteli, hogy a minim�lis k�sleltet�s elk�r�s�re a Long.MAX_VALUE-t kapjuk.
	 */
	@Test
	public void testGetMinDelayWithEmptySet() {
		assertTrue("Min delay  Long.MAX", toplist.getMinDelay() == Long.MAX_VALUE);

	}
	
	/**
	 * A minim�lis k�sleltet�s elk�r�s�t ellen�rzi egy toplista elem eset�n.
	 */
	@Test
	public void testGetMinDelayWithOneElement() {
		long expectedDelay = (long) (Math.random() * 1000f);
		Object element = setUpElementWithDelay(expectedDelay, getRandomCell(), getRandomCell());
		addElementToList(element);
		assertTrue("Minimum  " + expectedDelay, toplist.getMinDelay() == expectedDelay);

	}
	/**
	 * A minim�lis k�sleltet�s elk�r�s�t ellen�rzi f�lig felt�lt�tt (vagyis 5 elem) eset�n.
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
	 *  A minim�lis k�sleltet�s elk�r�s�t ellen�rzi 20 elem (vagyis k�tszer annyi elem, mint amit megjelen�t�nk) eset�n.
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
	 * �res toplista eset�n teszteli, hogy a maxim�lis k�sleltet�s elk�r�s�re az �rt�k 0.
	 */
	@Test
	public void testGetMaxDelayWithEmptySet() {
		assertTrue("Max delay  0", toplist.getMaxDelay() == 0);

	}
	/**
	 * A maxim�lis k�sleltet�s elk�r�s�t ellen�rzi egy toplista elem eset�n.
	 */
	@Test
	public void testGetMaxDelayWithOneElement() {
		long expectedDelay = (long) (Math.random() * 1000f);
		Object element = setUpElementWithDelay(expectedDelay, getRandomCell(), getRandomCell());
		addElementToList(element);
		assertTrue("Average  " + expectedDelay, toplist.getMaxDelay() == expectedDelay);

	}
	
	/**
	 * A maxim�lis k�sleltet�s elk�r�s�t ellen�rzi f�lig felt�lt�tt (vagyis 5 elem) eset�n.
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
	 *  A maxim�lis k�sleltet�s elk�r�s�t ellen�rzi 20 elem (vagyis k�tszer annyi elem, mint amit megjelen�t�nk) eset�n.
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
	 * A toplista sztring reprezent�ci�j�t ellen�rzi k�sleltet�s n�lk�l, ha nincs elem (vagyis minden sor NULL).
	 */
	@Test
	public void testToStringWithoutDelayOnEmptySet(){
		testToStringFuntionOnEmptySet(toplist-> toplist.toStringWithoutDelay());
	}
	/**
	 *  A toplista sztring reprezent�ci�j�t ellen�rzi k�sleltet�s n�lk�l, �t elem eset�n (vagyis az els� 5 helyen elem, ut�na 5 NULL).
	 */
	@Test
	public void testToStringWithoutDelayWithFiveElements(){
		testToStringFuntionWithFiveElements(toplist-> toplist.toStringWithoutDelay(), true);
	}
	/**
	 * A toplista sztring reprezent�ci�j�t ellen�rzi k�sleltet�s n�lk�l, �t elem eset�n (vagyis az els� 5 helyen elem, ut�na 5 NULL).
	 */
	@Test
	public void testToStringWithoutDelayWithTwentyElements(){
		testToStringFunctionWithTwentyElements(toplist-> toplist.toStringWithoutDelay(),true);
	}
	
	/**
	 * A toplista sztring reprezent�ci�j�t ellen�rzi k�sleltet�ssel, ha nincs elem (vagyis minden sor NULL).
	 */
	@Test
	public void testToStringOnEmptySet(){
		testToStringFuntionOnEmptySet(toplist-> toplist.toString());
	}
	/**
	 *  A toplista sztring reprezent�ci�j�t ellen�rzi k�sleltet�ssel, �t elem eset�n (vagyis az els� 5 helyen elem, ut�na 5 NULL).
	 */
	@Test
	public void testToStringWithFiveElements(){
		testToStringFuntionWithFiveElements(toplist-> toplist.toString(), false);
	}
	/**
	 * A toplista sztring reprezent�ci�j�t ellen�rzi k�sleltet�ssel, �t elem eset�n (vagyis az els� 5 helyen elem, ut�na 5 NULL).
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
	 * Ellen�rzi a toplista elemek insertedForDelay mez�inek friss�t�s�t 20 elemre (vagyis k�tszer annyira, mint amennyit megjelen�t�nk).
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
	 * Ellen�rzi a toplista elemek insertedForDelay mez�inek friss�t�s�t, ha a toplist�ban nincsen elem (ebben az esetben nem t�rt�nik semmi).
	 */
	@Test
	public void testRefreshInsertedForDelayWithoutElement(){
		Cell[] cells = new Cell[]{getRandomCell(), getRandomCell()};
		toplist.refreshInsertedForDelay(1000, cells);
		assertTrue(toplist.getMinDelay() == Long.MAX_VALUE && getInsertedForDelayOfElement(cells) == -1);
	}
	
	/**
	 * A minim�lis k�sleltet�s elk�r�s�t ellen�rzi, ha van elem, de -1 �rt�k� k�sleltet�ssel.
	 */
	@Test
	public void testGetMinDelayWithMinusDelay(){
		addElementToList(setUpElementWithDelay(-1, new Cell[]{getRandomCell(), getRandomCell()}));
		assertTrue(toplist.getMinDelay() == Long.MAX_VALUE);
	}
	
	/**
	 * A toplist�k teljes t�rl�s�t ellen�rzi.
	 */
	@Test
	public abstract void testClear();
	
	/**
	 * Ellen�rzi a k�sleltet�si id�k friss�t�s�t, ha a delay -1, �s ha nem. El�bbi esetben kell friss�teni.
	 */
	@Test
	public abstract void testRefreshDelayTimes();

}
