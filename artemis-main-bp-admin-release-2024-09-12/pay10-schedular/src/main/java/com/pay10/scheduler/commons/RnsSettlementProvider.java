package com.pay10.scheduler.commons;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.bson.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.mongodb.BasicDBObject;
import com.mongodb.BasicDBObjectBuilder;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import com.pay10.commons.mongo.MongoInstance;
import com.pay10.commons.util.FieldType;
import com.pay10.commons.util.StatusType;
import com.pay10.commons.util.TransactionManager;
import com.pay10.commons.util.TransactionType;

@Service
public class RnsSettlementProvider {
	private static final Logger logger = LoggerFactory.getLogger(RnsSettlementProvider.class);

	@Autowired
	private MongoInstance mongoInstance;

	@Autowired
	private ConfigurationProvider configurationProvider;

	public List<Document> fetchCapturedTransactionData() {

		List<Document> documents = new ArrayList<Document>();
		try {

			List<BasicDBObject> paramConditionLst = new ArrayList<BasicDBObject>();
			Calendar cal = Calendar.getInstance();
			DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

			// cal.add(Calendar.DATE, -1);
			BasicDBObject dateTimeQuery = new BasicDBObject();

			String startDate = StringUtils.join(dateFormat.format(cal.getTime()), " 00:00:00");
			String endDate = StringUtils.join(dateFormat.format(cal.getTime()), " 23:59:59");

			dateTimeQuery.put(FieldType.CREATE_DATE.getName(),
					BasicDBObjectBuilder.start("$gt", new SimpleDateFormat(startDate).toLocalizedPattern())
							.add("$lte", new SimpleDateFormat(endDate).toLocalizedPattern()).get());

			paramConditionLst.add(dateTimeQuery);

			String merchantPayId = configurationProvider.getMerchantPayId();
			String acquirer = configurationProvider.getAcquirerName();

			BasicDBObject merchantQuery = new BasicDBObject();
			BasicDBObject acquirerQuery = new BasicDBObject();

			List<BasicDBObject> merchantQueryList = new ArrayList<BasicDBObject>();
			List<BasicDBObject> acquirerQueryList = new ArrayList<BasicDBObject>();

			if (StringUtils.isNotBlank(merchantPayId) && !merchantPayId.equalsIgnoreCase("ALL")) {

				String merchantPayIdArr[] = merchantPayId.split(",");
				for (String merchant : merchantPayIdArr) {
					merchantQueryList.add(new BasicDBObject(FieldType.PAY_ID.getName(), merchant));
				}
			}

			if (StringUtils.isNotBlank(acquirer) && !acquirer.equalsIgnoreCase("ALL")) {

				String acquirerArr[] = acquirer.split(",");
				for (String acq : acquirerArr) {
					acquirerQueryList.add(new BasicDBObject(FieldType.ACQUIRER_TYPE.getName(), acq));
				}

			}

			merchantQuery.put("$or", merchantQueryList);
			acquirerQuery.put("$or", acquirerQueryList);

			if (StringUtils.isNotBlank(merchantPayId) && !merchantPayId.equalsIgnoreCase("ALL")) {
				paramConditionLst.add(merchantQuery);
			}

			if (StringUtils.isNotBlank(acquirer) && !acquirer.equalsIgnoreCase("ALL")) {
				paramConditionLst.add(acquirerQuery);
			}

			paramConditionLst.add(new BasicDBObject(FieldType.STATUS.getName(), StatusType.CAPTURED.getName()));
			paramConditionLst.add(new BasicDBObject(FieldType.TXNTYPE.getName(), TransactionType.SALE.getName()));

			BasicDBObject finalquery = new BasicDBObject("$and", paramConditionLst);

			logger.info("fetchCapturedTransactionData, finalquery : " + finalquery.toString());
			MongoDatabase dbIns = mongoInstance.getDB();
			MongoCollection<Document> collection = dbIns.getCollection("transactionStatus");
			MongoCursor<Document> result = collection.find(finalquery).iterator();

			while (result.hasNext()) {
				Document document = (Document) result.next();
				documents.add(document);
			}

			logger.info("fetchCapturedTransactionData:: captured docs size={} " + documents.size());
		} catch (Exception e) {
			logger.error("fetchCapturedTransactionData:: failed.", e);
		}
		return documents;

	}

	public boolean makeRnsFromCaptured(Document requestDoc) {
		try {
			DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			
			MongoDatabase dbIns = null;
			requestDoc.put("_id", TransactionManager.getNewTransactionId());
			requestDoc.put(FieldType.STATUS.getName(), StatusType.SETTLED_RECONCILLED.getName());
			requestDoc.put(FieldType.TXNTYPE.getName(), TransactionType.RECO.getName());
			requestDoc.put(FieldType.CREATE_DATE.getName(), dateFormat.format(new Date()));
			dbIns = mongoInstance.getDB();
			MongoCollection<Document> collection = dbIns.getCollection("transaction");
			collection.insertOne(requestDoc);
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	public boolean updateRnsFromCaptured(Document requestDoc) {
		try {
			MongoDatabase dbIns = null;
			dbIns = mongoInstance.getDB();
			MongoCollection<Document> collection = dbIns.getCollection("transactionStatus");
			List<BasicDBObject> query = new ArrayList<>();

			query.add(new BasicDBObject(FieldType.ORDER_ID.getName(), requestDoc.get(FieldType.ORDER_ID.getName())));
			query.add(new BasicDBObject(FieldType.STATUS.getName(), StatusType.CAPTURED.getName()));
			query.add(new BasicDBObject(FieldType.TXNTYPE.getName(), TransactionType.SALE.getName()));
			query.add(new BasicDBObject(FieldType.PG_REF_NUM.getName(), requestDoc.get(FieldType.PG_REF_NUM.getName())));

			BasicDBObject newFieldsObj = new BasicDBObject("$and", query);
			FindIterable<Document> output = collection.find(newFieldsObj);
			MongoCursor<Document> cursor = output.iterator();
			Document doc = null;
			int count = 0;
			while (cursor.hasNext()) {
				doc = cursor.next();
				++count;
			}
			cursor.close();
			if (count > 1) {
				logger.info("Unable to update transaction. Found multiple txns with same OrderId="+requestDoc.get(FieldType.ORDER_ID.getName()));
				return false;
			}

			if (doc == null) {
				logger.info("Unable to find transaction, OrderId="+requestDoc.get(FieldType.ORDER_ID.getName()));
				return false;
			}

			doc.put(FieldType.STATUS.getName(), StatusType.SETTLED_RECONCILLED.getName());
			doc.put(FieldType.TXNTYPE.getName(), TransactionType.RECO.getName());
			collection.replaceOne(newFieldsObj, doc);
		} catch (Exception exception) {
			String message = "Error while inserting transaction in database";
			logger.error(message, exception);
		}
		return true;
	}

	public List<Document> fetchRNSTransactionData() {
		List<Document> documents = new ArrayList<Document>();
		try {

			List<BasicDBObject> paramConditionLst = new ArrayList<BasicDBObject>();
			Calendar cal = Calendar.getInstance();
			DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

			// cal.add(Calendar.DATE, -1);
			BasicDBObject dateTimeQuery = new BasicDBObject();

			String startDate = StringUtils.join(dateFormat.format(cal.getTime()), " 00:00:00");
			String endDate = StringUtils.join(dateFormat.format(cal.getTime()), " 23:59:59");

			dateTimeQuery.put(FieldType.CREATE_DATE.getName(),
					BasicDBObjectBuilder.start("$gt", new SimpleDateFormat(startDate).toLocalizedPattern())
							.add("$lte", new SimpleDateFormat(endDate).toLocalizedPattern()).get());

			paramConditionLst.add(dateTimeQuery);

			paramConditionLst.add(new BasicDBObject(FieldType.STATUS.getName(), StatusType.SETTLED_RECONCILLED.getName()));
			paramConditionLst.add(new BasicDBObject(FieldType.TXNTYPE.getName(), TransactionType.RECO.getName()));
			//paramConditionLst.add(new BasicDBObject(FieldType.HOLD_RELEASE.getName(), 0));

			BasicDBObject finalquery = new BasicDBObject("$and", paramConditionLst);

			logger.info("fetchRNSTransactionData, finalquery : " + finalquery.toString());
			MongoDatabase dbIns = mongoInstance.getDB();
			MongoCollection<Document> collection = dbIns.getCollection("transactionStatus");
			MongoCursor<Document> result = collection.find(finalquery).iterator();

			while (result.hasNext()) {
				Document document = (Document) result.next();
				
				if (StringUtils.isNotBlank(String.valueOf(document.get(FieldType.HOLD_RELEASE.getName()))) 
						&& null != String.valueOf(document.get(FieldType.HOLD_RELEASE.getName()))
						&& String.valueOf(document.get(FieldType.HOLD_RELEASE.getName())).equalsIgnoreCase("1")) {
					logger.info("Transaction is not settled due to transaction in hold state, PG_REF_NUM={},ORDER_ID={}",document.getString(FieldType.PG_REF_NUM.getName()),document.getString(FieldType.ORDER_ID.getName()));
					continue;
				}
				documents.add(document);
			}

			logger.info("fetchRNSTransactionData:: Reconcilled But Not Settled docs size={} " + documents.size());
		} catch (Exception e) {
			logger.error("fetchRNSTransactionData:: failed.", e);
		}
		return documents;
	}

	public String makeSettedFromRns(Document requestDoc) {
		try {
			DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			DateFormat dateIndexFormat = new SimpleDateFormat("yyyyMMdd");
			Date date = new Date();
			MongoDatabase dbIns = null;
			String rrn = TransactionManager.getNewTransactionId();
			requestDoc.put("_id", TransactionManager.getNewTransactionId());
			requestDoc.put(FieldType.STATUS.getName(), StatusType.SETTLED_SETTLE.getName());
			requestDoc.put(FieldType.TXNTYPE.getName(), TransactionType.RECO.getName());
			requestDoc.put(FieldType.SETTLEMENT_DATE.getName(), dateFormat.format(date));
			requestDoc.put(FieldType.SETTLEMENT_DATE_INDEX.getName(), dateIndexFormat.format(date));
			requestDoc.put(FieldType.ALIAS_STATUS.getName(), StatusType.SETTLED_SETTLE.getName());
			requestDoc.put(FieldType.CREATE_DATE.getName(), dateFormat.format(date));
			requestDoc.put(FieldType.RRN.getName(), rrn);
			dbIns = mongoInstance.getDB();
			MongoCollection<Document> collection = dbIns.getCollection("transaction");
			collection.insertOne(requestDoc);
			return rrn;
		} catch (Exception e) {
			return null;
		}
	}

	public boolean updateSettedFromRns(Document requestDoc, String rrn) {
		try {
			DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			DateFormat dateIndexFormat = new SimpleDateFormat("yyyyMMdd");
			Date date = new Date();
			MongoDatabase dbIns = null;
			dbIns = mongoInstance.getDB();
			MongoCollection<Document> collection = dbIns.getCollection("transactionStatus");
			List<BasicDBObject> query = new ArrayList<>();

			query.add(new BasicDBObject(FieldType.ORDER_ID.getName(), requestDoc.get(FieldType.ORDER_ID.getName())));
			query.add(new BasicDBObject(FieldType.STATUS.getName(), StatusType.SETTLED_RECONCILLED.getName()));
			query.add(new BasicDBObject(FieldType.TXNTYPE.getName(), TransactionType.RECO.getName()));
			query.add(new BasicDBObject(FieldType.PG_REF_NUM.getName(), requestDoc.get(FieldType.PG_REF_NUM.getName())));
			BasicDBObject newFieldsObj = new BasicDBObject("$and", query);
			FindIterable<Document> output = collection.find(newFieldsObj);
			MongoCursor<Document> cursor = output.iterator();
			Document doc = null;
			int count = 0;
			while (cursor.hasNext()) {
				doc = cursor.next();
				++count;
			}
			cursor.close();
			if (count > 1) {
				logger.info("Unable to update transaction. Found multiple txns with same OrderId="+requestDoc.get(FieldType.ORDER_ID.getName()));
				return false;
			}

			if (doc == null) {
				logger.info("Unable to find transaction, OrderId="+requestDoc.get(FieldType.ORDER_ID.getName()));
				return false;
			}

			doc.put(FieldType.STATUS.getName(), StatusType.SETTLED_SETTLE.getName());
			doc.put(FieldType.SETTLEMENT_DATE.getName(), dateFormat.format(new Date()));
			doc.put(FieldType.SETTLEMENT_DATE_INDEX.getName(), dateIndexFormat.format(date));
			doc.put(FieldType.ALIAS_STATUS.getName(), StatusType.SETTLED_SETTLE.getName());
			doc.put(FieldType.RRN.getName(), rrn);

			//doc.put(FieldType.TXNTYPE.getName(), TransactionType.RECO.getName());
			collection.replaceOne(newFieldsObj, doc);
		} catch (Exception exception) {
			String message = "Error while inserting transaction in database";
			logger.error(message, exception);
		}
		return true;
	}
	
	public static void main(String[] args) {
		DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		System.out.println("-- "+dateFormat.format(new Date()));
	}

}
