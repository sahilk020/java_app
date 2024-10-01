package com.pay10.easebuzz;

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
public class EasebuzzIntegrator {
	
	private static final Logger logger = LoggerFactory.getLogger(EasebuzzIntegrator.class.getName());
	@Autowired
	@Qualifier("easebuzzTransactionConverter")
	private TransactionConverter converter;
	
	@Autowired
	@Qualifier("easebuzzTransactionCommunicator")
	private TransactionCommunicator communicator;
	
	@Autowired
	private TransactionFactory transactionFactory;
	
	private EasebuzzTransformer easebuzzTransformer = null;

	public void process(Fields fields) throws SystemException {

		send(fields);

	}//process
	
	public void send(Fields fields) throws SystemException {
		
		Transaction transactionRequest = new Transaction();
		Transaction transactionResponse = new Transaction();
		
		if (fields.get(FieldType.TXNTYPE.getName()).equalsIgnoreCase(TransactionType.ENROLL.getName())) {
			fields.put(FieldType.TXNTYPE.getName(),TransactionType.SALE.getName());
		}
		transactionRequest = transactionFactory.getInstance(fields);
		
		String request = converter.perpareRequest(fields, transactionRequest);

		logger.info("request of easebuzz"+request);
		String txnType = fields.get(FieldType.TXNTYPE.getName());
		if (txnType.equals(TransactionType.SALE.getName())) {
			communicator.updateSaleResponse(fields, request);
		} 
		else 
		{
			String response = communicator.getResponse(request, fields);
			logger.info("aaaaaaaaaaaaaaaa"+response);

			 transactionResponse = converter.toTransactionRefund(response);
			 easebuzzTransformer = new EasebuzzTransformer(transactionResponse);
			 
			 if (fields.get(FieldType.TXNTYPE.getName()).equalsIgnoreCase(TransactionType.REFUND.getName())) {
				 easebuzzTransformer.updateRefundResponse(fields);
			 }
			 
		}

	}

	public TransactionConverter getConverter() {
		return converter;
	}

	public void setConverter(TransactionConverter converter) {
		this.converter = converter;
	}

	public TransactionCommunicator getCommunicator() {
		return communicator;
	}

	public void setCommunicator(TransactionCommunicator communicator) {
		this.communicator = communicator;
	}

	public EasebuzzTransformer getCashfreeTransformer() {
		return easebuzzTransformer;
	}

	public void setCashfreeTransformer(EasebuzzTransformer cashfreeTransformer) {
		this.easebuzzTransformer = cashfreeTransformer;
	}

}
