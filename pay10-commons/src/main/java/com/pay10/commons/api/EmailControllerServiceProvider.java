package com.pay10.commons.api;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.MapType;
import com.pay10.commons.user.AcquirerDownTimeConfiguration;
import com.pay10.commons.user.MerchantSignupNotifier;
import com.pay10.commons.user.ResponseObject;
import com.pay10.commons.user.User;
import com.pay10.commons.util.Amount;
import com.pay10.commons.util.Constants;
import com.pay10.commons.util.PropertiesManager;

@Service("emailControllerServiceProvider")
public class EmailControllerServiceProvider {

	@Autowired
	private PropertiesManager propertiesManager;

	@Autowired
	private VelocityEmailer emailer;
	// PropertiesManager propertiesManager = new PropertiesManager();

	private static Logger logger = LoggerFactory.getLogger(EmailControllerServiceProvider.class.getName());

	public void emailValidator(ResponseObject responseObject) {
		try {

			String serviceUrl = PropertiesManager.propertiesMap.get("EmailServiceEmailValidatorURL");

			JSONObject json = new JSONObject();
			json.put(Constants.ACCOUNT_VALIDATE_ID.getValue(), responseObject.getAccountValidationID());
			json.put(Constants.EMAIL.getValue(), responseObject.getEmail());
			CloseableHttpClient httpClient = HttpClientBuilder.create().build();
			HttpPost request = new HttpPost(serviceUrl);
			StringEntity params = new StringEntity(json.toString());
			request.addHeader(Constants.CONTENT_TYPE.getValue(), Constants.APPLICATION_JSON.getValue());
			request.setEntity(params);
			HttpResponse resp = httpClient.execute(request);
			StatusLine statusLine = resp.getStatusLine();
			int statusCode = resp.getStatusLine().getStatusCode();
			System.out.println("request sent  resp code" + statusCode);

		} catch (Exception exception) {
			logger.error("Exception in email validator.");
			logger.error(exception.getMessage());
		}
	}

	public void addUser(ResponseObject responseObject, String firstName) {
		try {

			logger.info("Sending add user email");
			String serviceUrl = PropertiesManager.propertiesMap.get("EmailServiceEmailAddUserURL");
			JSONObject json = new JSONObject();
			json.put(Constants.ACCOUNT_VALIDATE_ID.getValue(), responseObject.getAccountValidationID());
			json.put(Constants.EMAIL.getValue(), responseObject.getEmail());
			json.put(Constants.NAME.getValue(), firstName);
			String url = serviceUrl;

			logger.info("Sending add user email , service url is " + url);
			logger.info("JSON is " + json);
			CloseableHttpClient httpClient = HttpClientBuilder.create().build();
			HttpPost request = new HttpPost(url);
			StringEntity params = new StringEntity(json.toString());
			request.addHeader(Constants.CONTENT_TYPE.getValue(), Constants.APPLICATION_JSON.getValue());
			request.setEntity(params);
			HttpResponse resp = httpClient.execute(request);
			StatusLine statusLine = resp.getStatusLine();
			int statusCode = resp.getStatusLine().getStatusCode();
		} catch (Exception exception) {
			logger.error("error" + exception);
		}

	}

	public void merchantTransactionDetails(ResponseObject responseObject, String firstName) {
		try {

			logger.info("Sending merchant Transaction Details email");
			String serviceUrl = PropertiesManager.propertiesMap.get("EmailServiceEmailMerchantTransactionDetailsURL");
			JSONObject json = new JSONObject();
			json.put(Constants.EMAIL.getValue(), responseObject.getEmail());
			json.put(Constants.PAYID.getValue(), responseObject.getPayId());
			json.put(Constants.SALT.getValue(), responseObject.getSalt());
			json.put(Constants.REQUEST_URL.getValue(), responseObject.getRequestUrl());
			json.put(Constants.MERCHANT_HOSTED_ENCRYPTION_KEY.getValue(),  responseObject.getMerchantHostedEncryptionKey());
			json.put(Constants.NAME.getValue(), firstName);
			String url = serviceUrl;

			logger.info("Sending merchant Transaction Details , service url is " + url);
			logger.info("JSON is " + json);
			CloseableHttpClient httpClient = HttpClientBuilder.create().build();
			HttpPost request = new HttpPost(url);
			StringEntity params = new StringEntity(json.toString());
			request.addHeader(Constants.CONTENT_TYPE.getValue(), Constants.APPLICATION_JSON.getValue());
			request.setEntity(params);
			HttpResponse resp = httpClient.execute(request);
			StatusLine statusLine = resp.getStatusLine();
			int statusCode = resp.getStatusLine().getStatusCode();
		} catch (Exception exception) {
			logger.error("error" + exception);
		}

	}

	public void passwordChange(ResponseObject responseObject, String emailId) {
		try {

			String servicesUrl = PropertiesManager.propertiesMap.get("EmailServicePasswordChangeURL");
			JSONObject json = new JSONObject();
			json.put(Constants.ACCOUNT_VALIDATE_ID.getValue(), responseObject.getAccountValidationID());
			json.put(Constants.EMAIL.getValue(), responseObject.getEmail());
			String url = servicesUrl + emailId;
			CloseableHttpClient httpClient = HttpClientBuilder.create().build();
			HttpPost request = new HttpPost(url);
			request.toString().concat(emailId);
			StringEntity params = new StringEntity(json.toString());
			request.addHeader(Constants.CONTENT_TYPE.getValue(), Constants.APPLICATION_JSON.getValue());
			request.setEntity(params);
			HttpResponse resp = httpClient.execute(request);
			StatusLine statusLine = resp.getStatusLine();
			int statusCode = resp.getStatusLine().getStatusCode();
		} catch (Exception exception) {
			logger.error("error" + exception);
		}

	}

	public void passwordReset(String accountValidationKey, String emailId) {
		try {
			String serviceUrl = PropertiesManager.propertiesMap.get("EmailServiceResetPasswordURL");
			logger.info("EmailServiceResetPasswordURL....={}",serviceUrl);
			StringBuilder uri = new StringBuilder();
			uri.append(serviceUrl);
			Map<String, Object> params = new LinkedHashMap<>();
			params.put(Constants.ACCOUNT_VALIDATE_ID.getValue(), accountValidationKey);
			params.put(Constants.EMAIL.getValue(), emailId);

			StringBuilder postData = new StringBuilder();
			for (Map.Entry<String, Object> param : params.entrySet()) {
				if (postData.length() != 0)
					postData.append("&");

				postData.append(URLEncoder.encode(param.getKey(), "UTF-8"));
				postData.append('=');
				postData.append(URLEncoder.encode(String.valueOf(param.getValue()), "UTF-8"));
			}

			uri.append(postData);
			URL url = new URL(uri.toString());

			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setRequestMethod("POST");
			conn.setRequestProperty(Constants.CONTENT_TYPE.getValue(), Constants.APPLICATION_JSON.getValue());
			conn.setDoOutput(true);

			BufferedReader in = new BufferedReader(
					new InputStreamReader(conn.getInputStream(), Charset.forName("UTF-8")));

			StringBuilder res = new StringBuilder();
			for (String c; (c = in.readLine()) != null;) {
				res.append(c);
			}
			res.toString();

		} catch (Exception exception) {
			logger.error("error" + exception);
		}
	}

	public void invoiceLink(String url, String emailId, String name, String merchant) {
		String responseBody = "";
		Map<String, String> resMap = new HashMap<String, String>();
		try {
			logger.info("Sending invoice link email at : " + emailId);
			String serviceUrl = PropertiesManager.propertiesMap.get("EmailServiceInvoiceLinkURL");

			JSONObject json = new JSONObject();
			json.put(Constants.URL.getValue(), url);
			json.put(Constants.EMAILID.getValue(), emailId);
			json.put(Constants.NAME.getValue(), name);
			json.put(Constants.MERCHANT.getValue(), merchant);

			CloseableHttpClient httpClient = HttpClientBuilder.create().build();
			HttpPost request = new HttpPost(serviceUrl);
			StringEntity params = new StringEntity(json.toString());
			request.addHeader("Content-type", "application/json");
			request.setEntity(params);
			CloseableHttpResponse resp = httpClient.execute(request);
			responseBody = EntityUtils.toString(resp.getEntity());
			final ObjectMapper mapper = new ObjectMapper();
			final MapType type = mapper.getTypeFactory().constructMapType(Map.class, String.class, Object.class);
			resMap = mapper.readValue(responseBody, type);
			logger.info("Response map after sending Email : " + resMap);

		} catch (Exception exception) {
			logger.error("Error while sending email at : " + emailId);
			logger.error(exception.getMessage());
		}

	}

	public void InvoiceSuccessTxnsNotification(String emailID, String subject, String customerName,
											   String merchnatBusinessName, String amount, String currencyCode, String invoiceNo) {
		String responseBody = "";
		Map<String, String> resMaps = new HashMap<String, String>();
		try {
			logger.info("Send invoice success email to customer at : " + emailID);
			String actualAmount = amount;
			amount = Amount.toDecimal(actualAmount, currencyCode);

			String serviceUrl = PropertiesManager.propertiesMap.get("EmailServiceInvoiceSuccessTxnsNotification");

			JSONObject json = new JSONObject();
			json.put(Constants.EMAILID.getValue(), emailID);
			json.put(Constants.SUBJECT.getValue(), subject);
			json.put(Constants.MERCHANT.getValue(), customerName);
			json.put(Constants.BUSINESS_NAME.getValue(), merchnatBusinessName);
			json.put(Constants.TOTAL_AMOUNT.getValue(), amount);
			json.put(Constants.INVOICE_CURRENCY_CODE.getValue(), currencyCode);
			json.put(Constants.INVOICE_NO.getValue(), invoiceNo);

			CloseableHttpClient httpClient = HttpClientBuilder.create().build();
			HttpPost request = new HttpPost(serviceUrl);
			StringEntity params = new StringEntity(json.toString());
			request.addHeader("Content-type", "application/json");
			request.setEntity(params);
			CloseableHttpResponse resp = httpClient.execute(request);
			responseBody = EntityUtils.toString(resp.getEntity());
			final ObjectMapper mapper = new ObjectMapper();
			final MapType type = mapper.getTypeFactory().constructMapType(Map.class, String.class, Object.class);
			resMaps = mapper.readValue(responseBody, type);
			logger.info("Reponse Map after sending email to customer at : " + emailID);
		} catch (Exception exception) {
			logger.error("Exception while sending email to customer at : " + emailID);
			logger.error(exception.getMessage());
		}

	}

	public void invoiceSuccessTxnsNotificationToMerchant(String emailID, String subject, String customerName,
														 String merchnatBusinessName, String amount, String currencyCode, String invoiceNo, String orderId,
														 String pgRefNum, String custPhone) {
		String responseBody = "";
		Map<String, String> resMaps = null;
		try {
			logger.info("Send invoice success email to merchant at : " + emailID);
//			String actualAmount = amount;
//			amount = Amount.toDecimal(actualAmount, currencyCode);

			String serviceUrl = PropertiesManager.propertiesMap
					.get("EmailServiceInvoiceSuccessTxnsNotificationMerchant");

			JSONObject json = new JSONObject();
			json.put(Constants.EMAILID.getValue(), emailID);
			json.put(Constants.SUBJECT.getValue(), subject);
			json.put(Constants.MERCHANT.getValue(), customerName);
			json.put(Constants.BUSINESS_NAME.getValue(), merchnatBusinessName);
			json.put(Constants.TOTAL_AMOUNT.getValue(), amount);
			json.put(Constants.INVOICE_CURRENCY_CODE.getValue(), currencyCode);
			json.put(Constants.INVOICE_NO.getValue(), invoiceNo);
			json.put(Constants.ORDER_ID.getValue(), orderId);
			json.put(Constants.PG_REF_NUM.getValue(), pgRefNum);
			json.put(Constants.PHONE.getValue(), custPhone);

			CloseableHttpClient httpClient = HttpClientBuilder.create().build();
			HttpPost request = new HttpPost(serviceUrl);
			StringEntity params = new StringEntity(json.toString());
			request.addHeader("Content-type", "application/json");
			request.setEntity(params);
			CloseableHttpResponse resp = httpClient.execute(request);
			responseBody = EntityUtils.toString(resp.getEntity());
			final ObjectMapper mapper = new ObjectMapper();
			final MapType type = mapper.getTypeFactory().constructMapType(Map.class, String.class, Object.class);
			resMaps = mapper.readValue(responseBody, type);
			logger.info("Reponse Map after sending email to merchant at : " + emailID);
		} catch (Exception exception) {
			logger.error("Exception while sending email to merchant at : " + emailID);
			logger.error(exception.getMessage());
		}

	}

	public void remittanceProcess(String utr, String payId, String merchant, String datefrom, String netAmount,
								  String remittedDate, String remittedAmount, String status) {
		try {
			String serviceUrl = PropertiesManager.propertiesMap.get("EmailServiceRemittanceProcessURL");
			StringBuilder uri = new StringBuilder();
			uri.append(serviceUrl);
			Map<String, Object> params = new LinkedHashMap<>();
			params.put(Constants.UTR.getValue(), utr);
			params.put(Constants.PAYID.getValue(), payId);
			params.put(Constants.MERCHANT.getValue(), merchant);
			params.put(Constants.DATE_FROM.getValue(), datefrom);
			params.put(Constants.NET_AMOUNT.getValue(), netAmount);
			params.put(Constants.REMITTED_DATE.getValue(), remittedDate);
			params.put(Constants.REMITTED_AMOUNT.getValue(), remittedAmount);
			params.put(Constants.STATUS.getValue(), status);
			StringBuilder postData = new StringBuilder();
			for (Map.Entry<String, Object> param : params.entrySet()) {
				if (postData.length() != 0)
					postData.append("&");

				postData.append(URLEncoder.encode(param.getKey(), "UTF-8"));
				postData.append('=');
				postData.append(URLEncoder.encode(String.valueOf(param.getValue()), "UTF-8"));

			}

			URL urlreq = new URL(uri.toString());

			HttpURLConnection conn = (HttpURLConnection) urlreq.openConnection();
			conn.setRequestMethod("POST");
			conn.setRequestProperty(Constants.CONTENT_TYPE.getValue(), Constants.APPLICATION_JSON.getValue());
			conn.setDoOutput(true);

			BufferedReader in = new BufferedReader(
					new InputStreamReader(conn.getInputStream(), Charset.forName("UTF-8")));

			StringBuilder res = new StringBuilder();
			for (String c; (c = in.readLine()) != null;) {
				res.append(c);
			}
			res.toString();
		} catch (Exception exception) {
			logger.error("error" + exception);
		}

	}

	public void sendBulkEmailServiceTax(String emailID, String subject, String messageBody) {
		try {

			String serviceURL = PropertiesManager.propertiesMap.get("EmailServiceSendBulkEmailServiceTaxURL");
			StringBuilder uri = new StringBuilder();
			uri.append(serviceURL);
			Map<String, Object> params = new LinkedHashMap<>();
			params.put(Constants.EMAIL.getValue(), emailID);
			params.put(Constants.SUBJECT.getValue(), subject);
			params.put(Constants.MESSAGE_BODY.getValue(), messageBody);
			StringBuilder postData = new StringBuilder();
			for (Map.Entry<String, Object> param : params.entrySet()) {
				if (postData.length() != 0)
					postData.append("&");
				postData.append(URLEncoder.encode(param.getKey(), "UTF-8"));
				postData.append('=');
				postData.append(URLEncoder.encode(String.valueOf(param.getValue()), "UTF-8"));
			}
			uri.append(postData);
			URL url = new URL(uri.toString());

			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setRequestMethod("POST");
			conn.setRequestProperty(Constants.CONTENT_TYPE.getValue(), Constants.APPLICATION_JSON.getValue());
			conn.setDoOutput(true);

			BufferedReader in = new BufferedReader(
					new InputStreamReader(conn.getInputStream(), Charset.forName("UTF-8")));

			StringBuilder res = new StringBuilder();
			for (String c; (c = in.readLine()) != null;) {
				res.append(c);
			}

			res.toString();
		} catch (Exception exception) {
			logger.error("error" + exception);
		}

	}

	public void emailPendingRequest(String emailID, String subject, String messageBody) {
		try {
			logger.info("send emailPendingRequest");
			String serviceURL = PropertiesManager.propertiesMap.get("EmailServicePendingRequestURL");
			logger.info("send emailPendingRequest service url = " + serviceURL);
			StringBuilder uri = new StringBuilder();
			uri.append(serviceURL);
			Map<String, Object> params = new LinkedHashMap<>();
			params.put(Constants.EMAIL.getValue(), emailID);
			params.put(Constants.SUBJECT.getValue(), subject);
			params.put(Constants.MESSAGE_BODY.getValue(), messageBody);
			StringBuilder postData = new StringBuilder();
			for (Map.Entry<String, Object> param : params.entrySet()) {
				if (postData.length() != 0)
					postData.append("&");
				postData.append(URLEncoder.encode(param.getKey(), "UTF-8"));
				postData.append('=');
				postData.append(URLEncoder.encode(String.valueOf(param.getValue()), "UTF-8"));
			}
			uri.append(postData);
			logger.info("uri.toString() = " + uri.toString());
			URL url = new URL(uri.toString());

			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setRequestMethod("POST");
			conn.setRequestProperty(Constants.CONTENT_TYPE.getValue(), Constants.APPLICATION_JSON.getValue());
			conn.setDoOutput(true);

			BufferedReader in = new BufferedReader(
					new InputStreamReader(conn.getInputStream(), Charset.forName("UTF-8")));

			StringBuilder res = new StringBuilder();
			for (String c; (c = in.readLine()) != null;) {
				res.append(c);
			}

			res.toString();
		} catch (Exception exception) {
			logger.error("error" + exception);
		}

	}

	public void failedTxnsNotification(String emailID, String subject, String customerName, String merchnatBusinessName,
									   String amount, String messageBody, String currencyCode) {

		String responseBody = "";
		Map<String, String> resMap = new HashMap<String, String>();
		try {
			logger.info("send failedTxnsNotification");
			String serviceUrl = PropertiesManager.propertiesMap.get("EmailServiceFailedTxnsNotificationURL");

			JSONObject json = new JSONObject();
			json.put(Constants.EMAILID.getValue(), emailID);
			json.put(Constants.SUBJECT.getValue(), subject);
			json.put(Constants.MERCHANT.getValue(), customerName);
			json.put(Constants.BUSINESS_NAME.getValue(), merchnatBusinessName);
			json.put(Constants.TOTAL_AMOUNT.getValue(), amount);
			json.put(Constants.MESSAGE_BODY.getValue(), messageBody);
			json.put(Constants.INVOICE_CURRENCY_CODE.getValue(), currencyCode);

			CloseableHttpClient httpClient = HttpClientBuilder.create().build();
			HttpPost request = new HttpPost(serviceUrl);
			StringEntity params = new StringEntity(json.toString());
			request.addHeader("Content-type", "application/json");
			request.setEntity(params);
			CloseableHttpResponse resp = httpClient.execute(request);
			responseBody = EntityUtils.toString(resp.getEntity());
			final ObjectMapper mapper = new ObjectMapper();
			final MapType type = mapper.getTypeFactory().constructMapType(Map.class, String.class, Object.class);
			resMap = mapper.readValue(responseBody, type);

		} catch (Exception exception) {
			logger.error("error" + exception);
		}

	}

	public void passwordResetConfirmationEmail(User user) {

		String responseBody = "";
		Map<String, String> resMap = new HashMap<String, String>();
		try {
			logger.info("send Password Reset Confirmation Email");
			String serviceUrl = PropertiesManager.propertiesMap.get("EmailServiceResetPasswordConfirmationURL");
			String customerSupportEmail = PropertiesManager.propertiesMap.get("COMPANY_CUSTOMER_SUPPORT_EMAIL");
			String messageBody = "This is to inform you that the password for your Merchant Panel Login has been reset successfully using email id "
					+ user.getEmailId()
					+ ", In case this has not been done by you please report immediately at our customer support email is "
					+ customerSupportEmail;
			JSONObject json = new JSONObject();
			json.put(Constants.EMAILID.getValue(), user.getEmailId());
			json.put(Constants.SUBJECT.getValue(), "Password Reset on BPGATE");
			json.put(Constants.NAME.getValue(), user.getLastName());
			json.put(Constants.BUSINESS_NAME.getValue(), user.getBusinessName());
			json.put(Constants.MESSAGE_BODY.getValue(), messageBody);

			CloseableHttpClient httpClient = HttpClientBuilder.create().build();
			HttpPost request = new HttpPost(serviceUrl);
			StringEntity params = new StringEntity(json.toString());
			request.addHeader("Content-type", "application/json");
			request.setEntity(params);
			CloseableHttpResponse resp = httpClient.execute(request);
			responseBody = EntityUtils.toString(resp.getEntity());
//			final ObjectMapper mapper = new ObjectMapper();
//			final MapType type = mapper.getTypeFactory().constructMapType(Map.class, String.class, Object.class);
//			resMap = mapper.readValue(responseBody, type);

		} catch (Exception exception) {
			logger.error("error" + exception);
		}

	}

	public void passwordResetSecurityAlertEmail(User user) {

		String responseBody = "";
		Map<String, String> resMap = new HashMap<String, String>();
		try {
			logger.info("send Password Reset Security Alert Email");
			String serviceUrl = PropertiesManager.propertiesMap.get("EmailServiceResetPasswordSecurityAlertURL");
			String customerSupportEmail = PropertiesManager.propertiesMap.get("COMPANY_CUSTOMER_SUPPORT_EMAIL");
			DateFormat df = new SimpleDateFormat("dd/MM/yy HH:mm:ss");
			String messageBody = "<br /> Greetings From BPGATE !! <br /><br /> This is to inform you that someone generated a password reset request for your  Merchant Panel using email id "
					+ user.getEmailId() + "  on " + df.format(new Date())
					+ ", If it was not you please contact us at our customer support email is " + customerSupportEmail;
			JSONObject json = new JSONObject();
			json.put(Constants.EMAILID.getValue(), user.getEmailId());
			json.put(Constants.SUBJECT.getValue(), "Password Reset attempted");
			json.put(Constants.NAME.getValue(), user.getLastName());
			json.put(Constants.BUSINESS_NAME.getValue(), user.getBusinessName());
			json.put(Constants.MESSAGE_BODY.getValue(), messageBody);

			CloseableHttpClient httpClient = HttpClientBuilder.create().build();
			HttpPost request = new HttpPost(serviceUrl);
			StringEntity params = new StringEntity(json.toString());
			request.addHeader("Content-type", "application/json");
			request.setEntity(params);
			CloseableHttpResponse resp = httpClient.execute(request);
			responseBody = EntityUtils.toString(resp.getEntity());
			final ObjectMapper mapper = new ObjectMapper();
			final MapType type = mapper.getTypeFactory().constructMapType(Map.class, String.class, Object.class);
			resMap = mapper.readValue(responseBody, type);

		} catch (Exception exception) {
			logger.error("error" + exception);
		}

	}

	public void passwordChangeConfirmationEmail(User user) {

		String responseBody = "";
		Map<String, String> resMap = new HashMap<String, String>();
		try {
			logger.info("send Password Change Confirmation Email");
			String serviceUrl = PropertiesManager.propertiesMap.get("EmailServiceChangePasswordConfirmationURL");
			String customerSupportEmail = PropertiesManager.propertiesMap.get("COMPANY_CUSTOMER_SUPPORT_EMAIL");
			String messageBody = "Greetings From BPGATE !! <br /><br /> This is to inform you that your Merchant Panel Account password has been changed successfully using email id "
					+ user.getEmailId()
					+ ",Incase of any further clarifications please contact us at our customer support email is "
					+ customerSupportEmail;
			JSONObject json = new JSONObject();
			json.put(Constants.EMAILID.getValue(), user.getEmailId());
			json.put(Constants.SUBJECT.getValue(), "Password changed for BPGATE account");
			json.put(Constants.NAME.getValue(), user.getLastName());
			json.put(Constants.BUSINESS_NAME.getValue(), user.getBusinessName());
			json.put(Constants.MESSAGE_BODY.getValue(), messageBody);

			CloseableHttpClient httpClient = HttpClientBuilder.create().build();
			HttpPost request = new HttpPost(serviceUrl);
			StringEntity params = new StringEntity(json.toString());
			request.addHeader("Content-type", "application/json");
			request.setEntity(params);
			CloseableHttpResponse resp = httpClient.execute(request);
			responseBody = EntityUtils.toString(resp.getEntity());
			final ObjectMapper mapper = new ObjectMapper();
			final MapType type = mapper.getTypeFactory().constructMapType(Map.class, String.class, Object.class);
			resMap = mapper.readValue(responseBody, type);

		} catch (Exception exception) {
			logger.error("error" + exception);
		}

	}

	public void merchantSignupNotifier(MerchantSignupNotifier merchantSignupNotifier) {
		try {

			String serviceUrl = PropertiesManager.propertiesMap.get("EmailMerchantSignUpNotifier");
			JSONObject json = new JSONObject();
			json.put("merchantEmail", merchantSignupNotifier.getMerchantEmail());
			json.put("merchantPhone", merchantSignupNotifier.getMerchantPhone());
			json.put("merchantName", merchantSignupNotifier.getMerchantName());
			json.put("receiverName", merchantSignupNotifier.getReceiverName());
			json.put("email", merchantSignupNotifier.getEmail());
			CloseableHttpClient httpClient = HttpClientBuilder.create().build();
			HttpPost request = new HttpPost(serviceUrl);
			StringEntity params = new StringEntity(json.toString());
			request.addHeader(Constants.CONTENT_TYPE.getValue(), Constants.APPLICATION_JSON.getValue());
			request.setEntity(params);
			HttpResponse resp = httpClient.execute(request);
			StatusLine statusLine = resp.getStatusLine();
			int statusCode = resp.getStatusLine().getStatusCode();
			System.out.println("request sent  resp code" + statusCode);

		} catch (Exception exception) {
			logger.error("Exception in sending ni=otifier email ", exception);
		}
	}

	public void repeatedMopTypeEmail(List<String> emailIds, Map<String, Double> amountsByPaymentType,
									 String merchantName) {

		String responseBody = "";
		try {
			Map<String, String> resMap = new HashMap<String, String>();
			logger.info("Repeated MOP Types Notification Email");
			String serviceUrl = PropertiesManager.propertiesMap.get("EmailServiceRepeatedMopTypeURL");
			String messageBody = "Greetings From BPGATE !! <br /><br /> This is to inform you that Merchant"
					+ merchantName + " use below payment type repeatedly.<br /><br />";
			String table = "<table><thead><th>Payment Type</th><th>Percentage</th></thead>";
			for (Map.Entry<String, Double> entry : amountsByPaymentType.entrySet()) {
				String tr = "<tr>";
				String td = "<td>";
				tr = StringUtils.join(tr, td, entry.getKey(), "</td>", td, entry.getValue(), "</td></tr>");
				table = StringUtils.join(table, tr);
			}
			table = StringUtils.join(table, "</table>");
			messageBody = StringUtils.join(messageBody, table);
			for (String email : emailIds) {
				JSONObject json = new JSONObject();
				json.put(Constants.EMAILID.getValue(), email);
				json.put(Constants.SUBJECT.getValue(), "Repeated MOP types");
				json.put(Constants.MESSAGE_BODY.getValue(), messageBody);

				CloseableHttpClient httpClient = HttpClientBuilder.create().build();
				HttpPost request = new HttpPost(serviceUrl);
				StringEntity params = new StringEntity(json.toString());
				request.addHeader("Content-type", "application/json");
				request.setEntity(params);
				CloseableHttpResponse resp = httpClient.execute(request);
				responseBody = EntityUtils.toString(resp.getEntity());
				final ObjectMapper mapper = new ObjectMapper();
				final MapType type = mapper.getTypeFactory().constructMapType(Map.class, String.class, Object.class);
				resMap = mapper.readValue(responseBody, type);
				logger.info("repeatedMopTypeEmail:: resMap={}", resMap);
			}

		} catch (Exception exception) {
			logger.error("error" + exception);
		}

	}

	public void sendFirstTransactionEmail(String emailIds, String merchantName) {
		logger.info("sendFirstTransactionEmail:: initialized. businessName={}", merchantName);
		try {
			String serviceUrl = PropertiesManager.propertiesMap.get("EmailServiceFirstTransactionAlertURL");
			String responseBody = "";
			JSONObject json = new JSONObject();
			json.put(Constants.EMAILID.getValue(), emailIds);
			json.put("businessName", merchantName);
			CloseableHttpClient httpClient = HttpClientBuilder.create().build();
			HttpPost request = new HttpPost(serviceUrl);
			StringEntity params = new StringEntity(json.toString());
			request.addHeader("Content-type", "application/json");
			request.setEntity(params);
			CloseableHttpResponse resp = httpClient.execute(request);
			responseBody = EntityUtils.toString(resp.getEntity());
			final ObjectMapper mapper = new ObjectMapper();
			final MapType type = mapper.getTypeFactory().constructMapType(Map.class, String.class, Object.class);
			Map<String, String> resMap = mapper.readValue(responseBody, type);
			logger.info("sendFirstTransactionEmail:: resMap={}", resMap);
		} catch (Exception ex) {
			logger.error("sendFirstTransactionEmail:: failed. businessName={}", merchantName, ex);
		}
	}

	public void velociTyBlockingEmail(List<String> emailIds, String merchantName, File attachment, boolean block,
									  String condition) {

		try {
			logger.info("VelociTy Blocking Email");
			String conditionBreached = StringUtils.join(condition, " limit");
			String message = block ? "Block Notification!!!!" : "Alert Notification!!!!";
			String messageBody = StringUtils.join("<p style='color:red;' align='center'>", message, "</p>", "<br>");
			messageBody = StringUtils.join(messageBody,
					"<p>This is to inform you that the listed merchant has breached the <b>", conditionBreached,
					"</b></p>");
			messageBody = StringUtils.join(messageBody, "<p>Condition Breached: ", conditionBreached, "</p>");
			messageBody = StringUtils.join(messageBody, "<p>Merchant Name: ", merchantName, "</p>");
			String subject = block ? "Velocity Block Notifications" : "Velocity Alert Notifications";
			for (String email : emailIds) {
				emailer.sendEmailWithTextAndAttachment(messageBody, subject, email, null, false, attachment.getName(),
						attachment);
			}

		} catch (Exception exception) {
			logger.error("error" + exception);
		}

	}

	public void failedCntLimitExceedEmail(String acquirerName, String paymentType, String mopType) {
		String responseBody = "";
		Map<String, String> resMaps = null;
		try {

			String serviceUrl = PropertiesManager.propertiesMap.get("EmailServiceFailedCountLimitExceedAlert");

			JSONObject json = new JSONObject();
			json.put("paymentType", paymentType);
			json.put("mopType", mopType);
			json.put("acquirerName", acquirerName);

			CloseableHttpClient httpClient = HttpClientBuilder.create().build();
			HttpPost request = new HttpPost(serviceUrl);
			StringEntity params = new StringEntity(json.toString());
			request.addHeader("Content-type", "application/json");
			request.setEntity(params);
			CloseableHttpResponse resp = httpClient.execute(request);
			responseBody = EntityUtils.toString(resp.getEntity());
			final ObjectMapper mapper = new ObjectMapper();
			final MapType type = mapper.getTypeFactory().constructMapType(Map.class, String.class, Object.class);
			resMaps = mapper.readValue(responseBody, type);
			logger.info("Reponse Map after sending email to business team (failedCntLimitExceedEmail) : ");
		} catch (Exception exception) {
			logger.error("Exception while sending email to business team (failedCntLimitExceedEmail) : ");
			logger.error(exception.getMessage());
		}

	}

	public void sendFraudRuleEmail(Map<String, String> mailRequest) {
		logger.info("sendFraudRuleEmail, MerchantName={}", mailRequest.get("merchantName"));
		try {
			String serviceUrl = PropertiesManager.propertiesMap.get("EmailServiceFraudRuleBreakAlertURL");
			logger.info("sendFraudRuleEmail serviceUrl =  "+serviceUrl);
			String responseBody = "";
			JSONObject json = new JSONObject();
			json.put("merchantName", mailRequest.get("merchantName"));
			json.put("paymentType", mailRequest.get("paymentType"));
			json.put("orderId", mailRequest.get("orderId"));
			json.put("fraudType", mailRequest.get("fraudType"));
			logger.info("sendFraudRuleEmail JSON =  "+json);
			logger.info("sendFraudRuleEmail JSON =  "+json);
			CloseableHttpClient httpClient = HttpClientBuilder.create().build();
			HttpPost request = new HttpPost(serviceUrl);
			StringEntity params = new StringEntity(json.toString());
			request.addHeader("Content-type", "application/json");
			request.setEntity(params);
			CloseableHttpResponse resp = httpClient.execute(request);
			responseBody = EntityUtils.toString(resp.getEntity());
			final ObjectMapper mapper = new ObjectMapper();
			final MapType type = mapper.getTypeFactory().constructMapType(Map.class, String.class, Object.class);
			Map<String, String> resMap = mapper.readValue(responseBody, type);
			logger.info("sendFraudRuleEmail, resMap={}", resMap);
		} catch (Exception ex) {
			logger.error("sendFraudRuleEmail, MerchantName={}", mailRequest.get("merchantName"), ex);
		}
	}

	public void notifyToBusinessTeamAsAcquirerDown(AcquirerDownTimeConfiguration action, String downTime) {
		logger.info("notifyToBusinessTeamAsAcquirerDown, Acquirer={}, paymentType= {} ", action.getAcquirerName(),action.getPaymentType());
		try {
			String serviceUrl = PropertiesManager.propertiesMap.get("EmailServiceAcquirerDownURL");
			logger.info("NotifyToBusinessTeamAsAcquirerDown serviceUrl =  "+serviceUrl);
			String responseBody = "";
			JSONObject json = new JSONObject();
			json.put("aqcquirerName", action.getAcquirerName());
			json.put("paymentType", action.getPaymentType());
			json.put("failedCnt", action.getFailedCount());
			json.put("downTime", downTime);
			logger.info("NotifyToBusinessTeamAsAcquirerDown JSON =  "+json);

			CloseableHttpClient httpClient = HttpClientBuilder.create().build();
			HttpPost request = new HttpPost(serviceUrl);
			StringEntity params = new StringEntity(json.toString());
			request.addHeader("Content-type", "application/json");
			request.setEntity(params);
			CloseableHttpResponse resp = httpClient.execute(request);
			responseBody = EntityUtils.toString(resp.getEntity());
			final ObjectMapper mapper = new ObjectMapper();
			final MapType type = mapper.getTypeFactory().constructMapType(Map.class, String.class, Object.class);
			Map<String, String> resMap = mapper.readValue(responseBody, type);
			logger.info("NotifyToBusinessTeamAsAcquirerDown, resMap={}", resMap);
		} catch (Exception ex) {
			logger.error("NotifyToBusinessTeamAsAcquirerDown, Acquirer={}, paymentType= {}, Exception = {} ", action.getAcquirerName(), action.getPaymentType(), ex);
		}
	}
}
