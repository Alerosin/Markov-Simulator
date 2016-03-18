package markovSim.FunctionMatrixCreation;

import java.io.File;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.Arrays;
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
 * 	- Reads the spreadsheet and stores the equations and symbols as strings
 * 	- Passes the equations to be parsed into EquationParser
 * 	- Calculates 
 * 
 * @author 2024351h
 *
 */
public class FunctionMatrixCreator {
	private String sheetPath;
	private double[][] funcMatrix, logMatrix; // Initialised in readSheet()
	
	protected HashMap<String, Integer> symbols; // Initialised in readSheet()
	protected String[] funcEqs, logEqs; // Initialised in readSheet()



	public FunctionMatrixCreator(String sheet) {
		sheetPath = sheet;
		funcMatrix = null;
		logMatrix = null;

		readSheet();
		convertInfixToPostfix();
		createMatrices();
		
		writeMatricesToFile();
	}

	private void createMatrices() {
		EquationSolver es = new EquationSolver(this);
		double[][] matrixRows = new double[2][funcEqs.length];
		
		for (int i = 0; i < funcEqs.length; i++) {
			matrixRows = es.getMatrixRow(i);
			copyRowIntoMatrix(i, matrixRows[0], funcMatrix);
			copyRowIntoMatrix(i, matrixRows[1], logMatrix);
		}
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

			// Now that sheet has been loaded instance variable are initialised with correct size
			int numberOfRows = (sheet.getPhysicalNumberOfRows()-1);
			funcEqs = new String[numberOfRows];
			logEqs = new String[numberOfRows];
			symbols = new HashMap<String, Integer>(numberOfRows);
			funcMatrix = new double[numberOfRows][numberOfRows];
			logMatrix = new double[numberOfRows][numberOfRows];

			Iterator<Row> rowIterator = sheet.iterator();
			Row row = rowIterator.next();

			for (int i = 0; i < numberOfRows; i++) {
				row = rowIterator.next();
				
				Cell symbol = row.getCell(2);
				symbols.put(symbol.getStringCellValue(), i);
				
				Cell c = row.getCell(3);
				if (c == null || c.getCellType() == Cell.CELL_TYPE_BLANK) {
					funcEqs[i] = symbol.getStringCellValue();
				} else {
					funcEqs[i] = c.getStringCellValue();
				}
				
				c = row.getCell(4);
				if (c == null || c.getCellType() == Cell.CELL_TYPE_BLANK) {
					logEqs[i] = symbol.getStringCellValue();
				} else {
					logEqs[i] = c.getStringCellValue();
				}
			}
			
			file.close();
			workbook.close();
		} 
		catch (Exception e) 
		{
			e.printStackTrace();
		}
	}


	private void convertInfixToPostfix() {
		for (int i = 0; i < funcEqs.length; i++) {
			funcEqs[i] = ShuntingYard.postfix(funcEqs[i]);
			
			logEqs[i] = ShuntingYard.postfix(logEqs[i]);
		}
	}
	
	private void copyRowIntoMatrix(int rowIndex, double[] row, double[][] matrix) {
		matrix[rowIndex] = Arrays.copyOf(row, row.length);
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
		for (String s : funcEqs) {
			System.out.println(s);
		}
		for (String s : logEqs) {
			System.out.println(s);
		}
	}
	
	private void writeMatricesToFile() {
		PrintWriter writer;
		try {
			writer = new PrintWriter("functionMatrix.txt", "UTF-8");
			
			for (int i = 0; i < funcMatrix.length; i++) {
				for (int j = 0; j < funcMatrix.length; j++) {
					writer.print(funcMatrix[i][j]);
					System.out.println(j);
					if (j == funcMatrix.length-1) {
						writer.print("\n");
					} else {
						writer.print(" ");
					}
				}
			}
			writer.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		
		try {
			writer = new PrintWriter("logFunctionMatrix.txt", "UTF-8");
			
			for (int i = 0; i < logMatrix.length; i++) {
				for (int j = 0; j < logMatrix.length; j++) {
					writer.print(logMatrix[i][j]);
					if (j == logMatrix.length-1) {
						writer.print("\n");
					} else {
						writer.print(" ");
					}
				}
			}
			writer.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
	}
}
