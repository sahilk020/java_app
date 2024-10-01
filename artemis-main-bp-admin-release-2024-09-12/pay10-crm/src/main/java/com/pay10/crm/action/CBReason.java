package com.pay10.crm.action;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CBReason extends AbstractSecureAction {

	private static final long serialVersionUID = -3306411281051464691L;
	private static Logger logger = LoggerFactory.getLogger(CBReason.class.getName());
	
	@Override
	public String execute() {
		logger.info("CB Reason Page Executed");
		return SUCCESS;
	}
}
