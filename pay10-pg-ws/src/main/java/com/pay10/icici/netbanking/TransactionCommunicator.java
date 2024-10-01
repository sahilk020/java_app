package com.pay10.icici.netbanking;

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
import com.pay10.commons.util.StatusType;

@Service("iciciBankNBTransactionCommunicator")
public class TransactionCommunicator {

	private static Logger logger = LoggerFactory.getLogger(TransactionCommunicator.class.getName());

	public void updateSaleResponse(Fields fields, String request) {

		fields.put(FieldType.ICICI_NB_FINAL_REQUEST.getName(), request.toString());
		fields.put(FieldType.STATUS.getName(), StatusType.SENT_TO_BANK.getName());
		fields.put(FieldType.RESPONSE_CODE.getName(), ErrorType.SUCCESS.getResponseCode());
		fields.put(FieldType.RESPONSE_MESSAGE.getName(), ErrorType.SUCCESS.getResponseMessage());

	}

	public String transactStatus(String hostUrl) throws SystemException {
        PostMethod postMethod = new PostMethod(hostUrl);
        return transact(postMethod, hostUrl);

    }

    public String transact(HttpMethod httpMethod, String hostUrl) throws SystemException {
        String response = "";

        try {
            HttpClient httpClient = new HttpClient();
            httpClient.executeMethod(httpMethod);

            if (httpMethod.getStatusCode() == HttpStatus.SC_OK) {
                response = httpMethod.getResponseBodyAsString();
                logger.info("Verification Response from ICICI Bank: " + response);
            } else {
                throw new SystemException(ErrorType.INTERNAL_SYSTEM_ERROR, "Network Exception with ICICI Bank "
                        + hostUrl + "Verification response code received" + httpMethod.getStatusCode());
            }
        } catch (IOException ioException) {
            throw new SystemException(ErrorType.INTERNAL_SYSTEM_ERROR, ioException,
                    "Network Exception with ICICI Bank " + hostUrl);
        }
        return response;

    }

}
