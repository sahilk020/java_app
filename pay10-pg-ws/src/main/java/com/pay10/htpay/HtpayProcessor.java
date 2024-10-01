package com.pay10.htpay;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.rmi.server.UID;
import java.text.DecimalFormat;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import javax.net.ssl.HttpsURLConnection;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.pay10.commons.exception.ErrorType;
import com.pay10.commons.exception.SystemException;
import com.pay10.commons.user.QuomoCurrencyConfiguration;
import com.pay10.commons.user.UserDao;
import com.pay10.commons.util.AcquirerType;
import com.pay10.commons.util.FieldType;
import com.pay10.commons.util.Fields;
import com.pay10.commons.util.PropertiesManager;
import com.pay10.commons.util.StatusType;
import com.pay10.commons.util.TransactionType;
import com.pay10.pg.core.util.Processor;

@Service("htpayProcessor")
public class HtpayProcessor implements Processor {
	private static final Logger logger = LoggerFactory.getLogger(HtpayProcessor.class.getName());
	private static final DecimalFormat df = new DecimalFormat("#.00");

	@Autowired
	UserDao userDao;

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

		if (!fields.get(FieldType.ACQUIRER_TYPE.getName()).equals(AcquirerType.HTPAY.getCode())) {
			return;
		}
		try {
			send(fields);
		} catch (Exception e) {
			e.printStackTrace();
			logger.info("Exception occured in HtpayProcessor, send() " + e);
		}

	}
	


	@Override
	public void postProcess(Fields fields) throws SystemException {

	}

	public void send(Fields fields) throws Exception {
		logger.info("Request received for sale qequest for HTPAY , OrderId={}, fields={}",
				fields.get(FieldType.ORDER_ID.getName()), fields.getFieldsAsString());
		String pgRefNo = fields.get(FieldType.PG_REF_NUM.getName());
		if (pgRefNo == null) {
			fields.put(FieldType.PG_REF_NUM.getName(), fields.get(FieldType.TXN_ID.getName()));
		}

		if (fields.get(FieldType.TXNTYPE.getName()).equalsIgnoreCase(TransactionType.ENROLL.getName())) {
			fields.put(FieldType.TXNTYPE.getName(), TransactionType.SALE.getName());
		}

		String txnType = fields.get(FieldType.TXNTYPE.getName());

		if (txnType.equals(TransactionType.SALE.getName())) {
			fields.put(FieldType.ORIG_TXN_ID.getName(), fields.get(FieldType.TXN_ID.getName()));
			String request = saleRequest(fields);
			logger.info("Quomo Sale Request={}, fields {}", request, fields);
			updateSaleResponse(fields, request);
		}
	}
	
	public static void main(String[] args) {
		String  dat1a="http://gateway.tgapg.com:9909/api/v2/pay/placeOrder";	
			HtpayProcessor data =new HtpayProcessor();
//			String key ="amount=1000&clientip=136.232.148.174&currency=VND&mhtorderno=1702558659081&mhtuserid=BTSE&notifyurl=https://bpgate.nxtpay.in/pgui/jsp/HtpayNotifyResponse&opmhtid=BTSE&payername=abhishek&paytype=vnbank&random=3a75a91618c6867720a-8000&returnurl=https://bpgate.nxtpay.in/pgui/jsp/HtpayResponse&sign=5853ed6a34c9860040f250573bbac74bac9c12755371a0dd81878b9d733d84e8fb6dbc3e9b527855373b2c07692d7246";
		String Key="amount=10000&clientip=136.232.148.174&currency=VND&mhtorderno=1702559789865&mhtuserid=BTSE&notifyurl=https://bpgate.nxtpay.in/pgui/jsp/HtpayNotifyResponse&opmhtid=BTSE&payername=yy&paytype=vnbank&random=3a75a91618c6867720a-7ffe&returnurl=https://bpgate.nxtpay.in/pgui/jsp/HtpayResponse&sign=cf02c68c329e747a8dfef1de57c38a1a80083f799cb37a8190698ceebfbb750d7df110793b9f11ef532829ba8335fe5a";
			System.out.println(data.postData( Key,dat1a	));
		 
			}	

	private String saleRequest(Fields fields) throws SystemException {
		String notifyUrl = PropertiesManager.propertiesMap.get("HtpayNotifyUrl");
		String returnUrl = PropertiesManager.propertiesMap.get("HtpayReturnUrl");
		String SaleUrl = PropertiesManager.propertiesMap.get("HtpaySaleUrl");



		String paymentType = fields.get(FieldType.PAYMENT_TYPE.getName());
		String mopType = fields.get(FieldType.MOP_TYPE.getName());
		String currency = fields.get(FieldType.CURRENCY_CODE.getName());
		String acquirer = fields.get(FieldType.ACQUIRER_TYPE.getName());
		logger.info("HTPAY saleRequest, acquirer={}, currency={}, paymentType={}, mopType={}", acquirer, currency,
				paymentType, mopType);
		QuomoCurrencyConfiguration quomoCurrencyConfiguration = userDao.getQuomoCurrencyConfiguration(acquirer,
				paymentType, mopType, currency);
		logger.info("CurrencyConfiguration " + quomoCurrencyConfiguration.toString());

		String amount = df.format(Double.valueOf(fields.get(FieldType.TOTAL_AMOUNT.getName())) / 100);
		if (amount.contains(".00")) {
			amount = amount.split("\\.")[0];
		}
		logger.info("HTPAY saleRequest, amount : " + amount);

		if (quomoCurrencyConfiguration.getAcquirer() == null) {
			fields.put(FieldType.STATUS.getName(), StatusType.INVALID.getName());
			fields.put(FieldType.PG_TXN_MESSAGE.getName(), "Currency not supported!!");
			logger.error("This currency not mapped with merchant, currnecy code = " + currency);
			throw new SystemException(ErrorType.CURRENCY_NOT_SUPPORTED,
					"Merchant not supported for this currency type payId= " + fields.get(FieldType.PAY_ID.getName()));

		}

		HTPayRequestParams params = new HTPayRequestParams();
		params.setAmount(fields.get(FieldType.TOTAL_AMOUNT.getName()));
		params.setClientip(fields.get(FieldType.INTERNAL_CUST_IP.getName()) != null ? fields.get(FieldType.INTERNAL_CUST_IP.getName()) : "136.232.148.174");
		
		params.setCurrency(quomoCurrencyConfiguration.getCurrency());
		params.setMhtorderno(System.currentTimeMillis() + "");
		params.setMhtuserid(fields.get(FieldType.ADF1.getName()));
		params.setNotifyurl(notifyUrl);
		params.setOpmhtid(fields.get(FieldType.ADF3.getName()));
		params.setPayername(fields.get(FieldType.CUST_NAME.getName()));
		params.setPayerphone(fields.get(FieldType.CUST_PHONE.getName()) != null ? fields.get(FieldType.CUST_PHONE.getName()) : "9988776655");
		params.setPaytype(quomoCurrencyConfiguration.getBankId());
		params.setRandom((new UID() + "").replaceAll(":", ""));
		//params.setAccno("rahul_2031@hdfcbank");
		params.setReturnurl(returnUrl);
		String s = prepareRequest(params,fields);
		logger.info("Request :: " + s);
		logger.info("SaleUrl :: " + SaleUrl);

		s = postData(s, SaleUrl);
		logger.info("Response :: " + s);
		JSONObject resp;
		resp = new JSONObject(s);
		logger.info("Josn Response: " + resp);
		String result = resp.get("result").toString();
		logger.info("result " + result);
		JSONObject innerresp = new JSONObject(result);
		logger.info("rtCode " + resp.getInt("rtCode"));
		logger.info("innerresp " + innerresp);
		String postUrl = innerresp.has("payurl") ? innerresp.getString("payurl") : "NA";
		logger.info("postUrl " + postUrl);
		return postUrl;

	
	}

	public void updateSaleResponse(Fields fields, String request) {

		fields.put(FieldType.HTPAY_FINAL_REQUEST.getName(), request);
		fields.put(FieldType.STATUS.getName(), StatusType.SENT_TO_BANK.getName());
		fields.put(FieldType.RESPONSE_CODE.getName(), ErrorType.SUCCESS.getResponseCode());
		fields.put(FieldType.RESPONSE_MESSAGE.getName(), ErrorType.SUCCESS.getResponseMessage());

	}

	public void updateFailedSaleResponse(Fields fields, String respMsg) {
		fields.put(FieldType.STATUS.getName(), StatusType.FAILED.getName());
		fields.put(FieldType.RESPONSE_CODE.getName(), ErrorType.ACUIRER_DOWN.getResponseCode());
		if (StringUtils.isNotBlank(respMsg)) {
			fields.put(FieldType.PG_TXN_MESSAGE.getName(), respMsg);
		} else {
			fields.put(FieldType.PG_TXN_MESSAGE.getName(), ErrorType.ACUIRER_DOWN.getResponseMessage());
		}

	}

	public static String prepareRequest(HTPayRequestParams params ,Fields fields) {
		StringBuilder sb = new StringBuilder();
		sb.append("amount=" + params.getAmount());
		sb.append("&clientip=" + params.getClientip());
		sb.append("&currency=" + params.getCurrency());
		sb.append("&mhtorderno=" + params.getMhtorderno());
		sb.append("&mhtuserid=" + params.getMhtuserid());
		sb.append("&notifyurl=" + params.getNotifyurl());
		sb.append("&opmhtid=" + params.getOpmhtid());
		sb.append("&payername=" + params.getPayername());
		sb.append("&payerphone="+params.getPayerphone());
		sb.append("&paytype=" + params.getPaytype());
		sb.append("&random=" + params.getRandom());
		sb.append("&returnurl=" + params.getReturnurl());
		
		String s = null;
		try {
			s = sb.toString() + "&sign=" + generateSignature(sb.toString(), fields.get(FieldType.ADF2.getName()));
		} catch (Exception e) {

			e.printStackTrace();
		}
		return s;
	}

	public static String generateSignature(String request, String key) throws Exception {

		SecretKeySpec signingKey = new SecretKeySpec(key.getBytes(), "HmacSHA384");
		Mac mac = Mac.getInstance("HmacSHA384");
		String sMac = null;
		try {
			mac.init(signingKey);
			System.out.println(bytesToHex(mac.doFinal(request.getBytes())));
			sMac = bytesToHex(mac.doFinal(request.getBytes()));
		} catch (Exception ex) {

		}
		return sMac;
	}

	private static String bytesToHex(final byte[] hash) {
		final StringBuffer hexString = new StringBuffer();
		for (int i = 0; i < hash.length; i++) {
			final String hex = Integer.toHexString(0xff & hash[i]);
			if (hex.length() == 1) {
				hexString.append('0');
			}
			hexString.append(hex);
		}
		return hexString.toString();
	}
	
	public String postData(String data,String URL) {

			String sResp =null;

			try {

				URL obj = new URL(URL);

				HttpURLConnection con = (HttpURLConnection) obj.openConnection();

				con.setRequestMethod("POST");

				con.setRequestProperty("Accept-Language","en-US");

				con.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");

				//con.setRequestProperty("Authorization", "Enlx4azMVN8c5XcQxv5suRNjdC6UnpomhuVUp8IIMfQg2JIEjyAGlRHfvI9QtrFN");//"pBmPFiN1eGwaQhP1EbXv7crXyBPAHBLyrjxiVZ5NGc8dlFCuMSzVQ8ydVnWGc99v");

				//for (int i = 0; i < headers.length; i++) {

					//con.setRequestProperty(headers[i],hVal[i]);

				//}

				// For POST only - START

				con.setDoOutput(true);

				OutputStream os = con.getOutputStream();
	 
				os.write(data.getBytes());

				os.flush();

				os.close();

				int responseCode = con.getResponseCode();

				System.out.println("POST Response Code :: " + responseCode);
	 
				if (responseCode == HttpURLConnection.HTTP_OK) { //success

					BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));

					String inputLine;

					StringBuffer response = new StringBuffer();
	 
					while ((inputLine = in.readLine()) != null) {

						response.append(inputLine);

					}

					in.close();
	 
					// print result

					logger.info(response.toString());

					sResp = response.toString();

				} else {

					logger.info("POST request did not work.");

				}

			} catch (Exception e) {

				e.printStackTrace();

			}

			return sResp;

		}	

	
	
	public String postData111(String data, String URL) {
		String sResp = null;
		try {
			URL obj = new URL(URL);
			HttpURLConnection con = (HttpURLConnection) obj.openConnection();
			con.setRequestMethod("POST");
			con.setRequestProperty("Accept-Language", "en-US");
			con.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
			// con.setRequestProperty("Authorization",
			// "Enlx4azMVN8c5XcQxv5suRNjdC6UnpomhuVUp8IIMfQg2JIEjyAGlRHfvI9QtrFN");//"pBmPFiN1eGwaQhP1EbXv7crXyBPAHBLyrjxiVZ5NGc8dlFCuMSzVQ8ydVnWGc99v");
			// for (int i = 0; i < headers.length; i++) {
			// con.setRequestProperty(headers[i],hVal[i]);
			// }
			// For POST only - START
			con.setDoOutput(true);
			OutputStream os = con.getOutputStream();

			os.write(data.getBytes());
			os.flush();
			os.close();
			int responseCode = con.getResponseCode();
			System.out.println("POST Response Code :: " + responseCode);

			if (responseCode == HttpURLConnection.HTTP_OK) { // success
				BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
				String inputLine;
				StringBuffer response = new StringBuffer();

				while ((inputLine = in.readLine()) != null) {
					response.append(inputLine);
				}
				in.close();

				// print result
				System.out.println(response.toString());
				sResp = response.toString();

			} else {

				System.out.println("POST request did not work.");
			}
		} catch (Exception e) {

			e.printStackTrace();
		}
		return sResp;
	}

	public String getResponse(String URL) {
		try {
			URL obj = new URL(URL);
			HttpsURLConnection con = (HttpsURLConnection) obj.openConnection();
			con.setRequestMethod("GET");
			int responseCode = con.getResponseCode();
			System.out.println("GET Response Code :: " + responseCode);
			if (responseCode == HttpURLConnection.HTTP_OK) { // success
				BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
				String inputLine;
				StringBuffer response = new StringBuffer();

				while ((inputLine = in.readLine()) != null) {
					response.append(inputLine);
				}
				in.close();

				// print result
				System.out.println(response.toString());
				return response.toString();
			} else {
				System.out.println("GET request did not work.");
				return null;
			}
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}

	}
}
