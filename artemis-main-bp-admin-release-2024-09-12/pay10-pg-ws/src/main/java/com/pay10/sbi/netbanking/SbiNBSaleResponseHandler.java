package com.pay10.sbi.netbanking;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.pay10.commons.api.Hasher;
import com.pay10.commons.exception.ErrorType;
import com.pay10.commons.exception.SystemException;
import com.pay10.commons.user.Account;
import com.pay10.commons.user.AccountCurrency;
import com.pay10.commons.user.User;
import com.pay10.commons.user.UserDao;
import com.pay10.commons.util.AcquirerType;
import com.pay10.commons.util.Amount;
import com.pay10.commons.util.FieldType;
import com.pay10.commons.util.Fields;
import com.pay10.commons.util.PropertiesManager;
import com.pay10.commons.util.StatusType;
import com.pay10.commons.util.TransactionManager;
import com.pay10.commons.util.Validator;
import com.pay10.pg.core.util.ProcessManager;
import com.pay10.pg.core.util.Processor;
import com.pay10.pg.core.util.SbiUtil;
import com.pay10.pg.history.Historian;
import com.pay10.sbi.SbiTransformer;

@Service
public class SbiNBSaleResponseHandler {

	private static Logger logger = LoggerFactory.getLogger(SbiNBSaleResponseHandler.class.getName());

	@Autowired
	private Validator generalValidator;
	
	@Autowired
	private Historian historian;

	@Autowired
	@Qualifier("updateProcessor")
	private Processor updateProcessor;

	@Autowired
	private SbiNBTransformer sbiTransformer;
	
	@Autowired
	private UserDao userDao;
	
	@Autowired
	private SbiUtil sbiUtil;

	public Map<String, String> process(Fields fields) throws SystemException, UnsupportedEncodingException {
		String newTxnId = TransactionManager.getNewTransactionId();
		fields.put(FieldType.TXN_ID.getName(), newTxnId);
		generalValidator.validate(fields);
		String doubleVerifcationrequest = doubleVerificationRequest(fields);
		String response = transactStatus(doubleVerifcationrequest, fields);
		String decrptedResponse = sbiUtil.decrypt(response);
		logger.info("Sbi double verification response " + fields.get(FieldType.PG_REF_NUM.getName()) + " " + decrptedResponse);
		Transaction transactionResponse = new Transaction();
		transactionResponse = toTransaction(decrptedResponse);
		logger.info("SBI-NB Sale Response Process::  pgRefNo={},totalAmount={},respAmount={} ", fields.get(FieldType.PG_REF_NUM.getName()), fields.get(FieldType.TOTAL_AMOUNT.getName()), transactionResponse.getAmount());
		if(StringUtils.equalsIgnoreCase(transactionResponse.getStatus(), "Pending")) {
			logger.info("SBI-NB Sale Response Received For Pending ::  pgRefNo={}", fields.get(FieldType.PG_REF_NUM.getName()));
			fields.put(FieldType.RESPONSE_CODE.getName(), ErrorType.PENDING.getCode());
			fields.put(FieldType.RESPONSE_MESSAGE.getName(), ErrorType.PENDING.getResponseMessage());
			fields.put(FieldType.INTERNAL_ORIG_TXN_TYPE.getName(), fields.get(FieldType.TXNTYPE.getName()));
			fields.remove(FieldType.SBI_RESPONSE_FIELD.getName());
			return fields.getFields();
		}
		if ( transactionResponse.getRef().equalsIgnoreCase(fields.get(FieldType.PG_REF_NUM.getName()) )
				&&transactionResponse.getAmount().equalsIgnoreCase(fields.get(FieldType.TOTAL_AMOUNT.getName()))) {

			sbiTransformer = new SbiNBTransformer(transactionResponse);
			sbiTransformer.updateResponse(fields);
		} else {
			logger.info(
					"doubleVerification:: failed.  resAmount={},pgRefNo={}",
					transactionResponse.getAmount(),transactionResponse.getRef());
		
			fields.put(FieldType.STATUS.getName(), StatusType.DENIED.getName());
			fields.put(FieldType.RESPONSE_CODE.getName(), ErrorType.SIGNATURE_MISMATCH.getCode());
			fields.put(FieldType.RESPONSE_MESSAGE.getName(), ErrorType.SIGNATURE_MISMATCH.getResponseMessage());
		}
		
		

		//historian.addPreviousSaleFields(fields);
		fields.put(FieldType.INTERNAL_ORIG_TXN_TYPE.getName(), fields.get(FieldType.TXNTYPE.getName()));
		fields.remove(FieldType.SBI_RESPONSE_FIELD.getName());
		ProcessManager.flow(updateProcessor, fields, true);
		return fields.getFields();

	}

	public String transactStatus(String encRequest, Fields fields) throws SystemException {
		String hostUrl = PropertiesManager.propertiesMap.get("SbiStatusEnqURL");
		String merchantId = getTxnKey(fields);
		PostMethod postMethod = new PostMethod(hostUrl);
		postMethod.addParameter("encdata", encRequest);
		postMethod.addParameter("merchant_code", merchantId);

		return transact(postMethod, hostUrl);

	}
	
	public String getTxnKey(Fields fields) throws SystemException {
		logger.info("SBI-NB getTxnKey : "+ fields.get(FieldType.PG_REF_NUM.getName()) +", "+fields.getFieldsAsString());
		User user = userDao.findPayId(fields.get(FieldType.PAY_ID.getName()));
		Account account = null;
		Set<Account> accounts = user.getAccounts();

		if (accounts == null || accounts.size() == 0) {
			logger.info("No account found for Pay ID = " + fields.get(FieldType.PAY_ID.getName()) + " and ORDER ID = "
					+ fields.get(FieldType.ORDER_ID.getName()));
		} else {
			for (Account accountThis : accounts) {
				if (accountThis.getAcquirerName()
						.equalsIgnoreCase(AcquirerType.getInstancefromCode(AcquirerType.SBINB.getCode()).getName())) {
					account = accountThis;
					break;
				}
			}
		}

		AccountCurrency accountCurrency = account.getAccountCurrency(fields.get(FieldType.CURRENCY_CODE.getName()));
		String merchantId = accountCurrency.getMerchantId();
		return merchantId;

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

	public String doubleVerificationRequest(Fields fields) throws SystemException, UnsupportedEncodingException {
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
		logger.info("Double verification plain text request: " + fields.get(FieldType.PG_REF_NUM.getName()) + " " + req.toString());
		String encryptedRequest = sbiUtil.encrypt(req.toString());
		System.out.println("Double verification encrypted request "+ fields.get(FieldType.PG_REF_NUM.getName()) + " " + encryptedRequest);
		return encryptedRequest;
	}
	
	public Transaction toTransaction(String response) {

		Transaction transaction = new Transaction();

		//String[] resparam = response.split("\\|");
		String[] resparam = StringUtils.split(response,"|");
		logger.info("SBI Response post parsing : {}", Arrays.asList(resparam));
		Map<String, String> resParamMap = new HashMap<String, String>();
		for (String param : resparam) {
			String[] parameterPair = param.split("=");
			if (parameterPair.length > 1) {
				resParamMap.put(parameterPair[0].trim(), parameterPair[1].trim());
			}
		}
		transaction.setRef(resParamMap.get("Ref_no"));
		//String amountas = String.valueOf(Math.round(Double.valueOf(resParamMap.get("Amount"))) * 100);
		//transaction.setAmount(amountas);
		
		String amount = Amount.formatAmount(resParamMap.get("Amount"), "356");
		transaction.setAmount(amount);

		transaction.setStatus(resParamMap.get("Status"));
		//added by RR for SBI ACQ_ID Fix
		transaction.setAcqId(resParamMap.get("sbi_ref_no"));
		transaction.setResponseMessage(resParamMap.get("PG_RESPONSE_MESSAGE"));
		return transaction;
	}// toTransaction()
	
}
