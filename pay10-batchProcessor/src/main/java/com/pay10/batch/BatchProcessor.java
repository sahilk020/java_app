package com.pay10.batch;

import java.util.HashMap;
import java.util.Map;

import com.pay10.batch.commons.Fields;
import com.pay10.batch.commons.FieldsDao;
import com.pay10.batch.commons.util.Amount;
import com.pay10.batch.commons.util.GeneralValidator;
import com.pay10.batch.commons.util.StepExecutor;
import com.pay10.batch.commons.util.TransactionManager;
import com.pay10.batch.exception.DatabaseException;
import com.pay10.batch.exception.ErrorType;
import com.pay10.batch.exception.SystemException;
import com.pay10.commons.util.Constants;
import com.pay10.commons.util.FieldType;
import com.pay10.commons.util.StatusType;
import com.pay10.commons.util.TransactionType;

import org.springframework.batch.item.ItemProcessor;
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
public abstract class BatchProcessor implements ItemProcessor<Fields, Fields> {
	
	@Autowired
	private Fields recoObject;
	
	@Autowired
	private FieldsDao fieldsDao;
	@Autowired
	private Amount amount;
	private FileValidator fileValidator;
	
	public void setFileValidator(FileValidator fileValidator) {
		this.fileValidator = fileValidator;
	}
	/* Process the {@link Fields} item
	 * performing validation using {@link GeneralValidator} 
	 */
	@Override
	
	public Fields process(Fields fields) throws SystemException, DatabaseException {
	    GeneralValidator generalValidator = new GeneralValidator();
	    generalValidator.validateRecoRefund(fields);
	    fields.put(FieldType.RECO_TXNTYPE.getName(), TransactionType.RECO.getName());
	    boolean isDataValid = fileValidator.isDataValid(fields);
	    if (isDataValid /*&& Boolean.parseBoolean(fields.get(FieldType.IS_RECO_RECONCILED.getName())) == false*/ 
	    		&& Boolean.parseBoolean(fields.get(FieldType.IS_RECO_SETTLED.getName())) == true){
	    	recoObject.setPrevious(fields.getPrevious());
	    	recoObject.setRefundPrevious(fields.getRefundPrevious());
	    	recoObject.setFields(createNewRecoObject(fields));
	    } else if (isDataValid &&  Boolean.parseBoolean(fields.get(FieldType.IS_SALE_CAPTURED.getName())) == true 
	    		&& Boolean.parseBoolean(fields.get(FieldType.IS_RECO_SETTLED.getName())) == false 
	    				/*&& Boolean.parseBoolean(fields.get(FieldType.IS_RECO_PENDING.getName())) == false*/) {
		      recoObject.setFields(createNewRecoPendingObject(fields));
	    } else if ((/*(Constants.RECO_TRUE.getValue().equalsIgnoreCase(fields.get(FieldType.IS_RECO_RECONCILED.getName()))) &&*/ (fields.get(FieldType.REFUND_FLAG.getName()) == null))
	    		/*|| Constants.RECO_TRUE.getValue().equalsIgnoreCase(fields.get(FieldType.IS_REFUND_RECO_RECONCILED.getName()))*/) {
	      Map<String, String> value = new HashMap<String, String>();
	      value.put(FieldType.NO_REPORTING_REQUIRED.getName(), Constants.RECO_TRUE.getValue());
	      recoObject.setFields(value);
	    } else {
	      recoObject.setFields(createNewRecoReportingObject(fields));
	    }
	    return recoObject;
	}
	
	/*Set required fields for RECO Transaction*/
	protected Map<String, String> createNewRecoObject(Fields fields) throws DatabaseException {
		Fields previous = fields.getPrevious();
		Map<String, String> value = new HashMap<String, String>();
		value.put(FieldType.TXN_ID.getName(), TransactionManager.getNewTransactionId());
		value.put(FieldType.PG_REF_NUM.getName(), fields.get(FieldType.PG_REF_NUM.getName()));
		value.put(FieldType.ORIG_TXN_ID.getName(), previous.get(FieldType.ORIG_TXN_ID.getName()));
		
		if(previous.get(FieldType.ORIG_TXNTYPE.getName()) != null) {
			value.put(FieldType.ORIG_TXNTYPE.getName(), previous.get(FieldType.ORIG_TXNTYPE.getName()));
		} else {
			value.put(FieldType.ORIG_TXNTYPE.getName(), previous.get(FieldType.RECO_TXNTYPE.getName()));
		}			
		
		/*if((previous.get(FieldType.RECO_TXNTYPE.getName()).equals(TransactionType.REFUND.getName()))  && (previous.get(FieldType.STATUS.getName()).equals(StatusType.PENDING.getName()))) {
			List<Fields> lstFields = new ArrayList<Fields>();
			lstFields = fieldsDao.getPreviousSaleCapturedForOid(previous.get(FieldType.OID.getName()));
			if(lstFields.size()>0) {
				value.put(FieldType.RECO_AMOUNT.getName(), amount.formatAmount(lstFields.get(0).get(FieldType.RECO_AMOUNT.getName()), lstFields.get(0).get(FieldType.CURRENCY_CODE.getName())));
				value.put(FieldType.RECO_TOTAL_AMOUNT.getName(),amount.formatAmount(lstFields.get(0).get(FieldType.RECO_TOTAL_AMOUNT.getName()), lstFields.get(0).get(FieldType.CURRENCY_CODE.getName())));
			} else {
				value.put(FieldType.RECO_AMOUNT.getName(), amount.formatAmount(previous.get(FieldType.RECO_AMOUNT.getName()), previous.get(FieldType.CURRENCY_CODE.getName())));
				value.put(FieldType.RECO_TOTAL_AMOUNT.getName(),amount.formatAmount(previous.get(FieldType.RECO_TOTAL_AMOUNT.getName()), previous.get(FieldType.CURRENCY_CODE.getName())));
			}
			
		} else {*/
			value.put(FieldType.RECO_AMOUNT.getName(), amount.formatAmount(previous.get(FieldType.RECO_AMOUNT.getName()), previous.get(FieldType.CURRENCY_CODE.getName())));
			value.put(FieldType.RECO_TOTAL_AMOUNT.getName(),amount.formatAmount(previous.get(FieldType.RECO_TOTAL_AMOUNT.getName()), previous.get(FieldType.CURRENCY_CODE.getName())));
		//}
		value.put(FieldType.ORIG_TXN_ID.getName(), previous.get(FieldType.ORIG_TXN_ID.getName()));
		value.put(FieldType.RECO_PAY_ID.getName(), previous.get(FieldType.RECO_PAY_ID.getName()));
		value.put(FieldType.CURRENCY_CODE.getName(), previous.get(FieldType.CURRENCY_CODE.getName()));
		value.put(FieldType.RECO_ORDER_ID.getName(), previous.get(FieldType.RECO_ORDER_ID.getName()));
		value.put(FieldType.OID.getName(), previous.get(FieldType.OID.getName()));
		value.put(FieldType.ARN.getName(),previous.get(FieldType.ARN.getName()));
		value.put(FieldType.RRN.getName(),previous.get(FieldType.RRN.getName()));
		value.put(FieldType.RECO_ACQ_ID.getName(),previous.get(FieldType.RECO_ACQ_ID.getName()));
		value.put(FieldType.RECO_PAYMENT_TYPE.getName(),previous.get(FieldType.RECO_PAYMENT_TYPE.getName()));
		value.put(FieldType.MOP_TYPE.getName(),previous.get(FieldType.MOP_TYPE.getName()));
		value.put(FieldType.RECO_ACQUIRER_TYPE.getName(),previous.get(FieldType.RECO_ACQUIRER_TYPE.getName()));
		value.put(FieldType.CUST_NAME.getName(),previous.get(FieldType.CUST_NAME.getName()));
		value.put(FieldType.CUST_EMAIL.getName(),previous.get(FieldType.CUST_EMAIL.getName()));
		value.put(FieldType.CARD_MASK.getName(),previous.get(FieldType.CARD_MASK.getName()));
		value.put(FieldType.SURCHARGE_FLAG.getName(),previous.get(FieldType.SURCHARGE_FLAG.getName()));
		value.put(FieldType.PAYMENTS_REGION.getName(),previous.get(FieldType.PAYMENTS_REGION.getName()));
		value.put(FieldType.RECO_CARD_HOLDER_TYPE.getName(),previous.get(FieldType.RECO_CARD_HOLDER_TYPE.getName()));
		value.put(FieldType.RECO_PG_TXN_MESSAGE.getName(),previous.get(FieldType.RECO_PG_TXN_MESSAGE.getName()));
		if(previous.get(FieldType.RECO_TXNTYPE.getName()).equals(TransactionType.RECO.getName()) &&
				previous.get(FieldType.STATUS.getName()).equals(StatusType.SETTLED_RECONCILLED.getName())) {
			value.put(FieldType.PG_DATE_TIME.getName(), previous.get(FieldType.PG_DATE_TIME.getName()));
		} else if(previous.get(FieldType.RECO_TXNTYPE.getName()).equals(TransactionType.SALE.getName()) &&
				previous.get(FieldType.STATUS.getName()).equals(StatusType.CAPTURED.getName())) {
			value.put(FieldType.PG_DATE_TIME.getName(), previous.get(FieldType.CREATE_DATE.getName()));
		} else {
			value.put(FieldType.PG_DATE_TIME.getName(), fieldsDao.getSaleDate(previous.get(FieldType.PG_REF_NUM.getName())));
		}
		
		if(Boolean.parseBoolean(fields.get(FieldType.IS_RECO_SETTLED.getName())) == true) {
			value.put(FieldType.IS_RECO_SETTLED.getName(),fields.get(FieldType.IS_RECO_SETTLED.getName()));
		}
		if(Boolean.parseBoolean(fields.get(FieldType.IS_RECO_FORCE_CAPRURED.getName())) == true) {
			value.put(FieldType.IS_RECO_FORCE_CAPRURED.getName(),fields.get(FieldType.IS_RECO_FORCE_CAPRURED.getName()));
		}
		/*if(Boolean.parseBoolean(fields.get(FieldType.IS_REFUND_PENDING.getName())) == true) {
			value.put(FieldType.IS_REFUND_PENDING.getName(),fields.get(FieldType.IS_REFUND_PENDING.getName()));
		}*/	
		value.put(FieldType.RESPONSE_CODE.getName(), ErrorType.SUCCESS.getResponseCode());
		value.put(FieldType.RESPONSE_MESSAGE.getName(), ErrorType.SUCCESS.getResponseMessage());
		return value;
	}
	
	/*Set required fields for RECO Transaction*/
	protected Map<String, String> createNewRecoPendingObject(Fields fields) throws DatabaseException {
		Fields previous = fields.getPrevious();
		Map<String, String> value = new HashMap<String, String>();
		value.put(FieldType.TXN_ID.getName(), TransactionManager.getNewTransactionId());
		value.put(FieldType.PG_REF_NUM.getName(), fields.get(FieldType.PG_REF_NUM.getName()));
		value.put(FieldType.ORIG_TXN_ID.getName(), previous.get(FieldType.ORIG_TXN_ID.getName()));
		
		if(previous.get(FieldType.ORIG_TXNTYPE.getName()) != null) {
			value.put(FieldType.ORIG_TXNTYPE.getName(), previous.get(FieldType.ORIG_TXNTYPE.getName()));
		} else {
			value.put(FieldType.ORIG_TXNTYPE.getName(), previous.get(FieldType.RECO_TXNTYPE.getName()));
		}
		
		value.put(FieldType.RECO_AMOUNT.getName(), amount.formatAmount(previous.get(FieldType.RECO_AMOUNT.getName()), previous.get(FieldType.CURRENCY_CODE.getName())));
		value.put(FieldType.RECO_TOTAL_AMOUNT.getName(),amount.formatAmount(previous.get(FieldType.RECO_TOTAL_AMOUNT.getName()), previous.get(FieldType.CURRENCY_CODE.getName())));
		value.put(FieldType.RECO_PAY_ID.getName(), previous.get(FieldType.RECO_PAY_ID.getName()));
		value.put(FieldType.CURRENCY_CODE.getName(), previous.get(FieldType.CURRENCY_CODE.getName()));
		value.put(FieldType.RECO_ORDER_ID.getName(), previous.get(FieldType.RECO_ORDER_ID.getName()));
		value.put(FieldType.OID.getName(), previous.get(FieldType.OID.getName()));
		value.put(FieldType.ARN.getName(),previous.get(FieldType.ARN.getName()));
		value.put(FieldType.RRN.getName(),previous.get(FieldType.RRN.getName()));
		value.put(FieldType.RECO_ACQ_ID.getName(),previous.get(FieldType.RECO_ACQ_ID.getName()));
		value.put(FieldType.RECO_PAYMENT_TYPE.getName(),previous.get(FieldType.RECO_PAYMENT_TYPE.getName()));
		value.put(FieldType.MOP_TYPE.getName(),previous.get(FieldType.MOP_TYPE.getName()));
		value.put(FieldType.RECO_ACQUIRER_TYPE.getName(),previous.get(FieldType.RECO_ACQUIRER_TYPE.getName()));
		value.put(FieldType.CUST_NAME.getName(),previous.get(FieldType.CUST_NAME.getName()));
		value.put(FieldType.CUST_EMAIL.getName(),previous.get(FieldType.CUST_EMAIL.getName()));
		value.put(FieldType.CARD_MASK.getName(),previous.get(FieldType.CARD_MASK.getName()));
		value.put(FieldType.SURCHARGE_FLAG.getName(),previous.get(FieldType.SURCHARGE_FLAG.getName()));
		value.put(FieldType.PAYMENTS_REGION.getName(),previous.get(FieldType.PAYMENTS_REGION.getName()));
		value.put(FieldType.RECO_CARD_HOLDER_TYPE.getName(),previous.get(FieldType.RECO_CARD_HOLDER_TYPE.getName()));
		value.put(FieldType.RECO_PG_TXN_MESSAGE.getName(),previous.get(FieldType.RECO_PG_TXN_MESSAGE.getName()));
		if(previous.get(FieldType.RECO_TXNTYPE.getName()).equals(TransactionType.SALE.getName()) &&
				previous.get(FieldType.STATUS.getName()).equals(StatusType.CAPTURED.getName())) {
			value.put(FieldType.PG_DATE_TIME.getName(), previous.get(FieldType.CREATE_DATE.getName()));
		} else {
			value.put(FieldType.PG_DATE_TIME.getName(), fieldsDao.getSaleDate(previous.get(FieldType.PG_REF_NUM.getName())));
		}

		value.put(FieldType.RESPONSE_CODE.getName(), ErrorType.SUCCESS.getResponseCode());
		value.put(FieldType.RESPONSE_MESSAGE.getName(), ErrorType.SUCCESS.getResponseMessage());
		return value;
	}
	
	/*Set required fields for RECO REPORTING Transaction*/
	protected Map<String, String> createNewRecoReportingObject(Fields fields) {
		Map<String, String> value = new HashMap<String, String>();
		Fields previous = fields.getPrevious();
		
		value.put(FieldType.TXN_ID.getName(), TransactionManager.getNewTransactionId());
		if(previous != null) {			
			value.put(FieldType.DB_TXNTYPE.getName(), previous.get(FieldType.RECO_TXNTYPE.getName()));
			value.put(FieldType.DB_PG_REF_NUM.getName(), previous.get(FieldType.PG_REF_NUM.getName()));
			value.put(FieldType.DB_TXN_ID.getName(), previous.get(FieldType.TXN_ID.getName()));
			value.put(FieldType.DB_OID.getName(), previous.get(FieldType.OID.getName()));
			value.put(FieldType.DB_ORIG_TXN_ID.getName(), previous.get(FieldType.ORIG_TXN_ID.getName()));
			value.put(FieldType.DB_ORIG_TXNTYPE.getName(), previous.get(FieldType.ORIG_TXNTYPE.getName()));
			value.put(FieldType.DB_AMOUNT.getName(), previous.get(FieldType.RECO_AMOUNT.getName()));
			value.put(FieldType.DB_ORDER_ID.getName(), previous.get(FieldType.RECO_ORDER_ID.getName()));
			value.put(FieldType.DB_PAY_ID.getName(), previous.get(FieldType.RECO_PAY_ID.getName()));
			value.put(FieldType.DB_ACQUIRER_TYPE.getName(), previous.get(FieldType.RECO_ACQUIRER_TYPE.getName()));
			value.put(FieldType.DB_ACQ_ID.getName(), previous.get(FieldType.RECO_ACQ_ID.getName()));
			value.put(FieldType.DB_TRANSACTION_STATUS.getName(), previous.get(FieldType.STATUS.getName()));
		}
		value.put(FieldType.FILE_LINE_NO.getName(), String.valueOf(fields.get(FieldType.FILE_LINE_NO.getName())));
		value.put(FieldType.FILE_LINE_DATA.getName(), fields.get(FieldType.FILE_LINE_DATA.getName()));
		value.put(FieldType.RESPONSE_CODE.getName(), fields.get(FieldType.RESPONSE_CODE.getName()));
		value.put(FieldType.RESPONSE_MESSAGE.getName(), fields.get(FieldType.RESPONSE_MESSAGE.getName()));
		value.put(FieldType.DB_USER_TYPE.getName(), fields.get(FieldType.USER_TYPE.getName()));
		value.put(FieldType.RECO_EXCEPTION_STATUS.getName(), Constants.EXCEPTION_STATUS.getValue());
		
		String fileName = StepExecutor.getFileName();
		String name = fileName != null ? fileName.substring(fileName.lastIndexOf(Constants.FORWARD_SLASH_SEPERATOR.getValue()) + 1)	: null;
		value.put(FieldType.FILE_NAME.getName(), name);
		
		return value;
	}
}

