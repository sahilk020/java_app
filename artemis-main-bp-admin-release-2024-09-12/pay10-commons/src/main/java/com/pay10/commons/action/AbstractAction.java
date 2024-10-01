package com.pay10.commons.action;

import com.opensymphony.xwork2.ActionSupport;

public class AbstractAction extends ActionSupport {

	private static final long serialVersionUID = 6489976829377740447L;

	public AbstractAction() {
	}
	
	
	@Override
	public String execute()  {
		
		return INPUT;
	}
}
