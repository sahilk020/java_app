package com.pay10.payu;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

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
import com.pay10.pg.core.payu.util.Constants;
import com.pay10.pg.core.payu.util.PayuHasher;

@Service
public class PayuStatusEnquiryProcessor {

	@Autowired
	@Qualifier("payuTransactionConverter")
	private TransactionConverter converter;

	@Autowired
	private FieldsDao fieldsDao;

	private static Logger logger = LoggerFactory.getLogger(PayuStatusEnquiryProcessor.class.getName());

	public void enquiryProcessor(Fields fields) {
		String request = statusEnquiryRequest(fields);
		String response = "";
		try {

			response = getResponse(request);
			updateFields(fields, response);

		} catch (SystemException exception) {
			logger.error("Exception", exception);
		} catch (Exception e) {
			logger.error("Exception in decrypting status enquiry response for payu ", e);
		}

	}

	public String statusEnquiryRequest(Fields fields) {

		try {
			String hash = PayuHasher.payuStatusEnqHash(fields);

			StringBuilder request = new StringBuilder();

			request.append(Constants.RKEY);
			request.append(fields.get(FieldType.TXN_KEY.getName()));
			request.append("&");
			request.append(Constants.RCOMMAND);
			request.append(Constants.VERIFY_PAYMENT);
			request.append("&");
			request.append(Constants.RHASH);
			request.append(hash);
			request.append("&");
			request.append(Constants.RVAR1);
			request.append(fields.get(FieldType.PG_REF_NUM.getName()));
			logger.info("request fot dule"+request.toString());

			String post_data = request.toString();
			return post_data;

		}

		catch (Exception e) {
			logger.error("Exception in preparing Payu Status Enquiry Request", e);
		}

		return null;

	}

	public static String getResponse(String request) throws SystemException {

		if (StringUtils.isBlank(request)) {
			logger.info("Request is empty for Payu status enquiry");
			return null;
		}

		String hostUrl = "";
		Fields fields = new Fields();
		fields.put(FieldType.TXNTYPE.getName(), "STATUS");

		try {

			hostUrl = PropertiesManager.propertiesMap.get(Constants.PAYU_STATUS_ENQ_URL);

			logger.info("Status Enquiry Request to Payu : TxnType = " + fields.get(FieldType.TXNTYPE.getName()) + " "
					+ "Order Id = " + fields.get(FieldType.ORDER_ID.getName()) + " " + request);

			URL url = new URL(hostUrl);

			String[] arrOfStr = request.split("&", 4);
			Map<String, String> reqMap = new HashMap<String, String>();
			for (String reqst : arrOfStr) {
				String[] reqStr = reqst.split("=");
				reqMap.put(reqStr[0], reqStr[1]);
			}

			StringBuilder postData = new StringBuilder();
			for (Map.Entry<String, String> param : reqMap.entrySet()) {
				if (postData.length() != 0)
					postData.append('&');
				postData.append(URLEncoder.encode(param.getKey(), "UTF-8"));
				postData.append('=');
				postData.append(URLEncoder.encode(String.valueOf(param.getValue()), "UTF-8"));
			}
			byte[] postDataBytes = postData.toString().getBytes("UTF-8");

			HttpURLConnection connection = (HttpURLConnection) url.openConnection();
			connection.setRequestMethod("POST");
			connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
			connection.setRequestProperty("charset", "utf-8");
			connection.setRequestProperty("Content-Length", String.valueOf(postDataBytes.length));
			connection.setDoOutput(true);
			connection.setDoOutput(true);
			connection.getOutputStream().write(postDataBytes);

			DataOutputStream requestWriter = new DataOutputStream(connection.getOutputStream());
			requestWriter.writeBytes(request);
			requestWriter.close();
			String responseData = "";
			InputStream is = connection.getInputStream();
			BufferedReader responseReader = new BufferedReader(new InputStreamReader(is));
			if ((responseData = responseReader.readLine()) != null) {
				logger.info("Response received for payu status enq : " + responseData);
			}

			responseReader.close();

			return responseData;

		} catch (Exception e) {
			logger.error("Exception in Payu Status Enquiry ", e);
		}
		return null;
	}

	public void updateFields(Fields fields, String jsonResponse) {

		Transaction transaction = new Transaction();

		transaction = toTransaction(jsonResponse, fields.get(FieldType.INTERNAL_ORIG_TXN_TYPE.getName()),
				fields.get(FieldType.PG_REF_NUM.getName()));

		String status = null;
		ErrorType errorType = null;
		String pgTxnMsg = null;

		if (fields.get(FieldType.INTERNAL_ORIG_TXN_TYPE.getName()).equalsIgnoreCase(TransactionType.SALE.getCode())) {

			if ((StringUtils.isNotBlank(transaction.getStatus()))
					&& ((transaction.getStatus()).equalsIgnoreCase(Constants.SUCCESS)))

			{
				status = StatusType.CAPTURED.getName();
				errorType = ErrorType.SUCCESS;

				if (StringUtils.isNotBlank(transaction.getResponseMsg())) {
					pgTxnMsg = transaction.getResponseMsg();
				}

				else {
					pgTxnMsg = ErrorType.SUCCESS.getResponseMessage();
				}

			}

			else {
				if ((StringUtils.isNotBlank(transaction.getResponseCode()))) {

					String respCode = null;
					if (StringUtils.isNotBlank(transaction.getResponseCode())) {
						respCode = transaction.getResponseCode();
					}

					PayuResultType resultInstance = PayuResultType.getInstanceFromName(respCode);

					if (resultInstance != null) {
						status = resultInstance.getStatusCode();
						errorType = ErrorType.getInstanceFromCode(resultInstance.getiPayCode());

						if (StringUtils.isNotBlank(transaction.getResponseMsg())) {
							pgTxnMsg = transaction.getResponseMsg();
						}

						else {
							pgTxnMsg = resultInstance.getMessage();
						}

					} else {
						status = StatusType.FAILED_AT_ACQUIRER.getName();
						errorType = ErrorType.getInstanceFromCode(Constants.ERROR022);

						if (StringUtils.isNotBlank(transaction.getResponseMsg())) {
							pgTxnMsg = transaction.getResponseMsg();
						}

						else {
							pgTxnMsg = ErrorType.FAILED.toString();
						}

					}

				} else {
					status = StatusType.FAILED_AT_ACQUIRER.getName();
					errorType = ErrorType.FAILED;
					if (StringUtils.isNotBlank(transaction.getResponseMsg())) {
						pgTxnMsg = transaction.getResponseMsg();
					} else {
						pgTxnMsg = ErrorType.FAILED.toString();
					}

				}
			}

			fields.put(FieldType.STATUS.getName(), status);
			fields.put(FieldType.RESPONSE_MESSAGE.getName(), errorType.getResponseMessage());
			fields.put(FieldType.RESPONSE_CODE.getName(), errorType.getResponseCode());

			// fields.put(FieldType.RRN.getName(), transaction.getBankRefNum());
			fields.put(FieldType.ACQ_ID.getName(), transaction.getMihPayuId());
			fields.put(FieldType.PG_RESP_CODE.getName(), transaction.getResponseCode());

			if (StringUtils.isNotBlank(transaction.getStatus())) {
				fields.put(FieldType.PG_TXN_STATUS.getName(), transaction.getStatus());
			} else {
				fields.put(FieldType.PG_TXN_STATUS.getName(), errorType.getResponseCode());
			}

			fields.put(FieldType.PG_TXN_MESSAGE.getName(), pgTxnMsg);

		}

		if (fields.get(FieldType.INTERNAL_ORIG_TXN_TYPE.getName()).equalsIgnoreCase(TransactionType.REFUND.getCode())) {

			if ((StringUtils.isNotBlank(transaction.getResponseCode()))
					&& (((transaction.getResponseCode()).equalsIgnoreCase("601")
							|| (transaction.getResponseCode()).equalsIgnoreCase("10"))))

			{
				status = StatusType.CAPTURED.getName();
				errorType = ErrorType.SUCCESS;
				pgTxnMsg = ErrorType.SUCCESS.getResponseMessage();

			}

			else {
				if ((StringUtils.isNotBlank(transaction.getResponseCode()))
						|| (StringUtils.isNotBlank(transaction.getResponseCode()))) {

					String respCode = null;
					if (StringUtils.isNotBlank(transaction.getResponseCode())) {

						respCode = transaction.getResponseCode();
					}

					PayuResultType resultInstance = PayuResultType.getInstanceFromName(respCode);

					if (resultInstance != null) {
						status = resultInstance.getStatusCode();
						errorType = ErrorType.getInstanceFromCode(resultInstance.getiPayCode());

						if (StringUtils.isNotBlank(transaction.getResponseMsg())) {
							pgTxnMsg = transaction.getResponseMsg();
						}

						else {
							pgTxnMsg = resultInstance.getMessage();
						}

					} else {
						status = StatusType.REJECTED.getName();
						errorType = ErrorType.getInstanceFromCode("007");

						if (StringUtils.isNotBlank(transaction.getResponseMsg())) {
							pgTxnMsg = transaction.getResponseMsg();
						}

						else {
							pgTxnMsg = "Transaction Declined";
						}

					}

				} else {
					status = StatusType.REJECTED.getName();
					errorType = ErrorType.REJECTED;
					if (StringUtils.isNotBlank(transaction.getResponseMsg())) {
						pgTxnMsg = transaction.getResponseMsg();
					} else {
						pgTxnMsg = "Transaction Rejected";
					}

				}
			}

			fields.put(FieldType.STATUS.getName(), status);
			fields.put(FieldType.RESPONSE_MESSAGE.getName(), errorType.getResponseMessage());
			fields.put(FieldType.RESPONSE_CODE.getName(), errorType.getResponseCode());

			// fields.put(FieldType.RRN.getName(), transaction.getBankRefNum());
			fields.put(FieldType.ACQ_ID.getName(), transaction.getMihPayuId());
			fields.put(FieldType.PG_RESP_CODE.getName(), transaction.getResponseCode());

			if (StringUtils.isNotBlank(transaction.getStatus())) {
				fields.put(FieldType.PG_TXN_STATUS.getName(), transaction.getStatus());
			} else {
				fields.put(FieldType.PG_TXN_STATUS.getName(), errorType.getResponseCode());
			}

			fields.put(FieldType.PG_TXN_MESSAGE.getName(), pgTxnMsg);

		}

	}

	public Transaction toTransaction(String jsonResponse, String txnType, String order) {

		Transaction transaction = new Transaction();

		if (StringUtils.isBlank(jsonResponse)) {

			logger.info("Empty response received for payu refund");
			return transaction;
		}

		JSONObject respObj = new JSONObject(jsonResponse);

		String mStatus = respObj.get(Constants.STATUS).toString();

		if (mStatus.equals("1")) {

			JSONObject respBody = new JSONObject(jsonResponse);
			respBody = (JSONObject) respObj.get(Constants.TRANSACTION_DETAIL);

			JSONObject resp = respBody.getJSONObject(order);
			try {
				if (resp.has(Constants.STATUS)) {
					String status = resp.getString(Constants.STATUS);
					transaction.setStatus(status);
				}
				if (resp.has(Constants.MIHPAYID)) {
					String mihPayuId = resp.getString(Constants.MIHPAYID);
					transaction.setMihPayuId(mihPayuId);
				}
				if (resp.has(Constants.ERROR_CODE)) {
					String error_code = resp.getString(Constants.ERROR_CODE);
					transaction.setResponseCode(error_code);
				}
				if (resp.has(Constants.AMT)) {
					String amt = resp.getString(Constants.AMT);
					transaction.setAmount(amt);
				}
				logger.info("jsonResponse",jsonResponse);
				if (resp.has("txnid")) {
					String txnId = resp.getString("txnid");
					transaction.setTxnId(txnId);
				}

			} catch (Exception e) {
				logger.info(e.toString());
			}

		}

		return transaction;

	}

}
