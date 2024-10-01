package com.pay10.crm.mongoReports;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

import com.mongodb.MongoClient;
import com.pay10.commons.util.*;
import org.apache.commons.lang3.StringUtils;
import org.bson.BsonNull;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.google.gson.Gson;
import com.mongodb.BasicDBObject;
import com.mongodb.BasicDBObjectBuilder;
import com.mongodb.client.AggregateIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import com.pay10.commons.mongo.MongoInstance;
import com.pay10.commons.user.MultCurrencyCodeDao;
import com.pay10.commons.user.POReportDTO;
import com.pay10.commons.user.TransactionSearchNew;
import com.pay10.commons.user.User;
import com.pay10.commons.user.UserDao;
import com.pay10.commons.user.UserType;

@Component
public class PoTxnReport {
    @Autowired
    PropertiesManager propertiesManager;

    @Autowired
    private MongoInstance mongoInstance;
    @Autowired
    private UserDao userdao;

    @Autowired
    private MultCurrencyCodeDao currencyCodeDao;
    private static Map<String, User> userMap = new HashMap<String, User>();

    private static Logger logger = LoggerFactory.getLogger(TxnReports.class.getName());
    private static final String prefix = "MONGO_DB_";

    public int pgReportSearchCount(String pgRefNum, String orderId, String customerEmail, String customerPhone,
                                   String paymentType, String aliasStatus, String currency, String transactionType, String mopType,
                                   String acquirer, String merchantPayId, String fromDate, String toDate, User user, int start, int length,
                                   String tenantId, String ipAddress, String totalAmount, String rrn, String channelName, String minAmount,
                                   String maxAmount, String columnName, String logicalCondition, String searchText, String newDespositor,
                                   String columnName1, String logicalCondition1, String searchText1, String columnName2,
                                   String logicalCondition2, String searchText2) {

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
                List<User> payIdLst = userdao.fetchUsersBasedOnPOEnable();
                logger.info("Get PayId List : " + payIdLst);
                List<String> payIds = payIdLst.stream().map(user1 -> user1.getPayId()).collect(Collectors.toList());
                if (payIds.size() > 0) {
                    paramConditionLst
                            .add(new BasicDBObject(FieldType.PAY_ID.getName(), new BasicDBObject("$in", payIds)));
                }
            } else {
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

            if (channelName.equalsIgnoreCase("All")) {
            } else {
                if (channelName.contains(",")) {
                    String[] channels = channelName.split(",");
                    ArrayList<String> aa = new ArrayList<>();
                    for (String channel : channels) {
                        aa.add(channel);
                    }
                    paramConditionLst.add(new BasicDBObject(FieldType.CHANNEL.getName(), new BasicDBObject("$in", aa)));
                } else {
                    paramConditionLst.add(new BasicDBObject(FieldType.CHANNEL.getName(),
                            new BasicDBObject("$in", Arrays.asList(channelName))));
                }
            }

            if (StringUtils.isNoneBlank(minAmount) && StringUtils.isNoneBlank(maxAmount)) {
                paramConditionLst.add(new BasicDBObject("$expr",
                        new BasicDBObject("$and", Arrays.asList(
                                new BasicDBObject("$gte",
                                        Arrays.asList(new BasicDBObject("$toDouble", "$TOTAL_AMOUNT"),
                                                Double.parseDouble(minAmount))),
                                new BasicDBObject("$lte", Arrays.asList(new BasicDBObject("$toDouble", "$TOTAL_AMOUNT"),
                                        Double.parseDouble(maxAmount)))))));
            }

            if (!columnName.equalsIgnoreCase("") && !logicalCondition.equalsIgnoreCase("")
                    && !searchText.equalsIgnoreCase("")) {
                paramConditionLst.add(new BasicDBObject(columnName, new BasicDBObject(logicalCondition, searchText)));
            }
            if (!columnName1.equalsIgnoreCase("") && !logicalCondition1.equalsIgnoreCase("")
                    && !searchText1.equalsIgnoreCase("")) {
                paramConditionLst
                        .add(new BasicDBObject(columnName1, new BasicDBObject(logicalCondition1, searchText1)));
            }
            if (!columnName2.equalsIgnoreCase("") && !logicalCondition2.equalsIgnoreCase("")
                    && !searchText2.equalsIgnoreCase("")) {
                paramConditionLst
                        .add(new BasicDBObject(columnName2, new BasicDBObject(logicalCondition2, searchText2)));
            }

            // Added by Deep Singh code end here
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

            List<String> custMailIdList = new ArrayList<>();
            if (newDespositor.equalsIgnoreCase("true")) {

                List<Document> matchCase = Arrays.asList(
                        new Document("$match",
                                new Document("CUST_EMAIL", new Document("$ne", new BsonNull())).append("CREATE_DATE",
                                        new Document("$gte", fromDate).append("$lte", toDate))),
                        new Document("$group",
                                new Document("_id", "$CUST_EMAIL").append("transaction_count",
                                        new Document("$sum", 1L))),
                        new Document("$match", new Document("transaction_count", new Document("$eq", 1L))),
                        new Document("$project", new Document("_id", 0L).append("CUST_EMAIL", "$_id")));

                MongoCursor<Document> cursor1 = coll.aggregate(matchCase).iterator();
                while (cursor1.hasNext()) {
                    Document document = (Document) cursor1.next();
                    custMailIdList.add(String.valueOf(document.get("CUST_EMAIL")));

                }
            }

            BasicDBObject match = null;
            BasicDBObject finalquery = null;
            if (newDespositor.equalsIgnoreCase("true")) {
                BasicDBObject dateQuery1 = new BasicDBObject();
                dateQuery1.put(FieldType.CREATE_DATE.getName(),
                        BasicDBObjectBuilder.start("$gte", new SimpleDateFormat(fromDate).toLocalizedPattern())
                                .add("$lte", new SimpleDateFormat(toDate).toLocalizedPattern()).get());
                dateQuery1.put(FieldType.CUST_EMAIL.getName(), new BasicDBObject("$in", custMailIdList));

                match = new BasicDBObject("$match", dateQuery1);
                finalquery = match;
            } else {
                finalquery = new BasicDBObject("$and", fianlList);
                match = new BasicDBObject("$match", finalquery);
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

    public List<TransactionSearchNew> pgReportSearch(String pgRefNum, String orderId, String customerEmail,
                                                     String customerPhone, String paymentType, String aliasStatus, String currency, String transactionType,
                                                     String mopType, String acquirer, String merchantPayId, String fromDate, String toDate, User user, int start,
                                                     int length, String tenantId, String ipAddress, String totalAmount, String rrn, String channelName,
                                                     String minAmount, String maxAmount, String columnName, String logicalCondition, String searchText,
                                                     String newDespositor, String columnName1, String logicalCondition1, String searchText1, String columnName2,
                                                     String logicalCondition2, String searchText2) {

        List<TransactionSearchNew> transactionList = new ArrayList<TransactionSearchNew>();

        try {

            MongoDatabase dbIns = mongoInstance.getDB();
            MongoCollection<Document> coll = dbIns.getCollection(PropertiesManager.propertiesMap.get(prefix + Constants.TRANSACTION_STATUS_COLLECTION.getValue()));

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
                List<User> payIdLst = userdao.fetchUsersBasedOnPOEnable();
                logger.info("Get PayId List : " + payIdLst);
                List<String> payIds = payIdLst.stream().map(user1 -> user1.getPayId()).collect(Collectors.toList());
                System.out.println("Deep List : " + new Gson().toJson(payIds));
                if (payIds.size() > 0) {
                    paramConditionLst
                            .add(new BasicDBObject(FieldType.PAY_ID.getName(), new BasicDBObject("$in", payIds)));
                }
            } else {
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

            if (channelName.equalsIgnoreCase("All")) {
            } else {
                if (channelName.contains(",")) {
                    String[] channels = channelName.split(",");
                    ArrayList<String> aa = new ArrayList<>();
                    for (String channel : channels) {
                        aa.add(channel);
                    }
                    paramConditionLst.add(new BasicDBObject(FieldType.CHANNEL.getName(), new BasicDBObject("$in", aa)));
                } else {
                    paramConditionLst.add(new BasicDBObject(FieldType.CHANNEL.getName(),
                            new BasicDBObject("$in", Arrays.asList(channelName))));
                }

            }

            if (StringUtils.isNoneBlank(minAmount) && StringUtils.isNoneBlank(maxAmount)) {
                paramConditionLst.add(new BasicDBObject("$expr",
                        new BasicDBObject("$and", Arrays.asList(
                                new BasicDBObject("$gte",
                                        Arrays.asList(new BasicDBObject("$toDouble", "$TOTAL_AMOUNT"),
                                                Double.parseDouble(minAmount))),
                                new BasicDBObject("$lte", Arrays.asList(new BasicDBObject("$toDouble", "$TOTAL_AMOUNT"),
                                        Double.parseDouble(maxAmount)))))));
            }

            if (!columnName.equalsIgnoreCase("") && !logicalCondition.equalsIgnoreCase("")
                    && !searchText.equalsIgnoreCase("")) {
                paramConditionLst.add(new BasicDBObject(columnName, new BasicDBObject(logicalCondition, searchText)));
            }

            if (!columnName1.equalsIgnoreCase("") && !logicalCondition1.equalsIgnoreCase("")
                    && !searchText1.equalsIgnoreCase("")) {
                paramConditionLst
                        .add(new BasicDBObject(columnName1, new BasicDBObject(logicalCondition1, searchText1)));
            }

            if (!columnName2.equalsIgnoreCase("") && !logicalCondition2.equalsIgnoreCase("")
                    && !searchText2.equalsIgnoreCase("")) {
                paramConditionLst
                        .add(new BasicDBObject(columnName2, new BasicDBObject(logicalCondition2, searchText2)));
            }

            // Added by Deep Singh code end here

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

            List<String> custEmailIdList = new ArrayList<>();
            if (newDespositor.equalsIgnoreCase("true")) {

                List<Document> matchCase = Arrays.asList(
                        new Document("$match",
                                new Document("CUST_EMAIL", new Document("$ne", new BsonNull())).append("CREATE_DATE",
                                        new Document("$gte", fromDate).append("$lte", toDate))),
                        new Document("$group",
                                new Document("_id", "$CUST_EMAIL").append("transaction_count",
                                        new Document("$sum", 1L))),
                        new Document("$match", new Document("transaction_count", new Document("$eq", 1L))),
                        new Document("$project", new Document("_id", 0L).append("CUST_EMAIL", "$_id")));

                MongoCursor<Document> cursor1 = coll.aggregate(matchCase).iterator();
                while (cursor1.hasNext()) {
                    Document document = (Document) cursor1.next();
                    custEmailIdList.add(String.valueOf(document.get("CUST_EMAIL")));

                }
            }

            BasicDBObject match = null;
            BasicDBObject finalquery = null;
            if (newDespositor.equalsIgnoreCase("true")) {
                BasicDBObject dateQuery1 = new BasicDBObject();
                dateQuery1.put(FieldType.CREATE_DATE.getName(),
                        BasicDBObjectBuilder.start("$gte", new SimpleDateFormat(fromDate).toLocalizedPattern())
                                .add("$lte", new SimpleDateFormat(toDate).toLocalizedPattern()).get());
                dateQuery1.put(FieldType.CUST_EMAIL.getName(), new BasicDBObject("$in", custEmailIdList));

                match = new BasicDBObject("$match", dateQuery1);
                finalquery = match;
            } else {
                finalquery = new BasicDBObject("$and", fianlList);
                match = new BasicDBObject("$match", finalquery);
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
                    transReport.setCurrency(
                            currencyCodeDao.getCurrencyNamebyCode(dbobj.getString(FieldType.CURRENCY_CODE.toString())));
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
                transReport.setStatus(resolveStatus(dbobj.getString(FieldType.STATUS.toString())));
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
                            String.valueOf(((Double.valueOf(dbobj.getString(FieldType.PG_TDR_SC.toString()))))));
                } else {
                    transReport.setPgSurchargeAmount("0.00");
                }

                if (StringUtils.isNotBlank(dbobj.getString(FieldType.ACQUIRER_TDR_SC.toString()))) {
                    transReport.setAcquirerSurchargeAmount(
                            String.valueOf((Double.valueOf(dbobj.getString(FieldType.ACQUIRER_TDR_SC.toString())))));
                } else {
                    transReport.setAcquirerSurchargeAmount("0.00");
                }

                if (StringUtils.isNotBlank(dbobj.getString(FieldType.PG_GST.toString()))) {
                    transReport
                            .setPgGst(String.valueOf(((Double.valueOf(dbobj.getString(FieldType.PG_GST.toString()))))));
                } else {
                    transReport.setPgGst("0.00");
                }

                if (StringUtils.isNotBlank(dbobj.getString(FieldType.ACQUIRER_GST.toString()))) {
                    transReport.setAcquirerGst(
                            String.valueOf((Double.valueOf(dbobj.getString(FieldType.ACQUIRER_GST.toString())))));
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


    public int poReportSearchCount(String fromDate, String toDate, String merchantPayId, User user,
                                   String orderId, String status, String accountNo, String pgRefNum) {

        logger.info(String.format("@@@ POReportSearch Count Parameters - fromDate: %s, toDate: %s, merchantPayId: %s, user: %s, orderId: %s, status: %s, accountNo: %s, pgRefNum: %s",
                fromDate, toDate, merchantPayId, user, orderId, status, accountNo, pgRefNum));
        try {

            MongoDatabase dbIns = mongoInstance.getDB();
            MongoCollection<Document> coll = dbIns.getCollection("POtransactionStatus");
            List<BasicDBObject> paramConditionLst = new ArrayList<BasicDBObject>();
            List<BasicDBObject> dateIndexConditionList = new ArrayList<BasicDBObject>();
            BasicDBObject dateIndexConditionQuery = new BasicDBObject();
            BasicDBObject dateQuery = new BasicDBObject();


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
            if (StringUtils.isNotBlank(PropertiesManager.propertiesMap.get(Constants.USE_DATE_INDEX.getValue())) &&
                    PropertiesManager.propertiesMap.get(Constants.USE_DATE_INDEX.getValue()).equalsIgnoreCase("Y")) {
                dateIndexConditionQuery.append("$or", dateIndexConditionList);
            }

            if (merchantPayId.equalsIgnoreCase("ALL")) {
                logger.info("TxnReport, MerchantId : " + merchantPayId + ", Segment : " + user.getSegment());
                List<User> payIdLst = userdao.fetchUsersBasedOnPOEnable();
                logger.info("Get PayId List : " + payIdLst);
                List<String> payIds = payIdLst.stream().map(user1 -> user1.getPayId()).collect(Collectors.toList());
                if (payIds.size() > 0) {
                    paramConditionLst
                            .add(new BasicDBObject(FieldType.PAY_ID.getName(), new BasicDBObject("$in", payIds)));
                }
            } else {
                paramConditionLst.add(new BasicDBObject(FieldType.PAY_ID.getName(), merchantPayId));
            }

            // Start Change By Pritam Ray
            if (StringUtils.isNotEmpty(orderId)) {
                paramConditionLst.add(new BasicDBObject(FieldType.ORDER_ID.getName(), orderId));
            }

            if (StringUtils.isNotEmpty(accountNo)) {
                paramConditionLst.add(new BasicDBObject(FieldType.ACC_NO.getName(), accountNo));
            }

            if (StringUtils.isNotEmpty(status) && !status.equalsIgnoreCase("All")) {
                paramConditionLst.add(new BasicDBObject(FieldType.STATUS.getName(), status.equalsIgnoreCase("Request Accepted") ? new BasicDBObject("$in", Arrays.asList("Pending", "Sent to Bank")) : status));
            }

            if (StringUtils.isNotEmpty(pgRefNum)) {
                paramConditionLst.add(new BasicDBObject(FieldType.PG_REF_NUM.getName(), pgRefNum));
            }
            // End Change By Pritam Ray


            List<BasicDBObject> allConditionQueryList = new ArrayList<BasicDBObject>();
            if (!dateQuery.isEmpty()) {
                allConditionQueryList.add(dateQuery);
            }
            if (!dateIndexConditionQuery.isEmpty()) {
                allConditionQueryList.add(dateIndexConditionQuery);
            }

            List<BasicDBObject> fianlList = new ArrayList<BasicDBObject>();
            if (!allConditionQueryList.isEmpty()) {
                BasicDBObject allConditionQueryObj = new BasicDBObject("$and", allConditionQueryList);
                if (!allConditionQueryObj.isEmpty()) {
                    fianlList.add(allConditionQueryObj);
                }
            }

            BasicDBObject allParamQuery = new BasicDBObject();

            if (!paramConditionLst.isEmpty()) {
                allParamQuery = new BasicDBObject("$and", paramConditionLst);
            }

            if (!allParamQuery.isEmpty()) {
                fianlList.add(allParamQuery);
            }

            BasicDBObject finalquery = new BasicDBObject("$and", fianlList);
            BasicDBObject match = new BasicDBObject("$match", finalquery);

            return coll.aggregate(Arrays.asList(match)).into(new ArrayList<>()).size();

        } catch (Exception e) {
            logger.info("Exception in POTxnReport " + e);
        }
        return 0;
    }

    public List<POReportDTO> poReportSearch(String fromDate, String toDate, String merchantPayId, int start, int length, User user,
                                            String orderId, String status, String accountNo, String pgRefNum) {

        logger.info(String.format("POReport Search Parameters - fromDate: %s, toDate: %s, merchantPayId: %s, start: %d, length: %d, user: %s, orderId: %s, status: %s, accountNo: %s, pgRefNum: %s",
                fromDate, toDate, merchantPayId, start, length, user, orderId, status, accountNo, pgRefNum));

        List<POReportDTO> poReportDTOs = new ArrayList<POReportDTO>();
        try {
            System.out.println("COLLECTION NAME : A ");

            MongoDatabase dbIns = mongoInstance.getDB();
            MongoCollection<Document> coll = dbIns.getCollection("POtransactionStatus");

            List<BasicDBObject> paramConditionLst = new ArrayList<BasicDBObject>();
            List<BasicDBObject> dateIndexConditionList = new ArrayList<BasicDBObject>();
            BasicDBObject dateIndexConditionQuery = new BasicDBObject();
            BasicDBObject dateQuery = new BasicDBObject();

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
            if (StringUtils.isNotBlank(PropertiesManager.propertiesMap.get(Constants.USE_DATE_INDEX.getValue())) && PropertiesManager.propertiesMap.get(Constants.USE_DATE_INDEX.getValue()).equalsIgnoreCase("Y")) {
                dateIndexConditionQuery.append("$or", dateIndexConditionList);
            }

            if (merchantPayId.equalsIgnoreCase("ALL")) {
                logger.info("TxnReport, MerchantId : " + merchantPayId + ", Segment : " + user.getSegment());
                List<User> payIdLst = userdao.fetchUsersBasedOnPOEnable();
                logger.info("Get PayId List : " + payIdLst);
                List<String> payIds = payIdLst.stream().map(user1 -> user1.getPayId()).collect(Collectors.toList());
                if (payIds.size() > 0) {
                    paramConditionLst.add(new BasicDBObject(FieldType.PAY_ID.getName(), new BasicDBObject("$in", payIds)));
                }
            } else {
                paramConditionLst.add(new BasicDBObject(FieldType.PAY_ID.getName(), merchantPayId));
            }

            // Start Change By Pritam Ray
            if (StringUtils.isNotEmpty(orderId)) {
                paramConditionLst.add(new BasicDBObject(FieldType.ORDER_ID.getName(), orderId));
            }

            if (StringUtils.isNotEmpty(accountNo)) {
                paramConditionLst.add(new BasicDBObject(FieldType.ACC_NO.getName(), accountNo));
            }

            if (StringUtils.isNotEmpty(status) && !status.equalsIgnoreCase("All")) {
                paramConditionLst.add(new BasicDBObject(FieldType.STATUS.getName(), status.equalsIgnoreCase("Request Accepted") ? new BasicDBObject("$in", Arrays.asList("Pending", "Sent to Bank")) : status));
            }

            if (StringUtils.isNotEmpty(pgRefNum)) {
                paramConditionLst.add(new BasicDBObject(FieldType.PG_REF_NUM.getName(), pgRefNum));
            }

            // End Change By Pritam Ray

            List<BasicDBObject> allConditionQueryList = new ArrayList<BasicDBObject>();
            if (!dateQuery.isEmpty()) {
                allConditionQueryList.add(dateQuery);
            }
            if (!dateIndexConditionQuery.isEmpty()) {
                allConditionQueryList.add(dateIndexConditionQuery);
            }

            List<BasicDBObject> fianlList = new ArrayList<BasicDBObject>();

            if (!allConditionQueryList.isEmpty()) {
                BasicDBObject allConditionQueryObj = new BasicDBObject("$and", allConditionQueryList);
                if (!allConditionQueryObj.isEmpty()) {
                    fianlList.add(allConditionQueryObj);
                }
            }

            BasicDBObject allParamQuery = new BasicDBObject();

            if (!paramConditionLst.isEmpty()) {
                allParamQuery = new BasicDBObject("$and", paramConditionLst);
            }

            if (!allParamQuery.isEmpty()) {
                fianlList.add(allParamQuery);
            }

            BasicDBObject finalquery = new BasicDBObject("$and", fianlList);
            BasicDBObject match = new BasicDBObject("$match", finalquery);


            BasicDBObject sort = new BasicDBObject("$sort", new BasicDBObject("CREATE_DATE", -1));
            BasicDBObject skip = new BasicDBObject("$skip", start);
            BasicDBObject limit = new BasicDBObject("$limit", length);


            List<Bson> pipeline = Arrays.asList(match, sort, skip, limit);

            // Log the pipeline components
            logger.info("@@@@ Admin MongoDB Aggregation Pipeline Query : " + pipeline);

            AggregateIterable<Document> output = coll.aggregate(pipeline);
            output.allowDiskUse(true);
            MongoCursor<Document> cursor = output.iterator();

            while (cursor.hasNext()) {
                Document document = cursor.next();
                System.out.println(document);
                POReportDTO pOReportDTO = new POReportDTO();
                pOReportDTO.setAccountBankName(document.getString("ACC_NAME"));
                pOReportDTO.setAccountCityName(document.getString("ACC_CITY_NAME"));
                pOReportDTO.setAccountNo(document.getString("ACC_NO"));
                pOReportDTO.setAccountProvince(document.getString("ACC_PROVINCE"));
                pOReportDTO.setAccountType(document.getString("ACC_TYPE"));
                pOReportDTO.setAcquireType(StringUtils.isNotBlank(document.getString("ACQUIRER_TYPE")) ? document.getString("ACQUIRER_TYPE") : "NA");
                pOReportDTO.setAmount(document.getString("AMOUNT"));

                pOReportDTO.setBankBranch(document.getString("BANK_BRANCH"));
                pOReportDTO.setBankCode(document.getString("BANK_CODE"));

                pOReportDTO.setIpaddress(document.getString("CLIENT_IP"));
                pOReportDTO.setDateFrom(document.getString("CREATE_DATE"));
                pOReportDTO.setCurrencyCode(document.getString("CURRENCY_CODE"));

                pOReportDTO.setMerchantName(document.getString("MERCHANT_NAME"));

                pOReportDTO.setOrderId(document.getString("ORDER_ID"));

                pOReportDTO.setPayerName(document.getString("PAYER_NAME"));
                pOReportDTO.setCustomerPhone(document.getString("PAYER_PHONE"));
                pOReportDTO.setPayId(document.getString("PAY_ID"));
                pOReportDTO.setMopType(document.getString("PAY_TYPE"));
                pOReportDTO.setPgRefNum(document.getString("PG_REF_NUM"));

                pOReportDTO.setRemark(document.getString("REMARKS"));
                pOReportDTO.setResponseCode(document.getString("RESPONSE_CODE"));
                pOReportDTO.setResponseMessage(document.getString("RESPONSE_MESSAGE"));

                pOReportDTO.setStatus(resolveStatus(document.getString("STATUS")));

                pOReportDTO.setTxnType(document.getString("TXNTYPE"));

                poReportDTOs.add(pOReportDTO);
            }

        } catch (Exception e) {
            logger.info("Exception in POTxnReport " + e);
        }
        return poReportDTOs;
    }

    public String resolveStatus(String status) {
        logger.info("resolveStatus : {}", status);
        if (StringUtils.isNotBlank(status) && (status.equalsIgnoreCase(StatusType.CAPTURED.getName()) || status.equalsIgnoreCase(StatusType.SETTLED_RECONCILLED.getName()) || status.equalsIgnoreCase(StatusType.SETTLED_SETTLE.getName()) || status.equalsIgnoreCase(StatusType.FAILED.getName()))) {
            logger.info("resolveStatus Return: {}", status);
            return status;
        } else if (StringUtils.isNotBlank(status) && (status.equalsIgnoreCase(StatusType.DENIED.getName()))) {
            logger.info("resolveStatus Return: {}", "REQUEST ACCEPTED");
            return "REQUEST ACCEPTED";
        } else if (StringUtils.isNotBlank(status) && (status.equalsIgnoreCase("REQUEST ACCEPTED") || status.equalsIgnoreCase("Pending") || status.equalsIgnoreCase(StatusType.SENT_TO_BANK.getName()))) {
            logger.info("resolveStatus Return: {}", "REQUEST ACCEPTED");
            return "REQUEST ACCEPTED";
        } else {
            logger.info("resolveStatus Return: {}", StatusType.FAILED.getName());
            return StatusType.FAILED.getName();
        }
    }

}
