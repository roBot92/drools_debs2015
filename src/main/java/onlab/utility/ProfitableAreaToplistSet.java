package onlab.utility;

import java.util.Collection;
import java.util.Iterator;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.logging.Logger;

import onlab.event.AreaWithProfit;

@SuppressWarnings("serial")
public class ProfitableAreaToplistSet<T> extends TreeSet<AreaWithProfit> implements SortedSet<AreaWithProfit> {

	private static int MAX_ELEMENT_NUMBER = 10;

	private static final Logger LOGGER = Logger.getLogger(ProfitableAreaToplistSet.class.getName());

	@Override
	public boolean add(AreaWithProfit area) {

		Iterator<AreaWithProfit> descIterator = this.descendingIterator();
		if (this.size() >= MAX_ELEMENT_NUMBER && descIterator.next().compareTo(area) == -1) {
			return false;
		}
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

		if(area.getDelay() == 0){
			area.setDelay(System.currentTimeMillis() - area.getInsertedForDelay());
		}
		return result;
	}

	/*
	 * public void refresh(long currentTimeInMillis){ List<AreaWithProfit>
	 * removables = new ArrayList<AreaWithProfit>(); for(AreaWithProfit i :
	 * this){ if(i.getLastInserted() + 15*60*1000 < currentTimeInMillis){
	 * removables.add(i); } }
	 * 
	 * this.removeAll(removables);
	 * 
	 * }
	 */

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		int counter = 1;
		for (AreaWithProfit area : this) {
			builder.append((counter++) + area.toString() + "\n");
		}

		while (counter <= MAX_ELEMENT_NUMBER) {
			builder.append((counter++) + "NULL" + "\n");
		}

		return builder.toString();

	}

	public AreaWithProfit get(int index) {
		if (index >=  this.size()) {
			return null;
		}
		Iterator<AreaWithProfit> iterator = this.iterator();
		for(int i = 0 ; i < index ; i++){
			iterator.next();
		}
		return iterator.next();
		
	}
	@Override
	public void clear(){
		super.clear();
	}

	@Override
	public boolean addAll(Collection<? extends AreaWithProfit> arg0) {
		long starttime = System.currentTimeMillis();
		boolean result = super.addAll(arg0);
		LOGGER.info(this.getClass().getName() + " addAll method length: "+ (System.currentTimeMillis() - starttime) + " ms");
		return result;
	}
	
	

}
