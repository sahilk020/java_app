package com.pay10.pg.core.util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.pay10.commons.exception.SystemException;
import com.pay10.commons.util.Fields;

@Service("responseProcessor")
public class ResponseProcessor implements Processor {

	@Autowired
	private TransactionResponser transactionResponser;

	@Override
	public void preProcess(Fields fields) {
	}

	@Override
	public void process(Fields fields) throws SystemException {

		boolean emitra = fields.contains("IS_EMITRA");
		if (emitra) {
			fields.remove("IS_EMITRA");
			transactionResponser.getResponse(fields, true);
			return;
		}
		transactionResponser.getResponse(fields);

	}

	@Override
	public void postProcess(Fields fields) {
		// fields.logAllFields("Sending Response to Client");
		fields.logAllFieldsUsingMasking("Sending Response to Client");
	}
}
