package com.pay10.batch;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import com.pay10.batch.commons.Fields;
import com.pay10.batch.commons.FieldsDao;
import com.pay10.batch.commons.util.Amount;
import com.pay10.batch.commons.util.DateCreater;
import com.pay10.batch.commons.util.GeneralSettleValidator;
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

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Process the provided item {@link Fields} object, Step1: performing validation
 * using {@link GeneralValidator}. Step2: calling history processor to get
 * previous data using TXN_ID. Step3: validating input data to previous data.
 * Step3: if valid, setting previous data to {@link Fields} object. Step4:
 * returning modified {@link Fields} object.
 * 
 * @param {@link Fields} object to be processed.
 * @return modified {@link Fields} object for continued processing.
 */
public abstract class BatchSettleProcessor implements ItemProcessor<Fields, Fields> {

	private static final Logger logger = LoggerFactory.getLogger(BatchSettleProcessor.class);

	@Autowired
	private Fields recoObject;

	@Autowired
	FieldsDao fieldsDao;

	@Autowired
	private Amount amount;

	private FileSettleValidator fileSettleValidator;

	public void setFileSettleValidator(FileSettleValidator fileSettleValidator) {
		this.fileSettleValidator = fileSettleValidator;
	}

	/*
	 * Process the {@link Fields} item performing validation using {@link
	 * GeneralValidator}
	 */
	public Fields process(Fields fields) throws SystemException, DatabaseException {

		GeneralSettleValidator generalValidator = new GeneralSettleValidator();
		if (fields.get(FieldType.RESPONSE_CODE.getName()).equals("15")) {
			fields.put(FieldType.REFUND_FLAG.getName(), null);
		}
		generalValidator.validateRecoRefund(fields);
		if (fields.get(FieldType.RESPONSE_CODE.getName()).equals("15")) {
			fields.put(FieldType.RECO_TXNTYPE.getName(), TransactionType.RECO.getName());
			if (fileSettleValidator.isDataValid(fields)) {
				recoObject.setPrevious(fields.getPrevious());
				recoObject.setRefundPrevious(fields.getRefundPrevious());
				Map<String, String> recoObjectFields = createNewRecoObject(fields);
				logger.info("Create New Reco Object : {}", recoObjectFields.toString());
				recoObject.setFields(recoObjectFields);
			} else if (((Constants.RECO_TRUE.getValue()
					.equalsIgnoreCase(fields.get(FieldType.IS_RECO_SETTLED.getName())))
					&& (fields.get(FieldType.REFUND_FLAG.getName()) == null))
					|| Constants.RECO_TRUE.getValue()
							.equalsIgnoreCase(fields.get(FieldType.IS_REFUND_RECO_SETTLED.getName()))
							|| Constants.RECO_TRUE.getValue()
							.equalsIgnoreCase(fields.get(FieldType.IS_RECO_FORCE_CAPRURED.getName()))) {
				Map<String, String> value = new HashMap<String, String>();
				value.put(FieldType.NO_REPORTING_REQUIRED.getName(), Constants.RECO_TRUE.getValue());
				recoObject.setFields(value);
			} else {
				recoObject.setFields(createNewRecoReportingObject(fields));
			}
		} else if (fields.get(FieldType.RESPONSE_CODE.getName()).equals("999")) {
			fields.put(FieldType.RECO_TXNTYPE.getName(), TransactionType.REFUNDRECO.getName());
			if (fileSettleValidator.isDataValid(fields)) {
				recoObject.setPrevious(fields.getPrevious());
				recoObject.setRefundPrevious(fields.getRefundPrevious());
				recoObject.setFields(createNewRefundRecoObject(fields));
			} else if (((Constants.RECO_TRUE.getValue()
					.equalsIgnoreCase(fields.get(FieldType.IS_RECO_SETTLED.getName())))
					&& (fields.get(FieldType.REFUND_FLAG.getName()) == null))
					|| Constants.RECO_TRUE.getValue()
							.equalsIgnoreCase(fields.get(FieldType.IS_REFUND_RECO_SETTLED.getName()))
							|| Constants.RECO_TRUE.getValue()
							.equalsIgnoreCase(fields.get(FieldType.IS_RECO_FORCE_CAPRURED.getName()))) {
				Map<String, String> value = new HashMap<String, String>();
				value.put(FieldType.NO_REPORTING_REQUIRED.getName(), Constants.RECO_TRUE.getValue());
				recoObject.setFields(value);
			} else {
				recoObject.setFields(createNewRefundRecoReportingObject(fields));
			}
		}
		return recoObject;
	}

	/* Set required fields for RECO Transaction */
	protected Map<String, String> createNewRecoObject(Fields fields) throws DatabaseException {
		logger.info(" >>>>>>>>>>>>>>>>>>>>>>> BatchSettleProcessor.createNewRecoObject() called ...... ");
		logger.info(
				" >>> BatchSettleProcessor.createNewRecoObject() called ...... fields : " + fields.getFieldsAsString());
		Fields previous = fields.getPrevious();
		logger.info(" >>> BatchSettleProcessor.createNewRecoObject() called ...... previous : "
				+ previous.getFieldsAsString());
		Map<String, String> value = new HashMap<String, String>();
		try {
			TimeUnit.MILLISECONDS.sleep(3);
		} catch (InterruptedException exception) {
			logger.error("Inside BatchSettleProcessor Class, in createNewRecoObject method  : ", exception);
		}
		value.put(FieldType.TXN_ID.getName(), TransactionManager.getNewTransactionId());
		value.put(FieldType.PG_REF_NUM.getName(), fields.get(FieldType.PG_REF_NUM.getName()));
		value.put(FieldType.ORIG_TXN_ID.getName(), previous.get(FieldType.ORIG_TXN_ID.getName()));

		if (previous.get(FieldType.ORIG_TXNTYPE.getName()) != null) {
			value.put(FieldType.ORIG_TXNTYPE.getName(), previous.get(FieldType.ORIG_TXNTYPE.getName()));
		} else {
			value.put(FieldType.ORIG_TXNTYPE.getName(), previous.get(FieldType.RECO_TXNTYPE.getName()));
		}

		value.put(FieldType.RECO_AMOUNT.getName(), amount.formatAmount(previous.get(FieldType.RECO_AMOUNT.getName()),
				previous.get(FieldType.CURRENCY_CODE.getName())));
		value.put(FieldType.RECO_TOTAL_AMOUNT.getName(), amount.formatAmount(
				previous.get(FieldType.RECO_TOTAL_AMOUNT.getName()), previous.get(FieldType.CURRENCY_CODE.getName())));
		value.put(FieldType.ORIG_TXN_ID.getName(), previous.get(FieldType.ORIG_TXN_ID.getName()));
		value.put(FieldType.RECO_PAY_ID.getName(), previous.get(FieldType.RECO_PAY_ID.getName()));
		value.put(FieldType.CURRENCY_CODE.getName(), previous.get(FieldType.CURRENCY_CODE.getName()));
		value.put(FieldType.RECO_ORDER_ID.getName(), previous.get(FieldType.RECO_ORDER_ID.getName()));
		value.put(FieldType.OID.getName(), previous.get(FieldType.OID.getName()));
		value.put(FieldType.ARN.getName(), fields.get(FieldType.ARN.getName()));
		value.put(FieldType.RRN.getName(), fields.get(FieldType.RRN.getName()));
		value.put(FieldType.ACQUIRER_TDR_SC.getName(), previous.get(FieldType.ACQUIRER_TDR_SC.getName()));
		value.put(FieldType.ACQUIRER_GST.getName(), previous.get(FieldType.ACQUIRER_GST.getName()));
		value.put(FieldType.PG_TDR_SC.getName(), previous.get(FieldType.PG_TDR_SC.getName()));
		value.put(FieldType.PG_GST.getName(), previous.get(FieldType.PG_GST.getName()));
		String fileName = StepExecutor.getFileName();
		String name = fileName != null
				? fileName.substring(fileName.lastIndexOf(Constants.FORWARD_SLASH_SEPERATOR.getValue()) + 1)
				: null;
		value.put(FieldType.RECO_ACQ_ID.getName(), fields.get(FieldType.RECO_ACQ_ID.getName()));
		value.put(FieldType.RECO_PAYMENT_TYPE.getName(), previous.get(FieldType.RECO_PAYMENT_TYPE.getName()));
		value.put(FieldType.MOP_TYPE.getName(), previous.get(FieldType.MOP_TYPE.getName()));
		value.put(FieldType.RECO_ACQUIRER_TYPE.getName(), previous.get(FieldType.RECO_ACQUIRER_TYPE.getName()));

		value.put(FieldType.CUST_NAME.getName(), previous.get(FieldType.CUST_NAME.getName()));
		value.put(FieldType.CUST_EMAIL.getName(), previous.get(FieldType.CUST_EMAIL.getName()));
		value.put(FieldType.CARD_MASK.getName(), previous.get(FieldType.CARD_MASK.getName()));
		value.put(FieldType.CARD_ISSUER_BANK.getName(), previous.get(FieldType.CARD_ISSUER_BANK.getName()));
		value.put(FieldType.CARD_ISSUER_COUNTRY.getName(), previous.get(FieldType.CARD_ISSUER_COUNTRY.getName()));
		value.put(FieldType.SURCHARGE_FLAG.getName(), previous.get(FieldType.SURCHARGE_FLAG.getName()));
		value.put(FieldType.PAYMENTS_REGION.getName(), previous.get(FieldType.PAYMENTS_REGION.getName()));
		value.put(FieldType.RECO_CARD_HOLDER_TYPE.getName(), previous.get(FieldType.RECO_CARD_HOLDER_TYPE.getName()));
		value.put(FieldType.RECO_PG_TXN_MESSAGE.getName(), previous.get(FieldType.RECO_PG_TXN_MESSAGE.getName()));
		value.put(FieldType.CREATE_DATE.getName(), DateCreater.defaultCurrentDateTime());
		value.put(FieldType.UPDATE_DATE.getName(), DateCreater.defaultCurrentDateTime());
		if (StringUtils.isEmpty(previous.get(FieldType.DATE_INDEX.getName()))) {
			value.put(FieldType.DATE_INDEX.getName(), "");
		} else {
			value.put(FieldType.DATE_INDEX.getName(), previous.get(FieldType.DATE_INDEX.getName()));
		}
		if (previous.get(FieldType.RECO_TXNTYPE.getName()).equals(TransactionType.SALE.getName())
				&& previous.get(FieldType.STATUS.getName()).equals(StatusType.CAPTURED.getName())) {
			value.put(FieldType.PG_DATE_TIME.getName(), previous.get(FieldType.CREATE_DATE.getName()));
		} else {
			value.put(FieldType.PG_DATE_TIME.getName(),
					fieldsDao.getSaleDate(previous.get(FieldType.PG_REF_NUM.getName())));
		}

		value.put(FieldType.RESPONSE_CODE.getName(), ErrorType.SUCCESS.getResponseCode());
		value.put(FieldType.RESPONSE_MESSAGE.getName(), ErrorType.SUCCESS.getResponseMessage());
		value.put(FieldType.UDF4.getName(), previous.get(FieldType.UDF4.getName()));
		value.put(FieldType.UDF5.getName(), previous.get(FieldType.UDF5.getName()));
		value.put(FieldType.UDF6.getName(), previous.get(FieldType.UDF6.getName()));

		return value;
	}

	/* Set required fields for RECO Transaction */
	protected Map<String, String> createNewRefundRecoObject(Fields fields) throws DatabaseException {
		Fields previous = fields.getPrevious();
		Map<String, String> value = new HashMap<String, String>();
		value.put(FieldType.TXN_ID.getName(), TransactionManager.getNewTransactionId());
		value.put(FieldType.PG_REF_NUM.getName(), fields.get(FieldType.PG_REF_NUM.getName()));
		value.put(FieldType.ORIG_TXN_ID.getName(), previous.get(FieldType.ORIG_TXN_ID.getName()));

		if (previous.get(FieldType.ORIG_TXNTYPE.getName()) != null) {
			value.put(FieldType.ORIG_TXNTYPE.getName(), previous.get(FieldType.ORIG_TXNTYPE.getName()));
		} else {
			value.put(FieldType.ORIG_TXNTYPE.getName(), previous.get(FieldType.RECO_TXNTYPE.getName()));
		}

		value.put(FieldType.RECO_AMOUNT.getName(), amount.formatAmount(previous.get(FieldType.RECO_AMOUNT.getName()),
				previous.get(FieldType.CURRENCY_CODE.getName())));
		value.put(FieldType.RECO_TOTAL_AMOUNT.getName(), amount.formatAmount(
				previous.get(FieldType.RECO_TOTAL_AMOUNT.getName()), previous.get(FieldType.CURRENCY_CODE.getName())));
		value.put(FieldType.ORIG_TXN_ID.getName(), previous.get(FieldType.ORIG_TXN_ID.getName()));
		value.put(FieldType.SALE_AMOUNT.getName(), previous.get(FieldType.SALE_AMOUNT.getName()));
		value.put(FieldType.SALE_TOTAL_AMOUNT.getName(), previous.get(FieldType.SALE_TOTAL_AMOUNT.getName()));
		value.put(FieldType.RECO_PAY_ID.getName(), previous.get(FieldType.RECO_PAY_ID.getName()));
		value.put(FieldType.CURRENCY_CODE.getName(), previous.get(FieldType.CURRENCY_CODE.getName()));
		value.put(FieldType.RECO_ORDER_ID.getName(), previous.get(FieldType.RECO_ORDER_ID.getName()));
		value.put(FieldType.OID.getName(), previous.get(FieldType.OID.getName()));
		value.put(FieldType.ARN.getName(), fields.get(FieldType.ARN.getName()));
		value.put(FieldType.RRN.getName(), fields.get(FieldType.RRN.getName()));
		// value.put(FieldType.RECO_ACQ_ID.getName(),previous.get(FieldType.RECO_ACQ_ID.getName()));
		value.put(FieldType.RECO_ACQ_ID.getName(), fields.get(FieldType.RECO_ACQ_ID.getName()));
		value.put(FieldType.RECO_PAYMENT_TYPE.getName(), previous.get(FieldType.RECO_PAYMENT_TYPE.getName()));
		value.put(FieldType.MOP_TYPE.getName(), previous.get(FieldType.MOP_TYPE.getName()));
		value.put(FieldType.RECO_ACQUIRER_TYPE.getName(), previous.get(FieldType.RECO_ACQUIRER_TYPE.getName()));
		value.put(FieldType.CUST_NAME.getName(), previous.get(FieldType.CUST_NAME.getName()));
		value.put(FieldType.CUST_EMAIL.getName(), previous.get(FieldType.CUST_EMAIL.getName()));
		value.put(FieldType.CARD_MASK.getName(), previous.get(FieldType.CARD_MASK.getName()));
		value.put(FieldType.SURCHARGE_FLAG.getName(), previous.get(FieldType.SURCHARGE_FLAG.getName()));
		value.put(FieldType.PAYMENTS_REGION.getName(), previous.get(FieldType.PAYMENTS_REGION.getName()));
		value.put(FieldType.RECO_CARD_HOLDER_TYPE.getName(), previous.get(FieldType.RECO_CARD_HOLDER_TYPE.getName()));
		value.put(FieldType.RECO_PG_TXN_MESSAGE.getName(), previous.get(FieldType.RECO_PG_TXN_MESSAGE.getName()));
		value.put(FieldType.REQUEST_DATE.getName(), previous.get(FieldType.REQUEST_DATE.getName()));

		value.put(FieldType.ACQUIRER_TDR_SC.getName(), previous.get(FieldType.ACQUIRER_TDR_SC.getName()));
		value.put(FieldType.ACQUIRER_GST.getName(), previous.get(FieldType.ACQUIRER_GST.getName()));
		value.put(FieldType.PG_TDR_SC.getName(), previous.get(FieldType.PG_TDR_SC.getName()));
		value.put(FieldType.PG_GST.getName(), previous.get(FieldType.PG_GST.getName()));

		if (StringUtils.isEmpty(previous.get(FieldType.DATE_INDEX.getName()))) {
			value.put(FieldType.DATE_INDEX.getName(), "");
		} else {
			value.put(FieldType.DATE_INDEX.getName(), previous.get(FieldType.DATE_INDEX.getName()));
		}
		if (previous.get(FieldType.RECO_TXNTYPE.getName()).equals(TransactionType.REFUND.getName())
				&& previous.get(FieldType.STATUS.getName()).equals(StatusType.CAPTURED.getName())) {
			value.put(FieldType.PG_DATE_TIME.getName(), previous.get(FieldType.CREATE_DATE.getName()));
		} else {
			value.put(FieldType.PG_DATE_TIME.getName(),
					fieldsDao.getRefundDate(previous.get(FieldType.PG_REF_NUM.getName())));
		}
		value.put(FieldType.REFUND_ORDER_ID.getName(), previous.get(FieldType.REFUND_ORDER_ID.getName()));
		value.put(FieldType.REFUND_FLAG.getName(), previous.get(FieldType.REFUND_FLAG.getName()));
		if (previous.contains(FieldType.UDF6.getName())) {
			if (!previous.get(FieldType.UDF6.getName()).isEmpty()) {
				value.put(FieldType.UDF6.getName(), Constants.Y_FLAG.getValue());
			}
		}
		if (fields.contains(FieldType.IS_REFUND_TIMEOUT.getName())) {
			if (!fields.get(FieldType.IS_REFUND_TIMEOUT.getName()).isEmpty()) {
				value.put(FieldType.IS_REFUND_TIMEOUT.getName(), Constants.RECO_TRUE.getValue());
			}
		}

		value.put(FieldType.RESPONSE_CODE.getName(), ErrorType.SUCCESS.getResponseCode());
		value.put(FieldType.RESPONSE_MESSAGE.getName(), ErrorType.SUCCESS.getResponseMessage());
		return value;
	}

	/* Set required fields for RECO REPORTING Transaction */
	protected Map<String, String> createNewRecoReportingObject(Fields fields) {
		Map<String, String> value = new HashMap<String, String>();
		Fields previous = fields.getPrevious();

		value.put(FieldType.TXN_ID.getName(), TransactionManager.getNewTransactionId());
		if (previous != null) {
			value.put(FieldType.DB_TXNTYPE.getName(), previous.get(FieldType.RECO_TXNTYPE.getName()));
			value.put(FieldType.DB_PG_REF_NUM.getName(), previous.get(FieldType.PG_REF_NUM.getName()));
			value.put(FieldType.DB_TXN_ID.getName(), previous.get(FieldType.TXN_ID.getName()));
			value.put(FieldType.DB_OID.getName(), previous.get(FieldType.OID.getName()));
			value.put(FieldType.DB_ORIG_TXN_ID.getName(), previous.get(FieldType.ORIG_TXN_ID.getName()));
			value.put(FieldType.DB_ORIG_TXNTYPE.getName(), previous.get(FieldType.ORIG_TXNTYPE.getName()));
			value.put(FieldType.DB_PG_REF_NUM.getName(), previous.get(FieldType.PG_REF_NUM.getName()));
			value.put(FieldType.DB_AMOUNT.getName(), previous.get(FieldType.RECO_AMOUNT.getName()));
			value.put(FieldType.DB_ORDER_ID.getName(), previous.get(FieldType.RECO_ORDER_ID.getName()));
			value.put(FieldType.DB_PAY_ID.getName(), previous.get(FieldType.RECO_PAY_ID.getName()));
			value.put(FieldType.DB_ACQUIRER_TYPE.getName(), previous.get(FieldType.RECO_ACQUIRER_TYPE.getName()));
			value.put(FieldType.DB_ACQ_ID.getName(), previous.get(FieldType.RECO_ACQ_ID.getName()));
			value.put(FieldType.DB_TRANSACTION_STATUS.getName(), previous.get(FieldType.STATUS.getName()));
		} else {
			value.put(FieldType.DB_PG_REF_NUM.getName(), fields.get(FieldType.PG_REF_NUM.getName()));
		}
		value.put(FieldType.FILE_LINE_NO.getName(), String.valueOf(fields.get(FieldType.FILE_LINE_NO.getName())));
		value.put(FieldType.FILE_LINE_DATA.getName(), fields.get(FieldType.FILE_LINE_DATA.getName()));
		value.put(FieldType.RESPONSE_CODE.getName(), fields.get(FieldType.RESPONSE_CODE.getName()));
		value.put(FieldType.RESPONSE_MESSAGE.getName(), fields.get(FieldType.RESPONSE_MESSAGE.getName()));
		value.put(FieldType.DB_USER_TYPE.getName(), fields.get(FieldType.USER_TYPE.getName()));
		value.put(FieldType.RECO_EXCEPTION_STATUS.getName(), Constants.EXCEPTION_STATUS.getValue());

		String fileName = StepExecutor.getFileName();
		String name = fileName != null
				? fileName.substring(fileName.lastIndexOf(Constants.FORWARD_SLASH_SEPERATOR.getValue()) + 1)
				: null;
		value.put(FieldType.FILE_NAME.getName(), name);

		return value;
	}

	/* Set required fields for RECO REPORTING Transaction */
	protected Map<String, String> createNewRefundRecoReportingObject(Fields fields) {
		Map<String, String> value = new HashMap<String, String>();
		Fields previous = fields.getPrevious();

		value.put(FieldType.TXN_ID.getName(), TransactionManager.getNewTransactionId());
		if (previous != null) {
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
			value.put(FieldType.DB_REFUND_FLAG.getName(), previous.get(FieldType.REFUND_FLAG.getName()));
			value.put(FieldType.DB_TRANSACTION_STATUS.getName(), previous.get(FieldType.STATUS.getName()));

		} else {
			value.put(FieldType.DB_PG_REF_NUM.getName(), fields.get(FieldType.PG_REF_NUM.getName()));
		}

		value.put(FieldType.FILE_LINE_NO.getName(), String.valueOf(fields.get(FieldType.FILE_LINE_NO.getName())));
		value.put(FieldType.FILE_LINE_DATA.getName(), fields.get(FieldType.FILE_LINE_DATA.getName()));
		value.put(FieldType.RESPONSE_CODE.getName(), fields.get(FieldType.RESPONSE_CODE.getName()));
		value.put(FieldType.RESPONSE_MESSAGE.getName(), fields.get(FieldType.RESPONSE_MESSAGE.getName()));
		value.put(FieldType.DB_USER_TYPE.getName(), fields.get(FieldType.USER_TYPE.getName()));
		value.put(FieldType.RECO_EXCEPTION_STATUS.getName(), Constants.EXCEPTION_STATUS.getValue());

		String fileName = StepExecutor.getFileName();
		String name = fileName != null
				? fileName.substring(fileName.lastIndexOf(Constants.FORWARD_SLASH_SEPERATOR.getValue()) + 1)
				: null;
		value.put(FieldType.FILE_NAME.getName(), name);

		return value;
	}
	// Code added to implement force captured functionality
		protected boolean isStatusMatched(Fields fields, String status) throws DatabaseException {
			Fields previous = fields.getPrevious();

			logger.info("current status :: " + status);
			logger.info("previous status :: " + previous.get(FieldType.STATUS.getName()));
			if (previous != null) {
				if (status != null
						&& previous.get(FieldType.STATUS.getName()).equalsIgnoreCase(StatusType.CAPTURED.getName())
						&& status.equalsIgnoreCase(previous.get(FieldType.STATUS.getName()))) {
							return true;
			}
		}
//		fields.put(FieldType.RESPONSE_CODE.getName(), ErrorType.MISMATCH_STATUS.getResponseCode());
//		fields.put(FieldType.RESPONSE_MESSAGE.getName(), ErrorType.MISMATCH_STATUS.getResponseMessage());
//		fields.put(FieldType.USER_TYPE.getName(), Constants.BANK.getValue());
		return false;
	}
}
