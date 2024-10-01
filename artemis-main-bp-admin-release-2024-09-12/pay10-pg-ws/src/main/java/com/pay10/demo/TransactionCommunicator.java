package com.pay10.demo;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.pay10.commons.exception.ErrorType;
import com.pay10.commons.exception.SystemException;
import com.pay10.commons.util.FieldType;
import com.pay10.commons.util.Fields;
import com.pay10.commons.util.PropertiesManager;
import com.pay10.commons.util.StatusType;
import com.pay10.commons.util.TransactionType;

@Service("demoTransactionCommunicator")
public class TransactionCommunicator {

	private static Logger logger = LoggerFactory.getLogger(TransactionCommunicator.class.getName());

	public void updateSaleResponse(Fields fields, String request) {
		
		fields.put(FieldType.DEMO_FINAL_REQUEST.getName(), request);
		fields.put(FieldType.STATUS.getName(), StatusType.SENT_TO_BANK.getName());
		fields.put(FieldType.RESPONSE_CODE.getName(), ErrorType.SUCCESS.getResponseCode());
		fields.put(FieldType.RESPONSE_MESSAGE.getName(), ErrorType.SUCCESS.getResponseMessage());
	}

	@SuppressWarnings("incomplete-switch")
	public String getResponse(String request, Fields fields) throws SystemException {

		String hostUrl = "";
		String response = "";

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
			hostUrl = PropertiesManager.propertiesMap.get(Constants.REFUND_REQUEST_URL);
			response = refundPostRequest(request, hostUrl);
			break;
		case STATUS:
			hostUrl = PropertiesManager.propertiesMap.get(Constants.STATUS_ENQ_REQUEST_URL);
			response = statusEnqPostRequest(request, hostUrl);
			break;
		}
		return response;

	}

	public String refundPostRequest(String request, String hostUrl) throws SystemException {
		String response = "";

		try {
			
			HttpURLConnection connection = null;
			URL url;
			url = new URL(hostUrl);
			connection = (HttpURLConnection) url.openConnection();
			connection.setRequestMethod("POST");
			connection.setRequestProperty("Content-Type","application/x-www-form-urlencoded");

			connection.setUseCaches(false);
			connection.setDoInput(true);
			connection.setDoOutput(true);
		    connection.setConnectTimeout(60000);
		    connection.setReadTimeout(60000);

			// Send request
			DataOutputStream wr = new DataOutputStream(
					connection.getOutputStream());
			wr.writeBytes(request);
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
			
			logger.info("Response for refund transaction >> " + response);

		} catch (Exception e) {
			logger.error("Exception in getting Refund respose for Cashfree", e);
			response = "{\"message\":\"Error in Refund.\",\"refundId\":NA,\"status\":\"FAILED\"}";
			return response;
		}
		return response;
	}

	public String statusEnqPostRequest(String request, String hostUrl) throws SystemException {
		String response = "";

		try {
			
			logger.info("Cashfree Status Enquiry Request " + request);
			HttpURLConnection connection = null;
			URL url;
			url = new URL(hostUrl);
			connection = (HttpURLConnection) url.openConnection();
			connection.setRequestMethod("POST");
			connection.setRequestProperty("Content-Type","application/x-www-form-urlencoded");

			connection.setUseCaches(false);
			connection.setDoInput(true);
			connection.setDoOutput(true);
		    connection.setConnectTimeout(60000);
		    connection.setReadTimeout(60000);

			// Send request
			DataOutputStream wr = new DataOutputStream(
					connection.getOutputStream());
			wr.writeBytes(request);
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
			
			logger.info("Response for Cashfree Status Enquiry transaction >> " + response);

		} catch (Exception e) {
			logger.error("Exception in getting Status Enquiry respose for Cashfree", e);
		}
		return response;
	}

}
