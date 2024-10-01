package com.pay10.crm.action;

import java.math.BigInteger;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.pay10.commons.exception.ErrorType;
import com.pay10.commons.user.TransactionSearch;
import com.pay10.commons.user.TransactionSearchNew;
import com.pay10.commons.user.User;
import com.pay10.commons.user.UserDao;
import com.pay10.commons.user.UserType;
import com.pay10.commons.util.Constants;
import com.pay10.commons.util.CrmFieldConstants;
import com.pay10.commons.util.CrmFieldType;
import com.pay10.commons.util.CrmValidator;
import com.pay10.commons.util.DataEncoder;
import com.pay10.commons.util.DateCreater;
import com.pay10.crm.actionBeans.SessionUserIdentifier;
import com.pay10.crm.mongoReports.TxnReportForGrs;
import com.pay10.crm.mongoReports.TxnReports;

public class GRSTransactionHistoryList extends AbstractSecureAction {

	@Autowired
	private CrmValidator validator;

	@Autowired
	private DataEncoder encoder;

	@Autowired
	private TxnReportForGrs txnReports;

	@Autowired
	private UserDao userDao;
	
	@Autowired
	private SessionUserIdentifier userIdentifier;
	private static Logger logger = LoggerFactory.getLogger(GRSTransactionHistoryList.class.getName());

	private static final long serialVersionUID = -6919220389124792416L;

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

	private String ipAddress;
	private String totalAmount;
	private String dateTo;
	private int draw;
	private int length;
	private int start;
	private String transactionType;
	private String mopType;
	private BigInteger recordsTotal = BigInteger.ZERO;
	public BigInteger recordsFiltered = BigInteger.ZERO;

	private String tenantId;

	private List<TransactionSearchNew> aaData = new ArrayList<TransactionSearchNew>();
	private User sessionUser = new User();
	private String startTime;
	private String endTime;


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
private String rrn ;
    
	public String getRrn() {
		return rrn;
	}

	public void setRrn(String rrn) {
		this.rrn = rrn;
	}
	@Override
	public String execute() {
		
		if (acquirer.isEmpty()) {
			acquirer = "ALL";

		}
		if (status.isEmpty()) {
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
				endTime = endTime + ":59";
			}

			fromDateWithTime += " " + startTime;
			toDateWithTime += " " + endTime;
		}

		logger.info("Inside TransactionSearchAction , execute()");
		int totalCount;
		sessionUser = (User) sessionMap.get(Constants.USER.getValue());
		List<TransactionSearch> transactionList = new ArrayList<TransactionSearch>();

		if (startTime != null && endTime != null && !startTime.isEmpty() && !endTime.isEmpty()) {
			setDateFrom(fromDateWithTime);
			setDateTo(toDateWithTime);
		} else {
			setDateFrom(DateCreater.toDateTimeformatCreater(dateFrom));
			setDateTo(DateCreater.formDateTimeformatCreater(dateTo));
		}

		//setDateFrom(DateCreater.toDateTimeformatCreater(dateFrom));
		//setDateTo(DateCreater.formDateTimeformatCreater(dateTo));


		try {
			
			String merchantPayId = userIdentifier.getMerchantPayId(sessionUser, merchantEmailId);
			logger.info("Inside TransactionSearchAction , merchantPayId = " + merchantPayId);
			User merchant = null;
			
			if (StringUtils.isNotBlank(merchantEmailId) && !merchantEmailId.equalsIgnoreCase("All")) {
				merchant = userDao.findByEmailId(merchantEmailId);
			}
			
			
						
			if (sessionUser.getUserType().equals(UserType.MERCHANT) ) {
				BigInteger bigInt = BigInteger.valueOf(txnReports.newSearchPaymentCount(transactionId, orderId, customerEmail, customerPhone, paymentType, 
						status, currency, transactionType, mopType, "ALL", sessionUser.getPayId(), dateFrom, dateTo, sessionUser, start, length,tenantId,ipAddress,totalAmount,rrn));//,ipAddress,totalAmount));
				logger.info("Inside TransactionSearchAction , bigInt=========== = " + bigInt);

				setRecordsTotal(bigInt);
				if (getLength() == -1) {
					setLength(getRecordsTotal().intValue());
				}
				aaData = encoder.encodeTransactionSearchObjNew(txnReports.newSearchPayment(transactionId, orderId, customerEmail, customerPhone, paymentType,
						status, currency, transactionType, mopType, "ALL", sessionUser.getPayId(), dateFrom, dateTo, sessionUser, start, length,tenantId,ipAddress,totalAmount,rrn));
				logger.info("startTime, ==========TransactionSearchAction========================= = "+startTime);
				logger.info("endTime, ===========TransactionSearchAction======================== = "+endTime);
				recordsFiltered = recordsTotal;
				
				} else if (sessionUser.getUserType().equals(UserType.RESELLER)) {
					BigInteger bigInt = BigInteger.valueOf(txnReports.newSearchPaymentCount(transactionId, orderId, customerEmail, customerPhone, paymentType, 
							status, currency, transactionType, mopType, "ALL", merchant.getPayId(), dateFrom, dateTo, sessionUser, start, length,tenantId,ipAddress,totalAmount,rrn));//,ipAddress,totalAmount));

					setRecordsTotal(bigInt);
					if (getLength() == -1) {
						setLength(getRecordsTotal().intValue());
					}
					aaData = encoder.encodeTransactionSearchObjNew(txnReports.newSearchPayment(transactionId, orderId,
							customerEmail, customerPhone, paymentType, status, currency, transactionType, mopType,
							"ALL", sessionUser.getPayId(), dateFrom, dateTo, sessionUser, start, length, tenantId,
							ipAddress, totalAmount,rrn));
					logger.info("startTime, ==========TransactionSearchAction========================= = " + startTime);
					logger.info("endTime, ===========TransactionSearchAction======================== = " + endTime);
					recordsFiltered = recordsTotal;
					
					} 
					else if (sessionUser.getUserType().equals(UserType.SUBADMIN)
							&& sessionUser.getSegment().equalsIgnoreCase("Split-Payment")) {
						BigInteger bigInt = BigInteger.valueOf(txnReports.newSearchPaymentCountForSplitPayment(
								transactionId, orderId, customerEmail, customerPhone, paymentType, status, currency,
								transactionType, mopType, "ALL", merchantPayId, dateFrom, dateTo, sessionUser, start,
								length, tenantId, ipAddress, totalAmount));// ,ipAddress,totalAmount));
						setRecordsTotal(bigInt);
						if (getLength() == -1) {
							setLength(getRecordsTotal().intValue());
						}
						aaData = encoder.encodeTransactionSearchObjNew(txnReports.newSearchPaymentForSplitPayment(
								transactionId, orderId, customerEmail, customerPhone, paymentType, status, currency,
								transactionType, mopType, "ALL", merchantPayId, dateFrom, dateTo, sessionUser, start,
								length, tenantId, ipAddress, totalAmount));
						logger.info(
								"startTime, ==========TransactionSearchAction========================= = " + startTime);
						logger.info("endTime, ===========TransactionSearchAction======================== = " + endTime);
						recordsFiltered = recordsTotal;
					}
			
				else {
					BigInteger bigInt = BigInteger.valueOf(txnReports.newSearchPaymentCount(transactionId, orderId, customerEmail, customerPhone, paymentType, 
							status, currency, transactionType, mopType, "ALL", merchantPayId, dateFrom, dateTo, sessionUser, start, length,tenantId,ipAddress,totalAmount,rrn));//,ipAddress,totalAmount));
					setRecordsTotal(bigInt);
					if (getLength() == -1) {
						setLength(getRecordsTotal().intValue());
					}
				aaData = encoder.encodeTransactionSearchObjNew(txnReports.newSearchPayment(transactionId, orderId, customerEmail, customerPhone, paymentType,
						status, currency, transactionType, mopType, "ALL", merchantPayId, dateFrom, dateTo, sessionUser, start, length,tenantId,ipAddress,totalAmount,rrn));
				logger.info("startTime, ==========TransactionSearchAction========================= = "+startTime);
				logger.info("endTime, ===========TransactionSearchAction======================== = "+endTime);
				recordsFiltered = recordsTotal;
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

	
	public List<TransactionSearchNew> getAaData() {
		return aaData;
	}



	public void setAaData(List<TransactionSearchNew> aaData) {
		this.aaData = aaData;
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

	public int getDraw() {
		return draw;
	}

	public void setDraw(int draw) {
		this.draw = draw;
	}

	public int getLength() {
		return length;
	}

	public void setLength(int length) {
		this.length = length;
	}

	public int getStart() {
		return start;
	}

	public void setStart(int start) {
		this.start = start;
	}

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
}
