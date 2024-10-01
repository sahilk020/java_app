package com.pay10.crm.action;

import java.math.BigInteger;
import java.util.List;

import org.slf4j.Logger; import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.pay10.commons.exception.ErrorType;
import com.pay10.commons.user.TransactionSearch;
import com.pay10.commons.user.User;
import com.pay10.commons.user.UserType;
import com.pay10.commons.util.Constants;
import com.pay10.commons.util.CrmFieldConstants;
import com.pay10.commons.util.CrmFieldType;
import com.pay10.commons.util.CrmValidator;
import com.pay10.commons.util.DataEncoder;
import com.pay10.commons.util.DateCreater;
import com.pay10.commons.util.StatusType;
import com.pay10.commons.util.TransactionType;
import com.pay10.crm.actionBeans.SessionUserIdentifier;
import com.pay10.crm.mongoReports.TxnReports;

public class SaleTransactionSearchAction extends AbstractSecureAction {

	@Autowired
	private CrmValidator validator;

	@Autowired
	private DataEncoder encoder;

	@Autowired
	private TxnReports txnReports;

	@Autowired
	private SessionUserIdentifier userIdentifier;
	private static Logger logger = LoggerFactory.getLogger(SaleTransactionSearchAction.class.getName());

	private static final long serialVersionUID = -6919220389124792416L;

	private String transactionId;
	private String orderId;
	private String customerEmail;
	private String merchantEmailId;
	private String paymentType;
	private String cardNumber;
	private String status;
	private String currency;
	private String dateFrom;
	private String dateTo;
	private int draw;
	private int length;
	private int start;
	private String transactionType;
	private BigInteger recordsTotal;
	public BigInteger recordsFiltered;
	private String phoneNo;
	private String mopType;
	private String udf4;

	private List<TransactionSearch> aaData;
	private User sessionUser = new User();
	
private String channel;
	
	

	public String getChannel() {
		return channel;
	}

	public void setChannel(String channel) {
		this.channel = channel;
	}

	@Override
	public String execute() {
		
		setTransactionType(TransactionType.SALE.getName());
		setStatus(StatusType.CAPTURED.getName());
		logger.info("Inside TransactionSearchAction , execute()");
		int totalCount;
		sessionUser = (User) sessionMap.get(Constants.USER.getValue());
		setDateFrom(DateCreater.toDateTimeformatCreater(dateFrom));
		setDateTo(DateCreater.formDateTimeformatCreater(dateTo));
		try {
			String merchantPayId = userIdentifier.getMerchantPayId(sessionUser, merchantEmailId);
			logger.info("Inside TransactionSearchAction , merchantPayId = " + merchantPayId);
			if (sessionUser.getUserType().equals(UserType.RESELLER)) {
				totalCount = txnReports.searchPaymentCount(getTransactionId(), getOrderId(), getCustomerEmail(),
						merchantPayId, getPaymentType(), getStatus(), getCurrency(), getTransactionType(),
						getDateFrom(), getDateTo(), sessionUser,getPhoneNo(),getMopType(),getUdf4(),channel);
				BigInteger bigInt = BigInteger.valueOf(totalCount);
				setRecordsTotal(bigInt);
				if (getLength() == -1) {
					setLength(getRecordsTotal().intValue());
				}

				aaData = encoder.encodeTransactionSearchObj(txnReports.searchPayment(getTransactionId(), getOrderId(),
						getCustomerEmail(), merchantPayId, getPaymentType(), getStatus(), getCurrency(),
						getTransactionType(), getDateFrom(), getDateTo(), sessionUser, getStart(), getLength(),getPhoneNo(),getMopType(),getUdf4(),channel));
				recordsFiltered = recordsTotal;
			}

			else if (sessionUser.getUserType().equals(UserType.MERCHANT)) {

				totalCount = txnReports.searchPaymentCount(getTransactionId(), getOrderId(), getCustomerEmail(),
						sessionUser.getPayId(), getPaymentType(), getStatus(), getCurrency(), getTransactionType(),
						getDateFrom(), getDateTo(), sessionUser,getPhoneNo(),getMopType(),getUdf4(),channel);
				BigInteger bigInt = BigInteger.valueOf(totalCount);
				setRecordsTotal(bigInt);
				if (getLength() == -1) {
					setLength(getRecordsTotal().intValue());
				}
				aaData = encoder.encodeTransactionSearchObj(txnReports.searchPayment(getTransactionId(), getOrderId(),
						getCustomerEmail(), sessionUser.getPayId(), getPaymentType(), getStatus(), getCurrency(),
						getTransactionType(), getDateFrom(), getDateTo(), sessionUser, getStart(), getLength(),getPhoneNo(),getMopType(),getUdf4(),channel));
				recordsFiltered = recordsTotal;

			}
			else if (sessionUser.getUserType().equals(UserType.SUBADMIN) && sessionUser.getSegment().equalsIgnoreCase("Split-Payment")) {

				totalCount = txnReports.searchPaymentCountSplitPayment(getTransactionId(), getOrderId(), getCustomerEmail(),
						merchantPayId, getPaymentType(), getStatus(), getCurrency(), getTransactionType(),
						getDateFrom(), getDateTo(), sessionUser,getPhoneNo(),getMopType());
				BigInteger bigInt = BigInteger.valueOf(totalCount);
				setRecordsTotal(bigInt);
				if (getLength() == -1) {
					setLength(getRecordsTotal().intValue());
				}
				aaData = encoder.encodeTransactionSearchObj(txnReports.searchPaymentSplitPayment(getTransactionId(), getOrderId(),
						getCustomerEmail(), merchantPayId , getPaymentType(), getStatus(), getCurrency(),
						getTransactionType(), getDateFrom(), getDateTo(), sessionUser, getStart(), getLength(),getPhoneNo(),getMopType()));
				recordsFiltered = recordsTotal;

			}

			else {
				totalCount = txnReports.searchPaymentCount(getTransactionId(), getOrderId(), getCustomerEmail(),
						merchantPayId, getPaymentType(), getStatus(), getCurrency(), getTransactionType(),
						getDateFrom(), getDateTo(), sessionUser,getPhoneNo(),getMopType(),getUdf4(),channel);
				BigInteger bigInt = BigInteger.valueOf(totalCount);
				setRecordsTotal(bigInt);
				if (getLength() == -1) {
					setLength(getRecordsTotal().intValue());
				}
				aaData = encoder.encodeTransactionSearchObj(txnReports.searchPayment(getTransactionId(), getOrderId(),
						getCustomerEmail(), merchantPayId, getPaymentType(), getStatus(), getCurrency(),
						getTransactionType(), getDateFrom(), getDateTo(), sessionUser, getStart(), getLength(),getPhoneNo(),getMopType(),getUdf4(),channel));
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
		} else if (!validator.validateField(CrmFieldType.TXN_STATUS, getStatus())) {
			addFieldError(CrmFieldType.TXN_STATUS.getName(), ErrorType.INVALID_FIELD.getResponseMessage());
		}

		if (validator.validateBlankField(getCurrency())) {
		} else if (!validator.validateField(CrmFieldType.CURRENCY, getCurrency())) {
			addFieldError(CrmFieldType.CURRENCY.getName(), ErrorType.INVALID_FIELD.getResponseMessage());
		}
		
		if (validator.validateBlankField(getPhoneNo())) {
		} else if (!validator.validateField(CrmFieldType.CUST_PHONE, getPhoneNo())) {
			addFieldError(CrmFieldType.CUST_PHONE.getName(), ErrorType.INVALID_FIELD.getResponseMessage());
		}
		
		if (validator.validateBlankField(getMopType())) {
		} else if (!validator.validateField(CrmFieldType.MOP_TYPE, getMopType())) {
			addFieldError(CrmFieldType.MOP_TYPE.getName(), ErrorType.INVALID_FIELD.getResponseMessage());
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

	public List<TransactionSearch> getaaData() {
		return aaData;
	}

	public void setaaData(List<TransactionSearch> setaaData) {
		this.aaData = setaaData;
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

	public String getPhoneNo() {
		return phoneNo;
	}

	public void setPhoneNo(String phoneNo) {
		this.phoneNo = phoneNo;
	}

	public String getMopType() {
		return mopType;
	}

	public void setMopType(String mopType) {
		this.mopType = mopType;
	}

	public String getUdf4() {
		return udf4;
	}

	public void setUdf4(String udf4) {
		this.udf4 = udf4;
	}

}
