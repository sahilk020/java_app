package com.pay10.phonepe;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.pay10.commons.exception.SystemException;
import com.pay10.commons.util.FieldType;
import com.pay10.commons.util.Fields;

import bsh.This;

@Component
public class PhonePeEnquiryRetryer {

	private static final Logger logger = LoggerFactory.getLogger(This.class.getName());
	private static long MAX_RETRY_TIME = 900000;

	@Autowired
	private PhonePeStatusEnquiryProcessor phonePeStatusEnquiryProcessor;

	/**
	 * This method is used for invoked status inquiry API till we get final response
	 * from acquirer.<br>
	 * Algorithm to invoked API with retry is with below conditions. <br>
	 * First invocation of API should be after 25 seconds of response received. <br>
	 * if response is in pending status<br>
	 * then every 3 seconds it will execute the status inquiry API for next 60
	 * seconds.<br>
	 * then every 6 seconds it will execute the status inquiry API for next 60
	 * seconds.<br>
	 * then every 10 seconds it will execute the status inquiry API for next 60
	 * seconds. <br>
	 * then every 30 seconds it will execute the status inquiry API for next 60
	 * seconds.<br>
	 * then every 1 minutes it will execute the status inquiry API till timeout.
	 * (Max 15 minutes).
	 * 
	 * 
	 * @param transaction response received from the acquirer side for the executed
	 *                    transaction request.
	 * @param fields      fields is used to prepare request for status inquiry
	 * @return response which is received on status inquiry API.
	 */
	public Transaction retry(Transaction transaction, Fields fields) {
		long RETRY_INTERVAL = 25000;
		int retryCount = 1;
		long retryTime = 0;
		String amount = transaction.getAmount();
		while (StringUtils.equalsAnyIgnoreCase(transaction.getCode(), PhonePeResultType.PHONEPE004.getBankCode(),
				PhonePeResultType.PHONEPE006.getBankCode()) && retryTime < MAX_RETRY_TIME) {
			try {
				logger.info("retry:: retry started count={}", retryCount);
				Thread.sleep(RETRY_INTERVAL);
				if (retryCount > 1) {
					retryTime = retryTime + RETRY_INTERVAL;
				}
				if (retryCount == 1) {
					RETRY_INTERVAL = 3000;
				} else if (retryCount == 10) {
					RETRY_INTERVAL = 6000;
				} else if (retryCount == 20) {
					RETRY_INTERVAL = 10000;
				} else if (retryCount == 26) {
					RETRY_INTERVAL = 30000;
				} else if (retryCount == 28) {
					RETRY_INTERVAL = 60000;
				}

				transaction = statusEnquiry(fields);
				retryCount++;
			} catch (Exception ex) {
				logger.error("retry:: failed. ex", ex);
			}
		}
		logger.info("retry:: retry completed.");
		transaction.setAmount(amount);
		return transaction;
	}

	@SuppressWarnings("static-access")
	private Transaction statusEnquiry(Fields fields) throws SystemException {

		String request = phonePeStatusEnquiryProcessor.statusEnquiryRequest(fields);

		logger.info("statusEnquiry:: orderId={}, request={}", fields.get(FieldType.ORDER_ID.getName()), request);

		String response = phonePeStatusEnquiryProcessor.getResponse(request);
		logger.info("statusEnquiry:: orderId={}, response={}", fields.get(FieldType.ORDER_ID.getName()), response);

		phonePeStatusEnquiryProcessor.updateFields(fields, response);

		Transaction responseVerification = phonePeStatusEnquiryProcessor.toTransaction(response);
		return responseVerification;
	}
}
