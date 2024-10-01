package com.pay10.crm.action;

import com.google.gson.Gson;
import com.pay10.commons.util.AcquirerTypeUI;
import com.pay10.commons.util.Constants;
import com.pay10.commons.util.PropertiesManager;
import com.pay10.crm.audittrail.dto.AcquirerCountAndAmountDTO;
import com.pay10.crm.audittrail.dto.FinalAcquirerWiseDTO;
import com.pay10.crm.mongoReports.TransactionMonitoringSummaryReports;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.Map.Entry;
import java.util.stream.Collectors;

public class TransactionMonitoringAction extends AbstractSecureAction {
	private static final long serialVersionUID = -3306411281051464691L;
	private static Logger logger = LoggerFactory.getLogger(TransactionMonitoringAction.class.getName());
	@Autowired
	private TransactionMonitoringSummaryReports transactionMonitoringSummaryReports;

	private Map<String, FinalAcquirerWiseDTO> acquirerWiseSummary = new HashMap<String, FinalAcquirerWiseDTO>();
	private Map<String, AcquirerCountAndAmountDTO> acquirerCountAndAmount = new HashMap<String, AcquirerCountAndAmountDTO>();
	private Map<String, FinalAcquirerWiseDTO> merchantWiseSummary = new HashMap<String, FinalAcquirerWiseDTO>();
	private DateFormat df=new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
	private InputStream fileInputStream;
	private String filename;
	private List<FinalAcquirerWiseDTO> aaData=new ArrayList<>();
	private List<AcquirerCountAndAmountDTO> aaDataCount=new ArrayList<>();
	@Override
	public String execute() {
		return SUCCESS;
	}

	public String acquirerWiseSummary() {
		logger.info("acquirerWiseSummary.......");
		String acquirerType = request.getParameter("acquirerType");
		String fromDate = request.getParameter("fromDate");
		String toDate = request.getParameter("toDate");
		
		logger.info(fromDate + "\t" + toDate);

		List<String> acquirerList = new ArrayList<String>();

		if (acquirerType.equalsIgnoreCase("ALL")) {
			List<AcquirerTypeUI> acqUIs = Arrays.asList(AcquirerTypeUI.values());
			for (AcquirerTypeUI acq : acqUIs) {
				acquirerList.add(acq.getCode());
			}
		} else {
			acquirerList = Arrays.asList(acquirerType.split(","));
		}
		logger.info("Acquirer : " + new Gson().toJson(acquirerList));
		Map<String, FinalAcquirerWiseDTO> acquirerWiseSummary = transactionMonitoringSummaryReports
				.getAcquirerWiseSummary(fromDate, toDate, acquirerList);
		
		Map<String, FinalAcquirerWiseDTO> sortedAcquirerWise=acquirerWiseSummary.entrySet().stream().sorted((e1,e2)->{
			
			return Long.compare(
					e2.getValue().getCaptured(),
					e1.getValue().getCaptured());
			}).collect(Collectors.toMap(Entry::getKey, Entry::getValue,
	                (e1, e2) -> e1, LinkedHashMap::new));
		
		System.out.println("Sorted By Captured : " + new Gson().toJson(sortedAcquirerWise));
		
		setAcquirerWiseSummary(sortedAcquirerWise);
		
		sortedAcquirerWise.entrySet().stream().forEach(entry->{
			aaData.add(entry.getValue());	
		});
		System.out.println(aaData.size());
		return SUCCESS;
	}

	public String countAndAmount() {
		logger.info("countAndAmount.......");
		String fromDate = request.getParameter("fromDate");
		String toDate = request.getParameter("toDate");
		logger.info("Date... " + fromDate + "\t" + toDate);
		Map<String, AcquirerCountAndAmountDTO> acquirerCountAndAmount = transactionMonitoringSummaryReports
				.getCountAndAmount(fromDate, toDate);
		
		Map<String, AcquirerCountAndAmountDTO> sortedAcquirerCountAndAmount=acquirerCountAndAmount.entrySet().stream().sorted((e1,e2)->{
			return Integer.compare(
					Integer.parseInt(e1.getValue().getTotalCount()+""),
					Integer.parseInt(e2.getValue().getTotalCount()+""));
			}).collect(Collectors.toMap(Entry::getKey, Entry::getValue,
	                (e1, e2) -> e1, LinkedHashMap::new));
		
		System.out.println(new Gson().toJson(sortedAcquirerCountAndAmount));
		
		setAcquirerCountAndAmount(sortedAcquirerCountAndAmount);
		sortedAcquirerCountAndAmount.entrySet().stream().forEach(entry->{
			aaDataCount.add(entry.getValue());	
		});
		return SUCCESS;
	}

	public String merchantWiseAcquirerReport() {
		logger.info("merchantWiseAcquirerReport.......");
		String acquirer = request.getParameter("acquirer");
		String fromDate = request.getParameter("fromDate");
		String toDate = request.getParameter("toDate");
		Map<String, FinalAcquirerWiseDTO> merchantWiseSummary = transactionMonitoringSummaryReports
				.merchantWiseAcquirerReport(fromDate, toDate, acquirer);
		
		Map<String, FinalAcquirerWiseDTO> sortedMerchantWise=merchantWiseSummary.entrySet().stream().sorted((e1,e2)->{
			return Long.compare(
					e1.getValue().getCaptured(),
					e2.getValue().getCaptured());
			}).collect(Collectors.toMap(Entry::getKey, Entry::getValue,
	                (e1, e2) -> e1, LinkedHashMap::new));
		
		System.out.println("Sorted By Captured : " + new Gson().toJson(sortedMerchantWise));
		
		setMerchantWiseSummary(sortedMerchantWise);
		
		sortedMerchantWise.entrySet().stream().forEach(entry->{
			aaData.add(entry.getValue());	
		});
		System.out.println(aaData.size());
		return SUCCESS;
	}
	
	//Download code start here
	public String acquirerWiseSummaryDownload() {
		logger.info("acquirerWiseSummaryDownload.......");
		String acquirerType = request.getParameter("acquirerType");
		String fromDate = request.getParameter("fromDate");
		String toDate = request.getParameter("toDate");
		
		logger.info(fromDate + "\t" + toDate);
		List<String> acquirerList = new ArrayList<String>();

		if (acquirerType.equalsIgnoreCase("ALL")) {
			List<AcquirerTypeUI> acqUIs = Arrays.asList(AcquirerTypeUI.values());
			for (AcquirerTypeUI acq : acqUIs) {
				acquirerList.add(acq.getCode());
			}
		} else {
			acquirerList = Arrays.asList(acquirerType.split(","));
		}
		logger.info("Acquirer : " + new Gson().toJson(acquirerList));
		Map<String, FinalAcquirerWiseDTO> acquirerWiseSummary = transactionMonitoringSummaryReports
				.getAcquirerWiseSummary(fromDate, toDate, acquirerList);
		
		Map<String, FinalAcquirerWiseDTO> sortedAcquirerWise=acquirerWiseSummary.entrySet().stream().sorted((e1,e2)->{
			
			return Long.compare(
					e2.getValue().getCaptured(),
					e1.getValue().getCaptured());
			}).collect(Collectors.toMap(Entry::getKey, Entry::getValue,
	                (e1, e2) -> e1, LinkedHashMap::new));
		
		
		
		logger.info("List generated successfully for Acquirer Wise Report , size = "+sortedAcquirerWise.size());
		SXSSFWorkbook wb = new SXSSFWorkbook(100);
		Row row;
		int rownum = 1;
		// Create a blank sheet
		Sheet sheet = wb.createSheet("Acquirer Wise Report");
		row = sheet.createRow(0);

		row.createCell(0).setCellValue("Acquirer Name");
		row.createCell(1).setCellValue("Captured");
		row.createCell(2).setCellValue("Captured %");
		row.createCell(3).setCellValue("Failed");
		row.createCell(4).setCellValue("Failed %");
		row.createCell(5).setCellValue("Request Accepted");
		row.createCell(6).setCellValue("Request Accepted %");
		row.createCell(7).setCellValue("Grand Total");
		

		if (sortedAcquirerWise.size() < 800000) {
			for (Entry<String, FinalAcquirerWiseDTO> transactionSearch : sortedAcquirerWise.entrySet()) {
				row = sheet.createRow(rownum++);
				//transactionSearch.setSrNo(String.valueOf(rownum-1));
				Object[] objArr = transactionSearch.getValue().myCsvMethod();

				int cellnum = 0;
				for (Object obj : objArr) {
					// this line creates a cell in the next column of that row
					Cell cell = row.createCell(cellnum++);
					if (obj instanceof String)
						cell.setCellValue((String) obj);
					else if (obj instanceof Integer)
						cell.setCellValue((Integer) obj);
					else if (obj instanceof Double)
						cell.setCellValue((Double) obj);
					else if (obj instanceof Long)
						cell.setCellValue((Long) obj);
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
			filename = "Acquirer_Wise_Report" + df.format(new Date()) + FILE_EXTENSION;
			File file = new File(PropertiesManager.propertiesMap.get(Constants.DOWNLOAD_PATH.getValue()) +File.separator+filename);
            
            logger.info("moni........"+PropertiesManager.propertiesMap.get(Constants.DOWNLOAD_PATH.getValue()));
			FileOutputStream out = new FileOutputStream(file);
			wb.write(out);
			out.flush();
			out.close();
			wb.dispose();
			fileInputStream = new FileInputStream(file);
			logger.info("File generated successfully for Acquirer Wise Report");
		} catch (Exception exception) {
			logger.error("Exception", exception);
		}
		
		return SUCCESS;
	}
	
	public String merchantWiseSummaryDownload() {
		logger.info("merchantWiseSummaryDownload.......");
		String acquirer = request.getParameter("acquirer");
		String fromDate = request.getParameter("fromDate");
		String toDate = request.getParameter("toDate");
		Map<String, FinalAcquirerWiseDTO> merchantWiseSummary = transactionMonitoringSummaryReports
				.merchantWiseAcquirerReport(fromDate, toDate, acquirer);
		
		Map<String, FinalAcquirerWiseDTO> sortedMerchantWise=merchantWiseSummary.entrySet().stream().sorted((e1,e2)->{
			return Long.compare(
					e1.getValue().getCaptured(),
					e2.getValue().getCaptured());
			}).collect(Collectors.toMap(Entry::getKey, Entry::getValue,
	                (e1, e2) -> e1, LinkedHashMap::new));
		
		System.out.println("Sorted By Captured : " + new Gson().toJson(sortedMerchantWise));
		
		logger.info("List generated successfully for Merchant Wise Report , size = "+sortedMerchantWise.size());
		SXSSFWorkbook wb = new SXSSFWorkbook(100);
		Row row;
		int rownum = 1;
		Sheet sheet = wb.createSheet("Merchant Wise Report");
		row = sheet.createRow(0);

		row.createCell(0).setCellValue("Merchant Name");
		row.createCell(1).setCellValue("Captured");
		row.createCell(2).setCellValue("Captured %");
		row.createCell(3).setCellValue("Failed");
		row.createCell(4).setCellValue("Failed %");
		row.createCell(5).setCellValue("Request Accepted");
		row.createCell(6).setCellValue("Request Accepted %");
		row.createCell(7).setCellValue("Grand Total");
		
		if (sortedMerchantWise.size() < 800000) {
			for (Entry<String, FinalAcquirerWiseDTO> transactionSearch : sortedMerchantWise.entrySet()) {
				row = sheet.createRow(rownum++);
				Object[] objArr = transactionSearch.getValue().myCsvMethodMerchant();

				int cellnum = 0;
				for (Object obj : objArr) {
					Cell cell = row.createCell(cellnum++);
					if (obj instanceof String)
						cell.setCellValue((String) obj);
					else if (obj instanceof Integer)
						cell.setCellValue((Integer) obj);
					else if (obj instanceof Double)
						cell.setCellValue((Double) obj);
					else if (obj instanceof Long)
						cell.setCellValue((Long) obj);
					
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
			filename = "Merchant_Wise_Report" + df.format(new Date()) + FILE_EXTENSION;
			File file = new File(PropertiesManager.propertiesMap.get(Constants.DOWNLOAD_PATH.getValue()) +File.separator+filename);
            
            logger.info("moni........"+PropertiesManager.propertiesMap.get(Constants.DOWNLOAD_PATH.getValue()));
			FileOutputStream out = new FileOutputStream(file);
			wb.write(out);
			out.flush();
			out.close();
			wb.dispose();
			fileInputStream = new FileInputStream(file);
			logger.info("File generated successfully for Merchant Wise Report");
		} catch (Exception exception) {
			logger.error("Exception", exception);
		}
		
		return SUCCESS;
		
	}
	
	public String countAndAmountDownload() {
		logger.info("countAndAmountDownload.......");
		String fromDate = request.getParameter("fromDate");
		String toDate = request.getParameter("toDate");
		logger.info("Date... " + fromDate + "\t" + toDate);
		Map<String, AcquirerCountAndAmountDTO> acquirerCountAndAmount = transactionMonitoringSummaryReports
				.getCountAndAmount(fromDate, toDate);
		
		Map<String, AcquirerCountAndAmountDTO> sortedAcquirerCountAndAmount=acquirerCountAndAmount.entrySet().stream().sorted((e1,e2)->{
			return Integer.compare(
					Integer.parseInt(e1.getValue().getTotalCount()+""),
					Integer.parseInt(e2.getValue().getTotalCount()+""));
			}).collect(Collectors.toMap(Entry::getKey, Entry::getValue,
	                (e1, e2) -> e1, LinkedHashMap::new));
		
		System.out.println(new Gson().toJson(sortedAcquirerCountAndAmount));
		
		logger.info("List generated successfully for countAndAmountDownload Report , size = "+sortedAcquirerCountAndAmount.size());
		SXSSFWorkbook wb = new SXSSFWorkbook(100);
		Row row;
		int rownum = 1;
		Sheet sheet = wb.createSheet("Count Wise Report");
		row = sheet.createRow(0);

		row.createCell(0).setCellValue("Acquirer Name");
		row.createCell(1).setCellValue("Total Amount");
		row.createCell(2).setCellValue("Total Count");
		
		if (sortedAcquirerCountAndAmount.size() < 800000) {
			for (Entry<String, AcquirerCountAndAmountDTO> transactionSearch : sortedAcquirerCountAndAmount.entrySet()) {
				row = sheet.createRow(rownum++);
				Object[] objArr = transactionSearch.getValue().myCsvMethod();

				int cellnum = 0;
				for (Object obj : objArr) {
					Cell cell = row.createCell(cellnum++);
					if (obj instanceof String)
						cell.setCellValue((String) obj);
					else if (obj instanceof Integer)
						cell.setCellValue((Integer) obj);
					else if (obj instanceof Double)
						cell.setCellValue((Double) obj);
					else if (obj instanceof Long)
						cell.setCellValue((Long) obj);
					else if (obj instanceof BigInteger)
						cell.setCellValue(((BigInteger) obj).longValue());
					else if (obj instanceof BigDecimal)
						cell.setCellValue(((BigDecimal) obj).doubleValue());
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
			filename = "Count_Amount_Wise_Report" + df.format(new Date()) + FILE_EXTENSION;
			File file = new File(PropertiesManager.propertiesMap.get(Constants.DOWNLOAD_PATH.getValue()) +File.separator+filename);
            
            logger.info("moni........"+PropertiesManager.propertiesMap.get(Constants.DOWNLOAD_PATH.getValue()));
			FileOutputStream out = new FileOutputStream(file);
			wb.write(out);
			out.flush();
			out.close();
			wb.dispose();
			fileInputStream = new FileInputStream(file);
			logger.info("File generated successfully for CountAndAmount Wise Report");
		} catch (Exception exception) {
			logger.error("Exception", exception);
		}
		
		return SUCCESS;
	}
	
	//Download code end here
	
	public Map<String, FinalAcquirerWiseDTO> getAcquirerWiseSummary() {
		return acquirerWiseSummary;
	}

	public void setAcquirerWiseSummary(Map<String, FinalAcquirerWiseDTO> acquirerWiseSummary) {
		this.acquirerWiseSummary = acquirerWiseSummary;
	}

	public Map<String, AcquirerCountAndAmountDTO> getAcquirerCountAndAmount() {
		return acquirerCountAndAmount;
	}

	public void setAcquirerCountAndAmount(Map<String, AcquirerCountAndAmountDTO> acquirerCountAndAmount) {
		this.acquirerCountAndAmount = acquirerCountAndAmount;
	}

	public Map<String, FinalAcquirerWiseDTO> getMerchantWiseSummary() {
		return merchantWiseSummary;
	}

	public void setMerchantWiseSummary(Map<String, FinalAcquirerWiseDTO> merchantWiseSummary) {
		this.merchantWiseSummary = merchantWiseSummary;
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

	public List<FinalAcquirerWiseDTO> getAaData() {
		return aaData;
	}

	public void setAaData(List<FinalAcquirerWiseDTO> aaData) {
		this.aaData = aaData;
	}

	public List<AcquirerCountAndAmountDTO> getAaDataCount() {
		return aaDataCount;
	}

	public void setAaDataCount(List<AcquirerCountAndAmountDTO> aaDataCount) {
		this.aaDataCount = aaDataCount;
	}
	
}
