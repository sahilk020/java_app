package com.pay10.pg.core.fraudPrevention.model;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.ParseException;
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
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.bson.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.mongodb.BasicDBObject;
import com.mongodb.BasicDBObjectBuilder;
import com.mongodb.MongoException;
import com.mongodb.client.AggregateIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import com.pay10.commons.mongo.MongoInstance;
import com.pay10.commons.util.Constants;
import com.pay10.commons.util.FieldType;
import com.pay10.commons.util.MopType;
import com.pay10.commons.util.PaymentType;
import com.pay10.commons.util.PropertiesManager;
import com.pay10.commons.util.StatusType;
import com.pay10.commons.util.TransactionType;

@Service
public class FraudTxnDao {
	private static Logger logger = LoggerFactory.getLogger(FraudTxnDao.class.getName());

	@Autowired
	private MongoInstance mongoInstance;

	private static final String prefix = "MONGO_DB_";

	// running
	public long getPerCardTransactions(String payId, String cardMask, String fromDate, String toDate) {
		long noOfTransactions = 0;

		try {

			BasicDBObject dateQuery = new BasicDBObject();

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

			MongoDatabase dbIns = mongoInstance.getDB();
			MongoCollection<Document> collection = dbIns.getCollection(
					PropertiesManager.propertiesMap.get(prefix + Constants.TRANSACTION_STATUS_COLLECTION.getValue()));
			List<BasicDBObject> obj = new ArrayList<BasicDBObject>();
			obj.add(new BasicDBObject(FieldType.PAY_ID.getName(), payId));
			obj.add(new BasicDBObject(FieldType.CARD_MASK.getName(), cardMask));
			BasicDBObject query1 = new BasicDBObject("$and", obj);

			List<BasicDBObject> saleConditionList = new ArrayList<BasicDBObject>();
			saleConditionList.add(new BasicDBObject(FieldType.ORIG_TXNTYPE.getName(), TransactionType.SALE.getName()));
			saleConditionList.add(new BasicDBObject(FieldType.ALIAS_STATUS.getName(), StatusType.CAPTURED.getName()));

			BasicDBObject saleConditionQuery = new BasicDBObject("$and", saleConditionList);
			List<BasicDBObject> salePendingConditionList = new ArrayList<BasicDBObject>();
			salePendingConditionList
					.add(new BasicDBObject(FieldType.ORIG_TXNTYPE.getName(), TransactionType.SALE.getName()));
			salePendingConditionList
					.add(new BasicDBObject(FieldType.ALIAS_STATUS.getName(), StatusType.PENDING.getName()));

			BasicDBObject salePendingConditionQuery = new BasicDBObject("$and", salePendingConditionList);

			List<BasicDBObject> bothConditionList = new ArrayList<BasicDBObject>();
			bothConditionList.add(saleConditionQuery);
			bothConditionList.add(salePendingConditionQuery);
			BasicDBObject addConditionListQuery = new BasicDBObject("$or", bothConditionList);
			List<BasicDBObject> obj3 = new ArrayList<BasicDBObject>();
			obj3.add(query1);
			obj3.add(addConditionListQuery);

			if (!dateIndexConditionQuery.isEmpty()) {
				obj3.add(dateIndexConditionQuery);
			}
			BasicDBObject finalquery = new BasicDBObject("$and", obj3);
			noOfTransactions = collection.countDocuments(finalquery);

		} catch (MongoException exception) {
			logger.error("Database Error while fetching getPerCardAllowedTransactions :" + exception);
		} catch (ParseException e) {
			logger.error("PARSE EXCEPTION :", e);
		}

		return noOfTransactions;

	}

	// running
	public int getPerMerchantTransactions(String payId, String startTimeStamp, String endTimeStamp) {
		int noOfTransactions = 0;
		try {
			MongoDatabase dbIns = mongoInstance.getDB();
			MongoCollection<Document> collection = dbIns.getCollection(
					PropertiesManager.propertiesMap.get(prefix + Constants.TRANSACTION_STATUS_COLLECTION.getValue()));
			BasicDBObject dateQuery = new BasicDBObject();
			BasicDBObject query = new BasicDBObject();
			query.put(FieldType.PAY_ID.getName(), payId);
			List<BasicDBObject> obj2 = new ArrayList<BasicDBObject>();

			obj2.add(new BasicDBObject(FieldType.TXNTYPE.getName(), "sale"));
			obj2.add(new BasicDBObject(FieldType.TXNTYPE.getName(), "AUTHORISE"));
			BasicDBObject query2 = new BasicDBObject("$or", obj2);
			if (startTimeStamp != null) {
				dateQuery.put(FieldType.CREATE_DATE.getName(),
						BasicDBObjectBuilder.start("$gte", startTimeStamp).add("$lte", endTimeStamp).get());

				List<BasicDBObject> obj3 = new ArrayList<BasicDBObject>();
				obj3.add(query);
				obj3.add(query2);
				obj3.add(dateQuery);

				BasicDBObject finalquery = new BasicDBObject("$and", obj3);
				MongoCursor<Document> cursor = collection.find(finalquery).iterator();
				while (cursor.hasNext()) {
					noOfTransactions++;
				}
			}
		} catch (MongoException exception) {
			logger.error("Database Error while fetching getPerCardAllowedTransactions :" + exception);
		}
		return noOfTransactions;
	}

	// running but ipAddress convert in string problem
	public long getSpecificIPandIntervalTransactions(String ipAddress, String payId, String startDate, String endDate) {
		long noOfTxn = 0;
		try {
			MongoDatabase dbIns = mongoInstance.getDB();
			MongoCollection<Document> collection = dbIns.getCollection(
					PropertiesManager.propertiesMap.get(prefix + Constants.TRANSACTION_STATUS_COLLECTION.getValue()));
			BasicDBObject dateQuery = new BasicDBObject();
			BasicDBObject query = new BasicDBObject();
			query.put(FieldType.PAY_ID.getName(), payId);
			BasicDBObject ipAddressquery = new BasicDBObject();
			ipAddressquery.put(FieldType.INTERNAL_CUST_IP.getName(), ipAddressquery);
			List<BasicDBObject> obj2 = new ArrayList<BasicDBObject>();

			obj2.add(new BasicDBObject(FieldType.TXNTYPE.getName(), "sale"));
			obj2.add(new BasicDBObject(FieldType.TXNTYPE.getName(), "AUTHORISE"));
			BasicDBObject query2 = new BasicDBObject("$or", obj2);

			dateQuery.put(FieldType.CREATE_DATE.getName(),
					BasicDBObjectBuilder.start("$gte", startDate).add("$lte", endDate).get());

			List<BasicDBObject> obj3 = new ArrayList<BasicDBObject>();
			obj3.add(query);
			obj3.add(query2);
			obj3.add(dateQuery);

			BasicDBObject finalquery1 = new BasicDBObject("$or", obj3);

			List<BasicDBObject> resultQuery = new ArrayList<BasicDBObject>();
			resultQuery.add(finalquery1);

			BasicDBObject finalquery = new BasicDBObject("$and", resultQuery);
			MongoCursor<Document> cursor = collection.find(finalquery).iterator();
			while (cursor.hasNext()) {
				Document doc = cursor.next();
				logger.info("doc ==========={}", doc);
			}
		} catch (MongoException exception) {
			logger.error("Database Error while fetching getPerCardAllowedTransactions :" + exception);
		}
		return noOfTxn;
	}

	public Double getTotalTxnAmount(TxnFilterRequest request) {

		logger.info("Inside Total Txn Amount Class");
		Double totalAmount = 0.00;
		try {
			totalAmount = getTxnByFilter(request).stream()
					.collect(Collectors.summingDouble(doc -> doc.getBaseAmount()));

		} catch (Exception e) {
			logger.error("Exception " + e);
		}
		logger.info("Cursor closed for total Amount query , total Amount is " + totalAmount);
		return totalAmount;
	}

	public Map<String, Object> getTxnAmountAndCountByFilter(TxnFilterRequest request) {
		Map<String, Object> txnByFilter = new HashMap<>();
		txnByFilter.put("Amount", 0d);
		txnByFilter.put("Count", 0);
		try {
			List<FraudTxnExcelModel> docs = getTxnByFilter(request);
			if (docs.size() == 0) {
				return txnByFilter;
			}
			double totalAmt = docs.stream().filter(doc -> StringUtils.equalsIgnoreCase(doc.getStatus(), "Captured"))
					.collect(Collectors.summingDouble(doc -> doc.getBaseAmount()));
			txnByFilter.put("Amount", totalAmt);
			txnByFilter.put("Count", docs.size());
		} catch (Exception e) {
			logger.error("Exception " + e);
		}
		return txnByFilter;
	}

	public List<Double> getAmountByMerchant(String payId) {
		List<BasicDBObject> paramConditionLst = new ArrayList<BasicDBObject>();
		BasicDBObject allParamQuery = new BasicDBObject();

		if (!payId.equalsIgnoreCase("ALL")) {
			paramConditionLst.add(new BasicDBObject(FieldType.PAY_ID.getName(), payId));
		}

		List<BasicDBObject> saleConditionList = new ArrayList<BasicDBObject>();
		saleConditionList.add(new BasicDBObject(FieldType.TXNTYPE.getName(), TransactionType.SALE.getName()));
		saleConditionList.add(new BasicDBObject(FieldType.STATUS.getName(), StatusType.CAPTURED.getName()));
		
		BasicDBObject saleConditionQuery = new BasicDBObject("$and", saleConditionList);
		List<BasicDBObject> authConditionList = new ArrayList<BasicDBObject>();
		authConditionList.add(new BasicDBObject(FieldType.TXNTYPE.getName(), TransactionType.AUTHORISE.getName()));
		authConditionList.add(new BasicDBObject(FieldType.STATUS.getName(), StatusType.CAPTURED.getName()));
		
		BasicDBObject authConditionQuery = new BasicDBObject("$and", authConditionList);

		List<BasicDBObject> bothConditionList = new ArrayList<BasicDBObject>();
		bothConditionList.add(saleConditionQuery);
		bothConditionList.add(authConditionQuery);
		BasicDBObject addConditionListQuery = new BasicDBObject("$or", bothConditionList);

		if (!paramConditionLst.isEmpty()) {
			allParamQuery = new BasicDBObject("$and", paramConditionLst);
		}

		List<BasicDBObject> fianlList = new ArrayList<BasicDBObject>();
		if (!allParamQuery.isEmpty()) {
			fianlList.add(allParamQuery);
		}
		if (!addConditionListQuery.isEmpty()) {
			fianlList.add(addConditionListQuery);
		}

		BasicDBObject finalquery = new BasicDBObject("$and", fianlList);
		MongoDatabase dbIns = mongoInstance.getDB();
		MongoCollection<Document> coll = dbIns.getCollection(
				PropertiesManager.propertiesMap.get(prefix + Constants.TRANSACTION_STATUS_COLLECTION.getValue()));

		logger.info("total Amount query , finalquery = " + finalquery);

		BasicDBObject match = new BasicDBObject("$match", finalquery);

		List<BasicDBObject> pipeline = Arrays.asList(match);
		AggregateIterable<Document> output = coll.aggregate(pipeline);
		output.allowDiskUse(true);
		MongoCursor<Document> cursor = output.iterator();
		List<Double> amounts = new ArrayList<>();
		while (cursor.hasNext()) {
			Document doc = cursor.next();
			if (StringUtils.isNotBlank(doc.getString("AMOUNT"))) {
				amounts.add(Double.valueOf(doc.getString("AMOUNT")));
			}
		}
		return amounts;
	}

	public List<FraudTxnExcelModel> getTxnByFilter(TxnFilterRequest request) throws ParseException {
		BasicDBObject dateQuery = new BasicDBObject();
		List<BasicDBObject> paramConditionLst = new ArrayList<BasicDBObject>();
		BasicDBObject allParamQuery = new BasicDBObject();

		String currentDate = null;
		if (!request.getFromDate().isEmpty()) {

			if (!request.getToDate().isEmpty()) {
				currentDate = request.getToDate();
			} else {
				DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				Calendar cal = Calendar.getInstance();
				currentDate = dateFormat.format(cal.getTime());
			}

			dateQuery.put(FieldType.CREATE_DATE.getName(),
					BasicDBObjectBuilder.start("$gte", new SimpleDateFormat(request.getFromDate()).toLocalizedPattern())
							.add("$lte", new SimpleDateFormat(currentDate).toLocalizedPattern()).get());
		}

		List<BasicDBObject> dateIndexConditionList = new ArrayList<BasicDBObject>();
		BasicDBObject dateIndexConditionQuery = new BasicDBObject();
		String startString = new SimpleDateFormat(request.getFromDate()).toLocalizedPattern();
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

		if (!request.getPayId().equalsIgnoreCase("ALL")) {
			paramConditionLst.add(new BasicDBObject(FieldType.PAY_ID.getName(), request.getPayId()));
		}

		if (StringUtils.isNotBlank(request.getCurrency()) && !request.getCurrency().equalsIgnoreCase("ALL")) {
			paramConditionLst.add(new BasicDBObject(FieldType.CURRENCY_CODE.getName(), request.getCurrency()));
		}

		if (StringUtils.isNotBlank(request.getIpAddress())) {
			paramConditionLst.add(new BasicDBObject(FieldType.INTERNAL_CUST_IP.getName(), request.getIpAddress()));
		}
		if (StringUtils.isNotBlank(request.getEmailId())) {
			paramConditionLst.add(new BasicDBObject(FieldType.CUST_EMAIL.getName(), request.getEmailId()));
		}
		if (StringUtils.isNotBlank(request.getTxnStatus())
				&& !StringUtils.equalsIgnoreCase(request.getTxnStatus(), "ALL")) {
			paramConditionLst.add(new BasicDBObject(FieldType.STATUS.getName(), request.getTxnStatus()));
		}
		if (StringUtils.isNotBlank(request.getVpa())) {
			paramConditionLst.add(new BasicDBObject(FieldType.CARD_MASK.getName(), request.getVpa()));
		}
		if (StringUtils.isNotBlank(request.getMop())) {
			paramConditionLst.add(new BasicDBObject(FieldType.MOP_TYPE.getName(), request.getMop()));
		}

		List<BasicDBObject> saleConditionList = new ArrayList<BasicDBObject>();
		saleConditionList.add(new BasicDBObject(FieldType.TXNTYPE.getName(), TransactionType.SALE.getName()));

		BasicDBObject saleConditionQuery = new BasicDBObject("$and", saleConditionList);
		List<BasicDBObject> authConditionList = new ArrayList<BasicDBObject>();
		authConditionList.add(new BasicDBObject(FieldType.TXNTYPE.getName(), TransactionType.AUTHORISE.getName()));

		BasicDBObject authConditionQuery = new BasicDBObject("$and", authConditionList);

		List<BasicDBObject> bothConditionList = new ArrayList<BasicDBObject>();
		bothConditionList.add(saleConditionQuery);
		bothConditionList.add(authConditionQuery);
		BasicDBObject addConditionListQuery = new BasicDBObject("$or", bothConditionList);

		if (!paramConditionLst.isEmpty()) {
			allParamQuery = new BasicDBObject("$and", paramConditionLst);
		}

		List<BasicDBObject> fianlList = new ArrayList<BasicDBObject>();

		if (!dateQuery.isEmpty()) {
			fianlList.add(dateQuery);
		}
		if (!dateIndexConditionQuery.isEmpty()) {
			fianlList.add(dateIndexConditionQuery);
		}

		if (!allParamQuery.isEmpty()) {
			fianlList.add(allParamQuery);
		}
		if (!addConditionListQuery.isEmpty()) {
			fianlList.add(addConditionListQuery);
		}

		BasicDBObject finalquery = new BasicDBObject("$and", fianlList);

		MongoDatabase dbIns = mongoInstance.getDB();
		MongoCollection<Document> coll = dbIns.getCollection(
				PropertiesManager.propertiesMap.get(prefix + Constants.TRANSACTION_STATUS_COLLECTION.getValue()));

		logger.info("total Amount query , finalquery = " + finalquery);

		BasicDBObject match = new BasicDBObject("$match", finalquery);

		List<BasicDBObject> pipeline = Arrays.asList(match);
		AggregateIterable<Document> output = coll.aggregate(pipeline);
		output.allowDiskUse(true);
		MongoCursor<Document> cursor = output.iterator();
		List<FraudTxnExcelModel> docs = new ArrayList<>();
		while (cursor.hasNext()) {
			Document doc = cursor.next();
			String internalField = doc.getString(FieldType.INTERNAL_REQUEST_FIELDS.getName());
			if (StringUtils.isNotBlank(internalField)) {
				String phoneNo = StringUtils.split(Arrays
						.asList(StringUtils.split(doc.getString(FieldType.INTERNAL_REQUEST_FIELDS.getName()), "~"))
						.stream().filter(internalFields -> StringUtils.containsIgnoreCase(internalFields, "CUST_PHONE"))
						.collect(Collectors.joining()), "=")[1];
				doc.put("CUST_PHONE", phoneNo);
				if (StringUtils.isNotBlank(request.getMobileNo())
						&& !StringUtils.equals(request.getMobileNo(), phoneNo)) {
					continue;
				}
			}

			// Amount base filter.
			if (request.getAmount() > 0 && !Double.valueOf(doc.getString("AMOUNT")).equals(request.getAmount())) {
				continue;
			}
			FraudTxnExcelModel model = docToEntity(doc);
			docs.add(model);
		}
		logger.info("getTxnByFilter:: specified input doc size in db={}", docs.size());
		cursor.close();
		return docs;
	}

	private FraudTxnExcelModel docToEntity(Document document) {
		FraudTxnExcelModel transaction = new FraudTxnExcelModel();
		transaction.setTxnId(document.getString(FieldType.TXN_ID.getName()));
		transaction.setPgRefNo(document.getString(FieldType.PG_REF_NUM.getName()));
		transaction.setDate(document.getString("CREATE_DATE"));
		transaction.setOrderId(document.getString("ORDER_ID"));
		transaction.setMerchantName(document.getString("PAY_ID"));
		transaction.setRefundOrderId(document.getString("REFUND_ORDER_ID"));
		String mopType = StringUtils.isNotBlank(document.getString("MOP_TYPE"))
				? MopType.getmopName(document.getString("MOP_TYPE"))
				: "NA";
		transaction.setMopType(mopType);
		String paymentType = StringUtils.isNotBlank(document.getString("PAYMENT_TYPE"))
				? PaymentType.getpaymentName(document.getString("PAYMENT_TYPE"))
				: "NA";
		transaction.setPaymentType(paymentType);
		transaction.setTxnType(document.getString("TXNTYPE"));
		transaction.setStatus(document.getString("STATUS"));
		transaction.setBaseAmount(Double.valueOf(document.getString("AMOUNT")));
		transaction.setCustomerEmail(document.getString("CUST_EMAIL"));
		transaction.setCustomerPhone(document.getString("CUST_PHONE"));
		transaction.setIpAddress(document.getString("INTERNAL_CUST_IP"));
		transaction.setCardMask(document.getString("CARD_MASK"));
		return transaction;
	}

	public long getTotalTxnCount(String payId, String fromDate, String toDate, String currency, String ipAddress,
			String emailId, String mobileNo, double amount) {

		logger.info("Inside getTotalTxnCount");
		long totalTxn = 0;
		try {

			BasicDBObject dateQuery = new BasicDBObject();
			List<BasicDBObject> paramConditionLst = new ArrayList<BasicDBObject>();
			BasicDBObject allParamQuery = new BasicDBObject();

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

			if (!payId.equalsIgnoreCase("ALL")) {
				paramConditionLst.add(new BasicDBObject(FieldType.PAY_ID.getName(), payId));
			}

			if (!currency.equalsIgnoreCase("ALL")) {
				paramConditionLst.add(new BasicDBObject(FieldType.CURRENCY_CODE.getName(), currency));
			}

			if (StringUtils.isNotBlank(ipAddress)) {
				paramConditionLst.add(new BasicDBObject(FieldType.INTERNAL_CUST_IP.getName(), ipAddress));
			}
			if (StringUtils.isNotBlank(emailId)) {
				paramConditionLst.add(new BasicDBObject(FieldType.CUST_EMAIL.getName(), emailId));
			}
			if (StringUtils.isNotBlank(mobileNo)) {
				paramConditionLst.add(new BasicDBObject(FieldType.CUST_PHONE.getName(), mobileNo));
			}
			if (amount > 0) {
				DecimalFormat df = new DecimalFormat("#.00");
				paramConditionLst.add(new BasicDBObject(FieldType.AMOUNT.getName(), df.format(amount)));
			}

			List<BasicDBObject> saleConditionList = new ArrayList<BasicDBObject>();
			saleConditionList.add(new BasicDBObject(FieldType.TXNTYPE.getName(), TransactionType.SALE.getName()));
			saleConditionList.add(new BasicDBObject(FieldType.STATUS.getName(), StatusType.CAPTURED.getName()));

			BasicDBObject saleConditionQuery = new BasicDBObject("$and", saleConditionList);
			List<BasicDBObject> authConditionList = new ArrayList<BasicDBObject>();
			authConditionList.add(new BasicDBObject(FieldType.TXNTYPE.getName(), TransactionType.AUTHORISE.getName()));
			authConditionList.add(new BasicDBObject(FieldType.STATUS.getName(), StatusType.CAPTURED.getName()));

			BasicDBObject authConditionQuery = new BasicDBObject("$and", authConditionList);

			List<BasicDBObject> bothConditionList = new ArrayList<BasicDBObject>();
			bothConditionList.add(saleConditionQuery);
			bothConditionList.add(authConditionQuery);
			BasicDBObject addConditionListQuery = new BasicDBObject("$or", bothConditionList);

			if (!paramConditionLst.isEmpty()) {
				allParamQuery = new BasicDBObject("$and", paramConditionLst);
			}

			List<BasicDBObject> fianlList = new ArrayList<BasicDBObject>();

			if (!dateQuery.isEmpty()) {
				fianlList.add(dateQuery);
			}
			if (!dateIndexConditionQuery.isEmpty()) {
				fianlList.add(dateIndexConditionQuery);
			}

			if (!allParamQuery.isEmpty()) {
				fianlList.add(allParamQuery);
			}
			if (!addConditionListQuery.isEmpty()) {
				fianlList.add(addConditionListQuery);
			}

			BasicDBObject finalquery = new BasicDBObject("$and", fianlList);

			MongoDatabase dbIns = mongoInstance.getDB();
			MongoCollection<Document> coll = dbIns.getCollection(
					PropertiesManager.propertiesMap.get(prefix + Constants.TRANSACTION_STATUS_COLLECTION.getValue()));

			BasicDBObject match = new BasicDBObject("$match", finalquery);

			List<BasicDBObject> pipeline = Arrays.asList(match);
			AggregateIterable<Document> output = coll.aggregate(pipeline);
			output.allowDiskUse(true);
			MongoCursor<Document> cursor = output.iterator();
			List<Document> docs = new ArrayList<>();
			while (cursor.hasNext()) {
				Document doc = cursor.next();
				String internalField = doc.getString(FieldType.INTERNAL_REQUEST_FIELDS.getName());
				if (StringUtils.isNotBlank(internalField)) {
					String phoneNo = StringUtils.split(Arrays
							.asList(StringUtils.split(doc.getString(FieldType.INTERNAL_REQUEST_FIELDS.getName()), "~"))
							.stream()
							.filter(internalFields -> StringUtils.containsIgnoreCase(internalFields, "CUST_PHONE"))
							.collect(Collectors.joining()), "=")[1];
					doc.put("CUST_PHONE", phoneNo);
					if (StringUtils.isNotBlank(mobileNo) && !StringUtils.equals(mobileNo, phoneNo)) {
						continue;
					}
				}
				docs.add(doc);
			}
			logger.info("total Count query , finalquery = " + finalquery);
			totalTxn = docs.size();

		} catch (Exception e) {
			logger.error("Exception " + e);
		}
		logger.info("Cursor closed for total Txn Count query , total txn is {}", totalTxn);
		return totalTxn;
	}

	public Double getPercentageByMopType(String payId, String paymentType, String fromDate, String toDate) {
		logger.info("Inside getPercentageByMopType");
		double percentage = 0;
		try {

			List<Document> docs = getTxnByPayIdAndDate(payId, fromDate, toDate);
			long total = docs.size();
			if (total == 0) {
				return percentage;
			}
			Map<String, Long> result = docs.stream()
					.collect(Collectors.groupingBy(doc -> doc.getString("PAYMENT_TYPE"), Collectors.counting()));
			long paymentTypeTxn = result.get(paymentType);
			percentage = paymentTypeTxn * 100 / total;
		} catch (Exception e) {
			logger.error("Exception " + e);
		}
		logger.info("Cursor closed for total Txn percentage by payment type query , total txn percentage is {}",
				percentage);
		return percentage;
	}

	private List<Document> getTxnByPayIdAndDate(String payId, String fromDate, String toDate) throws ParseException {
		BasicDBObject dateQuery = new BasicDBObject();
		List<BasicDBObject> paramConditionLst = new ArrayList<BasicDBObject>();
		BasicDBObject allParamQuery = new BasicDBObject();

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

		if (!payId.equalsIgnoreCase("ALL")) {
			paramConditionLst.add(new BasicDBObject(FieldType.PAY_ID.getName(), payId));
		}

		List<BasicDBObject> saleConditionList = new ArrayList<BasicDBObject>();
		saleConditionList.add(new BasicDBObject(FieldType.TXNTYPE.getName(), TransactionType.SALE.getName()));
		saleConditionList.add(new BasicDBObject(FieldType.STATUS.getName(), StatusType.CAPTURED.getName()));

		BasicDBObject saleConditionQuery = new BasicDBObject("$and", saleConditionList);
		List<BasicDBObject> authConditionList = new ArrayList<BasicDBObject>();
		authConditionList.add(new BasicDBObject(FieldType.TXNTYPE.getName(), TransactionType.AUTHORISE.getName()));
		authConditionList.add(new BasicDBObject(FieldType.STATUS.getName(), StatusType.CAPTURED.getName()));

		BasicDBObject authConditionQuery = new BasicDBObject("$and", authConditionList);

		List<BasicDBObject> bothConditionList = new ArrayList<BasicDBObject>();
		bothConditionList.add(saleConditionQuery);
		bothConditionList.add(authConditionQuery);
		BasicDBObject addConditionListQuery = new BasicDBObject("$or", bothConditionList);

		if (!paramConditionLst.isEmpty()) {
			allParamQuery = new BasicDBObject("$and", paramConditionLst);
		}

		List<BasicDBObject> fianlList = new ArrayList<BasicDBObject>();

		if (!dateQuery.isEmpty()) {
			fianlList.add(dateQuery);
		}
		if (!dateIndexConditionQuery.isEmpty()) {
			fianlList.add(dateIndexConditionQuery);
		}

		if (!allParamQuery.isEmpty()) {
			fianlList.add(allParamQuery);
		}
		if (!addConditionListQuery.isEmpty()) {
			fianlList.add(addConditionListQuery);
		}

		BasicDBObject finalquery = new BasicDBObject("$and", fianlList);

		MongoDatabase dbIns = mongoInstance.getDB();
		MongoCollection<Document> coll = dbIns.getCollection(
				PropertiesManager.propertiesMap.get(prefix + Constants.TRANSACTION_STATUS_COLLECTION.getValue()));

		logger.info("getPercentageByMopType query , finalquery = " + finalquery);

		BasicDBObject match = new BasicDBObject("$match", finalquery);

		List<BasicDBObject> pipeline = Arrays.asList(match);
		AggregateIterable<Document> output = coll.aggregate(pipeline);
		output.allowDiskUse(true);
		MongoCursor<Document> cursor = output.iterator();
		List<Document> docs = new ArrayList<>();
		while (cursor.hasNext()) {
			docs.add(cursor.next());
		}
		cursor.close();
		return docs;
	}

	public double getCardTxnAmount(String payId, String fromDate, String toDate) {
		double amount = 0;
		try {
			List<Document> docs = getTxnByPayIdAndDate(payId, fromDate, toDate);
			long total = docs.size();
			if (total == 0) {
				return amount;
			}
			Map<String, Double> result = docs.stream()
					.collect(Collectors.groupingBy(doc -> doc.getString("PAYMENT_TYPE"),
							Collectors.summingDouble(doc -> Double.valueOf(doc.getString("AMOUNT")))));
			double cc = result.getOrDefault("CC", 0d);
			double dc = result.getOrDefault("DC", 0d);
			amount = cc + dc;
		} catch (Exception e) {
			logger.error("Exception " + e);
		}
		return amount;
	}

	public Map<String, Object> getCardTxnAmountAndCount(String payId, String fromDate, String toDate) {
		Map<String, Object> txnByCard = new HashMap<>();
		txnByCard.put("Amount", 0d);
		txnByCard.put("Count", 0l);
		try {
			List<Document> docs = getTxnByPayIdAndDate(payId, fromDate, toDate);
			if (docs.size() == 0) {
				return txnByCard;
			}
			Map<String, Double> result = docs.stream()
					.collect(Collectors.groupingBy(doc -> doc.getString("PAYMENT_TYPE"),
							Collectors.summingDouble(doc -> Double.valueOf(doc.getString("AMOUNT")))));
			double cc = result.getOrDefault("CC", 0d);
			double dc = result.getOrDefault("DC", 0d);
			txnByCard.put("Amount", cc + dc);

			Map<String, Long> resultCount = docs.stream()
					.collect(Collectors.groupingBy(doc -> doc.getString("PAYMENT_TYPE"), Collectors.counting()));
			txnByCard.put("Count", resultCount.get("CC") + resultCount.get("DC"));

		} catch (Exception e) {
			logger.error("Exception " + e);
		}
		return txnByCard;
	}
}
