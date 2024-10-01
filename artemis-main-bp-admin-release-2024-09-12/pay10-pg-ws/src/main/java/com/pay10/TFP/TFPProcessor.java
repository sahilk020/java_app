package com.pay10.TFP;

import com.pay10.commons.api.Hasher;
import com.pay10.commons.exception.ErrorType;
import com.pay10.commons.exception.SystemException;
import com.pay10.commons.util.*;
import com.pay10.pg.core.util.AcquirerTxnAmountProvider;
import com.pay10.pg.core.util.Processor;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.net.ssl.HttpsURLConnection;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.TreeMap;

@Service("TFPProcessor")
public class TFPProcessor implements Processor {

	private static final Logger logger = LoggerFactory.getLogger(TFPProcessor.class.getName());
	@Autowired
	private AcquirerTxnAmountProvider acquirerTxnAmountProvider;

	@Autowired
	Hasher hasher;

	@Override
	public void preProcess(Fields fields) throws SystemException {

	}

	@Override
	public void process(Fields fields) throws SystemException {

		if ((fields.get(FieldType.TXNTYPE.getName()).equals(TransactionType.NEWORDER.getName())
				|| fields.get(FieldType.INTERNAL_ORIG_TXN_TYPE.getName()).equals(TransactionType.STATUS.getName())
				|| fields.get(FieldType.INTERNAL_ORIG_TXN_TYPE.getName()).equals(TransactionType.VERIFY.getName())
				|| fields.get(FieldType.INTERNAL_ORIG_TXN_TYPE.getName()).equals(TransactionType.RECO.getName())
				|| fields.get(FieldType.INTERNAL_ORIG_TXN_TYPE.getName())
						.equals(TransactionType.REFUNDRECO.getName()))) {
			return;
		}

		if (!fields.get(FieldType.ACQUIRER_TYPE.getName()).equals(AcquirerType.TFP.getCode())) {
			return;
		}
		try {
			saleRequest(fields);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void postProcess(Fields fields) throws SystemException {

	}

	public void saleRequest(Fields fields) throws Exception {
		String transactionUrl = PropertiesManager.propertiesMap.get("TFPSaleURL");
		String TFP_RETURN_URL = PropertiesManager.propertiesMap.get("TFP_RETURN_URL");
		if (fields.get(FieldType.TXNTYPE.getName()).equalsIgnoreCase(TransactionType.ENROLL.getName())) {
			fields.put(FieldType.TXNTYPE.getName(), TransactionType.SALE.getName());
		}
		JSONObject saleRequest = new JSONObject();

		String salt_key = fields.get(FieldType.ADF5.getName());
		String app_ip = fields.get(FieldType.ADF3.getName());
		String amount = fields.get(FieldType.TOTAL_AMOUNT.getName());
		String pgRefNo = fields.get(FieldType.PG_REF_NUM.getName());

		if (pgRefNo == null)
			fields.put(FieldType.PG_REF_NUM.getName(), fields.get(FieldType.TXN_ID.getName()));
		saleRequest.put("APP_ID", app_ip);
		saleRequest.put("MERCHANTNAME", fields.get(FieldType.MERCHANT_ID.getName()));
		saleRequest.put("ORDER_ID", fields.get(FieldType.PG_REF_NUM.getName()));
		saleRequest.put("TXNTYPE", fields.get(FieldType.TXNTYPE.getName()));
		saleRequest.put("CUST_NAME", fields.get(FieldType.CUST_NAME.getName()));
		saleRequest.put("CUST_FIRST_NAME", fields.get(FieldType.CUST_FIRST_NAME.getName()));
		saleRequest.put("CUST_LAST_NAME", fields.get(FieldType.CUST_LAST_NAME.getName()));
		saleRequest.put("CUST_STREET_ADDRESS1", fields.get(FieldType.CUST_STREET_ADDRESS1.getName()));
		saleRequest.put("CUST_CITY", fields.get(FieldType.CUST_CITY.getName()));
		saleRequest.put("CUST_STATE", fields.get(FieldType.CUST_STATE.getName()));
		saleRequest.put("CUST_COUNTRY", fields.get(FieldType.CUST_COUNTRY.getName()));
		saleRequest.put("CUST_ZIP", fields.get(FieldType.CUST_ZIP.getName()));
		saleRequest.put("CUST_PHONE", fields.get(FieldType.CUST_PHONE.getName()));
		saleRequest.put("CUST_EMAIL", fields.get(FieldType.CUST_EMAIL.getName()));
		saleRequest.put("PRODUCT_DESC", fields.get(FieldType.PRODUCT_DESC.getName()));
		saleRequest.put("AMOUNT", amount);
		saleRequest.put("CURRENCY_CODE", fields.get(FieldType.CURRENCY_CODE.getName()));
		saleRequest.put("PAYMENT_TYPE", fields.get(FieldType.PAYMENT_TYPE.getName()));
		saleRequest.put("CARD_NUMBER", fields.get(FieldType.CARD_NUMBER.getName()));
		saleRequest.put("CARD_EXP_DT", fields.get(FieldType.CARD_EXP_DT.getName()));
		saleRequest.put("CVV", fields.get(FieldType.CVV.getName()));
		saleRequest.put("RETURN_URL", TFP_RETURN_URL);
		saleRequest.put(FieldType.BROWSER_USER_AGENT.getName(),  fields.get(FieldType.BROWSER_USER_AGENT.getName()));
		saleRequest.put("BROWSER_LANGUAGE", fields.get(FieldType.BROWSER_LANG.getName()));
		saleRequest.put(FieldType.BROWSER_JAVA_ENABLED.getName(), fields.get(FieldType.BROWSER_JAVA_ENABLED.getName()));
		saleRequest.put(FieldType.BROWSER_COLOR_DEPTH.getName(), fields.get(FieldType.BROWSER_COLOR_DEPTH.getName()));
		saleRequest.put(FieldType.BROWSER_SCREEN_HEIGHT.getName(), fields.get(FieldType.BROWSER_SCREEN_HEIGHT.getName()));
		saleRequest.put(FieldType.BROWSER_SCREEN_WIDTH.getName(), fields.get(FieldType.BROWSER_SCREEN_WIDTH.getName()));
		saleRequest.put(FieldType.BROWSER_TZ.getName(), fields.get(FieldType.BROWSER_TZ.getName()));
		saleRequest.put(FieldType.BROWSER_ACCEPT_HEADER.getName(), fields.get(FieldType.BROWSER_ACCEPT_HEADER.getName()));
		logger.info("TFP saleRequest:{}", saleRequest);
		TreeMap<String, String> treeMap = new TreeMap<String, String>();
		for (String key : saleRequest.keySet()) {
			treeMap.put(key, saleRequest.getString(key));
			logger.info("Key: {}, Value: {}", key, saleRequest.get(key));
		}
		// Create TreeMap to store data
		// Iterate over JSON keys and values, and populate TreeMap
		StringBuilder allFields = new StringBuilder();
		for (String key : treeMap.keySet()) {
			allFields.append(ConfigurationConstants.FIELD_SEPARATOR.getValue());
			allFields.append(key);
			allFields.append(ConfigurationConstants.FIELD_EQUATOR.getValue());
			allFields.append(treeMap.get(key));
		}
		allFields.deleteCharAt(0);
		allFields.append(salt_key);

		logger.info("TFP transaction initiation request HASH params : {}", allFields);
		saleRequest.put("HASH", generateSHA512(allFields.toString()));
		logger.info("TFP transaction initiation request:  {}", saleRequest);
		String jsonResponse = callRESTApi(transactionUrl, saleRequest.toString(), "POST");
		logger.info("TFP transaction initiation response: {}", jsonResponse);
		JSONObject response = new JSONObject(jsonResponse);

		if ("000".equalsIgnoreCase(response.getString("RESPONSE_CODE"))
				&& "Enrolled".equalsIgnoreCase(response.getString("STATUS"))) {
			fields.put(FieldType.RRN.getName(), response.getString("TXN_ID"));

			fields.put(FieldType.STATUS.getName(), StatusType.SENT_TO_BANK.getName());
			fields.put(FieldType.RESPONSE_CODE.getName(), ErrorType.SUCCESS.getResponseCode());
			fields.put(FieldType.RESPONSE_MESSAGE.getName(), ErrorType.SUCCESS.getResponseMessage());
			capturedRequest(fields);
		} else {
			fields.put(FieldType.STATUS.getName(), StatusType.REJECTED.getName());
			fields.put(FieldType.RESPONSE_CODE.getName(), ErrorType.REJECTED.getResponseCode());
			fields.put(FieldType.RESPONSE_MESSAGE.getName(), ErrorType.REJECTED.getResponseMessage());
		}
	}

	private void capturedRequest(Fields fields) {
		String salt_key = fields.get(FieldType.ADF5.getName());

		JSONObject saleRequest = new JSONObject();

		saleRequest.put("APP_ID", fields.get(FieldType.ADF3.getName()));

		saleRequest.put("TXN_ID", fields.get(FieldType.RRN.getName()));
		logger.info("saleRequest" + saleRequest);
		TreeMap<String, String> treeMap = new TreeMap<String, String>();

		for (String key : saleRequest.keySet()) {

			treeMap.put(key, (String) saleRequest.getString(key));
		}

		StringBuilder allFields = new StringBuilder();
		for (String key : treeMap.keySet()) {
			allFields.append(ConfigurationConstants.FIELD_SEPARATOR.getValue());
			allFields.append(key);
			allFields.append(ConfigurationConstants.FIELD_EQUATOR.getValue());
			allFields.append(treeMap.get(key));
		}
		allFields.deleteCharAt(0);

		allFields.append(salt_key);
		logger.info("HASH DATA for CAPTURED TRANSACTION"+allFields);

		fields.put(FieldType.ACQ_ID.getName(), generateSHA512(allFields.toString()));

	}

	public String generateSHA512(String input) {
		try {
			// Create MessageDigest instance for SHA-512
			MessageDigest md = MessageDigest.getInstance("SHA-512");

			// Add input bytes to digest
			md.update(input.getBytes());

			// Get the hash bytes
			byte[] hashBytes = md.digest();

			// Convert byte array to a string of hexadecimal values
			StringBuilder sb = new StringBuilder();
			for (byte hashByte : hashBytes) {
				sb.append(Integer.toString((hashByte & 0xff) + 0x100, 16).substring(1));
			}

			return sb.toString().toUpperCase();
		} catch (NoSuchAlgorithmException e) {
			// Handle NoSuchAlgorithmException appropriately
			e.printStackTrace();
			return null;
		}
	}

	
	public static String callRESTApi(String apiUrl, String params, String method) {

		StringBuilder serverResponse = new StringBuilder();
		HttpsURLConnection connection = null;

		try {

			URL url = new URL(apiUrl);
			connection = (HttpsURLConnection) url.openConnection();
			connection.setRequestMethod(method);
			connection.setRequestProperty("Accept", "application/json");
			connection.setRequestProperty("Content-type", "application/json");
			connection.setRequestProperty("Content-Language", "en-US");
			connection.setDoOutput(true);
			OutputStream os = connection.getOutputStream();
			os.write(params.getBytes());
			os.flush();
			os.close();

			InputStream is = connection.getInputStream();
			BufferedReader rd = new BufferedReader(new InputStreamReader(is));
			String line;

			int code = (connection).getResponseCode();
			int firstDigitOfCode = Integer.parseInt(Integer.toString(code).substring(0, 1));
			if (firstDigitOfCode == 4 || firstDigitOfCode == 5) {
				logger.error("HTTP Response code of txn [TFP] :{}", code);
				throw new SystemException(ErrorType.ACUIRER_DOWN, "Network Exception with TFP" + apiUrl);
			}
			while ((line = rd.readLine()) != null) {
				serverResponse.append(line);

			}
			rd.close();

		} catch (Exception e) {
			logger.error("Exception in HTTP call TFP :{}", apiUrl, e);
		} finally {
			if (connection != null) {
				connection.disconnect();
			}
		}
		return serverResponse.toString();
	}

	public static void main(String[] args) {
		String URL = "https://sandbox.trustflowpay.com/pgui/services/paymentServices/initiate/payment";
		String reqData = "{ \"APP_ID\": \"1000221129001154\", \n" + "\"MERCHANTNAME\": \"Test Merchant\", \n"
				+ "\"ORDER_ID\": \"7773428492547592\", \n" + "\"TXNTYPE\": \"SALE\", \n"
				+ "\"CUST_NAME\": \"John Snow\", \n" + "\"CUST_FIRST_NAME\": \"John\", \n"
				+ "\"CUST_LAST_NAME\": \"Snow\", \n" + "\"CUST_STREET_ADDRESS1\": \"House No 123 Street No42\", \n"
				+ "\"CUST_CITY\": \"Las Vegas\", \n" + "\"CUST_STATE\": \"Nevada\", \n" + "\"CUST_COUNTRY\": \"US\", \n"
				+ "\"CUST_ZIP\": \"88901\", \n" + "\"CUST_PHONE\": \"9454243567\", \n"
				+ "\"CUST_EMAIL\": \"johnsnow@test.com\", \n" + "\"PRODUCT_DESC\": \"Iphone 14 Pro Max 256 GB\", \n"
				+ "\"AMOUNT\": \"119900\", \n" + "\"CURRENCY_CODE\": \"840\", \n" + "\"PAYMENT_TYPE\": \"CC\", \n"
				+ "\"CARD_NUMBER\": \"4111110000000211\", \n" + "\"CARD_EXP_DT\": \"122030\", \n"
				+ "\"CVV\": \"123\", \n" + "\"RETURN_URL\": \"https://www.merchantname.com/response.jsp\", \n"
				+ "\"HASH\": \"8B39ECAEEF6008954C2FFBB163DD2E7132E5625D37409D0B6A6C5A51DCA580DE\"}";
		JSONObject jsonObject = new JSONObject(reqData);

		System.out.println("jsonObject " + jsonObject);
		String res = callRESTApi(URL, jsonObject.toString(), "POST");
		System.out.println(res);

	}
}
