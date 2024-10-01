package com.pay10.direcpay;

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

import com.pay10.commons.exception.ErrorType;
import com.pay10.commons.exception.SystemException;
import com.pay10.commons.util.FieldType;
import com.pay10.commons.util.Fields;
import com.pay10.commons.util.MopType;
import com.pay10.commons.util.PaymentType;
import com.pay10.commons.util.PropertiesManager;
import com.pay10.commons.util.TransactionType;
import com.pay10.pg.core.util.AcquirerTxnAmountProvider;
import com.pay10.pg.core.util.DirecPayEncDecUtil;

/**
 * @author Shaiwal
 *
 */
@Service("direcpayTransactionConverter")
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

		StringBuilder merchantDataString = new StringBuilder();
		StringBuilder txnString = new StringBuilder();

		String amount = acquirerTxnAmountProvider.amountProvider(fields);
		String outerBEI = "";
		StringBuilder txnDataBEI_FEI = new StringBuilder();
		StringBuilder billingDataBEI_FEI = new StringBuilder();
		StringBuilder shippingDataBEI_FEI = new StringBuilder();
		StringBuilder paymentDataBEI_FEI = new StringBuilder();

		if (StringUtils.isNotBlank(transaction.getMerchantId())) {
			merchantDataString.append(transaction.getMerchantId());
			merchantDataString.append(DOUBLE_PIPE);
		} else {
			throw new SystemException(ErrorType.MERCHANT_ID_NOT_FOUND, "Merchant ID Not Available for transaction");
		}

		if (StringUtils.isNotBlank(transaction.getCollaboratorId())) {
			merchantDataString.append(transaction.getCollaboratorId());
			merchantDataString.append(DOUBLE_PIPE);
		} else {
			throw new SystemException(ErrorType.MERCHANT_ID_NOT_FOUND, "Collaborator ID Not Available for transaction");
		}

		if (PropertiesManager.propertiesMap.get(Constants.DIRECPAY_TXN_DATA_BLOCK).equalsIgnoreCase("1")) {

			outerBEI = outerBEI.concat("1");

			txnDataBEI_FEI.append(PropertiesManager.propertiesMap.get(Constants.DIRECPAY_FEI_TXN_DATA));
			txnDataBEI_FEI.append(PIPE);
			txnDataBEI_FEI.append(transaction.getMerchantOrderNumber());
			txnDataBEI_FEI.append(PIPE);
			txnDataBEI_FEI.append(amount);
			txnDataBEI_FEI.append(PIPE);
			txnDataBEI_FEI.append(transaction.getSuccessURL());
			txnDataBEI_FEI.append(PIPE);
			txnDataBEI_FEI.append(transaction.getFailureURL());
			txnDataBEI_FEI.append(PIPE);
			txnDataBEI_FEI.append(transaction.getTransactionMode());
			txnDataBEI_FEI.append(PIPE);
			txnDataBEI_FEI.append(transaction.getPayMode());
			txnDataBEI_FEI.append(PIPE);
			txnDataBEI_FEI.append(transaction.getTransactionType());
			txnDataBEI_FEI.append(PIPE);
			txnDataBEI_FEI.append(transaction.getCurrency());

		} else {
			outerBEI = outerBEI.concat("0");
		}
		
		// Always add billing and shipping info -- Changes by shaiwal
		if (true) {
			outerBEI = outerBEI.concat("1");

			billingDataBEI_FEI.append(PropertiesManager.propertiesMap.get(Constants.DIRECPAY_FEI_BILLING_DATA));
			billingDataBEI_FEI.append(PIPE);
			billingDataBEI_FEI.append(transaction.getBillToFirstName());
			billingDataBEI_FEI.append(PIPE);
			billingDataBEI_FEI.append(transaction.getBillToLastName());
			billingDataBEI_FEI.append(PIPE);
			billingDataBEI_FEI.append(transaction.getBillToStreet1());
			billingDataBEI_FEI.append(PIPE);
			billingDataBEI_FEI.append(transaction.getBillToStreet2());
			billingDataBEI_FEI.append(PIPE);
			billingDataBEI_FEI.append(transaction.getBillToCity());
			billingDataBEI_FEI.append(PIPE);
			billingDataBEI_FEI.append(transaction.getBillToState());
			billingDataBEI_FEI.append(PIPE);
			billingDataBEI_FEI.append(transaction.getBillToPostalCode());
			billingDataBEI_FEI.append(PIPE);
			billingDataBEI_FEI.append(transaction.getBillToCountry());
			billingDataBEI_FEI.append(PIPE);
			billingDataBEI_FEI.append(transaction.getBillToEmailId());
			billingDataBEI_FEI.append(PIPE);
			billingDataBEI_FEI.append(transaction.getBillToMobileNumber());
			billingDataBEI_FEI.append(PIPE);
			billingDataBEI_FEI.append(transaction.getBillToPhoneNumber1());
			billingDataBEI_FEI.append(PIPE);
			billingDataBEI_FEI.append(transaction.getBillToPhoneNumber2());
			billingDataBEI_FEI.append(PIPE);
			billingDataBEI_FEI.append(transaction.getBillToPhoneNumber3());
		} 

		// Always add billing and shipping info -- Changes by shaiwal
		if (true) {
			outerBEI = outerBEI.concat("1");

			shippingDataBEI_FEI.append(PropertiesManager.propertiesMap.get(Constants.DIRECPAY_FEI_SHIPPING_DATA));
			shippingDataBEI_FEI.append(PIPE);
			shippingDataBEI_FEI.append(transaction.getShipToFirstName());
			shippingDataBEI_FEI.append(PIPE);
			shippingDataBEI_FEI.append(transaction.getShipToLastName());
			shippingDataBEI_FEI.append(PIPE);
			shippingDataBEI_FEI.append(transaction.getShipToStreet1());
			shippingDataBEI_FEI.append(PIPE);
			shippingDataBEI_FEI.append(transaction.getShipToStreet2());
			shippingDataBEI_FEI.append(PIPE);
			shippingDataBEI_FEI.append(transaction.getShipToCity());
			shippingDataBEI_FEI.append(PIPE);
			shippingDataBEI_FEI.append(transaction.getShipToState());
			shippingDataBEI_FEI.append(PIPE);
			shippingDataBEI_FEI.append(transaction.getShipToPostalCode());
			shippingDataBEI_FEI.append(PIPE);
			shippingDataBEI_FEI.append(transaction.getShipToCountry());
			shippingDataBEI_FEI.append(PIPE);
			shippingDataBEI_FEI.append(transaction.getShipToEmailId());
			shippingDataBEI_FEI.append(PIPE);
			shippingDataBEI_FEI.append(transaction.getShipToMobileNumber());
			shippingDataBEI_FEI.append(PIPE);
			shippingDataBEI_FEI.append(transaction.getShipToPhoneNumber1());
			shippingDataBEI_FEI.append(PIPE);
			shippingDataBEI_FEI.append(transaction.getShipToPhoneNumber2());
			shippingDataBEI_FEI.append(PIPE);
			shippingDataBEI_FEI.append(transaction.getShipToPhoneNumber3());

		} 

		if (PropertiesManager.propertiesMap.get(Constants.DIRECPAY_PAYMENT_DATA_BLOCK).equalsIgnoreCase("1")) {
			outerBEI = outerBEI.concat("1");

			if (transaction.getPayMode().equalsIgnoreCase(PaymentType.NET_BANKING.getCode())) {
				paymentDataBEI_FEI.append(PropertiesManager.propertiesMap.get(Constants.DIRECPAY_FEI_PAYMENT_DATA_NB));
				paymentDataBEI_FEI.append(PIPE);
				paymentDataBEI_FEI.append(DirecpayMopType.getBankCode(transaction.getGatewayId()));
			} else if (transaction.getPayMode().equalsIgnoreCase(PaymentType.CREDIT_CARD.getCode()) || transaction.getPayMode().equalsIgnoreCase(PaymentType.DEBIT_CARD.getCode())) {
				
				paymentDataBEI_FEI.append(PropertiesManager.propertiesMap.get(Constants.DIRECPAY_FEI_PAYMENT_DATA_CC_DC));
				paymentDataBEI_FEI.append(PIPE);
				paymentDataBEI_FEI.append(transaction.getCardNumber());
				paymentDataBEI_FEI.append(PIPE);
				paymentDataBEI_FEI.append(transaction.getExpMonth());
				paymentDataBEI_FEI.append(PIPE);
				paymentDataBEI_FEI.append(transaction.getExpYear());
				paymentDataBEI_FEI.append(PIPE);
				paymentDataBEI_FEI.append(transaction.getCvv());
				paymentDataBEI_FEI.append(PIPE);
				paymentDataBEI_FEI.append(transaction.getCardHolderName());
				paymentDataBEI_FEI.append(PIPE);
				paymentDataBEI_FEI.append(MopType.getmopName(transaction.getGatewayId()));
			}

		} else {
			outerBEI = outerBEI.concat("0");
		}

		if (PropertiesManager.propertiesMap.get(Constants.DIRECPAY_MERCHANT_DATA_BLOCK).equalsIgnoreCase("1")) {
			outerBEI = outerBEI.concat("1");
		} else {
			outerBEI = outerBEI.concat("0");
		}

		if (PropertiesManager.propertiesMap.get(Constants.DIRECPAY_OTHER_DETAILS_DATA_BLOCK).equalsIgnoreCase("1")) {
			outerBEI = outerBEI.concat("1");
		} else {
			outerBEI = outerBEI.concat("0");
		}

		if (PropertiesManager.propertiesMap.get(Constants.DIRECPAY_DCC_DATA_BLOCK).equalsIgnoreCase("1")) {
			outerBEI = outerBEI.concat("1");
		} else {
			outerBEI = outerBEI.concat("0");
		}

		txnString.append(outerBEI);

		if (StringUtils.isNotBlank(txnDataBEI_FEI.toString())) {
			txnString.append(DOUBLE_PIPE);
			txnString.append(txnDataBEI_FEI.toString());
		}

		if (StringUtils.isNotBlank(billingDataBEI_FEI.toString())) {
			txnString.append(DOUBLE_PIPE);
			txnString.append(billingDataBEI_FEI.toString());

		}

		if (StringUtils.isNotBlank(shippingDataBEI_FEI.toString())) {
			txnString.append(DOUBLE_PIPE);
			txnString.append(shippingDataBEI_FEI.toString());

		}

		if (StringUtils.isNotBlank(paymentDataBEI_FEI.toString())) {
			txnString.append(DOUBLE_PIPE);
			txnString.append(paymentDataBEI_FEI.toString());

		}

		String encryptionKey = transaction.getEncryptionKey();
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
				logger.error("Exception in encrypting direcpay request ",e);
			}
			encDecMap.put(encryptionKey, secretKey);
		}

		logger.info("Direcpay Sale Request before encryption : " + txnString.toString());
		DirecPayEncDecUtil aesEncrypt = new DirecPayEncDecUtil(encryptionKey, secretKey);
		String encryptedStr = aesEncrypt.encrypt(txnString.toString());

		merchantDataString.append(encryptedStr);

		return merchantDataString.append(encryptedStr).toString();
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

		DirecPayEncDecUtil aesEncrypt = new DirecPayEncDecUtil(encryptionKey, secretKey);
		logger.info("Direcpay Refund Request before encryption : " + refundString.toString());
		String encRequest = aesEncrypt.encrypt(refundString.toString());
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

			DirecPayEncDecUtil aesEncrypt = new DirecPayEncDecUtil(encryptionKey, secretKey);
			logger.info("Direcpay Status Enquiry Request before encryption : " + enquiryString.toString());
			String encRequest = aesEncrypt.encrypt(enquiryString.toString());
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
