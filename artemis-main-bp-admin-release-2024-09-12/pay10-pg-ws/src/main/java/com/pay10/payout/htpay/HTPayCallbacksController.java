package com.pay10.payout.htpay;

import java.io.BufferedReader;
import java.io.FileReader;
import java.nio.charset.StandardCharsets;
import java.security.KeyFactory;
import java.security.MessageDigest;
import java.security.PublicKey;
import java.security.Security;
import java.security.Signature;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import java.util.Map;

import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.google.gson.Gson;


@RestController
public class HTPayCallbacksController {
	private static Logger log = LoggerFactory.getLogger(HTPayCallbacksController.class.getName());
	public static String publicKeyFilePath = "C://Users//Chetan//Desktop//BP_GATE-Payout//PAYOUT//DOCS//QUOMO//PUBLICKEY.txt";
	private static String secretkey = "Enlx4azMVN8c5XcQxv5suRNjdC6UnpomhuVUp8IIMfQg2JIEjyAGlRHfvI9QtrFN";

	@PostMapping("/htpay/notifyTxncallbackResponse")
	public String notifyResponse(@RequestBody Map<String, String> response) {
		System.out.println(new Gson().toJson(response));
		return "OK";
//		try {
//			log.info(new Gson().toJson(response));
//			String opmhtid = response.get("opmhtid");
//			String mhtorderno = response.get("mhtorderno");
//			String dataHash = response.get("data");
//			
//			
//			log.info("HASH received in callback" +dataHash);
//			
//			String yourId = "txn202102292259";//mhtorderno
//			String accNo = "0xc81A0cDd3f8Cd1b4eDd29720e87890FBDCAD538d";
//			double amount = 100.12;
//			String merchantSignKey = secretkey;
//
//			// Concatenate data
//			String dataToHash = yourId + accNo + amount + merchantSignKey;
//
//			// Calculate MD5 hash
//			String md5Hash = calculateMD5(dataToHash);
//
//			// Print the hash
//			System.out.println("MD5 Hash: " + md5Hash);
//
//			// Verify against the expected hash
//			String expectedHash = dataHash;
//			if (md5Hash.equals(expectedHash)) {
//				System.out.println("Hash verification successful!");
//				return "ok";
//			} else {
//				return "not ok";
//			}
//
//		} catch (Exception e) {
//			e.printStackTrace();
//			return "not ok";
//		}

		
	}

	private static String calculateMD5(String data) throws Exception {
		MessageDigest md = MessageDigest.getInstance("MD5");
		byte[] hashBytes = md.digest(data.getBytes(StandardCharsets.UTF_8));

		// Convert the byte array to a hexadecimal string
		StringBuilder hexStringBuilder = new StringBuilder();
		for (byte b : hashBytes) {
			hexStringBuilder.append(String.format("%02x", b));
		}

		return hexStringBuilder.toString();

	}

	@PostMapping("/htpay/verifyTxncallbackResponse")
	public String verifyResponse(@RequestParam String afterbalance, @RequestParam String amount,
			@RequestParam String currency, @RequestParam String mhtorderno, @RequestParam String pforderno,
			@RequestParam String random, @RequestParam String resultcode, @RequestParam String sign,
			@RequestParam(required = false) String note, @RequestParam String payouttime) {
		try {

			// Call update processor if transaction is failed or success

			String response = String.format(
					"Received: afterbalance=%s, amount=%s, currency=%s, mhtorderno=%s, "
							+ "pforderno=%s, random=%s, resultcode=%s, sign=%s, note=%s, payouttime=%s",
					afterbalance, amount, currency, mhtorderno, pforderno, random, resultcode, sign, note, payouttime);

			//return new ResponseEntity<>(response, org.springframework.http.HttpStatus.OK);
			System.out.println(response);
			return "success";
		} catch (Exception e) {
			e.printStackTrace();
			return "Invalid Request parameters received";
//			return new ResponseEntity<>("Invalid Request parameters received",
//					org.springframework.http.HttpStatus.BAD_REQUEST);
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
