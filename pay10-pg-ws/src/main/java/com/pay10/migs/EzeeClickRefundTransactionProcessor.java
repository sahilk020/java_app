package com.pay10.migs;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.pay10.commons.exception.SystemException;
import com.pay10.commons.util.Fields;
import com.pay10.pg.core.util.TransactionProcessor;

@Service
public class EzeeClickRefundTransactionProcessor implements TransactionProcessor{
	@Autowired
	@Qualifier("migsTransactionConverter")
	private TransactionConverter transactionConverter;
	@Autowired
	@Qualifier("migsTransactionCommunicator")
	private TransactionCommunicator communicator;
	
	@Autowired
	@Qualifier("migsTransformer")
	private MigsTransformer migsTransformer;
	
	@Override
	public void transact(Fields fields) throws SystemException {
	
		String request = transactionConverter.getRefundRequest(fields);
		String responseString = communicator.getResponseString(request, fields);

		Transaction response = transactionConverter.getResponse(responseString);
		migsTransformer.updateResponse(response, fields);
	}
}
