package com.pay10.pinelabs;

import java.io.UnsupportedEncodingException;
import java.util.Base64;
import java.util.regex.Pattern;

import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.google.gson.JsonObject;
import com.pay10.commons.exception.SystemException;
import com.pay10.commons.util.Amount;
import com.pay10.commons.util.FieldType;
import com.pay10.commons.util.Fields;
import com.pay10.commons.util.MopType;
import com.pay10.commons.util.PaymentType;
import com.pay10.commons.util.PropertiesManager;
import com.pay10.commons.util.TransactionType;
import com.pay10.pg.core.util.AcquirerTxnAmountProvider;
import com.pay10.pg.core.whitelabel.ReturnUrlCustomizer;

@Service("pinelabsTransactionConverter")
public class TransactionConverter {

	private static Logger logger = LoggerFactory.getLogger(TransactionConverter.class.getName());

	@Autowired
	private AcquirerTxnAmountProvider acquirerTxnAmountProvider;

	@Autowired
	private ReturnUrlCustomizer returnUrlCustomizer;

	@SuppressWarnings("incomplete-switch")
	public String perpareRequest(Fields fields, Transaction transaction, String token) throws SystemException {

		String request = null;

		switch (TransactionType.getInstance(fields.get(FieldType.TXNTYPE.getName()))) {
		case AUTHORISE:
		case ENROLL:
			break;
		case REFUND:
			request = refundRequest(fields, transaction);
			break;
		case SALE:
			request = saleRequest(fields, transaction, token);
			break;
		case CAPTURE:
			break;
		case STATUS:
			request = statusEnquiryRequest(fields, transaction);
			break;
		}
		return request;

	}

	public String saleRequest(Fields fields, Transaction transaction, String token) throws SystemException {

		String returnUrl = returnUrlCustomizer.customizeReturnUrl(fields,
				PropertiesManager.propertiesMap.get(Constants.SALE_RETURN_URL));
		returnUrl = PropertiesManager.propertiesMap.get(Constants.SALE_RETURN_URL);
		JsonObject jsonRequest = new JsonObject();
		prepareSaleRequest(jsonRequest, fields, returnUrl, token);
		String request = jsonRequest.toString();
		request = request.replaceAll("\\\\", "");
		request = request.replaceAll("\"\\{", "{");
		request = request.replaceAll("\\}\"", "}");
		logger.info("Pinelabs Final payment Request -------" + request);
		return request;

	}

	public String refundRequest(Fields fields, Transaction transaction) throws SystemException {

		try {

			String amount = acquirerTxnAmountProvider.amountProvider(fields);
			amount = Amount.formatAmount(amount, fields.get(FieldType.CURRENCY_CODE.getName()));
			StringBuffer request = new StringBuffer();
			request.append(Constants.pccAmt);
			request.append(Constants.EQUAL_SEPARATOR);
			request.append(amount);
			request.append("&");
			request.append(Constants.pccCurrencyCode);
			request.append(Constants.EQUAL_SEPARATOR);
			request.append(fields.get(FieldType.CURRENCY_CODE.getName()));
			request.append("&");
			request.append(Constants.pccMerchantAccessCode);
			request.append(Constants.EQUAL_SEPARATOR);
			request.append(fields.get(FieldType.ADF1.getName()));
			request.append("&");
			request.append(Constants.pccMerchantId);
			request.append(Constants.EQUAL_SEPARATOR);
			request.append(fields.get(FieldType.MERCHANT_ID.getName()));
			request.append("&");
			request.append(Constants.transactionId);
			request.append(Constants.EQUAL_SEPARATOR);
			request.append(fields.get(FieldType.ACQ_ID.getName()));
			request.append("&");
			request.append(Constants.pccTxnType);
			request.append(Constants.EQUAL_SEPARATOR);
			request.append(Constants.TxnTypeRefund);
			request.append("&");
			request.append(Constants.pccUniqueMid);
			request.append(Constants.EQUAL_SEPARATOR);
			request.append(fields.get(FieldType.TXN_ID.getName()));

			String requestBase64 = request.toString();// .getEncoder().encodeToString(request.toString().getBytes("UTF-8"));
			String key = fields.get(FieldType.TXN_KEY.getName());
			PinelabsUtils pinelabs = new PinelabsUtils();
			// logger.info("Pinelabs : Refund string for generating DIA SECRET -->" +
			// requestBase64);
			request.append("&");
			request.append(Constants.pccDiaSecret);
			request.append(Constants.EQUAL_SEPARATOR);
			request.append(pinelabs.GenerateHash(requestBase64, key));
			request.append("&");
			request.append(Constants.pccDiaSecretType);
			request.append(Constants.EQUAL_SEPARATOR);
			request.append(Constants.diaSecretType);

			logger.info("Pinelabs : Final request string for Refund -->" + request.toString());

			return request.toString();

		}

		catch (Exception e) {
			logger.error("Exception in generating pinelabs refund request", e);
			return null;
		}
	}

	public String statusEnquiryRequest(Fields fields, Transaction transaction) throws SystemException {

		try {

			String amount = acquirerTxnAmountProvider.amountProvider(fields);
			amount = Amount.formatAmount(amount, fields.get(FieldType.CURRENCY_CODE.getName()));
			StringBuffer request = new StringBuffer();
			request.append(Constants.pccAmt);
			request.append(Constants.EQUAL_SEPARATOR);
			request.append(amount);
			request.append("&");
			request.append(Constants.pccCurrencyCode);
			request.append(Constants.EQUAL_SEPARATOR);
			request.append(fields.get(FieldType.CURRENCY_CODE.getName()));
			request.append("&");
			request.append(Constants.pccMerchantAccessCode);
			request.append(Constants.EQUAL_SEPARATOR);
			request.append(fields.get(FieldType.ADF1.getName()));
			request.append("&");
			request.append(Constants.pccMerchantId);
			request.append(Constants.EQUAL_SEPARATOR);
			request.append(fields.get(FieldType.MERCHANT_ID.getName()));
			request.append("&");
//			request.append(Constants.transactionId);
//			request.append(Constants.EQUAL_SEPARATOR);
//			request.append(fields.get(FieldType.ACQ_ID.getName()));
//			request.append("&");
			request.append(Constants.pccTxnType);
			request.append(Constants.EQUAL_SEPARATOR);
			request.append(Constants.TxnTypeEnquiry);
			request.append("&");
			request.append(Constants.pccUniqueMid);
			request.append(Constants.EQUAL_SEPARATOR);
			request.append(fields.get(FieldType.PG_REF_NUM.getName()));

			String requestBase64 = request.toString();// .getEncoder().encodeToString(request.toString().getBytes("UTF-8"));
			String key = fields.get(FieldType.TXN_KEY.getName());
			PinelabsUtils pinelabs = new PinelabsUtils();
			logger.info("Pinelabs : status enquiry string for generating DIA SECRET -->" + requestBase64);
			request.append("&");
			request.append(Constants.pccDiaSecret);
			request.append(Constants.EQUAL_SEPARATOR);
			request.append(pinelabs.GenerateHash(requestBase64, key));
			request.append("&");
			request.append(Constants.pccDiaSecretType);
			request.append(Constants.EQUAL_SEPARATOR);
			request.append(Constants.diaSecretType);

			logger.info("Pinelabs : Final request string for status enquiry -->" + request.toString());

			return request.toString();

		}

		catch (Exception e) {
			logger.error("Exception in generating pinelabs Status enquiry request", e);
			return null;
		}

	}

	public Transaction toTransaction(String response, String currencyCode) {

		Transaction transaction = new Transaction();
		try {

			JSONObject resJson = new JSONObject(response);
			if (response.contains(Constants.orderId)) {
				transaction.setMerchantTxnId(resJson.get(Constants.orderId).toString());
			}

			if (response.contains(Constants.responseAmt)) {
				transaction.setCaptureAmt(resJson.get(Constants.responseAmt).toString());
			}

			if (response.contains(Constants.pinePGTxn_id)) {
				transaction.setPineTxnId(resJson.get(Constants.pinePGTxn_id).toString());
			}

			if (response.contains(Constants.parentTxnStatus)) {
				transaction.setParentTxnStatus(resJson.get(Constants.parentTxnStatus).toString());
			}

			if (response.contains(Constants.paymentMode)) {
				transaction.setPaymentMode(resJson.get(Constants.paymentMode).toString());
			}

			if (response.contains(Constants.pineTxnStatus)) {
				transaction.setPineTxnStatus(resJson.get(Constants.pineTxnStatus).toString());
			}
			if (response.contains(Constants.txnCompeletionDT)) {
				transaction.setTxnCompeletionDT(resJson.get(Constants.txnCompeletionDT).toString());
			}
			if (response.contains(Constants.acquirerName)) {
				transaction.setAcquirerName(resJson.get(Constants.acquirerName).toString());
			}
			if (response.contains(Constants.TxnAmount)) {
				transaction.setTxnAmt(Amount.toDecimal(resJson.get(Constants.TxnAmount).toString(), currencyCode));
			}
			if (response.contains(Constants.txnResponseMsg)) {
				transaction.setTxnResponseMsg(resJson.get(Constants.txnResponseMsg).toString());
			}
			if (response.contains(Constants.parentTxnMsg)) {
				transaction.setParentTxnMsg(resJson.get(Constants.parentTxnMsg).toString());
			}
			if (response.contains(Constants.parentTxnCode)) {
				transaction.setParentTxnCode(resJson.get(Constants.parentTxnCode).toString());
			}
		}

		catch (Exception e) {
			logger.error("Exception", e);
		}

		return transaction;
	}

	public void logRequest(String requestMessage, Fields fields) {
		log("Request message to pinelabs  : Url= " + requestMessage, fields);
	}

	private void log(String message, Fields fields) {
		message = Pattern.compile("(<CardNumber>)([\\s\\S]*?)(</card>)").matcher(message).replaceAll("$1$3");
		message = Pattern.compile("(<ExpiryDate>)([\\s\\S]*?)(</pan>)").matcher(message).replaceAll("$1$3");
		message = Pattern.compile("(<CardSecurityCode>)([\\s\\S]*?)(</expmonth>)").matcher(message).replaceAll("$1$3");
		message = Pattern.compile("(<TerminalId>)([\\s\\S]*?)(</expyear>)").matcher(message).replaceAll("$1$3");
		message = Pattern.compile("(<PassCode>)([\\s\\S]*?)(</cvv2>)").matcher(message).replaceAll("$1$3");
		// message =
		// Pattern.compile("(<password>)([\\s\\S]*?)(</password>)").matcher(message).replaceAll("$1$3");
		MDC.put(FieldType.INTERNAL_CUSTOM_MDC.getName(), fields.getCustomMDC());
		logger.info(message);
	}

	public String tokenRequest(Fields fields) throws SystemException {

		String amount = acquirerTxnAmountProvider.amountProvider(fields);
		amount = Amount.formatAmount(amount, fields.get(FieldType.CURRENCY_CODE.getName()));

		String returnUrl = PropertiesManager.propertiesMap.get(Constants.SALE_RETURN_URL);
		JsonObject jsonRequest = new JsonObject();

		// prepare Merchant data
		JsonObject jsonRequestMData = new JsonObject();
		jsonRequestMData.addProperty(Constants.merchantId, fields.get(FieldType.MERCHANT_ID.getName()));
		jsonRequestMData.addProperty(Constants.mAccessCode, fields.get(FieldType.ADF1.getName()));
		jsonRequestMData.addProperty(Constants.returnUrl, returnUrl);
		jsonRequestMData.addProperty(Constants.orderId, fields.get(FieldType.PG_REF_NUM.getName()));
		jsonRequest.addProperty(Constants.merchant_data, jsonRequestMData.toString());
		// prepare Customer data
		JsonObject jsonRequestCData = new JsonObject();
		jsonRequest.addProperty(Constants.customer_data, jsonRequestCData.toString());

		// prepare payment data
		JsonObject jsonRequestPData = new JsonObject();
		jsonRequestPData.addProperty(Constants.TxnAmount, amount);
		jsonRequest.addProperty(Constants.payment_data, jsonRequestPData.toString());

		// prepare txn data
		JsonObject jsonRequestTData = new JsonObject();
		jsonRequestTData.addProperty(Constants.navigation_mode, Constants.navigation_mode_val);
		jsonRequestTData.addProperty(Constants.paymentMode,
				PinelabsMopType.getBankCode(fields.get(FieldType.PAYMENT_TYPE.getName())));
		jsonRequestTData.addProperty(Constants.transactionType, Constants.transactionType_val);
		// jsonRequestPData.addProperty(Constants.time_stamp,
		// Constants.navigation_mode_val);
		jsonRequest.addProperty(Constants.txn_data, jsonRequestTData.toString());
		String request = jsonRequest.toString();
		logger.info("Pinelabs Final Token Request -------" + jsonRequest.toString());
		try {
			request = request.replaceAll("\\\\", "");
			request = request.replaceAll("\"\\{", "{");
			request = request.replaceAll("\\}\"", "}");
			logger.info("1.Pinelabs Final Token Request -------" + request);
			request = Base64.getEncoder().encodeToString(request.getBytes("UTF-8"));
			jsonRequest = new JsonObject();
			jsonRequest.addProperty("request", request);
			logger.info("Pinelabs Final payment Request base64-------" + jsonRequest.toString());
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return jsonRequest.toString();

	}

	public String tokenXverify(String request, Fields fields) {

		String xverify = "";
		JSONObject jsonRequest = new JSONObject(request);
		String requestBase64 = jsonRequest.getString("request");
		String key = fields.get(FieldType.TXN_KEY.getName());
		PinelabsUtils pinelabs = new PinelabsUtils();
		xverify = pinelabs.GenerateHash(requestBase64, key);
		logger.info("Pinelabs Final token Request - X-verify-------" + xverify);
		return xverify;
	}

	public Transaction toTransactionStatus(String response) {

		Transaction transaction = new Transaction();

		JSONObject resJson = new JSONObject(response);

		if (response.contains(Constants.pccTxnResponseCode)) {
			if (response.contains(Constants.pccTxnResponseCode)) {
				transaction.setResponseCode(resJson.get(Constants.pccTxnResponseCode).toString());
			}
			if (response.contains(Constants.pccTxnResponseMessage)) {
				transaction.setResponseMsg(resJson.get(Constants.pccTxnResponseMessage).toString());
			}
			if (response.contains(Constants.pcc_Parent_TxnStatus)) {
				transaction.setPineTxnStatus(resJson.get(Constants.pcc_Parent_TxnStatus).toString());
			}
			if (response.contains(Constants.transactionId)) {
				transaction.setPineTxnId(resJson.get(Constants.transactionId).toString());
			}
			if (response.contains(Constants.pccUniqueMid)) {
				transaction.setMerchantTxnId(resJson.get(Constants.pccUniqueMid).toString());
			}
			if (response.contains(Constants.pccPinePGTxnStatus)) {
				transaction.setPpcPinePGTxnStatus(resJson.get(Constants.pccPinePGTxnStatus).toString());
			}

		}

		return transaction;
	}

	public Transaction toTransactionRefund(String response) {

		Transaction transaction = new Transaction();

		JSONObject resJson = new JSONObject(response);

		if (response.contains(Constants.pccTxnResponseCode)) {
			if (response.contains(Constants.pccTxnResponseCode)) {
				transaction.setResponseCode(resJson.get(Constants.pccTxnResponseCode).toString());
			}
			if (response.contains(Constants.pccTxnResponseMessage)) {
				transaction.setResponseMsg(resJson.get(Constants.pccTxnResponseMessage).toString());
			}
			if (response.contains(Constants.pccPinePGTxnStatus)) {
				transaction.setPineTxnStatus(resJson.get(Constants.pccPinePGTxnStatus).toString());
			}
			if (response.contains(Constants.transactionId)) {
				transaction.setRefundId(resJson.get(Constants.transactionId).toString());
			}
			// ppc_PinePGTxnStatus=6,ppc_TxnResponseCode=1,ppc_TxnResponseMessage=SUCCESS

		}

		return transaction;
	}

	public Transaction toTransactionToken(String response) {

		Transaction transaction = new Transaction();

		JSONObject resJson = new JSONObject(response);

		if (response.contains(Constants.token)) {
			if (response.contains(Constants.token)) {
				transaction.setToken(resJson.get(Constants.token).toString());
			}
			if (response.contains(Constants.response_code)) {
				transaction.setResponseCode(resJson.get(Constants.response_code).toString());
			}
			if (response.contains(Constants.responseMsg)) {
				transaction.setResponseMsg(resJson.get(Constants.responseMsg).toString());
			}

		}

		return transaction;
	}

	public Transaction toTransactionPayment(String response) {

		Transaction transaction = new Transaction();

		JSONObject resJson = new JSONObject(response);

		if (response.contains(Constants.redirect_url)) {
			if (response.contains(Constants.redirect_url)) {
				transaction.setToken(resJson.get(Constants.redirect_url).toString());
			}
			if (response.contains(Constants.response_code)) {
				transaction.setResponseCode(resJson.get(Constants.response_code).toString());
			}
			if (response.contains(Constants.responseMsg)) {
				transaction.setResponseMsg(resJson.get(Constants.responseMsg).toString());
			}

		}

		return transaction;
	}

	private void prepareSaleRequest(JsonObject jsonRequest, Fields fields, String returnUrl, String token) {

		JsonObject jsonRequestData = new JsonObject();
		logger.info("fields.get(FieldType.PAYMENT_TYPE.getName()) ---" + fields.get(FieldType.PAYMENT_TYPE.getName()));
		if (fields.get(FieldType.PAYMENT_TYPE.getName()).equalsIgnoreCase(PaymentType.CREDIT_CARD.getCode())
				|| fields.get(FieldType.PAYMENT_TYPE.getName()).equalsIgnoreCase(PaymentType.DEBIT_CARD.getCode())) {

			logger.info("pinelabs Request before adding payment parameters  incase of cards " + jsonRequest.toString());
			jsonRequestData = new JsonObject();
			String expDate = fields.get(FieldType.CARD_EXP_DT.getName());

			jsonRequestData.addProperty(Constants.card_number, fields.get(FieldType.CARD_NUMBER.getName()));
			jsonRequestData.addProperty(Constants.card_holder, fields.get(FieldType.CARD_HOLDER_NAME.getName()));
			jsonRequestData.addProperty(Constants.card_expiry_month, expDate.substring(0, 2));
			jsonRequestData.addProperty(Constants.card_expiry_year, expDate.substring(2, 6));
			jsonRequestData.addProperty(Constants.card_cvv, fields.get(FieldType.CVV.getName()));
			jsonRequest.addProperty(Constants.paymentOptionCard, jsonRequestData.toString());

		} else if (fields.get(FieldType.PAYMENT_TYPE.getName()).equalsIgnoreCase(PaymentType.UPI.getCode())) {
			jsonRequestData = new JsonObject();
			if(fields.get(FieldType.MOP_TYPE.getName()).equalsIgnoreCase(MopType.GOOGLEPAY.getCode())) {
				//{ "upi_data": { "mobile_no": "9999999999", " upi_option": "GPAY" } }
				String gpaynum = fields.get(FieldType.PAYER_PHONE.getName()).replace("+91", "");
				
				jsonRequestData.addProperty(Constants.upi_upi_option, Constants.upi_option_gpay);
				jsonRequestData.addProperty(Constants.customerPhone, gpaynum);
				jsonRequest.addProperty(Constants.paymentOptionUpi, jsonRequestData.toString());
			}else {
				jsonRequestData.addProperty(Constants.upi_upi_option, Constants.upi_option_upi);
				jsonRequestData.addProperty(Constants.upi_vpa, fields.get(FieldType.PAYER_ADDRESS.getName()));
				jsonRequest.addProperty(Constants.paymentOptionUpi, jsonRequestData.toString());
			}
			
			logger.info("pinelabs Request for UPI " + jsonRequest.toString());

			fields.put(FieldType.CARD_MASK.getName(), fields.get(FieldType.PAYER_ADDRESS.getName()));

		} else if (fields.get(FieldType.PAYMENT_TYPE.getName()).equalsIgnoreCase(PaymentType.NET_BANKING.getCode())) {

			jsonRequestData = new JsonObject();
			jsonRequestData.addProperty(Constants.paymentCode,
					PinelabsMopType.getBankCode(fields.get(FieldType.MOP_TYPE.getName())));
			//jsonRequestData.addProperty(Constants.paymentCode, "NB1493");
			jsonRequest.addProperty(Constants.paymentOptionNB, jsonRequestData.toString());
			logger.info("pinelabs Request for NB " + jsonRequest.toString());

		} else if (fields.get(FieldType.PAYMENT_TYPE.getName()).equalsIgnoreCase(PaymentType.WALLET.getCode())) {
			jsonRequestData = new JsonObject();
			jsonRequestData.addProperty(Constants.wallet_code,
					PinelabsMopType.getBankCode(fields.get(FieldType.MOP_TYPE.getName())));
			// jsonRequestData.addProperty(Constants.w_mobile_number,
			// fields.get(FieldType.PAYER_PHONE.getName()));
			logger.info("wallet mobile number : " + fields.get(FieldType.PAYER_ADDRESS.getName()));
			jsonRequestData.addProperty(Constants.w_mobile_number, "");
			jsonRequest.addProperty(Constants.paymentOptionWallet, jsonRequestData.toString());
			logger.info("pinelabs Request for Wallet " + jsonRequest.toString());

		}

	}

}
