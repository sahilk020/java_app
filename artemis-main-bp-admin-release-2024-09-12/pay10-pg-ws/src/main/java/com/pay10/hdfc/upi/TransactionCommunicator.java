package com.pay10.hdfc.upi;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.json.JSONObject;
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
@Service("hdfcUpiTransactionCommunicator")
public class TransactionCommunicator {

	private static Logger logger = LoggerFactory.getLogger(TransactionCommunicator.class.getName());
	
	public void updateInvalidVpaResponse(Fields fields, String response) {

		fields.put(FieldType.STATUS.getName(), StatusType.REJECTED.getName());
		fields.put(FieldType.RESPONSE_CODE.getName(), ErrorType.INVALID_VPA.getResponseCode());
		fields.put(FieldType.PG_TXN_MESSAGE.getName(), ErrorType.INVALID_VPA.getResponseMessage());

	}

	public void updateSaleResponse(Fields fields, Transaction transactionResponse) {

		String statusType = transactionResponse.getStatus();
		if (statusType.equals(Constants.SUCCESS_REPONSE)) {
			fields.put(FieldType.STATUS.getName(), StatusType.SENT_TO_BANK.getName());
			fields.put(FieldType.RESPONSE_CODE.getName(), ErrorType.SUCCESS.getResponseCode());
			fields.put(FieldType.RESPONSE_MESSAGE.getName(), ErrorType.SUCCESS.getResponseMessage());
			
		
		} else {
			fields.put(FieldType.STATUS.getName(), StatusType.REJECTED.getName());
			fields.put(FieldType.RESPONSE_CODE.getName(), ErrorType.REJECTED.getResponseCode());
			fields.put(FieldType.RESPONSE_MESSAGE.getName(), ErrorType.REJECTED.getResponseMessage());
		}
		String pgRefNum = fields.get(FieldType.PG_REF_NUM.getName());
		if(pgRefNum == null){
			fields.put(FieldType.PG_REF_NUM.getName(), fields.get(FieldType.TXN_ID.getName()));
			}
	}

	@SuppressWarnings("incomplete-switch")
	public String getVPAResponse(JSONObject request, Fields fields) throws SystemException {
		StringBuilder serverResponse = new StringBuilder();
		String hostUrl = PropertiesManager.propertiesMap.get(Constants.HDFC_UPI_VPA_VAL_URL);

		// logger.info("Request sent to bank " + request);
		HttpsURLConnection connection = null;
		HttpURLConnection simulatorConn = null;

		if (hostUrl.contains("https")) {
			try {

				// Create connection

				URL url = new URL(hostUrl);
				disableSSLCertificateChecking();
				connection = (HttpsURLConnection) url.openConnection();

				connection.setRequestMethod("POST");
				connection.setRequestProperty("Content-Type", "application/json");
				connection.setRequestProperty("Content-Length", ""+request.toString().length());
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
				
				int code = ((HttpURLConnection) connection).getResponseCode();
				int firstDigitOfCode = Integer.parseInt(Integer.toString(code).substring(0, 1));
				if(firstDigitOfCode == 4 || firstDigitOfCode == 5){
					 fields.put(FieldType.STATUS.getName(),StatusType.ACQUIRER_DOWN.getName());
					 logger.error("Response code of txn :" + code);
					 throw new SystemException(ErrorType.ACUIRER_DOWN,
								 "Network Exception with hdfc Upi "
										+ hostUrl.toString());
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
		} else {

			try {

				// Create connection

				URL url = new URL(hostUrl);
				simulatorConn = (HttpURLConnection) url.openConnection();

				simulatorConn.setRequestMethod("POST");
				simulatorConn.setRequestProperty("Content-Type", "application/json");
				simulatorConn.setRequestProperty("Content-Length", request.toString());
				simulatorConn.setRequestProperty("Content-Language", "en-US");

				simulatorConn.setUseCaches(false);
				simulatorConn.setDoOutput(true);
				simulatorConn.setDoInput(true);

				// Send request
				OutputStream outputStream = simulatorConn.getOutputStream();
				DataOutputStream wr = new DataOutputStream(outputStream);
				wr.writeBytes(request.toString());
				wr.close();

				// Get Response
				InputStream is = simulatorConn.getInputStream();
				BufferedReader rd = new BufferedReader(new InputStreamReader(is));
				String line;
				
				
				while ((line = rd.readLine()) != null) {
					serverResponse.append(line);

				}
				rd.close();

			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				if (simulatorConn != null) {
					simulatorConn.disconnect();
				}
			}

		}

		// JSONObject response = new JSONObject(serverResponse.toString());
		return serverResponse.toString();
	}
	private static void disableSSLCertificateChecking() {
		TrustManager[] trustAllCerts = new TrustManager[] { new X509TrustManager() {
			public X509Certificate[] getAcceptedIssuers() {
				return null;
			}

			@Override
			public void checkClientTrusted(X509Certificate[] arg0, String arg1) throws CertificateException {
				// Not implemented
			}

			@Override
			public void checkServerTrusted(X509Certificate[] arg0, String arg1) throws CertificateException {
				// Not implemented
			}
		} };

		try {
			SSLContext sc = SSLContext.getInstance("TLS");

			sc.init(null, trustAllCerts, new java.security.SecureRandom());

			HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
		} catch (KeyManagementException e) {
			e.printStackTrace();
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
	}
	@SuppressWarnings("incomplete-switch")
	public String getResponse(JSONObject request, Fields fields) throws SystemException {
		StringBuilder serverResponse = new StringBuilder();
		String hostUrl = "";

		TransactionType transactionType = TransactionType.getInstance(fields.get(FieldType.TXNTYPE.getName()));
		switch (transactionType) {
		case SALE:
			hostUrl = PropertiesManager.propertiesMap.get(Constants.HDFC_UPI_SALE_URL);
			break;
		case REFUND:
			hostUrl = PropertiesManager.propertiesMap.get(Constants.HDFC_UPI_REFUND_URL);
			break;
		case ENQUIRY:
			hostUrl = PropertiesManager.propertiesMap.get(Constants.HDFC_UPI_STATUS_ENQ_URL);
		case STATUS:
			hostUrl = PropertiesManager.propertiesMap.get(Constants.HDFC_UPI_STATUS_ENQ_URL);

		}

		// logger.info("Request sent to bank " + request);
		HttpsURLConnection connection = null;
		HttpURLConnection simulatorConn = null;
		if (hostUrl.contains("https")) {
			try {

				// Create connection

				URL url = new URL(hostUrl);
				connection = (HttpsURLConnection) url.openConnection();

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
				int code = ((HttpURLConnection) connection).getResponseCode();
				int firstDigitOfCode = Integer.parseInt(Integer.toString(code).substring(0, 1));
				if(firstDigitOfCode == 4 || firstDigitOfCode == 5){
					 fields.put(FieldType.STATUS.getName(),StatusType.ACQUIRER_DOWN.getName());
					 logger.error("Response code of txn :" + code);
					 throw new SystemException(ErrorType.ACUIRER_DOWN,
								 "Network Exception with Hdfc Upi "
										+ hostUrl.toString());
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
		} else {
			try {
				URL url = new URL(hostUrl);
				simulatorConn = (HttpURLConnection) url.openConnection();

				simulatorConn.setRequestMethod("POST");
				simulatorConn.setRequestProperty("Content-Type", "application/json");
				simulatorConn.setRequestProperty("Content-Length", request.toString());
				simulatorConn.setRequestProperty("Content-Language", "en-US");

				simulatorConn.setUseCaches(false);
				simulatorConn.setDoOutput(true);
				simulatorConn.setDoInput(true);

				// Send request
				OutputStream outputStream = simulatorConn.getOutputStream();
				DataOutputStream wr = new DataOutputStream(outputStream);
				wr.writeBytes(request.toString());
				wr.close();

				// Get Response
				InputStream is = simulatorConn.getInputStream();
				BufferedReader rd = new BufferedReader(new InputStreamReader(is));
				String line;

				while ((line = rd.readLine()) != null) {
					serverResponse.append(line);

				}
				rd.close();

			} catch (Exception exception) {
				logger.error("Exception", exception);
			} finally {
				if (simulatorConn != null) {
					simulatorConn.disconnect();
				}

			}
		}

		// JSONObject response = new JSONObject(serverResponse.toString());
		return serverResponse.toString();
	}

}
