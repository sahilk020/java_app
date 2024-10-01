package com.pay10.pg.core.ingenico.util;

import java.io.ByteArrayInputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLStreamHandler;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.soap.MessageFactory;
import javax.xml.soap.MimeHeaders;
import javax.xml.soap.SOAPBody;
import javax.xml.soap.SOAPConnection;
import javax.xml.soap.SOAPConnectionFactory;
import javax.xml.soap.SOAPElement;
import javax.xml.soap.SOAPEnvelope;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPMessage;
import javax.xml.soap.SOAPPart;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;

public final class SoapWSCall
{
    final String strEx = "EXCEPTION";
    final String separator = "|";
    private static Logger logger = LoggerFactory.getLogger(SoapWSCall.class.getName());
    
    public Map getTransactionToken(final String strFinalPayload, final String webServiceLocator, final int intTimeOut, final String strRequestType) {
        final StringBuffer sbf = new StringBuffer();
        sbf.append(Math.random());
        sbf.append("|");
        sbf.append("STARTED COMMUNIATING WITH PG ");
        sbf.append("|");
        sbf.append(new Date());
        sbf.append("|");
        Map map = new HashMap();
        map.put("getTransactionTokenReturn", null);
        try {
            map = this.getToken(strFinalPayload, webServiceLocator, map, intTimeOut);
        }
        catch (Exception e) {
            if (strRequestType != null && !"TCS".equalsIgnoreCase(strRequestType)) {
                sbf.append("|");
                sbf.append("TRY 1 FAILED :" + e.getMessage());
                sbf.append("|");
                sbf.append(":TRYING WITH 2nd Interation");
                sbf.append("|");
                sbf.append(new Date());
                sbf.append("|");
                try {
                    map = this.getToken(strFinalPayload, webServiceLocator, map, intTimeOut);
                }
                catch (Exception e2) {
                    sbf.append("TRY 2 FAILED :" + e.getMessage());
                    sbf.append("|");
                    sbf.append(":TRYING WITH 3nd Interation");
                    sbf.append("|");
                    sbf.append(new Date());
                    sbf.append("|");
                    try {
                        map = this.getToken(strFinalPayload, webServiceLocator, map, intTimeOut);
                    }
                    catch (Exception e3) {
                        sbf.append("TRY 3 FAILED :" + e.getMessage());
                        sbf.append("|");
                        map.put("ERROR", e3.getMessage());
                    }
                }
            }
            return map;
        }
        finally {
            sbf.append(map);
            logger.info(new StringBuilder().append((Object)sbf).toString());
        }
        sbf.append(map);
        System.out.println(new StringBuilder().append((Object)sbf).toString());
        return map;
    }
    
    private Map getToken(final String msg, final String webServiceLocator, Map map, final int intTimeOut) throws SOAPException, MalformedURLException, Exception {
        SOAPConnectionFactory soapConnectionFactory = null;
        SOAPConnection soapConnection = null;
        String strRes = "";
        URL endpoint = null;
        soapConnectionFactory = SOAPConnectionFactory.newInstance();
        soapConnection = soapConnectionFactory.createConnection();
        try {
            endpoint = new URL(null, String.valueOf(webServiceLocator) + "?wsdl", (URLStreamHandler)new SoapWSCall1());
        }
        catch (Exception e) {
        	logger.error("EXCEP444",e);
            throw new Exception("ERROR093");
        }
        try {
            final long milliStart = System.currentTimeMillis();
            final SOAPMessage soapResponse = soapConnection.call(this.soapMessage(msg), (Object)endpoint);
            final long milliEnd = System.currentTimeMillis();
            final long milliTime = milliEnd - milliStart;
            strRes = this.printSOAPResponse(soapResponse);
            map = this.readXMLTwo8(strRes, map);
        }
        catch (Exception e) {
        	logger.error("ERROR092",e);
            throw new Exception("ERROR092");
        }
        return map;
    }
    
    private SOAPMessage soapMessage(final String msg) {
        SOAPMessage soapMessage = null;
        try {
            final MessageFactory messageFactory = MessageFactory.newInstance();
            soapMessage = messageFactory.createMessage();
            final SOAPPart soapPart = soapMessage.getSOAPPart();
            final SOAPEnvelope envelope = soapPart.getEnvelope();
            final SOAPBody soapBody = envelope.getBody();
            final SOAPElement soapBodyElem = soapBody.addChildElement("getTransactionToken", "", "http://shopping.util.pg.tp.com");
            final SOAPElement soapBodyElem2 = soapBodyElem.addChildElement("Parameters");
            soapBodyElem2.addTextNode(msg);
            final MimeHeaders headers = soapMessage.getMimeHeaders();
            headers.addHeader("SOAPAction", "http://shopping.util.pg.tp.com/getTransactionToken");
            soapMessage.saveChanges();
        }
        catch (Exception e) {
        	logger.error("inside SOAPMessage Exception is",e);
        }
        return soapMessage;
    }
    
    private SOAPMessage soapMessage(final String msg, final Map map) {
        SOAPMessage soapMessage = null;
        try {
            final MessageFactory messageFactory = MessageFactory.newInstance();
            soapMessage = messageFactory.createMessage();
            final SOAPPart soapPart = soapMessage.getSOAPPart();
            final SOAPEnvelope envelope = soapPart.getEnvelope();
            final SOAPBody soapBody = envelope.getBody();
            final SOAPElement soapBodyElem = soapBody.addChildElement("getTransactionToken", "", "http://shopping.util.pg.tp.com");
            final SOAPElement soapBodyElem2 = soapBodyElem.addChildElement("Parameters");
            soapBodyElem2.addTextNode(msg);
            final MimeHeaders headers = soapMessage.getMimeHeaders();
            headers.addHeader("SOAPAction", "http://shopping.util.pg.tp.com/getTransactionToken");
            soapMessage.saveChanges();
        }
        catch (Exception e) {
        	logger.error("inside SOAPMessage Exception is",e);
        }
        return soapMessage;
    }
    
    private String printSOAPResponse(final SOAPMessage soapResponse) {
        String strRetRes = "";
        try {
            final TransformerFactory transformerFactory = TransformerFactory.newInstance();
            final Transformer transformer = transformerFactory.newTransformer();
            final Source sourceContent = soapResponse.getSOAPPart().getContent();
            final StreamResult result = new StreamResult(new StringWriter());
            transformer.transform(sourceContent, result);
            strRetRes = result.getWriter().toString();
        }
        catch (Exception e) {
        	logger.error("inside SOAPMessage Exception is",e);
        }
        return strRetRes;
    }
    
    private Map readXMLTwo8(final String strXML, final Map<String,String> mapNode) {
        try {
            final DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            final DocumentBuilder db = dbf.newDocumentBuilder();
            final Document doc = db.parse(new InputSource(new ByteArrayInputStream(strXML.getBytes("utf-8"))));
            doc.getDocumentElement().normalize();
            for (final String value :  mapNode.keySet()) {
                mapNode.put(value, doc.getElementsByTagName(value).item(0).getTextContent());
            }
        }
        catch (Exception e) {
        	logger.error("inside SOAPMessage Exception is",e);
        }
        return mapNode;
    }
    
    public Map getExceptionMessageChain(Throwable throwable) {
        final Map map = new HashMap();
        StringWriter sw = null;
        PrintWriter pw = null;
        try {
            while (throwable != null) {
                map.put("EXCEPTION", throwable.getMessage());
                throwable = throwable.getCause();
                sw = new StringWriter();
                pw = new PrintWriter(sw);
                
                map.put("errorcause", sw.toString());
            }
        }
        finally {
            sw = null;
            pw = null;
        }
        sw = null;
        pw = null;
        return map;
    }
}
