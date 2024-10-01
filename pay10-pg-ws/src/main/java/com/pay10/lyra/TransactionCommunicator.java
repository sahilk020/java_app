package com.pay10.lyra;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.regex.Pattern;

import javax.net.ssl.HttpsURLConnection;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang3.StringUtils;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.pay10.commons.exception.ErrorType;
import com.pay10.commons.exception.SystemException;
import com.pay10.commons.util.FieldType;
import com.pay10.commons.util.Fields;
import com.pay10.commons.util.PaymentType;
import com.pay10.commons.util.PropertiesManager;
import com.pay10.commons.util.StatusType;
import com.pay10.commons.util.TransactionType;

@Service("lyraTransactionCommunicator")
public class TransactionCommunicator {

	private static Logger logger = LoggerFactory.getLogger(TransactionCommunicator.class.getName());

	@SuppressWarnings("incomplete-switch")
	public JSONObject getResponse(String request, Fields fields) throws SystemException {

		String hostUrl = "";

		try {

			TransactionType transactionType = TransactionType.getInstance(fields.get(FieldType.TXNTYPE.getName()));
			switch (transactionType) {
			case SALE:
				hostUrl = PropertiesManager.propertiesMap.get(Constants.SALE_REQUEST_URL);
				break;
			case AUTHORISE:
				break;
			case ENROLL:
				hostUrl = PropertiesManager.propertiesMap.get(Constants.SALE_REQUEST_URL);
				break;
			case CAPTURE:
				break;
			case REFUND:
				hostUrl = PropertiesManager.propertiesMap.get(Constants.REFUND_URL);
				break;
			case STATUS:
				hostUrl = PropertiesManager.propertiesMap.get(Constants.STATUS_ENQ_URL);
				break;
			}

			URL url = new URL(hostUrl);
			logRequest(request.toString(), fields);
			// Create connection
			String userName = fields.get(FieldType.MERCHANT_ID.getName());
			String pass = fields.get(FieldType.PASSWORD.getName());

			String authString = userName + ":" + pass;
			byte[] authEncBytes = Base64.encodeBase64(authString.getBytes());
			String authStringEnc = new String(authEncBytes);
			URLConnection connection = (HttpsURLConnection) url.openConnection();
			connection.setRequestProperty("Authorization", "Basic " + authStringEnc);
			connection.setRequestProperty("Content-Type", "application/json");
			connection.setDoInput(true);
			connection.setDoOutput(true);
			connection.setUseCaches(false);

			DataOutputStream dataoutputstream = new DataOutputStream(connection.getOutputStream());
			dataoutputstream.writeBytes(request);
			dataoutputstream.flush();
			dataoutputstream.close();
			BufferedReader bufferedreader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
			String decodedString;
			String serverResponse = "";
			while ((decodedString = bufferedreader.readLine()) != null) {
				serverResponse = serverResponse + decodedString;
			}
			logResponse(serverResponse, fields);
			JSONObject response = new JSONObject(serverResponse.toString());
			return response;
		} catch (IOException ioException) {
			fields.put(FieldType.STATUS.getName(), StatusType.ERROR.getName());
			logger.error("Network Exception with lyra", ioException);
			throw new SystemException(ErrorType.INTERNAL_SYSTEM_ERROR, ioException,
					"Network Exception with lyra " + hostUrl.toString());
		}
	}

	public JSONObject getNBResponse(String request, Fields fields, Transaction transactionResponse)
			throws SystemException {
		String hostUrl = "";
		String txnType = fields.get(FieldType.TXNTYPE.getName());
		if (txnType.equals(TransactionType.SALE.getName())) {
			if (StringUtils.isNotBlank(transactionResponse.getChargeFlag())) {
				hostUrl = PropertiesManager.propertiesMap.get("LyraNBUuidURL");
				if (fields.get(FieldType.PAYMENT_TYPE.getName()).equalsIgnoreCase(PaymentType.NET_BANKING.getCode())) {
					hostUrl = hostUrl + transactionResponse.getUuid() + "/submit/nb";
				} else if (fields.get(FieldType.PAYMENT_TYPE.getName())
						.equalsIgnoreCase(PaymentType.WALLET.getCode())) {
					hostUrl = hostUrl + transactionResponse.getUuid() + "/submit/wallet";
				} else if (fields.get(FieldType.PAYMENT_TYPE.getName()).equalsIgnoreCase(PaymentType.UPI.getCode())) {
					hostUrl = hostUrl + transactionResponse.getUuid() + "/submit/upi";
				} else {
					hostUrl = hostUrl + transactionResponse.getUuid() + "/submit/card";
				}
			} else {
				hostUrl = PropertiesManager.propertiesMap.get("LyraNBChargeUrl");

			}
		} else {
			hostUrl = PropertiesManager.propertiesMap.get("LyraNBChargeUrl");
			hostUrl = hostUrl + "/" + fields.get(FieldType.ACQ_ID.getName()) + "/refund";
		}
		try {

			URL url = new URL(hostUrl);
			logDirectRequest(request.toString(), fields);
			// Create connection
			String userName = fields.get(FieldType.MERCHANT_ID.getName());
			String pass = fields.get(FieldType.PASSWORD.getName());

			String authString = userName + ":" + pass;
			byte[] authEncBytes = Base64.encodeBase64(authString.getBytes());
			String authStringEnc = new String(authEncBytes);
			URLConnection connection = null;
			connection = (HttpsURLConnection) url.openConnection();
			connection.setRequestProperty("Authorization", "Basic " + authStringEnc);
			connection.setRequestProperty("Content-Type", "application/json");
			connection.setDoInput(true);
			connection.setDoOutput(true);
			connection.setUseCaches(false);

			DataOutputStream dataoutputstream = new DataOutputStream(connection.getOutputStream());
			dataoutputstream.writeBytes(request);
			dataoutputstream.flush();
			dataoutputstream.close();
			BufferedReader bufferedreader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
			String decodedString;
			String serverResponse = "";
			while ((decodedString = bufferedreader.readLine()) != null) {
				serverResponse = serverResponse + decodedString;
			}
			logResponse(serverResponse, fields);
			JSONObject response = new JSONObject(serverResponse.toString());
			return response;
		} catch (IOException ioException) {
			fields.put(FieldType.STATUS.getName(), StatusType.ERROR.getName());
			logger.error("Network Exception with lyra", ioException);
			throw new SystemException(ErrorType.INTERNAL_SYSTEM_ERROR, ioException,
					"Network Exception with lyra " + hostUrl.toString());
		}
	}

	public void updateSaleResponse(Fields fields, Transaction response) {

		fields.put(FieldType.LYRA_FINAL_REQUEST.getName(), response.getPareq());
		fields.put(FieldType.ACS_URL.getName(), response.getAcsUrl());
		fields.put(FieldType.STATUS.getName(), StatusType.SENT_TO_BANK.getName());
		fields.put(FieldType.RESPONSE_CODE.getName(), ErrorType.SUCCESS.getResponseCode());
		fields.put(FieldType.RESPONSE_MESSAGE.getName(), ErrorType.SUCCESS.getResponseMessage());

	}

	public void updateUpiSaleResponse(Fields fields, Transaction response) {
		// fields.put(FieldType.ACQ_ID.getName(), response.getUuid());
		fields.put(FieldType.STATUS.getName(), StatusType.SENT_TO_BANK.getName());
		fields.put(FieldType.RESPONSE_CODE.getName(), ErrorType.SUCCESS.getResponseCode());
		fields.put(FieldType.RESPONSE_MESSAGE.getName(), ErrorType.SUCCESS.getResponseMessage());

	}

	public void logRequest(String requestMessage, Fields fields) {
		log("Request to Lyra: TxnType = " + fields.get(FieldType.TXNTYPE.getName()) + " " + "Txn id = "
				+ fields.get(FieldType.TXN_ID.getName()) + " " + "Url= " + requestMessage, fields);
	}

	public void logResponse(String response, Fields fields) {
		log("Response from Lyra: TxnType = " + fields.get(FieldType.TXNTYPE.getName()) + " " + "Txn id = "
				+ fields.get(FieldType.TXN_ID.getName()) + " " + "Url= " + response, fields);
	}

	private void log(String message, Fields fields) {

		message = Pattern.compile("(pan\":\")([\\s\\S]*?)(\",\")").matcher(message).replaceAll("$1$3");
		message = Pattern.compile("(expiryYear\":\")([\\s\\S]*?)(\",\")").matcher(message).replaceAll("$1$3");
		message = Pattern.compile("(expiryMonth\":\")([\\s\\S]*?)(\",\")").matcher(message).replaceAll("$1$3");
		message = Pattern.compile("(securityCode\":\")([\\s\\S]*?)(\",\")").matcher(message).replaceAll("$1$3");

		logger.info(message);

	}

	public void logDirectRequest(String requestMessage, Fields fields) {
		logDirect("Request to Lyra: TxnType = " + fields.get(FieldType.TXNTYPE.getName()) + " " + "Txn id = "
				+ fields.get(FieldType.TXN_ID.getName()) + " " + "Url= " + requestMessage, fields);
	}

	private void logDirect(String message, Fields fields) {

		message = Pattern.compile("(cardNumber\":\")([\\s\\S]*?)(\",\")").matcher(message).replaceAll("$1$3");
		message = Pattern.compile("(expMonth\":\")([\\s\\S]*?)(\",\")").matcher(message).replaceAll("$1$3");
		message = Pattern.compile("(expYear\":\")([\\s\\S]*?)(\",\")").matcher(message).replaceAll("$1$3");
		message = Pattern.compile("(cvv\":\")([\\s\\S]*?)(\",\")").matcher(message).replaceAll("$1$3");

		logger.info(message);

	}

}
