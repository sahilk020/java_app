package com.pay10.paytm;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.pay10.commons.dao.FieldsDao;
import com.pay10.commons.exception.ErrorType;
import com.pay10.commons.exception.SystemException;
import com.pay10.commons.util.FieldType;
import com.pay10.commons.util.Fields;
import com.pay10.commons.util.PropertiesManager;
import com.pay10.commons.util.StatusType;
import com.pay10.commons.util.TransactionType;
import com.pay10.pg.core.paytm.util.CheckSumServiceHelper;

@Service
public class PaytmStatusEnquiryProcessor {

	@Autowired
	@Qualifier("paytmTransactionConverter")
	private TransactionConverter converter;

	@Autowired
	private FieldsDao fieldsDao;


	private static Logger logger = LoggerFactory.getLogger(PaytmStatusEnquiryProcessor.class.getName());

	public void enquiryProcessor(Fields fields) {
		String request = statusEnquiryRequest(fields);
		String response = "";
		try {

			response = getResponse(request);
			updateFields(fields, response);

		} catch (SystemException exception) {
			logger.error("Exception", exception);
		} catch (Exception e) {
			logger.error("Exception in decrypting status enquiry response for paytm ", e);
		}

	}

	public String statusEnquiryRequest(Fields fields) {

		try {

			JSONObject paytmParams = new JSONObject();

			JSONObject body = new JSONObject();
			body.put("mid", fields.get(FieldType.MERCHANT_ID.getName()));
			body.put("orderId", fields.get(FieldType.PG_REF_NUM.getName()));
			
			String checksum = new CheckSumServiceHelper().getCheckSumServiceHelper().genrateCheckSum(fields.get(FieldType.TXN_KEY.getName()),
					body.toString());
			
			JSONObject head = new JSONObject();
			head.put("signature", checksum);
			
			paytmParams.put("body", body);
			paytmParams.put("head", head);
			String post_data = paytmParams.toString();
			
			return post_data;
		}

		catch (Exception e) {
			logger.error("Exception in preparing Paytm Status Enquiry Request", e);
		}

		return null;

	}

	public static String getResponse(String request) throws SystemException {

		if (StringUtils.isBlank(request)) {
			logger.info("Request is empty for Paytm status enquiry");
			return null;
		}

		String hostUrl = "";
		Fields fields = new Fields();
		fields.put(FieldType.TXNTYPE.getName(), "STATUS");

		try {

			hostUrl = PropertiesManager.propertiesMap.get(Constants.PAYTM_STATUS_ENQ_URL);

			logger.info("Status Enquiry Request to Paytm : TxnType = " + fields.get(FieldType.TXNTYPE.getName()) + " "
					+ "Order Id = " + fields.get(FieldType.ORDER_ID.getName()) + " " + request);
			URL url = new URL(hostUrl);
			
			HttpURLConnection connection = (HttpURLConnection) url.openConnection();
			connection.setRequestMethod("POST");
			connection.setRequestProperty("Content-Type", "application/json");
			connection.setDoOutput(true);

			DataOutputStream requestWriter = new DataOutputStream(connection.getOutputStream());
			requestWriter.writeBytes(request);
			requestWriter.close();
			String responseData = "";
			InputStream is = connection.getInputStream();
			BufferedReader responseReader = new BufferedReader(new InputStreamReader(is));
			if ((responseData = responseReader.readLine()) != null) {
				logger.info("Response received for paytm status enq : " + responseData);
			}
			// System.out.append("Request: " + post_data);
			responseReader.close();
			
			return responseData;
			
		} catch (Exception e) {
			logger.error("Exception in Paytm Status Enquiry ", e);
		}
		return null;
	}

	public void updateFields(Fields fields, String jsonResponse) {

		Transaction transaction = new Transaction();

		transaction = toTransaction(jsonResponse, fields.get(FieldType.INTERNAL_ORIG_TXN_TYPE.getName()));
		
		String status = null;
		ErrorType errorType = null;
		String pgTxnMsg = null;
		
		if (fields.get(FieldType.INTERNAL_ORIG_TXN_TYPE.getName()).equalsIgnoreCase(TransactionType.SALE.getCode())) {
			
			if ((StringUtils.isNotBlank(transaction.getResultCode())) &&
					((transaction.getResultCode()).equalsIgnoreCase("01")))


			{
				status = StatusType.CAPTURED.getName();
				errorType = ErrorType.SUCCESS;
				
				if (StringUtils.isNotBlank(transaction.getResultMsg())){
					pgTxnMsg = transaction.getResultMsg();
				}
				
				else {
					pgTxnMsg = ErrorType.SUCCESS.getResponseMessage();
				}
				

			}
			
			
			else {
				if ((StringUtils.isNotBlank(transaction.getResultCode()))) {

					String respCode = null;
					if (StringUtils.isNotBlank(transaction.getResultCode())){
						respCode = transaction.getResultCode();
					}
					
					PaytmResultType resultInstance = PaytmResultType.getInstanceFromName(respCode);
					
					if (resultInstance != null) {
						status = resultInstance.getStatusCode();
						errorType = ErrorType.getInstanceFromCode(resultInstance.getiPayCode());
						
						if (StringUtils.isNotBlank(transaction.getResultMsg())){
							pgTxnMsg = transaction.getResultMsg();
						}
						
						else {
							pgTxnMsg = resultInstance.getMessage();
						}
						
					} else {
						status = StatusType.REJECTED.getName();
						errorType = ErrorType.getInstanceFromCode("007");
						
						if (StringUtils.isNotBlank(transaction.getResultMsg())){
							pgTxnMsg = transaction.getResultMsg();
						}
						
						else {
							pgTxnMsg = "Transaction Declined";
						}
						
					}

				} else {
					status = StatusType.REJECTED.getName();
					errorType = ErrorType.REJECTED;
					if (StringUtils.isNotBlank(transaction.getResultMsg())){
						pgTxnMsg = transaction.getResultMsg();
					}
					else {
						pgTxnMsg = "Transaction Rejected";
					}

				}
			}
			
			fields.put(FieldType.STATUS.getName(), status);
			fields.put(FieldType.RESPONSE_MESSAGE.getName(), errorType.getResponseMessage());
			fields.put(FieldType.RESPONSE_CODE.getName(), errorType.getResponseCode());

			fields.put(FieldType.RRN.getName(), transaction.getBankTxnId());
			fields.put(FieldType.ACQ_ID.getName(), transaction.getTxnId());
			fields.put(FieldType.PG_RESP_CODE.getName(), transaction.getResultCode());
			
			if (StringUtils.isNotBlank(transaction.getResultStatus())) {
				fields.put(FieldType.PG_TXN_STATUS.getName(), transaction.getResultStatus());
			}
			else {
				fields.put(FieldType.PG_TXN_STATUS.getName(), errorType.getResponseCode());
			}
			
			fields.put(FieldType.PG_TXN_MESSAGE.getName(), pgTxnMsg);
			
			
		}

		if (fields.get(FieldType.INTERNAL_ORIG_TXN_TYPE.getName()).equalsIgnoreCase(TransactionType.REFUND.getCode())) {
			
			if ((StringUtils.isNotBlank(transaction.getResultCode())) &&
					(((transaction.getResultCode()).equalsIgnoreCase("601") || (transaction.getResultCode()).equalsIgnoreCase("10"))))


			{
				status = StatusType.CAPTURED.getName();
				errorType = ErrorType.SUCCESS;
				pgTxnMsg = ErrorType.SUCCESS.getResponseMessage();

			}

			
			else {
				if ((StringUtils.isNotBlank(transaction.getResultCode())) || (StringUtils.isNotBlank(transaction.getResultCode()))) {

					String respCode = null;
					if (StringUtils.isNotBlank(transaction.getResultCode())){
						
						respCode = transaction.getResultCode();
					}
					
					PaytmResultType resultInstance = PaytmResultType.getInstanceFromName(respCode);
					
					if (resultInstance != null) {
						status = resultInstance.getStatusCode();
						errorType = ErrorType.getInstanceFromCode(resultInstance.getiPayCode());
						
						if (StringUtils.isNotBlank(transaction.getResultMsg())){
							pgTxnMsg = transaction.getResultMsg();
						}
						
						else {
							pgTxnMsg = resultInstance.getMessage();
						}
						
					} else {
						status = StatusType.REJECTED.getName();
						errorType = ErrorType.getInstanceFromCode("007");
						
						if (StringUtils.isNotBlank(transaction.getResultMsg())){
							pgTxnMsg = transaction.getResultMsg();
						}
						
						else {
							pgTxnMsg = "Transaction Declined";
						}
						
					}

				} else {
					status = StatusType.REJECTED.getName();
					errorType = ErrorType.REJECTED;
					if (StringUtils.isNotBlank(transaction.getResultMsg())){
						pgTxnMsg = transaction.getResultMsg();
					}
					else {
						pgTxnMsg = "Transaction Rejected";
					}

				}
			}

			fields.put(FieldType.STATUS.getName(), status);
			fields.put(FieldType.RESPONSE_MESSAGE.getName(), errorType.getResponseMessage());
			fields.put(FieldType.RESPONSE_CODE.getName(), errorType.getResponseCode());

			fields.put(FieldType.RRN.getName(), transaction.getRefundId());
			fields.put(FieldType.ACQ_ID.getName(), transaction.getTxnId());
			fields.put(FieldType.PG_RESP_CODE.getName(), transaction.getResultCode());
			
			if (StringUtils.isNotBlank(transaction.getResultStatus())) {
				fields.put(FieldType.PG_TXN_STATUS.getName(), transaction.getResultStatus());
			}
			else {
				fields.put(FieldType.PG_TXN_STATUS.getName(), errorType.getResponseCode());
			}
			
			fields.put(FieldType.PG_TXN_MESSAGE.getName(), pgTxnMsg);
			
			
		}
		
	}

	public Transaction toTransaction(String jsonResponse, String txnType) {

		Transaction transaction = new Transaction();

		if (StringUtils.isBlank(jsonResponse)) {

			logger.info("Empty response received for paytm refund");
			return transaction;
		}

		JSONObject respObj = new JSONObject(jsonResponse);

		if (respObj.has("body")) {

			JSONObject respBody = new JSONObject(jsonResponse);
			respBody = (JSONObject) respObj.get("body");

			if (respBody.has("resultInfo")) {

				JSONObject resultInfo = new JSONObject(jsonResponse);
				resultInfo = (JSONObject) respBody.get("resultInfo");

				if (resultInfo.has("resultStatus")) {
						
					String resultStatus = resultInfo.get("resultStatus").toString();
					transaction.setResultStatus(resultStatus);
				}

				if (resultInfo.has("resultCode")) {
					
					String resultCode = resultInfo.get("resultCode").toString();
					transaction.setResultCode(resultCode);
				}

				if (resultInfo.has("resultMsg")) {

					String resultMsg = resultInfo.get("resultMsg").toString();
					transaction.setResultMsg(resultMsg);
				}

			}

			if (respBody.has("txnId")) {

				String txnId = respBody.get("txnId").toString();
				transaction.setTxnId(txnId);
			}

			if (respBody.has("bankTxnId")) {

				String bankTxnId = respBody.get("bankTxnId").toString();
				transaction.setBankTxnId(bankTxnId);
			}

			if (respBody.has("refundId")) {

				String refundId = respBody.get("refundId").toString();
				transaction.setRefundId(refundId);
			}

		}

		return transaction;

	}
	
}
