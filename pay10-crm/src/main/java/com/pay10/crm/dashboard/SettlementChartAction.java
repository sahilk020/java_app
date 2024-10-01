package com.pay10.crm.dashboard;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger; import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.pay10.commons.action.AbstractSecureAction;
import com.pay10.commons.exception.ErrorType;
import com.pay10.commons.user.TransactionSearch;
import com.pay10.commons.user.User;
import com.pay10.commons.user.UserDao;
import com.pay10.commons.user.UserType;
import com.pay10.commons.util.Constants;
import com.pay10.commons.util.CrmFieldConstants;
import com.pay10.commons.util.CrmFieldType;
import com.pay10.commons.util.CrmValidator;
/**
 * Rohit , Rajendra
 */

public class SettlementChartAction extends AbstractSecureAction {
 
	private static final long serialVersionUID = -301932938829538298L;
 
	@Autowired
	private CrmValidator validator;
	
	@Autowired
	private SettlementChartService getSettlementChartService;

	@Autowired
	private UserDao userDao;

	private static Logger logger = LoggerFactory.getLogger(SettlementChartAction.class.getName());
	
	
	private String payId;
	private String currency;
	private String dateFrom;
	private String dateTo;
	private String merchantEmailId;
	private String adminPayid="";
	private String paymentType;
	private String acquirer;
	private String transactionType;
	private String mopType;
	
	private List<Object> settlementData = new ArrayList<Object>();

	@Override
	public String execute() {
		try {
			if (StringUtils.isBlank(transactionType)) {
				transactionType = "ALL";
			}
			if (StringUtils.isBlank(mopType)) {
				mopType = "ALL";
			}
			if(dateFrom.equals("undefined-undefined-") || dateTo.equals("undefined-undefined-")) {
				return SUCCESS;
			}
			
			User user = (User) sessionMap.get(Constants.USER.getValue());
			
			if (user.getUserType().equals(UserType.ADMIN)|| user.getUserType().equals(UserType.SUBADMIN)
					|| user.getUserType().equals(UserType.RESELLER)
					 || user.getUserType().equals(UserType.SUPERADMIN)) {
				if(merchantEmailId.equals("ALL")) {
					setSettlementData(getSettlementChartService.preparelist(adminPayid, getCurrency(), getDateFrom(),
							getDateTo(),paymentType,acquirer,transactionType,mopType,user));
				}else {
					user = userDao.find(merchantEmailId);
					setSettlementData(getSettlementChartService.preparelist(user.getPayId(), getCurrency(), getDateFrom(),
							getDateTo(),paymentType,acquirer,transactionType,mopType,user));
				}
				
					
			
			} else if(user.getUserType().equals(UserType.SUBUSER)) {
			setSettlementData(getSettlementChartService.preparelist(user.getParentPayId(), getCurrency(), getDateFrom(),
						getDateTo(),paymentType,acquirer,transactionType,mopType,user));
			} else if(user.getUserType().equals(UserType.MERCHANT)) {
				setSettlementData(getSettlementChartService.preparelist(user.getPayId(), getCurrency(), getDateFrom(),
						getDateTo(),paymentType,acquirer,transactionType,mopType,user));
			}
			
			return SUCCESS;
			
		} catch (Exception exception) {
			logger.error("Exception", exception);
			return ERROR;
		}

	}

	@Override
	public void validate() {
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
	
	public List<Object> getSettlementData() {
		return settlementData;
	}

	public void setSettlementData(List<Object> list) {
		this.settlementData = list;
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

	public String getPayId() {
		return payId;
	}

	public void setPayId(String payId) {
		this.payId = payId;
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
	
}