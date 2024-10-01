package com.pay10.crm.action;




import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.opensymphony.xwork2.ModelDriven;
import com.pay10.commons.audittrail.ActionMessageByAction;
import com.pay10.commons.audittrail.AuditTrail;
import com.pay10.commons.audittrail.AuditTrailService;
import com.pay10.commons.dao.ChargingDetailsFactory;
import com.pay10.commons.exception.ErrorType;
import com.pay10.commons.user.ChargingDetails;
import com.pay10.commons.user.ChargingDetailsDao;
import com.pay10.commons.user.TdrSetting;
import com.pay10.commons.user.TdrSettingDao;
import com.pay10.commons.user.User;
import com.pay10.commons.user.UserDao;
import com.pay10.commons.util.Constants;
import com.pay10.commons.util.CrmFieldType;
import com.pay10.commons.util.CrmValidator;
import com.pay10.crm.actionBeans.ChargingDetailsMaintainer;

/**
 * @author Puneet
 *
 */
public class TdrSettingEditActionAll extends AbstractSecureAction {

	

	@Autowired
	private ChargingDetailsMaintainer editChargingDetails;



	

	@Autowired
	private TdrSettingDao tdrSettingDao;
	
	@Autowired
	private TdrSettingDao dao;

	@Autowired
	private AuditTrailService auditTrailService;

	private static Logger logger = LoggerFactory.getLogger(TdrSettingEditActionAll.class.getName());
	private static final long serialVersionUID = -6517340843571949786L;

	
	private TdrSetting tdr;
	private String emailId;
	private String acquirer;
	private String response;
	private String userType;
	private String loginUserEmailId;
	private String paymentRegion;
	private String cardHolderType;
	private User sessionUser = null;
	private String responseMessage;

	@SuppressWarnings("unchecked")
	@Override
	public String execute() {
		try {
			
			
			setResponseMessage(editChargingDetails.editTdrSettingAll(tdr,emailId ,userType, loginUserEmailId));
			logger.info("Response :"+getResponseMessage());
	
		} catch (Exception exception) {
			setResponse(ErrorType.CHARGINGDETAIL_NOT_SAVED.getResponseMessage());
			logger.error("Exception", exception);
			return ERROR;
		}
		return SUCCESS;
	}

	

	public String getEmailId() {
		return emailId;
	}

	public void setEmailId(String emailId) {
		this.emailId = emailId;
	}

	public String getAcquirer() {
		return acquirer;
	}

	public void setAcquirer(String acquirer) {
		this.acquirer = acquirer;
	}

	

	public String getResponse() {
		return response;
	}

	public void setResponse(String response) {
		this.response = response;
	}

	public String getUserType() {
		return userType;
	}

	public void setUserType(String userType) {
		this.userType = userType;
	}

	public String getLoginUserEmailId() {
		return loginUserEmailId;
	}

	public void setLoginUserEmailId(String loginUserEmailId) {
		this.loginUserEmailId = loginUserEmailId;
	}

	public String getPaymentRegion() {
		return paymentRegion;
	}

	public void setPaymentRegion(String paymentRegion) {
		this.paymentRegion = paymentRegion;
	}

	public String getCardHolderType() {
		return cardHolderType;
	}

	public void setCardHolderType(String cardHolderType) {
		this.cardHolderType = cardHolderType;
	}

	public TdrSetting getTdr() {
		return tdr;
	}

	public void setTdr(TdrSetting tdr) {
		this.tdr = tdr;
	}



	public String getResponseMessage() {
		return responseMessage;
	}



	public void setResponseMessage(String responseMessage) {
		this.responseMessage = responseMessage;
	}



	

	
}
