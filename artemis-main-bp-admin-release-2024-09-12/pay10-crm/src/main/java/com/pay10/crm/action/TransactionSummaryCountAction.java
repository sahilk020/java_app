package com.pay10.crm.action;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger; import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.pay10.commons.exception.ErrorType;
import com.pay10.commons.user.TransactionCountSearch;
import com.pay10.commons.user.User;
import com.pay10.commons.user.UserDao;
import com.pay10.commons.user.UserType;
import com.pay10.commons.util.Constants;
import com.pay10.commons.util.CrmFieldConstants;
import com.pay10.commons.util.CrmFieldType;
import com.pay10.commons.util.CrmValidator;
import com.pay10.commons.util.DateCreater;
import com.pay10.crm.actionBeans.TransactionSummaryCountService;

@SuppressWarnings("serial")
public class TransactionSummaryCountAction extends AbstractSecureAction {

	private static Logger logger = LoggerFactory.getLogger(TransactionSummaryCountAction.class.getName());

	private String dateFrom;
	private String dateTo;
	public String paymentMethods;
	private String merchantEmailId;
	private String transactionType;
	private String paymentsRegion;
	private String cardHolderType;
	private String mopType;
	private String acquirer;
	
	TransactionCountSearch transactionCountSearch = new TransactionCountSearch();

	@Autowired
	private TransactionSummaryCountService transactionSummaryCountService;
	
	@Autowired
	private UserDao userDao;
	
	@Override
	public String execute() {
		
		try {
			
			if (StringUtils.isBlank(acquirer)) {
				acquirer = "ALL";
			}
			setDateFrom(DateCreater.toDateTimeformatCreater(dateFrom));
			setDateTo(DateCreater.formDateTimeformatCreater(dateTo));
			
			User sessionUser = (User) sessionMap.get(Constants.USER.getValue());
			if (sessionUser.getUserType().equals(UserType.SUPERADMIN) || sessionUser.getUserType().equals(UserType.ADMIN)
					|| sessionUser.getUserType().equals(UserType.SUBADMIN)
					|| sessionUser.getUserType().equals(UserType.ASSOCIATE)) {
				
				
				String merchantPayId = null;
				
				if (!merchantEmailId.equalsIgnoreCase("All")) {
					User merchant = userDao.findPayIdByEmail(merchantEmailId);
					merchantPayId = merchant.getPayId();
				} else {
					merchantPayId = merchantEmailId;
				}
				setTransactionCountSearch(transactionSummaryCountService.getTransactionCountForRevenue(dateFrom, dateTo, merchantPayId,
						paymentMethods, acquirer,sessionUser, 1, 1, getPaymentsRegion(),
						getCardHolderType(), mopType,transactionType));
			}

		}
		catch(Exception e) {
			logger.error("Exception in getting transaction summary count data " +e);
		}
		
		
		return SUCCESS;
	}

	@Override
	public void validate() {
		CrmValidator validator = new CrmValidator();

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

		if (validator.validateBlankField(getMerchantEmailId())
				|| getMerchantEmailId().equals(CrmFieldConstants.ALL.getValue())) {
		} else if (!validator.validateField(CrmFieldType.MERCHANT_EMAIL_ID, getMerchantEmailId())) {
			addFieldError(CrmFieldType.MERCHANT_EMAIL_ID.getName(), ErrorType.INVALID_FIELD.getResponseMessage());
		}

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


	public TransactionCountSearch getTransactionCountSearch() {
		return transactionCountSearch;
	}

	public void setTransactionCountSearch(TransactionCountSearch transactionCountSearch) {
		this.transactionCountSearch = transactionCountSearch;
	}

	public String getMerchantEmailId() {
		return merchantEmailId;
	}

	public void setMerchantEmailId(String merchantEmailId) {
		this.merchantEmailId = merchantEmailId;
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

	public String getAcquirer() {
		return acquirer;
	}

	public void setAcquirer(String acquirer) {
		this.acquirer = acquirer;
	}

}
