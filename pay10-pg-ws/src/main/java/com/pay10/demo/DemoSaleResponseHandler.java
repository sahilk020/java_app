package com.pay10.demo;

import java.util.Map;

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
import com.pay10.pg.core.util.CashfreeChecksumUtil;
import com.pay10.pg.core.util.ProcessManager;
import com.pay10.pg.core.util.Processor;

@Service
public class DemoSaleResponseHandler {

	private static Logger logger = LoggerFactory.getLogger(DemoSaleResponseHandler.class.getName());

	@Autowired
	private Validator generalValidator;

	@Autowired
	@Qualifier("updateProcessor")
	private Processor updateProcessor;

	@Autowired
	@Qualifier("demoTransactionConverter")
	private TransactionConverter transactionConverter;

	@Autowired
	private DemoTransformer demoTransformer;

	@Autowired
	private CashfreeChecksumUtil demoChecksumUtil;

	@Autowired
	@Qualifier("demoTransactionCommunicator")
	private TransactionCommunicator transactionCommunicator;

	public Map<String, String> process(Fields fields) throws SystemException {

		String newTxnId = TransactionManager.getNewTransactionId();
		fields.put(FieldType.TXN_ID.getName(), newTxnId);
		fields.put(FieldType.TXNTYPE.getName(), TransactionType.SALE.getCode());
		Transaction transactionResponse = new Transaction();
		String response = fields.get(FieldType.DEMO_RESPONSE_FIELD.getName());

		
		boolean res = true;
		boolean doubleVer = true;
		
		fields.remove(FieldType.DEMO_RESPONSE_FIELD.getName());
		generalValidator.validate(fields);

		if (res && doubleVer) {
			transactionResponse = toTransaction(response, transactionResponse);
			demoTransformer = new DemoTransformer(transactionResponse);
			demoTransformer.updateResponse(fields);
		} else {
			fields.put(FieldType.STATUS.getName(), StatusType.DENIED.getName());
			fields.put(FieldType.RESPONSE_CODE.getName(), ErrorType.SIGNATURE_MISMATCH.getCode());
			fields.put(FieldType.RESPONSE_MESSAGE.getName(), ErrorType.SIGNATURE_MISMATCH.getResponseMessage());
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

		resJson.remove(Constants.signature);
		String resSignatureCalculated = demoChecksumUtil.checkSaleResponseHash(resJson,
				fields.get(FieldType.TXN_KEY.getName()));

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
			logger.info("CASHFREE Before Double Verification transactionResponse : " + transactionResponse);
			JSONObject resJson = new JSONObject(transactionResponse);

			// Skip if txStatus is not present in response
			if (!transactionResponse.contains(Constants.txStatus)) {
				return true;
			}

			// Skip for unsuccessful transactions if
			if (transactionResponse.contains(Constants.txStatus)
					&& !resJson.get(Constants.txStatus).toString().equalsIgnoreCase("SUCCESS")) {
				return true;
			}

			String request = transactionConverter.statusEnquiryRequest(fields, null);
			logger.info("CASHFREE Double Verification Request : " + request);
			String hostUrl = PropertiesManager.propertiesMap.get(Constants.STATUS_ENQ_REQUEST_URL);
			String response = transactionCommunicator.statusEnqPostRequest(request, hostUrl);

			logger.info("CASHFREE Double Verification Response : " + response);

			JSONObject resJsonBank = new JSONObject(response);

			if (resJsonBank.has(Constants.txStatus) && resJsonBank.has(Constants.orderAmount)
					&& resJsonBank.has(Constants.referenceId)) {
				if (resJson.get(Constants.txStatus).toString().equals(resJsonBank.get(Constants.txStatus).toString())
						&& resJson.get(Constants.orderAmount).toString().equals(resJsonBank.get(Constants.orderAmount).toString())
						&& resJson.get(Constants.referenceId).toString().equals(resJsonBank.get(Constants.referenceId).toString())) {
					return true;
				} else {
					logger.info("Double Verification Response donot match for Cashfree");
					return false;
				}

			} else {
				logger.info("Double Verification Response false for Cashfree");
				return false;
			}
			/*
			 * if
			 * ((resJson.get(Constants.txStatus).toString().equals(resJsonBank.get(Constants
			 * .txStatus).toString())) && (resJson.get(Constants.orderAmount).toString()
			 * .equals(resJsonBank.get(Constants.orderAmount).toString()))) { return true; }
			 * else { logger.info("Double Verification Response donot match for Cashfree");
			 * return false; }
			 */

		}

		catch (Exception e) {
			logger.error("Exceptionn ", e);
			return false;
		}

	}
}
