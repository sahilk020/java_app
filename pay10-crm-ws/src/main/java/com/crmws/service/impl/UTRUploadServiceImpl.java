
package com.crmws.service.impl;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.crmws.service.UTRUploadService;
import com.itextpdf.text.log.SysoCounter;

@Service
public class UTRUploadServiceImpl implements UTRUploadService {
	private static final Logger logger = LoggerFactory.getLogger(UTRUploadServiceImpl.class.getName());
	@Autowired
	private MongoTransactionDetails txnDetails;
	private String return_msg="";
	@SuppressWarnings("deprecation")
	@Override
	public String save(MultipartFile file) {
		return_msg="";
		try {
			// logger.info("List of excel data" + entity.toString());
			XSSFWorkbook workbook = new XSSFWorkbook(file.getInputStream());

			logger.info("=> sheet reading file");

			workbook.forEach(sheet -> {
				System.out.println("=> sheet " + sheet.getSheetName());
				Map<Integer, String> map = new HashMap<Integer, String>();
				Cell currentCell = null;
				for (int i = 0; i < sheet.getPhysicalNumberOfRows(); i++) {
					String txnId = null;
					String utrNo = null;
					String utrDate = null;
					Row currentRow = sheet.getRow(i);
					int cellsInRow = currentRow != null ? currentRow.getPhysicalNumberOfCells() : 0;
					if (i == 0) {
						for (int j = 0; j < cellsInRow; j++) {
							currentCell = currentRow.getCell(j);
							if (currentCell != null)
								map.put(currentCell.getColumnIndex(), currentCell.getStringCellValue());
						}
					} else if (cellsInRow > 0) {
						for (Integer key : map.keySet()) {
							currentCell = currentRow.getCell(key);
							if (currentCell != null) {
								if (map.get(key).equalsIgnoreCase("TXN_ID")) {
									if (currentCell.getCellTypeEnum() == CellType.STRING) {
										txnId = String.valueOf(currentRow.getCell(key).getStringCellValue());
									} else if (currentCell.getCellTypeEnum() == CellType.NUMERIC) {
										txnId = String.valueOf(currentRow.getCell(key).getNumericCellValue() + "");
										if (txnId != null && txnId.length() > 0 && txnId.contains("E")) {
											BigDecimal bddouble = new BigDecimal(txnId);
											txnId = "" + bddouble.longValue();
										}
									}
								}
								if (map.get(key).equalsIgnoreCase("UTR_NO")) {
									if (currentCell.getCellTypeEnum() == CellType.STRING) {
										utrNo = String.valueOf(currentRow.getCell(key).getStringCellValue());
									} else if (currentCell.getCellTypeEnum() == CellType.NUMERIC) {
										utrNo = String.valueOf(currentRow.getCell(key).getNumericCellValue() + "");
										if (utrNo != null && utrNo.length() > 0 && utrNo.contains("E")) {
											BigDecimal bddouble = new BigDecimal(utrNo);
											utrNo = "" + bddouble.longValue();
										}
									}
								}
								if (map.get(key).equalsIgnoreCase("UTR_Date")) {
									DataFormatter dataFormatter = new DataFormatter();
									Cell cellValue = currentRow.getCell(currentCell.getColumnIndex());
									String formattedCellStr = dataFormatter.formatCellValue(cellValue);
									utrDate = formattedCellStr;
								}
							}
						}
						
						if (txnId != null&&!txnId.isEmpty() &&!utrNo.isEmpty() && utrNo != null && utrDate != null &&!utrDate.isEmpty()) {
							logger.info("Update TXN details :" + txnId + utrNo + utrDate);
							logger.info("Calling mongo method now");
							return_msg = txnDetails.updateTxnByTxnIDForUTRUpdateInTransactionStatusColl(txnId,
									utrNo, utrDate);
						}
					}
				}
			});
			workbook.close();
		} catch (Exception e) {
			return_msg ="Error while uploading UTR file";
			throw new RuntimeException("Error while uploading UTR file");
		}
		
		return return_msg;
	}

	@Override
	public String saveChargeback(MultipartFile file,String type) {
		return_msg="";
		try {
			// logger.info("List of excel data" + entity.toString());
			XSSFWorkbook workbook = new XSSFWorkbook(file.getInputStream());

			logger.info("=> sheet reading file");

			workbook.forEach(sheet -> {
				System.out.println("=> sheet " + sheet.getSheetName());
				Map<Integer, String> map = new HashMap<Integer, String>();
				Cell currentCell = null;
				for (int i = 0; i < sheet.getPhysicalNumberOfRows(); i++) {
					String txnId = null;
					String utrNo = null;
					String utrDate = null;
					Row currentRow = sheet.getRow(i);
					int cellsInRow = currentRow != null ? currentRow.getPhysicalNumberOfCells() : 0;
					if (i == 0) {
						for (int j = 0; j < cellsInRow; j++) {
							currentCell = currentRow.getCell(j);
							if (currentCell != null)
								map.put(currentCell.getColumnIndex(), currentCell.getStringCellValue());
						}
					} else if (cellsInRow > 0) {
						for (Integer key : map.keySet()) {
							currentCell = currentRow.getCell(key);
							if (currentCell != null) {
								if (map.get(key).equalsIgnoreCase("TXN_ID")) {
									if (currentCell.getCellTypeEnum() == CellType.STRING) {
										txnId = String.valueOf(currentRow.getCell(key).getStringCellValue());
									} else if (currentCell.getCellTypeEnum() == CellType.NUMERIC) {
										txnId = String.valueOf(currentRow.getCell(key).getNumericCellValue() + "");
										if (txnId != null && txnId.length() > 0 && txnId.contains("E")) {
											BigDecimal bddouble = new BigDecimal(txnId);
											txnId = "" + bddouble.longValue();
										}
									}
								}
								if (map.get(key).equalsIgnoreCase("UTR_NO")) {
									if (currentCell.getCellTypeEnum() == CellType.STRING) {
										utrNo = String.valueOf(currentRow.getCell(key).getStringCellValue());
									} else if (currentCell.getCellTypeEnum() == CellType.NUMERIC) {
										utrNo = String.valueOf(currentRow.getCell(key).getNumericCellValue() + "");
										if (utrNo != null && utrNo.length() > 0 && utrNo.contains("E")) {
											BigDecimal bddouble = new BigDecimal(utrNo);
											utrNo = "" + bddouble.longValue();
										}
									}
								}
								if (map.get(key).equalsIgnoreCase("UTR_Date")) {
									DataFormatter dataFormatter = new DataFormatter();
									Cell cellValue = currentRow.getCell(currentCell.getColumnIndex());
									String formattedCellStr = dataFormatter.formatCellValue(cellValue);
									utrDate = formattedCellStr;
								}
							}
						}
						
						if (txnId != null&&!txnId.isEmpty() &&!utrNo.isEmpty() && utrNo != null && utrDate != null &&!utrDate.isEmpty()) {
							logger.info("Update TXN details :" + txnId + utrNo + utrDate);
							logger.info("Calling mongo method now");
							return_msg = txnDetails.updateTxnByTxnIDForUTRUpdateInTransactionStatusCollChargeback(txnId,
									utrNo, utrDate,type);
						}
					}
				}
			});
			workbook.close();
		} catch (Exception e) {
			return_msg ="Error while uploading UTR file";
			throw new RuntimeException("Error while uploading UTR file");
		}
		
		return return_msg;
	}
}
