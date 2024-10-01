package com.pay10.yesbankcb;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.KeyManager;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;

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
 * @author VJ
 *
 */
@Service("yesBankCbTransactionCommunicator")
public class TransactionCommunicator {

	private static Logger logger = LoggerFactory.getLogger(TransactionCommunicator.class.getName());

	public String getVpaResponse(JSONObject request, Fields fields) throws SystemException {

		StringBuilder serverResponse = new StringBuilder();

		String hostUrl = PropertiesManager.propertiesMap.get(Constants.YES_BANK_UPI_VPA_URL);
		HttpsURLConnection connection = null;
		HttpURLConnection simulatorConn = null;
		try {
			if (hostUrl.contains("https")) {
				try {

					SSLContext sc = getSSLContext(PropertiesManager.propertiesMap.get(Constants.JKS_FILE_NAME));

					URL url = new URL(hostUrl);
					connection = (HttpsURLConnection) url.openConnection();
					connection.setSSLSocketFactory(sc.getSocketFactory());

					connection.setRequestMethod("POST");
					connection.setRequestProperty("X-IBM-Client-Secret",
							PropertiesManager.propertiesMap.get("YESBANK_UPI_HEADER_CLIENT_SECRET"));
					connection.setRequestProperty("X-IBM-Client-ID",
							PropertiesManager.propertiesMap.get("YESBANK_UPI_HEADER_CLIENT_ID"));
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
					code = 503;
					int firstDigitOfCode = Integer.parseInt(Integer.toString(code).substring(0, 1));
					if (firstDigitOfCode == 4 || firstDigitOfCode == 5) {
						fields.put(FieldType.STATUS.getName(), StatusType.ACQUIRER_DOWN.getName());
						logger.error("Response code of txn :" + code);
						throw new SystemException(ErrorType.ACUIRER_DOWN,
								"Network Exception with Yes bank upi " + hostUrl.toString());
					}

					while ((line = rd.readLine()) != null) {
						serverResponse.append(line);

					}

					rd.close();

				} catch (Exception e) {
					logger.error("Exception in yes bank upi communicator : " + e.getMessage());
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
								"Network Exception with Yes Bank Upi" + hostUrl.toString());
					}
				} finally {
					if (connection != null) {
						connection.disconnect();
					}

					System.clearProperty("https.protocols");
					System.clearProperty("javax.net.ssl.trustStore");
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
					logger.error("Exception", e);
				} finally {
					if (simulatorConn != null) {
						simulatorConn.disconnect();
					}
				}

			}
		} catch (Exception e) {
			logger.error("Exception", e);
			throw new SystemException(ErrorType.ACUIRER_DOWN,
					"Network Exception with Yes Bank Upi" + hostUrl.toString());
		}

		return serverResponse.toString();
	}

	public String getLogIntentResponse(JSONObject request, Fields fields) throws SystemException {

		StringBuilder serverResponse = new StringBuilder();
		String hostUrl = PropertiesManager.propertiesMap.get(Constants.YES_BANK_LOG_INTENT_URL);
		HttpsURLConnection connection = null;
		HttpURLConnection simulatorConn = null;
		String pgRefNo = fields.get(FieldType.PG_REF_NUM.getName());
		try {
			if (hostUrl.contains("https")) {
				try {

					SSLContext sc = getSSLContext(PropertiesManager.propertiesMap.get(Constants.JKS_FILE_NAME));

					URL url = new URL(hostUrl);
					connection = (HttpsURLConnection) url.openConnection();
					connection.setSSLSocketFactory(sc.getSocketFactory());

					connection.setRequestMethod("POST");
					connection.setRequestProperty("X-IBM-Client-Secret",
							PropertiesManager.propertiesMap.get("YESBANK_UPI_HEADER_CLIENT_SECRET"));
					connection.setRequestProperty("X-IBM-Client-ID",
							PropertiesManager.propertiesMap.get("YESBANK_UPI_HEADER_CLIENT_ID"));
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
						logger.error("getLogIntentResponse:: yes bank: failed. responseCode={}, pgRefNo={}", code,
								pgRefNo);
						throw new SystemException(ErrorType.ACUIRER_DOWN,
								"Network Exception with Yes bank upi " + hostUrl.toString());
					}

					while ((line = rd.readLine()) != null) {
						serverResponse.append(line);

					}

					rd.close();

				} catch (Exception e) {
					logger.error("getLogIntentResponse:: yes bank: failed. msg={}, pgRefNo={}", e.getMessage(), pgRefNo,
							e);
					int code = 0;
					try {
						code = ((HttpURLConnection) connection).getResponseCode();
					} catch (IOException e1) {
						logger.error("getLogIntentResponse:: yes bank: failed. pgRefNo={}", pgRefNo, e1);
					}
					int firstDigitOfCode = Integer.parseInt(Integer.toString(code).substring(0, 1));
					if (firstDigitOfCode == 4 || firstDigitOfCode == 5) {
						fields.put(FieldType.STATUS.getName(), StatusType.ACQUIRER_DOWN.getName());
						logger.error("getLogIntentResponse:: yes bank: resCode={}, pgRefNo={}", code, pgRefNo);
						throw new SystemException(ErrorType.ACUIRER_DOWN,
								"Network Exception with Yes Bank Upi" + hostUrl.toString());
					}
				} finally {
					if (connection != null) {
						connection.disconnect();
					}

					System.clearProperty("https.protocols");
					System.clearProperty("javax.net.ssl.trustStore");
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
					logger.error("getLogIntentResponse:: yes bank: failed. pgRefNo={}", pgRefNo, e);
				} finally {
					if (simulatorConn != null) {
						simulatorConn.disconnect();
					}
				}

			}
		} catch (Exception e) {
			logger.error("getLogIntentResponse:: yes bank: failed. pgRefNo={}", pgRefNo, e);
			throw new SystemException(ErrorType.ACUIRER_DOWN,
					"Network Exception with Yes Bank Upi" + hostUrl.toString());
		}

		return serverResponse.toString();
	}

	@SuppressWarnings("incomplete-switch")
	public String getResponse(JSONObject request, Fields fields) throws SystemException {

		StringBuilder serverResponse = new StringBuilder();

		String hostUrl = "";

		TransactionType transactionType = TransactionType.getInstance(fields.get(FieldType.TXNTYPE.getName()));
		switch (transactionType) {
		case SALE:
			hostUrl = PropertiesManager.propertiesMap.get(Constants.YES_BANKCB_UPI_SALE_URL);
			break;
		case REFUND:
			hostUrl = PropertiesManager.propertiesMap.get(Constants.YES_BANKCB_UPI_REFUND_URL);
			break;
		case STATUS:
			hostUrl = PropertiesManager.propertiesMap.get(Constants.YES_BANKCB_UPI_STATUS_ENQUIRY_URL);
		case ENQUIRY:
			hostUrl = PropertiesManager.propertiesMap.get(Constants.YES_BANKCB_UPI_STATUS_ENQUIRY_URL);
		}
		logger.info(transactionType + " trxn typeurl-----" + hostUrl);
		HttpsURLConnection connection = null;
		HttpURLConnection simulatorConn = null;
		try {
			if (hostUrl.contains("https")) {
				try {

					SSLContext sc = getSSLContext(PropertiesManager.propertiesMap.get(Constants.JKS_FILE_NAME));

					URL url = new URL(hostUrl);
					connection = (HttpsURLConnection) url.openConnection();
					connection.setSSLSocketFactory(sc.getSocketFactory());

					connection.setRequestMethod("POST");
					connection.setRequestProperty("X-IBM-Client-Secret",
							PropertiesManager.propertiesMap.get("YESBANK_UPI_HEADER_CLIENT_SECRET"));
					connection.setRequestProperty("X-IBM-Client-ID",
							PropertiesManager.propertiesMap.get("YESBANK_UPI_HEADER_CLIENT_ID"));
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
					// TO HANDLE EXCEPTION IF BANK SERVER IS DOWN
					int code = ((HttpURLConnection) connection).getResponseCode();
					int firstDigitOfCode = Integer.parseInt(Integer.toString(code).substring(0, 1));
					if (firstDigitOfCode == 4 || firstDigitOfCode == 5) {
						fields.put(FieldType.STATUS.getName(), StatusType.ACQUIRER_DOWN.getName());
						logger.error("Response code of txn :" + code);
						throw new SystemException(ErrorType.ACUIRER_DOWN,
								"Network Exception with Yes Bank Upi" + hostUrl.toString());
					}

					while ((line = rd.readLine()) != null) {
						serverResponse.append(line);

					}

					rd.close();

				} catch (Exception e) {
					logger.error("Exception in yes bank upi communicator : " + e.getMessage());
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
								"Network Exception with Yes Bank Upi" + hostUrl.toString());
					}
				} finally {
					if (connection != null) {
						connection.disconnect();
					}

					System.clearProperty("https.protocols");
					System.clearProperty("javax.net.ssl.trustStore");
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
		} catch (Exception exception) {
			logger.error("Exception", exception);
			throw new SystemException(ErrorType.ACUIRER_DOWN,
					"Network Exception with Yes Bank Upi" + hostUrl.toString());
		}
		logger.info("Yes UPI response" + serverResponse.toString());
		return serverResponse.toString();
	}

	private SSLContext getSSLContext(String path) throws SystemException {

		try {

			char[] password = PropertiesManager.propertiesMap.get(Constants.JKS_PASSWORD).toCharArray();
			KeyManagerFactory keyManagerFactory = KeyManagerFactory
					.getInstance(KeyManagerFactory.getDefaultAlgorithm());
			KeyStore keyStore = KeyStore.getInstance("JKS");

			InputStream keyInput = new FileInputStream(new File(path));
			keyStore.load(keyInput, password);
			keyInput.close();
			keyManagerFactory.init(keyStore, password);
			// getKeyManagers
			KeyManager[] keyManagers = keyManagerFactory.getKeyManagers();

			SecureRandom secureRandom = new SecureRandom();

			SSLContext sc = SSLContext.getInstance("TLSv1.2");
			sc.init(keyManagers, null, secureRandom);

			return sc;
		}

		catch (NoSuchAlgorithmException | KeyStoreException | CertificateException | IOException
				| UnrecoverableKeyException | KeyManagementException e) {
			logger.error("Exception in getSSLContext , exsception = " + e);
			throw new SystemException(ErrorType.INTERNAL_SYSTEM_ERROR, e,
					"unknown exception in getSSLContext  for yes upi in TransactionCommunicator");
		}

	}

}
