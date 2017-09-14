package onlab.utility;

import java.util.Iterator;
import java.util.SortedSet;
import java.util.TreeSet;

import onlab.event.Route;
import onlab.positioning.Cell;

@SuppressWarnings("serial")
public class FrequentRoutesToplistSet<T extends Route> extends TreeSet<Route> implements SortedSet<Route> {

	private static int MAX_ELEMENT_NUMBER = 10;

	@Override
	public boolean add(Route route) {

		Iterator<Route> descIterator = this.descendingIterator();
		if(route.getDelay() == -1){
			route.setDelay(System.currentTimeMillis() - route.getInsertedForDelay());
		}
		if (this.size() >= MAX_ELEMENT_NUMBER && descIterator.next().compareTo(route) == -1) {
			return false;
		}
		
		
		Iterator<Route> i = this.iterator();

		while (i.hasNext()) {
			Route iRoute = i.next();
			if (iRoute.getPickup_cell() == route.getPickup_cell()
					&& iRoute.getDropoff_cell() == route.getDropoff_cell()) {
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

	

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		int counter = 1;
		for (Route route : this) {
			builder.append((counter++) + route.toString() + "\n");
		}

		while (counter < MAX_ELEMENT_NUMBER + 1) {
			builder.append((counter++) + "NULL" + "\n");
		}

		return builder.toString();

	}

	public Route get(int index) {
		if (this.size() <= index) {
			return null;
		}

		Iterator<Route> iterator = this.iterator();
		for (int i = 0; i < index; i++) {
			iterator.next();
		}
		return iterator.next();
	}

	@Override
	public boolean contains(Object o) {
		if (o == null || !(o instanceof Route)) {
			return false;
		}
		
		Route route = (Route)o;
		for (Route iRoute : this) {
			if (iRoute.getPickup_cell() == route.getPickup_cell() && iRoute.getDropoff_cell() == route.getDropoff_cell()) {
				return true;
			}
		}
		return false;
	}
	
	public boolean contains(Cell startingCell, Cell dropoffCell) {
		for(Route route : this) {
			if(route.getPickup_cell() == startingCell && route.getDropoff_cell() == dropoffCell) {
				return true;
			}
		}
		
		return false;
	}

}
