package com.pay10.federalNB;

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
import com.pay10.commons.util.FieldType;
import com.pay10.commons.util.Fields;
import com.pay10.commons.util.PropertiesManager;
import com.pay10.commons.util.TransactionType;
import com.pay10.pg.core.util.FederalNBChecksumUtil;


@Service
public class FederalBankNBStatusEnquiryProcessor {

	@Autowired
	@Qualifier("federalBankNBTransactionConverter")
	private TransactionConverter converter;
	
	@Autowired
	private FederalNBChecksumUtil federalNBChecksumUtil;
	
	@Autowired
	private FieldsDao fieldsDao;

//	@Autowired
//	private FederalBankNBEncDecService federalBankNBEncDecService;

	private static Logger logger = LoggerFactory.getLogger(FederalBankNBStatusEnquiryProcessor.class.getName());

	public void enquiryProcessor(Fields fields) {

		String response = "";
		try {
			// String saleDate =
			// fieldsDao.getCaptureDate(fields.get(FieldType.PG_REF_NUM.getName()));
			// fields.put(FieldType.CREATE_DATE.getName(), saleDate);
			String request = statusEnquiryRequest(fields);
			response = getResponse(request);
			logger.info("final federal bank NB status enquiry response" + response.toString());
		} catch (SystemException exception) {
			logger.error("Exception", exception);
		}

		updateFields(fields, response);

	}

	public String statusEnquiryRequest(Fields fields) {

		Transaction transaction = new Transaction();
		
		try {
		StringBuilder hash_data = new StringBuilder();
		StringBuilder sb = new StringBuilder();
		sb.append(Constants.API_KEY + "=" + fields.get(FieldType.TXN_KEY.getName()));
		hash_data.append( fields.get(FieldType.TXN_KEY.getName()));
		sb.append("&");
		sb.append(Constants.BANK_CODE + "=" + FederalBankNBMopType.getBankCode(fields.get(FieldType.MOP_TYPE.getName())));
		hash_data.append("|" + FederalBankNBMopType.getBankCode(fields.get(FieldType.MOP_TYPE.getName())));
		sb.append("&");
		sb.append(Constants.OID + "=" + fields.get(FieldType.PG_REF_NUM.getName())); // refund reference
																								// number
		hash_data.append("|" + fields.get(FieldType.PG_REF_NUM.getName()));
//		sb.append("&");
//		sb.append(Constants.TXN + "=" + fields.get(FieldType.ACQ_ID.getName()));
//		hash_data.append("|" + fields.get(FieldType.ACQ_ID.getName()));
		// getting hashing...
		logger.info("getting hash for Federal bank NB status enquiry request" + hash_data);
		String signature = federalNBChecksumUtil.getHashRefund(hash_data.toString(),
				fields.get(FieldType.ADF1.getName()));
		sb.append("&");
		sb.append(Constants.HASH + "=" + signature);
		logger.info("final federal bank NB status enquiry request" + sb.toString());
		return sb.toString();

	}

	catch (Exception e) {
		logger.error("Exception in generating federal bank NB status enquiry request", e);
		return null;
	}

	}

	public static String getResponse(String request) throws SystemException {

		String hostUrl = "";
		Fields fields = new Fields();
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
				hostUrl = PropertiesManager.propertiesMap.get(Constants.FEDERALBANK_NB_STATUS_ENQ_URL);
				break;
			default:
				break;
			}

			logger.info("Request message to federal bank net banking : TxnType = "
					+ fields.get(FieldType.TXNTYPE.getName())  + " Request >>>>> " + request);

			URL url = new URL(hostUrl);
			Map<String, Object> params = new LinkedHashMap<>();
//			params.put("encdata", request);
//			params.put("payeeid", fields.get(FieldType.ADF10.getName()));
//
//			StringBuilder postData = new StringBuilder();
//			for (Map.Entry<String, Object> param : params.entrySet()) {
//				if (postData.length() != 0)
//					postData.append('&');
//				postData.append(URLEncoder.encode(param.getKey(), "UTF-8"));
//				postData.append('=');
//				postData.append(URLEncoder.encode(String.valueOf(param.getValue()), "UTF-8"));
//			}
			StringBuilder postData = new StringBuilder();
			postData.append(request);
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
			logger.error("Exception in federalbank NB txn Communicator for status enquiry ", e);
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
		
		
		Transaction transaction = converter.toTransactionStatus(xmlResponse);
		FederalBankNBTransformer  federalBankTransformer = new FederalBankNBTransformer(transaction);
		federalBankTransformer.updateResponse(fields);
		

	}

}
