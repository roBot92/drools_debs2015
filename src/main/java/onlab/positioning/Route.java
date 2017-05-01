package onlab.positioning;

import java.text.SimpleDateFormat;
import java.util.Date;

import onlab.positioning.Cell;

public class Route implements Comparable<Route> {

	private Cell pickup_Cell;
	private Cell dropoff_Cell;
	private long frequency;
	private Date lastDropoffTime;

	public Route(Cell pickup_Cell, Cell dropoff_Cell, Date lastDropoffTime, long frequency) {
		this.pickup_Cell = pickup_Cell;
		this.dropoff_Cell = dropoff_Cell;
		this.frequency = frequency;
		this.lastDropoffTime = lastDropoffTime;
	}

	public Cell getPickup_Cell() {
		return pickup_Cell;
	}

	public void setPickup_Cell(Cell pickup_Cell) {
		this.pickup_Cell = pickup_Cell;
	}

	public Cell getDropoff_Cell() {
		return dropoff_Cell;
	}

	public void setDropoff_Cell(Cell dropoff_Cell) {
		this.dropoff_Cell = dropoff_Cell;
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



	

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((dropoff_Cell == null) ? 0 : dropoff_Cell.hashCode());
		result = prime * result + (int) (frequency ^ (frequency >>> 32));
		result = prime * result + ((lastDropoffTime == null) ? 0 : lastDropoffTime.hashCode());
		result = prime * result + ((pickup_Cell == null) ? 0 : pickup_Cell.hashCode());
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
		if (dropoff_Cell == null) {
			if (other.dropoff_Cell != null)
				return false;
		} else if (!dropoff_Cell.equals(other.dropoff_Cell))
			return false;
		if (frequency != other.frequency)
			return false;
		if (lastDropoffTime == null) {
			if (other.lastDropoffTime != null)
				return false;
		} else if (!lastDropoffTime.equals(other.lastDropoffTime))
			return false;
		if (pickup_Cell == null) {
			if (other.pickup_Cell != null)
				return false;
		} else if (!pickup_Cell.equals(other.pickup_Cell))
			return false;
		return true;
	}

	@Override
	public int compareTo(Route r) {
		if (this.frequency < r.frequency) {
			return 1;
		} else if (this.frequency == r.frequency) {
			int result = -(this.lastDropoffTime.compareTo(r.lastDropoffTime));
			if(result == 0){
				return -1;
			}
			else{
				return result;
			}
		} else {
			return -1;
		}
	}

	@Override
	public String toString() {
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
		return "Route - Pickup cell:" + pickup_Cell + " Dropoff cell:" + dropoff_Cell + " - Frequency:" + frequency
				+ " - Last Dropoff Time: " + df.format(lastDropoffTime);
	}

}
