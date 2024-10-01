package com.pay10.crm.dashboard;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DateFormat;
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
import com.pay10.commons.exception.ErrorType;
import com.pay10.commons.exception.SystemException;
import com.pay10.commons.mongo.MongoInstance;
import com.pay10.commons.user.User;
import com.pay10.commons.user.UserDao;
import com.pay10.commons.user.UserType;
import com.pay10.commons.util.Constants;
import com.pay10.commons.util.FieldType;
import com.pay10.commons.util.MopType;
import com.pay10.commons.util.MopTypeUI;
import com.pay10.commons.util.PaymentType;
import com.pay10.commons.util.PropertiesManager;
import com.pay10.commons.util.StatusType;
import com.pay10.commons.util.TransactionType;

@Component
public class BarChartQuery {
	private static Logger logger = LoggerFactory.getLogger(BarChartQuery.class.getName());
	private static final String alphabaticFileName = "alphabatic-currencycode.properties";
	private static final String prefix = "MONGO_DB_";
	@Autowired
	private MongoInstance mongoInstance;
	@Autowired
	PropertiesManager propertiesManager;
	@Autowired
	UserDao userDao;

	public HashMap<String, String> chartTotalSummary(String payId, String currency, String dateFrom, String dateTo) {
		int noOfTransactionsvi = 0;
		int noOfTransactionsmc = 0;
		int noOfTransactionsAmex = 0;
		int noOfTransactionsMestro = 0;
		int noOfTransactionsEzee = 0;
		int noOfTransactionsWallet = 0;
		int noOfTransactionsNb = 0;
		int noOfTransactionsOther = 0;

		BasicDBObject dateQuery = new BasicDBObject();
		BasicDBObject mopQuery = new BasicDBObject();
		List<BasicDBObject> saleAndCaptureList = new ArrayList<BasicDBObject>();
		List<BasicDBObject> mopConditionLst = new ArrayList<BasicDBObject>();
		List<BasicDBObject> authAndApprovedList = new ArrayList<BasicDBObject>();
		Map<String, String> moplist = new HashMap<String, String>();
		try {
			if (!dateFrom.isEmpty()) {

				String currentDate = null;
				if (!dateTo.isEmpty()) {
					currentDate = dateTo;
				} else {
					DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
					Calendar cal = Calendar.getInstance();
					currentDate = dateFormat.format(cal.getTime());
				}
				BasicDBObject payIdquery = new BasicDBObject();
				if (!payId.equalsIgnoreCase("ALL MERCHANTS")) {
					payIdquery.put(FieldType.PAY_ID.getName(), payId);
				}
				dateQuery.put(FieldType.CREATE_DATE.getName(),
						BasicDBObjectBuilder.start("$gte", new SimpleDateFormat(dateFrom).toLocalizedPattern())
								.add("$lte", new SimpleDateFormat(currentDate).toLocalizedPattern()).get());

				List<BasicDBObject> dateIndexConditionList = new ArrayList<BasicDBObject>();
				BasicDBObject dateIndexConditionQuery = new BasicDBObject();
				String startString = new SimpleDateFormat(dateFrom).toLocalizedPattern();
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
						&& PropertiesManager.propertiesMap.get(Constants.USE_DATE_INDEX.getValue())
								.equalsIgnoreCase("Y")) {
					dateIndexConditionQuery.append("$or", dateIndexConditionList);
				}
				saleAndCaptureList.add(new BasicDBObject(FieldType.TXNTYPE.getName(), "SALE"));
				saleAndCaptureList.add(new BasicDBObject(FieldType.STATUS.getName(), "Capture"));
				BasicDBObject query1 = new BasicDBObject("$and", saleAndCaptureList);
				authAndApprovedList.add(new BasicDBObject(FieldType.TXNTYPE.getName(), "AUTHORISE"));
				authAndApprovedList.add(new BasicDBObject(FieldType.STATUS.getName(), "Approved"));
				BasicDBObject query2 = new BasicDBObject("$and", authAndApprovedList);
				BasicDBObject query = new BasicDBObject();
				query.put(FieldType.CURRENCY_CODE.getName(), currency);
				mopConditionLst.add(new BasicDBObject(FieldType.MOP_TYPE.getName(), MopType.VISA.getCode()));
				mopConditionLst.add(new BasicDBObject(FieldType.MOP_TYPE.getName(), MopType.MASTERCARD.getCode()));
				mopConditionLst.add(new BasicDBObject(FieldType.MOP_TYPE.getName(), MopType.DINERS.getCode()));
				mopConditionLst.add(new BasicDBObject(FieldType.MOP_TYPE.getName(), MopType.MAESTRO.getCode()));
				mopQuery.append("$or", mopConditionLst);
				List<BasicDBObject> fianlList = new ArrayList<BasicDBObject>();
				List<BasicDBObject> orConditionQueryList = new ArrayList<BasicDBObject>();
				orConditionQueryList.add(query1);
				orConditionQueryList.add(query2);
				BasicDBObject orConditionQueryObj = new BasicDBObject("$or", orConditionQueryList);
				if (!payIdquery.isEmpty()) {
					fianlList.add(payIdquery);
				}

				fianlList.add(dateQuery);
				fianlList.add(query);
				fianlList.add(mopQuery);
				fianlList.add(orConditionQueryObj);
				fianlList.add(dateIndexConditionQuery);

				BasicDBObject finalquery = new BasicDBObject("$and", fianlList);
				MongoDatabase dbIns = mongoInstance.getDB();
				MongoCollection<Document> coll = dbIns.getCollection(
						PropertiesManager.propertiesMap.get(prefix + Constants.COLLECTION_NAME.getValue()));

				BasicDBObject match = new BasicDBObject("$match", finalquery);

				BasicDBObject projectElement = new BasicDBObject();
				projectElement.put(FieldType.MOP_TYPE.toString(), 1);
				projectElement.put(FieldType.STATUS.toString(), 1);
				projectElement.put(FieldType.TXNTYPE.toString(), 1);
				BasicDBObject project = new BasicDBObject("$project", projectElement);

				List<BasicDBObject> pipeline = Arrays.asList(match, project);
				AggregateIterable<Document> output = coll.aggregate(pipeline);
				output.allowDiskUse(true);
				MongoCursor<Document> cursor = output.iterator();

				// MongoCursor<Document> cursor = coll.find(finalquery).iterator();
				while (cursor.hasNext()) {
					Document dbobj = cursor.next();

					if ((dbobj.getString(FieldType.MOP_TYPE.toString()).equals(MopType.VISA.getCode()))) {

						noOfTransactionsvi++;

					} else if ((dbobj.getString(FieldType.MOP_TYPE.toString()).equals(MopType.MASTERCARD.getCode()))) {

						noOfTransactionsmc++;

					} else if ((dbobj.getString(FieldType.MOP_TYPE.toString()).equals(MopType.AMEX.getCode()))) {

						noOfTransactionsAmex++;

					} else if ((dbobj.getString(FieldType.MOP_TYPE.toString()).equals(MopType.MAESTRO.getCode()))) {

						noOfTransactionsMestro++;

					} else if ((dbobj.getString(FieldType.MOP_TYPE.toString()).equals(MopType.EZEECLICK.getCode()))) {

						noOfTransactionsEzee++;

					} else if ((dbobj.getString(FieldType.MOP_TYPE.toString()).equals(PaymentType.WALLET.getCode()))) {

						noOfTransactionsWallet++;

					} else if ((dbobj.getString(FieldType.MOP_TYPE.toString())
							.equals(PaymentType.NET_BANKING.getCode()))) {

						noOfTransactionsNb++;

					} else if ((!dbobj.getString(FieldType.MOP_TYPE.toString())
							.equals(PaymentType.NET_BANKING.getCode()))
							|| (!dbobj.getString(FieldType.MOP_TYPE.toString()).equals(PaymentType.WALLET.getCode()))
							|| (!dbobj.getString(FieldType.MOP_TYPE.toString()).equals(MopType.EZEECLICK.getCode()))
							|| (!dbobj.getString(FieldType.MOP_TYPE.toString()).equals(MopType.MAESTRO.getCode())
									|| (!dbobj.getString(FieldType.MOP_TYPE.toString()).equals(MopType.VISA.getCode())))
							|| (!dbobj.getString(FieldType.MOP_TYPE.toString()).equals(MopType.MASTERCARD.getCode()))
							|| (!dbobj.getString(FieldType.MOP_TYPE.toString()).equals(MopType.AMEX.getCode()))) {
						noOfTransactionsOther++;
					}
				}
				moplist.put(MopType.VISA.getName(), String.valueOf(noOfTransactionsvi));
				moplist.put(MopType.MASTERCARD.getName(), String.valueOf(noOfTransactionsmc));
				moplist.put(MopType.AMEX.getName(), String.valueOf(noOfTransactionsAmex));
				moplist.put(MopType.MAESTRO.getName(), String.valueOf(noOfTransactionsMestro));
				moplist.put(MopType.EZEECLICK.getName(), String.valueOf(noOfTransactionsEzee));
				moplist.put(PaymentType.WALLET.getName(), String.valueOf(noOfTransactionsWallet));
				moplist.put(PaymentType.NET_BANKING.getName(), String.valueOf(noOfTransactionsNb));
				moplist.put("Other", String.valueOf(noOfTransactionsOther));
				cursor.close();
			}
		} catch (Exception exception) {
			logger.error("Exception", exception);

		}
		return (HashMap<String, String>) moplist;
	}

	public PieChart barChartTotalSummary(String payId, String currency, String dateFrom, String dateTo) {
		int totalCredit = 0;
		int totalDebit = 0;
		int totalNet = 0;
		int other = 0;
		PieChart pieChart = new PieChart();

		BasicDBObject payIdquery = new BasicDBObject();
		BasicDBObject mopQuery = new BasicDBObject();
		BasicDBObject dateQuery = new BasicDBObject();
		BasicDBObject txnTypeConditionQuery = new BasicDBObject();
		BasicDBObject paymentTypeQuery = new BasicDBObject();
		List<BasicDBObject> paymentTypeConditionLst = new ArrayList<BasicDBObject>();
		List<BasicDBObject> mopConditionLst = new ArrayList<BasicDBObject>();
		List<BasicDBObject> finalList = new ArrayList<BasicDBObject>();
		List<BasicDBObject> txnTypeConditionList = new ArrayList<BasicDBObject>();
		DateFormat df = new SimpleDateFormat("EEE MMM dd yyyy HH:mm:ss");
		SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd");
		try {
			String startDate = sdf1.format(df.parse(dateFrom));
			String endDate = sdf1.format(df.parse(dateTo));
			String currentDate = null;
			if (!startDate.isEmpty()) {

				if (!endDate.isEmpty()) {
					currentDate = endDate;
				} else {
					DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
					Calendar cal = Calendar.getInstance();
					currentDate = dateFormat.format(cal.getTime());
				}

				dateQuery.put(FieldType.CREATE_DATE.getName(),
						BasicDBObjectBuilder.start("$gte", new SimpleDateFormat(startDate).toLocalizedPattern())
								.add("$lte", new SimpleDateFormat(currentDate).toLocalizedPattern()).get());
			}

			List<BasicDBObject> dateIndexConditionList = new ArrayList<BasicDBObject>();
			BasicDBObject dateIndexConditionQuery = new BasicDBObject();
			String startString = new SimpleDateFormat(startDate).toLocalizedPattern();
			String endString = new SimpleDateFormat(currentDate).toLocalizedPattern();

			DateFormat format = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss", Locale.ENGLISH);
			Date dateStart = format.parse(startString);
			Date dateEnd = format.parse(endString);

			LocalDate incrementingDate = dateStart.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
			LocalDate endDates = dateEnd.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();

			List<String> allDatesIndex = new ArrayList<>();

			while (!incrementingDate.isAfter(endDates)) {
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

			if (!payId.equalsIgnoreCase("ALL MERCHANTS")) {

				payIdquery.put(FieldType.PAY_ID.getName(), payId);
			}
			BasicDBObject query = new BasicDBObject();
			query.put(FieldType.CURRENCY_CODE.getName(), currency);

			List<BasicDBObject> saleAndCapList = new ArrayList<BasicDBObject>();
			saleAndCapList.add(new BasicDBObject(FieldType.TXNTYPE.getName(), TransactionType.SALE.getName()));
			saleAndCapList.add(
					new BasicDBObject(new BasicDBObject(FieldType.STATUS.getName(), StatusType.CAPTURED.getName())));
			BasicDBObject saleAndCapquery2 = new BasicDBObject("$and", saleAndCapList);

			List<BasicDBObject> authAndAllList = new ArrayList<BasicDBObject>();
			authAndAllList.add(new BasicDBObject(FieldType.TXNTYPE.getName(), TransactionType.AUTHORISE.getName()));
			authAndAllList.add(
					new BasicDBObject(new BasicDBObject(FieldType.STATUS.getName(), StatusType.APPROVED.getName())));
			BasicDBObject authAndAppquery3 = new BasicDBObject("$and", authAndAllList);
			txnTypeConditionList.add(saleAndCapquery2);
			txnTypeConditionList.add(authAndAppquery3);
			txnTypeConditionQuery.append("$or", txnTypeConditionList);
			mopConditionLst.add(new BasicDBObject(FieldType.MOP_TYPE.getName(), MopType.VISA.getCode()));
			mopConditionLst.add(new BasicDBObject(FieldType.MOP_TYPE.getName(), MopType.MASTERCARD.getCode()));
			mopConditionLst.add(new BasicDBObject(FieldType.MOP_TYPE.getName(), MopType.DINERS.getCode()));
			mopConditionLst.add(new BasicDBObject(FieldType.MOP_TYPE.getName(), MopType.MAESTRO.getCode()));
			mopQuery.append("$or", mopConditionLst);

			paymentTypeConditionLst
					.add(new BasicDBObject(FieldType.PAYMENT_TYPE.getName(), PaymentType.CREDIT_CARD.getCode()));
			paymentTypeConditionLst
					.add(new BasicDBObject(FieldType.PAYMENT_TYPE.getName(), PaymentType.DEBIT_CARD.getCode()));
			paymentTypeConditionLst
					.add(new BasicDBObject(FieldType.PAYMENT_TYPE.getName(), PaymentType.NET_BANKING.getCode()));
		//	paymentTypeConditionLst.add(new BasicDBObject(FieldType.PAYMENT_TYPE.getName(), PaymentType.EMI.getCode()));
			paymentTypeConditionLst
					.add(new BasicDBObject(FieldType.PAYMENT_TYPE.getName(), PaymentType.WALLET.getCode()));
//			paymentTypeConditionLst
//					.add(new BasicDBObject(FieldType.PAYMENT_TYPE.getName(), PaymentType.RECURRING_PAYMENT.getCode()));
			paymentTypeConditionLst
					.add(new BasicDBObject(FieldType.PAYMENT_TYPE.getName(), PaymentType.EXPRESS_PAY.getCode()));
			paymentTypeQuery.append("$or", paymentTypeConditionLst);

			if (!dateQuery.isEmpty()) {
				finalList.add(dateQuery);
			}

			if (!mopQuery.isEmpty()) {
				finalList.add(mopQuery);
			}
			if (!query.isEmpty()) {
				finalList.add(query);
			}
			if (!payIdquery.isEmpty()) {
				finalList.add(payIdquery);
			}

			if (!txnTypeConditionQuery.isEmpty()) {
				finalList.add(txnTypeConditionQuery);
			}

			if (!dateIndexConditionQuery.isEmpty()) {
				finalList.add(dateIndexConditionQuery);
			}

			BasicDBObject finalobjectQuery = new BasicDBObject("$and", finalList);
			MongoDatabase dbIns = mongoInstance.getDB();
			MongoCollection<Document> coll = dbIns
					.getCollection(PropertiesManager.propertiesMap.get(prefix + Constants.COLLECTION_NAME.getValue()));

			BasicDBObject match = new BasicDBObject("$match", finalobjectQuery);

			BasicDBObject projectElement = new BasicDBObject();
			projectElement.put(FieldType.PAYMENT_TYPE.toString(), 1);
			projectElement.put(FieldType.STATUS.toString(), 1);
			projectElement.put(FieldType.TXNTYPE.toString(), 1);
			BasicDBObject project = new BasicDBObject("$project", projectElement);

			List<BasicDBObject> pipeline = Arrays.asList(match, project);
			AggregateIterable<Document> output = coll.aggregate(pipeline);
			output.allowDiskUse(true);
			MongoCursor<Document> cursor = output.iterator();

			// MongoCursor<Document> cursor = coll.find(finalobjectQuery).iterator();

			while (cursor.hasNext()) {
				Document dbobj = cursor.next();

				if ((dbobj.getString(FieldType.PAYMENT_TYPE.toString()).equals(PaymentType.CREDIT_CARD.getCode()))) {
					totalCredit++;

				} else if ((dbobj.getString(FieldType.PAYMENT_TYPE.toString())
						.equals(PaymentType.DEBIT_CARD.getCode()))) {
					totalDebit++;

				} else if ((dbobj.getString(FieldType.PAYMENT_TYPE.toString())
						.equals(PaymentType.NET_BANKING.getCode()))) {
					totalNet++;

				} else if ((!dbobj.getString(FieldType.PAYMENT_TYPE.toString())
						.equals(PaymentType.CREDIT_CARD.getCode()))
						|| (!dbobj.getString(FieldType.PAYMENT_TYPE.toString())
								.equals(PaymentType.DEBIT_CARD.getCode()))
						|| (!dbobj.getString(FieldType.PAYMENT_TYPE.toString())
								.equals(PaymentType.NET_BANKING.getCode()))) {
					other++;
				}

				pieChart.setTotalCredit(String.valueOf(totalCredit));
				pieChart.setTotalDebit(String.valueOf(totalDebit));
				pieChart.setNet(String.valueOf(totalNet));
				pieChart.setOther(String.valueOf(other));

			}

			cursor.close();
		} catch (ParseException exception) {
			logger.error("", exception);
		}

		return pieChart;
	}

	public Map<String, PieChart> totalTransactionRecord(String payId, String currency, String dateFrom, String dateTo , User user,String paymentType,String acquirer,String transactionType,String mopType)
			throws SystemException {
		
		Map<String, PieChart> dateChart = new HashMap<String, PieChart>();
		
		int totalSuccess = 0;
		int totalFailed = 0;
		int totalRefund = 0;
		int totalCancelled = 0 ;
		//BasicDBObject txnTypeQuery = new BasicDBObject();
		//List<BasicDBObject> statusConditionLst = new ArrayList<BasicDBObject>();
		//List<String> failedstatusLst = new ArrayList<String>();
		List<BasicDBObject> allConditionQueryList = new ArrayList<BasicDBObject>();
		//List<BasicDBObject> txnTypeConditionLst = new ArrayList<BasicDBObject>();
		BasicDBObject dateQuery = new BasicDBObject();
		//BasicDBObject statusQuery = new BasicDBObject();
		BasicDBObject payIdquery = new BasicDBObject();
		BasicDBObject resellerIdquery = new BasicDBObject();
		BasicDBObject acquirerQuery = new BasicDBObject();
		List<BasicDBObject> acquirerConditionLst = new ArrayList<BasicDBObject>();
		List<BasicDBObject> mopTypeConditionLst = new ArrayList<BasicDBObject>();
		BasicDBObject mopTypeQuery = new BasicDBObject();
		List<BasicDBObject> paymentTypeConditionLst = new ArrayList<BasicDBObject>();
		BasicDBObject paymentTypeQuery = new BasicDBObject();
		List<BasicDBObject> currencyConditionLst = new ArrayList<BasicDBObject>();
		BasicDBObject currencyCodeQuery = new BasicDBObject();

		
		MongoDatabase dbIns = mongoInstance.getDB();
		MongoCollection<Document> coll = dbIns
				.getCollection(PropertiesManager.propertiesMap.get(prefix + Constants.TRANSACTION_STATUS_COLLECTION.getValue()));

		
		dateFrom = dateFrom + " 00:00:00";
		dateTo = dateTo + " 23:59:59";
		try {

			String currentDate = null;
			if (!dateFrom.isEmpty()) {

				if (!dateTo.isEmpty()) {
					currentDate = dateTo;
				} else {
					DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
					Calendar cal = Calendar.getInstance();
					currentDate = dateFormat.format(cal.getTime());
				}

				dateQuery.put(FieldType.CREATE_DATE.getName(),
						BasicDBObjectBuilder.start("$gte", new SimpleDateFormat(dateFrom).toLocalizedPattern())
								.add("$lte", new SimpleDateFormat(currentDate).toLocalizedPattern()).get());
			}

			List<BasicDBObject> dateIndexConditionList = new ArrayList<BasicDBObject>();
			BasicDBObject dateIndexConditionQuery = new BasicDBObject();
			String startString = new SimpleDateFormat(dateFrom).toLocalizedPattern();
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
				
				if (payId.equalsIgnoreCase("ALL MERCHANTS")) {
					if (!user.getSegment().equalsIgnoreCase("Default")) {
						List<String> payIdLst = userDao.getPayIdForSplitPaymentMerchant(user.getSegment());
						logger.info("Get PayId For SplitPayment Merchant : " + payIdLst);
						if (payIdLst.size() > 0) {
							payIdquery.put(FieldType.PAY_ID.getName(), new BasicDBObject("$in", payIdLst));
						}
					}
				} else {
					payIdquery.put(FieldType.PAY_ID.getName(), payId);
				}
				dateIndexConditionList.add(new BasicDBObject("DATE_INDEX", dateIndex));
			}
			if (StringUtils.isNotBlank(PropertiesManager.propertiesMap.get(Constants.USE_DATE_INDEX.getValue()))
					&& PropertiesManager.propertiesMap.get(Constants.USE_DATE_INDEX.getValue()).equalsIgnoreCase("Y")) {
				
				if (dateIndexConditionList.size() > 0) {
					dateIndexConditionQuery.append("$or", dateIndexConditionList);
				}
				
			}

			if (payId.equalsIgnoreCase("ALL MERCHANTS")) {
				if (!user.getSegment().equalsIgnoreCase("Default")) {
					List<String> payIdLst = userDao.getPayIdForSplitPaymentMerchant(user.getSegment());
					logger.info("Get PayId For SplitPayment Merchant : " + payIdLst);
					if (payIdLst.size() > 0) {
						payIdquery.put(FieldType.PAY_ID.getName(), new BasicDBObject("$in", payIdLst));
					}
				}
			} else {
				payIdquery.put(FieldType.PAY_ID.getName(), payId);
			}
			
			if (user.getUserType().equals(UserType.RESELLER)) {
				resellerIdquery.put(FieldType.RESELLER_ID.getName(), user.getResellerId());

			}

			BasicDBObject query = new BasicDBObject();
			query.put(FieldType.CURRENCY_CODE.getName(), currency);
			
			if (!transactionType.equalsIgnoreCase("ALL")) {
				allConditionQueryList.add(new BasicDBObject(FieldType.ORIG_TXNTYPE.getName(), transactionType));
			}
					
			if (!acquirer.equalsIgnoreCase("ALL")) {
				List<String> acquirerList = Arrays.asList(acquirer.split(","));
				for (String acq : acquirerList) {
					acquirerConditionLst.add(new BasicDBObject(FieldType.ACQUIRER_TYPE.getName(), acq.trim()));
				}
				acquirerQuery.append("$or", acquirerConditionLst);

			}

			if (!mopType.equalsIgnoreCase("ALL")) {
				List<String> mopTypeList = Arrays.asList(mopType.split(","));
				for (String mop : mopTypeList) {
					if (mop.equalsIgnoreCase("Others")) {
							List<String> mopTypeOtherList = Arrays.asList(MopType.getOTherTypeCodes().split(","));
								;
							for (String mopOther : mopTypeOtherList) {
									mopTypeConditionLst.add(new BasicDBObject(FieldType.MOP_TYPE.getName(), mopOther.trim()));
							}
					} else {
							mopTypeConditionLst.add(new BasicDBObject(FieldType.MOP_TYPE.getName(), mop));
					}

				}
				mopTypeQuery.append("$or", mopTypeConditionLst);
			}

			if (!paymentType.equalsIgnoreCase("ALL")) {
				List<String> paymentTypeList = Arrays.asList(paymentType.split(","));
				for (String payment : paymentTypeList) {
					paymentTypeConditionLst.add(new BasicDBObject(FieldType.PAYMENT_TYPE.getName(), payment.trim()));
				}
				paymentTypeQuery.append("$or", paymentTypeConditionLst);
			}
			if (!currency.equalsIgnoreCase("ALL")) {
				List<String> currencyCodeList = Arrays.asList(currency.split(","));
				for (String currencyCode : currencyCodeList) {
					currencyConditionLst.add(new BasicDBObject(FieldType.CURRENCY_CODE.getName(), currencyCode.trim()));
				}
				currencyCodeQuery.append("$or", currencyConditionLst);
			}
		
			if (!query.isEmpty()) {
				allConditionQueryList.add(query);
			}
			if (!dateQuery.isEmpty()) {
				allConditionQueryList.add(dateQuery);
			}
			if (!payIdquery.isEmpty()) {
				allConditionQueryList.add(payIdquery);
			}
			
			if (!resellerIdquery.isEmpty()) {
				allConditionQueryList.add(resellerIdquery);
			}
			if (!dateIndexConditionQuery.isEmpty()) {
				allConditionQueryList.add(dateIndexConditionQuery);
			}

			if (!acquirerQuery.isEmpty()) {
				allConditionQueryList.add(acquirerQuery);
			}

			if (!mopTypeQuery.isEmpty()) {
				allConditionQueryList.add(mopTypeQuery);
			}
			if (!paymentTypeQuery.isEmpty()) {
				allConditionQueryList.add(paymentTypeQuery);
			}
			if (!currencyCodeQuery.isEmpty()) {
				allConditionQueryList.add(currencyCodeQuery);
			}
			
			BasicDBObject allCondi = new BasicDBObject("$and", allConditionQueryList);
			
			BasicDBObject match = new BasicDBObject("$match", allCondi);

			BasicDBObject projectElement = new BasicDBObject();
			projectElement.put(FieldType.CREATE_DATE.toString(), 1);
			projectElement.put(FieldType.ALIAS_STATUS.toString(), 1);
			projectElement.put(FieldType.TXNTYPE.toString(), 1);
			projectElement.put(FieldType.ORIG_TXNTYPE.toString(), 1);
			projectElement.put(FieldType.TOTAL_AMOUNT.toString(), 1);
			BasicDBObject project = new BasicDBObject("$project", projectElement);

			List<BasicDBObject> pipeline = Arrays.asList(match, project);
			AggregateIterable<Document> output = coll.aggregate(pipeline);
			output.allowDiskUse(true);
			MongoCursor<Document> cursor = output.iterator();

			while (cursor.hasNext()) {

				Document dbobj = cursor.next();
				String txndat = dbobj.getString(FieldType.CREATE_DATE.getName());
				String[] splitdate = txndat.split(" ");
				String date = splitdate[0];

				if (!StringUtils.isEmpty(date) && !StringUtils.isEmpty(dbobj.getString(FieldType.ALIAS_STATUS.toString()))) {

					if (dateChart.containsKey(date)) {
						PieChart piechart1 = dateChart.get(date);
						switch (dbobj.getString(FieldType.ORIG_TXNTYPE.toString())) {
						case "SALE":

							if (dbobj.getString(FieldType.ALIAS_STATUS.toString()).equals(StatusType.CAPTURED.getName()) ||
									dbobj.getString(FieldType.ALIAS_STATUS.toString()).equals(StatusType.SETTLED_SETTLE.getName())) {
								
								String successTxn = piechart1.getTotalSuccess();
								double amount = Double.valueOf(dbobj.get(FieldType.TOTAL_AMOUNT.getName(), "0"));
								if (successTxn == null) {

									totalSuccess = 1;
									piechart1.setTotalSuccess(String.valueOf(totalSuccess));
									piechart1.setTotalSuccessAmount(amount);
									dateChart.put(date, piechart1);
								} else {
									totalSuccess = Integer.parseInt(piechart1.getTotalSuccess());
									totalSuccess++;
									piechart1.setTotalSuccess(String.valueOf(totalSuccess));
									piechart1.setTotalSuccessAmount(piechart1.getTotalSuccessAmount() + amount);
									dateChart.put(date, piechart1);
								}

							} else if (dbobj.getString(FieldType.ALIAS_STATUS.toString()).equals(StatusType.FAILED.getName())) {
								
								String failedTxn = piechart1.getTotalFailed();
								if (failedTxn == null) {

									totalFailed = 1;
									piechart1.setTotalFailed(String.valueOf(totalFailed));
									dateChart.put(date, piechart1);
								} else {
									totalFailed = Integer.parseInt(piechart1.getTotalFailed());
									totalFailed++;
									piechart1.setTotalFailed(String.valueOf(totalFailed));
									dateChart.put(date, piechart1);
								}

							}else if (dbobj.getString(FieldType.ALIAS_STATUS.toString()).equals(StatusType.CANCELLED.getName())){
								String cancelTxn = piechart1.getTotalCancelled();
								if (cancelTxn == null) {
									totalCancelled = 1;
									piechart1.setTotalCancelled(String.valueOf(totalCancelled));
									piechart1.setTxndate(date);
									dateChart.put(date, piechart1);
								} else {
									totalCancelled = Integer.parseInt(piechart1.getTotalCancelled());
									totalCancelled++;
									piechart1.setTotalCancelled(String.valueOf(totalCancelled));
									piechart1.setTxndate(date);

									dateChart.put(date, piechart1);
								}

							}

							else {
								// Do Nothing
							}
							break;
						
							
						case "REFUND":
							if (dbobj.getString(FieldType.ALIAS_STATUS.toString()).equals(StatusType.CAPTURED.getName()) ||
									dbobj.getString(FieldType.ALIAS_STATUS.toString()).equals(StatusType.SETTLED_SETTLE.getName())) {

								String refundTxn = piechart1.getTotalRefunded();
								if (refundTxn == null) {
									totalRefund = 1;
									piechart1.setTotalRefunded(String.valueOf(totalRefund));
									dateChart.put(date, piechart1);
								} else {
									totalRefund = Integer.parseInt(piechart1.getTotalRefunded());
									totalRefund++;
									piechart1.setTotalRefunded(String.valueOf(totalRefund));

									dateChart.put(date, piechart1);
								}

							} else if (dbobj.getString(FieldType.ALIAS_STATUS.toString()).equals(StatusType.FAILED.getName())) {
								
								String failedTxn = piechart1.getTotalFailed();
								if (failedTxn == null) {

									totalFailed = 1;
									piechart1.setTotalFailed(String.valueOf(totalFailed));
									dateChart.put(date, piechart1);
								} else {
									totalFailed = Integer.parseInt(piechart1.getTotalFailed());
									totalFailed++;
									piechart1.setTotalFailed(String.valueOf(totalFailed));
									dateChart.put(date, piechart1);
								}

							}else if (dbobj.getString(FieldType.ALIAS_STATUS.toString()).equals(StatusType.CANCELLED.getName())){
								String cancelTxn = piechart1.getTotalCancelled();
								if (cancelTxn == null) {
									totalCancelled = 1;
									piechart1.setTotalCancelled(String.valueOf(totalCancelled));
									piechart1.setTxndate(date);
									dateChart.put(date, piechart1);
								} else {
									totalCancelled = Integer.parseInt(piechart1.getTotalCancelled());
									totalCancelled++;
									piechart1.setTotalCancelled(String.valueOf(totalCancelled));
									piechart1.setTxndate(date);

									dateChart.put(date, piechart1);
								}

							}
							else {
								// Do Nothing
							}

							break;

						}

					} else {
						PieChart piechart1 = dateChart.get(date);
						if (piechart1 == null) {
							piechart1 = new PieChart();
						}
						switch (dbobj.getString(FieldType.ORIG_TXNTYPE.toString())) {
						case "SALE":

							if (dbobj.getString(FieldType.ALIAS_STATUS.toString()).equals(StatusType.CAPTURED.getName()) ||
									dbobj.getString(FieldType.ALIAS_STATUS.toString()).equals(StatusType.SETTLED_SETTLE.getName())) {
								double amount = Double.valueOf(dbobj.get(FieldType.TOTAL_AMOUNT.getName(), "0"));
								String successTxn = piechart1.getTotalSuccess();
								if (successTxn == null) {

									totalSuccess = 1;
									piechart1.setTotalSuccess(String.valueOf(totalSuccess));
									piechart1.setTotalSuccessAmount(amount);
									dateChart.put(date, piechart1);
								} else {
									totalSuccess = Integer.parseInt(piechart1.getTotalSuccess());
									totalSuccess++;
									piechart1.setTotalSuccess(String.valueOf(totalSuccess));
									piechart1.setTotalSuccessAmount(piechart1.getTotalSuccessAmount() + amount);
									dateChart.put(date, piechart1);
								}

							} else if (dbobj.getString(FieldType.ALIAS_STATUS.toString()).equals(StatusType.FAILED.getName())) {
								
								String failedTxn = piechart1.getTotalFailed();
								if (failedTxn == null) {

									totalFailed = 1;
									piechart1.setTotalFailed(String.valueOf(totalFailed));
									dateChart.put(date, piechart1);
								} else {
									totalFailed = Integer.parseInt(piechart1.getTotalFailed());
									totalFailed++;
									piechart1.setTotalFailed(String.valueOf(totalFailed));
									dateChart.put(date, piechart1);
								}

							}else if (dbobj.getString(FieldType.ALIAS_STATUS.toString()).equals(StatusType.CANCELLED.getName())){
								String cancelTxn = piechart1.getTotalCancelled();
								if (cancelTxn == null) {
									totalCancelled = 1;
									piechart1.setTotalCancelled(String.valueOf(totalCancelled));
									piechart1.setTxndate(date);
									dateChart.put(date, piechart1);
								} else {
									totalCancelled = Integer.parseInt(piechart1.getTotalCancelled());
									totalCancelled++;
									piechart1.setTotalCancelled(String.valueOf(totalCancelled));
									piechart1.setTxndate(date);

									dateChart.put(date, piechart1);
								}

							}

							else {
								// Do Nothing
							}
							break;
						
							
						case "REFUND":
							if (dbobj.getString(FieldType.ALIAS_STATUS.toString()).equals(StatusType.CAPTURED.getName()) ||
									dbobj.getString(FieldType.ALIAS_STATUS.toString()).equals(StatusType.SETTLED_SETTLE.getName())) {

								String refundTxn = piechart1.getTotalRefunded();
								if (refundTxn == null) {
									totalRefund = 1;
									piechart1.setTotalRefunded(String.valueOf(totalRefund));
									dateChart.put(date, piechart1);
								} else {
									totalRefund = Integer.parseInt(piechart1.getTotalRefunded());
									totalRefund++;
									piechart1.setTotalRefunded(String.valueOf(totalRefund));

									dateChart.put(date, piechart1);
								}

							} else if (dbobj.getString(FieldType.ALIAS_STATUS.toString()).equals(StatusType.FAILED.getName())) {
								
								String failedTxn = piechart1.getTotalFailed();
								if (failedTxn == null) {

									totalFailed = 1;
									piechart1.setTotalFailed(String.valueOf(totalFailed));
									dateChart.put(date, piechart1);
								} else {
									totalFailed = Integer.parseInt(piechart1.getTotalFailed());
									totalFailed++;
									piechart1.setTotalFailed(String.valueOf(totalFailed));
									dateChart.put(date, piechart1);
								}

							}else if (dbobj.getString(FieldType.ALIAS_STATUS.toString()).equals(StatusType.CANCELLED.getName())){
								String cancelTxn = piechart1.getTotalCancelled();
								if (cancelTxn == null) {
									totalCancelled = 1;
									piechart1.setTotalCancelled(String.valueOf(totalCancelled));
									piechart1.setTxndate(date);
									dateChart.put(date, piechart1);
								} else {
									totalCancelled = Integer.parseInt(piechart1.getTotalCancelled());
									totalCancelled++;
									piechart1.setTotalCancelled(String.valueOf(totalCancelled));
									piechart1.setTxndate(date);

									dateChart.put(date, piechart1);
								}

							}
							else {
								// Do Nothing
							}

							break;

						}

				}

			}
			}

			cursor.close();
		} catch (Exception exception) {
			logger.error("Exception", exception);

		}
		return dateChart;
	}

	public Map<String, PieChart> totalMonthlyTransactionRecord(String payId, String currency, String dateFrom, String dateTo , User user)
			throws SystemException {
		
		Map<String, PieChart> dateChart = new HashMap<String, PieChart>();

		int totalSuccess = 0;
		int totalFailed = 0;
		int totalRefund = 0;
		int errors = 0 ;
		int timeout = 0; 
		int totalCancelled = 0;
		BasicDBObject txnTypeQuery = new BasicDBObject();
		List<BasicDBObject> statusConditionLst = new ArrayList<BasicDBObject>();
		List<BasicDBObject> allConditionQueryList = new ArrayList<BasicDBObject>();
		List<String> failedstatusLst = new ArrayList<String>();
		List<BasicDBObject> txnTypeConditionLst = new ArrayList<BasicDBObject>();
		BasicDBObject dateQuery = new BasicDBObject();
		BasicDBObject statusQuery = new BasicDBObject();
		BasicDBObject payIdquery = new BasicDBObject();
		BasicDBObject resellerIdquery = new BasicDBObject();
		
		MongoDatabase dbIns = mongoInstance.getDB();
		MongoCollection<Document> coll = dbIns
				.getCollection(PropertiesManager.propertiesMap.get(prefix + Constants.COLLECTION_NAME.getValue()));

		MongoCollection<Document> dashboardColl = dbIns
				.getCollection(PropertiesManager.propertiesMap.get(prefix + Constants.DASHBOARD_POPULATOR_NAME.getValue()));
		
		
		
		dateFrom = dateFrom + " 00:00:00";
		dateTo = dateTo + " 23:59:59";
		try {

			String currentDate = null;
			if (!dateFrom.isEmpty()) {

				if (!dateTo.isEmpty()) {
					currentDate = dateTo;
				} else {
					DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
					Calendar cal = Calendar.getInstance();
					currentDate = dateFormat.format(cal.getTime());
				}

				dateQuery.put(FieldType.CREATE_DATE.getName(),
						BasicDBObjectBuilder.start("$gte", new SimpleDateFormat(dateFrom).toLocalizedPattern())
								.add("$lte", new SimpleDateFormat(currentDate).toLocalizedPattern()).get());
			}

//			List<BasicDBObject> dateIndexConditionList = new ArrayList<BasicDBObject>();
//			BasicDBObject dateIndexConditionQuery = new BasicDBObject();
			String startString = new SimpleDateFormat(dateFrom).toLocalizedPattern();
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
				
				// Verify if data already present in dashboard populator collection
				
				BasicDBObject dateIndexQuery = new BasicDBObject(FieldType.CREATE_DATE.getName(), dateIndex);
				BasicDBObject payIdQuery = null;
				
				if (payId.equalsIgnoreCase("ALL MERCHANTS")) {
					payIdQuery = new BasicDBObject(FieldType.PAY_ID.getName(), "ALL");
				}
				else {
					payIdQuery = new BasicDBObject(FieldType.PAY_ID.getName(), payId);
				}
				
				if (user.getUserType().equals(UserType.RESELLER)) {
					resellerIdquery = new BasicDBObject(FieldType.RESELLER_ID.getName(),user.getResellerId() );
				}
				
				List<BasicDBObject> dashBoardCondQueryList = new ArrayList<BasicDBObject>();
				
				dashBoardCondQueryList.add(dateIndexQuery);
				dashBoardCondQueryList.add(payIdQuery);
				dashBoardCondQueryList.add(resellerIdquery);
				
				BasicDBObject dashBoardQuery = new BasicDBObject("$and", dashBoardCondQueryList);
				
				long dataCount = dashboardColl.count(dashBoardQuery);
				
				// Data is not present in dashboard populator collection
				if (dataCount < 1) {
//					dateIndexConditionList.add(new BasicDBObject("DATE_INDEX", dateIndex));
				}
				
				// Data found in dashboard populator collection
				else {
					
					FindIterable<Document> dashBoardobject = dashboardColl.find(dashBoardQuery);
					MongoCursor<Document> cursor = dashBoardobject.iterator();
					
					while(cursor.hasNext()) {
						
						Document doc = cursor.next();
						
						String createDate = doc.getString(FieldType.CREATE_DATE.getName());
						String year = createDate.substring(0,4);
						String month = createDate.substring(4,6);
						String date = createDate.substring(6,8);
						
						String createDateModified = year+"-"+month+"-"+date; 
						String saleCount = "0";
						String refundCount = "0";
						String failedCount = "0";
						if (user.getUserType().equals(UserType.ADMIN) || user.getUserType().equals(UserType.SUBADMIN) ) {
							
							saleCount = doc.getString(FieldType.DASHBOARD_SALE_COUNT.getName());
							refundCount = doc.getString(FieldType.DASHBOARD_REFUND_COUNT.getName());
							failedCount = doc.getString(FieldType.DASHBOARD_FAILED_COUNT.getName());
						}
						
						else {
							saleCount = doc.getString(FieldType.DASHBOARD_SALE_COUNT.getName());
							refundCount = doc.getString(FieldType.DASHBOARD_REFUND_COUNT.getName());
							failedCount = doc.getString(FieldType.DASHBOARD_FAILED_COUNT.getName());
							
						}
						
						PieChart pieChart = new PieChart();
						pieChart.setTotalSuccess(saleCount);
						pieChart.setTotalRefunded(refundCount);
						pieChart.setTotalFailed(failedCount);
						pieChart.setTxndate(createDateModified);
						
						dateChart.put(createDateModified, pieChart);
						break;
					}
					
					
					
				}
				
				
			}
			if (StringUtils.isNotBlank(PropertiesManager.propertiesMap.get(Constants.USE_DATE_INDEX.getValue()))
					&& PropertiesManager.propertiesMap.get(Constants.USE_DATE_INDEX.getValue()).equalsIgnoreCase("Y")) {
				
				/*
				 * if (dateIndexConditionList.size() > 0) {
				 * dateIndexConditionQuery.append("$or", dateIndexConditionList); }
				 */
				
			}

			if (!payId.equalsIgnoreCase("ALL MERCHANTS")) {
				payIdquery.put(FieldType.PAY_ID.getName(), payId);

			}
			
			if (user.getUserType().equals(UserType.RESELLER)) {
				resellerIdquery.put(FieldType.RESELLER_ID.getName(), user.getResellerId());

			}
			
			BasicDBObject query = new BasicDBObject();
			query.put(FieldType.CURRENCY_CODE.getName(), currency);
			txnTypeConditionLst.add(new BasicDBObject(FieldType.TXNTYPE.getName(), TransactionType.SALE.getName()));
			txnTypeConditionLst
					.add(new BasicDBObject(FieldType.TXNTYPE.getName(), TransactionType.AUTHORISE.getName()));
			txnTypeConditionLst
			.add(new BasicDBObject(FieldType.TXNTYPE.getName(), TransactionType.ENROLL.getName()));
			txnTypeConditionLst.add(new BasicDBObject(FieldType.TXNTYPE.getName(), TransactionType.CAPTURE.getName()));
			txnTypeConditionLst.add(new BasicDBObject(FieldType.TXNTYPE.getName(), TransactionType.REFUND.getName()));
			txnTypeQuery.append("$or", txnTypeConditionLst);
			statusConditionLst.add(new BasicDBObject(FieldType.STATUS.getName(), StatusType.CAPTURED.getName()));
			// statusConditionLst.add(new BasicDBObject(FieldType.STATUS.getName(),
			// StatusType.APPROVED.getName()));
			statusConditionLst.add(new BasicDBObject(FieldType.STATUS.getName(), StatusType.FAILED.getName()));
			statusConditionLst.add(new BasicDBObject(FieldType.STATUS.getName(), StatusType.DECLINED.getName()));
			statusConditionLst.add(new BasicDBObject(FieldType.STATUS.getName(), StatusType.REJECTED.getName()));			
			statusConditionLst.add(new BasicDBObject(FieldType.STATUS.getName(), StatusType.DENIED.getName()));
			statusConditionLst.add(new BasicDBObject(FieldType.STATUS.getName(), StatusType.DUPLICATE.getName()));
			statusConditionLst.add(new BasicDBObject(FieldType.STATUS.getName(), StatusType.AUTHENTICATION_FAILED.getName()));
			statusConditionLst.add(new BasicDBObject(FieldType.STATUS.getName(), StatusType.DENIED_BY_FRAUD.getName()));
			statusConditionLst.add(new BasicDBObject(FieldType.STATUS.getName(), StatusType.ACQUIRER_DOWN.getName()));
			statusConditionLst.add(new BasicDBObject(FieldType.STATUS.getName(), StatusType.FAILED_AT_ACQUIRER.getName()));
			
			statusConditionLst.add(new BasicDBObject(FieldType.STATUS.getName(), StatusType.ERROR.getName()));
			statusConditionLst.add(new BasicDBObject(FieldType.STATUS.getName(), StatusType.TIMEOUT.getName()));
			statusConditionLst.add(new BasicDBObject(FieldType.STATUS.getName(), StatusType.ACQUIRER_TIMEOUT.getName()));
			// statusConditionLst.add(new BasicDBObject(FieldType.STATUS.getName(),
			// StatusType.BROWSER_CLOSED.getName()));
			// statusConditionLst.add(new BasicDBObject(FieldType.STATUS.getName(),
			// StatusType.DENIED.getName()));
			// statusConditionLst.add(new BasicDBObject(FieldType.STATUS.getName(),
			// StatusType.DENIED_BY_FRAUD.getName()));
			// statusConditionLst.add(new BasicDBObject(FieldType.STATUS.getName(),
			// StatusType.INVALID.getName()));
			
			failedstatusLst.add(StatusType.FAILED.getName().toString());
			failedstatusLst.add(StatusType.DECLINED.getName().toString());
			failedstatusLst.add(StatusType.REJECTED.getName().toString());
			failedstatusLst.add(StatusType.DENIED.getName().toString());
			failedstatusLst.add(StatusType.DUPLICATE.getName().toString());									
			failedstatusLst.add(StatusType.AUTHENTICATION_FAILED.getName().toString());
			failedstatusLst.add(StatusType.DENIED_BY_FRAUD.getName().toString());
			failedstatusLst.add(StatusType.ACQUIRER_DOWN.getName().toString());
			failedstatusLst.add(StatusType.FAILED_AT_ACQUIRER.getName().toString());			

			statusQuery.append("$or", statusConditionLst);

			if (!query.isEmpty()) {
				allConditionQueryList.add(query);
			}
			if (!dateQuery.isEmpty()) {
				allConditionQueryList.add(dateQuery);
			}
			if (!txnTypeQuery.isEmpty()) {
				allConditionQueryList.add(txnTypeQuery);
			}
			if (!statusQuery.isEmpty()) {
				allConditionQueryList.add(statusQuery);
			}
			if (!payIdquery.isEmpty()) {
				allConditionQueryList.add(payIdquery);
			}
			
			if (!resellerIdquery.isEmpty()) {
				allConditionQueryList.add(resellerIdquery);
			}
			
			/*
			 * if (!dateIndexConditionQuery.isEmpty()) {
			 * allConditionQueryList.add(dateIndexConditionQuery); }
			 */

			BasicDBObject allCondi = new BasicDBObject("$and", allConditionQueryList);
			
			BasicDBObject match = new BasicDBObject("$match", allCondi);

			BasicDBObject projectElement = new BasicDBObject();
			projectElement.put(FieldType.CREATE_DATE.toString(), 1);
			projectElement.put(FieldType.STATUS.toString(), 1);
			projectElement.put(FieldType.TXNTYPE.toString(), 1);
			BasicDBObject project = new BasicDBObject("$project", projectElement);

			List<BasicDBObject> pipeline = Arrays.asList(match, project);
			AggregateIterable<Document> output = coll.aggregate(pipeline);
			output.allowDiskUse(true);
			MongoCursor<Document> cursor = output.iterator();

			while (cursor.hasNext()) {

				Document dbobj = cursor.next();
				String txndat = dbobj.getString(FieldType.CREATE_DATE.getName());
				String[] splitdate = txndat.split(" ");
				String date = splitdate[0].substring(0, 7);

				if (!StringUtils.isEmpty(date)) {

					if (dateChart.containsKey(date)) {
						PieChart piechart1 = dateChart.get(date);
						switch (dbobj.getString(FieldType.TXNTYPE.toString())) {
						case "SALE":

							if ((dbobj.getString(FieldType.TXNTYPE.toString()).equals(TransactionType.SALE.getName())
									&& dbobj.getString(FieldType.STATUS.toString())
											.equals(StatusType.CAPTURED.getName()))
									|| (dbobj.getString(FieldType.TXNTYPE.toString())
											.equals(TransactionType.AUTHORISE.getName())
											&& dbobj.getString(FieldType.STATUS.toString())
													.equals(StatusType.APPROVED.getName()))) {
								String successTxn = piechart1.getTotalSuccess();
								if (successTxn == null) {

									totalSuccess = 1;
									piechart1.setTotalSuccess(String.valueOf(totalSuccess));
									dateChart.put(date, piechart1);
								} else {
									totalSuccess = Integer.parseInt(piechart1.getTotalSuccess());
									totalSuccess++;
									piechart1.setTotalSuccess(String.valueOf(totalSuccess));

									dateChart.put(date, piechart1);
								}

							} else if ((dbobj.getString(FieldType.TXNTYPE.toString())
									.equals(TransactionType.SALE.getName())
									&& failedstatusLst.contains(dbobj.getString(FieldType.STATUS.toString())))
									|| (dbobj.getString(FieldType.TXNTYPE.toString())
											.equals(TransactionType.AUTHORISE.getName())
											&& failedstatusLst.contains(dbobj.getString(FieldType.STATUS.toString())))) {
								String failTxn = piechart1.getTotalFailed();
								if (failTxn == null) {
									totalFailed = 1;
									piechart1.setTotalFailed(String.valueOf(totalFailed));
									piechart1.setTxndate(date);
									dateChart.put(date, piechart1);
								} else {
									totalFailed = Integer.parseInt(piechart1.getTotalFailed());
									totalFailed++;
									piechart1.setTotalFailed(String.valueOf(totalFailed));
									piechart1.setTxndate(date);

									dateChart.put(date, piechart1);
								}

							}

							else if((dbobj.getString(FieldType.TXNTYPE.toString())
									.equals(TransactionType.SALE.getName())
									&& dbobj.getString(FieldType.STATUS.toString()).equals(StatusType.ERROR.getName()))){
								String errorTxn = piechart1.getTotalErrors();
								if (errorTxn == null) {
									errors = 1;
									piechart1.setTotalErrors(String.valueOf(errors));
									dateChart.put(date, piechart1);
								} else {
									errors = Integer.parseInt(piechart1.getTotalErrors());
									errors++;
									piechart1.setTotalErrors(String.valueOf(errors));
									piechart1.setTxndate(date);
									dateChart.put(date, piechart1);
								}
							}else if((dbobj.getString(FieldType.TXNTYPE.toString())
									.equals(TransactionType.SALE.getName())
									&& dbobj.getString(FieldType.STATUS.toString()).equals(StatusType.TIMEOUT.getName()))
									|| (dbobj.getString(FieldType.TXNTYPE.toString())
											.equals(TransactionType.SALE.getName())
											&& dbobj.getString(FieldType.STATUS.toString())
													.equals(StatusType.ACQUIRER_TIMEOUT.getName()))) {
								String timeoutTxn = piechart1.getTotalTimeouts();
								if (timeoutTxn == null) {

									timeout = 1;
									piechart1.setTotalTimeouts(String.valueOf(timeout));
									dateChart.put(date, piechart1);
								} else {
									timeout = Integer.parseInt(piechart1.getTotalTimeouts());
									timeout++;
									piechart1.setTotalTimeouts(String.valueOf(timeout));
									piechart1.setTxndate(date);
									
									dateChart.put(date, piechart1);
								}
							}
							else {
								// Do Nothing
							}
							break;
						case "AUTHORISE":
							if((dbobj.getString(FieldType.STATUS.toString()).equals(StatusType.ERROR.getName()))){
								String errorTxn = piechart1.getTotalErrors();
								if (errorTxn == null) {
									errors = 1;
									piechart1.setTotalErrors(String.valueOf(errors));
									dateChart.put(date, piechart1);
								} else {
									errors = Integer.parseInt(piechart1.getTotalErrors());
									errors++;
									piechart1.setTotalErrors(String.valueOf(errors));
									piechart1.setTxndate(date);
									dateChart.put(date, piechart1);
								}
							}else if((dbobj.getString(FieldType.STATUS.toString()).equals(StatusType.CAPTURED.getName()))){
								String successTxn = piechart1.getTotalSuccess();
								if (successTxn == null) {

									totalSuccess = 1;
									piechart1.setTotalSuccess(String.valueOf(totalSuccess));
									dateChart.put(date, piechart1);
								} else {
									totalSuccess = Integer.parseInt(piechart1.getTotalSuccess());
									totalSuccess++;
									piechart1.setTotalSuccess(String.valueOf(totalSuccess));

									dateChart.put(date, piechart1);
								}
							}else if (failedstatusLst.contains(dbobj.getString(FieldType.STATUS.toString()))) {
								String failTxn = piechart1.getTotalFailed();
								if (failTxn == null) {
									totalFailed = 1;
									piechart1.setTotalFailed(String.valueOf(totalFailed));
									piechart1.setTxndate(date);
									dateChart.put(date, piechart1);
								} else {
									totalFailed = Integer.parseInt(piechart1.getTotalFailed());
									totalFailed++;
									piechart1.setTotalFailed(String.valueOf(totalFailed));
									piechart1.setTxndate(date);

									dateChart.put(date, piechart1);
								}

							}else {
								
							}
							break;	
						case "ENROLL":
							if((dbobj.getString(FieldType.TXNTYPE.toString())
									.equals(TransactionType.ENROLL.getName())
									&& dbobj.getString(FieldType.STATUS.toString()).equals(StatusType.ERROR.getName()))){
								String errorTxn = piechart1.getTotalErrors();
								if (errorTxn == null) {
									errors = 1;
									piechart1.setTotalErrors(String.valueOf(errors));
									dateChart.put(date, piechart1);
								} else {
									errors = Integer.parseInt(piechart1.getTotalErrors());
									errors++;
									piechart1.setTotalErrors(String.valueOf(errors));
									piechart1.setTxndate(date);
									dateChart.put(date, piechart1);
								}
							}else if (failedstatusLst.contains(dbobj.getString(FieldType.STATUS.toString()))) {
								String failTxn = piechart1.getTotalFailed();
								if (failTxn == null) {
									totalFailed = 1;
									piechart1.setTotalFailed(String.valueOf(totalFailed));
									piechart1.setTxndate(date);
									dateChart.put(date, piechart1);
								} else {
									totalFailed = Integer.parseInt(piechart1.getTotalFailed());
									totalFailed++;
									piechart1.setTotalFailed(String.valueOf(totalFailed));
									piechart1.setTxndate(date);

									dateChart.put(date, piechart1);
								}

							}else {
								
							}
							break;
						case "REFUND":
							if (dbobj.getString(FieldType.STATUS.toString()).equals(StatusType.CAPTURED.getName())
									&& dbobj.getString(FieldType.TXNTYPE.toString())
											.equals(TransactionType.REFUND.getName())) {

								String refundTxn = piechart1.getTotalRefunded();
								if (refundTxn == null) {
									totalRefund = 1;
									piechart1.setTotalRefunded(String.valueOf(totalRefund));
									dateChart.put(date, piechart1);
								} else {
									totalRefund = Integer.parseInt(piechart1.getTotalRefunded());
									totalRefund++;
									piechart1.setTotalRefunded(String.valueOf(totalRefund));

									dateChart.put(date, piechart1);
								}

							} else if ((dbobj.getString(FieldType.TXNTYPE.toString())
									.equals(TransactionType.REFUND.getName())
									&& failedstatusLst.contains(dbobj.getString(FieldType.STATUS.toString())))) {
								String failTxn = piechart1.getTotalFailed();
								if (failTxn == null) {
									totalFailed = 1;	
									piechart1.setTotalFailed(String.valueOf(totalFailed));
									piechart1.setTxndate(date);
									dateChart.put(date, piechart1);
								} else {
									totalFailed = Integer.parseInt(piechart1.getTotalFailed());
									totalFailed++;
									piechart1.setTotalFailed(String.valueOf(totalFailed));
									piechart1.setTxndate(date);

									dateChart.put(date, piechart1);
								}

							}
							else if((dbobj.getString(FieldType.TXNTYPE.toString())
									.equals(TransactionType.REFUND.getName())
									&& dbobj.getString(FieldType.STATUS.toString()).equals(StatusType.ERROR.getName()))){
								String errorTxn = piechart1.getTotalErrors();
								if (errorTxn == null) {
									errors = 1;
									piechart1.setTotalErrors(String.valueOf(errors));
									dateChart.put(date, piechart1);
								} else {
									errors = Integer.parseInt(piechart1.getTotalErrors());
									errors++;
									piechart1.setTotalErrors(String.valueOf(errors));
									piechart1.setTxndate(date);
									
									dateChart.put(date, piechart1);
								}
							}else if((dbobj.getString(FieldType.TXNTYPE.toString())
									.equals(TransactionType.REFUND.getName())
									&& dbobj.getString(FieldType.STATUS.toString()).equals(StatusType.TIMEOUT.getName()))
									|| (dbobj.getString(FieldType.TXNTYPE.toString())
											.equals(TransactionType.REFUND.getName())
											&& dbobj.getString(FieldType.STATUS.toString())
													.equals(StatusType.ACQUIRER_TIMEOUT.getName()))) {
								String timeoutTxn = piechart1.getTotalTimeouts();
								if (timeoutTxn == null) {

									timeout = 1;
									piechart1.setTotalTimeouts(String.valueOf(timeout));
									dateChart.put(date, piechart1);
								} else {
									timeout = Integer.parseInt(piechart1.getTotalTimeouts());
									timeout++;
									piechart1.setTotalTimeouts(String.valueOf(timeout));
									piechart1.setTxndate(date);
									
									dateChart.put(date, piechart1);
								}
							}
							
							else {
								// Do Nothing
							}

							break;

						}

					} else {
						PieChart piechart1 = new PieChart();
						switch (dbobj.getString(FieldType.TXNTYPE.toString())) {
						case "SALE":
							if ((dbobj.getString(FieldType.TXNTYPE.toString()).equals(TransactionType.SALE.getName())
									&& dbobj.getString(FieldType.STATUS.toString())
											.equals(StatusType.CAPTURED.getName()))
									|| (dbobj.getString(FieldType.TXNTYPE.toString())
											.equals(TransactionType.AUTHORISE.getName())
											&& dbobj.getString(FieldType.STATUS.toString())
													.equals(StatusType.APPROVED.getName()))) {
								totalSuccess = 1;
								piechart1.setTotalSuccess(String.valueOf(totalSuccess));
								piechart1.setTotalFailed(String.valueOf(0));
								piechart1.setTotalRefunded(String.valueOf(0));
								piechart1.setTxndate(date);

								dateChart.put(date, piechart1);

							} else if ((dbobj.getString(FieldType.TXNTYPE.toString())
									.equals(TransactionType.SALE.getName())
									&& failedstatusLst.contains(dbobj.getString(FieldType.STATUS.toString())))
									|| (dbobj.getString(FieldType.TXNTYPE.toString())
											.equals(TransactionType.AUTHORISE.getName())
											&& failedstatusLst.contains(dbobj.getString(FieldType.STATUS.toString())))) {
								String failTxn = piechart1.getTotalFailed();
								if (failTxn == null) {
									totalFailed = 1;
									piechart1.setTotalFailed(String.valueOf(totalFailed));
									piechart1.setTxndate(date);
									dateChart.put(date, piechart1);
								} else {
									totalFailed = Integer.parseInt(piechart1.getTotalFailed());
									totalFailed++;
									piechart1.setTotalFailed(String.valueOf(totalFailed));
									piechart1.setTxndate(date);

									dateChart.put(date, piechart1);
								}

							}	else if((dbobj.getString(FieldType.TXNTYPE.toString())
									.equals(TransactionType.SALE.getName())
									&& dbobj.getString(FieldType.STATUS.toString()).equals(StatusType.ERROR.getName()))){
								String errorTxn = piechart1.getTotalErrors();
								if (errorTxn == null) {
									errors = 1;
									piechart1.setTotalErrors(String.valueOf(errors));
									dateChart.put(date, piechart1);
								} else {
									errors = Integer.parseInt(piechart1.getTotalErrors());
									errors++;
									piechart1.setTotalErrors(String.valueOf(errors));
									piechart1.setTxndate(date);
									dateChart.put(date, piechart1);
								}
							}else if((dbobj.getString(FieldType.TXNTYPE.toString())
									.equals(TransactionType.SALE.getName())
									&& dbobj.getString(FieldType.STATUS.toString()).equals(StatusType.TIMEOUT.getName()))
									|| (dbobj.getString(FieldType.TXNTYPE.toString())
											.equals(TransactionType.SALE.getName())
											&& dbobj.getString(FieldType.STATUS.toString())
													.equals(StatusType.ACQUIRER_TIMEOUT.getName()))) {
								String timeoutTxn = piechart1.getTotalTimeouts();
								if (timeoutTxn == null) {
									timeout = 1;	
									piechart1.setTotalTimeouts(String.valueOf(timeout));
									dateChart.put(date, piechart1);
								} else {
									timeout = Integer.parseInt(piechart1.getTotalTimeouts());
									timeout++;
									piechart1.setTotalTimeouts(String.valueOf(timeout));
									piechart1.setTxndate(date);
									dateChart.put(date, piechart1);
								}
							}

							else {
								// Do Nothing
							}
							break;
						case "AUTHORISE":
							if((dbobj.getString(FieldType.STATUS.toString()).equals(StatusType.ERROR.getName()))){
								String errorTxn = piechart1.getTotalErrors();
								if (errorTxn == null) {
									errors = 1;
									piechart1.setTotalErrors(String.valueOf(errors));
									dateChart.put(date, piechart1);
								} else {
									errors = Integer.parseInt(piechart1.getTotalErrors());
									errors++;
									piechart1.setTotalErrors(String.valueOf(errors));
									piechart1.setTxndate(date);
									dateChart.put(date, piechart1);
								}
							}else if((dbobj.getString(FieldType.STATUS.toString()).equals(StatusType.CAPTURED.getName()))){
								String successTxn = piechart1.getTotalSuccess();
								if (successTxn == null) {

									totalSuccess = 1;
									piechart1.setTotalSuccess(String.valueOf(totalSuccess));
									dateChart.put(date, piechart1);
								} else {
									totalSuccess = Integer.parseInt(piechart1.getTotalSuccess());
									totalSuccess++;
									piechart1.setTotalSuccess(String.valueOf(totalSuccess));

									dateChart.put(date, piechart1);
								}
							}else if (failedstatusLst.contains(dbobj.getString(FieldType.STATUS.toString()))) {
								String failTxn = piechart1.getTotalFailed();
								if (failTxn == null) {
									totalFailed = 1;
									piechart1.setTotalFailed(String.valueOf(totalFailed));
									piechart1.setTxndate(date);
									dateChart.put(date, piechart1);
								} else {
									totalFailed = Integer.parseInt(piechart1.getTotalFailed());
									totalFailed++;
									piechart1.setTotalFailed(String.valueOf(totalFailed));
									piechart1.setTxndate(date);

									dateChart.put(date, piechart1);
								}

							}else {
								
							}
							break;	
						case "ENROLL":
							if((dbobj.getString(FieldType.TXNTYPE.toString())
									.equals(TransactionType.ENROLL.getName())
									&& dbobj.getString(FieldType.STATUS.toString()).equals(StatusType.ERROR.getName()))){
								String errorTxn = piechart1.getTotalErrors();
								if (errorTxn == null) {
									errors = 1;
									piechart1.setTotalErrors(String.valueOf(errors));
									dateChart.put(date, piechart1);
								} else {
									errors = Integer.parseInt(piechart1.getTotalErrors());
									errors++;
									piechart1.setTotalErrors(String.valueOf(errors));
									piechart1.setTxndate(date);
									dateChart.put(date, piechart1);
								}
							}else if (failedstatusLst.contains(dbobj.getString(FieldType.STATUS.toString()))) {
								String failTxn = piechart1.getTotalFailed();
								if (failTxn == null) {
									totalFailed = 1;
									piechart1.setTotalFailed(String.valueOf(totalFailed));
									piechart1.setTxndate(date);
									dateChart.put(date, piechart1);
								} else {
									totalFailed = Integer.parseInt(piechart1.getTotalFailed());
									totalFailed++;
									piechart1.setTotalFailed(String.valueOf(totalFailed));
									piechart1.setTxndate(date);

									dateChart.put(date, piechart1);
								}

							}else {
								
							}
							break;
						case "REFUND":

							if (dbobj.getString(FieldType.STATUS.toString()).equals(StatusType.CAPTURED.getName())
									&& dbobj.getString(FieldType.TXNTYPE.toString())
											.equals(TransactionType.REFUND.getName())) {
								totalRefund = 1;
								piechart1.setTotalRefunded(String.valueOf(totalRefund));
								piechart1.setTotalFailed(String.valueOf(0));
								piechart1.setTotalSuccess(String.valueOf(0));
								piechart1.setTxndate(date);
								dateChart.put(date, piechart1);
							} else if ((dbobj.getString(FieldType.TXNTYPE.toString())
									.equals(TransactionType.REFUND.getName())
									&& failedstatusLst.contains(dbobj.getString(FieldType.STATUS.toString())))) {
								String failTxn = piechart1.getTotalFailed();
								if (failTxn == null) {
									totalFailed = 1;
									piechart1.setTotalFailed(String.valueOf(totalFailed));
									piechart1.setTxndate(date);
									dateChart.put(date, piechart1);
								} else {
									totalFailed = Integer.parseInt(piechart1.getTotalFailed());
									totalFailed++;
									piechart1.setTotalFailed(String.valueOf(totalFailed));
									piechart1.setTxndate(date);

									dateChart.put(date, piechart1);
								}

							}	else if((dbobj.getString(FieldType.TXNTYPE.toString())
									.equals(TransactionType.REFUND.getName())
									&& dbobj.getString(FieldType.STATUS.toString()).equals(StatusType.ERROR.getName()))){
								String errorTxn = piechart1.getTotalErrors();
								if (errorTxn == null) {
									errors = 1;
									piechart1.setTotalErrors(String.valueOf(errors));
									dateChart.put(date, piechart1);
								} else {
									errors = Integer.parseInt(piechart1.getTotalErrors());
									errors++;
									piechart1.setTotalErrors(String.valueOf(errors));
									piechart1.setTxndate(date);
									dateChart.put(date, piechart1);
								}
							}else if((dbobj.getString(FieldType.TXNTYPE.toString())
									.equals(TransactionType.REFUND.getName())
									&& dbobj.getString(FieldType.STATUS.toString()).equals(StatusType.TIMEOUT.getName()))
									|| (dbobj.getString(FieldType.TXNTYPE.toString())
											.equals(TransactionType.REFUND.getName())
											&& dbobj.getString(FieldType.STATUS.toString())
													.equals(StatusType.ACQUIRER_TIMEOUT.getName()))) {
								String timeoutTxn = piechart1.getTotalTimeouts();
								if (timeoutTxn == null) {
									timeout = 1;
									piechart1.setTotalTimeouts(String.valueOf(timeout));
									dateChart.put(date, piechart1);
								} else {
									timeout = Integer.parseInt(piechart1.getTotalTimeouts());
									timeout++;
									piechart1.setTotalTimeouts(String.valueOf(timeout));
									piechart1.setTxndate(date);
									dateChart.put(date, piechart1);
								}
							}

							else {
								// Do Nothing
							}

							break;

						}
					}

				}

			}

			cursor.close();
		} catch (Exception exception) {
			logger.error("Exception", exception);

		}
		return dateChart;
	}

	public Map<String, PieChart> totalHourlyTransactionRecord(String payId, String currency, String dateFrom, String dateTo , User user,String paymentType,String acquirer,String transactionType,String mopType)
			throws SystemException {
		
		Map<String, PieChart> dateChart = new HashMap<String, PieChart>();

		int totalSuccess = 0;
		int totalFailed = 0;
		int totalRefund = 0;
		int totalCancelled = 0;

		BasicDBObject txnTypeQuery = new BasicDBObject();
		List<BasicDBObject> statusConditionLst = new ArrayList<BasicDBObject>();
		List<BasicDBObject> allConditionQueryList = new ArrayList<BasicDBObject>();
		List<BasicDBObject> txnTypeConditionLst = new ArrayList<BasicDBObject>();
		List<String> failedstatusLst = new ArrayList<String>();
		
		BasicDBObject dateQuery = new BasicDBObject();
		BasicDBObject statusQuery = new BasicDBObject();
		BasicDBObject payIdquery = new BasicDBObject();
		BasicDBObject resellerIdquery = new BasicDBObject();
		
		BasicDBObject acquirerQuery = new BasicDBObject();
		List<BasicDBObject> acquirerConditionLst = new ArrayList<BasicDBObject>();
		List<BasicDBObject> mopTypeConditionLst = new ArrayList<BasicDBObject>();
		BasicDBObject mopTypeQuery = new BasicDBObject();
		List<BasicDBObject> paymentTypeConditionLst = new ArrayList<BasicDBObject>();
		BasicDBObject paymentTypeQuery = new BasicDBObject();
		
		MongoDatabase dbIns = mongoInstance.getDB();
		MongoCollection<Document> coll = dbIns
				.getCollection(PropertiesManager.propertiesMap.get(prefix + Constants.TRANSACTION_STATUS_COLLECTION.getValue()));

		MongoCollection<Document> dashboardColl = dbIns
				.getCollection(PropertiesManager.propertiesMap.get(prefix + Constants.DASHBOARD_POPULATOR_NAME.getValue()));
		
		
		
		try {

			String currentDate = null;
			if (!dateFrom.isEmpty()) {

				if (!dateTo.isEmpty()) {
					currentDate = dateTo;
				} else {
					DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
					Calendar cal = Calendar.getInstance();
					currentDate = dateFormat.format(cal.getTime());
				}

				dateQuery.put(FieldType.CREATE_DATE.getName(),
						BasicDBObjectBuilder.start("$gte", new SimpleDateFormat(dateFrom).toLocalizedPattern())
								.add("$lte", new SimpleDateFormat(currentDate).toLocalizedPattern()).get());
			}

			List<BasicDBObject> dateIndexConditionList = new ArrayList<BasicDBObject>();
			BasicDBObject dateIndexConditionQuery = new BasicDBObject();
			String startString = new SimpleDateFormat(dateFrom).toLocalizedPattern();
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
				
				// Verify if data already present in dashboard populator collection
				
				BasicDBObject dateIndexQuery = new BasicDBObject(FieldType.CREATE_DATE.getName(), dateIndex);
				BasicDBObject payIdQuery = null;
				BasicDBObject resellerIdQuery = new BasicDBObject();
				
				/*
				 * if (payId.equalsIgnoreCase("ALL MERCHANTS")) { payIdQuery = new
				 * BasicDBObject(FieldType.PAY_ID.getName(), "ALL"); } else { payIdQuery = new
				 * BasicDBObject(FieldType.PAY_ID.getName(), payId); }
				 */
				
				if (payId.equalsIgnoreCase("ALL MERCHANTS")) {
					List<String> payIdLst = userDao.getPayIdForSplitPaymentMerchant(user.getSegment());
					logger.info("Get PayId For SplitPayment Merchant : " + payIdLst);
					if (payIdLst.size() > 0) {
						payIdquery.put(FieldType.PAY_ID.getName(), new BasicDBObject("$in", payIdLst));
					}
				} else {
					payIdquery.put(FieldType.PAY_ID.getName(), payId);
				}
				
				if (user.getUserType().equals(UserType.RESELLER)) {
					resellerIdQuery = new BasicDBObject(FieldType.RESELLER_ID.getName(), user.getResellerId());
				}
				
				
				List<BasicDBObject> dashBoardCondQueryList = new ArrayList<BasicDBObject>();
				
				dashBoardCondQueryList.add(dateIndexQuery);
				dashBoardCondQueryList.add(payIdQuery);
				
				if (!resellerIdQuery.isEmpty()) {
					dashBoardCondQueryList.add(resellerIdQuery);
				}
				
				
				BasicDBObject dashBoardQuery = new BasicDBObject("$and", dashBoardCondQueryList);
				
				long dataCount = dashboardColl.count(dashBoardQuery);
				
				// Data is not present in dashboard populator collection
				if (dataCount < 1) {
					dateIndexConditionList.add(new BasicDBObject("DATE_INDEX", dateIndex));
				}
				
				// Data found in dashboard populator collection
				else {
					
					FindIterable<Document> dashBoardobject = dashboardColl.find(dashBoardQuery);
					MongoCursor<Document> cursor = dashBoardobject.iterator();
					
					while(cursor.hasNext()) {
						
						Document doc = cursor.next();
						
						String createDate = doc.getString(FieldType.CREATE_DATE.getName());
						String year = createDate.substring(0,4);
						String month = createDate.substring(4,6);
						String date = createDate.substring(6,8);
						
						String createDateModified = year+"-"+month+"-"+date; 
						String saleCount = "0";
						String refundCount = "0";
						String failedCount = "0";
						if (user.getUserType().equals(UserType.ADMIN) || user.getUserType().equals(UserType.SUBADMIN) ) {
							
							saleCount = doc.getString(FieldType.DASHBOARD_SALE_COUNT.getName());
							refundCount = doc.getString(FieldType.DASHBOARD_REFUND_COUNT.getName());
							failedCount = doc.getString(FieldType.DASHBOARD_FAILED_COUNT.getName());
						}
						
						else {
							saleCount = doc.getString(FieldType.DASHBOARD_SALE_COUNT.getName());
							refundCount = doc.getString(FieldType.DASHBOARD_REFUND_COUNT.getName());
							failedCount = doc.getString(FieldType.DASHBOARD_FAILED_COUNT.getName());
							
						}
						
						PieChart pieChart = new PieChart();
						pieChart.setTotalSuccess(saleCount);
						pieChart.setTotalRefunded(refundCount);
						pieChart.setTotalFailed(failedCount);
						pieChart.setTxndate(createDateModified);
						
						dateChart.put(createDateModified, pieChart);
						break;
					}
					
					
					
				}
				
				
			}
			if (StringUtils.isNotBlank(PropertiesManager.propertiesMap.get(Constants.USE_DATE_INDEX.getValue()))
					&& PropertiesManager.propertiesMap.get(Constants.USE_DATE_INDEX.getValue()).equalsIgnoreCase("Y")) {
				
				if (dateIndexConditionList.size() > 0) {
					dateIndexConditionQuery.append("$or", dateIndexConditionList);
				}
				
			}

			/*
			 * if (!payId.equalsIgnoreCase("ALL MERCHANTS")) {
			 * payIdquery.put(FieldType.PAY_ID.getName(), payId);
			 * 
			 * }
			 */
			
			if (payId.equalsIgnoreCase("ALL MERCHANTS")) {
				List<String> payIdLst = userDao.getPayIdForSplitPaymentMerchant(user.getSegment());
				logger.info("Get PayId For SplitPayment Merchant : " + payIdLst);
				if (payIdLst.size() > 0) {
					payIdquery.put(FieldType.PAY_ID.getName(), new BasicDBObject("$in", payIdLst));
				}
			} else {
				payIdquery.put(FieldType.PAY_ID.getName(), payId);
			}
			
			if (user.getUserType().equals(UserType.RESELLER)) {
				resellerIdquery.put(FieldType.RESELLER_ID.getName(), user.getResellerId());

			}

			BasicDBObject query = new BasicDBObject();
			query.put(FieldType.CURRENCY_CODE.getName(), currency);
			//txnTypeConditionLst.add(new BasicDBObject(FieldType.ORIG_TXNTYPE.getName(), TransactionType.SALE.getName()));
			//txnTypeConditionLst.add(new BasicDBObject(FieldType.ORIG_TXNTYPE.getName(), TransactionType.REFUND.getName()));
		
			//txnTypeQuery.append("$or", txnTypeConditionLst);
			statusConditionLst.add(new BasicDBObject(FieldType.ALIAS_STATUS.getName(), StatusType.CAPTURED.getName()));
			statusConditionLst.add(new BasicDBObject(FieldType.ALIAS_STATUS.getName(), StatusType.SETTLED_SETTLE.getName()));
			statusConditionLst.add(new BasicDBObject(FieldType.ALIAS_STATUS.getName(), StatusType.FAILED.getName()));
			statusConditionLst.add(new BasicDBObject(FieldType.ALIAS_STATUS.getName(), StatusType.CANCELLED.getName()));

			statusQuery.append("$or", statusConditionLst);

			if (!transactionType.equalsIgnoreCase("ALL")) {
				allConditionQueryList.add(new BasicDBObject(FieldType.ORIG_TXNTYPE.getName(), transactionType));
			}
					
			if (!acquirer.equalsIgnoreCase("ALL")) {
				List<String> acquirerList = Arrays.asList(acquirer.split(","));
				for (String acq : acquirerList) {
					acquirerConditionLst.add(new BasicDBObject(FieldType.ACQUIRER_TYPE.getName(), acq.trim()));
				}
				acquirerQuery.append("$or", acquirerConditionLst);

			}

			if (!mopType.equalsIgnoreCase("ALL")) {
				List<String> mopTypeList = Arrays.asList(mopType.split(","));
				for (String mop : mopTypeList) {
					if (mop.equalsIgnoreCase("Others")) {
							List<String> mopTypeOtherList = Arrays.asList(MopType.getOTherTypeCodes().split(","));
								;
							for (String mopOther : mopTypeOtherList) {
									mopTypeConditionLst.add(new BasicDBObject(FieldType.MOP_TYPE.getName(), mopOther.trim()));
							}
					} else {
							String mopCode = MopTypeUI.getmopCodeByUiName(mop);
							mopTypeConditionLst.add(new BasicDBObject(FieldType.MOP_TYPE.getName(), mopCode));
					}

				}
				mopTypeQuery.append("$or", mopTypeConditionLst);
			}

			if (!paymentType.equalsIgnoreCase("ALL")) {
				List<String> paymentTypeList = Arrays.asList(paymentType.split(","));
				for (String payment : paymentTypeList) {
					paymentTypeConditionLst.add(new BasicDBObject(FieldType.PAYMENT_TYPE.getName(), payment.trim()));
				}
				paymentTypeQuery.append("$or", paymentTypeConditionLst);
			}
			
			if (!query.isEmpty()) {
				allConditionQueryList.add(query);
			}
			if (!dateQuery.isEmpty()) {
				allConditionQueryList.add(dateQuery);
			}
			if (!txnTypeQuery.isEmpty()) {
				allConditionQueryList.add(txnTypeQuery);
			}
			if (!statusQuery.isEmpty()) {
				allConditionQueryList.add(statusQuery);
			}
			if (!payIdquery.isEmpty()) {
				allConditionQueryList.add(payIdquery);
			}
			if (!resellerIdquery.isEmpty()) {
				allConditionQueryList.add(resellerIdquery);
			}
			
			if (!dateIndexConditionQuery.isEmpty()) {
				allConditionQueryList.add(dateIndexConditionQuery);
			}

			if (!acquirerQuery.isEmpty()) {
				allConditionQueryList.add(acquirerQuery);
			}

			if (!mopTypeQuery.isEmpty()) {
				allConditionQueryList.add(mopTypeQuery);
			}
			if (!paymentTypeQuery.isEmpty()) {
				allConditionQueryList.add(paymentTypeQuery);
			}
			
			BasicDBObject allCondi = new BasicDBObject("$and", allConditionQueryList);
			
			BasicDBObject match = new BasicDBObject("$match", allCondi);

			BasicDBObject projectElement = new BasicDBObject();
			projectElement.put(FieldType.CREATE_DATE.toString(), 1);
			projectElement.put(FieldType.ALIAS_STATUS.toString(), 1);
			projectElement.put(FieldType.ORIG_TXNTYPE.toString(), 1);
			BasicDBObject project = new BasicDBObject("$project", projectElement);

			List<BasicDBObject> pipeline = Arrays.asList(match, project);
			AggregateIterable<Document> output = coll.aggregate(pipeline);
			output.allowDiskUse(true);
			MongoCursor<Document> cursor = output.iterator();

			while (cursor.hasNext()) {

				Document dbobj = cursor.next();
				String txndat = dbobj.getString(FieldType.CREATE_DATE.getName());
				String[] splitdate = txndat.split(" ");
				String date = splitdate[1].substring(0, 2);

				if (!StringUtils.isEmpty(date)) {

					if (dateChart.containsKey(date)) {
						PieChart piechart1 = dateChart.get(date);
						switch (dbobj.getString(FieldType.ORIG_TXNTYPE.toString())) {
						case "SALE":

							if (dbobj.getString(FieldType.ALIAS_STATUS.toString()).equals(StatusType.CAPTURED.getName()) ||
									dbobj.getString(FieldType.ALIAS_STATUS.toString()).equals(StatusType.SETTLED_SETTLE.getName())) {
								String successTxn = piechart1.getTotalSuccess();
								if (successTxn == null) {
									totalSuccess = 1;
									piechart1.setTotalSuccess(String.valueOf(totalSuccess));
									dateChart.put(date, piechart1);
								} else {
									totalSuccess = Integer.parseInt(piechart1.getTotalSuccess());
									totalSuccess++;
									piechart1.setTotalSuccess(String.valueOf(totalSuccess));

									dateChart.put(date, piechart1);
								}

							} else if (dbobj.getString(FieldType.ALIAS_STATUS.toString()).equals(StatusType.FAILED.getName())){
								String failTxn = piechart1.getTotalFailed();
								if (failTxn == null) {
									totalFailed = 1;
									piechart1.setTotalFailed(String.valueOf(totalFailed));
									piechart1.setTxndate(date);
									dateChart.put(date, piechart1);
								} else {
									totalFailed = Integer.parseInt(piechart1.getTotalFailed());
									totalFailed++;
									piechart1.setTotalFailed(String.valueOf(totalFailed));
									piechart1.setTxndate(date);

									dateChart.put(date, piechart1);
								}

							} else if (dbobj.getString(FieldType.ALIAS_STATUS.toString()).equals(StatusType.CANCELLED.getName())){
								String cancelTxn = piechart1.getTotalCancelled();
								if (cancelTxn == null) {
									totalCancelled = 1;
									piechart1.setTotalCancelled(String.valueOf(totalCancelled));
									piechart1.setTxndate(date);
									dateChart.put(date, piechart1);
								} else {
									totalCancelled = Integer.parseInt(piechart1.getTotalCancelled());
									totalCancelled++;
									piechart1.setTotalCancelled(String.valueOf(totalCancelled));
									piechart1.setTxndate(date);

									dateChart.put(date, piechart1);
								}

							}
							else {
								// Do Nothing
							}
							break;
						case "REFUND":
							if (dbobj.getString(FieldType.ALIAS_STATUS.toString()).equals(StatusType.CAPTURED.getName()) ||
									dbobj.getString(FieldType.ALIAS_STATUS.toString()).equals(StatusType.SETTLED_SETTLE.getName())) {

								String refundTxn = piechart1.getTotalRefunded();
								if (refundTxn == null) {
									totalRefund = 1;
									piechart1.setTotalRefunded(String.valueOf(totalRefund));
									dateChart.put(date, piechart1);
								} else {
									totalRefund = Integer.parseInt(piechart1.getTotalRefunded());
									totalRefund++;
									piechart1.setTotalRefunded(String.valueOf(totalRefund));

									dateChart.put(date, piechart1);
								}

							} else if (dbobj.getString(FieldType.ALIAS_STATUS.toString()).equals(StatusType.FAILED.getName())){
								String failTxn = piechart1.getTotalFailed();
								if (failTxn == null) {
									totalFailed = 1;
									piechart1.setTotalFailed(String.valueOf(totalFailed));
									piechart1.setTxndate(date);
									dateChart.put(date, piechart1);
								} else {
									totalFailed = Integer.parseInt(piechart1.getTotalFailed());
									totalFailed++;
									piechart1.setTotalFailed(String.valueOf(totalFailed));
									piechart1.setTxndate(date);

									dateChart.put(date, piechart1);
								}

							}else if (dbobj.getString(FieldType.ALIAS_STATUS.toString()).equals(StatusType.CANCELLED.getName())){
								String cancelTxn = piechart1.getTotalCancelled();
								if (cancelTxn == null) {
									totalCancelled = 1;
									piechart1.setTotalCancelled(String.valueOf(totalCancelled));
									piechart1.setTxndate(date);
									dateChart.put(date, piechart1);
								} else {
									totalCancelled = Integer.parseInt(piechart1.getTotalCancelled());
									totalCancelled++;
									piechart1.setTotalCancelled(String.valueOf(totalCancelled));
									piechart1.setTxndate(date);

									dateChart.put(date, piechart1);
								}

							}							
							else {
								// Do Nothing
							}

							break;

						}

					} else {
						PieChart piechart1 = new PieChart();
						switch (dbobj.getString(FieldType.ORIG_TXNTYPE.toString())) {
						case "SALE":
							if (dbobj.getString(FieldType.ALIAS_STATUS.toString()).equals(StatusType.CAPTURED.getName()) ||
									dbobj.getString(FieldType.ALIAS_STATUS.toString()).equals(StatusType.SETTLED_SETTLE.getName())) {
								totalSuccess = 1;
								piechart1.setTotalSuccess(String.valueOf(totalSuccess));
								piechart1.setTotalFailed(String.valueOf(0));
								piechart1.setTotalRefunded(String.valueOf(0));
								piechart1.setTxndate(date);

								dateChart.put(date, piechart1);

							} else if (dbobj.getString(FieldType.ALIAS_STATUS.toString()).equals(StatusType.FAILED.getName())){
								String failTxn = piechart1.getTotalFailed();
								if (failTxn == null) {
									totalFailed = 1;
									piechart1.setTotalFailed(String.valueOf(totalFailed));
									piechart1.setTxndate(date);
									dateChart.put(date, piechart1);
								} else {
									totalFailed = Integer.parseInt(piechart1.getTotalFailed());
									totalFailed++;
									piechart1.setTotalFailed(String.valueOf(totalFailed));
									piechart1.setTxndate(date);

									dateChart.put(date, piechart1);
								}

							} else if (dbobj.getString(FieldType.ALIAS_STATUS.toString()).equals(StatusType.CANCELLED.getName())){
								String cancelTxn = piechart1.getTotalCancelled();
								if (cancelTxn == null) {
									totalCancelled = 1;
									piechart1.setTotalCancelled(String.valueOf(totalCancelled));
									piechart1.setTxndate(date);
									dateChart.put(date, piechart1);
								} else {
									totalCancelled = Integer.parseInt(piechart1.getTotalCancelled());
									totalCancelled++;
									piechart1.setTotalCancelled(String.valueOf(totalCancelled));
									piechart1.setTxndate(date);

									dateChart.put(date, piechart1);
								}

							}

							else {
								// Do Nothing
							}
							break;
						case "REFUND":

							if (dbobj.getString(FieldType.ALIAS_STATUS.toString()).equals(StatusType.CAPTURED.getName()) ||
									dbobj.getString(FieldType.ALIAS_STATUS.toString()).equals(StatusType.SETTLED_SETTLE.getName())) {
								totalRefund = 1;
								piechart1.setTotalRefunded(String.valueOf(totalRefund));
								piechart1.setTotalFailed(String.valueOf(0));
								piechart1.setTotalSuccess(String.valueOf(0));
								piechart1.setTxndate(date);
								dateChart.put(date, piechart1);
							}  else if (dbobj.getString(FieldType.ALIAS_STATUS.toString()).equals(StatusType.FAILED.getName())){
								String failTxn = piechart1.getTotalFailed();
								if (failTxn == null) {
									totalFailed = 1;
									piechart1.setTotalFailed(String.valueOf(totalFailed));
									piechart1.setTxndate(date);
									dateChart.put(date, piechart1);
								} else {
									totalFailed = Integer.parseInt(piechart1.getTotalFailed());
									totalFailed++;
									piechart1.setTotalFailed(String.valueOf(totalFailed));
									piechart1.setTxndate(date);

									dateChart.put(date, piechart1);
								}

							}else if (dbobj.getString(FieldType.ALIAS_STATUS.toString()).equals(StatusType.CANCELLED.getName())){
								String cancelTxn = piechart1.getTotalCancelled();
								if (cancelTxn == null) {
									totalCancelled = 1;
									piechart1.setTotalCancelled(String.valueOf(totalCancelled));
									piechart1.setTxndate(date);
									dateChart.put(date, piechart1);
								} else {
									totalCancelled = Integer.parseInt(piechart1.getTotalCancelled());
									totalCancelled++;
									piechart1.setTotalCancelled(String.valueOf(totalCancelled));
									piechart1.setTxndate(date);

									dateChart.put(date, piechart1);
								}

							}else {
								// Do Nothing
							}

							break;

						}
					}

				}

			}

			cursor.close();
		} catch (Exception exception) {
			logger.error("Exception", exception);

		}
		return dateChart;
	}


	public Statistics statisticsSummary(String payId, String currency, String fromDate, String toDate,
			UserType userType,User user) {
		System.out.println("Statistics query generation started ");
		logger.info("Statistics query generation started ");
		Statistics statistics = new Statistics();
		BasicDBObject dateQuery = new BasicDBObject();
		List<BasicDBObject> currencyConditionLst = new ArrayList<BasicDBObject>();
		List<BasicDBObject> saleOrAuthList = new ArrayList<BasicDBObject>();

		BasicDBObject currencyQuery = new BasicDBObject();

		BasicDBObject payIdquery = new BasicDBObject();
		BasicDBObject resellerIdquery = new BasicDBObject();
		toDate = toDate + " 23:59:59";
		fromDate = fromDate + " 00:00:00";
		
		try {
			
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

			long diff = dateEnd.getTime() - dateStart.getTime();
			 
			int diffDays = (int) (diff / (24 * 60 * 60 * 1000));
			
			LocalDate incrementingDate = dateStart.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
			LocalDate endDate = dateEnd.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();

			List<String> allDatesIndex = new ArrayList<>();

			if(diffDays != 365 && diffDays != 366) {
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
				
			}
			
			if (!payId.equalsIgnoreCase("ALL MERCHANTS")) {
				payIdquery.put(FieldType.PAY_ID.getName(), payId);
			}
			
			if (userType.equals(UserType.RESELLER)) {
				resellerIdquery.put(FieldType.RESELLER_ID.getName(), user.getResellerId());
			}
			
			
			if (!currency.equalsIgnoreCase("ALL")) {
				currencyQuery.put(FieldType.CURRENCY_CODE.getName(), currency);
			} else {
				PropertiesManager propertiesManager = new PropertiesManager();
				Map<String, String> allCurrencyMap;
				allCurrencyMap = propertiesManager.getAllProperties(alphabaticFileName);
				for (Map.Entry<String, String> entry : allCurrencyMap.entrySet()) {
					currencyConditionLst.add(new BasicDBObject(FieldType.CURRENCY_CODE.getName(), entry.getKey()));
				}
				currencyQuery.append("$or", currencyConditionLst);
			}
			List<BasicDBObject> saleConditionList = new ArrayList<BasicDBObject>();
			saleConditionList.add(new BasicDBObject(FieldType.ORIG_TXNTYPE.getName(), TransactionType.SALE.getName()));
			saleConditionList.add(new BasicDBObject(FieldType.ALIAS_STATUS.getName(), StatusType.CAPTURED.getName()));
			saleConditionList.add(new BasicDBObject(FieldType.RESPONSE_CODE.getName(), ErrorType.SUCCESS.getCode()));

			BasicDBObject saleConditionQuery = new BasicDBObject("$and", saleConditionList);

			saleOrAuthList.add(saleConditionQuery);

			List<BasicDBObject> refundConditionList = new ArrayList<BasicDBObject>();
			refundConditionList.add(new BasicDBObject(FieldType.ORIG_TXNTYPE.getName(), TransactionType.REFUND.getName()));
			refundConditionList.add(new BasicDBObject(FieldType.ALIAS_STATUS.getName(), StatusType.CAPTURED.getName()));

			BasicDBObject refundConditionQuery = new BasicDBObject("$and", refundConditionList);

			// FAIL QUERY START //

//			List<BasicDBObject> failConditionList = new ArrayList<BasicDBObject>();
//			List<BasicDBObject> failTypeConditionLst = new ArrayList<BasicDBObject>();
//			failTypeConditionLst.add(new BasicDBObject(FieldType.ALIAS_STATUS.getName(), StatusType.FAILED.getName()));
			

			BasicDBObject failConditionQuery = new BasicDBObject(FieldType.ALIAS_STATUS.getName(), StatusType.FAILED.getName());
			// FAIL QUERY END //

			// REJECTED QUERY START //

			List<BasicDBObject> rejectedConditionList = new ArrayList<BasicDBObject>();
			BasicDBObject rejectedQuery = new BasicDBObject(FieldType.STATUS.getName(), StatusType.REJECTED.getName());
			rejectedConditionList.add(rejectedQuery);
			BasicDBObject rejectedConditionQuery = new BasicDBObject("$and", rejectedConditionList);

			// REJECTED QUERY END //

			// DROPPED QUERY START //
			List<BasicDBObject> droppedConditionList = new ArrayList<BasicDBObject>();
			BasicDBObject droppedQuery = new BasicDBObject(FieldType.STATUS.getName(), StatusType.TIMEOUT.getName());

			droppedConditionList.add(droppedQuery);

			BasicDBObject droppedConditionQuery = new BasicDBObject("$and", droppedConditionList);

			// DROPPED QUERY END //

			// CANCELLED QUERY START //

			List<BasicDBObject> cancelledConditionList = new ArrayList<BasicDBObject>();
			BasicDBObject cancelledQuery = new BasicDBObject(FieldType.ALIAS_STATUS.getName(),
					StatusType.CANCELLED.getName());
			//cancelledConditionList.add(cancelledQuery);

			//BasicDBObject cancelledConditionQuery = new BasicDBObject("$and", cancelledConditionList);

			// CANCELLED QUERY END //

			// FRAUD QUERY START //

			List<BasicDBObject> fraudConditionList = new ArrayList<BasicDBObject>();
			List<BasicDBObject> fraudConditionLst = new ArrayList<BasicDBObject>();
			fraudConditionList.add(new BasicDBObject(FieldType.STATUS.getName(),
					StatusType.DENIED_BY_FRAUD.getName()));
			fraudConditionList.add(new BasicDBObject(FieldType.STATUS.getName(),
					StatusType.DENIED.getName()));
			
			BasicDBObject fraudConditionQuery = new BasicDBObject();
			fraudConditionQuery.append("$or", fraudConditionList);

			//fraudConditionLst.add(failQuery2);
			
			//BasicDBObject fraudConditionQuery = new BasicDBObject("$or", fraudConditionList);

			// FRAUD QUERY END //

			// INVALID QUERY START //

			List<BasicDBObject> invalidConditionList = new ArrayList<BasicDBObject>();
			BasicDBObject invalidQuery = new BasicDBObject(FieldType.STATUS.getName(), StatusType.INVALID.getName());
			invalidConditionList.add(invalidQuery);
			BasicDBObject invalidConditionQuery = new BasicDBObject("$and", invalidConditionList);

			// INVALID QUERY END //

			List<BasicDBObject> allConditionQueryListForfail = new ArrayList<BasicDBObject>();
			if (!currencyQuery.isEmpty()) {
				allConditionQueryListForfail.add(currencyQuery);
			}

			if (!failConditionQuery.isEmpty()) {
				allConditionQueryListForfail.add(failConditionQuery);
			}
			if (!dateQuery.isEmpty()) {
				allConditionQueryListForfail.add(dateQuery);
			}
			if (!payIdquery.isEmpty()) {
				allConditionQueryListForfail.add(payIdquery);
			}
			if (!resellerIdquery.isEmpty()) {
				allConditionQueryListForfail.add(resellerIdquery);
			}
			

			if (!dateIndexConditionQuery.isEmpty()) {
				allConditionQueryListForfail.add(dateIndexConditionQuery);
			}

			BasicDBObject allConditionQueryObjforfail = new BasicDBObject("$and", allConditionQueryListForfail);

			List<BasicDBObject> allConditionQueryListForReject = new ArrayList<BasicDBObject>();
			if (!currencyQuery.isEmpty()) {
				allConditionQueryListForReject.add(currencyQuery);
			}

			if (!refundConditionQuery.isEmpty()) {
				allConditionQueryListForReject.add(rejectedConditionQuery);
			}
			if (!dateQuery.isEmpty()) {
				allConditionQueryListForReject.add(dateQuery);
			}
			if (!payIdquery.isEmpty()) {
				allConditionQueryListForReject.add(payIdquery);
			}
			
			if (!resellerIdquery.isEmpty()) {
				allConditionQueryListForReject.add(resellerIdquery);
			}
			
			if (!dateIndexConditionQuery.isEmpty()) {
				allConditionQueryListForReject.add(dateIndexConditionQuery);
			}

			BasicDBObject allConditionQueryObjforReject = new BasicDBObject("$and", allConditionQueryListForReject);

			List<BasicDBObject> allConditionQueryListForDropped = new ArrayList<BasicDBObject>();
			if (!currencyQuery.isEmpty()) {
				allConditionQueryListForDropped.add(currencyQuery);
			}

			if (!refundConditionQuery.isEmpty()) {
				allConditionQueryListForDropped.add(droppedConditionQuery);
			}

			if (!dateQuery.isEmpty()) {
				allConditionQueryListForDropped.add(dateQuery);
			}
			if (!payIdquery.isEmpty()) {
				allConditionQueryListForDropped.add(payIdquery);
			}
			if (!resellerIdquery.isEmpty()) {
				allConditionQueryListForDropped.add(resellerIdquery);
			}
			

			if (!dateIndexConditionQuery.isEmpty()) {
				allConditionQueryListForDropped.add(dateIndexConditionQuery);
			}

			BasicDBObject allConditionQueryObjforDropped = new BasicDBObject("$and", allConditionQueryListForDropped);

			List<BasicDBObject> allConditionQueryListForCancelled = new ArrayList<BasicDBObject>();
			if (!currencyQuery.isEmpty()) {
				allConditionQueryListForCancelled.add(currencyQuery);
			}

			if (!cancelledQuery.isEmpty()) {
				allConditionQueryListForCancelled.add(cancelledQuery);
			}
			if (!dateQuery.isEmpty()) {
				allConditionQueryListForCancelled.add(dateQuery);
			}
			if (!payIdquery.isEmpty()) {
				allConditionQueryListForCancelled.add(payIdquery);
			}
			if (!resellerIdquery.isEmpty()) {
				allConditionQueryListForCancelled.add(resellerIdquery);
			}
			
			if (!dateIndexConditionQuery.isEmpty()) {
				allConditionQueryListForCancelled.add(dateIndexConditionQuery);
			}

			BasicDBObject allConditionQueryObjforCancelled = new BasicDBObject("$and",
					allConditionQueryListForCancelled);

			List<BasicDBObject> allConditionQueryListForFraud = new ArrayList<BasicDBObject>();
			if (!currencyQuery.isEmpty()) {
				allConditionQueryListForFraud.add(currencyQuery);
			}

			if (!fraudConditionQuery.isEmpty()) {
				allConditionQueryListForFraud.add(fraudConditionQuery);
			}
			if (!dateQuery.isEmpty()) {
				allConditionQueryListForFraud.add(dateQuery);
			}
			if (!payIdquery.isEmpty()) {
				allConditionQueryListForFraud.add(payIdquery);
			}
			
			if (!resellerIdquery.isEmpty()) {
				allConditionQueryListForFraud.add(resellerIdquery);
			}
			

			if (!dateIndexConditionQuery.isEmpty()) {
				allConditionQueryListForFraud.add(dateIndexConditionQuery);
			}

			BasicDBObject allConditionQueryObjforFraud = new BasicDBObject("$and", allConditionQueryListForFraud);

			List<BasicDBObject> allConditionQueryListForInvalid = new ArrayList<BasicDBObject>();
			if (!currencyQuery.isEmpty()) {
				allConditionQueryListForInvalid.add(currencyQuery);
			}

			if (!refundConditionQuery.isEmpty()) {
				allConditionQueryListForInvalid.add(invalidConditionQuery);
			}
			if (!dateQuery.isEmpty()) {
				allConditionQueryListForInvalid.add(dateQuery);
			}
			if (!payIdquery.isEmpty()) {
				allConditionQueryListForInvalid.add(payIdquery);
			}
			if (!resellerIdquery.isEmpty()) {
				allConditionQueryListForInvalid.add(resellerIdquery);
			}

			if (!dateIndexConditionQuery.isEmpty()) {
				allConditionQueryListForInvalid.add(dateIndexConditionQuery);
			}

			BasicDBObject allConditionQueryObjforInvalid = new BasicDBObject("$and", allConditionQueryListForInvalid);

			MongoDatabase dbIns = mongoInstance.getDB();
			MongoCollection<Document> coll = dbIns
					.getCollection(PropertiesManager.propertiesMap.get(prefix + Constants.TRANSACTION_STATUS_COLLECTION.getValue()));

			int totalfail = 0;
			int totalrejectedDeclined = 0;
			int totalcancelled = 0;
			int totalfraud = 0;
			int totalinvalid = 0;
			int totaldropped = 0;
			int totalsucess=0;
			
			
			totalfail = (int) coll.count(allConditionQueryObjforfail);

			totalcancelled = (int) coll.count(allConditionQueryObjforCancelled);
			

			totalfraud = (int) coll.count(allConditionQueryObjforFraud);


			
			logger.info("Statistics All query end ");
			statistics.setTotalFailed((String.valueOf(totalfail)));
			statistics.setTotalCancelled(String.valueOf(totalcancelled));
			statistics.setTotalFraud(String.valueOf(totalfraud));

			//statistics.setTotalSuccess((String.valueOf(totalsucess)));
			//statistics.setTotalRejectedDeclined(String.valueOf(totalrejectedDeclined));
			//statistics.setTotalDropped(String.valueOf(totaldropped));
			//statistics.setTotalInvalid(String.valueOf(totalinvalid));
		} catch (Exception exception) {
			logger.error("Exception", exception);

		} finally {

		}
		return statistics;

	}

	public Statistics statisticsSummaryCapture(String payId, String currency, String fromDate, String toDate,
			UserType userType,User user) {
		logger.info("Statistics capture query generation started ");
		BigDecimal totalApproved = BigDecimal.ZERO;
		Statistics statistics = new Statistics();
		BasicDBObject dateQuery = new BasicDBObject();
		List<BasicDBObject> currencyConditionLst = new ArrayList<BasicDBObject>();
		List<BasicDBObject> saleOrAuthList = new ArrayList<BasicDBObject>();
		BasicDBObject currencyQuery = new BasicDBObject();
		BasicDBObject payIdquery = new BasicDBObject();
		BasicDBObject resellerIdquery =  new BasicDBObject();
		toDate = toDate + " 23:59:59";
		fromDate = fromDate + " 00:00:00";

		try {

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

			long diff = dateEnd.getTime() - dateStart.getTime();
			 
			int diffDays = (int) (diff / (24 * 60 * 60 * 1000));
			
			LocalDate incrementingDate = dateStart.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
			LocalDate endDate = dateEnd.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();

			List<String> allDatesIndex = new ArrayList<>();

			if(diffDays != 365 && diffDays != 366) {
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
				
			}
        
			/*
			 * if (!payId.equalsIgnoreCase("ALL MERCHANTS")) {
			 * payIdquery.put(FieldType.PAY_ID.getName(), payId); }
			 */
			
			if (payId.equalsIgnoreCase("ALL MERCHANTS")) {
				List<String> payIdLst = userDao.getPayIdForSplitPaymentMerchant(user.getSegment());
				logger.info("Get PayId For SplitPayment Merchant : " + payIdLst);
				if (payIdLst.size() > 0) {
					payIdquery.put(FieldType.PAY_ID.getName(), new BasicDBObject("$in", payIdLst));
				}
			} else {
				payIdquery.put(FieldType.PAY_ID.getName(), payId);
			}
			
			if (userType.equals(UserType.RESELLER)) {
				resellerIdquery.put(FieldType.RESELLER_ID.getName(), user.getResellerId());
			}
			
			
			if (!currency.equalsIgnoreCase("ALL")) {
				currencyQuery.put(FieldType.CURRENCY_CODE.getName(), currency);
			} else {
				PropertiesManager propertiesManager = new PropertiesManager();
				Map<String, String> allCurrencyMap;
				allCurrencyMap = propertiesManager.getAllProperties(alphabaticFileName);
				for (Map.Entry<String, String> entry : allCurrencyMap.entrySet()) {
					currencyConditionLst.add(new BasicDBObject(FieldType.CURRENCY_CODE.getName(), entry.getKey()));
				}
				currencyQuery.append("$or", currencyConditionLst);
			}
			List<BasicDBObject> saleConditionList = new ArrayList<BasicDBObject>();
			saleConditionList.add(new BasicDBObject(FieldType.ORIG_TXNTYPE.getName(), TransactionType.SALE.getName()));
			saleConditionList.add(new BasicDBObject(FieldType.ALIAS_STATUS.getName(), StatusType.CAPTURED.getName()));

			BasicDBObject saleConditionQuery = new BasicDBObject("$and", saleConditionList);
			saleOrAuthList.add(saleConditionQuery);
			

			List<BasicDBObject> saleSettleConditionList = new ArrayList<BasicDBObject>();
			saleSettleConditionList.add(new BasicDBObject(FieldType.ORIG_TXNTYPE.getName(), TransactionType.SALE.getName()));
			saleSettleConditionList.add(new BasicDBObject(FieldType.ALIAS_STATUS.getName(), StatusType.SETTLED_SETTLE.getName()));

			BasicDBObject saleSettleConditionQuery = new BasicDBObject("$and", saleSettleConditionList);

			saleOrAuthList.add(saleSettleConditionQuery);
			
			BasicDBObject authndSaleConditionQuery = new BasicDBObject("$or", saleOrAuthList);
			
			List<BasicDBObject> allConditionQueryList = new ArrayList<BasicDBObject>();
			if (!currencyQuery.isEmpty()) {
				allConditionQueryList.add(currencyQuery);
			}

			if (!authndSaleConditionQuery.isEmpty()) {
				allConditionQueryList.add(authndSaleConditionQuery);
			}
			if (!dateQuery.isEmpty()) {
				allConditionQueryList.add(dateQuery);
			}
			if (!payIdquery.isEmpty()) {
				allConditionQueryList.add(payIdquery);
			}
			if (!resellerIdquery.isEmpty()) {
				allConditionQueryList.add(resellerIdquery);
			}
			
			if (!dateIndexConditionQuery.isEmpty()) {
				allConditionQueryList.add(dateIndexConditionQuery);
			}

			BasicDBObject allConditionQueryObjforsale = new BasicDBObject("$and", allConditionQueryList);

			List<BasicDBObject> allConditionQueryListForApproved = new ArrayList<BasicDBObject>();
			if (!currencyQuery.isEmpty()) {
				allConditionQueryListForApproved.add(currencyQuery);
			}

			if (!dateQuery.isEmpty()) {
				allConditionQueryListForApproved.add(dateQuery);
			}
			if (!payIdquery.isEmpty()) {
				allConditionQueryListForApproved.add(payIdquery);
			}

			if (!dateIndexConditionQuery.isEmpty()) {
				allConditionQueryListForApproved.add(dateIndexConditionQuery);
			}

			MongoDatabase dbIns = mongoInstance.getDB();
			MongoCollection<Document> coll = dbIns
					.getCollection(PropertiesManager.propertiesMap.get(prefix + Constants.TRANSACTION_STATUS_COLLECTION.getValue()));

			logger.info("Statistics Capture query start ");
			int totalsucess = 0;
			statistics.setTotalSuccess(String.valueOf(totalsucess));

			BasicDBObject match = new BasicDBObject("$match", allConditionQueryObjforsale);

			BasicDBObject projectElement = new BasicDBObject();
			// projectElement.put(FieldType.PAYMENT_TYPE.toString(), 1);
			if (userType.equals(UserType.ADMIN) || userType.equals(UserType.SUBADMIN)) {
				projectElement.put(FieldType.TOTAL_AMOUNT.toString(), 1);
			}else {
				projectElement.put(FieldType.AMOUNT.toString(), 1);	
			}
			BasicDBObject project = new BasicDBObject("$project", projectElement);

			List<BasicDBObject> pipeline = Arrays.asList(match, project);
			AggregateIterable<Document> output = coll.aggregate(pipeline);
			output.allowDiskUse(true);
			MongoCursor<Document> cursor = output.iterator();

			logger.info("Statistics Capture query end ");

			logger.info("Statistics Approved amount calculation started ");
			if (userType.equals(UserType.ADMIN) || userType.equals(UserType.SUBADMIN)) {
				while (cursor.hasNext()) {
					totalsucess++;
					Document dbobj = cursor.next();
	
					String approvedAmount = "";
					approvedAmount = (dbobj.getString(FieldType.TOTAL_AMOUNT.toString()));
					BigDecimal addApproved = new BigDecimal(approvedAmount);
					totalApproved = totalApproved.add(addApproved).setScale(2, RoundingMode.HALF_DOWN);
	
				}
			}else {
				while (cursor.hasNext()) {
					totalsucess++;
					Document dbobj = cursor.next();

					String approvedAmount = "";
					approvedAmount = (dbobj.getString(FieldType.AMOUNT.toString()));
					BigDecimal addApproved = new BigDecimal(approvedAmount);
					totalApproved = totalApproved.add(addApproved).setScale(2, RoundingMode.HALF_DOWN);

				}

			}			
			cursor.close();
			logger.info("Statistics Approved amount calculation end ");
			statistics.setTotalSuccess(String.valueOf(totalsucess));
			statistics.setApprovedAmount(String.valueOf(totalApproved));
		} catch (Exception exception) {
			logger.error("Exception", exception);

		}
		return statistics;

	}

	public Statistics statisticsSummaryRefund(String payId, String currency, String fromDate, String toDate,
			UserType userType, User user) {
		System.out.println("Statistics refund query generation started ");
		logger.info("Statistics refund query generation started ");
		BigDecimal totalRefunded = BigDecimal.ZERO;
		Statistics statistics = new Statistics();
		BasicDBObject dateQuery = new BasicDBObject();
		List<BasicDBObject> currencyConditionLst = new ArrayList<BasicDBObject>();
		List<BasicDBObject> refundOrAuthList = new ArrayList<BasicDBObject>();
		BasicDBObject currencyQuery = new BasicDBObject();
		BasicDBObject payIdquery = new BasicDBObject();
		BasicDBObject resellerIdquery = new BasicDBObject();
		fromDate = fromDate + " 00:00:00";
		toDate = toDate + " 23:59:59";
		try {

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
			/*
			 * if (!payId.equalsIgnoreCase("ALL MERCHANTS")) {
			 * payIdquery.put(FieldType.PAY_ID.getName(), payId); }
			 */
			
			if (payId.equalsIgnoreCase("ALL MERCHANTS")) {
				List<String> payIdLst = userDao.getPayIdForSplitPaymentMerchant(user.getSegment());
				logger.info("Get PayId For SplitPayment Merchant : " + payIdLst);
				if (payIdLst.size() > 0) {
					payIdquery.put(FieldType.PAY_ID.getName(), new BasicDBObject("$in", payIdLst));
				}
			} else {
				payIdquery.put(FieldType.PAY_ID.getName(), payId);
			}
			
			if (userType.equals(UserType.RESELLER)) {
				resellerIdquery.put(FieldType.RESELLER_ID.getName(), user.getResellerId());
			}
			if (!currency.equalsIgnoreCase("ALL")) {
				currencyQuery.put(FieldType.CURRENCY_CODE.getName(), currency);
			} else {
				PropertiesManager propertiesManager = new PropertiesManager();
				Map<String, String> allCurrencyMap;
				allCurrencyMap = propertiesManager.getAllProperties(alphabaticFileName);
				for (Map.Entry<String, String> entry : allCurrencyMap.entrySet()) {
					currencyConditionLst.add(new BasicDBObject(FieldType.CURRENCY_CODE.getName(), entry.getKey()));
				}
				currencyQuery.append("$or", currencyConditionLst);
			}
			List<BasicDBObject> refundConditionList = new ArrayList<BasicDBObject>();
			refundConditionList.add(new BasicDBObject(FieldType.ORIG_TXNTYPE.getName(), TransactionType.REFUND.getName()));
			refundConditionList.add(new BasicDBObject(FieldType.ALIAS_STATUS.getName(), StatusType.CAPTURED.getName()));

			BasicDBObject refundConditionQuery = new BasicDBObject("$and", refundConditionList);
			refundOrAuthList.add(refundConditionQuery);
		

			List<BasicDBObject> refundSettledAmountList = new ArrayList<BasicDBObject>();
			refundSettledAmountList.add(new BasicDBObject(FieldType.ORIG_TXNTYPE.getName(), TransactionType.REFUND.getName()));
			refundSettledAmountList.add(new BasicDBObject(FieldType.ALIAS_STATUS.getName(), StatusType.SETTLED_SETTLE.getName()));
			BasicDBObject refundSettleConditionQuery = new BasicDBObject("$and", refundSettledAmountList);
			refundOrAuthList.add(refundSettleConditionQuery);
			
			
			BasicDBObject refundAuthConditionQuery = new BasicDBObject("$or", refundOrAuthList);
			
			List<BasicDBObject> allConditionQueryList = new ArrayList<BasicDBObject>();
			
			
			
			if (!currencyQuery.isEmpty()) {
				allConditionQueryList.add(currencyQuery);
			}

			if (!refundAuthConditionQuery.isEmpty()) {
				allConditionQueryList.add(refundAuthConditionQuery);
			}
			if (!dateQuery.isEmpty()) {
				allConditionQueryList.add(dateQuery);
			}
			if (!payIdquery.isEmpty()) {
				allConditionQueryList.add(payIdquery);
			}
			
			if (!resellerIdquery.isEmpty()) {
				allConditionQueryList.add(resellerIdquery);
			}
			

			if (!dateIndexConditionQuery.isEmpty()) {
				allConditionQueryList.add(dateIndexConditionQuery);
			}

			BasicDBObject allConditionQueryObjforsale = new BasicDBObject("$and", allConditionQueryList);

			List<BasicDBObject> allConditionQueryListForApproved = new ArrayList<BasicDBObject>();
			if (!currencyQuery.isEmpty()) {
				allConditionQueryListForApproved.add(currencyQuery);
			}

			if (!dateQuery.isEmpty()) {
				allConditionQueryListForApproved.add(dateQuery);
			}
			if (!payIdquery.isEmpty()) {
				allConditionQueryListForApproved.add(payIdquery);
			}
			
			if (!resellerIdquery.isEmpty()) {
				allConditionQueryListForApproved.add(resellerIdquery);
			}

			if (!dateIndexConditionQuery.isEmpty()) {
				allConditionQueryListForApproved.add(dateIndexConditionQuery);
			}

			MongoDatabase dbIns = mongoInstance.getDB();
			MongoCollection<Document> coll = dbIns
					.getCollection(PropertiesManager.propertiesMap.get(prefix + Constants.TRANSACTION_STATUS_COLLECTION.getValue()));

			logger.info("Statistics Refund query start ");
			int totalRefund = 0;

			BasicDBObject match = new BasicDBObject("$match", allConditionQueryObjforsale);
			BasicDBObject projectElement = new BasicDBObject();
			// projectElement.put(FieldType.PAYMENT_TYPE.toString(), 1);
			projectElement.put(FieldType.AMOUNT.toString(), 1);
			projectElement.put(FieldType.TOTAL_AMOUNT.toString(), 1);
			BasicDBObject project = new BasicDBObject("$project", projectElement);

			List<BasicDBObject> pipeline = Arrays.asList(match, project);
			AggregateIterable<Document> output = coll.aggregate(pipeline);
			output.allowDiskUse(true);
			MongoCursor<Document> cursor = output.iterator();

			logger.info("Statistics Refund query end ");

			while (cursor.hasNext()) {
				totalRefund++;
				Document dbobj = cursor.next();

				String approvedAmount = "";
				if (userType.equals(UserType.ADMIN) || userType.equals(UserType.SUBADMIN)) {

					approvedAmount = (dbobj.getString(FieldType.AMOUNT.toString()));
				}

				else {
					approvedAmount = (dbobj.getString(FieldType.AMOUNT.toString()));
				}

				totalRefunded = totalRefunded.add(new BigDecimal(approvedAmount)).setScale(2, RoundingMode.HALF_DOWN);

			}
			cursor.close();
			statistics.setTotalRefunded(String.valueOf(totalRefund));
			statistics.setRefundedAmount(String.valueOf(totalRefunded));
		} catch (Exception exception) {
			logger.error("Exception", exception);

		}
		return statistics;

	}

	public Statistics statisticsSettledAmountSummary(String payId, String currency, String fromDate, String toDate,
			UserType userType,User user) {


		logger.info("Statistics capture query generation started ");
		BigDecimal totalApproved = BigDecimal.ZERO;
		Statistics statistics = new Statistics();
		BasicDBObject dateQuery = new BasicDBObject();
		List<BasicDBObject> currencyConditionLst = new ArrayList<BasicDBObject>();
		List<BasicDBObject> saleOrAuthList = new ArrayList<BasicDBObject>();
		BasicDBObject currencyQuery = new BasicDBObject();
		BasicDBObject payIdquery = new BasicDBObject();
		BasicDBObject resellerIdquery = new BasicDBObject();
		toDate = toDate + " 23:59:59";
		fromDate = fromDate + " 00:00:00";

		try {

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

			long diff = dateEnd.getTime() - dateStart.getTime();
			 
			int diffDays = (int) (diff / (24 * 60 * 60 * 1000));
			
			LocalDate incrementingDate = dateStart.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
			LocalDate endDate = dateEnd.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();

			List<String> allDatesIndex = new ArrayList<>();

			if(diffDays != 365 && diffDays != 366) {
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
				
			}
        
			/*
			 * if (!payId.equalsIgnoreCase("ALL MERCHANTS")) {
			 * payIdquery.put(FieldType.PAY_ID.getName(), payId); }
			 */
			
			if (payId.equalsIgnoreCase("ALL MERCHANTS")) {
				List<String> payIdLst = userDao.getPayIdForSplitPaymentMerchant(user.getSegment());
				logger.info("Get PayId For SplitPayment Merchant : " + payIdLst);
				if (payIdLst.size() > 0) {
					payIdquery.put(FieldType.PAY_ID.getName(), new BasicDBObject("$in", payIdLst));
				}
			} else {
				payIdquery.put(FieldType.PAY_ID.getName(), payId);
			}
			
			if (userType.equals(UserType.RESELLER)) {
				resellerIdquery.put(FieldType.RESELLER_ID.getName(), user.getResellerId());
			}
			
			
			
			if (!currency.equalsIgnoreCase("ALL")) {
				currencyQuery.put(FieldType.CURRENCY_CODE.getName(), currency);
			} else {
				PropertiesManager propertiesManager = new PropertiesManager();
				Map<String, String> allCurrencyMap;
				allCurrencyMap = propertiesManager.getAllProperties(alphabaticFileName);
				for (Map.Entry<String, String> entry : allCurrencyMap.entrySet()) {
					currencyConditionLst.add(new BasicDBObject(FieldType.CURRENCY_CODE.getName(), entry.getKey()));
				}
				currencyQuery.append("$or", currencyConditionLst);
			}

			
			List<BasicDBObject> saleSettleConditionList = new ArrayList<BasicDBObject>();
			saleSettleConditionList.add(new BasicDBObject(FieldType.STATUS.getName(), StatusType.SETTLED_SETTLE.getName()));

			BasicDBObject saleSettleConditionQuery = new BasicDBObject("$and", saleSettleConditionList);

			saleOrAuthList.add(saleSettleConditionQuery);
			
			BasicDBObject authndSaleConditionQuery = new BasicDBObject("$or", saleOrAuthList);
			
			List<BasicDBObject> allConditionQueryList = new ArrayList<BasicDBObject>();
			if (!currencyQuery.isEmpty()) {
				allConditionQueryList.add(currencyQuery);
			}

			if (!authndSaleConditionQuery.isEmpty()) {
				allConditionQueryList.add(authndSaleConditionQuery);
			}
			if (!dateQuery.isEmpty()) {
				allConditionQueryList.add(dateQuery);
			}
			if (!payIdquery.isEmpty()) {
				allConditionQueryList.add(payIdquery);
			}
			
			if (!resellerIdquery.isEmpty()) {
				allConditionQueryList.add(resellerIdquery);
			}
			
			if (!dateIndexConditionQuery.isEmpty()) {
				allConditionQueryList.add(dateIndexConditionQuery);
			}

			BasicDBObject allConditionQueryObjforsale = new BasicDBObject("$and", allConditionQueryList);

			List<BasicDBObject> allConditionQueryListForApproved = new ArrayList<BasicDBObject>();
			if (!currencyQuery.isEmpty()) {
				allConditionQueryListForApproved.add(currencyQuery);
			}

			if (!dateQuery.isEmpty()) {
				allConditionQueryListForApproved.add(dateQuery);
			}
			if (!payIdquery.isEmpty()) {
				allConditionQueryListForApproved.add(payIdquery);
			}
			
			if (!resellerIdquery.isEmpty()) {
				allConditionQueryListForApproved.add(resellerIdquery);
			}

			if (!dateIndexConditionQuery.isEmpty()) {
				allConditionQueryListForApproved.add(dateIndexConditionQuery);
			}

			MongoDatabase dbIns = mongoInstance.getDB();
			MongoCollection<Document> coll = dbIns
					.getCollection(PropertiesManager.propertiesMap.get(prefix + Constants.TRANSACTION_STATUS_COLLECTION.getValue()));

			logger.info("Statistics Capture query start ");
			int totalsucess = 0;
			statistics.setTotalSuccess(String.valueOf(totalsucess));

			BasicDBObject match = new BasicDBObject("$match", allConditionQueryObjforsale);

			BasicDBObject projectElement = new BasicDBObject();
			// projectElement.put(FieldType.PAYMENT_TYPE.toString(), 1);
			if (userType.equals(UserType.ADMIN) || userType.equals(UserType.SUBADMIN)) {
				projectElement.put(FieldType.TOTAL_AMOUNT.toString(), 1);
			}else {
				projectElement.put(FieldType.AMOUNT.toString(), 1);	
			}
			BasicDBObject project = new BasicDBObject("$project", projectElement);

			List<BasicDBObject> pipeline = Arrays.asList(match, project);
			AggregateIterable<Document> output = coll.aggregate(pipeline);
			output.allowDiskUse(true);
			MongoCursor<Document> cursor = output.iterator();

			logger.info("Statistics Capture query end ");

			logger.info("Statistics Approved amount calculation started ");
			if (userType.equals(UserType.ADMIN) || userType.equals(UserType.SUBADMIN)) {
				while (cursor.hasNext()) {
					totalsucess++;
					Document dbobj = cursor.next();
	
					String approvedAmount = "";
					approvedAmount = (dbobj.getString(FieldType.TOTAL_AMOUNT.toString()));
					BigDecimal addApproved = new BigDecimal(approvedAmount);
					totalApproved = totalApproved.add(addApproved).setScale(2, RoundingMode.HALF_DOWN);
	
				}
			}else {
				while (cursor.hasNext()) {
					totalsucess++;
					Document dbobj = cursor.next();

					String approvedAmount = "";
					approvedAmount = (dbobj.getString(FieldType.AMOUNT.toString()));
					BigDecimal addApproved = new BigDecimal(approvedAmount);
					totalApproved = totalApproved.add(addApproved).setScale(2, RoundingMode.HALF_DOWN);

				}

			}			
			cursor.close();
			logger.info("Statistics Approved amount calculation end ");
			statistics.setTotalSettledAmount(String.valueOf(totalApproved));
		} catch (Exception exception) {
			logger.error("Exception", exception);

		}
		return statistics;

	
	}

}
