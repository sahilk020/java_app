package com.pay10.pg.action.service;

import org.apache.commons.lang3.StringUtils;
import org.apache.struts2.dispatcher.SessionMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.pay10.commons.exception.ErrorType;
import com.pay10.commons.exception.SystemException;
import com.pay10.commons.user.User;
import com.pay10.commons.util.Constants;
import com.pay10.commons.util.FieldType;
import com.pay10.commons.util.Fields;
import com.pay10.commons.util.StatusType;

@Service
public class RetryTransactionProcessor {
	private static Logger logger = LoggerFactory.getLogger(RetryTransactionProcessor.class.getName());
	public boolean retryTransaction(Fields responseMap, SessionMap<String, Object> sessionMap, User user, Fields fields)
			throws SystemException {
		logger.info("Retry Transaction, responseMap = {} ",responseMap);
		logger.info("Retry Transaction, sessionMap = {} ",sessionMap);
		logger.info("Retry Transaction, fields = {} ",fields);
		// No retry for rejected for fraud txns
		if (routeFraudTransactions(responseMap)) {
			return false;
		}
		String respCode = responseMap.get(FieldType.RESPONSE_CODE.getName());
		//attempt retry if user flag on and txn failed
		if (user.isRetryTransactionCustomeFlag()
				&& !respCode.equals(ErrorType.SUCCESS.getCode())) {
			//Get number of retry attempts allowed and update
			String retryAttemeptCountAllowedObj = user.getAttemptTrasacation();
			if(StringUtils.isBlank(retryAttemeptCountAllowedObj)) {
				return false;
			}
			Integer retryAttemeptCountAllowed = Integer.parseInt(retryAttemeptCountAllowedObj);
			Integer retryAttemepts = (Integer) sessionMap.get(Constants.RETRY_COUNT.getValue());
			if(null==retryAttemepts) {
				retryAttemepts = 1;
				sessionMap.put(Constants.RETRY_COUNT.getValue(), retryAttemepts);
				cleanFields(fields, sessionMap);
				return true;
			}else if (retryAttemeptCountAllowed <= retryAttemepts) {
				retryAttemepts = retryAttemepts + 1;
				sessionMap.put(Constants.RETRY_COUNT.getValue(), retryAttemepts);
				cleanFields(fields, sessionMap);
				return true;
			}
		}
		return false;
	}
	
	public void cleanFields(Fields fields,	SessionMap<String, Object> sessionMap) {
		//clean Fields and put back in session
		fields.remove(FieldType.ACS_URL.getName());
		fields.remove(FieldType.CARD_MASK.getName());
		fields.remove(FieldType.MOP_TYPE.getName());
		fields.remove(FieldType.ACQUIRER_GST.getName());
		fields.remove(FieldType.CARD_HOLDER_NAME.getName());
		fields.remove(FieldType.PG_TXN_MESSAGE.getName());
		fields.remove(FieldType.PG_REF_NUM.getName());
		fields.remove(FieldType.MD.getName());
		fields.remove(FieldType.PG_TDR_SC.getName());
		fields.remove(FieldType.PAYMENT_ID.getName());
		fields.remove(FieldType.CARD_HOLDER_TYPE.getName());
		fields.remove(FieldType.INTERNAL_VALIDATE_HASH_YN.getName());
		fields.remove(FieldType.INTERNAL_CARD_ISSUER_BANK.getName());
		fields.remove(FieldType.PAYMENTS_REGION.getName());
		fields.remove(FieldType.PARES.getName());
		fields.remove(FieldType.SURCHARGE_FLAG.getName());
		fields.remove(FieldType.PAYMENT_TYPE.getName());
		fields.remove(FieldType.INTERNAL_CARD_ISSUER_COUNTRY.getName());
		fields.remove(FieldType.PG_GST.getName());
		fields.remove(FieldType.ORIG_TXN_ID.getName());
		fields.remove(FieldType.ACQUIRER_TYPE.getName());
		fields.remove(FieldType.TOTAL_AMOUNT.getName());
		fields.remove(FieldType.ACQUIRER_TDR_SC.getName());
		fields.remove(FieldType.MERCHANT_ID.getName());
		fields.remove(FieldType.PASSWORD.getName());
		fields.remove(FieldType.PASSWORD.getName());
		fields.remove(FieldType.TXN_KEY.getName());
		fields.remove(FieldType.ADF1.getName());
		fields.remove(FieldType.ADF2.getName());
		fields.remove(FieldType.ADF3.getName());
		fields.remove(FieldType.ADF4.getName());
		fields.remove(FieldType.ADF5.getName());
		fields.remove(FieldType.ADF6.getName());
		fields.remove(FieldType.ADF7.getName());
		fields.remove(FieldType.ADF8.getName());
		fields.remove(FieldType.ADF9.getName());
		fields.remove(FieldType.ADF10.getName());
		fields.remove(FieldType.ADF11.getName());
		//TODO...check to remove other fields
		fields.put(FieldType.STATUS.getName(), StatusType.PENDING.getName());
		fields.put(FieldType.IS_RETRY.getName(), Constants.Y_FLAG.getValue());
		//clean sessionMap
		sessionMap.remove(FieldType.IS_ENROLLED.getName());
		sessionMap.remove(FieldType.PAYMENTS_REGION.getName());
		sessionMap.remove(FieldType.INTERNAL_CARD_ISSUER_COUNTRY.getName());
		sessionMap.remove(Constants.BIN.getValue());
		sessionMap.remove(FieldType.CARD_EXP_DT.getName());
		sessionMap.remove(FieldType.CARD_HOLDER_TYPE.getName());
		sessionMap.remove(FieldType.ACQUIRER_TYPE.getName());
		sessionMap.remove(FieldType.CARD_NUMBER.getName());
		sessionMap.remove(FieldType.CVV.getName());
		sessionMap.remove(FieldType.INTERNAL_CARD_ISSUER_BANK.getName());
		sessionMap.put(FieldType.IS_RETRY.getName(), Constants.Y_FLAG.getValue());
		sessionMap.put(Constants.FIELDS.getValue(), fields);
	}

	// to handle txn failed due by fraud prevention system
	private boolean routeFraudTransactions(Fields fields) throws SystemException {
		if (fields.get(FieldType.STATUS.getName()).equals(StatusType.DENIED_BY_FRAUD.getName())) {
			return true;
		}
		return false;
	}
}
