package com.pay10.batch;

import com.pay10.batch.commons.Fields;
import com.pay10.batch.commons.FieldsDao;
import com.pay10.batch.commons.util.Amount;
import com.pay10.batch.exception.DatabaseException;
import com.pay10.batch.exception.ErrorType;
import com.pay10.commons.util.Constants;
import com.pay10.commons.util.FieldType;

import org.springframework.beans.factory.annotation.Autowired;

/** Performing reco file data validations. */
public abstract class FileValidator {
	
	@Autowired
	private Amount amount;
	
	@Autowired
	private FieldsDao fieldsDao;

	/* Check if data in file is valid against the data in DB */
	protected abstract boolean isDataValid(Fields fields) throws DatabaseException;

	/* Validating Amount Converting the amount in decimal value for the currency
	 * code and then validate */
	protected boolean isAmountValid(Fields fields, String currAmount) throws DatabaseException {
		Fields previous = fields.getPrevious();
		String currency = previous.get(FieldType.CURRENCY_CODE.getName());
		String previousAmount = "0.00";
		if(previous != null) {
			/*if((previous.get(FieldType.RECO_TXNTYPE.getName()).equals(TransactionType.REFUND.getName()))  && (previous.get(FieldType.STATUS.getName()).equals(StatusType.PENDING.getName()))) {
				List<Fields> lstFields = new ArrayList<Fields>();
				lstFields = fieldsDao.getPreviousSaleCapturedForOid(previous.get(FieldType.OID.getName()));
				if(lstFields.size()>0) {
					previousAmount = amount.formatAmount(lstFields.get(0).get(FieldType.RECO_AMOUNT.getName()), currency);
				} else {
					previousAmount = amount.formatAmount(previous.get(FieldType.RECO_AMOUNT.getName()), currency);
				}
				
			} else {*/
				previousAmount = amount.formatAmount(previous.get(FieldType.RECO_AMOUNT.getName()), currency);
			//}
			currAmount = amount.formatAmount(currAmount, currency);
			if (previousAmount != null && previousAmount.equals(currAmount)) {
				return true;
			}
		}
		fields.put(FieldType.RESPONSE_CODE.getName(), ErrorType.INVALID_RECO_AMOUNT.getResponseCode());
		fields.put(FieldType.RESPONSE_MESSAGE.getName(), ErrorType.INVALID_RECO_AMOUNT.getResponseMessage());
		fields.put(FieldType.USER_TYPE.getName(), Constants.RECO_MERCHANT.getValue());
		return false;
	}

	/* Validating Bank Settlement Id */
	protected boolean isBankSettlementIdValid(Fields fields, String currOrderId) {
		Fields previous = fields.getPrevious();
		
		if (previous != null && previous.get(FieldType.RECO_ORDER_ID.getName()) != null && previous.get(FieldType.RECO_ORDER_ID.getName()).equalsIgnoreCase(currOrderId)) {
			return true;
		}
		fields.put(FieldType.RESPONSE_CODE.getName(), ErrorType.INVALID_RECO_ORDER_ID.getResponseCode());
		fields.put(FieldType.RESPONSE_MESSAGE.getName(), ErrorType.INVALID_RECO_ORDER_ID.getResponseMessage());
		fields.put(FieldType.USER_TYPE.getName(),Constants.RECO_MERCHANT.getValue());
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
		fields.put(FieldType.USER_TYPE.getName(),Constants.RECO_MERCHANT.getValue());
		return false;
	}
}