package markovSim.FunctionMatrixCreation;

import java.io.File;

import java.io.FileInputStream;
import java.util.HashMap;
import java.util.Iterator;

import org.apache.poi.*;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;


/**
 * Given a spreadsheet in proper format, this class creates two matrices representing the transition functions of the Markov Simulator.
 * To do this, it:
 * 	- Reads the spreadsheet
 * 	- Passes the equations to be parsed into EquationParser
 * 	- Calculates 
 * 
 * @author 2024351h
 *
 */
public class FunctionMatrixCreator {
	private String sheetPath;
	private String[] funcEqs, logEqs; // Initialised in readSheet()
	private double[][] funcMatrix, logMatrix; // Initialised in readSheet()
	private HashMap<String, Integer> symbols; // Initialised in readSheet()


	public FunctionMatrixCreator(String sheet) {
		sheetPath = sheet;
		funcMatrix = null;
		logMatrix = null;

		createMatrices();
		printEqs();
	}

	private void createMatrices() {
		readSheet();
	}

	/**
	 * Reads a spreadsheet and stores int's values in the appropriate data structure (symbols, funcEqs, logEqs)
	 * Code copied from the web - Modified slightly
	 * http://howtodoinjava.com/apache-commons/readingwriting-excel-files-in-java-poi-tutorial/
	 */
	private void readSheet() {
		try
		{
			
			FileInputStream file = new FileInputStream(new File(sheetPath));
			XSSFWorkbook workbook = new XSSFWorkbook(file);
			XSSFSheet sheet = workbook.getSheetAt(0);
			System.out.println("HERE");
			// Now that sheet has been loaded instance variable are initialised with correct size
			int variableNumber = (sheet.getPhysicalNumberOfRows() - 1);
			funcEqs = new String[variableNumber];
			logEqs = new String[variableNumber];
			symbols = new HashMap<String, Integer>(variableNumber);
			funcMatrix = new double[variableNumber*5][variableNumber*5];
			logMatrix = new double[variableNumber*5][variableNumber*5];

			
			Iterator<Row> rowIterator = sheet.iterator();
			System.out.println("HERE2");
			int i = -1; // Starts at -1 so that it skips 
			while (rowIterator.hasNext()) {
				if (i > -1) {
					Row row = rowIterator.next();
					Iterator<Cell> cellIterator = row.cellIterator();

					int j = 0;
					while (j < 5) {
						Cell cell = cellIterator.next();
						if (j == 2) { // Symbol String
							symbols.put(cell.getStringCellValue(), i);
						} else if (j == 3) { // Function Matrix
							if (cell == null || cell.getCellType() == cell.CELL_TYPE_BLANK) {
								funcEqs[i] = row.getCell(2).getStringCellValue();
							} else {
								funcEqs[i] = cell.getStringCellValue();
							}
						} else if(j == 4) { // Log Matrix
							if (cell == null || cell.getCellType() == cell.CELL_TYPE_BLANK) {
								logEqs[i] = row.getCell(2).getStringCellValue();
							} else {
								logEqs[i] = cell.getStringCellValue();
							}
						}
						j++;
					}
				}
				i++;
			}
			file.close();
			workbook.close();
		} 
		catch (Exception e) 
		{
			e.printStackTrace();
			System.out.println("FUCK");
		}
	}




	public double[][] getFuncMatrix() {
		if (funcMatrix != null) {
			return funcMatrix;
		} else {
			throw new NullPointerException("Tried to get funcMatrix from FunctionMatrixCreator, but it was null!");
		}
	}

	public double[][] getLogMatrix() {
		if (logMatrix != null) {
			return logMatrix;
		} else {
			throw new NullPointerException("Tried to get logMatrix from FunctionMatrixCreator, but it was null!");
		}
	}
	
	private void printEqs() {
		System.out.println(funcEqs.length);
		for (String s : funcEqs) {
			System.out.println(s);
		}
	}
}
