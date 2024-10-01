package com.pay10.crm.dispute_management;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.pay10.commons.user.Merchants;
import com.pay10.commons.user.User;
import com.pay10.commons.user.UserDao;
import com.pay10.commons.util.Constants;
import com.pay10.crm.action.AbstractSecureAction;

public class DisplayChargebackForm extends AbstractSecureAction {

	
	private static final long serialVersionUID = -5203748631719636792L;
	private static final Logger logger = LoggerFactory.getLogger(DisplayChargebackForm.class.getName());
	
	@Autowired
	private UserDao userDao;
	private List<Merchants> merchants;
	private long id;
	private User sessionUser = null;
	private String payid;
	private String caseid;
	@SuppressWarnings("unchecked")
	public String execute() {
		
		try {
			
			sessionUser = (User) sessionMap.get(Constants.USER.getValue());
			
			setMerchants(userDao.getActiveMerchantList(sessionMap.get(Constants.SEGMENT.getValue()).toString(), sessionUser.getRole().getId()));
			return INPUT;
		} catch (Exception exception) {
			logger.error("Exception : ", exception);
			return ERROR;
		}
	}
	public List<Merchants> getMerchants() {
		return merchants;
	}
	public void setMerchants(List<Merchants> merchants) {
		this.merchants = merchants;
	}
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public String getPayid() {
		return payid;
	}
	public void setPayid(String payid) {
		this.payid = payid;
	}
	public String getCaseid() {
		return caseid;
	}
	public void setCaseid(String caseid) {
		this.caseid = caseid;
	}
	
}
