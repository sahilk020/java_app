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

import com.pay10.commons.user.MISReportObject;
import com.pay10.commons.util.Constants;
import com.pay10.commons.util.PropertiesManager;
import com.pay10.crm.mongoReports.TransactionStatus;

public class DownloadPreMisSettlementReport extends AbstractSecureAction{
	private static final long serialVersionUID = -6497286461324980048L;
	private static Logger logger = LoggerFactory.getLogger(XssFormatFileDownload.class.getName());
	@Autowired
	private TransactionStatus transactionStatus;
	 @Autowired
	    private PropertiesManager propertiesManager;
	private String payId;
	private String dateFrom;
	private String acquirer;
	private String filename;
	private InputStream fileInputStream;

	
	public String getAcquirer() {
		return acquirer;
	}

	public void setAcquirer(String acquirer) {
		this.acquirer = acquirer;
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

	public String getPayId() {
		return payId;
	}

	public void setPayId(String payId) {
		this.payId = payId;
	}

	public String getDateFrom() {
		return dateFrom;
	}

	public void setDateFrom(String dateFrom) {
		this.dateFrom = dateFrom;
	}

	@Override
	public String execute() {
				try {
					
				logger.info("DownloadPreMisSettlementReport-------- Inside Pre-MIS Settlement Report Download");	
				List<MISReportObject> transactionList = new ArrayList<MISReportObject>();
				transactionList = transactionStatus.getPreMisSettlementReport(payId, dateFrom,acquirer);

				logger.info("DownloadPreMisSettlementReport------------List generated successfully for Pre-MIS Settlement Report , size = "+transactionList.size());
				SXSSFWorkbook wb = new SXSSFWorkbook(100);
				Row row;
				int rownum = 1;
				// Create a blank sheet
				Sheet sheet = wb.createSheet("PRE_MIS_Settlement_Report");
				row = sheet.createRow(0);

				row.createCell(0).setCellValue("Sr No");
				row.createCell(1).setCellValue("Merchant Name");
				row.createCell(2).setCellValue("MID");
				row.createCell(3).setCellValue("PG_REF_NUM");
				row.createCell(4).setCellValue("Order_ID");
				row.createCell(5).setCellValue("Transaction Date");
//				row.createCell(6).setCellValue("Settlement Date");
				row.createCell(6).setCellValue("Transaction type");
				row.createCell(7).setCellValue("Gross Transaction Amt");
				row.createCell(8).setCellValue("Total Aggregator Commission Amt Payable(Including GST)");
				row.createCell(9).setCellValue("Total Acquirer Commission Amt Payable(Including GST)");
				row.createCell(10).setCellValue("Total Amt Payable to Merchant A/c");
				row.createCell(11).setCellValue("Total Payout from Nodal Account");
				row.createCell(12).setCellValue("BankName_Receive_Funds");
				row.createCell(13).setCellValue("Nodal a/c no");
				row.createCell(14).setCellValue("Aggregator name");
				row.createCell(15).setCellValue("Acquirer Name");
				row.createCell(16).setCellValue("Refund Flag");
				row.createCell(17).setCellValue("Payments Type");
				row.createCell(18).setCellValue("MOP Type");
				row.createCell(19).setCellValue("Surcharge Flag");
				row.createCell(20).setCellValue("Settlement Cycle");
				row.createCell(21).setCellValue("Account Number");
				
				if (transactionList.size() < 800000) {
					for (MISReportObject transactionSearch : transactionList) {
						row = sheet.createRow(rownum++);
						transactionSearch.setSrNo(String.valueOf(rownum-1));
						Object[] objArr = transactionSearch.preMisCsvMethod();

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
					filename = "PRE_MIS_Settlement_Report" + df.format(new Date()) + FILE_EXTENSION;
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
					logger.info("File generated successfully for MIS Report");
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
}
