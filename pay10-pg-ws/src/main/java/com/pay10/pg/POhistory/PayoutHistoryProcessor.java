package com.pay10.pg.POhistory;

import org.owasp.esapi.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.pay10.commons.exception.ErrorType;
import com.pay10.commons.exception.SystemException;
import com.pay10.commons.util.FieldType;
import com.pay10.commons.util.Fields;
import com.pay10.commons.util.StatusType;
import com.pay10.commons.util.TransactionType;
import com.pay10.pg.core.util.Processor;

@Service("payouthistoryProcessor")
public class PayoutHistoryProcessor implements Processor {

	@Autowired
	private PayoutHistorian historian;

	@Override
	public void preProcess(Fields fields) throws SystemException {
		historian.findPreviousPayout(fields);
	}// preProcess()

	@Override
	public void process(Fields fields) throws SystemException {
		System.out.println("Enter History Processor : "+fields.getFieldsAsString());
		historian.populateFieldsFromPreviousPayout(fields);

		historian.validateSupportTransaction(fields);
		System.out.println("Exit History Processor : "+fields.getFieldsAsString());
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
		
		if ((fields.get(FieldType.STATUS.getName()).equals("REQUEST ACCEPTED"))){
			
			fields.put(FieldType.RESPONSE_CODE.getName(), ErrorType.PENDING.getCode());
		}
				
	}
}
