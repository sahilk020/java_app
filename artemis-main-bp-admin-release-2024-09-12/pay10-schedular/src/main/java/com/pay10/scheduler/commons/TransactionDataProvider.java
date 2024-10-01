package com.pay10.scheduler.commons;
import static com.mongodb.client.model.Sorts.descending;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.mongodb.BasicDBObject;
import com.mongodb.BasicDBObjectBuilder;
import com.mongodb.client.AggregateIterable;
import com.mongodb.client.DistinctIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Aggregates;
import com.pay10.commons.mongo.MongoInstance;
import com.pay10.commons.util.FieldType;
import com.pay10.commons.util.StatusType;
import com.slack.api.Slack;
import com.slack.api.methods.MethodsClient;
import com.slack.api.methods.SlackApiException;
import com.slack.api.methods.request.chat.ChatPostMessageRequest;
import com.slack.api.methods.response.chat.ChatPostMessageResponse;

@Service
public class TransactionDataProvider {

	@Autowired
	private MongoInstance mongoInstance;

	@Autowired
	private ConfigurationProvider configurationProvider;

	private static final Logger logger = LoggerFactory.getLogger(TransactionDataProvider.class);

	public Set<String> fetchTransactionData() {

		Set<String> pgRefSet = new HashSet<String>();
		try {

			String minutesBefore = configurationProvider.getMinutesBefore();
			String minutesInterval = configurationProvider.getMinutesInterval();

			DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			String timeNow = sdf.format(new Date()).toString();

			LocalDateTime datetime = LocalDateTime.parse(timeNow, formatter);
			LocalDateTime datetime2 = LocalDateTime.parse(timeNow, formatter);

			datetime = datetime.minusMinutes(Integer.valueOf(minutesBefore));
			String endTime = datetime.format(formatter);

			datetime2 = datetime2.minusMinutes(Integer.valueOf(minutesInterval) + Integer.valueOf(minutesBefore));
			String startTime = datetime2.format(formatter);

			logger.info("Scheduler status enquiry Start Time = " + startTime);
			logger.info("Scheduler status enquiry End  Time = " + endTime);
			Set<String> allPgRefSet = new HashSet<String>();

			BasicDBObject dateTimeQuery = new BasicDBObject();
			BasicDBObject txnTypQuery = new BasicDBObject();

			dateTimeQuery.put(FieldType.CREATE_DATE.getName(),
					BasicDBObjectBuilder.start("$gte", new SimpleDateFormat(startTime).toLocalizedPattern())
							.add("$lt", new SimpleDateFormat(endTime).toLocalizedPattern()).get());

			txnTypQuery.put(FieldType.TXNTYPE.getName(), "SALE");
			List<BasicDBObject> allConditionQueryList = new ArrayList<BasicDBObject>();

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

			allConditionQueryList.add(dateTimeQuery);
			allConditionQueryList.add(txnTypQuery);

			if (StringUtils.isNotBlank(merchantPayId) && !merchantPayId.equalsIgnoreCase("ALL")) {
				allConditionQueryList.add(merchantQuery);
			}

			if (StringUtils.isNotBlank(acquirer) && !acquirer.equalsIgnoreCase("ALL")) {
				allConditionQueryList.add(acquirerQuery);
			}

			BasicDBObject finalquery = new BasicDBObject("$and", allConditionQueryList);

			logger.info("Query to get data for status enquiry = " + finalquery);
			MongoDatabase dbIns = mongoInstance.getDB();
			MongoCollection<Document> collection = dbIns
					.getCollection(configurationProvider.getMONGO_DB_collectionName());

			BasicDBObject match = new BasicDBObject("$match", finalquery);
			logger.info("Query to get data for status enquiry [Match] = " + match);
			List<BasicDBObject> pipeline = Arrays.asList(match);
			AggregateIterable<Document> output = collection.aggregate(pipeline);
			output.allowDiskUse(true);
			MongoCursor<Document> cursor = output.iterator();
			while (cursor.hasNext()) {
				Document dbobj = cursor.next();

				if (null != dbobj.get(FieldType.PG_REF_NUM.getName())
						&& !dbobj.getString(FieldType.PG_REF_NUM.getName()).equalsIgnoreCase("0")) {
					allPgRefSet.add(dbobj.getString(FieldType.PG_REF_NUM.getName()));
				}

			}

			logger.info("Set of All allPgRefSet prepared with total number of OID : " + allPgRefSet.size());
			cursor.close();

			for (String pgRefNum : allPgRefSet) {

				boolean isCaptured = false;
				boolean isenrolled = false;
				String pgRef = null;

				BasicDBObject pgRefNumQuery = new BasicDBObject(FieldType.PG_REF_NUM.getName(), pgRefNum);
				BasicDBObject txnTypeQuery = new BasicDBObject(FieldType.TXNTYPE.getName(), "SALE");

				List<BasicDBObject> conditionList = new ArrayList<BasicDBObject>();
				conditionList.add(pgRefNumQuery);
				conditionList.add(txnTypeQuery);

				BasicDBObject query = new BasicDBObject("$and", conditionList);

				MongoDatabase dbIns1 = mongoInstance.getDB();
				MongoCollection<Document> collection1 = dbIns1
						.getCollection(configurationProvider.getMONGO_DB_statusEnquiryCollectionName());

				BasicDBObject match1 = new BasicDBObject("$match", query);

				List<BasicDBObject> pipeline1 = Arrays.asList(match1);
				AggregateIterable<Document> output1 = collection1.aggregate(pipeline1);
				output1.allowDiskUse(true);

				MongoCursor<Document> cursor1 = output1.iterator();
				while (cursor1.hasNext()) {
					Document dbobj = cursor1.next();

					if (dbobj.get(FieldType.STATUS.getName()) != null
							&& StringUtils.isNotBlank(dbobj.getString(FieldType.STATUS.getName()))) {
						// Done By chetan nagaria for change in settlement process to mark transaction as RNS
						if (dbobj.getString(FieldType.STATUS.getName()).equalsIgnoreCase(StatusType.CAPTURED.getName())
								|| dbobj.getString(FieldType.STATUS.getName())
										.equalsIgnoreCase(StatusType.SETTLED_SETTLE.getName())|| dbobj.getString(FieldType.STATUS.getName())
										.equalsIgnoreCase(StatusType.SETTLED_RECONCILLED.getName())) {
							isCaptured = true;
						}

						if (dbobj.getString(FieldType.STATUS.getName())
								.equalsIgnoreCase(StatusType.SENT_TO_BANK.getName())
								|| dbobj.getString(FieldType.STATUS.getName())
										.equalsIgnoreCase(StatusType.ENROLLED.getName()) || 
										dbobj.getString(FieldType.STATUS.getName())
										.equalsIgnoreCase(StatusType.DENIED.getName())) { //added status for denied as it is due to real time status mismatch

							pgRef = dbobj.getString(FieldType.PG_REF_NUM.getName());
						}

					}

					if (dbobj.get(FieldType.ACQUIRER_TYPE.getName()) != null
							&& StringUtils.isNotBlank(dbobj.getString(FieldType.ACQUIRER_TYPE.getName()))) {
						isenrolled = true;
					}

				}
				cursor1.close();

				if (!isCaptured && isenrolled) {

					if (StringUtils.isNotBlank(pgRef)) {
						pgRefSet.add(pgRef);
					}
				}
			}

			return pgRefSet;
		}

		catch (Exception e) {
			logger.error("Exception in getting data for status enquiry", e);
		}
		return pgRefSet;

	}

	
	
	public Set<String> fetchTransactionDataPayout() {
		Set<String> allPgRefSet = new HashSet<String>();

		try {

			String minutesBefore = configurationProvider.getMinutesBeforePayoutEnquiry();
			String minutesInterval = configurationProvider.getMinutesIntervalPayoutEnquiry();

			DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			String timeNow = sdf.format(new Date()).toString();

			LocalDateTime datetime = LocalDateTime.parse(timeNow, formatter);
			LocalDateTime datetime2 = LocalDateTime.parse(timeNow, formatter);

			datetime = datetime.minusMinutes(Integer.valueOf(minutesBefore));
			String endTime = datetime.format(formatter);

			datetime2 = datetime2.minusMinutes(Integer.valueOf(minutesInterval) + Integer.valueOf(minutesBefore));
			String startTime = datetime2.format(formatter);

			logger.info("Scheduler status enquiry Start Time = " + startTime);
			logger.info("Scheduler status enquiry End  Time = " + endTime);

			BasicDBObject dateTimeQuery = new BasicDBObject();
			BasicDBObject txnTypQuery = new BasicDBObject();

			dateTimeQuery.put(FieldType.CREATE_DATE.getName(),
					BasicDBObjectBuilder.start("$gte", new SimpleDateFormat(startTime).toLocalizedPattern())
							.add("$lt", new SimpleDateFormat(endTime).toLocalizedPattern()).get());

			txnTypQuery.put(FieldType.TXNTYPE.getName(), "SALE");
			List<BasicDBObject> allConditionQueryList = new ArrayList<BasicDBObject>();

			String acquirer = configurationProvider.getAcquirerNamePayout();

			BasicDBObject acquirerQuery = new BasicDBObject();

			List<BasicDBObject> acquirerQueryList = new ArrayList<BasicDBObject>();

		

			if (StringUtils.isNotBlank(acquirer) && !acquirer.equalsIgnoreCase("ALL")) {

				String acquirerArr[] = acquirer.split(",");

				for (String acq : acquirerArr) {
					acquirerQueryList.add(new BasicDBObject(FieldType.ACQUIRER_TYPE.getName(), acq));
				}

			}

			acquirerQuery.put("$or", acquirerQueryList);

			allConditionQueryList.add(dateTimeQuery);
			allConditionQueryList.add(txnTypQuery);


			if (StringUtils.isNotBlank(acquirer) && !acquirer.equalsIgnoreCase("ALL")) {
				allConditionQueryList.add(acquirerQuery);
			}

			BasicDBObject finalquery = new BasicDBObject("$and", allConditionQueryList);

			logger.info("Query to get data for status enquiry = " + finalquery);
			MongoDatabase dbIns = mongoInstance.getDB();
			MongoCollection<Document> collection = dbIns
					.getCollection("POtransactionStatus");

			BasicDBObject match = new BasicDBObject("$match", finalquery);
			logger.info("Query to get data for status enquiry  Payout[Match] = " + match);
			List<BasicDBObject> pipeline = Arrays.asList(match);
			AggregateIterable<Document> output = collection.aggregate(pipeline);
			output.allowDiskUse(true);
			MongoCursor<Document> cursor = output.iterator();
			while (cursor.hasNext()) {
				Document dbobj = cursor.next();

				if (null != dbobj.get(FieldType.PG_REF_NUM.getName())
						&& !dbobj.getString(FieldType.PG_REF_NUM.getName()).equalsIgnoreCase("0")) {
					allPgRefSet.add(dbobj.getString(FieldType.PG_REF_NUM.getName()));
				}

			}

			logger.info("Set of All allPgRefSet prepared Payout with total number of OID : " + allPgRefSet.size());
			cursor.close();

			

			return allPgRefSet;
		}

		catch (Exception e) {
			logger.error("Exception in getting data for status enquiry", e);
		}
		return allPgRefSet;

	}

	public Set<String> fetchTransactionPayoutData() {

		Set<String> orderIdSet = new HashSet<String>();
		try {

			String minutesBefore = configurationProvider.getMinutesBeforePayout();
			String minutesInterval = "200";

			DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			String timeNow = sdf.format(new Date()).toString();
			logger.info("Scheduler status enquiry Start Time = " + minutesBefore);
			logger.info("Scheduler status enquiry Start Time = " + minutesInterval);

			LocalDateTime datetime = LocalDateTime.parse(timeNow, formatter);
			LocalDateTime datetime2 = LocalDateTime.parse(timeNow, formatter);

			datetime = datetime.minusMinutes(Integer.valueOf(minutesBefore));
			String endTime = datetime.format(formatter);

			datetime2 = datetime2.minusMinutes(Integer.valueOf(minutesInterval) + Integer.valueOf(minutesBefore));
			String startTime = datetime2.format(formatter);

			logger.info("Scheduler status enquiry Start Time = " + startTime);
			logger.info("Scheduler status enquiry End  Time = " + endTime);
			Set<String> allPgRefSet = new HashSet<String>();

			BasicDBObject dateTimeQuery = new BasicDBObject();
			BasicDBObject txnTypQuery = new BasicDBObject();

			dateTimeQuery.put(FieldType.CREATE_DATE.getName(),
					BasicDBObjectBuilder.start("$gte", new SimpleDateFormat(startTime).toLocalizedPattern())
							.add("$lt", new SimpleDateFormat(endTime).toLocalizedPattern()).get());

			txnTypQuery.put(FieldType.TXNTYPE.getName(), "SALE");
			List<BasicDBObject> allConditionQueryList = new ArrayList<BasicDBObject>();

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

			

			merchantQuery.put("$or", merchantQueryList);

			allConditionQueryList.add(dateTimeQuery);
			allConditionQueryList.add(txnTypQuery);
			allConditionQueryList.add(new BasicDBObject().append("STATUS", "Pending"));


			if (StringUtils.isNotBlank(merchantPayId) && !merchantPayId.equalsIgnoreCase("ALL")) {
				allConditionQueryList.add(merchantQuery);
			}

			BasicDBObject finalquery = new BasicDBObject("$and", allConditionQueryList);

			logger.info("Query to get data for payout = " + finalquery);
			MongoDatabase dbIns = mongoInstance.getDB();
			logger.info("Query to get data for payout= " + dbIns);

			MongoCollection<Document> collection = dbIns
					.getCollection("POtransactionStatus");

			BasicDBObject match = new BasicDBObject("$match", finalquery);
			logger.info("Query to get data for payout= " + match);
			List<BasicDBObject> pipeline = Arrays.asList(match);
			AggregateIterable<Document> output = collection.aggregate(pipeline);

			output.allowDiskUse(true);
			MongoCursor<Document> cursor = output.iterator();

			while (cursor.hasNext()) {
				Document dbobj = cursor.next();
				logger.info("Query to get data for payout= " + dbobj);

				if (null != dbobj.get(FieldType.ORDER_ID.getName())
						&& !dbobj.getString(FieldType.ORDER_ID.getName()).equalsIgnoreCase("0")) {
					allPgRefSet.add(dbobj.getString(FieldType.ORDER_ID.getName()));
				}

			}

			logger.info("Set of All order Id  prepared with total number of OID : " + allPgRefSet.size());
			cursor.close();


			return allPgRefSet;
		}

		catch (Exception e) {
			logger.error("Exception in getting data for payout", e);
		}
		return orderIdSet;

	}
	
	
	public Set<String> fetchTransactionPayoutDataStatusCheck() {

		Set<String> orderIdSet = new HashSet<String>();
		try {

			String minutesBefore = configurationProvider.getMinutesBeforePayout();
			String minutesInterval = "200";

			DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			String timeNow = sdf.format(new Date()).toString();
			logger.info("Scheduler status enquiry Start Time = " + minutesBefore);
			logger.info("Scheduler status enquiry Start Time = " + minutesInterval);

			LocalDateTime datetime = LocalDateTime.parse(timeNow, formatter);
			LocalDateTime datetime2 = LocalDateTime.parse(timeNow, formatter);

			datetime = datetime.minusMinutes(Integer.valueOf(minutesBefore));
			String endTime = datetime.format(formatter);

			datetime2 = datetime2.minusMinutes(Integer.valueOf(minutesInterval) + Integer.valueOf(minutesBefore));
			String startTime = datetime2.format(formatter);

			logger.info("Scheduler status enquiry Start Time = " + startTime);
			logger.info("Scheduler status enquiry End  Time = " + endTime);
			Set<String> allPgRefSet = new HashSet<String>();

			BasicDBObject dateTimeQuery = new BasicDBObject();
			BasicDBObject txnTypQuery = new BasicDBObject();

			dateTimeQuery.put(FieldType.CREATE_DATE.getName(),
					BasicDBObjectBuilder.start("$gte", new SimpleDateFormat(startTime).toLocalizedPattern())
							.add("$lt", new SimpleDateFormat(endTime).toLocalizedPattern()).get());

			txnTypQuery.put(FieldType.TXNTYPE.getName(), "SALE");
			List<BasicDBObject> allConditionQueryList = new ArrayList<BasicDBObject>();

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

			

			merchantQuery.put("$or", merchantQueryList);

			allConditionQueryList.add(dateTimeQuery);
			allConditionQueryList.add(txnTypQuery);
			allConditionQueryList.add(new BasicDBObject().append("STATUS", "Sent to Bank"));


			if (StringUtils.isNotBlank(merchantPayId) && !merchantPayId.equalsIgnoreCase("ALL")) {
				allConditionQueryList.add(merchantQuery);
			}

			BasicDBObject finalquery = new BasicDBObject("$and", allConditionQueryList);

			logger.info("Query to get data for payout = " + finalquery);
			MongoDatabase dbIns = mongoInstance.getDB();
			logger.info("Query to get data for payout= " + dbIns);

			MongoCollection<Document> collection = dbIns
					.getCollection("POtransactionStatus");

			BasicDBObject match = new BasicDBObject("$match", finalquery);
			logger.info("Query to get data for payout= " + match);
			List<BasicDBObject> pipeline = Arrays.asList(match);
			AggregateIterable<Document> output = collection.aggregate(pipeline);

			output.allowDiskUse(true);
			MongoCursor<Document> cursor = output.iterator();

			while (cursor.hasNext()) {
				Document dbobj = cursor.next();
				logger.info("Query to get data for payout= " + dbobj);

				if (null != dbobj.get(FieldType.PG_REF_NUM.getName())
						&& !dbobj.getString(FieldType.PG_REF_NUM.getName()).equalsIgnoreCase("0")) {
					allPgRefSet.add(dbobj.getString(FieldType.PG_REF_NUM.getName()));
				}

			}

			logger.info("Set of All order Id  prepared with total number of OID : " + allPgRefSet.size());
			cursor.close();


			return allPgRefSet;
		}

		catch (Exception e) {
			logger.error("Exception in getting data for payout", e);
		}
		return orderIdSet;

	}
	
//	public Set<String> FetchPayuRefundData() {
//		Set<String> allRefundOrderId = new HashSet<String>();
//
//		List<BasicDBObject> dbObjList = new ArrayList<BasicDBObject>();
//		dbObjList.add(new BasicDBObject(FieldType.ACQUIRER_TYPE.getName(),"PAYU" ));
//		dbObjList.add(new BasicDBObject(FieldType.TXNTYPE.getName(), "REFUND"));
//		dbObjList.add(new BasicDBObject(FieldType.STATUS.getName(),"Pending"));
//
//		BasicDBObject andQuery = new BasicDBObject("$and", dbObjList);
//		MongoDatabase dbIns = mongoInstance.getDB();
//		MongoCollection<Document> coll = dbIns
//				.getCollection("transactionStatus");
//		MongoCursor<Document> cursor = coll.find(andQuery).iterator();
//		
//		cursor.close();
//		logger.info("data for payu refund  status enquiry");
//		return allRefundOrderId;
//
//	
//	}
//	
	
	public Set<String> FetchPayuRefundData() {
		Set<String> allRefundOrderId = new HashSet<String>();

		try {
			List<BasicDBObject> dbObjList = new ArrayList<BasicDBObject>();

			
			
			dbObjList.add(new BasicDBObject(FieldType.ACQUIRER_TYPE.getName(),"PAYU" ));
			dbObjList.add(new BasicDBObject(FieldType.TXNTYPE.getName(), "REFUND"));
			dbObjList.add(new BasicDBObject(FieldType.STATUS.getName(),"Pending"));
	
			BasicDBObject finalquery = new BasicDBObject("$and", dbObjList);

			logger.info("findLastTransaction, finalquery : " + finalquery.toString());
			MongoDatabase dbIns = mongoInstance.getDB();
			MongoCollection<Document> collection = dbIns.getCollection("transactionStatus");

			//FindIterable<Document> output = collection.find(finalquery);
			//logger.info("findLastTransaction : " + output);
			MongoCursor<Document> result = collection.find(finalquery).iterator();

			while (result.hasNext()) {
				Document dbobj = (Document) result.next();
				if (null != dbobj.get(FieldType.REFUND_ORDER_ID.getName())
						&& !dbobj.getString(FieldType.REFUND_ORDER_ID.getName()).equalsIgnoreCase("0")) {
					allRefundOrderId.add(dbobj.getString(FieldType.REFUND_ORDER_ID.getName()));
				}			}
			
		
			logger.info("PAYU status ENquiry:: pending docs size={} " + allRefundOrderId);
		} catch (Exception e) {
			logger.error("fetchPendingTransactionData:: failed.", e);
		}
		return allRefundOrderId ;
	
	}
	public List<Document> fetchPendingTransactionData() {
		List<Document> docs = new ArrayList<>();
		try {
			Calendar cal = Calendar.getInstance();
			DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
		
			cal.add(Calendar.DATE, -1);
			BasicDBObject dateTimeQuery = new BasicDBObject();
			BasicDBObject txnTypQuery = new BasicDBObject();
			
			List<BasicDBObject> statusQuery = new ArrayList<>();
			List<BasicDBObject> allConditionQueryList = new ArrayList<BasicDBObject>();
			
			String startDate = StringUtils.join(dateFormat.format(cal.getTime()), " 00:00:00");
			String endDate = StringUtils.join(dateFormat.format(cal.getTime()), " 23:59:59");
			dateTimeQuery.put(FieldType.CREATE_DATE.getName(),BasicDBObjectBuilder.start("$gt",new SimpleDateFormat(startDate).toLocalizedPattern())
							                 .add("$lte", new SimpleDateFormat(endDate).toLocalizedPattern()).get());
			txnTypQuery.put(FieldType.TXNTYPE.getName(),"SALE");
			List<String> pendingStatus = Arrays.asList(StringUtils.split(StatusType.getInternalStatus("Pending"), ","));
	
		    pendingStatus.forEach(status -> statusQuery.add(new BasicDBObject("STATUS", status)));
		    
		    allConditionQueryList.add(dateTimeQuery);
			allConditionQueryList.add(txnTypQuery);
			allConditionQueryList.add(new BasicDBObject("$or", statusQuery));
			BasicDBObject finalquery = new BasicDBObject("$and", allConditionQueryList);
			logger.info("finalquery data...={}",finalquery);
			
			MongoDatabase dbIns = mongoInstance.getDB();
			MongoCollection<Document> collection = dbIns.getCollection("transactionStatus");
			logger.info("collection data...={}",collection);

			BasicDBObject match = new BasicDBObject("$match", finalquery);
			logger.info("fetchPendingTransactionData:: final query={}", match);
			
			
			List<BasicDBObject> pipeline = Arrays.asList(match);
			logger.info("pipeline..={}",pipeline.toArray());
			
			
			AggregateIterable<Document> output = collection.aggregate(pipeline);
			logger.info("output..={}",output.toString());
			
			
			output.allowDiskUse(true);
			MongoCursor<Document> cursor = output.iterator();
			logger.info("cursor..={}",cursor.toString());
			while (cursor.hasNext()) {
				logger.info("invokedddd for get pending data");
				Document dbobj = cursor.next();
				docs.add(dbobj);
			}
			cursor.close();
			logger.info("fetchPendingTransactionData:: pending docs size={} " + docs.size());
		} catch (Exception e) {
			logger.error("fetchPendingTransactionData:: failed.", e);
		}
		return docs;
	}
	
	public List<Document> fetchPendingRefundSbiUPIData() {
		List<Document> documents = new ArrayList<Document>();
		try {
			
			List<BasicDBObject> paramConditionLst = new ArrayList<BasicDBObject>();
			
			Calendar cal = Calendar.getInstance();
			DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
		
			cal.add(Calendar.DATE, -1);
			BasicDBObject dateTimeQuery = new BasicDBObject();
			
			String startDate = StringUtils.join(dateFormat.format(cal.getTime()), " 00:00:00");
			String endDate = StringUtils.join(dateFormat.format(cal.getTime()), " 23:59:59");
			
			dateTimeQuery.put(FieldType.CREATE_DATE.getName(),BasicDBObjectBuilder.start("$gt",new SimpleDateFormat(startDate).toLocalizedPattern())
							                 .add("$lte", new SimpleDateFormat(endDate).toLocalizedPattern()).get());
			
			paramConditionLst.add(dateTimeQuery);
			
			paramConditionLst.add(new BasicDBObject(FieldType.PAYMENT_TYPE.getName(), "UP"));
			paramConditionLst.add(new BasicDBObject(FieldType.STATUS.getName(), "Pending"));
			paramConditionLst.add(new BasicDBObject(FieldType.TXNTYPE.getName(), "REFUND"));
			paramConditionLst.add(new BasicDBObject(FieldType.MOP_TYPE.getName(), "UP"));
			paramConditionLst.add(new BasicDBObject(FieldType.ACQUIRER_TYPE.getName(), "SBI"));
			
			BasicDBObject finalquery = new BasicDBObject("$and", paramConditionLst);

			logger.info("findLastTransaction, finalquery : " + finalquery.toString());
			MongoDatabase dbIns = mongoInstance.getDB();
			MongoCollection<Document> collection = dbIns.getCollection("transactionStatus");

			//FindIterable<Document> output = collection.find(finalquery);
			//logger.info("findLastTransaction : " + output);
			MongoCursor<Document> result = collection.find(finalquery).iterator();

			while (result.hasNext()) {
				Document document = (Document) result.next();
				documents.add(document);
			}
			
			logger.info("fetchPendingTransactionData:: pending docs size={} " + documents.size());
		} catch (Exception e) {
			logger.error("fetchPendingTransactionData:: failed.", e);
		}
		return documents;
	}
	
	
	@SuppressWarnings("unchecked")
	public void fetchtransactionForAlert() {
	
		try{
			long diffTime = Long.parseLong(configurationProvider.getAlertTimdiff());
		

		List<BasicDBObject> paramConditionLst = new ArrayList<BasicDBObject>();
		BasicDBObject limit = new BasicDBObject("$limit", 1);

		paramConditionLst.add(limit);

		MongoDatabase dbIns = mongoInstance.getDB();
		MongoCollection<Document> coll = dbIns.getCollection("transaction");
		List<Bson>bsons=Arrays.asList(Aggregates.sort(descending(FieldType.UPDATE_DATE.getName())), limit);
		logger.info("Query :\n"+bsons);
		MongoCursor<Document> cursor = coll
				.aggregate(bsons)
				.iterator();
		
		while (cursor.hasNext()) {
			Document document1 = (Document) cursor.next();
			JSONObject document = new JSONObject(document1.toJson());
			System.out.println(document);
			String  date = document.getString(FieldType.UPDATE_DATE.getName());
			long difftimeMin=dateforAlert(date);
			logger.info("date for last transcation "+date+difftimeMin);
			if(difftimeMin>=diffTime) {
				sendSlackAlert(date,diffTime);
			}
			


		}
	} catch (Exception e) {
		sendSlackAlertexcrp();
		logger.error("fetchtransactionForAlert:: failed.", e);
	}
	}

	public void sendSlackAlert(String date,long difftime) {
		String Channel = configurationProvider.getSlackchannel();
		String Token = configurationProvider.getSlackToken();

		Slack slack = Slack.getInstance();

		MethodsClient methods = slack.methods(Token);

		ChatPostMessageRequest request = ChatPostMessageRequest.builder()
				.channel(Channel)
				.text(":fire: There is no transaction received for the last "+difftime+" minutes at Pay10 ." +
						"The last transaction was received at "+date+" :fire:")
				.iconEmoji(":rotating_light:")
				.username("Transaction Alert")
				.build();

		try {
			ChatPostMessageResponse response = methods.chatPostMessage(request);
		} catch (IOException e) {
			throw new RuntimeException(e);
		} catch (SlackApiException e) {
			throw new RuntimeException(e);
		}
		
	}
	public void sendSlackAlertexcrp() {
		String Channel = configurationProvider.getSlackchannel();
		String Token = configurationProvider.getSlackToken();

		Slack slack = Slack.getInstance();

		MethodsClient methods = slack.methods(Token);

		ChatPostMessageRequest request = ChatPostMessageRequest.builder()
				.channel(Channel)
				//.channel("C04GC8UAPCZ") // Use a channel ID `C1234567` is preferable
				.text(":fire: We are not able to connect mongodb ." +
						"Please check  :fire:")
				.iconEmoji(":rotating_light:")
				.username("Transaction Alert")
				.build();

		try {
			ChatPostMessageResponse response = methods.chatPostMessage(request);
		} catch (IOException e) {
			throw new RuntimeException(e);
		} catch (SlackApiException e) {
			throw new RuntimeException(e);
		}
		
	}
	public long dateforAlert(String date ) {
		
		SimpleDateFormat sdfDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Date now = new Date();
		String strDate = sdfDate.format(now);

	DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

	LocalDateTime dateTime1= LocalDateTime.parse(date, formatter);
	LocalDateTime dateTime2= LocalDateTime.parse(strDate, formatter);

	long diffInMinutes = java.time.Duration.between(dateTime1, dateTime2).toMinutes();
	return diffInMinutes;
	}

	public List<Document> fetchOnlineRefundTransactionData() {

		List<Document> documents = new ArrayList<Document>();
		try {

			List<BasicDBObject> paramConditionLst = new ArrayList<BasicDBObject>();

			paramConditionLst.add(new BasicDBObject(FieldType.STATUS.getName(), "REFUND_INITIATED"));
			paramConditionLst.add(new BasicDBObject(FieldType.TXNTYPE.getName(), "REFUND"));
			paramConditionLst.add(new BasicDBObject("REFUND_MODE", "ONLINE"));
			paramConditionLst.add(new BasicDBObject("REFUND_PROCESS", "P"));

			BasicDBObject finalquery = new BasicDBObject("$and", paramConditionLst);

			logger.info("fetchOnlineRefundTransactionData, finalquery : " + finalquery.toString());
			MongoDatabase dbIns = mongoInstance.getDB();
			MongoCollection<Document> collection = dbIns.getCollection("transactionStatus");
			MongoCursor<Document> result = collection.find(finalquery).iterator();

			while (result.hasNext()) {
				Document document = (Document) result.next();
				documents.add(document);
			}

			logger.info("fetchOnlineRefundTransactionData:: Sent to Bank docs size={} " + documents.size());
		} catch (Exception e) {
			logger.error("fetchOnlineRefundTransactionData:: failed.", e);
		}
		return documents;

	}

	public List<String> fetchOfflineRefundTransactionData() {

		List<Document> documents = new ArrayList<Document>();
		List<String> acquirerList = new ArrayList<String>();
		try {

			List<BasicDBObject> paramConditionLst = new ArrayList<BasicDBObject>();

			Calendar cal = Calendar.getInstance();
			DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

			//cal.add(Calendar.DATE, -1);
			BasicDBObject dateTimeQuery = new BasicDBObject();

			String startDate = StringUtils.join(dateFormat.format(cal.getTime()), " 00:00:00");
			String endDate = StringUtils.join(dateFormat.format(cal.getTime()), " 23:59:59");

			dateTimeQuery.put(FieldType.CREATE_DATE.getName(),
					BasicDBObjectBuilder.start("$gt", new SimpleDateFormat(startDate).toLocalizedPattern())
							.add("$lte", new SimpleDateFormat(endDate).toLocalizedPattern()).get());

			paramConditionLst.add(dateTimeQuery);

			paramConditionLst.add(new BasicDBObject(FieldType.STATUS.getName(), "REFUND_INITIATED"));
			paramConditionLst.add(new BasicDBObject(FieldType.TXNTYPE.getName(), "REFUND"));
			paramConditionLst.add(new BasicDBObject("REFUND_MODE", "OFFLINE"));

			BasicDBObject finalquery = new BasicDBObject("$and", paramConditionLst);

			logger.info("fetchOfflineRefundTransactionData, finalquery : " + finalquery.toString());
			MongoDatabase dbIns = mongoInstance.getDB();
			MongoCollection<Document> collection = dbIns.getCollection("transactionStatus");

			DistinctIterable<String> output = collection.distinct("ACQUIRER_TYPE", finalquery, String.class);
			MongoCursor<String> cursor = output.iterator();

			while (cursor.hasNext()) {
				String value = cursor.next();
				acquirerList.add(value);
			}

			logger.info("AcquirerList " + acquirerList);

			logger.info("fetchOfflineRefundTransactionData:: Sent to Bank docs size={} " + documents.size());
		} catch (Exception e) {
			logger.error("fetchOfflineRefundTransactionData:: failed.", e);
		}
		return acquirerList;

	}

	public List<Document> fetchOfflineRefundTransactionDataNew(String acquirerType) {

		List<Document> documents = new ArrayList<Document>();
		try {

			List<BasicDBObject> paramConditionLst = new ArrayList<BasicDBObject>();

			Calendar cal = Calendar.getInstance();
			DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

			cal.add(Calendar.DATE, -1);
			BasicDBObject dateTimeQuery = new BasicDBObject();

			String startDate = StringUtils.join(dateFormat.format(cal.getTime()), " 00:00:00");
			String endDate = StringUtils.join(dateFormat.format(cal.getTime()), " 23:59:59");

			dateTimeQuery.put(FieldType.CREATE_DATE.getName(),
					BasicDBObjectBuilder.start("$gt", new SimpleDateFormat(startDate).toLocalizedPattern())
							.add("$lte", new SimpleDateFormat(endDate).toLocalizedPattern()).get());

			paramConditionLst.add(dateTimeQuery);

			paramConditionLst.add(new BasicDBObject(FieldType.STATUS.getName(), "REFUND_INITIATED"));
			paramConditionLst.add(new BasicDBObject(FieldType.TXNTYPE.getName(), "REFUND"));
			paramConditionLst.add(new BasicDBObject(FieldType.ACQUIRER_TYPE.getName(), acquirerType));
			paramConditionLst.add(new BasicDBObject("REFUND_MODE", "OFFLINE")); 
			paramConditionLst.add(new BasicDBObject("REFUND_PROCESS", "P"));

			BasicDBObject finalquery = new BasicDBObject("$and", paramConditionLst);

			logger.info("fetchOfflineRefundTransactionData, finalquery : " + finalquery.toString());
			MongoDatabase dbIns = mongoInstance.getDB();
			MongoCollection<Document> collection = dbIns.getCollection("transactionStatus");
			MongoCursor<Document> result = collection.find(finalquery).iterator();

			while (result.hasNext()) {
				Document document = (Document) result.next();
				documents.add(document);
			}

			logger.info("fetchOfflineRefundTransactionData:: Sent to Bank docs size={} " + documents.size());
		} catch (Exception e) {
			logger.error("fetchOfflineRefundTransactionData:: failed.", e);
		}
		return documents;

	}





	public List<Document> fetchIrctcSettlementData(String payId) {
		List<Document> documents = new ArrayList<Document>();
		try {

			List<BasicDBObject> paramConditionLst = new ArrayList<BasicDBObject>();
			Calendar cal = Calendar.getInstance();
			DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

			//cal.add(Calendar.DATE, -1);
			BasicDBObject dateTimeQuery = new BasicDBObject();

			String startDate = StringUtils.join(dateFormat.format(cal.getTime()), " 00:00:00");
			String endDate = StringUtils.join(dateFormat.format(cal.getTime()), " 23:59:59");

			dateTimeQuery.put(FieldType.CREATE_DATE.getName(),
					BasicDBObjectBuilder.start("$gt", new SimpleDateFormat(startDate).toLocalizedPattern())
							.add("$lte", new SimpleDateFormat(endDate).toLocalizedPattern()).get());

			paramConditionLst.add(dateTimeQuery);

			paramConditionLst.add(new BasicDBObject(FieldType.STATUS.getName(), "Captured"));
			paramConditionLst.add(new BasicDBObject(FieldType.TXNTYPE.getName(), "SALE"));
			paramConditionLst.add(new BasicDBObject("PAY_ID", payId)); 
			
			BasicDBObject finalquery = new BasicDBObject("$and", paramConditionLst);

			logger.info("fetchIrctcSettlementData, finalquery : " + finalquery.toString());
			MongoDatabase dbIns = mongoInstance.getDB();
			MongoCollection<Document> collection = dbIns.getCollection("transaction");
			MongoCursor<Document> result = collection.find(finalquery).iterator();

			while (result.hasNext()) {
				Document document = (Document) result.next();
				documents.add(document);
			}

			logger.info("fetchIrctcSettlementData:: Sent to Bank docs size={} " + documents.size());
		} catch (Exception e) {
			logger.error("fetchIrctcSettlementData:: failed.", e);
		}
		return documents;
	}





	public List<Document> fetchirctcRefundFileGenerationData(String payId, String irctcRefundFile) {
		List<Document> documents = new ArrayList<Document>();
		try {

			List<BasicDBObject> paramConditionLst = new ArrayList<BasicDBObject>();
			Calendar cal = Calendar.getInstance();
			DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

			//cal.add(Calendar.DATE, -1);
			BasicDBObject dateTimeQuery = new BasicDBObject();

			String startDate = StringUtils.join(dateFormat.format(cal.getTime()), " 00:00:00");
			String endDate = StringUtils.join(dateFormat.format(cal.getTime()), " 23:59:59");

			dateTimeQuery.put(FieldType.CREATE_DATE.getName(),
					BasicDBObjectBuilder.start("$gt", new SimpleDateFormat(startDate).toLocalizedPattern())
							.add("$lte", new SimpleDateFormat(endDate).toLocalizedPattern()).get());

			paramConditionLst.add(dateTimeQuery);

			paramConditionLst.add(new BasicDBObject(FieldType.STATUS.getName(), "Captured"));
			paramConditionLst.add(new BasicDBObject(FieldType.TXNTYPE.getName(), "REFUND"));
			paramConditionLst.add(new BasicDBObject("PAY_ID", payId));
			paramConditionLst.add(new BasicDBObject(FieldType.IRCTC_REFUND_FILE_TYPE.getName(), irctcRefundFile));
			
			BasicDBObject finalquery = new BasicDBObject("$and", paramConditionLst);

			logger.info("fetchirctcRefundFileGenerationData, finalquery : " + finalquery.toString());
			MongoDatabase dbIns = mongoInstance.getDB();
			MongoCollection<Document> collection = dbIns.getCollection("transaction");
			MongoCursor<Document> result = collection.find(finalquery).iterator();

			while (result.hasNext()) {
				Document document = (Document) result.next();
				documents.add(document);
			}

			logger.info("fetchirctcRefundFileGenerationData:: Sent to Bank docs size={} " + documents.size());
		} catch (Exception e) {
			logger.error("fetchirctcRefundFileGenerationData:: failed.", e);
		}
		return documents;
	}
}
