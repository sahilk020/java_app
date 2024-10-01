package com.pay10.migs;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.pay10.commons.exception.SystemException;
import com.pay10.commons.util.ConfigurationConstants;
import com.pay10.commons.util.Fields;
import com.pay10.pg.core.util.TransactionProcessor;
@Service
public class EzeeClickStatusTransactionProcessor implements TransactionProcessor{

	@Autowired
	@Qualifier("migsTransactionConverter")
	TransactionConverter transactionConverter;
	@Autowired
	@Qualifier("migsTransactionCommunicator")
	TransactionCommunicator communicator;

	@Override
	public void transact(Fields fields) throws SystemException {
		String request = transactionConverter.getEzeeClickStatusRequest(fields);
		String url = ConfigurationConstants.AMEX_EZEE_CLICK_STATUS_TXN_URL.getValue();
		String responseString = null;
		try {
			responseString = communicator.executePost(url,request);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	//	Transaction response = transactionConverter.getResponse(responseString);
	//	AmexTransformer amexTransformer = new AmexTransformer(response);
	//	amexTransformer.updateResponse(fields);
	}
}
