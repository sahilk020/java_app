package com.pay10.pg.core.util;

import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.pay10.commons.api.Hasher;
import com.pay10.commons.exception.ErrorType;
import com.pay10.commons.exception.SystemException;
import com.pay10.commons.util.Constants;
import com.pay10.commons.util.FieldType;
import com.pay10.commons.util.Fields;
import com.pay10.commons.util.StatusType;
import com.pay10.commons.util.SystemProperties;
import com.pay10.commons.util.UserAccountServices;

@Service
public class TransactionResponser {

	@Autowired
	private UserAccountServices userAccountServices;

	public static final String DATE_FORMAT = "yyyy-MM-dd HH:mm:ss";

	public void getResponse(Fields fields) throws SystemException {

		getOriginalTransaction(fields);

		addResponseFields(fields);

		removeInvalidResponseFields(fields);

		secureResponse(fields);

		updateError(fields);

		// Add hash in response, this should be validated by client
		addHash(fields);
	}

	public void addResponseFields(Fields fields) {
		addResponseDateTime(fields);
	}

	public void addResponseDateTime(Fields fields) {
		final Date date = new Date();
		final SimpleDateFormat simpleDateFormat = new SimpleDateFormat(DATE_FORMAT);
		fields.put(FieldType.RESPONSE_DATE_TIME.getName(), simpleDateFormat.format(date));
	}

	public void updateError(Fields fields) {
		String responseCode = fields.get(FieldType.RESPONSE_CODE.getName());
		if (null == responseCode) {
			responseCode = ErrorType.UNKNOWN.getCode();
			fields.put(FieldType.RESPONSE_CODE.getName(), responseCode);
			fields.put(FieldType.RESPONSE_MESSAGE.getName(), ErrorType.UNKNOWN.getResponseMessage());
		}

		if (!responseCode.equals(ErrorType.SUCCESS.getCode())) {
			// This is applicable when response is not having status or invalid
			// status
			String status = fields.get(FieldType.STATUS.getName());

			if (null == status) {
				status = StatusType.ERROR.getName();
			} else if (status.equals(StatusType.APPROVED.getName()) || status.equals(StatusType.CAPTURED.getName())) {
				fields.put(FieldType.STATUS.getName(), StatusType.ERROR.getName());
			}
		}
	}

	public void secureResponse(Fields fields) {
		CryptoUtil.truncateCardNumber(fields);
	}

	/*
	 * If this transaction is duplicate of a previous successful authorization,
	 * return original transaction response
	 */
	public void getOriginalTransaction(Fields fields) {
		// If
		// if(null == fields.get(FieldType.IS_DUPLICATE.getName()) ||
		// fields.get(FieldType.IS_DUPLICATE.getName()).equals("Y")){
		// return;
		// }
		//
		// Fields previousFields = fields.getPrevious();
		// fields.put(FieldType.ORIG_TXN_ID.getName(),
		// previousFields.remove(FieldType.TXN_ID.getName()));
		//
		// for(String key: SystemProperties.getResponseFields()){
		// String value = previousFields.get(key);
		// if(null != value){
		// fields.put(key, value);
		// }
		// }
	}

	public void removeInvalidResponseFields(Fields fields) {
		Fields responseFields = new Fields();
		String internalFlag = fields.get(FieldType.IS_INTERNAL_REQUEST.getName());
		Collection<String> validResponseFields = null;
		if (StringUtils.isNotEmpty(internalFlag) && internalFlag.equals(Constants.Y_FLAG.getValue())) {
			validResponseFields = SystemProperties.getInternalResponsefields();
		} else {
			validResponseFields = SystemProperties.getResponseFields();
		}
		for (String key : validResponseFields) {
			String value = fields.get(key);
			if (null != value) {
				responseFields.put(key, value);
			}
		}
		fields.clear();
		fields.putAll(responseFields.getFields());
	}

	public void addHash(Fields fields) throws SystemException {
//		String internalFlag = fields.get(FieldType.IS_INTERNAL_REQUEST.getName());
//		if (!(StringUtils.isNotEmpty(internalFlag) && internalFlag.equals(Constants.Y_FLAG.getValue()))) {
//			fields.put(FieldType.HASH.getName(), Hasher.getHash(fields));
//		}
		fields.put(FieldType.HASH.getName(), Hasher.getHash(fields));


	}

	/**
	 * This method is used to manipulate response for emitra.
	 * 
	 */
	public void getResponse(Fields fields, boolean emitra) throws SystemException {

		addResponseFields(fields);

		removeInvalidResponseFields(fields);

		secureResponse(fields);

		updateError(fields);
		
		StatusType statusType = StatusType.getInstanceFromName(StringUtils.upperCase(fields.get(FieldType.STATUS.getName())));
		String status = statusType.getUiName();
		status = StringUtils.equalsAnyIgnoreCase(status, "Invalid", "Cancelled") ? "Failed" : status;
		status = StringUtils.equalsAnyIgnoreCase(status, "Captured") ? "Success" : status;
		fields.put(FieldType.STATUS.getName(), StringUtils.upperCase(status));

		double amount = fields.get(FieldType.AMOUNT.getName()) == null ? 0
				: Double.valueOf(fields.get(FieldType.AMOUNT.getName())) / 100;
		double totalAmount = fields.get(FieldType.TOTAL_AMOUNT.getName()) == null ? 0
				: Double.valueOf(fields.get(FieldType.TOTAL_AMOUNT.getName())) / 100;
		fields.put(FieldType.AMOUNT.getName(), String.valueOf(amount));
		fields.put(FieldType.TOTAL_AMOUNT.getName(), String.valueOf(totalAmount));

		// Add hash in response, this should be validated by client
		addHash(fields, emitra);
	}

	public void addHash(Fields fields, boolean emitra) throws SystemException {
		String internalFlag = fields.get(FieldType.IS_INTERNAL_REQUEST.getName());
		String payId = fields.get(FieldType.PAY_ID.getName());
		String key = userAccountServices.generateMerchantHostedEncryptionKey(payId);
		if (!(StringUtils.isNotEmpty(internalFlag) && internalFlag.equals(Constants.Y_FLAG.getValue()))) {
			fields.put(FieldType.HASH.getName(), Hasher.getHash(fields.getFields(), key));
		}

	}
}
