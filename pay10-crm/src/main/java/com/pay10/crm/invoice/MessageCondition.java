package com.pay10.crm.invoice;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.pay10.commons.user.User;
import com.pay10.commons.user.UserDao;
import com.pay10.crm.action.AbstractSecureAction;

public class MessageCondition extends AbstractSecureAction {
	private static Logger logger = LoggerFactory.getLogger(MessageCondition.class.getName());
	private String payid;
	private String invoiceMessage;
	private String payMessage;
	private boolean tncStatus;
	@Autowired
	private UserDao userDao;

	@Override
	public String execute() {
		logger.info("MessageCondition : " + payid);
		User user=userDao.findByPayId(payid);
		setInvoiceMessage(user.getInvoiceText());
		setPayMessage(user.getPayText());
		setTncStatus(user.isTncStatus());
		logger.info("MessageCondition : " + user.getInvoiceText());
		return SUCCESS;
	}

	public String getPayid() {
		return payid;
	}

	public void setPayid(String payid) {
		this.payid = payid;
	}

	public String getInvoiceMessage() {
		return invoiceMessage;
	}

	public void setInvoiceMessage(String invoiceMessage) {
		this.invoiceMessage = invoiceMessage;
	}

	public String getPayMessage() {
		return payMessage;
	}

	public void setPayMessage(String payMessage) {
		this.payMessage = payMessage;
	}

	public boolean isTncStatus() {
		return tncStatus;
	}

	public void setTncStatus(boolean tncStatus) {
		this.tncStatus = tncStatus;
	}
	
	

}
