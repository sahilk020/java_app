package com.pay10.idbi;

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
public class IdbiSaleResponseHandler {
	private static Logger logger = LoggerFactory.getLogger(IdbiSaleResponseHandler.class.getName());

	@Autowired
	private Validator generalValidator;

	@Autowired
	@Qualifier("updateProcessor")
	private Processor updateProcessor;

	@Autowired
	private IdbiTransformer idbiTransformer;

	public Map<String, String> process(Fields fields) throws SystemException {
		String newTxnId = TransactionManager.getNewTransactionId();
		fields.put(FieldType.TXN_ID.getName(), newTxnId);
		generalValidator.validate(fields);
		Transaction transactionResponse = new Transaction();
		idbiTransformer = new IdbiTransformer(transactionResponse);
		idbiTransformer.updateResponse(fields);

		fields.put(FieldType.INTERNAL_ORIG_TXN_TYPE.getName(), fields.get(FieldType.TXNTYPE.getName()));
		fields.remove(FieldType.IDBI_RESPONSE_FIELD.getName());
		ProcessManager.flow(updateProcessor, fields, true);
		return fields.getFields();

	}


	
}
