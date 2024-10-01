package com.pay10.requestrouter;

import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.pay10.commons.util.FieldType;
import com.pay10.commons.util.Fields;
import com.pay10.pg.core.util.ProcessManager;
import com.pay10.pg.core.util.Processor;
import com.pay10.pg.core.util.ResponseCreator;

@Service
public class PayoutRouter {
	private static final Logger logger = LoggerFactory.getLogger(PayoutRouter.class);
	@Autowired
	@Qualifier("PayoutUpdateProcessor")
	private Processor payoutUpdateProcessor;

	@Autowired
	private ResponseCreator responseCreator;

	@Autowired
	@Qualifier("PayoutSecurityProcessor")
	private Processor payoutSecurityProcessor;
	
	@Autowired
	@Qualifier("payoutFRM")
	private Processor payoutFRM;

	@Autowired
	@Qualifier("paytenPayoutProcessor")
	private Processor pay10PayoutProcessor;
	
	@Autowired
	@Qualifier("htpayPayoutProcessor")
	private Processor htpayPayoutProcessor;
	@Autowired
	@Qualifier("quomoPayoutProcess")
	private Processor quomoPayoutProcess;

	@Autowired
	@Qualifier("payouthistoryProcessor")
	private Processor payouthistoryProcessor;

	public Map<String, String> route(Fields fields) {

		ProcessManager.flow(payoutSecurityProcessor, fields, false);

		ProcessManager.flow(payouthistoryProcessor, fields, false);
		logger.info("Before payoutFRM call service ... " + fields.getFieldsAsString());
		ProcessManager.flow(payoutFRM, fields, false);
		logger.info("After payoutFRM call service ... " + fields.getFieldsAsString());
		
		ProcessManager.flow(quomoPayoutProcess, fields, false);

		ProcessManager.flow(pay10PayoutProcessor, fields, false);
		ProcessManager.flow(htpayPayoutProcessor, fields, false);

		ProcessManager.flow(payoutUpdateProcessor, fields, true);

		createResponse(fields);

		return fields.getFields();
	}

	public void createResponse(Fields fields) {
		responseCreator.create(fields);
	}
}
