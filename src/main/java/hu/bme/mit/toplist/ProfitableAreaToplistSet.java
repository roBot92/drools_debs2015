package hu.bme.mit.toplist;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeSet;

import hu.bme.mit.entities.AreaWithProfit;
import hu.bme.mit.positioning.Cell;

/**
 * A DEBS2015 GrandChallenge második részfeladatához implementált osztály, amely
 * hu.bme.mit.entities.AreaWithProfit objektumokat tároló java.util.TreeSet és
 * java.util.HashMap kollekciókat tart karban.
 * 
 * @author Rózsavölgyi Botond
 * @see <a href="http://www.debs2015.org/call-grand-challenge.html">DEBS2015
 *      Grand Challenge</a>
 */
public class ProfitableAreaToplistSet implements ToplistSetInterface {

	/**
	 * A String-be írásnál használt maximális elemek száma. Alapértelmezett
	 * értéke: 10.
	 */
	public static final int MAX_ELEMENT_NUMBER = 10;

	/**
	 * A toplistát sorrendhelyesen tároló TreeSet objektum.
	 * 
	 * @see java.util.TreeSet
	 */
	private TreeSet<AreaWithProfit> toplist = new TreeSet<AreaWithProfit>();
	/**
	 * A toplista elemeit tartalmazó cella párok alapján tároló HashMap.
	 * 
	 * @see java.util.HashMap
	 * @see org.apache.commons.collections.keyvalue.MultiKey
	 */
	private Map<Cell, AreaWithProfit> areaMap = new HashMap<Cell, AreaWithProfit>();

	/**
	 * Új AreaWithProfit elem hozzáadása a
	 * {@link ProfitableAreaToplistSet#toplist} -hez és a
	 * {@link ProfitableAreaToplistSet#routeMap} -hez. A metódus nem gondoskodik
	 * az esetlegesen azonos cellával rendelkezõ másik AreaWithProfit objektum
	 * törlésérõl.
	 * 
	 * @param newArea
	 *            az hozzáadandó AreaWithProfit objektum
	 * @return true
	 */
	public boolean add(AreaWithProfit newArea) {

		if (newArea == null) {
			return false;
		}
		areaMap.put(newArea.getCell(), newArea);

		if (newArea.getMedianProfitIndex().compareTo(BigDecimal.ZERO) > 0) {
			toplist.add(newArea);
		}
		return true;
	}

	/**
	 * String formátumban visszaadja a {@link ProfitableAreaToplistSet#toplist}
	 * elsõ {@link ProfitableAreaToplistSet#MAX_ELEMENT_NUMBER} darab elemét
	 * rendezetten, sorszámmal megjelölve. Ha kevesebb az elem, mint a
	 * {@link ProfitableAreaToplistSet#MAX_ELEMENT_NUMBER} értéke, akkor az üres
	 * sorokba 'NULL' kerül.
	 */
	@Override
	public String toString() {
		return printToString(false);
	}

	/**
	 * 
	 * A {@link ProfitableAreaToplistSet#toString()}-hez hasonló formátumban
	 * adja vissza a {@link ProfitableAreaToplistSet#toplist} String
	 * reprezentációját, de az AreaWithProfit elemek delay mezõinek az értékei
	 * nélkül. A kimenet így String alapú összehasonlítására alkalmazható.
	 */
	public String toStringWithoutDelay() {
		return printToString(true);
	}

	private String printToString(boolean withoutDelay) {
		StringBuilder builder = new StringBuilder();
		int counter = 1;

		Iterator<AreaWithProfit> iterator = toplist.iterator();
		while (iterator.hasNext() && counter < MAX_ELEMENT_NUMBER + 1) {
			builder.append((counter++)
					+ (withoutDelay ? iterator.next().toStringWithoutDelay() : iterator.next().toString()) + "\n");
		}

		while (counter < MAX_ELEMENT_NUMBER + 1) {
			builder.append((counter++) + "NULL" + "\n");
		}

		return builder.toString();
	}

	/**
	 * Visszaadja a toplistában a kapott paraméter alapján az adott elemet a
	 * {@link ProfitableAreaToplistSet#toplist} objektumból.
	 * 
	 * @param index
	 *            a kért elem pozíciója a
	 *            {@link ProfitableAreaToplistSet#toplist} -ben.
	 * @return
	 */
	public AreaWithProfit get(int index) {
		if (index >= toplist.size()) {
			return null;
		}
		Iterator<AreaWithProfit> iterator = toplist.iterator();
		for (int i = 0; i < index; i++) {
			iterator.next();
		}
		return iterator.next();

	}

	/**
	 * A {@link ProfitableAreaToplistSet#toplist} aktuális 'telítettségét' adja
	 * vissza.<br>
	 * Formálisan a min({@link ProfitableAreaToplistSet#MAX_ELEMENT_NUMBER},
	 * {@link ProfitableAreaToplistSet#toplist}.size()) értékét.
	 * 
	 * @return
	 */
	public long size() {
		return toplist.size() < MAX_ELEMENT_NUMBER ? toplist.size() : MAX_ELEMENT_NUMBER;
	}

	/**
	 * Törli a paraméterként kapot AreaWithProfit objektumot a
	 * {@link ProfitableAreaToplistSet#toplist}-ból.
	 * 
	 * @param removableArea
	 */
	public void remove(AreaWithProfit removableArea) {
		if (removableArea != null) {
			toplist.remove(removableArea);
		}
	}

	/**
	 * A {@link ProfitableAreaToplistSet#toplist} üres -e.
	 * 
	 * @return
	 * @see java.util.Collections#isEmpty()
	 */
	public boolean isEmpty() {
		return toplist.isEmpty();
	}

	/**
	 * {@link ProfitableAreaToplistSet#toplist} -ból kitörli a paraméterként
	 * kapott Cell objektum alapján azonosított AreaWithProfit objektumot.
	 * 
	 * @param cell
	 * @return
	 */
	public AreaWithProfit removeByCell(Cell cell) {
		AreaWithProfit area = areaMap.get(cell);

		if (area != null) {
			toplist.remove(area);
		}
		return area;

	}

	// Esper
	/**
	 * A paraméterként kapott Cell objektum alapján azonosított AreaWithProfit
	 * elemnek frissíti a lastInserted, és a count mezõit. Ha még nem létezik az
	 * AreaWithProfit objektum, akkor létrehozza.<br>
	 * A lastInserted csak akkor frissül, ha az eredeti értéke null, vagy a
	 * paraméterként kapott érték nagyobb, mint az eredeti.<br>
	 * A {@link ProfitableAreaToplistSet#toplist} -be csak akkor kerül be a
	 * frissített AreaWithProfit objektum, ha a lastInserted mezõje nem null, és
	 * a medianProfitIndex mezõjének az értéke nagyobb, mint 0.
	 * 
	 * @param cell
	 * @param lastInserted
	 * @param count
	 */
	public void refreshAreaTaxiCount(Cell cell, Date lastInserted, long count) {
		AreaWithProfit area = removeByCell(cell);

		if (area == null) {
			area = new AreaWithProfit(cell, lastInserted);
			areaMap.put(cell, area);
		} else if (lastInserted != null) {
			if (area.getLastInserted() == null || lastInserted.after(area.getLastInserted()))
				area.setLastInserted(lastInserted);
		}

		area.setCountOfTaxes(count);
		if (area.getMedianProfitIndex() != null) {
			if (BigDecimal.ZERO.compareTo(area.getMedianProfitIndex()) == -1 && area.getLastInserted() != null) {
				toplist.add(area);
			}
		}

	}

	/**
	 * A paraméterként kapott Cell objektum alapján azonosított AreaWithProfit
	 * elemnek frissíti a lastInserted mezõjét és a median mezõjét. Ha még nem
	 * létezik az AreaWithProfit objektum, akkor létrehozza.<br>
	 * A lastInserted csak akkor frissül, ha az eredeti értéke null, vagy a
	 * paraméterként kapott érték nagyobb, mint az eredeti.<br>
	 * A {@link ProfitableAreaToplistSet#toplist} -be csak akkor kerül be a
	 * frissített AreaWithProfit objektum, ha a lastInserted mezõje nem null, és
	 * a medianProfitIndex mezõjének az értéke nagyobb, mint 0.
	 * 
	 * @param cell
	 * @param lastInserted
	 * @param median
	 */
	public void refreshAreaMedian(Cell cell, Date lastInserted, BigDecimal median) {
		AreaWithProfit area = removeByCell(cell);

		if (area == null) {
			area = new AreaWithProfit(cell, lastInserted);
			areaMap.put(cell, area);
		} else {
			if (lastInserted != null) {
				if (area.getLastInserted() == null) {
					area.setLastInserted(lastInserted);
				} else if (lastInserted.after(area.getLastInserted())) {
					area.setLastInserted(lastInserted);
				}

			}

		}

		area.setMedianProfit(median);
		if (BigDecimal.ZERO.compareTo(area.getMedianProfitIndex()) == -1 && area.getLastInserted() != null) {
			toplist.add(area);
		}

	}

	/**
	 * A paraméterként kapott Cell objektum alapján azonosított AreaWithProfit
	 * elemnek inkrementálja 1-gyel a count mezõjét, illetve frissíti a
	 * lastInserted mezõjét. Ha még nem létezik az AreaWithProfit objektum,
	 * akkor létrehozza.<br>
	 * A lastInserted csak akkor frissül, ha az eredeti értéke null, vagy a
	 * paraméterként kapott érték nagyobb, mint az eredeti.<br>
	 * A {@link ProfitableAreaToplistSet#toplist} -be csak akkor kerül be a
	 * frissített AreaWithProfit objektum, ha a lastInserted mezõje nem null, és
	 * a medianProfitIndex mezõjének az értéke nagyobb, mint 0.
	 * 
	 * @param cell
	 * @param lastInserted
	 */
	public void increaseAreaTaxiCount(Cell cell, Date lastInserted) {
		AreaWithProfit area = removeByCell(cell);
		if (area == null) {
			area = new AreaWithProfit(cell, lastInserted);
			areaMap.put(cell, area);
		}

		else if (lastInserted != null) {
			area.setLastInserted(lastInserted);
		}
		area.increaseCountOfTaxes();

		if (BigDecimal.ZERO.compareTo(area.getMedianProfitIndex()) == -1) {
			toplist.add(area);
		}
	}

	/**
	 * A paraméterként kapott Cell objektum alapján azonosított AreaWithProfit
	 * elemnek csökkenti 1-gyel a count mezõjét, ha az eredeti értéke nagyobb,
	 * mint 0, illetve frissíti a lastInserted mezõjét. Ha még nem létezik az
	 * AreaWithProfit objektum, akkor létrehozza.<br>
	 * A lastInserted csak akkor frissül, ha az eredeti értéke null, vagy a
	 * paraméterként kapott érték nagyobb, mint az eredeti.<br>
	 * A {@link ProfitableAreaToplistSet#toplist} -be csak akkor kerül be a
	 * frissített AreaWithProfit objektum, ha a lastInserted mezõje nem null, és
	 * a medianProfitIndex mezõjének az értéke nagyobb, mint 0.
	 * 
	 * @param cell
	 * @param lastInserted
	 */
	public void decreaseAreaTaxiCount(Cell cell, Date lastInserted) {
		AreaWithProfit area = removeByCell(cell);
		if (area == null) {
			area = new AreaWithProfit(cell, lastInserted);
			areaMap.put(cell, area);
		} else if (lastInserted != null) {
			area.setLastInserted(lastInserted);
		}
		area.decreaseCountOfTaxes();
		if (BigDecimal.ZERO.compareTo(area.getMedianProfitIndex()) == -1 && area.getLastInserted() != null) {
			toplist.add(area);
		}

	}

	@Override
	public long getAverageDelay() {
		long sum = 0;
		int counter = 0;
		for (AreaWithProfit r : toplist) {
			long delay = r.getDelay();
			if (delay > -1) {
				sum += r.getDelay();
				counter++;
			}
		}

		return counter == 0 ? 0 : sum / counter;
	}

	@Override
	public long getMaxDelay() {
		long max = 0;
		for (AreaWithProfit r : toplist) {
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
		for (AreaWithProfit r : toplist) {
			long delay = r.getDelay();
			if (delay > -1 && delay < min) {
				min = delay;
			}
		}

		return min;
	}

	/**
	 * Frissíti a {@link ProfitableAreaToplistSet#toplist} AreaWithProfit
	 * elemeinek a delay mezõit, ha a jelenlegi értékük -1.<br>
	 * Egy AreaWithProfit elem delay mezõjének új értéke ekkor a rendszeridõ
	 * pillanatnyi értékének és az AreaWithProfit objektum insertedForDelay
	 * mezõjének az értékének a különbsége lesz.<br>
	 * area.delay = {@link java.lang.System#currentTimeInMillis()} -
	 * area.insertedForDelay
	 * 
	 * @see ToplistSetInterface#refreshDelayTimes()
	 */
	@Override
	public void refreshDelayTimes() {
		for (AreaWithProfit area : toplist) {
			if (area.getDelay() == -1) {
				area.setDelay(System.currentTimeMillis() - area.getInsertedForDelay());
			}
		}

	}

	@Override
	public void refreshInsertedForDelay(long insertedForDelay, Cell... cells) {
		AreaWithProfit area = areaMap.get(cells[0]);
		if (area != null) {
			area.setInsertedForDelay(insertedForDelay);
			area.setDelay(-1);
		}

	}

	@Override
	public void clear() {
		toplist.clear();
		areaMap.clear();

	}

	public AreaWithProfit getArea(Cell cell) {
		return areaMap.get(cell);
	}
	/**
	 * A {@link ProfitableAreaToplistSet#toplist} pillanatnyi elemszámát adja vissza.
	 * @return
	 */
	public long getSetSize() {
		return toplist.size();
	}
}
