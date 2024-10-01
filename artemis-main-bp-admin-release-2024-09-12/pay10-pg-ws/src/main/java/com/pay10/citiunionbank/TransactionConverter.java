package com.pay10.citiunionbank;

import org.apache.commons.lang3.StringUtils;
import org.codehaus.jettison.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.pay10.axisbank.netbanking.Constants;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import com.pay10.commons.exception.ErrorType;
import com.pay10.commons.exception.SystemException;
import com.pay10.commons.util.Currency;
import com.pay10.commons.util.DataEncoder;
import com.pay10.commons.util.FieldType;
import com.pay10.commons.util.Fields;
import com.pay10.commons.util.PropertiesManager;
import com.pay10.commons.util.StatusType;
import com.pay10.commons.util.TransactionType;
import com.pay10.pg.core.jammuandKishmir.ShoppingMallBase64EncoderDecoder;
import com.pay10.pg.core.pageintegrator.CityUnionBankIUntil;
import com.pay10.pg.core.util.AcquirerTxnAmountProvider;

@Service("cityUnionBankTransactionConverter")

public class TransactionConverter {

	private static Logger logger = LoggerFactory.getLogger(TransactionConverter.class.getName());

	private CityUnionBankIUntil cityUnionBankIUntil;
	private AcquirerTxnAmountProvider acquirerTxnAmountProvider;

	
	
	public TransactionConverter(CityUnionBankIUntil cityUnionBankIUntil,
			AcquirerTxnAmountProvider acquirerTxnAmountProvider) {
		super();
		this.cityUnionBankIUntil = cityUnionBankIUntil;
		this.acquirerTxnAmountProvider = acquirerTxnAmountProvider;
	}


	@SuppressWarnings("incomplete-switch")
	public String perpareRequest(Fields fields, Transaction transaction) throws SystemException {

		String request = null;

		switch (TransactionType.getInstance(fields.get(FieldType.TXNTYPE.getName()))) {

		case SALE:
			request = saleRequest(fields);
			break;

			default:
				break;
		}
		return request;

	}

	public String saleRequest(Fields fields) throws SystemException {

		try {
			if (StringUtils.isBlank(fields.get(FieldType.PG_REF_NUM.getName()))) {
				fields.put(FieldType.PG_REF_NUM.getName(), fields.get(FieldType.TXN_ID.getName()));

			}
			String returnUrl = PropertiesManager.propertiesMap.get("CITYUNIONBANK_RETURNURL");
			logger.info("feild for city union bank={}" , fields.getFieldsAsString());
			String amount = acquirerTxnAmountProvider.amountProvider(fields);
			logger.info("feild for city union bank fields={},amount={}" , fields.getFieldsAsString() , amount);
			String currency = Currency.getAlphabaticCode(fields.get(FieldType.CURRENCY_CODE.getName()));
			String merchantCode = fields.get(FieldType.MERCHANT_ID.getName());
			String iv = fields.get(FieldType.ADF1.getName());

			String encryptKey = fields.get(FieldType.TXN_KEY.getName());
			String civ = fields.get(FieldType.ADF4.getName());

			String cencryptKey = fields.get(FieldType.ADF5.getName());
			String HandleId = fields.get(FieldType.ADF2.getName());
			String Pid = fields.get(FieldType.ADF3.getName());

			StringBuilder salerequest = new StringBuilder();

			salerequest.append("HandleID=");
			salerequest.append(HandleId);

			salerequest.append("&PID=");
			salerequest.append(Pid);
			salerequest.append("&merchantcode=");

			salerequest.append(merchantCode);
			salerequest.append("&trantype=P&");
			logger.info(" raw request City Union bank ={}", salerequest);
//			BRN=8006177963688806&AMT=4.0&RU=https://test.timesofmoney.com/direcpay/secure/transactionRe 
//				sponse.jsp&NAR=CA&ACCNO=NA
			StringBuilder dateEnc = new StringBuilder();
			dateEnc.append("BRN=");
			dateEnc.append(fields.get(FieldType.PG_REF_NUM.getName()));
			dateEnc.append("&AMT=");
			dateEnc.append(amount);
			dateEnc.append("&RU=");
			dateEnc.append(returnUrl);
			dateEnc.append("&NAR=CA&ACCNO=NA&CHECKSUM=");
			dateEnc.append(cityUnionBankIUntil.calculateChecksum(fields.get(FieldType.PG_REF_NUM.getName())+amount));
			logger.info(" encrypt request before data encryption union bank={}", dateEnc);

			String encryptyDate = cityUnionBankIUntil.getEncryptedString(iv, encryptKey, dateEnc.toString());
			logger.info(" encrypt  Data  citry union bank={}" , encryptyDate);
			salerequest.append("DATA=");
			salerequest.append(encryptyDate);
			logger.info(" raw request city union bank ={}" , salerequest);
			String encryptyRequest = cityUnionBankIUntil.getEncryptedString(civ, cencryptKey, salerequest.toString());
			logger.info(" encrypt  request city union bank={}",encryptyRequest);

			return encryptyRequest;

		}

		catch (Exception e) {
			logger.error("Exception in generating city UNION  bank request ", e);
		}

		return null;
	}

	 String getResponse(JSONObject finalRequest) {
		
		try{
			
			String hostUrl = PropertiesManager.propertiesMap.get("CITYUNIONBANK_SALE_URL");

		String clientId = PropertiesManager.propertiesMap.get("CITYUNIONBANK_X-IBM-Client-Id");
		String ClientSecret = PropertiesManager.propertiesMap.get("CITYUNIONBANKX-IBM-Client-Secret");


		logger.info("Sale Request to for city yunion bank :={}",finalRequest);
		URL url = new URL(hostUrl);
		
		HttpURLConnection connection = (HttpURLConnection) url.openConnection();
		connection.setRequestMethod("POST");
		connection.setRequestProperty("Content-Type", "application/json");
		connection.setRequestProperty("X-IBM-Client-Id", clientId);
		connection.setRequestProperty("X-IBM-Client-Secret", ClientSecret);

		connection.setDoOutput(true);

		DataOutputStream requestWriter = new DataOutputStream(connection.getOutputStream());
		requestWriter.writeBytes(finalRequest.toString());
		requestWriter.close();
		String responseData = "";
		int responseCode = connection.getResponseCode();
		InputStream inputStream;
		if (responseCode == HttpURLConnection.HTTP_OK)
		{
			inputStream = connection.getInputStream();
		} else {
			inputStream = connection.getErrorStream();
		}
		BufferedReader reader;
		reader = new BufferedReader(new InputStreamReader(inputStream));
		String response = "";
		while ((response = reader.readLine()) != null) {

			responseData += response;
		}
		inputStream.close();
		logger.info("response for city union bank ={}" , responseData);

		return responseData;
		
	} catch (Exception e) {
		logger.error("Exception in Paytm Status Enquiry ={}",e);
	}
	return null;

		
	}

	public void updateSaleResponse(Fields fields, String request) {
		logger.info("Encrypted Request to CITY UNION BANK bank: TxnType = {},Txn id ={},request={}",
				fields.get(FieldType.TXNTYPE.getName()), fields.get(FieldType.TXN_ID.getName()) , request);
		fields.put(FieldType.CITY_UNION_BANK_FINAL_REQUEST.getName(), request);
		fields.put(FieldType.STATUS.getName(), StatusType.SENT_TO_BANK.getName());
		fields.put(FieldType.RESPONSE_CODE.getName(), ErrorType.SUCCESS.getResponseCode());
		fields.put(FieldType.RESPONSE_MESSAGE.getName(), ErrorType.SUCCESS.getResponseMessage());
	}

	public Transaction toTransaction(String response) {
		Transaction transaction = new Transaction();
		// PID=1600671|PRN=1059130113163538|ITC=PAY10|AMT=10.00|CRN=INR|BID=23520792|TXN_STATUS=SUSPECT|
		String responseArray[] = response.split("\\|");

		for (String data : responseArray) {

			if (data.contains("PID")) {
				String dataArray[] = data.split("=");
				transaction.setPaid(dataArray[1]);
			}

			else if (data.contains("PRN")) {
				String dataArray[] = data.split("=");
				transaction.setPrn(dataArray[1]);
			}

			else if (data.contains("BID")) {
				String dataArray[] = data.split("=");
				transaction.setBid(dataArray[1]);
			}

			else if (data.contains("ITC")) {
				String dataArray[] = data.split("=");
				transaction.setItc(dataArray[1]);
			}

			else if (data.contains("AMT")) {
				String dataArray[] = data.split("=");
				transaction.setAmt(dataArray[1]);
			}

			else if (data.contains("CRN")) {
				String dataArray[] = data.split("=");
				transaction.setCrn(dataArray[1]);
			}

			else if (data.contains("TXN_STATUS")) {
				String dataArray[] = data.split("=");
				transaction.setStatus(dataArray[1]);
			} else {
				continue;
			}

		}
		logger.info("log transaction ={}", transaction);
		return transaction;

	}

}
