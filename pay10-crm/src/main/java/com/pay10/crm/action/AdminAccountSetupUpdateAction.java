package com.pay10.crm.action;

import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.opensymphony.xwork2.ModelDriven;
import com.pay10.commons.exception.ErrorType;
import com.pay10.commons.user.User;
import com.pay10.commons.user.UserDao;
import com.pay10.commons.util.CrmFieldType;
import com.pay10.commons.util.CrmValidator;
import com.pay10.commons.util.UserStatusType;
import com.pay10.crm.actionBeans.AdminRecordUpdater;

/**
 * @ Neeraj , Rajendra 
 */

public class AdminAccountSetupUpdateAction extends AbstractSecureAction {


	private static final long serialVersionUID = -2804573223359698889L;
	private static Logger logger = LoggerFactory.getLogger(AdminAccountSetupUpdateAction.class.getName());
	
	@Autowired
	private UserDao userDao;
	
	@Autowired
	private CrmValidator validator;
	@Autowired
	private AdminRecordUpdater adminRecordUpdater;
	private String payId ;
	private User user = new User();
	
	private String userStatus;
	private String emailId;
	private String businessName;
	private String mobile;
	private String tenantNumber;
	private String companyName;
	
	public String updateAdminSetup(){
		User userDb = userDao.findPayIdByEmail(emailId);
		
		Date date = new Date();
		try{

			userDb.setUpdateDate(date);
			userDb.setEmailId(getEmailId());
			userDb.setMobile(getMobile());
			userDb.setBusinessName(getBusinessName());
			userDb.setCompanyName(getCompanyName());
			userDb.setTenantNumber(getTenantNumber());
			
			if(userStatus.equalsIgnoreCase("ACTIVE")) {
				userDb.setUserStatus(UserStatusType.ACTIVE);
				userDb.setActivationDate(date);
			}else if(userStatus.equalsIgnoreCase("PENDING")) {
				userDb.setUserStatus(UserStatusType.PENDING);
			}else if(userStatus.equalsIgnoreCase("SUSPENDED")) {
				userDb.setUserStatus(UserStatusType.SUSPENDED);
			}else if(userStatus.equalsIgnoreCase("TRANSACTION_BLOCKED")){
				userDb.setUserStatus(UserStatusType.TRANSACTION_BLOCKED);
			}else {
				userDb.setUserStatus(UserStatusType.TERMINATED);
			}
			
			setUser(adminRecordUpdater.updateUserProfile(userDb));
			addActionMessage("Admin Status successfully Updated.");
			return SUCCESS;
		} catch (Exception exception) {
			logger.error("Exception", exception);
			return ERROR;     
		}
		
	}
	public User getUser() {
		return user;
	}
	public void setUser(User user) {
		this.user = user;
	}

	@Override
	public void validate(){
		
		if ((validator.validateBlankField(getBusinessName()))) {
			addFieldError(CrmFieldType.BUSINESS_NAME.getName(), validator.getResonseObject().getResponseMessage());
		} else if (!(validator.validateField(CrmFieldType.BUSINESS_NAME, getBusinessName()))) {
			addFieldError(CrmFieldType.BUSINESS_NAME.getName(), validator.getResonseObject().getResponseMessage());
		}

		if (validator.validateBlankField(getMobile())) {
			addFieldError(CrmFieldType.MOBILE.getName(), validator.getResonseObject().getResponseMessage());
		} else if (!(validator.validateField(CrmFieldType.MOBILE, getMobile()))) {
			addFieldError(CrmFieldType.MOBILE.getName(), validator.getResonseObject().getResponseMessage());
		}

		if (validator.validateBlankField(getEmailId())) {
			addFieldError(CrmFieldType.EMAILID.getName(), validator.getResonseObject().getResponseMessage());
		} else if (!(validator.isValidEmailId(getEmailId()))) {
			addFieldError(CrmFieldType.EMAILID.getName(), ErrorType.INVALID_FIELD_VALUE.getResponseMessage());
		}
	}

	
	public String getPayId() {
		return payId;
	}
	public void setPayId(String payId) {
		this.payId = payId;
	}
	
	public String getUserStatus() {
		return userStatus;
	}
	public void setUserStatus(String userStatus) {
		this.userStatus = userStatus;
	}
	public String getEmailId() {
		return emailId;
	}
	public void setEmailId(String emailId) {
		this.emailId = emailId;
	}
	public String getBusinessName() {
		return businessName;
	}
	public void setBusinessName(String businessName) {
		this.businessName = businessName;
	}
	public String getMobile() {
		return mobile;
	}
	public void setMobile(String mobile) {
		this.mobile = mobile;
	}
	public String getTenantNumber() {
		return tenantNumber;
	}
	public void setTenantNumber(String tenantNumber) {
		this.tenantNumber = tenantNumber;
	}
	public String getCompanyName() {
		return companyName;
	}
	public void setCompanyName(String companyName) {
		this.companyName = companyName;
	}

	
}
