package com.pay10.mobikwik;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.pay10.commons.exception.ErrorType;
import com.pay10.commons.exception.SystemException;
import com.pay10.commons.util.FieldType;
import com.pay10.commons.util.Fields;
import com.pay10.commons.util.PropertiesManager;
import com.pay10.commons.util.StatusType;
import com.pay10.pg.core.util.MobikwikEncDecUtil;

@Service
public class MobikwikStatusEnquiryProcessor {

	@Autowired
	@Qualifier("mobikwikTransactionConverter")
	private TransactionConverter converter;

	@Autowired
	private MobikwikEncDecUtil mobikwikEncDecUtil;

	public static final String STATUS_OPEN_TAG = "<status>";
	public static final String STATUS_CLOSE_TAG = "</status>";
	public static final String STATUS_CODE_OPEN_TAG = "<statuscode>";
	public static final String STATUS_CODE_CLOSE_TAG = "</statuscode>";
	public static final String STATUS_MESSAGE_OPEN_TAG = "<statusmessage>";
	public static final String STATUS_MESSAGE_CLOSE_TAG = "</statusmessage>";
	public static final String ORDER_ID_OPEN_TAG = "<orderid>";
	public static final String ORDER_ID_CLOSE_TAG = "</orderid>";
	public static final String REFID_OPEN_TAG = "<refid>";
	public static final String REFID_CLOSE_TAG = "</refid>";
	public static final String ORDER_TYPE_OPEN_TAG = "<ordertype>";
	public static final String ORDER_TYPE_CLOSE_TAG = "</ordertype>";
	private static Logger logger = LoggerFactory.getLogger(MobikwikStatusEnquiryProcessor.class.getName());

	public void enquiryProcessor(Fields fields) {
		String request = statusEnquiryRequest(fields);
		String response = "";
		try {

			response = getResponse(request);
			updateFields(fields, response);

		} catch (SystemException exception) {
			logger.error("Exception", exception);
		} catch (Exception e) {
			logger.error("Exception in decrypting status enquiry response for Mobikwik ", e);
		}

	}

	public String statusEnquiryRequest(Fields fields) {

		StringBuilder request = new StringBuilder();

		try {

			StringBuilder sb = new StringBuilder();

			sb.append("'" + fields.get(FieldType.MERCHANT_ID.getName()) + "'");
			sb.append("'" + fields.get(FieldType.PG_REF_NUM.getName()) + "'");

			String checksum = mobikwikEncDecUtil.calculateChecksum(fields.get(FieldType.TXN_KEY.getName()),
					sb.toString());

			request.append("mid=");
			request.append(fields.get(FieldType.MERCHANT_ID.getName()));
			request.append("&");
			request.append("orderid=");
			request.append(fields.get(FieldType.PG_REF_NUM.getName()));
			request.append("&");
			request.append("checksum=");
			request.append(checksum);

			return request.toString();
		}

		catch (Exception e) {

			logger.error("Exception in generating status enq request for Mobikwik", e);
			return request.toString();
		}

	}

	public static String getResponse(String request) throws SystemException {

		if (StringUtils.isBlank(request)) {
			logger.info("Request is empty for Mobikwik status enquiry");
			return null;
		}

		String hostUrl = "";
		Fields fields = new Fields();
		fields.put(FieldType.TXNTYPE.getName(), "STATUS");

		try {

			hostUrl = PropertiesManager.propertiesMap.get(Constants.MOBIKWIK_STATUS_ENQ_URL);

			logger.info("Status Enquiry Request to MObikwik : TxnType = " + fields.get(FieldType.TXNTYPE.getName())
					+ " " + "Order Id = " + fields.get(FieldType.ORDER_ID.getName()) + " " + request);
			StringBuilder serverResponse = new StringBuilder();
			HttpsURLConnection connection = null;

			URL url = new URL(hostUrl+request);

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
			logger.info(" Response received For MObikwik Status Enquiry >> " + serverResponse.toString());

			if (StringUtils.isNotBlank(serverResponse.toString())) {
				return serverResponse.toString();
			}
		} catch (Exception e) {
			logger.error("Exception in MObikwik Status Enquiry ", e);
		}
		return null;
	}

	public void updateFields(Fields fields, String xmlResponse) {

		Transaction transaction = new Transaction();

		transaction = toTransaction(xmlResponse, fields.get(FieldType.INTERNAL_ORIG_TXN_TYPE.getName()));

		String status = null;
		ErrorType errorType = null;
		String pgTxnMsg = null;

		if ((StringUtils.isNotBlank(transaction.getStatuscode()))
				&& transaction.getStatuscode().equalsIgnoreCase("0")) {

			status = StatusType.CAPTURED.getName();
			errorType = ErrorType.SUCCESS;
			pgTxnMsg = ErrorType.SUCCESS.getResponseMessage();

		}

		else {
			if ((StringUtils.isNotBlank(transaction.getStatuscode()))) {

				MobikwikResultType resultInstance = MobikwikResultType.getInstanceFromName(transaction.getStatuscode());

				if (resultInstance != null) {
					status = resultInstance.getStatusCode();
					errorType = ErrorType.getInstanceFromCode(resultInstance.getiPayCode());

					if (StringUtils.isNotBlank(transaction.getStatusmessage())) {
						pgTxnMsg = transaction.getStatusmessage();
					} else {
						pgTxnMsg = resultInstance.getMessage();
					}

				} else {
					status = StatusType.FAILED_AT_ACQUIRER.getName();
					errorType = ErrorType.getInstanceFromCode("022");

					if (StringUtils.isNotBlank(transaction.getStatusmessage())) {
						pgTxnMsg = transaction.getStatusmessage();
					} else {
						pgTxnMsg = "Transaction failed at acquirer";
					}

				}

			} else {
				status = StatusType.REJECTED.getName();
				errorType = ErrorType.REJECTED;

				if (StringUtils.isNotBlank(transaction.getStatusmessage())) {
					pgTxnMsg = transaction.getStatusmessage();
				} else {
					pgTxnMsg = ErrorType.REJECTED.getResponseMessage();
				}

			}
		}

		fields.put(FieldType.STATUS.getName(), status);
		fields.put(FieldType.RESPONSE_MESSAGE.getName(), errorType.getResponseMessage());
		fields.put(FieldType.RESPONSE_CODE.getName(), errorType.getResponseCode());

		if (StringUtils.isNotBlank(transaction.getRefId())) {
			fields.put(FieldType.ACQ_ID.getName(), transaction.getRefId());
		}

		fields.put(FieldType.PG_RESP_CODE.getName(), errorType.getResponseCode());
		fields.put(FieldType.PG_TXN_STATUS.getName(), status);
		fields.put(FieldType.PG_TXN_MESSAGE.getName(), pgTxnMsg);

	}

	public Transaction toTransaction(String xmlResponse, String txnType) {

		Transaction transaction = new Transaction();

		String status = getTextBetweenTags(xmlResponse, STATUS_OPEN_TAG, STATUS_CLOSE_TAG);
		String statuscode = getTextBetweenTags(xmlResponse, STATUS_CODE_OPEN_TAG, STATUS_CODE_CLOSE_TAG);
		String statusmessage = getTextBetweenTags(xmlResponse, STATUS_MESSAGE_OPEN_TAG, STATUS_MESSAGE_CLOSE_TAG);
		String txid = getTextBetweenTags(xmlResponse, ORDER_ID_OPEN_TAG, ORDER_ID_CLOSE_TAG);
		String refId = getTextBetweenTags(xmlResponse, REFID_OPEN_TAG, REFID_CLOSE_TAG);
		String ordertype = getTextBetweenTags(xmlResponse, ORDER_TYPE_OPEN_TAG, ORDER_TYPE_CLOSE_TAG);

		transaction.setStatus(status);
		transaction.setStatuscode(statuscode);
		transaction.setStatusmessage(statusmessage);
		transaction.setTxid(txid);
		transaction.setRefId(refId);
		transaction.setOrdertype(ordertype);

		return transaction;

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
