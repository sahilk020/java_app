package com.pay10.axisbank.card;

import java.security.spec.KeySpec;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;

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

/**
 * @author Shaiwal
 *
 */
@Service("axisBankCardTransactionConverter")
public class TransactionConverter {

	@Autowired
	private AcquirerTxnAmountProvider acquirerTxnAmountProvider;

	private static byte[] SALT = new byte[] { -57, -75, -103, -12, 75, 124, -127, 119 };
	private static Map<String, SecretKey> encDecMap = new HashMap<String, SecretKey>();

	private static final Logger logger = LoggerFactory.getLogger(TransactionConverter.class.getName());

	public static final String DOUBLE_PIPE = "||";
	public static final String PIPE = "|";

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

		return null;
	}

	public String refundRequest(Fields fields, Transaction transaction) throws SystemException {

		StringBuilder requestString = new StringBuilder();
		requestString.append(fields.get(FieldType.MERCHANT_ID.getName()));
		requestString.append("||");
		requestString.append(fields.get(FieldType.ADF1.getName()));
		requestString.append("");
		requestString.append("||");

		StringBuilder refundString = new StringBuilder();

		// Outer BEI to show two FEIs present
		refundString.append("11");
		refundString.append("||");

		// FEI-1 BEI to show 2 or 1 fields present

		if (StringUtils.isNotBlank(fields.get(FieldType.ORIG_TXN_ID.getName()))) {
			refundString.append("11");
			refundString.append("|");
			refundString.append(fields.get(FieldType.ACQ_ID.getName()));
			refundString.append("|");
			refundString.append(fields.get(FieldType.ORIG_TXN_ID.getName()));
			refundString.append("||");
		} else {
			refundString.append("10");
			refundString.append("|");
			refundString.append(fields.get(FieldType.ACQ_ID.getName()));
			refundString.append("||");
		}

		String amount = acquirerTxnAmountProvider.amountProvider(fields);
		refundString.append("110");
		refundString.append("|");
		refundString.append(fields.get(FieldType.PG_REF_NUM.getName()));
		refundString.append("|");
		refundString.append(amount);

		String encryptionKey = fields.get(FieldType.TXN_KEY.getName());
		SecretKey secretKey = null;

		if (encDecMap.get(encryptionKey) != null) {
			secretKey = encDecMap.get(encryptionKey);
		} else {
			try {
				SecretKeyFactory secretKeyFactory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
				KeySpec keySpec = new PBEKeySpec(encryptionKey.toCharArray(), SALT, 65536, 256);
				SecretKey secretKeyTemp = secretKeyFactory.generateSecret(keySpec);
				secretKey = new SecretKeySpec(secretKeyTemp.getEncoded(), "AES");
			} catch (Exception e) {
				e.printStackTrace();
			}
			encDecMap.put(encryptionKey, secretKey);
		}

		String encRequest = null;
		requestString.append(encRequest);

		return requestString.toString();

	}

	public String statusEnquiryRequest(Fields fields, Transaction transaction) {

		StringBuilder requestString = new StringBuilder();

		try {

			requestString.append(fields.get(FieldType.MERCHANT_ID.getName()));
			requestString.append("||");
			requestString.append(fields.get(FieldType.ADF1.getName()));
			requestString.append("||");

			StringBuilder enquiryString = new StringBuilder();

			// Outer BEI to show one FEI present
			enquiryString.append("1");
			enquiryString.append("||");

			// FEI-1 BEI to show 2 or 1 fields present

			if (StringUtils.isNotBlank(fields.get(FieldType.ACQ_ID.getName()))) {
				enquiryString.append("110");
				enquiryString.append("|");
				enquiryString.append(fields.get(FieldType.ACQ_ID.getName()));
				enquiryString.append("|");
				enquiryString.append(fields.get(FieldType.PG_REF_NUM.getName()));
			} else {
				enquiryString.append("010");
				enquiryString.append("|");
				enquiryString.append(fields.get(FieldType.PG_REF_NUM.getName()));
			}

			String encryptionKey = fields.get(FieldType.TXN_KEY.getName());
			SecretKey secretKey = null;

			if (encDecMap.get(encryptionKey) != null) {
				secretKey = encDecMap.get(encryptionKey);
			} else {
				try {
					SecretKeyFactory secretKeyFactory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
					KeySpec keySpec = new PBEKeySpec(encryptionKey.toCharArray(), SALT, 65536, 256);
					SecretKey secretKeyTemp = secretKeyFactory.generateSecret(keySpec);
					secretKey = new SecretKeySpec(secretKeyTemp.getEncoded(), "AES");
				} catch (Exception e) {
					e.printStackTrace();
				}
				encDecMap.put(encryptionKey, secretKey);
			}

			String encRequest = null;
			requestString.append(encRequest);
			return requestString.toString();
		}

		catch (Exception e) {
			logger.error("Exception in preapring direcpay status enquiry request", e);
		}
		return requestString.toString();
	}
	
	

	public Transaction toTransaction(String pipedResponse, String txnType) {

		Transaction transaction = new Transaction();
		String pipedResponseArray[] = pipedResponse.split(Pattern.quote("||"));

		if (txnType.equalsIgnoreCase(TransactionType.REFUND.getName())) {

			String responseBlockBitMapArray[] = pipedResponseArray[0].split("");
			int feiCounter = 1;

			if (responseBlockBitMapArray[0].equals("1")) {
				String txnData = pipedResponseArray[feiCounter];
				feiCounter = feiCounter + 1;
				String txnDataArray[] = txnData.split(Pattern.quote("|"));
				String txnDataBitmapArray[] = txnDataArray[0].split("");

				if (txnDataBitmapArray[0].equals("1")) {
					transaction.setResponse_GtwTraceNum(txnDataArray[1]);
				}

				if (txnDataBitmapArray[1].equals("1")) {
					transaction.setResponse_Mer_Ord_Num(txnDataArray[2]);
				}

				if (txnDataBitmapArray[2].equals("1")) {
					transaction.setResponse_Ref_Num(txnDataArray[3]);
				}
			}

			if (responseBlockBitMapArray[1].equals("1")) {
				String amtData = pipedResponseArray[feiCounter];
				feiCounter = feiCounter + 1;
				String amtDataArray[] = amtData.split(Pattern.quote("|"));
				String amtDataBitmapArray[] = amtDataArray[0].split("");

				if (amtDataBitmapArray[0].equals("1")) {
					transaction.setStatus_refundAmt(amtDataArray[1]);
				}

				if (amtDataBitmapArray[1].equals("1")) {
					transaction.setStatus_refundAmtAvl(amtDataArray[2]);
				}

				if (amtDataBitmapArray[2].equals("1")) {
					transaction.setCurrency(amtDataArray[3]);
				}
			}

			if (responseBlockBitMapArray[2].equals("1")) {
				String statusData = pipedResponseArray[feiCounter];
				String statusDataArray[] = statusData.split(Pattern.quote("|"));
				String statusDataBitmapArray[] = statusDataArray[0].split("");

				if (statusDataBitmapArray[0].equals("1")) {
					transaction.setResponse_Sts_Flag(statusDataArray[1]);
				}

				if (statusDataBitmapArray[1].equals("1")) {
					transaction.setResponse_Err_Code(statusDataArray[2]);
				}

				if (statusDataBitmapArray[2].equals("1")) {
					transaction.setResponse_Err_Msg(statusDataArray[3]);
				}

			}

		} else if (txnType.equalsIgnoreCase(TransactionType.STATUS.getName())) {

			String responseBlockBitMapArray[] = pipedResponseArray[0].split("");
			int feiCounter = 1;

			if (responseBlockBitMapArray[0].equals("1")) {
				String txnData = pipedResponseArray[feiCounter];
				feiCounter = feiCounter + 1;
				String txnDataArray[] = txnData.split(Pattern.quote("|"));
				String txnDataBitmapArray[] = txnDataArray[0].split("");

				if (txnDataBitmapArray[0].equals("1")) {
					transaction.setResponse_Ref_Num(txnDataArray[1]);
				}

				if (txnDataBitmapArray[1].equals("1")) {
					transaction.setResponse_Mer_Ord_Num(txnDataArray[2]);
				}

			}

			if (responseBlockBitMapArray[1].equals("1")) {
				String amtData = pipedResponseArray[feiCounter];
				feiCounter = feiCounter + 1;
				String amtDataArray[] = amtData.split(Pattern.quote("|"));
				String amtDataBitmapArray[] = amtDataArray[0].split("");

				if (amtDataBitmapArray[0].equals("1")) {
					transaction.setAmount(amtDataArray[1]);
				}

				if (amtDataBitmapArray[1].equals("1")) {
					transaction.setCurrency(amtDataArray[2]);
				}

				if (amtDataBitmapArray[2].equals("1")) {
					transaction.setCountry(amtDataArray[3]);
				}
			}

			if (responseBlockBitMapArray[2].equals("1")) {
				String statusData = pipedResponseArray[feiCounter];
				feiCounter = feiCounter + 1;
				String statusDataArray[] = statusData.split(Pattern.quote("|"));
				String statusDataBitmapArray[] = statusDataArray[0].split("");

				if (statusDataBitmapArray[0].equals("1")) {
					transaction.setStatus_statusOfTxn(statusDataArray[1]);
				}

				if (statusDataBitmapArray[1].equals("1")) {
					transaction.setResponse_Sts_Flag(statusDataArray[2]);
				}

				if (statusDataBitmapArray[2].equals("1")) {
					transaction.setResponse_Err_Code(statusDataArray[3]);
				}

				if (statusDataBitmapArray[3].equals("1")) {
					transaction.setResponse_Err_Msg(statusDataArray[4]);
				}

			}

			if (responseBlockBitMapArray[3].equals("1")) {
				String refundData = pipedResponseArray[feiCounter];
				feiCounter = feiCounter + 1;
				String refundDataArray[] = refundData.split(Pattern.quote("|"));
				String refundDataBitmapArray[] = refundDataArray[0].split("");

				if (refundDataBitmapArray[0].equals("1")) {
					transaction.setStatus_refundReqId(refundDataArray[1]);
				}

				if (refundDataBitmapArray[1].equals("1")) {
					transaction.setStatus_refundAmt(refundDataArray[2]);
				}

				if (refundDataBitmapArray[2].equals("1")) {
					transaction.setStatus_refundAmtAvl(refundDataArray[3]);
				}
			}

		}

		return transaction;
	}

	public TransactionConverter() {
	}

}
