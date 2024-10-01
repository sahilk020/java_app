package com.pay10.sbi.card;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pay10.commons.exception.ErrorType;
import com.pay10.commons.exception.SystemException;
import com.pay10.commons.util.*;
import com.pay10.pg.core.sbicard.DecryptionUtil;
import com.pay10.pg.core.sbicard.EncryptedRequestData;
import com.pay10.pg.core.sbicard.EncryptionUtil;
import com.pay10.pg.core.tokenization.TokenManager;
import com.pay10.pg.core.util.AcquirerTxnAmountProvider;
import com.pay10.pg.core.util.ProcessManager;
import com.pay10.pg.core.util.Processor;
import com.pay10.pg.core.util.SbiUtil;
import org.apache.commons.lang3.StringUtils;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.regex.Pattern;

@Service("sbiCardTransactionConverter")
public class TransactionConverter {

	@Autowired
	private AcquirerTxnAmountProvider acquirerTxnAmountProvider;

	@Autowired
	private SbiUtil sbiUtil;

	@Autowired
	private TokenManager tokenManager;

	@Autowired
	private SbiCardStatusEnquiryProcessor statusEnquiryProcessor;

	@Autowired
	@Qualifier("updateProcessor")
	private Processor updateProcessor;

	private static final Logger logger = LoggerFactory.getLogger(TransactionConverter.class.getName());
	private static final ObjectMapper mapper = new ObjectMapper();
	public static final String REQUEST_OPEN_TAG = "<request>";
	public static final String REQUEST_CLOSE_TAG = "</request>";
	public static final String RESULT_OPEN_TAG = "<result>";
	public static final String RESULT_CLOSE_TAG = "</result>";
	public static final String ERROR_TEXT_OPEN_TAG = "<error_text>";
	public static final String ERROR_TEXT_CLOSE_TAG = "</error_text>";
	public static final String PAYMENT_ID_OPEN_TAG = "<paymentid>";
	public static final String PAYMENT_ID_CLOSE_TAG = "</paymentid>";
	public static final String AUTH_OPEN_TAG = "<auth>";
	public static final String AUTH_CLOSE_TAG = "</auth>";
	public static final String REF_OPEN_TAG = "<ref>";
	public static final String REF_CLOSE_TAG = "</ref>";
	public static final String AVR_OPEN_TAG = "<avr>";
	public static final String AVR_CLOSE_TAG = "</avr>";
	public static final String TRANID_OPEN_TAG = "<tranid>";
	public static final String TRANID_CLOSE_TAG = "</tranid>";
	public static final String ERROR_CODE_OPEN_TAG = "<error_code_tag>";
	public static final String ERROR_CODE_CLOSE_TAG = "</error_code_tag>";
	public static final String ERROR_SERVICE_OPEN_TAG = "<error_service_tag>";
	public static final String ERROR_SERVICE_CLOSE_TAG = "</error_service_tag>";
	public static final String AMOUNT_OPEN_TAG = "<amt>";
	public static final String AMOUNT_CLOSE_TAG = "</amt>";
	public static final String TRACKID_OPEN_TAG = "<trackid>";
	public static final String TRACKID_CLOSE_TAG = "</trackid>";
	public static final String PAY_ID_OPEN_TAG = "<payid>";
	public static final String PAY_ID_CLOSE_TAG = "</payid>";
	public static final String AUTH_RESC_OPEN_TAG = "<authrescode>";
	public static final String AUTH_RESC_CLOSE_TAG = "</authrescode>";

	public static final String REQUEST = "request";
	public static final String ID = "id";
	public static final String PASSWORD = "password";
	public static final String ACTION = "action";
	public static final String AMT = "amt";
	public static final String CURRENCYCODE = "currencycode";
	public static final String TRACKID = "trackId";
	public static final String CARD = "card";
	public static final String EXPMONTH = "expmonth";
	public static final String EXPYEAR = "expyear";
	public static final String CVV2 = "cvv2";
	public static final String MEMBER = "member";
	public static final String TYPE = "type";
	public static final String ERRORURL = "errorURL";
	public static final String RESPONSEURL = "responseURL";
	public static final String LANGUAGE = "langid";
	public static final String TRANSID = "transid";
	public static final String CURRENCY = "currency";
	public static final String UDF1 = "udf1";
	public static final String UDF2 = "udf2";
	public static final String UDF3 = "udf3";
	public static final String UDF4 = "udf4";
	public static final String UDF5 = "udf5";
	private static final SimpleDateFormat format = new SimpleDateFormat("YYYYMMddHHmmss");
	private static final SimpleDateFormat authDateFormatter = new SimpleDateFormat("YYYYMMddHHmm");

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
			if(fields.get(FieldType.MOP_TYPE.getName()).equalsIgnoreCase(MopType.RUPAY.getCode())) {
				rupauSaleRequest(fields, transaction);
			}else {
				preparePvReq(fields, transaction);
	
			}			break;
		case CAPTURE:
			break;
		case STATUS:
			// request = statusEnquiryRequest(fields, transaction);
			break;
		}
		return request;

		
	}
	
	public void rupauSaleRequest(Fields fields, Transaction transaction) throws SystemException {
		// fields.put(FieldType.PG_REF_NUM.getName(), UUID.randomUUID().toString());
		if (StringUtils.isBlank(fields.get(FieldType.PG_REF_NUM.getName()))) {
			fields.put(FieldType.PG_REF_NUM.getName(), fields.get(FieldType.TXN_ID.getName()));
		}
		JSONObject req = preparePvReqDataRupay(fields, transaction);
		String response = "";
		String responses2s="";

		String pgRefNo = fields.get(FieldType.PG_REF_NUM.getName());
		try {
			String acqId = fields.get(FieldType.TXN_KEY.getName());
			String apiUrl = PropertiesManager.propertiesMap.get("SBI_CARD_BIN_RANGE");
			logger.info("preparePvReq:: apiUrl={}, pgRefNo={}, req={}", apiUrl, pgRefNo, req);
			response = executeApirupayotp(fields, req, response, apiUrl);
			logger.info("preparePvReq:: response={}, pgRefNo={}", response, pgRefNo);
			org.json.simple.JSONObject pvResp = decryptResponse(response);
			
			if(pvResp.containsKey("availableAuthMode")) {
				
				if(((String)pvResp.get("availableAuthMode")).equalsIgnoreCase("01")) {
				
					JSONObject requestS2s=	requestForRedirect(fields);
					
					
					String apiUrls2s = PropertiesManager.propertiesMap.get("SBI_CARD_INITIATE");
					logger.info("preparePvReq:: apiUrl={}, pgRefNo={}, req={}", apiUrls2s, pgRefNo, requestS2s);
					responses2s = executeApirupayotp(fields, requestS2s, responses2s, apiUrls2s);
					logger.info("preparePvReq:: response={}, pgRefNo={}", responses2s, pgRefNo);
					org.json.simple.JSONObject pvResps2s = decryptResponse(responses2s);
					if((pvResps2s.get("errorcode").toString()).equalsIgnoreCase("0")) {
						fields.put(FieldType.SBI_AUTH_RESPONSE.getName(), pvResps2s.get("session").toString());
						fields.put(FieldType.SBI_CARD_PA_RES.getName(), pvResps2s.get("accuRequestId").toString());
						fields.put(FieldType.RRN.getName(),pvResps2s.get("pgTransactionId").toString());
						fields.put(FieldType.SBI_FINAL_REQUEST.getName(), pvResps2s.get("redirectURL").toString());

						fields.put(FieldType.SBI_OTP_PAGE.getName(),"N");
						fields.put(FieldType.STATUS.getName(), StatusType.SENT_TO_BANK.getName());
						fields.put(FieldType.RESPONSE_CODE.getName(), ErrorType.SUCCESS.getResponseCode());
						fields.put(FieldType.RESPONSE_MESSAGE.getName(), ErrorType.SUCCESS.getResponseMessage());
						
					}
				}else {
					
					JSONObject requestS2s=	rupayS2sForRedirect(fields);
				
					String apiUrls2s = PropertiesManager.propertiesMap.get("SBI_CARD_GENERATE_OTP");
					logger.info("preparePvReq:: apiUrl={}, pgRefNo={}, req={}", apiUrls2s, pgRefNo, requestS2s);
					responses2s = executeApirupayotp(fields, requestS2s, responses2s, apiUrls2s);
					logger.info("preparePvReq:: response={}, pgRefNo={}", responses2s, pgRefNo);
					org.json.simple.JSONObject pvResps2s = decryptResponse(responses2s);
					if((pvResps2s.get("errorcode").toString()).equalsIgnoreCase("0")) {
						
						fields.put(FieldType.RRN.getName(),pvResps2s.get("pgTransactionId").toString());
						fields.put(FieldType.SBI_OTP_PAGE.getName(),"Y");
						fields.put(FieldType.STATUS.getName(), StatusType.SENT_TO_BANK.getName());
						fields.put(FieldType.RESPONSE_CODE.getName(), ErrorType.SUCCESS.getResponseCode());
						fields.put(FieldType.RESPONSE_MESSAGE.getName(), ErrorType.SUCCESS.getResponseMessage());
						
					}
					
	
					
				}
				
			}else {
				
				
				
				
			}
			
			
			
			
		} catch (Exception e) {
			logger.error("preparePvReq:: failed. pgRefNo={}", pgRefNo, e);
			throw new SystemException(ErrorType.FAILED, e.getMessage());
		}
	}
	public Transaction toTransactionRupay(JSONObject saleResponse) {
		Transaction transaction = new Transaction();
		String status = saleResponse.getInt("status") == 50020 ? "Success" : "Failed";
		transaction.setStatus(status);
		transaction.setAcqId(saleResponse.get("transactionId").toString());
		transaction.setResponseMessage(saleResponse.get("pgErrorCode").toString());
		transaction.setRef(saleResponse.get("rrn").toString());
		transaction.setPayId(saleResponse.get("rrn").toString());
		return transaction;
	}
	private JSONObject preparePvReqDataRupay(Fields fields, Transaction transaction) {
		JSONObject req = new JSONObject();
		String returnUrl = PropertiesManager.propertiesMap.get("SBI_CARD_SECURE_RETURN_URL");
		String acqId = fields.get(FieldType.ADF1.getName());
		req.put("pgInstanceId", fields.get(FieldType.ADF9.getName()));
		req.put("cardBin", fields.get(FieldType.CARD_NUMBER.getName()).substring(0, 9));
		logger.info("            "+req);
		req = prepareEncReq(fields, req, acqId);
		return req;
	}
	 String executeApirupayotp(Fields fields, JSONObject req, String response, String authenticationUrl)
				throws IOException {
			URL url = new URL(authenticationUrl);
			HttpURLConnection connection = (HttpURLConnection) url.openConnection();
			connection.setRequestMethod("POST");
			connection.setRequestProperty("Content-Type", "application/json");
			connection.setRequestProperty("cache-control", "no-cache");
			connection.setRequestProperty("x-api-key", fields.get(FieldType.ADF4.getName()));
			connection.setRequestProperty("pgInstanceId", fields.get(FieldType.ADF9.getName()));
			connection.setRequestProperty("merchantId", fields.get(FieldType.TXN_KEY.getName()));

			logger.info("executeApi:: xApiKey={}, pgInstanceId={},merchantId{}=", fields.get(FieldType.ADF4.getName()),
					fields.get(FieldType.ADF9.getName()),fields.get(FieldType.TXN_KEY.getName()));
			connection.setUseCaches(false);
			connection.setDoInput(true);
			connection.setDoOutput(true);
			connection.setConnectTimeout(60000);
			connection.setReadTimeout(60000);

			// Send request
			DataOutputStream wr = new DataOutputStream(connection.getOutputStream());
			wr.writeBytes(req.toString());
			wr.flush();
			wr.close();

		    BufferedReader br = null;

			int statusCode = connection.getResponseCode();

		    if (statusCode==200){
		        br = new BufferedReader(new InputStreamReader(connection.getInputStream()));
		    } else {
		        br = new BufferedReader(new InputStreamReader(connection.getErrorStream()));
		    }
			// Get Response

			String decodedString;

			while ((decodedString = br.readLine()) != null) {
				response = response + decodedString;
			}
			br.close();
			return response;
		}
	private JSONObject rupayS2sForRedirect(Fields fields) {
		JSONObject req = new JSONObject();
		String returnUrl = PropertiesManager.propertiesMap.get("SBI_CARD_SECURE_RETURN_URL");
		String acqId = fields.get(FieldType.TXN_KEY.getName());
		req.put("pgInstanceId", fields.get(FieldType.ADF9.getName()));
		req.put("merchantId", acqId);
		req.put("merchantReferenceNo", fields.get(FieldType.ORDER_ID.getName()));
		req.put("pan", fields.get(FieldType.CARD_NUMBER.getName()));
		req.put("cardExpDate", fields.get(FieldType.CARD_EXP_DT.getName()));
		req.put("cvd2", fields.get(FieldType.CVV.getName()));
		req.put("nameOnCard", fields.get(FieldType.CARD_HOLDER_NAME.getName()));
		req.put("cardHolderStatus", "NW");
		req.put("amount", fields.get(FieldType.TOTAL_AMOUNT.getName()));
		req.put("currencyCode", fields.get(FieldType.CURRENCY_CODE.getName()));
		req.put("orderDesc", "Order Description");
		req.put("email", fields.get(FieldType.CUST_EMAIL.getName()));
		

		req.put("customerIpAddress", fields.get(FieldType.INTERNAL_CUST_IP.getName()));
		if (StringUtils.isNotBlank(fields.get(FieldType.BROWSER_USER_AGENT.getName()))) {
			req.put("browserUserAgent", fields.get(FieldType.BROWSER_USER_AGENT.getName()));
		} else {
			req.put("browserUserAgent",
					"Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/112.0.0.0 Safari/537.36");

		}
		
		req.put("httpAccept", "text/html,72pplication/xhtml+xml,application/xml;q=0.9, image/webp, image/apng, */*;q=0.8");

req.put("purposeOfAuthentication", "Real Card Transaction");

		logger.info("            "+req);
		req = prepareEncReq(fields, req, acqId);
		return req;		
	}

	private JSONObject requestForRedirect(Fields fields) {
		JSONObject req = new JSONObject();
		String returnUrl = PropertiesManager.propertiesMap.get("SBICARDRuPayReturnUrl");
		String acqId = fields.get(FieldType.TXN_KEY.getName());
		req.put("pgInstanceId", fields.get(FieldType.ADF9.getName()));
		req.put("merchantId", acqId);
		req.put("merchantReferenceNo", fields.get(FieldType.ORDER_ID.getName()));
		req.put("pan", fields.get(FieldType.CARD_NUMBER.getName()));
		req.put("cardExpDate", fields.get(FieldType.CARD_EXP_DT.getName()));
		req.put("cvd2", fields.get(FieldType.CVV.getName()));
		req.put("nameOnCard", fields.get(FieldType.CARD_HOLDER_NAME.getName()));
		req.put("cardHolderStatus", "NW");
		req.put("authAmount", fields.get(FieldType.TOTAL_AMOUNT.getName()));
		req.put("currencyCode", fields.get(FieldType.CURRENCY_CODE.getName()));
		req.put("orderDesc", "Order Description");
		req.put("email", fields.get(FieldType.CUST_EMAIL.getName()));
		req.put("merchantResponseUrl", returnUrl);

		req.put("ipAddress", fields.get(FieldType.INTERNAL_CUST_IP.getName()));
		if (StringUtils.isNotBlank(fields.get(FieldType.BROWSER_USER_AGENT.getName()))) {
			req.put("browserUserAgent", fields.get(FieldType.BROWSER_USER_AGENT.getName()));
		} else {
			req.put("browserUserAgent",
					"Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/112.0.0.0 Safari/537.36");

		}
		
		req.put("httpAccept", "text/html,72pplication/xhtml+xml,application/xml;q=0.9, image/webp, image/apng, */*;q=0.8");

req.put("purposeOfAuthentication", "Real Card Transaction");

		logger.info("            "+req);
		req = prepareEncReq(fields, req, acqId);
		return req;		
	}


	public void preparePvReq(Fields fields, Transaction transaction) throws SystemException {
		// fields.put(FieldType.PG_REF_NUM.getName(), UUID.randomUUID().toString());
		if (StringUtils.isBlank(fields.get(FieldType.PG_REF_NUM.getName()))) {
			fields.put(FieldType.PG_REF_NUM.getName(), fields.get(FieldType.TXN_ID.getName()));
		}
		JSONObject req = preparePvReqData(fields, transaction);
		String response = "";
		String pgRefNo = fields.get(FieldType.PG_REF_NUM.getName());
		try {
			String acqId = fields.get(FieldType.ADF1.getName());
			String apiUrl = PropertiesManager.propertiesMap.get("SBI_CARD_PVRQ_URL");
			apiUrl = StringUtils.join(apiUrl, acqId, "/", pgRefNo);
			logger.info("preparePvReq:: apiUrl={}, pgRefNo={}, req={}", apiUrl, pgRefNo, req);
			response = executeApi(fields, req, response, apiUrl);
			logger.info("preparePvReq:: response={}, pgRefNo={}", response, pgRefNo);
			org.json.simple.JSONObject pvResp = decryptResponse(response);
			prepareParReq(fields, new HashMap<>(pvResp));
		} catch (Exception e) {
			logger.error("preparePvReq:: failed. pgRefNo={}", pgRefNo, e);
			throw new SystemException(ErrorType.FAILED, e.getMessage());
		}
	}

	private JSONObject preparePvReqData(Fields fields, Transaction transaction) {
		JSONObject req = new JSONObject();
		String returnUrl = PropertiesManager.propertiesMap.get("SBI_CARD_SECURE_RETURN_URL");
		String acqId = fields.get(FieldType.ADF1.getName());
		req.put("messageType", "pVrq");
		req.put("deviceChannel", "02");
		req.put("merchantTransID", fields.get(FieldType.PG_REF_NUM.getName()));
		req.put("acctNumber", fields.get(FieldType.CARD_NUMBER.getName()));
		req.put("acquirerID", acqId);
		req.put("acquirerBIN", fields.get(FieldType.ADF2.getName()));
		req.put("threeDSRequestorMethodNotificationRespURL", returnUrl);
		req.put("p_messageVersion", "2.1.0");
		req = prepareEncReq(fields, req, acqId);
		return req;
	}

	private JSONObject prepareEncReqre(Fields fields, JSONObject req, String acqId) {
		EncryptedRequestData encryptedRequestData = EncryptionUtil.encrypt(req.toString());
		JSONObject finalReq = new JSONObject();
		finalReq.put("signedEncRequestPayload", encryptedRequestData.getSignedEncRequestPayload());
		finalReq.put("requestSymmetricEncKey", encryptedRequestData.getRequestSymmetricEncKey());
		finalReq.put("iv", encryptedRequestData.getIv());
		finalReq.put("pgInstanceId", fields.get(FieldType.ADF9.getName()));
		finalReq.put("merchantId", acqId);
		finalReq.put("var1", fields.get(FieldType.PG_REF_NUM.getName()));
		finalReq.put("var2", "");
		finalReq.put("var3", "");
		return finalReq;
	}

	JSONObject prepareEncReq(Fields fields, JSONObject req, String acqId) {
		EncryptedRequestData encryptedRequestData = EncryptionUtil.encrypt(req.toString());
		JSONObject finalReq = new JSONObject();
		finalReq.put("signedEncRequestPayload", encryptedRequestData.getSignedEncRequestPayload());
		finalReq.put("requestSymmetricEncKey", encryptedRequestData.getRequestSymmetricEncKey());
		finalReq.put("iv", encryptedRequestData.getIv());
		finalReq.put("pgInstanceId", acqId);
		finalReq.put("merchantId", acqId);
		finalReq.put("var1", fields.get(FieldType.PG_REF_NUM.getName()));
		finalReq.put("var2", "");
		finalReq.put("var3", "");
		return finalReq;
	}

	public void prepareParReq(Fields fields, Map<String, Object> pvResponse) {
		String pgRefNo = fields.get(FieldType.PG_REF_NUM.getName());
		String response = "";
		try {
			JSONObject req = prepareParReqJson(fields, pvResponse, pgRefNo);
			String threeDsServerPaReqUrl = (String) pvResponse.get("threeDSServerPaRqURL");
			logger.info("prepareParReq:: apiUrl={}, pgRefNo={}", threeDsServerPaReqUrl, pgRefNo);
			response = executeApi(fields, req, response, threeDsServerPaReqUrl);
			logger.info("prepareParReq:: response={}, pgRefNo={}", response, pgRefNo);
			org.json.simple.JSONObject resJson = decryptResponse(response);
			fields.put(FieldType.SBI_AUTH_RESPONSE.getName(), resJson.toJSONString());
			fields.put(FieldType.SBI_CARD_PA_RES.getName(), resJson.toJSONString());
		} catch (Exception e) {
			logger.error("prepareParReq:: failed. pgRefNo={}", pgRefNo, e);
		}
	}

	private JSONObject prepareParReqJson(Fields fields, Map<String, Object> pvResponse, String pgRefNo)
			throws SystemException {
		JSONObject req = new JSONObject();
		String returnUrl = PropertiesManager.propertiesMap.get("SBI_CARD_SECURE_RETURN_URL");
		req.put("messageType", "pArq");
		req.put("deviceChannel", "02");
		req.put("merchantTransID", pgRefNo);
		req.put("messageVersion", pvResponse.get("p_messageVersion"));
		req.put("messageCategory", "01");
		req.put("threeDSServerTransID", pvResponse.get("threeDSServerTransID"));
		req.put("threeDSRequestorID", fields.get(FieldType.ORDER_ID.getName()));
		if (StringUtils.isNotBlank(fields.get(FieldType.MERCHANT_BUSS_NAME.getName()))) {
			req.put("threeDSRequestorName", fields.get(FieldType.MERCHANT_BUSS_NAME.getName()));
		} else {
			req.put("threeDSRequestorName", "PAY10");

		}
		req.put("threeDSRequestorURL", returnUrl);
		req.put("threeDSCompInd", "U");
		req.put("threeDSRequestorAuthenticationInd", "01");
		req.put("threeDSRequestorAuthenticationInfo", prepareRequestorAuthInfo());

		req.put("acctNumber", fields.get(FieldType.CARD_NUMBER.getName()));
		String accType = StringUtils.equalsIgnoreCase(fields.get(FieldType.PAYMENT_TYPE.getName()),
				PaymentType.CREDIT_CARD.getCode()) ? "02" : "03";
		req.put("acctType", accType);
		req.put("addrMatch", "Y");
		String expiryDate = fields.get(FieldType.CARD_EXP_DT.getName());
		String month = expiryDate.substring(0, 2);
		String year = expiryDate.substring(4, 6);
		req.put("cardExpiryDate", StringUtils.join(year, month));
		req.put("cardholderName", fields.get(FieldType.CARD_HOLDER_NAME.getName()));
		req.put("mcc", fields.get(FieldType.ADF8.getName()));
		if (StringUtils.isNoneBlank(fields.get(FieldType.MERCHANT_BUSS_NAME.getName()))) {
			req.put("merchantName", fields.get(FieldType.MERCHANT_BUSS_NAME.getName()));
		} else {
			req.put("merchantName", "PAY10");

		}
		req.put("merchantUrl", returnUrl);
		req.put("merchantCountryCode", fields.get(FieldType.CURRENCY_CODE.getName()));
		req.put("acquirerBIN", fields.get(FieldType.ADF2.getName()));
//        req.put("acquirerID", fields.get(FieldType.ADF1.getName()));
		req.put("acquirerMerchantID", fields.get(FieldType.ADF3.getName()));
		req.put("purchaseCurrency", fields.get(FieldType.CURRENCY_CODE.getName()));
		req.put("purchaseExponent", "2");
		fields.put(FieldType.TOTAL_AMOUNT.getName(),(fields.get(FieldType.TOTAL_AMOUNT.getName())).replace(".", ""));
		req.put("purchaseAmount", fields.get(FieldType.TOTAL_AMOUNT.getName()));

		String purchaseDate = format.format(new Date());
		req.put("purchaseDate", purchaseDate);
		req.put("mobilePhone", prepareContactDetails("91", fields.get(FieldType.CUST_PHONE.getName())));
		req.put("workPhone", prepareContactDetails("91", fields.get(FieldType.CUST_PHONE.getName())));
		req.put("email", fields.get(FieldType.CUST_EMAIL.getName()));
		req.put("homePhone", prepareContactDetails("91", fields.get(FieldType.CUST_PHONE.getName())));
		req.put("transType", "01");
		req.put("threeDSRequestorFinalAuthRespURL", returnUrl);
		req.put("p_messageVersion", pvResponse.get("p_messageVersion"));
		if (StringUtils.isNoneBlank(fields.get(FieldType.BROWSER_LANG.getName()))) {
			req.put("browserLanguage", fields.get(FieldType.BROWSER_LANG.getName()));
		} else {
			req.put("browserLanguage", "en-US");

		}
		if (StringUtils.isNoneBlank(fields.get(FieldType.BROWSER_ACCEPT_HEADER.getName()))) {
			req.put("browserAcceptHeader", fields.get(FieldType.BROWSER_ACCEPT_HEADER.getName()));
		} else {
			req.put("browserAcceptHeader",
					"text/html,application/xhtml+xml,application/xml;q=0.9,image/avif,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3;q=0.7");

		}
		if (StringUtils.isNotBlank(fields.get(FieldType.BROWSER_USER_AGENT.getName()))) {
			req.put("browserUserAgent", fields.get(FieldType.BROWSER_USER_AGENT.getName()));
		} else {
			req.put("browserUserAgent",
					"Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/112.0.0.0 Safari/537.36");

		}
		req.put("browserScreenHeight", fields.get(FieldType.BROWSER_SCREEN_HEIGHT.getName()));
		if (StringUtils.isNotBlank(fields.get(FieldType.BROWSER_SCREEN_HEIGHT.getName()))) {
			req.put("browserScreenHeight", fields.get(FieldType.BROWSER_SCREEN_HEIGHT.getName()));

		} else {
			req.put("browserScreenHeight", "856");

		}
		if (StringUtils.isNotBlank(fields.get(FieldType.BROWSER_SCREEN_WIDTH.getName()))) {
			req.put("browserScreenWidth", fields.get(FieldType.BROWSER_SCREEN_WIDTH.getName()));

		} else {
			req.put("browserScreenWidth", "1521");

		}
		req.put("browserJavaEnabled", Boolean.valueOf(fields.get(FieldType.BROWSER_JAVA_ENABLED.getName())));
		if (StringUtils.isNotBlank(fields.get(FieldType.BROWSER_COLOR_DEPTH.getName()))) {
			req.put("browserColorDepth", fields.get(FieldType.BROWSER_COLOR_DEPTH.getName()));
		} else {
			req.put("browserColorDepth", "24");

		}
		if (StringUtils.isNotBlank(fields.get(FieldType.BROWSER_TZ.getName()))) {
			req.put("browserTZ", fields.get(FieldType.BROWSER_TZ.getName()));

		} else {
			req.put("browserTZ", "-330");

		}
		req.put("browserIP", fields.get(FieldType.INTERNAL_CUST_IP.getName()));
		logger.info("prepareParReqJson:: plain request={}", req);
		return prepareEncReq(fields, req, fields.get(FieldType.ADF1.getName()));
	}

	private JSONObject prepareRequestorAuthInfo() {
		JSONObject authDetails = new JSONObject();
		authDetails.put("threeDSReqAuthMethod", "04");
		authDetails.put("threeDSReqAuthTimestamp", authDateFormatter.format(new Date()));
		authDetails.put("threeDSReqAuthData", "00");
		return authDetails;
	}

	private JSONObject prepareContactDetails(String countryCode, String mobileNo) {
		JSONObject contact = new JSONObject();
		contact.put("cc", countryCode);
		contact.put("subscriber", mobileNo);
		return contact;
	}

	public String preparePrqFReq(Fields fields) throws SystemException {
		String pgRefNo = fields.get(FieldType.PG_REF_NUM.getName());
		JSONObject paResponse = new JSONObject(fields.get(FieldType.SBI_AUTH_RESPONSE.getName()));
		JSONObject req = preparePrqFReqData(fields, paResponse);
		String response = "";
		try {
			String authenticationUrl = (String) paResponse.get("authenticationUrl");
			logger.info("preparePrqFReq:: apiUrl={}, pgRefNo={}, req={}", authenticationUrl, pgRefNo, req);
			response = executeApi(fields, req, response, authenticationUrl);
			org.json.simple.JSONObject decryptedRes = decryptResponse(response);
			logger.info("preparePrqFReq:: response={}, pgRefNo={}", decryptedRes, pgRefNo);
			return decryptedRes.toJSONString();
		} catch (Exception e) {
			logger.error("preparePrqFReq:: failed. pgRefNo={}", pgRefNo, e);
			throw new SystemException(ErrorType.FAILED, e.getMessage());
		}
	}

	private String executeApiRefund(Fields fields, JSONObject req, String response, String authenticationUrl)
			throws IOException {
		URL url = new URL(authenticationUrl);
		HttpURLConnection connection = (HttpURLConnection) url.openConnection();
		connection.setRequestMethod("POST");
		connection.setRequestProperty("Content-Type", "application/json");
		connection.setRequestProperty("cache-control", "no-cache");
		connection.setRequestProperty("x-api-key", fields.get(FieldType.ADF4.getName()));
		connection.setRequestProperty("pgInstanceId", fields.get(FieldType.ADF9.getName()));
		logger.info("executeApi:: xApiKey={}, pgInstanceId={}", fields.get(FieldType.ADF4.getName()),
				fields.get(FieldType.ADF9.getName()));
		connection.setUseCaches(false);
		connection.setDoInput(true);
		connection.setDoOutput(true);
		connection.setConnectTimeout(60000);
		connection.setReadTimeout(60000);

// Send request
		DataOutputStream wr = new DataOutputStream(connection.getOutputStream());
		wr.writeBytes(req.toString());
		wr.flush();
		wr.close();

// Get Response
		InputStream is = connection.getInputStream();

		BufferedReader bufferedreader = new BufferedReader(new InputStreamReader(is));
		String decodedString;

		while ((decodedString = bufferedreader.readLine()) != null) {
			response = response + decodedString;
		}
		bufferedreader.close();
		return response;
	}

	private String executeApi(Fields fields, JSONObject req, String response, String authenticationUrl)
			throws IOException {
		URL url = new URL(authenticationUrl);
		HttpURLConnection connection = (HttpURLConnection) url.openConnection();
		connection.setRequestMethod("POST");
		connection.setRequestProperty("Content-Type", "application/json");
		connection.setRequestProperty("cache-control", "no-cache");
		connection.setRequestProperty("x-api-key", fields.get(FieldType.ADF4.getName()));
		connection.setRequestProperty("pgInstanceId", fields.get(FieldType.ADF1.getName()));
		logger.info("executeApi:: xApiKey={}, pgInstanceId={}", fields.get(FieldType.ADF4.getName()),
				fields.get(FieldType.ADF1.getName()));
		connection.setUseCaches(false);
		connection.setDoInput(true);
		connection.setDoOutput(true);
		connection.setConnectTimeout(60000);
		connection.setReadTimeout(60000);

		// Send request
		DataOutputStream wr = new DataOutputStream(connection.getOutputStream());
		wr.writeBytes(req.toString());
		wr.flush();
		wr.close();

		// Get Response
		InputStream is = connection.getInputStream();

		BufferedReader bufferedreader = new BufferedReader(new InputStreamReader(is));
		String decodedString;

		while ((decodedString = bufferedreader.readLine()) != null) {
			response = response + decodedString;
		}
		bufferedreader.close();
		return response;
	}

	private String executeApi(Fields fields, JSONObject req, String response, String authenticationUrl,
			String pgInstanceId) throws IOException {
		URL url = new URL(authenticationUrl);
		HttpURLConnection connection = (HttpURLConnection) url.openConnection();
		connection.setRequestMethod("POST");
		connection.setRequestProperty("Content-Type", "application/json");
		connection.setRequestProperty("cache-control", "no-cache");
		connection.setRequestProperty("x-api-key", fields.get(FieldType.ADF4.getName()));
		connection.setRequestProperty("pgInstanceId", pgInstanceId);
		logger.info("executeApi:: xApiKey={}, pgInstanceId={}", fields.get(FieldType.ADF4.getName()), pgInstanceId);
		connection.setUseCaches(false);
		connection.setDoInput(true);
		connection.setDoOutput(true);
		connection.setConnectTimeout(60000);
		connection.setReadTimeout(60000);

		// Send request
		DataOutputStream wr = new DataOutputStream(connection.getOutputStream());
		wr.writeBytes(req.toString());
		wr.flush();
		wr.close();

		// Get Response
		InputStream is = connection.getInputStream();

		BufferedReader bufferedreader = new BufferedReader(new InputStreamReader(is));
		String decodedString;

		while ((decodedString = bufferedreader.readLine()) != null) {
			response = response + decodedString;
		}
		bufferedreader.close();
		return response;
	}

	private JSONObject preparePrqFReqData(Fields fields, JSONObject paResponse) {
		JSONObject req = new JSONObject();
		String acqId = fields.get(FieldType.ADF1.getName());
		req.put("messageType", "pRqFrq");
		req.put("messageVersion", paResponse.get("p_messageVersion"));
		req.put("threeDSServerTransID", paResponse.get("threeDSServerTransID"));
		req.put("acsTransID", paResponse.get("acsTransID"));
		req.put("dsTransID", paResponse.get("dsTransID"));
		req.put("merchantTransID", paResponse.get("merchantTransID"));
		req.put("signatureHashIdentifier", "null");
		req.put("acquirerID", acqId);
		req.put("acsAuthResponse", fields.get(FieldType.SBI_CARD_FINAL_RES.getName()));
		req.put("p_messageVersion", paResponse.get("p_messageVersion"));
		return prepareEncReq(fields, req, acqId);
	}

	org.json.simple.JSONObject decryptResponse(String encryptedText) throws SystemException {
		org.json.simple.JSONObject decryptedData = DecryptionUtil.decrypt(encryptedText);
		return decryptedData;
	}

	public void prepareAuthorizationReq(Fields fields, JSONObject autheResponse) throws SystemException {
		JSONObject req = prepareAuthReqParams(fields, autheResponse);
		String response = "";
		String pgRefNo = fields.get(FieldType.PG_REF_NUM.getName());
		try {
			String saleUrl = PropertiesManager.propertiesMap.get("SBI_CARD_SALE_URL");
			logger.info("prepareAuthorizationReq:: apiUrl={}, pgRefNo={}, req={}", saleUrl, pgRefNo, req);
			String pgInstanceId = fields.get(FieldType.ADF9.getName());
			response = executeApi(fields, req, response, saleUrl, pgInstanceId);
			org.json.simple.JSONObject decryptedRes = decryptResponse(response);
			logger.info("prepareAuthorizationReq:: response={}, pgRefNo={}", decryptedRes, pgRefNo);
			manageResponse(fields, new JSONObject(decryptedRes.toJSONString()));
		} catch (Exception e) {
			logger.error("prepareAuthorizationReq:: failed. pgRefNo={}", pgRefNo, e);
			throw new SystemException(ErrorType.FAILED, e.getMessage());
		}
	}

	private void manageResponse(Fields fields, JSONObject saleResponse) throws SystemException {
		Transaction transactionResponse = new Transaction();
		if (doubleVerification(fields, saleResponse)) {
			logger.info("manageResponse:: doubleVerification success. pgRefNo={}, amount={}, " + "orderId = {}",
					fields.get(FieldType.PG_REF_NUM.getName()), fields.get(FieldType.TOTAL_AMOUNT.getName()),
					fields.get(FieldType.ORDER_ID.getName()));
			transactionResponse = toTransaction(saleResponse);
			SbiCardTransformer sbiTransformer = new SbiCardTransformer(transactionResponse);
			sbiTransformer.updateResponse(fields);
		} else {
			fields.put(FieldType.STATUS.getName(), StatusType.DENIED.getName());
			fields.put(FieldType.RESPONSE_CODE.getName(), ErrorType.SIGNATURE_MISMATCH.getCode());
			fields.put(FieldType.RESPONSE_MESSAGE.getName(), ErrorType.SIGNATURE_MISMATCH.getResponseMessage());
		}
		fields.put(FieldType.INTERNAL_ORIG_TXN_TYPE.getName(), fields.get(FieldType.TXNTYPE.getName()));
		fields.remove(FieldType.SBI_CARD_FINAL_RES.getName());
		fields.put(FieldType.TXN_ID.getName(), TransactionManager.getNewTransactionId());
		ProcessManager.flow(updateProcessor, fields, true);
	}

	public Transaction toTransaction(JSONObject saleResponse) {
		Transaction transaction = new Transaction();
		String status = saleResponse.getInt("status") == 50020 ? "Success" : "Failed";
		transaction.setStatus(status);
		transaction.setAcqId(saleResponse.get("transactionId").toString());
		transaction.setResponseMessage(saleResponse.getString("pgErrorDetail"));
		transaction.setRef(saleResponse.getString("rrn"));
		transaction.setPayId(saleResponse.getString("rrn"));
		return transaction;
	}

	private boolean doubleVerification(Fields fields, JSONObject saleResponse) throws SystemException {
		try {
			if (saleResponse.getInt("status") == 50020) {
				logger.info("doubleVerification:: initialized status={}, pgRefNo={},amount={} ",
						saleResponse.getInt("status"), fields.get(FieldType.PG_REF_NUM.getName()),
						fields.get(FieldType.TOTAL_AMOUNT.getName()));
				JSONObject enquiryRes = statusEnquiryProcessor.statusEnquiryReq(fields);
				logger.info("doubleVerification response:: enquiryRes={}, pgRefNo={}", enquiryRes,
						fields.get(FieldType.PG_REF_NUM.getName()));
				if (enquiryRes.getInt("status") == saleResponse.getInt("status") && StringUtils.equalsIgnoreCase(
						enquiryRes.get("transactionId").toString(), saleResponse.get("transactionId").toString())) {
					return true;
				} else {
					logger.info("doubleVerification:: failed. Status={},  resAmount={},txn_ID={}",
							enquiryRes.getInt("status"), fields.get(FieldType.TOTAL_AMOUNT.getName()),
							enquiryRes.get("transactionId"));
				}
			}
		} catch (IOException e) {
			logger.error("doubleVerification:: failed. pgRefNo={}", e, fields.get(FieldType.PG_REF_NUM.getName()));
			throw new SystemException(ErrorType.ACQUIRER_ERROR, e.getMessage());
		}
		return false;
	}

	private JSONObject prepareAuthReqParams(Fields fields, JSONObject autheRes) throws SystemException {
		JSONObject req = new JSONObject();
		String acqId = fields.get(FieldType.ADF1.getName());
		req.put("pgInstanceId", fields.get(FieldType.ADF9.getName()));
		req.put("merchantId", fields.get(FieldType.ADF11.getName()));
		req.put("acquiringBankId", fields.get(FieldType.ADF10.getName()));
		req.put("action", "SERVICE_POST_MPI");
		req.put("transactionTypeCode", "9003");
		req.put("deviceCategory", "0");
		req.put("pan", fields.get(FieldType.CARD_NUMBER.getName()));
		String expDate = fields.get(FieldType.CARD_EXP_DT.getName());
		req.put("expiryDateYYYY", expDate.substring(2, 6));
		req.put("expiryDateMM", expDate.substring(0, 2));
		req.put("cvv2", fields.get(FieldType.CVV.getName()));
		req.put("nameOnCard", fields.get(FieldType.CARD_HOLDER_NAME.getName()));
		req.put("email", fields.get(FieldType.CUST_EMAIL.getName()));
		req.put("currencyCode", fields.get(FieldType.CURRENCY_CODE.getName()));
		req.put("amount", fields.get(FieldType.TOTAL_AMOUNT.getName()));
		req.put("merchantReferenceNo", fields.get(FieldType.ORDER_ID.getName()));
		req.put("orderDesc", "Sale Txn");
		req.put("mpiTransactionId", autheRes.getString("dsTransID"));
		req.put("threeDsStatus", autheRes.getString("transStatus"));
		req.put("threeDsEci", autheRes.getString("eci"));
		if (autheRes.has("XID")) {
			req.put("threeDsXid", autheRes.getString("XID"));
		} else {
			req.put("threeDsXid", System.currentTimeMillis());
		}
		req.put("threeDsCavvAav", autheRes.get("authenticationValue"));
		
		logger.info("prepareAuthReqParams:: sale req. plain req={}", req);
		return prepareEncReq(fields, req, acqId);
	}

//    public String saleRequest(Fields fields, Transaction transaction) throws SystemException {
//        String amount = acquirerTxnAmountProvider.amountProvider(fields);
//        String returnUrl = PropertiesManager.propertiesMap.get("SbiresponseURL");
//
//        // code added by sonu
//        if (fields.get(FieldType.PAYMENT_TYPE.getName()).equalsIgnoreCase(PaymentType.CREDIT_CARD.getCode())
//                || fields.get(FieldType.PAYMENT_TYPE.getName()).equalsIgnoreCase(PaymentType.DEBIT_CARD.getCode())) {
//
//            JsonObject jsonRequest = prepareTxnDetails(fields, amount, returnUrl);
//
//            logger.info("SBI Request before adding payment parameters  incase of cards " + jsonRequest.toString());
//
//            String expDate = fields.get(FieldType.CARD_EXP_DT.getName());
//
//            jsonRequest.addProperty(Constants.paymentOption, Constants.paymentOptionCard);
//            jsonRequest.addProperty(Constants.card_number, fields.get(FieldType.CARD_NUMBER.getName()));
//            jsonRequest.addProperty(Constants.card_holder,
//                    fields.get(FieldType.CARD_HOLDER_NAME.getName()).length() > 45
//                            ? fields.get(FieldType.CARD_HOLDER_NAME.getName()).substring(0, 45)
//                            : fields.get(FieldType.CARD_HOLDER_NAME.getName()));
//            jsonRequest.addProperty(Constants.card_expiryMonth, expDate.substring(0, 2));
//            jsonRequest.addProperty(Constants.card_expiryYear, expDate.substring(2, 6));
//            jsonRequest.addProperty(Constants.card_cvv, fields.get(FieldType.CVV.getName()));
//
//            // logger.info("Request Prepared For SBI Cards "+jsonRequest.toString());
//            return jsonRequest.toString();
//
//        } else {
//
//            // New Request as per the below Format
//
//            // Amount=1.00|Ref_no=3913511009155223|MERCHANT NAME=1|PAY
//            // ID=1|checkSum=42b0688e4b008ab5eb3c76f7d9462bbd1b33492b36afe7815dfe239717e14815
//
//            StringBuilder req = new StringBuilder();
//            req.append(Constants.Amount);
//            req.append("=");
//            req.append(amount);
//            req.append("|");
//            req.append(Constants.Ref_no);
//            req.append("=");
//            req.append(fields.get(FieldType.PG_REF_NUM.getName()));
//            req.append("|");
//            req.append(Constants.MERCHANT_NAME);
//            req.append("=");
//            req.append("1");
//            req.append("|");
//            req.append(Constants.PAY_ID);
//            req.append("=");
//            req.append("1");
//            String checksum = Hasher.getHash(req.toString());
//            req.append("|");
//            req.append(Constants.CHECKSUM);
//            req.append("=");
//            req.append(checksum.toLowerCase());
//
//            // Old Request
//            /*
//             * req.append(Constants.PG_REF_NUM); req.append("=");
//             * req.append(fields.get(FieldType.PG_REF_NUM.getName())); req.append("|");
//             * req.append(Constants.AMOUNT); req.append("="); req.append(amount);
//             * req.append("|"); req.append(Constants.RETURN_URL); req.append("=");
//             * req.append(returnUrl); req.append("|"); req.append(Constants.CANCEL_URL);
//             * req.append("="); req.append(returnUrl); String checksum =
//             * Hasher.getHash(req.toString()); req.append("|");
//             * req.append(Constants.CHECKSUM); req.append("=");
//             * req.append(checksum.toLowerCase());
//             */
//            logger.info(
//                    "Plain Text Request to SBI " + fields.get(FieldType.PG_REF_NUM.getName()) + ":" + req.toString());
//            String encryptedRequest = sbiUtil.encrypt(req.toString());
//            logger.info(
//                    "Encrypted Request to SBI " + fields.get(FieldType.PG_REF_NUM.getName()) + ":" + encryptedRequest);
//            return encryptedRequest;
//        }
//    }

//    private JsonObject prepareTxnDetails(Fields fields, String amount, String returnUrl) {
//        JsonObject jsonRequest = new JsonObject();
//
//        if (StringUtils.isNotBlank(fields.get(FieldType.CUST_NAME.getName()))) {
//            jsonRequest.addProperty(Constants.customerName, fields.get(FieldType.CUST_NAME.getName()));
//        } else {
//            jsonRequest.addProperty(Constants.customerName, "NA");
//        }
//
//        if (StringUtils.isNotBlank(fields.get(FieldType.CUST_EMAIL.getName()))) {
//            jsonRequest.addProperty(Constants.customerEmail, fields.get(FieldType.CUST_EMAIL.getName()));
//        } else {
//            jsonRequest.addProperty(Constants.customerEmail, "support.txn@asiancheckout.com");
//        }
//
//        if (StringUtils.isNotBlank(fields.get(FieldType.CUST_PHONE.getName()))) {
//            jsonRequest.addProperty(Constants.customerPhone, fields.get(FieldType.CUST_PHONE.getName()));
//        } else {
//            jsonRequest.addProperty(Constants.customerPhone, "9999999999");
//        }
//
//        jsonRequest.addProperty(Constants.returnUrl, returnUrl);
//
//        jsonRequest.addProperty(Constants.appId, fields.get(FieldType.MERCHANT_ID.getName()));
//        jsonRequest.addProperty(Constants.orderId, fields.get(FieldType.PG_REF_NUM.getName()));
//        jsonRequest.addProperty(Constants.orderAmount, amount);
//        jsonRequest.addProperty(Constants.orderCurrency,
//                Currency.getAlphabaticCode(fields.get(FieldType.CURRENCY_CODE.getName())));
//        return jsonRequest;
//    }

//    private String expressRequest(Fields fields, Transaction transaction) throws SystemException {
//        String amount = acquirerTxnAmountProvider.amountProvider(fields);
//        String returnUrl = PropertiesManager.propertiesMap.get("SbiresponseURL");
//        JsonObject jsonRequest = prepareTxnDetails(fields, amount, returnUrl);
//        jsonRequest.addProperty(FieldType.UDF6.getName(), calculateUdf6(fields));
//        return jsonRequest.toString();
//    }

//    private String calculateUdf6(Fields fields) throws SystemException {
//        Token token = getTokenDetails(fields);
//        String cardNo = StringUtils.substring(token.getCardMask(), token.getCardMask().length() - 4,
//                token.getCardMask().length());
//        char mopType = token.getMopType().charAt(0);
//        char paymentType = token.getPaymentType().charAt(0);
//        String separator = "|";
//        String udf6 = StringUtils.join(Constants.TOKEN_FLAG, separator, token.getNetworkToken(), separator, cardNo,
//                separator, token.getPayId(), separator, mopType, separator, paymentType);
//        return udf6;
//    }

//    private Token getTokenDetails(Fields fields) throws SystemException {
//        try {
//            Token token = tokenManager.getToken(fields);
//            if (ObjectUtils.isEmpty(token)) {
//                throw new SystemException(ErrorType.INVALID_TOKEN, ErrorType.INVALID_TOKEN.getInternalMessage());
//            }
//            return token;
//
//        } catch (Exception e) {
//            throw new SystemException(ErrorType.INVALID_TOKEN, e, ErrorType.INVALID_TOKEN.getInternalMessage());
//        }
//    }

	public void getElement(String name, String value, StringBuilder xml) {
		if (null == value) {
			return;
		}

		xml.append("<");
		xml.append(name);
		xml.append(">");
		xml.append(value);
		xml.append("</");
		xml.append(name);
		xml.append(">");
	}

	public String refundRequest(Fields fields, Transaction transaction) throws SystemException {
		JSONObject req = prepareRefundReq(fields);
		String pgRefNo = fields.get(FieldType.PG_REF_NUM.getName());
		String orderId = fields.get(FieldType.ORDER_ID.getName());
		try {
			String acqId = fields.get(FieldType.ADF1.getName());
			String apiUrl = PropertiesManager.propertiesMap.get("SBI_CARD_REFUND_URL");
			// apiUrl = StringUtils.join(apiUrl, acqId, "/", pgRefNo);
			logger.info("refundRequest:: apiUrl={}, pgRefNo={}, orderId={}, req={}", apiUrl, pgRefNo, orderId, req);
			String response = "";
			response = executeApiRefund(fields, req, response, apiUrl);
			logger.info("refundRequest:: response={}, pgRefNo={}, orderId={}", response, pgRefNo, orderId);
			return decryptResponse(response).toJSONString();
		} catch (Exception e) {
			logger.error("refundRequest:: failed. pgRefNo={}", pgRefNo, e);
			throw new SystemException(ErrorType.FAILED, e.getMessage());
		}
	}

	private JSONObject prepareRefundReq(Fields fields) {
		JSONObject req = new JSONObject();
		req.put("pgInstanceId", fields.get(FieldType.ADF9.getName()));
		req.put("merchantId", fields.get(FieldType.ADF11.getName()));
		req.put("merchantReferenceNo", fields.get(FieldType.ORDER_ID.getName()));
		req.put("action", "voidorrefund");
		req.put("originalTransactionId", fields.get(FieldType.ACQ_ID.getName()));
		req.put("amount", fields.get(FieldType.TOTAL_AMOUNT.getName()));
		req.put("refundtype", "");
		req.put("ext1", "");
		String pgRefNo = fields.get(FieldType.PG_REF_NUM.getName());
		String orderId = fields.get(FieldType.ORDER_ID.getName());
		logger.info("prepareRefundReq:: plain request={}, pgRefNo={}, orderId={}", req, pgRefNo, orderId);
		return prepareEncReqre(fields, req, fields.get(FieldType.ADF1.getName()));
	}

	public Transaction toTransaction(String xml) {

		Transaction transaction = new Transaction();

		return transaction;
	}// toTransaction()

	public TransactionConverter() {
	}

	public String getTextBetweenTags(String text, String tag1, String tag2) {

		int leftIndex = text.indexOf(tag1);
		if (leftIndex == -1) {
			return null;
		}

		int rightIndex = text.indexOf(tag2);
		if (rightIndex != -1) {
			leftIndex = leftIndex + tag1.length();
			return text.substring(leftIndex, rightIndex);
		}

		return null;
	}// getTextBetweenTags()

	public void logRequest(String requestMessage, Fields fields) {
		log("Request message to SBI: TxnType = " + fields.get(FieldType.TXNTYPE.getName()) + " " + "Txn id = "
				+ fields.get(FieldType.TXN_ID.getName()) + " " + "Url= " + requestMessage, fields);
	}

	private void log(String message, Fields fields) {
		message = Pattern.compile("(<card>)([\\s\\S]*?)(</card>)").matcher(message).replaceAll("$1$3");
		message = Pattern.compile("(<pan>)([\\s\\S]*?)(</pan>)").matcher(message).replaceAll("$1$3");
		message = Pattern.compile("(<expmonth>)([\\s\\S]*?)(</expmonth>)").matcher(message).replaceAll("$1$3");
		message = Pattern.compile("(<expyear>)([\\s\\S]*?)(</expyear>)").matcher(message).replaceAll("$1$3");
		message = Pattern.compile("(<cvv2>)([\\s\\S]*?)(</cvv2>)").matcher(message).replaceAll("$1$3");
		message = Pattern.compile("(<password>)([\\s\\S]*?)(</password>)").matcher(message).replaceAll("$1$3");
		MDC.put(FieldType.INTERNAL_CUSTOM_MDC.getName(), fields.getCustomMDC());
		logger.info(message);
	}

}
