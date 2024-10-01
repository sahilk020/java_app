package com.pay10.crm.action;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
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

import com.pay10.commons.user.LiabilityReportObject;
import com.pay10.commons.util.Constants;
import com.pay10.commons.util.PropertiesManager;
import com.pay10.crm.mongoReports.LiabilityReportService;

public class DownloadliabilityReport extends AbstractSecureAction {

	private static final long serialVersionUID = 1L;

	@Autowired
	LiabilityReportService liabilityReportService;
	@Autowired
    private PropertiesManager propertiesManager;
	
	private static Logger logger = LoggerFactory.getLogger(DownloadliabilityReport.class.getName());
	
	private String merchant;
	private String acquirer;
	private String dateFrom;
	private String dateTo;
	private String reportType;
	private InputStream fileInputStream;
	private String filename;

	
	
	
	
	public String getMerchant() {
		return merchant;
	}





	public void setMerchant(String merchant) {
		this.merchant = merchant;
	}





	public String getAcquirer() {
		return acquirer;
	}





	public void setAcquirer(String acquirer) {
		this.acquirer = acquirer;
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





	public String getReportType() {
		return reportType;
	}





	public void setReportType(String reportType) {
		this.reportType = reportType;
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





	@Override
	public String execute() {

		try {

			logger.info("Inside Liability Report Download");
			List<LiabilityReportObject> transactionList = new ArrayList<LiabilityReportObject>();
			transactionList = liabilityReportService.getLiabilityReport(getMerchant(), getAcquirer(), getReportType(),
					getDateFrom(), getDateTo());

			logger.info("List generated successfully for Liability Report , size = " + transactionList.size());
			SXSSFWorkbook wb = new SXSSFWorkbook(100);
			Row row;
			int rownum = 1;
			// Create a blank sheet
			Sheet sheet = wb.createSheet("Liability Report");
			row = sheet.createRow(0);

			if (reportType.equalsIgnoreCase("LiabilityAudit")) {
				row.createCell(0).setCellValue("Sr No");
				row.createCell(1).setCellValue("Merchant Name");
				row.createCell(2).setCellValue("Hold Date");
				row.createCell(3).setCellValue("Release Date");
				row.createCell(4).setCellValue("Mop Type");
				row.createCell(5).setCellValue("Total Amount");
				row.createCell(6).setCellValue("Pg Ref Number");
				row.createCell(7).setCellValue("Date");
				row.createCell(8).setCellValue("Txn type");
				row.createCell(9).setCellValue("Payment Type");
				row.createCell(10).setCellValue("Status");
				row.createCell(11).setCellValue("Acquirer Type");
				row.createCell(12).setCellValue("Days");
				row.createCell(13).setCellValue("Liability Release Remark");
				row.createCell(14).setCellValue("Liability Hold Remark");

			} else if (reportType.equalsIgnoreCase("Hold")) {
				row.createCell(0).setCellValue("Sr No");
				row.createCell(1).setCellValue("Merchant Name");
				row.createCell(2).setCellValue("Hold Date");
				row.createCell(3).setCellValue("Mop Type");
				row.createCell(4).setCellValue("Total Amount");
				row.createCell(5).setCellValue("Pg Ref Number");
				row.createCell(6).setCellValue("Date");
				row.createCell(7).setCellValue("Txn type");
				row.createCell(8).setCellValue("Payment Type");
				row.createCell(9).setCellValue("Status");
				row.createCell(10).setCellValue("Acquirer Type");
				row.createCell(11).setCellValue("Liability Hold Remark");
			} else {
				row.createCell(0).setCellValue("Sr No");
				row.createCell(1).setCellValue("Merchant Name");
				row.createCell(2).setCellValue("Release Date");
				row.createCell(3).setCellValue("Mop Type");
				row.createCell(4).setCellValue("Total Amount");
				row.createCell(5).setCellValue("Pg Ref Number");
				row.createCell(6).setCellValue("Date");
				row.createCell(7).setCellValue("Txn type");
				row.createCell(8).setCellValue("Payment Type");
				row.createCell(9).setCellValue("Status");
				row.createCell(10).setCellValue("Acquirer Type");
			}

			if (transactionList.size() < 800000) {
				for (LiabilityReportObject transactionSearch : transactionList) {
					row = sheet.createRow(rownum++);
					transactionSearch.setSrNo(String.valueOf(rownum - 1));
					Object[] objArr = null;
					if (reportType.equalsIgnoreCase("LiabilityAudit")) {
						objArr = transactionSearch.objectForAudit();
					} else if (reportType.equalsIgnoreCase("Hold")) {
						objArr = transactionSearch.objectForHold();
					} else {
						objArr = transactionSearch.objectForRelease();
					}
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
				filename = "Risk Report" + df.format(new Date()) + FILE_EXTENSION;
				File file = new File(PropertiesManager.propertiesMap.get(Constants.DOWNLOAD_PATH.getValue()) +File.separator+filename);
	            
	              logger.info("moni........"+PropertiesManager.propertiesMap.get(Constants.DOWNLOAD_PATH.getValue()));
				// this Writes the workbook
				FileOutputStream out = new FileOutputStream(file);
				wb.write(out);
				out.flush();
				out.close();
				wb.dispose();
				fileInputStream = new FileInputStream(file);
				logger.info("File generated successfully for Liability Report");
				addActionMessage(filename + " written successfully on disk.");
			} catch (Exception exception) {
				logger.error("Exception", exception);
				exception.printStackTrace();
			}

			return SUCCESS;

		} catch (Exception e) {
			logger.error("Exception1 " + e);
			e.printStackTrace();
			return SUCCESS;
		}

	}
}
