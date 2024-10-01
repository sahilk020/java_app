package com.pay10.crm.action;

public class ChargebackStatusUpdate extends AbstractSecureAction {

	private static final long serialVersionUID = -2211460869118319593L;

	@Override
	public String execute() {
		System.out.println("ChargebackStatusUpdate");
		return SUCCESS;
	}
}
