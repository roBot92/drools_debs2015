package onlab.event;

import java.text.SimpleDateFormat;
import java.util.Date;

import onlab.positioning.Cell;

public class Route implements Comparable<Route> {

	private Cell pickup_cell;
	private Cell dropoff_cell;
	private long frequency;
	private Date lastDropoffTime;
	private long delay;
	private long insertedForDelay;

	public Route(Cell pickup_Cell, Cell dropoff_Cell, Date lastDropoffTime, long frequency) {
		this.pickup_cell = pickup_Cell;
		this.dropoff_cell = dropoff_Cell;
		this.frequency = frequency;
		this.lastDropoffTime = lastDropoffTime;
	}

	public Cell getPickup_cell() {
		return pickup_cell;
	}

	public void setPickup_cell(Cell pickup_Cell) {
		this.pickup_cell = pickup_Cell;
	}

	public Cell getDropoff_cell() {
		return dropoff_cell;
	}

	public void setDropoff_cell(Cell dropoff_Cell) {
		this.dropoff_cell = dropoff_Cell;
	}

	public long getFrequency() {
		return frequency;
	}

	public void setFrequency(long frequency) {
		this.frequency = frequency;
	}

	public Date getLastDropoffTime() {
		return lastDropoffTime;
	}

	public void setLastDropoffTime(Date lastDropoffTime) {
		this.lastDropoffTime = lastDropoffTime;
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

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((dropoff_cell == null) ? 0 : dropoff_cell.hashCode());
		result = prime * result + (int) (frequency ^ (frequency >>> 32));
		result = prime * result + ((lastDropoffTime == null) ? 0 : lastDropoffTime.hashCode());
		result = prime * result + ((pickup_cell == null) ? 0 : pickup_cell.hashCode());
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
		Route other = (Route) obj;
		if (dropoff_cell == null) {
			if (other.dropoff_cell != null)
				return false;
		} else if (!dropoff_cell.equals(other.dropoff_cell))
			return false;
		if (frequency != other.frequency)
			return false;
		if (lastDropoffTime == null) {
			if (other.lastDropoffTime != null)
				return false;
		} else if (!lastDropoffTime.equals(other.lastDropoffTime))
			return false;
		if (pickup_cell == null) {
			if (other.pickup_cell != null)
				return false;
		} else if (!pickup_cell.equals(other.pickup_cell))
			return false;
		return true;
	}

	@Override
	public int compareTo(Route r) {
		if (this.equals(r)) {
			return 0;
		}
		if (this.frequency < r.frequency) {
			return 1;
		} else if (this.frequency == r.frequency) {
			int result = -(this.lastDropoffTime.compareTo(r.lastDropoffTime));
			if (result == 0) {
				return -1;
			} else {
				return result;
			}
		} else {
			return -1;
		}
	}
	

	@Override
	public String toString() {
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		return "Route - Pickup cell:" + pickup_cell + " Dropoff cell:" + dropoff_cell + " - Frequency:" + frequency
				+ " - Last Dropoff Time: " + df.format(lastDropoffTime) + " Delay: " + delay +" ms";
	}

	
}
