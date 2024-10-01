package com.pay10.isgpay;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.pay10.commons.exception.ErrorType;
import com.pay10.commons.exception.SystemException;
import com.pay10.commons.util.FieldType;
import com.pay10.commons.util.Fields;
import com.pay10.commons.util.StatusType;
import com.pay10.commons.util.TransactionManager;
import com.pay10.commons.util.Validator;
import com.pay10.pg.core.util.ProcessManager;
import com.pay10.pg.core.util.Processor;
import com.pay10.pg.history.Historian;

@Service
public class ISGPaySaleResponseHandler {

	private static Logger logger = LoggerFactory.getLogger(ISGPaySaleResponseHandler.class.getName());

	@Autowired
	private Validator generalValidator;

	@Autowired
	@Qualifier("updateProcessor")
	private Processor updateProcessor;

	@Autowired
	private ISGPayTransformer iSGPayTransformer;

	@Autowired
	private Historian historian;

	@Autowired
	ISGPayStatusEnquiryProcessor iSGPayStatusEnquiryProcessor;

	public Map<String, String> process(Fields fields) throws SystemException {

		String newTxnId = TransactionManager.getNewTransactionId();
		fields.put(FieldType.TXN_ID.getName(), newTxnId);
		generalValidator.validate(fields);
		Transaction transactionResponse = new Transaction();
		String isgPayResponse = fields.get(FieldType.ISGPAY_RESPONSE_FIELD.getName());
		transactionResponse = toTransaction(isgPayResponse);
/*

		String[] paramatersRespo = isgPayResponse.split("||");
		Map<String, String> paramMapRespo = new HashMap<String, String>();
		logger.info("Fields In isgPayResponse:{}"+fields.getFieldsAsString());
		for (String param : paramatersRespo) {
			String[] parameterPair = param.split("=");
			if (parameterPair.length > 1) {
				paramMapRespo.put(parameterPair[0], parameterPair[1]);


			}
		}


		String Message = paramMapRespo.get("Message");
*/

		boolean doubleVer = doubleVerification(transactionResponse, fields);

		logger.info("process:: doubleVerification={}, pgRefNo={}, orderId={}", doubleVer,
				fields.get(FieldType.PG_REF_NUM.getName()), fields.get(FieldType.ORDER_ID.getName()));
		if (doubleVer) {
			iSGPayTransformer = new ISGPayTransformer(transactionResponse);
			logger.info("fields for ISGPAYResponse" + fields.getFieldsAsString());
			iSGPayTransformer.updateResponse(fields);
		} else {
			fields.put(FieldType.STATUS.getName(), StatusType.DENIED.getName());
			fields.put(FieldType.RESPONSE_CODE.getName(), ErrorType.SIGNATURE_MISMATCH.getCode());
			fields.put(FieldType.RESPONSE_MESSAGE.getName(), transactionResponse.getMessage());
		}
		// iSGPayTransformer = new ISGPayTransformer(transactionResponse);
		// iSGPayTransformer.updateResponse(fields);

		// historian.addPreviousSaleFields(fields);
		fields.put(FieldType.INTERNAL_ORIG_TXN_TYPE.getName(), fields.get(FieldType.TXNTYPE.getName()));
		fields.remove(FieldType.ISGPAY_RESPONSE_FIELD.getName());
		ProcessManager.flow(updateProcessor, fields, true);
		return fields.getFields();

	}

	public Transaction toTransaction(String isgPayResponse) {

		Transaction transaction = new Transaction();

		String responseparamsArray[] = isgPayResponse.split(Pattern.quote("||"));

		Map<String, String> responseMap = new HashMap<String, String>();

		for (String item : responseparamsArray) {

			String itemsArray[] = item.split("=");
			String key = itemsArray[0];
			String value = itemsArray[1];
			responseMap.put(key, value);

		}

		logger.info("Final Bank Response for ISGPAY : " + isgPayResponse);

		if (StringUtils.isNotBlank(responseMap.get("TerminalId"))) {
			transaction.setTerminalId(responseMap.get("TerminalId"));
		}

		if (StringUtils.isNotBlank(responseMap.get("MerchantId"))) {
			transaction.setMerchantId(responseMap.get("MerchantId"));
		}

		if (StringUtils.isNotBlank(responseMap.get("BankId"))) {
			transaction.setBankId(responseMap.get("BankId"));
		}

		if (StringUtils.isNotBlank(responseMap.get("ResponseCode"))) {
			transaction.setResponseCode(responseMap.get("ResponseCode"));
		}

		if (StringUtils.isNotBlank(responseMap.get("Message"))) {
			transaction.setMessage(responseMap.get("Message"));
		}

		if (StringUtils.isNotBlank(responseMap.get("MCC"))) {
			transaction.setMcc(responseMap.get("MCC"));
		}

		if (StringUtils.isNotBlank(responseMap.get("pgTxnId"))) {
			transaction.setPgTxnId(responseMap.get("pgTxnId"));
		}

		if (StringUtils.isNotBlank(responseMap.get("TxnRefNo"))) {
			transaction.setTxnRefNo(responseMap.get("TxnRefNo"));
		}

		if (StringUtils.isNotBlank(responseMap.get("hashValidated"))) {
			transaction.setHashValidated(responseMap.get("hashValidated"));
		}

		if (StringUtils.isNotBlank(responseMap.get("TransactionType"))) {
			transaction.setIsgTransactionType(responseMap.get("TransactionType"));
		}

		if (StringUtils.isNotBlank(responseMap.get("ENROLLED"))) {
			transaction.setENROLLED(responseMap.get("ENROLLED"));
		}

		if (StringUtils.isNotBlank(responseMap.get("Stan"))) {
			transaction.setStan(responseMap.get("Stan"));
		}

		if (StringUtils.isNotBlank(responseMap.get("NewTransactionId"))) {
			transaction.setNewTransactionId(responseMap.get("NewTransactionId"));
		}

		if (StringUtils.isNotBlank(responseMap.get("TxnId"))) {
			transaction.setTxnId(responseMap.get("TxnId"));
		}

		if (StringUtils.isNotBlank(responseMap.get("UCAP"))) {
			transaction.setUCAP(responseMap.get("UCAP"));
		}

		if (StringUtils.isNotBlank(responseMap.get("MessageType"))) {
			transaction.setMessageType(responseMap.get("MessageType"));
		}

		if (StringUtils.isNotBlank(responseMap.get("TransactionDate"))) {
			transaction.setTransactionDate(responseMap.get("TransactionDate"));
		}

		if (StringUtils.isNotBlank(responseMap.get("AuthCode"))) {
			transaction.setAuthCode(responseMap.get("AuthCode"));
		}

		if (StringUtils.isNotBlank(responseMap.get("PosEntryMode"))) {
			transaction.setPosEntryMode(responseMap.get("PosEntryMode"));
		}

		if (StringUtils.isNotBlank(responseMap.get("RetRefNo"))) {
			transaction.setRetRefNo(responseMap.get("RetRefNo"));
		}

		if (StringUtils.isNotBlank(responseMap.get("TransactionTime"))) {
			transaction.setTransactionTime(responseMap.get("TransactionTime"));
		}

		if (StringUtils.isNotBlank(responseMap.get("AuthStatus"))) {
			transaction.setAuthStatus(responseMap.get("AuthStatus"));
		}

		if (StringUtils.isNotBlank(responseMap.get("ccAuthReply_processorResponse"))) {
			transaction.setCcAuthReply_processorResponse(responseMap.get("ccAuthReply_processorResponse"));
		}

		if (StringUtils.isNotBlank(responseMap.get("Amount"))) {
			transaction.setAmount(responseMap.get("Amount"));
		}

		return transaction;
	}

	private boolean doubleVerification(Transaction transactionResponse, Fields fields) {
		try {

			if (StringUtils.isBlank(transactionResponse.getResponseCode())) {
				return false;
			}

			// Don't check for transactions other than successful transactions.
			if (!(transactionResponse.getResponseCode()).equalsIgnoreCase(ISGPayResultType.ISGPAY001.getBankCode())) {
				return true;
			}

			fields.logAllFields("ISGPAY Double verification Request : ");
			String request = iSGPayStatusEnquiryProcessor.statusEnquiryRequest(fields);
			logger.info("ISGPAY double verification request : order id : " + fields.get(FieldType.ORDER_ID.getName())
					+ " : " + request);
			String response = iSGPayStatusEnquiryProcessor.getResponse(request);
			logger.info("ISGPAY double verification response : order id : " + fields.get(FieldType.ORDER_ID.getName())
					+ " : " + response);

			Transaction doubleVerTxnResponse = iSGPayStatusEnquiryProcessor.toTransaction(response);

			if (doubleVerTxnResponse == null) {
				logger.info("ISGPAY Double verification transaction response is null.");
				return false;
			}

			if (StringUtils.isBlank(doubleVerTxnResponse.getAmount())
					|| StringUtils.isBlank(transactionResponse.getAmount())) {
				logger.info("Amount is empty in txn response : " + transactionResponse.getAmount()
						+ ", double verification : " + doubleVerTxnResponse.getAmount());
				return false;
			}

			String fieldsAmount = fields.get(FieldType.TOTAL_AMOUNT.getName()); // Amount.toDecimal(fields.get(FieldType.TOTAL_AMOUNT.getName()),
																// fields.get(FieldType.CURRENCY_CODE.getName()));
	
			if ((doubleVerTxnResponse.getResponseCode().equals(transactionResponse.getResponseCode()))
					&& doubleVerTxnResponse.getAmount().equals(fields.get(FieldType.TOTAL_AMOUNT.getName()))
					&& doubleVerTxnResponse.getTxnRefNo().equals(fields.get(FieldType.PG_REF_NUM.getName()))) {
				return true;
			} else {
				logger.info(
						"doubleVerification:: failed.pgRefNo={}, orderId={}, doubleVerificationRescode={}, responseCode={}, doubleVerificationAmount={}, responseAmount={}, doubleVerificationTxnId={}, responseTxnId={}",
						fields.get(FieldType.PG_REF_NUM.getName()), fields.get(FieldType.ORDER_ID.getName()),doubleVerTxnResponse.getResponseCode(), transactionResponse.getResponseCode(),doubleVerTxnResponse.getAmount(),fieldsAmount,doubleVerTxnResponse.getTxnRefNo(),fields.get(FieldType.PG_REF_NUM.getName()));
				return false;
			}

		} catch (Exception e) {
			logger.info("Exception while ISGPAY double verification : " + e);
			logger.info(e.getMessage());
			return false;
		}
	}

}
