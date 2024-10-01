package com.pay10.pinelabs;

import java.util.Map;

import com.google.gson.JsonObject;
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
import com.pay10.pg.core.util.ProcessManager;
import com.pay10.pg.core.util.Processor;

@Service
public class PinelabsSaleResponseHandler {

	private static Logger logger = LoggerFactory.getLogger(PinelabsSaleResponseHandler.class.getName());

	@Autowired
	private Validator generalValidator;

	@Autowired
	@Qualifier("updateProcessor")
	private Processor updateProcessor;

	@Autowired
	@Qualifier("pinelabsTransactionConverter")
	private TransactionConverter transactionConverter;

	@Autowired
	private PinelabsTransformer pinelabsTransformer;

	@Autowired
	@Qualifier("pinelabsTransactionCommunicator")
	private TransactionCommunicator transactionCommunicator;

	public Map<String, String> process(Fields fields) throws SystemException {

		String newTxnId = TransactionManager.getNewTransactionId();
		fields.put(FieldType.TXN_ID.getName(), newTxnId);
		fields.put(FieldType.TXNTYPE.getName(), TransactionType.SALE.getCode());
		Transaction transactionResponse = new Transaction();
		String response = fields.get(FieldType.PINELABS_RESPONSE_FIELD.getName());
		transactionResponse = toTransaction(response, transactionResponse,fields.get(FieldType.CURRENCY_CODE.getName()));
		boolean doubleVer = doubleVerification(response, fields);
		logger.info("process:: doubleVerification={}, pgRefNo={}, orderId={}", doubleVer,
				fields.get(FieldType.PG_REF_NUM.getName()), fields.get(FieldType.ORDER_ID.getName()));

		fields.remove(FieldType.PINELABS_RESPONSE_FIELD.getName());
		generalValidator.validate(fields);

		if (doubleVer == true) {
			
			pinelabsTransformer = new PinelabsTransformer(transactionResponse);
			pinelabsTransformer.updateResponse(fields);
		} else {
			fields.put(FieldType.STATUS.getName(), StatusType.DENIED.getName());
			fields.put(FieldType.RESPONSE_CODE.getName(), ErrorType.SIGNATURE_MISMATCH.getCode());
			fields.put(FieldType.RESPONSE_MESSAGE.getName(), transactionResponse.getResponseMsg());
		}
		fields.put(FieldType.INTERNAL_ORIG_TXN_TYPE.getName(), fields.get(FieldType.TXNTYPE.getName()));
		ProcessManager.flow(updateProcessor, fields, true);
		return fields.getFields();

	}

	public Transaction toTransaction(String response, Transaction transactionResponse, String currencyCode) {
		Transaction transaction = new Transaction();
		transaction = transactionConverter.toTransaction(response, currencyCode);
		return transaction;
	}

	private boolean doubleVerification(String transactionResponse, Fields fields) throws SystemException {

		try {

			JSONObject resJson = new JSONObject(transactionResponse);

			// Skip if txStatus is not present in response
			if (!transactionResponse.contains(Constants.pineTxnStatus)) {
				return true;
			}

			// Skip for unsuccessful transactions if
			if (transactionResponse.contains(Constants.pineTxnStatus)
					&& !resJson.get(Constants.pineTxnStatus).toString().equalsIgnoreCase("4")) {
				return true;
			}

			String request = transactionConverter.statusEnquiryRequest(fields, null);
			String hostUrl = PropertiesManager.propertiesMap.get(Constants.STATUS_ENQ_REQUEST_URL);
			logger.info("Double verification:: Request={},pgRefNum={},orderId={} ", request,fields.get(FieldType.PG_REF_NUM.getName()), fields.get(FieldType.ORDER_ID.getName()));
			String response = transactionCommunicator.refundEnquiryPostRequest(request, hostUrl);

			logger.info("Double Verification:: Response ={},pgRefNo={}, orderId={}" , response,fields.get(FieldType.PG_REF_NUM.getName()), fields.get(FieldType.ORDER_ID.getName()));
			
			JSONObject resJsonBank = new JSONObject(response);
			// only for live
			if ((resJson.get(Constants.pineTxnStatus).toString()
					.equals(resJsonBank.get(Constants.pcc_Parent_TxnStatus).toString()))
					&& (resJsonBank.get(Constants.pccTxnResponseMessage).toString()
							.equals(resJson.get(Constants.txnResponseMsg).toString()))
					&&(resJsonBank.get(Constants.pccAmt).toString()
							.equals(resJson.get(Constants.responseAmt).toString()))
					&&(resJsonBank.get(Constants.pccUniqueMid).toString()
							.equals(resJson.get(Constants.orderId).toString()))) {
				//logger.info("PINELABS : Double Verification Response.");
				return true;
			} else {
				logger.info(
						"doubleVerification::failed.pgRefNo={}, orderId={}, responseAmount={},doubleVerificationAmount={},responseTxnId={},doubleVerificationTxnId={}",
						fields.get(FieldType.PG_REF_NUM.getName()), fields.get(FieldType.ORDER_ID.getName()),resJson.get(Constants.responseAmt).toString(),resJsonBank.get(Constants.pccAmt).toString(),(resJson.get(Constants.orderId).toString()),(resJsonBank.get(Constants.pccUniqueMid).toString()));
				return false;
			}

		}

		catch (Exception e) {
			logger.error("Exceptionn ", e);
			return false;
		}

	}
}
