package com.pay10.commons.util;

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
import com.pay10.commons.dao.ServiceTaxDao;
import com.pay10.commons.dao.SettledTransactionDataDao;
import com.pay10.commons.mongo.MongoInstance;
import com.pay10.commons.user.AccountCurrencyRegion;
import com.pay10.commons.user.CardHolderType;
import com.pay10.commons.user.ServiceTax;
import com.pay10.commons.user.SettledTransactionDataObject;
import com.pay10.commons.user.Surcharge;
import com.pay10.commons.user.SurchargeDao;
import com.pay10.commons.user.User;
import com.pay10.commons.user.UserDao;

@Service
public class SettledTransactionDataService {

	@Autowired
	private MongoInstance mongoInstance;

	@Autowired
	private UserDao userdao;

	@Autowired
	private SettledTransactionDataDao settledTransactionDataDao;

	@Autowired
	private ServiceTaxDao serviceTaxDao;

	@Autowired
	private SurchargeDao surchargeDao;

	@Autowired
	PropertiesManager propertiesManager;
	private static Logger logger = LoggerFactory.getLogger(SettledTransactionDataService.class.getName());
	private static final String prefix = "MONGO_DB_";

	public void uploadSettledData(String fromDate, String toDate) {

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
				}
				else {
					surchargeList = surchargeDao.findAllSurchargeByDate(date1, date2);
				}
				
			} catch (Exception e) {
				logger.error("Exception 1 " + e);
			}

			List<SettledTransactionDataObject> settledTransactionDataList = new ArrayList<SettledTransactionDataObject>();

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
                           dateIndexConditionList.add(new BasicDBObject("DATE_INDEX",dateIndex));
            }
            
            if (StringUtils.isNotBlank(PropertiesManager.propertiesMap.get(Constants.USE_DATE_INDEX.getValue())) && 
            		PropertiesManager.propertiesMap.get(Constants.USE_DATE_INDEX.getValue()).equalsIgnoreCase("Y")) {
            	 dateIndexConditionQuery.append("$or", dateIndexConditionList);
            }
           
			

			List<BasicDBObject> saleConditionList = new ArrayList<BasicDBObject>();
			saleConditionList.add(new BasicDBObject(FieldType.TXNTYPE.getName(), TransactionType.SALE.getName()));
			saleConditionList.add(new BasicDBObject(FieldType.STATUS.getName(), StatusType.SETTLED_SETTLE.getName()));

			BasicDBObject saleConditionQuery = new BasicDBObject("$and", saleConditionList);


			List<BasicDBObject> recoConditionList = new ArrayList<BasicDBObject>();
			recoConditionList.add(new BasicDBObject(FieldType.TXNTYPE.getName(), TransactionType.RECO.getName()));
			recoConditionList.add(new BasicDBObject(FieldType.STATUS.getName(), StatusType.SETTLED_SETTLE.getName()));

			BasicDBObject recoConditionQuery = new BasicDBObject("$and", recoConditionList);


			List<BasicDBObject> recoRefundConditionList = new ArrayList<BasicDBObject>();
			recoRefundConditionList
					.add(new BasicDBObject(FieldType.TXNTYPE.getName(), TransactionType.REFUNDRECO.getName()));
			recoRefundConditionList.add(new BasicDBObject(FieldType.STATUS.getName(), StatusType.SETTLED_SETTLE.getName()));

			BasicDBObject recoRefundConditionQuery = new BasicDBObject("$and", recoRefundConditionList);

			saleOrAuthList.add(saleConditionQuery);
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
			if (!authndSaleConditionQuery.isEmpty()) {
				allConditionQueryList.add(authndSaleConditionQuery);
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
			logger.info("finalquery for settlement data upload = " + finalquery);
			MongoDatabase dbIns = mongoInstance.getDB();
			MongoCollection<Document> coll = dbIns
					.getCollection(PropertiesManager.propertiesMap.get(prefix + Constants.COLLECTION_NAME.getValue()));
			BasicDBObject match = new BasicDBObject("$match", finalquery);

			BasicDBObject sort = new BasicDBObject("$sort", new BasicDBObject("CREATE_DATE", -1));

			List<BasicDBObject> pipeline = Arrays.asList(match, sort);
			AggregateIterable<Document> output = coll.aggregate(pipeline);
			output.allowDiskUse(true);
			MongoCursor<Document> cursor = output.iterator();

			while (cursor.hasNext()) {
				Document doc = cursor.next();
				
				if (!StringUtils.isBlank(doc.getString(FieldType.SURCHARGE_FLAG.getName()))
						&& doc.getString(FieldType.SURCHARGE_FLAG.getName()).equalsIgnoreCase("Y")) {
					SettledTransactionDataObject settledTransactionData = new SettledTransactionDataObject();

					settledTransactionData.set_id(doc.getString(FieldType.TXN_ID.getName()));
					settledTransactionData.setTransactionId(doc.getString(FieldType.TXN_ID.getName()));
					settledTransactionData.setPgRefNum(doc.getString(FieldType.PG_REF_NUM.getName()));
					
					if (StringUtils.isBlank(doc.getString(FieldType.PAYMENTS_REGION.getName()))) {
						settledTransactionData.setTransactionRegion(AccountCurrencyRegion.DOMESTIC.name());
					}
					else {
						settledTransactionData.setTransactionRegion(doc.getString(FieldType.PAYMENTS_REGION.getName()));
					}
					
					if (StringUtils.isBlank(doc.getString(FieldType.POST_SETTLED_FLAG.getName()))) {
						settledTransactionData.setPostSettledFlag("");
					}
					else {
						settledTransactionData.setPostSettledFlag(doc.getString(FieldType.POST_SETTLED_FLAG.getName()));
					}
					
					if (StringUtils.isBlank(doc.getString(FieldType.REFUND_ORDER_ID.getName()))) {
						settledTransactionData.setRefundOrderId("");
					}
					else {
						settledTransactionData.setRefundOrderId(doc.getString(FieldType.REFUND_ORDER_ID.getName()));
					}
					settledTransactionData.setTxnType(doc.getString(FieldType.TXNTYPE.getName()));
					settledTransactionData.setAcquirerType(doc.getString(FieldType.ACQUIRER_TYPE.getName()));
					settledTransactionData.setPaymentMethods(doc.getString(FieldType.PAYMENT_TYPE.getName()));
					settledTransactionData.setCreateDate(doc.getString(FieldType.CREATE_DATE.getName()));
					settledTransactionData.setOrderId(doc.getString(FieldType.ORDER_ID.getName()));
					settledTransactionData.setPayId(doc.getString(FieldType.PAY_ID.getName()));
					settledTransactionData.setMopType(doc.getString(FieldType.MOP_TYPE.getName()));
					settledTransactionData.setCurrency(doc.getString(FieldType.CURRENCY_CODE.getName()));
					settledTransactionData.setStatus(doc.getString(FieldType.STATUS.getName()));
					
					if (StringUtils.isBlank(doc.getString(FieldType.CARD_HOLDER_TYPE.getName()))) {
						settledTransactionData.setCardHolderType(CardHolderType.CONSUMER.name());
					}
					else {
						settledTransactionData.setCardHolderType(doc.getString(FieldType.CARD_HOLDER_TYPE.getName()));
					}
					
					if (StringUtils.isBlank(doc.getString(FieldType.PG_DATE_TIME.getName()))) {
						settledTransactionData.setCaptureDate("");
					}
					else {
						settledTransactionData.setCaptureDate(doc.getString(FieldType.PG_DATE_TIME.getName()));
					}
					
					if (StringUtils.isBlank(doc.getString(FieldType.RRN.getName()))) {
						settledTransactionData.setRrn("");
					}
					else {
						settledTransactionData.setRrn(doc.getString(FieldType.RRN.getName()));
					}
					
					settledTransactionData.setAcqId(doc.getString(FieldType.ACQ_ID.getName()));
					
					if (StringUtils.isBlank(doc.getString(FieldType.ARN.getName()))) {
						settledTransactionData.setArn("");
					}
					else {
						settledTransactionData.setArn(doc.getString(FieldType.ARN.getName()));
					}
					
					if (StringUtils.isBlank(doc.getString(FieldType.UDF6.getName()))) {
						settledTransactionData.setDeltaRefundFlag("");
					}
					else {
						settledTransactionData.setDeltaRefundFlag(doc.getString(FieldType.UDF6.getName()));
					}
					
					settledTransactionData.setSurchargeFlag(doc.getString(FieldType.SURCHARGE_FLAG.getName()));
					
					if (StringUtils.isBlank(doc.getString(FieldType.REFUND_FLAG.getName()))) {
						settledTransactionData.setRefundFlag("");
					}
					else {
						settledTransactionData.setRefundFlag(doc.getString(FieldType.REFUND_FLAG.getName()));
					}
					
					settledTransactionData.setAmount(Double.valueOf(doc.getString(FieldType.AMOUNT.getName())));
					settledTransactionData
							.setTotalAmount(Double.valueOf(doc.getString(FieldType.TOTAL_AMOUNT.getName())));

					BigDecimal amount = new BigDecimal(doc.getString(FieldType.AMOUNT.getName()));
					BigDecimal totalAmount = new BigDecimal(doc.getString(FieldType.TOTAL_AMOUNT.getName()));
					BigDecimal totalSurcharge = totalAmount.subtract(amount);

					if (totalAmount.equals(amount)) {

						settledTransactionData.setTdrScAcquirer(Double.valueOf(0));
						settledTransactionData.setGstScAcquirer(Double.valueOf(0));

						settledTransactionData.setTdrScIpay(Double.valueOf(0));
						settledTransactionData.setGstScIpay(Double.valueOf(0));

						settledTransactionData.setTdrScMmad(Double.valueOf(0));
						settledTransactionData.setGstScMmad(Double.valueOf(0));

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
							user = userdao.findPayId(payId);
							userMap.put(payId, user);
						}

						if (user == null) {
							logger.info("User not found for txn Id " + doc.getString(FieldType.TXN_ID.getName()));
							continue;
						}

						BigDecimal st = null;

						List<ServiceTax> serviceTaxList = null;
						if (serviceTaxMap.get(user.getIndustryCategory()) != null
								&& serviceTaxMap.get(user.getIndustryCategory()).size() > 0) {

							serviceTaxList = serviceTaxMap.get(user.getIndustryCategory());
						} else {
							serviceTaxList = serviceTaxDao.findServiceTaxForReportWithoutDate(
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
										.equalsIgnoreCase(settledTransactionData.getAcquirerType())
										&& surchargeData.getPaymentType().getCode()
												.equalsIgnoreCase(settledTransactionData.getPaymentMethods())
										&& surchargeData.getMopType().getCode()
												.equalsIgnoreCase(settledTransactionData.getMopType())
										&& surchargeData.getPaymentsRegion().name()
												.equalsIgnoreCase(settledTransactionData.getTransactionRegion())
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

						if (settledTransactionData.getCardHolderType()
								.equals(CardHolderType.COMMERCIAL.toString())) {

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
						BigDecimal divisor2 = new BigDecimal("2");

						BigDecimal pgSurcharge = totalPgSurcharge.divide(divisor, 2, RoundingMode.HALF_DOWN);
						BigDecimal pgGst = totalPgSurcharge.subtract(pgSurcharge);

						pgGst = pgGst.divide(divisor2, 2, RoundingMode.HALF_DOWN);
						pgSurcharge = pgSurcharge.divide(divisor2, 2, RoundingMode.HALF_DOWN);

						settledTransactionData.setTdrScAcquirer(acquirerSurcharge.doubleValue());
						settledTransactionData.setGstScAcquirer(acquirerGst.doubleValue());

						settledTransactionData.setTdrScIpay(pgSurcharge.doubleValue());
						settledTransactionData.setGstScIpay(pgGst.doubleValue());

						settledTransactionData.setTdrScMmad(pgSurcharge.doubleValue());
						settledTransactionData.setGstScMmad(pgGst.doubleValue());
					}

					settledTransactionDataList.add(settledTransactionData);
				}

				else {
					logger.info("No surcharge based transaction for date range "+fromDate+" TO "+toDate);
				}
			}
			logger.info("Found "+settledTransactionDataList.size()+" transactions in iPayTransactions for date range "+fromDate+" TO "+toDate);

			settledTransactionDataDao.insertTransaction(settledTransactionDataList);
		} catch (Exception e) {
			logger.error("Exception occured " + e);
		}
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
                           dateIndexConditionList.add(new BasicDBObject("DATE_INDEX",dateIndex));
            }
            
            if (StringUtils.isNotBlank(PropertiesManager.propertiesMap.get(Constants.USE_DATE_INDEX.getValue())) && 
            		PropertiesManager.propertiesMap.get(Constants.USE_DATE_INDEX.getValue()).equalsIgnoreCase("Y")) {
            	 dateIndexConditionQuery.append("$or", dateIndexConditionList);
            }

			List<BasicDBObject> recoConditionList = new ArrayList<BasicDBObject>();
			recoConditionList.add(new BasicDBObject(FieldType.STATUS.getName(), StatusType.SETTLED_SETTLE.getName()));

			BasicDBObject recoConditionQuery = new BasicDBObject("$and", recoConditionList);

			saleOrAuthList.add(recoConditionQuery);

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
			if (!authndSaleConditionQuery.isEmpty()) {
				allConditionQueryList.add(authndSaleConditionQuery);
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
			logger.info("finalquery for settlement data update = " + finalquery);
			MongoDatabase dbIns = mongoInstance.getDB();
			MongoCollection<Document> coll = dbIns
					.getCollection(PropertiesManager.propertiesMap.get(prefix + Constants.COLLECTION_NAME.getValue()));
			
			MongoCollection<Document> summaryColl = dbIns
					.getCollection(PropertiesManager.propertiesMap.get(prefix + Constants.SUMMARY_TRANSACTIONS_NAME.getValue()));
			
			MongoCollection<Document> duplicateColl = dbIns
					.getCollection(PropertiesManager.propertiesMap.get(prefix + Constants.DUPLICATE_SETTLEMENT_NAME.getValue()));
			
			BasicDBObject match = new BasicDBObject("$match", finalquery);
			List<BasicDBObject> pipeline = Arrays.asList(match);
			AggregateIterable<Document> output = coll.aggregate(pipeline);
			output.allowDiskUse(true);
			MongoCursor<Document> cursor = output.iterator();
			int updateCount =0;
			ArrayList<String> pgRefList = new ArrayList<String>(); 
			int counter = 0;
			int totalCount = 0;
			
			while (cursor.hasNext()) {
				counter = counter +1;
				totalCount = totalCount +1;
				if (counter == 10000) {
					logger.info("Transactions updated == " +totalCount);
				}
				Document doc = cursor.next();

				if (pgRefList.contains(doc.getString(FieldType.PG_REF_NUM.getName())+doc.getString(FieldType.ACQ_ID.getName()))) {
					
					logger.info("Duplicate settlement found for PG_REF_NUM : "+doc.getString(FieldType.PG_REF_NUM.getName())+"  ACQ_ID : "+doc.getString(FieldType.ACQ_ID.getName()));
					doc.put(FieldType.RESPONSE_MESSAGE.getName(), "Duplicate settlement found with this PG_REF_NUM and ACQ_ID");
					doc.put("_id", TransactionManager.getNewTransactionId());
					duplicateColl.insertOne(doc);
					
				}
				pgRefList.add(doc.getString(FieldType.PG_REF_NUM.getName())+doc.getString(FieldType.ACQ_ID.getName()));

				List<BasicDBObject> updateConditionList = new ArrayList<BasicDBObject>();
				updateConditionList.add(new BasicDBObject(FieldType.PG_REF_NUM.getName(), doc.getString(FieldType.PG_REF_NUM.getName())));
				updateConditionList.add(new BasicDBObject(FieldType.ACQ_ID.getName(), doc.getString(FieldType.ACQ_ID.getName())));

				BasicDBObject searchQuery = new BasicDBObject("$and", updateConditionList);
				
				long recordsCount = summaryColl.count(searchQuery);
				
				if (recordsCount == 0) {
					logger.info("No Records found for PG_REF_NUM : "+doc.getString(FieldType.PG_REF_NUM.getName())+"  ACQ_ID : "+doc.getString(FieldType.ACQ_ID.getName()));
					doc.put(FieldType.RESPONSE_MESSAGE.getName(), "No Record found with this PG_REF_NUM and ACQ_ID");
					doc.put("_id", TransactionManager.getNewTransactionId());
					duplicateColl.insertOne(doc);
				}
				else if (recordsCount > 1 ) {
					logger.info("Update Settled Data , found more than one record for PG_REF_NUM : " + doc.getString(FieldType.PG_REF_NUM.getName())
					+"  ACQ_ID : "+doc.getString(FieldType.ACQ_ID.getName()));
					doc.put(FieldType.RESPONSE_MESSAGE.getName(), "More than one Record found with this PG_REF_NUM and ACQ_ID");
					doc.put("_id", TransactionManager.getNewTransactionId());
					duplicateColl.insertOne(doc);
				}
				else {
					List<BasicDBObject> updateCollConditionList = new ArrayList<BasicDBObject>();
					updateCollConditionList.add(new BasicDBObject(FieldType.PG_REF_NUM.getName(), doc.getString(FieldType.PG_REF_NUM.getName())));
					updateCollConditionList.add(new BasicDBObject(FieldType.ACQ_ID.getName(), doc.getString(FieldType.ACQ_ID.getName())));

					BasicDBObject searchCollQuery = new BasicDBObject("$and", updateCollConditionList);
					Document docUpdate = new Document();
					docUpdate.append("$set", new BasicDBObject().append(FieldType.SETTLEMENT_DATE.getName(), doc.getString(FieldType.CREATE_DATE.getName())));
					summaryColl.updateOne(searchCollQuery, docUpdate);
					updateCount ++;
				}
			}
			logger.info("Found "+updateCount+" transactions in iPayTransactions for date range "+fromDate+" TO "+toDate);
		} catch (Exception e) {
			logger.error("Exception occured " + e);
		}
	}
	
}
