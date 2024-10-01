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
import org.apache.commons.math3.analysis.solvers.BisectionSolver;
import org.bson.BsonNull;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.hibernate.Session;
import org.hibernate.query.Query;
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
import com.pay10.commons.dao.HibernateSessionProvider;
import com.pay10.commons.dao.TransactionSearchServiceMongo;
import com.pay10.commons.mongo.MongoInstance;
import com.pay10.commons.user.CompanyProfile;
import com.pay10.commons.user.CompanyProfileDao;
import com.pay10.commons.user.Merchants;
import com.pay10.commons.user.MultCurrencyCodeDao;
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
public class TxnReports {
	final String bsesPayid = PropertiesManager.propertiesMap.get(Constants.BSESPAYID.getValue());
	private static Logger logger = LoggerFactory.getLogger(TxnReports.class.getName());
	private static final String prefix = "MONGO_DB_";
	private static Map<String, User> userMap = new HashMap<String, User>();
	private static final String companyProfileTenantId = "DEFAULT_COMPANY_TENANTID";
	@Autowired
	private UserDao userdao;
	
	@Autowired
	private MultCurrencyCodeDao currencyCodeDao;


	@Autowired
	private TransactionSearchServiceMongo transactionSearchServiceMongo;

	@Autowired
	private CompanyProfileDao companyProfileDao;

	@Autowired
	PropertiesManager propertiesManager;

	@Autowired
	private MongoInstance mongoInstance;

	public List<TransactionSearch> searchPayment(String pgRefNum, String orderId, String customerEmail,
			String merchantPayId, String paymentType, String Userstatus, String currency, String transactionType,
			String fromDate, String toDate, User user, int start, int length, String phoneNo, String mopType,
			String udf4,String channel) {

		logger.info("Inside TxnReports , searchPayment");

		boolean isParameterised = false;
		try {
			List<BasicDBObject> statusCondition = new ArrayList<BasicDBObject>();
			List<TransactionSearch> transactionList = new ArrayList<TransactionSearch>();
			List<BasicDBObject> dateCondition = new ArrayList<BasicDBObject>();
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

				dateCondition.add(new BasicDBObject(FieldType.CREATE_DATE.getName(),
						BasicDBObjectBuilder.start("$gte", new SimpleDateFormat(fromDate).toLocalizedPattern())
								.add("$lte", new SimpleDateFormat(currentDate).toLocalizedPattern()).get()));

				dateCondition.add(new BasicDBObject(FieldType.SETTLEMENT_DATE.getName(),
						BasicDBObjectBuilder.start("$gte", new SimpleDateFormat(fromDate).toLocalizedPattern())
								.add("$lte", new SimpleDateFormat(currentDate).toLocalizedPattern()).get()));

				dateQuery.put("$or", dateCondition);
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
				dateIndexConditionList.add(new BasicDBObject("SETTLEMENT_DATE_INDEX", dateIndex));
			}

			if (StringUtils.isNotBlank(PropertiesManager.propertiesMap.get(Constants.USE_DATE_INDEX.getValue()))
					&& PropertiesManager.propertiesMap.get(Constants.USE_DATE_INDEX.getValue()).equalsIgnoreCase("Y")) {
				dateIndexConditionQuery.append("$or", dateIndexConditionList);
			}

			/*
			 * if (!merchantPayId.equalsIgnoreCase("ALL")) { paramConditionLst.add(new
			 * BasicDBObject(FieldType.PAY_ID.getName(), merchantPayId));
			 * 
			 * isParameterised = true; }
			 */

			/*
			 * if (merchantPayId.equalsIgnoreCase("ALL")) {
			 * logger.info("TxnReport, MerchantId : "+merchantPayId +
			 * ", Segment : "+user.getSegment());
			 * if(!user.getSegment().equalsIgnoreCase("Default")) { List<String> payIdLst =
			 * userdao.getActiveMerchantList(user.getSegment(), user.getRole().getId()).stream().map(Merchants::getPayId).collect(Collectors.toList());
			 * logger.info("Get PayId List : "+payIdLst); if(payIdLst.size() > 0) {
			 * paramConditionLst.add(new BasicDBObject(FieldType.PAY_ID.getName(), new
			 * BasicDBObject("$in", payIdLst))); } }
			 * 
			 * } else { isParameterised = true; paramConditionLst.add(new
			 * BasicDBObject(FieldType.PAY_ID.getName(), merchantPayId)); }
			 */

			if (merchantPayId.equalsIgnoreCase("ALL")) {
				logger.info("TxnReport, MerchantId : " + merchantPayId + ", Segment : " + user.getSegment());
				if (!user.getSegment().equalsIgnoreCase("Default")) {
					List<String> payIdLst = userdao.getActiveMerchantList(user.getSegment(), user.getRole().getId()).stream().map(Merchants::getPayId).collect(Collectors.toList());
					logger.info("Get PayId List : " + payIdLst);
					if (payIdLst.size() > 0) {
						paramConditionLst
								.add(new BasicDBObject(FieldType.PAY_ID.getName(), new BasicDBObject("$in", payIdLst)));
					}
				}

			} else {
				isParameterised = true;
				paramConditionLst.add(new BasicDBObject(FieldType.PAY_ID.getName(), merchantPayId));
			}
			if (bsesPayid != null) {
				if (user.getPayId().equalsIgnoreCase(bsesPayid) && !StringUtils.isBlank(udf4)) {
					if (!udf4.equalsIgnoreCase("All")) {
						paramConditionLst.add(new BasicDBObject(FieldType.UDF4.getName(), udf4));
					}
				}
			}
			if (user.getUserType().equals(UserType.RESELLER)) {
				paramConditionLst.add(new BasicDBObject(FieldType.RESELLER_ID.getName(), user.getResellerId()));
			}

			if (!pgRefNum.isEmpty()) {
				isParameterised = true;
				paramConditionLst.add(new BasicDBObject(FieldType.PG_REF_NUM.getName(), pgRefNum));
			}
			if (!channel.equalsIgnoreCase("All")) {
				
				paramConditionLst.add(new BasicDBObject(FieldType.CHANNEL.getName(), channel));
			}
			if (!orderId.isEmpty()) {
				isParameterised = true;
				paramConditionLst.add(new BasicDBObject(FieldType.ORDER_ID.getName(), orderId));
			}
			if (!customerEmail.isEmpty()) {
				isParameterised = true;
				paramConditionLst.add(new BasicDBObject(FieldType.CUST_EMAIL.getName(), customerEmail));
			}

			if (!phoneNo.isEmpty()) {
				isParameterised = true;
				paramConditionLst.add(new BasicDBObject(FieldType.CUST_PHONE.getName(), phoneNo));
			}
			if (!mopType.equalsIgnoreCase("ALL")) {
				isParameterised = true;
				paramConditionLst.add(new BasicDBObject(FieldType.MOP_TYPE.getName(), mopType));
			}
			if (!Userstatus.equalsIgnoreCase("ALL")) {
				isParameterised = true;
				statusCondition.add(new BasicDBObject(FieldType.STATUS.getName(), Userstatus));
			} else {

			}

//			if (!StatusType.SETTLED_SETTLE.getName().equalsIgnoreCase(Userstatus)) {
//				statusCondition.add(new BasicDBObject(FieldType.STATUS.getName(), StatusType.SETTLED_SETTLE.getName()));
//			}
//
//			if (!StatusType.SETTLED_RECONCILLED.getName().equalsIgnoreCase(Userstatus)) {
//				statusCondition
//						.add(new BasicDBObject(FieldType.STATUS.getName(), StatusType.SETTLED_RECONCILLED.getName()));
//			}

			if (!statusCondition.isEmpty()) {
				BasicDBObject statusConditionQuery = new BasicDBObject("$or", statusCondition);
				paramConditionLst.add(statusConditionQuery);
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
			if (!paramConditionLst.isEmpty()) {
				allParamQuery = new BasicDBObject("$and", paramConditionLst);
			}

			List<BasicDBObject> allConditionQueryList = new ArrayList<BasicDBObject>();

			if (StringUtils.isNotBlank(pgRefNum) || StringUtils.isNotBlank(orderId)) {

			} else {
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

			BasicDBObject finalquery = new BasicDBObject("$and", fianlList);

			logger.info("Inside TxnReports , searchPayment , finalquery = " + finalquery);
			MongoDatabase dbIns = mongoInstance.getDB();
			MongoCollection<Document> coll = dbIns.getCollection(
					PropertiesManager.propertiesMap.get(prefix + Constants.TRANSACTION_STATUS_COLLECTION.getValue()));
			BasicDBObject match = new BasicDBObject("$match", finalquery);

			BasicDBObject sort = new BasicDBObject("$sort", new BasicDBObject("INSERTION_DATE", -1));
			BasicDBObject skip = new BasicDBObject("$skip", start);
			BasicDBObject limit = new BasicDBObject("$limit", length);
			Document firstGroup = new Document("_id", new Document("PG_REF_NUM", "$PG_REF_NUM")
					.append("STATUS", "$STATUS").append("REFUND_ORDER_ID", "$REFUND_ORDER_ID"));
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
				TransactionSearch transReport = new TransactionSearch();
				BigInteger txnID = new BigInteger(dbobj.getString(FieldType.TXN_ID.toString()));
				transReport.setTransactionId((txnID));
				transReport.setPgRefNum(dbobj.getString(FieldType.PG_REF_NUM.toString()));
				transReport.setPayId(dbobj.getString(FieldType.PAY_ID.toString()));

//added by deep
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
				transReport.setPgTdrSc(String.valueOf(
						(Double.valueOf(dbobj.getString(FieldType.PG_TDR_SC.toString()) != null
								? dbobj.getString(FieldType.PG_TDR_SC.toString())
								: "0")
								+ Double.valueOf(dbobj.getString(FieldType.PG_GST.toString()) != null
										? dbobj.getString(FieldType.PG_GST.toString())
										: "0"))));
				if (null != dbobj.getString(FieldType.UDF6.toString())) {
					transReport.setUdf6(dbobj.getString(FieldType.UDF6.toString()));
				} else {
					transReport.setUdf6(dbobj.getString(CrmFieldConstants.NA.getValue()));
				}

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

				if ((null != dbobj.getString(FieldType.TXNTYPE.toString())) && (dbobj
						.getString(FieldType.TXNTYPE.toString()).equalsIgnoreCase(TransactionType.REFUND.getName()))) {
					transReport.setTxnType(dbobj.getString(FieldType.TXNTYPE.toString()));
				} else if (null != dbobj.getString(FieldType.ORIG_TXNTYPE.toString())) {
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
				transReport.setPostSettledFlag(dbobj.getString(FieldType.POST_SETTLED_FLAG.getName()));
				transReport.setStatus(dbobj.getString(FieldType.STATUS.toString()));

				if (!Userstatus.equalsIgnoreCase(StatusType.SETTLED_SETTLE.getName())) {
					transReport.setDateFrom(dbobj.getString(FieldType.CREATE_DATE.getName()));
				} else {
					transReport.setDateFrom(dbobj.getString(FieldType.SETTLEMENT_DATE.getName()));
				}

				transReport.setAmount(dbobj.getString(FieldType.AMOUNT.toString()));
				transReport.setOrderId(dbobj.getString(FieldType.ORDER_ID.toString()));
				if (StringUtils.isNotBlank(dbobj.getString(FieldType.REFUND_ORDER_ID.toString()))) {
					transReport.setRefundOrderId(dbobj.getString(FieldType.REFUND_ORDER_ID.toString()));
				} else {
					transReport.setRefundOrderId(CrmFieldConstants.NA.getValue());
				}
				transReport.setoId(dbobj.getString(FieldType.OID.toString()));
				transReport.setProductDesc(dbobj.getString(FieldType.PRODUCT_DESC.toString()));
				transReport.setTransactionCaptureDate(dbobj.getString(FieldType.CREATE_DATE.toString()));
				if (transReport.getTxnType().contains(TransactionType.REFUND.getName())) {
					transReport.setTxnType(TransactionType.REFUND.getName());
				} else {
					transReport.setTxnType(TransactionType.SALE.getName());
				}

				transReport
						.setInternalCardIssusserBank(dbobj.getString(FieldType.INTERNAL_CARD_ISSUER_BANK.toString()));
				transReport.setInternalCardIssusserCountry(
						dbobj.getString(FieldType.INTERNAL_CARD_ISSUER_COUNTRY.toString()));
				transReport.setRefundableAmount(dbobj.getString(FieldType.REFUNDABLE_AMOUNT.toString()));

				transReport.setApprovedAmount(dbobj.getString(FieldType.AMOUNT.toString()));

// Refund Button logic
				String totalRefundAmount = transactionSearchServiceMongo
						.getTotlaRefundByORderId(dbobj.getString(FieldType.ORDER_ID.toString()));
				Double totalRef = Double.valueOf(totalRefundAmount);
				if (totalRef.equals(0.00)) {
					transReport.setRefundButtonName("Refund");
				} else if (totalRef < Double.valueOf(dbobj.getString(FieldType.AMOUNT.toString()))) {
					transReport.setRefundButtonName("Partial Refund");
				} else if ((totalRef.equals(Double.valueOf(dbobj.getString(FieldType.AMOUNT.toString()))))
						|| (totalRef > Double.valueOf(dbobj.getString(FieldType.AMOUNT.toString())))) {
					transReport.setRefundButtonName("Refunded");
				} else {
					transReport.setRefundButtonName("Refund");
				}

				if (dbobj.getString(FieldType.TOTAL_AMOUNT.toString()) != null) {
					transReport.setTotalAmount(dbobj.getString(FieldType.TOTAL_AMOUNT.toString()));

					Double amtPayableToMerch = 0.00;
					Double pgCommision = 0.00;
					Double acquirerCommision = 0.00;
					amtPayableToMerch = Double.valueOf(dbobj.getString(FieldType.TOTAL_AMOUNT.toString()));
					if (dbobj.getString(FieldType.PG_TDR_SC.toString()) != null
							&& dbobj.getString(FieldType.PG_GST.toString()) != null
							&& dbobj.getString(FieldType.ACQUIRER_TDR_SC.toString()) != null
							&& dbobj.getString(FieldType.ACQUIRER_GST.toString()) != null) {
						pgCommision = Double.valueOf(dbobj.getString(FieldType.PG_TDR_SC.toString()))
								+ Double.valueOf(dbobj.getString(FieldType.PG_GST.toString()));
						acquirerCommision = Double.valueOf(dbobj.getString(FieldType.ACQUIRER_TDR_SC.toString()))
								+ Double.valueOf(dbobj.getString(FieldType.ACQUIRER_GST.toString()));
						amtPayableToMerch -= (pgCommision + acquirerCommision);
						transReport.setSettledAmount(String.format("%.2f",amtPayableToMerch));
					} else {
						transReport.setSettledAmount(String.format("%.2f",amtPayableToMerch));
					}

				} else {
					transReport.setTotalAmount("");
				}

				if (null != dbobj.getString(FieldType.CURRENCY_CODE.toString())) {
					transReport.setCurrency(currencyCodeDao.getCurrencyNamebyCode(dbobj.getString(FieldType.CURRENCY_CODE.toString())));
//					Currency.getAlphabaticCode(dbobj.getString(FieldType.CURRENCY_CODE.toString())));

				} else {
					transReport.setCurrency(CrmFieldConstants.NA.getValue());
				}

				Comparator<TransactionSearch> comp = (TransactionSearch a, TransactionSearch b) -> {
					if(StringUtils.isEmpty(a.getDateFrom()) || StringUtils.isEmpty(b.getDateFrom())){
						return 0;
					}
					if (a.getDateFrom().compareTo(b.getDateFrom()) > 0) {
						return -1;
					} else if (a.getDateFrom().compareTo(b.getDateFrom()) < 0) {
						return 1;
					} else {
						return 0;
					}
				};

				transactionList.add(transReport);
				Collections.sort(transactionList, comp);
			}
			cursor.close();
			logger.info("Inside TxnReports , searchPayment , transactionListSize = " + transactionList.size());
			return transactionList;
		} catch (Exception e) {
			logger.error("Exception occured in TxnReports , searchPayment , Exception = ", e);
			return null;
		}
	}

	public List<TransactionSearch> searchPayment(String pgRefNum, String orderId, String customerEmail,
			String customerPhone, String merchantPayId, String paymentType, String Userstatus, String currency,
			String transactionType, String mopType, String fromDate, String toDate, User user, int start, int length) {

		logger.info("Inside TxnReports , searchPayment");

		boolean isParameterised = false;
		try {
			List<TransactionSearch> transactionList = new ArrayList<TransactionSearch>();

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

			/*
			 * if (!merchantPayId.equalsIgnoreCase("ALL")) { paramConditionLst.add(new
			 * BasicDBObject(FieldType.PAY_ID.getName(), merchantPayId));
			 * 
			 * isParameterised = true; }
			 */

			if (merchantPayId.equalsIgnoreCase("ALL")) {
				logger.info("TxnReport, MerchantId : " + merchantPayId + ", Segment : " + user.getSegment());
				if (!user.getSegment().equalsIgnoreCase("Default")) {
					List<String> payIdLst = userdao.getActiveMerchantList(user.getSegment(), user.getRole().getId()).stream().map(Merchants::getPayId).collect(Collectors.toList());
					logger.info("Get PayId List : " + payIdLst);
					if (payIdLst.size() > 0) {
						paramConditionLst
								.add(new BasicDBObject(FieldType.PAY_ID.getName(), new BasicDBObject("$in", payIdLst)));
					}
				}

			} else {
				isParameterised = true;
				paramConditionLst.add(new BasicDBObject(FieldType.PAY_ID.getName(), merchantPayId));
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
				paramConditionLst.add(new BasicDBObject(FieldType.CUST_EMAIL.getName(), customerEmail));
			}

			if (!customerPhone.isEmpty()) {
				isParameterised = true;
				paramConditionLst.add(new BasicDBObject(FieldType.CUST_PHONE.getName(), customerPhone));
			}
			if (!Userstatus.equalsIgnoreCase("ALL")) {
				isParameterised = true;
				paramConditionLst.add(new BasicDBObject(FieldType.STATUS.getName(), Userstatus));
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
			if (!mopType.equalsIgnoreCase("ALL")) {
				isParameterised = true;
				paramConditionLst.add(new BasicDBObject(FieldType.MOP_TYPE.getName(), mopType));
			} else {

			}
			if (!paymentType.equalsIgnoreCase("ALL")) {
				isParameterised = true;
				paramConditionLst.add(new BasicDBObject(FieldType.PAYMENT_TYPE.getName(), paymentType));
			} else {

			}
			if (!paramConditionLst.isEmpty()) {
				allParamQuery = new BasicDBObject("$and", paramConditionLst);
			}

			List<BasicDBObject> allConditionQueryList = new ArrayList<BasicDBObject>();

			if (StringUtils.isNotBlank(pgRefNum) || StringUtils.isNotBlank(orderId)) {

			} else {
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

			BasicDBObject finalquery = new BasicDBObject("$and", fianlList);

			logger.info("Inside TxnReports , searchPayment , finalquery = " + finalquery);
			MongoDatabase dbIns = mongoInstance.getDB();
			MongoCollection<Document> coll = dbIns
					.getCollection(PropertiesManager.propertiesMap.get(prefix + Constants.COLLECTION_NAME.getValue()));
			BasicDBObject match = new BasicDBObject("$match", finalquery);

			BasicDBObject sort = new BasicDBObject("$sort", new BasicDBObject("INSERTION_DATE", -1));
			BasicDBObject skip = new BasicDBObject("$skip", start);
			BasicDBObject limit = new BasicDBObject("$limit", length);

			List<BasicDBObject> pipeline = Arrays.asList(match, sort, skip, limit);

			AggregateIterable<Document> output = coll.aggregate(pipeline);
			output.allowDiskUse(true);
			MongoCursor<Document> cursor = output.iterator();

			while (cursor.hasNext()) {
				Document dbobj = cursor.next();

				TransactionSearch transReport = new TransactionSearch();
				BigInteger txnID = new BigInteger(dbobj.getString(FieldType.TXN_ID.toString()));
				transReport.setTransactionId((txnID));
				transReport.setPgRefNum(dbobj.getString(FieldType.PG_REF_NUM.toString()));
				transReport.setPayId(dbobj.getString(FieldType.PAY_ID.toString()));

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

				if ((null != dbobj.getString(FieldType.TXNTYPE.toString())) && (dbobj
						.getString(FieldType.TXNTYPE.toString()).equalsIgnoreCase(TransactionType.REFUND.getName()))) {
					transReport.setTxnType(dbobj.getString(FieldType.TXNTYPE.toString()));
				} else if (null != dbobj.getString(FieldType.ORIG_TXNTYPE.toString())) {
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
				transReport.setPostSettledFlag(dbobj.getString(FieldType.POST_SETTLED_FLAG.getName()));
				transReport.setStatus(dbobj.getString(FieldType.STATUS.toString()));
				transReport.setDateFrom(dbobj.getString(FieldType.CREATE_DATE.getName()));
				transReport.setAmount(dbobj.getString(FieldType.AMOUNT.toString()));
				transReport.setOrderId(dbobj.getString(FieldType.ORDER_ID.toString()));
				if (StringUtils.isNotBlank(dbobj.getString(FieldType.REFUND_ORDER_ID.toString()))) {
					transReport.setRefundOrderId(dbobj.getString(FieldType.REFUND_ORDER_ID.toString()));
				} else {
					transReport.setRefundOrderId(CrmFieldConstants.NA.getValue());
				}
				transReport.setoId(dbobj.getString(FieldType.OID.toString()));
				transReport.setProductDesc(dbobj.getString(FieldType.PRODUCT_DESC.toString()));
				transReport.setTransactionCaptureDate(dbobj.getString(FieldType.PG_DATE_TIME.toString()));
				if (transReport.getTxnType().contains(TransactionType.REFUND.getName())) {
					transReport.setTxnType(TransactionType.REFUND.getName());
				} else {
					transReport.setTxnType(TransactionType.SALE.getName());
				}

				transReport
						.setInternalCardIssusserBank(dbobj.getString(FieldType.INTERNAL_CARD_ISSUER_BANK.toString()));
				transReport.setInternalCardIssusserCountry(
						dbobj.getString(FieldType.INTERNAL_CARD_ISSUER_COUNTRY.toString()));
				transReport.setRefundableAmount(dbobj.getString(FieldType.REFUNDABLE_AMOUNT.toString()));

				transReport.setApprovedAmount(dbobj.getString(FieldType.AMOUNT.toString()));

				if (dbobj.getString(FieldType.TOTAL_AMOUNT.toString()) != null) {
					transReport.setTotalAmount(dbobj.getString(FieldType.TOTAL_AMOUNT.toString()));
				} else {
					transReport.setTotalAmount("");
				}

				if (null != dbobj.getString(FieldType.CURRENCY_CODE.toString())) {
					transReport.setCurrency(currencyCodeDao.getCurrencyNamebyCode(dbobj.getString(FieldType.CURRENCY_CODE.toString())));
				} else {
					transReport.setCurrency(CrmFieldConstants.NA.getValue());
				}

				Comparator<TransactionSearch> comp = (TransactionSearch a, TransactionSearch b) -> {

					if (a.getDateFrom().compareTo(b.getDateFrom()) > 0) {
						return -1;
					} else if (a.getDateFrom().compareTo(b.getDateFrom()) < 0) {
						return 1;
					} else {
						return 0;
					}
				};

				transactionList.add(transReport);
				Collections.sort(transactionList, comp);
			}
			cursor.close();
			logger.info("Inside TxnReports , searchPayment , transactionListSize = " + transactionList.size());
			return transactionList;
		} catch (Exception e) {
			logger.error("Exception occured in TxnReports , searchPayment , Exception = ", e);
			return null;
		}
	}

	public List<TransactionSearch> searchPayment(String pgRefNum, String orderId, String customerEmail,
			String customerPhone, String paymentType, String Userstatus, String currency, String transactionType,
			String mopType, String acquirer, String merchantPayId, String fromDate, String toDate, User user, int start,
			int length) {

		logger.info("Inside TxnReports , searchPayment");

		try {

			List<TransactionSearch> transactionList = new ArrayList<TransactionSearch>();
			List<BasicDBObject> paramConditionLst = new ArrayList<BasicDBObject>();
			BasicDBObject acquirerQuery = new BasicDBObject();
			List<BasicDBObject> acquirerConditionLst = new ArrayList<BasicDBObject>();
			BasicDBObject allParamQuery = new BasicDBObject();
			BasicDBObject dateQuery = new BasicDBObject();
			List<BasicDBObject> mopTypeConditionLst = new ArrayList<BasicDBObject>();
			BasicDBObject mopTypeQuery = new BasicDBObject();
			List<BasicDBObject> UserstatusConditionLst = new ArrayList<BasicDBObject>();
			BasicDBObject UserstatusQuery = new BasicDBObject();
			List<BasicDBObject> paymentTypeConditionLst = new ArrayList<BasicDBObject>();
			BasicDBObject paymentTypeQuery = new BasicDBObject();

			List<BasicDBObject> transactionTypeConditionLst = new ArrayList<BasicDBObject>();
			BasicDBObject transactionTypeQuery = new BasicDBObject();

			String currentDate = null;
			boolean isParameterised = false;
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

			/*
			 * if (!merchantPayId.equalsIgnoreCase("ALL")) { paramConditionLst.add(new
			 * BasicDBObject(FieldType.PAY_ID.getName(), merchantPayId));
			 * 
			 * }
			 */

			if (merchantPayId.equalsIgnoreCase("ALL")) {
				logger.info("TxnReport, MerchantId : " + merchantPayId + ", Segment : " + user.getSegment());
				if (!user.getSegment().equalsIgnoreCase("Default")) {
					List<String> payIdLst = userdao.getActiveMerchantList(user.getSegment(), user.getRole().getId()).stream().map(Merchants::getPayId).collect(Collectors.toList());
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

			if (!pgRefNum.isEmpty()) {
				isParameterised = true;
				paramConditionLst.add(new BasicDBObject(FieldType.PG_REF_NUM.getName(), pgRefNum));
			}
			if (!orderId.isEmpty()) {
				isParameterised = true;
				paramConditionLst.add(new BasicDBObject(FieldType.ORDER_ID.getName(), orderId));
			}
			if (!customerEmail.isEmpty()) {
				paramConditionLst.add(new BasicDBObject(FieldType.CUST_EMAIL.getName(), customerEmail));
			}

			if (!customerPhone.isEmpty()) {
				paramConditionLst.add(new BasicDBObject(FieldType.CUST_PHONE.getName(), customerPhone));
			}

			if (!currency.equalsIgnoreCase("ALL")) {
				paramConditionLst.add(new BasicDBObject(FieldType.CURRENCY_CODE.getName(), currency));
			} else {

			}

			if (!transactionType.equalsIgnoreCase("ALL")) {
				paramConditionLst.add(new BasicDBObject(FieldType.ORIG_TXNTYPE.getName(), transactionType));
			} else {
				// below logic is for both Sale or Refund transaction fetching
				List<TxnType> transactionTypeList = TxnType.gettxnType();
				for (TxnType txn : transactionTypeList) {
					transactionTypeConditionLst.add(new BasicDBObject(FieldType.ORIG_TXNTYPE.getName(), txn.getName()));
				}
				transactionTypeQuery.append("$or", transactionTypeConditionLst);
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
			if (!Userstatus.equalsIgnoreCase("ALL")) {
				List<String> UserstatusList = Arrays.asList(Userstatus.split(","));
				for (String userStatus : UserstatusList) {
					UserstatusConditionLst.add(new BasicDBObject(FieldType.STATUS.getName(), userStatus.trim()));
				}
				UserstatusQuery.append("$or", UserstatusConditionLst);
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
			if (!UserstatusQuery.isEmpty()) {
				fianlList.add(UserstatusQuery);
			}

			if (!transactionTypeQuery.isEmpty()) {
				fianlList.add(transactionTypeQuery);
			}

			BasicDBObject finalquery = new BasicDBObject("$and", fianlList);

			logger.info("Inside TxnReports , searchPayment , finalquery = " + finalquery);
			MongoDatabase dbIns = mongoInstance.getDB();
			MongoCollection<Document> coll = dbIns
					.getCollection(PropertiesManager.propertiesMap.get(prefix + Constants.COLLECTION_NAME.getValue()));
			BasicDBObject match = new BasicDBObject("$match", finalquery);

			BasicDBObject projectElement = new BasicDBObject();
			projectElement.put(FieldType.PG_REF_NUM.toString(), 1);
			BasicDBObject project = new BasicDBObject("$project", projectElement);
			List<BasicDBObject> pgRefPipeline = Arrays.asList(match, project);
			AggregateIterable<Document> pgRefTotal = coll.aggregate(pgRefPipeline);
			pgRefTotal.allowDiskUse(true);

			// Removing the Duplicates
			HashSet pgRefDistinctset = new HashSet();
			MongoCursor<Document> pgRefTotalCursor = (MongoCursor<Document>) pgRefTotal.iterator();

			while (pgRefTotalCursor.hasNext()) {
				pgRefDistinctset.add(pgRefTotalCursor.next().get("PG_REF_NUM"));
			}
			pgRefTotalCursor.close();
			pgRefDistinctset.remove("0");
			ArrayList<String> pgRefAr = new ArrayList<String>(pgRefDistinctset);
			// End logic

			LocalDate StartDate = dateStart.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
			String startDateIndex = StartDate.toString().replaceAll("-", "");
			String endDateIndex = endDate.toString().replaceAll("-", "");

			List<String> txnIdAr = searchPaymentTxnIdsList(Userstatus, fianlList, startDateIndex, endDateIndex,
					pgRefAr);

			Iterator txnIdCursor = txnIdAr.iterator();
			int txnLengthCount = 0;
			while (txnIdCursor.hasNext()) {

				if (txnLengthCount == (length + start)) {
					break;
				}

				if ((txnLengthCount >= start) && (txnLengthCount < (length + start))) {

					BasicDBObject txnFinalquery = new BasicDBObject("TXN_ID", txnIdCursor.next().toString());
					BasicDBObject txnMatch = new BasicDBObject("$match", txnFinalquery);
					BasicDBObject txnSort = new BasicDBObject("$sort", new BasicDBObject("CREATE_DATE", -1));
					BasicDBObject txnLimit = new BasicDBObject("$limit", 1);
					List<BasicDBObject> pipeline = Arrays.asList(txnMatch, txnSort, txnLimit);
					AggregateIterable<Document> output = coll.aggregate(pipeline);

					output.allowDiskUse(true);
					MongoCursor<Document> cursor = output.iterator();

					Document dbobj = cursor.next();

					TransactionSearch transReport = new TransactionSearch();
					BigInteger txnID = new BigInteger(dbobj.getString(FieldType.TXN_ID.toString()));
					transReport.setTransactionId((txnID));
					transReport.setPgRefNum(dbobj.getString(FieldType.PG_REF_NUM.toString()));
					transReport.setPayId(dbobj.getString(FieldType.PAY_ID.toString()));

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

					if ((null != dbobj.getString(FieldType.TXNTYPE.toString()))
							&& (dbobj.getString(FieldType.TXNTYPE.toString())
									.equalsIgnoreCase(TransactionType.REFUND.getName()))) {
						transReport.setTxnType(dbobj.getString(FieldType.TXNTYPE.toString()));
					} else if (null != dbobj.getString(FieldType.ORIG_TXNTYPE.toString())) {
						transReport.setTxnType(dbobj.getString(FieldType.ORIG_TXNTYPE.toString()));
					} else {

						// If ORIG_TXN_TYPE is not available incase of a timeout , set TXNTYPE instead
						// of ORIG_TXN_TYPE

						if (dbobj.getString(FieldType.STATUS.toString())
								.equalsIgnoreCase(StatusType.TIMEOUT.getName())) {
							transReport.setTxnType(dbobj.getString(FieldType.TXNTYPE.toString()));
						} else {
							transReport.setTxnType(CrmFieldConstants.NA.getValue());
						}

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
					transReport.setPostSettledFlag(dbobj.getString(FieldType.POST_SETTLED_FLAG.getName()));
					transReport.setStatus(dbobj.getString(FieldType.STATUS.toString()));
					transReport.setDateFrom(dbobj.getString(FieldType.CREATE_DATE.getName()));
					transReport.setAmount(dbobj.getString(FieldType.AMOUNT.toString()));
					transReport.setOrderId(dbobj.getString(FieldType.ORDER_ID.toString()));
					if (StringUtils.isNotBlank(dbobj.getString(FieldType.REFUND_ORDER_ID.toString()))) {
						transReport.setRefundOrderId(dbobj.getString(FieldType.REFUND_ORDER_ID.toString()));
					} else {
						transReport.setRefundOrderId(CrmFieldConstants.NA.getValue());
					}
					transReport.setoId(dbobj.getString(FieldType.OID.toString()));
					transReport.setProductDesc(dbobj.getString(FieldType.PRODUCT_DESC.toString()));
					transReport.setTransactionCaptureDate(dbobj.getString(FieldType.PG_DATE_TIME.toString()));
					if (transReport.getTxnType().contains(TransactionType.REFUND.getName())) {
						transReport.setTxnType(TransactionType.REFUND.getName());
					} else {
						transReport.setTxnType(TransactionType.SALE.getName());
					}

					transReport.setInternalCardIssusserBank(
							dbobj.getString(FieldType.INTERNAL_CARD_ISSUER_BANK.toString()));
					transReport.setInternalCardIssusserCountry(
							dbobj.getString(FieldType.INTERNAL_CARD_ISSUER_COUNTRY.toString()));
					transReport.setRefundableAmount(dbobj.getString(FieldType.REFUNDABLE_AMOUNT.toString()));

					transReport.setApprovedAmount(dbobj.getString(FieldType.AMOUNT.toString()));

					if (dbobj.getString(FieldType.TOTAL_AMOUNT.toString()) != null) {
						transReport.setTotalAmount(dbobj.getString(FieldType.TOTAL_AMOUNT.toString()));
					} else {
						transReport.setTotalAmount("");
					}

					if ((user.getUserType().equals(UserType.ADMIN) || user.getUserType().equals(UserType.SUBADMIN))
							&& null != dbobj.getString(FieldType.ACQUIRER_TYPE.toString())) {
						transReport.setAcquirerType(dbobj.getString(FieldType.ACQUIRER_TYPE.toString()));
					} else {
						transReport.setAcquirerType(CrmFieldConstants.NA.getValue());
					}

					if (null != dbobj.getString(FieldType.CURRENCY_CODE.toString())) {
						transReport.setCurrency(currencyCodeDao.getCurrencyNamebyCode(dbobj.getString(FieldType.CURRENCY_CODE.toString())));
					} else {
						transReport.setCurrency(CrmFieldConstants.NA.getValue());
					}

					Comparator<TransactionSearch> comp = (TransactionSearch a, TransactionSearch b) -> {

						if (a.getDateFrom().compareTo(b.getDateFrom()) > 0) {
							return -1;
						} else if (a.getDateFrom().compareTo(b.getDateFrom()) < 0) {
							return 1;
						} else {
							return 0;
						}
					};

					transactionList.add(transReport);
					Collections.sort(transactionList, comp);
					cursor.close();
				} else {
					txnIdCursor.next().toString();
				}
				txnLengthCount++;
			}

			logger.info("Inside TxnReports , searchPayment , transactionListSize = " + transactionList.size());
			return transactionList;
		} catch (Exception e) {
			logger.error("Exception occured in TxnReports , searchPayment , Exception = ", e);
			return null;
		}

	}

	public List<TransactionSearch> searchPayment(User user, Set<String> orderIdSet, int start, int length,
			String internalStatus) {
		logger.info("Inside TxnReports , searchPayment");

		List<TransactionSearch> transactionList = new ArrayList<TransactionSearch>();

		try {

			// First get list of transactions for which we want data
			List<String> txnList = new ArrayList<String>();
			Object[] orderIdTxnTypeArr = orderIdSet.toArray();
			for (int i = 0; i < length; i++) {

				if (orderIdTxnTypeArr.length <= start + i) {
					break;
				}
				txnList.add(String.valueOf(orderIdTxnTypeArr[start + i]));
			}

			if (!txnList.isEmpty()) {

				for (int i = 0; i < txnList.size(); i++) {

					MongoDatabase dbIns = mongoInstance.getDB();
					MongoCollection<Document> coll = dbIns.getCollection(
							PropertiesManager.propertiesMap.get(prefix + Constants.COLLECTION_NAME.getValue()));

					String txnUnit = txnList.get(i);
					String txnUnitArr[] = txnUnit.split(":");

					String orderId = txnUnitArr[0];
					String txnType = txnUnitArr[1];

					List<BasicDBObject> txnTypList = new ArrayList<BasicDBObject>();
					BasicDBObject ordquery = new BasicDBObject("ORDER_ID", orderId);

					if (txnType.equalsIgnoreCase(TransactionType.SALE.getName())) {

						txnTypList.add(new BasicDBObject("TXNTYPE", TransactionType.SALE.getName()));
						txnTypList.add(new BasicDBObject("TXNTYPE", TransactionType.ENROLL.getName()));
						txnTypList.add(new BasicDBObject("TXNTYPE", TransactionType.RECO.getName()));
						txnTypList.add(new BasicDBObject("TXNTYPE", TransactionType.NEWORDER.getName()));
					} else {
						txnTypList.add(new BasicDBObject("TXNTYPE", TransactionType.REFUND.getName()));
						txnTypList.add(new BasicDBObject("TXNTYPE", TransactionType.REFUNDRECO.getName()));
					}
					BasicDBObject txnTypQuery = new BasicDBObject("$or", txnTypList);

					List<BasicDBObject> queryList = new ArrayList<BasicDBObject>();

					queryList.add(txnTypQuery);
					queryList.add(ordquery);

					// For Cancelled , Failed and Timeout status , include status also in the query
					if (internalStatus.contains("Cancelled") || internalStatus.contains("Failed")
							|| internalStatus.contains("Timeout") || internalStatus.contains("Invalid")
							|| internalStatus.contains("Approved") || internalStatus.contains("Captured")) {

						List<BasicDBObject> transactionStatusConditionLst = new ArrayList<BasicDBObject>();
						BasicDBObject transactionStatusQuery = new BasicDBObject();

						List<String> txnStatusList = Arrays.asList(internalStatus.split(","));
						for (String txnStatus : txnStatusList) {
							transactionStatusConditionLst
									.add(new BasicDBObject(FieldType.STATUS.getName(), txnStatus.trim()));
						}
						transactionStatusQuery.append("$or", transactionStatusConditionLst);
						queryList.add(transactionStatusQuery);
					}

					BasicDBObject txnFinalquery = new BasicDBObject("$and", queryList);
					BasicDBObject txnMatch = new BasicDBObject("$match", txnFinalquery);
					BasicDBObject txnSort = new BasicDBObject("$sort", new BasicDBObject("CREATE_DATE", -1));
					BasicDBObject txnLimit = new BasicDBObject("$limit", 1);
					List<BasicDBObject> pipeline = Arrays.asList(txnMatch, txnSort, txnLimit);
					AggregateIterable<Document> output = coll.aggregate(pipeline);

					output.allowDiskUse(true);
					MongoCursor<Document> cursor = output.iterator();

					if (!cursor.hasNext()) {
						continue;
					}
					Document dbobj = cursor.next();

					TransactionSearch transReport = new TransactionSearch();
					BigInteger txnID = new BigInteger(dbobj.getString(FieldType.TXN_ID.toString()));
					transReport.setTransactionId((txnID));
					transReport.setTransactionIdString((txnID).toString());
					transReport.setPgRefNum(dbobj.getString(FieldType.PG_REF_NUM.toString()));
					transReport.setPayId(dbobj.getString(FieldType.PAY_ID.toString()));

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

					if ((null != dbobj.getString(FieldType.TXNTYPE.toString()))
							&& (dbobj.getString(FieldType.TXNTYPE.toString())
									.equalsIgnoreCase(TransactionType.REFUND.getName()))) {
						transReport.setTxnType(dbobj.getString(FieldType.TXNTYPE.toString()));
					} else if (null != dbobj.getString(FieldType.ORIG_TXNTYPE.toString())) {
						transReport.setTxnType(dbobj.getString(FieldType.ORIG_TXNTYPE.toString()));
					} else {

						// If ORIG_TXN_TYPE is not available incase of a timeout , set TXNTYPE instead
						// of ORIG_TXN_TYPE

						if (dbobj.getString(FieldType.STATUS.toString())
								.equalsIgnoreCase(StatusType.TIMEOUT.getName())) {
							transReport.setTxnType(dbobj.getString(FieldType.TXNTYPE.toString()));
						} else {
							transReport.setTxnType(CrmFieldConstants.NA.getValue());
						}

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
					transReport.setPostSettledFlag(dbobj.getString(FieldType.POST_SETTLED_FLAG.getName()));
					transReport.setStatus(dbobj.getString(FieldType.STATUS.toString()));
					if (dbobj.getString(FieldType.STATUS.toString())
							.equalsIgnoreCase(StatusType.SETTLED_SETTLE.getName())) {
						String pgRefnum = dbobj.getString(FieldType.PG_REF_NUM.toString());

						List<BasicDBObject> settledConditionLst = new ArrayList<BasicDBObject>();
						settledConditionLst.add(new BasicDBObject(FieldType.PG_REF_NUM.getName(), pgRefnum));
						settledConditionLst.add(new BasicDBObject(FieldType.STATUS.getName(),
								StatusType.CAPTURED.getName().toString()));
						BasicDBObject finalQuery = new BasicDBObject("$and", settledConditionLst);

						MongoCollection<Document> coll2 = dbIns.getCollection(
								PropertiesManager.propertiesMap.get(prefix + Constants.COLLECTION_NAME.getValue()));
						BasicDBObject match2 = new BasicDBObject("$match", finalQuery);
						BasicDBObject projectElement = new BasicDBObject();
						projectElement.put(FieldType.CREATE_DATE.toString(), 1);
						BasicDBObject project2 = new BasicDBObject("$project", projectElement);
						List<BasicDBObject> pgRefPipeline = Arrays.asList(match2, project2);
						AggregateIterable<Document> pgRefTotal1 = coll.aggregate(pgRefPipeline);
						pgRefTotal1.allowDiskUse(true);
						MongoCursor<Document> pgRefTotalCursor = (MongoCursor<Document>) pgRefTotal1.iterator();

						while (pgRefTotalCursor.hasNext()) {
							transReport.setDateFrom(pgRefTotalCursor.next().get("CREATE_DATE").toString());
							break;
						}
						pgRefTotalCursor.close();

					} else {
						transReport.setDateFrom(dbobj.getString(FieldType.CREATE_DATE.getName()));
					}
					transReport.setAmount(dbobj.getString(FieldType.AMOUNT.toString()));
					transReport.setOrderId(dbobj.getString(FieldType.ORDER_ID.toString()));
					if (StringUtils.isNotBlank(dbobj.getString(FieldType.REFUND_ORDER_ID.toString()))) {
						transReport.setRefundOrderId(dbobj.getString(FieldType.REFUND_ORDER_ID.toString()));
					} else {
						transReport.setRefundOrderId(CrmFieldConstants.NA.getValue());
					}
					transReport.setoId(dbobj.getString(FieldType.OID.toString()));
					transReport.setProductDesc(dbobj.getString(FieldType.PRODUCT_DESC.toString()));
					transReport.setTransactionCaptureDate(dbobj.getString(FieldType.PG_DATE_TIME.toString()));
					if (transReport.getTxnType().contains(TransactionType.REFUND.getName())) {
						transReport.setTxnType(TransactionType.REFUND.getName());
					} else {
						transReport.setTxnType(TransactionType.SALE.getName());
					}

					transReport.setInternalCardIssusserBank(
							dbobj.getString(FieldType.INTERNAL_CARD_ISSUER_BANK.toString()));
					transReport.setInternalCardIssusserCountry(
							dbobj.getString(FieldType.INTERNAL_CARD_ISSUER_COUNTRY.toString()));
					transReport.setRefundableAmount(dbobj.getString(FieldType.REFUNDABLE_AMOUNT.toString()));

					transReport.setApprovedAmount(dbobj.getString(FieldType.AMOUNT.toString()));

					if (dbobj.getString(FieldType.TOTAL_AMOUNT.toString()) != null) {
						transReport.setTotalAmount(dbobj.getString(FieldType.TOTAL_AMOUNT.toString()));
					} else {
						transReport.setTotalAmount("");
					}

					if ((user.getUserType().equals(UserType.ADMIN) || user.getUserType().equals(UserType.SUBADMIN))
							&& null != dbobj.getString(FieldType.ACQUIRER_TYPE.toString())) {
						transReport.setAcquirerType(dbobj.getString(FieldType.ACQUIRER_TYPE.toString()));
					} else {
						transReport.setAcquirerType(CrmFieldConstants.NA.getValue());
					}

					if (null != dbobj.getString(FieldType.CURRENCY_CODE.toString())) {
						transReport.setCurrency(currencyCodeDao.getCurrencyNamebyCode(dbobj.getString(FieldType.CURRENCY_CODE.toString())));
					} else {
						transReport.setCurrency(CrmFieldConstants.NA.getValue());
					}

					cursor.close();
					transactionList.add(transReport);
				}

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
			} else {
				return transactionList;
			}

		} catch (Exception e) {
			logger.error("Exception in search payment for admin", e);
		}
		return transactionList;
	}

	public List<TransactionSearch> searchPayment(String transactionID, String oId, User user, String orderId,
			String txnType, String pgRefNum) {

		logger.info("Inside TxnReports , searchPayment popup");

		try {

			MongoDatabase dbIns = mongoInstance.getDB();
			// Get current status of transaction Start //
			String currStatus = "Pending";
			String currAcq = "NA";
			String currArn = "NA";
			String currRrn = "NA";

			MongoCollection<Document> txnCollection = dbIns.getCollection(
					PropertiesManager.propertiesMap.get(prefix + Constants.TRANSACTION_STATUS_COLLECTION.getValue()));

			List<BasicDBObject> paramConditionLst = new ArrayList<BasicDBObject>();

			BasicDBObject oidQuery = new BasicDBObject(FieldType.OID.getName(), oId);
			BasicDBObject orderIdQuery = new BasicDBObject(FieldType.ORDER_ID.getName(), orderId);
			BasicDBObject txnTypeQuery = new BasicDBObject(FieldType.ORIG_TXNTYPE.getName(), txnType);

			paramConditionLst.add(oidQuery);
			paramConditionLst.add(orderIdQuery);
			paramConditionLst.add(txnTypeQuery);

			if (txnType.equalsIgnoreCase(TransactionType.REFUND.getName())) {
				BasicDBObject pgRefQuery = new BasicDBObject(FieldType.PG_REF_NUM.getName(), pgRefNum);
				paramConditionLst.add(pgRefQuery);
			}

			BasicDBObject finalquery = new BasicDBObject("$and", paramConditionLst);

			MongoCursor<Document> txnCursor = txnCollection.find(finalquery).iterator();

			while (txnCursor.hasNext()) {
				Document dbobj = txnCursor.next();
				currStatus = dbobj.getString(FieldType.STATUS.getName());

				if (StringUtils.isNotBlank(dbobj.getString(FieldType.ACQ_ID.getName()))) {
					currAcq = dbobj.getString(FieldType.ACQ_ID.getName());
				}

				if (StringUtils.isNotBlank(dbobj.getString(FieldType.ARN.getName()))) {
					currArn = dbobj.getString(FieldType.ARN.getName());
				}

				if (StringUtils.isNotBlank(dbobj.getString(FieldType.RRN.getName()))) {
					currRrn = dbobj.getString(FieldType.RRN.getName());
				}

				break;
			}
			// Get current status of transaction End //

			List<TransactionSearch> transactionList = new ArrayList<TransactionSearch>();
			BasicDBObject allParamQuery = new BasicDBObject();
			BasicDBObject oidFinalquery = new BasicDBObject();
			BasicDBObject txnFinalquery = new BasicDBObject();

			MongoCollection<Document> coll = dbIns
					.getCollection(PropertiesManager.propertiesMap.get(prefix + Constants.COLLECTION_NAME.getValue()));

			if (oId.equalsIgnoreCase("0")) {
				txnFinalquery.append("TXN_ID", transactionID);
				BasicDBObject match = new BasicDBObject("$match", txnFinalquery);
				BasicDBObject oidSort = new BasicDBObject("$sort", new BasicDBObject("CREATE_DATE", -1));
				BasicDBObject projectElement = new BasicDBObject();
				projectElement.put(FieldType.ORDER_ID.toString(), 1);
				BasicDBObject project = new BasicDBObject("$project", projectElement);
				List<BasicDBObject> oidPipeline = Arrays.asList(match, project, oidSort);
				AggregateIterable<Document> oidTotal = coll.aggregate(oidPipeline);
				oidTotal.allowDiskUse(true);
				MongoCursor<Document> oidTotalCursor = (MongoCursor<Document>) oidTotal.iterator();
				String ordId = null;
				while (oidTotalCursor.hasNext()) {
					Document dbobj = oidTotalCursor.next();
					ordId = dbobj.getString(FieldType.ORDER_ID.toString());
				}
				oidFinalquery.clear();
				// Here we are passing the Order Id to oidFinal query
				oidFinalquery.append("ORDER_ID", ordId);
			} else {
				oidFinalquery.append("OID", oId);
			}

			logger.info("Inside TxnReports , searchPayment , finalquery = " + oidFinalquery);

			BasicDBObject match = new BasicDBObject("$match", oidFinalquery);
			BasicDBObject oidSort = new BasicDBObject("$sort", new BasicDBObject("CREATE_DATE", -1));
			List<BasicDBObject> oidPipeline = Arrays.asList(match, oidSort);
			AggregateIterable<Document> oidTotal = coll.aggregate(oidPipeline);
			oidTotal.allowDiskUse(true);

			MongoCursor<Document> oidTotalCursor = (MongoCursor<Document>) oidTotal.iterator();

			while (oidTotalCursor.hasNext()) {
				Document dbobj = oidTotalCursor.next();
				TransactionSearch transReport = new TransactionSearch();
				BigInteger txnID = new BigInteger(dbobj.getString(FieldType.TXN_ID.toString()));
				transReport.setTransactionId((txnID));
				transReport.setPgRefNum(pgRefNum);
				transReport.setPayId(dbobj.getString(FieldType.PAY_ID.toString()));

				if ((null != dbobj.getString(FieldType.TXNTYPE.toString())) && (dbobj
						.getString(FieldType.TXNTYPE.toString()).equalsIgnoreCase(TransactionType.REFUND.getName()))) {
					transReport.setTxnType(dbobj.getString(FieldType.TXNTYPE.toString()));
					transReport.setRefundAmount(dbobj.getString(FieldType.REFUNDAMOUNT.toString()));
				} else if (null != dbobj.getString(FieldType.ORIG_TXNTYPE.toString())) {
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

				transReport.setRrn(currRrn);
				transReport.setAcqId(currAcq);

				if (null != dbobj.getString(FieldType.PAYMENT_TYPE.toString())) {
					transReport.setPaymentMethods(
							PaymentType.getpaymentName(dbobj.getString(FieldType.PAYMENT_TYPE.toString())));
				} else {
					transReport.setPaymentMethods(CrmFieldConstants.NA.getValue());
				}

				if ((null != dbobj.getString(FieldType.STATUS.toString())) && (dbobj
						.getString(FieldType.STATUS.toString()).equalsIgnoreCase(TransactionType.SETTLE.getName()))) {
					transReport.setArn(currArn);

				}

				transReport.setInternalCustIP(dbobj.getString(FieldType.INTERNAL_CUST_IP.getName()));
				transReport.setCreateDate(dbobj.getString(FieldType.CREATE_DATE.getName()));
				transReport.setStatus(dbobj.getString(FieldType.STATUS.toString()));
				transReport.setDateFrom(dbobj.getString(FieldType.CREATE_DATE.getName()));
				transReport.setAmount(dbobj.getString(FieldType.AMOUNT.toString()));
				transReport.setOrderId(dbobj.getString(FieldType.ORDER_ID.toString()));
				if (StringUtils.isNotBlank(dbobj.getString(FieldType.REFUND_ORDER_ID.toString()))) {
					transReport.setRefundOrderId(dbobj.getString(FieldType.REFUND_ORDER_ID.toString()));
				} else {
					transReport.setRefundOrderId(CrmFieldConstants.NA.getValue());
				}
				transReport.setoId(dbobj.getString(FieldType.OID.toString()));
				transReport.setTransactionCaptureDate(dbobj.getString(FieldType.PG_DATE_TIME.toString()));
				if (transReport.getTxnType().contains(TransactionType.REFUND.getName())) {
					transReport.setTxnType(TransactionType.REFUND.getName());
				} else {
					transReport.setTxnType(TransactionType.SALE.getName());
				}

				transReport.setApprovedAmount(dbobj.getString(FieldType.AMOUNT.toString()));

				if (dbobj.getString(FieldType.TOTAL_AMOUNT.toString()) != null) {
					transReport.setTotalAmount(dbobj.getString(FieldType.TOTAL_AMOUNT.toString()));
				} else {
					transReport.setTotalAmount("");
				}

				transReport.setCurrentStatus(currStatus);
				Comparator<TransactionSearch> comp = (TransactionSearch a, TransactionSearch b) -> {

					if (a.getDateFrom().compareTo(b.getDateFrom()) > 0) {
						return -1;
					} else if (a.getDateFrom().compareTo(b.getDateFrom()) < 0) {
						return 1;
					} else {
						return 0;
					}
				};

				transactionList.add(transReport);
				Collections.sort(transactionList, comp);
			}

			oidTotalCursor.close();
			logger.info("Inside TxnReports , searchPayment , transactionListSize = " + transactionList.size());
			return transactionList;
		} catch (Exception e) {
			logger.error("Exception occured in TxnReports , searchPayment , Exception = ", e);
			return null;
		}
	}

	public List<String> searchPaymentTxnIdsList(String UserStatus, List<BasicDBObject> fianlList, String startDateIndex,
			String endDateIndex, List<String> pgRefAr) {
		try {
			MongoDatabase dbIns = mongoInstance.getDB();
			MongoCollection<Document> coll = dbIns
					.getCollection(PropertiesManager.propertiesMap.get(prefix + Constants.COLLECTION_NAME.getValue()));
			HashMap<String, String> txnmap = new HashMap<String, String>();

			// For Zero PG_REF Below logic for only current date
			Date date = new Date();
			LocalDate StartDate = date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
			String currentDateIndex = StartDate.toString().replaceAll("-", "");
			if (currentDateIndex.equalsIgnoreCase(startDateIndex)) {
				List<BasicDBObject> oidConditionFinalLst = new ArrayList<BasicDBObject>();
				oidConditionFinalLst.addAll(fianlList);
				List<BasicDBObject> oidConditionLst = new ArrayList<BasicDBObject>();
				oidConditionLst.add(new BasicDBObject(FieldType.PG_REF_NUM.getName(), "0"));
				BasicDBObject finalOidQuery = new BasicDBObject("$and", oidConditionLst);
				oidConditionFinalLst.add(finalOidQuery);
				BasicDBObject finalOidTxnQuery = new BasicDBObject("$and", oidConditionFinalLst);

				BasicDBObject oidTxnMatch = new BasicDBObject("$match", finalOidTxnQuery);
				BasicDBObject oidTxnSort = new BasicDBObject("$sort", new BasicDBObject("CREATE_DATE", -1));

				BasicDBObject oidTxnProjectElement = new BasicDBObject();
				oidTxnProjectElement.put(FieldType.TXN_ID.toString(), 1);
				oidTxnProjectElement.put(FieldType.CREATE_DATE.toString(), 1);
				oidTxnProjectElement.put(FieldType.OID.toString(), 1);

				BasicDBObject oidTxnproject = new BasicDBObject("$project", oidTxnProjectElement);

				List<BasicDBObject> oidTxnpipeline = Arrays.asList(oidTxnMatch, oidTxnproject, oidTxnSort);
				AggregateIterable<Document> oidTxnoutput = coll.aggregate(oidTxnpipeline);
				oidTxnoutput.allowDiskUse(true);
				MongoCursor<Document> oidTxncursor = oidTxnoutput.iterator();
				while (oidTxncursor.hasNext()) {
					Document doc = oidTxncursor.next();
					BasicDBObject oidUniqueQr = new BasicDBObject("OID", doc.get("OID").toString());
					// BasicDBObject oidUniqueQrMatch = new BasicDBObject("$match", oidUniqueQr);
					int oidcount = (int) coll.count(oidUniqueQr);
					if (oidcount == 1) {
						txnmap.put(doc.get("TXN_ID").toString(), doc.get("CREATE_DATE").toString());
					}
				}
				oidTxncursor.close();
				oidConditionFinalLst.clear();
			}

			// End logic

			Iterator pgRefCursor = pgRefAr.iterator();
			List<String> orderIds = new ArrayList<String>();
			orderIds.add("-1");
			List<String> oIds = new ArrayList<String>();
			oIds.add("-1");
			while (pgRefCursor.hasNext()) {
				BasicDBObject pgRefFinalquery = new BasicDBObject("PG_REF_NUM", pgRefCursor.next().toString());
				BasicDBObject pgRefMatch = new BasicDBObject("$match", pgRefFinalquery);
				BasicDBObject pgRefSort = new BasicDBObject("$sort", new BasicDBObject("CREATE_DATE", -1));

				BasicDBObject pgRefProjectElement = new BasicDBObject();
				pgRefProjectElement.put(FieldType.TXN_ID.toString(), 1);
				pgRefProjectElement.put(FieldType.CREATE_DATE.toString(), 1);
				pgRefProjectElement.put(FieldType.ORDER_ID.toString(), 1);
				pgRefProjectElement.put(FieldType.TXNTYPE.toString(), 1);
				pgRefProjectElement.put(FieldType.OID.toString(), 1);
				pgRefProjectElement.put(FieldType.STATUS.toString(), 1);

				BasicDBObject pgRefproject = new BasicDBObject("$project", pgRefProjectElement);
				BasicDBObject pgRefLimit = new BasicDBObject("$limit", 1);
				List<BasicDBObject> pgRefTxnpipeline = Arrays.asList(pgRefMatch, pgRefproject, pgRefSort, pgRefLimit);
				AggregateIterable<Document> pgRefTxnoutput = coll.aggregate(pgRefTxnpipeline);

				pgRefTxnoutput.allowDiskUse(true);
				MongoCursor<Document> pgRefTxncursor = pgRefTxnoutput.iterator();

				if (pgRefTxncursor.hasNext()) {
					Document doc = pgRefTxncursor.next();
					int ordsize = doc.size();
					if (doc.get("TXNTYPE").toString().equalsIgnoreCase(TransactionType.SALE.getName())) {
						if (!(ordsize == 7 && null != doc.get("ORDER_ID")
								&& orderIds.contains(doc.get("ORDER_ID").toString())
								&& oIds.contains(doc.get("OID").toString()))) {

							if (UserStatus.contains("Pending")) {
								List<String> UserstatusList = Arrays.asList(UserStatus.split(","));
								String statusmongo = doc.get("STATUS").toString();
								if (UserstatusList.contains(statusmongo)) {
									txnmap.put(doc.get("TXN_ID").toString(), doc.get("CREATE_DATE").toString());
								}
							} else {
								txnmap.put(doc.get("TXN_ID").toString(), doc.get("CREATE_DATE").toString());
							}
						}
						if (ordsize == 7) {
							orderIds.add(doc.get("ORDER_ID").toString());
						}
						oIds.add(doc.get("OID").toString());
					} else {

						if (UserStatus.contains("Pending")) {
							List<String> UserstatusList = Arrays.asList(UserStatus.split(","));
							String statusmongo = doc.get("STATUS").toString();
							if (UserstatusList.contains(statusmongo)) {
								txnmap.put(doc.get("TXN_ID").toString(), doc.get("CREATE_DATE").toString());
							}
						} else {
							txnmap.put(doc.get("TXN_ID").toString(), doc.get("CREATE_DATE").toString());
						}
					}
				}
				pgRefTxncursor.close();
			}
			TreeMap<String, String> AsendingOrderTxnSet = new TreeMap<String, String>(txnmap);
			AsendingOrderTxnSet.keySet().size();
			Map sortedMap = sortByValueDesc(AsendingOrderTxnSet);
			List<String> txnIdAr = new ArrayList<String>(sortedMap.keySet());
			return txnIdAr;

		} catch (Exception e) {
			logger.error("Exception occured in TxnReports , searchPaymentCount n exception = ", e);
			return null;
		}

	}

	public HashSet<String> searchUniqueOrderId(String pgRefNum, String orderId, String customerEmail,
			String customerPhone, String paymentType, String transactionStatus, String currency, String transactionType,
			String mopType, String acquirer, String merchantPayId, String fromDate, String toDate, User user) {
		logger.info("Inside TxnReports , searchPayment");
		LinkedHashSet<String> orderIdSet = new LinkedHashSet<String>();
		try {

			List<BasicDBObject> paramConditionLst = new ArrayList<BasicDBObject>();
			BasicDBObject allParamQuery = new BasicDBObject();
			BasicDBObject dateQuery = new BasicDBObject();
			// Below variables are for the purpose of Acquirer multi selection
			BasicDBObject acquirerQuery = new BasicDBObject();
			List<BasicDBObject> acquirerConditionLst = new ArrayList<BasicDBObject>();
			// Below variables are for the purpose of Mop Type multi Selection
			List<BasicDBObject> mopTypeConditionLst = new ArrayList<BasicDBObject>();
			BasicDBObject mopTypeQuery = new BasicDBObject();

			// Below variables are for the purpose of Transaction Status multi Selection
			List<BasicDBObject> transactionStatusConditionLst = new ArrayList<BasicDBObject>();
			BasicDBObject transactionStatusQuery = new BasicDBObject();

			// Below variables are for the purpose of Payment Type multi Selection
			List<BasicDBObject> paymentTypeConditionLst = new ArrayList<BasicDBObject>();
			BasicDBObject paymentTypeQuery = new BasicDBObject();
			// Below variables are for the purpose of Transaction Type multi Selection
			List<BasicDBObject> transactionTypeConditionLst = new ArrayList<BasicDBObject>();
			BasicDBObject transactionTypeQuery = new BasicDBObject();

			String currentDate = null;
			boolean isParameterised = false;
			List<BasicDBObject> dateIndexConditionList = new ArrayList<BasicDBObject>();
			BasicDBObject dateIndexConditionQuery = new BasicDBObject();

			// For status pending use only last one hour data , rest will get udpated after
			// status enquiry
			if (transactionStatus.contains("Pending")) {

				DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				String dateEndInner = dateFormat.format(new Date());
				String dateStartInner = dateFormat.format(new Date(System.currentTimeMillis() - 3600 * 1000));

				dateQuery.put(FieldType.CREATE_DATE.getName(),
						BasicDBObjectBuilder.start("$gte", new SimpleDateFormat(dateStartInner).toLocalizedPattern())
								.add("$lte", new SimpleDateFormat(dateEndInner).toLocalizedPattern()).get());

				DateFormat format = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss", Locale.ENGLISH);
				Date dateStart = format.parse(dateStartInner);
				Date dateEnd = format.parse(dateEndInner);

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

			} else {

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
							&& PropertiesManager.propertiesMap.get(Constants.USE_DATE_INDEX.getValue())
									.equalsIgnoreCase("Y")) {
						dateIndexConditionQuery.append("$or", dateIndexConditionList);
					}
				}

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
					List<String> payIdLst = userdao.getActiveMerchantList(user.getSegment(), user.getRole().getId()).stream().map(Merchants::getPayId).collect(Collectors.toList());
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

			if (!pgRefNum.isEmpty()) {
				isParameterised = true;
				paramConditionLst.add(new BasicDBObject(FieldType.PG_REF_NUM.getName(), pgRefNum));
			}
			if (!orderId.isEmpty()) {
				isParameterised = true;
				paramConditionLst.add(new BasicDBObject(FieldType.ORDER_ID.getName(), orderId));
			}
			if (!customerEmail.isEmpty()) {
				paramConditionLst.add(new BasicDBObject(FieldType.CUST_EMAIL.getName(), customerEmail.trim()));
			}

			if (!customerPhone.isEmpty()) {
				paramConditionLst.add(new BasicDBObject(FieldType.CUST_PHONE.getName(), customerPhone));
			}

			if (!currency.equalsIgnoreCase("ALL")) {
				paramConditionLst.add(new BasicDBObject(FieldType.CURRENCY_CODE.getName(), currency));
			} else {

			}

			if (!transactionType.equalsIgnoreCase("ALL")) {
				paramConditionLst.add(new BasicDBObject(FieldType.ORIG_TXNTYPE.getName(), transactionType));
			} else {
				// below logic is for both Sale or Refund transaction fetching
				List<TxnType> transactionTypeList = TxnType.gettxnType();
				for (TxnType txn : transactionTypeList) {
					transactionTypeConditionLst.add(new BasicDBObject(FieldType.ORIG_TXNTYPE.getName(), txn.getName()));
				}
				transactionTypeQuery.append("$or", transactionTypeConditionLst);
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
			if (!transactionStatus.equalsIgnoreCase("ALL")) {
				List<String> txnStatusList = Arrays.asList(transactionStatus.split(","));
				for (String txnStatus : txnStatusList) {
					transactionStatusConditionLst.add(new BasicDBObject(FieldType.STATUS.getName(), txnStatus.trim()));
				}
				transactionStatusQuery.append("$or", transactionStatusConditionLst);
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

			logger.info("Inside TxnReports , searchPayment , finalquery = " + finalquery);
			MongoDatabase dbIns = mongoInstance.getDB();
			MongoCollection<Document> coll = dbIns
					.getCollection(PropertiesManager.propertiesMap.get(prefix + Constants.COLLECTION_NAME.getValue()));
			BasicDBObject match = new BasicDBObject("$match", finalquery);
			BasicDBObject txnSort = new BasicDBObject("$sort", new BasicDBObject("CREATE_DATE", -1));
			BasicDBObject projectElement = new BasicDBObject();
			projectElement.put(FieldType.ORDER_ID.toString(), 1);
			projectElement.put(FieldType.TXNTYPE.toString(), 1);
			projectElement.put(FieldType.CREATE_DATE.toString(), 1);
			BasicDBObject project = new BasicDBObject("$project", projectElement);
			List<BasicDBObject> pgRefPipeline = Arrays.asList(match, txnSort, project);
			AggregateIterable<Document> collIterate = coll.aggregate(pgRefPipeline);
			collIterate.allowDiskUse(true);

			MongoCursor<Document> cursor = (MongoCursor<Document>) collIterate.iterator();

			Set<String> orderIdSetStatusWise = new HashSet<String>();

			// For Pending first get all order id list then check last status for this order
			// id
			if (transactionStatus.contains("Pending")) {

				while (cursor.hasNext()) {

					Document doc = cursor.next();

					if (doc.getString("TXNTYPE").equalsIgnoreCase(TransactionType.SALE.getName())
							|| doc.getString("TXNTYPE").equalsIgnoreCase(TransactionType.NEWORDER.getName())) {
						orderIdSetStatusWise.add(doc.getString("ORDER_ID"));
					} else {
						continue;
					}

				}
				cursor.close();

				for (String orderIdPending : orderIdSetStatusWise) {

					BasicDBObject pendingQuery = new BasicDBObject(FieldType.ORDER_ID.getName(), orderIdPending);
					FindIterable<Document> docPending = coll.find(pendingQuery)
							.sort(new BasicDBObject("CREATE_DATE", -1)).limit(1)
							.projection(new BasicDBObject("STATUS", 1));

					if (docPending.iterator().hasNext()) {

						Document document = docPending.iterator().next();
						if (transactionStatus.contains(document.getString("STATUS"))) {
							orderIdSet.add(orderIdPending + ":" + "SALE");
						}
					}

				}

				return orderIdSet;
			}

			while (cursor.hasNext()) {

				Document doc = cursor.next();
				String txnType = "";
				if (doc.get("TXNTYPE") != null && StringUtils.isNotBlank(doc.getString("TXNTYPE"))) {

					if (doc.getString("TXNTYPE").equalsIgnoreCase(TransactionType.SALE.getName())) {
						txnType = TransactionType.SALE.getName();
					} else if (doc.getString("TXNTYPE").equalsIgnoreCase(TransactionType.ENROLL.getName())) {
						txnType = TransactionType.SALE.getName();
					} else if (doc.getString("TXNTYPE").equalsIgnoreCase(TransactionType.REFUND.getName())) {
						txnType = TransactionType.REFUND.getName();
					} else if (doc.getString("TXNTYPE").equalsIgnoreCase(TransactionType.RECO.getName())) {
						txnType = TransactionType.SALE.getName();
					} else if (doc.getString("TXNTYPE").equalsIgnoreCase(TransactionType.NEWORDER.getName())) {
						txnType = TransactionType.SALE.getName();
					} else if (doc.getString("TXNTYPE").equalsIgnoreCase(TransactionType.REFUNDRECO.getName())) {
						txnType = TransactionType.REFUND.getName();
					}

					orderIdSet.add(doc.getString("ORDER_ID") + ":" + txnType);

				} else {
					continue;
				}

			}
			cursor.close();
			return orderIdSet;
		} catch (Exception e) {
			logger.error("Exception occured in TxnReports , searchPayment , Exception = ", e);
			return orderIdSet;
		}

	}

	public List<TransactionSearch> getTxnData(String pgRefNum, String orderId, String customerEmail,
			String customerPhone, String paymentType, String Userstatus, String currency, String transactionType,
			String mopType, String acquirer, String merchantPayId, String fromDate, String toDate, User user) {

		List<TransactionSearch> txnList = new ArrayList<TransactionSearch>();

		try {

			List<BasicDBObject> paramConditionLst = new ArrayList<BasicDBObject>();
			BasicDBObject acquirerQuery = new BasicDBObject();
			List<BasicDBObject> acquirerConditionLst = new ArrayList<BasicDBObject>();
			BasicDBObject allParamQuery = new BasicDBObject();
			BasicDBObject dateQuery = new BasicDBObject();
			List<BasicDBObject> mopTypeConditionLst = new ArrayList<BasicDBObject>();
			BasicDBObject mopTypeQuery = new BasicDBObject();
			List<BasicDBObject> UserstatusConditionLst = new ArrayList<BasicDBObject>();
			BasicDBObject UserstatusQuery = new BasicDBObject();
			List<BasicDBObject> paymentTypeConditionLst = new ArrayList<BasicDBObject>();
			BasicDBObject paymentTypeQuery = new BasicDBObject();

			List<BasicDBObject> transactionTypeConditionLst = new ArrayList<BasicDBObject>();
			BasicDBObject transactionTypeQuery = new BasicDBObject();

			String currentDate = null;
			boolean isParameterised = false;
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

			/*
			 * if (!merchantPayId.equalsIgnoreCase("ALL")) { paramConditionLst.add(new
			 * BasicDBObject(FieldType.PAY_ID.getName(), merchantPayId));
			 * 
			 * }
			 */

			if (merchantPayId.equalsIgnoreCase("ALL")) {
				logger.info("TxnReport, MerchantId : " + merchantPayId + ", Segment : " + user.getSegment());
				if (!user.getSegment().equalsIgnoreCase("Default")) {
					List<String> payIdLst = userdao.getActiveMerchantList(user.getSegment(), user.getRole().getId()).stream().map(Merchants::getPayId).collect(Collectors.toList());
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

			if (!pgRefNum.isEmpty()) {
				isParameterised = true;
				paramConditionLst.add(new BasicDBObject(FieldType.PG_REF_NUM.getName(), pgRefNum));
			}
			if (!orderId.isEmpty()) {
				isParameterised = true;
				paramConditionLst.add(new BasicDBObject(FieldType.ORDER_ID.getName(), orderId));
			}
			if (!customerEmail.isEmpty()) {
				paramConditionLst.add(new BasicDBObject(FieldType.CUST_EMAIL.getName(), customerEmail));
			}

			if (!customerPhone.isEmpty()) {
				paramConditionLst.add(new BasicDBObject(FieldType.CUST_PHONE.getName(), customerPhone));
			}

			if (!currency.equalsIgnoreCase("ALL")) {
				paramConditionLst.add(new BasicDBObject(FieldType.CURRENCY_CODE.getName(), currency));
			} else {

			}

			if (!transactionType.equalsIgnoreCase("ALL")) {
				paramConditionLst.add(new BasicDBObject(FieldType.ORIG_TXNTYPE.getName(), transactionType));
			} else {
				// below logic is for both Sale or Refund transaction fetching
				List<TxnType> transactionTypeList = TxnType.gettxnType();
				for (TxnType txn : transactionTypeList) {
					transactionTypeConditionLst.add(new BasicDBObject(FieldType.ORIG_TXNTYPE.getName(), txn.getName()));
				}
				transactionTypeQuery.append("$or", transactionTypeConditionLst);
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
			if (!Userstatus.equalsIgnoreCase("ALL")) {
				List<String> UserstatusList = Arrays.asList(Userstatus.split(","));
				for (String userStatus : UserstatusList) {
					UserstatusConditionLst.add(new BasicDBObject(FieldType.STATUS.getName(), userStatus.trim()));
				}
				UserstatusQuery.append("$or", UserstatusConditionLst);
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
			if (!UserstatusQuery.isEmpty()) {
				fianlList.add(UserstatusQuery);
			}

			if (!transactionTypeQuery.isEmpty()) {
				fianlList.add(transactionTypeQuery);
			}

			BasicDBObject finalquery = new BasicDBObject("$and", fianlList);

			logger.info("Inside TxnReports , searchPayment , finalquery = " + finalquery);
			MongoDatabase dbIns = mongoInstance.getDB();
			MongoCollection<Document> coll = dbIns
					.getCollection(PropertiesManager.propertiesMap.get(prefix + Constants.COLLECTION_NAME.getValue()));
			BasicDBObject match = new BasicDBObject("$match", finalquery);
			BasicDBObject projectElement = new BasicDBObject();
			projectElement.put(FieldType.ORDER_ID.toString(), 1);
			projectElement.put(FieldType.TXNTYPE.toString(), 1);
			BasicDBObject project = new BasicDBObject("$project", projectElement);
			List<BasicDBObject> pgRefPipeline = Arrays.asList(match, project);
			AggregateIterable<Document> collIterate = coll.aggregate(pgRefPipeline);
			collIterate.allowDiskUse(true);

			// Removing the Duplicates
			HashSet<String> orderIdSet = new HashSet<String>();

			MongoCursor<Document> cursor = (MongoCursor<Document>) collIterate.iterator();

			while (cursor.hasNext()) {

				Document doc = cursor.next();

				String txnType = "";
				if (doc.get("TXNTYPE") != null && StringUtils.isNotBlank(doc.getString("TXNTYPE"))) {

					if (doc.getString("TXNTYPE").equalsIgnoreCase(TransactionType.SALE.getName())) {
						txnType = TransactionType.SALE.getName();
					} else if (doc.getString("TXNTYPE").equalsIgnoreCase(TransactionType.REFUND.getName())) {
						txnType = TransactionType.REFUND.getName();
					} else if (doc.getString("TXNTYPE").equalsIgnoreCase(TransactionType.RECO.getName())) {
						txnType = TransactionType.SALE.getName();
					} else if (doc.getString("TXNTYPE").equalsIgnoreCase(TransactionType.REFUNDRECO.getName())) {
						txnType = TransactionType.REFUND.getName();
					} else if (doc.getString("TXNTYPE").equalsIgnoreCase(TransactionType.NEWORDER.getName())) {
						txnType = TransactionType.SALE.getName();
					} else if (doc.getString("TXNTYPE").equalsIgnoreCase(TransactionType.REFUNDRECO.getName())) {
						txnType = TransactionType.REFUND.getName();
					}

					orderIdSet.add(doc.getString("ORDER_ID") + txnType);
				} else {
					continue;
				}

			}
			cursor.close();
			return txnList;
		} catch (Exception e) {
			logger.error("Exception occured in TxnReports , searchPayment , Exception = " , e);
			return txnList;
		}

	}

	public static Map<String, String> sortByValueDesc(Map<String, String> map) {
		List<Map.Entry<String, String>> list = new LinkedList(map.entrySet());
		Collections.sort(list, new Comparator<Map.Entry<String, String>>() {
			@Override
			public int compare(Map.Entry<String, String> o1, Map.Entry<String, String> o2) {
				return o2.getValue().compareTo(o1.getValue());
			}
		});

		Map<String, String> result = new LinkedHashMap<>();
		for (Map.Entry<String, String> entry : list) {
			result.put(entry.getKey(), entry.getValue());
		}
		return result;
	}

//	public int searchPaymentCount(String pgRefNum, String orderId, String customerEmail, String merchantPayId,
//			String paymentType, String Userstatus, String currency, String transactionType, String fromDate,
//			String toDate, User user, String phoneNo, String mopType) {

	public int searchPaymentCount(String pgRefNum, String orderId, String customerEmail, String merchantPayId,
			String paymentType, String Userstatus, String currency, String transactionType, String fromDate,
			String toDate, User user, String phoneNo, String mopType, String udf4,String channel) {

		logger.info("Inside TxnReports , searchPaymentCount");
		boolean isParameterised = false;
		try {
			List<BasicDBObject> paramConditionLst = new ArrayList<BasicDBObject>();
			List<BasicDBObject> statusCondition = new ArrayList<BasicDBObject>();

			List<BasicDBObject> dateCondition = new ArrayList<BasicDBObject>();

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

				dateCondition.add(new BasicDBObject(FieldType.CREATE_DATE.getName(),
						BasicDBObjectBuilder.start("$gte", new SimpleDateFormat(fromDate).toLocalizedPattern())
								.add("$lte", new SimpleDateFormat(currentDate).toLocalizedPattern()).get()));

				dateCondition.add(new BasicDBObject(FieldType.SETTLEMENT_DATE.getName(),
						BasicDBObjectBuilder.start("$gte", new SimpleDateFormat(fromDate).toLocalizedPattern())
								.add("$lte", new SimpleDateFormat(currentDate).toLocalizedPattern()).get()));

				dateQuery.put("$or", dateCondition);

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
				dateIndexConditionList.add(new BasicDBObject("SETTLEMENT_DATE_INDEX", dateIndex));
			}

			if (!Userstatus.equalsIgnoreCase(StatusType.SETTLED_SETTLE.getName())) {
				if (StringUtils.isNotBlank(PropertiesManager.propertiesMap.get(Constants.USE_DATE_INDEX.getValue()))
						&& PropertiesManager.propertiesMap.get(Constants.USE_DATE_INDEX.getValue())
								.equalsIgnoreCase("Y")) {
					dateIndexConditionQuery.append("$or", dateIndexConditionList);
				}
			} else {
				if (StringUtils.isNotBlank(PropertiesManager.propertiesMap.get(Constants.USE_DATE_INDEX.getValue()))
						&& PropertiesManager.propertiesMap.get(Constants.USE_DATE_INDEX.getValue())
								.equalsIgnoreCase("Y")) {
					dateIndexConditionQuery.append("$or", dateIndexConditionList);
				}
			}

			/*
			 * if (!merchantPayId.equalsIgnoreCase("ALL")) { //TODO MYSql -- >> SplitPayment
			 * PayIdList IN Condition isParameterised = true; paramConditionLst.add(new
			 * BasicDBObject(FieldType.PAY_ID.getName(), merchantPayId)); }
			 */

			if (merchantPayId.equalsIgnoreCase("ALL")) {
				logger.info("TxnReport, MerchantId : " + merchantPayId + ", Segment : " + user.getSegment());
				if (!user.getSegment().equalsIgnoreCase("Default")) {
					List<String> payIdLst = userdao.getActiveMerchantList(user.getSegment(), user.getRole().getId()).stream().map(Merchants::getPayId).collect(Collectors.toList());
					logger.info("Get PayId List : " + payIdLst);
					if (payIdLst.size() > 0) {
						paramConditionLst
								.add(new BasicDBObject(FieldType.PAY_ID.getName(), new BasicDBObject("$in", payIdLst)));
					}
				}

			} else {
				isParameterised = true;
				paramConditionLst.add(new BasicDBObject(FieldType.PAY_ID.getName(), merchantPayId));
			}
			if (bsesPayid != null) {
				if (user.getPayId().equalsIgnoreCase(bsesPayid) && !StringUtils.isBlank(udf4)) {
					if (!udf4.equalsIgnoreCase("All")) {
						paramConditionLst.add(new BasicDBObject(FieldType.UDF9.getName(), udf4));
					}

				}
			}
			if (user.getUserType().equals(UserType.RESELLER)) {
				paramConditionLst.add(new BasicDBObject(FieldType.RESELLER_ID.getName(), user.getResellerId()));
			}

			if (!channel.equalsIgnoreCase("All")) {
				
				paramConditionLst.add(new BasicDBObject(FieldType.CHANNEL.getName(), channel));
			}
			if (!pgRefNum.isEmpty()) {
				isParameterised = true;
				paramConditionLst.add(new BasicDBObject(FieldType.PG_REF_NUM.getName(), pgRefNum));
			}
			if (!orderId.isEmpty()) {
				isParameterised = true;
				paramConditionLst.add(new BasicDBObject(FieldType.ORDER_ID.getName(), orderId));
			}
			if (!phoneNo.isEmpty()) {
				isParameterised = true;
				paramConditionLst.add(new BasicDBObject(FieldType.CUST_PHONE.getName(), phoneNo));
			}
			if (!mopType.equalsIgnoreCase("ALL")) {
				isParameterised = true;
				paramConditionLst.add(new BasicDBObject(FieldType.MOP_TYPE.getName(), mopType));
			}
			if (!customerEmail.isEmpty()) {
				isParameterised = true;
				paramConditionLst.add(new BasicDBObject(FieldType.CUST_EMAIL.getName(), customerEmail));
			}

			if (!Userstatus.equalsIgnoreCase("ALL")) {
				isParameterised = true;
				statusCondition.add(new BasicDBObject(FieldType.STATUS.getName(), Userstatus));
			} else {

			}

			if (!StatusType.SETTLED_SETTLE.getName().equalsIgnoreCase(Userstatus)) {
				statusCondition.add(new BasicDBObject(FieldType.STATUS.getName(), StatusType.SETTLED_SETTLE.getName()));
			}

//            if (!StatusType.SETTLED_RECONCILLED.getName().equalsIgnoreCase(Userstatus)) {
//                statusCondition.add(new BasicDBObject(FieldType.STATUS.getName(), StatusType.SETTLED_RECONCILLED.getName()));
//            }

			if (!statusCondition.isEmpty()) {
				BasicDBObject statusConditionQuery = new BasicDBObject("$or", statusCondition);
				paramConditionLst.add(statusConditionQuery);
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

			logger.info("Inside TxnReports , searchPaymentCount , fianlList = " + fianlList);
			BasicDBObject finalquery = new BasicDBObject("$and", fianlList);
			MongoDatabase dbIns = mongoInstance.getDB();
			MongoCollection<Document> coll = dbIns.getCollection(
					PropertiesManager.propertiesMap.get(prefix + Constants.TRANSACTION_STATUS_COLLECTION.getValue()));

			BasicDBObject match = new BasicDBObject("$match", finalquery);
			Document firstGroup = new Document("_id", new Document("PG_REF_NUM", "$PG_REF_NUM")
					.append("STATUS", "$STATUS").append("REFUND_ORDER_ID", "$REFUND_ORDER_ID"));
			BasicDBObject firstGroupObject = new BasicDBObject(firstGroup);
			BasicDBObject secondGroup = new BasicDBObject("$last", "$$ROOT");
			BasicDBObject groupQuery = new BasicDBObject("$group", firstGroupObject.append("contentList", secondGroup));
			return coll.aggregate(Arrays.asList(match, groupQuery)).into(new ArrayList<>()).size();

		} catch (Exception e) {
			logger.error("Exception occured in TxnReports , searchPaymentCount n exception = " , e);
			return 0;
		}
	}

	public int searchPaymentCount(String pgRefNum, String orderId, String customerEmail, String customerPhone,
			String merchantPayId, String paymentType, String Userstatus, String currency, String transactionType,
			String mopType, String fromDate, String toDate, User user) {

		logger.info("Inside TxnReports , searchPaymentCount");
		boolean isParameterised = false;
		try {
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

			/*
			 * if (!merchantPayId.equalsIgnoreCase("ALL")) { isParameterised = true;
			 * paramConditionLst.add(new BasicDBObject(FieldType.PAY_ID.getName(),
			 * merchantPayId)); }
			 */

			if (merchantPayId.equalsIgnoreCase("ALL")) {
				logger.info("TxnReport, MerchantId : " + merchantPayId + ", Segment : " + user.getSegment());
				if (!user.getSegment().equalsIgnoreCase("Default")) {
					List<String> payIdLst = userdao.getActiveMerchantList(user.getSegment(), user.getRole().getId()).stream().map(Merchants::getPayId).collect(Collectors.toList());
					logger.info("Get PayId List : " + payIdLst);
					if (payIdLst.size() > 0) {
						paramConditionLst
								.add(new BasicDBObject(FieldType.PAY_ID.getName(), new BasicDBObject("$in", payIdLst)));
					}
				}

			} else {
				isParameterised = true;
				paramConditionLst.add(new BasicDBObject(FieldType.PAY_ID.getName(), merchantPayId));
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
				paramConditionLst.add(new BasicDBObject(FieldType.CUST_EMAIL.getName(), customerEmail));
			}

			if (!customerPhone.isEmpty()) {
				isParameterised = true;
				paramConditionLst.add(new BasicDBObject(FieldType.CUST_PHONE.getName(), customerPhone));
			}
			if (!Userstatus.equalsIgnoreCase("ALL")) {
				isParameterised = true;
				paramConditionLst.add(new BasicDBObject(FieldType.STATUS.getName(), Userstatus));
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
			if (!mopType.equalsIgnoreCase("ALL")) {
				isParameterised = true;
				paramConditionLst.add(new BasicDBObject(FieldType.MOP_TYPE.getName(), mopType));
			} else {
			}
			if (!paymentType.equalsIgnoreCase("ALL")) {
				isParameterised = true;
				paramConditionLst.add(new BasicDBObject(FieldType.PAYMENT_TYPE.getName(), paymentType));
			} else {

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

			logger.info("Inside TxnReports , searchPaymentCount , fianlList = " + fianlList);
			BasicDBObject finalquery = new BasicDBObject("$and", fianlList);
			MongoDatabase dbIns = mongoInstance.getDB();
			MongoCollection<Document> coll = dbIns
					.getCollection(PropertiesManager.propertiesMap.get(prefix + Constants.COLLECTION_NAME.getValue()));

			int txnCount = 0;
			txnCount = (int) coll.count(finalquery);
			return txnCount;

		} catch (Exception e) {
			logger.error("Exception occured in TxnReports , searchPaymentCount n exception = " , e);
			return 0;
		}
	}

	public int searchPaymentCount(String pgRefNum, String orderId, String customerEmail, String customerPhone,
			String paymentType, String Userstatus, String currency, String transactionType, String mopType,
			String acquirer, String merchantPayId, String fromDate, String toDate, User user) {

		logger.info("Inside TxnReports , searchPaymentCount");
		try {
			BasicDBObject dateQuery = new BasicDBObject();
			List<BasicDBObject> paramConditionLst = new ArrayList<BasicDBObject>();
			BasicDBObject acquirerQuery = new BasicDBObject();
			List<BasicDBObject> acquirerConditionLst = new ArrayList<BasicDBObject>();
			BasicDBObject allParamQuery = new BasicDBObject();
			List<BasicDBObject> mopTypeConditionLst = new ArrayList<BasicDBObject>();
			BasicDBObject mopTypeQuery = new BasicDBObject();
			List<BasicDBObject> UserstatusConditionLst = new ArrayList<BasicDBObject>();
			BasicDBObject UserstatusQuery = new BasicDBObject();
			List<BasicDBObject> paymentTypeConditionLst = new ArrayList<BasicDBObject>();
			BasicDBObject paymentTypeQuery = new BasicDBObject();
			List<BasicDBObject> transactionTypeConditionLst = new ArrayList<BasicDBObject>();
			BasicDBObject transactionTypeQuery = new BasicDBObject();

			boolean isParameterised = false;
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

			/*
			 * if (!merchantPayId.equalsIgnoreCase("ALL")) { paramConditionLst.add(new
			 * BasicDBObject(FieldType.PAY_ID.getName(), merchantPayId)); }
			 */

			if (merchantPayId.equalsIgnoreCase("ALL")) {
				logger.info("TxnReport, MerchantId : " + merchantPayId + ", Segment : " + user.getSegment());
				if (!user.getSegment().equalsIgnoreCase("Default")) {
					List<String> payIdLst = userdao.getActiveMerchantList(user.getSegment(), user.getRole().getId()).stream().map(Merchants::getPayId).collect(Collectors.toList());
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

			if (!pgRefNum.isEmpty()) {
				isParameterised = true;
				paramConditionLst.add(new BasicDBObject(FieldType.PG_REF_NUM.getName(), pgRefNum));
			}
			if (!orderId.isEmpty()) {
				isParameterised = true;
				paramConditionLst.add(new BasicDBObject(FieldType.ORDER_ID.getName(), orderId));
			}
			if (!customerEmail.isEmpty()) {
				paramConditionLst.add(new BasicDBObject(FieldType.CUST_EMAIL.getName(), customerEmail));
			}
			if (!customerPhone.isEmpty()) {
				paramConditionLst.add(new BasicDBObject(FieldType.CUST_PHONE.getName(), customerPhone));
			}

			if (!currency.equalsIgnoreCase("ALL")) {
				paramConditionLst.add(new BasicDBObject(FieldType.CURRENCY_CODE.getName(), currency));
			} else {

			}

			if (!transactionType.equalsIgnoreCase("ALL")) {
				paramConditionLst.add(new BasicDBObject(FieldType.ORIG_TXNTYPE.getName(), transactionType));
			} else {
				// below logic is for both Sale or Refund transaction fetching
				List<TxnType> transactionTypeList = TxnType.gettxnType();
				for (TxnType txn : transactionTypeList) {
					transactionTypeConditionLst.add(new BasicDBObject(FieldType.ORIG_TXNTYPE.getName(), txn.getName()));
				}
				transactionTypeQuery.append("$or", transactionTypeConditionLst);
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
					if (mop.equals("Others")) {
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
			if (!Userstatus.equalsIgnoreCase("ALL")) {
				List<String> UserstatusList = Arrays.asList(Userstatus.split(","));
				for (String userStatus : UserstatusList) {
					UserstatusConditionLst.add(new BasicDBObject(FieldType.STATUS.getName(), userStatus.trim()));
				}
				UserstatusQuery.append("$or", UserstatusConditionLst);
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
			if (!UserstatusQuery.isEmpty()) {
				fianlList.add(UserstatusQuery);
			}

			if (!transactionTypeQuery.isEmpty()) {
				fianlList.add(transactionTypeQuery);
			}

			logger.info("Inside TxnReports , searchPaymentCount , fianlList = " + fianlList);
			BasicDBObject finalquery = new BasicDBObject("$and", fianlList);
			MongoDatabase dbIns = mongoInstance.getDB();
			MongoCollection<Document> coll = dbIns
					.getCollection(PropertiesManager.propertiesMap.get(prefix + Constants.COLLECTION_NAME.getValue()));

			DistinctIterable<String> output = coll.distinct("PG_REF_NUM", finalquery, String.class);
			MongoCursor<String> cursor = output.iterator();
			HashSet distinctset = new HashSet();

			int txnCount = 0;
			while (cursor.hasNext()) {
				distinctset.add(cursor.next().toString());
			}
			cursor.close();
			distinctset.remove("0");
			ArrayList<String> pgRefAr = new ArrayList<String>(distinctset);

			LocalDate StartDate = dateStart.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
			String startDateIndex = StartDate.toString().replaceAll("-", "");
			String endDateIndex = endDate.toString().replaceAll("-", "");

			List<String> txnIdAr = searchPaymentTxnIdsList(Userstatus, fianlList, startDateIndex, endDateIndex,
					pgRefAr);
			txnCount = txnIdAr.size();

			// Below Total Count
			return txnCount;

		} catch (Exception e) {
			logger.error("Exception occured in TxnReports , searchPaymentCount n exception = " , e);
			return 0;
		}

	}

	public HashMap<String, String> totalAmountOfAllTxns(String pgRefNum, String orderId, String customerEmail,
			String customerPhone, String paymentType, String Userstatus, String currency, String transactionType,
			String mopType, String acquirer, String merchantPayId, String fromDate, String toDate, User user,
			boolean initiative, HashSet<String> orderIdSet) {

		logger.info("Inside TxnReports , totalAmountOFAllTxns");
		try {
			// HashSet<String> orderIdSet = new HashSet<String>();
			// If pending is searched for a previous date , send empty data as pending is
			// applicable for today's date only

			if (Userstatus.contains("Pending")) {

				DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				String dateToday = dateFormat.format(new Date());

				DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH);
				Date todayDate = format.parse(dateToday);
				Date providedDate = format.parse(toDate);

				if (providedDate.compareTo(todayDate) < 0) {

				} else {
					if (orderIdSet == null || orderIdSet.isEmpty() || orderIdSet.size() == 0) {
						orderIdSet = searchUniqueOrderId(pgRefNum, orderId, customerEmail, customerPhone, paymentType,
								Userstatus, currency, transactionType, mopType, acquirer, merchantPayId, fromDate,
								toDate, user);
					}

				}

			} else {
				if (orderIdSet == null || orderIdSet.isEmpty() || orderIdSet.size() == 0) {
					orderIdSet = searchUniqueOrderId(pgRefNum, orderId, customerEmail, customerPhone, paymentType,
							Userstatus, currency, transactionType, mopType, acquirer, merchantPayId, fromDate, toDate,
							user);
				}
			}

			// Below variables for initiative logic
			int totaltxns = 0;
			int totalSaleSuccCount = 0;
			int totalSalefailCount = 0;
			int totalSalePendingCount = 0;
			int totalSaleTOCount = 0;
			int totalSaleCancelledCount = 0;
			int totalSaleInvalidCount = 0;
			int totalRefundSuccCount = 0;
			int totalRefundFailCount = 0;
			Double totalTxnAmount = 0.00, totalSalefailAmount = 0.00, totalSalePendingAmount = 0.00,
					totalSaleSuccAmount = 0.00;
			Double totalSaleTOAmount = 0.00, totalSaleCancelledAmount = 0.00, totalRefundSuccAmount = 0.00,
					totalRefundFailAmount = 0.00;
			Double totalSaleInvalidAmount = 0.00;

			// Below variables for initiative logic
			int totalSettleCount = 0;
			int totalSaleCCcount = 0;
			int totalSaleDCcount = 0;
			int totalSaleNBcount = 0;
			int totalSalewalletcount = 0;
			int totalSaleUPIcount = 0;
			int totalSaleCount = 0;
			int totalRfCCcount = 0;
			int totalRfDCcount = 0;
			int totalRfNBcount = 0;
			int totalRfwalletcount = 0;
			int totalRfUPIcount = 0;
			int totalRfCount = 0;

			Double totalSettleAmount = 0.00, totalSaleCCAmount = 0.00, totalSaleDCAmount = 0.00,
					totalSaleNBAmount = 0.00;
			Double totalSalewalletAmount = 0.00, totalSaleUPIAmount = 0.00, totalSaleAmount = 0.00,
					totalRfCCAmount = 0.00;
			Double totalRfDCAmount = 0.00, totalRfNBAmount = 0.00, totalRfwalletAmount = 0.00, totalRfUPIAmount = 0.00;
			Double totalRfAmount = 0.00;
			HashMap<String, String> finalMap = new HashMap<String, String>();

			List<String> txnList = new ArrayList<String>(orderIdSet);

			if (!txnList.isEmpty()) {
				MongoDatabase dbIns = mongoInstance.getDB();
				MongoCollection<Document> coll = dbIns.getCollection(
						PropertiesManager.propertiesMap.get(prefix + Constants.COLLECTION_NAME.getValue()));
				for (int i = 0; i < txnList.size(); i++) {

					String txnUnit = txnList.get(i);
					String txnUnitArr[] = txnUnit.split(":");

					String actualorderId = txnUnitArr[0];
					String txnType = txnUnitArr[1];

					List<BasicDBObject> txnTypList = new ArrayList<BasicDBObject>();
					BasicDBObject ordquery = new BasicDBObject("ORDER_ID", actualorderId);
					List<BasicDBObject> queryList = new ArrayList<BasicDBObject>();

					if (!Userstatus.equalsIgnoreCase("Settled")) {
						if (txnType.equalsIgnoreCase(TransactionType.SALE.getName())) {
							txnTypList.add(new BasicDBObject("TXNTYPE", TransactionType.SALE.getName()));
							txnTypList.add(new BasicDBObject("TXNTYPE", TransactionType.ENROLL.getName()));
							txnTypList.add(new BasicDBObject("TXNTYPE", TransactionType.NEWORDER.getName()));
							txnTypList.add(new BasicDBObject("TXNTYPE", TransactionType.RECO.getName()));
						} else {
							txnTypList.add(new BasicDBObject("TXNTYPE", TransactionType.REFUND.getName()));
							txnTypList.add(new BasicDBObject("TXNTYPE", TransactionType.REFUNDRECO.getName()));
						}

						BasicDBObject txnTypQuery = new BasicDBObject("$or", txnTypList);
						queryList.add(txnTypQuery);
					} else {
						if (txnType.equalsIgnoreCase(TransactionType.SALE.getName())) {
							txnTypList.add(new BasicDBObject("TXNTYPE", TransactionType.RECO.getName()));
						} else {
							txnTypList.add(new BasicDBObject("TXNTYPE", TransactionType.REFUNDRECO.getName()));
						}

						BasicDBObject txnTypQuery = new BasicDBObject("$or", txnTypList);
						queryList.add(txnTypQuery);
					}

					// For Cancelled , Failed and Timeout status , include status also in the query
					if (Userstatus.contains("Cancelled") || Userstatus.contains("Failed")
							|| Userstatus.contains("Timeout") || Userstatus.contains("Invalid")
							|| Userstatus.contains("Approved") || Userstatus.contains("Captured")) {

						List<BasicDBObject> transactionStatusConditionLst = new ArrayList<BasicDBObject>();
						BasicDBObject transactionStatusQuery = new BasicDBObject();

						List<String> txnStatusList = Arrays.asList(Userstatus.split(","));
						for (String txnStatus : txnStatusList) {
							transactionStatusConditionLst
									.add(new BasicDBObject(FieldType.STATUS.getName(), txnStatus.trim()));
						}
						transactionStatusQuery.append("$or", transactionStatusConditionLst);
						queryList.add(transactionStatusQuery);
					}

					queryList.add(ordquery);

					BasicDBObject txnFinalquery = new BasicDBObject("$and", queryList);
					BasicDBObject txnMatch = new BasicDBObject("$match", txnFinalquery);
					BasicDBObject txnSort = new BasicDBObject("$sort", new BasicDBObject("CREATE_DATE", -1));
					BasicDBObject txnLimit = new BasicDBObject("$limit", 1);

					BasicDBObject txnProjectElement = new BasicDBObject();
					txnProjectElement.put(FieldType.AMOUNT.toString(), 1);
					txnProjectElement.put(FieldType.TXNTYPE.toString(), 1);
					txnProjectElement.put(FieldType.STATUS.toString(), 1);
					if (!initiative) {
						txnProjectElement.put(FieldType.PAYMENT_TYPE.toString(), 1);
					}

					BasicDBObject txnproject = new BasicDBObject("$project", txnProjectElement);

					List<BasicDBObject> pipeline = Arrays.asList(txnMatch, txnSort, txnLimit, txnproject);
					AggregateIterable<Document> output = coll.aggregate(pipeline);

					output.allowDiskUse(true);
					MongoCursor<Document> cursor = output.iterator();

					if (!cursor.hasNext()) {
						continue;
					}
					Document dbobj = cursor.next();
					// initiative logic

					try {
						if (initiative) {
							if (dbobj.get("AMOUNT") != null && StringUtils.isNotBlank(dbobj.getString("AMOUNT"))) {
								if (dbobj.getString(FieldType.TXNTYPE.toString())
										.equalsIgnoreCase(TransactionType.SALE.getName())
										|| dbobj.getString(FieldType.TXNTYPE.toString())
												.equalsIgnoreCase(TransactionType.ENROLL.getName())
										|| dbobj.getString(FieldType.TXNTYPE.toString())
												.equalsIgnoreCase(TransactionType.NEWORDER.getName())) {
									if (dbobj.getString(FieldType.STATUS.toString())
											.equalsIgnoreCase(StatusType.CAPTURED.getName())
											|| dbobj.getString(FieldType.STATUS.toString())
													.equalsIgnoreCase(StatusType.APPROVED.getName())) {
										// This logic for Success
										totalSaleSuccAmount += Double
												.valueOf(dbobj.get(FieldType.AMOUNT.toString()).toString());
										totalSaleSuccCount++;
									} else if (dbobj.getString(FieldType.STATUS.toString())
											.equalsIgnoreCase(StatusType.TIMEOUT.getName())
											|| dbobj.getString(FieldType.STATUS.toString())
													.equalsIgnoreCase(StatusType.ACQUIRER_TIMEOUT.getName())) {
										// This logic is for Time out
										totalSaleTOAmount += Double
												.valueOf(dbobj.get(FieldType.AMOUNT.toString()).toString());
										totalSaleTOCount++;
									} else if (dbobj.getString(FieldType.STATUS.toString())
											.equalsIgnoreCase(StatusType.PENDING.getName())
											|| dbobj.getString(FieldType.STATUS.toString())
													.equalsIgnoreCase(StatusType.PENDING_AT_ACQUIRER.getName())
											|| dbobj.getString(FieldType.STATUS.toString())
													.equalsIgnoreCase(StatusType.ENROLLED.getName())
											|| dbobj.getString(FieldType.STATUS.toString())
													.equalsIgnoreCase(StatusType.SENT_TO_BANK.getName())
											|| dbobj.getString(FieldType.STATUS.toString())
													.equalsIgnoreCase(StatusType.PROCESSING.getName())
											|| dbobj.getString(FieldType.STATUS.toString())
													.equalsIgnoreCase(StatusType.PROCESSED.getName())) {
										// This logic is for Pending
										totalSalePendingAmount += Double
												.valueOf(dbobj.get(FieldType.AMOUNT.toString()).toString());
										totalSalePendingCount++;
									} else if (dbobj.getString(FieldType.STATUS.toString())
											.equalsIgnoreCase(StatusType.BROWSER_CLOSED.getName())
											|| dbobj.getString(FieldType.STATUS.toString())
													.equalsIgnoreCase(StatusType.CANCELLED.getName())) {
										// This logic is for Cancelled
										totalSaleCancelledAmount += Double
												.valueOf(dbobj.get(FieldType.AMOUNT.toString()).toString());
										totalSaleCancelledCount++;
									} else if (dbobj.getString(FieldType.STATUS.toString())
											.equalsIgnoreCase(StatusType.INVALID.getName())) {
										// This logic is for Invalid
										totalSaleInvalidAmount += Double
												.valueOf(dbobj.get(FieldType.AMOUNT.toString()).toString());
										totalSaleInvalidCount++;
									} else {
										// This logic for Failed
										totalSalefailAmount += Double
												.valueOf(dbobj.get(FieldType.AMOUNT.toString()).toString());
										totalSalefailCount++;
									}
								}
								if (dbobj.getString(FieldType.TXNTYPE.toString())
										.equalsIgnoreCase(TransactionType.REFUND.getName())
										|| dbobj.getString(FieldType.TXNTYPE.toString())
												.equalsIgnoreCase(TransactionType.REFUNDRECO.getName())) {
									if (dbobj.getString(FieldType.STATUS.toString())
											.equalsIgnoreCase(StatusType.CAPTURED.getName())) {
										totalRefundSuccAmount += Double
												.valueOf(dbobj.get(FieldType.AMOUNT.toString()).toString());
										totalRefundSuccCount++;
									} else {
										totalRefundFailAmount += Double
												.valueOf(dbobj.get(FieldType.AMOUNT.toString()).toString());
										totalRefundFailCount++;
									}
								}
								totalTxnAmount += Double.valueOf(dbobj.get(FieldType.AMOUNT.toString()).toString());
								totaltxns++;
							} else {
								if (dbobj.getString(FieldType.TXNTYPE.toString())
										.equalsIgnoreCase(TransactionType.SALE.getName())
										|| dbobj.getString(FieldType.TXNTYPE.toString())
												.equalsIgnoreCase(TransactionType.ENROLL.getName())) {
									if (dbobj.getString(FieldType.STATUS.toString())
											.equalsIgnoreCase(StatusType.TIMEOUT.getName())
											|| dbobj.getString(FieldType.STATUS.toString())
													.equalsIgnoreCase(StatusType.ACQUIRER_TIMEOUT.getName())) {
										totalSaleTOCount++;
									} else if (dbobj.getString(FieldType.STATUS.toString())
											.equalsIgnoreCase(StatusType.PENDING.getName())
											|| dbobj.getString(FieldType.STATUS.toString())
													.equalsIgnoreCase(StatusType.PENDING_AT_ACQUIRER.getName())
											|| dbobj.getString(FieldType.STATUS.toString())
													.equalsIgnoreCase(StatusType.ENROLLED.getName())
											|| dbobj.getString(FieldType.STATUS.toString())
													.equalsIgnoreCase(StatusType.SENT_TO_BANK.getName())
											|| dbobj.getString(FieldType.STATUS.toString())
													.equalsIgnoreCase(StatusType.PROCESSING.getName())
											|| dbobj.getString(FieldType.STATUS.toString())
													.equalsIgnoreCase(StatusType.PROCESSED.getName())) {
										// This logic is for Pending
										totalSalePendingCount++;
									} else if (dbobj.getString(FieldType.STATUS.toString())
											.equalsIgnoreCase(StatusType.BROWSER_CLOSED.getName())
											|| dbobj.getString(FieldType.STATUS.toString())
													.equalsIgnoreCase(StatusType.CANCELLED.getName())) {
										// This logic is for Cancelled
										totalSaleCancelledCount++;
									} else if (dbobj.getString(FieldType.STATUS.toString())
											.equalsIgnoreCase(StatusType.INVALID.getName())) {
										// This logic is for Invalid
										totalSaleInvalidCount++;
									} else {
										totalSalefailCount++;
									}

								} else {
									totalRefundFailCount++;
								}
								totaltxns++;
							}
						} else {

							if (dbobj.get("AMOUNT") != null && StringUtils.isNotBlank(dbobj.getString("AMOUNT"))) {
								if (dbobj.getString(FieldType.TXNTYPE.toString())
										.equalsIgnoreCase(TransactionType.RECO.getName())) {
									if (dbobj.getString(FieldType.PAYMENT_TYPE.toString())
											.equalsIgnoreCase(PaymentTypeUI.CREDIT_CARD.getCode())) {
										totalSaleCCAmount += Double
												.valueOf(dbobj.get(FieldType.AMOUNT.toString()).toString());
										totalSaleCCcount++;
									} else if (dbobj.getString(FieldType.PAYMENT_TYPE.toString())
											.equalsIgnoreCase(PaymentTypeUI.DEBIT_CARD.getCode())) {
										totalSaleDCAmount += Double
												.valueOf(dbobj.get(FieldType.AMOUNT.toString()).toString());
										totalSaleDCcount++;
									} else if (dbobj.getString(FieldType.PAYMENT_TYPE.toString())
											.equalsIgnoreCase(PaymentTypeUI.NET_BANKING.getCode())) {
										totalSaleNBAmount += Double
												.valueOf(dbobj.get(FieldType.AMOUNT.toString()).toString());
										totalSaleNBcount++;
									} else if (dbobj.getString(FieldType.PAYMENT_TYPE.toString())
											.equalsIgnoreCase(PaymentTypeUI.WALLET.getCode())) {
										totalSalewalletAmount += Double
												.valueOf(dbobj.get(FieldType.AMOUNT.toString()).toString());
										totalSalewalletcount++;
									} else if (dbobj.getString(FieldType.PAYMENT_TYPE.toString())
											.equalsIgnoreCase(PaymentTypeUI.UPI.getCode())) {
										totalSaleUPIAmount += Double
												.valueOf(dbobj.get(FieldType.AMOUNT.toString()).toString());
										totalSaleUPIcount++;
									}

									totalSaleAmount += Double
											.valueOf(dbobj.get(FieldType.AMOUNT.toString()).toString());
									totalSaleCount++;
								} else if (dbobj.getString(FieldType.TXNTYPE.toString())
										.equalsIgnoreCase(TransactionType.REFUNDRECO.getName())) {
									if (dbobj.getString(FieldType.PAYMENT_TYPE.toString())
											.equalsIgnoreCase(PaymentTypeUI.CREDIT_CARD.getCode())) {
										totalRfCCAmount += Double
												.valueOf(dbobj.get(FieldType.AMOUNT.toString()).toString());
										totalRfCCcount++;
									} else if (dbobj.getString(FieldType.PAYMENT_TYPE.toString())
											.equalsIgnoreCase(PaymentTypeUI.DEBIT_CARD.getCode())) {
										totalRfDCAmount += Double
												.valueOf(dbobj.get(FieldType.AMOUNT.toString()).toString());
										totalRfDCcount++;
									} else if (dbobj.getString(FieldType.PAYMENT_TYPE.toString())
											.equalsIgnoreCase(PaymentTypeUI.NET_BANKING.getCode())) {
										totalRfNBAmount += Double
												.valueOf(dbobj.get(FieldType.AMOUNT.toString()).toString());
										totalRfNBcount++;
									} else if (dbobj.getString(FieldType.PAYMENT_TYPE.toString())
											.equalsIgnoreCase(PaymentTypeUI.WALLET.getCode())) {
										totalRfwalletAmount += Double
												.valueOf(dbobj.get(FieldType.AMOUNT.toString()).toString());
										totalRfwalletcount++;
									} else if (dbobj.getString(FieldType.PAYMENT_TYPE.toString())
											.equalsIgnoreCase(PaymentTypeUI.UPI.getCode())) {
										totalRfUPIAmount += Double
												.valueOf(dbobj.get(FieldType.AMOUNT.toString()).toString());
										totalRfUPIcount++;
									}

									totalRfAmount += Double.valueOf(dbobj.get(FieldType.AMOUNT.toString()).toString());
									totalRfCount++;
								}
								totalSettleAmount += Double.valueOf(dbobj.get(FieldType.AMOUNT.toString()).toString());
								totalSettleCount++;
							} else {
								if (dbobj.getString(FieldType.TXNTYPE.toString())
										.equalsIgnoreCase(TransactionType.RECO.getName())) {
									if (dbobj.getString(FieldType.PAYMENT_TYPE.toString())
											.equalsIgnoreCase(PaymentTypeUI.CREDIT_CARD.getCode())) {
										totalSaleCCcount++;
									} else if (dbobj.getString(FieldType.PAYMENT_TYPE.toString())
											.equalsIgnoreCase(PaymentTypeUI.DEBIT_CARD.getCode())) {
										totalSaleDCcount++;
									} else if (dbobj.getString(FieldType.PAYMENT_TYPE.toString())
											.equalsIgnoreCase(PaymentTypeUI.NET_BANKING.getCode())) {
										totalSaleNBcount++;
									} else if (dbobj.getString(FieldType.PAYMENT_TYPE.toString())
											.equalsIgnoreCase(PaymentTypeUI.WALLET.getCode())) {
										totalSalewalletcount++;
									} else if (dbobj.getString(FieldType.PAYMENT_TYPE.toString())
											.equalsIgnoreCase(PaymentTypeUI.UPI.getCode())) {
										totalSaleUPIcount++;
									}
									totalSaleCount++;
								} else if (dbobj.getString(FieldType.TXNTYPE.toString())
										.equalsIgnoreCase(TransactionType.REFUNDRECO.getName())) {
									if (dbobj.getString(FieldType.PAYMENT_TYPE.toString())
											.equalsIgnoreCase(PaymentTypeUI.CREDIT_CARD.getCode())) {
										totalRfCCcount++;
									} else if (dbobj.getString(FieldType.PAYMENT_TYPE.toString())
											.equalsIgnoreCase(PaymentTypeUI.DEBIT_CARD.getCode())) {
										totalRfDCcount++;
									} else if (dbobj.getString(FieldType.PAYMENT_TYPE.toString())
											.equalsIgnoreCase(PaymentTypeUI.NET_BANKING.getCode())) {
										totalRfNBcount++;
									} else if (dbobj.getString(FieldType.PAYMENT_TYPE.toString())
											.equalsIgnoreCase(PaymentTypeUI.WALLET.getCode())) {
										totalRfwalletcount++;
									} else if (dbobj.getString(FieldType.PAYMENT_TYPE.toString())
											.equalsIgnoreCase(PaymentTypeUI.UPI.getCode())) {
										totalRfUPIcount++;
									}
									totalRfCount++;
								}
								totalSettleCount++;
							}
						}
					} catch (Exception e) {
						totalSaleCount++;
					}
				}
			}

			if (initiative) {
				finalMap.put("totalSaleSuccAmount", String.format("%.2f", totalSaleSuccAmount));
				finalMap.put("totalSaleSuccCount", String.format("%d", totalSaleSuccCount));
				finalMap.put("totalSalefailAmount", String.format("%.2f", totalSalefailAmount));
				finalMap.put("totalSalefailCount", String.format("%d", totalSalefailCount));
				finalMap.put("totalSalePendingAmount", String.format("%.2f", totalSalePendingAmount));
				finalMap.put("totalSalePendingCount", String.format("%d", totalSalePendingCount));
				finalMap.put("totalSaleTOAmount", String.format("%.2f", totalSaleTOAmount));
				finalMap.put("totalSaleTOCount", String.format("%d", totalSaleTOCount));
				finalMap.put("totalSaleCancelledAmount", String.format("%.2f", totalSaleCancelledAmount));
				finalMap.put("totalSaleCancelledCount", String.format("%d", totalSaleCancelledCount));
				finalMap.put("totalSaleInvalidAmount", String.format("%.2f", totalSaleInvalidAmount));
				finalMap.put("totalSaleInvalidCount", String.format("%d", totalSaleInvalidCount));
				finalMap.put("totalRefundSuccAmount", String.format("%.2f", totalRefundSuccAmount));
				finalMap.put("totalRefundSuccCount", String.format("%d", totalRefundSuccCount));
				finalMap.put("totalRefundFailAmount", String.format("%.2f", totalRefundFailAmount));
				finalMap.put("totalRefundFailCount", String.format("%d", totalRefundFailCount));
				finalMap.put("totalTxnAmount", String.format("%.2f", totalTxnAmount));
				finalMap.put("totaltxns", String.format("%d", totaltxns));
			} else {
				finalMap.put("totalSettleAmount", String.format("%.2f", totalSettleAmount));
				finalMap.put("totalSettleCount", String.format("%d", totalSettleCount));
				finalMap.put("totalSaleCCAmount", String.format("%.2f", totalSaleCCAmount));
				finalMap.put("totalSaleCCcount", String.format("%d", totalSaleCCcount));
				finalMap.put("totalSaleDCAmount", String.format("%.2f", totalSaleDCAmount));
				finalMap.put("totalSaleDCcount", String.format("%d", totalSaleDCcount));
				finalMap.put("totalSaleNBAmount", String.format("%.2f", totalSaleNBAmount));
				finalMap.put("totalSaleNBcount", String.format("%d", totalSaleNBcount));
				finalMap.put("totalSalewalletAmount", String.format("%.2f", totalSalewalletAmount));
				finalMap.put("totalSalewalletcount", String.format("%d", totalSalewalletcount));
				finalMap.put("totalSaleUPIAmount", String.format("%.2f", totalSaleUPIAmount));
				finalMap.put("totalSaleUPIcount", String.format("%d", totalSaleUPIcount));
				finalMap.put("totalSaleAmount", String.format("%.2f", totalSaleAmount));
				finalMap.put("totalSaleCount", String.format("%d", totalSaleCount));
				finalMap.put("totalRfCCAmount", String.format("%.2f", totalRfCCAmount));
				finalMap.put("totalRfCCcount", String.format("%d", totalRfCCcount));
				finalMap.put("totalRfDCAmount", String.format("%.2f", totalRfDCAmount));
				finalMap.put("totalRfDCcount", String.format("%d", totalRfDCcount));
				finalMap.put("totalRfNBAmount", String.format("%.2f", totalRfNBAmount));
				finalMap.put("totalRfNBcount", String.format("%d", totalRfNBcount));
				finalMap.put("totalRfwalletAmount", String.format("%.2f", totalRfwalletAmount));
				finalMap.put("totalRfwalletcount", String.format("%d", totalRfwalletcount));
				finalMap.put("totalRfUPIAmount", String.format("%.2f", totalRfUPIAmount));
				finalMap.put("totalRfUPIcount", String.format("%d", totalRfUPIcount));
				finalMap.put("totalRfAmount", String.format("%.2f", totalRfAmount));
				finalMap.put("totalRfCount", String.format("%d", totalRfCount));
			}

			// Below is the Total Txn Amount
			return finalMap;

		} catch (Exception e) {
			logger.error("Exception occured in TxnReports , totalAmountOFAllTxns n exception = " , e);
			return null;
		}

	}

	public List<TransactionSearchDownloadObject> searchTransactionForDownload(String merchantPayId, String paymentType,
			String status, String currency, String transactionType, String fromDate, String toDate, User user,
			String paymentsRegion, String acquirer) {
		logger.info("Inside TxnReports , searchPayment");
		Map<String, User> userMap = new HashMap<String, User>();
		boolean isParameterised = false;
		List<TransactionSearchDownloadObject> transactionList = new ArrayList<TransactionSearchDownloadObject>();
		try {

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

			/*
			 * if (!merchantPayId.equalsIgnoreCase("ALL")) { paramConditionLst.add(new
			 * BasicDBObject(FieldType.PAY_ID.getName(), merchantPayId));
			 * 
			 * isParameterised = true; }
			 */

			if (merchantPayId.equalsIgnoreCase("ALL")) {
				logger.info("TxnReport, MerchantId : " + merchantPayId + ", Segment : " + user.getSegment());
				if (!user.getSegment().equalsIgnoreCase("Default")) {
					List<String> payIdLst = userdao.getActiveMerchantList(user.getSegment(), user.getRole().getId()).stream().map(Merchants::getPayId).collect(Collectors.toList());
					logger.info("Get PayId List : " + payIdLst);
					if (payIdLst.size() > 0) {
						paramConditionLst
								.add(new BasicDBObject(FieldType.PAY_ID.getName(), new BasicDBObject("$in", payIdLst)));
					}
				}

			} else {
				isParameterised = true;
				paramConditionLst.add(new BasicDBObject(FieldType.PAY_ID.getName(), merchantPayId));
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

					acquirerConditionLst.add(new BasicDBObject(FieldType.ACQUIRER_TYPE.getName(), acq.trim()));
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
				TransactionSearchDownloadObject transReport = new TransactionSearchDownloadObject();
				transReport.setTransactionId(dbobj.getString(FieldType.TXN_ID.toString()));
				transReport.setPgRefNum(dbobj.getString(FieldType.PG_REF_NUM.toString()));
				transReport.setPayId(dbobj.getString(FieldType.PAY_ID.toString()));
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

				if (user1 == null) {
					transReport.setMerchants(CrmFieldConstants.NA.getValue());
				} else {
					transReport.setMerchants(user1.getBusinessName());
				}

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
				if ((user.getUserType().equals(UserType.ADMIN) || user.getUserType().equals(UserType.SUBADMIN))
						&& null != dbobj.getString(FieldType.ACQUIRER_TYPE.toString())) {
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

				if (dbobj.getString(FieldType.REFUND_ORDER_ID.toString()) != null) {
					transReport.setRefundOrderId(dbobj.getString(FieldType.REFUND_ORDER_ID.toString()));
				} else {
					transReport.setRefundOrderId(CrmFieldConstants.NA.getValue());
				}

				if (dbobj.getString(FieldType.REFUND_FLAG.toString()) != null) {
					transReport.setRefundFlag(dbobj.getString(FieldType.REFUND_FLAG.toString()));
				} else {
					transReport.setRefundFlag(CrmFieldConstants.NA.getValue());
				}

				if (dbobj.getString(FieldType.MOP_TYPE.toString()) != null) {
					transReport.setMopType(MopType.getmop(dbobj.getString(FieldType.MOP_TYPE.toString())).getName());
				} else {
					transReport.setMopType(CrmFieldConstants.NA.getValue());
				}

				transactionList.add(transReport);

			}
			logger.info("transactionList created and size = " + transactionList.size());
			cursor.close();
			logger.info("Inside TxnReports , searchPayment , transactionListSize = " + transactionList.size());
			Comparator<TransactionSearchDownloadObject> comp = (TransactionSearchDownloadObject a,
					TransactionSearchDownloadObject b) -> {

				if (a.getDateFrom().compareTo(b.getDateFrom()) > 0) {
					return -1;
				} else if (a.getDateFrom().compareTo(b.getDateFrom()) < 0) {
					return 1;
				} else {
					return 0;
				}
			};
			Collections.sort(transactionList, comp);
			logger.info("transactionList created and Sorted");
			return transactionList;
		} catch (Exception e) {
			logger.error("Exception occured in TxnReports , searchPayment , Exception = " , e);
			return transactionList;
		}
	}

	public List<TransactionSearchDownloadObject> searchTransactionForDownload(String merchantPayId, String paymentType,
			String Userstatus, String currency, String transactionType, String fromDate, String toDate, User user,
			String paymentsRegion, String acquirer, String customerEmail, String customerPhone, String mopType,
			String pgRefNum, String orderId) {
		logger.info("Inside TxnReports , searchPayment");
		Map<String, User> userMap = new HashMap<String, User>();
		boolean isParameterised = false;
		List<TransactionSearchDownloadObject> transactionList = new ArrayList<TransactionSearchDownloadObject>();
		try {
			HashSet<String> orderIdSet = new HashSet<String>();

			if (Userstatus.contains("Pending")) {

				DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				String dateToday = dateFormat.format(new Date());

				DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH);
				Date todayDate = format.parse(dateToday);
				Date providedDate = format.parse(toDate);

				if (providedDate.compareTo(todayDate) < 0) {

				} else {
					orderIdSet = searchUniqueOrderId(pgRefNum, orderId, customerEmail, customerPhone, paymentType,
							Userstatus, currency, transactionType, mopType, acquirer, merchantPayId, fromDate, toDate,
							user);
				}

			} else {
				orderIdSet = searchUniqueOrderId(pgRefNum, orderId, customerEmail, customerPhone, paymentType,
						Userstatus, currency, transactionType, mopType, acquirer, merchantPayId, fromDate, toDate,
						user);
			}

			List<String> txnList = new ArrayList<String>(orderIdSet);

			if (!txnList.isEmpty()) {

				for (int i = 0; i < txnList.size(); i++) {

					MongoDatabase dbIns = mongoInstance.getDB();
					MongoCollection<Document> coll = dbIns.getCollection(
							PropertiesManager.propertiesMap.get(prefix + Constants.COLLECTION_NAME.getValue()));

					String txnUnit = txnList.get(i);
					String txnUnitArr[] = txnUnit.split(":");

					String actualorderId = txnUnitArr[0];
					String txnType = txnUnitArr[1];

					List<BasicDBObject> txnTypList = new ArrayList<BasicDBObject>();
					BasicDBObject ordquery = new BasicDBObject("ORDER_ID", actualorderId);

					if (txnType.equalsIgnoreCase(TransactionType.SALE.getName())) {

						txnTypList.add(new BasicDBObject("TXNTYPE", TransactionType.SALE.getName()));
						txnTypList.add(new BasicDBObject("TXNTYPE", TransactionType.ENROLL.getName()));
						txnTypList.add(new BasicDBObject("TXNTYPE", TransactionType.RECO.getName()));
						txnTypList.add(new BasicDBObject("TXNTYPE", TransactionType.NEWORDER.getName()));
					} else {
						txnTypList.add(new BasicDBObject("TXNTYPE", TransactionType.REFUND.getName()));
						txnTypList.add(new BasicDBObject("TXNTYPE", TransactionType.REFUNDRECO.getName()));
					}

					BasicDBObject txnTypQuery = new BasicDBObject("$or", txnTypList);

					List<BasicDBObject> queryList = new ArrayList<BasicDBObject>();

					// For Cancelled , Failed and Timeout status , include status also in the query
					if (Userstatus.contains("Cancelled") || Userstatus.contains("Failed")
							|| Userstatus.contains("Timeout") || Userstatus.contains("Invalid")
							|| Userstatus.contains("Approved") || Userstatus.contains("Captured")) {

						List<BasicDBObject> transactionStatusConditionLst = new ArrayList<BasicDBObject>();
						BasicDBObject transactionStatusQuery = new BasicDBObject();

						List<String> txnStatusList = Arrays.asList(Userstatus.split(","));
						for (String txnStatus : txnStatusList) {
							transactionStatusConditionLst
									.add(new BasicDBObject(FieldType.STATUS.getName(), txnStatus.trim()));
						}
						transactionStatusQuery.append("$or", transactionStatusConditionLst);
						queryList.add(transactionStatusQuery);
					}

					queryList.add(txnTypQuery);
					queryList.add(ordquery);

					BasicDBObject txnFinalquery = new BasicDBObject("$and", queryList);
					BasicDBObject txnMatch = new BasicDBObject("$match", txnFinalquery);
					BasicDBObject txnSort = new BasicDBObject("$sort", new BasicDBObject("CREATE_DATE", -1));
					BasicDBObject txnLimit = new BasicDBObject("$limit", 1);

					List<BasicDBObject> pipeline = Arrays.asList(txnMatch, txnSort, txnLimit);
					AggregateIterable<Document> output = coll.aggregate(pipeline);

					output.allowDiskUse(true);
					MongoCursor<Document> cursor = output.iterator();

					if (!cursor.hasNext()) {
						continue;
					}

					Document dbobj = cursor.next();
					TransactionSearchDownloadObject transReport = new TransactionSearchDownloadObject();
					transReport.setTransactionId(dbobj.getString(FieldType.TXN_ID.toString()));
					transReport.setPgRefNum(dbobj.getString(FieldType.PG_REF_NUM.toString()));
					transReport.setPayId(dbobj.getString(FieldType.PAY_ID.toString()));
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

					if (user1 == null) {
						transReport.setMerchants(CrmFieldConstants.NA.getValue());
					} else {
						transReport.setMerchants(user1.getBusinessName());
					}

					if (null != dbobj.getString(FieldType.ORIG_TXNTYPE.toString())) {
						transReport.setTxnType(dbobj.getString(FieldType.ORIG_TXNTYPE.toString()));
					} else {

						// If ORIG_TXN_TYPE is not available incase of a timeout , set TXNTYPE instead
						// of ORIG_TXN_TYPE
						if (dbobj.getString(FieldType.STATUS.toString())
								.equalsIgnoreCase(StatusType.TIMEOUT.getName())) {
							transReport.setTxnType(dbobj.getString(FieldType.TXNTYPE.toString()));
						} else {
							transReport.setTxnType(CrmFieldConstants.NA.getValue());
						}

					}
					if ((user.getUserType().equals(UserType.ADMIN) || user.getUserType().equals(UserType.SUBADMIN))
							&& null != dbobj.getString(FieldType.ACQUIRER_TYPE.toString())) {
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

					if (dbobj.getString(FieldType.STATUS.toString())
							.equalsIgnoreCase(StatusType.SETTLED_SETTLE.getName())) {
						String pgRefnum = dbobj.getString(FieldType.PG_REF_NUM.toString());

						List<BasicDBObject> settledConditionLst = new ArrayList<BasicDBObject>();
						settledConditionLst.add(new BasicDBObject(FieldType.PG_REF_NUM.getName(), pgRefnum));
						settledConditionLst.add(new BasicDBObject(FieldType.STATUS.getName(),
								StatusType.CAPTURED.getName().toString()));
						BasicDBObject finalQuery = new BasicDBObject("$and", settledConditionLst);

						MongoCollection<Document> coll2 = dbIns.getCollection(
								PropertiesManager.propertiesMap.get(prefix + Constants.COLLECTION_NAME.getValue()));
						BasicDBObject match2 = new BasicDBObject("$match", finalQuery);
						BasicDBObject projectElement = new BasicDBObject();
						projectElement.put(FieldType.CREATE_DATE.toString(), 1);
						BasicDBObject project2 = new BasicDBObject("$project", projectElement);
						List<BasicDBObject> pgRefPipeline = Arrays.asList(match2, project2);
						AggregateIterable<Document> pgRefTotal1 = coll.aggregate(pgRefPipeline);
						pgRefTotal1.allowDiskUse(true);
						MongoCursor<Document> pgRefTotalCursor = (MongoCursor<Document>) pgRefTotal1.iterator();

						while (pgRefTotalCursor.hasNext()) {
							transReport.setDateFrom(pgRefTotalCursor.next().get("CREATE_DATE").toString());
							break;
						}
						pgRefTotalCursor.close();

					} else {
						transReport.setDateFrom(dbobj.getString(FieldType.CREATE_DATE.getName()));
					}

					transReport.setAmount(dbobj.getString(FieldType.AMOUNT.toString()));
					transReport.setOrderId(dbobj.getString(FieldType.ORDER_ID.toString()));
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

					if (dbobj.getString(FieldType.REFUND_ORDER_ID.toString()) != null) {
						transReport.setRefundOrderId(dbobj.getString(FieldType.REFUND_ORDER_ID.toString()));
					} else {
						transReport.setRefundOrderId(CrmFieldConstants.NA.getValue());
					}

					if (dbobj.getString(FieldType.REFUND_FLAG.toString()) != null) {
						transReport.setRefundFlag(dbobj.getString(FieldType.REFUND_FLAG.toString()));
					} else {
						transReport.setRefundFlag(CrmFieldConstants.NA.getValue());
					}

					if (dbobj.getString(FieldType.MOP_TYPE.toString()) != null) {
						transReport
								.setMopType(MopType.getmop(dbobj.getString(FieldType.MOP_TYPE.toString())).getName());
					} else {
						transReport.setMopType(CrmFieldConstants.NA.getValue());
					}

					transactionList.add(transReport);

				}

			}

			logger.info("transactionList created and size = " + transactionList.size());

			logger.info("Inside TxnReports , searchPayment , transactionListSize = " + transactionList.size());
			Comparator<TransactionSearchDownloadObject> comp = (TransactionSearchDownloadObject a,
					TransactionSearchDownloadObject b) -> {

				if (a.getDateFrom().compareTo(b.getDateFrom()) > 0) {
					return -1;
				} else if (a.getDateFrom().compareTo(b.getDateFrom()) < 0) {
					return 1;
				} else {
					return 0;
				}
			};
			Collections.sort(transactionList, comp);
			logger.info("transactionList created and Sorted");
			return transactionList;
		} catch (Exception e) {
			logger.error("Exception occured in TxnReports , searchPayment , Exception = " , e);
			return transactionList;
		}
	}

	@SuppressWarnings("unchecked")
	public List<PaymentSearchDownloadObject> searchPaymentForDownload(String merchantPayId, String paymentType,
			String status, String currency, String transactionType, String fromDate, String toDate, User user,
			String paymentsRegion, String acquirer,String channel) {

		logger.info("Inside TxnReports , searchPayment");
		Map<String, User> userMap = new HashMap<String, User>();
		boolean isParameterised = false;
		List<PaymentSearchDownloadObject> transactionList = new ArrayList<PaymentSearchDownloadObject>();
		try {

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

			/*
			 * if (!merchantPayId.equalsIgnoreCase("ALL")) { paramConditionLst.add(new
			 * BasicDBObject(FieldType.PAY_ID.getName(), merchantPayId));
			 * 
			 * isParameterised = true; }
			 */

			if (merchantPayId.equalsIgnoreCase("ALL")) {
				logger.info("TxnReport, MerchantId : " + merchantPayId + ", Segment : " + user.getSegment());
				if (!user.getSegment().equalsIgnoreCase("Default")) {
					List<String> payIdLst = userdao.getActiveMerchantList(user.getSegment(), user.getRole().getId()).stream().map(Merchants::getPayId).collect(Collectors.toList());
					logger.info("Get PayId List : " + payIdLst);
					if (payIdLst.size() > 0) {
						paramConditionLst
								.add(new BasicDBObject(FieldType.PAY_ID.getName(), new BasicDBObject("$in", payIdLst)));
					}
				}

			} else {
				isParameterised = true;
				paramConditionLst.add(new BasicDBObject(FieldType.PAY_ID.getName(), merchantPayId));
			}

			if (user.getUserType().equals(UserType.RESELLER)) {
				paramConditionLst.add(new BasicDBObject(FieldType.RESELLER_ID.getName(), user.getResellerId()));
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
			if (!channel.equalsIgnoreCase("ALL")) {
				
				paramConditionLst.add(new BasicDBObject(FieldType.CHANNEL.getName(), channel));
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

					acquirerConditionLst.add(new BasicDBObject(FieldType.ACQUIRER_TYPE.getName(), acq.trim()));
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

			Document fGroup = new Document("_id",
					new Document("PG_REF_NUM", "$PG_REF_NUM").append("STATUS", "$STATUS"));
			BasicDBObject fGroupObject = new BasicDBObject(fGroup);
			BasicDBObject sGroup = new BasicDBObject("$last", "$$ROOT");
			BasicDBObject groupQuery = new BasicDBObject("$group", fGroupObject.append("contentList", sGroup));
			BasicDBObject replacedRoot = new BasicDBObject("newRoot", "$contentList");
			BasicDBObject replaceRootQuery = new BasicDBObject("$replaceRoot", replacedRoot);
			List<Bson> pipeline = Arrays.asList(match, groupQuery, replaceRootQuery, group, sort);

			AggregateIterable<Document> output = coll.aggregate(pipeline);
			output.allowDiskUse(true);
			MongoCursor<Document> cursor = output.iterator();
			// Loading the Company profile for calculating the cgst,sgst,igst

			CompanyProfile cp = new CompanyProfile();
			cp = companyProfileDao
					.getCompanyProfileByTenantId(PropertiesManager.propertiesMap.get(companyProfileTenantId));

			while (cursor.hasNext()) {
				Document mydata = cursor.next();
				List<Document> courses = (List<Document>) mydata.get("entries");
				Document dbobj = courses.get(0);
				PaymentSearchDownloadObject transReport = new PaymentSearchDownloadObject();
				transReport.setTransactionId(dbobj.getString(FieldType.TXN_ID.toString()));
				transReport.setPgRefNum(dbobj.getString(FieldType.PG_REF_NUM.toString()));
				transReport.setTransactionRegion(dbobj.getString(FieldType.PAYMENTS_REGION.toString()));
				transReport.setMerchants(dbobj.getString(CrmFieldType.BUSINESS_NAME.getName()));
				transReport.setPostSettledFlag(dbobj.getString(FieldType.POST_SETTLED_FLAG.getName()));

				// Added by Deep
				if (StringUtils.isNotBlank(dbobj.getString(FieldType.UDF4.getName()))) {
					transReport.setUdf4(dbobj.getString(FieldType.UDF4.getName()));
				} else {
					transReport.setUdf4(CrmFieldConstants.NA.getValue());
				}
				if (StringUtils.isNotBlank(dbobj.getString(FieldType.UDF5.getName()))) {
					transReport.setUdf5(dbobj.getString(FieldType.UDF5.getName()));
				} else {
					transReport.setUdf5(CrmFieldConstants.NA.getValue());
				}
				if (StringUtils.isNotBlank(dbobj.getString(FieldType.UDF6.getName()))) {
					transReport.setUdf6(dbobj.getString(FieldType.UDF6.getName()));
				} else {
					transReport.setUdf6(CrmFieldConstants.NA.getValue());
				}
				// end by deep

				String payid = (String) dbobj.get(FieldType.PAY_ID.getName());
				/* added by vijaylakshmi */
				String INTERNAL_CUST_IP = dbobj.getString(FieldType.INTERNAL_CUST_IP.getName());
				if (StringUtils.isBlank(INTERNAL_CUST_IP)) {
					logger.info("Fetching ipaddress from transaction table based on OID & pending status");
					INTERNAL_CUST_IP = getPreviousForOID(dbobj.getString(FieldType.OID.getName()));

					if (StringUtils.isBlank(INTERNAL_CUST_IP)) {
						logger.info(
								"Fetching ipaddress from transaction table based on PgRefNum & status -> Send to Bank ");
						INTERNAL_CUST_IP = getPreviousForPgRefNum(dbobj.getString(FieldType.PG_REF_NUM.toString()));
					}
				}
				logger.info("CUST_PHONE++++++++++++++++++++++++++++++" + dbobj.getString("CUST_PHONE"));
				logger.info("INTERNAL_CUST_IP++++++++++++++++++++++++++++++" + INTERNAL_CUST_IP);

				// Added by Deep
				if (StringUtils.isNotBlank(dbobj.getString(FieldType.UDF4.getName()))) {
					transReport.setUdf4(dbobj.getString(FieldType.UDF4.getName()));
				} else {
					transReport.setUdf4(CrmFieldConstants.NA.getValue());
				}
				if (StringUtils.isNotBlank(dbobj.getString(FieldType.UDF5.getName()))) {
					transReport.setUdf5(dbobj.getString(FieldType.UDF5.getName()));
				} else {
					transReport.setUdf5(CrmFieldConstants.NA.getValue());
				}
				if (StringUtils.isNotBlank(dbobj.getString(FieldType.UDF6.getName()))) {
					transReport.setUdf6(dbobj.getString(FieldType.UDF6.getName()));
				} else {
					transReport.setUdf6(CrmFieldConstants.NA.getValue());
				}
				// end by deep
				if (null != dbobj.getString(FieldType.UTR_NO.toString())) {
					transReport.setUtrNo(dbobj.getString(FieldType.UTR_NO.toString()));
				} else {
					transReport.setUtrNo(CrmFieldConstants.NA.getValue());
				}

				transReport.setCustEmail(dbobj.getString(FieldType.CUST_EMAIL.toString()));
				transReport.setCustMobileNo(dbobj.getString("CUST_PHONE"));
				transReport.setIpAddress(INTERNAL_CUST_IP);

				// transReport.setIpAddress(dbobj.getString("INTERNAL_CUST_IP"));
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
				} else {

					// If ORIG_TXN_TYPE is not available incase of a timeout , set TXNTYPE instead
					// of ORIG_TXN_TYPE
					if (dbobj.getString(FieldType.STATUS.toString()).equalsIgnoreCase(StatusType.TIMEOUT.getName())) {
						transReport.setTxnType(dbobj.getString(FieldType.TXNTYPE.toString()));
					} else {
						transReport.setTxnType(CrmFieldConstants.NA.getValue());
					}

				}

				if (null != dbobj.getString(FieldType.ACQUIRER_TYPE.toString())) {
					transReport.setAcquirerType(dbobj.getString(FieldType.ACQUIRER_TYPE.toString()));
				} else {
					transReport.setAcquirerType(CrmFieldConstants.NA.getValue());
				}
				if (status.equalsIgnoreCase(StatusType.SETTLED_SETTLE.toString())) {
					if (null != dbobj.getString(FieldType.PG_DATE_TIME.toString())) {
						transReport.setCaptureDateTime(dbobj.getString(FieldType.PG_DATE_TIME.toString()));
					} else {
						transReport.setCaptureDateTime(CrmFieldConstants.NA.getValue());
					}

					Double totalTdrSc = 0.00;
					Double totalGst = 0.00;
					Double netAmount = 0.00;
					Double cGst = 0.00;
					Double sGst = 0.00;
					Double iGst = 0.00;
					if (null != dbobj.getString(FieldType.PG_TDR_SC.toString())) {
						totalTdrSc += Double.valueOf(dbobj.getString(FieldType.PG_TDR_SC.toString()));
					}
					if (null != dbobj.getString(FieldType.ACQUIRER_TDR_SC.toString())) {
						totalTdrSc += Double.valueOf(dbobj.getString(FieldType.ACQUIRER_TDR_SC.toString()));
					}
					transReport.setTdrSurcharge(String.valueOf( totalTdrSc));

					if (null != dbobj.getString(FieldType.PG_GST.toString())) {
						totalGst += Double.valueOf(dbobj.getString(FieldType.PG_GST.toString()));
					}
					if (null != dbobj.getString(FieldType.ACQUIRER_GST.toString())) {
						totalGst += Double.valueOf(dbobj.getString(FieldType.ACQUIRER_GST.toString()));
					}
					// here need to implement the intra state supply related logic comparing both
					// merchant and buyer states
					if (totalGst != 0.00 && null != user1.getState() && null != cp && null != cp.getState()) {
						if (user1.getState().equalsIgnoreCase(cp.getState())) {
							sGst = totalGst / 2.00;
							cGst = totalGst / 2.00;
						} else {
							iGst = totalGst;
						}

					}
					transReport.setSgst(String.format("%.2f", sGst));
					transReport.setCgst(String.format("%.2f", cGst));
					transReport.setIgst(String.format("%.2f", iGst));
					transReport.setTotalGst(String.format("%.2f", totalGst));
					if (null != dbobj.getString(FieldType.TOTAL_AMOUNT.toString())) {
						netAmount = Double.valueOf(dbobj.getString(FieldType.TOTAL_AMOUNT.toString())) - totalTdrSc
								- totalGst;
					}
					transReport.setNetAmount(String.valueOf( netAmount));

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

				if (transactionType.equalsIgnoreCase(TransactionType.REFUND.toString())
						&& status.equalsIgnoreCase(StatusType.CAPTURED.toString())) {
					if (null != dbobj.getString(FieldType.REFUND_ORDER_ID.toString())) {
						transReport.setRefundOrderId(dbobj.getString(FieldType.REFUND_ORDER_ID.toString()));
					} else {
						transReport.setRefundOrderId(CrmFieldConstants.NA.getValue());
					}
				}

				if (dbobj.getString(FieldType.TOTAL_AMOUNT.toString()) != null) {
					transReport.setTotalAmount(dbobj.getString(FieldType.TOTAL_AMOUNT.toString()));
				} else {
					transReport.setTotalAmount("");
				}
				if (dbobj.getString(FieldType.RRN.toString()) != null) {
					transReport.setRrn(dbobj.getString(FieldType.RRN.toString()));
				} else {
					transReport.setRrn(CrmFieldConstants.NA.getValue());
				}
				if (dbobj.getString(FieldType.ARN.toString()) != null) {
					transReport.setArn(dbobj.getString(FieldType.ARN.toString()));
				} else {
					transReport.setArn(CrmFieldConstants.NA.getValue());
				}
				if (dbobj.getString(FieldType.ACQ_ID.toString()) != null) {
					transReport.setAcqId(dbobj.getString(FieldType.ACQ_ID.toString()));
				} else {
					transReport.setAcqId(CrmFieldConstants.NA.getValue());
				}
				if (dbobj.getString(FieldType.MOP_TYPE.toString()) != null) {
					transReport.setMopType(MopType.getmopName(dbobj.getString(FieldType.MOP_TYPE.toString())));
				} else {
					transReport.setMopType(CrmFieldConstants.NA.getValue());
				}
				if (dbobj.getString(FieldType.ACQUIRER_TYPE.toString()) != null) {
					transReport.setAcquirerType(
							AcquirerType.getAcquirerName(dbobj.getString(FieldType.ACQUIRER_TYPE.toString())));
				} else {
					transReport.setAcquirerType(CrmFieldConstants.NA.getValue());
				}

				transactionList.add(transReport);

			}
			logger.info("transactionList created and size = " + transactionList.size());
			cursor.close();
			logger.info("Inside TxnReports , searchPayment , transactionListSize = " + transactionList.size());
			Comparator<PaymentSearchDownloadObject> comp = (PaymentSearchDownloadObject a,
					PaymentSearchDownloadObject b) -> {

				if (a.getDateFrom().compareTo(b.getDateFrom()) > 0) {
					return -1;
				} else if (a.getDateFrom().compareTo(b.getDateFrom()) < 0) {
					return 1;
				} else {
					return 0;
				}
			};
			Collections.sort(transactionList, comp);
			logger.info("transactionList created and Sorted");
			return transactionList;
		} catch (Exception e) {
			logger.error("Exception occured in TxnReports , searchPayment , Exception = " , e);
			return transactionList;
		}
	}

	public String getPreviousForOID(String oid) {

		String internalCustIP = null;
		try {
			logger.info("Inside getPreviousForOID (OID): " + oid);

			List<BasicDBObject> condList = new ArrayList<BasicDBObject>();
			condList.add(new BasicDBObject(FieldType.OID.getName(), oid));
			condList.add(new BasicDBObject(FieldType.TXNTYPE.getName(), TransactionType.NEWORDER.getName()));
			condList.add(new BasicDBObject(FieldType.STATUS.getName(), StatusType.PENDING.getName()));
			BasicDBObject saleQuery = new BasicDBObject("$and", condList);
			MongoDatabase dbIns = mongoInstance.getDB();
			MongoCollection<Document> collection = dbIns
					.getCollection(propertiesManager.propertiesMap.get(prefix + Constants.COLLECTION_NAME.getValue()));
			MongoCursor<Document> cursor = collection.find(saleQuery).iterator();
			try {
				while (cursor.hasNext()) {
					Document doc = cursor.next();
					if (StringUtils.isNotBlank(String.valueOf(doc.get(FieldType.INTERNAL_CUST_IP.getName())))) {
						internalCustIP = String.valueOf(doc.get(FieldType.INTERNAL_CUST_IP.getName()));
						break;
					}
				}
			} finally {
				cursor.close();
			}
			return internalCustIP;
		} catch (Exception exception) {
			String message = "Error while previous based on OID from database";
			logger.error(message, exception);
			return internalCustIP;
		}
	}

	public String getPreviousForPgRefNum(String pgRefNo) {

		String internalCustIP = null;
		try {
			logger.info("Inside getPreviousForPgRefNum (OID): " + pgRefNo);

			List<BasicDBObject> condList = new ArrayList<BasicDBObject>();
			condList.add(new BasicDBObject(FieldType.PG_REF_NUM.getName(), pgRefNo));
			condList.add(new BasicDBObject(FieldType.TXNTYPE.getName(), TransactionType.SALE.getName()));
			condList.add(new BasicDBObject(FieldType.STATUS.getName(), StatusType.SENT_TO_BANK.getName()));
			BasicDBObject saleQuery = new BasicDBObject("$and", condList);
			MongoDatabase dbIns = mongoInstance.getDB();
			MongoCollection<Document> collection = dbIns
					.getCollection(propertiesManager.propertiesMap.get(prefix + Constants.COLLECTION_NAME.getValue()));
			MongoCursor<Document> cursor = collection.find(saleQuery).iterator();
			try {
				while (cursor.hasNext()) {
					Document doc = cursor.next();
					if (StringUtils.isNotBlank(String.valueOf(doc.get(FieldType.INTERNAL_CUST_IP.getName())))) {
						internalCustIP = String.valueOf(doc.get(FieldType.INTERNAL_CUST_IP.getName()));
						break;
					}
				}
			} finally {
				cursor.close();
			}
			return internalCustIP;
		} catch (Exception exception) {
			String message = "Error while previous based on OID from database";
			logger.error(message, exception);
			return internalCustIP;
		}
	}

	public List<SettledTransactionDataObject> txnDataForbatch(String orderId, String pgRefNum, String acqId) {

		// logger.info("Finding batch transactions ");
		List<SettledTransactionDataObject> capturedTransactionDataList = new ArrayList<SettledTransactionDataObject>();

		try {

			List<BasicDBObject> saleConditionList = new ArrayList<BasicDBObject>();

			if (StringUtils.isBlank(orderId) && StringUtils.isBlank(pgRefNum) && StringUtils.isBlank(acqId)) {
				return capturedTransactionDataList;
			}
			if (StringUtils.isNotBlank(orderId) && !orderId.equalsIgnoreCase("NA")) {
				saleConditionList.add(new BasicDBObject(FieldType.ORDER_ID.getName(), orderId));
			}
			if (StringUtils.isNotBlank(pgRefNum) && !pgRefNum.equalsIgnoreCase("NA")) {
				saleConditionList.add(new BasicDBObject(FieldType.PG_REF_NUM.getName(), pgRefNum));
			}
			if (StringUtils.isNotBlank(acqId) && !acqId.equalsIgnoreCase("NA")) {
				saleConditionList.add(new BasicDBObject(FieldType.ACQ_ID.getName(), acqId));
			}

			BasicDBObject saleConditionQuery = new BasicDBObject("$and", saleConditionList);

			MongoDatabase dbIns = mongoInstance.getDB();
			MongoCollection<Document> coll = dbIns
					.getCollection(PropertiesManager.propertiesMap.get(prefix + Constants.COLLECTION_NAME.getValue()));

			BasicDBObject match = new BasicDBObject("$match", saleConditionQuery);

			List<BasicDBObject> pipeline = Arrays.asList(match);
			AggregateIterable<Document> output = coll.aggregate(pipeline);
			output.allowDiskUse(true);
			MongoCursor<Document> cursor = output.iterator();
			int counter = 0;
			while (cursor.hasNext()) {

				counter = counter + 1;
				if (counter == 100) {
					logger.info("Checked 100 records in batch search");
					counter = 0;
				}

				Document dbobj = cursor.next();

				SettledTransactionDataObject transReport = new SettledTransactionDataObject();
				transReport.setTransactionId(dbobj.getString(FieldType.TXN_ID.toString()));
				transReport.setPgRefNum(dbobj.getString(FieldType.PG_REF_NUM.toString()));
				transReport.setPayId(dbobj.getString(FieldType.PAY_ID.toString()));
				transReport.setTransactionRegion(dbobj.getString(FieldType.PAYMENTS_REGION.toString()));
				transReport.setMerchants(dbobj.getString(CrmFieldType.BUSINESS_NAME.getName()));
				transReport.setPostSettledFlag(dbobj.getString(FieldType.POST_SETTLED_FLAG.getName()));
				if (dbobj.getString(FieldType.UDF6.getName()) != null) {
					transReport.setDeltaRefundFlag(dbobj.getString(FieldType.UDF6.getName()));
				} else {
					transReport.setDeltaRefundFlag("");
				}
				String payid = (String) dbobj.get(FieldType.PAY_ID.getName());

				if (null != dbobj.getString(FieldType.ORIG_TXNTYPE.toString())) {
					transReport.setTxnType(dbobj.getString(FieldType.ORIG_TXNTYPE.toString()));
				} else {

					if (dbobj.getString(FieldType.STATUS.toString()).equalsIgnoreCase(StatusType.TIMEOUT.getName())) {
						transReport.setTxnType(dbobj.getString(FieldType.TXNTYPE.toString()));
					} else {
						transReport.setTxnType(CrmFieldConstants.NA.getValue());
					}

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
				transReport.setCreateDate(dbobj.getString(FieldType.CREATE_DATE.getName()));
				transReport.setAmount(Double.valueOf(dbobj.getString(FieldType.AMOUNT.toString())));
				transReport.setOrderId(dbobj.getString(FieldType.ORDER_ID.toString()));
				if (dbobj.getString(FieldType.TOTAL_AMOUNT.toString()) != null) {
					transReport.setTotalAmount(Double.valueOf(dbobj.getString(FieldType.TOTAL_AMOUNT.toString())));
				} else {
					transReport.setTotalAmount(0.0);
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

				if (dbobj.getString(FieldType.ARN.toString()) != null) {
					transReport.setArn(dbobj.getString(FieldType.ARN.toString()));
				} else {
					transReport.setRrn(CrmFieldConstants.NA.getValue());
				}

				if (dbobj.getString(FieldType.REFUND_ORDER_ID.toString()) != null) {
					transReport.setRefundOrderId(dbobj.getString(FieldType.REFUND_ORDER_ID.toString()));
				} else {
					transReport.setRefundOrderId(CrmFieldConstants.NA.getValue());
				}

				if (dbobj.getString(FieldType.REFUND_FLAG.toString()) != null) {
					transReport.setRefundFlag(dbobj.getString(FieldType.REFUND_FLAG.toString()));
				} else {
					transReport.setRefundFlag(CrmFieldConstants.NA.getValue());
				}

				if (dbobj.getString(FieldType.MOP_TYPE.toString()) != null) {
					transReport.setMopType(dbobj.getString(FieldType.MOP_TYPE.toString()));
				} else {
					transReport.setMopType(CrmFieldConstants.NA.getValue());
				}

				if (dbobj.getString(FieldType.INTERNAL_REQUEST_FIELDS.toString()) != null) {
					transReport.setInternalRequestFields(dbobj.getString(FieldType.INTERNAL_REQUEST_FIELDS.toString()));
				} else {
					transReport.setInternalRequestFields(CrmFieldConstants.NA.getValue());
				}

				if (dbobj.getString(FieldType.RESPONSE_MESSAGE.toString()) != null) {
					transReport.setResponseMessage(dbobj.getString(FieldType.RESPONSE_MESSAGE.toString()));
				} else {
					transReport.setResponseMessage(CrmFieldConstants.NA.getValue());
				}

				if (dbobj.getString(FieldType.PG_TXN_MESSAGE.toString()) != null) {
					transReport.setPgResponseMessage(dbobj.getString(FieldType.PG_TXN_MESSAGE.toString()));
				} else {
					transReport.setPgResponseMessage(CrmFieldConstants.NA.getValue());
				}

				capturedTransactionDataList.add(transReport);
			}
			cursor.close();

		} catch (Exception e) {
			logger.error("Exception occured in fetching batch result " , e);
		}
		return capturedTransactionDataList;
	}

	public List<TransactionSearch> searchPayment(String orderId, String getPgRefNum) {
		Map<String, User> userMap = new HashMap<String, User>();

		logger.info("Inside TxnReports , searchPayment");

		boolean isParameterised = false;
		try {
			List<TransactionSearch> transactionList = new ArrayList<TransactionSearch>();

			PropertiesManager propManager = new PropertiesManager();
			List<BasicDBObject> paramConditionLst = new ArrayList<BasicDBObject>();

			BasicDBObject allParamQuery = new BasicDBObject();

			if (((orderId != null && !orderId.isEmpty())) && ((getPgRefNum != null && !getPgRefNum.isEmpty()))) {
				isParameterised = true;
				paramConditionLst.add(new BasicDBObject(FieldType.ORDER_ID.getName(), orderId));
				paramConditionLst.add(new BasicDBObject(FieldType.PG_REF_NUM.getName(), getPgRefNum));
				if (!paramConditionLst.isEmpty()) {
					allParamQuery = new BasicDBObject("$and", paramConditionLst);
				}
			} else if (((orderId != null && !orderId.isEmpty()))
					&& (!(getPgRefNum != null && !getPgRefNum.isEmpty()))) {
				isParameterised = true;
				allParamQuery = new BasicDBObject(FieldType.ORDER_ID.getName(), orderId);
			} else if (!((orderId != null && !orderId.isEmpty()))
					&& ((getPgRefNum != null && !getPgRefNum.isEmpty()))) {
				isParameterised = true;
				allParamQuery = new BasicDBObject(FieldType.PG_REF_NUM.getName(), getPgRefNum);
			}

			logger.info("Inside TxnReports , searchPayment , finalquery = " + allParamQuery);
			MongoDatabase dbIns = mongoInstance.getDB();
			MongoCollection<Document> coll = dbIns
					.getCollection(PropertiesManager.propertiesMap.get(prefix + Constants.COLLECTION_NAME.getValue()));
			BasicDBObject match = new BasicDBObject("$match", allParamQuery);
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
			BasicDBObject sort = new BasicDBObject("$sort", new BasicDBObject("CREATE_DATE", -1));

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
				transReport.setPgRefNum(dbobj.getString(FieldType.PG_REF_NUM.toString()));
				transReport.setPayId(dbobj.getString(FieldType.PAY_ID.toString()));

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

				if ((null != dbobj.getString(FieldType.TXNTYPE.toString())) && (dbobj
						.getString(FieldType.TXNTYPE.toString()).equalsIgnoreCase(TransactionType.REFUND.getName()))) {
					transReport.setTxnType(dbobj.getString(FieldType.TXNTYPE.toString()));
				} else if (null != dbobj.getString(FieldType.ORIG_TXNTYPE.toString())) {
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
				transReport.setPostSettledFlag(dbobj.getString(FieldType.POST_SETTLED_FLAG.getName()));
				transReport.setStatus(dbobj.getString(FieldType.STATUS.toString()));
				transReport.setDateFrom(dbobj.getString(FieldType.CREATE_DATE.getName()));
				transReport.setAmount(dbobj.getString(FieldType.AMOUNT.toString()));
				transReport.setOrderId(dbobj.getString(FieldType.ORDER_ID.toString()));
				if (StringUtils.isNotBlank(dbobj.getString(FieldType.REFUND_ORDER_ID.toString()))) {
					transReport.setRefundOrderId(dbobj.getString(FieldType.REFUND_ORDER_ID.toString()));
				} else {
					transReport.setRefundOrderId(CrmFieldConstants.NA.getValue());
				}
				transReport.setoId(dbobj.getString(FieldType.OID.toString()));
				transReport.setProductDesc(dbobj.getString(FieldType.PRODUCT_DESC.toString()));
				transReport.setTransactionCaptureDate(dbobj.getString(FieldType.PG_DATE_TIME.toString()));
				if (transReport.getTxnType().contains(TransactionType.REFUND.getName())) {
					transReport.setTxnType(TransactionType.REFUND.getName());
				} else {
					transReport.setTxnType(TransactionType.SALE.getName());
				}

				transReport.setCreateDate(dbobj.getString(FieldType.CREATE_DATE.getName()));
				transReport.setCardMask(dbobj.getString(FieldType.CARD_MASK.toString()));
				transReport
						.setInternalCardIssusserBank(dbobj.getString(FieldType.INTERNAL_CARD_ISSUER_BANK.toString()));
				transReport.setInternalCardIssusserCountry(
						dbobj.getString(FieldType.INTERNAL_CARD_ISSUER_COUNTRY.toString()));
				transReport.setRefundableAmount(dbobj.getString(FieldType.REFUNDABLE_AMOUNT.toString()));

				transReport.setApprovedAmount(dbobj.getString(FieldType.AMOUNT.toString()));

				if (dbobj.getString(FieldType.TOTAL_AMOUNT.toString()) != null) {
					transReport.setTotalAmount(dbobj.getString(FieldType.TOTAL_AMOUNT.toString()));
				} else {
					transReport.setTotalAmount("");
				}

				if (null != dbobj.getString(FieldType.CURRENCY_CODE.toString())) {
					transReport.setCurrency(currencyCodeDao.getCurrencyNamebyCode(dbobj.getString(FieldType.CURRENCY_CODE.toString())));
		} else {
					transReport.setCurrency(CrmFieldConstants.NA.getValue());
				}

				// Exclude certain records when loading search payment without any filters

				Comparator<TransactionSearch> comp = (TransactionSearch a, TransactionSearch b) -> {

					if (a.getDateFrom().compareTo(b.getDateFrom()) > 0) {
						return -1;
					} else if (a.getDateFrom().compareTo(b.getDateFrom()) < 0) {
						return 1;
					} else {
						return 0;
					}
				};

				transactionList.add(transReport);
				Collections.sort(transactionList, comp);
			}
			cursor.close();
			logger.info("Inside TxnReports , searchPayment , transactionListSize = " + transactionList.size());
			return transactionList;
		} catch (Exception e) {
			logger.error("Exception occured in TxnReports , searchPayment , Exception = " , e);
			return null;
		}
	}

	public List<TransactionSearchNew> newSearchPayment(String pgRefNum, String orderId, String customerEmail,
			String customerPhone, String paymentType, String aliasStatus, String currency, String transactionType,
			String mopType, String acquirer, String merchantPayId, String fromDate, String toDate, User user, int start,
			int length, String tenantId, String ipAddress, String totalAmount, String rrn,String channelName,String minAmount,String maxAmount,String columnName,String logicalCondition,String searchText, String newDespositor,String columnName1,String logicalCondition1,String searchText1,String columnName2,String logicalCondition2,String searchText2) {

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
					List<String> payIdLst = userdao.getActiveMerchantList(user.getSegment(), user.getRole().getId()).stream().map(Merchants::getPayId).collect(Collectors.toList());
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

				if (totalAmount.equals("0")) {

					logger.info(" totalAmount, @@@@@@@@@@@@@@1111111>>>>>> = " + totalAmount);
					paramConditionLst.add(new BasicDBObject(FieldType.TOTAL_AMOUNT.getName(), null));

				} else {

					if (total.length == 1) {
						totalAmount = totalAmount + ".00";
						logger.info(" totalAmount, @@@@@@@@@@22222222>>>>>> = " + totalAmount);
					}
					paramConditionLst.add(new BasicDBObject(FieldType.TOTAL_AMOUNT.getName(), totalAmount));
					logger.info("Inside txnReports.java @@@@@ totalAmount ======================= = "
							+ totalAmount);
				}

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
			 * "endDateTime========================= = " , endDateTime);
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
			 * logger.info("newSearchPayment++++++++++++++++++++++++++++++" , endTime);
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
			 * , endDateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd hh:mm:ss")));
			 * logger.
			 * info("endDateTime.format(DateTimeFormatter.ofPattern(\"yyyy-MM-dd hh:mm:ss\")))), ==========s========================= = "
			 * + startDateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd hh:mm:ss")));
			 * 
			 * 
			 * }
			 * 
			 */
			
			//Added by Deep Singh code start here
			
			logger.info("channelName::: " + channelName );
			logger.info("minAmount::: " + minAmount );
			logger.info("maxAmount::: " + maxAmount );
			logger.info("columnName::: " + columnName );
			logger.info("logicalCondition::: " + logicalCondition );
			logger.info("searchText::: " + searchText );
			
			
			if (channelName.equalsIgnoreCase("All")) {
//				paramConditionLst.add(new BasicDBObject(FieldType.CHANNEL.getName(),
//						new BasicDBObject("$in", Arrays.asList("Fiat", "Crypto"))));
			} else {
				if(channelName.contains(",")) {
					String [] channels=channelName.split(",");
					ArrayList<String> aa=new ArrayList<>();
					for(String channel:channels) {
						aa.add(channel);
					}
					paramConditionLst.add(new BasicDBObject(FieldType.CHANNEL.getName(), new BasicDBObject("$in",aa)));	
				}else {
					paramConditionLst.add(new BasicDBObject(FieldType.CHANNEL.getName(), new BasicDBObject("$in",Arrays.asList(channelName))));
				}
				
			}

//			if (!minAmount.equalsIgnoreCase("")) {
//				paramConditionLst
//						.add(new BasicDBObject(FieldType.TOTAL_AMOUNT.getName(), new BasicDBObject("$gte", minAmount)));
//			}
//
//			if (!maxAmount.equalsIgnoreCase("")) {
//				paramConditionLst
//						.add(new BasicDBObject(FieldType.TOTAL_AMOUNT.getName(), new BasicDBObject("$lte", maxAmount)));
//			}
			if(StringUtils.isNoneBlank(minAmount)&& StringUtils.isNoneBlank(maxAmount)) {
				paramConditionLst.add(
					    new BasicDBObject("$expr",
					    new BasicDBObject("$and", Arrays.asList(new BasicDBObject("$gte", Arrays.asList(new BasicDBObject("$toDouble", "$TOTAL_AMOUNT"), Double.parseDouble(minAmount))),
					                    new BasicDBObject("$lte", Arrays.asList(new BasicDBObject("$toDouble", "$TOTAL_AMOUNT"), Double.parseDouble(maxAmount)))))));
			}

			if (!columnName.equalsIgnoreCase("") && !logicalCondition.equalsIgnoreCase("")
					&& !searchText.equalsIgnoreCase("")) {
				paramConditionLst.add(new BasicDBObject(columnName, new BasicDBObject(logicalCondition, searchText)));
			}
			
			if (!columnName1.equalsIgnoreCase("") && !logicalCondition1.equalsIgnoreCase("")
					&& !searchText1.equalsIgnoreCase("")) {
				paramConditionLst.add(new BasicDBObject(columnName1, new BasicDBObject(logicalCondition1, searchText1)));
			}
			
			if (!columnName2.equalsIgnoreCase("") && !logicalCondition2.equalsIgnoreCase("")
					&& !searchText2.equalsIgnoreCase("")) {
				paramConditionLst.add(new BasicDBObject(columnName2, new BasicDBObject(logicalCondition2, searchText2)));
			}
			
			//Added by Deep Singh code end here
			
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
					aliasStatusConditionLst.add(new BasicDBObject(FieldType.STATUS.getName(), status.equalsIgnoreCase("Request Accepted")?"Sent to Bank":status.trim()));
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

			List<String>custEmailIdList=new ArrayList<>();
			if (newDespositor.equalsIgnoreCase("true")) {
				
//				List<Document> matchCase= Arrays.asList(new Document("$match", 
//					    new Document("CUST_EMAIL", 
//					    	    new Document("$ne", 
//					    	    new BsonNull()))
//					    	            .append("CREATE_DATE", 
//					    	    new Document("$gte", fromDate)
//					    	                .append("$lte", toDate))), 
//					    	    new Document("$group", 
//					    	    new Document("_id", "$CUST_EMAIL")
//					    	            .append("transaction_count", 
//					    	    new Document("$sum", 1L))), 
//					    	    new Document("$match", 
//					    	    new Document("transaction_count", 
//					    	    new Document("$eq", 1L))), 
//					    	    new Document("$project", 
//					    	    new Document("_id", 0L)
//					    	            .append("CUST_EMAIL", "$_id")));
				
				
//				List<Document> matchCase=Arrays.asList(new Document("$match",
//					    new Document("CREATE_DATE",
//					    new Document("$gte", fromDate)
//					                .append("$lte",toDate))),
//					    new Document("$group",
//					    new Document("_id", "$PAY_ID")
//					            .append("getMin",
//					    new Document("$min", "$CREATE_DATE"))
//					            .append("doc",
//					    new Document("$first", "$$ROOT"))),
//					    new Document("$replaceRoot",
//					    new Document("newRoot", "$doc")));
				
				List<Document> matchCase=Arrays.asList(new Document("$match",
					    new Document("CREATE_DATE",
					    new Document("$gte", "2024-06-01 00:00:00")
					                .append("$lte", "2024-06-15 23:59:59"))),
					    new Document("$group",
					    new Document("_id", "$PAY_ID")),
					    new Document("$lookup",
					    new Document("from", "transactionStatus")
					            .append("localField", "_id")
					            .append("foreignField", "PAY_ID")
					            .append("as", "allTransactions")),
					    new Document("$unwind", "$allTransactions"),
					    new Document("$sort",
					    new Document("allTransactions.CREATE_DATE", 1L)),
					    new Document("$group",
					    new Document("_id", "$_id")
					            .append("firstTransaction",
					    new Document("$first", "$allTransactions"))),
					    new Document("$replaceRoot",
					    new Document("newRoot",
					    new Document("$mergeObjects", Arrays.asList("$firstTransaction",
					                    new Document("merchantId", "$_id"))))),
					    new Document("$sort",
					    new Document("merchantId", 1L)));
				
				
				
				MongoCursor<Document> cursor1=coll.aggregate(matchCase).iterator();
				while (cursor1.hasNext()) {
					Document document = (Document) cursor1.next();
					custEmailIdList.add(String.valueOf(document.get("CUST_EMAIL")));
					
				}
				
				
				
			}
			

			BasicDBObject match =null;
			BasicDBObject finalquery =null;
			if (newDespositor.equalsIgnoreCase("true")) {
				
				
				
				BasicDBObject dateQuery1 = new BasicDBObject();
				dateQuery1.put(FieldType.CREATE_DATE.getName(),
									BasicDBObjectBuilder.start("$gte", new SimpleDateFormat(fromDate).toLocalizedPattern())
											.add("$lte", new SimpleDateFormat(toDate).toLocalizedPattern()).get());
				//BasicDBObject orderidListserach=new BasicDBObject();
				dateQuery1.put(FieldType.CUST_EMAIL.getName(),new BasicDBObject("$in", custEmailIdList));
				
				
				match=new BasicDBObject("$match", dateQuery1);
				finalquery=match;
			}else {

				 finalquery = new BasicDBObject("$and", fianlList);

				
				match=new BasicDBObject("$match", finalquery);
			}
			
			logger.info("1- Inside TxnReports , newSearchPayment method called, finalquery = " + finalquery);

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

			Map<String, String> businessName = new HashMap<String, String>();
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
				
				if (null != dbobj.getString(FieldType.CURRENCY_CODE.toString())) {
					transReport.setCurrency(currencyCodeDao.getCurrencyNamebyCode(dbobj.getString(FieldType.CURRENCY_CODE.toString())));
				} else {
					transReport.setCurrency(CrmFieldConstants.NA.getValue());
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
//				if (userMap.get(payid) != null && !userMap.get(payid).getPayId().isEmpty()) {
//					user1 = userMap.get(payid);
//				} else {
//					user1 = userdao.findPayId1(payid);
//					userMap.put(payid, user1);
//				}

				if(!businessName.containsKey(payid)) {
					logger.info("businessName hashmap={}", businessName);
					user1 = userdao.findPayId1(payid);
					businessName.put(payid, user1.getBusinessName());
				}
				
//				if (user1 == null) {
//					transReport.setMerchants(CrmFieldConstants.NA.getValue());
//				} else {
//					transReport.setMerchants(user1.getBusinessName());
//				}
				
				transReport.setMerchants(businessName.get(payid) !=null ? businessName.get(payid) : CrmFieldConstants.NA.getValue());

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
					transReport.setCurrency(currencyCodeDao.getCurrencyNamebyCode(dbobj.getString(FieldType.CURRENCY_CODE.toString())));
				} else {
					transReport.setCurrency(CrmFieldConstants.NA.getValue());
				}

				if (null != dbobj.getString(FieldType.ACQUIRER_TYPE.toString())) {
					transReport.setAcquirerType(dbobj.getString(FieldType.ACQUIRER_TYPE.toString()));
				}

				if (StringUtils.isNotBlank(dbobj.getString(FieldType.PG_TDR_SC.toString()))) {
					transReport.setPgSurchargeAmount(
							String.valueOf( ((Double.valueOf(dbobj.getString(FieldType.PG_TDR_SC.toString()))))));
				} else {
					transReport.setPgSurchargeAmount("0.00");
				}

				if (StringUtils.isNotBlank(dbobj.getString(FieldType.ACQUIRER_TDR_SC.toString()))) {
					transReport.setAcquirerSurchargeAmount(String.valueOf(
							(Double.valueOf(dbobj.getString(FieldType.ACQUIRER_TDR_SC.toString())))));
				} else {
					transReport.setAcquirerSurchargeAmount("0.00");
				}

				if (StringUtils.isNotBlank(dbobj.getString(FieldType.PG_GST.toString()))) {
					transReport.setPgGst(
							String.valueOf(((Double.valueOf(dbobj.getString(FieldType.PG_GST.toString()))))));
				} else {
					transReport.setPgGst("0.00");
				}

				if (StringUtils.isNotBlank(dbobj.getString(FieldType.ACQUIRER_GST.toString()))) {
					transReport.setAcquirerGst(String.valueOf(
							(Double.valueOf(dbobj.getString(FieldType.ACQUIRER_GST.toString())))));
				} else {
					transReport.setAcquirerGst("0.00");
				}
				String resolvedStatus = resolveStatus(transReport.getStatus());
				transReport.setStatus(resolvedStatus);
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

	public String resolveStatus(String status) {
		logger.info("resolveStatus : {}",status);
		if (StringUtils.isNotBlank(status) &&  (status.equalsIgnoreCase(StatusType.CAPTURED.getName()) || status.equalsIgnoreCase(StatusType.SETTLED_RECONCILLED.getName()) || status.equalsIgnoreCase(StatusType.SETTLED_SETTLE.getName())  || status.equalsIgnoreCase(StatusType.FAILED.getName())|| status.equalsIgnoreCase(StatusType.CHARGEBACK_INITIATED.getName()) 
				|| status.equalsIgnoreCase(StatusType.CHARGEBACK_REVERSAL.getName()))) {
			logger.info("resolveStatus Return: {}", status);
			return status;
		} else if(StringUtils.isNotBlank(status) && (status.equalsIgnoreCase(StatusType.DENIED.getName()))){
			logger.info("resolveStatus Return: {}", "REQUEST ACCEPTED");
			return "REQUEST ACCEPTED";
		}else if(StringUtils.isNotBlank(status) && (status.equalsIgnoreCase("REQUEST ACCEPTED") || status.equalsIgnoreCase("Pending") || status.equalsIgnoreCase(StatusType.SENT_TO_BANK.getName()))){
			logger.info("resolveStatus Return: {}", "REQUEST ACCEPTED");
			return "REQUEST ACCEPTED";
		}else {
			logger.info("resolveStatus Return: {}",StatusType.FAILED.getName());
			return StatusType.FAILED.getName();
		}
	}

	public int newSearchPaymentCount(String pgRefNum, String orderId, String customerEmail, String customerPhone,
			String paymentType, String aliasStatus, String currency, String transactionType, String mopType,
			String acquirer, String merchantPayId, String fromDate, String toDate, User user, int start, int length,
			String tenantId, String ipAddress, String totalAmount, String rrn,String channelName,String minAmount,String maxAmount,String columnName,String logicalCondition,String searchText, String newDespositor,String columnName1,String logicalCondition1,String searchText1,String columnName2,String logicalCondition2,String searchText2) {

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
					List<String> payIdLst = userdao.getActiveMerchantList(user.getSegment(), user.getRole().getId()).stream().map(Merchants::getPayId).collect(Collectors.toList());
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
				logger.info(" totalAmount, @@@@@@@@@@@@@@3333333>>>>>> = " +totalAmount);
				
				if (totalAmount.equals("0")) {

					logger.info(" totalAmount, @@@@@@@@@@@@@@3333333>>>>>> = " + totalAmount);
					paramConditionLst.add(new BasicDBObject(FieldType.TOTAL_AMOUNT.getName(), null));

				} else {

					if (total.length == 1) {
						totalAmount = totalAmount + ".00";
						logger.info(" totalAmount, @@@@@@@@@@44444444444>>>>>> = " + totalAmount);
					}
					paramConditionLst.add(new BasicDBObject(FieldType.TOTAL_AMOUNT.getName(), totalAmount));
					logger.info("Inside txnReports.java @@@@@333 totalAmount ======================= = "
							+ totalAmount);
				}

//				if (total.length == 1) {
//					totalAmount = totalAmount + ".00";
//					logger.info(" totalAmount, =======<1========================= = " + totalAmount);
//				}
//				paramConditionLst.add(new BasicDBObject(FieldType.TOTAL_AMOUNT.getName(), totalAmount));
//				logger.info(
//						"Inside txnReports.java  totalAmount, ==========s========================= = " + totalAmount);

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
			 * "endDateTime========================= = " , endDateTime);
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
			 * logger.info("newSearchPaymentCount++++++++++++++++++++++++++++++" , endTime
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
			 * , endDateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd hh:mm:ss")));
			 * logger.
			 * info("endDateTime.format(DateTimeFormatter.ofPattern(\"yyyy-MM-dd hh:mm:ss\")))), ==========s========================= = "
			 * + startDateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd hh:mm:ss")));
			 * 
			 * 
			 * }
			 */
			
			//Added by Deep Singh code start here
			
			logger.info("channelName::: " + channelName );
			logger.info("minAmount::: " + minAmount );
			logger.info("maxAmount::: " + maxAmount );
			logger.info("columnName::: " + columnName );
			logger.info("logicalCondition::: " + logicalCondition );
			logger.info("searchText::: " + searchText );
			
			
			if (channelName.equalsIgnoreCase("All")) {
//				paramConditionLst.add(new BasicDBObject(FieldType.CHANNEL.getName(),
//						new BasicDBObject("$in", Arrays.asList("Fiat", "Crypto"))));
			} else {
				if(channelName.contains(",")) {
					String [] channels=channelName.split(",");
					ArrayList<String> aa=new ArrayList<>();
					for(String channel:channels) {
						aa.add(channel);
					}
					paramConditionLst.add(new BasicDBObject(FieldType.CHANNEL.getName(), new BasicDBObject("$in",aa)));	
				}else {
					paramConditionLst.add(new BasicDBObject(FieldType.CHANNEL.getName(), new BasicDBObject("$in",Arrays.asList(channelName))));
				}
				
			}

//			if (!minAmount.equalsIgnoreCase("")) {
//				paramConditionLst
//						.add(new BasicDBObject(FieldType.TOTAL_AMOUNT.getName(), new BasicDBObject("$gte", minAmount)));
//			}
//
//			if (!maxAmount.equalsIgnoreCase("")) {
//				paramConditionLst
//						.add(new BasicDBObject(FieldType.TOTAL_AMOUNT.getName(), new BasicDBObject("$lte", maxAmount)));
//			}
			if(StringUtils.isNoneBlank(minAmount)&& StringUtils.isNoneBlank(maxAmount)) {
				paramConditionLst.add(
					    new BasicDBObject("$expr",
					    new BasicDBObject("$and", Arrays.asList(new BasicDBObject("$gte", Arrays.asList(new BasicDBObject("$toDouble", "$TOTAL_AMOUNT"), Double.parseDouble(minAmount))),
					                    new BasicDBObject("$lte", Arrays.asList(new BasicDBObject("$toDouble", "$TOTAL_AMOUNT"), Double.parseDouble(maxAmount)))))));
			}

			if (!columnName.equalsIgnoreCase("") && !logicalCondition.equalsIgnoreCase("")
					&& !searchText.equalsIgnoreCase("")) {
				paramConditionLst.add(new BasicDBObject(columnName, new BasicDBObject(logicalCondition, searchText)));
			}
			
			if (!columnName1.equalsIgnoreCase("") && !logicalCondition1.equalsIgnoreCase("")
					&& !searchText1.equalsIgnoreCase("")) {
				paramConditionLst.add(new BasicDBObject(columnName1, new BasicDBObject(logicalCondition1, searchText1)));
			}
			
			if (!columnName2.equalsIgnoreCase("") && !logicalCondition2.equalsIgnoreCase("")
					&& !searchText2.equalsIgnoreCase("")) {
				paramConditionLst.add(new BasicDBObject(columnName2, new BasicDBObject(logicalCondition2, searchText2)));
			}
			
			//Added by Deep Singh code end here
			
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
					aliasStatusConditionLst.add(new BasicDBObject(FieldType.STATUS.getName(), status.equalsIgnoreCase("Request Accepted")?"Sent to Bank":status.trim()));
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
			
			List<String>custMailIdList=new ArrayList<>();
			if (newDespositor.equalsIgnoreCase("true")) {
				
				List<Document> matchCase= Arrays.asList(new Document("$match", 
					    new Document("CUST_EMAIL", 
					    	    new Document("$ne", 
					    	    new BsonNull()))
					    	            .append("CREATE_DATE", 
					    	    new Document("$gte", fromDate)
					    	                .append("$lte", toDate))), 
					    	    new Document("$group", 
					    	    new Document("_id", "$CUST_EMAIL")
					    	            .append("transaction_count", 
					    	    new Document("$sum", 1L))), 
					    	    new Document("$match", 
					    	    new Document("transaction_count", 
					    	    new Document("$eq", 1L))), 
					    	    new Document("$project", 
					    	    new Document("_id", 0L)
					    	            .append("CUST_EMAIL", "$_id")));
				
				

				
				
				MongoCursor<Document> cursor1=coll.aggregate(matchCase).iterator();
				while (cursor1.hasNext()) {
					Document document = (Document) cursor1.next();
					custMailIdList.add(String.valueOf(document.get("CUST_EMAIL")));
					
				}
				
				
				
			}
			

			BasicDBObject match =null;
			BasicDBObject finalquery =null;
			if (newDespositor.equalsIgnoreCase("true")) {
				BasicDBObject dateQuery1 = new BasicDBObject();
				dateQuery1.put(FieldType.CREATE_DATE.getName(),
									BasicDBObjectBuilder.start("$gte", new SimpleDateFormat(fromDate).toLocalizedPattern())
											.add("$lte", new SimpleDateFormat(toDate).toLocalizedPattern()).get());
				//BasicDBObject orderidListserach=new BasicDBObject();
				dateQuery1.put(FieldType.CUST_EMAIL.getName(),new BasicDBObject("$in", custMailIdList));
				
				
				match=new BasicDBObject("$match", dateQuery1);
				finalquery=match;
			}else {

				 finalquery = new BasicDBObject("$and", fianlList);

				
				match=new BasicDBObject("$match", finalquery);
			}
			
			logger.info("1- Inside TxnReports , newSearchPayment method called, finalquery = " + finalquery);
			
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

	public HashMap<String, String> totalAmountOfAllTxnsNew(String pgRefNum, String orderId, String customerEmail,
			String customerPhone, String paymentType, String aliasStatus, String currency, String transactionType,
			String mopType, String acquirer, String merchantPayId, String fromDate, String toDate, User user,
			String type, boolean isSettled, String tenantId, String ipAdress, String totalAmount,String channelName,String minAmount,String maxAmount,String columnName,String logicalCondition,String searchText) {

		HashMap<String, String> finalMap = new HashMap<String, String>();

		try {

			MongoDatabase dbIns = mongoInstance.getDB();
			MongoCollection<Document> coll = dbIns.getCollection(
					PropertiesManager.propertiesMap.get(prefix + Constants.TRANSACTION_STATUS_COLLECTION.getValue()));

			List<BasicDBObject> paramConditionLst = new ArrayList<BasicDBObject>();
			BasicDBObject allParamQuery = new BasicDBObject();
			BasicDBObject saleDateQuery = new BasicDBObject();
			BasicDBObject settledDateQuery = new BasicDBObject();
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
			List<BasicDBObject> saleDateIndexConditionList = new ArrayList<BasicDBObject>();
			BasicDBObject saleDateIndexConditionQuery = new BasicDBObject();

			List<BasicDBObject> settledDateIndexConditionList = new ArrayList<BasicDBObject>();
			BasicDBObject settledDateIndexConditionQuery = new BasicDBObject();

			saleDateQuery.put(FieldType.CREATE_DATE.getName(),
					BasicDBObjectBuilder.start("$gte", new SimpleDateFormat(fromDate).toLocalizedPattern())
							.add("$lte", new SimpleDateFormat(toDate).toLocalizedPattern()).get());

			settledDateQuery.put(FieldType.SETTLEMENT_DATE.getName(),
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
				saleDateIndexConditionList.add(new BasicDBObject("DATE_INDEX", dateIndex));
			}
			if (StringUtils.isNotBlank(PropertiesManager.propertiesMap.get(Constants.USE_DATE_INDEX.getValue()))
					&& PropertiesManager.propertiesMap.get(Constants.USE_DATE_INDEX.getValue()).equalsIgnoreCase("Y")) {
				saleDateIndexConditionQuery.append("$or", saleDateIndexConditionList);
			}

			for (String dateIndex : allDatesIndex) {
				settledDateIndexConditionList.add(new BasicDBObject("SETTLEMENT_DATE_INDEX", dateIndex));
			}
			if (StringUtils.isNotBlank(PropertiesManager.propertiesMap.get(Constants.USE_DATE_INDEX.getValue()))
					&& PropertiesManager.propertiesMap.get(Constants.USE_DATE_INDEX.getValue()).equalsIgnoreCase("Y")) {
				settledDateIndexConditionQuery.append("$or", settledDateIndexConditionList);
			}

			/*
			 * if (!merchantPayId.equalsIgnoreCase("ALL")) { paramConditionLst.add(new
			 * BasicDBObject(FieldType.PAY_ID.getName(), merchantPayId)); }
			 */

			if (merchantPayId.equalsIgnoreCase("ALL")) {
				logger.info("TxnReport, MerchantId : " + merchantPayId + ", Segment : " + user.getSegment());
				if (!user.getSegment().equalsIgnoreCase("Default")) {
					List<String> payIdLst = userdao.getActiveMerchantList(user.getSegment(), user.getRole().getId()).stream().map(Merchants::getPayId).collect(Collectors.toList());
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
				paramConditionLst.add(new BasicDBObject(FieldType.CUST_EMAIL.getName(), customerEmail.trim()));
				isParameterised = true;
			}

			if (!customerPhone.isEmpty()) {
				paramConditionLst.add(new BasicDBObject(FieldType.CUST_PHONE.getName(), customerPhone.trim()));
				isParameterised = true;
			}

			if (!StringUtils.isBlank(ipAdress)) {
				paramConditionLst.add(new BasicDBObject(FieldType.INTERNAL_CUST_IP.getName(), ipAdress.trim()));
				// isParameterised = true;
			}

			if (StringUtils.isNotEmpty(totalAmount)) {

				String[] total = StringUtils.split(totalAmount, ".");
				if (totalAmount.equals("0")) {

					logger.info(" totalAmount, @@@@@@@@@@@@@@5555>>>>>> = " + totalAmount);
					paramConditionLst.add(new BasicDBObject(FieldType.TOTAL_AMOUNT.getName(), null));

				} else {

					if (total.length == 1) {
						totalAmount = totalAmount + ".00";
						logger.info(" totalAmount, @@@@@@@@@@6666>>>>>> = " + totalAmount);
					}
					paramConditionLst.add(new BasicDBObject(FieldType.TOTAL_AMOUNT.getName(), totalAmount));
					logger.info("Inside txnReports.java @@@@@555 totalAmount ======================= = "
							+ totalAmount);
				}

//				if (total.length == 1) {
//					totalAmount = totalAmount + ".00";
//					logger.info(" totalAmount, ================================ = " + totalAmount);
//				}
//				paramConditionLst.add(new BasicDBObject(FieldType.TOTAL_AMOUNT.getName(), totalAmount));
//				logger.info("Inside txnReports.java  totalAmount, ==========s========================= = " + totalAmount);

			}

			/*
			 * if (!totalAmount.isEmpty()) { paramConditionLst.add(new
			 * BasicDBObject(FieldType.TOTAL_AMOUNT.getName(), totalAmount.trim()));
			 * isParameterised = true; }
			 */

			//Added by Deep Singh code start here
			
			logger.info("channelName::: " + channelName );
			logger.info("minAmount::: " + minAmount );
			logger.info("maxAmount::: " + maxAmount );
			logger.info("columnName::: " + columnName );
			logger.info("logicalCondition::: " + logicalCondition );
			logger.info("searchText::: " + searchText );
			
			
			if (channelName.equalsIgnoreCase("All")) {
//				paramConditionLst.add(new BasicDBObject(FieldType.CHANNEL.getName(),
//						new BasicDBObject("$in", Arrays.asList("Fiat", "Crypto"))));
			} else {
				if(channelName.contains(",")) {
					String [] channels=channelName.split(",");
					ArrayList<String> aa=new ArrayList<>();
					for(String channel:channels) {
						aa.add(channel);
					}
					paramConditionLst.add(new BasicDBObject(FieldType.CHANNEL.getName(), new BasicDBObject("$in",aa)));	
				}else {
					paramConditionLst.add(new BasicDBObject(FieldType.CHANNEL.getName(), new BasicDBObject("$in",Arrays.asList(channelName))));
				}
				
			}

//			if (!minAmount.equalsIgnoreCase("")) {
//				paramConditionLst
//						.add(new BasicDBObject(FieldType.TOTAL_AMOUNT.getName(), new BasicDBObject("$gte", minAmount)));
//			}
//
//			if (!maxAmount.equalsIgnoreCase("")) {
//				paramConditionLst
//						.add(new BasicDBObject(FieldType.TOTAL_AMOUNT.getName(), new BasicDBObject("$lte", maxAmount)));
//			}
			if(StringUtils.isNoneBlank(minAmount)&& StringUtils.isNoneBlank(maxAmount)) {
				paramConditionLst.add(
					    new BasicDBObject("$expr",
					    new BasicDBObject("$and", Arrays.asList(new BasicDBObject("$gte", Arrays.asList(new BasicDBObject("$toDouble", "$TOTAL_AMOUNT"), Double.parseDouble(minAmount))),
					                    new BasicDBObject("$lte", Arrays.asList(new BasicDBObject("$toDouble", "$TOTAL_AMOUNT"), Double.parseDouble(maxAmount)))))));
			}

			if (!columnName.equalsIgnoreCase("") && !logicalCondition.equalsIgnoreCase("")
					&& !searchText.equalsIgnoreCase("")) {
				paramConditionLst.add(new BasicDBObject(columnName, new BasicDBObject(logicalCondition, searchText)));
			}
			
			//Added by Deep Singh code end here
			
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

			List<BasicDBObject> allConditionQueryList = new ArrayList<BasicDBObject>();

			// For Initiated txns
			if (type.equalsIgnoreCase("initiated")) {

				// Below variables for initiative logic
				int totaltxns = 0;
				int totalSaleSuccCount = 0;
				int totalSalefailCount = 0;
				int totalSalePendingCount = 0;
				int totalSaleTOCount = 0;
				int totalSaleCancelledCount = 0;
				int totalSaleInvalidCount = 0;
				int totalRefundSuccCount = 0;
				int totalRefundFailCount = 0;
				Double totalTxnAmount = 0.00, totalSalefailAmount = 0.00, totalSalePendingAmount = 0.00,
						totalSaleSuccAmount = 0.00;
				Double totalSaleTOAmount = 0.00, totalSaleCancelledAmount = 0.00, totalRefundSuccAmount = 0.00,
						totalRefundFailAmount = 0.00;
				Double totalSaleInvalidAmount = 0.00;

				// If status Settled is selected, don't need to calculate initiated data
				if (isSettled) {
					finalMap.put("totalSaleSuccAmount", String.format("%.2f", totalSaleSuccAmount));
					finalMap.put("totalSaleSuccCount", String.format("%d", totalSaleSuccCount));
					finalMap.put("totalSalefailAmount", String.format("%.2f", totalSalefailAmount));
					finalMap.put("totalSalefailCount", String.format("%d", totalSalefailCount));
					finalMap.put("totalSalePendingAmount", String.format("%.2f", totalSalePendingAmount));
					finalMap.put("totalSalePendingCount", String.format("%d", totalSalePendingCount));
					finalMap.put("totalSaleTOAmount", String.format("%.2f", totalSaleTOAmount));
					finalMap.put("totalSaleTOCount", String.format("%d", totalSaleTOCount));
					finalMap.put("totalSaleCancelledAmount", String.format("%.2f", totalSaleCancelledAmount));
					finalMap.put("totalSaleCancelledCount", String.format("%d", totalSaleCancelledCount));
					finalMap.put("totalSaleInvalidAmount", String.format("%.2f", totalSaleInvalidAmount));
					finalMap.put("totalSaleInvalidCount", String.format("%d", totalSaleInvalidCount));
					finalMap.put("totalRefundSuccAmount", String.format("%.2f", totalRefundSuccAmount));
					finalMap.put("totalRefundSuccCount", String.format("%d", totalRefundSuccCount));
					finalMap.put("totalRefundFailAmount", String.format("%.2f", totalRefundFailAmount));
					finalMap.put("totalRefundFailCount", String.format("%d", totalRefundFailCount));
					finalMap.put("totalTxnAmount", String.format("%.2f", totalTxnAmount));
					finalMap.put("totaltxns", String.format("%d", totaltxns));
					return finalMap;
				}

				if (!aliasStatus.equalsIgnoreCase("ALL")) {
					List<String> aliasStatusList = Arrays.asList(aliasStatus.split(","));
					for (String status : aliasStatusList) {
						aliasStatusConditionLst.add(new BasicDBObject(FieldType.STATUS.getName(), status.trim()));
					}
					transactionStatusQuery.append("$or", aliasStatusConditionLst);
				}

				if (!isParameterised) {
					if (!saleDateQuery.isEmpty()) {
						allConditionQueryList.add(saleDateQuery);
					}
					if (!saleDateIndexConditionQuery.isEmpty()) {
						allConditionQueryList.add(saleDateIndexConditionQuery);
					}
				}

				List<BasicDBObject> finalList = new ArrayList<BasicDBObject>();

				if (!allParamQuery.isEmpty()) {
					finalList.add(allParamQuery);
				}

				if (!allConditionQueryList.isEmpty()) {
					BasicDBObject allConditionQueryObj = new BasicDBObject("$and", allConditionQueryList);
					if (!allConditionQueryObj.isEmpty()) {
						finalList.add(allConditionQueryObj);
					}
				}

				if (!acquirerQuery.isEmpty()) {
					finalList.add(acquirerQuery);
				}

				if (!mopTypeQuery.isEmpty()) {
					finalList.add(mopTypeQuery);
				}
				if (!paymentTypeQuery.isEmpty()) {
					finalList.add(paymentTypeQuery);
				}
				if (!transactionStatusQuery.isEmpty()) {
					finalList.add(transactionStatusQuery);
				}

				if (!transactionTypeQuery.isEmpty()) {
					finalList.add(transactionTypeQuery);
				}

				BasicDBObject finalquery = new BasicDBObject("$and", finalList);
				logger.info("Inside TxnReports , finalquery = " + finalquery);

				BasicDBObject match = new BasicDBObject("$match", finalquery);
				Document firstGroup = new Document("_id",
						new Document("PG_REF_NUM", "$PG_REF_NUM").append("STATUS", "$STATUS"));
				BasicDBObject firstGroupObject = new BasicDBObject(firstGroup);
				BasicDBObject secondGroup = new BasicDBObject("$last", "$$ROOT");
				BasicDBObject groupQuery = new BasicDBObject("$group",
						firstGroupObject.append("contentList", secondGroup));

				List<Bson> pipeline = Arrays.asList(match, groupQuery);

				AggregateIterable<Document> output = coll.aggregate(pipeline);
				output.allowDiskUse(true);
				MongoCursor<Document> cursor = output.iterator();

				while (cursor.hasNext()) {

					Document dbobj = cursor.next();
					dbobj = new ObjectMapper().convertValue(dbobj.get("contentList"), Document.class);
					String aliasStat = dbobj.getString(FieldType.ALIAS_STATUS.getName());

					// Skip count for Settled Transactions
					if (null != aliasStat && aliasStat.equals(StatusType.SETTLED_SETTLE.getName())) {
						continue;
					}

					totaltxns = totaltxns + 1;
					Double totalAmt = 0.00;
					if (null != dbobj.get(FieldType.TOTAL_AMOUNT.getName())) {
						totalAmt = Double.valueOf(dbobj.get(FieldType.TOTAL_AMOUNT.getName()).toString());
						totalTxnAmount = totalTxnAmount + totalAmt;
					} else if (null != dbobj.get(FieldType.AMOUNT.getName())) {
						totalAmt = Double.valueOf(dbobj.get(FieldType.AMOUNT.getName()).toString());
						totalTxnAmount = totalTxnAmount + totalAmt;
					} else {
						totalTxnAmount = totalTxnAmount + totalAmt;
					}

					if (dbobj.getString(FieldType.ORIG_TXNTYPE.getName())
							.equalsIgnoreCase(TransactionType.SALE.getName())) {

						switch (aliasStat) {
						case "Captured":
							totalSaleSuccCount = totalSaleSuccCount + 1;
							totalSaleSuccAmount = totalSaleSuccAmount + totalAmt;
							break;
						case "Pending":
							totalSalePendingCount = totalSalePendingCount + 1;
							totalSalePendingAmount = totalSalePendingAmount + totalAmt;
							break;
						case "Failed":
							totalSalefailCount = totalSalefailCount + 1;
							totalSalefailAmount = totalSalefailAmount + totalAmt;
							break;
						case "Invalid":
							totalSaleInvalidCount = totalSaleInvalidCount + 1;
							totalSaleInvalidAmount = totalSaleInvalidAmount + totalAmt;
							break;
						case "Cancelled":
							totalSaleCancelledCount = totalSaleCancelledCount + 1;
							totalSaleCancelledAmount = totalSaleCancelledAmount + totalAmt;
							break;
						default:
							// code block
						}

					} else {
						switch (aliasStat) {
						case "Captured":
							totalRefundSuccCount = totalRefundSuccCount + 1;
							totalRefundSuccAmount = totalRefundSuccAmount + totalAmt;
							break;
						case "Settled":
							break;
						default:
							totalRefundFailCount = totalRefundFailCount + 1;
							totalRefundFailAmount = totalRefundFailAmount + totalAmt;
						}

					}

				}

				finalMap.put("totalSaleSuccAmount", String.format("%.2f", totalSaleSuccAmount));
				finalMap.put("totalSaleSuccCount", String.format("%d", totalSaleSuccCount));
				finalMap.put("totalSalefailAmount", String.format("%.2f", totalSalefailAmount));
				finalMap.put("totalSalefailCount", String.format("%d", totalSalefailCount));
				finalMap.put("totalSalePendingAmount", String.format("%.2f", totalSalePendingAmount));
				finalMap.put("totalSalePendingCount", String.format("%d", totalSalePendingCount));
				finalMap.put("totalSaleTOAmount", String.format("%.2f", totalSaleTOAmount));
				finalMap.put("totalSaleTOCount", String.format("%d", totalSaleTOCount));
				finalMap.put("totalSaleCancelledAmount", String.format("%.2f", totalSaleCancelledAmount));
				finalMap.put("totalSaleCancelledCount", String.format("%d", totalSaleCancelledCount));
				finalMap.put("totalSaleInvalidAmount", String.format("%.2f", totalSaleInvalidAmount));
				finalMap.put("totalSaleInvalidCount", String.format("%d", totalSaleInvalidCount));
				finalMap.put("totalRefundSuccAmount", String.format("%.2f", totalRefundSuccAmount));
				finalMap.put("totalRefundSuccCount", String.format("%d", totalRefundSuccCount));
				finalMap.put("totalRefundFailAmount", String.format("%.2f", totalRefundFailAmount));
				finalMap.put("totalRefundFailCount", String.format("%d", totalRefundFailCount));
				finalMap.put("totalTxnAmount", String.format("%.2f", totalTxnAmount));
				finalMap.put("totaltxns", String.format("%d", totaltxns));
				return finalMap;

			}
			// For Settled
			else {

				// Below variables for Settled logic
				int totalSettleCount = 0;
				int totalSaleCCcount = 0;
				int totalSaleDCcount = 0;
				int totalSaleNBcount = 0;
				int totalSalewalletcount = 0;
				int totalSaleUPIcount = 0;
				int totalSaleCount = 0;
				int totalRfCCcount = 0;
				int totalRfDCcount = 0;
				int totalRfNBcount = 0;
				int totalRfwalletcount = 0;
				int totalRfUPIcount = 0;
				int totalRfCount = 0;

				Double totalSettleAmount = 0.00, totalSaleCCAmount = 0.00, totalSaleDCAmount = 0.00,
						totalSaleNBAmount = 0.00;
				Double totalSalewalletAmount = 0.00, totalSaleUPIAmount = 0.00, totalSaleAmount = 0.00,
						totalRfCCAmount = 0.00;
				Double totalRfDCAmount = 0.00, totalRfNBAmount = 0.00, totalRfwalletAmount = 0.00,
						totalRfUPIAmount = 0.00;
				Double totalRfAmount = 0.00;

				if (isSettled) {

					if (!isParameterised) {
						if (!saleDateQuery.isEmpty()) {
							allConditionQueryList.add(saleDateQuery);
						}
						if (!saleDateIndexConditionQuery.isEmpty()) {
							allConditionQueryList.add(saleDateIndexConditionQuery);
						}
					}

					transactionStatusQuery.append(FieldType.ALIAS_STATUS.getName(), "Settled");

					List<BasicDBObject> finalList = new ArrayList<BasicDBObject>();

					if (!allParamQuery.isEmpty()) {
						finalList.add(allParamQuery);
					}

					if (!allConditionQueryList.isEmpty()) {
						BasicDBObject allConditionQueryObj = new BasicDBObject("$and", allConditionQueryList);
						if (!allConditionQueryObj.isEmpty()) {
							finalList.add(allConditionQueryObj);
						}
					}

					if (!acquirerQuery.isEmpty()) {
						finalList.add(acquirerQuery);
					}

					if (!mopTypeQuery.isEmpty()) {
						finalList.add(mopTypeQuery);
					}
					if (!paymentTypeQuery.isEmpty()) {
						finalList.add(paymentTypeQuery);
					}
					if (!transactionStatusQuery.isEmpty()) {
						finalList.add(transactionStatusQuery);
					}

					if (!transactionTypeQuery.isEmpty()) {
						finalList.add(transactionTypeQuery);
					}

					BasicDBObject finalquery = new BasicDBObject("$and", finalList);
					logger.info("Inside TxnReports , finalquery = " + finalquery);

					BasicDBObject match = new BasicDBObject("$match", finalquery);
					List<BasicDBObject> pipeline = Arrays.asList(match);

					AggregateIterable<Document> output = coll.aggregate(pipeline);
					output.allowDiskUse(true);
					MongoCursor<Document> cursor = output.iterator();

					while (cursor.hasNext()) {

						Document dbobj = cursor.next();
						String paymentCode = dbobj.getString(FieldType.PAYMENT_TYPE.getName());

						totalSettleCount = totalSettleCount + 1;
						Double thisAmt = 0.00;
						if (null != dbobj.get(FieldType.TOTAL_AMOUNT.getName())) {
							thisAmt = Double.valueOf(dbobj.get(FieldType.TOTAL_AMOUNT.getName()).toString());
							totalSettleAmount = totalSettleAmount + thisAmt;
						} else {
							thisAmt = Double.valueOf(dbobj.get(FieldType.AMOUNT.getName()).toString());
							totalSettleAmount = totalSettleAmount + thisAmt;
						}

						if (dbobj.getString(FieldType.ORIG_TXNTYPE.getName())
								.equalsIgnoreCase(TransactionType.SALE.getName())) {

							totalSaleAmount = totalSaleAmount + thisAmt;
							totalSaleCount = totalSaleCount + 1;
							switch (paymentCode) {
							case "CC":
								totalSaleCCcount = totalSaleCCcount + 1;
								totalSaleCCAmount = totalSaleCCAmount + thisAmt;
								break;
							case "DC":
								totalSaleDCcount = totalSaleDCcount + 1;
								totalSaleDCAmount = totalSaleDCAmount + thisAmt;
								break;
							case "WL":
								totalSalewalletcount = totalSalewalletcount + 1;
								totalSalewalletAmount = totalSalewalletAmount + thisAmt;
								break;
							case "NB":
								totalSaleNBcount = totalSaleNBcount + 1;
								totalSaleNBAmount = totalSaleNBAmount + thisAmt;
								break;
							case "UP":
								totalSaleUPIcount = totalSaleUPIcount + 1;
								totalSaleUPIAmount = totalSaleUPIAmount + thisAmt;
								break;
							default:
								// code block
							}

						} else {

							totalRfAmount = totalRfAmount + thisAmt;
							totalRfCount = totalRfCount + 1;
							switch (paymentCode) {
							case "CC":
								totalRfCCcount = totalRfCCcount + 1;
								totalRfCCAmount = totalRfCCAmount + thisAmt;
								break;
							case "DC":
								totalRfDCcount = totalRfDCcount + 1;
								totalRfDCAmount = totalRfDCAmount + thisAmt;
								break;
							case "WL":
								totalRfwalletcount = totalRfwalletcount + 1;
								totalRfwalletAmount = totalRfwalletAmount + thisAmt;
								break;
							case "NB":
								totalRfNBcount = totalRfNBcount + 1;
								totalRfNBAmount = totalRfNBAmount + thisAmt;
								break;
							case "UP":
								totalRfUPIcount = totalRfUPIcount + 1;
								totalRfUPIAmount = totalRfUPIAmount + thisAmt;
								break;
							default:
								// code block
							}
						}

					}

					finalMap.put("totalSettleAmount", String.format("%.2f", totalSettleAmount));
					finalMap.put("totalSettleCount", String.format("%d", totalSettleCount));
					finalMap.put("totalSaleCCAmount", String.format("%.2f", totalSaleCCAmount));
					finalMap.put("totalSaleCCcount", String.format("%d", totalSaleCCcount));
					finalMap.put("totalSaleDCAmount", String.format("%.2f", totalSaleDCAmount));
					finalMap.put("totalSaleDCcount", String.format("%d", totalSaleDCcount));
					finalMap.put("totalSaleNBAmount", String.format("%.2f", totalSaleNBAmount));
					finalMap.put("totalSaleNBcount", String.format("%d", totalSaleNBcount));
					finalMap.put("totalSalewalletAmount", String.format("%.2f", totalSalewalletAmount));
					finalMap.put("totalSalewalletcount", String.format("%d", totalSalewalletcount));
					finalMap.put("totalSaleUPIAmount", String.format("%.2f", totalSaleUPIAmount));
					finalMap.put("totalSaleUPIcount", String.format("%d", totalSaleUPIcount));
					finalMap.put("totalSaleAmount", String.format("%.2f", totalSaleAmount));
					finalMap.put("totalSaleCount", String.format("%d", totalSaleCount));
					finalMap.put("totalRfCCAmount", String.format("%.2f", totalRfCCAmount));
					finalMap.put("totalRfCCcount", String.format("%d", totalRfCCcount));
					finalMap.put("totalRfDCAmount", String.format("%.2f", totalRfDCAmount));
					finalMap.put("totalRfDCcount", String.format("%d", totalRfDCcount));
					finalMap.put("totalRfNBAmount", String.format("%.2f", totalRfNBAmount));
					finalMap.put("totalRfNBcount", String.format("%d", totalRfNBcount));
					finalMap.put("totalRfwalletAmount", String.format("%.2f", totalRfwalletAmount));
					finalMap.put("totalRfwalletcount", String.format("%d", totalRfwalletcount));
					finalMap.put("totalRfUPIAmount", String.format("%.2f", totalRfUPIAmount));
					finalMap.put("totalRfUPIcount", String.format("%d", totalRfUPIcount));
					finalMap.put("totalRfAmount", String.format("%.2f", totalRfAmount));
					finalMap.put("totalRfCount", String.format("%d", totalRfCount));
					return finalMap;

				} else {

					finalMap.put("totalSettleAmount", String.format("%.2f", totalSettleAmount));
					finalMap.put("totalSettleCount", String.format("%d", totalSettleCount));
					finalMap.put("totalSaleCCAmount", String.format("%.2f", totalSaleCCAmount));
					finalMap.put("totalSaleCCcount", String.format("%d", totalSaleCCcount));
					finalMap.put("totalSaleDCAmount", String.format("%.2f", totalSaleDCAmount));
					finalMap.put("totalSaleDCcount", String.format("%d", totalSaleDCcount));
					finalMap.put("totalSaleNBAmount", String.format("%.2f", totalSaleNBAmount));
					finalMap.put("totalSaleNBcount", String.format("%d", totalSaleNBcount));
					finalMap.put("totalSalewalletAmount", String.format("%.2f", totalSalewalletAmount));
					finalMap.put("totalSalewalletcount", String.format("%d", totalSalewalletcount));
					finalMap.put("totalSaleUPIAmount", String.format("%.2f", totalSaleUPIAmount));
					finalMap.put("totalSaleUPIcount", String.format("%d", totalSaleUPIcount));
					finalMap.put("totalSaleAmount", String.format("%.2f", totalSaleAmount));
					finalMap.put("totalSaleCount", String.format("%d", totalSaleCount));
					finalMap.put("totalRfCCAmount", String.format("%.2f", totalRfCCAmount));
					finalMap.put("totalRfCCcount", String.format("%d", totalRfCCcount));
					finalMap.put("totalRfDCAmount", String.format("%.2f", totalRfDCAmount));
					finalMap.put("totalRfDCcount", String.format("%d", totalRfDCcount));
					finalMap.put("totalRfNBAmount", String.format("%.2f", totalRfNBAmount));
					finalMap.put("totalRfNBcount", String.format("%d", totalRfNBcount));
					finalMap.put("totalRfwalletAmount", String.format("%.2f", totalRfwalletAmount));
					finalMap.put("totalRfwalletcount", String.format("%d", totalRfwalletcount));
					finalMap.put("totalRfUPIAmount", String.format("%.2f", totalRfUPIAmount));
					finalMap.put("totalRfUPIcount", String.format("%d", totalRfUPIcount));
					finalMap.put("totalRfAmount", String.format("%.2f", totalRfAmount));
					finalMap.put("totalRfCount", String.format("%d", totalRfCount));
					return finalMap;
				}
			}

		} catch (Exception e) {
			logger.error("Exception in getting initiated or settled results for search txn ", e);
		}
		return finalMap;

	}

	public HashMap<String, String> totalAmountOfAllTxnsNewSplitPayment(String pgRefNum, String orderId,
			String customerEmail, String customerPhone, String paymentType, String aliasStatus, String currency,
			String transactionType, String mopType, String acquirer, String merchantPayId, String fromDate,
			String toDate, User user, String type, boolean isSettled, String tenantId, String ipAdress,
			String totalAmount) {

		HashMap<String, String> finalMap = new HashMap<String, String>();

		try {

			MongoDatabase dbIns = mongoInstance.getDB();
			MongoCollection<Document> coll = dbIns.getCollection(
					PropertiesManager.propertiesMap.get(prefix + Constants.TRANSACTION_STATUS_COLLECTION.getValue()));

			List<BasicDBObject> paramConditionLst = new ArrayList<BasicDBObject>();
			BasicDBObject allParamQuery = new BasicDBObject();
			BasicDBObject saleDateQuery = new BasicDBObject();
			BasicDBObject settledDateQuery = new BasicDBObject();
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
			List<BasicDBObject> saleDateIndexConditionList = new ArrayList<BasicDBObject>();
			BasicDBObject saleDateIndexConditionQuery = new BasicDBObject();

			List<BasicDBObject> settledDateIndexConditionList = new ArrayList<BasicDBObject>();
			BasicDBObject settledDateIndexConditionQuery = new BasicDBObject();

			saleDateQuery.put(FieldType.CREATE_DATE.getName(),
					BasicDBObjectBuilder.start("$gte", new SimpleDateFormat(fromDate).toLocalizedPattern())
							.add("$lte", new SimpleDateFormat(toDate).toLocalizedPattern()).get());

			settledDateQuery.put(FieldType.SETTLEMENT_DATE.getName(),
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
				saleDateIndexConditionList.add(new BasicDBObject("DATE_INDEX", dateIndex));
			}
			if (StringUtils.isNotBlank(PropertiesManager.propertiesMap.get(Constants.USE_DATE_INDEX.getValue()))
					&& PropertiesManager.propertiesMap.get(Constants.USE_DATE_INDEX.getValue()).equalsIgnoreCase("Y")) {
				saleDateIndexConditionQuery.append("$or", saleDateIndexConditionList);
			}

			for (String dateIndex : allDatesIndex) {
				settledDateIndexConditionList.add(new BasicDBObject("SETTLEMENT_DATE_INDEX", dateIndex));
			}
			if (StringUtils.isNotBlank(PropertiesManager.propertiesMap.get(Constants.USE_DATE_INDEX.getValue()))
					&& PropertiesManager.propertiesMap.get(Constants.USE_DATE_INDEX.getValue()).equalsIgnoreCase("Y")) {
				settledDateIndexConditionQuery.append("$or", settledDateIndexConditionList);
			}

			/*
			 * if (!merchantPayId.equalsIgnoreCase("ALL")) { paramConditionLst.add(new
			 * BasicDBObject(FieldType.PAY_ID.getName(), merchantPayId)); }
			 */

			/*
			 * if (merchantPayId.equalsIgnoreCase("ALL")) { List<String> payIdLst =
			 * userdao.getActiveMerchantList(user.getSegment(), user.getRole().getId()).stream().map(Merchants::getPayId).collect(Collectors.toList());
			 * logger.info("Get PayId For SplitPayment Merchant : "+payIdLst);
			 * if(payIdLst.size() > 0) { //isParameterised = true; paramConditionLst.add(new
			 * BasicDBObject(FieldType.PAY_ID.getName(), new BasicDBObject("$in",
			 * payIdLst))); } } else { //isParameterised = true; paramConditionLst.add(new
			 * BasicDBObject(FieldType.PAY_ID.getName(), merchantPayId)); }
			 */

			if (merchantPayId.equalsIgnoreCase("ALL")) {
				logger.info("TxnReport, MerchantId : " + merchantPayId + ", Segment : " + user.getSegment());
				if (!user.getSegment().equalsIgnoreCase("Default")) {
					List<String> payIdLst = userdao.getActiveMerchantList(user.getSegment(), user.getRole().getId()).stream().map(Merchants::getPayId).collect(Collectors.toList());
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
				paramConditionLst.add(new BasicDBObject(FieldType.CUST_EMAIL.getName(), customerEmail.trim()));
				isParameterised = true;
			}

			if (!customerPhone.isEmpty()) {
				paramConditionLst.add(new BasicDBObject(FieldType.CUST_PHONE.getName(), customerPhone.trim()));
				isParameterised = true;
			}

			if (!StringUtils.isBlank(ipAdress)) {
				paramConditionLst.add(new BasicDBObject(FieldType.INTERNAL_CUST_IP.getName(), ipAdress.trim()));
//isParameterised = true;
			}

			if (StringUtils.isNotEmpty(totalAmount)) {

				String[] total = StringUtils.split(totalAmount, ".");
				if (totalAmount.equals("0")) {

					logger.info(" totalAmount, @@@@@@@@@@@@@@AAAA>>>>>> = " + totalAmount);
					paramConditionLst.add(new BasicDBObject(FieldType.TOTAL_AMOUNT.getName(),null));

				} else {

					if (total.length == 1) {
						totalAmount = totalAmount + ".00";
						logger.info(" totalAmount, @@@@@@@@@@BBBBB>>>>>> = " + totalAmount);
					}
					paramConditionLst.add(new BasicDBObject(FieldType.TOTAL_AMOUNT.getName(), totalAmount));
					logger.info("Inside txnReports.java @@@@@AAA totalAmount ======================= = "
							+ totalAmount);
				}

//				if (total.length == 1) {
//					totalAmount = totalAmount + ".00";
//					logger.info(" totalAmount, ================================ = " + totalAmount);
//				}
//				paramConditionLst.add(new BasicDBObject(FieldType.TOTAL_AMOUNT.getName(), totalAmount));
//				logger.info(
//						"Inside txnReports.java  totalAmount, ==========s========================= = " + totalAmount);

			}

			/*
			 * if (!totalAmount.isEmpty()) { paramConditionLst.add(new
			 * BasicDBObject(FieldType.TOTAL_AMOUNT.getName(), totalAmount.trim()));
			 * isParameterised = true; }
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

			List<BasicDBObject> allConditionQueryList = new ArrayList<BasicDBObject>();

// For Initiated txns
			if (type.equalsIgnoreCase("initiated")) {

// Below variables for initiative logic
				int totaltxns = 0;
				int totalSaleSuccCount = 0;
				int totalSalefailCount = 0;
				int totalSalePendingCount = 0;
				int totalSaleTOCount = 0;
				int totalSaleCancelledCount = 0;
				int totalSaleInvalidCount = 0;
				int totalRefundSuccCount = 0;
				int totalRefundFailCount = 0;
				Double totalTxnAmount = 0.00, totalSalefailAmount = 0.00, totalSalePendingAmount = 0.00,
						totalSaleSuccAmount = 0.00;
				Double totalSaleTOAmount = 0.00, totalSaleCancelledAmount = 0.00, totalRefundSuccAmount = 0.00,
						totalRefundFailAmount = 0.00;
				Double totalSaleInvalidAmount = 0.00;

// If status Settled is selected, don't need to calculate initiated data
				if (isSettled) {
					finalMap.put("totalSaleSuccAmount", String.format("%.2f", totalSaleSuccAmount));
					finalMap.put("totalSaleSuccCount", String.format("%d", totalSaleSuccCount));
					finalMap.put("totalSalefailAmount", String.format("%.2f", totalSalefailAmount));
					finalMap.put("totalSalefailCount", String.format("%d", totalSalefailCount));
					finalMap.put("totalSalePendingAmount", String.format("%.2f", totalSalePendingAmount));
					finalMap.put("totalSalePendingCount", String.format("%d", totalSalePendingCount));
					finalMap.put("totalSaleTOAmount", String.format("%.2f", totalSaleTOAmount));
					finalMap.put("totalSaleTOCount", String.format("%d", totalSaleTOCount));
					finalMap.put("totalSaleCancelledAmount", String.format("%.2f", totalSaleCancelledAmount));
					finalMap.put("totalSaleCancelledCount", String.format("%d", totalSaleCancelledCount));
					finalMap.put("totalSaleInvalidAmount", String.format("%.2f", totalSaleInvalidAmount));
					finalMap.put("totalSaleInvalidCount", String.format("%d", totalSaleInvalidCount));
					finalMap.put("totalRefundSuccAmount", String.format("%.2f", totalRefundSuccAmount));
					finalMap.put("totalRefundSuccCount", String.format("%d", totalRefundSuccCount));
					finalMap.put("totalRefundFailAmount", String.format("%.2f", totalRefundFailAmount));
					finalMap.put("totalRefundFailCount", String.format("%d", totalRefundFailCount));
					finalMap.put("totalTxnAmount", String.format("%.2f", totalTxnAmount));
					finalMap.put("totaltxns", String.format("%d", totaltxns));
					return finalMap;
				}

				if (!aliasStatus.equalsIgnoreCase("ALL")) {
					List<String> aliasStatusList = Arrays.asList(aliasStatus.split(","));
					for (String status : aliasStatusList) {
						aliasStatusConditionLst.add(new BasicDBObject(FieldType.ALIAS_STATUS.getName(), status.trim()));
					}
					transactionStatusQuery.append("$or", aliasStatusConditionLst);
				}

				if (!isParameterised) {
					if (!saleDateQuery.isEmpty()) {
						allConditionQueryList.add(saleDateQuery);
					}
					if (!saleDateIndexConditionQuery.isEmpty()) {
						allConditionQueryList.add(saleDateIndexConditionQuery);
					}
				}

				List<BasicDBObject> finalList = new ArrayList<BasicDBObject>();

				if (!allParamQuery.isEmpty()) {
					finalList.add(allParamQuery);
				}

				if (!allConditionQueryList.isEmpty()) {
					BasicDBObject allConditionQueryObj = new BasicDBObject("$and", allConditionQueryList);
					if (!allConditionQueryObj.isEmpty()) {
						finalList.add(allConditionQueryObj);
					}
				}

				if (!acquirerQuery.isEmpty()) {
					finalList.add(acquirerQuery);
				}

				if (!mopTypeQuery.isEmpty()) {
					finalList.add(mopTypeQuery);
				}
				if (!paymentTypeQuery.isEmpty()) {
					finalList.add(paymentTypeQuery);
				}
				if (!transactionStatusQuery.isEmpty()) {
					finalList.add(transactionStatusQuery);
				}

				if (!transactionTypeQuery.isEmpty()) {
					finalList.add(transactionTypeQuery);
				}

				BasicDBObject finalquery = new BasicDBObject("$and", finalList);
				logger.info("Inside TxnReports , finalquery = " + finalquery);

				BasicDBObject match = new BasicDBObject("$match", finalquery);
				Document firstGroup = new Document("_id",
						new Document("PG_REF_NUM", "$PG_REF_NUM").append("STATUS", "$STATUS"));
				BasicDBObject firstGroupObject = new BasicDBObject(firstGroup);
				BasicDBObject secondGroup = new BasicDBObject("$last", "$$ROOT");
				BasicDBObject groupQuery = new BasicDBObject("$group",
						firstGroupObject.append("contentList", secondGroup));

				List<Bson> pipeline = Arrays.asList(match, groupQuery);

				AggregateIterable<Document> output = coll.aggregate(pipeline);
				output.allowDiskUse(true);
				MongoCursor<Document> cursor = output.iterator();

				while (cursor.hasNext()) {

					Document dbobj = cursor.next();
					dbobj = new ObjectMapper().convertValue(dbobj.get("contentList"), Document.class);
					String aliasStat = dbobj.getString(FieldType.ALIAS_STATUS.getName());

// Skip count for Settled Transactions
					if (null != aliasStat && aliasStat.equals(StatusType.SETTLED_SETTLE.getName())) {
						continue;
					}

					totaltxns = totaltxns + 1;
					Double totalAmt = 0.00;
					if (null != dbobj.get(FieldType.TOTAL_AMOUNT.getName())) {
						totalAmt = Double.valueOf(dbobj.get(FieldType.TOTAL_AMOUNT.getName()).toString());
						totalTxnAmount = totalTxnAmount + totalAmt;
					} else if (null != dbobj.get(FieldType.AMOUNT.getName())) {
						totalAmt = Double.valueOf(dbobj.get(FieldType.AMOUNT.getName()).toString());
						totalTxnAmount = totalTxnAmount + totalAmt;
					} else {
						totalTxnAmount = totalTxnAmount + totalAmt;
					}

					if (dbobj.getString(FieldType.ORIG_TXNTYPE.getName())
							.equalsIgnoreCase(TransactionType.SALE.getName())) {

						switch (aliasStat) {
						case "Captured":
							totalSaleSuccCount = totalSaleSuccCount + 1;
							totalSaleSuccAmount = totalSaleSuccAmount + totalAmt;
							break;
						case "Pending":
							totalSalePendingCount = totalSalePendingCount + 1;
							totalSalePendingAmount = totalSalePendingAmount + totalAmt;
							break;
						case "Failed":
							totalSalefailCount = totalSalefailCount + 1;
							totalSalefailAmount = totalSalefailAmount + totalAmt;
							break;
						case "Invalid":
							totalSaleInvalidCount = totalSaleInvalidCount + 1;
							totalSaleInvalidAmount = totalSaleInvalidAmount + totalAmt;
							break;
						case "Cancelled":
							totalSaleCancelledCount = totalSaleCancelledCount + 1;
							totalSaleCancelledAmount = totalSaleCancelledAmount + totalAmt;
							break;
						default:
// code block
						}

					} else {
						switch (aliasStat) {
						case "Captured":
							totalRefundSuccCount = totalRefundSuccCount + 1;
							totalRefundSuccAmount = totalRefundSuccAmount + totalAmt;
							break;
						case "Settled":
							break;
						default:
							totalRefundFailCount = totalRefundFailCount + 1;
							totalRefundFailAmount = totalRefundFailAmount + totalAmt;
						}

					}

				}

				finalMap.put("totalSaleSuccAmount", String.format("%.2f", totalSaleSuccAmount));
				finalMap.put("totalSaleSuccCount", String.format("%d", totalSaleSuccCount));
				finalMap.put("totalSalefailAmount", String.format("%.2f", totalSalefailAmount));
				finalMap.put("totalSalefailCount", String.format("%d", totalSalefailCount));
				finalMap.put("totalSalePendingAmount", String.format("%.2f", totalSalePendingAmount));
				finalMap.put("totalSalePendingCount", String.format("%d", totalSalePendingCount));
				finalMap.put("totalSaleTOAmount", String.format("%.2f", totalSaleTOAmount));
				finalMap.put("totalSaleTOCount", String.format("%d", totalSaleTOCount));
				finalMap.put("totalSaleCancelledAmount", String.format("%.2f", totalSaleCancelledAmount));
				finalMap.put("totalSaleCancelledCount", String.format("%d", totalSaleCancelledCount));
				finalMap.put("totalSaleInvalidAmount", String.format("%.2f", totalSaleInvalidAmount));
				finalMap.put("totalSaleInvalidCount", String.format("%d", totalSaleInvalidCount));
				finalMap.put("totalRefundSuccAmount", String.format("%.2f", totalRefundSuccAmount));
				finalMap.put("totalRefundSuccCount", String.format("%d", totalRefundSuccCount));
				finalMap.put("totalRefundFailAmount", String.format("%.2f", totalRefundFailAmount));
				finalMap.put("totalRefundFailCount", String.format("%d", totalRefundFailCount));
				finalMap.put("totalTxnAmount", String.format("%.2f", totalTxnAmount));
				finalMap.put("totaltxns", String.format("%d", totaltxns));
				return finalMap;

			}
// For Settled
			else {

// Below variables for Settled logic
				int totalSettleCount = 0;
				int totalSaleCCcount = 0;
				int totalSaleDCcount = 0;
				int totalSaleNBcount = 0;
				int totalSalewalletcount = 0;
				int totalSaleUPIcount = 0;
				int totalSaleCount = 0;
				int totalRfCCcount = 0;
				int totalRfDCcount = 0;
				int totalRfNBcount = 0;
				int totalRfwalletcount = 0;
				int totalRfUPIcount = 0;
				int totalRfCount = 0;

				Double totalSettleAmount = 0.00, totalSaleCCAmount = 0.00, totalSaleDCAmount = 0.00,
						totalSaleNBAmount = 0.00;
				Double totalSalewalletAmount = 0.00, totalSaleUPIAmount = 0.00, totalSaleAmount = 0.00,
						totalRfCCAmount = 0.00;
				Double totalRfDCAmount = 0.00, totalRfNBAmount = 0.00, totalRfwalletAmount = 0.00,
						totalRfUPIAmount = 0.00;
				Double totalRfAmount = 0.00;

				if (isSettled) {

					if (!isParameterised) {
						if (!saleDateQuery.isEmpty()) {
							allConditionQueryList.add(saleDateQuery);
						}
						if (!saleDateIndexConditionQuery.isEmpty()) {
							allConditionQueryList.add(saleDateIndexConditionQuery);
						}
					}

					transactionStatusQuery.append(FieldType.ALIAS_STATUS.getName(), "Settled");

					List<BasicDBObject> finalList = new ArrayList<BasicDBObject>();

					if (!allParamQuery.isEmpty()) {
						finalList.add(allParamQuery);
					}

					if (!allConditionQueryList.isEmpty()) {
						BasicDBObject allConditionQueryObj = new BasicDBObject("$and", allConditionQueryList);
						if (!allConditionQueryObj.isEmpty()) {
							finalList.add(allConditionQueryObj);
						}
					}

					if (!acquirerQuery.isEmpty()) {
						finalList.add(acquirerQuery);
					}

					if (!mopTypeQuery.isEmpty()) {
						finalList.add(mopTypeQuery);
					}
					if (!paymentTypeQuery.isEmpty()) {
						finalList.add(paymentTypeQuery);
					}
					if (!transactionStatusQuery.isEmpty()) {
						finalList.add(transactionStatusQuery);
					}

					if (!transactionTypeQuery.isEmpty()) {
						finalList.add(transactionTypeQuery);
					}

					BasicDBObject finalquery = new BasicDBObject("$and", finalList);
					logger.info("Inside TxnReports , finalquery = " + finalquery);

					BasicDBObject match = new BasicDBObject("$match", finalquery);
					List<BasicDBObject> pipeline = Arrays.asList(match);

					AggregateIterable<Document> output = coll.aggregate(pipeline);
					output.allowDiskUse(true);
					MongoCursor<Document> cursor = output.iterator();

					while (cursor.hasNext()) {

						Document dbobj = cursor.next();
						String paymentCode = dbobj.getString(FieldType.PAYMENT_TYPE.getName());

						totalSettleCount = totalSettleCount + 1;
						Double thisAmt = 0.00;
						if (null != dbobj.get(FieldType.TOTAL_AMOUNT.getName())) {
							thisAmt = Double.valueOf(dbobj.get(FieldType.TOTAL_AMOUNT.getName()).toString());
							totalSettleAmount = totalSettleAmount + thisAmt;
						} else {
							thisAmt = Double.valueOf(dbobj.get(FieldType.AMOUNT.getName()).toString());
							totalSettleAmount = totalSettleAmount + thisAmt;
						}

						if (dbobj.getString(FieldType.ORIG_TXNTYPE.getName())
								.equalsIgnoreCase(TransactionType.SALE.getName())) {

							totalSaleAmount = totalSaleAmount + thisAmt;
							totalSaleCount = totalSaleCount + 1;
							switch (paymentCode) {
							case "CC":
								totalSaleCCcount = totalSaleCCcount + 1;
								totalSaleCCAmount = totalSaleCCAmount + thisAmt;
								break;
							case "DC":
								totalSaleDCcount = totalSaleDCcount + 1;
								totalSaleDCAmount = totalSaleDCAmount + thisAmt;
								break;
							case "WL":
								totalSalewalletcount = totalSalewalletcount + 1;
								totalSalewalletAmount = totalSalewalletAmount + thisAmt;
								break;
							case "NB":
								totalSaleNBcount = totalSaleNBcount + 1;
								totalSaleNBAmount = totalSaleNBAmount + thisAmt;
								break;
							case "UP":
								totalSaleUPIcount = totalSaleUPIcount + 1;
								totalSaleUPIAmount = totalSaleUPIAmount + thisAmt;
								break;
							default:
// code block
							}

						} else {

							totalRfAmount = totalRfAmount + thisAmt;
							totalRfCount = totalRfCount + 1;
							switch (paymentCode) {
							case "CC":
								totalRfCCcount = totalRfCCcount + 1;
								totalRfCCAmount = totalRfCCAmount + thisAmt;
								break;
							case "DC":
								totalRfDCcount = totalRfDCcount + 1;
								totalRfDCAmount = totalRfDCAmount + thisAmt;
								break;
							case "WL":
								totalRfwalletcount = totalRfwalletcount + 1;
								totalRfwalletAmount = totalRfwalletAmount + thisAmt;
								break;
							case "NB":
								totalRfNBcount = totalRfNBcount + 1;
								totalRfNBAmount = totalRfNBAmount + thisAmt;
								break;
							case "UP":
								totalRfUPIcount = totalRfUPIcount + 1;
								totalRfUPIAmount = totalRfUPIAmount + thisAmt;
								break;
							default:
// code block
							}
						}

					}

					finalMap.put("totalSettleAmount", String.format("%.2f", totalSettleAmount));
					finalMap.put("totalSettleCount", String.format("%d", totalSettleCount));
					finalMap.put("totalSaleCCAmount", String.format("%.2f", totalSaleCCAmount));
					finalMap.put("totalSaleCCcount", String.format("%d", totalSaleCCcount));
					finalMap.put("totalSaleDCAmount", String.format("%.2f", totalSaleDCAmount));
					finalMap.put("totalSaleDCcount", String.format("%d", totalSaleDCcount));
					finalMap.put("totalSaleNBAmount", String.format("%.2f", totalSaleNBAmount));
					finalMap.put("totalSaleNBcount", String.format("%d", totalSaleNBcount));
					finalMap.put("totalSalewalletAmount", String.format("%.2f", totalSalewalletAmount));
					finalMap.put("totalSalewalletcount", String.format("%d", totalSalewalletcount));
					finalMap.put("totalSaleUPIAmount", String.format("%.2f", totalSaleUPIAmount));
					finalMap.put("totalSaleUPIcount", String.format("%d", totalSaleUPIcount));
					finalMap.put("totalSaleAmount", String.format("%.2f", totalSaleAmount));
					finalMap.put("totalSaleCount", String.format("%d", totalSaleCount));
					finalMap.put("totalRfCCAmount", String.format("%.2f", totalRfCCAmount));
					finalMap.put("totalRfCCcount", String.format("%d", totalRfCCcount));
					finalMap.put("totalRfDCAmount", String.format("%.2f", totalRfDCAmount));
					finalMap.put("totalRfDCcount", String.format("%d", totalRfDCcount));
					finalMap.put("totalRfNBAmount", String.format("%.2f", totalRfNBAmount));
					finalMap.put("totalRfNBcount", String.format("%d", totalRfNBcount));
					finalMap.put("totalRfwalletAmount", String.format("%.2f", totalRfwalletAmount));
					finalMap.put("totalRfwalletcount", String.format("%d", totalRfwalletcount));
					finalMap.put("totalRfUPIAmount", String.format("%.2f", totalRfUPIAmount));
					finalMap.put("totalRfUPIcount", String.format("%d", totalRfUPIcount));
					finalMap.put("totalRfAmount", String.format("%.2f", totalRfAmount));
					finalMap.put("totalRfCount", String.format("%d", totalRfCount));
					return finalMap;

				} else {

					finalMap.put("totalSettleAmount", String.format("%.2f", totalSettleAmount));
					finalMap.put("totalSettleCount", String.format("%d", totalSettleCount));
					finalMap.put("totalSaleCCAmount", String.format("%.2f", totalSaleCCAmount));
					finalMap.put("totalSaleCCcount", String.format("%d", totalSaleCCcount));
					finalMap.put("totalSaleDCAmount", String.format("%.2f", totalSaleDCAmount));
					finalMap.put("totalSaleDCcount", String.format("%d", totalSaleDCcount));
					finalMap.put("totalSaleNBAmount", String.format("%.2f", totalSaleNBAmount));
					finalMap.put("totalSaleNBcount", String.format("%d", totalSaleNBcount));
					finalMap.put("totalSalewalletAmount", String.format("%.2f", totalSalewalletAmount));
					finalMap.put("totalSalewalletcount", String.format("%d", totalSalewalletcount));
					finalMap.put("totalSaleUPIAmount", String.format("%.2f", totalSaleUPIAmount));
					finalMap.put("totalSaleUPIcount", String.format("%d", totalSaleUPIcount));
					finalMap.put("totalSaleAmount", String.format("%.2f", totalSaleAmount));
					finalMap.put("totalSaleCount", String.format("%d", totalSaleCount));
					finalMap.put("totalRfCCAmount", String.format("%.2f", totalRfCCAmount));
					finalMap.put("totalRfCCcount", String.format("%d", totalRfCCcount));
					finalMap.put("totalRfDCAmount", String.format("%.2f", totalRfDCAmount));
					finalMap.put("totalRfDCcount", String.format("%d", totalRfDCcount));
					finalMap.put("totalRfNBAmount", String.format("%.2f", totalRfNBAmount));
					finalMap.put("totalRfNBcount", String.format("%d", totalRfNBcount));
					finalMap.put("totalRfwalletAmount", String.format("%.2f", totalRfwalletAmount));
					finalMap.put("totalRfwalletcount", String.format("%d", totalRfwalletcount));
					finalMap.put("totalRfUPIAmount", String.format("%.2f", totalRfUPIAmount));
					finalMap.put("totalRfUPIcount", String.format("%d", totalRfUPIcount));
					finalMap.put("totalRfAmount", String.format("%.2f", totalRfAmount));
					finalMap.put("totalRfCount", String.format("%d", totalRfCount));
					return finalMap;
				}
			}

		} catch (Exception e) {
			logger.error("Exception in getting initiated or settled results for search txn ", e);
		}
		return finalMap;

	}

	public List<TransactionSearchDownloadObject> searchTransactionForDownloadNew(String merchantPayId,
			String paymentType, String aliasStatus, String currency, String transactionType, String fromDate,
			String toDate, User user, String paymentsRegion, String acquirer, String customerEmail,
			String customerPhone, String mopType, String pgRefNum, String orderId, String tenantId, String ipAddress,
			String totalAmount, String rrn,String channelName,String minAmount,String maxAmount,String columnName,String logicalCondition,String searchText,String columnName1,String logicalCondition1,String searchText1,String newDespositor,String columnName2,String logicalCondition2,String searchText2, String smaId, String maId, String agentId) {
		logger.info("Inside TxnReports , searchPayment download");
		logger.info("ipAddress :======================================= " + ipAddress
				+ " totalAmount :====================================== " + totalAmount);

		List<TransactionSearchDownloadObject> transactionList = new ArrayList<TransactionSearchDownloadObject>();

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
			 * BasicDBObject(FieldType.PAY_ID.getName(), merchantPayId));
			 * 
			 * }
			 */

			if (merchantPayId.equalsIgnoreCase("ALL")) {
				logger.info("TxnReport, MerchantId : " + merchantPayId + ", Segment : " + user.getSegment());
				if (!user.getSegment().equalsIgnoreCase("Default")) {
					List<String> payIdLst = userdao.getActiveMerchantList(user.getSegment(), user.getRole().getId()).stream().map(Merchants::getPayId).collect(Collectors.toList());
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


			if (StringUtils.isNotBlank(smaId) && !StringUtils.equalsIgnoreCase(smaId, "ALL")){
				List<String> payIdLst = userdao.getMerchantBySMAId(smaId).stream().map(User::getPayId).collect(Collectors.toList());
				logger.info("Get PayId List : " + payIdLst);
				if (!payIdLst.isEmpty()) {
					paramConditionLst
							.add(new BasicDBObject(FieldType.PAY_ID.getName(), new BasicDBObject("$in", payIdLst)));
				}
			}

			if (StringUtils.isNotBlank(maId) &&!StringUtils.equalsIgnoreCase(maId, "ALL")){
				List<String> payIdLst = userdao.getMerchantByMAId(maId).stream().map(User::getPayId).collect(Collectors.toList());
				logger.info("Get PayId List : " + payIdLst);
				if (!payIdLst.isEmpty()) {
					paramConditionLst
							.add(new BasicDBObject(FieldType.PAY_ID.getName(), new BasicDBObject("$in", payIdLst)));
				}
			}

			if (StringUtils.isNotBlank(agentId) && !StringUtils.equalsIgnoreCase(agentId, "ALL")){
				List<String> payIdLst = userdao.getMerchantByAgentId(agentId).stream().map(User::getPayId).collect(Collectors.toList());
				logger.info("Get PayId List : " + payIdLst);
				if (!payIdLst.isEmpty()) {
					paramConditionLst
							.add(new BasicDBObject(FieldType.PAY_ID.getName(), new BasicDBObject("$in", payIdLst)));
				}
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
				paramConditionLst.add(new BasicDBObject(FieldType.CUST_EMAIL.getName(), customerEmail.trim()));
			}
			if (!customerPhone.isEmpty()) {
				paramConditionLst.add(new BasicDBObject(FieldType.CUST_PHONE.getName(), customerPhone.trim()));
			}

			if (!currency.equalsIgnoreCase("ALL")) {
				paramConditionLst.add(new BasicDBObject(FieldType.CURRENCY_CODE.getName(), currency));
			} else {

			}

			if (!transactionType.equalsIgnoreCase("ALL")) {
				paramConditionLst.add(new BasicDBObject(FieldType.ORIG_TXNTYPE.getName(), transactionType));
			}

			if (!StringUtils.isBlank(ipAddress)) {
				paramConditionLst.add(new BasicDBObject(FieldType.INTERNAL_CUST_IP.getName(), ipAddress.trim()));
			}

			if (!StringUtils.isBlank(totalAmount)) {
				paramConditionLst.add(new BasicDBObject(FieldType.TOTAL_AMOUNT.getName(), totalAmount.trim()));
			}
			if (!StringUtils.isBlank(rrn)) {
				paramConditionLst.add(new BasicDBObject(FieldType.RRN.getName(), rrn.trim()));
			}

//Added by Deep Singh code start here
			
			logger.info("channelName::: " + channelName );
			logger.info("minAmount::: " + minAmount );
			logger.info("maxAmount::: " + maxAmount );
			logger.info("columnName::: " + columnName );
			logger.info("logicalCondition::: " + logicalCondition );
			logger.info("searchText::: " + searchText );
			
			
			if (channelName.equalsIgnoreCase("All")) {
//				paramConditionLst.add(new BasicDBObject(FieldType.CHANNEL.getName(),
//						new BasicDBObject("$in", Arrays.asList("Fiat", "Crypto"))));
			} else {
				if(channelName.contains(",")) {
					String [] channels=channelName.split(",");
					ArrayList<String> aa=new ArrayList<>();
					for(String channel:channels) {
						aa.add(channel);
					}
					paramConditionLst.add(new BasicDBObject(FieldType.CHANNEL.getName(), new BasicDBObject("$in",aa)));	
				}else {
					paramConditionLst.add(new BasicDBObject(FieldType.CHANNEL.getName(), new BasicDBObject("$in",Arrays.asList(channelName))));
				}
				
			}

//			if (!minAmount.equalsIgnoreCase("")) {
//				paramConditionLst
//						.add(new BasicDBObject(FieldType.TOTAL_AMOUNT.getName(), new BasicDBObject("$gte", minAmount)));
//			}
//
//			if (!maxAmount.equalsIgnoreCase("")) {
//				paramConditionLst
//						.add(new BasicDBObject(FieldType.TOTAL_AMOUNT.getName(), new BasicDBObject("$lte", maxAmount)));
//			}
			if(StringUtils.isNoneBlank(minAmount)&& StringUtils.isNoneBlank(maxAmount)) {
				paramConditionLst.add(
					    new BasicDBObject("$expr",
					    new BasicDBObject("$and", Arrays.asList(new BasicDBObject("$gte", Arrays.asList(new BasicDBObject("$toDouble", "$TOTAL_AMOUNT"), Double.parseDouble(minAmount))),
					                    new BasicDBObject("$lte", Arrays.asList(new BasicDBObject("$toDouble", "$TOTAL_AMOUNT"), Double.parseDouble(maxAmount)))))));
			}

			if (!columnName.equalsIgnoreCase("") && !logicalCondition.equalsIgnoreCase("")
					&& !searchText.equalsIgnoreCase("")) {
				paramConditionLst.add(new BasicDBObject(columnName, new BasicDBObject(logicalCondition, searchText)));
			}
			
			if (!columnName1.equalsIgnoreCase("") && !logicalCondition1.equalsIgnoreCase("")
					&& !searchText1.equalsIgnoreCase("")) {
				paramConditionLst.add(new BasicDBObject(columnName1, new BasicDBObject(logicalCondition1, searchText1)));
			}
			

			if (!columnName2.equalsIgnoreCase("") && !logicalCondition2.equalsIgnoreCase("")
					&& !searchText2.equalsIgnoreCase("")) {
				paramConditionLst.add(new BasicDBObject(columnName2, new BasicDBObject(logicalCondition2, searchText1)));
			}
			
			//Added by Deep Singh code end here
			
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
						if (status.equalsIgnoreCase("REFUND_INITIATED")) {
							aliasStatusConditionLst.add(new BasicDBObject(FieldType.STATUS.getName(), status.trim()));
						} else {
							aliasStatusConditionLst
									.add(new BasicDBObject(FieldType.ALIAS_STATUS.getName(), status.trim()));
						}
						// aliasStatusConditionLst.add(new
						// BasicDBObject(FieldType.ALIAS_STATUS.getName(), status.trim()));
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
			
			List<String>custEmailIdList=new ArrayList<>();
			if (newDespositor.equalsIgnoreCase("true")) {
				
				List<Document> matchCase= Arrays.asList(new Document("$match", 
					    new Document("CUST_EMAIL", 
					    	    new Document("$ne", 
					    	    new BsonNull()))
					    	            .append("CREATE_DATE", 
					    	    new Document("$gte", fromDate)
					    	                .append("$lte", toDate))), 
					    	    new Document("$group", 
					    	    new Document("_id", "$CUST_EMAIL")
					    	            .append("transaction_count", 
					    	    new Document("$sum", 1L))), 
					    	    new Document("$match", 
					    	    new Document("transaction_count", 
					    	    new Document("$eq", 1L))), 
					    	    new Document("$project", 
					    	    new Document("_id", 0L)
					    	            .append("CUST_EMAIL", "$_id")));
				
				MongoCursor<Document> cursor1=coll.aggregate(matchCase).iterator();
				while (cursor1.hasNext()) {
					Document document = (Document) cursor1.next();
					custEmailIdList.add(String.valueOf(document.get("CUST_EMAIL")));
					
				}
				
			}

			BasicDBObject match =null;
			BasicDBObject finalquery =null;
			if (newDespositor.equalsIgnoreCase("true")) {
				
				BasicDBObject dateQuery1 = new BasicDBObject();
				dateQuery1.put(FieldType.CREATE_DATE.getName(),
									BasicDBObjectBuilder.start("$gte", new SimpleDateFormat(fromDate).toLocalizedPattern())
											.add("$lte", new SimpleDateFormat(toDate).toLocalizedPattern()).get());
				//BasicDBObject orderidListserach=new BasicDBObject();
				dateQuery1.put(FieldType.CUST_EMAIL.getName(),new BasicDBObject("$in", custEmailIdList));
				
				
				match=new BasicDBObject("$match", dateQuery1);
				finalquery=match;
			}else {

				 finalquery = new BasicDBObject("$and", fianlList);

				
				match=new BasicDBObject("$match", finalquery);
			}
			
			logger.info("1- Inside TxnReports , newSearchPayment method called, finalquery = " + finalquery);
			
			//BasicDBObject match1 = new BasicDBObject("$match", finalquery);
			BasicDBObject sort = new BasicDBObject("$sort", new BasicDBObject("CREATE_DATE", -1));
			Document firstGroup = new Document("_id", new Document("PG_REF_NUM", "$PG_REF_NUM")
					.append("STATUS", "$STATUS").append("TXNTYPE", "$TXNTYPE"));
			BasicDBObject firstGroupObject = new BasicDBObject(firstGroup);
			BasicDBObject secondGroup = new BasicDBObject("$last", "$$ROOT");
			BasicDBObject groupQuery = new BasicDBObject("$group", firstGroupObject.append("contentList", secondGroup));
			BasicDBObject replacedRoot = new BasicDBObject("newRoot", "$contentList");
			BasicDBObject replaceRootQuery = new BasicDBObject("$replaceRoot", replacedRoot);
			List<Bson> pipeline = Arrays.asList(match, groupQuery, replaceRootQuery, sort);

			AggregateIterable<Document> output = coll.aggregate(pipeline);
			output.allowDiskUse(true);
			MongoCursor<Document> cursor = output.iterator();

			while (cursor.hasNext()) {
				Document dbobj = cursor.next();
				TransactionSearchDownloadObject transReport = new TransactionSearchDownloadObject();
				transReport.setTransactionId(dbobj.getString(FieldType.TXN_ID.toString()));
				transReport.setPgRefNum(dbobj.getString(FieldType.PG_REF_NUM.toString()));
				transReport.setPayId(dbobj.getString(FieldType.PAY_ID.toString()));
				transReport.setTransactionRegion(dbobj.getString(FieldType.PAYMENTS_REGION.toString()));
				transReport.setMerchants(dbobj.getString(CrmFieldType.BUSINESS_NAME.getName()));
				transReport.setPostSettledFlag(dbobj.getString(FieldType.POST_SETTLED_FLAG.getName()));
				transReport.setAcquirerResponseMessage(dbobj.getString(FieldType.PG_TXN_MESSAGE.getName()));
				if (dbobj.getString(FieldType.UDF6.getName()) != null) {
					transReport.setDeltaRefundFlag(dbobj.getString(FieldType.UDF6.getName()));
				} else {
					transReport.setDeltaRefundFlag("");
				}

				// start by deep
				if (dbobj.getString(FieldType.UDF4.getName()) != null) {
					transReport.setUdf4(dbobj.getString(FieldType.UDF4.getName()));
				} else {
					transReport.setUdf4(dbobj.getString(CrmFieldConstants.NA.getValue()));
				}

				if (dbobj.getString(FieldType.UDF5.getName()) != null) {
					transReport.setUdf5(dbobj.getString(FieldType.UDF5.getName()));
				} else {
					transReport.setUdf5(dbobj.getString(CrmFieldConstants.NA.getValue()));
				}

				if (dbobj.getString(FieldType.UDF6.getName()) != null) {
					transReport.setUdf6(dbobj.getString(FieldType.UDF6.getName()));
				} else {
					transReport.setUdf6(dbobj.getString(CrmFieldConstants.NA.getValue()));
				}

				// end by deep

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
					if (dbobj.getString(FieldType.ORIG_TXNTYPE.toString())
							.equalsIgnoreCase(TransactionType.STATUS.getName())) {
						transReport.setTxnType(dbobj.getString(FieldType.TXNTYPE.toString()));
					}
				}
				if ((user.getUserType().equals(UserType.ADMIN) || user.getUserType().equals(UserType.SUBADMIN))
						&& null != dbobj.getString(FieldType.ACQUIRER_TYPE.toString())) {
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
				if (StringUtils.isNotBlank(dbobj.getString(FieldType.TXNTYPE.getName())) && dbobj
						.getString(FieldType.TXNTYPE.getName()).equalsIgnoreCase(TransactionType.REFUND.getName())) {
					transReport.setStatus(dbobj.getString(FieldType.STATUS.toString()));

				} else {

					transReport.setStatus(dbobj.getString(FieldType.ALIAS_STATUS.toString()));
				}
				transReport.setDateFrom(dbobj.getString(FieldType.CREATE_DATE.getName()));

				transReport.setAmount(dbobj.getString(FieldType.AMOUNT.toString()));
				transReport.setOrderId(dbobj.getString(FieldType.ORDER_ID.toString()));
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

				if (dbobj.getString(FieldType.REFUND_ORDER_ID.toString()) != null) {
					transReport.setRefundOrderId(dbobj.getString(FieldType.REFUND_ORDER_ID.toString()));
				} else {
					transReport.setRefundOrderId(CrmFieldConstants.NA.getValue());
				}

				if (dbobj.getString(FieldType.REFUND_FLAG.toString()) != null) {
					transReport.setRefundFlag(dbobj.getString(FieldType.REFUND_FLAG.toString()));
				} else {
					transReport.setRefundFlag(CrmFieldConstants.NA.getValue());
				}

				if (dbobj.getString(FieldType.MOP_TYPE.toString()) != null
						&& !dbobj.getString(FieldType.MOP_TYPE.toString()).equalsIgnoreCase("undefined")
						&& dbobj.getString(FieldType.MOP_TYPE.toString()).trim().length() > 0) {
					transReport.setMopType(MopType.getmop(dbobj.getString(FieldType.MOP_TYPE.toString())).getName());
				} else {
					transReport.setMopType(CrmFieldConstants.NA.getValue());
				}

				if (StringUtils.isNotBlank(dbobj.getString(FieldType.PG_TDR_SC.toString()))) {
					transReport.setPgSurchargeAmount(
							String.valueOf( ((Double.valueOf(dbobj.getString(FieldType.PG_TDR_SC.toString()))))));
				} else {
					transReport.setPgSurchargeAmount("0.00");
				}

				if (StringUtils.isNotBlank(dbobj.getString(FieldType.ACQUIRER_TDR_SC.toString()))) {
					transReport.setAcquirerSurchargeAmount(String.valueOf(
							(Double.valueOf(dbobj.getString(FieldType.ACQUIRER_TDR_SC.toString())))));
				} else {
					transReport.setAcquirerSurchargeAmount("0.00");
				}

				if (StringUtils.isNotBlank(dbobj.getString(FieldType.PG_GST.toString()))) {
					transReport.setPgGst(
							String.valueOf( ((Double.valueOf(dbobj.getString(FieldType.PG_GST.toString()))))));
				} else {
					transReport.setPgGst("0.00");
				}

				if (dbobj.getString(FieldType.PSPNAME.toString()) != null) {
					transReport.setPspName((dbobj.getString(FieldType.PSPNAME.toString())));
				} else {
					transReport.setPspName(CrmFieldConstants.NA.getValue());
				}
				if (StringUtils.isNotBlank(dbobj.getString(FieldType.ACQUIRER_GST.toString()))) {
					transReport.setAcquirerGst(String.valueOf(
							(Double.valueOf(dbobj.getString(FieldType.ACQUIRER_GST.toString())))));
				} else {
					transReport.setAcquirerGst("0.00");
				}
				
				if (null != dbobj.getString(FieldType.CURRENCY_CODE.toString())) {
					transReport.setCurrency(currencyCodeDao.getCurrencyNamebyCode(dbobj.getString(FieldType.CURRENCY_CODE.toString())));
				} else {
					transReport.setCurrency(CrmFieldConstants.NA.getValue());
				}

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
				if (dbobj.getString(FieldType.ACCT_ID.toString()) != null) {
					transReport.setBankrefno((dbobj.getString(FieldType.ACCT_ID.toString())));
				} else {
					transReport.setBankrefno(CrmFieldConstants.NA.getValue());
				}
				transReport.setIpAddress(dbobj.getString(FieldType.INTERNAL_CUST_IP.toString()));
				transReport.setCardMask(dbobj.getString(FieldType.CARD_MASK.toString()));
				transReport.setCustomerEmail(dbobj.getString(FieldType.CUST_EMAIL.getName()));
				transReport.setCustomerPhone(dbobj.getString(FieldType.CUST_PHONE.getName()));
				transactionList.add(transReport);
			}
			cursor.close();
			logger.info("Inside TxnReports , searchPayment dwonload, transactionListSize = " + transactionList.size());
			logger.info(" transactionList {} " , transactionList);
			return transactionList.stream().sorted(new Comparator<TransactionSearchDownloadObject>() {

				@Override
				public int compare(TransactionSearchDownloadObject o1, TransactionSearchDownloadObject o2) {
					return o2.getDateFrom().compareTo(o1.getDateFrom());
				}
			}).collect(Collectors.toList());

		} catch (Exception e) {
			logger.error("Exception in search payment download for admin", e);
		}
		return transactionList;
	}

	public List<TransactionSearchDownloadObject> bulkTransactionForDownload(String Pgrefno, String rrn,
			String orderId) {
		logger.info("Inside TxnReports , searchPayment download");

		List<TransactionSearchDownloadObject> transactionList = new ArrayList<TransactionSearchDownloadObject>();

		try {

			List<TransactionSearchNew> Data = new ArrayList<TransactionSearchNew>();
			List<BasicDBObject> rrnList = new ArrayList<BasicDBObject>();
			List<BasicDBObject> pgrefList = new ArrayList<BasicDBObject>();
			List<BasicDBObject> orderIdList = new ArrayList<BasicDBObject>();
			List<BasicDBObject> allConditionQueryList = new ArrayList<BasicDBObject>();
			BasicDBObject rrnQuery = new BasicDBObject();
			BasicDBObject pgrefQuery = new BasicDBObject();
			BasicDBObject orderidQuery = new BasicDBObject();

			if (StringUtils.isNotBlank(rrn)) {

				String rrnArr[] = rrn.split(",");

				for (String rrndata : rrnArr) {
					rrnList.add(new BasicDBObject(FieldType.RRN.getName(), rrndata.trim()));
				}

				rrnQuery.put("$or", rrnList);
				allConditionQueryList.add(rrnQuery);

			}

			if (StringUtils.isNotBlank(Pgrefno)) {

				String PgrefnoArr[] = Pgrefno.split(",");

				for (String Pgrefnodata : PgrefnoArr) {
					pgrefList.add(new BasicDBObject(FieldType.PG_REF_NUM.getName(), Pgrefnodata.trim()));
				}
				pgrefQuery.put("$or", pgrefList);
				allConditionQueryList.add(pgrefQuery);
			}

			if (StringUtils.isNotBlank(orderId)) {

				String orderIdArr[] = orderId.split(",");

				for (String orderIddata : orderIdArr) {
					orderIdList.add(new BasicDBObject(FieldType.ORDER_ID.getName(), orderIddata.trim()));
				}
				orderidQuery.put("$or", orderIdList);
				allConditionQueryList.add(orderidQuery);

			}
			BasicDBObject finalquery = new BasicDBObject("$or", allConditionQueryList);

			MongoDatabase dbIns = mongoInstance.getDB();
			MongoCollection<Document> coll = dbIns.getCollection(
					PropertiesManager.propertiesMap.get(prefix + Constants.TRANSACTION_STATUS_COLLECTION.getValue()));

			BasicDBObject match = new BasicDBObject("$match", finalquery);
			logger.info("Query to get data for status enquiry [Match] = " + match);
			List<BasicDBObject> pipeline = Arrays.asList(match);
			AggregateIterable<Document> output = coll.aggregate(pipeline);
			output.allowDiskUse(true);
			MongoCursor<Document> cursor = output.iterator();

			while (cursor.hasNext()) {
				Document dbobj = cursor.next();
				TransactionSearchDownloadObject transReport = new TransactionSearchDownloadObject();
				transReport.setTransactionId(dbobj.getString(FieldType.TXN_ID.toString()));
				transReport.setPgRefNum(dbobj.getString(FieldType.PG_REF_NUM.toString()));
				transReport.setPayId(dbobj.getString(FieldType.PAY_ID.toString()));
				transReport.setTransactionRegion(dbobj.getString(FieldType.PAYMENTS_REGION.toString()));
				transReport.setMerchants(dbobj.getString(CrmFieldType.BUSINESS_NAME.getName()));
				transReport.setPostSettledFlag(dbobj.getString(FieldType.POST_SETTLED_FLAG.getName()));
				transReport.setAcquirerResponseMessage(dbobj.getString(FieldType.PG_TXN_MESSAGE.getName()));
				if (dbobj.getString(FieldType.UDF6.getName()) != null) {
					transReport.setDeltaRefundFlag(dbobj.getString(FieldType.UDF6.getName()));
				} else {
					transReport.setDeltaRefundFlag("");
				}

				// start by deep
				if (dbobj.getString(FieldType.UDF4.getName()) != null) {
					transReport.setUdf4(dbobj.getString(FieldType.UDF4.getName()));
				} else {
					transReport.setUdf4(dbobj.getString(CrmFieldConstants.NA.getValue()));
				}

				if (dbobj.getString(FieldType.UDF5.getName()) != null) {
					transReport.setUdf5(dbobj.getString(FieldType.UDF5.getName()));
				} else {
					transReport.setUdf5(dbobj.getString(CrmFieldConstants.NA.getValue()));
				}

				if (dbobj.getString(FieldType.UDF6.getName()) != null) {
					transReport.setUdf6(dbobj.getString(FieldType.UDF6.getName()));
				} else {
					transReport.setUdf6(dbobj.getString(CrmFieldConstants.NA.getValue()));
				}

				// end by deep

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
					if (dbobj.getString(FieldType.ORIG_TXNTYPE.toString())
							.equalsIgnoreCase(TransactionType.STATUS.getName())) {
						transReport.setTxnType(dbobj.getString(FieldType.TXNTYPE.toString()));
					}
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

				if (StringUtils.isNotBlank(dbobj.getString(FieldType.TXNTYPE.getName())) && dbobj
						.getString(FieldType.TXNTYPE.getName()).equalsIgnoreCase(TransactionType.REFUND.getName())) {
					transReport.setStatus(dbobj.getString(FieldType.STATUS.toString()));

				} else {

					transReport.setStatus(dbobj.getString(FieldType.ALIAS_STATUS.toString()));
				}
				// transReport.setStatus(dbobj.getString(FieldType.ALIAS_STATUS.toString()));
				transReport.setDateFrom(dbobj.getString(FieldType.CREATE_DATE.getName()));

				transReport.setAmount(dbobj.getString(FieldType.AMOUNT.toString()));
				transReport.setOrderId(dbobj.getString(FieldType.ORDER_ID.toString()));
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

				if (dbobj.getString(FieldType.REFUND_ORDER_ID.toString()) != null) {
					transReport.setRefundOrderId(dbobj.getString(FieldType.REFUND_ORDER_ID.toString()));
				} else {
					transReport.setRefundOrderId(CrmFieldConstants.NA.getValue());
				}

				if (dbobj.getString(FieldType.REFUND_FLAG.toString()) != null) {
					transReport.setRefundFlag(dbobj.getString(FieldType.REFUND_FLAG.toString()));
				} else {
					transReport.setRefundFlag(CrmFieldConstants.NA.getValue());
				}

				if (dbobj.getString(FieldType.MOP_TYPE.toString()) != null
						&& !dbobj.getString(FieldType.MOP_TYPE.toString()).equalsIgnoreCase("undefined")
						&& dbobj.getString(FieldType.MOP_TYPE.toString()).trim().length() > 0) {
					transReport.setMopType(MopType.getmop(dbobj.getString(FieldType.MOP_TYPE.toString())).getName());
				} else {
					transReport.setMopType(CrmFieldConstants.NA.getValue());
				}

				if (StringUtils.isNotBlank(dbobj.getString(FieldType.PG_TDR_SC.toString()))) {
					transReport.setPgSurchargeAmount(
							String.valueOf( ((Double.valueOf(dbobj.getString(FieldType.PG_TDR_SC.toString()))))));
				} else {
					transReport.setPgSurchargeAmount("0.00");
				}

				if (StringUtils.isNotBlank(dbobj.getString(FieldType.ACQUIRER_TDR_SC.toString()))) {
					transReport.setAcquirerSurchargeAmount(String.valueOf(
							(Double.valueOf(dbobj.getString(FieldType.ACQUIRER_TDR_SC.toString())))));
				} else {
					transReport.setAcquirerSurchargeAmount("0.00");
				}

				if (StringUtils.isNotBlank(dbobj.getString(FieldType.PG_GST.toString()))) {
					transReport.setPgGst(
							String.valueOf( ((Double.valueOf(dbobj.getString(FieldType.PG_GST.toString()))))));
				} else {
					transReport.setPgGst("0.00");
				}

				if (StringUtils.isNotBlank(dbobj.getString(FieldType.ACQUIRER_GST.toString()))) {
					transReport.setAcquirerGst(String.valueOf(
							(Double.valueOf(dbobj.getString(FieldType.ACQUIRER_GST.toString())))));
				} else {
					transReport.setAcquirerGst("0.00");
				}
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
				if (dbobj.getString(FieldType.ACCT_ID.toString()) != null) {
					transReport.setBankrefno((dbobj.getString(FieldType.ACCT_ID.toString())));
				} else {
					transReport.setBankrefno(CrmFieldConstants.NA.getValue());
				}
				if (dbobj.getString(FieldType.PSPNAME.toString()) != null) {
					transReport.setPspName((dbobj.getString(FieldType.PSPNAME.toString())));
				} else {
					transReport.setPspName(CrmFieldConstants.NA.getValue());
				}
				transReport.setIpAddress(dbobj.getString(FieldType.INTERNAL_CUST_IP.toString()));
				transReport.setCardMask(dbobj.getString(FieldType.CARD_MASK.toString()));
				transReport.setCustomerEmail(dbobj.getString(FieldType.CUST_EMAIL.getName()));
				transReport.setCustomerPhone(dbobj.getString(FieldType.CUST_PHONE.getName()));
				transactionList.add(transReport);
			}
			cursor.close();
			logger.info("Inside TxnReports , searchPayment dwonload, transactionListSize = " + transactionList.size());
			return transactionList.stream().sorted(new Comparator<TransactionSearchDownloadObject>() {

				@Override
				public int compare(TransactionSearchDownloadObject o1, TransactionSearchDownloadObject o2) {
					return o2.getDateFrom().compareTo(o1.getDateFrom());
				}
			}).collect(Collectors.toList());

		} catch (Exception e) {
			logger.error("Exception in search payment download for admin", e);
		}
		return transactionList;
	}

	/*
	 * public List<TransactionSearchDownloadObject>
	 * bulkTransactionForDownload(String Pgrefno,String rrn ,String orderId) {
	 * logger.info("Inside TxnReports , searchPayment download");
	 * 
	 * 
	 * List<TransactionSearchDownloadObject> transactionList = new
	 * ArrayList<TransactionSearchDownloadObject>();
	 * 
	 * try {
	 * 
	 * 
	 * List<TransactionSearchNew> Data = new ArrayList<TransactionSearchNew>();
	 * List<BasicDBObject> rrnList = new ArrayList<BasicDBObject>();
	 * List<BasicDBObject> pgrefList = new ArrayList<BasicDBObject>();
	 * List<BasicDBObject> orderIdList = new ArrayList<BasicDBObject>();
	 * List<BasicDBObject> allConditionQueryList = new ArrayList<BasicDBObject>();
	 * BasicDBObject rrnQuery = new BasicDBObject(); BasicDBObject pgrefQuery = new
	 * BasicDBObject(); BasicDBObject orderidQuery = new BasicDBObject();
	 * 
	 * if (StringUtils.isNotBlank(rrn)) {
	 * 
	 * String rrnArr[] = rrn.split(",");
	 * 
	 * for (String rrndata : rrnArr) { rrnList.add(new
	 * BasicDBObject(FieldType.RRN.getName(), rrndata.trim())); }
	 * 
	 * rrnQuery.put("$or", rrnList); allConditionQueryList.add(rrnQuery);
	 * 
	 * }
	 * 
	 * if (StringUtils.isNotBlank(Pgrefno)) {
	 * 
	 * String PgrefnoArr[] = Pgrefno.split(",");
	 * 
	 * for (String Pgrefnodata : PgrefnoArr) { pgrefList.add(new
	 * BasicDBObject(FieldType.PG_REF_NUM.getName(), Pgrefnodata.trim())); }
	 * pgrefQuery.put("$or", pgrefList); allConditionQueryList.add(pgrefQuery); }
	 * 
	 * if (StringUtils.isNotBlank(orderId)) {
	 * 
	 * String orderIdArr[] = orderId.split(",");
	 * 
	 * for (String orderIddata : orderIdArr) { orderIdList.add(new
	 * BasicDBObject(FieldType.ORDER_ID.getName(), orderIddata.trim())); }
	 * orderidQuery.put("$or", orderIdList);
	 * allConditionQueryList.add(orderidQuery);
	 * 
	 * } BasicDBObject finalquery = new BasicDBObject("$or", allConditionQueryList);
	 * 
	 * MongoDatabase dbIns = mongoInstance.getDB(); MongoCollection<Document> coll =
	 * dbIns.getCollection( PropertiesManager.propertiesMap.get(prefix +
	 * Constants.TRANSACTION_STATUS_COLLECTION.getValue()));
	 * 
	 * BasicDBObject match = new BasicDBObject("$match", finalquery);
	 * logger.info("Query to get data for status enquiry [Match] = " + match);
	 * List<BasicDBObject> pipeline = Arrays.asList(match);
	 * AggregateIterable<Document> output = coll.aggregate(pipeline);
	 * output.allowDiskUse(true); MongoCursor<Document> cursor = output.iterator();
	 * 
	 * while (cursor.hasNext()) { Document dbobj = cursor.next();
	 * TransactionSearchDownloadObject transReport = new
	 * TransactionSearchDownloadObject();
	 * transReport.setTransactionId(dbobj.getString(FieldType.TXN_ID.toString()));
	 * transReport.setPgRefNum(dbobj.getString(FieldType.PG_REF_NUM.toString()));
	 * transReport.setPayId(dbobj.getString(FieldType.PAY_ID.toString()));
	 * transReport.setTransactionRegion(dbobj.getString(FieldType.PAYMENTS_REGION.
	 * toString()));
	 * transReport.setMerchants(dbobj.getString(CrmFieldType.BUSINESS_NAME.getName()
	 * ));
	 * transReport.setPostSettledFlag(dbobj.getString(FieldType.POST_SETTLED_FLAG.
	 * getName()));
	 * transReport.setAcquirerResponseMessage(dbobj.getString(FieldType.
	 * PG_TXN_MESSAGE.getName())); if (dbobj.getString(FieldType.UDF6.getName()) !=
	 * null) {
	 * transReport.setDeltaRefundFlag(dbobj.getString(FieldType.UDF6.getName())); }
	 * else { transReport.setDeltaRefundFlag(""); }
	 * 
	 * // start by deep if (dbobj.getString(FieldType.UDF4.getName()) != null) {
	 * transReport.setUdf4(dbobj.getString(FieldType.UDF4.getName())); } else {
	 * transReport.setUdf4(dbobj.getString(CrmFieldConstants.NA.getValue())); }
	 * 
	 * if (dbobj.getString(FieldType.UDF5.getName()) != null) {
	 * transReport.setUdf5(dbobj.getString(FieldType.UDF5.getName())); } else {
	 * transReport.setUdf5(dbobj.getString(CrmFieldConstants.NA.getValue())); }
	 * 
	 * if (dbobj.getString(FieldType.UDF6.getName()) != null) {
	 * transReport.setUdf6(dbobj.getString(FieldType.UDF6.getName())); } else {
	 * transReport.setUdf6(dbobj.getString(CrmFieldConstants.NA.getValue())); }
	 * 
	 * // end by deep
	 * 
	 * String payid = (String) dbobj.get(FieldType.PAY_ID.getName());
	 * 
	 * User user1 = new User();
	 * 
	 * if (userMap.get(payid) != null && !userMap.get(payid).getPayId().isEmpty()) {
	 * user1 = userMap.get(payid); } else { user1 = userdao.findPayId(payid);
	 * userMap.put(payid, user1); }
	 * 
	 * if (user1 == null) {
	 * transReport.setMerchants(CrmFieldConstants.NA.getValue()); } else {
	 * transReport.setMerchants(user1.getBusinessName()); }
	 * 
	 * if (null != dbobj.getString(FieldType.ORIG_TXNTYPE.toString())) {
	 * 
	 * transReport.setTxnType(dbobj.getString(FieldType.ORIG_TXNTYPE.toString()));
	 * if (dbobj.getString(FieldType.ORIG_TXNTYPE.toString())
	 * .equalsIgnoreCase(TransactionType.STATUS.getName())) {
	 * transReport.setTxnType(dbobj.getString(FieldType.TXNTYPE.toString())); } } if
	 * ( null != dbobj.getString(FieldType.ACQUIRER_TYPE.toString())) {
	 * transReport.setAcquirerType(dbobj.getString(FieldType.ACQUIRER_TYPE.toString(
	 * ))); } else { transReport.setAcquirerType(CrmFieldConstants.NA.getValue()); }
	 * 
	 * if (null != dbobj.getString(FieldType.PAYMENT_TYPE.toString())) {
	 * transReport.setPaymentMethods(
	 * PaymentType.getpaymentName(dbobj.getString(FieldType.PAYMENT_TYPE.toString())
	 * )); } else { transReport.setPaymentMethods(CrmFieldConstants.NA.getValue());
	 * }
	 * 
	 * transReport.setStatus(dbobj.getString(FieldType.ALIAS_STATUS.toString()));
	 * transReport.setDateFrom(dbobj.getString(FieldType.CREATE_DATE.getName()));
	 * 
	 * transReport.setAmount(dbobj.getString(FieldType.AMOUNT.toString()));
	 * transReport.setOrderId(dbobj.getString(FieldType.ORDER_ID.toString())); if
	 * (dbobj.getString(FieldType.TOTAL_AMOUNT.toString()) != null) {
	 * transReport.setTotalAmount(dbobj.getString(FieldType.TOTAL_AMOUNT.toString())
	 * ); } else { transReport.setTotalAmount(""); }
	 * 
	 * if (dbobj.getString(FieldType.ACQ_ID.toString()) != null) {
	 * transReport.setAcqId(dbobj.getString(FieldType.ACQ_ID.toString())); } else {
	 * transReport.setAcqId(CrmFieldConstants.NA.getValue()); }
	 * 
	 * if (dbobj.getString(FieldType.RRN.toString()) != null) {
	 * transReport.setRrn(dbobj.getString(FieldType.RRN.toString())); } else {
	 * transReport.setRrn(CrmFieldConstants.NA.getValue()); }
	 * 
	 * if (dbobj.getString(FieldType.REFUND_ORDER_ID.toString()) != null) {
	 * transReport.setRefundOrderId(dbobj.getString(FieldType.REFUND_ORDER_ID.
	 * toString())); } else {
	 * transReport.setRefundOrderId(CrmFieldConstants.NA.getValue()); }
	 * 
	 * if (dbobj.getString(FieldType.REFUND_FLAG.toString()) != null) {
	 * transReport.setRefundFlag(dbobj.getString(FieldType.REFUND_FLAG.toString()));
	 * } else { transReport.setRefundFlag(CrmFieldConstants.NA.getValue()); }
	 * 
	 * if (dbobj.getString(FieldType.MOP_TYPE.toString()) != null &&
	 * !dbobj.getString(FieldType.MOP_TYPE.toString()).equalsIgnoreCase("undefined")
	 * && dbobj.getString(FieldType.MOP_TYPE.toString()).trim().length() > 0) {
	 * transReport.setMopType(MopType.getmop(dbobj.getString(FieldType.MOP_TYPE.
	 * toString())).getName()); } else {
	 * transReport.setMopType(CrmFieldConstants.NA.getValue()); }
	 * 
	 * if (StringUtils.isNotBlank(dbobj.getString(FieldType.PG_TDR_SC.toString())))
	 * { transReport.setPgSurchargeAmount( String.format("%.2f",
	 * ((Double.valueOf(dbobj.getString(FieldType.PG_TDR_SC.toString())))))); } else
	 * { transReport.setPgSurchargeAmount("0.00"); }
	 * 
	 * if
	 * (StringUtils.isNotBlank(dbobj.getString(FieldType.ACQUIRER_TDR_SC.toString())
	 * )) { transReport.setAcquirerSurchargeAmount(String.format("%.2f",
	 * (Double.valueOf(dbobj.getString(FieldType.ACQUIRER_TDR_SC.toString()))))); }
	 * else { transReport.setAcquirerSurchargeAmount("0.00"); }
	 * 
	 * if (StringUtils.isNotBlank(dbobj.getString(FieldType.PG_GST.toString()))) {
	 * transReport.setPgGst( String.format("%.2f",
	 * ((Double.valueOf(dbobj.getString(FieldType.PG_GST.toString())))))); } else {
	 * transReport.setPgGst("0.00"); }
	 * 
	 * if
	 * (StringUtils.isNotBlank(dbobj.getString(FieldType.ACQUIRER_GST.toString())))
	 * { transReport.setAcquirerGst(String.format("%.2f",
	 * (Double.valueOf(dbobj.getString(FieldType.ACQUIRER_GST.toString()))))); }
	 * else { transReport.setAcquirerGst("0.00"); } if
	 * (String.valueOf(dbobj.get(FieldType.PAYMENT_TYPE.getName()))
	 * .equalsIgnoreCase(PaymentTypeUI.CREDIT_CARD.getCode()) ||
	 * String.valueOf(dbobj.get(FieldType.PAYMENT_TYPE.getName()))
	 * .equalsIgnoreCase(PaymentTypeUI.DEBIT_CARD.getCode()) ||
	 * String.valueOf(dbobj.get(FieldType.PAYMENT_TYPE.getName()))
	 * .equalsIgnoreCase(PaymentTypeUI.NET_BANKING.getCode())) {
	 * transReport.setCardHolderType(String.valueOf(
	 * dbobj.get(FieldType.CARD_HOLDER_TYPE.getName()) == null ?
	 * CrmFieldConstants.NA.getValue() :
	 * dbobj.get(FieldType.CARD_HOLDER_TYPE.getName()))); } else {
	 * transReport.setCardHolderType(CrmFieldConstants.NA.getValue()); } if
	 * (dbobj.getString(FieldType.ACCT_ID.toString()) != null) {
	 * transReport.setBankrefno((dbobj.getString(FieldType.ACCT_ID.toString()))); }
	 * else { transReport.setBankrefno(CrmFieldConstants.NA.getValue()); }
	 * transReport.setIpAddress(dbobj.getString(FieldType.INTERNAL_CUST_IP.toString(
	 * )));
	 * transReport.setCardMask(dbobj.getString(FieldType.CARD_MASK.toString()));
	 * transReport.setCustomerEmail(dbobj.getString(FieldType.CUST_EMAIL.getName()))
	 * ;
	 * transReport.setCustomerPhone(dbobj.getString(FieldType.CUST_PHONE.getName()))
	 * ; transactionList.add(transReport); } cursor.close(); logger.
	 * info("Inside TxnReports , searchPayment dwonload, transactionListSize = " +
	 * transactionList.size()); return transactionList.stream().sorted(new
	 * Comparator<TransactionSearchDownloadObject>() {
	 * 
	 * @Override public int compare(TransactionSearchDownloadObject o1,
	 * TransactionSearchDownloadObject o2) { return
	 * o2.getDateFrom().compareTo(o1.getDateFrom()); }
	 * }).collect(Collectors.toList());
	 * 
	 * } catch (Exception e) {
	 * logger.error("Exception in search payment download for admin", e); } return
	 * transactionList; }
	 */

	public int newSearchPaymentCountCustom(String pgRefNum, String orderId, String customerEmail, String customerPhone,
			String paymentType, String status, String currency, String transactionType, String mopType, String acquirer,
			String merchantPayId, String fromDate, String toDate, User user, int start, int length, String tenantId) {

		try {

			MongoDatabase dbIns = mongoInstance.getDB();
			MongoCollection<Document> coll = dbIns
					.getCollection(PropertiesManager.propertiesMap.get(prefix + Constants.COLLECTION_NAME.getValue()));

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
					List<String> payIdLst = userdao.getActiveMerchantList(user.getSegment(), user.getRole().getId()).stream().map(Merchants::getPayId).collect(Collectors.toList());
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
			if (!status.equalsIgnoreCase("ALL")) {
				transactionStatusQuery.append(FieldType.STATUS.getName(), status);
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
			logger.info("Inside TxnReports , newsearchPaymentCount custom report, finalquery = " + finalquery);

			long count = coll.countDocuments(finalquery);
			int cnt = Integer.valueOf(Long.toString(count));

			return cnt;

		} catch (Exception e) {
			logger.error("Exception in custom search payment for admin", e);
		}
		return 0;
	}

	public List<TransactionSearchNew> newSearchPaymentCustom(String pgRefNum, String orderId, String customerEmail,
			String customerPhone, String paymentType, String status, String currency, String transactionType,
			String mopType, String acquirer, String merchantPayId, String fromDate, String toDate, User user, int start,
			int length, String tenantId) {

		List<TransactionSearchNew> transactionList = new ArrayList<TransactionSearchNew>();

		try {

			MongoDatabase dbIns = mongoInstance.getDB();
			MongoCollection<Document> coll = dbIns
					.getCollection(PropertiesManager.propertiesMap.get(prefix + Constants.COLLECTION_NAME.getValue()));

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
					List<String> payIdLst = userdao.getActiveMerchantList(user.getSegment(), user.getRole().getId()).stream().map(Merchants::getPayId).collect(Collectors.toList());
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
			if (!status.equalsIgnoreCase("ALL")) {
				transactionStatusQuery.append(FieldType.STATUS.getName(), status);
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

			logger.info(" Inside TxnReports , newSearchPayment[newSearchPaymentCustom] , finalquery = " + finalquery);

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
				transReport.setIpaddress(dbobj.getString(FieldType.INTERNAL_CUST_IP.toString()));
				transReport.setCardMask(dbobj.getString(FieldType.CARD_MASK.toString()));
				transReport.setPgRefNum(dbobj.getString(FieldType.PG_REF_NUM.toString()));
				transReport.setPayId(dbobj.getString(FieldType.PAY_ID.toString()));

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
				} else {
					if (null != dbobj.getString(FieldType.TXNTYPE.toString())) {
						transReport.setTxnType(dbobj.getString(FieldType.TXNTYPE.toString()));
					}
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
				transReport.setStatus(dbobj.getString(FieldType.STATUS.toString()));
				transReport.setDateFrom(dbobj.getString(FieldType.CREATE_DATE.getName()));
				transReport.setAmount(dbobj.getString(FieldType.AMOUNT.toString()));
				transReport.setTotalAmount(dbobj.getString(FieldType.TOTAL_AMOUNT.toString()));
				transReport.setOrderId(dbobj.getString(FieldType.ORDER_ID.toString()));
				if (StringUtils.isNotBlank(dbobj.getString(FieldType.REFUND_ORDER_ID.toString()))) {
					transReport.setRefundOrderId(dbobj.getString(FieldType.REFUND_ORDER_ID.toString()));
				} else {
					transReport.setRefundOrderId(CrmFieldConstants.NA.getValue());
				}
				transReport.setoId(dbobj.getString(FieldType.OID.toString()));

				if (null != dbobj.getString(FieldType.CURRENCY_CODE.toString())) {
					transReport.setCurrency(
							currencyCodeDao.getCurrencyNamebyCode(dbobj.getString(FieldType.CURRENCY_CODE.toString())));
				} else {
					transReport.setCurrency(CrmFieldConstants.NA.getValue());
				}

				// For Super Admin
				transReport.setPgSurchargeAmount(
						String.format("%.2f", ((Double.valueOf(dbobj.getString(FieldType.PG_TDR_SC.toString()))))));
				transReport.setAcquirerSurchargeAmount(
						String.format("%.2f", (Double.valueOf(dbobj.getString(FieldType.ACQUIRER_TDR_SC.toString())))));
				transReport.setPgGst(
						String.format("%.2f", ((Double.valueOf(dbobj.getString(FieldType.PG_GST.toString()))))));
				transReport.setAcquirerGst(
						String.format("%.2f", (Double.valueOf(dbobj.getString(FieldType.ACQUIRER_GST.toString())))));

				transactionList.add(transReport);
			}
			cursor.close();
			logger.info("Inside TxnReports , searchPayment custom , transactionListSize = " + transactionList.size());
			return transactionList;

		} catch (Exception e) {
			logger.error("Exception in search payment for admin", e);
		}
		return transactionList;
	}

	public int searchPaymentCountSplitPayment(String pgRefNum, String orderId, String customerEmail,
			String merchantPayId, String paymentType, String Userstatus, String currency, String transactionType,
			String fromDate, String toDate, User user, String phoneNo, String mopType) {

		logger.info("Inside TxnReports , searchPaymentCountSplitPayment " + merchantPayId);
		boolean isParameterised = false;
		try {
			List<BasicDBObject> paramConditionLst = new ArrayList<BasicDBObject>();
			List<BasicDBObject> statusCondition = new ArrayList<BasicDBObject>();

			List<BasicDBObject> dateCondition = new ArrayList<BasicDBObject>();

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

				dateCondition.add(new BasicDBObject(FieldType.CREATE_DATE.getName(),
						BasicDBObjectBuilder.start("$gte", new SimpleDateFormat(fromDate).toLocalizedPattern())
								.add("$lte", new SimpleDateFormat(currentDate).toLocalizedPattern()).get()));

				dateCondition.add(new BasicDBObject(FieldType.SETTLEMENT_DATE.getName(),
						BasicDBObjectBuilder.start("$gte", new SimpleDateFormat(fromDate).toLocalizedPattern())
								.add("$lte", new SimpleDateFormat(currentDate).toLocalizedPattern()).get()));

				dateQuery.put("$or", dateCondition);

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
				dateIndexConditionList.add(new BasicDBObject("SETTLEMENT_DATE_INDEX", dateIndex));
			}

			if (!Userstatus.equalsIgnoreCase(StatusType.SETTLED_SETTLE.getName())) {
				if (StringUtils.isNotBlank(PropertiesManager.propertiesMap.get(Constants.USE_DATE_INDEX.getValue()))
						&& PropertiesManager.propertiesMap.get(Constants.USE_DATE_INDEX.getValue())
								.equalsIgnoreCase("Y")) {
					dateIndexConditionQuery.append("$or", dateIndexConditionList);
				}
			} else {
				if (StringUtils.isNotBlank(PropertiesManager.propertiesMap.get(Constants.USE_DATE_INDEX.getValue()))
						&& PropertiesManager.propertiesMap.get(Constants.USE_DATE_INDEX.getValue())
								.equalsIgnoreCase("Y")) {
					dateIndexConditionQuery.append("$or", dateIndexConditionList);
				}
			}

			if (merchantPayId.equalsIgnoreCase("ALL")) {
				List<String> payIdLst = userdao.getActiveMerchantList(user.getSegment(), user.getRole().getId()).stream().map(Merchants::getPayId).collect(Collectors.toList());
				logger.info("Get PayId For SplitPayment Merchant : " + payIdLst);
				if (payIdLst.size() > 0) {
					isParameterised = true;
					paramConditionLst
							.add(new BasicDBObject(FieldType.PAY_ID.getName(), new BasicDBObject("$in", payIdLst)));
				}
			} else {
				isParameterised = true;
				paramConditionLst.add(new BasicDBObject(FieldType.PAY_ID.getName(), merchantPayId));
			}

			if (user.getUserType().equals(UserType.RESELLER)) {
				paramConditionLst.add(new BasicDBObject(FieldType.RESELLER_ID.getName(), user.getResellerId()));
			}

			if (!pgRefNum.isEmpty()) {
				isParameterised = true;
				paramConditionLst.add(new BasicDBObject(FieldType.PG_REF_NUM.getName(), pgRefNum));
			}
			if (!orderId.isEmpty()) {
				isParameterised = true;
				paramConditionLst.add(new BasicDBObject(FieldType.ORDER_ID.getName(), orderId));
			}
			if (!phoneNo.isEmpty()) {
				isParameterised = true;
				paramConditionLst.add(new BasicDBObject(FieldType.CUST_PHONE.getName(), phoneNo));
			}
			if (!mopType.equalsIgnoreCase("ALL")) {
				isParameterised = true;
				paramConditionLst.add(new BasicDBObject(FieldType.MOP_TYPE.getName(), mopType));
			}
			if (!customerEmail.isEmpty()) {
				isParameterised = true;
				paramConditionLst.add(new BasicDBObject(FieldType.CUST_EMAIL.getName(), customerEmail));
			}

			if (!Userstatus.equalsIgnoreCase("ALL")) {
				isParameterised = true;
				statusCondition.add(new BasicDBObject(FieldType.STATUS.getName(), Userstatus));
			} else {

			}

			if (!StatusType.SETTLED_SETTLE.getName().equalsIgnoreCase(Userstatus)) {
				statusCondition.add(new BasicDBObject(FieldType.STATUS.getName(), StatusType.SETTLED_SETTLE.getName()));
			}

			if (!statusCondition.isEmpty()) {
				BasicDBObject statusConditionQuery = new BasicDBObject("$or", statusCondition);
				paramConditionLst.add(statusConditionQuery);
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

			logger.info("Inside TxnReports , searchPaymentCount , fianlList = " + fianlList);
			BasicDBObject finalquery = new BasicDBObject("$and", fianlList);
			MongoDatabase dbIns = mongoInstance.getDB();
			MongoCollection<Document> coll = dbIns.getCollection(
					PropertiesManager.propertiesMap.get(prefix + Constants.TRANSACTION_STATUS_COLLECTION.getValue()));

			BasicDBObject match = new BasicDBObject("$match", finalquery);
			Document firstGroup = new Document("_id",
					new Document("PG_REF_NUM", "$PG_REF_NUM").append("STATUS", "$STATUS"));
			BasicDBObject firstGroupObject = new BasicDBObject(firstGroup);
			BasicDBObject secondGroup = new BasicDBObject("$last", "$$ROOT");
			BasicDBObject groupQuery = new BasicDBObject("$group", firstGroupObject.append("contentList", secondGroup));
			return coll.aggregate(Arrays.asList(match, groupQuery)).into(new ArrayList<>()).size();

		} catch (Exception e) {
			logger.error("Exception occured in TxnReports , searchPaymentCount n exception = " , e);
			return 0;
		}
	}

	public List<TransactionSearch> searchPaymentSplitPayment(String pgRefNum, String orderId, String customerEmail,
			String merchantPayId, String paymentType, String Userstatus, String currency, String transactionType,
			String fromDate, String toDate, User user, int start, int length, String phoneNo, String mopType) {

		logger.info("Inside TxnReports , searchPaymentSplitPayment");

		boolean isParameterised = false;
		try {
			List<BasicDBObject> statusCondition = new ArrayList<BasicDBObject>();
			List<TransactionSearch> transactionList = new ArrayList<TransactionSearch>();
			List<BasicDBObject> dateCondition = new ArrayList<BasicDBObject>();
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

				dateCondition.add(new BasicDBObject(FieldType.CREATE_DATE.getName(),
						BasicDBObjectBuilder.start("$gte", new SimpleDateFormat(fromDate).toLocalizedPattern())
								.add("$lte", new SimpleDateFormat(currentDate).toLocalizedPattern()).get()));

				dateCondition.add(new BasicDBObject(FieldType.SETTLEMENT_DATE.getName(),
						BasicDBObjectBuilder.start("$gte", new SimpleDateFormat(fromDate).toLocalizedPattern())
								.add("$lte", new SimpleDateFormat(currentDate).toLocalizedPattern()).get()));

				dateQuery.put("$or", dateCondition);
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
				dateIndexConditionList.add(new BasicDBObject("SETTLEMENT_DATE_INDEX", dateIndex));
			}

			if (StringUtils.isNotBlank(PropertiesManager.propertiesMap.get(Constants.USE_DATE_INDEX.getValue()))
					&& PropertiesManager.propertiesMap.get(Constants.USE_DATE_INDEX.getValue()).equalsIgnoreCase("Y")) {
				dateIndexConditionQuery.append("$or", dateIndexConditionList);
			}

			/*
			 * if (!merchantPayId.equalsIgnoreCase("ALL")) { paramConditionLst.add(new
			 * BasicDBObject(FieldType.PAY_ID.getName(), merchantPayId));
			 * 
			 * isParameterised = true; }
			 */

			/*
			 * if (merchantPayId.equalsIgnoreCase("ALL")) { List<String> payIdLst =
			 * userdao.getActiveMerchantList(user.getSegment(), user.getRole().getId()).stream().map(Merchants::getPayId).collect(Collectors.toList());
			 * logger.info("Get PayId For SplitPayment Merchant : "+payIdLst);
			 * if(payIdLst.size() > 0) { isParameterised = true; paramConditionLst.add(new
			 * BasicDBObject(FieldType.PAY_ID.getName(), new BasicDBObject("$in",
			 * payIdLst))); } } else { isParameterised = true; paramConditionLst.add(new
			 * BasicDBObject(FieldType.PAY_ID.getName(), merchantPayId)); }
			 */

			if (merchantPayId.equalsIgnoreCase("ALL")) {
				logger.info("TxnReport, MerchantId : " + merchantPayId + ", Segment : " + user.getSegment());
				if (!user.getSegment().equalsIgnoreCase("Default")) {
					List<String> payIdLst = userdao.getActiveMerchantList(user.getSegment(), user.getRole().getId()).stream().map(Merchants::getPayId).collect(Collectors.toList());
					logger.info("Get PayId List : " + payIdLst);
					if (payIdLst.size() > 0) {
						paramConditionLst
								.add(new BasicDBObject(FieldType.PAY_ID.getName(), new BasicDBObject("$in", payIdLst)));
					}
				}

			} else {
				isParameterised = true;
				paramConditionLst.add(new BasicDBObject(FieldType.PAY_ID.getName(), merchantPayId));
			}

			if (user.getUserType().equals(UserType.RESELLER)) {
				paramConditionLst.add(new BasicDBObject(FieldType.RESELLER_ID.getName(), user.getResellerId()));
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
				paramConditionLst.add(new BasicDBObject(FieldType.CUST_EMAIL.getName(), customerEmail));
			}
			if (!phoneNo.isEmpty()) {
				isParameterised = true;
				paramConditionLst.add(new BasicDBObject(FieldType.CUST_PHONE.getName(), phoneNo));
			}
			if (!mopType.equalsIgnoreCase("ALL")) {
				isParameterised = true;
				paramConditionLst.add(new BasicDBObject(FieldType.MOP_TYPE.getName(), mopType));
			}
			if (!Userstatus.equalsIgnoreCase("ALL")) {
				isParameterised = true;
				statusCondition.add(new BasicDBObject(FieldType.STATUS.getName(), Userstatus));
			} else {

			}

			if (!StatusType.SETTLED_SETTLE.getName().equalsIgnoreCase(Userstatus)) {
				statusCondition.add(new BasicDBObject(FieldType.STATUS.getName(), StatusType.SETTLED_SETTLE.getName()));
			}

			if (!statusCondition.isEmpty()) {
				BasicDBObject statusConditionQuery = new BasicDBObject("$or", statusCondition);
				paramConditionLst.add(statusConditionQuery);
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
			if (!paramConditionLst.isEmpty()) {
				allParamQuery = new BasicDBObject("$and", paramConditionLst);
			}

			List<BasicDBObject> allConditionQueryList = new ArrayList<BasicDBObject>();

			if (StringUtils.isNotBlank(pgRefNum) || StringUtils.isNotBlank(orderId)) {

			} else {
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

			BasicDBObject finalquery = new BasicDBObject("$and", fianlList);

			logger.info("Inside TxnReports , searchPayment , finalquery = " + finalquery);
			MongoDatabase dbIns = mongoInstance.getDB();
			MongoCollection<Document> coll = dbIns.getCollection(
					PropertiesManager.propertiesMap.get(prefix + Constants.TRANSACTION_STATUS_COLLECTION.getValue()));
			BasicDBObject match = new BasicDBObject("$match", finalquery);

			BasicDBObject sort = new BasicDBObject("$sort", new BasicDBObject("INSERTION_DATE", -1));
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
				TransactionSearch transReport = new TransactionSearch();
				BigInteger txnID = new BigInteger(dbobj.getString(FieldType.TXN_ID.toString()));
				transReport.setTransactionId((txnID));
				transReport.setPgRefNum(dbobj.getString(FieldType.PG_REF_NUM.toString()));
				transReport.setPayId(dbobj.getString(FieldType.PAY_ID.toString()));

				// added by deep
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

				if ((null != dbobj.getString(FieldType.TXNTYPE.toString())) && (dbobj
						.getString(FieldType.TXNTYPE.toString()).equalsIgnoreCase(TransactionType.REFUND.getName()))) {
					transReport.setTxnType(dbobj.getString(FieldType.TXNTYPE.toString()));
				} else if (null != dbobj.getString(FieldType.ORIG_TXNTYPE.toString())) {
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
				transReport.setPostSettledFlag(dbobj.getString(FieldType.POST_SETTLED_FLAG.getName()));
				transReport.setStatus(dbobj.getString(FieldType.STATUS.toString()));

				if (!Userstatus.equalsIgnoreCase(StatusType.SETTLED_SETTLE.getName())) {
					transReport.setDateFrom(dbobj.getString(FieldType.CREATE_DATE.getName()));
				} else {
					transReport.setDateFrom(dbobj.getString(FieldType.SETTLEMENT_DATE.getName()));
				}

				transReport.setAmount(dbobj.getString(FieldType.AMOUNT.toString()));
				transReport.setOrderId(dbobj.getString(FieldType.ORDER_ID.toString()));
				if (StringUtils.isNotBlank(dbobj.getString(FieldType.REFUND_ORDER_ID.toString()))) {
					transReport.setRefundOrderId(dbobj.getString(FieldType.REFUND_ORDER_ID.toString()));
				} else {
					transReport.setRefundOrderId(CrmFieldConstants.NA.getValue());
				}
				transReport.setoId(dbobj.getString(FieldType.OID.toString()));
				transReport.setProductDesc(dbobj.getString(FieldType.PRODUCT_DESC.toString()));
				transReport.setTransactionCaptureDate(dbobj.getString(FieldType.CREATE_DATE.toString()));
				if (transReport.getTxnType().contains(TransactionType.REFUND.getName())) {
					transReport.setTxnType(TransactionType.REFUND.getName());
				} else {
					transReport.setTxnType(TransactionType.SALE.getName());
				}

				transReport
						.setInternalCardIssusserBank(dbobj.getString(FieldType.INTERNAL_CARD_ISSUER_BANK.toString()));
				transReport.setInternalCardIssusserCountry(
						dbobj.getString(FieldType.INTERNAL_CARD_ISSUER_COUNTRY.toString()));
				transReport.setRefundableAmount(dbobj.getString(FieldType.REFUNDABLE_AMOUNT.toString()));

				transReport.setApprovedAmount(dbobj.getString(FieldType.AMOUNT.toString()));

				// Refund Button logic
				String totalRefundAmount = transactionSearchServiceMongo
						.getTotlaRefundByORderId(dbobj.getString(FieldType.ORDER_ID.toString()));
				Double totalRef = Double.valueOf(totalRefundAmount);
				if (totalRef.equals(0.00)) {
					transReport.setRefundButtonName("Refund");
				} else if (totalRef < Double.valueOf(dbobj.getString(FieldType.AMOUNT.toString()))) {
					transReport.setRefundButtonName("Partial Refund");
				} else if ((totalRef.equals(Double.valueOf(dbobj.getString(FieldType.AMOUNT.toString()))))
						|| (totalRef > Double.valueOf(dbobj.getString(FieldType.AMOUNT.toString())))) {
					transReport.setRefundButtonName("Refunded");
				} else {
					transReport.setRefundButtonName("Refund");
				}

				if (!dbobj.getString(FieldType.TOTAL_AMOUNT.toString()).isEmpty()) {
					transReport.setTotalAmount(dbobj.getString(FieldType.TOTAL_AMOUNT.toString()));

					Double amtPayableToMerch = 0.00;
					Double pgCommision = 0.00;
					Double acquirerCommision = 0.00;
					amtPayableToMerch = Double.valueOf(dbobj.getString(FieldType.TOTAL_AMOUNT.toString()));
					if (dbobj.getString(FieldType.PG_TDR_SC.toString()) != null
							&& dbobj.getString(FieldType.PG_GST.toString()) != null
							&& dbobj.getString(FieldType.ACQUIRER_TDR_SC.toString()) != null
							&& dbobj.getString(FieldType.ACQUIRER_GST.toString()) != null) {
						pgCommision = Double.valueOf(dbobj.getString(FieldType.PG_TDR_SC.toString()))
								+ Double.valueOf(dbobj.getString(FieldType.PG_GST.toString()));
						acquirerCommision = Double.valueOf(dbobj.getString(FieldType.ACQUIRER_TDR_SC.toString()))
								+ Double.valueOf(dbobj.getString(FieldType.ACQUIRER_GST.toString()));
						amtPayableToMerch -= (pgCommision + acquirerCommision);
						transReport.setSettledAmount(String.format("%.2f", amtPayableToMerch));
					} else {
						transReport.setSettledAmount(String.format("%.2f", amtPayableToMerch));
					}

				} else {
					transReport.setTotalAmount("");
				}

				if (null != dbobj.getString(FieldType.CURRENCY_CODE.toString())) {
					transReport.setCurrency(
							currencyCodeDao.getCurrencyNamebyCode(dbobj.getString(FieldType.CURRENCY_CODE.toString())));

				} else {
					transReport.setCurrency(CrmFieldConstants.NA.getValue());
				}

				Comparator<TransactionSearch> comp = (TransactionSearch a, TransactionSearch b) -> {

					if (a.getDateFrom().compareTo(b.getDateFrom()) > 0) {
						return -1;
					} else if (a.getDateFrom().compareTo(b.getDateFrom()) < 0) {
						return 1;
					} else {
						return 0;
					}
				};

				transactionList.add(transReport);
				Collections.sort(transactionList, comp);
			}
			cursor.close();
			logger.info("Inside TxnReports , searchPayment , transactionListSize = " + transactionList.size());
			return transactionList;
		} catch (Exception e) {
			logger.error("Exception occured in TxnReports , searchPayment , Exception = " , e);
			return null;
		}
	}

	@SuppressWarnings("unchecked")
	public List<PaymentSearchDownloadObject> searchPaymentForDownloadSplitPayment(String merchantPayId,
			String paymentType, String status, String currency, String transactionType, String fromDate, String toDate,
			User user, String paymentsRegion, String acquirer) {

		logger.info("Inside TxnReports , searchPayment");
		Map<String, User> userMap = new HashMap<String, User>();
		boolean isParameterised = false;
		List<PaymentSearchDownloadObject> transactionList = new ArrayList<PaymentSearchDownloadObject>();
		try {

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

			/*
			 * if (!merchantPayId.equalsIgnoreCase("ALL")) { paramConditionLst.add(new
			 * BasicDBObject(FieldType.PAY_ID.getName(), merchantPayId));
			 * 
			 * isParameterised = true; }
			 */

			/*
			 * if (merchantPayId.equalsIgnoreCase("ALL")) { List<String> payIdLst =
			 * userdao.getActiveMerchantList(user.getSegment(), user.getRole().getId()).stream().map(Merchants::getPayId).collect(Collectors.toList());
			 * logger.info("Get PayId For SplitPayment Merchant : " + payIdLst); if
			 * (payIdLst.size() > 0) { isParameterised = true; paramConditionLst .add(new
			 * BasicDBObject(FieldType.PAY_ID.getName(), new BasicDBObject("$in",
			 * payIdLst))); } } else { isParameterised = true; paramConditionLst.add(new
			 * BasicDBObject(FieldType.PAY_ID.getName(), merchantPayId)); }
			 */

			if (merchantPayId.equalsIgnoreCase("ALL")) {
				logger.info("TxnReport, MerchantId : " + merchantPayId + ", Segment : " + user.getSegment());
				if (!user.getSegment().equalsIgnoreCase("Default")) {
					List<String> payIdLst = userdao.getActiveMerchantList(user.getSegment(), user.getRole().getId()).stream().map(Merchants::getPayId).collect(Collectors.toList());
					logger.info("Get PayId List : " + payIdLst);
					if (payIdLst.size() > 0) {
						paramConditionLst
								.add(new BasicDBObject(FieldType.PAY_ID.getName(), new BasicDBObject("$in", payIdLst)));
					}
				}

			} else {
				isParameterised = true;
				paramConditionLst.add(new BasicDBObject(FieldType.PAY_ID.getName(), merchantPayId));
			}

			if (user.getUserType().equals(UserType.RESELLER)) {
				paramConditionLst.add(new BasicDBObject(FieldType.RESELLER_ID.getName(), user.getResellerId()));
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

					acquirerConditionLst.add(new BasicDBObject(FieldType.ACQUIRER_TYPE.getName(), acq.trim()));
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

			Document fGroup = new Document("_id",
					new Document("PG_REF_NUM", "$PG_REF_NUM").append("STATUS", "$STATUS"));
			BasicDBObject fGroupObject = new BasicDBObject(fGroup);
			BasicDBObject sGroup = new BasicDBObject("$last", "$$ROOT");
			BasicDBObject groupQuery = new BasicDBObject("$group", fGroupObject.append("contentList", sGroup));
			BasicDBObject replacedRoot = new BasicDBObject("newRoot", "$contentList");
			BasicDBObject replaceRootQuery = new BasicDBObject("$replaceRoot", replacedRoot);
			List<Bson> pipeline = Arrays.asList(match, groupQuery, replaceRootQuery, group, sort);

			AggregateIterable<Document> output = coll.aggregate(pipeline);
			output.allowDiskUse(true);
			MongoCursor<Document> cursor = output.iterator();
			// Loading the Company profile for calculating the cgst,sgst,igst

			CompanyProfile cp = new CompanyProfile();
			cp = companyProfileDao
					.getCompanyProfileByTenantId(PropertiesManager.propertiesMap.get(companyProfileTenantId));

			while (cursor.hasNext()) {
				Document mydata = cursor.next();
				List<Document> courses = (List<Document>) mydata.get("entries");
				Document dbobj = courses.get(0);
				PaymentSearchDownloadObject transReport = new PaymentSearchDownloadObject();
				transReport.setTransactionId(dbobj.getString(FieldType.TXN_ID.toString()));
				transReport.setPgRefNum(dbobj.getString(FieldType.PG_REF_NUM.toString()));
				transReport.setTransactionRegion(dbobj.getString(FieldType.PAYMENTS_REGION.toString()));
				transReport.setMerchants(dbobj.getString(CrmFieldType.BUSINESS_NAME.getName()));
				transReport.setPostSettledFlag(dbobj.getString(FieldType.POST_SETTLED_FLAG.getName()));

				// Added by Deep
				if (StringUtils.isNotBlank(dbobj.getString(FieldType.UDF4.getName()))) {
					transReport.setUdf4(dbobj.getString(FieldType.UDF4.getName()));
				} else {
					transReport.setUdf4(CrmFieldConstants.NA.getValue());
				}
				if (StringUtils.isNotBlank(dbobj.getString(FieldType.UDF5.getName()))) {
					transReport.setUdf5(dbobj.getString(FieldType.UDF5.getName()));
				} else {
					transReport.setUdf5(CrmFieldConstants.NA.getValue());
				}
				if (StringUtils.isNotBlank(dbobj.getString(FieldType.UDF6.getName()))) {
					transReport.setUdf6(dbobj.getString(FieldType.UDF6.getName()));
				} else {
					transReport.setUdf6(CrmFieldConstants.NA.getValue());
				}
				// end by deep

				String payid = (String) dbobj.get(FieldType.PAY_ID.getName());
				/* added by vijaylakshmi */
				String INTERNAL_CUST_IP = dbobj.getString(FieldType.INTERNAL_CUST_IP.getName());
				if (StringUtils.isBlank(INTERNAL_CUST_IP)) {
					logger.info("Fetching ipaddress from transaction table based on OID & pending status");
					INTERNAL_CUST_IP = getPreviousForOID(dbobj.getString(FieldType.OID.getName()));

					if (StringUtils.isBlank(INTERNAL_CUST_IP)) {
						logger.info(
								"Fetching ipaddress from transaction table based on PgRefNum & status -> Send to Bank ");
						INTERNAL_CUST_IP = getPreviousForPgRefNum(dbobj.getString(FieldType.PG_REF_NUM.toString()));
					}
				}

				logger.info("CUST_PHONE++++++++++++++++++++++++++++++" + dbobj.getString("CUST_PHONE"));
				logger.info("INTERNAL_CUST_IP++++++++++++++++++++++++++++++" + INTERNAL_CUST_IP);

				transReport.setCustEmail(dbobj.getString(FieldType.CUST_EMAIL.toString()));
				transReport.setCustMobileNo(dbobj.getString("CUST_PHONE"));
				transReport.setIpAddress(INTERNAL_CUST_IP);

				// transReport.setIpAddress(dbobj.getString("INTERNAL_CUST_IP"));
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
				} else {

					// If ORIG_TXN_TYPE is not available incase of a timeout , set TXNTYPE instead
					// of ORIG_TXN_TYPE
					if (dbobj.getString(FieldType.STATUS.toString()).equalsIgnoreCase(StatusType.TIMEOUT.getName())) {
						transReport.setTxnType(dbobj.getString(FieldType.TXNTYPE.toString()));
					} else {
						transReport.setTxnType(CrmFieldConstants.NA.getValue());
					}

				}

				if (null != dbobj.getString(FieldType.ACQUIRER_TYPE.toString())) {
					transReport.setAcquirerType(dbobj.getString(FieldType.ACQUIRER_TYPE.toString()));
				} else {
					transReport.setAcquirerType(CrmFieldConstants.NA.getValue());
				}
				if (status.equalsIgnoreCase(StatusType.SETTLED_SETTLE.toString())) {
					if (null != dbobj.getString(FieldType.PG_DATE_TIME.toString())) {
						transReport.setCaptureDateTime(dbobj.getString(FieldType.PG_DATE_TIME.toString()));
					} else {
						transReport.setCaptureDateTime(CrmFieldConstants.NA.getValue());
					}
					Double totalTdrSc = 0.00;
					Double totalGst = 0.00;
					Double netAmount = 0.00;
					Double cGst = 0.00;
					Double sGst = 0.00;
					Double iGst = 0.00;
					if (null != dbobj.getString(FieldType.PG_TDR_SC.toString())) {
						totalTdrSc += Double.valueOf(dbobj.getString(FieldType.PG_TDR_SC.toString()));
					}
					if (null != dbobj.getString(FieldType.ACQUIRER_TDR_SC.toString())) {
						totalTdrSc += Double.valueOf(dbobj.getString(FieldType.ACQUIRER_TDR_SC.toString()));
					}
					transReport.setTdrSurcharge(String.format("%.2f", totalTdrSc));

					if (null != dbobj.getString(FieldType.PG_GST.toString())) {
						totalGst += Double.valueOf(dbobj.getString(FieldType.PG_GST.toString()));
					}
					if (null != dbobj.getString(FieldType.ACQUIRER_GST.toString())) {
						totalGst += Double.valueOf(dbobj.getString(FieldType.ACQUIRER_GST.toString()));
					}
					// here need to implement the intra state supply related logic comparing both
					// merchant and buyer states
					if (totalGst != 0.00 && null != user1.getState() && null != cp && null != cp.getState()) {
						if (user1.getState().equalsIgnoreCase(cp.getState())) {
							sGst = totalGst / 2.00;
							cGst = totalGst / 2.00;
						} else {
							iGst = totalGst;
						}

					}
					transReport.setSgst(String.format("%.2f", sGst));
					transReport.setCgst(String.format("%.2f", cGst));
					transReport.setIgst(String.format("%.2f", iGst));
					transReport.setTotalGst(String.format("%.2f", totalGst));
					if (null != dbobj.getString(FieldType.TOTAL_AMOUNT.toString())) {
						netAmount = Double.valueOf(dbobj.getString(FieldType.TOTAL_AMOUNT.toString())) - totalTdrSc
								- totalGst;
					}
					transReport.setNetAmount(String.format("%.2f", netAmount));

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

				if (transactionType.equalsIgnoreCase(TransactionType.REFUND.toString())
						&& status.equalsIgnoreCase(StatusType.CAPTURED.toString())) {
					if (null != dbobj.getString(FieldType.REFUND_ORDER_ID.toString())) {
						transReport.setRefundOrderId(dbobj.getString(FieldType.REFUND_ORDER_ID.toString()));
					} else {
						transReport.setRefundOrderId(CrmFieldConstants.NA.getValue());
					}
				}

				if (dbobj.getString(FieldType.TOTAL_AMOUNT.toString()) != null) {
					transReport.setTotalAmount(dbobj.getString(FieldType.TOTAL_AMOUNT.toString()));
				} else {
					transReport.setTotalAmount("");
				}
				if (dbobj.getString(FieldType.RRN.toString()) != null) {
					transReport.setRrn(dbobj.getString(FieldType.RRN.toString()));
				} else {
					transReport.setRrn(CrmFieldConstants.NA.getValue());
				}
				if (dbobj.getString(FieldType.ARN.toString()) != null) {
					transReport.setArn(dbobj.getString(FieldType.ARN.toString()));
				} else {
					transReport.setArn(CrmFieldConstants.NA.getValue());
				}
				if (dbobj.getString(FieldType.ACQ_ID.toString()) != null) {
					transReport.setAcqId(dbobj.getString(FieldType.ACQ_ID.toString()));
				} else {
					transReport.setAcqId(CrmFieldConstants.NA.getValue());
				}
				if (dbobj.getString(FieldType.MOP_TYPE.toString()) != null) {
					transReport.setMopType(MopType.getmopName(dbobj.getString(FieldType.MOP_TYPE.toString())));
				} else {
					transReport.setMopType(CrmFieldConstants.NA.getValue());
				}
				if (dbobj.getString(FieldType.ACQUIRER_TYPE.toString()) != null) {
					transReport.setAcquirerType(
							AcquirerType.getAcquirerName(dbobj.getString(FieldType.ACQUIRER_TYPE.toString())));
				} else {
					transReport.setAcquirerType(CrmFieldConstants.NA.getValue());
				}

				transactionList.add(transReport);

			}
			logger.info("transactionList created and size = " + transactionList.size());
			cursor.close();
			logger.info("Inside TxnReports , searchPayment , transactionListSize = " + transactionList.size());
			Comparator<PaymentSearchDownloadObject> comp = (PaymentSearchDownloadObject a,
					PaymentSearchDownloadObject b) -> {

				if (a.getDateFrom().compareTo(b.getDateFrom()) > 0) {
					return -1;
				} else if (a.getDateFrom().compareTo(b.getDateFrom()) < 0) {
					return 1;
				} else {
					return 0;
				}
			};
			Collections.sort(transactionList, comp);
			logger.info("transactionList created and Sorted");
			return transactionList;
		} catch (Exception e) {
			logger.error("Exception occured in TxnReports , searchPayment , Exception = " , e);
			return transactionList;
		}
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
			 * userdao.getActiveMerchantList(user.getSegment(), user.getRole().getId()).stream().map(Merchants::getPayId).collect(Collectors.toList());
			 * logger.info("Get PayId For SplitPayment Merchant : "+payIdLst);
			 * if(payIdLst.size() > 0) { //isParameterised = true; paramConditionLst.add(new
			 * BasicDBObject(FieldType.PAY_ID.getName(), new BasicDBObject("$in",
			 * payIdLst))); } } else { //isParameterised = true; paramConditionLst.add(new
			 * BasicDBObject(FieldType.PAY_ID.getName(), merchantPayId)); }
			 */

			if (merchantPayId.equalsIgnoreCase("ALL")) {
				logger.info("TxnReport, MerchantId : " + merchantPayId + ", Segment : " + user.getSegment());
				if (!user.getSegment().equalsIgnoreCase("Default")) {
					List<String> payIdLst = userdao.getActiveMerchantList(user.getSegment(), user.getRole().getId()).stream().map(Merchants::getPayId).collect(Collectors.toList());
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
				logger.info(" total =================================== = " + total);
				
				if (totalAmount.equals("0")) {

					logger.info(" totalAmount, @@@@@@@@@@@@@@cccccc>>>>>> = " + totalAmount);
					paramConditionLst.add(new BasicDBObject(FieldType.TOTAL_AMOUNT.getName(), null));

				} else {

					if (total.length == 1) {
						totalAmount = totalAmount + ".00";
						logger.info(" totalAmount, @@@@@@@@@@dddddd>>>>>> = " + totalAmount);
					}
					paramConditionLst.add(new BasicDBObject(FieldType.TOTAL_AMOUNT.getName(), totalAmount));
					logger.info("Inside txnReports.java @@@@@cccc  totalAmount ======================= = "
							+ totalAmount);
				}

//				if (total.length == 1) {
//					totalAmount = totalAmount + ".00";
//					logger.info(" totalAmount, =======<1========================= = " + totalAmount);
//				}
//				paramConditionLst.add(new BasicDBObject(FieldType.TOTAL_AMOUNT.getName(), totalAmount));
//				logger.info(
//						"Inside txnReports.java  totalAmount, ==========s========================= = " + totalAmount);

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
			 * "endDateTime========================= = " , endDateTime);
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
			 * logger.info("newSearchPayment++++++++++++++++++++++++++++++" , endTime);
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
			 * , endDateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd hh:mm:ss")));
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
					transReport.setCurrency(
							currencyCodeDao.getCurrencyNamebyCode(dbobj.getString(FieldType.CURRENCY_CODE.toString())));
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
			 * userdao.getActiveMerchantList(user.getSegment(), user.getRole().getId()).stream().map(Merchants::getPayId).collect(Collectors.toList());
			 * logger.info("Get PayId For SplitPayment Merchant : "+payIdLst);
			 * if(payIdLst.size() > 0) { //isParameterised = true; paramConditionLst.add(new
			 * BasicDBObject(FieldType.PAY_ID.getName(), new BasicDBObject("$in",
			 * payIdLst))); } } else { //isParameterised = true; paramConditionLst.add(new
			 * BasicDBObject(FieldType.PAY_ID.getName(), merchantPayId)); }
			 */

			if (merchantPayId.equalsIgnoreCase("ALL")) {
				logger.info("TxnReport, MerchantId : " + merchantPayId + ", Segment : " + user.getSegment());
				if (!user.getSegment().equalsIgnoreCase("Default")) {
					List<String> payIdLst = userdao.getActiveMerchantList(user.getSegment(), user.getRole().getId()).stream().map(Merchants::getPayId).collect(Collectors.toList());
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
				if (totalAmount.equals("0")) {

					logger.info(" totalAmount, @@@@@@@@@@@@@@xaxaxax>>>>>> = " + totalAmount);
					paramConditionLst.add(new BasicDBObject(FieldType.TOTAL_AMOUNT.getName(), null));

				} else {

					if (total.length == 1) {
						totalAmount = totalAmount + ".00";
						logger.info(" totalAmount, @@@@@@@@@@aasasasa>>>>>> = " + totalAmount);
					}
					paramConditionLst.add(new BasicDBObject(FieldType.TOTAL_AMOUNT.getName(), totalAmount));
					logger.info("Inside txnReports.java @@@@@xaaxaxax totalAmount ======================= = "
							+ totalAmount);
				}

//				if (total.length == 1) {
//					totalAmount = totalAmount + ".00";
//					logger.info(" totalAmount, =======<1========================= = " + totalAmount);
//				}
//				paramConditionLst.add(new BasicDBObject(FieldType.TOTAL_AMOUNT.getName(), totalAmount));
//				logger.info(
//						"Inside txnReports.java  totalAmount, ==========s========================= = " + totalAmount);

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
			 * "endDateTime========================= = " , endDateTime);
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
			 * logger.info("newSearchPaymentCount++++++++++++++++++++++++++++++" , endTime
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
			 * , endDateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd hh:mm:ss")));
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

	public List<TransactionSearchDownloadObject> searchTransactionForDownloadNewSplitPayment(String merchantPayId,
			String paymentType, String aliasStatus, String currency, String transactionType, String fromDate,
			String toDate, User user, String paymentsRegion, String acquirer, String customerEmail,
			String customerPhone, String mopType, String pgRefNum, String orderId, String tenantId, String ipAddress,
			String totalAmount, String rrn) {
		logger.info("Inside TxnReports , searchPayment download");
		logger.info("ipAddress :======================================= " + ipAddress
				+ " totalAmount :====================================== " + totalAmount);

		List<TransactionSearchDownloadObject> transactionList = new ArrayList<TransactionSearchDownloadObject>();

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
			 * BasicDBObject(FieldType.PAY_ID.getName(), merchantPayId));
			 * 
			 * }
			 */

			/*
			 * if (merchantPayId.equalsIgnoreCase("ALL")) { List<String> payIdLst =
			 * userdao.getActiveMerchantList(user.getSegment(), user.getRole().getId()).stream().map(Merchants::getPayId).collect(Collectors.toList());
			 * logger.info("Get PayId For SplitPayment Merchant : "+payIdLst);
			 * if(payIdLst.size() > 0) { //isParameterised = true; paramConditionLst.add(new
			 * BasicDBObject(FieldType.PAY_ID.getName(), new BasicDBObject("$in",
			 * payIdLst))); } } else { //isParameterised = true; paramConditionLst.add(new
			 * BasicDBObject(FieldType.PAY_ID.getName(), merchantPayId)); }
			 */

			if (merchantPayId.equalsIgnoreCase("ALL")) {
				logger.info("TxnReport, MerchantId : " + merchantPayId + ", Segment : " + user.getSegment());
				if (!user.getSegment().equalsIgnoreCase("Default")) {
					List<String> payIdLst = userdao.getActiveMerchantList(user.getSegment(), user.getRole().getId()).stream().map(Merchants::getPayId).collect(Collectors.toList());
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
				paramConditionLst.add(new BasicDBObject(FieldType.CUST_EMAIL.getName(), customerEmail.trim()));
			}
			if (!customerPhone.isEmpty()) {
				paramConditionLst.add(new BasicDBObject(FieldType.CUST_PHONE.getName(), customerPhone.trim()));
			}

			if (!currency.equalsIgnoreCase("ALL")) {
				paramConditionLst.add(new BasicDBObject(FieldType.CURRENCY_CODE.getName(), currency));
			} else {

			}

			if (!transactionType.equalsIgnoreCase("ALL")) {
				paramConditionLst.add(new BasicDBObject(FieldType.ORIG_TXNTYPE.getName(), transactionType));
			}

			if (!StringUtils.isBlank(ipAddress)) {
				paramConditionLst.add(new BasicDBObject(FieldType.INTERNAL_CUST_IP.getName(), ipAddress.trim()));
			}

			if (!StringUtils.isBlank(totalAmount)) {
				paramConditionLst.add(new BasicDBObject(FieldType.TOTAL_AMOUNT.getName(), totalAmount.trim()));
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
				if (!aliasStatus.equalsIgnoreCase("ALL")) {
					List<String> aliasStatusList = Arrays.asList(aliasStatus.split(","));
					for (String status : aliasStatusList) {
						aliasStatusConditionLst.add(new BasicDBObject(FieldType.STATUS.getName(), status.trim()));
					}
					transactionStatusQuery.append("$or", aliasStatusConditionLst);
				}
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
			logger.info("Inside TxnReports , newSearchPayment download , finalquery = " + finalquery);

			BasicDBObject match = new BasicDBObject("$match", finalquery);
			BasicDBObject sort = new BasicDBObject("$sort", new BasicDBObject("CREATE_DATE", -1));
			Document firstGroup = new Document("_id",
					new Document("PG_REF_NUM", "$PG_REF_NUM").append("STATUS", "$STATUS"));
			BasicDBObject firstGroupObject = new BasicDBObject(firstGroup);
			BasicDBObject secondGroup = new BasicDBObject("$last", "$$ROOT");
			BasicDBObject groupQuery = new BasicDBObject("$group", firstGroupObject.append("contentList", secondGroup));
			BasicDBObject replacedRoot = new BasicDBObject("newRoot", "$contentList");
			BasicDBObject replaceRootQuery = new BasicDBObject("$replaceRoot", replacedRoot);
			List<Bson> pipeline = Arrays.asList(match, groupQuery, replaceRootQuery, sort);

			AggregateIterable<Document> output = coll.aggregate(pipeline);
			output.allowDiskUse(true);
			MongoCursor<Document> cursor = output.iterator();

			while (cursor.hasNext()) {
				Document dbobj = cursor.next();
				TransactionSearchDownloadObject transReport = new TransactionSearchDownloadObject();
				transReport.setTransactionId(dbobj.getString(FieldType.TXN_ID.toString()));
				transReport.setPgRefNum(dbobj.getString(FieldType.PG_REF_NUM.toString()));
				transReport.setPayId(dbobj.getString(FieldType.PAY_ID.toString()));
				transReport.setTransactionRegion(dbobj.getString(FieldType.PAYMENTS_REGION.toString()));
				transReport.setMerchants(dbobj.getString(CrmFieldType.BUSINESS_NAME.getName()));
				transReport.setPostSettledFlag(dbobj.getString(FieldType.POST_SETTLED_FLAG.getName()));
				transReport.setAcquirerResponseMessage(dbobj.getString(FieldType.PG_TXN_MESSAGE.getName()));
				if (dbobj.getString(FieldType.UDF6.getName()) != null) {
					transReport.setDeltaRefundFlag(dbobj.getString(FieldType.UDF6.getName()));
				} else {
					transReport.setDeltaRefundFlag("");
				}

				if (dbobj.getString(FieldType.CURRENCY_CODE.getName()) != null) {
					transReport.setCurrency (dbobj.getString(FieldType.CURRENCY_CODE.getName()));
				} else {
					transReport.setCurrency(dbobj.getString(CrmFieldConstants.NA.getValue()));
				}

				// start by deep
				if (dbobj.getString(FieldType.UDF4.getName()) != null) {
					transReport.setUdf4(dbobj.getString(FieldType.UDF4.getName()));
				} else {
					transReport.setUdf4(dbobj.getString(CrmFieldConstants.NA.getValue()));
				}

				if (dbobj.getString(FieldType.UDF5.getName()) != null) {
					transReport.setUdf5(dbobj.getString(FieldType.UDF5.getName()));
				} else {
					transReport.setUdf5(dbobj.getString(CrmFieldConstants.NA.getValue()));
				}

				if (dbobj.getString(FieldType.UDF6.getName()) != null) {
					transReport.setUdf6(dbobj.getString(FieldType.UDF6.getName()));
				} else {
					transReport.setUdf6(dbobj.getString(CrmFieldConstants.NA.getValue()));
				}
				if (dbobj.getString(FieldType.CURRENCY_CODE.getName()) != null) {
					transReport.setCurrency (dbobj.getString(FieldType.CURRENCY_CODE.getName()));
				} else {
					transReport.setCurrency(dbobj.getString(CrmFieldConstants.NA.getValue()));
				}


				// UTR Number code added here

				if (StringUtils.isNotBlank(dbobj.getString(FieldType.UTR_NO.getName()))) {
					transReport.setUtrNo(dbobj.getString(FieldType.UTR_NO.getName()));
				} else {
					transReport.setUtrNo(dbobj.getString(CrmFieldConstants.NA.getValue()));
				}

				// end by deep

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
					if (dbobj.getString(FieldType.ORIG_TXNTYPE.toString())
							.equalsIgnoreCase(TransactionType.STATUS.getName())) {
						transReport.setTxnType(dbobj.getString(FieldType.TXNTYPE.toString()));
					}
				}
				if ((user.getUserType().equals(UserType.ADMIN) || user.getUserType().equals(UserType.SUBADMIN))
						&& null != dbobj.getString(FieldType.ACQUIRER_TYPE.toString())) {
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
				transReport.setOrderId(dbobj.getString(FieldType.ORDER_ID.toString()));
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

				if (dbobj.getString(FieldType.REFUND_ORDER_ID.toString()) != null) {
					transReport.setRefundOrderId(dbobj.getString(FieldType.REFUND_ORDER_ID.toString()));
				} else {
					transReport.setRefundOrderId(CrmFieldConstants.NA.getValue());
				}

				if (dbobj.getString(FieldType.REFUND_FLAG.toString()) != null) {
					transReport.setRefundFlag(dbobj.getString(FieldType.REFUND_FLAG.toString()));
				} else {
					transReport.setRefundFlag(CrmFieldConstants.NA.getValue());
				}

				if (dbobj.getString(FieldType.MOP_TYPE.toString()) != null
						&& !dbobj.getString(FieldType.MOP_TYPE.toString()).equalsIgnoreCase("undefined")
						&& dbobj.getString(FieldType.MOP_TYPE.toString()).trim().length() > 0) {
					transReport.setMopType(MopType.getmop(dbobj.getString(FieldType.MOP_TYPE.toString())).getName());
				} else {
					transReport.setMopType(CrmFieldConstants.NA.getValue());
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

				if (dbobj.getString(FieldType.PSPNAME.toString()) != null) {
					transReport.setPspName((dbobj.getString(FieldType.PSPNAME.toString())));
				} else {
					transReport.setPspName(CrmFieldConstants.NA.getValue());
				}
				if (StringUtils.isNotBlank(dbobj.getString(FieldType.ACQUIRER_GST.toString()))) {
					transReport.setAcquirerGst(String.format("%.2f",
							(Double.valueOf(dbobj.getString(FieldType.ACQUIRER_GST.toString())))));
				} else {
					transReport.setAcquirerGst("0.00");
				}

				if (dbobj.getString(FieldType.ACCT_ID.toString()) != null) {
					transReport.setBankrefno((dbobj.getString(FieldType.ACCT_ID.toString())));
				} else {
					transReport.setBankrefno(CrmFieldConstants.NA.getValue());
				}
				if (dbobj.getString(FieldType.PSPNAME.toString()) != null) {
					transReport.setPspName((dbobj.getString(FieldType.PSPNAME.toString())));
				} else {
					transReport.setPspName(CrmFieldConstants.NA.getValue());
				}
				transReport.setIpAddress(dbobj.getString(FieldType.INTERNAL_CUST_IP.toString()));
				transReport.setCardMask(dbobj.getString(FieldType.CARD_MASK.toString()));
				transReport.setCustomerEmail(dbobj.getString(FieldType.CUST_EMAIL.getName()));
				transReport.setCustomerPhone(dbobj.getString(FieldType.CUST_PHONE.getName()));
				transactionList.add(transReport);
			}
			cursor.close();
			logger.info("Inside TxnReports , searchPayment dwonload, transactionListSize = " + transactionList.size());
			return transactionList.stream().sorted(new Comparator<TransactionSearchDownloadObject>() {

				@Override
				public int compare(TransactionSearchDownloadObject o1, TransactionSearchDownloadObject o2) {
					return o2.getDateFrom().compareTo(o1.getDateFrom());
				}
			}).collect(Collectors.toList());

		} catch (Exception e) {
			logger.error("Exception in search payment download for admin", e);
		}
		return transactionList;
	}

	public List<TransactionSearchDownloadObject> globalSearchTransactionForDownloadNewSplitPayment(String filedType,
			String filedTypeValue, String fromDate, String toDate) {

		List<TransactionSearchDownloadObject> transactionList = new ArrayList<TransactionSearchDownloadObject>();

		try {

			MongoDatabase dbIns = mongoInstance.getDB();
			MongoCollection<Document> coll = dbIns.getCollection(
					PropertiesManager.propertiesMap.get(prefix + Constants.TRANSACTION_STATUS_COLLECTION.getValue()));

			List<String> filedValues = new ArrayList<>();
			for (String value : filedTypeValue.split(",")) {
				filedValues.add(value.trim());
			}

			BasicDBObject match = new BasicDBObject();

			match.put(filedType.trim(), new BasicDBObject("$in", filedValues));
			match.put(FieldType.DATE_INDEX.getName(), BasicDBObjectBuilder.start("$gte", fromDate.replaceAll("-", ""))
					.add("$lte", toDate.replaceAll("-", "")).get());

			List<Bson> pipeline = Arrays.asList(new BasicDBObject("$match", match));

			logger.info("Final Query : " + pipeline);

			AggregateIterable<Document> output = coll.aggregate(pipeline);
			output.allowDiskUse(true);
			MongoCursor<Document> cursor = output.iterator();

			while (cursor.hasNext()) {
				Document dbobj = cursor.next();
				TransactionSearchDownloadObject transReport = new TransactionSearchDownloadObject();
				transReport.setTransactionId(dbobj.getString(FieldType.TXN_ID.toString()));
				transReport.setPgRefNum(dbobj.getString(FieldType.PG_REF_NUM.toString()));
				transReport.setPayId(dbobj.getString(FieldType.PAY_ID.toString()));
				transReport.setTransactionRegion(dbobj.getString(FieldType.PAYMENTS_REGION.toString()));
				transReport.setMerchants(dbobj.getString(CrmFieldType.BUSINESS_NAME.getName()));
				transReport.setPostSettledFlag(dbobj.getString(FieldType.POST_SETTLED_FLAG.getName()));
				transReport.setAcquirerResponseMessage(dbobj.getString(FieldType.PG_TXN_MESSAGE.getName()));
				if (dbobj.getString(FieldType.UDF6.getName()) != null) {
					transReport.setDeltaRefundFlag(dbobj.getString(FieldType.UDF6.getName()));
				} else {
					transReport.setDeltaRefundFlag("");
				}

//start by deep
				if (dbobj.getString(FieldType.UDF4.getName()) != null) {
					transReport.setUdf4(dbobj.getString(FieldType.UDF4.getName()));
				} else {
					transReport.setUdf4(dbobj.getString(CrmFieldConstants.NA.getValue()));
				}

				if (dbobj.getString(FieldType.UDF5.getName()) != null) {
					transReport.setUdf5(dbobj.getString(FieldType.UDF5.getName()));
				} else {
					transReport.setUdf5(dbobj.getString(CrmFieldConstants.NA.getValue()));
				}

				if (dbobj.getString(FieldType.UDF6.getName()) != null) {
					transReport.setUdf6(dbobj.getString(FieldType.UDF6.getName()));
				} else {
					transReport.setUdf6(dbobj.getString(CrmFieldConstants.NA.getValue()));
				}

//end by deep

				String payid = (String) dbobj.get(FieldType.PAY_ID.getName());

//				User user1 = new User();
//
//				if (userMap.get(payid) != null && !userMap.get(payid).getPayId().isEmpty()) {
//					user1 = userMap.get(payid);
//				} else {
//					user1 = userdao.findPayId(payid);
//					userMap.put(payid, user1);
//				}
//
//				if (user1 == null) {
				transReport.setMerchants(CrmFieldConstants.NA.getValue());
//				} else {
//					transReport.setMerchants(user1.getBusinessName());
//				}

				if (null != dbobj.getString(FieldType.ORIG_TXNTYPE.toString())) {

					transReport.setTxnType(dbobj.getString(FieldType.ORIG_TXNTYPE.toString()));
					if (dbobj.getString(FieldType.ORIG_TXNTYPE.toString())
							.equalsIgnoreCase(TransactionType.STATUS.getName())) {
						transReport.setTxnType(dbobj.getString(FieldType.TXNTYPE.toString()));
					}
				}

				transReport.setAcquirerType(dbobj.getString(FieldType.ACQUIRER_TYPE.toString()));

				if (null != dbobj.getString(FieldType.PAYMENT_TYPE.toString())) {
					transReport.setPaymentMethods(
							PaymentType.getpaymentName(dbobj.getString(FieldType.PAYMENT_TYPE.toString())));
				} else {
					transReport.setPaymentMethods(CrmFieldConstants.NA.getValue());
				}

				transReport.setStatus(dbobj.getString(FieldType.ALIAS_STATUS.toString()));
				transReport.setDateFrom(dbobj.getString(FieldType.CREATE_DATE.getName()));

				transReport.setAmount(dbobj.getString(FieldType.AMOUNT.toString()));
				transReport.setOrderId(dbobj.getString(FieldType.ORDER_ID.toString()));
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

				if (dbobj.getString(FieldType.REFUND_ORDER_ID.toString()) != null) {
					transReport.setRefundOrderId(dbobj.getString(FieldType.REFUND_ORDER_ID.toString()));
				} else {
					transReport.setRefundOrderId(CrmFieldConstants.NA.getValue());
				}

				if (dbobj.getString(FieldType.REFUND_FLAG.toString()) != null) {
					transReport.setRefundFlag(dbobj.getString(FieldType.REFUND_FLAG.toString()));
				} else {
					transReport.setRefundFlag(CrmFieldConstants.NA.getValue());
				}

				if (dbobj.getString(FieldType.MOP_TYPE.toString()) != null
						&& !dbobj.getString(FieldType.MOP_TYPE.toString()).equalsIgnoreCase("undefined")
						&& dbobj.getString(FieldType.MOP_TYPE.toString()).trim().length() > 0) {
					transReport.setMopType(MopType.getmop(dbobj.getString(FieldType.MOP_TYPE.toString())).getName());
				} else {
					transReport.setMopType(CrmFieldConstants.NA.getValue());
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

				if (dbobj.getString(FieldType.CURRENCY_CODE.getName()) != null) {
					transReport.setCurrency (dbobj.getString(FieldType.CURRENCY_CODE.getName()));
				} else {
					transReport.setCurrency(dbobj.getString(CrmFieldConstants.NA.getValue()));
				}

				if (StringUtils.isNotBlank(dbobj.getString(FieldType.ACQUIRER_GST.toString()))) {
					transReport.setAcquirerGst(String.format("%.2f",
							(Double.valueOf(dbobj.getString(FieldType.ACQUIRER_GST.toString())))));
				} else {
					transReport.setAcquirerGst("0.00");
				}
				transReport.setIpAddress(dbobj.getString(FieldType.INTERNAL_CUST_IP.toString()));
				transReport.setCardMask(dbobj.getString(FieldType.CARD_MASK.toString()));
				transReport.setCustomerEmail(dbobj.getString(FieldType.CUST_EMAIL.getName()));
				transReport.setCustomerPhone(dbobj.getString(FieldType.CUST_PHONE.getName()));
				transactionList.add(transReport);
			}
			cursor.close();
			logger.info("Inside TxnReports , searchPayment dwonload, transactionListSize = " + transactionList.size());
			return transactionList.stream().sorted(new Comparator<TransactionSearchDownloadObject>() {

				@Override
				public int compare(TransactionSearchDownloadObject o1, TransactionSearchDownloadObject o2) {
					return o2.getDateFrom().compareTo(o1.getDateFrom());
				}
			}).collect(Collectors.toList());

		} catch (Exception e) {
			logger.error("Exception in search payment download for admin", e);
		}
		return transactionList;
	}

	public int globalSearchTransactionCount(String filedType, String filedTypeValue, String fromDate, String toDate) {

		try {

			MongoDatabase dbIns = mongoInstance.getDB();
			MongoCollection<Document> coll = dbIns.getCollection(
					PropertiesManager.propertiesMap.get(prefix + Constants.TRANSACTION_STATUS_COLLECTION.getValue()));

			List<String> filedValues = new ArrayList<>();
			for (String value : filedTypeValue.split(",")) {
				filedValues.add(value.trim());
			}

			BasicDBObject match = new BasicDBObject();

			match.put(filedType.trim(), new BasicDBObject("$in", filedValues));

			match.put(FieldType.DATE_INDEX.getName(), BasicDBObjectBuilder.start("$gte", fromDate.replaceAll("-", ""))
					.add("$lte", toDate.replaceAll("-", "")).get());

			List<Bson> pipeline = Arrays.asList(new BasicDBObject("$match", match));

			logger.info("Final Query : " + pipeline);

			return coll.aggregate(pipeline).into(new ArrayList<>()).size();
		} catch (Exception e) {
			logger.error("Exception in search payment for admin", e);
		}
		return 0;
	}

	public List<TransactionSearchNew> globalSearchTransaction(String filedType, String filedTypeValue, String fromDate,
			String toDate) {

		List<TransactionSearchNew> transactionList = new ArrayList<TransactionSearchNew>();

		try {

			MongoDatabase dbIns = mongoInstance.getDB();
			MongoCollection<Document> coll = dbIns.getCollection(
					PropertiesManager.propertiesMap.get(prefix + Constants.TRANSACTION_STATUS_COLLECTION.getValue()));

			List<String> filedValues = new ArrayList<>();
			for (String value : filedTypeValue.split(",")) {
				filedValues.add(value.trim());
			}

			BasicDBObject match = new BasicDBObject();

			match.put(filedType.trim(), new BasicDBObject("$in", filedValues));

			match.put(FieldType.DATE_INDEX.getName(), BasicDBObjectBuilder.start("$gte", fromDate.replaceAll("-", ""))
					.add("$lte", toDate.replaceAll("-", "")).get());

			List<Bson> pipeline = Arrays.asList(new BasicDBObject("$match", match));

			logger.info("Final Query : " + pipeline);

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

//				User user1 = new User();
//				if (userMap.get(payid) != null && !userMap.get(payid).getPayId().isEmpty()) {
//					user1 = userMap.get(payid);
//				} else {
//					user1 = userdao.findPayId(payid);
//					userMap.put(payid, user1);
//				}

//				if (user1 == null) {
				transReport.setMerchants(CrmFieldConstants.NA.getValue());
//				} else {
//					transReport.setMerchants(user1.getBusinessName());
//				}

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
				transReport.setIpaddress(dbobj.getString(FieldType.INTERNAL_CUST_IP.toString()));

				transReport.setCardMask(dbobj.getString(FieldType.CARD_MASK.toString()));

				transReport.setRrn(dbobj.getString(FieldType.RRN.toString()));
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
					transReport.setCurrency(
							currencyCodeDao.getCurrencyNamebyCode(dbobj.getString(FieldType.CURRENCY_CODE.toString())));
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

	public int newSearchPaymentHoldReleaseCount(String pgRefNum, String orderId, String customerEmail,
			String customerPhone, String paymentType, String aliasStatus, String currency, String transactionType,
			String mopType, String acquirer, String merchantPayId, String fromDate, String toDate, User user, int start,
			int length, String tenantId, String ipAddress, String totalAmount, String type) {
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

			if (merchantPayId.equalsIgnoreCase("ALL")) {
				logger.info("TxnReport, MerchantId : " + merchantPayId + ", Segment : " + user.getSegment());
				if (!user.getSegment().equalsIgnoreCase("Default")) {
					List<String> payIdLst = userdao.getActiveMerchantList(user.getSegment(), user.getRole().getId()).stream().map(Merchants::getPayId).collect(Collectors.toList());
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
			if (!StringUtils.isBlank(ipAddress)) {
				// isParameterised = true;
				paramConditionLst.add(new BasicDBObject(FieldType.INTERNAL_CUST_IP.getName(), ipAddress));
				logger.info("Inside TransactionSearchAction  ipAddress, =================================== = "
						+ ipAddress);
			}

			if (StringUtils.isNotEmpty(totalAmount)) {

				String[] total = StringUtils.split(totalAmount, ".");
				if (totalAmount.equals("0")) {

					logger.info(" totalAmount, @@@@@@@@@@@@@@qqqqq>>>>>> = " + totalAmount);
					paramConditionLst.add(new BasicDBObject(FieldType.TOTAL_AMOUNT.getName(), null));

				} else {

					if (total.length == 1) {
						totalAmount = totalAmount + ".00";
						logger.info(" totalAmount, @@@@@@@@@@wwwwwwww>>>>>> = " + totalAmount);
					}
					paramConditionLst.add(new BasicDBObject(FieldType.TOTAL_AMOUNT.getName(), totalAmount));
					logger.info("Inside txnReports.java @@@@@qqqqqq totalAmount ======================= = "
							+ totalAmount);
				}
//				if (total.length == 1) {
//					totalAmount = totalAmount + ".00";
//					logger.info(" totalAmount, =======<1========================= = " + totalAmount);
//				}
//				paramConditionLst.add(new BasicDBObject(FieldType.TOTAL_AMOUNT.getName(), totalAmount));
//				logger.info(
//						"Inside txnReports.java  totalAmount, ==========s========================= = " + totalAmount);

			}

			if (!currency.equalsIgnoreCase("ALL")) {
				paramConditionLst.add(new BasicDBObject(FieldType.CURRENCY_CODE.getName(), currency));
			} else {

			}
			if (type.equalsIgnoreCase("HOLD")) {
				paramConditionLst.add(new BasicDBObject(FieldType.HOLD_RELEASE.getName(), new BasicDBObject("$ne", 1)));
			} else if (type.equalsIgnoreCase("RELEASE")) {
				paramConditionLst.add(new BasicDBObject(FieldType.HOLD_RELEASE.getName(), 1));
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

	public List<TransactionSearchNew> searchTransactionsForLiabilityHoldRelease(String pgRefNum, String orderId,
			String customerEmail, String customerPhone, String paymentType, String aliasStatus, String currency,
			String transactionType, String mopType, String acquirer, String merchantPayId, String fromDate,
			String toDate, User user, int start, int length, String tenantId, String ipAddress, String totalAmount,
			String fetchType) {

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

			if (merchantPayId.equalsIgnoreCase("ALL")) {
				logger.info("TxnReport, MerchantId : " + merchantPayId + ", Segment : " + user.getSegment());
				if (!user.getSegment().equalsIgnoreCase("Default")) {
					List<String> payIdLst = userdao.getActiveMerchantList(user.getSegment(), user.getRole().getId()).stream().map(Merchants::getPayId).collect(Collectors.toList());
					logger.info("Get PayId List : " + payIdLst);
					if (payIdLst.size() > 0) {
						paramConditionLst
								.add(new BasicDBObject(FieldType.PAY_ID.getName(), new BasicDBObject("$in", payIdLst)));
					}
				}

			} else {
				paramConditionLst.add(new BasicDBObject(FieldType.PAY_ID.getName(), merchantPayId));
			}
			if (fetchType.equalsIgnoreCase("HOLD")) {
				paramConditionLst.add(new BasicDBObject(FieldType.HOLD_RELEASE.getName(), new BasicDBObject("$ne", 1)));
			} else if (fetchType.equalsIgnoreCase("RELEASE")) {
				paramConditionLst.add(new BasicDBObject(FieldType.HOLD_RELEASE.getName(), 1));
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
			}

			if (StringUtils.isNotEmpty(totalAmount)) {

				String[] total = StringUtils.split(totalAmount, ".");
				if (totalAmount.equals("0")) {

					logger.info(" totalAmount, @@@@@@@@@@@@@@eeee>>>>>> = " + totalAmount);
					paramConditionLst.add(new BasicDBObject(FieldType.TOTAL_AMOUNT.getName(), null));

				} else {

					if (total.length == 1) {
						totalAmount = totalAmount + ".00";
						logger.info(" totalAmount, @@@@@@@@@@rrrrrrr>>>>>> = " + totalAmount);
					}
					paramConditionLst.add(new BasicDBObject(FieldType.TOTAL_AMOUNT.getName(), totalAmount));
					logger.info("Inside txnReports.java @@@@@eeeeeee totalAmount ======================= = "
							+ totalAmount);
				}

//				if (total.length == 1) {
//					totalAmount = totalAmount + ".00";
//				}
//				paramConditionLst.add(new BasicDBObject(FieldType.TOTAL_AMOUNT.getName(), totalAmount));

			}

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

//				logger.info("dsfghjk======="+dbobj.getString(FieldType.UDF6.getName()));
				if (StringUtils.isNotBlank(dbobj.getString(FieldType.UDF6.getName()))) {
					transReport.setUdf6(dbobj.getString(FieldType.UDF6.getName()));
					transReport.setSplitPayment("True");
				} else {
					transReport.setUdf6(dbobj.getString(CrmFieldConstants.NA.getValue()));
					transReport.setSplitPayment("False");
				}
//				logger.info("inset========="+dbobj.getString(FieldType.UPDATEDBY.toString()));

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

				transReport.setRrn(dbobj.getString(FieldType.RRN.toString()));
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
			logger.info("Inside searchTransactionsForLiabilityHoldRelease = " + transactionList.size());
			return transactionList;

		} catch (Exception e) {
			logger.error("Exception in searchTransactionsForLiabilityHoldRelease", e);
		}
		return transactionList;
	}

	public int riskListReportCount(String acquirer, String payId, String fromDate, String toDate, int start,
			int length) {

		try {

			MongoDatabase dbIns = mongoInstance.getDB();
			MongoCollection<Document> coll = dbIns.getCollection(
					PropertiesManager.propertiesMap.get(prefix + Constants.TRANSACTION_STATUS_COLLECTION.getValue()));

			fromDate = fromDate.replaceAll("-", "");
			toDate = toDate.replaceAll("-", "");

			BasicDBObject match = new BasicDBObject();
			if (!payId.equalsIgnoreCase("All")) {
				match.put(FieldType.PAY_ID.getName(), payId);
			}
			match.put(FieldType.DATE_INDEX.getName(), new Document("$gte", fromDate).append("$lte", toDate));
			if (!acquirer.equalsIgnoreCase("All")) {
				match.put(FieldType.ACQUIRER_TYPE.getName(), acquirer);
			}
//			match.put(FieldType.STATUS.getName(),StatusType.FORCE_CAPTURED.getName());
			match.put(FieldType.HOLD_RELEASE.getName(), 1);
//			match.put(FieldType.STATUS.getName(),
//					new Document("$in",
//							Arrays.asList("User Inactive", "Failed at Acquirer", "FAILED", "Rejected",
//									"Authentication Failed", "Failed", "Declined", "Invalid", "Error",
//									"Cancelled by user", "Cancelled")));

			BasicDBObject matchQ = new BasicDBObject("$match", match);
//			BasicDBObject skip  = new BasicDBObject("$skip", start);
//			BasicDBObject limit = new BasicDBObject("$limit", length);
			// List<BasicDBObject> pipeline = Arrays.asList(matchQ, skip, limit);

			List<BasicDBObject> pipeline = Arrays.asList(matchQ);
			logger.info("Query : " + pipeline);
			return coll.aggregate(pipeline).into(new ArrayList<>()).size();
		} catch (Exception e) {
			logger.error("Exception in search payment for admin", e);
		}
		return 0;
	}

	public List<TransactionSearchNew> riskListReport(String acquirer, String payId, String fromDate, String toDate,
			int start, int length) {

		List<TransactionSearchNew> transactionList = new ArrayList<TransactionSearchNew>();

		try {

			MongoDatabase dbIns = mongoInstance.getDB();
			MongoCollection<Document> coll = dbIns.getCollection(
					PropertiesManager.propertiesMap.get(prefix + Constants.TRANSACTION_STATUS_COLLECTION.getValue()));

			fromDate = fromDate.replaceAll("-", "");
			toDate = toDate.replaceAll("-", "");

			BasicDBObject match = new BasicDBObject();
			if (!payId.equalsIgnoreCase("All")) {
				match.put(FieldType.PAY_ID.getName(), payId);
			}
			match.put(FieldType.DATE_INDEX.getName(), new Document("$gte", fromDate).append("$lte", toDate));
			if (!acquirer.equalsIgnoreCase("All")) {
				match.put(FieldType.ACQUIRER_TYPE.getName(), acquirer);
			}
			// match.put(FieldType.STATUS.getName(),StatusType.FORCE_CAPTURED.getName());

//			match.put(FieldType.STATUS.getName(),
//					new Document("$in",
//							Arrays.asList("User Inactive", "Failed at Acquirer", "FAILED", "Rejected",
//									"Authentication Failed", "Failed", "Declined", "Invalid", "Error",
//									"Cancelled by user", "Cancelled")));
			match.put(FieldType.HOLD_RELEASE.getName(), 1);

			BasicDBObject matchQ = new BasicDBObject("$match", match);
			BasicDBObject sort = new BasicDBObject("$sort", new BasicDBObject("CREATE_DATE", -1));
			BasicDBObject skip = new BasicDBObject("$skip", start);
			BasicDBObject limit = new BasicDBObject("$limit", length);
			List<BasicDBObject> pipeline = Arrays.asList(matchQ, sort, skip, limit);

			logger.info("Query : " + pipeline);

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
					transReport.setCurrency(
							currencyCodeDao.getCurrencyNamebyCode(dbobj.getString(FieldType.CURRENCY_CODE.toString())));
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

	public int exceptionListReportCount(String acquirer, String payId, String fromDate, String toDate, int start,
			int length) {

		try {

			MongoDatabase dbIns = mongoInstance.getDB();
			MongoCollection<Document> coll = dbIns.getCollection(
					PropertiesManager.propertiesMap.get(prefix + Constants.TRANSACTION_STATUS_COLLECTION.getValue()));

			fromDate = fromDate.replaceAll("-", "");
			toDate = toDate.replaceAll("-", "");

			BasicDBObject match = new BasicDBObject();

			if (!payId.contains("All")) {
				String payidList[] = payId.split(",");
				match.put(FieldType.PAY_ID.getName(), new BasicDBObject("$in", payidList));
			}

			if (!acquirer.contains("All")) {
				String acquirerList[] = acquirer.split(",");
				match.put(FieldType.ACQUIRER_TYPE.getName(), new BasicDBObject("$in", acquirerList));
			}

			match.put(FieldType.TXNTYPE.getName(), TransactionType.SALE.getName());
			match.put(FieldType.DATE_INDEX.getName(), new Document("$gte", fromDate).append("$lte", toDate));

			match.put(FieldType.STATUS.getName(), StatusType.FORCE_CAPTURED.getName());
//			match.put(FieldType.STATUS.getName(),
//					new Document("$in",
//							Arrays.asList("User Inactive", "Failed at Acquirer", "FAILED", "Rejected",
//									"Authentication Failed", "Failed", "Declined", "Invalid", "Error",
//									"Cancelled by user", "Cancelled")));

			BasicDBObject matchQ = new BasicDBObject("$match", match);
//			BasicDBObject skip  = new BasicDBObject("$skip", start);
//			BasicDBObject limit = new BasicDBObject("$limit", length);
			// List<BasicDBObject> pipeline = Arrays.asList(matchQ, skip, limit);

			List<BasicDBObject> pipeline = Arrays.asList(matchQ);
			logger.info("Query : " + pipeline);
			return coll.aggregate(pipeline).into(new ArrayList<>()).size();
		} catch (Exception e) {
			logger.error("Exception in search payment for admin", e);
		}
		return 0;
	}

	public List<TransactionSearchNew> exceptionListReport(String acquirer, String payId, String fromDate, String toDate,
			int start, int length) {

		List<TransactionSearchNew> transactionList = new ArrayList<TransactionSearchNew>();

		try {

			MongoDatabase dbIns = mongoInstance.getDB();
			MongoCollection<Document> coll = dbIns.getCollection(
					PropertiesManager.propertiesMap.get(prefix + Constants.TRANSACTION_STATUS_COLLECTION.getValue()));

			fromDate = fromDate.replaceAll("-", "");
			toDate = toDate.replaceAll("-", "");

			BasicDBObject match = new BasicDBObject();
			if (!payId.contains("ALL") && StringUtils.isNotBlank(payId)) {
				String payidList[] = payId.split(",");
				match.put(FieldType.PAY_ID.getName(), new BasicDBObject("$in", payidList));
			}

			if (!acquirer.contains("ALL") && StringUtils.isNotBlank(acquirer)) {
				String acquirerList[] = acquirer.split(",");
				match.put(FieldType.ACQUIRER_TYPE.getName(), new BasicDBObject("$in", acquirerList));
			}
			match.put(FieldType.TXNTYPE.getName(), TransactionType.SALE.getName());
			match.put(FieldType.DATE_INDEX.getName(), new Document("$gte", fromDate).append("$lte", toDate));
			match.put(FieldType.STATUS.getName(), StatusType.FORCE_CAPTURED.getName());

//			match.put(FieldType.STATUS.getName(),
//					new Document("$in",
//							Arrays.asList("User Inactive", "Failed at Acquirer", "FAILED", "Rejected",
//									"Authentication Failed", "Failed", "Declined", "Invalid", "Error",
//									"Cancelled by user", "Cancelled")));

			BasicDBObject matchQ = new BasicDBObject("$match", match);
			BasicDBObject sort = new BasicDBObject("$sort", new BasicDBObject("CREATE_DATE", -1));
			BasicDBObject skip = new BasicDBObject("$skip", start);
			BasicDBObject limit = new BasicDBObject("$limit", length);
			List<BasicDBObject> pipeline = Arrays.asList(matchQ, sort, skip, limit);

			logger.info("Query : " + pipeline);

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
					transReport.setCurrency(
							currencyCodeDao.getCurrencyNamebyCode(dbobj.getString(FieldType.CURRENCY_CODE.toString())));
				} else {
					transReport.setCurrency(CrmFieldConstants.NA.getValue());
				}

				if (null != dbobj.getString(FieldType.ACQUIRER_TYPE.toString())) {
					transReport.setAcquirerType(dbobj.getString(FieldType.ACQUIRER_TYPE.toString()));
				}

				if (StringUtils.isNotBlank(dbobj.getString(FieldType.PG_TDR_SC.toString()))) {
					transReport.setPgSurchargeAmount(
							String.valueOf( ((Double.valueOf(dbobj.getString(FieldType.PG_TDR_SC.toString()))))));
				} else {
					transReport.setPgSurchargeAmount("0.00");
				}

				if (StringUtils.isNotBlank(dbobj.getString(FieldType.ACQUIRER_TDR_SC.toString()))) {
					transReport.setAcquirerSurchargeAmount(String.valueOf(
							(Double.valueOf(dbobj.getString(FieldType.ACQUIRER_TDR_SC.toString())))));
				} else {
					transReport.setAcquirerSurchargeAmount("0.00");
				}

				if (StringUtils.isNotBlank(dbobj.getString(FieldType.PG_GST.toString()))) {
					transReport.setPgGst(
							String.valueOf( ((Double.valueOf(dbobj.getString(FieldType.PG_GST.toString()))))));
				} else {
					transReport.setPgGst("0.00");
				}

				if (StringUtils.isNotBlank(dbobj.getString(FieldType.ACQUIRER_GST.toString()))) {
					transReport.setAcquirerGst(String.valueOf(
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

	public int misReportCount(String acquirer, String payId, String fromDate, String toDate, int start, int length,
			String settlementDateFrom, String settlementDateTo, String status) {

		try {

			MongoDatabase dbIns = mongoInstance.getDB();
			MongoCollection<Document> coll = dbIns.getCollection(
					PropertiesManager.propertiesMap.get(prefix + Constants.TRANSACTION_STATUS_COLLECTION.getValue()));

			fromDate = fromDate.replaceAll("-", "");
			toDate = toDate.replaceAll("-", "");

			BasicDBObject match = new BasicDBObject();
			if (!payId.equalsIgnoreCase("All")) {
				match.put(FieldType.PAY_ID.getName(), new BasicDBObject("$in", payId.split(",")));
			}
			if (!StringUtils.isBlank(fromDate) && !StringUtils.isBlank(toDate)) {
				match.put(FieldType.DATE_INDEX.getName(),
						new Document("$gte", fromDate.replaceAll("-", "")).append("$lte", toDate.replaceAll("-", "")));
				if (!status.equalsIgnoreCase("All")) {
					if (status.equalsIgnoreCase("1") || status.equalsIgnoreCase("0")) {
						match.put(FieldType.HOLD_RELEASE.getName(), Integer.parseInt(status));
					} else {
						match.put(FieldType.STATUS.getName(), status);
					}
				} else {
					match.put(FieldType.STATUS.getName(),
							new Document("$in",
									Arrays.asList(StatusType.CAPTURED.getName(),
											StatusType.SETTLED_RECONCILLED.getName(),
											StatusType.SETTLED_SETTLE.getName(), StatusType.FORCE_CAPTURED.getName())));
				}
			}
			if (!StringUtils.isBlank(settlementDateFrom) && !StringUtils.isBlank(settlementDateTo)) {
				match.put(FieldType.SETTLEMENT_DATE_INDEX.getName(),
						new Document("$gte", settlementDateFrom.replaceAll("-", "")).append("$lte",
								settlementDateTo.replaceAll("-", "")));

				match.put(FieldType.STATUS.getName(), StatusType.SETTLED_SETTLE.getName());

			}

			if (!acquirer.equalsIgnoreCase("All")) {
				match.put(FieldType.ACQUIRER_TYPE.getName(), new BasicDBObject("$in", acquirer.split(",")));
			}

			BasicDBObject matchQ = new BasicDBObject("$match", match);
//			BasicDBObject skip  = new BasicDBObject("$skip", start);
//			BasicDBObject limit = new BasicDBObject("$limit", length);
			// List<BasicDBObject> pipeline = Arrays.asList(matchQ, skip, limit);

			List<BasicDBObject> pipeline = Arrays.asList(matchQ);
			logger.info("Query : " + pipeline);
			return coll.aggregate(pipeline).into(new ArrayList<>()).size();
		} catch (Exception e) {
			logger.error("Exception in search payment for admin", e);
		}
		return 0;
	}

	public List<TransactionSearchNew> misReportList(String acquirer, String payId, String fromDate, String toDate,
			int start, int length, String settlementDateFrom, String settlementDateTo, String status) {

		List<TransactionSearchNew> transactionList = new ArrayList<TransactionSearchNew>();
		Session session = HibernateSessionProvider.getSession();
		try {

			MongoDatabase dbIns = mongoInstance.getDB();
			MongoCollection<Document> coll = dbIns.getCollection(
					PropertiesManager.propertiesMap.get(prefix + Constants.TRANSACTION_STATUS_COLLECTION.getValue()));

			fromDate = fromDate.replaceAll("-", "");
			toDate = toDate.replaceAll("-", "");

			BasicDBObject match = new BasicDBObject();
			if (!payId.equalsIgnoreCase("All")) {
				match.put(FieldType.PAY_ID.getName(), new BasicDBObject("$in", payId.split(",")));
			}
			if (!StringUtils.isBlank(fromDate) && !StringUtils.isBlank(toDate)) {
				match.put(FieldType.DATE_INDEX.getName(),
						new Document("$gte", fromDate.replaceAll("-", "")).append("$lte", toDate.replaceAll("-", "")));
				if (!status.equalsIgnoreCase("All")) {
					if (status.equalsIgnoreCase("1") || status.equalsIgnoreCase("0")) {
						match.put(FieldType.HOLD_RELEASE.getName(), Integer.parseInt(status));
					} else {
						match.put(FieldType.STATUS.getName(), status);
					}
				} else {
					match.put(FieldType.STATUS.getName(),
							new Document("$in",
									Arrays.asList(StatusType.CAPTURED.getName(),
											StatusType.SETTLED_RECONCILLED.getName(),
											StatusType.SETTLED_SETTLE.getName(), StatusType.FORCE_CAPTURED.getName())));
				}
			}
			if (!StringUtils.isBlank(settlementDateFrom) && !StringUtils.isBlank(settlementDateTo)) {
				match.put(FieldType.SETTLEMENT_DATE_INDEX.getName(),
						new Document("$gte", settlementDateFrom.replaceAll("-", "")).append("$lte",
								settlementDateTo.replaceAll("-", "")));

				match.put(FieldType.STATUS.getName(), StatusType.SETTLED_SETTLE.getName());

			}

			if (!acquirer.equalsIgnoreCase("All")) {
				match.put(FieldType.ACQUIRER_TYPE.getName(), new BasicDBObject("$in", acquirer.split(",")));
			}

			BasicDBObject matchQ = new BasicDBObject("$match", match);
			BasicDBObject sort = new BasicDBObject("$sort", new BasicDBObject("CREATE_DATE", -1));
			BasicDBObject skip = new BasicDBObject("$skip", start);
			BasicDBObject limit = new BasicDBObject("$limit", length);
			List<BasicDBObject> pipeline = Arrays.asList(matchQ, sort, skip, limit);

			logger.info("Query : " + pipeline);

			AggregateIterable<Document> output = coll.aggregate(pipeline);
			output.allowDiskUse(true);
			MongoCursor<Document> cursor = output.iterator();
			TransactionSearchNew transReport = null;
			Query chargingdetailquery = null;
			Object[] details = null;
			List<Object[]> chargingDetailss = null;
			while (cursor.hasNext()) {
				Document dbobj = cursor.next();
				transReport = new TransactionSearchNew();
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
//					transReport.setMerchants("N/A");
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
					transReport.setCurrency(
							currencyCodeDao.getCurrencyNamebyCode(dbobj.getString(FieldType.CURRENCY_CODE.toString())));
				} else {
					transReport.setCurrency(CrmFieldConstants.NA.getValue());
				}

				if (null != dbobj.getString(FieldType.ACQUIRER_TYPE.toString())) {
					transReport.setAcquirerType(dbobj.getString(FieldType.ACQUIRER_TYPE.toString()));
				}
				if (StringUtils.isBlank(dbobj.getString(FieldType.SURCHARGE_FLAG.toString()))
						|| dbobj.getString(FieldType.SURCHARGE_FLAG.toString()).equalsIgnoreCase("N")) {
					transReport.setSurchargeFlag("N");
				} else {
					transReport.setSurchargeFlag("Y");
				}
				if (dbobj.getString("SURCHARGE_FLAG") == null
						|| dbobj.getString("SURCHARGE_FLAG").equalsIgnoreCase("N")) {

					if (StringUtils.isNotBlank(dbobj.getString(FieldType.PG_TDR_SC.toString()))) {
						transReport.setPgSurchargeAmount(String.valueOf(
								((Double.valueOf(dbobj.getString(FieldType.PG_TDR_SC.toString()))))));
					} else {
						transReport.setPgSurchargeAmount("0.00");
					}

					if (StringUtils.isNotBlank(dbobj.getString(FieldType.ACQUIRER_TDR_SC.toString()))) {
						transReport.setAcquirerSurchargeAmount(String.valueOf(
								(Double.valueOf(dbobj.getString(FieldType.ACQUIRER_TDR_SC.toString())))));
					} else {
						transReport.setAcquirerSurchargeAmount("0.00");
					}

					if (StringUtils.isNotBlank(dbobj.getString(FieldType.PG_GST.toString()))) {
						transReport.setPgGst(String.valueOf(
								((Double.valueOf(dbobj.getString(FieldType.PG_GST.toString()))))));
					} else {
						transReport.setPgGst("0.00");
					}

					if (StringUtils.isNotBlank(dbobj.getString(FieldType.ACQUIRER_GST.toString()))) {
						transReport.setAcquirerGst(String.valueOf(
								(Double.valueOf(dbobj.getString(FieldType.ACQUIRER_GST.toString())))));
					} else {
						transReport.setAcquirerGst("0.00");
					}

					double acuirerCommission = (Double.valueOf(transReport.getAcquirerSurchargeAmount())
							+ Double.valueOf(transReport.getAcquirerGst()));
					double aggregatorCommission = (Double.valueOf(transReport.getPgSurchargeAmount())
							+ Double.valueOf(transReport.getPgGst()));

					double amt = Double.valueOf(transReport.getTotalAmount());

					double totalPayoutFromNodal = amt - acuirerCommission;
					double totalPayableToMerchant = totalPayoutFromNodal - aggregatorCommission;

					transReport.setTotalAmountPayableToMerchant(String.valueOf(totalPayableToMerchant));
					transReport.setTotalPayoutFromNodal(String.valueOf(totalPayoutFromNodal));

				} else {

					logger.info("Pay id " + dbobj.getString(FieldType.PAY_ID.toString()) + "\t Acquirer type "
							+ dbobj.getString(FieldType.ACQUIRER_TYPE.toString()) + "\t PaymentType :"
							+ PaymentType.getInstanceUsingCode(dbobj.getString(FieldType.PAYMENT_TYPE.toString()))
									.toString()
							+ "\t MopType : "
							+ MopType.getmop(dbobj.getString(FieldType.MOP_TYPE.toString())).toString()
							+ " \t CURRENCY_CODE" + dbobj.getString(FieldType.CURRENCY_CODE.toString())
							+ "\t TXNTYPE : " + dbobj.getString(FieldType.TXNTYPE.toString()) + "\t" + "CREATE_DATE : "
							+ dbobj.getString(FieldType.CREATE_DATE.toString()));

					chargingdetailquery = session.createSQLQuery(
							"SELECT bankTDR , bankFixCharge,bankServiceTax, pgFixCharge,pgServiceTax,pgTDR,merchantTDR, merchantFixCharge FROM ChargingDetails WHERE payId=:payId and paymentType=:paymentType and mopType=:mopType and acquirerName=:acquirerName and createdDate<=:createdDate  order by createdDate desc");

					chargingdetailquery.setParameter("payId", dbobj.getString(FieldType.PAY_ID.toString()));
					chargingdetailquery.setParameter("paymentType", PaymentType
							.getInstanceUsingCode(dbobj.getString(FieldType.PAYMENT_TYPE.toString())).toString());
					chargingdetailquery.setParameter("mopType",
							MopType.getmop(dbobj.getString(FieldType.MOP_TYPE.toString())).toString());
					chargingdetailquery.setParameter("acquirerName",
							AcquirerType.getAcquirerName(dbobj.getString(FieldType.ACQUIRER_TYPE.toString())));
					chargingdetailquery.setParameter("createdDate", dbobj.getString(FieldType.CREATE_DATE.toString()));

					chargingDetailss = chargingdetailquery.list();
					logger.info("Query : " + chargingdetailquery.getQueryString());
					logger.info(" charging details lise size " + chargingDetailss);

					double bankTDR = 0;

					double bankFixCharge = 0;

					double bankServiceTax = 0;
					double pgServiceTax = 0;
					double pgFixCharge = 0;
					double pgTDR = 0;
					double merchantTDR = 0;
					double merchantFixCharge = 0;

					if (chargingDetailss.size() > 0) {
						details = chargingDetailss.get(0);
						bankTDR = Double.parseDouble("" + details[0]);

						bankFixCharge = Double.parseDouble("" + details[1]);

						bankServiceTax = Double.parseDouble("" + details[2]);
						pgServiceTax = Double.parseDouble("" + details[3]);
						pgFixCharge = Double.parseDouble("" + details[4]);
						pgTDR = Double.parseDouble("" + details[5]);
						merchantTDR = Double.parseDouble("" + details[6]);
						merchantFixCharge = Double.parseDouble("" + details[7]);

						logger.info("BANK TDR" + bankTDR);
						logger.info("bankServiceTax" + bankServiceTax);

						logger.info("bankFixCharge" + bankFixCharge);

						logger.info("pgTDR" + pgTDR);
						logger.info("pgServiceTax" + pgServiceTax);

						logger.info("pgFixCharge" + pgFixCharge);

						logger.info("merchantTDR" + merchantTDR);
						logger.info("merchantFixCharge" + merchantFixCharge);

						double btr = ((bankTDR / 100) * Double.valueOf(transReport.getAmount()));
						double bgst = ((bankServiceTax / 100) * btr);

						double ptdr = ((pgTDR / 100) * Double.valueOf(transReport.getAmount()));
						double pgst = ((pgServiceTax / 100) * btr);

						transReport.setPgSurchargeAmount(String.valueOf( ptdr));

						transReport.setPgGst(String.valueOf( pgst));

						transReport.setAcquirerGst(String.valueOf( bgst));

						transReport.setAcquirerSurchargeAmount(String.valueOf( ptdr));

						double acuirerCommission = (btr + bgst);
						double aggregatorCommission = (ptdr + pgst);

						double amt = Double.valueOf(transReport.getAmount());

						double totalPayoutFromNodal = amt - acuirerCommission;
						double totalPayableToMerchant = totalPayoutFromNodal - aggregatorCommission;

						transReport.setTotalAmountPayableToMerchant(String.valueOf( totalPayableToMerchant));
						transReport.setTotalPayoutFromNodal(String.valueOf( totalPayoutFromNodal));

					}

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

	public List<TransactionSearchNew> getBulkReport(String rrn, String Pgrefno, String orderId) {
		List<TransactionSearchNew> Data = new ArrayList<TransactionSearchNew>();
		List<BasicDBObject> rrnList = new ArrayList<BasicDBObject>();
		List<BasicDBObject> pgrefList = new ArrayList<BasicDBObject>();
		List<BasicDBObject> orderIdList = new ArrayList<BasicDBObject>();
		List<BasicDBObject> allConditionQueryList = new ArrayList<BasicDBObject>();
		BasicDBObject rrnQuery = new BasicDBObject();
		BasicDBObject pgrefQuery = new BasicDBObject();
		BasicDBObject orderidQuery = new BasicDBObject();

		if (StringUtils.isNotBlank(rrn)) {

			String rrnArr[] = rrn.split(",");

			for (String rrndata : rrnArr) {
				rrnList.add(new BasicDBObject(FieldType.RRN.getName(), rrndata.trim()));
			}

			rrnQuery.put("$or", rrnList);
			allConditionQueryList.add(rrnQuery);

		}

		if (StringUtils.isNotBlank(Pgrefno)) {

			String PgrefnoArr[] = Pgrefno.split(",");

			for (String Pgrefnodata : PgrefnoArr) {
				pgrefList.add(new BasicDBObject(FieldType.PG_REF_NUM.getName(), Pgrefnodata.trim()));
			}
			pgrefQuery.put("$or", pgrefList);
			allConditionQueryList.add(pgrefQuery);
		}

		if (StringUtils.isNotBlank(orderId)) {

			String orderIdArr[] = orderId.split(",");

			for (String orderIddata : orderIdArr) {
				orderIdList.add(new BasicDBObject(FieldType.ORDER_ID.getName(), orderIddata.trim()));
			}
			orderidQuery.put("$or", orderIdList);
			allConditionQueryList.add(orderidQuery);

		}
		BasicDBObject finalquery = new BasicDBObject("$or", allConditionQueryList);

		MongoDatabase dbIns = mongoInstance.getDB();
		MongoCollection<Document> coll = dbIns.getCollection(
				PropertiesManager.propertiesMap.get(prefix + Constants.TRANSACTION_STATUS_COLLECTION.getValue()));

		BasicDBObject match = new BasicDBObject("$match", finalquery);
		logger.info("Query to get data for status enquiry [Match] = " + match);
		List<BasicDBObject> pipeline = Arrays.asList(match);
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
				} else if ((dbobj.getString(FieldType.PAYMENT_TYPE.getName())).equals(PaymentType.WALLET.getCode())) {
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
				transReport.setCurrency(
						currencyCodeDao.getCurrencyNamebyCode(dbobj.getString(FieldType.CURRENCY_CODE.toString())));
			} else {
				transReport.setCurrency(CrmFieldConstants.NA.getValue());
			}

			if (null != dbobj.getString(FieldType.ACQUIRER_TYPE.toString())) {
				transReport.setAcquirerType(dbobj.getString(FieldType.ACQUIRER_TYPE.toString()));
			}

			if (StringUtils.isNotBlank(dbobj.getString(FieldType.PG_TDR_SC.toString()))) {
				transReport.setPgSurchargeAmount(
						String.valueOf( ((Double.valueOf(dbobj.getString(FieldType.PG_TDR_SC.toString()))))));
			} else {
				transReport.setPgSurchargeAmount("0.00");
			}

			if (StringUtils.isNotBlank(dbobj.getString(FieldType.ACQUIRER_TDR_SC.toString()))) {
				transReport.setAcquirerSurchargeAmount(
						String.valueOf( (Double.valueOf(dbobj.getString(FieldType.ACQUIRER_TDR_SC.toString())))));
			} else {
				transReport.setAcquirerSurchargeAmount("0.00");
			}

			if (StringUtils.isNotBlank(dbobj.getString(FieldType.PG_GST.toString()))) {
				transReport.setPgGst(
						String.valueOf( ((Double.valueOf(dbobj.getString(FieldType.PG_GST.toString()))))));
			} else {
				transReport.setPgGst("0.00");
			}

			if (StringUtils.isNotBlank(dbobj.getString(FieldType.ACQUIRER_GST.toString()))) {
				transReport.setAcquirerGst(
						String.valueOf( (Double.valueOf(dbobj.getString(FieldType.ACQUIRER_GST.toString())))));
			} else {
				transReport.setAcquirerGst("0.00");
			}

			logger.info(transReport.toString());
			Data.add(transReport);

		}
		return Data;
	}
	
	//Payout Related code start here
//	public List<Document> getPayoutReport() {
//		MongoDatabase dbIns = mongoInstance.getDB();
//
//		MongoCollection<Document> excepColl = dbIns
//				.getCollection(PropertiesManager.propertiesMap.get(prefix + "PO_TransactionStatus"));
//
//		
//		
//		
//	}

	public int[] newResellerSearchPaymentCount(String transactionId, String orderId, String customerEmail,
			String customerPhone, String paymentType, String status, String currency, String transactionType,
			String mopType, String acquirer, String merchantPayId, String dateFrom, String dateTo, User sessionUser,
			int start, int length, String tenantId, String ipAddress, String totalAmount, String rrn,
			String resellerId) {
		// TODO Auto-generated method stub
		int[] a=new int[1];
		return a;
	}

	public List<TransactionSearchNew> newResellerSearchPayment(String transactionId, String orderId,
			String customerEmail, String customerPhone, String paymentType, String status, String currency,
			String transactionType, String mopType, String acquirer, String merchantPayId, String dateFrom,
			String dateTo, User sessionUser, int start, int length, String tenantId, String ipAddress,
			String totalAmount, String rrn, String resellerId) {
		// TODO Auto-generated method stub
		return null;
	}

	public int newResellerSearchPaymentCount(String pgRefNum, String orderId, String customerEmail, String customerPhone,
			String paymentType, String aliasStatus, String currency, String transactionType, String mopType,
			String acquirer, String merchantPayId, String fromDate, String toDate, User user, int start, int length,
			String tenantId, String ipAddress, String totalAmount, String rrn,String channelName,
			String minAmount,String maxAmount,String columnName,String logicalCondition,String searchText, 
			String newDespositor,String columnName1,String logicalCondition1,String searchText1,String columnName2,
			String logicalCondition2,String searchText2, String smaId, String maId, String agentId) {


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
				if (!user.getSegment().equalsIgnoreCase("Default")) {
					List<String> payIdLst = userdao.getActiveMerchantList(user.getSegment(), user.getRole().getId()).stream().map(Merchants::getPayId).collect(Collectors.toList());
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

			if (!StringUtils.equalsIgnoreCase(smaId, "ALL")){
				List<String> payIdLst = userdao.getMerchantBySMAId(smaId).stream().map(User::getPayId).collect(Collectors.toList());
				logger.info("Get PayId List : " + payIdLst);
				if (!payIdLst.isEmpty()) {
					paramConditionLst
							.add(new BasicDBObject(FieldType.PAY_ID.getName(), new BasicDBObject("$in", payIdLst)));
				}
			}

			if (!StringUtils.equalsIgnoreCase(maId, "ALL")){
				List<String> payIdLst = userdao.getMerchantByMAId(maId).stream().map(User::getPayId).collect(Collectors.toList());
				logger.info("Get PayId List : " + payIdLst);
				if (!payIdLst.isEmpty()) {
					paramConditionLst
							.add(new BasicDBObject(FieldType.PAY_ID.getName(), new BasicDBObject("$in", payIdLst)));
				}
			}

			if (!StringUtils.equalsIgnoreCase(agentId, "ALL")){
				List<String> payIdLst = userdao.getMerchantByAgentId(agentId).stream().map(User::getPayId).collect(Collectors.toList());
				logger.info("Get PayId List : " + payIdLst);
				if (!payIdLst.isEmpty()) {
					paramConditionLst
							.add(new BasicDBObject(FieldType.PAY_ID.getName(), new BasicDBObject("$in", payIdLst)));
				}
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

				if (totalAmount.equals("0")) {

					logger.info(" totalAmount, @@@@@@@@@@@@@@ttttt>>>>>> = " + totalAmount);
					paramConditionLst.add(new BasicDBObject(FieldType.TOTAL_AMOUNT.getName(), null));

				} else {

					if (total.length == 1) {
						totalAmount = totalAmount + ".00";
						logger.info(" totalAmount, @@@@@@@@@@yyyyyyy>>>>>> = " + totalAmount);
					}
					paramConditionLst.add(new BasicDBObject(FieldType.TOTAL_AMOUNT.getName(), totalAmount));
					logger.info("Inside txnReports.java @@@@@tttt totalAmount ======================= = "
							+ totalAmount);
				}
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
			 * "endDateTime========================= = " , endDateTime);
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
			 * logger.info("newSearchPaymentCount++++++++++++++++++++++++++++++" , endTime
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
			 * , endDateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd hh:mm:ss")));
			 * logger.
			 * info("endDateTime.format(DateTimeFormatter.ofPattern(\"yyyy-MM-dd hh:mm:ss\")))), ==========s========================= = "
			 * + startDateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd hh:mm:ss")));
			 * 
			 * 
			 * }
			 */
			
			//Added by Deep Singh code start here
			
			logger.info("channelName::: " + channelName );
			logger.info("minAmount::: " + minAmount );
			logger.info("maxAmount::: " + maxAmount );
			logger.info("columnName::: " + columnName );
			logger.info("logicalCondition::: " + logicalCondition );
			logger.info("searchText::: " + searchText );
			
			
			if (channelName.equalsIgnoreCase("All")) {
//				paramConditionLst.add(new BasicDBObject(FieldType.CHANNEL.getName(),
//						new BasicDBObject("$in", Arrays.asList("Fiat", "Crypto"))));
			} else {
				if(channelName.contains(",")) {
					String [] channels=channelName.split(",");
					ArrayList<String> aa=new ArrayList<>();
					for(String channel:channels) {
						aa.add(channel);
					}
					paramConditionLst.add(new BasicDBObject(FieldType.CHANNEL.getName(), new BasicDBObject("$in",aa)));	
				}else {
					paramConditionLst.add(new BasicDBObject(FieldType.CHANNEL.getName(), new BasicDBObject("$in",Arrays.asList(channelName))));
				}
				
			}

//			if (!minAmount.equalsIgnoreCase("")) {
//				paramConditionLst
//						.add(new BasicDBObject(FieldType.TOTAL_AMOUNT.getName(), new BasicDBObject("$gte", minAmount)));
//			}
//
//			if (!maxAmount.equalsIgnoreCase("")) {
//				paramConditionLst
//						.add(new BasicDBObject(FieldType.TOTAL_AMOUNT.getName(), new BasicDBObject("$lte", maxAmount)));
//			}
			if(StringUtils.isNoneBlank(minAmount)&& StringUtils.isNoneBlank(maxAmount)) {
				paramConditionLst.add(
					    new BasicDBObject("$expr",
					    new BasicDBObject("$and", Arrays.asList(new BasicDBObject("$gte", Arrays.asList(new BasicDBObject("$toDouble", "$TOTAL_AMOUNT"), Double.parseDouble(minAmount))),
					                    new BasicDBObject("$lte", Arrays.asList(new BasicDBObject("$toDouble", "$TOTAL_AMOUNT"), Double.parseDouble(maxAmount)))))));
			}

			if (!columnName.equalsIgnoreCase("") && !logicalCondition.equalsIgnoreCase("")
					&& !searchText.equalsIgnoreCase("")) {
				paramConditionLst.add(new BasicDBObject(columnName, new BasicDBObject(logicalCondition, searchText)));
			}
			
			if (!columnName1.equalsIgnoreCase("") && !logicalCondition1.equalsIgnoreCase("")
					&& !searchText1.equalsIgnoreCase("")) {
				paramConditionLst.add(new BasicDBObject(columnName1, new BasicDBObject(logicalCondition1, searchText1)));
			}
			
			if (!columnName2.equalsIgnoreCase("") && !logicalCondition2.equalsIgnoreCase("")
					&& !searchText2.equalsIgnoreCase("")) {
				paramConditionLst.add(new BasicDBObject(columnName2, new BasicDBObject(logicalCondition2, searchText2)));
			}
			
			//Added by Deep Singh code end here
			
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
			
			List<String>custMailIdList=new ArrayList<>();
			if (newDespositor.equalsIgnoreCase("true")) {
				
				List<Document> matchCase= Arrays.asList(new Document("$match", 
					    new Document("CUST_EMAIL", 
					    	    new Document("$ne", 
					    	    new BsonNull()))
					    	            .append("CREATE_DATE", 
					    	    new Document("$gte", fromDate)
					    	                .append("$lte", toDate))), 
					    	    new Document("$group", 
					    	    new Document("_id", "$CUST_EMAIL")
					    	            .append("transaction_count", 
					    	    new Document("$sum", 1L))), 
					    	    new Document("$match", 
					    	    new Document("transaction_count", 
					    	    new Document("$eq", 1L))), 
					    	    new Document("$project", 
					    	    new Document("_id", 0L)
					    	            .append("CUST_EMAIL", "$_id")));
				
				

				
				
				MongoCursor<Document> cursor1=coll.aggregate(matchCase).iterator();
				while (cursor1.hasNext()) {
					Document document = (Document) cursor1.next();
					custMailIdList.add(String.valueOf(document.get("CUST_EMAIL")));
					
				}
				
				
				
			}
			

			BasicDBObject match =null;
			BasicDBObject finalquery =null;
			if (newDespositor.equalsIgnoreCase("true")) {
				BasicDBObject dateQuery1 = new BasicDBObject();
				dateQuery1.put(FieldType.CREATE_DATE.getName(),
									BasicDBObjectBuilder.start("$gte", new SimpleDateFormat(fromDate).toLocalizedPattern())
											.add("$lte", new SimpleDateFormat(toDate).toLocalizedPattern()).get());
				//BasicDBObject orderidListserach=new BasicDBObject();
				dateQuery1.put(FieldType.CUST_EMAIL.getName(),new BasicDBObject("$in", custMailIdList));
				
				
				match=new BasicDBObject("$match", dateQuery1);
				finalquery=match;
			}else {

				 finalquery = new BasicDBObject("$and", fianlList);

				
				match=new BasicDBObject("$match", finalquery);
			}
			
			logger.info("1- Inside TxnReports , newSearchPayment method called, finalquery = " + finalquery);
			
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

	public List<TransactionSearchNew> newResellerSearchPayment(String pgRefNum, String orderId, String customerEmail,
			String customerPhone, String paymentType, String aliasStatus, String currency, String transactionType,
			String mopType, String acquirer, String merchantPayId, String fromDate, String toDate, User user, int start,
			int length, String tenantId, String ipAddress, String totalAmount, String rrn,String channelName,String minAmount,
			String maxAmount,String columnName,String logicalCondition,String searchText, String newDespositor,String columnName1,
			String logicalCondition1,String searchText1,String columnName2,String logicalCondition2,String searchText2, String smaId, String maId, String agentId) {


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
					List<String> payIdLst = userdao.getActiveMerchantList(user.getSegment(), user.getRole().getId()).stream().map(Merchants::getPayId).collect(Collectors.toList());
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

			if (StringUtils.isNotBlank(smaId) && !StringUtils.equalsIgnoreCase(smaId, "ALL")){
				List<String> payIdLst = userdao.getMerchantBySMAId(smaId).stream().map(User::getPayId).collect(Collectors.toList());
				logger.info("Get PayId List : " + payIdLst);
				if (!payIdLst.isEmpty()) {
					paramConditionLst
							.add(new BasicDBObject(FieldType.PAY_ID.getName(), new BasicDBObject("$in", payIdLst)));
				}
			}

			if (StringUtils.isNotBlank(maId) &&!StringUtils.equalsIgnoreCase(maId, "ALL")){
				List<String> payIdLst = userdao.getMerchantByMAId(maId).stream().map(User::getPayId).collect(Collectors.toList());
				logger.info("Get PayId List : " + payIdLst);
				if (!payIdLst.isEmpty()) {
					paramConditionLst
							.add(new BasicDBObject(FieldType.PAY_ID.getName(), new BasicDBObject("$in", payIdLst)));
				}
			}

			if (StringUtils.isNotBlank(agentId) && !StringUtils.equalsIgnoreCase(agentId, "ALL")){
				List<String> payIdLst = userdao.getMerchantByAgentId(agentId).stream().map(User::getPayId).collect(Collectors.toList());
				logger.info("Get PayId List : " + payIdLst);
				if (!payIdLst.isEmpty()) {
					paramConditionLst
							.add(new BasicDBObject(FieldType.PAY_ID.getName(), new BasicDBObject("$in", payIdLst)));
				}
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
				
				if (totalAmount.equals("0")) {

					logger.info(" totalAmount, @@@@@@@@@@@@@@oooooo>>>>>> = " + totalAmount);
					paramConditionLst.add(new BasicDBObject(FieldType.TOTAL_AMOUNT.getName(), null));

				} else {

					if (total.length == 1) {
						totalAmount = totalAmount + ".00";
						logger.info(" totalAmount, @@@@@@@@@@ppppppp>>>>>> = " + totalAmount);
					}
					paramConditionLst.add(new BasicDBObject(FieldType.TOTAL_AMOUNT.getName(), totalAmount));
					logger.info("Inside txnReports.java @@@@@oooooo totalAmount ======================= = "
							+ totalAmount);
				}
				
			
//					if(total.length == 1) {
//					totalAmount = totalAmount + ".00";
//					logger.info(" totalAmount, =======<1========================= = " + totalAmount);
//					paramConditionLst.add(new BasicDBObject(FieldType.TOTAL_AMOUNT.getName(), totalAmount));
//									
//				}
//					
//				
//				//paramConditionLst.add(new BasicDBObject(FieldType.TOTAL_AMOUNT.getName(), totalAmount));
//				logger.info(
//						"Inside txnReports.java  totalAmount, ==========s========================= = " + totalAmount);

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
			 * "endDateTime========================= = " , endDateTime);
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
			 * logger.info("newSearchPayment++++++++++++++++++++++++++++++" , endTime);
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
			 * , endDateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd hh:mm:ss")));
			 * logger.
			 * info("endDateTime.format(DateTimeFormatter.ofPattern(\"yyyy-MM-dd hh:mm:ss\")))), ==========s========================= = "
			 * + startDateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd hh:mm:ss")));
			 * 
			 * 
			 * }
			 * 
			 */
			
			//Added by Deep Singh code start here
			
			logger.info("channelName::: " + channelName );
			logger.info("minAmount::: " + minAmount );
			logger.info("maxAmount::: " + maxAmount );
			logger.info("columnName::: " + columnName );
			logger.info("logicalCondition::: " + logicalCondition );
			logger.info("searchText::: " + searchText );
			
			
			if (channelName.equalsIgnoreCase("All")) {
//				paramConditionLst.add(new BasicDBObject(FieldType.CHANNEL.getName(),
//						new BasicDBObject("$in", Arrays.asList("Fiat", "Crypto"))));
			} else {
				if(channelName.contains(",")) {
					String [] channels=channelName.split(",");
					ArrayList<String> aa=new ArrayList<>();
					for(String channel:channels) {
						aa.add(channel);
					}
					paramConditionLst.add(new BasicDBObject(FieldType.CHANNEL.getName(), new BasicDBObject("$in",aa)));	
				}else {
					paramConditionLst.add(new BasicDBObject(FieldType.CHANNEL.getName(), new BasicDBObject("$in",Arrays.asList(channelName))));
				}
				
			}

//			if (!minAmount.equalsIgnoreCase("")) {
//				paramConditionLst
//						.add(new BasicDBObject(FieldType.TOTAL_AMOUNT.getName(), new BasicDBObject("$gte", minAmount)));
//			}
//
//			if (!maxAmount.equalsIgnoreCase("")) {
//				paramConditionLst
//						.add(new BasicDBObject(FieldType.TOTAL_AMOUNT.getName(), new BasicDBObject("$lte", maxAmount)));
//			}
			if(StringUtils.isNoneBlank(minAmount)&& StringUtils.isNoneBlank(maxAmount)) {
				paramConditionLst.add(
					    new BasicDBObject("$expr",
					    new BasicDBObject("$and", Arrays.asList(new BasicDBObject("$gte", Arrays.asList(new BasicDBObject("$toDouble", "$TOTAL_AMOUNT"), Double.parseDouble(minAmount))),
					                    new BasicDBObject("$lte", Arrays.asList(new BasicDBObject("$toDouble", "$TOTAL_AMOUNT"), Double.parseDouble(maxAmount)))))));
			}

			if (!columnName.equalsIgnoreCase("") && !logicalCondition.equalsIgnoreCase("")
					&& !searchText.equalsIgnoreCase("")) {
				paramConditionLst.add(new BasicDBObject(columnName, new BasicDBObject(logicalCondition, searchText)));
			}
			
			if (!columnName1.equalsIgnoreCase("") && !logicalCondition1.equalsIgnoreCase("")
					&& !searchText1.equalsIgnoreCase("")) {
				paramConditionLst.add(new BasicDBObject(columnName1, new BasicDBObject(logicalCondition1, searchText1)));
			}
			
			if (!columnName2.equalsIgnoreCase("") && !logicalCondition2.equalsIgnoreCase("")
					&& !searchText2.equalsIgnoreCase("")) {
				paramConditionLst.add(new BasicDBObject(columnName2, new BasicDBObject(logicalCondition2, searchText2)));
			}
			
			//Added by Deep Singh code end here
			
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

			List<String>custEmailIdList=new ArrayList<>();
			if (newDespositor.equalsIgnoreCase("true")) {
				
				List<Document> matchCase= Arrays.asList(new Document("$match", 
					    new Document("CUST_EMAIL", 
					    	    new Document("$ne", 
					    	    new BsonNull()))
					    	            .append("CREATE_DATE", 
					    	    new Document("$gte", fromDate)
					    	                .append("$lte", toDate))), 
					    	    new Document("$group", 
					    	    new Document("_id", "$CUST_EMAIL")
					    	            .append("transaction_count", 
					    	    new Document("$sum", 1L))), 
					    	    new Document("$match", 
					    	    new Document("transaction_count", 
					    	    new Document("$eq", 1L))), 
					    	    new Document("$project", 
					    	    new Document("_id", 0L)
					    	            .append("CUST_EMAIL", "$_id")));
				
				
				
				

				
				
				MongoCursor<Document> cursor1=coll.aggregate(matchCase).iterator();
				while (cursor1.hasNext()) {
					Document document = (Document) cursor1.next();
					custEmailIdList.add(String.valueOf(document.get("CUST_EMAIL")));
					
				}
				
				
				
			}
			

			BasicDBObject match =null;
			BasicDBObject finalquery =null;
			if (newDespositor.equalsIgnoreCase("true")) {
				
				
				
				BasicDBObject dateQuery1 = new BasicDBObject();
				dateQuery1.put(FieldType.CREATE_DATE.getName(),
									BasicDBObjectBuilder.start("$gte", new SimpleDateFormat(fromDate).toLocalizedPattern())
											.add("$lte", new SimpleDateFormat(toDate).toLocalizedPattern()).get());
				//BasicDBObject orderidListserach=new BasicDBObject();
				dateQuery1.put(FieldType.CUST_EMAIL.getName(),new BasicDBObject("$in", custEmailIdList));
				
				
				match=new BasicDBObject("$match", dateQuery1);
				finalquery=match;
			}else {

				 finalquery = new BasicDBObject("$and", fianlList);

				
				match=new BasicDBObject("$match", finalquery);
			}
			
			logger.info("1- Inside TxnReports , newSearchPayment method called, finalquery = " + finalquery);

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
				
				if (null != dbobj.getString(FieldType.CURRENCY_CODE.toString())) {
					transReport.setCurrency(currencyCodeDao.getCurrencyNamebyCode(dbobj.getString(FieldType.CURRENCY_CODE.toString())));
				} else {
					transReport.setCurrency(CrmFieldConstants.NA.getValue());
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
					transReport.setCurrency(currencyCodeDao.getCurrencyNamebyCode(dbobj.getString(FieldType.CURRENCY_CODE.toString())));
				} else {
					transReport.setCurrency(CrmFieldConstants.NA.getValue());
				}

				if (null != dbobj.getString(FieldType.ACQUIRER_TYPE.toString())) {
					transReport.setAcquirerType(dbobj.getString(FieldType.ACQUIRER_TYPE.toString()));
				}

				if (StringUtils.isNotBlank(dbobj.getString(FieldType.PG_TDR_SC.toString()))) {
					transReport.setPgSurchargeAmount(
							String.valueOf( ((Double.valueOf(dbobj.getString(FieldType.PG_TDR_SC.toString()))))));
				} else {
					transReport.setPgSurchargeAmount("0.00");
				}

				if (StringUtils.isNotBlank(dbobj.getString(FieldType.ACQUIRER_TDR_SC.toString()))) {
					transReport.setAcquirerSurchargeAmount(String.valueOf(
							(Double.valueOf(dbobj.getString(FieldType.ACQUIRER_TDR_SC.toString())))));
				} else {
					transReport.setAcquirerSurchargeAmount("0.00");
				}

				if (StringUtils.isNotBlank(dbobj.getString(FieldType.PG_GST.toString()))) {
					transReport.setPgGst(
							String.valueOf(((Double.valueOf(dbobj.getString(FieldType.PG_GST.toString()))))));
				} else {
					transReport.setPgGst("0.00");
				}

				if (StringUtils.isNotBlank(dbobj.getString(FieldType.ACQUIRER_GST.toString()))) {
					transReport.setAcquirerGst(String.valueOf(
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

}
