package com.pay10.payout.btsepay;

import java.nio.charset.StandardCharsets;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

public class BtseGetFeesPrepareRequest {

	public String prepareAndCall(String coin, String protocol) {

		String apiKey = BaseCreds.apiKey;
		String secretKey = BaseCreds.secretKey;
		String apiBaseUrl = BaseCreds.apiBaseUrl;
		String apiURL = "/pay-api/v1/withdraw/crypto/fee";
		coin = "USDT";
		protocol = "ERC20";

		try {
			// Generate nonce and timestamp
			String nonce = HmacSha384.getUNIXTS();

			// Create the signature
			String signature = HmacSha384.generateHmacSha384Signature(apiURL + nonce, secretKey);

			// Build the URL with query parameters
			String apiUrl = String.format(apiBaseUrl + apiURL + "?coin=%s&protocol=%s", coin, protocol);

			// Make the API request
			String response = makeApiRequest(apiUrl, apiKey, nonce, signature);

			System.out.println("API Response: " + response);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	private static String makeApiRequest(String apiUrl, String apiKey, String nonce, String signature)
			throws Exception {
		try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
			HttpUriRequest request = new HttpGet(apiUrl);

			// Set headers
			request.setHeader("api", apiKey);
			request.setHeader("nonce", nonce);
			request.setHeader("sign", signature);

			// Execute the request
			HttpResponse response = httpClient.execute(request);

			// Read and return the response
			HttpEntity entity = response.getEntity();
			return EntityUtils.toString(entity, StandardCharsets.UTF_8);
		}
	}
}
