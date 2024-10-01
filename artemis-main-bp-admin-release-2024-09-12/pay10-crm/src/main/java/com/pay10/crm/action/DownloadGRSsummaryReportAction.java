package com.pay10.crm.action;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
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
import com.pay10.commons.dto.GRS;
import com.pay10.commons.user.Merchants;
import com.pay10.commons.user.User;
import com.pay10.commons.user.UserDao;
import com.pay10.commons.util.Constants;
import com.pay10.commons.util.DataEncoder;
import com.pay10.commons.util.PropertiesManager;
import com.pay10.crm.mongoReports.TxnReportForGrs;

public class DownloadGRSsummaryReportAction  extends AbstractSecureAction {

	private static final long serialVersionUID = -1806801325621922073L;
	private static Logger logger = LoggerFactory.getLogger(DownloadGRSsummaryReportAction.class.getName());
	List<Merchants> merchantList = new ArrayList<>();
	List<String> statuss = new ArrayList<>();
	List<String> ruleTypee = new ArrayList<>();
	

	private BigInteger recordsTotal = BigInteger.ZERO;
	private BigInteger recordsFiltered = BigInteger.ZERO;

	private String merchant;
	private String status;
	private String dateFrom;
	private String dateTo;
	private User sessionUser = new User();
	private String filename;
	private InputStream fileInputStream;
	

	@Autowired
	private DataEncoder encoder;

	@Autowired
	private UserDao userDao;
	
	@Autowired
	private TxnReportForGrs forGrs;

	HttpServletRequest request = ServletActionContext.getRequest();

	HttpServletResponse response = ServletActionContext.getResponse();
	
	private List<GRS> aaData = new ArrayList<>();
	
	public String execute() {
		SXSSFWorkbook wb=null;
		try {
			setAaData(forGrs.grsReport(merchant, status, dateFrom,
					dateTo,0,1000000));
		logger.info("List generated successfully for DownloadPaymentsReportAction , size = " + aaData.size());

		wb = new SXSSFWorkbook(100);
		Row row;
		int rownum = 1;
		// Create a blank sheet
		Sheet sheet = wb.createSheet("GRS Summary Report");
		row = sheet.createRow(0);		

		row.createCell(0).setCellValue("Pg Ref Number");
		row.createCell(1).setCellValue("GRS Id");
		row.createCell(2).setCellValue("Merchant Name");
		row.createCell(3).setCellValue("GRS Title");
		row.createCell(4).setCellValue("Amount");
		
		row.createCell(5).setCellValue("Total Amount");
		row.createCell(6).setCellValue("Status");
		row.createCell(7).setCellValue("Order Id");
		row.createCell(8).setCellValue("Created Date");
		row.createCell(9).setCellValue("Created By");
		
		row.createCell(10).setCellValue("Txn Date");
		row.createCell(11).setCellValue("Payment Method");
		row.createCell(12).setCellValue("Mop Type");
		
		row.createCell(13).setCellValue("Pay Id");
		row.createCell(14).setCellValue("Customer Name");
		row.createCell(15).setCellValue("Customer Phone");
		
		row.createCell(16).setCellValue("Updated By");
		row.createCell(17).setCellValue("Updated At");
		

		if (aaData.size() < 800000) {

			for (GRS transactionSearch : aaData) {
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
			filename = "GRS_Summary_Report_" + df.format(new Date()) + FILE_EXTENSION;
			File file = new File(PropertiesManager.propertiesMap.get(Constants.DOWNLOAD_PATH.getValue()) +File.separator+filename);

			// this Writes the workbook
			FileOutputStream out = new FileOutputStream(file);
			wb.write(out);
			out.flush();
			out.close();
			wb.dispose();
			fileInputStream = new FileInputStream(file);
			addActionMessage(filename + " written successfully on disk.");
			logger.info("File generated successfully for Download GRS Summary Report");
		} catch (Exception exception) {
			logger.error("Exception", exception);
			exception.printStackTrace();
		}
		
		}catch(Exception e) {
			logger.error("Exception Occur in Download GRS Summary Report : ",e);
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

	public List<GRS> getAaData() {
		return aaData;
	}

	public void setAaData(List<GRS> aaData) {
		this.aaData = aaData;
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

	public String getMerchant() {
		return merchant;
	}

	public void setMerchant(String merchant) {
		this.merchant = merchant;
	}

	
	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}


	/*
	 * public int getDraw() { return draw; }
	 * 
	 * public void setDraw(int draw) { this.draw = draw; }
	 * 
	 * public int getLength() { return length; }
	 * 
	 * public void setLength(int length) { this.length = length; }
	 * 
	 * public int getStart() { return start; }
	 * 
	 * public void setStart(int start) { this.start = start; }
	 */

	public BigInteger getRecordsTotal() {
		return recordsTotal;
	}

	public void setRecordsTotal(BigInteger recordsTotal) {
		this.recordsTotal = recordsTotal;
	}

	public BigInteger getRecordsFiltered() {
		return recordsFiltered;
	}

	public void setRecordsFiltered(BigInteger recordsFiltered) {
		this.recordsFiltered = recordsFiltered;
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
