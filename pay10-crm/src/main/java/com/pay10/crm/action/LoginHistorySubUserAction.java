package com.pay10.crm.action;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger; import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.pay10.commons.exception.ErrorType;
import com.pay10.commons.user.LoginHistory;
import com.pay10.commons.user.LoginHistoryDao;
import com.pay10.commons.user.User;
import com.pay10.commons.user.UserType;
import com.pay10.commons.util.Constants;
import com.pay10.commons.util.CrmFieldConstants;
import com.pay10.commons.util.CrmFieldType;
import com.pay10.commons.util.CrmValidator;
import com.pay10.commons.util.DataEncoder;

public class LoginHistorySubUserAction extends AbstractSecureAction {

	
	@Autowired
	LoginHistoryDao loginHistoryDao;
	
	@Autowired
	DataEncoder encoder;
	
	@Autowired
	private CrmValidator validator;
	
	private static Logger logger = LoggerFactory.getLogger(LoginHistorySubUserAction.class.getName());
	private static final long serialVersionUID = -6336397235790682930L;
	
	private List<LoginHistory> aaData;
	private String emailId;

	@Override
	public String execute() {
		try {
			aaData=new ArrayList<LoginHistory>();
			User user = (User) sessionMap.get(Constants.USER);
			logger.info("User DTO{}",user);
			logger.info("Email Id "+user.getEmailId());
			logger.info("User Type "+user.getUserType());
			if (user.getUserType() == UserType.MERCHANT) {
				if( (getEmailId().equals(CrmFieldConstants.ALL_USERS.getValue()))) {
					aaData = encoder.encodeLoginHistoryObj(loginHistoryDao.findAllUsers(user.getPayId()));
				}
				else {
//					aaData=new ArrayList<LoginHistory>();
					aaData =encoder.encodeLoginHistoryObj(loginHistoryDao
							.findLoginHisAllSubUser(getEmailId(), user.getPayId()));
				}
			} 
			return SUCCESS;
		} catch (Exception exception) {
			logger.error("Exception", exception);
			return ERROR;
		}
	}

	@Override
	public void validate(){

		if(validator.validateBlankField(getEmailId()) || getEmailId().equals(CrmFieldConstants.ALL_MERCHANTS.getValue()) || getEmailId().equals(CrmFieldConstants.ALL_USERS.getValue())){
		}
        else if(!validator.validateField(CrmFieldType.EMAILID, getEmailId())){
        	addFieldError(CrmFieldType. EMAILID.getName(), ErrorType.INVALID_FIELD.getResponseMessage());
		}
	}

	public List<LoginHistory> getAaData() {
		return aaData;
	}

	public void setAaData(List<LoginHistory> aaData) {
		this.aaData = aaData;
	}

	public String getEmailId() {
		return emailId;
	}

	public void setEmailId(String emailId) {
		this.emailId = emailId;
	}

}
