package com.pay10.bob;

import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.pay10.commons.exception.SystemException;
import com.pay10.commons.util.Amount;
import com.pay10.commons.util.ConfigurationConstants;
import com.pay10.commons.util.FieldType;
import com.pay10.commons.util.Fields;
import com.pay10.commons.util.PaymentType;
import com.pay10.commons.util.PropertiesManager;
import com.pay10.commons.util.TransactionType;
import com.pay10.pg.core.util.AcquirerTxnAmountProvider;
import com.pay10.pg.core.util.BobUtil;
import com.pay10.pg.core.whitelabel.ReturnUrlCustomizer;

/**
 * @author Rahul
 *
 */
@Service("bobTransactionConverter")
public class TransactionConverter {

	@Autowired
	private AcquirerTxnAmountProvider acquirerTxnAmountProvider;

	@Autowired
	private BobUtil bobUtil;
	
	@Autowired
	private ReturnUrlCustomizer returnUrlCustomizer;

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
	public static final String UDF5 = "udf5";
	public static final String UDF6 = "udf6";
	public static final String UDF7 = "udf7";
	public static final String UDF8 = "udf8";
	public static final String UDF9 = "udf9";
	public static final String UDF10 = "udf10";
	public static final String UDF11 = "udf11";
	public static final String UDF12 = "udf12";
	public static final String UDF13 = "udf13";
	public static final String UDF14 = "udf14";
	public static final String UDF15 = "udf15";

	@SuppressWarnings("incomplete-switch")
	public String perpareRequest(Fields fields, Transaction transaction) throws SystemException {

		String request = null;

		if ((fields.get(FieldType.PAYMENT_TYPE.getName()).equals(PaymentType.NET_BANKING.getCode()))) {

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

		} else {

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
		}
		return request.toString();

	}

	public String saleRequest(Fields fields, Transaction transaction) throws SystemException {
		StringBuilder xml = new StringBuilder();
		String encryptedRequest = null;
		String amount = acquirerTxnAmountProvider.amountProvider(fields);
		String responseUrl = returnUrlCustomizer.customizeReturnUrl(fields, ConfigurationConstants.BOB_RESPONSE_URL.getValue());
		String language = PropertiesManager.propertiesMap.get(Constants.LANGUAGE);
		String key = transaction.getTxnKey();

		
		if (fields.get(FieldType.PAYMENT_TYPE.getName()).equalsIgnoreCase(PaymentType.UPI.getCode())) {
			
			xml.append(REQUEST_OPEN_TAG);
			getElement(CARD, transaction.getCard(), xml);
			getElement(CURRENCYCODE, fields.get(FieldType.CURRENCY_CODE.getName()), xml);
			getElement(TYPE, transaction.getType(), xml);
			getElement(MEMBER, transaction.getMember(), xml);
			getElement(AMT, amount, xml);
			getElement(ACTION, transaction.getAction(), xml);
			getElement(TRACKID, fields.get(FieldType.PG_REF_NUM.getName()), xml);
			getElement(ERRORURL, responseUrl, xml);
			getElement(RESPONSEURL, responseUrl, xml);
			getElement(ID, transaction.getId(), xml);
			getElement(LANGUAGE, language, xml);
			getElement(PASSWORD, transaction.getPassword(), xml);
			getElement(UDF6, "Pay10", xml);
			getElement(UDF7, transaction.getMember(), xml);
			getElement(UDF8, fields.get(FieldType.CUST_EMAIL.getName()), xml);
			getElement(UDF9, fields.get(FieldType.CUST_PHONE.getName()), xml);
			getElement(UDF10, "Gurgaon", xml);
			getElement(UDF11, Amount.toDecimal(fields.get(FieldType.TOTAL_AMOUNT.getName()), fields.get(FieldType.CURRENCY_CODE.getName())), xml);
			getElement(UDF12, fields.get(FieldType.ORDER_ID.getName()), xml);
			getElement(UDF13, "secure.pay10.com", xml);
			getElement(UDF14, transaction.getCardType(), xml);
			getElement(UDF15, transaction.getTransactionType(), xml);
			xml.append(REQUEST_CLOSE_TAG);
		}
		else {
			xml.append(REQUEST_OPEN_TAG);
			getElement(CARD, transaction.getCard(), xml);
			getElement(CVV2, transaction.getCvv(), xml);
			getElement(CURRENCYCODE, fields.get(FieldType.CURRENCY_CODE.getName()), xml);
			getElement(EXPYEAR, transaction.getExpYear(), xml);
			getElement(EXPMONTH, transaction.getExpMonth(), xml);
			getElement(TYPE, transaction.getType(), xml);
			getElement(MEMBER, transaction.getMember(), xml);
			getElement(AMT, amount, xml);
			getElement(ACTION, transaction.getAction(), xml);
			getElement(TRACKID, fields.get(FieldType.PG_REF_NUM.getName()), xml);
			getElement(ERRORURL, responseUrl, xml);
			getElement(RESPONSEURL, responseUrl, xml);
			getElement(ID, transaction.getId(), xml);
			getElement(LANGUAGE, language, xml);
			getElement(PASSWORD, transaction.getPassword(), xml);
			getElement(UDF12, transaction.getPaymentType(), xml);
			getElement(UDF13, transaction.getCardIssuerCountry(), xml);
			getElement(UDF14, transaction.getCardType(), xml);
			getElement(UDF15, transaction.getTransactionType(), xml);
			xml.append(REQUEST_CLOSE_TAG);
		}
		

		logRequest(xml.toString(), fields);

		try {
			encryptedRequest = bobUtil.encryptText(key, xml.toString());
		} catch (Exception exception) {
			logger.error("Exception", exception);
		}
		return encryptedRequest;
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

		StringBuilder xml = new StringBuilder();
		String udf5 = PropertiesManager.propertiesMap.get(Constants.UDF5_VALUE);
		// String amount = fields.get(FieldType.AMOUNT.getName());
		String amount = acquirerTxnAmountProvider.amountProvider(fields);
		String currencyCode = fields.get(FieldType.CURRENCY_CODE.getName());
		// amount = Amount.toDecimal(amount, currencyCode);

		xml.append(REQUEST_OPEN_TAG);
		getElement(CURRENCY, amount, xml);
		getElement(TRANSID, fields.get(FieldType.ORIG_TXN_ID.getName()), xml);
		getElement(AMT, amount, xml);
		getElement(ACTION, transaction.getAction(), xml);
		getElement(TRACKID, fields.get(FieldType.PG_REF_NUM.getName()), xml);
		getElement(UDF5, udf5, xml);
		getElement(CURRENCYCODE, fields.get(FieldType.CURRENCY_CODE.getName()), xml);
		getElement(ID, fields.get(FieldType.MERCHANT_ID.getName()), xml);
		getElement(PASSWORD, fields.get(FieldType.PASSWORD.getName()), xml);
		getElement(UDF11, fields.get(FieldType.REFUND_FLAG.getName()), xml);
		xml.append(REQUEST_CLOSE_TAG);
		return xml.toString();
	}

	public String statusEnquiryRequest(Fields fields, Transaction transaction) {

		StringBuilder xml = new StringBuilder();
		String language = PropertiesManager.propertiesMap.get(Constants.LANGUAGE);
		String udf5 = PropertiesManager.propertiesMap.get(Constants.UDF5_VALUE);
		String amount = fields.get(FieldType.TOTAL_AMOUNT.getName());
		String currencyCode = fields.get(FieldType.CURRENCY_CODE.getName());
		amount = Amount.toDecimal(amount, currencyCode);

		xml.append(REQUEST_OPEN_TAG);
		getElement(CURRENCYCODE, fields.get(FieldType.CURRENCY_CODE.getName()), xml);
		getElement(TYPE, transaction.getType(), xml);
		getElement(LANGUAGE, language, xml);
		getElement(ID, transaction.getId(), xml);
		getElement(TRANSID, fields.get(FieldType.ORIG_TXN_ID.getName()), xml);
		getElement(ACTION, transaction.getAction(), xml);
		getElement(TRACKID, fields.get(FieldType.PG_REF_NUM.getName()), xml);
		getElement(AMT, amount, xml);
		getElement(PASSWORD, transaction.getPassword(), xml);
		getElement(UDF5, udf5, xml);
		xml.append(REQUEST_CLOSE_TAG);
		return xml.toString();

	}

	public Transaction toTransaction(String xml) {

		Transaction transaction = new Transaction();
		String result = getTextBetweenTags(xml, RESULT_OPEN_TAG, RESULT_CLOSE_TAG);
		transaction.setResult(result);
		transaction.setError_code_tag(getTextBetweenTags(xml, ERROR_CODE_OPEN_TAG, ERROR_CODE_CLOSE_TAG));
		transaction.setError_service_tag(getTextBetweenTags(xml, ERROR_SERVICE_OPEN_TAG, ERROR_SERVICE_CLOSE_TAG));
		transaction.setError_text(getTextBetweenTags(xml, ERROR_TEXT_OPEN_TAG, ERROR_TEXT_CLOSE_TAG));
		transaction.setPaymentid(getTextBetweenTags(xml, PAYMENT_ID_OPEN_TAG, PAYMENT_ID_CLOSE_TAG));
		transaction.setAuth(getTextBetweenTags(xml, AUTH_OPEN_TAG, AUTH_CLOSE_TAG));
		transaction.setRef(getTextBetweenTags(xml, REF_OPEN_TAG, REF_CLOSE_TAG));
		transaction.setAvr(getTextBetweenTags(xml, AVR_OPEN_TAG, AVR_CLOSE_TAG));
		transaction.setTranId(getTextBetweenTags(xml, TRANID_OPEN_TAG, TRANID_CLOSE_TAG));
		transaction.setAmount(getTextBetweenTags(xml, AMOUNT_OPEN_TAG, AMOUNT_CLOSE_TAG));
		transaction.setPayId(getTextBetweenTags(xml, PAY_ID_OPEN_TAG, PAY_ID_CLOSE_TAG));
		transaction.setTrackId(getTextBetweenTags(xml, TRACKID_OPEN_TAG, TRACKID_CLOSE_TAG));
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
		log("Request message to BOB: TxnType = " + fields.get(FieldType.TXNTYPE.getName()) + " " + "Txn id = "
				+ fields.get(FieldType.TXN_ID.getName()) + " " + "Url= " + requestMessage, fields);
	}

	private void log(String message, Fields fields) {
		
		if (fields.get(FieldType.PAYMENT_TYPE.getName()).equalsIgnoreCase(PaymentType.UPI.getCode())) {
			message = Pattern.compile("(<pan>)([\\s\\S]*?)(</pan>)").matcher(message).replaceAll("$1$3");
			message = Pattern.compile("(<password>)([\\s\\S]*?)(</password>)").matcher(message).replaceAll("$1$3");
			MDC.put(FieldType.INTERNAL_CUSTOM_MDC.getName(), fields.getCustomMDC());
			logger.info(message);
		}
		else {
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

}
