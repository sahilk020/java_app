package com.pay10.axisbank.netbanking;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.LinkedHashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.pay10.commons.dao.FieldsDao;
import com.pay10.commons.exception.SystemException;
import com.pay10.commons.util.Amount;
import com.pay10.commons.util.FieldType;
import com.pay10.commons.util.Fields;
import com.pay10.commons.util.PropertiesManager;
import com.pay10.commons.util.TransactionType;
import com.pay10.pg.core.util.AxisBankNBEncDecService;

@Service
public class AxisBankNBStatusEnquiryProcessor {

	@Autowired
	@Qualifier("axisBankNBTransactionConverter")
	private TransactionConverter converter;

	@Autowired
	private FieldsDao fieldsDao;
	
	@Autowired
	private AxisBankNBEncDecService axisBankNBEncDecService;

	private static Logger logger = LoggerFactory.getLogger(AxisBankNBStatusEnquiryProcessor.class.getName());


	public void enquiryProcessor(Fields fields) {
		
		String response = "";
		try {
		//	String saleDate  = fieldsDao.getCaptureDate(fields.get(FieldType.PG_REF_NUM.getName()));
		//	fields.put(FieldType.CREATE_DATE.getName(), saleDate);
			String request = statusEnquiryRequest(fields);
			response = getResponse(request, fields);
		} catch (SystemException exception) {
			logger.error("Exception", exception);
		}

		updateFields(fields, response);

	}

	public String statusEnquiryRequest(Fields fields) {
		
		Transaction transaction = new Transaction();
		
		transaction.setPrn(fields.get(FieldType.PG_REF_NUM.getName()));
		transaction.setPid(fields.get(FieldType.ADF10.getName()));
		transaction.setAmt(Amount.toDecimal(fields.get(FieldType.AMOUNT.getName()),fields.get(FieldType.CURRENCY_CODE.getName())));
		//transaction.setItc("CASHLSO");
		transaction.setItc("PAY10"); //CASHLSO
		transaction.setDate(fields.get(FieldType.CREATE_DATE.getName()).substring(0,10));
	
		String checksumString = null;
		checksumString = "payeeid=" + fields.get(FieldType.ADF10.getName()) + "|itc=" + transaction.getItc() + "|prn=" + transaction.getPrn() + "|date=" + transaction.getDate() + "|amt=" + transaction.getAmt();
		logger.info("Request For checksum AxisBankNB Status Enquiry : "+checksumString);
		String checksumHash = axisBankNBEncDecService.getSha256(checksumString);
		
		String payString = "payeeid=" + transaction.getPid() + "|itc=" + transaction.getItc() + "|prn=" + transaction.getPrn() + "|date=" + transaction.getDate() + "|amt=" + transaction.getAmt()
				+ "|chksum=" + checksumHash;
		
		logger.info("Request For AxisBankNB Status Enquiry : "+payString+", And TxnKey : "+fields.get(FieldType.TXN_KEY.getName()));
		//String payStringEncrypted = axisBankNBEncDecService.AESEncrypt(payString, fields.get(FieldType.ADF9.getName()));
		//String payStringEncrypted = axisBankNBEncDecService.encrypt(fields.get(FieldType.ADF4.getName()), fields.get(FieldType.ADF5.getName()), fields.get(FieldType.ADF8.getName()), payString);
		String payStringEncrypted = AxisBankNBEncDecService.encryptStatusEnquiryReq(payString, fields.get(FieldType.TXN_KEY.getName()));
		
		logger.info("Encrypted Request For AxisBankNB Status Enquiry : "+payStringEncrypted);
		
		return payStringEncrypted;

	}

	public static String getResponse(String request, Fields fields) throws SystemException {

		String hostUrl = "";
		//Fields fields = new Fields();
		fields.put(FieldType.TXNTYPE.getName(), "STATUS");
		String xmlResponse = null;

		try {

			TransactionType transactionType = TransactionType.getInstance(fields.get(FieldType.TXNTYPE.getName()));
			switch (transactionType) {
			case SALE:
			case AUTHORISE:
				break;
			case ENROLL:
				break;
			case CAPTURE:
				break;
			case REFUND:
				break;
			case STATUS:
				hostUrl = PropertiesManager.propertiesMap.get(Constants.AXISBANK_NB_STATUS_ENQ_URL);
				break;
			default:
				break;
			}

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

	public void updateFields(Fields fields, String xmlResponse) {
		
		String decryptedResponse = null;
		try {
			 //decryptedResponse = axisBankNBEncDecService.AESDecrypt(xmlResponse, fields.get(FieldType.ADF9.getName()));
			decryptedResponse = AxisBankNBEncDecService.decryptStatusEnquiryResp(xmlResponse, fields.get(FieldType.TXN_KEY.getName()));
		}
		catch(Exception e){
			logger.error("Exception in parsing status enquiry response for axis bank nb ",e);
		}
		
		Transaction transaction = converter.toTransaction(decryptedResponse, TransactionType.STATUS.getName());
		AxisBankNBTransformer  axisBankTransformer = new AxisBankNBTransformer(transaction);
		axisBankTransformer.updateResponse(fields);
		

	}

}
