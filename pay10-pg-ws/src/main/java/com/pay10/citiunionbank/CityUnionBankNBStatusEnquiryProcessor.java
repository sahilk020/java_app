package com.pay10.citiunionbank;

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

import com.pay10.commons.exception.ErrorType;
import com.pay10.commons.exception.SystemException;
import com.pay10.commons.util.Amount;
import com.pay10.commons.util.Currency;
import com.pay10.commons.util.FieldType;
import com.pay10.commons.util.Fields;
import com.pay10.commons.util.PropertiesManager;
import com.pay10.commons.util.StatusType;
import com.pay10.pg.core.pageintegrator.CityUnionBankIUntil;

@Service
public class CityUnionBankNBStatusEnquiryProcessor {
	private static Logger logger = LoggerFactory.getLogger(CityUnionBankNBStatusEnquiryProcessor.class.getName());
		private static int TIMEOUT =60000;

	
private TransactionConverter converter;
	

	@Autowired
	CityUnionBankIUntil cityUnionBankIUntil;

	@Autowired
	CityUnionBankNBSaleResponseHandler cityUnionBankNBSaleResponseHandler;
	
	public CityUnionBankNBStatusEnquiryProcessor(
			@Qualifier("cityUnionBankTransactionConverter") TransactionConverter converter) 
	{
		super();
		this.converter = converter;
	}


	public void enquiryProcessor(Fields fields) throws SystemException {

		String response = "";
		
		String request = statusEnquiryRequest(fields);
		String hostUrl = PropertiesManager.propertiesMap.get("CITYUNIONBANK_SALE_URL");
	
		response = cityUnionBankNBSaleResponseHandler.Statusenquirerresponse(request, hostUrl);
	
		String []arrdata=response.split("<body>");
		String []arrdata1=arrdata[1].split("</body>");

		String decResponse=cityUnionBankNBSaleResponseHandler.decrytResponse(arrdata1[0],fields);
		updateFields(fields, decResponse);

	}
	
public String statusEnquiryRequest(Fields fields) {
		
		String currency = Currency.getAlphabaticCode(fields.get(FieldType.CURRENCY_CODE.getName()));
		String merchantCode = fields.get(FieldType.MERCHANT_ID.getName());
		String iv = fields.get(FieldType.ADF10.getName());

		String encryptKey = fields.get(FieldType.ADF11.getName());
		String civ = fields.get(FieldType.ADF4.getName());

		String cencryptKey = fields.get(FieldType.ADF5.getName());
		String HandleId = fields.get(FieldType.ADF9.getName());
		String Pid = fields.get(FieldType.ADF3.getName());
		
		StringBuilder salerequest = new StringBuilder();

		salerequest.append("HandleID=");
		salerequest.append(HandleId);

		salerequest.append("&PID=");
		salerequest.append(Pid);
		salerequest.append("&merchantcode=");

		salerequest.append(merchantCode);
		salerequest.append("&trantype=V&");
		logger.info(" raw doubleVerification request  City Union bank ={}"  ,salerequest);
//		BRN=8006177963688806&AMT=4.0&RU=https://test.timesofmoney.com/direcpay/secure/transactionRe 
//			sponse.jsp&NAR=CA&ACCNO=NA
		StringBuilder dateEnc = new StringBuilder();
		dateEnc.append("BRN=");
		dateEnc.append(fields.get(FieldType.PG_REF_NUM.getName()));
		dateEnc.append("&AMT=");
		dateEnc.append(Amount.toDecimal(fields.get(FieldType.TOTAL_AMOUNT.getName()), "356"));
		
		dateEnc.append("&CHECKSUM=");

		dateEnc.append(cityUnionBankIUntil.calculateChecksum(fields.get(FieldType.PG_REF_NUM.getName())
				+Amount.toDecimal(fields.get(FieldType.TOTAL_AMOUNT.getName()), "356")));
		logger.info(" encrypt doubleVerification request  before data encryption union bank={}" , dateEnc);

		String encryptyDate = cityUnionBankIUntil.getEncryptedString(iv, encryptKey, dateEnc.toString());
		logger.info(" encrypt doubleVerification request   Data  citry union bank={}" , encryptyDate);
		salerequest.append("DATA=");
		salerequest.append(encryptyDate);
		logger.info(" raw doubleVerification request  city union bank ={}",salerequest);
		String encryptyRequest = cityUnionBankIUntil.getEncryptedString(civ, cencryptKey, salerequest.toString());
		logger.info(" encrypt  doubleVerification request  city union bank={}" , encryptyRequest);

		logger.info("Request doubleVerification  CITY UNION BANK={}", encryptyRequest);

		return encryptyRequest;


	}


	
	
	
public void updateFields(Fields feilds, String response) {

    String status = "";
	ErrorType errorType = null;
	String pgTxnMsg = null;
	if(response.contains("Y")){
		String [] data=response.split("-");
		
		if(data[0].equalsIgnoreCase("Y")) {
			status = StatusType.CAPTURED.getName();
			errorType = ErrorType.SUCCESS;
			pgTxnMsg = ErrorType.SUCCESS.getResponseMessage();
    		feilds.put(FieldType.RRN.getName(),data[1] );
    		feilds.put(FieldType.ACQ_ID.getName(), data[1]);

		}

	}else {
		if(response.equalsIgnoreCase("N")) {
			status = StatusType.FAILED_AT_ACQUIRER.getName();
			errorType = ErrorType.getInstanceFromCode("022");
    		feilds.put(FieldType.RESPONSE_MESSAGE.getName(),"FAILED");

			
				pgTxnMsg = "Transaction failed at acquirer";
				
		}
	}
		

	    
	    		
	    		feilds.put(FieldType.STATUS.getName(), status);
	    		feilds.put(FieldType.RESPONSE_CODE.getName(), errorType.getResponseCode());
	    		

	    		
	    		feilds.put(FieldType.PG_TXN_MESSAGE.getName(), pgTxnMsg);
		
		
		
	}
	
	
	

}
