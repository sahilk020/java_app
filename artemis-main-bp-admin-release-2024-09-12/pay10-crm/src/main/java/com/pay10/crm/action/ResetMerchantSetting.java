package com.pay10.crm.action;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.pay10.commons.dao.ResetAllMerchantMappingDao;
import com.pay10.commons.dao.RouterConfigurationDao;

public class ResetMerchantSetting extends AbstractSecureAction{

	private static final long serialVersionUID = 1L;
	private static Logger logger = LoggerFactory.getLogger(ResetMerchantSetting.class.getName());
	@Autowired
	private ResetAllMerchantMappingDao resetAllMerchantMappingDao;
	
	private String payId;
	private String merchantEmail;
	private String message;

	public String execute() {
		
		logger.info("ResetMerchantSetting PayId and Email : " + payId + "\t" + merchantEmail);
		String status="Operation Failed";
				
		status=resetAllMerchantMappingDao.resetMethodExecutor(payId,merchantEmail);

		setMessage(status);
		
		return SUCCESS;
	}

	public String getPayId() {
		return payId;
	}

	public void setPayId(String payId) {
		this.payId = payId;
	}

	public String getMerchantEmail() {
		return merchantEmail;
	}

	public void setMerchantEmail(String merchantEmail) {
		this.merchantEmail = merchantEmail;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

}
