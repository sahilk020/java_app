package com.pay10.crm.action;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.pay10.commons.dao.TransactionSearchServiceMongo;
import com.pay10.commons.exception.ErrorType;
import com.pay10.commons.user.User;
import com.pay10.commons.util.Constants;
import com.pay10.commons.util.CrmFieldType;
import com.pay10.commons.util.CrmValidator;

/**
 * @author Rajendra
 *
 */

public class TotalRefundByOrderIdAction  extends AbstractSecureAction {

	private static final long serialVersionUID = 2845115077638291715L;
	private User sessionUser = new User();
	private static Logger logger = LoggerFactory.getLogger(TotalRefundByOrderIdAction.class.getName());

	private String orderId;
	private String totalRefund;
	@Autowired
	private TransactionSearchServiceMongo transactionSearchServiceMongo;

	private String responseMsg;
	
	@Override
	public String execute() {
		sessionUser = (User) sessionMap.get(Constants.USER.getValue());
		try {
			setTotalRefund(transactionSearchServiceMongo.getTotlaRefundByORderId(orderId));
			return SUCCESS;
		} catch (Exception exception) {
			logger.error("Exception", exception);
			return ERROR;
		}
	}

	@Override
	public void validate() {
		CrmValidator validator = new CrmValidator();

		if (validator.validateBlankField(getOrderId())) {
		} else if (!validator.validateField(CrmFieldType.ORDER_ID, getOrderId())) {
			addFieldError(CrmFieldType.ORDER_ID.getName(), ErrorType.INVALID_FIELD.getResponseMessage());
		}

	}
	
	public String getOrderId() {
		return orderId;
	}

	public void setOrderId(String orderId) {
		this.orderId = orderId;
	}

	public String getTotalRefund() {
		return totalRefund;
	}

	public void setTotalRefund(String totalRefund) {
		this.totalRefund = totalRefund;
	}
	
	public String getResponseMsg() {
		return responseMsg;
	}

	public void setResponseMsg(String responseMsg) {
		this.responseMsg = responseMsg;
	}


}
