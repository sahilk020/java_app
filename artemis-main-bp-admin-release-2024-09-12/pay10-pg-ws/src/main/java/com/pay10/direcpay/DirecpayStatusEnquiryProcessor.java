package com.pay10.direcpay;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.security.spec.KeySpec;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import javax.xml.soap.MessageFactory;
import javax.xml.soap.SOAPBody;
import javax.xml.soap.SOAPConnection;
import javax.xml.soap.SOAPConnectionFactory;
import javax.xml.soap.SOAPElement;
import javax.xml.soap.SOAPEnvelope;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPHeader;
import javax.xml.soap.SOAPMessage;
import javax.xml.soap.SOAPPart;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.pay10.cSource.Constants;
import com.pay10.commons.exception.ErrorType;
import com.pay10.commons.exception.SystemException;
import com.pay10.commons.util.FieldType;
import com.pay10.commons.util.Fields;
import com.pay10.commons.util.PropertiesManager;
import com.pay10.commons.util.StatusType;
import com.pay10.commons.util.TransactionType;
import com.pay10.pg.core.util.DirecPayEncDecUtil;

@Service
public class DirecpayStatusEnquiryProcessor {

	@Autowired
	@Qualifier("direcpayTransactionConverter")
	private TransactionConverter converter;

	private static byte[] SALT = new byte[] { -57, -75, -103, -12, 75, 124, -127, 119 };
	private static Map<String, SecretKey> encDecMap = new HashMap<String, SecretKey>();

	private static Logger logger = LoggerFactory.getLogger(DirecpayStatusEnquiryProcessor.class.getName());

	public static final String BEI_SEPARATOR = "||";
	public static final String FEI_SEPARATOR = "|";

	public void enquiryProcessor(Fields fields) {
		String request = statusEnquiryRequest(fields);
		String response = "";
		try {
			response = getResponse(request);
		} catch (SystemException exception) {
			logger.error("Exception", exception);
		}

		updateFields(fields, response);

	}

	public String statusEnquiryRequest(Fields fields) {

		String statusEnqString = fields.get(FieldType.MERCHANT_ID.getName()) + "||"
				+ fields.get(FieldType.ADF1.getName()) + "||";

		String paymentString = "";
		if (fields.get(FieldType.ORIG_TXNTYPE.getName()).equalsIgnoreCase(TransactionType.SALE.getName())) {
			paymentString = "1||010|" + fields.get(FieldType.PG_REF_NUM.getName());
		} else {
			paymentString = "1||011|" + fields.get(FieldType.PG_REF_NUM.getName()) + "|01";
		}

		fields.put(FieldType.TXNTYPE.getName(), TransactionType.STATUS.getName());

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

		logger.info("Direcpay Status Enquiry Request before encryption : " + paymentString);
		DirecPayEncDecUtil aesEncrypt = new DirecPayEncDecUtil(encryptionKey, secretKey);
		String encryptedStr = aesEncrypt.encrypt(paymentString);
		logger.info("Direcpay Status Enquiry Final Request : " + statusEnqString + encryptedStr);
		return statusEnqString + encryptedStr;

	}

	public static String getResponse(String request) throws SystemException {

		String hostUrl = "";
		String serverURI = "";
		String queryApi = "";
		String pipedResponse = null;

		try {

			hostUrl = PropertiesManager.propertiesMap.get(com.pay10.direcpay.Constants.DIRECPAY_STATUS_ENQ_URL);
			serverURI = PropertiesManager.propertiesMap.get(com.pay10.direcpay.Constants.DIRECPAY_SERVER_URI);
			queryApi = "invokeQueryAPI";

			logger.info("Status Enquiry Request message to direcpay >> " + request);

			MessageFactory messageFactory = MessageFactory.newInstance();
			SOAPMessage soapMessage = messageFactory.createMessage();
			SOAPPart soapPart = soapMessage.getSOAPPart();

			SOAPEnvelope envelope = soapPart.getEnvelope();
			envelope.removeNamespaceDeclaration(envelope.getPrefix());
			envelope.addNamespaceDeclaration("soapenv", "http://schemas.xmlsoap.org/soap/envelope/");
			envelope.addNamespaceDeclaration("uil", serverURI);
			envelope.setPrefix("soapenv");

			SOAPBody soapBody = envelope.getBody();
			SOAPElement soapBodyElem = soapBody.addChildElement(queryApi, "uil");
			SOAPElement soapBodyElem1 = soapBodyElem.addChildElement("requestparameters");
			soapBodyElem1.addTextNode(request);

			SOAPHeader soapheader = soapMessage.getSOAPHeader();
			soapheader.setPrefix("soapenv");
			soapBody.setPrefix("soapenv");
			soapMessage.saveChanges();

			String soapResponse = "";
			SOAPConnection conn = null;

			SOAPConnectionFactory scf = SOAPConnectionFactory.newInstance();
			conn = scf.createConnection();
			SOAPMessage soapResponseMessage = conn.call(soapMessage, hostUrl);
			soapResponse = prepareSoapString(soapResponseMessage);

			pipedResponse = getTextBetweenTags(soapResponse, "<return>", "</return>");
			logger.info("Status Enquiry mesage from direcpay >> " + pipedResponse);

			return pipedResponse;
		} catch (Exception e) {
			logger.error("Exception in Direcpay Status Enquiry ", e);
		}
		return pipedResponse;
	}

	private static String prepareSoapString(SOAPMessage message) {
		ByteArrayOutputStream req = new ByteArrayOutputStream();
		try {
			message.writeTo(req);

			String reqMsg = new String(req.toByteArray());
			reqMsg = reqMsg.replaceAll(Constants.AMP, Constants.SEPARATOR);
			return reqMsg;
		} catch (SOAPException e) {
		} catch (IOException e) {
		}
		return "";
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

	public void updateFields(Fields fields, String pipedResponse) {

		Transaction transaction = new Transaction();
		String pipedResponseArray[] = pipedResponse.split(Pattern.quote("||"));

		// Sale Status Enquiry Response
		if (pipedResponseArray[0].equalsIgnoreCase("1110")) {
			String responseBlockBitMapArray[] = pipedResponseArray[0].split("");

			if (responseBlockBitMapArray[0].equals("1")) {
				String txnData = pipedResponseArray[1];
				String txnDataArray[] = txnData.split(Pattern.quote("|"));

				if (txnDataArray[0].equalsIgnoreCase("1")) {
					transaction.setResponse_Ref_Num(txnDataArray[1]);
				}

				else if (txnDataArray[0].equalsIgnoreCase("11")) {

					transaction.setResponse_GtwTraceNum(txnDataArray[1]);
					transaction.setResponse_Ref_Num(txnDataArray[2]);
				}

			}

			if (responseBlockBitMapArray[2].equals("1")) {
				String statusData = pipedResponseArray[3];
				String statusDataArray[] = statusData.split(Pattern.quote("|"));
				String statusDataBitmapArray[] = statusDataArray[0].split("");

				if (statusDataBitmapArray[0] != null && statusDataBitmapArray[0].equals("1")) {
					transaction.setResponse_Sts_Comment(statusDataArray[1]);
				}

				if (statusDataBitmapArray[1] != null && statusDataBitmapArray[1].equals("1")) {
					transaction.setResponse_Sts_Flag(statusDataArray[2]);
				}

				if (statusDataBitmapArray[2] != null && statusDataBitmapArray[2].equals("1")) {
					transaction.setResponse_Err_Code(statusDataArray[3]);
				}

				if (statusDataBitmapArray[3] != null && statusDataBitmapArray[3].equals("1")) {
					transaction.setResponse_Err_Msg(statusDataArray[4]);
				}

			}
		}

		// Refund Status Enquiry Response
		if (pipedResponseArray[0].equalsIgnoreCase("1111")) {

			String responseBlockBitMapArray[] = pipedResponseArray[0].split("");

			if (responseBlockBitMapArray[3].equals("1")) {
				String refundData = pipedResponseArray[4];

				String refundDataArray[] = refundData.split(Pattern.quote("|"));
				String refundDataBitmapArray[] = refundDataArray[0].split("");

				if (refundDataBitmapArray[0] != null & refundDataBitmapArray[0].equals("1")) {
					transaction.setResponse_Sts_Flag(refundDataArray[1]);
				}

				if (refundDataBitmapArray[1] != null & refundDataBitmapArray[1].equals("1")) {
					transaction.setResponse_Err_Code(refundDataArray[2]);
				}

				if (refundDataBitmapArray[2] != null & refundDataBitmapArray[2].equals("1")) {
					transaction.setResponse_Err_Msg(refundDataArray[3]);
				}
			}

		}

		if (pipedResponseArray[0].equalsIgnoreCase("1010")) {

			String responseBlockBitMapArray[] = pipedResponseArray[0].split("");

			if (responseBlockBitMapArray[2].equals("1"))

			{
				String statusData = pipedResponseArray[2];

				String statusDataArray[] = statusData.split(Pattern.quote("|"));
				String statusDataBitmapArray[] = statusDataArray[0].split("");

				if (statusDataBitmapArray[0] != null & statusDataBitmapArray[0].equals("1")) {
					transaction.setResponse_Sts_Flag(statusDataArray[1]);
				}

				if (statusDataBitmapArray[1] != null & statusDataBitmapArray[1].equals("1")) {
					transaction.setResponse_Err_Code(statusDataArray[2]);
				}

				if (statusDataBitmapArray[2] != null & statusDataBitmapArray[2].equals("1")) {
					transaction.setResponse_Err_Msg(statusDataArray[3]);
				}
			}

		}

		String status = null;
		ErrorType errorType = null;
		String pgTxnMsg = null;

		if ((StringUtils.isNotBlank(transaction.getResponse_Err_Code()))
				&& (StringUtils.isNotBlank(transaction.getResponse_Err_Msg()))
				&& (StringUtils.isNotBlank(transaction.getResponse_Sts_Comment()))
				&& ((transaction.getResponse_Err_Code()).equalsIgnoreCase("00000"))
				&& ((transaction.getResponse_Sts_Flag()).equalsIgnoreCase("SUCCESS"))
				&& ((transaction.getResponse_Sts_Comment()).equalsIgnoreCase("Funds In Clearing")))

		{
			status = StatusType.CAPTURED.getName();
			errorType = ErrorType.SUCCESS;
			pgTxnMsg = ErrorType.SUCCESS.getResponseMessage();

		}

		else {

			// All Rejected except the ones captured
			status = StatusType.REJECTED.getName();
			errorType = ErrorType.REJECTED;
			if (StringUtils.isNotBlank(transaction.getResponse_Sts_Comment())) {
				pgTxnMsg = transaction.getResponse_Sts_Comment();
			} else {
				pgTxnMsg = ErrorType.REJECTED.getResponseMessage();
			}
		}

		fields.put(FieldType.STATUS.getName(), status);
		fields.put(FieldType.RESPONSE_MESSAGE.getName(), errorType.getResponseMessage());
		fields.put(FieldType.RESPONSE_CODE.getName(), errorType.getResponseCode());

		if (StringUtils.isNotBlank(transaction.getResponse_GtwTraceNum())) {
			fields.put(FieldType.ACQ_ID.getName(), transaction.getResponse_GtwTraceNum());
			fields.put(FieldType.RRN.getName(), transaction.getResponse_GtwTraceNum());
		}

		fields.put(FieldType.PG_RESP_CODE.getName(), transaction.getResponse_Err_Code());
		fields.put(FieldType.PG_TXN_STATUS.getName(), transaction.getResponse_Sts_Flag());
		fields.put(FieldType.PG_TXN_MESSAGE.getName(), pgTxnMsg);

	}

}
