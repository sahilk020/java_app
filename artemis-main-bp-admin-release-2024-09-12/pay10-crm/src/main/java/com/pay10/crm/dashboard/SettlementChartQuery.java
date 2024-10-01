package com.pay10.crm.dashboard;
 
import java.text.SimpleDateFormat;
 
import java.util.ArrayList;
import java.util.Arrays;
 
 
import java.util.List;
 
 
import org.bson.Document;
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
import com.pay10.commons.user.TransactionSearch;
import com.pay10.commons.user.User;
import com.pay10.commons.user.UserDao;
import com.pay10.commons.user.UserType;
import com.pay10.commons.util.Constants;
import com.pay10.commons.util.FieldType;
import com.pay10.commons.util.PropertiesManager;
/**
 * Rohit , Rajendra 
 */
@Component
public class SettlementChartQuery {
	private static Logger logger = LoggerFactory.getLogger(SettlementChartQuery.class.getName());
	 
	private static final String prefix = "MONGO_DB_";
	@Autowired
	private MongoInstance mongoInstance;
	@Autowired
	PropertiesManager propertiesManager;
	@Autowired
	UserDao userDao;
	
	public List<SettlementChart> getSettledValues(String payId, String currency, String dateFrom, String dateTo,String paymentType,String acquirer,String transactionType,String mopType,User user) {
		List<SettlementChart> transactionList = new ArrayList<SettlementChart>();
		
		try {
			
			MongoDatabase dbIns = mongoInstance.getDB();
			MongoCollection<Document> coll = dbIns
					.getCollection(PropertiesManager.propertiesMap.get(prefix + Constants.TRANSACTION_STATUS_COLLECTION.getValue()));
			
			BasicDBObject settlmentDateQuery = new BasicDBObject();
			BasicDBObject settlmentDateQuery2 = new BasicDBObject();
			BasicDBObject settlmentDateQuery3 = new BasicDBObject();
			BasicDBObject settlmentDateQuery4 = new BasicDBObject();
			
			List<BasicDBObject> allConditionQueryList = new ArrayList<BasicDBObject>();
			BasicDBObject acquirerQuery = new BasicDBObject();
			List<BasicDBObject> acquirerConditionLst = new ArrayList<BasicDBObject>();
			List<BasicDBObject> mopTypeConditionLst = new ArrayList<BasicDBObject>();
			BasicDBObject mopTypeQuery = new BasicDBObject();
			List<BasicDBObject> paymentTypeConditionLst = new ArrayList<BasicDBObject>();
			BasicDBObject paymentTypeQuery = new BasicDBObject();
			
			settlmentDateQuery.put(FieldType.CREATE_DATE.getName(),
					BasicDBObjectBuilder.start("$gte", new SimpleDateFormat(dateFrom).toLocalizedPattern())
							.add("$lte", new SimpleDateFormat(dateTo).toLocalizedPattern()).get());
			settlmentDateQuery.put(FieldType.STATUS.getName(),"Settled");
			
			/*
			 * if(!payId.isEmpty()) {
			 * settlmentDateQuery.put(FieldType.PAY_ID.getName(),payId); }
			 */
			
			if (payId.isEmpty()) {
				List<String> payIdLst = userDao.getPayIdForSplitPaymentMerchant(user.getSegment());
				logger.info("Get PayId For SplitPayment Merchant : " + payIdLst);
				if (payIdLst.size() > 0) {
					settlmentDateQuery.put(FieldType.PAY_ID.getName(), new BasicDBObject("$in", payIdLst));
				}
			} else {
				settlmentDateQuery.put(FieldType.PAY_ID.getName(), payId);
			}
			
			if(user.getUserType().equals(UserType.RESELLER)) {
				settlmentDateQuery.put(FieldType.RESELLER_ID.getName(),user.getResellerId());
			}
			
			if(!currency.isEmpty()) {
				settlmentDateQuery.put(FieldType.CURRENCY_CODE.getName(),currency);
			}
			
			if (!acquirer.equalsIgnoreCase("ALL")) {
				List<String> acquirerList = Arrays.asList(acquirer.split(","));
				for (String acq : acquirerList) {
					acquirerConditionLst.add(new BasicDBObject(FieldType.ACQUIRER_TYPE.getName(), acq.trim()));
				}
				acquirerQuery.append("$or", acquirerConditionLst);

			}
			
			if (!paymentType.equalsIgnoreCase("ALL")) {
				List<String> paymentTypeList = Arrays.asList(paymentType.split(","));
				for (String payment : paymentTypeList) {
					paymentTypeConditionLst.add(new BasicDBObject(FieldType.PAYMENT_TYPE.getName(), payment.trim()));
				}
				paymentTypeQuery.append("$or", paymentTypeConditionLst);
			}
			
			if (!acquirerQuery.isEmpty()) {
				allConditionQueryList.add(acquirerQuery);
			}

			if (!paymentTypeQuery.isEmpty()) {
				allConditionQueryList.add(paymentTypeQuery);
			}
			if(!settlmentDateQuery.isEmpty()) {
				allConditionQueryList.add(settlmentDateQuery);
			}
			
			settlmentDateQuery2.put("_id","$CREATE_DATE");
			settlmentDateQuery3.put("$toDouble","$AMOUNT");
			settlmentDateQuery2.put(FieldType.AMOUNT.getName(),new BasicDBObject("$sum", settlmentDateQuery3));
			settlmentDateQuery4.put("_id",1);
		 
			BasicDBObject allConditions = new BasicDBObject("$and", allConditionQueryList);
			
			BasicDBObject match = new BasicDBObject("$match", allConditions);
			BasicDBObject group = new BasicDBObject("$group", settlmentDateQuery2);
			BasicDBObject sort = new BasicDBObject("$sort", settlmentDateQuery4);
			
			List<BasicDBObject> pipeline = Arrays.asList(match, group, sort);
			AggregateIterable<Document> output = coll.aggregate(pipeline);
			output.allowDiskUse(true);
			MongoCursor<Document> cursor = output.iterator();
			 
			 try {
				 
		            while(cursor.hasNext()) {               
		            	Document doc = cursor.next();
		            	SettlementChart SettlementReport = new SettlementChart();
						SettlementReport.setCreateDate(doc.getString("_id"));
						SettlementReport.setSettledAmount(doc.getDouble(FieldType.AMOUNT.toString()));
						transactionList.add(SettlementReport);
		            }
		        } finally {
		            cursor.close();
		        }
			 
			return transactionList;	
		}catch (Exception exception) {
			logger.error("Exception", exception); 
			return null;
		}
		
		
	}

 
}