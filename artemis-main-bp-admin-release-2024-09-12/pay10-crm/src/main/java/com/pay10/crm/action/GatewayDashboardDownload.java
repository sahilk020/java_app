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
import java.util.ListIterator;

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

import com.google.gson.Gson;
import com.pay10.commons.util.Constants;
import com.pay10.commons.util.PropertiesManager;
import com.pay10.crm.actionBeans.GateWayDashboardBean;
import com.pay10.crm.mongoReports.GatewayDashboardService;

public class GatewayDashboardDownload extends AbstractSecureAction {
	private static final long serialVersionUID = 924993160136571311L;
	private static Logger logger = LoggerFactory.getLogger(GatewayDashboardDownload.class.getName());
	HttpServletRequest request = ServletActionContext.getRequest();
	HttpServletResponse response = ServletActionContext.getResponse();
	@Autowired
	GatewayDashboardService gatewayDashboardService;
	 @Autowired
	    private PropertiesManager propertiesManager;
	private InputStream fileInputStream;
	private String filename;

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

	private String acquirertype;

	public String getAcquirertype() {
		return acquirertype;
	}

	public void setAcquirertype(String acquirertype) {
		this.acquirertype = acquirertype;
	}

	@Override
	public String execute() {
		try {
			logger.info("GatewayDashboardDownload in execute()");

			logger.info("GatewayDashboardDownload in execute() acquirer : " + acquirertype);

			String output = gatewayDashboardService.getGatewayDashboardDetails(acquirertype);

			GateWayDashboardBean[] gateWayDashboardBeans = new Gson().fromJson(output, GateWayDashboardBean[].class);

			logger.info(
					"GatewayDashboardDownload------------List generated successfully for Gateway Dashboard , size = "
							+ gateWayDashboardBeans.length);
			SXSSFWorkbook wb = new SXSSFWorkbook(100);
			Row row;
			int rownum = 1;
			// Create a blank sheet
			Sheet sheet = wb.createSheet(acquirertype);
			row = sheet.createRow(0);

			row.createCell(0).setCellValue("PAYMENT TYPE");
			row.createCell(1).setCellValue("SUCEESS");
			row.createCell(2).setCellValue("FAILED");
			row.createCell(3).setCellValue("IN QUEUE");
			row.createCell(4).setCellValue("SUCCESS RATIO (%)");
			row.createCell(5).setCellValue("LAST TRANSACTION RECEIVED");
			row.createCell(6).setCellValue("STATUS");

			if (gateWayDashboardBeans.length < 800000) {
				for (GateWayDashboardBean gateWayDashboardBean : gateWayDashboardBeans) {
					row = sheet.createRow(rownum++);
					Object[] objArr = gateWayDashboardBean.gatewayDashboard();

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

			// String FILE_EXTENSION = ".xlsx";
			DateFormat df = new SimpleDateFormat("yyyyMMddhhmmss");
			filename = "Gateway Dashboard Report "+"("+acquirertype+") "+ df.format(new Date()) + ".xlsx";
			File file = new File(PropertiesManager.propertiesMap.get(Constants.DOWNLOAD_PATH.getValue()) +File.separator+filename);
            
            logger.info("moni........"+PropertiesManager.propertiesMap.get(Constants.DOWNLOAD_PATH.getValue()));
			// this Writes the workbook
			FileOutputStream out = new FileOutputStream(file);
			wb.write(out);
			out.flush();
			fileInputStream = new FileInputStream(file);
			logger.info("File generated successfully for GatewayDashboard Report and file name : "+file);

		} catch (Exception e) {
			logger.error("Exception in GatewayDashboardDownload in execute() :" + e);
			e.printStackTrace();
		}
		return SUCCESS;
	}

}
