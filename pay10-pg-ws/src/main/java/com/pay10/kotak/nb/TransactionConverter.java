package com.pay10.kotak.nb;

import java.io.UnsupportedEncodingException;
import java.security.GeneralSecurityException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.pay10.commons.exception.SystemException;
import com.pay10.commons.util.FieldType;
import com.pay10.commons.util.Fields;
import com.pay10.commons.util.PropertiesManager;
import com.pay10.commons.util.TransactionType;
import com.pay10.pg.core.util.AcquirerTxnAmountProvider;
import com.pay10.pg.core.util.KotakNBUtils;
import com.pay10.pg.core.util.refundkotaknb;

@Service("kotakNBTransactionConverter")
public class TransactionConverter {

	@Autowired
	RestTemplate restTemplate ;

	private static Logger logger = LoggerFactory.getLogger(TransactionConverter.class.getName());

	@Autowired
	private AcquirerTxnAmountProvider acquirerTxnAmountProvider;

	@Autowired
	@Qualifier("kotakNBUtils")
	private KotakNBUtils kotakNBUtils;

	@SuppressWarnings("incomplete-switch")
	public String perpareRequest(Fields fields, Transaction transaction) throws SystemException {

		String request = null;

		switch (TransactionType.getInstance(fields.get(FieldType.TXNTYPE.getName()))) {
		case REFUND:
			 request = refundRequest(fields, transaction);
			break;
		case SALE:
			request = saleRequest(fields, transaction);
			break;
		case ENQUIRY:
		request = statusEnquiryRequest1(fields, transaction);
			break;
		}
		return request;

	}

	public String saleRequest(Fields fields, Transaction transaction) throws SystemException {

		try {
			String amount = acquirerTxnAmountProvider.amountProvider(fields);
			String merchantCode = fields.get(FieldType.MERCHANT_ID.getName());
			String subMerchant = fields.get(FieldType.ADF1.getName());
			String encryptKey = fields.get(FieldType.TXN_KEY.getName());
			String checksumKey = fields.get(FieldType.ADF2.getName());
			
			String requestDate = getRequestDateFormat();

			StringBuilder paymentStringBuilder = new StringBuilder();

			paymentStringBuilder.append("0500");
			paymentStringBuilder.append("|");
			paymentStringBuilder.append(requestDate);
			paymentStringBuilder.append("|");
			paymentStringBuilder.append(merchantCode);
			paymentStringBuilder.append("|");
			paymentStringBuilder.append(fields.get(FieldType.PG_REF_NUM.getName()));
			paymentStringBuilder.append("|");
			paymentStringBuilder.append(amount); //fields.get(FieldType.AMOUNT.getName())
			paymentStringBuilder.append("|");
			paymentStringBuilder.append(subMerchant);
			paymentStringBuilder.append("|");
			paymentStringBuilder.append("|");
	
			paymentStringBuilder.append("|");
			paymentStringBuilder.append(fields.get(FieldType.ADF11.getName()));

			logger.info("Kotak NetBanking Sale Request : " + paymentStringBuilder.toString());
			//String checksum = kotakNBUtils.encodeWithHMACSHA2(paymentStringBuilder.toString(), checksumKey);
			//String checksum = kotakNBUtils.calculateHMAC(paymentStringBuilder.toString(), checksumKey);
			logger.info("checksum fo sale "+paymentStringBuilder);
			String checksum = kotakNBUtils.getHMAC256Checksum(paymentStringBuilder.toString(), checksumKey);
	

			paymentStringBuilder.append("|");
			paymentStringBuilder.append(checksum);
			
			logger.info("Kotak NetBanking Sale Request with checksum : " + paymentStringBuilder.toString()+"             "+encryptKey);
			String encData = kotakNBUtils.encrypt(paymentStringBuilder.toString(), encryptKey); // publicKeyPath
			System.out.println("Kotak NetBanking Sale Request with encData "+encData);
			return encData;

		}

		catch (Exception e) {
			logger.error("Exception in generating ATOM request ", e);
		}

		return null;
	}
	
	
	
	
	

	public String refundRequest(Fields fields, Transaction transaction) throws SystemException {
		String encData = null;

		try {
			
			//Message Code/Date and Time /Merchant Id/Trace number/Original Bank Ref No./Amount/Future1/Future2/Checksum

			
			String amount = acquirerTxnAmountProvider.amountProvider(fields);
			String merchantCode = fields.get(FieldType.MERCHANT_ID.getName());
			String subMerchant = fields.get(FieldType.ADF1.getName());
		//	String encryptKey = fields.get(FieldType.TXN_KEY.getName());
			String encryptKey=fields.get(FieldType.ADF3.getName());
			logger.info("checksum"+encryptKey);
			
			
			
			//"211995C8985ABA0F291E1941608FEFBD";
			String checksumKey = fields.get(FieldType.ADF2.getName());   
			
			String requestDate = getRequestDateFormat();

			StringBuilder paymentStringBuilder = new StringBuilder();

			paymentStringBuilder.append("0050");
			paymentStringBuilder.append("|");
			paymentStringBuilder.append(requestDate);
			paymentStringBuilder.append("|");
			paymentStringBuilder.append(merchantCode);
			paymentStringBuilder.append("|");
			paymentStringBuilder.append(fields.get(FieldType.REFUND_ORDER_ID.getName()));
			paymentStringBuilder.append("|");
			paymentStringBuilder.append(fields.get(FieldType.ACQ_ID.getName()));
			paymentStringBuilder.append("|");
			paymentStringBuilder.append(amount); //fields.get(FieldType.AMOUNT.getName())
			paymentStringBuilder.append("|");
			paymentStringBuilder.append("|");
			paymentStringBuilder.append("|");
			

			
			logger.info("Kotak NetBanking refund Request : " + paymentStringBuilder.toString());
			//String checksum = kotakNBUtils.encodeWithHMACSHA2(paymentStringBuilder.toString(), checksumKey);
			//String checksum = kotakNBUtils.calculateHMAC(paymentStringBuilder.toString(), checksumKey);
			String checksum = kotakNBUtils.getHMAC256Checksum(paymentStringBuilder.toString(), checksumKey);
			String data =checksum.toLowerCase();
			
			paymentStringBuilder.append(data);
			
			logger.info("Kotak NetBanking refund Request with checksum : " + paymentStringBuilder.toString()+"    "+encryptKey);

			 encData = refundkotaknb.encrypt(paymentStringBuilder.toString(), encryptKey); // publicKeyPath

			System.out.println("Kotak NetBanking refund Request with encData "+encData);
			return encData;
		}

		catch (Exception e) {
			logger.error("Exception in generating ATOM request ", e);
		}

		return encData;
	}
	


	public String perpareHeader(Fields fields, JSONObject req) throws Exception {

		String header = "";
		//header = getRequestHeader(fields, req);
		return header;

	}

	public String perpareRefundHeader(Fields fields, JSONObject req) throws Exception {

		String header = "";
		//header = getRequestHeader(fields, req);
		return header;

	}

	public String authoriseRequest(Fields fields) {

		String clientCredential = PropertiesManager.propertiesMap.get(Constants.CLIENTCREDENTIAL);
		String clientId = fields.get(FieldType.ADF5.getName());
		String clientsecret = fields.get(FieldType.ADF6.getName());

		StringBuilder request = new StringBuilder();

		request.append(Constants.GRANDTYPE);
		request.append(Constants.EQUATOR);
		request.append(clientCredential);
		request.append(Constants.SEPARATOR);
		request.append(Constants.CLIENTID);
		request.append(Constants.EQUATOR);
		request.append(clientId);
		request.append(Constants.SEPARATOR);
		request.append(Constants.CLIENTSECRET);
		request.append(Constants.EQUATOR);
		request.append(clientsecret);

		return request.toString();

	}

	public JSONObject vpaValidatorRequest(Fields fields) {

		String CustomerId = fields.get(FieldType.ADF8.getName());
		String aggregatorId = fields.get(FieldType.ADF9.getName());
		String merchantId = fields.get(FieldType.ADF10.getName());
		String payerAddress = fields.get(FieldType.PAYER_ADDRESS.getName());

		JSONObject json = new JSONObject();
		json.put(Constants.AGGREGATORID, aggregatorId);
		json.put(Constants.CUSTOMERID, CustomerId);
		json.put(Constants.MERCHANTID, merchantId);
		json.put(Constants.VPA, payerAddress);

		return json;

	}
	
	public String statusEnquiryRequest1(Fields fields, Transaction transaction) throws SystemException {
		String encData = null;

		try {
			
			String amount = acquirerTxnAmountProvider.amountProvider(fields);
			String merchantCode = fields.get(FieldType.MERCHANT_ID.getName());
			String subMerchant = fields.get(FieldType.ADF1.getName());
		//	String encryptKey = fields.get(FieldType.TXN_KEY.getName());
			String encryptKey=fields.get(FieldType.ADF3.getName());
			logger.info("checksum"+encryptKey);
			
			
			
			//"211995C8985ABA0F291E1941608FEFBD";
			String checksumKey = fields.get(FieldType.ADF2.getName());   
			
			String requestDate = getRequestDateFormat();

			StringBuilder paymentStringBuilder = new StringBuilder();

			paymentStringBuilder.append("0052");
			paymentStringBuilder.append("|");
			paymentStringBuilder.append(requestDate);
			paymentStringBuilder.append("|");
			paymentStringBuilder.append(merchantCode);
			paymentStringBuilder.append("|");
			paymentStringBuilder.append(fields.get(FieldType.PG_REF_NUM.getName()));
			paymentStringBuilder.append("|");
			paymentStringBuilder.append("|");
			paymentStringBuilder.append("|");
			

			
			logger.info("Kotak NetBanking refund Request : " + paymentStringBuilder.toString());
			//String checksum = kotakNBUtils.encodeWithHMACSHA2(paymentStringBuilder.toString(), checksumKey);
			//String checksum = kotakNBUtils.calculateHMAC(paymentStringBuilder.toString(), checksumKey);
			String checksum = kotakNBUtils.getHMAC256Checksum(paymentStringBuilder.toString(), checksumKey);
			String data =checksum.toLowerCase();
			
			paymentStringBuilder.append(data);
			
			logger.info("Kotak NetBanking refund Request with checksum : " + paymentStringBuilder.toString()+"    "+encryptKey);

			 encData = refundkotaknb.encrypt(paymentStringBuilder.toString(), encryptKey); // publicKeyPath

			System.out.println("Kotak NetBanking refund Request with encData "+encData);
			return encData;
		}

		catch (Exception e) {
			logger.error("Exception in generating ATOM request ", e);
		}

		return encData;
	}
	


	public JSONObject collectRequest(Fields fields, Transaction transaction) throws SystemException {
		// Transaction transaction = new Transaction();

		String amount = acquirerTxnAmountProvider.amountProvider(fields);
		String expiryTime = PropertiesManager.propertiesMap.get(Constants.EXPIRYTIME);
		String CustomerId = fields.get(FieldType.ADF8.getName());
		String merchantRefCode = fields.get(FieldType.ADF10.getName());
		String prefix = "KMBMIRCT";

		DateFormat currentDate = new SimpleDateFormat(Constants.DATEFORMAT);
		Calendar calobj = Calendar.getInstance();
		String timeStamp = currentDate.format(calobj.getTime());
		JSONObject json = new JSONObject();

		json.put(Constants.AGGREGATORVPA, fields.get(FieldType.ADF11.getName()));
		json.put(Constants.AMT, amount);
		json.put(Constants.CUSTOMERID, CustomerId);
		json.put(Constants.EXPIRY, expiryTime);
		json.put(Constants.MRC, merchantRefCode);
		json.put(Constants.ORDERID, fields.get(FieldType.ORDER_ID.getName()));
		json.put(Constants.PAYERVPA, fields.get(FieldType.PAYER_ADDRESS.getName()));
		json.put(Constants.REMARKS, Constants.TRNSINITIATED);
		json.put(Constants.TIMESTAMP, timeStamp);
		json.put(Constants.TXNID, prefix + fields.get(FieldType.PG_REF_NUM.getName()));

		JSONObject deviceDetails = new JSONObject();
		deviceDetails.put(Constants.MOBILE, fields.get(FieldType.ADF8.getName()));
		deviceDetails.put(Constants.APP, PropertiesManager.propertiesMap.get(Constants.APPVALUE));

		json.put(Constants.DEVICE, deviceDetails);

		return json;

	}

	/*
	 * public String getRequestHeader(Fields fields, JSONObject req) throws
	 * SystemException { String amount =
	 * acquirerTxnAmountProvider.amountProvider(fields); String expiryTime =
	 * PropertiesManager.propertiesMap.get(Constants.EXPIRYTIME); String CustomerId
	 * = fields.get(FieldType.ADF8.getName()); String merchantRefCode =
	 * fields.get(FieldType.ADF10.getName()); String prefix = "KMBMIRCT"; String
	 * timeStamp = req.getString("timeStamp");
	 * 
	 * StringBuilder request = new StringBuilder();
	 * request.append(fields.get(FieldType.ADF11.getName()));
	 * request.append(amount); request.append(CustomerId);
	 * request.append(PropertiesManager.propertiesMap.get(Constants.APPVALUE));
	 * request.append(fields.get(FieldType.ADF8.getName()));
	 * request.append(expiryTime); request.append(timeStamp);
	 * request.append(merchantRefCode);
	 * request.append(fields.get(FieldType.PAYER_ADDRESS.getName()));
	 * request.append(fields.get(FieldType.ORDER_ID.getName()));
	 * request.append(Constants.TRNSINITIATED); request.append(prefix +
	 * fields.get(FieldType.PG_REF_NUM.getName()));
	 * 
	 * String checkSumval = kotakUpiUtil.CheckSum(request.toString(), fields);
	 * return checkSumval;
	 * 
	 * }
	 */

	public String getRequestRefundHeader(Fields fields, JSONObject req) throws SystemException {

		return null;
	}

	public String payRequest(Fields fields, Transaction transaction) throws SystemException {
		String amount = acquirerTxnAmountProvider.amountProvider(fields);
		String CustomerId = fields.get(FieldType.ADF5.getName());

		JSONObject json = new JSONObject();
		json.put(Constants.AMT, amount);
		json.put(Constants.CUSTOMERID, CustomerId);
		json.put(Constants.ACCHN, fields.get(FieldType.ADF5.getName()));
		json.put(Constants.ACCREFN, fields.get(FieldType.ADF5.getName()));
		json.put(Constants.ACCTYPE, fields.get(FieldType.ADF5.getName()));
		json.put(Constants.PAYERVPA, fields.get(FieldType.ADF5.getName()));
		json.put(Constants.REMARKS, fields.get(FieldType.ADF5.getName()));
		json.put(Constants.TXNID, fields.get(FieldType.TXN_ID.getName()));
		json.put(Constants.PAYEEVPA, fields.get(FieldType.ADF5.getName()));
		json.put(Constants.MCC, fields.get(FieldType.ADF5.getName()));
		json.put(Constants.IFSC, fields.get(FieldType.ADF5.getName()));
		json.put(Constants.MERCHANTREFID, fields.get(FieldType.ADF5.getName()));
		json.put(Constants.DEVICEDETAILS, fields.get(FieldType.ADF5.getName()));
		json.put(Constants.APPROVALREF, fields.get(FieldType.ADF5.getName()));
		json.put(Constants.CUSTOMEREFID, fields.get(FieldType.ADF5.getName()));
		json.put(Constants.APP, fields.get(FieldType.ADF5.getName()));
		json.put(Constants.CAPABILITY, fields.get(FieldType.ADF5.getName()));
		json.put(Constants.GCMID, fields.get(FieldType.ADF5.getName()));
		json.put(Constants.GEOCODE, fields.get(FieldType.ADF5.getName()));
		json.put(Constants.ID, fields.get(FieldType.ADF5.getName()));
		json.put(Constants.IP, fields.get(FieldType.ADF5.getName()));
		json.put(Constants.LOCATION, fields.get(FieldType.ADF5.getName()));
		json.put(Constants.MOBILE, fields.get(FieldType.ADF5.getName()));
		json.put(Constants.OS, fields.get(FieldType.ADF5.getName()));
		json.put(Constants.TYPE, fields.get(FieldType.ADF5.getName()));
		return json.toString();
	}

	public String statusEnquiryRequest(Fields fields) {

		
		return null;
	}

	/*
	 * public static byte[] SHA256(String paramString) throws Exception {
	 * MessageDigest localMessageDigest = MessageDigest.getInstance("SHA-256");
	 * localMessageDigest.update(paramString.getBytes("UTF-8")); byte[] digest =
	 * localMessageDigest.digest(); return digest; }
	 * 
	 * public static byte[] hexStringToByteArray(String s) { byte[] b = new
	 * byte[s.length() / 2]; for (int i = 0; i < b.length; i++) { int index = i * 2;
	 * int v = Integer.parseInt(s.substring(index, index + 2), 16); b[i] = (byte) v;
	 * } return b; }
	 * 
	 * public static byte[] encrypt(byte[] key, byte[] checkSum) throws Exception {
	 * System.out.println(">>>>>>>>>KEY::" + key.length); SecretKeySpec
	 * secretKeySpec = new SecretKeySpec(key, "AES"); byte[] iv = new byte[16];
	 * IvParameterSpec ivSpec = new IvParameterSpec(iv); Cipher acipher =
	 * Cipher.getInstance("AES/CBC/PKCS5Padding"); acipher.init(Cipher.ENCRYPT_MODE,
	 * secretKeySpec, ivSpec); byte[] arrayOfByte1 = acipher.doFinal(checkSum);
	 * return arrayOfByte1; }
	 */

	public Transaction toTransactionForCollect(JSONObject response) {

		Transaction transaction = new Transaction();
		Map<String, String> responseMap = new HashMap<String, String>();

		JSONObject RespValAddObject = new JSONObject();
		RespValAddObject = response.getJSONObject("");

		for (Object key : RespValAddObject.keySet()) {

			String key1 = key.toString();
			String value = RespValAddObject.get(key.toString()).toString();
			responseMap.put(key1, value);

		}
		transaction.setResponse(responseMap.get(Constants.SUCCESS_RESPONSE_MSG));
		// transaction.setResponseCode(responseMap.get(Constants.SUCCESS_RESPONSE));

		return transaction;

	}

	public static String getRequestDateFormat() {
		Date dNow = new Date();
		SimpleDateFormat ft = new SimpleDateFormat("DDMMYYYYHH");
		return ft.format(dNow);

	}

	public String getRequestHourFormat() {
		Date dNow = new Date();
		SimpleDateFormat hour = new SimpleDateFormat("HH");
		return hour.format(dNow);

	}

	public String getRequestYearFormat() {
		Date dNow = new Date();
		SimpleDateFormat year = new SimpleDateFormat("YY");
		return year.format(dNow);

	}

	public String getRequestMinuteFormat() {
		Date dNow = new Date();
		SimpleDateFormat minute = new SimpleDateFormat("mm");
		return minute.format(dNow);

	}

	public int randomNumber() {
		Random rand = new Random();
		return rand.nextInt(9000000) + 1000000;
	}
//	
//	public  AccessTokenDTO httpCall() {
//
//		logger.info("Request Received");
//		
//		String url = "https://apigwuat.kotak.com:8443/k2/auth/oauth/v2/token";
//		HttpHeaders headers = new HttpHeaders();
//		headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
//
//		MultiValueMap<String, String> map = new LinkedMultiValueMap<String, String>();
//		map.add("grant_type", "client_credentials");
//		map.add("client_id","3b4c289e-c52c-4d77-a59c-34f6357f431f");
//		map.add("client_secret","119c9fb7-1c1d-4ac1-a4b8-4a4fe64693ed");
//	logger.info("map >>>>>>>>>>>>>>> "+map.toString());
//		HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<MultiValueMap<String, String>>(map, headers);
//		System.out.println(restTemplate);
//		ResponseEntity<AccessTokenDTO> response = restTemplate.postForEntity(url, request, AccessTokenDTO.class);
//		System.out.println("response " + response.toString());
//		System.out.println("response " +response.getBody());
//		
//		return response.getBody();
//	}
	public Transaction toTransactionRefund(String response, Fields fields)  {
		String encryptKey =fields.get(FieldType.ADF3.getName());                  //"211995C8985ABA0F291E1941608FEFBD";
	String encData;
	Transaction transaction = new Transaction();
	try {
		encData = refundkotaknb.decrypt(response, encryptKey);
	
	logger.info("encData"+encData);
		String s1=encData;  
		String[] words=s1.split("\\|");   //"  0051|07032022181903|OSENAM|1009120307111447||1.00|N|KPY209|dd1dcf73e29ef61608abb96c762bf1babd2769cb20eb140e3d2008b6010078f1"
		logger.info(words[3]);
	//	Transaction transaction = new Transaction();
	//	Message Code/Date and Time/Merchant Id/Trace number/Bank Reference No/Amount/Auth Status/Error code/Checksum
		transaction.setAuth_code(words[0]);
		transaction.setDateAndTime(words[1]);
		transaction.setMerchantId(words[2]);
		transaction.setTransactionId(words[3]);
		transaction.setBankReferenceNumber(words[4]);
		transaction.setAmount(words[5]);
		transaction.setAuthStatus(words[6]);
		transaction.setStatus(words[7]);
		transaction.setChecksum(words[8]);
		return transaction;
}
	catch (UnsupportedEncodingException | GeneralSecurityException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
	return transaction;}
	

	
}
