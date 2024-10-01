package com.pay10.federalNB;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.pay10.commons.exception.SystemException;
import com.pay10.commons.util.FieldType;
import com.pay10.commons.util.Fields;
import com.pay10.commons.util.TransactionType;

@Service
public class FederalBankNBIntegrator {

	@Autowired
	@Qualifier("federalBankNBTransactionConverter")
	private TransactionConverter converter;

	@Autowired
	@Qualifier("federalBankNBTransactionCommunicator")
	private TransactionCommunicator communicator;

	@Autowired
	@Qualifier("federalBankNBTransactionFactory")
	private TransactionFactory transactionFactory;
	
	public void process(Fields fields) throws SystemException {

		send(fields);

	}// process

	public void send(Fields fields) throws SystemException {

		Transaction transactionRequest = new Transaction();
		Transaction transactionResponse = new Transaction();

		if (fields.get(FieldType.TXNTYPE.getName()).equalsIgnoreCase(TransactionType.ENROLL.getName())) {
			fields.put(FieldType.TXNTYPE.getName(),TransactionType.SALE.getName());
		}
		
		transactionRequest = transactionFactory.getInstance(fields);

		String request = converter.perpareRequest(fields, transactionRequest);

		String txnType = fields.get(FieldType.TXNTYPE.getName());
		if (txnType.equals(TransactionType.SALE.getName())) {
			communicator.updateSaleResponse(fields, request);
		} else {
			String response = communicator.getResponse(request, fields);

			 transactionResponse = converter.toTransactionRefund(response);
			 FederalBankNBTransformer  federalBankTransformer = new FederalBankNBTransformer(transactionResponse);
			 federalBankTransformer.updateRefundResponse(fields);
		}

	}

}
