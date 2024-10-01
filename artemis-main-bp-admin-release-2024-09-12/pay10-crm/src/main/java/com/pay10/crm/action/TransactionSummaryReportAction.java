package com.pay10.crm.action;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.pay10.commons.exception.ErrorType;
import com.pay10.commons.exception.SystemException;
import com.pay10.commons.user.Surcharge;
import com.pay10.commons.user.TransactionSearch;
import com.pay10.commons.user.User;
import com.pay10.commons.user.UserDao;
import com.pay10.commons.user.UserType;
import com.pay10.commons.util.Constants;
import com.pay10.commons.util.CrmFieldConstants;
import com.pay10.commons.util.CrmFieldType;
import com.pay10.commons.util.CrmValidator;
import com.pay10.commons.util.DateCreater;

public class TransactionSummaryReportAction extends AbstractSecureAction {

	private static Logger logger = LoggerFactory.getLogger(TransactionSummaryReportAction.class.getName());
	private static final long serialVersionUID = -3131381841294843726L;

	private String dateFrom;
	private String dateTo;
	public String paymentMethods;
	public String acquirer;
	private String merchantEmailId;
	private String currency;
	private int draw;
	private int length;
	private int start;
	private String transactionType;
	private BigInteger recordsTotal;
	public BigInteger recordsFiltered;
	private String paymentsRegion;
	private String cardHolderType;
	private String mopType;
	private String pgRefNum;
	private String orderId;
	private String phoneNo;
	private int count = 1;
	private List<TransactionSearch> aaData = new ArrayList<TransactionSearch>();
	public static final BigDecimal ONE_HUNDRED = new BigDecimal(100);
	List<Surcharge> surchargeList = new ArrayList<Surcharge>();
	SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.ENGLISH);

	@Autowired
	private SummaryReportQuery summaryReportQuery;

	@Autowired
	private UserDao userDao;

	private User sessionUser = new User();

	@Override
	public String execute() {
		
		int totalCount = 0;
		sessionUser = (User) sessionMap.get(Constants.USER.getValue());
		logger.info("Inside search summary report Action");
		User sessionUser = (User) sessionMap.get(Constants.USER.getValue());
		List<TransactionSearch> transactionPaginationList = new ArrayList<TransactionSearch>();
		List<TransactionSearch> transactionList1 = new ArrayList<TransactionSearch>();

		if (StringUtils.isBlank(acquirer)) {
			acquirer = "ALL";
		}

		setDateFrom(dateFrom+CrmFieldConstants.TO_TIME_FORMAT.getValue());
		setDateTo(dateTo+CrmFieldConstants.TO_TIME_FORMAT.getValue());

		String merchantPayId = null;
		if (sessionUser.getUserType().equals(UserType.SUPERADMIN) || sessionUser.getUserType().equals(UserType.ADMIN)
				|| sessionUser.getUserType().equals(UserType.RESELLER)
				|| sessionUser.getUserType().equals(UserType.SUBADMIN)
				|| sessionUser.getUserType().equals(UserType.ASSOCIATE)) {

			if (!merchantEmailId.equalsIgnoreCase("All")) {
				User merchant = userDao.findPayIdByEmail(merchantEmailId);
				merchantPayId = merchant.getPayId();
			} else {
				merchantPayId = merchantEmailId;
			}
			try {
				transactionPaginationList = summaryReportQuery.summaryReportNew(dateFrom, dateTo, merchantPayId,
						paymentMethods, acquirer, currency, sessionUser, getStart(), getLength(), getPaymentsRegion(),
						getCardHolderType(), pgRefNum, mopType, transactionType, orderId,phoneNo);

				totalCount = summaryReportQuery.summaryReportCountNew(dateFrom, dateTo, merchantPayId,
						paymentMethods, acquirer, currency, sessionUser, getStart(), getLength(), getPaymentsRegion(),
						getCardHolderType(), pgRefNum, mopType, transactionType, orderId,phoneNo);
				logger.info("Inside search summary report Action , totalCount = " + totalCount);
			} catch (SystemException exception) {
				logger.error("Exception", exception);
			}
			transactionList1.addAll(transactionPaginationList);
			BigInteger bigInt = BigInteger.valueOf(totalCount);
			setRecordsTotal(bigInt);
			if (getLength() == -1) {
				setLength(getRecordsTotal().intValue());
			}
			setAaData(transactionList1);
			recordsFiltered = getRecordsTotal();
			return SUCCESS;
		} else if (sessionUser.getUserType().equals(UserType.MERCHANT)
				|| sessionUser.getUserType().equals(UserType.SUBUSER)) {


				User merchant = sessionUser;
				merchantPayId = merchant.getPayId();
				try {
					transactionPaginationList = summaryReportQuery.summaryReportNew(dateFrom, dateTo, sessionUser.getPayId(),
							paymentMethods, acquirer, currency, sessionUser, getStart(), getLength(), getPaymentsRegion(),
							getCardHolderType(), pgRefNum, mopType, transactionType, orderId,phoneNo);

					totalCount = summaryReportQuery.summaryReportCountNew(dateFrom, dateTo, sessionUser.getPayId(),
							paymentMethods, acquirer, currency, sessionUser, getStart(), getLength(), getPaymentsRegion(),
							getCardHolderType(), pgRefNum, mopType, transactionType,orderId,phoneNo);
					logger.info("Inside search summary report Action , totalCount = " + totalCount);
				} catch (SystemException exception) {
					logger.error("Exception", exception);
				}
				transactionList1.addAll(transactionPaginationList);

				BigInteger bigInt = BigInteger.valueOf(totalCount);
				setRecordsTotal(bigInt);
				if (getLength() == -1) {
					setLength(getRecordsTotal().intValue());
				}
				setAaData(transactionList1);
				recordsFiltered = recordsTotal;
				return SUCCESS;
				
		} else if (sessionUser.getUserType().equals(UserType.RESELLER)) {
			if (!merchantEmailId.equalsIgnoreCase("All")) {
				User merchant = userDao.findPayIdByEmail(merchantEmailId);
				merchantPayId = merchant.getPayId();
				try {
					transactionPaginationList = summaryReportQuery.summaryReport(dateFrom, dateTo, merchantPayId,
							paymentMethods, acquirer, currency, sessionUser, getStart(), getLength(),
							getPaymentsRegion(), getCardHolderType(), pgRefNum, mopType, transactionType,orderId,phoneNo);

					totalCount = summaryReportQuery.summaryReportRecord(dateFrom, dateTo, merchantPayId, paymentMethods,
							acquirer, currency, sessionUser, getPaymentsRegion(), getCardHolderType(), pgRefNum,
							mopType, transactionType,orderId,phoneNo);
				} catch (SystemException exception) {
					logger.error("Exception", exception);
				}

				transactionList1.addAll(transactionPaginationList);

				BigInteger bigInt = BigInteger.valueOf(transactionPaginationList.size());
				setRecordsTotal(bigInt);
				if (getLength() == -1) {
					setLength(getRecordsTotal().intValue());
				}
				setAaData(transactionList1);
				recordsFiltered = recordsTotal;
				return SUCCESS;
			}

		}

		else if (sessionUser.getUserType().equals(UserType.ACQUIRER)
				|| sessionUser.getUserType().equals(UserType.SUBACQUIRER)) {

			if (!merchantEmailId.equalsIgnoreCase("All")) {

				User merchant = userDao.findPayIdByEmail(merchantEmailId);
				merchantPayId = merchant.getPayId();

				try {
					transactionPaginationList = summaryReportQuery.summaryReport(dateFrom, dateTo, merchantPayId,
							paymentMethods, acquirer, currency, sessionUser, getStart(), getLength(),
							getPaymentsRegion(), getCardHolderType(), pgRefNum, mopType, transactionType,orderId,phoneNo);

					totalCount = summaryReportQuery.summaryReportRecord(dateFrom, dateTo, merchantPayId, paymentMethods,
							acquirer, currency, sessionUser, getPaymentsRegion(), getCardHolderType(), pgRefNum,
							mopType, transactionType,orderId,phoneNo);
				} catch (SystemException exception) {
					logger.error("Exception", exception);
				}

				transactionList1.addAll(transactionPaginationList);

				BigInteger bigInt = BigInteger.valueOf(totalCount);
				setRecordsTotal(bigInt);
				if (getLength() == -1) {
					setLength(getRecordsTotal().intValue());
				}
				setAaData(transactionList1);
				recordsFiltered = recordsTotal;
				return SUCCESS;
			}

		}

		return SUCCESS;
	}

//	@Override
//	public void validate() {
//		CrmValidator validator = new CrmValidator();
//
//		if (validator.validateBlankField(getCurrency())) {
//		} else if (!validator.validateField(CrmFieldType.CURRENCY, getCurrency())) {
//			addFieldError(CrmFieldType.CURRENCY.getName(), ErrorType.INVALID_FIELD.getResponseMessage());
//		}
//
//		if (validator.validateBlankField(getDateFrom())) {
//		} else if (!validator.validateField(CrmFieldType.DATE_FROM, getDateFrom())) {
//			addFieldError(CrmFieldType.DATE_FROM.getName(), ErrorType.INVALID_FIELD.getResponseMessage());
//		}
//
//		if (validator.validateBlankField(getDateTo())) {
//		} else if (!validator.validateField(CrmFieldType.DATE_TO, getDateTo())) {
//			addFieldError(CrmFieldType.DATE_TO.getName(), ErrorType.INVALID_FIELD.getResponseMessage());
//		}
//
//		if (!validator.validateBlankField(getDateTo())) {
//			if (DateCreater.formatStringToDate(DateCreater.formatFromDate(getDateFrom()))
//					.compareTo(DateCreater.formatStringToDate(DateCreater.formatFromDate(getDateTo()))) > 0) {
//				addFieldError(CrmFieldType.DATE_FROM.getName(), CrmFieldConstants.FROMTO_DATE_VALIDATION.getValue());
//			} else if (DateCreater.diffDate(getDateFrom(), getDateTo()) > 31) {
//				addFieldError(CrmFieldType.DATE_FROM.getName(), CrmFieldConstants.DATE_RANGE.getValue());
//			}
//		}
//
//		if (validator.validateBlankField(getMerchantEmailId())
//				|| getMerchantEmailId().equals(CrmFieldConstants.ALL.getValue())) {
//		} else if (!validator.validateField(CrmFieldType.MERCHANT_EMAIL_ID, getMerchantEmailId())) {
//			addFieldError(CrmFieldType.MERCHANT_EMAIL_ID.getName(), ErrorType.INVALID_FIELD.getResponseMessage());
//		}
//
//	}

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

	public List<TransactionSearch> getAaData() {
		return aaData;
	}

	public void setAaData(List<TransactionSearch> aaData) {
		this.aaData = aaData;
	}

	public String getAcquirer() {
		return acquirer;
	}

	public void setAcquirer(String acquirer) {
		this.acquirer = acquirer;
	}

	public String getCurrency() {
		return currency;
	}

	public void setCurrency(String currency) {
		this.currency = currency;
	}

	public String getMerchantEmailId() {
		return merchantEmailId;
	}

	public void setMerchantEmailId(String merchantEmailId) {
		this.merchantEmailId = merchantEmailId;
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

	public String getPaymentMethods() {
		return paymentMethods;
	}

	public void setPaymentMethods(String paymentMethods) {
		this.paymentMethods = paymentMethods;
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

	public String getPgRefNum() {
		return pgRefNum;
	}

	public void setPgRefNum(String pgRefNum) {
		this.pgRefNum = pgRefNum;
	}

	public String getMopType() {
		return mopType;
	}

	public void setMopType(String mopType) {
		this.mopType = mopType;
	}

	public String getTransactionType() {
		return transactionType;
	}

	public void setTransactionType(String transactionType) {
		this.transactionType = transactionType;
	}
	public String getOrderId() {
		return orderId;
	}

	public void setOrderId(String orderId) {
		this.orderId = orderId;
	}

	public String getPhoneNo() {
		return phoneNo;
	}

	public void setPhoneNo(String phoneNo) {
		this.phoneNo = phoneNo;
	}

}
