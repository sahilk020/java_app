package com.pay10.icici.mpgs;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.URL;
import java.util.regex.Pattern;

import javax.net.ssl.HttpsURLConnection;

import org.apache.commons.codec.binary.Base64;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.pay10.commons.exception.SystemException;
import com.pay10.commons.util.FieldType;
import com.pay10.commons.util.Fields;
import com.pay10.commons.util.PropertiesManager;
import com.pay10.commons.util.TransactionType;
import com.pay10.federal.Constants;

/**
 * @author Rahul
 *
 */
@Service("iciciMpgsTransactionCommunicator")
public class TransactionCommunicator {

	private static Logger logger = LoggerFactory.getLogger(TransactionCommunicator.class.getName());
	
	@SuppressWarnings("incomplete-switch")
	public JSONObject getSaleResponse(String request, Fields fields) throws SystemException {

		StringBuilder serverResponse = new StringBuilder();
		String hostUrl = "";

		TransactionType transactionType = TransactionType.getInstance(fields.get(FieldType.TXNTYPE.getName()));
		switch (transactionType) {
		case ENROLL:
			hostUrl = PropertiesManager.propertiesMap.get(Constants.ICICI_MPGS_ENROLLMENT_URL);
			hostUrl = hostUrl.concat(fields.get(FieldType.MERCHANT_ID.getName()));
			hostUrl = hostUrl.concat("/3DSecureId/");
			hostUrl = hostUrl.concat(fields.get(FieldType.TXN_ID.getName()));
			break;
		case SALE:
			hostUrl = PropertiesManager.propertiesMap.get(Constants.ICICI_MPGS_SALE_URL);
			hostUrl = hostUrl.concat(fields.get(FieldType.MERCHANT_ID.getName()));
			hostUrl = hostUrl.concat("/order/");
			hostUrl = hostUrl.concat(fields.get(FieldType.PG_REF_NUM.getName()));
			hostUrl = hostUrl.concat("/transaction/");
			hostUrl = hostUrl.concat(fields.get(FieldType.PG_REF_NUM.getName()));
			break;
		case REFUND:
			hostUrl = PropertiesManager.propertiesMap.get(Constants.ICICI_MPGS_REFUND_URL);
			hostUrl = hostUrl.concat(fields.get(FieldType.MERCHANT_ID.getName()));
			hostUrl = hostUrl.concat("/order/");
			hostUrl = hostUrl.concat(fields.get(FieldType.ORIG_TXN_ID.getName()));
			hostUrl = hostUrl.concat("/transaction/");
			hostUrl = hostUrl.concat(fields.get(FieldType.PG_REF_NUM.getName()));
			break;
		case STATUS:
			hostUrl = PropertiesManager.propertiesMap.get(Constants.ICICI_MPGS_STATUS_ENQ_URL);
			break;
		}

		HttpsURLConnection connection = null;
		logRequest(request.toString(), hostUrl, fields);
		try {

			// Create connection
			String userName = "merchant.".concat(fields.get(FieldType.MERCHANT_ID.getName()));
			String pass = fields.get(FieldType.PASSWORD.getName());

			String authString = userName + ":" + pass;
			byte[] authEncBytes = Base64.encodeBase64(authString.getBytes());
			String authStringEnc = new String(authEncBytes);

			URL url = new URL(hostUrl);
			connection = (HttpsURLConnection) url.openConnection();
			connection.setRequestProperty("Authorization", "Basic " + authStringEnc);

			connection.setRequestMethod("PUT");
			connection.setRequestProperty("Content-Type", "application/text");
			connection.setRequestProperty("Content-Length", request.toString());
			connection.setRequestProperty("Content-Language", "en-US");

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

			while ((line = rd.readLine()) != null) {
				serverResponse.append(line);
				serverResponse.append('\r');
			}
			rd.close();

		} catch (Exception e) {
			logger.info("Exception in MPGS TransactionCommunicator " + e.getMessage());
		} finally {
			if (connection != null) {
				connection.disconnect();
			}
		}

		JSONObject response = new JSONObject(serverResponse.toString());
		logResponse(serverResponse.toString(), fields);
		//logger.info("Response received from Icici MPGS bank " + serverResponse.toString());
		return response;

	}

	@SuppressWarnings("incomplete-switch")
	public JSONObject getResponse(JSONObject request, Fields fields) throws SystemException {

		StringBuilder serverResponse = new StringBuilder();
		String hostUrl = "";

		TransactionType transactionType = TransactionType.getInstance(fields.get(FieldType.TXNTYPE.getName()));
		switch (transactionType) {
		case ENROLL:
			hostUrl = PropertiesManager.propertiesMap.get(Constants.ICICI_MPGS_ENROLLMENT_URL);
			hostUrl = hostUrl.concat(fields.get(FieldType.MERCHANT_ID.getName()));
			hostUrl = hostUrl.concat("/3DSecureId/");
			hostUrl = hostUrl.concat(fields.get(FieldType.TXN_ID.getName()));
			break;
		case SALE:
			hostUrl = PropertiesManager.propertiesMap.get(Constants.ICICI_MPGS_SALE_URL);
			hostUrl = hostUrl.concat(fields.get(FieldType.MERCHANT_ID.getName()));
			hostUrl = hostUrl.concat("/order/");
			hostUrl = hostUrl.concat(fields.get(FieldType.PG_REF_NUM.getName()));
			hostUrl = hostUrl.concat("/transaction/");
			hostUrl = hostUrl.concat(fields.get(FieldType.OID.getName()));
			break;
		case REFUND:
			hostUrl = PropertiesManager.propertiesMap.get(Constants.ICICI_MPGS_REFUND_URL);
			hostUrl = hostUrl.concat(fields.get(FieldType.MERCHANT_ID.getName()));
			hostUrl = hostUrl.concat("/order/");
			hostUrl = hostUrl.concat(fields.get(FieldType.ORIG_TXN_ID.getName()));
			hostUrl = hostUrl.concat("/transaction/");
			hostUrl = hostUrl.concat(fields.get(FieldType.PG_REF_NUM.getName()));
			break;
		case STATUS:
			hostUrl = PropertiesManager.propertiesMap.get(Constants.ICICI_MPGS_STATUS_ENQ_URL);
			break;
		}

		HttpsURLConnection connection = null;
		logRequest(request.toString(), hostUrl, fields);
		try {

			// Create connection
			String userName = "merchant.".concat(fields.get(FieldType.MERCHANT_ID.getName()));
			String pass = fields.get(FieldType.PASSWORD.getName());

			String authString = userName + ":" + pass;
			byte[] authEncBytes = Base64.encodeBase64(authString.getBytes());
			String authStringEnc = new String(authEncBytes);

			URL url = new URL(hostUrl);
			connection = (HttpsURLConnection) url.openConnection();
			connection.setRequestProperty("Authorization", "Basic " + authStringEnc);

			connection.setRequestMethod("PUT");
			connection.setRequestProperty("Content-Type", "application/json");
			connection.setRequestProperty("Content-Length", request.toString());
			connection.setRequestProperty("Content-Language", "en-US");

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

			while ((line = rd.readLine()) != null) {
				serverResponse.append(line);
				serverResponse.append('\r');
			}
			rd.close();

		} catch (Exception e) {
			logger.info("Exception in MPGS TransactionCommunicator " + e.getMessage());
		} finally {
			if (connection != null) {
				connection.disconnect();
			}
		}

		JSONObject response = new JSONObject(serverResponse.toString());
		logResponse(serverResponse.toString(), fields);
		//logger.info("Response received from Icici MPGS bank " + serverResponse.toString());
		return response;

	}
	
	public JSONObject getAcsResponse(JSONObject request, Fields fields) throws SystemException {
		StringBuilder serverResponse = new StringBuilder();
		String hostUrl = PropertiesManager.propertiesMap.get(Constants.ICICI_MPGS_ENROLLMENT_URL);
		hostUrl = hostUrl.concat(fields.get(FieldType.MERCHANT_ID.getName()));
		hostUrl = hostUrl.concat("/3DSecureId/");
		hostUrl = hostUrl.concat(fields.get(FieldType.ORIG_TXN_ID.getName()));
		HttpsURLConnection connection = null;
		logRequest(request.toString(), hostUrl, fields);
		try {

			// Create connection
			String userName = "merchant.".concat(fields.get(FieldType.MERCHANT_ID.getName()));
			String pass = fields.get(FieldType.PASSWORD.getName());

			String authString = userName + ":" + pass;
			byte[] authEncBytes = Base64.encodeBase64(authString.getBytes());
			String authStringEnc = new String(authEncBytes);

			URL url = new URL(hostUrl);
			connection = (HttpsURLConnection) url.openConnection();
			connection.setRequestProperty("Authorization", "Basic " + authStringEnc);

			connection.setRequestMethod("POST");
			connection.setRequestProperty("Content-Type", "application/json");
			connection.setRequestProperty("Content-Length", request.toString());
			connection.setRequestProperty("Content-Language", "en-US");

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

			while ((line = rd.readLine()) != null) {
				serverResponse.append(line);
				serverResponse.append('\r');
			}
			rd.close();

		} catch (Exception e) {
			logger.info("Exception in MPGS TransactionCommunicator " + e.getMessage());
		} finally {
			if (connection != null) {
				connection.disconnect();
			}
		}

		JSONObject response = new JSONObject(serverResponse.toString());
		//logger.info("Response received from Icici MPGS bank " + serverResponse.toString());
		logResponse(serverResponse.toString(), fields);
		return response;
	}
	
	public void logRequest(String request, String url, Fields fields) {
		log("Request message to MPGS: TxnType = " + fields.get(FieldType.TXNTYPE.getName()) + " " + "Txn id = " + fields.get(FieldType.TXN_ID.getName()) + " " + "Url= " + url + " " + request);
	}

	public void logResponse(String responseMessage, String url, Fields fields) {
		log("Response message from MPGS: TxnType = " + fields.get(FieldType.TXNTYPE.getName()) + " " + "Txn id = " + fields.get(FieldType.TXN_ID.getName()) + " " + "Url= " + url + " " + responseMessage);
	}

	public void logRequest(String requestMessage, Fields fields) {
		log("Request message to MPGS: TxnType = " + fields.get(FieldType.TXNTYPE.getName()) + " " + "Txn id = " + fields.get(FieldType.TXN_ID.getName()) + " " + requestMessage);
	}

	public void logResponse(String responseMessage, Fields fields) {
		log("Response message from MPGS: TxnType = " + fields.get(FieldType.TXNTYPE.getName()) + " " + "Txn id = " + fields.get(FieldType.TXN_ID.getName()) + " " + responseMessage);
	}
	
	private void log(String message) {

		message = Pattern.compile("(\"number\":\")([\\s\\S]*?)(\")").matcher(message).replaceAll("$1$3");
		message = Pattern.compile("(\"securityCode\":\")([\\s\\S]*?)(\")").matcher(message).replaceAll("$1$3");
		message = Pattern.compile("(\"month\":\")([\\s\\S]*?)(\")").matcher(message).replaceAll("$1$3");
		message = Pattern.compile("(\"year\":\")([\\s\\S]*?)(\")").matcher(message).replaceAll("$1$3");
		//message = Pattern.compile("(Password>)([\\s\\S]*?)(Password>)").matcher(message).replaceAll("$1$3");
		// MDC.put(FieldType.INTERNAL_CUSTOM_MDC.getName(),
		// fields.getCustomMDC());
		logger.info(message);
	}
}
