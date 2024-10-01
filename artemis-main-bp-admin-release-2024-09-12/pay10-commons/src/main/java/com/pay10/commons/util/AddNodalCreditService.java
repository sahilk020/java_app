package com.pay10.commons.util;

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
import com.pay10.commons.mongo.MongoInstance;
import com.pay10.commons.user.AccountCurrencyRegion;
import com.pay10.commons.user.MISReportObject;
import com.pay10.commons.user.NodalPayoutPreviewObject;
import com.pay10.commons.user.NodalReportObject;
import com.pay10.commons.user.User;
import com.pay10.commons.user.UserDao;

@Service
public class AddNodalCreditService {

	@Autowired
	private MongoInstance mongoInstance;

	@Autowired
	private UserDao userDao;

	@Autowired
	PropertiesManager propertiesManager;
	private static Logger logger = LoggerFactory.getLogger(AddNodalCreditService.class.getName());
	private static final String prefix = "MONGO_DB_";

	public List<NodalReportObject> findNodalAmount(String payId, String acquirer, String paymentType,
			String captureDateRange, String settlementDate, String ignorePgRefList) {

		logger.info("Inside findNodalAmount method");
		List<NodalReportObject> nodalReportObjectList = new ArrayList<NodalReportObject>();
		String[] captureDateArray = captureDateRange.split(",");

		for (String captureDate : captureDateArray) {

			try {
				SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss", Locale.ENGLISH);
				Date fromSettledDate = format.parse(settlementDate.replace("/", "-") + " 00:00:00");
				Date toSettledDate = format.parse(settlementDate.replace("/", "-") + " 23:59:59");

				Date fromcaptureDate = format.parse(captureDate.replace("/", "-") + " 00:00:00");
				Date tocaptureDate = format.parse(captureDate.replace("/", "-") + " 23:59:59");

				SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				String fromSettledDateString = formatter.format(fromSettledDate);
				String toSettledDateString = formatter.format(toSettledDate);

				String fromcaptureDateString = formatter.format(fromcaptureDate);
				String tocaptureDateString = formatter.format(tocaptureDate);

				BasicDBObject captureDateQuery = new BasicDBObject();
				BasicDBObject settledDateQuery = new BasicDBObject();
				BasicDBObject merchantQuery = new BasicDBObject();
				BasicDBObject acquirerQuery = new BasicDBObject();
				BasicDBObject paymentTypeQuery = new BasicDBObject();

				if (!fromSettledDateString.isEmpty()) {

					String currentDate = null;
					if (!toSettledDateString.isEmpty()) {
						currentDate = toSettledDateString;
					} else {
						DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
						Calendar cal = Calendar.getInstance();
						currentDate = dateFormat.format(cal.getTime());
					}

					settledDateQuery.put(FieldType.SETTLEMENT_DATE.getName(),
							BasicDBObjectBuilder
									.start("$gte", new SimpleDateFormat(fromSettledDateString).toLocalizedPattern())
									.add("$lte", new SimpleDateFormat(currentDate).toLocalizedPattern()).get());
				}
				
				String currentDate = null;
				if (!fromcaptureDateString.isEmpty()) {

				
					if (!tocaptureDateString.isEmpty()) {
						currentDate = tocaptureDateString;
					} else {
						DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
						Calendar cal = Calendar.getInstance();
						currentDate = dateFormat.format(cal.getTime());
					}

					captureDateQuery.put(FieldType.CAPTURED_DATE.getName(),
							BasicDBObjectBuilder
									.start("$gte", new SimpleDateFormat(fromcaptureDateString).toLocalizedPattern())
									.add("$lte", new SimpleDateFormat(currentDate).toLocalizedPattern()).get());
				}
				
				
				List<BasicDBObject> dateIndexConditionList = new ArrayList<BasicDBObject>();
				BasicDBObject dateIndexConditionQuery = new BasicDBObject();
				String startString = new SimpleDateFormat(fromcaptureDateString).toLocalizedPattern();
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
					dateIndexConditionList.add(new BasicDBObject("DATE_INDEX", dateIndex));
				}
				if (StringUtils.isNotBlank(PropertiesManager.propertiesMap.get(Constants.USE_CAPTURED_DATE_INDEX.getValue()))
						&& PropertiesManager.propertiesMap.get(Constants.USE_CAPTURED_DATE_INDEX.getValue()).equalsIgnoreCase("Y")) {
					
					if (dateIndexConditionList.size() > 0) {
						dateIndexConditionQuery.append("$or", dateIndexConditionList);
					}
					
				}
				
				

				acquirerQuery = new BasicDBObject(FieldType.ACQUIRER_TYPE.getName(), acquirer);
				merchantQuery = new BasicDBObject(FieldType.PAY_ID.getName(), payId);

				List<BasicDBObject> paymentTypeList = new ArrayList<BasicDBObject>();

				if (paymentType.contains("CC")) {
					paymentTypeList.add(
							new BasicDBObject(FieldType.PAYMENT_TYPE.getName(), PaymentType.CREDIT_CARD.getCode()));
					paymentTypeList
							.add(new BasicDBObject(FieldType.PAYMENT_TYPE.getName(), PaymentType.DEBIT_CARD.getCode()));
					paymentTypeQuery.append("$or", paymentTypeList);

				} else {
					paymentTypeList.add(new BasicDBObject(FieldType.PAYMENT_TYPE.getName(),
							PaymentType.getInstanceIgnoreCase(paymentType).getCode()));
					paymentTypeQuery.append("$or", paymentTypeList);
				}

				List<BasicDBObject> allConditionQueryList = new ArrayList<BasicDBObject>();

				if (!acquirerQuery.isEmpty()) {
					allConditionQueryList.add(acquirerQuery);
				}
				if (!merchantQuery.isEmpty()) {
					allConditionQueryList.add(merchantQuery);
				}
				if (!paymentTypeQuery.isEmpty()) {
					allConditionQueryList.add(paymentTypeQuery);
				}
				if (!captureDateQuery.isEmpty()) {
					allConditionQueryList.add(captureDateQuery);
				}
				
				if (!dateIndexConditionQuery.isEmpty()) {
					allConditionQueryList.add(dateIndexConditionQuery);
				}
				
				if (!settledDateQuery.isEmpty()) {
					allConditionQueryList.add(settledDateQuery);
				}
				BasicDBObject allConditionQueryObj = new BasicDBObject("$and", allConditionQueryList);
				List<BasicDBObject> finalList = new ArrayList<BasicDBObject>();

				if (!allConditionQueryObj.isEmpty()) {
					finalList.add(allConditionQueryObj);
				}

				BasicDBObject finalquery = new BasicDBObject("$and", finalList);
				logger.info("Inside findNodalAmount method , final query  = " + finalquery);
				MongoDatabase dbIns = mongoInstance.getDB();
				MongoCollection<Document> coll = dbIns.getCollection(
						PropertiesManager.propertiesMap.get(prefix + Constants.SUMMARY_TRANSACTIONS_NAME.getValue()));
				BasicDBObject match = new BasicDBObject("$match", finalquery);

				List<BasicDBObject> pipeline = Arrays.asList(match);
				AggregateIterable<Document> output = coll.aggregate(pipeline);
				output.allowDiskUse(true);
				MongoCursor<Document> cursor = output.iterator();

				int saleCount = 0;
				double saleAmount = 0;
				int refundCount = 0;
				double refundAmount = 0;
				;

				while (cursor.hasNext()) {

					Document doc = cursor.next();

					if (ignorePgRefList.contains(doc.getString(FieldType.PG_REF_NUM.getName()))) {
						continue;
					}

					if (StringUtils.isNotBlank(doc.getString(FieldType.NODAL_CREDIT_DATE.getName()))) {
						continue;
					}

					if (doc.getString(FieldType.TXNTYPE.getName()).equalsIgnoreCase(TransactionType.SALE.getName())) {
						saleCount++;
						saleAmount = saleAmount + doc.getDouble(FieldType.TOTAL_AMOUNT.getName());
					} else {
						refundCount++;
						refundAmount = refundAmount + doc.getDouble(FieldType.TOTAL_AMOUNT.getName());
					}

				}

				NodalReportObject nodalReportObject = new NodalReportObject();

				nodalReportObject.setAcquirer(acquirer);
				nodalReportObject.setCaptureDate(tocaptureDateString.replace(" 23:59:59", ""));
				nodalReportObject.setNetAmount(String.format("%.2f", saleAmount - refundAmount));
				nodalReportObject.setSaleAmount(String.format("%.2f", saleAmount));
				nodalReportObject.setRefundAmount(String.format("%.2f", refundAmount));
				nodalReportObject.setSaleCount(String.valueOf(saleCount));
				nodalReportObject.setRefundCount(String.valueOf(refundCount));

				nodalReportObjectList.add(nodalReportObject);

			} catch (Exception e) {
				logger.error("Exception occured while updating nodal credit date " + e);
			}

		}

		return nodalReportObjectList;
	}

	public boolean updateNodalCreditDate(String payId, String acquirer, String paymentType, String captureDateRange,
			String settlementDate, String ignorePgRefList, String nodalCreditDate, String nodalDebitDate) {

		logger.info("Inside updateNodalCreditDate method");
		String[] captureDateArray = captureDateRange.split(",");

		for (String captureDate : captureDateArray) {

			try {
				SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss", Locale.ENGLISH);
				Date fromSettledDate = format.parse(settlementDate.replace("/", "-") + " 00:00:00");
				Date toSettledDate = format.parse(settlementDate.replace("/", "-") + " 23:59:59");

				Date fromcaptureDate = format.parse(captureDate.replace("/", "-") + " 00:00:00");
				Date tocaptureDate = format.parse(captureDate.replace("/", "-") + " 23:59:59");

				Date nodalCredit = format.parse(nodalCreditDate.replace("/", "-") + " 01:00:00");
				Date nodalDebit = format.parse(nodalDebitDate.replace("/", "-") + " 01:00:00");

				SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				String fromSettledDateString = formatter.format(fromSettledDate);
				String toSettledDateString = formatter.format(toSettledDate);

				String fromcaptureDateString = formatter.format(fromcaptureDate);
				String tocaptureDateString = formatter.format(tocaptureDate);

				String nodalCreditDateString = formatter.format(nodalCredit);
				String nodalDebitDateString = formatter.format(nodalDebit);

				BasicDBObject captureDateQuery = new BasicDBObject();
				BasicDBObject settledDateQuery = new BasicDBObject();
				BasicDBObject merchantQuery = new BasicDBObject();
				BasicDBObject acquirerQuery = new BasicDBObject();
				BasicDBObject paymentTypeQuery = new BasicDBObject();

				if (!fromSettledDateString.isEmpty()) {

					String currentDate = null;
					if (!toSettledDateString.isEmpty()) {
						currentDate = toSettledDateString;
					} else {
						DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
						Calendar cal = Calendar.getInstance();
						currentDate = dateFormat.format(cal.getTime());
					}

					settledDateQuery.put(FieldType.SETTLEMENT_DATE.getName(),
							BasicDBObjectBuilder
									.start("$gte", new SimpleDateFormat(fromSettledDateString).toLocalizedPattern())
									.add("$lte", new SimpleDateFormat(currentDate).toLocalizedPattern()).get());
				}
				
				
				String currentDate1 = null;
				if (!fromcaptureDateString.isEmpty()) {

					if (!tocaptureDateString.isEmpty()) {
						currentDate1 = tocaptureDateString;
					} else {
						DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
						Calendar cal = Calendar.getInstance();
						currentDate1 = dateFormat.format(cal.getTime());
					}

					captureDateQuery.put(FieldType.CAPTURED_DATE.getName(),
							BasicDBObjectBuilder
									.start("$gte", new SimpleDateFormat(fromcaptureDateString).toLocalizedPattern())
									.add("$lte", new SimpleDateFormat(currentDate1).toLocalizedPattern()).get());
				}

				
				List<BasicDBObject> dateIndexConditionList = new ArrayList<BasicDBObject>();
				BasicDBObject dateIndexConditionQuery = new BasicDBObject();
				String startString = new SimpleDateFormat(fromcaptureDateString).toLocalizedPattern();
				String endString = new SimpleDateFormat(currentDate1).toLocalizedPattern();

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
				if (StringUtils.isNotBlank(PropertiesManager.propertiesMap.get(Constants.USE_CAPTURED_DATE_INDEX.getValue()))
						&& PropertiesManager.propertiesMap.get(Constants.USE_CAPTURED_DATE_INDEX.getValue()).equalsIgnoreCase("Y")) {
					
					if (dateIndexConditionList.size() > 0) {
						dateIndexConditionQuery.append("$or", dateIndexConditionList);
					}
					
				}
				
				
				
				
				acquirerQuery = new BasicDBObject(FieldType.ACQUIRER_TYPE.getName(), acquirer);
				merchantQuery = new BasicDBObject(FieldType.PAY_ID.getName(), payId);

				List<BasicDBObject> paymentTypeList = new ArrayList<BasicDBObject>();

				if (paymentType.contains("CC")) {
					paymentTypeList.add(
							new BasicDBObject(FieldType.PAYMENT_TYPE.getName(), PaymentType.CREDIT_CARD.getCode()));
					paymentTypeList
							.add(new BasicDBObject(FieldType.PAYMENT_TYPE.getName(), PaymentType.DEBIT_CARD.getCode()));
					paymentTypeQuery.append("$or", paymentTypeList);

				} else {
					paymentTypeList.add(new BasicDBObject(FieldType.PAYMENT_TYPE.getName(),
							PaymentType.getInstanceIgnoreCase(paymentType).getCode()));
					paymentTypeQuery.append("$or", paymentTypeList);
				}

				List<BasicDBObject> allConditionQueryList = new ArrayList<BasicDBObject>();

				if (!acquirerQuery.isEmpty()) {
					allConditionQueryList.add(acquirerQuery);
				}
				if (!merchantQuery.isEmpty()) {
					allConditionQueryList.add(merchantQuery);
				}
				if (!paymentTypeQuery.isEmpty()) {
					allConditionQueryList.add(paymentTypeQuery);
				}
				if (!captureDateQuery.isEmpty()) {
					allConditionQueryList.add(captureDateQuery);
				}
				if (!dateIndexConditionQuery.isEmpty()) {
					allConditionQueryList.add(dateIndexConditionQuery);
				}
				if (!settledDateQuery.isEmpty()) {
					allConditionQueryList.add(settledDateQuery);
				}
				BasicDBObject allConditionQueryObj = new BasicDBObject("$and", allConditionQueryList);
				List<BasicDBObject> finalList = new ArrayList<BasicDBObject>();

				if (!allConditionQueryObj.isEmpty()) {
					finalList.add(allConditionQueryObj);
				}

				BasicDBObject finalquery = new BasicDBObject("$and", finalList);
				logger.info("Inside udpate nodal credit  date method , final query  = " + finalquery);
				MongoDatabase dbIns = mongoInstance.getDB();
				MongoCollection<Document> coll = dbIns.getCollection(
						PropertiesManager.propertiesMap.get(prefix + Constants.SUMMARY_TRANSACTIONS_NAME.getValue()));
				BasicDBObject match = new BasicDBObject("$match", finalquery);

				List<BasicDBObject> pipeline = Arrays.asList(match);
				AggregateIterable<Document> output = coll.aggregate(pipeline);
				output.allowDiskUse(true);
				MongoCursor<Document> cursor = output.iterator();

				while (cursor.hasNext()) {

					Document doc = cursor.next();

					if (ignorePgRefList.contains(doc.getString(FieldType.PG_REF_NUM.getName()))) {
						continue;
					}

					if (StringUtils.isNotBlank(doc.getString(FieldType.NODAL_CREDIT_DATE.getName()))) {
						continue;
					}

					List<BasicDBObject> updateCollConditionList = new ArrayList<BasicDBObject>();
					updateCollConditionList.add(new BasicDBObject(FieldType.PG_REF_NUM.getName(),
							doc.getString(FieldType.PG_REF_NUM.getName())));
					updateCollConditionList.add(
							new BasicDBObject(FieldType.ACQ_ID.getName(), doc.getString(FieldType.ACQ_ID.getName())));

					BasicDBObject searchCollQuery = new BasicDBObject("$and", updateCollConditionList);
					Document docUpdate = new Document();
					docUpdate.append("$set",
							new BasicDBObject().append(FieldType.NODAL_CREDIT_DATE.getName(), nodalCreditDateString));
					coll.updateOne(searchCollQuery, docUpdate);

				}

			} catch (Exception e) {
				logger.error("Exception occured while updating nodal credit date " + e);
				return false;
			}

		}
		return true;
	}

	public void updateSettledData(String fromDate, String toDate) {

		logger.info("Inside update settlement data");
		try {

			BasicDBObject dateQuery = new BasicDBObject();
			List<BasicDBObject> paramConditionLst = new ArrayList<BasicDBObject>();
			BasicDBObject currencyQuery = new BasicDBObject();
			BasicDBObject acquirerQuery = new BasicDBObject();

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
				
				if (dateIndexConditionList.size() > 0) {
					dateIndexConditionQuery.append("$or", dateIndexConditionList);
				}
				
			}

			List<BasicDBObject> recoConditionList = new ArrayList<BasicDBObject>();
			recoConditionList.add(new BasicDBObject(FieldType.TXNTYPE.getName(), TransactionType.RECO.getName()));
			recoConditionList.add(new BasicDBObject(FieldType.STATUS.getName(), StatusType.SETTLED_SETTLE.getName()));

			BasicDBObject recoConditionQuery = new BasicDBObject("$and", recoConditionList);

			List<BasicDBObject> recoRefundConditionList = new ArrayList<BasicDBObject>();
			recoRefundConditionList
					.add(new BasicDBObject(FieldType.TXNTYPE.getName(), TransactionType.REFUNDRECO.getName()));
			recoRefundConditionList.add(new BasicDBObject(FieldType.STATUS.getName(), StatusType.SETTLED_SETTLE.getName()));

			BasicDBObject recoRefundConditionQuery = new BasicDBObject("$and", recoRefundConditionList);

			saleOrAuthList.add(recoConditionQuery);
			saleOrAuthList.add(recoRefundConditionQuery);

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
			logger.info("finalquery for settlement data update = " + finalquery);
			MongoDatabase dbIns = mongoInstance.getDB();
			MongoCollection<Document> coll = dbIns
					.getCollection(PropertiesManager.propertiesMap.get(prefix + Constants.COLLECTION_NAME.getValue()));

			MongoCollection<Document> summaryColl = dbIns.getCollection(
					PropertiesManager.propertiesMap.get(prefix + Constants.SUMMARY_TRANSACTIONS_NAME.getValue()));

			MongoCollection<Document> duplicateColl = dbIns.getCollection(
					PropertiesManager.propertiesMap.get(prefix + Constants.DUPLICATE_SETTLEMENT_NAME.getValue()));

			BasicDBObject match = new BasicDBObject("$match", finalquery);
			List<BasicDBObject> pipeline = Arrays.asList(match);
			AggregateIterable<Document> output = coll.aggregate(pipeline);
			output.allowDiskUse(true);
			MongoCursor<Document> cursor = output.iterator();
			int updateCount = 0;
			ArrayList<String> pgRefList = new ArrayList<String>();
			while (cursor.hasNext()) {

				Document doc = cursor.next();

				if (pgRefList.contains(
						doc.getString(FieldType.PG_REF_NUM.getName()) + doc.getString(FieldType.ACQ_ID.getName()))) {

					logger.info("Duplicate settlement found for PG_REF_NUM : "
							+ doc.getString(FieldType.PG_REF_NUM.getName()) + "  ACQ_ID : "
							+ doc.getString(FieldType.ACQ_ID.getName()));
					doc.put(FieldType.RESPONSE_MESSAGE.getName(),
							"Duplicate settlement found with this PG_REF_NUM and ACQ_ID");
					duplicateColl.insertOne(doc);
				}
				pgRefList
						.add(doc.getString(FieldType.PG_REF_NUM.getName()) + doc.getString(FieldType.ACQ_ID.getName()));

				List<BasicDBObject> updateConditionList = new ArrayList<BasicDBObject>();
				updateConditionList.add(new BasicDBObject(FieldType.PG_REF_NUM.getName(),
						doc.getString(FieldType.PG_REF_NUM.getName())));
				updateConditionList
						.add(new BasicDBObject(FieldType.ACQ_ID.getName(), doc.getString(FieldType.ACQ_ID.getName())));

				BasicDBObject searchQuery = new BasicDBObject("$and", updateConditionList);

				long recordsCount = summaryColl.count(searchQuery);

				if (recordsCount == 0) {
					logger.info("No Records found for PG_REF_NUM : " + doc.getString(FieldType.PG_REF_NUM.getName())
							+ "  ACQ_ID : " + doc.getString(FieldType.ACQ_ID.getName()));
					doc.put(FieldType.RESPONSE_MESSAGE.getName(), "No Record found with this PG_REF_NUM and ACQ_ID");
					duplicateColl.insertOne(doc);
				} else if (recordsCount > 1) {
					logger.info("Update Settled Data , found more than one record for PG_REF_NUM : "
							+ doc.getString(FieldType.PG_REF_NUM.getName()) + "  ACQ_ID : "
							+ doc.getString(FieldType.ACQ_ID.getName()));
					doc.put(FieldType.RESPONSE_MESSAGE.getName(),
							"More than one Record found with this PG_REF_NUM and ACQ_ID");
					duplicateColl.insertOne(doc);
				} else {
					List<BasicDBObject> updateCollConditionList = new ArrayList<BasicDBObject>();
					updateCollConditionList.add(new BasicDBObject(FieldType.PG_REF_NUM.getName(),
							doc.getString(FieldType.PG_REF_NUM.getName())));
					updateCollConditionList.add(
							new BasicDBObject(FieldType.ACQ_ID.getName(), doc.getString(FieldType.ACQ_ID.getName())));

					BasicDBObject searchCollQuery = new BasicDBObject("$and", updateCollConditionList);
					Document docUpdate = new Document();
					docUpdate.append("$set", new BasicDBObject().append(FieldType.SETTLEMENT_DATE.getName(),
							doc.getString(FieldType.CREATE_DATE.getName())));
					summaryColl.updateOne(searchCollQuery, docUpdate);
					updateCount++;
				}
			}
			logger.info("Found " + updateCount + " transactions in iPayTransactions for date range " + fromDate + " TO "
					+ toDate);
		} catch (Exception e) {
			logger.error("Exception occured " + e);
		}
	}

	public List<MISReportObject> nodalMisReportDownload(String merchantPayId, String acquirer, String currency,
			String fromDate, String toDate) {

		logger.info("Inside Nodal MIS report query");

		List<MISReportObject> transactionList = new ArrayList<MISReportObject>();

		try {
		BasicDBObject dateQuery = new BasicDBObject();
		List<BasicDBObject> paramConditionLst = new ArrayList<BasicDBObject>();
		BasicDBObject currencyQuery = new BasicDBObject();
		BasicDBObject acquirerQuery = new BasicDBObject();
		BasicDBObject allParamQuery = new BasicDBObject();
		List<BasicDBObject> acquirerConditionLst = new ArrayList<BasicDBObject>();
		Map<String, User> userMap = new HashMap<String, User>();

		String currentDate = null;
		if (!fromDate.isEmpty()) {
			
			if (!toDate.isEmpty()) {
				currentDate = toDate;
			} else {
				DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				Calendar cal = Calendar.getInstance();
				currentDate = dateFormat.format(cal.getTime());
			}

			dateQuery.put(FieldType.CAPTURED_DATE.getName(),
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
			dateIndexConditionList.add(new BasicDBObject("DATE_INDEX", dateIndex));
		}
		if (StringUtils.isNotBlank(PropertiesManager.propertiesMap.get(Constants.USE_CAPTURED_DATE_INDEX.getValue()))
				&& PropertiesManager.propertiesMap.get(Constants.USE_CAPTURED_DATE_INDEX.getValue()).equalsIgnoreCase("Y")) {
			
			if (dateIndexConditionList.size() > 0) {
				dateIndexConditionQuery.append("$or", dateIndexConditionList);
			}
			
		}
		
		
		
		
		if (!merchantPayId.equalsIgnoreCase("ALL")) {
			paramConditionLst.add(new BasicDBObject(FieldType.PAY_ID.getName(), merchantPayId));
		}
		if (!currency.equalsIgnoreCase("ALL")) {
			paramConditionLst.add(new BasicDBObject(FieldType.CURRENCY_CODE.getName(), currency));
		}

		if (!acquirer.equalsIgnoreCase("ALL")) {
			List<String> acquirerList = Arrays.asList(acquirer.split(","));
			for (String acq : acquirerList) {

				acquirerConditionLst.add(new BasicDBObject(FieldType.ACQUIRER_TYPE.getName(), acq.trim()));
			}
			acquirerQuery.append("$or", acquirerConditionLst);

		}

		if (!paramConditionLst.isEmpty()) {
			allParamQuery = new BasicDBObject("$and", paramConditionLst);
		}

		List<BasicDBObject> fianlList = new ArrayList<BasicDBObject>();
		if (!currencyQuery.isEmpty()) {
			fianlList.add(currencyQuery);
		}
		if (!acquirerQuery.isEmpty()) {
			fianlList.add(acquirerQuery);
		}
		if (!dateQuery.isEmpty()) {
			fianlList.add(dateQuery);
		}
		if (!dateIndexConditionQuery.isEmpty()) {
			fianlList.add(dateIndexConditionQuery);
		}
		
		if (!allParamQuery.isEmpty()) {
			fianlList.add(allParamQuery);
		}
		BasicDBObject finalquery = new BasicDBObject("$and", fianlList);

		MongoDatabase dbIns = mongoInstance.getDB();
		MongoCollection<Document> coll = dbIns.getCollection(
				PropertiesManager.propertiesMap.get(prefix + Constants.SUMMARY_TRANSACTIONS_NAME.getValue()));

		logger.info("Inside Nodal MIS report query , finalquery = " + finalquery);

		BasicDBObject match = new BasicDBObject("$match", finalquery);

		List<BasicDBObject> pipeline = Arrays.asList(match);
		AggregateIterable<Document> output = coll.aggregate(pipeline);
		output.allowDiskUse(true);
		MongoCursor<Document> cursor = output.iterator();

		while (cursor.hasNext()) {
			Document doc = cursor.next();
			if (StringUtils.isBlank(doc.getString(FieldType.NODAL_CREDIT_DATE.toString()))) {
				continue;
			}

			MISReportObject transReport = new MISReportObject();

			if (userMap.get(doc.getString(FieldType.PAY_ID.toString())) != null) {

				transReport.setMerchants(userMap.get(doc.getString(FieldType.PAY_ID.toString())).getBusinessName());
			} else {
				User user = userDao.findPayId(doc.getString(FieldType.PAY_ID.toString()));
				transReport.setMerchants(user.getBusinessName());
				userMap.put(user.getPayId(), user);
			}

			transReport.setTransactionId(doc.getString(FieldType.PG_REF_NUM.toString()));
			transReport.setDateFrom(doc.getString(FieldType.SETTLEMENT_DATE.getName()));
			transReport.setPayId(doc.getString(FieldType.PAY_ID.toString()));
			transReport.setPgRefNum(doc.getString(FieldType.PG_REF_NUM.toString()));
			transReport.setMopType(doc.getString(FieldType.MOP_TYPE.toString()));
			transReport.setAcquirerType(doc.getString(FieldType.ACQUIRER_TYPE.toString()));
			transReport.setNodalCreditDate(doc.getString(FieldType.NODAL_CREDIT_DATE.toString()));
			transReport.setNodalPayoutInitiationDate(doc.getString(FieldType.NODAL_PAYOUT_INITIATED_DATE.toString()));

			transReport.setTxnType(doc.getString(FieldType.TXNTYPE.toString()));

			transReport.setPaymentMethods(doc.getString(FieldType.PAYMENT_TYPE.toString()));

			transReport.setTotalAmount(String.valueOf(doc.getDouble(FieldType.TOTAL_AMOUNT.toString())));
			transReport.setAmount(String.valueOf(doc.getDouble(FieldType.AMOUNT.toString())));

			if (doc.getString(FieldType.TXNTYPE.toString()).contains("REFUND")) {

				transReport.setGrossTransactionAmt(
						String.format("%.2f", doc.getDouble(FieldType.TOTAL_AMOUNT.toString()) * -1));
				transReport.setAggregatorCommissionAMT((String.format("%.2f",
						(((doc.getDouble(FieldType.TOTAL_AMOUNT.toString()) - doc.getDouble(FieldType.AMOUNT.toString())
								- doc.getDouble(FieldType.SURCHARGE_ACQ.toString())
								- doc.getDouble(FieldType.GST_ACQ.toString())) * -1)))));

				transReport.setAcquirerCommissionAMT(
						String.format("%.2f", ((doc.getDouble(FieldType.SURCHARGE_ACQ.toString())
								+ doc.getDouble(FieldType.GST_ACQ.toString())) * -1)));
				transReport.setTotalAmtPayable(String.format("%.2f", doc.getDouble(FieldType.AMOUNT.toString()) * -1));
				transReport.setTotalPayoutNodalAccount(
						String.format("%.2f", doc.getDouble(FieldType.TOTAL_AMOUNT.toString()) * -1));

			} else {

				transReport.setGrossTransactionAmt(
						String.format("%.2f", doc.getDouble(FieldType.TOTAL_AMOUNT.toString())));
				transReport.setAggregatorCommissionAMT((String.format("%.2f",
						(((doc.getDouble(FieldType.TOTAL_AMOUNT.toString()) - doc.getDouble(FieldType.AMOUNT.toString())
								- doc.getDouble(FieldType.SURCHARGE_ACQ.toString())
								- doc.getDouble(FieldType.GST_ACQ.toString())) * 1)))));

				transReport.setAcquirerCommissionAMT(
						String.format("%.2f", ((doc.getDouble(FieldType.SURCHARGE_ACQ.toString())
								+ doc.getDouble(FieldType.GST_ACQ.toString())) * 1)));
				transReport.setTotalAmtPayable(String.format("%.2f", doc.getDouble(FieldType.AMOUNT.toString()) * 1));
				transReport.setTotalPayoutNodalAccount(
						String.format("%.2f", doc.getDouble(FieldType.TOTAL_AMOUNT.toString()) * 1));

			}

			if (StringUtils.isBlank(doc.getString(FieldType.SURCHARGE_FLAG.toString()))) {
			} else {
				transReport.setSurchargeFlag("Y");
			}
			transReport.setTransactionDate(doc.getString(FieldType.CAPTURED_DATE.toString()));
			transReport.setOrderId(doc.getString(FieldType.ORDER_ID.toString()));
			transReport.setRefundFlag(doc.getString(FieldType.REFUND_FLAG.toString()));

			if (StringUtils.isNotBlank(doc.getString(FieldType.PAYMENTS_REGION.toString()))) {
				transReport.setTransactionRegion(doc.getString(FieldType.PAYMENTS_REGION.toString()));
			} else {
				transReport.setTransactionRegion(AccountCurrencyRegion.DOMESTIC.toString());
			}

			transactionList.add(transReport);
		}
		cursor.close();

		logger.info("Cursor closed for MIS Nodal report query , transactionList Size = " + transactionList.size());
		return transactionList;
		
		}
		catch(Exception e) {
			logger.error("Exception "+e);
		}
		return transactionList;
	}

	public void updateAndDownloadNodalMisReport(String merchantPayId, String acquirer, String currency, String fromDate,
			String toDate, String payoutDate) {

		logger.info("Inside Nodal MIS update and report query");

		try {
			BasicDBObject dateQuery = new BasicDBObject();
			List<BasicDBObject> paramConditionLst = new ArrayList<BasicDBObject>();
			BasicDBObject currencyQuery = new BasicDBObject();
			BasicDBObject acquirerQuery = new BasicDBObject();
			BasicDBObject allParamQuery = new BasicDBObject();
			List<BasicDBObject> acquirerConditionLst = new ArrayList<BasicDBObject>();
			Map<String, User> userMap = new HashMap<String, User>();
			String currentDate = null;
			if (!fromDate.isEmpty()) {
			
				if (!toDate.isEmpty()) {
					currentDate = toDate;
				} else {
					DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
					Calendar cal = Calendar.getInstance();
					currentDate = dateFormat.format(cal.getTime());
				}

				dateQuery.put(FieldType.CAPTURED_DATE.getName(),
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
			if (StringUtils.isNotBlank(PropertiesManager.propertiesMap.get(Constants.USE_CAPTURED_DATE_INDEX.getValue()))
					&& PropertiesManager.propertiesMap.get(Constants.USE_CAPTURED_DATE_INDEX.getValue()).equalsIgnoreCase("Y")) {
				
				if (dateIndexConditionList.size() > 0) {
					dateIndexConditionQuery.append("$or", dateIndexConditionList);
				}
				
			}
			
			
			
			if (!merchantPayId.equalsIgnoreCase("ALL")) {
				paramConditionLst.add(new BasicDBObject(FieldType.PAY_ID.getName(), merchantPayId));
			}
			if (!currency.equalsIgnoreCase("ALL")) {
				paramConditionLst.add(new BasicDBObject(FieldType.CURRENCY_CODE.getName(), currency));
			}

			if (!acquirer.equalsIgnoreCase("ALL")) {
				List<String> acquirerList = Arrays.asList(acquirer.split(","));
				for (String acq : acquirerList) {

					acquirerConditionLst.add(new BasicDBObject(FieldType.ACQUIRER_TYPE.getName(), acq.trim()));
				}
				acquirerQuery.append("$or", acquirerConditionLst);

			}

			if (!paramConditionLst.isEmpty()) {
				allParamQuery = new BasicDBObject("$and", paramConditionLst);
			}

			List<BasicDBObject> fianlList = new ArrayList<BasicDBObject>();
			if (!currencyQuery.isEmpty()) {
				fianlList.add(currencyQuery);
			}
			if (!acquirerQuery.isEmpty()) {
				fianlList.add(acquirerQuery);
			}
			if (!dateQuery.isEmpty()) {
				fianlList.add(dateQuery);
			}
			if (!dateIndexConditionQuery.isEmpty()) {
				fianlList.add(dateIndexConditionQuery);
			}
			if (!allParamQuery.isEmpty()) {
				fianlList.add(allParamQuery);
			}
			BasicDBObject finalquery = new BasicDBObject("$and", fianlList);

			MongoDatabase dbIns = mongoInstance.getDB();
			MongoCollection<Document> coll = dbIns.getCollection(
					PropertiesManager.propertiesMap.get(prefix + Constants.SUMMARY_TRANSACTIONS_NAME.getValue()));

			logger.info("Inside Nodal MIS report query with update , finalquery = " + finalquery);

			BasicDBObject match = new BasicDBObject("$match", finalquery);

			List<BasicDBObject> pipeline = Arrays.asList(match);
			AggregateIterable<Document> output = coll.aggregate(pipeline);
			output.allowDiskUse(true);
			MongoCursor<Document> cursor = output.iterator();

			while (cursor.hasNext()) {

				Document doc = cursor.next();

				if (StringUtils.isBlank(doc.getString(FieldType.NODAL_CREDIT_DATE.getName()))) {
					continue;
				}

				if (StringUtils.isNotBlank(doc.getString(FieldType.NODAL_PAYOUT_INITIATED_DATE.getName()))) {
					continue;
				}

				List<BasicDBObject> updateCollConditionList = new ArrayList<BasicDBObject>();
				updateCollConditionList.add(new BasicDBObject(FieldType.PG_REF_NUM.getName(),
						doc.getString(FieldType.PG_REF_NUM.getName())));
				updateCollConditionList
						.add(new BasicDBObject(FieldType.ACQ_ID.getName(), doc.getString(FieldType.ACQ_ID.getName())));

				BasicDBObject searchCollQuery = new BasicDBObject("$and", updateCollConditionList);
				Document docUpdate = new Document();
				docUpdate.append("$set",
						new BasicDBObject().append(FieldType.NODAL_PAYOUT_INITIATED_DATE.getName(), payoutDate));
				coll.updateOne(searchCollQuery, docUpdate);

			}
			cursor.close();
		}

		catch (Exception e) {
			logger.error("Exception in updating payout initiation date " + e);
		}

	}

	public List<NodalPayoutPreviewObject> previewNodalPayout(String merchantPayId, String acquirer, String currency,
			String fromDate, String toDate, String payoutDate) {

		logger.info("Inside Nodal payout preview method");
		List<NodalPayoutPreviewObject> transactionList = new ArrayList<NodalPayoutPreviewObject>();
		try {
			BasicDBObject dateQuery = new BasicDBObject();
			List<BasicDBObject> paramConditionLst = new ArrayList<BasicDBObject>();
			BasicDBObject currencyQuery = new BasicDBObject();
			BasicDBObject acquirerQuery = new BasicDBObject();
			BasicDBObject allParamQuery = new BasicDBObject();
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

				dateQuery.put(FieldType.CAPTURED_DATE.getName(),
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
			if (StringUtils.isNotBlank(PropertiesManager.propertiesMap.get(Constants.USE_CAPTURED_DATE_INDEX.getValue()))
					&& PropertiesManager.propertiesMap.get(Constants.USE_CAPTURED_DATE_INDEX.getValue()).equalsIgnoreCase("Y")) {
				
				if (dateIndexConditionList.size() > 0) {
					dateIndexConditionQuery.append("$or", dateIndexConditionList);
				}
				
			}
			
			
			if (!merchantPayId.equalsIgnoreCase("ALL")) {
				paramConditionLst.add(new BasicDBObject(FieldType.PAY_ID.getName(), merchantPayId));
			}
			if (!currency.equalsIgnoreCase("ALL")) {
				paramConditionLst.add(new BasicDBObject(FieldType.CURRENCY_CODE.getName(), currency));
			}

			if (!acquirer.equalsIgnoreCase("ALL")) {
				List<String> acquirerList = Arrays.asList(acquirer.split(","));
				for (String acq : acquirerList) {

					acquirerConditionLst.add(new BasicDBObject(FieldType.ACQUIRER_TYPE.getName(), acq.trim()));
				}
				acquirerQuery.append("$or", acquirerConditionLst);

			}

			if (!paramConditionLst.isEmpty()) {
				allParamQuery = new BasicDBObject("$and", paramConditionLst);
			}

			List<BasicDBObject> fianlList = new ArrayList<BasicDBObject>();
			if (!currencyQuery.isEmpty()) {
				fianlList.add(currencyQuery);
			}
			if (!acquirerQuery.isEmpty()) {
				fianlList.add(acquirerQuery);
			}
			if (!dateQuery.isEmpty()) {
				fianlList.add(dateQuery);
			}
			if (!dateIndexConditionQuery.isEmpty()) {
				fianlList.add(dateIndexConditionQuery);
			}
			if (!allParamQuery.isEmpty()) {
				fianlList.add(allParamQuery);
			}
			BasicDBObject finalquery = new BasicDBObject("$and", fianlList);

			MongoDatabase dbIns = mongoInstance.getDB();
			MongoCollection<Document> coll = dbIns.getCollection(
					PropertiesManager.propertiesMap.get(prefix + Constants.SUMMARY_TRANSACTIONS_NAME.getValue()));

			logger.info("Inside Nodal payout preview query with update , finalquery = " + finalquery);

			BasicDBObject match = new BasicDBObject("$match", finalquery);

			List<BasicDBObject> pipeline = Arrays.asList(match);
			AggregateIterable<Document> output = coll.aggregate(pipeline);
			output.allowDiskUse(true);
			MongoCursor<Document> cursor = output.iterator();

			int saleCount = 0;
			int refundCount = 0;

			double grossTxnAmount = 0;
			double totalIpayComm = 0;
			double totalAcqComm = 0;
			double totalMerchantAmt = 0;
			double totalPayable = 0;

			NodalPayoutPreviewObject nodalPayoutPreviewObject = new NodalPayoutPreviewObject();
			
			while (cursor.hasNext()) {

				Document doc = cursor.next();

				if (StringUtils.isBlank(doc.getString(FieldType.NODAL_CREDIT_DATE.getName()))) {
					continue;
				}

				if (StringUtils.isNotBlank(doc.getString(FieldType.NODAL_PAYOUT_INITIATED_DATE.getName()))) {
					continue;
				}

				if (doc.getString(FieldType.TXNTYPE.getName()).equalsIgnoreCase(TransactionType.SALE.getName())) {
					saleCount = saleCount + 1;

					grossTxnAmount = grossTxnAmount + doc.getDouble(FieldType.TOTAL_AMOUNT.getName());
					totalIpayComm = totalIpayComm + doc.getDouble(FieldType.SURCHARGE_IPAY.getName())
							+ doc.getDouble(FieldType.GST_IPAY.getName());
					totalAcqComm = totalAcqComm + doc.getDouble(FieldType.SURCHARGE_ACQ.getName())
							+ doc.getDouble(FieldType.GST_ACQ.getName());
					totalMerchantAmt = totalMerchantAmt + doc.getDouble(FieldType.AMOUNT.getName());
					totalPayable = totalPayable + doc.getDouble(FieldType.TOTAL_AMOUNT.getName());
					
				} else {
					
					refundCount = refundCount + 1;
					grossTxnAmount = grossTxnAmount - doc.getDouble(FieldType.TOTAL_AMOUNT.getName());
					totalIpayComm = totalIpayComm - doc.getDouble(FieldType.SURCHARGE_IPAY.getName())
					- doc.getDouble(FieldType.GST_IPAY.getName());
					totalAcqComm = totalAcqComm - doc.getDouble(FieldType.SURCHARGE_ACQ.getName())
							- doc.getDouble(FieldType.GST_ACQ.getName());
					totalMerchantAmt = totalMerchantAmt - doc.getDouble(FieldType.AMOUNT.getName());
					totalPayable = totalPayable - doc.getDouble(FieldType.TOTAL_AMOUNT.getName());
				}
			}
			
			nodalPayoutPreviewObject.setSaleCount(String.valueOf(saleCount));
			nodalPayoutPreviewObject.setRefundCount(String.valueOf(refundCount));
			nodalPayoutPreviewObject.setGrossTxnAmount(String.format( "%.2f",grossTxnAmount));
			nodalPayoutPreviewObject.setTotalIpayComm(String.format( "%.2f",totalIpayComm));
			nodalPayoutPreviewObject.setTotalAcqComm(String.format( "%.2f",totalAcqComm));
			nodalPayoutPreviewObject.setTotalMerchantAmt(String.format( "%.2f",totalMerchantAmt));
			nodalPayoutPreviewObject.setTotalPayable(String.format( "%.2f",totalPayable));
			transactionList.add(nodalPayoutPreviewObject);
			cursor.close();

			return transactionList;
		}

		catch (Exception e) {
			logger.error("Exception in preview payout initiation date " + e);
		}
		return transactionList;
	}

	public List<MISReportObject> nodalExceptionReport(String merchantPayId, String fromDate, String toDate) {

		logger.info("Inside Nodal Exceptions report");

		List<MISReportObject> transactionList = new ArrayList<MISReportObject>();

		BasicDBObject dateQuery = new BasicDBObject();
		List<BasicDBObject> paramConditionLst = new ArrayList<BasicDBObject>();
		BasicDBObject allParamQuery = new BasicDBObject();
		Map<String, User> userMap = new HashMap<String, User>();

		if (!fromDate.isEmpty()) {

			String currentDate = null;
			if (!toDate.isEmpty()) {
				currentDate = toDate;
			} else {
				DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				Calendar cal = Calendar.getInstance();
				currentDate = dateFormat.format(cal.getTime());
			}

			dateQuery.put(FieldType.PG_DATE_TIME.getName(),
					BasicDBObjectBuilder.start("$gte", new SimpleDateFormat(fromDate).toLocalizedPattern())
							.add("$lte", new SimpleDateFormat(currentDate).toLocalizedPattern()).get());
		}
		if (!merchantPayId.equalsIgnoreCase("ALL")) {
			paramConditionLst.add(new BasicDBObject(FieldType.PAY_ID.getName(), merchantPayId));
		}

		if (!paramConditionLst.isEmpty()) {
			allParamQuery = new BasicDBObject("$and", paramConditionLst);
		}

		List<BasicDBObject> fianlList = new ArrayList<BasicDBObject>();
		if (!dateQuery.isEmpty()) {
			fianlList.add(dateQuery);
		}

		if (!allParamQuery.isEmpty()) {
			fianlList.add(allParamQuery);
		}
		BasicDBObject finalquery = new BasicDBObject("$and", fianlList);

		MongoDatabase dbIns = mongoInstance.getDB();
		MongoCollection<Document> coll = dbIns.getCollection(
				PropertiesManager.propertiesMap.get(prefix + Constants.DUPLICATE_SETTLEMENT_NAME.getValue()));

		logger.info("Inside Nodal Exceptions report query , finalquery = " + finalquery);

		BasicDBObject match = new BasicDBObject("$match", finalquery);

		List<BasicDBObject> pipeline = Arrays.asList(match);
		AggregateIterable<Document> output = coll.aggregate(pipeline);
		output.allowDiskUse(true);
		MongoCursor<Document> cursor = output.iterator();
		int srNo = 1;
		while (cursor.hasNext()) {
			Document doc = cursor.next();

			MISReportObject transReport = new MISReportObject();

			if (userMap.get(doc.getString(FieldType.PAY_ID.toString())) != null) {

				transReport.setMerchants(userMap.get(doc.getString(FieldType.PAY_ID.toString())).getBusinessName());
			} else {
				User user = userDao.findPayId(doc.getString(FieldType.PAY_ID.toString()));
				transReport.setMerchants(user.getBusinessName());
				userMap.put(user.getPayId(), user);
			}
			transReport.setPgRefNum(String.valueOf(srNo));
			srNo = srNo++;
			transReport.setPgRefNum(doc.getString(FieldType.PG_REF_NUM.toString()));
			transReport.setDateFrom(doc.getString(FieldType.CREATE_DATE.getName()));
			transReport.setOrderId(doc.getString(FieldType.ORDER_ID.toString()));
			transReport.setTransactionDate(doc.getString(FieldType.PG_DATE_TIME.toString()));
			transReport.setDateFrom(doc.getString(FieldType.CREATE_DATE.toString()));
			transReport.setTxnType(doc.getString(FieldType.TXNTYPE.toString()));
			transReport.setAmount(doc.getString(FieldType.AMOUNT.toString()));
			transReport.setTotalAmount(doc.getString(FieldType.TOTAL_AMOUNT.toString()));

			if (StringUtils.isNotBlank(doc.getString(FieldType.POST_SETTLED_FLAG.toString()))) {
				transReport.setPostSettledFlag(doc.getString(FieldType.POST_SETTLED_FLAG.toString()));
			} else {
				transReport.setPostSettledFlag("");
			}

			if (StringUtils.isNotBlank(doc.getString(FieldType.ACQ_ID.toString()))) {
				transReport.setAcqId(doc.getString(FieldType.ACQ_ID.toString()));
			} else {
				transReport.setAcqId("");
			}

			if (StringUtils.isNotBlank(doc.getString(FieldType.RRN.toString()))) {
				transReport.setRrn(doc.getString(FieldType.RRN.toString()));
			} else {
				transReport.setRrn("");
			}

			if (StringUtils.isNotBlank(doc.getString(FieldType.ARN.toString()))) {
				transReport.setArn(doc.getString(FieldType.ARN.toString()));
			} else {
				transReport.setArn("");
			}

			transReport.setAcquirerType(doc.getString(FieldType.ACQUIRER_TYPE.toString()));
			transReport.setPaymentMethods(doc.getString(FieldType.PAYMENT_TYPE.toString()));
			transReport.setMopType(doc.getString(FieldType.MOP_TYPE.toString()));
			transReport.setRemarks(doc.getString(FieldType.RESPONSE_MESSAGE.toString()));

			transactionList.add(transReport);
		}
		cursor.close();

		logger.info(
				"Cursor closed for Exception Nodal report query , transactionList Size = " + transactionList.size());
		return transactionList;

	}

}
