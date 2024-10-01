package com.pay10.payout.btsepay;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

public class BtseStatusEnquiryPrepareRequest {

	public String prepareAndCall(String txId, String address, String clientOrderId, String referenceId,
			String transactionId, String transactionNumber, int transactionType, String coin, String protocol,
			long fromTime, long toTime) {
		// Replace these values with your actual API key and endpoint
		String apiKey = BaseCreds.apiKey;
		String secretKey = BaseCreds.secretKey;
		String apiBaseUrl = BaseCreds.apiBaseUrl;
		String apiURL = "/pay-api/v1/withdraw/wallet/crypto/history";

		// Replace these values with your actual query parameters
		txId = "0x06d14019fb69b82c62857f24d61baab2c209f56f75ad9921a87904ea72a44ae9";
		address = "0x4fb9948120A3DF77ace0F3ED7D4C9989e71B6ac9";
		clientOrderId = "USDT";
		referenceId = "txnRefTest0001";
		transactionId = "ddfdd8e3-2bbe-4677-89a3-3a7535e538b4";
		transactionNumber = "2021120200000005";
		transactionType = 2; // 1: Deposit, 2: Withdraw
		coin = "USDT";
		protocol = "ERC20";
		fromTime = 1638411897000L;
		toTime = 1638411899000L;

		// Current timestamp in milliseconds as nonce
		String nonce = String.valueOf(System.currentTimeMillis());

		// Calculate signature based on your logic
		String signature = HmacSha384.generateHmacSha384Signature(apiURL + nonce, secretKey);

		// Make the API request
		try {
			HttpClient httpClient = HttpClients.createDefault();
			HttpGet httpGet = new HttpGet(buildUrl(apiURL, txId, address, clientOrderId, referenceId, transactionId,
					transactionNumber, transactionType + "", coin, protocol, fromTime + "", toTime + ""));

			// Set headers
			httpGet.addHeader("api", apiKey);
			httpGet.addHeader("nonce", nonce);
			httpGet.addHeader("sign", signature);

			// Execute the request
			HttpResponse response = httpClient.execute(httpGet);

			// Handle the response (e.g., parse JSON response)
			System.out.println("Response Code: " + response.getStatusLine().getStatusCode());
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

//	private URI buildUrl(String string, String txId, String address, String clientOrderId, String referenceId,
//			String transactionId, String transactionNumber, int transactionType, String coin, String protocol,
//			long fromTime, long toTime) {
//		// TODO Auto-generated method stub
//		return null;
//	}

	private static String buildUrl(String apiUrl, String... queryParams) throws UnsupportedEncodingException {
		StringBuilder urlBuilder = new StringBuilder(apiUrl);
		if (queryParams.length > 0) {
			if (StringUtils.isNoneBlank(queryParams)) {

				urlBuilder.append("?");
				for (int i = 0; i < queryParams.length; i += 2) {
					if (StringUtils.isNotBlank(queryParams[i])) {
						urlBuilder.append(queryParams[i]);
						urlBuilder.append("=");
						urlBuilder.append(queryParams[i + 1]);
						if (i < queryParams.length - 2) {
							urlBuilder.append("&");
						}
					}
				}
			}

		}
		return urlBuilder.toString();
	}
}
