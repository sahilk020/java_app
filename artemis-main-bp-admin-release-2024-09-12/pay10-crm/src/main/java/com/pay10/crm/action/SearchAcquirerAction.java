package com.pay10.crm.action;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger; import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.pay10.commons.dao.SearchUserService;
import com.pay10.commons.exception.ErrorType;
import com.pay10.commons.user.User;
import com.pay10.commons.user.UserType;
import com.pay10.commons.util.Acquirer;
import com.pay10.commons.util.Constants;
import com.pay10.commons.util.CrmFieldType;
import com.pay10.commons.util.CrmValidator;
import com.pay10.commons.util.DataEncoder;

public class SearchAcquirerAction extends AbstractSecureAction {

	private static final long serialVersionUID = -7244483774691115085L;
	private static Logger logger = LoggerFactory.getLogger(SearchAcquirerAction.class.getName());
	private List<Acquirer> aaData = new ArrayList<Acquirer>();
	private String emailId;
	private String businessName;

	@Autowired
	private SearchUserService searchUserService;
	
	@Override
	public String execute() {
		logger.info("Inside SearchAcquirerAction");
		User sessionUser = (User) sessionMap.get(Constants.USER.getValue());
		DataEncoder encoder = new DataEncoder();
		try {
			if ((sessionUser.getUserType().equals(UserType.ADMIN)) || (sessionUser.getUserType().equals(UserType.SUBADMIN))) {
				setAaData(encoder.encodeAcquirersObj(searchUserService.getAcquirersList(sessionUser.getPayId())));

			}

		} catch (Exception exception) {
			logger.error("Exception", exception);
		}
		return SUCCESS;
	}

	@Override
	public void validate() {
		CrmValidator validator = new CrmValidator();

		if (validator.validateBlankField(getEmailId())) {
		} else if (!validator.validateField(CrmFieldType.EMAILID, getEmailId())) {
			addFieldError(CrmFieldType.EMAILID.getName(), ErrorType.INVALID_FIELD.getResponseMessage());
		}

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

	public List<Acquirer> getAaData() {
		return aaData;
	}

	public void setAaData(List<Acquirer> aaData) {
		this.aaData = aaData;
	}

}
