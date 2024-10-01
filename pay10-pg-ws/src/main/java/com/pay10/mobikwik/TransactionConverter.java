package com.pay10.mobikwik;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.pay10.commons.exception.SystemException;
import com.pay10.commons.util.FieldType;
import com.pay10.commons.util.Fields;
import com.pay10.commons.util.TransactionType;
import com.pay10.pg.core.util.AcquirerTxnAmountProvider;

/**
 * @author Shaiwal
 *
 */
@Service("mobikwikTransactionConverter")
public class TransactionConverter {

	@Autowired
	private AcquirerTxnAmountProvider acquirerTxnAmountProvider;


	private static final Logger logger = LoggerFactory.getLogger(TransactionConverter.class.getName());

	public static final String STATUS_OPEN_TAG = "<status>";
	public static final String STATUS_CLOSE_TAG = "</status>";
	public static final String STATUS_CODE_OPEN_TAG = "<statuscode>";
	public static final String STATUS_CODE_CLOSE_TAG = "</statuscode>";
	public static final String STATUS_MESSAGE_OPEN_TAG = "<statusmessage>";
	public static final String STATUS_MESSAGE_CLOSE_TAG = "</statusmessage>";
	public static final String TXNID_OPEN_TAG = "<txid>";
	public static final String TXNID_CLOSE_TAG = "</txid>";
	public static final String REFID_OPEN_TAG = "<refId>";
	public static final String REFID_CLOSE_TAG = "</refId>";
	
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


			StringBuilder txnString =  new StringBuilder(); 
			
			
			txnString.append(Constants.mid + "=");
			txnString.append(transaction.getMerchantIdentifier());
			txnString.append("&");
			txnString.append(Constants.orderid + "=");
			txnString.append(transaction.getOrderId());
			txnString.append("&");
			txnString.append(Constants.redirecturl + "=");
			txnString.append(transaction.getReturnUrl());
			txnString.append("&");
			txnString.append(Constants.amount + "=");
			txnString.append(transaction.getAmount());
			txnString.append("&");
			txnString.append(Constants.version + "=");
			txnString.append("2");
			txnString.append("&");
			txnString.append(Constants.merchantname + "=");
			txnString.append("pay10");
			txnString.append("&");
			txnString.append(Constants.checksum + "=");
			txnString.append(transaction.getChecksum());
			
			
			logger.info("Mobikwik payment request  =  " + txnString.toString());

			return txnString.toString();
		}

		catch (Exception e) {
			logger.error("Exception in generating Mobikwik request ", e);
		}

		return null;
	}

	public String refundRequest(Fields fields, Transaction transaction) throws SystemException {

		StringBuilder request = new StringBuilder();
		
		request.append("mid=");
		request.append(transaction.getMerchantIdentifier());
		request.append("&");
		request.append("txid=");
		request.append(transaction.getTxid());
		request.append("&");
		request.append("amount=");
		request.append(transaction.getAmount());
		request.append("&");
		request.append("checksum=");
		request.append(transaction.getChecksum());
		
		if (fields.get(FieldType.REFUND_FLAG.getName()).equalsIgnoreCase("C")) {
			request.append("&");
			request.append("ispartial=yes");
		}
		return request.toString();
	}


	public Transaction toTransaction(String xmlResponse, String txnType) {

		
		  Transaction transaction = new Transaction();
		  
		  String status = getTextBetweenTags(xmlResponse,STATUS_OPEN_TAG,STATUS_CLOSE_TAG);
		  String statuscode = getTextBetweenTags(xmlResponse,STATUS_CODE_OPEN_TAG,STATUS_CODE_CLOSE_TAG);
		  String statusmessage = getTextBetweenTags(xmlResponse,STATUS_MESSAGE_OPEN_TAG,STATUS_MESSAGE_CLOSE_TAG);
		  String txid = getTextBetweenTags(xmlResponse,TXNID_OPEN_TAG,TXNID_CLOSE_TAG);
		  String refId = getTextBetweenTags(xmlResponse,REFID_OPEN_TAG,REFID_CLOSE_TAG);
		  
		  transaction.setStatus(status);
		  transaction.setStatuscode(statuscode);
		  transaction.setStatusmessage(statusmessage);
		  transaction.setTxid(txid);
		  transaction.setRefId(refId);
		  
		  return transaction; 
		 
	}

	public TransactionConverter() {

	}
	
	public String getTextBetweenTags(String text, String tag1, String tag2) {

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
	}// getTextBetweenTags()

}
