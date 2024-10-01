package com.pay10.batch.reco;

import com.pay10.batch.FileValidator;
import com.pay10.batch.commons.Fields;
import com.pay10.batch.commons.util.Processor;
import com.pay10.batch.exception.DatabaseException;
import com.pay10.commons.util.Constants;
import com.pay10.commons.util.FieldType;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

/** Performing sale reco file data validations. */
public class RecoFileValidator extends FileValidator {

	private static final Logger logger = LoggerFactory.getLogger(RecoFileValidator.class);

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
	private boolean readPreviousValuesFromDB(Fields fields) throws DatabaseException {
		String pg_ref_num = fields.get(FieldType.PG_REF_NUM.getName());
		logger.info("Read previous values from DB for Pg_Ref_Num: " + pg_ref_num);
		historyProcessor.preProcess(fields);
		if (isTransactionValid(fields) && (isAmountValid(fields, fields.get(FieldType.RECO_BOOKING_AMOUNT.getName())))) {
			if (Constants.RECO_TRUE.getValue().equalsIgnoreCase(fields.get(FieldType.IS_RECO_SETTLED.getName()))) {
				logger.info("There is already a transaction with RECO and SETTLED state in DB for this SALE transaction with Pg_Ref_Num: " + pg_ref_num);
				return true;
			} else if (Constants.RECO_TRUE.getValue().equalsIgnoreCase(fields.get(FieldType.IS_SALE_CAPTURED.getName()))) {
				logger.info("Received transaction with SALE and CAPTURED state in DB for Pg_Ref_Num: " + pg_ref_num);
				if ((!isBankSettlementIdValid(fields, fields.get(FieldType.RECO_ORDER_ID.getName())))
						|| (!isAmountValid(fields, fields.get(FieldType.RECO_BOOKING_AMOUNT.getName())))) {
					return false;
				}
			}else if (Constants.RECO_TRUE.getValue().equalsIgnoreCase(fields.get(FieldType.IS_RECO_FORCE_CAPRURED.getName()))) {
				logger.info("There is already a transaction with RECO and FORCE_CAPRURED state in DB for this SALE transaction for status failed with Pg_Ref_Num: " + pg_ref_num);
				return true;
				
			}
			return true;
		}
		return false;
	}
}
