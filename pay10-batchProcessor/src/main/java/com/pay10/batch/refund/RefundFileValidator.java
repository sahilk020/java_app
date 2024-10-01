package com.pay10.batch.refund;

import com.pay10.batch.FileValidator;
import com.pay10.batch.commons.Fields;
import com.pay10.batch.commons.util.Amount;
import com.pay10.batch.commons.util.Processor;
import com.pay10.batch.exception.DatabaseException;
import com.pay10.batch.exception.ErrorType;
import com.pay10.commons.util.Constants;
import com.pay10.commons.util.FieldType;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

/** Performing refund reco file data validations. */
@Service
public class RefundFileValidator extends FileValidator {
	private static final Logger logger = LoggerFactory.getLogger(RefundFileValidator.class);

	@Autowired
	private Amount amount;
	
	@Autowired
	@Qualifier("historyProcessor")
	private Processor historyProcessor;
	
	/*Check if data in file is valid against the data in DB*/
	public boolean isDataValid(Fields fields) throws DatabaseException {
		return readPreviousValuesFromDB(fields);
	}

	/* Using history processor to get previous data using TXN_ID. 
	 * also validating input data to previous data.
	 * if validation is successful returns true.
	 * otherwise returns false.
	 */
	private boolean readPreviousValuesFromDB(Fields fields) throws DatabaseException
	{
		String pg_ref_num = fields.get(FieldType.PG_REF_NUM.getName());
		logger.info("Read previous values from DB for Refund Pg_Ref_Num: " + pg_ref_num);
		historyProcessor.preProcess(fields);
		Fields previous = fields.getPrevious();
		// Refund Reco already present
		if (Constants.RECO_TRUE.getValue().equalsIgnoreCase(fields.get(FieldType.IS_REFUND_RECO_RECONCILED.getName()))) {
			logger.info("There is already a transaction with REFUNDRECO and SETTLED state in DB for this REFUND transaction with Pg_Ref_Num: " + pg_ref_num);
			return false;
		}
		// Sale Capture present
		if (isTransactionValid(fields)) {
			if (Constants.RECO_TRUE.getValue().equalsIgnoreCase(fields.get(FieldType.IS_SALE_CAPTURED.getName()))) {
				logger.info("Received transaction for SALE and CAPTURED state in DB for Pg_Ref_Num: " + pg_ref_num);
				if ((isBankSettlementIdValid(fields, fields.get(FieldType.RECO_ORDER_ID.getName())))
						&& (isAmountValid(fields, fields.get(FieldType.RECO_REFUNDAMOUNT.getName())))
						&& (isRecoSettledValid(fields)))
						//|| (!isRefundflagValid(fields))
						//|| (!isRecoSettledValid(fields))) 
						{
							/*if(isRecoSettledValid(fields)) {
								return true;
							}*/
					
					return true;
				}
				
			}
		}
		return false;
	}
	
	protected boolean isAmountValid(Fields fields, String currAmount) {
		Fields previous = fields.getPrevious();
		String currency = previous.get(FieldType.CURRENCY_CODE.getName());
		String previousAmount = "0.00";
		if(previous != null) {
			if(fields.get(FieldType.REFUND_FLAG.getName()).equals(Constants.R_FLAG.getValue())) {
				previousAmount = amount.formatAmount(previous.get(FieldType.RECO_AMOUNT.getName()), currency);
				currAmount = amount.formatAmount(currAmount, currency);
				if (previousAmount != null && previousAmount.equals(currAmount)) {
					return true;
				}
			} else {
				return true;
			}			
		}
		fields.put(FieldType.RESPONSE_CODE.getName(), ErrorType.INVALID_REFUND_AMOUNT.getResponseCode());
		fields.put(FieldType.RESPONSE_MESSAGE.getName(), ErrorType.INVALID_REFUND_AMOUNT.getResponseMessage());
		fields.put(FieldType.USER_TYPE.getName(),Constants.RECO_MERCHANT.getValue());
		return false;
	}
	/*private boolean isRefundflagValid(Fields fields) {
		Fields previous = fields.getPrevious();
		if(previous != null 
				&& fields.get(FieldType.REFUND_FLAG.getName()).equals(previous.get(FieldType.REFUND_FLAG.getName()))) {
			return true;
		}
		fields.put(FieldType.RESPONSE_CODE.getName(), ErrorType.INVALID_REFUND_FLAG.getResponseCode());
		fields.put(FieldType.RESPONSE_MESSAGE.getName(), ErrorType.INVALID_REFUND_FLAG.getResponseMessage());
		return false;
	}*/
	
	private boolean isSaleTxnValid(Fields fields) {
		Fields previous = fields.getPrevious();
		if(previous != null  && Boolean.parseBoolean(fields.get(FieldType.IS_SALE_CAPTURED.getName()))) {
			return true;
		}
		fields.put(FieldType.RESPONSE_CODE.getName(), ErrorType.INVALID_REFUND_SALE.getResponseCode());
		fields.put(FieldType.RESPONSE_MESSAGE.getName(), ErrorType.INVALID_REFUND_SALE.getResponseMessage());
		return false;
	}
	
	private boolean isRecoSettledValid(Fields fields) {
		Fields previous = fields.getPrevious();
		if(previous != null 
				&& Boolean.parseBoolean(fields.get(FieldType.IS_RECO_SETTLED.getName()))) {
			return true;
		}
		fields.put(FieldType.RESPONSE_CODE.getName(), ErrorType.MISSING_RECO_SETTLED.getResponseCode());
		fields.put(FieldType.RESPONSE_MESSAGE.getName(), ErrorType.MISSING_RECO_SETTLED.getResponseMessage());
		return false;
	}
}
