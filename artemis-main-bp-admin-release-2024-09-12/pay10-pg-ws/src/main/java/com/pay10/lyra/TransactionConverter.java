package com.pay10.lyra;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.google.gson.JsonObject;
import com.pay10.commons.exception.SystemException;
import com.pay10.commons.util.Currency;
import com.pay10.commons.util.FieldType;
import com.pay10.commons.util.Fields;
import com.pay10.commons.util.PaymentType;
import com.pay10.commons.util.PropertiesManager;
import com.pay10.commons.util.TransactionType;
import com.pay10.pg.core.util.AcquirerTxnAmountProvider;

@Service("lyraTransactionConverter")
public class TransactionConverter {

	@Autowired
	private AcquirerTxnAmountProvider acquirerTxnAmountProvider;

	@Autowired
	private PropertiesManager propertiesManager;

	@SuppressWarnings("incomplete-switch")
	public String perpareRequest(Fields fields, Transaction transaction) throws SystemException {

		String request = null;

		switch (TransactionType.getInstance(fields.get(FieldType.TXNTYPE.getName()))) {
		case AUTHORISE:
		case ENROLL:
			request = saleRequest(fields, transaction);
			break;
		case REFUND:
			request = refundRequest(fields, transaction);
			break;
		case SALE:
			request = saleRequest(fields, transaction);
			break;
		case CAPTURE:
			break;
		case STATUS:
			request = statusEnquiryRequest(fields, transaction);
			break;
		}
		return request.toString();

	}

	public String saleRequest(Fields fields, Transaction transaction) throws SystemException {
		String amount = acquirerTxnAmountProvider.amountProvider(fields);
		String cuurencyAlpha = Currency.getAlphabaticCode(fields.get(FieldType.CURRENCY_CODE.getName()));
		String responseUrl = PropertiesManager.propertiesMap.get(Constants.RESPONSE_URL);

		JSONObject jsonRequest = new JSONObject();
		jsonRequest.put(Constants.ORDER_ID, fields.get(FieldType.PG_REF_NUM.getName()));
		jsonRequest.put(Constants.AMOUNT, amount);
		jsonRequest.put(Constants.CURRENCY, cuurencyAlpha);
		jsonRequest.put(Constants.VERSION, Constants.VERSION_VALUE);
		jsonRequest.put(Constants.FORM_ACTION, Constants.PAYMENT);

		JSONArray subReq = new JSONArray();
		JSONObject payment = new JSONObject();

		payment.put(Constants.PAYMENT_METHOD, Constants.CARD);
		payment.put(Constants.PAN, transaction.getCard());
		payment.put(Constants.EXPIRY_MONTH, transaction.getExpMonth());
		payment.put(Constants.EXPIRY_YEAR, transaction.getExpYear());
		payment.put(Constants.SECURITY_CODE, transaction.getCvv());
		subReq.put(payment);

		jsonRequest.put(Constants.PAYMENT_FORM, subReq);
		JSONObject customerInfo = new JSONObject();
		customerInfo.put(Constants.EMAIL, fields.get(FieldType.CUST_EMAIL.getName()));
		JSONObject billing = new JSONObject();
		billing.put(Constants.FIRST_NAME, fields.get(FieldType.CUST_NAME.getName()));
		billing.put(Constants.LAST_NAME, "");
		billing.put(Constants.PHONE_NO, fields.get(FieldType.CUST_PHONE.getName()));
		billing.put(Constants.ADDRESS, "");

		customerInfo.put(Constants.BILLING_DETAILS, billing);
		jsonRequest.put(Constants.CUSTOMER, customerInfo);
		jsonRequest.put(Constants.MERCHANT_POST_URL_REFUSED, responseUrl);
		jsonRequest.put(Constants.MERCHANT_POST_URL_SUCCESS, responseUrl);

		return jsonRequest.toString();
	}

	public String refundRequest(Fields fields, Transaction transaction) throws SystemException {
		String amount = acquirerTxnAmountProvider.amountProvider(fields);
		JSONObject jsonRequest = new JSONObject();

		jsonRequest.put(Constants.UUID, fields.get(FieldType.ACQ_ID.getName()));
		jsonRequest.put(Constants.AMOUNT, amount);
		jsonRequest.put(Constants.RESOLUTION_MODE, Constants.AUTO);
		return jsonRequest.toString();

	}

	public String statusEnquiryRequest(Fields fields, Transaction transaction) {
		return null;

	}

	public Transaction toTransaction(JSONObject response) {
		Transaction transaction = new Transaction();
		Map<String, String> secureMap = new HashMap<String, String>();
		Map<String, String> responseMap = new HashMap<String, String>();

		JSONObject secure = new JSONObject();
		secure = response.getJSONObject("answer");

		JSONObject authenticationRedirect = new JSONObject();
		authenticationRedirect = secure.getJSONObject("postData");

		for (Object key : secure.keySet()) {

			String key1 = key.toString();
			String value = secure.get(key.toString()).toString();
			secureMap.put(key1, value);
		}
		for (Object key : authenticationRedirect.keySet()) {

			String key1 = key.toString();
			String value = authenticationRedirect.get(key.toString()).toString();
			responseMap.put(key1, value);
		}
		transaction.setAcsUrl(secureMap.get("redirectUrl"));
		transaction.setSubmitUrl(authenticationRedirect.toString());
		transaction.setPareq(responseMap.get("EncData"));
		transaction.setMerchantId(responseMap.get("MerchantId"));
		transaction.setResult(response.getString("status"));
		return transaction;

	}

	public Transaction nbChargeToTransaction(JSONObject response) {

		Transaction transaction = new Transaction();
		transaction.setUuid(response.getString("uuid"));
		transaction.setResult(response.getString("status"));
		return transaction;

	}

	public Transaction nbUuidToTransaction(JSONObject response) {
		Transaction transaction = new Transaction();
		JSONObject param = new JSONObject();
		param = response.getJSONObject("params");
		transaction.setAcsUrl(response.getString("url"));
		transaction.setPareq(param.toString());
		return transaction;

	}

	public Transaction upiUuidToTransaction(JSONObject response) {
		Transaction transaction = new Transaction();

		if (response.has("customerName") && !response.isNull("customerName")) {
			transaction.setMember(response.getString("customerName"));
		}
		if (response.has("transactionUuid") && !response.isNull("transactionUuid")) {
			transaction.setUuid(response.getString("transactionUuid"));
		}

		if (response.has("date") && !response.isNull("date")) {
			transaction.setPgDateTime(response.getString("date"));
		}
		return transaction;

	}

	public Transaction nbRefundToTransaction(JSONObject response) {
		Transaction transaction = new Transaction();
		transaction.setUuid(response.getString("uuid"));
		transaction.setStatus(response.getString("status"));
		transaction.setPgDateTime(response.getString("date"));
		return transaction;

	}

	public Transaction refundToTransaction(JSONObject response) {

		Transaction transaction = new Transaction();

		Map<String, String> secureMap = new HashMap<String, String>();
		Map<String, String> transactionDetailsMap = new HashMap<String, String>();
		JSONObject secure = new JSONObject();
		secure = response.getJSONObject("answer");

		JSONObject transactionDetails = new JSONObject();
		transactionDetails = secure.getJSONObject("transactionDetails");
		for (Object key : secure.keySet()) {

			String key1 = key.toString();
			String value = secure.get(key.toString()).toString();
			secureMap.put(key1, value);
		}
		for (Object key : transactionDetails.keySet()) {

			String key1 = key.toString();
			String value = transactionDetails.get(key.toString()).toString();
			transactionDetailsMap.put(key1, value);
		}

		transaction.setUuid(secureMap.get("uuid"));
		transaction.setResult(response.get("status").toString());
		transaction.setPgDateTime(response.get("serverDate").toString());
		transaction.setErrorCode(secureMap.get("detailedErrorCode"));
		transaction.setErrorMessage(secureMap.get("errorMessage"));
		transaction.setRrn(transactionDetailsMap.get("externalTransactionId"));
		return transaction;

	}

	public Transaction statusToTransaction(JSONObject response) {
		Transaction transaction = new Transaction();
		JSONObject secure = new JSONObject();
		secure = response.getJSONObject("answer");

		String txn = secure.get("transactions").toString();
		txn = txn.substring(1, txn.length() - 1);
		JSONObject txnJson = new JSONObject(txn);

		JSONObject transactionDetailsJson = txnJson.getJSONObject("transactionDetails");
		transaction.setStatus(txnJson.get("status").toString());
		transaction.setPgDateTime(response.get("serverDate").toString());
		transaction.setUuid(txnJson.get("uuid").toString());
		transaction.setErrorCode(txnJson.get("detailedErrorCode").toString());
		transaction.setErrorMessage(txnJson.get("errorMessage").toString());
		transaction.setRrn(transactionDetailsJson.get("externalTransactionId").toString());
		return transaction;

	}

	public String createChargeRequest(Fields fields) throws SystemException {
		String amount = acquirerTxnAmountProvider.amountProvider(fields);

		// Removing decimal place
		if (amount.contains(".")) {
			amount = amount.replace(".", "");
		}

		String cuurencyAlpha = Currency.getAlphabaticCode(fields.get(FieldType.CURRENCY_CODE.getName()));
		String responseUrl = null;
		String serverResponse = null;

		responseUrl = propertiesManager.propertiesMap.get(Constants.NB_RESPONSE_URL);
		if (fields.get(FieldType.PAYMENT_TYPE.getName()).equalsIgnoreCase(PaymentType.UPI.getCode())) {
			serverResponse = propertiesManager.propertiesMap.get(Constants.UPI_CALLBACK_RESPONSE_URL);
		} else {
			serverResponse = propertiesManager.propertiesMap.get(Constants.SERVER_RESPONSE_URL);
		}

		responseUrl = responseUrl + fields.get(FieldType.PG_REF_NUM.getName());

		JsonObject chargeReq = new JsonObject();
		JsonObject customerReq = new JsonObject();
		JsonObject webhhokReq = new JsonObject();

		chargeReq.addProperty("orderId", fields.get(FieldType.PG_REF_NUM.getName()));
		chargeReq.addProperty("currency", cuurencyAlpha);
		chargeReq.addProperty("amount", amount);
		webhhokReq.addProperty("url", serverResponse);
		chargeReq.add("webhook", webhhokReq);
		chargeReq.addProperty("url", responseUrl);

		customerReq.addProperty(Constants.UID, propertiesManager.propertiesMap.get("Lyra_UID"));

		if (StringUtils.isNotBlank(fields.get(FieldType.CUST_NAME.getName()))) {
			customerReq.addProperty(Constants.NAME, fields.get(FieldType.CUST_NAME.getName()));
		} else {
			customerReq.addProperty(Constants.NAME, propertiesManager.propertiesMap.get("Lyra_NAME"));
		}

		if (StringUtils.isNotBlank(fields.get(FieldType.CUST_EMAIL.getName()))) {
			customerReq.addProperty(Constants.EMAIL_ID, fields.get(FieldType.CUST_EMAIL.getName()));
		} else {
			customerReq.addProperty(Constants.EMAIL_ID, propertiesManager.propertiesMap.get("Lyra_EMAIL_ID"));
		}

		if (StringUtils.isNotBlank(fields.get(FieldType.CUST_PHONE.getName()))) {
			customerReq.addProperty(Constants.PHONE, fields.get(FieldType.CUST_PHONE.getName()));
		} else {
			customerReq.addProperty(Constants.PHONE, propertiesManager.propertiesMap.get("Lyra_PHONE"));
		}

		if (StringUtils.isNotBlank(fields.get(FieldType.CUST_STREET_ADDRESS1.getName()))) {
			customerReq.addProperty(Constants.ADDRESS, fields.get(FieldType.CUST_STREET_ADDRESS1.getName()));
		} else {
			customerReq.addProperty(Constants.ADDRESS, propertiesManager.propertiesMap.get("Lyra_ADDRESS"));
		}

		if (StringUtils.isNotBlank(fields.get(FieldType.CUST_CITY.getName()))) {
			customerReq.addProperty(Constants.CITY, fields.get(FieldType.CUST_CITY.getName()));
		} else {
			customerReq.addProperty(Constants.CITY, propertiesManager.propertiesMap.get("Lyra_CITY"));
		}

		chargeReq.add(Constants.CUSTOMER, customerReq);

		return chargeReq.toString();
	}

	public String uuidRequest(Fields fields, Transaction transactionResponse) throws SystemException {
		JsonObject jsonRequest = new JsonObject();
		String bankCode = LyraBankCode.getBankCode(fields.get(FieldType.MOP_TYPE.getName()));
		if (fields.get(FieldType.PAYMENT_TYPE.getName()).equalsIgnoreCase(PaymentType.NET_BANKING.getCode())) {
			jsonRequest.addProperty("bankCode", bankCode);
		} else if (fields.get(FieldType.PAYMENT_TYPE.getName()).equalsIgnoreCase(PaymentType.WALLET.getCode())) {
			jsonRequest.addProperty("walletName", bankCode);
		} else if (fields.get(FieldType.PAYMENT_TYPE.getName()).equalsIgnoreCase(PaymentType.UPI.getCode())) {
			jsonRequest.addProperty("vpa", fields.get(FieldType.PAYER_ADDRESS.getName()));
			String webhookUrl = propertiesManager.propertiesMap.get(Constants.UPI_WEBHOOK_RESPONSE_URL);
			webhookUrl = webhookUrl + fields.get(FieldType.PG_REF_NUM.getName());
			jsonRequest.addProperty("webhook", webhookUrl);
		} else {
			jsonRequest.addProperty("cardNumber", transactionResponse.getCard());
			jsonRequest.addProperty("expMonth", transactionResponse.getExpMonth());
			jsonRequest.addProperty("expYear", transactionResponse.getExpYear());
			jsonRequest.addProperty("cvv", transactionResponse.getCvv());
			jsonRequest.addProperty("cardHolderName", transactionResponse.getMember());
			jsonRequest.addProperty("saveCard", "false");
		}
		return jsonRequest.toString();
	}

	public String createNBRefundRequest(Fields fields) throws SystemException {
		JsonObject jsonRequest = new JsonObject();
		String cuurencyAlpha = Currency.getAlphabaticCode(fields.get(FieldType.CURRENCY_CODE.getName()));
		jsonRequest.addProperty("currency", cuurencyAlpha);
		jsonRequest.addProperty("amount", fields.get(FieldType.AMOUNT.getName()));
		return jsonRequest.toString();
	}
}
