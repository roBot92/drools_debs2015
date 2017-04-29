package onlab.utility;

import java.util.Iterator;
import java.util.SortedSet;
import java.util.TreeSet;
import onlab.positioning.*;

@SuppressWarnings("serial")
public class FrequentRoutesToplistSet<T> extends TreeSet<Route> implements SortedSet<Route> {

	private static int MAX_ELEMENT_NUMBER = 10;

	@Override
	public boolean add(Route route) {

		Iterator<Route> i = this.iterator();
		
		while(i.hasNext()){
			Route iRoute = i.next();
			if(iRoute.getPickup_Cell() == route.getPickup_Cell() && iRoute.getDropoff_Cell() == route.getDropoff_Cell()){
				i.remove();
				break;
			}
		}
		boolean result = super.add(route);

		
		if (this.size() > MAX_ELEMENT_NUMBER) {
			i = this.descendingIterator();
			i.next();
			i.remove();
		}

		return result;
	}

	public void decreaseRouteFrequency(Cell pickupCell, Cell dropoffCell){
		Route removable = null;
		for(Route route : this){
			if(route.getPickup_Cell().equals(pickupCell) && route.getDropoff_Cell().equals(dropoffCell)){
				route.setFrequency(route.getFrequency() - 1);
				if(route.getFrequency() <= 0){
					removable = route;
				}
				break;
			}
		}
		
		if(removable != null){
			this.remove(removable);
		}
	}
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		int counter = 1;
		for(Route route : this){
			builder.append((counter++) + route.toString() + "\n");
		}
		
		while(counter < MAX_ELEMENT_NUMBER){
			builder.append((counter++) + "NULL" + "\n");
		}
		
		return builder.toString();
		
		
	}

	
}
