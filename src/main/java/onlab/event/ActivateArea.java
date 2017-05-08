package onlab.event;

import java.util.Date;

import onlab.positioning.Cell;

public class ActivateArea {
	private Cell cell;
	private Date currentTime;

	public ActivateArea(Cell cell, Date currentTime) {
		this.cell = cell;
		this.currentTime = currentTime;
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
	
	
	
}