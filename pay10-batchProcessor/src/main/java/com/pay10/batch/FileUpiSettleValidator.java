package com.pay10.batch;

import com.pay10.batch.commons.Fields;
import com.pay10.batch.commons.util.Amount;
import com.pay10.batch.exception.DatabaseException;
import com.pay10.batch.exception.ErrorType;
import com.pay10.commons.util.Constants;
import com.pay10.commons.util.FieldType;

import org.springframework.beans.factory.annotation.Autowired;

/** Performing reco file data validations. */
public abstract class FileUpiSettleValidator {
	
	@Autowired
	private Amount amount;

	/* Check if data in file is valid against the data in DB */
	protected abstract boolean isDataValid(Fields fields) throws DatabaseException;

	/* Validating Amount Converting the amount in decimal value for the currency
	 * code and then validate */
	protected boolean isAmountValid(Fields fields, String currAmount) {
		Fields previous = fields.getPrevious();
		String currency = previous.get(FieldType.CURRENCY_CODE.getName());
		if(previous != null) {
			String previousAmount = amount.formatAmount(previous.get(FieldType.RECO_TOTAL_AMOUNT.getName()), currency);
			currAmount = amount.formatAmount(currAmount, currency);
			if (previousAmount != null && previousAmount.equals(currAmount)) {
				return true;
			}
		}
		fields.put(FieldType.RESPONSE_CODE.getName(), ErrorType.INVALID_RECO_AMOUNT.getResponseCode());
		fields.put(FieldType.RESPONSE_MESSAGE.getName(), ErrorType.INVALID_RECO_AMOUNT.getResponseMessage());
		fields.put(FieldType.USER_TYPE.getName(), Constants.BANK.getValue());
		return false;
	}

	/* Validating Bank Settlement Id */
	protected boolean isBankSettlementIdValid(Fields fields, String currAcqId) {
		Fields previous = fields.getPrevious();
		
		if (previous != null && previous.get(FieldType.RECO_ACQ_ID.getName()) != null && previous.get(FieldType.RECO_ACQ_ID.getName()).equalsIgnoreCase(currAcqId)) {
			return true;
		}
		fields.put(FieldType.RESPONSE_CODE.getName(), ErrorType.INVALID_RECO_ACQ_ID.getResponseCode());
		fields.put(FieldType.RESPONSE_MESSAGE.getName(), ErrorType.INVALID_RECO_ACQ_ID.getResponseMessage());
		fields.put(FieldType.USER_TYPE.getName(),Constants.BANK.getValue());
		return false;
	}
	
	/* Validating Pg_Ref_Num */
	protected boolean isTransactionValid(Fields fields) {
		Fields previous = fields.getPrevious();
		if (previous != null && fields.get(FieldType.PG_REF_NUM.getName()).equals(previous.get(FieldType.PG_REF_NUM.getName()))) {
			return true;
		}
		fields.put(FieldType.RESPONSE_CODE.getName(), ErrorType.INVALID_RECO_TXN_ID.getResponseCode());
		fields.put(FieldType.RESPONSE_MESSAGE.getName(), ErrorType.INVALID_RECO_TXN_ID.getResponseMessage());
		fields.put(FieldType.USER_TYPE.getName(),Constants.BANK.getValue());
		return false;
	}
}