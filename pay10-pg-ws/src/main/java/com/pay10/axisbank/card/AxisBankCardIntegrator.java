package com.pay10.axisbank.card;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.pay10.commons.exception.SystemException;
import com.pay10.commons.util.FieldType;
import com.pay10.commons.util.Fields;
import com.pay10.commons.util.TransactionType;

@Service
public class AxisBankCardIntegrator {

	@Autowired
	@Qualifier("axisBankCardTransactionConverter")
	private TransactionConverter converter;

	@Autowired
	@Qualifier("axisBankCardransactionCommunicator")
	private TransactionCommunicator communicator;

	@Autowired
	@Qualifier("axisBankCardTransactionFactory")
	private TransactionFactory transactionFactory;
	
	private AxisBankCardTransformer axisBankTransformer = null;

	public void process(Fields fields) throws SystemException {

		send(fields);

	}// process

	public void send(Fields fields) throws SystemException {

		Transaction transactionRequest = new Transaction();
		Transaction transactionResponse = new Transaction();

		transactionRequest = transactionFactory.getInstance(fields);

		String request = converter.perpareRequest(fields, transactionRequest);

		String txnType = fields.get(FieldType.TXNTYPE.getName());
		if (txnType.equals(TransactionType.SALE.getName())) {
			communicator.updateSaleResponse(fields, request);
		} else {
			String response = communicator.getResponse(request, fields);

			 transactionResponse = converter.toTransaction(response,fields.get(FieldType.TXNTYPE.getName()));
			 axisBankTransformer = new AxisBankCardTransformer(transactionResponse);
			 axisBankTransformer.updateResponse(fields);
		}

	}

}
