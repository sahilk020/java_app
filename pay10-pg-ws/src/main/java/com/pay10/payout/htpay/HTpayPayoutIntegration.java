package com.pay10.payout.htpay;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.UUID;
import java.util.stream.Collectors;

import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.amazonaws.util.AWSRequestMetrics.Field;
import com.google.gson.Gson;
import com.pay10.commons.util.FieldType;
import com.pay10.commons.util.Fields;
import com.pay10.commons.util.TransactionType;


@Service
public class HTpayPayoutIntegration {
	private static Logger log = LoggerFactory.getLogger(HTpayPayoutIntegration.class.getName());
	private static String BASE_URL = "http://gateway.tgapg.com:9909/api/v2/payout";
	private static String TRANSFER_URL = "/placeOrder";
	private static String secretkey = "Enlx4azMVN8c5XcQxv5suRNjdC6UnpomhuVUp8IIMfQg2JIEjyAGlRHfvI9QtrFN";
	private static String BALANACE_ENQUIRY_URL = "/getBalance";

	private static String BANK_LIST_URL = "/getBankList";

	private static String Opmhtid = "BTSE";
	private static String Mhtuserid = "BTSE";

	
	public void process(Fields fields) {
		
		if(fields.get(FieldType.TXN_TYPE.getName()).equalsIgnoreCase(TransactionType.SALE.getCode()))
		saleTransferRequest(fields);

		
	}

	
	
	public static HtpayTransactionResponse transferRequest(HtpayTransactionRequest htpayTransactionRequest) {
		HtpayTransactionResponse htpayTransactionResponse = new HtpayTransactionResponse();
		try {
			URL url = new URL(BASE_URL + TRANSFER_URL);

			System.out.println("URL :" + url.toString());
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setDoOutput(true);
			conn.setRequestMethod("POST");
			conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");

			String encRequestFinal = null;
			try {
				encRequestFinal = RequestCreator
						.generateRequestTransfer(RequestCreator.prepareMapForTransferWithSign(htpayTransactionRequest));
				System.out.println("Final request to post :" + encRequestFinal);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			OutputStream os = conn.getOutputStream();
			os.write(encRequestFinal.getBytes());
			os.flush();
			if (conn.getResponseCode() != HttpURLConnection.HTTP_OK) {
				log.error(new Gson().toJson(conn));
				throw new RuntimeException("Failed : HTTP error code : " + conn.getResponseCode());
			}
			BufferedReader br = new BufferedReader(new InputStreamReader((conn.getInputStream())));
			String output;
			log.info("Output from Server .... \n");
			while ((output = br.readLine()) != null) {
				System.out.println(output);
				try {
					htpayTransactionResponse = new Gson().fromJson(output, HtpayTransactionResponse.class);
					JSONObject obj = new JSONObject(output);

				} catch (Exception ex) {
					ex.printStackTrace();
				}
			}
			conn.disconnect();
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return htpayTransactionResponse;
	}

 public String   saleTransferRequest(Fields fields) {
	HtpayTransactionRequest htpayTransactionRequest = new HtpayTransactionRequest();

		htpayTransactionRequest.setAcccityname(fields.get(FieldType.ACC_CITY_NAME.getName()));
		htpayTransactionRequest.setAccname(fields.get(FieldType.ACC_NAME.getName()));

		htpayTransactionRequest.setAccno(fields.get(FieldType.ACC_NO.getName()));
		htpayTransactionRequest.setAccprovince(fields.get(FieldType.ACC_PROVINCE.getName()));
		htpayTransactionRequest.setAcctype("bank_card");
		htpayTransactionRequest.setAmount(10000000);
		htpayTransactionRequest.setBankbranch("sdafrwetewrtw");
//		htpayTransactionRequest.setCurrency("VND");
//		htpayTransactionRequest.setBankcode("VNABB");
//		htpayTransactionRequest.setBankcode("VNVCB");

//		htpayTransactionRequest.setCurrency("CNY");
//		htpayTransactionRequest.setBankcode("OTHEACCP");

//		htpayTransactionRequest.setCurrency("THB");
//		htpayTransactionRequest.setBankcode("THABI");
		htpayTransactionRequest.setCurrency("IDR");
		htpayTransactionRequest.setBankcode("IDACEH");

		htpayTransactionRequest.setMhtorderno(UUID.randomUUID().toString());
		// htpayTransactionRequest.setMhtorderno("S60898120");

		htpayTransactionRequest.setNotifyurl("https://bpgate.nxtpay.in/pgws/htpay/notifyTxncallbackResponse");
		htpayTransactionRequest.setOpmhtid(Opmhtid);

		htpayTransactionRequest.setPayerphone("9999999999");
		htpayTransactionRequest.setRandom(UUID.randomUUID().toString());

	try {
			System.out.println("Request Before sign " + new Gson().toJson(htpayTransactionRequest));
			htpayTransactionRequest.setSign(RequestCreator
					.generateHmacSha384Sign(RequestCreator.prepareMapForTransfer(htpayTransactionRequest), secretkey));
			System.out.println("Request After sign " + new Gson().toJson(htpayTransactionRequest));
		} catch (Exception e) {
			e.printStackTrace();
		}
		transferRequest(htpayTransactionRequest);



		getBankList("IDR");
		return null;

	}
public static void main(String[] args) {
	getBankList("IDR");

}

	public static HTPayBankListResponse getBankList(String currency) {
		HTPayBankListResponse htPayBankListResponse = new HTPayBankListResponse();
		try {
			URL url = new URL(String.format(BASE_URL + BANK_LIST_URL + "?cur=%s", currency));
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setDoOutput(true);
			conn.setRequestMethod("GET");

			try {

			} catch (Exception e) {
				e.printStackTrace();
			}
			if (conn.getResponseCode() != HttpURLConnection.HTTP_OK) {
				log.error(new Gson().toJson(conn));
				throw new RuntimeException("Failed : HTTP error code : " + conn.getResponseCode());
			}
			BufferedReader br = new BufferedReader(new InputStreamReader((conn.getInputStream())));
			String output;
			log.info("Output from Server .... \n");
			while ((output = br.readLine()) != null) {
				System.out.println(output);
				try {
					htPayBankListResponse = new Gson().fromJson(output, HTPayBankListResponse.class);
					System.out.println(new Gson().toJson(htPayBankListResponse.getResult()));

				} catch (Exception ex) {
					ex.printStackTrace();
				}
			}
			conn.disconnect();
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return htPayBankListResponse;
	}

	public static HTpayBalanceResponse balanceEnquiry(HTpayBalanceRequest balanceRequest) {
		HTpayBalanceResponse hTpayBalanceResponse = new HTpayBalanceResponse();
		try {
			URL url = new URL(String.format(BASE_URL + BALANACE_ENQUIRY_URL + "?opmhtid=%s&random=%s&sign=%s",
					balanceRequest.getOpmhtid(), balanceRequest.getRandom(), balanceRequest.getSign()));
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setDoOutput(true);
			conn.setRequestMethod("GET");

			String encRequestFinal = null;
			try {

			} catch (Exception e) {
				e.printStackTrace();
			}
			if (conn.getResponseCode() != HttpURLConnection.HTTP_OK) {
				log.error(new Gson().toJson(conn));
				throw new RuntimeException("Failed : HTTP error code : " + conn.getResponseCode());
			}
			BufferedReader br = new BufferedReader(new InputStreamReader((conn.getInputStream())));
			String output;
			log.info("Output from Server .... \n");
			while ((output = br.readLine()) != null) {
				System.out.println(output);
				try {
					hTpayBalanceResponse = new Gson().fromJson(output, HTpayBalanceResponse.class);

					BigDecimal balance = hTpayBalanceResponse.getResult().stream()
							.filter(entry -> "VND".equalsIgnoreCase(entry.getCurrency().toString()))
							.collect(Collectors.toList()).get(0).getBalanceavailable();
					System.out.println(balance);

					JSONObject obj = new JSONObject(output);

				} catch (Exception ex) {
					ex.printStackTrace();
				}
			}
			conn.disconnect();
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return hTpayBalanceResponse;
	}

	
}
