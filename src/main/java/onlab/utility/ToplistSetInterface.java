package onlab.utility;



public interface ToplistSetInterface {
	public long getAverageDelay();

	public long getMaxDelay();

	public long getMinDelay();
	
	public String toStringWithoutDelay();
}
