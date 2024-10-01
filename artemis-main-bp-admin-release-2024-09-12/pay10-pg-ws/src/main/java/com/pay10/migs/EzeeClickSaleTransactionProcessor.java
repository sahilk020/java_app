package com.pay10.migs;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.pay10.commons.exception.ErrorType;
import com.pay10.commons.exception.SystemException;
import com.pay10.commons.util.FieldType;
import com.pay10.commons.util.Fields;
import com.pay10.commons.util.StatusType;
import com.pay10.pg.core.util.TransactionProcessor;

/**
 * @author Puneet
 *
 */
@Service
public class EzeeClickSaleTransactionProcessor implements TransactionProcessor{
	@Autowired
	@Qualifier("migsTransactionConverter")
	TransactionConverter transactionConverter;
	@Autowired
	@Qualifier("migsTransactionCommunicator")
	TransactionCommunicator communicator;

	@Override
	public void transact(Fields fields) throws SystemException {
		String request = transactionConverter.getEzeeClickRequest(fields);
		// TODO... after request preparation of request redirection will be done via request action so this code is commented for now   
	//	communicator.sendEnrollTransaction(fields, request, "");
		fields.put(FieldType.STATUS.getName(),StatusType.SENT_TO_BANK.getName());
		fields.put(FieldType.RESPONSE_CODE.getName(),ErrorType.SUCCESS.getResponseCode());
		fields.put(FieldType.RESPONSE_MESSAGE.getName(),ErrorType.SUCCESS.getResponseMessage());
	}
}
