package com.pay10.ingenico;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.pay10.commons.exception.SystemException;
import com.pay10.commons.util.FieldType;
import com.pay10.commons.util.Fields;
import com.pay10.commons.util.TransactionType;
import com.pay10.pg.core.ingenico.util.TransactionRequestBean;
import com.pay10.pg.core.util.AcquirerTxnAmountProvider;

/**
 * @author Shaiwal
 *
 */
@Service("ingenicoTransactionConverter")
public class TransactionConverter {

	@Autowired
	private AcquirerTxnAmountProvider acquirerTxnAmountProvider;

	private static final Logger logger = LoggerFactory.getLogger(TransactionConverter.class.getName());

	// Response for Refund Transactions
	public static final String REFUND_OPEN_TAG = "<REFUND>";
	public static final String REFUND_CLOSE_TAG = "</REFUND>";
	public static final String MERCHANTID_OPEN_TAG = "<MERCHANTID>";
	public static final String MERCHANTID_CLOSE_TAG = "</MERCHANTID>";
	public static final String TXNID_OPEN_TAG = "<TXNID>";
	public static final String TXNID_CLOSE_TAG = "</TXNID>";
	public static final String AMOUNT_OPEN_TAG = "<AMOUNT>";
	public static final String AMOUNT_CLOSE_TAG = "</AMOUNT>";
	public static final String STATUSCODE_OPEN_TAG = "<STATUSCODE>";
	public static final String STATUSCODE_CLOSE_TAG = "</STATUSCODE>";
	public static final String STATUSMESSAGE_OPEN_TAG = "<STATUSMESSAGE>";
	public static final String STATUSMESSAGE_CLOSE_TAG = "</STATUSMESSAGE>";
	public static final String ATOMREFUNDID_OPEN_TAG = "<ATOMREFUNDID>";
	public static final String ATOMREFUNDID_CLOSE_TAG = "</ATOMREFUNDID>";

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
		case CAPTURE:
			break;

		case SETTLE:
			break;
		case STATUS:
			request = statusEnquiryRequest(fields, transaction);
			break;
		}
		return request.toString();

	}

	public String saleRequest(Fields fields, Transaction transaction) throws SystemException {

		try {
			TransactionRequestBean objTransactionRequestBean = new TransactionRequestBean();

			objTransactionRequestBean.setStrRequestType(transaction.getRequestType());
			objTransactionRequestBean.setStrMerchantCode(transaction.getMerchantCode());
			objTransactionRequestBean.setMerchantTxnRefNumber(transaction.getMerchantTxnRefNum());// always new
			objTransactionRequestBean.setStrAmount(transaction.getAmount());
			objTransactionRequestBean.setStrCurrencyCode(transaction.getCurrencyCode());
			objTransactionRequestBean.setStrITC(transaction.getCustName());
			objTransactionRequestBean.setStrReturnURL(transaction.getReturnUrl());
			objTransactionRequestBean.setStrShoppingCartDetails(transaction.getShoppingCartDet());
			objTransactionRequestBean.setTxnDate(transaction.getTxnDate());
			objTransactionRequestBean.setStrBankCode(transaction.getBankCode());//
			objTransactionRequestBean.setWebServiceLocator(transaction.getWebServiceLoc());

			if (StringUtils.isNotBlank(transaction.getMobileNum())) {
				objTransactionRequestBean.setStrMobileNumber(transaction.getMobileNum());
			}

			objTransactionRequestBean.setKey(transaction.getKey().getBytes());
			objTransactionRequestBean.setIv(transaction.getIv().getBytes());
			objTransactionRequestBean.setStrCustomerName(transaction.getCustName());
			// objTransactionRequestBean.setStrEmail("test@pay10.com");
			objTransactionRequestBean.setStrTimeOut(transaction.getTimeout());
			objTransactionRequestBean.setCardNo(transaction.getCardNum());
			objTransactionRequestBean.setCardName(transaction.getCustName());
			objTransactionRequestBean.setCardExpMM(transaction.getExpMM());
			objTransactionRequestBean.setCardExpYY(transaction.getExpYYYY());
			objTransactionRequestBean.setCardCVV(transaction.getCardCvv());

			String token = objTransactionRequestBean.getTransactionToken();
			return token;

		}

		catch (Exception e) {
			logger.error("Exception in generating ATOM request ", e);
		}

		return null;
	}

	public String refundRequest(Fields fields, Transaction transaction) throws SystemException {

		TransactionRequestBean objTransactionRequestBean = new TransactionRequestBean();
		objTransactionRequestBean.setStrRequestType(transaction.getRequestType());
		objTransactionRequestBean.setStrMerchantCode(transaction.getMerchantCode());
		objTransactionRequestBean.setMerchantTxnRefNumber(transaction.getMerchantTxnRefNum());
		objTransactionRequestBean.setTxnDate(transaction.getTxnDate());
		objTransactionRequestBean.setStrBankCode(transaction.getBankCode());
		objTransactionRequestBean.setWebServiceLocator(transaction.getWebServiceLoc());
		objTransactionRequestBean.setKey(transaction.getKey().getBytes());
		objTransactionRequestBean.setIv(transaction.getIv().getBytes());
		objTransactionRequestBean.setStrTimeOut(transaction.getTimeout());
		objTransactionRequestBean.setStrAmount(transaction.getAmount());
		objTransactionRequestBean.setStrTPSLTxnID(transaction.getTpsl_txn_id());
		String token = objTransactionRequestBean.getTransactionToken();
		return token;
	}

	public String statusEnquiryRequest(Fields fields, Transaction transaction) {

		StringBuilder stsEnqString = new StringBuilder();

		/*
		 * stsEnqString.append(Constants.MERCHANTID + "=");
		 * stsEnqString.append(transaction.getLogin()); stsEnqString.append("&" +
		 * Constants.MERCHANT_TXN_ID + "=");
		 * stsEnqString.append(fields.get(FieldType.PG_REF_NUM.getName()));
		 * stsEnqString.append("&" + Constants.AMT + "=");
		 * stsEnqString.append(transaction.getAmt()); stsEnqString.append("&" +
		 * Constants.TDATE + "="); stsEnqString.append(transaction.getDate());
		 */

		return stsEnqString.toString();

	}

	public Transaction toTransaction(String response, String txnType) {

		Transaction transaction = new Transaction();

		if (StringUtils.isBlank(response)) {
			return transaction;
		}

		if (txnType.equalsIgnoreCase(TransactionType.REFUND.getName())) {

			String responseArray[] = response.split(Pattern.quote("|"));
			Map<String, String> responseMap = new HashMap<String, String>();

			for (String data : responseArray) {

				String dataArray[] = data.split("=");

				// Auth Code Sent is blank , hence check array length > 1 for response map
				if (dataArray.length > 1) {
					responseMap.put(dataArray[0], dataArray[1]);
				}

			}

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

		}

		return transaction;

	}

	public TransactionConverter() {
	}

}
