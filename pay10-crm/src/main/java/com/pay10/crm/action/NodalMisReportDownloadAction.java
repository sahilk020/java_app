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
import java.util.Locale;

import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.pay10.commons.exception.ErrorType;
import com.pay10.commons.user.MISReportObject;
import com.pay10.commons.user.NodalPayoutPreviewObject;
import com.pay10.commons.user.UserDao;
import com.pay10.commons.util.AddNodalCreditService;
import com.pay10.commons.util.Constants;
import com.pay10.commons.util.CrmFieldConstants;
import com.pay10.commons.util.CrmFieldType;
import com.pay10.commons.util.CrmValidator;
import com.pay10.commons.util.DateCreater;
import com.pay10.commons.util.PropertiesManager;

public class NodalMisReportDownloadAction extends AbstractSecureAction {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1471532495305994799L;
	private static Logger logger = LoggerFactory.getLogger(NodalMisReportDownloadAction.class.getName());
	private String currency;
	private String captureDateFrom;
	private String captureDateTo;
	private String payoutDate;
	private String acquirer;
	private boolean markInitiated;
	private String merchantPayId;
	private InputStream fileInputStream;
	private String filename;
	List<NodalPayoutPreviewObject> aaData = new ArrayList<NodalPayoutPreviewObject>();
			
	public static final BigDecimal ONE_HUNDRED = new BigDecimal(100);

	@Autowired
	private CrmValidator validator;
	  @Autowired
	    private PropertiesManager propertiesManager;
	
	@Autowired
	private UserDao userDao;
	
	@Autowired
	private AddNodalCreditService addNodalCreditService;
	
	@Override
	@SuppressWarnings("resource")
	public String execute() {
		setCaptureDateFrom(DateCreater.toDateTimeformatCreater(captureDateFrom));
		setCaptureDateTo(DateCreater.formDateTimeformatCreater(captureDateTo));
		if (StringUtils.isNotBlank(payoutDate)) {
			setPayoutDate(DateCreater.formDateTimeformatCreater(payoutDate));
		}
		
		try {
			
		logger.info("Inside Nodal MIS Report Download");	
		List<MISReportObject> transactionList = new ArrayList<MISReportObject>();
		
		if (markInitiated) {
			
			addNodalCreditService.updateAndDownloadNodalMisReport(merchantPayId, acquirer, currency, captureDateFrom, captureDateTo,payoutDate);
			transactionList = addNodalCreditService.nodalMisReportDownload(merchantPayId, acquirer, currency, captureDateFrom, captureDateTo);
		}
		else {
			transactionList = addNodalCreditService.nodalMisReportDownload(merchantPayId, acquirer, currency, captureDateFrom, captureDateTo);
		}
		
		

		logger.info("List generated successfully for Nodal MIS Report , size = " + transactionList.size());
		SXSSFWorkbook wb = new SXSSFWorkbook(100);
		Row row;
		int rownum = 1;
		// Create a blank sheet
		Sheet sheet = wb.createSheet("Nodal MIS Report");
		row = sheet.createRow(0);

		row.createCell(0).setCellValue("Sr No");
		row.createCell(1).setCellValue("Merchant Name");
		row.createCell(2).setCellValue("MID");
		row.createCell(3).setCellValue("Transaction ID");
		row.createCell(4).setCellValue("Order_ID");
		row.createCell(5).setCellValue("Transaction Date");
		row.createCell(6).setCellValue("Settlement Date");
		row.createCell(7).setCellValue("Nodal Credit Date");
		row.createCell(8).setCellValue("Nodal Payout Initiate Date");
		row.createCell(9).setCellValue("Transaction Type(Sale/Refund)");
		row.createCell(10).setCellValue("Gross Transaction Amt");
		row.createCell(11).setCellValue("Total Aggregator Commision Amt Payable(Including GST)");
		row.createCell(12).setCellValue("Total Acquirer Commision Amt Payable(Including GST)");
		row.createCell(13).setCellValue("Total Amt Payable to Merchant A/c");
		row.createCell(14).setCellValue("Total Payout from Nodal Account");
		row.createCell(15).setCellValue("BankName_Receive_Funds");
		row.createCell(16).setCellValue("Nodal a/c no");
		row.createCell(17).setCellValue("Aggregator name");
		row.createCell(18).setCellValue("Acquirer Name");
		row.createCell(19).setCellValue("Refund Flag");
		row.createCell(20).setCellValue("Payments Type");
		row.createCell(21).setCellValue("MOP Type");

		
		if(transactionList.size() < 800000) {
		
			for (MISReportObject transactionSearch : transactionList) {
				row = sheet.createRow(rownum++);
				transactionSearch.setSrNo(String.valueOf(rownum-1));
				Object[] objArr = transactionSearch.misNodalReport();

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
			filename = "Nodal_MIS_Report" + df.format(new Date()) + FILE_EXTENSION;
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
			logger.info("File generated successfully for Nodal MIS Report");
			addActionMessage(filename + " written successfully on disk.");
		} catch (Exception exception) {
			logger.error("Exception", exception);
		}

		return SUCCESS;

		}
		catch(Exception e) {
			logger.error("Exception in nodal MIS Report "+e);
			return SUCCESS;
		}
	}
	
	
	public String nodalPayoutPreview() {
		setCaptureDateFrom(DateCreater.toDateTimeformatCreater(captureDateFrom));
		setCaptureDateTo(DateCreater.formDateTimeformatCreater(captureDateTo));
		
		try {
			
		logger.info("Inside Nodal MIS Report Download");	
		List<NodalPayoutPreviewObject> transactionList = new ArrayList<NodalPayoutPreviewObject>();
		
		transactionList = addNodalCreditService.previewNodalPayout(merchantPayId, acquirer, currency, captureDateFrom, captureDateTo,payoutDate);
		setAaData(transactionList);
		
		} catch (Exception exception) {
			logger.error("Exception", exception);
		}

		return SUCCESS;

		}
	
	
	@Override
	public void validate() {
		
		DateFormat format = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss", Locale.ENGLISH);
		String fromDate = getCaptureDateFrom().replace("/", "-")+" 00:00:00";
		String toDate = getCaptureDateTo().replace("/", "-")+" 23:59:59";
		
		if (validator.validateBlankField(toDate)) {
		} else if (!validator.validateField(CrmFieldType.DATE_TO, toDate)) {
			addFieldError(CrmFieldType.DATE_TO.getName(), ErrorType.INVALID_FIELD.getResponseMessage());
		}

		if (!validator.validateBlankField(toDate)) {
			if (DateCreater.formatStringToDate(DateCreater.formatFromDate(fromDate))
					.compareTo(DateCreater.formatStringToDate(DateCreater.formatFromDate(toDate))) > 0) {
				addFieldError(CrmFieldType.DATE_FROM.getName(), CrmFieldConstants.FROMTO_DATE_VALIDATION.getValue());
			} else if (DateCreater.diffDate(fromDate, toDate) > 7) {
				addFieldError(CrmFieldType.DATE_FROM.getName(), CrmFieldConstants.DATE_RANGE.getValue());
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

	public String getFilename() {
		return filename;
	}

	public void setFilename(String filename) {
		this.filename = filename;
	}

	public String getCaptureDateFrom() {
		return captureDateFrom;
	}

	public void setCaptureDateFrom(String captureDateFrom) {
		this.captureDateFrom = captureDateFrom;
	}

	public String getCaptureDateTo() {
		return captureDateTo;
	}

	public void setCaptureDateTo(String captureDateTo) {
		this.captureDateTo = captureDateTo;
	}

	public String getAcquirer() {
		return acquirer;
	}

	public void setAcquirer(String acquirer) {
		this.acquirer = acquirer;
	}

	public String getMerchantPayId() {
		return merchantPayId;
	}

	public void setMerchantPayId(String merchantPayId) {
		this.merchantPayId = merchantPayId;
	}


	public boolean isMarkInitiated() {
		return markInitiated;
	}

	public void setMarkInitiated(boolean markInitiated) {
		this.markInitiated = markInitiated;
	}

	public String getPayoutDate() {
		return payoutDate;
	}

	public void setPayoutDate(String payoutDate) {
		this.payoutDate = payoutDate;
	}


	public List<NodalPayoutPreviewObject> getAaData() {
		return aaData;
	}


	public void setAaData(List<NodalPayoutPreviewObject> aaData) {
		this.aaData = aaData;
	}

}
