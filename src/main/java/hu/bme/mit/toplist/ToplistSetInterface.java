package hu.bme.mit.toplist;


import hu.bme.mit.positioning.Cell;
/**
 * A DEBS2015 GrandChallenge megvalósításához implementált toplisták interfésze, amely deklarálja a közös funkciókat, amelyek a mérések futtatásához,
 * és az eredmények feldolgozásához kellenek.
 * @author Rózsavölgyi Botond
 * @see <a href="http://www.debs2015.org/call-grand-challenge.html">DEBS2015 Grand Challenge</a>
 */
public interface ToplistSetInterface {
	/**
	 * Visszaadja a toplistában található elemek aktuális késleltetésének számtani közepét.
	 * @return	az átlagos késleltetés
	 */
	public long getAverageDelay();

	/**
	 * Visszadja a toplistában található elemek aktuális késleltetésének maximumát.
	 * @return	a maximális késleltetés
	 */
	public long getMaxDelay();

	/**
	 * Visszaadja a toplistában található elemek aktuális késleltetésének minimumát.
	 * @return	a minimális késleltetés
	 */
	public long getMinDelay();
	/**
	 * Visszaadja a toplista String reprezentációját a delay értékek nélkül.
	 * @return a toplista String reprezentációja
	 */
	public String toStringWithoutDelay();

	/**
	 * A toplistában lévõ elemeken frissíti a delay mezõ értékét.
	 */
	public void refreshDelayTimes();

	/**
	 * Adott elemen frissíti a beillesztési idõt.
	 * @param insertedForDelay az új beillesztés ideje ms-ben.
	 * @param cells a toplista elemét azonosító n darab cella.
	 */
	public void refreshInsertedForDelay(long insertedForDelay, Cell... cells);

	/**
	 * A toplista összes elemét törli.
	 * @see java.util.Collection#clear()
	 */
	public void clear();
	


}
