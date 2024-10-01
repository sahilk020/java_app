package com.pay10.kotak;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.security.GeneralSecurityException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.pay10.commons.api.Hasher;
import com.pay10.commons.exception.ErrorType;
import com.pay10.commons.exception.SystemException;
import com.pay10.commons.util.Amount;
import com.pay10.commons.util.FieldType;
import com.pay10.commons.util.Fields;
import com.pay10.commons.util.PropertiesManager;
import com.pay10.commons.util.StatusType;
import com.pay10.commons.util.TransactionManager;
import com.pay10.commons.util.Validator;
import com.pay10.kotak.nb.KotakTransformernb;
import com.pay10.kotak.nb.TransactionCommunicator;
import com.pay10.kotak.nb.TransactionConverter;
import com.pay10.pg.core.util.AcquirerTxnAmountProvider;
import com.pay10.pg.core.util.KotakNBUtils;
import com.pay10.pg.core.util.ProcessManager;
import com.pay10.pg.core.util.Processor;
import com.pay10.pg.core.util.refundkotaknb;

@Service
public class KotakSaleResponseHandler {

	private static Logger logger = LoggerFactory.getLogger(KotakSaleResponseHandler.class.getName());

	@Autowired
	private Validator generalValidator;

	@Autowired
	@Qualifier("kotakNBUtils")
	private KotakNBUtils kotakNBUtils;

	@Autowired
	@Qualifier("kotakNBTransactionCommunicator")
	private TransactionCommunicator communicator;

	@Autowired
	@Qualifier("kotakNBTransactionConverter")
	private TransactionConverter converter;

	@Autowired
	@Qualifier("updateProcessor")
	private Processor updateProcessor;

	@Autowired
	private KotakTransformer kotakTransformer;

	public Map<String, String> processOld(Fields fields) throws SystemException {
		String newTxnId = TransactionManager.getNewTransactionId();
		fields.put(FieldType.TXN_ID.getName(), newTxnId);
		Transaction transactionResponse = new Transaction();
		String response = fields.get(FieldType.KOTAK_RESPONSE_FIELD.getName());
		boolean res = isHashMatching(response, fields);
		fields.remove(FieldType.KOTAK_RESPONSE_FIELD.getName());

		generalValidator.validate(fields);

		try {
			response = URLDecoder.decode(response, "UTF-8");
		} catch (UnsupportedEncodingException exception) {
			logger.error("Exception ", exception);
		}

		if (res == true) {
			transactionResponse = toTransaction(response, transactionResponse);
			kotakTransformer = new KotakTransformer(transactionResponse);
			kotakTransformer.updateResponse(fields);
		} else {
			fields.put(FieldType.STATUS.getName(), StatusType.DENIED.getName());
			fields.put(FieldType.RESPONSE_CODE.getName(), ErrorType.SIGNATURE_MISMATCH.getCode());
			fields.put(FieldType.RESPONSE_MESSAGE.getName(), ErrorType.SIGNATURE_MISMATCH.getResponseMessage());
		}

		fields.put(FieldType.INTERNAL_ORIG_TXN_TYPE.getName(), fields.get(FieldType.TXNTYPE.getName()));
		ProcessManager.flow(updateProcessor, fields, true);
		return fields.getFields();

	}

	public Map<String, String> process(Fields fields) throws SystemException {
		logger.info("fields -------->>  " + fields.getFieldsAsString());
		String newTxnId = TransactionManager.getNewTransactionId();
		fields.put(FieldType.TXN_ID.getName(), newTxnId);
		Transaction transactionResponse = new Transaction();
		String response = fields.get(FieldType.KOTAK_RESPONSE_FIELD.getName());
		logger.info("KOTAK RESPONSE FIELD : " + response);
		// boolean res = isHashMatching(response, fields);
		// logger.info("res " + res);
		fields.remove(FieldType.KOTAK_RESPONSE_FIELD.getName());

		generalValidator.validate(fields);

		transactionResponse = toTransaction(response, transactionResponse);
		boolean doubleVer = doubleVerification(transactionResponse, fields);
		logger.info("process:: doubleVerification={}, pgRefNo={} ", doubleVer,
				fields.get(FieldType.PG_REF_NUM.getName()));
		if (doubleVer) {

			kotakTransformer = new KotakTransformer(transactionResponse);
			kotakTransformer.updateResponse(fields);
		} else {
			fields.put(FieldType.STATUS.getName(), StatusType.DENIED.getName());
			fields.put(FieldType.RESPONSE_CODE.getName(), ErrorType.SIGNATURE_MISMATCH.getCode());
			fields.put(FieldType.RESPONSE_MESSAGE.getName(), ErrorType.SIGNATURE_MISMATCH.getResponseMessage());
		}

		logger.info("<<<<<<< fields >>>>>>>>>> " + fields.getFieldsAsString());

		fields.put(FieldType.INTERNAL_ORIG_TXN_TYPE.getName(), fields.get(FieldType.TXNTYPE.getName()));
		ProcessManager.flow(updateProcessor, fields, true);
		return fields.getFields();

	}

	private boolean doubleVerification(Transaction transactionResponse, Fields fields) {

		try {

			String encrequest = statusEnquiryRequest(fields);
			String hostUrl = PropertiesManager.propertiesMap.get("refundurl");
			String response = communicator.refundPostRequest(encrequest, hostUrl, fields);

			String encryptKey = fields.get(FieldType.ADF3.getName()); // "211995C8985ABA0F291E1941608FEFBD";

			String encData = refundkotaknb.decrypt(response, encryptKey);

			logger.info("encData dual verification response" + encData);
			String s1 = encData;
			String[] words = s1.split("\\|"); // "
												// 0051|07032022181903|OSENAM|1009120307111447||1.00|N|KPY209|dd1dcf73e29ef61608abb96c762bf1babd2769cb20eb140e3d2008b6010078f1"
			logger.info(words[3]);
			String pgref = words[3];
			String status = words[5];
			String amount = words[4];

			if (!amount.contains(".")) {
				amount = amount + ".00";
			}else {
				String number = amount.substring(amount.lastIndexOf(".") + 1);
				if(number.length() == 1) {
					amount = amount + "0";
				}
			}

			String toamount = Amount.toDecimal(fields.get(FieldType.TOTAL_AMOUNT.getName()), "356");
			logger.info("dual verification::  kotak NB response. Status={},resAmount={},pgRefNo={},toamount={},reqPgRefNo={}",
					status, amount, pgref, toamount, fields.get(FieldType.PG_REF_NUM.getName()));

			if (status != null && status.equalsIgnoreCase("Y")
					&& fields.get(FieldType.PG_REF_NUM.getName()).equalsIgnoreCase(pgref)
					&& toamount.equalsIgnoreCase(amount)) {

				return true;
			}
			logger.info("doubleVerification:: failed. Status={},  resAmount={},pgRefNo={}", status, amount, pgref);

			return false;
		}

		catch (Exception e) {
			logger.error("Exceptionn ", e);
			return false;
		}

	}

	private String statusEnquiryRequest(Fields fields) {
		String merchantCode = fields.get(FieldType.MERCHANT_ID.getName());
		String encryptKey = fields.get(FieldType.ADF3.getName());
		logger.info("checksum" + encryptKey);

		// "211995C8985ABA0F291E1941608FEFBD";
		String checksumKey = fields.get(FieldType.ADF2.getName());

		String requestDate = getRequestDateFormat();

		StringBuilder paymentStringBuilder = new StringBuilder();

		// for kotak testing to be removed in prod
		paymentStringBuilder.append("0520");
		paymentStringBuilder.append("|");
		paymentStringBuilder.append(requestDate);
		paymentStringBuilder.append("|");
		paymentStringBuilder.append(merchantCode);
		paymentStringBuilder.append("|");
		paymentStringBuilder.append(fields.get(FieldType.PG_REF_NUM.getName()));
		paymentStringBuilder.append("|");
		paymentStringBuilder.append("|");
		paymentStringBuilder.append("|");

		logger.info("Kotak NetBanking dual verification Request : " + paymentStringBuilder.toString());
		// String checksum =
		// kotakNBUtils.encodeWithHMACSHA2(paymentStringBuilder.toString(),
		// checksumKey);
		// String checksum = kotakNBUtils.calculateHMAC(paymentStringBuilder.toString(),
		// checksumKey);
		String checksum = kotakNBUtils.getHMAC256Checksum(paymentStringBuilder.toString(), checksumKey);
		String data = checksum.toLowerCase();

		paymentStringBuilder.append(data);

		logger.info("Kotak NetBanking dual verification Request with checksum : " + paymentStringBuilder.toString()
				+ "    " + encryptKey);

		String encData = null;
		try {
			encData = refundkotaknb.encrypt(paymentStringBuilder.toString(), encryptKey);
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (GeneralSecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} // publicKeyPath

		return encData;
	}

	public Transaction toTransaction(String response, Transaction transactionResponse) {

		Transaction transaction = new Transaction();

		response = response.replace("~", "");
		String[] values = response.split("\\|");

		// String checksumStr = values[0] + "|" + values[1] + "|" + values[2] + "|" +
		// values[3] + "|"+ values[4] + "|" + values[5] + "|" + values[6];
		// System.out.println("checksumStr " + checksumStr);
		// String checksum = new KotakNBUtils().getHMAC256Checksum(checksumStr,
		// "KMBANK");
		// System.out.println("checksum " + checksum);

		transaction.setMessagCode(values[0]);
		transaction.setDateAndTime(values[1]);
		transaction.setMerchantId(values[2]);
		transaction.setMerchantReference(values[3]);
		transaction.setAmount(values[4]);
		transaction.setAuthStatus(values[5]);
		transaction.setBankReferenceNumber(values[6]);
		transaction.setChecksum(values[7]);

		/*
		 * Transaction transaction = new Transaction(); String[] values =
		 * response.split("::"); Map<String, String> receivedValues = new HashMap<>();
		 * for (String string : values) { String[] splitter =
		 * string.split(Constants.EQUATOR); receivedValues.put(splitter[0],
		 * splitter[1]);
		 * 
		 * } String responseCode = receivedValues.get(Constants.RESPONSE_CODE);
		 * transaction.setResponseCode(responseCode);
		 * transaction.setAcqId(receivedValues.get(Constants.RET_REF_NO)); if
		 * (responseCode.equals(Constants.RESPONSE_CODE_VALUE)) {
		 * transaction.setAuthCode(receivedValues.get(Constants.AUTH_CODE)); }
		 * transaction.setHash(receivedValues.get(Constants.HASH));
		 * transaction.setMessage(receivedValues.get(Constants.MESSAGE)); return
		 * transaction;
		 */

		logger.info("response form kotak bank" + transaction.toString());

		return transaction;
	}

	private boolean isHashMatching(String transactionResponse, Fields fields) throws SystemException {

		logger.info("transactionResponse : " + transactionResponse);
		logger.info("fields : " + fields.getFieldsAsString());
		String[] ary = transactionResponse.split(Constants.SEPARATOR);
		Arrays.sort(ary);
		logger.info(" ary : " + ary.toString());
		StringBuilder hashString = new StringBuilder();
		Map<String, String> myMap = new TreeMap<String, String>();

		for (int i = 0; i < ary.length; i++) {

			String key = ary[i].split(Constants.EQUATOR)[0];
			String value = ary[i].split(Constants.EQUATOR)[1];

			if (key.equalsIgnoreCase(Constants.MESSAGE)) {
				value = value.replace("+", " ");
			}
			try {
				value = URLDecoder.decode(value, "UTF-8");
			} catch (UnsupportedEncodingException exception) {
				logger.error("Exception ", exception);
			}
			myMap.put(key, value);
		}
		hashString.append(fields.get(FieldType.PASSWORD.getName()));
		// hashString.append(fields.get(FieldType.ADF2.getName()));

		logger.info("hashString " + hashString);
		for (Map.Entry<String, String> param : myMap.entrySet()) {

			if (param.getKey().equalsIgnoreCase(Constants.HASH)) {
				continue;
			} else {

				hashString.append(param.getValue());
			}
		}

		String generateHash = null;
		String receivedHash = myMap.get(Constants.HASH);
		logger.info("receivedHash " + receivedHash);
		try {
			generateHash = Hasher.getHash(hashString.toString());
			logger.info("generateHash " + generateHash);
		} catch (SystemException exception) {
			logger.error("Exception", exception);
		}

		return receivedHash.equalsIgnoreCase(generateHash);
	}

	public static void main(String[] args) {
		String response = "0502|17012022173710|OSENAM|1109120117173615|1|N|0006293429|D71D5E1A1906397DED2168EC208D53ACA41E7A0E5D9D042777B2BE6943696077~";

		/*
		 * response = response.replace("~", ""); String[] value_split =
		 * response.split("\\|");
		 * 
		 * String checkSumStr = value_split[0] + "|" + value_split[1] + "|" +
		 * value_split[2] + "|" + value_split[3] + "|" + value_split[4] + "|" +
		 * value_split[5] + "|" + value_split[6]; System.out.println("checkSumStr " +
		 * checkSumStr); String checksum = new
		 * KotakNBUtils().getHMAC256Checksum(checkSumStr, "KMBANK");
		 * 
		 * System.out.println("checksum " + checksum);
		 * 
		 * System.out.println("values 0 " + value_split[0]);
		 * System.out.println("values 1 " + value_split[1]);
		 * System.out.println("values 2 " + value_split[2]);
		 * System.out.println("values 3 " + value_split[3]);
		 * System.out.println("values 0 " + value_split[4]);
		 * System.out.println("values 1 " + value_split[5]);
		 * System.out.println("values 2 " + value_split[6]);
		 * System.out.println("values 3 " + value_split[7]);
		 */

		/*
		 * Transaction transaction = new Transaction();
		 * 
		 * response = response.replace("~", ""); String[] values =
		 * response.split("\\|");
		 * 
		 * String checksumStr = values[0] + "|" + values[1] + "|" + values[2] + "|" +
		 * values[3] + "|" + values[4] + "|" + values[5] + "|" + values[6];
		 * System.out.println("checksumStr " + checksumStr); String checksum = new
		 * KotakNBUtils().getHMAC256Checksum(checksumStr, "KMBANK");
		 * 
		 * System.out.println("checksum " + checksum);
		 * 
		 * transaction.setMessagCode(values[0]); transaction.setDateAndTime(values[1]);
		 * transaction.setMerchantId(values[2]);
		 * transaction.setMerchantReference(values[3]);
		 * transaction.setAmount(values[4]); transaction.setAuthStatus(values[5]);
		 * transaction.setBankReferenceNumber(values[6]);
		 * transaction.setChecksum(values[6]);
		 */
		Fields fields = new Fields();
		String s1 = response;
		String[] words = s1.split("\\|"); // "
											// 0051|07032022181903|OSENAM|1009120307111447||1.00|N|KPY209|dd1dcf73e29ef61608abb96c762bf1babd2769cb20eb140e3d2008b6010078f1"
		logger.info(words[3]);
		String pgref = words[3];
		String status = words[5];
		String amount = words[4];
		
		if (!amount.contains(".")) {
			amount = amount + ".00";
		}else {
			String number = amount.substring(amount.lastIndexOf(".") + 1);
			if(number.length() == 1) {
				amount = amount + "0";
			}
		}
		fields.put(FieldType.TOTAL_AMOUNT.getName(),"100");
		String toamount = Amount.toDecimal(fields.get(FieldType.TOTAL_AMOUNT.getName()), "356");
		
		System.out.println("toamount "+toamount + " amount "+amount);
		
	}
	

	public static String getRequestDateFormat() {
		Date dNow = new Date();
		SimpleDateFormat ft = new SimpleDateFormat("DDMMYYYYHH");
		return ft.format(dNow);

	}
}
