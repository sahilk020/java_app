package com.pay10.sbi.netbanking;

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
public class SbiNBIntegrator {

	private static final Logger logger = LoggerFactory.getLogger(SbiNBIntegrator.class.getName());
	@Autowired
	@Qualifier("sbiNBTransactionConverter")
	private TransactionConverter converter;

	@Autowired
	@Qualifier("sbiNBTransactionCommunicator")
	private TransactionCommunicator communicator;

	@Autowired
	private TransactionFactory TransactionFactory;

	private SbiNBTransformer sbiTransformer = null;

	public void process(Fields fields) throws SystemException {

		send(fields);

	}// process

	public void send(Fields fields) throws SystemException {

		Transaction transactionRequest = new Transaction();
		Transaction transactionResponse = new Transaction();

		logger.info("Request Received at SbiNBIntegrator :: "+fields.getFieldsAsString());
		if (fields.get(FieldType.TXNTYPE.getName()).equalsIgnoreCase(TransactionType.ENROLL.getName())) {
			fields.put(FieldType.TXNTYPE.getName(),TransactionType.SALE.getName());
		}
				
		transactionRequest = TransactionFactory.getInstance(fields);

		String request = converter.perpareRequest(fields, transactionRequest);

		String txnType = fields.get(FieldType.TXNTYPE.getName());
		if (txnType.equals(TransactionType.SALE.getName())) {
			communicator.updateSaleResponse(fields, request);

		} else {
			String response = communicator.getResponse(request, fields);

			transactionResponse = converter.toTransaction(response);
			sbiTransformer = new SbiNBTransformer(transactionResponse);
			sbiTransformer.updateResponse(fields);
		}

	}

}
