package com.pay10.nodal.payout;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.xml.soap.MessageFactory;
import javax.xml.soap.MimeHeaders;
import javax.xml.soap.SOAPBody;
import javax.xml.soap.SOAPConnection;
import javax.xml.soap.SOAPConnectionFactory;
import javax.xml.soap.SOAPConstants;
import javax.xml.soap.SOAPElement;
import javax.xml.soap.SOAPEnvelope;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPHeader;
import javax.xml.soap.SOAPMessage;
import javax.xml.soap.SOAPPart;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.pay10.cSource.Constants;
import com.pay10.commons.util.Amount;
import com.pay10.commons.util.FieldType;
import com.pay10.commons.util.Fields;
import com.pay10.commons.util.PropertiesManager;

/**
 * @author Amitosh
 *
 */
@Service("kotakBankTransactionConverter")
public class KotakBankTransactionConverter {

	private static final Logger logger = LoggerFactory.getLogger(KotakBankTransactionConverter.class);

	public static final String MESSAGEID_OPEN_TAG = "<ns0:MessageId>";
	public static final String MESSAGEID_CLOSE_TAG = "</ns0:MessageId>";
	public static final String STATUS_CODE_OPEN_TAG = "<ns0:StatusCd>";
	public static final String STATUS_CODE_CLOSE_TAG = "</ns0:StatusCd>";
	public static final String STATUS_REMARK_OPEN_TAG = "<ns0:StatusRem>";
	public static final String STATUS_REMARK_CLOSE_TAG = "</ns0:StatusRem>";

	public static final String REQUEST_ID_OPEN_TAG = "<ns0:Req_Id>";
	public static final String REQUEST_ID_CLOSE_TAG = "</ns0:Req_Id>";
	public static final String UTR_OPEN_TAG = "<ns0:UTR>";
	public static final String UTR_CLOSE_TAG = "</ns0:UTR>";
	public static final String CLIENT_CODE_OPEN_TAG = "<ns0:Client_Code>";
	public static final String CLIENT_CODE_CLOSE_TAG = "</ns0:Client_Code>";
	public static final String MESSAGE_POST_DATE_OPEN_TAG = "<ns0:Date_Post>";
	public static final String MESSAGE_POST_DATE_CLOSE_TAG = "</ns0:Date_Post>";
	public static final String MESSAGE_ID_OPEN_TAG = "<ns0:Msg_Id>";
	public static final String MESSAGE_ID_CLOSE_TAG = "</ns0:Msg_Id>";
	public static final String STATUS_CODE_OPEN_TAG1 = "<ns0:Status_Code>";
	public static final String STATUS_CODE_CLOSE_TAG1 = "</ns0:Status_Code>";
	public static final String STATUS_DESCRIPTION_OPEN_TAG = "<ns0:Status_Desc>";
	public static final String STATUS_DESCRIPTION_CLOSE_TAG = "</ns0:Status_Desc>";

	public SOAPMessage fundTransferRequest(Fields fields) {

		DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
		Date date = new Date();

		MessageFactory messageFactory;
		SOAPMessage soapMessage = null;
		SOAPPart soapPart;
		SOAPEnvelope soapEnvelope = null;
		try {
			messageFactory = MessageFactory.newInstance();
			soapMessage =MessageFactory.newInstance(SOAPConstants.SOAP_1_2_PROTOCOL).createMessage();
			soapPart = soapMessage.getSOAPPart();
			soapEnvelope = soapPart.getEnvelope();
		} catch (SOAPException e) {
			logger.error("Exception caught for OID: " + fields.get(FieldType.OID.getName()) + " and TXN_ID: "+ fields.get(FieldType.TXN_ID.getName())+" Exceptoin : " + e);
		}
		try {
			soapEnvelope.removeNamespaceDeclaration(soapEnvelope.getPrefix());

			soapEnvelope.addNamespaceDeclaration("ns3",
					"http://www.kotak.com/schemas/CMS_Generic/Reversal_Request.xsd");

			SOAPBody soapBody = soapEnvelope.getBody();
			SOAPElement ns3Payment = soapBody.addChildElement("Payment", "ns3");
			ns3Payment.addNamespaceDeclaration("ns1", "http://www.kotak.com/schemas/CMS_Generic/Reversal_Request.xsd");
			ns3Payment.addNamespaceDeclaration("ns2", "http://www.kotak.com/schemas/CMS_Generic/Reversal_Response.xsd");
			ns3Payment.addNamespaceDeclaration("ns3", "http://www.kotak.com/schemas/CMS_Generic/Payment_Request.xsd");
			ns3Payment.addNamespaceDeclaration("ns4", "http://www.kotak.com/schemas/CMS_Generic/Payment_Response.xsd");

			MimeHeaders headers = soapMessage.getMimeHeaders();

			headers.addHeader("SOAPAction",
					"/BusinessServices/StarterProcesses/CMS_Generic_Service.serviceagent/Payment");
			headers.addHeader("Content-Type", "application/soap+xml;charset=UTF-8");

			SOAPHeader header = soapMessage.getSOAPHeader();
			header.detachNode();

			SOAPElement ns3RequestHeader = ns3Payment.addChildElement("RequestHeader", "ns3");

			SOAPElement ns3MessageId = ns3RequestHeader.addChildElement("MessageId", "ns3");
			ns3MessageId.addTextNode(fields.get(FieldType.TXN_ID.getName()));

			SOAPElement ns3MsgSource = ns3RequestHeader.addChildElement("MsgSource", "ns3");
			ns3MsgSource.addTextNode(PropertiesManager.propertiesMap.get("KotakMsgSource"));

			SOAPElement ns3ClientCode = ns3RequestHeader.addChildElement("ClientCode", "ns3");
			ns3ClientCode.addTextNode(PropertiesManager.propertiesMap.get("KotakClientCode"));

			SOAPElement ns3BatchRefNmbr = ns3RequestHeader.addChildElement("BatchRefNmbr", "ns3");
			ns3BatchRefNmbr.addTextNode(fields.get(FieldType.TXN_ID.getName()));

			SOAPElement ns3InstrumentList = ns3Payment.addChildElement("InstrumentList", "ns3");

			SOAPElement ns3instrument = ns3InstrumentList.addChildElement("instrument", "ns3");

			SOAPElement ns3InstRefNo = ns3instrument.addChildElement("InstRefNo", "ns3");
			ns3InstRefNo.addTextNode(fields.get(FieldType.TXN_ID.getName()));

			SOAPElement payMyProdCode = ns3instrument.addChildElement("MyProdCode", "ns3");
			payMyProdCode.addTextNode(PropertiesManager.propertiesMap.get("KotakMyProdCode"));

			SOAPElement ns3PayMode = ns3instrument.addChildElement("PayMode", "ns3");
			ns3PayMode.addTextNode(fields.get(FieldType.PAYMENT_TYPE.getName()));

			SOAPElement ns3TxnAmnt = ns3instrument.addChildElement("TxnAmnt", "ns3");
			ns3TxnAmnt.addTextNode(Amount.toDecimal(fields.get(FieldType.AMOUNT.getName()),
					fields.get(FieldType.CURRENCY_CODE.getName())));

			SOAPElement ns3AccountNo = ns3instrument.addChildElement("AccountNo", "ns3");
			ns3AccountNo.addTextNode(fields.get(FieldType.SRC_ACCOUNT_NO.getName()));

			SOAPElement ns3DrDesc = ns3instrument.addChildElement("DrDesc", "ns3");
			ns3DrDesc.addTextNode(fields.get(FieldType.PRODUCT_DESC.getName()));

			SOAPElement ns3PaymentDt = ns3instrument.addChildElement("PaymentDt", "ns3");
			ns3PaymentDt.addTextNode(dateFormat.format(date).toString());

			SOAPElement ns3RecBrCd = ns3instrument.addChildElement("RecBrCd", "ns3");
			ns3RecBrCd.addTextNode(fields.get(FieldType.IFSC_CODE.getName()));

			SOAPElement ns3BeneAcctNo = ns3instrument.addChildElement("BeneAcctNo", "ns3");
			ns3BeneAcctNo.addTextNode(fields.get(FieldType.BENE_ACCOUNT_NO.getName()));

			SOAPElement ns3BeneName = ns3instrument.addChildElement("BeneName", "ns3");
			ns3BeneName.addTextNode(fields.get(FieldType.BENE_NAME.getName()));

			SOAPElement ns3BeneCode = ns3instrument.addChildElement("BeneCode", "ns3");
			ns3BeneCode.addTextNode(fields.get(FieldType.BENEFICIARY_CD.getName()));

			SOAPElement ns3InstDt = ns3instrument.addChildElement("InstDt", "ns3");
			ns3InstDt.addTextNode(dateFormat.format(date).toString());

			SOAPElement ns3PaymentDtl1 = ns3instrument.addChildElement("PaymentDtl1", "ns3");
			ns3PaymentDtl1.addTextNode(fields.get(FieldType.PRODUCT_DESC.getName()));

			SOAPElement ns3EnrichmentSet = ns3instrument.addChildElement("EnrichmentSet", "ns3");

			SOAPElement ns3Enrichment = ns3EnrichmentSet.addChildElement("Enrichment", "ns3");
			ns3Enrichment.addTextNode(fields.get(FieldType.TXN_ID.getName()) + "~"
					+ fields.get(FieldType.PAYMENT_TYPE.getName()) + "~" + fields.get(FieldType.AMOUNT.getName()) + "~"
					+ fields.get(FieldType.SRC_ACCOUNT_NO.getName()) + "~" + fields.get(FieldType.IFSC_CODE.getName())
					+ "~" + fields.get(FieldType.BENE_ACCOUNT_NO.getName()));

			ByteArrayOutputStream out = new ByteArrayOutputStream();
			soapMessage.writeTo(out);

			String strMsg = new String(out.toByteArray());

			strMsg = strMsg.replaceAll("SOAP-ENV", "soap");
			strMsg = strMsg.replaceAll("http://schemas.xmlsoap.org/soap/envelope/",
					"http://www.w3.org/2003/05/soap-envelope");
			strMsg = strMsg.replaceAll(":ns1", "");

			InputStream is = new ByteArrayInputStream(strMsg.getBytes());
			soapMessage =MessageFactory.newInstance(SOAPConstants.SOAP_1_2_PROTOCOL).createMessage(null, is);

			soapMessage.saveChanges();
			MimeHeaders mimeHeaders = soapMessage.getMimeHeaders();

			mimeHeaders.addHeader("SOAPAction",
					"/BusinessServices/StarterProcesses/CMS_Generic_Service.serviceagent/Payment");
			mimeHeaders.addHeader("Content-Type", "application/soap+xml;charset=UTF-8");
			logger.info("SOAP fund transfer request for OID : " + fields.get(FieldType.OID.getName()) + " and TXN_ID : "+ fields.get(FieldType.TXN_ID.getName())+"\nRequest : "+strMsg);
			soapMessage.writeTo(System.out);
		} catch (Exception e) {
			logger.error("Exception caught for OID: " + fields.get(FieldType.OID.getName()) + " and TXN_ID: "+ fields.get(FieldType.TXN_ID.getName())+" Exceptoin : " + e);
		}
		return soapMessage;
	}

	public static SOAPMessage getStatusRequest(Fields fields) {
		logger.info("Kotak fund Transfer status request initiated for OID: " + fields.get(FieldType.OID.getName()) + " and TXN_ID: "+ fields.get(FieldType.TXN_ID.getName()));

		DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
		Date date = new Date();

		MessageFactory messageFactory;
		SOAPMessage soapMessage = null;
		SOAPPart soapPart;
		SOAPEnvelope soapEnvelope = null;
		try {
			messageFactory = MessageFactory.newInstance();
			soapMessage =MessageFactory.newInstance(SOAPConstants.SOAP_1_2_PROTOCOL).createMessage();
			soapPart = soapMessage.getSOAPPart();
			soapEnvelope = soapPart.getEnvelope();
		} catch (SOAPException e) {
			logger.error("Exception caught while getting status for OID: " + fields.get(FieldType.OID.getName()) + " and TXN_ID: "+ fields.get(FieldType.TXN_ID.getName())+" Exceptoin : " + e);
		}
		try {
			soapMessage.setProperty(SOAPMessage.CHARACTER_SET_ENCODING, "UTF-8");
			soapMessage.setProperty(SOAPMessage.WRITE_XML_DECLARATION, "true");

			soapEnvelope.removeNamespaceDeclaration(soapEnvelope.getPrefix());
			soapEnvelope.addNamespaceDeclaration("rev",
					"http://www.kotak.com/schemas/CMS_Generic/Reversal_Request.xsd");
			soapEnvelope.addNamespaceDeclaration("soap", "http://www.w3.org/2003/05/soap-envelope");

			SOAPBody soapBody = soapEnvelope.getBody();

			SOAPElement revReversal = soapBody.addChildElement("Reversal", "rev");
			SOAPElement revHeader = revReversal.addChildElement("Header", "rev");

			SOAPElement revReq_Id = revHeader.addChildElement("Req_Id", "rev");
			revReq_Id.addTextNode(fields.get(FieldType.OID.getName()));

			SOAPElement revMsg_Src = revHeader.addChildElement("Msg_Src", "rev");
			revMsg_Src.addTextNode(PropertiesManager.propertiesMap.get("KotakMsgSource"));

			SOAPElement revClient_Code = revHeader.addChildElement("Client_Code", "rev");
			revClient_Code.addTextNode(PropertiesManager.propertiesMap.get("KotakClientCode"));

			SOAPElement revDate_Post = revHeader.addChildElement("Date_Post", "rev");
			revDate_Post.addTextNode(dateFormat.format(date).toString());

			SOAPElement revDetails = revReversal.addChildElement("Details", "rev");

			SOAPElement revMsg_Id = revDetails.addChildElement("Msg_Id", "rev");
			revMsg_Id.addTextNode(fields.get(FieldType.OID.getName()));

			ByteArrayOutputStream out = new ByteArrayOutputStream();
			soapMessage.writeTo(out);

			String strMsg = new String(out.toByteArray());

			strMsg = strMsg.replaceAll("SOAP-ENV", "soap");
			strMsg = strMsg.replaceAll("xmlns:soap=\"http://schemas.xmlsoap.org/soap/envelope/\"", "");

			InputStream is = new ByteArrayInputStream(strMsg.getBytes());
			soapMessage =MessageFactory.newInstance(SOAPConstants.SOAP_1_2_PROTOCOL).createMessage(null, is);

			soapMessage.saveChanges();

			MimeHeaders mimeHeaders = soapMessage.getMimeHeaders();

			mimeHeaders.addHeader("SOAPAction",
					"/BusinessServices/StarterProcesses/CMS_Generic_Service.serviceagent/Reversal");
			mimeHeaders.addHeader("Content-Type", "application/soap+xml;charset=UTF-8");

			soapMessage.writeTo(System.out);
			logger.info("Status enquiry SOAP request for OID : " + fields.get(FieldType.OID.getName()) + " and TXN_ID : "+ fields.get(FieldType.TXN_ID.getName())+"\nRequest : "+strMsg);
		} catch (Exception e) {
			logger.error("Exception caught for OID: " + fields.get(FieldType.OID.getName()) + " and TXN_ID: "+ fields.get(FieldType.TXN_ID.getName())+" Exceptoin : " + e);
		}
		return soapMessage;
	}

	public static String getFundTransferResponse(Fields fields, SOAPMessage request) {
		String soapResponse = "";
		SOAPConnection conn = null;
		try {
			String url = PropertiesManager.propertiesMap.get("KotakBankFundTransferURL");
			logger.info("URL: " + url);
			SOAPConnectionFactory scf = SOAPConnectionFactory.newInstance();
			conn = scf.createConnection();
			SOAPMessage rp = conn.call(request, url);
			soapResponse = prepareSoapString(rp);
			conn.close();
			logger.info("SOAP fund transfer response for OID : " + fields.get(FieldType.OID.getName()) + " and TXN_ID : "+ fields.get(FieldType.TXN_ID.getName())+"\nResponse : "+soapResponse);
		} catch (Exception exception) {
			logger.error("Exception caught while getting fund transfer response for OID: " + fields.get(FieldType.OID.getName()) + " and TXN_ID: "+ fields.get(FieldType.TXN_ID.getName())+" Exceptoin : " + exception);
		}
		return soapResponse;
	}

	private static String prepareSoapString(SOAPMessage message) {
		ByteArrayOutputStream req = new ByteArrayOutputStream();
		try {
			message.writeTo(req);

			String reqMsg = new String(req.toByteArray());
			reqMsg = reqMsg.replaceAll(Constants.AMP, Constants.SEPARATOR);
			return reqMsg;
		} catch (SOAPException e) {
			logger.error("Exception in prepareSoapString , exception = " + e);
		} catch (IOException e) {
			logger.error("Exception in prepareSoapString , exception = " + e);
		}
		return "";
	}

	public Transaction toTransaction(String xml) {
		Transaction transaction = new Transaction();
		transaction.setKotakMessageId(getTextBetweenTags(xml, MESSAGEID_OPEN_TAG, MESSAGEID_CLOSE_TAG));
		transaction.setStatusCode(getTextBetweenTags(xml, STATUS_CODE_OPEN_TAG, STATUS_CODE_CLOSE_TAG));
		transaction.setResponeMessage(getTextBetweenTags(xml, STATUS_REMARK_OPEN_TAG, STATUS_REMARK_CLOSE_TAG));

		return transaction;
	}

	public static Transaction statusToTransaction(String xml) {
		Transaction transaction = new Transaction();

		transaction.setUniqueResponseNo(getTextBetweenTags(xml, UTR_OPEN_TAG, UTR_CLOSE_TAG));
		transaction.setStatusCode(getTextBetweenTags(xml, STATUS_CODE_OPEN_TAG1, STATUS_CODE_CLOSE_TAG1));
		transaction
				.setResponeMessage(getTextBetweenTags(xml, STATUS_DESCRIPTION_OPEN_TAG, STATUS_DESCRIPTION_CLOSE_TAG));
		return transaction;
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
