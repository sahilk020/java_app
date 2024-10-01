package com.pay10.crm.web_store_api;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

import com.pay10.commons.action.AbstractSecureAction;
import com.pay10.commons.user.Merchants;
import com.pay10.commons.user.User;
import com.pay10.commons.user.UserDao;
import com.pay10.commons.util.Constants;

public class WebStoreDetailAction extends AbstractSecureAction {
	
	@Autowired
	private UserDao userDao;

	private List<Merchants> merchants;
	private User sessionUser = null;
	
	@Override
	public String execute() {
		//sessionUser = (User) sessionMap.get(Constants.USER.getValue());
		User user = (User) sessionMap.get(Constants.USER.getValue());
		Merchants merchant = new Merchants();
		if (StringUtils.equalsIgnoreCase(user.getUserGroup().getGroup(), "Merchant")) {
			merchant.setBusinessName(user.getBusinessName());
			merchant.setPayId(user.getPayId());
			merchant.setUuId(user.getUuId());
			merchant.setEmailId(user.getEmailId());
			merchant.setIsActive(true);
			List<Merchants> merchants = new ArrayList<>();
			merchants.add(merchant);
			setMerchants(merchants);

		}
		else {
		setMerchants(userDao.getWebStoreEnableMerchantList());
		}
		return SUCCESS;
	}

	public List<Merchants> getMerchants() {
		return merchants;
	}

	public void setMerchants(List<Merchants> merchants) {
		this.merchants = merchants;
	}

}
