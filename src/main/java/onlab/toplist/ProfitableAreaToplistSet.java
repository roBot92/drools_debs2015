package onlab.toplist;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeSet;

import onlab.event.AreaWithProfit;
import onlab.positioning.Cell;

public class ProfitableAreaToplistSet implements ToplistSetInterface{

	private static int MAX_ELEMENT_NUMBER = 10;

	TreeSet<AreaWithProfit> toplist = new TreeSet<AreaWithProfit>();
	Map<Cell, AreaWithProfit> areaMap = new HashMap<Cell, AreaWithProfit>();

	//A toplistából törlésrõl külön kell gondoskodni.
	public boolean add(AreaWithProfit newArea) {

		if (newArea == null) {
			return false;
		}
		areaMap.put(newArea.getCell(), newArea);

		
		if (newArea.getMedianProfitIndex().compareTo(BigDecimal.ZERO) > 0) {
			toplist.add(newArea);
		}
		return true;
	}

	@Override
	public String toString() {
		return printToString(false);
	}

	public String toStringWithoutDelay() {
		return printToString(true);
	}

	public String printToString(boolean withoutDelay) {
		StringBuilder builder = new StringBuilder();
		int counter = 1;

		Iterator<AreaWithProfit> iterator = toplist.iterator();
		while (iterator.hasNext() && counter < MAX_ELEMENT_NUMBER + 1) {
			builder.append((counter++)
					+ (withoutDelay ? iterator.next().toStringWithoutDelay() : iterator.next().toString()) + "\n");
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


	public long size() {
		return toplist.size() < MAX_ELEMENT_NUMBER ? toplist.size() : MAX_ELEMENT_NUMBER;
	}

	public void remove(AreaWithProfit removableArea) {
		if (removableArea != null) {
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
		} else if (lastInserted != null) {
			if (area.getLastInserted() == null || lastInserted.after(area.getLastInserted()))
				area.setLastInserted(lastInserted);
		}

		area.setCountOfTaxes(count);
		if (area.getMedianProfitIndex() != null) {
			if (BigDecimal.ZERO.compareTo(area.getMedianProfitIndex()) == -1 && area.getLastInserted() != null) {
				toplist.add(area);
			}
		}

	}

	public void refreshAreaMedian(Cell cell, Date lastInserted, BigDecimal median) {
		AreaWithProfit area = removeByCell(cell);

		if (area == null) {
			area = new AreaWithProfit(cell, lastInserted);
			areaMap.put(cell, area);
		} else {
			if (lastInserted != null) {
				if (area.getLastInserted() == null) {
					area.setLastInserted(lastInserted);
				} else if (lastInserted.after(area.getLastInserted())) {
					area.setLastInserted(lastInserted);
				}

			}

		}

		area.setMedianProfit(median);
		if (BigDecimal.ZERO.compareTo(area.getMedianProfitIndex()) == -1) {
			toplist.add(area);
		}

	}

	public void increaseAreaTaxiCount(Cell cell, Date lastInserted) {
		AreaWithProfit area = removeByCell(cell);
		if (area == null) {
			area = new AreaWithProfit(cell, lastInserted);
			areaMap.put(cell, area);
		}

		else if (lastInserted != null) {
			area.setLastInserted(lastInserted);
		}
		area.increaseCountOfTaxes();

		if (BigDecimal.ZERO.compareTo(area.getMedianProfitIndex()) == -1) {
			toplist.add(area);
		}
	}

	public void decreaseAreaTaxiCount(Cell cell, Date lastInserted) {
		AreaWithProfit area = removeByCell(cell);
		if (area == null && cell != null) {
			area = new AreaWithProfit(cell, lastInserted);
			areaMap.put(cell, area);
		} else if (lastInserted != null) {
			area.setLastInserted(lastInserted);
		}
		area.decreaseCountOfTaxes();
		if (BigDecimal.ZERO.compareTo(area.getMedianProfitIndex()) == -1) {
			toplist.add(area);
		}

	}

	public AreaWithProfit getAreaByCell(Cell cell) {
		return areaMap.get(cell);
	}
	
	public long getAverageDelay() {
		long sum = 0;
		int counter = 0;
		for (AreaWithProfit r : toplist) {
			long delay = r.getDelay();
			if (delay > -1) {
				sum += r.getDelay();
				counter++;
			}
		}

		return sum / counter;
	}

	public long getMaxDelay() {
		long max = 0;
		for (AreaWithProfit r : toplist) {
			long delay = r.getDelay();
			if (delay > max) {
				max = delay;
			}
		}

		return max;
	}

	public long getMinDelay() {
		long min = Long.MAX_VALUE;
		for (AreaWithProfit r : toplist) {
			long delay = r.getDelay();
			if (delay > -1 && delay < min) {
				min = delay;
			}
		}

		return min;
	}
	@Override
	public void refreshDelayTimes() {
		for(AreaWithProfit area : toplist){
			if(area.getDelay() == -1){
				area.setDelay(System.currentTimeMillis() - area.getInsertedForDelay());
			}
		}
		
	}
	
	@Override
	public void refreshInsertedForDelay(long insertedForDelay, Cell... cells) {
		AreaWithProfit area = areaMap.get(cells[0]);
		if(area != null){
			area.setInsertedForDelay(insertedForDelay);
			area.setDelay(-1);
		}
		
	}
	@Override
	public void clear() {
		toplist.clear();
		areaMap.clear();
		
	}
	
}
