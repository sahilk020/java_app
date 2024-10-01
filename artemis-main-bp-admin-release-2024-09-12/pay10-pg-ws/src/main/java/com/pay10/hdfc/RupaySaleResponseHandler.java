package com.pay10.hdfc;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.pay10.commons.exception.ErrorType;
import com.pay10.commons.exception.SystemException;
import com.pay10.commons.util.FieldType;
import com.pay10.commons.util.Fields;
import com.pay10.commons.util.StatusType;
import com.pay10.commons.util.TransactionManager;
import com.pay10.commons.util.Validator;
import com.pay10.pg.core.util.ProcessManager;
import com.pay10.pg.core.util.Processor;

@Service
public class RupaySaleResponseHandler {
	
	@Autowired
	private Validator generalValidator;

	@Autowired
	@Qualifier("updateProcessor")
	private Processor updateProcessor;
	
		
public Map<String, String> process(Fields fields) throws SystemException {
		
		String newTxnId = TransactionManager.getNewTransactionId();
		fields.put(FieldType.TXN_ID.getName(), newTxnId);
		generalValidator.validate(fields);
		fields.put(FieldType.INTERNAL_ORIG_TXN_TYPE.getName(), fields.get(FieldType.TXNTYPE.getName()));
		
		String pgStatus = fields.get(FieldType.PG_TXN_MESSAGE.getName()); 
		if(pgStatus.equalsIgnoreCase("CAPTURED")) {
			fields.put(FieldType.STATUS.getName(), StatusType.CAPTURED.getName());
			fields.put(FieldType.RESPONSE_CODE.getName(), ErrorType.SUCCESS.getResponseCode());
			fields.put(FieldType.RESPONSE_MESSAGE.getName(), ErrorType.SUCCESS.getResponseMessage());
		} else {
			fields.put(FieldType.STATUS.getName(), StatusType.FAILED.toString());
			fields.put(FieldType.RESPONSE_CODE.getName(), ErrorType.REJECTED.getResponseCode());
			fields.put(FieldType.RESPONSE_MESSAGE.getName(), ErrorType.REJECTED.getResponseMessage());
		}
		ProcessManager.flow(updateProcessor, fields, true);
		return fields.getFields();
	}

}
