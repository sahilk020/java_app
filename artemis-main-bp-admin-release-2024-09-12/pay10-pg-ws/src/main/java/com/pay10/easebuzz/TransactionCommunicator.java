package com.pay10.easebuzz;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import com.pay10.commons.exception.ErrorType;
import com.pay10.commons.exception.SystemException;
import com.pay10.commons.util.FieldType;
import com.pay10.commons.util.Fields;
import com.pay10.commons.util.PropertiesManager;
import com.pay10.commons.util.StatusType;
import com.pay10.commons.util.TransactionType;

@Service("easebuzzTransactionCommunicator")
public class TransactionCommunicator {

	private static Logger logger = LoggerFactory.getLogger(TransactionCommunicator.class.getName());

	@Autowired
	RestTemplate restTemplate;

	public void updateSaleResponse(Fields fields, String request) {

		fields.put(FieldType.EASEBUZZ_FINAL_REQUEST.getName(), request);
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
			hostUrl = PropertiesManager.propertiesMap.get(Constants.REFUND_REQUEST_URL);
			response = refundPostRequest(request, hostUrl);
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
			
logger.info("request of easebuzz refund  "+request+"        url  for refund   "+hostUrl);
			HttpURLConnection connection = null;
			URL url;
			url = new URL(hostUrl);
			connection = (HttpURLConnection) url.openConnection();
			connection.setRequestMethod("POST");
			connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
	connection.setUseCaches(false);
			connection.setDoInput(true);
			connection.setDoOutput(true);
			connection.setConnectTimeout(60000);
			connection.setReadTimeout(60000);

			// Send request
			DataOutputStream wr = new DataOutputStream(connection.getOutputStream());
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
			logger.error("Exception in getting Refund respose for easebuzz", e);
			response = "{\"message\":\"Error in Refund.\",\"refundId\":NA,\"status\":\"FAILED\"}";
			return response;
		}
		return response;
		
	}

	public String statusEnqPostRequest(String request, String hostUrl) throws SystemException {
		String response = "";

		try {

			logger.info("Easebuzz Status Enquiry Request " + request);
			HttpURLConnection connection = null;
			URL url;
			url = new URL(hostUrl);
			connection = (HttpURLConnection) url.openConnection();
			connection.setRequestMethod("POST");
			connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");

			connection.setUseCaches(false);
			connection.setDoInput(true);
			connection.setDoOutput(true);
			connection.setConnectTimeout(60000);
			connection.setReadTimeout(60000);

			// Send request
			DataOutputStream wr = new DataOutputStream(connection.getOutputStream());
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

			logger.info("Response for Easebuzz Status Enquiry transaction >> " + response);

		} catch (Exception e) {
			logger.error("Exception in getting Status Enquiry respose for Easebuzz", e);
		}
		return response;
	}

	public String doubleVeriPostRequest(MultiValueMap<String, String> finalRequest, String url) {

		logger.info("doubleVeriPostRequest finalRequest " + finalRequest.toString());
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

		HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<MultiValueMap<String, String>>(finalRequest,
				headers);
		ResponseEntity<String> response = restTemplate.postForEntity(url, request, String.class);
		logger.info("response " + response.toString());
		return response.getBody();
	}
	
	
	
	public static String refundPostRequest1(String request, String hostUrl) throws SystemException {
		String response = "";

		try {
logger.info("request of easebuzz refund  "+request+"        url  for refund   "+hostUrl);
			HttpURLConnection connection = null;
			URL url;
			url = new URL(hostUrl);
			connection = (HttpURLConnection) url.openConnection();
			connection.setRequestMethod("POST");
			connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
	connection.setUseCaches(false);
			connection.setDoInput(true);
			connection.setDoOutput(true);
			connection.setConnectTimeout(60000);
			connection.setReadTimeout(60000);

			// Send request
			DataOutputStream wr = new DataOutputStream(connection.getOutputStream());
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
			logger.error("Exception in getting Refund respose for easebuzz", e);
			response = "{\"message\":\"Error in Refund.\",\"refundId\":NA,\"status\":\"FAILED\"}";
			return response;
		}
		return response;
		
	}

	public static void main(String[] args) { 
		String url="https://dashboard.easebuzz.in/transaction/v1/refund";
//		String request="txnid=E220316Y6RYA85&refund_amount=1.22&phone=9769483293&key=L0O2G1PMIR&hash=43dee25913dfd18f8ded3e909af62aaf05a11e2a0947edb5e0891c3bbfa0fcba88a46695a51c5f26f67acd42fa0b91a506ab2b439e55a227795543feeb42af35&amount=1.22&email=abhishek@gamil.com";
//	String request="txnid=E220316Y6RYA85&refund_amount=1.22&phone=9769483293&key=L0O2G1PMIR&hash=43dee25913dfd18f8ded3e909af62aaf05a11e2a0947edb5e0891c3bbfa0fcba88a46695a51c5f26f67acd42fa0b91a506ab2b439e55a227795543feeb42af35&amount=1.22&email=abhishek@gamil.com";
	String request="txnid=E220316Y6RYA85&refund_amount=1.22&phone=9769483293&key=L0O2G1PMIR&hash=43dee25913dfd18f8ded3e909af62aaf05a11e2a0947edb5e0891c3bbfa0fcba88a46695a51c5f26f67acd42fa0b91a506ab2b439e55a227795543feeb42af35&amount=1.22&email=abhishek@gamil.com";	
	try {
		String response =	refundPostRequest1(request,url);
		System.out.println("aaaa"+response);
	} catch (SystemException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}		
		
	}

}
