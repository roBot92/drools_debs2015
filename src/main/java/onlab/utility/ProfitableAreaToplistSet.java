package onlab.utility;


import java.util.Iterator;
import java.util.SortedSet;
import java.util.TreeSet;

import onlab.positioning.AreaWithProfit;

@SuppressWarnings("serial")
public class ProfitableAreaToplistSet<T> extends TreeSet<AreaWithProfit> implements SortedSet<AreaWithProfit> {

	private static int MAX_ELEMENT_NUMBER = 10;

	@Override
	public boolean add(AreaWithProfit area) {
		Iterator<AreaWithProfit> i = this.iterator();

		while (i.hasNext()) {
			AreaWithProfit iArea = i.next();
			if (iArea.getCell() == area.getCell()) {
				i.remove();
				break;
			}
		}
		boolean result = super.add(area);

		if (this.size() > MAX_ELEMENT_NUMBER) {
			i = this.descendingIterator();
			i.next();
			i.remove();
		}

		return result;
	}
	
	/*public void refresh(long currentTimeInMillis){
		List<AreaWithProfit> removables = new ArrayList<AreaWithProfit>();
		for(AreaWithProfit i : this){
			if(i.getLastInserted() + 15*60*1000 < currentTimeInMillis){
				removables.add(i);
			}
		}
		
		this.removeAll(removables);
		
	}*/
	
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		int counter = 1;
		for(AreaWithProfit area : this){
			builder.append((counter++) + area.toString() + "\n");
		}
		
		while(counter <= MAX_ELEMENT_NUMBER){
			builder.append((counter++) + "NULL"+"\n");
		}
		
		return builder.toString();
		
		
	}

}
