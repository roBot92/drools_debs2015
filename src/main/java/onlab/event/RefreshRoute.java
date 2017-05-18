package onlab.event;



import onlab.positioning.Cell;

public class RefreshRoute {

	private Cell pickup_cell;
	private Cell dropoff_cell;
	private long currentTime;
	private long insertedForDelay;
	
	public RefreshRoute(Cell pickup_cell, Cell dropoff_cell, long current_time, long insertedForDelay) {
		this.pickup_cell = pickup_cell;
		this.dropoff_cell = dropoff_cell;
		this.currentTime = current_time;
		this.insertedForDelay = insertedForDelay;
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
	public long getCurrentTime() {
		return currentTime;
	}
	public void setCurrentTime(long current_time) {
		this.currentTime = current_time;
	}
	public long getInsertedForDelay() {
		return insertedForDelay;
	}
	public void setInsertedForDelay(long insertedForDelay) {
		this.insertedForDelay = insertedForDelay;
	}
	
	
	
	
}
