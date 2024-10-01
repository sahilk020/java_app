package com.pay10.batch.commons;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.mongodb.BasicDBObject;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import com.pay10.batch.commons.util.Amount;
import com.pay10.batch.commons.util.ConfigurationProvider;
import com.pay10.batch.commons.util.DateCreater;
import com.pay10.batch.commons.util.MongoInstance;
import com.pay10.batch.commons.util.SystemProperties;
import com.pay10.batch.exception.DatabaseException;
import com.pay10.batch.exception.ErrorType;
import com.pay10.commons.util.FieldType;
import com.pay10.commons.util.StatusType;
import com.pay10.commons.util.TransactionType;

@Service
public class FieldsDao {

	@Autowired
	@Qualifier("systemProperties")
	private SystemProperties systemProperties;

	private static Logger logger = Logger.getLogger(FieldsDao.class.getName());

	@Autowired
	@Qualifier("mongoInstance")
	private MongoInstance mongoInstance;
	@Autowired
	private ConfigurationProvider configProvider;

	@Autowired
	private Amount amount;

	public void insertTransaction(Fields fields) throws DatabaseException {
		try {
			logger.info("Inside insertTransaction with PG_REF_NUM : " + fields.get(FieldType.PG_REF_NUM.getName()) + ", fields "+fields.getFieldsAsString());
			MongoDatabase dbIns = null;
			BasicDBObject newFieldsObj = new BasicDBObject();

			String amountString = fields.get(FieldType.RECO_AMOUNT.getName());
			String totalAmountString = fields.get(FieldType.RECO_TOTAL_AMOUNT.getName());
			String surchargeAmountString = fields.get(FieldType.SURCHARGE_AMOUNT.getName());
			String currencyString = fields.get(FieldType.CURRENCY_CODE.getName());

			String amountValue = "0";
			if (!StringUtils.isEmpty(amountString) && !StringUtils.isEmpty(currencyString)) {
				amountValue = amount.toDecimal(amountString, currencyString);
				newFieldsObj.put(FieldType.RECO_AMOUNT.getName(), amountValue);
			}
			
			String totalAmountValue = "0";
			if (!StringUtils.isEmpty(totalAmountString) && !StringUtils.isEmpty(currencyString)) {
				totalAmountValue = amount.toDecimal(totalAmountString, currencyString);
				newFieldsObj.put(FieldType.RECO_TOTAL_AMOUNT.getName(), totalAmountValue);
			}

			String surchargeAmount = "0";
			if (!StringUtils.isEmpty(surchargeAmountString) && !StringUtils.isEmpty(currencyString)) {
				surchargeAmount = amount.toDecimal(surchargeAmountString, currencyString);
				newFieldsObj.put(FieldType.SURCHARGE_AMOUNT.getName(), surchargeAmount);
			}

			String pgRefNum = fields.get(FieldType.PG_REF_NUM.getName());
			if (StringUtils.isEmpty(pgRefNum)) {
				newFieldsObj.put(FieldType.PG_REF_NUM.getName(), "0");
			} else {
				newFieldsObj.put(FieldType.PG_REF_NUM.getName(), pgRefNum);
			}

			String acctIdStr = fields.get(FieldType.ACCT_ID.getName());
			if (acctIdStr != null && acctIdStr.length() > 0) {
				newFieldsObj.put(FieldType.ACCT_ID.getName(), acctIdStr);
			} else {
				newFieldsObj.put(FieldType.ACCT_ID.getName(), "0");
			}

			String acqIdStr = fields.get(FieldType.RECO_ACQ_ID.getName());
			if (acqIdStr != null && acqIdStr.length() > 0) {
				newFieldsObj.put(FieldType.RECO_ACQ_ID.getName(), acqIdStr);
			} else {
				newFieldsObj.put(FieldType.RECO_ACQ_ID.getName(), "0");
			}

			String oid = fields.get(FieldType.OID.getName());
			if (!StringUtils.isEmpty(oid)) {
				newFieldsObj.put(FieldType.OID.getName(), oid);
			} else {
				newFieldsObj.put(FieldType.OID.getName(), "0");
			}
			
			if (StringUtils.isNotBlank(fields.get(FieldType.ACQUIRER_TDR_SC.getName()))) {
				newFieldsObj.put(FieldType.ACQUIRER_TDR_SC.getName(), fields.get(FieldType.ACQUIRER_TDR_SC.getName()));
			}
			
			if (StringUtils.isNotBlank(fields.get(FieldType.ACQUIRER_GST.getName()))) {
				newFieldsObj.put(FieldType.ACQUIRER_GST.getName(), fields.get(FieldType.ACQUIRER_GST.getName()));
			}
			
			if (StringUtils.isNotBlank(fields.get(FieldType.PG_TDR_SC.getName()))) {
				newFieldsObj.put(FieldType.PG_TDR_SC.getName(), fields.get(FieldType.PG_TDR_SC.getName()));
			}
			
			if (StringUtils.isNotBlank(fields.get(FieldType.PG_GST.getName()))) {
				newFieldsObj.put(FieldType.PG_GST.getName(), fields.get(FieldType.PG_GST.getName()));
			}
			for (int i = 0; i < fields.size(); i++) {
				//logger.info("Inside fieldDAO for loop");
				Date dNow = new Date();
				String dateNow = DateCreater.formatDateForDb(dNow);
				for (String columnName : systemProperties.getTransactionFields()) {
					
					if (columnName.equals(FieldType.CREATE_DATE.getName())) {						
							newFieldsObj.put(columnName, dateNow);
					} else if (columnName.equals(FieldType.UPDATE_DATE.getName())) {						
							newFieldsObj.put(columnName, dateNow);
					} else if (columnName.equals(FieldType.DATE_INDEX.getName())) {						
						newFieldsObj.put(columnName, dateNow.substring(0,10).replace("-", ""));
					} else if (columnName.equals(FieldType.PG_DATE_TIME_INDEX.getName())) {		
						if(fields.get(FieldType.PG_DATE_TIME.getName())!=null) {
							newFieldsObj.put(columnName, fields.get(FieldType.PG_DATE_TIME.getName()).substring(0,10).replace("-", ""));
						}else {
							newFieldsObj.put(columnName, dateNow.substring(0,10).replace("-", ""));
						}
						
					} else if (columnName.equals("_id")) {
						newFieldsObj.put(columnName, fields.get(FieldType.TXN_ID.getName()));
					} else if (columnName.equals(FieldType.RECO_AMOUNT.getName())) {
						newFieldsObj.put(columnName, amountValue);
					} else if (columnName.equals(FieldType.RECO_TOTAL_AMOUNT.getName())) {
						newFieldsObj.put(columnName, totalAmountValue);
					} else if (columnName.equals(FieldType.INSERTION_DATE.getName())) {
                          newFieldsObj.put(columnName, dNow);
                    } else {
						newFieldsObj.put(columnName, fields.get(columnName));
					}
				}
			}

			dbIns = mongoInstance.getDB();
			MongoCollection<Document> collection = dbIns.getCollection(configProvider.getTransactionColl());
			Document doc = new Document(newFieldsObj);
			//Thread.sleep(10);
			collection.insertOne(doc);
			
			/*ThreadPoolProvider.getExecutorService().execute(new Runnable() {
				@Override
	            public void run() {
	                try {
	                	updateStatusColl(fields,doc);  		
	                	
	                } catch (Exception exception) {
	    	        	logger.error("Exception calling transacion status update service ",exception);
	                }
	            }
	        });	     */
			try {
				updateStatusColl(fields,doc);

			} catch (Exception exception) {
				logger.error("Exception calling transacion status update service ",exception);
			}
	        
		} catch (Exception exception) {
			String message = "Error while inserting transaction in database";
			logger.error(message, exception);
			throw new DatabaseException(ErrorType.DATABASE_ERROR, exception, message);
		}
	}

	public void insertTransactionWithDate(Fields fields) throws DatabaseException {
		try {
			logger.info("Inside insertTransaction : " + fields.get(FieldType.PG_REF_NUM.getName()));
			MongoDatabase dbIns = null;
			BasicDBObject newFieldsObj = new BasicDBObject();

			String amountString = fields.get(FieldType.RECO_AMOUNT.getName());
			String totalAmountString = fields.get(FieldType.RECO_TOTAL_AMOUNT.getName());
			String surchargeAmountString = fields.get(FieldType.SURCHARGE_AMOUNT.getName());
			String currencyString = fields.get(FieldType.CURRENCY_CODE.getName());

			String amountValue = "0";
			if (!StringUtils.isEmpty(amountString) && !StringUtils.isEmpty(currencyString)) {
				amountValue = amount.toDecimal(amountString, currencyString);
				newFieldsObj.put(FieldType.RECO_AMOUNT.getName(), amountValue);
			}
			
			String totalAmountValue = "0";
			if (!StringUtils.isEmpty(totalAmountString) && !StringUtils.isEmpty(currencyString)) {
				totalAmountValue = amount.toDecimal(totalAmountString, currencyString);
				newFieldsObj.put(FieldType.RECO_TOTAL_AMOUNT.getName(), totalAmountValue);
			}

			String surchargeAmount = "0";
			if (!StringUtils.isEmpty(surchargeAmountString) && !StringUtils.isEmpty(currencyString)) {
				surchargeAmount = amount.toDecimal(surchargeAmountString, currencyString);
				newFieldsObj.put(FieldType.SURCHARGE_AMOUNT.getName(), surchargeAmount);
			}

			String pgRefNum = fields.get(FieldType.PG_REF_NUM.getName());
			if (StringUtils.isEmpty(pgRefNum)) {
				newFieldsObj.put(FieldType.PG_REF_NUM.getName(), "0");
			} else {
				newFieldsObj.put(FieldType.PG_REF_NUM.getName(), pgRefNum);
			}

			String acctIdStr = fields.get(FieldType.ACCT_ID.getName());
			if (acctIdStr != null && acctIdStr.length() > 0) {
				newFieldsObj.put(FieldType.ACCT_ID.getName(), acctIdStr);
			} else {
				newFieldsObj.put(FieldType.ACCT_ID.getName(), "0");
			}

			String acqIdStr = fields.get(FieldType.RECO_ACQ_ID.getName());
			if (acqIdStr != null && acqIdStr.length() > 0) {
				newFieldsObj.put(FieldType.RECO_ACQ_ID.getName(), acqIdStr);
			} else {
				newFieldsObj.put(FieldType.RECO_ACQ_ID.getName(), "0");
			}

			String oid = fields.get(FieldType.OID.getName());
			if (!StringUtils.isEmpty(oid)) {
				newFieldsObj.put(FieldType.OID.getName(), oid);
			} else {
				newFieldsObj.put(FieldType.OID.getName(), "0");
			}
			
			if (StringUtils.isNotBlank(fields.get(FieldType.ACQUIRER_TDR_SC.getName()))) {
				newFieldsObj.put(FieldType.ACQUIRER_TDR_SC.getName(), fields.get(FieldType.ACQUIRER_TDR_SC.getName()));
			}
			
			if (StringUtils.isNotBlank(fields.get(FieldType.ACQUIRER_GST.getName()))) {
				newFieldsObj.put(FieldType.ACQUIRER_GST.getName(), fields.get(FieldType.ACQUIRER_GST.getName()));
			}
			
			if (StringUtils.isNotBlank(fields.get(FieldType.PG_TDR_SC.getName()))) {
				newFieldsObj.put(FieldType.PG_TDR_SC.getName(), fields.get(FieldType.PG_TDR_SC.getName()));
			}
			
			if (StringUtils.isNotBlank(fields.get(FieldType.PG_GST.getName()))) {
				newFieldsObj.put(FieldType.PG_GST.getName(), fields.get(FieldType.PG_GST.getName()));
			}

			for (int i = 0; i < fields.size(); i++) {
				logger.debug("Inside fieldDAO new " + newFieldsObj.getString("_id"));
				//logger.info("Inside fieldDAO for loop");
				Date dNow = new Date();
				
				for (String columnName : systemProperties.getTransactionFields()) {					
					 if (columnName.equals("_id")) {
						newFieldsObj.put(columnName, fields.get(FieldType.TXN_ID.getName()));
					} else if (columnName.equals(FieldType.RECO_AMOUNT.getName())) {
						newFieldsObj.put(columnName, amountValue);
					} else if (columnName.equals(FieldType.RECO_TOTAL_AMOUNT.getName())) {
						newFieldsObj.put(columnName, totalAmountValue);
					} else if (columnName.equals(FieldType.INSERTION_DATE.getName())) {
                          newFieldsObj.put(columnName, dNow);
                    } else {
						newFieldsObj.put(columnName, fields.get(columnName));
					}
				}
			}

			dbIns = mongoInstance.getDB();
			logger.info("Below dbIns = mongoInstance.getDB()");
			MongoCollection<Document> collection = dbIns.getCollection(configProvider.getTransactionColl());
			logger.info("Collection created : " + configProvider.getTransactionColl());
			Document doc = new Document(newFieldsObj);
			logger.info("New  document created");
			//Thread.sleep(10);
			collection.insertOne(doc);
			logger.info("Transaction Inserted at time : " + DateCreater.getCurrentDate());
		} catch (Exception exception) {
			String message = "Error while inserting transaction in database";
			logger.error(message, exception);
			throw new DatabaseException(ErrorType.DATABASE_ERROR, exception, message);
		}
	}
	public Fields getPreviousFields(String txnId) throws DatabaseException {
		try {
			return readAllPrevious(txnId);
		} catch (Exception exception) {
			String message = "Error while reading previous data from database for txnId: " + txnId;
			logger.error(message, exception);
			throw new DatabaseException(ErrorType.DATABASE_ERROR, exception, message);
		}
	}

	private Fields readAllPrevious(String txnId) {
		logger.info("Inside readAllPrevious function !!");
		Fields fields = new Fields();
		BasicDBObject query = new BasicDBObject(FieldType.TXN_ID.getName(), txnId);
		MongoDatabase dbIns = mongoInstance.getDB();
		logger.info("Mongodb Instance Created !!");
		MongoCollection<Document> coll = dbIns.getCollection(configProvider.getTransactionColl());
		logger.info("MongoCollection Created !!");
		MongoCursor<Document> cursor = coll.find(query).iterator();
		logger.info("MongoCursor Created !!");
		try {
			if (cursor.hasNext()) {
				Document documentObj = cursor.next();

				if (null != documentObj) {
					for (int j = 0; j < documentObj.size(); j++) {
						for (String columnName : systemProperties.getTransactionFields()) {
							fields.put(columnName, documentObj.getString(columnName));
						}
					}
				}
				if ( documentObj.get(FieldType.ACQUIRER_TDR_SC.getName()) != null ) {
					fields.put(FieldType.ACQUIRER_TDR_SC.getName(), documentObj.get(FieldType.ACQUIRER_TDR_SC.getName()).toString());
				}
				
				if ( documentObj.get(FieldType.ACQUIRER_GST.getName()) != null ) {
					fields.put(FieldType.ACQUIRER_GST.getName(), documentObj.get(FieldType.ACQUIRER_GST.getName()).toString());
				}
				
				if (documentObj.get(FieldType.PG_TDR_SC.getName()) != null ) {
					fields.put(FieldType.PG_TDR_SC.getName(), documentObj.get(FieldType.PG_TDR_SC.getName()).toString());
				}
				
				if (documentObj.get(FieldType.PG_GST.getName()) != null ) {
					fields.put(FieldType.PG_GST.getName(), documentObj.get(FieldType.PG_GST.getName()).toString());
				}
				
				fields.logAllFields("Previous fields");
				
			}
		} finally {
			cursor.close();
		}
		return fields;
	}

	public List<Fields> getPreviousSaleOrRecoForPgRefNum(String pg_ref_num) throws DatabaseException {
		try {
			List<Fields> fieldsList = new ArrayList<Fields>();

			List<BasicDBObject> saleList = new ArrayList<BasicDBObject>();
			saleList.add(new BasicDBObject(FieldType.PG_REF_NUM.getName(), pg_ref_num));
			saleList.add(new BasicDBObject(FieldType.RECO_TXNTYPE.getName(), TransactionType.SALE.getName()));
			saleList.add(new BasicDBObject(FieldType.STATUS.getName(), StatusType.CAPTURED.getName()));
			BasicDBObject saleQuery = new BasicDBObject("$and", saleList);

			List<BasicDBObject> transList = new ArrayList<BasicDBObject>();
			transList.add(saleQuery);
			BasicDBObject transQuery = new BasicDBObject("$or", transList);

			MongoDatabase dbIns = mongoInstance.getDB();

			MongoCollection<Document> collection = dbIns.getCollection(configProvider.getTransactionColl());

			MongoCursor<Document> cursor = collection.find(transQuery).iterator();
			try {
				while (cursor.hasNext()) {
					Document documentObj = (Document) cursor.next();
					if (null != documentObj) {
						Fields preFields = new Fields();
						for (int j = 0; j < documentObj.size(); j++) {
							for (String columnName : systemProperties.getTransactionFields()) {
								if (documentObj.get(columnName) != null) {
									preFields.put(columnName, documentObj.get(columnName).toString());
								}
							}
						}
						
						if ( documentObj.get(FieldType.ACQUIRER_TDR_SC.getName()) != null ) {
							preFields.put(FieldType.ACQUIRER_TDR_SC.getName(), documentObj.get(FieldType.ACQUIRER_TDR_SC.getName()).toString());
						}
						
						if ( documentObj.get(FieldType.ACQUIRER_GST.getName()) != null ) {
							preFields.put(FieldType.ACQUIRER_GST.getName(), documentObj.get(FieldType.ACQUIRER_GST.getName()).toString());
						}
						
						if (documentObj.get(FieldType.PG_TDR_SC.getName()) != null ) {
							preFields.put(FieldType.PG_TDR_SC.getName(), documentObj.get(FieldType.PG_TDR_SC.getName()).toString());
						}
						
						if (documentObj.get(FieldType.PG_GST.getName()) != null ) {
							preFields.put(FieldType.PG_GST.getName(), documentObj.get(FieldType.PG_GST.getName()).toString());
						}
						
						preFields.logAllFields(
								"Received RECO&RECONCILED/SALE&CAPTURED/SALE&PENDING transaction details for transaction with Pg_Ref_Num: "
										+ pg_ref_num);
						fieldsList.add(preFields);
					}
				}
			} finally {
				cursor.close();
			}

			return fieldsList;
		} catch (Exception exception) {
			String message = "Error while reading list of transactions based on PgRefNum from database";
			logger.error(message, exception);
			throw new DatabaseException(ErrorType.DATABASE_ERROR, exception, message);
		}
	}
	
	public List<Fields> getPreviousRecoOrPendingForPgRefNum(String pg_ref_num) throws DatabaseException {
		try {
			List<Fields> fieldsList = new ArrayList<Fields>();

			List<BasicDBObject> saleList = new ArrayList<BasicDBObject>();
			saleList.add(new BasicDBObject(FieldType.PG_REF_NUM.getName(), pg_ref_num));
			saleList.add(new BasicDBObject(FieldType.RECO_TXNTYPE.getName(), TransactionType.SALE.getName()));
			saleList.add(new BasicDBObject(FieldType.STATUS.getName(), StatusType.CAPTURED.getName()));
			BasicDBObject saleQuery = new BasicDBObject("$and", saleList);			

			List<BasicDBObject> transList = new ArrayList<BasicDBObject>();
			transList.add(saleQuery);

			BasicDBObject transQuery = new BasicDBObject("$or", transList);
			
			MongoDatabase dbIns = mongoInstance.getDB();

			MongoCollection<Document> collection = dbIns.getCollection(configProvider.getTransactionColl());

			MongoCursor<Document> cursor = collection.find(transQuery).iterator();
			try {
				while (cursor.hasNext()) {
					Document documentObj = (Document) cursor.next();
					if (null != documentObj) {
						Fields preFields = new Fields();
						for (int j = 0; j < documentObj.size(); j++) {
							for (String columnName : systemProperties.getTransactionFields()) {
								if (documentObj.get(columnName) != null) {
									preFields.put(columnName, documentObj.get(columnName).toString());
								}
							}
						}
						
						if ( documentObj.get(FieldType.ACQUIRER_TDR_SC.getName()) != null ) {
							preFields.put(FieldType.ACQUIRER_TDR_SC.getName(), documentObj.get(FieldType.ACQUIRER_TDR_SC.getName()).toString());
						}
						
						if ( documentObj.get(FieldType.ACQUIRER_GST.getName()) != null ) {
							preFields.put(FieldType.ACQUIRER_GST.getName(), documentObj.get(FieldType.ACQUIRER_GST.getName()).toString());
						}
						
						if (documentObj.get(FieldType.PG_TDR_SC.getName()) != null ) {
							preFields.put(FieldType.PG_TDR_SC.getName(), documentObj.get(FieldType.PG_TDR_SC.getName()).toString());
						}
						
						if (documentObj.get(FieldType.PG_GST.getName()) != null ) {
							preFields.put(FieldType.PG_GST.getName(), documentObj.get(FieldType.PG_GST.getName()).toString());
						}
						
						preFields.logAllFields(
								"Received RECO&RECONCILED/REFUND&PENDING/SALE&CAPTURED transaction details for transaction with Pg_Ref_Num: "
										+ pg_ref_num);
						fieldsList.add(preFields);
					}
				}
			} finally {
				cursor.close();
			}

			return fieldsList;
		} catch (Exception exception) {
			String message = "Error while reading list of transactions based on PgRefNum from database";
			logger.error(message, exception);
			throw new DatabaseException(ErrorType.DATABASE_ERROR, exception, message);
		}
	}
	
	public List<Fields> getPreviousRefundForPgRefNum(String pg_ref_num) throws DatabaseException {
		try {
			List<Fields> fieldsList = new ArrayList<Fields>();

			List<BasicDBObject> saleList = new ArrayList<BasicDBObject>();
			saleList.add(new BasicDBObject(FieldType.PG_REF_NUM.getName(), pg_ref_num));
			saleList.add(new BasicDBObject(FieldType.RECO_TXNTYPE.getName(), TransactionType.REFUND.getName()));
			saleList.add(new BasicDBObject(FieldType.STATUS.getName(), StatusType.CAPTURED.getName()));
			BasicDBObject saleQuery = new BasicDBObject("$and", saleList);

			MongoDatabase dbIns = mongoInstance.getDB();

			MongoCollection<Document> collection = dbIns.getCollection(configProvider.getTransactionColl());

			MongoCursor<Document> cursor = collection.find(saleQuery).iterator();
			try {
				while (cursor.hasNext()) {
					Document documentObj = (Document) cursor.next();
					if (null != documentObj) {
						Fields preFields = new Fields();
						for (int j = 0; j < documentObj.size(); j++) {
							for (String columnName : systemProperties.getTransactionFields()) {
								if (documentObj.get(columnName) != null) {
									preFields.put(columnName, documentObj.get(columnName).toString());
								}
							}
						}
						
						if ( documentObj.get(FieldType.ACQUIRER_TDR_SC.getName()) != null ) {
							preFields.put(FieldType.ACQUIRER_TDR_SC.getName(), documentObj.get(FieldType.ACQUIRER_TDR_SC.getName()).toString());
						}
						
						if ( documentObj.get(FieldType.ACQUIRER_GST.getName()) != null ) {
							preFields.put(FieldType.ACQUIRER_GST.getName(), documentObj.get(FieldType.ACQUIRER_GST.getName()).toString());
						}
						
						if (documentObj.get(FieldType.PG_TDR_SC.getName()) != null ) {
							preFields.put(FieldType.PG_TDR_SC.getName(), documentObj.get(FieldType.PG_TDR_SC.getName()).toString());
						}
						
						if (documentObj.get(FieldType.PG_GST.getName()) != null ) {
							preFields.put(FieldType.PG_GST.getName(), documentObj.get(FieldType.PG_GST.getName()).toString());
						}
						
						preFields.logAllFields(
								"Received REFUND&CAPTURED transaction details for transaction with Pg_Ref_Num: "
										+ pg_ref_num);
						fieldsList.add(preFields);
					}
				}
			} finally {
				cursor.close();
			}

			return fieldsList;
		} catch (Exception exception) {
			String message = "Error while reading list of transactions based on PgRefNum from database";
			logger.error(message, exception);
			throw new DatabaseException(ErrorType.DATABASE_ERROR, exception, message);
		}
	}
	
	public List<Fields> getPreviousRefundForRefundOrderId(String refundOrderId, String orderId) throws DatabaseException {
		try {
			List<Fields> fieldsList = new ArrayList<Fields>();

			List<BasicDBObject> refundList = new ArrayList<BasicDBObject>();
			refundList.add(new BasicDBObject(FieldType.REFUND_ORDER_ID.getName(), refundOrderId));
			refundList.add(new BasicDBObject(FieldType.RECO_ORDER_ID.getName(), orderId));
			refundList.add(new BasicDBObject(FieldType.RECO_TXNTYPE.getName(), TransactionType.REFUND.getName()));
			BasicDBObject refundQuery = new BasicDBObject("$and", refundList);

			MongoDatabase dbIns = mongoInstance.getDB();

			MongoCollection<Document> collection = dbIns.getCollection(configProvider.getTransactionColl());

			MongoCursor<Document> cursor = collection.find(refundQuery).iterator();
			try {
				while (cursor.hasNext()) {
					Document documentObj = (Document) cursor.next();
					if (null != documentObj) {
						Fields preFields = new Fields();
						for (int j = 0; j < documentObj.size(); j++) {
							for (String columnName : systemProperties.getTransactionFields()) {
								if (documentObj.get(columnName) != null) {
									preFields.put(columnName, documentObj.get(columnName).toString());
								}
							}
						}
						
						if ( documentObj.get(FieldType.ACQUIRER_TDR_SC.getName()) != null ) {
							preFields.put(FieldType.ACQUIRER_TDR_SC.getName(), documentObj.get(FieldType.ACQUIRER_TDR_SC.getName()).toString());
						}
						
						if ( documentObj.get(FieldType.ACQUIRER_GST.getName()) != null ) {
							preFields.put(FieldType.ACQUIRER_GST.getName(), documentObj.get(FieldType.ACQUIRER_GST.getName()).toString());
						}
						
						if (documentObj.get(FieldType.PG_TDR_SC.getName()) != null ) {
							preFields.put(FieldType.PG_TDR_SC.getName(), documentObj.get(FieldType.PG_TDR_SC.getName()).toString());
						}
						
						if (documentObj.get(FieldType.PG_GST.getName()) != null ) {
							preFields.put(FieldType.PG_GST.getName(), documentObj.get(FieldType.PG_GST.getName()).toString());
						}
						
						preFields.logAllFields(
								"Received REFUND transaction details for transaction with Refund_Order_ID: "
										+ refundOrderId + " and Order_ID : " + orderId);
						fieldsList.add(preFields);
					}
				}
			} finally {
				cursor.close();
			}

			return fieldsList;
		} catch (Exception exception) {
			String message = "Error while reading list of transactions based on PgRefNum from database";
			logger.error(message, exception);
			throw new DatabaseException(ErrorType.DATABASE_ERROR, exception, message);
		}
	}
	
	public boolean isExistRecoReconciled(String pg_ref_num) throws DatabaseException {
		try {
			List<Fields> fieldsList = new ArrayList<Fields>();

			List<BasicDBObject> recoList = new ArrayList<BasicDBObject>();
			recoList.add(new BasicDBObject(FieldType.PG_REF_NUM.getName(), pg_ref_num));
			recoList.add(new BasicDBObject(FieldType.RECO_TXNTYPE.getName(), TransactionType.RECO.getName()));
			//recoList.add(new BasicDBObject(FieldType.STATUS.getName(), StatusType.RECONCILED.getName()));
			BasicDBObject recoQuery = new BasicDBObject("$and", recoList);

			MongoDatabase dbIns = mongoInstance.getDB();

			MongoCollection<Document> collection = dbIns.getCollection(configProvider.getTransactionColl());

			int total = (int)collection.count(recoQuery);
			
			if(total > 0)
				return true;
			
			return false;
		} catch (Exception exception) {
			String message = "Error while reading list of transactions based on PgRefNum from database";
			logger.error(message, exception);
			throw new DatabaseException(ErrorType.DATABASE_ERROR, exception, message);
		}
	}

	public List<Fields> getPreviousRefundOrRecoForPgRefNum(String pg_ref_num) throws DatabaseException {
		try {
			List<Fields> fieldsList = new ArrayList<Fields>();

			List<BasicDBObject> recoList = new ArrayList<BasicDBObject>();
			recoList.add(new BasicDBObject(FieldType.PG_REF_NUM.getName(), pg_ref_num));
			recoList.add(new BasicDBObject(FieldType.RECO_TXNTYPE.getName(), TransactionType.REFUNDRECO.getName()));
			// Done By chetan nagaria for change in settlement process to mark transaction as RNS
//			recoList.add(new BasicDBObject(FieldType.STATUS.getName(), StatusType.SETTLED.getName()));
			recoList.add(new BasicDBObject(FieldType.STATUS.getName(), StatusType.SETTLED_RECONCILLED.getName()));
			BasicDBObject recoQuery = new BasicDBObject("$and", recoList);

			List<BasicDBObject> refundList = new ArrayList<BasicDBObject>();
			refundList.add(new BasicDBObject(FieldType.PG_REF_NUM.getName(), pg_ref_num));
			refundList.add(new BasicDBObject(FieldType.RECO_TXNTYPE.getName(), TransactionType.REFUND.getName()));
			refundList.add(new BasicDBObject(FieldType.STATUS.getName(), StatusType.CAPTURED.getName()));
			BasicDBObject refundQuery = new BasicDBObject("$and", refundList);
			

			List<BasicDBObject> transList = new ArrayList<BasicDBObject>();
			transList.add(recoQuery);
			transList.add(refundQuery);
			
			BasicDBObject transQuery = new BasicDBObject("$or", transList);

			MongoDatabase dbIns = mongoInstance.getDB();

			MongoCollection<Document> collection = dbIns.getCollection(configProvider.getTransactionColl());

			MongoCursor<Document> cursor = collection.find(transQuery).iterator();
			try {
				while (cursor.hasNext()) {
					Document documentObj = (Document) cursor.next();
					if (null != documentObj) {
						Fields preFields = new Fields();
						for (int j = 0; j < documentObj.size(); j++) {
							for (String columnName : systemProperties.getTransactionFields()) {
								if (documentObj.get(columnName) != null) {
									preFields.put(columnName, documentObj.get(columnName).toString());
								}
							}

						}
						
						if ( documentObj.get(FieldType.ACQUIRER_TDR_SC.getName()) != null ) {
							preFields.put(FieldType.ACQUIRER_TDR_SC.getName(), documentObj.get(FieldType.ACQUIRER_TDR_SC.getName()).toString());
						}
						
						if ( documentObj.get(FieldType.ACQUIRER_GST.getName()) != null ) {
							preFields.put(FieldType.ACQUIRER_GST.getName(), documentObj.get(FieldType.ACQUIRER_GST.getName()).toString());
						}
						
						if (documentObj.get(FieldType.PG_TDR_SC.getName()) != null ) {
							preFields.put(FieldType.PG_TDR_SC.getName(), documentObj.get(FieldType.PG_TDR_SC.getName()).toString());
						}
						
						if (documentObj.get(FieldType.PG_GST.getName()) != null ) {
							preFields.put(FieldType.PG_GST.getName(), documentObj.get(FieldType.PG_GST.getName()).toString());
						}
						
						preFields.logAllFields(
								"Received REFUNDRECO&SETTLED/REFUND&CAPTURED transaction details for transaction with Pg_Ref_Num: "
										+ pg_ref_num);
						fieldsList.add(preFields);
					}
				}
			} finally {
				cursor.close();
			}

			return fieldsList;
		} catch (Exception exception) {
			String message = "Error while reading list of transactions based on PgRefNum from database";
			logger.error(message, exception);
			throw new DatabaseException(ErrorType.DATABASE_ERROR, exception, message);
		}
	}

	public List<Fields> getPreviousRefundTxnForPgRefNum(String pg_ref_num) throws DatabaseException {
		try {
			List<Fields> fieldsList = new ArrayList<Fields>();

			List<BasicDBObject> refundList = new ArrayList<BasicDBObject>();
			refundList.add(new BasicDBObject(FieldType.PG_REF_NUM.getName(), pg_ref_num));
			refundList.add(new BasicDBObject(FieldType.RECO_TXNTYPE.getName(), TransactionType.REFUND.getName()));
			BasicDBObject refundQuery = new BasicDBObject("$and", refundList);
			
			List<BasicDBObject> transList = new ArrayList<BasicDBObject>();
			transList.add(refundQuery);
			
			BasicDBObject transQuery = new BasicDBObject("$or", transList);

			MongoDatabase dbIns = mongoInstance.getDB();

			MongoCollection<Document> collection = dbIns.getCollection(configProvider.getTransactionColl());

			MongoCursor<Document> cursor = collection.find(transQuery).iterator();
			try {
				while (cursor.hasNext()) {
					Document documentObj = (Document) cursor.next();
					if (null != documentObj) {
						Fields preFields = new Fields();
						for (int j = 0; j < documentObj.size(); j++) {
							for (String columnName : systemProperties.getTransactionFields()) {
								if (documentObj.get(columnName) != null) {
									preFields.put(columnName, documentObj.get(columnName).toString());
								}
							}
						}
						
						if ( documentObj.get(FieldType.ACQUIRER_TDR_SC.getName()) != null ) {
							preFields.put(FieldType.ACQUIRER_TDR_SC.getName(), documentObj.get(FieldType.ACQUIRER_TDR_SC.getName()).toString());
						}
						
						if ( documentObj.get(FieldType.ACQUIRER_GST.getName()) != null ) {
							preFields.put(FieldType.ACQUIRER_GST.getName(), documentObj.get(FieldType.ACQUIRER_GST.getName()).toString());
						}
						
						if (documentObj.get(FieldType.PG_TDR_SC.getName()) != null ) {
							preFields.put(FieldType.PG_TDR_SC.getName(), documentObj.get(FieldType.PG_TDR_SC.getName()).toString());
						}
						
						if (documentObj.get(FieldType.PG_GST.getName()) != null ) {
							preFields.put(FieldType.PG_GST.getName(), documentObj.get(FieldType.PG_GST.getName()).toString());
						}
						
						preFields.logAllFields(
								"Received REFUNDRECO&SETTLED/REFUND&CAPTURED transaction details for transaction with Pg_Ref_Num: "
										+ pg_ref_num);
						fieldsList.add(preFields);
					}
				}
			} finally {
				cursor.close();
			}

			return fieldsList;
		} catch (Exception exception) {
			String message = "Error while reading list of transactions based on PgRefNum from database";
			logger.error(message, exception);
			throw new DatabaseException(ErrorType.DATABASE_ERROR, exception, message);
		}
	}
	
	public List<Fields> getPreviousSettlementOrRecoForPgRefNum(String pg_ref_num) throws DatabaseException {
		try {
			List<Fields> fieldsList = new ArrayList<Fields>();

			List<BasicDBObject> settledList = new ArrayList<BasicDBObject>();
			settledList.add(new BasicDBObject(FieldType.PG_REF_NUM.getName(), pg_ref_num));
			settledList.add(new BasicDBObject(FieldType.RECO_TXNTYPE.getName(), TransactionType.RECO.getName()));
			// Done By chetan nagaria for change in settlement process to mark transaction as RNS
//			settledList.add(new BasicDBObject(FieldType.STATUS.getName(), StatusType.SETTLED.getName()));
			settledList.add(new BasicDBObject(FieldType.STATUS.getName(), StatusType.SETTLED_RECONCILLED.getName()));
			BasicDBObject settledQuery = new BasicDBObject("$and", settledList);

			List<BasicDBObject> saleList = new ArrayList<BasicDBObject>();
			saleList.add(new BasicDBObject(FieldType.PG_REF_NUM.getName(), pg_ref_num));
			saleList.add(new BasicDBObject(FieldType.RECO_TXNTYPE.getName(), TransactionType.SALE.getName()));
			saleList.add(new BasicDBObject(FieldType.STATUS.getName(), StatusType.CAPTURED.getName()));
			BasicDBObject saleQuery = new BasicDBObject("$and", saleList);
			
			List<BasicDBObject> transList = new ArrayList<BasicDBObject>();
			transList.add(settledQuery);
			transList.add(saleQuery);

			BasicDBObject transQuery = new BasicDBObject("$or", transList);

			MongoDatabase dbIns = mongoInstance.getDB();

			MongoCollection<Document> collection = dbIns.getCollection(configProvider.getTransactionColl());
			MongoCursor<Document> cursor = collection.find(transQuery).iterator();
			try {
				while (cursor.hasNext()) {
					Document documentObj = (Document) cursor.next();
					if (null != documentObj) {
						Fields preFields = new Fields();
						for (int j = 0; j < documentObj.size(); j++) {
							for (String columnName : systemProperties.getTransactionFields()) {
								if (documentObj.get(columnName) != null) {
									preFields.put(columnName, documentObj.get(columnName).toString());
								}
							}
						}
						
						if ( documentObj.get(FieldType.ACQUIRER_TDR_SC.getName()) != null ) {
							preFields.put(FieldType.ACQUIRER_TDR_SC.getName(), documentObj.get(FieldType.ACQUIRER_TDR_SC.getName()).toString());
						}
						
						if ( documentObj.get(FieldType.ACQUIRER_GST.getName()) != null ) {
							preFields.put(FieldType.ACQUIRER_GST.getName(), documentObj.get(FieldType.ACQUIRER_GST.getName()).toString());
						}
						
						if (documentObj.get(FieldType.PG_TDR_SC.getName()) != null ) {
							preFields.put(FieldType.PG_TDR_SC.getName(), documentObj.get(FieldType.PG_TDR_SC.getName()).toString());
						}
						
						if (documentObj.get(FieldType.PG_GST.getName()) != null ) {
							preFields.put(FieldType.PG_GST.getName(), documentObj.get(FieldType.PG_GST.getName()).toString());
						}
						
						preFields.logAllFields(
								"Received RECO&SETTLED/RECO&RECONCILED/SALE&CAPTURED/RECO&PENDING/REFUND&PENDING transaction details for transaction with Pg_Ref_Num: "
										+ pg_ref_num);
						fieldsList.add(preFields);
					}
				}
			} finally {
				cursor.close();
			}

			return fieldsList;
		} catch (Exception exception) {
			String message = "Error while reading list of transactions based on PgRefNum from database";
			logger.error(message, exception);
			throw new DatabaseException(ErrorType.DATABASE_ERROR, exception, message);
		}
	}
	
	public List<Fields> getPreviousSettlementOrSaleForPgRefNum(String pg_ref_num) throws DatabaseException {
		try {
			List<Fields> fieldsList = new ArrayList<Fields>();

			List<BasicDBObject> settledList = new ArrayList<BasicDBObject>();
			settledList.add(new BasicDBObject(FieldType.PG_REF_NUM.getName(), pg_ref_num));
			settledList.add(new BasicDBObject(FieldType.RECO_TXNTYPE.getName(), TransactionType.SALE.getName()));
			//settledList.add(new BasicDBObject(FieldType.STATUS.getName(), new Document("$in",Arrays.asList(StatusType.CAPTURED.getName(),StatusType.FAILED.getName()))));
			
//			settledList.add(new BasicDBObject(FieldType.STATUS.getName(),
//					new Document("$in",
//							Arrays.asList(StatusType.CAPTURED.getName(), StatusType.REJECTED.getName(),
//									StatusType.FAILED.getName(), StatusType.FAILED_AT_ACQUIRER.getName(),
//									StatusType.USER_INACTIVE.getName(), StatusType.ERROR.getName(),
//									StatusType.DENIED.getName()))));
			
//			settledList.add(new BasicDBObject(FieldType.STATUS.getName(), new Document("$in",Arrays.asList(StatusType.CAPTURED.getName(),StatusType.getInternalStatusForFailedAndCancelled("Failed","Cancelled")))));
			settledList.add(new BasicDBObject(FieldType.STATUS.getName(),
					new Document("$in", Arrays.asList(StatusType.CAPTURED.getName(), StatusType.DECLINED.getName(),
							StatusType.REJECTED.getName(), StatusType.ERROR.getName(), StatusType.TIMEOUT.getName(),
							StatusType.BROWSER_CLOSED.getName(), StatusType.CANCELLED.getName(),
							StatusType.DENIED.getName(), StatusType.DUPLICATE.getName(), StatusType.FAILED.getName(),
							StatusType.AUTHENTICATION_FAILED.getName(), StatusType.DENIED_BY_FRAUD.getName(),
							StatusType.ACQUIRER_DOWN.getName(), StatusType.FAILED_AT_ACQUIRER.getName(),
							StatusType.ACQUIRER_TIMEOUT.getName(), StatusType.USER_INACTIVE.getName()))));

			
			BasicDBObject saleQuery = new BasicDBObject("$and", settledList);
			
			List<BasicDBObject> recoList = new ArrayList<BasicDBObject>();
			recoList.add(new BasicDBObject(FieldType.PG_REF_NUM.getName(), pg_ref_num));
			recoList.add(new BasicDBObject(FieldType.RECO_TXNTYPE.getName(), TransactionType.RECO.getName()));
			// Done By chetan nagaria for change in settlement process to mark transaction as RNS
//			recoList.add(new BasicDBObject(FieldType.STATUS.getName(), StatusType.SETTLED.getName()));
			recoList.add(new BasicDBObject(FieldType.STATUS.getName(), new Document("$in",Arrays.asList(StatusType.SETTLED_RECONCILLED.getName(),StatusType.FORCE_CAPTURED.getName()))));
			
			BasicDBObject settledQuery = new BasicDBObject("$and", recoList);
			
			List<BasicDBObject> transList = new ArrayList<BasicDBObject>();
			transList.add(saleQuery);
			transList.add(settledQuery);

			BasicDBObject transQuery = new BasicDBObject("$or", transList);
			MongoDatabase dbIns = mongoInstance.getDB();
			MongoCollection<Document> collection = dbIns.getCollection(configProvider.getTransactionColl());
			MongoCursor<Document> cursor = collection.find(transQuery).iterator();
			try {
				while (cursor.hasNext()) {
					Document documentObj = (Document) cursor.next();
					if (null != documentObj) {
						Fields preFields = new Fields();
						for (int j = 0; j < documentObj.size(); j++) {
							for (String columnName : systemProperties.getTransactionFields()) {
								if (documentObj.get(columnName) != null) {
									preFields.put(columnName, documentObj.get(columnName).toString());
								}
							}
						}
						
						if ( documentObj.get(FieldType.ACQUIRER_TDR_SC.getName()) != null ) {
							preFields.put(FieldType.ACQUIRER_TDR_SC.getName(), documentObj.get(FieldType.ACQUIRER_TDR_SC.getName()).toString());
						}
						
						if ( documentObj.get(FieldType.ACQUIRER_GST.getName()) != null ) {
							preFields.put(FieldType.ACQUIRER_GST.getName(), documentObj.get(FieldType.ACQUIRER_GST.getName()).toString());
						}
						
						if (documentObj.get(FieldType.PG_TDR_SC.getName()) != null ) {
							preFields.put(FieldType.PG_TDR_SC.getName(), documentObj.get(FieldType.PG_TDR_SC.getName()).toString());
						}
						
						if (documentObj.get(FieldType.PG_GST.getName()) != null ) {
							preFields.put(FieldType.PG_GST.getName(), documentObj.get(FieldType.PG_GST.getName()).toString());
						}
						//added by RR
						if (documentObj.get(FieldType.ACQ_ID.getName()) != null ) {
							preFields.put(FieldType.ACQ_ID.getName(), documentObj.get(FieldType.ACQ_ID.getName()).toString());
						}
						if (documentObj.get(FieldType.ACQUIRER_TYPE.getName()) != null ) {
							preFields.put(FieldType.ACQUIRER_TYPE.getName(), documentObj.get(FieldType.ACQUIRER_TYPE.getName()).toString());
						}
						if (documentObj.get(FieldType.TOTAL_AMOUNT.getName()) != null ) {
							preFields.put(FieldType.RECO_AMOUNT.getName(), documentObj.get(FieldType.TOTAL_AMOUNT.getName()).toString());
						}
						//changed Reco_total_amt,ACQUIRER_TYPE

						//end by RR
						
						preFields.logAllFields(
								"Received SALE&CAPTURED/RECO&PENDING/REFUND&PENDING/RECO&SETTLED transaction details for transaction with Pg_Ref_Num: "
										+ pg_ref_num);
						fieldsList.add(preFields);
					}
				}
			} finally {
				cursor.close();
			}

			return fieldsList;
		} catch (Exception exception) {
			String message = "Error while reading list of transactions based on PgRefNum from database";
			logger.error(message, exception);
			throw new DatabaseException(ErrorType.DATABASE_ERROR, exception, message);
		}
	}
	
	public List<Fields> getPreviousEnrolledOrSaleForPgRefNum(String pg_ref_num) throws DatabaseException {
		try {
			List<Fields> fieldsList = new ArrayList<Fields>();

			List<BasicDBObject> enrollList = new ArrayList<BasicDBObject>();
			enrollList.add(new BasicDBObject(FieldType.PG_REF_NUM.getName(), pg_ref_num));
			enrollList.add(new BasicDBObject(FieldType.RECO_TXNTYPE.getName(), TransactionType.ENROLL.getName()));
			enrollList.add(new BasicDBObject(FieldType.STATUS.getName(), StatusType.ENROLLED.getName()));
			BasicDBObject enrollQuery = new BasicDBObject("$and", enrollList);

			List<BasicDBObject> timeoutList = new ArrayList<BasicDBObject>();
			timeoutList.add(new BasicDBObject(FieldType.PG_REF_NUM.getName(), pg_ref_num));
			timeoutList.add(new BasicDBObject(FieldType.RECO_TXNTYPE.getName(), TransactionType.SALE.getName()));
			timeoutList.add(new BasicDBObject(FieldType.STATUS.getName(), StatusType.TIMEOUT.getName()));
			BasicDBObject timeoutQuery = new BasicDBObject("$and", timeoutList);
			
			List<BasicDBObject> stbList = new ArrayList<BasicDBObject>();
			stbList.add(new BasicDBObject(FieldType.PG_REF_NUM.getName(), pg_ref_num));
			stbList.add(new BasicDBObject(FieldType.RECO_TXNTYPE.getName(), TransactionType.SALE.getName()));
			stbList.add(new BasicDBObject(FieldType.STATUS.getName(), StatusType.SENT_TO_BANK.getName()));
			BasicDBObject stbQuery = new BasicDBObject("$and", stbList);
			
			List<BasicDBObject> transList = new ArrayList<BasicDBObject>();
			transList.add(enrollQuery);
			transList.add(timeoutQuery);
			transList.add(stbQuery);

			BasicDBObject transQuery = new BasicDBObject("$or", transList);

			MongoDatabase dbIns = mongoInstance.getDB();

			MongoCollection<Document> collection = dbIns.getCollection(configProvider.getTransactionColl());

			MongoCursor<Document> cursor = collection.find(transQuery).iterator();
			try {
				while (cursor.hasNext()) {
					Document documentObj = (Document) cursor.next();
					if (null != documentObj) {
						Fields preFields = new Fields();
						for (int j = 0; j < documentObj.size(); j++) {
							for (String columnName : systemProperties.getTransactionFields()) {
								if (documentObj.get(columnName) != null) {
									preFields.put(columnName, documentObj.get(columnName).toString());
								}
							}
						}
						
						if ( documentObj.get(FieldType.ACQUIRER_TDR_SC.getName()) != null ) {
							preFields.put(FieldType.ACQUIRER_TDR_SC.getName(), documentObj.get(FieldType.ACQUIRER_TDR_SC.getName()).toString());
						}
						
						if ( documentObj.get(FieldType.ACQUIRER_GST.getName()) != null ) {
							preFields.put(FieldType.ACQUIRER_GST.getName(), documentObj.get(FieldType.ACQUIRER_GST.getName()).toString());
						}
						
						if (documentObj.get(FieldType.PG_TDR_SC.getName()) != null ) {
							preFields.put(FieldType.PG_TDR_SC.getName(), documentObj.get(FieldType.PG_TDR_SC.getName()).toString());
						}
						
						if (documentObj.get(FieldType.PG_GST.getName()) != null ) {
							preFields.put(FieldType.PG_GST.getName(), documentObj.get(FieldType.PG_GST.getName()).toString());
						}
						
						preFields.logAllFields(
								"Received ENROLL&ENROLLED/SALE&TIMEOUT/SALE&SENT_TO_BANK transaction details for transaction with Pg_Ref_Num: "
										+ pg_ref_num);
						fieldsList.add(preFields);
					}
				}
			} finally {
				cursor.close();
			}

			return fieldsList;
		} catch (Exception exception) {
			String message = "Error while reading list of transactions based on PgRefNum from database";
			logger.error(message, exception);
			throw new DatabaseException(ErrorType.DATABASE_ERROR, exception, message);
		}
	}
	
	public List<Fields> getPreviousSaleForPgRefNum(String pg_ref_num) throws DatabaseException {
		try {
			List<Fields> fieldsList = new ArrayList<Fields>();

			List<BasicDBObject> saleList = new ArrayList<BasicDBObject>();
			saleList.add(new BasicDBObject(FieldType.PG_REF_NUM.getName(), pg_ref_num));
			saleList.add(new BasicDBObject(FieldType.RECO_TXNTYPE.getName(), TransactionType.SALE.getName()));
			BasicDBObject saleQuery = new BasicDBObject("$and", saleList);
			
			List<BasicDBObject> enrollList = new ArrayList<BasicDBObject>();
			enrollList.add(new BasicDBObject(FieldType.PG_REF_NUM.getName(), pg_ref_num));
			enrollList.add(new BasicDBObject(FieldType.RECO_TXNTYPE.getName(), TransactionType.ENROLL.getName()));
			BasicDBObject enrollQuery = new BasicDBObject("$and", enrollList);
			
			List<BasicDBObject> transList = new ArrayList<BasicDBObject>();
			transList.add(saleQuery);
			transList.add(enrollQuery);

			BasicDBObject transQuery = new BasicDBObject("$or", transList);

			MongoDatabase dbIns = mongoInstance.getDB();

			MongoCollection<Document> collection = dbIns.getCollection(configProvider.getTransactionColl());

			List<Document> documents = collection.find(transQuery).sort(new BasicDBObject("CREATE_DATE",-1)).limit(1).into(new ArrayList<Document>());
				if(documents.size()>0) {
					Document documentObj = documents.get(0);
					if (null != documentObj) {
						Fields preFields = new Fields();
						for (int j = 0; j < documentObj.size(); j++) {
							for (String columnName : systemProperties.getTransactionFields()) {
								if (documentObj.get(columnName) != null) {
									preFields.put(columnName, documentObj.get(columnName).toString());
								}
							}
						}
						
						if ( documentObj.get(FieldType.ACQUIRER_TDR_SC.getName()) != null ) {
							preFields.put(FieldType.ACQUIRER_TDR_SC.getName(), documentObj.get(FieldType.ACQUIRER_TDR_SC.getName()).toString());
						}
						
						if ( documentObj.get(FieldType.ACQUIRER_GST.getName()) != null ) {
							preFields.put(FieldType.ACQUIRER_GST.getName(), documentObj.get(FieldType.ACQUIRER_GST.getName()).toString());
						}
						
						if (documentObj.get(FieldType.PG_TDR_SC.getName()) != null ) {
							preFields.put(FieldType.PG_TDR_SC.getName(), documentObj.get(FieldType.PG_TDR_SC.getName()).toString());
						}
						
						if (documentObj.get(FieldType.PG_GST.getName()) != null ) {
							preFields.put(FieldType.PG_GST.getName(), documentObj.get(FieldType.PG_GST.getName()).toString());
						}
						
						preFields.logAllFields(
								"Received ENROLL&ENROLLED/SALE&TIMEOUT/SALE&SENT_TO_BANK transaction details for transaction with Pg_Ref_Num: "
										+ pg_ref_num);
						fieldsList.add(preFields);
					}
				}

			return fieldsList;
		} catch (Exception exception) {
			String message = "Error while reading list of transactions based on PgRefNum from database";
			logger.error(message, exception);
			throw new DatabaseException(ErrorType.DATABASE_ERROR, exception, message);
		}
	}
	
	public List<Fields> getPreviousSaleCapturedForOid(String oid) throws DatabaseException {
		try {
			List<Fields> fieldsList = new ArrayList<Fields>();

			List<BasicDBObject> settledList = new ArrayList<BasicDBObject>();
			settledList.add(new BasicDBObject(FieldType.OID.getName(), oid));
			settledList.add(new BasicDBObject(FieldType.RECO_TXNTYPE.getName(), TransactionType.SALE.getName()));
			settledList.add(new BasicDBObject(FieldType.STATUS.getName(), StatusType.CAPTURED.getName()));
			BasicDBObject saleQuery = new BasicDBObject("$and", settledList);
			
			MongoDatabase dbIns = mongoInstance.getDB();
			MongoCollection<Document> collection = dbIns.getCollection(configProvider.getTransactionColl());
			MongoCursor<Document> cursor = collection.find(saleQuery).iterator();
			try {
				while (cursor.hasNext()) {
					Document documentObj = (Document) cursor.next();
					if (null != documentObj) {
						Fields preFields = new Fields();
						for (int j = 0; j < documentObj.size(); j++) {
							for (String columnName : systemProperties.getTransactionFields()) {
								if (documentObj.get(columnName) != null) {
									preFields.put(columnName, documentObj.get(columnName).toString());
								}
							}
						}
						
						if ( documentObj.get(FieldType.ACQUIRER_TDR_SC.getName()) != null ) {
							preFields.put(FieldType.ACQUIRER_TDR_SC.getName(), documentObj.get(FieldType.ACQUIRER_TDR_SC.getName()).toString());
						}
						
						if ( documentObj.get(FieldType.ACQUIRER_GST.getName()) != null ) {
							preFields.put(FieldType.ACQUIRER_GST.getName(), documentObj.get(FieldType.ACQUIRER_GST.getName()).toString());
						}
						
						if (documentObj.get(FieldType.PG_TDR_SC.getName()) != null ) {
							preFields.put(FieldType.PG_TDR_SC.getName(), documentObj.get(FieldType.PG_TDR_SC.getName()).toString());
						}
						
						if (documentObj.get(FieldType.PG_GST.getName()) != null ) {
							preFields.put(FieldType.PG_GST.getName(), documentObj.get(FieldType.PG_GST.getName()).toString());
						}
						
						preFields.logAllFields(
								"Received SALE&CAPTURED transaction details for transaction with OID: "
										+ oid);
						fieldsList.add(preFields);
					}
				}
			} finally {
				cursor.close();
			}

			return fieldsList;
		} catch (Exception exception) {
			String message = "Error while reading list of transactions based on OID from database";
			logger.error(message, exception);
			throw new DatabaseException(ErrorType.DATABASE_ERROR, exception, message);
		}
	}
	
	public List<Fields> getPreviousRefundCapturedForPgRefNum(String pgRefNum) throws DatabaseException {
		try {
			List<Fields> fieldsList = new ArrayList<Fields>();

			List<BasicDBObject> refundList = new ArrayList<BasicDBObject>();
			refundList.add(new BasicDBObject(FieldType.PG_REF_NUM.getName(), pgRefNum));
			refundList.add(new BasicDBObject(FieldType.RECO_TXNTYPE.getName(), TransactionType.REFUND.getName()));
			BasicDBObject refundQuery = new BasicDBObject("$and", refundList);
			MongoDatabase dbIns = mongoInstance.getDB();
			MongoCollection<Document> collection = dbIns.getCollection(configProvider.getTransactionColl());
			MongoCursor<Document> cursor = collection.find(refundQuery).sort(new BasicDBObject(FieldType.CREATE_DATE.getName(),-1)).iterator();
			try {
				while (cursor.hasNext()) {
					Document documentObj = (Document) cursor.next();
					if (null != documentObj) {
						Fields preFields = new Fields();
						for (int j = 0; j < documentObj.size(); j++) {
							for (String columnName : systemProperties.getTransactionFields()) {
								if (documentObj.get(columnName) != null) {
									preFields.put(columnName, documentObj.get(columnName).toString());
								}
							}
						}
						
						if ( documentObj.get(FieldType.ACQUIRER_TDR_SC.getName()) != null ) {
							preFields.put(FieldType.ACQUIRER_TDR_SC.getName(), documentObj.get(FieldType.ACQUIRER_TDR_SC.getName()).toString());
						}
						
						if ( documentObj.get(FieldType.ACQUIRER_GST.getName()) != null ) {
							preFields.put(FieldType.ACQUIRER_GST.getName(), documentObj.get(FieldType.ACQUIRER_GST.getName()).toString());
						}
						
						if (documentObj.get(FieldType.PG_TDR_SC.getName()) != null ) {
							preFields.put(FieldType.PG_TDR_SC.getName(), documentObj.get(FieldType.PG_TDR_SC.getName()).toString());
						}
						
						if (documentObj.get(FieldType.PG_GST.getName()) != null ) {
							preFields.put(FieldType.PG_GST.getName(), documentObj.get(FieldType.PG_GST.getName()).toString());
						}
						
						preFields.logAllFields(
								"Received REFUND transaction details for transaction with PG_REF_NUM: "
										+ pgRefNum);
						fieldsList.add(preFields);
					}
				}
			} finally {
				cursor.close();
			}

			return fieldsList;
		} catch (Exception exception) {
			String message = "Error while reading list of getPreviousRefundCapturedForPgRefNum from database";
			logger.error(message, exception);
			throw new DatabaseException(ErrorType.DATABASE_ERROR, exception, message);
		}
	}
	
	public List<Fields> getPreviousSaleCapturedForPgRefNum(String pgRefNum) throws DatabaseException {
		try {
			List<Fields> fieldsList = new ArrayList<Fields>();

			List<BasicDBObject> settledList = new ArrayList<BasicDBObject>();
			settledList.add(new BasicDBObject(FieldType.PG_REF_NUM.getName(), pgRefNum));
			settledList.add(new BasicDBObject(FieldType.RECO_TXNTYPE.getName(), TransactionType.SALE.getName()));
			settledList.add(new BasicDBObject(FieldType.STATUS.getName(), StatusType.CAPTURED.getName()));
			BasicDBObject saleQuery = new BasicDBObject("$and", settledList);
			
			MongoDatabase dbIns = mongoInstance.getDB();
			MongoCollection<Document> collection = dbIns.getCollection(configProvider.getTransactionColl());
			MongoCursor<Document> cursor = collection.find(saleQuery).iterator();
			try {
				while (cursor.hasNext()) {
					Document documentObj = (Document) cursor.next();
					if (null != documentObj) {
						Fields preFields = new Fields();
						for (int j = 0; j < documentObj.size(); j++) {
							for (String columnName : systemProperties.getTransactionFields()) {
								if (documentObj.get(columnName) != null) {
									preFields.put(columnName, documentObj.get(columnName).toString());
								}
							}
						}
						
						if ( documentObj.get(FieldType.ACQUIRER_TDR_SC.getName()) != null ) {
							preFields.put(FieldType.ACQUIRER_TDR_SC.getName(), documentObj.get(FieldType.ACQUIRER_TDR_SC.getName()).toString());
						}
						
						if ( documentObj.get(FieldType.ACQUIRER_GST.getName()) != null ) {
							preFields.put(FieldType.ACQUIRER_GST.getName(), documentObj.get(FieldType.ACQUIRER_GST.getName()).toString());
						}
						
						if (documentObj.get(FieldType.PG_TDR_SC.getName()) != null ) {
							preFields.put(FieldType.PG_TDR_SC.getName(), documentObj.get(FieldType.PG_TDR_SC.getName()).toString());
						}
						
						if (documentObj.get(FieldType.PG_GST.getName()) != null ) {
							preFields.put(FieldType.PG_GST.getName(), documentObj.get(FieldType.PG_GST.getName()).toString());
						}
						
						preFields.logAllFields(
								"Received SALE&CAPTURED transaction details for transaction with PG_REF_NUM: "
										+ pgRefNum);
						fieldsList.add(preFields);
					}
				}
			} finally {
				cursor.close();
			}

			return fieldsList;
		} catch (Exception exception) {
			String message = "Error while reading list of transactions based on OID from database";
			logger.error(message, exception);
			throw new DatabaseException(ErrorType.DATABASE_ERROR, exception, message);
		}
	}
	
	public List<Fields> getPreviousRecoSettledForPgRefNum(String pgRefNum) throws DatabaseException {
		try {
			List<Fields> fieldsList = new ArrayList<Fields>();

			List<BasicDBObject> settledList = new ArrayList<BasicDBObject>();
			settledList.add(new BasicDBObject(FieldType.PG_REF_NUM.getName(), pgRefNum));
			settledList.add(new BasicDBObject(FieldType.RECO_TXNTYPE.getName(), TransactionType.RECO.getName()));
			
			// Done By chetan nagaria for change in settlement process to mark transaction as RNS
			settledList.add(new BasicDBObject(FieldType.STATUS.getName(), StatusType.SETTLED_RECONCILLED.getName()));
//			settledList.add(new BasicDBObject(FieldType.STATUS.getName(), StatusType.SETTLED.getName()));
			BasicDBObject settledQuery = new BasicDBObject("$and", settledList);
			
			List<BasicDBObject> saleList = new ArrayList<BasicDBObject>();
			saleList.add(new BasicDBObject(FieldType.PG_REF_NUM.getName(), pgRefNum));
			saleList.add(new BasicDBObject(FieldType.RECO_TXNTYPE.getName(), TransactionType.SALE.getName()));
			saleList.add(new BasicDBObject(FieldType.STATUS.getName(), StatusType.CAPTURED.getName()));
			BasicDBObject saleQuery = new BasicDBObject("$and", saleList);
			
			List<BasicDBObject> transList = new ArrayList<BasicDBObject>();
			transList.add(settledQuery);
			transList.add(saleQuery);

			BasicDBObject transQuery = new BasicDBObject("$or", transList);
			MongoDatabase dbIns = mongoInstance.getDB();
			MongoCollection<Document> collection = dbIns.getCollection(configProvider.getTransactionColl());
			MongoCursor<Document> cursor = collection.find(transQuery).iterator();
			try {
				while (cursor.hasNext()) {
					Document documentObj = (Document) cursor.next();
					if (null != documentObj) {
						Fields preFields = new Fields();
						for (int j = 0; j < documentObj.size(); j++) {
							for (String columnName : systemProperties.getTransactionFields()) {
								if (documentObj.get(columnName) != null) {
									preFields.put(columnName, documentObj.get(columnName).toString());
								}
							}
						}
						
						if ( documentObj.get(FieldType.ACQUIRER_TDR_SC.getName()) != null ) {
							preFields.put(FieldType.ACQUIRER_TDR_SC.getName(), documentObj.get(FieldType.ACQUIRER_TDR_SC.getName()).toString());
						}
						
						if ( documentObj.get(FieldType.ACQUIRER_GST.getName()) != null ) {
							preFields.put(FieldType.ACQUIRER_GST.getName(), documentObj.get(FieldType.ACQUIRER_GST.getName()).toString());
						}
						
						if (documentObj.get(FieldType.PG_TDR_SC.getName()) != null ) {
							preFields.put(FieldType.PG_TDR_SC.getName(), documentObj.get(FieldType.PG_TDR_SC.getName()).toString());
						}
						
						if (documentObj.get(FieldType.PG_GST.getName()) != null ) {
							preFields.put(FieldType.PG_GST.getName(), documentObj.get(FieldType.PG_GST.getName()).toString());
						}
						
						preFields.logAllFields(
								"Received SALE&CAPTURED transaction details for transaction with PG_REF_NUM: "
										+ pgRefNum);
						fieldsList.add(preFields);
					}
				}
			} finally {
				cursor.close();
			}

			return fieldsList;
		} catch (Exception exception) {
			String message = "Error while reading list of transactions based on OID from database";
			logger.error(message, exception);
			throw new DatabaseException(ErrorType.DATABASE_ERROR, exception, message);
		}
	}
	
	public void insertRecoReportingFields(Fields fields) throws DatabaseException {
		try {
			Date dNow = new Date();
			String dateNow = DateCreater.formatDateForDb(dNow);

			BasicDBObject newFieldsObj = new BasicDBObject();
			newFieldsObj.put("_id", fields.get(FieldType.TXN_ID.getName()));
			for (int i = 0; i < fields.size(); i++) {
				Collection<String> recoReportingFields = systemProperties.getReportingFields();
				for (String columnName : recoReportingFields) {
					if (columnName.equals(FieldType.CREATE_DATE.getName())) {
						newFieldsObj.put(columnName, dateNow);
					} else if (columnName.equals(FieldType.UPDATE_DATE.getName())) {
						newFieldsObj.put(columnName, dateNow);
					} else {
						newFieldsObj.put(columnName, fields.get(columnName));
					}
				}
			}
			MongoDatabase dbIns = mongoInstance.getDB();
			MongoCollection<Document> collection = dbIns.getCollection(configProvider.getReportingColl());

			Document doc = new Document(newFieldsObj);
			collection.insertOne(doc);
		} catch (Exception exception) {
			String message = "Error while inserting reco reporting details in database";
			logger.error(message, exception);
			throw new DatabaseException(ErrorType.DATABASE_ERROR, exception, message);
		}
	}
	
	// Get Previous Sale Date 
		public String getSaleDate(String pgRefNum) throws DatabaseException {
			List<Fields> fieldsList = new ArrayList<Fields>();
			fieldsList = getPreviousSaleCapturedForPgRefNum(pgRefNum);
			if(fieldsList.size()>0) {
				return  fieldsList.get(0).get(FieldType.CREATE_DATE.getName());
			}
			return null;
		}
		
		// Get Previous Refund Date 
		public String getRefundDate(String pgRefNum) throws DatabaseException {
			List<Fields> fieldsList = new ArrayList<Fields>();
			Boolean capturedFlag = false;
			String capturedDate = null;
			fieldsList = getPreviousRefundCapturedForPgRefNum(pgRefNum);
			
			for(Fields fields : fieldsList) {
				if(fields.get(FieldType.STATUS.getName()).equals(StatusType.CAPTURED.getName())) {
					capturedDate = fields.get(FieldType.CREATE_DATE.getName());
					capturedFlag = true;
				}
			}
			
			if(fieldsList.size()>0) {
				if(capturedFlag == false)
					capturedDate =  fieldsList.get(0).get(FieldType.CREATE_DATE.getName());
			}
			return capturedDate;
		}
		
		
		public void updateStatusColl(Fields fields, Document document) {

			logger.info("updateStatusColl fields :  "+fields.getFieldsAsString());
			logger.info("updateStatusColl document :  "+document.toString());
			try {
				MongoDatabase dbIns = mongoInstance.getDB();

				String orderId = document.get(FieldType.RECO_ORDER_ID.getName()).toString();
				String oid = document.get(FieldType.OID.getName()).toString();
				String origTxnType = fields.get(FieldType.ORIG_TXNTYPE.getName());
				String pgRefNum = fields.get(FieldType.PG_REF_NUM.getName());
				String txnType = fields.get(FieldType.RECO_TXNTYPE.getName());
				
				if (txnType.equalsIgnoreCase(TransactionType.INVALID.getName()) || txnType.equalsIgnoreCase(TransactionType.NEWORDER.getName()) ||
						txnType.equalsIgnoreCase(TransactionType.RECO.getName())) {
					txnType = TransactionType.SALE.getName();
				}
				
				if (txnType.equalsIgnoreCase(TransactionType.REFUND.getName()) || txnType.equalsIgnoreCase(TransactionType.REFUNDRECO.getName()) ) {
					txnType = TransactionType.REFUND.getName();
				}
				
				
				
				if (StringUtils.isBlank(origTxnType)) {
					origTxnType = txnType;
				}

				if (origTxnType.equalsIgnoreCase(TransactionType.INVALID.getName())) {
					origTxnType = TransactionType.SALE.getName();
				}
				
				if (origTxnType.equalsIgnoreCase(TransactionType.STATUS.getName())) {
					origTxnType = txnType;
				}

				// SALE
				if (origTxnType.equalsIgnoreCase(TransactionType.SALE.getName())) {
					if (StringUtils.isBlank(orderId) || StringUtils.isBlank(oid) || StringUtils.isBlank(origTxnType)) {

						logger.info("Cannot update transaction status collection for combination " + " Order Id = "
								+ orderId + " OID = " + oid + " orig Txn Type = " + origTxnType);
						logger.info("Txn cannot be added , moving to transactionStatusException ");

						Document doc = new Document();
						doc.put(FieldType.RECO_ORDER_ID.getName(), orderId);
						doc.put(FieldType.OID.getName(), oid);
						doc.put(FieldType.ORIG_TXNTYPE.getName(), origTxnType);

						MongoCollection<Document> excepColl = dbIns.getCollection(configProvider.getTxnExcepColl());

						excepColl.insertOne(doc);
					} else {

						MongoCollection<Document> coll = dbIns.getCollection(configProvider.getTxnStatusColl());

						List<BasicDBObject> dbObjList = new ArrayList<BasicDBObject>();
						dbObjList.add(new BasicDBObject(FieldType.RECO_ORDER_ID.getName(), orderId));
						dbObjList.add(new BasicDBObject(FieldType.OID.getName(), oid));

						dbObjList.add(new BasicDBObject(FieldType.ORIG_TXNTYPE.getName(), origTxnType));

						BasicDBObject andQuery = new BasicDBObject("$and", dbObjList);

						FindIterable<Document> cursor = coll.find(andQuery);

						if (cursor.iterator().hasNext()) {

							String transactionStatusFields = configProvider.getTransactionStatusFields();
							String transactionStatusFieldsArr[] = transactionStatusFields.split(",");

							MongoCollection<Document> txnStatusColl = dbIns.getCollection(configProvider.getTxnStatusColl());

							BasicDBObject searchQuery = andQuery;
							BasicDBObject updateFields = new BasicDBObject();

							String status = fields.get(FieldType.STATUS.getName());
							String statusALias = resolveStatus(status);
							
							for (String key : transactionStatusFieldsArr) {
								// Done By chetan nagaria for change in settlement process to mark transaction as RNS
//								if (status.equalsIgnoreCase(StatusType.SETTLED.getName())) {
								if (status.equalsIgnoreCase(StatusType.SETTLED_RECONCILLED.getName())||status.equalsIgnoreCase(StatusType.FORCE_CAPTURED.getName())) {
									
									if ((key.equalsIgnoreCase(FieldType.DATE_INDEX.getName())) || (key.equalsIgnoreCase(FieldType.CREATE_DATE.getName())) 
											|| (key.equalsIgnoreCase(FieldType.UPDATE_DATE.getName()))
											|| (key.equalsIgnoreCase(FieldType.INSERTION_DATE.getName()))){
										continue;
									}
									
									if ((key.equalsIgnoreCase(FieldType.SETTLEMENT_DATE.getName()))){
										updateFields.put(key, document.get(FieldType.CREATE_DATE.getName()));
										continue;
									}
									
									if ((key.equalsIgnoreCase(FieldType.SETTLEMENT_FLAG.getName()))){
										updateFields.put(key, "Y");
										continue;
									}
									
									if ((key.equalsIgnoreCase(FieldType.SETTLEMENT_DATE_INDEX.getName()))){
										updateFields.put(key, document.get(FieldType.DATE_INDEX.getName()));
										continue;
									}
									
								}
								
								if (document.get(key) != null) {
									updateFields.put(key, document.get(key).toString());
								} else {
									updateFields.put(key, document.get(key));
								}

							}

							
							updateFields.put(FieldType.ALIAS_STATUS.getName(), statusALias);
							updateFields.put(FieldType.ORIG_TXNTYPE.getName(), origTxnType);
							txnStatusColl.updateOne(searchQuery, new BasicDBObject("$set", updateFields));

						} else {

							MongoCollection<Document> txnStatusColl = dbIns.getCollection(configProvider.getTxnStatusColl());

							String status = fields.get(FieldType.STATUS.getName());
							String statusALias = resolveStatus(status);
							document.put(FieldType.ALIAS_STATUS.getName(), statusALias);
							document.put(FieldType.ORIG_TXNTYPE.getName(), origTxnType);
							txnStatusColl.insertOne(document);

						}

					}
				}

				// REFUND

				if (origTxnType.equalsIgnoreCase(TransactionType.REFUND.getName())) {
					if (StringUtils.isBlank(pgRefNum) || StringUtils.isBlank(oid) || StringUtils.isBlank(origTxnType)) {

						logger.info("Cannot update transaction status collection for combination " + " PG REF NUM = "
								+ pgRefNum + " OID = " + oid + " orig Txn Type = " + origTxnType);
						logger.info("Txn cannot be added , moving to transactionStatusException ");

						Document doc = new Document();
						doc.put(FieldType.PG_REF_NUM.getName(), pgRefNum);
						doc.put(FieldType.OID.getName(), oid);
						doc.put(FieldType.ORIG_TXNTYPE.getName(), origTxnType);

						MongoCollection<Document> excepColl = dbIns.getCollection(configProvider.getTxnExcepColl());

						excepColl.insertOne(doc);
					} else {

						MongoCollection<Document> coll = dbIns.getCollection(configProvider.getTxnStatusColl());

						List<BasicDBObject> dbObjList = new ArrayList<BasicDBObject>();
						dbObjList.add(new BasicDBObject(FieldType.PG_REF_NUM.getName(), pgRefNum));
						dbObjList.add(new BasicDBObject(FieldType.OID.getName(), oid));
						dbObjList.add(new BasicDBObject(FieldType.ORIG_TXNTYPE.getName(), origTxnType));

						BasicDBObject andQuery = new BasicDBObject("$and", dbObjList);

						FindIterable<Document> cursor = coll.find(andQuery);

						if (cursor.iterator().hasNext()) {

							String transactionStatusFields = configProvider.getTransactionStatusFields();
							String transactionStatusFieldsArr[] = transactionStatusFields.split(",");

							MongoCollection<Document> txnStatusColl = dbIns.getCollection(configProvider.getTxnStatusColl());

							BasicDBObject searchQuery = andQuery;
							BasicDBObject updateFields = new BasicDBObject();

							String status = fields.get(FieldType.STATUS.getName());
							String statusALias = resolveStatus(status);
							
							for (String key : transactionStatusFieldsArr) {
								// Done By chetan nagaria for change in settlement process to mark transaction as RNS
//								if (status.equalsIgnoreCase(StatusType.SETTLED.getName())) {
									if (status.equalsIgnoreCase(StatusType.SETTLED_RECONCILLED.getName())) {									
									if ((key.equalsIgnoreCase(FieldType.DATE_INDEX.getName())) || (key.equalsIgnoreCase(FieldType.CREATE_DATE.getName())) 
											|| (key.equalsIgnoreCase(FieldType.UPDATE_DATE.getName()))
											|| (key.equalsIgnoreCase(FieldType.INSERTION_DATE.getName()))){
										continue;
									}
									
									if ((key.equalsIgnoreCase(FieldType.SETTLEMENT_DATE.getName()))){
										updateFields.put(key, document.get(FieldType.CREATE_DATE.getName()));
										continue;
									}
									
									if ((key.equalsIgnoreCase(FieldType.SETTLEMENT_FLAG.getName()))){
										updateFields.put(key, "Y");
										continue;
									}
									
									if ((key.equalsIgnoreCase(FieldType.SETTLEMENT_DATE_INDEX.getName()))){
										updateFields.put(key, document.get(FieldType.DATE_INDEX.getName()));
										continue;
									}
									
								}
								
								if (document.get(key) != null) {
									updateFields.put(key, document.get(key).toString());
								} else {
									updateFields.put(key, document.get(key));
								}

							}

							
							updateFields.put(FieldType.ALIAS_STATUS.getName(), statusALias);
							updateFields.put(FieldType.ORIG_TXNTYPE.getName(), origTxnType);
							txnStatusColl.updateOne(searchQuery, new BasicDBObject("$set", updateFields));

						} else {

							MongoCollection<Document> txnStatusColl = dbIns.getCollection(configProvider.getTxnStatusColl());

							String status = fields.get(FieldType.STATUS.getName());
							String statusALias = resolveStatus(status);
							document.put(FieldType.ALIAS_STATUS.getName(), statusALias);
							document.put(FieldType.ORIG_TXNTYPE.getName(), origTxnType);
							
							
							//rns
							txnStatusColl.insertOne(document);

						}


					}
				}
				
				
			}

			catch (Exception e) {
				logger.error("Exception in adding txn to transaction status", e);
			}

		}
		
		public String resolveStatus(String status) {

			if (StringUtils.isBlank(status)) {
				return status;
			} else {
				if (status.equals(StatusType.CAPTURED.getName())) {
					return "Captured";
					// Done By chetan nagaria for change in settlement process to mark transaction as RNS
//				} else if (status.equals(StatusType.SETTLED.getName())) {
				} else if (status.equals(StatusType.SETTLED_RECONCILLED.getName())) {
					return "RNS";

				} else if (status.equals(StatusType.PENDING.getName()) || status.equals(StatusType.SENT_TO_BANK.getName())
						|| status.equals(StatusType.ENROLLED.getName())) {
					return "Pending";

				} else if (status.equals(StatusType.BROWSER_CLOSED.getName())
						|| status.equals(StatusType.CANCELLED.getName())
						|| status.equals(StatusType.USER_INACTIVE.getName())) {
					return "Cancelled";

				} else if (status.equals(StatusType.INVALID.getName()) || status.equals(StatusType.DUPLICATE.getName())
						) {
					return "Invalid";

				} 
				else {
					return "Failed";
				}

			}
		}
		
	/*
	 * public void callTxnStatusUpdate(Document doc) {
	 * 
	 * String serviceUrl = "http://10.10.7.9:8080/sms/updateTxnStatus"; try {
	 * 
	 * JSONObject json = new JSONObject(); for (String key : doc.keySet()) {
	 * 
	 * if (null != doc.get(key)) { json.put(key, doc.get(key).toString()); } else {
	 * json.put(key, doc.get(key)); }
	 * 
	 * }
	 * 
	 * String url = serviceUrl; CloseableHttpClient httpClient =
	 * HttpClientBuilder.create().build(); HttpPost request = new HttpPost(url);
	 * StringEntity params = new StringEntity(json.toString());
	 * logger.info("JSON REQ "+json.toString()); request.addHeader("Content-Type",
	 * "application/json"); request.setEntity(params); HttpResponse resp =
	 * httpClient.execute(request); StatusLine statusLine = resp.getStatusLine();
	 * int statusCode = resp.getStatusLine().getStatusCode(); } catch (Exception
	 * exception) { logger.error("exception in updating transaction status " ,
	 * exception); } }
	 */

}