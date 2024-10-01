package com.pay10.ipay;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.pay10.commons.api.Hasher;
import com.pay10.commons.exception.ErrorType;
import com.pay10.commons.exception.SystemException;
import com.pay10.commons.util.Amount;
import com.pay10.commons.util.Currency;
import com.pay10.commons.util.FieldType;
import com.pay10.commons.util.Fields;
import com.pay10.commons.util.PropertiesManager;
import com.pay10.commons.util.StatusType;

@Service("iPayTransactionConverter")
public class TransactionConverter {
	
	private static Logger logger = LoggerFactory.getLogger(TransactionConverter.class.getName());
	private static final String prefix = "IPAY_ACQUIRER_";
	public void createSaleTransaction(Fields fields) throws SystemException {
		
		String acquirer = fields.get(FieldType.ACQUIRER_TYPE.getName());
		
		String key = fields.get(FieldType.TXN_KEY.getName());
		String iv = fields.get(FieldType.PASSWORD.getName());
		
		StringBuilder requestfields = mapSaleFields(fields);
		requestfields = mapChecksum(requestfields);
		logger.info("Sale request before getting encrypted: " + requestfields.toString());
		StringBuilder encryptedString = encryptRequest(requestfields, key, iv);
		String saleUrl = acquirer + Constants.IPAY_REQUEST_URL_APPENDER;
		String finalPaymentString = getFinalRequest(encryptedString, saleUrl);
		logger.info("Sale request sent to bank: " + finalPaymentString);
		fields.put(FieldType.IPAY_FINAL_REQUEST.getName(), finalPaymentString);
		fields.put(FieldType.PG_REF_NUM.getName(), fields.get(FieldType.TXN_ID.getName()));
		fields.put(FieldType.ORIG_TXN_ID.getName(), fields.get(FieldType.TXN_ID.getName()));
		fields.put(FieldType.STATUS.getName(), StatusType.SENT_TO_BANK.getName());
		fields.put(FieldType.RESPONSE_CODE.getName(), ErrorType.SUCCESS.getCode());
		fields.put(FieldType.RESPONSE_MESSAGE.getName(), ErrorType.SUCCESS.getResponseMessage());
	}

	private StringBuilder mapSaleFields(Fields fields) {
		StringBuilder request = new StringBuilder();
		String currencyCode = fields.get(FieldType.CURRENCY_CODE.getName());
		String amount = Amount.toDecimal(fields.get(FieldType.AMOUNT.getName()), currencyCode);
		
		Date date = new Date();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
		String currentDate = sdf.format(date);
		
		String responseUrl = PropertiesManager.propertiesMap.get(prefix+Constants.IPAY_RETURN_URL);
		
		request.append(Constants.MERCHANT_CODE).append(Constants.EQUATOR).append(fields.get(FieldType.MERCHANT_ID.getName()));
		request.append(Constants.IPAY_SEPARATOR);
		request.append(Constants.RESERVATION_ID).append(Constants.EQUATOR).append(fields.get(FieldType.TXN_ID.getName()));
		request.append(Constants.IPAY_SEPARATOR);
		request.append(Constants.TXN_AMOUNT).append(Constants.EQUATOR).append(amount);
		request.append(Constants.IPAY_SEPARATOR);
		request.append(Constants.CURRENCY_TYPE).append(Constants.EQUATOR).append(Currency.getAlphabaticCode(currencyCode));
		request.append(Constants.IPAY_SEPARATOR);
		request.append(Constants.APP_CODE).append(Constants.EQUATOR).append(fields.get(FieldType.APP_CODE.getName()));
		request.append(Constants.IPAY_SEPARATOR);
		request.append(Constants.PAYMENT_MODE).append(Constants.EQUATOR).append(fields.get(FieldType.TXN_SOURCE.getName()));
		request.append(Constants.IPAY_SEPARATOR);
		request.append(Constants.TXN_DATE).append(Constants.EQUATOR).append(currentDate);
		request.append(Constants.IPAY_SEPARATOR);
		request.append(Constants.SECURITY_ID).append(Constants.EQUATOR).append(Constants.IPAY_SECURITY_ID);
		request.append(Constants.IPAY_SEPARATOR);
		request.append(Constants.RESPONSE_URL).append(Constants.EQUATOR).append(responseUrl);
		
		return request;
	}

	private StringBuilder mapChecksum(StringBuilder requestfields) throws SystemException {
		String checksum = Hasher.getHash(requestfields.toString());
		
		requestfields.append(Constants.IPAY_SEPARATOR);
		requestfields.append(Constants.CHECKSUM).append(Constants.EQUATOR).append(checksum);
		
		return requestfields;
	}

	private StringBuilder encryptRequest(StringBuilder checksumString, String key, String iv) {
		logger.info("Encrypting request parameters for IPay Integration");
		//encrypt checksum file using AES algorithm with key and iv 
		StringBuilder encryptedString = new StringBuilder();
		encryptedString.append(IPayUtil.encrypt(checksumString.toString(), key, iv));
		return encryptedString;
	}

	private String getFinalRequest(StringBuilder encryptedString, String urlString) {
		String requestUrl = PropertiesManager.propertiesMap.get(prefix+urlString);
		StringBuilder finalRequest = new StringBuilder();
		finalRequest.append(requestUrl).append(Constants.PARAMETERS).append(Constants.EQUATOR).append(encryptedString);
		
		return finalRequest.toString();
	}
	
	public void createStatusEnquiryTransaction(Fields fields) throws SystemException {
		String acquirer = fields.get(FieldType.ACQUIRER_TYPE.getName());
		
		String key = fields.get(FieldType.TXN_KEY.getName());
		String iv = fields.get(FieldType.PASSWORD.getName());
		
		StringBuilder requestfields = mapStatusFields(fields);
		requestfields = mapChecksum(requestfields);
		logger.info("Status Enquiry request before getting encrypted: " + requestfields.toString());
		StringBuilder encryptedString = encryptRequest(requestfields, key, iv);
		
		String statusUrl = acquirer + Constants.IPAY_ENQUIRY_URL_APPENDER;
		String finalPaymentString = getFinalRequest(encryptedString, statusUrl);
		logger.info("Status enquiry request sent to bank: " + finalPaymentString);
		fields.put(FieldType.IPAY_FINAL_REQUEST.getName(), finalPaymentString);
		fields.put(FieldType.PG_REF_NUM.getName(), fields.get(FieldType.TXN_ID.getName()));
		fields.put(FieldType.ORIG_TXN_ID.getName(), fields.get(FieldType.TXN_ID.getName()));
		fields.put(FieldType.STATUS.getName(), StatusType.CAPTURED.getName());
		fields.put(FieldType.RESPONSE_CODE.getName(), ErrorType.SUCCESS.getCode());
		fields.put(FieldType.RESPONSE_MESSAGE.getName(), ErrorType.SUCCESS.getResponseMessage());
	}

	private StringBuilder mapStatusFields(Fields fields) {
		StringBuilder request = new StringBuilder();
		String currencyCode = fields.get(FieldType.CURRENCY_CODE.getName());
		String amount = Amount.toDecimal(fields.get(FieldType.AMOUNT.getName()), currencyCode);
		
		request.append(Constants.MERCHANT_CODE).append(Constants.EQUATOR).append(fields.get(FieldType.MERCHANT_ID.getName()));
		request.append(Constants.IPAY_SEPARATOR);
		request.append(Constants.RESERVATION_ID).append(Constants.EQUATOR).append(fields.get(FieldType.TXN_ID.getName()));
		request.append(Constants.IPAY_SEPARATOR);
		request.append(Constants.TXN_AMOUNT).append(Constants.EQUATOR).append(amount);
		return request;
	}
	
	public void createRefundTransaction(Fields fields) throws SystemException {
		String acquirer = fields.get(FieldType.ACQUIRER_TYPE.getName());
		
		String key = fields.get(FieldType.TXN_KEY.getName());
		String iv = fields.get(FieldType.PASSWORD.getName());
		
		StringBuilder requestfields = mapRefundFields(fields);
		requestfields = mapChecksum(requestfields);
		logger.info("Refund request before getting encrypted: " + requestfields.toString());
		StringBuilder encryptedString = encryptRequest(requestfields, key, iv);
		
		String refundUrl = acquirer + Constants.IPAY_REFUND_URL_APPENDER;
		String finalPaymentString = getFinalRequest(encryptedString, refundUrl);
		logger.info("Refund request sent to bank: " + finalPaymentString);
		fields.put(FieldType.IPAY_FINAL_REQUEST.getName(), finalPaymentString);
		fields.put(FieldType.PG_REF_NUM.getName(), fields.get(FieldType.TXN_ID.getName()));
		fields.put(FieldType.STATUS.getName(), StatusType.CAPTURED.getName());
		fields.put(FieldType.RESPONSE_CODE.getName(), ErrorType.SUCCESS.getCode());
		fields.put(FieldType.RESPONSE_MESSAGE.getName(), ErrorType.SUCCESS.getResponseMessage());
	}

	private StringBuilder mapRefundFields(Fields fields) {
		StringBuilder request = new StringBuilder();
		String currencyCode = fields.get(FieldType.CURRENCY_CODE.getName());
		String amount = Amount.toDecimal(fields.get(FieldType.AMOUNT.getName()), currencyCode);
		Date date = new Date();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
		String currentDate = sdf.format(date);
		
		Fields previous = fields.getPrevious();
		
		request.append(Constants.MERCHANT_CODE).append(Constants.EQUATOR).append(fields.get(FieldType.MERCHANT_ID.getName()));
		request.append(Constants.IPAY_SEPARATOR);
		request.append(Constants.SALE_RESERVATION_ID).append(Constants.EQUATOR).append(fields.get(FieldType.ORIG_TXN_ID.getName()));
		request.append(Constants.IPAY_SEPARATOR);
		request.append(Constants.REFUND_FLAG).append(Constants.EQUATOR).append(fields.get(FieldType.REFUND_FLAG.getName()));
		request.append(Constants.IPAY_SEPARATOR);
		request.append(Constants.REFUND_AMOUNT).append(Constants.EQUATOR).append(amount);
		request.append(Constants.IPAY_SEPARATOR);
		request.append(Constants.TXN_AMOUNT).append(Constants.EQUATOR).append(previous.get(FieldType.AMOUNT.getName()));
		request.append(Constants.IPAY_SEPARATOR);
		request.append(Constants.SALE_BANK_TXN_ID).append(Constants.EQUATOR).append(previous.get(FieldType.ACQ_ID.getName()));
		request.append(Constants.IPAY_SEPARATOR);
		request.append(Constants.CANCELLATION_DATE).append(Constants.EQUATOR).append(currentDate);
		request.append(Constants.IPAY_SEPARATOR);
		request.append(Constants.REFUND_RESERVATION_ID).append(Constants.EQUATOR).append(fields.get(FieldType.TXN_ID.getName()));
		return request;
	}
}
