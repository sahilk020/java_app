package com.pay10.payout.quomopay;

import java.io.BufferedReader;
import java.io.FileReader;
import java.security.KeyFactory;
import java.security.PublicKey;
import java.security.Security;
import java.security.Signature;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import java.util.Map;

import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.json.JSONObject;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.google.api.client.json.Json;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

@RestController
public class QuomoCallbacksController {
	//public static String publicKeyFilePath = "C://Users//Chetan//Desktop//BP_GATE-Payout//PAYOUT//DOCS//QUOMO//PUBLICKEY.txt";
	public static String publicKeyFilePath ="C://Users//Chetan//Desktop//BP_GATE-Payout//PAYOUT//DOCS//QUOMO//PublicKey(verifycallback).txt";
	@PostMapping("/quomo/verifyTxncallbackResponse")
	public String verifyResponse(@RequestBody Map<String, String> response) {
		

		
		try {
			String responseCode = response.get("responseCode");
			String merchantId = response.get("merchantId");
			String merchantTransactionId = response.get("merchantTransactionId");
			String currencyCode = response.get("currencyCode");
			String accountName = response.get("accountName");
			String accountNum = response.get("accountNum");
			String transactionAmount = response.get("transactionAmount");
			String bankName = response.get("bankName");
			String transactionResult = response.get("transactionResult");
			String failedReason = response.get("failedReason");
			String completeTime = response.get("completeTime");
			String signData = response.get("signData");

			// Construct the data string for verification
			String dataForVerification = "responseCode=" + responseCode + "&merchantId=" + merchantId
					+ "&merchantTransactionId=" + merchantTransactionId + "&currencyCode=" + currencyCode
					+ "&accountName=" + accountName + "&accountNum=" + accountNum + "&transactionAmount="
					+ transactionAmount + "&bankName=" + bankName + "&transactionResult=" + transactionResult
					+ "&failedReason=" + failedReason + "&completeTime=" + completeTime;

			// Load public key from a certificate file (in PEM or DER format)

			// Verify the signature
			boolean verificationResult = verifySignUsingRSA(dataForVerification, signData);

			if (verificationResult) {
				// return "Signature verification successful!";

				JSONObject object = new JSONObject();
				object.put("merchantTransactionId", merchantTransactionId);
				object.put("merchantId", merchantId);
				object.put("received", "100");
				return new Gson().toJson(object).toString();
				// call update processor to update transaction data
			} else {
				return "Signature verification failed!";
			}
		} catch (Exception e) {
			e.printStackTrace();
			return "Error during signature verification.";
		}
	}

	static boolean verifySignUsingRSA(String requestData, String sign) throws Exception {
		Security.addProvider(new BouncyCastleProvider());
		// Merchant generates keys using OpenSSL and shares the public key with Quomo

		String quomoPublicKeyStr = readKeyFromFile(publicKeyFilePath);

		// Quomo verifies the signature using the merchant's public key
		boolean isVerified = rsaVerify(requestData, sign, quomoPublicKeyStr);

		System.out.println("Signature verified by Quomo: " + isVerified);
		return isVerified;
	}

	private static String readKeyFromFile(String filePath) throws Exception {
		StringBuilder keyBuilder = new StringBuilder();
		try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
			String line;
			while ((line = reader.readLine()) != null) {
				keyBuilder.append(line).append("\n");
			}
		}
		return keyBuilder.toString();
	}

	private static boolean rsaVerify(String data, String signature, String publicKey) throws Exception {
		publicKey = publicKey.replace("-----BEGIN PUBLIC KEY-----", "").replace("-----END PUBLIC KEY-----", "")
				.replaceAll("\\s", "");

		byte[] keyBytes = Base64.getDecoder().decode(publicKey);
		X509EncodedKeySpec keySpec = new X509EncodedKeySpec(keyBytes);
		KeyFactory keyFactory = KeyFactory.getInstance("RSA");
		PublicKey publicKeyObj = keyFactory.generatePublic(keySpec);

		Signature verifier = Signature.getInstance("SHA256withRSA");
		verifier.initVerify(publicKeyObj);
		verifier.update(data.getBytes("UTF-8"));

		byte[] signatureBytes = Base64.getDecoder().decode(signature);
		return verifier.verify(signatureBytes);
	}
}
