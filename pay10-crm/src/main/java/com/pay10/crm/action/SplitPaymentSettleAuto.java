package com.pay10.crm.action;

import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.pay10.commons.user.BankSettle;
import com.pay10.commons.user.BankSettleDao;
import com.pay10.commons.user.User;
import com.pay10.commons.util.Constants;

public class SplitPaymentSettleAuto extends AbstractSecureAction {

	private static final long serialVersionUID = 1L;
	private static Logger logger = LoggerFactory.getLogger(SplitPaymentSettleAuto.class.getName());

	@Autowired
	private BankSettleDao bankSettleDao;
	private BankSettle bankSettle;
	private List<BankSettle> bankSettles;
	private String msg;
	
	
	public String save() {
		User sessionUser=(User) sessionMap.get(Constants.USER.getValue());
		bankSettle.setCreatedBy(sessionUser.getEmailId());
		bankSettle.setCreatedDate(new Date());
		logger.info("SessionUser : " + sessionUser.getEmailId());
		logger.info("BankSettle : " + bankSettle);
		String msg=bankSettleDao.insert(bankSettle);
		logger.info("SplitPaymentSettleAuto Save : " + msg);
		setMsg(msg);
		return SUCCESS;
	}
	
	public String delete() {
		User sessionUser=(User) sessionMap.get(Constants.USER.getValue());
		logger.info("SessionUser : " + sessionUser.getEmailId() + " ID : " + bankSettle.getId());
		String msg=bankSettleDao.deleteBank(bankSettle.getId(), sessionUser.getEmailId());
		logger.info("SplitPaymentSettleAuto Delete : " + msg);
		setMsg(msg);
		return SUCCESS;
	}
	
	
	public String getDetailsplitPaymentSettle() {
		setBankSettles(bankSettleDao.fetchAllBankSettleDetailsCurrency(bankSettle.getEmail(),bankSettle.getBankCurrency()));
		logger.info("SplitPaymentSettleAuto List : " + getBankSettles());
		return SUCCESS;
	}
	public String fetch() {
		setBankSettles(bankSettleDao.fetchAllBankSettleDetails(bankSettle.getEmail()));
		logger.info("SplitPaymentSettleAuto List : " + getBankSettles());
		return SUCCESS;
	}
	
	public String getMsg() {
		return msg;
	}

	public void setMsg(String msg) {
		this.msg = msg;
	}

	public BankSettle getBankSettle() {
		return bankSettle;
	}

	public void setBankSettle(BankSettle bankSettle) {
		this.bankSettle = bankSettle;
	}

	public List<BankSettle> getBankSettles() {
		return bankSettles;
	}

	public void setBankSettles(List<BankSettle> bankSettles) {
		this.bankSettles = bankSettles;
	}
	
}
