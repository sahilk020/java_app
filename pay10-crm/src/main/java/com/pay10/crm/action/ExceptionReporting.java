package com.pay10.crm.action;


import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.pay10.commons.user.Merchants;
import com.pay10.commons.user.User;
import com.pay10.commons.user.UserDao;
import com.pay10.commons.util.Constants;

public class ExceptionReporting extends AbstractSecureAction {

	private static final long serialVersionUID = -3306411281051464691L;
	private static Logger logger = LoggerFactory.getLogger(ExceptionReporting.class.getName());
	@Autowired
	private UserDao userDao;
	
	
	private User sessionUser = null;
	private List<Merchants> merchantList = new ArrayList<Merchants>();
	private String fileName;
	 
	



	public String getFileName() {
		return fileName;
	}



	public void setFileName(String fileName) {
		this.fileName = fileName;
	}



	public List<Merchants> getMerchantList() {
		return merchantList;
	}



	public void setMerchantList(List<Merchants> merchantList) {
		this.merchantList = merchantList;
	}

	@Override
	public String execute() {
		logger.info("ExceptionList Report Page Executed");
//		sessionUser = (User) sessionMap.get(Constants.USER.getValue());
//		setMerchantList(userDao.getMerchantList(sessionMap.get(Constants.SEGMENT.getValue()).toString(), sessionUser.getRole().getId()));
		return SUCCESS;
	}
}
