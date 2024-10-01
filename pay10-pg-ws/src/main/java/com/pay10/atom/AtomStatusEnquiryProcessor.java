package com.pay10.atom;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.net.URLConnection;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.pay10.commons.dao.FieldsDao;
import com.pay10.commons.exception.ErrorType;
import com.pay10.commons.exception.SystemException;
import com.pay10.commons.util.Amount;
import com.pay10.commons.util.FieldType;
import com.pay10.commons.util.Fields;
import com.pay10.commons.util.PropertiesManager;
import com.pay10.commons.util.StatusType;
import com.pay10.commons.util.TransactionType;
import com.pay10.pg.core.util.AtomEncDecUtil;

@Service
public class AtomStatusEnquiryProcessor {

	@Autowired
	@Qualifier("atomTransactionConverter")
	private TransactionConverter converter;

	@Autowired
	private FieldsDao fieldsDao;

	@Autowired
	private AtomEncDecUtil atomEncDecUtil;

	private static Logger logger = LoggerFactory.getLogger(AtomStatusEnquiryProcessor.class.getName());

	public void enquiryProcessor(Fields fields) {
		String request = statusEnquiryRequest(fields);
		String response = "";
		try {

			if (fields.get(FieldType.TXNTYPE.getName()).equalsIgnoreCase(TransactionType.REFUND.getName())) {
				response = getResponse(request, TransactionType.REFUND.getName());
				String decryptedResponse = atomEncDecUtil.decrypt(response, fields.get(FieldType.ADF4.getName()),
						fields.get(FieldType.ADF3.getName()));
				
				logger.info("Decrypted response for refund status enquiry ATOM >> "+decryptedResponse);
				updateFields(fields, decryptedResponse);
			} else {
				response = getResponse(request, TransactionType.SALE.getName());
				updateFields(fields, response);
			}

		} catch (SystemException exception) {
			logger.error("Exception", exception);
		} catch (Exception e) {
			logger.error("Exception in decrypting status enquiry response for atom ", e);
		}

	}

	public String statusEnquiryRequest(Fields fields) {

		Transaction transaction = new Transaction();
		String stsEnqRequest = null;

		try {

			String login = fields.get(FieldType.MERCHANT_ID.getName());
			String date = null;
			String acqId = null;

			if (fields.get(FieldType.ORIG_TXNTYPE.getName()).equalsIgnoreCase(TransactionType.SALE.getName())) {

				String dataString = fields.get(FieldType.CREATE_DATE.getName());

				if (StringUtils.isBlank(dataString)) {
					return stsEnqRequest;
				}
				String dataStringArray[] = dataString.split(",");

				// IF both ACQ ID and Create Date are present in Mongo DB
				if (dataStringArray.length > 1) {
					date = dataStringArray[0].substring(0, 10);
					transaction.setDate(date);
					acqId = dataStringArray[1];
				}
				// IF only Create Date is present in Mongo DB
				else {
					date = dataStringArray[0].substring(0, 10);
					transaction.setDate(date);
				}

				transaction.setAmt(Amount.toDecimal(fields.get(FieldType.AMOUNT.getName()),
						fields.get(FieldType.CURRENCY_CODE.getName())));
				transaction.setLogin(login);

				stsEnqRequest = converter.statusEnquiryRequest(fields, transaction);
				return stsEnqRequest;
			} else {

				String dataString = fieldsDao.getRefundCaptureDate(fields.get(FieldType.PG_REF_NUM.getName()));
				if (StringUtils.isBlank(dataString)) {
					return stsEnqRequest;
				}
				String dataStringArray[] = dataString.split(",");

				// IF both ACQ ID and Create Date are present in Mongo DB
				if (dataStringArray.length > 1) {
					date = dataStringArray[0].substring(0, 10);
					transaction.setDate(date);
					acqId = dataStringArray[1];
				}
				// IF only Create Date is present in Mongo DB return null as
				// request cannot be prepared without ACQ ID incase of refund
				else {
					return stsEnqRequest;
				}

				StringBuilder requestSB = new StringBuilder();
				requestSB.append(Constants.TXN_ID + "=");
				requestSB.append(acqId);
				requestSB.append("&" + Constants.MERCHANTID + "=");
				requestSB.append(login);
				requestSB.append("&" + Constants.PRODUCT + "=");
				requestSB.append(fields.get(FieldType.ADF2.getName()));

				String encData = atomEncDecUtil.encrypt(requestSB.toString(), fields.get(FieldType.ADF4.getName()),
						fields.get(FieldType.ADF3.getName()));

				StringBuilder finalRequest = new StringBuilder();
				finalRequest.append(Constants.LOGIN + "=");
				finalRequest.append(login);
				finalRequest.append("&" + Constants.ENCDATA + "=");
				finalRequest.append(encData);

				return finalRequest.toString();

			}

		}

		catch (Exception e) {
			logger.error("Exception in preparing Atom Status Enquiry Request", e);
		}

		return null;

	}

	public static String getResponse(String request, String txnType) throws SystemException {

		if (StringUtils.isBlank(request)) {
			logger.info("Request is empty for ATOM status enquiry");
			return null;
		}

		String hostUrl = "";
		Fields fields = new Fields();
		fields.put(FieldType.TXNTYPE.getName(), "STATUS");

		try {

			TransactionType transactionType = TransactionType.getInstance(fields.get(FieldType.TXNTYPE.getName()));
			switch (transactionType) {
			case SALE:
			case AUTHORISE:
				break;
			case ENROLL:
				break;
			case CAPTURE:
				break;
			case REFUND:
				break;
			case STATUS:

				// Different URLs for Sale and Refund Status Enquiry , parameter for same passed
				// in the method

				if (txnType.equalsIgnoreCase(TransactionType.REFUND.getName())) {
					hostUrl = PropertiesManager.propertiesMap.get(Constants.ATOM_REFUND_STATUS_ENQ_URL);
					break;
				} else {
					hostUrl = PropertiesManager.propertiesMap.get(Constants.ATOM_STATUS_ENQ_URL);
					break;
				}

			default:
				break;
			}

			logger.info("Status Enquiry Request to Atom : TxnType = " + fields.get(FieldType.TXNTYPE.getName()) + " "
					+ "Order Id = " + fields.get(FieldType.ORDER_ID.getName()) + " " + request);
			URL url = new URL(hostUrl);
			URLConnection conn = url.openConnection();

			conn.setDoOutput(true);

			OutputStreamWriter writer = new OutputStreamWriter(conn.getOutputStream());

			writer.write(request);
			writer.flush();

			String line;
			StringBuilder response = new StringBuilder();
			BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));

			while ((line = reader.readLine()) != null) {
				response.append(line);

			}
			logger.info("Response received for Atom Statuts Enquiry Response >>> " + response.toString());
			writer.close();
			reader.close();
			return response.toString();

		} catch (Exception e) {
			logger.error("Exception in Atom Status Enquiry ", e);
		}
		return null;
	}

	public void updateFields(Fields fields, String xmlResponse) {

		
		// Update for Refund Status Enquiry Response
		if (xmlResponse.contains("REFUNDSTATUS")) {

			String innerResponse = "";
			
			String ERRORCODE = null;
			String MESSAGE = null;
			String TXNID = null;
			String PRODUCT = null;
			String REFUNDAMOUNT = null;
			String REMARKS = null;
			String MEREFUNDREF = null;
			
			
			if (StringUtils.isNotBlank(xmlResponse)) {
				innerResponse = getTextBetweenTags(xmlResponse, "<REFUNDSTATUS>", "</REFUNDSTATUS>");
				
				if (innerResponse.contains("ERRORCODE")) {
					ERRORCODE = getTextBetweenTags(xmlResponse, "<ERRORCODE>", "</ERRORCODE>");
				}
				
				if (innerResponse.contains("MESSAGE")) {
					MESSAGE = getTextBetweenTags(xmlResponse, "<MESSAGE>", "</MESSAGE>");
				}
				
				if (innerResponse.contains("TXNID")) {
					TXNID = getTextBetweenTags(xmlResponse, "<TXNID>", "</TXNID>");
				}
				
				if (innerResponse.contains("REFUNDAMOUNT")) {
					REFUNDAMOUNT = getTextBetweenTags(xmlResponse, "<REFUNDAMOUNT>", "</REFUNDAMOUNT>");
				}
				
				if (innerResponse.contains("REMARKS")) {
					REMARKS = getTextBetweenTags(xmlResponse, "<REMARKS>", "</REMARKS>");
				}
				
				if (innerResponse.contains("MEREFUNDREF")) {
					MEREFUNDREF = getTextBetweenTags(xmlResponse, "<MEREFUNDREF>", "</MEREFUNDREF>");
				}
				
			}
			
			String status = null;
			ErrorType errorType = null;
			String pgTxnMsg = null;

			if ((StringUtils.isNotBlank(ERRORCODE)) && (ERRORCODE).equalsIgnoreCase("00")) {

				status = StatusType.CAPTURED.getName();
				errorType = ErrorType.SUCCESS;
				pgTxnMsg = ErrorType.SUCCESS.getResponseMessage();

			}

			else {
				if ((StringUtils.isNotBlank(ERRORCODE))) {

					AtomResultType resultInstance = AtomResultType.getInstanceFromName(ERRORCODE);

					if (resultInstance != null) {
						status = resultInstance.getStatusCode();
						errorType = ErrorType.getInstanceFromCode(resultInstance.getiPayCode());
						pgTxnMsg = resultInstance.getMessage();
					} else {
						status = StatusType.FAILED_AT_ACQUIRER.getName();
						errorType = ErrorType.getInstanceFromCode("022");
						pgTxnMsg = "Transaction failed at acquirer";
					}

				} else {
					status = StatusType.REJECTED.getName();
					errorType = ErrorType.REJECTED;
					pgTxnMsg = ErrorType.REJECTED.getResponseMessage();

				}
			}

			fields.put(FieldType.STATUS.getName(), status);
			fields.put(FieldType.RESPONSE_MESSAGE.getName(), errorType.getResponseMessage());
			fields.put(FieldType.RESPONSE_CODE.getName(), errorType.getResponseCode());

			if (StringUtils.isNotBlank(TXNID)) {
				fields.put(FieldType.ACQ_ID.getName(), TXNID);
			}

			fields.put(FieldType.PG_RESP_CODE.getName(), errorType.getResponseCode());
			fields.put(FieldType.PG_TXN_STATUS.getName(), status);
			fields.put(FieldType.PG_TXN_MESSAGE.getName(), MESSAGE);

		} 
		
		// Update For Sale Status Enquiry Response
		else {
			String innerResponse = "";

			String MerchantID = null;
			String MerchantTxnID = null;
			String VERIFIED = null;
			String BID = null;
			String atomtxnId = null;

			if (StringUtils.isNotBlank(xmlResponse)) {
				innerResponse = getTextBetweenTags(xmlResponse, "<VerifyOutput ", "/>");

				if (innerResponse.contains("MerchantID")) {
					MerchantID = getTextForTag(innerResponse, "MerchantID");
				}

				if (innerResponse.contains("MerchantTxnID")) {
					MerchantTxnID = getTextForTag(innerResponse, "MerchantTxnID");
				}

				if (innerResponse.contains("VERIFIED")) {
					VERIFIED = getTextForTag(innerResponse, "VERIFIED");
				}

				if (innerResponse.contains("BID")) {
					BID = getTextForTag(innerResponse, "BID");
				}

				if (innerResponse.contains("atomtxnId")) {
					atomtxnId = getTextForTag(innerResponse, "atomtxnId");
				}

			}

			String status = null;
			ErrorType errorType = null;
			String pgTxnMsg = null;

			if ((StringUtils.isNotBlank(VERIFIED)) && (VERIFIED).equalsIgnoreCase("SUCCESS")) {

				status = StatusType.CAPTURED.getName();
				errorType = ErrorType.SUCCESS;
				pgTxnMsg = ErrorType.SUCCESS.getResponseMessage();

			}

			else {
				if ((StringUtils.isNotBlank(VERIFIED))) {

					AtomResultType resultInstance = AtomResultType.getInstanceFromName(VERIFIED);

					if (resultInstance != null) {
						status = resultInstance.getStatusCode();
						errorType = ErrorType.getInstanceFromCode(resultInstance.getiPayCode());
						pgTxnMsg = resultInstance.getMessage();
					} else {
						status = StatusType.FAILED_AT_ACQUIRER.getName();
						errorType = ErrorType.getInstanceFromCode("022");
						pgTxnMsg = "Transaction failed at acquirer";
					}

				} else {
					status = StatusType.REJECTED.getName();
					errorType = ErrorType.REJECTED;
					pgTxnMsg = ErrorType.REJECTED.getResponseMessage();

				}
			}

			fields.put(FieldType.STATUS.getName(), status);
			fields.put(FieldType.RESPONSE_MESSAGE.getName(), errorType.getResponseMessage());
			fields.put(FieldType.RESPONSE_CODE.getName(), errorType.getResponseCode());

			if (StringUtils.isNotBlank(BID)) {
				fields.put(FieldType.RRN.getName(), BID);
			}

			if (StringUtils.isNotBlank(atomtxnId)) {
				fields.put(FieldType.ACQ_ID.getName(), atomtxnId);
			}

			fields.put(FieldType.PG_RESP_CODE.getName(), errorType.getResponseCode());
			fields.put(FieldType.PG_TXN_STATUS.getName(), status);
			fields.put(FieldType.PG_TXN_MESSAGE.getName(), VERIFIED);
		}

	}

	public static String getTextForTag(String text, String tag1) {

		StringBuilder sb = new StringBuilder();
		int leftIndex = text.indexOf(tag1);
		if (leftIndex == -1) {
			return null;
		}

		int startIndex = tag1.length() + leftIndex + 2;

		for (int i = 0; i < text.length() - startIndex; i++) {

			char txt = text.charAt(startIndex + i);
			String txtStr = String.valueOf(txt);
			if (txtStr.equalsIgnoreCase("\"")) {
				break;
			}
			sb.append(txtStr);

		}
		return sb.toString();
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
}
