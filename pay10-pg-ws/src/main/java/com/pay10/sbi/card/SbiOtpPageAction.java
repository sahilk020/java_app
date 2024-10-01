package com.pay10.sbi.card;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.pay10.commons.dao.FieldsDao;
import com.pay10.commons.exception.SystemException;
import com.pay10.commons.user.Account;
import com.pay10.commons.user.AccountCurrency;
import com.pay10.commons.user.User;
import com.pay10.commons.user.UserDao;
import com.pay10.commons.util.AcquirerType;
import com.pay10.commons.util.FieldType;
import com.pay10.commons.util.Fields;
import com.pay10.commons.util.PropertiesManager;
@Component
public class SbiOtpPageAction {
	private static Logger logger = LoggerFactory.getLogger(SbiOtpPageAction.class.getName());

	@Autowired
	private FieldsDao fieldsDao;
	@Autowired
	private  UserDao userDao;
	
	@Autowired
	TransactionConverter transactionConverter;
	

	

	public Map<String, String> otpResend(Map<String, String> reqmap) {
		Map<String, String> response=new HashMap<String,String>();

		try {
			
			String responses2s="";
			
			Fields fields =getprivousedata( reqmap);

			JSONObject requestS2s=new JSONObject();
			requestS2s.put("pgInstanceId",fields.get(FieldType.ADF9.getName()));
			requestS2s.put("merchantId", fields.get(FieldType.TXN_KEY.getName()));
			requestS2s.put("merchantReferenceNo",fields.get(FieldType.ORDER_ID.getName()));
			requestS2s.put("pgTransactionId", fields.get(FieldType.RRN.getName()));
			requestS2s.put("cardHolderStatus", "NW");

			
		JSONObject	request=transactionConverter.prepareEncReq(fields, requestS2s, fields.get(FieldType.TXN_KEY.getName()));
			
			String apiUrls2s = PropertiesManager.propertiesMap.get("SBI_RESEND_OTP");
			logger.info("resendSubmit:: apiUrl={}, pgRefNo={}, req={}", apiUrls2s,fields.get(FieldType.PG_REF_NUM.getName()) , requestS2s);

			logger.info("resendSubmit:: apiUrl={}, pgRefNo={}, req={}", apiUrls2s,fields.get(FieldType.PG_REF_NUM.getName()) , request);
			responses2s = transactionConverter.executeApirupayotp(fields, request, responses2s, apiUrls2s);
			logger.info("preparePvReq:: response={}, pgRefNo={}", responses2s, fields.get(FieldType.PG_REF_NUM.getName()));
			org.json.simple.JSONObject pvResps2s = transactionConverter.decryptResponse(responses2s);
			String decResponse;

			
			
			
			 logger.info(" decrytp resonse for otpResend" + pvResps2s);
			 if(pvResps2s.get("errorcode").toString().equalsIgnoreCase("0")){
				 response.put("ResponseMessage","Otp Generate Successful");
					response.put("ResponseCode", (String)pvResps2s.get("errorcode"));

			 }else {
				 response.put("ResponseMessage","fail to Generate Otp");
					response.put("ResponseCode", (String)pvResps2s.get("errorcode"));
				 
			 }
			 
				
				
		} catch (Exception e) {
			// TODO Auto-generated catch block
		}
		return response;
	}

	public Map<String, String> otpSubmit(Map<String, String> reqmap) {
		Map<String, String> response=new HashMap<String,String>();

		try {
			
			String responses2s="";
			
			Fields fields =getprivousedata( reqmap);

			JSONObject requestS2s=new JSONObject();
			requestS2s.put("pgInstanceId",fields.get(FieldType.ADF9.getName()));
			requestS2s.put("merchantId", fields.get(FieldType.TXN_KEY.getName()));
			requestS2s.put("merchantReferenceNo",fields.get(FieldType.ORDER_ID.getName()));
			requestS2s.put("otp",reqmap.get("Otp") );
			requestS2s.put("pgTransactionId", fields.get(FieldType.RRN.getName()));
			
		JSONObject	request=transactionConverter.prepareEncReq(fields, requestS2s, fields.get(FieldType.TXN_KEY.getName()));
			
			String apiUrls2s = PropertiesManager.propertiesMap.get("SBI_VERIFY_OTP");
			logger.info("otpSubmit:: apiUrl={}, pgRefNo={}, req={}", apiUrls2s,fields.get(FieldType.PG_REF_NUM.getName()) , requestS2s);

			logger.info("otpSubmit:: apiUrl={}, pgRefNo={}, req={}", apiUrls2s,fields.get(FieldType.PG_REF_NUM.getName()) , request);
			responses2s = transactionConverter.executeApirupayotp(fields, request, responses2s, apiUrls2s);
			logger.info("preparePvReq:: response={}, pgRefNo={}", responses2s, fields.get(FieldType.PG_REF_NUM.getName()));
			org.json.simple.JSONObject pvResps2s = transactionConverter.decryptResponse(responses2s);
			String decResponse;

			
			
			
			 logger.info(" decrytp resonse for otpResend" + pvResps2s);
			 if(pvResps2s.get("errorcode").toString().equalsIgnoreCase("00")){
				 response.put("ResponseMessage","Otp Verify Successful");
					response.put("ResponseCode", (String)pvResps2s.get("errorcode"));

			 }else {
				 response.put("ResponseMessage","fail to Verify Otp");
					response.put("ResponseCode", (String)pvResps2s.get("errorcode"));
				 
			 }
			 
				
				
		} catch (Exception e) {
			// TODO Auto-generated catch block
		}
		return response;
	}

	public Fields getprivousedata(Map<String, String> reqmap) {
		Fields fields =new Fields();
		try {
			 fields = fieldsDao.getPrivousFieldsByOderId(reqmap.get("orderId"));
			Map<String, String> keyMap = getTxnKey(fields);

			fields.put(FieldType.ADF1.getName(), keyMap.get("ADF1"));
			fields.put(FieldType.ADF2.getName(), keyMap.get("ADF2"));
			fields.put(FieldType.ADF3.getName(), keyMap.get("ADF3"));
			fields.put(FieldType.ADF9.getName(), keyMap.get("ADF9"));

			fields.put(FieldType.ADF4.getName(), keyMap.get("ADF4"));
			fields.put(FieldType.TXN_KEY.getName(), keyMap.get("txnKey"));
			fields.put(FieldType.MERCHANT_ID.getName(), keyMap.get("MerchantId"));
		} catch (SystemException e) {
			// TODO Auto-generated catch block
		}
		return fields;
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

}
