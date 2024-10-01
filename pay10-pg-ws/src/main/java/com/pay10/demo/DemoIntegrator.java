package com.pay10.demo;

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
public class DemoIntegrator {
	
	private static final Logger logger = LoggerFactory.getLogger(DemoIntegrator.class.getName());
	@Autowired
	@Qualifier("demoTransactionConverter")
	private TransactionConverter converter;
	
	@Autowired
	@Qualifier("demoTransactionCommunicator")
	private TransactionCommunicator communicator;
	
	@Autowired
	private TransactionFactory TransactionFactory;
	
	private DemoTransformer demoTransformer = null;

	public void process(Fields fields) throws SystemException {

		send(fields);

	}//process
	
	public void send(Fields fields) throws SystemException {
		
		Transaction transactionRequest = new Transaction();
		Transaction transactionResponse = new Transaction();
		
		logger.info("Before Set fields for SBI >>>>>>>>>> "+fields.getFieldsAsString());
		if (fields.get(FieldType.TXNTYPE.getName()).equalsIgnoreCase(TransactionType.ENROLL.getName())) {
			fields.put(FieldType.TXNTYPE.getName(),TransactionType.SALE.getName());
		}
		logger.info("After Set fields for CashFree >>>>>>>>>> "+fields.getFieldsAsString());
		
		transactionRequest = TransactionFactory.getInstance(fields);

		String request = converter.perpareRequest(fields, transactionRequest);

		String txnType = fields.get(FieldType.TXNTYPE.getName());
		if (txnType.equals(TransactionType.SALE.getName())) {
			communicator.updateSaleResponse(fields, request);
		} else {
			String response = communicator.getResponse(request, fields);

			 transactionResponse = converter.toTransactionRefund(response);
			 demoTransformer = new DemoTransformer(transactionResponse);
			 
			 if (fields.get(FieldType.TXNTYPE.getName()).equalsIgnoreCase(TransactionType.REFUND.getName())) {
				 demoTransformer.updateRefundResponse(fields);
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

	public DemoTransformer getCashfreeTransformer() {
		return demoTransformer;
	}

	public void setCashfreeTransformer(DemoTransformer demoTransformer) {
		this.demoTransformer = demoTransformer;
	}

}
