package com.pay10.batch.recoSettle;

import java.util.Map;

import com.pay10.batch.BatchSettleProcessor;
import com.pay10.batch.FileSettleValidator;
import com.pay10.batch.commons.Fields;
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
public class SettleRecoBatchProcessor extends BatchSettleProcessor   {
	@Autowired
	FileSettleValidator fileSettleValidator;
		
	public void setFileValidator(SettleRecoFileValidator fileValidator) {
		super.setFileSettleValidator(fileValidator);
	}

	private static final Logger logger = LoggerFactory.getLogger(SettleRecoBatchProcessor.class);
	
	/* Process the {@link Fields} item
	 * performing validation using {@link GeneralValidator} 
	 */
	public Fields process(Fields fields) throws SystemException, DatabaseException {
		logger.info("Validating reco fields on file processor: " + fields.getFieldsAsString());
		
		return super.process(fields);
		
	}

	 public Map<String, String> createNewRecoObject(Fields fields) throws DatabaseException
	  {
		  logger.info(" >>>>>>>>>>>>>> SettleRecoBatchProcessor.createNewRecoObject() called ...... ");
		 Fields previous = fields.getPrevious();
	    Map<String, String> value = super.createNewRecoObject(fields);
	    value.put(FieldType.RECO_TXNTYPE.getName(), TransactionType.RECO.getName());
	    // Done By chetan nagaria for change in settlement process to mark transaction as RNS
//	    value.put(FieldType.STATUS.getName(), StatusType.SETTLED.getName());
	    if (!isStatusMatched(fields, StatusType.CAPTURED.getName())) {
			value.put(FieldType.STATUS.getName(), StatusType.FORCE_CAPTURED.getName());
	    System.out.println("inside createNewRecoObject "+StatusType.FORCE_CAPTURED.getName());
	  }else
	    value.put(FieldType.STATUS.getName(), StatusType.SETTLED_RECONCILLED.getName());
	    /*value.put(FieldType.RECO_AMOUNT.getName(), amount.formatAmount(previous.get(FieldType.RECO_AMOUNT.getName()), (String)value.get(FieldType.CURRENCY_CODE.getName())));	*/
	   
	   /* if(Boolean.parseBoolean(fields.get(FieldType.IS_RECO_PENDING.getName()))== true) {
	    	value.put(FieldType.IS_RECO_PENDING.getName(), fields.get(FieldType.IS_RECO_PENDING.getName()));
	    } else*/ if(Boolean.parseBoolean(fields.get(FieldType.IS_ENROLL_ENROLLED.getName()))== true) {
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

		public Map<String, String> createNewRefundRecoObject(Fields fields) throws DatabaseException {
			// Fields previous = fields.getPrevious();
			Map<String, String> value = super.createNewRefundRecoObject(fields);
			value.put(FieldType.RECO_TXNTYPE.getName(), TransactionType.REFUNDRECO.getName());
			// Done By chetan nagaria for change in settlement process to mark transaction
			// as RNS
			// value.put(FieldType.STATUS.getName(), StatusType.SETTLED.getName());

			if (!isStatusMatched(fields, StatusType.CAPTURED.getName())) {
				value.put(FieldType.STATUS.getName(), StatusType.FORCE_CAPTURED.getName());
			 System.out.println("inside createNewRefundRecoObject "+StatusType.FORCE_CAPTURED.getName());
			}else
				value.put(FieldType.STATUS.getName(), StatusType.SETTLED_RECONCILLED.getName());
			return value;
		}
	 
	/*Set required fields for RECO REPORTING Transaction*/
	 public Map<String, String> createNewRefundRecoReportingObject(Fields fields) {
			Map<String, String> value = super.createNewRefundRecoReportingObject(fields);
			value.put(FieldType.RECO_TXNTYPE.getName(), TransactionType.REFUNDRECO.getName());
			return value;
		}
	public Map<String, String> createNewRecoReportingObject(Fields fields) {
		Map<String, String> value = super.createNewRecoReportingObject(fields);
		value.put(FieldType.RECO_TXNTYPE.getName(), TransactionType.RECO.getName());
		return value;
	}
}