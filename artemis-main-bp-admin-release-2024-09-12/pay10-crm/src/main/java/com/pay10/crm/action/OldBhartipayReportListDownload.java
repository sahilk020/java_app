package com.pay10.crm.action;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.math.BigInteger;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.apache.struts2.ServletActionContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.google.gson.Gson;
import com.pay10.commons.action.AbstractSecureAction;
import com.pay10.commons.user.Merchants;
import com.pay10.commons.user.TDRBifurcationReportDetails;
import com.pay10.commons.user.TransactionSearchNew;
import com.pay10.commons.user.User;
import com.pay10.commons.user.UserDao;
import com.pay10.commons.util.Constants;
import com.pay10.commons.util.DataEncoder;
import com.pay10.commons.util.OldBhartipayDTO;
import com.pay10.commons.util.PropertiesManager;
import com.pay10.crm.mongoReports.OldBhartipayReportData;
import com.pay10.crm.mongoReports.TransactionStatus;

public class OldBhartipayReportListDownload extends AbstractSecureAction {
	private static final long serialVersionUID = -1806801325621922073L;
	private static Logger logger = LoggerFactory.getLogger(OldBhartipayReportListDownload.class.getName());
	@Autowired
	private OldBhartipayReportData bhartipayReportData;
	

	private String filename;
	private InputStream fileInputStream;
	private String status;
	private String dateFrom;
	private String dateTo;
	

	List<OldBhartipayDTO> aaData = new ArrayList<OldBhartipayDTO>();

	public String execute() {
		try(SXSSFWorkbook wb=new SXSSFWorkbook(100);){
			aaData = bhartipayReportData.getOldbhartipayReportDownload(status,dateFrom,dateTo);
			logger.info("List Size : "+aaData.size());
			
			Row row;
			int rownum = 1;
			
			Sheet sheet = wb.createSheet("OLD BHARTIPAY REPORT");
			row = sheet.createRow(0);
			
			row.createCell(0).setCellValue("Txn Id");
			row.createCell(1).setCellValue("Oid");
			row.createCell(2).setCellValue("Txn Type");
			row.createCell(3).setCellValue("Amount");
			row.createCell(4).setCellValue("Order Id");	
			row.createCell(5).setCellValue("Customer Name");
			row.createCell(6).setCellValue("PayId");
			row.createCell(7).setCellValue("Mop Type");
			row.createCell(8).setCellValue("Payment Type");
			row.createCell(9).setCellValue("Currency Code");	
			row.createCell(10).setCellValue("Status");
			row.createCell(11).setCellValue("Create Date");
			row.createCell(12).setCellValue("Pgrefnum");		
			row.createCell(13).setCellValue("Acquirer Type");
			row.createCell(14).setCellValue("Surcharge Amount");
			
			
			if (aaData.size() < 800000) {

				for (OldBhartipayDTO transactionSearch : aaData) {
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
				filename = "Old_Bhartipay_Report" + df.format(new Date()) + FILE_EXTENSION;
				File file = new File(PropertiesManager.propertiesMap.get(Constants.DOWNLOAD_PATH.getValue()) +File.separator+filename);

				// this Writes the workbook
				FileOutputStream out = new FileOutputStream(file);
				wb.write(out);
				out.flush();
				out.close();
				wb.dispose();
				fileInputStream = new FileInputStream(file);
				addActionMessage(filename + " written successfully on disk.");
				logger.info("File generated successfully for Download OldBhartipayReportListDownload Report");
			} catch (Exception exception) {
				logger.error("Exception", exception);
				exception.printStackTrace();
			}
		}catch (Exception e) {
			logger.error("Exception Occur in OldBhartipayReportListDownload : ",e);
			e.printStackTrace();
		}
		

		

		return SUCCESS;
	}

		

	public List<OldBhartipayDTO> getAaData() {
		return aaData;
	}

	public void setAaData(List<OldBhartipayDTO> aaData) {
		this.aaData = aaData;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
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



	public String getFilename() {
		return filename;
	}



	public void setFilename(String filename) {
		this.filename = filename;
	}



	public InputStream getFileInputStream() {
		return fileInputStream;
	}



	public void setFileInputStream(InputStream fileInputStream) {
		this.fileInputStream = fileInputStream;
	}
	
	
	
}
