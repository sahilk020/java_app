package com.pay10.kotak.nb;

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
public class KotakNBIntegrator {
	private static Logger logger = LoggerFactory.getLogger(KotakNBIntegrator.class.getName());
	@Autowired
	@Qualifier("kotakNBTransactionConverter")
	private TransactionConverter converter;

	@Autowired
	@Qualifier("kotakNBFactory")
	private TransactionFactory transactionFactory;

	@Autowired
	private KotakTransformernb kotakTransformer;

	@Autowired
	@Qualifier("kotakNBTransactionCommunicator")
	private TransactionCommunicator communicator;

	public void process(Fields fields) throws SystemException {

		send(fields);
	}

	public void send(Fields fields) throws SystemException {

		if (fields.get(FieldType.TXNTYPE.getName()).equalsIgnoreCase(TransactionType.ENROLL.getName())) {
            fields.put(FieldType.TXNTYPE.getName(),TransactionType.SALE.getName());
        }
		logger.info("fields " + fields.getFieldsAsString());
		Transaction transactionRequest = new Transaction();
		Transaction transactionResponse = new Transaction();

		transactionRequest = transactionFactory.getInstance(fields);

		String request = converter.perpareRequest(fields, transactionRequest);
		

		String txnType = fields.get(FieldType.TXNTYPE.getName());
		if (txnType.equals(TransactionType.SALE.getName())) {
			
			communicator.updateSaleResponse(fields, request);
		} 
			else {



			
				String response = communicator.getrefundresponse(request, fields);
				transactionResponse = converter.toTransactionRefund(response, fields);
				kotakTransformer = new KotakTransformernb(transactionResponse);

				if (fields.get(FieldType.TXNTYPE.getName()).equalsIgnoreCase(TransactionType.REFUND.getName())) {
					kotakTransformer.updateRefundResponse(fields);

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

	public com.pay10.kotak.nb.KotakTransformernb getCashfreeTransformer() {
		return kotakTransformer;
	}

	public void setCashfreeTransformer(com.pay10.kotak.nb.KotakTransformernb kotakTransformer) {
		this.kotakTransformer = kotakTransformer;
	}

}

