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

import com.pay10.commons.dao.ChargingDetailsFactory;
import com.pay10.commons.exception.ErrorType;
import com.pay10.commons.user.MISReportObject;
import com.pay10.commons.util.Constants;
import com.pay10.commons.util.CrmFieldConstants;
import com.pay10.commons.util.CrmFieldType;
import com.pay10.commons.util.CrmValidator;
import com.pay10.commons.util.DateCreater;
import com.pay10.commons.util.PropertiesManager;
import com.pay10.crm.mongoReports.TransactionStatus;

public class XssFormatFileDownloadSplit extends AbstractSecureAction{

	/**
	 * 
	 */
	private static final long serialVersionUID = -1471532495305994799L;
	private static Logger logger = LoggerFactory.getLogger(XssFormatFileDownload.class.getName());
	private String currency;
	private String dateFrom;
	private String dateTo;
	private String name;
	private String merchant;
	private InputStream fileInputStream;
	private String filename;

	public static final BigDecimal ONE_HUNDRED = new BigDecimal(100);

	@Autowired
	private CrmValidator validator;
	
	@Autowired
	ChargingDetailsFactory cdf;
	 @Autowired
	    private PropertiesManager propertiesManager;

	@Autowired
	private SettlementReportQuery srq;

	@Override
	@SuppressWarnings("resource")
	public String execute() {
		if (name.isEmpty()) {
			name = "ALL";

		}
		setDateFrom(DateCreater.toDateTimeformatCreater(dateFrom));
		setDateTo(DateCreater.formDateTimeformatCreater(dateTo));
		try {
			
		logger.info("Inside Split MIS Report Download");	
		List<MISReportObject> transactionList = new ArrayList<MISReportObject>();
		transactionList = srq.settlementReportDownloadSplit(merchant, name, currency, dateFrom, dateTo);

		logger.info("List generated successfully for MIS Report , size = "+transactionList.size());
		SXSSFWorkbook wb = new SXSSFWorkbook(100);
		Row row;
		int rownum = 1;
		// Create a blank sheet
		Sheet sheet = wb.createSheet("Split MIS Report");
		row = sheet.createRow(0);

		row.createCell(0).setCellValue("Sr No");
		row.createCell(1).setCellValue("Merchant Name");
		row.createCell(2).setCellValue("MID");
		row.createCell(3).setCellValue("Transaction ID");
		row.createCell(4).setCellValue("Order_ID");
		row.createCell(5).setCellValue("Transaction Date");
		row.createCell(6).setCellValue("Settlement Date");
		row.createCell(7).setCellValue("Transaction type(Sale/Refund)");
		row.createCell(8).setCellValue("Gross Transaction Amt");
		row.createCell(9).setCellValue("Total Aggregator Commission Amt Payable(Including GST)");
		row.createCell(10).setCellValue("Total Acquirer Commission Amt Payable(Including GST)");
		row.createCell(11).setCellValue("Total Amt Payable to Merchant A/c");
		row.createCell(12).setCellValue("Total Payout from Nodal Account");
		row.createCell(13).setCellValue("BankName_Receive_Funds");
		row.createCell(14).setCellValue("Nodal a/c no");
		row.createCell(15).setCellValue("Aggregator name");
		row.createCell(16).setCellValue("Acquirer Name");
		row.createCell(17).setCellValue("Refund Flag");
		row.createCell(18).setCellValue("Payments Type");
		row.createCell(19).setCellValue("MOP Type");
		row.createCell(20).setCellValue("Account Number");
		row.createCell(21).setCellValue("IFSC Code");
		row.createCell(22).setCellValue("A/C Holder Name");
		row.createCell(23).setCellValue("Settlement Cycle");
		
		
		if (transactionList.size() < 800000) {
			for (MISReportObject transactionSearch : transactionList) {
				row = sheet.createRow(rownum++);
				transactionSearch.setSrNo(String.valueOf(rownum-1));
				Object[] objArr = transactionSearch.myCsvMethodSplit();

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
			Cell cell = row.createCell(1);
			cell.setCellValue("Data limit exceeded for excel file generation , please select a smaller date range");
		}
		
		try {
			String FILE_EXTENSION = ".xlsx";
			DateFormat df = new SimpleDateFormat("yyyyMMddhhmmss");
			filename = "Split_MIS_Report" + df.format(new Date()) + FILE_EXTENSION;
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
			logger.info("File generated successfully for Split MIS Report");
			addActionMessage(filename + " written successfully on disk.");
		} catch (Exception exception) {
			logger.error("Exception", exception);
		}

		return SUCCESS;

		}
		catch(Exception e) {
			logger.error("Exception1 "+e);
			return SUCCESS;
		}
	}
	
	@Override
	public void validate() {
		
		if (validator.validateBlankField(getDateTo())) {
		} else if (!validator.validateField(CrmFieldType.DATE_TO, getDateTo())) {
			addFieldError(CrmFieldType.DATE_TO.getName(), ErrorType.INVALID_FIELD.getResponseMessage());
		}

		if (!validator.validateBlankField(getDateTo())) {
			if (DateCreater.formatStringToDate(DateCreater.formatFromDate(getDateFrom()))
					.compareTo(DateCreater.formatStringToDate(DateCreater.formatFromDate(getDateTo()))) > 0) {
				addFieldError(CrmFieldType.DATE_FROM.getName(), CrmFieldConstants.FROMTO_DATE_VALIDATION.getValue());
			} else if (DateCreater.diffDate(getDateFrom(), getDateTo()) > 7) {
				addFieldError(CrmFieldType.DATE_FROM.getName(), CrmFieldConstants.DATE_RANGE_WEEK.getValue());
			}
		}
	}

	public InputStream getFileInputStream() {
		return fileInputStream;
	}

	public String getCurrency() {
		return currency;
	}

	public void setCurrency(String currency) {
		this.currency = currency;
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

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getMerchant() {
		return merchant;
	}

	public String getFilename() {
		return filename;
	}

	public void setFilename(String filename) {
		this.filename = filename;
	}

	public void setMerchant(String merchant) {
		this.merchant = merchant;
	}

}
