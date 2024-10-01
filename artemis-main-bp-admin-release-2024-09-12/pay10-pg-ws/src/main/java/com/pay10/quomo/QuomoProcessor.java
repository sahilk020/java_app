package com.pay10.quomo;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.bind.DatatypeConverter;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.MapType;
import com.pay10.commons.exception.ErrorType;
import com.pay10.commons.exception.SystemException;
import com.pay10.commons.user.QuomoCurrencyConfiguration;
import com.pay10.commons.user.UserDao;
import com.pay10.commons.util.AcquirerType;
import com.pay10.commons.util.FieldType;
import com.pay10.commons.util.Fields;
import com.pay10.commons.util.PropertiesManager;
import com.pay10.commons.util.StatusType;
import com.pay10.commons.util.TransactionType;
import com.pay10.pg.core.util.AcquirerTxnAmountProvider;
import com.pay10.pg.core.util.Processor;

@Service("quomoProcessor")
public class QuomoProcessor implements Processor {
	private static final Logger logger = LoggerFactory.getLogger(QuomoProcessor.class.getName());
	private static final DecimalFormat df = new DecimalFormat("#.00");
	@Autowired
	private AcquirerTxnAmountProvider acquirerTxnAmountProvider;

	@Autowired
	UserDao userDao;
	
	@Override
	public void preProcess(Fields fields) throws SystemException {

	}

	@Override
	public void process(Fields fields) throws SystemException {
		if ((fields.get(FieldType.TXNTYPE.getName()).equals(TransactionType.NEWORDER.getName())
				|| fields.get(FieldType.INTERNAL_ORIG_TXN_TYPE.getName()).equals(TransactionType.STATUS.getName())
				|| fields.get(FieldType.INTERNAL_ORIG_TXN_TYPE.getName()).equals(TransactionType.VERIFY.getName())
				|| fields.get(FieldType.INTERNAL_ORIG_TXN_TYPE.getName()).equals(TransactionType.RECO.getName())
				|| fields.get(FieldType.INTERNAL_ORIG_TXN_TYPE.getName())
						.equals(TransactionType.REFUNDRECO.getName()))) {
			return;
		}

		if (!fields.get(FieldType.ACQUIRER_TYPE.getName()).equals(AcquirerType.QUOMO.getCode())) {
			return;
		}
		try {
			send(fields);
		} catch (Exception e) {
			e.printStackTrace();
			logger.info("Exception occured in QuomoProcessor, send() " + e);
		}

	}

	@Override
	public void postProcess(Fields fields) throws SystemException {

	}

	public void send(Fields fields) throws Exception {
		logger.info("Request Received For Create Sale Request for Quomo , OrderId={}, fields={}", 
				fields.get(FieldType.ORDER_ID.getName()), fields.getFieldsAsString());
		String pgRefNo = fields.get(FieldType.PG_REF_NUM.getName());
        if (pgRefNo == null) {
            fields.put(FieldType.PG_REF_NUM.getName(), fields.get(FieldType.TXN_ID.getName()));
        }
        
		if (fields.get(FieldType.TXNTYPE.getName()).equalsIgnoreCase(TransactionType.ENROLL.getName())) {
			fields.put(FieldType.TXNTYPE.getName(), TransactionType.SALE.getName());
		}

		String txnType = fields.get(FieldType.TXNTYPE.getName());

		if (txnType.equals(TransactionType.SALE.getName())) {
			fields.put(FieldType.ORIG_TXN_ID.getName(), fields.get(FieldType.TXN_ID.getName()));
			String request = saleRequest(fields);
			logger.info("Quomo Sale Request={}, fields {}", request, fields);
			updateSaleResponse(fields, request);
		}
	}

//	public String saleRequest(Fields fields) throws Exception {
//
//		String paymentType = fields.get(FieldType.PAYMENT_TYPE.getName());
//		String mopType = fields.get(FieldType.MOP_TYPE.getName());
//		String currency = fields.get(FieldType.CURRENCY_CODE.getName());
//		String acquirer = fields.get(FieldType.ACQUIRER_TYPE.getName());
//		logger.info("Quomo saleRequest, acquirer={}, currency={}, paymentType={}, mopType={}",acquirer,currency,paymentType,mopType);
//		QuomoCurrencyConfiguration quomoCurrencyConfiguration = userDao.getQuomoCurrencyConfiguration(acquirer, paymentType, mopType, currency); 
//		logger.info("quomoCurrencyConfiguration "+quomoCurrencyConfiguration.toString());
//		
//		String amount = df.format(Double.valueOf(fields.get(FieldType.TOTAL_AMOUNT.getName())) / 100);
//		if(amount.contains(".00")) {
//			amount = amount.split("\\.")[0];
//		} 
//		logger.info("Quomo saleRequest, amount :: "+amount);
//		
//		if (quomoCurrencyConfiguration.getAcquirer() == null) {
//			fields.put(FieldType.STATUS.getName(), StatusType.INVALID.getName());
//			fields.put(FieldType.PG_TXN_MESSAGE.getName(), "Currency not supported!!");
//			logger.error("This currency not mapped with merchant, currnecy code = " + currency);
//			throw new SystemException(ErrorType.CURRENCY_NOT_SUPPORTED,
//					"Merchant not supported for this currency type payId= " + fields.get(FieldType.PAY_ID.getName()));
//
//		}
//		Map<String, Object> reqmap = new HashMap<String, Object>();
//		reqmap.put("md5Key", fields.get(FieldType.ADF1.getName())); //"APIKEYMD51800628664C1F0B6BBAF2"
//		reqmap.put("merchantId", fields.get(FieldType.MERCHANT_ID.getName())); //"18006286"
//		reqmap.put("businessEmail", "sonu@pay10.com"); //"sonu@pay10.com"
//		reqmap.put("productName", "Mobile"); //"Mobile"
//		reqmap.put("orderId", fields.get(FieldType.PG_REF_NUM.getName())); //String.valueOf(System.currentTimeMillis() / 1000L)
//		reqmap.put("depositMethodId", Integer.valueOf(quomoCurrencyConfiguration.getAccountId())); //9
//		reqmap.put("bankId", quomoCurrencyConfiguration.getBankId()); //"dp-ucpb"
//		reqmap.put("depositAmount", amount); //500
//		reqmap.put("currency", quomoCurrencyConfiguration.getCurrency());//VND // PHP 
//		reqmap.put("customerName", fields.get(FieldType.CUST_NAME.getName())); //"sonuchaudhari"
//		reqmap.put("customerIp", fields.get(FieldType.INTERNAL_CUST_IP.getName())); //"3.7.144.169"
//		reqmap.put("customerEmail", fields.get(FieldType.CUST_EMAIL.getName())); //"sonu@pay10.com"
//		reqmap.put("customerPhoneNo", fields.get(FieldType.CUST_PHONE.getName())); //"9767146866"
//		reqmap.put("customerAddress", "mumbai"); //"mumbai"
//		reqmap.put("note", "test");
//		reqmap.put("websiteUrl", "www.pay10.com"); //"www.pay10.com"
//		reqmap.put("requestTime", String.valueOf(System.currentTimeMillis() / 1000L));
//		reqmap.put("successUrl", "https://bpgate.nxtpay.in/pgui/jsp/quomoResponse"); //"https://uat.pay10.com/pgui/jsp/quomoResponse"
//		reqmap.put("failUrl", "https://bpgate.nxtpay.in/pgui/jsp/quomoResponse"); //"https://uat.pay10.com/pgui/jsp/quomoResponse"
//		reqmap.put("callbackNotiUrl", "https://bpgate.nxtpay.in/pgui/jsp/quomoNotificationResponse"); //"https://uat.pay10.com/pgui/jsp/quomoNotificationResponse"
//		reqmap.put("API_USER_NAME", fields.get(FieldType.ADF2.getName())); //"lmp@gateway.quomo.digital"
//		reqmap.put("API_PASSWORD", fields.get(FieldType.ADF3.getName())); //"lmp@pa$$ap!^wmDB39zt6grvP$dHXCB"
//		reqmap.put("URL", "https://gateway.quomo.digital/api/fundin/deposit"); //"https://gateway.quomo.digital/api/fundin/deposit"
//
//		JSONObject jsonRequest = new JSONObject();
//		jsonRequest.put("merchant_id", String.valueOf(reqmap.get("merchantId")));
//		jsonRequest.put("business_email", String.valueOf(reqmap.get("businessEmail")));
//		jsonRequest.put("product_name", String.valueOf(reqmap.get("productName")));
//		jsonRequest.put("order_id", String.valueOf(reqmap.get("orderId")));
//		jsonRequest.put("deposit_method_id", reqmap.get("depositMethodId"));
//		jsonRequest.put("bank_id", String.valueOf(reqmap.get("bankId")));
//		jsonRequest.put("deposit_amount", reqmap.get("depositAmount"));
//		jsonRequest.put("currency", String.valueOf(reqmap.get("currency")));
//		jsonRequest.put("customer_name", String.valueOf(reqmap.get("customerName")));
//		jsonRequest.put("card_number", "NA");
//		jsonRequest.put("card_month", "NA");
//		jsonRequest.put("card_year", "NA");
//		jsonRequest.put("card_cvv", "NA");
//		jsonRequest.put("customer_ip", String.valueOf(reqmap.get("customerIp")));
//		jsonRequest.put("customer_email", String.valueOf(reqmap.get("customerEmail")));
//		jsonRequest.put("customer_phone_no", String.valueOf(reqmap.get("customerPhoneNo")));
//		jsonRequest.put("customer_address", String.valueOf(reqmap.get("customerAddress")));
//		jsonRequest.put("note", String.valueOf(reqmap.get("note")));
//		jsonRequest.put("website_url", String.valueOf(reqmap.get("websiteUrl")));
//		jsonRequest.put("request_time", String.valueOf(reqmap.get("requestTime")));
//		jsonRequest.put("success_url", String.valueOf(reqmap.get("successUrl")));
//		jsonRequest.put("fail_url", String.valueOf(reqmap.get("failUrl")));
//		jsonRequest.put("callback_noti_url", String.valueOf(reqmap.get("callbackNotiUrl")));
//
//		jsonRequest.put("sign_data", signData(reqmap));
//
//		logger.info("Final Request :: " + jsonRequest);
//		String resp = sendPost(jsonRequest.toString().replace("\\", ""));
//		logger.info("Quomo Response =" + resp);
//		
//		if (isValid(resp)) {
//			JSONObject jsonResp = new JSONObject(resp);
//			if (jsonResp.has("errCode") && jsonResp.getInt("errCode") == 0) {
//				if (jsonResp.has("redirect_url")) {
//					return String.valueOf(jsonResp.get("redirect_url"));
//				} else if (jsonResp.has("next_action")) {
//					return String.valueOf(jsonResp.get("next_action"));
//				} else {
//					updateFailedSaleResponse(fields, jsonResp.has("error") ? jsonResp.getString("error") : "");
//					throw new SystemException(ErrorType.ACUIRER_DOWN, ErrorType.ACUIRER_DOWN.getResponseCode());
//				}
//			} else {
//				updateFailedSaleResponse(fields, jsonResp.has("error") ? jsonResp.getString("error") : "");
//				throw new SystemException(ErrorType.ACUIRER_DOWN, ErrorType.ACUIRER_DOWN.getResponseCode());
//			}
//
//		} else {
//			updateFailedSaleResponse(fields, "");
//			throw new SystemException(ErrorType.ACUIRER_DOWN, ErrorType.ACUIRER_DOWN.getResponseCode());
//		}
//	}

	
	public String saleRequest(Fields fields) throws Exception {
	 
			String paymentType = fields.get(FieldType.PAYMENT_TYPE.getName());

			String mopType = fields.get(FieldType.MOP_TYPE.getName());

			String currency = fields.get(FieldType.CURRENCY_CODE.getName());

			String acquirer = fields.get(FieldType.ACQUIRER_TYPE.getName());

			logger.info("Quomo saleRequest, acquirer={}, currency={}, paymentType={}, mopType={}",acquirer,currency,paymentType,mopType);

			QuomoCurrencyConfiguration quomoCurrencyConfiguration = userDao.getQuomoCurrencyConfiguration(acquirer, paymentType, mopType, currency); 

			logger.info("quomoCurrencyConfiguration "+quomoCurrencyConfiguration.toString());

			String amount = df.format(Double.valueOf(fields.get(FieldType.TOTAL_AMOUNT.getName())) / 100);

			if(amount.contains(".00")) {

				amount = amount.split("\\.")[0];

			} 

			logger.info("Quomo saleRequest, amount :: "+amount);

			if (quomoCurrencyConfiguration.getAcquirer() == null) {

				fields.put(FieldType.STATUS.getName(), StatusType.INVALID.getName());

				fields.put(FieldType.PG_TXN_MESSAGE.getName(), "Currency not supported!!");

				logger.error("This currency not mapped with merchant, currnecy code = " + currency);

				throw new SystemException(ErrorType.CURRENCY_NOT_SUPPORTED,

						"Merchant not supported for this currency type payId= " + fields.get(FieldType.PAY_ID.getName()));
	 
			}

			Map<String, Object> reqmap = new HashMap<String, Object>();

			reqmap.put("md5Key", fields.get(FieldType.ADF1.getName())); //"APIKEYMD51800628664C1F0B6BBAF2"

			reqmap.put("merchantId", fields.get(FieldType.MERCHANT_ID.getName())); //"18006286"

			reqmap.put("businessEmail", "sonu@pay10.com"); //"sonu@pay10.com"

			reqmap.put("productName", "Mobile"); //"Mobile"

			reqmap.put("orderId", fields.get(FieldType.PG_REF_NUM.getName())); //String.valueOf(System.currentTimeMillis() / 1000L)

			reqmap.put("depositMethodId", Integer.valueOf(quomoCurrencyConfiguration.getAccountId())); //9

			reqmap.put("bankId", quomoCurrencyConfiguration.getBankId()); //"dp-ucpb"

			reqmap.put("depositAmount", amount); //500

			reqmap.put("currency", quomoCurrencyConfiguration.getCurrency());//VND // PHP 

			reqmap.put("customerName", fields.get(FieldType.CUST_NAME.getName())); //"sonuchaudhari"

			reqmap.put("customerIp", fields.get(FieldType.INTERNAL_CUST_IP.getName())); //"3.7.144.169"

			reqmap.put("customerEmail", fields.get(FieldType.CUST_EMAIL.getName())); //"sonu@pay10.com"

			reqmap.put("customerPhoneNo", fields.get(FieldType.CUST_PHONE.getName())); //"9767146866"

			reqmap.put("customerAddress", "mumbai"); //"mumbai"

			reqmap.put("note", "test");

			reqmap.put("websiteUrl", "www.pay10.com"); //"www.pay10.com"

			reqmap.put("requestTime", String.valueOf(System.currentTimeMillis() / 1000L));

			reqmap.put("successUrl", PropertiesManager.propertiesMap.get("QUOMO_SUCCESSURL"));// "https://bpgate.nxtpay.in/pgui/jsp/quomoResponse"  //"https://uat.pay10.com/pgui/jsp/quomoResponse"

			reqmap.put("failUrl", PropertiesManager.propertiesMap.get("QUOMO_FAILURL")); // "https://bpgate.nxtpay.in/pgui/jsp/quomoResponse" //"https://uat.pay10.com/pgui/jsp/quomoResponse"

			reqmap.put("callbackNotiUrl", PropertiesManager.propertiesMap.get("QUOMO_CALLBACKNOTIURL")); // "https://bpgate.nxtpay.in/pgui/jsp/quomoNotificationResponse" //"https://uat.pay10.com/pgui/jsp/quomoNotificationResponse"

			reqmap.put("API_USER_NAME", fields.get(FieldType.ADF2.getName())); //"lmp@gateway.quomo.digital"

			reqmap.put("API_PASSWORD", fields.get(FieldType.ADF3.getName())); //"lmp@pa$$ap!^wmDB39zt6grvP$dHXCB"

			reqmap.put("URL", PropertiesManager.propertiesMap.get("QUOMO_SALE_REQUEST_URL")); // "https://gateway.quomo.digital/api/fundin/deposit" //"https://gateway.quomo.digital/api/fundin/deposit"
	 
			JSONObject jsonRequest = new JSONObject();

			jsonRequest.put("merchant_id", String.valueOf(reqmap.get("merchantId")));

			jsonRequest.put("business_email", String.valueOf(reqmap.get("businessEmail")));

			jsonRequest.put("product_name", String.valueOf(reqmap.get("productName")));

			jsonRequest.put("order_id", String.valueOf(reqmap.get("orderId")));

			jsonRequest.put("deposit_method_id", reqmap.get("depositMethodId"));

			jsonRequest.put("bank_id", String.valueOf(reqmap.get("bankId")));

			jsonRequest.put("deposit_amount", reqmap.get("depositAmount"));

			jsonRequest.put("currency", String.valueOf(reqmap.get("currency")));

			jsonRequest.put("customer_name", String.valueOf(reqmap.get("customerName")));

			jsonRequest.put("card_number", "NA");

			jsonRequest.put("card_month", "NA");

			jsonRequest.put("card_year", "NA");

			jsonRequest.put("card_cvv", "NA");

			jsonRequest.put("customer_ip", String.valueOf(reqmap.get("customerIp")));

			jsonRequest.put("customer_email", String.valueOf(reqmap.get("customerEmail")));

			jsonRequest.put("customer_phone_no", String.valueOf(reqmap.get("customerPhoneNo")));

			jsonRequest.put("customer_address", String.valueOf(reqmap.get("customerAddress")));

			jsonRequest.put("note", String.valueOf(reqmap.get("note")));

			jsonRequest.put("website_url", String.valueOf(reqmap.get("websiteUrl")));

			jsonRequest.put("request_time", String.valueOf(reqmap.get("requestTime")));

			jsonRequest.put("success_url", String.valueOf(reqmap.get("successUrl")));

			jsonRequest.put("fail_url", String.valueOf(reqmap.get("failUrl")));

			jsonRequest.put("callback_noti_url", String.valueOf(reqmap.get("callbackNotiUrl")));
	 
			jsonRequest.put("sign_data", signData(reqmap));
	 
			logger.info("Final Request :: " + jsonRequest);

			String resp = "";

			String quomoRedirection = PropertiesManager.propertiesMap.get("QuomoRedirection");

			logger.info("quomoRedirection :: " + quomoRedirection);

			if(null != quomoRedirection && quomoRedirection.equalsIgnoreCase("Y")) {

				resp = apiRedirectcall(jsonRequest.toString().replace("\\", ""));

			}else {

				resp = sendPost(jsonRequest.toString().replace("\\", ""));

			}

			//String resp = sendPost(jsonRequest.toString().replace("\\", ""));

			//String resp = apiRedirectcall(jsonRequest.toString().replace("\\", ""));

			logger.info("Quomo Response =" + resp);

			if (isValid(resp)) {

				JSONObject jsonResp = new JSONObject(resp);

				if (jsonResp.has("errCode") && jsonResp.getInt("errCode") == 0) {

					if (jsonResp.has("redirect_url")) {

						return String.valueOf(jsonResp.get("redirect_url"));

					} else if (jsonResp.has("next_action")) {

						return String.valueOf(jsonResp.get("next_action"));

					} else {

						updateFailedSaleResponse(fields, jsonResp.has("error") ? jsonResp.getString("error") : "");

						throw new SystemException(ErrorType.ACUIRER_DOWN, ErrorType.ACUIRER_DOWN.getResponseCode());

					}

				} else {

					updateFailedSaleResponse(fields, jsonResp.has("error") ? jsonResp.getString("error") : "");

					throw new SystemException(ErrorType.ACUIRER_DOWN, ErrorType.ACUIRER_DOWN.getResponseCode());

				}
	 
			} else {

				updateFailedSaleResponse(fields, "");

				throw new SystemException(ErrorType.ACUIRER_DOWN, ErrorType.ACUIRER_DOWN.getResponseCode());

			}

		}

	private String apiRedirectcall(String replace) throws SystemException {

			String responseBody = "";

			String serviceUrl = "http://136.232.148.174:8081/pgws/abhi/request";

			logger.info("serviceUrl @@@@@@@@@@@@@@@" + replace);

			logger.info("serviceUrl @@@@@@@@@@@@@@@" + replace);
	 
			Map<String, String> resMap = new HashMap<String, String>();

			try {
	 
				JSONObject json = new JSONObject();

				json.put("request", replace);

				logger.info("Payment procees json request" + json);

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

				logger.info("response " + resMap);

			} catch (Exception exception) {

				logger.error("exception is " + exception);

				exception.printStackTrace();

				throw new SystemException(ErrorType.INTERNAL_SYSTEM_ERROR, "Error communicating to PG WS");

			}

			return resMap.get("request");

		}
	public boolean isValid(String json) {
	    try {
	        new JSONObject(json);
	    } catch (JSONException e) {
	        return false;
	    }
	    return true;
	}
	
	public void updateSaleResponse(Fields fields, String request) {

		fields.put(FieldType.QUOMO_FINAL_REQUEST.getName(), request);
		fields.put(FieldType.STATUS.getName(), StatusType.SENT_TO_BANK.getName());
		fields.put(FieldType.RESPONSE_CODE.getName(), ErrorType.SUCCESS.getResponseCode());
		fields.put(FieldType.RESPONSE_MESSAGE.getName(), ErrorType.SUCCESS.getResponseMessage());

	}
	
	public void updateFailedSaleResponse(Fields fields, String respMsg) {
		fields.put(FieldType.STATUS.getName(), StatusType.FAILED.getName());
		fields.put(FieldType.RESPONSE_CODE.getName(), ErrorType.ACUIRER_DOWN.getResponseCode());
		if(StringUtils.isNotBlank(respMsg)) {
			fields.put(FieldType.PG_TXN_MESSAGE.getName(), respMsg);
		} else {
			fields.put(FieldType.PG_TXN_MESSAGE.getName(), ErrorType.ACUIRER_DOWN.getResponseMessage());
		}
		
	}

	public static String signData(Map<String, Object> reqmap) throws NoSuchAlgorithmException {
		logger.info("Quomo Request for signData ::: " + reqmap);
		StringBuilder reqParam = new StringBuilder();
		reqParam.append(reqmap.get("md5Key"));
		reqParam.append(reqmap.get("merchantId"));
		reqParam.append(reqmap.get("businessEmail"));
		reqParam.append(reqmap.get("productName"));
		reqParam.append(reqmap.get("orderId"));
		reqParam.append(reqmap.get("depositMethodId"));
		reqParam.append(reqmap.get("bankId"));
		reqParam.append(reqmap.get("depositAmount"));
		reqParam.append(reqmap.get("currency"));
		reqParam.append("NA");
		reqParam.append("NA");
		reqParam.append("NA");
		reqParam.append("NA");
		reqParam.append(reqmap.get("customerName"));
		reqParam.append(reqmap.get("customerIp"));
		reqParam.append(reqmap.get("customerEmail"));
		reqParam.append(reqmap.get("customerPhoneNo"));
		reqParam.append(reqmap.get("customerAddress"));
		reqParam.append(reqmap.get("note"));
		reqParam.append(reqmap.get("websiteUrl"));
		reqParam.append(reqmap.get("requestTime"));
		reqParam.append(reqmap.get("successUrl"));
		reqParam.append(reqmap.get("failUrl"));
		reqParam.append(reqmap.get("callbackNotiUrl"));
		logger.info("request prepaid for signData :: " + reqParam.toString());
		MessageDigest md = MessageDigest.getInstance("MD5");
		byte[] digest = md.digest(reqParam.toString().getBytes());
		String myHash = DatatypeConverter.printHexBinary(digest).toUpperCase();
		logger.info("Quomo,sign_data myHash :: " + myHash);
		return myHash;
	}

	public static String sendPost(String request) throws Exception {

		logger.info(" ############ request " + request);
		HttpPost post = new HttpPost("https://gateway.quomo.digital/api/fundin/deposit");
		//HttpPost post = new HttpPost("https://gateway.quomo.digital/checkfororderstatus");
		String resp = null;
		// add request parameter, form parameters
		List<NameValuePair> urlParameters = new ArrayList<>();
		urlParameters.add(new BasicNameValuePair("requestParams", request));
		post.setEntity(new UrlEncodedFormEntity(urlParameters));
		post.setHeader("Authorization", "Basic " + Base64.getEncoder()
				.encodeToString(("lmp@gateway.quomo.digital:lmp@pa$$ap!^wmDB39zt6grvP$dHXCB").getBytes()));

		try (CloseableHttpClient httpClient = HttpClients.createDefault();
				CloseableHttpResponse response = httpClient.execute(post)) {

			resp = EntityUtils.toString(response.getEntity());
		}
		return resp;
	}
	
	public static void main(String[] args) throws Exception {
		/*
		 * JSONObject jsonRequest = new JSONObject(); jsonRequest.put("merchant_id",
		 * "18006286"); jsonRequest.put("order_id", "1701846958");
		 * jsonRequest.put("sign_data", "0F5FC58B8CF70F3A40F0C1AFD9EB9A0D");
		 * System.out.println("jsonRequest "+jsonRequest); String resp =
		 * sendPost(jsonRequest.toString().replace("\\", ""));
		 * System.out.println("resp "+resp);
		 */
		
		/*
		 * String resp =
		 * "{\"status\":\"success\",\"next_action\":\"redirect\",\"order_id\":\"1089131207082117\",\"redirect_url\":\"https://gw.dragonpay.ph/Pay.aspx?tokenid=a79e13774ef155400a6f8c38f11d73d1&procid=UCPB\",\"errCode\":14}";
		 * JSONObject jsonResp = new JSONObject(resp); System.out.println("resp "+resp);
		 * System.out.println("jsonResp "+jsonResp); if(jsonResp.has("errCode") &&
		 * jsonResp.getInt("errCode") == 0) { System.out.println("In side if"); } else {
		 * System.out.println("In side else"); }
		 */
		final DecimalFormat df = new DecimalFormat("#.00");
		String amount = df.format(Double.valueOf("31000") / 100);
		if(amount.contains(".00")) {
			amount = amount.split("\\.")[0];
		} 
		System.out.println("amount "+amount);
	}
}
