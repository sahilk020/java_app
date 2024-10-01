package com.pay10.icici.upi;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

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

/**
 * @author Rahul
 *
 */
@Service("iciciUpiTransactionCommunicator")
public class TransactionCommunicator {

	private static Logger logger = LoggerFactory.getLogger(TransactionCommunicator.class.getName());

	public void updateInvalidVpaResponse(Fields fields, String response) {

		fields.put(FieldType.STATUS.getName(), StatusType.REJECTED.getName());
		fields.put(FieldType.RESPONSE_CODE.getName(), ErrorType.INVALID_VPA.getResponseCode());
		fields.put(FieldType.PG_TXN_MESSAGE.getName(), ErrorType.INVALID_VPA.getResponseMessage());

	}

	public void updateSaleResponse(Fields fields, Transaction transactionResponse) {

		String statusType = transactionResponse.getResponseStatus();
		if (statusType.equals(Constants.SUCCESS_REPONSE)) {
			fields.put(FieldType.STATUS.getName(), StatusType.SENT_TO_BANK.getName());
			fields.put(FieldType.RESPONSE_CODE.getName(), ErrorType.SUCCESS.getResponseCode());
			fields.put(FieldType.RESPONSE_MESSAGE.getName(), ErrorType.SUCCESS.getResponseMessage());
		} else {
			fields.put(FieldType.STATUS.getName(), StatusType.REJECTED.getName());
			fields.put(FieldType.RESPONSE_CODE.getName(), ErrorType.REJECTED.getResponseCode());
			fields.put(FieldType.RESPONSE_MESSAGE.getName(), ErrorType.REJECTED.getResponseMessage());
		}

	}

	@SuppressWarnings("incomplete-switch")
	public String getVPAResponse(String request, Fields fields) throws SystemException {
		StringBuilder serverResponse = new StringBuilder();
		String hostUrl = PropertiesManager.propertiesMap.get(Constants.ICICI_UPI_VPA_VAL_URL);
		logger.info("Request sent to bank URL -- " + hostUrl);
		logger.info("Request sent to bank " + request);
		HttpsURLConnection connection = null;
		HttpURLConnection simulatorConn = null;

		try {

			// Create connection

			URL url = new URL(hostUrl);
			connection = (HttpsURLConnection) url.openConnection();

			connection.setRequestMethod("POST");
			connection.setRequestProperty("Content-Type", "application/json");
			connection.setRequestProperty("Content-Length", String.valueOf(request.length()));
			connection.setRequestProperty("Content-Language", "en-US");
			connection.setRequestProperty("cache-control", "no-cache");
			connection.setRequestProperty("accept-language", "en-US,en");
			connection.setRequestProperty("accept", "*/*, accept-encoding:*");

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

			int code = ((HttpURLConnection) connection).getResponseCode();
			int firstDigitOfCode = Integer.parseInt(Integer.toString(code).substring(0, 1));
			if (firstDigitOfCode == 4 || firstDigitOfCode == 5) {
				fields.put(FieldType.STATUS.getName(), StatusType.ACQUIRER_DOWN.getName());
				logger.error("Response code of txn :" + code);
				throw new SystemException(ErrorType.ACUIRER_DOWN,
						"Network Exception with icici Upi " + hostUrl.toString());
			}

			while ((line = rd.readLine()) != null) {
				serverResponse.append(line);

			}
			rd.close();

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (connection != null) {
				connection.disconnect();
			}
		}

		// JSONObject response = new JSONObject(serverResponse.toString());
		return serverResponse.toString();
	}

	@SuppressWarnings("incomplete-switch")
	public String getTokenStatusResponse(String request, Fields fields) throws SystemException {
		StringBuilder serverResponse = new StringBuilder();
		String hostUrl = PropertiesManager.propertiesMap.get(Constants.ICICI_UPI_CALLBACK_STATUS_URL);
		logger.info("Request sent to bank URL -- " + hostUrl);
		logger.info("Request sent to bank " + request);
		HttpsURLConnection connection = null;
		HttpURLConnection simulatorConn = null;

		try {

			// Create connection

			URL url = new URL(hostUrl);
			connection = (HttpsURLConnection) url.openConnection();

			connection.setRequestMethod("POST");
			connection.setRequestProperty("Content-Type", "application/json");
			connection.setRequestProperty("Content-Length", String.valueOf(request.length()));
			connection.setRequestProperty("accept", "*/*");
			connection.setRequestProperty("accept-encoding", "*");
			connection.setRequestProperty("accept-language", "en-US,en");
			connection.setRequestProperty("q", "0");
			connection.setRequestProperty("q", "0.8,hi");
			connection.setRequestProperty("q", "0.6");
			connection.setRequestProperty("cache-control", "no-cache");

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

			int code = ((HttpURLConnection) connection).getResponseCode();
			int firstDigitOfCode = Integer.parseInt(Integer.toString(code).substring(0, 1));
			if (firstDigitOfCode == 4 || firstDigitOfCode == 5) {
				fields.put(FieldType.STATUS.getName(), StatusType.ACQUIRER_DOWN.getName());
				logger.error("Response code of txn :" + code);
				throw new SystemException(ErrorType.ACUIRER_DOWN,
						"Network Exception with icici Upi " + hostUrl.toString());
			}

			while ((line = rd.readLine()) != null) {
				serverResponse.append(line);

			}
			rd.close();

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (connection != null) {
				connection.disconnect();
			}
		}

		// JSONObject response = new JSONObject(serverResponse.toString());
		return serverResponse.toString();
	}

	
	@SuppressWarnings("incomplete-switch")
	public String getResponse(String request, Fields fields) throws SystemException {
		StringBuilder serverResponse = new StringBuilder();
		StringBuffer hostUrl = new StringBuffer();
		String merchantId = fields.get(FieldType.MERCHANT_ID.getName());
		TransactionType transactionType = TransactionType.getInstance(fields.get(FieldType.TXNTYPE.getName()));
		switch (transactionType) {
		case SALE:
			hostUrl.append(PropertiesManager.propertiesMap.get(Constants.ICICI_UPI_SALE_URL));
			break;
		case REFUND:
			hostUrl.append(PropertiesManager.propertiesMap.get(Constants.ICICI_UPI_REFUND_URL));
			break;
		case ENQUIRY:
			hostUrl.append(PropertiesManager.propertiesMap.get(Constants.ICICI_UPI_STATUS_ENQ_URL));

		}
		hostUrl.append(merchantId);
		// logger.info("Request sent to bank " + request);
		HttpsURLConnection connection = null;
		HttpURLConnection simulatorConn = null;
		try {

			// Create connection

			URL url = new URL(hostUrl.toString());
			connection = (HttpsURLConnection) url.openConnection();

			connection.setRequestMethod("POST");
//				connection.setRequestProperty("Content-Type", "application/json");
			connection.setRequestProperty("Content-Length", String.valueOf(request.length()));
			connection.setRequestProperty("accept", "*/*");
			connection.setRequestProperty("accept-encoding", "*");
			connection.setRequestProperty("accept-language", "en-US,en");
			connection.setRequestProperty("q", "0");
			connection.setRequestProperty("q", "0.8,hi");
			connection.setRequestProperty("q", "0.6");
			connection.setRequestProperty("cache-control", "no-cache");

			connection.setUseCaches(false);
			connection.setDoOutput(true);
			connection.setDoInput(true);

			// Send request
			OutputStream outputStream = connection.getOutputStream();
			DataOutputStream wr = new DataOutputStream(outputStream);
			wr.writeBytes(request);
			wr.close();

			// Get Response
			InputStream is = connection.getInputStream();
			BufferedReader rd = new BufferedReader(new InputStreamReader(is));
			String line;
			int code = ((HttpURLConnection) connection).getResponseCode();
			int firstDigitOfCode = Integer.parseInt(Integer.toString(code).substring(0, 1));
			if (firstDigitOfCode == 4 || firstDigitOfCode == 5) {
				fields.put(FieldType.STATUS.getName(), StatusType.ACQUIRER_DOWN.getName());
				logger.error("Response code of txn :" + code);
				throw new SystemException(ErrorType.ACUIRER_DOWN,
						"Network Exception with ICICI Upi " + hostUrl.toString());
			}

			while ((line = rd.readLine()) != null) {
				serverResponse.append(line);

			}
			rd.close();

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (connection != null) {
				connection.disconnect();
			}
		}

		// JSONObject response = new JSONObject(serverResponse.toString());
		return serverResponse.toString();
	}

}
