package com.pay10.batch.reco;

import java.util.Map;

import com.pay10.batch.BatchProcessor;
import com.pay10.batch.commons.Fields;
import com.pay10.batch.commons.util.Amount;
import com.pay10.batch.commons.util.GeneralValidator;
import com.pay10.batch.exception.DatabaseException;
import com.pay10.batch.exception.SystemException;
import com.pay10.commons.util.FieldType;
import com.pay10.commons.util.TransactionType;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

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
public class RecoBatchProcessor extends BatchProcessor {
	
	@Autowired
	private Amount amount;
	
	public void setFileValidator(RecoFileValidator fileValidator) {
		super.setFileValidator(fileValidator);
	}

	private static final Logger logger = LoggerFactory.getLogger(RecoBatchProcessor.class);
	
	/* Process the {@link Fields} item
	 * performing validation using {@link GeneralValidator} 
	 */
	public Fields process(Fields fields) throws SystemException, DatabaseException {
		logger.info("Validating reco fields on file processor: " + fields.getFieldsAsString());
		
		return super.process(fields);
	}

	/*Set required fields for RECO Transaction
	 public Map<String, String> createNewRecoObject(Fields fields) throws DatabaseException
	  {
		//Fields previous = fields.getPrevious();
	    Map<String, String> value = super.createNewRecoObject(fields);
	    value.put(FieldType.RECO_TXNTYPE.getName(), TransactionType.RECO.getName());
		value.put(FieldType.STATUS.getName(), StatusType.RECONCILED.getName());
	    
	    return value;
	  }*/
	 
	 /*Set required fields for RECO Transaction*/
	 /*public Map<String, String> createNewRecoPendingObject(Fields fields) throws DatabaseException
	  {
		Fields previous = fields.getPrevious();
	    Map<String, String> value = super.createNewRecoPendingObject(fields);
	    value.put(FieldType.RECO_TXNTYPE.getName(), TransactionType.RECO.getName());
	    value.put(FieldType.STATUS.getName(), StatusType.PENDING.getName());	    
	    return value;
	  }*/
	 
	/*Set required fields for RECO REPORTING Transaction*/
	public Map<String, String> createNewRecoReportingObject(Fields fields) {
		Map<String, String> value = super.createNewRecoReportingObject(fields);
		value.put(FieldType.RECO_TXNTYPE.getName(), TransactionType.RECO.getName());
		return value;
	}
}