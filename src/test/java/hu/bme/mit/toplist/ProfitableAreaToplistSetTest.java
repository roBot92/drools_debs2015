package hu.bme.mit.toplist;

import static org.junit.Assert.assertTrue;

import java.math.BigDecimal;
import java.util.Date;

import hu.bme.mit.entities.AreaWithProfit;
import hu.bme.mit.entities.Route;
import hu.bme.mit.positioning.Cell;

public class ProfitableAreaToplistSetTest extends ToplistSetInterfaceTest {

	protected ProfitableAreaToplistSet paToplist;


	@Override
	public void setUp() {
		paToplist = new ProfitableAreaToplistSet();
		toplist = paToplist;
		
	}
	

	@Override
	public Object setUpElementWithDelay(long delay, Cell... cells) {
		
		AreaWithProfit area = new AreaWithProfit(cells[0],BigDecimal.valueOf(delay),new Date());
		area.setDelay(delay);
		return area;
	}

	@Override
	public void addElementToList(Object o) {
		paToplist.add((AreaWithProfit)o);
		
	}

	@Override
	public Object getElementOfToplistSet(int index) {
		return paToplist.get(index);
	}

	@Override
	public String toStringOfElement(Object o, boolean withoutDelay) {
		return withoutDelay?((AreaWithProfit) o).toStringWithoutDelay() : ((AreaWithProfit) o).toString(); 
	}

	@Override
	public long getInsertedForDelayOfElement(Cell... cells) {
		AreaWithProfit a = paToplist.getArea(cells[0]);
		return a == null? -1:a.getInsertedForDelay();
	}

	@Override
	public void testClear() {
		Cell startingCell = super.getRandomCell();
		
		paToplist.add((AreaWithProfit)setUpElementWithDelay(1000, startingCell));
		
		assertTrue("size == 1",paToplist.getSetSize() == 1);
		
		paToplist.clear();
		
		assertTrue("size == 0",paToplist.getSetSize() == 0 && paToplist.getArea(startingCell) == null);
		
		
	}


}
