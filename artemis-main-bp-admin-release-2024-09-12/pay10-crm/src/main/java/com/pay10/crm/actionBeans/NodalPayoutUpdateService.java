package com.pay10.crm.actionBeans;

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
import java.util.concurrent.TimeUnit;

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
import com.pay10.commons.exception.SystemException;
import com.pay10.commons.mongo.MongoInstance;
import com.pay10.commons.user.AccountCurrencyRegion;
import com.pay10.commons.user.CardHolderType;
import com.pay10.commons.user.SummaryReportObject;
import com.pay10.commons.user.User;
import com.pay10.commons.user.UserDao;
import com.pay10.commons.util.Constants;
import com.pay10.commons.util.CrmFieldConstants;
import com.pay10.commons.util.CrmFieldType;
import com.pay10.commons.util.FieldType;
import com.pay10.commons.util.MopType;
import com.pay10.commons.util.PropertiesManager;
import com.pay10.commons.util.StatusType;
import com.pay10.commons.util.TransactionManager;

@Service
public class NodalPayoutUpdateService {

	private static Logger logger = LoggerFactory.getLogger(NodalPayoutUpdateService.class.getName());

	@Autowired
	private MongoInstance mongoInstance;

	@Autowired
	private UserDao userDao;

	@Autowired
	private PropertiesManager propertiesManager;

	private static final String prefix = "MONGO_DB_";

	public String updateNodalTransactions(String merchant, String acquirer, String nodalSettlementDate,
			String nodalType, String paymentType, String fromCaptureDate, String toCaptureDate, String fromDate,
			String toDate, User user) {

		logger.info("Inside NodalPayoutUpdateService , updateNodalTransactions");

		String status = "";

		if (nodalType.equalsIgnoreCase("nodalSettlement")) {
			status = StatusType.NODAL_CREDIT.getName();
		} else {
			status = StatusType.NODAL_PAYOUT.getName();
		}

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

			if (!merchant.equalsIgnoreCase("ALL")) {
				paramConditionLst.add(new BasicDBObject(FieldType.PAY_ID.getName(), merchant));
			}

			if (!paymentType.equalsIgnoreCase("ALL")) {
				paramConditionLst.add(new BasicDBObject(FieldType.PAYMENT_TYPE.getName(), paymentType));
			}

			if (!acquirer.equalsIgnoreCase("ALL")) {

				paramConditionLst.add(new BasicDBObject(FieldType.ACQUIRER_TYPE.getName(), acquirer));
			}

			if (status.equalsIgnoreCase(StatusType.NODAL_CREDIT.getName())) {
				paramConditionLst.add(new BasicDBObject(FieldType.STATUS.getName(), StatusType.SETTLED_SETTLE.getName()));
			} else {
				paramConditionLst.add(new BasicDBObject(FieldType.STATUS.getName(), StatusType.NODAL_CREDIT.getName()));
			}

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
			logger.info("finalquery for updateNodalTransactions = " + finalquery);
			MongoDatabase dbIns = mongoInstance.getDB();
			MongoCollection<Document> coll = dbIns.getCollection(
					PropertiesManager.propertiesMap.get(prefix + Constants.SETTLED_TRANSACTIONS_NAME.getValue()));
			BasicDBObject match = new BasicDBObject("$match", finalquery);

			BasicDBObject sort = new BasicDBObject("$sort", new BasicDBObject("CREATE_DATE", -1));

			List<BasicDBObject> pipeline = Arrays.asList(match, sort);
			AggregateIterable<Document> output = coll.aggregate(pipeline);
			output.allowDiskUse(true);
			MongoCursor<Document> cursor = output.iterator();
			List<Document> documents = new ArrayList<>();
			int updatedCount = 0;
			int totalCount = 0;
			while (cursor.hasNext()) {
				Document doc = cursor.next();
				totalCount++;
				// Check if record already entered
				String pgRefNum = doc.getString(FieldType.PG_REF_NUM.getName());
				List<BasicDBObject> saleTxnQuery1 = new ArrayList<BasicDBObject>();
				saleTxnQuery1.add(new BasicDBObject(FieldType.PG_REF_NUM.getName(), pgRefNum));
				saleTxnQuery1.add(new BasicDBObject(FieldType.STATUS.getName(), status));
				BasicDBObject saleConditionQuery = new BasicDBObject("$and", saleTxnQuery1);
				long count = coll.count(saleConditionQuery);
				if (count > 0) {
					// Record is already entered , move to next
				} else {
					String txnId = TransactionManager.getNewTransactionId();
					TimeUnit.MILLISECONDS.sleep(2);

					Document document = new Document();
					document.put("_id", txnId);
					document.put(FieldType.TXN_ID.getName(), txnId);
					document.put(FieldType.PG_REF_NUM.getName(), doc.get(FieldType.PG_REF_NUM.getName()));
					document.put(FieldType.PAYMENTS_REGION.getName(), doc.get(FieldType.PAYMENTS_REGION.getName()));
					document.put(FieldType.POST_SETTLED_FLAG.getName(), doc.get(FieldType.POST_SETTLED_FLAG.getName()));
					document.put(FieldType.TXNTYPE.getName(), doc.get(FieldType.TXNTYPE.getName()));
					document.put(FieldType.ACQUIRER_TYPE.getName(), doc.get(FieldType.ACQUIRER_TYPE.getName()));
					document.put(FieldType.PAYMENT_TYPE.getName(), doc.get(FieldType.PAYMENT_TYPE.getName()));

					document.put(FieldType.ORDER_ID.getName(), doc.get(FieldType.ORDER_ID.getName()));
					document.put(FieldType.PAY_ID.getName(), doc.get(FieldType.PAY_ID.getName()));
					document.put(FieldType.MOP_TYPE.getName(), doc.get(FieldType.MOP_TYPE.getName()));
					document.put(FieldType.CURRENCY_CODE.getName(), doc.get(FieldType.CURRENCY_CODE.getName()));
					document.put(FieldType.CARD_HOLDER_TYPE.getName(), doc.get(FieldType.CARD_HOLDER_TYPE.getName()));
					document.put(FieldType.PG_DATE_TIME.getName(), doc.get(FieldType.PG_DATE_TIME.getName()));
					document.put(FieldType.ACQ_ID.getName(), doc.get(FieldType.ACQ_ID.getName()));
					document.put(FieldType.RRN.getName(), doc.get(FieldType.RRN.getName()));
					document.put(FieldType.UDF6.getName(), doc.get(FieldType.UDF6.getName()));
					document.put(FieldType.SURCHARGE_FLAG.getName(), doc.get(FieldType.SURCHARGE_FLAG.getName()));
					document.put(FieldType.REFUND_FLAG.getName(), doc.get(FieldType.REFUND_FLAG.getName()));
					document.put(FieldType.ARN.getName(), doc.get(FieldType.ARN.getName()));
					document.put(FieldType.ORIG_TXN_ID.getName(), doc.get(FieldType.ORIG_TXN_ID.getName()));
					document.put(FieldType.TOTAL_AMOUNT.getName(), doc.get(FieldType.TOTAL_AMOUNT.getName()));
					document.put(FieldType.AMOUNT.getName(), doc.get(FieldType.AMOUNT.getName()));
					document.put(FieldType.SURCHARGE_ACQ.getName(), doc.get(FieldType.SURCHARGE_ACQ.getName()));
					document.put(FieldType.SURCHARGE_IPAY.getName(), doc.get(FieldType.SURCHARGE_IPAY.getName()));
					document.put(FieldType.SURCHARGE_MMAD.getName(), doc.get(FieldType.SURCHARGE_MMAD.getName()));
					document.put(FieldType.GST_ACQ.getName(), doc.get(FieldType.GST_ACQ.getName()));
					document.put(FieldType.GST_IPAY.getName(), doc.get(FieldType.GST_IPAY.getName()));
					document.put(FieldType.GST_MMAD.getName(), doc.get(FieldType.GST_MMAD.getName()));
					document.put(FieldType.STATUS.getName(), status);

					if (status.equalsIgnoreCase(StatusType.NODAL_CREDIT.getName())) {
						document.put(FieldType.NODAL_CREDIT_DATE.getName(), nodalSettlementDate);
						document.put(FieldType.SETTLEMENT_DATE.getName(), doc.get(FieldType.CREATE_DATE.getName()));
					} else if (status.equalsIgnoreCase(StatusType.NODAL_PAYOUT.getName())) {
						document.put(FieldType.NODAL_CREDIT_DATE.getName(),
								doc.get(FieldType.NODAL_CREDIT_DATE.getName()));
						document.put(FieldType.NODAL_PAYOUT_DATE.getName(), nodalSettlementDate);
						document.put(FieldType.SETTLEMENT_DATE.getName(), doc.get(FieldType.SETTLEMENT_DATE.getName()));
					} else {
						logger.info("Inside nodalPayoutUpdateService , no status type found");
						continue;
					}
					// ABCD
					documents.add(document);
					updatedCount++;
				}

			}

			cursor.close();

			if (totalCount == 0) {
				logger.info("No transactions in DB for settlements , payouts");

				return "Found 0 settled transactions in DB for selected date";
			}

			if (updatedCount == 0) {
				logger.info("No transactions in DB for updating , payouts");

				return "Found total settled transactions " + totalCount + " , updated " + updatedCount
						+ " transactions with status " + status;
			}

			logger.info(
					"Adding " + documents.size() + " transactions in mongo document for nodal settlements , payouts");
			coll.insertMany(documents);

			return "Found total settled transactions " + totalCount + " , updated " + updatedCount
					+ " transactions with status " + status;
		} catch (Exception exception) {
			logger.error("Exception", exception);
		}
		return "Unable to update transactions!";
	}

	public List<SummaryReportObject> downloadNodalReport(String merchant, String acquirer, String nodalSettlementDate,
			String nodalType, String paymentType, String fromDate, String toDate, User user) throws SystemException {
		List<SummaryReportObject> transactionList = new ArrayList<SummaryReportObject>();

		logger.info("Inside SummaryReportQuery summaryReportDownload");
		Map<String, User> userMap = new HashMap<String, User>();

		String status = "";
		String dateType = "";
		if (nodalType.equalsIgnoreCase("nodalSettlement")) {
			status = StatusType.NODAL_CREDIT.getName();
			dateType = FieldType.NODAL_CREDIT_DATE.getName();
		} else {
			status = StatusType.NODAL_PAYOUT.getName();
			dateType = FieldType.NODAL_PAYOUT_DATE.getName();
		}

		try {

			BasicDBObject dateQuery = new BasicDBObject();
			List<BasicDBObject> paramConditionLst = new ArrayList<BasicDBObject>();
			BasicDBObject currencyQuery = new BasicDBObject();
			BasicDBObject acquirerQuery = new BasicDBObject();

			BasicDBObject allParamQuery = new BasicDBObject();
			List<BasicDBObject> acquirerConditionLst = new ArrayList<BasicDBObject>();

			if (!fromDate.isEmpty()) {

				String currentDate = null;
				if (!toDate.isEmpty()) {
					currentDate = toDate;
				} else {
					DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
					Calendar cal = Calendar.getInstance();
					currentDate = dateFormat.format(cal.getTime());
				}

				dateQuery.put(dateType,
						BasicDBObjectBuilder.start("$gte", new SimpleDateFormat(fromDate).toLocalizedPattern())
								.add("$lte", new SimpleDateFormat(currentDate).toLocalizedPattern()).get());
			}

			if (!merchant.equalsIgnoreCase("ALL")) {
				paramConditionLst.add(new BasicDBObject(FieldType.PAY_ID.getName(), merchant));
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

			if (!status.equalsIgnoreCase("ALL")) {
				paramConditionLst.add(new BasicDBObject(FieldType.STATUS.getName(), status));
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

			BasicDBObject allConditionQueryObj = new BasicDBObject("$and", allConditionQueryList);
			List<BasicDBObject> fianlList = new ArrayList<BasicDBObject>();

			if (!allParamQuery.isEmpty()) {
				fianlList.add(allParamQuery);
			}
			if (!allConditionQueryObj.isEmpty()) {
				fianlList.add(allConditionQueryObj);
			}

			BasicDBObject finalquery = new BasicDBObject("$and", fianlList);
			logger.info("finalquery for downloadNodalSettlementReport = " + finalquery);
			MongoDatabase dbIns = mongoInstance.getDB();
			MongoCollection<Document> coll = dbIns.getCollection(
					PropertiesManager.propertiesMap.get(prefix + Constants.SETTLED_TRANSACTIONS_NAME.getValue()));
			BasicDBObject match = new BasicDBObject("$match", finalquery);

			BasicDBObject sort = new BasicDBObject("$sort", new BasicDBObject("CREATE_DATE", -1));

			List<BasicDBObject> pipeline = Arrays.asList(match, sort);
			AggregateIterable<Document> output = coll.aggregate(pipeline);
			output.allowDiskUse(true);
			MongoCursor<Document> cursor = output.iterator();

			while (cursor.hasNext()) {
				Document doc = cursor.next();
				SummaryReportObject transReport = new SummaryReportObject();
				transReport.setTransactionId(doc.getString(FieldType.TXN_ID.toString()));
				transReport.setDateFrom(doc.getString(FieldType.CREATE_DATE.getName()));
				if (null != doc.getString(FieldType.CURRENCY_CODE.toString())) {
					transReport.setCurrency(propertiesManager
							.getAlphabaticCurrencyCode(doc.getString(FieldType.CURRENCY_CODE.toString())));
				} else {
					transReport.setCurrency(CrmFieldConstants.NA.getValue());
				}
				transReport.setPayId(doc.getString(FieldType.PAY_ID.toString()));
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

				if (null != doc.getString(FieldType.STATUS.toString())) {
					transReport.setStatus(doc.getString(FieldType.STATUS.toString()));
				} else {
					transReport.setStatus(CrmFieldConstants.NOT_AVAILABLE.getValue());
				}

				transReport.setNodalDate(doc.getString(dateType));

				transReport.setCaptureDate(doc.getString(FieldType.PG_DATE_TIME.toString()));
				transReport.setTxnType(doc.getString(FieldType.TXNTYPE.toString()));
				transReport.setMerchants(doc.getString(CrmFieldType.BUSINESS_NAME.getName()));
				transReport.setAcqId(doc.getString(FieldType.ACQ_ID.toString()));
				transReport.setRrn(doc.getString(FieldType.RRN.toString()));
				transReport.setPostSettledFlag(doc.getString(FieldType.POST_SETTLED_FLAG.toString()));
				transReport.setDeltaRefundFlag(doc.getString(FieldType.UDF6.toString()));
				transReport.setDateFrom(doc.getString(FieldType.CREATE_DATE.toString()));

				transReport.setTdrScAcquirer(String.valueOf(doc.getDouble(FieldType.SURCHARGE_ACQ.toString())));
				transReport.setGstScAcquirer(String.valueOf(doc.getDouble(FieldType.GST_ACQ.toString())));

				Double surcharge = doc.getDouble(FieldType.SURCHARGE_IPAY.toString())
						+ doc.getDouble(FieldType.SURCHARGE_MMAD.toString());
				Double gst = doc.getDouble(FieldType.GST_IPAY.toString())
						+ doc.getDouble(FieldType.GST_MMAD.toString());
				Double divisor = 2.00;

				surcharge = surcharge / divisor;
				gst = gst / divisor;

				transReport.setTdrScIpay(String.format("%.3f", surcharge));
				transReport.setGstScIpay(String.format("%.3f", gst));
				transReport.setTdrScMmad(String.format("%.3f", surcharge));
				transReport.setGstScMmad(String.format("%.3f", gst));

				if (userMap.get(doc.getString(FieldType.PAY_ID.toString())) != null) {
					User userThis = userMap.get(doc.getString(FieldType.PAY_ID.toString()));
					transReport.setMerchants(userThis.getBusinessName());
				} else {
					User userThis = userDao.findPayId(doc.getString(FieldType.PAY_ID.toString()));
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
}
