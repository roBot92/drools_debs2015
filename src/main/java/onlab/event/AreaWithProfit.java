package onlab.event;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.SimpleDateFormat;
import java.util.Date;
import onlab.positioning.Cell;
import onlab.utility.DataFileParser;

public class AreaWithProfit implements Comparable<AreaWithProfit> {

	private Cell cell;
	private BigDecimal medianProfitIndex;
	private Date lastInserted;
	private long delay;
	private long insertedForDelay;

	// Drools-hoz nem kell jelenleg
	private BigDecimal medianProfit;
	private long countOfTaxes = 0;
	
	private static final SimpleDateFormat df = DataFileParser.SIMPLE_DATE_FORMAT;
	public AreaWithProfit(Cell cell, BigDecimal medianProfitIndex, Date lastInserted) {
		this.cell = cell;
		this.medianProfitIndex = medianProfitIndex;
		this.lastInserted = lastInserted;
		
		
	}

	public AreaWithProfit(Cell cell, Date lastInserted) {
		this.cell = cell;
		this.lastInserted = lastInserted;
		this.medianProfitIndex = BigDecimal.ZERO;
		this.medianProfit = BigDecimal.ZERO;
	
		
	}

	public Cell getCell() {
		return cell;
	}

	public void setCell(Cell cell) {
		this.cell = cell;
	}

	public BigDecimal getMedianProfitIndex() {
		return medianProfitIndex;
	}

	public void setMedianProfitIndex(BigDecimal medianProfit) {
		this.medianProfitIndex = medianProfit;
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
		result = prime * result + ((medianProfitIndex == null) ? 0 : medianProfitIndex.hashCode());
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
		if (cell == other.cell) {
			return true;
		}
		return false;
	}

	@Override
	public int compareTo(AreaWithProfit area) {
		if (this.equals(area)) {
			return 0;
		}

		BigDecimal otherMedian = area.getMedianProfitIndex();
		Date otherTime = area.getLastInserted();

		if (otherMedian == null) {
			return -1;
		} else if (medianProfitIndex == null) {
			return 1;
		}

		int comparison = -medianProfitIndex.compareTo(otherMedian);
		if (comparison != 0) {
			return comparison;
		}

		if (otherTime == null) {
			return -1;
		} else if (lastInserted == null) {
			return 1;
		}

		comparison = -lastInserted.compareTo(otherTime);
		if (comparison != 0) {
			return comparison;
		}

		return -this.cell.compareTo(area.cell);

	}

	public boolean valueEquals(AreaWithProfit other) {
		if(other == null) {
			return false;
		}
		if(other.getCell() != cell) {
			return false;
		}
		if(other.getMedianProfitIndex().compareTo(medianProfitIndex) != 0) {
			return false;
		}
		
		if(other.lastInserted.compareTo(lastInserted) != 0) {
			return false;
		}
		return true;
	}
	@Override
	public String toString() {
		if(medianProfitIndex.scale() != 2) {
			medianProfitIndex.setScale(2, RoundingMode.HALF_UP);
		};
		return "Cell: " + this.cell + " - Median profit index: " + this.medianProfitIndex + " - Dropoff time: " + df.format(lastInserted)
				+ "Delay: " + delay + " ms";
	}
	
	
	public String toStringWithoutDelay(){
		if(medianProfitIndex.scale() != 2) {
			medianProfitIndex.setScale(2, RoundingMode.HALF_UP);
		};
		return "Cell: " + this.cell + " - Median profit index: " + this.medianProfitIndex + " - Dropoff time: " + df.format(lastInserted);
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

	public BigDecimal getMedianProfit() {
		return medianProfit;
	}

	public void setMedianProfit(BigDecimal medianProfit) {
		this.medianProfit = (medianProfit == null) ? BigDecimal.ZERO : medianProfit;

		if (medianProfit.compareTo(BigDecimal.ZERO) > 0) {
			medianProfitIndex = (countOfTaxes == 0) ? medianProfit
					: medianProfit.divide(BigDecimal.valueOf(countOfTaxes), 2, RoundingMode.HALF_UP);
		} else {
			medianProfitIndex = BigDecimal.ZERO;
		}

	}

	public long getCountOfTaxes() {
		return countOfTaxes;
	}

	public void setCountOfTaxes(long countOfTaxes) {
		this.countOfTaxes = countOfTaxes;
		if (medianProfit != null) {
			medianProfitIndex = medianProfit.divide(BigDecimal.valueOf(countOfTaxes == 0 ? 1 : countOfTaxes), 2,
					RoundingMode.HALF_UP);
		} else {
			setMedianProfit(BigDecimal.ZERO);
		}
	}

	public void increaseCountOfTaxes() {
		setCountOfTaxes(countOfTaxes + 1);
	}

	public void decreaseCountOfTaxes() {
		//if (countOfTaxes > 0) {
			setCountOfTaxes(countOfTaxes - 1);
		//}

	}

}
