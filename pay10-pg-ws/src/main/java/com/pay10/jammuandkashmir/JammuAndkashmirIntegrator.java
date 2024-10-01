package com.pay10.jammuandkashmir;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.pay10.commons.exception.SystemException;
import com.pay10.commons.util.FieldType;
import com.pay10.commons.util.Fields;
import com.pay10.commons.util.TransactionType;

@Service

public class JammuAndkashmirIntegrator {
	
	private static Logger logger = LoggerFactory.getLogger(JammuAndkashmirIntegrator.class.getName());

	@Autowired
	@Qualifier("jammuandkashmirNBFactory")
	private TransactionFactory transactionFactory;

	@Autowired
	@Qualifier("jammuandkashmirNBTransactionConverter")
	private TransactionConverter converter;

	
	public void process(Fields fields) throws SystemException {

		send(fields);

	}//process
	
	public void send(Fields fields) throws SystemException {
		
		Transaction transactionRequest = new Transaction();
		Transaction transactionResponse = new Transaction();
		transactionRequest = transactionFactory.getInstance(fields);
		logger.info("feild for jammu and kishmir"+fields.getFieldsAsString());
		if (fields.get(FieldType.TXNTYPE.getName()).equalsIgnoreCase(TransactionType.ENROLL.getName())) {
			fields.put(FieldType.TXNTYPE.getName(),TransactionType.SALE.getName());
		}
		String request = converter.perpareRequest(fields, transactionRequest);
		logger.info("feild for jammu and kishmir"+fields.getFieldsAsString());

		String txnType = fields.get(FieldType.TXNTYPE.getName());
		

		if (txnType.equals(TransactionType.SALE.getName())) {
			converter.updateSaleResponse(fields, request);		} 
//		else {
//			String response = communicator.getResponse(request, fields);
//
//			 transactionResponse = converter.toTransaction(response);
//			 kotakTransformer = new KotakTransformer(transactionResponse);
//			 kotakTransformer.updateResponse(fields);
//		}

	}
}
