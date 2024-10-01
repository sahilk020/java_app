package com.pay10.crm.chargeback;

import org.slf4j.Logger; import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.pay10.crm.action.AbstractSecureAction;
import com.pay10.crm.chargeback.action.beans.ChargebackDao;

public class UpdateStatusByMerchant extends AbstractSecureAction {
	
	@Autowired
	private ChargebackDao chargebackDao;

	private static final long serialVersionUID = 8763650020568453333L;
	private static Logger logger = LoggerFactory.getLogger(UpdateStatusByMerchant.class.getName());
	
	private String chargebackStatus;
	private String caseId;
	@Override
	public String execute(){
		 try {
			chargebackDao.updateStatus(getChargebackStatus(),getCaseId());
		} catch (Exception exception) {
			logger.error("Exception", exception);
			return ERROR;
		}
		return SUCCESS;
		
	}
	public String getChargebackStatus() {
		return chargebackStatus;
	}
	public void setChargebackStatus(String chargebackStatus) {
		this.chargebackStatus = chargebackStatus;
	}
	public String getCaseId() {
		return caseId;
	}
	public void setCaseId(String caseId) {
		this.caseId = caseId;
	}

}
