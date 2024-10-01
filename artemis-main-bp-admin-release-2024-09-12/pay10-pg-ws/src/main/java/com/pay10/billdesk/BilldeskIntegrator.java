package com.pay10.billdesk;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.pay10.commons.exception.SystemException;
import com.pay10.commons.util.FieldType;
import com.pay10.commons.util.Fields;
import com.pay10.commons.util.TransactionType;

@Service
public class BilldeskIntegrator {

	@Autowired
	@Qualifier("billdeskTransactionConverter")
	private TransactionConverter converter;

	@Autowired
	@Qualifier("billdeskTransactionCommunicator")
	private TransactionCommunicator communicator;

	@Autowired
	private TransactionFactory TransactionFactory;

	private BilldeskTransformer billdeskTransformer = null;

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
			String response = communicator.getResponse(request, fields);

			if (fields.get(FieldType.TXNTYPE.getName()).equalsIgnoreCase(TransactionType.SALE.getName())) {

				transactionResponse = converter.toTransaction(response);
				billdeskTransformer = new BilldeskTransformer(transactionResponse);
				billdeskTransformer.updateResponse(fields);
			}

			if (fields.get(FieldType.TXNTYPE.getName()).equalsIgnoreCase(TransactionType.REFUND.getName())) {

				transactionResponse = converter.toTransactionRefund(response);
				billdeskTransformer = new BilldeskTransformer(transactionResponse);
				billdeskTransformer.updateRefundResponse(fields);
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

	public BilldeskTransformer getKotakTransformer() {
		return billdeskTransformer;
	}

	public void setKotakTransformer(BilldeskTransformer kotakTransformer) {
		this.billdeskTransformer = kotakTransformer;
	}

}
