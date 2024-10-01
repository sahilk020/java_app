package com.pay10.idfc;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.google.gson.JsonObject;
import com.pay10.commons.dao.FieldsDao;
import com.pay10.commons.util.Amount;
import com.pay10.commons.util.FieldType;
import com.pay10.commons.util.Fields;
import com.pay10.commons.util.PropertiesManager;
import com.pay10.pg.core.util.IdfcChecksum;

@Service
public class IdfcbankNBStatusEnquiryProcessor {

	private static Logger logger = LoggerFactory.getLogger(IdfcbankNBStatusEnquiryProcessor.class.getName());

	@Autowired
	private FieldsDao fieldsDao;

	@Autowired
	private IdfcChecksum idfcChecksum;

	public  void enquiryProcessor(Fields fields) {
		Transaction transactionresponse = toTransaction(fields);
		String response = verficationRequest(transactionresponse, fields);
		logger.info("fields Idfc Status equiry action -------->>>" + fields.getFieldsAsString());
		logger.info(" response for idfc status enquiry action -------->>>" + response);


		Transaction transactionStatus = toTransactionStatus(response);
		updateResponseStatusenquiry(transactionStatus,fields);
		
	}

	public String verficationRequest(Transaction transactionresponse, Fields fields) {
		String response;
		String checkKey = fields.get(FieldType.ADF2.getName());
		String encrptKey = fields.get(FieldType.ADF1.getName());

		String checkString = transactionresponse.getMID() + "|" + transactionresponse.getPID() + "|"
				+ transactionresponse.getAMT() + "|REQUERY|"
				+ transactionresponse.getMCC();
		logger.info("checksum raw for dual verification and status enquiry" + checkString);
		String checksum = null;
		try {
			checksum = idfcChecksum.getSecureSHAHash(checkKey, checkString);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		logger.info("checksum enc for dual verification and status enquiry" + checkString);
		
		JSONArray request=new JSONArray();
		JSONObject MID = new JSONObject();
		MID.put("k","MID");
		MID.put("v", transactionresponse.getMID());
		request.put(MID);
		JSONObject PID = new JSONObject();
		PID.put("k","PID");
		PID.put("v", transactionresponse.getPID());
		request.put(PID);
		JSONObject AMT = new JSONObject();
		AMT.put("k","AMT");
		AMT.put("v", transactionresponse.getAMT());
		request.put(AMT);
		
		JSONObject TXNTYPE = new JSONObject();
		TXNTYPE.put("k","TXNTYPE");
		TXNTYPE.put("v", "REQUERY");
		request.put(TXNTYPE);
//		JSONObject BID = new JSONObject();
//		BID.put("k","BID");
//		BID.put("v", transactionresponse.getBID());
//		request.put(BID);
		JSONObject MCC = new JSONObject();
		MCC.put("k","MCC");
		MCC.put("v", transactionresponse.getMCC());
		request.put(MCC);
		
		JSONObject CHECKSUM = new JSONObject();
		CHECKSUM.put("k","CHECKSUM");
		CHECKSUM.put("v", checksum);
		request.put(CHECKSUM);
		
//		String request = "[{k:MID,v:" + transactionresponse.getMID() + "},{k:PID,v:" + transactionresponse.getPID()
//				+ "},{k:AMT,v:" + transactionresponse.getAMT() + "},{k:ACCNO,v:},{k:TXNTYPE,v:Verification},{k"
//				+ ":BID,v:" + transactionresponse.getBID() + "},{k:MCC,v:" + transactionresponse.getMCC()
//				+ "},{k:CRN,v:INR},{k:CHECKSUM,v:" + checksum + "}]";
		logger.info("IDFC request for  Raw sale" + request);
		String encryptedString = idfcChecksum.encrypt(request.toString(), encrptKey);
		logger.info("IDFC request for  enc sale" + encryptedString);
		JSONObject requestFinal = new JSONObject();
		requestFinal.put("isEncReq", "Y");
		requestFinal.put("mid", fields.get(FieldType.MERCHANT_ID.getName()));
		requestFinal.put("encParams", encryptedString);
		requestFinal.put("serviceName", "statusRequery");
		logger.info("IDFC request for  Raw sale" + requestFinal);

		String Response = CallRestApi(requestFinal.toString());
		JSONObject responseObj = new JSONObject(Response);
		
		Response = responseObj.getString("response");

		Response = idfcChecksum.decrypt(Response, encrptKey);
		return Response;
	}

	private Transaction toTransaction(Fields fields) {
		Transaction feildsToTrancation = new Transaction();
		// MID| PID| AMT| ACCNO| TXNTYPE| BID| MCC| CHECKSUM

		feildsToTrancation.setMID(fields.get(FieldType.MERCHANT_ID.getName()));
		feildsToTrancation.setPID(fields.get(FieldType.PG_REF_NUM.getName()));
		String toamount = Amount.toDecimal(fields.get(FieldType.TOTAL_AMOUNT.getName()),
				fields.get(FieldType.CURRENCY_CODE.getName()));
		feildsToTrancation.setAMT(toamount);
if(fields.get(FieldType.RRN.getName())==null) {
	feildsToTrancation.setBID("");

}else {
	feildsToTrancation.setBID(fields.get(FieldType.RRN.getName()));

}
		feildsToTrancation.setMCC(fields.get(FieldType.ADF3.getName()));

		return feildsToTrancation;
	}

	public String doubleVerification(Fields fields, Transaction transactionResponse) {

		String response = verficationRequest(transactionResponse, fields);
		return response;
	}

	private String CallRestApi(String request) {
		String hostUrl = PropertiesManager.propertiesMap.get("idfcStatusEnquiryUrl");

		String responseData = "";

		try {
			URL url = new URL(hostUrl);

			HttpURLConnection connection = (HttpURLConnection) url.openConnection();
			connection.setRequestMethod("POST");
			connection.setRequestProperty("Content-Type", "application/json");
			connection.setDoOutput(true);

			DataOutputStream requestWriter = new DataOutputStream(connection.getOutputStream());
			requestWriter.writeBytes(request);
			requestWriter.close();
			InputStream is = connection.getInputStream();
			BufferedReader responseReader = new BufferedReader(new InputStreamReader(is));
			if ((responseData = responseReader.readLine()) != null) {
				logger.info("Idfc response  Response >>> " + responseData);
			}
			// System.out.append("Request: " + post_data);
			responseReader.close();
			logger.info("data in idfc response " + responseData);
			return responseData;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return responseData;

	}

	@SuppressWarnings("unlikely-arg-type")
	public Transaction toTransactionStatus(String response) {
		Transaction transactionResponse =new Transaction();
//		ID|PID|AMT|ACCNO|TXNTYPE|BID|TXNSTATUS| RESPONSECODE|RESPONSEMSG
//		EXAMPLE: MER000023|456|12000.00|32109767|Verification|567| Success| SUC000|Success
		JSONArray getArray = new JSONArray(response);
		// MID|PID|AMT|TxnType|ACCNO|NAR|BID|ResponseCode|ResponseMsg|PAID

		for (int i = 0; i < getArray.length(); i++) {
			JSONObject obj = (JSONObject) getArray.get(i);
			if ((obj.get("k")).equals("PID")) {
				transactionResponse.setPID(obj.get("v").toString());

			}
			if ((obj.get("k")).equals("MID")) {
				transactionResponse.setMID(obj.get("v").toString());

			}
			if ((obj.get("k")).equals("AMT")) {
				transactionResponse.setAMT(obj.get("v").toString());

			}
			if ((obj.get("k")).equals("TXNTYPE")) {
				transactionResponse.setTxnType(obj.get("v").toString());

			}
			if ((obj.get("k")).equals("BID")) {
				transactionResponse.setBID(obj.get("v").toString());

			}

			if ((obj.get("k")).equals("RESPONSECODE")) {
				transactionResponse.setResponseCode(obj.get("v").toString());

			}

			if ((obj.get("k")).equals("RESPONSEMSG")) {
				transactionResponse.setResponseMsg(obj.get("v").toString());

			}
			
		}

		return transactionResponse;
	}
	public void updateResponseStatusenquiry(Transaction transactionStatus, Fields fields) {
		
		String amount = Amount.toDecimal(fields.get(FieldType.TOTAL_AMOUNT.getName()), "356");

	 if (fields.get(FieldType.PG_REF_NUM.getName()).equalsIgnoreCase(transactionStatus.getPID())
				 &&amount.equalsIgnoreCase(transactionStatus.getAMT())) {

		idfcTransformer idfctransformer = new idfcTransformer(transactionStatus);
		idfctransformer.updateResponse(fields);
	 }

	}

}
