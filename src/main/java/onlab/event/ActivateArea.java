package onlab.event;

import onlab.positioning.Cell;

public class ActivateArea {
	private Cell cell;

	public ActivateArea(Cell cell) {
		this.cell = cell;
	}

	public Cell getCell() {
		return cell;
	}

	public void setCell(Cell cell) {
		this.cell = cell;
	}
	
}
