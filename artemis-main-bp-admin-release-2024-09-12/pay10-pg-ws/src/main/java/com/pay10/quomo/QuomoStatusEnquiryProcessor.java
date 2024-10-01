package com.pay10.quomo;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

import javax.xml.bind.DatatypeConverter;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.pay10.commons.util.FieldType;
import com.pay10.commons.util.Fields;

@Service
public class QuomoStatusEnquiryProcessor {
	private static Logger logger = LoggerFactory.getLogger(QuomoStatusEnquiryProcessor.class.getName());

	public void enquiryProcessor(Fields fields) {
		

	}

	public Transaction toTransaction(String jsonResponse, String txnType) {
		Transaction transaction = new Transaction();
		if (StringUtils.isBlank(jsonResponse)) {
			logger.info("toTransaction:: empty response from acquirer.");
			return transaction;
		}
		JSONObject respObj = new JSONObject(jsonResponse);
		logger.info("toTransaction:: respObj={}", respObj);
		transaction.setStatus(respObj.getString("order_status"));
		transaction.setPgRefNum(respObj.getString("order_id"));
		transaction.setMerchantId(respObj.getString("merchant_id"));
		transaction.setCurrency(respObj.getString("currency"));
		transaction.setSignData(respObj.getString("sign_data"));
		//transaction.setResponseCode(respObj.getString("resc"));
		transaction.setResponseMsg(respObj.getString("order_status"));
		transaction.setAmount(respObj.getString("deposit_amount"));
		transaction.setTxnId(respObj.getString("order_id"));
		transaction.setBankRefNum(respObj.getString("transaction_id"));
		return transaction;

	}

	public String statusEnquiryRequest(Fields fields, Transaction transactionResponse) throws NoSuchAlgorithmException {
		JSONObject jsonRequest = new JSONObject();
		jsonRequest.put("merchant_id", transactionResponse.getMerchantId());
		jsonRequest.put("order_id", fields.get(FieldType.PG_REF_NUM.getName()));

		StringBuilder reqParam = new StringBuilder();
		reqParam.append("APIKEYMD51800628664C1F0B6BBAF2");
		reqParam.append(transactionResponse.getMerchantId());
		//reqParam.append(fields.get(FieldType.PG_REF_NUM.getName()));
		reqParam.append(transactionResponse.getTxnId());
		logger.info("request prepaid for signData :: " + reqParam.toString());
		
		MessageDigest md = MessageDigest.getInstance("MD5");
		byte[] digest = md.digest(reqParam.toString().getBytes());
		String myHash = DatatypeConverter.printHexBinary(digest).toUpperCase();
		logger.info("Quomo,sign_data myHash :: " + myHash);
		jsonRequest.put("sign_data", myHash);
		return jsonRequest.toString();
	}

	public Transaction toTransactionForInquiry(String response, String string) {
		Transaction transaction = new Transaction();
		if (StringUtils.isBlank(response)) {
			logger.info("toTransaction:: empty response from acquirer.");
			return transaction;
		}
		JSONObject respObj = new JSONObject(response);
		logger.info("toTransaction:: respObj={}", respObj);
		transaction.setStatus(respObj.getString("order_status"));
		transaction.setPgRefNum(respObj.getString("order_id"));
		transaction.setMerchantId(respObj.getString("merchant_id"));
		transaction.setSignData(respObj.getString("sign_data"));
		transaction.setResponseMsg(respObj.getString("order_status"));
		transaction.setAmount(respObj.getString("transaction_amount"));
		transaction.setTxnId(respObj.getString("order_id"));
		transaction.setBankRefNum(respObj.getString("transaction_id"));
		return transaction;
	}

	public String getResponse(String request, String apiKey, String string, String string2) {
		// TODO Auto-generated method stub
		return null;
	}
	
	public String sendPostEnquiryRequest(String request) throws Exception {
		logger.info(" ############ sendPostEnquiryRequest ############ " + request);
		HttpPost post = new HttpPost("https://gateway.quomo.digital/checkfororderstatus");
		String resp = null;
		// add request parameter, form parameters
		List<NameValuePair> urlParameters = new ArrayList<>();
		urlParameters.add(new BasicNameValuePair("requestParams", request));
		post.setEntity(new UrlEncodedFormEntity(urlParameters));
		post.setHeader("Authorization", "Basic " + Base64.getEncoder()
				.encodeToString(("lmp@gateway.quomo.digital:lmp@pa$$ap!^wmDB39zt6grvP$dHXCB").getBytes()));

		try (CloseableHttpClient httpClient = HttpClients.createDefault();
				CloseableHttpResponse response = httpClient.execute(post)) {

			resp = EntityUtils.toString(response.getEntity());
		}
		return resp;
	}
}
