
package com.pay10.crm.action;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pay10.commons.exception.ErrorType;
import com.pay10.commons.util.CrmFieldType;
import com.pay10.commons.util.CrmValidator;
/**
 * @author Rahul
 *
 */
public class ChangeAdminDisplayAction extends AbstractSecureAction{

	private static final long serialVersionUID = 3702199787764289745L;
	private static Logger logger = LoggerFactory.getLogger(ChangeAdminDisplayAction.class.getName());
	
	private String firstName;
	private String lastName;
	private String emailId;
	private String adminEmailId;
	private String response;
	
	@Override
	public String execute() {
		
		if(firstName != null && lastName != null && emailId != null){
			return SUCCESS;
		} else {
			return ERROR;
		}
	}
	
	@Override
	public void validate() {
		CrmValidator validator = new CrmValidator();
		if (validator.validateBlankField(getEmailId())) {
			addFieldError(CrmFieldType.EMAILID.getName(), validator.getResonseObject().getResponseMessage());
		} else if (!(validator.isValidEmailId(getEmailId()))) {
			addFieldError(CrmFieldType.EMAILID.getName(), ErrorType.INVALID_FIELD_VALUE.getResponseMessage());
		}
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
		
	}

	public String getEmailId() {
		return emailId;
	}

	public void setEmailId(String emailId) {
		this.emailId = emailId;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getAdminEmailId() {
		return adminEmailId;
	}

	public void setAdminEmailId(String adminEmailId) {
		this.adminEmailId = adminEmailId;
	}

	public String getResponse() {
		return response;
	}

	public void setResponse(String response) {
		this.response = response;
	}

}
