package com.pay10.payten;

import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.EmptyStackException;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Stack;

import org.apache.commons.codec.binary.Hex;
import org.apache.commons.lang3.StringUtils;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.pay10.commons.exception.SystemException;
import com.pay10.commons.util.ChecksumUtils;
import com.pay10.commons.util.FieldType;
import com.pay10.commons.util.Fields;
import com.pay10.commons.util.PaymentType;
import com.pay10.commons.util.TransactionManager;
import com.pay10.commons.util.TransactionType;
import com.pay10.pg.core.util.AcquirerTxnAmountProvider;
import com.pay10.pg.core.util.ScramblerCustom;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.MapType;

@Service("paytenTransactionConverter")
public class TransactionConverter {

	@Autowired
	private AcquirerTxnAmountProvider acquirerTxnAmountProvider;

	private static final Logger logger = LoggerFactory.getLogger(TransactionConverter.class.getName());
	private static Stack<MessageDigest> stack = new Stack<MessageDigest>();
	public static final String ALGO = "AES";
	// private static String key = null;
//	private static Key keyObj = null;

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
		}
		return request.toString();

	}

	public String saleRequest(Fields fields, Transaction transaction) throws SystemException {
		String encData = null;

		try {

			String encKey = transaction.getEncKey();

			Map<String, String> parameters = new LinkedHashMap<String, String>();
			parameters.put("AMOUNT", transaction.getAmount());

			if (StringUtils.isNotBlank(fields.get(FieldType.PAYMENT_TYPE.getName()))
					&& (fields.get(FieldType.PAYMENT_TYPE.getName()).equalsIgnoreCase(PaymentType.CREDIT_CARD.getCode())
							|| fields.get(FieldType.PAYMENT_TYPE.getName())
									.equalsIgnoreCase(PaymentType.DEBIT_CARD.getCode()))) {

				parameters.put("CARD_EXP_DT", transaction.getExpiryDate());
				parameters.put("CARD_NUMBER", transaction.getCardNumber());
				parameters.put("CVV", transaction.getCavv());
				parameters.put("PAYMENT_TYPE", "CARD");
				parameters.put("CARD_SAVE_FLAG",fields.get(FieldType.CARD_SAVE_FLAG.getName()) );
			}

			else if (StringUtils.isNotBlank(fields.get(FieldType.PAYMENT_TYPE.getName()))
					&& (fields.get(FieldType.PAYMENT_TYPE.getName()).equalsIgnoreCase(PaymentType.UPI.getCode()))) {

				parameters.put("PAYMENT_TYPE", "UP");
				parameters.put("PAYER_ADDRESS", transaction.getPayerAddress());

			} else if (StringUtils.isNotBlank(fields.get(FieldType.PAYMENT_TYPE.getName())) && (fields
					.get(FieldType.PAYMENT_TYPE.getName()).equalsIgnoreCase(PaymentType.NET_BANKING.getCode()))) {
				parameters.put("PAYMENT_TYPE", "NB");
			}

			else if (StringUtils.isNotBlank(fields.get(FieldType.PAYMENT_TYPE.getName()))
					&& (fields.get(FieldType.PAYMENT_TYPE.getName()).equalsIgnoreCase(PaymentType.WALLET.getCode()))) {
				parameters.put("PAYMENT_TYPE", "WL");
			}

			parameters.put("MOP_TYPE", transaction.getMopType());
			parameters.put("CURRENCY_CODE", transaction.getCurrencyCode());

			if (StringUtils.isNotBlank(transaction.getCustEmail())) {
				parameters.put("CUST_EMAIL", transaction.getCustEmail());
			}

			if (StringUtils.isNotBlank(transaction.getCustName())) {
				parameters.put("CUST_NAME", transaction.getCustName());
			}
			if (StringUtils.isNotBlank(transaction.getCustPhone())) {
				parameters.put("CUST_PHONE", transaction.getCustPhone());
			}

			parameters.put("CARD_HOLDER_NAME", transaction.getCardHolderName());
			parameters.put("ORDER_ID", transaction.getOrderId());
			parameters.put("PAY_ID", transaction.getPayId());
			parameters.put("RETURN_URL", transaction.getRedirectUrl());
			if (StringUtils.isNotBlank(transaction.getUdf12())) {
				parameters.put("UDF12", transaction.getUdf12());
			}
			if (StringUtils.isNotBlank(transaction.getUdf13())) {
				parameters.put("UDF13", transaction.getUdf13());
			}
			String data = ChecksumUtils.getString(parameters);
			logger.info("Transaction data " + data);
			String req = data + "~HASH=" + ChecksumUtils.generateCheckSum(parameters, transaction.getSaltKey());
			logger.info("Transaction data with HASH " + req);
			encData = encrypt(transaction.getPayId(), req, encKey);

			logger.info("Transaction data Encdata " + encData);

		}

		catch (

		Exception e) {
			logger.error("Exception in generating payten sale request ", e);
			return null;
		}
		return encData;
	}

	/*
	 * public String encrypt(String data,String key ,Key keyObj ) { try { String
	 * ivString = key.substring(0,16); IvParameterSpec iv = new
	 * IvParameterSpec(ivString.getBytes("UTF-8")); Cipher cipher =
	 * Cipher.getInstance(ALGO + "/CBC/PKCS5PADDING");
	 * cipher.init(Cipher.ENCRYPT_MODE, keyObj, iv);
	 * 
	 * byte[] encValue = cipher.doFinal(data.getBytes("UTF-8"));
	 * 
	 * Base64.Encoder base64Encoder = Base64.getEncoder().withoutPadding(); String
	 * base64EncodedData = base64Encoder.encodeToString(encValue); return
	 * base64EncodedData; } catch (NoSuchAlgorithmException | NoSuchPaddingException
	 * | InvalidKeyException | IOException | IllegalBlockSizeException |
	 * BadPaddingException | InvalidAlgorithmParameterException
	 * scramblerExceptionException) { } return data; }
	 */

	/*
	 * public static String hostedEncrypt(String data ,String key ,Key keyObj )
	 * throws SystemException { try {
	 * 
	 * String ivString = key.substring(0, 16); IvParameterSpec iv = new
	 * IvParameterSpec(ivString.getBytes(SystemConstants.DEFAULT_ENCODING_UTF_8));
	 * 
	 * Cipher cipher = Cipher.getInstance(ALGO + "/CBC/PKCS5PADDING");
	 * cipher.init(Cipher.ENCRYPT_MODE, keyObj, iv);
	 * 
	 * byte[] encValue =
	 * cipher.doFinal(data.getBytes(SystemConstants.DEFAULT_ENCODING_UTF_8));
	 * 
	 * Base64.Encoder base64Encoder = Base64.getEncoder().withoutPadding(); String
	 * base64EncodedData = base64Encoder.encodeToString(encValue); return
	 * base64EncodedData; } catch (NoSuchAlgorithmException | NoSuchPaddingException
	 * | InvalidKeyException | IOException | IllegalBlockSizeException |
	 * BadPaddingException | InvalidAlgorithmParameterException
	 * scramblerExceptionException) { throw new
	 * SystemException(ErrorType.CRYPTO_ERROR, scramblerExceptionException,
	 * "Error during encryption process"); } }
	 */

	public static String getHash(String input) {
		String response = null;

		MessageDigest messageDigest = provide();
		messageDigest.update(input.getBytes());
		consume(messageDigest);

		response = new String(Hex.encodeHex(messageDigest.digest()));

		return response.toUpperCase();
	}

	public static MessageDigest provide() {
		MessageDigest digest = null;
		try {
			digest = stack.pop();
		} catch (EmptyStackException emptyStackException) {
			try {
				digest = MessageDigest.getInstance("SHA-256");
			} catch (NoSuchAlgorithmException noSuchAlgorithmException) {

			}
		}

		return digest;
	}

	public static void consume(MessageDigest digest) {
		stack.push(digest);
	}

	public String refundRequest(Fields fields, Transaction transaction) throws SystemException {
		
		String requestStr = null;
		try {
			
			String encKey = transaction.getEncKey();
			Map<String,String> request = new HashMap<>();

			request.put("REFUND_FLAG", "R");
			request.put("PAY_ID", transaction.getPayId());
			request.put("ORDER_ID", transaction.getOrderId());
			request.put("AMOUNT", transaction.getAmount());
			request.put("TXNTYPE", transaction.getTxnType());
			request.put("REFUND_ORDER_ID", transaction.getRefundOrderId());
			//request.put("PG_REF_NUM", transaction.getPgRefNum());
			request.put("CURRENCY_CODE", "356");
			//request.put("HASH", transaction.getHaskey());
			String data = ChecksumUtils.getString(request);
			logger.info("Payten refund request  =  " + request.toString());
			String encData = encrypt(transaction.getPayId(), data, encKey);
			logger.info("Refund data Encdata " + encData);
			JSONObject requestObj = new JSONObject();
			requestObj.put("PAY_ID", transaction.getPayId());
			requestObj.put("ENCDATA", encData);
			requestStr = requestObj.toString();
			logger.info("Refund request String " + requestStr);
			
			return requestStr;
		}

		catch (Exception e) {
			logger.error("Exception in generating Payten refund request", e);
		}
		return requestStr;

	}

	public Transaction toTransaction(String jsonResponse, String txnType) {

		Transaction transaction = new Transaction();

		return transaction;

	}
	
	
	public Transaction toTransactionRefund(String jsonResponse,Fields fields) {
		Transaction transaction = new Transaction();
		try {
		Map<String, String> resMap = new HashMap<String, String>();
		Map<String, String> decryptedResMap = new HashMap<String, String>();
		final ObjectMapper mapper = new ObjectMapper();
		final MapType type = mapper.getTypeFactory().constructMapType(Map.class, String.class, Object.class);
		
			resMap = mapper.readValue(jsonResponse, type);
		
		String decryptedData = decrypt(fields.get(FieldType.MERCHANT_ID.getName()), resMap.get("ENCDATA"),
				fields.get(FieldType.ADF1.getName()));
		logger.info("Decrypted Refund Response From PAYTEN : " + decryptedData);
		String[] paramaters = decryptedData.split("~");
		for (String param : paramaters) {
			String[] parameterPair = param.split("=");
			if (parameterPair.length > 1) {
				decryptedResMap.put(parameterPair[0].trim(), parameterPair[1].trim());
			}
		}
		
		transaction.setStatus(decryptedResMap.get(FieldType.STATUS.getName()));
		transaction.setResponseCode(decryptedResMap.get(FieldType.RESPONSE_CODE.getName()));
		//transaction.setRrn();
		//transaction.setAcqId();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return transaction;

	}

	public TransactionConverter() {

	}

	public String encrypt(String payId, String encData, String key) {

		try {

			ScramblerCustom scrambler = new ScramblerCustom(key);
			String data = scrambler.encrypt(encData);
			return data;
		}

		catch (Exception e) {
			logger.error("Exception ", e);
			return null;
		}

	}
	
	
	public String decrypt(String payId, String encData, String key) {

		try {

			ScramblerCustom scrambler = new ScramblerCustom(key);
			String data = scrambler.decrypt(encData);
			return data;
		}

		catch (Exception e) {
			logger.error("Exception ", e);
			return null;
		}

	}

}
