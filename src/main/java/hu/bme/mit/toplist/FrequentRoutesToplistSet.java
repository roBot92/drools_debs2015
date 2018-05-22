package hu.bme.mit.toplist;

import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeSet;

import org.apache.commons.collections.keyvalue.MultiKey;

import hu.bme.mit.entities.Route;
import hu.bme.mit.positioning.Cell;
/**
 * A DEBS2015 GrandChallenge els� r�szfeladat�hoz implement�lt oszt�ly,
 * amely hu.bme.mit.entities.Route objektumokat t�rol� java.util.TreeSet �s java.util.HashMap kollekci�kat tart karban.
 * 
 * @author R�zsav�lgyi Botond
 *	@see <a href="http://www.debs2015.org/call-grand-challenge.html">DEBS2015 Grand Challenge</a>
 */
public class FrequentRoutesToplistSet implements ToplistSetInterface{

	/**
	 * A String-be �r�sn�l haszn�lt maxim�lis elemek sz�ma. Alap�rtelmezett �rt�ke: 10.
	 */
	public static final int MAX_ELEMENT_NUMBER = 10;
	/**
	 * A toplist�t sorrendhelyesen t�rol� TreeSet objektum.
	 * @see java.util.TreeSet
	 */
	private TreeSet<Route> toplist = new TreeSet<Route>();
	/**
	 * A toplista elemeit tartalmaz� cella p�rok alapj�n t�rol� HashMap. A cella p�rokat MultiKey objektumok fogj�k �ssze.
	 * @see java.util.HashMap
	 * @see org.apache.commons.collections.keyvalue.MultiKey
	 */
	private Map<MultiKey, Route> routeMap = new HashMap<MultiKey, Route>();


	/**
	 * �j Route elem hozz�ad�sa a {@link FrequentRoutesToplistSet#toplist} -hez �s a {@link FrequentRoutesToplistSet#routeMap} -hez.
	 * A met�dus nem gondoskodik az azonos cell�val rendelkez� m�sik Route objektum t�rl�s�r�l!
	 * @param newRoute az hozz�adand� Route objektum
	 * @return true
	 */
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

	/**
	 * String form�tumban visszaadja a {@link FrequentRoutesToplistSet#toplist} els� {@link FrequentRoutesToplistSet#MAX_ELEMENT_NUMBER} darab elem�t rendezetten,
	 * sorsz�mmal megjel�lve. Ha kevesebb az elem, mint a {@link FrequentRoutesToplistSet#MAX_ELEMENT_NUMBER} �rt�ke, akkor az �res sorokba 'NULL' ker�l.
	 */
	@Override
	public String toString() {
		return printToString(false);
	}

	/**
	 * 
	 * A {@link FrequentRoutesToplistSet#toString()}-hez hasonl� form�tumban adja vissza a {@link FrequentRoutesToplistSet#toplist} String reprezent�co�j�t, de a Route elemek delay mez�inek az �rt�kei n�lk�l.
	 * A kimenet �gy String alap� �sszehasonl�t�s�ra haszn�lhat�.
	 */
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

	/**
	 * Visszaadja a toplist�ban a kapott param�ter alapj�n az adott elemet a {@link FrequentRoutesToplistSet#toplist} objektumb�l.
	 * @param index a k�rt elem poz�ci�ja a {@link FrequentRoutesToplistSet#toplist} -ben.
	 * @return
	 */
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


	/**
	 * True-val t�r vissza, ha jelenleg a {@link FrequentRoutesToplistSet#toplist}-ben l�tezik olyan Route objektum, aminek a cell�i megegyeznek a
	 * param�terk�nt kapott Route cell�ival.
	 * @param route
	 * @return
	 */
	public boolean contains(Route route) {
		if(route != null){
			Route containedRoute = routeMap.get(new MultiKey(route.getPickup_cell(), route.getDropoff_cell()));
			if(containedRoute != null){
				return toplist.contains(route);
			}
		}
		
		return false;
	}

	/**
	 * T�rli a {@link FrequentRoutesToplistSet#toplist} -b�l a param�terben megadott cell�kkal rendelkez� Route objektumot.
	 * @param pickupCell
	 * @param dropoffCell
	 * @return
	 */
	public Route remove(Cell pickupCell, Cell dropoffCell) {

		Route removable = routeMap.get(new MultiKey(pickupCell, dropoffCell));
		if (removable != null) {
			toplist.remove(removable);
		}

		return removable;
	}

	/**
	 * T�rli a {@link FrequentRoutesToplistSet#toplist}-b�l a param�terben kapott Route objektumot.
	 * @param route
	 * @return
	 */
	public boolean remove(Route route) {
		return toplist.remove(route);
	}

	/**
	 * Friss�ti a param�terben kapott  lastDropoffTime �s frequency �rt�kekkel a k�t cella alapj�n beazonos�tott Route objektumot.
	 * Ha a Route objektum m�g nem l�tezik, akkor l�trehozza, �s beilleszti a {@link FrequentRoutesToplistSet#toplist}-be �s a {@link FrequentRoutesToplistSet#routeMap} -be is. 
	 * A {@link FrequentRoutesToplistSet#toplist} tag rendezetts�ge megmarad. Frequency == 0 eset�n a {@link FrequentRoutesToplistSet#toplist}-ba 
	 * nem ker�l be/vissza a Route objektum.
	 * @param pickupCell a keresett Route pickup_cell mez�je
	 * @param dropoffCell a keresett Route dropoff_cell mez�je
	 * @param lastDropoffTime a keresett Route �j lastDropoffTime �rt�ke.
	 * @param frequency a keresett Route �j frequency �rt�ke.
	 */
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


	/**
	 * A k�t param�terben kapott cella alapj�n azonos�tott Route objektum frequency mez�j�nek �rt�k�t inkrement�lja 1-gyel, illetve friss�ti a lastDropoffTime
	 * �s az insertedForDelay mez�k �rt�k�t, a delay mez� �rt�k�t pedig -1 -re �ll�tja.
	 * @param pickupCell a keresett Route pickup_cell mez�je
	 * @param dropoffCell a keresett Route dropoff_cell mez�je
	 * @param lastDropoffTime a keresett Route �j lastDropoffTime �rt�ke.
	 * @param insertedForDelay a keresett Route insertedForDelay �rt�ke
	 */
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

	/**
	 *  A k�t param�terben kapott cella alapj�n azonos�tott Route objektum frequency mez�j�nek �rt�k�t dekrement�lja 1-gyel.
	 *  Ha frequency mez� �rt�ke 0, akkor t�rli a {@link FrequentRoutesToplistSet#toplist}-b�l.
	 * @param pickupCell
	 * @param dropoffCell
	 */
	public void decreaseRouteFrequency(Cell pickupCell, Cell dropoffCell) {
		Route route = remove(pickupCell, dropoffCell);
		if (route != null) {
			boolean isNotEmpty = route.decreaseFrequency();
			if (isNotEmpty) {
				toplist.add(route);
			}
		}
	}

	/**
	 * A {@link FrequentRoutesToplistSet#toplist} megjelen�tett r�sz�nek aktu�lis 'tel�tetts�g�t' adja vissza.<br> 
	 * Form�lisan a min({@link FrequentRoutesToplistSet#MAX_ELEMENT_NUMBER}, {@link FrequentRoutesToplistSet#toplist}.size()) �rt�k�t.
	 * @return 
	 */
	public long size() {
		return toplist.size() < MAX_ELEMENT_NUMBER ? toplist.size() : MAX_ELEMENT_NUMBER;
	}

	/**
	 * A {@link FrequentRoutesToplistSet#toplist} pillanatnyi elemsz�m�t adja vissza.
	 * @return
	 */
	public long getSetSize() {
		return toplist.size();
	}



	@Override
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

		return counter == 0 ? counter: sum / counter;
	}

	@Override
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

	@Override
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
/**
 * Friss�ti a {@link FrequentRoutesToplistSet#toplist} Route elemeinek a delay mez�it, ha a jelenlegi �rt�k�k -1.<br>
 * Egy Route elem delay mez�j�nek �j �rt�ke ekkor a rendszerid� pillanatnyi �rt�ke �s a Route objektum insertedForDelay mez�j�nek az �rt�k�nek a k�l�nbs�ge.<br>
 * route.delay = {@link java.lang.System#currentTimeMillis()} - route.insertedForDelay
 * @see ToplistSetInterface#refreshDelayTimes()
 */ 
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
	
	public Route getRoute(Cell startingCell, Cell dropoffCell){
		return routeMap.get(new MultiKey(startingCell, dropoffCell));
	}
	
}
