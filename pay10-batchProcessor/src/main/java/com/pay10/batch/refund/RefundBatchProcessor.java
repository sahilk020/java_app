package com.pay10.batch.refund;

import java.util.Map;

import com.pay10.batch.BatchProcessor;
import com.pay10.batch.commons.Fields;
import com.pay10.batch.commons.util.Amount;
import com.pay10.batch.commons.util.DateCreater;
import com.pay10.batch.commons.util.GeneralValidator;
import com.pay10.batch.exception.DatabaseException;
import com.pay10.batch.exception.SystemException;
import com.pay10.commons.util.Constants;
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
public class RefundBatchProcessor extends BatchProcessor {
	
private static final Logger logger = LoggerFactory.getLogger(RefundBatchProcessor.class);
	
	@Autowired
	private Amount amount;
	
	public void setFileValidator(RefundFileValidator fileValidator) {
		super.setFileValidator(fileValidator);
	}

	/* Process the {@link Fields} item
	 * performing validation using {@link GeneralValidator} 
	 */
	public Fields process(Fields fields) throws SystemException, DatabaseException {
        logger.info("Validating refund reco fields on file processor: " + fields.getFieldsAsString());
        
		return super.process(fields);
	}

	/*Set required fields for RECO Transaction*/
	public Map<String, String> createNewRecoObject(Fields fields) throws DatabaseException {
		Map<String, String> value = super.createNewRecoObject(fields);
		/*value.put(FieldType.STATUS.getName(), (statusType != null) && ((statusType.equals(Constants.RECO_SUCCESS.getValue()))
						|| (statusType.equals(StatusType.RECONCILED.getName()))) ? StatusType.RECONCILED.getName() : StatusType.REJECTED.getName());*/
		value.put(FieldType.RECO_PG_TXN_STATUS.getName(), fields.get(FieldType.STATUS.getName()));
		value.put(FieldType.REFUND_FLAG.getName(), fields.get(FieldType.REFUND_FLAG.getName()));
		value.put(FieldType.RECO_PG_TXN_MESSAGE.getName(), fields.get(FieldType.RECO_PG_TXN_MESSAGE.getName()));
		value.put(FieldType.RECO_AMOUNT.getName(), amount.formatAmount(fields.get(FieldType.RECO_REFUNDAMOUNT.getName()), (String) value.get(FieldType.CURRENCY_CODE.getName())));
		value.put(FieldType.PG_DATE_TIME.getName(), DateCreater.formatDateForReco(fields.get(FieldType.REFUND_DATE_TIME.getName()), Constants.REFUND_RECO_DATE_FORMAT_DB.getValue()));
		value.put(FieldType.PG_REF_NUM.getName(), fields.get(FieldType.PG_REF_NUM.getName()));
		value.put(FieldType.RECO_TXNTYPE.getName(), TransactionType.REFUNDRECO.getName());
		
		return value;
	}
	
	/*Set required fields for RECO REPORTING Transaction*/
	public Map<String, String> createNewRecoReportingObject(Fields fields) {
		Map<String, String> value = super.createNewRecoReportingObject(fields);
		value.put(FieldType.RECO_TXNTYPE.getName(), TransactionType.REFUNDRECO.getName());
		return value;
	}
}
