package com.pay10.citiunionbank;
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
public class CityUnionBankIntegrator 
	{
  private static final Logger log = LoggerFactory.getLogger(CityUnionBankIntegrator.class.getName());
	
	private TransactionFactory transactionFactory;
	
	private TransactionConverter converter;
	
	

	@Autowired
	public CityUnionBankIntegrator(@Qualifier("cityUnionBankNBFactory")TransactionFactory transactionFactory,
			@Qualifier("cityUnionBankTransactionConverter") TransactionConverter converter) 
	{
		this.transactionFactory = transactionFactory;
		this.converter = converter;
	}

	public void process(Fields fields) throws SystemException 
	{
		send(fields);

	}// process

	public void send(Fields fields) throws SystemException 
	{
		Transaction transactionRequest = new Transaction();
		log.info("feild for  city union bank={}", fields.getFieldsAsString());
		if (fields.get(FieldType.TXNTYPE.getName()).equalsIgnoreCase(TransactionType.ENROLL.getName())) 
		{
			fields.put(FieldType.TXNTYPE.getName(), TransactionType.SALE.getName());
		}
		transactionRequest = transactionFactory.getInstance(fields);

		String request = converter.perpareRequest(fields, transactionRequest);
		log.info("feild for city={},request={}", fields.getFieldsAsString(), request);

		String txnType = fields.get(FieldType.TXNTYPE.getName());

		if (txnType.equals(TransactionType.SALE.getName()))
		{
			converter.updateSaleResponse(fields, request);
		}

	}
}
