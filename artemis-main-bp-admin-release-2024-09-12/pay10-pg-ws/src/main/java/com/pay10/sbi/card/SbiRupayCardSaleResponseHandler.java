package com.pay10.sbi.card;

import java.io.IOException;
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
import com.pay10.commons.util.PropertiesManager;
import com.pay10.commons.util.StatusType;
import com.pay10.commons.util.TransactionManager;
import com.pay10.pg.core.util.KotakCardCheckSum;
import com.pay10.pg.core.util.ProcessManager;
import com.pay10.pg.core.util.Processor;

@Service
public class SbiRupayCardSaleResponseHandler {

	private static Logger logger = LoggerFactory.getLogger(SbiRupayCardSaleResponseHandler.class.getName());

@Autowired
TransactionConverter transactionConverter;
	
	@Autowired
	@Qualifier("updateProcessor")
	private Processor updateProcessor;

	@Autowired
	private UserDao userDao;

	

	public Map<String, String> process(Fields fields) throws Exception {
		logger.info("fields -------->>  " + fields.getFieldsAsString());
		String newTxnId = TransactionManager.getNewTransactionId();
		fields.put(FieldType.TXN_ID.getName(), newTxnId);
		Transaction transactionResponse = new Transaction();

		Map<String, String> keyMap = getTxnKey(fields);
		fields.put(FieldType.ADF1.getName(), keyMap.get("ADF1"));
		fields.put(FieldType.ADF2.getName(), keyMap.get("ADF2"));
		fields.put(FieldType.ADF3.getName(), keyMap.get("ADF3"));
		fields.put(FieldType.ADF9.getName(), keyMap.get("ADF9"));

		fields.put(FieldType.ADF4.getName(), keyMap.get("ADF4"));
		fields.put(FieldType.TXN_KEY.getName(), keyMap.get("txnKey"));
		fields.put(FieldType.MERCHANT_ID.getName(), keyMap.get("MerchantId"));

		org.json.simple.JSONObject responseAuthorize = saleAuthorize(fields);
		logger.info("before sbi rupay card dual verification for PG_REF_NUM ="+fields.get(FieldType.PG_REF_NUM.getName())+ " and their authorize response =" + responseAuthorize);
		
		boolean doubleVer = doubleVerification(responseAuthorize, fields);
		logger.info("process:: doubleVerification={}, pgRefNo={} ", doubleVer,
				fields.get(FieldType.PG_REF_NUM.getName()));
		if (doubleVer) {
			transactionResponse = toTransaction(new JSONObject(responseAuthorize.toJSONString()));
			SbiCardTransformer  sbiTransformer = new SbiCardTransformer(transactionResponse);
		        sbiTransformer.updateResponse(fields);
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

	private org.json.simple.JSONObject saleAuthorize(Fields fields) {
		String responses2s="";
		org.json.simple.JSONObject pvResps2s=new org.json.simple.JSONObject();

		JSONObject requestS2s=new JSONObject();
		requestS2s.put("pgInstanceId",fields.get(FieldType.ADF9.getName()));
		requestS2s.put("merchantId", fields.get(FieldType.TXN_KEY.getName()));
		requestS2s.put("merchantReferenceNo",fields.get(FieldType.ORDER_ID.getName()));
		requestS2s.put("pgTransactionId", fields.get(FieldType.RRN.getName()));
		
	JSONObject	request=transactionConverter.prepareEncReq(fields, requestS2s, fields.get(FieldType.ADF1.getName()));
		
		String apiUrls2s = PropertiesManager.propertiesMap.get("SBI_AUTHORIZE_URL");
		logger.info("SBI_AUTHORIZE:: apiUrl={}, pgRefNo={}, req={}", apiUrls2s,fields.get(FieldType.PG_REF_NUM.getName()) , requestS2s);

		logger.info("SBI_AUTHORIZE:: apiUrl={}, pgRefNo={}, req={}", apiUrls2s,fields.get(FieldType.PG_REF_NUM.getName()) , request);
		try {
			responses2s = transactionConverter.executeApirupayotp(fields, request, responses2s, apiUrls2s);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		logger.info("SBI_AUTHORIZE:: response={}, pgRefNo={}", responses2s, fields.get(FieldType.PG_REF_NUM.getName()));
		try {
			 pvResps2s = transactionConverter.decryptResponse(responses2s);
		} catch (SystemException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		logger.info("RESPONSE FOR  SALE Authorize"+pvResps2s);
			return pvResps2s;
	}

	private boolean doubleVerification(org.json.simple.JSONObject responseAuthorize, Fields fields) {

		try {
			if (!(responseAuthorize.get("status").toString()).equalsIgnoreCase("50020")) {
				return true;
			}

			org.json.simple.JSONObject statusResponse = statusEnquiryRequest(fields);
			logger.info("dual verification::  sbi rupay card response." + statusResponse);
			// = //communicator.refundPostRequest(encrequest, hostUrl,fields);
			
			JSONObject response=new JSONObject(statusResponse.toJSONString());

			String status =  response.get("status").toString();
			String pgErrorCode  =  response.get("pgErrorCode").toString();
			
			String RRN =  response.get("transactionId").toString();

			logger.info("dual verification::  sbi rupay card response. Status={}", status);

			if (status != null && status.equalsIgnoreCase("50020")
					&& ("0").equalsIgnoreCase(pgErrorCode)
					&& RRN.equalsIgnoreCase(fields.get(FieldType.RRN.getName()))) {

				return true;
			}
			logger.info("doubleVerification:: failed. Status={}, resAmount={},pgRefNo={}", status);

			return false;
		}

		catch (Exception e) {
			logger.error("Exceptionn ", e);
			return false;
		}

	}

	private org.json.simple.JSONObject statusEnquiryRequest(Fields fields) {
		String responses2s = "";
		org.json.simple.JSONObject pvResps2s = new org.json.simple.JSONObject();

		JSONObject requestS2s = new JSONObject();
		requestS2s.put("pgInstanceId", fields.get(FieldType.ADF9.getName()));
		requestS2s.put("merchantId", fields.get(FieldType.TXN_KEY.getName()));
		requestS2s.put("merchantReferenceNo", fields.get(FieldType.ORDER_ID.getName()));
		requestS2s.put("amount", fields.get(FieldType.TOTAL_AMOUNT.getName()));
		requestS2s.put("currencyCode", fields.get(FieldType.CURRENCY_CODE.getName()));
		logger.info("Request, dual verification for sbi rupay card, PG_REF_NUM = "+fields.get(FieldType.PG_REF_NUM.getName())+" and plain-text request = "+ requestS2s);
		JSONObject request = transactionConverter.prepareEncReq(fields, requestS2s, fields.get(FieldType.ADF1.getName()));
		logger.info("Request, dual verification for sbi rupay card, PG_REF_NUM = "+fields.get(FieldType.PG_REF_NUM.getName())+" and encrypted request = "+ request);
		String apiUrls2s = PropertiesManager.propertiesMap.get("SBI_CARD_STATUS_ENQ_URL");
		logger.info("SBI_AUTHORIZE:: apiUrl={}, pgRefNo={}, req={}", apiUrls2s,
				fields.get(FieldType.PG_REF_NUM.getName()), requestS2s);

		logger.info("SBI_AUTHORIZE:: apiUrl={}, pgRefNo={}, req={}", apiUrls2s,
				fields.get(FieldType.PG_REF_NUM.getName()), request);
		try {
			responses2s = transactionConverter.executeApirupayotp(fields, request, responses2s, apiUrls2s);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		logger.info("Response, dual verification for sbi rupay card, PG_REF_NUM ="+fields.get(FieldType.PG_REF_NUM.getName())+ " and encrypted response ="+responses2s);
		try {
			pvResps2s = transactionConverter.decryptResponse(responses2s);
		} catch (SystemException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		logger.info("Response, dual verification for sbi rupay card, PG_REF_NUM =" +fields.get(FieldType.PG_REF_NUM.getName())+ " and plain-text response ="+ pvResps2s);
		return pvResps2s;

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
						AcquirerType.getInstancefromCode(AcquirerType.SBICARD.getCode()).getName())) {
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
		keyMap.put("ADF9", accountCurrency.getAdf9());

		keyMap.put("txnKey", accountCurrency.getTxnKey());
		keyMap.put("MerchantId", accountCurrency.getMerchantId());
		logger.info("data for otp submit"+new Gson().toJson(keyMap));

		

		return keyMap;

	}
	
	
	public Transaction toTransaction(JSONObject saleResponse) {
		Transaction transaction = new Transaction();
		String status = saleResponse.getInt("status") == 50020 ? "Success" : "Failed";
		transaction.setStatus(status);
		transaction.setAcqId(saleResponse.get("transactionId").toString());
		transaction.setResponseMessage(saleResponse.get("pgErrorCode").toString());
		transaction.setRef(saleResponse.getString("rrn"));
		transaction.setPayId(saleResponse.getString("rrn"));
		return transaction;
	}

	
}
