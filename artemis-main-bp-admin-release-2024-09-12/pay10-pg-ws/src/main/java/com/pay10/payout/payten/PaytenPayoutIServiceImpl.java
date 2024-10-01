package com.pay10.payout.payten;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.LinkedHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.json.JSONObject;
import org.springframework.stereotype.Service;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;



@Service
public class PaytenPayoutIServiceImpl {
	private static Logger logger = LoggerFactory.getLogger(PaytenPayoutIServiceImpl.class.getName());
	//private static final Logger logger = Logger.getLogger(PaytenPayoutIServiceImpl.class.getName());

//	public static final String BASE_URL_TRANSFER = "/fundTransferRequest";
//
//	private static String secretKey = "A1YKTAarsJmCayAAOBA0WA==";
//	private static String resellerid = "MERCSPAY001722";
//	SessionFactory factory;
//	private static String BASE_URL = "https://payout.pay10.com/apiservice/rest";
	
	public static final String BASE_URL_TRANSFER = "/fundTransferRequest";
	private static String secretKey = "gzkpOpX9pBEBRTG0t9Tprw79Yzv3EA3HBrmeQcuOc8o=";
	private static String resellerid = "MERCRES001001";
	
	private static String BASE_URL = "https://payout.paymentage.in/apiservice/rest";

	public PaytenResponse fundTransferRequest(String merchantId, String orderId, String beneficiaryAccount,
			String beneficiaryIFSC, String amount, String purpose, String date, String transferMode, String beneName,
			String beneBankName) {
		logger.info("REQUEST  in ajay" );
		Gson gson = new Gson();
		PaytenRequest fundTransferBean = new PaytenRequest();
		
		
		PaytenResponse decResponse = new PaytenResponse();
		try {
			logger.info("REQUEST  in ajay2" );
			URL url = new URL(BASE_URL + "/payout/fundTransferRequest");
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setDoOutput(true);
			conn.setRequestMethod("POST");
			conn.setRequestProperty("Content-Type", "application/json");
			conn.setRequestProperty("IPIMEI", "0.0.0.0");
			conn.setRequestProperty("AGENT", "WEB");
			String encRequestFinal = getencStringForFundTransfer(merchantId, orderId, beneficiaryAccount,
					beneficiaryIFSC, amount, purpose, date, transferMode, beneName, beneBankName);
			logger.info("REQUEST  " + encRequestFinal);
			OutputStream os = conn.getOutputStream();
			os.write(encRequestFinal.getBytes());
			os.flush();
			if (conn.getResponseCode() != HttpURLConnection.HTTP_OK) {
				decResponse.setStatuscode("300");
				throw new RuntimeException("Failed : HTTP error code : " + conn.getResponseCode());
			}
			BufferedReader br = new BufferedReader(new InputStreamReader((conn.getInputStream())));
			String output;
			logger.info("Output from Server .... \n");
			while ((output = br.readLine()) != null) {
				logger.info(output);
				try {
					JSONObject obj = new JSONObject(output);
					if (obj.getString("code").equalsIgnoreCase("300")) {
						logger.info(obj.toString());
						decResponse.setStatuscode(String.valueOf(obj.getInt("code")));
					} else {
						decResponse.setStatuscode(String.valueOf(obj.getInt("code")));
						logger.info("else Dec response  " + gson.toJson(decResponse));
					}
				} catch (Exception e2) {
					decResponse.setStatuscode("300");
					e2.printStackTrace();
				}
			}
			conn.disconnect();
		} catch (MalformedURLException e) {
			logger.info("aaa :"+e);
			decResponse.setStatuscode("300");
			
			e.printStackTrace();
		} catch (IOException e) {
			logger.info("bb :"+e);
			decResponse.setStatuscode("300");
			e.printStackTrace();
		}
		return decResponse;
	}

	public String getencStringForFundTransfer(String merchantId, String orderId, String beneficiaryAccount,
			String beneficiaryIFSC, String amount, String purpose, String date, String transferMode, String beneName,
			String beneBankName) {
		JSONObject json = new JSONObject();
			String encRequest = null;
		Gson gson = new Gson();
		try {
			HashMap<String, Object> tree_map = new LinkedHashMap<>();
			tree_map.put("accountNo", beneficiaryAccount);
			tree_map.put("aggregatorId", resellerid);// put hard coded reseller id
			tree_map.put("amount", new BigDecimal(amount).setScale(2, RoundingMode.HALF_UP));
			tree_map.put("bankName", beneBankName);
			tree_map.put("beneName", beneName);
			tree_map.put("ifscCode", beneficiaryIFSC);
			tree_map.put("merchantTransId", orderId);
			tree_map.put("secretKey", secretKey);// put hard coded key
			tree_map.put("transferType", transferMode);
			gson = new GsonBuilder().disableHtmlEscaping().create();
			String jsonString = gson.toJson(tree_map);
			String hash = PaytenPayoutEncryptionDecryption.generatetHashString(jsonString);
			tree_map.put("hash", hash);
			tree_map.remove("secretKey");
			encRequest = gson.toJson(tree_map);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return encRequest;
	}

	public PaytenTransactionRequest Pay10BankTransfer(String merchantId, String orderId, String beneficiaryAccount,
			String beneficiaryIFSC, String amount, String purpose, String date, String transferMode, String beneName,
			String beneBankName) throws Exception {
		PaytenTransactionRequest cReq = new PaytenTransactionRequest();

		try {

			PaytenResponse resp = fundTransferRequest(merchantId, orderId, beneficiaryAccount, beneficiaryIFSC, amount,
					purpose, date, transferMode, beneName, beneBankName);
			if (resp != null) {
				String responseCode = resp.getStatuscode();
				cReq.setStatus("Pending");
				cReq.setResponseCode("002");
				cReq.setResponseMessage("PENDING.");
			} else {
				cReq.setStatus("Failed");
				cReq.setResponseCode("001");
				cReq.setResponseMessage("FAILED.");
			}
		} catch (Exception e) {
			logger.info("Typecasting error " + e.getMessage());
			e.printStackTrace();
			cReq.setStatus("Failed");
			cReq.setResponseCode("7001");
			cReq.setResponseMessage("Something went wrong.");
		}

		return cReq;
	}

	public Double balanceCheck() {

		try {
			URL url = new URL(BASE_URL + "/payout/getWalletBalanceDtl");
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setDoOutput(true);
			conn.setRequestMethod("POST");
			conn.setRequestProperty("Content-Type", "application/json");
			String encRequest = getencStringForBalanceEnquiry();
			logger.info("REQUEST" + encRequest);
			OutputStream os = conn.getOutputStream();
			os.write(encRequest.getBytes());
			os.flush();
			if (conn.getResponseCode() != HttpURLConnection.HTTP_OK) {
				throw new RuntimeException("Failed : HTTP error code : " + conn.getResponseCode());
			}
			BufferedReader br = new BufferedReader(new InputStreamReader((conn.getInputStream())));
			String output;
			logger.info("Output from Server .... \n");
			while ((output = br.readLine()) != null) {
				logger.info(output);
				try {
					JSONObject jsonObject = new JSONObject(output);
					Double amount = jsonObject.getDouble("amount");
					return amount;
				} catch (Exception e2) {
					e2.printStackTrace();
					return 0.0;
				}
			}
			conn.disconnect();
		} catch (MalformedURLException e) {

			e.printStackTrace();
			return 0.0;
		} catch (IOException e) {

			e.printStackTrace();
			return 0.0;

		}
		return 0.0;
	}

	public String getencStringForBalanceEnquiry() {
		String encRequest = null;
		Gson gson = new Gson();
		try {
			HashMap<String, String> tree_map = new LinkedHashMap<>();
			tree_map.put("aggregatorId", resellerid);
			tree_map.put("secretKey", secretKey);
			gson = new GsonBuilder().disableHtmlEscaping().create();
			String jsonString = gson.toJson(tree_map);
			String hash = PaytenPayoutEncryptionDecryption.generatetHashString(jsonString);
			tree_map.put("hash", hash);
			tree_map.remove("secretKey");
			encRequest = gson.toJson(tree_map);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return encRequest;
	}

}
