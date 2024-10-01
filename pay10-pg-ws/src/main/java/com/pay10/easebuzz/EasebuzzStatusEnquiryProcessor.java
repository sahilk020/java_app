package com.pay10.easebuzz;

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
public class EasebuzzStatusEnquiryProcessor {

	@Autowired
	@Qualifier("easebuzzTransactionConverter")
	private TransactionConverter converter;

	@Autowired
	@Qualifier("easebuzzTransactionCommunicator")
	private TransactionCommunicator transactionCommunicator;

	private EasebuzzTransformer easebuzzTransformer = null;

	private static Logger logger = LoggerFactory.getLogger(EasebuzzStatusEnquiryProcessor.class.getName());

	public void enquiryProcessor(Fields fields) {
		String request = statusEnquiryRequest(fields);
		String response = "";
		try {
			response = getResponse(request);
		} catch (SystemException exception) {
			logger.error("Exception", exception);
		}

		updateFields(fields, response);

	}

	public String statusEnquiryRequest(Fields fields) {

		String request = null;
		try {
			request = converter.statusEnquiryRequest(fields, null);
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

	public void updateFields(Fields fields, String jsonResponse) {

		Transaction transactionResponse = new Transaction();
		transactionResponse = converter.toTransactionStatus(jsonResponse);
		easebuzzTransformer = new EasebuzzTransformer(transactionResponse);
		easebuzzTransformer.updateStatusResponse(fields);

	}// toTransaction()

}
