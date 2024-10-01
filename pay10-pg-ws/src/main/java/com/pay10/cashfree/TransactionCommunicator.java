package com.pay10.cashfree;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.pay10.commons.exception.ErrorType;
import com.pay10.commons.exception.SystemException;
import com.pay10.commons.util.FieldType;
import com.pay10.commons.util.Fields;
import com.pay10.commons.util.PropertiesManager;
import com.pay10.commons.util.StatusType;
import com.pay10.commons.util.TransactionType;

@Service("cashfreeTransactionCommunicator")
public class TransactionCommunicator {

	private static Logger logger = LoggerFactory.getLogger(TransactionCommunicator.class.getName());

	public void updateSaleResponse(Fields fields, String request) {
		
		fields.put(FieldType.CASHFREE_FINAL_REQUEST.getName(), request);
		fields.put(FieldType.STATUS.getName(), StatusType.SENT_TO_BANK.getName());
		fields.put(FieldType.RESPONSE_CODE.getName(), ErrorType.SUCCESS.getResponseCode());
		fields.put(FieldType.RESPONSE_MESSAGE.getName(), ErrorType.SUCCESS.getResponseMessage());
	}

	@SuppressWarnings("incomplete-switch")
	public String getResponse(String request, Fields fields) throws SystemException {

		String hostUrl = "";
		String response = "";

		TransactionType transactionType = TransactionType.getInstance(fields.get(FieldType.TXNTYPE.getName()));
		switch (transactionType) {
		case SALE:
		case AUTHORISE:
			break;
		case ENROLL:
			break;
		case CAPTURE:
			break;
		case REFUND:
			//hostUrl = PropertiesManager.propertiesMap.get(Constants.REFUND_REQUEST_URL);
			//response = refundPostRequest(request, hostUrl);
			hostUrl = PropertiesManager.propertiesMap.get(Constants.REFUND_REQUEST_URL);
			response = refundPostRequest(request, hostUrl, fields);
			break;
		case STATUS:
			hostUrl = PropertiesManager.propertiesMap.get(Constants.STATUS_ENQ_REQUEST_URL);
			response = statusEnqPostRequest(request, hostUrl);
			break;
		}
		return response;

	}

	public String refundPostRequest(String request, String hostUrl) throws SystemException {
		String response = "";

		try {
			
			HttpURLConnection connection = null;
			URL url;
			url = new URL(hostUrl);
			connection = (HttpURLConnection) url.openConnection();
			connection.setRequestMethod("POST");
			connection.setRequestProperty("Content-Type","application/x-www-form-urlencoded");

			connection.setUseCaches(false);
			connection.setDoInput(true);
			connection.setDoOutput(true);
		    connection.setConnectTimeout(60000);
		    connection.setReadTimeout(60000);

			// Send request
			DataOutputStream wr = new DataOutputStream(
					connection.getOutputStream());
			wr.writeBytes(request);
			wr.flush();
			wr.close();

			// Get Response
			InputStream is = connection.getInputStream();
			
			BufferedReader bufferedreader = new BufferedReader(new InputStreamReader(is));
			String decodedString;
		
			while ((decodedString = bufferedreader.readLine()) != null) {
				response = response + decodedString;
			}
			
			bufferedreader.close();
			
			logger.info("Response for refund transaction >> " + response);

		} catch (Exception e) {
			logger.error("Exception in getting Refund respose for Cashfree", e);
			response = "{\"message\":\"Error in Refund.\",\"refundId\":NA,\"status\":\"FAILED\"}";
			return response;
		}
		return response;
	}

	public String refundPostRequest(String reqData, String hostUrl, Fields fields) throws SystemException {
		String responseBody = "";
		
		logger.info("Refund Request For Cashfree : "+reqData);
		logger.info("Refund URL For Cashfree :"+hostUrl);
		logger.info("Refund fields For Cashfree :"+fields.getFieldsAsString());
		hostUrl = hostUrl.replace("{order_id}", fields.get(FieldType.PG_REF_NUM.getName()));
		logger.info("Refund url For Cashfree after adding pgRefNo in url :"+hostUrl);
		try {

			CloseableHttpClient httpClient = HttpClientBuilder.create().build();
			HttpPost request = new HttpPost(hostUrl);
			StringEntity params = new StringEntity(reqData);
			request.addHeader("Accept", "application/json");
			request.addHeader("content-type", "application/json");
			request.addHeader("x-api-version", "2022-01-01");
			request.addHeader("x-client-id", fields.get(FieldType.MERCHANT_ID.getName()));
			request.addHeader("x-client-secret", fields.get(FieldType.TXN_KEY.getName()));
			
			request.setEntity(params);
			HttpResponse resp = httpClient.execute(request);
			responseBody = EntityUtils.toString(resp.getEntity());
			
			logger.info("responseBody "+responseBody);

		} catch (Exception exception) {
			logger.info("exception " + exception);
		}
		return responseBody;
	}
	
	public String statusEnqPostRequest(String request, String hostUrl) throws SystemException {
		String response = "";

		try {
			
			logger.info("Cashfree Status Enquiry Request " + request);
			HttpURLConnection connection = null;
			URL url;
			url = new URL(hostUrl);
			connection = (HttpURLConnection) url.openConnection();
			connection.setRequestMethod("POST");
			connection.setRequestProperty("Content-Type","application/x-www-form-urlencoded");

			connection.setUseCaches(false);
			connection.setDoInput(true);
			connection.setDoOutput(true);
		    connection.setConnectTimeout(60000);
		    connection.setReadTimeout(60000);

			// Send request
			DataOutputStream wr = new DataOutputStream(
					connection.getOutputStream());
			wr.writeBytes(request);
			wr.flush();
			wr.close();

			// Get Response
			InputStream is = connection.getInputStream();
			
			BufferedReader bufferedreader = new BufferedReader(new InputStreamReader(is));
			String decodedString;
		
			while ((decodedString = bufferedreader.readLine()) != null) {
				response = response + decodedString;
			}
			
			bufferedreader.close();
			
			logger.info("Response for Cashfree Status Enquiry transaction >> " + response);

		} catch (Exception e) {
			logger.error("Exception in getting Status Enquiry respose for Cashfree", e);
		}
		return response;
	}
	
	public String orderPostRequest(String reqData, String url, String appId, String secretKey) {

		logger.info("=================== reqData ============ "+reqData);
		logger.info("=================== url ============ "+url);
		logger.info("=================== appId ============ "+appId);
		logger.info("=================== secretKey ============ "+secretKey);
		String responseBody = "";
		try {

			CloseableHttpClient httpClient = HttpClientBuilder.create().build();
			HttpPost request = new HttpPost(url);
			StringEntity params = new StringEntity(reqData);
			request.addHeader("Accept", "application/json");
			request.addHeader("content-type", "application/json");
			request.addHeader("x-api-version", "2022-01-01");
			request.addHeader("x-client-id", appId);
			request.addHeader("x-client-secret", secretKey);
			
			request.setEntity(params);
			HttpResponse resp = httpClient.execute(request);
			responseBody = EntityUtils.toString(resp.getEntity());
			
			logger.info("responseBody "+responseBody);

		} catch (Exception exception) {
			System.out.println("exception " + exception);
		}
		return responseBody;
	}
	
	public String payOrderPostRequest(String reqData, String url) {

		String responseBody = "";
		try {

			CloseableHttpClient httpClient = HttpClientBuilder.create().build();
			HttpPost request = new HttpPost(url);
			StringEntity params = new StringEntity(reqData);
			request.addHeader("content-type", "application/json");
			request.addHeader("x-api-version", "2022-01-01");
			
			
			request.setEntity(params);
			HttpResponse resp = httpClient.execute(request);
			responseBody = EntityUtils.toString(resp.getEntity());

		} catch (Exception exception) {
			System.out.println("exception " + exception);
		}
		return responseBody;
	}
	
	public String getRefundStatus(String hostUrl, String merchantId, String secretKey, String refundOrderId, String pgRefNo) throws SystemException {
		String responseBody = "";
		
		logger.info("Refund Status Request URL For Cashfree :"+hostUrl);
		hostUrl = hostUrl.replace("{order_id}", pgRefNo);
		hostUrl = hostUrl.replace("{refund_id}", refundOrderId);
		logger.info("Refund Status Request url For Cashfree after adding pgRefNo in url :"+hostUrl);
		try {

			CloseableHttpClient httpClient = HttpClientBuilder.create().build();
			HttpGet request = new HttpGet(hostUrl);
			request.addHeader("Accept", "application/json");
			request.addHeader("content-type", "application/json");
			request.addHeader("x-api-version", "2022-01-01");
			request.addHeader("x-client-id", merchantId);
			request.addHeader("x-client-secret", secretKey);
			
			HttpResponse resp = httpClient.execute(request);
			responseBody = EntityUtils.toString(resp.getEntity());
			
			logger.info("responseBody "+responseBody);

		} catch (Exception exception) {
			logger.info("exception " + exception);
		}
		return responseBody;
	}

}
