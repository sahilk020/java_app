package com.pay10.crm.action;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.math.BigDecimal;
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

import com.pay10.commons.user.TransactionSearchDownloadObject;
import com.pay10.commons.user.User;
import com.pay10.commons.user.UserType;
import com.pay10.commons.util.Constants;
import com.pay10.commons.util.DateCreater;
import com.pay10.commons.util.PropertiesManager;
import com.pay10.crm.actionBeans.SessionUserIdentifier;
import com.pay10.crm.mongoReports.TxnReports;

public class GlobalDownloadPaymentsReportAction extends AbstractSecureAction {

	private static final long serialVersionUID = 2871252777725723745L;
	private static Logger logger = LoggerFactory.getLogger(GlobalDownloadPaymentsReportAction.class.getName());
	

	
	private String fieldType;
	private String dateFrom;
	private String dateTo;
	private String fieldTypeValue;

	private InputStream fileInputStream;
	private String filename;
	



	public static final BigDecimal ONE_HUNDRED = new BigDecimal(100);

	@Autowired
	private TxnReports txnReports;
	 @Autowired
	    private PropertiesManager propertiesManager;

	@Override
	@SuppressWarnings("resource")
	public String execute() {
		

		List<TransactionSearchDownloadObject> transactionList = new ArrayList<TransactionSearchDownloadObject>();
		
		transactionList = txnReports.globalSearchTransactionForDownloadNewSplitPayment(getFieldType(),getFieldTypeValue(),dateFrom, dateTo);

		logger.info("List generated successfully for DownloadPaymentsReportAction , size = " + transactionList.size());
		SXSSFWorkbook wb = new SXSSFWorkbook(100);
		Row row;
		int rownum = 1;
		// Create a blank sheet
		Sheet sheet =  wb.createSheet("Transactions Report");
		row = sheet.createRow(0);

		row.createCell(0).setCellValue("Sr No");
		row.createCell(1).setCellValue("Txn Id");
		row.createCell(2).setCellValue("Pg Ref Num");
		row.createCell(3).setCellValue("Merchant");
		row.createCell(4).setCellValue("Acquirer");
		row.createCell(5).setCellValue("Date");
		row.createCell(6).setCellValue("Order Id");
		row.createCell(7).setCellValue("Payment Method");
		row.createCell(8).setCellValue("Txn Type");
		row.createCell(9).setCellValue("Status");
		row.createCell(10).setCellValue("Transaction Region");
		row.createCell(11).setCellValue("Base Amount");
		row.createCell(12).setCellValue("Total Amount");
		row.createCell(13).setCellValue("Delta Refund Flag");
		row.createCell(14).setCellValue("ACQ ID");
		row.createCell(15).setCellValue("RRN");
		row.createCell(16).setCellValue("Post Settled Flag");
		row.createCell(17).setCellValue("Refund Order ID");
		row.createCell(18).setCellValue("Refund Flag");
		row.createCell(19).setCellValue("Mop Type");
		row.createCell(20).setCellValue("IP Address");
		row.createCell(21).setCellValue("Card Mask");
		row.createCell(22).setCellValue("Customer Email");
		row.createCell(23).setCellValue("Customer Phone");
		row.createCell(24).setCellValue("Acquirer Response");
		row.createCell(25).setCellValue("UDF4");
		row.createCell(26).setCellValue("UDF5");
		row.createCell(27).setCellValue("UDF6");

		if (transactionList.size() < 800000) {
			
			for (TransactionSearchDownloadObject transactionSearch : transactionList) {
				row = sheet.createRow(rownum++);
				transactionSearch.setSrNo(String.valueOf(rownum-1));
				Object[] objArr = transactionSearch.myCsvMethodDownloadPaymentsReport();

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
		}
		else {
			
			
			row = sheet.createRow(rownum++);
			// this line creates a cell in the next column of that row
			Cell cell = row.createCell(1);
			cell.setCellValue("Data limit exceeded for excel file generation , please select a smaller date range");
		}
			
		
		try {
			String FILE_EXTENSION = ".xlsx";
			DateFormat df = new SimpleDateFormat("yyyyMMddhhmmss");
			filename = "SearchPayment_Transactions_" + df.format(new Date()) + FILE_EXTENSION;
			File file = new File(PropertiesManager.propertiesMap.get(Constants.DOWNLOAD_PATH.getValue()) +File.separator+filename);
            
            logger.info("moni........"+PropertiesManager.propertiesMap.get(Constants.DOWNLOAD_PATH.getValue()));
			// this Writes the workbook
			FileOutputStream out = new FileOutputStream(file);
			wb.write(out);
			out.flush();
			out.close();
			wb.dispose();
			fileInputStream = new FileInputStream(file);
			addActionMessage(filename + " written successfully on disk.");
			logger.info("File generated successfully for DownloadPaymentsReportAction");
		} catch (Exception exception) {
			logger.error("Exception", exception);
		}

		return SUCCESS;

	}

	public String getFieldType() {
		return fieldType;
	}

	public void setFieldType(String fieldType) {
		this.fieldType = fieldType;
	}

	public String getDateFrom() {
		return dateFrom;
	}

	public void setDateFrom(String dateFrom) {
		this.dateFrom = dateFrom;
	}

	public String getDateTo() {
		return dateTo;
	}

	public void setDateTo(String dateTo) {
		this.dateTo = dateTo;
	}

	public String getFieldTypeValue() {
		return fieldTypeValue;
	}

	public void setFieldTypeValue(String fieldTypeValue) {
		this.fieldTypeValue = fieldTypeValue;
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


	

}
