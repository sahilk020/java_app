package com.pay10.migs;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.pay10.commons.exception.SystemException;
import com.pay10.commons.util.Fields;
import com.pay10.pg.core.util.AbstractTransactionProcessorFactory;
import com.pay10.pg.core.util.CryptoManager;
import com.pay10.pg.core.util.CryptoManagerFactory;
import com.pay10.pg.core.util.TransactionProcessor;

/**
 * @author Puneet
 *
 */
@Service
public class EzeeClickIntegrator {
	
	@Autowired
	@Qualifier("ezeeClick")
	private AbstractTransactionProcessorFactory transactionProcessorFactory;
	private CryptoManager cryptoManager = CryptoManagerFactory.getCryptoManager();

	public void process(Fields fields) throws SystemException{

		addDefaultFields(fields);

		send(fields);

		cryptoManager.secure(fields);
	}

	public void send(Fields fields) throws SystemException {

		TransactionProcessor transactionProcessor = transactionProcessorFactory.getInstance(fields);
		transactionProcessor.transact(fields);
	}

	public static void addDefaultFields(Fields fields) {

	}
}
