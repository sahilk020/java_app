package com.pay10.yesbank.netbanking;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.pay10.commons.exception.SystemException;
import com.pay10.commons.util.Fields;
import com.pay10.commons.util.PropertiesManager;

@Service
public class YesbankNBStatusEnquiryProcessor {
	@Autowired
	@Qualifier("yesbankNBTransactionConverter")
	private TransactionConverter converter;

	@Autowired
	@Qualifier("yesbankNBTransactionCommunicator")
	private TransactionCommunicator transactionCommunicator;

	private YesbankNBTransformer yesbankNBTransformer;

	private static Logger logger = LoggerFactory.getLogger(YesbankNBStatusEnquiryProcessor.class.getName());

	public void enquiryProcessor(Fields fields) {

		String response = "";
		try {
			String request = statusEnquiryRequest(fields);
			response = getResponse(request, fields);
		} catch (SystemException exception) {
			logger.error("Exception", exception);
		}

		updateFields(fields, response);

	}

	public String statusEnquiryRequest(Fields fields) {

		Transaction transaction = new Transaction();
		String encrequest = converter.statusEnquiryRequest(fields, transaction);

		return encrequest;

	}

	public String getResponse(String request, Fields fields) throws SystemException {

		String hostUrl = PropertiesManager.propertiesMap.get(Constants.YESBANK_STATUS_ENQ_REQUEST_URL);
		String response = transactionCommunicator.getEnqResponse(request, hostUrl, fields);

		return response;

	}

	public String getTextBetweenTags(String text, String tag1, String tag2) {

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

	public void updateFields(Fields fields, String xmlResponse) {

		try {
			Transaction transactionResponse = new Transaction();
			transactionResponse = converter.toTransactionStatus(xmlResponse);
			yesbankNBTransformer = new YesbankNBTransformer(transactionResponse);
			yesbankNBTransformer.updateStatusResponse(fields);

		} catch (Exception e) {
			logger.error("Exception in parsing status enquiry response for yes bank nb ", e);
		}

	}
}
