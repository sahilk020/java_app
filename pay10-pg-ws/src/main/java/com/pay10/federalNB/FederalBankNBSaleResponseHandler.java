package com.pay10.federalNB;

import java.util.Map;

import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.pay10.commons.exception.ErrorType;
import com.pay10.commons.exception.SystemException;
import com.pay10.commons.util.Amount;
import com.pay10.commons.util.FieldType;
import com.pay10.commons.util.Fields;
import com.pay10.commons.util.PropertiesManager;
import com.pay10.commons.util.StatusType;
import com.pay10.commons.util.TransactionManager;
import com.pay10.commons.util.Validator;
import com.pay10.pg.core.util.FederalNBChecksumUtil;
import com.pay10.pg.core.util.ProcessManager;
import com.pay10.pg.core.util.Processor;

@Service
public class FederalBankNBSaleResponseHandler {

	private static Logger logger = LoggerFactory.getLogger(FederalBankNBSaleResponseHandler.class.getName());

	@Autowired
	private Validator generalValidator;

	@Autowired
	@Qualifier("updateProcessor")
	private Processor updateProcessor;

	@Autowired
	@Qualifier("federalBankNBTransactionConverter")
	private TransactionConverter transactionConverter;

	@Autowired
	@Qualifier("federalBankNBTransactionCommunicator")
	private TransactionCommunicator transactionCommunicator;
	@Autowired
	private FederalNBChecksumUtil federalNBChecksumUtil;
	@Autowired
	private FederalBankNBTransformer federalBankTransformer;

	public static final String PIPE = "|";
	public static final String DOUBLE_PIPE = "||";

	public Map<String, String> process(Fields fields) throws SystemException {

		String newTxnId = TransactionManager.getNewTransactionId();
		fields.put(FieldType.TXN_ID.getName(), newTxnId);
		// generalValidator.validate(fields);
		Transaction transactionResponse = new Transaction();
		String pipedResponse = fields.get(FieldType.FEDERALBANK_NB_RESPONSE_FIELD.getName());
		transactionResponse = toTransaction(pipedResponse, transactionResponse);
		boolean res = isHashMatching(pipedResponse, fields);
		boolean doubleVer = doubleVerification(transactionResponse, fields);
		
		JSONObject resJsonBank = new JSONObject(pipedResponse);
		String response_message=resJsonBank.getString("response_message");

		logger.info("process:: doubleVerification={}, pgRefNo={} ", doubleVer,
				fields.get(FieldType.PG_REF_NUM.getName()));
		if (res == true && doubleVer == true) {

			federalBankTransformer = new FederalBankNBTransformer(transactionResponse);
			federalBankTransformer.updateResponse(fields);
		} else {
			fields.put(FieldType.STATUS.getName(), StatusType.DENIED.getName());
			fields.put(FieldType.RESPONSE_CODE.getName(), ErrorType.SIGNATURE_MISMATCH.getCode());
			//fields.put(FieldType.RESPONSE_MESSAGE.getName(), ErrorType.SIGNATURE_MISMATCH.getResponseMessage());
			fields.put(FieldType.RESPONSE_MESSAGE.getName(),response_message);
		}

		// transactionResponse = toTransaction(pipedResponse);

		// federalBankTransformer = new FederalBankNBTransformer(transactionResponse);
		// federalBankTransformer.updateResponse(fields);

		fields.put(FieldType.INTERNAL_ORIG_TXN_TYPE.getName(), fields.get(FieldType.TXNTYPE.getName()));
		fields.remove(FieldType.FEDERALBANK_NB_RESPONSE_FIELD.getName());
		ProcessManager.flow(updateProcessor, fields, true);
		return fields.getFields();

	}

	private boolean doubleVerification(Transaction transactionResponse, Fields fields) {

		try {
			// Skip for unsuccessful transactions if
			if (!transactionResponse.getResponse_code().equalsIgnoreCase("0")) {
				return true;
			}

			String encrequest = transactionConverter.statusEnquiryRequest(fields);
			String hostUrl = PropertiesManager.propertiesMap.get(Constants.FEDERALBANK_NB_STATUS_ENQ_URL);

			String response = transactionCommunicator.refundPostRequest(encrequest, hostUrl);
			Transaction transactionStatusResponse = transactionConverter.toTransactionStatus(response);

			logger.info(transactionStatusResponse.getResponse_code() + "Federal  bank Double verification Response = "
					+ response);
			
			
			
			String	toamount = Amount.toDecimal(fields.get(FieldType.TOTAL_AMOUNT.getName()), fields.get(FieldType.CURRENCY_CODE.getName()));
			logger.info(
					"doubleVerification::  Ferderal NB response. Status={},  resAmount={},pgRefNo={}",
					transactionStatusResponse.getResponse_code(),
					transactionResponse.getAmount(), transactionResponse.getOrder_id());
	
			if (transactionStatusResponse.getResponse_code() != null
					&& transactionStatusResponse.getResponse_code().equalsIgnoreCase("0")
					&&fields.get(FieldType.PG_REF_NUM.getName()).equalsIgnoreCase(transactionResponse.getOrder_id())
					&&toamount.equalsIgnoreCase(transactionResponse.getAmount())) {

				return true;
			}
			logger.info(
					"doubleVerification:: failed. Status={},  resAmount={},pgRefNo={}",
					transactionStatusResponse.getResponse_code(),
					transactionResponse.getAmount(), transactionResponse.getOrder_id());
		
			return false;
		}

		catch (Exception e) {
			logger.error("Exceptionn ", e);
			return false;
		}

	}

	public Transaction toTransaction(String response, Transaction transactionResponse) {
		Transaction transaction = new Transaction();
		transaction = transactionConverter.toTransaction(response);
		return transaction;
	}

	private boolean isHashMatching(String transactionResponse, Fields fields) throws SystemException {

		JSONObject resJson = new JSONObject(transactionResponse.toString());

		String responseSignature = (String) resJson.get(Constants.HASH);
		if (responseSignature == null) {
			return true;
		}
		resJson.remove(Constants.HASH);
		resJson.remove(Constants.API_KEY);
		String resSignatureCalculated = federalNBChecksumUtil.checkSaleResponseHash(resJson,
				fields.get(FieldType.ADF1.getName()));
		logger.info("response slat info -----------" + fields.get(FieldType.ADF1.getName()));
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
}
