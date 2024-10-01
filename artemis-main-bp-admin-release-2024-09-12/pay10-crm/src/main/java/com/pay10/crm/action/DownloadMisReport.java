package com.pay10.crm.action;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.pay10.commons.user.MISReportObject;
import com.pay10.commons.user.TransactionSearchNew;
import com.pay10.commons.util.Constants;
import com.pay10.commons.util.PropertiesManager;
import com.pay10.crm.mongoReports.TxnReports;

public class DownloadMisReport extends AbstractSecureAction {

	private static final long serialVersionUID = -6497286461324980048L;
	private static Logger logger = LoggerFactory.getLogger(XssFormatFileDownload.class.getName());

	@Autowired
	private TxnReports txnReports;
	 @Autowired
	    private PropertiesManager propertiesManager;

	private String payId;
	private String acq;
	private String dFrom;
	private String dTo;
	private String filename;
	private String sFrom;
	private String sTo;
	private String statusType;
	private InputStream fileInputStream;

	@Override
	public String execute() {
		SXSSFWorkbook wb = null;
		try {

			logger.info("DownloadMisReport-------- ");

			List<TransactionSearchNew> aaData = txnReports.misReportList(acq, payId, dFrom, dTo, 0, 100000,sFrom,sTo,statusType);

			logger.info(
					"DownloadMisReport------------List generated successfully for DownloadMisReport Report , size = "
							+ aaData.size());
			wb = new SXSSFWorkbook(100);
			Row row;
			int rownum = 1;
			// Create a blank sheet
			Sheet sheet = wb.createSheet("Transaction_Report");
			row = sheet.createRow(0);

			row.createCell(0).setCellValue("Sr No");
			row.createCell(1).setCellValue("Txn Id");
			row.createCell(2).setCellValue("Pay ID");
			row.createCell(3).setCellValue("Pg Ref Num");
			row.createCell(4).setCellValue("Merchant");
			row.createCell(5).setCellValue("Date");
			row.createCell(6).setCellValue("Order Id");
			row.createCell(7).setCellValue("Refund Order Id");
			row.createCell(8).setCellValue("Mop Type");
			row.createCell(9).setCellValue("Payment Method");
			row.createCell(10).setCellValue("Txn Type");
			row.createCell(11).setCellValue("Status");
			row.createCell(12).setCellValue("Total Amount");
			row.createCell(13).setCellValue("PG TDR");
			row.createCell(14).setCellValue("PG GST");
			row.createCell(15).setCellValue("Acquirer TDR");
			row.createCell(16).setCellValue("Acquirer GST");
			row.createCell(17).setCellValue("Total Amt Payable to Merchant A/c");
			row.createCell(18).setCellValue("Total Payout from Nodal Account");
			row.createCell(19).setCellValue("Surcharge Flag");
			row.createCell(20).setCellValue("Customer Email");
			row.createCell(21).setCellValue("Customer Ph Number");
			row.createCell(22).setCellValue("Acquirer Type");
			row.createCell(23).setCellValue("Ip Address");
			row.createCell(24).setCellValue("card Mask");
			row.createCell(25).setCellValue("RRN Number");
			row.createCell(26).setCellValue("SplitPayment");

			if (aaData.size() < 800000) {
				for (TransactionSearchNew transactionSearch : aaData) {
					row = sheet.createRow(rownum++);
					transactionSearch.setSrNo(String.valueOf(rownum - 1));
					Object[] objArr = transactionSearch.myCsvMethod();

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

			try {
				String FILE_EXTENSION = ".xlsx";
				DateFormat df = new SimpleDateFormat("yyyyMMddhhmmss");
				filename = "Total_Transaction_Report" + df.format(new Date()) + FILE_EXTENSION;
				File file = new File(PropertiesManager.propertiesMap.get(Constants.DOWNLOAD_PATH.getValue()) +File.separator+filename);
	            
	              logger.info("moni........"+PropertiesManager.propertiesMap.get(Constants.DOWNLOAD_PATH.getValue()));
				// this Writes the workbook
				FileOutputStream out = new FileOutputStream(file);
				wb.write(out);
				out.flush();
				out.close();
				wb.dispose();
				fileInputStream = new FileInputStream(file);
				logger.info("moni>>>>>>"+file);
				logger.info("File generated successfully for DownloadMisReport");
				addActionMessage(filename + " written successfully on disk.");
			} catch (Exception exception) {
				logger.error("Exception", exception);
			}

			return SUCCESS;

		} catch (Exception e) {
			logger.error("Exception1 " + e);
			return SUCCESS;
		} finally {
			try {
				wb.close();
			} catch (IOException e) {
				logger.error("Exception", e);
				e.printStackTrace();
			}
		}

	}

	public InputStream getFileInputStream() {
		return fileInputStream;
	}

	public void setFileInputStream(InputStream fileInputStream) {
		this.fileInputStream = fileInputStream;
	}

	public String getFilename() {
		return filename;
	}

	public void setFilename(String filename) {
		this.filename = filename;
	}

	public String getPayId() {
		return payId;
	}

	public void setPayId(String payId) {
		this.payId = payId;
	}

	public String getAcq() {
		return acq;
	}

	public void setAcq(String acq) {
		this.acq = acq;
	}

	public String getdFrom() {
		return dFrom;
	}

	public void setdFrom(String dFrom) {
		this.dFrom = dFrom;
	}

	public String getdTo() {
		return dTo;
	}

	public void setdTo(String dTo) {
		this.dTo = dTo;
	}

	public String getsFrom() {
		return sFrom;
	}

	public void setsFrom(String sFrom) {
		this.sFrom = sFrom;
	}

	public String getsTo() {
		return sTo;
	}

	public void setsTo(String sTo) {
		this.sTo = sTo;
	}

	public String getStatusType() {
		return statusType;
	}

	public void setStatusType(String statusType) {
		this.statusType = statusType;
	}

}
