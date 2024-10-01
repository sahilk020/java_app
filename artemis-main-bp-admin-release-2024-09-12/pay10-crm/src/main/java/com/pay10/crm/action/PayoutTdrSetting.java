package com.pay10.crm.action;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import com.google.gson.Gson;
import com.pay10.commons.dao.AcquirerMappingPoDao;
import com.pay10.commons.payoutEntity.PayoutAcquirer;
import com.pay10.commons.payoutEntity.PayoutAcquirerMappingDTO;
import com.pay10.commons.user.MultCurrencyCodeDao;
import com.pay10.commons.user.PayoutTdrSettingDao;
import com.pay10.commons.user.User;
import com.pay10.commons.user.UserDao;

public class PayoutTdrSetting extends AbstractSecureAction {

	@Autowired
	private UserDao userDao;
	
	@Autowired
	private PayoutTdrSettingDao payoutTdrSettingDao;

	public List<String> acquirerList = new ArrayList<>();
	
	private List<String> channelList = new ArrayList<>();
	
	private List<User> merchantList = new ArrayList<User>();
	
	private String acquirer;
	
	private String channel;
	
	@Override
	@SuppressWarnings("unchecked")
	public String execute() {
		setMerchantList(userDao.fetchUsersBasedOnPOEnable());
		return SUCCESS;
	}
	public List<User> getMerchantList() {
		return merchantList;
	}
	public void setMerchantList(List<User> merchantList) {
		this.merchantList = merchantList;
	}

	public List<String> getChannelList() {
		return channelList;
	}

	public void setChannelList(List<String> channelList) {
		this.channelList = channelList;
	}

	public String getAcquirer() {
		return acquirer;
	}

	public void setAcquirer(String acquirer) {
		this.acquirer = acquirer;
	}

	public void setChannel(String channel) {
		this.channel = channel;
	}

	public List<String> getAcquirerList() {
		return acquirerList;
	}

	public void setAcquirerList(List<String> acquirerList) {
		this.acquirerList = acquirerList;
	}

	
	
}
