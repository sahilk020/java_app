package com.pay10.apbl;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

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
import com.pay10.commons.util.Amount;
import com.pay10.commons.util.FieldType;
import com.pay10.commons.util.Fields;
import com.pay10.commons.util.PropertiesManager;
import com.pay10.commons.util.StatusType;
import com.pay10.commons.util.TransactionManager;
import com.pay10.pg.core.util.ApblEncDecUtil;

@Service
public class APBLStatusEnquiryProcessor {

	@Autowired
	@Qualifier("apblTransactionConverter")
	private TransactionConverter converter;

	@Autowired
	private FieldsDao fieldsDao;

	@Autowired
	private ApblEncDecUtil apblEncDecUtil;

	private static Logger logger = LoggerFactory.getLogger(APBLStatusEnquiryProcessor.class.getName());

	public void enquiryProcessor(Fields fields) {
		String request = statusEnquiryRequest(fields);
		String response = "";
		try {

			response = getResponse(request);
			updateFields(fields, response);

		} catch (SystemException exception) {
			logger.error("Exception", exception);
		} catch (Exception e) {
			logger.error("Exception in decrypting status enquiry response for APBL ", e);
		}

	}

	public String statusEnquiryRequest(Fields fields) {

		Transaction transaction = new Transaction();

		try {

			String dateTxn = fields.get(FieldType.CREATE_DATE.getName());
			String finalDate = null;
			if (StringUtils.isNotBlank(dateTxn)) {

				String dateTxnArr[] = dateTxn.split(" ");

				String date = dateTxnArr[0];
				String date1Arr[] = date.split("-");
				finalDate = date1Arr[2] + date1Arr[1] + date1Arr[0] + dateTxnArr[1].replace(":", "");

			}
			
			String amount  = Amount.toDecimal(fields.get(FieldType.TOTAL_AMOUNT.getName()), fields.get(FieldType.CURRENCY_CODE.getName()));

			JSONObject reqJson = new JSONObject();

			reqJson.put("feSessionId", TransactionManager.getNewTransactionId());
			reqJson.put("txnRefNO", fields.get(FieldType.PG_REF_NUM.getName()));
			reqJson.put("txnDate", finalDate);
			reqJson.put("merchantId", fields.get(FieldType.MERCHANT_ID.getName()));
			reqJson.put("amount", amount);
			reqJson.put("request", "ECOMM_INQ");
			//reqJson.put("langId", "001");
			StringBuilder hashString = new StringBuilder();

			hashString.append(fields.get(FieldType.MERCHANT_ID.getName()));
			hashString.append("#");
			hashString.append(fields.get(FieldType.PG_REF_NUM.getName()));
			hashString.append("#");
			hashString.append(amount);
			hashString.append("#");
			hashString.append(finalDate);
			hashString.append("#");
			hashString.append(fields.get(FieldType.TXN_KEY.getName()));

			String hash = apblEncDecUtil.getHash(hashString.toString());

			reqJson.put("hash", hash);

			return reqJson.toString();

		}

		catch (Exception e) {
			logger.error("Exception in preparing APBL Status Enquiry Request", e);
		}

		return null;

	}

	public static String getResponse(String request) throws SystemException {

		if (StringUtils.isBlank(request)) {
			logger.info("Request is empty for APBL status enquiry");
			return null;
		}

		String hostUrl = "";
		Fields fields = new Fields();
		fields.put(FieldType.TXNTYPE.getName(), "STATUS");

		try {

			hostUrl = PropertiesManager.propertiesMap.get(Constants.APBL_STATUS_ENQ_URL);

			logger.info("Status Enquiry Request to APBL : TxnType = " + fields.get(FieldType.TXNTYPE.getName()) + " "
					+ "Order Id = " + fields.get(FieldType.ORDER_ID.getName()) + " " + request);
			StringBuilder serverResponse = new StringBuilder();
			HttpsURLConnection connection = null;

			URL url = new URL(hostUrl);

			connection = (HttpsURLConnection) url.openConnection();

			connection.setRequestMethod("POST");
			connection.setRequestProperty("Content-Type", "application/json");
			connection.setRequestProperty("Content-Length", request.toString());

			connection.setUseCaches(false);
			connection.setDoOutput(true);
			connection.setDoInput(true);

			// Send request
			OutputStream outputStream = connection.getOutputStream();
			DataOutputStream wr = new DataOutputStream(outputStream);
			wr.writeBytes(request.toString());
			wr.close();

			// Get Response
			InputStream is = connection.getInputStream();
			BufferedReader rd = new BufferedReader(new InputStreamReader(is));
			String line;

			int code = ((HttpURLConnection) connection).getResponseCode();
			while ((line = rd.readLine()) != null) {
				serverResponse.append(line);

			}
			rd.close();
			logger.info(" Response received For APBL Status Enquiry >> " + serverResponse.toString());

			if (StringUtils.isNotBlank(serverResponse.toString())) {
				return serverResponse.toString();
			}
		} catch (Exception e) {
			logger.error("Exception in APBL Status Enquiry ", e);
		}
		return null;
	}

	public void updateFields(Fields fields, String jsonResponse) {

		Transaction transaction = new Transaction();

		if (StringUtils.isNotBlank(jsonResponse)) {

			JSONObject respObj = new JSONObject(jsonResponse);

			if (respObj.has("txns")) {

				if (respObj.getJSONArray("txns") != null &&  respObj.getJSONArray("txns").length() > 0) {
					org.json.JSONArray txns = respObj.getJSONArray("txns");
					
					JSONObject txnObject = txns.getJSONObject(0);

					if (txnObject.has("status")) {
						transaction.setSTATUS(String.valueOf(txnObject.get("status")));
					}

					if (txnObject.has("txnId")) {
						transaction.setTxnId(String.valueOf(txnObject.get("txnId")));
					}
					
				}

			
			}

			if (respObj.has("messageText")) {
				transaction.setMessageText(String.valueOf(respObj.get("messageText")));
			}

			if (respObj.has("code")) {
				transaction.setCODE(String.valueOf(respObj.get("code")));
			}

			if (respObj.has("errorCode")) {
				transaction.setErrorCode(String.valueOf(respObj.get("errorCode")));
			}

		} else {

		}

		String status = null;
		ErrorType errorType = null;
		String pgTxnMsg = null;

		if ((StringUtils.isNotBlank(transaction.getErrorCode())) && StringUtils.isNotBlank(transaction.getSTATUS())
		&& transaction.getErrorCode().equalsIgnoreCase("000") && transaction.getSTATUS().equalsIgnoreCase("SUC")) {

			status = StatusType.CAPTURED.getName();
			errorType = ErrorType.SUCCESS;
			pgTxnMsg = ErrorType.SUCCESS.getResponseMessage();

		}

		else {
			if ((StringUtils.isNotBlank(transaction.getErrorCode()))) {

				APBLResultType resultInstance = APBLResultType.getInstanceFromName(transaction.getErrorCode());

				if (resultInstance != null) {
					status = resultInstance.getStatusCode();
					errorType = ErrorType.getInstanceFromCode(resultInstance.getiPayCode());
					
					if (StringUtils.isNotBlank(transaction.getMessageText())) {
						pgTxnMsg = transaction.getMessageText();
					}
					else {
						pgTxnMsg = resultInstance.getMessage();
					}
				
				} else {
					status = StatusType.FAILED_AT_ACQUIRER.getName();
					errorType = ErrorType.getInstanceFromCode("022");
					
					if (StringUtils.isNotBlank(transaction.getMessageText())) {
						pgTxnMsg = transaction.getMessageText();
					}
					else {
						pgTxnMsg = "Transaction failed at acquirer";
					}
					
				}

			} else {
				status = StatusType.REJECTED.getName();
				errorType = ErrorType.REJECTED;
				
				if (StringUtils.isNotBlank(transaction.getMessageText())) {
					pgTxnMsg = transaction.getMessageText();
				}
				else {
					pgTxnMsg = ErrorType.REJECTED.getResponseMessage();
				}
				
			}
		}

		fields.put(FieldType.STATUS.getName(), status);
		fields.put(FieldType.RESPONSE_MESSAGE.getName(), errorType.getResponseMessage());
		fields.put(FieldType.RESPONSE_CODE.getName(), errorType.getResponseCode());

		if (StringUtils.isNotBlank(transaction.getTxnId())) {
			fields.put(FieldType.ACQ_ID.getName(), transaction.getTxnId());
			fields.put(FieldType.RRN.getName(), transaction.getTxnId());
		}

		fields.put(FieldType.PG_RESP_CODE.getName(), errorType.getResponseCode());
		fields.put(FieldType.PG_TXN_STATUS.getName(), status);
		fields.put(FieldType.PG_TXN_MESSAGE.getName(), pgTxnMsg);

	}

	
	public static String getTextForTag(String text, String tag1) {

		StringBuilder sb = new StringBuilder();
		int leftIndex = text.indexOf(tag1);
		if (leftIndex == -1) {
			return null;
		}

		int startIndex = tag1.length() + leftIndex + 2;

		for (int i = 0; i < text.length() - startIndex; i++) {

			char txt = text.charAt(startIndex + i);
			String txtStr = String.valueOf(txt);
			if (txtStr.equalsIgnoreCase("\"")) {
				break;
			}
			sb.append(txtStr);

		}
		return sb.toString();
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
}
