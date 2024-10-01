package com.pay10.ipay;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.pay10.commons.api.Hasher;
import com.pay10.commons.exception.ErrorType;
import com.pay10.commons.exception.SystemException;
import com.pay10.commons.util.Amount;
import com.pay10.commons.util.FieldType;
import com.pay10.commons.util.Fields;
import com.pay10.commons.util.StatusType;
import com.pay10.commons.util.TransactionType;
import com.pay10.commons.util.Validator;
import com.pay10.pg.core.util.Processor;
import com.pay10.pg.history.Historian;

@Service
public class IPayRefundResponseProcessor {
	
	private static Logger logger = LoggerFactory.getLogger(IPayRefundResponseProcessor.class.getName());
	
	@Autowired
	private Validator generalValidator;
	
	@Autowired
	private Historian historian;
	
	@Autowired
	@Qualifier("updateProcessor")
	private Processor updateProcessor;
	
	public void process(Fields fields) throws SystemException {
		transformResponse(fields);
		generalValidator.validate(fields);
		historian.findPrevious(fields);
		historian.populateFieldsFromPrevious(fields);
	}

	private void transformResponse(Fields fields) throws SystemException {
		String decryptedValue = decrypt(fields);
		logger.info("Refund response after decryption: " + decryptedValue);
		mapFields(decryptedValue, fields);
		if (!isCheckSumMatching(decryptedValue)) {
			fields.put(FieldType.RESPONSE_CODE.getName(), ErrorType.SIGNATURE_MISMATCH.getResponseCode());
			fields.put(FieldType.RESPONSE_MESSAGE.getName(), ErrorType.SIGNATURE_MISMATCH.getResponseMessage());
			fields.put(FieldType.STATUS.getName(), StatusType.FAILED.getName());
			logger.info("Checksum for Refund Response is not matching");
		}
	}

	private String decrypt(Fields fields) {
		String key = fields.get(FieldType.TXN_KEY.getName());
		String iv = fields.get(FieldType.PASSWORD.getName());
		String encrypted = fields.get(FieldType.IPAY_FINAL_ENC_RESPONSE.getName());
		logger.info("Refund response before decryption: " + encrypted);
		return IPayUtil.decrypt(encrypted.trim(), key, iv);
	}
	
	private void mapFields(String decryptedValue, Fields fields) {
		String [] values = decryptedValue.split(Constants.IPAY_RESPONSE_SEPARATOR);
		if (values.length == 7) {
			Map<String, String> receivedValues = new HashMap<>();
			for (String string : values) {
				String[] splitter = string.split(Constants.EQUATOR);
				receivedValues.put(splitter[0], splitter[1]);
			}
			
			fields.put(FieldType.MERCHANT_ID.getName(), receivedValues.get(Constants.MERCHANT_CODE));
			fields.put(FieldType.TXN_ID.getName(), receivedValues.get(Constants.REFUND_RESERVATION_ID));
			fields.put(FieldType.AMOUNT.getName(), Amount.formatAmount(receivedValues.get(Constants.REFUND_AMOUNT), fields.get(FieldType.CURRENCY_CODE.getName())));
			fields.put(FieldType.ACQ_ID.getName(), receivedValues.get(Constants.REFUND_INITIATION_ID));
			fields.put(FieldType.STATUS.getName(), receivedValues.get(Constants.STATUS).equalsIgnoreCase(Constants.SUCCESS)? StatusType.CAPTURED.getName() : StatusType.FAILED.getName());
			fields.put(FieldType.PG_TXN_STATUS.getName(), fields.get(FieldType.STATUS.getName()));
			fields.put(FieldType.INTERNAL_VALIDATE_HASH_YN.getName(), "N");
			fields.put(FieldType.RESPONSE_CODE.getName(), ErrorType.SUCCESS.getResponseCode());
			fields.put(FieldType.RESPONSE_MESSAGE.getName(), ErrorType.SUCCESS.getResponseMessage());
			fields.put(FieldType.INTERNAL_ORIG_TXN_TYPE.getName(), TransactionType.REFUND.getName());
		}
		else {
			logger.error("Response message has more/less parameters than expected: " + decryptedValue);
		}
	}

	private boolean isCheckSumMatching(String decryptedValue) throws SystemException {
		String receivedChecksum = decryptedValue.substring(decryptedValue.lastIndexOf(Constants.EQUATOR) + 1);
		String checksumString = decryptedValue.substring(0, decryptedValue.lastIndexOf(Constants.IPAY_SEPARATOR));
		String checksum = Hasher.getHash(checksumString);
		return receivedChecksum.equalsIgnoreCase(checksum);
	}

}
