package com.pay10.cosmos;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.pay10.commons.exception.ErrorType;
import com.pay10.commons.exception.SystemException;
import com.pay10.commons.user.Account;
import com.pay10.commons.user.AccountCurrency;
import com.pay10.commons.user.User;
import com.pay10.commons.user.UserDao;
import com.pay10.commons.util.AcquirerType;
import com.pay10.commons.util.Amount;
import com.pay10.commons.util.Constants;
import com.pay10.commons.util.FieldType;
import com.pay10.commons.util.Fields;
import com.pay10.commons.util.PropertiesManager;
import com.pay10.commons.util.StatusType;
import com.pay10.commons.util.TransactionManager;
import com.pay10.commons.util.Validator;
import com.pay10.federalNB.FederalBankNBTransformer;
import com.pay10.pg.core.util.FederalNBChecksumUtil;
import com.pay10.pg.core.util.ProcessManager;
import com.pay10.pg.core.util.Processor;

@Service
public class CosmosUpiintentResponseHandler {

	private static Logger logger = LoggerFactory.getLogger(CosmosUpiintentResponseHandler.class.getName());

	@Autowired
	private Validator generalValidator;

	@Autowired
	@Qualifier("updateProcessor")
	private Processor updateProcessor;

	@Autowired
	private UserDao userDao;

	@Autowired
	CosmosProcessor cosmosProcessor;


		
	public static final String PIPE = "|";
	public static final String DOUBLE_PIPE = "||";

	public Map<String, String> process(Fields fields) throws SystemException {

		String newTxnId = TransactionManager.getNewTransactionId();
		fields.put(FieldType.TXN_ID.getName(), newTxnId);
		logger.info("fieldsin response action -------->>>"+fields.getFieldsAsString());
		//generalValidator.validate(fields);
		Transaction transactionResponse = new Transaction();
		logger.info("fieldsin response action -------->>>"+fields.getFieldsAsString());

		String pipedResponse = fields.get(FieldType.COSMOS_UPI_RESPONSE_FIELD.getName());
		transactionResponse = toTransaction(pipedResponse, transactionResponse);
		logger.info("fieldsin response action -------->>>"+fields.getFieldsAsString());

		boolean doubleVer = doubleVerification(transactionResponse, fields);
		logger.info("process:: doubleVerification={}, pgRefNo={} ", doubleVer,
				fields.get(FieldType.PG_REF_NUM.getName()));
		if ( doubleVer ) {

			CosmosUpiTransformer	cosmosUpiTransformer = new CosmosUpiTransformer(transactionResponse);
			cosmosUpiTransformer.updateResponse(fields);
		} else {
			fields.put(FieldType.STATUS.getName(), StatusType.DENIED.getName());
			fields.put(FieldType.RESPONSE_CODE.getName(), ErrorType.SIGNATURE_MISMATCH.getCode());
			fields.put(FieldType.RESPONSE_MESSAGE.getName(), ErrorType.SIGNATURE_MISMATCH.getResponseMessage());
		}

		// transactionResponse = toTransaction(pipedResponse);

		// federalBankTransformer = new FederalBankNBTransformer(transactionResponse);
		// federalBankTransformer.updateResponse(fields);

		fields.put(FieldType.INTERNAL_ORIG_TXN_TYPE.getName(), fields.get(FieldType.TXNTYPE.getName()));
		fields.remove(FieldType.COSMOS_UPI_RESPONSE_FIELD.getName());
		ProcessManager.flow(updateProcessor, fields, true);
		return fields.getFields();

	}

	private boolean doubleVerification(Transaction transactionResponse, Fields fields) {

		try {

			if (!transactionResponse.getStatus().equalsIgnoreCase("SUCCESS")) {
				return true;
			}

			Map<String, String> keyMap = getTxnKey(fields);
			fields.put(FieldType.ADF1.getName(), keyMap.get("ADF1"));
			fields.put(FieldType.ADF2.getName(), keyMap.get("ADF2"));
			fields.put(FieldType.ADF4.getName(), keyMap.get("ADF4"));
			fields.put(FieldType.TXN_KEY.getName(), keyMap.get("txnKey"));
			fields.put(FieldType.MERCHANT_ID.getName(), keyMap.get("MerchantId"));
			logger.info("fieldsin response action -------->>>"+fields.getFieldsAsString());
			String internalRequestFields=fields.get(FieldType.INTERNAL_REQUEST_FIELDS.getName());
			String[] paramaters = internalRequestFields.split("~");
			Map<String, String> paramMap = new HashMap<String, String>();
			for (String param : paramaters) {
				String[] parameterPair = param.split("=");
				if (parameterPair.length > 1) {
					paramMap.put(parameterPair[0].trim(), parameterPair[1].trim());
				}
			}
			
			 if (StringUtils.isNotEmpty(paramMap.get(FieldType.UPI_INTENT.getName())) && paramMap.get(FieldType.UPI_INTENT.getName()).equalsIgnoreCase("1")){
				 fields.put(FieldType.UPI_INTENT.getName(),paramMap.get(FieldType.UPI_INTENT.getName()));
				
				
			 }
			JSONObject dualResponseJson= cosmosProcessor.transactionStatus(fields);
			logger.info("fieldsin response action -------->>>"+fields.getFieldsAsString());

			logger.info( "cosmos upi intent   Double verification Response = "
					+ dualResponseJson);
			  JSONArray data = dualResponseJson.getJSONArray("data");
		        String amount = data.getJSONObject(0).getString("amount");
		        String respMessage = data.getJSONObject(0).getString("respMessge");
		        String extTransactionId = data.getJSONObject(0).getString("extTransactionId");
		        
			String toamount = Amount.toDecimal(fields.get(FieldType.TOTAL_AMOUNT.getName()),
					fields.get(FieldType.CURRENCY_CODE.getName()));
			logger.info("doubleVerification::  cosmos upi response. Status={},  resAmount={},pgRefNo={}",
					respMessage, amount,
					extTransactionId.substring(9));

			if (respMessage != null
					&& respMessage.equalsIgnoreCase("SUCCESS")
					&& fields.get(FieldType.PG_REF_NUM.getName()).equalsIgnoreCase(extTransactionId.substring(9))
					&& toamount.equalsIgnoreCase(amount)) {

				return true;
			}
			logger.info("doubleVerification:: failed. Status={},  resAmount={},pgRefNo={}",
					respMessage, amount,
					extTransactionId.substring(9));

			return false;
		}

		catch (Exception e) {
			logger.error("Exceptionn ", e);
			return false;
		}

	}
String s=null;
String s1="";
//	{"merchant_vpa":"bhartipay@cosb","remark":"upiPayment",
//		"errCode":"U67","status":"FAILURE","merchant":["qr.bhar"],"extTransactionId":"1319121130180535","customerName":"KATKAM PALLAVI ASHOK","responseTime":"Wed Nov 30 18:25:59 IST 2022",
//		"customer_vpa":"7838304007@cosb","rrn":"233418240004","txnId":"COBQRVA96H6ARW3AZ3ZVEOPHE49E86TN4D2"
//			,"checksum":"","amount":"500.00"}
	public Transaction toTransaction(String response, Transaction transactionResponse) {
		JSONObject responseObj = new JSONObject(response);
		if (!responseObj.getString("extTransactionId").isEmpty()) {
		transactionResponse.setReferenceId(responseObj.getString("extTransactionId"));
		}
		if (!responseObj.getString("customer_vpa").isEmpty()) {

		transactionResponse.setMerchantVPA(responseObj.getString("customer_vpa"));
		}
		if (!responseObj.getString("status").isEmpty()) {

		transactionResponse.setStatus(responseObj.getString("status"));
		}
		if (!responseObj.get("errCode").toString().equalsIgnoreCase("null")
				&& !responseObj.get("errCode").toString().isEmpty()) {

		transactionResponse.setAuth_code(responseObj.get("errCode")+"");
		}
		if (!responseObj.getString("rrn").isEmpty()) {

		transactionResponse.setRrn(responseObj.getString("rrn"));
		}
		if (!responseObj.getString("txnId").isEmpty()) {

			transactionResponse.setAcq_id(responseObj.getString("txnId"));
			}
		return transactionResponse;
	}

	public Map<String, String> getTxnKey(Fields fields) throws SystemException {

		Map<String, String> keyMap = new HashMap<String, String>();

		User user = userDao.findPayId(fields.get(FieldType.PAY_ID.getName()));
		Account account = null;
		Set<Account> accounts = user.getAccounts();

		if (accounts == null || accounts.size() == 0) {
			logger.info("No account found for Pay ID = " + fields.get(FieldType.PAY_ID.getName()) + " and ORDER ID = "
					+ fields.get(FieldType.ORDER_ID.getName()));
		} else {
			for (Account accountThis : accounts) {
				if (accountThis.getAcquirerName()
						.equalsIgnoreCase(AcquirerType.getInstancefromCode(AcquirerType.COSMOS.getCode()).getName())) {
					account = accountThis;
					break;
				}
			}
		}

		AccountCurrency accountCurrency = account.getAccountCurrency(fields.get(FieldType.CURRENCY_CODE.getName()));

		keyMap.put("ADF1", accountCurrency.getAdf1());
		keyMap.put("ADF2", accountCurrency.getAdf2());
		keyMap.put("ADF4", accountCurrency.getAdf4());
		keyMap.put("txnKey", accountCurrency.getTxnKey());
		keyMap.put("MerchantId", accountCurrency.getMerchantId());

		return keyMap;

	}
	
	
	
}
