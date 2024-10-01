package com.pay10.batch.refundTicketing;

import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pay10.batch.BatchTicketingProcessor;
import com.pay10.batch.commons.Fields;
import com.pay10.batch.exception.SystemException;

public class RefundTicketingBatchProcessor extends BatchTicketingProcessor {

private static final Logger logger = LoggerFactory.getLogger(RefundTicketingBatchProcessor.class);
	
	public Fields process(Fields fields) throws Exception {
        logger.info("Validating refundTicketing fields on file processor: " + fields.getFieldsAsString());
        
		return super.process(fields);
	}

	/*Set required fields for RECO Transaction*/
	public JSONObject createNewRefundObject(Fields fields) throws JSONException, SystemException, com.pay10.commons.exception.SystemException {
		JSONObject value = super.createNewRefundIrctcObject(fields);
		
		return value;
	}
}
