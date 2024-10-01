package com.pay10.freecharge;

import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.pay10.commons.exception.SystemException;
import com.pay10.commons.user.UserDao;
import com.pay10.commons.util.FieldType;
import com.pay10.commons.util.Fields;
import com.pay10.commons.util.PropertiesManager;
import com.pay10.commons.util.TransactionType;
import com.pay10.pg.core.util.AcquirerTxnAmountProvider;
import com.pay10.pg.core.util.FreeChargeUtil;

@Service("freeChargeTransactionConverter")
public class TransactionConverter {

	@Autowired
	private AcquirerTxnAmountProvider acquirerTxnAmountProvider;

	@Autowired
	private FreeChargeUtil freeChargeUtil;

	@Autowired
	private UserDao userDao;

	private static final Logger logger = LoggerFactory.getLogger(TransactionConverter.class.getName());

	public static final String REQUEST_OPEN_TAG = "{";
	public static final String REQUEST_CLOSE_TAG = "}";

	// FREECHARGE SALE REQUEST PARAMETERS
	public static final String MERCHANT_ID = "merchantId";
	public static final String MERCHANT_TXN_ID = "merchantTxnId";
	public static final String REFUND_MERCHANT_TXN_ID = "refundMerchantTxnId";
	public static final String AMOUNT = "txnAmount";
	public static final String PAYER_VPA = "payerVpa";
	public static final String REFUNDAMOUNT = "refundAmount";
	public static final String CHANNEL = "channel";
	public static final String FAILUREURL = "furl";
	public static final String SUCCESSURL = "surl";
	public static final String CHECKSUM = "checksum";
	public static final String TXNTYPE = "txnType";
	public static final String PAYMENT_MODE = "paymentMode";

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
		return request.toString();

	}

	public String saleRequest(Fields fields, Transaction transaction) throws SystemException {

		StringBuilder sb = new StringBuilder();
		StringBuilder sbObj = new StringBuilder();
		String amount = acquirerTxnAmountProvider.amountProvider(fields);
		String responseUrl = null;

		responseUrl = PropertiesManager.propertiesMap.get(Constants.RESPONSE_URL);

		String key = fields.get(FieldType.PASSWORD.getName());

		sb.append(REQUEST_OPEN_TAG);
		getElement(MERCHANT_ID, transaction.getMerchantId(), sb);
		getElement(MERCHANT_TXN_ID, fields.get(FieldType.PG_REF_NUM.getName()), sb);
		getElement(PAYER_VPA, fields.get(FieldType.PAYER_ADDRESS.getName()), sb);
		getElement(PAYMENT_MODE, "UPI_COLLECT", sb);
		getElementN(AMOUNT, amount, sb);
		
		sb.deleteCharAt(sb.length() - 1);
		sb.append(REQUEST_CLOSE_TAG);

		String checksum = freeChargeUtil.generateChecksum(sb.toString(), key);
		
		sbObj.append(REQUEST_OPEN_TAG);
		getElement(MERCHANT_ID, transaction.getMerchantId(), sbObj);
		getElement(MERCHANT_TXN_ID, fields.get(FieldType.PG_REF_NUM.getName()), sbObj);
		getElement(PAYER_VPA, fields.get(FieldType.PAYER_ADDRESS.getName()), sbObj);
		getElement(PAYMENT_MODE, "UPI_COLLECT", sbObj);
		getElementN(AMOUNT, amount, sbObj);
		getElement(CHECKSUM, checksum, sbObj);
		sbObj.deleteCharAt(sbObj.length() - 1);
		sbObj.append(REQUEST_CLOSE_TAG);
		return sbObj.toString();
	}

	public void getElement(String name, String value, StringBuilder xml) {
		if (null == value) {
			return;
		}

		xml.append("\"");
		xml.append(name);
		xml.append("\"");
		xml.append(":");
		xml.append("\"");
		xml.append(value);
		xml.append("\"");
		xml.append(",");

	}
	
	
	public void getElementN(String name, String value, StringBuilder xml) {
		if (null == value) {
			return;
		}

		xml.append("\"");
		xml.append(name);
		xml.append("\"");
		xml.append(":");
		xml.append("");
		xml.append(value);
		xml.append("");
		xml.append(",");

	}

	public void getElementRequest(String name, String value, StringBuilder xml) {
		if (null == value) {
			return;
		}

		xml.append(name);
		xml.append("=");
		xml.append(value);
		xml.append("~");

	}

	public String refundRequest(Fields fields, Transaction transaction) throws SystemException {

		StringBuilder sb = new StringBuilder();
		JSONObject jsonObj = new JSONObject();
		String amount = acquirerTxnAmountProvider.amountProvider(fields);

		String key = transaction.getTxnKey();

		sb.append(REQUEST_OPEN_TAG);
		getElement(MERCHANT_ID, transaction.getMerchantId(), sb);
		getElement(MERCHANT_TXN_ID, fields.get(FieldType.ORDER_ID.getName()), sb);
		getElement(REFUNDAMOUNT, amount, sb);
		getElement(REFUND_MERCHANT_TXN_ID, fields.get(FieldType.REFUND_ORDER_ID.getName()), sb);
		sb.deleteCharAt(sb.length() - 1);
		sb.append(REQUEST_CLOSE_TAG);

		String checksum = freeChargeUtil.generateChecksum(sb.toString(), key);

		jsonObj.put(CHECKSUM, checksum);
		jsonObj.put(MERCHANT_ID, transaction.getMerchantId());
		jsonObj.put(MERCHANT_TXN_ID, fields.get(FieldType.ORDER_ID.getName()));
		jsonObj.put(REFUNDAMOUNT, amount);
		jsonObj.put(REFUND_MERCHANT_TXN_ID, fields.get(FieldType.REFUND_ORDER_ID.getName()));

		return jsonObj.toString();
	}

	public String statusEnquiryRequest(Fields fields, Transaction transaction) throws SystemException {
		StringBuilder sb = new StringBuilder();
		JSONObject jsonObj = new JSONObject();

		String key = transaction.getTxnKey();

		sb.append(REQUEST_OPEN_TAG);
		getElement(MERCHANT_ID, transaction.getMerchantId(), sb);
		getElement(MERCHANT_TXN_ID, fields.get(FieldType.ORDER_ID.getName()), sb);
		getElement(TXNTYPE, transaction.getTxnType(), sb);
		sb.deleteCharAt(sb.length() - 1);
		sb.append(REQUEST_CLOSE_TAG);

		String checksum = freeChargeUtil.generateChecksum(sb.toString(), key);

		jsonObj.put(CHECKSUM, checksum);
		jsonObj.put(MERCHANT_ID, transaction.getMerchantId());
		jsonObj.put(MERCHANT_TXN_ID, fields.get(FieldType.ORDER_ID.getName()));
		jsonObj.put(TXNTYPE, transaction.getTxnType());

		return jsonObj.toString();

	}

	public Transaction toTransaction(String response, String txnType) {
		Transaction transaction = new Transaction();

		try {
			logger.info("API Response for Freecharge == " + response);

			if (txnType.equalsIgnoreCase(TransactionType.REFUND.getName())) {

				JSONObject resJson = new JSONObject(response);

				if (response.contains(Constants.ERROR_CODE)) {
					transaction.setErrorCode(resJson.get(Constants.ERROR_CODE).toString());
				}
				if (response.contains(Constants.ERROR_MSG)) {
					transaction.setErrorMessage(resJson.get(Constants.ERROR_MSG).toString());
				}
				if (response.contains(Constants.MERCHANT_TXN_ID)) {
					transaction.setMerchantTxnId(resJson.get(Constants.MERCHANT_TXN_ID).toString());
				}
				if (response.contains(Constants.REFUND_MERCHANT_TXN_ID)) {
					transaction.setRefundMerchantTxnId(resJson.get(Constants.REFUND_MERCHANT_TXN_ID).toString());
				}
				if (response.contains(Constants.REFUNDTXNID)) {
					transaction.setRefundTxnId(resJson.get(Constants.REFUNDTXNID).toString());
				}
				if (response.contains(Constants.REFUNDEDAMOUNT)) {
					transaction.setRefundAmount(resJson.get(Constants.REFUNDEDAMOUNT).toString());
				}
				if (response.contains(Constants.STATUS)) {
					transaction.setStatus(resJson.get(Constants.STATUS).toString());
				}
				if (response.contains(Constants.CHECKSUM)) {
					transaction.setChecksum(resJson.get(Constants.CHECKSUM).toString());
				}
			}

			else if (txnType.equalsIgnoreCase(TransactionType.SALE.getName())) {

				JSONObject resJson = new JSONObject(response);

				if (response.contains(Constants.ERROR_CODE) && resJson.get(Constants.ERROR_CODE).toString() != null) {
					transaction.setErrorCode(resJson.get(Constants.ERROR_CODE).toString());
				}
				if (response.contains(Constants.ERROR_MSG) && resJson.get(Constants.ERROR_MSG).toString() != null) {
					transaction.setErrorMessage(resJson.get(Constants.ERROR_MSG).toString());
				}
				if (response.contains(Constants.MERCHANT_TXN_ID)) {
					transaction.setMerchantTxnId(resJson.get(Constants.MERCHANT_TXN_ID).toString());
				}
				if (response.contains(Constants.AMOUNT)) {
					transaction.setAmount(resJson.get(Constants.AMOUNT).toString());
				}
				if (response.contains(Constants.TXNID)) {
					transaction.setTxnId(resJson.get(Constants.TXNID).toString());
				}
				if (response.contains(Constants.STATUS)) {
					transaction.setStatus(resJson.get(Constants.STATUS).toString());
				}
				if (response.contains(Constants.CHECKSUM)) {
					transaction.setChecksum(resJson.get(Constants.CHECKSUM).toString());
				}
			}
		} catch (Exception e) {
			logger.error("Exception in parsing response for FreeCharge", e);
		}
		return transaction;
	}// toTransaction()

	public Transaction statusToTransaction(String response) {
		Transaction transaction = new Transaction();

		JSONObject resJson = new JSONObject(response);

		if (response.contains(Constants.ERROR_CODE) && resJson.get(Constants.ERROR_CODE).toString() != null) {
			transaction.setErrorCode(resJson.get(Constants.ERROR_CODE).toString());
		}
		if (response.contains(Constants.ERROR_MSG) && resJson.get(Constants.ERROR_MSG).toString() != null) {
			transaction.setErrorMessage(resJson.get(Constants.ERROR_MSG).toString());
		}
		if (response.contains(Constants.MERCHANT_TXN_ID)) {
			transaction.setMerchantTxnId(resJson.get(Constants.MERCHANT_TXN_ID).toString());
		}
		if (response.contains(Constants.AMOUNT)) {
			transaction.setAmount(resJson.get(Constants.AMOUNT).toString());
		}
		if (response.contains(Constants.TXNID)) {
			transaction.setTxnId(resJson.get(Constants.TXNID).toString());
		}
		if (response.contains(Constants.STATUS)) {
			transaction.setStatus(resJson.get(Constants.STATUS).toString());
		}
		if (response.contains(Constants.CHECKSUM)) {
			transaction.setChecksum(resJson.get(Constants.CHECKSUM).toString());
		}
		return transaction;

	}

	public TransactionConverter() {
	}

}
