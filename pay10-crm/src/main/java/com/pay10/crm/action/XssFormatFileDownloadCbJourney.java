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

import org.slf4j.Logger; import org.slf4j.LoggerFactory;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;

import com.pay10.commons.dao.ChargingDetailsFactory;
import com.pay10.commons.exception.ErrorType;
import com.pay10.commons.user.CbReportObject;
import com.pay10.commons.user.MISReportObject;
import com.pay10.commons.util.Constants;
import com.pay10.commons.util.CrmFieldConstants;
import com.pay10.commons.util.CrmFieldType;
import com.pay10.commons.util.CrmValidator;
import com.pay10.commons.util.DateCreater;
import com.pay10.commons.util.PropertiesManager;

public class XssFormatFileDownloadCbJourney extends AbstractSecureAction {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1471532495305994799L;
	private static Logger logger = LoggerFactory.getLogger(XssFormatFileDownloadCbJourney.class.getName());
	private String currency;
	private String dateFrom;
	private String dateTo;
	private String name;
	private String merchant;
	private String cbCaseId;
	private String pgRefNo;
	private InputStream fileInputStream;
	private String filename;

	public static final BigDecimal ONE_HUNDRED = new BigDecimal(100);

	@Autowired
	private CrmValidator validator;
	 @Autowired
	    private PropertiesManager propertiesManager;
	
	@Autowired
	ChargingDetailsFactory cdf;

	@Autowired
	private ChargebackReportQuery chargebackReportQuery;

	@Override
	@SuppressWarnings("resource")
	public String execute() {
		try {
			
		logger.info("Inside CB Report Download");	
		List<CbReportObject> transactionList = new ArrayList<CbReportObject>();
		transactionList = chargebackReportQuery.downloadCbJourneyReport(merchant, dateFrom, dateTo,cbCaseId,pgRefNo);

		logger.info("List generated successfully for CB Report , size = "+transactionList.size());
		SXSSFWorkbook wb = new SXSSFWorkbook(100);
		Row row;
		int rownum = 1;
		// Create a blank sheet
		Sheet sheet = wb.createSheet("CB Journey Report");
		row = sheet.createRow(0);

		row.createCell(0).setCellValue("Merchant Name");
		row.createCell(1).setCellValue("Cb Case Id");
		row.createCell(2).setCellValue("Date Of Txn");
		row.createCell(3).setCellValue("Transaction Amount");
		row.createCell(4).setCellValue("Pg Case Id");
		row.createCell(5).setCellValue("Merchant TxnId");
		row.createCell(6).setCellValue("Order Id");
		row.createCell(7).setCellValue("PgRefNo");
		row.createCell(8).setCellValue("Bank TxnId");
		row.createCell(9).setCellValue("CB Amount");
		row.createCell(10).setCellValue("Cb Reason");
		row.createCell(11).setCellValue("Cb Reason Code");
		row.createCell(12).setCellValue("Cb Intimation Date");
		row.createCell(13).setCellValue("Cb Dead line Date");
		row.createCell(14).setCellValue("Mode Of Payment");
		row.createCell(15).setCellValue("Acquirer Name");
		row.createCell(16).setCellValue("Settlement Date");
		row.createCell(17).setCellValue("Customer Name");
		row.createCell(18).setCellValue("Customer Phone");
		row.createCell(19).setCellValue("Email");
		row.createCell(20).setCellValue("Notification Email");
		row.createCell(21).setCellValue("Status");
		row.createCell(22).setCellValue("Action Date");

		
		if (transactionList.size() < 800000) {
			for (CbReportObject transactionSearch : transactionList) {
				row = sheet.createRow(rownum++);
				//transactionSearch.setSrNo(String.valueOf(rownum-1));
				Object[] objArr =transactionSearch.myCsvMethod();

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
			filename = "CBJourneyReport" + df.format(new Date()) + FILE_EXTENSION;
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
			logger.info("File generated successfully for CB Journey Report");
			addActionMessage(filename + " written successfully on disk.");
		} catch (Exception exception) {
			logger.error("Exception", exception);
		}

		

		}
		catch(Exception e) {
			logger.error("Exception1 "+e);
			
		}
		return SUCCESS;
	}
	
//	@Override
//	public void validate() {
//		
//		if (validator.validateBlankField(getDateTo())) {
//		} else if (!validator.validateField(CrmFieldType.DATE_TO, getDateTo())) {
//			addFieldError(CrmFieldType.DATE_TO.getName(), ErrorType.INVALID_FIELD.getResponseMessage());
//		}	
//
//		if (!validator.validateBlankField(getDateTo())) {
//			if (DateCreater.formatStringToDate(DateCreater.formatFromDate(getDateFrom()))
//					.compareTo(DateCreater.formatStringToDate(DateCreater.formatFromDate(getDateTo()))) > 0) {
//				addFieldError(CrmFieldType.DATE_FROM.getName(), CrmFieldConstants.FROMTO_DATE_VALIDATION.getValue());
//			} else if (DateCreater.diffDate(getDateFrom(), getDateTo()) > 7) {
//				addFieldError(CrmFieldType.DATE_FROM.getName(), CrmFieldConstants.DATE_RANGE_WEEK.getValue());
//			}
//		}
//	}

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

	public String getCbCaseId() {
		return cbCaseId;
	}

	public void setCbCaseId(String cbCaseId) {
		this.cbCaseId = cbCaseId;
	}

	public String getPgRefNo() {
		return pgRefNo;
	}

	public void setPgRefNo(String pgRefNo) {
		this.pgRefNo = pgRefNo;
	}

}
