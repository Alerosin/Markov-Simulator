package markovSim.FunctionMatrixCreation;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.HashMap;
import java.util.Iterator;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CreationHelper;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class SpreadSheetExtender {
	private String sheetPath;

	public SpreadSheetExtender(String sheetPath) {
		this.sheetPath = sheetPath;

		extendSheet();
	}


	private void extendSheet() {
		try
		{
			FileInputStream file = new FileInputStream(new File(sheetPath + ".xlsx"));
			XSSFWorkbook workbook = new XSSFWorkbook(file);
			XSSFSheet sheet = workbook.getSheetAt(0);

			XSSFWorkbook exBook = new XSSFWorkbook();
			XSSFSheet exSheet = exBook.createSheet();

			// Create all rows in the new sheet, and copy the values for column 0, 2, 3 & 4 into it.
			int numberOfRows = (sheet.getPhysicalNumberOfRows()-1);
			exSheet.createRow(0);
			for (int i = 0; i < numberOfRows*5; i++) {
				exSheet.createRow(i+1);
				Cell c = exSheet.getRow(i+1).createCell(0);
				c.setCellValue(i);
			}				


			String[] nsew = {"n", "s", "e", "w"};

			for (int i = 1; i < numberOfRows+1; i++) {
				// Copy values from columns 0, 2, 3, 4, 5 & 6 into the new spreadsheet
				Cell c = exSheet.getRow(i).createCell(2);
				c.setCellValue(sheet.getRow(i).getCell(2).getStringCellValue());
				
				c = exSheet.getRow(i).createCell(3);
				if (sheet.getRow(i).getCell(3) == null || sheet.getRow(i).getCell(3).getCellType() == Cell.CELL_TYPE_BLANK) {
					c.setCellValue(sheet.getRow(i).getCell(2).getStringCellValue());
				} else {
					c.setCellValue(sheet.getRow(i).getCell(3).getStringCellValue());
				}

				c = exSheet.getRow(i).createCell(4);
				if (sheet.getRow(i).getCell(4) == null || sheet.getRow(i).getCell(4).getCellType() == Cell.CELL_TYPE_BLANK) {
					c.setCellValue(sheet.getRow(i).getCell(2).getStringCellValue());
				} else {
					c.setCellValue(sheet.getRow(i).getCell(4).getStringCellValue());
				}
				
				c = exSheet.getRow(i).createCell(5);
				if (sheet.getRow(i).getCell(5) == null || sheet.getRow(i).getCell(5).getCellType() == Cell.CELL_TYPE_BLANK) {
					c.setCellValue(0);
				} else {
					c.setCellValue(sheet.getRow(i).getCell(5).getNumericCellValue());
				}
				
				c = exSheet.getRow(i).createCell(6);
				if (sheet.getRow(i).getCell(6) == null || sheet.getRow(i).getCell(6).getCellType() == Cell.CELL_TYPE_BLANK) {
					c.setCellValue(0);
				} else {
					c.setCellValue(sheet.getRow(i).getCell(6).getNumericCellValue());
				}
				
				// Extend columns 0, 2, 3, 4, 5 & 6
				int mult = 1;
				for (String s : nsew) {
					c = exSheet.getRow(mult*numberOfRows + i).createCell(2);
					c.setCellValue(s + sheet.getRow(i).getCell(2).getStringCellValue());
					
					
					c = exSheet.getRow(mult*numberOfRows + i).createCell(3);
					c.setCellValue(exSheet.getRow(mult*numberOfRows + i).getCell(2).getStringCellValue());
					c = exSheet.getRow(mult*numberOfRows + i).createCell(4);
					c.setCellValue(exSheet.getRow(mult*numberOfRows + i).getCell(2).getStringCellValue());
					c = exSheet.getRow(mult*numberOfRows + i).createCell(5);
					c.setCellValue(0);
					c = exSheet.getRow(mult*numberOfRows + i).createCell(6);
					c.setCellValue(0);
					mult++;
				}
			}

			file.close();
			workbook.close();

			// Write the output to a file
			FileOutputStream fileOut = new FileOutputStream(sheetPath + "_EXTENDED.xlsx");
			exBook.write(fileOut);
			fileOut.close();
		} 
		catch (Exception e) 
		{
			e.printStackTrace();
			System.exit(-1);
		}
	}
}
