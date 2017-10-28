package onlab.utility;

import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeSet;

import org.apache.commons.collections.keyvalue.MultiKey;

import onlab.event.Route;
import onlab.positioning.Cell;

public class FrequentRoutesToplistSet/* <T extends Route> extends TreeSet<Route> implements SortedSet<Route> */ {

	private static int MAX_ELEMENT_NUMBER = 10;
	private TreeSet<Route> toplist = new TreeSet<Route>();
	private Map<MultiKey, Route> routeMap = new HashMap<MultiKey, Route>();

	public boolean add(Route newRoute) {

		MultiKey routeKey = new MultiKey(newRoute.getPickup_cell(), newRoute.getDropoff_cell());
		Route containedRoute = routeMap.get(routeKey);

		if (containedRoute != null) {
			toplist.remove(containedRoute);
		}

		;
		routeMap.put(routeKey, newRoute);

		return toplist.add(newRoute);
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		int counter = 1;

		Iterator<Route> iterator = toplist.iterator();
		while (iterator.hasNext() && counter < 11) {
			builder.append((counter++) + iterator.next().toString() + "\n");
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

	public boolean contains(Object o) {
		if (o == null || !(o instanceof Route)) {
			return false;
		}

		Route route = (Route) o;

		Route containedRoute = routeMap.get(new MultiKey(route.getPickup_cell(), route.getDropoff_cell()));

		return toplist.contains(containedRoute);
	}

	public Route remove(Cell pickupCell, Cell dropoffCell) {

		Route removable = routeMap.get(new MultiKey(pickupCell, dropoffCell));
		if (removable != null) {
			toplist.remove(removable);
		}

		return removable;
	}

	// for Esper implementation
	public void refreshRoute(Cell pickupCell, Cell dropoffCell, Date lastDropoffTime, int frequency) {
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
		return toplist.size();
	}

	/*
	 * private Route getByCells(Cell pickupCell, Cell dropoffCell) { for(Route route
	 * : this) { if(route.getPickup_cell() == pickupCell && route.getDropoff_cell()
	 * == dropoffCell) { return route; } } return null; }
	 */
}
