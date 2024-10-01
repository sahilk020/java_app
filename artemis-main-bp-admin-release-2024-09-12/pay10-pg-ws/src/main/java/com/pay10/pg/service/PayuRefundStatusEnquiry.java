package com.pay10.pg.service;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

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
import com.pay10.commons.user.Account;
import com.pay10.commons.user.AccountCurrency;
import com.pay10.commons.user.User;
import com.pay10.commons.user.UserDao;
import com.pay10.commons.util.AcquirerType;
import com.pay10.commons.util.FieldType;
import com.pay10.commons.util.Fields;
import com.pay10.commons.util.PropertiesManager;
import com.pay10.commons.util.StatusType;
import com.pay10.commons.util.TransactionType;
import com.pay10.pg.core.payu.util.Constants;
import com.pay10.pg.core.payu.util.PayuHasher;
import com.pay10.pg.core.util.ProcessManager;
import com.pay10.pg.core.util.Processor;
import com.pay10.pg.security.SecurityProcessor;

@Service
public class PayuRefundStatusEnquiry {
	private static Logger logger = LoggerFactory.getLogger(PayuRefundStatusEnquiry.class.getName());

	@Autowired
	private StatusEnquiryProcessor statusEnquiryProcessor;

	@Autowired
	private SecurityProcessor securityProcessor;

	
	@Autowired
	@Qualifier("updateProcessor")
	private Processor updateProcessor;
	
	@Autowired
	private FieldsDao fieldsDao;

	@Autowired
	private UserDao userDao;

	@SuppressWarnings("incomplete-switch")
	public Map<String, String> process(Fields fields) throws SystemException {
		if (StringUtils.isBlank(fields.get(FieldType.REFUND_ORDER_ID.getName()))) {
			logger.info(
					"Acquirer not found for status enquiry , pgRef == " + fields.get(FieldType.PG_REF_NUM.getName()));
			fields.put(FieldType.STATUS.getName(), StatusType.INVALID.getName());
			fields.put(FieldType.RESPONSE_MESSAGE.getName(), "No Such Transaction found");
			fields.put(FieldType.PG_TXN_MESSAGE.getName(), "No Such Transaction found");

			fields.put(FieldType.RESPONSE_CODE.getName(), ErrorType.NO_SUCH_TRANSACTION.getCode());
			return fields.getFields();
		}
		
		if(fieldsDao.findTransactionforPayuRefund(fields.get(FieldType.REFUND_ORDER_ID.getName()))){
			
		}else {
			fields.put(FieldType.STATUS.getName(), StatusType.INVALID.getName());
			fields.put(FieldType.RESPONSE_MESSAGE.getName(), "No Such Transaction found");
			fields.put(FieldType.PG_TXN_MESSAGE.getName(), "No Such Transaction found");

			fields.put(FieldType.RESPONSE_CODE.getName(), ErrorType.NO_SUCH_TRANSACTION.getCode());
		}

		logger.info("Request received for statusEnquiryProcessor");
		// boolean iSTxnFound = statusEnquiryProcessor.getTransactionFields(fields);
		fieldsDao.createAllForPayuRefund(fields.get(FieldType.REFUND_ORDER_ID.getName()), fields);

		logger.info("Request received for statusEnquiryProcessor" + fields.getFieldsAsString());
		logger.info("Request received for statusEnquiryProcessor" + fields.getFieldsAsString());

		Map<String, String> keyMap = getTxnKey(fields);
		fields.put(FieldType.ADF1.getName(), keyMap.get("ADF1"));
		fields.put(FieldType.ADF2.getName(), keyMap.get("ADF2"));
		fields.put(FieldType.ADF4.getName(), keyMap.get("ADF4"));
		fields.put(FieldType.TXN_KEY.getName(), keyMap.get("txnKey"));
		fields.put(FieldType.MERCHANT_ID.getName(), keyMap.get("MerchantId"));
		logger.info("Request received for statusEnquiryProcessor" + fields.getFieldsAsString());

		payuRefundEnquiryProcessor(fields);
		logger.info("response for Payu status Enquiry refund " + fields.getFieldsAsString());

		if (StringUtils.isNotBlank(fields.getFields().get(FieldType.STATUS.getName()))
				&& (fields.get(FieldType.STATUS.getName()).equalsIgnoreCase(StatusType.CAPTURED.getName())
						|| fields.get(FieldType.STATUS.getName()).equalsIgnoreCase(StatusType.FAILED.getName()))) {

			if (!fields.getFields().get(FieldType.STATUS.getName()).equalsIgnoreCase(StatusType.INVALID.getName())
					&& !fields.getFields().get(FieldType.STATUS.getName())
							.equalsIgnoreCase(StatusType.PENDING.getName())) {
				fields.put(FieldType.INTERNAL_ORIG_TXN_TYPE.getName(),"REFUND");
				logger.info("response for Payu status Enquiry refund " + fields.getFieldsAsString());
				fieldsDao.insertTransaction(fields);
			}

		}

		return fields.getFields();

	}

	private void payuRefundEnquiryProcessor(Fields fields) throws SystemException {

		StringBuilder request = new StringBuilder();
		logger.info("response for Payu status Enquiry refund " + fields.getFieldsAsString());

		request.append("key=");
		request.append(fields.get(FieldType.TXN_KEY.getName()));
		request.append("&command=check_action_status&var1=");
		request.append(fields.get(FieldType.ACQ_ID.getName()));
		request.append("&");
		request.append("hash=");
		request.append(PayuHasher.payuResponseRefundstatus(fields.get(FieldType.TXN_KEY.getName()),
				fields.get(FieldType.ACQ_ID.getName()), fields));
		String response = getResponse(request.toString(), fields);
		logger.info("payu>>>>>>>>>>>>>>>>>>>>>>>>>>" + request.toString());

		logger.info("response for Payu status Enquiry refund " + response);
		JSONObject response1 = new JSONObject(response);
		logger.info("response for Payu status Enquiry refund " + response1);

		JSONObject response2 = new JSONObject(response1.get("transaction_details").toString());
		logger.info("response for Payu status Enquiry refund " + response2);
		JSONObject response3 = new JSONObject(response2.get(fields.get(FieldType.ACQ_ID.getName())).toString());

		updateFields(fields, response3.get(fields.get(FieldType.ACQ_ID.getName())).toString());

	}

	public String getResponse(String request, Fields fields) throws SystemException {

		String hostUrl = PropertiesManager.propertiesMap.get("PAYU__REFUND_STATUS_URL");

		try {

			URL url = new URL(hostUrl);

			String[] arrOfStr = request.split("&", 6);
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
			connection.setRequestProperty("Content-Length", String.valueOf(postDataBytes.length));
			connection.setDoOutput(true);
			connection.getOutputStream().write(postDataBytes);

			DataOutputStream requestWriter = new DataOutputStream(connection.getOutputStream());
			logger.info(request);
			requestWriter.writeBytes(request);
			requestWriter.close();
			String responseData = "";
			InputStream is = connection.getInputStream();
			BufferedReader responseReader = new BufferedReader(new InputStreamReader(is));
			if ((responseData = responseReader.readLine()) != null) {
				logger.info("Payu Refund Response >>> " + responseData);
			}

			responseReader.close();

			return responseData;

		} catch (Exception e) {
			logger.error("Exception in Payu txn Communicator", e);
		}
		return null;

	}

	public void updateFields(Fields fields, String jsonResponse) {
		logger.info("response for status enquiry for refund in payu" + jsonResponse);
		JSONObject response = new JSONObject(jsonResponse);

		logger.info("response for status enquiry for refund in payu" + response);

		String status = null;
		ErrorType errorType = null;
		String pgTxnMsg = null;

			logger.info("response for status enquiry for refund in payu" + response+fields.getFieldsAsString());
			logger.info("response for status enquiry for refund in payu" + response.getString("status"));

			if ((response.has("status") && (response.getString("status")).equalsIgnoreCase(Constants.SUCCESS)))

			{

				fields.put(FieldType.STATUS.getName(), StatusType.CAPTURED.getName());
				fields.put(FieldType.RESPONSE_CODE.getName(), ErrorType.SUCCESS.getResponseCode());
				fields.put(FieldType.RESPONSE_MESSAGE.getName(), ErrorType.SUCCESS.getResponseMessage());
				logger.info("response for status enquiry for refund in payu" + response + fields.getFieldsAsString());

			} else if ((response.has("status") && (response.getString("status")).equalsIgnoreCase("pending")))

			{
				fields.put(FieldType.STATUS.getName(), StatusType.PENDING.getName());
				fields.put(FieldType.RESPONSE_CODE.getName(), ErrorType.PENDING.getResponseCode());
				fields.put(FieldType.RESPONSE_MESSAGE.getName(), ErrorType.PENDING.getResponseMessage());
			}

			else {
				fields.put(FieldType.STATUS.getName(), StatusType.FAILED_AT_ACQUIRER.getName());
				fields.put(FieldType.RESPONSE_CODE.getName(), ErrorType.FAILED.getResponseCode());
				fields.put(FieldType.RESPONSE_MESSAGE.getName(), ErrorType.FAILED.getResponseMessage());
			}

			
	
		logger.info("response for status enquiry for refund in payu" + response+fields.getFieldsAsString());


		if(! (response.get("bank_ref_num").toString()== "null") ) {
		fields.put(FieldType.RRN.getName(), response.getString("bank_ref_num"));
		}
		fields.put(FieldType.ACQ_ID.getName(), response.get("mihpayid").toString());
		fields.put(FieldType.PG_RESP_CODE.getName(), response.get("request_id").toString());

	}

	public Map<String, String> getTxnKey(Fields fields) throws SystemException {

		Map<String, String> keyMap = new HashMap<String, String>();

		User user = userDao.findPayId(fields.get(FieldType.PAY_ID.getName()));
		Account account = null;
		Set<Account> accounts = user.getAccounts();

		if (accounts == null || accounts.size() == 0) {
			logger.info("No account found for Pay ID = " + fields.get(FieldType.PAY_ID.getName()) + " and ORDER ID = "
					+ fields.get(FieldType.ORDER_ID.getName()));
		} else {
			for (Account accountThis : accounts) {
				if (accountThis.getAcquirerName()
						.equalsIgnoreCase(AcquirerType.getInstancefromCode(AcquirerType.PAYU.getCode()).getName())) {
					account = accountThis;
					break;
				}
			}
		}

		AccountCurrency accountCurrency = account.getAccountCurrency(fields.get(FieldType.CURRENCY_CODE.getName()));

		keyMap.put("ADF1", accountCurrency.getAdf1());
		keyMap.put("ADF2", accountCurrency.getAdf2());
		keyMap.put("ADF4", accountCurrency.getAdf4());
		keyMap.put("txnKey", accountCurrency.getTxnKey());
		keyMap.put("MerchantId", accountCurrency.getMerchantId());

		return keyMap;

	}

}
