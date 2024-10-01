package com.pay10.commons.dao;

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
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.bson.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.mongodb.BasicDBObject;
import com.mongodb.BasicDBObjectBuilder;
import com.mongodb.client.AggregateIterable;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import com.pay10.commons.mongo.MongoInstance;
import com.pay10.commons.user.NodalTransactions;
import com.pay10.commons.user.TransactionSearch;
import com.pay10.commons.user.User;
import com.pay10.commons.user.UserDao;
import com.pay10.commons.util.BeneficiaryTypes;
import com.pay10.commons.util.Constants;
import com.pay10.commons.util.CrmFieldConstants;
import com.pay10.commons.util.CrmFieldType;
import com.pay10.commons.util.FieldType;
import com.pay10.commons.util.Fields;
import com.pay10.commons.util.MopType;
import com.pay10.commons.util.PaymentType;
import com.pay10.commons.util.PropertiesManager;
import com.pay10.commons.util.SettlementTransactionType;
import com.pay10.commons.util.StatusType;
import com.pay10.commons.util.TransactionType;

@Component
public class NodalTxnReports {

	private static Logger logger = LoggerFactory.getLogger(NodalTxnReports.class.getName());
	private static final String alphabaticFileName = "alphabatic-currencycode.properties";
	private static final String prefix = "MONGO_DB_";

	@Autowired
	private UserDao userdao;

	@Autowired
	PropertiesManager propertiesManager;

	@Autowired
	private FieldsDao fieldsDao;

	@Autowired
	private MongoInstance mongoInstance;

	public List<NodalTransactions> searchPayment(String payId, String txnId, String orderId, String status, String paymentType,
			String fromDate, String toDate, int start, int length, String beneType) {

		Map<String, User> userMap = new HashMap<String, User>();

		logger.info("Inside NodalTxnReports , searchPayment");

		boolean isParameterised = false;
		try {
			List<NodalTransactions> transactionList = new ArrayList<NodalTransactions>();

			PropertiesManager propManager = new PropertiesManager();
			List<BasicDBObject> paramConditionLst = new ArrayList<BasicDBObject>();
			BasicDBObject dateQuery = new BasicDBObject();
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

			boolean skipDateAndOtherFields = false;
			if (!txnId.equalsIgnoreCase("ALL")) {
				paramConditionLst.add(new BasicDBObject(FieldType.TXN_ID.getName(), txnId));
				isParameterised = true;
				skipDateAndOtherFields = true;
			}
			if (!orderId.equalsIgnoreCase("ALL")) {
				isParameterised = true;
				paramConditionLst.add(new BasicDBObject(FieldType.ORDER_ID.getName(), orderId));
				skipDateAndOtherFields = true;
			}
			if (!status.equalsIgnoreCase("ALL") && !skipDateAndOtherFields) {
				isParameterised = true;
				paramConditionLst.add(new BasicDBObject(FieldType.STATUS.getName(), status));
			}
			if (!paymentType.equalsIgnoreCase("ALL") && !skipDateAndOtherFields) {
				isParameterised = true;
				paramConditionLst.add(new BasicDBObject(FieldType.PAYMENT_TYPE.getName(), paymentType));
			}
			
			if (StringUtils.isNotBlank(payId) && !payId.equalsIgnoreCase("ALL") && !skipDateAndOtherFields) {
				isParameterised = true;
				paramConditionLst.add(new BasicDBObject(FieldType.PAY_ID.getName(), payId));
			}
			
			if (StringUtils.isNotBlank(beneType) && !beneType.equalsIgnoreCase("ALL") ) {
				isParameterised = true;
				paramConditionLst.add(new BasicDBObject(FieldType.BENE_USER_TYPE.getName(), beneType));
			}

			if (!paramConditionLst.isEmpty()) {
				allParamQuery = new BasicDBObject("$and", paramConditionLst);
			}

			List<BasicDBObject> allConditionQueryList = new ArrayList<BasicDBObject>();

			if (!dateQuery.isEmpty() && !skipDateAndOtherFields) {
				allConditionQueryList.add(dateQuery);
			}
			if (!dateIndexConditionQuery.isEmpty() && !skipDateAndOtherFields) {
				allConditionQueryList.add(dateIndexConditionQuery);
			}

			BasicDBObject allConditionQueryObj = new BasicDBObject("$and", allConditionQueryList);
			List<BasicDBObject> fianlList = new ArrayList<BasicDBObject>();

			if (!allParamQuery.isEmpty()) {
				fianlList.add(allParamQuery);
			}
			if (!allConditionQueryObj.isEmpty() && !allConditionQueryList.isEmpty()) {
				fianlList.add(allConditionQueryObj);
			}

			BasicDBObject finalquery = new BasicDBObject("$and", fianlList);

			logger.info("Inside NodalTxnReports , searchPayment , finalquery = " + finalquery);
			MongoDatabase dbIns = mongoInstance.getDB();
			MongoCollection<Document> coll = dbIns.getCollection(
					PropertiesManager.propertiesMap.get(prefix + Constants.SETTLEMENT_COLLECTION_NAME.getValue()));
			BasicDBObject match = new BasicDBObject("$match", finalquery);

			Document firstGroup;
			if (isParameterised) {
				firstGroup = new Document("_id", new Document("_id", "$_id"));
			} else {
				firstGroup = new Document("_id", new Document("OID", "$OID").append("ORIG_TXNTYPE", "$ORIG_TXNTYPE"));
			}

//			BasicDBObject firstGroupObject = new BasicDBObject(firstGroup);
//			BasicDBObject secondGroup = new BasicDBObject("$push", "$$ROOT");
//			BasicDBObject group = new BasicDBObject("$group", firstGroupObject.append("entries", secondGroup));
			BasicDBObject sort = new BasicDBObject("$sort", new BasicDBObject("INSERTION_DATE", -1));
			BasicDBObject skip = new BasicDBObject("$skip", start);
			BasicDBObject limit = new BasicDBObject("$limit", length);

			List<BasicDBObject> pipeline = Arrays.asList(match, sort, skip, limit);
			logger.info("Pipe line : " + pipeline.toString());
			AggregateIterable<Document> output = coll.aggregate(pipeline);
			output.allowDiskUse(true);
			MongoCursor<Document> cursor = output.iterator();

			while (cursor.hasNext()) {
//				Document mydata = cursor.next();
//				List<Document> courses = (List<Document>) mydata.get("entries");
				Document dbobj = cursor.next();

				NodalTransactions transReport = new NodalTransactions();
				BigInteger txnID = new BigInteger(dbobj.getString(FieldType.TXN_ID.toString()));
				transReport.setTxnId((String.valueOf(txnID)));
				transReport.setOid(dbobj.getString(FieldType.OID.toString()));
				transReport.setOrderId(dbobj.getString(FieldType.ORDER_ID.toString()));
				transReport.setCreatedDate(dbobj.getString(FieldType.CREATE_DATE.toString()));
				transReport.setCustomerId(dbobj.getString(FieldType.CUSTOMER_ID.toString()));
//				transReport.setSrcAccNo(dbobj.getString(FieldType.SRC_ACCOUNT_NO.toString()));
				transReport.setAmount(dbobj.getString(FieldType.AMOUNT.toString()));
				transReport.setTxnType(SettlementTransactionType.getInstance(dbobj.getString(FieldType.TXNTYPE.toString())));
				transReport.setStatus(dbobj.getString(FieldType.STATUS.toString()));
//				transReport.setComments(dbobj.getString(FieldType.PRODUCT_DESC.toString()));
				transReport.setAcquirer(dbobj.getString(FieldType.ACQUIRER_TYPE.toString()));
				transReport.setPaymentType(dbobj.getString(FieldType.PAYMENT_TYPE.toString()));
				transReport.setBeneAccNo(dbobj.getString(FieldType.BENE_ACCOUNT_NO.toString()));
				transReport.setBeneficiaryName(dbobj.getString(FieldType.BENE_MERCHANT_PROVIDED_NAME.getName()));
				transReport.setBeneficiaryCode(dbobj.getString(FieldType.BENE_MERCHANT_PROVIDED_ID.getName()));
				transReport.setRrn(dbobj.getString(FieldType.RRN.toString()));
				transReport.setMerchantBusinessName(dbobj.getString(FieldType.BENE_MERCHANT_BUSINESS_NAME.getName()));
				try{
					if(StringUtils.isBlank(transReport.getMerchantBusinessName())) {
						String businessName = userdao.getBusinessNameByPayId(dbobj.getString(FieldType.PAY_ID.getName()));
						if(businessName == null) {
							transReport.setMerchantBusinessName("NA");
						}else {
							transReport.setMerchantBusinessName(businessName);
						}
					}
				}catch(Exception e) {
					logger.error("Merchant not found for Pay id : " + FieldType.PAY_ID.getName());
					transReport.setMerchantBusinessName("NA");
				}
				
				transactionList.add(transReport);

				// Collections.sort(transactionList, comp);
			}
			cursor.close();
			logger.info("Inside NodalTxnReports , searchPayment , transactionListSize = " + transactionList.size());
			return transactionList;
		}

		catch (Exception e) {
			logger.error("Exception occured in NodalTxnReports , searchPayment , Exception = " + e);
			e.printStackTrace();
			return null;
		}
	}

	public int searchPaymentCount(String payId, String txnId, String orderId, String status, String paymentType, String fromDate,
			String toDate, String beneType) {

		logger.info("Inside NodalTxnReports , searchPaymentCount");
		boolean isParameterised = false;
		try {
			int total = 0;
			List<BasicDBObject> paramConditionLst = new ArrayList<BasicDBObject>();

			BasicDBObject dateQuery = new BasicDBObject();
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
			
			 boolean skipDateAndOtherFields = false;

			if (!txnId.equalsIgnoreCase("ALL")) {
				isParameterised = true;
				paramConditionLst.add(new BasicDBObject(FieldType.TXN_ID.getName(), txnId));
				skipDateAndOtherFields = true;
			}
			if (!orderId.equalsIgnoreCase("ALL")) {
				isParameterised = true;
				paramConditionLst.add(new BasicDBObject(FieldType.ORDER_ID.getName(), orderId));
				skipDateAndOtherFields = true;
			}
			if (!paymentType.equalsIgnoreCase("ALL") && !skipDateAndOtherFields) {
				isParameterised = true;
				paramConditionLst.add(new BasicDBObject(FieldType.PAYMENT_TYPE.getName(), paymentType));
			}
			if (!status.equalsIgnoreCase("ALL") && !skipDateAndOtherFields) {
				isParameterised = true;
				paramConditionLst.add(new BasicDBObject(FieldType.STATUS.getName(), status));
			}
			
			if (StringUtils.isNotBlank(payId) && !payId.equalsIgnoreCase("ALL") && !skipDateAndOtherFields) {
				isParameterised = true;
				paramConditionLst.add(new BasicDBObject(FieldType.PAY_ID.getName(), payId));
			}
			
			if (StringUtils.isNotBlank(beneType) && !beneType.equalsIgnoreCase("ALL")) {
				isParameterised = true;
				paramConditionLst.add(new BasicDBObject(FieldType.BENE_USER_TYPE.getName(), beneType));
			}

			if (!paramConditionLst.isEmpty()) {
				allParamQuery = new BasicDBObject("$and", paramConditionLst);
			}

			List<BasicDBObject> allConditionQueryList = new ArrayList<BasicDBObject>();

			if (!dateQuery.isEmpty() && !skipDateAndOtherFields) {
				allConditionQueryList.add(dateQuery);
			}
			if (!dateIndexConditionQuery.isEmpty() && !skipDateAndOtherFields) {
				allConditionQueryList.add(dateIndexConditionQuery);
			}

			BasicDBObject allConditionQueryObj = new BasicDBObject("$and", allConditionQueryList);
			List<BasicDBObject> fianlList = new ArrayList<>();

			if (!allParamQuery.isEmpty()) {
				fianlList.add(allParamQuery);
			}
			if (!allConditionQueryObj.isEmpty() && !allConditionQueryList.isEmpty()) {
				fianlList.add(allConditionQueryObj);
			}

			logger.info("Inside NodalTxnReports , searchPaymentCount , finalList = " + fianlList);
			BasicDBObject finalquery = new BasicDBObject("$and", fianlList);
			MongoDatabase dbIns = mongoInstance.getDB();
			MongoCollection<Document> coll = dbIns.getCollection(
					PropertiesManager.propertiesMap.get(prefix + Constants.SETTLEMENT_COLLECTION_NAME.getValue()));
			BasicDBObject match = new BasicDBObject("$match", finalquery);
			// Now the aggregate operation

			Document firstGroup;
			if (isParameterised) {
				firstGroup = new Document("_id", new Document("_id", "$_id"));
			}

			else {
				firstGroup = new Document("_id", new Document("OID", "$OID").append("ORIG_TXNTYPE", "$ORIG_TXNTYPE"));
			}
//			BasicDBObject firstGroupObject = new BasicDBObject(firstGroup);
//			BasicDBObject secondGroup = new BasicDBObject("$push", "$$ROOT");
//			BasicDBObject groupObject = new BasicDBObject("$group", firstGroupObject.append("entries", secondGroup));
			BasicDBObject sort = new BasicDBObject("$sort", new BasicDBObject("INSERTION_DATE", -1));
			BasicDBObject count = new BasicDBObject("$count", "totalCount");
			List<BasicDBObject> pipeline;
			pipeline = Arrays.asList(match, sort, count);
			logger.info("Inside NodalTxnReports , searchPayment , pipeline : " + pipeline);
			Document output = coll.aggregate(pipeline).first();
			if(output != null) {
				total = (int) output.get("totalCount");
			}
			return total;

		} catch (Exception e) {
			logger.error("Exception occured in TxnReports , searchPaymentCount n exception = ", e);
			return 0;
		}
	}

	public List<TransactionSearch> searchPaymentForDownload(String merchantPayId, String paymentType, String status,
			String currency, String transactionType, String fromDate, String toDate, User user, String paymentsRegion,
			String acquirer) {
		logger.info("Inside TxnReports , searchPayment");
		Map<String, User> userMap = new HashMap<String, User>();
		boolean isParameterised = false;
		try {
			List<TransactionSearch> transactionList = new ArrayList<TransactionSearch>();

			PropertiesManager propManager = new PropertiesManager();
			List<BasicDBObject> paramConditionLst = new ArrayList<BasicDBObject>();
			List<BasicDBObject> acquirerConditionLst = new ArrayList<BasicDBObject>();
			BasicDBObject dateQuery = new BasicDBObject();
			BasicDBObject acquirerQuery = new BasicDBObject();
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

			if (!merchantPayId.equalsIgnoreCase("ALL")) {
				paramConditionLst.add(new BasicDBObject(FieldType.PAY_ID.getName(), merchantPayId));

				isParameterised = true;
			}
			if (!status.equalsIgnoreCase("ALL")) {
				isParameterised = true;
				paramConditionLst.add(new BasicDBObject(FieldType.STATUS.getName(), status));
			} else {

			}

			if (!currency.equalsIgnoreCase("ALL")) {
				isParameterised = true;
				paramConditionLst.add(new BasicDBObject(FieldType.CURRENCY_CODE.getName(), currency));
			} else {

			}

			if (!transactionType.equalsIgnoreCase("ALL")) {
				isParameterised = true;
				paramConditionLst.add(new BasicDBObject(FieldType.ORIG_TXNTYPE.getName(), transactionType));
			} else {

			}
			if (!paymentType.equalsIgnoreCase("ALL")) {
				isParameterised = true;
				paramConditionLst.add(new BasicDBObject(FieldType.PAYMENT_TYPE.getName(), paymentType));
			} else {

			}

			if (!paymentsRegion.equalsIgnoreCase("ALL")) {
				isParameterised = true;
				paramConditionLst.add(new BasicDBObject(FieldType.PAYMENTS_REGION.getName(), paymentsRegion));
			} else {

			}

			if (!paramConditionLst.isEmpty()) {
				allParamQuery = new BasicDBObject("$and", paramConditionLst);
			}

			if (!acquirer.equalsIgnoreCase("ALL")) {
				List<String> acquirerList = Arrays.asList(acquirer.split(","));
				for (String acq : acquirerList) {

					acquirerConditionLst.add(new BasicDBObject(FieldType.ACQUIRER_TYPE.getName(), acq));
				}
				acquirerQuery.append("$or", acquirerConditionLst);

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

			if (!acquirerQuery.isEmpty()) {
				fianlList.add(acquirerQuery);
			}

			BasicDBObject finalquery = new BasicDBObject("$and", fianlList);

			logger.info("Inside TxnReports , searchPayment , finalquery = " + finalquery);
			MongoDatabase dbIns = mongoInstance.getDB();
			MongoCollection<Document> coll = dbIns
					.getCollection(PropertiesManager.propertiesMap.get(prefix + Constants.COLLECTION_NAME.getValue()));
			BasicDBObject match = new BasicDBObject("$match", finalquery);
			// Now the aggregate operation ()In case any parameter is passed in search query
			// , then show all records

			Document firstGroup;
			if (isParameterised) {
				firstGroup = new Document("_id", new Document("_id", "$_id"));
			} else {
				firstGroup = new Document("_id", new Document("OID", "$OID").append("ORIG_TXNTYPE", "$ORIG_TXNTYPE"));
			}

			BasicDBObject firstGroupObject = new BasicDBObject(firstGroup);
			BasicDBObject secondGroup = new BasicDBObject("$push", "$$ROOT");
			BasicDBObject group = new BasicDBObject("$group", firstGroupObject.append("entries", secondGroup));
			BasicDBObject sort = new BasicDBObject("$sort", new BasicDBObject("INSERTION_DATE", -1));

			List<BasicDBObject> pipeline = Arrays.asList(match, sort, group);

			AggregateIterable<Document> output = coll.aggregate(pipeline);
			output.allowDiskUse(true);
			MongoCursor<Document> cursor = output.iterator();
			while (cursor.hasNext()) {
				Document mydata = cursor.next();
				List<Document> courses = (List<Document>) mydata.get("entries");
				Document dbobj = courses.get(0);

				TransactionSearch transReport = new TransactionSearch();
				BigInteger txnID = new BigInteger(dbobj.getString(FieldType.TXN_ID.toString()));
				transReport.setTransactionId((txnID));
				transReport.setTransactionIdString((String.valueOf(txnID)));
				transReport.setPgRefNum(dbobj.getString(FieldType.PG_REF_NUM.toString()));
				transReport.setPayId(dbobj.getString(FieldType.PAY_ID.toString()));
				transReport.setCustomerName(dbobj.getString(FieldType.CARD_HOLDER_NAME.toString()));
				transReport.setTransactionRegion(dbobj.getString(FieldType.PAYMENTS_REGION.toString()));
				transReport.setMerchants(dbobj.getString(CrmFieldType.BUSINESS_NAME.getName()));
				transReport.setPostSettledFlag(dbobj.getString(FieldType.POST_SETTLED_FLAG.getName()));
				if (dbobj.getString(FieldType.UDF6.getName()) != null) {
					transReport.setDeltaRefundFlag(dbobj.getString(FieldType.UDF6.getName()));
				} else {
					transReport.setDeltaRefundFlag("");
				}

				String payid = (String) dbobj.get(FieldType.PAY_ID.getName());

				User user1 = new User();

				if (userMap.get(payid) != null && !userMap.get(payid).getPayId().isEmpty()) {
					user1 = userMap.get(payid);
				} else {
					user1 = userdao.findPayId(payid);
					userMap.put(payid, user1);
				}

				transReport.setMerchants(user1.getBusinessName());
				if (null != dbobj.getString(FieldType.ORIG_TXNTYPE.toString())) {
					transReport.setTxnType(dbobj.getString(FieldType.ORIG_TXNTYPE.toString()));
				} else {

					// If ORIG_TXN_TYPE is not available incase of a timeout , set TXNTYPE instead
					// of ORIG_TXN_TYPE
					if (dbobj.getString(FieldType.STATUS.toString()).equalsIgnoreCase(StatusType.TIMEOUT.getName())) {
						transReport.setTxnType(dbobj.getString(FieldType.TXNTYPE.toString()));
					} else {
						transReport.setTxnType(CrmFieldConstants.NA.getValue());
					}

				}

				if (null != dbobj.getString(FieldType.MOP_TYPE.toString())) {
					transReport.setMopType(MopType.getmopName(dbobj.getString(FieldType.MOP_TYPE.toString())));
				} else {
					transReport.setMopType(CrmFieldConstants.NA.getValue());
				}

				if (null != dbobj.getString(FieldType.ACQUIRER_TYPE.toString())) {
					transReport.setAcquirerType(dbobj.getString(FieldType.ACQUIRER_TYPE.toString()));
				} else {
					transReport.setAcquirerType(CrmFieldConstants.NA.getValue());
				}

				if (null != dbobj.getString(FieldType.PAYMENT_TYPE.toString())) {
					transReport.setPaymentMethods(
							PaymentType.getpaymentName(dbobj.getString(FieldType.PAYMENT_TYPE.toString())));
				} else {
					transReport.setPaymentMethods(CrmFieldConstants.NA.getValue());
				}

				transReport.setStatus(dbobj.getString(FieldType.STATUS.toString()));
				transReport.setDateFrom(dbobj.getString(FieldType.CREATE_DATE.getName()));
				transReport.setAmount(dbobj.getString(FieldType.AMOUNT.toString()));
				transReport.setOrderId(dbobj.getString(FieldType.ORDER_ID.toString()));
				transReport.setoId(dbobj.getString(FieldType.OID.toString()));
				transReport.setTransactionCaptureDate(dbobj.getString(FieldType.PG_DATE_TIME.toString()));
				if (dbobj.getString(FieldType.TOTAL_AMOUNT.toString()) != null) {
					transReport.setTotalAmount(dbobj.getString(FieldType.TOTAL_AMOUNT.toString()));
				} else {
					transReport.setTotalAmount("");
				}

				if (dbobj.getString(FieldType.ACQ_ID.toString()) != null) {
					transReport.setAcqId(dbobj.getString(FieldType.ACQ_ID.toString()));
				} else {
					transReport.setAcqId(CrmFieldConstants.NA.getValue());
				}

				if (dbobj.getString(FieldType.RRN.toString()) != null) {
					transReport.setRrn(dbobj.getString(FieldType.RRN.toString()));
				} else {
					transReport.setRrn(CrmFieldConstants.NA.getValue());
				}

				if (null != dbobj.getString(FieldType.CURRENCY_CODE.toString())) {
					transReport.setCurrency(
							propManager.getAlphabaticCurrencyCode(dbobj.getString(FieldType.CURRENCY_CODE.toString())));
				} else {
					transReport.setCurrency(CrmFieldConstants.NA.getValue());
				}

				transactionList.add(transReport);

			}
			cursor.close();
			logger.info("Inside TxnReports , searchPayment , transactionListSize = " + transactionList.size());
			Comparator<TransactionSearch> comp = (TransactionSearch a, TransactionSearch b) -> {

				if (a.getDateFrom().compareTo(b.getDateFrom()) > 0) {
					return -1;
				} else if (a.getDateFrom().compareTo(b.getDateFrom()) < 0) {
					return 1;
				} else {
					return 0;
				}
			};
			Collections.sort(transactionList, comp);
			return transactionList;
		}

		catch (Exception e) {
			logger.error("Exception occured in TxnReports , searchPayment , Exception = " + e);
			return null;
		}
	}

	public List<TransactionSearch> searchRejectedRefund(String merchant, String orderId, String fromDate, String toDate,
			String refundType) {

		List<TransactionSearch> transactionList = new ArrayList<TransactionSearch>();
		try {

			BasicDBObject dateQuery = new BasicDBObject();
			List<BasicDBObject> paramConditionLst = new ArrayList<BasicDBObject>();
			BasicDBObject acquirerQuery = new BasicDBObject();

			BasicDBObject allParamQuery = new BasicDBObject();
			List<BasicDBObject> acquirerConditionLst = new ArrayList<BasicDBObject>();
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
				dateIndexConditionQuery.append("$or", dateIndexConditionList);
			}

			if (!merchant.equalsIgnoreCase("ALL")) {
				paramConditionLst.add(new BasicDBObject(FieldType.PAY_ID.getName(), merchant));
			}

			if (StringUtils.isNotBlank(orderId)) {
				paramConditionLst.add(new BasicDBObject(FieldType.ORDER_ID.getName(), orderId));
			}

			paramConditionLst.add(new BasicDBObject(FieldType.TXNTYPE.getName(), TransactionType.REFUND.getName()));
			ArrayList<String> list = new ArrayList<>();
			list.add(StatusType.CAPTURED.getName());
			list.add(StatusType.DECLINED.getName());
			list.add(StatusType.REJECTED.getName());
			paramConditionLst.add(new BasicDBObject(FieldType.STATUS.getName(), new BasicDBObject("$in", list)));
			if (refundType.equals("DELTAREFUND")) {
				paramConditionLst.add(new BasicDBObject(FieldType.UDF6.getName(), Constants.Y.name()));
			}

			BasicDBObject refundConditionQuery = new BasicDBObject("$and", paramConditionLst);

			if (!paramConditionLst.isEmpty()) {
				allParamQuery = new BasicDBObject("$and", paramConditionLst);
			}

			List<BasicDBObject> allConditionQueryList = new ArrayList<BasicDBObject>();

			if (!acquirerQuery.isEmpty()) {
				allConditionQueryList.add(acquirerQuery);
			}
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

			logger.info("Inside search summary report query , finalquery = " + finalquery);

			MongoDatabase dbIns = mongoInstance.getDB();
			MongoCollection<Document> coll = dbIns
					.getCollection(PropertiesManager.propertiesMap.get(prefix + Constants.COLLECTION_NAME.getValue()));

			BasicDBObject match = new BasicDBObject("$match", finalquery);
			// Now the aggregate operation
			Document firstGroup = new Document("_id", new Document("PG_REF_NUM", "$PG_REF_NUM"));

			BasicDBObject firstGroupObject = new BasicDBObject(firstGroup);
			BasicDBObject secondGroup = new BasicDBObject("$push", "$$ROOT");
			BasicDBObject group = new BasicDBObject("$group", firstGroupObject.append("entries", secondGroup));
			BasicDBObject sort = new BasicDBObject("$sort", new BasicDBObject("CREATE_DATE", -1));
			List<BasicDBObject> pipeline = Arrays.asList(match, sort, group);
			AggregateIterable<Document> output = coll.aggregate(pipeline);
			output.allowDiskUse(true);

			MongoCursor<Document> cursor = output.iterator();
			Boolean capturedFlag = false;
			while (cursor.hasNext()) {
				capturedFlag = false;
				Document dbobj = cursor.next();
				List<Document> lstDoc = (List<Document>) dbobj.get("entries");
				for (int i = 0; i < lstDoc.size(); i++) {
					if (lstDoc.get(i).getString(FieldType.TXNTYPE.getName()).equals(TransactionType.REFUND.getName())
							&& lstDoc.get(i).getString(FieldType.STATUS.getName())
									.equals(StatusType.CAPTURED.getName())) {
						capturedFlag = true;
						break;
					}
				}

				if (!capturedFlag) {
					Document doc = lstDoc.get(0);
					TransactionSearch transactionSearch = new TransactionSearch();

					List<Fields> fieldsList = new ArrayList<Fields>();
					fieldsList = fieldsDao
							.getPreviousSaleCapturedForOrderId(doc.getString(FieldType.ORDER_ID.toString()));

					transactionSearch.setOrderId(doc.getString(FieldType.ORDER_ID.toString()));
					transactionSearch.setPgRefNum(doc.getString(FieldType.PG_REF_NUM.toString()));
					transactionSearch.setRefundDate(doc.getString(FieldType.CREATE_DATE.toString()));
					transactionSearch.setRefundAmount(doc.getString(FieldType.AMOUNT.toString()));
					transactionSearch.setTotalAmount(fieldsList.get(0).get(FieldType.AMOUNT.getName()));
					transactionSearch.setRefundFlag(doc.getString(FieldType.REFUND_FLAG.toString()));
					transactionSearch.setDateFrom(fieldsList.get(0).get(FieldType.CREATE_DATE.getName()));
					transactionList.add(transactionSearch);
				}
			}

			cursor.close();
			Comparator<TransactionSearch> comp = (TransactionSearch a, TransactionSearch b) -> {

				if (a.getDateFrom().compareTo(b.getDateFrom()) > 0) {
					return -1;
				} else if (a.getDateFrom().compareTo(b.getDateFrom()) < 0) {
					return 1;
				} else {
					return 0;
				}
			};

			return transactionList;

		}

		catch (Exception e) {
			logger.error("Exception in getting records for refund reject report " + e);
		}

		return transactionList;
	}
	
	public NodalTransactions getTransactionByOrderIdAndStatus(String txnId) {
		NodalTransactions transReport = null;
		MongoDatabase dbIns = mongoInstance.getDB();
		MongoCollection<Document> coll = dbIns
				.getCollection(PropertiesManager.propertiesMap.get(prefix + Constants.SETTLEMENT_COLLECTION_NAME.getValue()));

		
		BasicDBObject query = new BasicDBObject(FieldType.TXN_ID.getName(), txnId);

		logger.info("getTransactionByOrderIdAndStatus() query : " + query);
		FindIterable<Document> output = coll.find(query);
		MongoCursor<Document> cursor = output.iterator();
		if(cursor.hasNext()) {
			Document dbobj = cursor.next();
			transReport = new NodalTransactions();
			BigInteger txnID = new BigInteger(dbobj.getString(FieldType.TXN_ID.toString()));
			transReport.setTxnId((String.valueOf(txnID)));
			transReport.setOid(dbobj.getString(FieldType.OID.toString()));
			transReport.setOrderId(dbobj.getString(FieldType.ORDER_ID.toString()));
			transReport.setCreatedDate(dbobj.getString(FieldType.CREATE_DATE.toString()));
			transReport.setCustomerId(dbobj.getString(FieldType.CUSTOMER_ID.toString()));
			transReport.setSrcAccNo(dbobj.getString(FieldType.SRC_ACCOUNT_NO.toString()));
			transReport.setAmount(dbobj.getString(FieldType.AMOUNT.toString()));
			transReport.setTxnType(SettlementTransactionType.getInstance(dbobj.getString(FieldType.TXNTYPE.toString())));
			transReport.setStatus(dbobj.getString(FieldType.STATUS.toString()));
			transReport.setComments(dbobj.getString(FieldType.PRODUCT_DESC.toString()));
			transReport.setAcquirer(dbobj.getString(FieldType.ACQUIRER_TYPE.toString()));
			transReport.setPaymentType(dbobj.getString(FieldType.PAYMENT_TYPE.toString()));
			transReport.setBeneAccNo(dbobj.getString(FieldType.BENE_ACCOUNT_NO.toString()));
			transReport.setBeneficiaryName(dbobj.getString(FieldType.BENE_NAME.toString()));
			transReport.setBeneficiaryCode(dbobj.getString(FieldType.BENEFICIARY_CD.toString()));
			transReport.setMobile(dbobj.getString(FieldType.CUST_PHONE.getName()));
			transReport.setEmail(dbobj.getString(FieldType.CUST_EMAIL.getName()));
//			transReport.setCreatedDate(dbobj.getString(FieldType.CREATE_DATE.getName()));
			transReport.setRrn(dbobj.getString(FieldType.RRN.getName()));
			transReport.setPayId(dbobj.getString(FieldType.PAY_ID.getName()));
			transReport.setBeneIfscCode(dbobj.getString(FieldType.BENE_IFSC.toString()));
			if(StringUtils.isNotBlank(dbobj.getString(FieldType.BENE_USER_TYPE.getName())) && BeneficiaryTypes.getInstancefromCode(dbobj.getString(FieldType.BENE_USER_TYPE.getName())) != null){
				transReport.setBeneType(BeneficiaryTypes.getInstancefromCode(dbobj.getString(FieldType.BENE_USER_TYPE.getName())));			
			} else{
				transReport.setBeneType(null);		
			}

			try{
				String businessName = userdao.getBusinessNameByPayId(dbobj.getString(FieldType.PAY_ID.getName()));
				if(businessName == null) {
					transReport.setMerchantBusinessName("NA");
				}else {
					transReport.setMerchantBusinessName(businessName);
				}
			}catch(Exception e) {
				logger.error("Merchant not found for Pay id : " + FieldType.PAY_ID.getName());
				transReport.setMerchantBusinessName("NA");
			}
		}
		
		return transReport;
	}
	
	public Document getTransactionByOrderId(String orderId) {
		MongoDatabase dbIns = mongoInstance.getDB();
		MongoCollection<Document> coll = dbIns
				.getCollection(PropertiesManager.propertiesMap.get(prefix + Constants.SETTLEMENT_COLLECTION_NAME.getValue()));

		
		BasicDBObject query = new BasicDBObject(FieldType.ORDER_ID.getName(), orderId);

		logger.info("getTransactionByOrderIdAndStatus() query : " + query);
		FindIterable<Document> output = coll.find(query);
		MongoCursor<Document> cursor = output.iterator();
		Document dbobj = null;
		if(cursor.hasNext()) {
			dbobj = cursor.next();
//			transReport = new NodalTransactions();
//			BigInteger txnID = new BigInteger(dbobj.getString(FieldType.TXN_ID.toString()));
//			transReport.setTxnId((String.valueOf(txnID)));
//			transReport.setOid(dbobj.getString(FieldType.OID.toString()));
//			transReport.setOrderId(dbobj.getString(FieldType.ORDER_ID.toString()));
//			transReport.setCreatedDate(dbobj.getString(FieldType.CREATE_DATE.toString()));
//			transReport.setCustomerId(dbobj.getString(FieldType.CUSTOMER_ID.toString()));
//			transReport.setSrcAccNo(dbobj.getString(FieldType.SRC_ACCOUNT_NO.toString()));
//			transReport.setAmount(dbobj.getString(FieldType.AMOUNT.toString()));
//			transReport.setTxnType(SettlementTransactionType.getInstance(dbobj.getString(FieldType.TXNTYPE.toString())));
//			transReport.setStatus(dbobj.getString(FieldType.STATUS.toString()));
//			transReport.setComments(dbobj.getString(FieldType.PRODUCT_DESC.toString()));
//			transReport.setAcquirer(dbobj.getString(FieldType.ACQUIRER_TYPE.toString()));
//			transReport.setPaymentType(dbobj.getString(FieldType.PAYMENT_TYPE.toString()));
//			transReport.setBeneAccNo(dbobj.getString(FieldType.BENE_ACCOUNT_NO.toString()));
//			transReport.setBeneficiaryName(dbobj.getString(FieldType.BENE_NAME.toString()));
//			transReport.setBeneficiaryCode(dbobj.getString(FieldType.BENEFICIARY_CD.toString()));
//			transReport.setBeneIfscCode(dbobj.getString(FieldType.IFSC_CODE.getName()));
//			transReport.setMobile(dbobj.getString(FieldType.CUST_PHONE.getName()));
//			transReport.setEmail(dbobj.getString(FieldType.CUST_EMAIL.getName()));
////			transReport.setCreatedDate(dbobj.getString(FieldType.CREATE_DATE.getName()));
//			transReport.setRrn(dbobj.getString(FieldType.RRN.getName()));
//			transReport.setCurrencyCode(Currency.getAlphabaticCode(dbobj.getString(FieldType.CURRENCY_CODE.getName())));
//			transReport.setPayId(dbobj.getString(FieldType.PAY_ID.getName()));
//			transReport.setBeneMerchantProvidedCode(dbobj.getString(FieldType.BENE_MERCHANT_PROVIDED_ID.getName()));
//			transReport.setBeneMerchantProvidedName(dbobj.getString(FieldType.BENE_MERCHANT_PROVIDED_NAME.getName()));
//			try{
//				String businessName = userdao.getBusinessNameByPayId(dbobj.getString(FieldType.PAY_ID.getName()));
//				if(businessName == null) {
//					transReport.setMerchantBusinessName("NA");
//				}else {
//					transReport.setMerchantBusinessName(businessName);
//				}
//			}catch(Exception e) {
//				logger.error("Merchant not found for Pay id : " + FieldType.PAY_ID.getName());
//				transReport.setMerchantBusinessName("NA");
//			}
		}
		
		return dbobj;
	}

}
