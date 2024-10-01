package com.pay10.icici.upi;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.pay10.commons.exception.ErrorType;
import com.pay10.commons.exception.SystemException;
import com.pay10.commons.util.Amount;
import com.pay10.commons.util.Currency;
import com.pay10.commons.util.FieldType;
import com.pay10.commons.util.Fields;
import com.pay10.commons.util.PropertiesManager;
import com.pay10.commons.util.StatusType;
import com.pay10.commons.util.TransactionType;
import com.pay10.pg.core.util.AcquirerTxnAmountProvider;
import com.pay10.pg.core.util.ICICIUpiUtil;

/**
 * @author Rahul
 *
 */
@Service("iciciUpiTransactionConverter")
public class TransactionConverter {

	@Autowired
	private AcquirerTxnAmountProvider acquirerTxnAmountProvider;

	@Autowired
	@Qualifier("iciciUpiUtil")
	private ICICIUpiUtil iciciUpiUtil;

	private String decryptedString;

	private static Logger logger = LoggerFactory.getLogger(TransactionConverter.class.getName());

	@SuppressWarnings("incomplete-switch")
	public String perpareRequest(Fields fields) throws SystemException {

		String request = null;

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

	public String vpaValidatorRequest(Fields fields) throws SystemException {

		String status = PropertiesManager.propertiesMap.get("ICICIUpiStatusValue");
		String lastValue = PropertiesManager.propertiesMap.get("iciciUpiLastValue");
		String key = PropertiesManager.propertiesMap.get("ICICIUPI_PUBLIC_KEY_PATH");
		// String payerAddress =
		// PropertiesManager.propertiesMap.get("iciciUpiMerchantVPA");
		String amount = acquirerTxnAmountProvider.amountProvider(fields);
		String payerAddress = fields.get(FieldType.PAYER_ADDRESS.getName());
		String merchantId = fields.get(FieldType.MERCHANT_ID.getName());
		JSONObject json = new JSONObject();
		json.put(Constants.MERCHANT_ID, merchantId);
		json.put(Constants.MERCHANT_NAME, "Bhartipay");
		json.put(Constants.MERCHANT_SUB_ID, merchantId);
		json.put(Constants.MERCHANT_SUB_NAME, merchantId);
		json.put(Constants.TERMINAL_ID, "5411");
		json.put(Constants.MERCHANT_TRAN_ID, fields.get(FieldType.PG_REF_NUM.getName()));
		json.put(Constants.BILL_NUMBER, fields.get(FieldType.ORDER_ID.getName()));
		json.put(Constants.PAYERVA, payerAddress);
		json.put(Constants.AMT, amount);
		json.put(Constants.NOTE, fields.get(FieldType.PRODUCT_DESC.getName()));
		json.put(Constants.COLLECTBYDATE, merchantId);

		
		String encryptedString = null;
		try {
			encryptedString = iciciUpiUtil.getEncrypted(json.toString(), key);
		} catch (Exception e) {
			logger.error("Exception : " + e.getMessage());
		}

		String payerVpa = fields.get(FieldType.PAYER_ADDRESS.getName());
		String payerName = fields.get(FieldType.PAYER_NAME.getName());
		logger.info(payerVpa + "----payerVpa--payerName----" + payerName);
		fields.put(FieldType.UDF3.getName(), payerVpa);
		fields.put(FieldType.UDF4.getName(), payerName);
		return encryptedString;

	}

	public String collectRequestStatus(Fields fields,JSONObject jsonToke) throws SystemException {

		String amount = acquirerTxnAmountProvider.amountProvider(fields);
		String expiryTime = PropertiesManager.propertiesMap.get("ICICIUpiExpiryTime");
		String mccCode = fields.get(FieldType.ADF4.getName());
		String lastValue = PropertiesManager.propertiesMap.get("iciciUpiLastValue");
		String key = PropertiesManager.propertiesMap.get("ICICIUPI_PUBLIC_KEY_PATH");
		// String payerAddress =
		// PropertiesManager.propertiesMap.get("iciciUpiMerchantVPA");
		String payerAddress = fields.get(FieldType.PAYER_ADDRESS.getName());
		//{"response":"92","merchantId":"408095","subMerchantId":"408095","terminalId":"5411","success":"true","message":"Transaction Initiated","merchantTranId":"1010620222112850","BankRRN":"205300776844"}
		// String payerAddress =
		// PropertiesManager.propertiesMap.get("iciciUpiMerchantVPA");
		JSONObject json = new JSONObject();
		String merchantId = fields.get(FieldType.MERCHANT_ID.getName());
		json.put(Constants.MERCHANT_ID, merchantId);
		json.put(Constants.MERCHANT_SUB_ID, fields.get(FieldType.ADF2.getName()));
		json.put(Constants.TERMINAL_ID, mccCode);
		json.put(Constants.MERCHANT_TRAN_ID, fields.get(FieldType.PG_REF_NUM.getName()));
		json.put(Constants.BANKRRN, jsonToke.get(Constants.BANKRRN));
		json.put(Constants.TRANSACTION_TYPE, "C"); // 'C' For check collect pay 
		
		logger.info("final icici upi request for check collect pay status---" + json.toString());
		String encryptedString = null;
		try {
			encryptedString = iciciUpiUtil.getEncrypted(json.toString(), key);
		} catch (Exception e) {
			logger.error("Exception : " + e.getMessage());
		}
		logger.info("final icici upi request for check collect pay status encryptedString---" + encryptedString);
		return encryptedString;
	}

	public String collectRequest(Fields fields) throws SystemException {

		String amount = acquirerTxnAmountProvider.amountProvider(fields);
		int expiryTime = Integer.valueOf(PropertiesManager.propertiesMap.get("ICICIUpiExpiryTime"));
		String mccCode = fields.get(FieldType.ADF4.getName());
		String lastValue = PropertiesManager.propertiesMap.get("iciciUpiLastValue");
		String key = PropertiesManager.propertiesMap.get("ICICIUPI_PUBLIC_KEY_PATH");
		// String payerAddress =
		// PropertiesManager.propertiesMap.get("iciciUpiMerchantVPA");
		String payerAddress = fields.get(FieldType.PAYER_ADDRESS.getName());

		// String payerAddress =
		// PropertiesManager.propertiesMap.get("iciciUpiMerchantVPA");
		String merchantId = fields.get(FieldType.MERCHANT_ID.getName());
		JSONObject json = new JSONObject();
		json.put(Constants.MERCHANT_ID, merchantId);
		json.put(Constants.MERCHANT_NAME, fields.get(FieldType.ADF1.getName()));
		json.put(Constants.MERCHANT_SUB_ID, fields.get(FieldType.ADF2.getName()));
		json.put(Constants.MERCHANT_SUB_NAME, fields.get(FieldType.ADF3.getName()));
		json.put(Constants.TERMINAL_ID, mccCode);
		json.put(Constants.MERCHANT_TRAN_ID, fields.get(FieldType.PG_REF_NUM.getName()));
		json.put(Constants.BILL_NUMBER, fields.get(FieldType.ORDER_ID.getName()));
		json.put(Constants.PAYERVA, payerAddress);
		json.put(Constants.AMT, amount);
		if (StringUtils.isNotBlank(fields.get(FieldType.PRODUCT_DESC.getName()))) {
			json.put(Constants.NOTE, fields.get(FieldType.PRODUCT_DESC.getName()));
		} else {
			json.put(Constants.NOTE, "Pay10 Pay");
		}
		json.put(Constants.COLLECTBYDATE, currentTimeStamp(expiryTime));

		logger.info("final icici upi request---" + json.toString());
		String encryptedString = null;
		try {
			encryptedString = iciciUpiUtil.getEncrypted(json.toString(), key);
		} catch (Exception e) {
			logger.error("Exception : " + e.getMessage());
		}
		logger.info("final icici upi request encryptedString---" + encryptedString);
		return encryptedString;
	}

	
	public  String currentTimeStamp(int minute) {
		String capturedDate = null;
		SimpleDateFormat outputDateFormat = new SimpleDateFormat("dd/MM/yyyy hh:mm a");
		try {
			Calendar cal = Calendar.getInstance();
			cal.add(Calendar.MINUTE,minute);
			capturedDate = outputDateFormat.format(cal.getTime());
		} catch (Exception e) {
			logger.error("Exception", e);
		}
		System.out.println("date------>"+capturedDate);
		return capturedDate;
	}

	public String payRequest(Fields fields) {

		String key = PropertiesManager.propertiesMap.get("ICICIUPI_PUBLIC_KEY_PATH");
		String mccCode = fields.get(FieldType.ADF4.getName());
		String refundedAmount = Amount.toDecimal(fields.get(FieldType.AMOUNT.getName()),
				fields.get(FieldType.CURRENCY_CODE.getName()));
		//String currency = Currency.getAlphabaticCode(fields.get(FieldType.CURRENCY_CODE.getName()));
		String merchantId = fields.get(FieldType.MERCHANT_ID.getName());
		String payerAddress = fields.get(FieldType.PAYER_ADDRESS.getName());
		JSONObject json = new JSONObject();
		json.put(Constants.MERCHANT_ID, merchantId);
		json.put(Constants.MERCHANT_SUB_ID, fields.get(FieldType.ADF2.getName()));
		json.put(Constants.TERMINAL_ID, mccCode);
		json.put(Constants.ORIG_BANKRRN, fields.get(FieldType.RRN.getName()));
		json.put(Constants.MERCHANT_TRAN_ID, fields.get(FieldType.PG_REF_NUM.getName()));
		json.put(Constants.ORIG_MERCHANT_TRAN_ID, fields.get(FieldType.ORIG_TXN_ID.getName()));
		json.put(Constants.PAYEEVA, payerAddress);
		json.put(Constants.REFUNDAMT, refundedAmount);
		if (StringUtils.isNotBlank(fields.get(FieldType.PRODUCT_DESC.getName()))) {
			json.put(Constants.NOTE, fields.get(FieldType.PRODUCT_DESC.getName()));
		} else {
			json.put(Constants.NOTE, "Pay10 Refund");
		}
		json.put(Constants.ONLINEREFUND, "Y"); //Y for online refund and N for Offline refund

		String encryptedString = null;
		try {
			encryptedString = iciciUpiUtil.getEncrypted(json.toString(), key);
		} catch (Exception e) {
			logger.error("Exception : " + e.getMessage());
		}

		return encryptedString;
	}

	public String statusEnquiryRequest(Fields fields) {

		String key = PropertiesManager.propertiesMap.get("ICICIUPI_PUBLIC_KEY_PATH");
		String merchantId = fields.get(FieldType.MERCHANT_ID.getName());
		String mccCode = fields.get(FieldType.ADF4.getName());
		String payerAddress = fields.get(FieldType.PAYER_ADDRESS.getName());
		JSONObject json = new JSONObject();
		json.put(Constants.MERCHANT_ID, merchantId);
		json.put(Constants.MERCHANT_SUB_ID, fields.get(FieldType.ADF2.getName()));
		json.put(Constants.TERMINAL_ID, mccCode);
		json.put(Constants.MERCHANT_TRAN_ID, fields.get(FieldType.PG_REF_NUM.getName()));
		logger.info("icici upi Final Status check request----"+json.toString());
		String encryptedString = null;
		try {
			encryptedString = iciciUpiUtil.getEncrypted(json.toString(), key);
		} catch (Exception e) {
			logger.error("Exception : " + e.getMessage());
		}
		logger.info("icici upi Final Status check request- encryptedString---"+encryptedString);
		return encryptedString;
	}

	
	public JSONObject toTokenTransaction(String encryptedResponse) {
		String key = PropertiesManager.propertiesMap.get("ICICIUPI_PRIVATE_KEY_PATH");
		JSONObject resJson = null;
		try {
			logger.info("Icici Upi Request initaite response encryptedResponse : " + encryptedResponse);
			decryptedString = iciciUpiUtil.getMessageDiscryption(encryptedResponse, key);
			resJson = new JSONObject(decryptedString);
			logger.info("Icici Upi Request initaite response decryptedString : " + decryptedString);
		} catch (Exception e) {
			logger.error("Exception : While reading initail trx response " + e.getMessage());
		}
		
		
		return resJson;

	}
	
	public Transaction toTransaction(String encryptedResponse, Fields fields) {

		Transaction transaction = new Transaction();
		String key = PropertiesManager.propertiesMap.get("ICICIUPI_PRIVATE_KEY_PATH");
		try {
			logger.info("encryptedResponse : " + encryptedResponse);
			decryptedString = iciciUpiUtil.getMessageDiscryption(encryptedResponse, key);
			logger.info("decryptedString : " + decryptedString);
		} catch (Exception e) {
			logger.error("Exception : " + e.getMessage());
		}
		JSONObject resJson = new JSONObject(decryptedString);
		if (decryptedString.contains(Constants.MERCHANT_ID)) {
			transaction.setMerchantId(resJson.get(Constants.MERCHANT_ID).toString());
		}
		if (decryptedString.contains(Constants.SUCCESS_REPONSE_CODE)) {
			transaction.setResponseCode(resJson.get(Constants.SUCCESS_REPONSE_CODE).toString());
		}
		if (decryptedString.contains(Constants.SUCCESS_REPONSE)) {
			transaction.setResponseSuccess(resJson.get(Constants.SUCCESS_REPONSE).toString());
		}
		if (decryptedString.contains(Constants.REQUEST_MESSAGE)) {
			transaction.setResponseMessage(resJson.get(Constants.REQUEST_MESSAGE).toString());
		}
		if (decryptedString.contains(Constants.REPONSE_STATUS)) {
			transaction.setResponseStatus(resJson.get(Constants.REPONSE_STATUS).toString());
		}
		if (decryptedString.contains(Constants.ORIG_BANKRRN)) {
			transaction.setRrn(resJson.get(Constants.ORIG_BANKRRN).toString());
		}
		if (decryptedString.contains(Constants.MERCHANT_TRAN_ID)) {
			transaction.setMerchantTranId(resJson.get(Constants.MERCHANT_TRAN_ID).toString());
		}

		return transaction;
	}
	
	
	public Transaction toTransactionRefund(String decryptedResponse, Fields fields) {

		Transaction transaction = new Transaction();
		String key = PropertiesManager.propertiesMap.get("ICICIUPI_PRIVATE_KEY_PATH");
		try {
			logger.info("Icici Upi decrypted refund Response : " + decryptedResponse);
			decryptedString = iciciUpiUtil.getMessageDiscryption(decryptedResponse, key);
			logger.info("Icici Upi after decrypted refund Response : " + decryptedString);
		} catch (Exception e) {
			logger.error("Exception : " + e.getMessage());
		}
		JSONObject resJson = new JSONObject(decryptedString);
		if (decryptedString.contains(Constants.MERCHANT_ID)) {
			transaction.setMerchantId(resJson.get(Constants.MERCHANT_ID).toString());
		}
		if (decryptedString.contains(Constants.MERCHANT_SUB_ID)) {
			transaction.setSubMerchantId(resJson.get(Constants.MERCHANT_SUB_ID).toString());
		}
		if (decryptedString.contains(Constants.TERMINAL_ID)) {
			transaction.setTerminalId(resJson.get(Constants.TERMINAL_ID).toString());
		}
		if (decryptedString.contains(Constants.SUCCESS_REPONSE_CODE)) {
			transaction.setResponseCode(resJson.get(Constants.SUCCESS_REPONSE_CODE).toString());
		}
		if (decryptedString.contains(Constants.SUCCESS_REPONSE)) {
			transaction.setResponseSuccess(resJson.get(Constants.SUCCESS_REPONSE).toString());
		}
		if (decryptedString.contains(Constants.REQUEST_MESSAGE)) {
			transaction.setResponseMessage(resJson.get(Constants.REQUEST_MESSAGE).toString());
		}
		if (decryptedString.contains(Constants.REPONSE_STATUS)) {
			transaction.setResponseStatus(resJson.get(Constants.REPONSE_STATUS).toString());
		}
		if (decryptedString.contains(Constants.ORIG_BANKRRN)) {
			transaction.setRrn(resJson.get(Constants.ORIG_BANKRRN).toString());
		}
		if (decryptedString.contains(Constants.MERCHANT_TRAN_ID)) {
			transaction.setMerchantTranId(resJson.get(Constants.MERCHANT_TRAN_ID).toString());
		}

		return transaction;
	}

	public Transaction toTransactionStatusEnquiry(String encryptedResponse, Fields fields) {

		Transaction transaction = new Transaction();
		String key = PropertiesManager.propertiesMap.get("ICICIUPI_PRIVATE_KEY_PATH");

		try {
			decryptedString = iciciUpiUtil.getMessageDiscryption(encryptedResponse, key);
			logger.info("decryptedString : " + decryptedString);
		} catch (Exception e) {
			logger.error("Exception : " + e.getMessage());
		}

		logger.info("ICICI UPI StatusEnquiry RESPONSE  " + decryptedString);
		
		JSONObject resJson = new JSONObject(decryptedString);
		if (decryptedString.contains(Constants.MERCHANT_ID)) {
			transaction.setMerchantId(resJson.get(Constants.MERCHANT_ID).toString());
		}
		if (decryptedString.contains(Constants.SUCCESS_REPONSE_CODE)) {
			transaction.setResponseCode(resJson.get(Constants.SUCCESS_REPONSE_CODE).toString());
		}
		if (decryptedString.contains(Constants.SUCCESS_REPONSE)) {
			transaction.setResponseSuccess(resJson.get(Constants.SUCCESS_REPONSE).toString());
		}
		if (decryptedString.contains(Constants.REQUEST_MESSAGE)) {
			transaction.setResponseMessage(resJson.get(Constants.REQUEST_MESSAGE).toString());
		}
		if (decryptedString.contains(Constants.BANKRRN)) {
			transaction.setRrn(resJson.get(Constants.BANKRRN).toString());
		}
		if (decryptedString.contains(Constants.MERCHANT_TRAN_ID)) {
			transaction.setMerchantTranId(resJson.get(Constants.MERCHANT_TRAN_ID).toString());
		}
		if (decryptedString.contains(Constants.REPONSE_STATUS)) {
			transaction.setEnquiryStatus(resJson.get(Constants.REPONSE_STATUS).toString()); //PENDING, SUCCESSS, FAILURE
		}
		return transaction;

	}

	public String getStatusType(String receivedResponse) {
		String status = null;
		if (receivedResponse.equals(Constants.SUCCESS_REPONSE_CODE)) {
			status = StatusType.CAPTURED.getName();
		} else if (receivedResponse.equals("U19")) {
			status = StatusType.FAILED.getName();
		} else {
			status = StatusType.REJECTED.getName();
		}

		return status;
	}

	public ErrorType getErrorType(String receivedResponse) {
		ErrorType error = null;

		if (receivedResponse.equals(Constants.SUCCESS_REPONSE_CODE)) {
			error = ErrorType.SUCCESS;
		} else if (receivedResponse.equals("U19")) {
			error = ErrorType.INVALID_REQUEST_FIELD;
		} else if (receivedResponse.equals("002")) {
			error = ErrorType.CANCELLED;
		} else if (receivedResponse.equals("003")) {
			error = ErrorType.INTERNAL_SYSTEM_ERROR;
		} else if (receivedResponse.equals("004")) {
			error = ErrorType.CANCELLED;
		} else {
			error = ErrorType.DECLINED;
		}

		return error;
	}

}