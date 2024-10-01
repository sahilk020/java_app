package com.pay10.crm.action;

import java.math.BigInteger;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.pay10.commons.exception.ErrorType;
import com.pay10.commons.user.CompanyProfileDao;
import com.pay10.commons.user.CompanyProfileUi;
import com.pay10.commons.user.Merchants;
import com.pay10.commons.user.TransactionSearchNew;
import com.pay10.commons.user.User;
import com.pay10.commons.user.UserDao;
import com.pay10.commons.user.UserType;
import com.pay10.commons.util.Constants;
import com.pay10.commons.util.CrmFieldConstants;
import com.pay10.commons.util.CrmFieldType;
import com.pay10.commons.util.CrmValidator;
import com.pay10.commons.util.Currency;
import com.pay10.commons.util.DataEncoder;
import com.pay10.commons.util.DateCreater;
import com.pay10.commons.util.StatusType;
import com.pay10.commons.util.StatusTypeUI;
import com.pay10.commons.util.TransactionType;
import com.pay10.commons.util.TxnType;
import com.pay10.crm.actionBeans.CurrencyMapProvider;
import com.pay10.crm.actionBeans.SessionUserIdentifier;
import com.pay10.crm.mongoReports.TxnReports;

public class LiabilityHoldReleaseAction extends AbstractSecureAction {
	private static Logger logger = LoggerFactory.getLogger(LiabilityHoldReleaseAction.class.getName());
	private static final long serialVersionUID = -2211460869118319593L;

	public static Logger getLogger() {
		return logger;
	}

	public static void setLogger(Logger logger) {
		LiabilityHoldReleaseAction.logger = logger;
	}

	private List<TransactionSearchNew> aaData = new ArrayList<TransactionSearchNew>();
	private String acquirer;
	private String cardNumber;
	private List<CompanyProfileUi> companyList = new ArrayList<CompanyProfileUi>();
	private String currency;
	private Map<String, String> currencyMap = new HashMap<String, String>();
	@Autowired
	private CurrencyMapProvider currencyMapProvider;
	private String customerEmail;
	private String customerPhone;
	private String dateFrom;
	private String dateTo;
	private int draw;
	@Autowired
	private DataEncoder encoder;
	private String endTime;
	private String ipAddress;
	private int length;
	private List<StatusTypeUI> lst;
	private String merchantEmailId;
	private List<Merchants> merchantList = new ArrayList<Merchants>();
	private String mopType;
	private String orderId;
	private String paymentType;
	private HttpServletRequest request;
	private User sessionUser = null;
	private int start;
	private String startTime;
	private String status;
	private List<StatusType> statusList;
	private String tenantId;
	private String totalAmount;
	private String transactionId;
	private String transactionType;
	@Autowired
	private TxnReports txnReports;
	private List<TxnType> txnTypelist;
	@Autowired
	private UserDao userDao;

	@Autowired
	private SessionUserIdentifier userIdentifier;

	private BigInteger recordsTotal = BigInteger.ZERO;
	private BigInteger recordsFiltered = BigInteger.ZERO;
	
	
	
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

	@Override
	@SuppressWarnings("unchecked")
	public String execute() {
		try {

			sessionUser = (User) sessionMap.get(Constants.USER.getValue());
			if (sessionUser.getUserType().equals(UserType.ADMIN) || sessionUser.getUserType().equals(UserType.SUBADMIN)
					|| sessionUser.getUserType().equals(UserType.SUPERADMIN)
					|| sessionUser.getUserType().equals(UserType.SUBSUPERADMIN)) {
				// setMerchantList(userDao.getMerchantActiveList());
				setMerchantList(userDao.getMerchantActiveList(sessionMap.get(Constants.SEGMENT.getValue()).toString(),
						sessionUser.getRole().getId()));
				currencyMap = Currency.getAllCurrency();
				companyList = new CompanyProfileDao().getAllCompanyProfileList();
			} else if (sessionUser.getUserType().equals(UserType.RESELLER)) {
				setMerchantList(userDao.getResellerMerchantList(sessionUser.getResellerId()));
				currencyMap = Currency.getAllCurrency();
			} else if (sessionUser.getUserType().equals(UserType.MERCHANT)
					|| sessionUser.getUserType().equals(UserType.SUBUSER)
					|| sessionUser.getUserType().equals(UserType.SUBACQUIRER)) {
				Merchants merchant = new Merchants();
				merchant.setEmailId(sessionUser.getEmailId());
				merchant.setBusinessName(sessionUser.getBusinessName());
				merchant.setPayId(sessionUser.getPayId());
				merchantList.add(merchant);
				if (sessionUser.getUserType().equals(UserType.SUBUSER)
						|| sessionUser.getUserType().equals(UserType.SUBACQUIRER)) {
					String parentMerchantPayId = sessionUser.getParentPayId();
					User parentMerchant = userDao.findPayId(parentMerchantPayId);
					merchant.setMerchant(parentMerchant);
					merchantList.add(merchant);
					Object[] obj = merchantList.toArray();
					for (Object sortList : obj) {
						if (merchantList.indexOf(sortList) != merchantList.lastIndexOf(sortList)) {
							merchantList.remove(merchantList.lastIndexOf(sortList));
						}
					}
				}
				currencyMap = currencyMapProvider.currencyMap(sessionUser);
			}
			setTxnTypelist(TxnType.gettxnType());
			setLst(StatusTypeUI.getStatusType());
			setStatusList(StatusType.getStatusType());
		} catch (Exception exception) {
			logger.error("Exception", exception);
			return ERROR;
		}

		return SUCCESS;

	}

	public String liabilityHoldList() {
		if (acquirer.isEmpty()) {
			acquirer = "ALL";
		}
		if (status.isEmpty()) {
			status = "ALL";

		}

		if (tenantId == null) {
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
		sessionUser = (User) sessionMap.get(Constants.USER.getValue());
        setTransactionType(TransactionType.SALE.getName());
		if (startTime != null && endTime != null && !startTime.isEmpty() && !endTime.isEmpty()) {
			setDateFrom(fromDateWithTime);
			setDateTo(toDateWithTime);
		} else {
			setDateFrom(DateCreater.toDateTimeformatCreater(dateFrom));
			setDateTo(DateCreater.formDateTimeformatCreater(dateTo));
		}

		// setDateFrom(DateCreater.toDateTimeformatCreater(dateFrom));
		// setDateTo(DateCreater.formDateTimeformatCreater(dateTo));

		try {

			String merchantPayId = userIdentifier.getMerchantPayId(sessionUser, merchantEmailId);
			logger.info("Inside TransactionSearchActionAdmin , merchantPayId = " + merchantPayId);
			BigInteger bigInt = BigInteger.valueOf(txnReports.newSearchPaymentHoldReleaseCount(transactionId, orderId, customerEmail, customerPhone, paymentType, 
					status, currency, transactionType, mopType, acquirer, merchantPayId, dateFrom, dateTo, sessionUser, start, length,tenantId,ipAddress,totalAmount,"HOLD"));//,ipAddress,totalAmount));
			logger.info("Inside liabilityHoldList , bigInt=========== = " + bigInt);
			setRecordsTotal(bigInt);
			if (getLength() == -1) {
				setLength(getRecordsTotal().intValue());
			}
			aaData = encoder.encodeTransactionSearchObjNew(txnReports.searchTransactionsForLiabilityHoldRelease(
					transactionId, orderId, customerEmail, customerPhone, paymentType, status, currency,
					transactionType, mopType, acquirer, merchantPayId, dateFrom, dateTo, sessionUser, start, length,
					tenantId, ipAddress, totalAmount, "HOLD"));

			aaData = aaData.stream().sorted(new Comparator<TransactionSearchNew>() {

				@Override
				public int compare(TransactionSearchNew o1, TransactionSearchNew o2) {
					String date1 = StringUtils.isBlank(o1.getCreateDate()) ? o1.getDateFrom() : o1.getCreateDate();
					String date2 = StringUtils.isBlank(o2.getCreateDate()) ? o2.getDateFrom() : o2.getCreateDate();
					return date2.compareTo(date1);
				}
			}).collect(Collectors.toList());
			recordsFiltered = recordsTotal;
		} catch (Exception exception) {
			logger.error("Exception", exception);
			return ERROR;
		}
		return SUCCESS;

	}
	
	public String liabilityReleaseList() {
		if (acquirer.isEmpty()) {
			acquirer = "ALL";
		}
		if (status.isEmpty()) {
			status = "ALL";

		}

		if (tenantId == null) {
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
		sessionUser = (User) sessionMap.get(Constants.USER.getValue());
		setTransactionType(TransactionType.SALE.getName());
		if (startTime != null && endTime != null && !startTime.isEmpty() && !endTime.isEmpty()) {
			setDateFrom(fromDateWithTime);
			setDateTo(toDateWithTime);
		} else {
			setDateFrom(DateCreater.toDateTimeformatCreater(dateFrom));
			setDateTo(DateCreater.formDateTimeformatCreater(dateTo));
		}


		try {

			String merchantPayId = userIdentifier.getMerchantPayId(sessionUser, merchantEmailId);
			logger.info("Inside TransactionSearchActionAdmin , merchantPayId = " + merchantPayId);
			BigInteger bigInt = BigInteger.valueOf(txnReports.newSearchPaymentHoldReleaseCount(transactionId, orderId, customerEmail, customerPhone, paymentType, 
					status, currency, transactionType, mopType, acquirer, merchantPayId, dateFrom, dateTo, sessionUser, start, length,tenantId,ipAddress,totalAmount,"RELEASE"));//,ipAddress,totalAmount));
			logger.info("Inside liabilityReleaseList , bigInt=========== = " + bigInt);
			setRecordsTotal(bigInt);
			if (getLength() == -1) {
				setLength(getRecordsTotal().intValue());
			}
			aaData = encoder.encodeTransactionSearchObjNew(txnReports.searchTransactionsForLiabilityHoldRelease(
					transactionId, orderId, customerEmail, customerPhone, paymentType, status, currency,
					transactionType, mopType, acquirer, merchantPayId, dateFrom, dateTo, sessionUser, start, length,
					tenantId, ipAddress, totalAmount, "RELEASE"));
			aaData = aaData.stream().sorted(new Comparator<TransactionSearchNew>() {

				@Override
				public int compare(TransactionSearchNew o1, TransactionSearchNew o2) {
					String date1 = StringUtils.isBlank(o1.getCreateDate()) ? o1.getDateFrom() : o1.getCreateDate();
					String date2 = StringUtils.isBlank(o2.getCreateDate()) ? o2.getDateFrom() : o2.getCreateDate();
					return date2.compareTo(date1);
				}
			}).collect(Collectors.toList());
			recordsFiltered = recordsTotal;
		} catch (Exception exception) {
			logger.error("Exception", exception);
			return ERROR;
		}
		return SUCCESS;

	}

	public List<TransactionSearchNew> getAaData() {
		return aaData;
	}

	public String getAcquirer() {
		return acquirer;
	}

	public String getCardNumber() {
		return cardNumber;
	}

	public List<CompanyProfileUi> getCompanyList() {
		return companyList;
	}

	public String getCurrency() {
		return currency;
	}

	public Map<String, String> getCurrencyMap() {
		return currencyMap;
	}

	public String getCustomerEmail() {
		return customerEmail;
	}

	public String getCustomerPhone() {
		return customerPhone;
	}

	public String getDateFrom() {
		return dateFrom;
	}

	public String getDateTo() {
		return dateTo;
	}

	public int getDraw() {
		return draw;
	}

	public String getEndTime() {
		return endTime;
	}

	public String getIpAddress() {
		return ipAddress;
	}

	public int getLength() {
		return length;
	}

	public List<StatusTypeUI> getLst() {
		return lst;
	}

	public String getMerchantEmailId() {
		return merchantEmailId;
	}

	public List<Merchants> getMerchantList() {
		return merchantList;
	}

	public String getMopType() {
		return mopType;
	}

	public String getOrderId() {
		return orderId;
	}

	public String getPaymentType() {
		return paymentType;
	}

	public HttpServletRequest getRequest() {
		return request;
	}

	public User getSessionUser() {
		return sessionUser;
	}

	public int getStart() {
		return start;
	}

	public String getStartTime() {
		return startTime;
	}

	public String getStatus() {
		return status;
	}

	public List<StatusType> getStatusList() {
		return statusList;
	}

	public String getTenantId() {
		return tenantId;
	}

	public String getTotalAmount() {
		return totalAmount;
	}

	public String getTransactionId() {
		return transactionId;
	}

	public String getTransactionType() {
		return transactionType;
	}

	public List<TxnType> getTxnTypelist() {
		return txnTypelist;
	}

	public void setAaData(List<TransactionSearchNew> aaData) {
		this.aaData = aaData;
	}

	public void setAcquirer(String acquirer) {
		this.acquirer = acquirer;
	}

	public void setCardNumber(String cardNumber) {
		this.cardNumber = cardNumber;
	}

	public void setCompanyList(List<CompanyProfileUi> companyList) {
		this.companyList = companyList;
	}

	public void setCurrency(String currency) {
		this.currency = currency;
	}

	public void setCurrencyMap(Map<String, String> currencyMap) {
		this.currencyMap = currencyMap;
	}

	public void setCustomerEmail(String customerEmail) {
		this.customerEmail = customerEmail;
	}

	public void setCustomerPhone(String customerPhone) {
		this.customerPhone = customerPhone;
	}

	public void setDateFrom(String dateFrom) {
		this.dateFrom = dateFrom;
	}

	public void setDateTo(String dateTo) {
		this.dateTo = dateTo;
	}

	public void setDraw(int draw) {
		this.draw = draw;
	}

	public void setEndTime(String endTime) {
		this.endTime = endTime;
	}

	public void setIpAddress(String ipAddress) {
		this.ipAddress = ipAddress;
	}

	public void setLength(int length) {
		this.length = length;
	}

	public void setLst(List<StatusTypeUI> lst) {
		this.lst = lst;
	}

	public void setMerchantEmailId(String merchantEmailId) {
		this.merchantEmailId = merchantEmailId;
	}

	public void setMerchantList(List<Merchants> merchantList) {
		this.merchantList = merchantList;
	}

	public void setMopType(String mopType) {
		this.mopType = mopType;
	}

	public void setOrderId(String orderId) {
		this.orderId = orderId;
	}

	public void setPaymentType(String paymentType) {
		this.paymentType = paymentType;
	}

	public void setRequest(HttpServletRequest request) {
		this.request = request;
	}

	public void setSessionUser(User sessionUser) {
		this.sessionUser = sessionUser;
	}

	public void setStart(int start) {
		this.start = start;
	}

	public void setStartTime(String startTime) {
		this.startTime = startTime;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public void setStatusList(List<StatusType> statusList) {
		this.statusList = statusList;
	}

	public void setTenantId(String tenantId) {
		this.tenantId = tenantId;
	}

	public void setTotalAmount(String totalAmount) {
		this.totalAmount = totalAmount;
	}

	public void setTransactionId(String transactionId) {
		this.transactionId = transactionId;
	}

	public void setTransactionType(String transactionType) {
		this.transactionType = transactionType;
	}

	public void setTxnTypelist(List<TxnType> txnTypelist) {
		this.txnTypelist = txnTypelist;
	}

}
