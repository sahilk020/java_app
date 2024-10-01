package com.pay10.crm.action;

import com.opensymphony.xwork2.ModelDriven;
import com.pay10.commons.user.TransactionSearch;

public class TransactionSearchRedirect extends AbstractSecureAction implements
		ModelDriven<TransactionSearch> {

	private static final long serialVersionUID = 5763627063996812999L;
	private TransactionSearch transactionSearch = new TransactionSearch();

	@Override
	public String execute() {
		return SUCCESS;
	}

	@Override
	public TransactionSearch getModel() {
		return transactionSearch;
	}
}
