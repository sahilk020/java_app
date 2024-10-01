package com.pay10.axisbank.netbanking;

import java.security.MessageDigest;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.pay10.commons.exception.SystemException;
import com.pay10.commons.util.FieldType;
import com.pay10.commons.util.Fields;
import com.pay10.commons.util.TransactionType;
import com.pay10.pg.core.util.AcquirerTxnAmountProvider;
import com.pay10.pg.core.util.AxisBankNBEncDecService;

/**
 * @author Shaiwal
 *
 */
@Service("axisBankNBTransactionConverter")
public class TransactionConverter {

	@Autowired
	private AcquirerTxnAmountProvider acquirerTxnAmountProvider;

	@Autowired
	private AxisBankNBEncDecService axisBankNBEncDecService;

	private static final Logger logger = LoggerFactory.getLogger(TransactionConverter.class.getName());

	public static final String PAYEEID_OPEN_TAG = "<PAYEEID>";
	public static final String PAYEEID_CLOSE_TAG = "</PAYEEID>";
	public static final String ITC_OPEN_TAG = "<ITC>";
	public static final String ITC_CLOSE_TAG = "</ITC>";
	public static final String PRN_OPEN_TAG = "<PRN>";
	public static final String PRN_CLOSE_TAG = "</PRN>";
	public static final String PAYMENT_DATE_OPEN_TAG = "<PaymentDate>";
	public static final String PAYMENT_DATE_CLOSE_TAG = "</PaymentDate>";
	public static final String AMOUNT_OPEN_TAG = "<Amount>";
	public static final String AMOUNT_CLOSE_TAG = "</Amount>";
	public static final String BID_OPEN_TAG = "<BID>";
	public static final String BID_CLOSE_TAG = "</BID>";
	public static final String PAYMENT_STATUS_OPEN_TAG = "<PaymentStatus>";
	public static final String PAYMENT_STATUS_CLOSE_TAG = "</PaymentStatus>";

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

		StringBuilder requestString = new StringBuilder();
		String encryptedString = null;
		try {

			requestString.append(Constants.PRN);
			requestString.append(Constants.AXIS_NB_EQUATOR);
			requestString.append(transaction.getPrn());
			requestString.append(Constants.AXIS_NB_SEPARATOR);
			requestString.append(Constants.PID);
			requestString.append(Constants.AXIS_NB_EQUATOR);
			requestString.append(transaction.getPid());
			requestString.append(Constants.AXIS_NB_SEPARATOR);
			requestString.append(Constants.MD);
			requestString.append(Constants.AXIS_NB_EQUATOR);
			requestString.append(transaction.getMd());
			requestString.append(Constants.AXIS_NB_SEPARATOR);
			requestString.append(Constants.ITC);
			requestString.append(Constants.AXIS_NB_EQUATOR);
			requestString.append(transaction.getItc());
			requestString.append(Constants.AXIS_NB_SEPARATOR);
			requestString.append(Constants.CRN);
			requestString.append(Constants.AXIS_NB_EQUATOR);
			requestString.append(transaction.getCrn());
			requestString.append(Constants.AXIS_NB_SEPARATOR);
			requestString.append(Constants.AMT);
			requestString.append(Constants.AXIS_NB_EQUATOR);
			requestString.append(transaction.getAmt());
			requestString.append(Constants.AXIS_NB_SEPARATOR);
			requestString.append(Constants.RESPONSE);
			requestString.append(Constants.AXIS_NB_EQUATOR);
			requestString.append(transaction.getResponse());
			requestString.append(Constants.AXIS_NB_SEPARATOR);
			requestString.append(Constants.CG);
			requestString.append(Constants.AXIS_NB_EQUATOR);
			requestString.append(transaction.getCg());

			logger.info("Axis Bank Net Banking Request String >> " + requestString.toString());

			String checksum = sha256(requestString.toString());

			requestString.append(Constants.AXIS_NB_SEPARATOR);
			requestString.append(Constants.CHKSUM);
			requestString.append(Constants.AXIS_NB_EQUATOR);
			requestString.append(checksum);

			logger.info("Axis Bank Net Banking Request String after checksum >> " + requestString.toString());
			// encryptedString =
			// axisBankNBEncDecService.AESEncrypt(requestString.toString(),
			// fields.get(FieldType.ADF8.getName()));
			
			encryptedString = axisBankNBEncDecService.encrypt(fields.get(FieldType.ADF4.getName()),
					fields.get(FieldType.ADF5.getName()), fields.get(FieldType.ADF8.getName()), requestString.toString());
			
			logger.info("Axis Bank Net Banking Encrypted Request String >> " + encryptedString.toString());
			return encryptedString;

		}

		catch (Exception e) {
			logger.error("Exception in generating Axis Bank NB Sale Request ", e);
			return null;
		}

	}

	// code added by sonu
	public static String sha256(final String base) {
		try {
			final MessageDigest digest = MessageDigest.getInstance("SHA-256");
			final byte[] hash = digest.digest(base.getBytes("UTF-8"));
			final StringBuilder hexString = new StringBuilder();
			for (int i = 0; i < hash.length; i++) {
				final String hex = Integer.toHexString(0xff & hash[i]);
				if (hex.length() == 1)
					hexString.append('0');
				hexString.append(hex);
			}
			return hexString.toString();
		} catch (Exception ex) {
			throw new RuntimeException(ex);
		}
	}

	public String refundRequest(Fields fields, Transaction transaction) throws SystemException {

		return null;

	}

	public String statusEnquiryRequest(Fields fields, Transaction transaction) {

		String checksumString = null;
		checksumString = "payeeid=" + transaction.getPayeeId() + "|itc=" + transaction.getItc() + "|prn="
				+ transaction.getPrn() + "|date=" + transaction.getDate() + "|amt=" + transaction.getAmt();
		String checksumHash = axisBankNBEncDecService.getSha256(checksumString);

		String payString = "payeeid=" + transaction.getPayeeId() + "|itc=" + transaction.getItc() + "|prn="
				+ transaction.getPrn() + "|date=" + transaction.getDate() + "|amt=" + transaction.getAmt() + "|chksum="
				+ checksumHash;

		String payStringEncrypted = axisBankNBEncDecService.AESEncrypt(payString, fields.get(FieldType.ADF9.getName()));

		return payStringEncrypted;
	}

	public Transaction toTransaction(String xmlResponse, String txnType) {

		Transaction transaction = new Transaction();

		if (StringUtils.isBlank(xmlResponse)) {
			return transaction;
		}

		if (StringUtils.isBlank(txnType)) {
			return transaction;
		}

		if (txnType.equalsIgnoreCase(TransactionType.STATUS.getName())) {

			if (xmlResponse.contains("Table1")) {
				String innerXml = getTextBetweenTags(xmlResponse, "<Table1>", "</Table1>");

				String payeeId = getTextBetweenTags(innerXml, PAYEEID_OPEN_TAG, PAYEEID_CLOSE_TAG);
				String itc = getTextBetweenTags(innerXml, ITC_OPEN_TAG, ITC_CLOSE_TAG);
				String prn = getTextBetweenTags(innerXml, PRN_OPEN_TAG, PRN_CLOSE_TAG);
				String paymentDate = getTextBetweenTags(innerXml, PAYMENT_DATE_OPEN_TAG, PAYMENT_DATE_CLOSE_TAG);
				String amount = getTextBetweenTags(innerXml, AMOUNT_OPEN_TAG, AMOUNT_CLOSE_TAG);
				String bid = getTextBetweenTags(innerXml, BID_OPEN_TAG, BID_CLOSE_TAG);
				String paymentStatus = getTextBetweenTags(innerXml, PAYMENT_STATUS_OPEN_TAG, PAYMENT_STATUS_CLOSE_TAG);

				logger.info("Response received for status enquiry , AxisBank NB >>>>> " + innerXml);

				transaction.setItc(itc);
				transaction.setPrn(prn);
				transaction.setBid(bid);
				transaction.setPid(payeeId);
				transaction.setStatFlg(paymentStatus);
				transaction.setAmt(amount);

				return transaction;
			} else {
				return transaction;
			}

		}
		return transaction;
	}

	public TransactionConverter() {
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
	}// getTextBetweenTags()
}
