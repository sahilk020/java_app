package com.pay10.pg.action.beans;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.pay10.commons.exception.ErrorType;
import com.pay10.commons.exception.SystemException;
import com.pay10.commons.util.ConfigurationConstants;
import com.pay10.commons.util.FieldType;
import com.pay10.commons.util.Fields;
import com.pay10.commons.util.StatusType;
import com.pay10.pg.core.util.ResponseCreator;

/**
 * @author Sunil,Puneet
 *
 */
public class ResponsePreparer {
	private static Logger logger = LoggerFactory.getLogger(ResponsePreparer.class.getName());
	@Autowired
	private ResponseCreator responseCreator;
	public void prepareCancelResponse(Fields fields,Map<String,Object> sessionMap){

		
		if (null == fields) {
			fields = new Fields();
			fields.put(FieldType.RESPONSE_MESSAGE.getName(),
					ErrorType.TIMEOUT.getResponseMessage());
			fields.put(FieldType.RESPONSE_CODE.getName(),
					ErrorType.TIMEOUT.getCode());
			fields.put(FieldType.STATUS.getName(),
					StatusType.PENDING.getName());
			fields.put(FieldType.RETURN_URL.getName(),
					ConfigurationConstants.DEFAULT_RETURN_URL.getValue());
		} else {
			String shopifyFlag = (String) sessionMap.get(FieldType.INTERNAL_SHOPIFY_YN.getName());

			fields.put(FieldType.RESPONSE_MESSAGE.getName(),
					ErrorType.CANCELLED.getResponseMessage());
			fields.put(FieldType.RESPONSE_CODE.getName(),
					ErrorType.CANCELLED.getCode());
			fields.put(FieldType.STATUS.getName(),
					StatusType.CANCELLED.getName());
			fields.put(FieldType.INTERNAL_SHOPIFY_YN.getName(),
					(String) sessionMap.get(FieldType.INTERNAL_SHOPIFY_YN.getName()));
			fields.put((FieldType.CANCEL_URL.getName()),
					(String) sessionMap.get(FieldType.CANCEL_URL.getName()));

			Object txnTypeObject = sessionMap
					.get(FieldType.INTERNAL_ORIG_TXN_TYPE.getName());
			if(null!=shopifyFlag && shopifyFlag.equals("Y")){
				fields.put((FieldType.RETURN_URL.getName()),
						(String) sessionMap.get(FieldType.CANCEL_URL.getName()));
			}

			if (null != txnTypeObject) {
				String txnType = (String) txnTypeObject;
				fields.put(FieldType.TXNTYPE.getName(), txnType);
			}
			fields.put(FieldType.INTERNAL_ORIG_TXN_ID.getName(),
					fields.get(FieldType.TXN_ID.getName()));
			try {
				fields.updateNewOrderDetails();
			} catch (SystemException systemException) {
				logger.error("Unable to update cancelled transaction",
						systemException);
			}
		}
		responseCreator.ResponsePost(fields);
	}
}
