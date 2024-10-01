package com.pay10.notification.sms.smsCreater;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.bson.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.mongodb.BasicDBObject;
import com.mongodb.BasicDBObjectBuilder;
import com.mongodb.client.AggregateIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import com.pay10.commons.dao.ServiceTaxDao;
import com.pay10.commons.dao.SettledTransactionDataDao;
import com.pay10.commons.mongo.MongoInstance;
import com.pay10.commons.user.AccountCurrencyRegion;
import com.pay10.commons.user.CardHolderType;
import com.pay10.commons.user.Merchants;
import com.pay10.commons.user.ServiceTax;
import com.pay10.commons.user.SettledTransactionDataObject;
import com.pay10.commons.user.Surcharge;
import com.pay10.commons.user.SurchargeDao;
import com.pay10.commons.user.User;
import com.pay10.commons.user.UserDao;
import com.pay10.commons.util.AcquirerType;
import com.pay10.commons.util.Constants;
import com.pay10.commons.util.FieldType;
import com.pay10.commons.util.PropertiesManager;
import com.pay10.commons.util.StatusType;
import com.pay10.commons.util.TransactionManager;
import com.pay10.commons.util.TransactionType;

@Service
public class UpdateSettlementDataService {

	@Autowired
	private MongoInstance mongoInstance;

	@Autowired
	PropertiesManager propertiesManager;
	
	@Autowired
	private SurchargeDao surchargeDao;
	
	@Autowired
	private UserDao userdao;
	
	@Autowired
	private ServiceTaxDao serviceTaxDao;
	
	@Autowired
	private SettledTransactionDataDao settledTransactionDataDao;
	
	private static Logger logger = LoggerFactory.getLogger(UpdateSettlementDataService.class.getName());
	private static final String prefix = "MONGO_DB_";

	public void updateSettledData(String fromDate, String toDate) {

		logger.info("Inside update settlement date for captured transactions ");
		try {

			BasicDBObject dateQuery = new BasicDBObject();
			List<BasicDBObject> paramConditionLst = new ArrayList<BasicDBObject>();

			BasicDBObject allParamQuery = new BasicDBObject();
			List<BasicDBObject> saleOrAuthList = new ArrayList<BasicDBObject>();

			if (!fromDate.isEmpty()) {

				String currentDate = null;
				if (!toDate.isEmpty()) {
					currentDate = toDate;
				} else {
					DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
					Calendar cal = Calendar.getInstance();
					currentDate = dateFormat.format(cal.getTime());
				}

				dateQuery.put(FieldType.CREATE_DATE.getName(),
						BasicDBObjectBuilder.start("$gte", new SimpleDateFormat(fromDate).toLocalizedPattern())
								.add("$lte", new SimpleDateFormat(currentDate).toLocalizedPattern()).get());
			}


			

			List<BasicDBObject> dateIndexConditionList = new ArrayList<BasicDBObject>();
			BasicDBObject dateIndexConditionQuery = new BasicDBObject();
			String startString = new SimpleDateFormat(fromDate).toLocalizedPattern();
			String endString = new SimpleDateFormat(toDate).toLocalizedPattern();

			DateFormat format1 = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss", Locale.ENGLISH);
			Date dateStart = format1.parse(startString);
			Date dateEnd = format1.parse(endString);

			LocalDate incrementingDate = dateStart.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
			LocalDate endDate = dateEnd.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();

			List<String> allDatesIndex = new ArrayList<>();

			while (!incrementingDate.isAfter(endDate)) {
				allDatesIndex.add(incrementingDate.toString().replaceAll("-", ""));
				incrementingDate = incrementingDate.plusDays(1);
			}

			for (String dateIndex : allDatesIndex) {
				dateIndexConditionList.add(new BasicDBObject("DATE_INDEX", dateIndex));
			}
			if (StringUtils.isNotBlank(PropertiesManager.propertiesMap.get(Constants.USE_DATE_INDEX.getValue()))
					&& PropertiesManager.propertiesMap.get(Constants.USE_DATE_INDEX.getValue()).equalsIgnoreCase("Y")) {
				dateIndexConditionQuery.append("$or", dateIndexConditionList);
			}
			
			
			List<BasicDBObject> recoConditionList = new ArrayList<BasicDBObject>();
			recoConditionList.add(new BasicDBObject(FieldType.STATUS.getName(), StatusType.SETTLED_SETTLE.getName()));

			BasicDBObject recoConditionQuery = new BasicDBObject("$and", recoConditionList);

			saleOrAuthList.add(recoConditionQuery);

			BasicDBObject authndSaleConditionQuery = new BasicDBObject("$or", saleOrAuthList);

			if (!paramConditionLst.isEmpty()) {
				allParamQuery = new BasicDBObject("$and", paramConditionLst);
			}

			List<BasicDBObject> allConditionQueryList = new ArrayList<BasicDBObject>();
			if (!dateQuery.isEmpty()) {
				allConditionQueryList.add(dateQuery);
			}
			if (!dateIndexConditionQuery.isEmpty()) {
				allConditionQueryList.add(dateIndexConditionQuery);
			}
			
			if (!authndSaleConditionQuery.isEmpty()) {
				allConditionQueryList.add(authndSaleConditionQuery);
			}

			BasicDBObject allConditionQueryObj = new BasicDBObject("$and", allConditionQueryList);
			List<BasicDBObject> fianlList = new ArrayList<BasicDBObject>();

			if (!allParamQuery.isEmpty()) {
				fianlList.add(allParamQuery);
			}
			if (!allConditionQueryObj.isEmpty()) {
				fianlList.add(allConditionQueryObj);
			}

			BasicDBObject finalquery = new BasicDBObject("$and", fianlList);
			logger.info("finalquery processing for settlement date update = " + finalquery);
			MongoDatabase dbIns = mongoInstance.getDB();
			MongoCollection<Document> coll = dbIns
					.getCollection(PropertiesManager.propertiesMap.get(prefix + Constants.COLLECTION_NAME.getValue()));
			
			MongoCollection<Document> summaryColl = dbIns
					.getCollection(PropertiesManager.propertiesMap.get(prefix + Constants.SUMMARY_TRANSACTIONS_NAME.getValue()));
			
			MongoCollection<Document> duplicateColl = dbIns
					.getCollection(PropertiesManager.propertiesMap.get(prefix + Constants.DUPLICATE_SETTLEMENT_NAME.getValue()));
			
			BasicDBObject match = new BasicDBObject("$match", finalquery);
			List<BasicDBObject> pipeline = Arrays.asList(match);
			AggregateIterable<Document> output = coll.aggregate(pipeline);
			output.allowDiskUse(true);
			MongoCursor<Document> cursor = output.iterator();
			int updateCount =0;
			ArrayList<String> pgRefList = new ArrayList<String>(); 
			int counter = 0;
			int totalCount = 0;
			logger.info("finalquery processed for settlement date update = " + finalquery);
			while (cursor.hasNext()) {
				counter = counter +1;
				totalCount = totalCount +1;
				if (counter == 5000) {
					logger.info("Transactions updated == " +totalCount);
					counter = 0;
				}
				Document doc = cursor.next();

				if (pgRefList.contains(doc.getString(FieldType.PG_REF_NUM.getName())+doc.getString(FieldType.ACQ_ID.getName()))) {
					
					logger.info("Duplicate settlement found for PG_REF_NUM : "+doc.getString(FieldType.PG_REF_NUM.getName())+"  ACQ_ID : "+doc.getString(FieldType.ACQ_ID.getName()));
					doc.put(FieldType.RESPONSE_MESSAGE.getName(), "Duplicate settlement found with this PG_REF_NUM and ACQ_ID");
					doc.put("_id", TransactionManager.getNewTransactionId());
					duplicateColl.insertOne(doc);
					
				}
				pgRefList.add(doc.getString(FieldType.PG_REF_NUM.getName())+doc.getString(FieldType.ACQ_ID.getName()));

				List<BasicDBObject> updateConditionList = new ArrayList<BasicDBObject>();
				updateConditionList.add(new BasicDBObject(FieldType.PG_REF_NUM.getName(), doc.getString(FieldType.PG_REF_NUM.getName())));
				updateConditionList.add(new BasicDBObject(FieldType.ACQ_ID.getName(), doc.getString(FieldType.ACQ_ID.getName())));

				BasicDBObject searchQuery = new BasicDBObject("$and", updateConditionList);
				
				long recordsCount = summaryColl.count(searchQuery);
				
				if (recordsCount == 0) {
					
					
					logger.info("No Records found for PG_REF_NUM : "+doc.getString(FieldType.PG_REF_NUM.getName())+"  ACQ_ID : "+doc.getString(FieldType.ACQ_ID.getName()));
					doc.put(FieldType.RESPONSE_MESSAGE.getName(), "No Record found with this PG_REF_NUM and ACQ_ID");
					doc.put("_id", TransactionManager.getNewTransactionId());
					duplicateColl.insertOne(doc);
				}
				else if (recordsCount > 1 ) {
					logger.info("Update Settled Data , found more than one record for PG_REF_NUM : " + doc.getString(FieldType.PG_REF_NUM.getName())
					+"  ACQ_ID : "+doc.getString(FieldType.ACQ_ID.getName()));
					doc.put(FieldType.RESPONSE_MESSAGE.getName(), "More than one Record found with this PG_REF_NUM and ACQ_ID");
					doc.put("_id", TransactionManager.getNewTransactionId());
					duplicateColl.insertOne(doc);
				}
				else {
					List<BasicDBObject> updateCollConditionList = new ArrayList<BasicDBObject>();
					updateCollConditionList.add(new BasicDBObject(FieldType.PG_REF_NUM.getName(), doc.getString(FieldType.PG_REF_NUM.getName())));
					updateCollConditionList.add(new BasicDBObject(FieldType.ACQ_ID.getName(), doc.getString(FieldType.ACQ_ID.getName())));

					BasicDBObject searchCollQuery = new BasicDBObject("$and", updateCollConditionList);
					Document docUpdate = new Document();
					docUpdate.append("$set", new BasicDBObject().append(FieldType.SETTLEMENT_DATE.getName(), doc.getString(FieldType.CREATE_DATE.getName())));
					summaryColl.updateOne(searchCollQuery, docUpdate);
					updateCount ++;
				}
			}
			logger.info("Found and updated settlement for "+updateCount+" transactions in iPayTransactions for date range "+fromDate+" TO "+toDate);
			cursor.close();
		} catch (Exception e) {
			logger.error("Exception occured " + e);
		}
	}
	
	public static String getNextDate(String curDate) {
		String nextDate = "";
		try {
			Calendar today = Calendar.getInstance();
			DateFormat format = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
			Date date = format.parse(curDate);
			today.setTime(date);
			today.add(Calendar.DAY_OF_YEAR, 1);
			nextDate = format.format(today.getTime());
		} catch (Exception e) {
			return nextDate;
		}
		return nextDate;
	}

	public void updateDateIndex(String fromDate, String toDate , String collection) {

		logger.info("Inside update settlement date for captured transactions ");
		
		
		try {
			
			BasicDBObject dateQuery = new BasicDBObject();
			BasicDBObject allParamQuery = new BasicDBObject();
			if (!fromDate.isEmpty()) {

				String currentDate = null;
				if (!toDate.isEmpty()) {
					currentDate = toDate;
				} else {
					DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
					Calendar cal = Calendar.getInstance();
					currentDate = dateFormat.format(cal.getTime());
				}

				dateQuery.put(FieldType.CREATE_DATE.getName(),
						BasicDBObjectBuilder.start("$gte", new SimpleDateFormat(fromDate).toLocalizedPattern())
								.add("$lte", new SimpleDateFormat(toDate).toLocalizedPattern()).get());
			}

				List<BasicDBObject> allConditionQueryList = new ArrayList<BasicDBObject>();
				if (!dateQuery.isEmpty()) {
					allConditionQueryList.add(dateQuery);
				}

				BasicDBObject allConditionQueryObj = new BasicDBObject("$and", allConditionQueryList);
				List<BasicDBObject> fianlList = new ArrayList<BasicDBObject>();

				if (!allParamQuery.isEmpty()) {
					fianlList.add(allParamQuery);
				}
				if (!allConditionQueryObj.isEmpty()) {
					fianlList.add(allConditionQueryObj);
				}

				BasicDBObject finalquery = new BasicDBObject("$and", fianlList);
				logger.info("finalquery processing for date index update = " + finalquery);
				MongoDatabase dbIns = mongoInstance.getDB();
				
				if (StringUtils.isBlank(collection)) {
					logger.info("Please provide collection value  ");
				}
				else{
					
					logger.info("Collection name is "+collection);
					MongoCollection<Document> coll = dbIns
							.getCollection(PropertiesManager.propertiesMap.get(prefix + collection));
					
					BasicDBObject match = new BasicDBObject("$match", finalquery);
					
					BasicDBObject projectElement = new BasicDBObject();
					projectElement.put("_id", 1);
					projectElement.put("CREATE_DATE", 1);
					BasicDBObject project = new BasicDBObject("$project", projectElement);
					
					List<BasicDBObject> pipeline = Arrays.asList(match,project);
					AggregateIterable<Document> output = coll.aggregate(pipeline);
					output.allowDiskUse(true);
					MongoCursor<Document> cursor = output.iterator();
					int updateCount =0;
					ArrayList<String> pgRefList = new ArrayList<String>(); 
					int counter = 0;
					int totalCount = 0;
					logger.info("finalquery processed for date index update = " + finalquery);
					while (cursor.hasNext()) {
						Document doc = cursor.next();
						counter = counter +1;
						totalCount = totalCount +1;
						if (counter == 10000) {
							logger.info("Processing current date == "+ doc.getString("CREATE_DATE") );
							logger.info("Transactions updated  == " +totalCount);
							counter = 0;
						}
							List<BasicDBObject> updateCollConditionList = new ArrayList<BasicDBObject>();
							updateCollConditionList.add(new BasicDBObject("_id", doc.get("_id")));
							BasicDBObject searchCollQuery = new BasicDBObject("$and", updateCollConditionList);
							Document docUpdate = new Document();
							docUpdate.append("$set", new BasicDBObject().append(FieldType.DATE_INDEX.getName(),  doc.getString("CREATE_DATE").substring(0,10).replace("-", "")));
							coll.updateOne(searchCollQuery, docUpdate);
							updateCount ++;
					}
					logger.info("Task Complete , Total DATE_INDEX added == "+ totalCount);
				}
			
			
		} catch (Exception e) {
			logger.error("Exception occured while adding date index " + e);
		}
	}

	
	public void updateCaptureDateIndex(String fromDate, String toDate , String collection) {

		logger.info("Inside update settlement date for captured transactions ");
		try {
			
			BasicDBObject dateQuery = new BasicDBObject();
			BasicDBObject allParamQuery = new BasicDBObject();
			if (!fromDate.isEmpty()) {

				String currentDate = null;
				if (!toDate.isEmpty()) {
					currentDate = toDate;
				} else {
					DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
					Calendar cal = Calendar.getInstance();
					currentDate = dateFormat.format(cal.getTime());
				}

				dateQuery.put(FieldType.CAPTURED_DATE.getName(),
						BasicDBObjectBuilder.start("$gte", new SimpleDateFormat(fromDate).toLocalizedPattern())
								.add("$lte", new SimpleDateFormat(toDate).toLocalizedPattern()).get());
			}

				List<BasicDBObject> allConditionQueryList = new ArrayList<BasicDBObject>();
				if (!dateQuery.isEmpty()) {
					allConditionQueryList.add(dateQuery);
				}

				BasicDBObject allConditionQueryObj = new BasicDBObject("$and", allConditionQueryList);
				List<BasicDBObject> fianlList = new ArrayList<BasicDBObject>();

				if (!allParamQuery.isEmpty()) {
					fianlList.add(allParamQuery);
				}
				if (!allConditionQueryObj.isEmpty()) {
					fianlList.add(allConditionQueryObj);
				}

				BasicDBObject finalquery = new BasicDBObject("$and", fianlList);
				logger.info("finalquery processing for CAPTURED_DATE index update = " + finalquery);
				MongoDatabase dbIns = mongoInstance.getDB();
				
				collection = "summaryTransactionsName";
				
				if (StringUtils.isBlank(collection)) {
					logger.info("Please provide collection value  ");
				}
				else{
					
					logger.info("Collection name is "+collection);
					MongoCollection<Document> coll = dbIns
							.getCollection(PropertiesManager.propertiesMap.get(prefix + collection));
					
					BasicDBObject match = new BasicDBObject("$match", finalquery);
					
					BasicDBObject projectElement = new BasicDBObject();
					projectElement.put("_id", 1);
					projectElement.put("CAPTURED_DATE", 1);
					BasicDBObject project = new BasicDBObject("$project", projectElement);
					
					List<BasicDBObject> pipeline = Arrays.asList(match,project);
					AggregateIterable<Document> output = coll.aggregate(pipeline);
					output.allowDiskUse(true);
					MongoCursor<Document> cursor = output.iterator();
					int updateCount =0;
					ArrayList<String> pgRefList = new ArrayList<String>(); 
					int counter = 0;
					int totalCount = 0;
					logger.info("finalquery processed for CAPTURED_DATE index update = " + finalquery);
					while (cursor.hasNext()) {
						Document doc = cursor.next();
						counter = counter +1;
						totalCount = totalCount +1;
						if (counter == 10000) {
							logger.info("Processing current date == "+ doc.getString("CAPTURED_DATE") );
							logger.info("Transactions updated  == " +totalCount);
							counter = 0;
						}
							List<BasicDBObject> updateCollConditionList = new ArrayList<BasicDBObject>();
							updateCollConditionList.add(new BasicDBObject("_id", doc.get("_id")));
							BasicDBObject searchCollQuery = new BasicDBObject("$and", updateCollConditionList);
							Document docUpdate = new Document();
							docUpdate.append("$set", new BasicDBObject().append(FieldType.DATE_INDEX.getName(),  doc.getString("CAPTURED_DATE").substring(0,10).replace("-", "")));
							coll.updateOne(searchCollQuery, docUpdate);
							updateCount ++;
					}
					logger.info("Task Complete , Total CAPTURED_DATE added == "+ totalCount);
				}
			
			
		} catch (Exception e) {
			logger.error("Exception occured while adding date index " + e);
		}
	}

	public void updatePgDateTimeIndexNew(String fromDate, String toDate , String collection) {


		logger.info("Inside update pg date time index settled transactions from Date = "+fromDate+"  to date  =  "+toDate);
		
		
		try {
			
			
			List<Date> dates = new ArrayList<Date>();
			DateFormat formatter ; 

			formatter = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
			DateFormat formatterFrom = new SimpleDateFormat("yyyy-MM-dd 00:00:00");
			DateFormat formatterTo = new SimpleDateFormat("yyyy-MM-dd 23:59:59");
			DateFormat formatterIndex = new SimpleDateFormat("yyyyMMdd");
			
			Date  startDate = formatter.parse(fromDate); 
			Date  endDate = formatter.parse(toDate);
			long interval = 24*1000 * 60 * 60; 
			long endTime =endDate.getTime() ; 
			long curTime = startDate.getTime();
			while (curTime <= endTime) {
			    dates.add(new Date(curTime));
			    curTime += interval;
			}
			for(int i=0;i<dates.size();i++){
				
				
			    Date lDate =dates.get(i);
			    String dateStart = formatterFrom.format(lDate);    
			    String dateEnd = formatterTo.format(lDate);
			    String pgDateIndex = formatterIndex.format(lDate);
			    
			    BasicDBObject dateQuery = new BasicDBObject();

				dateQuery.put(FieldType.PG_DATE_TIME.getName(),
						BasicDBObjectBuilder.start("$gte", new SimpleDateFormat(dateStart).toLocalizedPattern())
								.add("$lte", new SimpleDateFormat(dateEnd).toLocalizedPattern()).get());
				
			
				MongoDatabase dbIns = mongoInstance.getDB();
				MongoCollection<Document> coll = null;
				if (StringUtils.isBlank(collection)) {
					logger.info("Please provide collection value  ");
				}
				else{
					
					logger.info("Collection name is "+collection);
					 coll = dbIns
							.getCollection(PropertiesManager.propertiesMap.get(prefix + collection));
				}
				
				logger.info("dateQuery = "+dateQuery);
				Document docUpdate = new Document();
				docUpdate.append("$set", new BasicDBObject().append(FieldType.PG_DATE_TIME_INDEX.getName(),  pgDateIndex));
				coll.updateMany(dateQuery, docUpdate);
			    
				logger.info("PG_DATE_TIME_INDEX updated for date index = "+pgDateIndex);
			}
			
		}
		catch(Exception e) {
			logger.error("Exception ",e);
		}


	}
	
	public void updatePgDateTimeIndex(String fromDate, String toDate , String collection) {


		logger.info("Inside update pg date time index settled transactions ");
		
		
		try {
			
			BasicDBObject dateQuery = new BasicDBObject();
			BasicDBObject allParamQuery = new BasicDBObject();
			if (!fromDate.isEmpty()) {

				String currentDate = null;
				if (!toDate.isEmpty()) {
					currentDate = toDate;
				} else {
					DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
					Calendar cal = Calendar.getInstance();
					currentDate = dateFormat.format(cal.getTime());
				}

				dateQuery.put(FieldType.CREATE_DATE.getName(),
						BasicDBObjectBuilder.start("$gte", new SimpleDateFormat(fromDate).toLocalizedPattern())
								.add("$lte", new SimpleDateFormat(toDate).toLocalizedPattern()).get());
				}

			List<BasicDBObject> dateIndexConditionList = new ArrayList<BasicDBObject>();
			BasicDBObject dateIndexConditionQuery = new BasicDBObject();
			String startString = new SimpleDateFormat(fromDate).toLocalizedPattern();
			String endString = new SimpleDateFormat(toDate).toLocalizedPattern();

			DateFormat format = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss", Locale.ENGLISH);
			Date dateStart = format.parse(startString);
			Date dateEnd = format.parse(endString);

			LocalDate incrementingDate = dateStart.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
			LocalDate endDate = dateEnd.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();

			List<String> allDatesIndex = new ArrayList<>();

			while (!incrementingDate.isAfter(endDate)) {
				allDatesIndex.add(incrementingDate.toString().replaceAll("-", ""));
				incrementingDate = incrementingDate.plusDays(1);
			}

			for (String dateIndex : allDatesIndex) {
				dateIndexConditionList.add(new BasicDBObject("DATE_INDEX", dateIndex));
			}
			if (StringUtils.isNotBlank(PropertiesManager.propertiesMap.get(Constants.USE_DATE_INDEX.getValue()))
					&& PropertiesManager.propertiesMap.get(Constants.USE_DATE_INDEX.getValue()).equalsIgnoreCase("Y")) {
				dateIndexConditionQuery.append("$or", dateIndexConditionList);
			}
			
			BasicDBObject statusQuery = new BasicDBObject(FieldType.STATUS.getName(), StatusType.SETTLED_SETTLE.getName());
			List<BasicDBObject> allConditionQueryList = new ArrayList<BasicDBObject>();
				
				
				if (!dateQuery.isEmpty()) {
					allConditionQueryList.add(dateQuery);
				}
				
				if (!statusQuery.isEmpty()) {
					allConditionQueryList.add(statusQuery);
				}
				
				if (!dateIndexConditionQuery.isEmpty()) {
					allConditionQueryList.add(dateIndexConditionQuery);
				}
				BasicDBObject allConditionQueryObj = new BasicDBObject("$and", allConditionQueryList);
				List<BasicDBObject> fianlList = new ArrayList<BasicDBObject>();

				if (!allParamQuery.isEmpty()) {
					fianlList.add(allParamQuery);
				}
				if (!allConditionQueryObj.isEmpty()) {
					fianlList.add(allConditionQueryObj);
				}

				BasicDBObject finalquery = new BasicDBObject("$and", fianlList);
				logger.info("finalquery processing for pg date time index update = " + finalquery);
				MongoDatabase dbIns = mongoInstance.getDB();
				
				if (StringUtils.isBlank(collection)) {
					logger.info("Please provide collection value  ");
				}
				else{
					
					logger.info("Collection name is "+collection);
					MongoCollection<Document> coll = dbIns
							.getCollection(PropertiesManager.propertiesMap.get(prefix + collection));
					
					BasicDBObject match = new BasicDBObject("$match", finalquery);
					
					BasicDBObject projectElement = new BasicDBObject();
					projectElement.put("_id", 1);
					projectElement.put("PG_DATE_TIME", 1);
					BasicDBObject project = new BasicDBObject("$project", projectElement);
					
					List<BasicDBObject> pipeline = Arrays.asList(match,project);
					AggregateIterable<Document> output = coll.aggregate(pipeline);
					output.allowDiskUse(true);
					MongoCursor<Document> cursor = output.iterator();
					int counter = 0;
					int totalCount = 0;
					logger.info("finalquery processed for pg date time index update = " + finalquery);
					while (cursor.hasNext()) {
						Document doc = cursor.next();
						
						counter = counter +1;
						totalCount = totalCount +1;
						if (counter == 10000) {
							logger.info("Processing current pg date time  == "+ doc.getString("PG_DATE_TIME") );
							logger.info("Transactions updated  == " +totalCount);
							counter = 0;
						}
							List<BasicDBObject> updateCollConditionList = new ArrayList<BasicDBObject>();
							updateCollConditionList.add(new BasicDBObject("_id", doc.get("_id")));
							BasicDBObject searchCollQuery = new BasicDBObject("$and", updateCollConditionList);
							Document docUpdate = new Document();
							docUpdate.append("$set", new BasicDBObject().append(FieldType.PG_DATE_TIME_INDEX.getName(),  doc.getString("PG_DATE_TIME").substring(0,10).replace("-", "")));
							coll.updateOne(searchCollQuery, docUpdate);
					}
					logger.info("Task Complete , Total PG_DATE_TIME_INDEX added == "+ totalCount);
				}
			
			
		} catch (Exception e) {
			logger.error("Exception occured while adding PG_DATE_TIME_INDEX " + e);
		}
	}
	
	
	public void fixDateIndexForNull(String fromDate, String toDate , String collection) {

		logger.info("Inside fix date index for date_index = null ");
		
		
		try {
			
			BasicDBObject dateQuery = new BasicDBObject();
			BasicDBObject allParamQuery = new BasicDBObject();
			BasicDBObject nullQuery = new BasicDBObject();
			if (!fromDate.isEmpty()) {

				String currentDate = null;
				if (!toDate.isEmpty()) {
					currentDate = toDate;
				} else {
					DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
					Calendar cal = Calendar.getInstance();
					currentDate = dateFormat.format(cal.getTime());
				}

				dateQuery.put(FieldType.CREATE_DATE.getName(),
						BasicDBObjectBuilder.start("$gte", new SimpleDateFormat(fromDate).toLocalizedPattern())
								.add("$lte", new SimpleDateFormat(toDate).toLocalizedPattern()).get());
				}

				nullQuery.put(FieldType.DATE_INDEX.getName(), null);
				List<BasicDBObject> allConditionQueryList = new ArrayList<BasicDBObject>();
				if (!dateQuery.isEmpty()) {
					allConditionQueryList.add(dateQuery);
				}
				allConditionQueryList.add(nullQuery);
				
				BasicDBObject allConditionQueryObj = new BasicDBObject("$and", allConditionQueryList);
				List<BasicDBObject> fianlList = new ArrayList<BasicDBObject>();

				if (!allParamQuery.isEmpty()) {
					fianlList.add(allParamQuery);
				}
				if (!allConditionQueryObj.isEmpty()) {
					fianlList.add(allConditionQueryObj);
				}

				BasicDBObject finalquery = new BasicDBObject("$and", fianlList);
				logger.info("finalquery processing for date index update = " + finalquery);
				MongoDatabase dbIns = mongoInstance.getDB();
				
				if (StringUtils.isBlank(collection)) {
					logger.info("Please provide collection value  ");
				}
				else{
					
					logger.info("Collection name is "+collection);
					MongoCollection<Document> coll = dbIns
							.getCollection(PropertiesManager.propertiesMap.get(prefix + collection));
					
					BasicDBObject match = new BasicDBObject("$match", finalquery);
					
					BasicDBObject projectElement = new BasicDBObject();
					projectElement.put("_id", 1);
					projectElement.put("CREATE_DATE", 1);
					BasicDBObject project = new BasicDBObject("$project", projectElement);
					
					List<BasicDBObject> pipeline = Arrays.asList(match,project);
					AggregateIterable<Document> output = coll.aggregate(pipeline);
					output.allowDiskUse(true);
					MongoCursor<Document> cursor = output.iterator();
					int updateCount =0;
					int counter = 0;
					int totalCount = 0;
					logger.info("finalquery processed for date index update = " + finalquery);
					while (cursor.hasNext()) {
						Document doc = cursor.next();
						counter = counter +1;
						totalCount = totalCount +1;
						if (counter == 100) {
							logger.info("Processing current date == "+ doc.getString("CREATE_DATE") );
							logger.info("Transactions updated  == " +totalCount);
							counter = 0;
						}
							List<BasicDBObject> updateCollConditionList = new ArrayList<BasicDBObject>();
							updateCollConditionList.add(new BasicDBObject("_id", doc.get("_id")));
							BasicDBObject searchCollQuery = new BasicDBObject("$and", updateCollConditionList);
							Document docUpdate = new Document();
							docUpdate.append("$set", new BasicDBObject().append(FieldType.DATE_INDEX.getName(),  doc.getString("CREATE_DATE").substring(0,10).replace("-", "")));
							coll.updateOne(searchCollQuery, docUpdate);
							updateCount ++;
					}
					logger.info("Task Complete , Total DATE_INDEX fixed == "+ totalCount);
				}
			
			
		} catch (Exception e) {
			logger.error("Exception occured while fixing null date index " + e);
		}
	}
	
	
	public void updateAcqIdMPGS(String fromDate, String toDate , String collection) {

		logger.info("Inside update ACq id for MPGS");
		try {
			
			BasicDBObject dateQuery = new BasicDBObject();
			BasicDBObject statusQuery = new BasicDBObject();
			BasicDBObject acquierQuery = new BasicDBObject();
			BasicDBObject txntypeQuery = new BasicDBObject();
			BasicDBObject allParamQuery = new BasicDBObject();
			if (!fromDate.isEmpty()) {

				String currentDate = null;
				if (!toDate.isEmpty()) {
					currentDate = toDate;
				} else {
					DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
					Calendar cal = Calendar.getInstance();
					currentDate = dateFormat.format(cal.getTime());
				}

				
				if (collection.equalsIgnoreCase("collectionName")) {
					dateQuery.put(FieldType.CREATE_DATE.getName(),
							BasicDBObjectBuilder.start("$gte", new SimpleDateFormat(fromDate).toLocalizedPattern())
									.add("$lte", new SimpleDateFormat(toDate).toLocalizedPattern()).get());
				}
				else {
					dateQuery.put(FieldType.CAPTURED_DATE.getName(),
							BasicDBObjectBuilder.start("$gte", new SimpleDateFormat(fromDate).toLocalizedPattern())
									.add("$lte", new SimpleDateFormat(toDate).toLocalizedPattern()).get());
				}
				
			}

			
			List<BasicDBObject> dateIndexConditionList = new ArrayList<BasicDBObject>();
			BasicDBObject dateIndexConditionQuery = new BasicDBObject();
			String startString = new SimpleDateFormat(fromDate).toLocalizedPattern();
			String endString = new SimpleDateFormat(toDate).toLocalizedPattern();

			DateFormat format1 = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss", Locale.ENGLISH);
			Date dateStart = format1.parse(startString);
			Date dateEnd = format1.parse(endString);

			LocalDate incrementingDate = dateStart.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
			LocalDate endDate = dateEnd.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();

			List<String> allDatesIndex = new ArrayList<>();

			while (!incrementingDate.isAfter(endDate)) {
				allDatesIndex.add(incrementingDate.toString().replaceAll("-", ""));
				incrementingDate = incrementingDate.plusDays(1);
			}

			for (String dateIndex : allDatesIndex) {
				dateIndexConditionList.add(new BasicDBObject("DATE_INDEX", dateIndex));
			}
			if (StringUtils.isNotBlank(PropertiesManager.propertiesMap.get(Constants.USE_DATE_INDEX.getValue()))
					&& PropertiesManager.propertiesMap.get(Constants.USE_DATE_INDEX.getValue()).equalsIgnoreCase("Y")) {
				dateIndexConditionQuery.append("$or", dateIndexConditionList);
			}
			
			
			statusQuery.put(FieldType.STATUS.getName(), StatusType.CAPTURED.getName());
			acquierQuery.put(FieldType.ACQUIRER_TYPE.getName(), AcquirerType.ICICI_MPGS.getCode());
			txntypeQuery.put(FieldType.TXNTYPE.getName(), TransactionType.REFUND.getName());
				List<BasicDBObject> allConditionQueryList = new ArrayList<BasicDBObject>();
				if (!dateQuery.isEmpty()) {
					allConditionQueryList.add(dateQuery);
				}
				
				if (!dateIndexConditionQuery.isEmpty()) {
					allConditionQueryList.add(dateIndexConditionQuery);
				}
				
				
				if (!statusQuery.isEmpty()) {
					allConditionQueryList.add(statusQuery);
				}
				
				if (!acquierQuery.isEmpty()) {
					allConditionQueryList.add(acquierQuery);
				}
				
				if (!txntypeQuery.isEmpty()) {
					allConditionQueryList.add(txntypeQuery);
				}
				
				BasicDBObject allConditionQueryObj = new BasicDBObject("$and", allConditionQueryList);
				List<BasicDBObject> fianlList = new ArrayList<BasicDBObject>();

				if (!allParamQuery.isEmpty()) {
					fianlList.add(allParamQuery);
				}
				if (!allConditionQueryObj.isEmpty()) {
					fianlList.add(allConditionQueryObj);
				}

				BasicDBObject finalquery = new BasicDBObject("$and", fianlList);
				logger.info("finalquery processing for CAPTURED_DATE index update = " + finalquery);
				MongoDatabase dbIns = mongoInstance.getDB();
				
				if (StringUtils.isBlank(collection)) {
					logger.info("Please provide collection value  ");
				}
				else{
					
					logger.info("Collection name is "+collection);
					MongoCollection<Document> coll = dbIns
							.getCollection(PropertiesManager.propertiesMap.get(prefix + collection));
					
					BasicDBObject match = new BasicDBObject("$match", finalquery);
					
					BasicDBObject projectElement = new BasicDBObject();
					projectElement.put("_id", 1);
					projectElement.put(FieldType.RRN.getName(), 1);
					BasicDBObject project = new BasicDBObject("$project", projectElement);
					
					List<BasicDBObject> pipeline = Arrays.asList(match,project);
					AggregateIterable<Document> output = coll.aggregate(pipeline);
					output.allowDiskUse(true);
					MongoCursor<Document> cursor = output.iterator();
					int counter = 0;
					int totalCount = 0;
					logger.info("finalquery processed for ACQ_ID update in MPGS = " + finalquery);
					while (cursor.hasNext()) {
						Document doc = cursor.next();
						counter = counter +1;
						totalCount = totalCount +1;
						if (counter == 3000) {
							logger.info("Transactions updated  == " +totalCount);
							counter = 0;
						}
							List<BasicDBObject> updateCollConditionList = new ArrayList<BasicDBObject>();
							updateCollConditionList.add(new BasicDBObject("_id", doc.get("_id")));
							BasicDBObject searchCollQuery = new BasicDBObject("$and", updateCollConditionList);
							Document docUpdate = new Document();
							docUpdate.append("$set", new BasicDBObject().append(FieldType.ACQ_ID.getName(), doc.getString(FieldType.RRN.getName())));
							coll.updateOne(searchCollQuery, docUpdate);
					}
					logger.info("Task Complete , Total ACQ_ID updated == "+ totalCount);
				}
			
			
		} catch (Exception e) {
			logger.error("Exception occured while updating acq id " + e);
		}
	}
	
	public void insertSettledData(String fromDate, String toDate, String pgRefNum) {
		try {
			Map<String, User> userMap = new HashMap<String, User>();
			Map<String, List<ServiceTax>> serviceTaxMap = new HashMap<String, List<ServiceTax>>();

			List<Surcharge> surchargeList = new ArrayList<Surcharge>();
			SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.ENGLISH);
			try {

				Date date1 = format.parse(fromDate);
				Date date2 = format.parse(toDate);

				if (surchargeDao.findAllSurchargeByDate(date1, date2) == null) {
					logger.info("No surcharge data found");
				}
				else {
					surchargeList = surchargeDao.findAllSurchargeByDate(date1, date2);
				}
				
			} catch (Exception e) {
				logger.error("Exception 1 " + e);
			}

			List<SettledTransactionDataObject> settledTransactionDataList = new ArrayList<SettledTransactionDataObject>();

			BasicDBObject dateQuery = new BasicDBObject();
			List<BasicDBObject> paramConditionLst = new ArrayList<BasicDBObject>();
			BasicDBObject currencyQuery = new BasicDBObject();
			BasicDBObject acquirerQuery = new BasicDBObject();
			BasicDBObject pgRefNumQuery = new BasicDBObject();

			BasicDBObject allParamQuery = new BasicDBObject();
			List<BasicDBObject> saleOrAuthList = new ArrayList<BasicDBObject>();
			String currentDate = null;
			if (!fromDate.isEmpty()) {

				if (!toDate.isEmpty()) {
					currentDate = toDate;
				} else {
					DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
					Calendar cal = Calendar.getInstance();
					currentDate = dateFormat.format(cal.getTime());
				}

				dateQuery.put(FieldType.CREATE_DATE.getName(),
						BasicDBObjectBuilder.start("$gte", new SimpleDateFormat(fromDate).toLocalizedPattern())
								.add("$lte", new SimpleDateFormat(currentDate).toLocalizedPattern()).get());
			}
			
			
			
			List<BasicDBObject> dateIndexConditionList = new ArrayList<BasicDBObject>();
            BasicDBObject dateIndexConditionQuery = new BasicDBObject();
            String startString = new SimpleDateFormat(fromDate).toLocalizedPattern();
            String endString = new SimpleDateFormat(currentDate).toLocalizedPattern();

            DateFormat format1 = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss", Locale.ENGLISH);
            Date dateStart = format1.parse(startString);
            Date dateEnd = format1.parse(endString);
            
            LocalDate incrementingDate = dateStart.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
            LocalDate endDate = dateEnd.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();

            List<String> allDatesIndex = new ArrayList<>();
            

            while (!incrementingDate.isAfter(endDate)) {
                allDatesIndex.add(incrementingDate.toString().replaceAll("-", ""));
                incrementingDate = incrementingDate.plusDays(1);
            }
            
            
            for (String dateIndex : allDatesIndex) {
                           dateIndexConditionList.add(new BasicDBObject("DATE_INDEX",dateIndex));
            }
            
            if (StringUtils.isNotBlank(PropertiesManager.propertiesMap.get(Constants.USE_DATE_INDEX.getValue())) && 
            		PropertiesManager.propertiesMap.get(Constants.USE_DATE_INDEX.getValue()).equalsIgnoreCase("Y")) {
            	 dateIndexConditionQuery.append("$or", dateIndexConditionList);
            }
           
            pgRefNumQuery.put(FieldType.PG_REF_NUM.getName(), pgRefNum);

			List<BasicDBObject> saleConditionList = new ArrayList<BasicDBObject>();
			saleConditionList.add(new BasicDBObject(FieldType.TXNTYPE.getName(), TransactionType.SALE.getName()));
			saleConditionList.add(new BasicDBObject(FieldType.STATUS.getName(), StatusType.SETTLED_SETTLE.getName()));

			BasicDBObject saleConditionQuery = new BasicDBObject("$and", saleConditionList);


			List<BasicDBObject> recoConditionList = new ArrayList<BasicDBObject>();
			recoConditionList.add(new BasicDBObject(FieldType.TXNTYPE.getName(), TransactionType.RECO.getName()));
			recoConditionList.add(new BasicDBObject(FieldType.STATUS.getName(), StatusType.SETTLED_SETTLE.getName()));

			BasicDBObject recoConditionQuery = new BasicDBObject("$and", recoConditionList);


			/*List<BasicDBObject> recoRefundConditionList = new ArrayList<BasicDBObject>();
			recoRefundConditionList
					.add(new BasicDBObject(FieldType.TXNTYPE.getName(), TransactionType.REFUNDRECO.getName()));
			recoRefundConditionList.add(new BasicDBObject(FieldType.STATUS.getName(), StatusType.SETTLED.getName()));

			BasicDBObject recoRefundConditionQuery = new BasicDBObject("$and", recoRefundConditionList);*/

			saleOrAuthList.add(saleConditionQuery);
			saleOrAuthList.add(recoConditionQuery);
			//saleOrAuthList.add(recoRefundConditionQuery);

			BasicDBObject authndSaleConditionQuery = new BasicDBObject("$or", saleOrAuthList);

			if (!paramConditionLst.isEmpty()) {
				allParamQuery = new BasicDBObject("$and", paramConditionLst);
			}

			List<BasicDBObject> allConditionQueryList = new ArrayList<BasicDBObject>();
			if (!currencyQuery.isEmpty()) {
				allConditionQueryList.add(currencyQuery);
			}
			if (!acquirerQuery.isEmpty()) {
				allConditionQueryList.add(acquirerQuery);
			}
			if (!pgRefNumQuery.isEmpty()) {
				allConditionQueryList.add(pgRefNumQuery);
			}
			if (!dateQuery.isEmpty()) {
				allConditionQueryList.add(dateQuery);
			}
			if (!authndSaleConditionQuery.isEmpty()) {
				allConditionQueryList.add(authndSaleConditionQuery);
			}
			if (!dateIndexConditionQuery.isEmpty()) {
				allConditionQueryList.add(dateIndexConditionQuery);
			}

			BasicDBObject allConditionQueryObj = new BasicDBObject("$and", allConditionQueryList);
			List<BasicDBObject> fianlList = new ArrayList<BasicDBObject>();

			if (!allParamQuery.isEmpty()) {
				fianlList.add(allParamQuery);
			}
			if (!allConditionQueryObj.isEmpty()) {
				fianlList.add(allConditionQueryObj);
			}

			BasicDBObject finalquery = new BasicDBObject("$and", fianlList);
			logger.info("finalquery for settlement data upload = " + finalquery);
			MongoDatabase dbIns = mongoInstance.getDB();
			MongoCollection<Document> coll = dbIns
					.getCollection(PropertiesManager.propertiesMap.get(prefix + Constants.COLLECTION_NAME.getValue()));
			BasicDBObject match = new BasicDBObject("$match", finalquery);

			BasicDBObject sort = new BasicDBObject("$sort", new BasicDBObject("CREATE_DATE", -1));

			List<BasicDBObject> pipeline = Arrays.asList(match, sort);
			AggregateIterable<Document> output = coll.aggregate(pipeline);
			output.allowDiskUse(true);
			MongoCursor<Document> cursor = output.iterator();

			while (cursor.hasNext()) {
				Document doc = cursor.next();
				
				if (!StringUtils.isBlank(doc.getString(FieldType.SURCHARGE_FLAG.getName()))
						&& doc.getString(FieldType.SURCHARGE_FLAG.getName()).equalsIgnoreCase("Y")) {
					SettledTransactionDataObject settledTransactionData = new SettledTransactionDataObject();

					settledTransactionData.set_id(doc.getString(FieldType.TXN_ID.getName()));
					settledTransactionData.setTransactionId(doc.getString(FieldType.TXN_ID.getName()));
					settledTransactionData.setPgRefNum(doc.getString(FieldType.PG_REF_NUM.getName()));
					
					if (StringUtils.isBlank(doc.getString(FieldType.PAYMENTS_REGION.getName()))) {
						settledTransactionData.setTransactionRegion(AccountCurrencyRegion.DOMESTIC.name());
					}
					else {
						settledTransactionData.setTransactionRegion(doc.getString(FieldType.PAYMENTS_REGION.getName()));
					}
					
					if (StringUtils.isBlank(doc.getString(FieldType.POST_SETTLED_FLAG.getName()))) {
						settledTransactionData.setPostSettledFlag("");
					}
					else {
						settledTransactionData.setPostSettledFlag(doc.getString(FieldType.POST_SETTLED_FLAG.getName()));
					}
					
					if (StringUtils.isBlank(doc.getString(FieldType.REFUND_ORDER_ID.getName()))) {
						settledTransactionData.setRefundOrderId("");
					}
					else {
						settledTransactionData.setRefundOrderId(doc.getString(FieldType.REFUND_ORDER_ID.getName()));
					}
					settledTransactionData.setTxnType(doc.getString(FieldType.TXNTYPE.getName()));
					settledTransactionData.setAcquirerType(doc.getString(FieldType.ACQUIRER_TYPE.getName()));
					settledTransactionData.setPaymentMethods(doc.getString(FieldType.PAYMENT_TYPE.getName()));
					settledTransactionData.setCreateDate(doc.getString(FieldType.CREATE_DATE.getName()));
					settledTransactionData.setOrderId(doc.getString(FieldType.ORDER_ID.getName()));
					settledTransactionData.setPayId(doc.getString(FieldType.PAY_ID.getName()));
					settledTransactionData.setMopType(doc.getString(FieldType.MOP_TYPE.getName()));
					settledTransactionData.setCurrency(doc.getString(FieldType.CURRENCY_CODE.getName()));
					settledTransactionData.setStatus(doc.getString(FieldType.STATUS.getName()));
					
					if (StringUtils.isBlank(doc.getString(FieldType.CARD_HOLDER_TYPE.getName()))) {
						settledTransactionData.setCardHolderType(CardHolderType.CONSUMER.name());
					}
					else {
						settledTransactionData.setCardHolderType(doc.getString(FieldType.CARD_HOLDER_TYPE.getName()));
					}
					
					if (StringUtils.isBlank(doc.getString(FieldType.PG_DATE_TIME.getName()))) {
						settledTransactionData.setCaptureDate("");
					}
					else {
						settledTransactionData.setCaptureDate(doc.getString(FieldType.PG_DATE_TIME.getName()));
					}
					
					if (StringUtils.isBlank(doc.getString(FieldType.RRN.getName()))) {
						settledTransactionData.setRrn("");
					}
					else {
						settledTransactionData.setRrn(doc.getString(FieldType.RRN.getName()));
					}
					
					settledTransactionData.setAcqId(doc.getString(FieldType.ACQ_ID.getName()));
					
					if (StringUtils.isBlank(doc.getString(FieldType.ARN.getName()))) {
						settledTransactionData.setArn("");
					}
					else {
						settledTransactionData.setArn(doc.getString(FieldType.ARN.getName()));
					}
					
					if (StringUtils.isBlank(doc.getString(FieldType.UDF6.getName()))) {
						settledTransactionData.setDeltaRefundFlag("");
					}
					else {
						settledTransactionData.setDeltaRefundFlag(doc.getString(FieldType.UDF6.getName()));
					}
					
					settledTransactionData.setSurchargeFlag(doc.getString(FieldType.SURCHARGE_FLAG.getName()));
					
					if (StringUtils.isBlank(doc.getString(FieldType.REFUND_FLAG.getName()))) {
						settledTransactionData.setRefundFlag("");
					}
					else {
						settledTransactionData.setRefundFlag(doc.getString(FieldType.REFUND_FLAG.getName()));
					}
					
					settledTransactionData.setAmount(Double.valueOf(doc.getString(FieldType.AMOUNT.getName())));
					settledTransactionData
							.setTotalAmount(Double.valueOf(doc.getString(FieldType.TOTAL_AMOUNT.getName())));

					BigDecimal amount = new BigDecimal(doc.getString(FieldType.AMOUNT.getName()));
					BigDecimal totalAmount = new BigDecimal(doc.getString(FieldType.TOTAL_AMOUNT.getName()));
					BigDecimal totalSurcharge = totalAmount.subtract(amount);

					if (totalAmount.equals(amount)) {

						settledTransactionData.setTdrScAcquirer(Double.valueOf(0));
						settledTransactionData.setGstScAcquirer(Double.valueOf(0));

						settledTransactionData.setTdrScIpay(Double.valueOf(0));
						settledTransactionData.setGstScIpay(Double.valueOf(0));

						settledTransactionData.setTdrScMmad(Double.valueOf(0));
						settledTransactionData.setGstScMmad(Double.valueOf(0));

					} else {
						String payId = doc.getString(FieldType.PAY_ID.getName());
						if (StringUtils.isBlank(payId)) {
							logger.info("Pay Id not present for " + doc.getString(FieldType.TXN_ID.getName()));
							continue;
						}

						User user = null;
						if (userMap.get(payId) != null && !userMap.get(payId).getPayId().equals("")) {
							user = userMap.get(payId);
						} else {
							user = userdao.findPayId(payId);
							userMap.put(payId, user);
						}

						if (user == null) {
							logger.info("User not found for txn Id " + doc.getString(FieldType.TXN_ID.getName()));
							continue;
						}

						BigDecimal st = null;

						List<ServiceTax> serviceTaxList = null;
						if (serviceTaxMap.get(user.getIndustryCategory()) != null
								&& serviceTaxMap.get(user.getIndustryCategory()).size() > 0) {

							serviceTaxList = serviceTaxMap.get(user.getIndustryCategory());
						} else {
							serviceTaxList = serviceTaxDao.findServiceTaxForReportWithoutDate(
									user.getIndustryCategory(), doc.getString(FieldType.CREATE_DATE.getName()));
							serviceTaxMap.put(user.getIndustryCategory(), serviceTaxList);
						}

						for (ServiceTax serviceTax : serviceTaxList) {
							st = serviceTax.getServiceTax();
							st = st.setScale(2, RoundingMode.HALF_DOWN);
						}

						Date surchargeStartDate = null;
						Date surchargeEndDate = null;
						Date settlementDate = null;
						Surcharge surcharge = new Surcharge();

						try {
							for (Surcharge surchargeData : surchargeList) {

								if (AcquirerType.getInstancefromName(surchargeData.getAcquirerName()).getCode()
										.equalsIgnoreCase(settledTransactionData.getAcquirerType())
										&& surchargeData.getPaymentType().getCode()
												.equalsIgnoreCase(settledTransactionData.getPaymentMethods())
										&& surchargeData.getMopType().getCode()
												.equalsIgnoreCase(settledTransactionData.getMopType())
										&& surchargeData.getPaymentsRegion().name()
												.equalsIgnoreCase(settledTransactionData.getTransactionRegion())
										&& surchargeData.getPayId().equalsIgnoreCase(payId)) {

									surchargeStartDate = format.parse(surchargeData.getCreatedDate().toString());
									surchargeEndDate = format.parse(surchargeData.getUpdatedDate().toString());
									if (surchargeStartDate.compareTo(surchargeEndDate) == 0) {
										surchargeEndDate = new Date();
									}

									settlementDate = format.parse(doc.getString(FieldType.CREATE_DATE.getName()));

									if (settlementDate.compareTo(surchargeStartDate) >= 0
											&& settlementDate.compareTo(surchargeEndDate) <= 0) {
										surcharge = surchargeData;
										break;
									} else {
										continue;
									}

								}
							}
						} catch (Exception e) {
							logger.error("Exception " + e);
						}

						if (surcharge.getBankSurchargeAmountCustomer() == null
								|| surcharge.getBankSurchargePercentageCustomer() == null
								|| surcharge.getBankSurchargeAmountCommercial() == null
								|| surcharge.getBankSurchargePercentageCommercial() == null) {

							logger.info("Surcharge is null for payId = " + payId + " acquirer = "
									+ doc.getString(FieldType.ACQUIRER_TYPE.getName()) + " mop = "
									+ doc.getString(FieldType.MOP_TYPE.getName()) + "  paymentType = "
									+ doc.getString(FieldType.PAYMENT_TYPE.getName()) + "  paymentRegion = "
									+ doc.getString(FieldType.PAYMENTS_REGION.getName()) + "  date = "
									+ doc.getString(FieldType.CREATE_DATE.getName()));
							continue;
						}

						BigDecimal bankFixCharge = BigDecimal.ZERO;
						BigDecimal bankChargePr = BigDecimal.ZERO;

						if (settledTransactionData.getCardHolderType()
								.equals(CardHolderType.COMMERCIAL.toString())) {

							bankFixCharge = surcharge.getBankSurchargeAmountCommercial();
							bankChargePr = surcharge.getBankSurchargePercentageCommercial();
						} else {
							bankFixCharge = surcharge.getBankSurchargeAmountCustomer();
							bankChargePr = surcharge.getBankSurchargePercentageCustomer();
						}

						BigDecimal acquirerSurcharge = amount.multiply(bankChargePr.divide(BigDecimal.valueOf(100)));
						acquirerSurcharge = acquirerSurcharge.add(bankFixCharge).setScale(2, RoundingMode.HALF_DOWN);
						BigDecimal acquirerGst = acquirerSurcharge.multiply(st.divide(BigDecimal.valueOf(100)))
								.setScale(2, RoundingMode.HALF_DOWN);
						BigDecimal totalAcquirerSurcharge = acquirerSurcharge.add(acquirerGst);

						BigDecimal totalPgSurcharge = totalSurcharge.subtract(totalAcquirerSurcharge);
						BigDecimal divisor = new BigDecimal("1")
								.add(st.divide(new BigDecimal("100"), 2, RoundingMode.HALF_DOWN));
						BigDecimal divisor2 = new BigDecimal("2");

						BigDecimal pgSurcharge = totalPgSurcharge.divide(divisor, 2, RoundingMode.HALF_DOWN);
						BigDecimal pgGst = totalPgSurcharge.subtract(pgSurcharge);

						pgGst = pgGst.divide(divisor2, 2, RoundingMode.HALF_DOWN);
						pgSurcharge = pgSurcharge.divide(divisor2, 2, RoundingMode.HALF_DOWN);

						settledTransactionData.setTdrScAcquirer(acquirerSurcharge.doubleValue());
						settledTransactionData.setGstScAcquirer(acquirerGst.doubleValue());

						settledTransactionData.setTdrScIpay(pgSurcharge.doubleValue());
						settledTransactionData.setGstScIpay(pgGst.doubleValue());

						settledTransactionData.setTdrScMmad(pgSurcharge.doubleValue());
						settledTransactionData.setGstScMmad(pgGst.doubleValue());
					}

					settledTransactionDataList.add(settledTransactionData);
				}

				else {
					logger.info("No surcharge based transaction for date range "+fromDate+" TO "+toDate);
				}
			}
			logger.info("Found "+settledTransactionDataList.size()+" transactions in iPayTransactions for date range "+fromDate+" TO "+toDate);

			settledTransactionDataDao.insertTransaction(settledTransactionDataList);
		} catch (Exception e) {
			logger.error("Exception occured " + e);
		}
	}

	@SuppressWarnings("unchecked")
	public void dashboardPopulator(String fromDate, String toDate) {

		logger.info("Inside Dashboard Populator");
		
		try {
			
			List<Date> dates = new ArrayList<Date>();
			DateFormat formatter ; 

			formatter = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
			DateFormat formatterFrom = new SimpleDateFormat("yyyy-MM-dd 00:00:00");
			DateFormat formatterTo = new SimpleDateFormat("yyyy-MM-dd 23:59:59");
			DateFormat formatterIndex = new SimpleDateFormat("yyyyMMdd");
			
			Date  startDate = formatter.parse(fromDate); 
			Date  endDate = formatter.parse(toDate);
			long interval = 24*1000 * 60 * 60; 
			long endTime =endDate.getTime() ; 
			long curTime = startDate.getTime();
			while (curTime <= endTime) {
			    dates.add(new Date(curTime));
			    curTime += interval;
			}
			
			
			
			for(int i=0;i<dates.size();i++){
				
				MongoDatabase dbIns = mongoInstance.getDB();
				MongoCollection<Document> coll = dbIns
						.getCollection(PropertiesManager.propertiesMap.get(prefix + Constants.COLLECTION_NAME.getValue()));
				
				MongoCollection<Document> dashboardColl = dbIns
						.getCollection(PropertiesManager.propertiesMap.get(prefix + Constants.DASHBOARD_POPULATOR_NAME.getValue()));
				
				
				
				 Date lDateAll =dates.get(i);
				    String pgDateIndexAll = formatterIndex.format(lDateAll);
				
				String payIdAll = "ALL";
				String dateIndexObjectAll = pgDateIndexAll;
				Double saleCaptureAmountAll = 0.0;
				Double saleCaptureTotalAmountAll = 0.0;
				Double refundCaptureAmountAll = 0.0;
				Double refundCaptureTotalAmountAll = 0.0;
				
				int saleCaptureCountAll = 0;
				int refundCaptureCountAll = 0;
				int failCountAll = 0;
				
				List<Merchants> merchantList = new ArrayList<Merchants>();
				//merchantList = userdao.getActiveMerchantList();
				merchantList = userdao.getActiveMerchantListPgWs();
				
				for (Merchants merchant : merchantList) {
					
					 Date lDate =dates.get(i);
					    String dateStart = formatterFrom.format(lDate);    
					    String dateEnd = formatterTo.format(lDate);
					    String pgDateIndex = formatterIndex.format(lDate);
					    
						BasicDBObject dateQuery = new BasicDBObject();
						BasicDBObject statusQuery = new BasicDBObject();
						BasicDBObject txntypeQuery = new BasicDBObject();
						BasicDBObject allParamQuery = new BasicDBObject();
						BasicDBObject payIdQuery = new BasicDBObject();
						
						if (!fromDate.isEmpty()) {

							String currentDate = null;
							if (!toDate.isEmpty()) {
								currentDate = toDate;
							} else {
								DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
								Calendar cal = Calendar.getInstance();
								currentDate = dateFormat.format(cal.getTime());
							}
							dateQuery.put(FieldType.CREATE_DATE.getName(),
									BasicDBObjectBuilder.start("$gte", new SimpleDateFormat(dateStart).toLocalizedPattern())
											.add("$lte", new SimpleDateFormat(dateEnd).toLocalizedPattern()).get());				
						}

						List<BasicDBObject> dateIndexConditionList = new ArrayList<BasicDBObject>();
						BasicDBObject dateIndexConditionQuery = new BasicDBObject();
						String startString = new SimpleDateFormat(dateStart).toLocalizedPattern();
						String endString = new SimpleDateFormat(dateEnd).toLocalizedPattern();

						DateFormat format1 = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss", Locale.ENGLISH);
						Date dateStart1 = format1.parse(startString);
						Date dateEnd1 = format1.parse(endString);

						LocalDate incrementingDate = dateStart1.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
						LocalDate endDate1 = dateEnd1.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();

						List<String> allDatesIndex = new ArrayList<>();

						while (!incrementingDate.isAfter(endDate1)) {
							allDatesIndex.add(incrementingDate.toString().replaceAll("-", ""));
							incrementingDate = incrementingDate.plusDays(1);
						}

						for (String dateIndex : allDatesIndex) {
							dateIndexConditionList.add(new BasicDBObject("DATE_INDEX", dateIndex));
						}
						if (StringUtils.isNotBlank(PropertiesManager.propertiesMap.get(Constants.USE_DATE_INDEX.getValue()))
								&& PropertiesManager.propertiesMap.get(Constants.USE_DATE_INDEX.getValue()).equalsIgnoreCase("Y")) {
							dateIndexConditionQuery.append("$or", dateIndexConditionList);
						}
						
						List<BasicDBObject> statusQueryConditionList = new ArrayList<BasicDBObject>();
						
						statusQueryConditionList.add(new BasicDBObject(FieldType.STATUS.getName(), StatusType.CAPTURED.getName()));
						statusQueryConditionList.add(new BasicDBObject(FieldType.STATUS.getName(), StatusType.FAILED.getName()));
						statusQuery.put("$or", statusQueryConditionList);
						
						List<BasicDBObject> txnTypeConditionList = new ArrayList<BasicDBObject>();
						
						txnTypeConditionList.add(new BasicDBObject(FieldType.TXNTYPE.getName(), TransactionType.SALE.getName()));
						txnTypeConditionList.add(new BasicDBObject(FieldType.TXNTYPE.getName(), TransactionType.REFUND.getName()));
						
						payIdQuery.put(FieldType.PAY_ID.getName(), merchant.getPayId());
						
						txntypeQuery.put("$or", txnTypeConditionList);
						
							List<BasicDBObject> allConditionQueryList = new ArrayList<BasicDBObject>();
							if (!dateQuery.isEmpty()) {
								allConditionQueryList.add(dateQuery);
							}
							
							if (!dateIndexConditionQuery.isEmpty()) {
								allConditionQueryList.add(dateIndexConditionQuery);
							}
							
							if (!statusQuery.isEmpty()) {
								allConditionQueryList.add(statusQuery);
							}
							
							if (!txntypeQuery.isEmpty()) {
								allConditionQueryList.add(txntypeQuery);
							}
							
							if (!payIdQuery.isEmpty()) {
								allConditionQueryList.add(payIdQuery);
							}
							
							BasicDBObject allConditionQueryObj = new BasicDBObject("$and", allConditionQueryList);
							List<BasicDBObject> fianlList = new ArrayList<BasicDBObject>();

							if (!allParamQuery.isEmpty()) {
								fianlList.add(allParamQuery);
							}
							if (!allConditionQueryObj.isEmpty()) {
								fianlList.add(allConditionQueryObj);
							}

							BasicDBObject finalquery = new BasicDBObject("$and", fianlList);
						
						logger.info("finalquery = "+finalquery);
						
						BasicDBObject match = new BasicDBObject("$match", finalquery);
						BasicDBObject projectElement = new BasicDBObject();
						projectElement.put(FieldType.TXNTYPE.getName(), 1);
						projectElement.put(FieldType.STATUS.getName(), 1);
						projectElement.put(FieldType.PAY_ID.getName(), 1);
						projectElement.put(FieldType.AMOUNT.getName(), 1);
						projectElement.put(FieldType.TOTAL_AMOUNT.getName(), 1);
						
						BasicDBObject project = new BasicDBObject("$project", projectElement);
						

						String payId = merchant.getPayId();
						String dateIndexObject = pgDateIndex;
						Double saleCaptureAmount = 0.0;
						Double saleCaptureTotalAmount = 0.0;
						Double refundCaptureAmount = 0.0;
						Double refundCaptureTotalAmount = 0.0;
						
						int saleCaptureCount = 0;
						int refundCaptureCount = 0;
						int failCount = 0;
						
						logger.info("finalquery dashboard updater query processing for  payId = " + payId + " Date index = "+pgDateIndex);
						
						List<BasicDBObject> pipeline = Arrays.asList(match,project);
						AggregateIterable<Document> output = coll.aggregate(pipeline);
						output.allowDiskUse(true);
						MongoCursor<Document> cursor = output.iterator();
						
						logger.info("finalquery dashboard updater query processed for payId = " + payId + " Date index = "+pgDateIndex);
						while (cursor.hasNext()) {
							
							
							Document doc = cursor.next();
							
							if (doc.getString(FieldType.STATUS.getName()).equalsIgnoreCase(StatusType.FAILED.getName())) {
								failCount = failCount + 1;
								failCountAll = failCountAll + 1;
							}
							
							else if (doc.getString(FieldType.STATUS.getName()).equalsIgnoreCase(StatusType.CAPTURED.getName())) {
								
								if (doc.getString(FieldType.TXNTYPE.getName()).equalsIgnoreCase(TransactionType.SALE.getName()))
								{
									saleCaptureCount = saleCaptureCount + 1;
									saleCaptureAmount = saleCaptureAmount + Double.valueOf(doc.getString(FieldType.AMOUNT.getName()));
									saleCaptureTotalAmount = saleCaptureTotalAmount + Double.valueOf(doc.getString(FieldType.TOTAL_AMOUNT.getName()));
									
									
									saleCaptureCountAll = saleCaptureCountAll + 1;
									saleCaptureAmountAll = saleCaptureAmountAll + Double.valueOf(doc.getString(FieldType.AMOUNT.getName()));
									saleCaptureTotalAmountAll = saleCaptureTotalAmountAll + Double.valueOf(doc.getString(FieldType.TOTAL_AMOUNT.getName()));
								}
								
								else if (doc.getString(FieldType.TXNTYPE.getName()).equalsIgnoreCase(TransactionType.REFUND.getName())) {
									
									refundCaptureCount = refundCaptureCount + 1;
									refundCaptureAmount = refundCaptureAmount + Double.valueOf(doc.getString(FieldType.AMOUNT.getName()));
									refundCaptureTotalAmount = refundCaptureTotalAmount + Double.valueOf(doc.getString(FieldType.TOTAL_AMOUNT.getName()));
									
									refundCaptureCountAll = refundCaptureCountAll + 1;
									refundCaptureAmountAll = refundCaptureAmountAll + Double.valueOf(doc.getString(FieldType.AMOUNT.getName()));
									refundCaptureTotalAmountAll = refundCaptureTotalAmountAll + Double.valueOf(doc.getString(FieldType.TOTAL_AMOUNT.getName()));
									
								}
							}
						}
						
						Document document = new Document();
						
						document.put(FieldType.CREATE_DATE.getName(), pgDateIndex);
						document.put(FieldType.PAY_ID.getName(), payId);
						document.put(FieldType.DASHBOARD_SALE_AMOUNT.getName(), String.format("%.2f", saleCaptureAmount));
						document.put(FieldType.DASHBOARD_SALE_TOTAL_AMOUNT.getName(), String.format("%.2f", saleCaptureTotalAmount));
						document.put(FieldType.DASHBOARD_REFUND_AMOUNT.getName(), String.format("%.2f", refundCaptureAmount));
						document.put(FieldType.DASHBOARD_REFUND_TOTAL_AMOUNT.getName(), String.format("%.2f", refundCaptureTotalAmount));
						document.put(FieldType.DASHBOARD_SALE_COUNT.getName(), String.valueOf(saleCaptureCount));
						document.put(FieldType.DASHBOARD_REFUND_COUNT.getName(), String.valueOf(refundCaptureCount));
						document.put(FieldType.DASHBOARD_FAILED_COUNT.getName(), String.valueOf(failCount));
					    
						logger.info("dashboard values updated for date index = "+pgDateIndex + " merchant = " +payId );
						
						dashboardColl.insertOne(document);
						logger.info("dashboard values added to collection date index = "+pgDateIndex + " merchant = " +payId );
					
				}
				
				
				Document document = new Document();
				
				document.put(FieldType.CREATE_DATE.getName(), pgDateIndexAll);
				document.put(FieldType.PAY_ID.getName(), payIdAll);
				document.put(FieldType.DASHBOARD_SALE_AMOUNT.getName(), String.format("%.2f", saleCaptureAmountAll));
				document.put(FieldType.DASHBOARD_SALE_TOTAL_AMOUNT.getName(), String.format("%.2f", saleCaptureTotalAmountAll));
				document.put(FieldType.DASHBOARD_REFUND_AMOUNT.getName(), String.format("%.2f", refundCaptureAmountAll));
				document.put(FieldType.DASHBOARD_REFUND_TOTAL_AMOUNT.getName(), String.format("%.2f", refundCaptureTotalAmountAll));
				document.put(FieldType.DASHBOARD_SALE_COUNT.getName(), String.valueOf(saleCaptureCountAll));
				document.put(FieldType.DASHBOARD_REFUND_COUNT.getName(), String.valueOf(refundCaptureCountAll));
				document.put(FieldType.DASHBOARD_FAILED_COUNT.getName(), String.valueOf(failCountAll));
				logger.info("PG_DATE_TIME_INDEX updated for date index = "+pgDateIndexAll + " merchant = " +payIdAll );
				
				dashboardColl.insertOne(document);
				logger.info("dashboard values added to collection date index = "+pgDateIndexAll + " merchant = " +payIdAll );
				
				
			}
			
		}
		catch(Exception e) {
			logger.error("Exception ",e);
		}

	}
	
	
}


