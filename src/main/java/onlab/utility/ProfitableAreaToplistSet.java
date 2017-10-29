package onlab.utility;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeSet;

import onlab.event.AreaWithProfit;
import onlab.event.Route;
import onlab.positioning.Cell;

public class ProfitableAreaToplistSet /* extends TreeSet<AreaWithProfit> implements SortedSet<AreaWithProfit> */ {

	private static int MAX_ELEMENT_NUMBER = 11;

	TreeSet<AreaWithProfit> toplist = new TreeSet<AreaWithProfit>();
	Map<Cell, AreaWithProfit> areaMap = new HashMap<Cell, AreaWithProfit>();

	public boolean add(AreaWithProfit newArea) {

		AreaWithProfit containedArea = areaMap.get(newArea.getCell());

		if (containedArea != null) {
			toplist.remove(containedArea);
		}

		toplist.add(newArea);
		areaMap.put(newArea.getCell(), newArea);

		return toplist.add(newArea);/*
									 * Iterator<AreaWithProfit> descIterator = this.descendingIterator(); if
									 * (this.size() >= MAX_ELEMENT_NUMBER && descIterator.next().compareTo(area) ==
									 * -1) { if (area.getDelay() == -1) { area.setDelay(System.currentTimeMillis() -
									 * area.getInsertedForDelay()); } } Iterator<AreaWithProfit> i =
									 * this.iterator();
									 * 
									 * while (i.hasNext()) { AreaWithProfit iArea = i.next(); if (iArea.getCell() ==
									 * area.getCell()) { i.remove(); break; } }
									 * 
									 * boolean result = super.add(area);
									 * 
									 * if (this.size() > MAX_ELEMENT_NUMBER) { i = this.descendingIterator();
									 * i.next(); i.remove(); }
									 * 
									 * if (area.getDelay() == -1) { area.setDelay(System.currentTimeMillis() -
									 * area.getInsertedForDelay()); } return result;
									 */
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		int counter = 1;

		Iterator<AreaWithProfit> iterator = toplist.iterator();
		while (iterator.hasNext() && counter < 11) {
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
		return toplist.size();
	}

	public void remove(AreaWithProfit removableArea) {
		toplist.remove(removableArea);
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
		} else {
			area.setLastInserted(lastInserted);
		}

		area.setCountOfTaxes(count);
		if (area.getMedianProfitIndex() != null) {
			if (BigDecimal.ZERO.compareTo(area.getMedianProfitIndex()) == -1 && lastInserted != null) {
				toplist.add(area);
			}
		}

	}

	public void refreshAreaMedian(Cell cell, Date lastInserted, Double median) {
		AreaWithProfit area = removeByCell(cell);

		if (area == null) {
			area = new AreaWithProfit(cell, lastInserted);
			areaMap.put(cell, area);
		} else {
			area.setLastInserted(lastInserted);
		}

		area.setMedianProfit(median != null ? BigDecimal.valueOf(median) : null);
		if (BigDecimal.ZERO.compareTo(area.getMedianProfitIndex()) == -1 && lastInserted != null) {
			toplist.add(area);
		}

	}
}
