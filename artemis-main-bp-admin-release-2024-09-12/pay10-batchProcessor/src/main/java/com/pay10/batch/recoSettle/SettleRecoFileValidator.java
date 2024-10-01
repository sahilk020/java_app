package com.pay10.batch.recoSettle;

import com.pay10.batch.FileSettleValidator;
import com.pay10.batch.commons.Fields;
import com.pay10.batch.commons.util.Processor;
import com.pay10.batch.exception.DatabaseException;
import com.pay10.commons.util.Constants;
import com.pay10.commons.util.FieldType;
import com.pay10.commons.util.StatusType;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

/** Performing sale reco file data validations. */
public class SettleRecoFileValidator extends FileSettleValidator {

	private static final Logger logger = LoggerFactory.getLogger(SettleRecoFileValidator.class);

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
		if (isTransactionValid(fields)) {
			if (Constants.RECO_TRUE.getValue().equalsIgnoreCase(fields.get(FieldType.IS_RECO_SETTLED.getName()))) {
				logger.info("There is already a transaction with RECO and SETTLEDstate in DB for this SALE transaction with Pg_Ref_Num: " + pg_ref_num);
				return false;
			} else if (Constants.RECO_TRUE.getValue().equalsIgnoreCase(fields.get(FieldType.IS_RECO_FORCE_CAPRURED.getName()))) {
				logger.info("There is already a transaction with RECO and FORCE CAPTURED state in DB for this SALE transaction for Failed status with Pg_Ref_Num: " + pg_ref_num);
				return false;
			} else if (Constants.RECO_TRUE.getValue().equalsIgnoreCase(fields.get(FieldType.IS_REFUND_RECO_SETTLED.getName()))) {
				logger.info("There is already a transaction with REFUNDRECO and SETTLED state in DB for this REFUND transaction with Pg_Ref_Num: " + pg_ref_num);
				return false;
			} else if (Constants.RECO_TRUE.getValue().equalsIgnoreCase(fields.get(FieldType.IS_SALE_CAPTURED.getName()))
					&& Constants.RECO_TRUE.getValue().equalsIgnoreCase(fields.get(FieldType.IS_RECO_PENDING.getName()))) {
				logger.info("Received transaction with SALE/CAPTURED and RECO/PENDING state in DB for Pg_Ref_Num: " + pg_ref_num);
				if ((!isBankSettlementIdValid(fields, fields.get(FieldType.RECO_ACQ_ID.getName())))
						|| (!isAcquirerTypeValid(fields))
						|| (!isAmountValid(fields, fields.get(FieldType.RECO_TOTAL_AMOUNT.getName())))
//						|| (!isStatusMatched(fields, StatusType.CAPTURED.getName()))
						) {
					return false;
				}
			} else if (Constants.RECO_TRUE.getValue().equalsIgnoreCase(fields.get(FieldType.IS_ENROLL_ENROLLED.getName()))
					|| Constants.RECO_TRUE.getValue().equalsIgnoreCase(fields.get(FieldType.IS_SALE_TIMEOUT.getName()))
					|| Constants.RECO_TRUE.getValue().equalsIgnoreCase(fields.get(FieldType.IS_SALE_SENT_TO_BANK.getName()))) {
				logger.info("Received transaction with ENROLL/ENROLLED, SALE/TIMEOUT and SALE/SENT_TO_BANK state in DB for Pg_Ref_Num: " + pg_ref_num);
				if ((!isAmountValid(fields, fields.get(FieldType.RECO_TOTAL_AMOUNT.getName())))
						|| (!isAcquirerTypeValid(fields))
//						|| (!isStatusMatched(fields, StatusType.CAPTURED.getName()))
						) {
					return false;
				}
			} else if (Constants.RECO_TRUE.getValue().equalsIgnoreCase(fields.get(FieldType.IS_SALE_CAPTURED.getName()))) {
				logger.info("Received transaction with SALE and CAPTURED state in DB for Pg_Ref_Num: " + pg_ref_num);
				if ((!isBankSettlementIdValid(fields, fields.get(FieldType.RECO_ACQ_ID.getName())))
						|| (!isAcquirerTypeValid(fields))
						|| (!isAmountValid(fields, fields.get(FieldType.RECO_TOTAL_AMOUNT.getName())))
//						|| (!isStatusMatched(fields, StatusType.CAPTURED.getName()))
						) {
					logger.info("Received transaction with SALE and CAPTURED state in DB for Pg_Ref_Num 2: " + pg_ref_num);
					return false;
				}
			}
			else if (Constants.RECO_TRUE.getValue().equalsIgnoreCase(fields.get(FieldType.IS_REFUND_CAPTURED.getName()))) {
				logger.info("Received transaction with REFUND and CAPTURED state in DB for Pg_Ref_Num: " + pg_ref_num);
				if ((!isBankSettlementIdValid(fields, fields.get(FieldType.RECO_ACQ_ID.getName())))
						|| (!isAcquirerTypeValid(fields))
						|| (!isAmountValid(fields, fields.get(FieldType.RECO_TOTAL_AMOUNT.getName())))) {
					return false;
				}
			}
			return true;
		}
		return false;
	}
}
