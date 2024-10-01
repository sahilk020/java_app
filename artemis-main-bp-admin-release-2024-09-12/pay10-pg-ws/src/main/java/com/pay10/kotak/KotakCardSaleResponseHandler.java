package com.pay10.kotak;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.google.gson.Gson;
import com.pay10.commons.exception.ErrorType;
import com.pay10.commons.exception.SystemException;
import com.pay10.commons.user.Account;
import com.pay10.commons.user.AccountCurrency;
import com.pay10.commons.user.User;
import com.pay10.commons.user.UserDao;
import com.pay10.commons.util.AcquirerType;
import com.pay10.commons.util.Amount;
import com.pay10.commons.util.FieldType;
import com.pay10.commons.util.Fields;
import com.pay10.commons.util.StatusType;
import com.pay10.commons.util.TransactionManager;
import com.pay10.pg.core.util.KotakCardCheckSum;
import com.pay10.pg.core.util.ProcessManager;
import com.pay10.pg.core.util.Processor;

@Service
public class KotakCardSaleResponseHandler {

	private static Logger logger = LoggerFactory.getLogger(KotakCardSaleResponseHandler.class.getName());

	@Autowired
	KotakCardStatusEnquiryProcessor kotakCardStatusEnquiryProcessor;

	@Autowired
	private KotakCardCheckSum kotakCardCheckSum;

	@Autowired
	@Qualifier("kotakTransactionConverter")
	private TransactionConverter converter;

	@Autowired
	@Qualifier("updateProcessor")
	private Processor updateProcessor;

	@Autowired
	private UserDao userDao;

	@Autowired
	private KotakTransformer kotakTransformer;

	public Map<String, String> process(Fields fields) throws Exception {
		logger.info("fields -------->>  " + fields.getFieldsAsString());
		String newTxnId = TransactionManager.getNewTransactionId();
		fields.put(FieldType.TXN_ID.getName(), newTxnId);
		Transaction transactionResponse = new Transaction();
		String response = fields.get(FieldType.KOTAK_RESPONSE_FIELD.getName());
		logger.info("KOTAK RESPONSE FIELD : " + response);

		Map<String, String> keyMap = getTxnKey(fields);
		fields.put(FieldType.ADF1.getName(), keyMap.get("ADF1"));
		fields.put(FieldType.ADF2.getName(), keyMap.get("ADF2"));
		fields.put(FieldType.ADF3.getName(), keyMap.get("ADF3"));

		fields.put(FieldType.ADF4.getName(), keyMap.get("ADF4"));
		fields.put(FieldType.TXN_KEY.getName(), keyMap.get("txnKey"));
		fields.put(FieldType.MERCHANT_ID.getName(), keyMap.get("MerchantId"));

		String responseAuthorize = converter.saleAuthorize(fields, response);
		logger.info("responseAuthorize" + responseAuthorize);
		// fields.remove(FieldType.KOTAK_RESPONSE_FIELD.getName());

		// generalValidator.validate(fields);

		transactionResponse = toTransaction(responseAuthorize);
		boolean doubleVer = doubleVerification(transactionResponse, fields);
		logger.info("process:: doubleVerification={}, pgRefNo={} ", doubleVer,
				fields.get(FieldType.PG_REF_NUM.getName()));
		if (doubleVer) {

			kotakTransformer = new KotakTransformer(transactionResponse);
			kotakTransformer.updateresponseKotakCard(fields);
		} else {
			fields.put(FieldType.STATUS.getName(), StatusType.DENIED.getName());
			fields.put(FieldType.RESPONSE_CODE.getName(), ErrorType.SIGNATURE_MISMATCH.getCode());
			fields.put(FieldType.RESPONSE_MESSAGE.getName(), ErrorType.SIGNATURE_MISMATCH.getResponseMessage());
		}

		logger.info("<<<<<<< fields >>>>>>>>>> " + fields.getFieldsAsString());

		fields.put(FieldType.INTERNAL_ORIG_TXN_TYPE.getName(), fields.get(FieldType.TXNTYPE.getName()));
		ProcessManager.flow(updateProcessor, fields, true);
		return fields.getFields();

	}

	private boolean doubleVerification(Transaction transactionResponse, Fields fields) {

		try {
			if (!transactionResponse.getResponseCode().equalsIgnoreCase("00")) {
				return true;
			}

			String encresponse = kotakCardStatusEnquiryProcessor.statusEnquiryRequest(fields);
			logger.info("Dual verification response" + encresponse);
			// = //communicator.refundPostRequest(encrequest, hostUrl,fields);
			JSONObject statusResponse = new JSONObject(encresponse);

			String status = (String) statusResponse.get("ResponseCode");
			String pgref = (String) statusResponse.get("OrderId");
			String amount = (String) statusResponse.get("Amount");
			String responseMessage = (String) statusResponse.get("ResponseMessage");

			logger.info("dual verification::  kotak card response. Status={},  resAmount={},orderid={}", status, amount,
					pgref);

			if (status != null && status.equalsIgnoreCase("00")
					&& ("Transaction Successful").equalsIgnoreCase(responseMessage)
					&& amount.equalsIgnoreCase(fields.get(FieldType.TOTAL_AMOUNT.getName()))) {

				return true;
			}
			logger.info("doubleVerification:: failed. Status={},  resAmount={},pgRefNo={}", status, amount, pgref);

			return false;
		}

		catch (Exception e) {
			logger.error("Exceptionn ", e);
			return false;
		}

	}

	public Map<String, String> getTxnKey(Fields fields) throws SystemException {

		Map<String, String> keyMap = new HashMap<String, String>();

		User user = userDao.findPayId(fields.get(FieldType.PAY_ID.getName()));
		Account account = null;
		Set<Account> accounts = user.getAccounts();

		if (accounts == null || accounts.size() == 0) {
			logger.info("No account found for Pay ID = " + fields.get(FieldType.PAY_ID.getName()) + " and ORDER ID = "
					+ fields.get(FieldType.ORDER_ID.getName()));
		} else {
			for (Account accountThis : accounts) {
				if (accountThis.getAcquirerName().equalsIgnoreCase(
						AcquirerType.getInstancefromCode(AcquirerType.KOTAK_CARD.getCode()).getName())) {
					account = accountThis;
					break;
				}
			}
		}

		AccountCurrency accountCurrency = account.getAccountCurrency("356");

		keyMap.put("ADF1", accountCurrency.getAdf1());
		keyMap.put("ADF2", accountCurrency.getAdf2());
		keyMap.put("ADF3", accountCurrency.getAdf3());
		keyMap.put("ADF4", accountCurrency.getAdf4());

		keyMap.put("txnKey", accountCurrency.getTxnKey());
		keyMap.put("MerchantId", accountCurrency.getMerchantId());
		logger.info("data for otp submit"+new Gson().toJson(keyMap));

		

		return keyMap;

	}

	public Transaction toTransaction(String response) {
		Transaction transaction = new Transaction();

		JSONObject finalResponse = new JSONObject(response);

		transaction.setResponseMessage((String) finalResponse.get("ResponseMessage"));
		transaction.setResponseCode((String) finalResponse.get("ResponseCode"));
		if(finalResponse.has("Amount")) {

		transaction.setAmount((String) finalResponse.get("Amount"));
		}
		if(finalResponse.has("RetRefNo")) {
		transaction.setAcqId((String) finalResponse.get("RetRefNo"));
		}
		transaction.setMerchantReference((String) finalResponse.get("OrderId"));
		logger.info("transcation " + transaction.toString());
		return transaction;

	}

}
