package com.pay10.shivalikNB;

import com.pay10.axisbank.netbanking.AxisBankNBTransformer;
import com.pay10.commons.exception.ErrorType;
import com.pay10.commons.exception.SystemException;
import com.pay10.commons.util.*;
import com.pay10.pg.core.util.AcquirerTxnAmountProvider;
import com.pay10.pg.core.util.CosmosUtil;
import com.pay10.pg.core.util.Processor;
import com.pay10.pg.core.util.ShivalikNBUtil;
import com.pay10.tmbNB.TMBNBProcessor;

import bsh.StringUtil;

import org.apache.commons.lang.StringUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.net.ssl.HttpsURLConnection;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.MessageDigest;
import java.util.ArrayList;

import static org.owasp.esapi.StringUtilities.isEmpty;

@Service("SHIVALIKNBProcessor")
public class ShivaliknbProcessor implements Processor {
	@Autowired
	private AcquirerTxnAmountProvider acquirerTxnAmountProvider;
	private static final Logger logger = LoggerFactory.getLogger(TMBNBProcessor.class.getName());

	@Override
	public void preProcess(Fields fields) throws SystemException {

	}

	@Override
	public void process(Fields fields) throws SystemException {
		// logger.info("process Shivalik NetBanking
		// fields=======================================: {}",
		// fields.getFieldsAsString());

		if ((fields.get(FieldType.TXNTYPE.getName()).equals(TransactionType.NEWORDER.getName())
				|| fields.get(FieldType.INTERNAL_ORIG_TXN_TYPE.getName()).equals(TransactionType.STATUS.getName())
				|| fields.get(FieldType.INTERNAL_ORIG_TXN_TYPE.getName()).equals(TransactionType.VERIFY.getName())
				|| fields.get(FieldType.INTERNAL_ORIG_TXN_TYPE.getName()).equals(TransactionType.RECO.getName())
				|| fields.get(FieldType.INTERNAL_ORIG_TXN_TYPE.getName())
						.equals(TransactionType.REFUNDRECO.getName()))) {
			return;
		}

		if (!fields.get(FieldType.ACQUIRER_TYPE.getName()).equals(AcquirerType.SHIVALIKNBBANK.getCode())) {
			return;
		}
		try {
			send(fields);
		} catch (Exception e) {
			e.printStackTrace();
		}
		logger.info(" process Shivalik NetBanking fields:++++++++++++++++++++++++++++++++++++++++ {}",
				fields.getFieldsAsString());

	}

	@Override
	public void postProcess(Fields fields) throws SystemException {

	}

	public void send(Fields fields) throws Exception {

		if (fields.get(FieldType.TXNTYPE.getName()).equalsIgnoreCase(TransactionType.ENROLL.getName())) {
			fields.put(FieldType.TXNTYPE.getName(), TransactionType.SALE.getName());
		}

		logger.info("Request Received For Create Sale Request for shivalik , OrderId={}",
				fields.get(FieldType.ORDER_ID.getName()));
		String txnType = fields.get(FieldType.TXNTYPE.getName());

		if (txnType.equals(TransactionType.SALE.getName())) {
			fields.put(FieldType.ORIG_TXN_ID.getName(), fields.get(FieldType.TXN_ID.getName()));
			if(org.apache.commons.lang3.StringUtils.isNotBlank(fields.get(FieldType.PG_REF_NUM.getName()))) {
			fields.put(FieldType.PG_REF_NUM.getName(), fields.get(FieldType.TXN_ID.getName()));
			}
			String request = saleRequest(fields);
			logger.info("Shivalik Sale Request={}", request);
			updateSaleResponse(fields, request);
		} else if (txnType.equals(TransactionType.REFUND.getName())) {
			if(org.apache.commons.lang3.StringUtils.isNotBlank(fields.get(FieldType.PG_REF_NUM.getName()))) {

			fields.put(FieldType.PG_REF_NUM.getName(), fields.get(FieldType.TXN_ID.getName()));

			}
			refundReuest(fields);
		}

		else {
			String apiUrl = PropertiesManager.propertiesMap.get("SHIVALIKNBBANKSaleUrl");
			;
			String api_key = fields.get(FieldType.ADF2.getName());

		}
	}

	private Transaction toTransactionRefund(JSONObject errorMessage) {

		Transaction refundResponse1 = new Transaction();
		logger.info("ERROR in refuund response" + errorMessage);
		refundResponse1.setStatus(errorMessage.get("code").toString());
		refundResponse1.setMessage(errorMessage.get("message").toString());
		logger.info("transcation in refund for shivlik" + refundResponse1.toString());
		return refundResponse1;
	}

	private void refundReuest(Fields fields) throws SystemException {

		String secretKey = fields.get(FieldType.ADF4.getName());
		// String secretKey = PropertiesManager.propertiesMap.get("secretKey");
		String hostUrl = PropertiesManager.propertiesMap.get("SHIVALIKNBBANKRefundUrl");
		// String salt_key = PropertiesManager.propertiesMap.get("salt_key");
		String salt_key = fields.get(FieldType.ADF3.getName());
		String hash_data;
		JSONObject Statusrequest = new JSONObject();
		Statusrequest.put("api_key", fields.get(FieldType.ADF2.getName()));

		Statusrequest.put("merchant_refund_id", fields.get(FieldType.TXN_ID.getName()));
		Statusrequest.put("transaction_id", fields.get(FieldType.RRN.getName()));
		//String amount = acquirerTxnAmountProvider.amountProvider(fields);
		String amount = Amount.toDecimal(fields.get(FieldType.AMOUNT.getName()), "356");
		Statusrequest.put("amount", amount);
		Statusrequest.put("description", "REFUND");

		ArrayList<String> hashParam = new ArrayList<String>();
		hashParam.add(Statusrequest.getString("amount"));

		hashParam.add(Statusrequest.getString("api_key"));
		hashParam.add(Statusrequest.getString("description"));
		hashParam.add(Statusrequest.getString("merchant_refund_id"));
		hashParam.add(Statusrequest.getString("transaction_id"));

		String generateHash_data = null;
		try {
			generateHash_data = generateHashNew(hashParam, salt_key);
		} catch (Exception e) {
			e.printStackTrace();
		}

		StringBuilder request = new StringBuilder();
		request.append("amount=");
		request.append(amount);
		request.append("&api_key=");
		request.append(fields.get(FieldType.ADF2.getName()));
		request.append("&description=REFUND");

		request.append("&merchant_refund_id=");
		request.append(fields.get(FieldType.TXN_ID.getName()));
		request.append("&transaction_id=");
		request.append(fields.get(FieldType.RRN.getName()));

		request.append("&hash=");
		request.append(generateHash_data);

		logger.info("request for shivalik refund" + request.toString());

		logger.info("request for shivalik refund" + request);

		String transactionENCResponse = null;
		transactionENCResponse = getResponserefund(request.toString());
		logger.info("SHIVALIK NB refund:{}", transactionENCResponse.toString());
		logger.info("SHIVALIK NB refund Response : {},{}", fields.get(FieldType.ORDER_ID.getName()),
				transactionENCResponse);

		JSONObject reponse = new JSONObject(transactionENCResponse);
		if (reponse.has("data")) {
			JSONArray arrResponse = reponse.getJSONArray("data");
			if (arrResponse.getJSONObject(0).has("refund_reference_no")
					&& org.apache.commons.lang3.StringUtils
							.isNotBlank(arrResponse.getJSONObject(0).get("refund_reference_no").toString())
					&& arrResponse.getJSONObject(0).has("refund_id") && org.apache.commons.lang3.StringUtils
							.isNotBlank(arrResponse.getJSONObject(0).get("refund_id").toString())) {
				fields.put(FieldType.STATUS.getName(), StatusType.CAPTURED.getName());
				fields.put(FieldType.RESPONSE_MESSAGE.getName(), ErrorType.SUCCESS.getResponseMessage());
				fields.put(FieldType.RESPONSE_CODE.getName(), ErrorType.SUCCESS.getCode());
				fields.put(FieldType.RRN.getName(), arrResponse.getJSONObject(0).get("refund_reference_no").toString());
				fields.put(FieldType.ACQ_ID.getName(), arrResponse.getJSONObject(0).get("refund_id").toString());

			}

		} else if (reponse.has("error")) {
			JSONObject errorMessage = reponse.getJSONObject("error");

			logger.info("SHIVALIK NB refund:{}", errorMessage);
			logger.info("SHIVALIK NB refund:{}", reponse);

			Transaction refundTransaction = new Transaction();
			refundTransaction = toTransactionRefund(errorMessage);
			ShivalikBankNBTransformer shivalikBankNBTransformer = new ShivalikBankNBTransformer(refundTransaction);
			shivalikBankNBTransformer.updateResponserefund(fields);

		} else {

			fields.put(FieldType.STATUS.getName(), StatusType.FAILED.getName());
			fields.put(FieldType.RESPONSE_MESSAGE.getName(), ErrorType.FAILED.getResponseMessage());
			fields.put(FieldType.RESPONSE_CODE.getName(), ErrorType.FAILED.getCode());

		}

	}

	public String getResponserefund(String request) {
		String response = "";
		try {
			String hostUrl = PropertiesManager.propertiesMap.get("SHIVALIKNBBANKRefundUrl");
			hostUrl = hostUrl + "?" + request;
			logger.info("request ofshivalik NB  " + request + "        url     " + hostUrl);
			HttpURLConnection connection = null;
			URL url;
			url = new URL(hostUrl);
			connection = (HttpURLConnection) url.openConnection();
			connection.setRequestMethod("POST");
			connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
			connection.setUseCaches(false);
			connection.setDoInput(true);
			connection.setDoOutput(true);
			connection.setConnectTimeout(60000);
			connection.setReadTimeout(60000);

			// Send request
			DataOutputStream wr = new DataOutputStream(connection.getOutputStream());
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

			logger.info("Response for dual verfication for shivalik bank >> " + response);

		} catch (Exception e) {
			logger.error("Exception in getting dual verfication for shivalik bank  ", e);
			return response;
		}
		return response;

	}

	public String saleRequest(Fields fields) throws Exception {
		if (fields.get(FieldType.TXNTYPE.getName()).equalsIgnoreCase(TransactionType.ENROLL.getName())) {
			fields.put(FieldType.TXNTYPE.getName(), TransactionType.SALE.getName());
		}

		if (null != fields.get(FieldType.TXNTYPE.getName())
				&& fields.get(FieldType.TXNTYPE.getName()).equalsIgnoreCase(TransactionType.REFUND.getName())) {
			// TODO PAY10-563 : Write Refund Code & return refund request string
			return "";
		}

		JSONObject saleRequest = new JSONObject();
		StringBuffer hash_data = new StringBuffer();
		// String secretKey = PropertiesManager.propertiesMap.get("secretKey");
		String secretKey = fields.get(FieldType.ADF4.getName());
		// String secret="1c30f76d3ec045e18af930c09cf104ae";//request enc key
		// String salt_key = "67a7f27b87b3422097482af47ce5136c";
		String SHIVALIKNBBANKReturnUrl = PropertiesManager.propertiesMap.get("SHIVALIKNBBANKReturnUrl");
		String salt_key = fields.get(FieldType.ADF3.getName());
		String amount = acquirerTxnAmountProvider.amountProvider(fields);
		String pgRefNo = fields.get(FieldType.PG_REF_NUM.getName());
		if (pgRefNo == null)
			fields.put(FieldType.PG_REF_NUM.getName(), fields.get(FieldType.TXN_ID.getName()));
		saleRequest.put("api_key", fields.get(FieldType.ADF2.getName()));
		// saleRequest.put("address_line_1",fields.get(FieldType.CUST_STREET_ADDRESS1.getName()));
		// saleRequest.put("address_line_2",fields.get(FieldType.CUST_STREET_ADDRESS2.getName()));
		logger.info("secretKey={}, salt_key={}", secretKey, salt_key);
		saleRequest.put("amount", amount);
		saleRequest.put("bank_code", "SHIVN");
		saleRequest.put("city", "delhi");
		saleRequest.put("country", "IND");
		saleRequest.put("currency", fields.get(FieldType.CURRENCY_CODE.getName()));
		saleRequest.put("description", "Payment Short Description");
		saleRequest.put("email", fields.get(FieldType.CUST_EMAIL.getName()));
		saleRequest.put("mode", "LIVE");
		saleRequest.put("name", fields.get(FieldType.CUST_NAME.getName()));
		saleRequest.put("order_id", fields.get(FieldType.PG_REF_NUM.getName()));
		saleRequest.put("phone", fields.get(FieldType.CUST_PHONE.getName()));
		saleRequest.put("return_url", SHIVALIKNBBANKReturnUrl);// callback url response Action http://lo
		saleRequest.put("return_url_failure", SHIVALIKNBBANKReturnUrl);
		saleRequest.put("return_url_cancel", SHIVALIKNBBANKReturnUrl);
		saleRequest.put("state", "KARNATAKA"); // fields.get(FieldType.CUST_STATE.getName())
		saleRequest.put("udf1", "");
		saleRequest.put("udf2", "");
		saleRequest.put("udf3", "");
		saleRequest.put("udf4", "");
		saleRequest.put("udf5", "");
		saleRequest.put("udf6", "");
		saleRequest.put("udf7", "");
		saleRequest.put("udf8", "");
		saleRequest.put("udf9", "");
		saleRequest.put("udf10", "");
		saleRequest.put("zip_code", "");

		// String hashParam; //= new ArrayList<String>();
		ArrayList<String> hashParam = new ArrayList<String>();

		hashParam.add(saleRequest.getString("amount"));
		hashParam.add(saleRequest.getString("api_key"));
		hashParam.add(saleRequest.getString("bank_code"));
		hashParam.add(saleRequest.getString("city"));
		hashParam.add(saleRequest.getString("country"));
		hashParam.add(saleRequest.getString("currency"));
		hashParam.add(saleRequest.getString("description"));
		hashParam.add(saleRequest.getString("email"));
		hashParam.add(saleRequest.getString("mode"));
		hashParam.add(saleRequest.getString("name"));
		hashParam.add(saleRequest.getString("order_id"));
		hashParam.add(saleRequest.getString("phone"));
		hashParam.add(saleRequest.getString("return_url"));
		hashParam.add(saleRequest.getString("return_url_failure"));
		hashParam.add(saleRequest.getString("return_url_cancel"));
		hashParam.add(saleRequest.getString("state"));
		hashParam.add(saleRequest.getString("udf1"));
		hashParam.add(saleRequest.getString("udf2"));
		hashParam.add(saleRequest.getString("udf3"));
		hashParam.add(saleRequest.getString("udf4"));
		hashParam.add(saleRequest.getString("udf5"));
		hashParam.add(saleRequest.getString("udf6"));
		hashParam.add(saleRequest.getString("udf7"));
		hashParam.add(saleRequest.getString("udf8"));
		hashParam.add(saleRequest.getString("udf9"));
		hashParam.add(saleRequest.getString("udf10"));
		hashParam.add(saleRequest.getString("zip_code"));

		logger.info("saleRequest" + saleRequest);
		logger.info("hashParam" + hashParam.toString());
		String generateHash_data = generateHashNew(hashParam, salt_key);
		// String generateHash_data = generateHash(hashParam, salt_key);
		logger.info("generateHash_data  " + generateHash_data);
		saleRequest.put("hash", generateHash_data);
		logger.info("saleRequest+hash: " + saleRequest);
		String encryptedRequest = null;
		encryptedRequest = ShivalikNBUtil.encrypt(saleRequest.toString(), secretKey);
		logger.info("encryptedRequest" + encryptedRequest);
		// updateSaleResponse(fields,encryptedRequest);
		// String hostUrl = PropertiesManager.propertiesMap.get("ShivalikNBSaleUrl");
		// String transactionENCResponse = callRESTApi(hostUrl,
		// encryptedRequest.toString(), "POST",
		// fields.get(FieldType.MERCHANT_ID.getName()));
		return encryptedRequest;
	}

	public void updateSaleResponse(Fields fields, String request) {

		fields.put(FieldType.SHIVALIK_NB_FINAL_REQUEST.getName(), request);
		fields.put(FieldType.STATUS.getName(), StatusType.SENT_TO_BANK.getName());
		fields.put(FieldType.RESPONSE_CODE.getName(), ErrorType.SUCCESS.getResponseCode());
		fields.put(FieldType.RESPONSE_MESSAGE.getName(), ErrorType.SUCCESS.getResponseMessage());

	}

	public String statusEnquiry(Fields fields) {
		String secretKey = fields.get(FieldType.ADF4.getName());
		// String secretKey = PropertiesManager.propertiesMap.get("secretKey");
		String hostUrl = PropertiesManager.propertiesMap.get("SHIVALIKNBBANKStatusEnqUrl");
		// String salt_key = PropertiesManager.propertiesMap.get("salt_key");
		String salt_key = fields.get(FieldType.ADF3.getName());
		String hash_data;
		JSONObject Statusrequest = new JSONObject();
		Statusrequest.put("api_key", fields.get(FieldType.ADF2.getName()));
		Statusrequest.put("bank_code", "SHIVN");
		Statusrequest.put("customer_phone", "");
		Statusrequest.put("Customer_email", fields.get(FieldType.CUST_EMAIL.getName()));
		Statusrequest.put("customer_name", fields.get(FieldType.CUST_NAME.getName()));
		Statusrequest.put("date_from", "");
		Statusrequest.put("date_to", "");
		Statusrequest.put("order_id", fields.get(FieldType.PG_REF_NUM.getName()));
		Statusrequest.put("response_code", "");
		ArrayList<String> hashParam = new ArrayList<String>();

		hashParam.add(Statusrequest.getString("api_key"));
		hashParam.add(Statusrequest.getString("bank_code"));
		hashParam.add(Statusrequest.getString("customer_phone"));
		hashParam.add(Statusrequest.getString("Customer_email"));
		hashParam.add(Statusrequest.getString("customer_name"));
		hashParam.add(Statusrequest.getString("date_from"));
		hashParam.add(Statusrequest.getString("date_to"));
		hashParam.add(Statusrequest.getString("order_id"));
		hashParam.add(Statusrequest.getString("response_code"));

		String generateHash_data = null;
		try {
			generateHash_data = generateHashNew(hashParam, salt_key);
		} catch (Exception e) {
			e.printStackTrace();
		}

		Statusrequest.put("hash", generateHash_data);
		String encryptedRequest = null;
		encryptedRequest = ShivalikNBUtil.encrypt(Statusrequest.toString(), secretKey);
		String transactionENCResponse = null;
		try {
			transactionENCResponse = callRESTApi(hostUrl, encryptedRequest.toString(), "POST",
					fields.get(FieldType.ADF2.getName()));
		} catch (SystemException e) {
			e.printStackTrace();
		}
		return transactionENCResponse.toString();
	}

	public JSONObject statusEnquiry(Fields fields, boolean isDual) {

		String secretKey = fields.get(FieldType.ADF4.getName());
		// String secretKey = PropertiesManager.propertiesMap.get("secretKey");
		String hostUrl = PropertiesManager.propertiesMap.get("SHIVALIKNBBANKStatusEnqUrl");
		// String salt_key = PropertiesManager.propertiesMap.get("salt_key");
		String salt_key = fields.get(FieldType.ADF3.getName());
		String hash_data;
		JSONObject Statusrequest = new JSONObject();
		Statusrequest.put("api_key", fields.get(FieldType.ADF2.getName()));
		Statusrequest.put("bank_code", "SHIVN");
		Statusrequest.put("customer_phone", "");
		Statusrequest.put("Customer_email", fields.get(FieldType.CUST_EMAIL.getName()));
		Statusrequest.put("customer_name", fields.get(FieldType.CUST_NAME.getName()));
		Statusrequest.put("date_from", "");
		Statusrequest.put("date_to", "");
		Statusrequest.put("order_id", fields.get(FieldType.PG_REF_NUM.getName()));
		if (isDual) {
			Statusrequest.put("response_code", fields.get(FieldType.RRN.getName()));
		} else {
			Statusrequest.put("response_code", "");
		}

		ArrayList<String> hashParam = new ArrayList<String>();

		hashParam.add(Statusrequest.getString("api_key"));
		hashParam.add(Statusrequest.getString("bank_code"));
		hashParam.add(Statusrequest.getString("customer_phone"));
		hashParam.add(Statusrequest.getString("Customer_email"));
		hashParam.add(Statusrequest.getString("customer_name"));
		hashParam.add(Statusrequest.getString("date_from"));
		hashParam.add(Statusrequest.getString("date_to"));
		hashParam.add(Statusrequest.getString("order_id"));
		hashParam.add(Statusrequest.getString("response_code"));

		/*
		 * hash_data = Statusrequest.get("api_key") + "|" +
		 * Statusrequest.get("bank_code") + "|" + Statusrequest.get("customer_phone") +
		 * "|" + Statusrequest.get("Customer_email") + "|" +
		 * Statusrequest.get("customer_name") + "|" + Statusrequest.get("date_from") +
		 * "|" + Statusrequest.get("date_to") + "|" + Statusrequest.get("order_id") +
		 * "|" + Statusrequest.get("response_code");
		 */
		String generateHash_data = null;
		try {
			generateHash_data = generateHashNew(hashParam, salt_key);
		} catch (Exception e) {
			e.printStackTrace();
		}

		Statusrequest.put("hash", generateHash_data);

		if (isDual) {
			logger.info("SHIVALIK NB dual verification Plain request:{},pgRefNo:{}", Statusrequest.toString(),
					fields.get(FieldType.PG_REF_NUM.getName()));
		} else {
			logger.info("SHIVALIK NB Status check Plain request:{},pgRefNo:{}", Statusrequest.toString(),
					fields.get(FieldType.PG_REF_NUM.getName()));
		}
		String encryptedRequest = null;
		encryptedRequest = ShivalikNBUtil.encrypt(Statusrequest.toString(), secretKey);

		String transactionENCResponse = null;
		try {
			transactionENCResponse = callRESTApi(hostUrl, encryptedRequest.toString(), "POST",
					fields.get(FieldType.ADF2.getName()));
			logger.info("SHIVALIK NB transactionENCResponse:{}", transactionENCResponse.toString());
			String transactionStatusResponse = ShivalikNBUtil.decrypt(transactionENCResponse, secretKey);
			logger.info("SHIVALIK NB transactionStatus  or dual verification Response : {},{}",
					fields.get(FieldType.ORDER_ID.getName()), transactionENCResponse);

			logger.info("SHIVALIK NB transactionStatus  or dual verification  Response : {},{}",
					fields.get(FieldType.ORDER_ID.getName()), transactionStatusResponse);
			if (org.apache.commons.lang.StringUtils.isNotBlank(transactionStatusResponse)) {
				JSONObject statusResponseJson = new JSONObject(transactionStatusResponse);
				return statusResponseJson;
			}
			/*
			 * 1 - Decrypt status check response 2- Convert decrypted response in JSON
			 * boject 3- then return this JSON object
			 */

		} catch (SystemException e) {
			e.printStackTrace();
		}
		return null;
	}

	public String refund(Fields fields) {

		return null;
	}

	private String generateHash(String requestData, String saltKey) throws Exception {
		/*
		 * String respHashData = saltKey; for (int i = 0; i < requestData.length(); i++)
		 * { if (isEmpty(requestData)) continue; else respHashData += '|' +
		 * respHashData; }
		 */
		String respHashData = saltKey;
		for (int i = 0; i < requestData.length(); i++) {
			if (isEmpty(requestData))
				continue;
			else
				respHashData += '|' + respHashData;
		}
		return hashCalculate(respHashData);
		// return hashCalculate(requestData);
	}

	private Boolean generateHash(String[] responseData, String saltKey, String responseHash) throws Exception {
		String tempHashData = saltKey;
		for (int i = 0; i < responseData.length; i++) {
			if (isEmpty(responseData[i]))
				continue;
			else
				tempHashData += '|' + responseData[i];
		}
		if (responseHash.equals(new ShivalikNBUtil().hashCalculate(tempHashData)))
			return true;
		else
			return false;
	}

	public static String hashCalculate(String str) throws Exception {
		MessageDigest md = MessageDigest.getInstance("SHA-512");
		md.update(str.getBytes("UTF-8"));
		byte byteData[] = md.digest();
// convert the byte to hex format method 1
		StringBuffer hashCodeBuffer = new StringBuffer();
		for (int i = 0; i < byteData.length; i++) {
			hashCodeBuffer.append(Integer.toString((byteData[i] & 0xff) + 0x100, 16).substring(1));
		}
		return hashCodeBuffer.toString().toUpperCase();
	}

	public static String callRESTApi(String apiUrl, String request, String method, String api_key)
			throws SystemException {

		StringBuilder serverResponse = new StringBuilder();

		HttpsURLConnection connection = null;

		try {

			URL url = new URL(apiUrl);
			// disableSSLCertificateChecking();
			connection = (HttpsURLConnection) url.openConnection();

			connection.setRequestMethod("POST");

			// connection.setRequestProperty("Content-Type", "text/plain");
			connection.setRequestProperty("Content-Type", " text/plain");
			connection.setRequestProperty("Content-Length", "" + request.length());
			connection.setRequestProperty("Content-Language", "en-US");
			connection.setRequestProperty("api_key", api_key);
			connection.setUseCaches(false);
			connection.setDoOutput(true);
			connection.setDoInput(true);

			// Send request
			OutputStream outputStream = connection.getOutputStream();
			DataOutputStream wr = new DataOutputStream(outputStream);
			wr.writeBytes(request.toString());
			wr.close();

			// Get Response
			InputStream is = connection.getInputStream();
			BufferedReader rd = new BufferedReader(new InputStreamReader(is));
			String line;

			int code = ((HttpURLConnection) connection).getResponseCode();// 200, 401,404, 500
			int firstDigitOfCode = Integer.parseInt(Integer.toString(code).substring(0, 1));
			if (firstDigitOfCode == 4 || firstDigitOfCode == 5) {
				logger.error("HTTP Response code of txn [SHIVALIK NB] :" + code);
				throw new SystemException(ErrorType.ACUIRER_DOWN, "Network Exception with shivalik nb " + apiUrl);
			}
			while ((line = rd.readLine()) != null) {
				serverResponse.append(line);

			}
			rd.close();

		} catch (Exception e) {
			logger.error("Exception in HTTP call SHIVALIK NB ::" + apiUrl, e);
			logger.error("HTTP Response code of txn [SHIVALIK NB] :::");
			throw new SystemException(ErrorType.ACUIRER_DOWN, "Network Exception with shivalik nb" + apiUrl);
		} finally {
			if (connection != null) {
				connection.disconnect();
			}
		}
		return serverResponse.toString();
	}

	public String generateHashNew(ArrayList<String> requestData, String saltKey) throws Exception {

		String respHashData = saltKey;
		for (int i = 0; i < requestData.size(); i++) {
			if (StringUtils.isBlank(requestData.get(i)))
				continue;
			else
				respHashData += '|' + requestData.get(i);
		}
		System.out.println("respHashData " + respHashData);
		return hashCalculate(respHashData);
	}
}
