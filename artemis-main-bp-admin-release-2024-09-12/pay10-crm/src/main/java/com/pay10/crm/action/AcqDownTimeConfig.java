package com.pay10.crm.action;

import org.springframework.beans.factory.annotation.Autowired;

import com.pay10.commons.user.AcquirerSchadulardao;

public class AcqDownTimeConfig extends AbstractSecureAction{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Autowired
	AcquirerSchadulardao acquirerSchadulardao;
	
	public String execute() {		
		return INPUT;
	}
	
	
	
	
	

	
}
