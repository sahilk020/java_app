package com.pay10.axisbank.card;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.pay10.cSource.Constants;
import com.pay10.commons.exception.ErrorType;
import com.pay10.commons.exception.SystemException;
import com.pay10.commons.util.FieldType;
import com.pay10.commons.util.Fields;
import com.pay10.commons.util.PropertiesManager;
import com.pay10.commons.util.StatusType;
import com.pay10.commons.util.TransactionType;

/**
 * @author Shaiwal
 *
 */
@Service("axisBankCardransactionCommunicator")
public class TransactionCommunicator {

	private static Logger logger = LoggerFactory.getLogger(TransactionCommunicator.class.getName());

	public void updateSaleResponse(Fields fields, String request) {
		
		fields.put(FieldType.DIRECPAY_FINAL_REQUEST.getName(), request.toString());
		fields.put(FieldType.STATUS.getName(), StatusType.SENT_TO_BANK.getName());
		fields.put(FieldType.RESPONSE_CODE.getName(), ErrorType.SUCCESS.getResponseCode());
		fields.put(FieldType.RESPONSE_MESSAGE.getName(), ErrorType.SUCCESS.getResponseMessage());

	}

	@SuppressWarnings("incomplete-switch")
	public String getResponse(String request, Fields fields) throws SystemException {

		String hostUrl = "";
		String serverURI= "";
		String queryApi= "";
		
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
				hostUrl = PropertiesManager.propertiesMap.get(com.pay10.direcpay.Constants.DIRECPAY_REFUND_URL);
				serverURI = PropertiesManager.propertiesMap.get(com.pay10.direcpay.Constants.DIRECPAY_SERVER_URI);
				queryApi = "invokeRefundAPI";
				break;
			case STATUS:
				hostUrl = PropertiesManager.propertiesMap.get(com.pay10.direcpay.Constants.DIRECPAY_STATUS_ENQ_URL);
				serverURI = PropertiesManager.propertiesMap.get(com.pay10.direcpay.Constants.DIRECPAY_SERVER_URI);
				queryApi = "invokeQueryAPI";
				break;
			}

			logger.info("Request message to direcpay: TxnType = " + fields.get(FieldType.TXNTYPE.getName()) + " " + "Order Id = " + fields.get(FieldType.ORDER_ID.getName()) + " " + request);
			
			
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
		
			String pipedResponse = getTextBetweenTags(soapResponse,"<return>","</return>");
			logger.info("Response mesage from direcpay: TxnType = " + fields.get(FieldType.TXNTYPE.getName()) + " " + "Order id = " + fields.get(FieldType.ORDER_ID.getName()) + " " + pipedResponse);
			return pipedResponse;
		
	
		} catch (Exception e) {
			logger.error("Exception in direcpay txn Communicator" ,e);
		}
		return null;		
			
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

}
