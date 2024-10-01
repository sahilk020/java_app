package com.pay10.payout.quomopay;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;

import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;

public class QuomoPayPayoutIntegration {
	private static Logger log = LoggerFactory.getLogger(QuomoPayPayoutIntegration.class.getName());

	public static String MerchantId = "35540466";

	private static String BASE_URL = "https://www.quomo.digital";
	private static String TRANSFER_URL = "/Payapi_Index_TransdfUser.html";
	private static String BALANACE_ENQUIRY_URL = "/Payapi_Index_PBalance.html";
	private static String BANK_LIST_URL = "/getBankList";

	public static void main(String[] args) {
		QuomoPayBalanceEnquiryRequest balanceEnquiryRequest = new QuomoPayBalanceEnquiryRequest();
		balanceEnquiryRequest.setMerchantId(MerchantId);
		balanceEnquiryRequest.setRequestTime(balanceEnquiryRequest.getRequestTime());

		balanceEnquiry(balanceEnquiryRequest);

//		QuomoPayTransferRequest payTransferRequest=new QuomoPayTransferRequest();
//		payTransferRequest.setAccountName("Chetan Nagaria");
//		payTransferRequest.setAccountNum("");
//		payTransferRequest.setBankCity("");
//		payTransferRequest.setBankName("");
//		payTransferRequest.setBankProv("");
//		payTransferRequest.setCallback("");
//		payTransferRequest.setCurrencyCode("");
//		payTransferRequest.setMerchantId(MerchantId);
//		payTransferRequest.setMerchantTransactionId(UUID.randomUUID().toString());
//		payTransferRequest.setRequestTime("");
//		payTransferRequest.setTransactionAmount("");
//		transfer(payTransferRequest);
	}

	public static QuomoPayTransferResponse transfer(QuomoPayTransferRequest payTransferRequest) {

		QuomoPayTransferResponse quomoPayTransferResponse = new QuomoPayTransferResponse();
		try {
			URL url = new URL(BASE_URL + TRANSFER_URL);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setDoOutput(true);
			conn.setRequestMethod("POST");
			conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");

			String signdatarequest = null;
			try {
				signdatarequest = RequestCreator
						.generateSignUsingRSA(RequestCreator.prepareMapForTransfer(payTransferRequest));

				System.out.println(signdatarequest);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			OutputStream os = conn.getOutputStream();
			os.write(signdatarequest.getBytes());
			os.flush();
			if (conn.getResponseCode() != HttpURLConnection.HTTP_OK) {
				log.error(new Gson().toJson(conn));
				throw new RuntimeException("Failed : HTTP error code : " + conn.getResponseCode());
			}
			BufferedReader br = new BufferedReader(new InputStreamReader((conn.getInputStream())));
			String output;
			log.info("Output from Server .... \n");
			while ((output = br.readLine()) != null) {
				System.out.println(output);
				try {
					quomoPayTransferResponse = new Gson().fromJson(output, QuomoPayTransferResponse.class);
					JSONObject obj = new JSONObject(output);

				} catch (Exception ex) {
					ex.printStackTrace();
				}
			}
			conn.disconnect();
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return quomoPayTransferResponse;

	}

	private static String encodeParams(String paramName, String paramValue) throws UnsupportedEncodingException {
		return URLEncoder.encode(paramName, "UTF-8") + "=" + URLEncoder.encode(paramValue, "UTF-8");
	}

	public static QuomoPayBalanceEnquiryResponse balanceEnquiry(
			QuomoPayBalanceEnquiryRequest quomoPayBalanceEnquiryRequest) {

		QuomoPayBalanceEnquiryResponse quomoPayBalanceEnquiryResponse = new QuomoPayBalanceEnquiryResponse();
		String requestString = null;
		try {
			String signdatarequest = null;
			try {

				signdatarequest = RequestCreator
						.generateSignUsingRSA(RequestCreator.prepareMapForEnquiry(quomoPayBalanceEnquiryRequest));
				quomoPayBalanceEnquiryRequest.setSignData(signdatarequest);
				// requestString =
				System.out.println(new Gson().toJson(quomoPayBalanceEnquiryRequest));
				System.out.println(signdatarequest);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			URL url = new URL(BASE_URL + BALANACE_ENQUIRY_URL);
			System.out.println(url.toString());
//					+ "?"
//					+ encodeParams("merchantId", quomoPayBalanceEnquiryRequest.getMerchantId()) + "&"
//					+ encodeParams("requestTime", quomoPayBalanceEnquiryRequest.getRequestTime()) + "&"
//					+ encodeParams("signData", quomoPayBalanceEnquiryRequest.getSignData()));
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setDoOutput(true);
			conn.setRequestMethod("POST");
			// conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
			OutputStream os = conn.getOutputStream();
			// os.write(requestString.getBytes());
			os.flush();
			if (conn.getResponseCode() != HttpURLConnection.HTTP_OK) {
				/* log.error(new Gson().toJson(conn)); */
				throw new RuntimeException("Failed : HTTP error code : " + conn.getResponseCode());
			}
			BufferedReader br = new BufferedReader(new InputStreamReader((conn.getInputStream())));
			String output;
			log.info("Output from Server .... \n");
			while ((output = br.readLine()) != null) {
				System.out.println(output);
				try {
					quomoPayBalanceEnquiryResponse = new Gson().fromJson(output, QuomoPayBalanceEnquiryResponse.class);
					JSONObject obj = new JSONObject(output);

				} catch (Exception ex) {
					ex.printStackTrace();
				}
			}
			conn.disconnect();
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return quomoPayBalanceEnquiryResponse;

	}

}
