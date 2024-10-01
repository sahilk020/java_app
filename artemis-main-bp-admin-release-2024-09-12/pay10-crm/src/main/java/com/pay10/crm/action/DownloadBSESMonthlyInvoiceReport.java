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
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.apache.struts2.ServletActionContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.pay10.commons.action.AbstractSecureAction;
import com.pay10.commons.user.BSESMonthlyInvoiceReportDetails;
import com.pay10.commons.util.Constants;
import com.pay10.commons.util.PropertiesManager;
import com.pay10.crm.mongoReports.BSESInvoiceReport;

public class DownloadBSESMonthlyInvoiceReport  extends AbstractSecureAction {
	@Autowired
	private BSESInvoiceReport bsesInvoiceReport;
	private static final long serialVersionUID = -1806801325621922073L;
	private static Logger logger = LoggerFactory.getLogger(DownloadBSESMonthlyInvoiceReport.class.getName());
	
	private String filename;
	private InputStream fileInputStream;
	private String year;
	private String month;
	private String merchant;

	HttpServletRequest request = ServletActionContext.getRequest();

	HttpServletResponse response = ServletActionContext.getResponse();
	private List<BSESMonthlyInvoiceReportDetails> bsesMonthlyInvoiceReportDetails = new ArrayList<BSESMonthlyInvoiceReportDetails>();
	
	public String execute() {
		bsesMonthlyInvoiceReportDetails=bsesInvoiceReport.BSESMonthlyInvoiceReportList(merchant, year, month);
		SXSSFWorkbook wb=null;
		try {
		logger.info("List generated successfully for DownloadPaymentsReportAction , size = " + bsesMonthlyInvoiceReportDetails.size());

		wb = new SXSSFWorkbook(100);
		Row row;
		int rownum = 1;
		// Create a blank sheet
		Sheet sheet = wb.createSheet("Monthly Invoice Report");
		row = sheet.createRow(0);		

		row.createCell(0).setCellValue("PG REF No.");
		row.createCell(1).setCellValue("PayId");
		row.createCell(2).setCellValue("Merchant Name");
		row.createCell(3).setCellValue("Transaction Date");
		row.createCell(4).setCellValue("Order Id");
		row.createCell(5).setCellValue("Payment Method");
		row.createCell(6).setCellValue("Txn. Type");
		row.createCell(7).setCellValue("Mop Type");
		row.createCell(8).setCellValue("Udf9");
		row.createCell(9).setCellValue("Udf10");
		row.createCell(10).setCellValue("Udf11");
		row.createCell(11).setCellValue("Amount");
		row.createCell(12).setCellValue("Invoice Value");
		row.createCell(13).setCellValue("Invoice Gst");
		row.createCell(14).setCellValue("Total Invoice Value");
				

		if (bsesMonthlyInvoiceReportDetails.size() < 800000) {

			for (BSESMonthlyInvoiceReportDetails transactionSearch : bsesMonthlyInvoiceReportDetails) {
				row = sheet.createRow(rownum++);
				Object[] objArr = transactionSearch.myCsvMethodDownloadPaymentsReport();
				int cellnum=0;
				for (Object obj:objArr) {
					Cell cell = row.createCell(cellnum++);
					if (obj instanceof String)
						cell.setCellValue((String) obj);
					else if (obj instanceof Integer)
						cell.setCellValue((Integer) obj);
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
			filename = "BSESMonthly_Invoice_Report_" + df.format(new Date()) + FILE_EXTENSION;
			File file = new File(PropertiesManager.propertiesMap.get(Constants.DOWNLOAD_PATH.getValue()) +File.separator+filename);

			// this Writes the workbook
			FileOutputStream out = new FileOutputStream(file);
			wb.write(out);
			out.flush();
			out.close();
			wb.dispose();
			fileInputStream = new FileInputStream(file);
			addActionMessage(filename + " written successfully on disk.");
			logger.info("File generated successfully for Download BSES Monthly Invoice Report");
		} catch (Exception exception) {
			logger.error("Exception", exception);
			exception.printStackTrace();
		}
		
		}catch(Exception e) {
			logger.error("Exception Occur in Download BSES Monthly Invoice Report : ",e);
			e.printStackTrace();
		}finally {
			try {
				wb.close();
			} catch (IOException e) {
				logger.error("Exception in close SXSSFWorkbook object", e);
				e.printStackTrace();
			}
		}
		return SUCCESS;
	}

	public String getYear() {
		return year;
	}

	public void setYear(String year) {
		this.year = year;
	}

	public String getMonth() {
		return month;
	}

	public void setMonth(String month) {
		this.month = month;
	}

	public String getMerchant() {
		return merchant;
	}

	public void setMerchant(String merchant) {
		this.merchant = merchant;
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
