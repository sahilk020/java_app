package com.pay10.batch.recoSettle;

import java.util.Map;

import com.pay10.batch.BatchUpiSettleProcessor;
import com.pay10.batch.commons.Fields;
import com.pay10.batch.commons.util.Amount;
import com.pay10.batch.commons.util.GeneralValidator;
import com.pay10.batch.exception.DatabaseException;
import com.pay10.batch.exception.SystemException;
import com.pay10.commons.util.FieldType;
import com.pay10.commons.util.StatusType;
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
public class UpiSettleRecoBatchProcessor extends BatchUpiSettleProcessor {
	
	@Autowired
	private Amount amount;
	
	public void setFileValidator(UpiSettleRecoFileValidator fileValidator) {
		super.setFileSettleValidator(fileValidator);
	}

	private static final Logger logger = LoggerFactory.getLogger(UpiSettleRecoBatchProcessor.class);
	
	/* Process the {@link Fields} item
	 * performing validation using {@link GeneralValidator} 
	 */
	public Fields process(Fields fields) throws DatabaseException {
		logger.info("Validating reco fields on file processor: " + fields.getFieldsAsString());
		
		return super.process(fields);
	}

	 public Map<String, String> createNewRecoObject(Fields fields) throws SystemException
	  {
		 Fields previous = fields.getPrevious();
	    Map<String, String> value = super.createNewRecoObject(fields);
	    value.put(FieldType.RECO_TXNTYPE.getName(), TransactionType.RECO.getName());
	    // Done By chetan nagaria for change in settlement process to mark transaction as RNS
//	    value.put(FieldType.STATUS.getName(), StatusType.SETTLED.getName());
	    try {
			if (!isStatusMatched(fields, StatusType.CAPTURED.getName())) {
				 System.out.println("inside createNewRecoObject "+StatusType.FORCE_CAPTURED.getName());
				value.put(FieldType.STATUS.getName(), StatusType.FORCE_CAPTURED.getName());
			}else
			value.put(FieldType.STATUS.getName(), StatusType.SETTLED_RECONCILLED.getName());
		} catch (DatabaseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	    
	    /*if(Boolean.parseBoolean(fields.get(FieldType.IS_SALE_CAPTURED.getName()))== false) {
	    	value.put(FieldType.IS_SALE_CAPTURED.getName(), fields.get(FieldType.IS_SALE_CAPTURED.getName()));
	    }*/
	    
	    if(Boolean.parseBoolean(fields.get(FieldType.IS_RECO_PENDING.getName()))== true) {
	    	value.put(FieldType.IS_RECO_PENDING.getName(), fields.get(FieldType.IS_RECO_PENDING.getName()));
	    } else if(Boolean.parseBoolean(fields.get(FieldType.IS_ENROLL_ENROLLED.getName()))== true) {
	    	value.put(FieldType.IS_ENROLL_ENROLLED.getName(), fields.get(FieldType.IS_ENROLL_ENROLLED.getName()));
	    } else if(Boolean.parseBoolean(fields.get(FieldType.IS_SALE_TIMEOUT.getName()))== true) {
	    	value.put(FieldType.IS_SALE_TIMEOUT.getName(), fields.get(FieldType.IS_SALE_TIMEOUT.getName()));
	    } else if(Boolean.parseBoolean(fields.get(FieldType.IS_SALE_SENT_TO_BANK.getName()))== true) {
	    	value.put(FieldType.IS_SALE_SENT_TO_BANK.getName(), fields.get(FieldType.IS_SALE_SENT_TO_BANK.getName()));
	    } 
	    
	    /*if(Boolean.parseBoolean(fields.get(FieldType.IS_REFUND_PENDING.getName()))== true) {
	    	value.put(FieldType.IS_REFUND_PENDING.getName(), fields.get(FieldType.IS_REFUND_PENDING.getName()));
	    }*/
	    
	    return value;
	  }
	 public Map<String, String> createNewRefundRecoObject(Fields fields) throws SystemException
	  { 
		 //Fields previous = fields.getPrevious();
	    Map<String, String> value = super.createNewRefundRecoObject(fields);
	    value.put(FieldType.RECO_TXNTYPE.getName(), TransactionType.REFUNDRECO.getName());
	 // Done By chetan nagaria for change in settlement process to mark transaction as RNS
//		value.put(FieldType.STATUS.getName(), StatusType.SETTLED.getName());
		value.put(FieldType.STATUS.getName(), StatusType.SETTLED_RECONCILLED.getName());
		if(Boolean.parseBoolean(fields.get(FieldType.IS_REFUND_CAPTURED.getName()))== true) {
	    	value.put(FieldType.IS_REFUND_CAPTURED.getName(), fields.get(FieldType.IS_REFUND_CAPTURED.getName()));
	    }  else if(Boolean.parseBoolean(fields.get(FieldType.IS_REFUND_TIMEOUT.getName()))== true) {
	    	value.put(FieldType.IS_REFUND_TIMEOUT.getName(), fields.get(FieldType.IS_REFUND_TIMEOUT.getName()));
	    } 
	    return value;
	  }
	/*Set required fields for RECO REPORTING Transaction*/
	 public Map<String, String> createNewRefundRecoReportingObject(Fields fields) throws SystemException {
			Map<String, String> value = super.createNewRefundRecoReportingObject(fields);
			value.put(FieldType.RECO_TXNTYPE.getName(), TransactionType.REFUNDRECO.getName());
			return value;
		}
	public Map<String, String> createNewRecoReportingObject(Fields fields) throws SystemException {
		Map<String, String> value = super.createNewRecoReportingObject(fields);
		value.put(FieldType.RECO_TXNTYPE.getName(), TransactionType.RECO.getName());
		return value;
	}
}