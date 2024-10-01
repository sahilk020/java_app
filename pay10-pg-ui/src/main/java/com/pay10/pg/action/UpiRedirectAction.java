package com.pay10.pg.action;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.opensymphony.xwork2.Action;
import com.pay10.commons.exception.ErrorType;
import com.pay10.commons.exception.SystemException;
import com.pay10.commons.util.AcquirerType;
import com.pay10.commons.util.Constants;
import com.pay10.commons.util.FieldType;
import com.pay10.commons.util.Fields;
import com.pay10.commons.util.PropertiesManager;
import com.pay10.pg.core.fraudPrevention.core.FraudRuleImplementor;
import com.pay10.pg.core.util.RequestCreator;
import com.pay10.pg.core.util.ResponseCreator;

public class UpiRedirectAction extends AbstractSecureAction {

	private static final long serialVersionUID = -862716535985091306L;
	private static Logger logger = LoggerFactory.getLogger(UpiRedirectAction.class.getName());

	@Autowired
	private RequestCreator requestCreator;

	@Autowired
	private FraudRuleImplementor fraudRuleImplementor;

	@Autowired
	private ResponseCreator responseCreator;

	private String acquirerFlag;

	@Autowired
	private PropertiesManager propertiesManager;

	@Override
	public String execute() throws SystemException {

		try {

			Fields fields = (Fields) sessionMap.get(Constants.FIELDS.getValue());
			logger.info("Fields Values In UpiRedirectAction :: "+fields.getFieldsAsString());
			if (fields.get(FieldType.RESPONSE_CODE.getName())
					.equalsIgnoreCase(ErrorType.DENIED_BY_FRAUD.getCode())) {
				
				responseCreator.ResponsePost(fields);
				sessionMap.invalidate();
				return Action.NONE;
			}
			
			if (fields.get(FieldType.RESPONSE_CODE.getName())
					.equalsIgnoreCase(ErrorType.TDR_NOT_SET_FOR_THIS_AMOUNT.getResponseCode())) {		
				responseCreator.ResponsePost(fields);
				sessionMap.invalidate();
				return Action.NONE;
			}

			logger.info("<<<<<<<<<<<<<<<<<<<<<<< fields >>>>>>>>>>>>>>>>>>>>>>>"+fields.getFieldsAsString());
			
			if (fields.get(FieldType.RESPONSE_CODE.getName())
					.equalsIgnoreCase(ErrorType.DENIED_BY_FRAUD.getCode())) {
				
				responseCreator.ResponsePost(fields);
				sessionMap.invalidate();
				return Action.NONE;
			}


			if (fields.get(FieldType.RESPONSE_CODE.getName())
					.equalsIgnoreCase(ErrorType.ACQUIRER_NOT_FOUND.getCode())) {

				responseCreator.ResponsePost(fields);
				sessionMap.invalidate();
				return Action.NONE;
			}
			
			fields.put("NEED_TO_STORE_FRAUD", "N");
			fraudRuleImplementor.applyRule(fields);

			String acquirer = fields.get(FieldType.ACQUIRER_TYPE.getName());

			if (acquirer.equalsIgnoreCase(AcquirerType.BOB.getCode())) {
				requestCreator.generateBobRequest(fields);
			} else if (acquirer.equalsIgnoreCase(AcquirerType.PAYU.getCode())) {
				requestCreator.generatePayuRequest(fields);
			} else if (acquirer.equalsIgnoreCase(AcquirerType.ATOM.getCode())) {
				requestCreator.generateAtomRequest(fields);
			}else if (acquirer.equalsIgnoreCase(AcquirerType.PAYMENTAGE.getCode())) {
				requestCreator.generatePaymentEdgeRequest(fields);
			} else if (acquirer.equalsIgnoreCase(AcquirerType.CASHFREE.getCode())) {
				requestCreator.generateCashfreeRequest(fields);
			}
			else if (acquirer.equalsIgnoreCase(AcquirerType.PAY10.getCode())) {
				requestCreator.generatePay10Request(fields);
			} else if (acquirer.equalsIgnoreCase(AcquirerType.DEMO.getCode())) {
				requestCreator.generateDemoRequest(fields);
			} else if (acquirer.equalsIgnoreCase(AcquirerType.EASEBUZZ.getCode())) {
				requestCreator.generateEasebuzzRequest(fields);
			} else if (acquirer.equalsIgnoreCase(AcquirerType.AGREEPAY.getCode())) {
				requestCreator.generateAgreepayRequest(fields);
			} else if (acquirer.equalsIgnoreCase(AcquirerType.PINELABS.getCode())) {
				requestCreator.generatePinelabsRequest(fields);
			} else if (acquirer.equalsIgnoreCase(AcquirerType.ISGPAY.getCode())) {
				requestCreator.generateIsgpayRequest(fields);
			}else if (acquirer.equalsIgnoreCase(AcquirerType.CAMSPAY.getCode())) {
				requestCreator.generateCamsPayRequest(fields);
			}else if (acquirer.equalsIgnoreCase(AcquirerType.HTPAY.getCode())) {
				requestCreator.generateHtpayRequest(fields);
			}
			return getAcquirerFlag();
		} catch (SystemException systemException) {
			logger.error("Exception", systemException);
			Fields fields = (Fields) sessionMap.get(Constants.FIELDS.getValue());

			if (null == fields) {
				return "invalidRequest";
			}

			String salt = propertiesManager.getSalt(fields.get(FieldType.PAY_ID.getName()));

			if (null == salt) {
				return "invalidRequest";
			}
			fields.put(FieldType.RESPONSE_CODE.getName(), systemException.getErrorType().getCode());
			fields.put(FieldType.RESPONSE_MESSAGE.getName(), systemException.getErrorType().getResponseMessage());
			responseCreator.ResponsePost(fields);
			sessionMap.invalidate();
			return Action.NONE;
		} catch (Exception e) {
			logger.error("Exception", e);
			return ERROR;
		}

	}

	public String getAcquirerFlag() {
		return acquirerFlag;
	}

	public void setAcquirerFlag(String acquirerFlag) {
		this.acquirerFlag = acquirerFlag;
	}

}
