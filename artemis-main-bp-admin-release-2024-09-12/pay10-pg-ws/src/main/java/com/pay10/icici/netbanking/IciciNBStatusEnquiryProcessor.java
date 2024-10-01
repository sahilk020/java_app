package com.pay10.icici.netbanking;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.w3c.dom.Document;

import com.pay10.commons.dao.FieldsDao;
import com.pay10.commons.exception.ErrorType;
import com.pay10.commons.exception.SystemException;
import com.pay10.commons.util.FieldType;
import com.pay10.commons.util.Fields;
import com.pay10.commons.util.PropertiesManager;
import com.pay10.commons.util.StatusType;
import com.pay10.pg.core.util.AxisBankNBEncDecService;

@Service
public class IciciNBStatusEnquiryProcessor {

	@Autowired
	@Qualifier("iciciBankNBTransactionConverter")
	private TransactionConverter converter;

	@Autowired
	private FieldsDao fieldsDao;

	@Autowired
	private AxisBankNBEncDecService axisBankNBEncDecService;

	private static Logger logger = LoggerFactory.getLogger(IciciNBStatusEnquiryProcessor.class.getName());

	public void enquiryProcessor(Fields fields) {

		String response = "";
		try {
			// String saleDate =
			// fieldsDao.getCaptureDate(fields.get(FieldType.PG_REF_NUM.getName()));
			// fields.put(FieldType.CREATE_DATE.getName(), saleDate);
			String request = statusEnquiryRequest(fields);
			response = transactStatus(request);
		} catch (SystemException exception) {
			logger.error("Exception", exception);
		}

		updateFields(fields, response);

	}

	public String statusEnquiryRequest(Fields fields) {
		String transactionUrl = PropertiesManager.propertiesMap.get("ICICINBStatusEnqURL");
		StringBuilder request = new StringBuilder();
		String formattedDate = null;
		if (!StringUtils.isEmpty(fields.get(FieldType.CREATE_DATE.getName()))) {
			formattedDate = fields.get(FieldType.CREATE_DATE.getName()).substring(0, 10);
		}

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
		request.append("V");
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
		request.append(fields.get(FieldType.TXN_ID.getName()));
		request.append("&");
		request.append("AMT");
		request.append("=");
		request.append(fields.get(FieldType.AMOUNT.getName()));
		request.append("&");
		request.append("CRN");
		request.append("=");
		request.append("INR");
		request.append("&");
		request.append("Pmt_Date");
		request.append("=");
		request.append(formattedDate);

		return transactionUrl + request.toString();

	}

	public String transactStatus(String hostUrl) throws SystemException {
		PostMethod postMethod = new PostMethod(hostUrl);
		return transact(postMethod, hostUrl);

	}

	public String transact(HttpMethod httpMethod, String hostUrl) throws SystemException {
		String response = "";

		try {
			HttpClient httpClient = new HttpClient();
			httpClient.executeMethod(httpMethod);

			if (httpMethod.getStatusCode() == HttpStatus.SC_OK) {
				response = httpMethod.getResponseBodyAsString();
				logger.info("Verification Response from ICICI Bank: " + response);
			} else {
				throw new SystemException(ErrorType.INTERNAL_SYSTEM_ERROR, "Network Exception with ICICI Bank "
						+ hostUrl + "Verification response code received" + httpMethod.getStatusCode());
			}
		} catch (IOException ioException) {
			throw new SystemException(ErrorType.INTERNAL_SYSTEM_ERROR, ioException,
					"Network Exception with ICICI Bank " + hostUrl);
		}
		return response;

	}

	Map<String, String> parseStatusTxnResponse(String xmlResponse) {
		Map<String, String> responseMap = new HashMap<>();
		try {
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			DocumentBuilder builder = factory.newDocumentBuilder();
			InputStream inputStream = new ByteArrayInputStream(xmlResponse.getBytes());
			Document document = builder.parse(inputStream);
			document.getDocumentElement().normalize();

			if (document.getElementsByTagName("VerifyOutput") != null
					&& document.getElementsByTagName("VerifyOutput").item(0) != null
					&& document.getElementsByTagName("VerifyOutput").item(0).getAttributes() != null) {

				responseMap.put("STATUS", document.getElementsByTagName("VerifyOutput").item(0).getAttributes()
						.getNamedItem("STATUS").getNodeValue());

				if (document.getElementsByTagName("VerifyOutput").item(0).getAttributes().getNamedItem("BID") != null) {
					responseMap.put("BID", document.getElementsByTagName("VerifyOutput").item(0).getAttributes()
							.getNamedItem("BID").getNodeValue());
				}
			}
		} catch (Exception exception) {
			logger.error("StatusXMLResponseParser Exception: " + exception.getMessage());
		}
		return responseMap;
	}

	public void updateFields(Fields fields, String xmlResponse) {
		Map<String, String> statusResponseMap = parseStatusTxnResponse(xmlResponse);
		updateICICIStatusTxnResponse(fields, statusResponseMap);

	}
	
    void updateICICIStatusTxnResponse(Fields fields, Map<String, String> responseMap){

       
        StatusType status = getNetBankingStatusType(responseMap.get("STATUS"));
        ErrorType errorType = getNetBankingErrorType(responseMap.get("STATUS"));

        fields.put(FieldType.STATUS.getName(), status.getName());
        fields.put(FieldType.PG_REF_NUM.getName(), responseMap.get("BID"));
        fields.put(FieldType.PG_RESP_CODE.getName(), responseMap.get("STATUS"));
        fields.put(FieldType.RESPONSE_MESSAGE.getName(), errorType.getResponseMessage());
        fields.put(FieldType.RESPONSE_CODE.getName(), errorType.getResponseCode());

    }
    
    private StatusType getNetBankingStatusType(String respCode) {
        StatusType status;

        if (null == respCode) {
            status = StatusType.FAILED;
        } else if (respCode.equals("Y") || respCode.equalsIgnoreCase("Success")) {
            status = StatusType.CAPTURED;
        } else if (respCode.equals("N")) {
            status = StatusType.CANCELLED;
        } else if (respCode.equals("FRAUD")) {
            status = StatusType.DENIED_BY_FRAUD;
        } else {
            status = StatusType.FAILED;
        }

        return status;
    }
    
    private ErrorType getNetBankingErrorType(String respCode) {
        ErrorType errorType;

        if (null == respCode) {
            errorType = ErrorType.FAILED;
        } else if (respCode.equals("Y") || respCode.equalsIgnoreCase("Success")) {
            errorType = ErrorType.SUCCESS;
        } else if (respCode.equals("N")) {
            errorType = ErrorType.CANCELLED;
        } else if (respCode.equals("FRAUD")) {
            errorType = ErrorType.DENIED_BY_FRAUD;
        } else {
            errorType = ErrorType.FAILED;
        }

        return errorType;
    }


}
