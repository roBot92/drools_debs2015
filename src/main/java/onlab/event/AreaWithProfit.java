package onlab.event;

import java.math.BigDecimal;
import java.util.Date;
import java.util.logging.Logger;

import onlab.main.DataFileParser;
import onlab.positioning.Cell;

public class AreaWithProfit implements Comparable<AreaWithProfit> {

	private Cell cell;
	private BigDecimal medianProfit;
	private Date lastInserted;
	private long delay;
	private long insertedForDelay;

	public AreaWithProfit(Cell cell, BigDecimal medianProfit, Date lastInserted) {
		this.cell = cell;
		this.medianProfit = medianProfit;
		this.lastInserted = lastInserted;
	}
	
	public AreaWithProfit(Cell cell, Date lastInserted) {
		this.cell = cell;
		this.lastInserted = lastInserted;
	}

	public Cell getCell() {
		return cell;
	}

	public void setCell(Cell cell) {
		this.cell = cell;
	}

	public BigDecimal getMedianProfit() {
		return medianProfit;
	}

	public void setMedianProfit(BigDecimal medianProfit) {
		this.medianProfit = medianProfit;
	}

	public Date getLastInserted() {
		return lastInserted;
	}

	public void setLastInserted(Date lastInserted) {
		this.lastInserted = lastInserted;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((cell == null) ? 0 : cell.hashCode());
		result = prime * result + ((lastInserted == null) ? 0 : lastInserted.hashCode());
		result = prime * result + ((medianProfit == null) ? 0 : medianProfit.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		AreaWithProfit other = (AreaWithProfit) obj;
		if (cell == null) {
			if (other.cell != null)
				return false;
		} else if (!cell.equals(other.cell))
			return false;
		if (lastInserted == null) {
			if (other.lastInserted != null)
				return false;
		} else if (!lastInserted.equals(other.lastInserted))
			return false;
		if (medianProfit == null) {
			if (other.medianProfit != null)
				return false;
		} else if (medianProfit.compareTo(other.medianProfit) != 0)
			return false;
		return true;
	}

	@Override
	public int compareTo(AreaWithProfit area) {
		if (this.equals(area)) {
			return 0;
		}

		BigDecimal otherMedian = area.getMedianProfit();
		Date otherTime = area.getLastInserted();
		
		if(otherMedian == null){
			return -1;
		}
		else if( medianProfit == null){
			return 1;
		}
		if(medianProfit.compareTo(otherMedian) != 0){
			return medianProfit.compareTo(otherMedian) * -1;
		}
		
		if(otherTime == null){
			return -1;
		}
		else if( lastInserted == null){
			return 1;
		}
		
		if(lastInserted.compareTo(otherTime) != 0){
			return lastInserted.compareTo(otherTime) * -1;
		}
		
		return -1;
			
	}

	@Override
	public String toString() {
		return "Cell: " + this.cell + " - Median profit: " + this.medianProfit + " - Dropoff time: " + lastInserted + "Delay: " + delay + " ms";
	}

	public long getDelay() {
		return delay;
	}

	public void setDelay(long delay) {
		this.delay = delay;
	}

	public long getInsertedForDelay() {
		return insertedForDelay;
	}

	public void setInsertedForDelay(long insertedForDelay) {
		this.insertedForDelay = insertedForDelay;
	}
	
	

}
