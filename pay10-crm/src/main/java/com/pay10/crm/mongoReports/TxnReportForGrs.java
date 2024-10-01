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
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.apache.jasper.tagplugins.jstl.core.ForEach;
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
import com.mongodb.client.DistinctIterable;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import com.pay10.commons.dao.TransactionSearchServiceMongo;
import com.pay10.commons.dto.GRS;
import com.pay10.commons.dto.GrsIssueHistoryDto;
import com.pay10.commons.mongo.MongoInstance;
import com.pay10.commons.user.CompanyProfile;
import com.pay10.commons.user.CompanyProfileDao;
import com.pay10.commons.user.PaymentSearchDownloadObject;
import com.pay10.commons.user.SettledTransactionDataObject;
import com.pay10.commons.user.TransactionSearch;
import com.pay10.commons.user.TransactionSearchDownloadObject;
import com.pay10.commons.user.TransactionSearchNew;
import com.pay10.commons.user.User;
import com.pay10.commons.user.UserDao;
import com.pay10.commons.user.UserType;
import com.pay10.commons.util.AcquirerType;
import com.pay10.commons.util.Constants;
import com.pay10.commons.util.CrmFieldConstants;
import com.pay10.commons.util.CrmFieldType;
import com.pay10.commons.util.Currency;
import com.pay10.commons.util.FieldType;
import com.pay10.commons.util.MopType;
import com.pay10.commons.util.MopTypeUI;
import com.pay10.commons.util.PaymentType;
import com.pay10.commons.util.PaymentTypeUI;
import com.pay10.commons.util.PropertiesManager;
import com.pay10.commons.util.StatusType;
import com.pay10.commons.util.TransactionType;
import com.pay10.commons.util.TxnType;

@Component
public class TxnReportForGrs {
	final String bsesPayid = PropertiesManager.propertiesMap.get(Constants.BSESPAYID.getValue());
	private static Logger logger = LoggerFactory.getLogger(TxnReportForGrs.class.getName());
	private static final String prefix = "MONGO_DB_";
	private static Map<String, User> userMap = new HashMap<String, User>();
	private static final String companyProfileTenantId = "DEFAULT_COMPANY_TENANTID";
	@Autowired
	private UserDao userdao;

	@Autowired
	private TransactionSearchServiceMongo transactionSearchServiceMongo;

	@Autowired
	private CompanyProfileDao companyProfileDao;

	@Autowired
	PropertiesManager propertiesManager;

	@Autowired
	private MongoInstance mongoInstance;

	public int newSearchPaymentCount(String pgRefNum, String orderId, String customerEmail, String customerPhone,
			String paymentType, String aliasStatus, String currency, String transactionType, String mopType,
			String acquirer, String merchantPayId, String fromDate, String toDate, User user, int start, int length,
			String tenantId, String ipAddress, String totalAmount, String rrn) {

		try {

			MongoDatabase dbIns = mongoInstance.getDB();
			MongoCollection<Document> coll = dbIns.getCollection(
					PropertiesManager.propertiesMap.get(prefix + Constants.TRANSACTION_STATUS_COLLECTION.getValue()));

			List<BasicDBObject> paramConditionLst = new ArrayList<BasicDBObject>();
			BasicDBObject allParamQuery = new BasicDBObject();
			BasicDBObject dateQuery = new BasicDBObject();
			BasicDBObject acquirerQuery = new BasicDBObject();
			List<BasicDBObject> acquirerConditionLst = new ArrayList<BasicDBObject>();
			List<BasicDBObject> mopTypeConditionLst = new ArrayList<BasicDBObject>();
			BasicDBObject mopTypeQuery = new BasicDBObject();
			BasicDBObject transactionStatusQuery = new BasicDBObject();
			List<BasicDBObject> paymentTypeConditionLst = new ArrayList<BasicDBObject>();
			List<BasicDBObject> aliasStatusConditionLst = new ArrayList<BasicDBObject>();
			BasicDBObject paymentTypeQuery = new BasicDBObject();
			BasicDBObject transactionTypeQuery = new BasicDBObject();

			boolean isParameterised = false;
			List<BasicDBObject> dateIndexConditionList = new ArrayList<BasicDBObject>();
			BasicDBObject dateIndexConditionQuery = new BasicDBObject();

			dateQuery.put(FieldType.CREATE_DATE.getName(),
					BasicDBObjectBuilder.start("$gte", new SimpleDateFormat(fromDate).toLocalizedPattern())
							.add("$lte", new SimpleDateFormat(toDate).toLocalizedPattern()).get());

			DateFormat format = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss", Locale.ENGLISH);
			Date dateStart = format.parse(fromDate);
			Date dateEnd = format.parse(toDate);

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

			/*
			 * if (!merchantPayId.equalsIgnoreCase("ALL")) { paramConditionLst.add(new
			 * BasicDBObject(FieldType.PAY_ID.getName(), merchantPayId));
			 * 
			 * }
			 */

			if (merchantPayId.equalsIgnoreCase("ALL")) {
				logger.info("TxnReport, MerchantId : " + merchantPayId + ", Segment : " + user.getSegment());
				if (!user.getSegment().equalsIgnoreCase("Default")) {
					List<String> payIdLst = userdao.getPayIdForSplitPaymentMerchant(user.getSegment());
					logger.info("Get PayId List : " + payIdLst);
					if (payIdLst.size() > 0) {
						paramConditionLst
								.add(new BasicDBObject(FieldType.PAY_ID.getName(), new BasicDBObject("$in", payIdLst)));
					}
				}

			} else {
				// isParameterised = true;
				paramConditionLst.add(new BasicDBObject(FieldType.PAY_ID.getName(), merchantPayId));
			}

			if (user.getUserType().equals(UserType.RESELLER)) {
				paramConditionLst.add(new BasicDBObject(FieldType.RESELLER_ID.getName(), user.getResellerId()));

			}

			if (!tenantId.equalsIgnoreCase("ALL")) {
				paramConditionLst.add(new BasicDBObject(FieldType.TENANT_ID.getName(), tenantId));

			}

			if (!pgRefNum.isEmpty()) {
				isParameterised = true;
				paramConditionLst.add(new BasicDBObject(FieldType.PG_REF_NUM.getName(), pgRefNum));
			}
			if (!orderId.isEmpty()) {
				isParameterised = true;
				paramConditionLst.add(new BasicDBObject(FieldType.ORDER_ID.getName(), orderId));
			}
			if (!customerEmail.isEmpty()) {
				isParameterised = true;
				paramConditionLst.add(new BasicDBObject(FieldType.CUST_EMAIL.getName(), customerEmail.trim()));
			}

			if (!customerPhone.isEmpty()) {
				isParameterised = true;
				paramConditionLst.add(new BasicDBObject(FieldType.CUST_PHONE.getName(), customerPhone.trim()));
			}
			if (!StringUtils.isBlank(rrn)) {
				isParameterised = true;
				paramConditionLst.add(new BasicDBObject(FieldType.RRN.getName(), rrn.trim()));
			}
			if (!StringUtils.isBlank(ipAddress)) {
				// isParameterised = true;
				paramConditionLst.add(new BasicDBObject(FieldType.INTERNAL_CUST_IP.getName(), ipAddress));
				logger.info("Inside TransactionSearchAction  ipAddress, =================================== = "
						+ ipAddress);
			}

			if (StringUtils.isNotEmpty(totalAmount)) {

				String[] total = StringUtils.split(totalAmount, ".");

				if (total.length == 1) {
					totalAmount = totalAmount + ".00";
					logger.info(" totalAmount, =======<1========================= = " + totalAmount);
				}
				paramConditionLst.add(new BasicDBObject(FieldType.TOTAL_AMOUNT.getName(), totalAmount));
				logger.info(
						"Inside txnReports.java  totalAmount, ==========s========================= = " + totalAmount);

			}

			// added by vijaylaxmi
			/* added by vijaylaxmi */
			/*
			 * LocalDateTime startDateTime = null; LocalDateTime endDateTime = null; if
			 * (startTime != null && !startTime.isEmpty()) {
			 * 
			 * } if (endTime != null && !endTime.isEmpty()) {
			 * 
			 * } logger.info("startDateTime ==========" + startDateTime +
			 * "endDateTime========================= = " + endDateTime);
			 * 
			 * if (!startTime.isEmpty() && !endTime.isEmpty()) {
			 * 
			 * String[] startTimearr = StringUtils.split(startTime,":"); if
			 * (startTimearr.length == 2) { startTime = startTime + ":01";
			 * logger.info("newSearchPaymentCount++++++++++++++++++++++++++++++" + startTime
			 * );
			 * 
			 * }
			 * 
			 * //DateTimeFormatter parseFormat = new
			 * DateTimeFormatterBuilder().appendPattern("hh:mm:ss").toFormatter();
			 * DateTimeFormatter parseFormat = DateTimeFormatter.ofPattern("H:mm:ss");
			 * 
			 * LocalTime lStartTime = LocalTime.parse(startTime,parseFormat); startDateTime
			 * = LocalDateTime.of(incrementingDate, lStartTime);
			 * 
			 * String[] endTimearr = StringUtils.split(endTime,":"); if (endTimearr.length
			 * == 2) { //String str = "12:40:12";
			 * 
			 * endTime = endTime + ":59";
			 * logger.info("newSearchPaymentCount++++++++++++++++++++++++++++++" + endTime
			 * );
			 * 
			 * } LocalTime lEndTime = LocalTime.parse(endTime,parseFormat); endDateTime =
			 * LocalDateTime.of(endDate, lEndTime);
			 * 
			 * isParameterised = true; BasicDBObject timeQuery = new BasicDBObject();
			 * timeQuery.put(FieldType.CREATE_DATE.getName(),
			 * BasicDBObjectBuilder.start("$gte",
			 * startDateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd hh:mm:ss")))
			 * .add("$lte",
			 * endDateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd hh:mm:ss"))).get()
			 * ); paramConditionLst.add(timeQuery);
			 * logger.info("timeQuery, ==========s========================= = " +
			 * timeQuery);
			 * 
			 * logger.
			 * info("startDateTime.format(DateTimeFormatter.ofPattern(\"yyyy-MM-dd hh:mm:ss\")))), ==========s========================= = "
			 * + endDateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd hh:mm:ss")));
			 * logger.
			 * info("endDateTime.format(DateTimeFormatter.ofPattern(\"yyyy-MM-dd hh:mm:ss\")))), ==========s========================= = "
			 * + startDateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd hh:mm:ss")));
			 * 
			 * 
			 * }
			 */
			if (!currency.equalsIgnoreCase("ALL")) {
				paramConditionLst.add(new BasicDBObject(FieldType.CURRENCY_CODE.getName(), currency));
			} else {

			}

			if (!transactionType.equalsIgnoreCase("ALL")) {
				paramConditionLst.add(new BasicDBObject(FieldType.ORIG_TXNTYPE.getName(), transactionType));
			}

			if (!paramConditionLst.isEmpty()) {
				allParamQuery = new BasicDBObject("$and", paramConditionLst);
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
			if (!aliasStatus.equalsIgnoreCase("ALL")) {
				List<String> aliasStatusList = Arrays.asList(aliasStatus.split(","));
				for (String status : aliasStatusList) {
					aliasStatusConditionLst.add(new BasicDBObject(FieldType.STATUS.getName(), status.trim()));
				}
				transactionStatusQuery.append("$or", aliasStatusConditionLst);
			}

			List<BasicDBObject> allConditionQueryList = new ArrayList<BasicDBObject>();

			if (!isParameterised) {
				if (!dateQuery.isEmpty()) {
					allConditionQueryList.add(dateQuery);
				}
				if (!dateIndexConditionQuery.isEmpty()) {
					allConditionQueryList.add(dateIndexConditionQuery);
				}
			}

			List<BasicDBObject> fianlList = new ArrayList<BasicDBObject>();

			if (!allParamQuery.isEmpty()) {
				fianlList.add(allParamQuery);
			}

			if (!allConditionQueryList.isEmpty()) {
				BasicDBObject allConditionQueryObj = new BasicDBObject("$and", allConditionQueryList);
				if (!allConditionQueryObj.isEmpty()) {
					fianlList.add(allConditionQueryObj);
				}
			}

			if (!acquirerQuery.isEmpty()) {
				fianlList.add(acquirerQuery);
			}

			if (!mopTypeQuery.isEmpty()) {
				fianlList.add(mopTypeQuery);
			}
			if (!paymentTypeQuery.isEmpty()) {
				fianlList.add(paymentTypeQuery);
			}
			if (!transactionStatusQuery.isEmpty()) {
				fianlList.add(transactionStatusQuery);
			}

			if (!transactionTypeQuery.isEmpty()) {
				fianlList.add(transactionTypeQuery);
			}

			BasicDBObject finalquery = new BasicDBObject("$and", fianlList);
			logger.info("Inside TxnReports , newsearchPaymentCount , finalquery = " + finalquery);
			BasicDBObject match = new BasicDBObject("$match", finalquery);
			Document firstGroup = new Document("_id", new Document("PG_REF_NUM", "$PG_REF_NUM")
					.append("STATUS", "$STATUS").append("TXNTYPE", "$TXNTYPE"));
			BasicDBObject firstGroupObject = new BasicDBObject(firstGroup);
			BasicDBObject secondGroup = new BasicDBObject("$last", "$$ROOT");
			BasicDBObject groupQuery = new BasicDBObject("$group", firstGroupObject.append("contentList", secondGroup));
			return coll.aggregate(Arrays.asList(match, groupQuery)).into(new ArrayList<>()).size();
		} catch (Exception e) {
			logger.error("Exception in search payment for admin", e);
		}
		return 0;
	}

	public List<TransactionSearchNew> newSearchPayment(String pgRefNum, String orderId, String customerEmail,
			String customerPhone, String paymentType, String aliasStatus, String currency, String transactionType,
			String mopType, String acquirer, String merchantPayId, String fromDate, String toDate, User user, int start,
			int length, String tenantId, String ipAddress, String totalAmount, String rrn) {

		List<TransactionSearchNew> transactionList = new ArrayList<TransactionSearchNew>();

		try {

			MongoDatabase dbIns = mongoInstance.getDB();
			MongoCollection<Document> coll = dbIns.getCollection(
					PropertiesManager.propertiesMap.get(prefix + Constants.TRANSACTION_STATUS_COLLECTION.getValue()));

			List<BasicDBObject> paramConditionLst = new ArrayList<BasicDBObject>();
			BasicDBObject allParamQuery = new BasicDBObject();
			BasicDBObject dateQuery = new BasicDBObject();
			BasicDBObject acquirerQuery = new BasicDBObject();
			List<BasicDBObject> acquirerConditionLst = new ArrayList<BasicDBObject>();
			List<BasicDBObject> mopTypeConditionLst = new ArrayList<BasicDBObject>();
			BasicDBObject mopTypeQuery = new BasicDBObject();
			BasicDBObject transactionStatusQuery = new BasicDBObject();
			List<BasicDBObject> paymentTypeConditionLst = new ArrayList<BasicDBObject>();
			BasicDBObject paymentTypeQuery = new BasicDBObject();
			BasicDBObject transactionTypeQuery = new BasicDBObject();
			List<BasicDBObject> aliasStatusConditionLst = new ArrayList<BasicDBObject>();

			boolean isParameterised = false;
			List<BasicDBObject> dateIndexConditionList = new ArrayList<BasicDBObject>();
			BasicDBObject dateIndexConditionQuery = new BasicDBObject();

			dateQuery.put(FieldType.CREATE_DATE.getName(),
					BasicDBObjectBuilder.start("$gte", new SimpleDateFormat(fromDate).toLocalizedPattern())
							.add("$lte", new SimpleDateFormat(toDate).toLocalizedPattern()).get());

			DateFormat format = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss", Locale.ENGLISH);
			Date dateStart = format.parse(fromDate);
			Date dateEnd = format.parse(toDate);

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

			/*
			 * if (!merchantPayId.equalsIgnoreCase("ALL")) { paramConditionLst.add(new
			 * BasicDBObject(FieldType.PAY_ID.getName(), merchantPayId)); }
			 */

			if (merchantPayId.equalsIgnoreCase("ALL")) {
				logger.info("TxnReport, MerchantId : " + merchantPayId + ", Segment : " + user.getSegment());
				if (!user.getSegment().equalsIgnoreCase("Default")) {
					List<String> payIdLst = userdao.getPayIdForSplitPaymentMerchant(user.getSegment());
					logger.info("Get PayId List : " + payIdLst);
					if (payIdLst.size() > 0) {
						paramConditionLst
								.add(new BasicDBObject(FieldType.PAY_ID.getName(), new BasicDBObject("$in", payIdLst)));
					}
				}

			} else {
				// isParameterised = true;
				paramConditionLst.add(new BasicDBObject(FieldType.PAY_ID.getName(), merchantPayId));
			}

			if (user.getUserType().equals(UserType.RESELLER)) {
				paramConditionLst.add(new BasicDBObject(FieldType.RESELLER_ID.getName(), user.getResellerId()));
			}

			if (!tenantId.equalsIgnoreCase("ALL")) {
				paramConditionLst.add(new BasicDBObject(FieldType.PAY_ID.getName(), tenantId));

			}

			if (!pgRefNum.isEmpty()) {
				isParameterised = true;
				paramConditionLst.add(new BasicDBObject(FieldType.PG_REF_NUM.getName(), pgRefNum));
			}
			if (!orderId.isEmpty()) {
				isParameterised = true;
				paramConditionLst.add(new BasicDBObject(FieldType.ORDER_ID.getName(), orderId));
			}
			if (!customerEmail.isEmpty()) {
				isParameterised = true;
				paramConditionLst.add(new BasicDBObject(FieldType.CUST_EMAIL.getName(), customerEmail.trim()));
			}

			if (!customerPhone.isEmpty()) {
				isParameterised = true;
				paramConditionLst.add(new BasicDBObject(FieldType.CUST_PHONE.getName(), customerPhone.trim()));
			}
			if (!StringUtils.isBlank(rrn)) {
				isParameterised = true;
				paramConditionLst.add(new BasicDBObject(FieldType.RRN.getName(), rrn.trim()));
			}
			if (StringUtils.isNotEmpty(ipAddress)) {
				paramConditionLst.add(new BasicDBObject(FieldType.INTERNAL_CUST_IP.getName(), ipAddress));
				logger.info("Inside txnReports.java ipAddress, =================================== = " + ipAddress);
			}

			if (StringUtils.isNotEmpty(totalAmount)) {

				String[] total = StringUtils.split(totalAmount, ".");

				if (total.length == 1) {
					totalAmount = totalAmount + ".00";
					logger.info(" totalAmount, =======<1========================= = " + totalAmount);
				}
				paramConditionLst.add(new BasicDBObject(FieldType.TOTAL_AMOUNT.getName(), totalAmount));
				logger.info(
						"Inside txnReports.java  totalAmount, ==========s========================= = " + totalAmount);

			}

			// added by vijaylaxmi

			/* added by vijaylaxmi */
			/*
			 * LocalDateTime startDateTime = null; LocalDateTime endDateTime = null; if
			 * (startTime != null && !startTime.isEmpty()) {
			 * 
			 * } if (endTime != null && !endTime.isEmpty()) {
			 * 
			 * } logger.info("startDateTime ==========" + startDateTime +
			 * "endDateTime========================= = " + endDateTime);
			 * 
			 * if (!startTime.isEmpty() && !endTime.isEmpty()) {
			 * 
			 * String[] startTimearr = StringUtils.split(startTime,":"); if
			 * (startTimearr.length == 2) { //String str = "12:40:12"; startTime = startTime
			 * + ":01"; logger.info("newSearchPayment++++++++++++++++++++++++++++++" +
			 * startTime );
			 * 
			 * } DateTimeFormatter parseFormat = DateTimeFormatter.ofPattern("H:mm:ss");
			 * 
			 * // DateTimeFormatter parseFormat = new
			 * DateTimeFormatterBuilder().appendPattern("hh:mm:ss").toFormatter(); LocalTime
			 * lStartTime = LocalTime.parse(startTime,parseFormat); startDateTime =
			 * LocalDateTime.of(incrementingDate, lStartTime);
			 * 
			 * String[] endTimearr = StringUtils.split(endTime,":"); if (endTimearr.length
			 * == 2) { //String str = "12:40:12";
			 * 
			 * endTime = endTime + ":59";
			 * logger.info("newSearchPayment++++++++++++++++++++++++++++++" + endTime);
			 * 
			 * } LocalTime lEndTime = LocalTime.parse(endTime,parseFormat); endDateTime =
			 * LocalDateTime.of(endDate, lEndTime); isParameterised = true; BasicDBObject
			 * timeQuery = new BasicDBObject();
			 * timeQuery.put(FieldType.CREATE_DATE.getName(),
			 * BasicDBObjectBuilder.start("$gte",
			 * startDateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd hh:mm:ss")))
			 * .add("$lte",
			 * endDateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd hh:mm:ss"))).get()
			 * ); paramConditionLst.add(timeQuery);
			 * logger.info("timeQuery, ==========s========================= = " +
			 * timeQuery);
			 * 
			 * logger.
			 * info("startDateTime.format(DateTimeFormatter.ofPattern(\"yyyy-MM-dd hh:mm:ss\")))), ==========s========================= = "
			 * + endDateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd hh:mm:ss")));
			 * logger.
			 * info("endDateTime.format(DateTimeFormatter.ofPattern(\"yyyy-MM-dd hh:mm:ss\")))), ==========s========================= = "
			 * + startDateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd hh:mm:ss")));
			 * 
			 * 
			 * }
			 * 
			 */
			if (!currency.equalsIgnoreCase("ALL")) {
				paramConditionLst.add(new BasicDBObject(FieldType.CURRENCY_CODE.getName(), currency));
			} else {

			}

			if (!transactionType.equalsIgnoreCase("ALL")) {
				paramConditionLst.add(new BasicDBObject(FieldType.ORIG_TXNTYPE.getName(), transactionType));
			}

			if (!paramConditionLst.isEmpty()) {
				allParamQuery = new BasicDBObject("$and", paramConditionLst);
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
			if (!aliasStatus.equalsIgnoreCase("ALL")) {
				List<String> aliasStatusList = Arrays.asList(aliasStatus.split(","));
				for (String status : aliasStatusList) {
					aliasStatusConditionLst.add(new BasicDBObject(FieldType.STATUS.getName(), status.trim()));
				}
				transactionStatusQuery.append("$or", aliasStatusConditionLst);
			}

			List<BasicDBObject> allConditionQueryList = new ArrayList<BasicDBObject>();

			if (!isParameterised) {
				if (!dateQuery.isEmpty()) {
					allConditionQueryList.add(dateQuery);
				}
				if (!dateIndexConditionQuery.isEmpty()) {
					allConditionQueryList.add(dateIndexConditionQuery);
				}
			}

			List<BasicDBObject> fianlList = new ArrayList<BasicDBObject>();

			if (!allParamQuery.isEmpty()) {
				fianlList.add(allParamQuery);
			}

			if (!allConditionQueryList.isEmpty()) {
				BasicDBObject allConditionQueryObj = new BasicDBObject("$and", allConditionQueryList);
				if (!allConditionQueryObj.isEmpty()) {
					fianlList.add(allConditionQueryObj);
				}
			}

			if (!acquirerQuery.isEmpty()) {
				fianlList.add(acquirerQuery);
			}

			if (!mopTypeQuery.isEmpty()) {
				fianlList.add(mopTypeQuery);
			}
			if (!paymentTypeQuery.isEmpty()) {
				fianlList.add(paymentTypeQuery);
			}
			if (!transactionStatusQuery.isEmpty()) {
				fianlList.add(transactionStatusQuery);
			}

			if (!transactionTypeQuery.isEmpty()) {
				fianlList.add(transactionTypeQuery);
			}

			BasicDBObject finalquery = new BasicDBObject("$and", fianlList);

			logger.info("1- Inside TxnReports , newSearchPayment method called, finalquery = " + finalquery);

			BasicDBObject match = new BasicDBObject("$match", finalquery);

			BasicDBObject sort = new BasicDBObject("$sort", new BasicDBObject("CREATE_DATE", -1));
			BasicDBObject skip = new BasicDBObject("$skip", start);
			BasicDBObject limit = new BasicDBObject("$limit", length);

			Document firstGroup = new Document("_id", new Document("PG_REF_NUM", "$PG_REF_NUM")
					.append("STATUS", "$STATUS").append("TXNTYPE", "$TXNTYPE"));
			BasicDBObject firstGroupObject = new BasicDBObject(firstGroup);
			BasicDBObject secondGroup = new BasicDBObject("$last", "$$ROOT");
			BasicDBObject groupQuery = new BasicDBObject("$group", firstGroupObject.append("contentList", secondGroup));
			BasicDBObject replacedRoot = new BasicDBObject("newRoot", "$contentList");
			BasicDBObject replaceRootQuery = new BasicDBObject("$replaceRoot", replacedRoot);
			List<Bson> pipeline = Arrays.asList(match, groupQuery, replaceRootQuery, sort, skip, limit);

			AggregateIterable<Document> output = coll.aggregate(pipeline);
			output.allowDiskUse(true);
			MongoCursor<Document> cursor = output.iterator();

			while (cursor.hasNext()) {
				Document dbobj = cursor.next();
				TransactionSearchNew transReport = new TransactionSearchNew();
				transReport.setTransactionIdString(dbobj.getString(FieldType.TXN_ID.toString()));
				transReport.setPgRefNum(dbobj.getString(FieldType.PG_REF_NUM.toString()));
				transReport.setPayId(dbobj.getString(FieldType.PAY_ID.toString()));

				if (StringUtils.isNotBlank(dbobj.getString(FieldType.UDF6.getName()))) {
					transReport.setUdf6(dbobj.getString(FieldType.UDF6.getName()));
					transReport.setSplitPayment("True");
				} else {
					transReport.setUdf6(dbobj.getString(CrmFieldConstants.NA.getValue()));
					transReport.setSplitPayment("False");
				}

				if (null != dbobj.getString(FieldType.UDF4.toString())) {
					transReport.setUdf4(dbobj.getString(FieldType.UDF4.toString()));
				} else {
					transReport.setUdf4(dbobj.getString(CrmFieldConstants.NA.getValue()));
				}

				if (null != dbobj.getString(FieldType.UDF5.toString())) {
					transReport.setUdf5(dbobj.getString(FieldType.UDF5.toString()));
				} else {
					transReport.setUdf5(dbobj.getString(CrmFieldConstants.NA.getValue()));
				}

				if (null != dbobj.getString(FieldType.UDF6.toString())) {
					transReport.setUdf6(dbobj.getString(FieldType.UDF6.toString()));
				} else {
					transReport.setUdf6(dbobj.getString(CrmFieldConstants.NA.getValue()));
				}

				// end by deep

				if (StringUtils.isNotBlank(dbobj.getString(FieldType.UDF6.getName()))) {
					transReport.setUdf6(dbobj.getString(FieldType.UDF6.getName()));
					transReport.setSplitPayment("True");
				} else {
					transReport.setUdf6(dbobj.getString(CrmFieldConstants.NA.getValue()));
					transReport.setSplitPayment("False");
				}

				if (null != dbobj.getString(FieldType.UDF4.toString())) {
					transReport.setUdf4(dbobj.getString(FieldType.UDF4.toString()));
				} else {
					transReport.setUdf4(dbobj.getString(CrmFieldConstants.NA.getValue()));
				}

				if (null != dbobj.getString(FieldType.PSPNAME.toString())) {
					transReport.setPspName(dbobj.getString(FieldType.PSPNAME.toString()));
				} else {
					transReport.setPspName(dbobj.getString(CrmFieldConstants.NA.getValue()));
				}

				if (null != dbobj.getString(FieldType.UDF5.toString())) {
					transReport.setUdf5(dbobj.getString(FieldType.UDF5.toString()));
				} else {
					transReport.setUdf5(dbobj.getString(CrmFieldConstants.NA.getValue()));
				}

				if (null != dbobj.getString(FieldType.UDF6.toString())) {
					transReport.setUdf6(dbobj.getString(FieldType.UDF6.toString()));
				} else {
					transReport.setUdf6(dbobj.getString(CrmFieldConstants.NA.getValue()));
				}

				// end by deep

				if (null != dbobj.getString(FieldType.CARD_HOLDER_NAME.toString())) {
					transReport.setCardHolderName(dbobj.getString(FieldType.CARD_HOLDER_NAME.toString()));
				} else if (null != dbobj.getString(FieldType.CUST_NAME.toString())) {
					transReport.setCustomerName(dbobj.getString(FieldType.CUST_NAME.toString()));
				} else {
					transReport.setCardHolderName(CrmFieldConstants.NA.getValue());
					transReport.setCustomerName(CrmFieldConstants.NA.getValue());
				}

				if (null != dbobj.getString(FieldType.CUST_EMAIL.toString())) {
					transReport.setCustomerEmail(dbobj.getString(FieldType.CUST_EMAIL.toString()));
				} else {
					transReport.setCustomerEmail(CrmFieldConstants.NA.getValue());
				}

				if (null != dbobj.getString(FieldType.UPDATEDBY.toString())) {
					transReport.setUpdatedBy(dbobj.getString(FieldType.UPDATEDBY.toString()));
				} else {
					transReport.setUpdatedBy(CrmFieldConstants.NA.getValue());
				}
				if (null != dbobj.getString(FieldType.UPDATEDAT.toString())) {
					transReport.setUpdatedAt(dbobj.getString(FieldType.UPDATEDAT.toString()));
				} else {
					transReport.setUpdatedAt(CrmFieldConstants.NA.getValue());
				}

				if (null != dbobj.getString(FieldType.CUST_PHONE.toString())) {
					transReport.setCustomerPhone(dbobj.getString(FieldType.CUST_PHONE.toString()));
				} else {
					transReport.setCustomerPhone(CrmFieldConstants.NA.getValue());
				}
				if (null != dbobj.getString(FieldType.ACCT_ID.toString())) {
					transReport.setTransactRef(dbobj.getString(FieldType.ACCT_ID.toString()));
				} else {
					transReport.setTransactRef(dbobj.getString(CrmFieldConstants.NA.getValue()));
				}
				transReport.setMerchants(dbobj.getString(CrmFieldType.BUSINESS_NAME.getName()));
				String payid = (String) dbobj.get(FieldType.PAY_ID.getName());

				User user1 = new User();
				if (userMap.get(payid) != null && !userMap.get(payid).getPayId().isEmpty()) {
					user1 = userMap.get(payid);
				} else {
					user1 = userdao.findPayId(payid);
					userMap.put(payid, user1);
				}

				if (user1 == null) {
					transReport.setMerchants(CrmFieldConstants.NA.getValue());
				} else {
					transReport.setMerchants(user1.getBusinessName());
				}

				if (null != dbobj.getString(FieldType.ORIG_TXNTYPE.toString())) {
					transReport.setTxnType(dbobj.getString(FieldType.ORIG_TXNTYPE.toString()));
				}

				if (null != dbobj.getString(FieldType.MOP_TYPE.toString())) {
					transReport.setMopType(MopType.getmopName(dbobj.getString(FieldType.MOP_TYPE.toString())));
				} else {
					transReport.setMopType(CrmFieldConstants.NOT_AVAILABLE.getValue());
				}
				if (null != dbobj.getString(FieldType.PAYMENT_TYPE.toString())) {
					transReport.setPaymentMethods(
							PaymentType.getpaymentName(dbobj.getString(FieldType.PAYMENT_TYPE.toString())));
				} else {
					transReport.setPaymentMethods(CrmFieldConstants.NA.getValue());
				}

				if (null != dbobj.getString(FieldType.PAYMENT_TYPE.toString())) {
					if (null != dbobj.getString(FieldType.CARD_MASK.toString())) {
						transReport.setCardNumber(dbobj.getString(FieldType.CARD_MASK.toString()));
					} else if ((dbobj.getString(FieldType.PAYMENT_TYPE.getName()))
							.equals(PaymentType.NET_BANKING.getCode())) {
						transReport.setCardNumber(CrmFieldConstants.NET_BANKING.getValue());
					} else if ((dbobj.getString(FieldType.PAYMENT_TYPE.getName()))
							.equals(PaymentType.WALLET.getCode())) {
						transReport.setCardNumber(CrmFieldConstants.WALLET.getValue());
					}
				} else {
					transReport.setCardNumber(CrmFieldConstants.NA.getValue());
				}
				transReport.setRrn(dbobj.getString(FieldType.RRN.toString()));

				transReport.setIpaddress(dbobj.getString(FieldType.INTERNAL_CUST_IP.toString()));

				transReport.setCardMask(dbobj.getString(FieldType.CARD_MASK.toString()));

				transReport.setRrn(dbobj.getString(FieldType.RRN.toString()));
				if (String.valueOf(dbobj.get(FieldType.PAYMENT_TYPE.getName()))
						.equalsIgnoreCase(PaymentTypeUI.CREDIT_CARD.getCode())
						|| String.valueOf(dbobj.get(FieldType.PAYMENT_TYPE.getName()))
								.equalsIgnoreCase(PaymentTypeUI.DEBIT_CARD.getCode())
						|| String.valueOf(dbobj.get(FieldType.PAYMENT_TYPE.getName()))
								.equalsIgnoreCase(PaymentTypeUI.NET_BANKING.getCode())) {
					transReport.setCardHolderType(String.valueOf(
							dbobj.get(FieldType.CARD_HOLDER_TYPE.getName()) == null ? CrmFieldConstants.NA.getValue()
									: dbobj.get(FieldType.CARD_HOLDER_TYPE.getName())));
				} else {
					transReport.setCardHolderType(CrmFieldConstants.NA.getValue());
				}
				transReport.setStatus(dbobj.getString(FieldType.STATUS.toString()));
				transReport.setDateFrom(dbobj.getString(FieldType.CREATE_DATE.getName()));
				transReport.setAmount(dbobj.getString(FieldType.AMOUNT.toString()));
				transReport.setTotalAmount(dbobj.getString(FieldType.TOTAL_AMOUNT.toString()));
				transReport.setOrderId(dbobj.getString(FieldType.ORDER_ID.toString()));
				transReport.setIpaddress(dbobj.getString(FieldType.INTERNAL_CUST_IP.toString()));
				if (StringUtils.isNotBlank(dbobj.getString(FieldType.REFUND_ORDER_ID.toString()))) {
					transReport.setRefundOrderId(dbobj.getString(FieldType.REFUND_ORDER_ID.toString()));
				} else {
					transReport.setRefundOrderId(CrmFieldConstants.NA.getValue());
				}
				transReport.setoId(dbobj.getString(FieldType.OID.toString()));

				if (null != dbobj.getString(FieldType.CURRENCY_CODE.toString())) {
					transReport.setCurrency(dbobj.getString(FieldType.CURRENCY_CODE.toString()));
				} else {
					transReport.setCurrency(CrmFieldConstants.NA.getValue());
				}

				if (null != dbobj.getString(FieldType.ACQUIRER_TYPE.toString())) {
					transReport.setAcquirerType(dbobj.getString(FieldType.ACQUIRER_TYPE.toString()));
				}

				if (StringUtils.isNotBlank(dbobj.getString(FieldType.PG_TDR_SC.toString()))) {
					transReport.setPgSurchargeAmount(
							String.format("%.2f", ((Double.valueOf(dbobj.getString(FieldType.PG_TDR_SC.toString()))))));
				} else {
					transReport.setPgSurchargeAmount("0.00");
				}

				if (StringUtils.isNotBlank(dbobj.getString(FieldType.ACQUIRER_TDR_SC.toString()))) {
					transReport.setAcquirerSurchargeAmount(String.format("%.2f",
							(Double.valueOf(dbobj.getString(FieldType.ACQUIRER_TDR_SC.toString())))));
				} else {
					transReport.setAcquirerSurchargeAmount("0.00");
				}

				if (StringUtils.isNotBlank(dbobj.getString(FieldType.PG_GST.toString()))) {
					transReport.setPgGst(
							String.format("%.2f", ((Double.valueOf(dbobj.getString(FieldType.PG_GST.toString()))))));
				} else {
					transReport.setPgGst("0.00");
				}

				if (StringUtils.isNotBlank(dbobj.getString(FieldType.ACQUIRER_GST.toString()))) {
					transReport.setAcquirerGst(String.format("%.2f",
							(Double.valueOf(dbobj.getString(FieldType.ACQUIRER_GST.toString())))));
				} else {
					transReport.setAcquirerGst("0.00");
				}
				transactionList.add(transReport);
			}
			cursor.close();
			logger.info("Inside TxnReports , searchPayment , transactionListSize = " + transactionList.size());
			return transactionList;

		} catch (Exception e) {
			logger.error("Exception in search payment for admin", e);
		}
		return transactionList;
	}

	public int newSearchPaymentCountForSplitPayment(String pgRefNum, String orderId, String customerEmail,
			String customerPhone, String paymentType, String aliasStatus, String currency, String transactionType,
			String mopType, String acquirer, String merchantPayId, String fromDate, String toDate, User user, int start,
			int length, String tenantId, String ipAddress, String totalAmount) {

		try {

			MongoDatabase dbIns = mongoInstance.getDB();
			MongoCollection<Document> coll = dbIns.getCollection(
					PropertiesManager.propertiesMap.get(prefix + Constants.TRANSACTION_STATUS_COLLECTION.getValue()));

			List<BasicDBObject> paramConditionLst = new ArrayList<BasicDBObject>();
			BasicDBObject allParamQuery = new BasicDBObject();
			BasicDBObject dateQuery = new BasicDBObject();
			BasicDBObject acquirerQuery = new BasicDBObject();
			List<BasicDBObject> acquirerConditionLst = new ArrayList<BasicDBObject>();
			List<BasicDBObject> mopTypeConditionLst = new ArrayList<BasicDBObject>();
			BasicDBObject mopTypeQuery = new BasicDBObject();
			BasicDBObject transactionStatusQuery = new BasicDBObject();
			List<BasicDBObject> paymentTypeConditionLst = new ArrayList<BasicDBObject>();
			List<BasicDBObject> aliasStatusConditionLst = new ArrayList<BasicDBObject>();
			BasicDBObject paymentTypeQuery = new BasicDBObject();
			BasicDBObject transactionTypeQuery = new BasicDBObject();

			boolean isParameterised = false;
			List<BasicDBObject> dateIndexConditionList = new ArrayList<BasicDBObject>();
			BasicDBObject dateIndexConditionQuery = new BasicDBObject();

			dateQuery.put(FieldType.CREATE_DATE.getName(),
					BasicDBObjectBuilder.start("$gte", new SimpleDateFormat(fromDate).toLocalizedPattern())
							.add("$lte", new SimpleDateFormat(toDate).toLocalizedPattern()).get());

			DateFormat format = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss", Locale.ENGLISH);
			Date dateStart = format.parse(fromDate);
			Date dateEnd = format.parse(toDate);

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

			/*
			 * if (!merchantPayId.equalsIgnoreCase("ALL")) { paramConditionLst.add(new
			 * BasicDBObject(FieldType.PAY_ID.getName(), merchantPayId));
			 * 
			 * }
			 */

			/*
			 * if (merchantPayId.equalsIgnoreCase("ALL")) { List<String> payIdLst =
			 * userdao.getPayIdForSplitPaymentMerchant(user.getSegment());
			 * logger.info("Get PayId For SplitPayment Merchant : "+payIdLst);
			 * if(payIdLst.size() > 0) { //isParameterised = true; paramConditionLst.add(new
			 * BasicDBObject(FieldType.PAY_ID.getName(), new BasicDBObject("$in",
			 * payIdLst))); } } else { //isParameterised = true; paramConditionLst.add(new
			 * BasicDBObject(FieldType.PAY_ID.getName(), merchantPayId)); }
			 */

			if (merchantPayId.equalsIgnoreCase("ALL")) {
				logger.info("TxnReport, MerchantId : " + merchantPayId + ", Segment : " + user.getSegment());
				if (!user.getSegment().equalsIgnoreCase("Default")) {
					List<String> payIdLst = userdao.getPayIdForSplitPaymentMerchant(user.getSegment());
					logger.info("Get PayId List : " + payIdLst);
					if (payIdLst.size() > 0) {
						paramConditionLst
								.add(new BasicDBObject(FieldType.PAY_ID.getName(), new BasicDBObject("$in", payIdLst)));
					}
				}

			} else {
				paramConditionLst.add(new BasicDBObject(FieldType.PAY_ID.getName(), merchantPayId));
			}

			if (user.getUserType().equals(UserType.RESELLER)) {
				paramConditionLst.add(new BasicDBObject(FieldType.RESELLER_ID.getName(), user.getResellerId()));

			}

			if (!tenantId.equalsIgnoreCase("ALL")) {
				paramConditionLst.add(new BasicDBObject(FieldType.TENANT_ID.getName(), tenantId));

			}

			if (!pgRefNum.isEmpty()) {
				isParameterised = true;
				paramConditionLst.add(new BasicDBObject(FieldType.PG_REF_NUM.getName(), pgRefNum));
			}
			if (!orderId.isEmpty()) {
				isParameterised = true;
				paramConditionLst.add(new BasicDBObject(FieldType.ORDER_ID.getName(), orderId));
			}
			if (!customerEmail.isEmpty()) {
				isParameterised = true;
				paramConditionLst.add(new BasicDBObject(FieldType.CUST_EMAIL.getName(), customerEmail.trim()));
			}
			if (!customerPhone.isEmpty()) {
				isParameterised = true;
				paramConditionLst.add(new BasicDBObject(FieldType.CUST_PHONE.getName(), customerPhone.trim()));
			}
			if (!StringUtils.isBlank(ipAddress)) {
//isParameterised = true;
				paramConditionLst.add(new BasicDBObject(FieldType.INTERNAL_CUST_IP.getName(), ipAddress));
				logger.info("Inside TransactionSearchAction  ipAddress, =================================== = "
						+ ipAddress);
			}

			if (StringUtils.isNotEmpty(totalAmount)) {

				String[] total = StringUtils.split(totalAmount, ".");

				if (total.length == 1) {
					totalAmount = totalAmount + ".00";
					logger.info(" totalAmount, =======<1========================= = " + totalAmount);
				}
				paramConditionLst.add(new BasicDBObject(FieldType.TOTAL_AMOUNT.getName(), totalAmount));
				logger.info(
						"Inside txnReports.java  totalAmount, ==========s========================= = " + totalAmount);

			}

//added by vijaylaxmi
			/* added by vijaylaxmi */
			/*
			 * LocalDateTime startDateTime = null; LocalDateTime endDateTime = null; if
			 * (startTime != null && !startTime.isEmpty()) {
			 * 
			 * } if (endTime != null && !endTime.isEmpty()) {
			 * 
			 * } logger.info("startDateTime ==========" + startDateTime +
			 * "endDateTime========================= = " + endDateTime);
			 * 
			 * if (!startTime.isEmpty() && !endTime.isEmpty()) {
			 * 
			 * String[] startTimearr = StringUtils.split(startTime,":"); if
			 * (startTimearr.length == 2) { startTime = startTime + ":01";
			 * logger.info("newSearchPaymentCount++++++++++++++++++++++++++++++" + startTime
			 * );
			 * 
			 * }
			 * 
			 * //DateTimeFormatter parseFormat = new
			 * DateTimeFormatterBuilder().appendPattern("hh:mm:ss").toFormatter();
			 * DateTimeFormatter parseFormat = DateTimeFormatter.ofPattern("H:mm:ss");
			 * 
			 * LocalTime lStartTime = LocalTime.parse(startTime,parseFormat); startDateTime
			 * = LocalDateTime.of(incrementingDate, lStartTime);
			 * 
			 * String[] endTimearr = StringUtils.split(endTime,":"); if (endTimearr.length
			 * == 2) { //String str = "12:40:12";
			 * 
			 * endTime = endTime + ":59";
			 * logger.info("newSearchPaymentCount++++++++++++++++++++++++++++++" + endTime
			 * );
			 * 
			 * } LocalTime lEndTime = LocalTime.parse(endTime,parseFormat); endDateTime =
			 * LocalDateTime.of(endDate, lEndTime);
			 * 
			 * isParameterised = true; BasicDBObject timeQuery = new BasicDBObject();
			 * timeQuery.put(FieldType.CREATE_DATE.getName(),
			 * BasicDBObjectBuilder.start("$gte",
			 * startDateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd hh:mm:ss")))
			 * .add("$lte",
			 * endDateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd hh:mm:ss"))).get()
			 * ); paramConditionLst.add(timeQuery);
			 * logger.info("timeQuery, ==========s========================= = " +
			 * timeQuery);
			 * 
			 * logger.
			 * info("startDateTime.format(DateTimeFormatter.ofPattern(\"yyyy-MM-dd hh:mm:ss\")))), ==========s========================= = "
			 * + endDateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd hh:mm:ss")));
			 * logger.
			 * info("endDateTime.format(DateTimeFormatter.ofPattern(\"yyyy-MM-dd hh:mm:ss\")))), ==========s========================= = "
			 * + startDateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd hh:mm:ss")));
			 * 
			 * 
			 * }
			 */
			if (!currency.equalsIgnoreCase("ALL")) {
				paramConditionLst.add(new BasicDBObject(FieldType.CURRENCY_CODE.getName(), currency));
			} else {

			}

			if (!transactionType.equalsIgnoreCase("ALL")) {
				paramConditionLst.add(new BasicDBObject(FieldType.ORIG_TXNTYPE.getName(), transactionType));
			}

			if (!paramConditionLst.isEmpty()) {
				allParamQuery = new BasicDBObject("$and", paramConditionLst);
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
			if (!aliasStatus.equalsIgnoreCase("ALL")) {
				List<String> aliasStatusList = Arrays.asList(aliasStatus.split(","));
				for (String status : aliasStatusList) {
					aliasStatusConditionLst.add(new BasicDBObject(FieldType.ALIAS_STATUS.getName(), status.trim()));
				}
				transactionStatusQuery.append("$or", aliasStatusConditionLst);
			}

			List<BasicDBObject> allConditionQueryList = new ArrayList<BasicDBObject>();

			if (!isParameterised) {
				if (!dateQuery.isEmpty()) {
					allConditionQueryList.add(dateQuery);
				}
				if (!dateIndexConditionQuery.isEmpty()) {
					allConditionQueryList.add(dateIndexConditionQuery);
				}
			}

			List<BasicDBObject> fianlList = new ArrayList<BasicDBObject>();

			if (!allParamQuery.isEmpty()) {
				fianlList.add(allParamQuery);
			}

			if (!allConditionQueryList.isEmpty()) {
				BasicDBObject allConditionQueryObj = new BasicDBObject("$and", allConditionQueryList);
				if (!allConditionQueryObj.isEmpty()) {
					fianlList.add(allConditionQueryObj);
				}
			}

			if (!acquirerQuery.isEmpty()) {
				fianlList.add(acquirerQuery);
			}

			if (!mopTypeQuery.isEmpty()) {
				fianlList.add(mopTypeQuery);
			}
			if (!paymentTypeQuery.isEmpty()) {
				fianlList.add(paymentTypeQuery);
			}
			if (!transactionStatusQuery.isEmpty()) {
				fianlList.add(transactionStatusQuery);
			}

			if (!transactionTypeQuery.isEmpty()) {
				fianlList.add(transactionTypeQuery);
			}

			BasicDBObject finalquery = new BasicDBObject("$and", fianlList);
			logger.info("Inside TxnReports , newsearchPaymentCount , finalquery = " + finalquery);
			BasicDBObject match = new BasicDBObject("$match", finalquery);
			Document firstGroup = new Document("_id",
					new Document("PG_REF_NUM", "$PG_REF_NUM").append("STATUS", "$STATUS"));
			BasicDBObject firstGroupObject = new BasicDBObject(firstGroup);
			BasicDBObject secondGroup = new BasicDBObject("$last", "$$ROOT");
			BasicDBObject groupQuery = new BasicDBObject("$group", firstGroupObject.append("contentList", secondGroup));
			return coll.aggregate(Arrays.asList(match, groupQuery)).into(new ArrayList<>()).size();
		} catch (Exception e) {
			logger.error("Exception in search payment for admin", e);
		}
		return 0;
	}

	public List<TransactionSearchNew> newSearchPaymentForSplitPayment(String pgRefNum, String orderId,
			String customerEmail, String customerPhone, String paymentType, String aliasStatus, String currency,
			String transactionType, String mopType, String acquirer, String merchantPayId, String fromDate,
			String toDate, User user, int start, int length, String tenantId, String ipAddress, String totalAmount) {

		List<TransactionSearchNew> transactionList = new ArrayList<TransactionSearchNew>();

		try {

			MongoDatabase dbIns = mongoInstance.getDB();
			MongoCollection<Document> coll = dbIns.getCollection(
					PropertiesManager.propertiesMap.get(prefix + Constants.TRANSACTION_STATUS_COLLECTION.getValue()));

			List<BasicDBObject> paramConditionLst = new ArrayList<BasicDBObject>();
			BasicDBObject allParamQuery = new BasicDBObject();
			BasicDBObject dateQuery = new BasicDBObject();
			BasicDBObject acquirerQuery = new BasicDBObject();
			List<BasicDBObject> acquirerConditionLst = new ArrayList<BasicDBObject>();
			List<BasicDBObject> mopTypeConditionLst = new ArrayList<BasicDBObject>();
			BasicDBObject mopTypeQuery = new BasicDBObject();
			BasicDBObject transactionStatusQuery = new BasicDBObject();
			List<BasicDBObject> paymentTypeConditionLst = new ArrayList<BasicDBObject>();
			BasicDBObject paymentTypeQuery = new BasicDBObject();
			BasicDBObject transactionTypeQuery = new BasicDBObject();
			List<BasicDBObject> aliasStatusConditionLst = new ArrayList<BasicDBObject>();

			boolean isParameterised = false;
			List<BasicDBObject> dateIndexConditionList = new ArrayList<BasicDBObject>();
			BasicDBObject dateIndexConditionQuery = new BasicDBObject();

			dateQuery.put(FieldType.CREATE_DATE.getName(),
					BasicDBObjectBuilder.start("$gte", new SimpleDateFormat(fromDate).toLocalizedPattern())
							.add("$lte", new SimpleDateFormat(toDate).toLocalizedPattern()).get());

			DateFormat format = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss", Locale.ENGLISH);
			Date dateStart = format.parse(fromDate);
			Date dateEnd = format.parse(toDate);

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

			/*
			 * if (!merchantPayId.equalsIgnoreCase("ALL")) { paramConditionLst.add(new
			 * BasicDBObject(FieldType.PAY_ID.getName(), merchantPayId)); }
			 */

			/*
			 * if (merchantPayId.equalsIgnoreCase("ALL")) { List<String> payIdLst =
			 * userdao.getPayIdForSplitPaymentMerchant(user.getSegment());
			 * logger.info("Get PayId For SplitPayment Merchant : "+payIdLst);
			 * if(payIdLst.size() > 0) { //isParameterised = true; paramConditionLst.add(new
			 * BasicDBObject(FieldType.PAY_ID.getName(), new BasicDBObject("$in",
			 * payIdLst))); } } else { //isParameterised = true; paramConditionLst.add(new
			 * BasicDBObject(FieldType.PAY_ID.getName(), merchantPayId)); }
			 */

			if (merchantPayId.equalsIgnoreCase("ALL")) {
				logger.info("TxnReport, MerchantId : " + merchantPayId + ", Segment : " + user.getSegment());
				if (!user.getSegment().equalsIgnoreCase("Default")) {
					List<String> payIdLst = userdao.getPayIdForSplitPaymentMerchant(user.getSegment());
					logger.info("Get PayId List : " + payIdLst);
					if (payIdLst.size() > 0) {
						paramConditionLst
								.add(new BasicDBObject(FieldType.PAY_ID.getName(), new BasicDBObject("$in", payIdLst)));
					}
				}

			} else {
				paramConditionLst.add(new BasicDBObject(FieldType.PAY_ID.getName(), merchantPayId));
			}

			if (user.getUserType().equals(UserType.RESELLER)) {
				paramConditionLst.add(new BasicDBObject(FieldType.RESELLER_ID.getName(), user.getResellerId()));
			}

			if (!tenantId.equalsIgnoreCase("ALL")) {
				paramConditionLst.add(new BasicDBObject(FieldType.PAY_ID.getName(), tenantId));

			}

			if (!pgRefNum.isEmpty()) {
				isParameterised = true;
				paramConditionLst.add(new BasicDBObject(FieldType.PG_REF_NUM.getName(), pgRefNum));
			}
			if (!orderId.isEmpty()) {
				isParameterised = true;
				paramConditionLst.add(new BasicDBObject(FieldType.ORDER_ID.getName(), orderId));
			}
			if (!customerEmail.isEmpty()) {
				isParameterised = true;
				paramConditionLst.add(new BasicDBObject(FieldType.CUST_EMAIL.getName(), customerEmail.trim()));
			}
			if (!customerPhone.isEmpty()) {
				isParameterised = true;
				paramConditionLst.add(new BasicDBObject(FieldType.CUST_PHONE.getName(), customerPhone.trim()));
			}
			if (StringUtils.isNotEmpty(ipAddress)) {
				paramConditionLst.add(new BasicDBObject(FieldType.INTERNAL_CUST_IP.getName(), ipAddress));
				logger.info("Inside txnReports.java ipAddress, =================================== = " + ipAddress);
			}

			if (StringUtils.isNotEmpty(totalAmount)) {

				String[] total = StringUtils.split(totalAmount, ".");

				if (total.length == 1) {
					totalAmount = totalAmount + ".00";
					logger.info(" totalAmount, =======<1========================= = " + totalAmount);
				}
				paramConditionLst.add(new BasicDBObject(FieldType.TOTAL_AMOUNT.getName(), totalAmount));
				logger.info(
						"Inside txnReports.java  totalAmount, ==========s========================= = " + totalAmount);

			}

//added by vijaylaxmi

			/* added by vijaylaxmi */
			/*
			 * LocalDateTime startDateTime = null; LocalDateTime endDateTime = null; if
			 * (startTime != null && !startTime.isEmpty()) {
			 * 
			 * } if (endTime != null && !endTime.isEmpty()) {
			 * 
			 * } logger.info("startDateTime ==========" + startDateTime +
			 * "endDateTime========================= = " + endDateTime);
			 * 
			 * if (!startTime.isEmpty() && !endTime.isEmpty()) {
			 * 
			 * String[] startTimearr = StringUtils.split(startTime,":"); if
			 * (startTimearr.length == 2) { //String str = "12:40:12"; startTime = startTime
			 * + ":01"; logger.info("newSearchPayment++++++++++++++++++++++++++++++" +
			 * startTime );
			 * 
			 * } DateTimeFormatter parseFormat = DateTimeFormatter.ofPattern("H:mm:ss");
			 * 
			 * // DateTimeFormatter parseFormat = new
			 * DateTimeFormatterBuilder().appendPattern("hh:mm:ss").toFormatter(); LocalTime
			 * lStartTime = LocalTime.parse(startTime,parseFormat); startDateTime =
			 * LocalDateTime.of(incrementingDate, lStartTime);
			 * 
			 * String[] endTimearr = StringUtils.split(endTime,":"); if (endTimearr.length
			 * == 2) { //String str = "12:40:12";
			 * 
			 * endTime = endTime + ":59";
			 * logger.info("newSearchPayment++++++++++++++++++++++++++++++" + endTime);
			 * 
			 * } LocalTime lEndTime = LocalTime.parse(endTime,parseFormat); endDateTime =
			 * LocalDateTime.of(endDate, lEndTime); isParameterised = true; BasicDBObject
			 * timeQuery = new BasicDBObject();
			 * timeQuery.put(FieldType.CREATE_DATE.getName(),
			 * BasicDBObjectBuilder.start("$gte",
			 * startDateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd hh:mm:ss")))
			 * .add("$lte",
			 * endDateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd hh:mm:ss"))).get()
			 * ); paramConditionLst.add(timeQuery);
			 * logger.info("timeQuery, ==========s========================= = " +
			 * timeQuery);
			 * 
			 * logger.
			 * info("startDateTime.format(DateTimeFormatter.ofPattern(\"yyyy-MM-dd hh:mm:ss\")))), ==========s========================= = "
			 * + endDateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd hh:mm:ss")));
			 * logger.
			 * info("endDateTime.format(DateTimeFormatter.ofPattern(\"yyyy-MM-dd hh:mm:ss\")))), ==========s========================= = "
			 * + startDateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd hh:mm:ss")));
			 * 
			 * 
			 * }
			 * 
			 */
			if (!currency.equalsIgnoreCase("ALL")) {
				paramConditionLst.add(new BasicDBObject(FieldType.CURRENCY_CODE.getName(), currency));
			} else {

			}

			if (!transactionType.equalsIgnoreCase("ALL")) {
				paramConditionLst.add(new BasicDBObject(FieldType.ORIG_TXNTYPE.getName(), transactionType));
			}

			if (!paramConditionLst.isEmpty()) {
				allParamQuery = new BasicDBObject("$and", paramConditionLst);
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
			if (!aliasStatus.equalsIgnoreCase("ALL")) {
				List<String> aliasStatusList = Arrays.asList(aliasStatus.split(","));
				for (String status : aliasStatusList) {
					aliasStatusConditionLst.add(new BasicDBObject(FieldType.ALIAS_STATUS.getName(), status.trim()));
				}
				transactionStatusQuery.append("$or", aliasStatusConditionLst);
			}

			List<BasicDBObject> allConditionQueryList = new ArrayList<BasicDBObject>();

			if (!isParameterised) {
				if (!dateQuery.isEmpty()) {
					allConditionQueryList.add(dateQuery);
				}
				if (!dateIndexConditionQuery.isEmpty()) {
					allConditionQueryList.add(dateIndexConditionQuery);
				}
			}

			List<BasicDBObject> fianlList = new ArrayList<BasicDBObject>();

			if (!allParamQuery.isEmpty()) {
				fianlList.add(allParamQuery);
			}

			if (!allConditionQueryList.isEmpty()) {
				BasicDBObject allConditionQueryObj = new BasicDBObject("$and", allConditionQueryList);
				if (!allConditionQueryObj.isEmpty()) {
					fianlList.add(allConditionQueryObj);
				}
			}

			if (!acquirerQuery.isEmpty()) {
				fianlList.add(acquirerQuery);
			}

			if (!mopTypeQuery.isEmpty()) {
				fianlList.add(mopTypeQuery);
			}
			if (!paymentTypeQuery.isEmpty()) {
				fianlList.add(paymentTypeQuery);
			}
			if (!transactionStatusQuery.isEmpty()) {
				fianlList.add(transactionStatusQuery);
			}

			if (!transactionTypeQuery.isEmpty()) {
				fianlList.add(transactionTypeQuery);
			}

			BasicDBObject finalquery = new BasicDBObject("$and", fianlList);

			logger.info("1- Inside TxnReports , newSearchPayment method called, finalquery = " + finalquery);

			BasicDBObject match = new BasicDBObject("$match", finalquery);

			BasicDBObject sort = new BasicDBObject("$sort", new BasicDBObject("CREATE_DATE", -1));
			BasicDBObject skip = new BasicDBObject("$skip", start);
			BasicDBObject limit = new BasicDBObject("$limit", length);

			Document firstGroup = new Document("_id",
					new Document("PG_REF_NUM", "$PG_REF_NUM").append("STATUS", "$STATUS"));
			BasicDBObject firstGroupObject = new BasicDBObject(firstGroup);
			BasicDBObject secondGroup = new BasicDBObject("$last", "$$ROOT");
			BasicDBObject groupQuery = new BasicDBObject("$group", firstGroupObject.append("contentList", secondGroup));
			BasicDBObject replacedRoot = new BasicDBObject("newRoot", "$contentList");
			BasicDBObject replaceRootQuery = new BasicDBObject("$replaceRoot", replacedRoot);
			List<Bson> pipeline = Arrays.asList(match, groupQuery, replaceRootQuery, sort, skip, limit);

			AggregateIterable<Document> output = coll.aggregate(pipeline);
			output.allowDiskUse(true);
			MongoCursor<Document> cursor = output.iterator();

			while (cursor.hasNext()) {
				Document dbobj = cursor.next();
				TransactionSearchNew transReport = new TransactionSearchNew();
				transReport.setTransactionIdString(dbobj.getString(FieldType.TXN_ID.toString()));
				transReport.setPgRefNum(dbobj.getString(FieldType.PG_REF_NUM.toString()));
				transReport.setPayId(dbobj.getString(FieldType.PAY_ID.toString()));

				if (StringUtils.isNotBlank(dbobj.getString(FieldType.UDF6.getName()))) {
					transReport.setUdf6(dbobj.getString(FieldType.UDF6.getName()));
					transReport.setSplitPayment("True");
				} else {
					transReport.setUdf6(dbobj.getString(CrmFieldConstants.NA.getValue()));
					transReport.setSplitPayment("False");
				}

				if (null != dbobj.getString(FieldType.UDF4.toString())) {
					transReport.setUdf4(dbobj.getString(FieldType.UDF4.toString()));
				} else {
					transReport.setUdf4(dbobj.getString(CrmFieldConstants.NA.getValue()));
				}

				if (null != dbobj.getString(FieldType.UDF5.toString())) {
					transReport.setUdf5(dbobj.getString(FieldType.UDF5.toString()));
				} else {
					transReport.setUdf5(dbobj.getString(CrmFieldConstants.NA.getValue()));
				}

				if (null != dbobj.getString(FieldType.UDF6.toString())) {
					transReport.setUdf6(dbobj.getString(FieldType.UDF6.toString()));
				} else {
					transReport.setUdf6(dbobj.getString(CrmFieldConstants.NA.getValue()));
				}

//end by deep

				if (null != dbobj.getString(FieldType.CARD_HOLDER_NAME.toString())) {
					transReport.setCardHolderName(dbobj.getString(FieldType.CARD_HOLDER_NAME.toString()));
				} else if (null != dbobj.getString(FieldType.CUST_NAME.toString())) {
					transReport.setCustomerName(dbobj.getString(FieldType.CUST_NAME.toString()));
				} else {
					transReport.setCardHolderName(CrmFieldConstants.NA.getValue());
					transReport.setCustomerName(CrmFieldConstants.NA.getValue());
				}

				if (null != dbobj.getString(FieldType.CUST_EMAIL.toString())) {
					transReport.setCustomerEmail(dbobj.getString(FieldType.CUST_EMAIL.toString()));
				} else {
					transReport.setCustomerEmail(CrmFieldConstants.NA.getValue());
				}
				if (null != dbobj.getString(FieldType.CUST_PHONE.toString())) {
					transReport.setCustomerPhone(dbobj.getString(FieldType.CUST_PHONE.toString()));
				} else {
					transReport.setCustomerPhone(CrmFieldConstants.NA.getValue());
				}
				transReport.setMerchants(dbobj.getString(CrmFieldType.BUSINESS_NAME.getName()));
				String payid = (String) dbobj.get(FieldType.PAY_ID.getName());

				User user1 = new User();
				if (userMap.get(payid) != null && !userMap.get(payid).getPayId().isEmpty()) {
					user1 = userMap.get(payid);
				} else {
					user1 = userdao.findPayId(payid);
					userMap.put(payid, user1);
				}

				if (user1 == null) {
					transReport.setMerchants(CrmFieldConstants.NA.getValue());
				} else {
					transReport.setMerchants(user1.getBusinessName());
				}

				if (null != dbobj.getString(FieldType.ORIG_TXNTYPE.toString())) {
					transReport.setTxnType(dbobj.getString(FieldType.ORIG_TXNTYPE.toString()));
				}

				if (null != dbobj.getString(FieldType.MOP_TYPE.toString())) {
					transReport.setMopType(MopType.getmopName(dbobj.getString(FieldType.MOP_TYPE.toString())));
				} else {
					transReport.setMopType(CrmFieldConstants.NOT_AVAILABLE.getValue());
				}
				if (null != dbobj.getString(FieldType.PAYMENT_TYPE.toString())) {
					transReport.setPaymentMethods(
							PaymentType.getpaymentName(dbobj.getString(FieldType.PAYMENT_TYPE.toString())));
				} else {
					transReport.setPaymentMethods(CrmFieldConstants.NA.getValue());
				}

				if (null != dbobj.getString(FieldType.PAYMENT_TYPE.toString())) {
					if (null != dbobj.getString(FieldType.CARD_MASK.toString())) {
						transReport.setCardNumber(dbobj.getString(FieldType.CARD_MASK.toString()));
					} else if ((dbobj.getString(FieldType.PAYMENT_TYPE.getName()))
							.equals(PaymentType.NET_BANKING.getCode())) {
						transReport.setCardNumber(CrmFieldConstants.NET_BANKING.getValue());
					} else if ((dbobj.getString(FieldType.PAYMENT_TYPE.getName()))
							.equals(PaymentType.WALLET.getCode())) {
						transReport.setCardNumber(CrmFieldConstants.WALLET.getValue());
					}
				} else {
					transReport.setCardNumber(CrmFieldConstants.NA.getValue());
				}
				transReport.setRrn(dbobj.getString(FieldType.RRN.toString()));

				transReport.setIpaddress(dbobj.getString(FieldType.INTERNAL_CUST_IP.toString()));

				transReport.setCardMask(dbobj.getString(FieldType.CARD_MASK.toString()));

				transReport.setStatus(dbobj.getString(FieldType.STATUS.toString()));
				transReport.setDateFrom(dbobj.getString(FieldType.CREATE_DATE.getName()));
				transReport.setAmount(dbobj.getString(FieldType.AMOUNT.toString()));
				transReport.setTotalAmount(dbobj.getString(FieldType.TOTAL_AMOUNT.toString()));
				transReport.setOrderId(dbobj.getString(FieldType.ORDER_ID.toString()));
				transReport.setIpaddress(dbobj.getString(FieldType.INTERNAL_CUST_IP.toString()));
				if (StringUtils.isNotBlank(dbobj.getString(FieldType.REFUND_ORDER_ID.toString()))) {
					transReport.setRefundOrderId(dbobj.getString(FieldType.REFUND_ORDER_ID.toString()));
				} else {
					transReport.setRefundOrderId(CrmFieldConstants.NA.getValue());
				}
				transReport.setoId(dbobj.getString(FieldType.OID.toString()));

				if (null != dbobj.getString(FieldType.CURRENCY_CODE.toString())) {
					transReport.setCurrency(dbobj.getString(FieldType.CURRENCY_CODE.toString()));
				} else {
					transReport.setCurrency(CrmFieldConstants.NA.getValue());
				}

				if (null != dbobj.getString(FieldType.ACQUIRER_TYPE.toString())) {
					transReport.setAcquirerType(dbobj.getString(FieldType.ACQUIRER_TYPE.toString()));
				}

				if (StringUtils.isNotBlank(dbobj.getString(FieldType.PG_TDR_SC.toString()))) {
					transReport.setPgSurchargeAmount(
							String.format("%.2f", ((Double.valueOf(dbobj.getString(FieldType.PG_TDR_SC.toString()))))));
				} else {
					transReport.setPgSurchargeAmount("0.00");
				}

				if (StringUtils.isNotBlank(dbobj.getString(FieldType.ACQUIRER_TDR_SC.toString()))) {
					transReport.setAcquirerSurchargeAmount(String.format("%.2f",
							(Double.valueOf(dbobj.getString(FieldType.ACQUIRER_TDR_SC.toString())))));
				} else {
					transReport.setAcquirerSurchargeAmount("0.00");
				}

				if (StringUtils.isNotBlank(dbobj.getString(FieldType.PG_GST.toString()))) {
					transReport.setPgGst(
							String.format("%.2f", ((Double.valueOf(dbobj.getString(FieldType.PG_GST.toString()))))));
				} else {
					transReport.setPgGst("0.00");
				}

				if (StringUtils.isNotBlank(dbobj.getString(FieldType.ACQUIRER_GST.toString()))) {
					transReport.setAcquirerGst(String.format("%.2f",
							(Double.valueOf(dbobj.getString(FieldType.ACQUIRER_GST.toString())))));
				} else {
					transReport.setAcquirerGst("0.00");
				}
				transactionList.add(transReport);
			}
			cursor.close();
			logger.info("Inside TxnReports , searchPayment , transactionListSize = " + transactionList.size());
			return transactionList;

		} catch (Exception e) {
			logger.error("Exception in search payment for admin", e);
		}
		return transactionList;
	}

	public int grsCount(String merchant, String status, String dateFrom, String dateTo) {
		try {
			MongoDatabase dbIns = mongoInstance.getDB();
			MongoCollection<Document> coll = dbIns.getCollection(
					PropertiesManager.propertiesMap.get(prefix + Constants.GrievanceRedressalSystem.getValue()));

			BasicDBObject match = new BasicDBObject();

			if (!merchant.equalsIgnoreCase("All")) {
				match.put(FieldType.PAY_ID.getName(), merchant);
			}

			if (!status.equalsIgnoreCase("All")) {
				match.put(FieldType.STATUS.getName(), status);
			}

			if (StringUtils.isNoneBlank(dateFrom) && StringUtils.isNoneBlank(dateTo)) {
				match.put(FieldType.CREATE_DATE.getName(),
						new BasicDBObject("$gte", dateFrom + " 00:00:00").append("$lte", dateTo + " 23:59:59"));
			}

			BasicDBObject matchQ = new BasicDBObject("$match", match);
			List<BasicDBObject> pipeline = Arrays.asList(matchQ);
			logger.info("Query : " + pipeline);
			return coll.aggregate(pipeline).into(new ArrayList<>()).size();
		} catch (Exception e) {
			logger.error("Exception Occur in grsCount() ", e);
			e.printStackTrace();
			return 0;
		}

	}

	public List<GRS> grsReport(String merchant, String status, String dateFrom, String dateTo, int start, int length) {
		List<GRS>list=new ArrayList<>();
		try {
			MongoDatabase dbIns = mongoInstance.getDB();
			MongoCollection<Document> coll = dbIns.getCollection(
					PropertiesManager.propertiesMap.get(prefix + Constants.GrievanceRedressalSystem.getValue()));

			BasicDBObject match = new BasicDBObject();

			if (!merchant.equalsIgnoreCase("All")) {
				match.put(FieldType.PAY_ID.getName(), merchant);
			}

			if (!status.equalsIgnoreCase("All")) {
				match.put(FieldType.STATUS.getName(), status);
			}

			if (StringUtils.isNoneBlank(dateFrom) && StringUtils.isNoneBlank(dateTo)) {
				match.put(FieldType.CREATE_DATE.getName(),
						new BasicDBObject("$gte", dateFrom + " 00:00:00").append("$lte", dateTo + " 23:59:59"));
			}

			BasicDBObject matchQ = new BasicDBObject("$match", match);
			BasicDBObject sort = new BasicDBObject("$sort", new BasicDBObject("CREATE_DATE", -1));
			BasicDBObject skip = new BasicDBObject("$skip", start);
			BasicDBObject limit = new BasicDBObject("$limit", length);
			List<BasicDBObject> pipeline = Arrays.asList(matchQ, sort, skip, limit);
			
			logger.info("Query : " + pipeline);
			MongoCursor<Document> cursor = coll.aggregate(pipeline).iterator();
			
			GRS grs=null;
			while (cursor.hasNext()) {
				grs=new GRS();
				Document document = (Document) cursor.next();
				
				grs.setPgrefNum(document.getString("PG_REF_NUM")!=null?document.getString("PG_REF_NUM"):"");
				grs.setGrsId(document.getString("GRSID")!=null?document.getString("GRSID"):"");
				grs.setMerchantName(document.getString("MERCHANT_NAME")!=null?document.getString("MERCHANT_NAME"):"");
				grs.setGrsTittle(document.getString("TITTLE")!=null?document.getString("TITTLE"):"");
				grs.setAmount(document.getString("AMOUNT")!=null?document.getString("AMOUNT"):"");
				grs.setTotalAmount(document.getString("TOTAL_AMOUNT")!=null?document.getString("TOTAL_AMOUNT"):"");
				grs.setStatus(document.getString("STATUS")!=null?document.getString("STATUS"):"");
				grs.setOrderId(document.getString("ORDER_ID")!=null?document.getString("ORDER_ID"):"");
				grs.setCreatedDate(document.getString("CREATE_DATE")!=null?document.getString("CREATE_DATE"):"");
				grs.setCreatedBy(document.getString("CREATED_BY")!=null?document.getString("CREATED_BY"):"");
				grs.setTxnDate(document.getString("TRANSACTION_DATE")!=null?document.getString("TRANSACTION_DATE"):"");
				grs.setPaymentMethod(document.getString("PAYMENT_TYPE")!=null?document.getString("PAYMENT_TYPE"):"");
				grs.setMopType(document.getString("MOP_TYPE")!=null?document.getString("MOP_TYPE"):"");
				grs.setPayId(document.getString("PAY_ID")!=null?document.getString("PAY_ID"):"");
				grs.setCustomerName(document.getString("CUST_NAME")!=null?document.getString("CUST_NAME"):"");
				grs.setCustomerPhone(document.getString("CUST_PHONE")!=null?document.getString("CUST_PHONE"):"");
				grs.setUpdatedAt(document.getString("UPDATED_AT")!=null?document.getString("UPDATED_AT"):"");
				grs.setUpdatedBy(document.getString("UPDATED_BY")!=null?document.getString("UPDATED_BY"):"");
				
				list.add(grs);
			}
			
		} catch (Exception e) {
			logger.error("Exception Occur in grsCount() ", e);
			e.printStackTrace();
		}
		return list;
	}

	
	public GRS getGrsFromGrsID(String gsrId) {
		GRS grs=new GRS();
		try {
			MongoDatabase dbIns = mongoInstance.getDB();
			MongoCollection<Document> coll = dbIns.getCollection(
					PropertiesManager.propertiesMap.get(prefix + Constants.GrievanceRedressalSystem.getValue()));

			BasicDBObject match = new BasicDBObject();
			match.put(FieldType.GRSID.getName(), gsrId);

			BasicDBObject matchQ = new BasicDBObject("$match", match);
			
			List<BasicDBObject> pipeline = Arrays.asList(matchQ);
			
			logger.info("Query : " + pipeline);
			MongoCursor<Document> cursor = coll.aggregate(pipeline).iterator();
			
			
			if (cursor.hasNext()) {
				Document document = (Document) cursor.next();
				
				grs.setPgrefNum(document.getString("PG_REF_NUM")!=null?document.getString("PG_REF_NUM"):"");
				grs.setGrsId(document.getString("GRSID")!=null?document.getString("GRSID"):"");
				grs.setMerchantName(document.getString("MERCHANT_NAME")!=null?document.getString("MERCHANT_NAME"):"");
				grs.setGrsTittle(document.getString("TITTLE")!=null?document.getString("TITTLE"):"");
				grs.setAmount(document.getString("AMOUNT")!=null?document.getString("AMOUNT"):"");
				grs.setTotalAmount(document.getString("TOTAL_AMOUNT")!=null?document.getString("TOTAL_AMOUNT"):"");
				grs.setStatus(document.getString("STATUS")!=null?document.getString("STATUS"):"");
				grs.setOrderId(document.getString("ORDER_ID")!=null?document.getString("ORDER_ID"):"");
				grs.setCreatedDate(document.getString("CREATE_DATE")!=null?document.getString("CREATE_DATE"):"");
				grs.setCreatedBy(document.getString("CREATED_BY")!=null?document.getString("CREATED_BY"):"");
				grs.setTxnDate(document.getString("TRANSACTION_DATE")!=null?document.getString("TRANSACTION_DATE"):"");
				grs.setPaymentMethod(document.getString("PAYMENT_TYPE")!=null?document.getString("PAYMENT_TYPE"):"");
				grs.setMopType(document.getString("MOP_TYPE")!=null?document.getString("MOP_TYPE"):"");
				grs.setPayId(document.getString("PAY_ID")!=null?document.getString("PAY_ID"):"");
				grs.setCustomerName(document.getString("CUST_NAME")!=null?document.getString("CUST_NAME"):"");
				grs.setCustomerPhone(document.getString("CUST_PHONE")!=null?document.getString("CUST_PHONE"):"");
				grs.setUpdatedAt(document.getString("UPDATED_AT")!=null?document.getString("UPDATED_AT"):"");
				grs.setUpdatedBy(document.getString("UPDATED_BY")!=null?document.getString("UPDATED_BY"):"");
				grs.setGrsDesc(document.getString("GRS_DESCRIPTION")!=null?document.getString("GRS_DESCRIPTION"):"");
				
				
			}
			
		} catch (Exception e) {
			logger.error("Exception Occur in grsCount() ", e);
			e.printStackTrace();
		}
		return grs;
	}
	
	public GrsIssueHistoryDto getGrsIssueHistoryFromGrsID(String gsrId) {
		GrsIssueHistoryDto grs=new GrsIssueHistoryDto();
		try {
			MongoDatabase dbIns = mongoInstance.getDB();
			MongoCollection<Document> coll = dbIns.getCollection(
					PropertiesManager.propertiesMap.get(prefix + Constants.GrievanceRedressalSystemDescriptionHistory.getValue()));

			BasicDBObject match = new BasicDBObject();
			match.put(FieldType.GRSID.getName(), gsrId);

			BasicDBObject matchQ = new BasicDBObject("$match", match);
			
			List<BasicDBObject> pipeline = Arrays.asList(matchQ);
			
			logger.info("Query : " + pipeline);
			MongoCursor<Document> cursor = coll.aggregate(pipeline).iterator();
			
			
			while (cursor.hasNext()) {
				Document document = (Document) cursor.next();
				
				grs.setGrsId(document.getString("GRSID") != null ? document.getString("GRSID") : "N/A");
				grs.setCreatedBy(document.getString("CREATED_BY") != null ? document.getString("CREATED_BY") : "N/A");
				grs.setCreateDate(document.getString("CREATE_DATE") != null ? document.getString("CREATE_DATE") : "N/A");
				grs.setDescription(document.getString("DESCRIPTION") != null ? document.getString("DESCRIPTION") : "N/A");
				grs.setFile(document.getString("FILENAME") != null ? document.getString("FILENAME") : "N/A");
				
			}
			
		} catch (Exception e) {
			logger.error("Exception Occur in grsCount() ", e);
			e.printStackTrace();
		}
		return grs;
	}

}
