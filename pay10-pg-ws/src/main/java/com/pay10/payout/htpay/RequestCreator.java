package com.pay10.payout.htpay;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.lang3.StringUtils;

import com.pay10.payout.baseInterfaces.PayoutIntegartorInterface;
import com.pay10.payout.payten.PaytenPayoutInterface;

import java.math.BigDecimal;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.Map;
import java.util.TreeMap;
import java.util.UUID;

public class RequestCreator {

	private static String secretkey = "DUMMY";

	public static void main(String[] args) {
		try {
			// Sample parameters
			Map<String, Object> parameters = new TreeMap<>();
			parameters.put("amount", "1234");
			parameters.put("clientip", "123.123.123.123");
			parameters.put("currency", "JPY");
			parameters.put("mhtorderno", "txn202002272259");
			parameters.put("mhtuserid", "user001");
			parameters.put("notifyurl", "https://example.com");
			parameters.put("opmhtid", "yourId");
			parameters.put("payername", "yourName");
			parameters.put("paytype", "bank");
			parameters.put("random", "gir49gJeo w3");
			parameters.put("returnurl", "http://aa.bb.cc/xxx");

			// Calculate the sign
			String secretKey = secretkey;
			String sign = generateHmacSha384Sign(parameters, secretKey);

			// Print the result
			System.out.println("Generated Sign: " + sign);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static Map<String, Object> prepareMapForTransfer(HtpayTransactionRequest request) {
		Map<String, Object> parameters = new TreeMap<>();

		parameters.put("acccityname", request.getAcccityname());
		parameters.put("accname", request.getAccname());
		parameters.put("accno", request.getAccno());
		parameters.put("accprovince", request.getAccprovince());
		parameters.put("acctype", request.getAcctype());
		parameters.put("amount", request.getAmount());
		parameters.put("bankbranch", request.getBankbranch());
		parameters.put("bankcode", request.getBankcode());
		parameters.put("currency", request.getCurrency());
		parameters.put("mhtorderno", request.getMhtorderno());
		parameters.put("notifyurl", request.getNotifyurl());
		parameters.put("opmhtid", request.getOpmhtid());
		parameters.put("payerphone", request.getPayerphone());
		parameters.put("random", request.getRandom());

//		System.out.println(new gson);
		return parameters;

	}

	public static Map<String, Object> prepareMapForTransferWithSign(HtpayTransactionRequest request) {
		Map<String, Object> parameters = new TreeMap<>();
		parameters.put("acccityname", request.getAcccityname());
		parameters.put("accname", request.getAccname());
		parameters.put("accno", request.getAccno());
		parameters.put("accprovince", request.getAccprovince());
		parameters.put("acctype", request.getAcctype());
		parameters.put("amount", request.getAmount());
		parameters.put("bankbranch", request.getBankbranch());
		parameters.put("bankcode", request.getBankcode());
		parameters.put("currency", request.getCurrency());
		parameters.put("mhtorderno", request.getMhtorderno());
		parameters.put("notifyurl", request.getNotifyurl());
		parameters.put("opmhtid", request.getOpmhtid());
		parameters.put("payerphone", request.getPayerphone());
		parameters.put("random", request.getRandom());
		parameters.put("sign", request.getSign());

		return parameters;

	}

	public static Map<String, Object> prepareMapForEnquiry(HTpayBalanceRequest request) {

		Map<String, Object> parameters = new TreeMap<>();
		parameters.put("opmhtid", request.getOpmhtid());
		parameters.put("random", request.getRandom());
		return parameters;

	}

	static String generateRequestTransfer(Map<String, Object> parameters) throws Exception {
		// Concatenate parameters into a string

		StringBuilder data = new StringBuilder();
		for (Map.Entry<String, Object> entry : parameters.entrySet()) {
				data.append(entry.getKey()).append("=")
						.append(entry.getValue()).append("&");

		}
		data.deleteCharAt(data.length() - 1); // Remove the last "&"

		return data.toString();
	}

	static String generateHmacSha384Sign(Map<String, Object> parameters, String secretKey) throws Exception {
		// Concatenate parameters into a string

		StringBuilder data = new StringBuilder();
		for (Map.Entry<String, Object> entry : parameters.entrySet()) {
			data.append(entry.getKey()).append("=")
					.append(entry.getValue()).append("&");
		}
		data.deleteCharAt(data.length() - 1); // Remove the last "&"

		// Generate the HMAC-SHA-384 signature
		Mac sha384Hmac = Mac.getInstance("HmacSHA384");
		SecretKeySpec secretKeySpec = new SecretKeySpec(secretKey.getBytes(StandardCharsets.UTF_8), "HmacSHA384");
		sha384Hmac.init(secretKeySpec);

		byte[] hashBytes = sha384Hmac.doFinal(data.toString().getBytes(StandardCharsets.UTF_8));

		// Convert the byte array to a hexadecimal string
		StringBuilder hexString = new StringBuilder();
		for (byte hashByte : hashBytes) {
			String hex = Integer.toHexString(0xff & hashByte);
			if (hex.length() == 1)
				hexString.append('0');
			hexString.append(hex);
		}

		return hexString.toString();
	}
}
