package hu.bme.mit.toplist;

import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeSet;

import org.apache.commons.collections.keyvalue.MultiKey;

import hu.bme.mit.entities.Route;
import hu.bme.mit.positioning.Cell;

public class FrequentRoutesToplistSet implements ToplistSetInterface{

	private static int MAX_ELEMENT_NUMBER = 10;
	private TreeSet<Route> toplist = new TreeSet<Route>();
	private Map<MultiKey, Route> routeMap = new HashMap<MultiKey, Route>();


	//A toplistából való törlésrõl kívülrõl kell gondoskodni!
	public boolean add(Route newRoute) {

		if (newRoute == null) {
			return false;
		}
		routeMap.put(new MultiKey(newRoute.getPickup_cell(), newRoute.getDropoff_cell()), newRoute);
		if (newRoute.getFrequency() > 0 && newRoute.getLastDropoffTime() != null) {
			toplist.add(newRoute);
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
	public void increaseRouteFrequency(Cell pickupCell, Cell dropoffCell, Date lastDropoffTime, long insertedForDelay) {
		Route route = remove(pickupCell, dropoffCell);
		if (route == null) {
			route = new Route(pickupCell, dropoffCell, lastDropoffTime, 1);
			routeMap.put(new MultiKey(pickupCell, dropoffCell), route);
		} else {
			route.increaseFrequency();
			route.setLastDropoffTime(lastDropoffTime);
		}
		route.setDelay(-1);
		route.setInsertedForDelay(insertedForDelay);
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

	@Override
	public void refreshDelayTimes() {
		for(Route r : toplist){
			if(r.getDelay() == -1){
				r.setDelay(System.currentTimeMillis() - r.getInsertedForDelay());
			}
		}
		
	}
	
	@Override
	public void refreshInsertedForDelay(long insertedForDelay, Cell... cells) {
		Route route = routeMap.get(new MultiKey(cells[0], cells[1]));
		if(route != null){
			route.setInsertedForDelay(insertedForDelay);
			route.setDelay(-1);
		}
		
	}
	@Override
	public void clear() {
		toplist.clear();
		routeMap.clear();
		
	}
	
}
