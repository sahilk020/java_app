package com.pay10.pinelabs;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

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

@Service("pinelabsTransactionCommunicator")
public class TransactionCommunicator {

	private static Logger logger = LoggerFactory.getLogger(TransactionCommunicator.class.getName());

	public void updateSaleResponse(Fields fields, String request) {

		logger.info("second token request : " + request);
		fields.put(FieldType.PINELABS_FINAL_REQUEST.getName(), request);
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
			response = refundEnquiryPostRequest(request, hostUrl);
			break;
		case STATUS:
			hostUrl = PropertiesManager.propertiesMap.get(Constants.STATUS_ENQ_REQUEST_URL);
			response = refundEnquiryPostRequest(request, hostUrl);
			break;
		}
		return response;

	}

	public String refundEnquiryPostRequest(String request, String hostUrl) throws SystemException {
		String response = "";

		try {

			HttpURLConnection connection = null;
			URL url;
			url = new URL(hostUrl);
			connection = (HttpURLConnection) url.openConnection();
			connection.setRequestMethod("POST");
			connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
			connection.setRequestProperty("cache-control", "no-cache");
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

			logger.info("Response for refund & Enquiry transaction >> " + response);

		} catch (Exception e) {
			logger.error("Exception in getting Refund & Enquiry respose for Pinelabs", e);

		}
		return response;
	}

	public String TokenPostRequest(String request, String xVerify) throws SystemException {
		String response = "";

		try {
			String hostUrl = PropertiesManager.propertiesMap.get(Constants.TOKEN_RETURN_URL);
			// hostUrl = "https://uat.pinepg.in/api/v2/accept/payment";
			HttpURLConnection connection = null;
			URL url;
			System.out.println(request + "url ---- > " + hostUrl);
			url = new URL(hostUrl);
			connection = (HttpURLConnection) url.openConnection();
			connection.setRequestMethod("POST");
			connection.setRequestProperty("Content-Type", "application/json");
			connection.setRequestProperty("cache-control", "no-cache");
			connection.setRequestProperty("X-VERIFY", xVerify);

			connection.setUseCaches(false);
			connection.setDoInput(true);
			connection.setDoOutput(true);
			connection.setConnectTimeout(60000);
			connection.setReadTimeout(60000);

			// Send request
			DataOutputStream wr = new DataOutputStream(connection.getOutputStream());
			wr.writeBytes(request);
			// wr.flush();
			wr.close();

			// Get Response
			int code = ((HttpURLConnection) connection).getResponseCode();
			System.out.println("input-------->" + code);
			InputStream is = connection.getInputStream();

			BufferedReader bufferedreader = new BufferedReader(new InputStreamReader(is));
			String decodedString;

			while ((decodedString = bufferedreader.readLine()) != null) {
				response = response + decodedString;
			}

			bufferedreader.close();

			logger.info("Pinelabs -Response for Token transaction >> " + response);

		} catch (Exception e) {
			logger.error("Exception in getting token respose for Pinelabs", e);

			return response;
		}
		return response;
	}

	public String PostRequest(String request, String token) throws SystemException {
		String response = "";

		try {
			String hostUrl = PropertiesManager.propertiesMap.get("PINELABSSaleUrl");
			// hostUrl = "https://uat.pinepg.in/api/v2/accept/payment";
			hostUrl = hostUrl + "?token=" + token;
			HttpURLConnection connection = null;
			URL url;
			System.out.println(request + "url ---- > " + hostUrl);
			url = new URL(hostUrl);
			connection = (HttpURLConnection) url.openConnection();
			connection.setRequestMethod("POST");
			connection.setRequestProperty("Content-Type", "application/json");
			connection.setRequestProperty("cache-control", "no-cache");
			// connection.setRequestProperty("X-VERIFY",xVerify);

			connection.setUseCaches(false);
			connection.setDoInput(true);
			connection.setDoOutput(true);
			connection.setConnectTimeout(60000);
			connection.setReadTimeout(60000);

			// Send request
			DataOutputStream wr = new DataOutputStream(connection.getOutputStream());
			wr.writeBytes(request);
			// wr.flush();
			wr.close();

			// Get Response
			int code = ((HttpURLConnection) connection).getResponseCode();
			System.out.println("input-------->" + code);
			InputStream is = connection.getInputStream();

			BufferedReader bufferedreader = new BufferedReader(new InputStreamReader(is));
			String decodedString;

			while ((decodedString = bufferedreader.readLine()) != null) {
				response = response + decodedString;
			}

			bufferedreader.close();

			logger.info("Pinelabs -Response for Token transaction >> " + response);

		} catch (Exception e) {
			logger.error("Exception in getting token respose for Pinelabs", e);

			return response;
		}
		return response;
	}

	public String statusEnqPostRequest(String request, String hostUrl) throws SystemException {
		String response = "";

		try {

			logger.info("Pinelabs Status Enquiry Request " + request);
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

			logger.info("Response for Pinelabs Status Enquiry transaction >> " + response);

		} catch (Exception e) {
			logger.error("Exception in getting Status Enquiry respose for Pinelabs", e);
		}
		return response;
	}

}
