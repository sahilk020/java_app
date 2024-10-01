package com.pay10.crm.action;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.pay10.commons.user.TransactionSearchDownloadObject;
import com.pay10.commons.user.User;
import com.pay10.commons.user.UserType;
import com.pay10.commons.util.Constants;
import com.pay10.commons.util.DateCreater;
import com.pay10.commons.util.PropertiesManager;
import com.pay10.crm.actionBeans.SessionUserIdentifier;
import com.pay10.crm.mongoReports.TxnReports;

public class DownloadBulkReportSearch extends AbstractSecureAction {

	private static final long serialVersionUID = 2871252777725723745L;
	private static Logger logger = LoggerFactory.getLogger(DownloadBulkReportSearch.class.getName());

	@Autowired
	private SessionUserIdentifier userIdentifier;
	@Autowired
	private PropertiesManager propertiesManager;
	private String pgRefNum;

	public String getPgRefNum() {
		return pgRefNum;
	}

	public void setPgRefNum(String pgRefNum) {
		this.pgRefNum = pgRefNum;
	}

	private InputStream fileInputStream;
	private String filename;
	private User sessionUser = new User();

	private String orderId;

	private boolean needToShowAcquirerDetails = true;

	public String getTotalAmount() {
		return totalAmount;
	}

	public void setTotalAmount(String totalAmount) {
		this.totalAmount = totalAmount;
	}

	private String totalAmount;
	private String rrn;

	public String getRrn() {
		return rrn;
	}

	public void setRrn(String rrn) {
		this.rrn = rrn;
	}

	private String tenantId;

	public static final BigDecimal ONE_HUNDRED = new BigDecimal(100);

	@Autowired
	private TxnReports txnReports;

	@Override
	@SuppressWarnings("resource")
	public String execute() {

		List<TransactionSearchDownloadObject> transactionList = new ArrayList<TransactionSearchDownloadObject>();

		logger.info("datt for download " + pgRefNum + rrn + orderId);

		transactionList = txnReports.bulkTransactionForDownload(pgRefNum, rrn, orderId);

		logger.info("List generated successfully for DownloadPaymentsReportAction , size = " + transactionList.size());

		SXSSFWorkbook wb = new SXSSFWorkbook(100);
		Row row;
		int rownum = 1;
		// Create a blank sheet
		Sheet sheet = wb.createSheet("Transactions Report");
		row = sheet.createRow(0);
		boolean splitPaymentSegment = false;
		List<String> headers = getHeaders(splitPaymentSegment);
		for (int i = 0; i < headers.size(); i++) {
			row.createCell(i).setCellValue(headers.get(i));
		}

		if (transactionList.size() < 800000) {

			for (TransactionSearchDownloadObject transactionSearch : transactionList) {
				row = sheet.createRow(rownum++);
				transactionSearch.setSrNo(String.valueOf(rownum - 1));
				List<Object> objArr = transactionSearch.myCsvMethodDownloadPaymentsReport(splitPaymentSegment,
						isNeedToShowAcquirerDetails());
				for (int cellnum = 0; cellnum < objArr.size(); cellnum++) {
					Cell cell = row.createCell(cellnum);
					if (objArr.get(cellnum) instanceof String)
						cell.setCellValue((String) objArr.get(cellnum));
					else if (objArr.get(cellnum) instanceof Integer)
						cell.setCellValue((Integer) objArr.get(cellnum));
				}
			}
		} else {

			row = sheet.createRow(rownum++);
			// this line creates a cell in the next column of that row
			Cell cell = row.createCell(1);
			cell.setCellValue("Data limit exceeded for excel file generation , please select a smaller date range");
		}

		try {
			String FILE_EXTENSION = ".xlsx";
			DateFormat df = new SimpleDateFormat("yyyyMMddhhmmss");
			filename = "SearchPayment_Transactions_" + df.format(new Date()) + FILE_EXTENSION;
			File file = new File(PropertiesManager.propertiesMap.get(Constants.DOWNLOAD_PATH.getValue())
					+ File.separator + filename);

			logger.info("moni........" + PropertiesManager.propertiesMap.get(Constants.DOWNLOAD_PATH.getValue()));
			// this Writes the workbook
			FileOutputStream out = new FileOutputStream(file);
			wb.write(out);
			out.flush();
			out.close();
			wb.dispose();
			fileInputStream = new FileInputStream(file);
			logger.info("moni>>>>>>" + file);
			addActionMessage(filename + " written successfully on disk.");
			logger.info("File generated successfully for DownloadPaymentsReportAction");
		} catch (Exception exception) {
			logger.error("Exception", exception);
		}

		return SUCCESS;

	}

	private List<String> getHeaders(boolean segment) {
		List<String> headers = new ArrayList<>();
		headers.add("Sr No");
		headers.add("Txn Id");
		headers.add("Pg Ref Num");
		headers.add("Merchant");
		if (isNeedToShowAcquirerDetails()) {
			headers.add("Acquirer");
		}
		headers.add("Date");
		headers.add("Order Id");
		if (isNeedToShowAcquirerDetails()) {
			headers.add("Payment Method");
		}
		headers.add("Txn Type");
		headers.add("Status");
		headers.add("Transaction Region");
		headers.add("Base Amount");
		headers.add("Total Amount");
		if (!segment) {
			//headers.add("Delta Refund Flag");
			//headers.add("ACQ ID");
		}
		headers.add("RRN");
		//if (!segment) {
		//headers.add("Post Settled Flag");
		//}
		//headers.add("Refund Order ID");
		//if (!segment) {
		//headers.add("Refund Flag");
		//}
		if (isNeedToShowAcquirerDetails()) {
			headers.add("Mop Type");
		}
		if (!segment) {
			headers.add("IP Address");
		}
		headers.add("Card Mask");
		headers.add("Customer Email");
		headers.add("Customer Phone");
		if (!segment) {
			headers.add("Acquirer Response");
		}
		//	headers.add("UDF4");
		//	headers.add("UDF5");
		//	headers.add("UDF6");
		if (!segment) {
			headers.add("UTR");
		}
		headers.add("Card Holder Type");
		headers.add("Bank Ref No");
		headers.add("PSP Name");
		headers.add("Currency");

		return headers;
	}

	public InputStream getFileInputStream() {
		return fileInputStream;
	}

	public String getTenantId() {
		return tenantId;
	}

	public void setTenantId(String tenantId) {
		this.tenantId = tenantId;
	}

	public String getFilename() {
		return filename;
	}

	public void setFilename(String filename) {
		this.filename = filename;
	}

	public String getOrderId() {
		return orderId;
	}

	public void setOrderId(String orderId) {
		this.orderId = orderId;
	}

	public boolean isNeedToShowAcquirerDetails() {
		return needToShowAcquirerDetails;
	}

	public void setNeedToShowAcquirerDetails(boolean needToShowAcquirerDetails) {
		this.needToShowAcquirerDetails = needToShowAcquirerDetails;
	}

}
