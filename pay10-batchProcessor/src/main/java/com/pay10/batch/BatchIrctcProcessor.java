package com.pay10.batch;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.bson.Document;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.beans.factory.annotation.Autowired;

import com.mongodb.BasicDBObject;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import com.pay10.batch.commons.Fields;
import com.pay10.batch.commons.util.Amount;
import com.pay10.batch.commons.util.ConfigurationProvider;
import com.pay10.batch.commons.util.DateCreater;
import com.pay10.batch.commons.util.GeneralValidator;
import com.pay10.batch.commons.util.MongoInstance;
import com.pay10.batch.commons.util.StepExecutor;
import com.pay10.batch.commons.util.TransactionManager;
import com.pay10.batch.exception.DatabaseException;
import com.pay10.batch.exception.SystemException;
import com.pay10.batch.refund.RefundFileValidator;
import com.pay10.commons.api.Hasher;
import com.pay10.commons.util.Constants;
import com.pay10.commons.util.FieldType;
import com.pay10.commons.util.TransactionType;

/**
 * Process the provided item {@link Fields} object, Step1: performing validation
 * using {@link GeneralValidator}. Step2: calling history processor to get
 * previous data using TXN_ID. Step3: validating input data to previous data.
 * Step3: if valid, setting previous data to {@link Fields} object. Step4:
 * returning modified {@link Fields} object.
 * 
 * @param {@link
 * 			Fields} object to be processed.
 * @return modified {@link Fields} object for continued processing.
 */
public class BatchIrctcProcessor implements ItemProcessor<Fields, Fields> {
	private static Logger logger = Logger.getLogger(BatchIrctcProcessor.class.getName());

	@Autowired
	private MongoInstance mongoInstance;
	@Autowired
	private RefundCommunicator refundCommunicatior;
	@Autowired
	private Fields recoObject;

	@Autowired
	private ConfigurationProvider configProvider;

	@Autowired
	private Amount amount;
	
	@Autowired
	private RefundFileValidator fileValidator;

	/*public void setFileValidator(RefundFileValidator fileValidator) {
		this.fileValidator = fileValidator;
	}*/

	/*
	 * Process the {@link Fields} item performing validation using {@link
	 * GeneralValidator}
	 */
	@Override

	public Fields process(Fields fields) throws SystemException, DatabaseException, com.pay10.commons.exception.SystemException {
		GeneralValidator generalValidator = new GeneralValidator();
		//FileValidator fileValidator = new FileValidator();
		if(fields.contains(FieldType.RECO_REFUNDAMOUNT.getName())) {
			fields.put(FieldType.RECO_REFUNDAMOUNT.getName(), amount.convertToDecimal(fields.get(FieldType.RECO_REFUNDAMOUNT.getName()), getCurrencyCode(fields.get(FieldType.PG_REF_NUM.getName()))));
		}
		generalValidator.validateRecoRefund(fields);
		fields.put(FieldType.RECO_TXNTYPE.getName(), TransactionType.REFUND.getName());
		fields.put(FieldType.RECO_PAY_ID.getName(), getPayId(fields.get(FieldType.PG_REF_NUM.getName())));
		fields.put(FieldType.CURRENCY_CODE.getName(), getCurrencyCode(fields.get(FieldType.PG_REF_NUM.getName())));
		String newTxnId = TransactionManager.getNewTransactionId();
		fields.put(FieldType.TXN_ID.getName(), newTxnId);
		if (fileValidator.isDataValid(fields)) {
			if(Boolean.parseBoolean(fields.get(FieldType.IS_RECO_SETTLED.getName())) == true) {
				JSONObject refundValue = createNewRefundIrctcObject(fields);
				String response = refundCommunicatior.communicator(refundValue, fields);
				logger.info("refund API  response received from pg ws " + response);
			} /*else {
				if(fields.contains(FieldType.IS_REFUND_PENDING.getName()) == false) {
					recoObject.setFields(createNewRefundPendingIrctcObject(fields));
					recoObject.insertNewRecoRecord();
				}
			}*/
		} else {
			recoObject.setFields(createNewRecoReportingObject(fields));
			recoObject.insertNewRecoReportingRecord();
	    }		

		return null;
	}

	// Set required fields for RefundIrctc Transaction
	protected JSONObject createNewRefundIrctcObject(Fields fields) throws JSONException, com.pay10.commons.exception.SystemException {
		JSONObject value = new JSONObject();
		value.put(FieldType.RECO_ORDER_ID.getName(), fields.get(FieldType.RECO_ORDER_ID.getName()));
		value.put(FieldType.REFUND_FLAG.getName(), fields.get(FieldType.REFUND_FLAG.getName()));
		value.put(FieldType.RECO_AMOUNT.getName(), (amount.formatAmount(fields.get(FieldType.RECO_REFUNDAMOUNT.getName()), fields.get(FieldType.CURRENCY_CODE.getName()))));
		//value.put(FieldType.RECO_TOTAL_AMOUNT.getName(), (amount.formatAmount(fields.get(FieldType.RECO_BOOKING_AMOUNT.getName()), fields.get(FieldType.CURRENCY_CODE.getName()))));
		value.put(FieldType.PG_REF_NUM.getName(), fields.get(FieldType.PG_REF_NUM.getName()));
		value.put(FieldType.REFUND_ORDER_ID.getName(), fields.get(FieldType.REFUND_ORDER_ID.getName()));
		value.put(FieldType.CURRENCY_CODE.getName(), fields.get(FieldType.CURRENCY_CODE.getName()));
		value.put(FieldType.RECO_TXNTYPE.getName(), TransactionType.REFUND.getName());
		value.put(FieldType.RECO_PAY_ID.getName(), fields.get(FieldType.RECO_PAY_ID.getName()));
		value.put(FieldType.REQUEST_DATE.getName(), DateCreater.dateformatCreater(fields.get(FieldType.REFUND_DATE_TIME.getName())));
		if(StringUtils.isNotEmpty(fields.get(FieldType.RECO_ACQ_ID.getName()))) {
			value.put(FieldType.RECO_ACQ_ID.getName(), fields.get(FieldType.RECO_ACQ_ID.getName()));
		}
		value.put(FieldType.HASH.getName(), Hasher.getHash(fields.get(FieldType.TXN_ID.getName())));
		/*RefundRequestCreater refundRequestCreater = new RefundRequestCreater();
		value = refundRequestCreater.createNewRefundIrctcObject(fields);*/
		return value;
	}
	
	/*protected Map<String, String> createNewRefundPendingIrctcObject(Fields fields) {
		Map<String, String> value = new HashMap<String, String>();
		Fields previous = fields.getPrevious();
		value.put(FieldType.RECO_TXNTYPE.getName(),TransactionType.REFUND.getName());
		value.put(FieldType.STATUS.getName(),StatusType.PENDING.getName());
		value.put(FieldType.TXN_ID.getName(), TransactionManager.getNewTransactionId());
		value.put(FieldType.PG_REF_NUM.getName(), fields.get(FieldType.PG_REF_NUM.getName()));
		value.put(FieldType.ORIG_TXN_ID.getName(), previous.get(FieldType.ORIG_TXN_ID.getName()));
		value.put(FieldType.ORIG_TXNTYPE.getName(), previous.get(FieldType.ORIG_TXNTYPE.getName()));
		value.put(FieldType.RECO_AMOUNT.getName(), amount.formatAmount(fields.get(FieldType.RECO_REFUNDAMOUNT.getName()), previous.get(FieldType.CURRENCY_CODE.getName())));
		value.put(FieldType.RECO_TOTAL_AMOUNT.getName(),amount.formatAmount(fields.get(FieldType.RECO_REFUNDAMOUNT.getName()), previous.get(FieldType.CURRENCY_CODE.getName())));
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
		value.put(FieldType.REFUND_FLAG.getName(), fields.get(FieldType.REFUND_FLAG.getName()));
		value.put(FieldType.REFUND_ORDER_ID.getName(), fields.get(FieldType.REFUND_ORDER_ID.getName()));
		value.put(FieldType.PG_DATE_TIME.getName(), previous.get(FieldType.PG_DATE_TIME.getName()));
		if(previous.get(FieldType.PG_DATE_TIME.getName()) != null) {
			value.put(FieldType.PG_DATE_TIME.getName(), DateCreater.formatDateForReco(previous.get(FieldType.PG_DATE_TIME.getName()), Constants.SETTLE_DATE_FORMAT_DB.getValue()));
		}
		
		value.put(FieldType.RESPONSE_CODE.getName(), ErrorType.RECO_SUCCESS.getResponseCode());
		value.put(FieldType.RESPONSE_MESSAGE.getName(), ErrorType.RECO_SUCCESS.getResponseMessage());
		return value; 
	}*/
	
	/*Set required fields for RECO REPORTING Transaction*/
	protected Map<String, String> createNewRecoReportingObject(Fields fields) {
		Map<String, String> value = new HashMap<String, String>();
		Fields previous = fields.getPrevious();

		value.put(FieldType.RECO_TXNTYPE.getName(), TransactionType.REFUND.getName());
		value.put(FieldType.TXN_ID.getName(), TransactionManager.getNewTransactionId());
		if(previous != null) {
			value.put(FieldType.DB_TXNTYPE.getName(), previous.get(FieldType.RECO_TXNTYPE.getName()));
			value.put(FieldType.DB_PG_REF_NUM.getName(), previous.get(FieldType.PG_REF_NUM.getName()));
			value.put(FieldType.DB_TXN_ID.getName(), previous.get(FieldType.TXN_ID.getName()));
			value.put(FieldType.DB_OID.getName(), previous.get(FieldType.OID.getName()));
			value.put(FieldType.DB_ORIG_TXNTYPE.getName(), previous.get(FieldType.ORIG_TXN_ID.getName()));
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

	private String getPayId(String pgRefNum) {

		BasicDBObject finalquery = new BasicDBObject(FieldType.PG_REF_NUM.getName(), pgRefNum);

		MongoDatabase dbIns = mongoInstance.getDB();

		MongoCollection<Document> coll = dbIns.getCollection(configProvider.getTransactionColl());
		MongoCursor<Document> cursor = coll.find(finalquery).iterator();
		String payId = "";
		while (cursor.hasNext()) {
			Document doc = cursor.next();
			payId = doc.getString(FieldType.RECO_PAY_ID.toString());
			break;
		}
		return payId;
	}
	
	private String getCurrencyCode(String pgRefNum) {

		BasicDBObject finalquery = new BasicDBObject(FieldType.PG_REF_NUM.getName(), pgRefNum);

		MongoDatabase dbIns = mongoInstance.getDB();

		MongoCollection<Document> coll = dbIns.getCollection(configProvider.getTransactionColl());
		MongoCursor<Document> cursor = coll.find(finalquery).iterator();
		String currencyCode = "";
		while (cursor.hasNext()) {
			Document doc = cursor.next();
			currencyCode = doc.getString(FieldType.CURRENCY_CODE.toString());
			break;
		}
		return currencyCode;
	}

	}
