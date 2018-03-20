package onlab.utility;

import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeSet;

import org.apache.commons.collections.keyvalue.MultiKey;

import onlab.event.Route;
import onlab.positioning.Cell;

public class FrequentRoutesToplistSet implements ToplistSetInterface{

	private static int MAX_ELEMENT_NUMBER = 10;
	private TreeSet<Route> toplist = new TreeSet<Route>();
	private Map<MultiKey, Route> routeMap = new HashMap<MultiKey, Route>();

	public boolean add(Route newRoute) {

		if (newRoute == null) {
			return false;
		}

		// MultiKey key = new MultiKey(newRoute.getPickup_cell(),
		// newRoute.getDropoff_cell());
		// Route oldRoute = routeMap.put(key, newRoute);

		// TODO ez lehet, hogy kell, de nem biztos
		/*
		 * if( oldRoute != null){ toplist.remove(oldRoute); }
		 */

		if (newRoute.getFrequency() > 0 && newRoute.getLastDropoffTime() != null) {
			toplist.add(newRoute);
		}

		if (newRoute.getDelay() == -1) {
			newRoute.setDelay(System.currentTimeMillis() - newRoute.getInsertedForDelay());
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

	private String printToString(boolean withoutDelay) {
		StringBuilder builder = new StringBuilder();
		int counter = 1;

		Iterator<Route> iterator = toplist.iterator();
		while (iterator.hasNext() && counter < 11) {
			builder.append((counter++)
					+ (withoutDelay ? iterator.next().toStringWithoutDelay() : iterator.next().toString()) + "\n");
		}

		while (counter < MAX_ELEMENT_NUMBER + 1) {
			builder.append((counter++) + "NULL" + "\n");
		}

		return builder.toString();
	}

	public Route get(int index) {
		if (toplist.size() <= index) {
			return null;
		}

		Iterator<Route> iterator = toplist.iterator();
		for (int i = 0; i < index; i++) {
			iterator.next();
		}
		return iterator.next();
	}

	public boolean setContains(Route route) {
		if (route == null) {
			return false;
		}
		Route containedRoute = routeMap.get(new MultiKey(route.getPickup_cell(), route.getDropoff_cell()));

		return toplist.contains(containedRoute);
	}

	public boolean contains(Route route) {
		if (route == null) {
			return false;
		}
		for (int i = 0; i < MAX_ELEMENT_NUMBER; i++) {
			Route tRoute = get(i);
			if (tRoute == null) {
				return false;
			}
			if (tRoute.equals(route)) {
				return true;
			}

		}
		return false;
	}

	public Route remove(Cell pickupCell, Cell dropoffCell) {

		Route removable = routeMap.get(new MultiKey(pickupCell, dropoffCell));
		if (removable != null) {
			toplist.remove(removable);
		}

		return removable;
	}

	public boolean remove(Route route) {
		// TODO lehet, hogy kell még
		/*
		 * if(route == null) { return null; } return
		 * remove(route.getPickup_cell(), route.getDropoff_cell());
		 */

		return toplist.remove(route);
	}

	// for Esper implementation
	public void refreshRoute(Cell pickupCell, Cell dropoffCell, Date lastDropoffTime, long frequency) {
		Route route = remove(pickupCell, dropoffCell);

		if (route == null) {
			route = new Route(pickupCell, dropoffCell, lastDropoffTime, frequency);
			routeMap.put(new MultiKey(pickupCell, dropoffCell), route);
		} else {
			if (lastDropoffTime != null) {
				route.setLastDropoffTime(lastDropoffTime);
			}

			route.setFrequency(frequency);
		}
		if (frequency > 0 && lastDropoffTime != null) {
			toplist.add(route);
		}

	}

	// for BeepBeep implementation
	public void increaseRouteFrequency(Cell pickupCell, Cell dropoffCell, Date lastDropoffTime) {
		Route route = remove(pickupCell, dropoffCell);
		if (route == null) {
			route = new Route(pickupCell, dropoffCell, lastDropoffTime, 1);
			routeMap.put(new MultiKey(pickupCell, dropoffCell), route);
		} else {
			route.increaseFrequency();
			route.setLastDropoffTime(lastDropoffTime);
		}

		toplist.add(route);
	}

	public void decreaseRouteFrequency(Cell pickupCell, Cell dropoffCell) {
		Route route = remove(pickupCell, dropoffCell);
		if (route != null) {
			boolean isNotEmpty = route.decreaseFrequency();
			if (isNotEmpty) {
				toplist.add(route);
			}
		}
	}

	public long size() {
		return toplist.size() < MAX_ELEMENT_NUMBER ? toplist.size() : MAX_ELEMENT_NUMBER;
	}

	public long getSetSize() {
		return toplist.size();
	}

	/*
	 * private Route getByCells(Cell pickupCell, Cell dropoffCell) { for(Route
	 * route : this) { if(route.getPickup_cell() == pickupCell &&
	 * route.getDropoff_cell() == dropoffCell) { return route; } } return null;
	 * }
	 */
	public long getAverageDelay() {
		long sum = 0;
		int counter = 0;
		for (Route r : toplist) {
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
		for (Route r : toplist) {
			long delay = r.getDelay();
			if (delay > max) {
				max = delay;
			}
		}

		return max;
	}

	public long getMinDelay() {
		long min = Long.MAX_VALUE;
		for (Route r : toplist) {
			long delay = r.getDelay();
			if (delay > -1 && delay < min) {
				min = delay;
			}
		}

		return min;
	}
}
