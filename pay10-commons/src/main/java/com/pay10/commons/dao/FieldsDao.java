package com.pay10.commons.dao;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.servlet.jsp.tagext.TryCatchFinally;

import com.pay10.commons.util.*;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.hibernate.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.google.gson.JsonObject;
import com.mongodb.BasicDBObject;
import com.mongodb.BasicDBObjectBuilder;
import com.mongodb.MongoException;
import com.mongodb.client.AggregateIterable;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Sorts;
import com.mongodb.client.model.UpdateOptions;
import com.mongodb.client.model.Updates;
import com.mongodb.client.result.UpdateResult;
import com.pay10.commons.api.Hasher;
import com.pay10.commons.api.SmsSender;
import com.pay10.commons.dto.MerchantWalletPODTO;
import com.pay10.commons.exception.ErrorType;
import com.pay10.commons.exception.SystemException;
import com.pay10.commons.mongo.MerchantWalletPODao;
import com.pay10.commons.mongo.MongoInstance;
import com.pay10.commons.mongo.WalletHistoryRepository;
import com.pay10.commons.user.AccountCurrencyRegion;
import com.pay10.commons.user.CardHolderType;
import com.pay10.commons.user.CompanyProfileDao;
import com.pay10.commons.user.MultCurrencyCodeDao;
import com.pay10.commons.user.TransactionSearchNew;
import com.pay10.commons.user.User;
import com.pay10.commons.user.UserDao;

@Service
public class FieldsDao {

	private static Logger logger = LoggerFactory.getLogger(FieldsDao.class.getName());

	// All static fields
	private static final Collection<String> allDBRequestFields = SystemProperties.getAllDBRequestFields();
	private static final Collection<String> aLLDB_Fields = SystemProperties.getDBFields();
	private static final Collection<String> nodalDB_Fields = SystemProperties.getNodalDBFields();
	private static final String prefix = "MONGO_DB_";
	private SystemProperties systemProperties = new SystemProperties();
	private ExecutorService executorService = Executors.newSingleThreadScheduledExecutor();
	@Autowired
	private MongoInstance mongoInstance;

	@Autowired
	private CompanyProfileDao companyProfileDao;

	@Autowired
	private PropertiesManager propertiesManager;

	@Autowired
	private RouterConfigurationService routerConfigurationService;

	@Autowired
	private SmsSender smsSender;

	@Autowired
	private UserDao userDao;

	@Autowired
	private RouterConfigurationDao routerConfigurationDao;
	final String bsesPayid = PropertiesManager.propertiesMap.get(Constants.BSESPAYID.getValue());

	private static int smsCount = 0;

	public void updateNewOrderDetails(Fields fields) throws SystemException {
		try {
			String amountString = fields.get(FieldType.AMOUNT.getName());
			String surchargeAmountString = fields.get(FieldType.SURCHARGE_AMOUNT.getName());
			String currencyString = fields.get(FieldType.CURRENCY_CODE.getName());

			String amount = "0";
			if (!StringUtils.isEmpty(amountString) && !StringUtils.isEmpty(currencyString)) {
				amount = Amount.toDecimal(amountString, currencyString);
			}
			fields.put(FieldType.AMOUNT.getName(), amount);
			String surchargeAmount = "0";
			if (!StringUtils.isEmpty(surchargeAmountString) && !StringUtils.isEmpty(currencyString)) {
				surchargeAmount = Amount.toDecimal(surchargeAmountString, currencyString);
			}
			fields.put(FieldType.SURCHARGE_AMOUNT.getName(), surchargeAmount);
			BasicDBObject oldFieldsObj = new BasicDBObject();
			oldFieldsObj.put(FieldType.TXN_ID.getName(), fields.get(FieldType.TXN_ID.getName()));
			Document oldDoc = new Document(oldFieldsObj);
			BasicDBObject newFieldsObj = new BasicDBObject();
			if (fields.contains(FieldType.INTERNAL_CUST_IP.getName())) {
				String ip = fields.get(FieldType.INTERNAL_CUST_IP.getName());
				ip = StringUtils.isBlank(ip) ? getIPFromInitiateRequest(fields.get(FieldType.OID.getName())) : ip;
				fields.put(FieldType.INTERNAL_CUST_IP.getName(), ip);
			}
			for (int i = 0; i < fields.size(); i++) {
				Collection<String> aLLDB_Fields = SystemProperties.getDBFields();
				for (String columnName : aLLDB_Fields) {
					newFieldsObj.put(columnName, fields.get(columnName));
				}
			}
			BasicDBObject updateObj = new BasicDBObject();
			updateObj.put("$set", newFieldsObj);
			MongoDatabase dbIns = mongoInstance.getDB();
			MongoCollection<Document> collection = dbIns
					.getCollection(propertiesManager.propertiesMap.get(prefix + Constants.COLLECTION_NAME.getValue()));
			Document newDoc = new Document(updateObj);
			collection.updateOne(oldDoc, newDoc);
		} catch (Exception exception) {
			// MDC.put(FieldType.INTERNAL_CUSTOM_MDC.getName(),
			// fields.getCustomMDC());
			String message = "Error while inserting transaction in database";
			logger.error(message, exception);
			throw new SystemException(ErrorType.DATABASE_ERROR, exception, message);
		}
	}

	public void updateStatus(Fields fields) throws SystemException {
		try {
			MongoDatabase dbIns = null;
			BasicDBObject oldFieldsObj = new BasicDBObject();
			oldFieldsObj.put(FieldType.ORDER_ID.getName(), fields.get(FieldType.ORDER_ID.getName()));
			BasicDBObject newFieldsObj = new BasicDBObject();
			if (fields.contains(FieldType.INTERNAL_CUST_IP.getName())) {
				String ip = fields.get(FieldType.INTERNAL_CUST_IP.getName());
				ip = StringUtils.isBlank(ip) ? getIPFromInitiateRequest(fields.get(FieldType.OID.getName())) : ip;
				fields.put(FieldType.INTERNAL_CUST_IP.getName(), ip);
			}
			for (int i = 0; i < fields.size(); i++) {
				Collection<String> aLLDB_Fields = systemProperties.getDBFields();
				for (String columnName : aLLDB_Fields) {
					newFieldsObj.put(columnName, fields.get(columnName));
				}
			}
			BasicDBObject updateObj = new BasicDBObject();
			updateObj.put("$set", newFieldsObj);
			dbIns = mongoInstance.getDB();
			MongoCollection<Document> collection = dbIns
					.getCollection(propertiesManager.propertiesMap.get(prefix + Constants.COLLECTION_NAME.getValue()));
			Document newDoc = new Document(updateObj);
			Document oldDoc = new Document(oldFieldsObj);
			collection.updateOne(oldDoc, newDoc);
		} catch (Exception exception) {
			// MDC.put(FieldType.INTERNAL_CUSTOM_MDC.getName(),
			// fields.getCustomMDC());
			String message = "Error while inserting transaction in database";
			logger.error(message, exception);
			throw new SystemException(ErrorType.DATABASE_ERROR, exception, message);
		}
	}
	
	
	public boolean findTransactionByPgRefforResponse(String pgRefNum) {
		boolean ret_Val = false;
		MongoCursor<Document> cursor = null;
		List<String> statusLst = new ArrayList<String>();
		statusLst.add(StatusType.FAILED.getName());
		statusLst.add(StatusType.FAILED_AT_ACQUIRER.getName());

		statusLst.add(StatusType.CAPTURED.getName());


		try {
			logger.info("Inside findTransactionForPgRefNoStatusWise (PG_REF_NUM): " + pgRefNum);
			List<BasicDBObject> condList = new ArrayList<BasicDBObject>();
			condList.add(new BasicDBObject(FieldType.PG_REF_NUM.getName(), pgRefNum));
			condList.add(new BasicDBObject(FieldType.TXNTYPE.getName(), TransactionType.SALE.getName()));
			condList.add(new BasicDBObject(FieldType.STATUS.getName(), new BasicDBObject("$in", statusLst)));
			BasicDBObject saleQuery = new BasicDBObject("$and", condList);
			logger.info("Inside findTransactionForPgRefNoStatusWise PG_REF_NUM={}, Query={} ",pgRefNum, saleQuery);
			MongoDatabase dbIns = mongoInstance.getDB();
			MongoCollection<Document> collection = dbIns
					.getCollection(propertiesManager.propertiesMap.get(prefix + Constants.COLLECTION_NAME.getValue()));
			cursor = collection.find(saleQuery).iterator();
			if (cursor.hasNext()) {
				ret_Val = true;
			}
		} catch (Exception exception) {
			String message = "Error while previous based on Pg Ref Num from database";
			logger.error(message, exception);
		} finally {
			cursor.close();
		}
		return ret_Val;
	}
	public String  getDateForPgRefNum(String pgRefNum) throws SystemException {
		String date = null;		try {
					logger.info("Inside getPreviousForPgRefNum (PG_REF_NUM): " + pgRefNum);
					List<BasicDBObject> condList = new ArrayList<BasicDBObject>();
					condList.add(new BasicDBObject(FieldType.PG_REF_NUM.getName(), pgRefNum));
					condList.add(new BasicDBObject(FieldType.TXNTYPE.getName(), TransactionType.SALE.getName()));
					condList.add(new BasicDBObject(FieldType.STATUS.getName(), StatusType.CAPTURED.getName()));
					BasicDBObject saleQuery = new BasicDBObject("$and", condList);
					MongoDatabase dbIns = mongoInstance.getDB();
					MongoCollection<Document> collection = dbIns
							.getCollection(propertiesManager.propertiesMap.get(prefix + Constants.COLLECTION_NAME.getValue()));
					MongoCursor<Document> cursor = collection.find(saleQuery).iterator();
					try {
						if (cursor.hasNext()) {
							Document documentObj = (Document) cursor.next();

							if (null != documentObj) {
								for (int j = 0; j < documentObj.size(); j++) {
									date= documentObj.get("DATE_INDEX").toString();
										}
									}
								
							}

					} finally {
						cursor.close();
					}

					// Correct Amount to decimal format

					


					return date;
				} catch (Exception exception) {
					String message = "Error while previous based on dateNum from database";
					logger.error(message, exception);
					return date;
				}

			}


	public void updateCurrentTransaction(Fields fields) throws SystemException {
		try {
			MongoDatabase dbIns = null;
			BasicDBObject oldFieldsObj = new BasicDBObject();
			oldFieldsObj.put(FieldType.TXN_ID.getName(), fields.get(FieldType.TXN_ID.getName()));
			if (fields.contains(FieldType.INTERNAL_CUST_IP.getName())) {
				String ip = fields.get(FieldType.INTERNAL_CUST_IP.getName());
				ip = StringUtils.isBlank(ip) ? getIPFromInitiateRequest(fields.get(FieldType.OID.getName())) : ip;
				fields.put(FieldType.INTERNAL_CUST_IP.getName(), ip);
			}
			BasicDBObject newFieldsObj = new BasicDBObject();
			for (int i = 0; i < fields.size(); i++) {
				for (String columnName : allDBRequestFields) {
					newFieldsObj.put(columnName, fields.get(columnName));
				}
			}
			BasicDBObject updateObj = new BasicDBObject();
			updateObj.put("$set", newFieldsObj);
			dbIns = mongoInstance.getDB();
			MongoCollection<Document> collection = dbIns
					.getCollection(propertiesManager.propertiesMap.get(prefix + Constants.COLLECTION_NAME.getValue()));
			Document newDoc = new Document(updateObj);
			Document oldDoc = new Document(oldFieldsObj);
			collection.updateOne(oldDoc, newDoc);
		} catch (Exception exception) {
			// MDC.put(FieldType.INTERNAL_CUSTOM_MDC.getName(),
			// fields.getCustomMDC());
			String message = "Error while inserting transaction in database";
			logger.error(message, exception);
			throw new SystemException(ErrorType.DATABASE_ERROR, exception, message);
		}
	}
	public Fields getFieldsForAcquirerUpi(String refId, String txnType) throws SystemException {
		try {
			return createAllForAcquirerUpi(refId, txnType);
		} catch (Exception exception) {
			// MDC.put(FieldType.INTERNAL_CUSTOM_MDC.getName(),
			// fields.getCustomMDC());
			String message = "Error while inserting transaction in database";
			logger.error(message, exception);
			throw new SystemException(ErrorType.DATABASE_ERROR, exception, message);
		}
	}
	
	
	public boolean findTransactionByPgRefFinalStatus(String pgRefNum) {
		boolean ret_Val = true;
		MongoCursor<Document> cursor = null;
		List<String> statusLst = new ArrayList<String>();
		statusLst.add(StatusType.SETTLED_SETTLE.getName());
		statusLst.add(StatusType.FAILED.getName());
		statusLst.add(StatusType.CAPTURED.getName());
		statusLst.add(StatusType.DENIED.getName());


		try {
			logger.info("Inside findTransactionForPgRefNoStatusWise (PG_REF_NUM): " + pgRefNum);
			List<BasicDBObject> condList = new ArrayList<BasicDBObject>();
			condList.add(new BasicDBObject(FieldType.PG_REF_NUM.getName(), pgRefNum));
			condList.add(new BasicDBObject(FieldType.TXNTYPE.getName(), TransactionType.SALE.getName()));
			condList.add(new BasicDBObject(FieldType.STATUS.getName(), new BasicDBObject("$in", statusLst)));
			BasicDBObject saleQuery = new BasicDBObject("$and", condList);
			logger.info("Inside findTransactionForPgRefNoStatusWise PG_REF_NUM={}, Query={} ",pgRefNum, saleQuery);
			MongoDatabase dbIns = mongoInstance.getDB();
			MongoCollection<Document> collection = dbIns
					.getCollection(propertiesManager.propertiesMap.get(prefix + Constants.COLLECTION_NAME.getValue()));
			cursor = collection.find(saleQuery).iterator();
			if (cursor.hasNext()) {
				ret_Val = false;
			}
		} catch (Exception exception) {
			String message = "Error while previous based on Pg Ref Num from database";
			logger.error(message, exception);
		} finally {
			cursor.close();
		}
		return ret_Val;
	}
	private Fields createAllForAcquirerUpi(String refId, String txnType) {

		Fields fields = new Fields();
		// PropertiesManager propManager = new PropertiesManager();
		List<BasicDBObject> dbObjList = new ArrayList<BasicDBObject>();
		dbObjList.add(new BasicDBObject(FieldType.PG_REF_NUM.getName(), refId));
		dbObjList.add(new BasicDBObject(FieldType.TXNTYPE.getName(), txnType));
		BasicDBObject andQuery = new BasicDBObject("$and", dbObjList);
		MongoDatabase dbIns = mongoInstance.getDB();
		MongoCollection<Document> coll = dbIns
				.getCollection(propertiesManager.propertiesMap.get(prefix + Constants.COLLECTION_NAME.getValue()));
		MongoCursor<Document> cursor = coll.find(andQuery).iterator();
		if (cursor.hasNext()) {
			Document documentObj = cursor.next();

			if (null != documentObj) {
				for (int j = 0; j < documentObj.size(); j++) {
					for (String columnName : aLLDB_Fields) {
						if (documentObj.get(columnName) != null) {
							fields.put(columnName, documentObj.get(columnName).toString());
						} else {

						}

					}
				}
			}
			fields.logAllFields("Previous fields");
		}
		cursor.close();
		return fields;

	}
	public void updateForAuthorization(Fields fields) throws SystemException {
		try {
			MongoDatabase dbIns = null;
			BasicDBObject oldFieldsObj = new BasicDBObject();
			oldFieldsObj.put(FieldType.TXN_ID.getName(), fields.get(FieldType.TXN_ID.getName()));
			if (fields.contains(FieldType.INTERNAL_CUST_IP.getName())) {
				String ip = fields.get(FieldType.INTERNAL_CUST_IP.getName());
				ip = StringUtils.isBlank(ip) ? getIPFromInitiateRequest(fields.get(FieldType.OID.getName())) : ip;
				fields.put(FieldType.INTERNAL_CUST_IP.getName(), ip);
			}
			BasicDBObject newFieldsObj = new BasicDBObject();
			for (int i = 0; i < fields.size(); i++) {
				Collection<String> aLLDB_Fields = SystemProperties.getDBFields();
				for (String columnName : aLLDB_Fields) {
					newFieldsObj.put(columnName, fields.get(columnName));
				}
			}
			BasicDBObject updateObj = new BasicDBObject();
			updateObj.put("$set", newFieldsObj);
			dbIns = mongoInstance.getDB();
			MongoCollection<Document> collection = dbIns
					.getCollection(propertiesManager.propertiesMap.get(prefix + Constants.COLLECTION_NAME.getValue()));
			Document newDoc = new Document(updateObj);
			Document oldDoc = new Document(oldFieldsObj);
			collection.updateOne(oldDoc, newDoc);
		} catch (Exception exception) {
			// MDC.put(FieldType.INTERNAL_CUSTOM_MDC.getName(),
			// fields.getCustomMDC());
			String message = "Error while inserting transaction in database";
			logger.error(message, exception);
			throw new SystemException(ErrorType.DATABASE_ERROR, exception, message);
		}
	}


	public Fields getPrivousFieldsByOderId(String orderId) {

		Fields fields = new Fields();
		// PropertiesManager propManager = new PropertiesManager();
		List<BasicDBObject> dbObjList = new ArrayList<BasicDBObject>();
		dbObjList.add(new BasicDBObject(FieldType.ORDER_ID.getName(), orderId));
		dbObjList.add(new BasicDBObject(FieldType.TXNTYPE.getName(), "SALE"));
		dbObjList.add(new BasicDBObject(FieldType.STATUS.getName(), "Sent to Bank"));

		BasicDBObject andQuery = new BasicDBObject("$and", dbObjList);
		logger.info("query for privous fields"+andQuery);
		MongoDatabase dbIns = mongoInstance.getDB();
		MongoCollection<Document> coll = dbIns
				.getCollection(propertiesManager.propertiesMap.get(prefix + Constants.COLLECTION_NAME.getValue()));
		MongoCursor<Document> cursor = coll.find(andQuery).iterator();
		if (cursor.hasNext()) {
			Document documentObj = cursor.next();

			if (null != documentObj) {
				for (int j = 0; j < documentObj.size(); j++) {
					for (String columnName : aLLDB_Fields) {
						if (documentObj.get(columnName) != null) {
							if(columnName.equalsIgnoreCase(FieldType.AMOUNT.getName())||columnName.equalsIgnoreCase(FieldType.TOTAL_AMOUNT.getName())) {
								fields.put(columnName, Amount.formatAmount(documentObj.get(columnName).toString(),"356"));

							}else {
								fields.put(columnName, documentObj.get(columnName).toString());
							}
						} else {

						}

					}
				}
			}
			fields.logAllFields("Previous fields");
		}
		cursor.close();
		return fields;

	}

	private Fields createAllSelect(String txnId, String payId) {

		Fields fields = new Fields();
		// PropertiesManager propManager = new PropertiesManager();
		List<BasicDBObject> dbObjList = new ArrayList<BasicDBObject>();
		dbObjList.add(new BasicDBObject(FieldType.TXN_ID.getName(), txnId));
		dbObjList.add(new BasicDBObject(FieldType.PAY_ID.getName(), payId));
		BasicDBObject andQuery = new BasicDBObject("$and", dbObjList);
		MongoDatabase dbIns = mongoInstance.getDB();
		MongoCollection<Document> coll = dbIns
				.getCollection(propertiesManager.propertiesMap.get(prefix + Constants.COLLECTION_NAME.getValue()));
		MongoCursor<Document> cursor = coll.find(andQuery).iterator();
		if (cursor.hasNext()) {
			Document documentObj = cursor.next();

			if (null != documentObj) {
				for (int j = 0; j < documentObj.size(); j++) {
					for (String columnName : aLLDB_Fields) {
						if (documentObj.get(columnName) != null) {
							fields.put(columnName, documentObj.get(columnName).toString());
						} else {

						}

					}
				}
			}
			fields.logAllFields("Previous fields");
		}
		cursor.close();
		return fields;

	}

	private Fields createAllForRefund(String txnId, String payId, String txnType, String status) {

		Fields fields = new Fields();
		// PropertiesManager propManager = new PropertiesManager();
		List<BasicDBObject> dbObjList = new ArrayList<BasicDBObject>();
		dbObjList.add(new BasicDBObject(FieldType.PG_REF_NUM.getName(), txnId));
		dbObjList.add(new BasicDBObject(FieldType.PAY_ID.getName(), payId));
		dbObjList.add(new BasicDBObject(FieldType.TXNTYPE.getName(), txnType));
		dbObjList.add(new BasicDBObject(FieldType.STATUS.getName(), status));
		BasicDBObject andQuery = new BasicDBObject("$and", dbObjList);
		MongoDatabase dbIns = mongoInstance.getDB();
		MongoCollection<Document> coll = dbIns
				.getCollection(propertiesManager.propertiesMap.get(prefix + Constants.COLLECTION_NAME.getValue()));
		MongoCursor<Document> cursor = coll.find(andQuery).iterator();
		if (cursor.hasNext()) {
			Document documentObj = cursor.next();

			if (null != documentObj) {
				for (int j = 0; j < documentObj.size(); j++) {
					for (String columnName : aLLDB_Fields) {
						if (documentObj.get(columnName) != null) {
							fields.put(columnName, documentObj.get(columnName).toString());
						} else {

						}

					}
				}
			}
			fields.logAllFields("Previous fields");
		}
		cursor.close();
		return fields;

	}
	
	
	private Fields createAllForRefundPayout(String txnId, String payId, String txnType, String status) {

		Fields fields = new Fields();
		// PropertiesManager propManager = new PropertiesManager();
		List<BasicDBObject> dbObjList = new ArrayList<BasicDBObject>();
		dbObjList.add(new BasicDBObject(FieldType.PG_REF_NUM.getName(), txnId));
		dbObjList.add(new BasicDBObject(FieldType.PAY_ID.getName(), payId));
		dbObjList.add(new BasicDBObject(FieldType.TXNTYPE.getName(), txnType));
		dbObjList.add(new BasicDBObject(FieldType.STATUS.getName(), status));
		BasicDBObject andQuery = new BasicDBObject("$and", dbObjList);
		MongoDatabase dbIns = mongoInstance.getDB();
		MongoCollection<Document> coll = dbIns
				.getCollection(propertiesManager.propertiesMap.get(prefix + "PO_Transaction"));
		MongoCursor<Document> cursor = coll.find(andQuery).iterator();
		if (cursor.hasNext()) {
			Document documentObj = cursor.next();

			if (null != documentObj) {
				for (int j = 0; j < documentObj.size(); j++) {
					for (String columnName : aLLDB_Fields) {
						if (documentObj.get(columnName) != null) {
							fields.put(columnName, documentObj.get(columnName).toString());
						} else {

						}

					}
				}
			}
			fields.logAllFields("Previous fields");
		}
		cursor.close();
		return fields;

	}

	// YesUpi Enquiry
	public boolean createAllForYesUpiEnquiry(String pgRefNum) {

		Fields fields = new Fields();
		boolean flag = false;
		// PropertiesManager propManager = new PropertiesManager();
		List<BasicDBObject> dbObjList = new ArrayList<BasicDBObject>();
		dbObjList.add(new BasicDBObject(FieldType.PG_REF_NUM.getName(), pgRefNum));
		dbObjList.add(new BasicDBObject(FieldType.TXNTYPE.getName(), TransactionType.SALE.getName()));
		dbObjList.add(new BasicDBObject(FieldType.STATUS.getName(), StatusType.CANCELLED.getName()));
		BasicDBObject andQuery = new BasicDBObject("$and", dbObjList);
		MongoDatabase dbIns = mongoInstance.getDB();
		MongoCollection<Document> coll = dbIns
				.getCollection(propertiesManager.propertiesMap.get(prefix + Constants.COLLECTION_NAME.getValue()));
		MongoCursor<Document> cursor = coll.find(andQuery).iterator();
		if (cursor.hasNext()) {
			Document documentObj = cursor.next();

			if (null != documentObj) {
				for (int j = 0; j < documentObj.size(); j++) {
					for (String columnName : aLLDB_Fields) {
						if (documentObj.get(columnName) != null) {
							fields.put(columnName, documentObj.get(columnName).toString());
						} else {

						}

					}
				}
			}
			fields.logAllFields("Previous fields");
		}
		cursor.close();
		if (fields.size() > 0)
			flag = true;
		return flag;

	}

	private Fields createAllForFedUpi(String refId, String txnType) {

		Fields fields = new Fields();
		// PropertiesManager propManager = new PropertiesManager();
		List<BasicDBObject> dbObjList = new ArrayList<BasicDBObject>();
		dbObjList.add(new BasicDBObject(FieldType.UDF5.getName(), refId));
		dbObjList.add(new BasicDBObject(FieldType.TXNTYPE.getName(), txnType));
		BasicDBObject andQuery = new BasicDBObject("$and", dbObjList);
		MongoDatabase dbIns = mongoInstance.getDB();
		MongoCollection<Document> coll = dbIns
				.getCollection(propertiesManager.propertiesMap.get(prefix + Constants.COLLECTION_NAME.getValue()));
		MongoCursor<Document> cursor = coll.find(andQuery).iterator();
		if (cursor.hasNext()) {
			Document documentObj = cursor.next();

			if (null != documentObj) {
				for (int j = 0; j < documentObj.size(); j++) {
					for (String columnName : aLLDB_Fields) {
						if (documentObj.get(columnName) != null) {
							fields.put(columnName, documentObj.get(columnName).toString());
						} else {

						}

					}
				}
			}
			fields.logAllFields("Previous fields");
		}
		cursor.close();
		return fields;

	}
	public Fields getFieldsByorderIdPayout(String orderId) {

		Fields fields = new Fields();
		// PropertiesManager propManager = new PropertiesManager();
		List<BasicDBObject> dbObjList = new ArrayList<BasicDBObject>();
		dbObjList.add(new BasicDBObject(FieldType.ORDER_ID.getName(), orderId));
		BasicDBObject andQuery = new BasicDBObject("$and", dbObjList);
		MongoDatabase dbIns = mongoInstance.getDB();
		MongoCollection<Document> coll = dbIns
				.getCollection(propertiesManager.propertiesMap.get("MONGO_DB_PO_TransactionStatus"));
		MongoCursor<Document> cursor = coll.find(andQuery).iterator();
		if (cursor.hasNext()) {
			Document documentObj = cursor.next();

			if (null != documentObj) {
				for (int j = 0; j < documentObj.size(); j++) {
					for (String columnName : documentObj.keySet()	) {
						if (documentObj.get(columnName) != null ) {
							
							
							fields.put(columnName, documentObj.get(columnName).toString());
						} else {

						}

					}
				}
			}
			fields.logAllFields("Previous fields");
			if(fields.contains("_id")) {
				fields.remove("_id");
				
			}
			if(fields.contains(FieldType.AMOUNT.getName())) {
				fields.put(FieldType.AMOUNT.getName(), Amount.formatAmount(fields.get(FieldType.AMOUNT.getName()), fields.get(FieldType.CURRENCY_CODE.getName())));

				
			}
			fields.put(FieldType.CARD_HOLDER_TYPE.getName(), CardHolderType.CONSUMER.toString());
			fields.put(FieldType.PAYMENTS_REGION.getName(), AccountCurrencyRegion.DOMESTIC.toString());

		}
		cursor.close();
		return fields;

	}
	private Fields createAllForHdfcUpi(String refId, String txnType) {

		Fields fields = new Fields();
		// PropertiesManager propManager = new PropertiesManager();
		List<BasicDBObject> dbObjList = new ArrayList<BasicDBObject>();
		dbObjList.add(new BasicDBObject(FieldType.PG_REF_NUM.getName(), refId));
		dbObjList.add(new BasicDBObject(FieldType.TXNTYPE.getName(), txnType));
		BasicDBObject andQuery = new BasicDBObject("$and", dbObjList);
		MongoDatabase dbIns = mongoInstance.getDB();
		MongoCollection<Document> coll = dbIns
				.getCollection(propertiesManager.propertiesMap.get(prefix + Constants.COLLECTION_NAME.getValue()));
		MongoCursor<Document> cursor = coll.find(andQuery).iterator();
		if (cursor.hasNext()) {
			Document documentObj = cursor.next();

			if (null != documentObj) {
				for (int j = 0; j < documentObj.size(); j++) {
					for (String columnName : aLLDB_Fields) {
						if (documentObj.get(columnName) != null) {
							fields.put(columnName, documentObj.get(columnName).toString());
						} else {

						}

					}
				}
			}
			fields.logAllFields("Previous fields");
		}
		cursor.close();
		return fields;

	}

	
	public void createAllForPayuRefund(String refId,Fields fields) {

		// PropertiesManager propManager = new PropertiesManager();
		List<BasicDBObject> dbObjList = new ArrayList<BasicDBObject>();
		dbObjList.add(new BasicDBObject(FieldType.REFUND_ORDER_ID.getName(), refId));
		dbObjList.add(new BasicDBObject(FieldType.TXNTYPE.getName(), "REFUND"));
		dbObjList.add(new BasicDBObject(FieldType.STATUS.getName(),"Pending"));

		BasicDBObject andQuery = new BasicDBObject("$and", dbObjList);
		MongoDatabase dbIns = mongoInstance.getDB();
		MongoCollection<Document> coll = dbIns
				.getCollection(propertiesManager.propertiesMap.get(prefix + Constants.COLLECTION_NAME.getValue()));
		MongoCursor<Document> cursor = coll.find(andQuery).iterator();
		if (cursor.hasNext()) {
			Document documentObj = cursor.next();

			if (null != documentObj) {
				for (int j = 0; j < documentObj.size(); j++) {
					for (String columnName : aLLDB_Fields) {
						if (documentObj.get(columnName) != null) {
							fields.put(columnName, documentObj.get(columnName).toString());
						} else {

						}

					}
				}
			}
			fields.logAllFields("Previous fields");
		}
		cursor.close();

	}
	
	
	
	public Fields createAllSelectForStatusPayout(String orderId, String payId, String amount) {

		Fields fields = new Fields();
		// PropertiesManager propManager = new PropertiesManager();
		List<BasicDBObject> dbObjList = new ArrayList<BasicDBObject>();
		List<BasicDBObject> txnTypeConditionLst = new ArrayList<BasicDBObject>();

		BasicDBObject query = new BasicDBObject();
		query.append(FieldType.RESPONSE_CODE.getName(),
				new BasicDBObject("$ne", ErrorType.DUPLICATE_ORDER_ID.getCode()));

		dbObjList.add(new BasicDBObject(FieldType.ORDER_ID.getName(), orderId));
		dbObjList.add(new BasicDBObject(FieldType.PAY_ID.getName(), payId));
		dbObjList.add(new BasicDBObject(FieldType.AMOUNT.getName(), amount));

		BasicDBObject txnTypeQuery = new BasicDBObject();
		txnTypeConditionLst.add(new BasicDBObject(FieldType.TXNTYPE.getName(), TransactionType.SALE.getName()));
//		txnTypeConditionLst.add(new BasicDBObject(FieldType.TXNTYPE.getName(), TransactionType.ENROLL.getName()));
//		txnTypeConditionLst.add(new BasicDBObject(FieldType.TXNTYPE.getName(), TransactionType.NEWORDER.getName()));
//		txnTypeConditionLst.add(new BasicDBObject(FieldType.TXNTYPE.getName(), TransactionType.RECO.getName()));

		txnTypeQuery.append("$or", txnTypeConditionLst);

		dbObjList.add(txnTypeQuery);
		dbObjList.add(query);
		BasicDBObject andQuery = new BasicDBObject("$and", dbObjList);
		logger.info("Query In createAllSelectForStatus : "+andQuery);
		MongoDatabase dbIns = mongoInstance.getDB();
		MongoCollection<Document> coll = dbIns
				.getCollection(propertiesManager.propertiesMap.get(prefix + Constants.TRANSACTION_LEDGER_PO.getValue()));

		FindIterable<Document> iterator = coll.find(andQuery).sort(new BasicDBObject(FieldType.CREATE_DATE.getName(), -1)).limit(1);

		MongoCursor<Document> saleCursor = iterator.iterator();

		if (saleCursor.hasNext()) {
			Document documentObj = saleCursor.next();

			if (null != documentObj) {
				for (int j = 0; j < documentObj.size(); j++) {
					for (String columnName : aLLDB_Fields) {
						if (documentObj.get(columnName) != null) {
							fields.put(columnName, documentObj.get(columnName).toString());
						} else {

						}

					}
				}
			}

			fields.logAllFields("Previous fields");
			saleCursor.close();
		} 

		if ((fields.get(FieldType.TXNTYPE.getName()).equalsIgnoreCase(TransactionType.SALE.getName())
						&& fields.get(FieldType.STATUS.getName()).equalsIgnoreCase(StatusTypePayout.REQUEST_ACCEPTED.getName()))
				
				) {

			fields.put(FieldType.IS_STATUS_FINAL.getName(), Constants.N_FLAG.getValue());

		} else {
			fields.put(FieldType.IS_STATUS_FINAL.getName(), Constants.Y_FLAG.getValue());
		}

		return fields;

	}

	//TODO need changes in this method change insertion date to create date
	public Fields createAllSelectForStatus(String orderId, String payId, String amount) {

		Fields fields = new Fields();
		// PropertiesManager propManager = new PropertiesManager();
		List<BasicDBObject> dbObjList = new ArrayList<BasicDBObject>();
		List<BasicDBObject> txnTypeConditionLst = new ArrayList<BasicDBObject>();

		BasicDBObject query = new BasicDBObject();
		query.append(FieldType.RESPONSE_CODE.getName(),
				new BasicDBObject("$ne", ErrorType.DUPLICATE_ORDER_ID.getCode()));

		dbObjList.add(new BasicDBObject(FieldType.ORDER_ID.getName(), orderId));
		dbObjList.add(new BasicDBObject(FieldType.PAY_ID.getName(), payId));
		//dbObjList.add(new BasicDBObject(FieldType.AMOUNT.getName(), amount));

		BasicDBObject txnTypeQuery = new BasicDBObject();
		txnTypeConditionLst.add(new BasicDBObject(FieldType.TXNTYPE.getName(), TransactionType.SALE.getName()));
		txnTypeConditionLst.add(new BasicDBObject(FieldType.TXNTYPE.getName(), TransactionType.ENROLL.getName()));
		txnTypeConditionLst.add(new BasicDBObject(FieldType.TXNTYPE.getName(), TransactionType.NEWORDER.getName()));
		txnTypeConditionLst.add(new BasicDBObject(FieldType.TXNTYPE.getName(), TransactionType.RECO.getName()));

		txnTypeQuery.append("$or", txnTypeConditionLst);

		dbObjList.add(txnTypeQuery);
		dbObjList.add(query);
		BasicDBObject andQuery = new BasicDBObject("$and", dbObjList);
		logger.info("Query In createAllSelectForStatus : "+andQuery);
		MongoDatabase dbIns = mongoInstance.getDB();
		MongoCollection<Document> coll = dbIns
				.getCollection(propertiesManager.propertiesMap.get(prefix + Constants.COLLECTION_NAME.getValue()));

		FindIterable<Document> iterator = coll.find(andQuery).sort(new BasicDBObject(FieldType.CREATE_DATE.getName(), -1)).limit(1);

		MongoCursor<Document> saleCursor = iterator.iterator();

		if (saleCursor.hasNext()) {
			Document documentObj = saleCursor.next();

			if (null != documentObj) {
				for (int j = 0; j < documentObj.size(); j++) {
					for (String columnName : aLLDB_Fields) {
						if (documentObj.get(columnName) != null) {
							fields.put(columnName, documentObj.get(columnName).toString());
						} else {

						}

					}
				}
			}

			fields.logAllFields("Previous fields");
			saleCursor.close();
		} else {
			saleCursor.close();
			txnTypeConditionLst.clear();
			dbObjList.clear();
			query.clear();
			txnTypeQuery.clear();
			andQuery.clear();

			query.append(FieldType.RESPONSE_CODE.getName(),
					new BasicDBObject("$ne", ErrorType.DUPLICATE_ORDER_ID.getCode()));

			dbObjList.add(new BasicDBObject(FieldType.REFUND_ORDER_ID.getName(), orderId));
			dbObjList.add(new BasicDBObject(FieldType.PAY_ID.getName(), payId));
			dbObjList.add(new BasicDBObject(FieldType.AMOUNT.getName(), amount));

			txnTypeConditionLst.add(new BasicDBObject(FieldType.TXNTYPE.getName(), TransactionType.REFUND.getName()));

			txnTypeQuery.append("$or", txnTypeConditionLst);

			dbObjList.add(txnTypeQuery);
			dbObjList.add(query);
			andQuery = new BasicDBObject("$and", dbObjList);

			FindIterable<Document> refundIterator = coll.find(andQuery).sort(new BasicDBObject("INSERTION_DATE", -1))
					.limit(1);

			MongoCursor<Document> refundCursor = refundIterator.iterator();

			if (refundCursor.hasNext()) {
				Document documentObj = refundCursor.next();

				if (null != documentObj) {
					for (int j = 0; j < documentObj.size(); j++) {
						for (String columnName : aLLDB_Fields) {
							if (documentObj.get(columnName) != null) {
								fields.put(columnName, documentObj.get(columnName).toString());
							} else {

							}

						}
					}
				}
				refundCursor.close();
			}

		}

		if ((fields.get(FieldType.TXNTYPE.getName()).equalsIgnoreCase(TransactionType.NEWORDER.getName())
				&& fields.get(FieldType.STATUS.getName()).equalsIgnoreCase(StatusType.PENDING.getName()))
				|| (fields.get(FieldType.TXNTYPE.getName()).equalsIgnoreCase(TransactionType.SALE.getName())
						&& fields.get(FieldType.STATUS.getName()).equalsIgnoreCase(StatusType.SENT_TO_BANK.getName()))
				|| (fields.get(FieldType.TXNTYPE.getName()).equalsIgnoreCase(TransactionType.ENROLL.getName())
						&& fields.get(FieldType.STATUS.getName()).equalsIgnoreCase(StatusType.ENROLLED.getName()))
				|| (fields.get(FieldType.TXNTYPE.getName()).equalsIgnoreCase(TransactionType.REFUND.getName())
						&& fields.get(FieldType.STATUS.getName()).equalsIgnoreCase(StatusType.PENDING.getName()))) {

			fields.put(FieldType.IS_STATUS_FINAL.getName(), Constants.N_FLAG.getValue());

		} else {
			fields.put(FieldType.IS_STATUS_FINAL.getName(), Constants.Y_FLAG.getValue());
		}

		return fields;

	}

	public Fields createAllSelectForVerify(String orderId, String payId, String amount, String pgRefNum,
			String status) {

		Fields fields = new Fields();
		// PropertiesManager propManager = new PropertiesManager();
		List<BasicDBObject> dbObjList = new ArrayList<BasicDBObject>();
		// List<BasicDBObject> txnTypeConditionLst = new ArrayList<BasicDBObject>();
		dbObjList.add(new BasicDBObject(FieldType.ORDER_ID.getName(), orderId));
		dbObjList.add(new BasicDBObject(FieldType.PAY_ID.getName(), payId));
		dbObjList.add(new BasicDBObject(FieldType.AMOUNT.getName(), amount));
		dbObjList.add(new BasicDBObject(FieldType.PG_REF_NUM.getName(), pgRefNum));
		dbObjList.add(new BasicDBObject(FieldType.STATUS.getName(), status));
		dbObjList.add(new BasicDBObject(FieldType.TXNTYPE.getName(), TransactionType.SALE.getName()));
		/*
		 * BasicDBObject txnTypeQuery = new BasicDBObject();
		 * 
		 * txnTypeConditionLst.add(new BasicDBObject(FieldType.TXNTYPE.getName(),
		 * TransactionType.SALE.getName())); txnTypeConditionLst.add(new
		 * BasicDBObject(FieldType.TXNTYPE.getName(),
		 * TransactionType.ENROLL.getName())); txnTypeConditionLst.add(new
		 * BasicDBObject(FieldType.TXNTYPE.getName(),
		 * TransactionType.NEWORDER.getName())); txnTypeConditionLst.add(new
		 * BasicDBObject(FieldType.TXNTYPE.getName(), TransactionType.RECO.getName()));
		 * 
		 * txnTypeQuery.append("$or", txnTypeConditionLst);
		 * 
		 * dbObjList.add(txnTypeQuery);
		 */
		BasicDBObject andQuery = new BasicDBObject("$and", dbObjList);
		MongoDatabase dbIns = mongoInstance.getDB();
		MongoCollection<Document> coll = dbIns
				.getCollection(propertiesManager.propertiesMap.get(prefix + Constants.COLLECTION_NAME.getValue()));

		FindIterable<Document> cursor = coll.find(andQuery);

		// MongoCursor<Document> cursor2 = cursor.iterator();

		if (cursor.iterator().hasNext()) {
			Document documentObj = cursor.iterator().next();

			if (null != documentObj) {
				for (int j = 0; j < documentObj.size(); j++) {
					for (String columnName : aLLDB_Fields) {
						if (documentObj.get(columnName) != null) {
							fields.put(columnName, documentObj.get(columnName).toString());
						} else {

						}

					}
				}
			}
			fields.logAllFields("Previous fields");
		}
		cursor.iterator().close();

		return fields;

	}

	public Fields createAllSelectForSettlement(String custId, String oId, String status) {

		Fields fields = new Fields();
		// PropertiesManager propManager = new PropertiesManager();
		List<BasicDBObject> dbObjList = new ArrayList<BasicDBObject>();
		// List<BasicDBObject> txnTypeConditionLst = new ArrayList<BasicDBObject>();
		dbObjList.add(new BasicDBObject(FieldType.CUSTOMER_ID.getName(), custId));
		dbObjList.add(new BasicDBObject(FieldType.TXN_ID.getName(), oId));
		dbObjList.add(new BasicDBObject(FieldType.STATUS.getName(), status));

		BasicDBObject andQuery = new BasicDBObject("$and", dbObjList);
		MongoDatabase dbIns = mongoInstance.getDB();
		MongoCollection<Document> coll = dbIns.getCollection(
				propertiesManager.propertiesMap.get(prefix + Constants.SETTLEMENT_COLLECTION_NAME.getValue()));

		FindIterable<Document> cursor = coll.find(andQuery);

		// MongoCursor<Document> cursor2 = cursor.iterator();

		if (cursor.iterator().hasNext()) {
			Document documentObj = cursor.iterator().next();

			if (null != documentObj) {
				for (int j = 0; j < documentObj.size(); j++) {
					for (String columnName : aLLDB_Fields) {
						if (documentObj.get(columnName) != null) {
							fields.put(columnName, documentObj.get(columnName).toString());
						} else {

						}

					}
				}
			}
			fields.logAllFields("Previous fields");
		}
		cursor.iterator().close();

		return fields;

	}

	public Long createAllSelectForSaleOrderId(String orderId, String payId, String currencyCode) {

		List<BasicDBObject> dbObjList = new ArrayList<BasicDBObject>();
		dbObjList.add(new BasicDBObject(FieldType.ORDER_ID.getName(), orderId));
		dbObjList.add(new BasicDBObject(FieldType.PAY_ID.getName(), payId));
		dbObjList.add(new BasicDBObject(FieldType.CURRENCY_CODE.getName(), currencyCode));
		BasicDBObject andQuery = new BasicDBObject("$and", dbObjList);
		logger.info("check dupicate sale order id individually createAllSelectForSaleOrderId :" + andQuery);
		MongoDatabase dbIns = mongoInstance.getDB();
		MongoCollection<Document> coll = dbIns
				.getCollection(PropertiesManager.propertiesMap.get(prefix + Constants.COLLECTION_NAME.getValue()));

		long recordsCount = coll.count(andQuery);

		return recordsCount;

	}
	public Long createAllSelectForSaleOrderIdPayout(String orderId, String payId, String currencyCode) {

		List<BasicDBObject> dbObjList = new ArrayList<BasicDBObject>();
		dbObjList.add(new BasicDBObject(FieldType.ORDER_ID.getName(), orderId));
		dbObjList.add(new BasicDBObject(FieldType.PAY_ID.getName(), payId));
		dbObjList.add(new BasicDBObject(FieldType.CURRENCY_CODE.getName(), currencyCode));
		BasicDBObject andQuery = new BasicDBObject("$and", dbObjList);
		logger.info("check dupicate sale order id individually createAllSelectForSaleOrderId :" + andQuery);
		MongoDatabase dbIns = mongoInstance.getDB();
		MongoCollection<Document> coll = dbIns
				.getCollection(PropertiesManager.propertiesMap.get(prefix + Constants.TRANSACTION_LEDGER_PO.getValue()));

		long recordsCount = coll.count(andQuery);

		return recordsCount;

	}

	public Long createAllSelectForDuplicateSubmit(String orderId) {

		List<BasicDBObject> dbObjList = new ArrayList<BasicDBObject>();
		List<BasicDBObject> dbSaleSentTobankList = new ArrayList<BasicDBObject>();
		List<BasicDBObject> dbSaleEnrolledList = new ArrayList<BasicDBObject>();
		List<BasicDBObject> dbSalePendingList = new ArrayList<BasicDBObject>();
		List<BasicDBObject> dbEnrollPendingList = new ArrayList<BasicDBObject>();

		dbObjList.add(new BasicDBObject(FieldType.ORDER_ID.getName(), orderId));

		dbSaleSentTobankList.add(new BasicDBObject(FieldType.TXNTYPE.getName(), TransactionType.SALE.getName()));
		dbSaleSentTobankList.add(new BasicDBObject(FieldType.STATUS.getName(), StatusType.SENT_TO_BANK.getName()));
		BasicDBObject dbSaleSentTobankQuery = new BasicDBObject("$and", dbSaleSentTobankList);

		dbSalePendingList.add(new BasicDBObject(FieldType.TXNTYPE.getName(), TransactionType.SALE.getName()));
		dbSalePendingList.add(new BasicDBObject(FieldType.STATUS.getName(), StatusType.PENDING.getName()));
		BasicDBObject dbSalePendingQuery = new BasicDBObject("$and", dbSalePendingList);

		dbSaleEnrolledList.add(new BasicDBObject(FieldType.TXNTYPE.getName(), TransactionType.ENROLL.getName()));
		dbSaleEnrolledList.add(new BasicDBObject(FieldType.STATUS.getName(), StatusType.ENROLLED.getName()));
		BasicDBObject dbSaleEnrolledTobankQuery = new BasicDBObject("$and", dbSaleEnrolledList);

		dbEnrollPendingList.add(new BasicDBObject(FieldType.TXNTYPE.getName(), TransactionType.ENROLL.getName()));
		dbEnrollPendingList.add(new BasicDBObject(FieldType.STATUS.getName(), StatusType.PENDING.getName()));
		BasicDBObject dbEnrollPendingQuery = new BasicDBObject("$and", dbEnrollPendingList);

		List<BasicDBObject> dbTxnConditionList = new ArrayList<BasicDBObject>();
		dbTxnConditionList.add(dbSaleSentTobankQuery);
		dbTxnConditionList.add(dbSaleEnrolledTobankQuery);
		dbTxnConditionList.add(dbSalePendingQuery);
		dbTxnConditionList.add(dbEnrollPendingQuery);

		BasicDBObject andQuery = new BasicDBObject();
		andQuery.append("$and", dbObjList);
		andQuery.append("$or", dbTxnConditionList);

		logger.info("check dupicate submit for a same order Id :" + andQuery);
		MongoDatabase dbIns = mongoInstance.getDB();
		MongoCollection<Document> coll = dbIns
				.getCollection(propertiesManager.propertiesMap.get(prefix + Constants.COLLECTION_NAME.getValue()));

		long recordsCount = coll.count(andQuery);

		return recordsCount;

	}

	public Long createAllSelectForRefundOrderIdSale(String orderId, String payId, String currencyCode) {

		List<BasicDBObject> dbObjList = new ArrayList<BasicDBObject>();
		dbObjList.add(new BasicDBObject(FieldType.REFUND_ORDER_ID.getName(), orderId));
		dbObjList.add(new BasicDBObject(FieldType.PAY_ID.getName(), payId));
		dbObjList.add(new BasicDBObject(FieldType.CURRENCY_CODE.getName(), currencyCode));
		BasicDBObject andQuery = new BasicDBObject("$and", dbObjList);
		MongoDatabase dbIns = mongoInstance.getDB();
		MongoCollection<Document> coll = dbIns
				.getCollection(propertiesManager.propertiesMap.get(prefix + Constants.COLLECTION_NAME.getValue()));

		long recordsCount = coll.count(andQuery);

		return recordsCount;

	}

	public Long createAllSelectForSaleOrderIdRefund(String refundOrderId, String payId, String currencyCode) {

		List<BasicDBObject> dbObjList = new ArrayList<BasicDBObject>();
		dbObjList.add(new BasicDBObject(FieldType.ORDER_ID.getName(), refundOrderId));
		dbObjList.add(new BasicDBObject(FieldType.PAY_ID.getName(), payId));
		dbObjList.add(new BasicDBObject(FieldType.CURRENCY_CODE.getName(), currencyCode));
		BasicDBObject andQuery = new BasicDBObject("$and", dbObjList);
		MongoDatabase dbIns = mongoInstance.getDB();
		MongoCollection<Document> coll = dbIns
				.getCollection(propertiesManager.propertiesMap.get(prefix + Constants.COLLECTION_NAME.getValue()));

		long recordsCount = coll.count(andQuery);

		return recordsCount;

	}

	public Long createAllSelectForOrderId(Fields fields, String orderId, String payId, String currencyCode) {

		String refundorderId = fields.get(FieldType.REFUND_ORDER_ID.getName());
		orderId = fields.get(FieldType.ORDER_ID.getName());
		payId = fields.get(FieldType.PAY_ID.getName());
		currencyCode = fields.get(FieldType.CURRENCY_CODE.getName());

		List<BasicDBObject> duplicateConditionList = new ArrayList<BasicDBObject>();
		long recordsCount = 0;

		if (fields.get(FieldType.TXNTYPE.getName()).equalsIgnoreCase(TransactionType.REFUND.getName())) {

			// duplicateConditionList.add(new BasicDBObject(FieldType.ORDER_ID.getName(),
			// refundorderId));
			duplicateConditionList.add(new BasicDBObject(FieldType.REFUND_ORDER_ID.getName(), refundorderId));
			duplicateConditionList.add(new BasicDBObject(FieldType.STATUS.getName(), StatusType.CAPTURED.getName()));
		} else {
			duplicateConditionList.add(new BasicDBObject(FieldType.ORDER_ID.getName(), orderId));
			duplicateConditionList.add(new BasicDBObject(FieldType.REFUND_ORDER_ID.getName(), orderId));

		}

		BasicDBObject duplicateConditionsQuery = new BasicDBObject("$and", duplicateConditionList);

		List<BasicDBObject> finalList1 = new ArrayList<BasicDBObject>();
		finalList1.add(duplicateConditionsQuery);
		finalList1.add(new BasicDBObject(FieldType.PAY_ID.getName(), payId));
		finalList1.add(new BasicDBObject(FieldType.CURRENCY_CODE.getName(), currencyCode));

		BasicDBObject finalQuery = new BasicDBObject("$and", finalList1);
		logger.info("Check dupicate order id for Never createAllSelectForOrderId: " + finalQuery);
		MongoDatabase dbIns = mongoInstance.getDB();
		MongoCollection<Document> coll = dbIns
				.getCollection(PropertiesManager.propertiesMap.get(prefix + Constants.COLLECTION_NAME.getValue()));
		recordsCount = coll.count(finalQuery);

		return recordsCount;

	}

	public Long createAllSelectForRefundOrderId(String refundOrderId, String payId, String currencyCode,
			String status) {

		List<BasicDBObject> dbObjList = new ArrayList<BasicDBObject>();
		dbObjList.add(new BasicDBObject(FieldType.REFUND_ORDER_ID.getName(), refundOrderId));
		dbObjList.add(new BasicDBObject(FieldType.PAY_ID.getName(), payId));
		dbObjList.add(new BasicDBObject(FieldType.CURRENCY_CODE.getName(), currencyCode));

		List<BasicDBObject> statusList = new ArrayList<BasicDBObject>();
		statusList.add(new BasicDBObject(FieldType.STATUS.getName(), status));
		statusList.add(new BasicDBObject(FieldType.STATUS.getName(), StatusType.PENDING.getName()));

		dbObjList.add(new BasicDBObject("$or", statusList));
		BasicDBObject andQuery = new BasicDBObject("$and", dbObjList);
		logger.info("check dupicate refund order id individually createAllSelectForRefundOrderId :  " + andQuery);
		MongoDatabase dbIns = mongoInstance.getDB();
		MongoCollection<Document> coll = dbIns
				.getCollection(propertiesManager.propertiesMap.get(prefix + Constants.COLLECTION_NAME.getValue()));

		long recordsCount = coll.count(andQuery);

		return recordsCount;

	}

	private Fields createAllSelectForReco(String pgrefNo, String payId, String amount, String orderId, String txnType) {

		Fields fields = new Fields();
		// PropertiesManager propManager = new PropertiesManager();
		List<BasicDBObject> dbObjList = new ArrayList<BasicDBObject>();
		dbObjList.add(new BasicDBObject(FieldType.PG_REF_NUM.getName(), pgrefNo));
		dbObjList.add(new BasicDBObject(FieldType.PAY_ID.getName(), payId));
		dbObjList.add(new BasicDBObject(FieldType.AMOUNT.getName(), amount));
		dbObjList.add(new BasicDBObject(FieldType.ORDER_ID.getName(), orderId));
		dbObjList.add(new BasicDBObject(FieldType.TXNTYPE.getName(), txnType));
		BasicDBObject andQuery = new BasicDBObject("$and", dbObjList);
		MongoDatabase dbIns = mongoInstance.getDB();
		MongoCollection<Document> coll = dbIns
				.getCollection(propertiesManager.propertiesMap.get(prefix + Constants.COLLECTION_NAME.getValue()));
		MongoCursor<Document> cursor = coll.find(andQuery).iterator();
		if (cursor.hasNext()) {
			Document documentObj = cursor.next();

			if (null != documentObj) {
				for (int j = 0; j < documentObj.size(); j++) {
					for (String columnName : aLLDB_Fields) {
						if (documentObj.get(columnName) != null) {
							fields.put(columnName, documentObj.get(columnName).toString());
						} else {

						}

					}
				}
			}
			fields.logAllFields("Previous fields");
		}
		cursor.close();
		return fields;

	}

	public Fields createAllSelectForRefundStatus(String orderId, String payId, String amount) {

		Fields fields = new Fields();
		// PropertiesManager propManager = new PropertiesManager();
		List<BasicDBObject> dbObjList = new ArrayList<BasicDBObject>();
		List<BasicDBObject> txnTypeConditionLst = new ArrayList<BasicDBObject>();
		dbObjList.add(new BasicDBObject(FieldType.REFUND_ORDER_ID.getName(), orderId));
		dbObjList.add(new BasicDBObject(FieldType.PAY_ID.getName(), payId));
		dbObjList.add(new BasicDBObject(FieldType.AMOUNT.getName(), amount));
		BasicDBObject txnTypeQuery = new BasicDBObject();

		txnTypeConditionLst.add(new BasicDBObject(FieldType.TXNTYPE.getName(), TransactionType.REFUND.getName()));
		txnTypeConditionLst.add(new BasicDBObject(FieldType.TXNTYPE.getName(), TransactionType.REFUNDRECO.getName()));

		txnTypeQuery.append("$or", txnTypeConditionLst);

		dbObjList.add(txnTypeQuery);
		BasicDBObject andQuery = new BasicDBObject("$and", dbObjList);
		MongoDatabase dbIns = mongoInstance.getDB();
		MongoCollection<Document> coll = dbIns
				.getCollection(propertiesManager.propertiesMap.get(prefix + Constants.COLLECTION_NAME.getValue()));

		FindIterable<Document> cursor = coll.find(andQuery).sort(new BasicDBObject("CREATE_DATE", -1)).limit(1);

		MongoCursor<Document> cursor2 = cursor.iterator();

		if (cursor2.hasNext()) {
			Document documentObj = cursor2.next();

			if (null != documentObj) {
				for (int j = 0; j < documentObj.size(); j++) {
					for (String columnName : aLLDB_Fields) {
						if (documentObj.get(columnName) != null) {
							fields.put(columnName, documentObj.get(columnName).toString());
						} else {

						}

					}
				}
			}
			fields.logAllFields("Previous fields");
		}
		cursor2.close();

		return fields;

	}

	public Fields createAllSelectForSale(String oid) {
		Fields fields = new Fields();
		// PropertiesManager propManager = new PropertiesManager();
		List<BasicDBObject> dbObjList = new ArrayList<BasicDBObject>();
		List<BasicDBObject> txnTypeConditionLst = new ArrayList<BasicDBObject>();
		dbObjList.add(new BasicDBObject(FieldType.OID.getName(), oid));
		BasicDBObject txnTypeQuery = new BasicDBObject();

		txnTypeConditionLst.add(new BasicDBObject(FieldType.TXNTYPE.getName(), TransactionType.SALE.getName()));
		txnTypeConditionLst.add(new BasicDBObject(FieldType.STATUS.getName(), StatusType.CAPTURED.getName()));

		txnTypeQuery.append("$and", txnTypeConditionLst);

		dbObjList.add(txnTypeQuery);
		BasicDBObject andQuery = new BasicDBObject("$and", dbObjList);
		MongoDatabase dbIns = mongoInstance.getDB();
		MongoCollection<Document> coll = dbIns
				.getCollection(propertiesManager.propertiesMap.get(prefix + Constants.COLLECTION_NAME.getValue()));

		FindIterable<Document> cursor = coll.find(andQuery).sort(new BasicDBObject("CREATE_DATE", -1)).limit(1);

		MongoCursor<Document> cursor2 = cursor.iterator();

		if (cursor2.hasNext()) {
			Document documentObj = cursor2.next();

			if (null != documentObj) {
				for (int j = 0; j < documentObj.size(); j++) {
					for (String columnName : aLLDB_Fields) {
						if (documentObj.get(columnName) != null) {
							fields.put(columnName, documentObj.get(columnName).toString());
						} else {

						}

					}
				}
			}
			fields.logAllFields("Previous fields");
		}
		cursor2.close();

		return fields;
	}

	public void getDuplicate(Fields fields) throws SystemException {
		try {
			MongoDatabase dbIns = null;
			String orderId = fields.get(FieldType.ORDER_ID.getName());
			String payId = fields.get(FieldType.PAY_ID.getName());
			String currencyString = fields.get(FieldType.CURRENCY_CODE.getName());
			String amount = fields.get(FieldType.AMOUNT.getName());
			if (!StringUtils.isEmpty(amount) && !StringUtils.isEmpty(currencyString)) {
				amount = Amount.toDecimal(amount, currencyString);
			}
			List<BasicDBObject> conditionList = new ArrayList<BasicDBObject>();
			conditionList.add(new BasicDBObject(FieldType.ORDER_ID.getName(), orderId));
			conditionList.add(new BasicDBObject(FieldType.PAY_ID.getName(), payId));
			conditionList.add(new BasicDBObject(FieldType.CURRENCY_CODE.getName(), currencyString));
			conditionList.add(new BasicDBObject(FieldType.AMOUNT.getName(), amount));
			BasicDBObject query = new BasicDBObject("$and", conditionList);
			dbIns = mongoInstance.getDB();
			MongoCollection<Document> collection = dbIns
					.getCollection(propertiesManager.propertiesMap.get(prefix + Constants.COLLECTION_NAME.getValue()));
			MongoCursor<Document> cursor = collection.find(query).iterator();
			if (cursor.hasNext()) {
				Document documentObj = cursor.next();
				if (null != documentObj) {
					for (int i = 0; i < documentObj.size(); i++) {
						String ORIG_TXN_ID = documentObj.getString(FieldType.ORIG_TXN_ID.getName());
						fields.put(FieldType.ORIG_TXN_ID.getName(), ORIG_TXN_ID);
						fields.put(FieldType.DUPLICATE_YN.getName(), "Y");
					}
				} else {
					fields.put(FieldType.DUPLICATE_YN.getName(), "N");
				}
			}
			cursor.close();
		} catch (Exception exception) {
			// MDC.put(FieldType.INTERNAL_CUSTOM_MDC.getName(),
			// fields.getCustomMDC());
			String message = "Error while inserting transaction in database";
			logger.error(message, exception);
			throw new SystemException(ErrorType.DATABASE_ERROR, exception, message);
		}
	}

	public Fields getFields(String txnId, String payId) throws SystemException {
		try {
			return createAllSelect(txnId, payId);
		} catch (Exception exception) {
			// MDC.put(FieldType.INTERNAL_CUSTOM_MDC.getName(),
			// fields.getCustomMDC());
			String message = "Error while inserting transaction in database";
			logger.error(message, exception);
			throw new SystemException(ErrorType.DATABASE_ERROR, exception, message);
		}
	}

	public Fields getFieldsForRefund(String txnId, String payId, String txnType, String status) throws SystemException {
		try {
			return createAllForRefund(txnId, payId, txnType, status);
		} catch (Exception exception) {
			// MDC.put(FieldType.INTERNAL_CUSTOM_MDC.getName(),
			// fields.getCustomMDC());
			String message = "No such transaction found";
			logger.error(message, exception);
			throw new SystemException(ErrorType.NO_SUCH_TRANSACTION, exception, message);
		}
	}
	
	
	public Fields getFieldsForRefundPayout(String txnId, String payId, String txnType, String status) throws SystemException {
		try {
			return createAllForRefundPayout(txnId, payId, txnType, status);
		} catch (Exception exception) {
			// MDC.put(FieldType.INTERNAL_CUSTOM_MDC.getName(),
			// fields.getCustomMDC());
			String message = "No such transaction found";
			logger.error(message, exception);
			throw new SystemException(ErrorType.NO_SUCH_TRANSACTION, exception, message);
		}
	}

	public Fields getFieldsForFedUpi(String refId, String txnType) throws SystemException {
		try {
			return createAllForFedUpi(refId, txnType);
		} catch (Exception exception) {
			// MDC.put(FieldType.INTERNAL_CUSTOM_MDC.getName(),
			// fields.getCustomMDC());
			String message = "Error while inserting transaction in database";
			logger.error(message, exception);
			throw new SystemException(ErrorType.DATABASE_ERROR, exception, message);
		}
	}

	public Fields getFieldsForHdfcUpi(String refId, String txnType) throws SystemException {
		try {
			return createAllForHdfcUpi(refId, txnType);
		} catch (Exception exception) {
			// MDC.put(FieldType.INTERNAL_CUSTOM_MDC.getName(),
			// fields.getCustomMDC());
			String message = "Error while inserting transaction in database";
			logger.error(message, exception);
			throw new SystemException(ErrorType.DATABASE_ERROR, exception, message);
		}
	}

	public Fields getFieldsForReco(String pgrefNo, String payId, String amount, String orderId, String txnType)
			throws SystemException {
		try {
			return createAllSelectForReco(pgrefNo, payId, amount, orderId, txnType);
		} catch (Exception exception) {
			// MDC.put(FieldType.INTERNAL_CUSTOM_MDC.getName(),
			// fields.getCustomMDC());
			String message = "Error while inserting transaction in database";
			logger.error(message, exception);
			throw new SystemException(ErrorType.DATABASE_ERROR, exception, message);
		}
	}

	public Fields getFieldsForStatus(String orderId, String payId, String amount) throws SystemException {
		try {
			return createAllSelectForStatus(orderId, payId, amount);
		} catch (Exception exception) {
			// MDC.put(FieldType.INTERNAL_CUSTOM_MDC.getName(),
			// fields.getCustomMDC());
			String message = "No such transaction found";
			logger.error(message, exception);
			throw new SystemException(ErrorType.NO_SUCH_TRANSACTION, exception, message);
		}
	}
	
	public Fields getFieldsForStatusPayout(String orderId, String payId, String amount) throws SystemException {
		try {
			return createAllSelectForStatusPayout(orderId, payId, amount);
		} catch (Exception exception) {
			// MDC.put(FieldType.INTERNAL_CUSTOM_MDC.getName(),
			// fields.getCustomMDC());
			String message = "No such transaction found";
			logger.error(message, exception);
			throw new SystemException(ErrorType.NO_SUCH_TRANSACTION, exception, message);
		}
	}
	
	

	public Long validateDuplicateSaleOrderId(String orderId, String payId, String currencyCode) throws SystemException {
		try {
			return createAllSelectForSaleOrderId(orderId, payId, currencyCode);
		} catch (Exception exception) {
			String message = "Error while searching transaction in database";
			logger.error(message, exception);
			throw new SystemException(ErrorType.DATABASE_ERROR, exception, message);
		}
	}
	public Long validateDuplicateSaleOrderIdPayout(String orderId, String payId, String currencyCode) throws SystemException {
		try {
			return createAllSelectForSaleOrderIdPayout(orderId, payId, currencyCode);
		} catch (Exception exception) {
			String message = "Error while searching transaction in database";
			logger.error(message, exception);
			throw new SystemException(ErrorType.DATABASE_ERROR, exception, message);
		}
	}

	public Long validateDuplicateSubmit(String orderId) throws SystemException {
		try {
			return createAllSelectForDuplicateSubmit(orderId);
		} catch (Exception exception) {
			String message = "Error while searching transaction in database";
			logger.error(message, exception);
			throw new SystemException(ErrorType.DATABASE_ERROR, exception, message);
		}
	}

	public Long validateDuplicateSaleOrderIdInRefund(String RefundOrderId, String payId, String currencyCode)
			throws SystemException {
		try {
			return createAllSelectForSaleOrderIdRefund(RefundOrderId, payId, currencyCode);
		} catch (Exception exception) {
			String message = "Error while searching transaction in database";
			logger.error(message, exception);
			throw new SystemException(ErrorType.DATABASE_ERROR, exception, message);
		}
	}

	public Long validateDuplicateRefundOrderIdInSale(String orderId, String payId, String currencyCode)
			throws SystemException {
		try {
			return createAllSelectForRefundOrderIdSale(orderId, payId, currencyCode);
		} catch (Exception exception) {
			String message = "Error while searching transaction in database";
			logger.error(message, exception);
			throw new SystemException(ErrorType.DATABASE_ERROR, exception, message);
		}
	}

	public Long validateDuplicateOrderId(Fields fields, String orderId, String payId, String currencyCode)
			throws SystemException {
		try {
			return createAllSelectForOrderId(fields, orderId, payId, currencyCode);
		} catch (Exception exception) {

			String message = "Error while searching transaction in database";
			logger.error(message, exception);
			throw new SystemException(ErrorType.DATABASE_ERROR, exception, message);
		}
	}

	public Long validateDuplicateRefundOrderId(String refundOrderId, String payId, String currencyCode, String status)
			throws SystemException {
		try {
			return createAllSelectForRefundOrderId(refundOrderId, payId, currencyCode, status);
		} catch (Exception exception) {
			String message = "Error while searching transaction in database";
			logger.error(message, exception);
			throw new SystemException(ErrorType.DATABASE_ERROR, exception, message);
		}
	}

	public Fields getFieldsForVerify(String orderId, String payId, String amount, String pgRefNum, String status)
			throws SystemException {
		try {
			return createAllSelectForVerify(orderId, payId, amount, pgRefNum, status);
		} catch (Exception exception) {
			String message = "Error while searching transaction in database";
			logger.error(message, exception);
			throw new SystemException(ErrorType.DATABASE_ERROR, exception, message);
		}
	}

	public Fields getFieldsForSettlement(String custId, String oId, String status) throws SystemException {
		try {
			return createAllSelectForSettlement(custId, oId, status);
		} catch (Exception exception) {
			String message = "Error while searching transaction in database";
			logger.error(message, exception);
			throw new SystemException(ErrorType.DATABASE_ERROR, exception, message);
		}
	}

	public Fields getFieldsForRefundStatus(String orderId, String payId, String amount) throws SystemException {
		try {
			return createAllSelectForRefundStatus(orderId, payId, amount);
		} catch (Exception exception) {
			// MDC.put(FieldType.INTERNAL_CUSTOM_MDC.getName(),
			// fields.getCustomMDC());
			String message = "Error while inserting transaction in database";
			logger.error(message, exception);
			throw new SystemException(ErrorType.DATABASE_ERROR, exception, message);
		}
	}

	public Fields getFieldsForSale(String oid) throws SystemException {
		try {
			return createAllSelectForSale(oid);
		} catch (Exception exception) {
			// MDC.put(FieldType.INTERNAL_CUSTOM_MDC.getName(),
			// fields.getCustomMDC());
			String message = "Error while inserting transaction in database";
			logger.error(message, exception);
			throw new SystemException(ErrorType.DATABASE_ERROR, exception, message);
		}
	}

	public void insertTransaction(Fields fields) throws SystemException {
		try {
			MongoDatabase dbIns = null;
			BasicDBObject newFieldsObj = new BasicDBObject();

			String amountString = fields.get(FieldType.AMOUNT.getName());
			String surchargeAmountString = fields.get(FieldType.SURCHARGE_AMOUNT.getName());
			String currencyString = fields.get(FieldType.CURRENCY_CODE.getName());
			String totalAmountString = fields.get(FieldType.TOTAL_AMOUNT.getName());

			String amount = "0";
			if (!StringUtils.isEmpty(amountString) && !StringUtils.isEmpty(currencyString)) {
				amount = Amount.toDecimal(amountString, currencyString);
				newFieldsObj.put(FieldType.AMOUNT.getName(), amount);
			}

			String totalAmount = "0";
			if (!StringUtils.isEmpty(totalAmountString) && !StringUtils.isEmpty(currencyString)) {
				totalAmount = Amount.toDecimal(totalAmountString, currencyString);
				newFieldsObj.put(FieldType.TOTAL_AMOUNT.getName(), totalAmount);
			}

			String surchargeAmount = "0";
			if (!StringUtils.isEmpty(surchargeAmountString) && !StringUtils.isEmpty(currencyString)) {
				surchargeAmount = Amount.toDecimal(surchargeAmountString, currencyString);
				newFieldsObj.put(FieldType.SURCHARGE_AMOUNT.getName(), surchargeAmount);
			}
			String origTxnId = "0";
			String origTxnStr = fields.get(FieldType.ORIG_TXN_ID.getName());
			if (StringUtils.isEmpty(origTxnStr)) {
				String internalOrigTxnStr = fields.get(FieldType.INTERNAL_ORIG_TXN_ID.getName());
				if (StringUtils.isEmpty(internalOrigTxnStr)) {
					newFieldsObj.put(FieldType.ORIG_TXN_ID.getName(), origTxnId);
				}
				if (!StringUtils.isEmpty(internalOrigTxnStr)) {
					newFieldsObj.put(FieldType.ORIG_TXN_ID.getName(), internalOrigTxnStr);
				}
			}

			if (!StringUtils.isEmpty(origTxnStr)) {
				newFieldsObj.put(FieldType.ORIG_TXN_ID.getName(), origTxnStr);
			}

			String origTxnType = fields.get(FieldType.INTERNAL_ORIG_TXN_TYPE.getName());
			if (!StringUtils.isEmpty(origTxnType)) {
				String txnType = fields.get(FieldType.TXNTYPE.getName());
				if ((txnType.equals(TransactionType.REFUND.getName()))
						|| (txnType.equals(TransactionType.REFUNDRECO.getName()))) {
					newFieldsObj.put(FieldType.ORIG_TXNTYPE.getName(), TransactionType.REFUND.getName());
				} else {
					newFieldsObj.put(FieldType.ORIG_TXNTYPE.getName(), origTxnType);
				}

			}
			if (bsesPayid.equalsIgnoreCase(fields.get(FieldType.PAY_ID.getName()))) {
				newFieldsObj.put(FieldType.UDF9.getName(), fields.get(FieldType.UDF9.getName()));
				newFieldsObj.put(FieldType.UDF10.getName(), fields.get(FieldType.UDF10.getName()));
				newFieldsObj.put(FieldType.UDF11.getName(), fields.get(FieldType.UDF11.getName()));
				
			}
			String pgRefNo = "0";
			String pgRefNum = fields.get(FieldType.PG_REF_NUM.getName());
			if (StringUtils.isEmpty(pgRefNum)) {
				newFieldsObj.put(FieldType.PG_REF_NUM.getName(), pgRefNo);
			}

			if (!StringUtils.isEmpty(pgRefNum)) {
				newFieldsObj.put(FieldType.PG_REF_NUM.getName(), pgRefNum);
			}

			String invoiceId = fields.get(FieldType.INVOICE_ID.getName());
			if (!StringUtils.isEmpty(invoiceId)) {
				newFieldsObj.put(FieldType.INVOICE_ID.getName(), invoiceId);
			}

			String domainName = fields.get(FieldType.INTERNAL_CUST_DOMAIN.getName());
			long tenantId = 0L;

			if (null != domainName && !domainName.equalsIgnoreCase("NA")) {
				tenantId = companyProfileDao.getTenantIdByDomainName(domainName);
			}

			if (tenantId > 0L) {
				newFieldsObj.put(FieldType.TENANT_ID.getName(), tenantId);
			}

			String acctId = "0";
			String acctIdStr = fields.get(FieldType.ACCT_ID.getName());
			if (acctIdStr != null && acctIdStr.length() > 0) {

				newFieldsObj.put(FieldType.ACCT_ID.getName(), acctIdStr);
			}
			if (acctIdStr == null) {
				newFieldsObj.put(FieldType.ACCT_ID.getName(), acctId);
			}
			String acqId = "0";
			String acqIdStr = fields.get(FieldType.ACQ_ID.getName());
			if (acqIdStr != null && acqIdStr.length() > 0) {
				newFieldsObj.put(FieldType.ACQ_ID.getName(), acqIdStr);
			}
			if (acqIdStr == null) {
				newFieldsObj.put(FieldType.ACQ_ID.getName(), acqId);
			}
			String oid = fields.get(FieldType.OID.getName());
			String longOid = "0";
			if (!StringUtils.isEmpty(oid)) {

				newFieldsObj.put(FieldType.OID.getName(), oid);
			}
			if (StringUtils.isEmpty(oid)) {
				newFieldsObj.put(FieldType.OID.getName(), longOid);
			}
			String udf1 = fields.get(FieldType.UDF1.getName());
			if (!StringUtils.isEmpty(udf1)) {
				newFieldsObj.put(FieldType.UDF1.getName(), udf1);
			}
			String udf2 = fields.get(FieldType.UDF2.getName());
			if (!StringUtils.isEmpty(udf2)) {
				newFieldsObj.put(FieldType.UDF2.getName(), udf2);
			}
			String udf3 = fields.get(FieldType.UDF3.getName());
			if (!StringUtils.isEmpty(udf3)) {
				newFieldsObj.put(FieldType.UDF3.getName(), udf3);
			}
			String udf4 = fields.get(FieldType.UDF4.getName());
			if (!StringUtils.isEmpty(udf4)) {
				newFieldsObj.put(FieldType.UDF4.getName(), udf4);
			}
			String udf5 = fields.get(FieldType.UDF5.getName());
			if (!StringUtils.isEmpty(udf5)) {
				newFieldsObj.put(FieldType.UDF5.getName(), udf5);
			}
			String phone = fields.get(FieldType.CUST_PHONE.getName());
			if (!StringUtils.isEmpty(phone)) {
				newFieldsObj.put(FieldType.CUST_PHONE.getName(), phone);
			}
			String udf6 = fields.get(FieldType.UDF6.getName());
			if (!StringUtils.isEmpty(udf6)) {
				newFieldsObj.put(FieldType.UDF6.getName(), udf6);
			}
			String udf7 = fields.get(FieldType.UDF6.getName());
			if (!StringUtils.isEmpty(udf7)) {
				newFieldsObj.put(FieldType.UDF7.getName(), udf7);
			}
			String udf8 = fields.get(FieldType.UDF8.getName());
			if (!StringUtils.isEmpty(udf6)) {
				newFieldsObj.put(FieldType.UDF8.getName(), udf8);
			}
			String udf9 = fields.get(FieldType.UDF9.getName());
			if (!StringUtils.isEmpty(udf9)) {
				newFieldsObj.put(FieldType.UDF9.getName(), udf9);
			}
			String udf10 = fields.get(FieldType.UDF10.getName());
			if (!StringUtils.isEmpty(udf10)) {
				newFieldsObj.put(FieldType.UDF10.getName(), udf10);
			}
			String udf11 = fields.get(FieldType.UDF11.getName());
			if (!StringUtils.isEmpty(udf11)) {
				newFieldsObj.put(FieldType.UDF11.getName(), udf11);
			}
			String udf12 = fields.get(FieldType.UDF12.getName());
			if (!StringUtils.isEmpty(udf12)) {
				newFieldsObj.put(FieldType.UDF12.getName(), udf12);
			}
			String udf13 = fields.get(FieldType.UDF13.getName());
			if (!StringUtils.isEmpty(udf13)) {
				newFieldsObj.put(FieldType.UDF13.getName(), udf13);
			}
			String paymentsRegion = fields.get(FieldType.PAYMENTS_REGION.getName());
			if (!StringUtils.isEmpty(paymentsRegion)) {
				newFieldsObj.put(FieldType.PAYMENTS_REGION.getName(), paymentsRegion);
			}

			String cardHolderType = fields.get(FieldType.CARD_HOLDER_TYPE.getName());
			if (!StringUtils.isEmpty(cardHolderType)) {
				newFieldsObj.put(FieldType.CARD_HOLDER_TYPE.getName(), cardHolderType);
			}

			String requestDate = fields.get(FieldType.REQUEST_DATE.getName());
			if (!StringUtils.isEmpty(requestDate)) {
				newFieldsObj.put(FieldType.REQUEST_DATE.getName(), requestDate);
			}

			String saleAmoiunt = fields.get(FieldType.SALE_AMOUNT.getName());
			if ((!StringUtils.isEmpty(saleAmoiunt) && !StringUtils.isEmpty(currencyString))) {
				saleAmoiunt = Amount.toDecimal(saleAmoiunt, currencyString);
				newFieldsObj.put(FieldType.SALE_AMOUNT.getName(), saleAmoiunt);
			}

			String totalSaleAmoiunt = fields.get(FieldType.SALE_TOTAL_AMOUNT.getName());
			if ((!StringUtils.isEmpty(totalSaleAmoiunt) && !StringUtils.isEmpty(currencyString))) {
				totalSaleAmoiunt = Amount.toDecimal(totalSaleAmoiunt, currencyString);
				newFieldsObj.put(FieldType.SALE_TOTAL_AMOUNT.getName(), totalSaleAmoiunt);
			}
			
			String refundMode = fields.get("REFUND_MODE");
			if (!StringUtils.isEmpty(refundMode)) {
				newFieldsObj.put("REFUND_MODE", refundMode);
			}
			
			String refundProcess = fields.get("REFUND_PROCESS");
			if (!StringUtils.isEmpty(refundProcess)) {
				newFieldsObj.put("REFUND_PROCESS", refundProcess);
			}

			String postingMethodFlag = fields.get(FieldType.POSTING_METHOD_FLAG.getName());
			if (!StringUtils.isEmpty(postingMethodFlag)) {
				newFieldsObj.put(FieldType.POSTING_METHOD_FLAG.getName(), postingMethodFlag);
			}
			
			for (int i = 0; i < fields.size(); i++) {

				Date dNow = new Date();
				String dateNow = DateCreater.formatDateForDb(dNow);
				for (String columnName : aLLDB_Fields) {

					if (columnName.equals(FieldType.CREATE_DATE.getName())) {
						newFieldsObj.put(columnName, dateNow);
					} else if (columnName.equals(FieldType.DATE_INDEX.getName())) {
						newFieldsObj.put(columnName, dateNow.substring(0, 10).replace("-", ""));
					} else if (columnName.equals(FieldType.UPDATE_DATE.getName())) {
						newFieldsObj.put(columnName, dateNow);
					} else if (columnName.equals("_id")) {
						newFieldsObj.put(columnName, fields.get(FieldType.TXN_ID.getName()));
					} else if (columnName.equals(FieldType.INSERTION_DATE.getName())) {
						newFieldsObj.put(columnName, dNow);
					} else if (columnName.equals(FieldType.ACQUIRER_TDR_SC.getName())) {
						newFieldsObj.put(columnName, fields.get(FieldType.ACQUIRER_TDR_SC.getName()));
					} else if (columnName.equals(FieldType.ACQUIRER_GST.getName())) {
						newFieldsObj.put(columnName, fields.get(FieldType.ACQUIRER_GST.getName()));
					} else if (columnName.equals(FieldType.PG_TDR_SC.getName())) {
						newFieldsObj.put(columnName, fields.get(FieldType.PG_TDR_SC.getName()));
					} else if (columnName.equals(FieldType.PG_GST.getName())) {
						newFieldsObj.put(columnName, fields.get(FieldType.PG_GST.getName()));
					} else if (columnName.equals(FieldType.AMOUNT.getName())) {
						continue;
					} else if (columnName.equals(FieldType.TOTAL_AMOUNT.getName())) {
						continue;
					} else if (columnName.equals(FieldType.SURCHARGE_AMOUNT.getName())) {
						continue;
					} else if (columnName.equals(FieldType.ORIG_TXN_ID.getName())) {
						continue;
					} else if (columnName.equals(FieldType.PG_REF_NUM.getName())) {
						continue;
					} else if (columnName.equals(FieldType.ACCT_ID.getName())) {
						continue;
					} else if (columnName.equals(FieldType.ACQ_ID.getName())) {
						continue;
					} else if (columnName.equals(FieldType.OID.getName())) {
						continue;
					} else if (columnName.equals(FieldType.UDF1.getName())) {
						continue;
					} else if (columnName.equals(FieldType.CUST_PHONE.getName())) {
						continue;
					} else if (columnName.equals(FieldType.UDF2.getName())) {
						continue;
					} else if (columnName.equals(FieldType.UDF3.getName())) {
						continue;
					} else if (columnName.equals(FieldType.UDF4.getName())) {
						continue;
					} else if (columnName.equals(FieldType.UDF5.getName())) {
						continue;
					} else if (columnName.equals(FieldType.UDF6.getName())) {
						continue;
					}else if (columnName.equals(FieldType.UDF7.getName())) {
						continue;
					}else if (columnName.equals(FieldType.UDF8.getName())) {
						continue;
					}else if (columnName.equals(FieldType.UDF9.getName())) {
						continue;
					}else if (columnName.equals(FieldType.UDF10.getName())) {
						continue;
					}else if (columnName.equals(FieldType.UDF11.getName())) {
						continue;
					}else if (columnName.equals(FieldType.UDF12.getName())) {
						continue;
					}else if (columnName.equals(FieldType.UDF13.getName())) {
						continue;
					}else if (columnName.equals(FieldType.PAYMENTS_REGION.getName())) {
						continue;
					} else if (columnName.equals(FieldType.CARD_HOLDER_TYPE.getName())) {
						continue;
					} else if (columnName.equals(FieldType.SALE_AMOUNT.getName())) {
						continue;
					} else if (columnName.equals(FieldType.SALE_TOTAL_AMOUNT.getName())) {
						continue;
					} else if (columnName.equals(FieldType.ORIG_TXNTYPE.getName())) {
						continue;
					} else {
						newFieldsObj.put(columnName, fields.get(columnName));
					}
				}
			}

			String txnStatus = fields.get(FieldType.STATUS.getName());
			String refundTxnType = fields.get(FieldType.TXNTYPE.getName());
			String refundOrigTxnId = fields.get(FieldType.ORIG_TXN_ID.getName());
			logger.info("txnStatus = {}, refundTxnType = {}, refundOrigTxnId = {}",txnStatus,refundTxnType,refundOrigTxnId);
			if(!StringUtils.isEmpty(txnStatus) && !txnStatus.equalsIgnoreCase("REFUND_INITIATED") 
					&& !StringUtils.isEmpty(refundTxnType) && refundTxnType.equalsIgnoreCase("REFUND")) {
				logger.info("+++++++++++++++++++++++++ REFUND_PROCESS ++++++++++++++++++++++++++++++++++++");
				newFieldsObj.put("REFUND_PROCESS", "S");
				if(!StringUtils.isEmpty(refundOrigTxnId)){
					newFieldsObj.put(FieldType.PG_REF_NUM.getName(), refundOrigTxnId);
				}
			}
			
			newFieldsObj.put("CHANNEL", "Fiat");
			
			String orderId = newFieldsObj.getString(FieldType.ORDER_ID.getName());
			String oids = newFieldsObj.getString(FieldType.OID.getName());
			String origTxnTypes = newFieldsObj.getString(FieldType.ORIG_TXNTYPE.getName());
			String status = newFieldsObj.getString(FieldType.STATUS.getName());

			logger.info("Before fetching InternalFieldRequest Datat,  orderId :" + orderId + " origTxnTypes :"
					+ origTxnTypes + " oids :" + oids + " status " + status);
			String internalFieldReq = null;
			if (!StringUtils.isBlank(orderId) & !StringUtils.isBlank(oids) & !StringUtils.isBlank(origTxnTypes)
					& !StringUtils.isBlank(status) & status.equalsIgnoreCase("Sent to Bank")) {
				internalFieldReq = getInternalFieldRequest(orderId, oids, origTxnTypes);
			}

			if (null != internalFieldReq) {
				newFieldsObj.put(FieldType.INTERNAL_REQUEST_FIELDS.getName(), internalFieldReq);
			}

			dbIns = mongoInstance.getDB();
			MongoCollection<Document> collection = dbIns
					.getCollection(propertiesManager.propertiesMap.get(prefix + Constants.COLLECTION_NAME.getValue()));
			Document doc = new Document(newFieldsObj);

			if (StringUtils.isBlank(doc.getString(FieldType.INTERNAL_CUST_IP.getName()))) {
				String ip = getIPFromInitiateRequest(doc.getString(FieldType.OID.getName()));
				doc.put(FieldType.INTERNAL_CUST_IP.getName(), ip);
			}

			collection.insertOne(doc);

			// Change by Shaiwal, whole transaction status update block moved inside a
			// separate try/catch
			try {
				logger.info("calling update service for order id " + fields.get(FieldType.ORDER_ID.getName()));
				Runnable runnable = new Runnable() {
					public void run() {

						logger.info(
								"Updating Status Collection for Order Id " + fields.get(FieldType.ORDER_ID.getName()));
						updateStatusColl(fields, doc);
					}
				};
				// Added by RR to update Txn status
				executorService.submit(runnable);
				/*
				 * Thread thread = new Thread(runnable); thread.start();
				 */
			}

			catch (Exception e) {
				logger.error("Txn Status Collection failed for Order Id " + fields.get(FieldType.ORDER_ID.getName()));
				logger.error("Exception in updating Order status", e);

				if (StringUtils.isNotBlank(PropertiesManager.propertiesMap.get("AllowTxnUpdtFailAlert"))
						&& PropertiesManager.propertiesMap.get("AllowTxnUpdtFailAlert").equalsIgnoreCase("Y")) {

					smsCount = smsCount + 1;
					if (smsCount % 10 == 0) {

						StringBuilder smsBody = new StringBuilder();
						smsBody.append("Pay10 Alert !");
						smsBody.append("Transaction Status not updated for Order Id = "
								+ fields.get(FieldType.ORDER_ID.getName()));

						String smsSenderList = PropertiesManager.propertiesMap.get("smsAlertList");

						for (String mobile : smsSenderList.split(",")) {
							try {
								smsSender.sendSMS(mobile, smsBody.toString());
							} catch (IOException ioEx) {
								logger.error("SMS not sent for transaction status update failure when account is null",
										ioEx);
							}
						}

					}

				}

			}

		} catch (Exception exception) {
			String message = "Error while inserting transaction in database";
			logger.error(message, exception);
			throw new SystemException(ErrorType.DATABASE_ERROR, exception, message);
		}
	}
	
	
	
	
	
	public void insertTransactionPO(Fields fields) throws SystemException {
		logger.info("CHECKING ABC: "+fields.getFieldsAsString());
		
		try {
			MongoDatabase dbIns = null;
			BasicDBObject newFieldsObj = new BasicDBObject();

			
			newFieldsObj.put(FieldType.ACC_NO.getName(),fields.get(FieldType.ACC_NO.getName()));
			
			if(StringUtils.isNoneBlank(fields.get(FieldType.CURRENCY_CODE.getName()))) {
				newFieldsObj.put(FieldType.AMOUNT.getName(),Amount.toDecimal(fields.get(FieldType.AMOUNT.getName()), fields.get(FieldType.CURRENCY_CODE.getName())));

			}else {
				newFieldsObj.put(FieldType.AMOUNT.getName(),Amount.toDecimal(fields.get(FieldType.AMOUNT.getName()), "356"));

			}
			newFieldsObj.put(FieldType.CLIENT_IP.getName(),fields.get(FieldType.CLIENT_IP.getName()));
			newFieldsObj.put(FieldType.CURRENCY_CODE.getName(),fields.get(FieldType.CURRENCY_CODE.getName()));
			newFieldsObj.put(FieldType.ORDER_ID.getName(),fields.get(FieldType.ORDER_ID.getName()));
			newFieldsObj.put(FieldType.ACQUIRER_TYPE.getName(),fields.get(FieldType.ACQUIRER_TYPE.getName()));
			newFieldsObj.put(FieldType.PAY_ID.getName(),fields.get(FieldType.PAY_ID.getName()));
			newFieldsObj.put(FieldType.PAYER_NAME.getName(),fields.get(FieldType.CUST_NAME.getName()));
			newFieldsObj.put(FieldType.PAYER_PHONE.getName(),fields.get(FieldType.CUST_PHONE.getName()));
			newFieldsObj.put(FieldType.PAY_TYPE.getName(),fields.get(FieldType.PAY_TYPE.getName()));
			newFieldsObj.put(FieldType.ACC_TYPE.getName(),fields.get(FieldType.ACC_TYPE.getName()));
			newFieldsObj.put(FieldType.ACC_PROVINCE.getName(),fields.get(FieldType.ACC_PROVINCE.getName()));
			newFieldsObj.put(FieldType.ACC_CITY_NAME.getName(),fields.get(FieldType.ACC_CITY_NAME.getName()));
			newFieldsObj.put(FieldType.ACC_NAME.getName(),fields.get(FieldType.ACC_NAME.getName()));
			newFieldsObj.put(FieldType.BANK_BRANCH.getName(),fields.get(FieldType.BANK_BRANCH.getName()));
			newFieldsObj.put(FieldType.BANK_CODE.getName(),fields.get(FieldType.BANK_CODE.getName()));
			newFieldsObj.put(FieldType.STATUS.getName(),fields.get(FieldType.STATUS.getName()));
			newFieldsObj.put(FieldType.IFSC.getName(),fields.get(FieldType.IFSC.getName()));
			newFieldsObj.put(FieldType.DATE_INDEX.getName(),new SimpleDateFormat("yyyyMMdd").format(new Date()));
			newFieldsObj.put(FieldType.TXN_TYPE.getName(),fields.get(FieldType.TXN_TYPE.getName()));
			newFieldsObj.put(FieldType.PG_TXN_MESSAGE.getName(),fields.get(FieldType.PG_TXN_MESSAGE.getName()));
			newFieldsObj.put(FieldType.ACQUIRER_TDR_SC.getName(),fields.get(FieldType.ACQUIRER_TDR_SC.getName()));
			newFieldsObj.put(FieldType.ACQUIRER_GST.getName(),fields.get(FieldType.ACQUIRER_GST.getName()));
			newFieldsObj.put(FieldType.PG_TDR_SC.getName(),fields.get(FieldType.PG_TDR_SC.getName()));
			newFieldsObj.put(FieldType.SURCHARGE_FLAG.getName(),fields.get(FieldType.SURCHARGE_FLAG.getName()));
			newFieldsObj.put(FieldType.SURCHARGE_FLAG.getName(),fields.get(FieldType.SURCHARGE_FLAG.getName()));
			newFieldsObj.put(FieldType.BANK_NAME.getName(),fields.get(FieldType.BANK_NAME.getName()));
			newFieldsObj.put(FieldType.TRANSFER_TYPE.getName(),fields.get(FieldType.TRANSFER_TYPE.getName()));
			
			

			
			
//			if(StringUtils.isNotBlank(fields.get(FieldType.TOTAL_AMOUNT.getName()))) {
//
//				if(StringUtils.isNoneBlank(fields.get(FieldType.CURRENCY_CODE.getName()))) {
//					newFieldsObj.put(FieldType.TOTAL_AMOUNT.getName(),Amount.toDecimal(fields.get(FieldType.TOTAL_AMOUNT.getName()), fields.get(FieldType.CURRENCY_CODE.getName())));
//
//				}else {
//					newFieldsObj.put(FieldType.TOTAL_AMOUNT.getName(),Amount.toDecimal(fields.get(FieldType.AMOUNT.getName()), "356"));
//
//				}
//			}
			
			
			if(StringUtils.isNotBlank(fields.get(FieldType.RESPONSE_CODE.getName()))) {
				newFieldsObj.put(FieldType.RESPONSE_CODE.getName(),fields.get(FieldType.RESPONSE_CODE.getName()));
				}
			
			if(StringUtils.isNotBlank(fields.get(FieldType.RESPONSE_MESSAGE.getName()))) {
				newFieldsObj.put(FieldType.RESPONSE_MESSAGE.getName(),fields.get(FieldType.RESPONSE_MESSAGE.getName()));
				}
			
			if(StringUtils.isNotBlank(fields.get(FieldType.SURCHARGE_AMOUNT.getName()))) {
			newFieldsObj.put(FieldType.SURCHARGE_AMOUNT.getName(),Amount.toDecimal(fields.get(FieldType.SURCHARGE_AMOUNT.getName()), fields.get(FieldType.CURRENCY_CODE.getName())));
			}
			newFieldsObj.put(FieldType.REMARKS.getName(),fields.get(FieldType.REMARKS.getName()));
			newFieldsObj.put(FieldType.TXNTYPE.getName(),fields.get(FieldType.TXNTYPE.getName()));
			
			if(StringUtils.isNotBlank(fields.get(FieldType.MERCHANT_NAME.getName()))) {
			newFieldsObj.put(FieldType.MERCHANT_NAME.getName(),fields.get(FieldType.MERCHANT_NAME.getName()));
			}
			newFieldsObj.put(FieldType.PG_REF_NUM.getName(),fields.get(FieldType.PG_REF_NUM.getName()));
			
			newFieldsObj.put(FieldType.CREATE_DATE.getName(),new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
			
			newFieldsObj.put(FieldType.WEBHOOK_FAILED_COUNT.getName(), "0");
			newFieldsObj.put(FieldType.WEBHOOK_POST_FLAG.getName(), "N");
			
			
			
			
			dbIns = mongoInstance.getDB();
			MongoCollection<Document> collection = dbIns
					.getCollection(propertiesManager.propertiesMap.get(prefix + "PO_Transaction"));
			Document doc = new Document(newFieldsObj);

			
			collection.insertOne(doc);

			// Change by Shaiwal, whole transaction status update block moved inside a
			// separate try/catch
			try {
				logger.info("calling update service for order id " + fields.get(FieldType.ORDER_ID.getName()));
				Runnable runnable = new Runnable() {
					public void run() {

						logger.info(
								"Updating Status Collection for Order Id " + fields.get(FieldType.ORDER_ID.getName()));
						updateStatusCollPO(fields, doc);
					}
				};
				// Added by RR to update Txn status
				executorService.submit(runnable);
				/*
				 * Thread thread = new Thread(runnable); thread.start();
				 */
			}

			catch (Exception e) {
				logger.error("Txn Status Collection failed for Order Id " + fields.get(FieldType.ORDER_ID.getName()));
				logger.error("Exception in updating Order status", e);

				if (StringUtils.isNotBlank(PropertiesManager.propertiesMap.get("AllowTxnUpdtFailAlert"))
						&& PropertiesManager.propertiesMap.get("AllowTxnUpdtFailAlert").equalsIgnoreCase("Y")) {

					smsCount = smsCount + 1;
					if (smsCount % 10 == 0) {

						StringBuilder smsBody = new StringBuilder();
						smsBody.append("Pay10 Alert !");
						smsBody.append("Transaction Status not updated for Order Id = "
								+ fields.get(FieldType.ORDER_ID.getName()));

						String smsSenderList = PropertiesManager.propertiesMap.get("smsAlertList");

						for (String mobile : smsSenderList.split(",")) {
							try {
								smsSender.sendSMS(mobile, smsBody.toString());
							} catch (IOException ioEx) {
								logger.error("SMS not sent for transaction status update failure when account is null",
										ioEx);
							}
						}

					}

				}

			}

		} catch (Exception exception) {
			String message = "Error while inserting transaction in database";
			logger.error(message, exception);
			throw new SystemException(ErrorType.DATABASE_ERROR, exception, message);
		}
	}

	public void insertStatusEnqTransaction(Fields fields) throws SystemException {
		try {
			MongoDatabase dbIns = null;
			BasicDBObject newFieldsObj = new BasicDBObject();

			String amountString = fields.get(FieldType.AMOUNT.getName());
			String surchargeAmountString = fields.get(FieldType.SURCHARGE_AMOUNT.getName());
			String currencyString = fields.get(FieldType.CURRENCY_CODE.getName());
			String totalAmountString = fields.get(FieldType.TOTAL_AMOUNT.getName());

			String amount = "0";
			if (!StringUtils.isEmpty(amountString) && !StringUtils.isEmpty(currencyString)) {
				amount = Amount.toDecimal(amountString, currencyString);
				newFieldsObj.put(FieldType.AMOUNT.getName(), amount);
			}

			String totalAmount = "0";
			if (!StringUtils.isEmpty(totalAmountString) && !StringUtils.isEmpty(currencyString)) {
				totalAmount = Amount.toDecimal(totalAmountString, currencyString);
				newFieldsObj.put(FieldType.TOTAL_AMOUNT.getName(), totalAmount);
			}

			String surchargeAmount = "0";
			if (!StringUtils.isEmpty(surchargeAmountString) && !StringUtils.isEmpty(currencyString)) {
				surchargeAmount = Amount.toDecimal(surchargeAmountString, currencyString);
				newFieldsObj.put(FieldType.SURCHARGE_AMOUNT.getName(), surchargeAmount);
			}
			String origTxnId = "0";
			String origTxnStr = fields.get(FieldType.ORIG_TXN_ID.getName());
			if (StringUtils.isEmpty(origTxnStr)) {
				String internalOrigTxnStr = fields.get(FieldType.INTERNAL_ORIG_TXN_ID.getName());
				if (StringUtils.isEmpty(internalOrigTxnStr)) {
					newFieldsObj.put(FieldType.ORIG_TXN_ID.getName(), origTxnId);
				}
				if (!StringUtils.isEmpty(internalOrigTxnStr)) {
					newFieldsObj.put(FieldType.ORIG_TXN_ID.getName(), internalOrigTxnStr);
				}
			}

			if (!StringUtils.isEmpty(origTxnStr)) {
				newFieldsObj.put(FieldType.ORIG_TXN_ID.getName(), origTxnStr);
			}

			String origTxnType = fields.get(FieldType.INTERNAL_ORIG_TXN_TYPE.getName());
			if (!StringUtils.isEmpty(origTxnType)) {
				String txnType = fields.get(FieldType.TXNTYPE.getName());
				if ((txnType.equals(TransactionType.REFUND.getName()))
						|| (txnType.equals(TransactionType.REFUNDRECO.getName()))) {
					newFieldsObj.put(FieldType.ORIG_TXNTYPE.getName(), TransactionType.REFUND.getName());
				} else {
					newFieldsObj.put(FieldType.ORIG_TXNTYPE.getName(), origTxnType);
				}

			}
			String pgRefNo = "0";
			String pgRefNum = fields.get(FieldType.PG_REF_NUM.getName());
			if (StringUtils.isEmpty(pgRefNum)) {
				newFieldsObj.put(FieldType.PG_REF_NUM.getName(), pgRefNo);
			}

			if (!StringUtils.isEmpty(pgRefNum)) {
				newFieldsObj.put(FieldType.PG_REF_NUM.getName(), pgRefNum);
			}
			String acctId = "0";
			String acctIdStr = fields.get(FieldType.ACCT_ID.getName());
			if (acctIdStr != null && acctIdStr.length() > 0) {

				newFieldsObj.put(FieldType.ACCT_ID.getName(), acctIdStr);
			}
			if (acctIdStr == null) {
				newFieldsObj.put(FieldType.ACCT_ID.getName(), acctId);
			}
			String acqId = "0";
			String acqIdStr = fields.get(FieldType.ACQ_ID.getName());
			if (acqIdStr != null && acqIdStr.length() > 0) {
				newFieldsObj.put(FieldType.ACQ_ID.getName(), acqIdStr);
			}
			if (acqIdStr == null) {
				newFieldsObj.put(FieldType.ACQ_ID.getName(), acqId);
			}
			String oid = fields.get(FieldType.OID.getName());
			String longOid = "0";
			if (!StringUtils.isEmpty(oid)) {

				newFieldsObj.put(FieldType.OID.getName(), oid);
			}
			if (StringUtils.isEmpty(oid)) {
				newFieldsObj.put(FieldType.OID.getName(), longOid);
			}
			String udf1 = fields.get(FieldType.UDF1.getName());
			if (!StringUtils.isEmpty(udf1)) {
				newFieldsObj.put(FieldType.UDF1.getName(), udf1);
			}
			String udf2 = fields.get(FieldType.UDF2.getName());
			if (!StringUtils.isEmpty(udf2)) {
				newFieldsObj.put(FieldType.UDF2.getName(), udf2);
			}
			String udf3 = fields.get(FieldType.UDF3.getName());
			if (!StringUtils.isEmpty(udf3)) {
				newFieldsObj.put(FieldType.UDF3.getName(), udf3);
			}
			String udf4 = fields.get(FieldType.UDF4.getName());
			if (!StringUtils.isEmpty(udf4)) {
				newFieldsObj.put(FieldType.UDF4.getName(), udf4);
			}
			String udf5 = fields.get(FieldType.UDF5.getName());
			if (!StringUtils.isEmpty(udf5)) {
				newFieldsObj.put(FieldType.UDF5.getName(), udf5);
			}
			String udf6 = fields.get(FieldType.UDF6.getName());
			if (!StringUtils.isEmpty(udf6)) {
				newFieldsObj.put(FieldType.UDF6.getName(), udf6);
			}

			String paymentsRegion = fields.get(FieldType.PAYMENTS_REGION.getName());
			if (!StringUtils.isEmpty(paymentsRegion)) {
				newFieldsObj.put(FieldType.PAYMENTS_REGION.getName(), paymentsRegion);
			}

			String cardHolderType = fields.get(FieldType.CARD_HOLDER_TYPE.getName());
			if (!StringUtils.isEmpty(cardHolderType)) {
				newFieldsObj.put(FieldType.CARD_HOLDER_TYPE.getName(), cardHolderType);
			}

			String requestDate = fields.get(FieldType.REQUEST_DATE.getName());
			if (!StringUtils.isEmpty(requestDate)) {
				newFieldsObj.put(FieldType.REQUEST_DATE.getName(), requestDate);
			}

			String saleAmoiunt = fields.get(FieldType.SALE_AMOUNT.getName());
			if ((!StringUtils.isEmpty(saleAmoiunt) && !StringUtils.isEmpty(currencyString))) {
				saleAmoiunt = Amount.toDecimal(saleAmoiunt, currencyString);
				newFieldsObj.put(FieldType.SALE_AMOUNT.getName(), saleAmoiunt);
			}

			String totalSaleAmoiunt = fields.get(FieldType.SALE_TOTAL_AMOUNT.getName());
			if ((!StringUtils.isEmpty(totalSaleAmoiunt) && !StringUtils.isEmpty(currencyString))) {
				totalSaleAmoiunt = Amount.toDecimal(totalSaleAmoiunt, currencyString);
				newFieldsObj.put(FieldType.SALE_TOTAL_AMOUNT.getName(), totalSaleAmoiunt);
			}

			for (int i = 0; i < fields.size(); i++) {

				Date dNow = new Date();
				String dateNow = DateCreater.formatDateForDb(dNow);

				// Set create date as pg date time received in response
				String pgDateTime = "";
				if (!StringUtils.isEmpty(fields.get(FieldType.PG_DATE_TIME.getName()))) {
					pgDateTime = fields.get(FieldType.PG_DATE_TIME.getName());
				}
				;

				String createDate = "";
				if (!StringUtils.isEmpty(pgDateTime) && pgDateTime.length() == 19) {
					String pgDate = pgDateTime.substring(0, 10);
					String pgTime = "23:59:59";
					createDate = pgDate.replace(":", "-") + " " + pgTime;
				}

				if (!StringUtils.isEmpty(createDate)) {
					dateNow = createDate;
				} else {
					dateNow = DateCreater.formatDateForDb(dNow);
				}
				for (String columnName : aLLDB_Fields) {

					if (columnName.equals(FieldType.CREATE_DATE.getName())) {
						newFieldsObj.put(columnName, dateNow);
					} else if (columnName.equals(FieldType.DATE_INDEX.getName())) {
						newFieldsObj.put(columnName, dateNow.substring(0, 10).replace("-", ""));
					} else if (columnName.equals(FieldType.UPDATE_DATE.getName())) {
						newFieldsObj.put(columnName, dateNow);
					} else if (columnName.equals("_id")) {
						newFieldsObj.put(columnName, fields.get(FieldType.TXN_ID.getName()));
					} else if (columnName.equals(FieldType.INSERTION_DATE.getName())) {
						newFieldsObj.put(columnName, dNow);
					} else if (columnName.equals(FieldType.AMOUNT.getName())) {
						continue;
					} else if (columnName.equals(FieldType.TOTAL_AMOUNT.getName())) {
						continue;
					} else if (columnName.equals(FieldType.SURCHARGE_AMOUNT.getName())) {
						continue;
					} else if (columnName.equals(FieldType.ORIG_TXN_ID.getName())) {
						continue;
					} else if (columnName.equals(FieldType.PG_REF_NUM.getName())) {
						continue;
					} else if (columnName.equals(FieldType.ACCT_ID.getName())) {
						continue;
					} else if (columnName.equals(FieldType.ACQ_ID.getName())) {
						continue;
					} else if (columnName.equals(FieldType.OID.getName())) {
						continue;
					} else if (columnName.equals(FieldType.UDF1.getName())) {
						continue;
					} else if (columnName.equals(FieldType.UDF2.getName())) {
						continue;
					} else if (columnName.equals(FieldType.UDF3.getName())) {
						continue;
					} else if (columnName.equals(FieldType.UDF4.getName())) {
						continue;
					} else if (columnName.equals(FieldType.UDF5.getName())) {
						continue;
					} else if (columnName.equals(FieldType.UDF6.getName())) {
						continue;
					} else if (columnName.equals(FieldType.PAYMENTS_REGION.getName())) {
						continue;
					} else if (columnName.equals(FieldType.CARD_HOLDER_TYPE.getName())) {
						continue;
					} else if (columnName.equals(FieldType.SALE_AMOUNT.getName())) {
						continue;
					} else if (columnName.equals(FieldType.SALE_TOTAL_AMOUNT.getName())) {
						continue;
					} else if (columnName.equals(FieldType.ORIG_TXNTYPE.getName())) {
						continue;
					} else {
						newFieldsObj.put(columnName, fields.get(columnName));
					}
				}
			}

			dbIns = mongoInstance.getDB();
			MongoCollection<Document> collection = dbIns
					.getCollection(propertiesManager.propertiesMap.get(prefix + Constants.COLLECTION_NAME.getValue()));
			Document doc = new Document(newFieldsObj);
			collection.insertOne(doc);

		} catch (Exception exception) {
			String message = "Error while inserting transaction in database";
			logger.error(message, exception);
			throw new SystemException(ErrorType.DATABASE_ERROR, exception, message);
		}
	}

	public void insertSettlementTransaction(Fields fields) throws SystemException {
		try {

			MongoDatabase dbIns = null;
			BasicDBObject newFieldsObj = new BasicDBObject();

			String amountString = fields.get(FieldType.AMOUNT.getName());
			String currencyString = fields.get(FieldType.CURRENCY_CODE.getName());

			String amount = "0";
			if (!StringUtils.isEmpty(amountString) && !StringUtils.isEmpty(currencyString)) {
				amount = Amount.toDecimal(amountString, currencyString);
				newFieldsObj.put(FieldType.AMOUNT.getName(), amount);
			}

			String txnId = "0";
			String transId = fields.get(FieldType.TXN_ID.getName());
			if (StringUtils.isEmpty(transId)) {
				newFieldsObj.put(FieldType.TXN_ID.getName(), txnId);
				newFieldsObj.put(FieldType.ORIG_TXN_ID.getName(), txnId);
			}

			if (!StringUtils.isEmpty(transId)) {
				newFieldsObj.put(FieldType.TXN_ID.getName(), transId);
				newFieldsObj.put(FieldType.ORIG_TXN_ID.getName(), transId);
			}

			String rrn = fields.get(FieldType.RRN.getName());
			if (!StringUtils.isEmpty(rrn)) {
				newFieldsObj.put(FieldType.RRN.getName(), rrn);
			}

			String acquirerType = fields.get(FieldType.NODAL_ACQUIRER.getName());
			if (!StringUtils.isEmpty(acquirerType)) {
				newFieldsObj.put(FieldType.ACQUIRER_TYPE.getName(), acquirerType);
			}

			String txnType = fields.get(FieldType.TXNTYPE.getName());
			if (!StringUtils.isEmpty(txnType)) {
				newFieldsObj.put(FieldType.TXNTYPE.getName(), txnType);
			}
			String requestBy = fields.get(FieldType.REQUESTED_BY.getName());
			if (!StringUtils.isEmpty(requestBy)) {
				newFieldsObj.put(FieldType.REQUESTED_BY.getName(), requestBy);
			}
			String oId = "0";
			String orId = fields.get(FieldType.OID.getName());
			if (StringUtils.isEmpty(orId)) {
				newFieldsObj.put(FieldType.OID.getName(), oId);
			}
			if (!StringUtils.isEmpty(orId)) {
				newFieldsObj.put(FieldType.OID.getName(), orId);
			}
			String customerId = fields.get(FieldType.CUSTOMER_ID.getName());
			if (!StringUtils.isEmpty(customerId)) {
				newFieldsObj.put(FieldType.CUSTOMER_ID.getName(), customerId);
			}
			String appId = fields.get(FieldType.APP_ID.getName());
			if (!StringUtils.isEmpty(appId)) {
				newFieldsObj.put(FieldType.APP_ID.getName(), appId);
			}
			String srcAccountNo = fields.get(FieldType.SRC_ACCOUNT_NO.getName());
			if (!StringUtils.isEmpty(srcAccountNo)) {
				newFieldsObj.put(FieldType.SRC_ACCOUNT_NO.getName(), srcAccountNo);
			}
			String beneCode = fields.get(FieldType.BENEFICIARY_CD.getName());
			if (!StringUtils.isEmpty(beneCode)) {
				newFieldsObj.put(FieldType.BENEFICIARY_CD.getName(), beneCode);
			}
			String paymentType = fields.get(FieldType.PAYMENT_TYPE.getName());
			if (!StringUtils.isEmpty(paymentType)) {
				newFieldsObj.put(FieldType.PAYMENT_TYPE.getName(), paymentType);
			}
			String beneAcc = fields.get(FieldType.BENE_ACCOUNT_NO.getName());
			if (!StringUtils.isEmpty(beneAcc)) {
				newFieldsObj.put(FieldType.BENE_ACCOUNT_NO.getName(), beneAcc);
			}
			String beneName = fields.get(FieldType.BENE_NAME.getName());
			if (!StringUtils.isEmpty(beneName)) {
				newFieldsObj.put(FieldType.BENE_NAME.getName(), beneName);
			}

			String batchId = fields.get(Constants.BATCH_ID.getValue());
			if (!StringUtils.isEmpty(batchId)) {
				newFieldsObj.put(Constants.BATCH_ID.getValue(), batchId);
			}
//			for (int i = 0; i < fields.size(); i++) {

			Date dNow = new Date();
			String dateNow = DateCreater.formatDateForDb(dNow);
			for (String columnName : nodalDB_Fields) {

				if (columnName.equals(FieldType.CREATE_DATE.getName())) {
					newFieldsObj.put(columnName, dateNow);
				} else if (columnName.equals(FieldType.REQUEST_DATE.getName())) {
					newFieldsObj.put(columnName, dateNow);
				} else if (columnName.equals(FieldType.DATE_INDEX.getName())) {
					newFieldsObj.put(columnName, dateNow.substring(0, 10).replace("-", ""));
				} else if (columnName.equals(FieldType.UPDATE_DATE.getName())) {
					newFieldsObj.put(columnName, dateNow);
				} else if (columnName.equals("_id")) {
					newFieldsObj.put(columnName, fields.get(FieldType.TXN_ID.getName()));
				} else if (columnName.equals(FieldType.INSERTION_DATE.getName())) {
					newFieldsObj.put(columnName, dNow);
				} else if (columnName.equals(FieldType.AMOUNT.getName())
						|| (columnName.equals(FieldType.ORIG_TXN_ID.getName()))
						|| (columnName.equals(FieldType.PG_REF_NUM.getName()))
						|| (columnName.equals(FieldType.OID.getName()))
//							|| (columnName.equals(FieldType.RRN.getName()))
						|| (columnName.equals(FieldType.TXNTYPE.getName()))
						|| (columnName.equals(FieldType.CUSTOMER_ID.getName()))
						|| (columnName.equals(FieldType.APP_ID.getName()))
						|| (columnName.equals(FieldType.ACQUIRER_TYPE.getName()))
						|| (columnName.equals(FieldType.REQUESTED_BY.getName()))
						|| (columnName.equals(FieldType.PAYMENT_TYPE.getName()))
						|| (columnName.equals(FieldType.SRC_ACCOUNT_NO.getName()))
						|| (columnName.equals(FieldType.BENEFICIARY_CD.getName()))
						|| (columnName.equals(FieldType.BENE_NAME.getName()))
						|| (columnName.equals(FieldType.BENE_ACCOUNT_NO.getName()))
						|| (columnName.equals(FieldType.CARD_MASK.getName()))
						|| (columnName.equals(FieldType.CUST_NAME.getName()))
						|| (columnName.equals(FieldType.MOP_TYPE.getName()))
						|| (columnName.equals(FieldType.ACCT_ID.getName()))
//							|| (columnName.equals(FieldType.CUST_EMAIL.getName()))
						|| (columnName.equals(FieldType.AUTH_CODE.getName()))
//							|| (columnName.equals(FieldType.INTERNAL_CUST_IP.getName()))
						|| (columnName.equals(FieldType.INTERNAL_CARD_ISSUER_BANK.getName()))
						|| (columnName.equals(FieldType.INTERNAL_CARD_ISSUER_COUNTRY.getName()))
						|| (columnName.equals(FieldType.INTERNAL_USER_EMAIL.getName()))
						|| (columnName.equals(FieldType.INTERNAL_CUST_COUNTRY_NAME.getName()))
//							|| (columnName.equals(FieldType.INTERNAL_REQUEST_FIELDS.getName()))
//							|| (columnName.equals(FieldType.SURCHARGE_FLAG.getName()))
						|| (columnName.equals(FieldType.SURCHARGE_AMOUNT.getName()))
//							|| (columnName.equals(FieldType.ACQ_ID.getName()))
						|| (columnName.equals(FieldType.REFUND_FLAG.getName()))
						|| (columnName.equals(FieldType.REFUND_ORDER_ID.getName()))
						|| (columnName.equals(FieldType.UDF1.getName()))
						|| (columnName.equals(FieldType.UDF2.getName()))
						|| (columnName.equals(FieldType.UDF3.getName()))
						|| (columnName.equals(FieldType.UDF4.getName()))
						|| (columnName.equals(FieldType.UDF5.getName()))
						|| (columnName.equals(FieldType.UDF6.getName()))
//							|| (columnName.equals(FieldType.TOTAL_AMOUNT.getName()))
//							|| (columnName.equals(FieldType.PAYMENTS_REGION.getName()))
						|| (columnName.equals(FieldType.CARD_HOLDER_TYPE.getName()))
//							|| (columnName.equals(FieldType.REQUEST_DATE.getName()))
						|| (columnName.equals(FieldType.ORIG_TXNTYPE.getName()))
//							|| (columnName.equals(FieldType.PG_TDR_SC.getName()))
//							|| (columnName.equals(FieldType.ACQUIRER_TDR_SC.getName()))
//							|| (columnName.equals(FieldType.PG_GST.getName()))
//							|| (columnName.equals(FieldType.ACQUIRER_GST.getName()))
//							|| (columnName.equals(FieldType.CUST_PHONE.getName()))
						|| (columnName.equals(FieldType.CUST_ID.getName()))
						|| (columnName.equals(FieldType.CARD_HOLDER_NAME.getName()))
						|| (columnName.equals(FieldType.INVOICE_ID.getName()))) {
				} else {
					newFieldsObj.put(columnName, fields.get(columnName));
				}
			}
//			}
			dbIns = mongoInstance.getDB();
			MongoCollection<Document> collection = dbIns.getCollection(
					PropertiesManager.propertiesMap.get(prefix + Constants.SETTLEMENT_COLLECTION_NAME.getValue()));
			Document doc = new Document(newFieldsObj);
			if (StringUtils.isBlank(doc.getString(FieldType.INTERNAL_CUST_IP.getName()))) {
				doc.put(FieldType.INTERNAL_CUST_IP.getName(),
						getIPFromInitiateRequest(doc.getString(FieldType.OID.getName())));
			}
			collection.insertOne(doc);
		} catch (Exception exception) {
			String message = "Error while inserting transaction in database";
			logger.error(message, exception);
			throw new SystemException(ErrorType.DATABASE_ERROR, exception, message);
		}
	}

	public void updateSettlementTransaction(Fields fields) throws SystemException {
		try {
			MongoDatabase dbIns = null;
			dbIns = mongoInstance.getDB();
			MongoCollection<Document> collection = dbIns.getCollection(
					PropertiesManager.propertiesMap.get(prefix + Constants.SETTLEMENT_COLLECTION_NAME.getValue()));
			List<BasicDBObject> query = new ArrayList<>();

			query.add(new BasicDBObject(FieldType.ORDER_ID.getName(), fields.get(FieldType.ORDER_ID.getName())));
			query.add(new BasicDBObject(FieldType.STATUS.getName(),
					new BasicDBObject("$ne", NodalStatusType.FAILED.getName())));
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
				logger.error("Unable to update transaction. Found multiple txns with same ORDER ID");
				return;
			}

			if (doc == null) {
				logger.error("Unable to find transaction.");
				return;
			}

			String rrn = fields.get(FieldType.RRN.getName());
			if (!StringUtils.isEmpty(rrn)) {
				doc.replace(FieldType.RRN.getName(), fields.get(FieldType.RRN.getName()));
			}
			doc.replace(FieldType.STATUS.getName(), fields.get(FieldType.STATUS.getName()));
			doc.replace(FieldType.RESPONSE_CODE.getName(), fields.get(FieldType.RESPONSE_CODE.getName()));
			doc.replace(FieldType.RESPONSE_MESSAGE.getName(), fields.get(FieldType.RESPONSE_MESSAGE.getName()));
			doc.replace(FieldType.PG_RESP_CODE.getName(), fields.get(FieldType.PG_RESP_CODE.getName()));
			doc.replace(FieldType.PG_TXN_MESSAGE.getName(), fields.get(FieldType.PG_TXN_MESSAGE.getName()));
			doc.replace(FieldType.UPDATE_DATE.getName(), DateCreater.formatDateForDb(new Date()));
			collection.replaceOne(newFieldsObj, doc);
		} catch (Exception exception) {
			String message = "Error while inserting transaction in database";
			logger.error(message, exception);
			throw new SystemException(ErrorType.DATABASE_ERROR, exception, message);
		}
	}

	private byte[] getInternalRequestFields(Fields fields) {
		String internalReqFields = fields.get(FieldType.INTERNAL_REQUEST_FIELDS.getName());

		if (null != internalReqFields) {
			byte[] allFields = Base64.encodeBase64(internalReqFields.getBytes());
			return allFields;
		} else {
			return null;
		}
	}

	public void insertCustomerInfo(Fields fields) throws SystemException {
		try {
			// Return for invalid transaction
			String responseCode = fields.get(FieldType.RESPONSE_CODE.getName());
			if (responseCode != null && responseCode.equals(ErrorType.VALIDATION_FAILED.getCode())) {
				return;
			}
			// Return for invalid transaction (Hash invalid)
			String invalidHash = fields.get(FieldType.INTERNAL_VALIDATE_HASH_YN.getName());
			if (null != invalidHash && invalidHash.equals("Y")) {
				return;
			}

			boolean custInfoPresent = false;// if not a new order then return
			// TODO.......
			// if fields do not contain any shipping/billing information then
			// return
			if (!fields.get(FieldType.TXNTYPE.getName()).equals(TransactionType.NEWORDER.getName())) {
				return;
			}

			Set<String> fieldsKeySet = fields.keySet();
			for (String fieldName : fieldsKeySet) {
				if (fieldName.startsWith("CUST_") && !(fieldName.equals(FieldType.CUST_NAME.getName()))
						&& !(fieldName.equals(FieldType.CUST_EMAIL.getName()))
						&& !(fieldName.equals(FieldType.CUST_PHONE.getName()))
						&& !fieldName.equals(FieldType.CUST_ID.getName())) {
					custInfoPresent = true;
					break;
				}
			}
			if (custInfoPresent) {
				// execute query
				insertCustomerInfoQuery(fields);
			}
		} catch (Exception exception) {
			// MDC.put(FieldType.INTERNAL_CUSTOM_MDC.getName(),
			// fields.getCustomMDC());
			String message = "Error while inserting transaction in database";
			logger.error(message, exception);
			throw new SystemException(ErrorType.DATABASE_ERROR, exception, message);
		}
	}

	@SuppressWarnings("incomplete-switch")
	public void updateNewOrder(Fields fields) throws SystemException {
		try {
			String internalOrigTxnId = fields.get(FieldType.INTERNAL_ORIG_TXN_ID.getName());
			String status = fields.get(FieldType.STATUS.getName());
			if (StringUtils.isEmpty(internalOrigTxnId) || StringUtils.isEmpty(status)) {
				return;
			}

			TransactionType transactionType = TransactionType.getInstance(fields.get(FieldType.TXNTYPE.getName()));
			switch (transactionType) {
			case SALE:
				updateNewOrderDetails(fields);
				break;
			case AUTHORISE:
				updateNewOrderDetails(fields);
				break;
			case ENROLL:
				updateNewOrderDetails(fields);
				break;
			}
		} catch (Exception exception) {
			// MDC.put(FieldType.INTERNAL_CUSTOM_MDC.getName(),
			// fields.getCustomMDC());
			String message = "Error while inserting transaction in database";
			logger.error(message, exception);
			throw new SystemException(ErrorType.DATABASE_ERROR, exception, message);
		}
	}

	public void insert(Fields fields) throws SystemException {
		try {

			if (fields.get(FieldType.TXNTYPE.getName()).equalsIgnoreCase(TransactionType.SALE.getName())
					&& fields.get(FieldType.STATUS.getName()).equalsIgnoreCase(StatusType.CAPTURED.getName())) {
				routerConfigurationService.clearFailCount(fields);
			}

			insertTransaction(fields);
			insertCustomerInfo(fields);
			// updateNewOrder(fields);
		} catch (MongoException exception) {
			// MDC.put("MongoException", exception.toString());
			logger.error("MongoException", exception);
		}
	}
	
	public void insertPO(Fields fields) throws SystemException {
		try {

			insertTransactionPO(fields);
			
		} catch (MongoException exception) {
			// MDC.put("MongoException", exception.toString());
			logger.error("MongoException", exception);
		}
	}

	public void insertCustomerInfoQuery(Fields fields) throws SystemException {
		// getCustInfoFields
		try {
			BasicDBObject newFieldsObj = new BasicDBObject();
			newFieldsObj.put("_id", fields.get(FieldType.TXN_ID.getName()));

			Collection<String> aLLDB_Fields = SystemProperties.getCustInfoFields();
			for (String columnName : aLLDB_Fields) {
				newFieldsObj.put(columnName, fields.get(columnName));
			}
			MongoDatabase dbIns = mongoInstance.getDB();
			MongoCollection<Document> collection = dbIns.getCollection(Constants.BILLING_COLLECTION.getValue());
			Document doc = new Document(newFieldsObj);
			collection.insertOne(doc);
		} catch (Exception exception) {
			String message = "Error while inserting transaction in database";
			logger.error(message, exception);
			throw new SystemException(ErrorType.DATABASE_ERROR, exception, message);
		}

	}

	/*
	 * public void insertCustomerInfoQuery(Fields fields) throws SystemException {
	 * try { BasicDBObject newFieldsObj = new BasicDBObject(); for (int i = 0; i <
	 * fields.size(); i++) { Collection<String> aLLDB_Fields =
	 * SystemProperties.getDBFields(); for (String columnName : aLLDB_Fields) {
	 * newFieldsObj.put(columnName, fields.get(columnName)); } } MongoDatabase dbIns
	 * = mongoInstance.getDB(); MongoCollection<Document> collection =
	 * dbIns.getCollection(Constants.COLLECTION_NAME.getValue()); Document doc = new
	 * Document(newFieldsObj); collection.insertOne(doc); } catch (Exception
	 * exception) { // MDC.put(FieldType.INTERNAL_CUSTOM_MDC.getName(), //
	 * fields.getCustomMDC()); String message =
	 * "Error while inserting transaction in database"; logger.error(message,
	 * exception); throw new SystemException(ErrorType.DATABASE_ERROR, exception,
	 * message); }
	 * 
	 * }
	 */

	public void insertNewOrder(Fields fields) throws SystemException {
		try {
			String amountString = fields.get(FieldType.AMOUNT.getName());
			String surchargeAmountString = fields.get(FieldType.SURCHARGE_AMOUNT.getName());
			String currencyString = fields.get(FieldType.CURRENCY_CODE.getName());

			String amount = "0";
			if (!StringUtils.isEmpty(amountString) && !StringUtils.isEmpty(currencyString)) {
				amount = Amount.toDecimal(amountString, currencyString);
				fields.put(FieldType.AMOUNT.getName(), amount);
			}

			String surchargeAmount = "0";
			if (!StringUtils.isEmpty(surchargeAmountString) && !StringUtils.isEmpty(currencyString)) {
				surchargeAmount = Amount.toDecimal(surchargeAmountString, currencyString);
				fields.put(FieldType.SURCHARGE_AMOUNT.getName(), surchargeAmount);
			}
			long origTxnId = 0;
			String origTxnStr = fields.get(FieldType.ORIG_TXN_ID.getName());
			if (StringUtils.isEmpty(origTxnStr)) {
				origTxnStr = fields.get(FieldType.INTERNAL_ORIG_TXN_ID.getName());
				fields.put(FieldType.ORIG_TXN_ID.getName(), origTxnStr);
			}
			if (!StringUtils.isEmpty(origTxnStr)) {
				origTxnId = Long.parseLong(origTxnStr);
				// fields.put(FieldType.ORIG_TXN_ID.getName(),origTxnId);
			}
			long acctId = 0;
			String acctIdStr = fields.get(FieldType.ACCT_ID.getName());
			if (acctIdStr != null && acctIdStr.length() > 0) {
				acctId = Long.parseLong(acctIdStr);
				// fields.put(FieldType.ACCT_ID.getName(),acctId);
			}
			String oid = fields.get(FieldType.OID.getName());
			long longOid = 0;
			if (!StringUtils.isEmpty(oid)) {
				longOid = Long.parseLong(oid);
			}
			BasicDBObject newFieldsObj = new BasicDBObject();
			for (int i = 0; i < fields.size(); i++) {
				Collection<String> aLLDB_Fields = SystemProperties.getAllDBRequestFields();
				for (String columnName : aLLDB_Fields) {
					newFieldsObj.put(columnName, fields.get(columnName));
				}
			}
			MongoDatabase dbIns = mongoInstance.getDB();
			MongoCollection<Document> collection = dbIns
					.getCollection(propertiesManager.propertiesMap.get(prefix + Constants.COLLECTION_NAME.getValue()));
			Document doc = new Document(newFieldsObj);
			collection.insertOne(doc);
		} catch (Exception exception) {
			// MDC.put(FieldType.INTERNAL_CUSTOM_MDC.getName(),
			// fields.getCustomMDC());
			String message = "Error while inserting transaction in database";
			logger.error(message, exception);
			throw new SystemException(ErrorType.DATABASE_ERROR, exception, message);
		}
	}

	private List<Fields> getPreviousSaleCapturedForPgRefNum(String pgRefNum) throws SystemException {
		try {
			List<Fields> fieldsList = new ArrayList<Fields>();

			List<BasicDBObject> settledList = new ArrayList<BasicDBObject>();
			settledList.add(new BasicDBObject(FieldType.PG_REF_NUM.getName(), pgRefNum));
			settledList.add(new BasicDBObject(FieldType.TXNTYPE.getName(), TransactionType.SALE.getName()));
			settledList.add(new BasicDBObject(FieldType.STATUS.getName(), StatusType.CAPTURED.getName()));
			BasicDBObject saleQuery = new BasicDBObject("$and", settledList);

			MongoDatabase dbIns = mongoInstance.getDB();
			MongoCollection<Document> collection = dbIns
					.getCollection(propertiesManager.propertiesMap.get(prefix + Constants.COLLECTION_NAME.getValue()));
			MongoCursor<Document> cursor = collection.find(saleQuery).iterator();
			try {
				while (cursor.hasNext()) {
					Document documentObj = (Document) cursor.next();
					if (null != documentObj) {
						Fields preFields = new Fields();
						for (int j = 0; j < documentObj.size(); j++) {
							for (String columnName : systemProperties.getDBFields()) {
								if (documentObj.get(columnName) != null) {
									preFields.put(columnName, documentObj.get(columnName).toString());
								}
							}
						}
						preFields.logAllFields(
								"Received SALE&CAPTURED transaction details for transaction with PG_REF_NUM: "
										+ pgRefNum);
						fieldsList.add(preFields);
					}
				}
				logger.info("Got Previous Data with count : " + fieldsList.size());
			} finally {
				cursor.close();
			}

			return fieldsList;
		} catch (Exception exception) {
			String message = "Error while reading list of transactions based on OID from database";
			logger.error(message, exception);
			throw new SystemException(ErrorType.DATABASE_ERROR, exception, message);
		}
	}

	private String getCreateDateForPgRefNum(String pgRefNum) throws SystemException {
		try {
			List<Fields> fieldsList = new ArrayList<Fields>();

			List<BasicDBObject> settledList = new ArrayList<BasicDBObject>();
			settledList.add(new BasicDBObject(FieldType.PG_REF_NUM.getName(), pgRefNum));
			settledList.add(new BasicDBObject(FieldType.TXNTYPE.getName(), TransactionType.SALE.getName()));
			settledList.add(new BasicDBObject(FieldType.STATUS.getName(), StatusType.CAPTURED.getName()));
			BasicDBObject saleQuery = new BasicDBObject("$and", settledList);

			MongoDatabase dbIns = mongoInstance.getDB();
			MongoCollection<Document> collection = dbIns
					.getCollection(propertiesManager.propertiesMap.get(prefix + Constants.COLLECTION_NAME.getValue()));
			MongoCursor<Document> cursor = collection.find(saleQuery).iterator();
			try {
				while (cursor.hasNext()) {
					StringBuilder responseData = new StringBuilder();
					Document documentObj = (Document) cursor.next();
					if (StringUtils.isNotBlank(documentObj.getString(FieldType.CREATE_DATE.getName()))) {
						responseData.append(documentObj.getString(FieldType.CREATE_DATE.getName()));
						responseData.append(",");
					}
					if (StringUtils.isNotBlank(documentObj.getString(FieldType.ACQ_ID.getName()))) {
						responseData.append(documentObj.getString(FieldType.ACQ_ID.getName()));
					}
					return responseData.toString();
				}
			} finally {
				cursor.close();
			}

			return null;
		} catch (Exception exception) {
			String message = "Error while reading create date from database";
			logger.error(message, exception);
			throw new SystemException(ErrorType.DATABASE_ERROR, exception, message);
		}
	}

	private String getCreateDateForOrderId(String orderId) throws SystemException {
		try {
			List<Fields> fieldsList = new ArrayList<Fields>();

			List<BasicDBObject> settledList = new ArrayList<BasicDBObject>();
			settledList.add(new BasicDBObject(FieldType.ORDER_ID.getName(), orderId));
			settledList.add(new BasicDBObject(FieldType.TXNTYPE.getName(), TransactionType.SALE.getName()));
			settledList.add(new BasicDBObject(FieldType.STATUS.getName(), StatusType.CAPTURED.getName()));
			BasicDBObject saleQuery = new BasicDBObject("$and", settledList);

			MongoDatabase dbIns = mongoInstance.getDB();
			MongoCollection<Document> collection = dbIns
					.getCollection(propertiesManager.propertiesMap.get(prefix + Constants.COLLECTION_NAME.getValue()));
			MongoCursor<Document> cursor = collection.find(saleQuery).iterator();
			try {
				while (cursor.hasNext()) {
					StringBuilder responseData = new StringBuilder();
					Document documentObj = (Document) cursor.next();
					if (StringUtils.isNotBlank(documentObj.getString(FieldType.CREATE_DATE.getName()))) {
						responseData.append(documentObj.getString(FieldType.CREATE_DATE.getName()));
						responseData.append(",");
					}
					if (StringUtils.isNotBlank(documentObj.getString(FieldType.ACQ_ID.getName()))) {
						responseData.append(documentObj.getString(FieldType.ACQ_ID.getName()));
					}
					return responseData.toString();
				}
			} finally {
				cursor.close();
			}

			return null;
		} catch (Exception exception) {
			String message = "Error while reading create date from database";
			logger.error(message, exception);
			throw new SystemException(ErrorType.DATABASE_ERROR, exception, message);
		}
	}

	private String getRefundCreateDateForPgRefNum(String pgRefNum) throws SystemException {
		try {
			List<Fields> fieldsList = new ArrayList<Fields>();

			List<BasicDBObject> settledList = new ArrayList<BasicDBObject>();
			settledList.add(new BasicDBObject(FieldType.PG_REF_NUM.getName(), pgRefNum));
			settledList.add(new BasicDBObject(FieldType.TXNTYPE.getName(), TransactionType.REFUND.getName()));
			BasicDBObject saleQuery = new BasicDBObject("$and", settledList);

			MongoDatabase dbIns = mongoInstance.getDB();
			MongoCollection<Document> collection = dbIns
					.getCollection(propertiesManager.propertiesMap.get(prefix + Constants.COLLECTION_NAME.getValue()));
			MongoCursor<Document> cursor = collection.find(saleQuery).iterator();
			try {
				while (cursor.hasNext()) {
					StringBuilder responseData = new StringBuilder();
					Document documentObj = (Document) cursor.next();
					if (StringUtils.isNotBlank(documentObj.getString(FieldType.CREATE_DATE.getName()))) {
						responseData.append(documentObj.getString(FieldType.CREATE_DATE.getName()));
						responseData.append(",");
					}
					if (StringUtils.isNotBlank(documentObj.getString(FieldType.ACQ_ID.getName()))) {
						responseData.append(documentObj.getString(FieldType.ACQ_ID.getName()));
					}
					return responseData.toString();
				}
				logger.info("Got Previous Data with count : " + fieldsList.size());
			} finally {
				cursor.close();
			}

			return null;
		} catch (Exception exception) {
			String message = "Error while reading create date from database";
			logger.error(message, exception);
			throw new SystemException(ErrorType.DATABASE_ERROR, exception, message);
		}
	}

	public List<Fields> getPreviousSaleCapturedForOrderId(String orderId) throws SystemException {
		try {
			logger.info("Inside getPreviousSaleCapturedForPgRefNum (PG_REF_NUM): " + orderId);
			List<Fields> fieldsList = new ArrayList<Fields>();

			List<BasicDBObject> settledList = new ArrayList<BasicDBObject>();
			settledList.add(new BasicDBObject(FieldType.ORDER_ID.getName(), orderId));
			settledList.add(new BasicDBObject(FieldType.TXNTYPE.getName(), TransactionType.SALE.getName()));
			settledList.add(new BasicDBObject(FieldType.STATUS.getName(), StatusType.CAPTURED.getName()));
			BasicDBObject saleQuery = new BasicDBObject("$and", settledList);

			/*
			 * List<BasicDBObject> transList = new ArrayList<BasicDBObject>();
			 * transList.add(saleQuery);
			 * 
			 * BasicDBObject transQuery = new BasicDBObject("$or", transList);
			 */
			logger.info("Query Created");
			MongoDatabase dbIns = mongoInstance.getDB();
			logger.info("Below MongoDatabase dbIns = mongoInstance.getDB()");
			MongoCollection<Document> collection = dbIns
					.getCollection(propertiesManager.propertiesMap.get(prefix + Constants.COLLECTION_NAME.getValue()));
			MongoCursor<Document> cursor = collection.find(saleQuery).iterator();
			logger.info("Query Executed");
			try {
				while (cursor.hasNext()) {
					Document documentObj = (Document) cursor.next();
					if (null != documentObj) {
						Fields preFields = new Fields();
						for (int j = 0; j < documentObj.size(); j++) {
							for (String columnName : systemProperties.getDBFields()) {
								if (documentObj.get(columnName) != null) {
									preFields.put(columnName, documentObj.get(columnName).toString());
								}
							}
						}
						preFields.logAllFields(
								"Received SALE&CAPTURED transaction details for transaction with PG_REF_NUM: "
										+ orderId);
						fieldsList.add(preFields);
					}
				}
				logger.info("Got Previous Data with count : " + fieldsList.size());
			} finally {
				cursor.close();
			}

			return fieldsList;
		} catch (Exception exception) {
			String message = "Error while reading list of transactions based on OID from database";
			logger.error(message, exception);
			throw new SystemException(ErrorType.DATABASE_ERROR, exception, message);
		}
	}

	// Get Previous Sale Date
	public String getSaleDate(String pgRefNum) throws SystemException {
		List<Fields> fieldsList = new ArrayList<Fields>();
		fieldsList = getPreviousSaleCapturedForPgRefNum(pgRefNum);
		if (fieldsList.size() > 0) {
			return fieldsList.get(0).get(FieldType.CREATE_DATE.getName());
		}
		return null;
	}

// Get Create Date and ACQ ID for Axis NB Status Enquiry and Atom Status Enquiry
	public String getCaptureDate(String pgRefNum) throws SystemException {

		String responseData = getCreateDateForPgRefNum(pgRefNum);
		return responseData;
	}

// Get Create Date and ACQ ID for Axis NB Status Enquiry and Atom Status Enquiry
	public String getCaptureDateByOrderId(String orderId) throws SystemException {

		String responseData = getCreateDateForOrderId(orderId);
		return responseData;
	}

// Get Create Date and ACQ ID for Axis NB Status Enquiry and Atom Status Enquiry for Refund
	public String getRefundCaptureDate(String pgRefNum) throws SystemException {

		String responseData = getRefundCreateDateForPgRefNum(pgRefNum);
		return responseData;
	}

	public String getSaleDateByOrderId(String orderId) throws SystemException {
		List<Fields> fieldsList = new ArrayList<Fields>();
		fieldsList = getPreviousSaleCapturedForOrderId(orderId);
		if (fieldsList.size() > 0) {
			return fieldsList.get(0).get(FieldType.CREATE_DATE.getName());
		}
		return null;
	}

	public Fields getFieldsForSettlement(String oId, String status) throws SystemException {
		try {
			return createAllSelectForSettlement(oId, status);
		} catch (Exception exception) {
			String message = "Error while searching transaction in database";
			logger.error(message, exception);
			throw new SystemException(ErrorType.DATABASE_ERROR, exception, message);
		}
	}

	public Fields createAllSelectForSettlement(String oId, String status) {

		Fields fields = new Fields();
		List<BasicDBObject> dbObjList = new ArrayList<BasicDBObject>();
		dbObjList.add(new BasicDBObject(FieldType.TXN_ID.getName(), oId));
		dbObjList.add(new BasicDBObject(FieldType.STATUS.getName(), status));

		BasicDBObject andQuery = new BasicDBObject("$and", dbObjList);
		MongoDatabase dbIns = mongoInstance.getDB();
		MongoCollection<Document> coll = dbIns.getCollection(
				propertiesManager.propertiesMap.get(prefix + Constants.SETTLEMENT_COLLECTION_NAME.getValue()));

		FindIterable<Document> cursor = coll.find(andQuery);

		// MongoCursor<Document> cursor2 = cursor.iterator();

		if (cursor.iterator().hasNext()) {
			Document documentObj = cursor.iterator().next();

			if (null != documentObj) {
				for (int j = 0; j < documentObj.size(); j++) {
					for (String columnName : aLLDB_Fields) {
						if (documentObj.get(columnName) != null) {
							fields.put(columnName, documentObj.get(columnName).toString());
						} else {

						}

					}
				}
			}
			fields.logAllFields("Previous fields");
		}
		cursor.iterator().close();

		return fields;

	}

	public String getIpFromSTB(String oid) {
		String ipAddress = null;
		try {

			logger.info("Inside getIpFromSTB (OID): " + oid);
			List<BasicDBObject> condList = new ArrayList<BasicDBObject>();
			condList.add(new BasicDBObject(FieldType.OID.getName(), oid));
			condList.add(new BasicDBObject(FieldType.TXNTYPE.getName(), TransactionType.SALE.getName()));
			condList.add(new BasicDBObject(FieldType.STATUS.getName(), StatusType.SENT_TO_BANK.getName()));
			BasicDBObject saleQuery = new BasicDBObject("$and", condList);
			MongoDatabase dbIns = mongoInstance.getDB();
			MongoCollection<Document> collection = dbIns.getCollection(
					propertiesManager.propertiesMap.get(prefix + Constants.TRANSACTION_STATUS_COLLECTION.getValue()));
			MongoCursor<Document> cursor = collection.find(saleQuery).iterator();
			try {
				while (cursor.hasNext()) {
					Document doc = cursor.next();
					if (StringUtils.isNotBlank(String.valueOf(doc.get(FieldType.INTERNAL_CUST_IP.getName())))) {
						ipAddress = String.valueOf(doc.get(FieldType.INTERNAL_CUST_IP.getName()));
						break;
					}
				}
			} finally {
				cursor.close();
			}
			return ipAddress;
		} catch (Exception exception) {
			String message = "Error while getIpFromSTB based on OID from database";
			logger.error(message, exception);
			return ipAddress;
		}
	}

	public String getIPFromInitiateRequest(String oid) {
		String ipAddress = null;
		try {

			if (StringUtils.isBlank(oid)) {
				logger.info("getIPFromInitiateRequest:: skipped as oid is blank.");
				return null;
			}
			logger.info("Inside getIPFromInitiateRequest (OID): " + oid);
			List<BasicDBObject> condList = new ArrayList<BasicDBObject>();
			condList.add(new BasicDBObject(FieldType.OID.getName(), oid));
			condList.add(new BasicDBObject(FieldType.TXNTYPE.getName(), TransactionType.NEWORDER.getName()));
			condList.add(new BasicDBObject(FieldType.STATUS.getName(), StatusType.PENDING.getName()));
			BasicDBObject saleQuery = new BasicDBObject("$and", condList);
			MongoDatabase dbIns = mongoInstance.getDB();
			MongoCollection<Document> collection = dbIns
					.getCollection(propertiesManager.propertiesMap.get(prefix + Constants.COLLECTION_NAME.getValue()));
			MongoCursor<Document> cursor = collection.find(saleQuery).iterator();
			try {
				while (cursor.hasNext()) {
					Document doc = cursor.next();
					if (StringUtils.isNotBlank(String.valueOf(doc.get(FieldType.INTERNAL_CUST_IP.getName())))) {
						ipAddress = String.valueOf(doc.get(FieldType.INTERNAL_CUST_IP.getName()));
						break;
					}
				}
			} finally {
				cursor.close();
			}
			return ipAddress;
		} catch (Exception exception) {
			String message = "Error while getIPFromInitiateRequest based on OID from database";
			logger.error(message, exception);
			return ipAddress;
		}
	}

	public void updateStatusCollPO(Fields fields, Document document) {

		try {

			MongoDatabase dbIns = mongoInstance.getDB();

								MongoCollection<Document> excepColl = dbIns.getCollection(PropertiesManager.propertiesMap
							.get(prefix +"PO_TransactionStatus"));

				excepColl.insertOne(document);
							// REFUND

					}

		catch (Exception e) {
			logger.error("Exception in adding txn to transaction status", e);
		}

	}
	
	public void updateStatusColl(Fields fields, Document document) {

		try {

			logger.info("Transaction Status :: " + document.get(FieldType.STATUS.getName()).toString());
			logger.info("Router Configuration Id :: " + fields.get(FieldType.ROUTER_CONFIGURATION_ID.getName()));

			logger.info("updateStatusColl,, Document ::: " + document.toString());
			/*
			 * //added by sonu for smart routing merchant payment release if
			 * (fields.get(FieldType.ROUTER_CONFIGURATION_ID.getName()) != null &&
			 * (document.get(FieldType.STATUS.getName()).toString().equalsIgnoreCase(
			 * "Declined") ||
			 * document.get(FieldType.STATUS.getName()).toString().equalsIgnoreCase(
			 * "FAILED") ||
			 * document.get(FieldType.STATUS.getName()).toString().equalsIgnoreCase(
			 * "Cancelled") || document.get(FieldType.STATUS.getName()).toString().
			 * equalsIgnoreCase("Failed at Acquirer") ||
			 * document.get(FieldType.STATUS.getName()).toString().equalsIgnoreCase(
			 * "Invalid") || document.get(FieldType.STATUS.getName()).toString().
			 * equalsIgnoreCase("User Inactive"))) {
			 * 
			 * //!(document.get(FieldType.STATUS.getName()).toString().equalsIgnoreCase(
			 * "Captured"))) {
			 * 
			 * //&& (document.get(FieldType.STATUS.getName()).toString().equalsIgnoreCase(
			 * "Declined") // ||
			 * document.get(FieldType.STATUS.getName()).toString().equalsIgnoreCase("FAILED"
			 * ))) {
			 * 
			 * logger.info("Step 1"); // TODO Call Method to release merchant freeze amount.
			 * logger.info("Request received to release merchant freeze amount.");
			 * 
			 * routerConfigurationDao.freezeRouterConfigurationMaxAmountById(
			 * Double.valueOf(document.get(FieldType.TOTAL_AMOUNT.getName()).toString()),
			 * Long.valueOf(fields.get(FieldType.ROUTER_CONFIGURATION_ID.getName()))); }
			 */

			// added by sonu for smart routing merchant payment release
			if (fields.get(FieldType.ROUTER_CONFIGURATION_ID.getName()) != null
					&& (document.get(FieldType.STATUS.getName()).toString().equalsIgnoreCase("Captured"))) {

				logger.info("Request received to freeze merchant amount through RealTime.");

				routerConfigurationDao.freezeRouterConfigurationMaxAmountById(
						Double.valueOf(document.get(FieldType.TOTAL_AMOUNT.getName()).toString()),
						Long.valueOf(fields.get(FieldType.ROUTER_CONFIGURATION_ID.getName())));
			}

			MongoDatabase dbIns = mongoInstance.getDB();

			String orderId = document.getString(FieldType.ORDER_ID.getName());
			String oid = document.get(FieldType.OID.getName()).toString();
			String origTxnType = fields.get(FieldType.ORIG_TXNTYPE.getName());
			String pgRefNum = fields.get(FieldType.PG_REF_NUM.getName());
			String txnType = fields.get(FieldType.TXNTYPE.getName());

			logger.info("updateStatusColl :: orderId = {}, oid = {}, origTxnType = {}, pgRefNum = {}, txnType = {}",
					orderId, oid, origTxnType, pgRefNum, txnType);
			
			if (origTxnType == null) {
				origTxnType = document.getString(FieldType.ORIG_TXNTYPE.getName());
			}
			if (txnType == null) {
				txnType = document.getString(FieldType.TXNTYPE.getName());
			}

			if (txnType.equalsIgnoreCase(TransactionType.INVALID.getName())
					|| txnType.equalsIgnoreCase(TransactionType.NEWORDER.getName())
					|| txnType.equalsIgnoreCase(TransactionType.RECO.getName())
					|| txnType.equalsIgnoreCase(TransactionType.ENROLL.getName())) {
				txnType = TransactionType.SALE.getName();
			}

			if (txnType.equalsIgnoreCase(TransactionType.REFUND.getName())
					|| txnType.equalsIgnoreCase(TransactionType.REFUNDRECO.getName())) {
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
					doc.put(FieldType.ORDER_ID.getName(), orderId);
					doc.put(FieldType.OID.getName(), oid);
					doc.put(FieldType.ORIG_TXNTYPE.getName(), origTxnType);

					MongoCollection<Document> excepColl = dbIns.getCollection(PropertiesManager.propertiesMap
							.get(prefix + Constants.TRANSACTION_STATUS_EXCEP_COLLECTION.getValue()));

					excepColl.insertOne(doc);
				} else {

					MongoCollection<Document> coll = dbIns.getCollection(propertiesManager.propertiesMap
							.get(prefix + Constants.TRANSACTION_STATUS_COLLECTION.getValue()));

					List<BasicDBObject> dbObjList = new ArrayList<BasicDBObject>();
					dbObjList.add(new BasicDBObject(FieldType.ORDER_ID.getName(), orderId));
					dbObjList.add(new BasicDBObject(FieldType.OID.getName(), oid));
					// dbObjList.add(new BasicDBObject(FieldType.PG_REF_NUM.getName(),
					// document.getString(FieldType.PG_REF_NUM.getName())));
					// ***** *** ** End by RR change duplicate txn issue fix

					dbObjList.add(new BasicDBObject(FieldType.ORIG_TXNTYPE.getName(), origTxnType));

					BasicDBObject andQuery = new BasicDBObject("$and", dbObjList);
					// adding RR
					logger.info("Txn status collection query while update : {} ", andQuery.toString());
					FindIterable<Document> cursor = coll.find(andQuery);

					if (cursor.iterator().hasNext()) {
						logger.info("Txn status collection query while update [Found record updating]: {} ", orderId);
						String transactionStatusFields = PropertiesManager.propertiesMap.get("TransactionStatusFields");
						String transactionStatusFieldsArr[] = transactionStatusFields.split(",");

						MongoCollection<Document> txnStatusColl = dbIns.getCollection(propertiesManager.propertiesMap
								.get(prefix + Constants.TRANSACTION_STATUS_COLLECTION.getValue()));

						BasicDBObject searchQuery = andQuery;
						BasicDBObject updateFields = new BasicDBObject();

						String status = document.get(FieldType.STATUS.getName()).toString();
						String statusALias = resolveStatus(status);

						// Added to avoid exception in report
						if (StringUtils.isBlank(statusALias)) {
							logger.info("Alias Status not resolved for order Id = " + orderId);
							statusALias = "Cancelled";
						}

						logger.info("FieldsDao : logs : " + document.toJson());
						
						for (String key : transactionStatusFieldsArr) {

							/* if (status.equalsIgnoreCase(StatusType.SETTLED_SETTLE.getName())) { */

								if ((key.equalsIgnoreCase(FieldType.DATE_INDEX.getName()))
										|| (key.equalsIgnoreCase(FieldType.CREATE_DATE.getName()))
										|| (key.equalsIgnoreCase(FieldType.UPDATE_DATE.getName()))
										|| (key.equalsIgnoreCase(FieldType.INSERTION_DATE.getName()))) {
									continue;
								}

								/*
								 * if ((key.equalsIgnoreCase(FieldType.SETTLEMENT_DATE.getName()))) {
								 * updateFields.put(key, document.get(FieldType.CREATE_DATE.getName()));
								 * continue; }
								 * 
								 * if ((key.equalsIgnoreCase(FieldType.SETTLEMENT_FLAG.getName()))) {
								 * updateFields.put(key, "Y"); continue; }
								 * 
								 * if ((key.equalsIgnoreCase(FieldType.SETTLEMENT_DATE_INDEX.getName()))) {
								 * updateFields.put(key, document.get(FieldType.DATE_INDEX.getName()));
								 * continue; }
								 */

							/*}*/

							if (document.get(key) != null) {
								updateFields.put(key, document.get(key).toString());
							} else {
								updateFields.put(key, document.get(key));
							}

						}
						if(StringUtils.isNotBlank((String)document.get(FieldType.PSPNAME.getName()))) {
							updateFields.put(FieldType.PSPNAME.getName(), document.get(FieldType.PSPNAME.getName()));

						}
						if(StringUtils.isNotBlank((String)document.get(FieldType.BROWSER_DEVICE.getName()))) {
							updateFields.put(FieldType.BROWSER_DEVICE.getName(), document.get(FieldType.BROWSER_DEVICE.getName()));

						}

						updateFields.put(FieldType.ALIAS_STATUS.getName(), statusALias);
						updateFields.put(FieldType.ORIG_TXNTYPE.getName(), origTxnType);
						logger.info("Txn status collection updating order ID: {} , with other fields : {} ", orderId,
								updateFields.toString());
						txnStatusColl.updateOne(searchQuery, new BasicDBObject("$set", updateFields));
						logger.info("Updated TXN Status collection update");
					} else {

						MongoCollection<Document> txnStatusColl = dbIns.getCollection(propertiesManager.propertiesMap
								.get(prefix + Constants.TRANSACTION_STATUS_COLLECTION.getValue()));

						String status = fields.get(FieldType.STATUS.getName());
						String statusALias = resolveStatus(status);

						// Added to avoid exception in report
						if (StringUtils.isBlank(statusALias)) {
							logger.info("Alias Status not resolved for order Id = " + orderId);
							statusALias = "Cancelled";
						}

						document.put(FieldType.ALIAS_STATUS.getName(), statusALias);
						document.put(FieldType.ORIG_TXNTYPE.getName(), origTxnType);
						document.put(FieldType.WEBHOOK_FAILED_COUNT.getName(), "0");
						document.put(FieldType.WEBHOOK_POST_FLAG.getName(), "N");
						logger.info("Txn status collection inserting order ID: {} , with other fields : {} ", orderId,
								document.toString());
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

					MongoCollection<Document> excepColl = dbIns.getCollection(PropertiesManager.propertiesMap
							.get(prefix + Constants.TRANSACTION_STATUS_EXCEP_COLLECTION.getValue()));

					excepColl.insertOne(doc);
				} else {

					logger.info("Refund document = {} ",document);
					logger.info("Refund REFUND_ORDER_ID document = {} ",document.get(FieldType.REFUND_ORDER_ID.getName()));
					logger.info("Refund REFUND_ORDER_ID fields == {} ",fields.get(FieldType.REFUND_ORDER_ID.getName()));
					
					MongoCollection<Document> coll = dbIns.getCollection(propertiesManager.propertiesMap
							.get(prefix + Constants.TRANSACTION_STATUS_COLLECTION.getValue()));

					List<BasicDBObject> dbObjList = new ArrayList<BasicDBObject>();
					//dbObjList.add(new BasicDBObject(FieldType.PG_REF_NUM.getName(), pgRefNum));
					dbObjList.add(new BasicDBObject(FieldType.REFUND_ORDER_ID.getName(), fields.get(FieldType.REFUND_ORDER_ID.getName())));
					dbObjList.add(new BasicDBObject(FieldType.OID.getName(), oid));
					dbObjList.add(new BasicDBObject(FieldType.ORIG_TXNTYPE.getName(), origTxnType));

					BasicDBObject andQuery = new BasicDBObject("$and", dbObjList);
					logger.info("Refund Query = {} ",andQuery);
					FindIterable<Document> cursor = coll.find(andQuery);

					if (cursor.iterator().hasNext()) {

						String transactionStatusFields = PropertiesManager.propertiesMap.get("TransactionStatusFields");
						String transactionStatusFieldsArr[] = transactionStatusFields.split(",");

						MongoCollection<Document> txnStatusColl = dbIns.getCollection(propertiesManager.propertiesMap
								.get(prefix + Constants.TRANSACTION_STATUS_COLLECTION.getValue()));

						BasicDBObject searchQuery = andQuery;
						BasicDBObject updateFields = new BasicDBObject();

						for (String key : transactionStatusFieldsArr) {

							if (document.get(key) != null) {
								updateFields.put(key, document.get(key).toString());
							} else {
								updateFields.put(key, document.get(key));
							}

						}

						String status = fields.get(FieldType.STATUS.getName());
						String statusALias = resolveStatus(status);
						updateFields.put(FieldType.ALIAS_STATUS.getName(), statusALias);
						updateFields.put(FieldType.ORIG_TXNTYPE.getName(), origTxnType);
						txnStatusColl.updateOne(searchQuery, new BasicDBObject("$set", updateFields));

					} else {

						MongoCollection<Document> txnStatusColl = dbIns.getCollection(propertiesManager.propertiesMap
								.get(prefix + Constants.TRANSACTION_STATUS_COLLECTION.getValue()));

						String status = fields.get(FieldType.STATUS.getName());
						String statusALias = resolveStatus(status);
						document.put(FieldType.ALIAS_STATUS.getName(), statusALias);
						document.put(FieldType.ORIG_TXNTYPE.getName(), origTxnType);
						document.put(FieldType.REFUND_ORDER_ID.getName(), fields.get(FieldType.REFUND_ORDER_ID.getName()));
						txnStatusColl.insertOne(document);

					}

				}
			}

		}

		catch (Exception e) {
			logger.error("Exception in adding txn to transaction status", e);
		}

	}

	// added by sonu for fetching InternalFieldRequest from transactionStatus
	// collection while inserting send to bank request
	public String getInternalFieldRequest(String orderId, String oids, String origTxnTypes) {

		logger.info("Get InternalFieldRequest For Send to bank Request for orderId : " + orderId);
		MongoDatabase dbIns = mongoInstance.getDB();
		MongoCollection<Document> coll = dbIns.getCollection(
				propertiesManager.propertiesMap.get(prefix + Constants.TRANSACTION_STATUS_COLLECTION.getValue()));

		List<BasicDBObject> dbObjList = new ArrayList<BasicDBObject>();
		dbObjList.add(new BasicDBObject(FieldType.ORDER_ID.getName(), orderId));
		dbObjList.add(new BasicDBObject(FieldType.OID.getName(), oids));
		dbObjList.add(new BasicDBObject(FieldType.ORIG_TXNTYPE.getName(), origTxnTypes));

		BasicDBObject andQuery = new BasicDBObject("$and", dbObjList);
		logger.info("query for fetching InternalFieldRequest : {} ", andQuery.toString());
		FindIterable<Document> findIterable = coll.find(andQuery);
		MongoCursor<Document> cursor = findIterable.iterator();
		if (cursor.hasNext()) {
			Document document1 = cursor.next();
			return document1.getString("INTERNAL_REQUEST_FIELDS");
		}
		return null;
	}

	public String resolveStatus(String status) {

		if (StringUtils.isBlank(status)) {
			return status;
		} else {
			if (status.equals(StatusType.CAPTURED.getName())) {
				return "Captured";

			} else if (status.equals(StatusType.SETTLED_SETTLE.getName())) {
				return "Settled";

			} else if (status.equals(StatusType.PENDING.getName()) || status.equals(StatusType.SENT_TO_BANK.getName())
					|| status.equals(StatusType.ENROLLED.getName()) || status.equals(StatusType.PROCESSING.getName())) {
				return "Pending";

			} else if (status.equals(StatusType.BROWSER_CLOSED.getName())
					|| status.equals(StatusType.CANCELLED.getName())
					|| status.equals(StatusType.USER_INACTIVE.getName())) {
				return "Cancelled";

			} else if (status.equals(StatusType.INVALID.getName()) || status.equals(StatusType.DUPLICATE.getName())) {
				return "Invalid";

			} else {
				return "Failed";
			}

		}
	}

	public String getPreviousForOID(String oid) throws SystemException {

		String internalRequestFields = null;
		try {
			logger.info("Inside getPreviousForOID (OID): " + oid);

			List<BasicDBObject> condList = new ArrayList<BasicDBObject>();
			condList.add(new BasicDBObject(FieldType.OID.getName(), oid));
			condList.add(new BasicDBObject(FieldType.TXNTYPE.getName(), TransactionType.NEWORDER.getName()));
			condList.add(new BasicDBObject(FieldType.STATUS.getName(), StatusType.PENDING.getName()));
			BasicDBObject saleQuery = new BasicDBObject("$and", condList);

			MongoDatabase dbIns = mongoInstance.getDB();
			MongoCollection<Document> collection = dbIns
					.getCollection(propertiesManager.propertiesMap.get(prefix + Constants.COLLECTION_NAME.getValue()));
			MongoCursor<Document> cursor = collection.find(saleQuery).iterator();
			try {
				while (cursor.hasNext()) {

					Document doc = cursor.next();

					if (StringUtils.isNotBlank(String.valueOf(doc.get(FieldType.INTERNAL_REQUEST_FIELDS.getName())))) {
						internalRequestFields = String.valueOf(doc.get(FieldType.INTERNAL_REQUEST_FIELDS.getName()));
						break;
					}
				}
			} finally {
				cursor.close();
			}

			return internalRequestFields;
		} catch (Exception exception) {
			String message = "Error while previous based on OID from database";
			logger.error(message, exception);
			return internalRequestFields;
		}

	}

	public String getPreviousForOIDSTB(String oid) throws SystemException {

		String internalRequestFields = null;
		try {
			logger.info("Inside getPreviousForOID (OID): " + oid);

			List<BasicDBObject> condList = new ArrayList<BasicDBObject>();
			condList.add(new BasicDBObject(FieldType.OID.getName(), oid));
			condList.add(new BasicDBObject(FieldType.TXNTYPE.getName(), TransactionType.SALE.getName()));
			condList.add(new BasicDBObject(FieldType.STATUS.getName(), StatusType.SENT_TO_BANK.getName()));
			BasicDBObject saleQuery = new BasicDBObject("$and", condList);

			MongoDatabase dbIns = mongoInstance.getDB();
			MongoCollection<Document> collection = dbIns
					.getCollection(propertiesManager.propertiesMap.get(prefix + Constants.COLLECTION_NAME.getValue()));
			MongoCursor<Document> cursor = collection.find(saleQuery).iterator();
			try {
				while (cursor.hasNext()) {

					Document doc = cursor.next();

					if (StringUtils.isNotBlank(String.valueOf(doc.get(FieldType.INTERNAL_REQUEST_FIELDS.getName())))) {
						internalRequestFields = String.valueOf(doc.get(FieldType.INTERNAL_REQUEST_FIELDS.getName()));
						break;
					}
				}
			} finally {
				cursor.close();
			}

			return internalRequestFields;
		} catch (Exception exception) {
			String message = "Error while previous based on OID from database";
			logger.error(message, exception);
			return internalRequestFields;
		}

	}
	
	
	public boolean findTransactionforPayuRefund(String RefundOrderId) {
		boolean ret_Val = true;
		MongoCursor<Document> cursor = null;
		List<String> statusLst = new ArrayList<String>();
		statusLst.add(StatusType.CAPTURED.getName());
		try {
			logger.info("Inside findTransactionForPgRefNoStatusWise (RefundOrderId): " + RefundOrderId);
			List<BasicDBObject> condList = new ArrayList<BasicDBObject>();
			condList.add(new BasicDBObject(FieldType.REFUND_ORDER_ID.getName(), RefundOrderId));
			condList.add(new BasicDBObject(FieldType.TXNTYPE.getName(), TransactionType.REFUND.getName()));
			condList.add(new BasicDBObject(FieldType.STATUS.getName(), new BasicDBObject("$in", statusLst)));
			BasicDBObject saleQuery = new BasicDBObject("$and", condList);
			logger.info("Inside findTransactionForPgRefNoStatusWise RefundOrderId={}, Query={} ",RefundOrderId, saleQuery);
			MongoDatabase dbIns = mongoInstance.getDB();
			MongoCollection<Document> collection = dbIns
					.getCollection(propertiesManager.propertiesMap.get(prefix + Constants.COLLECTION_NAME.getValue()));
			cursor = collection.find(saleQuery).iterator();
			if (cursor.hasNext()) {
				ret_Val = false;
			}
		} catch (Exception exception) {
			String message = "Error while previous based on Pg Ref Num from database";
			logger.error(message, exception);
		} finally {
			cursor.close();
		}
		return ret_Val;
	}

	public boolean findTransactionForPgRefNoStatusWise(String pgRefNum) {
		boolean ret_Val = true;
		MongoCursor<Document> cursor = null;
		List<String> statusLst = new ArrayList<String>();
		statusLst.add(StatusType.SETTLED_SETTLE.getName());
		statusLst.add(StatusType.CAPTURED.getName());
		try {
			logger.info("Inside findTransactionForPgRefNoStatusWise (PG_REF_NUM): " + pgRefNum);
			List<BasicDBObject> condList = new ArrayList<BasicDBObject>();
			condList.add(new BasicDBObject(FieldType.PG_REF_NUM.getName(), pgRefNum));
			condList.add(new BasicDBObject(FieldType.TXNTYPE.getName(), TransactionType.SALE.getName()));
			condList.add(new BasicDBObject(FieldType.STATUS.getName(), new BasicDBObject("$in", statusLst)));
			BasicDBObject saleQuery = new BasicDBObject("$and", condList);
			logger.info("Inside findTransactionForPgRefNoStatusWise PG_REF_NUM={}, Query={} ",pgRefNum, saleQuery);
			MongoDatabase dbIns = mongoInstance.getDB();
			MongoCollection<Document> collection = dbIns
					.getCollection(propertiesManager.propertiesMap.get(prefix + Constants.COLLECTION_NAME.getValue()));
			cursor = collection.find(saleQuery).iterator();
			if (cursor.hasNext()) {
				ret_Val = false;
			}
		} catch (Exception exception) {
			String message = "Error while previous based on Pg Ref Num from database";
			logger.error(message, exception);
		} finally {
			cursor.close();
		}
		return ret_Val;
	}
	
	public boolean findTransactionForPgRefNoStatusWiseForSBIUPI(String pgRefNum) {
		boolean ret_Val = true;
		MongoCursor<Document> cursor = null;
		List<String> statusLst = new ArrayList<String>();
		statusLst.add(StatusType.SETTLED_SETTLE.getName());
		statusLst.add(StatusType.CAPTURED.getName());
		statusLst.add(StatusType.FAILED.getName());
		try {
			logger.info("Inside findTransactionForPgRefNoStatusWise (PG_REF_NUM): " + pgRefNum);
			List<BasicDBObject> condList = new ArrayList<BasicDBObject>();
			condList.add(new BasicDBObject(FieldType.PG_REF_NUM.getName(), pgRefNum));
			condList.add(new BasicDBObject(FieldType.TXNTYPE.getName(), TransactionType.SALE.getName()));
			condList.add(new BasicDBObject(FieldType.STATUS.getName(), new BasicDBObject("$in", statusLst)));
			BasicDBObject saleQuery = new BasicDBObject("$and", condList);
			logger.info("Inside findTransactionForPgRefNoStatusWise PG_REF_NUM={}, Query={} ",pgRefNum, saleQuery);
			MongoDatabase dbIns = mongoInstance.getDB();
			MongoCollection<Document> collection = dbIns
					.getCollection(propertiesManager.propertiesMap.get(prefix + Constants.COLLECTION_NAME.getValue()));
			cursor = collection.find(saleQuery).iterator();
			if (cursor.hasNext()) {
				ret_Val = false;
			}
		} catch (Exception exception) {
			String message = "Error while previous based on Pg Ref Num from database";
			logger.error(message, exception);
		} finally {
			cursor.close();
		}
		return ret_Val;
	}

	public Fields getPreviousForPgRefNumForSbiDelayedReponse(String pgRefNum) throws SystemException {

		Fields preFields = new Fields();
		try {

			logger.info("Inside getPreviousForPgRefNumForSbiDelayedReponse (PG_REF_NUM) ::: " + pgRefNum);
//			boolean checkFlag = true;
//			if ((findTransactionForPgRefNoStatusWise(pgRefNum, Arrays.asList(StatusType.SETTLED.getName(),StatusType.CAPTURED.getName())))){
//							
//					//|| (findTransactionForPgRefNoStatusWise(pgRefNum, StatusType.CAPTURED.getName()))){
//				checkFlag = false;
//			}

			// Done By chetan nagaria for change in settlement process to mark transaction as RNS
			if (findTransactionForPgRefNoStatusWise(pgRefNum)) {

				List<BasicDBObject> condList = new ArrayList<BasicDBObject>();
				condList.add(new BasicDBObject(FieldType.PG_REF_NUM.getName(), pgRefNum));
				condList.add(new BasicDBObject(FieldType.TXNTYPE.getName(), TransactionType.SALE.getName()));
				condList.add(new BasicDBObject(FieldType.STATUS.getName(), StatusType.SENT_TO_BANK.getName()));
				BasicDBObject saleQuery = new BasicDBObject("$and", condList);
				MongoDatabase dbIns = mongoInstance.getDB();
				MongoCollection<Document> collection = dbIns.getCollection(
						propertiesManager.propertiesMap.get(prefix + Constants.COLLECTION_NAME.getValue()));
				MongoCursor<Document> cursor = collection.find(saleQuery).iterator();
				try {
					if (cursor.hasNext()) {
						Document documentObj = (Document) cursor.next();
						if (null != documentObj) {
							for (int j = 0; j < documentObj.size(); j++) {
								for (String columnName : systemProperties.getDBFields()) {
									if (documentObj.get(columnName) != null) {
										preFields.put(columnName, documentObj.get(columnName).toString());
									}
								}
							}
						}
					}
				} finally {
					cursor.close();
				}

				// Correct Amount to decimal format

				if (StringUtils.isNotBlank(preFields.get(FieldType.AMOUNT.getName()))) {
					preFields.put(FieldType.AMOUNT.getName(),
							Amount.formatAmount(preFields.get(FieldType.AMOUNT.getName()),
									preFields.get(FieldType.CURRENCY_CODE.getName())));
				}

				if (StringUtils.isNotBlank(preFields.get(FieldType.TOTAL_AMOUNT.getName()))) {
					preFields.put(FieldType.TOTAL_AMOUNT.getName(),
							Amount.formatAmount(preFields.get(FieldType.TOTAL_AMOUNT.getName()),
									preFields.get(FieldType.CURRENCY_CODE.getName())));
				}

				preFields.put(FieldType.HASH.getName(), Hasher.getHash(preFields));

			}
			return preFields;
		} catch (Exception exception) {
			String message = "Error while previous based on Pg Ref Num from database";
			logger.error(message, exception);
			return preFields;
		}

	}

	public Fields getPreviousForPgRefNum(String pgRefNum) throws SystemException {
		Fields preFields = new Fields();
		try {
			logger.info("Inside getPreviousForPgRefNum (PG_REF_NUM): " + pgRefNum);
			List<BasicDBObject> condList = new ArrayList<BasicDBObject>();
			condList.add(new BasicDBObject(FieldType.PG_REF_NUM.getName(), pgRefNum));
			condList.add(new BasicDBObject(FieldType.TXNTYPE.getName(), TransactionType.SALE.getName()));
			condList.add(new BasicDBObject(FieldType.STATUS.getName(), StatusType.SENT_TO_BANK.getName()));
			BasicDBObject saleQuery = new BasicDBObject("$and", condList);
			MongoDatabase dbIns = mongoInstance.getDB();
			MongoCollection<Document> collection = dbIns
					.getCollection(propertiesManager.propertiesMap.get(prefix + Constants.COLLECTION_NAME.getValue()));
			MongoCursor<Document> cursor = collection.find(saleQuery).iterator();
			try {
				if (cursor.hasNext()) {
					Document documentObj = (Document) cursor.next();
					if (null != documentObj) {
						for (int j = 0; j < documentObj.size(); j++) {
							for (String columnName : systemProperties.getDBFields()) {
								if (documentObj.get(columnName) != null) {
									preFields.put(columnName, documentObj.get(columnName).toString());
								}
							}
						}
					}
				}
			} finally {
				cursor.close();
			}

			// Correct Amount to decimal format

			if (StringUtils.isNotBlank(preFields.get(FieldType.AMOUNT.getName()))) {
				preFields.put(FieldType.AMOUNT.getName(), Amount.formatAmount(preFields.get(FieldType.AMOUNT.getName()),
						preFields.get(FieldType.CURRENCY_CODE.getName())));
			}

			if (StringUtils.isNotBlank(preFields.get(FieldType.TOTAL_AMOUNT.getName()))) {
				preFields.put(FieldType.TOTAL_AMOUNT.getName(),
						Amount.formatAmount(preFields.get(FieldType.TOTAL_AMOUNT.getName()),
								preFields.get(FieldType.CURRENCY_CODE.getName())));
			}

			preFields.put(FieldType.HASH.getName(), Hasher.getHash(preFields));

			return preFields;
		} catch (Exception exception) {
			String message = "Error while previous based on Pg Ref Num from database";
			logger.error(message, exception);
			return preFields;
		}

	}

	//for fetching last status entry
	public Fields getPreviousStatusForPgRefNum(String pgRefNum) throws SystemException {
		Fields preFields = new Fields();
		try {
			logger.info("Inside getPreviousForPgRefNum (PG_REF_NUM): " + pgRefNum);
			List<BasicDBObject> condList = new ArrayList<BasicDBObject>();
			condList.add(new BasicDBObject(FieldType.PG_REF_NUM.getName(), pgRefNum));
			condList.add(new BasicDBObject(FieldType.TXNTYPE.getName(), TransactionType.SALE.getName()));
			//condList.add(new BasicDBObject(FieldType.STATUS.getName(), StatusType.SENT_TO_BANK.getName()));
			BasicDBObject saleQuery = new BasicDBObject("$and", condList);
			MongoDatabase dbIns = mongoInstance.getDB();
			MongoCollection<Document> collection = dbIns
					.getCollection(propertiesManager.propertiesMap.get(prefix + Constants.TRANSACTION_STATUS_COLLECTION.getValue()));
			MongoCursor<Document> cursor = collection.find(saleQuery).iterator();
			try {
				if (cursor.hasNext()) {
					Document documentObj = (Document) cursor.next();
					if (null != documentObj) {
						for (int j = 0; j < documentObj.size(); j++) {
							for (String columnName : systemProperties.getDBFields()) {
								if (documentObj.get(columnName) != null) {
									preFields.put(columnName, documentObj.get(columnName).toString());
								}
							}
						}
					}
				}
			} finally {
				cursor.close();
			}

			// Correct Amount to decimal format

			if (StringUtils.isNotBlank(preFields.get(FieldType.AMOUNT.getName()))) {
				preFields.put(FieldType.AMOUNT.getName(), Amount.formatAmount(preFields.get(FieldType.AMOUNT.getName()),
						preFields.get(FieldType.CURRENCY_CODE.getName())));
			}

			if (StringUtils.isNotBlank(preFields.get(FieldType.TOTAL_AMOUNT.getName()))) {
				preFields.put(FieldType.TOTAL_AMOUNT.getName(),
						Amount.formatAmount(preFields.get(FieldType.TOTAL_AMOUNT.getName()),
								preFields.get(FieldType.CURRENCY_CODE.getName())));
			}

			//preFields.put(FieldType.HASH.getName(), Hasher.getHash(preFields));

			return preFields;
		} catch (Exception exception) {
			String message = "Error while previous based on Pg Ref Num from database";
			logger.error(message, exception);
			return preFields;
		}

	}

	public boolean getCaptuedForPgRef(String pgRefNum) throws SystemException {

		boolean isCaptured = false;
		try {
			logger.info("Inside getPreviousForPgRef (pgRefNum): " + pgRefNum);

			List<BasicDBObject> condList = new ArrayList<BasicDBObject>();
			condList.add(new BasicDBObject(FieldType.PG_REF_NUM.getName(), pgRefNum));
			condList.add(new BasicDBObject(FieldType.TXNTYPE.getName(), TransactionType.SALE.getName()));
			condList.add(new BasicDBObject(FieldType.STATUS.getName(), StatusType.CAPTURED.getName()));
			BasicDBObject saleQuery = new BasicDBObject("$and", condList);

			logger.info("Inside getPreviousForPgRef (pgRefNum): QUERY " + saleQuery);

			MongoDatabase dbIns = mongoInstance.getDB();
			MongoCollection<Document> collection = dbIns
					.getCollection(propertiesManager.propertiesMap.get(prefix + Constants.COLLECTION_NAME.getValue()));

			long count = collection.count(saleQuery);
			if (count > 0) {
				isCaptured = true;
			}

			return isCaptured;
		} catch (Exception exception) {
			String message = "Error while checking captued via pg ref num from database";
			logger.error(message, exception);
			return isCaptured;
		}

	}

	// SBI UPI check duplicate call back response
	public boolean getDuplicateCallBackForPgRef(String pgRefNum, String acq_id) throws SystemException {

		boolean isDuplicate = false;
		try {
			logger.info("Inside getDuplicateCallBackForPgRef (pgRefNum): " + pgRefNum);

			List<BasicDBObject> condList = new ArrayList<BasicDBObject>();
			condList.add(new BasicDBObject(FieldType.PG_REF_NUM.getName(), pgRefNum));
			condList.add(new BasicDBObject(FieldType.ACQ_ID.getName(), acq_id));
			condList.add(new BasicDBObject(FieldType.TXNTYPE.getName(), TransactionType.SALE.getName()));
			condList.add(new BasicDBObject(FieldType.STATUS.getName(),
					new BasicDBObject("$ne", StatusType.SENT_TO_BANK.getName())));
			BasicDBObject saleQuery = new BasicDBObject("$and", condList);

			logger.info("Inside getDuplicateCallBackForPgRef (pgRefNum): QUERY " + saleQuery);

			MongoDatabase dbIns = mongoInstance.getDB();
			MongoCollection<Document> collection = dbIns
					.getCollection(propertiesManager.propertiesMap.get(prefix + Constants.COLLECTION_NAME.getValue()));

			long count = collection.count(saleQuery);
			if (count > 0) {
				isDuplicate = true;
			}

			return isDuplicate;
		} catch (Exception exception) {
			String message = "Error while checking getDuplicateCallBackForPgRef via pg ref num from database";
			logger.error(message, exception);
			return isDuplicate;
		}

	}

	// added by sonu for cashfree new flow
	public Fields getCaptuedDataPgRefNum(String pgRefNum) throws SystemException {

		Fields preFields = new Fields();

		try {
			logger.info("Inside getCaptuedDataPgRefNum (PG_REF_NUM): " + pgRefNum);

			List<BasicDBObject> condList = new ArrayList<BasicDBObject>();
			condList.add(new BasicDBObject(FieldType.PG_REF_NUM.getName(), pgRefNum));
			condList.add(new BasicDBObject(FieldType.TXNTYPE.getName(), TransactionType.SALE.getName()));
			condList.add(new BasicDBObject(FieldType.STATUS.getName(), StatusType.CAPTURED.getName()));
			BasicDBObject saleQuery = new BasicDBObject("$and", condList);

			MongoDatabase dbIns = mongoInstance.getDB();
			MongoCollection<Document> collection = dbIns
					.getCollection(propertiesManager.propertiesMap.get(prefix + Constants.COLLECTION_NAME.getValue()));
			MongoCursor<Document> cursor = collection.find(saleQuery).iterator();
			try {
				if (cursor.hasNext()) {
					Document documentObj = (Document) cursor.next();
					if (null != documentObj) {
						for (int j = 0; j < documentObj.size(); j++) {
							for (String columnName : systemProperties.getDBFields()) {
								if (documentObj.get(columnName) != null) {
									preFields.put(columnName, documentObj.get(columnName).toString());
								}
							}
						}
					}
				}
			} finally {
				cursor.close();
			}

			// Correct Amount to decimal format

			if (StringUtils.isNotBlank(preFields.get(FieldType.AMOUNT.getName()))) {
				preFields.put(FieldType.AMOUNT.getName(), Amount.formatAmount(preFields.get(FieldType.AMOUNT.getName()),
						preFields.get(FieldType.CURRENCY_CODE.getName())));
			}

			if (StringUtils.isNotBlank(preFields.get(FieldType.TOTAL_AMOUNT.getName()))) {
				preFields.put(FieldType.TOTAL_AMOUNT.getName(),
						Amount.formatAmount(preFields.get(FieldType.TOTAL_AMOUNT.getName()),
								preFields.get(FieldType.CURRENCY_CODE.getName())));
			}

			preFields.put(FieldType.HASH.getName(), Hasher.getHash(preFields));
			return preFields;
		} catch (Exception exception) {
			String message = "Error while previous based on Pg Ref Num from database";
			logger.error(message, exception);
			return preFields;
		}

	}

	public Fields getByPgRefNumAndStatus(String pgRefNum,String status) {

		Fields preFields = new Fields();

		try {
			logger.info("Inside getByPgRefNumAndStatus (PG_REF_NUM): " + pgRefNum+" and status: "+status);

			List<BasicDBObject> condList = new ArrayList<BasicDBObject>();
			condList.add(new BasicDBObject(FieldType.PG_REF_NUM.getName(), pgRefNum));
			condList.add(new BasicDBObject(FieldType.TXNTYPE.getName(), TransactionType.SALE.getName()));
			condList.add(new BasicDBObject(FieldType.STATUS.getName(), status));
			BasicDBObject saleQuery = new BasicDBObject("$and", condList);

			MongoDatabase dbIns = mongoInstance.getDB();
			MongoCollection<Document> collection = dbIns
					.getCollection(propertiesManager.propertiesMap.get(prefix + Constants.COLLECTION_NAME.getValue()));
			MongoCursor<Document> cursor = collection.find(saleQuery).iterator();
			try {
				if (cursor.hasNext()) {
					Document documentObj = (Document) cursor.next();
					if (null != documentObj) {
						for (int j = 0; j < documentObj.size(); j++) {
							for (String columnName : systemProperties.getDBFields()) {
								if (documentObj.get(columnName) != null) {
									preFields.put(columnName, documentObj.get(columnName).toString());
								}
							}
						}
					}
				}
			} finally {
				cursor.close();
			}

			// Correct Amount to decimal format

			if (StringUtils.isNotBlank(preFields.get(FieldType.AMOUNT.getName()))) {
				preFields.put(FieldType.AMOUNT.getName(), Amount.formatAmount(preFields.get(FieldType.AMOUNT.getName()),
						preFields.get(FieldType.CURRENCY_CODE.getName())));
			}

			if (StringUtils.isNotBlank(preFields.get(FieldType.TOTAL_AMOUNT.getName()))) {
				preFields.put(FieldType.TOTAL_AMOUNT.getName(),
						Amount.formatAmount(preFields.get(FieldType.TOTAL_AMOUNT.getName()),
								preFields.get(FieldType.CURRENCY_CODE.getName())));
			}

			preFields.put(FieldType.HASH.getName(), Hasher.getHash(preFields));
			return preFields;
		} catch (Exception exception) {
			String message = "Error while previous based on Pg Ref Num from database";
			logger.error(message, exception);
			return preFields;
		}

	}

	// Get Create Date and ACQ ID for Axis NB Status Enquiry and Atom Status Enquiry
	public Map<String, String> getCaptureTxn(String orderId) throws SystemException {

		Map<String, String> responseData = getSaletxnForOrderId(orderId);
		return responseData;
	}

	public Map<String, String> getCaptureTxn(String orderId,String payId) throws SystemException {

		Map<String, String> responseData = getSaletxnForOrderIdAndPayId(orderId,payId);
		return responseData;
	}

	private Map<String, String> getSaletxnForOrderId(String orderId) throws SystemException {
		try {

			Map<String, String> responseMap = new HashMap<String, String>();
			List<BasicDBObject> settledList = new ArrayList<BasicDBObject>();
			settledList.add(new BasicDBObject(FieldType.ORDER_ID.getName(), orderId));
			settledList.add(new BasicDBObject(FieldType.TXNTYPE.getName(), TransactionType.SALE.getName()));
			settledList.add(new BasicDBObject(FieldType.STATUS.getName(), StatusType.CAPTURED.getName()));
			BasicDBObject saleQuery = new BasicDBObject("$and", settledList);

			MongoDatabase dbIns = mongoInstance.getDB();
			MongoCollection<Document> collection = dbIns
					.getCollection(propertiesManager.propertiesMap.get(prefix + Constants.COLLECTION_NAME.getValue()));
			MongoCursor<Document> cursor = collection.find(saleQuery).iterator();
			try {
				while (cursor.hasNext()) {
					Document documentObj = (Document) cursor.next();
					if (StringUtils.isNotBlank(documentObj.getString(FieldType.CREATE_DATE.getName()))) {
						responseMap.put(FieldType.CREATE_DATE.getName(),
								documentObj.getString(FieldType.CREATE_DATE.getName()));
					}
					if (StringUtils.isNotBlank(documentObj.getString(FieldType.TOTAL_AMOUNT.getName()))) {
						responseMap.put(FieldType.TOTAL_AMOUNT.getName(),
								documentObj.getString(FieldType.TOTAL_AMOUNT.getName()));
					}
					if (StringUtils.isNotBlank(documentObj.getString(FieldType.PG_REF_NUM.getName()))) {
						responseMap.put(FieldType.PG_REF_NUM.getName(),
								documentObj.getString(FieldType.PG_REF_NUM.getName()));
					}
					return responseMap;
				}
			} finally {
				cursor.close();
			}

			return null;
		} catch (Exception exception) {
			String message = "Error while reading create date from database";
			logger.error(message, exception);
			throw new SystemException(ErrorType.DATABASE_ERROR, exception, message);
		}
	}


	private Map<String, String> getSaletxnForOrderIdAndPayId(String orderId,String payId) throws SystemException {
		try {

			Map<String, String> responseMap = new HashMap<String, String>();
			List<BasicDBObject> settledList = new ArrayList<BasicDBObject>();
			settledList.add(new BasicDBObject(FieldType.ORDER_ID.getName(), orderId));
			settledList.add(new BasicDBObject(FieldType.PAY_ID.getName(), payId));
			settledList.add(new BasicDBObject(FieldType.TXNTYPE.getName(), TransactionType.SALE.getName()));
			settledList.add(new BasicDBObject(FieldType.STATUS.getName(), StatusType.CAPTURED.getName()));
			BasicDBObject saleQuery = new BasicDBObject("$and", settledList);

			MongoDatabase dbIns = mongoInstance.getDB();
			MongoCollection<Document> collection = dbIns
					.getCollection(propertiesManager.propertiesMap.get(prefix + Constants.COLLECTION_NAME.getValue()));
			MongoCursor<Document> cursor = collection.find(saleQuery).iterator();
			try {
				while (cursor.hasNext()) {
					Document documentObj = (Document) cursor.next();
					if (StringUtils.isNotBlank(documentObj.getString(FieldType.CREATE_DATE.getName()))) {
						responseMap.put(FieldType.CREATE_DATE.getName(),
								documentObj.getString(FieldType.CREATE_DATE.getName()));
					}
					if (StringUtils.isNotBlank(documentObj.getString(FieldType.TOTAL_AMOUNT.getName()))) {
						responseMap.put(FieldType.TOTAL_AMOUNT.getName(),
								documentObj.getString(FieldType.TOTAL_AMOUNT.getName()));
					}
					if (StringUtils.isNotBlank(documentObj.getString(FieldType.PG_REF_NUM.getName()))) {
						responseMap.put(FieldType.PG_REF_NUM.getName(),
								documentObj.getString(FieldType.PG_REF_NUM.getName()));
					}
					return responseMap;
				}
			} finally {
				cursor.close();
			}

			return null;
		} catch (Exception exception) {
			String message = "Error while reading create date from database";
			logger.error(message, exception);
			throw new SystemException(ErrorType.DATABASE_ERROR, exception, message);
		}
	}

	public Fields getFieldsForSaleTxn(String orderId, String payId, String amount, String txnType, String status,
			String pgRefNum) throws SystemException {
		try {
			return createAllSelectForSale(orderId, payId, amount, txnType, status, pgRefNum);
		} catch (Exception exception) {
			String message = "No such transaction found";
			logger.error(message, exception);
			throw new SystemException(ErrorType.NO_SUCH_TRANSACTION, exception, message);
		}
	}

	public Fields getFieldsForLyraUpi(String pgRefNum, String txnType, String status) throws SystemException {
		try {
			return createAllForLyraUpiSale(pgRefNum, txnType, status);
		} catch (Exception exception) {
			String message = "Error while inserting transaction in database";
			logger.error(message, exception);
			throw new SystemException(ErrorType.DATABASE_ERROR, exception, message);
		}
	}

	public Fields createAllSelectForSale(String orderId, String payId, String amount, String txnType, String status,
			String pgRefNum) {

		Fields fields = new Fields();
		List<BasicDBObject> dbObjList = new ArrayList<BasicDBObject>();
		dbObjList.add(new BasicDBObject(FieldType.ORDER_ID.getName(), orderId));
		dbObjList.add(new BasicDBObject(FieldType.PAY_ID.getName(), payId));
		dbObjList.add(new BasicDBObject(FieldType.AMOUNT.getName(), amount));
		dbObjList.add(new BasicDBObject(FieldType.TXNTYPE.getName(), txnType));
		dbObjList.add(new BasicDBObject(FieldType.STATUS.getName(), status));
		dbObjList.add(new BasicDBObject(FieldType.PG_REF_NUM.getName(), pgRefNum));

		BasicDBObject andQuery = new BasicDBObject("$and", dbObjList);
		MongoDatabase dbIns = mongoInstance.getDB();
		MongoCollection<Document> coll = dbIns
				.getCollection(propertiesManager.propertiesMap.get(prefix + Constants.COLLECTION_NAME.getValue()));

		FindIterable<Document> cursor = coll.find(andQuery);

		if (cursor.iterator().hasNext()) {
			Document documentObj = cursor.iterator().next();

			if (null != documentObj) {
				for (int j = 0; j < documentObj.size(); j++) {
					for (String columnName : aLLDB_Fields) {
						if (documentObj.get(columnName) != null) {
							fields.put(columnName, documentObj.get(columnName).toString());
						} else {

						}

					}
				}
			}
			fields.logAllFields("Previous fields");
		}
		cursor.iterator().close();

		return fields;

	}

	@SuppressWarnings("static-access")
	private Fields createAllForLyraUpiSale(String pgRefNum, String txnType, String status) {
		Fields fields = new Fields();
		List<BasicDBObject> dbObjList = new ArrayList<BasicDBObject>();
		dbObjList.add(new BasicDBObject(FieldType.PG_REF_NUM.getName(), pgRefNum));
		dbObjList.add(new BasicDBObject(FieldType.TXNTYPE.getName(), txnType));
		dbObjList.add(new BasicDBObject(FieldType.STATUS.getName(), status));

		List<BasicDBObject> qrObjList = new ArrayList<BasicDBObject>();
		qrObjList.add(new BasicDBObject(FieldType.PG_REF_NUM.getName(), pgRefNum));
		qrObjList.add(new BasicDBObject(FieldType.STATUS.getName(), StatusType.PENDING.getName()));
		qrObjList.add(new BasicDBObject(FieldType.TXNTYPE.getName(), TransactionType.NEWORDER.getName()));

		BasicDBObject vpaQuery = new BasicDBObject("$and", dbObjList);
		BasicDBObject qrQuery = new BasicDBObject("$and", qrObjList);
		BasicDBObject finalQuery = new BasicDBObject();

		List<BasicDBObject> finalList = new ArrayList<BasicDBObject>();
		finalList.add(vpaQuery);
		finalList.add(qrQuery);
		finalQuery.put("$or", finalList);

		MongoDatabase dbIns = mongoInstance.getDB();
		MongoCollection<Document> coll = dbIns
				.getCollection(propertiesManager.propertiesMap.get(prefix + Constants.COLLECTION_NAME.getValue()));
		MongoCursor<Document> cursor = coll.find(finalQuery).iterator();
		if (cursor.hasNext()) {
			Document documentObj = cursor.next();
			if (null != documentObj) {
				for (int j = 0; j < documentObj.size(); j++) {
					for (String columnName : aLLDB_Fields) {
						if (documentObj.get(columnName) != null) {
							fields.put(columnName, documentObj.get(columnName).toString());
						} else {

						}
					}
				}
			}
			fields.logAllFields("Previous fields");
		}
		cursor.close();
		return fields;
	}

	@SuppressWarnings("static-access")
	public void sendCallback(Fields fields) {

		User user = userDao.findPayId(fields.get(FieldType.PAY_ID.getName()));

		if (StringUtils.isNotBlank(user.getCallBackUrl())) {
			if (user.isCallBackFlag()) {
				JsonObject json = new JsonObject();
				try {

					// Check to see if a transaction is already added in DB with
					// Same
					// RRN and status
					// as Captured and Skip that entry when sending callback
					if (StringUtils.isNotBlank(fields.get(FieldType.TXNTYPE.getName()))
							&& StringUtils.isNotBlank(fields.get(FieldType.STATUS.getName()))) {

						if (fields.get(FieldType.TXNTYPE.getName()).equalsIgnoreCase(TransactionType.SALE.getName())
								&& fields.get(FieldType.STATUS.getName())
										.equalsIgnoreCase(StatusType.CAPTURED.getName())) {

							List<BasicDBObject> saleConList = new ArrayList<BasicDBObject>();
							saleConList.add(
									new BasicDBObject(FieldType.TXNTYPE.getName(), TransactionType.SALE.getName()));
							saleConList
									.add(new BasicDBObject(FieldType.STATUS.getName(), StatusType.CAPTURED.getName()));
							saleConList.add(
									new BasicDBObject(FieldType.RRN.getName(), fields.get(FieldType.RRN.getName())));
							saleConList.add(new BasicDBObject(FieldType.PAY_ID.getName(),
									fields.get(FieldType.PAY_ID.getName())));

							BasicDBObject duplicateRRNQuery = new BasicDBObject("$and", saleConList);

							MongoDatabase dbIns = null;
							dbIns = mongoInstance.getDB();
							MongoCollection<Document> collection = dbIns.getCollection(propertiesManager.propertiesMap
									.get("MONGO_DB_" + Constants.COLLECTION_NAME.getValue()));

							long count = collection.count(duplicateRRNQuery);

							if (count > 1) {
								logger.info("Skipping duplicate capture entry for transaction with Same RRN = "
										+ fields.get(FieldType.RRN.getName()) + " and Order ID = "
										+ fields.get(FieldType.ORDER_ID.getName()));
								return;
							}

						}

					}
					Date date = new Date();
					SimpleDateFormat df = new SimpleDateFormat("dd-MM-yyyy hh:mm:ss");
					String strDate = df.format(date);

					Fields newFields = new Fields();
					for (int i = 0; i < fields.size(); i++) {
						Collection<String> callbackFields = SystemProperties.getCallbackfields();
						for (String key : callbackFields) {
							if (StringUtils.isNotBlank(fields.get(key))) {
								newFields.put(key, fields.get(key));
							}
						}
					}
					newFields.put(FieldType.RESPONSE_DATE_TIME.getName(), strDate);
					String hash = Hasher.getHash(newFields);
					newFields.put("HASH", hash);

					List<String> fieldTypeList = new ArrayList<String>(newFields.getFields().keySet());
					for (String fieldType : fieldTypeList) {
						json.addProperty(fieldType, newFields.get(fieldType));
					}

					String serviceUrl = user.getCallBackUrl();
					logger.info("Callback url >>> " + serviceUrl);
					HttpPost request = new HttpPost(serviceUrl);

					int CONNECTION_TIMEOUT_MS = 30 * 1000; // Timeout in millis.
					RequestConfig requestConfig = RequestConfig.custom()
							.setConnectionRequestTimeout(CONNECTION_TIMEOUT_MS).setConnectTimeout(CONNECTION_TIMEOUT_MS)
							.setSocketTimeout(CONNECTION_TIMEOUT_MS).build();

					request.setConfig(requestConfig);

					CloseableHttpClient httpClient = HttpClientBuilder.create().build();

					logger.info("Callback Request for PG_REF_NUM " + newFields.get(FieldType.PG_REF_NUM.getName())
							+ " to " + user.getBusinessName() + " >>> " + json.toString());
					StringEntity params = new StringEntity(json.toString());

					request.addHeader("content-type", "application/json");
					request.setEntity(params);

					HttpResponse resp = httpClient.execute(request);
					HttpEntity response = resp.getEntity();
					String responseBody = EntityUtils.toString(response);
					logger.info("Callback Response from " + user.getBusinessName() + " >>> " + responseBody.toString());

				} catch (Exception e) {
					logger.error("Exception in sending call back response to " + user.getBusinessName()
							+ " for Order Id == " + fields.get(FieldType.ORDER_ID.getName()) + " and PG Ref Num == "
							+ fields.get(FieldType.PG_REF_NUM.getName()), e);
				}
			}
		}
	}

	public String getPreviousByOIDForSentToBank(String oid) throws SystemException {

		String internalRequestFields = null;
		try {
			logger.info("Inside getPreviousByOIDForSentToBank (OID): " + oid);
			List<BasicDBObject> condList = new ArrayList<BasicDBObject>();
			condList.add(new BasicDBObject(FieldType.OID.getName(), oid));
			condList.add(new BasicDBObject(FieldType.TXNTYPE.getName(), TransactionType.SALE.getName()));
			condList.add(new BasicDBObject(FieldType.STATUS.getName(), StatusType.SENT_TO_BANK.getName()));
			BasicDBObject saleQuery = new BasicDBObject("$and", condList);

			MongoDatabase dbIns = mongoInstance.getDB();
			MongoCollection<Document> collection = dbIns
					.getCollection(propertiesManager.propertiesMap.get(prefix + Constants.COLLECTION_NAME.getValue()));
			MongoCursor<Document> cursor = collection.find(saleQuery).iterator();
			try {
				while (cursor.hasNext()) {
					Document doc = cursor.next();
					if (StringUtils.isNotBlank(String.valueOf(doc.get(FieldType.INTERNAL_REQUEST_FIELDS.getName())))) {
						internalRequestFields = String.valueOf(doc.get(FieldType.INTERNAL_REQUEST_FIELDS.getName()));
						break;
					}
				}
			} finally {
				cursor.close();
			}
			return internalRequestFields;
		} catch (Exception exception) {
			String message = "Error while previous based on OID from database";
			logger.error(message, exception);
			return internalRequestFields;
		}
	}

	public String getIpAddressByOIDForPending(String oid) throws SystemException {

		String ipAddress = null;
		try {
			logger.info("Inside getIpAddressByOIDForPending (OID): " + oid);

			List<BasicDBObject> condList = new ArrayList<BasicDBObject>();
			condList.add(new BasicDBObject(FieldType.OID.getName(), oid));
			condList.add(new BasicDBObject(FieldType.TXNTYPE.getName(), TransactionType.NEWORDER.getName()));
			condList.add(new BasicDBObject(FieldType.STATUS.getName(), StatusType.PENDING.getName()));
			BasicDBObject saleQuery = new BasicDBObject("$and", condList);

			MongoDatabase dbIns = mongoInstance.getDB();
			MongoCollection<Document> collection = dbIns
					.getCollection(propertiesManager.propertiesMap.get(prefix + Constants.COLLECTION_NAME.getValue()));
			MongoCursor<Document> cursor = collection.find(saleQuery).iterator();
			try {
				while (cursor.hasNext()) {

					Document doc = cursor.next();

					if (StringUtils.isNotBlank(String.valueOf(doc.get(FieldType.INTERNAL_CUST_IP.getName())))) {
						ipAddress = String.valueOf(doc.get(FieldType.INTERNAL_CUST_IP.getName()));
						break;
					}
				}
			} finally {
				cursor.close();
			}

			return ipAddress;
		} catch (Exception exception) {
			String message = "Error while previous based on OID from database";
			logger.error(message, exception);
			return ipAddress;
		}

	}

	public String getIpAddressByPgRefNumForSTB(String pgRefNum) {
		String ipAddress = null;
		try {

			logger.info("Inside getIpAddressByPgRefNumForSTB (pgRefNum): " + pgRefNum);
			List<BasicDBObject> condList = new ArrayList<BasicDBObject>();
			condList.add(new BasicDBObject(FieldType.PG_REF_NUM.getName(), pgRefNum));
			condList.add(new BasicDBObject(FieldType.TXNTYPE.getName(), TransactionType.SALE.getName()));
			condList.add(new BasicDBObject(FieldType.STATUS.getName(), StatusType.SENT_TO_BANK.getName()));
			BasicDBObject saleQuery = new BasicDBObject("$and", condList);
			MongoDatabase dbIns = mongoInstance.getDB();
			MongoCollection<Document> collection = dbIns
					.getCollection(propertiesManager.propertiesMap.get(prefix + Constants.COLLECTION_NAME.getValue()));
			MongoCursor<Document> cursor = collection.find(saleQuery).iterator();
			try {
				while (cursor.hasNext()) {
					Document doc = cursor.next();
					if (StringUtils.isNotBlank(String.valueOf(doc.get(FieldType.INTERNAL_CUST_IP.getName())))) {
						ipAddress = String.valueOf(doc.get(FieldType.INTERNAL_CUST_IP.getName()));
						break;
					}
				}
			} finally {
				cursor.close();
			}
			return ipAddress;
		} catch (Exception exception) {
			String message = "Error while getIpAddressByPgRefNumForSTB based on PgRefNum from database";
			logger.error(message, exception);
			return ipAddress;
		}
	}

	public String getIpFromSTBUsingPgRefNum(String pgRefNum) {
		String ipAddress = null;
		try {

			logger.info("Inside getIpFromSTB (PGREFNUM): " + pgRefNum);
			List<BasicDBObject> condList = new ArrayList<BasicDBObject>();
			condList.add(new BasicDBObject(FieldType.PG_REF_NUM.getName(), pgRefNum));
			condList.add(new BasicDBObject(FieldType.TXNTYPE.getName(), TransactionType.SALE.getName()));
			condList.add(new BasicDBObject(FieldType.STATUS.getName(), StatusType.SENT_TO_BANK.getName()));
			BasicDBObject saleQuery = new BasicDBObject("$and", condList);
			MongoDatabase dbIns = mongoInstance.getDB();
			MongoCollection<Document> collection = dbIns.getCollection(
					propertiesManager.propertiesMap.get(prefix + Constants.TRANSACTION_STATUS_COLLECTION.getValue()));
			MongoCursor<Document> cursor = collection.find(saleQuery).iterator();
			try {
				while (cursor.hasNext()) {
					Document doc = cursor.next();
					if (StringUtils.isNotBlank(String.valueOf(doc.get(FieldType.INTERNAL_CUST_IP.getName())))) {
						return ipAddress = String.valueOf(doc.get(FieldType.INTERNAL_CUST_IP.getName()));
					}
				}
			} finally {
				cursor.close();
			}
			return ipAddress;
		} catch (Exception exception) {
			String message = "Error while getIpFromSTB based on OID from database";
			logger.error(message, exception);
			return ipAddress;
		}
	}

	public Fields getPreviousRefundPendingForPgRefNum(String pgRefNum, String refundId) {

		logger.info("pgRefNum " + pgRefNum);
		logger.info("refundId " + refundId);
		Fields fields = new Fields();
		List<BasicDBObject> dbObjList = new ArrayList<BasicDBObject>();
		dbObjList.add(new BasicDBObject(FieldType.PG_REF_NUM.getName(), pgRefNum));
		dbObjList.add(new BasicDBObject(FieldType.TXNTYPE.getName(), TransactionType.REFUND.getName()));
		dbObjList.add(new BasicDBObject(FieldType.STATUS.getName(), "REFUND_INITIATED"));
		dbObjList.add(new BasicDBObject(FieldType.REFUND_ORDER_ID.getName(), refundId));

		BasicDBObject andQuery = new BasicDBObject("$and", dbObjList);
		MongoDatabase dbIns = mongoInstance.getDB();
		MongoCollection<Document> coll = dbIns.getCollection("transactionStatus");
		logger.info("andQuery" + andQuery);
		FindIterable<Document> cursor = coll.find(andQuery);

		if (cursor.iterator().hasNext()) {
			Document documentObj = cursor.iterator().next();

			if (null != documentObj) {
				for (int j = 0; j < documentObj.size(); j++) {
					for (String columnName : aLLDB_Fields) {
						if (documentObj.get(columnName) != null) {
							fields.put(columnName, documentObj.get(columnName).toString());
						} else {

						}

					}
				}
			}
			fields.logAllFields("Previous fields");
		}
		cursor.iterator().close();


		return fields;
	}

	public Document getLatestByPgRefNum(String pgRefNum) throws SystemException {
		Document document = new Document();
		try {
			logger.info("getLatestByPgRefNum: pgRefNo={}", pgRefNum);
			List<BasicDBObject> condList = new ArrayList<BasicDBObject>();
			condList.add(new BasicDBObject(FieldType.PG_REF_NUM.getName(), pgRefNum));
			condList.add(new BasicDBObject(FieldType.TXNTYPE.getName(), TransactionType.SALE.getName()));
			condList.add(new BasicDBObject(FieldType.STATUS.getName(), StatusType.CAPTURED.getName()));
			BasicDBObject saleQuery = new BasicDBObject("$and", condList);

			MongoDatabase dbIns = mongoInstance.getDB();
			MongoCollection<Document> collection = dbIns
					.getCollection(propertiesManager.propertiesMap.get(prefix + Constants.COLLECTION_NAME.getValue()));
			MongoCursor<Document> cursor = collection.find(saleQuery).iterator();
			try {
				if (cursor.hasNext()) {
					document = (Document) cursor.next();
				}
			} finally {
				cursor.close();
			}
			
		} catch (Exception exception) {
			String message = "getLatestByPgRefNum:: Error while previous based on Pg Ref Num from database";
			logger.error(message, exception);
		}
		return document;

	}

	public boolean checkRefundStatus(String pgRefNum, String refundId) {

		logger.info("checkRefundStatus PG_REF_NUM = {}, REFUND_ORDER_ID = {} ",pgRefNum,refundId);
		
		List<BasicDBObject> dbObjList = new ArrayList<BasicDBObject>();
		dbObjList.add(new BasicDBObject(FieldType.PG_REF_NUM.getName(), pgRefNum));
		dbObjList.add(new BasicDBObject(FieldType.TXNTYPE.getName(), TransactionType.REFUND.getName()));
		dbObjList.add(new BasicDBObject(FieldType.STATUS.getName(), StatusType.CAPTURED.getName()));
		dbObjList.add(new BasicDBObject(FieldType.REFUND_ORDER_ID.getName(), refundId));

		BasicDBObject andQuery = new BasicDBObject("$and", dbObjList);
		MongoDatabase dbIns = mongoInstance.getDB();
		MongoCollection<Document> coll = dbIns.getCollection("transactionStatus");
		logger.info("andQuery" + andQuery);
		FindIterable<Document> cursor = coll.find(andQuery);

		if (cursor.iterator().hasNext()) {
			return true;
		}
		cursor.iterator().close();
		
		return false;
	}
	
	public boolean findBulkRefund(String fileName) {
		boolean ret_Val = true;
		MongoCursor<Document> cursor = null;
		try {
			logger.info("Inside Bulkrefund (fileName): " + fileName);
			List<BasicDBObject> condList = new ArrayList<BasicDBObject>();
			condList.add(new BasicDBObject("fileName", fileName));
			BasicDBObject saleQuery = new BasicDBObject("$and", condList);
			logger.info("Inside Bulkrefund fileName={}, Query={} ", fileName, saleQuery);
			MongoDatabase dbIns = mongoInstance.getDB();
			MongoCollection<Document> collection = dbIns.getCollection("bulkRefundEntity");
			cursor = collection.find(saleQuery).iterator();
			if (cursor.hasNext()) {
				ret_Val = false;
			}
		} catch (Exception exception) {
			String message = "Error while findBulkRefund";
			logger.error(message, exception);
		} finally {
			cursor.close();
		}
		return ret_Val;
	}
	public Map<String, String> checkAcquirerDownStatus(String acquirerName, String paymentType, int failedCnt, String timeSlab, List<String> responseCode) {
		logger.info("checkAcquirerDownStatus acquirerName = {}, paymentType = {}, failedCnt = {}, timeSlab = {} ",acquirerName, paymentType, failedCnt, timeSlab);
		Map<String, String> mapResp = new HashMap<String, String>();
		String minutesBefore = timeSlab;
		
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String timeNow = sdf.format(new Date()).toString();

		LocalDateTime datetime = LocalDateTime.parse(timeNow, formatter);
		LocalDateTime datetime3 = LocalDateTime.parse(timeNow, formatter);

		datetime = datetime.minusMinutes(Integer.valueOf(minutesBefore));
		String endTime = datetime.format(formatter);
		String startTime = datetime3.format(formatter);

		logger.info("Scheduler status enquiry Start Time = " + startTime);
		logger.info("Scheduler status enquiry End  Time = " + endTime);
		
		BasicDBObject dateTimeQuery = new BasicDBObject();
		
		dateTimeQuery.put(FieldType.CREATE_DATE.getName(),
				BasicDBObjectBuilder.start("$gte", new SimpleDateFormat(endTime).toLocalizedPattern())
						.add("$lt", new SimpleDateFormat(startTime).toLocalizedPattern()).get());
		
		List<BasicDBObject> dbObjList = new ArrayList<BasicDBObject>();
		dbObjList.add(new BasicDBObject(FieldType.ACQUIRER_TYPE.getName(), acquirerName));
		dbObjList.add(new BasicDBObject(FieldType.TXNTYPE.getName(), TransactionType.SALE.getName()));
		dbObjList.add(new BasicDBObject(FieldType.PAYMENT_TYPE.getName(), paymentType));
		dbObjList.add(new BasicDBObject(FieldType.RESPONSE_CODE.getName(), new BasicDBObject("$nin", responseCode)));
		
		dbObjList.add(dateTimeQuery);

		BasicDBObject andQuery = new BasicDBObject("$and", dbObjList);
		MongoDatabase dbIns = mongoInstance.getDB();
		MongoCollection<Document> coll = dbIns.getCollection(propertiesManager.propertiesMap.get(prefix + Constants.TRANSACTION_STATUS_COLLECTION.getValue()));
		logger.info("checkAcquirerDownStatus AndQuery : " + andQuery);
		FindIterable<Document> cursor = coll.find(andQuery).limit(failedCnt).sort(Sorts.descending("CREATE_DATE"));
		MongoCursor<Document> result = cursor.iterator();
		mapResp.put("downTime", endTime);
		int txnCnt = 0;
		while (result.hasNext()) {
			txnCnt ++;
			Document document = (Document) result.next();
			//logger.info("checkAcquirerDownStatus txnCnt= {}, Document = {} ", txnCnt, document.toString());
			if(StringUtils.equalsAnyIgnoreCase(document.getString("STATUS"), "Captured")) {
				//return false;
				mapResp.put("status", "N");
				return mapResp;
			}
		}
		cursor.iterator().close();
		logger.info("checkAcquirerDownStatus txnCnt : " + txnCnt);
		logger.info("checkAcquirerDownStatus failedCnt : " + failedCnt);
		if(txnCnt < failedCnt) {
			mapResp.put("status", "N"); //return false;
			return mapResp;
		}
		mapResp.put("status", "Y");  //return true;
		return mapResp;
	}
	
	
	public void addTransactionExtensionn(String payId, String orderId) throws SystemException {
		try {
			logger.info("Request For addTransactionExtensionn: OrderId:" + orderId + ", PayId:" + payId);
			MongoDatabase dbIns = null;
			BasicDBObject newFieldsObj = new BasicDBObject();
			newFieldsObj.put("ORDER_ID", orderId);
			newFieldsObj.put("PAY_ID", payId);
			dbIns = mongoInstance.getDB();
			MongoCollection<Document> collection = dbIns.getCollection("transactionExtension");
			Document doc = new Document(newFieldsObj);
			collection.insertOne(doc);
		} catch (Exception e) {
			logger.info("Exception in addTransactionExtensionn "+e);
			throw new SystemException(ErrorType.DUPLICATE_ORDER_ID, "Duplicate OrderId:" + orderId);
		}

	}
/*	public Fields getPrivousFieldsByOderId(String orderId) {

		Fields fields = new Fields();
		// PropertiesManager propManager = new PropertiesManager();
		List<BasicDBObject> dbObjList = new ArrayList<BasicDBObject>();
		dbObjList.add(new BasicDBObject(FieldType.ORDER_ID.getName(), orderId));
		dbObjList.add(new BasicDBObject(FieldType.TXNTYPE.getName(), "SALE"));
		dbObjList.add(new BasicDBObject(FieldType.STATUS.getName(), "Sent to Bank"));

		BasicDBObject andQuery = new BasicDBObject("$and", dbObjList);
		logger.info("query for privous fields"+andQuery);
		MongoDatabase dbIns = mongoInstance.getDB();
		MongoCollection<Document> coll = dbIns
				.getCollection(propertiesManager.propertiesMap.get(prefix + Constants.COLLECTION_NAME.getValue()));
		MongoCursor<Document> cursor = coll.find(andQuery).iterator();
		if (cursor.hasNext()) {
			Document documentObj = cursor.next();

			if (null != documentObj) {
				for (int j = 0; j < documentObj.size(); j++) {
					for (String columnName : aLLDB_Fields) {
						if (documentObj.get(columnName) != null) {
							if(columnName.equalsIgnoreCase(FieldType.AMOUNT.getName())||columnName.equalsIgnoreCase(FieldType.TOTAL_AMOUNT.getName())) {
								fields.put(columnName, Amount.formatAmount(documentObj.get(columnName).toString(),"356"));
	
							}else {
							fields.put(columnName, documentObj.get(columnName).toString());
							}
						} else {

						}

					}
				}
			}
			fields.logAllFields("Previous fields");
		}
		cursor.close();
		return fields;

	}*/

	public boolean findIrctcRefundFileName(String fileName) {
		boolean ret_Val = false;
		MongoCursor<Document> cursor = null;
		try {
			logger.info("Inside Bulkrefund (fileName): " + fileName);
			List<BasicDBObject> condList = new ArrayList<BasicDBObject>();
			condList.add(new BasicDBObject("fileName", fileName));
			BasicDBObject saleQuery = new BasicDBObject("$and", condList);
			logger.info("Inside Bulkrefund fileName={}, Query={} ", fileName, saleQuery);
			MongoDatabase dbIns = mongoInstance.getDB();
			MongoCollection<Document> collection = dbIns.getCollection("irctcRefundEntity");
			cursor = collection.find(saleQuery).iterator();
			if (cursor.hasNext()) {
				ret_Val = true;
			}
		} catch (Exception exception) {
			String message = "Error while findBulkRefund";
			logger.error(message, exception);
		} finally {
			cursor.close();
		}
		return ret_Val;
	}
	
	public String getLastestCapturedTransation(String acquirer) {
		String payid = "";

		Session session = HibernateSessionProvider.getSession();
		try {

			MongoDatabase dbIns = mongoInstance.getDB();
			MongoCollection<Document> coll = dbIns.getCollection(
					PropertiesManager.propertiesMap.get(prefix + Constants.TRANSACTION_STATUS_COLLECTION.getValue()));

			BasicDBObject match = new BasicDBObject();

			match.put(FieldType.STATUS.getName(), StatusType.CAPTURED.getName());

			match.put(FieldType.TXNTYPE.getName(), "SALE");
			match.put(FieldType.ACQUIRER_TYPE.getName(), acquirer);
			match.put(FieldType.PAYMENT_TYPE.getName(), "UP");


			BasicDBObject matchQ = new BasicDBObject("$match", match);
			BasicDBObject sort = new BasicDBObject("$sort", new BasicDBObject("CREATE_DATE", -1));
			BasicDBObject limit = new BasicDBObject("$limit", 1);
			List<BasicDBObject> pipeline = Arrays.asList(matchQ, sort, limit);

			logger.info("Query : " + pipeline);

			AggregateIterable<Document> output = coll.aggregate(pipeline);
			output.allowDiskUse(true);
			MongoCursor<Document> cursor = output.iterator();
			TransactionSearchNew transReport = null;
			while (cursor.hasNext()) {
				Document dbobj = cursor.next();
				payid = dbobj.getString(FieldType.PAY_ID.toString());

			}
			cursor.close();
			return payid;

		} catch (Exception e) {
			logger.error("Exception in search payment for admin", e);
		}
		return payid;
	}
	

	public void getLastestCapturedTransationforAllAcquirer(Fields fields) {
		String payid = "";

		Session session = HibernateSessionProvider.getSession();
		try {

			MongoDatabase dbIns = mongoInstance.getDB();
			MongoCollection<Document> coll = dbIns.getCollection(
					PropertiesManager.propertiesMap.get(prefix + Constants.TRANSACTION_STATUS_COLLECTION.getValue()));
			List<String> acqList = new ArrayList<String>();
			acqList.add("SBI");
			acqList.add("COSMOS");
			acqList.add("YESBANKCB");

			BasicDBObject match = new BasicDBObject();

			match.put(FieldType.STATUS.getName(), StatusType.CAPTURED.getName());
			match.put(FieldType.ACQUIRER_TYPE.getName(), new BasicDBObject("$in", acqList));
			match.put(FieldType.TXNTYPE.getName(), "SALE");
			match.put(FieldType.PAYMENT_TYPE.getName(), "UP");

			BasicDBObject matchQ = new BasicDBObject("$match", match);
			BasicDBObject sort = new BasicDBObject("$sort", new BasicDBObject("CREATE_DATE", -1));
			BasicDBObject limit = new BasicDBObject("$limit", 1);
			List<BasicDBObject> pipeline = Arrays.asList(matchQ, sort, limit);

			logger.info("Query : " + pipeline);

			AggregateIterable<Document> output = coll.aggregate(pipeline);
			output.allowDiskUse(true);
			MongoCursor<Document> cursor = output.iterator();
			TransactionSearchNew transReport = null;
			while (cursor.hasNext()) {
				Document dbobj = cursor.next();
				fields.put( FieldType.PAY_ID.getName()  ,dbobj.getString(FieldType.PAY_ID.toString()));
				fields.put( FieldType.ACQUIRER_TYPE.getName()  ,dbobj.getString(FieldType.ACQUIRER_TYPE.toString()));


			}
			cursor.close();

		} catch (Exception e) {
			logger.error("Exception in search payment for admin", e);
		}
	}

	public Fields getPreviousForOrderIdSTB(String orderId) throws SystemException {

		Fields preFields = new Fields();

		try {
			logger.info("Inside getPreviousForOrderIdSTB (ORDER_ID): " + orderId);

			List<BasicDBObject> condList = new ArrayList<BasicDBObject>();
			condList.add(new BasicDBObject(FieldType.ORDER_ID.getName(), orderId));
			condList.add(new BasicDBObject(FieldType.TXNTYPE.getName(), TransactionType.SALE.getName()));
			condList.add(new BasicDBObject(FieldType.STATUS.getName(), StatusType.SENT_TO_BANK.getName()));
			BasicDBObject saleQuery = new BasicDBObject("$and", condList);

			MongoDatabase dbIns = mongoInstance.getDB();
			MongoCollection<Document> collection = dbIns
					.getCollection(propertiesManager.propertiesMap.get(prefix + Constants.COLLECTION_NAME.getValue()));
			MongoCursor<Document> cursor = collection.find(saleQuery).iterator();
			try {
				if (cursor.hasNext()) {
					Document documentObj = (Document) cursor.next();
					if (null != documentObj) {
						for (int j = 0; j < documentObj.size(); j++) {
							for (String columnName : systemProperties.getDBFields()) {
								if (documentObj.get(columnName) != null) {
									preFields.put(columnName, documentObj.get(columnName).toString());
								}
							}
						}
					}
				}
			} finally {
				cursor.close();
			}

			// Correct Amount to decimal format

			if (StringUtils.isNotBlank(preFields.get(FieldType.AMOUNT.getName()))) {
				preFields.put(FieldType.AMOUNT.getName(), Amount.formatAmount(preFields.get(FieldType.AMOUNT.getName()),
						preFields.get(FieldType.CURRENCY_CODE.getName())));
			}

			if (StringUtils.isNotBlank(preFields.get(FieldType.TOTAL_AMOUNT.getName()))) {
				preFields.put(FieldType.TOTAL_AMOUNT.getName(),
						Amount.formatAmount(preFields.get(FieldType.TOTAL_AMOUNT.getName()),
								preFields.get(FieldType.CURRENCY_CODE.getName())));
			}

			preFields.put(FieldType.HASH.getName(), Hasher.getHash(preFields));
			return preFields;
		} catch (Exception exception) {
			String message = "Error while previous based on Order Id Num from database";
			logger.error(message, exception);
			return preFields;
		}

	}

	public void insertPOUpdate(Fields fields) {
			
			try {
			MongoDatabase dbIns = mongoInstance.getDB();
			MongoCollection<Document> coll = dbIns.getCollection("POtransactionStatus");
			logger.info("fields in update processor : "+fields.getFieldsAsString());

			List<BasicDBObject> params=new ArrayList<>();
			
			params.add(new BasicDBObject(FieldType.PAY_ID.getName(), fields.get(FieldType.PAY_ID.getName())));
			params.add(new BasicDBObject("TXNTYPE", fields.get(FieldType.TXNTYPE.getName())));
			params.add(new BasicDBObject(FieldType.ORDER_ID.getName(), fields.get(FieldType.ORDER_ID.getName())));

			
			BasicDBObject finalquery = new BasicDBObject("$and", params);
			logger.info("fields in update processor : "+fields.getFieldsAsString());

			MongoCursor<Document> result = coll.find(finalquery).iterator();
			Document document=null;
			if (result.hasNext()) {
				document= (Document) result.next();
			}
			
			logger.info("fields in update processor : "+fields.getFieldsAsString());

			if(document!=null) {
				DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				
				document.put("_id", TransactionManager.getNewTransactionId());			
				document.put(FieldType.UPDATEDBY.getName(), dateFormat.format(new Date()));
				document.put(FieldType.RESPONSE_CODE.getName(), fields.get(FieldType.RESPONSE_CODE.getName()));
				document.put(FieldType.STATUS.getName(), fields.get(FieldType.STATUS.getName()));
				document.put(FieldType.RESPONSE_MESSAGE.getName(), fields.get(FieldType.RESPONSE_MESSAGE.getName()));
				document.put(FieldType.ACQUIRER_TYPE.getName(), fields.get(FieldType.ACQUIRER_TYPE.getName()));
				document.put(FieldType.PG_TXN_MESSAGE.getName(),fields.get(FieldType.PG_TXN_MESSAGE.getName()));
				document.put(FieldType.ACQUIRER_TDR_SC.getName(),fields.get(FieldType.ACQUIRER_TDR_SC.getName()));
				document.put(FieldType.PG_TDR_SC.getName(),fields.get(FieldType.PG_TDR_SC.getName()));
				document.put(FieldType.ACQUIRER_GST.getName(),fields.get(FieldType.ACQUIRER_GST.getName()));

				
				
				MongoCollection<Document> collectionDb = dbIns.getCollection("POtransactionStatus");
				
				Bson updates = null;		
				updates = Updates.combine(Updates.set(FieldType.STATUS.getName(), fields.get(FieldType.STATUS.getName())),
						Updates.set(FieldType.UPDATEDBY.getName(), dateFormat.format(new Date())),
						Updates.set(FieldType.RESPONSE_CODE.getName(), fields.get(FieldType.RESPONSE_CODE.getName())),
						Updates.set(FieldType.STATUS.getName(), fields.get(FieldType.STATUS.getName())),
						Updates.set(FieldType.RESPONSE_MESSAGE.getName(), fields.get(FieldType.RESPONSE_MESSAGE.getName())),
						Updates.set(FieldType.ACQUIRER_TYPE.getName(), fields.get(FieldType.ACQUIRER_TYPE.getName())),
						Updates.set(FieldType.PG_TXN_MESSAGE.getName(),fields.get(FieldType.PG_TXN_MESSAGE.getName())),
						Updates.set(FieldType.ACQUIRER_TDR_SC.getName(),fields.get(FieldType.ACQUIRER_TDR_SC.getName())),
						Updates.set(FieldType.PG_TDR_SC.getName(),fields.get(FieldType.PG_TDR_SC.getName())),
						Updates.set(FieldType.ACQUIRER_GST.getName(),fields.get(FieldType.ACQUIRER_GST.getName())));
				
				UpdateOptions options = new UpdateOptions().upsert(true);
				BasicDBObject searchQuery = new BasicDBObject("$and", params);
				UpdateResult result1 = collectionDb.updateOne(searchQuery, updates, options);
				System.out.println(result1);
				
				
				MongoCollection<Document> collection = dbIns.getCollection("POtransaction");
				collection.insertOne(document);
			}
			
			}catch (Exception e) {
				e.printStackTrace();
			}
	}


	public void removeFieldsByPropertyFile(Fields fields, String applicationFileKey) {
		logger.info("data1"+applicationFileKey);
		String commaSeparatedKeys = propertiesManager.getSystemProperty(applicationFileKey);
		Collection<String> keys = (Helper.parseFields(commaSeparatedKeys));
		Fields orginalFields = new Fields(fields);
		for(String key : keys){
			orginalFields.remove(key);
		}
		for(String key : orginalFields.keySet()){
			fields.remove(key);
		}
	}

	public boolean checkPaymentByOrderId(String orderId, String payId) {
		boolean response = false;
		MongoCursor<Document> cursor = null;
		try {
			logger.info("checkPaymentByOrderId, check Payment OrderId exist or not, PayId={}, OrderId={} ",payId, orderId);
			List<BasicDBObject> condList = new ArrayList<BasicDBObject>();
			condList.add(new BasicDBObject(FieldType.ORDER_ID.getName(), orderId));
			condList.add(new BasicDBObject(FieldType.PAY_ID.getName(), payId));
			
			BasicDBObject saleQuery = new BasicDBObject("$and", condList);
			logger.info("checkPaymentByOrderId, PayId={}, OrderId={}, Query={} ",payId, orderId, saleQuery);
			MongoDatabase dbIns = mongoInstance.getDB();
			MongoCollection<Document> collection = dbIns.getCollection(PropertiesManager.propertiesMap.get(prefix + Constants.COLLECTION_NAME.getValue()));
			cursor = collection.find(saleQuery).iterator();
			if (cursor.hasNext()) {
				response = true;
			}
		} catch (Exception exception) {
			String message = "Error while previous based on Pg Ref Num from database";
			logger.error(message, exception);
			response = false;
		} finally {
			cursor.close();
		}
		return response;
	
	}

	public void validateDuplicateOrderIdForPayout(String payId, String orderId) throws SystemException {
		logger.info("validateDuplicateOrderIdForPayout, payId:"+payId+", orderId:"+orderId);
		MongoCursor<Document> cursor = null;
		try {
			List<BasicDBObject> dbObjList = new ArrayList<BasicDBObject>();
			dbObjList.add(new BasicDBObject(FieldType.ORDER_ID.getName(), orderId));
			dbObjList.add(new BasicDBObject(FieldType.PAY_ID.getName(), payId));

			BasicDBObject andQuery = new BasicDBObject("$and", dbObjList);
			logger.info("check dupicate sale order id individually validateDuplicateOrderIdForPayout :" + andQuery);
			MongoDatabase dbIns = mongoInstance.getDB();
			MongoCollection<Document> coll = dbIns.getCollection(PropertiesManager.propertiesMap.get(prefix + Constants.TRANSACTION_LEDGER_PO.getValue()));

			cursor = coll.find(andQuery).iterator();
			if (cursor.hasNext()) {
				throw new SystemException(ErrorType.DUPLICATE_ORDER_ID, "Duplicate order Id");
			}
		} catch (Exception exception) {
			String message = "Error while previous based on Pg Ref Num from database";
			logger.error(message, exception);
			throw new SystemException(ErrorType.DUPLICATE_ORDER_ID, "Duplicate order Id");
		} finally {
			cursor.close();
		}
	}
	
	@Autowired
	private MultCurrencyCodeDao multCurrencyCodeDao;
	@Autowired
	private MerchantWalletPODao merchantWalletPODao;
	@Autowired
	private WalletHistoryRepository walletHistoryRepository;

	public void updateMerchantTotalBalance(Fields fields) {
		logger.info("Update total balance : " + fields.getFieldsAsString());
		MongoDatabase dbIns = mongoInstance.getDB();
		MongoCollection<Document> collectiondb = dbIns.getCollection("WalletHistory");

        Document query = new Document("payId", fields.get(FieldType.PAY_ID.getName())).append("currency",multCurrencyCodeDao.getCurrencyNamebyCode(fields.get(FieldType.CURRENCY_CODE.getName())));
        String totalBalance="0";
        MongoCursor<Document> cursor= collectiondb.find(query).iterator();
        if(cursor.hasNext()) {
        	Document documentDB=cursor.next();
        	totalBalance=documentDB.getString("totalBalance");
        }
        logger.info("before total balance payout approve request : " + totalBalance);
        //totalBalance=String.valueOf(Double.parseDouble(totalBalance)-Double.parseDouble(Amount.formatAmount(fields.get(FieldType.AMOUNT.getName()), fields.get(FieldType.CURRENCY_CODE.getName()))));
        totalBalance=String.valueOf(Double.parseDouble(totalBalance)-Double.parseDouble(Amount.toDecimal(fields.get(FieldType.AMOUNT.getName()), fields.get(FieldType.CURRENCY_CODE.getName()))));
        logger.info("after total balance payout approve request : " + totalBalance);
        MerchantWalletPODTO merchantWalletPODTOdb=new MerchantWalletPODTO();
        merchantWalletPODTOdb.setCurrency(multCurrencyCodeDao.getCurrencyNamebyCode(fields.get(FieldType.CURRENCY_CODE.getName())));
        merchantWalletPODTOdb.setTotalBalance(totalBalance);
        merchantWalletPODTOdb.setPayId(fields.get(FieldType.PAY_ID.getName()));
		merchantWalletPODao.SaveAndUpdteTotalBalanceWallet(merchantWalletPODTOdb);
		
		walletHistoryRepository.updateBalanceByPayIdAndCurrencyTotalBalance
		(fields.get(FieldType.PAY_ID.getName()), merchantWalletPODTOdb.getCurrency(), "debit", Amount.toDecimal(fields.get(FieldType.AMOUNT.getName()), fields.get(FieldType.CURRENCY_CODE.getName())), totalBalance);
		merchantWalletPODao.SaveAndUpdteTotalBalanceWallet(merchantWalletPODTOdb);
	}

}