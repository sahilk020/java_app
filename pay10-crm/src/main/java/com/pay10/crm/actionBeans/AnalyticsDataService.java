package com.pay10.crm.actionBeans;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
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
import org.springframework.stereotype.Service;

import com.mongodb.BasicDBObject;
import com.mongodb.BasicDBObjectBuilder;
import com.mongodb.QueryBuilder;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import com.pay10.commons.dao.ServiceTaxDao;
import com.pay10.commons.exception.ErrorType;
import com.pay10.commons.mongo.MongoInstance;
import com.pay10.commons.user.AccountCurrencyRegion;
import com.pay10.commons.user.AnalyticsData;
import com.pay10.commons.user.CardHolderType;
import com.pay10.commons.user.FunnelChatData;
import com.pay10.commons.user.ServiceTax;
import com.pay10.commons.user.Surcharge;
import com.pay10.commons.user.SurchargeDao;
import com.pay10.commons.user.User;
import com.pay10.commons.user.UserDao;
import com.pay10.commons.user.UserType;
import com.pay10.commons.util.AcquirerType;
import com.pay10.commons.util.Constants;
import com.pay10.commons.util.FieldType;
import com.pay10.commons.util.MopType;
import com.pay10.commons.util.MopTypeUI;
import com.pay10.commons.util.PaymentType;
import com.pay10.commons.util.PropertiesManager;
import com.pay10.commons.util.StatusType;
import com.pay10.commons.util.StatusTypePayout;
import com.pay10.commons.util.TransactionType;
import com.pay10.commons.util.TxnType;
import com.pay10.crm.action.TdrPojo;
import java.util.regex.Pattern;

@Service
public class AnalyticsDataService {

	@Autowired
	private MongoInstance mongoInstance;

	@Autowired
	private UserDao userDao;

	@Autowired
	private ServiceTaxDao serviceTaxDao;

	@Autowired
	private SurchargeDao surchargeDao;

	@Autowired
	private PropertiesManager propertiesManager;

	private static Logger logger = LoggerFactory.getLogger(AnalyticsDataService.class.getName());
	private static final String prefix = "MONGO_DB_";
	public static final BigDecimal ONE_HUNDRED = new BigDecimal(100);

	private static final String CAPTURED = "Captured";
	private static final String SETTLED = "Settled";
	private static final String ERROR = "Error";
	private static final String TIMEOUT = "Timeout";
	private static final String CANCELLED = "Cancelled";
	private static final String DENIED = "Denied by risk";
	private static final String REJECTED = "Rejected";
	private static final String FAILED = "Failed";
	private static final String INVALID = "Invalid";
	private static final String DENIED_BY_FRAUD = "Denied due to fraud";
	private static final String SALE = "SALE";
	List<Surcharge> surchargeList = new ArrayList<Surcharge>();
	Map<String, List<ServiceTax>> serviceTaxMap = new HashMap<String, List<ServiceTax>>();
	private double postSettledTransactionCount = 0;
	Map<String, User> userMap = new HashMap<String, User>();

	public AnalyticsData getTransactionCount(String fromDate, String toDate, String payId, String paymentType,
			String acquirer, User user, String param, String transactionType, String mopType, String currency) {

		logger.info(" inside getTransactionCount for AnalyticsDataService ");

		try {
			BasicDBObject dateQuery = new BasicDBObject();
			List<BasicDBObject> paramConditionLst = new ArrayList<BasicDBObject>();
			BasicDBObject acquirerQuery = new BasicDBObject();
			List<BasicDBObject> mopTypeConditionLst = new ArrayList<BasicDBObject>();
			BasicDBObject mopTypeQuery = new BasicDBObject();

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
			 * if (!payId.equalsIgnoreCase("ALL")) { paramConditionLst.add(new
			 * BasicDBObject(FieldType.PAY_ID.getName(), payId)); }
			 */
			if (!payId.equalsIgnoreCase("ALL")) {
//				List<String> payIdLst = userDao.getPayIdForSplitPaymentMerchant(user.getSegment());
//				logger.info("Get PayId For SplitPayment Merchant : " + payIdLst);
//				if (payIdLst.size() > 0) {
//					paramConditionLst
//							.add(new BasicDBObject(FieldType.PAY_ID.getName(), new BasicDBObject("$in", payIdLst)));
//				}

                paramConditionLst.add(new BasicDBObject(FieldType.PAY_ID.getName(), payId));

            }
//			paramConditionLst.add(
//				    new BasicDBObject(FieldType.STATUS.getName(), 
//				        new BasicDBObject("$nin", Arrays.asList(
//				            Pattern.compile(StatusTypePayout.REQUEST_ACCEPTED.getName(), Pattern.CASE_INSENSITIVE)
//				        ))
//				    )
//				);
			
			paramConditionLst.add(
				    new BasicDBObject(FieldType.ALIAS_STATUS.getName(), 
				        new BasicDBObject("$in", Arrays.asList("Failed","Cancelled","Invalid","Captured","Settled")
				        ))
				    );

			if (user.getUserType().equals(UserType.RESELLER)) {
				paramConditionLst.add(new BasicDBObject(FieldType.RESELLER_ID.getName(), user.getResellerId()));
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

			if (!paymentType.equalsIgnoreCase("ALL")) {
				paramConditionLst.add(new BasicDBObject(FieldType.CURRENCY_CODE.getName(), currency));
			}

			if (!transactionType.equalsIgnoreCase("ALL")) {
				paramConditionLst.add(new BasicDBObject(FieldType.ORIG_TXNTYPE.getName(), transactionType));
			}

			if (!paramConditionLst.isEmpty()) {
				allParamQuery = new BasicDBObject("$and", paramConditionLst);
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

			/*
			 * // SALE Captured query List<BasicDBObject> txnConditionsList = new
			 * ArrayList<BasicDBObject>(); List<BasicDBObject> saleCapturedConditionList =
			 * new ArrayList<BasicDBObject>();
			 * 
			 * if (!transactionType.equalsIgnoreCase("ALL")) { // paramConditionLst.add(new
			 * BasicDBObject(FieldType.ORIG_TXNTYPE.getName(), transactionType));
			 * saleCapturedConditionList .add(new
			 * BasicDBObject(FieldType.ORIG_TXNTYPE.getName(), transactionType)); }else {
			 * saleCapturedConditionList .add(new
			 * BasicDBObject(FieldType.ORIG_TXNTYPE.getName(),
			 * TransactionType.REFUND.getName())); saleCapturedConditionList .add(new
			 * BasicDBObject(FieldType.ORIG_TXNTYPE.getName(),
			 * TransactionType.SALE.getName())); }
			 * 
			 * 
			 * 
			 * BasicDBObject saleConditionQuery = new BasicDBObject("$or",
			 * saleCapturedConditionList); txnConditionsList.add(saleConditionQuery);
			 * 
			 * 
			 * BasicDBObject saleCapturedConditionQuery = new BasicDBObject("$or",
			 * txnConditionsList);
			 */

			List<BasicDBObject> allConditionQueryList = new ArrayList<BasicDBObject>();

			if (!dateQuery.isEmpty()) {
				allConditionQueryList.add(dateQuery);
			}

			if (!dateIndexConditionQuery.isEmpty()) {
				allConditionQueryList.add(dateIndexConditionQuery);
			}

//			allConditionQueryList.add(saleCapturedConditionQuery);

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
			if (!mopTypeQuery.isEmpty()) {
				fianlList.add(mopTypeQuery);
			}
			logger.info("Query For performence Report : "+fianlList);
			BasicDBObject finalQuery = new BasicDBObject("$and", fianlList);
			MongoDatabase dbIns = mongoInstance.getDB();
			MongoCollection<Document> coll = dbIns.getCollection(
					PropertiesManager.propertiesMap.get(prefix + Constants.TRANSACTION_STATUS_COLLECTION.getValue()));

			double ccSuccess = 0;
			double ccFailed = 0;
			double ccCancelled = 0;
			double ccInvalid = 0;

			double dcSuccess = 0;
			double dcFailed = 0;
			double dcCancelled = 0;
			double dcInvalid = 0;

			double upSuccess = 0;
			double upFailed = 0;
			double upCancelled = 0;
			double upInvalid = 0;

			double wlSuccess = 0;
			double wlFailed = 0;
			double wlCancelled = 0;
			double wlInvalid = 0;

			double nbSuccess = 0;
			double nbFailed = 0;
			double nbCancelled = 0;
			double nbInvalid = 0;

			double totalTxn = 0;
			double totalCCTxn = 0;
			double totalDCTxn = 0;
			double totalUPTxn = 0;
			double totalWLTxn = 0;
			double totalNBTxn = 0;
			int unknownTransactions = 0;

			BigDecimal totalTxnAmount = BigDecimal.ZERO.setScale(2, RoundingMode.HALF_DOWN);
			BigDecimal totalCCTxnAmount = BigDecimal.ZERO.setScale(2, RoundingMode.HALF_DOWN);
			BigDecimal totalDCTxnAmount = BigDecimal.ZERO.setScale(2, RoundingMode.HALF_DOWN);
			BigDecimal totalUPTxnAmount = BigDecimal.ZERO.setScale(2, RoundingMode.HALF_DOWN);
			BigDecimal totalWLTxnAmount = BigDecimal.ZERO.setScale(2, RoundingMode.HALF_DOWN);
			BigDecimal totalNBTxnAmount = BigDecimal.ZERO.setScale(2, RoundingMode.HALF_DOWN);

			BigDecimal capturedTotalAmount = BigDecimal.ZERO.setScale(2, RoundingMode.HALF_DOWN);
			BigDecimal failedTotalAmount = BigDecimal.ZERO.setScale(2, RoundingMode.HALF_DOWN);
			BigDecimal cancelledTotalAmount = BigDecimal.ZERO.setScale(2, RoundingMode.HALF_DOWN);
			BigDecimal invalidTotalAmount = BigDecimal.ZERO.setScale(2, RoundingMode.HALF_DOWN);

			MongoCursor<Document> cursor = coll.find(finalQuery).iterator();

			// Remove all data from an earlier map
			while (cursor.hasNext()) {

				Document dbobj = cursor.next();

				String aliasStatus = dbobj.getString(FieldType.ALIAS_STATUS.toString());
				String pType = dbobj.getString(FieldType.PAYMENT_TYPE.toString());

				if (StringUtils.isNotBlank(aliasStatus)) {
					totalTxn++;
				}

				if (StringUtils.isBlank(dbobj.getString(FieldType.PAYMENT_TYPE.toString()))) {
					unknownTransactions++;
					continue;
				}

				if (null != dbobj.getString(FieldType.PAYMENT_TYPE.toString())
						&& dbobj.getString(FieldType.PAYMENT_TYPE.toString()).equalsIgnoreCase("CARD")) {
					unknownTransactions++;
					continue;
				}

				switch (PaymentType.getInstanceUsingCode(dbobj.getString(FieldType.PAYMENT_TYPE.toString()))) {

				case DEBIT_CARD:

					totalDCTxn++;
					switch (dbobj.getString(FieldType.ALIAS_STATUS.toString())) {

					case CAPTURED:

						if (!StringUtils.isBlank(dbobj.getString(FieldType.TOTAL_AMOUNT.toString()))) {
							BigDecimal txnAmount = new BigDecimal(dbobj.getString(FieldType.TOTAL_AMOUNT.toString()))
									.setScale(2, RoundingMode.HALF_DOWN);
							totalTxnAmount = totalTxnAmount.add(txnAmount).setScale(2, RoundingMode.HALF_DOWN);
							totalDCTxnAmount = totalDCTxnAmount.add(txnAmount).setScale(2, RoundingMode.HALF_DOWN);
							capturedTotalAmount = capturedTotalAmount.add(txnAmount).setScale(2,
									RoundingMode.HALF_DOWN);
						}

						dcSuccess++;
						break;

					case SETTLED:

						if (!StringUtils.isBlank(dbobj.getString(FieldType.TOTAL_AMOUNT.toString()))) {
							BigDecimal txnAmount = new BigDecimal(dbobj.getString(FieldType.TOTAL_AMOUNT.toString()))
									.setScale(2, RoundingMode.HALF_DOWN);
							totalTxnAmount = totalTxnAmount.add(txnAmount).setScale(2, RoundingMode.HALF_DOWN);
							totalDCTxnAmount = totalDCTxnAmount.add(txnAmount).setScale(2, RoundingMode.HALF_DOWN);
						}

						dcSuccess++;
						break;

					case ERROR:
						dcFailed++;
						break;

					case CANCELLED:

						if (!StringUtils.isBlank(dbobj.getString(FieldType.TOTAL_AMOUNT.toString()))) {
							BigDecimal txnAmount = new BigDecimal(dbobj.getString(FieldType.TOTAL_AMOUNT.toString()))
									.setScale(2, RoundingMode.HALF_DOWN);
							cancelledTotalAmount = cancelledTotalAmount.add(txnAmount).setScale(2,
									RoundingMode.HALF_DOWN);
						}

						dcCancelled++;
						break;

					case FAILED:

						if (!StringUtils.isBlank(dbobj.getString(FieldType.TOTAL_AMOUNT.toString()))) {
							BigDecimal txnAmount = new BigDecimal(dbobj.getString(FieldType.TOTAL_AMOUNT.toString()))
									.setScale(2, RoundingMode.HALF_DOWN);
							failedTotalAmount = failedTotalAmount.add(txnAmount).setScale(2, RoundingMode.HALF_DOWN);
						}

						dcFailed++;
						break;

					case INVALID:

						if (!StringUtils.isBlank(dbobj.getString(FieldType.TOTAL_AMOUNT.toString()))) {
							BigDecimal txnAmount = new BigDecimal(dbobj.getString(FieldType.TOTAL_AMOUNT.toString()))
									.setScale(2, RoundingMode.HALF_DOWN);
							invalidTotalAmount = invalidTotalAmount.add(txnAmount).setScale(2, RoundingMode.HALF_DOWN);
						}

						dcInvalid++;
						break;

					default:
						break;
					}

					break;

				case CREDIT_CARD:

					totalCCTxn++;
					switch (dbobj.getString(FieldType.ALIAS_STATUS.toString())) {

					case CAPTURED:

						if (!StringUtils.isBlank(dbobj.getString(FieldType.TOTAL_AMOUNT.toString()))) {
							BigDecimal txnAmount = new BigDecimal(dbobj.getString(FieldType.TOTAL_AMOUNT.toString()))
									.setScale(2, RoundingMode.HALF_DOWN);
							;
							totalTxnAmount = totalTxnAmount.add(txnAmount).setScale(2, RoundingMode.HALF_DOWN);
							totalCCTxnAmount = totalCCTxnAmount.add(txnAmount).setScale(2, RoundingMode.HALF_DOWN);
							capturedTotalAmount = capturedTotalAmount.add(txnAmount).setScale(2,
									RoundingMode.HALF_DOWN);
						}

						ccSuccess++;

						break;

					case SETTLED:

						if (!StringUtils.isBlank(dbobj.getString(FieldType.TOTAL_AMOUNT.toString()))) {
							BigDecimal txnAmount = new BigDecimal(dbobj.getString(FieldType.TOTAL_AMOUNT.toString()))
									.setScale(2, RoundingMode.HALF_DOWN);
							;
							totalTxnAmount = totalTxnAmount.add(txnAmount).setScale(2, RoundingMode.HALF_DOWN);
							totalCCTxnAmount = totalCCTxnAmount.add(txnAmount).setScale(2, RoundingMode.HALF_DOWN);
						}

						ccSuccess++;

						break;

					case ERROR:

						ccFailed++;
						break;

					case CANCELLED:
						if (!StringUtils.isBlank(dbobj.getString(FieldType.TOTAL_AMOUNT.toString()))) {
							BigDecimal txnAmount = new BigDecimal(dbobj.getString(FieldType.TOTAL_AMOUNT.toString()))
									.setScale(2, RoundingMode.HALF_DOWN);
							cancelledTotalAmount = cancelledTotalAmount.add(txnAmount).setScale(2,
									RoundingMode.HALF_DOWN);
						}
						ccCancelled++;
						break;

					case FAILED:
						if (!StringUtils.isBlank(dbobj.getString(FieldType.TOTAL_AMOUNT.toString()))) {
							BigDecimal txnAmount = new BigDecimal(dbobj.getString(FieldType.TOTAL_AMOUNT.toString()))
									.setScale(2, RoundingMode.HALF_DOWN);
							failedTotalAmount = failedTotalAmount.add(txnAmount).setScale(2, RoundingMode.HALF_DOWN);
						}
						ccFailed++;
						break;

					case INVALID:
						if (!StringUtils.isBlank(dbobj.getString(FieldType.TOTAL_AMOUNT.toString()))) {
							BigDecimal txnAmount = new BigDecimal(dbobj.getString(FieldType.TOTAL_AMOUNT.toString()))
									.setScale(2, RoundingMode.HALF_DOWN);
							invalidTotalAmount = invalidTotalAmount.add(txnAmount).setScale(2, RoundingMode.HALF_DOWN);
						}
						ccInvalid++;
						break;

					default:
						break;
					}

					break;

				case WALLET:

					totalWLTxn++;
					switch (dbobj.getString(FieldType.ALIAS_STATUS.toString())) {

					case CAPTURED:

						if (!StringUtils.isBlank(dbobj.getString(FieldType.TOTAL_AMOUNT.toString()))) {
							BigDecimal txnAmount = new BigDecimal(dbobj.getString(FieldType.TOTAL_AMOUNT.toString()))
									.setScale(2, RoundingMode.HALF_DOWN);
							totalTxnAmount = totalTxnAmount.add(txnAmount).setScale(2, RoundingMode.HALF_DOWN);
							totalWLTxnAmount = totalWLTxnAmount.add(txnAmount).setScale(2, RoundingMode.HALF_DOWN);
							capturedTotalAmount = capturedTotalAmount.add(txnAmount).setScale(2,
									RoundingMode.HALF_DOWN);
						}

						wlSuccess++;
						break;

					case SETTLED:

						if (!StringUtils.isBlank(dbobj.getString(FieldType.TOTAL_AMOUNT.toString()))) {
							BigDecimal txnAmount = new BigDecimal(dbobj.getString(FieldType.TOTAL_AMOUNT.toString()))
									.setScale(2, RoundingMode.HALF_DOWN);
							totalTxnAmount = totalTxnAmount.add(txnAmount).setScale(2, RoundingMode.HALF_DOWN);
							totalWLTxnAmount = totalWLTxnAmount.add(txnAmount).setScale(2, RoundingMode.HALF_DOWN);
						}

						wlSuccess++;
						break;
					case ERROR:
						wlFailed++;
						break;

					case CANCELLED:
						if (!StringUtils.isBlank(dbobj.getString(FieldType.TOTAL_AMOUNT.toString()))) {
							BigDecimal txnAmount = new BigDecimal(dbobj.getString(FieldType.TOTAL_AMOUNT.toString()))
									.setScale(2, RoundingMode.HALF_DOWN);
							cancelledTotalAmount = cancelledTotalAmount.add(txnAmount).setScale(2,
									RoundingMode.HALF_DOWN);
						}
						wlCancelled++;
						break;

					case FAILED:
						if (!StringUtils.isBlank(dbobj.getString(FieldType.TOTAL_AMOUNT.toString()))) {
							BigDecimal txnAmount = new BigDecimal(dbobj.getString(FieldType.TOTAL_AMOUNT.toString()))
									.setScale(2, RoundingMode.HALF_DOWN);
							failedTotalAmount = failedTotalAmount.add(txnAmount).setScale(2, RoundingMode.HALF_DOWN);
						}
						wlFailed++;
						break;

					case INVALID:
						if (!StringUtils.isBlank(dbobj.getString(FieldType.TOTAL_AMOUNT.toString()))) {
							BigDecimal txnAmount = new BigDecimal(dbobj.getString(FieldType.TOTAL_AMOUNT.toString()))
									.setScale(2, RoundingMode.HALF_DOWN);
							invalidTotalAmount = invalidTotalAmount.add(txnAmount).setScale(2, RoundingMode.HALF_DOWN);
						}
						wlInvalid++;
						break;

					default:
						break;
					}

					break;

				case UPI:

					totalUPTxn++;
					switch (dbobj.getString(FieldType.ALIAS_STATUS.toString())) {

					case CAPTURED:

						if (!StringUtils.isBlank(dbobj.getString(FieldType.TOTAL_AMOUNT.toString()))) {
							BigDecimal txnAmount = new BigDecimal(dbobj.getString(FieldType.TOTAL_AMOUNT.toString()))
									.setScale(2, RoundingMode.HALF_DOWN);
							totalTxnAmount = totalTxnAmount.add(txnAmount).setScale(2, RoundingMode.HALF_DOWN);
							totalUPTxnAmount = totalUPTxnAmount.add(txnAmount).setScale(2, RoundingMode.HALF_DOWN);
							capturedTotalAmount = capturedTotalAmount.add(txnAmount).setScale(2,
									RoundingMode.HALF_DOWN);

						}

						upSuccess++;
						break;

					case SETTLED:

						if (!StringUtils.isBlank(dbobj.getString(FieldType.TOTAL_AMOUNT.toString()))) {
							BigDecimal txnAmount = new BigDecimal(dbobj.getString(FieldType.TOTAL_AMOUNT.toString()))
									.setScale(2, RoundingMode.HALF_DOWN);
							totalTxnAmount = totalTxnAmount.add(txnAmount).setScale(2, RoundingMode.HALF_DOWN);
							totalUPTxnAmount = totalUPTxnAmount.add(txnAmount).setScale(2, RoundingMode.HALF_DOWN);

						}

						upSuccess++;
						break;
					case ERROR:
						upFailed++;
						break;

					case CANCELLED:
						if (!StringUtils.isBlank(dbobj.getString(FieldType.TOTAL_AMOUNT.toString()))) {
							BigDecimal txnAmount = new BigDecimal(dbobj.getString(FieldType.TOTAL_AMOUNT.toString()))
									.setScale(2, RoundingMode.HALF_DOWN);
							cancelledTotalAmount = cancelledTotalAmount.add(txnAmount).setScale(2,
									RoundingMode.HALF_DOWN);
						}
						upCancelled++;
						break;

					case FAILED:
						if (!StringUtils.isBlank(dbobj.getString(FieldType.TOTAL_AMOUNT.toString()))) {
							BigDecimal txnAmount = new BigDecimal(dbobj.getString(FieldType.TOTAL_AMOUNT.toString()))
									.setScale(2, RoundingMode.HALF_DOWN);
							failedTotalAmount = failedTotalAmount.add(txnAmount).setScale(2, RoundingMode.HALF_DOWN);
						}
						upFailed++;
						break;

					case INVALID:
						if (!StringUtils.isBlank(dbobj.getString(FieldType.TOTAL_AMOUNT.toString()))) {
							BigDecimal txnAmount = new BigDecimal(dbobj.getString(FieldType.TOTAL_AMOUNT.toString()))
									.setScale(2, RoundingMode.HALF_DOWN);
							invalidTotalAmount = invalidTotalAmount.add(txnAmount).setScale(2, RoundingMode.HALF_DOWN);
						}
						upInvalid++;
						break;

					default:
						break;
					}

					break;

				case NET_BANKING:

					totalNBTxn++;
					switch (dbobj.getString(FieldType.ALIAS_STATUS.toString())) {

					case CAPTURED:

						if (!StringUtils.isBlank(dbobj.getString(FieldType.TOTAL_AMOUNT.toString()))) {
							BigDecimal txnAmount = new BigDecimal(dbobj.getString(FieldType.TOTAL_AMOUNT.toString()))
									.setScale(2, RoundingMode.HALF_DOWN);
							totalTxnAmount = totalTxnAmount.add(txnAmount).setScale(2, RoundingMode.HALF_DOWN);
							totalNBTxnAmount = totalNBTxnAmount.add(txnAmount).setScale(2, RoundingMode.HALF_DOWN);
							capturedTotalAmount = capturedTotalAmount.add(txnAmount).setScale(2,
									RoundingMode.HALF_DOWN);
						}

						nbSuccess++;
						break;

					case SETTLED:

						if (!StringUtils.isBlank(dbobj.getString(FieldType.TOTAL_AMOUNT.toString()))) {
							BigDecimal txnAmount = new BigDecimal(dbobj.getString(FieldType.TOTAL_AMOUNT.toString()))
									.setScale(2, RoundingMode.HALF_DOWN);
							totalTxnAmount = totalTxnAmount.add(txnAmount).setScale(2, RoundingMode.HALF_DOWN);
							totalNBTxnAmount = totalNBTxnAmount.add(txnAmount).setScale(2, RoundingMode.HALF_DOWN);
						}

						nbSuccess++;
						break;

					case ERROR:
						nbFailed++;
						break;

					case CANCELLED:
						if (!StringUtils.isBlank(dbobj.getString(FieldType.TOTAL_AMOUNT.toString()))) {
							BigDecimal txnAmount = new BigDecimal(dbobj.getString(FieldType.TOTAL_AMOUNT.toString()))
									.setScale(2, RoundingMode.HALF_DOWN);
							cancelledTotalAmount = cancelledTotalAmount.add(txnAmount).setScale(2,
									RoundingMode.HALF_DOWN);
						}
						nbCancelled++;
						break;

					case FAILED:
						if (!StringUtils.isBlank(dbobj.getString(FieldType.TOTAL_AMOUNT.toString()))) {
							BigDecimal txnAmount = new BigDecimal(dbobj.getString(FieldType.TOTAL_AMOUNT.toString()))
									.setScale(2, RoundingMode.HALF_DOWN);
							failedTotalAmount = failedTotalAmount.add(txnAmount).setScale(2, RoundingMode.HALF_DOWN);
						}
						nbFailed++;
						break;

					case INVALID:
						if (!StringUtils.isBlank(dbobj.getString(FieldType.TOTAL_AMOUNT.toString()))) {
							BigDecimal txnAmount = new BigDecimal(dbobj.getString(FieldType.TOTAL_AMOUNT.toString()))
									.setScale(2, RoundingMode.HALF_DOWN);
							invalidTotalAmount = invalidTotalAmount.add(txnAmount).setScale(2, RoundingMode.HALF_DOWN);
						}
						nbInvalid++;
						break;

					default:
						break;
					}

					break;

				default:
					break;

				}

			}

			cursor.close();

			// totalTxnAmount = totalTxnAmount.setScale(2, RoundingMode.HALF_DOWN);
			AnalyticsData analyticsData = new AnalyticsData();
			// logger.info(" inside getTransactionCount for Preparing iPaycapturedData SMS
			// for analyticsData " );
			double totalTxnSuccess = ccSuccess + dcSuccess + upSuccess + wlSuccess + nbSuccess;
			double totalFailed = ccFailed + dcFailed + upFailed + wlFailed + nbFailed;

			analyticsData.setTotalTxnCount(String.format("%.0f", totalTxn));
			analyticsData.setSuccessTxnCount(String.format("%.0f", totalTxnSuccess));
			analyticsData.setFailedTxnCount(String.format("%.2f", totalFailed));

			// logger.info(" inside getTransactionCount for Preparing iPaycapturedData SMS
			// for analyticsData ");

			if (totalTxn == 0.00) {
				analyticsData.setSuccessTxnPercent("0.00");
			} else {

				double successTxnPercent = (totalTxnSuccess / (totalTxn)) * 100;
				Double successTxnPercentInDouble = new Double(successTxnPercent);
				if (successTxnPercentInDouble.isNaN()) {
					analyticsData.setSuccessTxnPercent("0.00");
				} else {
					analyticsData.setSuccessTxnPercent(String.format("%.2f", successTxnPercent));
				}
			}

			BigDecimal totalTxnCountBD = new BigDecimal(totalTxnSuccess);
			totalTxnCountBD = totalTxnCountBD.setScale(2, RoundingMode.HALF_DOWN);

			if (totalTxnCountBD.compareTo(BigDecimal.ONE) < 0) {
				analyticsData.setAvgTkt("0.00");
			} else {

				String totalTxnAmountString = String.valueOf(totalTxnAmount);

				double totalTxnAmountDouble = Double.valueOf(totalTxnAmountString);
				double totalTxnSuccessDouble = Double.valueOf(totalTxnSuccess);
				double avgTicketSizeDouble = totalTxnAmountDouble / totalTxnSuccessDouble;

				analyticsData.setAvgTkt(String.format("%.2f", avgTicketSizeDouble));
			}
			// Percentage Share
			if (totalTxnSuccess < 1) {
				analyticsData.setCCTxnPercent("0.00");
				analyticsData.setDCTxnPercent("0.00");
				analyticsData.setUPTxnPercent("0.00");
				analyticsData.setWLTxnPercent("0.00");
				analyticsData.setNBTxnPercent("0.00");
				analyticsData.setOverallTotalPercent("0.00");
			}

			else {

				double cCTxnPercent = (ccSuccess / totalTxnSuccess) * 100;
				double dCTxnPercent = (dcSuccess / totalTxnSuccess) * 100;
				double upTxnPercent = (upSuccess / totalTxnSuccess) * 100;
				double wlTxnPercent = (wlSuccess / totalTxnSuccess) * 100;
				double nBTxnPercent = (nbSuccess / totalTxnSuccess) * 100;
				double allTotalPercent = (cCTxnPercent + dCTxnPercent + upTxnPercent + wlTxnPercent + nBTxnPercent);

				analyticsData.setCCTxnPercent(String.format("%.2f", cCTxnPercent));
				analyticsData.setDCTxnPercent(String.format("%.2f", dCTxnPercent));
				analyticsData.setUPTxnPercent(String.format("%.2f", upTxnPercent));
				analyticsData.setWLTxnPercent(String.format("%.2f", wlTxnPercent));
				analyticsData.setNBTxnPercent(String.format("%.2f", nBTxnPercent));
				analyticsData.setOverallTotalPercent(String.format("%.2f", allTotalPercent));
			}

			if (ccSuccess + ccFailed < 1) {
				analyticsData.setCCSuccessRate("0.00");
			} else {
				double cCSuccessRate = (ccSuccess / (ccSuccess + ccFailed)) * 100;
				analyticsData.setCCSuccessRate(String.format("%.2f", cCSuccessRate));
			}

			if (dcSuccess + dcFailed < 1) {
				analyticsData.setDCSuccessRate("0.00");
			} else {
				double dCSuccessRate = (dcSuccess / (dcSuccess + dcFailed)) * 100;
				analyticsData.setDCSuccessRate(String.format("%.2f", dCSuccessRate));
			}

			if (upSuccess + upFailed < 1) {
				analyticsData.setUPSuccessRate("0.00");
			} else {
				double uPSuccessRate = (upSuccess / (upSuccess + upFailed)) * 100;
				analyticsData.setUPSuccessRate(String.format("%.2f", uPSuccessRate));
			}

			if (wlSuccess + wlFailed < 1) {
				analyticsData.setWLSuccessRate("0.00");
			} else {
				double wLSuccessRate = (wlSuccess / (wlSuccess + wlFailed)) * 100;
				analyticsData.setWLSuccessRate(String.format("%.2f", wLSuccessRate));
			}

			if (nbSuccess + nbFailed < 1) {
				analyticsData.setNBSuccessRate("0.00");
			} else {
				double nBSuccessRate = (nbSuccess / (nbSuccess + nbFailed)) * 100;
				analyticsData.setWLSuccessRate(String.format("%.2f", nBSuccessRate));
			}

			// for Payement Type Performance
			if (totalCCTxn < 1) {

				analyticsData.setTotalCCTxn("0");
				analyticsData.setTotalCCSuccessTxnPercent("0.00");
				analyticsData.setTotalCCFailedTxnPercent("0.00");
				analyticsData.setTotalCCCancelledTxnPercent("0.00");
				analyticsData.setTotalCCInvalidTxnPercent("0.00");

			} else {

				double totalCCSuccessTxnPercent = (ccSuccess / (totalCCTxn)) * 100;
				double totalCCFailedTxnPercent = (ccFailed / (totalCCTxn)) * 100;
				double totalCCCancelledTxnPercent = (ccCancelled / (totalCCTxn)) * 100;
				double totalCCInvalidTxnPercent = (ccInvalid / (totalCCTxn)) * 100;

				if (totalCCFailedTxnPercent + totalCCSuccessTxnPercent + totalCCCancelledTxnPercent
						+ totalCCInvalidTxnPercent < 100.0) {
					totalCCFailedTxnPercent = 100.0
							- (totalCCSuccessTxnPercent + totalCCCancelledTxnPercent + totalCCInvalidTxnPercent);
				}

				analyticsData.setTotalCCTxn(String.format("%.0f", totalCCTxn));
				analyticsData.setTotalCCSuccessTxnPercent(String.format("%.2f", totalCCSuccessTxnPercent));
				analyticsData.setTotalCCFailedTxnPercent(String.format("%.2f", totalCCFailedTxnPercent));
				analyticsData.setTotalCCCancelledTxnPercent(String.format("%.2f", totalCCCancelledTxnPercent));
				analyticsData.setTotalCCInvalidTxnPercent(String.format("%.2f", totalCCInvalidTxnPercent));

			}

			if (totalDCTxn < 1) {

				analyticsData.setTotalDCTxn("0");
				analyticsData.setTotalDCSuccessTxnPercent("0.00");
				analyticsData.setTotalDCFailedTxnPercent("0.00");
				analyticsData.setTotalDCCancelledTxnPercent("0.00");
				analyticsData.setTotalDCInvalidTxnPercent("0.00");

			} else {

				double totalDCSuccessTxnPercent = (dcSuccess / (totalDCTxn)) * 100;
				double totalDCFailedTxnPercent = (dcFailed / (totalDCTxn)) * 100;
				double totalDCCancelledTxnPercent = (dcCancelled / (totalDCTxn)) * 100;
				double totalDCInvalidTxnPercent = (dcInvalid / (totalDCTxn)) * 100;

				if (totalDCFailedTxnPercent + totalDCSuccessTxnPercent + totalDCCancelledTxnPercent
						+ totalDCInvalidTxnPercent < 100.0) {
					totalDCFailedTxnPercent = 100.0
							- (totalDCSuccessTxnPercent + totalDCCancelledTxnPercent + totalDCInvalidTxnPercent);
				}

				analyticsData.setTotalDCTxn(String.format("%.0f", totalDCTxn));
				analyticsData.setTotalDCSuccessTxnPercent(String.format("%.2f", totalDCSuccessTxnPercent));
				analyticsData.setTotalDCFailedTxnPercent(String.format("%.2f", totalDCFailedTxnPercent));
				analyticsData.setTotalDCCancelledTxnPercent(String.format("%.2f", totalDCCancelledTxnPercent));
				analyticsData.setTotalDCInvalidTxnPercent(String.format("%.2f", totalDCInvalidTxnPercent));

			}

			if (totalUPTxn < 1) {

				analyticsData.setTotalUPTxn("0");
				analyticsData.setTotalUPSuccessTxnPercent("0.00");
				analyticsData.setTotalUPFailedTxnPercent("0.00");
				analyticsData.setTotalUPCancelledTxnPercent("0.00");
				analyticsData.setTotalUPInvalidTxnPercent("0.00");
			} else {

				analyticsData.setTotalUPTxn(String.format("%.0f", totalUPTxn));

				double totalUPSuccessTxnPercent = (upSuccess / (totalUPTxn)) * 100;
				double totalUPFailedTxnPercent = (upFailed / (totalUPTxn)) * 100;
				double totalUPCancelledTxnPercent = (upCancelled / (totalUPTxn)) * 100;
				double totalUPInvalidTxnPercent = (upInvalid / (totalUPTxn)) * 100;

				if (totalUPFailedTxnPercent + totalUPSuccessTxnPercent + totalUPCancelledTxnPercent
						+ totalUPInvalidTxnPercent < 100.0) {
					totalUPFailedTxnPercent = 100.0
							- (totalUPSuccessTxnPercent + totalUPCancelledTxnPercent + totalUPInvalidTxnPercent);
				}

				analyticsData.setTotalUPSuccessTxnPercent(String.format("%.2f", totalUPSuccessTxnPercent));
				analyticsData.setTotalUPFailedTxnPercent(String.format("%.2f", totalUPFailedTxnPercent));
				analyticsData.setTotalUPCancelledTxnPercent(String.format("%.2f", totalUPCancelledTxnPercent));
				analyticsData.setTotalUPInvalidTxnPercent(String.format("%.2f", totalUPInvalidTxnPercent));

			}

			if (totalWLTxn < 1) {

				analyticsData.setTotalWLTxn("0");
				analyticsData.setTotalWLSuccessTxnPercent("0.00");
				analyticsData.setTotalWLFailedTxnPercent("0.00");
				analyticsData.setTotalWLCancelledTxnPercent("0.00");
				analyticsData.setTotalWLInvalidTxnPercent("0.00");
			} else {

				analyticsData.setTotalWLTxn(String.format("%.0f", totalWLTxn));

				double totalWLSuccessTxnPercent = (wlSuccess / (totalWLTxn)) * 100;
				double totalWLFailedTxnPercent = (wlFailed / (totalWLTxn)) * 100;
				double totalWLCancelledTxnPercent = (wlCancelled / (totalWLTxn)) * 100;
				double totalWLInvalidTxnPercent = (wlInvalid / (totalWLTxn)) * 100;

				if (totalWLFailedTxnPercent + totalWLSuccessTxnPercent + totalWLCancelledTxnPercent
						+ totalWLInvalidTxnPercent < 100.0) {
					totalWLFailedTxnPercent = 100.0
							- (totalWLSuccessTxnPercent + totalWLCancelledTxnPercent + totalWLInvalidTxnPercent);
				}

				analyticsData.setTotalWLSuccessTxnPercent(String.format("%.2f", totalWLSuccessTxnPercent));
				analyticsData.setTotalWLFailedTxnPercent(String.format("%.2f", totalWLFailedTxnPercent));
				analyticsData.setTotalWLCancelledTxnPercent(String.format("%.2f", totalWLCancelledTxnPercent));
				analyticsData.setTotalWLInvalidTxnPercent(String.format("%.2f", totalWLInvalidTxnPercent));

			}

			if (totalNBTxn < 1) {

				analyticsData.setTotalNBTxn("0");
				analyticsData.setTotalNBSuccessTxnPercent("0.00");
				analyticsData.setTotalNBFailedTxnPercent("0.00");
				analyticsData.setTotalNBCancelledTxnPercent("0.00");
				analyticsData.setTotalNBInvalidTxnPercent("0.00");
			} else {

				analyticsData.setTotalNBTxn(String.format("%.0f", totalNBTxn));

				double totalNBSuccessTxnPercent = (nbSuccess / (totalNBTxn)) * 100;
				double totalNBFailedTxnPercent = (nbFailed / (totalNBTxn)) * 100;
				double totalNBCancelledTxnPercent = (nbCancelled / (totalNBTxn)) * 100;
				double totalNBInvalidTxnPercent = (nbInvalid / (totalNBTxn)) * 100;

				if (totalNBFailedTxnPercent + totalNBSuccessTxnPercent + totalNBCancelledTxnPercent
						+ totalNBInvalidTxnPercent < 100.0) {
					totalNBFailedTxnPercent = 100.0
							- (totalNBSuccessTxnPercent + totalNBCancelledTxnPercent + totalNBInvalidTxnPercent);
				}

				analyticsData.setTotalNBSuccessTxnPercent(String.format("%.2f", totalNBSuccessTxnPercent));
				analyticsData.setTotalNBFailedTxnPercent(String.format("%.2f", totalNBFailedTxnPercent));
				analyticsData.setTotalNBCancelledTxnPercent(String.format("%.2f", totalNBCancelledTxnPercent));
				analyticsData.setTotalNBInvalidTxnPercent(String.format("%.2f", totalNBInvalidTxnPercent));

			} // end of Payment Type Perofrmance code.

			if (paymentType.equals(PaymentType.CREDIT_CARD.getCode())) {

				analyticsData.setCaptured(String.format("%.0f", ccSuccess));
				analyticsData.setFailed(String.format("%.0f", ccFailed));
				analyticsData.setCancelled(String.format("%.0f", ccCancelled));
				analyticsData.setInvalid(String.format("%.0f", ccInvalid));

				analyticsData.setCapturedPercent(String.format("%.2f", (ccSuccess / totalTxn) * 100));
				analyticsData.setFailedPercent(String.format("%.2f", (ccFailed / totalTxn) * 100));
				analyticsData.setCancelledPercent(String.format("%.2f", (ccCancelled / totalTxn) * 100));
				analyticsData.setInvalidPercent(String.format("%.2f", (ccInvalid / totalTxn) * 100));

			} else if (paymentType.equals(PaymentType.DEBIT_CARD.getCode())) {

				analyticsData.setCaptured(String.format("%.0f", dcSuccess));
				analyticsData.setFailed(String.format("%.0f", dcFailed));
				analyticsData.setCancelled(String.format("%.0f", dcCancelled));
				analyticsData.setInvalid(String.format("%.0f", dcInvalid));

				analyticsData.setCapturedPercent(String.format("%.2f", (dcSuccess / totalTxn) * 100));
				analyticsData.setFailedPercent(String.format("%.2f", (dcFailed / totalTxn) * 100));
				analyticsData.setCancelledPercent(String.format("%.2f", (dcCancelled / totalTxn) * 100));
				analyticsData.setInvalidPercent(String.format("%.2f", (dcInvalid / totalTxn) * 100));

			} else if (paymentType.equals(PaymentType.UPI.getCode())) {

				analyticsData.setCaptured(String.format("%.0f", upSuccess));
				analyticsData.setFailed(String.format("%.0f", upFailed));
				analyticsData.setCancelled(String.format("%.0f", upCancelled));
				analyticsData.setInvalid(String.format("%.0f", upInvalid));

				analyticsData.setCapturedPercent(String.format("%.2f", (upSuccess / totalTxn) * 100));
				analyticsData.setFailedPercent(String.format("%.2f", (upFailed / totalTxn) * 100));
				analyticsData.setCancelledPercent(String.format("%.2f", (upCancelled / totalTxn) * 100));
				analyticsData.setInvalidPercent(String.format("%.2f", (upInvalid / totalTxn) * 100));

			} else if (paymentType.equals(PaymentType.WALLET.getCode())) {

				analyticsData.setCaptured(String.format("%.0f", wlSuccess));
				analyticsData.setFailed(String.format("%.0f", wlFailed));
				analyticsData.setCancelled(String.format("%.0f", wlCancelled));
				analyticsData.setInvalid(String.format("%.0f", wlInvalid));

				analyticsData.setCapturedPercent(String.format("%.2f", (wlSuccess / totalTxn) * 100));
				analyticsData.setFailedPercent(String.format("%.2f", (wlFailed / totalTxn) * 100));
				analyticsData.setCancelledPercent(String.format("%.2f", (wlCancelled / totalTxn) * 100));
				analyticsData.setInvalidPercent(String.format("%.2f", (wlInvalid / totalTxn) * 100));

			} else if (paymentType.equals(PaymentType.NET_BANKING.getCode())) {

				analyticsData.setCaptured(String.format("%.0f", nbSuccess));
				analyticsData.setFailed(String.format("%.0f", nbFailed));
				analyticsData.setCancelled(String.format("%.0f", nbCancelled));
				analyticsData.setInvalid(String.format("%.0f", nbInvalid));

				analyticsData.setCapturedPercent(String.format("%.2f", (nbSuccess / totalTxn) * 100));
				analyticsData.setFailedPercent(String.format("%.2f", (nbFailed / totalTxn) * 100));
				analyticsData.setCancelledPercent(String.format("%.2f", (nbCancelled / totalTxn) * 100));
				analyticsData.setInvalidPercent(String.format("%.2f", (nbInvalid / totalTxn) * 100));

			} else {

				analyticsData.setCaptured("0");
				analyticsData.setFailed("0");
				analyticsData.setCancelled("0");
				analyticsData.setInvalid("0");

				analyticsData.setCapturedPercent("0.00");
				analyticsData.setFailedPercent("0.00");
				analyticsData.setCancelledPercent("0.00");
				analyticsData.setInvalidPercent("0.00");

			}

			// Total amounts
			analyticsData.setTotalCCTxnAmount(String.format("%.2f", totalCCTxnAmount));
			analyticsData.setTotalDCTxnAmount(String.format("%.2f", totalDCTxnAmount));
			analyticsData.setTotalUPTxnAmount(String.format("%.2f", totalUPTxnAmount));
			analyticsData.setTotalWLTxnAmount(String.format("%.2f", totalWLTxnAmount));
			analyticsData.setTotalNBTxnAmount(String.format("%.2f", totalNBTxnAmount));

			// Total Captured Counts
			analyticsData.setTotalCCCapturedCount(String.format("%.0f", ccSuccess));
			analyticsData.setTotalDCCapturedCount(String.format("%.0f", dcSuccess));
			analyticsData.setTotalUPCapturedCount(String.format("%.0f", upSuccess));
			analyticsData.setTotalWLCapturedCount(String.format("%.0f", wlSuccess));
			analyticsData.setTotalNBCapturedCount(String.format("%.0f", nbSuccess));

			// Total captured amount
			analyticsData.setCapturedTotalAmount(String.format("%.2f", capturedTotalAmount));
			analyticsData.setFailedTotalAmount(String.format("%.2f", failedTotalAmount));
			analyticsData.setCancelledTotalAmount(String.format("%.2f", cancelledTotalAmount));
			analyticsData.setInvalidTotalAmount(String.format("%.2f", invalidTotalAmount));

			analyticsData.setUnknownTxnCount(String.valueOf(unknownTransactions));
			analyticsData.setTotalCapturedTxnAmount(String.format("%.2f", totalTxnAmount));

			analyticsData.setMerchantPgRatio("0.00 %");
			analyticsData.setAcquirerPgRatio("0.00 %");
			// logger.info(" inside getTransactionCount for Preparing iPaycapturedData SMS
			// for final value to return ");

			return analyticsData;
		}

		catch (Exception e) {
			logger.error("Exception in transaction summary count service " + e);
		}
		return null;
	}
	// get ipayProfit

	public AnalyticsData getMerchantSMSData(String fromDate, String toDate, String payId, String paymentType,
			String acquirer, User user, String param) {
		logger.info(" inside getMerchantSMSData for Preparing iPaycapturedData SMS  ");
		AnalyticsData analyticsData = new AnalyticsData();

		try {

			/*
			 * fromDate = DateCreater.toDateTimeformatCreater(fromDate); toDate =
			 * DateCreater.formDateTimeformatCreater(toDate);
			 */

			BasicDBObject dateQuery = new BasicDBObject();
			List<BasicDBObject> paramConditionLst = new ArrayList<BasicDBObject>();
			BasicDBObject currencyQuery = new BasicDBObject();
			BasicDBObject acquirerQuery = new BasicDBObject();

			BasicDBObject allParamQuery = new BasicDBObject();
			List<BasicDBObject> acquirerConditionLst = new ArrayList<BasicDBObject>();
			List<BasicDBObject> saleCapturedList = new ArrayList<BasicDBObject>();
			List<BasicDBObject> refundCapturedList = new ArrayList<BasicDBObject>();

			if (!fromDate.isEmpty()) {

				String currentDate = null;
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

			if (!payId.equalsIgnoreCase("ALL")) {
				paramConditionLst.add(new BasicDBObject(FieldType.PAY_ID.getName(), payId));
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

			if (!paramConditionLst.isEmpty()) {
				allParamQuery = new BasicDBObject("$and", paramConditionLst);
			}

			// SALE Settled query
			List<BasicDBObject> saleCapturedConditionList = new ArrayList<BasicDBObject>();
			saleCapturedConditionList
					.add(new BasicDBObject(FieldType.TXNTYPE.getName(), TransactionType.SALE.getName()));
			saleCapturedConditionList.add(new BasicDBObject(FieldType.STATUS.getName(), StatusType.CAPTURED.getName()));

			BasicDBObject saleConditionQuery = new BasicDBObject("$and", saleCapturedConditionList);
			saleCapturedList.add(saleConditionQuery);

			// REFUND Settled query
			List<BasicDBObject> refundCapturedConditionList = new ArrayList<BasicDBObject>();
			refundCapturedConditionList
					.add(new BasicDBObject(FieldType.TXNTYPE.getName(), TransactionType.REFUND.getName()));
			refundCapturedConditionList
					.add(new BasicDBObject(FieldType.STATUS.getName(), StatusType.CAPTURED.getName()));

			BasicDBObject refundConditionQuery = new BasicDBObject("$and", refundCapturedConditionList);
			saleCapturedList.add(refundConditionQuery);

			BasicDBObject saleCapturedConditionQuery = new BasicDBObject("$or", saleCapturedList);
			BasicDBObject refundCapturedConditionQuery = new BasicDBObject("$or", refundCapturedList);

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
			allConditionQueryList.add(saleCapturedConditionQuery);
			// allConditionQueryList.add(refundSettledConditionQuery);

			BasicDBObject allConditionQueryObj = new BasicDBObject("$and", allConditionQueryList);

			List<BasicDBObject> fianlList = new ArrayList<BasicDBObject>();

			if (!allParamQuery.isEmpty()) {
				fianlList.add(allParamQuery);
			}
			if (!allConditionQueryObj.isEmpty()) {
				fianlList.add(allConditionQueryObj);
			}

			BasicDBObject finalQuery = new BasicDBObject("$and", fianlList);
			MongoDatabase dbIns = mongoInstance.getDB();
			MongoCollection<Document> coll = dbIns
					.getCollection(PropertiesManager.propertiesMap.get(prefix + Constants.COLLECTION_NAME.getValue()));

			BigDecimal saleCapturedAmount = BigDecimal.ZERO;
			BigDecimal refundCapturedAmount = BigDecimal.ZERO;
			BigDecimal iPayProfitAmount = BigDecimal.ZERO;

			int ccCapturedCount = 0;
			int dcCapturedCount = 0;
			int upCapturedCount = 0;

			Double ccTxnPer = 0.00;
			Double ccAmtPer = 0.00;
			BigDecimal ccTotalAmt = BigDecimal.ZERO;
			Double dcTxnPer = 0.00;
			Double dcAmtPer = 0.00;
			BigDecimal dcTotalAmt = BigDecimal.ZERO;
			Double upTxnPer = 0.00;
			Double upAmtPer = 0.00;
			BigDecimal upTotalAmt = BigDecimal.ZERO;

			List<String> captureDateArray = new ArrayList<String>();

			MongoCursor<Document> cursor = coll.find(finalQuery).iterator();

			while (cursor.hasNext()) {

				// logger.info(" inside getMerchantSMSData for Preparing iPaycapturedData SMS in
				// side while loop ");
				Document dbobj = cursor.next();

				if (dbobj.getString(FieldType.TXNTYPE.getName()).equalsIgnoreCase(TransactionType.SALE.getName())) {
					saleCapturedAmount = saleCapturedAmount
							.add(new BigDecimal(dbobj.getString(FieldType.AMOUNT.getName())));
					// logger.info(" inside getMerchantSMSData for Preparing iPaycapturedData SMS in
					// side while loop saleCapturedAmount " +saleCapturedAmount);
					if (dbobj.getString(FieldType.PAYMENT_TYPE.getName())
							.equalsIgnoreCase(PaymentType.CREDIT_CARD.getCode())) {
						ccCapturedCount++;
						ccTotalAmt = ccTotalAmt.add(new BigDecimal(dbobj.getString(FieldType.AMOUNT.getName())));
						// logger.info(" inside getMerchantSMSData for Preparing iPaycapturedData SMS in
						// side while loop ccTotalAmt " +ccTotalAmt);
					} else if (dbobj.getString(FieldType.PAYMENT_TYPE.getName())
							.equalsIgnoreCase(PaymentType.DEBIT_CARD.getCode())) {
						dcCapturedCount++;
						dcTotalAmt = dcTotalAmt.add(new BigDecimal(dbobj.getString(FieldType.AMOUNT.getName())));
						// logger.info(" inside getMerchantSMSData for Preparing iPaycapturedData SMS in
						// side while loop dcTotalAmt " +dcTotalAmt);
					} else if (dbobj.getString(FieldType.PAYMENT_TYPE.getName())
							.equalsIgnoreCase(PaymentType.UPI.getCode())) {
						upCapturedCount++;
						upTotalAmt = upTotalAmt.add(new BigDecimal(dbobj.getString(FieldType.AMOUNT.getName())));
						// logger.info(" inside getMerchantSMSData for Preparing iPaycapturedData SMS in
						// side while loop upTotalAmt " +upTotalAmt);
					}

					captureDateArray.add(dbobj.getString(FieldType.CREATE_DATE.getName()));

				} else {
					refundCapturedAmount = refundCapturedAmount
							.add(new BigDecimal(dbobj.getString(FieldType.AMOUNT.getName())));
					// logger.info(" inside getMerchantSMSData for Preparing iPaycapturedData SMS in
					// side while loop refundCapturedAmount " +refundCapturedAmount);
				}

			}

			cursor.close();
			Collections.sort(captureDateArray);

			if ((ccCapturedCount + dcCapturedCount + upCapturedCount) > 0) {

				ccTxnPer = (Double.valueOf(ccCapturedCount)
						/ Double.valueOf(ccCapturedCount + dcCapturedCount + upCapturedCount)) * 100;
				dcTxnPer = (Double.valueOf(dcCapturedCount)
						/ Double.valueOf(ccCapturedCount + dcCapturedCount + upCapturedCount)) * 100;
				upTxnPer = (Double.valueOf(upCapturedCount)
						/ Double.valueOf(ccCapturedCount + dcCapturedCount + upCapturedCount)) * 100;

				ccAmtPer = (ccTotalAmt.divide(saleCapturedAmount, 2, RoundingMode.HALF_DOWN))
						.multiply(new BigDecimal("100")).setScale(2, RoundingMode.HALF_DOWN).doubleValue();
				dcAmtPer = (dcTotalAmt.divide(saleCapturedAmount, 2, RoundingMode.HALF_DOWN))
						.multiply(new BigDecimal("100")).setScale(2, RoundingMode.HALF_DOWN).doubleValue();
				upAmtPer = (upTotalAmt.divide(saleCapturedAmount, 2, RoundingMode.HALF_DOWN))
						.multiply(new BigDecimal("100")).setScale(2, RoundingMode.HALF_DOWN).doubleValue();
				iPayProfitAmount = saleCapturedAmount.subtract(refundCapturedAmount);
				// logger.info(" inside getMerchantSMSData for Preparing iPaycapturedData SMS in
				// side while loop iPayProfitAmount " +iPayProfitAmount);

			}

			SimpleDateFormat outFormat = new SimpleDateFormat("dd-MMM-yyyy");

			Date dateCapFrom = new SimpleDateFormat("yyyy-mm-dd hh:mm:ss", Locale.ENGLISH)
					.parse(captureDateArray.get(0));
			Date dateCapTo = new SimpleDateFormat("yyyy-mm-dd hh:mm:ss", Locale.ENGLISH)
					.parse(captureDateArray.get(captureDateArray.size() - 1));

			String dateCapFromString = outFormat.format(dateCapFrom);
			String dateCapToString = outFormat.format(dateCapTo);

			if (captureDateArray.size() > 0) {

				if (dateCapFromString.equalsIgnoreCase(dateCapToString)) {

					analyticsData.setDateCaptured(dateCapFromString);
				} else {
					analyticsData.setDateCaptured(dateCapFromString + " to " + dateCapToString);
				}

			} else {
				analyticsData.setDateCaptured("No Data Found");
			}

			Date dateSettleFrom = new SimpleDateFormat("yyyy-mm-dd hh:mm:ss", Locale.ENGLISH).parse(fromDate);
			Date dateSettleTo = new SimpleDateFormat("yyyy-mm-dd hh:mm:ss", Locale.ENGLISH).parse(toDate);

			String dateSettleFromString = outFormat.format(dateSettleFrom);
			String dateSettleToString = outFormat.format(dateSettleTo);

			if (dateSettleFromString.equalsIgnoreCase(dateSettleToString)) {
				analyticsData.setDateSettled(dateSettleFromString);
			} else {
				analyticsData.setDateSettled(dateSettleFromString + " to " + dateSettleToString);
			}

			analyticsData.setIpayProfitAmount(format(String.format("%.0f", iPayProfitAmount)) + ".00");
			// logger.info(" inside getMerchantSMSData for Preparing iPaycapturedData SMS in
			// side while loop analyticsData final value "
			// +analyticsData.getIpayProfitAmount());

			return analyticsData;
		}

		catch (Exception e) {

			logger.error("Exception in AnalyticsData service " + e);
			analyticsData.setDateCaptured("Invalid Date Range");
			analyticsData.setDateSettled("Invalid Date Range");
			analyticsData.setIpayProfitAmount("0.00");

			return analyticsData;
		}

	}

	public String format(String amount) {
		StringBuilder stringBuilder = new StringBuilder();
		char amountArray[] = amount.toCharArray();
		int a = 0, b = 0;
		for (int i = amountArray.length - 1; i >= 0; i--) {
			if (a < 3) {
				stringBuilder.append(amountArray[i]);
				a++;
			} else if (b < 2) {
				if (b == 0) {
					stringBuilder.append(",");
					stringBuilder.append(amountArray[i]);
					b++;
				} else {
					stringBuilder.append(amountArray[i]);
					b = 0;
				}
			}
		}
		return stringBuilder.reverse().toString();
	}
	// total Amount inc/exc GST

	public AnalyticsData getTransactionTotalProfitCount(String fromDate, String toDate, String payId,
			String paymentType, String acquirer, User user, String param) {
		logger.info(" inside getTransactionTotalProfitCount for Preparing iPaycapturedData SMS  ");
		List<AnalyticsData> analyticsDataSearchList = new ArrayList<AnalyticsData>();
		List<String> txnTypeList = new ArrayList<String>();
		txnTypeList.add(TxnType.SALE.getName());
		txnTypeList.add(TxnType.REFUND.getName());

		try {

			surchargeList.clear();
			SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.ENGLISH);
			Date date1 = format.parse(fromDate);
			Date date2 = format.parse(toDate);

			surchargeList = surchargeDao.findAllSurchargeByDate(date1, date2);
			serviceTaxMap.clear();
			postSettledTransactionCount = 0;
			BasicDBObject dateQuery = new BasicDBObject();
			List<BasicDBObject> paramConditionLst = new ArrayList<BasicDBObject>();
			BasicDBObject currencyQuery = new BasicDBObject();
			BasicDBObject acquirerQuery = new BasicDBObject();

			BasicDBObject allParamQuery = new BasicDBObject();
			List<BasicDBObject> acquirerConditionLst = new ArrayList<BasicDBObject>();
			List<BasicDBObject> saleCapturedList = new ArrayList<BasicDBObject>();
			List<BasicDBObject> refundCapturedList = new ArrayList<BasicDBObject>();
			String statusList = "Captured";
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
			if (StringUtils.isNotBlank(PropertiesManager.propertiesMap.get(Constants.USE_DATE_INDEX.getValue()))
					&& PropertiesManager.propertiesMap.get(Constants.USE_DATE_INDEX.getValue()).equalsIgnoreCase("Y")) {
				dateIndexConditionQuery.append("$or", dateIndexConditionList);
			}

			if (!payId.equalsIgnoreCase("ALL")) {
				paramConditionLst.add(new BasicDBObject(FieldType.PAY_ID.getName(), payId));
			}

			if (user.getUserType().equals(UserType.RESELLER)) {
				paramConditionLst.add(new BasicDBObject(FieldType.RESELLER_ID.getName(), user.getResellerId()));
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

			if (!paramConditionLst.isEmpty()) {
				allParamQuery = new BasicDBObject("$and", paramConditionLst);
			}

			// SALE Captured query
			List<BasicDBObject> saleCapturedConditionList = new ArrayList<BasicDBObject>();
			saleCapturedConditionList
					.add(new BasicDBObject(FieldType.TXNTYPE.getName(), TransactionType.SALE.getName()));
			saleCapturedConditionList.add(new BasicDBObject(FieldType.STATUS.getName(), StatusType.CAPTURED.getName()));
			saleCapturedConditionList
					.add(new BasicDBObject(FieldType.RESPONSE_CODE.getName(), ErrorType.SUCCESS.getCode()));

			BasicDBObject saleConditionQuery = new BasicDBObject("$and", saleCapturedConditionList);
			saleCapturedList.add(saleConditionQuery);

			// REFUND Captured query
			List<BasicDBObject> refundCapturedConditionList = new ArrayList<BasicDBObject>();
			refundCapturedConditionList
					.add(new BasicDBObject(FieldType.TXNTYPE.getName(), TransactionType.REFUND.getName()));
			refundCapturedConditionList
					.add(new BasicDBObject(FieldType.STATUS.getName(), StatusType.CAPTURED.getName()));
			refundCapturedConditionList
					.add(new BasicDBObject(FieldType.RESPONSE_CODE.getName(), ErrorType.SUCCESS.getCode()));

			BasicDBObject refundConditionQuery = new BasicDBObject("$and", refundCapturedConditionList);
			refundCapturedList.add(refundConditionQuery);

			BasicDBObject saleSettledConditionQuery = new BasicDBObject("$or", saleCapturedList);
			BasicDBObject refundSettledConditionQuery = new BasicDBObject("$or", refundCapturedList);

			AnalyticsData analyticsData = new AnalyticsData();
			analyticsData.setAcquirer(acquirer);

			if (!paymentType.equalsIgnoreCase("ALL")) {
				analyticsData.setPaymentMethod(PaymentType.getpaymentName(paymentType));
			} else {
				analyticsData.setPaymentMethod(paymentType);
			}

			for (String txnType : txnTypeList) {

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
				if (!dateQuery.isEmpty()) {
					allConditionQueryList.add(dateQuery);
				}

				if (!dateIndexConditionQuery.isEmpty()) {
					allConditionQueryList.add(dateIndexConditionQuery);
				}
				if (txnType.equalsIgnoreCase(TransactionType.SALE.getName())) {
					if (!saleSettledConditionQuery.isEmpty()) {
						allConditionQueryList.add(saleSettledConditionQuery);
					}
				} else {
					if (!refundSettledConditionQuery.isEmpty()) {
						allConditionQueryList.add(refundSettledConditionQuery);
					}
				}

				BasicDBObject allConditionQueryObj = new BasicDBObject("$and", allConditionQueryList);

				List<BasicDBObject> fianlList = new ArrayList<BasicDBObject>();

				if (!allParamQuery.isEmpty()) {
					fianlList.add(allParamQuery);
				}
				if (!allConditionQueryObj.isEmpty()) {
					fianlList.add(allConditionQueryObj);
				}

				BasicDBObject finalQuery = new BasicDBObject("$and", fianlList);
				MongoDatabase dbIns = mongoInstance.getDB();
				MongoCollection<Document> coll = dbIns.getCollection(
						PropertiesManager.propertiesMap.get(prefix + Constants.COLLECTION_NAME.getValue()));

				double ccSuccess = 0;

				double dcSuccess = 0;

				double upSuccess = 0;
				double wlSuccess = 0;
				double prepaidSuccess = 0;
				double nbSuccess = 0;

				double totalTxn = 0;
				double totalCCTxn = 0;
				double totalDCTxn = 0;
				double totalUPTxn = 0;
				double totalWLTxn = 0;
				double totalPPTxn = 0;
				double totalNBTxn = 0;

				int unknownTransactions = 0;

				BigDecimal totalTxnAmount = BigDecimal.ZERO.setScale(2, RoundingMode.HALF_DOWN);

				BigDecimal totalCCTxnAmount = BigDecimal.ZERO.setScale(2, RoundingMode.HALF_DOWN);
				BigDecimal totalDCTxnAmount = BigDecimal.ZERO.setScale(2, RoundingMode.HALF_DOWN);
				BigDecimal totalUPTxnAmount = BigDecimal.ZERO.setScale(2, RoundingMode.HALF_DOWN);
				BigDecimal totalWLTxnAmount = BigDecimal.ZERO.setScale(2, RoundingMode.HALF_DOWN);
				BigDecimal totalNBTxnAmount = BigDecimal.ZERO.setScale(2, RoundingMode.HALF_DOWN);
				MongoCursor<Document> cursor = coll.find(finalQuery).iterator();

				// Remove all data from an earlier map
				while (cursor.hasNext()) {

					Document dbobj = cursor.next();
					// logger.info("Inside , whileLoop =>, "
					// +dbobj.getString(FieldType.PG_REF_NUM.toString()));

					if (statusList.contains(dbobj.getString(FieldType.STATUS.toString()))) {
						totalTxn++;
					}

					if (StringUtils.isBlank(dbobj.getString(FieldType.PAYMENT_TYPE.toString()))
							&& statusList.contains(dbobj.getString(FieldType.STATUS.toString()))) {
						unknownTransactions++;
						continue;
					}

					if (StringUtils.isBlank(dbobj.getString(FieldType.PAYMENT_TYPE.toString()))
							&& !statusList.contains(dbobj.getString(FieldType.STATUS.toString()))) {
						continue;
					}

					switch (PaymentType.getInstanceUsingCode(dbobj.getString(FieldType.PAYMENT_TYPE.toString()))) {

					case DEBIT_CARD:

						totalDCTxn++;

						if (dbobj.getString(FieldType.TXNTYPE.toString()).equals(SALE)) {

							if (!StringUtils.isBlank(dbobj.getString(FieldType.TOTAL_AMOUNT.toString()))) {
								BigDecimal txnAmount = new BigDecimal(
										dbobj.getString(FieldType.TOTAL_AMOUNT.toString()))
										.setScale(2, RoundingMode.HALF_DOWN);
								totalTxnAmount = totalTxnAmount.add(txnAmount).setScale(2, RoundingMode.HALF_DOWN);
								totalDCTxnAmount = totalDCTxnAmount.add(txnAmount).setScale(2, RoundingMode.HALF_DOWN);

							}

							dcSuccess++;
						}

						break;

					case CREDIT_CARD:

						totalCCTxn++;
						if (dbobj.getString(FieldType.TXNTYPE.toString()).equals(SALE)) {

							if (!StringUtils.isBlank(dbobj.getString(FieldType.TOTAL_AMOUNT.toString()))) {
								BigDecimal txnAmount = new BigDecimal(
										dbobj.getString(FieldType.TOTAL_AMOUNT.toString()))
										.setScale(2, RoundingMode.HALF_DOWN);
								;
								totalTxnAmount = totalTxnAmount.add(txnAmount).setScale(2, RoundingMode.HALF_DOWN);
								totalCCTxnAmount = totalCCTxnAmount.add(txnAmount).setScale(2, RoundingMode.HALF_DOWN);
							}

							ccSuccess++;

						}

						break;

					case UPI:

						totalUPTxn++;
						if (dbobj.getString(FieldType.TXNTYPE.toString()).equals(SALE)) {

							if (!StringUtils.isBlank(dbobj.getString(FieldType.TOTAL_AMOUNT.toString()))) {
								BigDecimal txnAmount = new BigDecimal(
										dbobj.getString(FieldType.TOTAL_AMOUNT.toString()))
										.setScale(2, RoundingMode.HALF_DOWN);
								totalTxnAmount = totalTxnAmount.add(txnAmount).setScale(2, RoundingMode.HALF_DOWN);
								totalUPTxnAmount = totalUPTxnAmount.add(txnAmount).setScale(2, RoundingMode.HALF_DOWN);

							}

							upSuccess++;
						}

						break;
					case WALLET:

						totalWLTxn++;
						if (dbobj.getString(FieldType.TXNTYPE.toString()).equals(SALE)) {

							if (!StringUtils.isBlank(dbobj.getString(FieldType.TOTAL_AMOUNT.toString()))) {
								BigDecimal txnAmount = new BigDecimal(
										dbobj.getString(FieldType.TOTAL_AMOUNT.toString()))
										.setScale(2, RoundingMode.HALF_DOWN);
								totalTxnAmount = totalTxnAmount.add(txnAmount).setScale(2, RoundingMode.HALF_DOWN);
								totalWLTxnAmount = totalWLTxnAmount.add(txnAmount).setScale(2, RoundingMode.HALF_DOWN);
								// logger.info("Inside , WALLET , => " + totalTxnAmount);
							}

							wlSuccess++;
						}

						break;

					case PREPAID_CARD:

						totalPPTxn++;
						if (dbobj.getString(FieldType.TXNTYPE.toString()).equals(SALE)) {

							if (!StringUtils.isBlank(dbobj.getString(FieldType.TOTAL_AMOUNT.toString()))) {
								BigDecimal txnAmount = new BigDecimal(
										dbobj.getString(FieldType.TOTAL_AMOUNT.toString()))
										.setScale(2, RoundingMode.HALF_DOWN);
								totalTxnAmount = totalTxnAmount.add(txnAmount).setScale(2, RoundingMode.HALF_DOWN);
								totalUPTxnAmount = totalUPTxnAmount.add(txnAmount).setScale(2, RoundingMode.HALF_DOWN);

							}

							prepaidSuccess++;
						}

						break;

					case NET_BANKING:

						totalNBTxn++;
						if (dbobj.getString(FieldType.TXNTYPE.toString()).equals(SALE)) {

							if (!StringUtils.isBlank(dbobj.getString(FieldType.TOTAL_AMOUNT.toString()))) {
								BigDecimal txnAmount = new BigDecimal(
										dbobj.getString(FieldType.TOTAL_AMOUNT.toString()))
										.setScale(2, RoundingMode.HALF_DOWN);
								totalTxnAmount = totalTxnAmount.add(txnAmount).setScale(2, RoundingMode.HALF_DOWN);
								totalNBTxnAmount = totalNBTxnAmount.add(txnAmount).setScale(2, RoundingMode.HALF_DOWN);

							}

							nbSuccess++;
						}

						break;

					default:
						break;

					}

					AnalyticsData analyticsDataObj = new AnalyticsData();
					analyticsDataObj = findDetails(dbobj);
					analyticsDataObj.setSaleCapturedCount(analyticsData.getSaleCapturedCount());
					analyticsDataObj.setRefundCapturedCount(analyticsData.getRefundCapturedCount());
					analyticsData.setGst(analyticsDataObj.getGst());
					analyticsDataSearchList.add(analyticsDataObj);

				}

				cursor.close();
				if (txnType.equals(SALE)) {
					double totalTxnSuccess = ccSuccess + dcSuccess + upSuccess + wlSuccess + nbSuccess;
					analyticsData.setSuccessTxnCount(String.format("%.0f", totalTxnSuccess));
					analyticsData.setTotalCapturedTxnAmount(String.format("%.2f", totalTxnAmount));

					if (totalTxnSuccess < 1) {
						analyticsData.setCCTxnPercent("0.00");
						analyticsData.setDCTxnPercent("0.00");
						analyticsData.setUPTxnPercent("0.00");
						analyticsData.setWLTxnPercent("0.00");
						analyticsData.setNBTxnPercent("0.00");
					}

					else {

						double cCTxnPercent = (ccSuccess / totalTxnSuccess) * 100;
						double dCTxnPercent = (dcSuccess / totalTxnSuccess) * 100;
						double upTxnPercent = (upSuccess / totalTxnSuccess) * 100;
						double wlTxnPercent = (wlSuccess / totalTxnSuccess) * 100;
						double nbTxnPercent = (nbSuccess / totalTxnSuccess) * 100;

						analyticsData.setCCTxnPercent(String.format("%.2f", cCTxnPercent));
						analyticsData.setDCTxnPercent(String.format("%.2f", dCTxnPercent));
						analyticsData.setUPTxnPercent(String.format("%.2f", upTxnPercent));
						analyticsData.setWLTxnPercent(String.format("%.2f", wlTxnPercent));
						analyticsData.setNBTxnPercent(String.format("%.2f", nbTxnPercent));
					}
				}

			}

			BigDecimal saleCapturedAmount = BigDecimal.ZERO;
			BigDecimal pgSaleSurcharge = BigDecimal.ZERO;
			BigDecimal acquirerSaleSurcharge = BigDecimal.ZERO;
			BigDecimal pgSaleGst = BigDecimal.ZERO;
			BigDecimal acquirerSaleGst = BigDecimal.ZERO;

			BigDecimal refundCapturedAmount = BigDecimal.ZERO;
			BigDecimal pgRefundSurcharge = BigDecimal.ZERO;
			BigDecimal acquirerRefundSurcharge = BigDecimal.ZERO;
			BigDecimal pgRefundGst = BigDecimal.ZERO;
			BigDecimal acquirerRefundGst = BigDecimal.ZERO;

			BigDecimal totalMerchantAmount = BigDecimal.ZERO;
			BigDecimal merchantSaleCapturedAmount = BigDecimal.ZERO;
			BigDecimal merchantRefundCapturedAmount = BigDecimal.ZERO;

			BigDecimal totalCapturedAmountActual = BigDecimal.ZERO;
			BigDecimal totalSettledAmountDelta = BigDecimal.ZERO;

			for (AnalyticsData analyticsDataSearchObj : analyticsDataSearchList) {

				if (analyticsDataSearchObj.getTxnType().equalsIgnoreCase(TransactionType.SALE.getName())) {

					BigDecimal saleCapturedAmountObj = new BigDecimal(analyticsDataSearchObj.getSaleCapturedAmount());
					BigDecimal pgSaleSurchargeObj = new BigDecimal(analyticsDataSearchObj.getPgSaleSurcharge());
					BigDecimal acquirerSaleSurchargeObj = new BigDecimal(
							analyticsDataSearchObj.getAcquirerSaleSurcharge());
					BigDecimal pgSaleGstObj = new BigDecimal(analyticsDataSearchObj.getPgSaleGst());
					BigDecimal acquirerSaleGstObj = new BigDecimal(analyticsDataSearchObj.getAcquirerSaleGst());

					saleCapturedAmount = saleCapturedAmount.add(saleCapturedAmountObj).setScale(2,
							RoundingMode.HALF_DOWN);
					pgSaleSurcharge = pgSaleSurcharge.add(pgSaleSurchargeObj).setScale(2, RoundingMode.HALF_DOWN);
					acquirerSaleSurcharge = acquirerSaleSurcharge.add(acquirerSaleSurchargeObj).setScale(2,
							RoundingMode.HALF_DOWN);
					pgSaleGst = pgSaleGst.add(pgSaleGstObj).setScale(2, RoundingMode.HALF_DOWN);
					acquirerSaleGst = acquirerSaleGst.add(acquirerSaleGstObj).setScale(2, RoundingMode.HALF_DOWN);

				} else {

					BigDecimal refundCapturedAmountObj = new BigDecimal(
							analyticsDataSearchObj.getRefundCapturedAmount());
					BigDecimal pgRefundSurchargeObj = new BigDecimal(analyticsDataSearchObj.getPgRefundSurcharge());
					BigDecimal acquirerRefundSurchargeObj = new BigDecimal(
							analyticsDataSearchObj.getAcquirerRefundSurcharge());
					BigDecimal pgRefundGstObj = new BigDecimal(analyticsDataSearchObj.getPgRefundGst());
					BigDecimal acquirerRefundGstObj = new BigDecimal(analyticsDataSearchObj.getAcquirerRefundGst());

					refundCapturedAmount = refundCapturedAmount.add(refundCapturedAmountObj).setScale(2,
							RoundingMode.HALF_DOWN);
					pgRefundSurcharge = pgRefundSurcharge.add(pgRefundSurchargeObj).setScale(2, RoundingMode.HALF_DOWN);
					acquirerRefundSurcharge = acquirerRefundSurcharge.add(acquirerRefundSurchargeObj).setScale(2,
							RoundingMode.HALF_DOWN);
					pgRefundGst = pgRefundGst.add(pgRefundGstObj).setScale(2, RoundingMode.HALF_DOWN);
					acquirerRefundGst = acquirerRefundGst.add(acquirerRefundGstObj).setScale(2, RoundingMode.HALF_DOWN);

					// totalMerchantAmount = totalMerchantAmount.add(refundSettledAmountObj);

				}

			}

			if (payId.equalsIgnoreCase("ALL")) {
				analyticsData.setMerchantName(payId);
			} else {
				analyticsData.setMerchantName(userDao.findPayId(payId).getBusinessName());
			}

			merchantSaleCapturedAmount = saleCapturedAmount
					.subtract(pgSaleSurcharge.add(acquirerSaleSurcharge).add(pgSaleGst).add(acquirerSaleGst))
					.setScale(2, RoundingMode.HALF_DOWN);
			merchantRefundCapturedAmount = refundCapturedAmount
					.subtract(pgRefundSurcharge.add(acquirerRefundSurcharge).add(pgRefundGst).add(acquirerRefundGst))
					.setScale(2, RoundingMode.HALF_DOWN);

			analyticsData.setSaleCapturedAmount(String.valueOf(saleCapturedAmount));
			analyticsData.setPgSaleSurcharge(String.valueOf(pgSaleSurcharge.setScale(2, RoundingMode.HALF_DOWN)));
			analyticsData.setAcquirerSaleSurcharge(
					String.valueOf(acquirerSaleSurcharge.setScale(2, RoundingMode.HALF_DOWN)));
			analyticsData.setPgSaleGst(String.valueOf(pgSaleGst.setScale(2, RoundingMode.HALF_DOWN)));
			analyticsData.setAcquirerSaleGst(String.valueOf(acquirerSaleGst.setScale(2, RoundingMode.HALF_DOWN)));
			// analyticsData.setGst(String.valueOf(saleGst.setScale(2,
			// RoundingMode.HALF_DOWN)));

			analyticsData
					.setRefundCapturedAmount(String.valueOf(refundCapturedAmount.setScale(2, RoundingMode.HALF_DOWN)));
			analyticsData.setPgRefundSurcharge(String.valueOf(pgRefundSurcharge.setScale(2, RoundingMode.HALF_DOWN)));
			analyticsData.setAcquirerRefundSurcharge(
					String.valueOf(acquirerRefundSurcharge.setScale(2, RoundingMode.HALF_DOWN)));
			analyticsData.setPgRefundGst(String.valueOf(pgRefundGst.setScale(2, RoundingMode.HALF_DOWN)));
			analyticsData.setAcquirerRefundGst(String.valueOf(acquirerRefundGst.setScale(2, RoundingMode.HALF_DOWN)));
			// analyticsData.setGst(String.valueOf(refundGst.setScale(2,
			// RoundingMode.HALF_DOWN)));

			totalMerchantAmount = merchantSaleCapturedAmount
					.subtract(merchantRefundCapturedAmount.setScale(2, RoundingMode.HALF_DOWN));

			// total amnt including gst
			analyticsData.setIpayProfitInclGstCumm(String.valueOf(pgSaleSurcharge.add(pgSaleGst)
					.subtract(pgRefundSurcharge).subtract(pgRefundGst).setScale(2, RoundingMode.HALF_DOWN)));

			return analyticsData;
		}

		catch (Exception e) {
			logger.error("Exception in getTransactionTotalProfitCount service " + e);
		}
		return null;
	}

	public AnalyticsData findDetails(Document dbobj) {

		// logger.info("Inside , findDetails , ");

		List<ServiceTax> serviceTaxList = new ArrayList<ServiceTax>();
		BigDecimal merchantGstAmount = BigDecimal.ZERO.setScale(2, RoundingMode.HALF_DOWN);
		BigDecimal acquirerGstAmount = BigDecimal.ZERO.setScale(2, RoundingMode.HALF_DOWN);
		TdrPojo tdrPojo = new TdrPojo();
		BigDecimal st = null;
		String bussinessType = null;
		String bussinessName = "";
		String payId = (dbobj.getString(FieldType.PAY_ID.toString()));
		if (!StringUtils.isBlank(payId)) {

			User user = new User();

			if (userMap.get(payId) != null) {
				user = userMap.get(payId);
			} else {
				user = userDao.findPayId(payId);
				userMap.put(payId, user);
			}

			if (StringUtils.isNotBlank(dbobj.getString(FieldType.POST_SETTLED_FLAG.toString()))) {
				if (dbobj.getString(FieldType.POST_SETTLED_FLAG.toString()).equalsIgnoreCase("Y")) {
					postSettledTransactionCount++;
				}
			}

			String amount = (dbobj.getString(FieldType.AMOUNT.toString()));
			String totalAmount = (dbobj.getString(FieldType.TOTAL_AMOUNT.toString()));
			bussinessType = user.getIndustryCategory();
			bussinessName = user.getBusinessName();

			if (serviceTaxMap.get(bussinessType) != null) {
				serviceTaxList = serviceTaxMap.get(bussinessType);
			} else {
				serviceTaxList = serviceTaxDao.findServiceTaxForReportWithoutDate(user.getIndustryCategory(),
						dbobj.getString(FieldType.CREATE_DATE.toString()));
				serviceTaxMap.put(bussinessType, serviceTaxList);
			}

			for (ServiceTax serviceTax : serviceTaxList) {
				st = serviceTax.getServiceTax();
				st = st.setScale(2, RoundingMode.HALF_DOWN);

			}

			if (!StringUtils.isBlank(dbobj.getString(FieldType.SURCHARGE_FLAG.toString()))) {

				if (dbobj.getString(FieldType.SURCHARGE_FLAG.toString()).equals("Y")) {

					String txnAmount = amount;
					String surchargeAmount = totalAmount;
					BigDecimal nettxnAmount = new BigDecimal(txnAmount);

					PaymentType paymentType = PaymentType
							.getInstanceUsingCode(dbobj.getString(FieldType.PAYMENT_TYPE.toString()));

					if (paymentType == null) {
						return null;
					}

					AcquirerType acquirerType = AcquirerType
							.getInstancefromCode(dbobj.getString(FieldType.ACQUIRER_TYPE.toString()));

					if (acquirerType == null) {
						return null;
					}

					MopType mopType = MopType.getmop(dbobj.getString(FieldType.MOP_TYPE.toString()));

					if (mopType == null) {
						return null;
					}

					String paymentsRegion = (dbobj.getString(FieldType.PAYMENTS_REGION.toString()));

					if (paymentsRegion == null) {
						paymentsRegion = AccountCurrencyRegion.DOMESTIC.toString();
					}

					StringBuilder surchargeIdentifier = new StringBuilder();
					surchargeIdentifier.append(payId);
					surchargeIdentifier.append(paymentType.getName());
					surchargeIdentifier.append(acquirerType.getName());
					surchargeIdentifier.append(mopType.getName());
					surchargeIdentifier.append(paymentsRegion);

					Date surchargeStartDate = null;
					Date surchargeEndDate = null;
					Date settlementDate = null;
					Surcharge surcharge = new Surcharge();
					String transactionRegion = null;
					if (StringUtils.isNotBlank(dbobj.getString(FieldType.PAYMENTS_REGION.toString()))) {
						transactionRegion = dbobj.getString(FieldType.PAYMENTS_REGION.toString());
					} else {
						transactionRegion = AccountCurrencyRegion.DOMESTIC.name();
					}
					SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.ENGLISH);
					try {
						for (Surcharge surchargeData : surchargeList) {

							if (AcquirerType.getInstancefromName(surchargeData.getAcquirerName()).toString()
									.equalsIgnoreCase(dbobj.getString(FieldType.ACQUIRER_TYPE.toString()))
									&& surchargeData.getPaymentType().getCode()
											.equalsIgnoreCase(dbobj.getString(FieldType.PAYMENT_TYPE.toString()))
									&& surchargeData.getMopType().getCode()
											.equalsIgnoreCase(dbobj.getString(FieldType.MOP_TYPE.toString()))
									&& surchargeData.getPaymentsRegion().name().equalsIgnoreCase(transactionRegion)
									&& surchargeData.getPayId()
											.equalsIgnoreCase(dbobj.getString(FieldType.PAY_ID.toString()))) {

								surchargeStartDate = format.parse(surchargeData.getCreatedDate().toString());
								surchargeEndDate = format.parse(surchargeData.getUpdatedDate().toString());
								if (surchargeStartDate.compareTo(surchargeEndDate) == 0) {
									surchargeEndDate = new Date();
								}

								settlementDate = format.parse(dbobj.getString(FieldType.CREATE_DATE.toString()));

								if (settlementDate.compareTo(surchargeStartDate) >= 0
										&& settlementDate.compareTo(surchargeEndDate) <= 0) {
									surcharge = surchargeData;
									break;
								} else {
									continue;
								}

							}
						}
					} catch (Exception e) {
						logger.error("Exception " + e);
					}
					BigDecimal bankSurchargeFC;
					BigDecimal bankSurchargePercent;

					if (StringUtils.isBlank(dbobj.getString(FieldType.CARD_HOLDER_TYPE.toString()))) {

						bankSurchargeFC = surcharge.getBankSurchargeAmountCustomer();
						bankSurchargePercent = surcharge.getBankSurchargePercentageCustomer();
					}

					else if ((dbobj.getString(FieldType.CARD_HOLDER_TYPE.toString()))
							.equalsIgnoreCase(CardHolderType.CONSUMER.toString())) {

						bankSurchargeFC = surcharge.getBankSurchargeAmountCustomer();
						bankSurchargePercent = surcharge.getBankSurchargePercentageCustomer();
					} else {

						bankSurchargeFC = surcharge.getBankSurchargeAmountCommercial();
						bankSurchargePercent = surcharge.getBankSurchargePercentageCommercial();
					}

					BigDecimal netsurchargeAmount = new BigDecimal(surchargeAmount);

					BigDecimal netcalculatedSurcharge = netsurchargeAmount.subtract(nettxnAmount);
					netcalculatedSurcharge = netcalculatedSurcharge.setScale(2, RoundingMode.HALF_DOWN);

					BigDecimal gstCalculate = netcalculatedSurcharge.multiply(st).divide(((ONE_HUNDRED).add(st)), 2,
							RoundingMode.HALF_DOWN);

					BigDecimal pgSurchargeAmount;
					BigDecimal acquirerSurchargeAmount;

					if (netcalculatedSurcharge.equals(BigDecimal.ZERO.setScale(2, RoundingMode.HALF_DOWN))) {
						pgSurchargeAmount = BigDecimal.ZERO.setScale(2, RoundingMode.HALF_DOWN);
						acquirerSurchargeAmount = BigDecimal.ZERO.setScale(2, RoundingMode.HALF_DOWN);
					}

					else {

						acquirerSurchargeAmount = nettxnAmount.multiply(bankSurchargePercent).divide(((ONE_HUNDRED)), 2,
								RoundingMode.HALF_DOWN);
						acquirerSurchargeAmount = acquirerSurchargeAmount.add(bankSurchargeFC);

						pgSurchargeAmount = netcalculatedSurcharge.subtract(acquirerSurchargeAmount);
						pgSurchargeAmount = pgSurchargeAmount.subtract(gstCalculate);
						pgSurchargeAmount = pgSurchargeAmount.setScale(2, RoundingMode.HALF_DOWN);

					}

					BigDecimal totalSurcharge = netcalculatedSurcharge.subtract(gstCalculate);
					BigDecimal totalAmtPaytoMerchant = netsurchargeAmount.subtract(gstCalculate.add(totalSurcharge));

					acquirerGstAmount = acquirerSurchargeAmount.multiply(st).divide(ONE_HUNDRED, 2,
							RoundingMode.HALF_DOWN);

					merchantGstAmount = pgSurchargeAmount.multiply(st).divide(ONE_HUNDRED, 2, RoundingMode.HALF_DOWN);

					String gstCalculateString = String.valueOf(gstCalculate);
					String totalSurchargeString = String.valueOf(totalSurcharge);
					String totalAmtPaytoMerchantString = String.valueOf(totalAmtPaytoMerchant);
					tdrPojo.setTotalAmtPaytoMerchant(totalAmtPaytoMerchantString);
					tdrPojo.setTotalGstOnMerchant(gstCalculateString);
					tdrPojo.setNetMerchantPayableAmount(totalAmtPaytoMerchantString);
					tdrPojo.setMerchantTdrCalculate(totalSurchargeString);
					tdrPojo.setTotalAmount(surchargeAmount);
					tdrPojo.setAcquirerSurchargeAmount(String.valueOf(acquirerSurchargeAmount));
					tdrPojo.setPgSurchargeAmount(String.valueOf(pgSurchargeAmount));
					//
					AnalyticsData analyticsDataCountSearchObj = new AnalyticsData();
					analyticsDataCountSearchObj.setGst(String.valueOf(st));

					if (dbobj.getString(FieldType.TXNTYPE.toString()).equalsIgnoreCase(TransactionType.RECO.getName())
							|| dbobj.getString(FieldType.TXNTYPE.toString())
									.equalsIgnoreCase(TransactionType.SALE.getName())) {

						analyticsDataCountSearchObj
								.setPaymentMethod(dbobj.getString(FieldType.PAYMENT_TYPE.toString()));
						analyticsDataCountSearchObj.setTxnType(TransactionType.SALE.getName());
						analyticsDataCountSearchObj.setSaleCapturedAmount(tdrPojo.getTotalAmount());
						analyticsDataCountSearchObj.setPgSaleSurcharge(tdrPojo.getPgSurchargeAmount());
						analyticsDataCountSearchObj.setAcquirerSaleSurcharge(tdrPojo.getAcquirerSurchargeAmount());
						analyticsDataCountSearchObj.setPgSaleGst(String.valueOf(merchantGstAmount));
						analyticsDataCountSearchObj.setAcquirerSaleGst(String.valueOf(acquirerGstAmount));
					} else if (dbobj.getString(FieldType.TXNTYPE.toString())
							.equalsIgnoreCase(TransactionType.REFUNDRECO.getName())
							|| dbobj.getString(FieldType.TXNTYPE.toString())
									.equalsIgnoreCase(TransactionType.REFUND.getName())) {
						{

							analyticsDataCountSearchObj
									.setPaymentMethod(dbobj.getString(FieldType.PAYMENT_TYPE.toString()));
							analyticsDataCountSearchObj.setTxnType(TransactionType.REFUND.getName());
							analyticsDataCountSearchObj.setRefundCapturedAmount(tdrPojo.getTotalAmount());
							analyticsDataCountSearchObj.setPgRefundSurcharge(tdrPojo.getPgSurchargeAmount());
							analyticsDataCountSearchObj
									.setAcquirerRefundSurcharge(tdrPojo.getAcquirerSurchargeAmount());
							analyticsDataCountSearchObj.setPgRefundGst(String.valueOf(merchantGstAmount));
							analyticsDataCountSearchObj.setAcquirerRefundGst(String.valueOf(acquirerGstAmount));
						}

					}

					return analyticsDataCountSearchObj;
				}
			}

			else {
				// Get TDR Mode report values here
			}

		}
		return null;
	}

	public AnalyticsData getSettledTransaction(String fromDate, String toDate, String payId, String paymentType,
			String acquirer, User user) {

		try {
			BasicDBObject dateQuery = new BasicDBObject();
			List<BasicDBObject> paramConditionLst = new ArrayList<BasicDBObject>();
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

			if (user.getUserType().equals(UserType.RESELLER)) {
				paramConditionLst.add(new BasicDBObject(FieldType.RESELLER_ID.getName(), user.getResellerId()));
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

			if (!paramConditionLst.isEmpty()) {
				allParamQuery = new BasicDBObject("$and", paramConditionLst);
			}

			// SALE Captured query
			List<BasicDBObject> txnConditionsList = new ArrayList<BasicDBObject>();
			List<BasicDBObject> saleCapturedConditionList = new ArrayList<BasicDBObject>();
			saleCapturedConditionList
					.add(new BasicDBObject(FieldType.TXNTYPE.getName(), TransactionType.RECO.getName()));
			saleCapturedConditionList.add(new BasicDBObject(FieldType.STATUS.getName(), StatusType.SETTLED_SETTLE.getName()));

			BasicDBObject saleConditionQuery = new BasicDBObject("$and", saleCapturedConditionList);
			txnConditionsList.add(saleConditionQuery);

			// FAIL query
			List<BasicDBObject> failedConditionList = new ArrayList<BasicDBObject>();
			failedConditionList.add(new BasicDBObject(FieldType.TXNTYPE.getName(), TransactionType.SALE.getName()));
			failedConditionList.add(new BasicDBObject(FieldType.STATUS.getName(), StatusType.FAILED.getName()));

			BasicDBObject failedConditionQuery = new BasicDBObject("$and", failedConditionList);
			txnConditionsList.add(failedConditionQuery);
			// Error query
			List<BasicDBObject> errorConditionList = new ArrayList<BasicDBObject>();
			errorConditionList.add(new BasicDBObject(FieldType.TXNTYPE.getName(), TransactionType.SALE.getName()));
			errorConditionList.add(new BasicDBObject(FieldType.STATUS.getName(), StatusType.ERROR.getName()));

			BasicDBObject errorConditionQuery = new BasicDBObject("$and", errorConditionList);

			txnConditionsList.add(errorConditionQuery);

			// Cancelled query
			List<BasicDBObject> cancelledConditionList = new ArrayList<BasicDBObject>();
			cancelledConditionList.add(new BasicDBObject(FieldType.TXNTYPE.getName(), TransactionType.SALE.getName()));
			cancelledConditionList.add(new BasicDBObject(FieldType.STATUS.getName(), StatusType.CANCELLED.getName()));

			BasicDBObject cancelledConditionQuery = new BasicDBObject("$and", cancelledConditionList);
			txnConditionsList.add(cancelledConditionQuery);

			// Invalid query
			List<BasicDBObject> invalidConditionList = new ArrayList<BasicDBObject>();
			invalidConditionList.add(new BasicDBObject(FieldType.TXNTYPE.getName(), TransactionType.SALE.getName()));
			invalidConditionList.add(new BasicDBObject(FieldType.STATUS.getName(), StatusType.INVALID.getName()));

			BasicDBObject invalidConditionQuery = new BasicDBObject("$and", invalidConditionList);
			txnConditionsList.add(invalidConditionQuery);

			// Fraud query
			List<BasicDBObject> fraudConditionList = new ArrayList<BasicDBObject>();
			fraudConditionList.add(new BasicDBObject(FieldType.TXNTYPE.getName(), TransactionType.SALE.getName()));
			fraudConditionList.add(new BasicDBObject(FieldType.STATUS.getName(), StatusType.DENIED_BY_FRAUD.getName()));

			BasicDBObject fraudConditionQuery = new BasicDBObject("$and", fraudConditionList);
			txnConditionsList.add(fraudConditionQuery);

			// Dropped query
			List<BasicDBObject> droppedConditionList = new ArrayList<BasicDBObject>();
			droppedConditionList.add(new BasicDBObject(FieldType.TXNTYPE.getName(), TransactionType.SALE.getName()));
			droppedConditionList.add(new BasicDBObject(FieldType.STATUS.getName(), StatusType.TIMEOUT.getName()));

			BasicDBObject droppedConditionQuery = new BasicDBObject("$and", droppedConditionList);
			txnConditionsList.add(droppedConditionQuery);

			// Rejected query
			List<BasicDBObject> rejectedConditionList = new ArrayList<BasicDBObject>();
			rejectedConditionList.add(new BasicDBObject(FieldType.TXNTYPE.getName(), TransactionType.SALE.getName()));
			rejectedConditionList.add(new BasicDBObject(FieldType.STATUS.getName(), StatusType.REJECTED.getName()));

			BasicDBObject rejectedConditionQuery = new BasicDBObject("$and", rejectedConditionList);
			txnConditionsList.add(rejectedConditionQuery);

			BasicDBObject saleCapturedConditionQuery = new BasicDBObject("$or", txnConditionsList);

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

			allConditionQueryList.add(saleCapturedConditionQuery);

			BasicDBObject allConditionQueryObj = new BasicDBObject("$and", allConditionQueryList);

			List<BasicDBObject> fianlList = new ArrayList<BasicDBObject>();

			if (!allParamQuery.isEmpty()) {
				fianlList.add(allParamQuery);
			}
			if (!allConditionQueryObj.isEmpty()) {
				fianlList.add(allConditionQueryObj);
			}

			BasicDBObject finalQuery = new BasicDBObject("$and", fianlList);
			MongoDatabase dbIns = mongoInstance.getDB();
			MongoCollection<Document> coll = dbIns
					.getCollection(PropertiesManager.propertiesMap.get(prefix + Constants.COLLECTION_NAME.getValue()));

			double ccSuccess = 0;
			double ccFailed = 0;
			double ccCancelled = 0;
			double ccInvalid = 0;
			double ccFraud = 0;
			double ccDropped = 0;
			double ccRejected = 0;

			double dcSuccess = 0;
			double dcFailed = 0;
			double dcCancelled = 0;
			double dcInvalid = 0;
			double dcFraud = 0;
			double dcDropped = 0;
			double dcRejected = 0;

			double upSuccess = 0;
			double upFailed = 0;
			double upCancelled = 0;
			double upInvalid = 0;
			double upFraud = 0;
			double upDropped = 0;
			double upRejected = 0;

			double totalTxn = 0;
			double totalCCTxn = 0;
			double totalDCTxn = 0;
			double totalUPTxn = 0;

			int unknownTransactions = 0;

			BigDecimal totalTxnAmount = BigDecimal.ZERO.setScale(2, RoundingMode.HALF_DOWN);

			BigDecimal totalCCTxnAmount = BigDecimal.ZERO.setScale(2, RoundingMode.HALF_DOWN);
			BigDecimal totalDCTxnAmount = BigDecimal.ZERO.setScale(2, RoundingMode.HALF_DOWN);
			BigDecimal totalUPTxnAmount = BigDecimal.ZERO.setScale(2, RoundingMode.HALF_DOWN);

			MongoCursor<Document> cursor = coll.find(finalQuery).iterator();

			// Remove all data from an earlier map
			while (cursor.hasNext()) {

				totalTxn++;
				Document dbobj = cursor.next();

				if (StringUtils.isBlank(dbobj.getString(FieldType.PAYMENT_TYPE.toString()))) {
					unknownTransactions++;
					continue;
				}
				switch (PaymentType.getInstanceUsingCode(dbobj.getString(FieldType.PAYMENT_TYPE.toString()))) {

				case DEBIT_CARD:

					totalDCTxn++;
					switch (dbobj.getString(FieldType.STATUS.toString())) {

					case CAPTURED:

						if (!StringUtils.isBlank(dbobj.getString(FieldType.TOTAL_AMOUNT.toString()))) {
							BigDecimal txnAmount = new BigDecimal(dbobj.getString(FieldType.TOTAL_AMOUNT.toString()))
									.setScale(2, RoundingMode.HALF_DOWN);
							totalTxnAmount = totalTxnAmount.add(txnAmount).setScale(2, RoundingMode.HALF_DOWN);
							totalDCTxnAmount = totalDCTxnAmount.add(txnAmount).setScale(2, RoundingMode.HALF_DOWN);

						}

						dcSuccess++;
						break;

					case ERROR:
						dcFailed++;
						break;

					case TIMEOUT:
						dcDropped++;
						break;

					case CANCELLED:
						dcCancelled++;
						break;

					case DENIED:
						dcRejected++;
						break;

					case REJECTED:
						dcRejected++;
						break;

					case FAILED:
						dcFailed++;
						break;

					case INVALID:
						dcInvalid++;
						break;

					case DENIED_BY_FRAUD:
						dcFraud++;
						break;

					default:
						break;
					}

					break;

				case CREDIT_CARD:

					totalCCTxn++;
					switch (dbobj.getString(FieldType.STATUS.toString())) {

					case CAPTURED:

						if (!StringUtils.isBlank(dbobj.getString(FieldType.TOTAL_AMOUNT.toString()))) {
							BigDecimal txnAmount = new BigDecimal(dbobj.getString(FieldType.TOTAL_AMOUNT.toString()))
									.setScale(2, RoundingMode.HALF_DOWN);
							;
							totalTxnAmount = totalTxnAmount.add(txnAmount).setScale(2, RoundingMode.HALF_DOWN);
							totalCCTxnAmount = totalCCTxnAmount.add(txnAmount).setScale(2, RoundingMode.HALF_DOWN);
						}

						ccSuccess++;

						break;

					case ERROR:
						ccFailed++;
						break;

					case TIMEOUT:
						ccDropped++;
						break;

					case CANCELLED:
						ccCancelled++;
						break;

					case DENIED:
						ccRejected++;
						break;

					case REJECTED:
						ccRejected++;
						break;

					case FAILED:
						ccFailed++;
						break;

					case INVALID:
						ccInvalid++;
						break;

					case DENIED_BY_FRAUD:
						ccFraud++;
						break;

					default:
						break;
					}

					break;

				case UPI:

					totalUPTxn++;
					switch (dbobj.getString(FieldType.STATUS.toString())) {

					case CAPTURED:

						if (!StringUtils.isBlank(dbobj.getString(FieldType.TOTAL_AMOUNT.toString()))) {
							BigDecimal txnAmount = new BigDecimal(dbobj.getString(FieldType.TOTAL_AMOUNT.toString()))
									.setScale(2, RoundingMode.HALF_DOWN);
							totalTxnAmount = totalTxnAmount.add(txnAmount).setScale(2, RoundingMode.HALF_DOWN);
							totalUPTxnAmount = totalUPTxnAmount.add(txnAmount).setScale(2, RoundingMode.HALF_DOWN);
						}

						upSuccess++;
						break;

					case ERROR:
						upFailed++;
						break;

					case TIMEOUT:
						upDropped++;
						break;

					case CANCELLED:
						upCancelled++;
						break;

					case DENIED:
						upRejected++;
						break;

					case REJECTED:
						upRejected++;
						break;

					case FAILED:
						upFailed++;
						break;

					case INVALID:
						upInvalid++;
						break;

					case DENIED_BY_FRAUD:
						upFraud++;
						break;

					default:
						break;
					}

					break;

				default:
					break;

				}
			}

			cursor.close();

			// totalTxnAmount = totalTxnAmount.setScale(2, RoundingMode.HALF_DOWN);
			AnalyticsData analyticsData = new AnalyticsData();

			double totalTxnSuccess = ccSuccess + dcSuccess + upSuccess;
			double totalFailed = ccFailed + dcFailed + upFailed;

			analyticsData.setTotalTxnCount(String.format("%.0f", totalTxn));
			analyticsData.setSuccessTxnCount(String.format("%.0f", totalTxnSuccess));
			analyticsData.setFailedTxnCount(String.format("%.2f", totalFailed));

			if (totalTxn == 0.00) {
				analyticsData.setSuccessTxnPercent("0.00");
			} else {

				double successTxnPercent = (totalTxnSuccess / totalTxn) * 100;
				analyticsData.setSuccessTxnPercent(String.format("%.2f", successTxnPercent));
			}

			BigDecimal totalTxnCountBD = new BigDecimal(totalTxnSuccess);
			totalTxnCountBD = totalTxnCountBD.setScale(2, RoundingMode.HALF_DOWN);

			if (totalTxnCountBD.compareTo(BigDecimal.ONE) < 0) {
				analyticsData.setAvgTkt("0.00");
			} else {

				String totalTxnAmountString = String.valueOf(totalTxnAmount);

				double totalTxnAmountDouble = Double.valueOf(totalTxnAmountString);
				double totalTxnSuccessDouble = Double.valueOf(totalTxnSuccess);
				double avgTicketSizeDouble = totalTxnAmountDouble / totalTxnSuccessDouble;

				analyticsData.setAvgTkt(String.format("%.2f", avgTicketSizeDouble));
			}

			if (totalTxnSuccess < 1) {
				analyticsData.setCCTxnPercent("0.00");
				analyticsData.setDCTxnPercent("0.00");
				analyticsData.setUPTxnPercent("0.00");
			}

			else {

				double cCTxnPercent = (ccSuccess / totalTxnSuccess) * 100;
				double dCTxnPercent = (dcSuccess / totalTxnSuccess) * 100;
				double upTxnPercent = (upSuccess / totalTxnSuccess) * 100;

				analyticsData.setCCTxnPercent(String.format("%.2f", cCTxnPercent));
				analyticsData.setDCTxnPercent(String.format("%.2f", dCTxnPercent));
				analyticsData.setUPTxnPercent(String.format("%.2f", upTxnPercent));
			}

			if (ccSuccess + ccFailed < 1) {
				analyticsData.setCCSuccessRate("0.00");
			} else {
				double cCSuccessRate = (ccSuccess / (ccSuccess + ccFailed)) * 100;
				analyticsData.setCCSuccessRate(String.format("%.2f", cCSuccessRate));
			}

			if (dcSuccess + dcFailed < 1) {
				analyticsData.setDCSuccessRate("0.00");
			} else {
				double dCSuccessRate = (dcSuccess / (dcSuccess + dcFailed)) * 100;
				analyticsData.setDCSuccessRate(String.format("%.2f", dCSuccessRate));
			}

			if (upSuccess + upFailed < 1) {
				analyticsData.setUPSuccessRate("0.00");
			} else {
				double uPSuccessRate = (upSuccess / (upSuccess + upFailed)) * 100;
				analyticsData.setUPSuccessRate(String.format("%.2f", uPSuccessRate));
			}

			if (totalCCTxn < 1) {

				analyticsData.setTotalCCTxn("0");
				analyticsData.setTotalCCSuccessTxnPercent("0.00");
				analyticsData.setTotalCCFailedTxnPercent("0.00");
				analyticsData.setTotalCCCancelledTxnPercent("0.00");
				analyticsData.setTotalCCInvalidTxnPercent("0.00");
				analyticsData.setTotalCCFraudTxnPercent("0.00");
				analyticsData.setTotalCCDroppedTxnPercent("0.00");
				analyticsData.setTotalCCRejectedTxnPercent("0.00");

			} else {

				double totalCCSuccessTxnPercent = (ccSuccess / (totalCCTxn)) * 100;
				double totalCCFailedTxnPercent = (ccFailed / (totalCCTxn)) * 100;
				double totalCCCancelledTxnPercent = (ccCancelled / (totalCCTxn)) * 100;
				double totalCCInvalidTxnPercent = (ccInvalid / (totalCCTxn)) * 100;
				double totalCCFraudTxnPercent = (ccFraud / (totalCCTxn)) * 100;
				double totalCCDroppedTxnPercent = (ccDropped / (totalCCTxn)) * 100;
				double totalCCRejectedTxnPercent = (ccRejected / (totalCCTxn)) * 100;

				analyticsData.setTotalCCTxn(String.format("%.0f", totalCCTxn));
				analyticsData.setTotalCCSuccessTxnPercent(String.format("%.2f", totalCCSuccessTxnPercent) + " %");
				analyticsData.setTotalCCFailedTxnPercent(String.format("%.2f", totalCCFailedTxnPercent));
				analyticsData.setTotalCCCancelledTxnPercent(String.format("%.2f", totalCCCancelledTxnPercent));
				analyticsData.setTotalCCInvalidTxnPercent(String.format("%.2f", totalCCInvalidTxnPercent));
				analyticsData.setTotalCCFraudTxnPercent(String.format("%.2f", totalCCFraudTxnPercent));
				analyticsData.setTotalCCDroppedTxnPercent(String.format("%.2f", totalCCDroppedTxnPercent));
				analyticsData.setTotalCCRejectedTxnPercent(String.format("%.2f", totalCCRejectedTxnPercent));

			}

			if (totalDCTxn < 1) {

				analyticsData.setTotalDCTxn("0");
				analyticsData.setTotalDCSuccessTxnPercent("0.00");
				analyticsData.setTotalDCFailedTxnPercent("0.00");
				analyticsData.setTotalDCCancelledTxnPercent("0.00");
				analyticsData.setTotalDCInvalidTxnPercent("0.00");
				analyticsData.setTotalDCFraudTxnPercent("0.00");
				analyticsData.setTotalDCDroppedTxnPercent("0.00");
				analyticsData.setTotalDCRejectedTxnPercent("0.00");

			} else {

				double totalDCSuccessTxnPercent = (dcSuccess / (totalDCTxn)) * 100;
				double totalDCFailedTxnPercent = (dcFailed / (totalDCTxn)) * 100;
				double totalDCCancelledTxnPercent = (dcCancelled / (totalDCTxn)) * 100;
				double totalDCInvalidTxnPercent = (dcInvalid / (totalDCTxn)) * 100;
				double totalDCFraudTxnPercent = (dcFraud / (totalDCTxn)) * 100;
				double totalDCDroppedTxnPercent = (dcDropped / (totalDCTxn)) * 100;
				double totalDCRejectedTxnPercent = (dcRejected / (totalDCTxn)) * 100;

				analyticsData.setTotalDCTxn(String.format("%.0f", totalDCTxn));
				analyticsData.setTotalDCSuccessTxnPercent(String.format("%.2f", totalDCSuccessTxnPercent));
				analyticsData.setTotalDCFailedTxnPercent(String.format("%.2f", totalDCFailedTxnPercent));
				analyticsData.setTotalDCCancelledTxnPercent(String.format("%.2f", totalDCCancelledTxnPercent));
				analyticsData.setTotalDCInvalidTxnPercent(String.format("%.2f", totalDCInvalidTxnPercent));
				analyticsData.setTotalDCFraudTxnPercent(String.format("%.2f", totalDCFraudTxnPercent));
				analyticsData.setTotalDCDroppedTxnPercent(String.format("%.2f", totalDCDroppedTxnPercent));
				analyticsData.setTotalDCRejectedTxnPercent(String.format("%.2f", totalDCRejectedTxnPercent));

			}

			if (totalUPTxn < 1) {

				analyticsData.setTotalUPTxn("0");
				analyticsData.setTotalUPSuccessTxnPercent("0.00");
				analyticsData.setTotalUPFailedTxnPercent("0.00");
				analyticsData.setTotalUPCancelledTxnPercent("0.00");
				analyticsData.setTotalUPInvalidTxnPercent("0.00");
				analyticsData.setTotalUPFraudTxnPercent("0.00");
				analyticsData.setTotalUPDroppedTxnPercent("0.00");
				analyticsData.setTotalUPRejectedTxnPercent("0.00");
			} else {

				analyticsData.setTotalUPTxn(String.format("%.0f", totalUPTxn));

				double totalUPSuccessTxnPercent = (upSuccess / (totalUPTxn)) * 100;
				double totalUPFailedTxnPercent = (upFailed / (totalUPTxn)) * 100;
				double totalUPCancelledTxnPercent = (upCancelled / (totalUPTxn)) * 100;
				double totalUPInvalidTxnPercent = (upInvalid / (totalUPTxn)) * 100;
				double totalUPFraudTxnPercent = (upFraud / (totalUPTxn)) * 100;
				double totalUPDroppedTxnPercent = (upDropped / (totalUPTxn)) * 100;
				double totalUPRejectedTxnPercent = (upRejected / (totalUPTxn)) * 100;

				analyticsData.setTotalUPSuccessTxnPercent(String.format("%.2f", totalUPSuccessTxnPercent));
				analyticsData.setTotalUPFailedTxnPercent(String.format("%.2f", totalUPFailedTxnPercent));
				analyticsData.setTotalUPCancelledTxnPercent(String.format("%.2f", totalUPCancelledTxnPercent));
				analyticsData.setTotalUPInvalidTxnPercent(String.format("%.2f", totalUPInvalidTxnPercent));
				analyticsData.setTotalUPFraudTxnPercent(String.format("%.2f", totalUPFraudTxnPercent));
				analyticsData.setTotalUPDroppedTxnPercent(String.format("%.2f", totalUPDroppedTxnPercent));
				analyticsData.setTotalUPRejectedTxnPercent(String.format("%.2f", totalUPRejectedTxnPercent));

			}

			if (paymentType.equals(PaymentType.CREDIT_CARD.getCode())) {

				analyticsData.setCaptured(String.format("%.0f", ccSuccess));
				analyticsData.setFailed(String.format("%.0f", ccFailed));
				analyticsData.setCancelled(String.format("%.0f", ccCancelled));
				analyticsData.setInvalid(String.format("%.0f", ccInvalid));
				analyticsData.setFraud(String.format("%.0f", ccFraud));
				analyticsData.setDropped(String.format("%.0f", ccDropped));
				analyticsData.setRejected(String.format("%.0f", ccRejected));

				analyticsData.setCapturedPercent(String.format("%.2f", (ccSuccess / totalCCTxn) * 100));
				analyticsData.setFailedPercent(String.format("%.2f", (ccFailed / totalCCTxn) * 100));
				analyticsData.setCancelledPercent(String.format("%.2f", (ccCancelled / totalCCTxn) * 100));
				analyticsData.setInvalidPercent(String.format("%.2f", (ccInvalid / totalCCTxn) * 100));
				analyticsData.setFraudPercent(String.format("%.2f", (ccFraud / totalCCTxn) * 100));
				analyticsData.setDroppedPercent(String.format("%.2f", (ccDropped / totalCCTxn) * 100));
				analyticsData.setRejectedPercent(String.format("%.2f", (ccRejected / totalCCTxn) * 100));

			} else if (paymentType.equals(PaymentType.DEBIT_CARD.getCode())) {

				analyticsData.setCaptured(String.format("%.0f", dcSuccess));
				analyticsData.setFailed(String.format("%.0f", dcFailed));
				analyticsData.setCancelled(String.format("%.0f", dcCancelled));
				analyticsData.setInvalid(String.format("%.0f", dcInvalid));
				analyticsData.setFraud(String.format("%.0f", dcFraud));
				analyticsData.setDropped(String.format("%.0f", dcDropped));
				analyticsData.setRejected(String.format("%.0f", dcRejected));

				analyticsData.setCapturedPercent(String.format("%.2f", (dcSuccess / totalDCTxn) * 100));
				analyticsData.setFailedPercent(String.format("%.2f", (dcFailed / totalDCTxn) * 100));
				analyticsData.setCancelledPercent(String.format("%.2f", (dcCancelled / totalDCTxn) * 100));
				analyticsData.setInvalidPercent(String.format("%.2f", (dcInvalid / totalDCTxn) * 100));
				analyticsData.setFraudPercent(String.format("%.2f", (dcFraud / totalDCTxn) * 100));
				analyticsData.setDroppedPercent(String.format("%.2f", (dcDropped / totalDCTxn) * 100));
				analyticsData.setRejectedPercent(String.format("%.2f", (dcRejected / totalDCTxn) * 100));

			} else if (paymentType.equals(PaymentType.UPI.getCode())) {

				analyticsData.setCaptured(String.format("%.0f", upSuccess));
				analyticsData.setFailed(String.format("%.0f", upFailed));
				analyticsData.setCancelled(String.format("%.0f", upCancelled));
				analyticsData.setInvalid(String.format("%.0f", upInvalid));
				analyticsData.setFraud(String.format("%.0f", upFraud));
				analyticsData.setDropped(String.format("%.0f", upDropped));
				analyticsData.setRejected(String.format("%.0f", upRejected));

				analyticsData.setCapturedPercent(String.format("%.2f", (upSuccess / totalUPTxn) * 100));
				analyticsData.setFailedPercent(String.format("%.2f", (upFailed / totalUPTxn) * 100));
				analyticsData.setCancelledPercent(String.format("%.2f", (upCancelled / totalUPTxn) * 100));
				analyticsData.setInvalidPercent(String.format("%.2f", (upInvalid / totalUPTxn) * 100));
				analyticsData.setFraudPercent(String.format("%.2f", (upFraud / totalUPTxn) * 100));
				analyticsData.setDroppedPercent(String.format("%.2f", (upDropped / totalUPTxn) * 100));
				analyticsData.setRejectedPercent(String.format("%.2f", (upRejected / totalUPTxn) * 100));

			}

			else {

				analyticsData.setCaptured("0");
				analyticsData.setFailed("0");
				analyticsData.setCancelled("0");
				analyticsData.setInvalid("0");
				analyticsData.setFraud("0");
				analyticsData.setDropped("0");
				analyticsData.setRejected("0");

				analyticsData.setCapturedPercent("0.00");
				analyticsData.setFailedPercent("0.00");
				analyticsData.setCancelledPercent("0.00");
				analyticsData.setInvalidPercent("0.00");
				analyticsData.setFraudPercent("0.00");
				analyticsData.setDroppedPercent("0.00");
				analyticsData.setRejectedPercent("0.00");

			}

			analyticsData.setTotalCCTxnAmount(String.format("%.2f", totalCCTxnAmount));
			analyticsData.setTotalDCTxnAmount(String.format("%.2f", totalDCTxnAmount));
			analyticsData.setTotalUPTxnAmount(String.format("%.2f", totalUPTxnAmount));

			analyticsData.setTotalCCCapturedCount(String.format("%.0f", ccSuccess));
			analyticsData.setTotalDCCapturedCount(String.format("%.0f", dcSuccess));
			analyticsData.setTotalUPCapturedCount(String.format("%.0f", upSuccess));

			analyticsData.setUnknownTxnCount(String.valueOf(unknownTransactions));

			// Refund and settled query
			List<BasicDBObject> saleSettleConditionsList = new ArrayList<BasicDBObject>();

			List<BasicDBObject> saleSettledConditionList = new ArrayList<BasicDBObject>();
			saleSettledConditionList
					.add(new BasicDBObject(FieldType.TXNTYPE.getName(), TransactionType.RECO.getName()));
			saleSettledConditionList.add(new BasicDBObject(FieldType.STATUS.getName(), StatusType.SETTLED_SETTLE.getName()));

			BasicDBObject saleSettledQuery = new BasicDBObject("$and", saleSettledConditionList);

			saleSettleConditionsList.add(saleSettledQuery);

			List<BasicDBObject> deltaRefundCapturedConditionsList = new ArrayList<BasicDBObject>();

			List<BasicDBObject> deltaRefundCapturedConditionList = new ArrayList<BasicDBObject>();
			deltaRefundCapturedConditionList
					.add(new BasicDBObject(FieldType.TXNTYPE.getName(), TransactionType.REFUND.getName()));
			deltaRefundCapturedConditionList
					.add(new BasicDBObject(FieldType.STATUS.getName(), StatusType.CAPTURED.getName()));
			deltaRefundCapturedConditionList.add(new BasicDBObject(FieldType.UDF6.getName(), Constants.Y.getValue()));

			BasicDBObject deltaRefundQuery = new BasicDBObject("$and", deltaRefundCapturedConditionList);

			deltaRefundCapturedConditionsList.add(deltaRefundQuery);

			List<BasicDBObject> postSettleConditionsList = new ArrayList<BasicDBObject>();

			List<BasicDBObject> postSettleConditionList = new ArrayList<BasicDBObject>();
			postSettleConditionList.add(new BasicDBObject(FieldType.TXNTYPE.getName(), TransactionType.SALE.getName()));
			postSettleConditionList.add(new BasicDBObject(FieldType.STATUS.getName(), StatusType.CAPTURED.getName()));
			postSettleConditionList
					.add(new BasicDBObject(FieldType.POST_SETTLED_FLAG.getName(), Constants.Y.getValue()));

			BasicDBObject postSettleQuery = new BasicDBObject("$and", postSettleConditionList);

			postSettleConditionsList.add(postSettleQuery);

			BasicDBObject saleSettledConditionQuery = new BasicDBObject("$or", saleSettleConditionsList);
			BasicDBObject deltaRefundConditionQuery = new BasicDBObject("$or", deltaRefundCapturedConditionsList);
			BasicDBObject postSettleConditionQuery = new BasicDBObject("$or", postSettleConditionsList);

			List<BasicDBObject> allConditionQueryList1 = new ArrayList<BasicDBObject>();
			List<BasicDBObject> allConditionQueryList2 = new ArrayList<BasicDBObject>();
			List<BasicDBObject> allConditionQueryList3 = new ArrayList<BasicDBObject>();

			if (!acquirerQuery.isEmpty()) {
				allConditionQueryList1.add(acquirerQuery);
			}
			if (!dateQuery.isEmpty()) {
				allConditionQueryList1.add(dateQuery);
			}

			if (!dateIndexConditionQuery.isEmpty()) {
				allConditionQueryList1.add(dateIndexConditionQuery);
			}

			allConditionQueryList1.add(saleSettledConditionQuery);

			if (!acquirerQuery.isEmpty()) {
				allConditionQueryList2.add(acquirerQuery);
			}
			if (!dateQuery.isEmpty()) {
				allConditionQueryList2.add(dateQuery);
			}

			if (!dateIndexConditionQuery.isEmpty()) {
				allConditionQueryList2.add(dateIndexConditionQuery);
			}

			allConditionQueryList2.add(deltaRefundConditionQuery);

			if (!acquirerQuery.isEmpty()) {
				allConditionQueryList3.add(acquirerQuery);
			}
			if (!dateQuery.isEmpty()) {
				allConditionQueryList3.add(dateQuery);
			}

			if (!dateIndexConditionQuery.isEmpty()) {
				allConditionQueryList3.add(dateIndexConditionQuery);
			}
			allConditionQueryList3.add(postSettleConditionQuery);

			BasicDBObject allConditionQueryObj1 = new BasicDBObject("$and", allConditionQueryList1);
			BasicDBObject allConditionQueryObj2 = new BasicDBObject("$and", allConditionQueryList2);
			BasicDBObject allConditionQueryObj3 = new BasicDBObject("$and", allConditionQueryList3);

			List<BasicDBObject> finalList1 = new ArrayList<BasicDBObject>();
			List<BasicDBObject> finalList2 = new ArrayList<BasicDBObject>();
			List<BasicDBObject> finalList3 = new ArrayList<BasicDBObject>();

			if (!allParamQuery.isEmpty()) {
				finalList1.add(allParamQuery);
			}
			if (!allConditionQueryObj1.isEmpty()) {
				finalList1.add(allConditionQueryObj1);
			}

			if (!allParamQuery.isEmpty()) {
				finalList2.add(allParamQuery);
			}
			if (!allConditionQueryObj2.isEmpty()) {
				finalList2.add(allConditionQueryObj2);
			}

			if (!allParamQuery.isEmpty()) {
				finalList3.add(allParamQuery);
			}
			if (!allConditionQueryObj2.isEmpty()) {
				finalList3.add(allConditionQueryObj3);
			}

			BasicDBObject finalQuery1 = new BasicDBObject("$and", finalList1);
			BasicDBObject finalQuery2 = new BasicDBObject("$and", finalList2);
			BasicDBObject finalQuery3 = new BasicDBObject("$and", finalList3);

			double totalSettled = coll.count(finalQuery1);
			double totalDeltaRefund = coll.count(finalQuery2);
			double totalPostSettled = coll.count(finalQuery3);

			if (totalSettled == 0.0) {
				analyticsData.setMerchantPgRatio("0.00 %");
				analyticsData.setAcquirerPgRatio("0.00 %");
			}

			else {
				double merchantPgRatio = (((totalSettled - totalDeltaRefund) / (totalSettled)) * 100);
				double acquirerPgRatio = (((totalSettled - totalPostSettled) / (totalSettled)) * 100);

				analyticsData.setMerchantPgRatio(String.format("%.2f", merchantPgRatio) + " %");
				analyticsData.setAcquirerPgRatio(String.format("%.2f", acquirerPgRatio) + " %");
			}

			return analyticsData;
		}

		catch (Exception e) {
			logger.error("Exception in transaction summary count service " + e);
		}
		return null;
	}

	public FunnelChatData funnelChatDataSummary(String payId, String currency, String fromDate, String toDate,
			UserType userType, String paymentType, String acquirer, String transactionType, String mopType, User user) {
		logger.info("FunnelChatData query generation started ");
		FunnelChatData funnelChatData = new FunnelChatData();
		BasicDBObject dateQuery = new BasicDBObject();
		/*
		 * List<BasicDBObject> currencyConditionLst = new ArrayList<BasicDBObject>();
		 * List<BasicDBObject> saleOrAuthList = new ArrayList<BasicDBObject>();
		 */
		BasicDBObject acquirerQuery = new BasicDBObject();
		List<BasicDBObject> acquirerConditionLst = new ArrayList<BasicDBObject>();
		List<BasicDBObject> mopTypeConditionLst = new ArrayList<BasicDBObject>();
		BasicDBObject mopTypeQuery = new BasicDBObject();
		List<BasicDBObject> paymentTypeConditionLst = new ArrayList<BasicDBObject>();
		BasicDBObject paymentTypeQuery = new BasicDBObject();
		List<BasicDBObject> currencyConditionLst = new ArrayList<BasicDBObject>();
		BasicDBObject currencyCodeQuery = new BasicDBObject();

		// BasicDBObject currencyQuery = new BasicDBObject();

		BasicDBObject payIdquery = new BasicDBObject();
		BasicDBObject resellerIdquery = new BasicDBObject();

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
			fromDate = fromDate + " 00:00:00";
			currentDate = currentDate + " 23:59:59";
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
			 * if (!payId.equalsIgnoreCase("ALL")) {
			 * payIdquery.put(FieldType.PAY_ID.getName(), payId); }
			 */
			if (!payId.equalsIgnoreCase("ALL")) {
                payIdquery.put(FieldType.PAY_ID.getName(), payId);
            }

			if (user.getUserType().equals(UserType.RESELLER)) {
				resellerIdquery.put(FieldType.RESELLER_ID.getName(), user.getResellerId());
			}

			/*
			 * if (!currency.equalsIgnoreCase("ALL")) {
			 * currencyQuery.put(FieldType.CURRENCY_CODE.getName(), currency); }
			 */

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

			BasicDBObject txntypeQuery = new BasicDBObject();
			if (!transactionType.equalsIgnoreCase("ALL")) {
				txntypeQuery = new BasicDBObject(FieldType.ORIG_TXNTYPE.getName(), transactionType);
			}

			// FAIL QUERY START //
			BasicDBObject failedQuery = new BasicDBObject(FieldType.ALIAS_STATUS.getName(),
					StatusType.FAILED.getName());
			// FAIL QUERY END //

			// CANCELLED QUERY START //
			BasicDBObject cancelledQuery = new BasicDBObject(FieldType.ALIAS_STATUS.getName(),
					StatusType.CANCELLED.getName());
			// CANCELLED QUERY END //

			// INVALID QUERY START //
			BasicDBObject invalidQuery = new BasicDBObject(FieldType.ALIAS_STATUS.getName(),
					StatusType.INVALID.getName());
			// INVALID QUERY END //

			// SUCCESS Query Start
			List<BasicDBObject> successList = new ArrayList<BasicDBObject>();
			successList.add(new BasicDBObject(FieldType.ALIAS_STATUS.getName(), StatusType.CAPTURED.getName()));
			successList.add(new BasicDBObject(FieldType.ALIAS_STATUS.getName(), StatusType.SETTLED_SETTLE.getName()));
			BasicDBObject successQuery = new BasicDBObject("$or", successList);
			// SUCCESS Query End

			// Success start
			List<BasicDBObject> allConditionQueryListForSucc = new ArrayList<BasicDBObject>();
			/*
			 * if (!currencyQuery.isEmpty()) {
			 * allConditionQueryListForSucc.add(currencyQuery); }
			 */

			if (!dateQuery.isEmpty()) {
				allConditionQueryListForSucc.add(dateQuery);
			}
			if (!payIdquery.isEmpty()) {
				allConditionQueryListForSucc.add(payIdquery);
			}

			if (!resellerIdquery.isEmpty()) {
				allConditionQueryListForSucc.add(resellerIdquery);
			}

			if (!dateIndexConditionQuery.isEmpty()) {
				allConditionQueryListForSucc.add(dateIndexConditionQuery);
			}

			if (!txntypeQuery.isEmpty()) {
				allConditionQueryListForSucc.add(txntypeQuery);
			}

			if (!acquirerQuery.isEmpty()) {
				allConditionQueryListForSucc.add(acquirerQuery);
			}

			if (!mopTypeQuery.isEmpty()) {
				allConditionQueryListForSucc.add(mopTypeQuery);
			}
			if (!paymentTypeQuery.isEmpty()) {
				allConditionQueryListForSucc.add(paymentTypeQuery);
			}
			if (!currencyCodeQuery.isEmpty()) {
				allConditionQueryListForSucc.add(currencyCodeQuery);
			}

			allConditionQueryListForSucc.add(successQuery);

			BasicDBObject allConditionQueryObjforsucc = new BasicDBObject("$and", allConditionQueryListForSucc);

			// Success end

			// fail start
			List<BasicDBObject> allConditionQueryListForfail = new ArrayList<BasicDBObject>();
			/*
			 * if (!currencyQuery.isEmpty()) {
			 * allConditionQueryListForfail.add(currencyQuery); }
			 */

			if (!dateQuery.isEmpty()) {
				allConditionQueryListForfail.add(dateQuery);
			}
			if (!payIdquery.isEmpty()) {
				allConditionQueryListForfail.add(payIdquery);
			}

			if (!dateIndexConditionQuery.isEmpty()) {
				allConditionQueryListForfail.add(dateIndexConditionQuery);
			}

			if (!txntypeQuery.isEmpty()) {
				allConditionQueryListForfail.add(txntypeQuery);
			}

			if (!acquirerQuery.isEmpty()) {
				allConditionQueryListForfail.add(acquirerQuery);
			}

			if (!mopTypeQuery.isEmpty()) {
				allConditionQueryListForfail.add(mopTypeQuery);
			}
			if (!paymentTypeQuery.isEmpty()) {
				allConditionQueryListForfail.add(paymentTypeQuery);
			}
			if (!currencyCodeQuery.isEmpty()) {
				allConditionQueryListForfail.add(currencyCodeQuery);
			}

			allConditionQueryListForfail.add(failedQuery);

			BasicDBObject allConditionQueryObjforfail = new BasicDBObject("$and", allConditionQueryListForfail);

			// failed end

			// cancel start
			List<BasicDBObject> allConditionQueryListForCancelled = new ArrayList<BasicDBObject>();
			/*
			 * if (!currencyQuery.isEmpty()) {
			 * allConditionQueryListForCancelled.add(currencyQuery); }
			 */
			if (!dateQuery.isEmpty()) {
				allConditionQueryListForCancelled.add(dateQuery);
			}
			if (!payIdquery.isEmpty()) {
				allConditionQueryListForCancelled.add(payIdquery);
			}

			if (!dateIndexConditionQuery.isEmpty()) {
				allConditionQueryListForCancelled.add(dateIndexConditionQuery);
			}

			if (!txntypeQuery.isEmpty()) {
				allConditionQueryListForCancelled.add(txntypeQuery);
			}

			if (!acquirerQuery.isEmpty()) {
				allConditionQueryListForCancelled.add(acquirerQuery);
			}

			if (!mopTypeQuery.isEmpty()) {
				allConditionQueryListForCancelled.add(mopTypeQuery);
			}
			if (!paymentTypeQuery.isEmpty()) {
				allConditionQueryListForCancelled.add(paymentTypeQuery);
			}
			if (!currencyCodeQuery.isEmpty()) {
				allConditionQueryListForCancelled.add(currencyCodeQuery);
			}

			allConditionQueryListForCancelled.add(cancelledQuery);

			BasicDBObject allConditionQueryObjforCancelled = new BasicDBObject("$and",
					allConditionQueryListForCancelled);

			// cancel end

			// Invalid start
			List<BasicDBObject> allConditionQueryListForInvalid = new ArrayList<BasicDBObject>();
			/*
			 * if (!currencyQuery.isEmpty()) {
			 * allConditionQueryListForInvalid.add(currencyQuery); }
			 */

			if (!dateQuery.isEmpty()) {
				allConditionQueryListForInvalid.add(dateQuery);
			}
			if (!payIdquery.isEmpty()) {
				allConditionQueryListForInvalid.add(payIdquery);
			}

			if (!dateIndexConditionQuery.isEmpty()) {
				allConditionQueryListForInvalid.add(dateIndexConditionQuery);
			}

			if (!txntypeQuery.isEmpty()) {
				allConditionQueryListForInvalid.add(txntypeQuery);
			}

			if (!acquirerQuery.isEmpty()) {
				allConditionQueryListForInvalid.add(acquirerQuery);
			}

			if (!mopTypeQuery.isEmpty()) {
				allConditionQueryListForInvalid.add(mopTypeQuery);
			}
			if (!paymentTypeQuery.isEmpty()) {
				allConditionQueryListForInvalid.add(paymentTypeQuery);
			}
			if (!currencyCodeQuery.isEmpty()) {
				allConditionQueryListForInvalid.add(currencyCodeQuery);
			}

			allConditionQueryListForInvalid.add(invalidQuery);

			BasicDBObject allConditionQueryObjforInvalid = new BasicDBObject("$and", allConditionQueryListForInvalid);

			// Invalid end

			MongoDatabase dbIns = mongoInstance.getDB();
			MongoCollection<Document> coll = dbIns.getCollection(
					PropertiesManager.propertiesMap.get(prefix + Constants.TRANSACTION_STATUS_COLLECTION.getValue()));

			int totalfail = 0;
			int totalcancelled = 0;
			int totalinvalid = 0;
			int totalsucess = 0;
			int totalHits = 0;

			totalfail = (int) coll.countDocuments(allConditionQueryObjforfail);
			totalcancelled = (int) coll.countDocuments(allConditionQueryObjforCancelled);
			totalinvalid = (int) coll.countDocuments(allConditionQueryObjforInvalid);
			totalsucess = (int) coll.countDocuments(allConditionQueryObjforsucc);

			logger.info("Statistics All query end ");
			totalHits = totalsucess + totalfail + totalcancelled + totalinvalid;

			funnelChatData.setSuccessTxnCount((String.valueOf(totalsucess)));
			funnelChatData.setFailedTxnCount((String.valueOf(totalfail)));
			funnelChatData.setCancelledTxnCount((String.valueOf(totalcancelled)));
			funnelChatData.setInvalidTxnCount(String.valueOf(totalinvalid));

			funnelChatData.setTotalTxnCount(String.valueOf(totalHits));

		} catch (Exception exception) {
			logger.error("Exception", exception);

		} finally {

		}
		return funnelChatData;

	}

}