package onlab.positioning;


import static org.junit.Assert.*;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
 
//import onlab.positioning.Cell;
//import onlab.positioning.CellHelper;
//import onlab.positioning.Coordinate;
public class CellHelperTest {

	private CellHelper cellHelper;
	
	@Before
	public void setUp() throws Exception {
		cellHelper = new CellHelper(BigDecimal.valueOf(-10), BigDecimal.TEN, BigDecimal.ONE, BigDecimal.ONE, 10);

	}

	
	@Test
	public void testGetCellWithEdgeValues() {
		
		Cell cell1 = cellHelper.getCell(new Coordinate(BigDecimal.valueOf(-10.4999), BigDecimal.valueOf(10.4999)));
		Cell cell2 = cellHelper.getCell(new Coordinate(BigDecimal.valueOf(-10.5), BigDecimal.valueOf(10.4999)));
		Cell cell3 = cellHelper.getCell(new Coordinate(BigDecimal.valueOf(-10.4999), BigDecimal.valueOf(10.5)));
		
		Cell cell4 = cellHelper.getCell(new Coordinate(BigDecimal.valueOf(-10.4999), BigDecimal.valueOf(0.50001)));
		Cell cell5 = cellHelper.getCell(new Coordinate(BigDecimal.valueOf(-10.5), BigDecimal.valueOf(0.50001)));
		Cell cell6 = cellHelper.getCell(new Coordinate(BigDecimal.valueOf(-10.4999), BigDecimal.valueOf(0.5)));
		
		Cell cell7 = cellHelper.getCell(new Coordinate(BigDecimal.valueOf(-0.50001), BigDecimal.valueOf(0.50001)));
		Cell cell8 = cellHelper.getCell(new Coordinate(BigDecimal.valueOf(-0.5), BigDecimal.valueOf(0.50001)));
		Cell cell9 = cellHelper.getCell(new Coordinate(BigDecimal.valueOf(-0.50001), BigDecimal.valueOf(0.5)));
		
		Cell cell10 = cellHelper.getCell(new Coordinate(BigDecimal.valueOf(-0.50001), BigDecimal.valueOf(10.4999)));
		Cell cell11 = cellHelper.getCell(new Coordinate(BigDecimal.valueOf(-0.5), BigDecimal.valueOf(10.4999)));
		Cell cell12 = cellHelper.getCell(new Coordinate(BigDecimal.valueOf(-0.50001), BigDecimal.valueOf(10.5)));
		
	
		
		
		
		List<Cell> cells = Arrays.asList(cell1, cell2, cell3, cell4, cell5, cell6, cell7, cell8, cell9, cell10, cell11, cell12);
		
		List<Cell> expectedCells = Arrays.asList(	new Cell(1,1), null, null,
													new Cell(1,10), null, null,
													new Cell(10,10), null, null,
													new Cell(10,1), null, null
												);
		for(int i = 0 ; i < cells.size() ; i++){
			
			assertTrue("Cell"+i, (i%3 == 0)? cells.get(i).equals(expectedCells.get(i)) : cells.get(i) == expectedCells.get(i));
		}
		

	}

}
