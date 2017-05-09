package onlab.event;

import java.util.Date;

import onlab.positioning.Cell;

public class RefreshArea {

	private Cell cell;
	private Date currentTime;
	private long insertedForDelay;
	
	
	public RefreshArea(Cell cell, Date currentTime, long inserted) {
		this.cell = cell;
		this.currentTime = currentTime;
		this.insertedForDelay = inserted;
	}
	public Cell getCell() {
		return cell;
	}
	public void setCell(Cell cell) {
		this.cell = cell;
	}
	public Date getCurrentTime() {
		return currentTime;
	}
	public void setCurrentTime(Date currentTime) {
		this.currentTime = currentTime;
	}
	public long getInsertedForDelay() {
		return insertedForDelay;
	}
	public void setInsertedForDelay(long insertedForDelay) {
		this.insertedForDelay = insertedForDelay;
	}
	
	
	
	
}
