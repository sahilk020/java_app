package com.pay10.icici.netbanking;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import com.pay10.commons.exception.SystemException;
import com.pay10.commons.util.FieldType;
import com.pay10.commons.util.Fields;
import com.pay10.commons.util.PropertiesManager;
import com.pay10.commons.util.TransactionType;
import com.pay10.pg.core.util.AcquirerTxnAmountProvider;
import com.pay10.pg.core.util.IciciUtil;
import com.pay10.pg.core.whitelabel.ReturnUrlCustomizer;

@Service("iciciBankNBTransactionConverter")
public class TransactionConverter {

	@Autowired
	private AcquirerTxnAmountProvider acquirerTxnAmountProvider;

	@Autowired
	private ReturnUrlCustomizer returnUrlCustomizer;
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

		String transactionUrl = PropertiesManager.propertiesMap.get("ICICINBSaleUrl");
		String responseUrl = returnUrlCustomizer.customizeReturnUrl(fields,
				PropertiesManager.propertiesMap.get("ICICINBResponseURL"));
		responseUrl = responseUrl + "?pgrefnum=" + fields.get(FieldType.PG_REF_NUM.getName());
		String amount = acquirerTxnAmountProvider.amountProvider(fields);
		StringBuilder httpRequest = new StringBuilder();
		StringBuilder request = new StringBuilder();

		request.append("PRN");
		request.append("=");
		request.append(fields.get(FieldType.ORDER_ID.getName()));
		request.append("&");
		request.append("ITC");
		request.append("=");
		request.append(fields.get(FieldType.TXN_ID.getName()));
		request.append("&");
		request.append("AMT");
		request.append("=");
		request.append(amount);
		request.append("&");
		request.append("CRN");
		request.append("=");
		request.append("INR");
		request.append("&");
		request.append("RU");
		request.append("=%22");
		request.append(responseUrl);
		request.append("%22&");
		request.append("CG");
		request.append("=");
		request.append("Y");

		String encryptedRequest = null;
		try {
			IciciUtil iciciUtil = new IciciUtil(fields.get(FieldType.TXN_KEY.getName()));
			encryptedRequest = iciciUtil.encrypt(request.toString());
		} catch (Exception exception) {
			logger.error("Error while encrypting ICICI Bank request: " + exception.getMessage());
		}

		// final request
		StringBuilder urlParameter = new StringBuilder();
		urlParameter.append("IWQRYTASKOBJNAME");
		urlParameter.append("=");
		urlParameter.append("bay_mc_login");
		urlParameter.append("&");
		urlParameter.append("BAY_BANKID");
		urlParameter.append("=");
		urlParameter.append("ICI");
		urlParameter.append("&");
		urlParameter.append("MD");
		urlParameter.append("=");
		urlParameter.append("P");
		urlParameter.append("&");
		urlParameter.append("PID");
		urlParameter.append("=");
		urlParameter.append(fields.get(FieldType.MERCHANT_ID.getName()));
		urlParameter.append("&");
		urlParameter.append("SPID");
		urlParameter.append("=");
		urlParameter.append(fields.get(FieldType.ADF1.getName()));
		urlParameter.append("&");
		urlParameter.append("ES");
		urlParameter.append("=");
		urlParameter.append(encryptedRequest);

		String finalRequest = transactionUrl + urlParameter.toString();

		// Net Banking Form Post Request
		httpRequest.append("<HTML>");
		httpRequest.append("<BODY OnLoad=\"OnLoadEvent();\" >");
		httpRequest.append("<form name=\"form1\" action=\"");
		httpRequest.append(finalRequest);
		httpRequest.append("\" method=\"post\">");
		httpRequest.append("</form>");
		httpRequest.append("<script language=\"JavaScript\">");
		httpRequest.append("function OnLoadEvent()");
		httpRequest.append("{document.form1.submit();}");
		httpRequest.append("</script>");
		httpRequest.append("</BODY>");
		httpRequest.append("</HTML>");

		return httpRequest.toString();

	}

	public String refundRequest(Fields fields, Transaction transaction) throws SystemException {
		String amount = acquirerTxnAmountProvider.amountProvider(fields);
		String transactionUrl = PropertiesManager.propertiesMap.get("ICICINBRefundEnqURL");
		StringBuilder request = new StringBuilder();

		request.append("IWQRYTASKOBJNAME");
		request.append("=");
		request.append("bay_mc_login");
		request.append("&");
		request.append("BAY_BANKID");
		request.append("=");
		request.append("ICI");
		request.append("&");
		request.append("MD");
		request.append("=");
		request.append("RVT");
		request.append("&");
		request.append("PID");
		request.append("=");
		request.append(fields.get(FieldType.MERCHANT_ID.getName()));
		request.append("&");
		request.append("SPID");
		request.append("=");
		request.append(fields.get(FieldType.ADF1.getName()));
		request.append("&");
		request.append("PRN");
		request.append("=");
		request.append(fields.get(FieldType.ORDER_ID.getName()));
		request.append("&");
		request.append("ITC");
		request.append("=");
		request.append(fields.get(FieldType.ORIG_TXN_ID.getName()));
		request.append("&");
		request.append("AMT");
		request.append("=");
		request.append(amount);
		request.append("&");
		request.append("CRN");
		request.append("=");
		request.append("INR");
		request.append("&");
		request.append("RID");
		request.append("=");
		request.append(fields.get(FieldType.RRN.getName()));

		return transactionUrl + request.toString();

	}
	

	public String statusEnquiryRequest(Fields fields, Transaction transaction) {

		return null;
	}
	
    public Map<String, String> parseRefundTrackingTxnResponse(String xmlResponse) {
        Map<String, String> trackResponseMap = new HashMap<>();
        try{
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            InputStream inputStream = new ByteArrayInputStream(xmlResponse.getBytes());
            Document document = builder.parse(inputStream);
            document.getDocumentElement().normalize();

            NodeList smsAway = document.getElementsByTagName("SMSAWAY");
            for(int count=0;count<smsAway.getLength();count++){
                Node smsAwayNode = smsAway.item(count);
                if (smsAwayNode.getNodeType() == Node.ELEMENT_NODE) {
                    Element smsElement = (Element) smsAwayNode;
                    if(smsElement.getElementsByTagName("MESSAGE").item(0) != null){
                        trackResponseMap.put("Message",
                                smsElement.getElementsByTagName("MESSAGE").item(0).getTextContent());
                    }

                    if(smsElement.getElementsByTagName("RESPONSE").item(0) != null){
                        trackResponseMap.put("STATUS",
                                smsElement.getElementsByTagName("RESPONSE").item(0).getTextContent());
                    }

                    if(smsElement.getElementsByTagName("Response").item(0) != null){
                        trackResponseMap.put("STATUS",
                                smsElement.getElementsByTagName("Response").item(0).getTextContent());
                    }
                }
            }

            if(document.getElementsByTagName("RECORD") != null) {
                NodeList record = document.getElementsByTagName("RECORD");
                for (int count = 0; count < record.getLength(); count++) {
                    Node recordNode = record.item(count);
                    if (recordNode.getNodeType() == Node.ELEMENT_NODE) {
                        Element recordElement = (Element) recordNode;
                        if (recordElement.getElementsByTagName("RevStatus").item(0) != null) {
                            trackResponseMap.put("RevStatus",
                                    recordElement.getElementsByTagName("RevStatus").item(0).getTextContent());
                        }

                        if (recordElement.getElementsByTagName("RevId").item(0) != null) {
                            trackResponseMap.put("RevId",
                                    recordElement.getElementsByTagName("RevId").item(0).getTextContent());
                        }

                        if (recordElement.getElementsByTagName("BID").item(0) != null) {
                            trackResponseMap.put("BID",
                                    recordElement.getElementsByTagName("BID").item(0).getTextContent());
                        }

                        if (recordElement.getElementsByTagName("RevAmount").item(0) != null) {
                            trackResponseMap.put("RevAmount",
                                    recordElement.getElementsByTagName("RevAmount").item(0).getTextContent());
                        }

                        if (recordElement.getElementsByTagName("RevDate").item(0) != null) {
                            trackResponseMap.put("RevDate",
                                    recordElement.getElementsByTagName("RevDate").item(0).getTextContent());
                        }
                    }
                }
            }

        } catch(Exception exception){
            logger.info("StatusXMLResponseParser Exception: "+exception.getMessage());
        }
        return trackResponseMap;
    }

}
