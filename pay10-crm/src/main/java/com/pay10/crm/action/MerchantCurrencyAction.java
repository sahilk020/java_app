package com.pay10.crm.action;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.pay10.commons.exception.ErrorType;
import com.pay10.commons.user.User;
import com.pay10.commons.user.UserDao;
import com.pay10.commons.util.CrmFieldType;
import com.pay10.commons.util.CrmValidator;
import com.pay10.commons.util.Currency;

/**
 * @author shashi
 *
 */
public class MerchantCurrencyAction extends AbstractSecureAction {

	@Autowired
	private UserDao userDao;
	@Autowired
	private CrmValidator validator;

	private static Logger logger = LoggerFactory.getLogger(MerchantCurrencyAction.class.getName());
	private static final long serialVersionUID = -4779558725256308048L;
	private String payId;
	private String response;
	private Map<String, String> currencyMap;

	@Override
	public String execute() {
		try {
			User userFromDb;
			userFromDb = userDao.findPayId(payId);

			currencyMap = Currency.getSupportedCurreny(userFromDb);

			return SUCCESS;
		} catch (Exception exception) {
			logger.error("Exception ", exception);
			return ERROR;
		}
	}

	@Override
	public void validate() {
		if (validator.validateBlankField(getPayId())) {
			addFieldError(CrmFieldType.PAY_ID.getName(), validator.getResonseObject().getResponseMessage());
		} else if (!(validator.isValidEmailId(getPayId()))) {
			addFieldError(CrmFieldType.PAY_ID.getName(), ErrorType.INVALID_FIELD_VALUE.getResponseMessage());
		}
	}


	public String getResponse() {
		return response;
	}

	public void setResponse(String response) {
		this.response = response;
	}

	public Map<String, String> getCurrencyMap() {
		return currencyMap;
	}

	public void setCurrencyMap(Map<String, String> currencyMap) {
		this.currencyMap = currencyMap;
	}

	public String getPayId() {
		return payId;
	}

	public void setPayId(String payId) {
		this.payId = payId;
	}

}
