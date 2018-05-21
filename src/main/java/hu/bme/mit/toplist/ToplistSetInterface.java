package hu.bme.mit.toplist;


import hu.bme.mit.positioning.Cell;
/**
 * A DEBS2015 GrandChallenge megval�s�t�s�hoz implement�lt toplist�k interf�sze, amely deklar�lja a k�z�s funkci�kat, amelyek a m�r�sek futtat�s�hoz,
 * �s az eredm�nyek feldolgoz�s�hoz kellenek.
 * @author R�zsav�lgyi Botond
 * @see <a href="http://www.debs2015.org/call-grand-challenge.html">DEBS2015 Grand Challenge</a>
 */
public interface ToplistSetInterface {
	/**
	 * Visszaadja a toplist�ban tal�lhat� elemek aktu�lis k�sleltet�s�nek sz�mtani k�zep�t.
	 * @return	az �tlagos k�sleltet�s
	 */
	public long getAverageDelay();

	/**
	 * Visszadja a toplist�ban tal�lhat� elemek aktu�lis k�sleltet�s�nek maximum�t.
	 * @return	a maxim�lis k�sleltet�s
	 */
	public long getMaxDelay();

	/**
	 * Visszaadja a toplist�ban tal�lhat� elemek aktu�lis k�sleltet�s�nek minimum�t.
	 * @return	a minim�lis k�sleltet�s
	 */
	public long getMinDelay();
	/**
	 * Visszaadja a toplista String reprezent�ci�j�t a delay �rt�kek n�lk�l.
	 * @return a toplista String reprezent�ci�ja
	 */
	public String toStringWithoutDelay();

	/**
	 * A toplist�ban l�v� elemeken friss�ti a delay mez� �rt�k�t.
	 */
	public void refreshDelayTimes();

	/**
	 * Adott elemen friss�ti a beilleszt�si id�t.
	 * @param insertedForDelay az �j beilleszt�s ideje ms-ben.
	 * @param cells a toplista elem�t azonos�t� n darab cella.
	 */
	public void refreshInsertedForDelay(long insertedForDelay, Cell... cells);

	/**
	 * A toplista �sszes elem�t t�rli.
	 * @see java.util.Collection#clear()
	 */
	public void clear();
	


}
