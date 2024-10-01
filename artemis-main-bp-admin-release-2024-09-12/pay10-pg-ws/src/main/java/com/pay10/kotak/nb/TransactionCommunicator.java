package com.pay10.kotak.nb;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

import org.json.JSONObject;
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
import com.pay10.federalNB.Constants;

import com.pay10.federalNB.Constants;
import com.pay10.pg.core.util.refundkotaknb;


@Service("kotakNBTransactionCommunicator")
public class TransactionCommunicator {
	
	private static Logger logger = LoggerFactory.getLogger(TransactionCommunicator.class.getName());

	@Autowired
	RestTemplate restTemplate;
	
	public void updateInvalidVpaResponse(Fields fields, String response) {

		fields.put(FieldType.STATUS.getName(), StatusType.REJECTED.getName());
		fields.put(FieldType.RESPONSE_CODE.getName(), ErrorType.INVALID_VPA.getResponseCode());
		fields.put(FieldType.PG_TXN_MESSAGE.getName(), ErrorType.INVALID_VPA.getResponseMessage());
	}

	public void updateSaleResponse(Fields fields, String request) {

		// String statusType = response;
		// if (response!= null) {
		// if (statusType.equals(Constants.SUCCESS_RESPONSE)) {
		
		fields.put(FieldType.KOTAK_FINAL_REQUEST.getName(), request.toString());
		fields.put(FieldType.STATUS.getName(), StatusType.SENT_TO_BANK.getName());
		fields.put(FieldType.RESPONSE_CODE.getName(), ErrorType.SUCCESS.getResponseCode());
		fields.put(FieldType.RESPONSE_MESSAGE.getName(), ErrorType.SUCCESS.getResponseMessage());
		/*
		 * } else { fields.put(FieldType.STATUS.getName(),
		 * StatusType.REJECTED.getName()); fields.put(FieldType.RESPONSE_CODE.getName(),
		 * ErrorType.REJECTED.getResponseCode());
		 * fields.put(FieldType.RESPONSE_MESSAGE.getName(),
		 * ErrorType.REJECTED.getResponseMessage()); } } else {
		 * fields.put(FieldType.STATUS.getName(), StatusType.REJECTED.getName());
		 * fields.put(FieldType.RESPONSE_CODE.getName(),
		 * ErrorType.REJECTED.getResponseCode());
		 * fields.put(FieldType.RESPONSE_MESSAGE.getName(),
		 * ErrorType.REJECTED.getResponseMessage()); }
		 */

	}

	@SuppressWarnings("incomplete-switch")
	public JSONObject getAuthoriseResponse(String request, Fields fields) throws SystemException {
		StringBuilder serverResponse = new StringBuilder();
		String hostUrl = PropertiesManager.propertiesMap.get("KotakUpiAuthUrl");

		// logger.info("Request sent to bank " + request);
		HttpsURLConnection connection = null;
		try {

			// Create connection

			URL url = new URL(hostUrl);
			connection = (HttpsURLConnection) url.openConnection();

			connection.setRequestMethod("POST");
			connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");

			connection.setUseCaches(false);
			connection.setDoOutput(true);
			connection.setDoInput(true);

			// Send request
			OutputStream outputStream = connection.getOutputStream();
			DataOutputStream wr = new DataOutputStream(outputStream);
			wr.writeBytes(request.toString());
			wr.close();

			// Get Response
			InputStream is = connection.getInputStream();
			BufferedReader rd = new BufferedReader(new InputStreamReader(is));
			String line;

			int code = ((HttpURLConnection) connection).getResponseCode();
			int firstDigitOfCode = Integer.parseInt(Integer.toString(code).substring(0, 1));
			if (firstDigitOfCode == 4 || firstDigitOfCode == 5) {
				fields.put(FieldType.STATUS.getName(), StatusType.ACQUIRER_DOWN.getName());
				logger.error("Response code of txn :" + code);
				throw new SystemException(ErrorType.ACUIRER_DOWN,
						"Network Exception with Kotak Upi " + hostUrl.toString());
			}

			while ((line = rd.readLine()) != null) {
				serverResponse.append(line);

			}
			rd.close();

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (connection != null) {
				connection.disconnect();
			}
		}
		JSONObject response = new JSONObject(serverResponse.toString());
		logger.info(" OAuth Response received from KotakUpi" + response.toString());
		return response;
	}

	// @SuppressWarnings("incomplete-switch")
	public JSONObject getVPAResponse(JSONObject request, Fields fields, String accessTokenResponse)
			throws SystemException {
		StringBuilder serverResponse = new StringBuilder();
		String hostUrl = PropertiesManager.propertiesMap.get("KotakUpiVPAUrl");
		HttpsURLConnection connection = null;

		try {

			// Create connection

			URL url = new URL(hostUrl);
			connection = (HttpsURLConnection) url.openConnection();

			connection.setRequestMethod("POST");
			connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
			connection.setRequestProperty("Authorization", "Bearer " + accessTokenResponse);

			connection.setUseCaches(false);
			connection.setDoOutput(true);
			connection.setDoInput(true);

			// Send request
			OutputStream outputStream = connection.getOutputStream();
			DataOutputStream wr = new DataOutputStream(outputStream);
			wr.writeBytes(request.toString());
			wr.close();

			// Get Response
			InputStream is = connection.getInputStream();
			BufferedReader rd = new BufferedReader(new InputStreamReader(is));
			String line;

			int code = ((HttpURLConnection) connection).getResponseCode();
			int firstDigitOfCode = Integer.parseInt(Integer.toString(code).substring(0, 1));
			if (firstDigitOfCode == 4 || firstDigitOfCode == 5) {
				fields.put(FieldType.STATUS.getName(), StatusType.ACQUIRER_DOWN.getName());
				logger.error("Response code of txn :" + code);
				throw new SystemException(ErrorType.ACUIRER_DOWN,
						"Network Exception with Kotak Upi " + hostUrl.toString());
			}

			while ((line = rd.readLine()) != null) {
				serverResponse.append(line);

			}
			rd.close();

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (connection != null) {
				// connection.close();
			}
		}

		JSONObject response = new JSONObject(serverResponse.toString());
		logger.info(" VPA Validation Response received from kotakUpi" + response.toString());
		return response;
	}

	@SuppressWarnings("incomplete-switch")
	public JSONObject getResponse(JSONObject request, Fields fields, String accessTokenResponse, String xHeader)
			throws SystemException {
		StringBuilder serverResponse = new StringBuilder();
		String hostUrl = "";

		TransactionType transactionType = TransactionType.getInstance(fields.get(FieldType.TXNTYPE.getName()));
		switch (transactionType) {
		case SALE:
			hostUrl = PropertiesManager.propertiesMap.get("KotakUpiSaleUrl");
			break;
		case REFUND:
			hostUrl = PropertiesManager.propertiesMap.get("KotakUpiRefundUrl");
			break;
		case ENQUIRY:
			hostUrl = PropertiesManager.propertiesMap.get("KotakUpiStatusEnqUrl");

		}

		HttpsURLConnection connection = null;
		try {

			// Create connection

			URL url = new URL(hostUrl);
			connection = (HttpsURLConnection) url.openConnection();

			connection.setRequestMethod("POST");
			connection.setRequestProperty("Content-Type", "application/json");
			connection.setRequestProperty("Authorization", "Bearer " + accessTokenResponse);
			connection.setRequestProperty("X-Check", xHeader);

			connection.setUseCaches(false);
			connection.setDoOutput(true);
			connection.setDoInput(true);

			// Send request
			OutputStream outputStream = connection.getOutputStream();
			DataOutputStream wr = new DataOutputStream(outputStream);
			wr.writeBytes(request.toString());
			wr.close();

			// Get Response
			InputStream is = connection.getInputStream();
			BufferedReader rd = new BufferedReader(new InputStreamReader(is));
			String line;

			int code = ((HttpURLConnection) connection).getResponseCode();
			int firstDigitOfCode = Integer.parseInt(Integer.toString(code).substring(0, 1));
			if (firstDigitOfCode == 4 || firstDigitOfCode == 5) {
				fields.put(FieldType.STATUS.getName(), StatusType.ACQUIRER_DOWN.getName());
				logger.error("Response code of txn :" + code);
				throw new SystemException(ErrorType.ACUIRER_DOWN,
						"Network Exception with Kotak Upi " + hostUrl.toString());
			}

			while ((line = rd.readLine()) != null) {
				serverResponse.append(line);

			}
			rd.close();

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (connection != null) {
				connection.disconnect();
			}
		}

		JSONObject response = new JSONObject(serverResponse.toString());
		logger.info(" Response received from kotakUpi" + response.toString());
		return response;
	}

	
	
	@SuppressWarnings("incomplete-switch")
	public String getrefundresponse(String request, Fields fields) throws SystemException {

		String hostUrl = "";
		String response = "";
		try {

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
				hostUrl = PropertiesManager.propertiesMap.get("refundurl");
				response = refundPostRequest(request, hostUrl,fields);
				break;
			case STATUS:
				hostUrl = PropertiesManager.propertiesMap.get(Constants.FEDERALBANK_NB_STATUS_ENQ_URL);
				break;
			}

			
		} catch (Exception e) {
			logger.error("Exception in axisbank NB txn Communicator", e);
		}
		return response;

	}
	public String refundPostRequest(String request, String hostUrl,Fields fields) {
		String response = "";

		try {
			
			AccessTokenDTO accessTokenDTO =httpCall(fields);
			String token=accessTokenDTO.getAccess_token();
			logger.info("accessToken"+token);

			HttpURLConnection connection = null;
			URL url;
			url = new URL(hostUrl);
			

			connection = (HttpURLConnection) url.openConnection();
			connection.setRequestMethod("POST");
			connection.setRequestProperty("Content-Type","text/xml");
			connection.setRequestProperty("Authorization","Bearer "+token);
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
			
			logger.info(" kotak bank NB Response for refund transaction >> " + response);
			
		

		} catch (Exception e) {
		logger.info("Exception in getting Refund respose for kotak bank NB"+e);
			response = "{\"message\":\"Error in Refund.\",\"refundId\":NA,\"status\":\"FAILED\"}";
			return response;
		}
		return response;
	}
	//encData = refundkotaknb.encrypt(response, encryptKey);

	public  AccessTokenDTO httpCall(Fields fields) {

		logger.info("Request Received");
		String url = 	PropertiesManager.propertiesMap.get("KotakAccessToken");
		logger.info("url in KotakAccessToken"+url);
	//	String url = "https://apigwuat.kotak.com:8443/k2/auth/oauth/v2/token";
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

		MultiValueMap<String, String> map = new LinkedMultiValueMap<String, String>();
//		map.add("grant_type", "client_credentials");
//		map.add("client_id","3b4c289e-c52c-4d77-a59c-34f6357f431f");
//		map.add("client_secret","119c9fb7-1c1d-4ac1-a4b8-4a4fe64693ed");
		map.add("grant_type",fields.get(FieldType.ADF4.getName()));
		logger.info("grant_type"+fields.get(FieldType.ADF4.getName()));
		map.add("client_id",fields.get(FieldType.ADF5.getName()));
		map.add("client_secret",fields.get(FieldType.ADF10.getName()));
		map.add("scope","kbsecpg");
		logger.info("map >>>>>>>>>>>>>>> "+map.toString());
		HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<MultiValueMap<String, String>>(map, headers);
		System.out.println(restTemplate);
		ResponseEntity<AccessTokenDTO> response = restTemplate.postForEntity(url, request, AccessTokenDTO.class);
		System.out.println("response " + response.toString());
		System.out.println("response " +response.getBody());
		
		return response.getBody();
		
	}

	
	
	

	public static void main(String[] args) throws Exception {
		
		
	

	}
	}



