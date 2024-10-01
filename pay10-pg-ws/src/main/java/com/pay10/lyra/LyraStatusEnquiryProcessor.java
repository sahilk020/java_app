package com.pay10.lyra;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;

import javax.net.ssl.HttpsURLConnection;

import org.apache.commons.codec.binary.Base64;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.pay10.commons.exception.ErrorType;
import com.pay10.commons.exception.SystemException;
import com.pay10.commons.util.FieldType;
import com.pay10.commons.util.Fields;
import com.pay10.commons.util.PaymentType;
import com.pay10.commons.util.PropertiesManager;
import com.pay10.commons.util.StatusType;

@Service
public class LyraStatusEnquiryProcessor {

	@Autowired
	private TransactionConverter converter;

	@Autowired
	private LyraNBSaleResponseHandler lyraNBSaleResponseHandler;

	private LyraTransformer lyraTransformer = null;

	private static Logger logger = LoggerFactory.getLogger(LyraStatusEnquiryProcessor.class.getName());

	public void enquiryProcessor(Fields fields) {
		if (fields.get(FieldType.PAYMENT_TYPE.getName()).equals(PaymentType.NET_BANKING.getCode())
				|| fields.get(FieldType.PAYMENT_TYPE.getName()).equals(PaymentType.WALLET.getCode())) {
			String response = getNBStatus(fields);
			updateNBFields(fields, response);
		} else {
			String request = statusEnquiryRequest(fields);
			JSONObject response = new JSONObject();
			try {
				response = getResponse(request, fields);
			} catch (SystemException exception) {
				logger.error("Exception", exception);
			}

			updateFields(fields, response);
		}

	}

	public String statusEnquiryRequest(Fields fields) {
		JSONObject jsonRequest = new JSONObject();

		jsonRequest.put("orderId", fields.get(FieldType.PG_REF_NUM.getName()));
		return jsonRequest.toString();

	}

	public static JSONObject getResponse(String request, Fields fields) throws SystemException {

		String hostUrl = "";

		try {

			hostUrl = PropertiesManager.propertiesMap.get(Constants.STATUS_ENQ_URL);
			;

			logger.info("LYRA status enquiry request, " + request);
			URL url = new URL(hostUrl);
			logger.info("Request message to Lyra: TxnType = " + fields.get(FieldType.TXNTYPE.getName()) + " "
					+ "Txn id = " + fields.get(FieldType.TXN_ID.getName()) + " " + request);
			// logRequest(request, hostUrl,fields);
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
			logger.info("Response mesage from lyra: TxnType = " + fields.get(FieldType.TXNTYPE.getName()) + " "
					+ "Txn id = " + fields.get(FieldType.TXN_ID.getName()) + " " + serverResponse);
			// logResponse(response, hostUrl,fields);
			JSONObject response = new JSONObject(serverResponse.toString());
			return response;
		} catch (IOException ioException) {
			fields.put(FieldType.STATUS.getName(), StatusType.ERROR.getName());
			logger.error("Network Exception with lyra", ioException);
			throw new SystemException(ErrorType.INTERNAL_SYSTEM_ERROR, ioException,
					"Network Exception with lyra " + hostUrl.toString());
		}
	}

	public void updateFields(Fields fields, JSONObject response) {

		Transaction transactionResponse = new Transaction();
		transactionResponse = converter.statusToTransaction(response);
		lyraTransformer = new LyraTransformer(transactionResponse);
		lyraTransformer.updateResponse(fields);

	}// toTransaction()

	public String getNBStatus(Fields fields) {
		try {
			HttpsURLConnection connection = null;
			StringBuilder serverResponse = new StringBuilder();
			String hostUrl = PropertiesManager.propertiesMap.get("LyraNBChargeUrl");
			hostUrl = hostUrl + "/" + fields.get(FieldType.ACQ_ID.getName());
			URL url = new URL(hostUrl);
			String userName = fields.get(FieldType.MERCHANT_ID.getName());
			String pass = fields.get(FieldType.PASSWORD.getName());

			String authString = userName + ":" + pass;
			byte[] authEncBytes = Base64.encodeBase64(authString.getBytes());
			String authStringEnc = new String(authEncBytes);

			connection = (HttpsURLConnection) url.openConnection();
			connection.setRequestProperty("Authorization", "Basic " + authStringEnc);
			connection.setRequestMethod("GET");
			connection.setRequestProperty("Content-Type", "application/json");
			connection.setRequestProperty("Content-Language", "en-US");
			connection.setUseCaches(false);
			connection.setDoOutput(true);
			connection.setDoInput(true);
			InputStream is = connection.getInputStream();
			BufferedReader rd = new BufferedReader(new InputStreamReader(is));
			String line;
			while ((line = rd.readLine()) != null) {
				serverResponse.append(line);
				serverResponse.append('\r');
			}
			rd.close();
			String str = serverResponse.toString();
			return str;

		} catch (IOException e) {
			logger.error("Error communicating with Lyra, " + e);
		}
		return null;

	}

	public void updateNBFields(Fields fields, String response) {

		Transaction transactionResponse = new Transaction();
		JSONObject res = new JSONObject(response);
		transactionResponse = lyraNBSaleResponseHandler.toTransaction(res);

		lyraTransformer = new LyraTransformer(transactionResponse);
		lyraTransformer.updateNBResponse(fields);

	}// toTransaction()

}
