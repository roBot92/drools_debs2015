package onlab.event;

public class Tick {

	public long currentTime;
	public long inserted;

	public Tick(long currentTime) {
		this.currentTime = currentTime;
	}

	
	public Tick(long currentTime, long inserted) {
		this.currentTime = currentTime;
		this.inserted = inserted;
	}


	public long getCurrentTime() {
		return currentTime;
	}

	public void setCurrentTime(long currentTime) {
		this.currentTime = currentTime;
	}


	public long getInserted() {
		return inserted;
	}


	public void setInserted(long inserted) {
		this.inserted = inserted;
	}
	
	
	
}
