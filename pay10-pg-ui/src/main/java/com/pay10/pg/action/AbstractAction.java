package com.pay10.pg.action;

import com.opensymphony.xwork2.ActionSupport;
import com.pay10.commons.exception.SystemException;

public class AbstractAction extends ActionSupport {

	private static final long serialVersionUID = 6489976829377740447L;

	public AbstractAction() {
	}
	
	
	@Override
	public String execute() throws SystemException {
		
		return INPUT;
	}
	
}
