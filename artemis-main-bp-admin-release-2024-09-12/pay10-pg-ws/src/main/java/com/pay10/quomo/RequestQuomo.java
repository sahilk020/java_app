package com.pay10.quomo;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.bind.DatatypeConverter;

import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;

import com.pay10.commons.exception.SystemException;

public class RequestQuomo {
	private static Logger logger = LoggerFactory.getLogger(RequestQuomo.class.getName());

	public static String signData(Map<String, Object> reqmap) throws NoSuchAlgorithmException {
		logger.info("Quomo Request for signData ::: " + reqmap);
		StringBuilder reqParam = new StringBuilder();
		reqParam.append(reqmap.get("md5Key"));
		reqParam.append(reqmap.get("merchantId"));
		reqParam.append(reqmap.get("businessEmail"));
		reqParam.append(reqmap.get("productName"));
		reqParam.append(reqmap.get("orderId"));
		reqParam.append(reqmap.get("depositMethodId"));
		reqParam.append(reqmap.get("bankId"));
		reqParam.append(reqmap.get("depositAmount"));
		reqParam.append(reqmap.get("currency"));
		reqParam.append("NA");
		reqParam.append("NA");
		reqParam.append("NA");
		reqParam.append("NA");
		reqParam.append(reqmap.get("customerName"));
		reqParam.append(reqmap.get("customerIp"));
		reqParam.append(reqmap.get("customerEmail"));
		reqParam.append(reqmap.get("customerPhoneNo"));
		reqParam.append(reqmap.get("customerAddress"));
		reqParam.append(reqmap.get("note"));
		reqParam.append(reqmap.get("websiteUrl"));
		reqParam.append(reqmap.get("requestTime"));
		reqParam.append(reqmap.get("successUrl"));
		reqParam.append(reqmap.get("failUrl"));
		reqParam.append(reqmap.get("callbackNotiUrl"));
		logger.info("request prepaid for signData :: " + reqParam.toString());
		MessageDigest md = MessageDigest.getInstance("MD5");
		byte[] digest = md.digest(reqParam.toString().getBytes());
		String myHash = DatatypeConverter.printHexBinary(digest).toUpperCase();
		logger.info("Quomo,sign_data myHash :: " + myHash);
		return myHash;
	}

	public static String HttpBasicAuth(String request) {
		logger.info("---------------- HttpBasicAuth --------------------");

		StringBuilder serverResponse = new StringBuilder();
		try {
			JSONObject jsonRequest = new JSONObject();
			jsonRequest.put("requestParams", request);
			logger.info("jsonRequest ");
			URL url = new URL("https://gateway.quomo.digital/api/fundin/deposit");
			String encoding = Base64.getEncoder()
					.encodeToString(("lmp@gateway.quomo.digital:lmp@pa$$ap!^wmDB39zt6grvP$dHXCB").getBytes("UTF-8"));
			logger.info("encoding " + encoding);
			HttpURLConnection connection = (HttpURLConnection) url.openConnection();
			connection.setRequestMethod("POST");
			connection.setUseCaches(false);
			connection.setDoOutput(true);
			connection.setDoInput(true);
			connection.setRequestProperty("Content-Disposition", "multipart/form-data");
			connection.setRequestProperty("Authorization", "Basic " + encoding);
			OutputStream outputStream = connection.getOutputStream();
			DataOutputStream wr = new DataOutputStream(outputStream);
			wr.writeBytes(jsonRequest.toString());
			wr.close();

			InputStream content = (InputStream) connection.getInputStream();
			int code = ((HttpURLConnection) connection).getResponseCode();
			logger.info("Http Response Code" + code);

			BufferedReader in = new BufferedReader(new InputStreamReader(content));
			String line;
			while ((line = in.readLine()) != null) {
				serverResponse.append(line);
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.info("Exception HttpBasicAuth, " + e);
		}
		return serverResponse.toString();
	}

	@SuppressWarnings("unchecked")
	public static void sendRequest(String request) {
		try {
			System.out.println("request " + request);
			URL url = new URL("https://gateway.quomo.digital/api/fundin/deposit");
			HttpURLConnection connection = (HttpURLConnection) url.openConnection();
			connection.setRequestMethod("POST");
			connection.setDoOutput(true);
			connection.setRequestProperty("Content-Type", MediaType.APPLICATION_FORM_URLENCODED_VALUE);
			connection.setRequestProperty("Content-Disposition", "form-data");
			connection.setRequestProperty("Authorization", "Basic " + Base64.getEncoder()
					.encodeToString(("lmp@gateway.quomo.digital:lmp@pa$$ap!^wmDB39zt6grvP$dHXCB").getBytes()));
			JSONObject jsonRequest = new JSONObject();
			jsonRequest.put("requestParams", request);
			System.out.println("jsonRequest " + jsonRequest);
			byte[] out = jsonRequest.toString().getBytes(StandardCharsets.UTF_8);
			OutputStream stream = connection.getOutputStream();
			stream.write(out);
			System.out.println(connection.getResponseCode() + " " + connection.getResponseMessage()); // THis is
																										// optional
			connection.disconnect();
		} catch (Exception e) {
			System.out.println(e);
			System.out.println("Failed successfully");
		}
	}

	public static String HttpBasicAuth1(String request) {
		logger.info("---------------- HttpBasicAuth1 --------------------");
		StringBuilder serverResponse = new StringBuilder();
		try {

			URL url = new URL("https://gateway.quomo.digital/api/fundin/deposit");
			String encoding = Base64.getEncoder()
					.encodeToString(("lmp@gateway.quomo.digital:lmp@pa$$ap!^wmDB39zt6grvP$dHXCB").getBytes("UTF-8"));
			logger.info("encoding " + encoding);
			HttpURLConnection connection = (HttpURLConnection) url.openConnection();
			connection.setRequestMethod("POST");
			connection.setUseCaches(false);
			connection.setDoOutput(true);
			connection.setDoInput(true);
			connection.setRequestProperty("Authorization", encoding);
			OutputStream outputStream = connection.getOutputStream();
			DataOutputStream wr = new DataOutputStream(outputStream);
			wr.writeBytes(request.toString());
			wr.close();

			InputStream content = (InputStream) connection.getInputStream();
			int code = ((HttpURLConnection) connection).getResponseCode();
			logger.info("Http Response Code" + code);

			BufferedReader in = new BufferedReader(new InputStreamReader(content));
			String line;
			while ((line = in.readLine()) != null) {
				serverResponse.append(line);
			}
		} catch (Exception e) {
			logger.info("Exception HttpBasicAuth1, " + e);
			e.printStackTrace();
		}
		return serverResponse.toString();
	}

	@SuppressWarnings("unchecked")
	public String testRequest1(Map<String, String> reqmap1) throws NoSuchAlgorithmException, SystemException {
		Map<String, Object> reqmap = new HashMap<String, Object>();
		reqmap.put("md5Key", "APIKEYMD51800628664C1F0B6BBAF2");
		reqmap.put("merchantId", "18006286");
		reqmap.put("businessEmail", "sonu@pay10.com");
		reqmap.put("productName", "Mobile");
		reqmap.put("orderId", String.valueOf(System.currentTimeMillis() / 1000L));
		reqmap.put("depositMethodId", 8);
		reqmap.put("bankId", "HDFC.IN");
		reqmap.put("depositAmount", 100);
		reqmap.put("currency", "INR");
		reqmap.put("customerName", "sonuchaudhari");
		reqmap.put("customerIp", "3.7.144.169");
		reqmap.put("customerEmail", "sonu@pay10.com");
		reqmap.put("customerPhoneNo", "9767146866");
		reqmap.put("customerAddress", "mumbai");
		reqmap.put("note", "test");
		reqmap.put("websiteUrl", "www.pay10.com");
		reqmap.put("requestTime", String.valueOf(System.currentTimeMillis() / 1000L));
		reqmap.put("successUrl", "https://uat.pay10.com/pgui/jsp/quomoResponse");
		reqmap.put("failUrl", "https://uat.pay10.com/pgui/jsp/quomoResponse");
		reqmap.put("callbackNotiUrl", "https://uat.pay10.com/pgui/jsp/quomoNotificationResponse");
		reqmap.put("API_USER_NAME", "lmp@gateway.quomo.digital");
		reqmap.put("API_PASSWORD", "lmp@pa$$ap!^wmDB39zt6grvP$dHXCB");
		reqmap.put("URL", "https://gateway.quomo.digital/api/fundin/deposit");
		// reqmap.put("Authorization",
		// "bG1wQGdhdGV3YXkucXVvbW8uZGlnaXRhbDpsbXBAcGEkJGFwIV53bURCMzl6dDZncnZQJGRIWENC");
		logger.info("######## reqmap ########" + reqmap);

		JSONObject jsonRequest = new JSONObject();
		jsonRequest.put("merchant_id", String.valueOf(reqmap.get("merchantId")));
		jsonRequest.put("business_email", String.valueOf(reqmap.get("businessEmail")));
		jsonRequest.put("product_name", String.valueOf(reqmap.get("productName")));
		jsonRequest.put("order_id", String.valueOf(reqmap.get("orderId")));
		jsonRequest.put("deposit_method_id", reqmap.get("depositMethodId"));
		jsonRequest.put("bank_id", String.valueOf(reqmap.get("bankId")));
		jsonRequest.put("deposit_amount", reqmap.get("depositAmount"));
		jsonRequest.put("currency", String.valueOf(reqmap.get("currency")));
		jsonRequest.put("customer_name", String.valueOf(reqmap.get("customerName")));
		jsonRequest.put("card_number", "NA");
		jsonRequest.put("card_month", "NA");
		jsonRequest.put("card_year", "NA");
		jsonRequest.put("card_cvv", "NA");
		jsonRequest.put("customer_ip", String.valueOf(reqmap.get("customerIp")));
		jsonRequest.put("customer_email", String.valueOf(reqmap.get("customerEmail")));
		jsonRequest.put("customer_phone_no", String.valueOf(reqmap.get("customerPhoneNo")));
		jsonRequest.put("customer_address", String.valueOf(reqmap.get("customerAddress")));
		jsonRequest.put("note", String.valueOf(reqmap.get("note")));
		jsonRequest.put("website_url", String.valueOf(reqmap.get("websiteUrl")));
		jsonRequest.put("request_time", String.valueOf(reqmap.get("requestTime")));
		jsonRequest.put("success_url", String.valueOf(reqmap.get("successUrl")));
		jsonRequest.put("fail_url", String.valueOf(reqmap.get("failUrl")));
		jsonRequest.put("callback_noti_url", String.valueOf(reqmap.get("callbackNotiUrl")));

		jsonRequest.put("sign_data", signData(reqmap));

		logger.info("Final Request : " + jsonRequest.toString());
		// String resp = httpCall(jsonRequest, reqmap);
		String HttpBasicAuth = HttpBasicAuth(jsonRequest.toString());
		logger.info("resp " + HttpBasicAuth);
		// String HttpBasicAuth1 = HttpBasicAuth1(jsonRequest.toString());
		// logger.info("resp " + HttpBasicAuth1);
		return "success";
	}

	@SuppressWarnings("unchecked")
	public static void main(String[] args) throws Exception {
		Map<String, Object> reqmap = new HashMap<String, Object>();
		reqmap.put("md5Key", "APIKEYMD51800628664C1F0B6BBAF2");
		reqmap.put("merchantId", "18006286"); //18006286
		reqmap.put("businessEmail", "sonu@pay10.com");
		reqmap.put("productName", "Mobile");
		reqmap.put("orderId", String.valueOf(System.currentTimeMillis() / 1000L));
		reqmap.put("depositMethodId", 9);
		reqmap.put("bankId", "allbank-qr-hybrid");
		reqmap.put("depositAmount", 100000);
		reqmap.put("currency", "PHP");
		reqmap.put("customerName", "sonuchaudhari");
		reqmap.put("customerIp", "3.7.144.169");
		reqmap.put("customerEmail", "sonu@pay10.com");
		reqmap.put("customerPhoneNo", "9767146866");
		reqmap.put("customerAddress", "mumbai");
		reqmap.put("note", "test");
		reqmap.put("websiteUrl", "www.pay10.com");
		reqmap.put("requestTime", String.valueOf(System.currentTimeMillis() / 1000L));
		reqmap.put("successUrl", "https://uat.pay10.com/pgui/jsp/quomoResponse");
		reqmap.put("failUrl", "https://uat.pay10.com/pgui/jsp/quomoResponse");
		reqmap.put("callbackNotiUrl", "https://uat.pay10.com/pgui/jsp/quomoNotificationResponse");
		reqmap.put("API_USER_NAME", "lmp@gateway.quomo.digital");
		reqmap.put("API_PASSWORD", "lmp@pa$$ap!^wmDB39zt6grvP$dHXCB");
		reqmap.put("URL", "https://gateway.quomo.digital/api/fundin/deposit");

		JSONObject jsonRequest = new JSONObject();
		jsonRequest.put("merchant_id", String.valueOf(reqmap.get("merchantId")));
		jsonRequest.put("business_email", String.valueOf(reqmap.get("businessEmail")));
		jsonRequest.put("product_name", String.valueOf(reqmap.get("productName")));
		jsonRequest.put("order_id", String.valueOf(reqmap.get("orderId")));
		jsonRequest.put("deposit_method_id", reqmap.get("depositMethodId"));
		jsonRequest.put("bank_id", String.valueOf(reqmap.get("bankId")));
		jsonRequest.put("deposit_amount", reqmap.get("depositAmount"));
		jsonRequest.put("currency", String.valueOf(reqmap.get("currency")));
		jsonRequest.put("customer_name", String.valueOf(reqmap.get("customerName")));
		jsonRequest.put("card_number", "NA");
		jsonRequest.put("card_month", "NA");
		jsonRequest.put("card_year", "NA");
		jsonRequest.put("card_cvv", "NA");
		jsonRequest.put("customer_ip", String.valueOf(reqmap.get("customerIp")));
		jsonRequest.put("customer_email", String.valueOf(reqmap.get("customerEmail")));
		jsonRequest.put("customer_phone_no", String.valueOf(reqmap.get("customerPhoneNo")));
		jsonRequest.put("customer_address", String.valueOf(reqmap.get("customerAddress")));
		jsonRequest.put("note", String.valueOf(reqmap.get("note")));
		jsonRequest.put("website_url", String.valueOf(reqmap.get("websiteUrl")));
		jsonRequest.put("request_time", String.valueOf(reqmap.get("requestTime")));
		jsonRequest.put("success_url", String.valueOf(reqmap.get("successUrl")));
		jsonRequest.put("fail_url", String.valueOf(reqmap.get("failUrl")));
		jsonRequest.put("callback_noti_url", String.valueOf(reqmap.get("callbackNotiUrl")));

		jsonRequest.put("sign_data", signData(reqmap));

		logger.info("Final Request :: " + jsonRequest);
		String resp = sendPost(jsonRequest.toString().replace("\\", ""));

		logger.info("Resposne ::  "+resp);
		//JSONObject jsonResp = new JSONObject(resp);
		//logger.info("jsonResp "+String.valueOf(jsonResp.get("next_action")));
		/*
		 * String paymentString =
		 * "https://vndpays.com/redirect?refid=FIVND8017756ACBJ5Z02RLRE&key=684090DC7E1FF99FE0FE740CBACDCA49981BCC05";
		 * StringBuilder httpRequest = new StringBuilder();
		 * httpRequest.append("<HTML>");
		 * httpRequest.append("<BODY OnLoad=\"OnLoadEvent();\" >");
		 * httpRequest.append("<form name=\"form1\" action=\"");
		 * httpRequest.append(paymentString); httpRequest.append("\" method=\"post\">");
		 * 
		 * httpRequest.append("</form>");
		 * httpRequest.append("<script language=\"JavaScript\">");
		 * httpRequest.append("function OnLoadEvent()");
		 * httpRequest.append("{document.form1.submit();}");
		 * httpRequest.append("</script>"); httpRequest.append("</BODY>");
		 * httpRequest.append("</HTML>"); logger.info("Final Request :: "+httpRequest);
		 */
	}

	public static String sendPost(String request) throws Exception {

		logger.info(" ############ request " + request);
		HttpPost post = new HttpPost("https://gateway.quomo.digital/api/fundin/deposit");
		String resp = null;
		// add request parameter, form parameters
		List<NameValuePair> urlParameters = new ArrayList<>();
		urlParameters.add(new BasicNameValuePair("requestParams", request));
		post.setEntity(new UrlEncodedFormEntity(urlParameters));
		post.setHeader("Authorization", "Basic " + Base64.getEncoder()
				.encodeToString(("lmp@gateway.quomo.digital:lmp@pa$$ap!^wmDB39zt6grvP$dHXCB").getBytes()));

		try (CloseableHttpClient httpClient = HttpClients.createDefault();
				CloseableHttpResponse response = httpClient.execute(post)) {

			resp = EntityUtils.toString(response.getEntity());
		}
		return resp;
	}

}
