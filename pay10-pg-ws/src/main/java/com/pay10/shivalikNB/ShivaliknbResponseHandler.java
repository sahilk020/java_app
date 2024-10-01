package com.pay10.shivalikNB;

import com.google.gson.JsonArray;
import com.pay10.axisbank.netbanking.Constants;
import com.pay10.canaraNBbank.CanaraNBProcessor;
import com.pay10.canaraNBbank.CanaraNBResponseHandler;
import com.pay10.commons.exception.ErrorType;
import com.pay10.commons.exception.SystemException;
import com.pay10.commons.user.Account;
import com.pay10.commons.user.AccountCurrency;
import com.pay10.commons.user.User;
import com.pay10.commons.user.UserDao;
import com.pay10.commons.util.*;
import com.pay10.errormapping.ErrorMappingDTO;
import com.pay10.errormapping.Impl.ErrorMappingDAOImpl;

import com.pay10.pg.core.util.CanaraUtil;
import com.pay10.pg.core.util.ProcessManager;
import com.pay10.pg.core.util.Processor;
import com.pay10.pg.core.util.ShivalikNBUtil;
import org.apache.commons.lang3.StringUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@Service
public class ShivaliknbResponseHandler {

	private static Logger logger = LoggerFactory.getLogger(ShivaliknbResponseHandler.class.getName());
	@Autowired
	ShivaliknbProcessor shivaliknbProcessor;
	@Autowired
	private Validator generalValidator;
	@Autowired
	private UserDao userDao;
	@Autowired
	@Qualifier("updateProcessor")
	private Processor updateProcessor;

	public Map<String, String> process(Fields fields) throws SystemException {
		String decryptedResponse = "";

		String newTxnId = TransactionManager.getNewTransactionId();
		fields.put(FieldType.TXN_ID.getName(), newTxnId);
		logger.info("SHIVALIK NB Response Handler1: " + fields.getFieldsAsString());

		Transaction transactionResponse = new Transaction();
		String pipedResponse = fields.get(FieldType.SHIVALIK_NB_RESPONSE_FIELD.getName());
		transactionResponse = toTransaction(pipedResponse);

		boolean doubleVer = doubleVerification(transactionResponse, fields);
		logger.info("process:: doubleVerification={}, pgRefNo={} ", doubleVer,
				fields.get(FieldType.PG_REF_NUM.getName()));
		if (doubleVer == true) {
			ShivalikBankNBTransformer shivalikBankNBTransformer = new ShivalikBankNBTransformer(transactionResponse);
			shivalikBankNBTransformer.updateResponse(fields);

		} else {
			fields.put(FieldType.STATUS.getName(), StatusType.DENIED.getName());
			fields.put(FieldType.RESPONSE_CODE.getName(), ErrorType.SIGNATURE_MISMATCH.getCode());
			fields.put(FieldType.RESPONSE_MESSAGE.getName(), ErrorType.SIGNATURE_MISMATCH.getResponseMessage());
		}

		fields.put(FieldType.INTERNAL_ORIG_TXN_TYPE.getName(), fields.get(FieldType.TXNTYPE.getName()));
		fields.remove(FieldType.SHIVALIK_NB_RESPONSE_FIELD.getName());
		ProcessManager.flow(updateProcessor, fields, true);
		return fields.getFields();
	}

	private boolean doubleVerification(Transaction transactionResponse, Fields fields) {

		try {
			if (!transactionResponse.getStatus().equalsIgnoreCase("0")) {
				return true;
			}

			Map<String, String> keyMap = getTxnKey(fields);
			fields.put(FieldType.ADF1.getName(), keyMap.get("ADF1"));
			fields.put(FieldType.ADF2.getName(), keyMap.get("ADF2"));
			fields.put(FieldType.ADF3.getName(), keyMap.get("ADF3"));
			fields.put(FieldType.ADF5.getName(), keyMap.get("ADF5"));

			fields.put(FieldType.ADF4.getName(), keyMap.get("ADF4"));
			fields.put(FieldType.MERCHANT_ID.getName(), keyMap.get("MerchantId"));
			logger.info("Field in doubleVerification request " + fields.getFieldsAsString());
			JSONArray dualVerificationResponse = statusEnquiry(fields, transactionResponse);

			logger.info("Response doubleVerification shivalik bank pgrefno={},amount={}",
					dualVerificationResponse.getJSONObject(0).getString("order_id"),
					dualVerificationResponse.getJSONObject(0).getString("response_code"));
			String toamount = Amount.toDecimal(fields.get(FieldType.TOTAL_AMOUNT.getName()),
					fields.get(FieldType.CURRENCY_CODE.getName()));

			if (dualVerificationResponse.getJSONObject(0).getString("order_id")
					.equalsIgnoreCase(fields.get(FieldType.PG_REF_NUM.getName()))
					&& dualVerificationResponse.getJSONObject(0).getString("response_code").equalsIgnoreCase("0")
					&& dualVerificationResponse.getJSONObject(0).getString("amount").equalsIgnoreCase(toamount)
					&& (dualVerificationResponse.getJSONObject(0).getString("response_message")
							.equalsIgnoreCase("Transaction successful"))) {

				return true;
			}
			logger.info("doubleVerification::shivalik failed.   resAmount={},pgRefNo={}", transactionResponse.getAmt(),
					transactionResponse.getPid());

			return false;
		}

		catch (Exception e) {
			logger.error("Exceptionn ", e);
			return false;
		}

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
				if (accountThis.getAcquirerName().equalsIgnoreCase(
						AcquirerType.getInstancefromCode(AcquirerType.SHIVALIKNBBANK.getCode()).getName())) {
					account = accountThis;
					break;
				}
			}
		}

		AccountCurrency accountCurrency = account.getAccountCurrency(fields.get(FieldType.CURRENCY_CODE.getName()));

		keyMap.put("ADF1", accountCurrency.getAdf1());
		keyMap.put("ADF2", accountCurrency.getAdf2());
		keyMap.put("ADF4", accountCurrency.getAdf4());
		keyMap.put("ADF5", accountCurrency.getAdf5());
		keyMap.put("ADF3", accountCurrency.getAdf3());
		keyMap.put("MerchantId", accountCurrency.getMerchantId());

		return keyMap;

	}

	public JSONArray statusEnquiry(Fields fields, Transaction transactionResponse) {

		String secretKey = fields.get(FieldType.ADF4.getName());
		// String secretKey = PropertiesManager.propertiesMap.get("secretKey");
		// String salt_key = PropertiesManager.propertiesMap.get("salt_key");
		String salt_key = fields.get(FieldType.ADF3.getName());
		String hash_data;
		JSONObject Statusrequest = new JSONObject();
		Statusrequest.put("api_key", fields.get(FieldType.ADF2.getName()));
		Statusrequest.put("bank_code", "SHIVN");
		Statusrequest.put("customer_phone", fields.get(FieldType.CUST_PHONE.getName()));
		Statusrequest.put("Customer_email", fields.get(FieldType.CUST_EMAIL.getName()));
		Statusrequest.put("customer_name", fields.get(FieldType.CUST_NAME.getName()));

		Statusrequest.put("order_id", fields.get(FieldType.PG_REF_NUM.getName()));
		Statusrequest.put("response_code", transactionResponse.getStatus());

		Statusrequest.put("transaction_id", transactionResponse.getAcqId());

		ArrayList<String> hashParam = new ArrayList<String>();

		hashParam.add(Statusrequest.getString("api_key"));
		hashParam.add(Statusrequest.getString("bank_code"));

		hashParam.add(Statusrequest.getString("order_id"));

		/*
		 * hash_data = Statusrequest.get("api_key") + "|" +
		 * Statusrequest.get("bank_code") + "|" + Statusrequest.get("customer_phone") +
		 * "|" + Statusrequest.get("Customer_email") + "|" +
		 * Statusrequest.get("customer_name") + "|" + Statusrequest.get("date_from") +
		 * "|" + Statusrequest.get("date_to") + "|" + Statusrequest.get("order_id") +
		 * "|" + Statusrequest.get("response_code");
		 */
		String generateHash_data = null;
		try {
			generateHash_data = shivaliknbProcessor.generateHashNew(hashParam, salt_key);
		} catch (Exception e) {
			e.printStackTrace();
		}

		Statusrequest.put("hash", generateHash_data);

		logger.info(Statusrequest.toString());
		String encryptedRequest = null;
		// encryptedRequest = ShivalikNBUtil.encrypt(Statusrequest.toString(),
		// secretKey);
		logger.info(encryptedRequest);

		StringBuilder request = new StringBuilder();
		request.append("api_key=");
		request.append(fields.get(FieldType.ADF2.getName()));
		request.append("&bank_code=SHIVN");

		request.append("&order_id=");
		request.append(fields.get(FieldType.PG_REF_NUM.getName()));

		request.append("&hash=");
		request.append(generateHash_data);

		String transactionENCResponse = null;
		transactionENCResponse = getResponse(request.toString());
		logger.info("SHIVALIK NB transactionENCResponse:{}", transactionENCResponse.toString());
		String transactionStatusResponse = ShivalikNBUtil.decrypt(transactionENCResponse, secretKey);
		logger.info("SHIVALIK NB transactionStatus  or dual verification Response : {},{}",
				fields.get(FieldType.ORDER_ID.getName()), transactionENCResponse);

		JSONObject statusResponseJson = new JSONObject(transactionENCResponse);

		JSONArray data = statusResponseJson.getJSONArray("data");

		return data;
	}

	public String getResponse(String request) {
		String response = "";
		try {
			String hostUrl = PropertiesManager.propertiesMap.get("SHIVALIKNBBANKStatusEnqUrl");
			hostUrl = hostUrl + "?" + request;
			logger.info("request of shivalik NB bank dual verification  " + request + "        url     " + hostUrl);
			HttpURLConnection connection = null;
			URL url;
			url = new URL(hostUrl);
			connection = (HttpURLConnection) url.openConnection();
			connection.setRequestMethod("POST");
			connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
			connection.setUseCaches(false);
			connection.setDoInput(true);
			connection.setDoOutput(true);
			connection.setConnectTimeout(60000);
			connection.setReadTimeout(60000);

			// Send request
			DataOutputStream wr = new DataOutputStream(connection.getOutputStream());
			wr.flush();
			wr.close();

			// Get Response
			InputStream is = connection.getInputStream();

			BufferedReader bufferedreader = new BufferedReader(new InputStreamReader(is));
			String decodedString;

			while ((decodedString = bufferedreader.readLine()) != null) {
				response = response + decodedString;
			}

			bufferedreader.close();

			logger.info("Response for dual verfication for shivalik bank >> " + response);

		} catch (Exception e) {
			logger.error("Exception in getting dual verfication for shivalik bank  ", e);
			return response;
		}
		return response;

	}

	public Transaction toTransaction(String pipedResponse) {

		JSONObject response = new JSONObject(pipedResponse);
		Transaction transaction = new Transaction();
		transaction.setAcqId(response.getString("transaction_id"));
		transaction.setAmount(response.getString("amount"));
		transaction.setPid(response.getString("order_id"));
		transaction.setStatus(response.getString("response_code"));
		logger.info("transaction for shivalik bank " + transaction.toString());

		return transaction;
	}

}
