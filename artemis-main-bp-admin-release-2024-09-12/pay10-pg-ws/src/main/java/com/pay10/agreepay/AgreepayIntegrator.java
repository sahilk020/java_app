package com.pay10.agreepay;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.pay10.commons.exception.SystemException;
import com.pay10.commons.util.FieldType;
import com.pay10.commons.util.Fields;
import com.pay10.commons.util.TransactionType;

@Service
public class AgreepayIntegrator {
	@Autowired
	@Qualifier("agreepayTransactionConverter")
	private TransactionConverter converter;
	
	@Autowired
	@Qualifier("agreepayTransactionCommunicator")
	private TransactionCommunicator communicator;
	
	@Autowired
	private TransactionFactory TransactionFactory;
	
	private AgreepayTransformer agreepayTransformer = null;

	public void process(Fields fields) throws SystemException {

		send(fields);

	}//process
	
	public void send(Fields fields) throws SystemException {
		
		Transaction transactionRequest = new Transaction();
		Transaction transactionResponse = new Transaction();
		
		transactionRequest = TransactionFactory.getInstance(fields);

		String request = converter.perpareRequest(fields, transactionRequest);

		String txnType = fields.get(FieldType.TXNTYPE.getName());
		if (txnType.equals(TransactionType.SALE.getName())) {
			communicator.updateSaleResponse(fields, request);
		} else {
			String response = communicator.getResponse(request, fields);

			 transactionResponse = converter.toTransactionRefund(response);
			 agreepayTransformer = new AgreepayTransformer(transactionResponse);
			 
			 if (fields.get(FieldType.TXNTYPE.getName()).equalsIgnoreCase(TransactionType.REFUND.getName())) {
				 agreepayTransformer.updateRefundResponse(fields);
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

	public AgreepayTransformer getAgreepayTransformer() {
		return agreepayTransformer;
	}

	public void setAgreepayTransformer(AgreepayTransformer agreepayTransformer) {
		this.agreepayTransformer = agreepayTransformer;
	}

}

	

