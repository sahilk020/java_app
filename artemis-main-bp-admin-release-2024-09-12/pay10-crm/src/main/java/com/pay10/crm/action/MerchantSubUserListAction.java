package com.pay10.crm.action;

import java.util.List;

import org.slf4j.Logger; import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.pay10.commons.dao.MerchantGridViewService;
import com.pay10.commons.user.MerchantDetails;
import com.pay10.commons.user.User;
import com.pay10.commons.user.UserDao;
import com.pay10.commons.user.UserType;
import com.pay10.commons.util.Constants;
import com.pay10.commons.util.CrmFieldType;
import com.pay10.commons.util.CrmValidator;
import com.pay10.commons.util.DataEncoder;

public class MerchantSubUserListAction extends AbstractSecureAction {

	@Autowired
	private DataEncoder encoder;

	@Autowired
	private MerchantGridViewService merchantGridViewService;

	/**
	 * Neeraj
	 */
	@Autowired
	private CrmValidator validator;

	private static final long serialVersionUID = -3220279696930666685L;
	private static Logger logger = LoggerFactory.getLogger(MerchantSubUserListAction.class.getName());
	private List<MerchantDetails> aaData;
	private User sessionUser = new User();
	private String emailId;
	@Autowired
	private UserDao userDao ;

	@Override
	public String execute() {
		sessionUser = (User) sessionMap.get(Constants.USER.getValue());
		try {
			if (sessionUser.getUserType().equals(UserType.ADMIN)
					|| sessionUser.getUserType().equals(UserType.SUBADMIN)) {
				if (emailId.equals("ALL")) {
					aaData = encoder.encodeMerchantDetailsObj(merchantGridViewService.getAllMerchantSubUser());
				} else {
					aaData = encoder
							.encodeMerchantDetailsObj(merchantGridViewService.getAllMerchantSubUserList(getEmailId()));
				}
			}

			return SUCCESS;
		} catch (Exception exception) {
			logger.error("Exception", exception);
			return ERROR;
		}
	}

	@Override
	public void validate() {
		if ((validator.validateBlankField(getEmailId()))) {
			/*
			 * addFieldError(CrmFieldType.PAY_ID.getName(), validator
			 * .getResonseObject().getResponseMessage());
			 */
		} else if (!(validator.validateField(CrmFieldType.EMAILID, getEmailId()))) {
			if (getEmailId().equals("ALL")) {

			} else {
				addFieldError(CrmFieldType.EMAILID.getName(), validator.getResonseObject().getResponseMessage());
			}
		}

	}

	
	public List<MerchantDetails> getaaData() {
		return aaData;
	}

	public void setaaData(List<MerchantDetails> setaaData) {
		this.aaData = setaaData;
	}

	public String getEmailId() {
		return emailId;
	}

	public void setEmailId(String emailId) {
		this.emailId = emailId;
	}
}
