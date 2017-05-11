package onlab.event;

import java.util.Date;

import onlab.positioning.Cell;

public class ActivateRoute {

	private Cell pickup_cell;
	private Cell dropoff_cell;
	private Date lastInserted;
	
	
	
	public ActivateRoute(Cell pickup_cell, Cell dropoff_cell, Date lastInserted) {
		this.pickup_cell = pickup_cell;
		this.dropoff_cell = dropoff_cell;
		this.lastInserted = lastInserted;
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
	public Date getLastInserted() {
		return lastInserted;
	}
	public void setLastInserted(Date lastInserted) {
		this.lastInserted = lastInserted;
	}
	
	
}
