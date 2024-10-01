package com.pay10.pg.history;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.pay10.commons.exception.ErrorType;
import com.pay10.commons.exception.SystemException;
import com.pay10.commons.util.FieldType;
import com.pay10.commons.util.Fields;
import com.pay10.commons.util.StatusType;
import com.pay10.commons.util.TransactionType;
import com.pay10.pg.core.util.Processor;

@Service("historyProcessor")
public class HistoryProcessor implements Processor {

	@Autowired
	private Historian historian;

	@Override
	public void preProcess(Fields fields) throws SystemException {
		historian.findPrevious(fields);
	}// preProcess()

	@Override
	public void process(Fields fields) throws SystemException {
		historian.populateFieldsFromPrevious(fields);

		historian.validateSupportTransaction(fields);

		// Check duplicate authorization transaction
		//historian.detectDuplicate(fields);
	}

	@Override
	public void postProcess(Fields fields) {
		if (fields.get(FieldType.TXNTYPE.getName()).equals(TransactionType.NEWORDER.getName())
				&& fields.get(FieldType.STATUS.getName()).equals(StatusType.PENDING.getName())) {
			String responseCode = fields.get(FieldType.RESPONSE_CODE.getName());
			if (null == responseCode) {
				fields.put(FieldType.RESPONSE_CODE.getName(), ErrorType.SUCCESS.getCode());
				fields.put(FieldType.RESPONSE_MESSAGE.getName(), ErrorType.SUCCESS.getResponseMessage());
			}
		}
		
		// Change Response code to 026 when Status is Pending or Sent to bank or Enrolled
		
		if ((fields.get(FieldType.STATUS.getName()).equals(StatusType.PENDING.getName()) ||
				fields.get(FieldType.STATUS.getName()).equals(StatusType.SENT_TO_BANK.getName()) ||
				fields.get(FieldType.STATUS.getName()).equals(StatusType.ENROLLED.getName())) &&
				fields.get(FieldType.INTERNAL_ORIG_TXN_TYPE.getName()).equals(TransactionType.STATUS.getName()) ){
			
			fields.put(FieldType.RESPONSE_CODE.getName(), ErrorType.PENDING.getCode());
		}
				
	}
}
