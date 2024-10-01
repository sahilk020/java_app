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

public class QuomoTransactionConverter1 {

	public static String privateKeyFilePath = "D://bpGateKey//PRIVATEKEY.txt";

	public static Map<String, String> jsonToMap(JSONObject json) {
		Map<String, String> map = new TreeMap<>();
		for (String key : json.keySet()) {
			map.put(key, (String) json.get(key).toString());
		}
		return map;
	}

	public static String getRestApi(JSONObject request) throws Exception {

		String response = "";

		System.out.println(" data       " + request);

		URL url = new URL("https://gateway.quomo.digital/Payapi_Index_TransdfUser.html");
		HttpURLConnection connection = (HttpURLConnection) url.openConnection();
		connection.setRequestMethod("POST");
		connection.setRequestProperty("Content-Type", "application/json");
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
		System.out.println("statusCode" + statusCode);

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
		System.out.println("response" + response);

		br.close();
		return response;
	}

	public static void main(String[] args) throws JSONException, Exception {
		JSONArray jsonArray = new JSONArray();

		DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
		LocalDateTime now = LocalDateTime.now();
		JSONObject paymentObject = new JSONObject();
//		paymentObject.put("accountNum", "0919891002");
//		paymentObject.put("accountName", "abhishek");
//
//		paymentObject.put("merchantId", "35540466");
//
//		paymentObject.put("requestTime", "1707726727");
//		paymentObject.put("merchantTransactionId", "1152808115852");
//		paymentObject.put("callback", "https://bpgate.nxtpay.in/pgws/quomo/verifyTxncallbackResponse");
//		paymentObject.put("bankName", "BBL");
//		paymentObject.put("bankProv", "NOIDA");
//		paymentObject.put("currencyCode", "THB");
//		paymentObject.put("bankCity", "NOIDA");		
//		paymentObject.put("transactionAmount", 100);
//
//		
		paymentObject.put("merchantId", "35540466");
		paymentObject.put("merchantTransactionId", "310120121");
		paymentObject.put("currencyCode", "THB");
		paymentObject.put("accountName", "abhishek. tiwari");
		paymentObject.put("accountNum", "0919891002");
		paymentObject.put("transactionAmount", "100");
		paymentObject.put("bankName", "BBL");
		paymentObject.put("bankProv", "NOIDA");
		paymentObject.put("bankCity", "NOIDA");
		paymentObject.put("requestTime",dtf.format(now) );
		paymentObject.put("callback", "https://bpgate.nxtpay.in/pgws/quomo/verifyTxncallbackResponse");
		paymentObject.put("signData", encryptRequest(jsonToMap(paymentObject)));

		System.out.println(paymentObject.toString());
		getRestApi(paymentObject);
	}

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

}
