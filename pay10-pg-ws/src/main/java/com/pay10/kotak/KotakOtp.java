package com.pay10.kotak;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pay10.commons.dao.FieldsDao;
import com.pay10.commons.exception.SystemException;
import com.pay10.commons.util.FieldType;
import com.pay10.commons.util.Fields;
import com.pay10.commons.util.PropertiesManager;
import com.pay10.pg.core.util.KotakCardCheckSum;
@Component
public class KotakOtp {
	private static Logger logger = LoggerFactory.getLogger(KotakOtp.class.getName());

	@Autowired
	private KotakCardCheckSum kotakCardCheckSum;
	@Autowired
	private FieldsDao fieldsDao;

	@Qualifier("kotakTransactionConverter")
	@Autowired
	TransactionConverter transactionConverter;

	@Autowired
	private KotakCardSaleResponseHandler kotakCardSaleResponseHandler;

	public Map<String, String> otpResend(Map<String, String> reqmap) {
		Map<String, String> response=new HashMap<String,String>();
		try {
			Fields fields =getprivousedata( reqmap);

			logger.info("get fields data in optresend" + fields.getFieldsAsString());
			String salt = fields.get(FieldType.ADF1.getName());
			String encryptionKey = fields.get(FieldType.TXN_KEY.getName());
			JSONObject json = new JSONObject();
			json.put("BankId", fields.get(FieldType.ADF2.getName()));//
			json.put("MerchantId", fields.get(FieldType.MERCHANT_ID.getName()));
			json.put("TerminalId", fields.get(FieldType.ADF3.getName()));
			json.put("OrderId", fields.get(FieldType.ORDER_ID.getName()));
			json.put("AccessCode", fields.get(FieldType.ADF4.getName()));//
			json.put("PgId", fields.get(FieldType.ACQ_ID.getName()));

			HashMap<Object, Object> result = new ObjectMapper().readValue(json.toString(), HashMap.class);

			logger.info(" raw hash for  otpResend card" + result);

			String hash = kotakCardCheckSum.getHashValue(result, salt);
			logger.info("hash for authentication sale card" + hash);

			json.put("SecureHash", hash);
			logger.info("raw request for otpResend " + json);
			String encryptedString = kotakCardCheckSum.encrypt(json.toString(), encryptionKey);

			JSONObject mainrequest = new JSONObject();
			mainrequest.put("bankId", fields.get(FieldType.ADF2.getName()));//
			mainrequest.put("merchantId", fields.get(FieldType.MERCHANT_ID.getName()));
			mainrequest.put("terminalId", fields.get(FieldType.ADF3.getName()));
			mainrequest.put("orderId", fields.get(FieldType.ORDER_ID.getName()));
			mainrequest.put("encData", encryptedString);

			String urlAuthent = PropertiesManager.propertiesMap.get("KOTAKOTRESENDURL");
			logger.info("otpResend request and Url" + mainrequest + urlAuthent);

			Map<String, String> data = transactionConverter.callRestApi(mainrequest.toString(), urlAuthent);

			String decResponse = kotakCardCheckSum.decrypt(data.get("encData"), encryptionKey);
			logger.info(" decrytp resonse for otpSubmit" + decResponse);

			JSONObject responsedata=new JSONObject(decResponse);
			
				
		 logger.info(" decrytp resonse for otpResend" + decResponse);
		 
			response.put("ResponseMessage",(String) responsedata.get("ResponseMessage"));
			response.put("ResponseCode", (String)responsedata.get("ResponseCode"));


		 
		} catch (Exception e) {
			// TODO Auto-generated catch block
		}
		return response;

	}

	public Map<String, String> otpSubmit(Map<String, String> reqmap) {
		Map<String, String> response=new HashMap<String,String>();

		try {
			Fields fields =getprivousedata( reqmap);

			logger.info("get fields data in otpSubmit" + fields.getFieldsAsString());
			String salt = fields.get(FieldType.ADF1.getName());
			String encryptionKey = fields.get(FieldType.TXN_KEY.getName());
			JSONObject json = new JSONObject();
			json.put("BankId", fields.get(FieldType.ADF2.getName()));//
			json.put("MerchantId", fields.get(FieldType.MERCHANT_ID.getName()));
			json.put("TerminalId", fields.get(FieldType.ADF3.getName()));
			json.put("OrderId", fields.get(FieldType.ORDER_ID.getName()));
			json.put("AccessCode", fields.get(FieldType.ADF4.getName()));//
			json.put("PgId", fields.get(FieldType.ACQ_ID.getName()));
			json.put("OTP", reqmap.get("otp"));

			HashMap<Object, Object> result = new ObjectMapper().readValue(json.toString(), HashMap.class);

			logger.info(" raw hash for  otpSubmit card" + result);

			String hash = kotakCardCheckSum.getHashValue(result, salt);
			logger.info("hash for otpSubmit  " + hash);

			json.put("SecureHash", hash);
			logger.info("raw request for otpSubmit " + json);
			String encryptedString = kotakCardCheckSum.encrypt(json.toString(), encryptionKey);

			JSONObject mainrequest = new JSONObject();
			mainrequest.put("bankId", fields.get(FieldType.ADF2.getName()));//
			mainrequest.put("merchantId", fields.get(FieldType.MERCHANT_ID.getName()));
			mainrequest.put("terminalId", fields.get(FieldType.ADF3.getName()));
			mainrequest.put("orderId", fields.get(FieldType.ORDER_ID.getName()));
			mainrequest.put("encData", encryptedString);

			String urlAuthent = PropertiesManager.propertiesMap.get("KOTAKOTSUBMITDURL");
			logger.info("otpSubmit request and Url" + mainrequest + urlAuthent);

			Map<String, String> data = transactionConverter.callRestApi(mainrequest.toString(), urlAuthent);

			String decResponse;

			decResponse = kotakCardCheckSum.decrypt(data.get("encData"), encryptionKey);
			JSONObject responsedata=new JSONObject(decResponse);
			
			
			 logger.info(" decrytp resonse for otpResend" + decResponse);
			 
				response.put("ResponseMessage",(String) responsedata.get("ResponseMessage"));
				response.put("ResponseCode", (String)responsedata.get("ResponseCode"));

				
		} catch (Exception e) {
			// TODO Auto-generated catch block
		}
		return response;
	}

	public Fields getprivousedata(Map<String, String> reqmap) {
		Fields fields =new Fields();
		try {
			 fields = fieldsDao.getPrivousFieldsByOderId(reqmap.get("orderId"));
			Map<String, String> keyMap = kotakCardSaleResponseHandler.getTxnKey(fields);

			fields.put(FieldType.ADF1.getName(), keyMap.get("ADF1"));
			fields.put(FieldType.ADF2.getName(), keyMap.get("ADF2"));
			fields.put(FieldType.ADF3.getName(), keyMap.get("ADF3"));

			fields.put(FieldType.ADF4.getName(), keyMap.get("ADF4"));
			fields.put(FieldType.TXN_KEY.getName(), keyMap.get("txnKey"));
			fields.put(FieldType.MERCHANT_ID.getName(), keyMap.get("MerchantId"));
		} catch (SystemException e) {
			// TODO Auto-generated catch block
		}
		return fields;
	}

}
