package com.pay10.shivalikNB;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.pay10.commons.exception.SystemException;
import com.pay10.commons.util.Amount;
import com.pay10.commons.util.Currency;
import com.pay10.commons.util.FieldType;
import com.pay10.commons.util.Fields;
import com.pay10.commons.util.PropertiesManager;
import com.pay10.pg.core.util.ShivalikNBUtil;

@Service
public class ShivalikStatusEnquiryProcessor {
	private static Logger logger = LoggerFactory.getLogger(ShivalikStatusEnquiryProcessor.class.getName());

	@Autowired
	ShivaliknbProcessor shivaliknbProcessor;

	public void enquiryProcessor(Fields fields) throws SystemException {

		String response = "";

		JSONArray request = statusEnquiryRequest(fields);

		updateFields(fields, request);

	}

	private JSONArray statusEnquiryRequest(Fields fields) {

		String secretKey = fields.get(FieldType.ADF4.getName());

		String salt_key = fields.get(FieldType.ADF3.getName());
		String hash_data;
		JSONObject Statusrequest = new JSONObject();
		Statusrequest.put("api_key", fields.get(FieldType.ADF2.getName()));
		Statusrequest.put("bank_code", "SHIVN");

		Statusrequest.put("order_id", fields.get(FieldType.PG_REF_NUM.getName()));

		ArrayList<String> hashParam = new ArrayList<String>();

		hashParam.add(Statusrequest.getString("api_key"));
		hashParam.add(Statusrequest.getString("bank_code"));

		hashParam.add(Statusrequest.getString("order_id"));

		String generateHash_data = null;
		try {
			generateHash_data = shivaliknbProcessor.generateHashNew(hashParam, salt_key);
		} catch (Exception e) {
			e.printStackTrace();
		}

		logger.info(Statusrequest.toString());

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
		logger.info("SHIVALIK NB transactionStatus   Response : {},{}", fields.get(FieldType.ORDER_ID.getName()),
				transactionENCResponse);

		JSONObject statusResponseJson = new JSONObject(transactionENCResponse);
		logger.info("status enquiry response" + statusResponseJson.get("data"));

		JSONArray data = statusResponseJson.getJSONArray("data");

		return data;
	}

	public String getResponse(String request) {
		String response = "";
		try {
			String hostUrl = PropertiesManager.propertiesMap.get("SHIVALIKNBBANKStatusEnqUrl");
			hostUrl = hostUrl + "?" + request;
			logger.info("request of jammu and kishmir bank status enquiry  " + request + "        url     " + hostUrl);
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

			logger.info("Response for status enquiry for shivalik bank >> " + response);

		} catch (Exception e) {
			logger.error("Exception in getting status enquiry for shivalik bank  ", e);
			return response;
		}
		return response;

	}

	public void updateFields(Fields fields, JSONArray response) {

		Transaction transactionStatusResponse = toTransactionEnquiry(response);
		String amount = Amount.toDecimal(fields.get(FieldType.TOTAL_AMOUNT.getName()), "356");

		if (fields.get(FieldType.PG_REF_NUM.getName()).equalsIgnoreCase(transactionStatusResponse.getPid())
				&& amount.equalsIgnoreCase(transactionStatusResponse.getAmount())) {

			ShivalikBankNBTransformer shivalikBankNBTransformer = new ShivalikBankNBTransformer(
					transactionStatusResponse);
			shivalikBankNBTransformer.updateResponse(fields);
		}
	}

	public Transaction toTransactionEnquiry(JSONArray pipedResponse) {

		Transaction transaction = new Transaction();
		transaction.setAcqId(pipedResponse.getJSONObject(0).getString("transaction_id"));
		transaction.setAmount(pipedResponse.getJSONObject(0).getString("amount"));
		transaction.setPid(pipedResponse.getJSONObject(0).getString("order_id"));
		transaction.setStatus(pipedResponse.getJSONObject(0).getString("response_code"));
		transaction.setMessage(pipedResponse.getJSONObject(0).getString("response_message"));
		logger.info("transaction for shivalik bank " + transaction.toString());

		return transaction;
	}
}
