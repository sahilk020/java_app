package com.pay10.crm.action;

import java.math.BigDecimal;
import java.math.RoundingMode;
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
import org.springframework.stereotype.Component;

import com.mongodb.BasicDBObject;
import com.mongodb.BasicDBObjectBuilder;
import com.mongodb.client.AggregateIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import com.pay10.commons.dao.ServiceTaxDao;
import com.pay10.commons.exception.SystemException;
import com.pay10.commons.mongo.MongoInstance;
import com.pay10.commons.user.AccountCurrencyRegion;
import com.pay10.commons.user.CardHolderType;
import com.pay10.commons.user.DailyReportObject;
import com.pay10.commons.user.Merchants;
import com.pay10.commons.user.ServiceTax;
import com.pay10.commons.user.Surcharge;
import com.pay10.commons.user.SurchargeDao;
import com.pay10.commons.user.User;
import com.pay10.commons.user.UserDao;
import com.pay10.commons.util.AcquirerType;
import com.pay10.commons.util.Constants;
import com.pay10.commons.util.FieldType;
import com.pay10.commons.util.PropertiesManager;
import com.pay10.commons.util.StatusType;
import com.pay10.commons.util.TransactionType;

@Component
public class DailyReportQuery {

	@Autowired
	PropertiesManager propertiesManager;

	@Autowired
	private UserDao userDao;

	@Autowired
	private MongoInstance mongoInstance;

	@Autowired
	private SurchargeDao surchargeDao;

	@Autowired
	private ServiceTaxDao servicetaxDao;

	private static final String prefix = "MONGO_DB_";

	private static Logger logger = LoggerFactory.getLogger(DailyReportQuery.class.getName());

	public List<DailyReportObject> dailyReportDownload(String fromDate, String toDate, String segment, long roleId) throws SystemException {
		logger.info("inside dailyReportDownload method,DailyReportQuery Class");
		List<DailyReportObject> settledTransactionDataList = new ArrayList<DailyReportObject>();

		@SuppressWarnings("unchecked")
		//List<Merchants> userList = userDao.getActiveMerchantList();
		List<Merchants> userList = userDao.getActiveMerchantList(segment, roleId);
		
		Map<String,String> merchantPayIdNameMap = new HashMap<String,String>();
		
		for (Merchants user : userList) {
			merchantPayIdNameMap.put(user.getPayId(), user.getBusinessName().replace(" ", ""));
		}
		
		try {
			Map<String, User> userMap = new HashMap<String, User>();
			Map<String, List<ServiceTax>> serviceTaxMap = new HashMap<String, List<ServiceTax>>();

			List<Surcharge> surchargeList = new ArrayList<Surcharge>();
			SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.ENGLISH);
			try {

				Date date1 = format.parse(fromDate);
				Date date2 = format.parse(toDate);

				if (surchargeDao.findAllSurchargeByDate(date1, date2) == null) {
					logger.info("No surcharge data found");
				} else {
					surchargeList = surchargeDao.findAllSurchargeByDate(date1, date2);
				}

			} catch (Exception e) {
				logger.error("Exception 1 " + e);
			}

			BasicDBObject dateQuery = new BasicDBObject();
			BasicDBObject statusQuery = new BasicDBObject();
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

			statusQuery.put(FieldType.STATUS.getName(), StatusType.CAPTURED.getName());

			List<BasicDBObject> allConditionQueryList = new ArrayList<BasicDBObject>();
			if (!statusQuery.isEmpty()) {
				allConditionQueryList.add(statusQuery);
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
			logger.info("processing finalquery for daily capture data = " + finalquery);
			MongoDatabase dbIns = mongoInstance.getDB();
			MongoCollection<Document> coll = dbIns
					.getCollection(PropertiesManager.propertiesMap.get(prefix + Constants.COLLECTION_NAME.getValue()));
			BasicDBObject match = new BasicDBObject("$match", finalquery);

			List<BasicDBObject> pipeline = Arrays.asList(match);
			AggregateIterable<Document> output = coll.aggregate(pipeline);
			output.allowDiskUse(true);
			MongoCursor<Document> cursor = output.iterator();

			logger.info("processed finalquery for daily capture data = " + finalquery);

			while (cursor.hasNext()) {
				Document doc = cursor.next();

				if (!StringUtils.isBlank(doc.getString(FieldType.SURCHARGE_FLAG.getName()))
						&& doc.getString(FieldType.SURCHARGE_FLAG.getName()).equalsIgnoreCase("Y")) {
					DailyReportObject dailyReportObject = new DailyReportObject();

					dailyReportObject.setPgRefNum(doc.getString(FieldType.PG_REF_NUM.getName()));
					dailyReportObject.setPaymentMethod(doc.getString(FieldType.PAYMENT_TYPE.getName()));
					dailyReportObject.setMopType(doc.getString(FieldType.MOP_TYPE.getName()));
					dailyReportObject.setOrderId(doc.getString(FieldType.ORDER_ID.getName()));
					dailyReportObject.setBusinessName(merchantPayIdNameMap.get(doc.getString(FieldType.PAY_ID.getName())));
					dailyReportObject.setCurrency(doc.getString(FieldType.CURRENCY_CODE.getName()));
					dailyReportObject.setTxnType(doc.getString(FieldType.TXNTYPE.getName()));
					dailyReportObject.setCaptureDate(doc.getString(FieldType.CREATE_DATE.getName()));
					dailyReportObject.setTransactionRegion(AccountCurrencyRegion.DOMESTIC.name());
					dailyReportObject.setCardHolderType(doc.getString(FieldType.CARD_HOLDER_TYPE.getName()));
					dailyReportObject.setAcquirer(doc.getString(FieldType.ACQUIRER_TYPE.getName()));
					dailyReportObject.setTotalAmount(doc.getString(FieldType.TOTAL_AMOUNT.getName()));
					dailyReportObject.setMerchantAmount(doc.getString(FieldType.AMOUNT.getName()));
					if (StringUtils.isBlank(doc.getString(FieldType.RRN.getName()))) {
						dailyReportObject.setRrn("");
					} else {
						dailyReportObject.setRrn(doc.getString(FieldType.RRN.getName()));
					}

					if (StringUtils.isBlank(doc.getString(FieldType.ACQ_ID.getName()))) {
						dailyReportObject.setAcqId("");
					} else {
						dailyReportObject.setAcqId(doc.getString(FieldType.ACQ_ID.getName()));
					}

					if (StringUtils.isBlank(doc.getString(FieldType.POST_SETTLED_FLAG.getName()))) {
						dailyReportObject.setPostSettledFlag("");
					} else {
						dailyReportObject.setPostSettledFlag(doc.getString(FieldType.POST_SETTLED_FLAG.getName()));
					}

					if (StringUtils.isBlank(doc.getString(FieldType.REFUND_ORDER_ID.getName()))) {
						dailyReportObject.setRefundOrderId("");
					} else {
						dailyReportObject.setRefundOrderId(doc.getString(FieldType.REFUND_ORDER_ID.getName()));
					}

					if (StringUtils.isBlank(doc.getString(FieldType.REFUND_FLAG.getName()))) {
						dailyReportObject.setRefundFlag("");
					} else {
						dailyReportObject.setRefundFlag(doc.getString(FieldType.REFUND_FLAG.getName()));
					}

					BigDecimal amount = new BigDecimal(doc.getString(FieldType.AMOUNT.getName()));
					BigDecimal totalAmount = new BigDecimal(doc.getString(FieldType.TOTAL_AMOUNT.getName()));
					BigDecimal totalSurcharge = totalAmount.subtract(amount);

					if (totalAmount.equals(amount)) {

						dailyReportObject.setSurchargeAcquirer("0.00");
						dailyReportObject.setGstAcq("0.00");

						dailyReportObject.setSurchargeIpay("0.00");
						dailyReportObject.setGstIpay("0.00");

					} else {
						String payId = doc.getString(FieldType.PAY_ID.getName());
						if (StringUtils.isBlank(payId)) {
							logger.info("Pay Id not present for " + doc.getString(FieldType.TXN_ID.getName()));
							continue;
						}

						User user = null;
						if (userMap.get(payId) != null && !userMap.get(payId).getPayId().equals("")) {
							user = userMap.get(payId);
							
						} else {
							user = userDao.findPayId(payId);
							userMap.put(payId, user);
						}
						
						if (user == null) {
							logger.info("No user found for pg ref num "+ doc.getString(FieldType.PG_REF_NUM.getName()));
							continue;
						}
						
						BigDecimal st = null;

						List<ServiceTax> serviceTaxList = null;
						if (serviceTaxMap.get(user.getIndustryCategory()) != null
								&& serviceTaxMap.get(user.getIndustryCategory()).size() > 0) {

							serviceTaxList = serviceTaxMap.get(user.getIndustryCategory());
						} else {
							serviceTaxList = servicetaxDao.findServiceTaxForReportWithoutDate(
									user.getIndustryCategory(), doc.getString(FieldType.CREATE_DATE.getName()));
							serviceTaxMap.put(user.getIndustryCategory(), serviceTaxList);
						}

						for (ServiceTax serviceTax : serviceTaxList) {
							st = serviceTax.getServiceTax();
							st = st.setScale(2, RoundingMode.HALF_DOWN);
						}

						Date surchargeStartDate = null;
						Date surchargeEndDate = null;
						Date settlementDate = null;
						Surcharge surcharge = new Surcharge();

						try {
							for (Surcharge surchargeData : surchargeList) {

								if (AcquirerType.getInstancefromName(surchargeData.getAcquirerName()).getCode()
										.equalsIgnoreCase(dailyReportObject.getAcquirer())
										&& surchargeData.getPaymentType().getCode()
												.equalsIgnoreCase(dailyReportObject.getPaymentMethod())
										&& surchargeData.getMopType().getCode()
												.equalsIgnoreCase(dailyReportObject.getMopType())
										&& surchargeData.getPaymentsRegion().name()
												.equalsIgnoreCase(dailyReportObject.getTransactionRegion())
										&& surchargeData.getPayId().equalsIgnoreCase(payId)) {

									surchargeStartDate = format.parse(surchargeData.getCreatedDate().toString());
									surchargeEndDate = format.parse(surchargeData.getUpdatedDate().toString());
									if (surchargeStartDate.compareTo(surchargeEndDate) == 0) {
										surchargeEndDate = new Date();
									}

									settlementDate = format.parse(doc.getString(FieldType.CREATE_DATE.getName()));

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

						if (surcharge.getBankSurchargeAmountCustomer() == null
								|| surcharge.getBankSurchargePercentageCustomer() == null
								|| surcharge.getBankSurchargeAmountCommercial() == null
								|| surcharge.getBankSurchargePercentageCommercial() == null) {

							logger.info("Surcharge is null for payId = " + payId + " acquirer = "
									+ doc.getString(FieldType.ACQUIRER_TYPE.getName()) + " mop = "
									+ doc.getString(FieldType.MOP_TYPE.getName()) + "  paymentType = "
									+ doc.getString(FieldType.PAYMENT_TYPE.getName()) + "  paymentRegion = "
									+ doc.getString(FieldType.PAYMENTS_REGION.getName()) + "  date = "
									+ doc.getString(FieldType.CREATE_DATE.getName()));
							continue;
						}

						BigDecimal bankFixCharge = BigDecimal.ZERO;
						BigDecimal bankChargePr = BigDecimal.ZERO;

						if (dailyReportObject.getCardHolderType().equals(CardHolderType.COMMERCIAL.toString())) {

							bankFixCharge = surcharge.getBankSurchargeAmountCommercial();
							bankChargePr = surcharge.getBankSurchargePercentageCommercial();
						} else {
							bankFixCharge = surcharge.getBankSurchargeAmountCustomer();
							bankChargePr = surcharge.getBankSurchargePercentageCustomer();
						}

						BigDecimal acquirerSurcharge = amount.multiply(bankChargePr.divide(BigDecimal.valueOf(100)));
						acquirerSurcharge = acquirerSurcharge.add(bankFixCharge).setScale(2, RoundingMode.HALF_DOWN);
						BigDecimal acquirerGst = acquirerSurcharge.multiply(st.divide(BigDecimal.valueOf(100)))
								.setScale(2, RoundingMode.HALF_DOWN);
						BigDecimal totalAcquirerSurcharge = acquirerSurcharge.add(acquirerGst);

						BigDecimal totalPgSurcharge = totalSurcharge.subtract(totalAcquirerSurcharge);
						BigDecimal divisor = new BigDecimal("1")
								.add(st.divide(new BigDecimal("100"), 2, RoundingMode.HALF_DOWN));

						BigDecimal pgSurcharge = totalPgSurcharge.divide(divisor, 2, RoundingMode.HALF_DOWN);
						BigDecimal pgGst = totalPgSurcharge.subtract(pgSurcharge);

						dailyReportObject.setSurchargeAcquirer(String.valueOf(acquirerSurcharge.doubleValue()));
						dailyReportObject.setGstAcq(String.valueOf(acquirerGst.doubleValue()));

						dailyReportObject.setSurchargeIpay(String.valueOf(pgSurcharge.doubleValue()));
						dailyReportObject.setGstIpay(String.valueOf(pgGst.doubleValue()));

					}

					settledTransactionDataList.add(dailyReportObject);
				}

				else {
					logger.info("No surcharge based transaction for date range " + fromDate + " TO " + toDate);
				}
			}

			logger.info("Found " + settledTransactionDataList.size()
					+ " transactions in iPayTransactions for date range " + fromDate + " TO " + toDate);

			return settledTransactionDataList;
		} catch (Exception e) {
			logger.error("Exception occured " + e);
		}
		return settledTransactionDataList;
	}

	/*
	 * public List<DailyReportObject> dailyReportDownload(String fromDate, String
	 * toDate) throws SystemException {
	 * logger.info("inside dailyReportDownload method,DailyReportQuery Class");
	 * List<DailyReportObject> transactionList = new ArrayList<DailyReportObject>();
	 * List<Merchants> merchantsList = new ArrayList<Merchants>(); Map<String,
	 * String> mapBusinessName = new HashMap<String, String>(); merchantsList =
	 * userDao.getActiveMerchantList(); for (Merchants merchants : merchantsList) {
	 * mapBusinessName.put(merchants.getPayId(), merchants.getBusinessName()); } try
	 * { BasicDBObject dateQuery = new BasicDBObject(); BasicDBObject statusQuery =
	 * new BasicDBObject(); BasicDBObject andQuery = new BasicDBObject();
	 * List<BasicDBObject> obj = new ArrayList<BasicDBObject>();
	 * 
	 * if (!fromDate.isEmpty()) {
	 * 
	 * String currentDate = null; if (!toDate.isEmpty()) { currentDate = toDate; }
	 * else { DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	 * Calendar cal = Calendar.getInstance(); currentDate =
	 * dateFormat.format(cal.getTime()); }
	 * 
	 * dateQuery.put(FieldType.CREATE_DATE.getName(),
	 * BasicDBObjectBuilder.start("$gte", new
	 * SimpleDateFormat(fromDate).toLocalizedPattern()) .add("$lte", new
	 * SimpleDateFormat(currentDate).toLocalizedPattern()).get()); }
	 * 
	 * if (!dateQuery.isEmpty()) { obj.add(dateQuery); }
	 * 
	 * andQuery.put("$and", obj);
	 * 
	 * MongoDatabase dbIns = mongoInstance.getDB(); MongoCollection<Document> coll =
	 * dbIns.getCollection( propertiesManager.propertiesMap.get(prefix +
	 * Constants.COLLECTION_NAME.getValue())); MongoCursor<Document> cursor =
	 * coll.find(andQuery).iterator();
	 * 
	 * while (cursor.hasNext()) { Document doc = cursor.next();
	 * 
	 * DailyReportObject report = new DailyReportObject();
	 * report.setPgRefNum(doc.getString(FieldType.PG_REF_NUM.toString()));
	 * report.setPaymentMethod(doc.getString(FieldType.PAYMENT_TYPE.toString()));
	 * report.setMopType(doc.getString(FieldType.MOP_TYPE.toString()));
	 * report.setOrderId(doc.getString(FieldType.ORDER_ID.toString()));
	 * report.setCurrency(Currency.getAlphabaticCode(doc.getString(FieldType.
	 * CURRENCY_CODE.toString())));
	 * 
	 * 
	 * if (doc.getString(FieldType.TXNTYPE.toString()).equals("RECO")) {
	 * report.setTxnType(TransactionType.SALE.getName()); } else {
	 * report.setTxnType(TransactionType.REFUND.getName()); }
	 * report.setBusinessName(mapBusinessName.get(doc.getString(FieldType.PAY_ID.
	 * toString())));
	 * report.setCaptureDate(DateCreater.formatDateTransaction(doc.getString(
	 * FieldType.PG_DATE_TIME.toString())));
	 * report.setTransactionRegion(doc.getString(FieldType.PAYMENTS_REGION.toString(
	 * )));
	 * report.setCardHolderType(doc.getString(FieldType.CARD_HOLDER_TYPE.toString())
	 * ); report.setAcquirer(doc.getString(FieldType.ACQUIRER_TYPE.toString()));
	 * report.setTotalAmount(String.valueOf(doc.getDouble(FieldType.TOTAL_AMOUNT.
	 * toString())));
	 * report.setSurchargeAcquirer(String.valueOf(doc.getDouble(FieldType.
	 * SURCHARGE_ACQ.toString())));
	 * report.setSurchargeIpay(String.valueOf(doc.getDouble(FieldType.SURCHARGE_IPAY
	 * .toString())));
	 * report.setGstAcq(String.valueOf(doc.getDouble(FieldType.GST_ACQ.toString())))
	 * ;
	 * report.setGstIpay(String.valueOf(doc.getDouble(FieldType.GST_IPAY.toString())
	 * )); report.setMerchantAmount(String.valueOf(doc.getDouble(FieldType.AMOUNT.
	 * toString()))); report.setAcqId(doc.getString(FieldType.ACQ_ID.toString()));
	 * report.setRrn(doc.getString(FieldType.RRN.toString()));
	 * report.setPostSettledFlag(doc.getString(FieldType.POST_SETTLED_FLAG.toString(
	 * ))); report.setRefundFlag(doc.getString(FieldType.REFUND_FLAG.toString()));
	 * report.setRefundOrderId(doc.getString(FieldType.REFUND_ORDER_ID.toString()));
	 * transactionList.add(report); }
	 * 
	 * return transactionList; } catch (Exception e) {
	 * logger.error("Exception occured Search , Exception = " + e); return null; } }
	 */

	public boolean dailyReportCount(String fromCaptureDate, String toCaptureDate) {
		logger.info("inside dailyReportCount method,DailyReportQuery Class");
		try {

			BasicDBObject dateQuery = new BasicDBObject();
			BasicDBObject settleMentQuery = new BasicDBObject();
			BasicDBObject andQuery = new BasicDBObject();
			BasicDBObject andSettleQuery = new BasicDBObject();
			List<BasicDBObject> objCaptured = new ArrayList<BasicDBObject>();
			List<BasicDBObject> objSettled = new ArrayList<BasicDBObject>();

			List<BasicDBObject> saleCapturedList = new ArrayList<BasicDBObject>();
			List<BasicDBObject> refundCapturedList = new ArrayList<BasicDBObject>();

			if (!fromCaptureDate.isEmpty()) {

				String currentDate = null;
				if (!toCaptureDate.isEmpty()) {
					currentDate = toCaptureDate;
				} else {
					DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
					Calendar cal = Calendar.getInstance();
					currentDate = dateFormat.format(cal.getTime());
				}

				dateQuery.put(FieldType.CREATE_DATE.getName(),
						BasicDBObjectBuilder.start("$gte", new SimpleDateFormat(fromCaptureDate).toLocalizedPattern())
								.add("$lte", new SimpleDateFormat(currentDate).toLocalizedPattern()).get());
			}

			// Sale Captured query

			List<BasicDBObject> saleCapturedConditionList = new ArrayList<BasicDBObject>();
			saleCapturedConditionList
					.add(new BasicDBObject(FieldType.TXNTYPE.getName(), TransactionType.SALE.getName()));
			saleCapturedConditionList.add(new BasicDBObject(FieldType.STATUS.getName(), StatusType.CAPTURED.getName()));

			BasicDBObject saleConditionQuery = new BasicDBObject("$and", saleCapturedConditionList);
			saleCapturedList.add(saleConditionQuery);

			// REFUND Captured query
			List<BasicDBObject> refundCapturedConditionList = new ArrayList<BasicDBObject>();
			refundCapturedConditionList
					.add(new BasicDBObject(FieldType.TXNTYPE.getName(), TransactionType.REFUND.getName()));
			refundCapturedConditionList
					.add(new BasicDBObject(FieldType.STATUS.getName(), StatusType.CAPTURED.getName()));

			BasicDBObject refundConditionQuery = new BasicDBObject("$and", refundCapturedConditionList);
			refundCapturedList.add(refundConditionQuery);

			List<BasicDBObject> transList = new ArrayList<BasicDBObject>();
			transList.add(saleConditionQuery);
			transList.add(refundConditionQuery);
			BasicDBObject transQuery = new BasicDBObject("$or", transList);

			if (!dateQuery.isEmpty()) {
				objCaptured.add(dateQuery);
			}

			if (!transQuery.isEmpty()) {
				objCaptured.add(transQuery);
			}
			andQuery.put("$and", objCaptured);
			MongoDatabase dbIns = mongoInstance.getDB();
			MongoCollection<Document> coll = dbIns
					.getCollection(PropertiesManager.propertiesMap.get(prefix + Constants.COLLECTION_NAME.getValue()));
			long capturedCount = coll.count(andQuery);

			logger.info("inside dailyReportCount get Captured Count" + capturedCount);

			if (!fromCaptureDate.isEmpty()) {

				String currentDate = null;
				if (!toCaptureDate.isEmpty()) {
					currentDate = toCaptureDate;
				} else {
					DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
					Calendar cal = Calendar.getInstance();
					currentDate = dateFormat.format(cal.getTime());
				}

				settleMentQuery.put(FieldType.PG_DATE_TIME.getName(),
						BasicDBObjectBuilder.start("$gte", new SimpleDateFormat(fromCaptureDate).toLocalizedPattern())
								.add("$lte", new SimpleDateFormat(currentDate).toLocalizedPattern()).get());
			}

			if (!settleMentQuery.isEmpty()) {
				objSettled.add(settleMentQuery);
			}
			andSettleQuery.put("$and", objSettled);

			coll = dbIns.getCollection(
					PropertiesManager.propertiesMap.get(prefix + Constants.SETTLED_TRANSACTIONS_NAME.getValue()));

			long settledCount = coll.count(andSettleQuery);

			logger.info("inside dailyReportCount get Total Settled Count" + settledCount);

			if (settledCount >= capturedCount) {
				return true;
			} else {
				return false;
			}

		} catch (Exception e) {
			logger.error("Exception occured Search , Exception = " + e);
			return false;
		}
	}

}
