package onlab.utility;

import java.util.Iterator;
import java.util.SortedSet;
import java.util.TreeSet;
import onlab.positioning.*;

@SuppressWarnings("serial")
public class FrequentRoutesToplistSet<T extends Route> extends TreeSet<Route> implements SortedSet<Route> {

	private static int MAX_ELEMENT_NUMBER = 10;

	@Override
	public boolean add(Route route) {

		Iterator<Route> descIterator = this.descendingIterator();
		if (this.size() >= MAX_ELEMENT_NUMBER && descIterator.next().compareTo(route) == -1) {
			return false;
		}
		Iterator<Route> i = this.iterator();

		while (i.hasNext()) {
			Route iRoute = i.next();
			if (iRoute.getPickup_Cell() == route.getPickup_Cell()
					&& iRoute.getDropoff_Cell() == route.getDropoff_Cell()) {
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

	public void decreaseRouteFrequency(Cell pickupCell, Cell dropoffCell) {
		Route decreasable = null;
		Iterator<Route> iterator = this.iterator();
		while (iterator.hasNext()) {
			Route route = iterator.next();
			if (route.getPickup_Cell().equals(pickupCell) && route.getDropoff_Cell().equals(dropoffCell)) {
				decreasable = route;
				iterator.remove();
				break;
			}
		}

		if (decreasable != null) {
			decreasable.setFrequency(decreasable.getFrequency() - 1);
			if (decreasable.getFrequency() > 0) {
				super.add(decreasable);
			}
		}

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

	public boolean containsRoute(Cell pickup, Cell dropoff) {
		if (this.size() == 0) {
			return false;
		}
		for (Route route : this) {
			if (route.getPickup_Cell() == pickup && route.getDropoff_Cell() == dropoff) {
				return true;
			}
		}
		return false;
	}

}
