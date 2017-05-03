package test;


import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import onlab.positioning.Cell;
import onlab.positioning.CellHelper;
import onlab.positioning.Coordinate;

@SuppressWarnings("restriction")
public class CellHelperTest {

	private CellHelper cellHelper;
	private  Path testFile;
	
	@Before
	public void setUp() throws Exception {
		testFile = Paths.get("testCSV.csv");
		List<String> lines = Arrays.asList(
				"F8B0B95E14882D63C9FE3C880158C463,CCEA1D19ED163EBD24AE2B44D35A45B5,2013-01-01 00:15:00,2013-01-01 00:24:00,540,1.28,-10,9,-8.6,9,CRD,7.00,0.50,0.50,1.50,0.00,9.50",
				"FB044FA253630BF36002695B99406E81,B03D4C6AD99CDD6D0BD3E2ECD97DAC6D,2013-01-01 00:17:00,2013-01-01 00:24:00,420,1.48,0,0,-1,2,CSH,7.00,0.50,0.50,0.00,0.00,8.00",
				"FB132F8415ADC48146FFB35E2EC2A3C4,28B3DC931EEC8E22D221F596D07284CF,2013-01-01 00:18:00,2013-01-01 00:24:00,360,1.05,-73.990273,40.737083,-73.984077,40.737495,CRD,5.50,0.50,0.50,1.00,0.00,7.50",
				"FB84C95C217D345556E3B14EA6D63E5C,F9B8CC42051C6D1C1DDAF5260118D585,2013-01-01 00:19:00,2013-01-01 00:24:00,300,0.99,-73.956215,40.778702,-73.948227,40.782135,CSH,6.00,0.50,0.50,0.00,0.00,7.00",
				"FBC62923BA6B622E85C0FC9821AD83D5,9F494D9A695D9717A0B23DBF096601CF,2013-01-01 00:15:00,2013-01-01 00:24:00,540,1.78,-73.992569,40.741211,-73.995773,40.759300,CRD,8.50,0.50,0.50,1.80,0.00,11.30"
				
				);
		
		
		Files.write(testFile, lines);
		
		cellHelper = new CellHelper(BigDecimal.valueOf(-10), BigDecimal.TEN, BigDecimal.ONE, BigDecimal.ONE, 10);
		
	}

	@After
	public void tearDown() throws Exception {
		Files.delete(testFile);
		
	}

	@Test
	public void testWithValidValues() {
		/*Cell cell1 = cellHelper.getCell(new Coordinate(BigDecimal.valueOf(-5), BigDecimal.valueOf(5)));
		Cell cell2 = cellHelper.getCell(new Coordinate(BigDecimal.valueOf(-1.6), BigDecimal.valueOf(4.1)));
		Cell cell3 = cellHelper.getCell(new Coordinate(BigDecimal.valueOf(-0.501), BigDecimal.valueOf(6)));
		Cell cell4 = cellHelper.getCell(new Coordinate(BigDecimal.valueOf(-6.49), BigDecimal.valueOf(6.5)));
		Cell cell5 = cellHelper.getCell(new Coordinate(BigDecimal.valueOf(-10.49999), BigDecimal.valueOf(10.499999)));
		Cell cell6 = cellHelper.getCell(new Coordinate(BigDecimal.valueOf(-0.501), BigDecimal.valueOf(0.5)));
		*/
		//Assert.assertArrayEquals(Arrays.asList(new Cell()), actuals);
		
	//	List<Cell> cells = Arrays.asList(cell1, cell2, cell3, cell4, cell5, cell6);
	    //Assert.assertArrayEquals([new Cell(6,6)], actuals);

	}

}
