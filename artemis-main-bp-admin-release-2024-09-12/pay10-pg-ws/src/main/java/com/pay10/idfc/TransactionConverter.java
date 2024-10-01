package com.pay10.idfc;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.pay10.commons.dao.FieldsDao;
import com.pay10.commons.exception.SystemException;
import com.pay10.commons.util.Amount;
import com.pay10.commons.util.FieldType;
import com.pay10.commons.util.Fields;
import com.pay10.commons.util.PropertiesManager;
import com.pay10.commons.util.TransactionType;
import com.pay10.pg.core.util.AcquirerTxnAmountProvider;
import com.pay10.pg.core.util.IdfcChecksum;

/**
 * @author Shaiwal
 *
 */
@Service("idfcTransactionConverter")
public class TransactionConverter {
	private static Logger logger = LoggerFactory.getLogger(TransactionConverter.class.getName());

	@Autowired
	private AcquirerTxnAmountProvider acquirerTxnAmountProvider;

	@Autowired
	private IdfcChecksum idfcChecksum;
	
	@Autowired
	private FieldsDao fieldsDao;

	@SuppressWarnings("incomplete-switch")
	public String perpareRequest(Fields fields) throws SystemException {

		String request = null;

		switch (TransactionType.getInstance(fields.get(FieldType.TXNTYPE.getName()))) {
		case AUTHORISE:
		case ENROLL:
			break;
		case REFUND:
			request = refundRequest(fields);
			break;
		case SALE:
			request = saleRequest(fields);
			break;
		case CAPTURE:
			break;

		case SETTLE:
			break;
		case STATUS:
			// request = statusEnquiryRequest(fields, transaction);
			break;
		}
		return request.toString();

	}

	public String refundRequest(Fields fields) {
		JSONObject refundRequest = new JSONObject();
		try {
			String date = fieldsDao.getDateForPgRefNum(fields.get(FieldType.PG_REF_NUM.getName()));
			logger.info("fields in idfc bank refund"+date);

			logger.info("fields in idfc bank refund"+fields.getFieldsAsString());
			String checkkey = fields.get(FieldType.ADF2.getName());
			String EnycrtpKey = fields.get(FieldType.ADF1.getName());
			String baseamount=Amount.toDecimal(fields.get(FieldType.TOTAL_AMOUNT.getName()),
					fields.get(FieldType.CURRENCY_CODE.getName()));
			String refundamount=Amount.toDecimal(fields.get(FieldType.AMOUNT.getName()),
					fields.get(FieldType.CURRENCY_CODE.getName()));
			String mid=fields.get(FieldType.MERCHANT_ID.getName());
			String pid=fields.get(FieldType.PG_REF_NUM.getName());
			String Mcc=fields.get(FieldType.ADF3.getName());
			refundRequest.put("mid", fields.get(FieldType.MERCHANT_ID.getName()));
			refundRequest.put("pid", fields.get(FieldType.PG_REF_NUM.getName()));
			refundRequest.put("txnAmt", baseamount);
			refundRequest.put("refundAmt", refundamount);

			refundRequest.put("txnDate", date);
			refundRequest.put("txnType", "Refund");
			refundRequest.put("accNo", "");
			refundRequest.put("mcc", fields.get(FieldType.ADF3.getName()));
			logger.info("refund request for checksum" + refundRequest);
			String checkstring=mid+"|"+pid+"|"+baseamount+"|"+refundamount+"|"+date+"|Refund||"+Mcc;
			logger.info("refund request for checksum" + checkstring);
			
			String checksum = idfcChecksum.getSecureSHAHash(checkkey, checkstring);
			logger.info("refund request genrated checksum" + checksum);

			refundRequest.put("checksum", checksum);
			logger.info("refund request for encryption " + refundRequest);
						return refundRequest.toString();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return refundRequest.toString();
	}

	public String saleRequest(Fields fields) throws SystemException {

		StringBuilder requestString = new StringBuilder();
		String encryptedString = null;
		try {
			logger.info("Feilds data in idfc sale request " + fields.getFieldsAsString());

			if(StringUtils.isBlank(fields.get(FieldType.PG_REF_NUM.getName())) ){
				fields.put(FieldType.PG_REF_NUM.getName(), fields.get(FieldType.TXN_ID.getName()));

				
			}
			logger.info("Feilds data in idfc sale request " + fields.getFieldsAsString());
			String amount = acquirerTxnAmountProvider.amountProvider(fields);
			String Merchnatkey = fields.get(FieldType.MERCHANT_ID.getName());
			String PID = fields.get(FieldType.PG_REF_NUM.getName());
			String MCC = fields.get(FieldType.ADF3.getName());
			String checkkey = fields.get(FieldType.ADF2.getName());
			String EnycrtpKey = fields.get(FieldType.ADF1.getName());
			
			String url = PropertiesManager.propertiesMap.get("idfcNBReturnUrl");

			// MID|PID|AMT|RetURL|TxnType|ACCNO|NAR|Channel|MCC|CRN

			String checkString = Merchnatkey + "|" + PID + "|" + amount + "|" + url + "|payment||PAY10|01|" + MCC
					+ "|INR";
			logger.info(" idfc sale request before  checksum " + checkString);

			String checksum = idfcChecksum.getSecureSHAHash(checkkey, checkString);
			logger.info(" idfc sale request  checksum " + checksum);
			JSONArray request=new JSONArray();
			JSONObject MID = new JSONObject();
			MID.put("v", Merchnatkey);
			MID.put("k","MID");
			request.put(MID);
			JSONObject PIDJ = new JSONObject();
			PIDJ.put("v", PID);
			PIDJ.put("k","PID");
			request.put(PIDJ);
			JSONObject Amt = new JSONObject();
			Amt.put("v", amount);
			Amt.put("k","AMT");
			request.put(Amt);
			JSONObject returlurl = new JSONObject();
			returlurl.put("v", url);
			returlurl.put("k","RETURL");
			request.put(returlurl);
			JSONObject Txntype = new JSONObject();
			Txntype.put("v", "payment");
			Txntype.put("k","TXNTYPE");
			request.put(Txntype);
			JSONObject Acountno = new JSONObject();
			Acountno.put("v", "");
			Acountno.put("k","ACCNO");
			request.put(Acountno);
			JSONObject NAR = new JSONObject();
			NAR.put("v", "PAY10");
			NAR.put("k","NAR");
			request.put(NAR);
			JSONObject channel = new JSONObject();
			channel.put("v", "01");
			channel.put("k","CHANNEL");
			request.put(channel);
			JSONObject MCCJson = new JSONObject();
			MCCJson.put("v", fields.get(FieldType.ADF3.getName()));
			MCCJson.put("k","MCC");
			request.put(MCCJson);
			JSONObject CRN = new JSONObject();
			CRN.put("v", "INR");
			CRN.put("k","CRN");
			request.put(CRN);
			JSONObject CHECKSUM = new JSONObject();
			CHECKSUM.put("v",checksum );
			CHECKSUM.put("k","CHECKSUM");
			request.put(CHECKSUM);

			System.out.println(PID);

//			String request = "[{k:MID,v:" + Merchnatkey + "},{k:PID,v:" + PID + "},{k:AMT,v:" + amount
//					+ "},{k:RETURL,v:" + url + "},{k:TXNTYPE,v:payment},{k:ACCNO,v:},{k:NAR,v:" + "PAY10" + "},{k"
//					+ ":CHANNEL,v:01},{k:MCC,v:" + fields.get(FieldType.ADF3.getName())
//					+ "},{k:CRN,v:INR},{k:CHECKSUM,v:" + checksum + "}]";
			logger.info("raw request before encryption " + request);
			logger.info("raw request before encryption " + request.toString());

			encryptedString = idfcChecksum.encrypt(request.toString(), EnycrtpKey);
			logger.info("request for idfc in enc data" + encryptedString);

			return encryptedString;

		}

		catch (Exception e) {
			logger.error("Exception in generating idfc Bank NB Sale Request ", e);
			return null;
		}

	}
	
	
	 public String RestApiCall(Fields fields, String request) {
		 
		 String response = "";

			try {
				String hostUrl = PropertiesManager.propertiesMap.get("IdfcRefundUrl");
				HttpURLConnection connection = null;
				URL url;
				System.out.println(request + "url ---- > " + hostUrl);
				url = new URL(hostUrl);
				connection = (HttpURLConnection) url.openConnection();
				connection.setRequestMethod("POST");
				connection.setRequestProperty("Content-Type", "application/json");
				connection.setRequestProperty("cache-control", "no-cache");

				connection.setUseCaches(false);
				connection.setDoInput(true);
				connection.setDoOutput(true);
				connection.setConnectTimeout(60000);
				connection.setReadTimeout(60000);

				// Send request
				DataOutputStream wr = new DataOutputStream(connection.getOutputStream());
				wr.writeBytes(request);
				// wr.flush();
				wr.close();

				// Get Response
				int code = ((HttpURLConnection) connection).getResponseCode();
				System.out.println("input-------->" + code);
				InputStream is = connection.getInputStream();

				BufferedReader bufferedreader = new BufferedReader(new InputStreamReader(is));
				String decodedString;

				while ((decodedString = bufferedreader.readLine()) != null) {
					response = response + decodedString;
				}

				bufferedreader.close();

				logger.info("Idfc netbanking -Response >> " + response);
				return response;

			} catch (Exception e) {
				logger.error("Exception in getting token respose for Pinelabs", e);

		 
	 }
			return response;

	 }

	public Transaction toTransaction(String response, Transaction transactionResponse) {
		logger.info(">>>>>>>>>>>>>>>>>>>>>>>>>>"+response);

        JSONObject responsedata = new JSONObject(response);
        transactionResponse.setBID(responsedata.get("BID").toString());
        transactionResponse.setPID(responsedata.get("PID").toString());
        transactionResponse.setTXNAMT(responsedata.get("TXNAMT").toString());
        transactionResponse.setREFUNDAMT(responsedata.get("REFUNDAMT").toString());
        transactionResponse.setTxnType(responsedata.get("TxnType").toString());
        transactionResponse.setMID(responsedata.get("MID").toString());
        transactionResponse.setMCC(responsedata.get("MCC").toString());
        transactionResponse.setResponseCode(responsedata.get("ResponseCode").toString());
        transactionResponse.setResponseMsg(responsedata.get("ResponseMsg").toString());
		logger.info(">>>>>>>>>>>>>>>>>>>>>>>>>>"+transactionResponse.toString());



		return transactionResponse;
	}
}
