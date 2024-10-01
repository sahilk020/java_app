package com.pay10.crm.action;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pay10.commons.user.User;
import com.pay10.commons.util.Constants;

public class BulkRefundSearchAction extends AbstractSecureAction {
	private static final long serialVersionUID = 1L;
	private static Logger logger = LoggerFactory.getLogger(BulkRefundSearchAction.class.getName());

	private String email;

	public String execute() {

		User sessionUser = (User) sessionMap.get(Constants.USER.getValue());
		logger.info("BulkRefundSearchAction, EmailId :: " + sessionUser.getEmailId());
		email = sessionUser.getEmailId();
		return INPUT;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

}
