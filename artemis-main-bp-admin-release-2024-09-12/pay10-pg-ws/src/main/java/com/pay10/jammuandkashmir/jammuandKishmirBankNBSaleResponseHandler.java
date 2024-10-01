package com.pay10.jammuandkashmir;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.pay10.axisbank.netbanking.Constants;
import com.pay10.commons.exception.ErrorType;
import com.pay10.commons.exception.SystemException;
import com.pay10.commons.util.Amount;
import com.pay10.commons.util.FieldType;
import com.pay10.commons.util.Fields;
import com.pay10.commons.util.PropertiesManager;
import com.pay10.commons.util.StatusType;
import com.pay10.commons.util.TransactionManager;
import com.pay10.commons.util.Validator;
import com.pay10.pg.core.util.AxisBankNBEncDecService;
import com.pay10.pg.core.util.ProcessManager;
import com.pay10.pg.core.util.Processor;

@Service
public class jammuandKishmirBankNBSaleResponseHandler {

	private static Logger logger = LoggerFactory.getLogger(jammuandKishmirBankNBSaleResponseHandler.class.getName());

	@Autowired
	@Qualifier("jammuandkashmirNBTransactionConverter")
	private TransactionConverter converter;

	@Autowired
	private Validator generalValidator;

	@Autowired
	@Qualifier("updateProcessor")
	private Processor updateProcessor;

	public static final String PIPE = "|";
	public static final String DOUBLE_PIPE = "||";

	public Map<String, String> process(Fields fields) throws SystemException {

		String newTxnId = TransactionManager.getNewTransactionId();
		fields.put(FieldType.TXN_ID.getName(), newTxnId);
		// generalValidator.validate(fields);
		Transaction transactionResponse = new Transaction();
		String pipedResponse = fields.get(FieldType.JAMMU_AND_KASHMIR_RESPONSE_FIELD.getName());
		transactionResponse = toTransaction(pipedResponse);
		boolean doubleVer = doubleVerification(transactionResponse, fields);
		logger.info("process:: doubleVerification={}, pgRefNo={} ", doubleVer,
				fields.get(FieldType.PG_REF_NUM.getName()));
		if (doubleVer == true) {
			JammuandKashmirBankNBTransformer jammuandKashmirBankNBTransformer = new JammuandKashmirBankNBTransformer(
					transactionResponse);
			jammuandKashmirBankNBTransformer.updateResponse(fields);

		} else {
			fields.put(FieldType.STATUS.getName(), StatusType.DENIED.getName());
			fields.put(FieldType.RESPONSE_CODE.getName(), ErrorType.SIGNATURE_MISMATCH.getCode());
			fields.put(FieldType.RESPONSE_MESSAGE.getName(), ErrorType.SIGNATURE_MISMATCH.getResponseMessage());
		}

		fields.put(FieldType.INTERNAL_ORIG_TXN_TYPE.getName(), fields.get(FieldType.TXNTYPE.getName()));
		fields.remove(FieldType.JAMMU_AND_KASHMIR_RESPONSE_FIELD.getName());
		ProcessManager.flow(updateProcessor, fields, true);
		return fields.getFields();

	}

	private boolean doubleVerification(Transaction transactionResponse, Fields fields) {

			try {
				// Skip for unsuccessful transactions if
				if (!transactionResponse.getPaid().equalsIgnoreCase("Y")) {
					return true;
				}

				String request = statusEnquiryRequest(fields,transactionResponse);
	String	hosturl = PropertiesManager.propertiesMap.get("JammuandKishmirbankNBStatusEnqUrl");
//      String hosturl="http://1.6.147.2:9085/corp/VService?Action.ShoppingMall.Login.Init=Y&BankId=JKB&UserType=1&USER_LANG_ID=001&AppType=corporate&MD=V&";
				String response =Statusenquirerresponse(request, hosturl,fields);
				logger.info("Response doubleVerification J&K"+response);

				Transaction transactionStatusResponsedata =toTransactiondual(response);

	logger.info("Response doubleVerification J&K pgrefno={},amount={}",fields.get(FieldType.PG_REF_NUM.getName()),fields.get(FieldType.TOTAL_AMOUNT.getName()));
				logger.info(transactionStatusResponsedata.getPrn() + "Response doubleVerification J&K = "
						+ response);
				String toamount = Amount.toDecimal(fields.get(FieldType.TOTAL_AMOUNT.getName()),
						fields.get(FieldType.CURRENCY_CODE.getName()));				if (transactionStatusResponsedata.getPrn().equalsIgnoreCase(fields.get(FieldType.PG_REF_NUM.getName()))&&
						transactionStatusResponsedata.getStatus().equalsIgnoreCase("SUCCESS")&&
					 (transactionStatusResponsedata.getAmt().equalsIgnoreCase(toamount))){

					return true;
				}
				logger.info(
						"doubleVerification::jammu and kishmir failed.   resAmount={},pgRefNo={}",
						transactionStatusResponsedata.getAmt(),transactionStatusResponsedata.getPrn());
		
				return false;
			}

			catch (Exception e) {
				logger.error("Exceptionn ", e);
				return false;
			}

		}

	public Transaction toTransactiondual(String response) {
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
		logger.info("doubleVerification transaction J&K " + transaction.toString());
		return transaction;

	}

	public String Statusenquirerresponse(String request, String hostUrl, Fields fields) {
		String response = "";
		try {
			logger.info(
					"Request doubleVerification  J&K" + request + "        url     " + hostUrl);
			HttpURLConnection connection = null;
			URL url;
			url = new URL(hostUrl);
			connection = (HttpURLConnection) url.openConnection();
			connection.setRequestMethod("POST");
			connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
			connection.setUseCaches(false);
			connection.setDoInput(true);
			connection.setDoOutput(true);
			connection.setConnectTimeout(60000);
			connection.setReadTimeout(60000);

			// Send request
			DataOutputStream wr = new DataOutputStream(connection.getOutputStream());
			wr.writeBytes(request);
			wr.flush();
			wr.close();

			// Get Response
			InputStream is = connection.getInputStream();

			BufferedReader bufferedreader = new BufferedReader(new InputStreamReader(is));
			String decodedString;

			while ((decodedString = bufferedreader.readLine()) != null) {
				response = response + decodedString;
			}

			bufferedreader.close();

			logger.info("Response doubleVerification  J&K" + response);

		} catch (Exception e) {
			logger.error("Exception in getting doubleVerification for jammuand kishmir  ", e);
			return response;
		}
		return response;

	}

	public String statusEnquiryRequest(Fields fields, Transaction transaction) {
		// &PID=40110&CG=Y&PRN=MJKB0000446119&
		// ITC=MJKB0000446119&AMT=27.48&CRN=INR

		StringBuilder request = new StringBuilder();
		request.append("PID=");
		request.append(transaction.getPid());
		request.append("&CG=Y");
		request.append("&PRN=");
		request.append(transaction.getPrn());
		request.append("&ITC=");
		request.append(transaction.getItc());
		request.append("&AMT=");
		request.append(transaction.getAmt());
		request.append("&CRN=");
		request.append(transaction.getCrn());
		request.append("&RU=");
		request.append(fields.get(FieldType.RETURN_URL.getName()));

		request.append("BID=");
		request.append(transaction.getBid());
		request.append("&STATFLG=H");

		logger.info("Request doubleVerification  J&K={}", request);
		return request.toString();
	}

	public Transaction toTransaction(String pipedResponse) {

		Transaction transaction = new Transaction();

		String responseArray[] = pipedResponse.split("&");

		for (String data : responseArray) {

			if (data.contains(Constants.PAID)) {
				String dataArray[] = data.split("=");
				transaction.setPaid(dataArray[1]);
			}

			else if (data.contains(Constants.PRN)) {
				String dataArray[] = data.split("=");
				transaction.setPrn(dataArray[1]);
			}

			else if (data.contains(Constants.BID)) {
				String dataArray[] = data.split("=");
				transaction.setBid(dataArray[1]);
			}

			else if (data.contains(Constants.ITC)) {
				String dataArray[] = data.split("=");
				transaction.setItc(dataArray[1]);
			}

			else if (data.contains(Constants.AMT)) {
				String dataArray[] = data.split("=");
				transaction.setAmt(dataArray[1]);
			}

			else if (data.contains(Constants.CRN)) {
				String dataArray[] = data.split("=");
				transaction.setCrn(dataArray[1]);
			}

			else if (data.contains(Constants.PID)) {
				String dataArray[] = data.split("=");
				transaction.setPid(dataArray[1]);
			} else {
				continue;
			}

		}

		return transaction;
	}// toTransaction()

}
