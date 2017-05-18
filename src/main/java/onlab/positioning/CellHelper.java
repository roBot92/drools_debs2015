package onlab.positioning;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.Map;

public class CellHelper {

	private class Tuple {
		int x;
		int y;

		public Tuple(int x, int y) {
			this.x = x;
			this.y = y;
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + getOuterType().hashCode();
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
			Tuple other = (Tuple) obj;
			if (!getOuterType().equals(other.getOuterType()))
				return false;
			if (x != other.x)
				return false;
			if (y != other.y)
				return false;
			return true;
		}

		private CellHelper getOuterType() {
			return CellHelper.this;
		}

	}

	private Map<Tuple, Cell> cells = new HashMap<Tuple, Cell>();

	private Coordinate FIRST_CELL_COORDINATE;
	private BigDecimal xShift;
	private BigDecimal yShift;
	private int resolution;

	public CellHelper(BigDecimal firstX, BigDecimal firstY, BigDecimal xShift, BigDecimal yShift, int resolution) {

		initialize(firstX, firstY, xShift, yShift, resolution);

	}

	public void reInitialize(BigDecimal firstX, BigDecimal firstY, BigDecimal xShift, BigDecimal yShift,
			int resolution) {
		initialize(firstX, firstY, xShift, yShift, resolution);
	}

	private void initialize(BigDecimal firstX, BigDecimal firstY, BigDecimal xShift, BigDecimal yShift,
			int resolution) {
		FIRST_CELL_COORDINATE = new Coordinate(firstX, firstY);
		this.xShift = xShift;
		this.yShift = yShift;
		this.resolution = resolution;

		cells = new HashMap<Tuple, Cell>();

		for (int i = 1; i <= resolution; i++) {
			for (int j = 1; j <= resolution; j++) {

				cells.put(new Tuple(i, j), new Cell(i, j));
			}
		}
	}

	public Cell getCell(Coordinate coordinate) {

		BigDecimal yDifference = FIRST_CELL_COORDINATE.getY().subtract(coordinate.getY());
		BigDecimal xDifference = coordinate.getX().subtract(FIRST_CELL_COORDINATE.getX());

		int tupleY = yDifference.divide(yShift, BigDecimal.ROUND_HALF_UP).setScale(0, RoundingMode.HALF_UP).intValue()
				+ 1;
		int tupleX = xDifference.divide(xShift, BigDecimal.ROUND_HALF_UP).setScale(0, RoundingMode.HALF_UP).intValue()
				+ 1;

		if (tupleY < 1 || tupleY > resolution || tupleX < 1 || tupleY > resolution) {
			return null;
		}

		return cells.get(new Tuple(tupleX, tupleY));

	}

}
