package com.pay10.isgpay;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.pay10.commons.exception.SystemException;
import com.pay10.commons.util.FieldType;
import com.pay10.commons.util.Fields;
import com.pay10.commons.util.PropertiesManager;
import com.pay10.commons.util.TransactionType;

@Service
public class ISGPayIntegrator {

	@Autowired
	@Qualifier("iSGPayTransactionConverter")
	private TransactionConverter converter;

	@Autowired
	@Qualifier("iSGPayTransactionCommunicator")
	private TransactionCommunicator communicator;

	@Autowired
	private TransactionFactory transactionFactory;
	
	private ISGPayTransformer iSGPayTransformer = null;

	public void process(Fields fields) throws SystemException {

		send(fields);

	}// process

	public void send(Fields fields) throws SystemException {

		// For Rupay Transactions, separate Details are present
		
		String rupayFlag = PropertiesManager.propertiesMap.get(Constants.SELECT_MID_FOR_RUPAY);
		
		if (fields.get(FieldType.TXNTYPE.getName()).equalsIgnoreCase(TransactionType.ENROLL.getName())) {
			fields.put(FieldType.TXNTYPE.getName(),TransactionType.SALE.getName());
		}
		
		Transaction transactionRequest = new Transaction();
		Transaction transactionResponse = new Transaction();

		transactionRequest = transactionFactory.getInstance(fields,rupayFlag);

		String request = converter.perpareRequest(fields, transactionRequest);

		String txnType = fields.get(FieldType.TXNTYPE.getName());
		if (txnType.equals(TransactionType.SALE.getName())) {
			communicator.updateSaleResponse(fields, request);
		} else {
			String response = communicator.getResponse(request, fields);

			 transactionResponse = converter.toTransaction(response,fields.get(FieldType.TXNTYPE.getName()));
			 iSGPayTransformer = new ISGPayTransformer(transactionResponse);
			 iSGPayTransformer.updateResponse(fields);
		}

	}

}
