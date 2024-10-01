package com.pay10.cashfree;

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
public class CashfreeIntegrator {
	
	private static final Logger logger = LoggerFactory.getLogger(CashfreeIntegrator.class.getName());
	@Autowired
	@Qualifier("cashfreeTransactionConverter")
	private TransactionConverter converter;
	
	@Autowired
	@Qualifier("cashfreeTransactionCommunicator")
	private TransactionCommunicator communicator;
	
	@Autowired
	private TransactionFactory TransactionFactory;
	
	private CashfreeTransformer cashfreeTransformer = null;

	public void process(Fields fields) throws SystemException {

		send(fields);

	}//process
	
	public void send(Fields fields) throws SystemException {
		
		Transaction transactionRequest = new Transaction();
		Transaction transactionResponse = new Transaction();
		
		logger.info("Before Set fields for CashFree >>>>>>>>>> "+fields.getFieldsAsString());
		if (fields.get(FieldType.TXNTYPE.getName()).equalsIgnoreCase(TransactionType.ENROLL.getName())) {
			fields.put(FieldType.TXNTYPE.getName(),TransactionType.SALE.getName());
		}
		logger.info("After Set fields for CashFree >>>>>>>>>> "+fields.getFieldsAsString());
		
		transactionRequest = TransactionFactory.getInstance(fields);

		String request = converter.perpareRequest(fields, transactionRequest);

		logger.info("Request For Cashfree :: "+request);
		String txnType = fields.get(FieldType.TXNTYPE.getName());
		if (txnType.equals(TransactionType.SALE.getName())) {
			communicator.updateSaleResponse(fields, request);
		} else {
			String response = communicator.getResponse(request, fields);

			 transactionResponse = converter.toTransactionRefund(response);
			 cashfreeTransformer = new CashfreeTransformer(transactionResponse);
			 
			 if (fields.get(FieldType.TXNTYPE.getName()).equalsIgnoreCase(TransactionType.REFUND.getName())) {
				 cashfreeTransformer.updateRefundResponse(fields);
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

	public CashfreeTransformer getCashfreeTransformer() {
		return cashfreeTransformer;
	}

	public void setCashfreeTransformer(CashfreeTransformer cashfreeTransformer) {
		this.cashfreeTransformer = cashfreeTransformer;
	}

}
