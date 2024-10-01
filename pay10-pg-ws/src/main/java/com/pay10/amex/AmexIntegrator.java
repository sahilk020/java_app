package com.pay10.amex;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.pay10.commons.exception.SystemException;
import com.pay10.commons.util.FieldType;
import com.pay10.commons.util.Fields;
import com.pay10.commons.util.TransactionType;
import com.pay10.migs.MigsTransformer;
import com.pay10.migs.Transaction;
import com.pay10.migs.TransactionCommunicator;
import com.pay10.migs.TransactionConverter;
import com.pay10.pg.core.util.CryptoManager;
import com.pay10.pg.core.util.CryptoManagerFactory;
import com.pay10.pg.core.util.TransactionProcessor;

/**
 * @author Sunil
 *
 */
@Service
public class AmexIntegrator {

	@Autowired
	@Qualifier("migsSale")
	TransactionProcessor transactionProcessor;
	
	@Autowired
	@Qualifier("migsTransactionCommunicator")
	private TransactionCommunicator communicator;
	
	private CryptoManager cryptoManager = CryptoManagerFactory.getCryptoManager();

	@Autowired
	@Qualifier("migsTransactionConverter")
	private TransactionConverter transactionConverter;

	@Autowired
	@Qualifier("migsTransformer")
	private MigsTransformer migsTransformer;

	public void process(Fields fields) throws SystemException {                           
		send(fields);
		cryptoManager.secure(fields);
	}// process

	public void send(Fields fields) throws SystemException {

		// TODO.... make processor for refund and capture also
		if (fields.get(FieldType.TXNTYPE.getName()).equals(TransactionType.ENROLL.getName())) {
			transactionProcessor.transact(fields);
			return;
		}
		String request = transactionConverter.getRequest(fields);

		String responseString = communicator.getResponseString(request, fields);

		Transaction response = transactionConverter.getResponse(responseString);

		migsTransformer.updateResponse(response, fields);
	}

	public TransactionCommunicator getCommunicator() {
		return communicator;
	}

	public void setCommunicator(TransactionCommunicator communicator) {
		this.communicator = communicator;
	}
}
