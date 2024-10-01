package com.pay10.icici.netbanking;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.pay10.commons.exception.SystemException;
import com.pay10.commons.util.FieldType;
import com.pay10.commons.util.Fields;
import com.pay10.commons.util.TransactionType;

@Service
public class IciciNBIntegrator {

	@Autowired
	@Qualifier("iciciBankNBTransactionConverter")
	private TransactionConverter converter;

	@Autowired
	@Qualifier("iciciBankNBTransactionCommunicator")
	private TransactionCommunicator communicator;

	@Autowired
	@Qualifier("iciciBankNBTransactionFactory")
	private TransactionFactory transactionFactory;

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
			String response = communicator.transactStatus(request);
			Map<String, String> trackingResponseMap = converter.parseRefundTrackingTxnResponse(response);
			IciciNBTransformer iciciNBTransformer = new IciciNBTransformer(transactionResponse);
			iciciNBTransformer.updateResponse(fields, trackingResponseMap);
		}

	}

}
