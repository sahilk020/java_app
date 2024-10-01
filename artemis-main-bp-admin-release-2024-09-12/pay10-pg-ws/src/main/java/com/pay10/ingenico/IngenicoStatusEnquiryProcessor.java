package com.pay10.ingenico;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.pay10.commons.dao.FieldsDao;
import com.pay10.commons.exception.ErrorType;
import com.pay10.commons.util.FieldType;
import com.pay10.commons.util.Fields;
import com.pay10.commons.util.PropertiesManager;
import com.pay10.commons.util.StatusType;
import com.pay10.commons.util.TransactionType;
import com.pay10.pg.core.ingenico.util.TransactionRequestBean;

@Service
public class IngenicoStatusEnquiryProcessor {

	@Autowired
	@Qualifier("ingenicoTransactionConverter")
	private TransactionConverter converter;

	@Autowired
	private FieldsDao fieldsDao;

	private static Logger logger = LoggerFactory.getLogger(IngenicoStatusEnquiryProcessor.class.getName());

	public void enquiryProcessor(Fields fields) {
		TransactionRequestBean request = statusEnquiryRequest(fields);
		String response = "";
		try {

			response = request.getTransactionToken();
			logger.info("Response for refund status enquiry Ingenico >> " + response);
			updateFields(fields, response);

		} catch (Exception e) {
			logger.error("Exception in getting status enquiry response for ingenico ", e);
		}

	}

	public TransactionRequestBean statusEnquiryRequest(Fields fields) {

		TransactionRequestBean objTransactionRequestBean = new TransactionRequestBean();

		try {

			String hostUrl = PropertiesManager.propertiesMap.get(Constants.INGENICO_STATUS_ENQ_URL);
			objTransactionRequestBean.setStrRequestType(Constants.STATUS_ENQ_CODE);
			objTransactionRequestBean.setStrMerchantCode(fields.get(FieldType.MERCHANT_ID.getName()));
			objTransactionRequestBean.setMerchantTxnRefNumber(fields.get(FieldType.PG_REF_NUM.getName()));

			String dataString = fields.get(FieldType.CREATE_DATE.getName());

			String date1[] = dataString.substring(0, 10).split("-");
			StringBuilder sb = new StringBuilder();
			sb.append(date1[2]);
			sb.append("-");
			sb.append(date1[1]);
			sb.append("-");
			sb.append(date1[0]);

			objTransactionRequestBean.setTxnDate(sb.toString());
			objTransactionRequestBean.setWebServiceLocator(hostUrl);
			objTransactionRequestBean.setKey(fields.get(FieldType.TXN_KEY.getName()).getBytes());
			objTransactionRequestBean.setIv(fields.get(FieldType.PASSWORD.getName()).getBytes());
			objTransactionRequestBean.setStrTimeOut("1000");

			return objTransactionRequestBean;
		}

		catch (Exception e) {
			logger.error("Exception in preparing Atom Status Enquiry Request", e);
		}

		return null;

	}

	public void updateFields(Fields fields, String response) {

		String responseArray[] = response.split(Pattern.quote("|"));
		Map<String, String> responseMap = new HashMap<String, String>();

		for (String data : responseArray) {

			String dataArray[] = data.split("=");
			if (dataArray.length > 1) {
				responseMap.put(dataArray[0], dataArray[1]);
			}

		}

		Transaction transaction = new Transaction();

		if (StringUtils.isNotBlank(responseMap.get(Constants.txn_status))) {
			transaction.setTxn_status(responseMap.get(Constants.txn_status));
		}

		if (StringUtils.isNotBlank(responseMap.get(Constants.txn_msg))) {
			transaction.setTxn_msg(responseMap.get(Constants.txn_msg));
		}

		if (StringUtils.isNotBlank(responseMap.get(Constants.txn_err_msg))) {
			transaction.setTxn_err_msg(responseMap.get(Constants.txn_err_msg));
		}

		if (StringUtils.isNotBlank(responseMap.get(Constants.clnt_txn_ref))) {
			transaction.setClnt_txn_ref(responseMap.get(Constants.clnt_txn_ref));
		}

		if (StringUtils.isNotBlank(responseMap.get(Constants.tpsl_bank_cd))) {
			transaction.setTpsl_bank_cd(responseMap.get(Constants.tpsl_bank_cd));
		}

		if (StringUtils.isNotBlank(responseMap.get(Constants.tpsl_txn_id))) {
			transaction.setTpsl_txn_id(responseMap.get(Constants.tpsl_txn_id));
		}

		if (StringUtils.isNotBlank(responseMap.get(Constants.tpsl_rfnd_id))) {
			transaction.setTpsl_rfnd_id(responseMap.get(Constants.tpsl_rfnd_id));
		}

		if (StringUtils.isNotBlank(responseMap.get(Constants.rqst_token))) {
			transaction.setRqst_token(responseMap.get(Constants.rqst_token));
		}

		String status = null;
		ErrorType errorType = null;
		String pgTxnMsg = null;

		// For Sale Transaction success code is 0300
		if ((StringUtils.isNotBlank(transaction.getTxn_status()))
				&& ((transaction.getTxn_status()).equalsIgnoreCase("0300")) &&
					(fields.get(FieldType.INTERNAL_ORIG_TXN_TYPE.getName()).equalsIgnoreCase(TransactionType.SALE.getName())))

		{
			status = StatusType.CAPTURED.getName();
			errorType = ErrorType.SUCCESS;

			if (StringUtils.isNotBlank(transaction.getTxn_msg())) {
				pgTxnMsg = transaction.getTxn_msg();
			} else {
				pgTxnMsg = ErrorType.SUCCESS.getResponseMessage();
			}

		}

		// For Refund Transaction success code is 0400
		else if ((StringUtils.isNotBlank(transaction.getTxn_status()))
				&& ((transaction.getTxn_status()).equalsIgnoreCase("0400"))&&
				(fields.get(FieldType.INTERNAL_ORIG_TXN_TYPE.getName()).equalsIgnoreCase(TransactionType.REFUND.getName())))

		{
			status = StatusType.CAPTURED.getName();
			errorType = ErrorType.SUCCESS;

			if (StringUtils.isNotBlank(transaction.getTxn_msg())) {
				pgTxnMsg = transaction.getTxn_msg();
			} else {
				pgTxnMsg = ErrorType.SUCCESS.getResponseMessage();
			}

		}
		
		else {
			if ((StringUtils.isNotBlank(transaction.getTxn_status()))) {

				IngenicoResultType resultInstance = IngenicoResultType.getInstanceFromName(transaction.getTxn_status());

				if (resultInstance != null) {
					status = resultInstance.getStatusCode();
					errorType = ErrorType.getInstanceFromCode(resultInstance.getiPayCode());
					pgTxnMsg = resultInstance.getMessage();
				} else {
					status = StatusType.FAILED_AT_ACQUIRER.getName();
					errorType = ErrorType.getInstanceFromCode("022");

					if (StringUtils.isNotBlank(transaction.getTxn_err_msg())) {
						pgTxnMsg = transaction.getTxn_err_msg();
					} else {
						pgTxnMsg = "Transaction failed at acquirer";
					}

				}

			} else {
				status = StatusType.REJECTED.getName();
				errorType = ErrorType.REJECTED;

				if (StringUtils.isNotBlank(transaction.getTxn_err_msg())) {
					pgTxnMsg = transaction.getTxn_err_msg();
				} else {
					pgTxnMsg = ErrorType.REJECTED.getResponseMessage();
				}

			}
		}

		fields.put(FieldType.STATUS.getName(), status);
		fields.put(FieldType.RESPONSE_MESSAGE.getName(), errorType.getResponseMessage());
		fields.put(FieldType.RESPONSE_CODE.getName(), errorType.getResponseCode());

		if (StringUtils.isNotBlank(transaction.getRqst_token())) {
			fields.put(FieldType.AUTH_CODE.getName(), transaction.getRqst_token());
		}

		else {
			fields.put(FieldType.AUTH_CODE.getName(), "0");
		}

		fields.put(FieldType.RRN.getName(), transaction.getTpsl_rfnd_id());
		fields.put(FieldType.ACQ_ID.getName(), transaction.getTpsl_txn_id());
		fields.put(FieldType.PG_RESP_CODE.getName(), transaction.getTxn_status());

		if (StringUtils.isNotBlank(transaction.getTxn_msg())) {
			fields.put(FieldType.PG_TXN_STATUS.getName(), transaction.getTxn_msg());
		} else {
			fields.put(FieldType.PG_TXN_STATUS.getName(), errorType.getResponseCode());
		}

		fields.put(FieldType.PG_TXN_MESSAGE.getName(), pgTxnMsg);
	}

}
