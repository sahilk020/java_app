package com.pay10.billdesk;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.pay10.commons.exception.SystemException;
import com.pay10.commons.util.FieldType;
import com.pay10.commons.util.Fields;
import com.pay10.commons.util.TransactionType;

@Service
public class BilldeskUpiIntegrator {

	@Autowired
	@Qualifier("billdeskTransactionConverter")
	private TransactionConverter converter;

	@Autowired
	@Qualifier("billdeskTransactionCommunicator")
	private TransactionCommunicator communicator;

	@Autowired
	private TransactionFactory TransactionFactory;

	public void process(Fields fields) throws SystemException {

		send(fields);

	}// process

	public void send(Fields fields) throws SystemException {

		Transaction transactionRequest = new Transaction();
		Transaction transactionResponse = new Transaction();

		transactionRequest = TransactionFactory.getInstance(fields);
		
		String request = converter.perpareRequest(fields, transactionRequest);
		String response = null;

		String txnType = fields.get(FieldType.TXNTYPE.getName());
		
		if (txnType.equals(TransactionType.SALE.getName()) || txnType.equals(TransactionType.ENROLL.getName())) {
			response = communicator.getUpiResponse(request, fields);
		}
		
		if (StringUtils.isNotBlank(response)) {
			if (response.contains(Constants.MSG)) {
				transactionResponse = converter.toUpiTransaction(response);
				BilldeskTransformer billdeskTransformer = new BilldeskTransformer(transactionResponse);
				billdeskTransformer.updateUpiResponse(fields,transactionResponse);
				
			} 

		} else {
			BilldeskTransformer billdeskTransformer = new BilldeskTransformer(transactionResponse);
			billdeskTransformer.updateResponse(fields);
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

}
