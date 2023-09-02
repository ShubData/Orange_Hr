package utility;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class XLS_Reader {
	public String path = "";
	FileInputStream fin = null;
	public static String ext;
	public Workbook wb;
	public Sheet sheet;
	public Row row = null;
	public Cell cell = null;

	public XLS_Reader(String path) {
		this.path = path;
		File file = new File(path);
		try {
			if (file.exists()) {
				fin = new FileInputStream(file);
				ext = path.substring(path.indexOf("."));
				if (ext.equals(".xlsx"))
					wb = new XSSFWorkbook(fin);
				else if (ext.equals(".xls"))
					wb = new HSSFWorkbook(fin);
				fin.close();
			} else {
				ext = path.substring(path.indexOf("."));
				if (ext.equals(".xlsx"))
					wb = new XSSFWorkbook();
				else if (ext.equals(".xls"))
					wb = new HSSFWorkbook();

			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	public int getRowCount(String sheetName) {
		int index = wb.getSheetIndex(sheetName);
		if (index == -1)
			return 0;
		else {
			sheet = wb.getSheetAt(index);
			return sheet.getLastRowNum() + 1;
		}

	}

	public int getColCount(String sheetName) {
		sheet = wb.getSheet(sheetName);
		row = sheet.getRow(0);
		return row.getLastCellNum();
	}

	public String getCellData(String sheetName, int colNum, int rowNum) {

		int index = wb.getSheetIndex(sheetName);
		if (index == -1)
			return "";
		sheet = wb.getSheetAt(index);
		row = sheet.getRow(rowNum);
		if (row == null)
			return "";
		cell = row.getCell(colNum);
		if (cell == null)
			return "";
		if (cell.getCellType() == CellType.STRING)
			return cell.getStringCellValue();
		else if (cell.getCellType() == CellType.NUMERIC || cell.getCellType() == CellType.FORMULA)
			return String.valueOf(cell.getNumericCellValue());
		else if (cell.getCellType() == CellType.BOOLEAN)
			return String.valueOf(cell.getBooleanCellValue());
		else
			return "";
	}

}
