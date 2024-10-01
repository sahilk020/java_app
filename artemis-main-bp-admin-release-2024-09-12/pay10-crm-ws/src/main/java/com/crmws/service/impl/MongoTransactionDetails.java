package com.crmws.service.impl;

import java.sql.Date;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.StringUtils;
import org.bson.Document;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.crmws.dto.ChargeBackAutoPopulateData;
import com.google.gson.Gson;
import com.ibm.icu.impl.duration.DateFormatter;
import com.mongodb.BasicDBObject;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import com.pay10.commons.mongo.MongoInstance;
import com.pay10.commons.user.MultCurrencyCodeDao;
import com.pay10.commons.user.Resellerdailyupdate;
import com.pay10.commons.util.Constants;
import com.pay10.commons.util.FieldType;
import com.pay10.commons.util.PaymentType;
import com.pay10.commons.util.PropertiesManager;
import com.pay10.commons.util.StatusType;
import com.pay10.commons.util.TransactionManager;
import com.pay10.commons.util.TransactionType;

@Component
public class MongoTransactionDetails {

	private static final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
	private static final SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
	private static final String prefix = "MONGO_DB_";
	private static final Logger logger = LoggerFactory.getLogger(MongoTransactionDetails.class.getName());
	private static final DecimalFormat df = new DecimalFormat("0.00");
	@Autowired
	private MongoInstance mongoInstance;
	private Object currentuser;
	@Autowired
	private MultCurrencyCodeDao multCurrencyCodeDao;

	public ChargeBackAutoPopulateData getByPgRefNo(String pgRefNo) {

		ChargeBackAutoPopulateData data = new ChargeBackAutoPopulateData();
		logger.info("get pg reference no....." + pgRefNo);
		Document doc = getTxnByPgRefNo(pgRefNo);
		logger.info("get transaction details from mongodb... " + doc);
		try {
			if (doc!=null) {
				data.setAcquirerName(doc.getString(FieldType.ACQUIRER_TYPE.getName()));
				data.setBankTxnId(doc.getString(FieldType.OID.getName()));
				data.setPgRefNo(doc.getString(FieldType.PG_REF_NUM.getName()));
				data.setMerchantTxnId(doc.getString(FieldType.TXN_ID.getName()));

				if (StringUtils.isNotBlank(doc.getString(FieldType.CREATE_DATE.getName()))) {
					data.setDateOfTxn(sdf.format(sdf.parse(doc.getString(FieldType.CREATE_DATE.getName()))));
				}
				if (StringUtils.isNotBlank(doc.getString(FieldType.SETTLEMENT_DATE.getName()))) {
					data.setSettlementDate(sdf.format(sdf.parse(doc.getString(FieldType.SETTLEMENT_DATE.getName()))));
				}
				data.setModeOfPayment(doc.getString(FieldType.PAYMENT_TYPE.getName()));
				data.setTxnAmount(doc.getString(FieldType.AMOUNT.getName()));
				data.setPayId(doc.getString(FieldType.PAY_ID.getName()));
				// logger.info("payId..."+doc.getString("PAY_ID"));
				data.setMobile(doc.getString(FieldType.CUST_PHONE.getName()));
				data.setMerchantName(doc.getString(FieldType.CUST_NAME.getName()));				
				data.setEmail(doc.getString(FieldType.CUST_EMAIL.getName()));
				data.setOrderId(doc.getString(FieldType.ORDER_ID.getName()));
				data.setCurrencyName(multCurrencyCodeDao.getCurrencyNamebyCode(doc.getString(FieldType.CURRENCY_CODE.getName())));
			}else {
				return null;
			}
			
		} catch (Exception ex) {
			logger.error("Exception in getByPgRefNo method={}", pgRefNo, ex);
		}

		return data;
	}
	
//By Chetan For RNS
	public String updateTxnByTxnIDForUTRUpdateInTransactionStatusColl(String txnId, String UTR_NO, String UTR_Date) {
		logger.info("pgRefNo in get Txn By PgRefNo ()..." + txnId);
		Document doc = null;
		String ret_Str = "Sucesss";
		System.out.println(txnId + "    Details    " + UTR_NO);
		try {
			List<BasicDBObject> paramConditionLst = new ArrayList<BasicDBObject>();
			BasicDBObject pgRefQuery = new BasicDBObject(FieldType.PG_REF_NUM.getName(), txnId.trim());
			logger.info("pgRefQuery..." + pgRefQuery);
			paramConditionLst.add(pgRefQuery);
			System.out.println(UTR_NO);
			System.out.println(txnId);
			System.out.println(StatusType.SETTLED_RECONCILLED.getName());
			paramConditionLst.add(new BasicDBObject(FieldType.STATUS.getName(), new Document("$in",
					Arrays.asList(StatusType.SETTLED_RECONCILLED.getName(), StatusType.SETTLED_SETTLE.getName()))));
			BasicDBObject finalquery = new BasicDBObject("$and", paramConditionLst);
			logger.info("finalquery.." + finalquery);

			MongoDatabase dbIns = mongoInstance.getDB();
			MongoCollection<Document> coll = dbIns.getCollection(
					PropertiesManager.propertiesMap.get(prefix + Constants.TRANSACTION_STATUS_COLLECTION.getValue()));
			FindIterable<Document> output = coll.find(finalquery);
			MongoCursor<Document> cursor = output.iterator();
			while (cursor.hasNext()) {
				doc = cursor.next();
				doc.put(FieldType.STATUS.getName(), StatusType.SETTLED_SETTLE.getName());
				doc.put(FieldType.ALIAS_STATUS.getName(), StatusType.SETTLED_SETTLE.getName());
				if (doc.get(FieldType.UTR_NO.getName()) != null && doc.get(FieldType.UTR_NO.getName()) != "") {
					doc.put(FieldType.UTR_NO.getName(), doc.get(FieldType.UTR_NO.getName()) + "|" + UTR_NO);
				} else {
					doc.put(FieldType.UTR_NO.getName(), UTR_NO);
				}
				DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
				String strDate = UTR_Date;
//						dateFormat.format(Date.valueOf(UTR_Date));
				doc.put(FieldType.SETTLEMENT_DATE.getName(), strDate);
				//doc.put(FieldType.SETTLEMENT_FLAG.getName(), "Y");
				String[] date=UTR_Date.split(" ");
				if(date.length>0) {
					doc.put(FieldType.SETTLEMENT_DATE_INDEX.getName(), date[0].toString().replaceAll("-", ""));	
				}
				logger.info(" update method of tran_status");
				coll.updateOne(finalquery, new BasicDBObject("$set", doc));
				logger.info("calling update method of tran");
				updateTxnByTxnIDForUTRUpdateInTransactionColl(txnId, UTR_NO, UTR_Date);
			}
			cursor.close();

		} catch (Exception e) {
			logger.error("Exception occured in getTxnByTxnIDForUTRUpdate", e);
			ret_Str = "Failure";
		}
		return ret_Str;
	}

	private String updateTxnByTxnIDForUTRUpdateInTransactionColl(String txnId, String UTR_NO, String UTR_Date) {
		logger.info("pgRefNo in get Txn By PgRefNo ()..." + txnId);
		Document doc = null;
		DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
		String ret_Str = "Sucesss";
		try {
			List<BasicDBObject> paramConditionLst = new ArrayList<BasicDBObject>();
			BasicDBObject pgRefQuery = new BasicDBObject(FieldType.PG_REF_NUM.getName(), txnId.trim());
			logger.info("pgRefQuery..." + pgRefQuery);
			paramConditionLst.add(pgRefQuery);
			paramConditionLst
					.add(new BasicDBObject(FieldType.STATUS.getName(), StatusType.SETTLED_RECONCILLED.getName()));
			BasicDBObject finalquery = new BasicDBObject("$and", paramConditionLst);
			logger.info("finalquery.." + finalquery);

			MongoDatabase dbIns = mongoInstance.getDB();
			MongoCollection<Document> coll = dbIns
					.getCollection(PropertiesManager.propertiesMap.get(prefix + Constants.COLLECTION_NAME.getValue()));
			FindIterable<Document> output = coll.find(finalquery);
			MongoCursor<Document> cursor = output.iterator();
			// if the transaction is marked RNS
			if (cursor.hasNext()) {
				// if transaction already marked as settled (for uploading mutiple utr no in
				// case of split payment only)
				// check if settled exsist
				List<BasicDBObject> paramConditionLst1 = new ArrayList<BasicDBObject>();
				BasicDBObject pgRefQuery1 = new BasicDBObject(FieldType.PG_REF_NUM.getName(), txnId.trim());
				paramConditionLst1.add(pgRefQuery1);
				paramConditionLst1
						.add(new BasicDBObject(FieldType.STATUS.getName(), StatusType.SETTLED_SETTLE.getName()));
				BasicDBObject finalquerySettle = new BasicDBObject("$and", paramConditionLst1);
				logger.info("finalquery.." + finalquerySettle);
				MongoDatabase dbIns1 = mongoInstance.getDB();
				MongoCollection<Document> coll1 = dbIns1.getCollection(
						PropertiesManager.propertiesMap.get(prefix + Constants.COLLECTION_NAME.getValue()));

				FindIterable<Document> output1 = coll1.find(finalquerySettle);
				MongoCursor<Document> cursor1 = output1.iterator();
				if (cursor1.hasNext()) {
					Document documentSettleDoc = cursor1.next();
					documentSettleDoc.put(FieldType.UTR_NO.getName(),
							documentSettleDoc.get(FieldType.UTR_NO.getName()) + "|" + UTR_NO);
					String strDate = UTR_Date;
//							dateFormat.format(Date.valueOf(UTR_Date));
					documentSettleDoc.put(FieldType.CREATE_DATE.getName(), strDate);
					
					String[] date=UTR_Date.split(" ");
					if(date.length>0) {
						documentSettleDoc.put(FieldType.DATE_INDEX.getName(), date[0].toString().replaceAll("-", ""));
					}
					coll1.updateOne(finalquerySettle, new BasicDBObject("$set", documentSettleDoc));
				} else {
					while (cursor.hasNext()) {
						doc = cursor.next();
						doc.put(FieldType.STATUS.getName(), StatusType.SETTLED_SETTLE.getName());
						doc.put(FieldType.UTR_NO.getName(), UTR_NO);
						String strDate = UTR_Date;
						//dateFormat.format(Date.valueOf(UTR_Date));
						doc.put(FieldType.CREATE_DATE.getName(), strDate);
						String[] date=UTR_Date.split(" ");
						if(date.length>0) {
						doc.put(FieldType.DATE_INDEX.getName(), date[0].toString().replaceAll("-", ""));
						}
						String id = TransactionManager.getNewTransactionId();
						doc.put("_id", id);
						String newTxnId = TransactionManager.getNewTransactionId();
						doc.put("TXN_ID", newTxnId);
						Document document = new Document(doc);
						coll.insertOne(document);
					}
				}
				cursor1.close();

			} else {
			}
			cursor.close();

		} catch (Exception e) {
			logger.error("Exception occured in getTxnByTxnIDForUTRUpdate", e);
			ret_Str = "Failure";
		}
		return ret_Str;
	}

	public String updateTxnByTxnIDForUTRUpdateInTransactionStatusCollChargeback(String txnId, String UTR_NO, String UTR_Date,String type) {
		logger.info("pgRefNo in get Txn By PgRefNo ()..." + txnId);
		Document doc = null;
		String ret_Str = "Sucesss";
		System.out.println(txnId + "    Details    " + UTR_NO);
		try {
			List<BasicDBObject> paramConditionLst = new ArrayList<BasicDBObject>();
			BasicDBObject pgRefQuery = new BasicDBObject(FieldType.PG_REF_NUM.getName(), txnId.trim());
			logger.info("pgRefQuery..." + pgRefQuery);
			paramConditionLst.add(pgRefQuery);
			System.out.println(UTR_NO);
			System.out.println(txnId);
			System.out.println(StatusType.CHARGEBACK_INITIATED.getName());
			paramConditionLst.add(new BasicDBObject(FieldType.STATUS.getName(), type));
			BasicDBObject finalquery = new BasicDBObject("$and", paramConditionLst);
			logger.info("finalquery.." + finalquery);

			MongoDatabase dbIns = mongoInstance.getDB();
			MongoCollection<Document> coll = dbIns.getCollection(
					PropertiesManager.propertiesMap.get(prefix + Constants.TRANSACTION_STATUS_COLLECTION.getValue()));
			FindIterable<Document> output = coll.find(finalquery);
			MongoCursor<Document> cursor = output.iterator();
			while (cursor.hasNext()) {
				doc = cursor.next();
//				doc.put(FieldType.STATUS.getName(), StatusType.SETTLED_SETTLE.getName());
//				doc.put(FieldType.ALIAS_STATUS.getName(), StatusType.SETTLED_SETTLE.getName());
				if (doc.get(FieldType.UTR_NO.getName()) != null && doc.get(FieldType.UTR_NO.getName()) != "") {
					doc.put(FieldType.UTR_NO.getName(), doc.get(FieldType.UTR_NO.getName()) + "|" + UTR_NO);
				} else {
					doc.put(FieldType.UTR_NO.getName(), UTR_NO);
				}
				DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
				String strDate = UTR_Date;
//						dateFormat.format(Date.valueOf(UTR_Date));
				doc.put(FieldType.SETTLEMENT_DATE.getName(), strDate);
				String id = TransactionManager.getNewTransactionId();
				doc.put(FieldType.PG_REF_NUM.getName(), id);
//				doc.put(FieldType.SETTLEMENT_FLAG.getName(), "Y");
				String[] date=UTR_Date.split(" ");
				if(date.length>0) {
					doc.put(FieldType.SETTLEMENT_DATE_INDEX.getName(), date[0].toString().replaceAll("-", ""));	
				}
				logger.info(" update method of tran_status");
				coll.updateOne(finalquery, new BasicDBObject("$set", doc));
				logger.info("calling update method of tran");
				updateTxnByTxnIDForUTRUpdateInTransactionCollChargeback(txnId, UTR_NO, UTR_Date,type);
			}
			cursor.close();

		} catch (Exception e) {
			logger.error("Exception occured in getTxnByTxnIDForUTRUpdate", e);
			ret_Str = "Failure";
		}
		return ret_Str;
	}

	private String updateTxnByTxnIDForUTRUpdateInTransactionCollChargeback(String txnId, String UTR_NO, String UTR_Date,String type) {
		logger.info("pgRefNo in get Txn By PgRefNo ()..." + txnId);
		Document doc = null;
		DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
		String ret_Str = "Sucesss";
		try {
			List<BasicDBObject> paramConditionLst = new ArrayList<BasicDBObject>();
			BasicDBObject pgRefQuery = new BasicDBObject(FieldType.PG_REF_NUM.getName(), txnId.trim());
			logger.info("pgRefQuery..." + pgRefQuery);
			paramConditionLst.add(pgRefQuery);
			paramConditionLst
					.add(new BasicDBObject(FieldType.STATUS.getName(), type));
			BasicDBObject finalquery = new BasicDBObject("$and", paramConditionLst);
			logger.info("finalquery.." + finalquery);

			MongoDatabase dbIns = mongoInstance.getDB();
			MongoCollection<Document> coll = dbIns
					.getCollection(PropertiesManager.propertiesMap.get(prefix + Constants.COLLECTION_NAME.getValue()));
			FindIterable<Document> output = coll.find(finalquery);
			MongoCursor<Document> cursor = output.iterator();
			// if the transaction is marked RNS
			if (cursor.hasNext()) {
				// if transaction already marked as settled (for uploading mutiple utr no in
				// case of split payment only)
				// check if settled exsist
				List<BasicDBObject> paramConditionLst1 = new ArrayList<BasicDBObject>();
				BasicDBObject pgRefQuery1 = new BasicDBObject(FieldType.PG_REF_NUM.getName(), txnId.trim());
				paramConditionLst1.add(pgRefQuery1);
				paramConditionLst1
						.add(new BasicDBObject(FieldType.STATUS.getName(), type));
				BasicDBObject finalquerySettle = new BasicDBObject("$and", paramConditionLst1);
				logger.info("finalquery.." + finalquerySettle);
				MongoDatabase dbIns1 = mongoInstance.getDB();
				MongoCollection<Document> coll1 = dbIns1.getCollection(
						PropertiesManager.propertiesMap.get(prefix + Constants.COLLECTION_NAME.getValue()));

				FindIterable<Document> output1 = coll1.find(finalquerySettle);
				MongoCursor<Document> cursor1 = output1.iterator();
				if (cursor1.hasNext()) {
					Document documentSettleDoc = cursor1.next();
					documentSettleDoc.put(FieldType.UTR_NO.getName(),
							documentSettleDoc.get(FieldType.UTR_NO.getName()) + "|" + UTR_NO);
					String strDate = UTR_Date;
//							dateFormat.format(Date.valueOf(UTR_Date));
					documentSettleDoc.put(FieldType.CREATE_DATE.getName(), strDate);
					String id = TransactionManager.getNewTransactionId();
					documentSettleDoc.put(FieldType.PG_REF_NUM.getName(), id);
					String[] date=UTR_Date.split(" ");
					if(date.length>0) {
						documentSettleDoc.put(FieldType.DATE_INDEX.getName(), date[0].toString().replaceAll("-", ""));
					}
					coll1.updateOne(finalquerySettle, new BasicDBObject("$set", documentSettleDoc));
				} else {
					while (cursor.hasNext()) {
						doc = cursor.next();
						String id1 = TransactionManager.getNewTransactionId();
						doc.put(FieldType.PG_REF_NUM.getName(), id1);
						doc.put(FieldType.STATUS.getName(), type);
						doc.put(FieldType.UTR_NO.getName(), UTR_NO);
						String strDate = UTR_Date;
						//dateFormat.format(Date.valueOf(UTR_Date));
						doc.put(FieldType.CREATE_DATE.getName(), strDate);
						String[] date=UTR_Date.split(" ");
						if(date.length>0) {
						doc.put(FieldType.DATE_INDEX.getName(), date[0].toString().replaceAll("-", ""));
						}
						String id = TransactionManager.getNewTransactionId();
						doc.put("_id", id);
						String newTxnId = TransactionManager.getNewTransactionId();
						doc.put("TXN_ID", newTxnId);
						Document document = new Document(doc);
						coll.insertOne(document);
					}
				}
				cursor1.close();

			} else {
			}
			cursor.close();

		} catch (Exception e) {
			logger.error("Exception occured in getTxnByTxnIDForUTRUpdate", e);
			ret_Str = "Failure";
		}
		return ret_Str;
	}

	
	private Document getTxnByTxnId(String txnId) {
		logger.info("pgRefNo in get Txn By txnId ()..." + txnId);
		Document doc = null;
		try {
			List<BasicDBObject> paramConditionLst = new ArrayList<BasicDBObject>();
			BasicDBObject pgRefQuery = new BasicDBObject(FieldType.TXN_ID.getName(), txnId.trim());
			logger.info("pgRefQuery..." + pgRefQuery);
			paramConditionLst.add(pgRefQuery);
			paramConditionLst.add(new BasicDBObject(FieldType.STATUS.getName(), StatusType.SETTLED_SETTLE.getName()));
			BasicDBObject finalquery = new BasicDBObject("$and", paramConditionLst);
			logger.info("finalquery.." + finalquery);

			MongoDatabase dbIns = mongoInstance.getDB();
			MongoCollection<Document> coll = dbIns.getCollection(
					PropertiesManager.propertiesMap.get(prefix + Constants.TRANSACTION_STATUS_COLLECTION.getValue()));
			FindIterable<Document> output = coll.find(finalquery);
			MongoCursor<Document> cursor = output.iterator();
			while (cursor.hasNext()) {
				doc = cursor.next();
			}
			cursor.close();
		} catch (Exception e) {
			logger.error("Exception occured in getTxnByPgRefNo", e);
			return null;
		}
		return doc;
	}

	public Document getTxnByPgRefNo(String pgRefNo) {
		logger.info("pgRefNo in get Txn By PgRefNo ()..." + pgRefNo);
		Document doc = null;
		try {

			BasicDBObject match =new BasicDBObject();
			match.put(FieldType.PG_REF_NUM.getName(), pgRefNo.trim());
			match.put(FieldType.STATUS.getName(),
					new BasicDBObject("$in", Arrays.asList(StatusType.CAPTURED.getName(), StatusType.SETTLED_SETTLE.getName(),StatusType.SETTLED_RECONCILLED.getName())));


			logger.info("pgRefQuery..." + pgRefNo.trim());
			
			List<BasicDBObject> query=Arrays.asList(new BasicDBObject("$match", match));

			logger.info("finalquery.." + query);

			MongoDatabase dbIns = mongoInstance.getDB();
			MongoCollection<Document> coll = dbIns.getCollection(
					PropertiesManager.propertiesMap.get(prefix + Constants.TRANSACTION_STATUS_COLLECTION.getValue()));
			MongoCursor<Document> capture = coll.aggregate(query).iterator();

			while (capture.hasNext()) {

				doc = capture.next();

			}
			capture.close();
		} catch (Exception e) {
			logger.error("Exception occured in getTxnByPgRefNo", e);
			return null;
		}
		return doc;
	}

	public List<String> getPgRefNosByPayId(String payId) {
		logger.info("merchantPayId..." + payId);
		try {
			List<BasicDBObject> paramConditionLst = new ArrayList<BasicDBObject>();
			BasicDBObject pgRefQuery = new BasicDBObject(FieldType.PAY_ID.getName(), payId);
			paramConditionLst.add(pgRefQuery);

			paramConditionLst.add(new BasicDBObject(FieldType.STATUS.getName(),
					 new BasicDBObject("$in",Arrays.asList(StatusType.SETTLED_SETTLE.getName(),StatusType.CAPTURED.getName(),StatusType.RECONCILED.getName(),StatusType.FAILED.getName()))));
			// paramConditionLst.add(new BasicDBObject(FieldType.STATUS.getName(),
			// StatusType.SETTLED_SETTLE));
			BasicDBObject finalquery = new BasicDBObject("$and", paramConditionLst);

			MongoDatabase dbIns = mongoInstance.getDB();
			MongoCollection<Document> coll = dbIns.getCollection(
					PropertiesManager.propertiesMap.get(prefix + Constants.TRANSACTION_STATUS_COLLECTION.getValue()));
			FindIterable<Document> output = coll.find(finalquery);
			MongoCursor<Document> cursor = output.iterator();
			List<String> pgRefNos = new ArrayList<>();
			while (cursor.hasNext()) {
				Document doc = cursor.next();
				pgRefNos.add(doc.getString(FieldType.PG_REF_NUM.getName()));
			}
			logger.info("pgRefNos...." + pgRefNos);
			cursor.close();
			return pgRefNos;
		} catch (Exception e) {
			logger.error("Exception occured in getTxnByPgRefNo", e);
			return new ArrayList<>();
		}
	}

	public JSONObject getResellerCommsionReport(String dateFrom, String dateTo, String resellerId, String merchantId,
			String paymentType) {

		List<BasicDBObject> paramConditionLst = new ArrayList<BasicDBObject>();
		Resellerdailyupdate resellerData = new Resellerdailyupdate();

		if (resellerId != null && !resellerId.equalsIgnoreCase("ALL")) {
			paramConditionLst.add(new BasicDBObject("resellerId", resellerId));
		}

		if (merchantId != null && !merchantId.equalsIgnoreCase("ALL")) {
			paramConditionLst.add(new BasicDBObject("MERCHANT_ID", merchantId));
		}

		if (paymentType != null && !paymentType.equalsIgnoreCase("ALL")) {
			paymentType = StringUtils.replace(paymentType, "_", " ");
			paymentType = PaymentType.getInstanceIgnoreCase(paymentType).getCode();
			System.out.println("paymentType" + paymentType);
			paramConditionLst.add(new BasicDBObject("paymentType", paymentType));
		}

		paramConditionLst.add(new BasicDBObject("updateDate", new BasicDBObject().append("$gte", dateFrom)));
		paramConditionLst.add(new BasicDBObject("updateDate", new BasicDBObject().append("$lte", dateTo)));

		BasicDBObject finalquery = new BasicDBObject("$and", paramConditionLst);

		MongoDatabase dbIns = mongoInstance.getDB();
		MongoCollection<Document> coll = dbIns.getCollection("Resellerdailyupdate");

		FindIterable<Document> output = coll.find(finalquery);
		MongoCursor<Document> result = output.iterator();

		long totalTxn = 0;
		long totalCCTxn = 0;
		long totalDCTxn = 0;
		long totalUPTxn = 0;
		long totalWLTxn = 0;
		long totalNBTxn = 0;

		double totalCCTxnAmount = 0;
		double totalDCTxnAmount = 0;
		double totalUPTxnAmount = 0;
		double totalWLTxnAmount = 0;
		double totalNBTxnAmount = 0;

		double totalCom = 0;
		double totalCCCom = 0;
		double totalDCCom = 0;
		double totalUPCom = 0;
		double totalWLCom = 0;
		double totalNBCom = 0;
		double TotalTxnAmount = 0;

		while (result.hasNext()) {
			Document document = (Document) result.next();
			System.out.println(document.toJson());
			if (document.getString("paymentType").equalsIgnoreCase("CC")) {
				totalCCTxn++;
				totalCCTxnAmount += document.getDouble("netAmount");
				totalCCTxnAmount = Double.parseDouble(df.format(totalCCTxnAmount));

				totalCCCom += document.getDouble("resellercommision");
				totalCCCom = Double.parseDouble(df.format(totalCCCom));

				logger.info("CC txn amount....={}", totalCCTxnAmount);
				logger.info("totalCCTxn....={}", totalCCTxn);
				logger.info("totalCCCom....={}", totalCCCom);
			}
			if (document.getString("paymentType").equalsIgnoreCase("DC")) {
				totalDCTxn++;
				totalDCTxnAmount += document.getDouble("netAmount");
				totalDCTxnAmount = Double.parseDouble(df.format(totalDCTxnAmount));

				totalDCCom += document.getDouble("resellercommision");
				totalDCCom = Double.parseDouble(df.format(totalDCCom));

				logger.info("DC txn amount....={}", totalDCTxnAmount);
				logger.info("totalDCTxn...={}", totalDCTxn);
				logger.info("totalDCCom...={}", totalDCCom);
			}
			if (document.getString("paymentType").equalsIgnoreCase("WL")) {
				totalWLTxn++;
				totalWLTxnAmount += document.getDouble("netAmount");
				totalWLTxnAmount = Double.parseDouble(df.format(totalWLTxnAmount));

				totalWLCom += document.getDouble("resellercommision");
				totalWLCom = Double.parseDouble(df.format(totalWLCom));

				logger.info("WL txn amount....={}", totalWLTxnAmount);
				logger.info("totalWLTxn...={}", totalWLTxn);
				logger.info("totalWLCom....={}", totalWLCom);
			}
			if (document.getString("paymentType").equalsIgnoreCase("NB")) {

				totalNBTxn++;
				totalNBTxnAmount += document.getDouble("netAmount");
				totalNBTxnAmount = Double.parseDouble(df.format(totalNBTxnAmount));

				totalNBCom += document.getDouble("resellercommision");
				totalNBCom = Double.parseDouble(df.format(totalNBCom));

				logger.info("NB txn amount....={}", totalNBTxnAmount);
				logger.info("totalNBTxn....={}", totalNBTxn);
				logger.info("totalNBCom....={}", totalNBCom);
			}
			if (document.getString("paymentType").equalsIgnoreCase("UP")) {

				totalUPTxn++;
				totalUPTxnAmount += document.getDouble("netAmount");
				totalUPTxnAmount = Double.parseDouble(df.format(totalUPTxnAmount));

				totalUPCom += document.getDouble("resellercommision");
				totalUPCom = Double.parseDouble(df.format(totalUPCom));

				logger.info("totalUPTxnAmount....={}", totalUPTxnAmount);
				logger.info("totalUPTxn....={}", totalUPTxn);
				logger.info("totalUPCom....={}", totalUPCom);
			}

			TotalTxnAmount = totalCCTxnAmount + totalDCTxnAmount + totalWLTxnAmount + totalNBTxnAmount
					+ totalUPTxnAmount;

			TotalTxnAmount = Double.parseDouble(df.format(TotalTxnAmount));

			totalCom = totalCCCom + totalDCCom + totalNBCom + totalUPCom + totalWLCom;
			totalCom = Double.parseDouble(df.format(totalCom));

			totalTxn = totalCCTxn + totalDCTxn + totalNBTxn + totalUPTxn + totalWLTxn;

			logger.info("TotalTxnAmount....={}", TotalTxnAmount);
			logger.info("totalCom....={}", totalCom);
			logger.info("totalTxnCount....={}", totalTxn);

		}

		JSONObject jo = new JSONObject();
		jo.put("totalTxnCount", totalTxn);
		jo.put("totalCCTxn", totalCCTxn);
		jo.put("totalDCTxn", totalDCTxn);
		jo.put("totalNBTxn", totalNBTxn);
		jo.put("totalUPTxn", totalUPTxn);
		jo.put("totalWLTxn", totalWLTxn);

		jo.put("CCTxnAmount", totalCCTxnAmount);
		jo.put("DCTxnAmount", totalDCTxnAmount);
		jo.put("UPTxnAmount", totalUPTxnAmount);
		jo.put("WLTxnAmount", totalWLTxnAmount);
		jo.put("NBTxnAmount", totalNBTxnAmount);

		jo.put("TotalTxnAmount", TotalTxnAmount);
		jo.put("totalCom", totalCom);

		jo.put("totalCCCom", totalCCCom);
		jo.put("totalDCCom", totalDCCom);
		jo.put("totalNBCom", totalNBCom);
		jo.put("totalUPCom", totalUPCom);
		jo.put("totalWLCom", totalWLCom);

		return jo;
	}
	public String holdReleaseTransaction(String pgrefNo,String type,String remarks,String user) {
		logger.info("pgRefNo in get Txn By PgRefNo ()..." + pgrefNo);
		Document doc = null;
		String ret_Str = "Sucesss";
		try {
			List<BasicDBObject> paramConditionLst = new ArrayList<BasicDBObject>();
			BasicDBObject pgRefQuery = new BasicDBObject(FieldType.PG_REF_NUM.getName(), pgrefNo.trim());
			logger.info("pgRefQuery..." + pgRefQuery);
			paramConditionLst.add(pgRefQuery);
			paramConditionLst.add(new BasicDBObject(FieldType.STATUS.getName(), new Document("$in",
					Arrays.asList(StatusType.SETTLED_RECONCILLED.getName(), StatusType.SETTLED_SETTLE.getName(),StatusType.CAPTURED.getName()))));
			BasicDBObject finalquery = new BasicDBObject("$and", paramConditionLst);
			logger.info("finalquery.." + finalquery);

			MongoDatabase dbIns = mongoInstance.getDB();
			MongoCollection<Document> coll = dbIns.getCollection(
					PropertiesManager.propertiesMap.get(prefix + Constants.TRANSACTION_STATUS_COLLECTION.getValue()));
			FindIterable<Document> output = coll.find(finalquery);
			MongoCursor<Document> cursor = output.iterator();
			while (cursor.hasNext()) {
				doc = cursor.next();
				if(type.equalsIgnoreCase("HOLD")) {
					doc.put(FieldType.HOLD_RELEASE.getName(), 1);
					doc.put(FieldType.HOLD_DATE.getName(),new java.util.Date());
					doc.put(FieldType.LIABILITYHOLDREMARKS.getName(),remarks );
					doc.put(FieldType.UPDATEDBY.getName(),user );
					doc.put(FieldType.UPDATEDAT.getName(), new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new java.util.Date()));


					
				}else if(type.equalsIgnoreCase("RELEASE")) { 
					doc.put(FieldType.HOLD_RELEASE.getName(), 0);
					doc.put(FieldType.RELEASE_DATE.getName(),new java.util.Date());
					doc.put(FieldType.LIABILITYRELEASEREMARKS.getName(),remarks );
					doc.put(FieldType.UPDATEDBY.getName(),user );
					doc.put(FieldType.UPDATEDAT.getName(), new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new java.util.Date()));
				}
				
				coll.updateOne(finalquery, new BasicDBObject("$set", doc));
				holdReleaseTransactionStatus(pgrefNo,type,remarks,user);
				ret_Str = "sucesss";
			}
			cursor.close();

		} catch (Exception e) {
			logger.error("Exception occured in holdReleaseTransaction", e);
			ret_Str = "Failure";
		}
		return ret_Str;
	}
	public String holdReleaseTransactionStatus(String pgrefNo,String type,String remarks,String user) {
		logger.info("pgRefNo in get Txn By PgRefNo ()..." + pgrefNo);
		Document doc = null;
		String ret_Str = "Sucesss";
		try {
			List<BasicDBObject> paramConditionLst = new ArrayList<BasicDBObject>();
			BasicDBObject pgRefQuery = new BasicDBObject(FieldType.PG_REF_NUM.getName(), pgrefNo.trim());
			logger.info("pgRefQuery..." + pgRefQuery);
			paramConditionLst.add(pgRefQuery);
			paramConditionLst.add(new BasicDBObject(FieldType.STATUS.getName(), new Document("$in",
					Arrays.asList(StatusType.SETTLED_RECONCILLED.getName(), StatusType.SETTLED_SETTLE.getName(),StatusType.CAPTURED.getName()))));
			BasicDBObject finalquery = new BasicDBObject("$and", paramConditionLst);
			logger.info("finalquery.." + finalquery);

			MongoDatabase dbIns = mongoInstance.getDB();
			MongoCollection<Document> coll = dbIns.getCollection(
					PropertiesManager.propertiesMap.get(prefix + Constants.COLLECTION_NAME.getValue()));
			FindIterable<Document> output = coll.find(finalquery);
			MongoCursor<Document> cursor = output.iterator();
			while (cursor.hasNext()) {
				doc = cursor.next();
				if(type.equalsIgnoreCase("HOLD")) {
					doc.put(FieldType.HOLD_RELEASE.getName(), 1);
					doc.put(FieldType.HOLD_DATE.getName(),new java.util.Date());
					doc.put(FieldType.LIABILITYHOLDREMARKS.getName(),remarks );
					doc.put(FieldType.UPDATEDBY.getName(),user );
					doc.put(FieldType.UPDATEDAT.getName(), new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").format(new java.util.Date()));
					
				}else if(type.equalsIgnoreCase("RELEASE")) { 
					doc.put(FieldType.HOLD_RELEASE.getName(), 0);
					doc.put(FieldType.RELEASE_DATE.getName(),new java.util.Date());
					doc.put(FieldType.LIABILITYRELEASEREMARKS.getName(),remarks );
					doc.put(FieldType.UPDATEDBY.getName(),user );
					doc.put(FieldType.UPDATEDAT.getName(), new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").format(new java.util.Date()));
				}
				coll.updateOne(finalquery, new BasicDBObject("$set", doc));
			}
			cursor.close();

		} catch (Exception e) {
			logger.error("Exception occured in holdReleaseTransactionStatus", e);
			ret_Str = "Failure";
		}
		return ret_Str;
	}
	public String creatNewEntryInTransactionStatusCBReversal(String pgrefNo,java.util.Date date) {
		logger.info("pgRefNo in get Txn By PgRefNo ()..." + pgrefNo);
		Document doc = null;
		String ret_Str = "Sucesss";
		try {
			List<BasicDBObject> paramConditionLst = new ArrayList<BasicDBObject>();
			BasicDBObject pgRefQuery = new BasicDBObject(FieldType.PG_REF_NUM.getName(), pgrefNo.trim());
			logger.info("pgRefQuery..." + pgRefQuery);
			paramConditionLst.add(pgRefQuery);
			paramConditionLst.add(new BasicDBObject(FieldType.STATUS.getName(), StatusType.CHARGEBACK_INITIATED.getName()));
			
//			paramConditionLst.add(new BasicDBObject("$sort", 
//				    new BasicDBObject("CREATE_DATE", -1L)));
//			
//			paramConditionLst.add(new BasicDBObject("$limit", 1L));
//		    
			
			BasicDBObject finalquery = new BasicDBObject("$and", paramConditionLst);
			logger.info("finalquery.." + finalquery);
			MongoDatabase dbIns = mongoInstance.getDB();
			MongoCollection<Document> coll = dbIns.getCollection(
					PropertiesManager.propertiesMap.get(prefix + Constants.TRANSACTION_STATUS_COLLECTION.getValue()));
			FindIterable<Document> output = coll.find(finalquery);
			MongoCursor<Document> cursor = output.iterator();
			if (cursor.hasNext()) {
				doc = cursor.next();
				String newTxnId1 = TransactionManager.getNewTransactionId();
				doc.put("_id", newTxnId1);
				doc.put(FieldType.TXNTYPE.getName(), TransactionType.CHARGEBACK.getName());
				doc.put(FieldType.STATUS.getName(), StatusType.CHARGEBACK_REVERSAL.getName());
				doc.put(FieldType.ALIAS_STATUS.getName(), StatusType.CHARGEBACK_REVERSAL.getName());
				DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
				String strDate = dateFormat.format(date);
				doc.put(FieldType.CREATE_DATE.getName(), strDate);
				doc.put(FieldType.DATE_INDEX.getName(), strDate.toString().replaceAll("-", ""));
//				String id = TransactionManager.getNewTransactionId();
//				doc.put("_id", id);
				String newTxnId = TransactionManager.getNewTransactionId();
				doc.put("TXN_ID", newTxnId);
				Document document = new Document(doc);
				//coll.updateMany(finalquery, new BasicDBObject("$set", document));
				coll.insertOne(document);
				logger.info("calling update method of tran");
				creatNewEntryInTransactionCBReversal(pgrefNo, date);
			}
			cursor.close();

		} catch (Exception e) {
			logger.error("Exception occured in creatNewEntryInTransactionStatusCB", e);
			ret_Str = "Failure";
		}
		return ret_Str;
	}
	public String creatNewEntryInTransactionStatusCBInitiated(String pgrefNo,String cbDate) {
		logger.info("pgRefNo in get Txn By PgRefNo ()..." + pgrefNo);
		Document doc = null;
		String ret_Str = "Sucesss";
		try {
			List<BasicDBObject> paramConditionLst = new ArrayList<BasicDBObject>();
			BasicDBObject pgRefQuery = new BasicDBObject(FieldType.PG_REF_NUM.getName(), pgrefNo.trim());
			logger.info("pgRefQuery..." + pgRefQuery);
			paramConditionLst.add(pgRefQuery);
			paramConditionLst.add(new BasicDBObject(FieldType.STATUS.getName(), new Document("$in",
					Arrays.asList(StatusType.SETTLED_RECONCILLED.getName(), StatusType.SETTLED_SETTLE.getName(), StatusType.CAPTURED.getName()))));
			
//			paramConditionLst.add(new BasicDBObject("$sort", 
//				    new BasicDBObject("CREATE_DATE", -1L)));
//			
//			paramConditionLst.add(new BasicDBObject("$limit", 1L));
//		    
			
			BasicDBObject finalquery = new BasicDBObject("$and", paramConditionLst);
			logger.info("finalquery.." + finalquery);

			MongoDatabase dbIns = mongoInstance.getDB();
			MongoCollection<Document> coll = dbIns.getCollection(
					PropertiesManager.propertiesMap.get(prefix + Constants.TRANSACTION_STATUS_COLLECTION.getValue()));
			FindIterable<Document> output = coll.find(finalquery);
			MongoCursor<Document> cursor = output.iterator();
			if (cursor.hasNext()) {
				doc = cursor.next();
				doc.put(FieldType.TXNTYPE.getName(), TransactionType.CHARGEBACK.getName());
				doc.put(FieldType.STATUS.getName(), StatusType.CHARGEBACK_INITIATED.getName());
				doc.put(FieldType.ALIAS_STATUS.getName(), StatusType.CHARGEBACK_INITIATED.getName());
				//DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
				String strDate = dateFormat.format(Date.valueOf(cbDate));
				
				java.util.Date date=new java.util.Date();
				doc.put(FieldType.CREATE_DATE.getName(), dateFormat.format(date));
				doc.put(FieldType.DATE_INDEX.getName(), new SimpleDateFormat("yyyyMMdd").format(date));
				
				String newTxnId = TransactionManager.getNewTransactionId();
				doc.put("TXN_ID", newTxnId);
				
				
				String id = TransactionManager.getNewTransactionId();
				doc.put("_id", id);
				
				Document document = new Document(doc);
				coll.insertOne(document);
				//coll.updateMany(finalquery, new BasicDBObject("$set", document));
				logger.info("calling update method of tran");
				creatNewEntryInTransactionCBInitiated(pgrefNo, cbDate);
			}
			cursor.close();

		} catch (Exception e) {
			logger.error("Exception occured in creatNewEntryInTransactionStatusCB", e);
			ret_Str = "Failure";
		}
		return ret_Str;
	}
	private String creatNewEntryInTransactionCBReversal(String pgrefNo, java.util.Date date) {
		logger.info("pgRefNo in get Txn By PgRefNo ()..." + pgrefNo);
		Document doc = null;
		DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
		String ret_Str = "Sucesss";
		try {
			List<BasicDBObject> paramConditionLst = new ArrayList<BasicDBObject>();
			BasicDBObject pgRefQuery = new BasicDBObject(FieldType.PG_REF_NUM.getName(), pgrefNo.trim());
			logger.info("pgRefQuery..." + pgRefQuery);
			paramConditionLst.add(pgRefQuery);
//			paramConditionLst
//					.add(new BasicDBObject(FieldType.STATUS.getName(), StatusType.SETTLED_RECONCILLED.getName()));
			paramConditionLst.add(new BasicDBObject(FieldType.STATUS.getName(), StatusType.CHARGEBACK_INITIATED.getName()));
//			paramConditionLst.add(new BasicDBObject("$sort", 
//				    new BasicDBObject("CREATE_DATE", -1L)));
//			paramConditionLst.add(new BasicDBObject("$limit", 1L));
			BasicDBObject finalquery = new BasicDBObject("$and", paramConditionLst);
			logger.info("finalquery.." + finalquery);

			
			
			
			MongoDatabase dbIns = mongoInstance.getDB();
			MongoCollection<Document> coll = dbIns.getCollection(
					PropertiesManager.propertiesMap.get(prefix + Constants.COLLECTION_NAME.getValue()));
			FindIterable<Document> output = coll.find(finalquery);
			MongoCursor<Document> cursor = output.iterator();
			
//            MongoDatabase dbIns = mongoInstance.getDB();
//            MongoCollection<Document> coll = dbIns.getCollection(
//                    PropertiesManager.propertiesMap.get(prefix + Constants.COLLECTION_NAME.getValue()));
//            FindIterable<Document> output = coll.find(finalquery);
//            MongoCursor<Document> cursor = output.iterator();
//            
					while (cursor.hasNext()) {
						doc = cursor.next();
						doc.put(FieldType.TXNTYPE.getName(), TransactionType.CHARGEBACK.getName());
						doc.put(FieldType.STATUS.getName(), StatusType.CHARGEBACK_REVERSAL.getName());
						String strDate = dateFormat.format(date);
						doc.put(FieldType.CREATE_DATE.getName(), strDate);
						doc.put(FieldType.DATE_INDEX.getName(), strDate.toString().replaceAll("-", ""));
						String id = TransactionManager.getNewTransactionId();
						doc.put("_id", id);
						String newTxnId = TransactionManager.getNewTransactionId();
						doc.put("TXN_ID", newTxnId);
						Document document = new Document(doc);
						coll.insertOne(document);
						
					}
			cursor.close();

		} catch (Exception e) {
			logger.error("Exception occured in creatNewEntryInTransactionCB", e);
			ret_Str = "Failure";
		}
		return ret_Str;
	}
	
	DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	private String creatNewEntryInTransactionCBInitiated(String pgrefNo, String cbDate) {
		logger.info("pgRefNo in get Txn By PgRefNo ()..." + pgrefNo);
		Document doc = null;
		//DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
		String ret_Str = "Sucesss";
		try {
			List<BasicDBObject> paramConditionLst = new ArrayList<BasicDBObject>();
			BasicDBObject pgRefQuery = new BasicDBObject(FieldType.PG_REF_NUM.getName(), pgrefNo.trim());
			logger.info("pgRefQuery..." + pgRefQuery);
			paramConditionLst.add(pgRefQuery);
//			paramConditionLst
//					.add(new BasicDBObject(FieldType.STATUS.getName(), StatusType.SETTLED_RECONCILLED.getName()));
			paramConditionLst.add(new BasicDBObject(FieldType.STATUS.getName(), new Document("$in",
					Arrays.asList(StatusType.SETTLED_RECONCILLED.getName(), StatusType.SETTLED_SETTLE.getName(), StatusType.CAPTURED.getName()))));
//			paramConditionLst.add(new BasicDBObject("$sort", 
//				    new BasicDBObject("CREATE_DATE", -1L)));
//			paramConditionLst.add(new BasicDBObject("$limit", 1L));
			BasicDBObject finalquery = new BasicDBObject("$and", paramConditionLst);
			logger.info("finalquery.." + finalquery);

            MongoDatabase dbIns = mongoInstance.getDB();
            MongoCollection<Document> coll = dbIns.getCollection(
                    PropertiesManager.propertiesMap.get(prefix + Constants.COLLECTION_NAME.getValue()));
            FindIterable<Document> output = coll.find(finalquery);
            MongoCursor<Document> cursor = output.iterator();

					if (cursor.hasNext()) {
						
						doc = cursor.next();
						doc.put(FieldType.TXNTYPE.getName(), TransactionType.CHARGEBACK.getName());
						doc.put(FieldType.STATUS.getName(), StatusType.CHARGEBACK_INITIATED.getName());
						String strDate = dateFormat.format(Date.valueOf(cbDate));
						java.util.Date date=new java.util.Date();
						doc.put(FieldType.CREATE_DATE.getName(), dateFormat.format(date));
						doc.put(FieldType.DATE_INDEX.getName(), new SimpleDateFormat("yyyyMMdd").format(date));
						String id = TransactionManager.getNewTransactionId();
						doc.put("_id", id);
						String newTxnId = TransactionManager.getNewTransactionId();
						doc.put("TXN_ID", newTxnId);
						Document document = new Document(doc);
						coll.insertOne(document);

					}
			cursor.close();

		} catch (Exception e) {
			logger.error("Exception occured in creatNewEntryInTransactionCB", e);
			ret_Str = "Failure";
		}
		return ret_Str;
	}
}
