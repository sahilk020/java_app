package com.pay10.ipay;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.pay10.commons.exception.SystemException;
import com.pay10.commons.util.Fields;
import com.pay10.pg.core.util.AbstractTransactionProcessorFactory;
import com.pay10.pg.core.util.TransactionProcessor;

@Service
public class IPayIntegrator {
	
	@Autowired
	@Qualifier("iPayTxnProcessor")
	private AbstractTransactionProcessorFactory ipayTransactionFactory;
	
	public void process(Fields fields) throws SystemException {
		
		TransactionProcessor ipayProcessor = ipayTransactionFactory.getInstance(fields);
		ipayProcessor.transact(fields);
	}

}
