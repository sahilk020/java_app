package com.pay10.jammuandkashmir;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.pay10.commons.exception.SystemException;
import com.pay10.commons.util.Amount;
import com.pay10.commons.util.Currency;
import com.pay10.commons.util.FieldType;
import com.pay10.commons.util.Fields;
import com.pay10.commons.util.PropertiesManager;

@Service
public class JammuandKishmirBankNBStatusEnquiryProcessor {
	private static Logger logger = LoggerFactory.getLogger(JammuandKishmirBankNBStatusEnquiryProcessor.class.getName());


	@Autowired
	@Qualifier("jammuandkashmirNBTransactionConverter")
	private TransactionConverter converter;
	

	/*
	 * @Autowired private JammuandKashmirBankNBTransformer
	 * jammuandKashmirBankNBTransformer;
	 */

	
	public void enquiryProcessor(Fields fields) throws SystemException {

		String response = "";
		// String saleDate =
		// fieldsDao.getCaptureDate(fields.get(FieldType.PG_REF_NUM.getName()));
		// fields.put(FieldType.CREATE_DATE.getName(), saleDate);
		String request = statusEnquiryRequest(fields);
		response = getResponse(request);

		updateFields(fields, response);

	}

	
	public String statusEnquiryRequest(Fields fields) {
		//	&PID=40110&CG=Y&PRN=MJKB0000446119&
			//ITC=MJKB0000446119&AMT=27.48&CRN=INR
		
		String currency=Currency.getAlphabaticCode(fields.get(FieldType.CURRENCY_CODE.getName()));
		String amount=fields.get(FieldType.TOTAL_AMOUNT.getName());
		amount = Amount.toDecimal(amount, fields.get(FieldType.CURRENCY_CODE.getName()));
		String returnUrl = PropertiesManager.propertiesMap.get("JAMMUANDKASHMIR_RETURNURL");

		StringBuilder request = new StringBuilder();
		request.append("PID=");
		request.append(fields.get(FieldType.MERCHANT_ID.getName()));
		request.append("&CG=Y");
		request.append("&PRN=");
		request.append(fields.get(FieldType.PG_REF_NUM.getName()));
		request.append("&ITC=");
		request.append("PAY10");
		request.append("&AMT=");
		request.append(amount);
		request.append("&CRN=");
		request.append(currency);
		request.append("&RU=");
		request.append(returnUrl);

		request.append("BID=");
		request.append("");
		request.append("&STATFLG=H");

		
		logger.info("request for Status Enquiry in jammuand kishmir bank={}",request);
			return request.toString();
		}
	public String getResponse(String request)
	{    String response = null;
		try {
			String	hostUrl = PropertiesManager.propertiesMap.get("JammuandKishmirbankNBStatusEnqUrl");
			logger.info("request of jammu and kishmir bank dual verification  "+request+"        url     "+hostUrl);
						HttpURLConnection connection = null;
						URL url;
						url = new URL(hostUrl);
						connection = (HttpURLConnection) url.openConnection();
						connection.setRequestMethod("POST");
						connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
				connection.setUseCaches(false);
						connection.setDoInput(true);
						connection.setDoOutput(true);
						connection.setConnectTimeout(60000);
						connection.setReadTimeout(60000);

						// Send request
						DataOutputStream wr = new DataOutputStream(connection.getOutputStream());
						wr.writeBytes(request);
						wr.flush();
						wr.close();

						// Get Response
						InputStream is = connection.getInputStream();

						BufferedReader bufferedreader = new BufferedReader(new InputStreamReader(is));
						String decodedString;

						while ((decodedString = bufferedreader.readLine()) != null) {
							response = response + decodedString;
						}

						bufferedreader.close();

						logger.info("Response for dual verfication for jammuand kishmir  >> " + response);

					} catch (Exception e) {
						logger.error("Exception in getting dual verfication for jammuand kishmir  ", e);
						return response;
					}
					return response;
					
				}
	
public void updateFields(Fields fields, String response) {
	
	Transaction transactionStatusResponse =converter.toTransaction(response,fields.get(FieldType.TXNTYPE.getName()));
	String amount = Amount.toDecimal(fields.get(FieldType.TOTAL_AMOUNT.getName()), "356");

	 if (fields.get(FieldType.PG_REF_NUM.getName()).equalsIgnoreCase(transactionStatusResponse.getPrn())
				 &&amount.equalsIgnoreCase(transactionStatusResponse.getAmt())) {
		
	JammuandKashmirBankNBTransformer jmk = new JammuandKashmirBankNBTransformer(transactionStatusResponse);
	jmk.updateResponse(fields);
	 }		
}
}
