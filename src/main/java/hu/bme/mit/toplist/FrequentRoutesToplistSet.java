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
 * A DEBS2015 GrandChallenge elsõ részfeladatához implementált osztály,
 * amely hu.bme.mit.entities.Route objektumokat tároló java.util.TreeSet és java.util.HashMap kollekciókat tart karban.
 * 
 * @author Rózsavölgyi Botond
 *	@see <a href="http://www.debs2015.org/call-grand-challenge.html">DEBS2015 Grand Challenge</a>
 */
public class FrequentRoutesToplistSet implements ToplistSetInterface{

	/**
	 * A String-be írásnál használt maximális elemek száma. Alapértelmezett értéke: 10.
	 */
	public static final int MAX_ELEMENT_NUMBER = 10;
	/**
	 * A toplistát sorrendhelyesen tároló TreeSet objektum.
	 * @see java.util.TreeSet
	 */
	private TreeSet<Route> toplist = new TreeSet<Route>();
	/**
	 * A toplista elemeit tartalmazó cella párok alapján tároló HashMap. A cella párokat MultiKey objektumok fogják össze.
	 * @see java.util.HashMap
	 * @see org.apache.commons.collections.keyvalue.MultiKey
	 */
	private Map<MultiKey, Route> routeMap = new HashMap<MultiKey, Route>();


	/**
	 * Új Route elem hozzáadása a {@link FrequentRoutesToplistSet#toplist} -hez és a {@link FrequentRoutesToplistSet#routeMap} -hez.
	 * A metódus nem gondoskodik az azonos cellával rendelkezõ másik Route objektum törlésérõl!
	 * @param newRoute az hozzáadandó Route objektum
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
	 * String formátumban visszaadja a {@link FrequentRoutesToplistSet#toplist} elsõ {@link FrequentRoutesToplistSet#MAX_ELEMENT_NUMBER} darab elemét rendezetten,
	 * sorszámmal megjelölve. Ha kevesebb az elem, mint a {@link FrequentRoutesToplistSet#MAX_ELEMENT_NUMBER} értéke, akkor az üres sorokba 'NULL' kerül.
	 */
	@Override
	public String toString() {
		return printToString(false);
	}

	/**
	 * 
	 * A {@link FrequentRoutesToplistSet#toString()}-hez hasonló formátumban adja vissza a {@link FrequentRoutesToplistSet#toplist} String reprezentácoóját, de a Route elemek delay mezõinek az értékei nélkül.
	 * A kimenet így String alapú összehasonlítására használható.
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
	 * Visszaadja a toplistában a kapott paraméter alapján az adott elemet a {@link FrequentRoutesToplistSet#toplist} objektumból.
	 * @param index a kért elem pozíciója a {@link FrequentRoutesToplistSet#toplist} -ben.
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
	 * True-val tér vissza, ha jelenleg a {@link FrequentRoutesToplistSet#toplist}-ben létezik olyan Route objektum, aminek a cellái megegyeznek a
	 * paraméterként kapott Route celláival.
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
	 * Törli a {@link FrequentRoutesToplistSet#toplist} -bõl a paraméterben megadott cellákkal rendelkezõ Route objektumot.
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
	 * Törli a {@link FrequentRoutesToplistSet#toplist}-bõl a paraméterben kapott Route objektumot.
	 * @param route
	 * @return
	 */
	public boolean remove(Route route) {
		return toplist.remove(route);
	}

	/**
	 * Frissíti a paraméterben kapott  lastDropoffTime és frequency értékekkel a két cella alapján beazonosított Route objektumot.
	 * Ha a Route objektum még nem létezik, akkor létrehozza, és beilleszti a {@link FrequentRoutesToplistSet#toplist}-be és a {@link FrequentRoutesToplistSet#routeMap} -be is. 
	 * A {@link FrequentRoutesToplistSet#toplist} tag rendezettsége megmarad. Frequency == 0 esetén a {@link FrequentRoutesToplistSet#toplist}-ba 
	 * nem kerül be/vissza a Route objektum.
	 * @param pickupCell a keresett Route pickup_cell mezõje
	 * @param dropoffCell a keresett Route dropoff_cell mezõje
	 * @param lastDropoffTime a keresett Route új lastDropoffTime értéke.
	 * @param frequency a keresett Route új frequency értéke.
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
	 * A két paraméterben kapott cella alapján azonosított Route objektum frequency mezõjének értékét inkrementálja 1-gyel, illetve frissíti a lastDropoffTime
	 * és az insertedForDelay mezõk értékét, a delay mezõ értékét pedig -1 -re állítja.
	 * @param pickupCell a keresett Route pickup_cell mezõje
	 * @param dropoffCell a keresett Route dropoff_cell mezõje
	 * @param lastDropoffTime a keresett Route új lastDropoffTime értéke.
	 * @param insertedForDelay a keresett Route insertedForDelay értéke
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
	 *  A két paraméterben kapott cella alapján azonosított Route objektum frequency mezõjének értékét dekrementálja 1-gyel.
	 *  Ha frequency mezõ értéke 0, akkor törli a {@link FrequentRoutesToplistSet#toplist}-bõl.
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
	 * A {@link FrequentRoutesToplistSet#toplist} megjelenített részének aktuális 'telítettségét' adja vissza.<br> 
	 * Formálisan a min({@link FrequentRoutesToplistSet#MAX_ELEMENT_NUMBER}, {@link FrequentRoutesToplistSet#toplist}.size()) értékét.
	 * @return 
	 */
	public long size() {
		return toplist.size() < MAX_ELEMENT_NUMBER ? toplist.size() : MAX_ELEMENT_NUMBER;
	}

	/**
	 * A {@link FrequentRoutesToplistSet#toplist} pillanatnyi elemszámát adja vissza.
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
 * Frissíti a {@link FrequentRoutesToplistSet#toplist} Route elemeinek a delay mezõit, ha a jelenlegi értékük -1.<br>
 * Egy Route elem delay mezõjének új értéke ekkor a rendszeridõ pillanatnyi értéke és a Route objektum insertedForDelay mezõjének az értékének a különbsége.<br>
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
