package com.pay10.pg.core.util;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UncheckedIOException;
import java.util.Base64;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.imageio.ImageIO;

import org.apache.struts2.ServletActionContext;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.util.HtmlUtils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.pay10.commons.exception.SystemException;
import com.pay10.commons.user.UserDao;
import com.pay10.commons.util.ConfigurationConstants;
import com.pay10.commons.util.FieldType;
import com.pay10.commons.util.Fields;
import com.pay10.commons.util.MopType;
import com.pay10.commons.util.PaymentType;
import com.pay10.commons.util.PropertiesManager;
import com.pay10.pg.core.tokenization.TokenManager;
import com.pay10.pg.core.whitelabel.ReturnUrlCustomizer;

@Service
public class RequestFieldsCreator {
	


	private static Logger logger = LoggerFactory.getLogger(RequestCreator.class.getName());

	@Autowired
	private TransactionResponser transactionResponser;

	@Autowired
	private ReturnUrlCustomizer returnUrlCustomizer;

	@Autowired
	private UserDao userDao;

	@Autowired
	private TokenManager tokenManager;
	
	@Autowired
	private ObjectMapper mapper;

	public Map<String,String> EnrollRequest(Fields responseMap) {
		Map<String,String> requestMap = new HashMap<>();
		try {
			/************* Enrolled card condition starts here ************/
			String acsurl = responseMap.get(FieldType.ACS_URL.getName());
			String PAReq = responseMap.get(FieldType.PAREQ.getName());
			String paymentid = responseMap.get(FieldType.PAYMENT_ID.getName());
			String termURL = returnUrlCustomizer.customizeReturnUrl(responseMap,
					PropertiesManager.propertiesMap.get("Request3DSURL"));

			requestMap.put("URL",acsurl);

			if (responseMap.get(FieldType.MOP_TYPE.getName()).equals(MopType.RUPAY.getCode())) {
				requestMap.put("PaymentID",paymentid);
			} else {

				requestMap.put("PaReq",PAReq);
				requestMap.put("MD",paymentid);
				requestMap.put("TermUrl",termURL);
			}

			/************* Enrolled card condition Ends here ************/
		} catch (Exception exception) {
			logger.error("Exception", exception);
		}
		
		return requestMap;
	}

	public Map<String,String> FirstDataEnrollRequest(Fields responseMap) {
		Map<String,String> requestMap = new HashMap<>();
		try {
			/************* Enrolled card condition starts here ************/
			String acsurl = responseMap.get(FieldType.ACS_URL.getName());
			String PAReq = responseMap.get(FieldType.PAREQ.getName());
			// String termURL =
			// propertiesManager.getSystemProperty("Request3DSURL");
			String termURL = returnUrlCustomizer.customizeReturnUrl(responseMap,
					PropertiesManager.propertiesMap.get("FirstData3DSUrl"));
			String md = responseMap.get(FieldType.MD.getName());
			requestMap.put("URL",acsurl);
			requestMap.put("PaReq",PAReq);
			requestMap.put("MD",md);
			requestMap.put("TermUrl",termURL);
			/************* Enrolled card condition Ends here ************/
		} catch (Exception exception) {
			logger.error("Exception", exception);
		}
		
		return requestMap;
	}

	public Map<String,String> cyberSourceEnrollRequest(Fields responseMap) {
		Map<String,String> requestMap = new HashMap<>();
		try {
			/************* Enrolled card condition starts here ************/
			String acsurl = responseMap.get(FieldType.ACS_URL.getName());
			String PAReq = responseMap.get(FieldType.PAREQ.getName());
			// String termURL =
			// propertiesManager.getSystemProperty("Request3DSURL");
			String termURL = returnUrlCustomizer.customizeReturnUrl(responseMap,
					PropertiesManager.propertiesMap.get("CyberSource3DSUrl"));
			String md = responseMap.get(FieldType.MD.getName());
			requestMap.put("URL",acsurl);
			requestMap.put("PaReq",PAReq);
			requestMap.put("MD",md);
			requestMap.put("TermUrl",termURL);
			/************* Enrolled card condition Ends here ************/
		} catch (Exception exception) {
			logger.error("Exception", exception);
		}
		
		return requestMap;
	}

	public Map<String,String> generateFreeChargeRequest(Fields fields) {
		Map<String,String> requestMap = new HashMap<>();
		try {
			String requestURL = PropertiesManager.propertiesMap.get("FREECHARGESaleUrl");
			requestMap.put("URL",requestURL);
			String finalRequest = fields.get(FieldType.FREECHARGE_FINAL_REQUEST.getName());
			String finalRequestSplit[] = finalRequest.split("~");


			for (String entry : finalRequestSplit) {

				String entrySplit[] = entry.split("=");
				requestMap.put(entrySplit[0],entrySplit[1]);
			}

		} catch (Exception exception) {
			logger.error("Exception", exception);
		}
		return requestMap;
	}

	public Map<String,String> iciciMpgsEnrollRequest(Fields responseMap) {
		Map<String,String> requestMap = new HashMap<>();
		try {
			/************* Enrolled card condition starts here ************/
			String acsurl = responseMap.get(FieldType.ACS_URL.getName());
			String PAReq = responseMap.get(FieldType.PAREQ.getName());
			// String termURL =
			// propertiesManager.getSystemProperty("Request3DSURL");
			String termURL = PropertiesManager.propertiesMap.get("IciciMpgsReturnUrl");
			String md = responseMap.get(FieldType.MD.getName());
			requestMap.put("URL",acsurl);
			requestMap.put("PaReq",PAReq);
			requestMap.put("MD",md);
			requestMap.put("TermUrl",termURL);
			/************* Enrolled card condition Ends here ************/
		} catch (Exception exception) {
			logger.error("Exception", exception);
		}
		
		return requestMap;
	}


	// public void WebsitePackageRequest(String PAY_ID,String ORDER_ID,String
	// AMOUNT,String TXNTYPE,String CUST_NAME,String CUST_EMAIL,String
	// PRODUCT_DESC,String CURRENCY_CODE,String RETURN_URL,String HASH) {
	public Map<String,String> WebsitePackageRequest(Fields fields) {
		Map<String,String> requestMap = new HashMap<>();
		try {
			String requestURL = PropertiesManager.propertiesMap.get("RequestURL");
			requestMap.put("URL",requestURL);
			for (String key : fields.keySet()) {
				requestMap.put(key,fields.get(key));
			}

		} catch (Exception exception) {
			logger.error("Exception", exception);
		}
		
		return requestMap;
	}

	public Map<String,String> generateIPayRequest(Fields fields) {
		Map<String,String> requestMap = new HashMap<>();
		String finalRequest = fields.get(FieldType.IPAY_FINAL_REQUEST.getName());
		String finalRequestSplit[] = finalRequest.split("~");


		for (String entry : finalRequestSplit) {

			String entrySplit[] = entry.split("=");
			requestMap.put(entrySplit[0],entrySplit[1]);
		}

		return requestMap;
	}

	public Map<String,String> generateFederalRequest(Fields fields) throws SystemException {
		Map<String,String> requestMap = new HashMap<>();
		String finalRequest = fields.get(FieldType.FEDERAL_ENROLL_FINAL_REQUEST.getName());
		String finalRequestSplit[] = finalRequest.split("~");


		for (String entry : finalRequestSplit) {

			String entrySplit[] = entry.split("=");
			requestMap.put(entrySplit[0],entrySplit[1]);
		}

		return requestMap;
	}

	public Map<String,String> generateIciciNBRequest(Fields fields) throws SystemException {
		Map<String,String> requestMap = new HashMap<>();
		String request = fields.get(FieldType.ICICI_NB_FINAL_REQUEST.getName());
		String finalRequestSplit[] = request.split("~");


		for (String entry : finalRequestSplit) {

			String entrySplit[] = entry.split("=");
			requestMap.put(entrySplit[0],entrySplit[1]);
		}

		return requestMap;
	}



	public Map<String,String> generateSbiRequest(Fields fields, String cardSaveFlag, String expiryMonth, String expiryYear) {
		Map<String,String> requestMap = new HashMap<>();
		try {

			// logger.info("fields :: " + fields.getFieldsAsString());
			fields.logAllFieldsUsingMasking("Request Fields Foe SBI Request :");

			if (fields.get(FieldType.PAYMENT_TYPE.getName()).equalsIgnoreCase("NB")) {

				try {
					String requestURL = PropertiesManager.propertiesMap.get("SbiSaleUrl");
					requestMap.put("url", requestURL);
					requestMap.put("encdata", fields.get(FieldType.SBI_FINAL_REQUEST.getName()));
					requestMap.put("merchant_code", fields.get(FieldType.MERCHANT_ID.getName()));
					
				} catch (Exception exception) {
					logger.error("Exception", exception);
				}

			}

		} catch (Exception e) {
			logger.error("Exception", e);
		}
		
		return requestMap;

	}

	public Map<String,String> generateBobRequest(Fields fields) {
		Map<String,String> requestMap = new HashMap<>();
		try {
			PrintWriter out = ServletActionContext.getResponse().getWriter();
			String finalRequest = fields.get(FieldType.BOB_FINAL_REQUEST.getName());
			String finalRequestSplit[] = finalRequest.split("~");


			for (String entry : finalRequestSplit) {

				String entrySplit[] = entry.split("=");
				requestMap.put(entrySplit[0],entrySplit[1]);
			}
		} catch (Exception exception) {
			logger.error("Exception", exception);
		}
		return requestMap;
	}

	public Map<String,String> generateFssRequest(Fields fields) {
		Map<String,String> requestMap = new HashMap<>();
		try {
			String finalRequest = fields.get(FieldType.FSS_FINAL_REQUEST.getName());

			String finalRequestSplit[] = finalRequest.split("~");


			for (String entry : finalRequestSplit) {

				String entrySplit[] = entry.split("=");
				requestMap.put(entrySplit[0],entrySplit[1]);
			}
		} catch (Exception exception) {
			logger.error("Exception", exception);
		}
		return requestMap;
	}

	public Map<String,String> generateAtlRequest(Fields fields) {
		Map<String,String> requestMap = new HashMap<>();
		try {
			String requestURL = PropertiesManager.propertiesMap.get("ATLSaleUrl");
			requestMap.put("url",requestURL);
			requestMap.put("encdata",fields.get(FieldType.ATL_FINAL_REQUEST.getName()));
		} catch (Exception exception) {
			logger.error("Exception", exception);
		}
		
		return requestMap;
	}

	public Map<String,String> generateKotakRequest(Fields fields) {
		Map<String,String> requestMap = new HashMap<>();
		try {

			PrintWriter out = ServletActionContext.getResponse().getWriter();
			StringBuilder httpRequest = new StringBuilder();

			logger.info("Request Received For generateKotakRequest " + fields.get(FieldType.PAYMENT_TYPE.getName()));
			if (fields.get(FieldType.PAYMENT_TYPE.getName()).equalsIgnoreCase("NB")) {
				String requestURL = PropertiesManager.propertiesMap.get("KotakNBSaleUrl");
				requestMap.put("url", requestURL);
				requestMap.put("msg", fields.get(FieldType.KOTAK_FINAL_REQUEST.getName()));
				requestMap.put("merchantId", fields.get(FieldType.MERCHANT_ID.getName()));
			} 

		} catch (Exception exception) {
			logger.error("Exception", exception);
		}
		
		return requestMap;
	}

	public Map<String,String> generateIdbiRequest(Fields fields) {
		Map<String,String> requestMap = new HashMap<>();
		try {
			String requestURL = PropertiesManager.propertiesMap.get("IdbiSaleUrl");
			requestMap.put("url",requestURL);
			requestMap.put("merchantRequest",fields.get(FieldType.IDBI_FINAL_REQUEST.getName()));
			requestMap.put("MID",fields.get(FieldType.MERCHANT_ID.getName()));
		} catch (Exception exception) {
			logger.error("Exception", exception);
		}
		return requestMap;
	}

	public Map<String,String> generateDirecpayRequest(Fields fields) {
		Map<String,String> requestMap = new HashMap<>();
		try {
			String requestURL = PropertiesManager.propertiesMap.get("DirecpaySaleUrl");
			requestMap.put("url",requestURL);

			requestMap.put("requestParameter",fields.get(FieldType.DIRECPAY_FINAL_REQUEST.getName()));
		} catch (Exception exception) {
			logger.error("Exception", exception);
		}
		
		return requestMap;
	}

	public Map<String,String> generateAtomRequest(Fields fields) {
		
		Map<String,String> requestMap = new HashMap<>();
		try {
			String loginString = "login=" + fields.get(FieldType.MERCHANT_ID.getName());
			String encDataString = "&encdata=" + fields.get(FieldType.ATOM_FINAL_REQUEST.getName());

			String requestURL = PropertiesManager.propertiesMap.get("ATOMSaleUrl");
			requestURL = requestURL + loginString + encDataString;
			requestMap.put("url",requestURL);
			
		} catch (Exception exception) {
			logger.error("Exception", exception);
		}
		return requestMap;
	}

	public Map<String,String> generateIngenicoRequest(Fields fields) {
		Map<String,String> requestMap = new HashMap<>();
		try {
			String requestURL = fields.get(FieldType.INGENICO_FINAL_REQUEST.getName());
			requestMap.put("url",requestURL);
		} catch (Exception exception) {
			logger.error("Exception", exception);
		}
		
		return requestMap;
	}

	public Map<String,String> generateApblRequest(Fields fields) {
		Map<String,String> requestMap = new HashMap<>();
		try {
			String paymentString = fields.get(FieldType.APBL_FINAL_REQUEST.getName());
			requestMap.put("url",paymentString);
			
		} catch (Exception exception) {
			logger.error("Exception while posting APBL payment request ", exception);
		}
		return requestMap;
	}

	

	public Map<String,String> generatePayuRequest(Fields fields) {
		Map<String,String> requestMap = new HashMap<>();
		try {

			String paymentString = fields.get(FieldType.PAYU_FINAL_REQUEST.getName());
			String finalRequestSplit[] = paymentString.split("~");
			requestMap.put("url",paymentString);

			for (String entry : finalRequestSplit) {

				String entrySplit[] = entry.split("=");
				requestMap.put(entrySplit[0],entrySplit[1]);
			}
			

		} catch (Exception exception) {
			logger.error("Exception while posting PAYU payment request ", exception);
		}
		
		return requestMap;
	}

	public Map<String,String> generatePhonePeRequest(Fields fields) {
		Map<String,String> requestMap = new HashMap<>();
		try {
			String paymentString = fields.get(FieldType.PHONEPE_FINAL_REQUEST.getName());

			String finalRequestSplit[] = paymentString.split("~");
			requestMap.put("url",paymentString);

			for (String entry : finalRequestSplit) {

				String entrySplit[] = entry.split("=");
				requestMap.put(entrySplit[0],entrySplit[1]);
			}
			

		} catch (Exception exception) {
			logger.error("Exception while posting PhonePe payment request ", exception);
		}
		
		return requestMap;
	}
	
	
	public Map<String,String> generatePaytmRequest(Fields fields) {
		Map<String,String> requestMap = new HashMap<>();
		try {
			String paymentString = fields.get(FieldType.PAYTM_FINAL_REQUEST.getName());
			requestMap.put("url",paymentString);
			String finalRequestSplit[] = paymentString.split("~");
			for (String entry : finalRequestSplit) {

				String entrySplit[] = entry.split("=");
				requestMap.put(entrySplit[0],entrySplit[1]);
			}

		} catch (Exception exception) {
			logger.error("Exception while posting PAYTM payment request ", exception);
		}
		return requestMap;
	}

	public Map<String,String> generateMobikwikRequest(Fields fields) {
		Map<String,String> requestMap = new HashMap<>();
		try {
			String paymentString = fields.get(FieldType.MOBIKWIK_FINAL_REQUEST.getName());

			String requestURL = PropertiesManager.propertiesMap.get("MobikwikSaleUrl");
			requestMap.put("url",requestURL);
			requestURL = requestURL.concat(paymentString);
			
			String finalRequestSplit[] = paymentString.split("~");


			for (String entry : finalRequestSplit) {

				String entrySplit[] = entry.split("=");
				requestMap.put(entrySplit[0],entrySplit[1]);
			}
			
		} catch (Exception exception) {
			logger.error("Exception while posting mobikwik payment request ", exception);
		}
		
		return requestMap;
	}

	public Map<String,String> generateAxisBankRequest(Fields fields) {
		Map<String,String> requestMap = new HashMap<>();
		try {
			String requestURL = PropertiesManager.propertiesMap.get("DirecpaySaleUrl");
			requestMap.put("url",requestURL);

			requestMap.put("requestParameter",fields.get(FieldType.DIRECPAY_FINAL_REQUEST.getName()));
			
		} catch (Exception exception) {
			logger.error("Exception", exception);
		}
		
		return requestMap;
	}

	public Map<String,String> generateAxisBankNBRequest(Fields fields) {
		Map<String,String> requestMap = new HashMap<>();
		try {
			// String requestURL = PropertiesManager.propertiesMap.get("AxisbankNBSaleUrl")
			// + "AXBHRT";
			// + fields.get(FieldType.ADF11.getName());

			String requestURL = PropertiesManager.propertiesMap.get("AxisbankNBSaleUrl")
					+ fields.get(FieldType.ADF11.getName());
			requestMap.put("url", requestURL);
			logger.info("requestURL " + requestURL);
			String reutrnURL = returnUrlCustomizer.customizeReturnUrl(fields,
					PropertiesManager.propertiesMap.get("AxisbankNBReturnUrl"));
			requestMap.put("qs", fields.get(FieldType.AXISBANK_NB_FINAL_REQUEST.getName()));
			requestMap.put("RU", reutrnURL);
		} catch (Exception exception) {
			logger.error("Exception", exception);
		}
		return requestMap;
	}

	/**
	 * This function will be used to post the data on Federal bank URL
	 */
	public Map<String,String> generateFederalBankNBRequest(Fields fields) {
		Map<String,String> requestMap = new HashMap<>();
		try {
			String requestURL = PropertiesManager.propertiesMap.get("FederalbankNBSaleUrl");

			logger.info(fields.get(FieldType.FEDERALBANK_NB_REQUEST.getName())
					+ "========FederalbankNBSaleUrl   requestURL " + requestURL);
			JSONObject reqJson = new JSONObject(fields.get(FieldType.FEDERALBANK_NB_REQUEST.getName()));

			requestMap.put("url", requestURL);

			for (String keys : reqJson.keySet()) {
				requestMap.put(keys,reqJson.get(keys).toString());
			}

		} catch (Exception exception) {
			logger.error("Exception", exception);
		}
		
		return requestMap;
	}

	public Map<String,String> generateBilldeskRequest(Fields fields) {
		Map<String,String> requestMap = new HashMap<>();
		try {
			String requestURL = PropertiesManager.propertiesMap.get("BilldeskSaleUrl");
			requestMap.put("url", requestURL);
			requestMap.put("msg", fields.get(FieldType.BILLDESK_FINAL_REQUEST.getName()));
			
		} catch (Exception exception) {
			logger.error("Exception", exception);
		}
		return requestMap;
	}

	public Map<String,String> generateIsgpayRequest(Fields fields) {
		Map<String,String> requestMap = new HashMap<>();
		try {
			String requestURL = PropertiesManager.propertiesMap.get("ISGPAYSaleUrl");
			requestMap.put("url", requestURL);
			logger.info("<<<<< requestURL >>>>>>> " + requestURL);
			logger.info("<<<<<< ISGPAY_FINAL_REQUEST >>>>>> " + fields.get(FieldType.ISGPAY_FINAL_REQUEST.getName()));
			logger.info("<<<<< MERCHANT_ID >>>>>>> " + fields.get(FieldType.MERCHANT_ID.getName()));
			logger.info("<<<<< TERMINAL_ID >>>>>>> " + fields.get(FieldType.TERMINAL_ID.getName()));

			String merchantId = null;
			String terminalId = null;

			merchantId = fields.get(FieldType.MERCHANT_ID.getName());
			terminalId = fields.get(FieldType.TERMINAL_ID.getName());

			requestMap.put("MerchantId", merchantId);
			requestMap.put("TerminalId", terminalId);
			requestMap.put("BankId", fields.get(FieldType.BANK_ID.getName()));
			requestMap.put("EncData", fields.get(FieldType.ISGPAY_FINAL_REQUEST.getName()));
			
		} catch (Exception exception) {
			logger.error("Exception", exception);
		}
		return requestMap;
	}
	public Map<String,String> generatecosmosQRBankNBRequest(Fields fields) {
		Map<String,String> requestMap = new HashMap<>();
		try {
			// String requestURL = PropertiesManager.propertiesMap.get("AxisbankNBSaleUrl")
			// + "AXBHRT";
			// + fields.get(FieldType.ADF11.getName());

			logger.info("fields data"+fields.getFieldsAsString());
			String requestURL = fields.get(FieldType.RETURN_URL.getName());
			if (fields.get(FieldType.PAYMENT_TYPE.getName()).equals(PaymentType.UPI.getName()))	
				
			{
				requestMap.put("url", (fields.get(FieldType.COSMOS_UPI_FINAL_REQUEST.getName())));

			}else {
				requestMap.put("url", generateQRCodeImage(fields.get(FieldType.COSMOS_UPI_FINAL_REQUEST.getName())));
	
			}
			
			logger.info("requestURL " + requestURL);
			
			requestMap.put("QR", fields.get(FieldType.COSMOS_UPI_FINAL_REQUEST.getName()));
			requestMap.put("ORDERID", fields.get(FieldType.ORDER_ID.getName()));
		} catch (Exception exception) {
			logger.error("Exception", exception);
		}
		return requestMap;
	}
	public static String generateQRCodeImage(String barcodeText) throws Exception {
		final ByteArrayOutputStream os = new ByteArrayOutputStream();
	    QRCodeWriter barcodeWriter = new QRCodeWriter();
	    BitMatrix bitMatrix = 
	      barcodeWriter.encode(barcodeText, BarcodeFormat.QR_CODE, 200, 200);
	    BufferedImage img = MatrixToImageWriter.toBufferedImage(bitMatrix);
	    		try (final java.io.OutputStream b64os = (java.io.OutputStream)Base64.getEncoder().wrap(os)) {
	    	        ImageIO.write(img, "png", b64os);
	    	    } catch (final IOException ioe) {
	    	        throw new UncheckedIOException(ioe);
	    	    }
	    	    return os.toString();
	}
	public Map<String,String> generateMatchMoveRequest(Fields fields) {
		Map<String,String> requestMap = new HashMap<>();
		try {
			String finalRequest = fields.get(FieldType.MATCH_MOVE_FINAL_REQUEST.getName());

			String finalRequestSplit[] = finalRequest.split("~");


			for (String entry : finalRequestSplit) {

				String entrySplit[] = entry.split("=");
				requestMap.put(entrySplit[0],entrySplit[1]);
			}
		} catch (Exception exception) {
			logger.error("Exception", exception);
		}
		return requestMap;

	}

	public Map<String,String> generateCamsPayRequest(Fields fields) {
		Map<String,String> requestMap = new HashMap<>();
		try {
			String paymentString = fields.get(FieldType.CAMSPAY_FINAL_REQUEST.getName());
			logger.info("generateCamsPayRequest:: fields={}", fields.getFieldsAsString());
			logger.info("generateCamsPayRequest:: post to acquirer paymentString={}", paymentString);
			String finalRequestSplit[] = paymentString.split("~");


			for (String entry : finalRequestSplit) {

				String entrySplit[] = entry.split("=");
				requestMap.put(entrySplit[0],entrySplit[1]);
			}
		} catch (Exception exception) {
			logger.error("generateCamsPayRequest:: failed", exception);
		}
		
		return requestMap;
	}


	public Map<String,String> sendMigsEnrollTransaction(Fields fields) throws SystemException {
		Map<String,String> requestMap = new HashMap<>();
		String url = ConfigurationConstants.AXIS_MIGS_TRANSACTION_URL.getValue();
		requestMap.put("url",url);
		String finalRequest = fields.get(FieldType.MIGS_FINAL_REQUEST.getName());
		String finalRequestSplit[] = finalRequest.split("~");

		for (String entry : finalRequestSplit) {

			String entrySplit[] = entry.split("=");
			requestMap.put(entrySplit[0],entrySplit[1]);
		}
		
		return requestMap;
	}

	public Map<String,String> generateEnrollIdbiRequest(Fields fields) {
		Map<String,String> requestMap = new HashMap<>();
		try {

			String returnUrl = returnUrlCustomizer.customizeReturnUrl(fields,
					PropertiesManager.propertiesMap.get("IdbiReturnUrl"));
			String AcsReq = fields.get(FieldType.ACS_REQ_MAP.getName());
			String value = HtmlUtils.htmlUnescape(AcsReq);
			JSONObject jsonData = new JSONObject(value);
			for (Object key : jsonData.keySet()) {
				requestMap.put(key.toString(), jsonData.getString(key.toString()));
			}
			requestMap.put("url",fields.get(FieldType.ACS_URL.getName()));
			requestMap.put(fields.get(FieldType.ACS_RETURN_URL.getName()),returnUrl);
			
		} catch (Exception exception) {
			logger.error("Exception", exception);
		}
		
		return requestMap;
	}

	public Map<String,String> lyraEnrollRequest(Fields responseMap) {
		Map<String,String> requestMap = new HashMap<>();
		try {
			/************* Enrolled card condition starts here ************/
			String acsurl = responseMap.get(FieldType.ACS_URL.getName());
			requestMap.put("url",acsurl);
			String request = responseMap.get(FieldType.LYRA_FINAL_REQUEST.getName());


			JSONObject object = new JSONObject(request);
			Iterator<String> keys = object.keys();

			while (keys.hasNext()) {
				String key = keys.next();
				String value = object.getString(key);
				requestMap.put(key,value);
			}

			/************* Enrolled card condition Ends here ************/
		} catch (Exception exception) {
			logger.error("Exception", exception);
		}
		
		return requestMap;
	}


	public  Map<String,String> generateCashfreeRequest(Fields fields) {
		Map<String,String> requestMap = new HashMap<>();
		try {
			
			String requestURL;
			JSONObject reqJson = new JSONObject(fields.get(FieldType.CASHFREE_FINAL_REQUEST.getName()));

			String simulatorFlag = PropertiesManager.propertiesMap.get("CashfreeSimulator");
			
			logger.info("simulatorFlag : "+simulatorFlag);
			if (simulatorFlag.equalsIgnoreCase("Y")) {
				requestURL = PropertiesManager.propertiesMap.get("CASHFREESimulatorUrl");
			} else {
				requestURL = PropertiesManager.propertiesMap.get("CASHFREESaleUrl");
			}
			logger.info("requestURL : "+requestURL);
			requestMap.put("url",requestURL);
			for (String keys : reqJson.keySet()) {
				requestMap.put(keys,reqJson.get(keys).toString());
			}

			
		} catch (Exception exception) {
			logger.error("Exception", exception);
		}
		
		return requestMap;
	}
	

	/*
	 * public Map<String,String> generateCashfreeRequest(Fields fields) {
	 * Map<String,String> requestMap = new HashMap<>(); try { String finalRequest =
	 * fields.get(FieldType.CASHFREE_FINAL_REQUEST.getName()); String
	 * finalRequestSplit[] = finalRequest.split("~");
	 * 
	 * for (String entry : finalRequestSplit) {
	 * 
	 * String entrySplit[] = entry.split("=");
	 * requestMap.put(entrySplit[0],entrySplit[1]); } } catch (Exception exception)
	 * { logger.error("Exception", exception); }
	 * 
	 * return requestMap; }
	 */



	public Map<String,String> generateEasebuzzRequest(Fields fields) {
		Map<String,String> requestMap = new HashMap<>();
		try {

			logger.info("Request Received for Easebuzz :: " + fields.getFieldsAsString());
			String requestURL = PropertiesManager.propertiesMap.get("EASEBUZZSaleUrl");
			requestMap.put("url",requestURL);
			logger.info("requestURL >>>>>>>>> " + requestURL);
			JSONObject reqJson = new JSONObject(fields.get(FieldType.EASEBUZZ_FINAL_REQUEST.getName()));
			logger.info("reqJson >>>>>> " + reqJson.toString());

			for (String keys : reqJson.keySet()) {
				requestMap.put(keys,reqJson.get(keys).toString());
			}

		} catch (Exception exception) {
			logger.error("Exception", exception);
		}
		
		return requestMap;
	}

	public Map<String,String> generateAgreepayRequest(Fields fields) {
		Map<String,String> requestMap = new HashMap<>();
		try {
			logger.info("Request Received for Agreepay :: " + fields.getFieldsAsString());
			String requestURL = PropertiesManager.propertiesMap.get("AGREEPAYSaleUrl");
			requestMap.put("url",requestURL);
			JSONObject reqJson = new JSONObject(fields.get(FieldType.AGREEPAY_FINAL_REQUEST.getName()));

			for (String keys : reqJson.keySet()) {
				requestMap.put(keys,reqJson.get(keys).toString());
			}
		} catch (Exception exception) {
			logger.error("Exception", exception);
		}
		
		return requestMap;
	}

	public Map<String,String> generatePinelabsRequest(Fields fields) {
		Map<String,String> requestMap = new HashMap<>();
		try {
			String request = fields.get(FieldType.PINELABS_FINAL_REQUEST.getName());
			String finalRequestSplit[] = request.split("~");

			for (String entry : finalRequestSplit) {

				String entrySplit[] = entry.split("=");
				requestMap.put(entrySplit[0],entrySplit[1]);
			}
		} catch (Exception exception) {
			logger.error("Exception", exception);
		}
		
		return requestMap;
	}

	public Map<String,String> generateYesBankNBRequest(Fields fields) {
		Map<String,String> requestMap = new HashMap<>();
		try {
			String requestURL = PropertiesManager.propertiesMap.get("YESBANK_SALE_RETURN_URL");
			requestMap.put("url",requestURL);
			logger.info(fields.get(FieldType.YESBANKNB_FINAL_REQUEST.getName())
					+ "======== yesbankNBSaleUrl requestURL " + requestURL);
			String reqJson = fields.get(FieldType.YESBANKNB_FINAL_REQUEST.getName());

			requestMap.put("PID",fields.get(FieldType.MERCHANT_ID.getName()));
			requestMap.put("encdata",reqJson);
			
		} catch (Exception exception) {
			logger.error("YESBANKNB generateYesBankNBRequest Exception", exception);
		}
		return requestMap;
	}


}
