package com.pay10.scheduler.commons;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Set;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.bson.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.pay10.commons.dao.OfflineRefundDao;
import com.pay10.commons.exception.SystemException;
import com.pay10.commons.user.Account;
import com.pay10.commons.user.AccountCurrency;
import com.pay10.commons.user.OfflineRefund;
import com.pay10.commons.user.User;
import com.pay10.commons.user.UserDao;
import com.pay10.commons.util.FieldType;

@Service
public class BulkRefundFileFormat {
	private static final Logger logger = LoggerFactory.getLogger(BulkRefundFileFormat.class);

	@Autowired
	private UserDao userDao;

	@Autowired
	OfflineRefundDao offlineRefundDao;

	public void canaraRefundFile(List<Document> refundRecord, String refundFilePath, String acquirer) throws ParseException {

		if (refundRecord.size() != 0) {
			DateFormat df1 = new SimpleDateFormat("dd-MM-yyyy");
			DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy hh:mm:ss a");
			StringBuilder stringBuilder = new StringBuilder();
			String fileTxnTime = new SimpleDateFormat("ddMMYYYY").format(new Date());
			String refundDate = new SimpleDateFormat("dd/MM/YYYY").format(new Date());
			

			logger.info("refundDate "+refundDate);
			refundFilePath += "TENREF" + fileTxnTime+ ".txt";
			String fileName = "TENREF" + fileTxnTime + ".txt";
			logger.info("RefundFilePath = {}, FileName = {} ", refundFilePath, fileName);
			File file = new File(refundFilePath);
			PrintWriter writer = null;
			try {
				writer = new PrintWriter(refundFilePath);
			} catch (FileNotFoundException e1) {
				e1.printStackTrace();
			}
			if (!file.exists()) {
				boolean flag;
				try {
					flag = file.createNewFile();
					logger.info("new file created on: " + refundFilePath + ", " + flag);
				} catch (IOException e) {
					e.printStackTrace();
				}

			}

			stringBuilder.append(
					"Transaction Date | Refund Date | Bank Merchant Code | Bank Reference Number | PGI Reference Number | Transaction Amount | Refund Amount | Refund Reference \n");
			// 04/05/2023 09:31:44
			// AM|5/4/2023|PAY10|520804052012286|8367130504093123|1000|1000|0805202301

			for (Document transactionSearch : refundRecord) {
				logger.info("transactionSearch :: " + transactionSearch);
				String dates = (String) transactionSearch.get(FieldType.CREATE_DATE.getName());
				Date txnDate= new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").parse(dates);  
				String transactionDate = dateFormat.format(txnDate);
				stringBuilder.append(transactionDate + "|" + refundDate
						+ "|PAY10|" + transactionSearch.get(FieldType.RRN.getName()) + "|"
						+ transactionSearch.get(FieldType.PG_REF_NUM.getName()) + "|"
						+ transactionSearch.get(FieldType.TOTAL_AMOUNT.getName()) + "|"
						+ transactionSearch.get(FieldType.AMOUNT.getName()) + "|"
						+ transactionSearch.get(FieldType.REFUND_ORDER_ID.getName()) + "\n");
			}

			writer.write(stringBuilder.toString());
			writer.close();

			OfflineRefund offlineRefund = new OfflineRefund();
			offlineRefund.setAcquirerName(acquirer);
			offlineRefund.setDate(df1.format(new Date()));
			offlineRefund.setFileName(fileName);
			offlineRefund.setFilePath(refundFilePath);
			offlineRefundDao.save(offlineRefund);
		}
	}

	public void JammuAndKashmirRefundFile(List<Document> refundRecord, String refundFilePath, String acquirer) throws ParseException {
		if (refundRecord.size() != 0) {
			DateFormat df1 = new SimpleDateFormat("dd-MM-yyyy");
			DateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
			StringBuilder stringBuilder = new StringBuilder();
			String fileTxnTime = new SimpleDateFormat("dd-MM-YYYY").format(new Date());
			String refundDate = new SimpleDateFormat("dd/MM/YYYY").format(new Date());

			refundFilePath += "RefundFile_" + fileTxnTime+ ".txt";
			String fileName = "RefundFile_" + fileTxnTime + ".txt";
			File file = new File(refundFilePath);
			PrintWriter writer = null;
			try {
				writer = new PrintWriter(refundFilePath);
			} catch (FileNotFoundException e1) {
				e1.printStackTrace();
			}
			if (!file.exists()) {
				boolean flag;
				try {
					flag = file.createNewFile();
					logger.info("new file created on: " + refundFilePath + ", " + flag);
				} catch (IOException e) {
					e.printStackTrace();
				}

			}

			stringBuilder.append("BID|Payee-id|Transactions AMT|Refund Amount|CRN|TXN_TIME|Status|order_no or ITC \n");
			// 04/05/2023 09:31:44
			// AM|5/4/2023|PAY10|520804052012286|8367130504093123|1000|1000|0805202301

			for (Document transactionSearch : refundRecord) {
				/*
				 * String merchantCode =
				 * getMerchantKey(transactionSearch.get(FieldType.PAY_ID.getName()).toString(),
				 * transactionSearch.get(FieldType.REFUND_ORDER_ID.getName()).toString(),
				 * transactionSearch.get(FieldType.ACQUIRER_TYPE.getName()).toString());
				 */
				
				String merchantCode = "000002432822";
				String dates = (String) transactionSearch.get(FieldType.CREATE_DATE.getName());
				Date txnDate= new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").parse(dates);  
				String transactionDate = dateFormat.format(txnDate);
				stringBuilder.append(transactionSearch.get(FieldType.RRN.getName()) + "|" + merchantCode
						+ "|" + transactionSearch.get(FieldType.TOTAL_AMOUNT.getName()) + "|"
						+ transactionSearch.get(FieldType.AMOUNT.getName()) + "|INR|"
						+ transactionDate + "|S|"
						+ transactionSearch.get(FieldType.PG_REF_NUM.getName()) + "\n");
			}

			writer.write(stringBuilder.toString());
			writer.close();

			OfflineRefund offlineRefund = new OfflineRefund();
			offlineRefund.setAcquirerName(acquirer);
			offlineRefund.setDate(df1.format(new Date()));
			offlineRefund.setFileName(fileName);
			offlineRefund.setFilePath(refundFilePath);
			offlineRefundDao.save(offlineRefund);
		}

	}

	public void axisBankNBRefundFile(List<Document> refundRecord, String refundFilePath, String acquirer) throws ParseException {
		if (refundRecord.size() != 0) {
			DateFormat df1 = new SimpleDateFormat("dd-MM-yyyy");
			StringBuilder stringBuilder = new StringBuilder();
			String fileTxnTime = new SimpleDateFormat("YYYYMMdd").format(new Date());
			String refundDate = new SimpleDateFormat("dd/MM/YYYY").format(new Date());
			DateFormat dateFormat = new SimpleDateFormat("YYYY/MM/dd");

			refundFilePath += "IConnect_Refund_BHARTIPAY_Refund_" + fileTxnTime + "_1" + ".txt";
			String fileName = "IConnect_Refund_BHARTIPAY_Refund_" + fileTxnTime + "_1" + ".txt";
			File file = new File(refundFilePath);
			PrintWriter writer = null;
			try {
				writer = new PrintWriter(refundFilePath);
			} catch (FileNotFoundException e1) {
				e1.printStackTrace();
			}
			if (!file.exists()) {
				boolean flag;
				try {
					flag = file.createNewFile();
					logger.info("new file created on: " + refundFilePath + ", " + flag);
				} catch (IOException e) {
					e.printStackTrace();
				}

			}

			stringBuilder.append(
					"Payee ID~~Payee Name~~BID (Bank ref no.)~~ITC (Atom id)~~PRN(MID no.)~~TRXN AMOUNT~~DATE~~REFUND AMOUNT \n");

			for (Document transactionSearch : refundRecord) {
				/*
				 * String merchantCode =
				 * getMerchantKey(transactionSearch.get(FieldType.PAY_ID.getName()).toString(),
				 * transactionSearch.get(FieldType.REFUND_ORDER_ID.getName()).toString(),
				 * transactionSearch.get(FieldType.ACQUIRER_TYPE.getName()).toString());
				 */
				String merchantCode = "009998043277";
				String merchantName = "test";
				String dates = (String) transactionSearch.get(FieldType.CREATE_DATE.getName());
				Date txnDate= new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").parse(dates);  
				String transactionDate = dateFormat.format(txnDate);
				
				stringBuilder.append(merchantCode + "~~" + merchantName + "~~"
						+ transactionSearch.get(FieldType.RRN.getName()) + "~~PAY10~~"
						+ transactionSearch.get(FieldType.PG_REF_NUM.getName()) + "~~"
						+ transactionSearch.get(FieldType.TOTAL_AMOUNT.getName()) + "~~"
						+ transactionDate + "~~"
						+ transactionSearch.get(FieldType.AMOUNT.getName()) + "\n");
			}

			writer.write(stringBuilder.toString());
			writer.close();

			OfflineRefund offlineRefund = new OfflineRefund();
			offlineRefund.setAcquirerName(acquirer);
			offlineRefund.setDate(df1.format(new Date()));
			offlineRefund.setFileName(fileName);
			offlineRefund.setFilePath(refundFilePath);
			offlineRefundDao.save(offlineRefund);
		}

	}

	public void sbiNBRefundFile(List<Document> refundRecord, String refundFilePath, String acquirer) {
		if (refundRecord.size() != 0) {
			DateFormat df1 = new SimpleDateFormat("dd-MM-yyyy");
			StringBuilder stringBuilder = new StringBuilder();
			String fileTxnTime = new SimpleDateFormat("YYYYMMdd").format(new Date());
			String refundDate = new SimpleDateFormat("dd/MM/YYYY").format(new Date());

			refundFilePath += "bhartipay_refund_" + fileTxnTime + "_badc83f27f92a8f3a179af8767b9608e" + ".txt";
			String fileName = "bhartipay_refund_" + fileTxnTime + "_badc83f27f92a8f3a179af8767b9608e" + ".txt";
			File file = new File(refundFilePath);
			PrintWriter writer = null;
			try {
				writer = new PrintWriter(refundFilePath);
			} catch (FileNotFoundException e1) {
				e1.printStackTrace();
			}
			if (!file.exists()) {
				boolean flag;
				try {
					flag = file.createNewFile();
					logger.info("new file created on: " + refundFilePath + ", " + flag);
				} catch (IOException e) {
					e.printStackTrace();
				}

			}

			int cnt = 1;
			for (Document transactionSearch : refundRecord) {
				stringBuilder.append(transactionSearch.get(FieldType.RRN.getName()) + "|"
						+ transactionSearch.get(FieldType.PG_REF_NUM.getName()) + "|" + cnt + "|"
						+ transactionSearch.get(FieldType.AMOUNT.getName()) + "|" + "\n");
				cnt++;
			}

			writer.write(stringBuilder.toString());
			writer.close();

			OfflineRefund offlineRefund = new OfflineRefund();
			offlineRefund.setAcquirerName(acquirer);
			offlineRefund.setDate(df1.format(new Date()));
			offlineRefund.setFileName(fileName);
			offlineRefund.setFilePath(refundFilePath);
			offlineRefundDao.save(offlineRefund);
		}

	}
	
	public void sbiCardRefundFile(List<Document> refundRecord, String refundFilePath, String acquirer) {
		if (refundRecord.size() != 0) {
			DateFormat df1 = new SimpleDateFormat("dd-MM-yyyy");
			DateFormat dateFormt = new SimpleDateFormat("ddMMYY");
			StringBuilder stringBuilder = new StringBuilder();
			String fileTxnTime = new SimpleDateFormat("ddMMYYYY").format(new Date());
			
			refundFilePath += "PG_Refund_" + fileTxnTime + "_1" + ".txt";
			String fileName = "PG_Refund_" + fileTxnTime + "_1" + ".txt";
			File file = new File(refundFilePath);
			PrintWriter writer = null;
			try {
				writer = new PrintWriter(refundFilePath);
			} catch (FileNotFoundException e1) {
				e1.printStackTrace();
			}
			if (!file.exists()) {
				boolean flag;
				try {
					flag = file.createNewFile();
					logger.info("new file created on: " + refundFilePath + ", " + flag);
				} catch (IOException e) {
					e.printStackTrace();
				}

			}

			int cnt = 01;
			for (Document transactionSearch : refundRecord) {
				String refundDate = "Refund"+fileTxnTime+cnt;
				stringBuilder.append("2,,,,356,"+transactionSearch.get(FieldType.RRN.getName()) + ",,,Bhartipay,"
						+ transactionSearch.get(FieldType.AMOUNT.getName()) + "," + refundDate + ",udf1,udf2,udf3,udf4,UDF5," + "\n");
				cnt++;
			}

			writer.write(stringBuilder.toString());
			writer.close();

			OfflineRefund offlineRefund = new OfflineRefund();
			offlineRefund.setAcquirerName(acquirer);
			offlineRefund.setDate(df1.format(new Date()));
			offlineRefund.setFileName(fileName);
			offlineRefund.setFilePath(refundFilePath);
			offlineRefundDao.save(offlineRefund);
		}

	}

	public void kotakNBRefundFile(List<Document> refundRecord, String refundFilePath, String acquirer) throws ParseException {
		if (refundRecord.size() != 0) {
			DateFormat df1 = new SimpleDateFormat("dd-MM-yyyy");
			StringBuilder stringBuilder = new StringBuilder();
			String fileTxnTime = new SimpleDateFormat("dd-MM-YYYY").format(new Date());
			String refundDate = new SimpleDateFormat("dd-MM-YYYY").format(new Date());
			DateFormat dateFormat = new SimpleDateFormat("dd-MMM-YYYY");

			refundFilePath += "Kotak_Netbanking_Refund_" + fileTxnTime + ".txt";
			String fileName = "Kotak_Netbanking_Refund_" + fileTxnTime + ".txt";
			File file = new File(refundFilePath);
			PrintWriter writer = null;
			try {
				writer = new PrintWriter(refundFilePath);
			} catch (FileNotFoundException e1) {
				e1.printStackTrace();
			}
			if (!file.exists()) {
				boolean flag;
				try {
					flag = file.createNewFile();
					logger.info("new file created on: " + refundFilePath + ", " + flag);
				} catch (IOException e) {
					e.printStackTrace();
				}

			}

			int cnt = 0;
			double refundAmt = 0.0;
			for (Document transactionSearch : refundRecord) {
				cnt++;
				refundAmt = refundAmt + Double.valueOf(transactionSearch.getString(FieldType.AMOUNT.getName()));
			}

			stringBuilder.append(fileName + "|" + cnt + "|" + refundAmt + "|NVRCrPUwneKJI7zZ41/CLw==" + "\n");
			int serialCnt = 1;
			for (Document transactionSearch : refundRecord) {
				/*
				 * String merchantCode =
				 * getMerchantKey(transactionSearch.get(FieldType.PAY_ID.getName()).toString(),
				 * transactionSearch.get(FieldType.REFUND_ORDER_ID.getName()).toString(),
				 * transactionSearch.get(FieldType.ACQUIRER_TYPE.getName()).toString());
				 */
				
				String merchantCode = "OSBHARTIPY";
				String dates = (String) transactionSearch.get(FieldType.CREATE_DATE.getName());
				Date txnDate= new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").parse(dates);  
				String transactionDate = dateFormat.format(txnDate);
				
				stringBuilder.append(serialCnt + "|" + merchantCode + "|"
						+ transactionDate + "|"
						+ transactionSearch.get(FieldType.PG_REF_NUM.getName()) + "|"
						+ transactionSearch.get(FieldType.AMOUNT.getName()) + "|"
						+ transactionSearch.get(FieldType.RRN.getName()) + "\n");
				serialCnt++;
			}

			writer.write(stringBuilder.toString());
			writer.close();

			OfflineRefund offlineRefund = new OfflineRefund();
			offlineRefund.setAcquirerName(acquirer);
			offlineRefund.setDate(df1.format(new Date()));
			offlineRefund.setFileName(fileName);
			offlineRefund.setFilePath(refundFilePath);
			offlineRefundDao.save(offlineRefund);
		}

	}

	public void cosmosUPIRefundFile(List<Document> refundRecord, String refundFilePath, String acquirer) throws ParseException {
		if (refundRecord.size() != 0) {
			DateFormat df1 = new SimpleDateFormat("dd-MM-yyyy");
			StringBuilder stringBuilder = new StringBuilder();
			String fileTxnTime = new SimpleDateFormat("ddMMYYYY").format(new Date());
			
			String fileExtension = ".xlsx";
			refundFilePath += fileTxnTime + " - UPI Refunds" +fileExtension;
			String fileName = fileTxnTime + " - UPI Refunds" + fileExtension;
			File file = new File(refundFilePath);
			PrintWriter writer = null;
			try {
				writer = new PrintWriter(refundFilePath);
			} catch (FileNotFoundException e1) {
				e1.printStackTrace();
			}
			if (!file.exists()) {
				boolean flag;
				try {
					flag = file.createNewFile();
					logger.info("new file created on: " + refundFilePath + ", " + flag);
				} catch (IOException e) {
					e.printStackTrace();
				}

			}

			SXSSFWorkbook wb = new SXSSFWorkbook(100);
			Row row;
			int rownum = 1;
			// Create a blank sheet
			Sheet sheet = wb.createSheet("Refund Trans");

			row = sheet.createRow(0);

			row.createCell(0).setCellValue("S. No");
			row.createCell(1).setCellValue("Txn Date");
			row.createCell(2).setCellValue("Txn Amt");
			row.createCell(3).setCellValue("Txn Type");
			row.createCell(4).setCellValue("RRN");
			row.createCell(5).setCellValue("UPI TRAN ID (35 digits)");
			row.createCell(6).setCellValue("Reason");
			row.createCell(7).setCellValue("Refund Request Date");

			if (refundRecord.size() < 800000) {

				int cnt = 1;
				for (Document transactionSearch : refundRecord) {

					logger.info("fetchOfflineRefundDataForRefundFile " + transactionSearch);
					row = sheet.createRow(rownum++);
					// transactionSearch.setSrNo(String.valueOf(rownum - 1));
					String MerchantId = String.valueOf(rownum - 1);
					Object[] objArr = null;

					objArr = myCsvMethodDownloadPaymentsforSaleCapturedForCosmos(transactionSearch, cnt);

					int cellnum = 0;
					for (Object obj : objArr) {
						// this line creates a cell in the next column of that row
						Cell cell = row.createCell(cellnum++);
						if (obj instanceof String)
							cell.setCellValue((String) obj);
						else if (obj instanceof Integer)
							cell.setCellValue((Integer) obj);

					}
					cnt++;
				}
			} else {
				row = sheet.createRow(rownum++);

				Cell cell = row.createCell(1);
				cell.setCellValue("Data limit exceeded for excel file generation , please select a smaller date range");
			}

			if (refundRecord.size() != 0) {

				// this Writes the workbook
				FileOutputStream out;
				try {
					out = new FileOutputStream(file);
					wb.write(out);
					out.flush();
					out.close();
					wb.dispose();
				} catch (Exception e) {
					logger.info("Exception in Offline Refund ", e);
					e.printStackTrace();
				}
				OfflineRefund offlineRefund = new OfflineRefund();
				offlineRefund.setAcquirerName(acquirer);
				offlineRefund.setDate(df1.format(new Date()));
				offlineRefund.setFileName(fileName);
				offlineRefund.setFilePath(refundFilePath);
				offlineRefundDao.save(offlineRefund);

				logger.info("Offline Refund Files Completed");
			}
		}

	}
	
	public void sbiUPIRefundFile(List<Document> refundRecord, String refundFilePath, String acquirer) throws ParseException {
		if (refundRecord.size() != 0) {
			DateFormat df1 = new SimpleDateFormat("dd-MM-yyyy");
			String fileTxnTime = new SimpleDateFormat("ddMMYYYY").format(new Date());
			
			String fileExtension = ".csv";
			refundFilePath += "SBI0000003232739" + fileTxnTime + fileExtension;
			String fileName = "SBI0000003232739" + fileTxnTime + fileExtension;
			File file = new File(refundFilePath);
			
			if (!file.exists()) {
				boolean flag;
				try {
					flag = file.createNewFile();
					logger.info("new file created on: " + refundFilePath + ", " + flag);
				} catch (IOException e) {
					e.printStackTrace();
				}

			}

			SXSSFWorkbook wb = new SXSSFWorkbook(100);
			Row row;
			int rownum = 1;
			// Create a blank sheet
			Sheet sheet = wb.createSheet("Refund");

			row = sheet.createRow(0);

			row.createCell(0).setCellValue("PG MERCHANT ID");
			row.createCell(1).setCellValue("REFUND REQ NO");
			row.createCell(2).setCellValue("TRANS REF NO.");
			row.createCell(3).setCellValue("CUSTOMER REF NO.");
			row.createCell(4).setCellValue("ORDER NO");
			row.createCell(5).setCellValue("REFUND REQ AMT");
			row.createCell(6).setCellValue("REFUND REMARK");
			
			if (refundRecord.size() < 800000) {

				int cnt = 1;
				for (Document transactionSearch : refundRecord) {

					logger.info("fetchOfflineRefundDataForRefundFile " + transactionSearch);
					row = sheet.createRow(rownum++);
					// transactionSearch.setSrNo(String.valueOf(rownum - 1));
					String MerchantId = String.valueOf(rownum - 1);
					Object[] objArr = null;

					objArr = myCsvMethodDownloadPaymentsforSaleCapturedForSbiUpi(transactionSearch, cnt);

					int cellnum = 0;
					for (Object obj : objArr) {
						// this line creates a cell in the next column of that row
						Cell cell = row.createCell(cellnum++);
						if (obj instanceof String)
							cell.setCellValue((String) obj);
						else if (obj instanceof Integer)
							cell.setCellValue((Integer) obj);

					}
					cnt++;
				}
			} else {
				row = sheet.createRow(rownum++);

				Cell cell = row.createCell(1);
				cell.setCellValue("Data limit exceeded for excel file generation , please select a smaller date range");
			}

			if (refundRecord.size() != 0) {

				// this Writes the workbook
				FileOutputStream out;
				try {
					out = new FileOutputStream(file);
					wb.write(out);
					out.flush();
					out.close();
					wb.dispose();
				} catch (Exception e) {
					logger.info("Exception in Offline Refund ", e);
					e.printStackTrace();
				}
				OfflineRefund offlineRefund = new OfflineRefund();
				offlineRefund.setAcquirerName(acquirer);
				offlineRefund.setDate(df1.format(new Date()));
				offlineRefund.setFileName(fileName);
				offlineRefund.setFilePath(refundFilePath);
				offlineRefundDao.save(offlineRefund);

				logger.info("Offline Refund Files Completed");
			}
		}

	}

	public void yesbankNBRefundFile(List<Document> refundRecord, String refundFilePath, String acquirer) throws ParseException {
		if (refundRecord.size() != 0) {
			DateFormat df1 = new SimpleDateFormat("dd-MM-yyyy");
			String fileTxnTime = new SimpleDateFormat("ddMMYYYY").format(new Date());

			String fileExtension = ".xlsx";
			refundFilePath += "BHARTIPAY REFUND FILE - " + fileTxnTime + fileExtension;
			String fileName = "BHARTIPAY REFUND FILE - " + fileTxnTime + fileExtension;
			File file = new File(refundFilePath);

			if (!file.exists()) {
				boolean flag;
				try {
					flag = file.createNewFile();
					logger.info("new file created on: " + refundFilePath + ", " + flag);
				} catch (IOException e) {
					e.printStackTrace();
				}

			}

			SXSSFWorkbook wb = new SXSSFWorkbook(100);
			Row row;
			int rownum = 1;
			// Create a blank sheet
			Sheet sheet = wb.createSheet("REFUND");

			row = sheet.createRow(0);

			row.createCell(0).setCellValue("AGGREGATOR CODE");
			row.createCell(1).setCellValue("TRANSACTION DATE");
			row.createCell(2).setCellValue("MERCHANT REF. NO");
			row.createCell(3).setCellValue("BANK REF. NO");
			row.createCell(4).setCellValue("TRANSACTION AMOUNT");
			row.createCell(5).setCellValue("REFUND AMOUNT");

			if (refundRecord.size() < 800000) {

				for (Document transactionSearch : refundRecord) {

					logger.info("fetchOfflineRefundDataForRefundFile " + transactionSearch);
					row = sheet.createRow(rownum++);
					String MerchantId = String.valueOf(rownum - 1);
					Object[] objArr = null;

					objArr = myCsvMethodDownloadPaymentsforSaleCapturedForYesbankNB(transactionSearch);

					int cellnum = 0;
					for (Object obj : objArr) {
						// this line creates a cell in the next column of that row
						Cell cell = row.createCell(cellnum++);
						if (obj instanceof String)
							cell.setCellValue((String) obj);
						else if (obj instanceof Integer)
							cell.setCellValue((Integer) obj);

					}
				}
			} else {
				row = sheet.createRow(rownum++);

				Cell cell = row.createCell(1);
				cell.setCellValue("Data limit exceeded for excel file generation , please select a smaller date range");
			}

			if (refundRecord.size() != 0) {

				// this Writes the workbook
				FileOutputStream out;
				try {
					out = new FileOutputStream(file);
					wb.write(out);
					out.flush();
					out.close();
					wb.dispose();
				} catch (Exception e) {
					logger.info("Exception in Offline Refund ", e);
					e.printStackTrace();
				}
				OfflineRefund offlineRefund = new OfflineRefund();
				offlineRefund.setAcquirerName(acquirer);
				offlineRefund.setDate(df1.format(new Date()));
				offlineRefund.setFileName(fileName);
				offlineRefund.setFilePath(refundFilePath);
				offlineRefundDao.save(offlineRefund);

				logger.info("Offline Refund Files Completed");
			}
		}

	}

	public Object[] myCsvMethodDownloadPaymentsforSaleCapturedForCosmos(Document redundDocument, int seqNo) throws ParseException {
		String refundDate = new SimpleDateFormat("dd-MMM-YY").format(new Date());
		DateFormat dateFormat = new SimpleDateFormat("dd-MMM-YYYY");
		Object[] objectArray = new Object[23];

		String dates = (String) redundDocument.get(FieldType.CREATE_DATE.getName());
		Date txnDate= new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").parse(dates);  
		String transactionDate = dateFormat.format(txnDate);
		// objectArray[0] = merchantId;
		objectArray[0] = seqNo;
		objectArray[1] = transactionDate;
		objectArray[2] = redundDocument.get(FieldType.AMOUNT.getName());
		objectArray[3] = "";
		objectArray[4] = redundDocument.get(FieldType.RRN.getName());
		objectArray[5] = redundDocument.get(FieldType.ACQ_ID.getName());
		objectArray[6] = redundDocument.get(FieldType.PG_REF_NUM.getName());
		objectArray[7] = refundDate;

		return objectArray;
	}

	public Object[] myCsvMethodDownloadPaymentsforSaleCapturedForSbiUpi(Document redundDocument, int seqNo) throws ParseException {
		Object[] objectArray = new Object[23];

		// objectArray[0] = merchantId;
		objectArray[0] = getMerchantKey(redundDocument.get(FieldType.PAY_ID.getName()).toString(),
				redundDocument.get(FieldType.REFUND_ORDER_ID.getName()).toString(),
				redundDocument.get(FieldType.ACQUIRER_TYPE.getName()).toString());
		objectArray[1] = redundDocument.get(FieldType.REFUND_ORDER_ID.getName()).toString();
		objectArray[2] = redundDocument.get(FieldType.PG_REF_NUM.getName()).toString();
		objectArray[3] = redundDocument.get(FieldType.ORIG_TXN_ID.getName()).toString();
		objectArray[4] = redundDocument.get(FieldType.ORDER_ID.getName()).toString();
		objectArray[5] = redundDocument.get(FieldType.AMOUNT.getName()).toString();
		objectArray[6] = "Customer refund";

		return objectArray;
	}
	
	public Object[] myCsvMethodDownloadPaymentsforSaleCapturedForYesbankNB(Document redundDocument) throws ParseException {
		Object[] objectArray = new Object[23];
		DateFormat dateFormat = new SimpleDateFormat("dd/MM/YYYY");
		String dates = (String) redundDocument.get(FieldType.CREATE_DATE.getName());
		Date txnDate= new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").parse(dates);  
		String transactionDate = dateFormat.format(txnDate);
		// objectArray[0] = merchantId;
		objectArray[0] = "BHARTIPAY";
		objectArray[1] = transactionDate;
		objectArray[2] = redundDocument.get(FieldType.PG_REF_NUM.getName());
		objectArray[3] = redundDocument.get(FieldType.RRN.getName());
		objectArray[4] = redundDocument.get(FieldType.TOTAL_AMOUNT.getName());
		objectArray[5] = redundDocument.get(FieldType.AMOUNT.getName());

		return objectArray;
	}

	public String getMerchantKey(String payId, String orderId, String acquirerName) {
		logger.info("getMerchantKey, payId={}, orderId={}, acquirerName={}", payId, orderId, acquirerName);
		User user = userDao.findPayId1(payId);
		Account account = null;
		Set<Account> accounts = user.getAccounts();

		if (accounts == null || accounts.size() == 0) {
			logger.info("No account found for Pay ID = " + payId + " and ORDER ID = " + orderId);
		} else {
			for (Account accountThis : accounts) {
				logger.info("accountThis " + accountThis);
				if (accountThis.getAcquirerName().equalsIgnoreCase(acquirerName)) {
					account = accountThis;
					break;
				}
			}
		}

		AccountCurrency accountCurrency = null;
		try {
			accountCurrency = account.getAccountCurrency("356");
		} catch (SystemException e) {
			logger.info("Exception while generating refund file ", e);
		}

		String merchantName = accountCurrency.getMerchantId();
		logger.info("merchantName");
		return merchantName;

	}

	public static void main(String[] args) throws ParseException {
		
		DateFormat df1 = new SimpleDateFormat("dd-MM-yyyy");
		StringBuilder stringBuilder = new StringBuilder();
		String fileTxnTime = new SimpleDateFormat("ddMMYYYY").format(new Date());
		String refundDate = new SimpleDateFormat("dd/MM/YYYY").format(new Date());

		//logger.info("fileTxnTime "+fileTxnTime);
		//logger.info("refundDate "+refundDate);
		
		String sDate1="2023-06-07 14:35:08";  
	    Date date1= new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").parse(sDate1);  
	    System.out.println(sDate1+"\t"+date1);  
		
 
        // 2. get Date with Zone & AM/PM marker
        DateFormat dateFormat = new SimpleDateFormat("dd-MMM-YY");
        String formatterDateTime = dateFormat.format(date1);
        System.out.print("\nFormatted Date/time with Zone is :- \n" + formatterDateTime);
	}
}
