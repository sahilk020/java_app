package com.pay10.crm.action;

import com.pay10.commons.dao.RoleDao;
import com.pay10.commons.user.UserGroup;
import com.pay10.commons.user.UserType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.pay10.commons.exception.ErrorType;
import com.pay10.commons.user.User;
import com.pay10.commons.user.UserDao;
import com.pay10.commons.util.Constants;
import com.pay10.commons.util.CrmFieldConstants;
import com.pay10.commons.util.CrmFieldType;
import com.pay10.commons.util.CrmValidator;

import java.util.ArrayList;
import java.util.List;

public class AcquirerFormEditAction extends AbstractSecureAction {
	/**
	 * 
	 */
	private static final long serialVersionUID = 7242292488127283807L;

	@Autowired
	private CrmValidator validator;

	private static Logger logger = LoggerFactory.getLogger(AcquirerFormEditAction.class.getName());
	private String firstName;
	private String lastName;
	private String businessName;
	private String emailId;
	private String accountNo;

	private  int roleId;
	private String payId;

	private	List roles=new ArrayList();
	private List userGroups=new ArrayList();
	@Autowired
	private RoleDao roleDao;
	
	

	public String editAcquirer() {
		try {


			User user = new User();
			UserDao userDao = new UserDao();
			user = userDao.find(getEmailId());
			logger.info("Role Id:"+getRoleId());
			user.setFirstName(getFirstName());
			user.setLastName(getLastName());
			user.setBusinessName(getBusinessName());
		//	user.setAccountNo(getAccountNo());

			 UserGroup group=new UserGroup();
			group.setDescription(UserType.ACQUIRER.name());
			group.setGroup(UserType.ACQUIRER.name());
			group.setId(2);
		 	user.setUserGroup(group);
			logger.info("User Group{}",group);

			user.setRole(roleDao.getRole(getRoleId()));
			logger.info("Role Id: "+getRoleId());
			userDao.update(user);

			addActionMessage(CrmFieldConstants.ACQUIRER_DETAILS_UPDATED.getValue());

			//Setting parameters
			setRoles(roleDao.getRoleByEmailId(getEmailId()));

			setUserGroups(new ArrayList<>());
			setPayId(user.getPayId());
			logger.info("Saved Successfully");

			return INPUT;
		} catch (Exception exception) {
			logger.error("Exception", exception);
			return ERROR;
		}
	}

	public void validator() {
		logger.info("Validator");

		if ((validator.validateBlankField(getFirstName()))) {
			addFieldError(CrmFieldType.FIRSTNAME.getName(), validator.getResonseObject().getResponseMessage());
		} else if (!(validator.validateField(CrmFieldType.FIRSTNAME, getFirstName()))) {
			addFieldError(CrmFieldType.FIRSTNAME.getName(), validator.getResonseObject().getResponseMessage());
		}
		
		
		
		if ((validator.validateBlankField(getLastName()))) {
			addFieldError(CrmFieldType.LASTNAME.getName(), validator.getResonseObject().getResponseMessage());
		} else if (!(validator.validateField(CrmFieldType.LASTNAME, getLastName()))) {
			addFieldError(CrmFieldType.LASTNAME.getName(), validator.getResonseObject().getResponseMessage());
		}
		
		
		
		if ((validator.validateBlankField(getBusinessName()))) {
			addFieldError(CrmFieldType.BUSINESS_NAME.getName(), validator.getResonseObject().getResponseMessage());
		} else if (!(validator.validateField(CrmFieldType.BUSINESS_NAME, getBusinessName()))) {
			addFieldError(CrmFieldType.BUSINESS_NAME.getName(), validator.getResonseObject().getResponseMessage());
		}
		
		
		
		if ((validator.validateBlankField(getEmailId()))) {
			addFieldError(CrmFieldType.EMAILID.getName(), validator.getResonseObject().getResponseMessage());
		} else if (!(validator.validateField(CrmFieldType.EMAILID, getEmailId()))) {
			addFieldError(CrmFieldType.EMAILID.getName(), validator.getResonseObject().getResponseMessage());
		}
		

	}



	@Override
	public void validate() {
		logger.info("Validate");


		User sessionUser = (User) sessionMap.get(Constants.USER.getValue());
		CrmValidator validator = new CrmValidator();

		if ((validator.validateBlankField(getFirstName()))) {
			addFieldError(CrmFieldType.FIRSTNAME.getName(), validator.getResonseObject().getResponseMessage());
		} else if (!(validator.validateField(CrmFieldType.FIRSTNAME, getFirstName()))) {
			addFieldError(CrmFieldType.FIRSTNAME.getName(), validator.getResonseObject().getResponseMessage());
		}

		if ((validator.validateBlankField(getLastName()))) {
			addFieldError(CrmFieldType.LASTNAME.getName(), validator.getResonseObject().getResponseMessage());
		} else if (!(validator.validateField(CrmFieldType.LASTNAME, getLastName()))) {
			addFieldError(CrmFieldType.LASTNAME.getName(), validator.getResonseObject().getResponseMessage());
		}
		
		
		if ((validator.validateBlankField(getBusinessName()))) {
			addFieldError(CrmFieldType.BUSINESS_NAME.getName(), validator.getResonseObject().getResponseMessage());
		} else if (!(validator.validateField(CrmFieldType.BUSINESS_NAME, getBusinessName()))) {
			addFieldError(CrmFieldType.BUSINESS_NAME.getName(), validator.getResonseObject().getResponseMessage());
		}
		

		if (validator.validateBlankField(getEmailId())) {
			logger.info("Email Validation: ");
			addFieldError(CrmFieldType.EMAILID.getName(), validator.getResonseObject().getResponseMessage());
		} else if (!(validator.isValidEmailId(getEmailId()))) {
			addFieldError(CrmFieldType.EMAILID.getName(), ErrorType.INVALID_FIELD_VALUE.getResponseMessage());
		}
		
	
		
	}

	
	
	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public String getBusinessName() {
		return businessName;
	}

	public void setBusinessName(String businessName) {
		this.businessName = businessName;
	}

	public String getEmailId() {
		return emailId;
	}

	public void setEmailId(String emailId) {
		this.emailId = emailId;
	}

	public String getAccountNo() {
		return accountNo;
	}

	public void setAccountNo(String accountNo) {
		this.accountNo = accountNo;
	}

	public int getRoleId() {
		return roleId;
	}

	public void setRoleId(int roleId) {
		this.roleId = roleId;
	}


	public List getRoles() {
		return roles;
	}

	public void setRoles(List roles) {
		this.roles = roles;
	}

	public List getUserGroups() {
		return userGroups;
	}

	public void setUserGroups(List userGroups) {
		this.userGroups = userGroups;
	}

	public String getPayId() {
		return payId;
	}

	public void setPayId(String payId) {
		this.payId = payId;
	}
}

