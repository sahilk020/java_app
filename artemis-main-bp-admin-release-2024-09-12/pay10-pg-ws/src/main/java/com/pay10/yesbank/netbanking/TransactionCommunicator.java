package com.pay10.yesbank.netbanking;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.LinkedHashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.pay10.commons.exception.ErrorType;
import com.pay10.commons.exception.SystemException;
import com.pay10.commons.util.FieldType;
import com.pay10.commons.util.Fields;
import com.pay10.commons.util.PropertiesManager;
import com.pay10.commons.util.StatusType;
import com.pay10.commons.util.TransactionType;
import com.pay10.pg.core.util.YesBankNBEncDecService;

@Service("yesbankNBTransactionCommunicator")
public class TransactionCommunicator {

	private static Logger logger = LoggerFactory.getLogger(TransactionCommunicator.class.getName());

	@Autowired
	YesBankNBEncDecService yesBankNBEncDecService;

	public void updateSaleResponse(Fields fields, String request) {

		fields.put(FieldType.YESBANKNB_FINAL_REQUEST.getName(), request);
		fields.put(FieldType.STATUS.getName(), StatusType.SENT_TO_BANK.getName());
		fields.put(FieldType.RESPONSE_CODE.getName(), ErrorType.SUCCESS.getResponseCode());
		fields.put(FieldType.RESPONSE_MESSAGE.getName(), ErrorType.SUCCESS.getResponseMessage());

	}

	@SuppressWarnings("incomplete-switch")
	public String getResponse(String request, Fields fields) throws SystemException {

		String hostUrl = "";

		try {

			TransactionType transactionType = TransactionType.getInstance(fields.get(FieldType.TXNTYPE.getName()));
			switch (transactionType) {
			case SALE:
			case AUTHORISE:
				break;
			case ENROLL:
				break;
			case CAPTURE:
				break;
			case REFUND:
				hostUrl = PropertiesManager.propertiesMap.get(Constants.YESBANK_REFUND_REQUEST_URL);
				break;
			case STATUS:
				hostUrl = PropertiesManager.propertiesMap.get(Constants.YESBANK_STATUS_ENQ_REQUEST_URL);
				break;
			}

			logger.info("Request message to yes bank net banking : TxnType = " + fields.get(FieldType.TXNTYPE.getName())
					+ " " + "Order Id = " + fields.get(FieldType.ORDER_ID.getName()) + " Request >>>>> " + request);

			URL url = new URL(hostUrl);
			Map<String, Object> params = new LinkedHashMap<>();
			params.put("encdata", request);
			params.put("payeeid", fields.get(FieldType.ADF10.getName()));

			StringBuilder postData = new StringBuilder();
			for (Map.Entry<String, Object> param : params.entrySet()) {
				if (postData.length() != 0)
					postData.append('&');
				postData.append(URLEncoder.encode(param.getKey(), "UTF-8"));
				postData.append('=');
				postData.append(URLEncoder.encode(String.valueOf(param.getValue()), "UTF-8"));
			}
			byte[] postDataBytes = postData.toString().getBytes("UTF-8");

			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setRequestMethod("POST");
			conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
			conn.setRequestProperty("Content-Length", String.valueOf(postDataBytes.length));
			conn.setRequestProperty("payloadtype", "json");
			conn.setDoOutput(true);
			conn.getOutputStream().write(postDataBytes);

			Reader in = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"));

			StringBuilder sb = new StringBuilder();
			for (int c; (c = in.read()) >= 0;)
				sb.append((char) c);
			String xmlResponse = sb.toString();
			return xmlResponse;

		} catch (Exception e) {
			logger.error("Exception in yesbank NB txn Communicator", e);
		}
		return null;

	}


	public String getEnqResponse(String request, String hostUrl, Fields fields) throws SystemException {

		try {
			logger.info(
					"Request message to yes bank net banking : TxnType =Double verification  Request >>>>> " + request);

			Map<String, Object> params = new LinkedHashMap<>();
			params.put("encdata", request);
			params.put("PID", fields.get(FieldType.MERCHANT_ID.getName()));

			StringBuilder postData = new StringBuilder();
			for (Map.Entry<String, Object> param : params.entrySet()) {
				if (postData.length() != 0)
					postData.append('&');
				postData.append(URLEncoder.encode(param.getKey(), "UTF-8"));
				postData.append('=');
				postData.append(URLEncoder.encode(String.valueOf(param.getValue()), "UTF-8"));
			}
			
			String strUrl = hostUrl+"?"+postData.toString();
			logger.info("Sending GET requst for status enq to YESBANK URL " + strUrl);
			URL url = new URL(strUrl);
			
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setRequestMethod("GET");
			
			/*
			 * byte[] postDataBytes = postData.toString().getBytes("UTF-8");
			 * 
			 * HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			 * conn.setRequestMethod("GET"); conn.setRequestProperty("Content-Type",
			 * "application/x-www-form-urlencoded");
			 * conn.setRequestProperty("Content-Length",
			 * String.valueOf(postDataBytes.length)); conn.setDoOutput(true);
			 * conn.getOutputStream().write(postDataBytes);
			 */

			Reader in = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"));

			StringBuilder sb = new StringBuilder();
			for (int c; (c = in.read()) >= 0;)
				sb.append((char) c);
			String response = sb.toString();
			String key = fields.get(FieldType.TXN_KEY.getName());
			String res = "";
			logger.info("Yes bank double verfication response : " + response);
			if (response != null) {
				response = yesBankNBEncDecService.decrypt(response, key);
				logger.info("Yes bank double verfication response after decryption  : " + response);

			}

			return response;

		} catch (Exception e) {
			e.printStackTrace();
			logger.error("Exception in yesbank NB txn Communicator", e);
		}
		return null;

	}

	public static String getTextBetweenTags(String text, String tag1, String tag2) {

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
	}

}
