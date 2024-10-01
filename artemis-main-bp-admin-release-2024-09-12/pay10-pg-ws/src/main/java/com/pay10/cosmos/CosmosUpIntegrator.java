package com.pay10.cosmos;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.pay10.commons.exception.ErrorType;
import com.pay10.commons.exception.SystemException;
import com.pay10.commons.util.FieldType;
import com.pay10.commons.util.Fields;
import com.pay10.commons.util.StatusType;
import com.pay10.commons.util.TransactionType;

@Service
public class CosmosUpIntegrator {
	
	private static final Logger logger = LoggerFactory.getLogger(CosmosUpIntegrator.class.getName());
	@Autowired
	@Qualifier("cosmosupTransactionConverter")
	private TransactionConverter converter;


	public void process(Fields fields) throws SystemException {

		send(fields);

	}//process
	
	public void send(Fields fields) throws SystemException {
		
		logger.info("Before Set fields for cosmos upi intent >>>>>>>>>> "+fields.getFieldsAsString());
		if (fields.get(FieldType.TXNTYPE.getName()).equalsIgnoreCase(TransactionType.ENROLL.getName())) {
			fields.put(FieldType.TXNTYPE.getName(),TransactionType.SALE.getName());
		}
		logger.info("After Set fields for cosmos upi intent >>>>>>>>>> "+fields.getFieldsAsString());
		
		

		String response = converter.perpareRequest(fields);
		

		logger.info("response For cosmos upi intent :: "+response);
		JSONObject resJson = new JSONObject(response);
		String qrString = null, status = null;
		if (response.contains("qrString")) {
	 qrString= (resJson.get("qrString").toString());
		}

		if (response.contains("status")) {
			status=(resJson.get("status").toString());
		}
		try {
			qrString=	URLDecoder.decode(qrString, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		String txnType = fields.get(FieldType.TXNTYPE.getName());
		if (txnType.equals(TransactionType.SALE.getName())||status.equalsIgnoreCase("SUCCESS")) {
			fields.put(FieldType.COSMOS_UPI_FINAL_REQUEST.getName(),  qrString  );
			fields.put(FieldType.STATUS.getName(), StatusType.SENT_TO_BANK.getName());
			fields.put(FieldType.RESPONSE_CODE.getName(), ErrorType.SUCCESS.getResponseCode());
			fields.put(FieldType.RESPONSE_MESSAGE.getName(), ErrorType.SUCCESS.getResponseMessage());
		
		 
		}

	}

	public TransactionConverter getConverter() {
		return converter;
	}

	public void setConverter(TransactionConverter converter) {
		this.converter = converter;
	}

	

	

}