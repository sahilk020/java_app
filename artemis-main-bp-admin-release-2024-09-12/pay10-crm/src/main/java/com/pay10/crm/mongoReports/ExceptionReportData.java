package com.pay10.crm.mongoReports;

import java.math.BigInteger;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import org.apache.commons.lang.StringUtils;
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
import com.pay10.commons.mongo.MongoInstance;
import com.pay10.commons.user.ExceptionReport;
import com.pay10.commons.util.Constants;
import com.pay10.commons.util.FieldType;
import com.pay10.commons.util.PropertiesManager;

@Service
public class ExceptionReportData {

	private static Logger logger = LoggerFactory.getLogger(TxnReports.class.getName());
	private static final String alphabaticFileName = "alphabatic-currencycode.properties";

	@Autowired
	PropertiesManager propertiesManager;

	@Autowired
	private MongoInstance mongoInstance;

	private static final String prefix = "MONGO_DB_";

	public int getDataCount(String merchantPayId, String acquirer, String status, String fromDate, String toDate,
			String DB_USER_TYPE) {
		try {
			int total;

			BasicDBObject dateQuery = new BasicDBObject();
			List<BasicDBObject> paramConditionLst = new ArrayList<BasicDBObject>();
			BasicDBObject statusQuery = new BasicDBObject();
			BasicDBObject acquirerQuery = new BasicDBObject();
			BasicDBObject allParamQuery = new BasicDBObject();
			List<BasicDBObject> statusConditionLst = new ArrayList<BasicDBObject>();
			List<BasicDBObject> acquirerConditionLst = new ArrayList<BasicDBObject>();

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
			if (!merchantPayId.equalsIgnoreCase("ALL")) {
				paramConditionLst.add(new BasicDBObject(FieldType.DB_PAY_ID.getName(), merchantPayId));
			}

			if (!acquirer.equalsIgnoreCase("ALL")) {
				paramConditionLst.add(new BasicDBObject(FieldType.DB_ACQUIRER_TYPE.getName(), acquirer));
			}

			if (!status.equalsIgnoreCase("ALL")) {
				paramConditionLst.add(new BasicDBObject(FieldType.EXCEPTION_STATUS.getName(), status));
			}

			paramConditionLst.add(new BasicDBObject(FieldType.DB_USER_TYPE.getName(), DB_USER_TYPE));

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

			BasicDBObject allConditionQueryObj = new BasicDBObject("$and", allConditionQueryList);
			List<BasicDBObject> fianlList = new ArrayList<BasicDBObject>();

			if (!allParamQuery.isEmpty()) {
				fianlList.add(allParamQuery);
			}
			if (!allConditionQueryObj.isEmpty()) {
				fianlList.add(allConditionQueryObj);
			}

			BasicDBObject finalquery = new BasicDBObject("$and", fianlList);

			MongoDatabase dbIns = mongoInstance.getDB();
			MongoCollection<Document> coll = dbIns.getCollection(
					PropertiesManager.propertiesMap.get(prefix + Constants.REPORTING_COLLECTION_NAME.getValue()));
			// MongoCollection<Document> coll = dbIns.getCollection("reporting");

			BasicDBObject match = new BasicDBObject("$match", finalquery);
			// Now the aggregate operation
			Document firstGroup = new Document("_id", new Document("DB_PG_REF_NUM", "$DB_PG_REF_NUM"));
			BasicDBObject firstGroupObject = new BasicDBObject(firstGroup);
			BasicDBObject secondGroup = new BasicDBObject("$push", "$$ROOT");
			BasicDBObject group = new BasicDBObject("$group", firstGroupObject.append("entries", secondGroup));
			BasicDBObject sort = new BasicDBObject("$sort", new BasicDBObject("CREATE_DATE", -1));
			BasicDBObject count = new BasicDBObject("$count", "totalCount");
			List<BasicDBObject> pipeline = Arrays.asList(match, group, sort, count);
			Document output = coll.aggregate(pipeline).first();
			total = (int) output.get("totalCount");
			/*
			 * AggregateIterable<Document> output = coll.aggregate(pipeline);
			 * if(output.first() != null) { Document doc = output.first(); total = (int)
			 * doc.get("totalCount"); } else { total= 0; }
			 */
			// MongoCursor<Document> cursor = output.iterator();
			// MongoCollection<Document> coll = dbIns.getCollection("reporting");
			// total = (int) coll.count(output);

			return total;
		} catch (Exception e) {
			logger.error("Exception occured in TxnReports , searchPaymentCount n exception = " + e);
			return 0;
		}
	}

	public List<ExceptionReport> getData(String merchantPayId, String acquirer, String status, String fromDate,
			String toDate, int start, int length, String DB_USER_TYPE) {
		try {
			List<ExceptionReport> exceptionReportList = new ArrayList<ExceptionReport>();

			BasicDBObject dateQuery = new BasicDBObject();
			List<BasicDBObject> paramConditionLst = new ArrayList<BasicDBObject>();
			BasicDBObject statusQuery = new BasicDBObject();
			BasicDBObject acquirerQuery = new BasicDBObject();
			BasicDBObject allParamQuery = new BasicDBObject();
			List<BasicDBObject> statusConditionLst = new ArrayList<BasicDBObject>();
			List<BasicDBObject> acquirerConditionLst = new ArrayList<BasicDBObject>();

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

			if (!merchantPayId.equalsIgnoreCase("ALL")) {
				paramConditionLst.add(new BasicDBObject(FieldType.DB_PAY_ID.getName(), merchantPayId));
			}

			if (!acquirer.equalsIgnoreCase("ALL")) {
				paramConditionLst.add(new BasicDBObject(FieldType.DB_ACQUIRER_TYPE.getName(), acquirer));
			}

			if (!status.equalsIgnoreCase("ALL")) {
				paramConditionLst.add(new BasicDBObject(FieldType.EXCEPTION_STATUS.getName(), status));
			}

			paramConditionLst.add(new BasicDBObject(FieldType.DB_USER_TYPE.getName(), DB_USER_TYPE));

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

			BasicDBObject allConditionQueryObj = new BasicDBObject("$and", allConditionQueryList);
			List<BasicDBObject> fianlList = new ArrayList<BasicDBObject>();

			if (!allParamQuery.isEmpty()) {
				fianlList.add(allParamQuery);
			}
			if (!allConditionQueryObj.isEmpty()) {
				fianlList.add(allConditionQueryObj);
			}

			BasicDBObject finalquery = new BasicDBObject("$and", fianlList);

			MongoDatabase dbIns = mongoInstance.getDB();
			MongoCollection<Document> coll = dbIns.getCollection(
					PropertiesManager.propertiesMap.get(prefix + Constants.REPORTING_COLLECTION_NAME.getValue()));
			// MongoCollection<Document> coll = dbIns.getCollection("reporting");

			BasicDBObject match = new BasicDBObject("$match", finalquery);
			// Now the aggregate operation
			Document firstGroup = new Document("_id", new Document("DB_PG_REF_NUM", "$DB_PG_REF_NUM"));
			BasicDBObject firstGroupObject = new BasicDBObject(firstGroup);
			BasicDBObject secondGroup = new BasicDBObject("$push", "$$ROOT");
			BasicDBObject group = new BasicDBObject("$group", firstGroupObject.append("entries", secondGroup));
			BasicDBObject sort = new BasicDBObject("$sort", new BasicDBObject("CREATE_DATE", -1));
			BasicDBObject skip = new BasicDBObject("$skip", start);
			BasicDBObject limit = new BasicDBObject("$limit", length);
			List<BasicDBObject> pipeline = Arrays.asList(match, sort, group, skip, limit);
			AggregateIterable<Document> output = coll.aggregate(pipeline);
			output.allowDiskUse(true);
			MongoCursor<Document> cursor = output.iterator();

			// MongoCursor<Document> cursor = coll.find(finalquery).iterator();
			while (cursor.hasNext()) {
				Document myDoc = cursor.next();
				List<Document> lstDoc = (List<Document>) myDoc.get("entries");
				Document doc = lstDoc.get(0);
				ExceptionReport exceptionReport = new ExceptionReport();

				exceptionReport.setPgRefNo(doc.getString(FieldType.DB_PG_REF_NUM.toString()));
				BigInteger txnId = new BigInteger(doc.getString(FieldType.TXN_ID.toString()));
				exceptionReport.setTxnId(txnId);
				exceptionReport.setOrderId(doc.getString(FieldType.DB_ORDER_ID.toString()));
				exceptionReport.setAcqId(doc.getString(FieldType.DB_ACQUIRER_TYPE.toString()));
				exceptionReport.setCreatedDate(doc.getString(FieldType.CREATE_DATE.toString()));
				exceptionReport.setStatus(doc.getString(FieldType.EXCEPTION_STATUS.toString()));
				exceptionReport.setException(doc.getString(FieldType.RESPONSE_MESSAGE.toString()));

				Comparator<ExceptionReport> comp = (ExceptionReport a, ExceptionReport b) -> {

					if (a.getCreatedDate().compareTo(b.getCreatedDate()) > 0) {
						return -1;
					} else if (a.getCreatedDate().compareTo(b.getCreatedDate()) < 0) {
						return 1;
					} else {
						return 0;
					}
				};

				exceptionReportList.add(exceptionReport);
				Collections.sort(exceptionReportList, comp);
			}
			cursor.close();
			return exceptionReportList;

		} catch (Exception e) {
			logger.error("Exception occured in ExceptionReportData , ExceptionReport , Exception = " + e);
			return null;
		}
	}
}
