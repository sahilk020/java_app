package com.pay10.crm.dispute_management;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.pay10.crm.action.AbstractSecureAction;


public class DisplayChargebackList extends AbstractSecureAction {
	
	/**
	 * Sweety
	 */
	
	private static final long serialVersionUID = -8081930439236006475L;
	private static final Logger logger = LoggerFactory.getLogger(DisplayChargebackList.class.getName());

	@Override
	public String execute() {
		try {
			
			return INPUT;
		} catch (Exception exception) {
			logger.error("Exception : ", exception);
			return ERROR;
		}
	}
	
}
