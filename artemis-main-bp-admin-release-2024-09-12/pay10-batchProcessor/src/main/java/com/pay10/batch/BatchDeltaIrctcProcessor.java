package com.pay10.batch;

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
import com.pay10.batch.commons.util.GeneralValidator;
import com.pay10.batch.commons.util.MongoInstance;
import com.pay10.batch.commons.util.TransactionManager;
import com.pay10.batch.exception.DatabaseException;
import com.pay10.batch.exception.SystemException;
import com.pay10.commons.api.Hasher;
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
public abstract class BatchDeltaIrctcProcessor implements ItemProcessor<Fields, Fields> {
	private static Logger logger = Logger.getLogger(BatchDeltaIrctcProcessor.class.getName());

	@Autowired
	private MongoInstance mongoInstance;
	@Autowired
	private DeltaRefundCommunicator deltaRefundCommunicator;
	
	@Autowired
	private ConfigurationProvider configProvider;
	
	@Autowired
	private Fields recoObject;
	
	@Autowired
	private Amount amount;

	private FileValidator fileValidator;

	public void setFileValidator(FileValidator fileValidator) {
		this.fileValidator = fileValidator;
	}

	/*
	 * Process the {@link Fields} item performing validation using {@link
	 * GeneralValidator}
	 */
	@Override

	public Fields process(Fields fields) throws SystemException, DatabaseException, com.pay10.commons.exception.SystemException {
		GeneralValidator generalValidator = new GeneralValidator();
		if(fields.contains(FieldType.RECO_REFUNDAMOUNT.getName())) {
			fields.put(FieldType.RECO_REFUNDAMOUNT.getName(), amount.convertToDecimal(fields.get(FieldType.RECO_REFUNDAMOUNT.getName()), getCurrencyCode(fields.get(FieldType.PG_REF_NUM.getName()))));
		}
		generalValidator.validateRecoRefund(fields);
		fields.put(FieldType.RECO_TXNTYPE.getName(), TransactionType.REFUND.getName());
		fields.put(FieldType.RECO_PAY_ID.getName(), getPayId(fields.get(FieldType.PG_REF_NUM.getName())));
		fields.put(FieldType.CURRENCY_CODE.getName(), getCurrencyCode(fields.get(FieldType.PG_REF_NUM.getName())));
		String newTxnId = TransactionManager.getNewTransactionId();
		fields.put(FieldType.TXN_ID.getName(), newTxnId);
		JSONObject refundValue = createNewRefundIrctcObject(fields);
		String response = deltaRefundCommunicator.deltaCommunicator(refundValue, fields);

		logger.info("DeltaRefund API  response received from pg ws " + response);

		return null;
	}

	// Set required fields for RefundIrctc Transaction
	protected JSONObject createNewRefundIrctcObject(Fields fields) throws JSONException, com.pay10.commons.exception.SystemException {
		JSONObject value = new JSONObject();
		value.put(FieldType.RECO_ORDER_ID.getName(), fields.get(FieldType.RECO_ORDER_ID.getName()));
		value.put(FieldType.REFUND_FLAG.getName(), fields.get(FieldType.REFUND_FLAG.getName()));
		value.put(FieldType.RECO_AMOUNT.getName(), (amount.formatAmount(fields.get(FieldType.RECO_REFUNDAMOUNT.getName()), fields.get(FieldType.CURRENCY_CODE.getName()))));
		value.put(FieldType.PG_REF_NUM.getName(), fields.get(FieldType.PG_REF_NUM.getName()));
		value.put(FieldType.REFUND_ORDER_ID.getName(), fields.get(FieldType.REFUND_ORDER_ID.getName()));
		value.put(FieldType.CURRENCY_CODE.getName(), fields.get(FieldType.CURRENCY_CODE.getName()));
		value.put(FieldType.RECO_TXNTYPE.getName(), TransactionType.REFUND.getName());
		value.put(FieldType.UDF6.getName(), fields.get(FieldType.UDF6.getName()));
		value.put(FieldType.RECO_PAY_ID.getName(), fields.get(FieldType.RECO_PAY_ID.getName()));
		value.put(FieldType.HASH.getName(), Hasher.getHash(fields.get(FieldType.TXN_ID.getName())));

		return value;
	}
	
	private String getPayId(String pgRefNum) {			
			
		BasicDBObject finalquery = new BasicDBObject(FieldType.PG_REF_NUM.getName(), pgRefNum);

		MongoDatabase dbIns = mongoInstance.getDB();

		MongoCollection<Document> coll = dbIns.getCollection(configProvider.getTransactionColl());
		MongoCursor<Document> cursor = coll.find(finalquery).iterator();
		String payId  = "";
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
