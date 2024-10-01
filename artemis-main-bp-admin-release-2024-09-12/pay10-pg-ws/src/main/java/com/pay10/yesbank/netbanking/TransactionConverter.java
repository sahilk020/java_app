package com.pay10.yesbank.netbanking;

import java.security.MessageDigest;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.pay10.commons.exception.SystemException;
import com.pay10.commons.util.Currency;
import com.pay10.commons.util.FieldType;
import com.pay10.commons.util.Fields;
import com.pay10.commons.util.PropertiesManager;
import com.pay10.commons.util.TransactionType;
import com.pay10.pg.core.util.AcquirerTxnAmountProvider;
import com.pay10.pg.core.util.YesBankNBEncDecService;

@Service("yesbankNBTransactionConverter")
public class TransactionConverter {

	private static Logger logger = LoggerFactory.getLogger(TransactionConverter.class.getName());

	@Autowired
	private AcquirerTxnAmountProvider acquirerTxnAmountProvider;

	@Autowired
	private YesBankNBEncDecService yesBankNBEncDecService;

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
			request = statusEnquiryRequest(fields, transaction);
			break;
		default:
			request = "";
			break;
		}
		return request.toString();

	}

	public String saleRequest(Fields fields, Transaction transaction) throws SystemException {

		StringBuilder requestString = new StringBuilder();

		try {// TRANC ID
			String txnDt = currentTimeStamp();
			requestString.append(Constants.clientCode);
			requestString.append(Constants.YESBANK_NB_EQUATOR);
			requestString.append(fields.get(FieldType.ADF1.getName())); //
			requestString.append(Constants.YESBANK_NB_AND);

			requestString.append(Constants.merchantCode);
			requestString.append(Constants.YESBANK_NB_EQUATOR);
			requestString.append(fields.get(FieldType.MERCHANT_ID.getName()));// BHARTIPAY
			requestString.append(Constants.YESBANK_NB_AND);

			requestString.append(Constants.txnCurrency);
			requestString.append(Constants.YESBANK_NB_EQUATOR);
			requestString.append(Currency.getAlphabaticCode(fields.get(FieldType.CURRENCY_CODE.getName())));
			requestString.append(Constants.YESBANK_NB_AND);

			String amount = acquirerTxnAmountProvider.amountProvider(fields);
			requestString.append(Constants.txnAmount);
			requestString.append(Constants.YESBANK_NB_EQUATOR);
			requestString.append(amount);
			requestString.append(Constants.YESBANK_NB_AND);

			requestString.append(Constants.txnSurchargeAmount);
			requestString.append(Constants.YESBANK_NB_EQUATOR);
			if (StringUtils.isNotBlank(fields.get(FieldType.SURCHARGE_AMOUNT.getName()))) {
				requestString.append(fields.get(FieldType.SURCHARGE_AMOUNT.getName()));
			} else {
				requestString.append("0");
			}

			requestString.append(Constants.YESBANK_NB_AND);

			requestString.append(Constants.merchantRefNumber);
			requestString.append(Constants.YESBANK_NB_EQUATOR);
			requestString.append(fields.get(FieldType.PG_REF_NUM.getName()));
			requestString.append(Constants.YESBANK_NB_AND);

			requestString.append(Constants.merchantTimeStamp);
			requestString.append(Constants.YESBANK_NB_EQUATOR);
			requestString.append(txnDt);
			requestString.append(Constants.YESBANK_NB_AND);
			fields.put(FieldType.ADF10.getName(), txnDt);
			fields.put(FieldType.UDF1.getName(), txnDt);
			requestString.append(Constants.subMerchantCode);
			requestString.append(Constants.YESBANK_NB_EQUATOR);
			requestString.append(fields.get(FieldType.ADF2.getName()));// MER CODE
			requestString.append(Constants.YESBANK_NB_AND);

			requestString.append(Constants.returnURL);
			requestString.append(Constants.YESBANK_NB_EQUATOR);
			requestString.append(PropertiesManager.propertiesMap.get(Constants.YESBANK_SALE_RETURN_URL));

			logger.info("Yes Bank Net Banking Request String >> " + requestString.toString());

			String checksum = getCalculatedChecksum(requestString.toString());
			if (checksum != null && !checksum.isEmpty()) {
				requestString.append(Constants.YESBANK_NB_AND);
				requestString.append(Constants.checkSum);
				requestString.append(Constants.YESBANK_NB_EQUATOR);
				requestString.append(checksum);
			}

			logger.info("Yes Bank Net Banking Request String after checksum >> " + requestString.toString());

			String encryptedString = yesBankNBEncDecService.encrypt(requestString.toString(),
					fields.get(FieldType.TXN_KEY.getName()));

			logger.info("Yes Bank Net Banking Encrypted Request String >> " + encryptedString.toString());
			return encryptedString;

		}

		catch (Exception e) {
			logger.error("Exception in generating Yes Bank NB Sale Request ", e);
			e.printStackTrace();
			return null;
		}

	}

	public static String getCalculatedChecksum(String p_message) {
		String digest = null;

		try {
			MessageDigest md = MessageDigest.getInstance("MD5");
			byte[] hash = md.digest(p_message.getBytes("UTF-8"));

			StringBuffer sb = new StringBuffer();
			for (int i = 0; i < hash.length; i++) {
				sb.append(Integer.toString((hash[i] & 0xFF) + 256, 16).substring(1));

			}
			digest = sb.toString();

		} catch (Exception e) {
			e.printStackTrace();
		}
		return digest;
	}

	public String refundRequest(Fields fields, Transaction transaction) throws SystemException {

		return null;

	}

	public String statusEnquiryRequest(Fields fields, Transaction transaction) {

		StringBuilder requestString = new StringBuilder();

		try {// TRANC ID
			String txnDt = fields.get(FieldType.ADF10.getName());
			if(StringUtils.isBlank(txnDt)) {
				txnDt = fields.get(FieldType.UDF1.getName());// dt get from db for status enquiry call
			}
			requestString.append(Constants.clientCode);
			requestString.append(Constants.YESBANK_NB_EQUATOR);
			// requestString.append(transaction.getClientCode()); //
			requestString.append(fields.get(FieldType.ADF1.getName())); //
			requestString.append(Constants.YESBANK_NB_AND);

			requestString.append(Constants.merchantCode);
			requestString.append(Constants.YESBANK_NB_EQUATOR);
			// requestString.append(transaction.getMerchantCode());// BHARTIPAY
			//bug fix yesbank deniek by risk
			if(!StringUtils.isEmpty(fields.get(FieldType.MERCHANT_ID.getName())))
			{
				requestString.append(fields.get(FieldType.MERCHANT_ID.getName()));// BHARTIPAY
			}else {
				requestString.append(transaction.getMerchantCode());
				fields.put(FieldType.MERCHANT_ID.getName(),transaction.getMerchantCode());// BHARTIPAY
			}
			
			requestString.append(Constants.YESBANK_NB_AND);

			requestString.append(Constants.txnCurrency);
			requestString.append(Constants.YESBANK_NB_EQUATOR);
			// requestString.append(transaction.getTransactionCurrency());
			requestString.append(Currency.getAlphabaticCode(fields.get(FieldType.CURRENCY_CODE.getName())));
			requestString.append(Constants.YESBANK_NB_AND);

			String amount = acquirerTxnAmountProvider.amountProvider(fields);
			requestString.append(Constants.txnAmount);
			requestString.append(Constants.YESBANK_NB_EQUATOR);
			// requestString.append(transaction.getTxnAmount());
			requestString.append(amount);
			requestString.append(Constants.YESBANK_NB_AND);

			requestString.append(Constants.txnSurchargeAmount);
			requestString.append(Constants.YESBANK_NB_EQUATOR);
			// requestString.append(transaction.getTxnSurchargeAmount());
			if (StringUtils.isNotBlank(fields.get(FieldType.SURCHARGE_AMOUNT.getName()))) {
				requestString.append(fields.get(FieldType.SURCHARGE_AMOUNT.getName()));
			} else {
				requestString.append("0");
			}
			requestString.append(Constants.YESBANK_NB_AND);

			requestString.append(Constants.merchantRefNumber);
			requestString.append(Constants.YESBANK_NB_EQUATOR);
			// requestString.append(transaction.getMerchantRefNumber());
			requestString.append(fields.get(FieldType.PG_REF_NUM.getName()));
			requestString.append(Constants.YESBANK_NB_AND);

//			requestString.append(Constants.bankRefNo);
//			requestString.append(Constants.YESBANK_NB_EQUATOR);
//			requestString.append(transaction.getBankRefNo() == null ? "" : transaction.getBankRefNo());
//			requestString.append(Constants.YESBANK_NB_AND);

			requestString.append(Constants.flagVerify);
			requestString.append(Constants.YESBANK_NB_EQUATOR);
			requestString.append("V");
			requestString.append(Constants.YESBANK_NB_AND);

			requestString.append(Constants.merchantTimeStamp);
			requestString.append(Constants.YESBANK_NB_EQUATOR);
			requestString.append(txnDt);
			requestString.append(Constants.YESBANK_NB_AND);

			requestString.append(Constants.subMerchantCode);
			requestString.append(Constants.YESBANK_NB_EQUATOR);
			// requestString.append(transaction.getSubMerchantCode());// MER CODE
			requestString.append(fields.get(FieldType.ADF2.getName()));// MER CODE
			requestString.append(Constants.YESBANK_NB_AND);

			requestString.append(Constants.returnURL);
			requestString.append(Constants.YESBANK_NB_EQUATOR);
			requestString.append(PropertiesManager.propertiesMap.get(Constants.YESBANK_SALE_RETURN_URL));

			logger.info("Yes Bank Net Banking dv Request String >> " + requestString.toString());

			String checksum = getCalculatedChecksum(requestString.toString());
			if (checksum != null && !checksum.isEmpty()) {
				requestString.append(Constants.YESBANK_NB_AND);
				requestString.append(Constants.checkSum);
				requestString.append(Constants.YESBANK_NB_EQUATOR);
				requestString.append(checksum);
			}
			logger.info("Yes Bank Net Banking dv Request String after checksum >> " + requestString.toString());

			String encryptedString = yesBankNBEncDecService.encrypt(requestString.toString(),
					fields.get(FieldType.TXN_KEY.getName()));
			return encryptedString;

		} catch (Exception e) {
			logger.error("statusEnquiryRequest Exception ", e);
		}
		return null;
	}

	public String currentTimeStamp() {
		String capturedDate = null;
		SimpleDateFormat outputDateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
		try {
			Calendar cal = Calendar.getInstance();
			capturedDate = outputDateFormat.format(cal.getTime());
		} catch (Exception e) {
			logger.error("Exception", e);
		}
		return capturedDate;
	}

	public Transaction toTransaction(String response) {

		Transaction transaction = new Transaction();
		try {
			logger.info("response----" + response);
			String strArr[] = response.split("&");
			Map<String, String> responseMap = new HashMap<String, String>();
			if (strArr.length > 0) {
				List<String> strList = Arrays.asList(strArr);
				for (String str : strList) {
					String keyList[] = str.split("=");
					if (keyList.length > 1) {
						responseMap.put(keyList[0], keyList[1]);
					}
				}
			}

			if (response.contains(Constants.merchantCode)) {
				transaction.setMerchantCode(responseMap.get(Constants.merchantCode));
			}

			if (response.contains(Constants.clientCode)) {
				transaction.setClientCode(responseMap.get(Constants.clientCode));
			}

			if (response.contains(Constants.txnCurrency)) {
				transaction.setTransactionCurrency(responseMap.get(Constants.txnCurrency));
			}

			if (response.contains(Constants.txnAmount)) {
				transaction.setTxnAmount(responseMap.get(Constants.txnAmount));
			}

			if (response.contains(Constants.txnSurchargeAmount)) {
				transaction.setTxnSurchargeAmount(responseMap.get(Constants.txnSurchargeAmount));
			}

			if (response.contains(Constants.merchantRefNumber)) {
				transaction.setMerchantRefNumber(responseMap.get(Constants.merchantRefNumber));
			}
			if (response.contains(Constants.defaultSuccessFlag)) {
				transaction.setDefaultSuccessFlag(responseMap.get(Constants.defaultSuccessFlag));
			} else {
				transaction.setDefaultSuccessFlag("N");
			}

			if (response.contains(Constants.defaultFailedFlag)) {
				transaction.setDefaultFailedFlag(responseMap.get(Constants.defaultFailedFlag));
			} else {
				transaction.setDefaultFailedFlag("N");
			}

			if (response.contains(Constants.merchantTimeStamp)) {
				transaction.setMerchantTimeStamp(responseMap.get(Constants.merchantTimeStamp));
			}
			if (response.contains(Constants.bankRefNo)) {
				transaction.setBankRefNo(responseMap.get(Constants.bankRefNo));
			}

			if (response.contains(Constants.subMerchantCode)) {
				transaction.setSubMerchantCode(responseMap.get(Constants.subMerchantCode));
			}

			if (response.contains(Constants.message)) {
				transaction.setMessage(responseMap.get(Constants.message));
			}

			if (response.contains("checkSum")) {
				transaction.setCheckSum(responseMap.get("checkSum"));
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
			logger.info("response----" + response);
			String strArr[] = response.split("&");
			Map<String, String> responseMap = new HashMap<String, String>();
			if (strArr.length > 0) {
				List<String> strList = Arrays.asList(strArr);
				for (String str : strList) {
					String keyList[] = str.split("=");
					if (keyList.length > 1) {
						responseMap.put(keyList[0], keyList[1]);
					}
				}
			}

			if (response.contains(Constants.merchantCode)) {
				transaction.setMerchantCode(responseMap.get(Constants.merchantCode));
			}

			if (response.contains(Constants.clientCode)) {
				transaction.setClientCode(responseMap.get(Constants.clientCode));
			}

			if (response.contains(Constants.txnCurrency)) {
				transaction.setTransactionCurrency(responseMap.get(Constants.txnCurrency));
			}

			if (response.contains(Constants.txnAmount)) {
				transaction.setTxnAmount(responseMap.get(Constants.txnAmount));
			}

			if (response.contains(Constants.txnSurchargeAmount)) {
				transaction.setTxnSurchargeAmount(responseMap.get(Constants.txnSurchargeAmount));
			}

			if (response.contains(Constants.merchantRefNumber)) {
				transaction.setMerchantRefNumber(responseMap.get(Constants.merchantRefNumber));
			}
			if (response.contains(Constants.defaultSuccessFlag)) {
				transaction.setDefaultSuccessFlag(responseMap.get(Constants.defaultSuccessFlag));
			} else {
				transaction.setDefaultSuccessFlag("N");
			}

			if (response.contains(Constants.defaultFailedFlag)) {
				transaction.setDefaultFailedFlag(responseMap.get(Constants.defaultFailedFlag));
			} else {
				transaction.setDefaultFailedFlag("N");
			}

			if (response.contains(Constants.merchantTimeStamp)) {
				transaction.setMerchantTimeStamp(responseMap.get(Constants.merchantTimeStamp));
			}
			if (response.contains(Constants.bankRefNo)) {
				transaction.setBankRefNo(responseMap.get(Constants.bankRefNo));
			}

			if (response.contains(Constants.subMerchantCode)) {
				transaction.setSubMerchantCode(responseMap.get(Constants.subMerchantCode));
			}

			if (response.contains(Constants.message)) {
				transaction.setMessage(responseMap.get(Constants.message));
			}
			if (response.contains(Constants.flgSuccess)) {
				transaction.setFlgSuccess(responseMap.get(Constants.flgSuccess));
			}

			if (response.contains("checkSum")) {
				transaction.setCheckSum(responseMap.get("checkSum"));
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
