package com.pay10.crm.action;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import com.pay10.commons.user.Merchants;
import com.pay10.commons.user.User;
import com.pay10.commons.user.UserDao;
import com.pay10.commons.util.Constants;

public class ChargebackJourney extends AbstractSecureAction {

	private static final long serialVersionUID = -2211460869118319593L;

	@Autowired
	private UserDao userDao;
	
	private User sessionUser = null;
	
	private List<Merchants> merchantList = new ArrayList<Merchants>();
	
	
	
	public List<Merchants> getMerchantList() {
		return merchantList;
	}



	public void setMerchantList(List<Merchants> merchantList) {
		this.merchantList = merchantList;
	}

	@Override
	public String execute() {
		System.out.println("ChargebackJourney");
		sessionUser = (User) sessionMap.get(Constants.USER.getValue());
		setMerchantList(userDao.getMerchantList(sessionMap.get(Constants.SEGMENT.getValue()).toString(), sessionUser.getRole().getId()));
		return SUCCESS;
	}
}
