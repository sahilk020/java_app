package com.pay10.agreepay;

import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.google.gson.JsonObject;
import com.pay10.commons.dao.FieldsDao;
import com.pay10.commons.exception.SystemException;
import com.pay10.commons.util.Currency;
import com.pay10.commons.util.FieldType;
import com.pay10.commons.util.Fields;
import com.pay10.commons.util.PaymentType;
import com.pay10.commons.util.PropertiesManager;
import com.pay10.commons.util.TransactionType;
import com.pay10.pg.core.util.AcquirerTxnAmountProvider;
import com.pay10.pg.core.util.AgreepayChecksumUtil;
import com.pay10.pg.core.whitelabel.ReturnUrlCustomizer;

@Service("agreepayTransactionConverter")
public class TransactionConverter {

	private static Logger logger = LoggerFactory.getLogger(TransactionConverter.class.getName());

	@Autowired
	private AcquirerTxnAmountProvider acquirerTxnAmountProvider;

	@Autowired
	private ReturnUrlCustomizer returnUrlCustomizer;

	@Autowired
	private AgreepayChecksumUtil agreepayChecksumUtil;

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
		returnUrl = PropertiesManager.propertiesMap.get(Constants.SALE_RETURN_URL);
		JsonObject jsonRequest = new JsonObject();
		prepareSaleRequest(jsonRequest, fields, returnUrl, amount);

		String signature = agreepayChecksumUtil.getHash(jsonRequest, fields.get(FieldType.ADF1.getName()));
		jsonRequest.addProperty(Constants.signature, signature);

		return jsonRequest.toString();

	}

	public String refundRequest(Fields fields, Transaction transaction) throws SystemException {

		try {
			StringBuilder hash_data = new StringBuilder();
			String amount = acquirerTxnAmountProvider.amountProvider(fields);
			StringBuilder sb = new StringBuilder();
			sb.append(Constants.TxnAmount + "=" + amount);
			hash_data.append(amount);
			sb.append("&");
			sb.append(Constants.appId + "=" + fields.get(FieldType.TXN_KEY.getName()));
			hash_data.append( "|" + fields.get(FieldType.TXN_KEY.getName()));
			sb.append("&");
			sb.append(Constants.description + "=" + "Refund");
			hash_data.append( "|Refund");
			sb.append("&");
			sb.append(Constants.merchantOrderId + "=" + fields.get(FieldType.ORIG_TXN_ID.getName())); //This is txn reference number which submitted while making the original transaction
			hash_data.append("|" + fields.get(FieldType.ORIG_TXN_ID.getName()));
			sb.append("&");
			sb.append(Constants.merchantRefundId + "=" + fields.get(FieldType.PG_REF_NUM.getName())); //refund reference number
			hash_data.append("|" + fields.get(FieldType.PG_REF_NUM.getName()));
			sb.append("&");
			sb.append(Constants.transactionId + "=" + fields.get(FieldType.ACQ_ID.getName()));
			hash_data.append("|" + fields.get(FieldType.ACQ_ID.getName()));
			// getting hashing...
			logger.info("getting hash for agreepay refund request"+ hash_data);
			String signature = agreepayChecksumUtil.getHashRefund(hash_data.toString(), fields.get(FieldType.ADF1.getName()));
			sb.append("&");
			sb.append(Constants.signature + "=" + signature);
			logger.info("final agreepay refund request"+ sb.toString());
			return sb.toString();

		}

		catch (Exception e) {
			logger.error("Exception in generating agreepay refund request", e);
			return null;
		}
	}

	public String statusEnquiryRequest(Fields fields, Transaction transaction) throws SystemException {

		StringBuilder sb = new StringBuilder();
		String hash_data = "";
		sb.append(Constants.appId + "=" + fields.get(FieldType.TXN_KEY.getName()));
		sb.append("&");
		sb.append(Constants.orderId + "=" + fields.get(FieldType.ORIG_TXN_ID.getName()));
		sb.append("&");
		sb.append(Constants.transactionId + "=" + transaction.getReferenceId());
		hash_data = fields.get(FieldType.TXN_KEY.getName()) + "|" + fields.get(FieldType.ORIG_TXN_ID.getName()) + "|"
				+ transaction.getReferenceId();
		String signature = agreepayChecksumUtil.getHashRefund(hash_data, fields.get(FieldType.ADF1.getName()));
		sb.append("&");
		sb.append(Constants.signature + "=" + signature);
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

			if (response.contains(Constants.transactionId)) {
				transaction.setReferenceId(resJson.get(Constants.transactionId).toString());
			}

			if (response.contains(Constants.txStatus)) {
				transaction.setTxStatus(resJson.get(Constants.txStatus).toString());
			}

			if (response.contains(Constants.paymentOption)) {
				transaction.setPaymentMode(resJson.get(Constants.paymentOption).toString());
			}

			if (response.contains(Constants.txMsg)) {
				transaction.setTxMsg(resJson.get(Constants.txMsg).toString());
			}
			if (response.contains(Constants.errorDesc)) {
				transaction.setTxErrorDes(resJson.get(Constants.errorDesc).toString());
			}
		}

		catch (Exception e) {
			logger.error("Exception", e);
		}

		return transaction;
	}

	public void logRequest(String requestMessage, Fields fields) {
		log("Request message to agreepay  : Url= " + requestMessage, fields);
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

		jsonRequest.addProperty(Constants.appId, fields.get(FieldType.TXN_KEY.getName()));
		jsonRequest.addProperty(Constants.orderId, fields.get(FieldType.PG_REF_NUM.getName()));
		jsonRequest.addProperty(Constants.mode, "LIVE"); // TEST only for DEMN(Demo Bank)
		jsonRequest.addProperty(Constants.TxnAmount, amount);
		jsonRequest.addProperty(Constants.orderCurrency,
				Currency.getAlphabaticCode(fields.get(FieldType.CURRENCY_CODE.getName())));
		jsonRequest.addProperty(Constants.description, fields.get(FieldType.PRODUCT_DESC.getName()));
		if (StringUtils.isNotBlank(fields.get(FieldType.CUST_NAME.getName()))) {
			jsonRequest.addProperty(Constants.customerName, fields.get(FieldType.CUST_NAME.getName()));
		} else {
			jsonRequest.addProperty(Constants.customerName, "NA");
		}

		if (StringUtils.isNotBlank(fields.get(FieldType.CUST_EMAIL.getName()))) {
			jsonRequest.addProperty(Constants.customerEmail, fields.get(FieldType.CUST_EMAIL.getName()));
		} else {
			jsonRequest.addProperty(Constants.customerEmail, "support.txn@pay10.com");
		}

		if (StringUtils.isNotBlank(fields.get(FieldType.CUST_PHONE.getName()))) {
			jsonRequest.addProperty(Constants.customerPhone, fields.get(FieldType.CUST_PHONE.getName()));
		} else {
			jsonRequest.addProperty(Constants.customerPhone, "999999999");
		}
		if (StringUtils.isNotBlank(fields.get(FieldType.CUST_CITY.getName()))) {
			jsonRequest.addProperty(Constants.city, fields.get(FieldType.CUST_CITY.getName()));
		} else {
			jsonRequest.addProperty(Constants.city, "Jaipur");
		}

		if (StringUtils.isNotBlank(fields.get(FieldType.CUST_COUNTRY.getName()))) {
			jsonRequest.addProperty(Constants.country, fields.get(FieldType.CUST_COUNTRY.getName()));
		} else {
			jsonRequest.addProperty(Constants.country, "IND");
		}
		if (StringUtils.isNotBlank(fields.get(FieldType.CUST_ZIP.getName()))) {
			jsonRequest.addProperty(Constants.zipCode, fields.get(FieldType.CUST_ZIP.getName()));
		} else {
			jsonRequest.addProperty(Constants.zipCode, "302001");
		}

		jsonRequest.addProperty(Constants.returnUrl, returnUrl);

				if (fields.get(FieldType.PAYMENT_TYPE.getName()).equalsIgnoreCase(PaymentType.CREDIT_CARD.getCode())
				|| fields.get(FieldType.PAYMENT_TYPE.getName()).equalsIgnoreCase(PaymentType.DEBIT_CARD.getCode())) {

			logger.info("agreepay Request before adding payment parameters  incase of cards " + jsonRequest.toString());
			// jsonRequest.addProperty(Constants.mode, "TEST"); // TEST only for DEMN(Demo
			// Bank)
			// jsonRequest.addProperty(Constants.paymentOption, "DEMN");

			//
			if (fields.get(FieldType.PAYMENT_TYPE.getName()).equalsIgnoreCase(PaymentType.CREDIT_CARD.getCode())) {
				jsonRequest.addProperty(Constants.paymentOption,
						AgreepayMopType.getBankCode(fields.get(FieldType.MOP_TYPE.getName())) + "C");
			} else {
				jsonRequest.addProperty(Constants.paymentOption,
						AgreepayMopType.getBankCode(fields.get(FieldType.MOP_TYPE.getName())) + "D");
			}

			String expDate = fields.get(FieldType.CARD_EXP_DT.getName());

			jsonRequest.addProperty(Constants.card_number, fields.get(FieldType.CARD_NUMBER.getName()));
			jsonRequest.addProperty(Constants.card_holder, fields.get(FieldType.CARD_HOLDER_NAME.getName()));
			jsonRequest.addProperty(Constants.card_expiryDate, expDate.substring(0, 2) + "/" + expDate.substring(2, 6));
			jsonRequest.addProperty(Constants.card_cvv, fields.get(FieldType.CVV.getName()));

		} else if (fields.get(FieldType.PAYMENT_TYPE.getName()).equalsIgnoreCase(PaymentType.UPI.getCode())) {
			jsonRequest.addProperty(Constants.paymentOption, Constants.paymentOptionUPI);
			jsonRequest.addProperty(Constants.upi_vpa, fields.get(FieldType.PAYER_ADDRESS.getName()));

			logger.info("agreepay Request for UPI " + jsonRequest.toString());

			fields.put(FieldType.CARD_MASK.getName(), fields.get(FieldType.PAYER_ADDRESS.getName()));

		} else if (fields.get(FieldType.PAYMENT_TYPE.getName()).equalsIgnoreCase(PaymentType.NET_BANKING.getCode())) {
//			jsonRequest.addProperty(Constants.mode, "TEST"); // TEST only for DEMN(Demo Bank)
//			jsonRequest.addProperty(Constants.paymentOption, "DEMN");
			jsonRequest.addProperty(Constants.paymentOption,
				AgreepayMopType.getBankCode(fields.get(FieldType.MOP_TYPE.getName())));

			logger.info("agreepay Request for NB " + jsonRequest.toString());

		} else if (fields.get(FieldType.PAYMENT_TYPE.getName()).equalsIgnoreCase(PaymentType.WALLET.getCode())) {

			// jsonRequest.addProperty(Constants.paymentOption,
			// Constants.paymentOptionWallet);
			jsonRequest.addProperty(Constants.paymentOption,
					AgreepayMopType.getBankCode(fields.get(FieldType.MOP_TYPE.getName())));

			logger.info("agreepay Request for Wallet " + jsonRequest.toString());

		}

	}

	public Transaction toTransactionStatus(String response) {

		Transaction transaction = new Transaction();
		try {

			JSONObject resJson = new JSONObject(response);

			if (response.contains("data")) {
				resJson = resJson.getJSONObject("data");
				if (response.contains(Constants.txStatus)) {
					transaction.setOrderStatus(resJson.get(Constants.txStatus).toString());
				}
				if (response.contains(Constants.TxnAmount)) {
					transaction.setOrderAmount(resJson.get(Constants.TxnAmount).toString());
				}
				if (response.contains(Constants.txMsg)) {
					transaction.setTxMsg(resJson.get(Constants.txMsg).toString());
				}

				if (response.contains(Constants.transactionId)) {
					transaction.setReferenceId(resJson.get(Constants.transactionId).toString());
				}
				if (response.contains(Constants.status)) {
					transaction.setStatus(resJson.get(Constants.status).toString());
				}

				if (response.contains(Constants.txStatus)) {
					transaction.setTxStatus(resJson.get(Constants.txStatus).toString());
				}


			} else if (response.contains("error")) {
				if (response.contains(Constants.txMsg)) {
					transaction.setTxMsg(resJson.get(Constants.txMsg).toString());
				}
				if (response.contains(Constants.txStatus)) {
					transaction.setOrderStatus(resJson.get(Constants.txStatus).toString());
				}

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

			if (response.contains("data")) {
				resJson = resJson.getJSONObject("data");
				if (response.contains(Constants.refundId)) {
					transaction.setRefundId(resJson.get(Constants.refundId).toString());
				}
				if (response.contains(Constants.refundRefNo)) {
					transaction.setRefundRefNo(resJson.get(Constants.refundRefNo).toString());
				}
				if (response.contains(Constants.merchantRefundId)) {
					transaction.setMerchantRefundId(resJson.get(Constants.merchantRefundId).toString());
				}
				if (response.contains(Constants.merchantOrderId)) {
					transaction.setMerchantOrderId(resJson.get(Constants.merchantOrderId).toString());
				}
				transaction.setStatus("0");

			} else if (response.contains("error")) {
				resJson = resJson.getJSONObject("error");
				if (response.contains(Constants.message)) {
					transaction.setMessage(resJson.get(Constants.message).toString());
				}
				if (response.contains(Constants.status)) {
					transaction.setStatus(resJson.get(Constants.status).toString());
				}

			}

		}

		catch (Exception e) {
			logger.error("Exception", e);
		}

		return transaction;
	}

}
