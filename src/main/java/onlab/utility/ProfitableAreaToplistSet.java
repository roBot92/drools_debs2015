package onlab.utility;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeSet;

import onlab.event.AreaWithProfit;
import onlab.positioning.Cell;

public class ProfitableAreaToplistSet /* extends TreeSet<AreaWithProfit> implements SortedSet<AreaWithProfit> */ {

	private static int MAX_ELEMENT_NUMBER = 10;

	TreeSet<AreaWithProfit> toplist = new TreeSet<AreaWithProfit>();
	Map<Cell, AreaWithProfit> areaMap = new HashMap<Cell, AreaWithProfit>();

	public boolean add(AreaWithProfit newArea) {

		AreaWithProfit containedArea = areaMap.get(newArea.getCell());

		if (containedArea != null) {
			toplist.remove(containedArea);
		}

		toplist.add(newArea);
		areaMap.put(newArea.getCell(), newArea);

		return toplist.add(newArea);
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		int counter = 1;

		Iterator<AreaWithProfit> iterator = toplist.iterator();
		while (iterator.hasNext()/* && counter < MAX_ELEMENT_NUMBER + 1*/) {
			builder.append((counter++) + iterator.next().toString() + "\n");
		}

		while (counter < MAX_ELEMENT_NUMBER + 1) {
			builder.append((counter++) + "NULL" + "\n");
		}

		return builder.toString();
	}

	public AreaWithProfit get(int index) {
		if (index >= toplist.size()) {
			return null;
		}
		Iterator<AreaWithProfit> iterator = toplist.iterator();
		for (int i = 0; i < index; i++) {
			iterator.next();
		}
		return iterator.next();

	}

	public long getAverageDelay() {
		long sumDelay = 0;
		for (AreaWithProfit area : toplist) {
			sumDelay += area.getDelay();
		}

		return toplist.size() == 0 ? 0 : sumDelay / toplist.size();
	}

	public long size() {
		return toplist.size() < MAX_ELEMENT_NUMBER ? toplist.size() : MAX_ELEMENT_NUMBER;
	}

	public void remove(AreaWithProfit removableArea) {
		if(removableArea != null) {
			removeByCell(removableArea.getCell());
		}
	}

	public boolean isEmpty() {
		return toplist.isEmpty();
	}

	public AreaWithProfit removeByCell(Cell cell) {
		AreaWithProfit area = areaMap.get(cell);

		if (area != null) {
			toplist.remove(area);
		}
		return area;

	}

	// Esper
	public void refreshAreaTaxiCount(Cell cell, Date lastInserted, long count) {
		AreaWithProfit area = removeByCell(cell);

		if (area == null) {
			area = new AreaWithProfit(cell, lastInserted);
			areaMap.put(cell, area);
		} else if(lastInserted != null){
			if(area.getLastInserted() == null || lastInserted.after(area.getLastInserted()))
			area.setLastInserted(lastInserted);
		}

		area.setCountOfTaxes(count);
		if (area.getMedianProfitIndex() != null) {
			if (BigDecimal.ZERO.compareTo(area.getMedianProfitIndex()) == -1 && area.getLastInserted() != null) {
				toplist.add(area);
			}
		}
		if(cell.equals(new Cell(163,155))) {
			System.out.println(area.getLastInserted() + " count: " + area.getCountOfTaxes() + " median:" + area.getMedianProfit());
		}

	}

	public void refreshAreaMedian(Cell cell, Date lastInserted, BigDecimal median) {
		AreaWithProfit area = removeByCell(cell);

		if (area == null) {
			area = new AreaWithProfit(cell, lastInserted);
			areaMap.put(cell, area);
		} else {
			if (lastInserted != null) {
				if(area.getLastInserted() == null) {
					area.setLastInserted(lastInserted);
				} else if(lastInserted.after(area.getLastInserted())){
					area.setLastInserted(lastInserted);
				}
				
			}

		}

		area.setMedianProfit(median);
		if (BigDecimal.ZERO.compareTo(area.getMedianProfitIndex()) == -1) {
			toplist.add(area);
		}
		
		if(cell.equals(new Cell(163,155))) {
			System.out.println(area.getLastInserted() + " count: " + area.getCountOfTaxes() + " median:" + area.getMedianProfit());
		}

	}
	
	public void increaseAreaTaxiCount(Cell cell, Date lastInserted) {
		AreaWithProfit area = removeByCell(cell);
		if(area == null) {
			area = new AreaWithProfit(cell, lastInserted);
			areaMap.put(cell, area);
		}
		
		else if(lastInserted != null) {
			area.setLastInserted(lastInserted);
		}
		area.increaseCountOfTaxes();
		
		if (BigDecimal.ZERO.compareTo(area.getMedianProfitIndex()) == -1) {
			toplist.add(area);
		}
		if(cell.equals(new Cell(163,155))) {
			System.out.println(area.getLastInserted() + " count: " + area.getCountOfTaxes() + " median:" + area.getMedianProfit());
		}
	}
	
	public void decreaseAreaTaxiCount(Cell cell, Date lastInserted) {
		AreaWithProfit area = removeByCell(cell);
		if(area == null && cell != null) {
			area = new AreaWithProfit(cell, lastInserted);
			areaMap.put(cell, area);
		}
		else if(lastInserted != null) {
			area.setLastInserted(lastInserted);
		}
		area.decreaseCountOfTaxes();
		if (BigDecimal.ZERO.compareTo(area.getMedianProfitIndex()) == -1) {
			toplist.add(area);
		}
		if(cell.equals(new Cell(163,155))) {
			System.out.println(area.getLastInserted() + " count: " + area.getCountOfTaxes() + " median:" + area.getMedianProfit());
		}
		
		
	}
	
	public AreaWithProfit getAreaByCell(Cell cell) {
		return areaMap.get(cell);
	}
}
