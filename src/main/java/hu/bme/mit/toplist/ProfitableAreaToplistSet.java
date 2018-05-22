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
 * A DEBS2015 GrandChallenge m�sodik r�szfeladat�hoz implement�lt oszt�ly, amely
 * hu.bme.mit.entities.AreaWithProfit objektumokat t�rol� java.util.TreeSet �s
 * java.util.HashMap kollekci�kat tart karban.
 * 
 * @author R�zsav�lgyi Botond
 * @see <a href="http://www.debs2015.org/call-grand-challenge.html">DEBS2015
 *      Grand Challenge</a>
 */
public class ProfitableAreaToplistSet implements ToplistSetInterface {

	/**
	 * A String-be �r�sn�l haszn�lt maxim�lis elemek sz�ma. Alap�rtelmezett
	 * �rt�ke: 10.
	 */
	public static final int MAX_ELEMENT_NUMBER = 10;

	/**
	 * A toplist�t sorrendhelyesen t�rol� TreeSet objektum.
	 * 
	 * @see java.util.TreeSet
	 */
	private TreeSet<AreaWithProfit> toplist = new TreeSet<AreaWithProfit>();
	/**
	 * A toplista elemeit tartalmaz� cella p�rok alapj�n t�rol� HashMap.
	 * 
	 * @see java.util.HashMap
	 * @see org.apache.commons.collections.keyvalue.MultiKey
	 */
	private Map<Cell, AreaWithProfit> areaMap = new HashMap<Cell, AreaWithProfit>();

	/**
	 * �j AreaWithProfit elem hozz�ad�sa a
	 * {@link ProfitableAreaToplistSet#toplist} -hez �s a
	 * {@link ProfitableAreaToplistSet#routeMap} -hez. A met�dus nem gondoskodik
	 * az esetlegesen azonos cell�val rendelkez� m�sik AreaWithProfit objektum
	 * t�rl�s�r�l.
	 * 
	 * @param newArea
	 *            az hozz�adand� AreaWithProfit objektum
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
	 * String form�tumban visszaadja a {@link ProfitableAreaToplistSet#toplist}
	 * els� {@link ProfitableAreaToplistSet#MAX_ELEMENT_NUMBER} darab elem�t
	 * rendezetten, sorsz�mmal megjel�lve. Ha kevesebb az elem, mint a
	 * {@link ProfitableAreaToplistSet#MAX_ELEMENT_NUMBER} �rt�ke, akkor az �res
	 * sorokba 'NULL' ker�l.
	 */
	@Override
	public String toString() {
		return printToString(false);
	}

	/**
	 * 
	 * A {@link ProfitableAreaToplistSet#toString()}-hez hasonl� form�tumban
	 * adja vissza a {@link ProfitableAreaToplistSet#toplist} String
	 * reprezent�ci�j�t, de az AreaWithProfit elemek delay mez�inek az �rt�kei
	 * n�lk�l. A kimenet �gy String alap� �sszehasonl�t�s�ra alkalmazhat�.
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
	 * Visszaadja a toplist�ban a kapott param�ter alapj�n az adott elemet a
	 * {@link ProfitableAreaToplistSet#toplist} objektumb�l.
	 * 
	 * @param index
	 *            a k�rt elem poz�ci�ja a
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
	 * A {@link ProfitableAreaToplistSet#toplist} aktu�lis 'tel�tetts�g�t' adja
	 * vissza.<br>
	 * Form�lisan a min({@link ProfitableAreaToplistSet#MAX_ELEMENT_NUMBER},
	 * {@link ProfitableAreaToplistSet#toplist}.size()) �rt�k�t.
	 * 
	 * @return
	 */
	public long size() {
		return toplist.size() < MAX_ELEMENT_NUMBER ? toplist.size() : MAX_ELEMENT_NUMBER;
	}

	/**
	 * T�rli a param�terk�nt kapot AreaWithProfit objektumot a
	 * {@link ProfitableAreaToplistSet#toplist}-b�l.
	 * 
	 * @param removableArea
	 */
	public void remove(AreaWithProfit removableArea) {
		if (removableArea != null) {
			toplist.remove(removableArea);
		}
	}

	/**
	 * A {@link ProfitableAreaToplistSet#toplist} �res -e.
	 * 
	 * @return
	 * @see java.util.Collections#isEmpty()
	 */
	public boolean isEmpty() {
		return toplist.isEmpty();
	}

	/**
	 * {@link ProfitableAreaToplistSet#toplist} -b�l kit�rli a param�terk�nt
	 * kapott Cell objektum alapj�n azonos�tott AreaWithProfit objektumot.
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
	 * A param�terk�nt kapott Cell objektum alapj�n azonos�tott AreaWithProfit
	 * elemnek friss�ti a lastInserted, �s a count mez�it. Ha m�g nem l�tezik az
	 * AreaWithProfit objektum, akkor l�trehozza.<br>
	 * A lastInserted csak akkor friss�l, ha az eredeti �rt�ke null, vagy a
	 * param�terk�nt kapott �rt�k nagyobb, mint az eredeti.<br>
	 * A {@link ProfitableAreaToplistSet#toplist} -be csak akkor ker�l be a
	 * friss�tett AreaWithProfit objektum, ha a lastInserted mez�je nem null, �s
	 * a medianProfitIndex mez�j�nek az �rt�ke nagyobb, mint 0.
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
	 * A param�terk�nt kapott Cell objektum alapj�n azonos�tott AreaWithProfit
	 * elemnek friss�ti a lastInserted mez�j�t �s a median mez�j�t. Ha m�g nem
	 * l�tezik az AreaWithProfit objektum, akkor l�trehozza.<br>
	 * A lastInserted csak akkor friss�l, ha az eredeti �rt�ke null, vagy a
	 * param�terk�nt kapott �rt�k nagyobb, mint az eredeti.<br>
	 * A {@link ProfitableAreaToplistSet#toplist} -be csak akkor ker�l be a
	 * friss�tett AreaWithProfit objektum, ha a lastInserted mez�je nem null, �s
	 * a medianProfitIndex mez�j�nek az �rt�ke nagyobb, mint 0.
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
	 * A param�terk�nt kapott Cell objektum alapj�n azonos�tott AreaWithProfit
	 * elemnek inkrement�lja 1-gyel a count mez�j�t, illetve friss�ti a
	 * lastInserted mez�j�t. Ha m�g nem l�tezik az AreaWithProfit objektum,
	 * akkor l�trehozza.<br>
	 * A lastInserted csak akkor friss�l, ha az eredeti �rt�ke null, vagy a
	 * param�terk�nt kapott �rt�k nagyobb, mint az eredeti.<br>
	 * A {@link ProfitableAreaToplistSet#toplist} -be csak akkor ker�l be a
	 * friss�tett AreaWithProfit objektum, ha a lastInserted mez�je nem null, �s
	 * a medianProfitIndex mez�j�nek az �rt�ke nagyobb, mint 0.
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
	 * A param�terk�nt kapott Cell objektum alapj�n azonos�tott AreaWithProfit
	 * elemnek cs�kkenti 1-gyel a count mez�j�t, ha az eredeti �rt�ke nagyobb,
	 * mint 0, illetve friss�ti a lastInserted mez�j�t. Ha m�g nem l�tezik az
	 * AreaWithProfit objektum, akkor l�trehozza.<br>
	 * A lastInserted csak akkor friss�l, ha az eredeti �rt�ke null, vagy a
	 * param�terk�nt kapott �rt�k nagyobb, mint az eredeti.<br>
	 * A {@link ProfitableAreaToplistSet#toplist} -be csak akkor ker�l be a
	 * friss�tett AreaWithProfit objektum, ha a lastInserted mez�je nem null, �s
	 * a medianProfitIndex mez�j�nek az �rt�ke nagyobb, mint 0.
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
	 * Friss�ti a {@link ProfitableAreaToplistSet#toplist} AreaWithProfit
	 * elemeinek a delay mez�it, ha a jelenlegi �rt�k�k -1.<br>
	 * Egy AreaWithProfit elem delay mez�j�nek �j �rt�ke ekkor a rendszerid�
	 * pillanatnyi �rt�k�nek �s az AreaWithProfit objektum insertedForDelay
	 * mez�j�nek az �rt�k�nek a k�l�nbs�ge lesz.<br>
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
	 * A {@link ProfitableAreaToplistSet#toplist} pillanatnyi elemsz�m�t adja vissza.
	 * @return
	 */
	public long getSetSize() {
		return toplist.size();
	}
}
