package com.pay10.kotak.upi;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.pay10.commons.exception.SystemException;
import com.pay10.commons.util.FieldType;
import com.pay10.commons.util.Fields;
import com.pay10.commons.util.TransactionManager;
import com.pay10.commons.util.Validator;
import com.pay10.pg.core.util.ProcessManager;
import com.pay10.pg.core.util.Processor;

@Service
public class KotakUpiSaleResponseHandler {
private static Logger logger = LoggerFactory.getLogger(KotakUpiSaleResponseHandler.class.getName());
	
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
						
		ProcessManager.flow(updateProcessor, fields, true);
		return fields.getFields();
	}


}
