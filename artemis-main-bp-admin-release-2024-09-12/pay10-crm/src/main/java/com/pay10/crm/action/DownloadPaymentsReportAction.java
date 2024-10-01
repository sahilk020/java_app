package com.pay10.crm.action;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import com.pay10.commons.user.TransactionSearchDownloadObject;
import com.pay10.commons.user.User;
import com.pay10.commons.user.UserType;
import com.pay10.commons.util.Constants;
import com.pay10.commons.util.DateCreater;
import com.pay10.commons.util.PropertiesManager;
import com.pay10.crm.actionBeans.SessionUserIdentifier;
import com.pay10.crm.mongoReports.TxnReports;

public class DownloadPaymentsReportAction extends AbstractSecureAction {

	private static final long serialVersionUID = 2871252777725723745L;
	private static Logger logger = LoggerFactory.getLogger(DownloadPaymentsReportAction.class.getName());

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

	private String ipAddress;
	private boolean needToShowAcquirerDetails;
	private String custom_location;

	private String startTime;
	private String endTime;
	
	//Added by deep singh
	private String channelName;
	private String minAmount;
	private String maxAmount;
	private String columnName;
	private String logicalCondition;
	private String searchText;
	
	
	private String columnName1;
	private String logicalCondition1;
	private String searchText1;
	
	private String newDespositor;
	
	
	private String columnName2;
	private String logicalCondition2;
	private String searchText2;


	private String smaId;

	private String maId;

	private String agentId;
	
	
	
	
	//Added by deep singh


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

	public String getStartTime() {
		return startTime;
	}

	public void setStartTime(String startTime) {
		this.startTime = startTime;
	}

	public String getEndTime() {
		return endTime;
	}

	public void setEndTime(String endTime) {
		this.endTime = endTime;
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

	private String totalAmount;
	private String rrn;

	public String getRrn() {
		return rrn;
	}

	public void setRrn(String rrn) {
		this.rrn = rrn;
	}

	private String tenantId;

	public static final BigDecimal ONE_HUNDRED = new BigDecimal(100);

	@Autowired
	private TxnReports txnReports;

	@Override
	@SuppressWarnings("resource")
	public String execute() {

		if (acquirer.isEmpty()) {
			acquirer = "ALL";

		}

		if (tenantId == null) {
			tenantId = "ALL";
		}

		internalStatus = status;

		sessionUser = (User) sessionMap.get(Constants.USER.getValue());
		List<TransactionSearchDownloadObject> transactionList = new ArrayList<TransactionSearchDownloadObject>();

		logger.info("<<<< dateFrom ==========" + dateFrom + " ,  dateTo================ " + dateTo);
		logger.info("<<<< startTime ==========" + startTime + " ,  endTime================ " + endTime);

		String fromDateWithTime = null;
		String toDateWithTime = null;
		if (startTime != null && endTime != null && !startTime.isEmpty() && !endTime.isEmpty()) {
			DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
			DateTimeFormatter formatter2 = DateTimeFormatter.ofPattern("yyyy-MM-dd");

			fromDateWithTime = LocalDate.parse(dateFrom, formatter).format(formatter2);
			toDateWithTime = LocalDate.parse(dateTo, formatter).format(formatter2);

			String[] startTimearr = StringUtils.split(startTime, ":");
			if (startTimearr.length == 2) {
				startTime = startTime + ":01";
			}

			String[] endTimearr = StringUtils.split(endTime, ":");
			if (endTimearr.length == 2) {
				endTime = endTime + ":59";
			}

			fromDateWithTime += " " + startTime;
			toDateWithTime += " " + endTime;
		}

		logger.info("Inside TransactionSearchAction , execute()");
		sessionUser = (User) sessionMap.get(Constants.USER.getValue());

		if (startTime != null && endTime != null && !startTime.isEmpty() && !endTime.isEmpty()) {
			setDateFrom(fromDateWithTime);
			setDateTo(toDateWithTime);
		} else {
			setDateFrom(DateCreater.toDateTimeformatCreater(dateFrom));
			setDateTo(DateCreater.formDateTimeformatCreater(dateTo));
		}


		String merchaPayId = userIdentifier.getMerchantPayId(sessionUser, merchantPayId);

		
		if (sessionUser.getUserType().equals(UserType.SUBADMIN)
				&& sessionUser.getSegment().equalsIgnoreCase("Split-Payment")) {
			logger.info("Not Condition");
			transactionList = txnReports.searchTransactionForDownloadNewSplitPayment(merchaPayId, paymentType,
					internalStatus, currency, transactionType, dateFrom, dateTo, sessionUser, paymentsRegion, acquirer,
					customerEmail, customerPhone, mopType, transactionId, orderId, tenantId, ipAddress, totalAmount,
					rrn);

		} else {
			transactionList = txnReports.searchTransactionForDownloadNew(merchaPayId, paymentType, internalStatus,
					currency, transactionType, dateFrom, dateTo, sessionUser, paymentsRegion, acquirer, customerEmail,
					customerPhone, mopType, transactionId, orderId, tenantId, ipAddress, totalAmount, rrn,channelName,minAmount,maxAmount,columnName,logicalCondition,searchText,columnName1,logicalCondition1,searchText1,newDespositor,columnName2,logicalCondition2,searchText2, smaId, maId, agentId);
		}

		//logger.info("List generated successfully for DownloadPaymentsReportAction , size = " + transactionList.size());

		SXSSFWorkbook wb = new SXSSFWorkbook(100);
		Row row;
		int rownum = 1;
		// Create a blank sheet
		Sheet sheet = wb.createSheet("Transactions Report");
		row = sheet.createRow(0);
		boolean splitPaymentSegment = sessionUser.getUserType().equals(UserType.SUBADMIN)
				&& sessionUser.getSegment().equalsIgnoreCase("Split-Payment");
		List<String> headers = getHeaders(splitPaymentSegment);
		for (int i = 0; i < headers.size(); i++) {
			row.createCell(i).setCellValue(headers.get(i));
		}
		logger.info("Search Payment Header List{}",headers);
		if (transactionList.size() < 800000) {

			for (TransactionSearchDownloadObject transactionSearch : transactionList) {
				row = sheet.createRow(rownum++);
				transactionSearch.setSrNo(String.valueOf(rownum - 1));
				List<Object> objArr = transactionSearch.myCsvMethodDownloadPaymentsReport(splitPaymentSegment,
						isNeedToShowAcquirerDetails());

				logger.info("Search Payment Object Array{}",objArr);
				for (int cellnum = 0; cellnum < objArr.size(); cellnum++) {
					Cell cell = row.createCell(cellnum);
					if (objArr.get(cellnum) instanceof String)
						cell.setCellValue((String) objArr.get(cellnum));
					else if (objArr.get(cellnum) instanceof Integer)
						cell.setCellValue((Integer) objArr.get(cellnum));
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

	private List<String> getHeaders(boolean segment) {
		List<String> headers = new ArrayList<>();
		headers.add("Sr No");
		headers.add("Txn Id");
		headers.add("Pg Ref Num");
		headers.add("Merchant");
		if (isNeedToShowAcquirerDetails()) {
			headers.add("Acquirer");
		}
		headers.add("Date");
		headers.add("Order Id");
		if (isNeedToShowAcquirerDetails()) {
			headers.add("Payment Method");
		}
		headers.add("Txn Type");
		headers.add("Status");
		headers.add("Transaction Region");
		headers.add("Base Amount");
		headers.add("Total Amount");
		if (!segment) {
			//headers.add("Delta Refund Flag");
			headers.add("ACQ ID");
		}
		headers.add("RRN");
		/*if (!segment) {
			headers.add("Post Settled Flag");
		}*/
		//headers.add("Refund Order ID");
		/*if (!segment) {
			headers.add("Refund Flag");
		}*/
		if (isNeedToShowAcquirerDetails()) {
			headers.add("Mop Type");
		}
		if (!segment) {
			headers.add("IP Address");
		}
		headers.add("Card Mask");
		headers.add("Customer Email");
		headers.add("Customer Phone");
		
		//headers.add("UDF4");
		//headers.add("UDF5");
		//headers.add("UDF6");
		if (!segment) {
			headers.add("UTR");
		}
		headers.add("Card Holder Type");
		headers.add("Bank Ref Number");
		headers.add("Psp Name");
		headers.add("currency");
		if (!segment) {
			headers.add("Acquirer Response");
		}


		return headers;
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

	public String getTenantId() {
		return tenantId;
	}

	public void setTenantId(String tenantId) {
		this.tenantId = tenantId;
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

	public boolean isNeedToShowAcquirerDetails() {
		return needToShowAcquirerDetails;
	}

	public void setNeedToShowAcquirerDetails(boolean needToShowAcquirerDetails) {
		this.needToShowAcquirerDetails = needToShowAcquirerDetails;
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
