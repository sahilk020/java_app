package com.pay10.agreepay;

import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.pay10.commons.exception.ErrorType;
import com.pay10.commons.exception.SystemException;
import com.pay10.commons.util.FieldType;
import com.pay10.commons.util.Fields;
import com.pay10.commons.util.PropertiesManager;
import com.pay10.commons.util.StatusType;
import com.pay10.commons.util.TransactionManager;
import com.pay10.commons.util.TransactionType;
import com.pay10.commons.util.Validator;
import com.pay10.pg.core.util.AgreepayChecksumUtil;
import com.pay10.pg.core.util.ProcessManager;
import com.pay10.pg.core.util.Processor;

@Service
public class AgreepaySaleResponseHandler {

	private static Logger logger = LoggerFactory.getLogger(AgreepaySaleResponseHandler.class.getName());

	@Autowired
	private Validator generalValidator;

	@Autowired
	@Qualifier("updateProcessor")
	private Processor updateProcessor;

	@Autowired
	@Qualifier("agreepayTransactionConverter")
	private TransactionConverter transactionConverter;

	@Autowired
	private AgreepayTransformer agreepayTransformer;

	@Autowired
	private AgreepayChecksumUtil agreepayChecksumUtil;

	@Autowired
	@Qualifier("agreepayTransactionCommunicator")
	private TransactionCommunicator transactionCommunicator;

	public Map<String, String> process(Fields fields) throws SystemException {

		String newTxnId = TransactionManager.getNewTransactionId();
		fields.put(FieldType.TXN_ID.getName(), newTxnId);
		fields.put(FieldType.TXNTYPE.getName(), TransactionType.SALE.getCode());
		generalValidator.validate(fields);
		Transaction transactionResponse = new Transaction();
		String response = fields.get(FieldType.AGREEPAY_RESPONSE_FIELD.getName());
		transactionResponse = toTransaction(response, transactionResponse);
		/*
		 * JSONObject resJsonNB = new JSONObject(response); String response_message
		 * =resJsonNB.getString("response_message");
		 */

		boolean res = isHashMatching(response, fields);
		boolean doubleVer = doubleVerification(response, fields);

		fields.remove(FieldType.AGREEPAY_RESPONSE_FIELD.getName());
		

		if (res == true && doubleVer == true) {
			
			agreepayTransformer = new AgreepayTransformer(transactionResponse);
			agreepayTransformer.updateResponse(fields);
		} else {
			fields.put(FieldType.STATUS.getName(), StatusType.DENIED.getName());
			fields.put(FieldType.RESPONSE_CODE.getName(), ErrorType.SIGNATURE_MISMATCH.getCode());
			//fields.put(FieldType.RESPONSE_MESSAGE.getName(), ErrorType.SIGNATURE_MISMATCH.getResponseMessage());
			fields.put(FieldType.RESPONSE_MESSAGE.getName(),transactionResponse.getTxMsg());
		}
		fields.put(FieldType.INTERNAL_ORIG_TXN_TYPE.getName(), fields.get(FieldType.TXNTYPE.getName()));
		ProcessManager.flow(updateProcessor, fields, true);
		return fields.getFields();

	}

	public Transaction toTransaction(String response, Transaction transactionResponse) {
		Transaction transaction = new Transaction();
		transaction = transactionConverter.toTransaction(response);
		return transaction;
	}

	private boolean isHashMatching(String transactionResponse, Fields fields) throws SystemException {

		JSONObject resJson = new JSONObject(transactionResponse.toString());

		String responseSignature = (String) resJson.get(Constants.signature);
		if(responseSignature == null) {
			return true;
		}
		resJson.remove(Constants.signature);
		String resSignatureCalculated = agreepayChecksumUtil.checkSaleResponseHash(resJson,
				fields.get(FieldType.ADF1.getName()));
		logger.info("response slat info -----------"+fields.get(FieldType.ADF1.getName()));
		logger.info("Order Id " + fields.get(FieldType.ORDER_ID.getName()) + "  bank response signature == "
				+ responseSignature);
		logger.info("Order Id " + fields.get(FieldType.ORDER_ID.getName()) + "  calculated signature == "
				+ resSignatureCalculated);

		if (responseSignature.contentEquals(resSignatureCalculated)) {
			return true;
		} else {
			logger.info("Signature from Bank did not match with generated Signature");
			logger.info("Bank Hash = " + responseSignature);
			logger.info("Calculated Hash = " + resSignatureCalculated);
			return false;
		}

	}

	private boolean doubleVerification(String transactionResponse, Fields fields) throws SystemException {

		try {
			Transaction transaction = new Transaction();
			JSONObject resJson = new JSONObject(transactionResponse);

			// Skip if txStatus is not present in response
			if (!transactionResponse.contains(Constants.txStatus)){
				return true;
			}
			
			// Skip for unsuccessful transactions if
			if (transactionResponse.contains(Constants.txStatus)
					&& !resJson.get(Constants.txStatus).toString().equalsIgnoreCase("0")) {
				return true;
			}
			if (transactionResponse.contains(Constants.transactionId)) {
				transaction.setReferenceId(resJson.get(Constants.transactionId).toString());
			}
			String request = transactionConverter.statusEnquiryRequest(fields, transaction);
			String hostUrl = PropertiesManager.propertiesMap.get(Constants.STATUS_ENQ_REQUEST_URL);
			String response = transactionCommunicator.statusEnqPostRequest(request, hostUrl);

			logger.info("Bank Response = " + transactionResponse);
			logger.info("Double Verification Response = " + response);


			JSONObject resJsonBank = new JSONObject(response);
			JSONArray resArrayBank = resJsonBank.getJSONArray("data");
			//only for live	
			if ((resJson.get(Constants.txStatus).toString().equals(resArrayBank.getJSONObject(0).get(Constants.txStatus).toString())) && 
					(resJson.get(Constants.TxnAmount).toString().equals(resArrayBank.getJSONObject(0).get(Constants.TxnAmount).toString()))) {
				return true;
			} else {
				logger.info("Double Verification Response donot match for Agreepay");
				return false;
			}
			
		}

		catch (Exception e) {
			logger.error("Exceptionn ", e);
			return false;
		}

	}
}

