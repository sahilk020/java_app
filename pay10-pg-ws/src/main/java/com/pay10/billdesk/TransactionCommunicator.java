package com.pay10.billdesk;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.methods.PostMethod;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.cookie.CookiePolicy;
import org.apache.commons.httpclient.methods.PostMethod;

import com.pay10.commons.exception.ErrorType;
import com.pay10.commons.exception.SystemException;
import com.pay10.commons.util.FieldType;
import com.pay10.commons.util.Fields;
import com.pay10.commons.util.PropertiesManager;
import com.pay10.commons.util.StatusType;
import com.pay10.commons.util.TransactionType;

@Service("billdeskTransactionCommunicator")
public class TransactionCommunicator {

	private static Logger logger = LoggerFactory.getLogger(TransactionCommunicator.class.getName());

	public void updateSaleResponse(Fields fields, String request) {
		logger.info("Request to Billdesk: TxnType = " + fields.get(FieldType.TXNTYPE.getName()) + " " + "Txn id = "
				+ fields.get(FieldType.TXN_ID.getName()) + " " + request);
		fields.put(FieldType.BILLDESK_FINAL_REQUEST.getName(), request);
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
			try {
				response = executePost(request, hostUrl, fields);
			} catch (Exception exception) {
				logger.error("Exception : " + exception.getMessage());
			}
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
			PostMethod postMethod = new PostMethod(hostUrl);
			postMethod.addParameter("msg", request);

			HttpClient httpClient = new HttpClient();
			httpClient.executeMethod(postMethod);

			if (postMethod.getStatusCode() == HttpStatus.SC_OK) {
				response = postMethod.getResponseBodyAsString();
				return response;

			} else {
				logger.error("Exception in getting Refund respose for billdesk , HTTP response = "
						+ postMethod.getStatusCode());
			}
		} catch (Exception e) {
			logger.error("Exception in getting Refund respose for billdesk", e);
		}
		return response;
	}

	public String statusEnqPostRequest(String request, String hostUrl) throws SystemException {
		String response = "";

		try {
			PostMethod postMethod = new PostMethod(hostUrl);
			postMethod.addParameter("msg", request);

			HttpClient httpClient = new HttpClient();
			httpClient.executeMethod(postMethod);

			if (postMethod.getStatusCode() == HttpStatus.SC_OK) {
				response = postMethod.getResponseBodyAsString();
				logger.info("Billdesk Status Enquiry Response >>> " + response);
				return response;

			} else {
				logger.error("Exception in getting status enq respose for billdesk , HTTP response = "
						+ postMethod.getStatusCode());
			}
		} catch (Exception e) {
			logger.error("Exception in getting status enq respose for billdesk", e);
		}
		return response;
	}

	
	public String executePost(String urlParameters, String hostUrl, Fields fields) throws Exception {
		logger.info("Refund Request for BillDesk: Txn id = " + fields.get(FieldType.TXN_ID.getName()) + " "
				+ urlParameters);
		String response = "";

		HttpURLConnection connection = null;
		URL url;
		try {
			// Create connection
			url = new URL(hostUrl);

			connection = (HttpURLConnection) url.openConnection();
			connection.setRequestMethod("POST");
			connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");

			connection.setRequestProperty("Content-Length", "" + Integer.toString(urlParameters.getBytes().length));
			connection.setRequestProperty("Content-Language", "en-US");

			connection.setUseCaches(false);
			connection.setDoInput(true);
			connection.setDoOutput(true);
			connection.setConnectTimeout(60000);
			connection.setReadTimeout(60000);

			// Send request
			DataOutputStream wr = new DataOutputStream(connection.getOutputStream());
			wr.writeBytes(urlParameters);
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
			logger.info("Response Received from BillDesk: TxnType = " + fields.get(FieldType.TXNTYPE.getName()) + " "
					+ "Txn id = " + fields.get(FieldType.TXN_ID.getName()) + " " + response);
			return response;
		}

		catch (Exception exception) {
			logger.error("Exception : ", exception);
			throw new SystemException(ErrorType.ACQUIRER_ERROR, "Unable to get respose from BillDesk");
		} finally {

			if (connection != null) {
				connection.disconnect();
			}
		}
	}
	
	public String getUpiResponse(String request, Fields fields) throws SystemException {
		
		String hostUrl = PropertiesManager.propertiesMap.get(Constants.BILLDESK_UPI_SALE_URL);
		PostMethod postMethod = new PostMethod(hostUrl);
		
		postMethod.addParameter("msg", request.toString());
		if (fields.get(FieldType.INTERNAL_CUST_IP.getName()) == null) {
			postMethod.addParameter("ipaddress", "49.36.131.101");
		}
		else {
			postMethod.addParameter("ipaddress", fields.get(FieldType.INTERNAL_CUST_IP.getName()));
		}
		
		postMethod.addParameter("useragent", fields.get(FieldType.INTERNAL_HEADER_USER_AGENT.getName()));

		return upiSend(postMethod, hostUrl, fields);

	}

	public String upiSend(HttpMethod httpMethod, String hostUrl, Fields fields) throws SystemException {
		String response = "";

		try {
			HttpClient httpClient = new HttpClient();
			httpClient.executeMethod(httpMethod);

			if (httpMethod.getStatusCode() == HttpStatus.SC_OK) {
				response = httpMethod.getResponseBodyAsString();
				logger.info("Response from BillDesk: " + fields.get(FieldType.TXNTYPE.getName()) + " " + "Txn id = "
						+ fields.get(FieldType.TXN_ID.getName()) + response);
			} else {
				throw new SystemException(ErrorType.INTERNAL_SYSTEM_ERROR, "Network Exception with billdesk "
						+ hostUrl.toString() + "recieved response code" + httpMethod.getStatusCode());
			}
		} catch (IOException ioException) {
			throw new SystemException(ErrorType.INTERNAL_SYSTEM_ERROR, ioException,
					"Network Exception with BillDesk  " + hostUrl.toString());
		}
		return response;

	}
}
