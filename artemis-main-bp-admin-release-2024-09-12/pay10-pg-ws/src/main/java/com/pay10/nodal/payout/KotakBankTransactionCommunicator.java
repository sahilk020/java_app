package com.pay10.nodal.payout;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import javax.xml.soap.SOAPConnection;
import javax.xml.soap.SOAPConnectionFactory;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPMessage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.pay10.cSource.Constants;
import com.pay10.commons.util.FieldType;
import com.pay10.commons.util.Fields;
import com.pay10.commons.util.PropertiesManager;

/**
 * @author Amitosh
 *
 */
@Service("kotakBankTransactionCommunicator")
public class KotakBankTransactionCommunicator {

	private static final Logger logger=LoggerFactory.getLogger(KotakBankTransactionCommunicator.class);
	
	public String getFundTransferResponse(Fields fields, SOAPMessage request) {
		String soapResponse = "";
		SOAPConnection conn = null;
		try {
			String url=PropertiesManager.propertiesMap.get("KotakBankFundTransferURL");
			SOAPConnectionFactory scf = SOAPConnectionFactory.newInstance();
			conn = scf.createConnection();
			SOAPMessage rp = conn.call(request, url);
			soapResponse = prepareSoapString(rp);
			logger.info("Fund Transfer Response URL: "+url+"\nSOAP response for OID : " + fields.get(FieldType.OID.getName()) + " and TXN_ID : "+ fields.get(FieldType.TXN_ID.getName())+"\nResponse : "+soapResponse);
			conn.close();
		} catch (Exception exception) {
			logger.error("Exception caught while getting fund transfer response for OID: " + fields.get(FieldType.OID.getName()) + " and TXN_ID: "+ fields.get(FieldType.TXN_ID.getName())+" Exceptoin : " + exception);
		}
		return soapResponse;
	}

	private String prepareSoapString(SOAPMessage message) {
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

}
