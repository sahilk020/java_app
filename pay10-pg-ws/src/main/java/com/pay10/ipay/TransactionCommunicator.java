package com.pay10.ipay;

import java.io.IOException;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.methods.PostMethod;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.pay10.commons.exception.ErrorType;
import com.pay10.commons.exception.SystemException;
import com.pay10.commons.util.FieldType;
import com.pay10.commons.util.Fields;

@Service("iPayTransactionCommunicator")
public class TransactionCommunicator {

	private static Logger logger = LoggerFactory.getLogger(TransactionCommunicator.class.getName());

	public String transactEnquiry(Fields fields) throws SystemException {
		String statusRequest = fields.get(FieldType.IPAY_FINAL_REQUEST.getName());
		PostMethod postMethod = new PostMethod(statusRequest);
		return transact(postMethod);
	}
	
	private String transact(HttpMethod httpMethod) throws SystemException {
		String response = "";

		try {
			HttpClient httpClient = new HttpClient();
			httpClient.executeMethod(httpMethod);

			if (httpMethod.getStatusCode() == HttpStatus.SC_OK) {
				response = httpMethod.getResponseBodyAsString();
				logger.info("Status Response from : " + response);
			} else {
				throw new SystemException(ErrorType.INTERNAL_SYSTEM_ERROR,
						"Network Exception recieved with response code"
								+ httpMethod.getStatusCode());
			}
		} catch (IOException ioException) {
			throw new SystemException(ErrorType.INTERNAL_SYSTEM_ERROR,
					ioException, "Network Exception.");
		}
		logger.info("Response message: " + response);
		return response;
	}
	
	public String transactRefund(Fields fields) throws SystemException {
		String refundRequest = fields.get(FieldType.IPAY_FINAL_REQUEST.getName());
		PostMethod postMethod = new PostMethod(refundRequest);
		return transact(postMethod);
	}

}
