package com.pay10.crm.action;


import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.pay10.commons.exception.ErrorType;
import com.pay10.commons.user.TransactionSearch;
import com.pay10.commons.user.User;
import com.pay10.commons.user.UserType;
import com.pay10.commons.util.Constants;
import com.pay10.commons.util.CrmFieldConstants;
import com.pay10.commons.util.CrmFieldType;
import com.pay10.commons.util.CrmValidator;
import com.pay10.commons.util.DateCreater;
import com.pay10.commons.util.StatusType;
import com.pay10.crm.actionBeans.SessionUserIdentifier;
import com.pay10.crm.mongoReports.TxnReports;

/**
 * @author Rajendra
 *
 */

public class TotalAmountOfAllTxnsAction  extends AbstractSecureAction {
	
	@Autowired
	private CrmValidator validator;

	@Autowired
	private TxnReports txnReports;

	@Autowired
	private SessionUserIdentifier userIdentifier;
	
	private static final long serialVersionUID = 2845995077638261715L;
	private User sessionUser = new User();
	private static Logger logger = LoggerFactory.getLogger(TotalAmountOfAllTxnsAction.class.getName());
	
	private String transactionId;
	private String orderId;
	private String customerEmail;
	private String customerPhone;
	private String merchantEmailId;
	private String paymentType;
	private String cardNumber;
	private String status;
	private String acquirer;
	private String currency;
	private String dateFrom;
	private String dateTo;
	private String tenantId;
	private String ipAddress;
	private String totalAmount;
	private String transactionType;
	private String mopType;

	private int totalTxnAmount;

	private HashMap<String,String> totalFinalResult=new HashMap<String,String>();
	private HashMap<String,String> InitiatedResult=new HashMap<String,String>();
	private HashMap<String,String> settledResult=new HashMap<String,String>();
	private String startTime;
	private String endTime;
	
	
	// Added by deep singh
	private String channelName;
	private String minAmount;
	private String maxAmount;
	private String columnName;
	private String logicalCondition;
	private String searchText;
	// Added by deep singh



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

	@Override
	public String execute() {
		boolean settledFlag,settledOnlyFlag;
		if (null == acquirer || acquirer.isEmpty()) {
			acquirer = "ALL";
		}
		if (null == status || status.isEmpty()) {
			status = "ALL";
		}
		
		if(null == tenantId) {
			tenantId = "ALL";
		}


		logger.info("<<<< dateFrom ==========" + dateFrom + " ,  dateTo================ " + dateTo);
		logger.info("<<<< startTime ==========" + startTime + " ,  endTime================ " + endTime);

		String fromDateWithTime = null;
		String toDateWithTime = null;
		if (startTime != null && endTime != null && !startTime.isEmpty() && !endTime.isEmpty()) {
			DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
			DateTimeFormatter formatter2 = DateTimeFormatter.ofPattern("yyyy-MM-dd");

			fromDateWithTime = LocalDate.parse(dateFrom, formatter).format(formatter2);
			toDateWithTime = LocalDate.parse(dateTo, formatter).format(formatter2);

			String[] startTimearr = org.apache.commons.lang.StringUtils.split(startTime, ":");
			if (startTimearr.length == 2) {
				startTime = startTime + ":01";
			}

			String[] endTimearr = org.apache.commons.lang.StringUtils.split(endTime, ":");
			if (endTimearr.length == 2) {
				if(endTime.equalsIgnoreCase("00:00")) {
					endTime = "23:59:59";
				}else {
					endTime = endTime + ":59";
				}
			}

			fromDateWithTime += " " + startTime;
			toDateWithTime += " " + endTime;
		}

		logger.info("Inside TotalAmountOfAllTxnsAction , execute()");
		sessionUser = (User) sessionMap.get(Constants.USER.getValue());


		if (startTime != null && endTime != null && !startTime.isEmpty() && !endTime.isEmpty()) {
			setDateFrom(fromDateWithTime);
			setDateTo(toDateWithTime);
		} else {
			setDateFrom(DateCreater.toDateTimeformatCreater(dateFrom));
			setDateTo(DateCreater.formDateTimeformatCreater(dateTo));
		}

		//setDateFrom(DateCreater.toDateTimeformatCreater(dateFrom));
		//setDateTo(DateCreater.formDateTimeformatCreater(dateTo));
		
		String internalStatus = status;
		
		if(internalStatus.contains("ALL") || internalStatus.contains("Settled")) {
			settledFlag = true;						
		}else {
			settledFlag = false;
		}
		List<String> aliasStatusList = Arrays.asList(internalStatus.split(","));
		if(aliasStatusList.size() == 1 && aliasStatusList.contains("Settled")) {
			settledOnlyFlag = true;
		}else {
			settledOnlyFlag = false;
		}
		
		try {
			String merchantPayId = userIdentifier.getMerchantPayId(sessionUser, merchantEmailId);
			logger.info("Inside TransactionSearchAction , merchantPayId = " + merchantPayId);
				
				if (sessionUser.getUserType().equals(UserType.SUBUSER)) {
					
					InitiatedResult = txnReports.totalAmountOfAllTxnsNew(getTransactionId(), getOrderId(), getCustomerEmail(),
							getCustomerPhone(), getPaymentType(), internalStatus, getCurrency(),
							getTransactionType(), getMopType(), getAcquirer(),sessionUser.getParentPayId(), getDateFrom(), getDateTo(), sessionUser, "initiated",settledOnlyFlag,tenantId,getIpAddress(),getTotalAmount(),channelName,minAmount,maxAmount,columnName,logicalCondition,searchText);
				
				  settledResult = txnReports.totalAmountOfAllTxnsNew(getTransactionId(),
				  getOrderId(), getCustomerEmail(), getCustomerPhone(), getPaymentType(),
				  "Settled", getCurrency(), getTransactionType(), getMopType(),
				  getAcquirer(),sessionUser.getParentPayId(), getDateFrom(), getDateTo(), sessionUser, "settled",settledFlag,tenantId,getIpAddress(),getTotalAmount(),channelName,minAmount,maxAmount,columnName,logicalCondition,searchText);
				 

					totalFinalResult.putAll(InitiatedResult);
					totalFinalResult.putAll(settledResult);
					
				} else if (sessionUser.getUserType().equals(UserType.MERCHANT)) {
					
					InitiatedResult = txnReports.totalAmountOfAllTxnsNew(getTransactionId(), getOrderId(), getCustomerEmail(),
							getCustomerPhone(), getPaymentType(), internalStatus, getCurrency(),
							getTransactionType(), getMopType(), getAcquirer(),sessionUser.getPayId(), getDateFrom(), getDateTo(), sessionUser, "initiated",settledOnlyFlag,tenantId,getIpAddress(),getTotalAmount(),channelName,minAmount,maxAmount,columnName,logicalCondition,searchText);
				
				  settledResult = txnReports.totalAmountOfAllTxnsNew(getTransactionId(),
				  getOrderId(), getCustomerEmail(), getCustomerPhone(), getPaymentType(),
				  "Settled", getCurrency(), getTransactionType(), getMopType(),
				  getAcquirer(),sessionUser.getPayId(), getDateFrom(), getDateTo(), sessionUser, "settled",settledFlag,tenantId,getIpAddress(),getTotalAmount(),channelName,minAmount,maxAmount,columnName,logicalCondition,searchText);
				 

					totalFinalResult.putAll(InitiatedResult);
					totalFinalResult.putAll(settledResult);

				} else if (sessionUser.getUserType().equals(UserType.RESELLER)){
					
					InitiatedResult = txnReports.totalAmountOfAllTxnsNew(getTransactionId(), getOrderId(), getCustomerEmail(),
							getCustomerPhone(), getPaymentType(), internalStatus, getCurrency(),
							getTransactionType(), getMopType(), getAcquirer(),sessionUser.getPayId(), getDateFrom(), getDateTo(), sessionUser, "initiated",settledOnlyFlag,tenantId,getIpAddress(),getTotalAmount(),channelName,minAmount,maxAmount,columnName,logicalCondition,searchText);
				
				  settledResult = txnReports.totalAmountOfAllTxnsNew(getTransactionId(),
				  getOrderId(), getCustomerEmail(), getCustomerPhone(), getPaymentType(),
				  "Settled", getCurrency(), getTransactionType(), getMopType(),
				  getAcquirer(),sessionUser.getPayId(), getDateFrom(), getDateTo(), sessionUser, "settled",settledFlag,tenantId,getIpAddress(),getTotalAmount(),channelName,minAmount,maxAmount,columnName,logicalCondition,searchText);
				 

					totalFinalResult.putAll(InitiatedResult);
					totalFinalResult.putAll(settledResult);
				}
			

		} catch (Exception exception) {
			logger.error("Exception", exception);
			return ERROR;
		}
		return SUCCESS;

	}



	@Override
	public void validate() {

		if (validator.validateBlankField(getTransactionId())) {
		} else if (!validator.validateField(CrmFieldType.TRANSACTION_ID, getTransactionId())) {
			addFieldError(CrmFieldType.TRANSACTION_ID.getName(), ErrorType.INVALID_FIELD.getResponseMessage());
		}

		if (validator.validateBlankField(getOrderId())) {
		} else if (!validator.validateField(CrmFieldType.ORDER_ID, getOrderId())) {
			addFieldError(CrmFieldType.ORDER_ID.getName(), ErrorType.INVALID_FIELD.getResponseMessage());
		}

		if (validator.validateBlankField(getCustomerEmail())) {
		} else if (!validator.validateField(CrmFieldType.CUSTOMER_EMAIL_ID, getCustomerEmail())) {
			addFieldError(CrmFieldType.CUSTOMER_EMAIL_ID.getName(), ErrorType.INVALID_FIELD.getResponseMessage());
		}

		if (validator.validateBlankField(getCustomerPhone())) {
		} else if (!validator.validateField(CrmFieldType.MOBILE, getCustomerPhone())) {
			addFieldError(CrmFieldType.MOBILE.getName(), ErrorType.INVALID_FIELD.getResponseMessage());
		}

		if (validator.validateBlankField(getMopType()) || getMopType().equals(CrmFieldConstants.ALL.getValue())) {
		} else if (!validator.validateField(CrmFieldType.MOP_TYPE, getMopType())) {
			addFieldError(CrmFieldType.MOP_TYPE.getName(), ErrorType.INVALID_FIELD.getResponseMessage());
		}
		
		if (validator.validateBlankField(getAcquirer()) || getAcquirer().equals(CrmFieldConstants.ALL.getValue())) {
		} else if (!validator.validateField(CrmFieldType.ACQUIRER, getAcquirer())) {
			addFieldError(CrmFieldType.ACQUIRER.getName(), ErrorType.INVALID_FIELD.getResponseMessage());
		}

		if (validator.validateBlankField(getMerchantEmailId())
				|| getMerchantEmailId().equals(CrmFieldConstants.ALL.getValue())) {
		} else if (!validator.validateField(CrmFieldType.MERCHANT_EMAIL_ID, getMerchantEmailId())) {
			addFieldError(CrmFieldType.MERCHANT_EMAIL_ID.getName(), ErrorType.INVALID_FIELD.getResponseMessage());
		}

		if (validator.validateBlankField(getPaymentType())) {
		} else if (!validator.validateField(CrmFieldType.PAYMENT_TYPE, getPaymentType())) {
			addFieldError(CrmFieldType.PAYMENT_TYPE.getName(), ErrorType.INVALID_FIELD.getResponseMessage());
		}

		if (validator.validateBlankField(getCardNumber())) {
		} else if (!validator.validateField(CrmFieldType.CARD_NUMBER_MASK, getCardNumber())) {
			addFieldError(CrmFieldType.CARD_NUMBER_MASK.getName(), ErrorType.INVALID_FIELD.getResponseMessage());
		}

		if (validator.validateBlankField(getStatus())) {
		} else if (!validator.validateField(CrmFieldType.ALIAS_STATUS, getStatus())) {
			addFieldError(CrmFieldType.ALIAS_STATUS.getName(), ErrorType.INVALID_FIELD.getResponseMessage());
		}

		if (validator.validateBlankField(getCurrency())) {
		} else if (!validator.validateField(CrmFieldType.CURRENCY, getCurrency())) {
			addFieldError(CrmFieldType.CURRENCY.getName(), ErrorType.INVALID_FIELD.getResponseMessage());
		}

		if (validator.validateBlankField(getDateFrom())) {
		} else if (!validator.validateField(CrmFieldType.DATE_FROM, getDateFrom())) {
			addFieldError(CrmFieldType.DATE_FROM.getName(), ErrorType.INVALID_FIELD.getResponseMessage());
		}

		if (validator.validateBlankField(getDateTo())) {
		} else if (!validator.validateField(CrmFieldType.DATE_TO, getDateTo())) {
			addFieldError(CrmFieldType.DATE_TO.getName(), ErrorType.INVALID_FIELD.getResponseMessage());
		}

		if (!validator.validateBlankField(getDateTo())) {
			if (DateCreater.formatStringToDate(DateCreater.formatFromDate(getDateFrom()))
					.compareTo(DateCreater.formatStringToDate(DateCreater.formatFromDate(getDateTo()))) > 0) {
				addFieldError(CrmFieldType.DATE_FROM.getName(), CrmFieldConstants.FROMTO_DATE_VALIDATION.getValue());
			} else if (DateCreater.diffDate(getDateFrom(), getDateTo()) > 31) {
				addFieldError(CrmFieldType.DATE_FROM.getName(), CrmFieldConstants.DATE_RANGE.getValue());
			}
		}
	}


	
	public HashMap<String,String> getTotalFinalResult() {
		return totalFinalResult;
	}

	public void setTotalFinalResult(HashMap<String,String> totalFinalResult) {
		this.totalFinalResult = totalFinalResult;
	}

	
	public int getTotalTxnAmount() {
		return totalTxnAmount;
	}

	public void setTotalTxnAmount(int totalTxnAmount) {
		this.totalTxnAmount = totalTxnAmount;
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

	public String getCardNumber() {
		return cardNumber;
	}

	public void setCardNumber(String cardNumber) {
		this.cardNumber = cardNumber;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
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

	public String getMerchantEmailId() {
		return merchantEmailId;
	}

	public void setMerchantEmailId(String merchantEmailId) {
		this.merchantEmailId = merchantEmailId;
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

	public String getMopType() {
		return mopType;
	}

	public void setMopType(String mopType) {
		this.mopType = mopType;
	}
	
	
	public String getAcquirer() {
		return acquirer;
	}

	public void setAcquirer(String acquirer) {
		this.acquirer = acquirer;
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
	
}
