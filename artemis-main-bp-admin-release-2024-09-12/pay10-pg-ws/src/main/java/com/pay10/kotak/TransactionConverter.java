package com.pay10.kotak;

import java.security.Key;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.amazonaws.internal.FIFOCache;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.MapType;
import com.google.gson.Gson;
import com.google.gson.internal.LinkedTreeMap;
import com.pay10.commons.exception.ErrorType;
import com.pay10.commons.exception.SystemException;
import com.pay10.commons.util.FieldType;
import com.pay10.commons.util.Fields;
import com.pay10.commons.util.MopType;
import com.pay10.commons.util.PropertiesManager;
import com.pay10.commons.util.StatusType;
import com.pay10.commons.util.TransactionType;
import com.pay10.pg.core.util.AcquirerTxnAmountProvider;
import com.pay10.pg.core.util.KotakCardCheckSum;
import com.pay10.pg.core.whitelabel.ReturnUrlCustomizer;

@Service("kotakTransactionConverter")
public class TransactionConverter {

	private static Logger logger = LoggerFactory.getLogger(TransactionConverter.class.getName());

	@Autowired
	KotakCardCheckSum kotakCardCheckSum;

	@Autowired
	private AcquirerTxnAmountProvider acquirerTxnAmountProvider;
	@Autowired
	private ReturnUrlCustomizer returnUrlCustomizer;

	@SuppressWarnings("incomplete-switch")
	public String perpareRequest(Fields fields, Transaction transaction) throws SystemException {

		String request = null;

		switch (TransactionType.getInstance(fields.get(FieldType.TXNTYPE.getName()))) {
		case AUTHORISE:
		case ENROLL:
			break;
		case REFUND:
			 request = refundRequest(fields, transaction);
			break;
		case SALE:
			request = saleRequest(fields, transaction);
			break;
		case CAPTURE:
			break;
		case STATUS:
			// request = statusEnquiryRequest(fields, transaction);
			break;
		}
		return request;

	}

	private String refundRequest(Fields fields, Transaction transaction) {
		try {
			if(StringUtils.isBlank(fields.get(FieldType.PG_REF_NUM.getName()))){
				fields.put(FieldType.PG_REF_NUM.getName(), fields.get(FieldType.TXN_ID.getName()));
				}
		String refundAmount = fields.get(FieldType.AMOUNT.getName());

		JSONObject json = new JSONObject();
		json.put("BankId", fields.get(FieldType.ADF2.getName()));//
		json.put("MerchantId", fields.get(FieldType.MERCHANT_ID.getName()));
		json.put("TerminalId", fields.get(FieldType.ADF3.getName()));
		json.put("OrderId", fields.get(FieldType.ORDER_ID.getName()));
		json.put("AccessCode", fields.get(FieldType.ADF4.getName()));//
		json.put("RefundAmount",refundAmount);
		json.put("RetRefNo", fields.get(FieldType.RRN.getName()));
		json.put("RefCancellationId", fields.get(FieldType.REFUND_ORDER_ID.getName()));
		json.put("RefReasonCode", "2360");
		json.put("TxnType", "Refund");
		HashMap<Object, Object> result = new ObjectMapper().readValue(json.toString(), HashMap.class);

		String hash = kotakCardCheckSum.getHashValue(result, fields.get(FieldType.ADF1.getName()));

		json.put("SecureHash", hash);
logger.info(json.toString());
		String encryptedString = kotakCardCheckSum.encrypt(json.toString(), fields.get(FieldType.TXN_KEY.getName()));
		JSONObject mainrequest = new JSONObject();
		mainrequest.put("bankId", fields.get(FieldType.ADF2.getName()));//
		mainrequest.put("merchantId", fields.get(FieldType.MERCHANT_ID.getName()));
		mainrequest.put("terminalId", fields.get(FieldType.ADF3.getName()));
		mainrequest.put("orderId", fields.get(FieldType.ORDER_ID.getName()));
		mainrequest.put("encData", encryptedString);
		String refundUrl=PropertiesManager.propertiesMap.get("KOTAKRefundURL");
		logger.info("refiund request  request " + mainrequest.toString()+refundUrl);

		Map<String, String> data = callRestApi(mainrequest.toString(), refundUrl);
		logger.info("refund  response" + data);

	String	response = kotakCardCheckSum.decrypt(data.get("encData"), fields.get(FieldType.TXN_KEY.getName()));
		logger.info("Dual verification response decdata" + response);
		return response;
		} catch (Exception e) {
			// TODO Auto-generated catch block
		}
		
		
		return null;
	}

	public String saleRequest(Fields fields, Transaction transaction) {
		String encryptedString = null;
		try {
			if(StringUtils.isBlank(fields.get(FieldType.PG_REF_NUM.getName()))){
				fields.put(FieldType.PG_REF_NUM.getName(), fields.get(FieldType.TXN_ID.getName()));
				}
			fields.put(FieldType.TOTAL_AMOUNT.getName(),(fields.get(FieldType.TOTAL_AMOUNT.getName()).replace(".", "")));
			logger.info("fields data " + fields.getFieldsAsString());
			String key = fields.get(FieldType.TXN_KEY.getName());
			String amount = fields.get(FieldType.TOTAL_AMOUNT.getName());

			String returnUrl = returnUrlCustomizer.customizeReturnUrl(fields,
					PropertiesManager.propertiesMap.get(Constants.SALE_CARD_RETURN_URL));

			returnUrl = returnUrl + "?MerchantId=" + fields.get(FieldType.MERCHANT_ID.getName()) + "&TerminalId="
					+ fields.get(FieldType.ADF3.getName()) + "&BankId=" + fields.get(FieldType.ADF2.getName())
					+ "&OrderId=" + fields.get(FieldType.ORDER_ID.getName());
			Map<String, String> responseAuthentication = saleAuthentication(fields, transaction);
			logger.info("return url " + returnUrl);
		String	encryptedStringres = kotakCardCheckSum.decrypt(responseAuthentication.get("encData"), key);
			logger.info("encryptedString  response " + encryptedStringres);
			JSONObject response = new JSONObject(encryptedStringres);
		if (response.get("ResponseCode").equals("00")) {
			fields.put(FieldType.STATUS.getName(), StatusType.SENT_TO_BANK.getName());
			fields.put(FieldType.RESPONSE_CODE.getName(), ErrorType.SUCCESS.getResponseCode());
			fields.put(FieldType.RESPONSE_MESSAGE.getName(), ErrorType.SUCCESS.getResponseMessage());
		}else {
			fields.put(FieldType.STATUS.getName(), StatusType.INVALID.getName());
			fields.put(FieldType.RESPONSE_CODE.getName(), ErrorType.AUTHENTICATION_UNAVAILABLE.getResponseCode());
			fields.put(FieldType.RESPONSE_MESSAGE.getName(), ErrorType.AUTHENTICATION_UNAVAILABLE.getResponseMessage());
		}
		logger.info("AuthenticationData  response " + response.get("AuthenticationData").toString());
		fields.put(FieldType.ACQ_ID.getName(), responseAuthentication.get("pgId"));
		JSONObject Htmljson = new JSONObject(response.get("AuthenticationData").toString());
		logger.info("AuthenticationData  response Htmljson " + Htmljson);
		
		if(!(fields.get(FieldType.MOP_TYPE.getName()).equalsIgnoreCase("RU"))&&(Htmljson.has("OTPRederingContent"))) {
		logger.info(
				"AuthenticationData  response Htmljson OTPRederingContent" + Htmljson.get("OTPRederingContent"));

	String	OtpRenderpage = (String) Htmljson.get("OTPRederingContent");
		fields.put(FieldType.KOTAK_FINAL_REQUEST.getName(), OtpRenderpage);
		logger.info("AuthenticationData  response " + fields.getFieldsAsString());

		logger.info("AuthenticationData  response " + OtpRenderpage);
		}
		
		// Map<String, String> responseAuthorize=saleAuthorize(fields, transaction
		// ,responseAuthentication);

		} catch (Exception e) {

			e.printStackTrace();
		}
		return encryptedString;

	}

	String saleAuthorize(Fields fields, String response) throws Exception {

		Map<String, String> responsesale = Arrays
				.stream(response.replace("{", "").replace("}", "").replace(" ", "").split(",")).map(s -> s.split("="))
				.collect(Collectors.toMap(s -> s[0], s -> s[1]));

		String amount = acquirerTxnAmountProvider.amountProvider(fields);
		String returnUrl = PropertiesManager.propertiesMap.get(Constants.SALE_CARD_RETURN_URL);
		String salt = fields.get(FieldType.ADF1.getName());
		String encryptionKey = fields.get(FieldType.TXN_KEY.getName());
		logger.info("Encrytion key and salt for saleauthentication" + salt + encryptionKey);
		JSONObject json = new JSONObject();
		json.put("BankId", fields.get(FieldType.ADF2.getName()));//
		json.put("MerchantId", fields.get(FieldType.MERCHANT_ID.getName()));
		json.put("TerminalId", fields.get(FieldType.ADF3.getName()));
		json.put("OrderId", fields.get(FieldType.ORDER_ID.getName()));
		json.put("AccessCode", fields.get(FieldType.ADF4.getName()));//
		json.put("PgId", fields.get(FieldType.ACQ_ID.getName()));
		json.put("CRes", responsesale.get("cres"));

		HashMap<Object, Object> result = new ObjectMapper().readValue(json.toString(), HashMap.class);

		logger.info(" raw hash for authentication sale card" + result);

		String hash = kotakCardCheckSum.getHashValue(result, salt);
		logger.info("hash for authentication sale card" + hash);

		json.put("SecureHash", hash);
		logger.info("raw request for sale in saleauthentication " + json);
		String encryptedString = kotakCardCheckSum.encrypt(json.toString(), encryptionKey);

		JSONObject mainrequest = new JSONObject();
		mainrequest.put("bankId", fields.get(FieldType.ADF2.getName()));//
		mainrequest.put("merchantId", fields.get(FieldType.MERCHANT_ID.getName()));
		mainrequest.put("terminalId", fields.get(FieldType.ADF3.getName()));
		mainrequest.put("orderId", fields.get(FieldType.ORDER_ID.getName()));
		mainrequest.put("encData", encryptedString);
		
		String urlAuthent = PropertiesManager.propertiesMap.get(Constants.SALE_CARD_Authorize);
		logger.info("request and Url"+mainrequest+urlAuthent);

		Map<String, String> data = callRestApi(mainrequest.toString(), urlAuthent);
		
		String decResponse = kotakCardCheckSum.decrypt(data.get("encData"), encryptionKey);
		logger.info(" decrytp resonse for AuthoriZe"+decResponse);
		return decResponse;

	}

	private Map<String, String> saleAuthentication(Fields fields, Transaction transaction) throws Exception {
		String amount = fields.get(FieldType.TOTAL_AMOUNT.getName());
		String returnUrl = PropertiesManager.propertiesMap.get(Constants.SALE_CARD_RETURN_URL);
		returnUrl = returnUrl +  "?OrderId="
				+ fields.get(FieldType.ORDER_ID.getName())+"&MerchantId=" + fields.get(FieldType.MERCHANT_ID.getName()) + "&TerminalId="
				+ fields.get(FieldType.ADF3.getName()) + "&BankId=" + fields.get(FieldType.ADF2.getName());
		String salt = fields.get(FieldType.ADF1.getName());
		String encryptionKey = fields.get(FieldType.TXN_KEY.getName());
		logger.info("Encrytion key and salt for saleauthentication" + salt + encryptionKey);
		JSONObject json = new JSONObject();
		json.put("BankId", fields.get(FieldType.ADF2.getName()));//
		json.put("MerchantId", fields.get(FieldType.MERCHANT_ID.getName()));
		json.put("TerminalId", fields.get(FieldType.ADF3.getName()));
		json.put("OrderId", fields.get(FieldType.ORDER_ID.getName()));
		json.put("MCC", fields.get(FieldType.ADF5.getName()));
		json.put("AccessCode", fields.get(FieldType.ADF4.getName()));//
		json.put("Command", "Pay");
		json.put("Currency", fields.get(FieldType.CURRENCY_CODE.getName()));
		json.put("Amount", amount);//
		if(fields.get(FieldType.PAYMENT_TYPE.getName()).equalsIgnoreCase("CC")){
			json.put("PaymentOption", "cc");

		}
if(fields.get(FieldType.PAYMENT_TYPE.getName()).equalsIgnoreCase("DC")){
	json.put("PaymentOption", "dc");

		}
			json.put("IpAddress", fields.get(FieldType.INTERNAL_CUST_IP.getName()));

		
		if (StringUtils.isNotBlank(fields.get(FieldType.BROWSER_USER_AGENT.getName()))) {
			json.put("UserAgent", fields.get(FieldType.BROWSER_USER_AGENT.getName()));

		} else {
			String UserAgent=PropertiesManager.propertiesMap.get("USERAGENT");
			json.put("UserAgent", UserAgent);

		}
		if (StringUtils.isNotBlank(fields.get(FieldType.BROWSER_ACCEPT_HEADER.getName()))) {
			json.put("AcceptHeader",fields.get(FieldType.BROWSER_ACCEPT_HEADER.getName()));

		} else {
			String ACCEPTHEADER=PropertiesManager.propertiesMap.get("ACCEPTHEADER");
			json.put("AcceptHeader",ACCEPTHEADER);

		}
		
		String BROWSERDETAILS=PropertiesManager.propertiesMap.get("BROWSERDETAILS");

		json.put("BrowserDetails", BROWSERDETAILS);
		json.put("CardNumber",fields.get(FieldType.CARD_NUMBER.getName()));
		json.put("ExpiryDate", fields.get(FieldType.CARD_EXP_DT.getName()));
		json.put("CVV", fields.get(FieldType.CVV.getName()));
		json.put("AuthenticationResponseURL", returnUrl);//
		json.put("browserTZ", "480");
		json.put("browserScreenHeight", "400");
		json.put("browserScreenWidth", "250");
		json.put("browserJavaEnabled", "false");
		json.put("browserColorDepth", "8");//
		json.put("browserJavascriptEnabled", "true");

		HashMap<Object, Object> result = new ObjectMapper().readValue(json.toString(), HashMap.class);

		logger.info(" raw hash for authentication sale card" + result);

		String hash = kotakCardCheckSum.getHashValue(result, salt);
		logger.info("hash for authentication sale card" + hash);

		json.put("SecureHash", hash);
		logger.info("raw request for sale in saleauthentication " + json);
		String encryptedString = KotakCardCheckSum.encrypt(json.toString(), encryptionKey);

		JSONObject mainrequest = new JSONObject();
		mainrequest.put("bankId", fields.get(FieldType.ADF2.getName()));//
		mainrequest.put("merchantId", fields.get(FieldType.MERCHANT_ID.getName()));
		mainrequest.put("terminalId", fields.get(FieldType.ADF3.getName()));
		mainrequest.put("orderId", fields.get(FieldType.ORDER_ID.getName()));
		mainrequest.put("encData", encryptedString);
		String urlAuthentication = PropertiesManager.propertiesMap.get(Constants.SALE_CARD_Authentication);
		
		logger.info("111111111111111111111"+mainrequest+urlAuthentication);
		Map<String, String> data = callRestApi(mainrequest.toString(), urlAuthentication);

		return data;

	}

	public void logRequest(String requestMessage, Fields fields) {
		log("Request message to KOTAK bank: Url= " + requestMessage, fields);
	}

	private void log(String message, Fields fields) {
		message = Pattern.compile("(<CardNumber>)([\\s\\S]*?)(</card>)").matcher(message).replaceAll("$1$3");
		message = Pattern.compile("(<ExpiryDate>)([\\s\\S]*?)(</pan>)").matcher(message).replaceAll("$1$3");
		message = Pattern.compile("(<CardSecurityCode>)([\\s\\S]*?)(</expmonth>)").matcher(message).replaceAll("$1$3");
		message = Pattern.compile("(<TerminalId>)([\\s\\S]*?)(</expyear>)").matcher(message).replaceAll("$1$3");
		message = Pattern.compile("(<PassCode>)([\\s\\S]*?)(</cvv2>)").matcher(message).replaceAll("$1$3");
		// message =
		// Pattern.compile("(<password>)([\\s\\S]*?)(</password>)").matcher(message).replaceAll("$1$3");
		MDC.put(FieldType.INTERNAL_CUSTOM_MDC.getName(), fields.getCustomMDC());
		logger.info(message);
	}

	public Map<String, String> callRestApi(String json, String serviceUrl) {
		String response = "";
		Map<String, String> resMap = new HashMap<String, String>();

		try {
			String responseBody = "";
			CloseableHttpClient httpClient = HttpClientBuilder.create().build();
			HttpPost request = new HttpPost(serviceUrl);
			StringEntity params = new StringEntity(json.toString());
			request.addHeader("content-type", "application/json");
			request.setEntity(params);
			HttpResponse resp = httpClient.execute(request);
			responseBody = EntityUtils.toString(resp.getEntity());
			final ObjectMapper mapper = new ObjectMapper();
			final MapType type = mapper.getTypeFactory().constructMapType(Map.class, String.class, Object.class);
			resMap = mapper.readValue(responseBody, type);
			logger.info(" kotak bank NB Response for refund transaction >> " + resMap);

		} catch (Exception e) {
			logger.info("Exception in getting Refund respose for kotak bank NB" + e);

			return resMap;
		}
		return resMap;
	}


}
