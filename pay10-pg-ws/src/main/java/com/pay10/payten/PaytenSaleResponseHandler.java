package com.pay10.payten;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.MapType;
import com.pay10.commons.exception.ErrorType;
import com.pay10.commons.exception.SystemException;
import com.pay10.commons.user.Account;
import com.pay10.commons.user.AccountCurrency;
import com.pay10.commons.user.User;
import com.pay10.commons.user.UserDao;
import com.pay10.commons.util.ChecksumUtils;
import com.pay10.commons.util.FieldType;
import com.pay10.commons.util.Fields;
import com.pay10.commons.util.PropertiesManager;
import com.pay10.commons.util.StatusType;
import com.pay10.commons.util.TransactionManager;
import com.pay10.commons.util.Validator;
import com.pay10.pg.core.util.ProcessManager;
import com.pay10.pg.core.util.Processor;

@Service
public class PaytenSaleResponseHandler {

	private static Logger logger = LoggerFactory.getLogger(PaytenSaleResponseHandler.class.getName());
//
//	@Autowired
//	@Qualifier("s2SStatusService")
//	private S2SStatusService s2SStatusService;

	@Autowired
	private Validator generalValidator;

	@Autowired
	private UserDao userDao;

	@Autowired
	@Qualifier("updateProcessor")
	private Processor updateProcessor;

	public Map<String, String> process(Fields fields) throws SystemException {

		String newTxnId = TransactionManager.getNewTransactionId();
		fields.put(FieldType.TXN_ID.getName(), newTxnId);
//		generalValidator.validate(fields);
		Transaction transactionResponse = new Transaction();
		String response = fields.get(FieldType.PAY10_FINAL_RESPONSE.getName());
		transactionResponse = toTransaction(response, fields);
		logger.info("Transaction Response For Payten : OrderId :" + fields.get(FieldType.ORDER_ID.getName())
				+ " Txn Response : " + transactionResponse.toString());
		if (StringUtils.equalsIgnoreCase(transactionResponse.getResponseCode(), ErrorType.PENDING.getResponseCode())
				|| StringUtils.equalsIgnoreCase(transactionResponse.getStatus(), StatusType.SENT_TO_BANK.getName())
				|| StringUtils.equalsIgnoreCase(transactionResponse.getStatus(), StatusType.DENIED.getName())) {
			logger.info("Payten Sale Response Received For Pending ::  pgRefNo={}",
					fields.get(FieldType.PG_REF_NUM.getName()));
			fields.put(FieldType.STATUS.getName(), StatusType.SENT_TO_BANK.getName());
			fields.put(FieldType.RESPONSE_CODE.getName(), ErrorType.PENDING.getResponseCode());
			fields.put(FieldType.RESPONSE_MESSAGE.getName(), ErrorType.PENDING.getResponseMessage());
			fields.put(FieldType.INTERNAL_ORIG_TXN_TYPE.getName(), fields.get(FieldType.TXNTYPE.getName()));
			fields.put(FieldType.RRN.getName(), transactionResponse.getAcqId());
			fields.put(FieldType.ACQ_ID.getName(), transactionResponse.getAcqId());
			fields.put(FieldType.PG_RESP_CODE.getName(), ErrorType.PENDING.getResponseCode());
			fields.put(FieldType.PG_TXN_STATUS.getName(), StatusType.SENT_TO_BANK.getName());
			fields.put(FieldType.PG_TXN_MESSAGE.getName(), ErrorType.PENDING.getResponseMessage());

			fields.remove(FieldType.PAY10_FINAL_RESPONSE.getName());
			return fields.getFields();
		}

		boolean doubleVer = doubleVerification(transactionResponse, fields);
		logger.info("process:: doubleVerification={}, pgRefNo={} ", doubleVer,
				fields.get(FieldType.PG_REF_NUM.getName()));
		if (doubleVer) {

			PaytenTransformer paytenTransformer = new PaytenTransformer(transactionResponse);
			paytenTransformer.updateResponse(fields);
		} else {
			fields.put(FieldType.STATUS.getName(), StatusType.DENIED.getName());
			fields.put(FieldType.RESPONSE_CODE.getName(), ErrorType.SIGNATURE_MISMATCH.getCode());
			fields.put(FieldType.RESPONSE_MESSAGE.getName(), ErrorType.SIGNATURE_MISMATCH.getResponseMessage());
		}

		fields.put(FieldType.INTERNAL_ORIG_TXN_TYPE.getName(), fields.get(FieldType.TXNTYPE.getName()));
		fields.remove(FieldType.PAY10_FINAL_RESPONSE.getName());
		ProcessManager.flow(updateProcessor, fields, true);
		return fields.getFields();

	}

	private boolean doubleVerification(Transaction transactionResponse, Fields fields) throws SystemException {

		if (!(transactionResponse.getStatus().equalsIgnoreCase("Captured")
				&& transactionResponse.getResponseCode().equalsIgnoreCase("000"))) {
			return true;

		}

		Map<String, String> keyMap = getTxnKey(fields);
		fields.put(FieldType.ADF1.getName(), keyMap.get("ADF1"));
		fields.put(FieldType.TXN_KEY.getName(), keyMap.get("TXN_KEY"));
		fields.put(FieldType.MERCHANT_ID.getName(), keyMap.get("MerchantId"));

		Map<String, String> response = getVpaStatus(fields);
		Fields fields1 = new Fields(response);

		logger.info("dual verfication response for pay10" + response);
		if (fields1.get(FieldType.STATUS.getName()).equalsIgnoreCase("Captured") && fields1
				.get(FieldType.AMOUNT.getName()).equalsIgnoreCase(fields.get(FieldType.TOTAL_AMOUNT.getName()))) {
			return true;

		} else {

			return false;
		}
	}

	public Map<String, String> getVpaStatus(Fields fields) throws SystemException {
		String responseBody = "";
		String serviceUrl = PropertiesManager.propertiesMap.get("PAY10StatusEnqUrl");
		Map<String, String> resMap = new HashMap<String, String>();
		Map<String, String> decryptedResMap = new HashMap<String, String>();
		try {

			JSONObject json = new JSONObject();
			json.put("PAY_ID", fields.get(FieldType.MERCHANT_ID.getName()));
			json.put("ORDER_ID", fields.get(FieldType.ORDER_ID.getName()));
			json.put("AMOUNT", fields.get(FieldType.TOTAL_AMOUNT.getName()));
			json.put("TXNTYPE", "STATUS");
			json.put("CURRENCY_CODE", "356");
			json.put("IS_MERCHANT_HOSTED", "Y");
			json.put("HASH", hashForStatus(fields));
			logger.info("STATUS Call  To PAYTEN and request dual Verfication : " + json.toString());
			CloseableHttpClient httpClient = HttpClientBuilder.create().build();
			HttpPost request = new HttpPost(serviceUrl);
			StringEntity params = new StringEntity(json.toString());
			request.addHeader("content-type", "application/json");
			request.setEntity(params);
			HttpResponse resp = httpClient.execute(request);
			responseBody = EntityUtils.toString(resp.getEntity());
			logger.info(
					"STATUS Call Response from PAYTEN STATUS Call To PAYTEN and dual Verfication  : " + responseBody);

			final ObjectMapper mapper = new ObjectMapper();
			final MapType type = mapper.getTypeFactory().constructMapType(Map.class, String.class, Object.class);
			resMap = mapper.readValue(responseBody, type);

		} catch (Exception exception) {
			exception.printStackTrace();
			logger.error("exception is " + exception);
			throw new SystemException(ErrorType.INTERNAL_SYSTEM_ERROR, "Internal Error");
		}
		return resMap;
	}

	public String hashForStatus(Fields fields) {
		try {
			Map<String, String> parameters = new LinkedHashMap<String, String>();
			parameters.put("PAY_ID", fields.get(FieldType.MERCHANT_ID.getName()));
			parameters.put("ORDER_ID", fields.get(FieldType.ORDER_ID.getName()));
			parameters.put("AMOUNT", fields.get(FieldType.TOTAL_AMOUNT.getName()));
			parameters.put("TXNTYPE", "STATUS");
			parameters.put("CURRENCY_CODE", "356");
			String data = ChecksumUtils.getString(parameters);

			logger.info("----- data " + data);
			String hash = ChecksumUtils.generateCheckSum(parameters, fields.get(FieldType.TXN_KEY.getName()));
			return hash;
		} catch (Exception e) {
			logger.error("Exception ", e);
			return null;
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
				if (accountThis.getAcquirerName()
						.equalsIgnoreCase(fields.get(FieldType.INTERNAL_ACQUIRER_TYPE.getName()))) {
					account = accountThis;
					break;
				}
			}
		}

		AccountCurrency accountCurrency = account.getAccountCurrency(fields.get(FieldType.CURRENCY_CODE.getName()));

		keyMap.put("ADF1", accountCurrency.getAdf1());
		keyMap.put("MerchantId", accountCurrency.getMerchantId());
		keyMap.put("TXN_KEY", accountCurrency.getTxnKey());

		return keyMap;

	}

	public Transaction toTransaction(String response, Fields fields) {

		Transaction transaction = new Transaction();

		String respSplit[] = response.split("~");

		for (String data : respSplit) {

			String dataSplit[] = data.split("=");

			String key = dataSplit[0];
			String value = dataSplit[1];

			if (key.equalsIgnoreCase("RESPONSE_CODE")) {
				transaction.setResponseCode(value);
			}

			else if (key.equalsIgnoreCase("STATUS")) {
				transaction.setStatus(value);
			}

//			else if (key.equalsIgnoreCase("ACQ_ID")) {
//				transaction.setAcqId(value);
//			}

			else if (key.equalsIgnoreCase("RRN")) {
				transaction.setRrn(value);
			}

			else if (key.equalsIgnoreCase("PG_REF_NUM")) {
				transaction.setAcqId(value);
			}

			else if (key.equalsIgnoreCase("PG_TXN_MESSAGE")) {
				transaction.setPgTxnMsg(value);
			}

			else if (key.equalsIgnoreCase("ORDER_ID")) {
				transaction.setOrderId(value);
			}

		}

		return transaction;

	}// toTransaction()

}
