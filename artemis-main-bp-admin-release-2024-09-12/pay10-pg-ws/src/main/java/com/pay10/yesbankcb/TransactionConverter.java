package com.pay10.yesbankcb;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.pay10.commons.exception.ErrorType;
import com.pay10.commons.exception.SystemException;
import com.pay10.commons.util.Currency;
import com.pay10.commons.util.FieldType;
import com.pay10.commons.util.Fields;
import com.pay10.commons.util.PropertiesManager;
import com.pay10.commons.util.TransactionType;
import com.pay10.pg.core.util.AcquirerTxnAmountProvider;
import com.pay10.pg.core.util.HdfcUpiUtil;

/**
 * @author VJ
 *
 */
@Service("yesBankCbTransactionConverter")
public class TransactionConverter {
	private static Logger logger = LoggerFactory.getLogger(TransactionConverter.class.getName());

	@Autowired
	private AcquirerTxnAmountProvider acquirerTxnAmountProvider;
	@Autowired
	@Qualifier("hdfcUpiUtil")
	private HdfcUpiUtil hdfcUpiUtil;

	@SuppressWarnings("incomplete-switch")
	public JSONObject perpareRequest(Fields fields) throws SystemException {

		JSONObject request = null;

		switch (TransactionType.getInstance(fields.get(FieldType.TXNTYPE.getName()))) {
		case REFUND:
			request = payRequest(fields);
			break;
		case SALE:
			request = collectRequest(fields);
			break;
		case ENQUIRY:
			request = statusEnquiryRequest(fields);
			break;
		}
		return request;

	}

	public JSONObject vpaValidatorRequest(Fields fields) throws SystemException {

		StringBuilder request = new StringBuilder();
		request.append(fields.get(FieldType.ADF5.getName()));
		request.append(Constants.PIPE_SEPARATOR);
		request.append(fields.get(FieldType.PG_REF_NUM.getName()));
		request.append(Constants.PIPE_SEPARATOR);
		request.append(fields.get(FieldType.PAYER_ADDRESS.getName()));
		request.append(Constants.PIPE_SEPARATOR);
		request.append(Constants.REQ_TYPE_VALUE);
		request.append(Constants.PIPE_SEPARATOR);
		request.append(Constants.APP_VALUE);
		request.append(Constants.PIPE_SEPARATOR);
		request.append(Constants.GEOCODE_VALUE);
		request.append(Constants.PIPE_SEPARATOR);
		request.append(Constants.LOCATION_VALUE);
		request.append(Constants.PIPE_SEPARATOR);
		request.append(Constants.IP_VALUE);
		request.append(Constants.PIPE_SEPARATOR);
		request.append(Constants.TYPE_VALUE);
		request.append(Constants.PIPE_SEPARATOR);
		request.append(Constants.CAPABILITY_VALUE);
		request.append(Constants.PIPE_SEPARATOR);
		request.append(Constants.OS_VALUE);
		request.append(Constants.PIPE_SEPARATOR);
		request.append(Constants.DEVICE_ID_VALUE);
		request.append(Constants.PIPE_SEPARATOR);
		request.append(Constants.SIM_ID);
		request.append(Constants.PIPE_SEPARATOR);
		request.append(Constants.SYSTEM_UNIQUE_ID);
		request.append(Constants.PIPE_SEPARATOR);
		request.append(Constants.BLUETOOTH_MAC);
		request.append(Constants.PIPE_SEPARATOR);
		request.append(Constants.WIFI_MAC);
		request.append(Constants.PIPE_SEPARATOR);
		request.append("");
		request.append(Constants.PIPE_SEPARATOR);
		request.append("");
		request.append(Constants.PIPE_SEPARATOR);
		request.append("");
		request.append(Constants.PIPE_SEPARATOR);
		request.append("");
		request.append(Constants.PIPE_SEPARATOR);
		request.append("");
		request.append(Constants.PIPE_SEPARATOR);
		request.append("");
		request.append(Constants.PIPE_SEPARATOR);
		request.append("");
		request.append(Constants.PIPE_SEPARATOR);
		request.append("");
		request.append(Constants.PIPE_SEPARATOR);
		request.append(Constants.LAST_VALUE);
		request.append(Constants.PIPE_SEPARATOR);
		request.append(Constants.LAST_VALUE);

		logger.info("yes bank UPI VPA VALIDATION request = " + fields.get(FieldType.TXNTYPE.getName()) + " " + "Txn id"
				+ fields.get(FieldType.TXN_ID.getName()) + " " + request.toString());

		String encryptedString = encrypt(request.toString(), fields);

		JSONObject json = new JSONObject();
		json.put(Constants.REQUEST_MESSAGE, encryptedString);
		json.put(Constants.PG_MERCHANT_ID, fields.get(FieldType.ADF5.getName()));

		fields.put(FieldType.UDF3.getName(), fields.get(FieldType.PAYER_ADDRESS.getName()));
		String payerName = fields.get(FieldType.PAYER_NAME.getName());
		fields.put(FieldType.UDF4.getName(), payerName);
		return json;

	}

	public JSONObject logIntentRequest(Fields fields) throws SystemException {

		String merchantId = fields.get(FieldType.ADF5.getName());
		String amount = acquirerTxnAmountProvider.amountProvider(fields);
		StringBuilder request = new StringBuilder();
		request.append(merchantId);
		request.append(Constants.PIPE_SEPARATOR);
		request.append(fields.get(FieldType.PG_REF_NUM.getName()));
		request.append(Constants.PIPE_SEPARATOR);
		request.append("");
		request.append(Constants.PIPE_SEPARATOR);
		request.append(amount);
		request.append(Constants.PIPE_SEPARATOR);
		request.append("");
		request.append(Constants.PIPE_SEPARATOR);
		request.append("");
		request.append(Constants.PIPE_SEPARATOR);
		request.append("");
		request.append(Constants.PIPE_SEPARATOR);
		request.append("");
		request.append(Constants.PIPE_SEPARATOR);
		request.append("");
		request.append(Constants.PIPE_SEPARATOR);
		request.append("");
		request.append(Constants.PIPE_SEPARATOR);
		request.append("");
		request.append(Constants.PIPE_SEPARATOR);
		request.append("");
		request.append(Constants.PIPE_SEPARATOR);
		request.append("");
		request.append(Constants.PIPE_SEPARATOR);
		request.append("");
		request.append(Constants.PIPE_SEPARATOR);
		request.append(Constants.LAST_VALUE);
		request.append(Constants.PIPE_SEPARATOR);
		request.append(Constants.LAST_VALUE);
		logger.info("logIntentRequest:: yes bank. txnType={}, Txn id={}, request={}",
				fields.get(FieldType.TXNTYPE.getName()), fields.get(FieldType.TXN_ID.getName()), request.toString());
		String encryptedString = encrypt(request.toString(), fields);

		JSONObject json = new JSONObject();
		json.put(Constants.REQUEST_MESSAGE, encryptedString);
		json.put(Constants.PG_MERCHANT_ID, merchantId);
		return json;
	}

	public JSONObject collectRequest(Fields fields) throws SystemException {

		String payerAddress = fields.get(FieldType.PAYER_ADDRESS.getName());
		String amount = acquirerTxnAmountProvider.amountProvider(fields);
		String exp_value = PropertiesManager.propertiesMap.get(Constants.EXPIRY_VALUE);
		String exp_type = PropertiesManager.propertiesMap.get(Constants.EXPIRY_TYPE);
		StringBuilder request = new StringBuilder();
		request.append(fields.get(FieldType.ADF5.getName()));
		request.append(Constants.PIPE_SEPARATOR);
		request.append(fields.get(FieldType.PG_REF_NUM.getName()));
		request.append(Constants.PIPE_SEPARATOR);
		request.append(payerAddress);
		request.append(Constants.PIPE_SEPARATOR);
		request.append(amount);
		request.append(Constants.PIPE_SEPARATOR);
		request.append(Constants.PRODUCT_DESC);
		request.append(Constants.PIPE_SEPARATOR);
		request.append(exp_type);
		request.append(Constants.PIPE_SEPARATOR);
		request.append(exp_value);
		request.append(Constants.PIPE_SEPARATOR);
		request.append(fields.get(FieldType.ADF2.getName()));
		request.append(Constants.PIPE_SEPARATOR);
		request.append("");
		request.append(Constants.PIPE_SEPARATOR);
		request.append("");
		request.append(Constants.PIPE_SEPARATOR);
		request.append("");
		request.append(Constants.PIPE_SEPARATOR);
		request.append("");
		request.append(Constants.PIPE_SEPARATOR);
		request.append("");
		request.append(Constants.PIPE_SEPARATOR);
		request.append("");
		request.append(Constants.PIPE_SEPARATOR);
		request.append("");
		request.append(Constants.PIPE_SEPARATOR);
		request.append("");
		request.append(Constants.PIPE_SEPARATOR);
		request.append("");
		request.append(Constants.PIPE_SEPARATOR);
		request.append("");
		request.append(Constants.PIPE_SEPARATOR);
		request.append("");
		request.append(Constants.PIPE_SEPARATOR);
		request.append("");
		request.append(Constants.PIPE_SEPARATOR);
		request.append("");
		request.append(Constants.PIPE_SEPARATOR);
		request.append("");
		request.append(Constants.PIPE_SEPARATOR);
		request.append("");
		request.append(Constants.PIPE_SEPARATOR);
		request.append("");
		request.append(Constants.PIPE_SEPARATOR);
		request.append("");
		request.append(Constants.PIPE_SEPARATOR);
		request.append("");
		request.append(Constants.PIPE_SEPARATOR);
		request.append("");
		request.append(Constants.PIPE_SEPARATOR);
		request.append(Constants.LAST_VALUE);
		request.append(Constants.PIPE_SEPARATOR);
		request.append(Constants.LAST_VALUE);
		logger.info("yes bank UPI collect request =  " + fields.get(FieldType.TXNTYPE.getName()) + " " + "Txn id"
				+ fields.get(FieldType.TXN_ID.getName()) + " " + request.toString());
		String encryptedString = encrypt(request.toString(), fields);

		JSONObject json = new JSONObject();
		json.put(Constants.REQUEST_MESSAGE, encryptedString);
		json.put(Constants.PG_MERCHANT_ID, fields.get(FieldType.ADF5.getName()));

		fields.put(FieldType.UDF3.getName(), payerAddress);
		fields.put(FieldType.CARD_MASK.getName(), fields.get(FieldType.PAYER_ADDRESS.getName())); // show in txn reports
		String payerName = fields.get(FieldType.PAYER_NAME.getName());
		fields.put(FieldType.UDF4.getName(), payerName);
		return json;

	}

	public JSONObject payRequest(Fields fields) throws SystemException {

		String refundedAmount = acquirerTxnAmountProvider.amountProvider(fields);

		String currency = Currency.getAlphabaticCode(fields.get(FieldType.CURRENCY_CODE.getName()));
		String merchantId = fields.get(FieldType.ADF5.getName());

		StringBuilder request = new StringBuilder();
		request.append(merchantId);
		request.append(Constants.PIPE_SEPARATOR);
		request.append(fields.get(FieldType.PG_REF_NUM.getName()));
		request.append(Constants.PIPE_SEPARATOR);
		request.append(fields.get(FieldType.ORIG_TXN_ID.getName()));
		request.append(Constants.PIPE_SEPARATOR);
		request.append(fields.get(FieldType.ACQ_ID.getName()));
		request.append(Constants.PIPE_SEPARATOR);
		request.append(fields.get(FieldType.RRN.getName()));
		request.append(Constants.PIPE_SEPARATOR);
		request.append("Refund for");
		request.append(Constants.PIPE_SEPARATOR);
		request.append(refundedAmount);
		request.append(Constants.PIPE_SEPARATOR);
		request.append(currency);
		request.append(Constants.PIPE_SEPARATOR);
		request.append(Constants.PAYMENT_TYPE);
		request.append(Constants.PIPE_SEPARATOR);
		request.append(Constants.YES_BANK_UPI_TXN_TYPE);
		request.append(Constants.PIPE_SEPARATOR);
		request.append("");
		request.append(Constants.PIPE_SEPARATOR);
		request.append("");
		request.append(Constants.PIPE_SEPARATOR);
		request.append("");
		request.append(Constants.PIPE_SEPARATOR);
		request.append("");
		request.append(Constants.PIPE_SEPARATOR);
		request.append("");
		request.append(Constants.PIPE_SEPARATOR);
		request.append("");
		request.append(Constants.PIPE_SEPARATOR);
		request.append("");
		request.append(Constants.PIPE_SEPARATOR);
		request.append("");
		request.append(Constants.PIPE_SEPARATOR);
		request.append(Constants.LAST_VALUE);
		request.append(Constants.PIPE_SEPARATOR);
		request.append(Constants.LAST_VALUE);
		logger.info("yes bank UPI refund request = " + fields.get(FieldType.TXNTYPE.getName()) + " " + "Txn id"
				+ fields.get(FieldType.TXN_ID.getName()) + " " + request.toString());
		String encryptedString = encrypt(request.toString(), fields);

		JSONObject json = new JSONObject();
		json.put(Constants.REQUEST_MESSAGE, encryptedString);
		json.put(Constants.PG_MERCHANT_ID, merchantId);
		return json;
	}

	public JSONObject statusEnquiryRequest(Fields fields) throws SystemException {

		String merchantId = fields.get(FieldType.ADF5.getName());

		StringBuilder request = new StringBuilder();
		request.append(merchantId);
		request.append(Constants.PIPE_SEPARATOR);
		request.append(fields.get(FieldType.PG_REF_NUM.getName()));
		request.append(Constants.PIPE_SEPARATOR);
		request.append("");
		request.append(Constants.PIPE_SEPARATOR);
		request.append("");
		request.append(Constants.PIPE_SEPARATOR);
		request.append("");
		request.append(Constants.PIPE_SEPARATOR);
		request.append("");
		request.append(Constants.PIPE_SEPARATOR);
		request.append("");
		request.append(Constants.PIPE_SEPARATOR);
		request.append("");
		request.append(Constants.PIPE_SEPARATOR);
		request.append("");
		request.append(Constants.PIPE_SEPARATOR);
		request.append("");
		request.append(Constants.PIPE_SEPARATOR);
		request.append("");
		request.append(Constants.PIPE_SEPARATOR);
		request.append("");
		request.append(Constants.PIPE_SEPARATOR);
		request.append("");
		request.append(Constants.PIPE_SEPARATOR);
		request.append(Constants.LAST_VALUE);
		request.append(Constants.PIPE_SEPARATOR);
		request.append(Constants.LAST_VALUE);
		logger.info("yes bank UPI StatusEnquiry request =  " + fields.get(FieldType.TXNTYPE.getName()) + " " + "Txn id"
				+ fields.get(FieldType.TXN_ID.getName()) + " " + request.toString());
		String encryptedString = encrypt(request.toString(), fields);

		JSONObject json = new JSONObject();
		json.put(Constants.REQUEST_MESSAGE, encryptedString);
		json.put(Constants.PG_MERCHANT_ID, merchantId);
		return json;
	}

	public Transaction toTransactionStatusEnquiry(String encryptedResponse, Fields fields) throws SystemException {

		Transaction transaction = new Transaction();
		String decryptedString = decrypt(encryptedResponse, fields);

		String[] value_split = decryptedString.split("\\|");
		logger.info("yes bank UPI StatusEnquiry RESPONSE  " + decryptedString);

		String status = value_split[4];
		String responseCode = value_split[6];
		String responseMsg = value_split[5];
		String dateTime = value_split[3];
		String acqId = value_split[9];
		String customerReference = value_split[11];
		String TranRef = value_split[0];
		String accNo = value_split[12];
		String accType = value_split[23];

		transaction.setResponse(responseCode);
		transaction.setResponseMessage(responseMsg);
		transaction.setStatus(status);
		transaction.setDateTime(dateTime);
		transaction.setRrn(customerReference);
		transaction.setAcq_id(acqId);
		transaction.setTrantRef(TranRef);
		transaction.setAccNo(accNo);
		transaction.setCreditTxn(StringUtils.equalsIgnoreCase(accType, Constants.CREDIT) ? "Y" : "N");

		return transaction;

	}

	public String encrypt(String encryptedRequest, Fields fields) throws SystemException {
		String key = fields.get(FieldType.ADF1.getName());
		if (StringUtils.isBlank(key)) {
			key = PropertiesManager.propertiesMap.get(Constants.YES_BANKCB_UPI_KEY);
		}

		String encryptedString = "";
		try {
			encryptedString = hdfcUpiUtil.encrypt(encryptedRequest.toString(), key);
		} catch (Exception e) {
			logger.error("Exception : " + e.getMessage());
			throw new SystemException(ErrorType.INTERNAL_SYSTEM_ERROR, e,
					"unknown exception in encrypt method for yes upi in TransactionConverter");
		}
		return encryptedString;

	}

	public String toVpaTransaction(String encryptedResponse, Fields fields) throws SystemException {
		String vpaValidationStatus = "";
		String decryptedString = "";
		try {
			if (!encryptedResponse.contains(Constants.YES_RES_CONTAINS_NA)) {
				decryptedString = decrypt(encryptedResponse, fields);
				logger.info(
						"vpa validation API decryptedString YESBANK UPI : " + fields.get(FieldType.TXNTYPE.getName())
								+ " " + "Txn id" + fields.get(FieldType.TXN_ID.getName()) + " " + decryptedString);
			} else {
				decryptedString = encryptedResponse;
				logger.info(
						"vpa validation API decryptedString YESBANK UPI : " + fields.get(FieldType.TXNTYPE.getName())
								+ " " + "Txn id" + fields.get(FieldType.TXN_ID.getName()) + " " + decryptedString);
			}
			// 4758522|work@yesb|PUNEET KAPOOR|VE|Virtual Address already
			// exists|NA|NA|YESB0000423|NA|NA|NA|NA|NA|NA|NA
			String[] value_split = decryptedString.split("\\|");
			vpaValidationStatus = value_split[3];
			if (vpaValidationStatus.equalsIgnoreCase(Constants.VPA_SUCCESSFULLY_STATUS_CODE)) {
				fields.put(FieldType.PAYER_NAME.getName(), value_split[2]);
			}

		} catch (Exception e) {
			logger.error(
					"Exception in toVpaTransaction methord in converter class for yes bank upi : " + e.getMessage());
			throw new SystemException(ErrorType.INTERNAL_SYSTEM_ERROR, e,
					"unknown exception in toVpaTransaction method for yes upi in TransactionConverter");
		}

		return vpaValidationStatus;

	}

	public Transaction toTransaction(String encryptedResponse, Fields fields) throws SystemException {

		Transaction transaction = new Transaction();
		String decryptedString = "";
		try {
			if (!encryptedResponse.contains(Constants.YES_RES_CONTAINS_NA)) {
				decryptedString = decrypt(encryptedResponse, fields);
				logger.info("Collect API decryptedString YESBANK UPI : " + decryptedString);
			} else {
				decryptedString = encryptedResponse;
			}
			logger.info("Collect API decryptedString YESBANK UPI : " + decryptedString);
			String txnType = fields.get(FieldType.TXNTYPE.getName());
			String[] value_split = decryptedString.split("\\|");

			if (txnType.equalsIgnoreCase(TransactionType.SALE.name())) {
				String collectStatus = value_split[3];
				String collectMsg = value_split[4];
				String Acq = value_split[9];
				String RRN = value_split[11];
				transaction.setStatus(collectStatus);
				transaction.setResponseMessage(collectMsg);
				transaction.setAcq_id(Acq);
				transaction.setRrn(RRN);
			} else if (txnType.equalsIgnoreCase(TransactionType.REFUND.name())) {
				String ref_id = value_split[0];
				String refundStatus = value_split[4];
				String refundAcq = value_split[9];
				String refundRRN = value_split[10];
				String refundResponseCode = value_split[6];
				String refundResponseMsg = value_split[5];

				String dateTime = value_split[3];
				String merchantVPA = value_split[8];

				if (StringUtils.isNotBlank(refundStatus)
						&& refundStatus.equalsIgnoreCase(Constants.YES_UPI_REFUND_PENDING)) {
					transaction.setResponse(refundStatus);
				} else {
					transaction.setResponse(refundResponseCode);
				}
				transaction.setReferenceId(ref_id);

				transaction.setAcq_id(refundAcq);
				transaction.setRrn(refundRRN);
				transaction.setStatus(refundStatus);
				transaction.setResponseMessage(refundResponseMsg);
				transaction.setDateTime(dateTime);
				transaction.setMerchantVpa(merchantVPA);

			}
		} catch (Exception e) {
			logger.error("Exception in to transact methord in converter class for yes bank upi : " + e.getMessage());
			throw new SystemException(ErrorType.INTERNAL_SYSTEM_ERROR, e,
					"unknown exception in toTransaction method for yes upi in TransactionConverter");
		}
		logger.info("Collect API sale response after decryption = " + fields.get(FieldType.TXNTYPE.getName()) + " "
				+ "Txn id" + fields.get(FieldType.TXN_ID.getName()) + " " + transaction.getStatus());
		return transaction;

	}

	public Transaction toTransactionRefundFai(String encryptedResponse, Fields fields) throws SystemException {

		Transaction transaction = new Transaction();
		try {
			logger.info("Collect API decryptedString YESBANK UPI if refund response contains pending : "
					+ encryptedResponse);
			String[] value_split = encryptedResponse.split("\\|");
			String refundStatus = value_split[4];
			String refundAcq = value_split[10];
			String refundRRN = value_split[10];
			String refundResponseCode = value_split[6];
			String refundResponseMsg = value_split[5];

			String dateTime = value_split[3];
			String merchantVPA = value_split[8];

			if (StringUtils.isNotBlank(refundStatus)
					&& refundStatus.equalsIgnoreCase(Constants.YES_UPI_REFUND_PENDING)) {
				transaction.setResponse(refundStatus);
			} else {
				transaction.setResponse(refundResponseCode);
			}
			transaction.setAcq_id(refundAcq);
			transaction.setRrn(refundRRN);
			transaction.setStatus(refundStatus);
			transaction.setResponseMessage(refundResponseMsg);
			transaction.setDateTime(dateTime);
			transaction.setMerchantVpa(merchantVPA);

		} catch (Exception e) {
			logger.error("Exception in to transact methord in converter class for yes bank upi : " + e.getMessage());
			throw new SystemException(ErrorType.INTERNAL_SYSTEM_ERROR, e,
					"unknown exception in toTransaction method for yes upi in TransactionConverter");
		}
		logger.info("Collect API refund response after decryption = " + fields.get(FieldType.TXNTYPE.getName()) + " "
				+ "Txn id" + fields.get(FieldType.TXN_ID.getName()) + " " + transaction.getStatus());
		return transaction;

	}

	public Transaction toTransactionFailureRes(String encryptedResponse, Fields fields) throws SystemException {
		Transaction transaction = new Transaction();
		try {
			String[] value_split = encryptedResponse.split("\\|");

			String failureCode = value_split[4];
			String failureMsg = value_split[5];
			String dateTime = value_split[3];
			transaction.setResponse(failureCode);
			transaction.setStatus(failureMsg);
			transaction.setDateTime(dateTime);
		} catch (Exception e) {
			logger.error("Exception in to transact methord in converter class for yes bank upi : " + e.getMessage());
			throw new SystemException(ErrorType.INTERNAL_SYSTEM_ERROR, e,
					"unknown exception in toTransaction method for yes upi in TransactionConverter");
		}
		logger.info("Collect API sale response after decryption = " + fields.get(FieldType.TXNTYPE.getName()) + " "
				+ "Txn id" + fields.get(FieldType.TXN_ID.getName()) + " " + transaction.getStatus());
		return transaction;

	}

	public Transaction toTransactionCollectFailureRes(String encryptedResponse, Fields fields) throws SystemException {
		Transaction transaction = new Transaction();
		try {
			String[] value_split = encryptedResponse.split("\\|");

			String failureRes = value_split[3];
			String failureMsg = value_split[4];

			transaction.setResponse(failureRes);
			transaction.setStatus(failureMsg);

		} catch (Exception e) {
			logger.error("Exception in to transact methord in converter class for yes bank upi : " + e.getMessage());
			throw new SystemException(ErrorType.INTERNAL_SYSTEM_ERROR, e,
					"unknown exception in toTransaction method for yes upi in TransactionConverter");
		}
		logger.info("Collect API sale response after decryption = " + fields.get(FieldType.TXNTYPE.getName()) + " "
				+ "Txn id" + fields.get(FieldType.TXN_ID.getName()) + " " + transaction.getStatus());
		return transaction;

	}

	public String decrypt(String encryptedRequest, Fields fields) throws SystemException {
		String decryptedString = "";
		String key = fields.get(FieldType.ADF1.getName());
		// logger.info("key value for yes bank upi in ADF 1 : " + key);
		if (StringUtils.isBlank(key)) {
			logger.info("Key not found in ADF 1");
			key = PropertiesManager.propertiesMap.get(Constants.YES_BANKCB_UPI_KEY);
		}
		try {
			decryptedString = hdfcUpiUtil.decrypt(encryptedRequest, key);
			logger.info("decryptedString : " + fields.get(FieldType.TXNTYPE.getName()) + " " + "Txn id"
					+ fields.get(FieldType.TXN_ID.getName()) + " " + decryptedString);
		} catch (Exception e) {
			logger.error("Exception : " + e.getMessage());
			throw new SystemException(ErrorType.INTERNAL_SYSTEM_ERROR, e,
					"unknown exception in decrypt method for yes upi in TransactionConverter");
		}
		return decryptedString;

	}

	public Transaction toTransactionLogIntent(String encryptedResponse, Fields fields) throws SystemException {

		Transaction transaction = new Transaction();
		String decryptedString = decrypt(encryptedResponse, fields);

		String[] value_split = decryptedString.split("\\|");
		logger.info("toTransactionLogIntent:: YesBank: pgRefNo={}, decryptedRes={}",
				fields.get(FieldType.PG_REF_NUM.getName()), decryptedString);

		String txnRef = value_split[0];
		String status = value_split[4];//
		String responseMsg = value_split[5];
		String dateTime = value_split[3];
		String txnId = value_split[6];

		transaction.setResponseMessage(responseMsg);
		transaction.setStatus(status);
		transaction.setDateTime(dateTime);
		transaction.setTrantRef(txnRef);
		transaction.setTransactionId(txnId);
		return transaction;
	}

}
