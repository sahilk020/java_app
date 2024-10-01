package com.pay10.commons.api;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.io.IOUtils;

import org.apache.commons.lang.StringUtils;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.pay10.commons.dao.HibernateSessionProvider;
import com.pay10.commons.user.Invoice;
import com.pay10.commons.user.User;
import com.pay10.commons.util.Amount;
import com.pay10.commons.util.Constants;
import com.pay10.commons.util.FieldType;
import com.pay10.commons.util.Fields;
import com.pay10.commons.util.PropertiesManager;

@Service("smsControllerServiceProvider")
public class SmsControllerServiceProvider {

	private static Logger logger = LoggerFactory.getLogger(SmsControllerServiceProvider.class.getName());

	@Autowired
	private PropertiesManager propertiesManager;

	@Autowired
	private ShortUrlProvider urlProvider;

	public void sendPromoSMS(Invoice invoiceDB, String shortUrl) {
		try {
			JSONObject json = new JSONObject();
			json.put("invoiceId", invoiceDB.getInvoiceId());
			json.put("payId", invoiceDB.getPayId());
			json.put("businessName", invoiceDB.getBusinessName());
			json.put("invoiceNo", invoiceDB.getInvoiceNo());
			json.put("name", invoiceDB.getName());
			json.put("city", invoiceDB.getCity());
			json.put("country", invoiceDB.getCountry());
			json.put("state", invoiceDB.getState());
			json.put("zip", invoiceDB.getZip());
			json.put("phone", invoiceDB.getPhone());
			json.put("email", invoiceDB.getEmail());
			json.put("address", invoiceDB.getAddress());
			json.put("productName", invoiceDB.getProductName());
			json.put("productDesc", invoiceDB.getProductDesc());
			json.put("quantity", invoiceDB.getQuantity());
			json.put("amount", invoiceDB.getAmount());
			json.put("serviceCharge", invoiceDB.getServiceCharge());
			json.put("totalAmount", invoiceDB.getTotalAmount());
			json.put("currencyCode", invoiceDB.getCurrencyCode());
			json.put("expiresDay", invoiceDB.getExpiresDay());
			json.put("expiresHour", invoiceDB.getExpiresHour());
			json.put("createDate", invoiceDB.getCreateDate());
			json.put("updateDate", invoiceDB.getUpdateDate());
			json.put("saltKey", invoiceDB.getSaltKey());
			json.put("returnUrl", invoiceDB.getReturnUrl());
			json.put("recipientMobile", invoiceDB.getRecipientMobile());
			json.put("messageBody", invoiceDB.getMessageBody());
			json.put("shortUrl", invoiceDB.getShortUrl());
			json.put("invoiceType", invoiceDB.getInvoiceType());

			String serviceUrl = PropertiesManager.propertiesMap.get("SMSServiceSendPromoSMSURL");
			String url = serviceUrl + shortUrl;
			CloseableHttpClient httpClient = HttpClientBuilder.create().build();
			HttpPost request = new HttpPost(url);
			StringEntity params = new StringEntity(json.toString());
			request.addHeader("Content-Type", "application/json");
			request.setEntity(params);
			HttpResponse resp = httpClient.execute(request);
			StatusLine statusLine = resp.getStatusLine();
			int statusCode = resp.getStatusLine().getStatusCode();
			System.out.println("request sent  resp code" + statusCode);
		} catch (Exception exception) {
			logger.error("error" + exception);
		}

	}

	@SuppressWarnings("unchecked")
	public String sendSMS(String mobile, String message) throws IOException {

		String smsUrl = PropertiesManager.propertiesMap.get(Constants.SMS_URL.getValue());
		String senderId = PropertiesManager.propertiesMap.get(Constants.SENDER_ID.getValue());
		String senderPassword = PropertiesManager.propertiesMap.get(Constants.SENDER_PASSWORD.getValue());
		String senderName = PropertiesManager.propertiesMap.get(Constants.SENDER_NAME.getValue());

		StringBuilder xmlString = new StringBuilder();
		xmlString.append("<?xml version=\"1.0\" encoding=\"ISO-8859-1\"?>");
		xmlString.append("<!DOCTYPE MESSAGE SYSTEM \"https://api.myvaluefirst.com/psms/dtd/messagev12.dtd\">");
		xmlString.append("<MESSAGE VER=\"1.2\">");
		xmlString.append("<USER USERNAME=\"" + senderId + "\" PASSWORD=\"" + senderPassword + "\" />");
		xmlString.append("<SMS TEXT=\"" + message + "\"  ID=\"1\">");
		xmlString.append("<ADDRESS FROM=\"" + senderName + "\" TO=\"" + mobile + "\" SEQ=\"1\" />");
		xmlString.append("</SMS>");
		xmlString.append("</MESSAGE>");

		StringBuilder responseBuilder = new StringBuilder();
		try {
			HttpClient httpclient = HttpClients.createDefault();
			HttpPost httppost = new HttpPost(smsUrl);

			List<NameValuePair> params = new ArrayList<NameValuePair>(2);
			params.add(new BasicNameValuePair("data", xmlString.toString()));
			params.add(new BasicNameValuePair("action", "send"));
			httppost.setEntity(new UrlEncodedFormEntity(params, "UTF-8"));

			// Execute and get the response.
			HttpResponse response = httpclient.execute(httppost);
			HttpEntity entity = response.getEntity();

			if (entity != null) {
				try (InputStream instream = entity.getContent()) {

					Reader in = new BufferedReader(new InputStreamReader(instream, "UTF-8"));

					for (int c; (c = in.read()) >= 0;)
						responseBuilder.append((char) c);
				}
			}

			logger.info("SMS Response - " + responseBuilder.toString());
		} catch (Exception exception) {
			logger.error("Exception in sending SMS ", exception);
			return null;
		}
		return "success";

	}

	@SuppressWarnings({ "unchecked", "static-access" })
	public String sendTxnFailedSms(String mobile, String subject, String customerName, String merchnatBusinessName,
			String amount, String message, String currencyCode) throws IOException {

		String smsUrl = PropertiesManager.propertiesMap.get(Constants.SMS_URL.getValue());
		String senderId = PropertiesManager.propertiesMap.get(Constants.SENDER_ID.getValue());
		String senderPassword = PropertiesManager.propertiesMap.get(Constants.SENDER_PASSWORD.getValue());
		String senderName = PropertiesManager.propertiesMap.get(Constants.SENDER_NAME.getValue());

		Amount af = new Amount();
		String actualAmount = amount;
		amount = af.toDecimal(actualAmount, currencyCode);

		String smsMessageBody = "Your transaction on " + merchnatBusinessName + " for " + amount
				+ " just failed due to " + message
				+ " Please retry the same preferably with a different payment method.";
		StringBuilder xmlString = new StringBuilder();
		xmlString.append("<?xml version=\"1.0\" encoding=\"ISO-8859-1\"?>");
		xmlString.append("<!DOCTYPE MESSAGE SYSTEM \"https://api.myvaluefirst.com/psms/dtd/messagev12.dtd\">");
		xmlString.append("<MESSAGE VER=\"1.2\">");
		xmlString.append("<USER USERNAME=\"" + senderId + "\" PASSWORD=\"" + senderPassword + "\" />");
		xmlString.append("<SMS TEXT=\"" + smsMessageBody + "\"  ID=\"1\">");
		xmlString.append("<ADDRESS FROM=\"" + senderName + "\" TO=\"" + mobile + "\" SEQ=\"1\" />");
		xmlString.append("</SMS>");
		xmlString.append("</MESSAGE>");

		StringBuilder responseBuilder = new StringBuilder();
		try {
			HttpClient httpclient = HttpClients.createDefault();
			HttpPost httppost = new HttpPost(smsUrl);

			List<NameValuePair> params = new ArrayList<NameValuePair>(2);
			params.add(new BasicNameValuePair("data", xmlString.toString()));
			params.add(new BasicNameValuePair("action", "send"));
			httppost.setEntity(new UrlEncodedFormEntity(params, "UTF-8"));

			// Execute and get the response.
			HttpResponse response = httpclient.execute(httppost);
			HttpEntity entity = response.getEntity();

			if (entity != null) {
				try (InputStream instream = entity.getContent()) {

					Reader in = new BufferedReader(new InputStreamReader(instream, "UTF-8"));

					for (int c; (c = in.read()) >= 0;)
						responseBuilder.append((char) c);
				}
			}

			logger.info("SMS Response - " + responseBuilder.toString());
		} catch (Exception exception) {
			logger.error("Exception in sending SMS ", exception);
			return null;
		}
		return "success";

	}

// send invoice via sms
	public void sendInvoiceSMS(String mobile, String url, String merchant, String name) {
		String smsUrl = PropertiesManager.propertiesMap.get(Constants.SMS_URL.getValue());
		String senderId = PropertiesManager.propertiesMap.get(Constants.SENDER_ID.getValue());
		String senderPassword = PropertiesManager.propertiesMap.get(Constants.SENDER_PASSWORD.getValue());
		String senderName = PropertiesManager.propertiesMap.get(Constants.SENDER_NAME.getValue());
		String smsMessageBody = "Dear " + name + ", Please click below for making the payment for invoice shared. "
				+ url + " Team " + merchant + " ";

		StringBuilder xmlString = new StringBuilder();
		xmlString.append("<?xml version=\"1.0\" encoding=\"ISO-8859-1\"?>");
		xmlString.append("<!DOCTYPE MESSAGE SYSTEM \"https://api.myvaluefirst.com/psms/dtd/messagev12.dtd\">");
		xmlString.append("<MESSAGE VER=\"1.2\">");
		xmlString.append("<USER USERNAME=\"" + senderId + "\" PASSWORD=\"" + senderPassword + "\" />");
		xmlString.append("<SMS TEXT=\"" + smsMessageBody + "\"  ID=\"1\">");
		xmlString.append("<ADDRESS FROM=\"" + senderName + "\" TO=\"" + mobile + "\" SEQ=\"1\" />");
		xmlString.append("</SMS>");
		xmlString.append("</MESSAGE>");

		StringBuilder responseBuilder = new StringBuilder();
		try {
			HttpClient httpclient = HttpClients.createDefault();
			HttpPost httppost = new HttpPost(smsUrl);

			List<NameValuePair> params = new ArrayList<NameValuePair>(2);
			params.add(new BasicNameValuePair("data", xmlString.toString()));
			params.add(new BasicNameValuePair("action", "send"));
			httppost.setEntity(new UrlEncodedFormEntity(params, "UTF-8"));

			// Execute and get the response.
			HttpResponse response = httpclient.execute(httppost);
			HttpEntity entity = response.getEntity();

			if (entity != null) {
				try (InputStream instream = entity.getContent()) {

					Reader in = new BufferedReader(new InputStreamReader(instream, "UTF-8"));

					for (int c; (c = in.read()) >= 0;)
						responseBuilder.append((char) c);
				}
			}

			logger.info("SMS Response - " + responseBuilder.toString());
		} catch (Exception exception) {
			logger.error("Exception in sending SMS at : " + mobile);
			logger.error(exception.getMessage());
		}

	}

	@SuppressWarnings({ "unchecked", "static-access" })
	public String sendInvoiceSuccessTxnSms(String mobile, String customerName, String merchnatBusinessName,
			String amount, String orderNo, String pgRefNo, String prodName, String currencyCode, String message)
			throws IOException {

		String smsUrl = PropertiesManager.propertiesMap.get(Constants.SMS_URL.getValue());
		String senderId = PropertiesManager.propertiesMap.get(Constants.SENDER_ID.getValue());
		String senderPassword = PropertiesManager.propertiesMap.get(Constants.SENDER_PASSWORD.getValue());
		String senderName = PropertiesManager.propertiesMap.get(Constants.SENDER_NAME.getValue());

		Amount af = new Amount();
		String actualAmount = amount;
		amount = af.toDecimal(actualAmount, currencyCode);

		String smsMessageBody = "Dear," + customerName + " your Invoice Transaction on " + merchnatBusinessName
				+ " for " + prodName + " is " + message + ". Your Invoice Number: " + orderNo + " Total Amount: "
				+ amount + " PG_REF_NO: " + pgRefNo + " ThankYou ";
		StringBuilder xmlString = new StringBuilder();
		xmlString.append("<?xml version=\"1.0\" encoding=\"ISO-8859-1\"?>");
		xmlString.append("<!DOCTYPE MESSAGE SYSTEM \"https://api.myvaluefirst.com/psms/dtd/messagev12.dtd\">");
		xmlString.append("<MESSAGE VER=\"1.2\">");
		xmlString.append("<USER USERNAME=\"" + senderId + "\" PASSWORD=\"" + senderPassword + "\" />");
		xmlString.append("<SMS TEXT=\"" + smsMessageBody + "\"  ID=\"1\">");
		xmlString.append("<ADDRESS FROM=\"" + senderName + "\" TO=\"" + mobile + "\" SEQ=\"1\" />");
		xmlString.append("</SMS>");
		xmlString.append("</MESSAGE>");

		StringBuilder responseBuilder = new StringBuilder();
		try {
			HttpClient httpclient = HttpClients.createDefault();
			HttpPost httppost = new HttpPost(smsUrl);
			List<NameValuePair> params = new ArrayList<NameValuePair>(2);
			params.add(new BasicNameValuePair("data", xmlString.toString()));
			params.add(new BasicNameValuePair("action", "send"));
			httppost.setEntity(new UrlEncodedFormEntity(params, "UTF-8"));

			// Execute and get the response.
			HttpResponse response = httpclient.execute(httppost);
			HttpEntity entity = response.getEntity();

			if (entity != null) {
				try (InputStream instream = entity.getContent()) {

					Reader in = new BufferedReader(new InputStreamReader(instream, "UTF-8"));

					for (int c; (c = in.read()) >= 0;)
						responseBuilder.append((char) c);
				}
			}

			logger.info("SMS Response - " + responseBuilder.toString());
		} catch (Exception exception) {
			logger.error("Exception in sending SMS at : " + mobile);
			logger.error(exception.getMessage());
			return null;
		}
		return "success";

	}

	// deepak
	// ResponseSMS For Transaction Status To Customer

	public String sendSmsKaleyra(Fields fields, User user) {
		logger.info("Inside sendSmsKaleyra()");
		// propertiesManager.propertiesMap.get(acquirer);

		String endpoint = PropertiesManager.propertiesMap.get(Constants.SMS_URL.getValue());
		String sender = PropertiesManager.propertiesMap.get(Constants.SENDER_ID.getValue());
		String api_key = PropertiesManager.propertiesMap.get(Constants.SENDER_PASSWORD.getValue());
		// String senderName
		// =PropertiesManager.propertiesMap.get(Constants.SENDER_NAME.getValue());
		String method = "sms";
		// SmsMessageCreater smsMessageCreater=new SmsMessageCreater ();
		String message = createResponseSMS(fields, user);
		// logger.info("message sender api##################"+message);
		// String message="Hi, Your transaction is successfully completed with payment
		// Id <<txnid>> of amount Rs <<amount>>. Please keep the details for future
		// reference. Team Bhartipay.";
		// String endpoint="https://api-alerts.kaleyra.com/v4/";
		// String api_key="A675ff1f8beb7167542504f5d1ae0fa2f";
		// String method="sms";
		// String sender="BRTPAY";

		HttpPost httpPost = null;
		List<org.apache.http.NameValuePair> param = new LinkedList<>();
		param.add(new BasicNameValuePair("api_key", api_key));
		param.add(new BasicNameValuePair("method", method));
		param.add(new BasicNameValuePair("message", message));
		param.add(new BasicNameValuePair("to", fields.get(FieldType.CUST_PHONE.getName())));
		param.add(new BasicNameValuePair("sender", sender));
		logger.info("Param Value : " + param);
		String queryString = URLEncodedUtils.format(param, "UTF-8");
		String finalEndpoint = endpoint + "?" + queryString;
		logger.info("finalEndpoint : " + finalEndpoint);
		HttpResponse httpResponse = null;
		try {
			httpPost = new HttpPost(finalEndpoint);
			// org.apache.http.client.HttpClient
			// http4Client=PoolingManager.INSTANCE.getHttpClient();
			DefaultHttpClient http4Client = new DefaultHttpClient();

			httpResponse = http4Client.execute(httpPost);
			boolean is2xx = httpResponse.getStatusLine().getStatusCode() >= 200
					&& httpResponse.getStatusLine().getStatusCode() < 300;
			logger.info("Http Response after calling SMS URL : " + is2xx);
			if (is2xx) {
				String result = IOUtils.toString(httpResponse.getEntity().getContent(), "UTF-8");
				logger.info("Response From SMS : " + result);
				return result;
			}
		} catch (Exception e) {
			logger.error("Exception occur in sendSmsKaleyra() :" + e);
			e.printStackTrace();
		} finally {

			if (httpResponse != null && httpResponse.getEntity() != null) {
				EntityUtils.consumeQuietly(httpResponse.getEntity());
			}
			try {
				httpPost.releaseConnection();
			} catch (Exception e2) {
				logger.error("Exception occur in sendSmsKaleyra() e2 :" + e2);
				e2.printStackTrace();
			}
		}
		return "";
	}

	// Added By Sweety
	public String sendSmsToMerchant(Fields fields, User user) {
		logger.info("Inside sendSmsToMerchant()");
		logger.info("Inside sendSmsToMerchant() Merchant Mobile No={}",user.getMobile());

		String endpoint = PropertiesManager.propertiesMap.get(Constants.SMS_URL.getValue());
		String sender = PropertiesManager.propertiesMap.get(Constants.SENDER_ID.getValue());
		String api_key = PropertiesManager.propertiesMap.get(Constants.SENDER_PASSWORD.getValue());
		String method = "sms";
		String message = createResponseSMS(fields, user);
		
		HttpPost httpPost = null;
		List<org.apache.http.NameValuePair> param = new LinkedList<>();
		param.add(new BasicNameValuePair("api_key", api_key));
		param.add(new BasicNameValuePair("method", method));
		param.add(new BasicNameValuePair("message", message));
		param.add(new BasicNameValuePair("to",user.getMobile()));
		param.add(new BasicNameValuePair("sender", sender));
		logger.info("Param Value : " + param);
		String queryString = URLEncodedUtils.format(param, "UTF-8");
		String finalEndpoint = endpoint + "?" + queryString;
		logger.info("finalEndpoint : " + finalEndpoint);
		HttpResponse httpResponse = null;
		try {
			httpPost = new HttpPost(finalEndpoint);
			// org.apache.http.client.HttpClient
			// http4Client=PoolingManager.INSTANCE.getHttpClient();
			DefaultHttpClient http4Client = new DefaultHttpClient();

			httpResponse = http4Client.execute(httpPost);
			boolean is2xx = httpResponse.getStatusLine().getStatusCode() >= 200
					&& httpResponse.getStatusLine().getStatusCode() < 300;
			logger.info("Http Response after calling SMS URL : " + is2xx);
			if (is2xx) {
				String result = IOUtils.toString(httpResponse.getEntity().getContent(), "UTF-8");
				logger.info("Response From SMS : " + result);
				return result;
			}
		} catch (Exception e) {
			logger.error("Exception occur in sendSmsKaleyra() :" + e);
			e.printStackTrace();
		} finally {

			if (httpResponse != null && httpResponse.getEntity() != null) {
				EntityUtils.consumeQuietly(httpResponse.getEntity());
			}
			try {
				httpPost.releaseConnection();
			} catch (Exception e2) {
				logger.error("Exception occur in sendSmsKaleyra() e2 :" + e2);
				e2.printStackTrace();
			}
		}
		return "";
	} // DEEPAK
		// ResponseSMS For Transaction Status To Customer

	public String createResponseSMS(Fields fields, User user) {

		Session session = HibernateSessionProvider.getSession();
		Criteria criteria = session.createCriteria(User.class);
		criteria.add(Restrictions.eq("payId", fields.get(FieldType.PAY_ID.getName())));
		System.out.println("User List Size =" + criteria.list().size());
		User user2 = (User) criteria.uniqueResult();
		String ALIAS_NAME = user2.getAliasName();
		System.out.println("user2.getBusinessName()  " + user2.getBusinessName());
		String BUSINESS_NAME = user2.getBusinessName();
		// System.out.println(user2.getAliasName());
		// String BUSINESS_NAME=fields.get(CrmFieldType.BUSINESS_NAME.getName());
		// String aliasName=fields.get(CrmFieldType.ALIAS_NAME.getName());
		logger.info("ALIAS_NAME" + ALIAS_NAME);
		logger.info("user2.getAliasName()" + user2.getAliasName());
		logger.info("BUSINESS_NAME" + BUSINESS_NAME);

		StringBuilder message = new StringBuilder();
		if (!StringUtils.isEmpty(ALIAS_NAME)) {
			try {
				logger.info("inside  ALIAS_NAME");
				message.append("Hi, Your transaction on Merchant Name ");
				message.append(ALIAS_NAME);
				message.append(" is successfully completed with payment Id ");
				// message.append("Hi, Your transaction is successfully completed with payment
				// Id ");
				message.append(fields.get(FieldType.TXN_ID.getName()));
				message.append(" of amount Rs");
				message.append(Amount.toDecimal(fields.get(FieldType.AMOUNT.getName()),
						fields.get(FieldType.CURRENCY_CODE.getName())));
				message.append(". Please keep the details for future reference. Team Bhartipay.");

				// message.append("Hi, Your transaction on Merchant Name {#var#} is successfully
				// completed with payment Id {#var#} of amount Rs {#var#}. Please keep the
				// details for future reference. Team Bhartipay.");
			} catch (Exception exception) {
				logger.error("Exception", exception);
			}
		}
		if (StringUtils.isEmpty(ALIAS_NAME) && !StringUtils.isEmpty(BUSINESS_NAME)) {

			try {
				logger.info("Inside BUSINESS_NAME" + BUSINESS_NAME);
				message.append("Hi, Your transaction on Merchant Name ");
				message.append(BUSINESS_NAME);
				message.append(" is successfully completed with payment Id ");
				message.append(fields.get(FieldType.TXN_ID.getName()));
				message.append(" of amount Rs");
				message.append(Amount.toDecimal(fields.get(FieldType.AMOUNT.getName()),
						fields.get(FieldType.CURRENCY_CODE.getName())));
				message.append(". Please keep the details for future reference. Team Bhartipay.");

			} catch (Exception exception) {
				logger.error("Exception", exception);
			}
		}
		logger.info("message.toString()" + message);
		return message.toString();
	}

	// send Invoice for payment through SMS
	public String sendSmsKaleyraInvoice(String mobile, String url, String merchant, String amount, String invoiceNo) {
		logger.info("Inside sendSmsKaleyraInvoiceD()");
		String endpoint = PropertiesManager.propertiesMap.get(Constants.SMS_URL.getValue());
		String sender = PropertiesManager.propertiesMap.get(Constants.SENDER_ID.getValue());
		String api_key = PropertiesManager.propertiesMap.get(Constants.SENDER_PASSWORD.getValue());
		String method = "sms";
		String message = createResponseSMSForInvoice(mobile, url, merchant, amount, invoiceNo);
		logger.info("template for invoice message={}", message);

		HttpPost httpPost = null;
		List<org.apache.http.NameValuePair> param = new LinkedList<>();
		param.add(new BasicNameValuePair("api_key", api_key));
		param.add(new BasicNameValuePair("method", method));
		param.add(new BasicNameValuePair("message", message));
		param.add(new BasicNameValuePair("to", mobile));
		param.add(new BasicNameValuePair("sender", sender));
		logger.info("Param Value for Invoice: " + param);
		String queryString = URLEncodedUtils.format(param, "UTF-8");
		String finalEndpoint = endpoint + "?" + queryString;
		logger.info("finalEndpoint Invoice : " + finalEndpoint);
		HttpResponse httpResponse = null;
		try {
			httpPost = new HttpPost(finalEndpoint);
			DefaultHttpClient http4Client = new DefaultHttpClient();

			httpResponse = http4Client.execute(httpPost);
			boolean is2xx = httpResponse.getStatusLine().getStatusCode() >= 200
					&& httpResponse.getStatusLine().getStatusCode() < 300;
			logger.info("Http Response for Invoice after calling SMS URL : " + is2xx);
			if (is2xx) {
				String result = IOUtils.toString(httpResponse.getEntity().getContent(), "UTF-8");
				logger.info("Response From SMS Invoice : " + result);
				return result;
			}
		} catch (Exception e) {
			logger.error("Exception occur in sendSmsKaleyra() Invoice :" + e);
		} finally {

			if (httpResponse != null && httpResponse.getEntity() != null) {
				EntityUtils.consumeQuietly(httpResponse.getEntity());
			}
			try {
				httpPost.releaseConnection();
			} catch (Exception e2) {
				logger.error("Exception occur in sendSmsKaleyra() e2 invoice :" + e2);
			}
		}
		return "";
	}

	public String createResponseSMSForInvoice(String mobile, String url, String merchant, String amount,
			String invoiceNo) {

		StringBuilder message = new StringBuilder();

		// send message to customer
		if (!StringUtils.isEmpty(merchant)) {
			try {

				urlProvider.setAliasName(String.valueOf(System.currentTimeMillis()));
				String url1 = urlProvider.ShortUrl(url);
				message.append("Dear Customer, Payment of Rs " + amount + " is requested by " + merchant
						+ ". You can pay online by clicking on " + url1 + ". Powered by Bhartipay.");
			} catch (Exception exception) {
				logger.error("Exception", exception);
			}
		}
		logger.info("message.toString()" + message);
		return message.toString();
	}

}
