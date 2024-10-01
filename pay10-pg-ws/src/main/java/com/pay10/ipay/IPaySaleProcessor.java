package com.pay10.ipay;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.pay10.commons.exception.SystemException;
import com.pay10.commons.util.Fields;
import com.pay10.pg.core.util.TransactionProcessor;

@Service
public class IPaySaleProcessor implements TransactionProcessor {
	
	private static Logger logger = LoggerFactory.getLogger(IPaySaleProcessor.class.getName());
	
	@Autowired
	@Qualifier("iPayTransactionConverter")
	private TransactionConverter converter;

	@Override
	public void transact(Fields fields) throws SystemException {
		logger.info("Generating sale transaction for IPay Net Banking.");
		converter.createSaleTransaction(fields);
	}

}
