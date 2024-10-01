package com.pay10.crm.action;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.pay10.commons.user.User;
import com.pay10.commons.user.UserDao;
import com.pay10.commons.user.Merchants;
import com.pay10.commons.util.Constants;

public class CBJourneyReport extends AbstractSecureAction {

	private static final long serialVersionUID = -3306411281051464691L;
	private static Logger logger = LoggerFactory.getLogger(CBJourneyReport.class.getName());
	
	@Autowired
	private UserDao userDao;
	
	private User sessionUser = null;
	
	private List<Merchants> merchantList = new ArrayList<Merchants>();
	
	private String merchant;
	private String pgRefNum;
	private String dateFrom;
	private String dateTo;
	private String cbCaseID;
	
	
	
	public List<Merchants> getMerchantList() {
		return merchantList;
	}



	public void setMerchantList(List<Merchants> merchantList) {
		this.merchantList = merchantList;
	}
	
	
	
	public String getMerchant() {
		return merchant;
	}



	public void setMerchant(String merchant) {
		this.merchant = merchant;
	}



	public String getPgRefNum() {
		return pgRefNum;
	}



	public void setPgRefNum(String pgRefNum) {
		this.pgRefNum = pgRefNum;
	}



	public String getDateFrom() {
		return dateFrom;
	}



	public void setDateFrom(String dateFrom) {
		this.dateFrom = dateFrom;
	}



	public String getDateTo() {
		return dateTo;
	}



	public void setDateTo(String dateTo) {
		this.dateTo = dateTo;
	}



	public String getCbCaseID() {
		return cbCaseID;
	}



	public void setCbCaseID(String cbCaseID) {
		this.cbCaseID = cbCaseID;
	}



	@Override
	public String execute() {
		logger.info("CBJourneyReport Page Executed");
		sessionUser = (User) sessionMap.get(Constants.USER.getValue());
		setMerchantList(userDao.getMerchantList(sessionMap.get(Constants.SEGMENT.getValue()).toString(), sessionUser.getRole().getId()));
		return SUCCESS;
	}
	
	public void downloadCBJourneyReport() {
		System.out.println("merchant : "+getMerchant() +"\nPG REF NUM : "+getPgRefNum()+"\ndateFrom : "+getDateFrom()+"\n dateTo :"+getDateTo()+"\nCB Case ID : "+getCbCaseID());
	}
}
