package com.pay10.crm.action;


import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.pay10.commons.dao.BankListDao;
import com.pay10.commons.entity.BankList;

public class AcquirerNodalMapping extends AbstractSecureAction {

	private static final long serialVersionUID = -3306411281051464691L;
	private static Logger logger = LoggerFactory.getLogger(AcquirerNodalMapping.class.getName());
	
	@Autowired
	private BankListDao bankListDao;
	
	List<BankList>bankLists=new ArrayList<>();
	
	@Override
	public String execute() {
		logger.info("AcquirerNodalMapping Page Executed");
		setBankLists(bankListDao.getAllBankList());
		return SUCCESS;
	}

	public List<BankList> getBankLists() {
		return bankLists;
	}

	public void setBankLists(List<BankList> bankLists) {
		this.bankLists = bankLists;
	}
	
	
}
