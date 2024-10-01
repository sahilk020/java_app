package com.pay10.axisbank.netbanking;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.pay10.commons.exception.ErrorType;
import com.pay10.commons.exception.SystemException;
import com.pay10.commons.util.Amount;
import com.pay10.commons.util.FieldType;
import com.pay10.commons.util.Fields;
import com.pay10.commons.util.PropertiesManager;
import com.pay10.commons.util.StatusType;
import com.pay10.commons.util.TransactionManager;
import com.pay10.commons.util.TransactionType;
import com.pay10.commons.util.Validator;
import com.pay10.pg.core.util.AxisBankNBEncDecService;
import com.pay10.pg.core.util.ProcessManager;
import com.pay10.pg.core.util.Processor;

@Service
public class AxisBankNBSaleResponseHandler {

	private static Logger logger = LoggerFactory.getLogger(AxisBankNBSaleResponseHandler.class.getName());

	@Autowired
	@Qualifier("axisBankNBTransactionConverter")
	private TransactionConverter converter;
	
	@Autowired
	private Validator generalValidator;
	
	@Autowired
	private AxisBankNBEncDecService axisBankNBEncDecService;

	@Autowired
	@Qualifier("updateProcessor")
	private Processor updateProcessor;

	@Autowired
	private AxisBankNBTransformer axisBankTransformer;

	public static final String PIPE = "|";
	public static final String DOUBLE_PIPE = "||";

	public Map<String, String> process(Fields fields) throws SystemException {
		
		String newTxnId = TransactionManager.getNewTransactionId();
		fields.put(FieldType.TXN_ID.getName(), newTxnId);
		logger.info("Fields In AxisBankNBSaleResponseHandler "+fields.getFieldsAsString());
		generalValidator.validate(fields);
		Transaction transactionResponse = new Transaction();
		String pipedResponse = fields.get(FieldType.AXISBANK_NB_RESPONSE_FIELD.getName());
		transactionResponse = toTransaction(pipedResponse);
		
		boolean doubleVer = doubleVerification(transactionResponse, fields);
		logger.info("process:: doubleVerification={}, pgRefNo={} ", doubleVer,
				fields.get(FieldType.PG_REF_NUM.getName()));
		if (doubleVer) {
			axisBankTransformer = new AxisBankNBTransformer(transactionResponse);
			axisBankTransformer.updateResponse(fields);

		} else {
			fields.put(FieldType.STATUS.getName(), StatusType.DENIED.getName());
			fields.put(FieldType.RESPONSE_CODE.getName(), ErrorType.SIGNATURE_MISMATCH.getCode());
			fields.put(FieldType.RESPONSE_MESSAGE.getName(), ErrorType.SIGNATURE_MISMATCH.getResponseMessage());
		}
		
		fields.put(FieldType.INTERNAL_ORIG_TXN_TYPE.getName(), fields.get(FieldType.TXNTYPE.getName()));
		fields.remove(FieldType.AXISBANK_NB_RESPONSE_FIELD.getName());
		fields.remove(FieldType.TXN_KEY.getName());
		fields.remove(FieldType.ADF10.getName());
		fields.remove(FieldType.ADF9.getName());
		fields.remove(FieldType.ADF11.getName());
		logger.info("Fields before call updateProcessor in saleHandler "+fields.getFieldsAsString());
		ProcessManager.flow(updateProcessor, fields, true);
		return fields.getFields();

	}
	private boolean doubleVerification(Transaction transactionResponse, Fields fields) {

		try {
			// Skip for unsuccessful transactions if
			if (!transactionResponse.getStatFlg().equalsIgnoreCase("S")) {
				return true;
			}

			String encrequest = statusEnquiryRequest(fields,transactionResponse);

			String response =getResponse(encrequest,fields);
			String decryptedResponse = null;
				 //decryptedResponse = axisBankNBEncDecService.AESDecrypt(xmlResponse, fields.get(FieldType.ADF9.getName()));
			decryptedResponse = AxisBankNBEncDecService.decryptStatusEnquiryResp(response, fields.get(FieldType.ADF9.getName()));
			Transaction transactionStatusResponse =converter.toTransaction(decryptedResponse,"STATUS");


			String	toamount = Amount.toDecimal(fields.get(FieldType.TOTAL_AMOUNT.getName()), fields.get(FieldType.CURRENCY_CODE.getName()));
			logger.info("response from  axis nb  dual validation pgrefno={},amount={}",fields.get(FieldType.PG_REF_NUM.getName()),fields.get(FieldType.TOTAL_AMOUNT.getName()));
						logger.info(transactionStatusResponse.getPrn() + "axis nb  bank Double verification Response = "
								+ response);
						if (transactionStatusResponse.getPrn().equalsIgnoreCase(fields.get(FieldType.PG_REF_NUM.getName()))&&
							 (transactionStatusResponse.getAmt().equalsIgnoreCase(toamount))&&transactionStatusResponse.getStatFlg().equalsIgnoreCase("S")) {

							return true;
						}
			logger.info(
					"doubleVerification:: failed.   resAmount={},pgRefNo={}",
					transactionStatusResponse.getAmt(),transactionStatusResponse.getPrn());
	
			return false;
		}

		catch (Exception e) {
			logger.error("Exceptionn ", e);
			return false;
		}

	}
	
	
	
	public static String getResponse(String request, Fields fields) throws SystemException {

		String hostUrl = "";
		//fields.put(FieldType.TXNTYPE.getName(), "STATUS");
		String xmlResponse = null;

		try {

			
				hostUrl = PropertiesManager.propertiesMap.get(Constants.AXISBANK_NB_STATUS_ENQ_URL);
			

			logger.info(
					"Request message to axis bank net banking : TxnType = " + fields.get(FieldType.TXNTYPE.getName())
							+ " " + "Order Id = " + fields.get(FieldType.ORDER_ID.getName()) + " Request >>>>> " + request);

			logger.info("AxisBankNB Status Enquiry URL : "+hostUrl);
			
			URL url = new URL(hostUrl);
			Map<String, Object> params = new LinkedHashMap<>();
			params.put("encdata", request);
			params.put("payeeid", fields.get(FieldType.ADF10.getName()));
			params.put("enccat", "A6ECS6");
			params.put("mercat", fields.get(FieldType.ADF11.getName()));

			StringBuilder postData = new StringBuilder();
			for (Map.Entry<String, Object> param : params.entrySet()) {
				if (postData.length() != 0)
					postData.append('&');
				postData.append(URLEncoder.encode(param.getKey(), "UTF-8"));
				postData.append('=');
				postData.append(URLEncoder.encode(String.valueOf(param.getValue()), "UTF-8"));
			}
			logger.info("postData : "+postData.toString());
			byte[] postDataBytes = postData.toString().getBytes("UTF-8");

			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setRequestMethod("POST");
			conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
			conn.setRequestProperty("Content-Length", String.valueOf(postDataBytes.length));
			conn.setRequestProperty("payloadtype", "json");
			conn.setDoOutput(true);
			conn.getOutputStream().write(postDataBytes);

			Reader in = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"));

			StringBuilder sb = new StringBuilder();
			for (int c; (c = in.read()) >= 0;)
				sb.append((char) c);
			 xmlResponse = sb.toString();
			return xmlResponse;

		} catch (Exception e) {
			logger.error("Exception in axisbank NB txn Communicator for status enquiry ", e);
		}
		return null;
	}


	public static String getTextBetweenTags(String text, String tag1, String tag2) {

		int leftIndex = text.indexOf(tag1);
		if (leftIndex == -1) {
			return null;
		}

		int rightIndex = text.indexOf(tag2);
		if (rightIndex != -1) {
			leftIndex = leftIndex + tag1.length();
			return text.substring(leftIndex, rightIndex);
		}

		return null;
	}

	
	public String Statusenquirerresponse(String request,String hostUrl,Fields fields)
	{
		try {
	URL url = new URL(hostUrl);
	Map<String, Object> params = new LinkedHashMap<>();
	params.put("encdata", request);
	params.put("payeeid", fields.get(FieldType.ADF10.getName()));

	StringBuilder postData = new StringBuilder();
	for (Map.Entry<String, Object> param : params.entrySet()) {
		if (postData.length() != 0)
			postData.append('&');
		postData.append(URLEncoder.encode(param.getKey(), "UTF-8"));
		postData.append('=');
		postData.append(URLEncoder.encode(String.valueOf(param.getValue()), "UTF-8"));
	}
	byte[] postDataBytes = postData.toString().getBytes("UTF-8");

	HttpURLConnection conn = (HttpURLConnection) url.openConnection();
	conn.setRequestMethod("POST");
	conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
	conn.setRequestProperty("Content-Length", String.valueOf(postDataBytes.length));
	conn.setRequestProperty("payloadtype", "json");
	conn.setDoOutput(true);
	conn.getOutputStream().write(postDataBytes);

	Reader in = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"));

	StringBuilder sb = new StringBuilder();
	for (int c; (c = in.read()) >= 0;)
		sb.append((char) c);
	String xmlResponse = sb.toString();
	return xmlResponse;

} catch (Exception e) {
	logger.error("Exception in axisbank NB txn Communicator", e);
}
return null;

}

	public String statusEnquiryRequest(Fields fields, Transaction transaction) {
		
		
	
		
			

		String pattern = "yyyy-MM-dd";
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);

		String date = simpleDateFormat.format(new Date());
		String checksumString = null;
		
		checksumString = "payeeid=" + fields.get(FieldType.ADF10.getName()) + "|itc=" + transaction.getItc() + "|prn="
				+ transaction.getPrn() + "|date=" + date + "|amt=" + transaction.getAmt();
		logger.info("Request For checksum AxisBankNB Status Enquiry : "+checksumString);

		String checksumHash = axisBankNBEncDecService.getSha256(checksumString);

String payString = "payeeid=" + fields.get(FieldType.ADF10.getName()) + "|itc=" + transaction.getItc() + "|prn="
				+ transaction.getPrn() + "|date=" + date + "|amt=" + transaction.getAmt() + "|chksum="
				+ checksumHash;
		logger.info("Request For AxisBankNB Status Enquiry : "+payString+", And TxnKey : "+fields.get(FieldType.ADF9.getName()));

		String payStringEncrypted = AxisBankNBEncDecService.encryptStatusEnquiryReq(payString, fields.get(FieldType.ADF9.getName()));
		
		return payStringEncrypted;
	}
	public Transaction toTransaction(String pipedResponse) {
		Transaction transaction = new Transaction();
		
		String responseArray [] = pipedResponse.split("&");
		
		for (String data : responseArray) {
			
			if (data.contains(Constants.PAID)) {
				String dataArray [] = data.split("=");
				transaction.setPaid(dataArray[1]);
			}
			
			else if (data.contains(Constants.PRN)) {
				String dataArray [] = data.split("=");
				transaction.setPrn(dataArray[1]);
			}
			
			else if (data.contains(Constants.BID)) {
				String dataArray [] = data.split("=");
				transaction.setBid(dataArray[1]);
			}
			
			else if (data.contains(Constants.ITC)) {
				String dataArray [] = data.split("=");
				transaction.setItc(dataArray[1]);
			}
			
			else if (data.contains(Constants.AMT)) {
				String dataArray [] = data.split("=");
				transaction.setAmt(dataArray[1]);
			}
			
			else if (data.contains(Constants.CRN)) {
				String dataArray [] = data.split("=");
				transaction.setCrn(dataArray[1]);
			}
			
			else if (data.contains(Constants.STATFLG)) {
				String dataArray [] = data.split("=");
				transaction.setStatFlg(dataArray[1]);
			}
			else {
				continue;
			}
			
		}
		
		return transaction;
	}// toTransaction()


}
