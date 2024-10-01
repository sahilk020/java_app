package com.pay10.sbi.upi;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
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

@Service("sbiUpiTransactionCommunicator")
public class TransactionCommunicator {

	private static Logger logger = LoggerFactory.getLogger(TransactionCommunicator.class.getName());

	@SuppressWarnings("incomplete-switch")
	public String getRefreshTokenResponse(Transaction request, Fields fields, String hostUrl) throws SystemException {

		StringBuilder serverResponse = new StringBuilder();
		HttpsURLConnection connection = null;

		try {
//https://uatupionline.sbi/upi2/oauth/token?grant_type=password&client_secret=a8a5bef651e149d08dbdf9da8133dd1f&client_id=102230125217&username=oauth2-api-merweb-SBI0000000031539&password=CC3F901BACEF6B8A3CD0DBC3700637FD0A998019ED67D2C77B4F5554DCB48B13
			SSLContext sc = getSSLContext();
			StringBuffer strUrl = new StringBuffer();
			strUrl.append(hostUrl);
			strUrl.append("?" + Constants.GRANT_TYPE + "=" + Constants.GRANT_TYPE_VAL);
			strUrl.append("&" + Constants.CLIENT_SECRET + "=" + request.getClientSecret());
			strUrl.append("&" + Constants.CLIENT_ID + "=" + request.getClientId());
			strUrl.append("&" + Constants.USERNAME + "=" + fields.get(FieldType.ADF1.getName()));
			strUrl.append("&" + Constants.PASSWORD + "=" + fields.get(FieldType.ADF2.getName()));
			logger.info("refresh token request :" + strUrl);
			URL url = new URL(strUrl.toString());

			connection = (HttpsURLConnection) url.openConnection();
			connection.setSSLSocketFactory(sc.getSocketFactory());
			connection.setRequestMethod("POST");
			connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
			connection.setRequestProperty("Content-Language", "en-US");
			connection.setUseCaches(false);
			connection.setDoOutput(true);
			connection.setDoInput(true);
			// Send request
			OutputStream outputStream = connection.getOutputStream();
			DataOutputStream wr = new DataOutputStream(outputStream);
			wr.writeBytes("");
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
						"Network Exception with SBI UPI " + hostUrl.toString());
			}

			while ((line = rd.readLine()) != null) {
				serverResponse.append(line);
			}
			rd.close();

		} catch (Exception e) {
			logger.info("SBI UPI : METHOD : getRefreshTokenResponse : PG_REF_NUM : "
					+ fields.get(FieldType.PG_REF_NUM.getName()) + "   EXCEPTION REFRESH TOKEN : " + e.getMessage());
			int code = 0;
			try {
				code = ((HttpURLConnection) connection).getResponseCode();
			} catch (IOException e1) {
				logger.error("Response  of txn IN CATCH BLOCK :" + e1);
			}
			int firstDigitOfCode = Integer.parseInt(Integer.toString(code).substring(0, 1));
			if (firstDigitOfCode == 4 || firstDigitOfCode == 5) {
				fields.put(FieldType.STATUS.getName(), StatusType.ACQUIRER_DOWN.getName());
				logger.error("Response code of txn :" + code);
				throw new SystemException(ErrorType.ACUIRER_DOWN,
						"Network Exception with SBI UPI" + hostUrl.toString());
			}
		} finally {
			if (connection != null) {
				connection.disconnect();
			}

			System.clearProperty("https.protocols");
			System.clearProperty("javax.net.ssl.trustStore");
		}

		return serverResponse.toString();
	}

	@SuppressWarnings("incomplete-switch")
	public String getHandShakeResponse(JSONObject request, Fields fields, String hostUrl) throws SystemException {

		StringBuilder serverResponse = new StringBuilder();
		HttpsURLConnection connection = null;
		try {
			SSLContext sc = getSSLContext();

			URL url = new URL(hostUrl);
			connection = (HttpsURLConnection) url.openConnection();
			connection.setSSLSocketFactory(sc.getSocketFactory());

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
			if (firstDigitOfCode == 4 || firstDigitOfCode == 5) {
				fields.put(FieldType.STATUS.getName(), StatusType.ACQUIRER_DOWN.getName());
				logger.error("Response code of txn :" + code);
				throw new SystemException(ErrorType.ACUIRER_DOWN,
						"Network Exception with SBI UPI " + hostUrl.toString());
			}

			while ((line = rd.readLine()) != null) {
				serverResponse.append(line);

			}

			rd.close();

		} catch (Exception e) {
			logger.info("SBI UPI : METHOD : getHandShakeResponse : PG_REF_NUM : "
					+ fields.get(FieldType.PG_REF_NUM.getName()) + "   EXCEPTION HANDSHAKE : " + e.getMessage());
			int code = 0;
			try {
				code = ((HttpURLConnection) connection).getResponseCode();
			} catch (IOException e1) {
				logger.error("Response  of txn IN CATCH BLOCK :" + e1);
			}
			int firstDigitOfCode = Integer.parseInt(Integer.toString(code).substring(0, 1));
			if (firstDigitOfCode == 4 || firstDigitOfCode == 5) {
				fields.put(FieldType.STATUS.getName(), StatusType.ACQUIRER_DOWN.getName());
				logger.error("Response code of txn :" + code);
				throw new SystemException(ErrorType.ACUIRER_DOWN,
						"Network Exception with SBI UPI" + hostUrl.toString());
			}
		} finally {
			if (connection != null) {
				connection.disconnect();
			}

			System.clearProperty("https.protocols");
			System.clearProperty("javax.net.ssl.trustStore");
		}

		return serverResponse.toString();
	}

	@SuppressWarnings("incomplete-switch")
	public String getVpaResponse(JSONObject request, Fields fields, String hostUrl, String access_token)
			throws SystemException {

		StringBuilder serverResponse = new StringBuilder();

		HttpsURLConnection connection = null;
		try {

			SSLContext sc = getSSLContext();

			URL url = new URL(hostUrl);
			connection = (HttpsURLConnection) url.openConnection();
			connection.setSSLSocketFactory(sc.getSocketFactory());

			connection.setRequestMethod("POST");
			connection.setRequestProperty("Content-Type", "application/json");
			connection.setRequestProperty("Content-Length", request.toString());
			connection.setRequestProperty("Content-Language", "en-US");
			connection.setRequestProperty("Authorization", "Bearer " + access_token);

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
			code = 503;
			int firstDigitOfCode = Integer.parseInt(Integer.toString(code).substring(0, 1));
			if (firstDigitOfCode == 4 || firstDigitOfCode == 5) {
				fields.put(FieldType.STATUS.getName(), StatusType.ACQUIRER_DOWN.getName());
				logger.error("Response code of txn :" + code);
				throw new SystemException(ErrorType.ACUIRER_DOWN,
						"Network Exception with SBI UPI " + hostUrl.toString());
			}

			while ((line = rd.readLine()) != null) {
				serverResponse.append(line);

			}

			rd.close();

		} catch (Exception e) {
			logger.info("SBI UPI : METHOD : getVpaResponse : PG_REF_NUM : " + fields.get(FieldType.PG_REF_NUM.getName())
					+ "   EXCEPTION VPA VALIDATE : " + e.getMessage());
			int code = 0;
			try {
				code = ((HttpURLConnection) connection).getResponseCode();
			} catch (IOException e1) {
				logger.error("Response  of txn IN CATCH BLOCK :" + e1);
			}
			int firstDigitOfCode = Integer.parseInt(Integer.toString(code).substring(0, 1));
			if (firstDigitOfCode == 4 || firstDigitOfCode == 5) {
				fields.put(FieldType.STATUS.getName(), StatusType.ACQUIRER_DOWN.getName());
				logger.error("Response code of txn :" + code);
				throw new SystemException(ErrorType.ACUIRER_DOWN,
						"Network Exception with SBI UPI" + hostUrl.toString());
			}
		} finally {
			if (connection != null) {
				connection.disconnect();
			}

			System.clearProperty("https.protocols");
			System.clearProperty("javax.net.ssl.trustStore");
		}

		return serverResponse.toString();
	}
	
	@SuppressWarnings("incomplete-switch")
	public String getResponse(JSONObject request, Fields fields, String access_token) throws SystemException {

		StringBuilder serverResponse = new StringBuilder();

		String hostUrl = "";

		TransactionType transactionType = TransactionType.getInstance(fields.get(FieldType.TXNTYPE.getName()));
		logger.info(" trxn transactionType-----" + transactionType);
		switch (transactionType) {
		case SALE:
			hostUrl = PropertiesManager.propertiesMap.get(Constants.SBI_UPI_COLLECT_PAY_URL);
			break;
		case REFUND:
			// hostUrl = PropertiesManager.propertiesMap.get(Constants.SBI_UPI_REFUND_URL);
			break;
		case STATUS:
			hostUrl = PropertiesManager.propertiesMap.get(Constants.SBI_UPI_STATUS_ENQUIRY_URL);
			break;
		case ENQUIRY:
			hostUrl = PropertiesManager.propertiesMap.get(Constants.SBI_UPI_STATUS_ENQUIRY_URL);
			break;
		}
		logger.info(transactionType + " trxn typeurl-----" + hostUrl);
		HttpsURLConnection connection = null;

		try {

			SSLContext sc = getSSLContext();

			URL url = new URL(hostUrl);
			connection = (HttpsURLConnection) url.openConnection();
			connection.setSSLSocketFactory(sc.getSocketFactory());

			connection.setRequestMethod("POST");
			connection.setRequestProperty("Content-Type", "application/json");
			connection.setRequestProperty("Content-Length", request.toString());
			connection.setRequestProperty("Content-Language", "en-US");
			connection.setRequestProperty("Authorization", "Bearer" + access_token);
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
			// TO HANDLE EXCEPTION IF BANK SERVER IS DOWN
			int code = ((HttpURLConnection) connection).getResponseCode();
			int firstDigitOfCode = Integer.parseInt(Integer.toString(code).substring(0, 1));
			if (firstDigitOfCode == 4 || firstDigitOfCode == 5) {
				fields.put(FieldType.STATUS.getName(), StatusType.ACQUIRER_DOWN.getName());
				logger.error("Response code of txn :" + code);
				throw new SystemException(ErrorType.ACUIRER_DOWN,
						"Network Exception with SBI UPI" + hostUrl.toString());
			}

			while ((line = rd.readLine()) != null) {
				serverResponse.append(line);

			}

			rd.close();

		} catch (Exception e) {
			logger.info("SBI UPI : METHOD : getResponse : PG_REF_NUM : " + fields.get(FieldType.PG_REF_NUM.getName())
					+ "   EXCEPTION GET RESPONSE : " + e.getMessage());
			int code = 0;
			try {
				code = ((HttpURLConnection) connection).getResponseCode();
			} catch (IOException e1) {
				logger.error("Response  of txn IN CATCH BLOCK :" + e1);
			}
			int firstDigitOfCode = Integer.parseInt(Integer.toString(code).substring(0, 1));
			if (firstDigitOfCode == 4 || firstDigitOfCode == 5) {
				fields.put(FieldType.STATUS.getName(), StatusType.ACQUIRER_DOWN.getName());
				logger.error("Response code of txn :" + code);
				throw new SystemException(ErrorType.ACUIRER_DOWN,
						"Network Exception with SBI UPI" + hostUrl.toString());
			}
		} finally {
			if (connection != null) {
				connection.disconnect();
			}

			System.clearProperty("https.protocols");
			System.clearProperty("javax.net.ssl.trustStore");
		}

		return serverResponse.toString();
	}

	private SSLContext getSSLContext() throws SystemException {

		try {

			TrustManager[] trustAllCerts = new TrustManager[] { new X509TrustManager() {
				public java.security.cert.X509Certificate[] getAcceptedIssuers() {
					return null;
				}

				public void checkClientTrusted(X509Certificate[] certs, String authType) {
				}

				public void checkServerTrusted(X509Certificate[] certs, String authType) {
				}
			} };
			SecureRandom secureRandom = new SecureRandom();

			SSLContext sc = SSLContext.getInstance("TLSv1.2");
			sc.init(null, trustAllCerts, secureRandom);

			return sc;
		}

		catch (NoSuchAlgorithmException | KeyManagementException e) {
			logger.error("Exception in getSSLContext , exsception = " + e);
			throw new SystemException(ErrorType.INTERNAL_SYSTEM_ERROR, e,
					"unknown exception in getSSLContext  for Sbi upi in TransactionCommunicator");
		}

	}

}