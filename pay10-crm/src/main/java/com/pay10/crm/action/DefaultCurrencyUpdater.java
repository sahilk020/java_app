package com.pay10.crm.action;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.pay10.commons.audittrail.ActionMessageByAction;
import com.pay10.commons.audittrail.AuditTrail;
import com.pay10.commons.audittrail.AuditTrailService;
import com.pay10.commons.exception.ErrorType;
import com.pay10.commons.user.User;
import com.pay10.commons.user.UserDao;
import com.pay10.commons.util.Constants;
import com.pay10.commons.util.CrmFieldConstants;
import com.pay10.commons.util.CrmFieldType;
import com.pay10.commons.util.CrmValidator;

/**
 * @author SHASHI
 *
 */
public class DefaultCurrencyUpdater extends AbstractSecureAction {

	@Autowired
	private UserDao userDao;

	@Autowired
	private CrmValidator validator;

	@Autowired
	private AuditTrailService auditTrailService;

	private static Logger logger = LoggerFactory.getLogger(DefaultCurrencyUpdater.class.getName());

	private static final long serialVersionUID = 7631759858298731581L;
	private String defaultCurrency;
	private String response;

	@SuppressWarnings("unchecked")
	@Override
	public String execute() {
		User userFromDB;
		User sessionUser;
		try {
			sessionUser = (User) sessionMap.get(Constants.USER.getValue());
			userFromDB = userDao.findPayId(sessionUser.getPayId());
			String prevUser = mapper.writeValueAsString(userFromDB);
			userFromDB.setDefaultCurrency(defaultCurrency);
			userDao.update(userFromDB);

			Map<String, ActionMessageByAction> actionMessagesByAction = (Map<String, ActionMessageByAction>) sessionMap
					.get(Constants.ACTION_MESSAGE_BY_ACTION.getValue());
			AuditTrail auditTrail = new AuditTrail(mapper.writeValueAsString(userFromDB), prevUser,
					actionMessagesByAction.get("setDefaultCurrency"));
			auditTrailService.saveAudit(request, auditTrail);

			sessionMap.replace(Constants.USER.getValue(), userFromDB);
			response = CrmFieldConstants.DEFAUL_CURRENCY_UPDATE.getValue();
			return SUCCESS;
		} catch (Exception exception) {
			logger.error("DefaultCurrencyUpdater", exception);
			ErrorType.INVALID_DEFAULT_CURRENCY.getResponseMessage();
			return ERROR;
		}

	}

	@Override
	public void validate() {
		if (!validator.validateField(CrmFieldType.DEFAULT_CURRENCY, getDefaultCurrency())) {
			addFieldError(CrmFieldType.DEFAULT_CURRENCY.getName(),
					ErrorType.INTERNAL_SYSTEM_ERROR.getResponseMessage());
		}
	}

	public String getDefaultCurrency() {
		return defaultCurrency;
	}

	public void setDefaultCurrency(String defaultCurrency) {
		this.defaultCurrency = defaultCurrency;
	}

	public String getResponse() {
		return response;
	}

	public void setResponse(String response) {
		this.response = response;
	}
}
