package com.pay10.pg.core.fraudPrevention.model;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.CreationHelper;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FraudTxnExcelWriter {

	private static final Logger logger = LoggerFactory.getLogger(FraudTxnExcelWriter.class.getName());

	public static File writeExcel(List<FraudTxnExcelModel> transactions) {

		try {
			Workbook xlsFile = new XSSFWorkbook();
			CreationHelper helper = xlsFile.getCreationHelper();
			Sheet sheet = xlsFile.createSheet();

			// write header for the excel
			writeHeaderRow(sheet, helper);

			// write data for the excel
			writeData(transactions, sheet, helper);

			String fileName = StringUtils.join("Transactions-", System.currentTimeMillis());
			File outputFile = File.createTempFile(fileName, ".xlsx");
			try (OutputStream stream = new FileOutputStream(outputFile)) {
				xlsFile.write(stream);
			}
			xlsFile.close();
			return outputFile;

		} catch (Exception ex) {
			logger.error("writeExcel:: failed.", ex);
			return null;
		}
	}

	private static void writeHeaderRow(Sheet sheet, CreationHelper helper) {
		Row row = sheet.createRow(0);
		int cell = 0;
		for (String header : FraudTxnExcelModel.getHeaderName()) {
			row.createCell(cell).setCellValue(helper.createRichTextString(header));
			cell++;
		}

	}

	private static void writeData(List<FraudTxnExcelModel> transactions, Sheet sheet, CreationHelper helper) {

		int rowCount = 1;
		for (FraudTxnExcelModel transaction : transactions) {
			Row row = sheet.createRow(rowCount);
			int cell = 0;
			for (Object data : transaction.excelData()) {
				row.createCell(cell).setCellValue(helper.createRichTextString(String.valueOf(data)));
				cell++;
			}
			rowCount++;
		}
	}

}
