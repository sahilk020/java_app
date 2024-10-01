package com.pay10.phonepe;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.Base64;
import java.util.Map;
import java.util.TreeMap;

import javax.xml.bind.DatatypeConverter;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.pay10.commons.exception.SystemException;
import com.pay10.commons.util.FieldType;
import com.pay10.commons.util.Fields;
import com.pay10.commons.util.PropertiesManager;
import com.pay10.commons.util.TransactionType;
import com.pay10.pg.core.paytm.util.CheckSumServiceHelper;
import com.pay10.pg.core.util.AcquirerTxnAmountProvider;

/**
 * @author Shaiwal
 *
 */
@Service("phonepeTransactionConverter")
public class TransactionConverter {

	@Autowired
	private AcquirerTxnAmountProvider acquirerTxnAmountProvider;

	private static final Logger logger = LoggerFactory.getLogger(TransactionConverter.class.getName());

	@SuppressWarnings("incomplete-switch")
	public String perpareRequest(Fields fields, Transaction transaction) throws SystemException {

		String request = null;

		switch (TransactionType.getInstance(fields.get(FieldType.TXNTYPE.getName()))) {
		case AUTHORISE:
		case ENROLL:
			break;
		case REFUND:
			request = refundRequest(fields, transaction);
			break;
		case SALE:
			request = saleRequest(fields, transaction);
			break;
		}
		return request.toString();

	}

	public String saleRequest(Fields fields, Transaction transaction) throws SystemException {

		try {

			String finalRedirectUrl = "";
			String requestURL = PropertiesManager.propertiesMap.get(Constants.PHONEPE_SALE_REQUEST_URL);
			JSONObject request = new JSONObject();

			request.put(Constants.MERCHANT_ID, transaction.getMerchantId());
			request.put(Constants.TRANSACTION_ID, transaction.getTransactionId());
			if (StringUtils.isNotBlank(transaction.getMerchantUserId())) {
				request.put(Constants.MERCHANT_USER_ID, transaction.getMerchantUserId());
			}
			request.put(Constants.AMOUNT, transaction.getAmount());
			request.put(Constants.SUB_MERCHANT, transaction.getSubMerchant());

			logger.info("phone pe payment request  =  " + request.toString());

			String base64encodedRequest = Base64.getEncoder().encodeToString(request.toString().getBytes("utf-8"));

			String prefix = Constants.SALE_TXN_PREFIX;
			String saltKey = transaction.getSaltKey();
			String saltIndex = transaction.getSaltIndex();

			StringBuilder finalReq = new StringBuilder();
			finalReq.append(base64encodedRequest);
			finalReq.append(prefix);
			finalReq.append(saltKey);

			MessageDigest md = MessageDigest.getInstance("SHA-256");
			byte[] digest = md.digest(finalReq.toString().getBytes(StandardCharsets.UTF_8));
			String sha256String = DatatypeConverter.printHexBinary(digest).toLowerCase();

			String xVerify = sha256String + Constants.SEPARATOR + saltIndex;

			logger.info("phone pe payment request  xverify =  " + xVerify);

			JSONObject requestFinal = new JSONObject();
			requestFinal.put("request", base64encodedRequest);

			URL url = new URL(requestURL);

			HttpURLConnection connection = (HttpURLConnection) url.openConnection();
			connection.setRequestMethod("POST");
			connection.setRequestProperty("Content-Type", "application/json");
			connection.setRequestProperty("X-VERIFY", xVerify);
			connection.setRequestProperty("X-REDIRECT-URL", transaction.getRedirectUrl());
			connection.setRequestProperty("X-REDIRECT-MODE", Constants.REDIRECT_MODE);

			connection.setDoOutput(true);

			DataOutputStream requestWriter = new DataOutputStream(connection.getOutputStream());
			requestWriter.writeBytes(requestFinal.toString());
			requestWriter.close();
			String responseData = "";
			InputStream is = connection.getInputStream();
			BufferedReader responseReader = new BufferedReader(new InputStreamReader(is));
			if ((responseData = responseReader.readLine()) != null) {
				logger.info("Phone Pe Sale Redirect URL Request  >>> " + responseData);

				JSONObject responseObj = new JSONObject(responseData);

				if (responseObj.has("data")) {

					JSONObject data = responseObj.getJSONObject("data");
					if (data.has("redirectURL")) {
						finalRedirectUrl = data.get("redirectURL").toString();
					}
				}

			} else {
				logger.info("PhonePe Sale Redirect URL Request received as empty , cannot proceed further");
			}
			responseReader.close();

			return finalRedirectUrl;
		}

		catch (Exception e) {
			logger.error("Exception in generating PhonePe sale request ", e);
			return null;
		}

	}

	public String refundRequest(Fields fields, Transaction transaction) throws SystemException {

		try {
			JSONObject request = new JSONObject();

			request.put(Constants.MERCHANT_ID, transaction.getMerchantId());
			request.put(Constants.TRANSACTION_ID, transaction.getTransactionId());
			request.put(Constants.PROVIDER_REF_ID, transaction.getProviderReferenceId());
			request.put(Constants.AMOUNT, Long.valueOf(transaction.getAmount()));
			request.put(Constants.MERCHANT_ORDER_ID, transaction.getMerchantOrderId());
			request.put(Constants.MESSAGE, transaction.getMessage());
			request.put(Constants.SUB_MERCHANT, transaction.getSubMerchant());

			logger.info("PhonePe refund request  =  " + request.toString());

			String base64encodedRequest = Base64.getEncoder().encodeToString(request.toString().getBytes("utf-8"));

			String prefix = Constants.REFUND_TXN_PREFIX;
			String saltKey = transaction.getSaltKey();
			String saltIndex = transaction.getSaltIndex();

			StringBuilder finalReq = new StringBuilder();

			finalReq.append(base64encodedRequest);
			finalReq.append(prefix);
			finalReq.append(saltKey);

			MessageDigest md = MessageDigest.getInstance("SHA-256");
			byte[] digest = md.digest(finalReq.toString().getBytes(StandardCharsets.UTF_8));
			String sha256 = DatatypeConverter.printHexBinary(digest).toLowerCase();
			String xVerify = sha256 + Constants.SEPARATOR + saltIndex;
			logger.info("X Verify for refund = " + xVerify);

			JSONObject reqParams = new JSONObject();
			reqParams.put("request", base64encodedRequest);
			reqParams.put("xVerify", xVerify);

			return reqParams.toString();
		}

		catch (Exception e) {
			logger.error("Exception in generating PhonePe refund request", e);
		}
		return null;

	}

	public Transaction toTransaction(String jsonResponse, String txnType) {

		Transaction transaction = new Transaction();

		if (StringUtils.isBlank(jsonResponse)) {
			logger.info("Empty response received for PhonePe refund");
			return transaction;
		}

		JSONObject respObj = new JSONObject(jsonResponse);

		if (respObj.has("success")) {
			transaction.setSuccess(respObj.get("success").toString());
		}

		if (respObj.has("code")) {
			transaction.setCode(respObj.get("code").toString());
		}

		if (respObj.has("message")) {
			transaction.setMessage(respObj.get("message").toString());
		}

		if (respObj.has("data")) {

			JSONObject data = new JSONObject();
			data = respObj.getJSONObject("data");

			if (data.has("providerReferenceId")) {
				transaction.setProviderReferenceId(data.get("providerReferenceId").toString());
			}

			if (data.has("status")) {
				transaction.setStatus(data.get("status").toString());
			}

			if (data.has("payResponseCode")) {
				transaction.setPayResponseCode(data.get("payResponseCode").toString());
			}

		}

		return transaction;

	}

	public TransactionConverter() {

	}

}
