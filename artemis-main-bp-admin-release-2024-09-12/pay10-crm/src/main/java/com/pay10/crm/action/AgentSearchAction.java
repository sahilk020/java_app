package com.pay10.crm.action;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.pay10.commons.exception.ErrorType;
import com.pay10.commons.user.SearchTransaction;
import com.pay10.commons.user.User;
import com.pay10.commons.user.UserType;
import com.pay10.commons.util.Constants;
import com.pay10.commons.util.CrmFieldType;
import com.pay10.commons.util.CrmValidator;
import com.pay10.commons.util.DataEncoder;
import com.pay10.crm.mongoReports.SearchTransactionReport;

public class AgentSearchAction extends AbstractSecureAction {

	private static final long serialVersionUID = -5956533558995482980L;
	private static Logger logger = LoggerFactory.getLogger(AgentSearchAction.class.getName());

	@Autowired
	private CrmValidator validator;

	@Autowired
	private DataEncoder encoder;

	@Autowired
	private SearchTransactionReport searchTransactionReport;

	private String orderId;
	private String pgRefNum;
	private String acquirerId;
	private String customerEmail;
	private String customerPhone;

	private List<SearchTransaction> aaData;
	private User sessionUser = new User();

	@Override
	public String execute() {
		sessionUser = (User) sessionMap.get(Constants.USER.getValue());
		try {

			aaData = encoder.encodeSearchTransactionObj(searchTransactionReport.searchPaymentForAgent(getOrderId(),
					getPgRefNum(), getAcquirerId(), getCustomerEmail(), getCustomerPhone(), sessionUser));

			return SUCCESS;

		} catch (Exception exception) {
			logger.error("Exception", exception);
			return ERROR;
		}
	}

	public String custom() {
		sessionUser = (User) sessionMap.get(Constants.USER.getValue());
		try {

			if (sessionUser.getUserType().equals(UserType.ADMIN)
					|| sessionUser.getUserType().equals(UserType.SUBADMIN)) {
				aaData = encoder
						.encodeSearchTransactionObj(searchTransactionReport.searchPaymentForAgentCustom(getOrderId(),
								getPgRefNum(), getAcquirerId(), getCustomerEmail(), getCustomerPhone(), sessionUser));
			}

			return SUCCESS;

		} catch (Exception exception) {
			logger.error("Exception", exception);
			return ERROR;
		}
	}

	@Override
	public void validate() {

		if (validator.validateBlankField(getOrderId())) {
		} else if (!validator.validateField(CrmFieldType.ORDER_ID, getOrderId())) {
			addFieldError(CrmFieldType.ORDER_ID.getName(), ErrorType.INVALID_FIELD.getResponseMessage());
		}
		if (validator.validateBlankField(getPgRefNum())) {
		} else if (!validator.validateField(CrmFieldType.PG_REF_NUM, getPgRefNum())) {
			addFieldError(CrmFieldType.PG_REF_NUM.getName(), ErrorType.INVALID_FIELD.getResponseMessage());
		}
		if (validator.validateBlankField(getAcquirerId())) {
		} else if (!validator.validateField(CrmFieldType.ACQUIRER_ID, getAcquirerId())) {
			addFieldError(CrmFieldType.ACQUIRER_ID.getName(), ErrorType.INVALID_FIELD.getResponseMessage());
		}

		if (validator.validateBlankField(getCustomerEmail())) {
		} else if (!validator.validateField(CrmFieldType.CUSTOMER_EMAIL_ID, getCustomerEmail())) {
			addFieldError(CrmFieldType.CUSTOMER_EMAIL_ID.getName(), ErrorType.INVALID_FIELD.getResponseMessage());
		}

		if (validator.validateBlankField(getCustomerPhone())) {
		} else if (!validator.validateField(CrmFieldType.MOBILE, getCustomerPhone())) {
			addFieldError(CrmFieldType.MOBILE.getName(), ErrorType.INVALID_FIELD.getResponseMessage());
		}
	}

	public List<SearchTransaction> getaaData() {
		return aaData;
	}

	public void setaaData(List<SearchTransaction> setaaData) {
		this.aaData = setaaData;
	}

	public String getOrderId() {
		return orderId;
	}

	public void setOrderId(String orderId) {
		this.orderId = orderId;
	}

	public String getPgRefNum() {
		return pgRefNum;
	}

	public void setPgRefNum(String pgRefNum) {
		this.pgRefNum = pgRefNum;
	}

	public String getAcquirerId() {
		return acquirerId;
	}

	public void setAcquirerId(String acquirerId) {
		this.acquirerId = acquirerId;
	}

	public String getCustomerEmail() {
		return customerEmail;
	}

	public void setCustomerEmail(String customerEmail) {
		this.customerEmail = customerEmail;
	}

	public String getCustomerPhone() {
		return customerPhone;
	}

	public void setCustomerPhone(String customerPhone) {
		this.customerPhone = customerPhone;
	}

}
