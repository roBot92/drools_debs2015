package hu.bme.mit.toplist;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Calendar;
import java.util.Date;

import org.junit.Test;

import hu.bme.mit.entities.AreaWithProfit;
import hu.bme.mit.positioning.Cell;

/**
 * A {@link hu.bme.mit.toplist.ProfitableAreaToplistSet} osztályhoz készült
 * egységtesztelõ osztály.
 * 
 * @author Rózsavölgyi Botond
 *
 */
public class ProfitableAreaToplistSetTest extends ToplistSetInterfaceTest {

	protected ProfitableAreaToplistSet paToplist;

	@Override
	public void setUp() {
		paToplist = new ProfitableAreaToplistSet();
		toplist = paToplist;

	}

	@Override
	public Object setUpElementWithDelay(long delay, Cell... cells) {

		AreaWithProfit area = new AreaWithProfit(cells[0], BigDecimal.valueOf(delay), new Date());
		area.setDelay(delay);
		return area;
	}

	@Override
	public void addElementToList(Object o) {
		paToplist.add((AreaWithProfit) o);

	}

	@Override
	protected Object getElementOfToplistSet(int index) {
		return paToplist.get(index);
	}

	@Override
	protected String toStringOfElement(Object o, boolean withoutDelay) {
		return withoutDelay ? ((AreaWithProfit) o).toStringWithoutDelay() : ((AreaWithProfit) o).toString();
	}

	@Override
	protected long getInsertedForDelayOfElement(Cell... cells) {
		AreaWithProfit a = paToplist.getArea(cells[0]);
		return a == null ? -1 : a.getInsertedForDelay();
	}

	@Override
	public void testClear() {
		Cell startingCell = super.getRandomCell();

		paToplist.add((AreaWithProfit) setUpElementWithDelay(1000, startingCell));

		assertTrue("size == 1", paToplist.getSetSize() == 1);

		paToplist.clear();

		assertTrue("size == 0", paToplist.getSetSize() == 0 && paToplist.getArea(startingCell) == null);

	}

	@Override
	public void testRefreshDelayTimes() {
		paToplist.refreshDelayTimes();
		assertTrue("1", paToplist.size() == 0);

		AreaWithProfit a1 = setUpArea();
		a1.setDelay(-1);
		AreaWithProfit a2 = setUpArea();
		a2.setDelay(Long.MAX_VALUE);

		long insertTime = System.currentTimeMillis();
		a1.setInsertedForDelay(insertTime);
		paToplist.add(a1);
		paToplist.add(a2);

		paToplist.refreshDelayTimes();

		long delay = a1.getDelay();

		assertTrue("1", a2.getDelay() == Long.MAX_VALUE);
		assertTrue("2", delay >= 0 && delay <= System.currentTimeMillis() - insertTime);

	}

	private AreaWithProfit setUpArea() {
		return setUpArea(null, null, null);
	}

	private AreaWithProfit setUpArea(Cell cell, BigDecimal medianProfitIndex, Date lastInserted) {
		if (cell == null) {
			cell = getRandomCell();
		}
		if (medianProfitIndex == null) {
			medianProfitIndex = BigDecimal.ONE;
		}
		if (lastInserted == null) {
			lastInserted = new Date();
		}

		AreaWithProfit area = new AreaWithProfit(cell, medianProfitIndex, lastInserted);
		area.setMedianProfit(area.getMedianProfitIndex());
		return area;
	}

	/**
	 * A {@link hu.bme.mit.toplist.ProfitableAreaToplistSet#add(AreaWithProfit)}
	 * metódust teszteli null értékkel.
	 */
	@Test
	public void testAddNull() {
		assertTrue(paToplist.isEmpty() && paToplist.size() == 0);
		paToplist.add(null);

		assertTrue(paToplist.isEmpty() && paToplist.size() == 0);

	}

	/**
	 * A {@link hu.bme.mit.toplist.ProfitableAreaToplistSet#get(int)} metódust
	 * teszteli létezõ és nem létezõ indexekkel.
	 */
	@Test
	public void testGet() {
		AreaWithProfit a1 = setUpArea();
		AreaWithProfit a2 = setUpArea();
		a1.setMedianProfitIndex(BigDecimal.TEN.add(BigDecimal.ONE));

		paToplist.add(a1);
		paToplist.add(a2);

		assertTrue("1", paToplist.get(0) == a1);
		assertTrue("2", paToplist.get(1) == a2);
		assertTrue("3", paToplist.get(3) == null);
	}

	/**
	 * A {@link hu.bme.mit.toplist.ProfitableAreaToplistSet#getMinDelay()}
	 * metódust teszteli, ha van negatív érték a késleltetések között.
	 */
	@Test
	public void testGetMinDelayWithMinus() {
		AreaWithProfit a1 = setUpArea();
		AreaWithProfit a2 = setUpArea();
		AreaWithProfit a3 = setUpArea();

		a1.setDelay(1);
		a2.setDelay(-1);
		a3.setDelay(2);

		paToplist.add(a1);
		paToplist.add(a2);
		paToplist.add(a3);

		assertTrue(toplist.getMinDelay() == 1);
	}

	/**
	 * A {@link hu.bme.mit.toplist.ProfitableAreaToplistSet#getSetSize()} és a
	 * {@link hu.bme.mit.toplist.ProfitableAreaToplistSet#size()} metódusok
	 * tesztelése üres listával, 10 elemû listával és 20 elemû listával.
	 */
	@Test
	public void testSize() {
		assertTrue("1", paToplist.size() == 0 && paToplist.getSetSize() == paToplist.size());

		for (int i = 0; i < 10; i++) {
			paToplist.add(setUpArea());
		}
		assertTrue("2", paToplist.size() == 10 && paToplist.getSetSize() == paToplist.size());

		for (int i = 0; i < 10; i++) {
			paToplist.add(setUpArea());
		}

		assertTrue("3", paToplist.size() == 10 && paToplist.getSetSize() == 20);
	}

	/**
	 * A
	 * {@link hu.bme.mit.toplist.ProfitableAreaToplistSet#remove(AreaWithProfit)}
	 * metódust ellenõrzi a listában levõ objektummal, a listában nem levõ
	 * objektummal, illetve null értékkel.
	 */
	@Test
	public void testRemove() {

		paToplist.remove(null);
		assertTrue("1", paToplist.size() == 0);
		AreaWithProfit a = setUpArea();
		assertTrue("2", paToplist.size() == 0 && paToplist.getArea(a.getCell()) == null);

		paToplist.add(a);
		assertTrue("3", paToplist.get(0) == a && paToplist.size() == 1 && paToplist.getArea(a.getCell()) == a);

		paToplist.remove(a);
		assertTrue("4", paToplist.size() == 0 && paToplist.get(0) == null && paToplist.getArea(a.getCell()) == a);

	}

	/**
	 * A {@link hu.bme.mit.toplist.ProfitableAreaToplistSet#removeByCell(Cell)}
	 * metódus ellenõrzi a listában lévõ objektummal, a listában nem levõ
	 * objektummal, illetve null értékkel.
	 */
	@Test
	public void testRemoveByCell() {
		AreaWithProfit a = setUpArea();
		AreaWithProfit resultArea = paToplist.removeByCell(a.getCell());
		assertTrue("1", resultArea == null && paToplist.size() == 0 && paToplist.getArea(a.getCell()) == null);

		paToplist.add(a);
		assertTrue("2", paToplist.get(0) == a && paToplist.size() == 1 && paToplist.getArea(a.getCell()) == a);

		resultArea = paToplist.removeByCell(a.getCell());
		assertTrue("3", paToplist.size() == 0 && resultArea == a && paToplist.getArea(a.getCell()) == a);
	}

	/**
	 * A
	 * {@link hu.bme.mit.toplist.ProfitableAreaToplistSet#refreshAreaTaxiCount(Cell, Date, long)}
	 * metódust ellenõrzi a a listában még nem létezõ objektummal.
	 */
	@Test
	public void testRefreshAreaTaxiCountWithoutExistingArea() {
		Cell cell = getRandomCell();
		Date date = new Date();
		long count = 1;
		paToplist.refreshAreaTaxiCount(cell, date, count);

		AreaWithProfit area = paToplist.getArea(cell);
		assertTrue("1",
				paToplist.size() == 0 && area != null && area.getMedianProfitIndex().compareTo(BigDecimal.ZERO) == 0
						&& area.getCountOfTaxes() == count && area.getLastInserted().equals(date));

		count = 2;
		paToplist.refreshAreaTaxiCount(cell, null, count);
		assertTrue("2",
				paToplist.size() == 0 && area != null && area.getMedianProfitIndex().compareTo(BigDecimal.ZERO) == 0
						&& area.getCountOfTaxes() == count && area.getLastInserted().equals(date));

		count = 4;
		Date prevDate = new Date(area.getLastInserted().getTime() - 10000l);
		paToplist.refreshAreaTaxiCount(cell, prevDate, count);

		assertTrue("3",
				paToplist.size() == 0 && area != null && area.getMedianProfitIndex().compareTo(BigDecimal.ZERO) == 0
						&& area.getCountOfTaxes() == count && area.getLastInserted().equals(date));

		Date newDate = new Date(date.getTime() + 1);
		paToplist.refreshAreaTaxiCount(cell, newDate, count);

		assertTrue("4",
				paToplist.size() == 0 && area != null && area.getMedianProfitIndex().compareTo(BigDecimal.ZERO) == 0
						&& area.getCountOfTaxes() == count && area.getLastInserted().equals(newDate));

		area.setLastInserted(null);

		count = 10;
		paToplist.refreshAreaTaxiCount(cell, newDate, count);
		assertTrue("5",
				paToplist.size() == 0 && area != null && area.getMedianProfitIndex().compareTo(BigDecimal.ZERO) == 0
						&& area.getCountOfTaxes() == count && area.getLastInserted().equals(newDate));

	}

	/**
	 * A
	 * {@link hu.bme.mit.toplist.ProfitableAreaToplistSet#refreshAreaTaxiCount(Cell, Date, long)}
	 * metódust ellenõrzi a listában már létezõ objektummal.
	 */
	@Test
	public void testRefreshAreaTaxiCountWithExistingArea() {
		AreaWithProfit a = setUpArea();
		long originalCount = 1;
		a.setCountOfTaxes(originalCount);
		BigDecimal originalMedianProfitIndex = a.getMedianProfitIndex();
		paToplist.add(a);

		assertTrue("1", paToplist.size() == 1 && paToplist.getArea(a.getCell()) == a);

		Date date = new Date();
		long newCount = originalCount * 2;
		BigDecimal newMedianProfitIndex = originalMedianProfitIndex.divide(BigDecimal.valueOf(2), 2,
				RoundingMode.HALF_UP);

		paToplist.refreshAreaTaxiCount(a.getCell(), date, newCount);

		assertTrue("2", paToplist.size() == 1 && a.getCountOfTaxes() == newCount
				&& a.getMedianProfitIndex().compareTo(newMedianProfitIndex) == 0);
	}

	/**
	 * A
	 * {@link hu.bme.mit.toplist.ProfitableAreaToplistSet#increaseAreaTaxiCount(Cell, Date)}
	 * metódust ellenõrzi létezõ Area objektummal.
	 */
	@Test
	public void testIncreaseAreaTaxiCountWithExistingArea() {
		AreaWithProfit a = setUpArea();
		paToplist.add(a);
		assertTrue("1", paToplist.size() == 1 && paToplist.get(0) == a);

		long originalCount = a.getCountOfTaxes();
		BigDecimal originalMedianProfitIndex = a.getMedianProfitIndex();
		Date originalDate = a.getLastInserted();

		paToplist.increaseAreaTaxiCount(a.getCell(), null);
		assertTrue("2",
				a.getCountOfTaxes() == originalCount + 1 && paToplist.size() == 1
						&& a.getLastInserted().equals(originalDate)
						&& a.getMedianProfitIndex().compareTo(originalMedianProfitIndex) == 0);

		Date newDate = new Date(originalDate.getTime() + 10000l);
		paToplist.increaseAreaTaxiCount(a.getCell(), newDate);

		assertTrue("3",
				a.getCountOfTaxes() == originalCount + 2 && paToplist.size() == 1 && a.getLastInserted().equals(newDate)
						&& a.getMedianProfitIndex().compareTo(
								originalMedianProfitIndex.divide(BigDecimal.valueOf(2), 2, RoundingMode.HALF_UP)) == 0);

	}

	/**
	 * A
	 * {@link hu.bme.mit.toplist.ProfitableAreaToplistSet#increaseAreaTaxiCount(Cell, Date)}
	 * metódust ellenõrzi még nem létezõ Area objektummal.
	 */
	@Test
	public void testIncreaseAreaTaxiCountWithoutExistingArea() {

		long originalCount = 1;
		Date originalDate = new Date();
		Cell cell = getRandomCell();

		paToplist.increaseAreaTaxiCount(cell, originalDate);
		AreaWithProfit a = paToplist.getArea(cell);
		assertTrue("1", a != null && a.getCountOfTaxes() == originalCount && paToplist.size() == 0
				&& a.getMedianProfitIndex().compareTo(BigDecimal.ZERO) == 0);

		paToplist.increaseAreaTaxiCount(cell, null);
		assertTrue("2", a != null && a.getCountOfTaxes() == originalCount + 1 && paToplist.size() == 0
				&& a.getMedianProfitIndex().compareTo(BigDecimal.ZERO) == 0);

	}

	/**
	 * A
	 * {@link hu.bme.mit.toplist.ProfitableAreaToplistSet#decreaseAreaTaxiCount(Cell, Date)}
	 * metódust ellenõrzi a listában már létezõ objektummal.
	 */
	@Test
	public void testDecreaseAreaTaxiCountWithExistingArea() {
		AreaWithProfit a = setUpArea();
		Date originalDate = a.getLastInserted();
		a.setCountOfTaxes(2);

		paToplist.add(a);

		assertTrue("1", paToplist.get(0) == a && a.getMedianProfitIndex().compareTo(BigDecimal.valueOf(0.5)) == 0);

		paToplist.decreaseAreaTaxiCount(a.getCell(), null);
		assertTrue("2", paToplist.get(0) == a && a.getMedianProfitIndex().compareTo(BigDecimal.valueOf(1)) == 0
				&& a.getLastInserted().equals(originalDate) && a.getCountOfTaxes() == 1);

		Date newDate = new Date(originalDate.getTime() + 10000l);
		paToplist.decreaseAreaTaxiCount(a.getCell(), newDate);

		assertTrue("3", paToplist.get(0) == a && a.getMedianProfitIndex().compareTo(BigDecimal.valueOf(1)) == 0
				&& a.getLastInserted().equals(newDate) && a.getCountOfTaxes() == 0);

	}

	/**
	 * A
	 * {@link hu.bme.mit.toplist.ProfitableAreaToplistSet#decreaseAreaTaxiCount(Cell, Date)}
	 * metódust ellenõrzi a listában még nem létezõ objektummal.
	 */
	@Test
	public void testDecreaseAreaTaxiCountWithoutExistingArea() {
		Cell cell = getRandomCell();

		paToplist.decreaseAreaTaxiCount(cell, null);
		AreaWithProfit a = paToplist.getArea(cell);
		assertTrue("1", paToplist.size() == 0 && a != null && a.getMedianProfitIndex().compareTo(BigDecimal.ZERO) == 0
				&& a.getLastInserted() == null);

		Date date = new Date();
		paToplist.decreaseAreaTaxiCount(cell, date);
		assertTrue("2", paToplist.size() == 0 && a != null && a.getLastInserted() != null && a.getCountOfTaxes() == -2);

	}

	/**
	 * A
	 * {@link hu.bme.mit.toplist.ProfitableAreaToplistSet#refreshAreaMedian(Cell, Date, BigDecimal)}
	 * metódust ellenõrzi a listában már létezõ objektummal.
	 */
	@Test
	public void testRefreshAreaMedianWithExistingArea() {
		AreaWithProfit a = setUpArea();
		Date originalDate = a.getLastInserted();
		paToplist.add(a);
		assertTrue("1", paToplist.get(0) == a && paToplist.size() == 1);

		BigDecimal newMedian = BigDecimal.valueOf(100);

		paToplist.refreshAreaMedian(a.getCell(), null, newMedian);

		assertTrue("2", paToplist.get(0) == a && a.getMedianProfit().compareTo(newMedian) == 0
				&& a.getLastInserted().equals(originalDate));

		newMedian = newMedian.multiply(BigDecimal.TEN);
		Date newDate = new Date(originalDate.getTime() + 10000l);
		paToplist.refreshAreaMedian(a.getCell(), newDate, newMedian);

		assertTrue("3", paToplist.get(0) == a && a.getMedianProfit().compareTo(newMedian) == 0
				&& a.getLastInserted().equals(newDate));

	}

	/**
	 * A
	 * {@link hu.bme.mit.toplist.ProfitableAreaToplistSet#refreshAreaMedian(Cell, Date, BigDecimal)}
	 * metódust ellenõrzi a listában még nem létezõ objektummal.
	 */
	@Test
	public void testRefreshAreaMedianWithoutExistingArea() {
		Cell cell = getRandomCell();
		BigDecimal median = BigDecimal.valueOf(100);
		Date date = new Date();

		paToplist.refreshAreaMedian(cell, null, median);
		AreaWithProfit a = paToplist.getArea(cell);

		assertTrue("1", paToplist.size() == 0 && a != null && a.getMedianProfit().compareTo(median) == 0);

		median = median.multiply(BigDecimal.valueOf(2));

		paToplist.refreshAreaMedian(cell, date, median);

		assertTrue("2", paToplist.size() == 1 && a.getMedianProfit().compareTo(median) == 0
				&& a.getLastInserted().equals(date));

		median = BigDecimal.ZERO;
		paToplist.refreshAreaMedian(cell, date, median);
		assertTrue("3",
				paToplist.size() == 0 && paToplist.getArea(cell) != null && a.getMedianProfit().compareTo(median) == 0);

		Date previousDate = new Date(date.getTime() - 10000l);
		paToplist.refreshAreaMedian(cell, previousDate, median);

		assertTrue("4", paToplist.size() == 0 && paToplist.getArea(cell) != null && a.getLastInserted().equals(date));

	}

	/**
	 * Ellenõrzi a medián profitindex szerinti rendezést két elem esetén.
	 */
	@Test
	public void testOrderingByMedianProfitIndexWithTwoElements() {
		AreaWithProfit a1 = setUpArea();
		AreaWithProfit a2 = setUpArea();

		a1.setMedianProfitIndex(BigDecimal.valueOf(2));
		a2.setMedianProfitIndex(BigDecimal.valueOf(3));

		paToplist.add(a1);
		assertTrue("1", paToplist.size() == 1 && paToplist.get(0) == a1);

		paToplist.add(a2);
		assertTrue("2", paToplist.size() == 2 && paToplist.get(0) == a2 && paToplist.get(1) == a1);

		paToplist.refreshAreaMedian(a1.getCell(), null, BigDecimal.valueOf(4));
		assertTrue("3", paToplist.size() == 2 && paToplist.get(0) == a1 && paToplist.get(1) == a2);
		paToplist.remove(a1);
		assertTrue("4", paToplist.size() == 1 && paToplist.get(0) == a2);
	}

	/**
	 * Ellenõrzi a medián profitindex szerinti rendezést két 20 elem esetén.
	 */
	@Test
	public void testOrderingByMedianWith20Elements(){
		
		for(int i = 0; i < 20; i++){
			AreaWithProfit r = setUpArea();
			r.setMedianProfitIndex(BigDecimal.valueOf(Math.random()*1000l));
			paToplist.add(r);
		}
		assertTrue("1",paToplist.size() == ProfitableAreaToplistSet.MAX_ELEMENT_NUMBER && paToplist.getSetSize() == 20);
		
		BigDecimal median = BigDecimal.valueOf(Long.MAX_VALUE);
		for(int i = 0; i < 20; i++){
			assertTrue(i+". iteration", median.compareTo(paToplist.get(i).getMedianProfitIndex()) >= 0);
			median = paToplist.get(i).getMedianProfitIndex();
		}
		
		AreaWithProfit areaOfMinFreq = paToplist.get(19);
		BigDecimal diffBetweenMinAndMax = paToplist.get(0).getMedianProfitIndex().subtract(areaOfMinFreq.getMedianProfitIndex());
		
		for(int i = 0; i< diffBetweenMinAndMax.longValue()-1;i++){
			paToplist.refreshAreaMedian(areaOfMinFreq.getCell(), areaOfMinFreq.getLastInserted(), areaOfMinFreq.getMedianProfitIndex().add(BigDecimal.ONE));
		}
		assertFalse("2",paToplist.get(0) == areaOfMinFreq);
		paToplist.refreshAreaMedian(areaOfMinFreq.getCell(), areaOfMinFreq.getLastInserted(), areaOfMinFreq.getMedianProfitIndex().add(BigDecimal.valueOf(2)));
		assertTrue("3",paToplist.get(0) == areaOfMinFreq);
	}
	
	/**
	 * Ellenõrzi a lastInserted mezõ szerinti rendezést két elem esetén.
	 */
	@Test
	public void testOrderingByLastDropoffTimeWithTwoElements(){
		AreaWithProfit r1 = setUpArea();
		AreaWithProfit r2 = setUpArea();
		Calendar calendar = Calendar.getInstance();
		r1.setLastInserted(calendar.getTime());
		calendar.add(Calendar.SECOND, 1);
		r2.setLastInserted(calendar.getTime());
		
		
		paToplist.add(r1);
		assertTrue("1",paToplist.size() == 1 && paToplist.get(0) == r1);
		
		paToplist.add(r2);
		assertTrue("2",paToplist.size() == 2 && paToplist.get(0) == r2 && paToplist.get(1) == r1);
		
		
		calendar.add(Calendar.SECOND, 1);
		paToplist.remove(r1);
		r1.setLastInserted(calendar.getTime());
		paToplist.add(r1);
		
		assertTrue("3",paToplist.size() == 2 && paToplist.get(0) == r1 && paToplist.get(1) == r2);
		paToplist.remove(r1);
		assertTrue("4",paToplist.size() == 1 && paToplist.get(0) == r2);		
	}
	
	/**
	 * Ellenõrzi a lastInserted mezõ szerinti rendezést 20 elem esetén.
	 */
	@Test
	public void testOrderingByLastDropoffTimeWith20Elements(){
		Calendar calendar = Calendar.getInstance();
		for(int i = 0; i < 20; i++){
			AreaWithProfit r = setUpArea();
			r.setLastInserted(calendar.getTime());
			paToplist.add(r);
			
			calendar.add(Calendar.SECOND, (int) (Math.random()*100l-50));
		}
		assertTrue("1",paToplist.size() == ProfitableAreaToplistSet.MAX_ELEMENT_NUMBER && paToplist.getSetSize() == 20);
		
		Date date = paToplist.get(0).getLastInserted();
		for(int i = 0; i < 20; i++){
			assertTrue(i+". iteration", date.getTime() >= paToplist.get(i).getLastInserted().getTime());
			date = paToplist.get(i).getLastInserted();
		}
		
		AreaWithProfit AreaOfMinDate = paToplist.get(19);
		long diffBetweenMinAndMaxInSec = paToplist.get(0).getLastInserted().getTime() - AreaOfMinDate.getLastInserted().getTime();
		
		calendar.setTime(AreaOfMinDate.getLastInserted());
		for(int i = 0; i< diffBetweenMinAndMaxInSec-1000;i+=1000){
			calendar.add(Calendar.SECOND, 1);
			paToplist.remove(AreaOfMinDate);
			AreaOfMinDate.setLastInserted(calendar.getTime());
			paToplist.add(AreaOfMinDate);
		}
		assertFalse("2",paToplist.get(0) == AreaOfMinDate);
		calendar.add(Calendar.SECOND, 1);
		paToplist.remove(AreaOfMinDate);
		AreaOfMinDate.setLastInserted(new Date(calendar.getTimeInMillis()+1000));
		paToplist.add(AreaOfMinDate);
		
		assertTrue("3",paToplist.get(0) == AreaOfMinDate);	
	}
	
	/**
	 * Ellenõrzi, hogy a medián profitindex és a lastDropoffTime különbözõség esetén a profitindex az elsõdleges.
	 */
	@Test
	public void testOrderingByMedianProfitIndexAndLastDropoffTimeWithTwoElements(){
		AreaWithProfit r1 = setUpArea();
		AreaWithProfit  r2 = setUpArea();
		r1.setMedianProfitIndex(BigDecimal.valueOf(2));
		r2.setMedianProfitIndex(BigDecimal.valueOf(2));
		
		Calendar calendar = Calendar.getInstance();
		r1.setLastInserted(calendar.getTime());
		calendar.add(Calendar.SECOND, 1);
		r2.setLastInserted(calendar.getTime());
		
		paToplist.add(r1);
		paToplist.add(r2);
		
		assertTrue("1",paToplist.get(0) == r2 && paToplist.get(1) == r1);
		
		paToplist.refreshAreaMedian(r1.getCell(),r1.getLastInserted(), BigDecimal.valueOf(3));
		assertTrue("2",paToplist.get(0) == r1 && paToplist.get(1) == r2);
		
		paToplist.refreshAreaMedian(r2.getCell(),r2.getLastInserted(), BigDecimal.valueOf(3));
		assertTrue("3",paToplist.get(0) == r2 && paToplist.get(1) == r1);
		
		
		
	}
}
