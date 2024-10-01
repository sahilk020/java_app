package com.pay10.freecharge;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.pay10.commons.exception.SystemException;
import com.pay10.commons.util.FieldType;
import com.pay10.commons.util.Fields;
import com.pay10.commons.util.TransactionType;

@Service
public class FreeChargeIntegrator {

	@Autowired
	@Qualifier("freeChargeTransactionConverter")
	private TransactionConverter converter;

	@Autowired
	@Qualifier("freeChargeTransactionCommunicator")
	private TransactionCommunicator communicator;

	@Autowired
	@Qualifier("freeChargeFactory")
	private TransactionFactory transactionFactory;

	private FreeChargeTransformer freeChargeTransformer = null;

	public void process(Fields fields) throws SystemException {

		send(fields);

	}// process

	public void send(Fields fields) throws SystemException {

		Transaction transactionRequest = new Transaction();
		Transaction transactionResponse = new Transaction();

		transactionRequest = transactionFactory.getInstance(fields);
		String request = converter.perpareRequest(fields, transactionRequest);
		String response = communicator.getResponse(request, fields);

		transactionResponse = converter.toTransaction(response, fields.get(FieldType.TXNTYPE.getName()));
		if (fields.get(FieldType.TXNTYPE.getName()).equalsIgnoreCase(TransactionType.SALE.getCode())) {
			freeChargeTransformer = new FreeChargeTransformer(transactionResponse);
			freeChargeTransformer.updateResponse(fields);
		} else {
			freeChargeTransformer = new FreeChargeTransformer(transactionResponse);
			freeChargeTransformer.updateRefundResponse(fields);
		}

	}
}
