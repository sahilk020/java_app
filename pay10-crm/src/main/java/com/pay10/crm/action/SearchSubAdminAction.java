package com.pay10.crm.action;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger; import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.pay10.commons.dao.SearchUserService;
import com.pay10.commons.exception.ErrorType;
import com.pay10.commons.user.User;
import com.pay10.commons.user.UserType;
import com.pay10.commons.util.Constants;
import com.pay10.commons.util.CrmFieldType;
import com.pay10.commons.util.CrmValidator;
import com.pay10.commons.util.DataEncoder;
import com.pay10.commons.util.SubAdmin;

public class SearchSubAdminAction extends AbstractSecureAction {

	/**
	 * @author Neeraj
	 */
	private static final long serialVersionUID = 5929390136391392637L;
	private static Logger logger = LoggerFactory.getLogger(SearchSubAdminAction.class.getName());
	private List<SubAdmin> aaData = new ArrayList<SubAdmin>();
	private String emailId;
	private String phoneNo;

	@Autowired
	private SearchUserService searchUserService;
	
	@Override
	public String execute() {
		User sessionUser = (User) sessionMap.get(Constants.USER.getValue());
		DataEncoder encoder = new DataEncoder();
		try {
			// TODO resolve error
			if ((sessionUser.getUserType().equals(UserType.ADMIN)) || (sessionUser.getUserType().equals(UserType.SUBADMIN))) {
				setAaData(encoder.encodeAgentsObj(searchUserService.getAgentsList(sessionUser.getPayId())));

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

		if (validator.validateBlankField(getPhoneNo())) {
		} else if (!validator.validateField(CrmFieldType.MOBILE, getPhoneNo())) {
			addFieldError("phoneNo", ErrorType.INVALID_FIELD.getResponseMessage());
		}
	}

	public List<SubAdmin> getAaData() {
		return aaData;
	}

	public void setAaData(List<SubAdmin> aaData) {
		this.aaData = aaData;
	}

	public String getEmailId() {
		return emailId;
	}

	public void setEmailId(String emailId) {
		this.emailId = emailId;
	}

	public String getPhoneNo() {
		return phoneNo;
	}

	public void setPhoneNo(String phoneNo) {
		this.phoneNo = phoneNo;
	}

}
