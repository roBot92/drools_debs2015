package onlab.positioning;

import java.math.BigDecimal;
import java.util.Date;

public class AreaWithProfit implements Comparable<AreaWithProfit> {

	private Cell cell;
	private BigDecimal medianProfit;
	private Date lastInserted;

	public AreaWithProfit(Cell cell, double medianProfit, Date lastInserted) {
		this.cell = cell;
		this.medianProfit = BigDecimal.valueOf(medianProfit);
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
		} else if (!medianProfit.equals(other.medianProfit))
			return false;
		return true;
	}

	@Override
	public int compareTo(AreaWithProfit area) {
		if (this.equals(area)) {
			return 0;
		}

		// Reverse order, biggest first.
		else
			return this.medianProfit.compareTo(area.getMedianProfit()) * -1;
	}

	@Override
	public String toString() {
		return "Cell: " + this.cell + " - Median profit: " + this.medianProfit + " - Dropoff time: " + lastInserted;
	}

}
