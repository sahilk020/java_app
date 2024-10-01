package com.pay10.commons.dao;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

import com.mongodb.client.result.DeleteResult;
import org.apache.commons.lang3.StringUtils;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.mongodb.BasicDBObject;
import com.mongodb.BasicDBObjectBuilder;
import com.mongodb.client.AggregateIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import com.pay10.commons.mongo.MongoInstance;
import com.pay10.commons.user.MultCurrencyCode;
import com.pay10.commons.user.Resellerdailyupdate;
import com.pay10.commons.user.UserDao;
import com.pay10.commons.util.Constants;
import com.pay10.commons.util.CrmFieldType;
import com.pay10.commons.util.FieldType;
import com.pay10.commons.util.MopType;
import com.pay10.commons.util.PaymentType;
import com.pay10.commons.util.PropertiesManager;
import com.pay10.commons.util.ResellerdailyupdateUtilities;

@Component
public class Resellerdailyupdatedao {
	@Autowired
	private MongoInstance mongoInstance;

	@Autowired
	UserDao userdao;

	@Autowired
	ResellerDao resellerdao;

	private static final String PREFIX = "MONGO_DB_";
	private static Logger logger = LoggerFactory.getLogger(Resellerdailyupdatedao.class.getName());
	private static final DecimalFormat df = new DecimalFormat("0.00");

	public void create(Resellerdailyupdate resellerdailyupdate) {

		Document doc = ResellerdailyupdateUtilities.getDocFromresellerdailyupdate(resellerdailyupdate);
		Document deleteDoc = ResellerdailyupdateUtilities.getDocFromresellerdailyupdateForDelete(resellerdailyupdate);
		logger.info("create:: doc={}", doc);
		MongoDatabase dbIns = mongoInstance.getDB();
		MongoCollection<Document> collection = dbIns.getCollection("Resellerdailyupdate");
		DeleteResult deleteResult = collection.deleteMany(deleteDoc);
		logger.info("Deleted Old Data: {}", deleteResult.getDeletedCount());
		collection.insertOne(doc);
	}

	public List<Document> getByPayId(String payId, String fromDate, String toDate) {
		BasicDBObject dateTimeQuery = new BasicDBObject();
		BasicDBObject txnStatusQuery = new BasicDBObject();
		dateTimeQuery.put(FieldType.CREATE_DATE.getName(),
				BasicDBObjectBuilder.start("$gte", fromDate).add("$lte", toDate).get());
		txnStatusQuery.put(FieldType.STATUS.getName(), "Captured");
		List<BasicDBObject> allConditionQueryList = new ArrayList<BasicDBObject>();
		BasicDBObject merchantQuery = new BasicDBObject(FieldType.PAY_ID.getName(), payId);
		allConditionQueryList.add(dateTimeQuery);
		allConditionQueryList.add(txnStatusQuery);
		allConditionQueryList.add(merchantQuery);
		BasicDBObject finalquery = new BasicDBObject("$and", allConditionQueryList);
		logger.info("Query to get data by PayId finalQuery= {}", finalquery);
		MongoDatabase dbIns = mongoInstance.getDB();
		MongoCollection<Document> collection = dbIns
				.getCollection(PropertiesManager.propertiesMap.get(PREFIX + Constants.COLLECTION_NAME.getValue()));
		BasicDBObject match = new BasicDBObject("$match", finalquery);
		List<BasicDBObject> pipeline = Arrays.asList(match);
		AggregateIterable<Document> output = collection.aggregate(pipeline);
		output.allowDiskUse(true);
		MongoCursor<Document> cursor = output.iterator();
		List<Document> docs = new ArrayList<>();
		while (cursor.hasNext()) {
			docs.add(cursor.next());
		}
		return docs;
	}

	public List<Document> getForPayoutByResellerAndDate(String resellerPayId, String fromDate, String toDate,MultCurrencyCode currency) {
		BasicDBObject dateTimeQuery = new BasicDBObject();
		dateTimeQuery.put("updateDate", BasicDBObjectBuilder.start("$gte", fromDate).add("$lte", toDate).get());
		List<BasicDBObject> allConditionQueryList = new ArrayList<BasicDBObject>();
		BasicDBObject smaQuery = new BasicDBObject("smaId", resellerPayId);
		BasicDBObject maQuery = new BasicDBObject("maId", resellerPayId);
		BasicDBObject agentQuery = new BasicDBObject("agentId", resellerPayId);
		BasicDBObject currencyQuery = new BasicDBObject("currency", currency.getCode());
		List<BasicDBObject> orConditionQuery = new ArrayList<>();
		orConditionQuery.add(smaQuery);
		orConditionQuery.add(maQuery);
		orConditionQuery.add(agentQuery);
		BasicDBObject payIdQuery = new BasicDBObject("$or", orConditionQuery);
		allConditionQueryList.add(dateTimeQuery);
		//allConditionQueryList.add(merchantQuery);
		allConditionQueryList.add(payIdQuery);
		allConditionQueryList.add(currencyQuery);
		BasicDBObject finalquery = new BasicDBObject("$and", allConditionQueryList);
		logger.info("Query to get data by PayId finalQuery={}", finalquery);
		MongoDatabase dbIns = mongoInstance.getDB();
		MongoCollection<Document> collection = dbIns.getCollection("Resellerdailyupdate");
		BasicDBObject match = new BasicDBObject("$match", finalquery);
		List<BasicDBObject> pipeline = Arrays.asList(match);
		AggregateIterable<Document> output = collection.aggregate(pipeline);
		output.allowDiskUse(true);
		MongoCursor<Document> cursor = output.iterator();
		List<Document> docs = new ArrayList<>();
		while (cursor.hasNext()) {
			docs.add(cursor.next());
		}
		return docs;
	}

	public List<Resellerdailyupdate> getResellerDailyUpdate(String resellerId, String merchantname, String paymentType,
			String mopType,  String dateFrom, String dateTo, String currency,String userType) {

		logger.info(
				"getResellerDailyUpdate:: resellerId={}, merchantName={}, paymentType={}, mopType={}, dateFrom={}, dateTo={},currency={}",
				resellerId, merchantname, paymentType, mopType, dateFrom, dateTo,currency);

		try {
			List<Resellerdailyupdate> transactionList = new ArrayList<Resellerdailyupdate>();
			List<BasicDBObject> dateCondition = new ArrayList<BasicDBObject>();
			List<BasicDBObject> paramConditionLst = new ArrayList<BasicDBObject>();
			BasicDBObject dateQuery = new BasicDBObject();
			BasicDBObject allParamQuery = new BasicDBObject();
			String currentDate = null;
			if (!dateFrom.isEmpty()) {

				if (!dateTo.isEmpty()) {
					currentDate = dateTo;
				} else {
					DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
					Calendar cal = Calendar.getInstance();
					currentDate = dateFormat.format(cal.getTime());
				}
				dateCondition.add(new BasicDBObject("updateDate",
						BasicDBObjectBuilder.start("$gte", new SimpleDateFormat(dateFrom).toLocalizedPattern())
								.add("$lte", new SimpleDateFormat(currentDate).toLocalizedPattern()).get()));

				dateQuery.put("$or", dateCondition);
			}

			BasicDBObject smaQuery = new BasicDBObject("smaId", resellerId);
			BasicDBObject maQuery = new BasicDBObject("maId", resellerId);
			BasicDBObject agentQuery = new BasicDBObject("agentId", resellerId);
			
			List<BasicDBObject> orConditionQuery = new ArrayList<>();
			orConditionQuery.add(smaQuery);
			orConditionQuery.add(maQuery);
			orConditionQuery.add(agentQuery);
			BasicDBObject payIdQuery = new BasicDBObject("$or", orConditionQuery);
			paramConditionLst.add(payIdQuery);

			if (StringUtils.isNotBlank(merchantname)) {
				paramConditionLst.add(new BasicDBObject("MERCHANT_ID", merchantname));
			}
			if (StringUtils.isNotBlank(paymentType)) {
				paymentType = StringUtils.replace(paymentType, "_", " ");
				paymentType = PaymentType.getInstanceIgnoreCase(paymentType).getCode();
				paramConditionLst.add(new BasicDBObject("paymentType", paymentType));
			}
			if (StringUtils.isNotBlank(mopType)) {
				//mopType = StringUtils.replace(mopType, "_", " ");
				mopType = MopType.getInstanceUsingStringValue1(mopType).getCode();
				paramConditionLst.add(new BasicDBObject("mop type", mopType));
			}
			if (StringUtils.isNotBlank(currency)) {
				paramConditionLst.add(new BasicDBObject("currency", currency));
			}
			if (!paramConditionLst.isEmpty()) {
				allParamQuery = new BasicDBObject("$and", paramConditionLst);
			}

			List<BasicDBObject> allConditionQueryList = new ArrayList<BasicDBObject>();

			if (!dateQuery.isEmpty()) {
				allConditionQueryList.add(dateQuery);
			}

			List<BasicDBObject> fianlList = new ArrayList<BasicDBObject>();

			if (!allParamQuery.isEmpty()) {
				fianlList.add(allParamQuery);
			}

			if (!allConditionQueryList.isEmpty()) {
				BasicDBObject allConditionQueryObj = new BasicDBObject("$and", allConditionQueryList);
				if (!allConditionQueryObj.isEmpty()) {
					fianlList.add(allConditionQueryObj);
				}
			}

			BasicDBObject finalquery = new BasicDBObject("$and", fianlList);

			logger.info("Inside Reseller Daily update , finalquery = {}", finalquery);
			MongoDatabase dbIns = mongoInstance.getDB();
			MongoCollection<Document> coll = dbIns.getCollection("Resellerdailyupdate");
			BasicDBObject match = new BasicDBObject("$match", finalquery);
			List<Bson> pipeline = Arrays.asList(match);

			AggregateIterable<Document> output = coll.aggregate(pipeline);
			output.allowDiskUse(true);
			MongoCursor<Document> cursor = output.iterator();

			while (cursor.hasNext()) {

				Document dbobj = cursor.next();

				Resellerdailyupdate resellerReport = new Resellerdailyupdate();
				
				resellerReport.setMerchant_payId(dbobj.getString("MERCHANT_ID"));
				String mop=MopType.getmopName(dbobj.getString("mop type"));
				String paytype=PaymentType.getpaymentName(dbobj.getString("paymentType"));
				resellerReport.setMOP(mop);
				resellerReport.setTransType(paytype);
				//Double commission = dbobj.getDouble(CrmFieldType.RESELLER_COMMISION.getName());
				//commission= commission.parseDouble(df.format(commission));
				//logger.info("commission...={}",commission);
				//resellerReport.setCommisionamount(commission);
				Double smacommission = dbobj.getDouble(CrmFieldType.SMA_COMMISSION.getName());
				smacommission= smacommission.parseDouble(df.format(smacommission));
				
				Double macommission = dbobj.getDouble(CrmFieldType.MA_COMMISSION.getName());
				macommission= macommission.parseDouble(df.format(macommission));
				
				Double agentcommission = dbobj.getDouble(CrmFieldType.AGENT_COMMISSION.getName());
				agentcommission= agentcommission.parseDouble(df.format(agentcommission));
				if(userType.equalsIgnoreCase("SMA")) {
				resellerReport.setReseller_payId(dbobj.getString("smaId"));
				resellerReport.setCommisionamount(smacommission);
				}
				if(userType.equalsIgnoreCase("MA")) {
					resellerReport.setReseller_payId(dbobj.getString("maId"));
					resellerReport.setCommisionamount(macommission);
					}
				if(userType.equals("Agent")) {
					resellerReport.setReseller_payId(dbobj.getString("agentId"));
					resellerReport.setCommisionamount(agentcommission);
					}
				resellerReport.setSMA_payId(dbobj.getString("smaId"));
				resellerReport.setMA_payId(dbobj.getString("maId"));
				resellerReport.setAgent_payId(dbobj.getString("agentId"));
				
				//resellerdailyupdate.setCommisionamount((double) commissionDetails.getOrDefault("commissionAmount", 0));
				resellerReport.setSmacommisionamount(smacommission);
				resellerReport.setMacommisionamount(macommission);
				resellerReport.setAgentcommisionamount(agentcommission);
				resellerReport.setSaleamount(dbobj.getDouble(CrmFieldType.SALE_AMOUNT.getName()));
				resellerReport.setTotalChargeback(dbobj.getDouble(CrmFieldType.TOTAL_CHARGEBACK.getName()));
				resellerReport.setTotalRefund(dbobj.getDouble(CrmFieldType.TOTAL_REFUND.getName()));
				resellerReport.setAmount(dbobj.getDouble("netAmount"));
				resellerReport.setTransDate(dbobj.getString(CrmFieldType.TXN_DATE.getName()));
				transactionList.add(resellerReport);
			}
			cursor.close();
			logger.info("getResellerDailyUpdate:: transactionListSize = {}", transactionList.size());
			return transactionList;
		} catch (Exception e) {
			logger.error("getResellerDailyUpdate:: failed, Exception:", e);
			return null;
		}
	}

}