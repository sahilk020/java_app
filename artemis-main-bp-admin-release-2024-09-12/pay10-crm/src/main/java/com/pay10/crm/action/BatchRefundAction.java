package com.pay10.crm.action;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.apache.struts2.ServletActionContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.pay10.commons.exception.ErrorType;
import com.pay10.commons.exception.SystemException;
import com.pay10.commons.user.BatchTransactionObj;
import com.pay10.commons.user.User;
import com.pay10.commons.util.Constants;
import com.pay10.commons.util.CrmFieldConstants;
import com.pay10.commons.util.CrmFieldType;
import com.pay10.commons.util.CrmValidator;
import com.pay10.crm.actionBeans.RefundProcessor;

public class BatchRefundAction extends AbstractSecureAction {
	
	@Autowired
	private RefundProcessor refundProcessor;

	@Autowired
	private CrmValidator validator;
	
	private static final long serialVersionUID = -4502244100134090359L;
	private static Logger logger = LoggerFactory.getLogger(BatchRefundAction.class.getName());
	private List<BatchTransactionObj> refundList = new ArrayList<BatchTransactionObj>();
	private String response;

	@Override
	public String execute() {
		try {
			HttpServletRequest request = ServletActionContext.getRequest();
			User sessionUser=(User) sessionMap.get(Constants.USER.getValue());
			response = refundProcessor.processAll(refundList, sessionUser, request.getRemoteAddr());
			if(StringUtils.isEmpty(response)){
				setResponse(CrmFieldConstants.PROCESS_INITIATED_SUCCESSFULLY.getValue());
			}
		}
		catch (SystemException systemException) {
			logger.error("Error while processing batch refund:" + systemException);
			setResponse(systemException.getMessage());
		}catch (Exception exception) {
			logger.error("Error while processing batch refund:" + exception);
			setResponse(ErrorType.REFUND_NOT_SUCCESSFULL.getResponseMessage());
		}
		refundList.clear();
		return SUCCESS;
	}
	@Override
	public void validate(){
		if ((validator.validateBlankField(getResponse()))) {
			addFieldError(CrmFieldType.RESPONSE.getName(), validator.getResonseObject().getResponseMessage());
		} else if (!(validator.validateField(CrmFieldType.RESPONSE, getResponse()))) {
			addFieldError(CrmFieldType.RESPONSE.getName(), validator.getResonseObject().getResponseMessage());
		}
	}
	

	public String getResponse() {
		return response;
	}

	public void setResponse(String response) {
		this.response = response;
	}

	public List<BatchTransactionObj> getRefundList() {
		return refundList;
	}

	public void setRefundList(List<BatchTransactionObj> refundList) {
		this.refundList = refundList;
	}
}

