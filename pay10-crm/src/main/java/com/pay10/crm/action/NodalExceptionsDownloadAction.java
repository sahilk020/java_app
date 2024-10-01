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

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.pay10.commons.exception.ErrorType;
import com.pay10.commons.user.MISReportObject;
import com.pay10.commons.util.AddNodalCreditService;
import com.pay10.commons.util.Constants;
import com.pay10.commons.util.CrmFieldConstants;
import com.pay10.commons.util.CrmFieldType;
import com.pay10.commons.util.CrmValidator;
import com.pay10.commons.util.DateCreater;
import com.pay10.commons.util.PropertiesManager;

public class NodalExceptionsDownloadAction extends AbstractSecureAction {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1471532495305994799L;
	private static Logger logger = LoggerFactory.getLogger(NodalExceptionsDownloadAction.class.getName());
	private String captureDateFrom;
	private String captureDateTo;
	private String merchantPayId;
	private InputStream fileInputStream;
	private String filename;

	public static final BigDecimal ONE_HUNDRED = new BigDecimal(100);

	@Autowired
	private CrmValidator validator;
	@Autowired
    private PropertiesManager propertiesManager;
	
	@Autowired
	private AddNodalCreditService addNodalCreditService;
	
	@Override
	@SuppressWarnings("resource")
	public String execute() {
		setCaptureDateFrom(DateCreater.toDateTimeformatCreater(captureDateFrom));
		setCaptureDateTo(DateCreater.formDateTimeformatCreater(captureDateTo));
		
		try {
			
		logger.info("Inside Nodal MIS Report Download");	
		List<MISReportObject> transactionList = new ArrayList<MISReportObject>();
		
		transactionList = addNodalCreditService.nodalExceptionReport(merchantPayId,captureDateFrom, captureDateTo);

		logger.info("List generated successfully for Nodal MIS Report");
		SXSSFWorkbook wb = new SXSSFWorkbook(100);
		Row row;
		int rownum = 1;
		// Create a blank sheet
		Sheet sheet = wb.createSheet("Nodal Exceptions Report");
		row = sheet.createRow(0);

		row.createCell(0).setCellValue("Sr No");
		row.createCell(1).setCellValue("Merchant Name");
		row.createCell(2).setCellValue("PG Ref Num");
		row.createCell(3).setCellValue("Order ID");
		row.createCell(4).setCellValue("Transaction Date");
		row.createCell(5).setCellValue("Settlement Date");
		row.createCell(6).setCellValue("Transaction Type(Sale/Refund)");
		row.createCell(7).setCellValue("Amount");
		row.createCell(8).setCellValue("Total Amount");
		row.createCell(9).setCellValue("ACQ Id");
		row.createCell(10).setCellValue("RRN");
		row.createCell(11).setCellValue("ARN");
		row.createCell(12).setCellValue("Acquirer name");
		row.createCell(13).setCellValue("Post Settled Flag");
		row.createCell(14).setCellValue("Payments Type");
		row.createCell(15).setCellValue("MOP Type");
		row.createCell(16).setCellValue("Remarks");

		for (MISReportObject transactionSearch : transactionList) {
			row = sheet.createRow(rownum++);
			transactionSearch.setSrNo(String.valueOf(rownum-1));
			Object[] objArr = transactionSearch.exceptionsNodalReport();

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
		try {
			String FILE_EXTENSION = ".xlsx";
			DateFormat df = new SimpleDateFormat("yyyyMMddhhmmss");
			filename = "Nodal_Exceptions_Report" + df.format(new Date()) + FILE_EXTENSION;
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
			logger.info("File generated successfully for Nodal Exceptions Report");
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
			} else if (DateCreater.diffDate(fromDate, toDate) > 365) {
				addFieldError(CrmFieldType.DATE_FROM.getName(), CrmFieldConstants.DATE_RANGE.getValue());
			}
		}
	}

	public InputStream getFileInputStream() {
		return fileInputStream;
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

	public String getMerchantPayId() {
		return merchantPayId;
	}

	public void setMerchantPayId(String merchantPayId) {
		this.merchantPayId = merchantPayId;
	}

}
