package com.pay10.crm.action;

import com.pay10.commons.user.Merchants;
import com.pay10.commons.user.TDRBifurcationReportDetails;
import com.pay10.commons.user.TransactionSearchNew;
import com.pay10.commons.user.User;
import com.pay10.commons.user.UserDao;
import com.pay10.commons.util.Constants;
import com.pay10.commons.util.DataEncoder;
import com.pay10.commons.util.PropertiesManager;
import com.pay10.crm.mongoReports.TransactionStatus;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.apache.struts2.ServletActionContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.math.BigInteger;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class DownloadForceCapturedReport extends AbstractSecureAction{

    private static final long serialVersionUID = -1806801325621922073L;
    private static Logger logger = LoggerFactory.getLogger(DownloadForceCapturedReport.class.getName());
    List<Merchants> merchantList = new ArrayList<>();
    List<String> statuss = new ArrayList<>();
    List<String> ruleTypee = new ArrayList<>();


    private BigInteger recordsTotal = BigInteger.ZERO;
    private BigInteger recordsFiltered = BigInteger.ZERO;

    private String merchant;
    private String dateRange;
    private String status;
    private String ruleType;
    private String acquirer;
    private String dateFrom;
    private String dateTo;
//    private String settlementDateFrom;
//    private String settlementDateTo;
    private User sessionUser = new User();
    private String filename;
    private InputStream fileInputStream;


    @Autowired
    private DataEncoder encoder;

    @Autowired
    private UserDao userDao;
    @Autowired
    private TransactionStatus transactionStatus;

    HttpServletRequest request = ServletActionContext.getRequest();

    HttpServletResponse response = ServletActionContext.getResponse();
    private List<TransactionSearchNew> forceCaptureReportDetails = new ArrayList<TransactionSearchNew>();

    public String execute() {
        SXSSFWorkbook wb=null;
        try {

             forceCaptureReportDetails =transactionStatus.getForceCapturedReport(merchant, status, acquirer, dateFrom,
                     dateTo,0,1000000);

            logger.info("List generated successfully for DownloadPaymentsReportAction , size = " + forceCaptureReportDetails.size());
			 wb = new SXSSFWorkbook(100);
			Row row;
			int rownum = 1;
			// Create a blank sheet
			Sheet sheet = wb.createSheet("Transaction_Report");
			row = sheet.createRow(0);

			row.createCell(0).setCellValue("Sr No");
			row.createCell(1).setCellValue("Txn Id");
			row.createCell(2).setCellValue("Pg Ref Num");
			row.createCell(3).setCellValue("Merchant");
			row.createCell(4).setCellValue("Date");
			row.createCell(5).setCellValue("Order Id");
			row.createCell(6).setCellValue("Refund Order Id");
			row.createCell(7).setCellValue("Mop Type");
			row.createCell(8).setCellValue("Payment Method");
			row.createCell(9).setCellValue("Txn Type");
			row.createCell(10).setCellValue("Status");
			row.createCell(11).setCellValue("Base Amount");
			row.createCell(12).setCellValue("Total Amount");
			row.createCell(13).setCellValue("Pay ID");
			row.createCell(14).setCellValue("Customer Email");
			row.createCell(15).setCellValue("Customer Ph Number");
			row.createCell(16).setCellValue("Acquirer Type");
			row.createCell(17).setCellValue("Ip Address");
			row.createCell(18).setCellValue("card Mask");
			row.createCell(19).setCellValue("RRN Number");
			row.createCell(20).setCellValue("SplitPayment");
			

			
			if (forceCaptureReportDetails.size() < 800000) {
				for (TransactionSearchNew transactionSearch : forceCaptureReportDetails) {
					row = sheet.createRow(rownum++);
					transactionSearch.setSrNo(String.valueOf(rownum-1));
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
			}
			else {
				row = sheet.createRow(rownum++);
				Cell cell = row.createCell(1);
				cell.setCellValue("Data limit exceeded for excel file generation , please select a smaller date range");
			}
			
			try {
				String FILE_EXTENSION = ".xlsx";
				DateFormat df = new SimpleDateFormat("yyyyMMddhhmmss");
				filename = "Force_Captured" + df.format(new Date()) + FILE_EXTENSION;
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
				logger.info("File generated successfully for DownloalExceptionList");
				addActionMessage(filename + " written successfully on disk.");
			} catch (Exception exception) {
				logger.error("Exception", exception);
			}

			return SUCCESS;

			}
			catch(Exception e) {
				logger.error("Exception1 "+e);
				return SUCCESS;
			}finally {
				try {
					wb.close();
				} catch (IOException e) {
					logger.error("Exception", e);
					e.printStackTrace();
				}
			}
    }


  

    public List<String> getRuleTypee() {
        return ruleTypee;
    }

    public void setRuleTypee(List<String> ruleTypee) {
        this.ruleTypee = ruleTypee;
    }

    public List<String> getStatuss() {
        return statuss;
    }

    public void setStatuss(List<String> statuss) {
        this.statuss = statuss;
    }

    public List<Merchants> getMerchantList() {
        return merchantList;
    }

    public void setMerchantList(List<Merchants> merchantList) {
        this.merchantList = merchantList;
    }

//    public String getSettlementDateFrom() {
//        return settlementDateFrom;
//    }
//
//    public void setSettlementDateFrom(String settlementDateFrom) {
//        this.settlementDateFrom = settlementDateFrom;
//    }
//
//    public String getSettlementDateTo() {
//        return settlementDateTo;
//    }
//
//    public void setSettlementDateTo(String settlementDateTo) {
//        this.settlementDateTo = settlementDateTo;
//    }

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

    public String getMerchant() {
        return merchant;
    }

    public void setMerchant(String merchant) {
        this.merchant = merchant;
    }

    public String getDateRange() {
        return dateRange;
    }

    public void setDateRange(String dateRange) {
        this.dateRange = dateRange;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getRuleType() {
        return ruleType;
    }

    public void setRuleType(String ruleType) {
        this.ruleType = ruleType;
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
