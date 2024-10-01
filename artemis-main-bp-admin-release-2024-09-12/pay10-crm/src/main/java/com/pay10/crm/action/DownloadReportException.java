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

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.pay10.commons.dto.ReportingCollection;
import com.pay10.commons.user.MISReportObject;
import com.pay10.commons.user.TransactionSearchNew;
import com.pay10.commons.util.Constants;
import com.pay10.commons.util.PropertiesManager;
import com.pay10.crm.mongoReports.DashboardService;
import com.pay10.crm.mongoReports.TxnReports;

public class DownloadReportException extends AbstractSecureAction {

	private static final long serialVersionUID = -6497286461324980048L;
	private static Logger logger = LoggerFactory.getLogger(XssFormatFileDownload.class.getName());

	@Autowired
	private TxnReports txnReports;
	 @Autowired
	    private PropertiesManager propertiesManager;
	

	private String payId;
	private String acq;
	private String dFrom;
	private String dTo;
	private String filename;
	private String sFrom;
	private String sTo;
	private String statusType;
	private InputStream fileInputStream;
	private String type;
	private String dateFrom;

	@Autowired
	private DashboardService dashboardService;

	@Override
	public String execute() {
		SXSSFWorkbook wb = null;
		try {

			logger.info("Exception_Report-------- ");
			List<ReportingCollection> aaData=null;
			if (dFrom != null && dTo != null) {
				dFrom=dFrom+" 00:00:00";
				dTo=dTo+" 23:59:59";
				aaData = dashboardService.dashboardExceptionReport(type, 0, 800000, dFrom,
						dTo);
			} else {
				dTo=dFrom+" 23:59:59";
				dFrom=dFrom+" 00:00:00";
				
				aaData = dashboardService.dashboardExceptionReport(type, 0, 800000, dFrom,
						dTo);
			}

			logger.info(
					"Exception_Report------------List generated successfully for DownloadMisReport Report , size = "
							+ aaData.size());
			wb = new SXSSFWorkbook(100);
			Row row;
			int rownum = 1;
			// Create a blank sheet
			Sheet sheet = wb.createSheet("Exception_Report");
			row = sheet.createRow(0);

			row.createCell(0).setCellValue("SrNo");
			row.createCell(1).setCellValue("TxnId");
			row.createCell(2).setCellValue("RECO_TXNTYPE");
			row.createCell(3).setCellValue("FILE_NAME");
			row.createCell(4).setCellValue("FILE_LINE_NO");
			row.createCell(5).setCellValue("FILE_LINE_DATA");
			row.createCell(6).setCellValue("DB_TXN_ID");
			row.createCell(7).setCellValue("DB_TXNTYPE");
			row.createCell(8).setCellValue("DB_OID");
			row.createCell(9).setCellValue("DB_ACQ_ID");
			row.createCell(10).setCellValue("DB_ORIG_TXN_ID");
			row.createCell(11).setCellValue("DB_ORIG_TXNTYPE");
			row.createCell(12).setCellValue("DB_AMOUNT");
			row.createCell(13).setCellValue("DB_PG_REF_NUM");
			row.createCell(14).setCellValue("DB_ORDER_ID");
			row.createCell(15).setCellValue("DB_PAY_ID");
			row.createCell(16).setCellValue("DB_ACQUIRER_TYPE");
			row.createCell(17).setCellValue("CREATE_DATE");
			row.createCell(18).setCellValue("UPDATE_DATE");
			row.createCell(19).setCellValue("RESPONSE_CODE");
			row.createCell(20).setCellValue("RESPONSE_MESSAGE");
			row.createCell(21).setCellValue("DB_USER_TYPE");
			row.createCell(22).setCellValue("RECO_EXCEPTION_STATUS");

			if (aaData.size() < 800000) {
				for (ReportingCollection transactionSearch : aaData) {
					row = sheet.createRow(rownum++);
					transactionSearch.setSrNo(String.valueOf(rownum - 1));
					Object[] objArr = transactionSearch.myCsvMethod();

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
				filename = "Exception_Report" + df.format(new Date()) + FILE_EXTENSION;
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
				logger.info("File generated successfully for Exception_Report");
				addActionMessage(filename + " written successfully on disk.");
			} catch (Exception exception) {
				logger.error("Exception", exception);
			}

			return SUCCESS;

		} catch (Exception e) {
			logger.error("Exception1 " + e);
			return SUCCESS;
		} finally {
			try {
				wb.close();
			} catch (IOException e) {
				logger.error("Exception", e);
				e.printStackTrace();
			}
		}

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

	public String getAcq() {
		return acq;
	}

	public void setAcq(String acq) {
		this.acq = acq;
	}

	public String getdFrom() {
		return dFrom;
	}

	public void setdFrom(String dFrom) {
		this.dFrom = dFrom;
	}

	public String getdTo() {
		return dTo;
	}

	public void setdTo(String dTo) {
		this.dTo = dTo;
	}

	public String getsFrom() {
		return sFrom;
	}

	public void setsFrom(String sFrom) {
		this.sFrom = sFrom;
	}

	public String getsTo() {
		return sTo;
	}

	public void setsTo(String sTo) {
		this.sTo = sTo;
	}

	public String getStatusType() {
		return statusType;
	}

	public void setStatusType(String statusType) {
		this.statusType = statusType;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getDateFrom() {
		return dateFrom;
	}

	public void setDateFrom(String dateFrom) {
		this.dateFrom = dateFrom;
	}

}
