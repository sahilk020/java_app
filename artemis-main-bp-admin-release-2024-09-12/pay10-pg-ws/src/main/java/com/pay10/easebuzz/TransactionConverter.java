package com.pay10.easebuzz;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.regex.Pattern;

import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import com.google.gson.JsonObject;
import com.pay10.commons.dao.FieldsDao;
import com.pay10.commons.exception.SystemException;
import com.pay10.commons.util.Amount;
import com.pay10.commons.util.FieldType;
import com.pay10.commons.util.Fields;
import com.pay10.commons.util.PaymentType;
import com.pay10.commons.util.PropertiesManager;
import com.pay10.commons.util.TransactionType;
import com.pay10.pg.core.util.AcquirerTxnAmountProvider;
import com.pay10.pg.core.util.EasebuzzChecksumUtil;
import com.pay10.pg.core.whitelabel.ReturnUrlCustomizer;

@Service("easebuzzTransactionConverter")
public class TransactionConverter {

	private static Logger logger = LoggerFactory.getLogger(TransactionConverter.class.getName());

	@Autowired
	private AcquirerTxnAmountProvider acquirerTxnAmountProvider;

	@Autowired
	private ReturnUrlCustomizer returnUrlCustomizer;

	@Autowired
	private EasebuzzChecksumUtil easebuzzChecksumUtil;

	@Autowired
	private FieldsDao fieldsDao;

	@Autowired
	RestTemplate restTemplate;

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
		return request;

	}

	public String saleRequest(Fields fields, Transaction transaction) throws SystemException {

		logger.info("transaction " + transaction);
		String accessKey = getEasebuzzAccessKey(fields, transaction);
		// String amount = acquirerTxnAmountProvider.amountProvider(fields);
		String returnUrl = returnUrlCustomizer.customizeReturnUrl(fields,
				PropertiesManager.propertiesMap.get(Constants.SALE_RETURN_URL));

		logger.info("transaction >>>>>>>>>> " + transaction.toString());
		logger.info("fields >>>>>>>>>> " + fields.getFieldsAsString());

		JsonObject jsonRequest = new JsonObject();
		prepareSaleRequest(jsonRequest, fields, transaction, accessKey);
		logger.info("jsonRequest " + jsonRequest);

		/*
		 * String cardExpiryDateFormat = transaction.getCardExp(); cardExpiryDateFormat
		 * = cardExpiryDateFormat.substring(0, 2) + "/" +
		 * cardExpiryDateFormat.substring(2);
		 * logger.info("cardExpiryDateFormat >>>>>>>>>>>>>> " + cardExpiryDateFormat);
		 * String cardNumber = easebuzzChecksumUtil.encrypt(transaction.getCardNo(),
		 * transaction.getEncryptionKey(), transaction.getIv()); String card_cvv =
		 * easebuzzChecksumUtil.encrypt(transaction.getCvv(),
		 * transaction.getEncryptionKey(), transaction.getIv()); String cardExpiryDate =
		 * easebuzzChecksumUtil.encrypt(cardExpiryDateFormat,
		 * transaction.getEncryptionKey(), transaction.getIv()); String cardHolderName =
		 * easebuzzChecksumUtil.encrypt(transaction.getCardHname(),
		 * transaction.getEncryptionKey(), transaction.getIv());
		 * 
		 * JsonObject finalRequest = new JsonObject();
		 * finalRequest.addProperty("access_key", accessKey);
		 * finalRequest.addProperty("payment_mode", transaction.getPaymentMode());
		 * finalRequest.addProperty("card_holder_name", cardHolderName);
		 * finalRequest.addProperty("card_number", cardNumber);
		 * finalRequest.addProperty("card_expiry_date", cardExpiryDate);
		 * finalRequest.addProperty("card_cvv", card_cvv);
		 */
		// finalRequest.addProperty("pay_later_app", "Simpl");
		// finalRequest.addProperty("request_mode", "SUVA");

		logger.info("FInal Request jsonRequest >>>>>>>>>>> " + jsonRequest.toString());
		return jsonRequest.toString();

	}

	private String getEasebuzzAccessKey(Fields fields, Transaction transaction) throws SystemException {
		String amount = acquirerTxnAmountProvider.amountProvider(fields);
		StringBuilder sb = new StringBuilder();

		// key|txnid|amount|productinfo|firstname|email|udf1|udf2|udf3|udf4|udf5|udf6|udf7|udf8|udf9|udf10|salt
		// key|txnid|amount|productinfo|firstname|email|||||||||||salt
		// 2PBP7IABZ2|1901202215021234|10.00|Testing|Sonu|sonu@pay10.com|||||||||||DAH88E3UWQ

		sb.append(transaction.getMerchantKey() + "|");
		sb.append(fields.get(FieldType.TXN_ID.getName()) + "|");
		sb.append(transaction.getAmount() + "|");
		sb.append(transaction.getProductinfo() + "|");
		sb.append(transaction.getFirstname() + "|");
		sb.append(transaction.getEmail());
		sb.append("|||||||||||");
		sb.append(transaction.getSalt());

		logger.info("Plain request for generating checksum for easebuzz access key " + sb.toString());

		String checkSum = easebuzzChecksumUtil.checksumForInitiateLink(sb.toString());
		logger.info("checksum for access Key " + checkSum);
		transaction.setHash(checkSum);

		AccessTokenDTO accessTokenDTO = httpCall(transaction);
		logger.info("accessTokenDTO " + accessTokenDTO.toString());
		return accessTokenDTO.getData();
	}

	public AccessTokenDTO httpCall(Transaction transaction) {

		logger.info("Request Received");
		logger.info("transaction " + transaction.toString());
		String url = "https://pay.easebuzz.in/payment/initiateLink";
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

		MultiValueMap<String, String> map = new LinkedMultiValueMap<String, String>();
		map.add("key", transaction.getMerchantKey());
		map.add("txnid", transaction.getTxnid());
		map.add("amount", transaction.getAmount());
		map.add("productinfo", transaction.getProductinfo());
		map.add("firstname", transaction.getFirstname());
		map.add("phone", transaction.getPhone());
		map.add("email", transaction.getEmail());
		map.add("surl", transaction.getSurl());
		map.add("furl", transaction.getFurl());
		map.add("hash", transaction.getHash());
		map.add("udf1", "");
		map.add("udf2", "");
		map.add("udf3", "");
		map.add("udf4", "");
		map.add("udf5", "");
		map.add("udf6", "");
		map.add("udf7", "");
		map.add("udf8", "");
		map.add("udf9", "");
		map.add("udf10", "");
		map.add("request_flow", "SEAMLESS");

		logger.info("map >>>>>>>>>>>>>>> "+map.toString());
		/*
		 * MultiValueMap<String, String> map = new LinkedMultiValueMap<String,
		 * String>(); map.add("key", "D4MK4S2MSO"); map.add("txnid",
		 * "1901202215021235"); map.add("amount", "1.00"); map.add("productinfo",
		 * "Testing"); map.add("firstname", "Sonu"); map.add("phone", "9767146866");
		 * map.add("email", "sonu@pay10.com"); map.add("surl",
		 * "http://uat.pay10.com/pgui/jsp/invoiceResponse"); map.add("furl",
		 * "http://uat.pay10.com/pgui/jsp/invoiceResponse"); map.add("hash",
		 * "7c7dc59fa2258bcb83d21bc36249ea4ccf8618dc5d57578c197b432d1516edb39a2e8ffcb5d2e5e3dcdf887fcd90889109227ca044b9a54037c43133a986ccca"
		 * );
		 */

		HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<MultiValueMap<String, String>>(map, headers);
		ResponseEntity<AccessTokenDTO> response = restTemplate.postForEntity(url, request, AccessTokenDTO.class);
		logger.info("response " + response.toString());
		return response.getBody();
	}
//
//	public String refundRequest(Fields fields, Transaction transaction) throws SystemException {
//		//txnid/refund_amount/phone/key/hash/amount/email
//		
//		
//			String reqfloat = acquirerTxnAmountProvider.amountProvider(fields);
//			String amount=easebuzzChecksumUtil.getfloatamoutforrefund(reqfloat);
//			String hashval=getrefundhash(fields,transaction,amount);
//
//			MultiValueMap<String, String> map = new LinkedMultiValueMap<String, String>();
//			map.add("txnid", fields.get(FieldType.RRN.getName()));
//			map.add("refund_amount",amount);
//			map.add("phone",fields.get(FieldType.CUST_PHONE.getName()));
//			map.add("key",fields.get(FieldType.MERCHANT_ID.getName()));
//			map.add("hash", hashval);
//			map.add("amount",amount);
//			map.add("email", fields.get(FieldType.CUST_EMAIL.getName()));
//
//
//			logger.info("map >>>>>>>>>>>>>>> "+map);
//			logger.info("transcation id "+ transaction.getPhone());
//			return map.toString();  
//		}
	

	public String refundRequest(Fields fields, Transaction transaction) throws SystemException {
		//txnid/refund_amount/phone/key/hash/amount/email
		String	toamount = Amount.toDecimal(fields.get(FieldType.TOTAL_AMOUNT.getName()), fields.get(FieldType.CURRENCY_CODE.getName()));

		String totalamount=easebuzzChecksumUtil.getfloatamoutforrefund(toamount);

			String reqfloat = acquirerTxnAmountProvider.amountProvider(fields);

			String amount=easebuzzChecksumUtil.getfloatamoutforrefund(reqfloat);
		
			String hashval=getrefundhash(fields,transaction,amount,totalamount);

		
			StringBuilder request = new StringBuilder();

			request.append("txnid=");
			request.append( fields.get(FieldType.RRN.getName()));
			request.append("&");
			request.append("refund_amount=");
			request.append(amount);
			request.append("&");
			request.append("phone=");
			request.append(fields.get(FieldType.CUST_PHONE.getName()));
			request.append("&");
			request.append("key=");
			request.append(fields.get(FieldType.MERCHANT_ID.getName()));
			request.append("&");
			request.append("hash=");
			request.append(hashval);
			request.append("&");
			request.append("amount=");
			request.append(totalamount);
			request.append("&");
			request.append("email=");
			request.append(fields.get(FieldType.CUST_EMAIL.getName()));
			logger.info("easebuzz>>>>>>>>>>>>>>>>>>>>>>>>>>"+request.toString());

			String post_data = request.toString();
			return post_data;



			
			 
		}
			
	public String getrefundhash(Fields fields,Transaction transaction,String amount,String totalamount) throws SystemException {

		try {
			//txnid/refund_amount/phone/key/hash/amount/email

			StringBuilder sb = new StringBuilder();
			sb.append(fields.get(FieldType.MERCHANT_ID.getName()) + "|");
			sb.append(fields.get(FieldType.RRN.getName()) + "|");
			sb.append(totalamount + "|");
			sb.append(amount + "|");
			 sb.append(fields.get(FieldType.CUST_EMAIL.getName()));
	            sb.append("|");
			 sb.append(fields.get(FieldType.CUST_PHONE.getName()));
	            sb.append("|");
           
            sb.append(fields.get(FieldType.ADF1.getName()));
            logger.info("checksumfeild"+sb.toString());
            String checkSum = easebuzzChecksumUtil.checksumForInitiateLink(sb.toString());
            logger.info("checksumfeild"+checkSum.toString());
            transaction.setHash(checkSum);
			return checkSum;
		}

		catch (Exception e) {
			logger.error("Exception in generating cashfree refund request", e);
			return null;
		}
	}


	public String statusEnquiryRequest(Fields fields, Transaction transaction) throws SystemException {

		StringBuilder sb = new StringBuilder();

		sb.append("key=" + fields.get(FieldType.MERCHANT_ID.getName()));
		sb.append("&");
		sb.append("txnid=" + fields.get(FieldType.TXN_KEY.getName()));
		sb.append("&");
		sb.append("amount=" + fields.get(FieldType.PG_REF_NUM.getName()));
		sb.append("&");
		sb.append("email=" + fields.get(FieldType.MERCHANT_ID.getName()));
		sb.append("&");
		sb.append("phone=" + fields.get(FieldType.TXN_KEY.getName()));
		sb.append("&");
		sb.append("salt=" + fields.get(FieldType.PG_REF_NUM.getName()));

		return sb.toString();

	}

	public MultiValueMap<String, String> doubleVerificationRequest(Transaction transaction) {

		MultiValueMap<String, String> map = new LinkedMultiValueMap<String, String>();
		map.add("key", transaction.getMerchantKey());
		map.add("txnid", transaction.getTxnid());
		map.add("amount", transaction.getAmount());
		map.add("phone", transaction.getPhone());
		map.add("email", transaction.getEmail());
		map.add("hash", transaction.getHash());

		return map;

	}

	public String checksumForDoubleVerification(Transaction transaction) {

		StringBuilder sb = new StringBuilder();
		//key|txnid|amount|email|phone|salt
		
		sb.append(transaction.getMerchantKey() +"|");
		sb.append(transaction.getTxnid()+"|");
		sb.append(transaction.getAmount()+"|");
		sb.append(transaction.getEmail()+"|");
		sb.append(transaction.getPhone()+"|");
		sb.append(transaction.getSalt());

		logger.info("checksumForDoubleVerification sb :: "+sb.toString());
		String checkSum = easebuzzChecksumUtil.checksumForInitiateLink(sb.toString());
		return checkSum;
	}

	public Transaction toTransaction(String response) {

		Transaction transaction = new Transaction();
		try {

			JSONObject resJson = new JSONObject(response);

			if (response.contains(Constants.txnid)) {
				transaction.setTxnid(resJson.get(Constants.txnid).toString());
			}

			if (response.contains(Constants.Amount)) {
				transaction.setAmount(resJson.get(Constants.Amount).toString());
			}

			if (response.contains(Constants.EasePayId)) {
				transaction.setReferenceId(resJson.get(Constants.EasePayId).toString());
			}

			if (response.contains(Constants.status)) {
				transaction.setStatus(resJson.get(Constants.status).toString());
			}

			if (response.contains(Constants.paymentMode)) {
				transaction.setPaymentMode(resJson.get(Constants.paymentMode).toString());
			}

			if (response.contains(Constants.ErrorMsg)) {
				transaction.setTxMsg(resJson.get(Constants.ErrorMsg).toString());
			}
			
			if (response.contains("error_Message")) {
				transaction.setTxMsg(resJson.get("error_Message").toString());
			}

		}

		catch (Exception e) {
			logger.error("Exception", e);
		}

		return transaction;
	}

	public void logRequest(String requestMessage, Fields fields) {
		log("Request message to Cashfree  : Url= " + requestMessage, fields);
	}

	private void log(String message, Fields fields) {
		message = Pattern.compile("(<CardNumber>)([\\s\\S]*?)(</card>)").matcher(message).replaceAll("$1$3");
		message = Pattern.compile("(<ExpiryDate>)([\\s\\S]*?)(</pan>)").matcher(message).replaceAll("$1$3");
		message = Pattern.compile("(<CardSecurityCode>)([\\s\\S]*?)(</expmonth>)").matcher(message).replaceAll("$1$3");
		message = Pattern.compile("(<TerminalId>)([\\s\\S]*?)(</expyear>)").matcher(message).replaceAll("$1$3");
		message = Pattern.compile("(<PassCode>)([\\s\\S]*?)(</cvv2>)").matcher(message).replaceAll("$1$3");
		// message =
		// Pattern.compile("(<password>)([\\s\\S]*?)(</password>)").matcher(message).replaceAll("$1$3");
		MDC.put(FieldType.INTERNAL_CUSTOM_MDC.getName(), fields.getCustomMDC());
		logger.info(message);
	}

	private void prepareSaleRequest(JsonObject jsonRequest, Fields fields, Transaction transaction, String accessKey) {

		if (fields.get(FieldType.PAYMENT_TYPE.getName()).equalsIgnoreCase(PaymentType.CREDIT_CARD.getCode())
				|| fields.get(FieldType.PAYMENT_TYPE.getName()).equalsIgnoreCase(PaymentType.DEBIT_CARD.getCode())) {

			logger.info("easebuzz Request before adding payment parameters  incase of cards " + jsonRequest.toString());

			String cardExpiryDateFormat = transaction.getCardExp();
			cardExpiryDateFormat = cardExpiryDateFormat.substring(0, 2) + "/" + cardExpiryDateFormat.substring(2);
			logger.info("cardExpiryDateFormat >>>>>>>>>>>>>> " + cardExpiryDateFormat);
			String cardNumber = easebuzzChecksumUtil.encrypt(transaction.getCardNo(), transaction.getEncryptionKey(), transaction.getIv());
			String card_cvv = easebuzzChecksumUtil.encrypt(transaction.getCvv(), transaction.getEncryptionKey(), transaction.getIv());
			String cardExpiryDate = easebuzzChecksumUtil.encrypt(cardExpiryDateFormat, transaction.getEncryptionKey(), transaction.getIv());
			String cardHolderName = easebuzzChecksumUtil.encrypt(transaction.getCardHname(), transaction.getEncryptionKey(), transaction.getIv());

			jsonRequest.addProperty("access_key", accessKey);
			jsonRequest.addProperty("payment_mode", transaction.getPaymentMode());
			jsonRequest.addProperty("card_holder_name", cardHolderName);
			jsonRequest.addProperty("card_number", cardNumber);
			jsonRequest.addProperty("card_expiry_date", cardExpiryDate);
			jsonRequest.addProperty("card_cvv", card_cvv);
			

		} else if (fields.get(FieldType.PAYMENT_TYPE.getName()).equalsIgnoreCase(PaymentType.UPI.getCode())) {
			
			jsonRequest.addProperty("access_key", accessKey);
			jsonRequest.addProperty("payment_mode", transaction.getPaymentMode());
			jsonRequest.addProperty("upi_va", transaction.getUpiVA());
			//jsonRequest.addProperty("request_mode", "SUVA");
			
			logger.info("Easebuzz Request for UPI " + jsonRequest.toString());

			fields.put(FieldType.CARD_MASK.getName(), fields.get(FieldType.PAYER_ADDRESS.getName()));

		} else if (fields.get(FieldType.PAYMENT_TYPE.getName()).equalsIgnoreCase(PaymentType.NET_BANKING.getCode())) {

			jsonRequest.addProperty("access_key", accessKey);
			jsonRequest.addProperty("payment_mode", transaction.getPaymentMode());
			jsonRequest.addProperty("bank_code", EasebuzzMopType.getBankCode(fields.get(FieldType.MOP_TYPE.getName())));
			
			logger.info("Easebuzz Request for NB " + jsonRequest.toString());

		} else if (fields.get(FieldType.PAYMENT_TYPE.getName()).equalsIgnoreCase(PaymentType.WALLET.getCode())) {

			jsonRequest.addProperty(Constants.paymentOption, Constants.paymentOptionWallet);
			jsonRequest.addProperty(Constants.paymentCode,
					EasebuzzMopType.getBankCode(fields.get(FieldType.MOP_TYPE.getName())));

			logger.info("eashbuzz Request for Wallet " + jsonRequest.toString());

		}

	}

	public Transaction toTransactionStatus(String response) {

		Transaction transaction = new Transaction();
		try {

			JSONObject resJson = new JSONObject(response);

			if (response.contains(Constants.orderStatus)) {
				transaction.setOrderStatus(resJson.get(Constants.orderStatus).toString());
			}

			if (response.contains(Constants.orderAmount)) {
				transaction.setOrderAmount(resJson.get(Constants.orderAmount).toString());
			}

			if (response.contains(Constants.status)) {
				transaction.setStatus(resJson.get(Constants.status).toString());
			}

			if (response.contains(Constants.txStatus)) {
				transaction.setTxStatus(resJson.get(Constants.txStatus).toString());
			}

			if (response.contains(Constants.txMsg)) {
				transaction.setTxMsg(resJson.get(Constants.txMsg).toString());
			}

			if (response.contains(Constants.referenceId)) {
				transaction.setReferenceId(resJson.get(Constants.referenceId).toString());
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

			if (response.contains(Constants.message)) {
				transaction.setMessage(resJson.get(Constants.message).toString());
			}

			if (response.contains(Constants.refundId)) {
				transaction.setRefundId(resJson.get(Constants.refundId).toString());
			}
			if (response.contains("reason")) {
				transaction.setMessage(resJson.get("reason").toString());
			}
			if (response.contains("refund_id")) {
				transaction.setAcqrefundid(resJson.get("refund_id").toString());
			}
			if (response.contains("error_msg")) {
				transaction.setError_Message(resJson.get("error_msg").toString());
			}
			if (response.contains("easebuzz_id")) {
				transaction.setACquirertxid(resJson.get("easebuzz_id").toString());
			}
			if (response.contains("refund_amount")) {
				transaction.setAmount(resJson.get("refund_amount").toString());
	
			}
			
			if (response.contains(Constants.status)) {
				transaction.setStatus(resJson.get(Constants.status).toString());
			}
			logger.info("**************************"+transaction.getStatus());
			logger.info("**************************"+transaction.getRefundId());
			logger.info("**************************"+transaction.getMessage());
			
		}
		catch (Exception e) {
			logger.error("Exception", e);
		}

		return transaction;
	}

	public static String encryptThisString(String input) {
		try {
			MessageDigest md = MessageDigest.getInstance("SHA-512");
			byte[] messageDigest = md.digest(input.getBytes());

			BigInteger no = new BigInteger(1, messageDigest);
			String hashtext = no.toString(16);

			while (hashtext.length() < 32) {
				hashtext = "0" + hashtext;
			}

			return hashtext;
		} catch (NoSuchAlgorithmException e) {
			throw new RuntimeException(e);
		}
	}

	// 22ba9bc8444c037c0963456ef15915120bff2e2afc4915e493696b4dee1519558dd1907a9c877e106f20ba45f18a53a3f7d469fcbe905bb8305d43e25a6f8654
	// 22ba9bc8444c037c0963456ef15915120bff2e2afc4915e493696b4dee1519558dd1907a9c877e106f20ba45f18a53a3f7d469fcbe905bb8305d43e25a6f8654
	public static void main(String[] args) {
		// System.out.println(encryptThisString("2PBP7IABZ2|1901202215021234|10.00|Testing|Sonu|sonu@pay10.com|||||||||||DAH88E3UWQ"));
//		String cardDate = "092025";
//		cardDate = cardDate.substring(0, 2) + "/" + cardDate.substring(2);
//
//		System.out.println(cardDate);

		// httpCall(new Transaction());
		
		String str = "1.00";
		String amount = String.valueOf(Math.round(Double.valueOf(str)) * 100);
		System.out.println(amount);
	}

}
