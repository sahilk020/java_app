package com.pay10.mobikwik;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;

import javax.net.ssl.HttpsURLConnection;

import org.apache.commons.lang3.StringUtils;
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
 * @author Shaiwal
 *
 */
@Service("mobikwikTransactionCommunicator")
public class TransactionCommunicator {

	private static Logger logger = LoggerFactory.getLogger(TransactionCommunicator.class.getName());

	public void updateSaleResponse(Fields fields, String request) {

		fields.put(FieldType.MOBIKWIK_FINAL_REQUEST.getName(), request.toString());
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
				hostUrl = PropertiesManager.propertiesMap.get(Constants.MOBIKWIK_REFUND_URL);
				break;
			case STATUS:
				hostUrl = PropertiesManager.propertiesMap.get(Constants.MOBIKWIK_STATUS_ENQ_URL);
				break;
			}

			logger.info("Request message to Mobikwik : TxnType = " + fields.get(FieldType.TXNTYPE.getName()) + " "
					+ "Order Id = " + fields.get(FieldType.ORDER_ID.getName()) + "  Request = " + request);

			URL url = new URL(hostUrl+request);
			URLConnection conn = url.openConnection();
			conn.setDoOutput(true);
			OutputStreamWriter writer = new OutputStreamWriter(conn.getOutputStream());

			writer.write(request.toString());
			writer.flush();

			String line;
			StringBuilder serverResponse = new StringBuilder();
			BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));

			while ((line = reader.readLine()) != null) {
				serverResponse.append(line);
				
			}
			writer.close();
			reader.close();  
			
			logger.info(" Response received For mobikwik Refund >> " + serverResponse.toString());
			
			if (StringUtils.isNotBlank(serverResponse.toString())) {
				return serverResponse.toString();
			}
			

		} catch (Exception e) {
			logger.error("Exception in mobikwik txn Communicator", e);
		}
		return null;

	}

	public static String getTextBetweenTags(String text, String tag1, String tag2) {

		int leftIndex = text.indexOf(tag1);
		if (leftIndex == -1) {
			return null;
		}

		int rightIndex = text.indexOf(tag2);
		if (rightIndex != -1) {
			leftIndex = leftIndex + tag1.length();
			return text.substring(leftIndex, rightIndex);
		}

		return null;
	}

}
