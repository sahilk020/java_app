package com.pay10.kotak;

import java.util.HashMap;
import java.util.Map;

import org.codehaus.jackson.map.ObjectMapper;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.google.gson.Gson;
import com.pay10.commons.exception.SystemException;
import com.pay10.commons.util.FieldType;
import com.pay10.commons.util.Fields;
import com.pay10.commons.util.PropertiesManager;
import com.pay10.pg.core.util.KotakCardCheckSum;

@Service
public class KotakCardStatusEnquiryProcessor {

	private static Logger logger = LoggerFactory.getLogger(KotakCardStatusEnquiryProcessor.class.getName());

	@Qualifier("kotakTransactionConverter")
	@Autowired
	TransactionConverter transactionConverter;

	@Autowired
	KotakCardCheckSum kotakCardCheckSum;

	@Autowired
	private KotakTransformer kotakTransformer;

	public void enquiryProcessor(Fields fields) {

		try {
			String response = statusEnquiryRequest(fields);
			JSONObject statusResponse = new JSONObject(response);

			String status = (String) statusResponse.get("ResponseCode");
			String pgref = (String) statusResponse.get("OrderId");
			String amount = (String) statusResponse.get("Amount");
			String responseMessage = (String) statusResponse.get("ResponseMessage");
			logger.info("Status enquiry for kotak card "+status+"   "+pgref+"   "+amount+"   "+responseMessage);

			if (status != null && status.equalsIgnoreCase("00")
					&& fields.get(FieldType.ORDER_ID.getName()).equalsIgnoreCase(pgref)
					&& ("Transaction Successful").equalsIgnoreCase(responseMessage)
					&& amount.equalsIgnoreCase(fields.get(FieldType.TOTAL_AMOUNT.getName()))) {
				updateFields(fields, response);
			}
		} catch (SystemException exception) {
			logger.error("Exception", exception);
		} catch (Exception e) {
			logger.error("Exception in decrypting status enquiry response for payu ", e);
		}

	}

	@SuppressWarnings("unchecked")
	public String statusEnquiryRequest(Fields fields) throws Exception {
		String statusEnquiry = PropertiesManager.propertiesMap.get("KOTAKEnquiryUrl");

		JSONObject request = new JSONObject();
		request.put("BankId", fields.get(FieldType.ADF2.getName()));//
		request.put("MerchantId", fields.get(FieldType.MERCHANT_ID.getName()));
		request.put("TerminalId", fields.get(FieldType.ADF3.getName()));
		request.put("OrderId", fields.get(FieldType.ORDER_ID.getName()));
		request.put("AccessCode", fields.get(FieldType.ADF4.getName()));//
		request.put("TxnType", "Status");
		HashMap<Object, Object> result = new ObjectMapper().readValue(request.toString(), HashMap.class);

		String hash = kotakCardCheckSum.getHashValue(result, fields.get(FieldType.ADF1.getName()));

		request.put("SecureHash", hash);

		logger.info("Dual verification request "+new Gson().toJson(request));
		String encryptedString = kotakCardCheckSum.encrypt(request.toString(), fields.get(FieldType.TXN_KEY.getName()));
		JSONObject mainrequest = new JSONObject();
		mainrequest.put("bankId", fields.get(FieldType.ADF2.getName()));//
		mainrequest.put("merchantId", fields.get(FieldType.MERCHANT_ID.getName()));
		mainrequest.put("terminalId", fields.get(FieldType.ADF3.getName()));
		mainrequest.put("orderId", fields.get(FieldType.ORDER_ID.getName()));
		mainrequest.put("encData", encryptedString);
		logger.info("Dual verification request " + new Gson().toJson(mainrequest) + statusEnquiry);

		Map<String, String> data = transactionConverter.callRestApi(mainrequest.toString(), statusEnquiry);
		logger.info("Dual verification response" + data);

		String response = kotakCardCheckSum.decrypt(data.get("encData"), fields.get(FieldType.TXN_KEY.getName()));

		logger.info("Dual verification response decdata" + response);

		return response;

	}

	public Transaction toTransaction(String response) {
		Transaction transaction = new Transaction();

		JSONObject finalResponse = new JSONObject(response);

		transaction.setResponseMessage((String) finalResponse.get("ResponseMessage"));
		transaction.setResponseCode((String) finalResponse.get("ResponseCode"));
		transaction.setAmount((String) finalResponse.get("Amount"));
		transaction.setAcqId((String) finalResponse.get("RetRefNo"));
		transaction.setMerchantReference((String) finalResponse.get("OrderId"));
		logger.info("transcation " + transaction.toString());
		return transaction;

	}

	public void updateFields(Fields fields, String jsonResponse) {
		Transaction transactionResponse = new Transaction();
		transactionResponse = toTransaction(jsonResponse);

		kotakTransformer = new KotakTransformer(transactionResponse);
		kotakTransformer.updateresponseKotakCard(fields);
	}
}
