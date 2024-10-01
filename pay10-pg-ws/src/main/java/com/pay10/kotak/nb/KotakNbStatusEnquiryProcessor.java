package com.pay10.kotak.nb;

import java.io.UnsupportedEncodingException;
import java.security.GeneralSecurityException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.pay10.axisbank.netbanking.AxisBankNBTransformer;
import com.pay10.axisbank.netbanking.Transaction;
import com.pay10.commons.dao.FieldsDao;
import com.pay10.commons.exception.ErrorType;
import com.pay10.commons.exception.SystemException;
import com.pay10.commons.util.FieldType;
import com.pay10.commons.util.Fields;
import com.pay10.commons.util.PropertiesManager;
import com.pay10.commons.util.StatusType;
import com.pay10.commons.util.TransactionType;
import com.pay10.pg.core.util.AxisBankNBEncDecService;
import com.pay10.pg.core.util.KotakNBUtils;
import com.pay10.pg.core.util.refundkotaknb;

@Service
public class KotakNbStatusEnquiryProcessor {
	private static Logger logger = LoggerFactory.getLogger(KotakNbStatusEnquiryProcessor.class.getName());

	@Autowired
	@Qualifier("kotakNBUtils")
	private KotakNBUtils kotakNBUtils;
	
	@Autowired
	private FieldsDao fieldsDao;
	
	@Autowired
	@Qualifier("kotakNBTransactionCommunicator")
	private TransactionCommunicator communicator;
	
public void enquiryProcessor(Fields fields) {
		
		String response = "";
		//	String saleDate  = fieldsDao.getCaptureDate(fields.get(FieldType.PG_REF_NUM.getName()));
		//	fields.put(FieldType.CREATE_DATE.getName(), saleDate);
			String request = statusEnquiryRequest(fields);
			String	hostUrl = PropertiesManager.propertiesMap.get("refundurl");

			 response = communicator.refundPostRequest(request, hostUrl,fields);

		updateFields(fields, response);

	}
private String statusEnquiryRequest(Fields fields) {
	String encData = null;
	try {	String merchantCode = fields.get(FieldType.MERCHANT_ID.getName());
	String encryptKey=fields.get(FieldType.ADF3.getName());
	logger.info("checksum"+encryptKey);
	
	
	
	//"211995C8985ABA0F291E1941608FEFBD";
	String checksumKey = fields.get(FieldType.ADF2.getName());   
	
	String requestDate = getRequestDateFormat();

	StringBuilder paymentStringBuilder = new StringBuilder();

	paymentStringBuilder.append("0520");
	paymentStringBuilder.append("|");
	paymentStringBuilder.append(requestDate);
	paymentStringBuilder.append("|");
	paymentStringBuilder.append(merchantCode);
	paymentStringBuilder.append("|");
	paymentStringBuilder.append(fields.get(FieldType.PG_REF_NUM.getName()));
	paymentStringBuilder.append("|");
	paymentStringBuilder.append("|");
	paymentStringBuilder.append("|");
	

	
	logger.info("Kotak NetBanking refund Request : " + paymentStringBuilder.toString());
	//String checksum = kotakNBUtils.encodeWithHMACSHA2(paymentStringBuilder.toString(), checksumKey);
	//String checksum = kotakNBUtils.calculateHMAC(paymentStringBuilder.toString(), checksumKey);
	String checksum = kotakNBUtils.getHMAC256Checksum(paymentStringBuilder.toString(), checksumKey);
	String data =checksum.toLowerCase();
	
	paymentStringBuilder.append(data);
	
	logger.info("Kotak NetBanking refund Request with checksum : " + paymentStringBuilder.toString()+"    "+encryptKey);



	encData = refundkotaknb.encrypt(paymentStringBuilder.toString(), encryptKey);
} catch (UnsupportedEncodingException e) {
	// TODO Auto-generated catch block
	e.printStackTrace();
} catch (GeneralSecurityException e) {
	// TODO Auto-generated catch block
	e.printStackTrace();
} // publicKeyPath

	return encData;
}
public static String getRequestDateFormat() {
	Date dNow = new Date();
	SimpleDateFormat ft = new SimpleDateFormat("DDMMYYYYHH");
	return ft.format(dNow);

}

public void updateFields(Fields fields, String Response) {
try {
	

	String encryptKey =fields.get(FieldType.ADF3.getName());                  //"211995C8985ABA0F291E1941608FEFBD";
	
	String	encData = refundkotaknb.decrypt(Response, encryptKey);
	
		String status = "";
		ErrorType errorType = null;
		String pgTxnMsg = null;
		
		logger.info("encData"+encData);
		String s1=encData;  
		String[] words=s1.split("\\|");   //"  0051|07032022181903|OSENAM|1009120307111447||1.00|N|KPY209|dd1dcf73e29ef61608abb96c762bf1babd2769cb20eb140e3d2008b6010078f1"
		logger.info(words[3]);
		String pgref=words[3];
		String code=words[5];
		String amount=words[4];
		String bankref=words[6];
			if (StringUtils.isNotBlank(code) && ((code).equals("Y"))) {
				status = StatusType.CAPTURED.getName();
				errorType = ErrorType.SUCCESS;
				pgTxnMsg = ErrorType.SUCCESS.getResponseMessage();
				//errorCode = ErrorType.SUCCESS.getCode();

			} else {

				status = StatusType.FAILED.getName();
				errorType = ErrorType.FAILED;
				pgTxnMsg = ErrorType.FAILED.getResponseMessage();
				//errorCode = ErrorType.SUCCESS.getCode();

			}

		fields.put(FieldType.STATUS.getName(), status);
		fields.put(FieldType.RESPONSE_MESSAGE.getName(), errorType.getResponseMessage());
		fields.put(FieldType.RESPONSE_CODE.getName(), errorType.getResponseCode());

		//fields.put(FieldType.AUTH_CODE.getName(), transaction.getAuthCode());
		fields.put(FieldType.ACQ_ID.getName(),bankref);
		fields.put(FieldType.PG_RESP_CODE.getName(), code);
		fields.put(FieldType.PG_TXN_MESSAGE.getName(), pgTxnMsg);
		fields.put(FieldType.RRN.getName(),bankref );
		fields.put(FieldType.PG_TXN_STATUS.getName(),code );

	

}
	catch(Exception e){
		logger.error("Exception in parsing status enquiry response for axis bank nb ",e);
	}
	
	
	

}

}
