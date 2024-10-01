package com.pay10.federalNB;

import java.security.MessageDigest;
import java.util.regex.Matcher;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
import com.pay10.pg.core.util.FederalBankNBEncDecService;
import com.pay10.pg.core.util.FederalNBChecksumUtil;

@Service("federalBankNBTransactionConverter")
public class TransactionConverter {

	@Autowired
	private AcquirerTxnAmountProvider acquirerTxnAmountProvider;

	@Autowired
	private FederalBankNBEncDecService federalBankNBEncDecService;

	@Autowired
	private FederalNBChecksumUtil federalNBChecksumUtil;

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
		case CAPTURE:
			break;

		case SETTLE:
			break;
		case STATUS:
			request = statusEnquiryRequest(fields);
			break;
		}
		return request.toString();

	}

	public String saleRequest(Fields fields, Transaction transaction) throws SystemException {

		JsonObject jsonRequest = new JsonObject();
		String encryptReq = prepareSaleRequest(fields);
		jsonRequest.addProperty(Constants.API_KEY, fields.get(FieldType.TXN_KEY.getName()));
		jsonRequest.addProperty(Constants.ENC_DATA, encryptReq);
		logger.info("Federal Bank Net Banking Final Request >> " + jsonRequest.toString());
		return jsonRequest.toString();

	}

	public String prepareSaleRequest(Fields fields) throws SystemException {

		String encryptedString = null;
		try {
			String amount = acquirerTxnAmountProvider.amountProvider(fields);
			JsonObject jsonRequest = new JsonObject();
			jsonRequest.addProperty(Constants.API_KEY, fields.get(FieldType.TXN_KEY.getName()));
			jsonRequest.addProperty(Constants.AMT, amount);
			if (StringUtils.isNotBlank(fields.get(FieldType.CUST_CITY.getName()))) {
				jsonRequest.addProperty(Constants.CITY, fields.get(FieldType.CUST_CITY.getName()));
			} else {
				jsonRequest.addProperty(Constants.CITY, "Jaipur");
			}
			if (StringUtils.isNotBlank(fields.get(FieldType.CUST_COUNTRY.getName()))) {
				jsonRequest.addProperty(Constants.COUNTRY, fields.get(FieldType.CUST_COUNTRY.getName()));
			} else {
				jsonRequest.addProperty(Constants.COUNTRY, "IND");
			}
			jsonRequest.addProperty(Constants.CURRENCY,
					Currency.getAlphabaticCode(fields.get(FieldType.CURRENCY_CODE.getName())));
			if (StringUtils.isNotBlank(fields.get(FieldType.PRODUCT_DESC.getName()))) {
				jsonRequest.addProperty(Constants.DESC, fields.get(FieldType.PRODUCT_DESC.getName()));
			} else {
				jsonRequest.addProperty(Constants.DESC, "pay10");
			}

			if (StringUtils.isNotBlank(fields.get(FieldType.CUST_EMAIL.getName()))) {
				jsonRequest.addProperty(Constants.EMAIL, fields.get(FieldType.CUST_EMAIL.getName()));
			} else {
				jsonRequest.addProperty(Constants.EMAIL, "support.txn@pay10.com");
			}
			jsonRequest.addProperty(Constants.MODE, "LIVE");
			if (StringUtils.isNotBlank(fields.get(FieldType.CUST_NAME.getName()))) {
				jsonRequest.addProperty(Constants.NAME, fields.get(FieldType.CUST_NAME.getName()));
			} else {
				jsonRequest.addProperty(Constants.NAME, "NA");
			}
			jsonRequest.addProperty(Constants.OID, fields.get(FieldType.PG_REF_NUM.getName()));
			if (StringUtils.isNotBlank(fields.get(FieldType.CUST_PHONE.getName()))) {
				jsonRequest.addProperty(Constants.PHONE, fields.get(FieldType.CUST_PHONE.getName()));
			} else {
				jsonRequest.addProperty(Constants.PHONE, "999999999");
			}
			jsonRequest.addProperty(Constants.RURL,
					PropertiesManager.propertiesMap.get(Constants.FEDERALBANK_NB_RESPONSE_URL));
			if (StringUtils.isNotBlank(fields.get(FieldType.CUST_ZIP.getName()))) {
				jsonRequest.addProperty(Constants.ZIP, fields.get(FieldType.CUST_ZIP.getName()));
			} else {
				jsonRequest.addProperty(Constants.ZIP, "302001");
			}
			if (fields.get(FieldType.PAYMENT_TYPE.getName()).equalsIgnoreCase(PaymentType.NET_BANKING.getCode())) {
				// FEDN
				jsonRequest.addProperty(Constants.BANK_CODE,
						FederalBankNBMopType.getBankCode(fields.get(FieldType.MOP_TYPE.getName())));
			}

			String signature = federalNBChecksumUtil.getHash(jsonRequest, fields.get(FieldType.ADF1.getName()));
			jsonRequest.addProperty(Constants.HASH, signature);

			logger.info(fields.get(FieldType.ADF2.getName()) + "----Federal Bank Net Banking Request String >> "
					+ jsonRequest.toString());

			String enc = jsonRequest.toString().replaceAll("/", Matcher.quoteReplacement("\\/"));
			encryptedString = federalBankNBEncDecService.encrypt(enc, fields.get(FieldType.ADF2.getName()));

			logger.info("Federal Bank Net Banking Encrypted Request String >> " + encryptedString.toString());
			return encryptedString;

		}

		catch (Exception e) {
			logger.error("Exception in generating Federal Bank NB Sale Request ", e);
			return null;
		}

	}

	public static String encryptData(final String base) {
		try {
			final MessageDigest digest = MessageDigest.getInstance("SHA-256");
			final byte[] hash = digest.digest(base.getBytes("UTF-8"));
			final StringBuilder hexString = new StringBuilder();
			for (int i = 0; i < hash.length; i++) {
				final String hex = Integer.toHexString(0xff & hash[i]);
				if (hex.length() == 1)
					hexString.append('0');
				hexString.append(hex);
			}
			return hexString.toString();
		} catch (Exception ex) {
			throw new RuntimeException(ex);
		}
	}

	public String refundRequest(Fields fields, Transaction transaction) throws SystemException {

		try {
			StringBuilder hash_data = new StringBuilder();
			String amount = acquirerTxnAmountProvider.amountProvider(fields);
			StringBuilder sb = new StringBuilder();
			sb.append(Constants.AMT + "=" + amount);
			hash_data.append(amount);
			sb.append("&");
			sb.append(Constants.API_KEY + "=" + fields.get(FieldType.TXN_KEY.getName()));
			hash_data.append("|" + fields.get(FieldType.TXN_KEY.getName()));
			sb.append("&");
			sb.append(Constants.DESC + "=" + "Refund");
			hash_data.append("|Refund");
			sb.append("&");
			sb.append(Constants.M_REFUND_ID + "=" + fields.get(FieldType.PG_REF_NUM.getName())); // refund reference
																									// number
			hash_data.append("|" + fields.get(FieldType.PG_REF_NUM.getName()));
			sb.append("&");
			sb.append(Constants.TXN + "=" + fields.get(FieldType.ACQ_ID.getName()));
			hash_data.append("|" + fields.get(FieldType.ACQ_ID.getName()));
			// getting hashing...
			logger.info("getting hash for Federal bank NB refund request" + hash_data);
			String signature = federalNBChecksumUtil.getHashRefund(hash_data.toString(),
					fields.get(FieldType.ADF1.getName()));
			sb.append("&");
			sb.append(Constants.HASH + "=" + signature);
			logger.info("final federal bank NB refund request" + sb.toString());
			return sb.toString();

		}

		catch (Exception e) {
			logger.error("Exception in generating federal bank NB refund request", e);
			return null;
		}

	}

	public String statusEnquiryRequest(Fields fields) {

		Transaction transaction = new Transaction();

		try {
			StringBuilder hash_data = new StringBuilder();
			StringBuilder sb = new StringBuilder();
			sb.append(Constants.API_KEY + "=" + fields.get(FieldType.TXN_KEY.getName()));
			hash_data.append(fields.get(FieldType.TXN_KEY.getName()));
			sb.append("&");
			sb.append(Constants.BANK_CODE + "="
					+ FederalBankNBMopType.getBankCode(fields.get(FieldType.MOP_TYPE.getName())));
			hash_data.append("|" + FederalBankNBMopType.getBankCode(fields.get(FieldType.MOP_TYPE.getName())));
			sb.append("&");
			sb.append(Constants.OID + "=" + fields.get(FieldType.PG_REF_NUM.getName())); // refund reference
																							// number
			hash_data.append("|" + fields.get(FieldType.PG_REF_NUM.getName()));
//		sb.append("&");
//		sb.append(Constants.TXN + "=" + fields.get(FieldType.ACQ_ID.getName()));
//		hash_data.append("|" + fields.get(FieldType.ACQ_ID.getName()));
			// getting hashing...
			logger.info("request for dualverfication Federal pgrefno ={}",fields.get(FieldType.PG_REF_NUM.getName()));

			logger.info("dualverfication hash for Federal bank NB status enquiry request" + hash_data);
			String signature = federalNBChecksumUtil.getHashRefund(hash_data.toString(),
					fields.get(FieldType.ADF1.getName()));
			sb.append("&");
			sb.append(Constants.HASH + "=" + signature);
			logger.info("final federal bank NB dual verfication  request" + sb.toString());
			return sb.toString();

		}

		catch (Exception e) {
			logger.error("Exception in generating federal bank NB status enquiry request", e);
			return null;
		}

	}

	public Transaction toTransaction(String response) {

		Transaction transaction = new Transaction();
		try {

			JSONObject resJson = new JSONObject(response);
			if (response.contains(Constants.OID)) {
				transaction.setOrder_id(resJson.get(Constants.OID).toString());
			}

			if (response.contains(Constants.AMT)) {
				transaction.setAmount(resJson.get(Constants.AMT).toString());
			}

			if (response.contains(Constants.TXN)) {
				transaction.setTransaction_id(resJson.get(Constants.TXN).toString());
			}

			if (response.contains(Constants.RESPONSE_CODE)) {
				transaction.setResponse_code(resJson.get(Constants.RESPONSE_CODE).toString());
			}

			if (response.contains(Constants.PAY_CHANNEL)) {
				transaction.setPayment_channel(resJson.get(Constants.PAY_CHANNEL).toString());
			}

			if (response.contains(Constants.RESPONSE_MSG)) {
				transaction.setResponse_message(resJson.get(Constants.RESPONSE_MSG).toString());
			}
			if (response.contains(Constants.ERROR_DESC)) {
				transaction.setError_desc(resJson.get(Constants.ERROR_DESC).toString());
			}
			if (response.contains(Constants.ERROR_DESC)) {
				transaction.setError_desc(resJson.get(Constants.ERROR_DESC).toString());
			}
			if (response.contains(Constants.PAY_DT)) {
				transaction.setError_desc(resJson.get(Constants.PAY_DT).toString());
			}
		}

		catch (Exception e) {
			logger.error("Exception", e);
		}

		return transaction;
	}

	public Transaction toTransactionStatus(String response) {

		Transaction transaction = new Transaction();
		try {

			JSONObject res = new JSONObject(response);
			if (response.contains("data")) {

				JSONArray resArr = res.getJSONArray("data");
				JSONObject resJson = resArr.getJSONObject(0);
				if (response.contains(Constants.OID)) {
					transaction.setOrder_id(resJson.get(Constants.OID).toString());
				}

				if (response.contains(Constants.AMT)) {
					transaction.setAmount(resJson.get(Constants.AMT).toString());
				}

				if (response.contains(Constants.TXN)) {
					transaction.setTransaction_id(resJson.get(Constants.TXN).toString());
				}

				if (response.contains(Constants.RESPONSE_CODE)) {
					transaction.setResponse_code(resJson.get(Constants.RESPONSE_CODE).toString());
				}

				if (response.contains(Constants.BANK_TXN_ID)) {
					transaction.setBank_transaction_id(resJson.get(Constants.BANK_TXN_ID).toString());
				}
				if (response.contains(Constants.RESPONSE_MSG)) {
					transaction.setResponse_message(resJson.get(Constants.RESPONSE_MSG).toString());
				}
				if (response.contains(Constants.ERROR_DESC)) {
					transaction.setError_desc(resJson.get(Constants.ERROR_DESC).toString());
				}

				if (response.contains(Constants.PAY_DT)) {
					transaction.setError_desc(resJson.get(Constants.PAY_DT).toString());
				}
			} else if (response.contains("error")) {
				JSONObject resJson = res.getJSONObject("error");
				if (response.contains(Constants.MSG)) {
					transaction.setMessage(resJson.get(Constants.MSG).toString());
				}
				if (response.contains(Constants.CODE)) {
					transaction.setCode(resJson.get(Constants.CODE).toString());
				}
logger.info("response for federal nd dual verification pgref={},amount={}",transaction.getOrder_id(),transaction.getAmount());
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
				JSONArray res = resJson.getJSONArray("data");
				resJson = res.getJSONObject(0);
				if (response.contains(Constants.REFUND_ID)) {
					transaction.setRefund_id(resJson.get(Constants.REFUND_ID).toString());
				}
				if (response.contains(Constants.R_REF_NO)) {
					transaction.setRefund_reference_no(resJson.get(Constants.R_REF_NO).toString());
				}
				if (response.contains(Constants.M_REFUND_ID)) {
					transaction.setMerchant_refund_id(resJson.get(Constants.M_REFUND_ID).toString());
				}
				if (response.contains(Constants.M_ORDER_ID)) {
					transaction.setMerchant_order_id(resJson.get(Constants.M_ORDER_ID).toString());
				}
				transaction.setCode("0");

			} else if (response.contains("error")) {
				resJson = resJson.getJSONObject("error");
				if (response.contains(Constants.MSG)) {
					transaction.setMessage(resJson.get(Constants.MSG).toString());
				}
				if (response.contains(Constants.CODE)) {
					transaction.setCode(resJson.get(Constants.CODE).toString());
				}

			}

		}

		catch (Exception e) {
			logger.error("Exception", e);
		}

		return transaction;
	}

	public TransactionConverter() {
	}

	public static String getTextBetweenTags(String text, String tag1, String tag2) {

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

}
