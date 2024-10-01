package com.pay10.payout.btsepay;

import java.nio.charset.StandardCharsets;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

public class BtseSendCryptoPrepareRequest {

	public String prepareAndCall() {

		String apiKey = BaseCreds.apiKey;
		String secretKey = BaseCreds.secretKey;
		String apiBaseUrl = BaseCreds.apiBaseUrl;
		String apiURL = "/pay-api/v1/withdraw/crypto";

		try {

			String payload = HmacSha384.generateJSON();
			// Generate nonce and timestamp
			String nonce = HmacSha384.getUNIXTS();

			// Create the signature
			String signature = HmacSha384.generateHmacSha384Signature(apiURL + nonce + payload, secretKey);

			// Build the URL with query parameters
			String apiUrl = String.format(apiBaseUrl + apiURL);

			// add payload code here

			// Make the API request
			String response = makeApiRequest(apiUrl, apiKey, nonce, signature, payload);

			System.out.println("API Response: " + response);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	private static String makeApiRequest(String apiUrl, String apiKey, String nonce, String signature, String Json)
			throws Exception {
		try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
			HttpPost request = new HttpPost(apiUrl);

			// Set headers
			request.setHeader("api", apiKey);
			request.setHeader("nonce", nonce);
			request.setHeader("sign", signature);

			StringEntity sentity = new StringEntity(Json);
			request.setEntity(sentity);

			// Execute the request
			HttpResponse response = httpClient.execute(request);

			// Read and return the response
			HttpEntity entity = response.getEntity();
			return EntityUtils.toString(entity, StandardCharsets.UTF_8);
		}
	}

}
