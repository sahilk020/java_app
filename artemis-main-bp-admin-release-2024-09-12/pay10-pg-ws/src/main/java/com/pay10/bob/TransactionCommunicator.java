package com.pay10.bob;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.regex.Pattern;

import javax.net.ssl.HttpsURLConnection;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.pay10.commons.exception.ErrorType;
import com.pay10.commons.exception.SystemException;
import com.pay10.commons.util.ConfigurationConstants;
import com.pay10.commons.util.FieldType;
import com.pay10.commons.util.Fields;
import com.pay10.commons.util.PaymentType;
import com.pay10.commons.util.PropertiesManager;
import com.pay10.commons.util.StatusType;
import com.pay10.commons.util.TransactionType;
import com.pay10.pg.core.whitelabel.ReturnUrlCustomizer;

/**
 * @author Rahul
 *
 */
@Service("bobTransactionCommunicator")
public class TransactionCommunicator {

	private static Logger logger = LoggerFactory.getLogger(TransactionCommunicator.class.getName());
	
	@Autowired
	private ReturnUrlCustomizer returnUrlCustomizer;
	public void updateSaleResponse(Fields fields, String request) {
		
		String requestUrl = ConfigurationConstants.BOB_SALE_REQUEST_URL.getValue();
		String responseUrl = returnUrlCustomizer.customizeReturnUrl(fields, ConfigurationConstants.BOB_RESPONSE_URL.getValue());
			
		StringBuilder req = new StringBuilder();
		req.append(requestUrl);
		req.append(request);
		req.append(Constants.BOB_ERROR_URL_TAG);
		req.append(responseUrl);
		req.append(Constants.BOB_RESPONSE_URL_TAG);
		req.append(responseUrl);
		req.append(Constants.BOB_TRANPORTAL_ID_TAG);
		req.append(fields.get(FieldType.MERCHANT_ID.getName()));
		
		fields.put(FieldType.BOB_FINAL_REQUEST.getName(), req.toString());
		fields.put(FieldType.STATUS.getName(), StatusType.SENT_TO_BANK.getName());
		fields.put(FieldType.RESPONSE_CODE.getName(), ErrorType.SUCCESS.getResponseCode());
		fields.put(FieldType.RESPONSE_MESSAGE.getName(), ErrorType.SUCCESS.getResponseMessage());

	}

	@SuppressWarnings("incomplete-switch")
	public String getResponse(String request, Fields fields) throws SystemException {

		String hostUrl = "";

		try {

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
				hostUrl = PropertiesManager.propertiesMap.get(Constants.REFUND_URL);
				break;
			case STATUS:
				hostUrl = PropertiesManager.propertiesMap.get(Constants.STATUS_ENQ_URL);
				break;
			}

			URL url = new URL(hostUrl);
			//logger.info("Request message to bob: TxnType = " + fields.get(FieldType.TXNTYPE.getName()) + " " + "Txn id = " + fields.get(FieldType.TXN_ID.getName()) + " " + request);
			logRequest(request.toString(), fields);

			URLConnection connection = null;
			if (ConfigurationConstants.IS_DEBUG.getValue().equals("1")) {
				connection = url.openConnection();
				connection.setRequestProperty("Content-Type", "application/xml");
			} else {
				connection = url.openConnection();
				connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
			}
			connection.setDoInput(true);
			connection.setDoOutput(true);
			connection.setUseCaches(false);

			DataOutputStream dataoutputstream = new DataOutputStream(connection.getOutputStream());
			dataoutputstream.writeBytes(request);
			dataoutputstream.flush();
			dataoutputstream.close();
			BufferedReader bufferedreader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
			String decodedString;
			String response = "";
			while ((decodedString = bufferedreader.readLine()) != null) {
				response = response + decodedString;
			}
			logger.info("Response mesage from bob: TxnType = " + fields.get(FieldType.TXNTYPE.getName()) + " " + "Txn id = " + fields.get(FieldType.TXN_ID.getName()) + " " + response);
			// logResponse(response, hostUrl,fields);
			return response;
		} catch (IOException ioException) {
			MDC.put(FieldType.INTERNAL_CUSTOM_MDC.getName(), fields.getCustomMDC());
			fields.put(FieldType.STATUS.getName(), StatusType.ERROR.getName());
			logger.error("Network Exception with BOB", ioException);
			throw new SystemException(ErrorType.INTERNAL_SYSTEM_ERROR, ioException,
					"Network Exception with BOB " + hostUrl.toString());
		}
	}

	
	public void logRequest(String requestMessage, Fields fields) {
		log("Request message to BOB: TxnType = " + fields.get(FieldType.TXNTYPE.getName()) + " " + "Txn id = "
				+ fields.get(FieldType.TXN_ID.getName()) + " " + "Url= " + requestMessage, fields);
	}

	private void log(String message, Fields fields) {
		
		if (fields.get(FieldType.PAYMENT_TYPE.getName()).equalsIgnoreCase(PaymentType.UPI.getCode())) {
			message = Pattern.compile("(<pan>)([\\s\\S]*?)(</pan>)").matcher(message).replaceAll("$1$3");
			message = Pattern.compile("(<password>)([\\s\\S]*?)(</password>)").matcher(message).replaceAll("$1$3");
			MDC.put(FieldType.INTERNAL_CUSTOM_MDC.getName(), fields.getCustomMDC());
			logger.info(message);
		}
		else {
			message = Pattern.compile("(<card>)([\\s\\S]*?)(</card>)").matcher(message).replaceAll("$1$3");
			message = Pattern.compile("(<pan>)([\\s\\S]*?)(</pan>)").matcher(message).replaceAll("$1$3");
			message = Pattern.compile("(<expmonth>)([\\s\\S]*?)(</expmonth>)").matcher(message).replaceAll("$1$3");
			message = Pattern.compile("(<expyear>)([\\s\\S]*?)(</expyear>)").matcher(message).replaceAll("$1$3");
			message = Pattern.compile("(<cvv2>)([\\s\\S]*?)(</cvv2>)").matcher(message).replaceAll("$1$3");
			message = Pattern.compile("(<password>)([\\s\\S]*?)(</password>)").matcher(message).replaceAll("$1$3");
			MDC.put(FieldType.INTERNAL_CUSTOM_MDC.getName(), fields.getCustomMDC());
			logger.info(message);
		}
	}
}
