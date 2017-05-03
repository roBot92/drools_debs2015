package onlab.event;

import java.util.Date;

import onlab.positioning.Cell;

public class RefreshRoute {

	private Cell pickup_cell;
	private Cell dropoff_cell;
	private Date currentTime;
	
	
	public RefreshRoute(Cell pickup_cell, Cell dropoff_cell, long current_time) {
		this.pickup_cell = pickup_cell;
		this.dropoff_cell = dropoff_cell;
		this.currentTime = new Date(current_time);
	}
	public Cell getPickup_cell() {
		return pickup_cell;
	}
	public void setPickup_cell(Cell pickup_cell) {
		this.pickup_cell = pickup_cell;
	}
	public Cell getDropoff_cell() {
		return dropoff_cell;
	}
	public void setDropoff_cell(Cell dropoff_cell) {
		this.dropoff_cell = dropoff_cell;
	}
	public Date getCurrentTime() {
		return currentTime;
	}
	public void setCurrentTime(Date current_time) {
		this.currentTime = current_time;
	}
	
	
	
	
}
