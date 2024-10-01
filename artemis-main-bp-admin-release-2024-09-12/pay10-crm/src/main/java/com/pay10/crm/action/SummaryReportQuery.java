package com.pay10.crm.action;

import java.math.BigInteger;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mongodb.BasicDBObject;
import com.mongodb.BasicDBObjectBuilder;
import com.mongodb.client.AggregateIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import com.pay10.commons.dao.TransactionSearchServiceMongo;
import com.pay10.commons.exception.ErrorType;
import com.pay10.commons.exception.SystemException;
import com.pay10.commons.mongo.MongoInstance;
import com.pay10.commons.user.AccountCurrencyRegion;
import com.pay10.commons.user.CardHolderType;
import com.pay10.commons.user.SummaryObject;
import com.pay10.commons.user.SummaryReportNodalObject;
import com.pay10.commons.user.TransactionSearch;
import com.pay10.commons.user.User;
import com.pay10.commons.user.UserDao;
import com.pay10.commons.user.UserType;
import com.pay10.commons.util.Constants;
import com.pay10.commons.util.CrmFieldConstants;
import com.pay10.commons.util.CrmFieldType;
import com.pay10.commons.util.DateCreater;
import com.pay10.commons.util.FieldType;
import com.pay10.commons.util.MopType;
import com.pay10.commons.util.PaymentType;
import com.pay10.commons.util.PropertiesManager;
import com.pay10.commons.util.StatusType;
import com.pay10.commons.util.TransactionType;
import com.pay10.commons.util.TxnType;

@Component
public class SummaryReportQuery {
	@Autowired
	private MongoInstance mongoInstance;

	@Autowired
	private UserDao userdao;

	@Autowired
	private TransactionSearchServiceMongo transactionSearchServiceMongo;

	@Autowired
	PropertiesManager propertiesManager;
	private static Logger logger = LoggerFactory.getLogger(SummaryReportQuery.class.getName());
	private static final String alphabaticFileName = "alphabatic-currencycode.properties";
	private static final String prefix = "MONGO_DB_";
	private static Map<String, User> userMap = new HashMap<String, User>();

	public List<TransactionSearch> summaryReport(String fromDate, String toDate, String payId, String paymentType,
			String acquirer, String currency, User user, int start, int length, String paymentsRegion,
			String cardHolderType, String pgRefNum, String mopType, String transactionType, String orderId, String phoneNo) throws SystemException {

		logger.info("Inside search summary report query");

		List<TransactionSearch> transactionList = new ArrayList<TransactionSearch>();
		try {

			BasicDBObject dateQuery = new BasicDBObject();
			List<BasicDBObject> paramConditionLst = new ArrayList<BasicDBObject>();
			BasicDBObject currencyQuery = new BasicDBObject();
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

				dateQuery.put(FieldType.PG_DATE_TIME.getName(),
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
				dateIndexConditionList.add(new BasicDBObject("PG_DATE_TIME_INDEX", dateIndex));
			}
			if (StringUtils.isNotBlank(PropertiesManager.propertiesMap.get(Constants.USE_PG_DATE_TIME_INDEX.getValue()))
					&& PropertiesManager.propertiesMap.get(Constants.USE_PG_DATE_TIME_INDEX.getValue())
							.equalsIgnoreCase("Y")) {
				dateIndexConditionQuery.append("$or", dateIndexConditionList);
			}
			if (!pgRefNum.isEmpty()) {
				paramConditionLst.add(new BasicDBObject(FieldType.PG_REF_NUM.getName(), pgRefNum));
			}
			if (!orderId.isEmpty()) {
				paramConditionLst.add(new BasicDBObject(FieldType.ORDER_ID.getName(), orderId));
			}
			if (!phoneNo.isEmpty()) {
				paramConditionLst.add(new BasicDBObject(FieldType.CUST_PHONE.getName(), phoneNo));
			}
			if (!payId.equalsIgnoreCase("ALL")) {
				paramConditionLst.add(new BasicDBObject(FieldType.PAY_ID.getName(), payId));
			}
			if (!currency.equalsIgnoreCase("ALL")) {
				paramConditionLst.add(new BasicDBObject(FieldType.CURRENCY_CODE.getName(), currency));
			}

			if (!paymentsRegion.equalsIgnoreCase("ALL")) {
				paramConditionLst.add(new BasicDBObject(FieldType.PAYMENTS_REGION.getName(), paymentsRegion));
			}

			if (!cardHolderType.equalsIgnoreCase("ALL")) {
				paramConditionLst.add(new BasicDBObject(FieldType.CARD_HOLDER_TYPE.getName(), cardHolderType));
			}

			if (!mopType.equalsIgnoreCase("ALL")) {
				paramConditionLst.add(new BasicDBObject(FieldType.MOP_TYPE.getName(), mopType));
			}

			if (!acquirer.equalsIgnoreCase("ALL")) {

				List<String> acquirerList = Arrays.asList(acquirer.split(","));
				for (String acq : acquirerList) {

					acquirerConditionLst.add(new BasicDBObject(FieldType.ACQUIRER_TYPE.getName(), acq));
				}
				acquirerQuery.append("$or", acquirerConditionLst);

			}

			if (!paymentType.equalsIgnoreCase("ALL")) {
				paramConditionLst.add(new BasicDBObject(FieldType.PAYMENT_TYPE.getName(), paymentType));
			}

			if (transactionType.equalsIgnoreCase("ALL")) {

				List<BasicDBObject> saleConditionList = new ArrayList<BasicDBObject>();
				saleConditionList.add(new BasicDBObject(FieldType.TXNTYPE.getName(), TransactionType.SALE.getName()));
				// Done By chetan nagaria for change in settlement process to mark transaction as RNS
//				saleConditionList.add(new BasicDBObject(FieldType.STATUS.getName(), StatusType.SETTLED.getName()));
				saleConditionList.add(new BasicDBObject(FieldType.STATUS.getName(), StatusType.SETTLED_RECONCILLED.getName()));
				saleConditionList.add(new BasicDBObject(FieldType.STATUS.getName(), StatusType.SETTLED_SETTLE.getName()));
				saleConditionList
						.add(new BasicDBObject(FieldType.RESPONSE_CODE.getName(), ErrorType.SUCCESS.getCode()));

				BasicDBObject saleConditionQuery = new BasicDBObject("$and", saleConditionList);

				List<BasicDBObject> authConditionList = new ArrayList<BasicDBObject>();
				authConditionList
						.add(new BasicDBObject(FieldType.TXNTYPE.getName(), TransactionType.AUTHORISE.getName()));
				authConditionList.add(new BasicDBObject(FieldType.STATUS.getName(), StatusType.APPROVED.getName()));

				BasicDBObject authConditionQuery = new BasicDBObject("$and", authConditionList);

				List<BasicDBObject> recoConditionList = new ArrayList<BasicDBObject>();
//				Done By chetan nagaria for change in settlement process to mark transaction as RNS
				recoConditionList.add(new BasicDBObject(FieldType.TXNTYPE.getName(), TransactionType.RECO.getName()));
//				recoConditionList.add(new BasicDBObject(FieldType.STATUS.getName(), StatusType.SETTLED.getName()));
				saleConditionList.add(new BasicDBObject(FieldType.STATUS.getName(), new Document("$in", Arrays.asList(StatusType.SETTLED_SETTLE.getName(),StatusType.SETTLED_RECONCILLED.getName()))));
				recoConditionList.add(new BasicDBObject(FieldType.STATUS.getName(), StatusType.SETTLED_SETTLE.getName()));

				BasicDBObject recoConditionQuery = new BasicDBObject("$and", recoConditionList);

				List<BasicDBObject> refundConditionList = new ArrayList<BasicDBObject>();
				refundConditionList
						.add(new BasicDBObject(FieldType.TXNTYPE.getName(), TransactionType.REFUND.getName()));
//				Done By chetan nagaria for change in settlement process to mark transaction as RNS
				refundConditionList.add(new BasicDBObject(FieldType.STATUS.getName(), StatusType.SETTLED_SETTLE.getName()));

				BasicDBObject refundConditionQuery = new BasicDBObject("$and", refundConditionList);

				List<BasicDBObject> recoRefundConditionList = new ArrayList<BasicDBObject>();
				recoRefundConditionList
						.add(new BasicDBObject(FieldType.TXNTYPE.getName(), TransactionType.REFUNDRECO.getName()));
//				Done By chetan nagaria for change in settlement process to mark transaction as RNS
				recoRefundConditionList
						.add(new BasicDBObject(FieldType.STATUS.getName(), StatusType.SETTLED_SETTLE.getName()));

				BasicDBObject recoRefundConditionQuery = new BasicDBObject("$and", recoRefundConditionList);

				saleOrAuthList.add(saleConditionQuery);
				saleOrAuthList.add(authConditionQuery);
				saleOrAuthList.add(recoConditionQuery);
				saleOrAuthList.add(refundConditionQuery);
				saleOrAuthList.add(recoRefundConditionQuery);

			}

			else if (transactionType.equalsIgnoreCase("SALE")) {

				List<BasicDBObject> saleConditionList = new ArrayList<BasicDBObject>();
				saleConditionList.add(new BasicDBObject(FieldType.TXNTYPE.getName(), TransactionType.SALE.getName()));
//				Done By chetan nagaria for change in settlement process to mark transaction as RNS
				saleConditionList.add(new BasicDBObject(FieldType.STATUS.getName(), StatusType.SETTLED_SETTLE.getName()));
				saleConditionList
						.add(new BasicDBObject(FieldType.RESPONSE_CODE.getName(), ErrorType.SUCCESS.getCode()));

				BasicDBObject saleConditionQuery = new BasicDBObject("$and", saleConditionList);

				List<BasicDBObject> authConditionList = new ArrayList<BasicDBObject>();
				authConditionList
						.add(new BasicDBObject(FieldType.TXNTYPE.getName(), TransactionType.AUTHORISE.getName()));
				authConditionList.add(new BasicDBObject(FieldType.STATUS.getName(), StatusType.APPROVED.getName()));

				BasicDBObject authConditionQuery = new BasicDBObject("$and", authConditionList);

				List<BasicDBObject> recoConditionList = new ArrayList<BasicDBObject>();
				recoConditionList.add(new BasicDBObject(FieldType.TXNTYPE.getName(), TransactionType.RECO.getName()));
//				Done By chetan nagaria for change in settlement process to mark transaction as RNS
				recoConditionList.add(new BasicDBObject(FieldType.STATUS.getName(), StatusType.SETTLED_SETTLE.getName()));

				BasicDBObject recoConditionQuery = new BasicDBObject("$and", recoConditionList);

				saleOrAuthList.add(saleConditionQuery);
				saleOrAuthList.add(authConditionQuery);
				saleOrAuthList.add(recoConditionQuery);

			}

			else {

				List<BasicDBObject> refundConditionList = new ArrayList<BasicDBObject>();
				refundConditionList
						.add(new BasicDBObject(FieldType.TXNTYPE.getName(), TransactionType.REFUND.getName()));
//				Done By chetan nagaria for change in settlement process to mark transaction as RNS
				refundConditionList.add(new BasicDBObject(FieldType.STATUS.getName(), StatusType.SETTLED_SETTLE.getName()));

				BasicDBObject refundConditionQuery = new BasicDBObject("$and", refundConditionList);

				List<BasicDBObject> recoRefundConditionList = new ArrayList<BasicDBObject>();
				recoRefundConditionList
						.add(new BasicDBObject(FieldType.TXNTYPE.getName(), TransactionType.REFUNDRECO.getName()));
//				Done By chetan nagaria for change in settlement process to mark transaction as RNS
				recoRefundConditionList
						.add(new BasicDBObject(FieldType.STATUS.getName(), StatusType.SETTLED_SETTLE.getName()));

				BasicDBObject recoRefundConditionQuery = new BasicDBObject("$and", recoRefundConditionList);

				saleOrAuthList.add(refundConditionQuery);
				saleOrAuthList.add(recoRefundConditionQuery);

			}

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
			
			if (orderId.isEmpty() && pgRefNum.isEmpty()) {

				if (!dateQuery.isEmpty()) {
					allConditionQueryList.add(dateQuery);
				}
				if (!dateIndexConditionQuery.isEmpty()) {
					allConditionQueryList.add(dateIndexConditionQuery);
				}
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

			logger.info("Inside search summary report query , finalquery = " + finalquery);
			MongoDatabase dbIns = mongoInstance.getDB();
			MongoCollection<Document> coll = dbIns
					.getCollection(PropertiesManager.propertiesMap.get(prefix + Constants.COLLECTION_NAME.getValue()));
			BasicDBObject match = new BasicDBObject("$match", finalquery);

			BasicDBObject sort = new BasicDBObject("$sort", new BasicDBObject("CREATE_DATE", -1));
			BasicDBObject skip = new BasicDBObject("$skip", start);
			BasicDBObject limit = new BasicDBObject("$limit", length);
			
			Document firstGroup = new Document("_id", new Document("PG_REF_NUM", "$PG_REF_NUM").append("STATUS", "$STATUS"));
            BasicDBObject firstGroupObject = new BasicDBObject(firstGroup);
            BasicDBObject secondGroup = new BasicDBObject("$last", "$$ROOT");
            BasicDBObject groupQuery = new BasicDBObject("$group", firstGroupObject.append("contentList", secondGroup));
            BasicDBObject replacedRoot = new BasicDBObject("newRoot", "$contentList");
            BasicDBObject replaceRootQuery = new BasicDBObject("$replaceRoot", replacedRoot);
            List<Bson> pipeline = Arrays.asList(match, groupQuery, replaceRootQuery, sort, skip, limit);
			AggregateIterable<Document> output = coll.aggregate(pipeline);
			output.allowDiskUse(true);
			MongoCursor<Document> cursor = output.iterator();
			User merchant = new User();
			int count = 1;
			while (cursor.hasNext()) {

				Document doc = cursor.next();
				if (userMap.get(doc.getString(FieldType.PAY_ID.toString())) != null) {
					merchant = userMap.get(doc.getString(FieldType.PAY_ID.toString()));
				} else {
					merchant = userdao.findPayId(doc.getString(FieldType.PAY_ID.toString()));
					userMap.put(payId, merchant);
				}

				TransactionSearch transReport = new TransactionSearch();
				transReport.setCustomerName(doc.getString(FieldType.CARD_HOLDER_NAME.toString()));
				transReport.setSrNo(count++);
				BigInteger txnId = new BigInteger(doc.getString(FieldType.TXN_ID.toString()));
				transReport.setTransactionId(txnId);
				transReport.setTransactionIdString(String.valueOf(txnId));
				transReport.setDateFrom(doc.getString(FieldType.CREATE_DATE.getName()));
				transReport.setInternalCardIssusserBank(doc.getString(FieldType.INTERNAL_CARD_ISSUER_BANK.toString()));
				transReport.setInternalCardIssusserCountry(
						doc.getString(FieldType.INTERNAL_CARD_ISSUER_COUNTRY.toString()));
				transReport.setPayId(doc.getString(FieldType.PAY_ID.toString()));

				transReport.setOrderId(doc.getString(FieldType.ORDER_ID.toString()));
				String surchargeFlag = doc.getString(FieldType.SURCHARGE_FLAG.toString());
				transReport.setSurchargeFlag(surchargeFlag);
				transReport.setPgRefNum(doc.getString(FieldType.PG_REF_NUM.toString()));
				transReport.setCardMask(doc.getString(FieldType.CARD_MASK.toString()));
				transReport.setAmount(doc.getString(FieldType.AMOUNT.toString()));
				transReport.setTransactionRegion(doc.getString(FieldType.PAYMENTS_REGION.toString()));
				transReport.setCardHolderType(doc.getString(FieldType.CARD_HOLDER_TYPE.toString()));
				transReport.setTotalAmount(doc.getString(FieldType.TOTAL_AMOUNT.toString()));
				transReport.setAcquirerType(doc.getString(FieldType.ACQUIRER_TYPE.toString()));
				transReport.setMopType(doc.getString(FieldType.MOP_TYPE.toString()));
				transReport.setPaymentMethods(doc.getString(FieldType.PAYMENT_TYPE.toString()));
				transReport.setTransactionCaptureDate(doc.getString(FieldType.PG_DATE_TIME.toString()));
				if (doc.getString(FieldType.TXNTYPE.toString()).equalsIgnoreCase(TransactionType.RECO.getName())) {
					transReport.setTxnType(TransactionType.SALE.getName());
				} else {
					transReport.setTxnType(TransactionType.REFUND.getName());
				}

				transReport.setInternalCardIssusserCountry(
						doc.getString(FieldType.INTERNAL_CARD_ISSUER_COUNTRY.toString()));
				transReport.setCurrency(doc.getString(FieldType.CURRENCY_CODE.toString()));
				transReport.setPostSettledFlag(doc.getString(FieldType.POST_SETTLED_FLAG.toString()));
				transReport.setAcqId(doc.getString(FieldType.ACQ_ID.toString()));
				transReport.setRrn(doc.getString(FieldType.RRN.toString()));
				transReport.setTotalGstOnMerchant(
						String.format("%.2f", ((Double.valueOf(doc.getString(FieldType.ACQUIRER_TDR_SC.toString()))
								+ Double.valueOf(doc.getString(FieldType.PG_TDR_SC.toString()))))));
				transReport.setMerchantTdrCalculate(
						String.format("%.2f", (Double.valueOf(doc.getString(FieldType.ACQUIRER_GST.toString()))
								+ Double.valueOf(doc.getString(FieldType.PG_GST.toString())))));
				if (StringUtils.isNotBlank(merchant.getBusinessName())) {
					transReport.setBusinessName(merchant.getBusinessName());
				} else {
					transReport.setBusinessName("");
				}

				transReport.setNetMerchantPayableAmount(String.format("%.2f",
						((Double.valueOf(doc.getString(FieldType.TOTAL_AMOUNT.toString()))
								- Double.valueOf(doc.getString(FieldType.ACQUIRER_TDR_SC.toString()))
								- Double.valueOf(doc.getString(FieldType.PG_TDR_SC.toString()))
								- Double.valueOf(doc.getString(FieldType.ACQUIRER_GST.toString()))
								- Double.valueOf(doc.getString(FieldType.PG_GST.toString()))))));

				// For Admin and Sub Admin

				transReport.setPgSurchargeAmount(
						String.format("%.2f", ((Double.valueOf(doc.getString(FieldType.PG_TDR_SC.toString()))))));
				transReport.setAcquirerSurchargeAmount(
						String.format("%.2f", (Double.valueOf(doc.getString(FieldType.ACQUIRER_TDR_SC.toString())))));
				transReport.setPgGst(
						String.format("%.2f", ((Double.valueOf(doc.getString(FieldType.PG_GST.toString()))))));
				transReport.setAcquirerGst(
						String.format("%.2f", (Double.valueOf(doc.getString(FieldType.ACQUIRER_GST.toString())))));

				transactionList.add(transReport);
			}

			logger.info("Inside search summary report query , transactionList size = " + transactionList.size());
			cursor.close();

		} catch (Exception exception) {
			logger.error("Exception", exception);
		}
		return transactionList.stream().sorted(new Comparator<TransactionSearch>() {

			@Override
			public int compare(TransactionSearch o1, TransactionSearch o2) {
				return o2.getCreateDate().compareTo(o1.getCreateDate());
			}
		}).collect(Collectors.toList());
	}

	public int summaryReportRecord(String fromDate, String toDate, String payId, String paymentType, String acquirer,
			String currency, User user, String paymentsRegion, String cardHolderType, String pgRefNum, String mopType,
			String transactionType, String orderId, String phoneNo) throws SystemException {

		int total = 0;
		try {

			BasicDBObject dateQuery = new BasicDBObject();
			List<BasicDBObject> paramConditionLst = new ArrayList<BasicDBObject>();
			BasicDBObject currencyQuery = new BasicDBObject();
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

				dateQuery.put(FieldType.PG_DATE_TIME.getName(),
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
				dateIndexConditionList.add(new BasicDBObject("PG_DATE_TIME_INDEX", dateIndex));
			}
			if (StringUtils.isNotBlank(PropertiesManager.propertiesMap.get(Constants.USE_PG_DATE_TIME_INDEX.getValue()))
					&& PropertiesManager.propertiesMap.get(Constants.USE_PG_DATE_TIME_INDEX.getValue())
							.equalsIgnoreCase("Y")) {
				if (orderId.isEmpty() && pgRefNum.isEmpty()) {
				dateIndexConditionQuery.append("$or", dateIndexConditionList);
				}
			}
			if (!pgRefNum.isEmpty()) {
				paramConditionLst.add(new BasicDBObject(FieldType.PG_REF_NUM.getName(), pgRefNum));
			}
			
			if (!orderId.isEmpty()) {
				paramConditionLst.add(new BasicDBObject(FieldType.ORDER_ID.getName(), orderId));
			}
			
			if (!phoneNo.isEmpty()) {
				paramConditionLst.add(new BasicDBObject(FieldType.CUST_PHONE.getName(), phoneNo));
			}

			if (!payId.equalsIgnoreCase("ALL")) {
				paramConditionLst.add(new BasicDBObject(FieldType.PAY_ID.getName(), payId));
			}

			if (!paymentsRegion.equalsIgnoreCase("ALL")) {
				paramConditionLst.add(new BasicDBObject(FieldType.PAYMENTS_REGION.getName(), paymentsRegion));
			}

			if (!cardHolderType.equalsIgnoreCase("ALL")) {
				paramConditionLst.add(new BasicDBObject(FieldType.CARD_HOLDER_TYPE.getName(), cardHolderType));
			}

			if (!currency.equalsIgnoreCase("ALL")) {
				paramConditionLst.add(new BasicDBObject(FieldType.CURRENCY_CODE.getName(), currency));
			}

			if (!mopType.equalsIgnoreCase("ALL")) {
				paramConditionLst.add(new BasicDBObject(FieldType.MOP_TYPE.getName(), mopType));
			}

			if (!acquirer.equalsIgnoreCase("ALL")) {

				List<String> acquirerList = Arrays.asList(acquirer.split(","));
				for (String acq : acquirerList) {

					acquirerConditionLst.add(new BasicDBObject(FieldType.ACQUIRER_TYPE.getName(), acq));
				}
				acquirerQuery.append("$or", acquirerConditionLst);
			}

			if (!paymentType.equalsIgnoreCase("ALL")) {
				paramConditionLst.add(new BasicDBObject(FieldType.PAYMENT_TYPE.getName(), paymentType));
			} else {
			}

			if (transactionType.equalsIgnoreCase("ALL")) {

				List<BasicDBObject> saleConditionList = new ArrayList<BasicDBObject>();
				saleConditionList.add(new BasicDBObject(FieldType.TXNTYPE.getName(), TransactionType.SALE.getName()));
//				Done By chetan nagaria for change in settlement process to mark transaction as RNS
				saleConditionList.add(new BasicDBObject(FieldType.STATUS.getName(), StatusType.SETTLED_SETTLE.getName()));
				saleConditionList
						.add(new BasicDBObject(FieldType.RESPONSE_CODE.getName(), ErrorType.SUCCESS.getCode()));

				BasicDBObject saleConditionQuery = new BasicDBObject("$and", saleConditionList);

				List<BasicDBObject> authConditionList = new ArrayList<BasicDBObject>();
				authConditionList
						.add(new BasicDBObject(FieldType.TXNTYPE.getName(), TransactionType.AUTHORISE.getName()));
				authConditionList.add(new BasicDBObject(FieldType.STATUS.getName(), StatusType.APPROVED.getName()));

				BasicDBObject authConditionQuery = new BasicDBObject("$and", authConditionList);

				List<BasicDBObject> recoConditionList = new ArrayList<BasicDBObject>();
				recoConditionList.add(new BasicDBObject(FieldType.TXNTYPE.getName(), TransactionType.RECO.getName()));
//				Done By chetan nagaria for change in settlement process to mark transaction as RNS
				recoConditionList.add(new BasicDBObject(FieldType.STATUS.getName(), StatusType.SETTLED_SETTLE.getName()));

				BasicDBObject recoConditionQuery = new BasicDBObject("$and", recoConditionList);

				List<BasicDBObject> refundConditionList = new ArrayList<BasicDBObject>();
				refundConditionList
						.add(new BasicDBObject(FieldType.TXNTYPE.getName(), TransactionType.REFUND.getName()));
//				Done By chetan nagaria for change in settlement process to mark transaction as RNS
				refundConditionList.add(new BasicDBObject(FieldType.STATUS.getName(), StatusType.SETTLED_SETTLE.getName()));

				BasicDBObject refundConditionQuery = new BasicDBObject("$and", refundConditionList);

				List<BasicDBObject> recoRefundConditionList = new ArrayList<BasicDBObject>();
				recoRefundConditionList
						.add(new BasicDBObject(FieldType.TXNTYPE.getName(), TransactionType.REFUNDRECO.getName()));
//				Done By chetan nagaria for change in settlement process to mark transaction as RNS
				recoRefundConditionList
						.add(new BasicDBObject(FieldType.STATUS.getName(), StatusType.SETTLED_SETTLE.getName()));

				BasicDBObject recoRefundConditionQuery = new BasicDBObject("$and", recoRefundConditionList);

				saleOrAuthList.add(saleConditionQuery);
				saleOrAuthList.add(authConditionQuery);
				saleOrAuthList.add(recoConditionQuery);
				saleOrAuthList.add(refundConditionQuery);
				saleOrAuthList.add(recoRefundConditionQuery);

			}

			else if (transactionType.equalsIgnoreCase("SALE")) {

				List<BasicDBObject> saleConditionList = new ArrayList<BasicDBObject>();
				saleConditionList.add(new BasicDBObject(FieldType.TXNTYPE.getName(), TransactionType.SALE.getName()));
//				Done By chetan nagaria for change in settlement process to mark transaction as RNS
				saleConditionList.add(new BasicDBObject(FieldType.STATUS.getName(), StatusType.SETTLED_SETTLE.getName()));
				saleConditionList
						.add(new BasicDBObject(FieldType.RESPONSE_CODE.getName(), ErrorType.SUCCESS.getCode()));

				BasicDBObject saleConditionQuery = new BasicDBObject("$and", saleConditionList);

				List<BasicDBObject> authConditionList = new ArrayList<BasicDBObject>();
				authConditionList
						.add(new BasicDBObject(FieldType.TXNTYPE.getName(), TransactionType.AUTHORISE.getName()));
				authConditionList.add(new BasicDBObject(FieldType.STATUS.getName(), StatusType.APPROVED.getName()));

				BasicDBObject authConditionQuery = new BasicDBObject("$and", authConditionList);

				List<BasicDBObject> recoConditionList = new ArrayList<BasicDBObject>();
				recoConditionList.add(new BasicDBObject(FieldType.TXNTYPE.getName(), TransactionType.RECO.getName()));
//				Done By chetan nagaria for change in settlement process to mark transaction as RNS
				recoConditionList.add(new BasicDBObject(FieldType.STATUS.getName(), StatusType.SETTLED_SETTLE.getName()));

				BasicDBObject recoConditionQuery = new BasicDBObject("$and", recoConditionList);

				saleOrAuthList.add(saleConditionQuery);
				saleOrAuthList.add(authConditionQuery);
				saleOrAuthList.add(recoConditionQuery);

			}

			else {

				List<BasicDBObject> refundConditionList = new ArrayList<BasicDBObject>();
				refundConditionList
						.add(new BasicDBObject(FieldType.TXNTYPE.getName(), TransactionType.REFUND.getName()));
//				Done By chetan nagaria for change in settlement process to mark transaction as RNS
				refundConditionList.add(new BasicDBObject(FieldType.STATUS.getName(), StatusType.SETTLED_SETTLE.getName()));

				BasicDBObject refundConditionQuery = new BasicDBObject("$and", refundConditionList);

				List<BasicDBObject> recoRefundConditionList = new ArrayList<BasicDBObject>();
				recoRefundConditionList
						.add(new BasicDBObject(FieldType.TXNTYPE.getName(), TransactionType.REFUNDRECO.getName()));
//				Done By chetan nagaria for change in settlement process to mark transaction as RNS
				recoRefundConditionList
						.add(new BasicDBObject(FieldType.STATUS.getName(), StatusType.SETTLED_SETTLE.getName()));

				BasicDBObject recoRefundConditionQuery = new BasicDBObject("$and", recoRefundConditionList);

				saleOrAuthList.add(refundConditionQuery);
				saleOrAuthList.add(recoRefundConditionQuery);

			}

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
			
			if (orderId.isEmpty() && pgRefNum.isEmpty()) {

				if (!dateQuery.isEmpty()) {
					allConditionQueryList.add(dateQuery);
				}
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

			MongoDatabase dbIns = mongoInstance.getDB();
			MongoCollection<Document> coll = dbIns
					.getCollection(PropertiesManager.propertiesMap.get(prefix + Constants.COLLECTION_NAME.getValue()));

			BasicDBObject match = new BasicDBObject("$match", finalquery);
			Document firstGroup = new Document("_id", new Document("PG_REF_NUM", "$PG_REF_NUM").append("STATUS", "$STATUS"));
            BasicDBObject firstGroupObject = new BasicDBObject(firstGroup);
            BasicDBObject secondGroup = new BasicDBObject("$last", "$$ROOT");
            BasicDBObject groupQuery = new BasicDBObject("$group", firstGroupObject.append("contentList", secondGroup));
            return coll.aggregate(Arrays.asList(match, groupQuery)).into(new ArrayList<>()).size();
		}

		catch (Exception e) {
			logger.error("Exception " + e);
		}
		return total;

	}

	public List<TransactionSearch> summaryReportRecordMerchant(String fromDate, String toDate, String payId,
			String paymentType, String currency, User user) throws SystemException {

		List<TransactionSearch> transactionList = new ArrayList<TransactionSearch>();

		try {
			BasicDBObject dateQuery = new BasicDBObject();
			List<BasicDBObject> paramConditionLst = new ArrayList<BasicDBObject>();
			BasicDBObject currencyQuery = new BasicDBObject();
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
			if (!payId.equalsIgnoreCase("ALL")) {
				paramConditionLst.add(new BasicDBObject(FieldType.PAY_ID.getName(), payId));
			}
			if (!currency.equalsIgnoreCase("ALL")) {
				paramConditionLst.add(new BasicDBObject(FieldType.CURRENCY_CODE.getName(), currency));
			}

			if (!paymentType.equalsIgnoreCase("ALL")) {
				paramConditionLst.add(new BasicDBObject(FieldType.PAYMENT_TYPE.getName(), paymentType));
			}
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
			MongoCollection<Document> coll = dbIns
					.getCollection(PropertiesManager.propertiesMap.get(prefix + Constants.COLLECTION_NAME.getValue()));

			// create our pipeline operations, first with the $match
			MongoCursor<Document> cursor = coll.find(finalquery).iterator();
			while (cursor.hasNext()) {
				Document doc = cursor.next();
				TransactionSearch transReport = new TransactionSearch();
				transReport.setCustomerName(doc.getString(FieldType.CARD_HOLDER_NAME.toString()));
				BigInteger txnId = new BigInteger(doc.getString(FieldType.TXN_ID.toString()));
				transReport.setTransactionId(txnId);
				transReport.setTransactionIdString(String.valueOf(txnId));
				transReport.setDateFrom(doc.getString(FieldType.CREATE_DATE.getName()));
				transReport.setInternalCardIssusserBank(doc.getString(FieldType.INTERNAL_CARD_ISSUER_BANK.toString()));
				transReport.setInternalCardIssusserCountry(
						doc.getString(FieldType.INTERNAL_CARD_ISSUER_COUNTRY.toString()));
				if (null != doc.getString(FieldType.CURRENCY_CODE.toString())) {
					transReport.setCurrency(propertiesManager
							.getAlphabaticCurrencyCode(doc.getString(FieldType.CURRENCY_CODE.toString())));
				} else {
					transReport.setCurrency(CrmFieldConstants.NA.getValue());
				}
				transReport.setPayId(doc.getString(FieldType.PAY_ID.toString()));
				transReport.setPgRefNum(doc.getString(FieldType.PG_REF_NUM.toString()));
				transReport.setPaymentMethods(doc.getString(FieldType.PAYMENT_TYPE.toString()));
				transReport.setTransactionCaptureDate(doc.getString(FieldType.PG_DATE_TIME.toString()));
				transReport.setOrderId(doc.getString(FieldType.ORDER_ID.toString()));
				String surchargeFlag = doc.getString(FieldType.SURCHARGE_FLAG.toString());
				transReport.setSurchargeFlag(surchargeFlag);
				if (!StringUtils.isBlank(surchargeFlag)) {
					if (surchargeFlag.equalsIgnoreCase("Y")) {
						transReport.setAmount(doc.getString(FieldType.TOTAL_AMOUNT.toString()));

					} else {
						transReport.setAmount(doc.getString(FieldType.AMOUNT.toString()));
					}
				} else {
					transReport.setAmount(doc.getString(FieldType.AMOUNT.toString()));
				}
				transReport.setAcquirerType(doc.getString(FieldType.ACQUIRER_TYPE.toString()));
				transReport.setMopType(doc.getString(FieldType.MOP_TYPE.toString()));
				transReport.setTxnType(doc.getString(FieldType.TXNTYPE.toString()));

				transReport.setCurrency(doc.getString(FieldType.CURRENCY_CODE.toString()));
				transactionList.add(transReport);
			}
			cursor.close();

		} catch (Exception e) {
			logger.error("Exception " + e);
		}
		return transactionList;

	}

	public List<TransactionSearch> summaryReportMerchant(String fromDate, String toDate, String payId,
			String paymentType, String currency, User user, int start, int length) throws SystemException {

		double bankTdr = 0;
		double PgFixCharge = 0;
		double pgTdr = 0;
		double bankFixCharge = 0;
		double merchantFixCharge = 0;
		double merchantTdr = 0;
		double merchantServiceTax = 0;

		TdrPojo tdrPojo = new TdrPojo();
		List<TransactionSearch> transactionList = new ArrayList<TransactionSearch>();

		try {
			BasicDBObject dateQuery = new BasicDBObject();
			List<BasicDBObject> paramConditionLst = new ArrayList<BasicDBObject>();
			BasicDBObject currencyQuery = new BasicDBObject();
			BasicDBObject acquirerQuery = new BasicDBObject();
			BasicDBObject allParamQuery = new BasicDBObject();
			List<BasicDBObject> acquirerConditionLst = new ArrayList<BasicDBObject>();
			List<BasicDBObject> userTypeLst = new ArrayList<BasicDBObject>();
			List<BasicDBObject> currencyConditionLst = new ArrayList<BasicDBObject>();
			List<BasicDBObject> paymentTypeConditionLst = new ArrayList<BasicDBObject>();
			BasicDBObject paymentTypeQuery = new BasicDBObject();

			String currentDate = null;
			if (!fromDate.isEmpty()) {

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
				dateIndexConditionList.add(new BasicDBObject("PG_DATE_TIME_INDEX", dateIndex));
			}
			if (StringUtils.isNotBlank(PropertiesManager.propertiesMap.get(Constants.USE_PG_DATE_TIME_INDEX.getValue()))
					&& PropertiesManager.propertiesMap.get(Constants.USE_PG_DATE_TIME_INDEX.getValue())
							.equalsIgnoreCase("Y")) {
				dateIndexConditionQuery.append("$or", dateIndexConditionList);
			}
			if (!payId.equalsIgnoreCase("ALL")) {
				paramConditionLst.add(new BasicDBObject(FieldType.PAY_ID.getName(), payId));
			}
			if (!currency.equalsIgnoreCase("ALL")) {
				paramConditionLst.add(new BasicDBObject(FieldType.CURRENCY_CODE.getName(), currency));
			} else {
				PropertiesManager propertiesManager = new PropertiesManager();
				Map<String, String> allCurrencyMap;
				allCurrencyMap = propertiesManager.getAllProperties(alphabaticFileName);
				for (Map.Entry<String, String> entry : allCurrencyMap.entrySet()) {

					currencyConditionLst.add(new BasicDBObject(FieldType.CURRENCY_CODE.getName(), entry.getKey()));
				}

				currencyQuery.append("$or", currencyConditionLst);
			}

			if (!paymentType.equalsIgnoreCase("ALL")) {
				paramConditionLst.add(new BasicDBObject(FieldType.PAYMENT_TYPE.getName(), paymentType));
			} else {
				paymentTypeConditionLst
						.add(new BasicDBObject(FieldType.PAYMENT_TYPE.getName(), PaymentType.CREDIT_CARD.getCode()));
				paymentTypeConditionLst
						.add(new BasicDBObject(FieldType.PAYMENT_TYPE.getName(), PaymentType.DEBIT_CARD.getCode()));
				paymentTypeConditionLst
						.add(new BasicDBObject(FieldType.PAYMENT_TYPE.getName(), PaymentType.NET_BANKING.getCode()));
//				paymentTypeConditionLst
//						.add(new BasicDBObject(FieldType.PAYMENT_TYPE.getName(), PaymentType.EMI.getCode()));
				paymentTypeConditionLst
						.add(new BasicDBObject(FieldType.PAYMENT_TYPE.getName(), PaymentType.WALLET.getCode()));
//				paymentTypeConditionLst.add(
//						new BasicDBObject(FieldType.PAYMENT_TYPE.getName(), PaymentType.RECURRING_PAYMENT.getCode()));
				paymentTypeConditionLst
						.add(new BasicDBObject(FieldType.PAYMENT_TYPE.getName(), PaymentType.EXPRESS_PAY.getCode()));
				paymentTypeConditionLst.add(new BasicDBObject(FieldType.PAYMENT_TYPE.getName(), null));
				paymentTypeQuery.append("$or", paymentTypeConditionLst);
			}

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
			MongoCollection<Document> coll = dbIns
					.getCollection(PropertiesManager.propertiesMap.get(prefix + Constants.COLLECTION_NAME.getValue()));
			BasicDBObject match = new BasicDBObject("$match", finalquery);

			BasicDBObject sort = new BasicDBObject("$sort", new BasicDBObject("CREATE_DATE", -1));
			BasicDBObject skip = new BasicDBObject("$skip", start);
			BasicDBObject limit = new BasicDBObject("$limit", length);

			// run aggregation

			List<BasicDBObject> pipeline = Arrays.asList(match, sort, skip, limit);
			AggregateIterable<Document> output = coll.aggregate(pipeline);
			output.allowDiskUse(true);
			MongoCursor<Document> cursor = output.iterator();
			// create our pipeline operations, first with the $match
			// MongoCursor<Document> cursor = coll.find(finalquery).iterator();
			while (cursor.hasNext()) {
				Document doc = cursor.next();
				TransactionSearch transReport = new TransactionSearch();
				transReport.setCustomerName(doc.getString(FieldType.CARD_HOLDER_NAME.toString()));
				BigInteger txnId = new BigInteger(doc.getString(FieldType.TXN_ID.toString()));
				transReport.setTransactionId(txnId);
				transReport.setTransactionIdString(String.valueOf(txnId));
				transReport.setDateFrom(doc.getString(FieldType.CREATE_DATE.getName()));
				transReport.setInternalCardIssusserBank(doc.getString(FieldType.INTERNAL_CARD_ISSUER_BANK.toString()));
				transReport.setInternalCardIssusserCountry(
						doc.getString(FieldType.INTERNAL_CARD_ISSUER_COUNTRY.toString()));
				if (null != doc.getString(FieldType.CURRENCY_CODE.toString())) {
					transReport.setCurrency(propertiesManager
							.getAlphabaticCurrencyCode(doc.getString(FieldType.CURRENCY_CODE.toString())));
				} else {
					transReport.setCurrency(CrmFieldConstants.NA.getValue());
				}
				transReport.setPayId(doc.getString(FieldType.PAY_ID.toString()));
				transReport.setPgRefNum(doc.getString(FieldType.PG_REF_NUM.toString()));
				transReport.setPaymentMethods(doc.getString(FieldType.PAYMENT_TYPE.toString()));
				transReport.setTransactionCaptureDate(doc.getString(FieldType.PG_DATE_TIME.toString()));
				transReport.setOrderId(doc.getString(FieldType.ORDER_ID.toString()));
				String surchargeFlag = doc.getString(FieldType.SURCHARGE_FLAG.toString());
				transReport.setSurchargeFlag(surchargeFlag);
				if (!StringUtils.isBlank(surchargeFlag)) {
					if (surchargeFlag.equalsIgnoreCase("Y")) {
						transReport.setAmount(doc.getString(FieldType.TOTAL_AMOUNT.toString()));

					} else {
						transReport.setAmount(doc.getString(FieldType.AMOUNT.toString()));
					}
				} else {
					transReport.setAmount(doc.getString(FieldType.AMOUNT.toString()));
				}
				transReport.setAcquirerType(doc.getString(FieldType.ACQUIRER_TYPE.toString()));
				transReport.setMopType(doc.getString(FieldType.MOP_TYPE.toString()));
				transReport.setTxnType(doc.getString(FieldType.TXNTYPE.toString()));
				transReport.setCurrency(doc.getString(FieldType.CURRENCY_CODE.toString()));
				transactionList.add(transReport);
			}
		} catch (Exception e) {
			logger.error("Exception " + e);
		}
		return transactionList;

	}

	public List<SummaryObject> summaryReportDownload(String fromDate, String toDate, String payId, String paymentType,
			String acquirer, String currency, User user, String paymentsRegion, String cardHolderType, String pgRefNum,
			String mopType, String transactionType) throws SystemException {
		List<SummaryObject> transactionList = new ArrayList<SummaryObject>();

		logger.info("Inside SummaryReportQuery summaryReportDownload");

		try {

			BasicDBObject dateQuery = new BasicDBObject();
			List<BasicDBObject> paramConditionLst = new ArrayList<BasicDBObject>();
			BasicDBObject currencyQuery = new BasicDBObject();
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

				dateQuery.put(FieldType.PG_DATE_TIME.getName(),
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
				dateIndexConditionList.add(new BasicDBObject("PG_DATE_TIME_INDEX", dateIndex));
			}
			if (StringUtils.isNotBlank(PropertiesManager.propertiesMap.get(Constants.USE_PG_DATE_TIME_INDEX.getValue()))
					&& PropertiesManager.propertiesMap.get(Constants.USE_PG_DATE_TIME_INDEX.getValue())
							.equalsIgnoreCase("Y")) {
				dateIndexConditionQuery.append("$or", dateIndexConditionList);
			}
//			Done By chetan nagaria for change in settlement process to mark transaction as RNS
			paramConditionLst.add(new BasicDBObject(FieldType.STATUS.getName(), StatusType.SETTLED_SETTLE.getName()));

			if (!payId.equalsIgnoreCase("ALL") && StringUtils.isNotBlank(payId)) {
				paramConditionLst.add(new BasicDBObject(FieldType.PAY_ID.getName(), payId));
			}

			if (!paymentsRegion.equalsIgnoreCase("ALL")) {
				paramConditionLst.add(new BasicDBObject(FieldType.PAYMENTS_REGION.getName(), paymentsRegion));
			}

			if (!cardHolderType.equalsIgnoreCase("ALL")) {
				paramConditionLst.add(new BasicDBObject(FieldType.CARD_HOLDER_TYPE.getName(), cardHolderType));
			}

			if (!mopType.equalsIgnoreCase("ALL")) {
				paramConditionLst.add(new BasicDBObject(FieldType.MOP_TYPE.getName(), mopType));
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
			if (!paymentType.equalsIgnoreCase("ALL") && StringUtils.isNotBlank(paymentType)) {
				paramConditionLst.add(new BasicDBObject(FieldType.PAYMENT_TYPE.getName(), paymentType));
			}

			if (transactionType.equalsIgnoreCase("ALL")) {

			}

			else if (transactionType.equalsIgnoreCase("SALE")) {

				List<BasicDBObject> recoConditionList = new ArrayList<BasicDBObject>();
				recoConditionList.add(new BasicDBObject(FieldType.TXNTYPE.getName(), TransactionType.RECO.getName()));
//				Done By chetan nagaria for change in settlement process to mark transaction as RNS
				recoConditionList.add(new BasicDBObject(FieldType.STATUS.getName(), StatusType.SETTLED_SETTLE.getName()));

				BasicDBObject recoConditionQuery = new BasicDBObject("$and", recoConditionList);

				saleOrAuthList.add(recoConditionQuery);

			}

			else {

				List<BasicDBObject> recoRefundConditionList = new ArrayList<BasicDBObject>();
				recoRefundConditionList
						.add(new BasicDBObject(FieldType.TXNTYPE.getName(), TransactionType.REFUNDRECO.getName()));
//				Done By chetan nagaria for change in settlement process to mark transaction as RNS
				recoRefundConditionList
						.add(new BasicDBObject(FieldType.STATUS.getName(), StatusType.SETTLED_SETTLE.getName()));

				BasicDBObject recoRefundConditionQuery = new BasicDBObject("$and", recoRefundConditionList);

				saleOrAuthList.add(recoRefundConditionQuery);

			}

			BasicDBObject authndSaleConditionQuery = new BasicDBObject();
			if (saleOrAuthList.size() > 0) {
				authndSaleConditionQuery.put("$or", saleOrAuthList);
			}

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
			logger.info("finalquery processing for downloadSummaryReport = " + finalquery);
			MongoDatabase dbIns = mongoInstance.getDB();
			MongoCollection<Document> coll = dbIns
					.getCollection(PropertiesManager.propertiesMap.get(prefix + Constants.COLLECTION_NAME.getValue()));
			BasicDBObject match = new BasicDBObject("$match", finalquery);

			List<BasicDBObject> pipeline = Arrays.asList(match);
			AggregateIterable<Document> output = coll.aggregate(pipeline);
			output.allowDiskUse(true);
			MongoCursor<Document> cursor = output.iterator();
			logger.info("finalquery processed for downloadSummaryReport = " + finalquery);
			int counter = 1;
			int total = 1;
			while (cursor.hasNext()) {
				counter = counter + 1;
				total = total + 1;
				Document doc = cursor.next();

				if (counter == 3000) {
					logger.info("generating summary report , add fields = " + total);
					counter = 0;
				}

				SummaryObject transReport = new SummaryObject();
				transReport.setTransactionId(doc.getString(FieldType.TXN_ID.toString()));
				transReport.setDateFrom(doc.getString(FieldType.CREATE_DATE.getName()));
				transReport.setSrNo(String.valueOf(total));
				transReport.setCurrency("INR");
				transReport.setOrderId(doc.getString(FieldType.ORDER_ID.toString()));
				transReport.setPgRefNum(doc.getString(FieldType.PG_REF_NUM.toString()));
				if (StringUtils.isBlank(doc.getString(FieldType.PAYMENTS_REGION.toString()))) {
					transReport.setTransactionRegion(AccountCurrencyRegion.DOMESTIC.toString());
				} else {
					transReport.setTransactionRegion(doc.getString(FieldType.PAYMENTS_REGION.toString()));
				}

				if (StringUtils.isBlank(doc.getString(FieldType.CARD_HOLDER_TYPE.toString()))) {

					transReport.setCardHolderType(CardHolderType.CONSUMER.toString());
				} else {
					transReport.setCardHolderType(doc.getString(FieldType.CARD_HOLDER_TYPE.toString()));
				}

				transReport.setTotalAmount(String.valueOf(doc.getString(FieldType.TOTAL_AMOUNT.toString())));
				transReport.setAcquirerType(doc.getString(FieldType.ACQUIRER_TYPE.toString()));

				if (null != doc.getString(FieldType.MOP_TYPE.toString())) {
					transReport.setMopType(MopType.getmopName(doc.getString(FieldType.MOP_TYPE.toString())));
				} else {
					transReport.setMopType(CrmFieldConstants.NOT_AVAILABLE.getValue());
				}

				if (null != doc.getString(FieldType.PAYMENT_TYPE.toString())) {
					transReport.setPaymentMethods(doc.getString(FieldType.PAYMENT_TYPE.toString()));
				} else {
					transReport.setPaymentMethods(CrmFieldConstants.NOT_AVAILABLE.getValue());
				}
				transReport.setCaptureDate(doc.getString(FieldType.PG_DATE_TIME.toString()));
				transReport.setTxnType(doc.getString(FieldType.TXNTYPE.toString()));
				transReport.setMerchants(doc.getString(CrmFieldType.BUSINESS_NAME.getName()));
				transReport.setAcqId(doc.getString(FieldType.ACQ_ID.toString()));
				transReport.setRrn(doc.getString(FieldType.RRN.toString()));
				transReport.setPostSettledFlag(doc.getString(FieldType.POST_SETTLED_FLAG.toString()));
				transReport.setDateFrom(doc.getString(FieldType.CREATE_DATE.toString()));

				// Refund values to be shown as negative in Summary Report
				if (doc.getString(FieldType.TXNTYPE.toString()).contains(TransactionType.REFUNDRECO.getName())) {
					transReport.setTdrScAcquirer(
							String.valueOf(Double.valueOf(doc.getString(FieldType.ACQUIRER_TDR_SC.getName())) * -1));
					transReport.setGstScAcquirer(
							String.valueOf(Double.valueOf(doc.getString(FieldType.ACQUIRER_GST.getName())) * -1));
					transReport.setTdrScIpay(
							String.valueOf(Double.valueOf(doc.getString(FieldType.PG_TDR_SC.getName())) * -1));
					transReport.setGstScIpay(
							String.valueOf(Double.valueOf(doc.getString(FieldType.PG_GST.getName())) * -1));
					transReport
							.setAmount(String.valueOf(Double.valueOf(doc.getString(FieldType.AMOUNT.getName())) * -1));
				}

				// Sale values to be shown as positive in Summary Report
				else {
					transReport.setTdrScAcquirer(
							String.valueOf((Double.valueOf(doc.getString(FieldType.ACQUIRER_TDR_SC.getName())) * 1)));
					transReport.setGstScAcquirer(
							String.valueOf(Double.valueOf(doc.getString(FieldType.ACQUIRER_GST.getName())) * 1));
					transReport.setTdrScIpay(
							String.valueOf(Double.valueOf(doc.getString(FieldType.PG_TDR_SC.getName())) * 1));
					transReport.setGstScIpay(
							String.valueOf(Double.valueOf(doc.getString(FieldType.PG_GST.getName())) * 1));
					transReport
							.setAmount(String.valueOf(Double.valueOf(doc.getString(FieldType.AMOUNT.getName())) * 1));
				}

				if (userMap.get(doc.getString(FieldType.PAY_ID.toString())) != null) {
					User userThis = userMap.get(doc.getString(FieldType.PAY_ID.toString()));
					transReport.setMerchants(userThis.getBusinessName());
				} else {
					User userThis = userdao.findPayId(doc.getString(FieldType.PAY_ID.toString()));
					transReport.setMerchants(userThis.getBusinessName());
					userMap.put(userThis.getPayId(), userThis);
				}
				transactionList.add(transReport);
			}

			cursor.close();

		} catch (Exception exception) {
			logger.error("Exception in summary report download ", exception);
		}
		return transactionList;
	}

	public List<SummaryReportNodalObject> summaryReportNodalDownload(String fromDate, String toDate, String payId,
			String paymentType, String acquirer, String currency, User user, String paymentsRegion,
			String cardHolderType, String pgRefNum, String mopType, String transactionType, String settlementStatus)
			throws SystemException {
		List<SummaryReportNodalObject> transactionList = new ArrayList<SummaryReportNodalObject>();

		logger.info("Inside SummaryReportQuery summaryReportNodalDownload");
		Map<String, User> userMap = new HashMap<String, User>();

		try {

			BasicDBObject dateQuery = new BasicDBObject();
			List<BasicDBObject> paramConditionLst = new ArrayList<BasicDBObject>();
			BasicDBObject currencyQuery = new BasicDBObject();
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
			if (StringUtils
					.isNotBlank(PropertiesManager.propertiesMap.get(Constants.USE_CAPTURED_DATE_INDEX.getValue()))
					&& PropertiesManager.propertiesMap.get(Constants.USE_CAPTURED_DATE_INDEX.getValue())
							.equalsIgnoreCase("Y")) {
				dateIndexConditionQuery.append("$or", dateIndexConditionList);
			}

			if (!payId.equalsIgnoreCase("ALL")) {
				paramConditionLst.add(new BasicDBObject(FieldType.PAY_ID.getName(), payId));
			}

			if (!paymentsRegion.equalsIgnoreCase("ALL")) {
				paramConditionLst.add(new BasicDBObject(FieldType.PAYMENTS_REGION.getName(), paymentsRegion));
			}

			if (!cardHolderType.equalsIgnoreCase("ALL")) {
				paramConditionLst.add(new BasicDBObject(FieldType.CARD_HOLDER_TYPE.getName(), cardHolderType));
			}

			if (!mopType.equalsIgnoreCase("ALL")) {
				paramConditionLst.add(new BasicDBObject(FieldType.MOP_TYPE.getName(), mopType));
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
			if (!paymentType.equalsIgnoreCase("ALL")) {
				paramConditionLst.add(new BasicDBObject(FieldType.PAYMENT_TYPE.getName(), paymentType));
			}

			if (transactionType.equalsIgnoreCase("ALL")) {

				List<BasicDBObject> saleConditionList = new ArrayList<BasicDBObject>();
				saleConditionList.add(new BasicDBObject(FieldType.TXNTYPE.getName(), TransactionType.SALE.getName()));
				saleConditionList.add(new BasicDBObject(FieldType.STATUS.getName(), StatusType.CAPTURED.getName()));

				BasicDBObject saleConditionQuery = new BasicDBObject("$and", saleConditionList);

				List<BasicDBObject> refundConditionList = new ArrayList<BasicDBObject>();
				refundConditionList
						.add(new BasicDBObject(FieldType.TXNTYPE.getName(), TransactionType.REFUND.getName()));
				refundConditionList.add(new BasicDBObject(FieldType.STATUS.getName(), StatusType.CAPTURED.getName()));

				BasicDBObject refundConditionQuery = new BasicDBObject("$and", refundConditionList);

				saleOrAuthList.add(saleConditionQuery);
				saleOrAuthList.add(refundConditionQuery);

			}

			else if (transactionType.equalsIgnoreCase("SALE")) {

				List<BasicDBObject> saleConditionList = new ArrayList<BasicDBObject>();
				saleConditionList.add(new BasicDBObject(FieldType.TXNTYPE.getName(), TransactionType.SALE.getName()));
				saleConditionList.add(new BasicDBObject(FieldType.STATUS.getName(), StatusType.CAPTURED.getName()));

				BasicDBObject saleConditionQuery = new BasicDBObject("$and", saleConditionList);

				saleOrAuthList.add(saleConditionQuery);

			}

			else {

				List<BasicDBObject> refundConditionList = new ArrayList<BasicDBObject>();
				refundConditionList
						.add(new BasicDBObject(FieldType.TXNTYPE.getName(), TransactionType.REFUND.getName()));
				refundConditionList.add(new BasicDBObject(FieldType.STATUS.getName(), StatusType.CAPTURED.getName()));

				BasicDBObject refundConditionQuery = new BasicDBObject("$and", refundConditionList);

				saleOrAuthList.add(refundConditionQuery);

			}

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
			logger.info("finalquery processing for downloadSummaryReport = " + finalquery);
			MongoDatabase dbIns = mongoInstance.getDB();
			MongoCollection<Document> coll = dbIns.getCollection(
					PropertiesManager.propertiesMap.get(prefix + Constants.SUMMARY_TRANSACTIONS_NAME.getValue()));
			BasicDBObject match = new BasicDBObject("$match", finalquery);

			List<BasicDBObject> pipeline = Arrays.asList(match);
			AggregateIterable<Document> output = coll.aggregate(pipeline);
			output.allowDiskUse(true);
			MongoCursor<Document> cursor = output.iterator();
			logger.info("finalquery processed for downloadSummaryReport = " + finalquery);
			while (cursor.hasNext()) {
				Document doc = cursor.next();

				if (settlementStatus.equalsIgnoreCase("Settled Only")) {
					if (StringUtils.isBlank(doc.getString(FieldType.SETTLEMENT_DATE.toString()))) {
						continue;
					}
				} else if (settlementStatus.equalsIgnoreCase("All Not Settled")) {
					if (StringUtils.isNotBlank(doc.getString(FieldType.SETTLEMENT_DATE.toString()))) {
						continue;
					}
				} else {

				}

				SummaryReportNodalObject transReport = new SummaryReportNodalObject();
				transReport.setTransactionId(doc.getString(FieldType.TXN_ID.toString()));
				transReport.setCaptureDate(doc.getString(FieldType.CAPTURED_DATE.getName()));
				transReport.setCurrency("356");
				if (StringUtils.isBlank(doc.getString(FieldType.SETTLEMENT_DATE.toString()))) {

					transReport.setSettlementDate("");
				} else {
					transReport.setSettlementDate(doc.getString(FieldType.SETTLEMENT_DATE.toString()));
				}

				if (StringUtils.isBlank(doc.getString(FieldType.NODAL_CREDIT_DATE.toString()))) {

					transReport.setNodalCreditDate("");
				} else {
					transReport.setNodalCreditDate(doc.getString(FieldType.NODAL_CREDIT_DATE.toString()));
				}

				if (StringUtils.isBlank(doc.getString(FieldType.NODAL_PAYOUT_INITIATED_DATE.toString()))) {

					transReport.setNodalPayoutInitiationDate("");
				} else {
					transReport.setNodalPayoutInitiationDate(
							doc.getString(FieldType.NODAL_PAYOUT_INITIATED_DATE.toString()));
				}

				if (StringUtils.isBlank(doc.getString(FieldType.NODAL_PAYOUT_DATE.toString()))) {

					transReport.setNodalPayoutDate("");
				} else {
					transReport.setNodalPayoutDate(doc.getString(FieldType.NODAL_PAYOUT_DATE.toString()));
				}

				transReport.setOrderId(doc.getString(FieldType.ORDER_ID.toString()));
				String surchargeFlag = doc.getString(FieldType.SURCHARGE_FLAG.toString());
				if (StringUtils.isNotBlank(surchargeFlag)) {
					transReport.setSurchargeFlag(surchargeFlag);
				}

				transReport.setPgRefNum(doc.getString(FieldType.PG_REF_NUM.toString()));
				if (StringUtils.isBlank(doc.getString(FieldType.PAYMENTS_REGION.toString()))) {

					transReport.setTransactionRegion(AccountCurrencyRegion.DOMESTIC.toString());
				} else {
					transReport.setTransactionRegion(doc.getString(FieldType.PAYMENTS_REGION.toString()));
				}

				if (StringUtils.isBlank(doc.getString(FieldType.CARD_HOLDER_TYPE.toString()))) {

					transReport.setCardHolderType(CardHolderType.CONSUMER.toString());
				} else {
					transReport.setCardHolderType(doc.getString(FieldType.CARD_HOLDER_TYPE.toString()));
				}

				transReport.setTotalAmount(String.valueOf(doc.getDouble(FieldType.TOTAL_AMOUNT.toString())));
				transReport.setAcquirerType(doc.getString(FieldType.ACQUIRER_TYPE.toString()));

				if (null != doc.getString(FieldType.MOP_TYPE.toString())) {
					transReport.setMopType(MopType.getmopName(doc.getString(FieldType.MOP_TYPE.toString())));
				} else {
					transReport.setMopType(CrmFieldConstants.NOT_AVAILABLE.getValue());
				}

				if (null != doc.getString(FieldType.PAYMENT_TYPE.toString())) {
					transReport.setPaymentMethods(doc.getString(FieldType.PAYMENT_TYPE.toString()));
				} else {
					transReport.setPaymentMethods(CrmFieldConstants.NOT_AVAILABLE.getValue());
				}

				transReport.setTxnType(doc.getString(FieldType.TXNTYPE.toString()));
				transReport.setMerchants(doc.getString(CrmFieldType.BUSINESS_NAME.getName()));
				transReport.setAcqId(doc.getString(FieldType.ACQ_ID.toString()));
				transReport.setRrn(doc.getString(FieldType.RRN.toString()));
				transReport.setPostSettledFlag(doc.getString(FieldType.POST_SETTLED_FLAG.toString()));

				if (doc.getString(FieldType.TXNTYPE.toString()).contains(TxnType.REFUND.getName())) {
					transReport
							.setTdrScAcquirer(String.valueOf(doc.getDouble(FieldType.SURCHARGE_ACQ.toString()) * -1));
					transReport.setGstScAcquirer(String.valueOf(doc.getDouble(FieldType.GST_ACQ.toString()) * -1));
					transReport.setTdrScIpay(String.valueOf(doc.getDouble(FieldType.SURCHARGE_IPAY.toString()) * -1));
					transReport.setGstScIpay(String.valueOf(doc.getDouble(FieldType.GST_IPAY.toString()) * -1));
					transReport.setAmount(String.valueOf(doc.getDouble(FieldType.AMOUNT.toString()) * -1));
				} else {
					transReport.setTdrScAcquirer(String.valueOf(doc.getDouble(FieldType.SURCHARGE_ACQ.toString())));
					transReport.setGstScAcquirer(String.valueOf(doc.getDouble(FieldType.GST_ACQ.toString())));
					transReport.setTdrScIpay(String.valueOf(doc.getDouble(FieldType.SURCHARGE_IPAY.toString())));
					transReport.setGstScIpay(String.valueOf(doc.getDouble(FieldType.GST_IPAY.toString())));
					transReport.setAmount(String.valueOf(doc.getDouble(FieldType.AMOUNT.toString())));
				}

				if (userMap.get(doc.getString(FieldType.PAY_ID.toString())) != null) {
					User userThis = userMap.get(doc.getString(FieldType.PAY_ID.toString()));
					transReport.setMerchants(userThis.getBusinessName());
				} else {
					User userThis = userdao.findPayId(doc.getString(FieldType.PAY_ID.toString()));
					transReport.setMerchants(userThis.getBusinessName());
					userMap.put(userThis.getPayId(), userThis);
				}
				transactionList.add(transReport);
			}

			cursor.close();

		} catch (Exception exception) {
			logger.error("Exception", exception);
		}
		return transactionList;
	}

	public List<TransactionSearch> downloadSettlementReport(String merchantPayId, String currency, String saleDate,
			User user, String acquirer) {
		logger.info("Inside TxnReports , searchPayment");
		try {
			List<TransactionSearch> transactionList = new ArrayList<TransactionSearch>();

			List<BasicDBObject> paramConditionLst = new ArrayList<BasicDBObject>();
			List<BasicDBObject> acquirerConditionLst = new ArrayList<BasicDBObject>();
			BasicDBObject dateQuery = new BasicDBObject();
			BasicDBObject acquirerQuery = new BasicDBObject();
			BasicDBObject allParamQuery = new BasicDBObject();
			String currentDate = null;
			if (!saleDate.isEmpty()) {

				DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				Calendar cal = Calendar.getInstance();
				// add days to from date
				Date date1 = dateFormat.parse(saleDate);
				cal.setTime(date1);
				cal.add(Calendar.DATE, 1);
				currentDate = dateFormat.format(cal.getTime());

				dateQuery.put(FieldType.PG_DATE_TIME.getName(),
						BasicDBObjectBuilder.start("$gte", new SimpleDateFormat(saleDate).toLocalizedPattern())
								.add("$lt", new SimpleDateFormat(currentDate).toLocalizedPattern()).get());
			}

			List<BasicDBObject> dateIndexConditionList = new ArrayList<BasicDBObject>();
			BasicDBObject dateIndexConditionQuery = new BasicDBObject();
			String startString = new SimpleDateFormat(saleDate).toLocalizedPattern();
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
				dateIndexConditionList.add(new BasicDBObject("PG_DATE_TIME_INDEX", dateIndex));
			}
			if (StringUtils.isNotBlank(PropertiesManager.propertiesMap.get(Constants.USE_PG_DATE_TIME_INDEX.getValue()))
					&& PropertiesManager.propertiesMap.get(Constants.USE_PG_DATE_TIME_INDEX.getValue())
							.equalsIgnoreCase("Y")) {

				if (dateIndexConditionList.size() > 0) {
					dateIndexConditionQuery.append("$or", dateIndexConditionList);
				}

			}

			if (!merchantPayId.equalsIgnoreCase("ALL")) {
				paramConditionLst.add(new BasicDBObject(FieldType.PAY_ID.getName(), merchantPayId));
			}
//			Done By chetan nagaria for change in settlement process to mark transaction as RNS
			paramConditionLst.add(new BasicDBObject(FieldType.STATUS.getName(), StatusType.SETTLED_SETTLE.getName()));
			paramConditionLst.add(new BasicDBObject(FieldType.TXNTYPE.getName(), TransactionType.RECO.getName()));

			if (!currency.equalsIgnoreCase("ALL")) {
				paramConditionLst.add(new BasicDBObject(FieldType.CURRENCY_CODE.getName(), currency));
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

			logger.info("Inside Settlement Reprort , searchPayment , finalquery = " + finalquery);
			MongoDatabase dbIns = mongoInstance.getDB();
			MongoCollection<Document> coll = dbIns
					.getCollection(PropertiesManager.propertiesMap.get(prefix + Constants.COLLECTION_NAME.getValue()));
			MongoCursor<Document> cursor = coll.find(finalquery).iterator();
			while (cursor.hasNext()) {
				Document doc = cursor.next();

				TransactionSearch transReport = new TransactionSearch();
				transReport.setPgRefNum(doc.getString(FieldType.PG_REF_NUM.toString()));
				transReport.setAmount(doc.getString(FieldType.AMOUNT.toString()));
				transReport
						.setDateFrom(DateCreater.formatSaleDateTime(doc.getString(FieldType.PG_DATE_TIME.getName())));
				transReport.setOrderId(doc.getString(FieldType.ORDER_ID.toString()));
				if (doc.getString(FieldType.PAYMENT_TYPE.toString()).equals(PaymentType.UPI.getCode())) {
					transReport.setPaymentMethods(PaymentType.UPI.getName());
				} else {
					transReport.setPaymentMethods(doc.getString(FieldType.PAYMENT_TYPE.toString()));
				}
				transactionList.add(transReport);
			}
			cursor.close();
			logger.info("Inside Download Settlement Reports , transactionListSize = " + transactionList.size());
			return transactionList;
		}

		catch (Exception e) {
			logger.error("Exception occured in TxnReports , searchPayment , Exception = " + e);
			return null;
		}
	}

	public List<TransactionSearch> summaryReportNew(String fromDate, String toDate, String payId, String paymentType,
			String acquirer, String currency, User user, int start, int length, String paymentsRegion,
			String cardHolderType, String pgRefNum, String mopType, String transactionType, String orderId, String phoneNo)
			throws SystemException {

		logger.info("Inside search summary report New query");

		List<TransactionSearch> transactionList = new ArrayList<TransactionSearch>();
		try {

			BasicDBObject dateQuery = new BasicDBObject();
			List<BasicDBObject> paramConditionLst = new ArrayList<BasicDBObject>();
			BasicDBObject currencyQuery = new BasicDBObject();
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
			if (!pgRefNum.isEmpty()) {
				paramConditionLst.add(new BasicDBObject(FieldType.PG_REF_NUM.getName(), pgRefNum));
			}
			if (!payId.equalsIgnoreCase("ALL")) {
				paramConditionLst.add(new BasicDBObject(FieldType.PAY_ID.getName(), payId));
			}
			if (user.getUserType().equals(UserType.RESELLER)) {
				paramConditionLst.add(new BasicDBObject(FieldType.RESELLER_ID.getName(), user.getResellerId()));
			}
			
			if (!currency.equalsIgnoreCase("ALL")) {
				paramConditionLst.add(new BasicDBObject(FieldType.CURRENCY_CODE.getName(), currency));
			}

			if (!paymentsRegion.equalsIgnoreCase("ALL")) {
				paramConditionLst.add(new BasicDBObject(FieldType.PAYMENTS_REGION.getName(), paymentsRegion));
			}

			if (!cardHolderType.equalsIgnoreCase("ALL")) {
				paramConditionLst.add(new BasicDBObject(FieldType.CARD_HOLDER_TYPE.getName(), cardHolderType));
			}

			if (!mopType.equalsIgnoreCase("ALL")) {
				paramConditionLst.add(new BasicDBObject(FieldType.MOP_TYPE.getName(), mopType));
			}

			if (!acquirer.equalsIgnoreCase("ALL")) {

				List<String> acquirerList = Arrays.asList(acquirer.split(","));
				for (String acq : acquirerList) {

					acquirerConditionLst.add(new BasicDBObject(FieldType.ACQUIRER_TYPE.getName(), acq));
				}
				acquirerQuery.append("$or", acquirerConditionLst);

			}

			if (!paymentType.equalsIgnoreCase("ALL")) {
				paramConditionLst.add(new BasicDBObject(FieldType.PAYMENT_TYPE.getName(), paymentType));
			}

			if (!orderId.isEmpty()) {
				paramConditionLst.add(new BasicDBObject(FieldType.ORDER_ID.getName(), orderId));
			}
			
			if (!phoneNo.isEmpty()) {
				paramConditionLst.add(new BasicDBObject(FieldType.CUST_PHONE.getName(), phoneNo));
			}

			if (transactionType.equalsIgnoreCase("ALL")) {

				List<BasicDBObject> saleConditionList = new ArrayList<BasicDBObject>();
				saleConditionList
						.add(new BasicDBObject(FieldType.ORIG_TXNTYPE.getName(), TransactionType.SALE.getName()));
//				Done By chetan nagaria for change in settlement process to mark transaction as RNS
				saleConditionList
						.add(new BasicDBObject(FieldType.ALIAS_STATUS.getName(), StatusType.SETTLED_SETTLE.getName()));
				saleConditionList
						.add(new BasicDBObject(FieldType.RESPONSE_CODE.getName(), ErrorType.SUCCESS.getCode()));

				BasicDBObject saleConditionQuery = new BasicDBObject("$and", saleConditionList);

				List<BasicDBObject> authConditionList = new ArrayList<BasicDBObject>();
				authConditionList
						.add(new BasicDBObject(FieldType.ORIG_TXNTYPE.getName(), TransactionType.AUTHORISE.getName()));
				authConditionList
						.add(new BasicDBObject(FieldType.ALIAS_STATUS.getName(), StatusType.APPROVED.getName()));

				BasicDBObject authConditionQuery = new BasicDBObject("$and", authConditionList);

				List<BasicDBObject> refundConditionList = new ArrayList<BasicDBObject>();
				refundConditionList
						.add(new BasicDBObject(FieldType.ORIG_TXNTYPE.getName(), TransactionType.REFUND.getName()));
//				Done By chetan nagaria for change in settlement process to mark transaction as RNS
				refundConditionList
						.add(new BasicDBObject(FieldType.ALIAS_STATUS.getName(), StatusType.SETTLED_SETTLE.getName()));

				BasicDBObject refundConditionQuery = new BasicDBObject("$and", refundConditionList);

				saleOrAuthList.add(saleConditionQuery);
				saleOrAuthList.add(authConditionQuery);
				saleOrAuthList.add(refundConditionQuery);

			}

			else if (transactionType.equalsIgnoreCase("SALE")) {

				List<BasicDBObject> saleConditionList = new ArrayList<BasicDBObject>();
				saleConditionList
						.add(new BasicDBObject(FieldType.ORIG_TXNTYPE.getName(), TransactionType.SALE.getName()));
//				Done By chetan nagaria for change in settlement process to mark transaction as RNS
				saleConditionList
						.add(new BasicDBObject(FieldType.ALIAS_STATUS.getName(), StatusType.SETTLED_SETTLE.getName()));
				saleConditionList
						.add(new BasicDBObject(FieldType.RESPONSE_CODE.getName(), ErrorType.SUCCESS.getCode()));

				BasicDBObject saleConditionQuery = new BasicDBObject("$and", saleConditionList);

				List<BasicDBObject> authConditionList = new ArrayList<BasicDBObject>();
				authConditionList
						.add(new BasicDBObject(FieldType.ORIG_TXNTYPE.getName(), TransactionType.AUTHORISE.getName()));
				authConditionList
						.add(new BasicDBObject(FieldType.ALIAS_STATUS.getName(), StatusType.APPROVED.getName()));

				BasicDBObject authConditionQuery = new BasicDBObject("$and", authConditionList);

				saleOrAuthList.add(saleConditionQuery);
				saleOrAuthList.add(authConditionQuery);

			}

			else {

				List<BasicDBObject> refundConditionList = new ArrayList<BasicDBObject>();
				refundConditionList
						.add(new BasicDBObject(FieldType.ORIG_TXNTYPE.getName(), TransactionType.REFUND.getName()));
//				Done By chetan nagaria for change in settlement process to mark transaction as RNS
				refundConditionList
						.add(new BasicDBObject(FieldType.ALIAS_STATUS.getName(), StatusType.SETTLED_SETTLE.getName()));

				BasicDBObject refundConditionQuery = new BasicDBObject("$and", refundConditionList);
				saleOrAuthList.add(refundConditionQuery);

			}

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

			// if orderId or PgRefNum is not blank we can't use dateQuery or
			// dateIndexConditionQuery
			if (orderId.isEmpty() && pgRefNum.isEmpty()) {

				if (!dateQuery.isEmpty()) {
					allConditionQueryList.add(dateQuery);
				}
				if (!dateIndexConditionQuery.isEmpty()) {
					allConditionQueryList.add(dateIndexConditionQuery);
				}
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

			logger.info("Inside search summary report new query , finalquery = " + finalquery);
			MongoDatabase dbIns = mongoInstance.getDB();
			MongoCollection<Document> coll = dbIns.getCollection(
					PropertiesManager.propertiesMap.get(prefix + Constants.TRANSACTION_STATUS_COLLECTION.getValue()));
			BasicDBObject match = new BasicDBObject("$match", finalquery);

			BasicDBObject sort = new BasicDBObject("$sort", new BasicDBObject("CREATE_DATE", -1));
			BasicDBObject skip = new BasicDBObject("$skip", start);
			BasicDBObject limit = new BasicDBObject("$limit", length);
			Document firstGroup = new Document("_id", new Document("PG_REF_NUM", "$PG_REF_NUM").append("STATUS", "$STATUS"));
            BasicDBObject firstGroupObject = new BasicDBObject(firstGroup);
            BasicDBObject secondGroup = new BasicDBObject("$last", "$$ROOT");
            BasicDBObject groupQuery = new BasicDBObject("$group", firstGroupObject.append("contentList", secondGroup));
            BasicDBObject replacedRoot = new BasicDBObject("newRoot", "$contentList");
            BasicDBObject replaceRootQuery = new BasicDBObject("$replaceRoot", replacedRoot);
            List<Bson> pipeline = Arrays.asList(match, groupQuery, replaceRootQuery, sort, skip, limit);
			AggregateIterable<Document> output = coll.aggregate(pipeline);
			output.allowDiskUse(true);
			MongoCursor<Document> cursor = output.iterator();
			User merchant = new User();
			int count = 1;
			while (cursor.hasNext()) {

				Document doc = cursor.next();

				if (userMap.get(doc.getString(FieldType.PAY_ID.toString())) != null) {
					merchant = userMap.get(doc.getString(FieldType.PAY_ID.toString()));
				} else {
					merchant = userdao.findPayId1(doc.getString(FieldType.PAY_ID.toString()));
					if (null != merchant) {
						userMap.put(merchant.getPayId(), merchant);
					}
				}

				TransactionSearch transReport = new TransactionSearch();
				transReport.setCustomerName(doc.getString(FieldType.CARD_HOLDER_NAME.toString()));
				transReport.setSrNo(count++);
				BigInteger txnId = new BigInteger(doc.getString(FieldType.TXN_ID.toString()));
				transReport.setTransactionId(txnId);
				transReport.setTransactionIdString(String.valueOf(txnId));
				transReport.setDateFrom(doc.getString(FieldType.SETTLEMENT_DATE.getName()));
				transReport.setInternalCardIssusserBank(doc.getString(FieldType.INTERNAL_CARD_ISSUER_BANK.toString()));
				transReport.setInternalCardIssusserCountry(
						doc.getString(FieldType.INTERNAL_CARD_ISSUER_COUNTRY.toString()));
				transReport.setPayId(doc.getString(FieldType.PAY_ID.toString()));

				transReport.setOrderId(doc.getString(FieldType.ORDER_ID.toString()));
				String surchargeFlag = doc.getString(FieldType.SURCHARGE_FLAG.toString());
				transReport.setSurchargeFlag(surchargeFlag);
				transReport.setPgRefNum(doc.getString(FieldType.PG_REF_NUM.toString()));
				transReport.setCardMask(doc.getString(FieldType.CARD_MASK.toString()));
				transReport.setAmount(doc.getString(FieldType.AMOUNT.toString()));
				transReport.setTransactionRegion(doc.getString(FieldType.PAYMENTS_REGION.toString()));
				transReport.setCardHolderType(doc.getString(FieldType.CARD_HOLDER_TYPE.toString()));
				transReport.setTotalAmount(doc.getString(FieldType.TOTAL_AMOUNT.toString()));
				transReport.setAcquirerType(doc.getString(FieldType.ACQUIRER_TYPE.toString()));
				transReport.setMopType(doc.getString(FieldType.MOP_TYPE.toString()));
				transReport.setPaymentMethods(doc.getString(FieldType.PAYMENT_TYPE.toString()));
				transReport.setTransactionCaptureDate(doc.getString(FieldType.CREATE_DATE.toString()));
				if (doc.getString(FieldType.ORIG_TXNTYPE.toString()).equalsIgnoreCase(TransactionType.SALE.getName())) {
					transReport.setTxnType(TransactionType.SALE.getName());
				} else {
					transReport.setTxnType(TransactionType.REFUND.getName());
				}

				transReport.setCurrency(doc.getString(FieldType.CURRENCY_CODE.toString()));
				transReport.setPostSettledFlag(doc.getString(FieldType.POST_SETTLED_FLAG.toString()));
				transReport.setAcqId(doc.getString(FieldType.ACQ_ID.toString()));
				transReport.setRrn(doc.getString(FieldType.RRN.toString()));
				transReport.setTotalGstOnMerchant(
						String.valueOf( ((Double.valueOf(doc.getString(FieldType.ACQUIRER_TDR_SC.toString()))
								+ Double.valueOf(doc.getString(FieldType.PG_TDR_SC.toString()))))));
				transReport.setMerchantTdrCalculate(
						String.valueOf( (Double.valueOf(doc.getString(FieldType.ACQUIRER_GST.toString()))
								+ Double.valueOf(doc.getString(FieldType.PG_GST.toString())))));
				if (null != merchant && StringUtils.isNotBlank(merchant.getBusinessName())) {
					transReport.setBusinessName(merchant.getBusinessName());
				} else {
					transReport.setBusinessName("");
				}

				transReport.setNetMerchantPayableAmount(String.valueOf(
						((Double.valueOf(doc.getString(FieldType.TOTAL_AMOUNT.toString()))
								- Double.valueOf(doc.getString(FieldType.ACQUIRER_TDR_SC.toString()))
								- Double.valueOf(doc.getString(FieldType.PG_TDR_SC.toString()))
								- Double.valueOf(doc.getString(FieldType.ACQUIRER_GST.toString()))
								- Double.valueOf(doc.getString(FieldType.PG_GST.toString()))))));

				// For Admin and Sub Admin

				transReport.setPgSurchargeAmount(
						String.valueOf( ((Double.valueOf(doc.getString(FieldType.PG_TDR_SC.toString()))))));
				transReport.setAcquirerSurchargeAmount(
						String.valueOf( (Double.valueOf(doc.getString(FieldType.ACQUIRER_TDR_SC.toString())))));
				transReport.setPgGst(
						String.valueOf( ((Double.valueOf(doc.getString(FieldType.PG_GST.toString()))))));
				transReport.setAcquirerGst(
						String.valueOf( (Double.valueOf(doc.getString(FieldType.ACQUIRER_GST.toString())))));

				// Refund Button logic
				String totalRefundAmount = transactionSearchServiceMongo
						.getTotlaRefundByORderId(doc.getString(FieldType.ORDER_ID.toString()));
				Double totalRef = Double.valueOf(totalRefundAmount);
				if (totalRef.equals(0.00)) {
					transReport.setRefundButtonName("Refund");
				} else if (totalRef < Double.valueOf(doc.getString(FieldType.AMOUNT.toString()))) {
					transReport.setRefundButtonName("Partial Refund");
				} else if ((totalRef.equals(Double.valueOf(doc.getString(FieldType.AMOUNT.toString()))))
						|| (totalRef > Double.valueOf(doc.getString(FieldType.AMOUNT.toString()))) || (transReport.getTxnType().equalsIgnoreCase(TransactionType.REFUND.getName()))) {
					transReport.setRefundButtonName("Refunded");
				} else {
					transReport.setRefundButtonName("Refund");
				}

				transactionList.add(transReport);
			}

			logger.info("Inside search summary report query , transactionList size = " + transactionList.size());
			cursor.close();

		} catch (Exception exception) {
			logger.error("Exception", exception);
		}
		return transactionList.stream().sorted(new Comparator<TransactionSearch>() {

			@Override
			public int compare(TransactionSearch o1, TransactionSearch o2) {
				return o2.getTransactionCaptureDate().compareTo(o1.getTransactionCaptureDate());
			}
		}).collect(Collectors.toList());
	}

	public int summaryReportCountNew(String fromDate, String toDate, String payId, String paymentType, String acquirer,
			String currency, User user, int start, int length, String paymentsRegion, String cardHolderType,
			String pgRefNum, String mopType, String transactionType, String orderId, String phoneNo) throws SystemException {

		logger.info("Inside search summary count report New query");

		try {

			BasicDBObject dateQuery = new BasicDBObject();
			List<BasicDBObject> paramConditionLst = new ArrayList<BasicDBObject>();
			BasicDBObject currencyQuery = new BasicDBObject();
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
			if (!pgRefNum.isEmpty()) {
				paramConditionLst.add(new BasicDBObject(FieldType.PG_REF_NUM.getName(), pgRefNum));
			}
			if (!payId.equalsIgnoreCase("ALL")) {
				paramConditionLst.add(new BasicDBObject(FieldType.PAY_ID.getName(), payId));
			}
			if (user.getUserType().equals(UserType.RESELLER)) {
				paramConditionLst.add(new BasicDBObject(FieldType.RESELLER_ID.getName(), user.getResellerId()));
			}
			if (!currency.equalsIgnoreCase("ALL")) {
				paramConditionLst.add(new BasicDBObject(FieldType.CURRENCY_CODE.getName(), currency));
			}

			if (!paymentsRegion.equalsIgnoreCase("ALL")) {
				paramConditionLst.add(new BasicDBObject(FieldType.PAYMENTS_REGION.getName(), paymentsRegion));
			}

			if (!cardHolderType.equalsIgnoreCase("ALL")) {
				paramConditionLst.add(new BasicDBObject(FieldType.CARD_HOLDER_TYPE.getName(), cardHolderType));
			}

			if (!mopType.equalsIgnoreCase("ALL")) {
				paramConditionLst.add(new BasicDBObject(FieldType.MOP_TYPE.getName(), mopType));
			}

			if (!acquirer.equalsIgnoreCase("ALL")) {

				List<String> acquirerList = Arrays.asList(acquirer.split(","));
				for (String acq : acquirerList) {

					acquirerConditionLst.add(new BasicDBObject(FieldType.ACQUIRER_TYPE.getName(), acq));
				}
				acquirerQuery.append("$or", acquirerConditionLst);

			}

			if (!paymentType.equalsIgnoreCase("ALL")) {
				paramConditionLst.add(new BasicDBObject(FieldType.PAYMENT_TYPE.getName(), paymentType));
			}

			if (!orderId.isEmpty()) {
				paramConditionLst.add(new BasicDBObject(FieldType.ORDER_ID.getName(), orderId));
			}
			
			if (!phoneNo.isEmpty()) {
				paramConditionLst.add(new BasicDBObject(FieldType.CUST_PHONE.getName(), phoneNo));
			}

			if (transactionType.equalsIgnoreCase("ALL")) {

				List<BasicDBObject> saleConditionList = new ArrayList<BasicDBObject>();
				saleConditionList
						.add(new BasicDBObject(FieldType.ORIG_TXNTYPE.getName(), TransactionType.SALE.getName()));
//				Done By chetan nagaria for change in settlement process to mark transaction as RNS
				saleConditionList
						.add(new BasicDBObject(FieldType.ALIAS_STATUS.getName(), StatusType.SETTLED_SETTLE.getName()));
				saleConditionList
						.add(new BasicDBObject(FieldType.RESPONSE_CODE.getName(), ErrorType.SUCCESS.getCode()));

				BasicDBObject saleConditionQuery = new BasicDBObject("$and", saleConditionList);

				List<BasicDBObject> authConditionList = new ArrayList<BasicDBObject>();
				authConditionList
						.add(new BasicDBObject(FieldType.ORIG_TXNTYPE.getName(), TransactionType.AUTHORISE.getName()));
				authConditionList
						.add(new BasicDBObject(FieldType.ALIAS_STATUS.getName(), StatusType.APPROVED.getName()));

				BasicDBObject authConditionQuery = new BasicDBObject("$and", authConditionList);

				List<BasicDBObject> refundConditionList = new ArrayList<BasicDBObject>();
				refundConditionList
						.add(new BasicDBObject(FieldType.ORIG_TXNTYPE.getName(), TransactionType.REFUND.getName()));
//				Done By chetan nagaria for change in settlement process to mark transaction as RNS
				refundConditionList
						.add(new BasicDBObject(FieldType.ALIAS_STATUS.getName(), StatusType.SETTLED_SETTLE.getName()));

				BasicDBObject refundConditionQuery = new BasicDBObject("$and", refundConditionList);

				saleOrAuthList.add(saleConditionQuery);
				saleOrAuthList.add(authConditionQuery);
				saleOrAuthList.add(refundConditionQuery);

			}

			else if (transactionType.equalsIgnoreCase("SALE")) {

				List<BasicDBObject> saleConditionList = new ArrayList<BasicDBObject>();
				saleConditionList
						.add(new BasicDBObject(FieldType.ORIG_TXNTYPE.getName(), TransactionType.SALE.getName()));
//				Done By chetan nagaria for change in settlement process to mark transaction as RNS
				saleConditionList
						.add(new BasicDBObject(FieldType.ALIAS_STATUS.getName(), StatusType.SETTLED_SETTLE.getName()));
				saleConditionList
						.add(new BasicDBObject(FieldType.RESPONSE_CODE.getName(), ErrorType.SUCCESS.getCode()));

				BasicDBObject saleConditionQuery = new BasicDBObject("$and", saleConditionList);

				List<BasicDBObject> authConditionList = new ArrayList<BasicDBObject>();
				authConditionList
						.add(new BasicDBObject(FieldType.ORIG_TXNTYPE.getName(), TransactionType.AUTHORISE.getName()));
				authConditionList
						.add(new BasicDBObject(FieldType.ALIAS_STATUS.getName(), StatusType.APPROVED.getName()));

				BasicDBObject authConditionQuery = new BasicDBObject("$and", authConditionList);

				saleOrAuthList.add(saleConditionQuery);
				saleOrAuthList.add(authConditionQuery);

			}

			else {

				List<BasicDBObject> refundConditionList = new ArrayList<BasicDBObject>();
				refundConditionList
						.add(new BasicDBObject(FieldType.ORIG_TXNTYPE.getName(), TransactionType.REFUND.getName()));
//				Done By chetan nagaria for change in settlement process to mark transaction as RNS
				refundConditionList
						.add(new BasicDBObject(FieldType.ALIAS_STATUS.getName(), StatusType.SETTLED_SETTLE.getName()));

				BasicDBObject refundConditionQuery = new BasicDBObject("$and", refundConditionList);
				saleOrAuthList.add(refundConditionQuery);

			}

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
			if (orderId.isEmpty() && pgRefNum.isEmpty()) {

				if (!dateQuery.isEmpty()) {
					allConditionQueryList.add(dateQuery);
				}
				if (!dateIndexConditionQuery.isEmpty()) {
					allConditionQueryList.add(dateIndexConditionQuery);
				}
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

			logger.info("Inside search summary report new count query , finalquery = " + finalquery);
			MongoDatabase dbIns = mongoInstance.getDB();
			MongoCollection<Document> coll = dbIns.getCollection(
					PropertiesManager.propertiesMap.get(prefix + Constants.TRANSACTION_STATUS_COLLECTION.getValue()));

			BasicDBObject match = new BasicDBObject("$match", finalquery);
			Document firstGroup = new Document("_id", new Document("PG_REF_NUM", "$PG_REF_NUM").append("STATUS", "$STATUS"));
            BasicDBObject firstGroupObject = new BasicDBObject(firstGroup);
            BasicDBObject secondGroup = new BasicDBObject("$last", "$$ROOT");
            BasicDBObject groupQuery = new BasicDBObject("$group", firstGroupObject.append("contentList", secondGroup));
            return coll.aggregate(Arrays.asList(match, groupQuery)).into(new ArrayList<>()).size();

		} catch (Exception exception) {
			logger.error("Exception", exception);
		}
		return 0;
	}

	public List<SummaryObject> summaryReportDownloadNew(String fromDate, String toDate, String payId,
			String paymentType, String acquirer, String currency, User user, String paymentsRegion,
			String cardHolderType, String pgRefNum, String mopType, String transactionType) throws SystemException {
		List<SummaryObject> transactionList = new ArrayList<SummaryObject>();

		logger.info("Inside SummaryReportQuery new");

		try {

			BasicDBObject dateQuery = new BasicDBObject();
			List<BasicDBObject> paramConditionLst = new ArrayList<BasicDBObject>();
			BasicDBObject currencyQuery = new BasicDBObject();
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
//			Done By chetan nagaria for change in settlement process to mark transaction as RNS
			paramConditionLst.add(new BasicDBObject(FieldType.ALIAS_STATUS.getName(), StatusType.SETTLED_SETTLE.getName()));

			if (!payId.equalsIgnoreCase("ALL") && StringUtils.isNotBlank(payId)) {
				paramConditionLst.add(new BasicDBObject(FieldType.PAY_ID.getName(), payId));
			}

			if (!paymentsRegion.equalsIgnoreCase("ALL")) {
				paramConditionLst.add(new BasicDBObject(FieldType.PAYMENTS_REGION.getName(), paymentsRegion));
			}

			if (!cardHolderType.equalsIgnoreCase("ALL")) {
				paramConditionLst.add(new BasicDBObject(FieldType.CARD_HOLDER_TYPE.getName(), cardHolderType));
			}

			if (!mopType.equalsIgnoreCase("ALL")) {
				paramConditionLst.add(new BasicDBObject(FieldType.MOP_TYPE.getName(), mopType));
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
			if (!paymentType.equalsIgnoreCase("ALL") && StringUtils.isNotBlank(paymentType)) {
				paramConditionLst.add(new BasicDBObject(FieldType.PAYMENT_TYPE.getName(), paymentType));
			}

			if (transactionType.equalsIgnoreCase("ALL")) {

			}

			else if (transactionType.equalsIgnoreCase("SALE")) {

				List<BasicDBObject> recoConditionList = new ArrayList<BasicDBObject>();
				recoConditionList
						.add(new BasicDBObject(FieldType.ORIG_TXNTYPE.getName(), TransactionType.SALE.getName()));
//				Done By chetan nagaria for change in settlement process to mark transaction as RNS
				recoConditionList
						.add(new BasicDBObject(FieldType.ALIAS_STATUS.getName(), StatusType.SETTLED_SETTLE.getName()));

				BasicDBObject recoConditionQuery = new BasicDBObject("$and", recoConditionList);

				saleOrAuthList.add(recoConditionQuery);

			}

			else {

				List<BasicDBObject> recoRefundConditionList = new ArrayList<BasicDBObject>();
				recoRefundConditionList
						.add(new BasicDBObject(FieldType.ORIG_TXNTYPE.getName(), TransactionType.REFUND.getName()));
//				Done By chetan nagaria for change in settlement process to mark transaction as RNS
				recoRefundConditionList
						.add(new BasicDBObject(FieldType.ALIAS_STATUS.getName(), StatusType.SETTLED_SETTLE.getName()));

				BasicDBObject recoRefundConditionQuery = new BasicDBObject("$and", recoRefundConditionList);

				saleOrAuthList.add(recoRefundConditionQuery);

			}

			BasicDBObject authndSaleConditionQuery = new BasicDBObject();
			if (saleOrAuthList.size() > 0) {
				authndSaleConditionQuery.put("$or", saleOrAuthList);
			}

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
			logger.info("finalquery processing for downloadSummaryReport new  = " + finalquery);
			MongoDatabase dbIns = mongoInstance.getDB();
			MongoCollection<Document> coll = dbIns.getCollection(
					PropertiesManager.propertiesMap.get(prefix + Constants.TRANSACTION_STATUS_COLLECTION.getValue()));
			BasicDBObject match = new BasicDBObject("$match", finalquery);

			List<BasicDBObject> pipeline = Arrays.asList(match);
			AggregateIterable<Document> output = coll.aggregate(pipeline);
			output.allowDiskUse(true);
			MongoCursor<Document> cursor = output.iterator();
			int counter = 1;
			int total = 1;
			while (cursor.hasNext()) {
				counter = counter + 1;
				total = total + 1;
				Document doc = cursor.next();

				if (counter == 3000) {
					logger.info("generating summary report , add fields = " + total);
					counter = 0;
				}

				SummaryObject transReport = new SummaryObject();
				transReport.setTransactionId(doc.getString(FieldType.TXN_ID.toString()));
				transReport.setDateFrom(doc.getString(FieldType.SETTLEMENT_DATE.getName()));
				transReport.setSrNo(String.valueOf(total));
				transReport.setCurrency("INR");
				transReport.setOrderId(doc.getString(FieldType.ORDER_ID.toString()));
				transReport.setPgRefNum(doc.getString(FieldType.PG_REF_NUM.toString()));
				if (StringUtils.isBlank(doc.getString(FieldType.PAYMENTS_REGION.toString()))) {
					transReport.setTransactionRegion(AccountCurrencyRegion.DOMESTIC.toString());
				} else {
					transReport.setTransactionRegion(doc.getString(FieldType.PAYMENTS_REGION.toString()));
				}

				if (StringUtils.isBlank(doc.getString(FieldType.CARD_HOLDER_TYPE.toString()))) {

					transReport.setCardHolderType(CardHolderType.CONSUMER.toString());
				} else {
					transReport.setCardHolderType(doc.getString(FieldType.CARD_HOLDER_TYPE.toString()));
				}

				transReport.setTotalAmount(String.valueOf(doc.getString(FieldType.TOTAL_AMOUNT.toString())));
				transReport.setAcquirerType(doc.getString(FieldType.ACQUIRER_TYPE.toString()));

				if (null != doc.getString(FieldType.MOP_TYPE.toString())) {
					transReport.setMopType(MopType.getmopName(doc.getString(FieldType.MOP_TYPE.toString())));
				} else {
					transReport.setMopType(CrmFieldConstants.NOT_AVAILABLE.getValue());
				}

				if (null != doc.getString(FieldType.PAYMENT_TYPE.toString())) {
					transReport.setPaymentMethods(doc.getString(FieldType.PAYMENT_TYPE.toString()));
				} else {
					transReport.setPaymentMethods(CrmFieldConstants.NOT_AVAILABLE.getValue());
				}
				transReport.setCaptureDate(doc.getString(FieldType.CREATE_DATE.toString()));
				transReport.setTxnType(doc.getString(FieldType.ORIG_TXNTYPE.toString()));
				transReport.setMerchants(doc.getString(CrmFieldType.BUSINESS_NAME.getName()));
				transReport.setAcqId(doc.getString(FieldType.ACQ_ID.toString()));
				transReport.setRrn(doc.getString(FieldType.RRN.toString()));
				transReport.setPostSettledFlag(doc.getString(FieldType.POST_SETTLED_FLAG.toString()));
				transReport.setDateFrom(doc.getString(FieldType.CREATE_DATE.toString()));

				// Refund values to be shown as negative in Summary Report
				if (doc.getString(FieldType.ORIG_TXNTYPE.toString()).contains(TransactionType.REFUND.getName())) {
					transReport.setTdrScAcquirer(
							String.valueOf(Double.valueOf(doc.getString(FieldType.ACQUIRER_TDR_SC.getName())) * -1));
					transReport.setGstScAcquirer(
							String.valueOf(Double.valueOf(doc.getString(FieldType.ACQUIRER_GST.getName())) * -1));
					transReport.setTdrScIpay(
							String.valueOf(Double.valueOf(doc.getString(FieldType.PG_TDR_SC.getName())) * -1));
					transReport.setGstScIpay(
							String.valueOf(Double.valueOf(doc.getString(FieldType.PG_GST.getName())) * -1));
					transReport
							.setAmount(String.valueOf(Double.valueOf(doc.getString(FieldType.AMOUNT.getName())) * -1));
				}

				// Sale values to be shown as positive in Summary Report
				else {
					transReport.setTdrScAcquirer(
							String.valueOf((Double.valueOf(doc.getString(FieldType.ACQUIRER_TDR_SC.getName())) * 1)));
					transReport.setGstScAcquirer(
							String.valueOf(Double.valueOf(doc.getString(FieldType.ACQUIRER_GST.getName())) * 1));
					transReport.setTdrScIpay(
							String.valueOf(Double.valueOf(doc.getString(FieldType.PG_TDR_SC.getName())) * 1));
					transReport.setGstScIpay(
							String.valueOf(Double.valueOf(doc.getString(FieldType.PG_GST.getName())) * 1));
					transReport
							.setAmount(String.valueOf(Double.valueOf(doc.getString(FieldType.AMOUNT.getName())) * 1));
				}

				if (userMap.get(doc.getString(FieldType.PAY_ID.toString())) != null) {
					User userThis = userMap.get(doc.getString(FieldType.PAY_ID.toString()));
					transReport.setMerchants(userThis.getBusinessName());
				} else {
					User userThis = userdao.findPayId(doc.getString(FieldType.PAY_ID.toString()));
					if (userThis != null) {
						if (userThis.getBusinessName() != null) {
							transReport.setMerchants(userThis.getBusinessName());
						} else {
							transReport.setMerchants(doc.getString(FieldType.PAY_ID.toString()));
						}
						userMap.put(userThis.getPayId(), userThis);
					} else {
						transReport.setMerchants(doc.getString(FieldType.PAY_ID.toString()));
					}

				}
				transactionList.add(transReport);
			}

			cursor.close();

		} catch (Exception exception) {
			logger.error("Exception in summary report download ", exception);
		}
		return transactionList;
	}
}
