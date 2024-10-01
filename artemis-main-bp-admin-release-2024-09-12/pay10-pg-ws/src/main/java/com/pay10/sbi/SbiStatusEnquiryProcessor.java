package com.pay10.sbi;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import com.pay10.commons.util.Amount;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.methods.PostMethod;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.pay10.commons.api.Hasher;
import com.pay10.commons.exception.ErrorType;
import com.pay10.commons.exception.SystemException;
import com.pay10.commons.util.FieldType;
import com.pay10.commons.util.Fields;
import com.pay10.commons.util.PropertiesManager;
import com.pay10.pg.core.util.SbiUtil;

@Service
public class SbiStatusEnquiryProcessor {

	@Autowired
	@Qualifier("sbiTransactionConverter")
	private TransactionConverter converter;

	private SbiTransformer sbiTransformer = null;
	
	@Autowired
	private SbiUtil sbiUtil;
	
	private static Logger logger = LoggerFactory.getLogger(SbiStatusEnquiryProcessor.class.getName());
	
	public void enquiryProcessor(Fields fields) throws SystemException {
		String request = statusEnquiryRequest(fields);
		String response = transactStatus(request, fields);
		String decrptedResponse = sbiUtil.decrypt(response);
		updateFields(fields, decrptedResponse);

	}


	public String statusEnquiryRequest(Fields fields) throws SystemException {
		StringBuilder req = new StringBuilder();
		req.append("Ref_no");
		req.append("=");
		req.append(fields.get(FieldType.PG_REF_NUM.getName()));
		req.append("|");
		req.append("Amount");
		req.append("=");
		//req.append(Amount.toDecimal(fields.get(FieldType.AMOUNT.getName()), fields.get(FieldType.CURRENCY_CODE.getName())));
		req.append(Amount.toDecimal(fields.get(FieldType.TOTAL_AMOUNT.getName()), fields.get(FieldType.CURRENCY_CODE.getName())));
		String checksum = Hasher.getHash(req.toString());
		req.append("|");
		req.append(Constants.CHECKSUM);
		req.append("=");
		req.append(checksum.toLowerCase());
		logger.info("SBI status check plain text request: " + fields.get(FieldType.PG_REF_NUM.getName()) + " " + req.toString());
		String encryptedRequest = sbiUtil.encrypt(req.toString());
		System.out.println("SBI status check encrypted request "+ fields.get(FieldType.PG_REF_NUM.getName()) + " " + encryptedRequest);
		return encryptedRequest;

	}
	
	public String transactStatus(String encRequest, Fields fields) throws SystemException {
		String hostUrl = PropertiesManager.propertiesMap.get("SbiStatusEnqURL");
		PostMethod postMethod = new PostMethod(hostUrl);
		postMethod.addParameter("encdata", encRequest);
		postMethod.addParameter("merchant_code", fields.get(FieldType.MERCHANT_ID.getName()));

		return transact(postMethod, hostUrl);

	}
	
	public String transact(HttpMethod httpMethod, String hostUrl) throws SystemException {
		String response = "";
		try {
			HttpClient httpClient = new HttpClient();
			httpClient.executeMethod(httpMethod);

			if (httpMethod.getStatusCode() == HttpStatus.SC_OK) {
				response = httpMethod.getResponseBodyAsString();
				logger.info("Response from SBI: " + response);
			} else {
				throw new SystemException(ErrorType.INTERNAL_SYSTEM_ERROR, "Network Exception with SBI "
						+ hostUrl.toString() + "recieved response code" + httpMethod.getStatusCode());
			}
		} catch (IOException ioException) {
			throw new SystemException(ErrorType.INTERNAL_SYSTEM_ERROR, ioException,
					"Network Exception with SBI " + hostUrl.toString());
		}
		return response;

	}


	public void updateFields(Fields fields, String decrptedResponse) {

		Transaction transactionResponse = new Transaction();
		transactionResponse = toTransaction(decrptedResponse);

		sbiTransformer = new SbiTransformer(transactionResponse);
		sbiTransformer.updateResponse(fields);

	}// toTransaction()
	
	public Transaction toTransaction(String response) {

		Transaction transaction = new Transaction();

		String[] resparam = response.split("\\|");
		Map<String, String> resParamMap = new HashMap<String, String>();
		for (String param : resparam) {
			String[] parameterPair = param.split("=");
			if (parameterPair.length > 1) {
				resParamMap.put(parameterPair[0].trim(), parameterPair[1].trim());
			}
		}

		// Added by RR for SBI status enquiry check bug fix
		transaction.setStatus(resParamMap.get("Status"));
		transaction.setAcqId(resParamMap.get("sbi_ref_no"));
		transaction.setResponseMessage(resParamMap.get("PG_RESPONSE_MESSAGE"));
		return transaction;
	}// toTransaction()


}
