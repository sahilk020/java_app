package com.pay10.payout.quomopay;

import java.io.BufferedReader;
import java.io.FileReader;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Security;
import java.security.Signature;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import java.util.Map;
import java.util.TreeMap;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import org.apache.commons.lang3.StringUtils;
import org.bouncycastle.jce.provider.BouncyCastleProvider;

public class RequestCreator {
	public static String privateKeyFilePath = "C://Users//Chetan//Desktop//BP_GATE-Payout//PAYOUT//DOCS//QUOMO//PRIVATEKEY.txt";
	public static String publicKeyFilePath = "C://Users//Chetan//Desktop//BP_GATE-Payout//PAYOUT//DOCS//QUOMO//PUBLICKEY.txt";

	public static void main(String[] args) {
		try {
			// Sample parameters for sign verify
			String requestData = "merchantId=18006286&requestTime=2023-12-21+16%3A47%3A43";
			String sign = "Q0TnFJzhWCKnxhGQNQq9eBAnaAPCH00UMNhx6gCcxN2MAxbldjT6WZsI/vHQ/YfZIw4gx49MzhcTwlWOsSXujgh0RvzlK1eXnGAhnKcJLQC6NtHKtRrrFhM3EbDCcLpayYCoONyv79Rtjsdy3fK+HjYderlwI9exunX5sHUBk5E=";

			verifySignUsingRSA(requestData, sign);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/*
	 * public static Map<String, String>
	 * prepareMapForTransferWithSign(HtpayTransactionRequest request) { Map<String,
	 * String> parameters = new TreeMap<>(); parameters.put("amount",
	 * request.getAmount() + ""); parameters.put("clientip", request.getClientip());
	 * parameters.put("currency", request.getCurrency());
	 * parameters.put("mhtorderno", request.getMhtorderno());
	 * parameters.put("mhtuserid", request.getMhtuserid());
	 * parameters.put("notifyurl", request.getNotifyurl());
	 * parameters.put("opmhtid", request.getOpmhtid()); parameters.put("payername",
	 * request.getPayername()); parameters.put("payerphone",
	 * request.getPayerphone()); parameters.put("paytype", request.getPaytype());
	 * parameters.put("random", request.getRandom()); parameters.put("returnurl",
	 * request.getReturnurl()); parameters.put("paytype", request.getPaytype());
	 * parameters.put("bankcode", request.getBankcode());
	 * parameters.put("bankbranch", request.getBankbranch());
	 * parameters.put("acctype", request.getAcctype());
	 * parameters.put("accprovince", request.getAccprovince());
	 * parameters.put("accname", request.getAccname());
	 * parameters.put("acccityname", request.getAcccityname());
	 * parameters.put("sign", request.getSign()); parameters.put("accno",
	 * request.getAccno()); return parameters;
	 * 
	 * }
	 */
	
	public static  Map<String, String> prepareMapForTransfer(Object request) {
        Map<String, String> parameters = new TreeMap<>();
        Class<?> clazz = request.getClass();

        try {
            for (Field field : clazz.getDeclaredFields()) {
                field.setAccessible(true);
                Object value = field.get(request);
                if (value != null) {
                    parameters.put(field.getName(), value.toString());
                }
            }
        } catch (IllegalAccessException e) {
            // Handle the exception as needed
            e.printStackTrace();
        }

        return parameters;
    }
	

	public static  Map<String, String> prepareMapForEnquiry(QuomoPayBalanceEnquiryRequest request) {

		Map<String, String> parameters = new TreeMap<>();
		parameters.put("merchantId", request.getMerchantId());
		parameters.put("requestTime", request.getRequestTime());
		if (StringUtils.isNotBlank(request.getSignData())) {
			parameters.put("signData", request.getSignData());
		}
		return parameters;
	}

	static String generateRequest(Map<String, String> parameters) throws Exception {
		// Concatenate parameters into a string

		StringBuilder data = new StringBuilder();
		for (Map.Entry<String, String> entry : parameters.entrySet()) {
			data.append(entry.getKey()).append("=")
					.append(URLEncoder.encode(entry.getValue(), StandardCharsets.UTF_8.toString())).append("&");
		}
		data.deleteCharAt(data.length() - 1); // Remove the last "&"

		return data.toString();
	}

	static String generateSignUsingRSA(Map<String, String> requestData) throws Exception {
		Security.addProvider(new BouncyCastleProvider());
		// Merchant generates keys using OpenSSL and shares the public key with Quomo

		String merchantPrivateKeyStr = null;
		try {
			merchantPrivateKeyStr = readKeyFromFile(privateKeyFilePath);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println(generateRequest(requestData));
		// Merchant signs the request message with its private key
		String signature = null;
		try {
			signature = rsaSign(generateRequest(requestData), merchantPrivateKeyStr);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println(signature);
		return signature;
	}

	static String verifySignUsingRSA(String requestData, String sign) throws Exception {
		Security.addProvider(new BouncyCastleProvider());
		// Merchant generates keys using OpenSSL and shares the public key with Quomo

		String quomoPublicKeyStr = null;
		try {
			quomoPublicKeyStr = readKeyFromFile(publicKeyFilePath);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		// Quomo verifies the signature using the merchant's public key
		boolean isVerified = false;
		try {
			isVerified = rsaVerify(requestData, sign, quomoPublicKeyStr);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		System.out.println("Signature verified by Quomo: " + isVerified);
		return "";
	}

	private static  String readKeyFromFile(String filePath) throws Exception {
		StringBuilder keyBuilder = new StringBuilder();
		try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
			String line;
			while ((line = reader.readLine()) != null) {
				keyBuilder.append(line).append("\n");
			}
		}
		return keyBuilder.toString();
	}

	static String rsaSign(String data, String privateKey) throws Exception {
		privateKey = privateKey.replace("-----BEGIN PRIVATE KEY-----", "")
				.replace("-----END PRIVATE KEY-----", "").replaceAll("\\s", "");

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

	private static  boolean rsaVerify(String data, String signature, String publicKey) throws Exception {
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
