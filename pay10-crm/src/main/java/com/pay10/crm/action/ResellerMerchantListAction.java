package com.pay10.crm.action;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.pay10.commons.exception.ErrorType;
import com.pay10.commons.user.Merchants;
import com.pay10.commons.user.UserDao;
import com.pay10.commons.util.CrmFieldConstants;
import com.pay10.commons.util.CrmFieldType;
import com.pay10.commons.util.CrmValidator;

public class ResellerMerchantListAction extends AbstractSecureAction {
	@Autowired
	private CrmValidator validator;

	@Autowired
	private UserDao userDao;

	private static Logger logger = LoggerFactory.getLogger(ResellerMerchantListAction.class.getName());

	private static final long serialVersionUID = -6919220389124792416L;

	private String resellerId;
	private String payId;

	@SuppressWarnings("unchecked")
	public String execute() {
		logger.info("Inside ResellerMerchantListAction , execute()");

		try {
			List<Merchants> merchList = new ArrayList<Merchants>();

			if (resellerId.equalsIgnoreCase("All")) {
				merchList = userDao.getResellerMerchants();
			} else {
				merchList = userDao.getResellerByResellerId(resellerId);

			}
			setAaData(merchList);
			Collections.sort(getAaData());
			return SUCCESS;
		} catch (Exception e) {
			logger.info("Exception Caught " + e);
		}
		return SUCCESS;
	}

	public void validate() {

		if ((validator.validateBlankField(getPayId()))
				|| (getPayId().equalsIgnoreCase(CrmFieldConstants.ALL.getValue()))) {
		} else if (!(validator.validateField(CrmFieldType.PAY_ID, getPayId()))) {
			addFieldError(CrmFieldType.PAY_ID.getName(), ErrorType.INVALID_FIELD_VALUE.getResponseMessage());
		}

	}

	private List<Merchants> aaData;

	public String getPayId() {
		return payId;
	}

	public void setPayId(String payId) {
		this.payId = payId;
	}

	public String getResellerId() {
		return resellerId;
	}

	public void setResellerId(String resellerId) {
		this.resellerId = resellerId;
	}

	@SuppressWarnings("rawtypes")
	public List getAaData() {
		return aaData;
	}

	public void setAaData(List<Merchants> aaData) {
		this.aaData = aaData;
	}

}
