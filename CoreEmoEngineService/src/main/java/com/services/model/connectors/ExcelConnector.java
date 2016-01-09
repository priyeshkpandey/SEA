package com.services.model.connectors;

import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;



public class ExcelConnector {
	
    Workbook sourceWorkbook;
	Sheet sourceSheet;
	
	ExcelConnector()
	{
		sourceWorkbook = new XSSFWorkbook();
		sourceSheet = sourceWorkbook.createSheet("Source1");
	}
	
	ExcelConnector(String sheetName)
	{
		sourceWorkbook = new XSSFWorkbook();
		sourceSheet = sourceWorkbook.createSheet(sheetName);
	}
	
	ExcelConnector(String workbookPath, String sheetName)
	{
		
	}
	
	public boolean openWorkbook()
	{
		
		return true;
	}

}
