package com.pay10.crm.action;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import com.pay10.commons.user.Merchants;
import com.pay10.commons.user.UserDao;

public class ResellerMerchantAction extends AbstractSecureAction{
	public List<Merchants> merchantList = new ArrayList<Merchants>();
	public List<Merchants> listReseller = new ArrayList<Merchants>();


	@Autowired
	private UserDao userDao;

	@SuppressWarnings("unchecked")
	public String execute() {
		setMerchantList(userDao.getResellerMerchants());
		setListReseller(userDao.getResellerList());
		
		return SUCCESS;
	}

	public List<Merchants> getMerchantList() {
		return merchantList;
	}

	public void setMerchantList(List<Merchants> merchantList) {
		this.merchantList = merchantList;
	}

	public List<Merchants> getListReseller() {
		return listReseller;
	}

	public void setListReseller(List<Merchants> listReseller) {
		this.listReseller = listReseller;
	}
	
}
