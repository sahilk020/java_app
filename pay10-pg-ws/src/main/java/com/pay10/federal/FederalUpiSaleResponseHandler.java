package com.pay10.federal;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
import com.pay10.pg.history.Historian;

@Service
public class FederalUpiSaleResponseHandler {
	
	private static Logger logger = LoggerFactory.getLogger(FederalUpiSaleResponseHandler.class.getName());
	
	@Autowired
	private Validator generalValidator;
	
	@Autowired
	private Historian historian;
	
	@Autowired
	private FederalUpiMapper federalUpiMapper;
	
	@Autowired
	@Qualifier("updateProcessor")
	private Processor updateProcessor;
	
		
	public Map<String, String> process(Fields fields) throws SystemException {
		logger.info("inside saleResponse handler" );
		generalValidator.validate(fields);
		historian.findPrevious(fields);
		historian.populateFieldsFromPrevious(fields);
		
		fields.put(FieldType.INTERNAL_ORIG_TXN_TYPE.getName(), fields.get(FieldType.TXNTYPE.getName()));
		String upiResponseMsg = fields.get(FieldType.UPI_RESPONSE_MESSAGE.getName());
		String upiResponseCode  = fields.get(FieldType.UPI_RESPONSE_CODE.getName());
		
		fields.put(FieldType.PG_RESP_CODE.getName(), upiResponseCode);
		fields.put(FieldType.PG_TXN_MESSAGE.getName(), upiResponseMsg);
		
		StatusType status = federalUpiMapper.getStatusType(upiResponseCode);
		ErrorType errorMsg = federalUpiMapper.getErrorType(upiResponseCode);
		
		fields.put(FieldType.STATUS.getName(), status.toString());
		fields.put(FieldType.RESPONSE_MESSAGE.getName(), errorMsg.toString());
		fields.put(FieldType.RESPONSE_CODE.getName(), errorMsg.toString());
		String newTxnId = TransactionManager.getNewTransactionId();
		fields.put(FieldType.TXN_ID.getName(), newTxnId);
		logger.info("after status " + fields.getFieldsAsString());
		ProcessManager.flow(updateProcessor, fields, true);
		logger.info("after update txn ");
		return fields.getFields();
	}

}
