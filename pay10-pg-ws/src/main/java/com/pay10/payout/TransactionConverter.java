package com.pay10.payout;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;

import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.pay10.commons.dao.FieldsDao;
import com.pay10.commons.exception.ErrorType;
import com.pay10.commons.exception.SystemException;
import com.pay10.commons.user.AccountCurrencyPayout;
import com.pay10.commons.user.AccountCurrencyPayoutDao;
import com.pay10.commons.user.UserDao;
import com.pay10.commons.util.Amount;
import com.pay10.commons.util.FieldType;
import com.pay10.commons.util.Fields;
import com.pay10.commons.util.StatusType;
import com.pay10.payout.payten.PaytenPayoutEncryptionDecryption;
import com.pay10.payout.payten.PaytenPayoutIServiceImpl;
import com.pay10.payout.payten.PaytenRequest;
import com.pay10.payout.payten.PaytenResponse;
import com.pay10.pg.core.util.AcquirerTxnAmountProvider;

@Service("PayoutTransactionConverter")
public class TransactionConverter {

	private static Logger logger = LoggerFactory.getLogger(TransactionConverter.class.getName());

	@Autowired
	private AcquirerTxnAmountProvider acquirerTxnAmountProvider;

	@Autowired
	private FieldsDao fieldsDao;

	@Autowired
	private UserDao userDao;

	@Autowired
	private PaytenPayoutIServiceImpl integration;

	@Autowired
	AccountCurrencyPayoutDao accountCurrencyPayoutDao;

	@SuppressWarnings("incomplete-switch")
	public void  perpareRequest(Fields fields) throws SystemException {

		String request;
		AccountCurrencyPayout accountCurrencyRequest = accountCurrencyPayoutDao.getAccountCurrencyPayoutDetail(
				fields.get(FieldType.ACQUIRER_TYPE.getName()), fields.get(FieldType.CURRENCY_CODE.getName()));

		  saleRequest(fields, accountCurrencyRequest);


	}

	private void saleRequest(Fields fields, AccountCurrencyPayout accountCurrencyRequest) throws SystemException {
		PaytenResponse transactionENCResponse = null;
		Gson gson = new Gson();
		String encRequest = null;

		try {
			logger.info("field " + fields.getFieldsAsString());
			HashMap<String, Object> tree_map = new LinkedHashMap<>();
			tree_map.put("accountNo", fields.get(FieldType.ACC_NO.getName()));
			tree_map.put("aggregatorId", accountCurrencyRequest.getMerchantId());// put hard coded reseller id
			tree_map.put("amount", new BigDecimal(Amount.toDecimal(fields.get(FieldType.AMOUNT.getName()),
					fields.get(FieldType.CURRENCY_CODE.getName()))).setScale(2, RoundingMode.HALF_UP));
			tree_map.put("bankName", fields.get(FieldType.ACC_NAME.getName()));
			tree_map.put("beneName", fields.get(FieldType.PAYER_NAME.getName()));
			tree_map.put("ifscCode", fields.get(FieldType.IFSC.getName()));
			tree_map.put("merchantTransId", fields.get(FieldType.PG_REF_NUM.getName()));
			tree_map.put("secretKey", accountCurrencyRequest.getAdf1());// put hard coded key
			tree_map.put("transferType", fields.get(FieldType.TRANSFER_TYPE.getName()));
			logger.info("request for payout " + tree_map);

			gson = new GsonBuilder().disableHtmlEscaping().create();
			String jsonString = gson.toJson(tree_map);
			String hash = PaytenPayoutEncryptionDecryption.generatetHashString(jsonString);
			tree_map.put("hash", hash);
			tree_map.remove("secretKey");
			logger.info("request for payout " + tree_map);

			encRequest = gson.toJson(tree_map);
			logger.info("request for payout " + encRequest);

			String response = callRestAPi(fields, encRequest, accountCurrencyRequest.getAdf3());
			JSONObject resp = new JSONObject(response);
			if(((String)resp.getString("response")).equalsIgnoreCase("REQUEST ACCEPTED")&&((String)resp.getString("code")).equalsIgnoreCase("300")) {
				  fields.put(FieldType.STATUS.getName(), StatusType.SENT_TO_BANK.getName());
                  fields.put(FieldType.RESPONSE_CODE.getName(), ErrorType.SUCCESS.getResponseCode());
                  fields.put(FieldType.RESPONSE_MESSAGE.getName(), ErrorType.SUCCESS.getResponseMessage());

			}else {
			  fields.put(FieldType.STATUS.getName(), StatusType.FAILED_AT_ACQUIRER.getName());
              fields.put(FieldType.RESPONSE_CODE.getName(), ErrorType.FAILED.getResponseCode());
              fields.put(FieldType.RESPONSE_MESSAGE.getName(), ErrorType.FAILED.getResponseMessage());
			}
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			 fields.put(FieldType.STATUS.getName(), StatusType.FAILED_AT_ACQUIRER.getName());
             fields.put(FieldType.RESPONSE_CODE.getName(), ErrorType.FAILED.getResponseCode());
             fields.put(FieldType.RESPONSE_MESSAGE.getName(), ErrorType.FAILED.getResponseMessage());
		
		}
	}

	public String callRestAPi(Fields fields, String encRequestFinal, String Url) {
		PaytenRequest fundTransferBean = new PaytenRequest();
		Gson gson = new Gson();
		StringBuilder serverResponse = new StringBuilder();

		PaytenResponse decResponse = new PaytenResponse();
		try {
			logger.info("request Api CAll url={},Request={}", Url, encRequestFinal);
			URL url = new URL(Url);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setDoOutput(true);
			conn.setRequestMethod("POST");
			conn.setRequestProperty("Content-Type", "application/json");
			conn.setRequestProperty("IPIMEI", "0.0.0.0");
			conn.setRequestProperty("AGENT", "WEB");

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
				serverResponse.append(output);

			}
			logger.info("Output from Server .... \n" + serverResponse);

			conn.disconnect();
		} catch (MalformedURLException e) {
			logger.info("aaa :" + e);
			decResponse.setStatuscode("300");

			e.printStackTrace();
		} catch (IOException e) {
			logger.info("bb :" + e);
			decResponse.setStatuscode("300");
			e.printStackTrace();
		}
		return serverResponse.toString();
	}

	public String callIntegrator(Fields fields) {

		PaytenResponse paytenResponse = null;
		try {

			logger.info("CALLING PAY10 Acquirere for payouts");
			logger.info("Field " + fields.getFieldsAsString());

			logger.info("PaytenPayoutIntegration :" + integration);

			paytenResponse = integration.fundTransferRequest("", fields.get(FieldType.ORDER_ID.getName()),
					fields.get(FieldType.ACC_NO.getName()), fields.get(FieldType.IFSC.getName()).toString(),
					Amount.toDecimal(fields.get(FieldType.AMOUNT.getName()).toString(),
							fields.get(FieldType.CURRENCY_CODE.getName())),
					fields.get(FieldType.REMARKS.getName()).toString(),
					new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()), "IMPS",
					fields.get(FieldType.CUST_NAME.getName()).toString(),
					fields.get(FieldType.ACC_NAME.getName()).toString());

			logger.info("Exit PaytenPayoutIntegration :" + integration);

		} catch (Exception e) {
			logger.error("aziz sir :", e);
			e.printStackTrace();
		}
		return new Gson().toJson(paytenResponse);
	}



	String date() {
		LocalDateTime myDateObj = LocalDateTime.now();
		DateTimeFormatter myFormatObj = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

		return myDateObj.format(myFormatObj);
	}
}
