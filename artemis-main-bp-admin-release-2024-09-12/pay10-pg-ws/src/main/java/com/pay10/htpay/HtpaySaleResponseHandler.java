package com.pay10.htpay;


import java.io.IOException;
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
import com.pay10.errormapping.ErrorMappingService;
import com.pay10.pg.core.util.ProcessManager;
import com.pay10.pg.core.util.Processor;

@Service
public class HtpaySaleResponseHandler {
	private static Logger logger = LoggerFactory.getLogger(HtpaySaleResponseHandler.class.getName());

	@Autowired
	private Validator generalValidator;

	@Autowired
	@Qualifier("updateProcessor")
	private Processor updateProcessor;

	@Autowired
	private ErrorMappingService errorMappingService;

	public Map<String, String> process(Fields fields) throws SystemException, IOException {
		String newTxnId = TransactionManager.getNewTransactionId();
		logger.info("HTPAY  UPI Response Handler " + fields.getFieldsAsString());
		fields.put(FieldType.TXN_ID.getName(), newTxnId);
		String response = fields.get(FieldType.HTPAY_RESPONSE_FIELD.getName());
		Transaction transactionResponse = getTranscationFields(fields.get(FieldType.HTPAY_RESPONSE_FIELD.getName()));

		Htpaytransformer htpaytransformer = new Htpaytransformer(transactionResponse);
		htpaytransformer.updateResponse(fields);

		fields.put(FieldType.INTERNAL_ORIG_TXN_TYPE.getName(), fields.get(FieldType.TXNTYPE.getName()));
		fields.remove(FieldType.HTPAY_RESPONSE_FIELD.getName());

		ProcessManager.flow(updateProcessor, fields, true);
		return fields.getFields();

	}


	private Transaction getTranscationFields(String encData) {
		Transaction transaction = new Transaction();
		for (String fields : encData.split("&")) {
			if (fields.contains("pforderno")) {
				transaction.setPforderno( fields.split("=")[1]);
			}
			if (fields.contains("mhtorderno")) {
				transaction.setMhtorderno( fields.split("=")[1]);
			}
			if (fields.contains("status")) {
				transaction.setStatus( fields.split("=")[1]);
			}
		}
		return transaction;
	}

}
