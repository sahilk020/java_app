package com.pay10.billdesk;

import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.pay10.commons.exception.SystemException;
import com.pay10.commons.util.FieldType;
import com.pay10.commons.util.Fields;
import com.pay10.commons.util.PropertiesManager;

@Service
public class BilldeskStatusEnquiryProcessor {

	@Autowired
	@Qualifier("billdeskTransactionConverter")
	private TransactionConverter converter;

	@Autowired
	@Qualifier("billdeskTransactionCommunicator")
	private TransactionCommunicator transactionCommunicator;

	private BilldeskTransformer billdeskTransformer = null;

	private static Logger logger = LoggerFactory.getLogger(BilldeskStatusEnquiryProcessor.class.getName());

	public void enquiryProcessor(Fields fields) {
		String request = statusEnquiryRequest(fields);
		String response = "";
		try {
			response = getResponse(request);
		} catch (SystemException exception) {
			logger.error("Exception", exception);
		}

		updateFields(fields, response);

		logger.info("Billdesk Status Eqnuiry response , status == " +fields.get(FieldType.STATUS.getName()) +  "PG REf == " +fields.get(FieldType.PG_REF_NUM.getName()));
	}

	public String statusEnquiryRequest(Fields fields) {

		String request = null;
		try {
			request = converter.statusEnquiryRequest(fields, null);
			logger.info("Billdesk Status Enquiry Request >> " + request);
			return request;
		} catch (Exception e) {
			logger.error("Exception e", e);
			return request;
		}

	}

	public String getResponse(String request) throws SystemException {

		try {
			String hostUrl = PropertiesManager.propertiesMap.get(Constants.STATUS_ENQ_REQUEST_URL);
			String response = transactionCommunicator.statusEnqPostRequest(request, hostUrl);
			
			return response;

		} catch (Exception e) {

			return null;
		}
	}

	public void updateFields(Fields fields, String xml) {

		Transaction transactionResponse = new Transaction();
		transactionResponse = converter.toTransactionStatus(xml);
		billdeskTransformer = new BilldeskTransformer(transactionResponse);
		billdeskTransformer.updateStatusResponse(fields);

	}// toTransaction()

	public static void logRequest(String requestMessage, Fields fields) {
		log("Request message to BIlldesk: TxnType = " + fields.get(FieldType.TXNTYPE.getName()) + " " + "Txn id = "
				+ fields.get(FieldType.TXN_ID.getName()) + " " + "Url= " + requestMessage, fields);
	}

	private static void log(String message, Fields fields) {
		message = Pattern.compile("(<password>)([\\s\\S]*?)(</password>)").matcher(message).replaceAll("$1$3");
		MDC.put(FieldType.INTERNAL_CUSTOM_MDC.getName(), fields.getCustomMDC());
		logger.info(message);
	}
}
