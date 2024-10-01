package com.pay10.atom;

import com.pay10.commons.exception.SystemException;
import com.pay10.commons.util.FieldType;
import com.pay10.commons.util.Fields;
import com.pay10.commons.util.PaymentType;
import com.pay10.commons.util.TransactionType;
import com.pay10.pg.core.util.AcquirerTxnAmountProvider;
import com.pay10.pg.core.util.AtomEncDecUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;

@Service("atomTransactionConverter")
public class TransactionConverter {

	@Autowired
	private AcquirerTxnAmountProvider acquirerTxnAmountProvider;

	@Autowired
	private AtomEncDecUtil atomEncDecUtil;

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
			logger.info("ATOM SaleRequest Fields = {} ",fields.getFieldsAsString());
			logger.info("ATOM SaleRequest transaction = {} ",transaction);

			String login = fields.get(FieldType.MERCHANT_ID.getName());
			//String pass = fields.get(FieldType.PASSWORD.getName());
			String pass = fields.get(FieldType.ADF11.getName());
			String reqHashKey = fields.get(FieldType.TXN_KEY.getName());
			String IV = fields.get(FieldType.ADF3.getName());
			String key = fields.get(FieldType.ADF4.getName());

			String domain = getMerchantDomain(fields);
			String domainName;
			if(null != domain) {
				URI uri = new URI(domain);
				domainName = uri.getHost();
			}else {
				URI uri = new URI(transaction.getRu());
				domainName = uri.getHost();
			}

			
			String signature_request = atomEncDecUtil.getEncodedValueWithSha2(reqHashKey, login, pass,
					transaction.getTtype(), transaction.getProdid(), transaction.getTxnid(), transaction.getAmt(),
					transaction.getTxncurr());

			StringBuilder paymentStringBuilder = new StringBuilder();

			paymentStringBuilder.append("login=");
			paymentStringBuilder.append(login);
			paymentStringBuilder.append("&pass=");
			paymentStringBuilder.append(pass);
			paymentStringBuilder.append("&ttype=");
			paymentStringBuilder.append(transaction.getTtype());
			paymentStringBuilder.append("&prodid=");
			paymentStringBuilder.append(transaction.getProdid());
			paymentStringBuilder.append("&amt=");
			paymentStringBuilder.append(transaction.getAmt());
			paymentStringBuilder.append("&txncurr=");
			paymentStringBuilder.append(transaction.getTxncurr());
			paymentStringBuilder.append("&txnscamt=");
			paymentStringBuilder.append(transaction.getTxnscamt());
			paymentStringBuilder.append("&clientcode=");
			paymentStringBuilder.append(transaction.getClientcode());
			paymentStringBuilder.append("&txnid=");
			paymentStringBuilder.append(transaction.getTxnid());
			paymentStringBuilder.append("&date=");
			paymentStringBuilder.append(transaction.getDate());
			paymentStringBuilder.append("&custacc=");
			paymentStringBuilder.append(transaction.getCustacc());
			
			paymentStringBuilder.append("&udf1=");
			paymentStringBuilder.append(transaction.getUdf1());
			paymentStringBuilder.append("&udf2=");
			paymentStringBuilder.append(transaction.getUdf2());
			paymentStringBuilder.append("&udf3=");
			paymentStringBuilder.append(transaction.getUdf3());
			if (fields.get(FieldType.PAYMENT_TYPE.getName()).equalsIgnoreCase(
					PaymentType.EMI.getCode())) {
				paymentStringBuilder.append("&udf5=");
				paymentStringBuilder.append(transaction.getUdf5());

				paymentStringBuilder.append("&udf6=");
				paymentStringBuilder.append(transaction.getUdf6());
			}
			paymentStringBuilder.append("&udf21=");
			paymentStringBuilder.append(fields.get(FieldType.ADF1.getName())+"~"+fields.get(FieldType.ADF9.getName())+"~"+domainName);

			
			if (fields.get(FieldType.PAYMENT_TYPE.getName()).equalsIgnoreCase(PaymentType.NET_BANKING.getCode())) {
				paymentStringBuilder.append("&bankid=");
				paymentStringBuilder.append(transaction.getBankid());
			}
			
			else if (fields.get(FieldType.PAYMENT_TYPE.getName()).equalsIgnoreCase(PaymentType.CREDIT_CARD.getCode()) ||
					fields.get(FieldType.PAYMENT_TYPE.getName()).equalsIgnoreCase(PaymentType.DEBIT_CARD.getCode())) {
				paymentStringBuilder.append("&mdd=");
				paymentStringBuilder.append(transaction.getMdd());
			}
			else if (fields.get(FieldType.PAYMENT_TYPE.getName()).equalsIgnoreCase(PaymentType.UPI.getCode())) {
				paymentStringBuilder.append("&mdd=");
				paymentStringBuilder.append(transaction.getMdd());
			} else if (fields.get(FieldType.PAYMENT_TYPE.getName()).
					equalsIgnoreCase(PaymentType.EMI.getCode())) {
				paymentStringBuilder.append("&mdd=");
				paymentStringBuilder.append(transaction.getMdd());
			}
		
			paymentStringBuilder.append("&ru=");
			paymentStringBuilder.append(transaction.getRu());
			paymentStringBuilder.append("&signature=");
			paymentStringBuilder.append(signature_request);

			logger.info("Atom Sale Request : " + paymentStringBuilder.toString());
			String encData = atomEncDecUtil.encrypt(paymentStringBuilder.toString(), key, IV);
			return encData;

		}

		catch (Exception e) {
			logger.error("Exception in generating ATOM request ", e);
		}

		return null;
	}

	public String refundRequest(Fields fields, Transaction transaction) throws SystemException {

		StringBuilder refundString = new StringBuilder();

		refundString.append(Constants.MERCHANTID + "=");
		refundString.append(transaction.getLogin());
		refundString.append("&" + Constants.PWD + "=");
		refundString.append(transaction.getPass());
		refundString.append("&" + Constants.ATOM_TXN_ID + "=");
		refundString.append(transaction.getTxnid());
		refundString.append("&" + Constants.REFUND_AMT + "=");
		refundString.append(transaction.getAmt());
		refundString.append("&" + Constants.TXNDATE + "=");
		refundString.append(transaction.getDate());
		refundString.append("&" + Constants.MER_REFUN_REF + "=");
		refundString.append(transaction.getMerefundref());

		return refundString.toString();
	}

	public String statusEnquiryRequest(Fields fields, Transaction transaction) {
		
		StringBuilder stsEnqString = new StringBuilder();

		stsEnqString.append(Constants.MERCHANTID + "=");
		stsEnqString.append(transaction.getLogin());
		stsEnqString.append("&" + Constants.MERCHANT_TXN_ID + "=");
		stsEnqString.append(fields.get(FieldType.PG_REF_NUM.getName()));
		stsEnqString.append("&" + Constants.AMT + "=");
		stsEnqString.append(transaction.getAmt());
		stsEnqString.append("&" + Constants.TDATE + "=");
		stsEnqString.append(transaction.getDate());
logger.info("@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@"+stsEnqString.toString());
		return stsEnqString.toString();

	}

	public Transaction toTransaction(String xmlResponse, String txnType) {

		Transaction transaction = new Transaction();
		
		if (txnType.equalsIgnoreCase(TransactionType.REFUND.getName())) {
			
			String responseXml = getTextBetweenTags(xmlResponse,REFUND_OPEN_TAG,REFUND_CLOSE_TAG);
			
			String merchantId = getTextBetweenTags(responseXml,MERCHANTID_OPEN_TAG,MERCHANTID_CLOSE_TAG);
			String txnId = getTextBetweenTags(responseXml,TXNID_OPEN_TAG,TXNID_CLOSE_TAG);
			String amount = getTextBetweenTags(responseXml,AMOUNT_OPEN_TAG,AMOUNT_CLOSE_TAG);
			String statusCode = getTextBetweenTags(responseXml,STATUSCODE_OPEN_TAG,STATUSCODE_CLOSE_TAG);
			String statusMessage = getTextBetweenTags(responseXml,STATUSMESSAGE_OPEN_TAG,STATUSMESSAGE_CLOSE_TAG);
			String atomRefundId = getTextBetweenTags(responseXml,ATOMREFUNDID_OPEN_TAG,ATOMREFUNDID_CLOSE_TAG);
			
			transaction.setMerchant_id(merchantId);
			transaction.setMmp_txn(txnId);
			transaction.setAmt(amount);
			transaction.setF_code(statusCode);
			transaction.setStatusMessage(statusMessage);
			transaction.setBank_txn(atomRefundId);
			
			return transaction;
		}

		if (txnType.equalsIgnoreCase(TransactionType.STATUS.getName())) {
logger.info("@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@"+xmlResponse);
			String response = getTextBetweenTags(xmlResponse, "<VerifyOutput ", "/>");
			
			String MerchantID = null;
			String MerchantTxnID = null;
			String AMT = null;
			String VERIFIED = null;
			String BID = null;
			String bankname = null;
			String atomtxnId = null;
			String discriminator = null;
			String reconstatus = null;
			
		}

		return null;
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
	
	public static String getTextForTag(String text, String tag1) {

		StringBuilder sb = new StringBuilder();
		int leftIndex = text.indexOf(tag1);
		if (leftIndex == -1) {
			return null;
		}

		int startIndex = tag1.length() + leftIndex + 2;
		
		for (int i=0;i<text.length() - startIndex ; i ++) {
			
			char txt = text.charAt(startIndex + i);
			String txtStr = String.valueOf(txt);
			if (txtStr.equalsIgnoreCase("\"")) {
				break;
			}
			sb.append(txtStr);
			
		}
		return sb.toString();
	}

	public TransactionConverter() {
	}
	
	private String getMerchantDomain(Fields fields) {

		String internalRequestFields = fields.get(FieldType.INTERNAL_REQUEST_FIELDS.getName());
		if (null != internalRequestFields) {
			String[] paramaters = internalRequestFields.split("~");
			Map<String, String> paramMap = new HashMap<String, String>();
			for (String param : paramaters) {
				String[] parameterPair = param.split("=");
				if (parameterPair.length > 1) {
					paramMap.put(parameterPair[0].trim(), parameterPair[1].trim());
				}
			}
			return paramMap.get(FieldType.RETURN_URL.getName());
		}

		return null;
	}


}
