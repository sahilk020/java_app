package com.pay10.pg.core.util;

import org.springframework.stereotype.Component;

import com.pay10.commons.exception.SystemException;
import com.pay10.commons.util.Fields;
import com.pay10.pg.core.util.TransactionProcessor;

@Component
public interface AbstractTransactionProcessorFactory {
	
	public TransactionProcessor getInstance(Fields fields) throws SystemException;

}
