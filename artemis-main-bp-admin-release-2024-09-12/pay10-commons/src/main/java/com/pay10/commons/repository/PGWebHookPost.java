package com.pay10.commons.repository;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.bson.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.mongodb.BasicDBObject;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.MongoCredential;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import com.pay10.commons.entity.PGWebHookPostConfigURL;
import com.pay10.commons.mongo.MongoInstance;
import com.pay10.commons.user.PGWebHookPostConfigDao;
import com.pay10.commons.util.Constants;
import com.pay10.commons.util.FieldType;
import com.pay10.commons.util.PasswordGenerator;
import com.pay10.commons.util.PropertiesManager;
import com.pay10.commons.util.StatusType;

@Component("PGWebHookPost")
public class PGWebHookPost {

//	private static Logger logger = LoggerFactory.getLogger(PGWebHookPost.class.getName());
//	@Autowired
//	private MongoInstance mongoInstance;
//	private static final String prefix = "MONGO_DB_";
//
//	@Autowired
//	private PGWebHookPostConfigDao pgWebHookPostConfigDao;
//	
//
//	public List<Document> fetchPGWebHookDetails(String webhookFailedCnt, String webhooktype) {
//		List<PGWebHookPostConfigURL> pgWebHookPostConfigURL = pgWebHookPostConfigDao.fetchPGWebHookPostConfigData(webhooktype);
//		List<Document> documentsList = new ArrayList<Document>();
//		logger.info("fetchPGWebHookDetails, pgWebHookPostEntities={}" + pgWebHookPostConfigURL.toString());
//		try {
//			List<String> acquirerPayId = new ArrayList<>();
//			pgWebHookPostConfigURL.stream().forEach(rule -> acquirerPayId.add(rule.getPayId()));
//
//			List<BasicDBObject> paramConditionLst = new ArrayList<BasicDBObject>();
//			if (acquirerPayId != null) {
//				paramConditionLst
//						.add(new BasicDBObject(FieldType.PAY_ID.getName(), new BasicDBObject("$in", acquirerPayId)));
//			}
//			
//			List<String> txnStatus = new ArrayList<>();
//			txnStatus.add(StatusType.CAPTURED.getName());
//			txnStatus.add(StatusType.FAILED.getName());
//			txnStatus.add(StatusType.FAILED_AT_ACQUIRER.getName());
//			txnStatus.add(StatusType.SUCCESS.getName());
//			txnStatus.add(StatusType.SETTLED_SETTLE.getName());
//			txnStatus.add(StatusType.REJECTED.getName());
//			txnStatus.add(StatusType.INVALID.getName());
//			txnStatus.add(StatusType.ERROR.getName());
//			txnStatus.add(StatusType.SETTLED_RECONCILLED.getName());
//
//			if (txnStatus != null) {
//				paramConditionLst.add(new BasicDBObject(FieldType.STATUS.getName(), new BasicDBObject("$in", txnStatus)));
//			}
//
//			paramConditionLst.add(new BasicDBObject(FieldType.WEBHOOK_POST_FLAG.getName(), "N"));
//
//			BasicDBObject failedCntQuery = new BasicDBObject();
//			failedCntQuery.append("$lt", webhookFailedCnt);
//			BasicDBObject failedCntQuery2 = new BasicDBObject(FieldType.WEBHOOK_FAILED_COUNT.getName(), failedCntQuery);
//			paramConditionLst.add(failedCntQuery2);
//
//			BasicDBObject finalquery = new BasicDBObject("$and", paramConditionLst);
//
//			logger.info("Fetch Data for Posting Webhook Response To Merchant, finalquery={}", finalquery.toString());
//			MongoDatabase dbIns = mongoInstance.getDB();
//			
//			MongoCollection<Document> collection;
//			if(webhooktype.equalsIgnoreCase("PAYIN")) {
//				collection = dbIns.getCollection("transactionStatus");
//			}else {
//				collection = dbIns.getCollection("POtransactionStatus");
//			}
//			
//			MongoCursor<Document> iterator = collection.find(finalquery).iterator();
//
//			while (iterator.hasNext()) {
//				Document document = iterator.next();
//				documentsList.add(document);
//			}
//
//		} catch (Exception e) {
//			logger.error("ERROR OCCURED, fetchPGWebHookDetails, Exception={}", e);
//		}
//		return documentsList;
//	}
//
//	public PGWebHookPostConfigURL fetchPGWebHookPostConfigURLByPayId(String payId, String webhooktype) {
//		return pgWebHookPostConfigDao.fetchPGWebHookPostConfigURLByPayId(payId, webhooktype);
//	}
//
//	public void updateFlagAndFailedCount(Document doc, Boolean responseData, String webhookType) {
//		logger.info("Webhook Update FailedCount and UpdateFlage, PG_REF_NUM={}, ORDER_ID={}, WebhookResponse={}, webhookType={}",doc.get(FieldType.PG_REF_NUM.getName()),doc.get(FieldType.ORDER_ID.getName()), responseData, webhookType);
//		try {
//			MongoCollection<Document> coll;
//
//			if(webhookType.equalsIgnoreCase("PAYIN")) {
//				coll = mongoInstance.getDB().getCollection(PropertiesManager.propertiesMap.get(prefix + Constants.TRANSACTION_STATUS_COLLECTION.getValue()));
//			}else {
//				coll = mongoInstance.getDB().getCollection("POtransactionStatus");
//			}
//			List<BasicDBObject> dbObjList = new ArrayList<BasicDBObject>();
//			//dbObjList.add(new BasicDBObject("_id", doc.get("_id")));
//			dbObjList.add(new BasicDBObject(FieldType.PG_REF_NUM.getName(), doc.get(FieldType.PG_REF_NUM.getName())));
//			dbObjList.add(new BasicDBObject(FieldType.ORDER_ID.getName(), doc.get(FieldType.ORDER_ID.getName())));
//			//dbObjList.add(new BasicDBObject(FieldType.TXNTYPE.getName(), doc.get(FieldType.TXNTYPE.getName())));
//			BasicDBObject andQuery = new BasicDBObject("$and", dbObjList);
//			logger.info("Webhook Update FailedCount and UpdateFlage,  final query for, webhookType={}, update={} ",webhookType, andQuery.toString());
//			MongoCursor<Document> iterator = coll.find(andQuery).iterator();
//			
//			if (iterator.hasNext()) {
//				Document document = iterator.next();
//				
////				String transactionStatusFields = PropertiesManager.propertiesMap.get("TransactionStatusFields");
////				String transactionStatusFieldsArr[] = transactionStatusFields.split(",");
//
//				
//				MongoCollection<Document> updateColl;
//
//				if(webhookType.equalsIgnoreCase("PAYIN")) {
//					updateColl = mongoInstance.getDB().getCollection(PropertiesManager.propertiesMap.get(prefix + Constants.TRANSACTION_STATUS_COLLECTION.getValue()));
//				}else {
//					updateColl = mongoInstance.getDB().getCollection("POtransactionStatus");
//				}
//				
//				BasicDBObject searchQuery = andQuery;
//				//BasicDBObject updateFields = new BasicDBObject();
//
////				for (String key : transactionStatusFieldsArr) {
////
////					if ((key.equalsIgnoreCase(FieldType.DATE_INDEX.getName()))
////							|| (key.equalsIgnoreCase(FieldType.CREATE_DATE.getName()))
////							|| (key.equalsIgnoreCase(FieldType.UPDATE_DATE.getName()))
////							|| (key.equalsIgnoreCase(FieldType.INSERTION_DATE.getName()))) {
////						continue;
////					}
////
////					if (document.get(key) != null) {
////						updateFields.put(key, document.get(key).toString());
////					} else {
////						updateFields.put(key, document.get(key));
////					}
////
////				}
//				
//				if (responseData) {
//					if (StringUtils.isNotBlank(document.getString(FieldType.WEBHOOK_FAILED_COUNT.getName()))) {
//						long updatedfailCount = Long.parseLong(document.getString(FieldType.WEBHOOK_FAILED_COUNT.getName()));
//						updatedfailCount++;
//						document.append(FieldType.WEBHOOK_FAILED_COUNT.getName(), String.valueOf(updatedfailCount));
//					} else {
//						document.append(FieldType.WEBHOOK_FAILED_COUNT.getName(), "1");
//					}
//
//					if (StringUtils.isNotBlank(document.getString(FieldType.WEBHOOK_POST_FLAG.getName()))) {
//						document.append(FieldType.WEBHOOK_POST_FLAG.getName(), "Y");
//					} else {
//						document.append(FieldType.WEBHOOK_POST_FLAG.getName(), "Y");
//					}
//				} else {
//
//					if (StringUtils.isNotBlank(document.getString(FieldType.WEBHOOK_FAILED_COUNT.getName()))) {
//						long updatedfailCount = Long.parseLong(document.getString(FieldType.WEBHOOK_FAILED_COUNT.getName()));
//						updatedfailCount++;
//						document.append(FieldType.WEBHOOK_FAILED_COUNT.getName(), String.valueOf(updatedfailCount));
//					} else {
//						document.append(FieldType.WEBHOOK_FAILED_COUNT.getName(), "1");
//					}
//				}
//				
//				updateColl.updateOne(searchQuery, new BasicDBObject("$set", document));
//			}
//
//		} catch (Exception e) {
//			logger.error("ERROR OCCURED While Updating FailedCnt and UpdateFlag for Webhook Response, Exception={}",e);
//		}
//	}

	private static Logger logger = LoggerFactory.getLogger(PGWebHookPost.class.getName());
	@Autowired
	private MongoInstance mongoInstance;
	//private static final String prefix = "MONGO_DB_";
	@Autowired
	private PGWebHookPostConfigDao pgWebHookPostConfigDao;
	private static final String prefix = "MONGO_DB_";

	public List<Document> fetchPGWebHookDetails(String webhookFailedCnt, String webhookType) {
		List<PGWebHookPostConfigURL> pgWebHookPostConfigURL = this.pgWebHookPostConfigDao
				.fetchPGWebHookPostConfigData(webhookType);
		List<Document> documentsList = new ArrayList<Document>();
		logger.info("fetchPGWebHookDetails, pgWebHookPostEntities={}" + pgWebHookPostConfigURL.toString());

		try {
			List<String> acquirerPayId = new ArrayList<String>();
			pgWebHookPostConfigURL.stream().forEach((rule) -> {
				acquirerPayId.add(rule.getPayId());
			});
			List<BasicDBObject> paramConditionLst = new ArrayList<BasicDBObject>();
			if (acquirerPayId != null) {
				paramConditionLst
						.add(new BasicDBObject(FieldType.PAY_ID.getName(), new BasicDBObject("$in", acquirerPayId)));
			}

			List<String> txnStatus = new ArrayList<String>();
			txnStatus.add(StatusType.CAPTURED.getName());
			txnStatus.add(StatusType.FAILED.getName());
			txnStatus.add(StatusType.FAILED_AT_ACQUIRER.getName());
			txnStatus.add(StatusType.SUCCESS.getName());
			txnStatus.add(StatusType.SETTLED_SETTLE.getName());
			txnStatus.add(StatusType.ERROR.getName());
			txnStatus.add(StatusType.INVALID.getName());
			txnStatus.add(StatusType.REJECTED.getName());
			txnStatus.add(StatusType.SETTLED_RECONCILLED.getName());
			txnStatus.add(StatusType.USER_INACTIVE.getName());
			txnStatus.add(StatusType.CANCELLED_BY_USER.getName());
			txnStatus.add(StatusType.CANCELLED.getName());
			
			if (txnStatus != null) {
				paramConditionLst
						.add(new BasicDBObject(FieldType.STATUS.getName(), new BasicDBObject("$in", txnStatus)));
			}

			paramConditionLst.add(new BasicDBObject(FieldType.WEBHOOK_POST_FLAG.getName(), "N"));
			BasicDBObject failedCntQuery = new BasicDBObject();
			failedCntQuery.append("$lt", webhookFailedCnt);
			BasicDBObject failedCntQuery2 = new BasicDBObject(FieldType.WEBHOOK_FAILED_COUNT.getName(), failedCntQuery);
			paramConditionLst.add(failedCntQuery2);
			BasicDBObject finalquery = new BasicDBObject("$and", paramConditionLst);
			logger.info("Fetch Data for Posting Webhook Response To Merchant, finalquery={}", finalquery.toString());
			MongoDatabase dbIns = mongoInstance.getDB();
			MongoCollection<Document> collection =null;
		    
			if (webhookType.equalsIgnoreCase("PAYIN")) {
				collection = dbIns.getCollection("transactionStatus");
			} else {
				collection=dbIns.getCollection("POtransactionStatus");
			}

			MongoCursor<Document> iterator = collection.find(finalquery).iterator();

			while (iterator.hasNext()) {
				Document document = (Document) iterator.next();
				documentsList.add(document);
			}
		} catch (Exception var15) {
			var15.printStackTrace();
			logger.error("ERROR OCCURED, fetchPGWebHookDetails, Exception={}", var15);
		}
		logger.info("Document data is : " + documentsList);
		return documentsList;
	}
//	private static volatile MongoClient mongoClient;
//	private MongoClient getMongoClient() {
//		if(mongoClient==null) {
//		String decryptedPass = PasswordGenerator.getDeryptedPassword(
//				PropertiesManager.propertiesMap.get(prefix + Constants.MONGO_PASSWORD.getValue()),
//				PropertiesManager.propertiesMap.get(Constants.ENC_DEK.getValue()));
//		String mongoURL = PropertiesManager.propertiesMap.get(prefix + Constants.MONGO_URI_PREFIX.getValue())
//				+ PropertiesManager.propertiesMap.get(prefix + Constants.MONGO_USERNAME.getValue()) + ":"
//				+ decryptedPass + PropertiesManager.propertiesMap.get(prefix + Constants.MONGO_URI_SUFFIX.getValue());
//			MongoClientURI mClientURI = new MongoClientURI(mongoURL);
//			mongoClient=new MongoClient(mClientURI);
//		}
//		
//		return mongoClient;
//	}

	public PGWebHookPostConfigURL fetchPGWebHookPostConfigURLByPayId(String payId, String webhookType) {
		return this.pgWebHookPostConfigDao.fetchPGWebHookPostConfigURLByPayId(payId, webhookType);
	}

	public void updateFlagAndFailedCount(Document doc, Boolean responseData) {
		logger.info("Webhook Update FailedCount and UpdateFlage, PG_REF_NUM={}, ORDER_ID={}, WebhookResponse={}",
				new Object[] { doc.get(FieldType.PG_REF_NUM.getName()), doc.get(FieldType.ORDER_ID.getName()),
						responseData });

		try {
			MongoCollection<Document> coll = this.mongoInstance.getDB()
					.getCollection((String) PropertiesManager.propertiesMap.get("MONGO_DB_" + Constants.TRANSACTION_STATUS_COLLECTION.getValue()));
			List<BasicDBObject> dbObjList = new ArrayList<BasicDBObject>();
			dbObjList.add(new BasicDBObject("_id", doc.get("_id")));
			dbObjList.add(new BasicDBObject(FieldType.PG_REF_NUM.getName(), doc.get(FieldType.PG_REF_NUM.getName())));
			dbObjList.add(new BasicDBObject(FieldType.ORDER_ID.getName(), doc.get(FieldType.ORDER_ID.getName())));
			dbObjList.add(new BasicDBObject(FieldType.TXNTYPE.getName(), doc.get(FieldType.TXNTYPE.getName())));
			BasicDBObject andQuery = new BasicDBObject("$and", dbObjList);
			logger.info("Webhook Update FailedCount and UpdateFlage,  final query for update={} ", andQuery.toString());
			MongoCursor<Document> iterator = coll.find(andQuery).iterator();
			if (iterator.hasNext()) {
				Document document = (Document) iterator.next();
				String transactionStatusFields = (String) PropertiesManager.propertiesMap
						.get("TransactionStatusFields");
				String[] transactionStatusFieldsArr = transactionStatusFields.split(",");
				MongoCollection<Document> updateColl = mongoInstance.getDB()
						.getCollection((String) PropertiesManager.propertiesMap.get("MONGO_DB_" + Constants.TRANSACTION_STATUS_COLLECTION.getValue()));
				BasicDBObject updateFields = new BasicDBObject();
				String[] var13 = transactionStatusFieldsArr;
				int var14 = transactionStatusFieldsArr.length;

				for (int var15 = 0; var15 < var14; ++var15) {
					String key = var13[var15];
					if (!key.equalsIgnoreCase(FieldType.DATE_INDEX.getName())
							&& !key.equalsIgnoreCase(FieldType.CREATE_DATE.getName())
							&& !key.equalsIgnoreCase(FieldType.UPDATE_DATE.getName())
							&& !key.equalsIgnoreCase(FieldType.INSERTION_DATE.getName())) {
						if (document.get(key) != null) {
							updateFields.put(key, document.get(key).toString());
						} else {
							updateFields.put(key, document.get(key));
						}
					}
				}

				long updatedfailCount;
				if (responseData) {
					if (StringUtils.isNotBlank(document.getString(FieldType.WEBHOOK_FAILED_COUNT.getName()))) {
						updatedfailCount = Long.parseLong(document.getString(FieldType.WEBHOOK_FAILED_COUNT.getName()));
						++updatedfailCount;
						updateFields.append(FieldType.WEBHOOK_FAILED_COUNT.getName(), String.valueOf(updatedfailCount));
					} else {
						updateFields.append(FieldType.WEBHOOK_FAILED_COUNT.getName(), "1");
					}

					if (StringUtils.isNotBlank(document.getString(FieldType.WEBHOOK_POST_FLAG.getName()))) {
						updateFields.append(FieldType.WEBHOOK_POST_FLAG.getName(), "Y");
					} else {
						updateFields.append(FieldType.WEBHOOK_POST_FLAG.getName(), "Y");
					}
				} else if (StringUtils.isNotBlank(document.getString(FieldType.WEBHOOK_FAILED_COUNT.getName()))) {
					updatedfailCount = Long.parseLong(document.getString(FieldType.WEBHOOK_FAILED_COUNT.getName()));
					++updatedfailCount;
					updateFields.append(FieldType.WEBHOOK_FAILED_COUNT.getName(), String.valueOf(updatedfailCount));
				} else {
					updateFields.append(FieldType.WEBHOOK_FAILED_COUNT.getName(), "1");
				}

				updateColl.updateOne(andQuery, new BasicDBObject("$set", updateFields));
			}
		} catch (Exception var17) {
			logger.error("ERROR OCCURED While Updating FailedCnt and UpdateFlag for Webhook Response, Exception={}",
					var17);
		}

	}

	public void updateFlagAndFailedCount_payOut(Document doc, boolean responseData) {
		logger.info(
				"updateFlagAndFailedCount_payOut, Webhook Update FailedCount and UpdateFlage, PG_REF_NUM={}, ORDER_ID={}, WebhookResponse={}",
				new Object[] { doc.get(FieldType.PG_REF_NUM.getName()), doc.get(FieldType.ORDER_ID.getName()),
						responseData });

		try {
			MongoCollection<Document> coll = mongoInstance.getDB().getCollection("POtransactionStatus");
			List<BasicDBObject> dbObjList = new ArrayList<BasicDBObject>();
			dbObjList.add(new BasicDBObject(FieldType.PG_REF_NUM.getName(), doc.get(FieldType.PG_REF_NUM.getName())));
			dbObjList.add(new BasicDBObject(FieldType.ORDER_ID.getName(), doc.get(FieldType.ORDER_ID.getName())));
			BasicDBObject andQuery = new BasicDBObject("$and", dbObjList);
			logger.info(
					"updateFlagAndFailedCount_payOut, Webhook Update FailedCount and UpdateFlage,  final query for update={} ",
					andQuery.toString());
			MongoCursor<Document> iterator = coll.find(andQuery).iterator();
			if (iterator.hasNext()) {
				Document document = (Document) iterator.next();
				MongoCollection<Document> updateColl = this.mongoInstance.getDB().getCollection("POtransactionStatus");
				long updatedfailCount;
				if (responseData) {
					if (StringUtils.isNotBlank(document.getString(FieldType.WEBHOOK_FAILED_COUNT.getName()))) {
						updatedfailCount = Long.parseLong(document.getString(FieldType.WEBHOOK_FAILED_COUNT.getName()));
						++updatedfailCount;
						document.append(FieldType.WEBHOOK_FAILED_COUNT.getName(), String.valueOf(updatedfailCount));
					} else {
						document.append(FieldType.WEBHOOK_FAILED_COUNT.getName(), "1");
					}

					if (StringUtils.isNotBlank(document.getString(FieldType.WEBHOOK_POST_FLAG.getName()))) {
						document.append(FieldType.WEBHOOK_POST_FLAG.getName(), "Y");
					} else {
						document.append(FieldType.WEBHOOK_POST_FLAG.getName(), "Y");
					}
				} else if (StringUtils.isNotBlank(document.getString(FieldType.WEBHOOK_FAILED_COUNT.getName()))) {
					updatedfailCount = Long.parseLong(document.getString(FieldType.WEBHOOK_FAILED_COUNT.getName()));
					++updatedfailCount;
					document.append(FieldType.WEBHOOK_FAILED_COUNT.getName(), String.valueOf(updatedfailCount));
				} else {
					document.append(FieldType.WEBHOOK_FAILED_COUNT.getName(), "1");
				}

				updateColl.updateOne(andQuery, new BasicDBObject("$set", document));
			}
		} catch (Exception var12) {
			logger.error(
					"updateFlagAndFailedCount_payOut, ERROR OCCURED While Updating FailedCnt and UpdateFlag for Webhook Response, Exception={}",
					var12);
		}

	}

}
