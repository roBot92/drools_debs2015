package onlab.event;

public class Tick {

	public long currentTime;

	public Tick(long currentTime) {
		this.currentTime = currentTime;
	}

	public long getCurrentTime() {
		return currentTime;
	}

	public void setCurrentTime(long currentTime) {
		this.currentTime = currentTime;
	}
	
}
