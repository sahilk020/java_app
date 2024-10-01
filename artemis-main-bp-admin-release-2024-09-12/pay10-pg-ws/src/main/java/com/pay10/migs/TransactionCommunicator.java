package com.pay10.migs;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.regex.Pattern;
import javax.net.ssl.HttpsURLConnection;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.pay10.commons.exception.ErrorType;
import com.pay10.commons.exception.SystemException;
import com.pay10.commons.util.AcquirerType;
import com.pay10.commons.util.ConfigurationConstants;
import com.pay10.commons.util.FieldType;
import com.pay10.commons.util.Fields;
import com.pay10.commons.util.StatusType;
import com.pay10.commons.util.SystemConstants;
import com.pay10.commons.util.TransactionType;

@Service("migsTransactionCommunicator")
public class TransactionCommunicator {
	@Autowired
	@Qualifier("migsTransactionConverter")
	private TransactionConverter txnConverter;
	
	private static Logger logger = LoggerFactory.getLogger(TransactionCommunicator.class.getName());

	public String getResponseString(String request, Fields fields) throws SystemException {
		String url = null;
		String response = "";

		switch (TransactionType.getInstance(fields.get(FieldType.TXNTYPE.getName()))) {
		case ENROLL:
			if (fields.get(FieldType.ACQUIRER_TYPE.getName()).equals(AcquirerType.AXISMIGS.getCode())) {
				url = ConfigurationConstants.AXIS_MIGS_TRANSACTION_URL.getValue();
				fields.put(FieldType.MIGS_FINAL_REQUEST.getName(), request);
			} else {
				url = ConfigurationConstants.AMEX_TRANSACTION_URL.getValue();
				fields.put(FieldType.IPAY_FINAL_REQUEST.getName(), request);
			}
			break;
		case SALE:
		case AUTHORISE:
			if (fields.get(FieldType.ACQUIRER_TYPE.getName()).equals(AcquirerType.AXISMIGS.getCode())) {
				url = ConfigurationConstants.AXIS_MIGS_SUPPORT_URL.getValue();
			} else {
				url = ConfigurationConstants.AMEX_SUPPORT_URL.getValue();
			}
			response = transact(fields, request, url);
			break;
		case CAPTURE:
			if (fields.get(FieldType.ACQUIRER_TYPE.getName()).equals(AcquirerType.AXISMIGS.getCode())) {
				url = ConfigurationConstants.AXIS_MIGS_SUPPORT_URL.getValue();
			} else {
				url = ConfigurationConstants.AMEX_SUPPORT_URL.getValue();
			}
			response = transact(fields, request, url);
			break;
		case REFUND:
			if (fields.get(FieldType.ACQUIRER_TYPE.getName()).equals(AcquirerType.AXISMIGS.getCode())) {
				url = ConfigurationConstants.AXIS_MIGS_SUPPORT_URL.getValue();
			} else {
				url = ConfigurationConstants.AMEX_SUPPORT_URL.getValue();
			}
			response = transact(fields, request, url);
			break;
		case ENQUIRY:
			if (fields.get(FieldType.ACQUIRER_TYPE.getName()).equals(AcquirerType.AXISMIGS.getCode())) {
				url = ConfigurationConstants.AXIS_MIGS_SUPPORT_URL.getValue();
			} else {
				url = ConfigurationConstants.AMEX_SUPPORT_URL.getValue();
			}
			response = transact(fields, request, url);
			break;
		default:
			break;
		}
		return response;
	}

	public Transaction getResponseObject(String request, Fields fields) throws SystemException {

		String response = getResponseString(request, fields);
		return txnConverter.getResponse(response);
	}

	public String transact(Fields fields, String request, String hostUrl) throws SystemException {
		logRequest(request, fields);
		String response = "";
		try {
			URL url = new URL(hostUrl);
			HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();

			connection.setRequestProperty("Accept-Charset", SystemConstants.DEFAULT_ENCODING_UTF_8);
			connection.setDoInput(true);
			connection.setDoOutput(true);
			connection.setUseCaches(false);
			connection.setRequestMethod("POST");

			DataOutputStream dataoutputstream = new DataOutputStream(connection.getOutputStream());

			dataoutputstream.write(request.getBytes(SystemConstants.DEFAULT_ENCODING_UTF_8));
			dataoutputstream.flush();
			dataoutputstream.close();

			BufferedReader bufferedreader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
			String decodedString;
			int code = ((HttpURLConnection) connection).getResponseCode();
			int firstDigitOfCode = Integer.parseInt(Integer.toString(code).substring(0, 1));
			if(firstDigitOfCode == 4 || firstDigitOfCode == 5){
				 fields.put(FieldType.STATUS.getName(),StatusType.ACQUIRER_DOWN.getName());
				 logger.error("Response code of txn :" + code);
				 throw new SystemException(ErrorType.ACUIRER_DOWN,
							 "Network Exception with Migs "
									+ hostUrl.toString());
				}
			while ((decodedString = bufferedreader.readLine()) != null) {
				response = response + decodedString;
			}
			bufferedreader.close();
			connection.disconnect();
		} catch (IOException ioException) {
			logger.error("exception : " ,ioException);
			throw new SystemException(ErrorType.INTERNAL_SYSTEM_ERROR, ioException, "Network Exception with Amex for "
					+ fields.get(FieldType.TXNTYPE.getName()) + " txn " + hostUrl.toString());
		}
		logResponse(response, fields);
		return response;
	}

	public String executePost(String targetURL, String urlParameters) throws Exception {
		URL url;
		HttpURLConnection connection = null;
		String resStr = null;
		try {
			// Create connection
			url = new URL(targetURL);
			connection = (HttpURLConnection) url.openConnection();
			connection.setRequestMethod("POST");
			connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");

			connection.setRequestProperty("Content-Length", "" + Integer.toString(urlParameters.getBytes().length));
			connection.setRequestProperty("Content-Language", "en-US");

			connection.setUseCaches(false);
			connection.setDoInput(true);
			connection.setDoOutput(true);

			// Send request
			DataOutputStream wr = new DataOutputStream(connection.getOutputStream());
			wr.writeBytes(urlParameters);
			wr.flush();
			wr.close();

			// Get Response
			InputStream is = connection.getInputStream();
			BufferedReader rd = new BufferedReader(new InputStreamReader(is));
			String line;
			StringBuffer response = new StringBuffer();
			while ((line = rd.readLine()) != null) {
				response.append(line);
				response.append('\r');
			}
			rd.close();

			resStr = response.toString();

			if (resStr != null)
				return resStr.trim();
			else
				return null;

		} catch (Exception exception) {
			logger.error("exception : ", exception);
			throw new SystemException(ErrorType.ACQUIRER_ERROR, "Unable to get respose from ezeeclick");
		} finally {

			if (connection != null) {
				connection.disconnect();
			}
		}
	}
	public void logRequest(String requestMessage, String url,Fields fields){
		log("Request message to Amex: Url= "+url +" "+ requestMessage, fields);
	}
	
	public void logResponse(String responseMessage, String url,Fields fields){
		log("Response message from Amex: Url= " +url+" "+responseMessage, fields);
	}

	public void logRequest(String requestMessage, Fields fields) {
		log("Request message to Amex:" + requestMessage, fields);
	}

	public void logResponse(String responseMessage, Fields fields) {
		log("Response message from Amex:" + responseMessage, fields);
	}

	public void log(String message, Fields fields) {
		message = Pattern.compile("(vpc_CardNum=)([\\s\\S]*?)(&)").matcher(message).replaceAll("$1$3");
		message = Pattern.compile("(vpc_CardExp=)([\\s\\S]*?)(&)").matcher(message).replaceAll("$1$3");
		message = Pattern.compile("(vpc_CardSecurityCode=)([\\s\\S]*?)(&)").matcher(message).replaceAll("$1$3");

		MDC.put(FieldType.INTERNAL_CUSTOM_MDC.getName(), fields.getCustomMDC());
		logger.info(message);
	}

}
