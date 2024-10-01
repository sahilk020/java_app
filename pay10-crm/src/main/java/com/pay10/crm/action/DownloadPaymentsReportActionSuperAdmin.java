package com.pay10.crm.action;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.math.BigDecimal;
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

import com.pay10.commons.user.TransactionSearchDownloadObject;
import com.pay10.commons.user.User;
import com.pay10.commons.util.Constants;
import com.pay10.commons.util.DateCreater;
import com.pay10.commons.util.PropertiesManager;
import com.pay10.crm.actionBeans.SessionUserIdentifier;
import com.pay10.crm.mongoReports.TxnReports;

/**
 * @author Rajendra
 *
 */

public class DownloadPaymentsReportActionSuperAdmin  extends AbstractSecureAction {

	private static final long serialVersionUID = 2871252777725723745L;
	private static Logger logger = LoggerFactory.getLogger(DownloadPaymentsReportActionSuperAdmin.class.getName());
	
	@Autowired
	private SessionUserIdentifier userIdentifier;
	  @Autowired
	    private PropertiesManager propertiesManager;
	
	private String currency;
	private String dateFrom;
	private String dateTo;
	private String merchantPayId;
	private String paymentType;
	private String transactionType;
	private String status;
	private String acquirer;
	private InputStream fileInputStream;
	private String filename;
	private User sessionUser = new User();
	private String paymentsRegion;
	private String cardHolderType;
	private String customerEmail;
	private String customerPhone;
	private String mopType;
	private String transactionId;
	private String orderId;
	private String internalStatus;		

	private String tenantId;

	private String ipAddress;
	private String totalAmount;
	
	//Added by deep singh
	private String channelName;
	private String minAmount;
	private String maxAmount;
	private String columnName;
	private String logicalCondition;
	private String searchText;
	
private String newDespositor;
	
private String columnName1;
private String logicalCondition1;
private String searchText1;
	private String columnName2;
	private String logicalCondition2;
	private String searchText2;

	private String smaId;
	private String maId;
	private String agentId;
	
	
	
	
	//Added by deep singh
	
	public String getNewDespositor() {
		return newDespositor;
	}

	public void setNewDespositor(String newDespositor) {
		this.newDespositor = newDespositor;
	}

	public String getColumnName1() {
		return columnName1;
	}

	public void setColumnName1(String columnName1) {
		this.columnName1 = columnName1;
	}

	public String getLogicalCondition1() {
		return logicalCondition1;
	}

	public void setLogicalCondition1(String logicalCondition1) {
		this.logicalCondition1 = logicalCondition1;
	}

	public String getSearchText1() {
		return searchText1;
	}

	public void setSearchText1(String searchText1) {
		this.searchText1 = searchText1;
	}

	public String getColumnName2() {
		return columnName2;
	}

	public void setColumnName2(String columnName2) {
		this.columnName2 = columnName2;
	}

	public String getLogicalCondition2() {
		return logicalCondition2;
	}

	public void setLogicalCondition2(String logicalCondition2) {
		this.logicalCondition2 = logicalCondition2;
	}

	public String getSearchText2() {
		return searchText2;
	}

	public void setSearchText2(String searchText2) {
		this.searchText2 = searchText2;
	}

	public String getIpAddress() {
		return ipAddress;
	}

	public void setIpAddress(String ipAddress) {
		this.ipAddress = ipAddress;
	}

	public String getTotalAmount() {
		return totalAmount;
	}

	public void setTotalAmount(String totalAmount) {
		this.totalAmount = totalAmount;
	}
private String rrn ;

    
	public String getRrn() {
		return rrn;
	}

	public void setRrn(String rrn) {
		this.rrn = rrn;
	}
	public static final BigDecimal ONE_HUNDRED = new BigDecimal(100);

	@Autowired
	private TxnReports txnReports;

	@Override
	@SuppressWarnings("resource")
	public String execute() {
		
		if (acquirer.isEmpty()) {
			acquirer = "ALL";

		}
		internalStatus = status;
		
		sessionUser = (User) sessionMap.get(Constants.USER.getValue());
		List<TransactionSearchDownloadObject> transactionList = new ArrayList<TransactionSearchDownloadObject>();
		
		dateFrom = dateFrom + ":00";
		dateTo = dateTo + ":59";
		
		setDateFrom(DateCreater.toDateTimeformatCreaterWithHhMmSs(dateFrom));
		setDateTo(DateCreater.toDateTimeformatCreaterWithHhMmSs(dateTo));
		
		String merchaPayId = userIdentifier.getMerchantPayId(sessionUser, merchantPayId);
		logger.info("Before Call searchTransactionForDownloadNew Method====DownloadPaymentsReportActionSuperAdmin=============================================");

		transactionList = txnReports.searchTransactionForDownloadNew( merchaPayId, paymentType, internalStatus, currency, transactionType,
				dateFrom,  dateTo, sessionUser , paymentsRegion , acquirer, customerEmail,customerPhone,mopType,transactionId,orderId,tenantId,ipAddress,totalAmount,rrn,channelName,minAmount,maxAmount,columnName,logicalCondition,searchText,columnName1,logicalCondition1,searchText1,newDespositor,columnName2,logicalCondition2,searchText2, smaId, maId, agentId);
		
		BigDecimal st = null;

		logger.info("List generated successfully for DownloadPaymentsReportAction , size = " + transactionList.size());
		SXSSFWorkbook wb = new SXSSFWorkbook(100);
		Row row;
		int rownum = 1;
		// Create a blank sheet
		Sheet sheet =  wb.createSheet("Transactions Report");
		row = sheet.createRow(0);

		row.createCell(0).setCellValue("Sr No");
		row.createCell(1).setCellValue("Txn Id");
		row.createCell(2).setCellValue("Pg Ref Num");
		row.createCell(3).setCellValue("Merchant");
		row.createCell(4).setCellValue("Acquirer");
		row.createCell(5).setCellValue("Date");
		row.createCell(6).setCellValue("Order Id");
		row.createCell(7).setCellValue("Payment Method");
		row.createCell(8).setCellValue("Txn Type");
		row.createCell(9).setCellValue("Status");
		row.createCell(10).setCellValue("Transaction Region");
		row.createCell(11).setCellValue("Base Amount");
		row.createCell(12).setCellValue("Total Amount");
		row.createCell(13).setCellValue("Delta Refund Flag");
		row.createCell(14).setCellValue("ACQ ID");
		row.createCell(15).setCellValue("RRN");
		row.createCell(16).setCellValue("Post Settled Flag");
		row.createCell(17).setCellValue("Refund Order ID");
		row.createCell(18).setCellValue("Refund Flag");
		row.createCell(19).setCellValue("Mop Type");
		row.createCell(20).setCellValue("Post Settled Flag");
		row.createCell(21).setCellValue("Refund Order ID");
		row.createCell(22).setCellValue("Refund Flag");
		row.createCell(23).setCellValue("Mop Type");
		
		if (transactionList.size() < 800000) {
			
			for (TransactionSearchDownloadObject transactionSearch : transactionList) {
				row = sheet.createRow(rownum++);
				transactionSearch.setSrNo(String.valueOf(rownum-1));
				Object[] objArr = transactionSearch.myCsvMethodDownloadPaymentsReportSuperAdmin();

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
			// this line creates a cell in the next column of that row
			Cell cell = row.createCell(1);
			cell.setCellValue("Data limit exceeded for excel file generation , please select a smaller date range");
		}
			
		
		try {
			String FILE_EXTENSION = ".xlsx";
			DateFormat df = new SimpleDateFormat("yyyyMMddhhmmss");
			filename = "SearchPayment_Transactions_" + df.format(new Date()) + FILE_EXTENSION;
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
			logger.info("File generated successfully for DownloadPaymentsReportAction");
		} catch (Exception exception) {
			logger.error("Exception", exception);
		}

		return SUCCESS;

	}


	public InputStream getFileInputStream() {
		return fileInputStream;
	}

	public String getCurrency() {
		return currency;
	}

	public void setCurrency(String currency) {
		this.currency = currency;
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

	public String getPaymentsRegion() {
		return paymentsRegion;
	}

	public void setPaymentsRegion(String paymentsRegion) {
		this.paymentsRegion = paymentsRegion;
	}

	public String getCardHolderType() {
		return cardHolderType;
	}

	public void setCardHolderType(String cardHolderType) {
		this.cardHolderType = cardHolderType;
	}

	public String getPaymentType() {
		return paymentType;
	}

	public void setPaymentType(String paymentType) {
		this.paymentType = paymentType;
	}

	public String getTransactionType() {
		return transactionType;
	}

	public void setTransactionType(String transactionType) {
		this.transactionType = transactionType;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getMerchantPayId() {
		return merchantPayId;
	}

	public void setMerchantPayId(String merchantPayId) {
		this.merchantPayId = merchantPayId;
	}


	public String getAcquirer() {
		return acquirer;
	}


	public void setAcquirer(String acquirer) {
		this.acquirer = acquirer;
	}

	public String getTransactionId() {
		return transactionId;
	}

	public void setTransactionId(String transactionId) {
		this.transactionId = transactionId;
	}

	public String getOrderId() {
		return orderId;
	}

	public void setOrderId(String orderId) {
		this.orderId = orderId;
	}

	public String getTenantId() {
		return tenantId;
	}


	public void setTenantId(String tenantId) {
		this.tenantId = tenantId;
	}


	public String getCustomerEmail() {
		return customerEmail;
	}

	public void setCustomerEmail(String customerEmail) {
		this.customerEmail = customerEmail;
	}

	public String getCustomerPhone() {
		return customerPhone;
	}

	public void setCustomerPhone(String customerPhone) {
		this.customerPhone = customerPhone;
	}
	
	public String getMopType() {
		return mopType;
	}

	public void setMopType(String mopType) {
		this.mopType = mopType;
	}

	public String getChannelName() {
		return channelName;
	}

	public void setChannelName(String channelName) {
		this.channelName = channelName;
	}

	public String getMinAmount() {
		return minAmount;
	}

	public void setMinAmount(String minAmount) {
		this.minAmount = minAmount;
	}

	public String getMaxAmount() {
		return maxAmount;
	}

	public void setMaxAmount(String maxAmount) {
		this.maxAmount = maxAmount;
	}

	public String getColumnName() {
		return columnName;
	}

	public void setColumnName(String columnName) {
		this.columnName = columnName;
	}

	public String getLogicalCondition() {
		return logicalCondition;
	}

	public void setLogicalCondition(String logicalCondition) {
		this.logicalCondition = logicalCondition;
	}

	public String getSearchText() {
		return searchText;
	}

	public void setSearchText(String searchText) {
		this.searchText = searchText;
	}


	public String getSmaId(String smaId) {
		return this.smaId;
	}

	public String getMaId(String maId) {
		return this.maId;
	}

	public String getAgentId(String agentId) {
		return this.agentId;
	}

	public void setSmaId(String smaId) {
		this.smaId = smaId;
	}

	public void setMaId(String maId) {
		this.maId = maId;
	}

	public void setAgentId(String agentId) {
		this.agentId = agentId;
	}
}
