package com.pay10.atom;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pay10.commons.exception.ErrorType;
import com.pay10.commons.exception.SystemException;
import com.pay10.commons.util.*;
import com.pay10.pg.core.util.AtomEncDecUtil;
import com.pay10.pg.core.util.ProcessManager;
import com.pay10.pg.core.util.Processor;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.json.simple.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;

@Service
public class AtomSaleResponseHandler {

	private static Logger logger = LoggerFactory.getLogger(AtomSaleResponseHandler.class.getName());

	@Autowired
	private Validator generalValidator;

	@Autowired
	@Qualifier("updateProcessor")
	private Processor updateProcessor;

	@Autowired
	private AtomEncDecUtil atomEncDecUtil;

	@Autowired
	private ObjectMapper mapper;
	

	public Map<String, String> process(Fields fields) throws SystemException {
		
		String newTxnId = TransactionManager.getNewTransactionId();
		fields.put(FieldType.TXN_ID.getName(), newTxnId);
		generalValidator.validate(fields);
		Transaction transactionResponse = new Transaction();
		String response = fields.get(FieldType.ATOM_RESPONSE_FIELD.getName());
		transactionResponse = toTransaction(response);
		/*String[] paramatersRespo = response.split("&");
		Map<String, String> paramMapRespo = new HashMap<String, String>();

		//db
		for (String param : paramatersRespo) {
			String[] parameterPair = param.split("=");
			if (parameterPair.length > 1) {
				paramMapRespo.put(parameterPair[0], parameterPair[1]);
				logger.info("paramMapRespo.put(parameterPair[0], parameterPair[1] :" + paramMapRespo.put(parameterPair[0], parameterPair[1]));

			}
		}
		String desc = paramMapRespo.get("desc");*/
		boolean doubleVer = doubleVerification(transactionResponse, fields);
		//logger.info("process:: doubleVerification={}, pgRefNo={} ", doubleVer, fields.get(FieldType.PG_REF_NUM.getName()));
		logger.info("ATOM DoubleVerification Process:: doubleVerification={}, pgRefNo={} ", doubleVer, 	fields.get(FieldType.PG_REF_NUM.getName()));
		if (doubleVer) {

			AtomTransformer atomTransformer = new AtomTransformer(transactionResponse);
			atomTransformer.updateResponse(fields);

		} else {
			fields.put(FieldType.STATUS.getName(), StatusType.DENIED.getName());
			fields.put(FieldType.RESPONSE_CODE.getName(), ErrorType.SIGNATURE_MISMATCH.getCode());
			//fields.put(FieldType.RESPONSE_MESSAGE.getName(), ErrorType.SIGNATURE_MISMATCH.getResponseMessage());
			fields.put(FieldType.RESPONSE_MESSAGE.getName(), transactionResponse.getDesc());
		}
		//AtomTransformer atomTransformer = new AtomTransformer(transactionResponse);
		//atomTransformer.updateResponse(fields);

		fields.put(FieldType.INTERNAL_ORIG_TXN_TYPE.getName(), fields.get(FieldType.TXNTYPE.getName()));
		fields.remove(FieldType.ATOM_RESPONSE_FIELD.getName());
		ProcessManager.flow(updateProcessor, fields, true);
		return fields.getFields();

	}
	
	private boolean doubleVerification(Transaction transactionResponse, Fields fields) {

		try {
			logger.info("Transaction Response Before Double Verification :: " + transactionResponse.toString());
			// Skip for unsuccessful transactions if
			if (!transactionResponse.getF_code().equalsIgnoreCase("Ok")) {
				return true;
			}
			String encrequest = statusEnquiryRequest(fields, transactionResponse);

			String response = getResponse(fields, encrequest);
			logger.info("Double Verification Response For ATOM Acquirer :: " + response);

			Transaction doubleVerTxnResponse = doubleVerificationToTransaction(response);

			if (doubleVerTxnResponse == null) {
				logger.info("doubleVerification:: transaction response is null for atom acquirer.");
				return false;
			}
			logger.info("Transaction Response For Double Verification :: " + doubleVerTxnResponse.toString());

			if (StringUtils.isBlank(doubleVerTxnResponse.getAmt())
					|| StringUtils.isBlank(transactionResponse.getAmt())) {
				logger.info("doubleVerification:: amount={}, doubleVerificationAmount={}", transactionResponse.getAmt(),
						doubleVerTxnResponse.getAmt());
				return false;
			}

			String fieldsAmount = Amount.toDecimal(fields.get(FieldType.TOTAL_AMOUNT.getName()),
					fields.get(FieldType.CURRENCY_CODE.getName()));

			logger.info("dual verfication for ATOM response txnid={},amount={}", transactionResponse.getMer_txn(),
					fieldsAmount);

			/*
			 * if (transactionResponse.getUdf6()!= null &&
			 * transactionResponse.getUdf6().equalsIgnoreCase("SUCCESS")
			 * &&fields.get(FieldType.PG_REF_NUM.getName()).equalsIgnoreCase(
			 * transactionResponse.getUdf5())
			 * &&toamount.equalsIgnoreCase(transactionResponse.getAmt())) {
			 * 
			 * return true; }
			 */

			// UDF5 = PGREFNUM,
			if (doubleVerTxnResponse.getUdf6().equalsIgnoreCase("SUCCESS")
					&& doubleVerTxnResponse.getMer_txn().equalsIgnoreCase(transactionResponse.getMer_txn())
					&& doubleVerTxnResponse.getAmt().equalsIgnoreCase(transactionResponse.getAmt())
					&& doubleVerTxnResponse.getAmt().equalsIgnoreCase(fieldsAmount)
					&& doubleVerTxnResponse.getMmp_txn().equalsIgnoreCase(transactionResponse.getMmp_txn())) {

				return true;
			}
			logger.info("DoubleVerification Failed For ATOM ::  Status={},  resAmount={},pgRefNo={}",
					doubleVerTxnResponse.getUdf6(), doubleVerTxnResponse.getAmt(), doubleVerTxnResponse.getUdf5());

			return false;

		}

		catch (Exception e) {
			logger.error("Exceptionn ", e);
			return false;
		}

	}
	
	/*
	 * private boolean doubleVerification(Transaction transactionResponse, Fields
	 * fields) {
	 * 
	 * try { // Skip for unsuccessful transactions if if
	 * (!transactionResponse.getF_code().equalsIgnoreCase("Ok")) { return true; }
	 * String encrequest = statusEnquiryRequest(fields,transactionResponse);
	 * 
	 * String response = getResponse(encrequest);
	 * logger.info("response for atom"+response);
	 * 
	 * transactionResponse = toTransaction(response);
	 * 
	 * String toamount =
	 * Amount.toDecimal(fields.get(FieldType.TOTAL_AMOUNT.getName()),
	 * fields.get(FieldType.CURRENCY_CODE.getName())); if
	 * (transactionResponse.getUdf6()!= null &&
	 * transactionResponse.getUdf6().equalsIgnoreCase("SUCCESS")
	 * &&fields.get(FieldType.PG_REF_NUM.getName()).equalsIgnoreCase(
	 * transactionResponse.getUdf5())
	 * &&toamount.equalsIgnoreCase(transactionResponse.getAmt())) {
	 * 
	 * return true; } logger.info(
	 * "doubleVerification:: failed. Status={},  resAmount={},pgRefNo={}",
	 * transactionResponse.getUdf6(), transactionResponse.getAmt(),
	 * transactionResponse.getUdf5());
	 * 
	 * return false;
	 * 
	 * }
	 * 
	 * catch (Exception e) { logger.error("Exceptionn ", e); return false; }
	 * 
	 * }
	 */
	
	private Transaction doubleVerificationToTransaction(String response) throws IOException {
		Transaction transaction = new Transaction();
		if (StringUtils.isBlank(response)) {
			return transaction;
		}
		List<?> responseList = mapper.readValue(response, ArrayList.class);
		JSONObject responseJSON = new JSONObject((Map) responseList.get(0));
		logger.info("DoubleVerificationToTransaction ResponseMap : {}" , responseJSON);
		transaction.setUdf6(String.valueOf(responseJSON.get("verified")));
		transaction.setUdf5(String.valueOf(responseJSON.get("merchantTxnID")));
		transaction.setMmp_txn(String.valueOf(responseJSON.get("atomTxnId")));
		transaction.setMer_txn(String.valueOf(responseJSON.get("merchantTxnID")));
		transaction.setAmt(String.valueOf(responseJSON.get("amt")));
		transaction.setMerchant_id(String.valueOf(responseJSON.get("merchantID")));
		transaction.setBank_name(String.valueOf(responseJSON.get("bankName")));
		transaction.setBank_txn(String.valueOf(responseJSON.get("bid")));
		transaction.setDiscriminator(String.valueOf(responseJSON.get("discriminator")));
		transaction.setData(String.valueOf(responseJSON.get("txnDate")));
		transaction.setSurcharge(String.valueOf(responseJSON.get("surcharge")));
		logger.info("DoubleVerificationToTransaction Transaction Data :: {}",
				transaction.toString());
		return transaction;
	}
	
	public String getResponse(Fields fields, String request) throws SystemException {

		try {
			String hostUrl = PropertiesManager.propertiesMap.get(Constants.ATOM_STATUS_ENQ_URL);
			logger.info("Request message to ATOM : request = " + request);
			CloseableHttpClient httpclient = HttpClients.createDefault();
			HttpGet httpget = new HttpGet(hostUrl + "?" + request);
			CloseableHttpResponse resp = httpclient.execute(httpget);
			String inqRes = EntityUtils.toString(resp.getEntity());
			httpclient.close();
			logger.info("Response received for Atom Response >>> " + inqRes);

			String salt = fields.get(FieldType.ADF5.getName());
			String pass = fields.get(FieldType.ADF8.getName());
			return atomEncDecUtil.decrypt(inqRes, pass, salt);
		} catch (Exception e) {
			logger.error("Exception in ATOM txn Communicator", e);
		}
		return null;

	}

	public Transaction toTransaction(String response) {

		Transaction transaction = new Transaction();
		
		if (StringUtils.isBlank(response)) {
			return transaction;
		}
		
		String responseArray [] = response.split("&");
		Map<String,String> responseMap = new HashMap<String,String>();
		
		
		for (String data:responseArray ) {
		
			String dataArray [] = data.split("=");
			
			// Auth Code Sent is blank , hence check array length > 1 for response map
			if (dataArray.length > 1 ) {
				responseMap.put(dataArray[0], dataArray[1]);
			}
			
		}
		
		if (StringUtils.isNotBlank(responseMap.get(Constants.DESC))) {
			transaction.setDesc(responseMap.get(Constants.DESC));
		}
		if (StringUtils.isNotBlank(responseMap.get("VERIFIED"))) {
			transaction.setUdf6(responseMap.get("VERIFIED"));;
		}
		
		if (StringUtils.isNotBlank(responseMap.get("MerchantTxnID"))) {
			transaction.setUdf5(responseMap.get("MerchantTxnID"));;
		}
		if (StringUtils.isNotBlank(responseMap.get(Constants.MMP_TXN))) {
			transaction.setMmp_txn(responseMap.get(Constants.MMP_TXN));
		}
		if (StringUtils.isNotBlank(responseMap.get(Constants.MMP_TXN))) {
			transaction.setMmp_txn(responseMap.get(Constants.MMP_TXN));
		}
		
		if (StringUtils.isNotBlank(responseMap.get(Constants.MMP_TXN))) {
			transaction.setMmp_txn(responseMap.get(Constants.MMP_TXN));
		}
		
		if (StringUtils.isNotBlank(responseMap.get(Constants.MER_TXN))) {
			transaction.setMer_txn(responseMap.get(Constants.MER_TXN));
		}
		
		if (StringUtils.isNotBlank(responseMap.get(Constants.AMT))) {
			transaction.setAmt(responseMap.get(Constants.AMT));
		}
		if (StringUtils.isNotBlank(responseMap.get("AMT"))) {
			transaction.setAmt(responseMap.get("AMT"));
		}
		if (StringUtils.isNotBlank(responseMap.get(Constants.PROD))) {
			transaction.setProd(responseMap.get(Constants.PROD));
		}
		
		if (StringUtils.isNotBlank(responseMap.get(Constants.BANK_TXN))) {
			transaction.setBank_txn(responseMap.get(Constants.BANK_TXN));
		}
		
		if (StringUtils.isNotBlank(responseMap.get(Constants.F_CODE))) {
			transaction.setF_code(responseMap.get(Constants.F_CODE));
		}
		
		if (StringUtils.isNotBlank(responseMap.get(Constants.CLIENTCODE))) {
			transaction.setClientcode(responseMap.get(Constants.CLIENTCODE));
		}
		
		if (StringUtils.isNotBlank(responseMap.get(Constants.BANK_NAME))) {
			transaction.setBank_name(responseMap.get(Constants.BANK_NAME));
		}
		
		if (StringUtils.isNotBlank(responseMap.get(Constants.MERCHANT_ID))) {
			transaction.setMerchant_id(responseMap.get(Constants.MERCHANT_ID));
		}
		if (StringUtils.isNotBlank(responseMap.get(Constants.MERCHANT_ID))) {
			transaction.setMerchant_id(responseMap.get(Constants.MERCHANT_ID));
		}
		if (StringUtils.isNotBlank(responseMap.get("MerchantID"))) {
			transaction.setMerchant_id(responseMap.get("MerchantID"));
		
		if (StringUtils.isNotBlank(responseMap.get(Constants.DISCRIMINATOR))) {
			transaction.setDiscriminator(responseMap.get(Constants.DISCRIMINATOR));
		}
		
		
		
		if (StringUtils.isNotBlank(responseMap.get(Constants.AUTH_CODE))) {
			transaction.setAuth_code(responseMap.get(Constants.AUTH_CODE));
		}
		
		if (StringUtils.isNotBlank(responseMap.get(Constants.IPG_TXN_ID))) {
			transaction.setIpg_txn_id(responseMap.get(Constants.IPG_TXN_ID));
		}
		
		if (StringUtils.isNotBlank(responseMap.get("TxnDate"))) {
			transaction.setData(responseMap.get("TxnDate"));
		}
		if (StringUtils.isNotBlank(responseMap.get(Constants.TXNDATE))) {
			transaction.setData(responseMap.get(Constants.TXNDATE));
		}
		if (StringUtils.isNotBlank(responseMap.get(Constants.SURCHARGE))) {
			transaction.setSurcharge(responseMap.get(Constants.SURCHARGE));
		}
		if (StringUtils.isNotBlank(responseMap.get(Constants.SIGNATURE))) {
			transaction.setSignature(responseMap.get(Constants.SIGNATURE));
		}
		
		if (StringUtils.isNotBlank(responseMap.get(Constants.MERCHANT_TXN_ID))) {
			transaction.setIpg_txn_id(responseMap.get(Constants.MERCHANT_TXN_ID));
		}
		}
		logger.info("data"+transaction.toString());

		return transaction;
		
	}

	public String statusEnquiryRequest(Fields fields, Transaction transactionResponse) throws Exception {
		String pattern = "yyyy-MM-dd";
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);

		String date = simpleDateFormat.format(new Date());
		StringBuilder stsEnqString = new StringBuilder();

		stsEnqString.append(Constants.MERCHANTID + "=");
		stsEnqString.append(transactionResponse.getMerchant_id());
		stsEnqString.append("&" + Constants.MERCHANT_TXN_ID + "=");
		stsEnqString.append(transactionResponse.getMer_txn());
		stsEnqString.append("&" + Constants.AMT + "=");
		stsEnqString.append(transactionResponse.getAmt());
		stsEnqString.append("&" + Constants.TDATE + "=");
		stsEnqString.append(date);
		logger.info("Double Verification Request for ATOM Acquirer : {}", stsEnqString.toString());
		String salt = fields.get(FieldType.ADF3.getName());
		String pass = fields.get(FieldType.ADF4.getName());
		return StringUtils.join(Constants.LOGIN, "=",
				transactionResponse.getMerchant_id(), "&", Constants.ENCDATA, "=",
				atomEncDecUtil.encrypt(stsEnqString.toString(), pass, salt));
	}


	// toTransaction()
	
	
	
	/*
	 * private boolean doubleVerification(String transactionResponse, Fields fields)
	 * throws SystemException {
	 * 
	 * try {
	 * 
	 * JSONObject resJson = new JSONObject(transactionResponse);
	 * 
	 * // Skip if txStatus is not present in response if
	 * (!transactionResponse.contains(Constants.txStatus)){ return true; }
	 * 
	 * // Skip for unsuccessful transactions if if
	 * (transactionResponse.contains(Constants.txStatus) &&
	 * !resJson.get(Constants.txStatus).toString().equalsIgnoreCase("0")) { return
	 * true; }
	 * 
	 * String request = transactionConverter.statusEnquiryRequest(fields, null);
	 * String hostUrl =
	 * PropertiesManager.propertiesMap.get(Constants.STATUS_ENQ_REQUEST_URL); String
	 * response = transactionCommunicator.statusEnqPostRequest(request, hostUrl);
	 * 
	 * logger.info("Bank Response = " + transactionResponse);
	 * logger.info("Double Verification Response = " + response);
	 * 
	 * 
	 * JSONObject resJsonBank = new JSONObject(response); //only for live if
	 * ((resJson.get(Constants.txStatus).toString().equals(resJsonBank.get(Constants
	 * .txStatus).toString())) &&
	 * (resJson.get(Constants.orderAmount).toString().equals(resJsonBank.get(
	 * Constants.orderAmount).toString()))) { return true; } else {
	 * logger.info("Double Verification Response donot match for Agreepay"); return
	 * false; }
	 * 
	 * }catch (Exception e) { logger.error("Exceptionn ", e); return false; }
	 * 
	 * 
	 * 
	 * 
	 * }
	 */


}
