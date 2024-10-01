package com.pay10.sbi;

import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import com.google.gson.JsonObject;
import com.pay10.commons.api.Hasher;
import com.pay10.commons.exception.ErrorType;
import com.pay10.commons.exception.SystemException;
import com.pay10.commons.user.Token;
import com.pay10.commons.util.Currency;
import com.pay10.commons.util.FieldType;
import com.pay10.commons.util.Fields;
import com.pay10.commons.util.PaymentType;
import com.pay10.commons.util.PropertiesManager;
import com.pay10.commons.util.TransactionType;
import com.pay10.pg.core.tokenization.TokenManager;
import com.pay10.pg.core.util.AcquirerTxnAmountProvider;
import com.pay10.pg.core.util.SbiUtil;

@Service("sbiTransactionConverter")
public class TransactionConverter {

	@Autowired
	private AcquirerTxnAmountProvider acquirerTxnAmountProvider;

	@Autowired
	private SbiUtil sbiUtil;

	@Autowired
	private TokenManager tokenManager;

	private static final Logger logger = LoggerFactory.getLogger(TransactionConverter.class.getName());

	public static final String REQUEST_OPEN_TAG = "<request>";
	public static final String REQUEST_CLOSE_TAG = "</request>";
	public static final String RESULT_OPEN_TAG = "<result>";
	public static final String RESULT_CLOSE_TAG = "</result>";
	public static final String ERROR_TEXT_OPEN_TAG = "<error_text>";
	public static final String ERROR_TEXT_CLOSE_TAG = "</error_text>";
	public static final String PAYMENT_ID_OPEN_TAG = "<paymentid>";
	public static final String PAYMENT_ID_CLOSE_TAG = "</paymentid>";
	public static final String AUTH_OPEN_TAG = "<auth>";
	public static final String AUTH_CLOSE_TAG = "</auth>";
	public static final String REF_OPEN_TAG = "<ref>";
	public static final String REF_CLOSE_TAG = "</ref>";
	public static final String AVR_OPEN_TAG = "<avr>";
	public static final String AVR_CLOSE_TAG = "</avr>";
	public static final String TRANID_OPEN_TAG = "<tranid>";
	public static final String TRANID_CLOSE_TAG = "</tranid>";
	public static final String ERROR_CODE_OPEN_TAG = "<error_code_tag>";
	public static final String ERROR_CODE_CLOSE_TAG = "</error_code_tag>";
	public static final String ERROR_SERVICE_OPEN_TAG = "<error_service_tag>";
	public static final String ERROR_SERVICE_CLOSE_TAG = "</error_service_tag>";
	public static final String AMOUNT_OPEN_TAG = "<amt>";
	public static final String AMOUNT_CLOSE_TAG = "</amt>";
	public static final String TRACKID_OPEN_TAG = "<trackid>";
	public static final String TRACKID_CLOSE_TAG = "</trackid>";
	public static final String PAY_ID_OPEN_TAG = "<payid>";
	public static final String PAY_ID_CLOSE_TAG = "</payid>";
	public static final String AUTH_RESC_OPEN_TAG = "<authrescode>";
	public static final String AUTH_RESC_CLOSE_TAG = "</authrescode>";

	public static final String REQUEST = "request";
	public static final String ID = "id";
	public static final String PASSWORD = "password";
	public static final String ACTION = "action";
	public static final String AMT = "amt";
	public static final String CURRENCYCODE = "currencycode";
	public static final String TRACKID = "trackId";
	public static final String CARD = "card";
	public static final String EXPMONTH = "expmonth";
	public static final String EXPYEAR = "expyear";
	public static final String CVV2 = "cvv2";
	public static final String MEMBER = "member";
	public static final String TYPE = "type";
	public static final String ERRORURL = "errorURL";
	public static final String RESPONSEURL = "responseURL";
	public static final String LANGUAGE = "langid";
	public static final String TRANSID = "transid";
	public static final String CURRENCY = "currency";
	public static final String UDF1 = "udf1";
	public static final String UDF2 = "udf2";
	public static final String UDF3 = "udf3";
	public static final String UDF4 = "udf4";
	public static final String UDF5 = "udf5";

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
			request = StringUtils.isBlank(fields.get(FieldType.TOKEN_ID.getName())) ? saleRequest(fields, transaction)
					: expressRequest(fields, transaction);
			break;
		case CAPTURE:
			break;
		case STATUS:
			// request = statusEnquiryRequest(fields, transaction);
			break;
		}
		return request.toString();

	}

	/*
	 * public String saleRequest(Fields fields, Transaction transaction) throws
	 * SystemException { String amount =
	 * acquirerTxnAmountProvider.amountProvider(fields); String returnUrl =
	 * PropertiesManager.propertiesMap.get("SbiresponseURL"); StringBuilder req =
	 * new StringBuilder();
	 * 
	 * // New Request as per the below Format
	 * 
	 * // Amount=1.00|Ref_no=3913511009155223|MERCHANT NAME=1|PAY // ID=1|checkSum=
	 * 42b0688e4b008ab5eb3c76f7d9462bbd1b33492b36afe7815dfe239717e14815
	 * 
	 * req.append(Constants.Amount); req.append("="); req.append(amount);
	 * req.append("|"); req.append(Constants.Ref_no); req.append("=");
	 * req.append(fields.get(FieldType.PG_REF_NUM.getName())); req.append("|");
	 * req.append(Constants.MERCHANT_NAME); req.append("="); req.append("1");
	 * req.append("|"); req.append(Constants.PAY_ID); req.append("=");
	 * req.append("1"); String checksum = Hasher.getHash(req.toString());
	 * req.append("|"); req.append(Constants.CHECKSUM); req.append("=");
	 * req.append(checksum.toLowerCase());
	 * 
	 * // Old Request
	 * 
	 * req.append(Constants.PG_REF_NUM); req.append("=");
	 * req.append(fields.get(FieldType.PG_REF_NUM.getName())); req.append("|");
	 * req.append(Constants.AMOUNT); req.append("="); req.append(amount);
	 * req.append("|"); req.append(Constants.RETURN_URL); req.append("=");
	 * req.append(returnUrl); req.append("|"); req.append(Constants.CANCEL_URL);
	 * req.append("="); req.append(returnUrl); String checksum =
	 * Hasher.getHash(req.toString()); req.append("|");
	 * req.append(Constants.CHECKSUM); req.append("=");
	 * req.append(checksum.toLowerCase());
	 * 
	 * logger.info("Plain Text Request to SBI " +
	 * fields.get(FieldType.PG_REF_NUM.getName()) + ":" + req.toString()); String
	 * encryptedRequest = sbiUtil.encrypt(req.toString());
	 * logger.info("Encrypted Request to SBI " +
	 * fields.get(FieldType.PG_REF_NUM.getName()) + ":" + encryptedRequest); return
	 * encryptedRequest; }
	 */

	public String saleRequest(Fields fields, Transaction transaction) throws SystemException {
		String amount = acquirerTxnAmountProvider.amountProvider(fields);
		String returnUrl = PropertiesManager.propertiesMap.get("SbiresponseURL");

		// code added by sonu
		if (fields.get(FieldType.PAYMENT_TYPE.getName()).equalsIgnoreCase(PaymentType.CREDIT_CARD.getCode())
				|| fields.get(FieldType.PAYMENT_TYPE.getName()).equalsIgnoreCase(PaymentType.DEBIT_CARD.getCode())) {

			JsonObject jsonRequest = prepareTxnDetails(fields, amount, returnUrl);

			logger.info("SBI Request before adding payment parameters  incase of cards " + jsonRequest.toString());

			String expDate = fields.get(FieldType.CARD_EXP_DT.getName());

			jsonRequest.addProperty(Constants.paymentOption, Constants.paymentOptionCard);
			jsonRequest.addProperty(Constants.card_number, fields.get(FieldType.CARD_NUMBER.getName()));
			jsonRequest.addProperty(Constants.card_holder, fields.get(FieldType.CARD_HOLDER_NAME.getName()));
			jsonRequest.addProperty(Constants.card_expiryMonth, expDate.substring(0, 2));
			jsonRequest.addProperty(Constants.card_expiryYear, expDate.substring(2, 6));
			jsonRequest.addProperty(Constants.card_cvv, fields.get(FieldType.CVV.getName()));

			// logger.info("Request Prepared For SBI Cards "+jsonRequest.toString());
			return jsonRequest.toString();

		} else {

			// New Request as per the below Format

			// Amount=1.00|Ref_no=3913511009155223|MERCHANT NAME=1|PAY
			// ID=1|checkSum=42b0688e4b008ab5eb3c76f7d9462bbd1b33492b36afe7815dfe239717e14815

			StringBuilder req = new StringBuilder();
			req.append(Constants.Amount);
			req.append("=");
			req.append(amount);
			req.append("|");
			req.append(Constants.Ref_no);
			req.append("=");
			req.append(fields.get(FieldType.PG_REF_NUM.getName()));
			req.append("|");
			req.append(Constants.MERCHANT_NAME);
			req.append("=");
			req.append("1");
			req.append("|");
			req.append(Constants.PAY_ID);
			req.append("=");
			req.append("1");
			String checksum = Hasher.getHash(req.toString());
			req.append("|");
			req.append(Constants.CHECKSUM);
			req.append("=");
			req.append(checksum.toLowerCase());

			// Old Request
			/*
			 * req.append(Constants.PG_REF_NUM); req.append("=");
			 * req.append(fields.get(FieldType.PG_REF_NUM.getName())); req.append("|");
			 * req.append(Constants.AMOUNT); req.append("="); req.append(amount);
			 * req.append("|"); req.append(Constants.RETURN_URL); req.append("=");
			 * req.append(returnUrl); req.append("|"); req.append(Constants.CANCEL_URL);
			 * req.append("="); req.append(returnUrl); String checksum =
			 * Hasher.getHash(req.toString()); req.append("|");
			 * req.append(Constants.CHECKSUM); req.append("=");
			 * req.append(checksum.toLowerCase());
			 */
			logger.info(
					"Plain Text Request to SBI " + fields.get(FieldType.PG_REF_NUM.getName()) + ":" + req.toString());
			String encryptedRequest = sbiUtil.encrypt(req.toString());
			logger.info(
					"Encrypted Request to SBI " + fields.get(FieldType.PG_REF_NUM.getName()) + ":" + encryptedRequest);
			return encryptedRequest;
		}
	}

	private JsonObject prepareTxnDetails(Fields fields, String amount, String returnUrl) {
		JsonObject jsonRequest = new JsonObject();

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
			jsonRequest.addProperty(Constants.customerPhone, "9999999999");
		}

		jsonRequest.addProperty(Constants.returnUrl, returnUrl);

		jsonRequest.addProperty(Constants.appId, fields.get(FieldType.MERCHANT_ID.getName()));
		jsonRequest.addProperty(Constants.orderId, fields.get(FieldType.PG_REF_NUM.getName()));
		jsonRequest.addProperty(Constants.orderAmount, amount);
		jsonRequest.addProperty(Constants.orderCurrency,
				Currency.getAlphabaticCode(fields.get(FieldType.CURRENCY_CODE.getName())));
		return jsonRequest;
	}

	private String expressRequest(Fields fields, Transaction transaction) throws SystemException {
		String amount = acquirerTxnAmountProvider.amountProvider(fields);
		String returnUrl = PropertiesManager.propertiesMap.get("SbiresponseURL");
		JsonObject jsonRequest = prepareTxnDetails(fields, amount, returnUrl);
		jsonRequest.addProperty(FieldType.UDF6.getName(), calculateUdf6(fields));
		return jsonRequest.toString();
	}

	private String calculateUdf6(Fields fields) throws SystemException {
		Token token = getTokenDetails(fields);
		String cardNo = StringUtils.substring(token.getCardMask(), token.getCardMask().length() - 4,
				token.getCardMask().length());
		char mopType = token.getMopType().charAt(0);
		char paymentType = token.getPaymentType().charAt(0);
		String separator = "|";
		String udf6 = StringUtils.join(Constants.TOKEN_FLAG, separator, token.getNetworkToken(), separator, cardNo,
				separator, token.getPayId(), separator, mopType, separator, paymentType);
		return udf6;
	}

	private Token getTokenDetails(Fields fields) throws SystemException {
		try {
			Token token = tokenManager.getToken(fields);
			if (ObjectUtils.isEmpty(token)) {
				throw new SystemException(ErrorType.INVALID_TOKEN, ErrorType.INVALID_TOKEN.getInternalMessage());
			}
			return token;

		} catch (Exception e) {
			throw new SystemException(ErrorType.INVALID_TOKEN, e, ErrorType.INVALID_TOKEN.getInternalMessage());
		}
	}

	public void getElement(String name, String value, StringBuilder xml) {
		if (null == value) {
			return;
		}

		xml.append("<");
		xml.append(name);
		xml.append(">");
		xml.append(value);
		xml.append("</");
		xml.append(name);
		xml.append(">");
	}

	public String refundRequest(Fields fields, Transaction transaction) throws SystemException {

		return null;
	}

	public Transaction toTransaction(String xml) {

		Transaction transaction = new Transaction();

		return transaction;
	}// toTransaction()

	public TransactionConverter() {
	}

	public String getTextBetweenTags(String text, String tag1, String tag2) {

		int leftIndex = text.indexOf(tag1);
		if (leftIndex == -1) {
			return null;
		}

		int rightIndex = text.indexOf(tag2);
		if (rightIndex != -1) {
			leftIndex = leftIndex + tag1.length();
			return text.substring(leftIndex, rightIndex);
		}

		return null;
	}// getTextBetweenTags()

	public void logRequest(String requestMessage, Fields fields) {
		log("Request message to SBI: TxnType = " + fields.get(FieldType.TXNTYPE.getName()) + " " + "Txn id = "
				+ fields.get(FieldType.TXN_ID.getName()) + " " + "Url= " + requestMessage, fields);
	}

	private void log(String message, Fields fields) {
		message = Pattern.compile("(<card>)([\\s\\S]*?)(</card>)").matcher(message).replaceAll("$1$3");
		message = Pattern.compile("(<pan>)([\\s\\S]*?)(</pan>)").matcher(message).replaceAll("$1$3");
		message = Pattern.compile("(<expmonth>)([\\s\\S]*?)(</expmonth>)").matcher(message).replaceAll("$1$3");
		message = Pattern.compile("(<expyear>)([\\s\\S]*?)(</expyear>)").matcher(message).replaceAll("$1$3");
		message = Pattern.compile("(<cvv2>)([\\s\\S]*?)(</cvv2>)").matcher(message).replaceAll("$1$3");
		message = Pattern.compile("(<password>)([\\s\\S]*?)(</password>)").matcher(message).replaceAll("$1$3");
		MDC.put(FieldType.INTERNAL_CUSTOM_MDC.getName(), fields.getCustomMDC());
		logger.info(message);
	}

}
