package com.pay10.pg.autodebit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.pay10.commons.exception.SystemException;
import com.pay10.commons.util.FieldType;
import com.pay10.commons.util.Fields;
import com.pay10.commons.util.TransactionType;

@Service
public class AtlIntegrator {
	
	@Autowired
	private TransactionFactory TransactionFactory;
	
	@Autowired
	@Qualifier("atlTransactionConverter")
	private TransactionConverter converter;
	
	@Autowired
	@Qualifier("atlTransactionCommunicator")
	private TransactionCommunicator communicator;
	
	public void process(Fields fields) throws SystemException {

		send(fields);

	}// process
	
	public void send(Fields fields) throws SystemException {

		Transaction transactionRequest = new Transaction();
		Transaction transactionResponse = new Transaction();

		transactionRequest = TransactionFactory.getInstance(fields);

		String request = converter.perpareRequest(fields, transactionRequest);

		String txnType = fields.get(FieldType.TXNTYPE.getName());
		if (txnType.equals(TransactionType.SALE.getName())) {
			communicator.updateSaleResponse(fields, request);
		} else {
			
		}

	}

}
