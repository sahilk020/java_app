package com.pay10.crm.action;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;

import org.apache.commons.lang3.StringUtils;
import org.bson.BsonDocument;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.hibernate.Session;
import org.hibernate.query.Query;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.google.gson.Gson;
import com.mongodb.BasicDBObject;
import com.mongodb.BasicDBObjectBuilder;
import com.mongodb.MongoClient;
import com.mongodb.client.AggregateIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import com.pay10.commons.dao.HibernateSessionProvider;
import com.pay10.commons.dao.ServiceTaxDao;
import com.pay10.commons.mongo.MongoInstance;
import com.pay10.commons.user.AccountCurrencyRegion;
import com.pay10.commons.user.BankSettle;
import com.pay10.commons.user.BankSettleDao;
import com.pay10.commons.user.CompanyProfile;
import com.pay10.commons.user.MISReportObject;
import com.pay10.commons.user.Resellercommision;
import com.pay10.commons.user.User;
import com.pay10.commons.user.UserDao;
import com.pay10.commons.user.UserType;
import com.pay10.commons.user.resellercommisiondao;
import com.pay10.commons.util.AcquirerType;
import com.pay10.commons.util.Constants;
import com.pay10.commons.util.FieldType;
import com.pay10.commons.util.MopType;
import com.pay10.commons.util.PaymentType;
import com.pay10.commons.util.PaymentTypeUI;
import com.pay10.commons.util.PropertiesManager;
import com.pay10.commons.util.StatusType;
import com.pay10.commons.util.TransactionType;

import static com.mongodb.client.model.Filters.*;

@Component
public class SettlementReportQuery {

    @Autowired
    private MongoInstance mongoInstance;

    @Autowired
    PropertiesManager propertiesManager;

    @Autowired
    private UserDao userDao;

    @Autowired
    private BankSettleDao bankSettleDao;

    @Autowired
    private resellercommisiondao commisionDao;

    private static final String prefix = "MONGO_DB_";

    private static Logger logger = LoggerFactory.getLogger(SettlementReportQuery.class.getName());

    //here we are creating second version of the settlementReportDownload
    public List<MISReportObject> settlementReportDownloadV2(String merchantPayId, String acquirer, String currency,
                                                            String fromDate, String toDate) {
        logger.info("Inside Settlement report query Updated Class.");
        logger.info("Method settlementReportDownload() called for MISReportObject : {} , {}, {}, {},{}", merchantPayId,
                acquirer, currency, fromDate, toDate);
        List<MISReportObject> transactionList = new ArrayList<MISReportObject>();
        try {
            Bson query = buildQuery(merchantPayId, acquirer, currency,
                    fromDate, toDate);
            BsonDocument bsonDocument = query.toBsonDocument(BsonDocument.class, MongoClient.getDefaultCodecRegistry());
            System.out.print(bsonDocument.toString());
            logger.info("Is the query :- {} ", bsonDocument);
            logger.info("What is the query :- " + query.toString());
            transactionList = getMISReportObject(query);
        } catch (Exception e) {
            logger.error("Exception " + e);
            logger.info("Error occurred !! ", e);
        }
        logger.info("Cursor closed for Settlement report query , transactionList Size = " + transactionList.size());
        return transactionList;
    }

    private static Bson buildQuery(String merchantPayId, String acquirer, String currency,
                                   String fromDate, String toDate) {
        List<Bson> andFilters = new ArrayList<>();
        Bson dateRange = and(
                gte("CREATE_DATE", fromDate),
                lte("CREATE_DATE", toDate)
        );
        andFilters.add(dateRange);
        Bson statusCondition = or(
                and(ne(FieldType.HOLD_RELEASE.getName(), 1), in(FieldType.STATUS.getName(), StatusType.SETTLED_RECONCILLED.getName())),
                and(in(FieldType.TXNTYPE.getName(), TransactionType.SALE.getName(), TransactionType.RECO.getName()), in(FieldType.STATUS.getName(), StatusType.SETTLED_RECONCILLED.getName()))
        );
        andFilters.add(statusCondition);
        if (!merchantPayId.equalsIgnoreCase("ALL")) {
            andFilters.add(eq(FieldType.PAY_ID.getName(), merchantPayId));
        }
        if (!currency.equalsIgnoreCase("ALL")) {
            andFilters.add(eq(FieldType.CURRENCY_CODE.getName(), currency));
        }
        if (!"ALL".equalsIgnoreCase(acquirer)) {
            String[] acquirerList = acquirer.split(",");
            List<String> acquirerTypes = new ArrayList<>();
            for (String acq : acquirerList) {
                if (!"ALL".equalsIgnoreCase(acq.trim())) {
                    acquirerTypes.add(acq.trim());
                }
            }
            if (!acquirerTypes.isEmpty()) {
                andFilters.add(in(FieldType.ACQUIRER_TYPE.getName(), acquirerTypes));
            }
        }
        return and(andFilters);
    }

    //function to get the list of MIS Report
    private List<MISReportObject> getMISReportObject(Bson query) {
        List<MISReportObject> transactionList = new ArrayList<MISReportObject>();
        MongoDatabase dbIns = mongoInstance.getDB();
        MongoCollection<Document> coll = dbIns.getCollection(PropertiesManager.propertiesMap.get(prefix + Constants.TRANSACTION_STATUS_COLLECTION.getValue()));
        Map<String, User> userMap = new HashMap<String, User>();
        try (MongoCursor<Document> cursor = coll.find(query).iterator()) {
            while (cursor.hasNext()) {
                MISReportObject transReport = new MISReportObject();
                Document doc = cursor.next();
                String tempDate = "";
                String preCreateDate = "";
                User user = userDao.findPayId(doc.getString(FieldType.PAY_ID.toString()));
                if (userMap.get(doc.getString(FieldType.PAY_ID.toString())) != null) {
                    logger.info("SAme User  : {}", doc.getString(FieldType.PAY_ID.toString()));
                    transReport.setMerchants(userMap.get(doc.getString(FieldType.PAY_ID.toString())).getBusinessName());
                    transReport.setSettlementCycle(String
                            .valueOf(userMap.get(doc.getString(FieldType.PAY_ID.toString())).getSettlementDays()));
                    transReport.setAccountNo(
                            userMap.get(doc.getString(FieldType.PAY_ID.toString())).getAccountNo() == null ? "N/A"
                                    : userMap.get(doc.getString(FieldType.PAY_ID.toString())).getAccountNo());
                } else {
                    logger.info("Looking for PayId : {}", doc.getString(FieldType.PAY_ID.toString()));
                    try {
//                        User user = userDao.findPayId(doc.getString(FieldType.PAY_ID.toString()));
                        if (null != user) {
                            transReport.setMerchants(user.getBusinessName());
                            userMap.put(user.getPayId(), user);

                            preCreateDate = doc.getString(FieldType.CREATE_DATE.toString());
                            String[] hh = preCreateDate.split("\\s+");
                            tempDate = LocalDate.parse(hh[0]).plusDays(user.getSettlementDays()).toString() + " "
                                    + hh[1];
                            logger.info(" date updated as : " + preCreateDate + "\t tempdate " + tempDate
                                    + " and Date difference is: " + preCreateDate.compareTo(tempDate));
                            transReport.setSettlementCycle(String.valueOf(user.getSettlementDays()));
                            transReport.setAccountNo(user.getAccountNo() == null ? "N/A" : user.getAccountNo());
                        } else {
                            transReport.setMerchants("ALL");
                            transReport.setSettlementCycle("N/A");
                            transReport.setAccountNo("N/A");
                        }
                    } catch (Exception ee) {
                        logger.info("Error occurred !!! ", ee);
                    }
                }
                if (preCreateDate.compareTo(tempDate) <= 0) {
                    transReport.setTransactionId(doc.getString(FieldType.PG_REF_NUM.toString()));
                    transReport.setDateFrom(doc.getString(FieldType.SETTLEMENT_DATE.getName()));
                    transReport.setPayId(doc.getString(FieldType.PAY_ID.toString()));
                    transReport.setPgRefNum(doc.getString(FieldType.PG_REF_NUM.toString()));
                    transReport.setMopType(
                            StringUtils.isNotBlank(doc.getString(FieldType.MOP_TYPE.toString()))
                                    ? (MopType.getmop(doc.getString(FieldType.MOP_TYPE.toString())) != null
                                    ? MopType.getmop(doc.getString(FieldType.MOP_TYPE.toString())).getName()
                                    : "N/A") : "NA");
                    transReport.setAcquirerType(doc.getString(FieldType.ACQUIRER_TYPE.toString()));
                    transReport.setTxnType(
                            doc.getString(FieldType.TXNTYPE.toString()).contains(TransactionType.REFUND.getName())
                                    ? TransactionType.REFUND.getName()
                                    : TransactionType.SALE.getName());
                    transReport.setPaymentMethods(doc.getString(FieldType.PAYMENT_TYPE.toString()));
                    transReport.setTotalAmount(doc.getString(FieldType.TOTAL_AMOUNT.toString()));
                    transReport.setAmount(doc.getString(FieldType.AMOUNT.toString()));

                    if (doc.getString(FieldType.TXNTYPE.toString()).contains("REFUND")) {
                        transReport = asPerRefund(transReport, doc);
                    } else {
                        transReport = asPerOtherTransaction(transReport, doc);
                    }
                    if (StringUtils.isBlank(doc.getString(FieldType.SURCHARGE_FLAG.toString()))
                            || doc.getString(FieldType.SURCHARGE_FLAG.toString()).equalsIgnoreCase("N")) {
                        transReport.setSurchargeFlag("N");
                    } else {
                        transReport.setSurchargeFlag("Y");
                    }
                    if (StringUtils.isBlank(doc.getString(FieldType.CREATE_DATE.toString()))) {
                        transReport.setTransactionDate("NA");
                    } else {
                        transReport.setTransactionDate(doc.getString(FieldType.CREATE_DATE.toString()));
                    }
                    transReport.setOrderId(doc.getString(FieldType.ORDER_ID.toString()));
                    transReport.setRefundFlag(doc.getString(FieldType.REFUND_FLAG.toString()));
                    if (StringUtils.isNotBlank(doc.getString(FieldType.PAYMENTS_REGION.toString()))) {
                        transReport.setTransactionRegion(doc.getString(FieldType.PAYMENTS_REGION.toString()));
                    } else {
                        transReport.setTransactionRegion(AccountCurrencyRegion.DOMESTIC.toString());
                    }
//                    User user = userDao.findPayId(doc.getString(FieldType.PAY_ID.toString()));
                    if (user != null) {
                        String resellerId = user.getResellerId();
                        logger.info("getResellerId......" + resellerId);
                        if (resellerId != null) {
                            transReport = asPerReseller(transReport, doc, resellerId);
                        }
                    }
                    transactionList.add(transReport);
                }
            }
            cursor.close();
        }
        return transactionList;
    }

    //setting the transreport as per the Refund transaction
    public MISReportObject asPerRefund(MISReportObject transReport, Document doc) {
        double amtPayableToMerch = Double.parseDouble("1.01")
                - Double.parseDouble("2.00")
                - Double.parseDouble("0.00") * -1;
        transReport.setTotalAmtPayable(String.valueOf(amtPayableToMerch));
        double totalPayoutNodalAccount = Double.parseDouble(doc.getString(FieldType.TOTAL_AMOUNT.toString()))
                - Double.parseDouble(doc.getString(FieldType.ACQUIRER_TDR_SC.toString())) * -1;
        transReport.setTotalPayoutNodalAccount(String.valueOf(totalPayoutNodalAccount));
        transReport.setGrossTransactionAmt(String.format("%.2f",
                Double.parseDouble(doc.getString(FieldType.TOTAL_AMOUNT.toString())) * -1));
        transReport
                .setAggregatorCommissionAMT(
                        String.valueOf(
                                ((Double.parseDouble(doc.getString(FieldType.PG_TDR_SC.toString()))
                                        + Double.parseDouble(doc.getString(FieldType.PG_GST.toString())))
                                        * -1)));
        transReport
                .setAcquirerCommissionAMT(String.valueOf(
                        ((Double.parseDouble(doc.getString(FieldType.ACQUIRER_TDR_SC.toString()))
                                + Double.parseDouble(doc.getString(FieldType.ACQUIRER_GST.toString())))
                                * -1)));
        return transReport;
    }

    //set as per other Transaction
    public MISReportObject asPerOtherTransaction(MISReportObject transReport, Document doc) {
        double totalAmount = parseDoubleSafe(doc.getString(FieldType.TOTAL_AMOUNT.toString()));
        double pgTdrSc = parseDoubleSafe(doc.getString(FieldType.PG_TDR_SC.toString()));
        double pgGst = parseDoubleSafe(doc.getString(FieldType.PG_GST.toString()));
        double acquirerTdrSc = parseDoubleSafe(doc.getString(FieldType.ACQUIRER_TDR_SC.toString()));
        double acquirerGst = parseDoubleSafe(doc.getString(FieldType.ACQUIRER_GST.toString()));
        transReport.setGrossTransactionAmt(String.valueOf(totalAmount));
        if (pgTdrSc > 0 || pgGst > 0) {
            transReport.setAggregatorCommissionAMT(String.valueOf(pgTdrSc + pgGst));
        }
        if (acquirerTdrSc > 0 || acquirerGst > 0) {
            transReport.setAcquirerCommissionAMT(String.valueOf(acquirerTdrSc + acquirerGst));
        }
        double amtPayableToMerch = totalAmount;
        if (transReport.getAggregatorCommissionAMT() != null) {
            amtPayableToMerch -= parseDoubleSafe(transReport.getAggregatorCommissionAMT());
        }
        if (transReport.getAcquirerCommissionAMT() != null) {
            amtPayableToMerch -= parseDoubleSafe(transReport.getAcquirerCommissionAMT());
        }
        if (transReport.getGrossTransactionAmt() != null
                && transReport.getAggregatorCommissionAMT() != null
                && transReport.getAcquirerCommissionAMT() != null) {
            double totalAmtPayable = parseDoubleSafe(transReport.getGrossTransactionAmt())
                    - parseDoubleSafe(transReport.getAggregatorCommissionAMT())
                    - parseDoubleSafe(transReport.getAcquirerCommissionAMT());
            transReport.setTotalAmtPayable(String.valueOf(totalAmtPayable));
        }
        if (transReport.getGrossTransactionAmt() != null
                && transReport.getAcquirerCommissionAMT() != null) {
            double totalPayoutNodalAccount = parseDoubleSafe(transReport.getGrossTransactionAmt())
                    - parseDoubleSafe(transReport.getAcquirerCommissionAMT());
            transReport.setTotalPayoutNodalAccount(String.valueOf(totalPayoutNodalAccount));
        }
        return transReport;
    }

    //Set as per the reseller
    public MISReportObject asPerReseller(MISReportObject transReport, Document doc, String resellerId) {
        List<Resellercommision> resellercommissionList = commisionDao.getchargingdetail(resellerId,
                doc.getString(FieldType.PAY_ID.toString()), doc.getString(FieldType.CURRENCY_CODE.toString()));
        for (PaymentType paymentType : PaymentType.values()) {
            logger.info("get Payment Type for reseller commission...={}", paymentType);
            List<Resellercommision> resellercommisiondata = new ArrayList<Resellercommision>();
            String paymentName = paymentType.getCode();
            logger.info(" modified Payment Type for reseller commission...={}", paymentName);
            for (Resellercommision cDetail : resellercommissionList) {
                logger.info("cDetail..={}", cDetail.toString());
                if (cDetail.getTransactiontype().equals(paymentName)) {
                    cDetail.setMop(MopType.getmopName(cDetail.getMop()));
                    cDetail.setTransactiontype(
                            PaymentType.getpaymentName(cDetail.getTransactiontype()));
                    // resellercommisiondata.add(cDetail);
                    double txnAmount = Double.valueOf(doc.getString(FieldType.AMOUNT.toString()));
                    double commissionAmount = StringUtils.isNotBlank(cDetail.getCommission_amount())
                            ? Double.valueOf(cDetail.getCommission_amount())
                            : 0;
                    logger.info(" commissionAmount for reseller commission...={}",
                            commissionAmount);
                    double commissionAmtForPercentage = getCommissionAmountForPercentage(
                            cDetail.getCommission_percent(), txnAmount);
                    logger.info(" commissionAmtForPercentage for reseller commission...={}",
                            commissionAmtForPercentage);
                    double finalCommission = getCommissionAmountBaseonConfig(
                            cDetail.isCommissiontype(), commissionAmount,
                            commissionAmtForPercentage);
                    logger.info("finalCommission for reseller commission...={}", finalCommission);
                    transReport.setReseller_commission(String.valueOf(finalCommission));
                }
            }
        }
        return transReport;
    }

    double parseDoubleSafe(String value) {
        try {
            return Double.parseDouble(value);
        } catch (NullPointerException | NumberFormatException nullPointerException) {
            return 0.0;
        }
    }


    private double getCommissionAmountForPercentage(String commissionPercentage, double txnAmount) {
        double commissionAmtForPercentage = 0;
        if (StringUtils.isNotBlank(commissionPercentage)) {
            commissionAmtForPercentage = txnAmount * Double.valueOf(commissionPercentage) / 100;
        }
        return commissionAmtForPercentage;
    }

    private double getCommissionAmountBaseonConfig(boolean isHigher, double amount, double percentageAmount) {
        if (isHigher) {
            return percentageAmount > amount ? percentageAmount : amount;
        }
        return percentageAmount < amount ? percentageAmount : amount;
    }

    public Map<String, String> settlementReportDownload(String merchantPayId, String currency,
                                                        String fromDate, String toDate, CompanyProfile cp, User user) {

        logger.info("Inside amountMap report query Updated Class");

        Map<String, String> amountMap = new HashMap<String, String>();
        try {
            BasicDBObject dateQuery = new BasicDBObject();
            List<BasicDBObject> paramConditionLst = new ArrayList<BasicDBObject>();
            BasicDBObject currencyQuery = new BasicDBObject();
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
            }
            if (!currency.equalsIgnoreCase("ALL")) {
                paramConditionLst.add(new BasicDBObject(FieldType.CURRENCY_CODE.getName(), currency));
            }
            paramConditionLst.add(new BasicDBObject(FieldType.STATUS.getName(), StatusType.SETTLED_SETTLE.getName()));

            if (!paramConditionLst.isEmpty()) {
                allParamQuery = new BasicDBObject("$and", paramConditionLst);
            }

            List<BasicDBObject> fianlList = new ArrayList<BasicDBObject>();

            if (!allParamQuery.isEmpty()) {
                fianlList.add(allParamQuery);
            }

            List<BasicDBObject> allConditionQueryList = new ArrayList<BasicDBObject>();

            if (!dateQuery.isEmpty()) {
                allConditionQueryList.add(dateQuery);
            }
            if (!dateIndexConditionQuery.isEmpty()) {
                // allConditionQueryList.add(dateIndexConditionQuery);
            }

            BasicDBObject allConditionQueryObj = new BasicDBObject("$and", allConditionQueryList);

            if (!allConditionQueryObj.isEmpty()) {
                fianlList.add(allConditionQueryObj);
            }

            BasicDBObject finalquery = new BasicDBObject("$and", fianlList);

            MongoDatabase dbIns = mongoInstance.getDB();
            MongoCollection<Document> coll = dbIns.getCollection(
                    PropertiesManager.propertiesMap.get(prefix + Constants.COLLECTION_NAME.getValue()));

            logger.info("Inside Summary report query , finalquery = " + finalquery);

            BasicDBObject match = new BasicDBObject("$match", finalquery);
            logger.info("@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@");
            List<BasicDBObject> pipeline = Arrays.asList(match);
            AggregateIterable<Document> output = coll.aggregate(pipeline);
            logger.info("@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@");

            output.allowDiskUse(true);
            logger.info("@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@");

            MongoCursor<Document> cursor = output.iterator();
            Double amtPgTaxExempt = 0.00;
            Double amtPgTaxable = 0.00;
            Double amtPg = 0.00;
            Double totalamt = 0.00;
            Double amtGst = 0.00;
            Double gstpercent = 0.00;
            Double amtSGst = 0.00;
            Double sgstpercent = 0.00;
            Double amtCGst = 0.00;
            Double cgstpercent = 0.00;
            boolean first = true;
            logger.info("@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@");

            while (cursor.hasNext()) {
                Document doc = cursor.next();
                Double actualAmt = 0.00;
                actualAmt = Double.valueOf(doc.getString(FieldType.AMOUNT.toString()));
                if ((actualAmt <= 2000.00) && (doc.getString(FieldType.PAYMENT_TYPE.toString())
                        .equalsIgnoreCase(PaymentTypeUI.CREDIT_CARD.getCode().toString())
                        || doc.getString(FieldType.PAYMENT_TYPE.toString())
                        .equalsIgnoreCase(PaymentTypeUI.DEBIT_CARD.getCode().toString()))) {
                    amtPgTaxExempt += Double.valueOf(doc.getString(FieldType.PG_TDR_SC.toString()))
                            + Double.valueOf(doc.getString(FieldType.ACQUIRER_TDR_SC.toString()));
                    logger.info("@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@");

                } else {

                    amtPgTaxable += Double.valueOf(doc.getString(FieldType.PG_TDR_SC.toString()))
                            + Double.valueOf(doc.getString(FieldType.ACQUIRER_TDR_SC.toString()));
                    amtGst += Double.valueOf(doc.getString(FieldType.ACQUIRER_GST.toString()))
                            + Double.valueOf(doc.getString(FieldType.PG_GST.toString()));

                    logger.info("@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@" + amtGst);
                }

            }

            ServiceTaxDao std = new ServiceTaxDao();
            gstpercent = Double.valueOf(std.findServiceTaxByPayId(merchantPayId).doubleValue());
            logger.info("@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@");

            amtPg += amtPgTaxExempt + amtPgTaxable;
            cursor.close();
            totalamt += amtPgTaxExempt + amtPgTaxable + amtGst;
            logger.info("@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@" + totalamt);

            if (user.getState().equalsIgnoreCase(cp.getState())) {
                logger.info("@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@");

                amtSGst = amtGst / 2.00;
                sgstpercent = gstpercent / 2.00;
                amtCGst = amtGst / 2.00;
                cgstpercent = gstpercent / 2.00;
                logger.info("@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@");

                amountMap.put("totalamt", String.valueOf(totalamt));
                amountMap.put("amtPgTaxExempt", String.valueOf(amtPgTaxExempt));
                amountMap.put("amtPgTaxable", String.valueOf(amtPgTaxable));
                amountMap.put("amtSGst", String.valueOf(amtSGst));
                amountMap.put("sgstpercent", String.valueOf(sgstpercent));
                amountMap.put("amtCGst", String.valueOf(amtCGst));
                amountMap.put("cgstpercent", String.valueOf(cgstpercent));
                amountMap.put("amtGst", String.valueOf(amtGst));
                amountMap.put("gstpercent", String.valueOf(gstpercent));
                amountMap.put("amtPg", String.valueOf(amtPg));
                logger.info("@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@");

            } else {
                logger.info("@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@");

                amountMap.put("totalamt", String.valueOf(totalamt));
                amountMap.put("amtPgTaxExempt", String.valueOf(amtPgTaxExempt));
                amountMap.put("amtPgTaxable", String.valueOf(amtPgTaxable));
                amountMap.put("amtGst", String.valueOf(amtGst));
                amountMap.put("gstpercent", String.valueOf(gstpercent));
                amountMap.put("amtPg", String.valueOf(amtPg));
                logger.info("@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@");

            }

        } catch (Exception e) {
            logger.error("Exception " + e);
        }
        logger.info("Cursor closed for Settlement report query , amountMap Size = " + amountMap.size());
        return amountMap;

    }

    public List<MISReportObject> settlementReport(String merchantPayId, String acquirer, String currency,
                                                  String fromDate, String toDate) {

        List<MISReportObject> transactionList = new ArrayList<MISReportObject>();

        try {
            BasicDBObject dateQuery = new BasicDBObject();
            List<BasicDBObject> paramConditionLst = new ArrayList<BasicDBObject>();
            BasicDBObject currencyQuery = new BasicDBObject();
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

            if (!merchantPayId.equalsIgnoreCase("ALL")) {
                paramConditionLst.add(new BasicDBObject(FieldType.PAY_ID.getName(), merchantPayId));
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

            List<BasicDBObject> saleConditionList = new ArrayList<BasicDBObject>();
            saleConditionList.add(new BasicDBObject(FieldType.TXNTYPE.getName(), TransactionType.RECO.getName()));
            saleConditionList.add(new BasicDBObject(FieldType.STATUS.getName(), StatusType.SETTLED_SETTLE.getName()));

            BasicDBObject saleConditionQuery = new BasicDBObject("$and", saleConditionList);
            List<BasicDBObject> refundConditionList = new ArrayList<BasicDBObject>();
            refundConditionList
                    .add(new BasicDBObject(FieldType.TXNTYPE.getName(), TransactionType.REFUNDRECO.getName()));
            refundConditionList.add(new BasicDBObject(FieldType.STATUS.getName(), StatusType.SETTLED_SETTLE.getName()));

            BasicDBObject refundConditionQuery = new BasicDBObject("$and", refundConditionList);

            List<BasicDBObject> bothConditionList = new ArrayList<BasicDBObject>();
            bothConditionList.add(saleConditionQuery);
            bothConditionList.add(refundConditionQuery);
            BasicDBObject addConditionListQuery = new BasicDBObject("$or", bothConditionList);
            if (!paramConditionLst.isEmpty()) {
                allParamQuery = new BasicDBObject("$and", paramConditionLst);
            }

            List<BasicDBObject> fianlList = new ArrayList<BasicDBObject>();
            if (!currencyQuery.isEmpty()) {
                fianlList.add(currencyQuery);
            }
            if (!acquirerQuery.isEmpty()) {
                fianlList.add(acquirerQuery);
            }
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
                    PropertiesManager.propertiesMap.get(prefix + Constants.SETTLED_TRANSACTIONS_NAME.getValue()));

            // create our pipeline operations, first with the $match
            MongoCursor<Document> cursor = coll.find(finalquery).iterator();
            while (cursor.hasNext()) {
                Document doc = cursor.next();
                MISReportObject transReport = new MISReportObject();
                transReport.setTransactionId(doc.getString(FieldType.TXN_ID.toString()));
                transReport.setDateFrom(doc.getString(FieldType.CREATE_DATE.getName()));
                transReport.setPayId(doc.getString(FieldType.PAY_ID.toString()));
                transReport.setPgRefNum(doc.getString(FieldType.PG_REF_NUM.toString()));
                transReport.setMopType(doc.getString(FieldType.MOP_TYPE.toString()));
                transReport.setAcquirerType(doc.getString(FieldType.ACQUIRER_TYPE.toString()));

                if (doc.getString(FieldType.TXNTYPE.toString()).contains(TransactionType.REFUND.getName())) {
                    transReport.setTxnType(TransactionType.REFUND.getName());
                } else {
                    transReport.setTxnType(TransactionType.SALE.getName());
                }

                transReport.setPaymentMethods(doc.getString(FieldType.PAYMENT_TYPE.toString()));
                transReport.setTotalAmount(doc.getString(FieldType.TOTAL_AMOUNT.toString()));
                transReport.setAmount(doc.getString(FieldType.AMOUNT.toString()));

                if (StringUtils.isBlank(doc.getString(FieldType.SURCHARGE_FLAG.toString()))) {
                } else {
                    transReport.setSurchargeFlag("Y");
                }

                if (StringUtils.isBlank(doc.getString(FieldType.PG_DATE_TIME.toString()))) {

                    transReport.setTransactionDate("NA");
                } else {
                    transReport.setTransactionDate(doc.getString(FieldType.PG_DATE_TIME.toString()));
                }

                transReport.setTxnType(doc.getString(FieldType.ORIG_TXNTYPE.toString()));
                transReport.setOrderId(doc.getString(FieldType.ORDER_ID.toString()));
                transReport.setRefundFlag(doc.getString(FieldType.REFUND_FLAG.toString()));

                if (StringUtils.isNotBlank(doc.getString(FieldType.PAYMENTS_REGION.toString()))) {
                    transReport.setTransactionRegion(doc.getString(FieldType.PAYMENTS_REGION.toString()));
                } else {
                    transReport.setTransactionRegion(AccountCurrencyRegion.DOMESTIC.toString());
                }

                transactionList.add(transReport);
            }
            cursor.close();

        } catch (Exception e) {
            logger.error("Exception " + e);

        } finally {

        }
        return transactionList;

    }

    public TdrPojo calculationForTdr(double bankTdr, double PgFixCharge, double pgTdr, double bankFixCharge,
                                     double merchantFixCharge, double merchantTdr, double merchantServiceTax, String amount) {
        TdrPojo tdrPojo = new TdrPojo();

        double totalamount = Double.parseDouble(amount);

        // Bank tdr
        double bankTdrCalculate = (bankTdr / 100);
        double totalGstOnBank = ((bankFixCharge + bankTdrCalculate) * 18 / 100);
        double bankTotalTdr = totalamount - totalGstOnBank;

        // pg TDR
        double pgTdrCalculate = (pgTdr / 100);
        double totalGstOnPG = ((PgFixCharge + pgTdrCalculate) * 18 / 100);
        double pgTotalTdr = totalamount - totalGstOnPG;

        // merchant TDR
        double merchantTdrCalculate = (merchantTdr / 100);
        double totalGstOnMerchant = ((merchantFixCharge + merchantTdrCalculate) * 18 / 100);
        double merchantTotalTdr = totalamount - totalGstOnMerchant;
        String totalPgTdr = String.valueOf((bankTotalTdr - merchantTotalTdr));

        return tdrPojo;

    }

    public List<MISReportObject> settlementReportDownloadSplit(String merchantPayId, String acquirer, String currency,
                                                               String fromDate, String toDate) {

        logger.info("Inside Settlement report query Updated Class.");
        logger.info("Method settlementReportDownloadSplit() called for MISReportObject : {} , {}, {}, {},{}",
                merchantPayId, acquirer, currency, fromDate, toDate);
        List<MISReportObject> transactionList = new ArrayList<MISReportObject>();

        try {
            BasicDBObject dateQuery = new BasicDBObject();
            List<BasicDBObject> paramConditionLst = new ArrayList<BasicDBObject>();
            BasicDBObject currencyQuery = new BasicDBObject();
            BasicDBObject acquirerQuery = new BasicDBObject();
            BasicDBObject allParamQuery = new BasicDBObject();
            List<BasicDBObject> acquirerConditionLst = new ArrayList<BasicDBObject>();
            Map<String, User> userMap = new HashMap<String, User>();

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
            }
            if (!currency.equalsIgnoreCase("ALL")) {
                paramConditionLst.add(new BasicDBObject(FieldType.CURRENCY_CODE.getName(), currency));
            }

            paramConditionLst
                    .add(new BasicDBObject(FieldType.STATUS.getName(), StatusType.SETTLED_RECONCILLED.getName()));

            if (!acquirer.equalsIgnoreCase("ALL")) {
                List<String> acquirerList = Arrays.asList(acquirer.split(","));
                for (String acq : acquirerList) {

                    acquirerConditionLst.add(new BasicDBObject(FieldType.ACQUIRER_TYPE.getName(), acq.trim()));
                }
                acquirerQuery.append("$or", acquirerConditionLst);

            }

            List<BasicDBObject> saleConditionList = new ArrayList<BasicDBObject>();
            saleConditionList.add(new BasicDBObject(FieldType.TXNTYPE.getName(), TransactionType.SALE.getName()));
            saleConditionList
                    .add(new BasicDBObject(FieldType.STATUS.getName(), StatusType.SETTLED_RECONCILLED.getName()));

            BasicDBObject saleConditionQuery = new BasicDBObject("$and", saleConditionList);
            List<BasicDBObject> refundConditionList = new ArrayList<BasicDBObject>();
            refundConditionList
                    .add(new BasicDBObject(FieldType.TXNTYPE.getName(), TransactionType.REFUNDRECO.getName()));
            refundConditionList.add(new BasicDBObject(FieldType.STATUS.getName(), StatusType.SETTLED_SETTLE.getName()));

            BasicDBObject refundConditionQuery = new BasicDBObject("$and", refundConditionList);

            List<BasicDBObject> bothConditionList = new ArrayList<BasicDBObject>();
            bothConditionList.add(saleConditionQuery);
            bothConditionList.add(refundConditionQuery);
            BasicDBObject addConditionListQuery = new BasicDBObject("$or", bothConditionList);
            if (!paramConditionLst.isEmpty()) {
                allParamQuery = new BasicDBObject("$and", paramConditionLst);
            }

            List<BasicDBObject> fianlList = new ArrayList<BasicDBObject>();
            if (!currencyQuery.isEmpty()) {
                fianlList.add(currencyQuery);
            }
            if (!acquirerQuery.isEmpty()) {
                fianlList.add(acquirerQuery);
            }
            if (!dateQuery.isEmpty()) {
                // fianlList.add(dateQuery);
            }
            if (!dateIndexConditionQuery.isEmpty()) {
                // fianlList.add(dateIndexConditionQuery);
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

            logger.info("Inside Summary report query , finalquery = " + finalquery);

            BasicDBObject match = new BasicDBObject("$match", finalquery);

            List<BasicDBObject> pipeline = Arrays.asList(match);
            AggregateIterable<Document> output = coll.aggregate(pipeline);
            output.allowDiskUse(true);
            MongoCursor<Document> cursor = output.iterator();

            while (cursor.hasNext()) {
                Document doc = cursor.next();
                String tempDate = "";
                String preCreateDate = "";
                // logger.info("Document from DB : {}",doc.toJson());
                MISReportObject transReport = new MISReportObject();

                if (userMap.get(doc.getString(FieldType.PAY_ID.toString())) != null) {

                    transReport.setMerchants(userMap.get(doc.getString(FieldType.PAY_ID.toString())).getBusinessName());
                    transReport.setSettlementCycle(String
                            .valueOf(userMap.get(doc.getString(FieldType.PAY_ID.toString())).getSettlementDays()));

                } else {
                    logger.info("Looking for PayId : {}", doc.getString(FieldType.PAY_ID.toString()));
                    try {
                        // Kept try-Catch to avoid indiviual payId failure
                        User user = userDao.findPayId(doc.getString(FieldType.PAY_ID.toString()));
                        if (null != user) {
                            transReport.setMerchants(user.getBusinessName());
                            userMap.put(user.getPayId(), user);
                            preCreateDate = doc.getString(FieldType.CREATE_DATE.toString());
                            String[] hh = preCreateDate.split("\\s+");
                            tempDate = LocalDate.parse(hh[0]).plusDays(user.getSettlementDays()).toString() + " "
                                    + hh[1];
                            logger.info(" date updated as : " + preCreateDate + "\t tempdate " + tempDate
                                    + " and Date difference is: " + preCreateDate.compareTo(tempDate));
                            transReport.setSettlementCycle(String.valueOf(user.getSettlementDays()));

                        } else {
                            transReport.setMerchants("ALL");
                            transReport.setSettlementCycle("N/A");

                        }
                    } catch (Exception ee) {
                        logger.info("Error occurred !!! ", ee);
                    }

                }
                if (preCreateDate.compareTo(tempDate) <= 0) {
                    transReport.setTransactionId(doc.getString(FieldType.PG_REF_NUM.toString()));
                    transReport.setDateFrom(doc.getString(FieldType.CREATE_DATE.getName()));
                    transReport.setPayId(doc.getString(FieldType.PAY_ID.toString()));
                    transReport.setPgRefNum(doc.getString(FieldType.PG_REF_NUM.toString()));

                    if (StringUtils.isNotBlank(doc.getString(FieldType.MOP_TYPE.toString()))) {
                        transReport.setMopType(MopType.getmop(doc.getString(FieldType.MOP_TYPE.toString())).getName());
                    } else {
                        transReport.setMopType("NA");
                    }

                    transReport.setAcquirerType(doc.getString(FieldType.ACQUIRER_TYPE.toString()));

                    if (doc.getString(FieldType.TXNTYPE.toString()).contains(TransactionType.REFUND.getName())) {
                        transReport.setTxnType(TransactionType.REFUND.getName());
                    } else {
                        transReport.setTxnType(TransactionType.SALE.getName());
                    }

                    transReport.setPaymentMethods(doc.getString(FieldType.PAYMENT_TYPE.toString()));
                    transReport.setTotalAmount(doc.getString(FieldType.TOTAL_AMOUNT.toString()));
                    transReport.setAmount(doc.getString(FieldType.AMOUNT.toString()));

                    if (doc.getString(FieldType.TXNTYPE.toString()).contains("REFUND")) {

                        Double amtPayableToMerch = 0.00;

                        amtPayableToMerch = Double.valueOf(doc.getString(FieldType.TOTAL_AMOUNT.toString()));
                        amtPayableToMerch = amtPayableToMerch
                                - Double.valueOf(doc.getString(FieldType.PG_TDR_SC.toString()));
                        amtPayableToMerch = amtPayableToMerch
                                - Double.valueOf(doc.getString(FieldType.ACQUIRER_TDR_SC.toString()));

                        Double totalPayoutNodalAccount = 0.00;

                        totalPayoutNodalAccount = Double.valueOf(doc.getString(FieldType.TOTAL_AMOUNT.toString()));
                        totalPayoutNodalAccount = totalPayoutNodalAccount
                                - Double.valueOf(doc.getString(FieldType.ACQUIRER_TDR_SC.toString()));

                        transReport.setGrossTransactionAmt(String.format("%.2f",
                                Double.valueOf(doc.getString(FieldType.TOTAL_AMOUNT.toString())) * -1));

                        transReport
                                .setAggregatorCommissionAMT(
                                        String.format("%.2f",
                                                ((Double.valueOf(doc.getString(FieldType.PG_TDR_SC.toString()))
                                                        + Double.valueOf(doc.getString(FieldType.PG_GST.toString())))
                                                        * -1)));

                        transReport
                                .setAcquirerCommissionAMT(String.format("%.2f",
                                        ((Double.valueOf(doc.getString(FieldType.ACQUIRER_TDR_SC.toString()))
                                                + Double.valueOf(doc.getString(FieldType.ACQUIRER_GST.toString())))
                                                * -1)));

                        transReport.setTotalAmtPayable(String.format("%.2f", amtPayableToMerch * -1));

                        transReport.setTotalPayoutNodalAccount(String.format("%.2f", totalPayoutNodalAccount * -1));

                    } else {

                        Double amtPayableToMerch = 0.00;

                        amtPayableToMerch = Double.valueOf(doc.getString(FieldType.TOTAL_AMOUNT.toString()));
                        // amtPayableToMerch = amtPayableToMerch -
                        // Double.valueOf(doc.getString(FieldType.PG_TDR_SC.toString()));
                        // amtPayableToMerch = amtPayableToMerch -
                        // Double.valueOf(doc.getString(FieldType.ACQUIRER_TDR_SC.toString()));

                        Double totalPayoutNodalAccount = 0.00;

                        // totalPayoutNodalAccount = totalPayoutNodalAccount -
                        // Double.valueOf(doc.getString(FieldType.ACQUIRER_TDR_SC.toString()));

                        transReport.setGrossTransactionAmt(String.format("%.2f",
                                Double.valueOf(doc.getString(FieldType.TOTAL_AMOUNT.toString()))));
                        transReport.setAggregatorCommissionAMT(
                                String.format("%.2f", Double.valueOf(doc.getString(FieldType.PG_TDR_SC.toString()))
                                        + Double.valueOf(doc.getString(FieldType.PG_GST.toString()))));
                        transReport.setAcquirerCommissionAMT(String.format("%.2f",
                                Double.valueOf(doc.getString(FieldType.ACQUIRER_TDR_SC.toString()))
                                        + Double.valueOf(doc.getString(FieldType.ACQUIRER_GST.toString()))));

                        amtPayableToMerch -= Double.valueOf(transReport.getAggregatorCommissionAMT().toString());
                        amtPayableToMerch -= Double.valueOf(transReport.getAcquirerCommissionAMT().toString());

                        totalPayoutNodalAccount -= Double.valueOf(transReport.getAcquirerCommissionAMT().toString());

                        transReport.setTotalAmtPayable(String.format("%.2f", amtPayableToMerch));
                        transReport.setTotalPayoutNodalAccount(String.format("%.2f", totalPayoutNodalAccount));

                    }

                    if (StringUtils.isBlank(doc.getString(FieldType.SURCHARGE_FLAG.toString()))) {
                        transReport.setSurchargeFlag("N");
                    } else {
                        transReport.setSurchargeFlag("Y");
                    }

                    if (StringUtils.isBlank(doc.getString(FieldType.PG_DATE_TIME.toString()))) {
                        transReport.setTransactionDate("NA");
                    } else {
                        transReport.setTransactionDate(doc.getString(FieldType.PG_DATE_TIME.toString()));
                    }

                    transReport.setOrderId(doc.getString(FieldType.ORDER_ID.toString()));
                    transReport.setRefundFlag(doc.getString(FieldType.REFUND_FLAG.toString()));

                    if (StringUtils.isNotBlank(doc.getString(FieldType.PAYMENTS_REGION.toString()))) {
                        transReport.setTransactionRegion(doc.getString(FieldType.PAYMENTS_REGION.toString()));
                    } else {
                        transReport.setTransactionRegion(AccountCurrencyRegion.DOMESTIC.toString());
                    }

                    // transactionList.add(transReport);

                    // added by deep
                    if (StringUtils.isNotBlank(doc.getString(FieldType.UDF6.getName()))) {
                        if (doc.getString(FieldType.UDF6.getName()).contains("|")) {

                            String data = doc.getString(FieldType.UDF6.getName());
                            String[] accountDetails = data.split("\\|");
                            logger.info("Split Report UDF 6 : " + doc.getString(FieldType.UDF6.getName()));
                            int count = 0;
                            for (String account : accountDetails) {
                                MISReportObject transReportDuplicateAcNoEntry = (MISReportObject) transReport.clone();
                                // fetch account detail from banksettle based on productid
                                logger.info("Split Report UDF 6 : " + account);
                                String[] pro_acco = account.split("=");
                                logger.info("Split Report UDF 6 : " + pro_acco[0]);
                                List<BankSettle> bankSettleAccount = bankSettleDao.getSplitAccountDetail(pro_acco[0]);
                                logger.info("Split Report UDF 6 : " + bankSettleAccount);
                                if (count != 0) {
                                    transReportDuplicateAcNoEntry.setGrossTransactionAmt("0");
                                    transReportDuplicateAcNoEntry.setTotalAmount("0");
                                    transReportDuplicateAcNoEntry.setAggregatorCommissionAMT("0");
                                    transReportDuplicateAcNoEntry.setTotalAmtPayable("0");
                                    transReportDuplicateAcNoEntry.setAcquirerCommissionAMT("0");
                                    transReportDuplicateAcNoEntry.setTotalPayoutNodalAccount("0");
                                }
                                count++;
                                if (bankSettleAccount.size() > 0) {
                                    transReportDuplicateAcNoEntry
                                            .setAccountHolderName(bankSettleAccount.get(0).getAccountHolderName());
                                    transReportDuplicateAcNoEntry
                                            .setAccountNumber(bankSettleAccount.get(0).getAccountNumber());
                                    transReportDuplicateAcNoEntry.setIfsc(bankSettleAccount.get(0).getIfscCode());
                                    transReportDuplicateAcNoEntry.setTotalAmtPayable(
                                            StringUtils.isNotBlank(pro_acco[1]) ? pro_acco[1] : "0");
                                    transactionList.add(transReportDuplicateAcNoEntry);
                                }

                            }

                        }

                    } else {
                        // User user=userDao.findPayId(userMap.get(key));
                        User user = userDao.findPayId(doc.getString(FieldType.PAY_ID.toString()));
                        transReport.setAccountHolderName(user.getAccHolderName());
                        transReport.setAccountNumber(user.getAccountNo());
                        transReport.setIfsc(user.getIfscCode());

                        transactionList.add(transReport);
                    }
                    // ended by deep
                } else {
                    // do not add any entries
                    // do nothing
                }
            }
            cursor.close();

        } catch (Exception e) {
            logger.error("Exception " + e);
            logger.info("Error occurred !! ", e);
        }
        logger.info("Cursor closed for Settlement report query , transactionList Size = " + transactionList.size());
        return transactionList;
    }

    public List<MISReportObject> settlementReportDownloadSplitSettled(String merchantPayId, String acquirer, String currency,
                                                                      String fromDate, String toDate, User user) {

        logger.info("Inside Settlement report query Updated Class.");
        logger.info("Method settlementReportDownloadSplitSettled() called for Settled MIS ReportObject : {} , {}, {}, {},{}",
                merchantPayId, acquirer, currency, fromDate, toDate);

        List<MISReportObject> transactionList = new ArrayList<MISReportObject>();
        try {
            BasicDBObject dateQuery = new BasicDBObject();
            List<BasicDBObject> paramConditionLst = new ArrayList<BasicDBObject>();
            BasicDBObject currencyQuery = new BasicDBObject();
            BasicDBObject acquirerQuery = new BasicDBObject();
            BasicDBObject allParamQuery = new BasicDBObject();
            List<BasicDBObject> acquirerConditionLst = new ArrayList<BasicDBObject>();
            Map<String, User> userMap = new HashMap<String, User>();

            String currentDate = null;
            if (!fromDate.isEmpty()) {

                if (!toDate.isEmpty()) {
                    currentDate = toDate;
                } else {
                    DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    Calendar cal = Calendar.getInstance();
                    currentDate = dateFormat.format(cal.getTime());
                }

                dateQuery.put(FieldType.SETTLEMENT_DATE.getName(),
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
                dateIndexConditionList.add(new BasicDBObject(FieldType.SETTLEMENT_DATE_INDEX.getName(), dateIndex));
            }
            if (StringUtils.isNotBlank(PropertiesManager.propertiesMap.get(Constants.USE_DATE_INDEX.getValue()))
                    && PropertiesManager.propertiesMap.get(Constants.USE_DATE_INDEX.getValue()).equalsIgnoreCase("Y")) {
                dateIndexConditionQuery.append("$or", dateIndexConditionList);
            }

            if (!merchantPayId.equalsIgnoreCase("ALL")) {
                paramConditionLst.add(new BasicDBObject(FieldType.PAY_ID.getName(), merchantPayId));
            } else if (!user.getUserType().equals(UserType.ADMIN)) {

                if (merchantPayId.equalsIgnoreCase("ALL")) {
                    logger.info("TxnReport, MerchantId : " + merchantPayId + ", Segment : " + user.getSegment());
                    if (!user.getSegment().equalsIgnoreCase("Default")) {
                        List<String> payIdLst = userDao.getPayIdForSplitPaymentMerchant(user.getSegment());
                        logger.info("Get PayId List : " + payIdLst);
                        if (payIdLst.size() > 0) {
                            paramConditionLst.add(new BasicDBObject(FieldType.PAY_ID.getName(), new BasicDBObject("$in", payIdLst)));
                        }
                    }

                }
            }

            if (!currency.equalsIgnoreCase("ALL")) {
                paramConditionLst.add(new BasicDBObject(FieldType.CURRENCY_CODE.getName(), currency));
            }

            paramConditionLst
                    .add(new BasicDBObject(FieldType.STATUS.getName(), StatusType.SETTLED_SETTLE.getName()));

            if (!acquirer.equalsIgnoreCase("ALL")) {
                List<String> acquirerList = Arrays.asList(acquirer.split(","));
                for (String acq : acquirerList) {

                    acquirerConditionLst.add(new BasicDBObject(FieldType.ACQUIRER_TYPE.getName(), acq.trim()));
                }
                acquirerQuery.append("$or", acquirerConditionLst);

            }

            List<BasicDBObject> saleConditionList = new ArrayList<BasicDBObject>();
            saleConditionList.add(new BasicDBObject(FieldType.TXNTYPE.getName(), TransactionType.SALE.getName()));
            saleConditionList
                    .add(new BasicDBObject(FieldType.STATUS.getName(), StatusType.SETTLED_SETTLE.getName()));

            BasicDBObject saleConditionQuery = new BasicDBObject("$and", saleConditionList);
            List<BasicDBObject> refundConditionList = new ArrayList<BasicDBObject>();
            refundConditionList
                    .add(new BasicDBObject(FieldType.TXNTYPE.getName(), TransactionType.REFUNDRECO.getName()));
            refundConditionList.add(new BasicDBObject(FieldType.STATUS.getName(), StatusType.SETTLED_SETTLE.getName()));

            BasicDBObject refundConditionQuery = new BasicDBObject("$and", refundConditionList);

            List<BasicDBObject> bothConditionList = new ArrayList<BasicDBObject>();
            bothConditionList.add(saleConditionQuery);
            bothConditionList.add(refundConditionQuery);
            BasicDBObject addConditionListQuery = new BasicDBObject("$or", bothConditionList);
            if (!paramConditionLst.isEmpty()) {
                allParamQuery = new BasicDBObject("$and", paramConditionLst);
            }

            List<BasicDBObject> fianlList = new ArrayList<BasicDBObject>();
            if (!currencyQuery.isEmpty()) {
                fianlList.add(currencyQuery);
            }
            if (!acquirerQuery.isEmpty()) {
                fianlList.add(acquirerQuery);
            }
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

            logger.info("Inside Summary report query , finalquery = " + finalquery);

            BasicDBObject match = new BasicDBObject("$match", finalquery);

            List<BasicDBObject> pipeline = Arrays.asList(match);
            AggregateIterable<Document> output = coll.aggregate(pipeline);
            output.allowDiskUse(true);
            MongoCursor<Document> cursor = output.iterator();

            while (cursor.hasNext()) {
                MISReportObject transReport = new MISReportObject();
                Document document = (Document) cursor.next();


                if (userMap.get(document.get(FieldType.PAY_ID.getName())) != null) {
                    transReport.setMerchants(userMap.get(document.getString(FieldType.PAY_ID.toString())).getBusinessName());

                } else {
                    logger.info("Looking for PayId : {}", document.getString(FieldType.PAY_ID.toString()));
                    try {
                        // Kept try-Catch to avoid indiviual payId failure
                        User user1 = userDao.findPayId(document.getString(FieldType.PAY_ID.toString()));
                        if (null != user1) {
                            transReport.setMerchants(user1.getBusinessName());

                            userMap.put(user1.getPayId(), user1);

                        } else {
                            transReport.setMerchants("N/A");
                        }
                    } catch (Exception ee) {
                        logger.info("Error occurred !!! ", ee);
                    }

                }

                transReport.setPayId(document.getString(FieldType.PAY_ID.toString()));
                transReport.setPgRefNum(document.getString(FieldType.PG_REF_NUM.toString()));
                transReport.setTransactionId(document.getString(FieldType.PG_REF_NUM.toString()));
                transReport.setOrderId(document.getString(FieldType.ORDER_ID.toString()));
                transReport.setTransactionDate(document.getString(FieldType.CREATE_DATE.toString()));
                transReport.setDateFrom(document.getString(FieldType.SETTLEMENT_DATE.toString()));
                transReport.setTxnType(TransactionType.SALE.toString());
                transReport.setPaymentMethods(document.getString(FieldType.PAYMENT_TYPE.toString()));
                transReport.setMopType(document.getString(FieldType.MOP_TYPE.toString()));
                transReport.setUtrNo(document.getString(FieldType.UTR_NO.toString()));
                transReport.setAmount(document.getString(FieldType.AMOUNT.toString()));
                transReport.setTotalAmount(document.getString(FieldType.TOTAL_AMOUNT.toString()));

                double tdrAmount = 0.0;
                double gstOnTdr = 0.0;
                if (!document.getString(FieldType.TXNTYPE.toString()).toLowerCase().contains("refund")) {
                    if (StringUtils.isBlank(document.getString(FieldType.SURCHARGE_FLAG.toString())) || document.getString(FieldType.SURCHARGE_FLAG.toString()).equalsIgnoreCase("N")) {
                        double acqGst = Double.parseDouble(document.getString(FieldType.ACQUIRER_GST.toString()) == null ? "0.0" : document.getString(FieldType.ACQUIRER_GST.toString()));
                        double PgGst = Double.parseDouble(document.getString(FieldType.PG_GST.toString()) == null ? "0.0" : document.getString(FieldType.PG_GST.toString()));

                        double acqTdr = Double.parseDouble(document.getString(FieldType.ACQUIRER_TDR_SC.toString()) == null ? "0.0" : document.getString(FieldType.ACQUIRER_TDR_SC.toString()));
                        double pgTdr = Double.parseDouble(document.getString(FieldType.PG_TDR_SC.toString()) == null ? "0.0" : document.getString(FieldType.PG_TDR_SC.toString()));

                        tdrAmount = acqTdr + pgTdr;
                        gstOnTdr = PgGst + acqGst;


                    } else {
                        Session session = HibernateSessionProvider.getSession();
                        Query chargingdetailquery = session.createSQLQuery(
                                "SELECT bankTDR, bankTDRAFC, bankServiceTax, pgTDR, pgTDRAFC, pgServiceTax,allowFixCharge FROM ChargingDetails WHERE payId=:payId and paymentType=:paymentType and mopType=:mopType and acquirerName=:acquirerName and createdDate<=:createdDate  order by createdDate desc");
                        chargingdetailquery.setParameter("payId", document.getString(FieldType.PAY_ID.toString()));
                        chargingdetailquery.setParameter("paymentType", PaymentType
                                .getInstanceUsingCode(document.getString(FieldType.PAYMENT_TYPE.toString())).toString());
                        chargingdetailquery.setParameter("mopType",
                                MopType.getmop(document.getString(FieldType.MOP_TYPE.toString())).toString());
                        chargingdetailquery.setParameter("acquirerName",
                                AcquirerType.getAcquirerName(document.getString(FieldType.ACQUIRER_TYPE.toString())));
                        chargingdetailquery.setParameter("createdDate",
                                document.getString(FieldType.CREATE_DATE.toString()));
                        List<Object[]> chargingdetail = chargingdetailquery.list();
                        logger.info("Query : " + chargingdetailquery.getQueryString());

                        Object[] object = chargingdetail.get(0);
                        double bankTDR = Double.parseDouble("" + object[0]);
                        double bankTDRAFC = Double.parseDouble("" + object[1]);
                        double bankServiceTax = Double.parseDouble("" + object[2]);

                        double pgTDR = Double.parseDouble("" + object[3]);

                        double pgTDRAFC = Double.parseDouble("" + object[4]);

                        double pgServiceTax = Double.parseDouble("" + object[5]);

                        boolean allowFixCharge = Boolean.parseBoolean("" + object[6]);
                        logger.info(" charging details lise size " + chargingdetail.size());
                        logger.info(" charging details are:");
                        logger.info("allowFixCharge : " + allowFixCharge);

                        double aggregatortdramount = 0;

                        double acquirertdramount = 0;
                        logger.info("Total Amount : "
                                + String.format("%.2f",
                                Double.valueOf(document.getString(FieldType.TOTAL_AMOUNT.toString())))
                                + "\t Amount : " + transReport.getAmount());
                        if (allowFixCharge == true) {

                            aggregatortdramount = Double.valueOf(transReport.getAmount()) * (pgTDRAFC / 100);

                            tdrAmount = aggregatortdramount;
                            gstOnTdr = ((pgServiceTax / 100) * aggregatortdramount);

                            acquirertdramount = Double.valueOf(transReport.getAmount()) * (bankTDRAFC / 100);
                            tdrAmount += acquirertdramount;
                            gstOnTdr += ((bankServiceTax / 100) * acquirertdramount);

                        } else {

                            aggregatortdramount = Double.valueOf(transReport.getAmount()) * (pgTDR / 100);
                            tdrAmount = aggregatortdramount;
                            gstOnTdr = ((pgServiceTax / 100) * aggregatortdramount);


                            acquirertdramount = Double.valueOf(transReport.getAmount()) * (bankTDR / 100);
                            tdrAmount += acquirertdramount;

                            gstOnTdr += ((bankServiceTax / 100) * acquirertdramount);

                        }

                    }
                    double payToMerchant = Double.valueOf(transReport.getTotalAmount()) - gstOnTdr - tdrAmount;
                    transReport.setGrossTransactionAmt(transReport.getTotalAmount());
                    //transReport.setGrossTransactionAmt(transReport.getTotalAmount());
                    transReport.setTotalAmtPayable(String.format("%.2f", payToMerchant));
                    transReport.setTdrAmount(String.format("%.2f", tdrAmount));
                    transReport.setGstOnTdrAmount(String.format("%.2f", gstOnTdr));
                }


                if (StringUtils.isNotBlank(document.getString(FieldType.UDF6.getName()))) {
                    if (document.getString(FieldType.UDF6.getName()).contains("|")) {

                        String data = document.getString(FieldType.UDF6.getName());
                        String[] accountDetails = data.split("\\|");
                        logger.info("Split Report UDF 6 : " + document.getString(FieldType.UDF6.getName()));
                        int count = 0;
                        //map object for utr no logic here

                        //UTR Logic start here

                        Map<String, String> utr_map = new HashMap<String, String>();
                        if (StringUtils.isNotBlank(document.getString(FieldType.UTR_NO.getName()))) {
                            String[] utrAccSet = document.getString(FieldType.UTR_NO.getName()).split("\\|");
                            for (String accUtr : utrAccSet) {
                                String[] accUtrSplit = accUtr.split(":");
                                utr_map.put(accUtrSplit[0], accUtrSplit[1]);
                            }
                        }

                        //UTR logic end here

                        for (String account : accountDetails) {
                            MISReportObject transReportDuplicateAcNoEntry = (MISReportObject) transReport.clone();
                            // fetch account detail from banksettle based on productid
                            logger.info("Split Report UDF 6 : " + account);
                            String[] pro_acco = account.split("=");
                            logger.info("Split Report UDF 6 : " + pro_acco[0]);
                            List<BankSettle> bankSettleAccount = bankSettleDao.getSplitAccountDetail(pro_acco[0]);
                            logger.info("Split Report UDF 6 : " + bankSettleAccount);
                            if (count != 0) {
                                transReportDuplicateAcNoEntry.setGrossTransactionAmt("0");
                                transReportDuplicateAcNoEntry.setTotalAmount("0");
                                transReportDuplicateAcNoEntry.setAggregatorCommissionAMT("0");
                                //transReportDuplicateAcNoEntry.setTotalAmtPayable("0");
                                transReportDuplicateAcNoEntry
                                        .setTotalAmtPayable(
                                                StringUtils.isNotBlank(pro_acco[1]) ? pro_acco[1] : "0");
                                transReportDuplicateAcNoEntry.setAcquirerCommissionAMT("0");
                                transReportDuplicateAcNoEntry.setTotalPayoutNodalAccount("0");
                            }
                            count++;
                            if (bankSettleAccount.size() > 0) {

                                transReportDuplicateAcNoEntry
                                        .setAccountHolderName(bankSettleAccount.get(0).getAccountHolderName());

                                transReportDuplicateAcNoEntry.setAccountNumber(bankSettleAccount.get(0).getAccountNumber());
                                if (!utr_map.isEmpty() && StringUtils.isNotBlank(utr_map.get(bankSettleAccount.get(0).getAccountNumber()))) {
                                    transReportDuplicateAcNoEntry
                                            .setUtrNo(utr_map.get(bankSettleAccount.get(0).getAccountNumber()));
                                }

                                transReportDuplicateAcNoEntry.setIfsc(bankSettleAccount.get(0).getIfscCode());
                                transReportDuplicateAcNoEntry
                                        .setTotalAmtPayable(
                                                StringUtils.isNotBlank(pro_acco[1]) ? pro_acco[1] : "0");

                                transactionList.add(transReportDuplicateAcNoEntry);
                            }

                        }

                    }
                } else {
                    // User user=userDao.findPayId(userMap.get(key));
                    User user1 = userMap.get(document.getString(FieldType.PAY_ID.toString()));
                    transReport
                            .setAccountHolderName(user1.getAccHolderName());
                    transReport
                            .setAccountNumber(user1.getAccountNo());
                    transReport.setIfsc(user1.getIfscCode());

                    transReport.setUtrNo(document.getString(FieldType.UTR_NO.toString()));
                    transactionList.add(transReport);
                }

            }
            cursor.close();

        } catch (Exception e) {
            logger.error("Exception " + e);
            logger.info("Error occurred !! ", e);
        }
        logger.info("Cursor closed for Settlement report query , transactionList Size = " + transactionList.size());
        logger.info("Split Settled MIS Report = " + new Gson().toJson(transactionList));
        return transactionList;

    }

}
