package com.pay10.apbl;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.pay10.commons.exception.SystemException;
import com.pay10.commons.util.FieldType;
import com.pay10.commons.util.Fields;
import com.pay10.commons.util.PropertiesManager;
import com.pay10.commons.util.TransactionType;
import com.pay10.pg.core.util.AcquirerTxnAmountProvider;

/**
 * @author Shaiwal
 *
 */
@Service("apblTransactionConverter")
public class TransactionConverter {

	@Autowired
	private AcquirerTxnAmountProvider acquirerTxnAmountProvider;


	private static final Logger logger = LoggerFactory.getLogger(TransactionConverter.class.getName());

	@SuppressWarnings("incomplete-switch")
	public String perpareRequest(Fields fields, Transaction transaction) throws SystemException {

		String request = null;

		switch (TransactionType.getInstance(fields.get(FieldType.TXNTYPE.getName()))) {
		case AUTHORISE:
		case ENROLL:
			break;
		case REFUND:
			request = refundRequest(fields, transaction);
			break;
		case SALE:
			request = saleRequest(fields, transaction);
			break;
		}
		return request.toString();

	}

	public String saleRequest(Fields fields, Transaction transaction) throws SystemException {

		try {

			String requestURL = PropertiesManager.propertiesMap.get("APBLSaleUrl");

			StringBuilder sb = new StringBuilder();

			sb.append(requestURL);
			sb.append(Constants.MID);
			sb.append("=");
			sb.append(transaction.getMID());
			sb.append("&");
			sb.append(Constants.TXN_REF_NO);
			sb.append("=");
			sb.append(transaction.getTXN_REF_NO());
			sb.append("&");
			sb.append(Constants.SU);
			sb.append("=");
			sb.append(transaction.getSU());
			sb.append("&");
			sb.append(Constants.FU);
			sb.append("=");
			sb.append(transaction.getFU());
			sb.append("&");
			sb.append(Constants.AMT);
			sb.append("=");
			sb.append(transaction.getAMT());
			sb.append("&");
			sb.append(Constants.DATE);
			sb.append("=");
			sb.append(transaction.getDATE());
			sb.append("&");
			sb.append("CUR");
			sb.append("=");
			sb.append(transaction.getCUR());
			sb.append("&");
			sb.append(Constants.HASH);
			sb.append("=");
			sb.append(transaction.getHASH());
			sb.append("&");
			sb.append(Constants.service);
			sb.append("=");
			sb.append(transaction.getService());

			logger.info("APBL payment request  =  " + sb.toString());

			return sb.toString();
		}

		catch (Exception e) {
			logger.error("Exception in generating APBL request ", e);
		}

		return null;
	}

	public String refundRequest(Fields fields, Transaction transaction) throws SystemException {

		JSONObject refundObj = new JSONObject();

		refundObj.put("feSessionId", transaction.getFeSessionId());
		refundObj.put("txnId", transaction.getTxnId());
		refundObj.put("txnDate", transaction.getDATE());
		refundObj.put("request", transaction.getRequest());
		refundObj.put("merchantId", transaction.getMID());
		refundObj.put("hash", transaction.getHASH());
		refundObj.put("amount", transaction.getAMT());

		return refundObj.toString();
	}


	public Transaction toTransaction(String jsonResponse, String txnType) {

		Transaction transaction = new Transaction();

		if (txnType.equalsIgnoreCase(TransactionType.REFUND.getName())) {

			if (StringUtils.isNotBlank(jsonResponse)) {

				JSONObject respObj = new JSONObject(jsonResponse);

				if (respObj.has("messageText")) {
					transaction.setMessageText(String.valueOf(respObj.get("messageText")));
				}

				if (respObj.has("txnId")) {
					transaction.setTxnId(String.valueOf(respObj.get("txnId")));
				}

				if (respObj.has("status")) {
					transaction.setSTATUS(String.valueOf(respObj.get("status")));
				}

				if (respObj.has("code")) {
					transaction.setCODE(String.valueOf(respObj.get("code")));
				}

				if (respObj.has("errorCode")) {
					transaction.setErrorCode(String.valueOf(respObj.get("errorCode")));
				}

				if (respObj.has("new_txn_id")) {
					transaction.setNew_txn_id(String.valueOf(respObj.get("new_txn_id")));
				}

			}

			return transaction;
		}

		return null;
	}

	public TransactionConverter() {

	}

}
