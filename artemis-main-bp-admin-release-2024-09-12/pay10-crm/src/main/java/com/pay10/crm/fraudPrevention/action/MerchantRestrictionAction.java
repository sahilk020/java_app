package com.pay10.crm.fraudPrevention.action;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.pay10.commons.user.MerchantFpsAccess;
import com.pay10.commons.user.MerchantFpsAccessDao;
import com.pay10.commons.user.Merchants;
import com.pay10.commons.user.User;
import com.pay10.commons.user.UserDao;
import com.pay10.commons.user.UserType;
import com.pay10.commons.util.Constants;
import com.pay10.crm.action.AbstractSecureAction;
import com.pay10.crm.actionBeans.CurrencyMapProvider;

/**
 * @author Rajendra
 *
 */
public class MerchantRestrictionAction  extends AbstractSecureAction {
	
	private static final long serialVersionUID = -6323553936458486881L;

	private static Logger logger = LoggerFactory.getLogger(MerchantRestrictionAction.class.getName());

	private String payId;
	private String preferenceSet;
	private boolean activateMerchantMgmt;
	private Map<String, String> currencyMap = new LinkedHashMap<String, String>();
	
	@Autowired
	private MerchantFpsAccessDao merchantFpsAccessDao;

	@Override
	public String execute() {
		MerchantFpsAccess merchantFpsAccess = new MerchantFpsAccess();
		CurrencyMapProvider currencyMapProvider = new CurrencyMapProvider();
		try {
			User sessionUser = (User) sessionMap.get(Constants.USER.getValue());
			if (sessionUser.getUserType().equals(UserType.MERCHANT)) {
				payId = sessionUser.getPayId();				
				setPayId(payId);
				merchantFpsAccess = merchantFpsAccessDao.getMerchantFpsAccessByPayId(getPayId());
				if (merchantFpsAccess != null) {
					setPreferenceSet(merchantFpsAccess.getPreferenceSet());
					setActivateMerchantMgmt(merchantFpsAccess.isActivateMerchantMgmt());
				}else {
					setPreferenceSet("");
				}
				currencyMap = currencyMapProvider.currencyMap(sessionUser);
			}

		} catch (Exception e) {
			logger.error("Exception in getting transaction summary count data " + e);
			return NONE;
		}

		return SUCCESS;
	}


	public boolean isActivateMerchantMgmt() {
		return activateMerchantMgmt;
	}


	public void setActivateMerchantMgmt(boolean activateMerchantMgmt) {
		this.activateMerchantMgmt = activateMerchantMgmt;
	}
	
	public String getPreferenceSet() {
		return preferenceSet;
	}

	public void setPreferenceSet(String preferenceSet) {
		this.preferenceSet = preferenceSet;
	}

	public String getPayId() {
		return payId;
	}

	public void setPayId(String payId) {
		this.payId = payId;
	}

	
	public Map<String, String> getCurrencyMap() {
		return currencyMap;
	}

	public void setCurrencyMap(Map<String, String> currencyMap) {
		this.currencyMap = currencyMap;
	}
	
}
