package com.pay10.ingenico;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.net.URLConnection;

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
@Service("ingenicoTransactionCommunicator")
public class TransactionCommunicator {

	private static Logger logger = LoggerFactory.getLogger(TransactionCommunicator.class.getName());

	public void updateSaleResponse(Fields fields, String request) {

		fields.put(FieldType.INGENICO_FINAL_REQUEST.getName(), request.toString());
		fields.put(FieldType.STATUS.getName(), StatusType.SENT_TO_BANK.getName());
		fields.put(FieldType.RESPONSE_CODE.getName(), ErrorType.SUCCESS.getResponseCode());
		fields.put(FieldType.RESPONSE_MESSAGE.getName(), ErrorType.SUCCESS.getResponseMessage());

	}

	// Communicator not needed for ingenico as we are doing a soap ws call from util
	/*
	 * @SuppressWarnings("incomplete-switch") public String getResponse(String
	 * request, Fields fields) throws SystemException {
	 * 
	 * String hostUrl = "";
	 * 
	 * try {
	 * 
	 * TransactionType transactionType =
	 * TransactionType.getInstance(fields.get(FieldType.TXNTYPE.getName())); switch
	 * (transactionType) { case SALE: case AUTHORISE: break; case ENROLL: break;
	 * case CAPTURE: break; case REFUND: hostUrl =
	 * PropertiesManager.propertiesMap.get(Constants.INGENICO_REFUND_URL); break;
	 * case STATUS: hostUrl =
	 * PropertiesManager.propertiesMap.get(Constants.INGENICO_STATUS_ENQ_URL);
	 * break; }
	 * 
	 * logger.info("Request message to INGENICO : TxnType = " +
	 * fields.get(FieldType.TXNTYPE.getName()) + " " + "Order Id = " +
	 * fields.get(FieldType.ORDER_ID.getName()) + "  Request = " + request);
	 * 
	 * URL url = new URL(hostUrl+request); URLConnection conn =
	 * url.openConnection();
	 * 
	 * conn.setDoOutput(true);
	 * 
	 * OutputStreamWriter writer = new OutputStreamWriter(conn.getOutputStream());
	 * 
	 * writer.write(request); writer.flush();
	 * 
	 * String line; StringBuilder response = new StringBuilder(); BufferedReader
	 * reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
	 * 
	 * while ((line = reader.readLine()) != null) { response.append(line);
	 * 
	 * } logger.info("Response received for INGENICO Refund Response >>> " +
	 * response.toString()); writer.close(); reader.close();
	 * 
	 * return response.toString();
	 * 
	 * } catch (Exception e) {
	 * logger.error("Exception in INGENICO txn Communicator", e); } return null;
	 * 
	 * }
	 */


}
