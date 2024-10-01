package com.pay10.crm.action;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.pay10.commons.user.SettledTransactionDataObject;
import com.pay10.commons.user.UserDao;
import com.pay10.commons.util.Constants;
import com.pay10.commons.util.PropertiesManager;
import com.pay10.crm.mongoReports.TxnReports;

import au.com.bytecode.opencsv.CSVReader;

public class TransactionValidationReportAction extends AbstractSecureAction {

	private static final long serialVersionUID = -8129011751068997117L;
	private static Logger logger = LoggerFactory.getLogger(TransactionValidationReportAction.class.getName());
	private InputStream fileInputStream;
	private String filename;
	private String fileName;
	private String response;
	
	@Autowired
	private UserDao userDao;
	@Autowired
    private PropertiesManager propertiesManager;

	@Autowired
	private TxnReports txnReports;

	@Override
	@SuppressWarnings("resource")
	public String execute() {
		
		CSVReader csvReader;
		List<SettledTransactionDataObject> txnList = new LinkedList<SettledTransactionDataObject>();
		String line = "";
		
		try {
			// Start reading from line number 2 (line numbers start from zero)
			csvReader = new CSVReader(new FileReader(fileName), '\t', '\'', 1);
			String[] nextLine;

			try {
				while ((nextLine = csvReader.readNext()) != null) {
					line = Arrays.toString(nextLine).replace("[", "").replace("]", "").replace("'", "");
					String lineSplit [] = line.split(",");
					
					List<SettledTransactionDataObject> listResponse = txnReports.txnDataForbatch(lineSplit[0],lineSplit[1],lineSplit[2]);
					
					
					if (listResponse.size() > 0) {
						
						txnList.addAll(listResponse);
						
					} else {
						logger.info("csvReaderForTxnValidation , no data found for " + line);
					}

				}
			} catch (IOException exception) {
				logger.error("Exception", exception);
			}

		} catch (FileNotFoundException exception) {
			logger.error("Exception", exception);
		}

		logger.info("List generated successfully for Batch Search Object , size = " + txnList.size());
		SXSSFWorkbook wb = new SXSSFWorkbook(100);
		Row row;
		int rownum = 1;
		// Create a blank sheet
		Sheet sheet = wb.createSheet("Txn Validation Report");

		row = sheet.createRow(0);

		row.createCell(0).setCellValue("Txn Id");
		row.createCell(1).setCellValue("Pg Ref Num");
		row.createCell(2).setCellValue("Pay Id");
		row.createCell(3).setCellValue("Payments Region");
		row.createCell(4).setCellValue("Post Settled Flag");
		row.createCell(5).setCellValue("Txn Type");
		row.createCell(6).setCellValue("Order Id");
		row.createCell(7).setCellValue("Acquirer Type");
		row.createCell(8).setCellValue("Payment Type");
		row.createCell(9).setCellValue("Mop Type");
		row.createCell(10).setCellValue("Status");
		row.createCell(11).setCellValue("Create Date");
		row.createCell(12).setCellValue("Amount");
		row.createCell(13).setCellValue("Total Amount");
		row.createCell(14).setCellValue("Acq Id");
		row.createCell(15).setCellValue("RRN");
		row.createCell(16).setCellValue("ARN");
		row.createCell(17).setCellValue("Refund Order Id");
		row.createCell(18).setCellValue("Refund Flag");
		row.createCell(19).setCellValue("Internal Request Fields");
		row.createCell(20).setCellValue("iPay Message");
		row.createCell(21).setCellValue("Acquirer Message");
		
		if (txnList.size() < 800000) {
			for (SettledTransactionDataObject transactionSearch : txnList) {
				
				row = sheet.createRow(rownum++);
				Object[] objArr = transactionSearch.batchtxnSearchDownload();
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
			filename = "Transaction_Validation_Report" + df.format(new Date()) + FILE_EXTENSION;
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
			addActionMessage(filename + " written successfully on disk.");
			
			return SUCCESS;
		}

		catch (Exception e) {
			logger.error("Exception in fetching batch search data " +e);
		}
		return SUCCESS;

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


	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public String getResponse() {
		return response;
	}

	public void setResponse(String response) {
		this.response = response;
	}

}
