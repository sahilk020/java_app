package com.crmws.service.impl;

import java.io.BufferedWriter;
import java.io.File;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.crmws.service.BsesTransactionFileDownloadService;
import com.crmws.worker.dto.BsesTransactionWiseDTO;
import com.pay10.commons.api.VelocityEmailer;
import com.pay10.commons.dao.TdrWaveOffSettingRepository;
import com.pay10.commons.entity.TdrWaveOffSettingEntity;
import com.pay10.commons.user.TransactionSearchDownloadObject;
import com.pay10.commons.util.Constants;
import com.pay10.commons.util.PaymentType;
import com.pay10.commons.util.PropertiesManager;

import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.FileInputStream;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.security.spec.KeySpec;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

@Service
public class BsesTransactionFileDownloadServiceImpl implements BsesTransactionFileDownloadService {

	@Autowired
	private TdrWaveOffSettingRepository tdrWaveOffSettingRepository;

	@Autowired
	private VelocityEmailer emailer;

	@Autowired
	private PropertiesManager propertiesManager;

	private String fileNameOfTxtFile;
	private String fileNameOfMisReport;
	private String fileNameOfTransactionWiseReport;
	private String fileNameOfNonTxtEncryptFile;

	private File fileOfTxtFile;
	private File fileOfMisReport;
	private File fileOfTransactionWiseReport;
	private File fileOfNonTxtEncryptFile;

	private static final Logger logger = LoggerFactory
			.getLogger(BsesTransactionFileDownloadServiceImpl.class.getName());
	final String bsesPayid = PropertiesManager.propertiesMap.get(Constants.BSESPAYID.getValue());
	final String bsesControl = PropertiesManager.propertiesMap.get(Constants.BSESCONTROL.getValue());
	final String bsesCounterNo = PropertiesManager.propertiesMap.get(Constants.BSESCOUNTERNO.getValue());
	final String bsesPayment = PropertiesManager.propertiesMap.get(Constants.BSESPAYMENT.getValue());
	final int bsesBillingCycle = Integer
			.valueOf(PropertiesManager.propertiesMap.get(Constants.BSESBILLINGCYCLE.getValue()));
	final String bsesCompanyCode = PropertiesManager.propertiesMap.get(Constants.BSESCOMPANYCODE.getValue());
	final String bsesPaymentMode = PropertiesManager.propertiesMap.get(Constants.BSESPAYMENTMODE.getValue());
	private InputStream fileInputStream;
	private String filename;
	final String commanFileLocation = PropertiesManager.propertiesMap.get(Constants.BSESFILELOCATION.getValue());
	Map<String, File> map = null;

	public Date yesterDayDate() {
		Calendar calendar = Calendar.getInstance();
		calendar.add(Calendar.DATE, -1);
		Date date = calendar.getTime();
		return date;
	}

	public Date todayDayDate() {
		Calendar calendar = Calendar.getInstance();
		Date date = calendar.getTime();
		return date;
	}

	@Override
	public String getDownloadTransactionWiseReport() {
		logger.info("Transaction Wise Report Start");
		String status = "Please Try After Some Time";
		try {
			DecimalFormat decimalFormat = new DecimalFormat();
			decimalFormat.setMaximumFractionDigits(2);
			Date date = yesterDayDate();

			SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
			String dateIndexForYesterdayTransaction = dateFormat.format(date);

			logger.info("getDownloadTransactionWiseReport() : bsesPayid :" + bsesPayid + "\tdateIndex :"
					+ dateIndexForYesterdayTransaction);
			List<TransactionSearchDownloadObject> transactionSearchDownloadObjects = tdrWaveOffSettingRepository
					.getYesterDayTransation(bsesPayid, dateIndexForYesterdayTransaction);

			SimpleDateFormat dateFormat1 = new SimpleDateFormat("dd.MM.yyy");
			String dateIndexForFile = dateFormat1.format(new Date());

			int listSize = transactionSearchDownloadObjects.size();
			logger.info("getDownloadTransactionWiseReport()-> calling tdrWaveOffSettingRepository return size : "
					+ listSize);

			List<BsesTransactionWiseDTO> bsesTransactionWiseDTOs = new ArrayList<>();

			for (TransactionSearchDownloadObject t : transactionSearchDownloadObjects) {

				SimpleDateFormat dateFormatt1 = new SimpleDateFormat("yyyyMMdd");
				SimpleDateFormat dateFormatt2 = new SimpleDateFormat("dd.MM.yyy");

				BsesTransactionWiseDTO bsesTransactionWiseDTO = new BsesTransactionWiseDTO();
				bsesTransactionWiseDTO.setPaymentFileDate(dateIndexForFile);
				bsesTransactionWiseDTO.setAgencyName("Pay10");
				bsesTransactionWiseDTO.setPayMode(t.getPaymentMethods());
				bsesTransactionWiseDTO.setProductCode(t.getMopType());
				bsesTransactionWiseDTO.setTransactionRefNo(t.getOrderId());
				bsesTransactionWiseDTO.setCaNumber(String.valueOf(t.getUdf5()));
				bsesTransactionWiseDTO.setTransationDate(dateFormatt2.format(dateFormatt1.parse(t.getDateindex())));

				if (t.getSurchargeFlag().equalsIgnoreCase("Y")) {
					BigDecimal[] entity = tdrWaveOffSettingRepository.getTdrReports(bsesPayid,
							PaymentType.getpaymentCode(t.getPaymentMethods()), String.valueOf(t.getAmount()));
					bsesTransactionWiseDTO.setMdr("" + 0);
					bsesTransactionWiseDTO.setGst("" + 0);
					bsesTransactionWiseDTO.setConvenienceFee(
							decimalFormat.format(Double.valueOf(entity[0].toString())).replaceAll(",", ""));
					bsesTransactionWiseDTO.setNetAmount(t.getAmount());
					bsesTransactionWiseDTO
							.setGrossAmount(decimalFormat
									.format((Double.valueOf(t.getGrossAmount())
											- Double.valueOf(bsesTransactionWiseDTO.getConvenienceFee())))
									.replaceAll(",", ""));
				} else {

					bsesTransactionWiseDTO.setMdr(
							decimalFormat.format(Double.valueOf(t.getAcquirerTdrSC()) + Double.valueOf(t.getPgTdrSC()))
									.replaceAll(",", ""));
					bsesTransactionWiseDTO.setGst(
							decimalFormat.format(Double.valueOf(t.getPgGst()) + Double.valueOf(t.getAcquirerGst()))
									.replaceAll(",", ""));
					bsesTransactionWiseDTO.setConvenienceFee("" + 0);
					bsesTransactionWiseDTO.setGrossAmount(
							decimalFormat.format(Double.valueOf(t.getGrossAmount())).replaceAll(",", ""));
					bsesTransactionWiseDTO
							.setNetAmount(
									decimalFormat
											.format((Double.valueOf(bsesTransactionWiseDTO.getGrossAmount())
													- Double.valueOf(bsesTransactionWiseDTO.getMdr())
													- Double.valueOf(bsesTransactionWiseDTO.getGst())))
											.replaceAll(",", ""));
				}
				bsesTransactionWiseDTOs.add(bsesTransactionWiseDTO);
			}
			try (SXSSFWorkbook wb = new SXSSFWorkbook(100)) {
				Row row;
				int rownum = 1;
				// Create a blank sheet
				Sheet sheet = wb.createSheet("Transaction Wise Report");
				row = sheet.createRow(0);

				row.createCell(0).setCellValue("Payment File Date");
				row.createCell(1).setCellValue("Agency Name");
				row.createCell(2).setCellValue("Pay Mode");
				row.createCell(3).setCellValue("Product Code");
				row.createCell(4).setCellValue("Transaction Ref No");
				row.createCell(5).setCellValue("CA Number");
				row.createCell(6).setCellValue("Transaction Date");
				row.createCell(7).setCellValue("Gross Amount (Rs.)");
				row.createCell(8).setCellValue("MDR Charges (Rs.) (on payment amount upto Rs. 5K)");
				row.createCell(9).setCellValue("GST (Rs.)");
				row.createCell(10).setCellValue("Convenience fee (above Rs. 5K, charged from consumer)  (Rs.)");
				row.createCell(11).setCellValue("Net Amount (Rs.)");

				if (bsesTransactionWiseDTOs.size() < 800000) {
					for (BsesTransactionWiseDTO transactionSearch : bsesTransactionWiseDTOs) {
						row = sheet.createRow(rownum++);
//							transactionSearch.setSrNo(String.valueOf(rownum - 1));
						Object[] objArr = transactionSearch.getTransactionObject();

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
					cell.setCellValue(
							"Data limit exceeded for excel file generation , please select a smaller date range");
				}
				String FILE_EXTENSION = ".xlsx";
				DateFormat df = new SimpleDateFormat("yyyyMMdd");
				filename = commanFileLocation + File.separator + "Transaction Wise Report_Pay10_"
						+ df.format(new Date()) + FILE_EXTENSION;
				File file = new File(filename);

				// this Writes the workbook
				FileOutputStream out = new FileOutputStream(file);
				wb.write(out);
				out.flush();
				out.close();
				wb.dispose();
				fileInputStream = new FileInputStream(file);
				setFileNameOfTransactionWiseReport(file.getName());
				setFileOfTransactionWiseReport(file);
				status = "File generated successfully for Transaction Report at location of :" + file;
				logger.info(status);
			} catch (Exception e) {
				status = "Someting Went Wrong File Not Created";
				logger.error("Exception Occur in excel file creating ", e);
				e.printStackTrace();
			}

		} catch (Exception e) {
			status = "Someting Went Wrong";
			logger.info(
					"Exception Occur inside class BsesTransactionFileDownloadServiceImpl inside method getDownloadTransactionWiseReport() : ",
					e);
			e.printStackTrace();
		}
		logger.info("Status Of this Method : :" + status);
		return status;
	}

	@Override
	public String downloadMisReport() {
		String status = "Please Try After Some Time";
		try {
			DecimalFormat decimalFormat = new DecimalFormat();
			decimalFormat.setMaximumFractionDigits(2);
			Date date = yesterDayDate();

			SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
			String dateIndexForYesterdayTransaction = dateFormat.format(date);

			List<TransactionSearchDownloadObject> transactionSearchDownloadObjects = tdrWaveOffSettingRepository
					.getYesterDayMisTransation(bsesPayid, dateIndexForYesterdayTransaction, bsesCounterNo);
			logger.info("downloadMisReport()-> calling tdrWaveOffSettingRepository return size : "
					+ transactionSearchDownloadObjects.size());

			String total = "Total";
			double totalTransactionCount = 0;
			double totalTransactionAmount = 0;
			double totalSurchargeTransactionCount = 0;
			double totalSurchargeTransactionAmount = 0;
			double totalMdrTransactionCount = 0;
			double totalMdrTransactionAmount = 0;
			double totalMdrCharge = 0;
			double totalGst = 0;
			double totalNetAmount = 0;

			for (TransactionSearchDownloadObject transactionSearchDownloadObject : transactionSearchDownloadObjects) {
				totalTransactionCount = totalTransactionCount
						+ Double.valueOf(transactionSearchDownloadObject.getTotalTransactionCount());
				totalTransactionAmount = totalTransactionAmount
						+ Double.valueOf(transactionSearchDownloadObject.getTotalTransactionAmount());
				totalSurchargeTransactionCount = totalSurchargeTransactionCount
						+ Double.valueOf(transactionSearchDownloadObject.getTotalSurchargeTransactionCount());
				totalSurchargeTransactionAmount = totalSurchargeTransactionAmount
						+ Double.valueOf(transactionSearchDownloadObject.getTotalSurchargeTransactionAmount());
				totalMdrTransactionCount = totalMdrTransactionCount
						+ Double.valueOf(transactionSearchDownloadObject.getTotalMdrTransactionCount());
				totalMdrTransactionAmount = totalMdrTransactionAmount
						+ Double.valueOf(transactionSearchDownloadObject.getTotalMdrTransactionAmount());
				totalMdrCharge = totalMdrCharge + Double.valueOf(transactionSearchDownloadObject.getTotalMdrCharge());
				totalGst = totalGst + Double.valueOf(transactionSearchDownloadObject.getTotalGst());
				totalNetAmount = totalNetAmount + Double.valueOf(transactionSearchDownloadObject.getTotalNetAmount());
			}
			TransactionSearchDownloadObject searchDownloadObject = new TransactionSearchDownloadObject();
			searchDownloadObject.setTotal(total);
			searchDownloadObject.setTotalTransactionCount(String.valueOf(totalTransactionCount));
			searchDownloadObject.setTotalTransactionAmount(decimalFormat.format(totalTransactionAmount));
			searchDownloadObject.setTotalSurchargeTransactionCount(String.valueOf(totalSurchargeTransactionCount));
			searchDownloadObject
					.setTotalSurchargeTransactionAmount(decimalFormat.format(totalSurchargeTransactionAmount));
			searchDownloadObject.setTotalMdrTransactionCount(String.valueOf(totalMdrTransactionCount));
			searchDownloadObject.setTotalMdrTransactionAmount(decimalFormat.format(totalMdrTransactionAmount));
			searchDownloadObject.setTotalMdrCharge(decimalFormat.format(totalMdrCharge));
			searchDownloadObject.setTotalGst(String.valueOf(totalGst));
			searchDownloadObject.setTotalNetAmount(decimalFormat.format(totalNetAmount));

			List<TransactionSearchDownloadObject> summary = new ArrayList<>();
			TransactionSearchDownloadObject one = new TransactionSearchDownloadObject();
			one.setTotal("Total records");
			one.setTotalTransactionCount(String.valueOf(totalTransactionCount));
			summary.add(one);

			TransactionSearchDownloadObject two = new TransactionSearchDownloadObject();
			two.setTotal("Gross Amount");
			two.setTotalTransactionCount(decimalFormat.format(totalTransactionAmount));
			summary.add(two);

			TransactionSearchDownloadObject three = new TransactionSearchDownloadObject();
			three.setTotal("Net Amount");
			three.setTotalTransactionCount(decimalFormat.format(totalNetAmount));
			summary.add(three);

			TransactionSearchDownloadObject four = new TransactionSearchDownloadObject();
			four.setTotal("RTGS Amount");
			four.setTotalTransactionCount(decimalFormat.format(totalNetAmount));
			summary.add(four);

			TransactionSearchDownloadObject five = new TransactionSearchDownloadObject();
			five.setTotal("RTGS Amount");
			five.setTotalTransactionCount(decimalFormat.format((totalNetAmount - totalNetAmount)));
			summary.add(five);

			try (SXSSFWorkbook wb = new SXSSFWorkbook(100)) {
				Row row;
				int rownum = 3;
				// Create a blank sheet
				Sheet sheet = wb.createSheet("Mis");

				Row row1 = sheet.createRow(0);

				Cell cellx = row1.createCell(0);
				cellx.setCellValue("MIS Pertains to Collection Date: " + new SimpleDateFormat("dd.MM.yyyy")
						.format(new SimpleDateFormat("yyyyMMdd").parse(dateIndexForYesterdayTransaction)));
				sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, 10));

				Row row2 = sheet.createRow(1);

				Cell cellt = row2.createCell(2);
				cellt.setCellValue("Total Transactions");
				sheet.addMergedRegion(new CellRangeAddress(1, 1, 2, 3));

				Cell celly = row2.createCell(4);
				celly.setCellValue("Transactions for which MDR Charges are not borne by BRPL");
				sheet.addMergedRegion(new CellRangeAddress(1, 1, 4, 5));

				Cell cellu = row2.createCell(6);
				cellu.setCellValue("Transactions for which MDR Charges are borne by BRPL");
				sheet.addMergedRegion(new CellRangeAddress(1, 1, 6, 7));

				row = sheet.createRow(2);

				row.createCell(0).setCellValue("Counter No.");
				row.createCell(1).setCellValue("Payment Mode");
				row.createCell(2).setCellValue("Number");
				row.createCell(3).setCellValue("Amount(RS.)");
				row.createCell(4).setCellValue("Number");
				row.createCell(5).setCellValue("Amount(RS.)");
				row.createCell(6).setCellValue("Number");
				row.createCell(7).setCellValue("Amount(RS.)");
				row.createCell(8).setCellValue("MDR Charges (Rs.)");
				row.createCell(9).setCellValue("GST (Rs.)");
				row.createCell(10).setCellValue("Net Amount (Rs.)");

				if (transactionSearchDownloadObjects.size() < 800000) {
					for (TransactionSearchDownloadObject transactionSearch : transactionSearchDownloadObjects) {
						row = sheet.createRow(rownum++);
						Object[] objArr = transactionSearch.getBsesMisReport();

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
					row = sheet.createRow(rownum++);
					Object[] objArr = searchDownloadObject.getBsesMisReportTotal();

					int cellnum = 1;
					for (Object obj : objArr) {
						// this line creates a cell in the next column of that row
						Cell cell = row.createCell(cellnum++);
						if (obj instanceof String)
							cell.setCellValue((String) obj);
						else if (obj instanceof Integer)
							cell.setCellValue((Integer) obj);
					}

					for (TransactionSearchDownloadObject transactionSearch : summary) {
						row = sheet.createRow(++rownum);
						Object[] objArr1 = transactionSearch.getBsesMisReportTotalSummary();

						int cellnum1 = 0;
						for (Object obj : objArr1) {
							// this line creates a cell in the next column of that row
							Cell cell = row.createCell(cellnum1++);
							if (obj instanceof String)
								cell.setCellValue((String) obj);
							else if (obj instanceof Integer)
								cell.setCellValue((Integer) obj);
						}
					}

				} else {
					row = sheet.createRow(rownum++);
					Cell cell = row.createCell(1);
					cell.setCellValue(
							"Data limit exceeded for excel file generation , please select a smaller date range");
				}
				String FILE_EXTENSION = ".xlsx";
				DateFormat df = new SimpleDateFormat("yyyyMMdd");
				filename = commanFileLocation + File.separator + "Mis_Pay10_" + df.format(new Date()) + FILE_EXTENSION;
				File file = new File(filename);

				// this Writes the workbook
				FileOutputStream out = new FileOutputStream(file);
				wb.write(out);
				out.flush();
				out.close();
				wb.dispose();
				fileInputStream = new FileInputStream(file);
				status = "File generated successfully for Mis_Pay10 Report at location of :" + file;
				setFileNameOfMisReport(file.getName());
				setFileOfMisReport(file);
				logger.info(status);
			} catch (Exception e) {
				status = "Someting Went Wrong File Not Created";
				logger.error("Exception Occur in excel file creating ", e);
				e.printStackTrace();
			}

		} catch (Exception e) {
			e.printStackTrace();
			status = "Something Went Wrong";
		}
		return status;
	}

	@Override
	public String downloadTxtFile() {
		String status = "Please Try After Some TIme";
		File file = null;
		FileOutputStream fout = null;
		BufferedWriter bufferedWriter = null;

		try {

			Date date = yesterDayDate();
			SimpleDateFormat dateYesterday = new SimpleDateFormat("yyyyMMdd");
			SimpleDateFormat dateTodayddMM = new SimpleDateFormat("ddMM");
			SimpleDateFormat dateTodayddMMyyy = new SimpleDateFormat("ddMMyyyy");
			String dateIndexForYesterdayTransaction = dateYesterday.format(date);
			List<String> paymentType = tdrWaveOffSettingRepository.getPaymentTypes(bsesPayid,
					dateIndexForYesterdayTransaction);
			map = new HashMap<>();
			if (paymentType.size() > 0) {
				for (String string : paymentType) {

					final String bsesCno = PropertiesManager.propertiesMap.get("ENERGY" + string);

					List<TransactionSearchDownloadObject> transactionSearchDownloadObjects = tdrWaveOffSettingRepository
							.getYesterDayTransationForTxtFile(bsesPayid, dateIndexForYesterdayTransaction, string);
					double totalAmount = tdrWaveOffSettingRepository.getYesterDayTransationAmount(bsesPayid,
							dateIndexForYesterdayTransaction);

					int totalAmountWithoutPrecision = Integer.valueOf(String.format("%.0f", totalAmount));

					String totalAmountWithTwoPrecision = String.format("%.2f", totalAmount);

					String amountprecision = totalAmountWithTwoPrecision.split("\\.")[1];

					int transactionSize = transactionSearchDownloadObjects.size();

					String todayDateDDMM = dateTodayddMM.format(todayDayDate());

					ArrayList<String> txtFileContent = new ArrayList<>();

					// Countrol Line
					String controlLine = (bsesControl + bsesCno + todayDateDDMM
							+ String.format("%010d", totalAmountWithoutPrecision) + amountprecision
							+ String.format("%010d", totalAmountWithoutPrecision) + amountprecision
							+ String.format("%05d", transactionSize));

					logger.info("Control line Length : " + controlLine.length());

					txtFileContent.add(controlLine);

					// Payment Line
					for (TransactionSearchDownloadObject transactionSearchDownloadObject : transactionSearchDownloadObjects) {
						String grossAmount = transactionSearchDownloadObject.getAmount();
						String grossAmountWithoutPrecision = String.format("%.0f", Double.valueOf(grossAmount));
						String grossAmountWithPrecision = grossAmount.split("\\.")[1];
						String paymentLine = String.format("%-76s",
								bsesPayment + String.format("%02d", bsesBillingCycle)
										+ String.format("%-4s", bsesCounterNo)
										+ transactionSearchDownloadObject.getUdf5()
										+ String.format("%010d", Integer.valueOf(grossAmountWithoutPrecision))
										+ grossAmountWithPrecision + "00001" + dateTodayddMMyyy.format(todayDayDate())
										+ bsesCompanyCode + bsesPaymentMode);
						logger.info("Payment line Length : " + paymentLine.length());
						txtFileContent.add(paymentLine);
					}

					file = new File(commanFileLocation + File.separator + "P"
							+ new SimpleDateFormat("ddMMyy").format(date) + "." + bsesCno + ".txt");
					fout = new FileOutputStream(file, false);
					bufferedWriter = new BufferedWriter(new OutputStreamWriter(fout));

					for (int i = 0; i < txtFileContent.size(); i++) {

						bufferedWriter.write(txtFileContent.get(i));
						bufferedWriter.newLine();
						bufferedWriter.flush();
					}
					File fileEncryptLoaction = fileEncryption(file);
					File fileDecryptLoaction = fileDecryption(fileEncryptLoaction);
					status = "Encrypted File generated successfully for Transaction Report in txt at location of :"
							+ fileEncryptLoaction;
					logger.info("Encrypted File generated successfully for Transaction Report in txt at location of :"
							+ fileEncryptLoaction);
					logger.info("Decrypted File generated successfully for Transaction Report in txt at location of :"
							+ fileDecryptLoaction);
					setFileNameOfTxtFile(fileEncryptLoaction.getName());
					setFileOfTxtFile(fileEncryptLoaction);
					setFileNameOfNonTxtEncryptFile(file.getName());
					setFileOfNonTxtEncryptFile(file);

					map.put(fileEncryptLoaction.getName(), fileEncryptLoaction);
				}
			}
		} catch (Exception e) {
			status = "Someting Went Wrong File Not Created";
			logger.error(
					"Exception Occur inside class BsesTransactionFileDownloadServiceImpl inside method downloadTxtFile() :",
					e);
			e.printStackTrace();
		} finally {
			try {
				fout.close();
				bufferedWriter.close();
			} catch (IOException e) {
				status = "Exception Occur inside class BsesTransactionFileDownloadServiceImpl inside method downloadTxtFile() while FileOutputStream and BufferedWriter object close";
				logger.error(
						"Exception Occur inside class BsesTransactionFileDownloadServiceImpl inside method downloadTxtFile() while FileOutputStream and BufferedWriter object close:",
						e);
				e.printStackTrace();
			}

		}
		return status;
	}

	public File fileEncryption(File file) throws Exception {
		File fileOut = new File(commanFileLocation + File.separator + "P"
				+ new SimpleDateFormat("yyMMdd").format(new Date()) + "." + bsesCounterNo + ".txt");

		String encryptionKey = "DTVJIHJOHCVG_KCH";
		char[] salt = { 'S', '@', '1', 't', 'S', '@', '1', 't' };
		String iv = "e675f725e675f725";

		SecretKeySpec keySpec = new SecretKeySpec(getKey(encryptionKey, salt), "AES");

		IvParameterSpec ivSpec = new IvParameterSpec(iv.getBytes(StandardCharsets.UTF_8));

		Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
		cipher.init(Cipher.ENCRYPT_MODE, keySpec, ivSpec);

		FileInputStream inputFileStream = new FileInputStream(file);
		CipherInputStream cipherInputStream = new CipherInputStream(inputFileStream, cipher);
		FileOutputStream outputFileStream = new FileOutputStream(fileOut);

		int data;
		while ((data = cipherInputStream.read()) != -1) {
			outputFileStream.write((byte) data);
		}

		cipherInputStream.close();
		inputFileStream.close();
		outputFileStream.close();
		return fileOut;

	}

	public File fileDecryption(File file) throws Exception {
		File fileOut = new File(commanFileLocation + File.separator + "P"
				+ new SimpleDateFormat("yyMMdd").format(new Date()) + "." + bsesCounterNo + "Decrypt" + ".txt");

		String encryptionKey = "DTVJIHJOHCVG_KCH";
		char[] salt = { 'S', '@', '1', 't', 'S', '@', '1', 't' };
		String iv = "e675f725e675f725";

		SecretKeySpec keySpec = new SecretKeySpec(getKey(encryptionKey, salt), "AES");

		IvParameterSpec ivSpec = new IvParameterSpec(iv.getBytes(StandardCharsets.UTF_8));

		Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
		cipher.init(Cipher.DECRYPT_MODE, keySpec, ivSpec);

		FileInputStream inputFileStream = new FileInputStream(file);
		CipherInputStream cipherInputStream = new CipherInputStream(inputFileStream, cipher);
		FileOutputStream outputFileStream = new FileOutputStream(fileOut);

		int data;
		while ((data = cipherInputStream.read()) != -1) {
			outputFileStream.write((byte) data);
		}

		cipherInputStream.close();
		inputFileStream.close();
		outputFileStream.close();
		return fileOut;

	}

	private static byte[] getKey(String encryptionKey, char[] salt) throws Exception {
		int iterationCount = 65536;
		int keyLength = 128;
		KeySpec keySpec = new PBEKeySpec(encryptionKey.toCharArray(), toBytes(salt), iterationCount, keyLength);
		SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
		byte[] keyBytes = keyFactory.generateSecret(keySpec).getEncoded();
		return keyBytes;
	}

	private static byte[] toBytes(char[] chars) {
		CharBuffer charBuffer = CharBuffer.wrap(chars);
		ByteBuffer byteBuffer = Charset.forName("UTF-8").encode(charBuffer);
		byte[] bytes = Arrays.copyOfRange(byteBuffer.array(), byteBuffer.position(), byteBuffer.limit());
		Arrays.fill(byteBuffer.array(), (byte) 0); // clear sensitive data
		return bytes;
	}

	@Override
	public String save(TdrWaveOffSettingEntity entity) {
		String status = "Please try later";
		try {
			DecimalFormat decimalFormat = new DecimalFormat();
			decimalFormat.setMaximumFractionDigits(2);
			entity.setPgPercentage(Double
					.valueOf(decimalFormat.format((entity.getMerchantPercentage() - entity.getBankPercentage()))));
			tdrWaveOffSettingRepository.save(entity);
			status = "Successfully Save";
		} catch (Exception e) {
			status = "Something Went Wrong";
			logger.error("Exception occur while saving object", e);
			e.printStackTrace();
		}
		return status;
	}

	public List<String> getPaymentype(String payid) {
		return tdrWaveOffSettingRepository.getPaymentTypeByPayid(payid);
	}

	@Override
	public String sendMailBses() {
		String res = "Failed";
		try {
			String mailId = propertiesManager.propertiesMap.get("MAILIDFORBSES");
			String[] mailToStrings = mailId.split(",");

			String body = propertiesManager.propertiesMap.get("BODYFORMAILFORBSES");

			String subject = propertiesManager.propertiesMap.get("SUBJECTFORMAILFORBSES");

			logger.info("Inside sendMailBses parameters :" + mailId + "\t" + body + "\t" + subject);

			downloadMisReport();
			downloadTxtFile();
			getDownloadTransactionWiseReport();

			Map<String, File> files = new HashMap<>();
			files.putAll(map);
			// files.put(getFileNameOfTxtFile(), getFileOfTxtFile());
			// files.put(getFileNameOfNonTxtEncryptFile(), getFileOfNonTxtEncryptFile());
			files.put(getFileNameOfMisReport(), getFileOfMisReport());
			files.put(getFileNameOfTransactionWiseReport(), getFileOfTransactionWiseReport());

			if (mailToStrings != null) {
				res = emailer.sendEmailBses(body, subject, mailToStrings, files);
			} else {
				res = "Mail To is Empty Please check Application.yml file";
			}

		} catch (Exception e) {
			logger.error("Exception Occur in sendMail() : ", e);
			res = "Something went wrong";
			e.printStackTrace();
		}
		return res;
	}

	public String getFileNameOfNonTxtEncryptFile() {
		return fileNameOfNonTxtEncryptFile;
	}

	public void setFileNameOfNonTxtEncryptFile(String fileNameOfNonTxtEncryptFile) {
		this.fileNameOfNonTxtEncryptFile = fileNameOfNonTxtEncryptFile;
	}

	public File getFileOfNonTxtEncryptFile() {
		return fileOfNonTxtEncryptFile;
	}

	public void setFileOfNonTxtEncryptFile(File fileOfNonTxtEncryptFile) {
		this.fileOfNonTxtEncryptFile = fileOfNonTxtEncryptFile;
	}

	public File getFileOfTxtFile() {
		return fileOfTxtFile;
	}

	public void setFileOfTxtFile(File fileOfTxtFile) {
		this.fileOfTxtFile = fileOfTxtFile;
	}

	public File getFileOfMisReport() {
		return fileOfMisReport;
	}

	public void setFileOfMisReport(File fileOfMisReport) {
		this.fileOfMisReport = fileOfMisReport;
	}

	public File getFileOfTransactionWiseReport() {
		return fileOfTransactionWiseReport;
	}

	public void setFileOfTransactionWiseReport(File fileOfTransactionWiseReport) {
		this.fileOfTransactionWiseReport = fileOfTransactionWiseReport;
	}

	public String getFilename() {
		return filename;
	}

	public void setFilename(String filename) {
		this.filename = filename;
	}

	public InputStream getFileInputStream() {
		return fileInputStream;
	}

	public void setFileInputStream(InputStream fileInputStream) {
		this.fileInputStream = fileInputStream;
	}

	public String getFileNameOfTxtFile() {
		return fileNameOfTxtFile;
	}

	public void setFileNameOfTxtFile(String fileNameOfTxtFile) {
		this.fileNameOfTxtFile = fileNameOfTxtFile;
	}

	public String getFileNameOfMisReport() {
		return fileNameOfMisReport;
	}

	public void setFileNameOfMisReport(String fileNameOfMisReport) {
		this.fileNameOfMisReport = fileNameOfMisReport;
	}

	public String getFileNameOfTransactionWiseReport() {
		return fileNameOfTransactionWiseReport;
	}

	public void setFileNameOfTransactionWiseReport(String fileNameOfTransactionWiseReport) {
		this.fileNameOfTransactionWiseReport = fileNameOfTransactionWiseReport;
	}

}
