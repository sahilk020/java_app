package com.pay10.crm.action;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.pay10.commons.exception.ErrorType;
import com.pay10.commons.user.FunnelChatData;
import com.pay10.commons.user.User;
import com.pay10.commons.user.UserDao;
import com.pay10.commons.user.UserType;
import com.pay10.commons.util.Constants;
import com.pay10.commons.util.CrmFieldConstants;
import com.pay10.commons.util.CrmFieldType;
import com.pay10.commons.util.CrmValidator;
import com.pay10.commons.util.DateCreater;
import com.pay10.crm.actionBeans.AnalyticsDataService;

/**
 * Rajendra
 */
public class FunnelChartDataAction extends AbstractSecureAction {


	private static final long serialVersionUID = -6323553936458486881L;

	private static Logger logger = LoggerFactory.getLogger(FunnelChartDataAction.class.getName());

	private String dateFrom;
	private String dateTo;
	private String currency;
	private String merchantEmailId;
	FunnelChatData funnelChatData = new FunnelChatData();
	
	private String paymentType;
	private String acquirer;
	private String transactionType;
	private String mopType;
	
	@Autowired
	private CrmValidator validator;
	
	@Autowired
	private AnalyticsDataService AnalyticsDataService;
	
	@Autowired
	private UserDao userDao;
	
	@Override
	public String execute() {
		
		try {
			if (StringUtils.isBlank(transactionType)) {
				transactionType = "ALL";
			}

			setDateFrom(DateCreater.toDateTimeformatCreater(dateFrom));
			setDateTo(DateCreater.formDateTimeformatCreater(dateTo));
			
			User sessionUser = (User) sessionMap.get(Constants.USER.getValue());
			if (sessionUser.getUserType().equals(UserType.SUPERADMIN) || sessionUser.getUserType().equals(UserType.ADMIN)
					 || sessionUser.getUserType().equals(UserType.RESELLER)
					|| sessionUser.getUserType().equals(UserType.SUBADMIN)
					|| sessionUser.getUserType().equals(UserType.ASSOCIATE)
					|| sessionUser.getUserType().equals(UserType.MERCHANT)) {
				
				
				String merchantPayId = null;
				
				if (!merchantEmailId.equalsIgnoreCase("All")) {
					User merchant = userDao.findPayIdByEmail(merchantEmailId);
					merchantPayId = merchant.getPayId();
				} else {
					merchantPayId = merchantEmailId;
				}
				
				setFunnelChatData(AnalyticsDataService.funnelChatDataSummary(merchantPayId,getCurrency(), dateFrom, dateTo,
							sessionUser.getUserType(),paymentType,acquirer,transactionType,mopType,sessionUser));
			}else {
				String merchantPayId = null;
				User merchant = userDao.findPayId(sessionUser.getParentPayId());
				merchantPayId = merchant.getPayId();
				setFunnelChatData(AnalyticsDataService.funnelChatDataSummary(merchantPayId,getCurrency(), dateFrom, dateTo,
						merchant.getUserType(),paymentType,acquirer,transactionType,mopType,sessionUser));
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
			}
		}

		if (validator.validateBlankField(getMerchantEmailId())
				|| getMerchantEmailId().equals(CrmFieldConstants.ALL.getValue())) {
		} else if (!validator.validateField(CrmFieldType.MERCHANT_EMAIL_ID, getMerchantEmailId())) {
			addFieldError(CrmFieldType.MERCHANT_EMAIL_ID.getName(), ErrorType.INVALID_FIELD.getResponseMessage());
		}
		if (validator.validateBlankField(getMopType()) || getMopType().equals(CrmFieldConstants.ALL.getValue())) {
		} else if (!validator.validateField(CrmFieldType.MOP_TYPE, getMopType())) {
			addFieldError(CrmFieldType.MOP_TYPE.getName(), ErrorType.INVALID_FIELD.getResponseMessage());
		}
		
		if (validator.validateBlankField(getAcquirer()) || getAcquirer().equals(CrmFieldConstants.ALL.getValue())) {
		} else if (!validator.validateField(CrmFieldType.ACQUIRER, getAcquirer())) {
			addFieldError(CrmFieldType.ACQUIRER.getName(), ErrorType.INVALID_FIELD.getResponseMessage());
		}
		
		if (validator.validateBlankField(getPaymentType())) {
		} else if (!validator.validateField(CrmFieldType.PAYMENT_TYPE, getPaymentType())) {
			addFieldError(CrmFieldType.PAYMENT_TYPE.getName(), ErrorType.INVALID_FIELD.getResponseMessage());
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

	public FunnelChatData getFunnelChatData() {
		return funnelChatData;
	}

	public void setFunnelChatData(FunnelChatData funnelChatData) {
		this.funnelChatData = funnelChatData;
	}


	public String getMerchantEmailId() {
		return merchantEmailId;
	}

	public void setMerchantEmailId(String merchantEmailId) {
		this.merchantEmailId = merchantEmailId;
	}

	public String getCurrency() {
		return currency;
	}

	public void setCurrency(String currency) {
		this.currency = currency;
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
	
}
