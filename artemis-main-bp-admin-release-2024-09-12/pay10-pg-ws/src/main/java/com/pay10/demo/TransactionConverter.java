package com.pay10.demo;

import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.pay10.commons.dao.FieldsDao;
import com.pay10.commons.exception.SystemException;
import com.pay10.commons.util.Currency;
import com.pay10.commons.util.FieldType;
import com.pay10.commons.util.Fields;
import com.pay10.commons.util.PaymentType;
import com.pay10.commons.util.PropertiesManager;
import com.pay10.commons.util.TransactionType;
import com.pay10.pg.core.util.AcquirerTxnAmountProvider;
import com.pay10.pg.core.util.CashfreeChecksumUtil;
import com.pay10.pg.core.whitelabel.ReturnUrlCustomizer;
import com.google.gson.JsonObject;

@Service("demoTransactionConverter")
public class TransactionConverter {

	private static Logger logger = LoggerFactory.getLogger(TransactionConverter.class.getName());

	@Autowired
	private AcquirerTxnAmountProvider acquirerTxnAmountProvider;

	@Autowired
	private ReturnUrlCustomizer returnUrlCustomizer;

	@Autowired
	private CashfreeChecksumUtil demoChecksumUtil;

	@Autowired
	private FieldsDao fieldsDao;

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
		case CAPTURE:
			break;
		case STATUS:
			request = statusEnquiryRequest(fields, transaction);
			break;
		}
		return request;

	}

	public String saleRequest(Fields fields, Transaction transaction) throws SystemException {

		String amount = acquirerTxnAmountProvider.amountProvider(fields);
		String returnUrl = returnUrlCustomizer.customizeReturnUrl(fields,
				PropertiesManager.propertiesMap.get(Constants.SALE_RETURN_URL));

		JsonObject jsonRequest = new JsonObject();
		prepareSaleRequest(jsonRequest, fields, returnUrl, amount);

		String signature = demoChecksumUtil.getHash(jsonRequest, fields.get(FieldType.TXN_KEY.getName()));
		jsonRequest.addProperty(Constants.signature, signature);

		return jsonRequest.toString();

	}

	public String refundRequest(Fields fields, Transaction transaction) throws SystemException {

		try {

			String amount = acquirerTxnAmountProvider.amountProvider(fields);
			StringBuilder sb = new StringBuilder();

			sb.append("appId=" + fields.get(FieldType.MERCHANT_ID.getName()));
			sb.append("&");
			sb.append("secretKey=" + fields.get(FieldType.TXN_KEY.getName()));
			sb.append("&");
			sb.append("referenceId=" + fields.get(FieldType.ACQ_ID.getName()));
			sb.append("&");
			sb.append("refundAmount=" + amount);
			sb.append("&");
			sb.append("refundNote=" + "Refund");

			return sb.toString();

		}

		catch (Exception e) {
			logger.error("Exception in generating cashfree refund request", e);
			return null;
		}
	}

	public String statusEnquiryRequest(Fields fields, Transaction transaction) throws SystemException {

		StringBuilder sb = new StringBuilder();

		sb.append("appId=" + fields.get(FieldType.MERCHANT_ID.getName()));
		sb.append("&");
		sb.append("secretKey=" + fields.get(FieldType.TXN_KEY.getName()));
		sb.append("&");
		sb.append("orderId=" + fields.get(FieldType.PG_REF_NUM.getName()));

		return sb.toString();

	}

	public Transaction toTransaction(String response) {

		Transaction transaction = new Transaction();
		try {

			JSONObject resJson = new JSONObject(response);

			if (response.contains(Constants.orderId)) {
				transaction.setOrderId(resJson.get(Constants.orderId).toString());
			}

			if (response.contains(Constants.orderAmount)) {
				transaction.setOrderAmount(resJson.get(Constants.orderAmount).toString());
			}

			if (response.contains(Constants.referenceId)) {
				transaction.setReferenceId(resJson.get(Constants.referenceId).toString());
			}

			if (response.contains(Constants.txStatus)) {
				transaction.setTxStatus(resJson.get(Constants.txStatus).toString());
			}

			if (response.contains(Constants.paymentMode)) {
				transaction.setPaymentMode(resJson.get(Constants.paymentMode).toString());
			}

			if (response.contains(Constants.txMsg)) {
				transaction.setTxMsg(resJson.get(Constants.txMsg).toString());
			}
		}

		catch (Exception e) {
			logger.error("Exception", e);
		}

		return transaction;
	}

	public void logRequest(String requestMessage, Fields fields) {
		log("Request message to Cashfree  : Url= " + requestMessage, fields);
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

	private void prepareSaleRequest(JsonObject jsonRequest, Fields fields, String returnUrl, String amount) {

		jsonRequest.addProperty(Constants.appId, fields.get(FieldType.MERCHANT_ID.getName()));
		jsonRequest.addProperty(Constants.orderId, fields.get(FieldType.PG_REF_NUM.getName()));
		jsonRequest.addProperty(Constants.orderAmount, amount);
		jsonRequest.addProperty(Constants.orderCurrency,
				Currency.getAlphabaticCode(fields.get(FieldType.CURRENCY_CODE.getName())));

		if (StringUtils.isNotBlank(fields.get(FieldType.CUST_NAME.getName()))) {
			jsonRequest.addProperty(Constants.customerName, fields.get(FieldType.CUST_NAME.getName()));
		} else {
			jsonRequest.addProperty(Constants.customerName, "NA");
		}

		if (StringUtils.isNotBlank(fields.get(FieldType.CUST_EMAIL.getName()))) {
			jsonRequest.addProperty(Constants.customerEmail, fields.get(FieldType.CUST_EMAIL.getName()));
		} else {
			jsonRequest.addProperty(Constants.customerEmail, "support.txn@asiancheckout.com");
		}

		if (StringUtils.isNotBlank(fields.get(FieldType.CUST_PHONE.getName()))) {
			jsonRequest.addProperty(Constants.customerPhone, fields.get(FieldType.CUST_PHONE.getName()));
		} else {
			//Added by Sonu Chaudhari HardCoded CustomerPhone Number
			jsonRequest.addProperty(Constants.customerPhone, "9767146866");
		}

		jsonRequest.addProperty(Constants.returnUrl, returnUrl);

		if (fields.get(FieldType.PAYMENT_TYPE.getName()).equalsIgnoreCase(PaymentType.CREDIT_CARD.getCode())
				|| fields.get(FieldType.PAYMENT_TYPE.getName()).equalsIgnoreCase(PaymentType.DEBIT_CARD.getCode())) {

			logger.info("Cashfree Request before adding payment parameters  incase of cards " + jsonRequest.toString());

			String expDate = fields.get(FieldType.CARD_EXP_DT.getName());

			jsonRequest.addProperty(Constants.paymentOption, Constants.paymentOptionCard);
			jsonRequest.addProperty(Constants.card_number, fields.get(FieldType.CARD_NUMBER.getName()));
			jsonRequest.addProperty(Constants.card_holder, fields.get(FieldType.CARD_HOLDER_NAME.getName()));
			jsonRequest.addProperty(Constants.card_expiryMonth, expDate.substring(0, 2));
			jsonRequest.addProperty(Constants.card_expiryYear, expDate.substring(2, 6));
			jsonRequest.addProperty(Constants.card_cvv, fields.get(FieldType.CVV.getName()));

		} else if (fields.get(FieldType.PAYMENT_TYPE.getName()).equalsIgnoreCase(PaymentType.UPI.getCode())) {
			jsonRequest.addProperty(Constants.paymentOption, Constants.paymentOptionUPI);
			jsonRequest.addProperty(Constants.upi_vpa, fields.get(FieldType.PAYER_ADDRESS.getName()));

			logger.info("Cashfree Request for UPI " + jsonRequest.toString());

			fields.put(FieldType.CARD_MASK.getName(), fields.get(FieldType.PAYER_ADDRESS.getName()));

		} else if (fields.get(FieldType.PAYMENT_TYPE.getName()).equalsIgnoreCase(PaymentType.NET_BANKING.getCode())) {

			jsonRequest.addProperty(Constants.paymentOption, Constants.paymentOptionNB);
			jsonRequest.addProperty(Constants.paymentCode,
					DemoMopType.getBankCode(fields.get(FieldType.MOP_TYPE.getName())));

			logger.info("Cashfree Request for NB " + jsonRequest.toString());

		} else if (fields.get(FieldType.PAYMENT_TYPE.getName()).equalsIgnoreCase(PaymentType.WALLET.getCode())) {

			jsonRequest.addProperty(Constants.paymentOption, Constants.paymentOptionWallet);
			jsonRequest.addProperty(Constants.paymentCode,
					DemoMopType.getBankCode(fields.get(FieldType.MOP_TYPE.getName())));

			logger.info("Cashfree Request for Wallet " + jsonRequest.toString());

		}

	}

	public Transaction toTransactionStatus(String response) {

		Transaction transaction = new Transaction();
		try {

			JSONObject resJson = new JSONObject(response);

			if (response.contains(Constants.orderStatus)) {
				transaction.setOrderStatus(resJson.get(Constants.orderStatus).toString());
			}

			if (response.contains(Constants.orderAmount)) {
				transaction.setOrderAmount(resJson.get(Constants.orderAmount).toString());
			}

			if (response.contains(Constants.status)) {
				transaction.setStatus(resJson.get(Constants.status).toString());
			}

			if (response.contains(Constants.txStatus)) {
				transaction.setTxStatus(resJson.get(Constants.txStatus).toString());
			}

			if (response.contains(Constants.txMsg)) {
				transaction.setTxMsg(resJson.get(Constants.txMsg).toString());
			}

			if (response.contains(Constants.referenceId)) {
				transaction.setReferenceId(resJson.get(Constants.referenceId).toString());
			}
		}

		catch (Exception e) {
			logger.error("Exception", e);
		}

		return transaction;
	}

	public Transaction toTransactionRefund(String response) {

		Transaction transaction = new Transaction();
		try {

			JSONObject resJson = new JSONObject(response);

			if (response.contains(Constants.message)) {
				transaction.setMessage(resJson.get(Constants.message).toString());
			}

			if (response.contains(Constants.refundId)) {
				transaction.setRefundId(resJson.get(Constants.refundId).toString());
			}

			if (response.contains(Constants.status)) {
				transaction.setStatus(resJson.get(Constants.status).toString());
			}
		}

		catch (Exception e) {
			logger.error("Exception", e);
		}

		return transaction;
	}

}
