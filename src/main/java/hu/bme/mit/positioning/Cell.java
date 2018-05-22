package hu.bme.mit.positioning;



public class Cell implements Comparable<Cell>{

	private int x;
	private int y;

	
	public Cell(int x, int y) {
		this.x = x;
		this.y = y;
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

	

	@Override
	public String toString() {
		return "Cell("+getX()+","+getY()+")";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
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
		if (x != other.x)
			return false;
		if (y != other.y)
			return false;
		return true;
	}

	/**
	 * Dictionary-like comparison
	 */
	@Override
	public int compareTo(Cell o) {
		if(this.x > o.x) {
			return 1;
		}
		if(this.x < o.x) {
			return -1;
		}
		
		if(this.y > o.y) {
			return 1;
		}
		if(this.y < o.y) {
			return -1;
		}
		return 0;
		
		
	}

	


	
	
	
	
	
	
	
	
}
