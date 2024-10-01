package com.pay10.cashfree;

import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.pay10.commons.dao.FieldsDao;
import com.pay10.commons.exception.ErrorType;
import com.pay10.commons.exception.SystemException;
import com.pay10.commons.user.UserDao;
import com.pay10.commons.util.Currency;
import com.pay10.commons.util.FieldType;
import com.pay10.commons.util.Fields;
import com.pay10.commons.util.PaymentType;
import com.pay10.commons.util.PropertiesManager;
import com.pay10.commons.util.TransactionType;
import com.pay10.pg.core.util.AcquirerTxnAmountProvider;
import com.pay10.pg.core.util.CashfreeChecksumUtil;
import com.pay10.pg.core.whitelabel.ReturnUrlCustomizer;

@Service("cashfreeTransactionConverter")
public class TransactionConverter {

	private static Logger logger = LoggerFactory.getLogger(TransactionConverter.class.getName());

	@Autowired
	private AcquirerTxnAmountProvider acquirerTxnAmountProvider;

	@Autowired
	private ReturnUrlCustomizer returnUrlCustomizer;

	@Autowired
	private CashfreeChecksumUtil cashfreeChecksumUtil;

	@Autowired
	@Qualifier("cashfreeTransactionCommunicator")
	private TransactionCommunicator communicator;

	@Autowired
	private FieldsDao fieldsDao;
	
	@Autowired
	private UserDao userDao;

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
		// String returnUrl = returnUrlCustomizer.customizeReturnUrl(fields,
		// PropertiesManager.propertiesMap.get(Constants.SALE_RETURN_URL));

		// TODO Call Order API's
		CashfreeOrderRespDTO orderResp = getOrderId(fields, transaction, amount);
		logger.info("orderResp received from cashfree :: " + orderResp.toString());

		JSONObject payOrderRequest = new JSONObject();
		prepareSaleRequest(payOrderRequest, fields, orderResp);
		logger.info("Request create for payOrder for cashfree acquirer : " + payOrderRequest);

		// TODO Call PayOrder
		CashfreePayOrderRespDTO payOrderResp = payOrder(payOrderRequest, fields);
		logger.info("Response received for payOrder from cashfree acquirer : " + payOrderResp);

		// if (!tdrMap.isEmpty()) {
		// throw new SystemException(ErrorType.TDR_SETTING_PENDING,
		// ErrorType.TDR_SETTING_PENDING.getResponseCode());
		// }

		/*
		 * if(payOrderResp.getCode() != null) {
		 * logger.info("Throw SystemException For Cashfree Request "); throw new
		 * SystemException(ErrorType.PAYMENT_OPTION_NOT_SUPPORTED,
		 * payOrderResp.getCode()); }
		 */
		if (fields.get(FieldType.PAYMENT_TYPE.getName()).equalsIgnoreCase(PaymentType.UPI.getCode())
				&& fields.get(FieldType.UPI_INTENT.getName()) !=null
				&& fields.get(FieldType.UPI_INTENT.getName()).equalsIgnoreCase("1")) {
			return (String) payOrderResp.getData().getPayload().get("default");
		}
		// return (String) payOrderResp.getData().getPayload().get("default");
		return (String) payOrderResp.getData().getUrl();
	}

	private void prepareSaleRequest(JSONObject jsonRequest, Fields fields, CashfreeOrderRespDTO orderResp) {

		jsonRequest.put("order_token", orderResp.getOrder_token());
		JSONObject paymentMethod = new JSONObject();

		if (fields.get(FieldType.PAYMENT_TYPE.getName()).equalsIgnoreCase(PaymentType.CREDIT_CARD.getCode())
				|| fields.get(FieldType.PAYMENT_TYPE.getName()).equalsIgnoreCase(PaymentType.DEBIT_CARD.getCode())) {

			logger.info("Cashfree Request before adding payment parameters  incase of cards " + jsonRequest.toString());

			JSONObject card = new JSONObject();
			String expDate = fields.get(FieldType.CARD_EXP_DT.getName());
			card.put("channel", "link");
			card.put("card_number", fields.get(FieldType.CARD_NUMBER.getName()));
			card.put("card_holder_name", fields.get(FieldType.CARD_HOLDER_NAME.getName()));
			card.put("card_expiry_mm", expDate.substring(0, 2));
			card.put("card_expiry_yy", expDate.substring(4, 6));
			card.put("card_cvv", fields.get(FieldType.CVV.getName()));

			paymentMethod.put("card", card);
		} else if (fields.get(FieldType.PAYMENT_TYPE.getName()).equalsIgnoreCase(PaymentType.UPI.getCode())) {

			if (fields.get(FieldType.PAYMENT_TYPE.getName()).equalsIgnoreCase(PaymentType.UPI.getCode())
					&& fields.get(FieldType.UPI_INTENT.getName()) != null
					&& fields.get(FieldType.UPI_INTENT.getName()).equalsIgnoreCase("1")) {

				JSONObject upi = new JSONObject();
				upi.put("channel", "link");

				paymentMethod.put("upi", upi);
				logger.info("Cashfree Request for Intent UPI " + jsonRequest.toString());

			} else {
				
				JSONObject upi = new JSONObject();
				upi.put("channel", "collect");
				upi.put("upi_id", fields.get(FieldType.PAYER_ADDRESS.getName()));

				paymentMethod.put("upi", upi);
				logger.info("Cashfree Request for UPI " + jsonRequest.toString());
			}
			
		} else if (fields.get(FieldType.PAYMENT_TYPE.getName()).equalsIgnoreCase(PaymentType.NET_BANKING.getCode())) {

			JSONObject netbanking = new JSONObject();
			netbanking.put("channel", "link");
			netbanking.put("netbanking_bank_code",
					Float.parseFloat(CashfreeMopType.getBankCode(fields.get(FieldType.MOP_TYPE.getName()))));

			paymentMethod.put("netbanking", netbanking);
			logger.info("Cashfree Request for NB " + jsonRequest.toString());

		} else if (fields.get(FieldType.PAYMENT_TYPE.getName()).equalsIgnoreCase(PaymentType.WALLET.getCode())) {

			// TODO check wallet flow
			JSONObject app = new JSONObject();
			app.put("channel", "link");
			app.put("provider", CashfreeMopType.getBankCode(fields.get(FieldType.MOP_TYPE.getName())));
			app.put("phone", fields.get(FieldType.CUST_PHONE.getName()));

			paymentMethod.put("app", app);
			logger.info("Cashfree Request for Wallet " + jsonRequest.toString());

			// jsonRequest.put(Constants.paymentOption, Constants.paymentOptionWallet);
			// jsonRequest.put(Constants.paymentCode,
			// CashfreeMopType.getBankCode(fields.get(FieldType.MOP_TYPE.getName())));
			// logger.info("Cashfree Request for Wallet " + jsonRequest.toString());

		}

		jsonRequest.put("payment_method", paymentMethod);
		// logger.info("Prepare Final Request "+jsonRequest);
	}

	private CashfreePayOrderRespDTO payOrder(JSONObject orderResp, Fields fields) {

		logger.info("Request For Cashfree PayOrder For New Flow :: " + orderResp);

		CashfreePayOrderRespDTO cashfreePayOrderResp = new CashfreePayOrderRespDTO();
		String hostUrl = PropertiesManager.propertiesMap.get(Constants.PAY_ORDER_REQUEST_URL);
		// logger.info("hostUrl >>>>>>>>>>>>>>>>> "+hostUrl);
		String payOrderResp = communicator.payOrderPostRequest(orderResp.toString(), hostUrl);

		logger.info("Response Received From Cashfree New Flow payOrder :: " + payOrderResp);
		Gson gson = new Gson();
		cashfreePayOrderResp = gson.fromJson(payOrderResp, CashfreePayOrderRespDTO.class);

		return cashfreePayOrderResp;
	}

	private CashfreeOrderRespDTO getOrderId(Fields fields, Transaction transaction, String amount) throws SystemException {

		// Get Return URL As Merchant URL For MerchantReturnURL Table
		String returnURL = null;
		try {
			logger.info(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>"+fields.getFieldsAsString());
			returnURL = userDao.getMerchantReturnURL(fields.get(FieldType.PAY_ID.getName()));
			logger.info("Merchant Return URL For Cashfree PAYID : " + fields.get(FieldType.PAY_ID.getName())
					+ " and Merchant Return URL :: " + returnURL);
		} catch (SystemException e) {
			throw new SystemException(ErrorType.MERCHANT_RETURN_URL_NOT_MAPPED,
					ErrorType.MERCHANT_RETURN_URL_NOT_MAPPED.getResponseCode());
		}

		if (returnURL == null || returnURL.isEmpty()) {
			logger.info("Merchant return url not mapped for cashfree acquirer in db for the payId :: "
					+ fields.get(FieldType.PAY_ID.getName()));
			throw new SystemException(ErrorType.MERCHANT_RETURN_URL_NOT_MAPPED,
					ErrorType.MERCHANT_RETURN_URL_NOT_MAPPED.getResponseCode());
		}
				
		//String returnURL = PropertiesManager.propertiesMap.get(Constants.SALE_RETURN_URL);
		String notifyURL = PropertiesManager.propertiesMap.get(Constants.RESPONSE_NOTIFY_URL);
		JSONObject orderRequest = new JSONObject();
		JSONObject custInfo = new JSONObject();
		JSONObject orderMeta = new JSONObject();
		JSONObject orderTags = new JSONObject();

		CashfreeOrderRespDTO cashfreeOrderResp = new CashfreeOrderRespDTO();
		logger.info("fields : " + fields.getFieldsAsString());
		logger.info("transaction : " + transaction.toString());
		logger.info("amount : " + amount);

		orderRequest.put("order_id", fields.get(FieldType.PG_REF_NUM.getName()));
		orderRequest.put("order_amount", Float.valueOf(amount));
		orderRequest.put("order_currency", Currency.getAlphabaticCode(fields.get(FieldType.CURRENCY_CODE.getName())));
		// orderRequest.put("order_note", "NA");

		// custInfo.put("customer_id", fields.get(FieldType.CUST_NAME.getName()));
		// custInfo.put("customer_email", fields.get(FieldType.CUST_EMAIL.getName()));
		// custInfo.put("customer_phone",
		// fields.get(FieldType.CUST_PHONE.getName()).substring(fields.get(FieldType.CUST_PHONE.getName()).length()
		// - 10));

		if (StringUtils.isNotBlank(fields.get(FieldType.CUST_NAME.getName()))) {
			custInfo.put("customer_id", fields.get(FieldType.CUST_NAME.getName()));
		} else {
			custInfo.put("customer_id", "NA");
		}

		if (StringUtils.isNotBlank(fields.get(FieldType.CUST_EMAIL.getName()))) {
			custInfo.put("customer_email", fields.get(FieldType.CUST_EMAIL.getName()));
		} else {
			custInfo.put("customer_email", "support.txn@pay10.com");
		}

		if (StringUtils.isNotBlank(fields.get(FieldType.CUST_PHONE.getName()))) {
			custInfo.put("customer_phone", fields.get(FieldType.CUST_PHONE.getName())
					.substring(fields.get(FieldType.CUST_PHONE.getName()).length() - 10));
		} else {
			custInfo.put("customer_phone", "9999999999");
		}

		orderRequest.put("customer_details", custInfo);

		orderMeta.put("notify_url", notifyURL);
		orderMeta.put("return_url", returnURL);
		orderMeta.put("payment_methods", "");
		orderRequest.put("order_meta", orderMeta);

		orderTags.put("newKey", "");
		orderRequest.put("order_tags", orderTags);

		// orderRequest.put("order_expiry_time", "2022-04-20T15:14+0530");

		logger.info("OrderRequest For Cashfree Acquirer : PG_Ref_No : " + orderRequest.toString());

		String hostUrl = PropertiesManager.propertiesMap.get(Constants.GENERATE_ORDER_REQUEST_URL);

		String orderResp = communicator.orderPostRequest(orderRequest.toString(), hostUrl,
				fields.get(FieldType.MERCHANT_ID.getName()), fields.get(FieldType.TXN_KEY.getName()));

		logger.info(" Received Cashfree New Flow Order Response ::  " + orderResp);
		Gson gson = new Gson();
		cashfreeOrderResp = gson.fromJson(orderResp, CashfreeOrderRespDTO.class);

		return cashfreeOrderResp;
	}

	public String refundRequest(Fields fields, Transaction transaction) throws SystemException {

		try {

			logger.info("fields : " + fields.getFieldsAsString());
			logger.info("transaction : " + transaction.toString());
			String amount = acquirerTxnAmountProvider.amountProvider(fields);

			JSONObject refundReq = new JSONObject();
			refundReq.put("refund_amount", amount);
			refundReq.put("refund_id", fields.get(FieldType.REFUND_ORDER_ID.getName()));
			refundReq.put("refund_note", "refund for order " + fields.get(FieldType.REFUND_ORDER_ID.getName()));

			return refundReq.toString();

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

			JSONObject dataInfo = new JSONObject(resJson.get("data").toString());
			JSONObject orderInfo = new JSONObject(dataInfo.get("order").toString());
			JSONObject paymentInfo = new JSONObject(dataInfo.get("payment").toString());

			if (orderInfo.has("order_id")) {
				transaction.setOrderId(orderInfo.get("order_id").toString());
			}

			if (paymentInfo.has("payment_amount")) {
				transaction.setOrderAmount(paymentInfo.get("payment_amount").toString());
			}

			if(paymentInfo.has("cf_payment_id")) {
				transaction.setReferenceId(paymentInfo.get("cf_payment_id").toString());
			}
			
			/*
			 * if (paymentInfo.has("payment_group")) {
			 * 
			 * if (paymentInfo.getString("payment_group").equalsIgnoreCase("upi")) {
			 * transaction.setReferenceId(paymentInfo.get("cf_payment_id").toString()); }
			 * else if (paymentInfo.getString("payment_group").equalsIgnoreCase("WALLET")) {
			 * transaction.setReferenceId(paymentInfo.get("cf_payment_id").toString()); }
			 * else { transaction.setReferenceId(paymentInfo.get("auth_id").toString()); }
			 * 
			 * }
			 */
			
			/*
			 * if (paymentInfo.has("auth_id")) {
			 * transaction.setReferenceId(paymentInfo.get("auth_id").toString()); }
			 */

			// payment_status = "SUCCESS"
			if (paymentInfo.has("payment_status")) {
				transaction.setTxStatus(paymentInfo.get("payment_status").toString());
			}

			if (paymentInfo.has("payment_group")) {
				transaction.setPaymentMode(paymentInfo.get("payment_group").toString());
			}

			if (paymentInfo.has("payment_message")) {
				transaction.setTxMsg(paymentInfo.get("payment_message").toString());
			}
		}

		catch (Exception e) {
			logger.error("Exception", e);
		}

		return transaction;
	}

	public Transaction toTransactionOld(String response) {

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

	private void prepareSaleRequestOld(JsonObject jsonRequest, Fields fields, String returnUrl, String amount) {

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
			// Added by Sonu Chaudhari HardCoded CustomerPhone Number
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
					CashfreeMopType.getBankCode(fields.get(FieldType.MOP_TYPE.getName())));

			logger.info("Cashfree Request for NB " + jsonRequest.toString());

		} else if (fields.get(FieldType.PAYMENT_TYPE.getName()).equalsIgnoreCase(PaymentType.WALLET.getCode())) {

			jsonRequest.addProperty(Constants.paymentOption, Constants.paymentOptionWallet);
			jsonRequest.addProperty(Constants.paymentCode,
					CashfreeMopType.getBankCode(fields.get(FieldType.MOP_TYPE.getName())));

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

			if (response.contains("status_description")) {
				transaction.setMessage(resJson.get("status_description").toString());
			}

			if (response.contains("refund_id")) {
				transaction.setRefundId(resJson.get("refund_id").toString());
			}

			if (response.contains("refund_status")) {
				transaction.setStatus(resJson.get("refund_status").toString());
			}
		}

		catch (Exception e) {
			logger.error("Exception", e);
		}

		return transaction;
	}
	
	public Transaction toTransactionRefundOld(String response) {

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
