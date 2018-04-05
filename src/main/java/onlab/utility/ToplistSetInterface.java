package onlab.utility;

import onlab.positioning.Cell;

public interface ToplistSetInterface {
	public long getAverageDelay();

	public long getMaxDelay();

	public long getMinDelay();

	public String toStringWithoutDelay();

	public void refreshDelayTimes();

	public void refreshInsertedForDelay(long insertedForDelay, Cell... cells);

	public void clear();

}
