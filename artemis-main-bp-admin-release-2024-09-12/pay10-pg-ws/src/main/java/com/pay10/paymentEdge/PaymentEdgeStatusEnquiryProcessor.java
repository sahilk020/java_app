package com.pay10.paymentEdge;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;

import javax.xml.bind.DatatypeConverter;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.pay10.commons.exception.SystemException;
import com.pay10.commons.util.FieldType;
import com.pay10.commons.util.Fields;
import com.pay10.commons.util.PropertiesManager;

@Service
public class PaymentEdgeStatusEnquiryProcessor {

	@Autowired
	@Qualifier("payementEdgeTransactionConverter")
	private TransactionConverter converter;

	private static Logger logger = LoggerFactory.getLogger(PaymentEdgeStatusEnquiryProcessor.class.getName());

	public void enquiryProcessor(Fields fields) {
		String request = statusEnquiryRequest(fields);
		String response = "";
		try {

			response = getResponse(request);
			updateFields(fields, response);

		} catch (SystemException exception) {
			logger.error("Exception", exception);
		} catch (Exception e) {
			logger.error("Exception in decrypting status enquiry response for Asiancheckout ", e);
		}

	}

	public String statusEnquiryRequest(Fields fields) {

		try {

			JSONObject reqObj = new JSONObject();

			String merchantId = fields.get(FieldType.MERCHANT_ID.getName());
			String transactionId = fields.get(FieldType.PG_REF_NUM.getName());
			;
			String prefix = Constants.STATUS_TXN_PREFIX;
			String saltKey = fields.get(FieldType.TXN_KEY.getName());
			String saltIndex = fields.get(FieldType.ADF1.getName());

			StringBuilder finalReq = new StringBuilder();

			finalReq.append(prefix);
			finalReq.append(merchantId);
			finalReq.append("/");
			finalReq.append(transactionId);
			finalReq.append("/");
			finalReq.append("status");
			finalReq.append(saltKey);

			MessageDigest md = MessageDigest.getInstance("SHA-256");
			byte[] digest = md.digest(finalReq.toString().getBytes(StandardCharsets.UTF_8));
			String sha256 = DatatypeConverter.printHexBinary(digest).toLowerCase();

			String xverify = sha256 + Constants.SEPARATOR + saltIndex;

			reqObj.put("xverify", xverify);
			reqObj.put("merchantId", merchantId);
			reqObj.put("transactionId", transactionId);

			return reqObj.toString();
		}

		catch (Exception e) {
			logger.error("Exception in preparing Asiancheckout Status Enquiry Request", e);
			return null;
		}
	}

	public static String getResponse(String request) throws SystemException {

		if (StringUtils.isBlank(request)) {
			logger.info("Request is empty for PaymentEdge status enquiry");
			return null;
		}

		String hostUrl = "";
		Fields fields = new Fields();
		fields.put(FieldType.TXNTYPE.getName(), "STATUS");

		try {

			hostUrl = PropertiesManager.propertiesMap.get(Constants.PAYMENTEDGE_STATUS_ENQ_URL);
			StringBuilder sb = new StringBuilder();
			sb.append(hostUrl);

			JSONObject reqObj = new JSONObject(request);

			if (reqObj.has("merchantId")) {
				sb.append(reqObj.get("merchantId").toString());
			}

			sb.append("/");
			if (reqObj.has("transactionId")) {
				sb.append(reqObj.get("transactionId").toString());
			}
			sb.append("/status");

			String xverify = "";
			if (reqObj.has("xverify")) {
				xverify = reqObj.get("xverify").toString();
			}

			logger.info("Status Enquiry Request to PaymentEdge : TxnType = " + fields.get(FieldType.TXNTYPE.getName()) + " "
					+ "PG REF NUM = " + reqObj.getString("transactionId") + " " + sb.toString());

			HttpURLConnection httpClient = (HttpURLConnection) new URL(sb.toString()).openConnection();
			httpClient.setRequestMethod("GET");
			httpClient.setRequestProperty("X-VERIFY", xverify);
			logger.info("Sending GET requst for status enq to URL " + sb.toString());

			try (BufferedReader in = new BufferedReader(new InputStreamReader(httpClient.getInputStream()))) {

				StringBuilder response = new StringBuilder();
				String line;

				while ((line = in.readLine()) != null) {
					response.append(line);
				}

				if (StringUtils.isBlank(response)) {

					return null;
				} else {
					logger.info("Response received for PaymentEdge status enq " + response.toString());
					return response.toString();
				}

			}

		} catch (Exception e) {
			logger.error("Exception in PaymentEdge Status Enquiry ", e);
			return null;
		}

	}

	public void updateFields(Fields fields, String jsonResponse) {

		/*
		 * Transaction transaction = new Transaction();
		 * 
		 * transaction = toTransaction(jsonResponse,
		 * fields.get(FieldType.INTERNAL_ORIG_TXN_TYPE.getName()));
		 * 
		 * String status = null; ErrorType errorType = null; String pgTxnMsg = null;
		 * 
		 * 
		 * if (StringUtils.isNotBlank(transaction.getAmount()) &&
		 * StringUtils.isNotBlank(fields.get(FieldType.AMOUNT.getName())) &&
		 * !(fields.get(FieldType.AMOUNT.getName()).equals(transaction.getAmount()))) {
		 * 
		 * logger.info("Amount in Status enquiry response not matching with request");
		 * status = StatusType.AUTHENTICATION_FAILED.getName(); errorType =
		 * ErrorType.AMOUNT_MISMATCH; pgTxnMsg =
		 * ErrorType.AMOUNT_MISMATCH.getResponseMessage(); }
		 * 
		 * else if (StringUtils.isNotBlank(transaction.getSuccess()) &&
		 * StringUtils.isNotBlank(transaction.getCode()) &&
		 * ((transaction.getSuccess()).equalsIgnoreCase("true")) &&
		 * ((transaction.getCode()).equalsIgnoreCase("PAYMENT_SUCCESS")) )
		 * 
		 * { status = StatusType.CAPTURED.getName(); errorType = ErrorType.SUCCESS;
		 * 
		 * if (StringUtils.isNotBlank(transaction.getMessage())) { pgTxnMsg =
		 * transaction.getMessage(); }
		 * 
		 * else { pgTxnMsg = ErrorType.SUCCESS.getResponseMessage(); }
		 * 
		 * }
		 * 
		 * else { if ((StringUtils.isNotBlank(transaction.getCode()))) {
		 * 
		 * String respCode = null; if (StringUtils.isNotBlank(transaction.getCode())) {
		 * respCode = transaction.getCode(); }
		 * 
		 * AsiancheckoutResultType resultInstance =
		 * AsiancheckoutResultType.getInstanceFromName(respCode);
		 * 
		 * if (resultInstance != null) { status = resultInstance.getStatusCode();
		 * errorType = ErrorType.getInstanceFromCode(resultInstance.getiPayCode());
		 * 
		 * if (StringUtils.isNotBlank(transaction.getMessage())) { pgTxnMsg =
		 * transaction.getMessage(); }
		 * 
		 * else { pgTxnMsg = resultInstance.getMessage(); }
		 * 
		 * } else { status = StatusType.REJECTED.getName(); errorType =
		 * ErrorType.getInstanceFromCode("007");
		 * 
		 * if (StringUtils.isNotBlank(transaction.getMessage())) { pgTxnMsg =
		 * transaction.getMessage(); }
		 * 
		 * else { pgTxnMsg = "Transaction Declined"; }
		 * 
		 * }
		 * 
		 * } else { status = StatusType.REJECTED.getName(); errorType =
		 * ErrorType.REJECTED; if (StringUtils.isNotBlank(transaction.getMessage())) {
		 * pgTxnMsg = transaction.getMessage(); } else { pgTxnMsg =
		 * "Transaction Rejected"; }
		 * 
		 * } }
		 * 
		 * fields.put(FieldType.STATUS.getName(), status);
		 * fields.put(FieldType.RESPONSE_MESSAGE.getName(),
		 * errorType.getResponseMessage());
		 * fields.put(FieldType.RESPONSE_CODE.getName(), errorType.getResponseCode());
		 * 
		 * fields.put(FieldType.RRN.getName(), transaction.getProviderReferenceId());
		 * fields.put(FieldType.ACQ_ID.getName(), transaction.getProviderReferenceId());
		 * fields.put(FieldType.PG_RESP_CODE.getName(),
		 * transaction.getPayResponseCode());
		 * 
		 * if (StringUtils.isNotBlank(transaction.getPaymentState())) {
		 * fields.put(FieldType.PG_TXN_STATUS.getName(), transaction.getPaymentState());
		 * } else { fields.put(FieldType.PG_TXN_STATUS.getName(),
		 * errorType.getResponseCode()); }
		 * 
		 * fields.put(FieldType.PG_TXN_MESSAGE.getName(), pgTxnMsg);
		 */

	}

	public Transaction toTransaction(String jsonResponse, String txnType) {

		Transaction transaction = new Transaction();

		/*
		 * if (StringUtils.isBlank(jsonResponse)) {
		 * logger.info("Empty response received for Asiancheckout Status Enquiry");
		 * return transaction; }
		 * 
		 * JSONObject respObj = new JSONObject(jsonResponse);
		 * 
		 * if (respObj.has("success")) {
		 * transaction.setSuccess(respObj.get("success").toString()); }
		 * 
		 * if (respObj.has("code")) {
		 * transaction.setCode(respObj.get("code").toString()); }
		 * 
		 * if (respObj.has("message")) {
		 * transaction.setMessage(respObj.get("message").toString()); }
		 * 
		 * if (respObj.has("amount")) {
		 * transaction.setAmount(respObj.get("amount").toString()); }
		 * 
		 * if (respObj.has("data")) {
		 * 
		 * JSONObject data = new JSONObject(respObj); data =
		 * respObj.getJSONObject("data");
		 * 
		 * if (data.has("providerReferenceId")) {
		 * transaction.setProviderReferenceId(data.get("providerReferenceId").toString()
		 * ); }
		 * 
		 * if (data.has("paymentState")) {
		 * transaction.setPaymentState(data.get("paymentState").toString()); }
		 * 
		 * if (data.has("payResponseCode")) {
		 * transaction.setPayResponseCode(data.get("payResponseCode").toString()); } if
		 * (data.has("amount")) { transaction.setAmount(data.get("amount").toString());
		 * }
		 * 
		 * }
		 */

		return transaction;

	}

}
