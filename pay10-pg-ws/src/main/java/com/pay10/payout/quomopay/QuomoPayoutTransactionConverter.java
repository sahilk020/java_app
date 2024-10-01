package com.pay10.payout.quomopay;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.Signature;
import java.security.spec.PKCS8EncodedKeySpec;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.pay10.commons.user.AccountCurrencyPayout;
import com.pay10.commons.user.AccountCurrencyPayoutDao;
import com.pay10.commons.user.MultCurrencyCodeDao;
import com.pay10.commons.user.PayoutBankCodeconfigurationDao;
import com.pay10.commons.util.FieldType;
import com.pay10.commons.util.Fields;

@Service
public class QuomoPayoutTransactionConverter {
	private static Logger logger = LoggerFactory.getLogger(QuomoPayoutTransactionConverter.class.getName());

	public static String privateKeyFilePath = "D://bpGate1Key//PRIVATEKEY.txt";

	@Autowired
	AccountCurrencyPayoutDao accountCurrencyPayoutDao;
	
	

	String merchantId = "35540466";
	String url = "https://bpgate.nxtpay.in/pgws/quomo/verifyTxncallbackResponse";
	String adf2 = "\"https://gateway.quomo.digital/Payapi_Index_TransdfUser.html\"";
	String adf3 = "https://gateway.quomo.digital/Payapi_Index_PBalance.html";

	@Autowired
	MultCurrencyCodeDao multCurrencyCodeDao;

	public JSONObject getTransferRequest(Fields fields, AccountCurrencyPayout accountCurrencyRequest) {
		DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
		LocalDateTime now = LocalDateTime.now();
		JSONObject paymentObject = new JSONObject();
		paymentObject.put("merchantId", accountCurrencyRequest.getMerchantId());
		paymentObject.put("merchantTransactionId", fields.get(FieldType.PG_REF_NUM.getName()));
		paymentObject.put("currencyCode",
				multCurrencyCodeDao.getCurrencyNamebyCode(fields.get(FieldType.CURRENCY_CODE.getName())));
		paymentObject.put("accountName", fields.get(FieldType.CUST_NAME.getName()));
		paymentObject.put("accountNum", fields.get(FieldType.ACC_NO.getName()));
		paymentObject.put("transactionAmount", fields.get(FieldType.AMOUNT.getName()));
		paymentObject.put("bankName", fields.get(FieldType.BANK_NAME.getName()));
		paymentObject.put("bankProv", fields.get(FieldType.ACC_PROVINCE.getName()));
		paymentObject.put("bankCity", fields.get(FieldType.BANK_BRANCH.getName()));
		paymentObject.put("requestTime", dtf.format(now));
		paymentObject.put("callback", accountCurrencyRequest.getAdf1());

		
		try {
			paymentObject.put("signData", encryptRequest(jsonToMap(paymentObject)));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		logger.info("Money Transfer " + paymentObject.toString());
		return paymentObject;
	}

	public static Map<String, String> jsonToMap(JSONObject json) {
		Map<String, String> map = new TreeMap<>();
		for (String key : json.keySet()) {
			map.put(key, (String) json.get(key));
		}
		return map;
	}


	

	public static String getRestApi(JSONObject request, Fields fields, String requestUrl) throws Exception {

		String response = "";

		logger.info("request" + request + "   url    " + requestUrl);

		URL url = new URL(requestUrl);
		HttpURLConnection connection = (HttpURLConnection) url.openConnection();
		connection.setRequestMethod("POST");
		connection.setRequestProperty("Content-Type", "application/json");
		connection.setRequestProperty("cache-control", "no-cache");

        connection.setRequestProperty("User-Agent", "YourUserAgent/1.0");

		connection.setUseCaches(false);
		connection.setDoInput(true);
		connection.setDoOutput(true);
		connection.setConnectTimeout(60000);
		connection.setReadTimeout(60000);

		// Send request
		DataOutputStream wr = new DataOutputStream(connection.getOutputStream());
		wr.writeBytes(request.toString());
		wr.flush();
		wr.close();

		BufferedReader br = null;

		int statusCode = connection.getResponseCode();
		logger.info("statusCode" + statusCode);

		if (statusCode == 200) {
			br = new BufferedReader(new InputStreamReader(connection.getInputStream()));
		} else {
			br = new BufferedReader(new InputStreamReader(connection.getErrorStream()));
		}
		// Get Response

		String decodedString;

		while ((decodedString = br.readLine()) != null) {
			response = response + decodedString;
		}
		logger.info("response" + response);

		br.close();
		return response;
	}

	

//	public static void main(String[] args) throws JSONException, Exception {
//
//		DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
//		LocalDateTime now = LocalDateTime.now();
//		JSONObject paymentObject = new JSONObject();
//		paymentObject.put("merchantId", "35540466");
//	
//		paymentObject.put("requestTime", dtf.format(now));
//
//		paymentObject.put("signData", encryptRequest(jsonToMap(paymentObject)));
//
//		System.out.println(paymentObject.toString());
//		getRestApi(paymentObject, null);
//	}

	public static String encryptRequest(Map<String, String> requestData) throws Exception {

		String privateKey = readPrivateKey(privateKeyFilePath);
		String preSignString = signMsg(requestData);
		String signData = rsaSign(preSignString, privateKey);

		return signData;

	}

	private static String readPrivateKey(String filePath) throws Exception {
		Path path = Paths.get(filePath);
		byte[] privateKeyBytes = Files.readAllBytes(path);
		return new String(privateKeyBytes, StandardCharsets.UTF_8);
	}

	private static String signMsg(Map<String, String> data) {
		StringBuilder msg = new StringBuilder();
		int i = 0;
		for (Map.Entry<String, String> entry : data.entrySet()) {
			if (i == 0) {
				msg.append(entry.getKey()).append("=").append(entry.getValue());
			} else {
				msg.append("&").append(entry.getKey()).append("=").append(entry.getValue());
			}
			i++;
		}

		System.out.println(msg);
		return msg.toString();
	}

	static String rsaSign(String data, String privateKey) throws Exception {
		privateKey = privateKey.replace("-----BEGIN PRIVATE KEY-----", "").replace("-----END PRIVATE KEY-----", "")
				.replaceAll("\\s", "");

		byte[] keyBytes = Base64.getDecoder().decode(privateKey);
		PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(keyBytes);
		KeyFactory keyFactory = KeyFactory.getInstance("RSA");
		PrivateKey privateKeyObj = keyFactory.generatePrivate(keySpec);

		Signature signature = Signature.getInstance("SHA256withRSA");
		signature.initSign(privateKeyObj);
		signature.update(data.getBytes("UTF-8"));
		byte[] signBytes = signature.sign();

		return Base64.getEncoder().encodeToString(signBytes);
	}

	public JSONObject checkBlance(Fields fields, AccountCurrencyPayout accountCurrencyRequest) {

		try {
			DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
			LocalDateTime now = LocalDateTime.now();
			JSONObject paymentObject = new JSONObject();
			paymentObject.put("merchantId", accountCurrencyRequest.getMerchantId());

			paymentObject.put("requestTime", dtf.format(now));

			paymentObject.put("signData", encryptRequest(jsonToMap(paymentObject)));

			logger.info(paymentObject.toString());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return null;
	}

}
