package onlab.positioning;



public class Cell {

	private int x;
	private int y;
	private Coordinate coordinate; // todo kidob?
	
	//This constructor is only for tests!
	public Cell(int x, int y){
		this.x = x;
		this.y = y;
	}
	
	Cell(int x, int y, Coordinate coordinate) {
		this.x = x;
		this.y = y;
		this.coordinate = coordinate;
	}
	
	int getX() {
		return x;
	}
	void setX(int x) {
		this.x = x;
	}
	int getY() {
		return y;
	}
	void setY(int y) {
		this.y = y;
	}

	Coordinate getCoordinate() {
		return coordinate;
	}

	void setCoordinate(Coordinate coordinate) {
		this.coordinate = coordinate;
	}

	@Override
	public String toString() {
		return "Cell("+getX()+","+getY()+")";
	}

	

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((coordinate == null) ? 0 : coordinate.hashCode());
		result = prime * result + x;
		result = prime * result + y;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Cell other = (Cell) obj;
		if (coordinate == null) {
			if (other.coordinate != null)
				return false;
		} else if (!coordinate.equals(other.coordinate))
			return false;
		if (x != other.x)
			return false;
		if (y != other.y)
			return false;
		return true;
	}
	
	
	
	
	
	
	
}
