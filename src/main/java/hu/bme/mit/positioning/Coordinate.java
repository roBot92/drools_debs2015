package hu.bme.mit.positioning;

import java.math.BigDecimal;

public class Coordinate {

	private BigDecimal x;
	private BigDecimal y;

	public Coordinate(BigDecimal x, BigDecimal y) {
		this.x = x;
		this.y = y;
	}

	public BigDecimal getX() {
		return x;
	}

	public BigDecimal getY() {
		return y;
	}

	
	
	

}
