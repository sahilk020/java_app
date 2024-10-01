package com.pay10.batch.refundIrctc;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.pay10.batch.BatchIrctcProcessor;
import com.pay10.batch.commons.Fields;
import com.pay10.batch.commons.util.Amount;
import com.pay10.batch.commons.util.GeneralValidator;
import com.pay10.batch.exception.DatabaseException;
import com.pay10.batch.exception.SystemException;

import org.json.JSONException;
import org.json.JSONObject;

/** Process the provided item {@link Fields} object, 
 * Step1: performing validation using {@link GeneralValidator}.
 * Step2: calling history processor to get previous data using TXN_ID.
 * Step3: validating input data to previous data.
 * Step3: if valid, setting previous data to {@link Fields} object.
 * Step4: returning modified {@link Fields} object.
 * 
 * @param {@link Fields} object to be processed.
 * @return modified {@link Fields} object for continued processing.
 */
public class RefundIrctcBatchProcessor extends BatchIrctcProcessor {
	
private static final Logger logger = LoggerFactory.getLogger(RefundIrctcBatchProcessor.class);
	
	@Autowired
	private Amount amount;
	
	/*public void setFileValidator(RefundFileValidator fileValidator) {
		super.setFileValidator(fileValidator);
	}*/

	/* Process the {@link Fields} item
	 * performing validation using {@link GeneralValidator} 
	 */
	public Fields process(Fields fields) throws SystemException, DatabaseException, com.pay10.commons.exception.SystemException {
        logger.info("Validating refundIrctc fields on file processor: " + fields.getFieldsAsString());
        
		return super.process(fields);
	}

	/*Set required fields for RECO Transaction*/
	public JSONObject createNewRefundObject(Fields fields) throws JSONException, SystemException, com.pay10.commons.exception.SystemException {
		JSONObject value = super.createNewRefundIrctcObject(fields);
		
		return value;
	}
}
