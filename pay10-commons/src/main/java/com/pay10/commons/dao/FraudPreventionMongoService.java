package com.pay10.commons.dao;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ibm.icu.text.DateFormat;
import com.ibm.icu.text.SimpleDateFormat;
import com.ibm.icu.util.Calendar;
import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.result.UpdateResult;
import com.pay10.commons.entity.FraudManagementDto;
import com.pay10.commons.exception.DataAccessLayerException;
import com.pay10.commons.exception.ErrorType;
import com.pay10.commons.exception.SystemException;
import com.pay10.commons.mongo.MongoInstance;
import com.pay10.commons.user.*;
import com.pay10.commons.util.*;
import org.apache.commons.lang3.StringUtils;
import org.bson.Document;
import org.hibernate.HibernateException;
import org.hibernate.ObjectNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.text.DecimalFormat;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

@Service
public class FraudPreventionMongoService {

    @Autowired
    private MongoInstance mongoInstance;

    private ErrorType responseErrorType;

    @Autowired
    private ResponseObject ruleCheckResponse;

    @Autowired
    private ObjectMapper mapper;

    @Autowired
    private UserDao userDao;

    @Autowired
    private MultCurrencyCodeDao multCurrencyCodeDao;


    private static final String prefix = "MONGO_DB_";

    private static final String PREFIX_CONFIG = "FRAUD_CONFIG_";

    private static Logger logger = LoggerFactory.getLogger(FraudPreventionMongoService.class.getName());

    public void create(FraudPreventionObj fraudPreventionObj) throws SystemException {

        String currentDate = null;
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Calendar cal = Calendar.getInstance();
        currentDate = dateFormat.format(cal.getTime());
        MongoCollection<Document> coll = getCollection();
        Document doc = new Document();
        String id = TransactionManager.getNewTransactionId();
        if (StringUtils.isNotBlank(fraudPreventionObj.getId())) {
            id = fraudPreventionObj.getId();
        } else {
            FraudPreventionObj fraudPreventionDbObj = fpRulefindById(id, fraudPreventionObj.getCurrency());
            if (!ObjectUtils.isEmpty(fraudPreventionDbObj)) {
                logger.info("Create Fraud Rule By Id Already Exists : " + id + " generating new Id");
                id = TransactionManager.getTimestampId();
            }
        }

        if (!fraudPreventionObj.isFixedAmountFlag()) {
            if (null != fraudPreventionObj.getMaxTransactionAmount()) {
                double maxAmount = Double.parseDouble(fraudPreventionObj.getMaxTransactionAmount());
                DecimalFormat df = new DecimalFormat("#.00");
                fraudPreventionObj.setMaxTransactionAmount(df.format(maxAmount));
            }
            if (null != fraudPreventionObj.getMinTransactionAmount()) {
                double minAmount = Double.parseDouble(fraudPreventionObj.getMinTransactionAmount());
                DecimalFormat df = new DecimalFormat("#.00");
                fraudPreventionObj.setMinTransactionAmount(df.format(minAmount));
            }
        }
        try {
            doc.put("alwaysOnFlag", fraudPreventionObj.isAlwaysOnFlag());
            doc.put("createDate", currentDate);
            logger.info("Create Merchant Currency: " + fraudPreventionObj.getCurrency());
            //   doc.put("currency", fraudPreventionObj.getMerchantCurrency());
            doc.put("dateActiveFrom", fraudPreventionObj.getDateActiveFrom());
            doc.put("dateActiveTo", fraudPreventionObj.getDateActiveTo());
            doc.put("email", fraudPreventionObj.getEmail());
            doc.put("endTime", fraudPreventionObj.getEndTime());
            doc.put("fraudType", fraudPreventionObj.getFraudType().getValue());
            doc.put("ipAddress", fraudPreventionObj.getIpAddress());
            doc.put("issuerCountry", fraudPreventionObj.getIssuerCountry());
            doc.put("maxTransactionAmount", fraudPreventionObj.getMaxTransactionAmount());
            doc.put("minTransactionAmount", fraudPreventionObj.getMinTransactionAmount());
            doc.put("minutesTxnLimit", fraudPreventionObj.getMinutesTxnLimit());
            doc.put("negativeBin", fraudPreventionObj.getNegativeBin());
            doc.put("negativeCard", fraudPreventionObj.getNegativeCard());
            doc.put("noOfTransactionAllowed", fraudPreventionObj.getNoOfTransactionAllowed());
            doc.put("payId", fraudPreventionObj.getPayId());
            doc.put("perCardTransactionAllowed", fraudPreventionObj.getPerCardTransactionAllowed());
            doc.put("repeatDays", fraudPreventionObj.getRepeatDays());
            doc.put("startTime", fraudPreventionObj.getStartTime());
            doc.put("status", TDRStatus.ACTIVE.getName());
            doc.put("subnetMask", fraudPreventionObj.getSubnetMask());
            doc.put("updateDate", currentDate);
            doc.put("userCountry", fraudPreventionObj.getUserCountry());
            doc.put("phone", fraudPreventionObj.getPhone());
            doc.put("userIdentifier", fraudPreventionObj.getUserIdentifier());
            doc.put("createdBy", fraudPreventionObj.getCreatedBy());
            doc.put("updatedBy", fraudPreventionObj.getCreatedBy());
            doc.put("mackAddress", fraudPreventionObj.getMackAddress());
            doc.put("transactionAmount", fraudPreventionObj.getTransactionAmount());
            doc.put("repetationCount", fraudPreventionObj.getRepetationCount());
            doc.put("blockTimeUnits", fraudPreventionObj.getBlockTimeUnits());
            doc.put("fixedAmountFlag", fraudPreventionObj.isFixedAmountFlag());
            doc.put("emailToNotify", fraudPreventionObj.getEmailToNotify());
            doc.put("paymentType", fraudPreventionObj.getPaymentType());
            doc.put("percentageOfRepeatedMop", fraudPreventionObj.getPercentageOfRepeatedMop());
            doc.put("vpa", fraudPreventionObj.getVpa());
            doc.put("statusVelocity", fraudPreventionObj.getStatusVelocity());
            doc.put("monitoringType", fraudPreventionObj.getMonitoringType());
            doc.put("notified", fraudPreventionObj.isNotified());
            doc.put("stateCode", fraudPreventionObj.getStateCode());
            doc.put("country", fraudPreventionObj.getCountry());
            doc.put("state", fraudPreventionObj.getState());
            doc.put("city", fraudPreventionObj.getCity());
            doc.put("parentId", null);

            doc.put("blockDailyType", "N");
            doc.put("blockWeeklyType", "N");
            doc.put("blockMonthlyType", "N");

            // Added By Sweety
            if (fraudPreventionObj.getMerchantProfile() != null) {
                doc.put("blockDaily", fraudPreventionObj.getBlockDaily());
                doc.put("blockWeekly", fraudPreventionObj.getBlockWeekly());
                doc.put("blockMonthly", fraudPreventionObj.getBlockMonthly());
                doc.put("dailyAmount", fraudPreventionObj.getDailyAmount());
                doc.put("weeklyAmount", fraudPreventionObj.getWeeklyAmount());
                doc.put("monthlyAmount", fraudPreventionObj.getMonthlyAmount());
                doc.put("FRM_Merchant_Profile", fraudPreventionObj.getMerchantProfile());
            }
            doc.put("vpaAddress", fraudPreventionObj.getVpaAddress());
            List<String>currencyList=getCurrency(fraudPreventionObj.getPayId(), fraudPreventionObj.getCurrency());
            for (int i = 0; i <currencyList.size(); i++) {
                logger.info("!!!!!!! Creating New Entry for: " + currencyList.get(i));
                doc.put("_id", id);
                doc.put("id", id);
                doc.put("currency", currencyList.get(i));
                 coll.insertOne(doc);
                id = TransactionManager.getNewTransactionId();

            }

            if (StringUtils.equalsIgnoreCase(fraudPreventionObj.getPayId(), "all")) {
                doc.put("parentId", id);
                createDocForAllActiveMerchant(doc, fraudPreventionObj.getCurrency());
            }
        } catch (Exception e) {
            logger.error("Exception in getting Fraud Prevention ", e);
        }

    }

    public void createIndividualMerchant(FraudPreventionObj fraudPreventionObj) throws SystemException {

        String currentDate = null;
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Calendar cal = Calendar.getInstance();
        currentDate = dateFormat.format(cal.getTime());
        MongoCollection<Document> coll = getCollection();
        Document doc = new Document();
        String id = TransactionManager.getNewTransactionId();
        if (StringUtils.isNotBlank(fraudPreventionObj.getId())) {
            id = fraudPreventionObj.getId();
        } else {
            FraudPreventionObj fraudPreventionDbObj = fpRulefindById(id, fraudPreventionObj.getCurrency());
            if (!ObjectUtils.isEmpty(fraudPreventionDbObj)) {
                logger.info("Create Fraud Rule By Id Already Exists : " + id + " generating new Id");
                id = TransactionManager.getTimestampId();
            }
        }

        if (!fraudPreventionObj.isFixedAmountFlag()) {
            if (null != fraudPreventionObj.getMaxTransactionAmount()) {
                double maxAmount = Double.parseDouble(fraudPreventionObj.getMaxTransactionAmount());
                DecimalFormat df = new DecimalFormat("#.00");
                fraudPreventionObj.setMaxTransactionAmount(df.format(maxAmount));
            }
            if (null != fraudPreventionObj.getMinTransactionAmount()) {
                double minAmount = Double.parseDouble(fraudPreventionObj.getMinTransactionAmount());
                DecimalFormat df = new DecimalFormat("#.00");
                fraudPreventionObj.setMinTransactionAmount(df.format(minAmount));
            }
        }
        try {
            doc.put("alwaysOnFlag", fraudPreventionObj.isAlwaysOnFlag());
            doc.put("createDate", currentDate);
            logger.info("Create Merchant Currency: " + fraudPreventionObj.getCurrency());
            //   doc.put("currency", fraudPreventionObj.getMerchantCurrency());
            doc.put("dateActiveFrom", fraudPreventionObj.getDateActiveFrom());
            doc.put("dateActiveTo", fraudPreventionObj.getDateActiveTo());
            doc.put("email", fraudPreventionObj.getEmail());
            doc.put("endTime", fraudPreventionObj.getEndTime());
            doc.put("fraudType", fraudPreventionObj.getFraudType().getValue());
            doc.put("ipAddress", fraudPreventionObj.getIpAddress());
            doc.put("issuerCountry", fraudPreventionObj.getIssuerCountry());
            doc.put("maxTransactionAmount", fraudPreventionObj.getMaxTransactionAmount());
            doc.put("minTransactionAmount", fraudPreventionObj.getMinTransactionAmount());
            doc.put("minutesTxnLimit", fraudPreventionObj.getMinutesTxnLimit());
            doc.put("negativeBin", fraudPreventionObj.getNegativeBin());
            doc.put("negativeCard", fraudPreventionObj.getNegativeCard());
            doc.put("noOfTransactionAllowed", fraudPreventionObj.getNoOfTransactionAllowed());
            doc.put("payId", fraudPreventionObj.getPayId());
            doc.put("perCardTransactionAllowed", fraudPreventionObj.getPerCardTransactionAllowed());
            doc.put("repeatDays", fraudPreventionObj.getRepeatDays());
            doc.put("startTime", fraudPreventionObj.getStartTime());
            doc.put("status", TDRStatus.ACTIVE.getName());
            doc.put("subnetMask", fraudPreventionObj.getSubnetMask());
            doc.put("updateDate", currentDate);
            doc.put("userCountry", fraudPreventionObj.getUserCountry());
            doc.put("phone", fraudPreventionObj.getPhone());
            doc.put("userIdentifier", fraudPreventionObj.getUserIdentifier());
            doc.put("createdBy", fraudPreventionObj.getCreatedBy());
            doc.put("updatedBy", fraudPreventionObj.getCreatedBy());
            doc.put("mackAddress", fraudPreventionObj.getMackAddress());
            doc.put("transactionAmount", fraudPreventionObj.getTransactionAmount());
            doc.put("repetationCount", fraudPreventionObj.getRepetationCount());
            doc.put("blockTimeUnits", fraudPreventionObj.getBlockTimeUnits());
            doc.put("fixedAmountFlag", fraudPreventionObj.isFixedAmountFlag());
            doc.put("emailToNotify", fraudPreventionObj.getEmailToNotify());
            doc.put("paymentType", fraudPreventionObj.getPaymentType());
            doc.put("percentageOfRepeatedMop", fraudPreventionObj.getPercentageOfRepeatedMop());
            doc.put("vpa", fraudPreventionObj.getVpa());
            doc.put("statusVelocity", fraudPreventionObj.getStatusVelocity());
            doc.put("monitoringType", fraudPreventionObj.getMonitoringType());
            doc.put("notified", fraudPreventionObj.isNotified());
            doc.put("stateCode", fraudPreventionObj.getStateCode());
            doc.put("country", fraudPreventionObj.getCountry());
            doc.put("state", fraudPreventionObj.getState());
            doc.put("city", fraudPreventionObj.getCity());
            doc.put("parentId", null);

            doc.put("blockDailyType", "N");
            doc.put("blockWeeklyType", "N");
            doc.put("blockMonthlyType", "N");

            // Added By Sweety
            if (fraudPreventionObj.getMerchantProfile() != null) {
                doc.put("blockDaily", fraudPreventionObj.getBlockDaily());
                doc.put("blockWeekly", fraudPreventionObj.getBlockWeekly());
                doc.put("blockMonthly", fraudPreventionObj.getBlockMonthly());
                doc.put("dailyAmount", fraudPreventionObj.getDailyAmount());
                doc.put("weeklyAmount", fraudPreventionObj.getWeeklyAmount());
                doc.put("monthlyAmount", fraudPreventionObj.getMonthlyAmount());
                doc.put("FRM_Merchant_Profile", fraudPreventionObj.getMerchantProfile());
            }
            doc.put("vpaAddress", fraudPreventionObj.getVpaAddress());
            List<String>currencyList=getCurrency(fraudPreventionObj.getPayId(), fraudPreventionObj.getCurrency());
            for (int i = 0; i <currencyList.size(); i++) {
                logger.info("!!!!!!! Creating New Entry for: " + currencyList.get(i));
                doc.put("_id", id);
                doc.put("id", id);
                doc.put("currency", currencyList.get(i));
                coll.insertOne(doc);
                id = TransactionManager.getNewTransactionId();

            }


        } catch (Exception e) {
            logger.error("Exception in getting Fraud Prevention ", e);
        }

    }
    private MongoCollection<Document> getCollection() {
        MongoDatabase dbIns = mongoInstance.getDB();
        MongoCollection<Document> coll = dbIns.getCollection(
                PropertiesManager.propertiesMap.get(prefix + Constants.FRAUD_PREVENTION_COLLECTION_NAME.getValue()));
        return coll;
    }



  //  private static ExecutorService executors = Executors.newFixedThreadPool(10);
    @SuppressWarnings("unused")
    private void createDocForAllActiveMerchant(Document doc, String currency) {
        int INSERT_SIZE = 500;
        MongoCollection<Document> coll = getCollection();
        List<Document> docs = new ArrayList<>();
        List<Merchants> merchants = userDao.getActiveMerchantListPgWs();
        logger.info("Size of active merchant: "+merchants.size());
        AtomicInteger count= new AtomicInteger();
        merchants.parallelStream().forEach(merchant -> {

            Document document = new Document();
            logger.info("Merchant count "+(count.getAndIncrement()));
            logger.info("Document to insert for All Active Merchant{} ", doc);
            document.putAll(doc);
            String id = TransactionManager.getNewTransactionId();
            FraudPreventionObj fraudPreventionDbObj = fpRulefindById(id, currency);
            if (!ObjectUtils.isEmpty(fraudPreventionDbObj)) {
                logger.info("Create for all active Fraud Rule By Id Already Exists : " + id + " generating new Id");
                id = TransactionManager.getTimestampId();
            }
            document.put("_id", id);
            document.put("id", id);
            document.put("payId", merchant.getPayId());

            docs.add(document);

           /* if( docs.size() % INSERT_SIZE == 0){
                coll.insertMany(docs);
                docs.clear();
            }
            */

            //Running in sequeence
           FraudPreventationConfiguration configuration = new FraudPreventationConfiguration();
            configuration = getConfigByPayId(merchant.getPayId(), currency);
            if (configuration != null) {

                    configuration=getConfigByFraudType(doc);
                //if (configuration.isOnIpBlocking() == false || configuration.isOnVpaAddressBlocking() == false) {
                    configuration.setId(merchant.getPayId());
                    logger.info("Configuration to be update {} ",configuration);
                   // configuration.setOnIpBlocking(true);
                   // configuration.setOnVpaAddressBlocking(true);
                    updateConfigAll(merchant.getPayId(), currency, configuration);
                //}
            }
            if (configuration == null) {
                configuration = new FraudPreventationConfiguration();
                configuration=getConfigByFraudType(doc);
                configuration.setId(merchant.getPayId());
               // configuration.setOnIpBlocking(true);
                //configuration.setOnVpaAddressBlocking(true);
                logger.info("Configuration to be insert {} ",configuration);

                createConfigDoc(configuration, currency);
            }
        }); //End of stream
        coll.insertMany(docs);
    }

    private FraudPreventationConfiguration getConfigByFraudType(Document doc){
        FraudPreventationConfiguration configuration=new FraudPreventationConfiguration();
        logger.info("FRAUD_TYPE: "+doc.getString("fraudType"));
        switch (doc.getString("fraudType")){
            case "BLOCK_IP_ADDRESS" :
                logger.info("IP Address Enabled");
                configuration.setOnIpBlocking(true);
                break;
            case "BLOCK_USER_COUNTRY":
                logger.info("BLOCK_USER_COUNTRY Enabled");
                configuration.setOnUserCountriesBlocking(true);
                break;
            case "BLOCK_EMAIL_ID":
                logger.info("BLOCK_EMAIL_ID Enabled");
                configuration.setOnEmailBlocking(true);
                break;
            case "BLOCK_NO_OF_TXNS":
                logger.info("BLOCK_NO_OF_TXNS Enabled");
                configuration.setOnLimitCardTxnBlocking(true);
                break;
            case "BLOCK_TXN_AMOUNT":
                logger.info("BLOCK_TXN_AMOUNT Enabled");
                configuration.setOnLimitTxnAmtBlocking(true);
                break;
            case "BLOCK_CARD_ISSUER_COUNTRY":
                logger.info("BLOCK_CARD_ISSUER_COUNTRY Enabled");
                configuration.setOnIssuerCountriesBlocking(true);
                break;
            case "BLOCK_CARD_BIN":
                logger.info("BLOCK_CARD_BIN Enabled");
                configuration.setOnCardRangeBlocking(true);
                break;
            case "BLOCK_CARD_NO":
                logger.info("BLOCK_CARD_NO Enabled");
                configuration.setOnCardMaskBlocking(true);
                break;
            case "BLOCK_PHONE_NUMBER":
                logger.info("BLOCK_PHONE_NUMBER Enabled");
                configuration.setOnPhoneNoBlocking(true);
                break;
            case "BLOCK_TXN_AMOUNT_VELOCITY":
                logger.info("BLOCK_TXN_AMOUNT_VELOCITY Enabled");
                configuration.setOnTxnAmountVelocityBlocking(true);
                break;
            case "BLOCK_MACK_ADDRESS":
                logger.info("BLOCK_MACK_ADDRESS Enabled");
                configuration.setOnMacBlocking(true);
                break;
            case "REPEATED_MOP_TYPES":
                logger.info("REPEATED_MOP_TYPES Enabled");
                configuration.setOnNotifyRepeatedMopType(true);
                break;
            case "BLOCK_USER_STATE":
                logger.info("BLOCK_USER_STATE Enabled");
                configuration.setOnStateBlocking(true);
                break;
            case "BLOCK_USER_CITY":
                logger.info("BLOCK_USER_CITY Enabled");
                configuration.setOnCityBlocking(true);
                break;
            case "BLOCK_REPEATED_MOP_TYPE_FOR_SAME_DETAIL":
                logger.info("BLOCK_REPEATED_MOP_TYPE_FOR_SAME_DETAIL Enabled");
                configuration.setOnBlockRepeatedMopTypeForSameDetails(true);
                break;
            case "BLOCK_TRANSACTION_LIMIT":
                logger.info("BLOCK_TRANSACTION_LIMIT Enabled");
                configuration.setOnLimitTxnAmtBlocking(true);
                break;
            case "BLOCK_VPA_ADDRESS":
                logger.info("BLOCK_VPA_ADDRESS Enabled");
                configuration.setOnVpaAddressBlocking(true);
                break;
        }
        return configuration;

    }
    private void updateConfigAll(String payId, String currency, FraudPreventationConfiguration configuration) {
        if (configuration.isOnIpBlocking()) {
            updateConfigDoc(payId, currency, "onIpBlocking", true);
        }
        if (configuration.isOnVpaAddressBlocking()) {
            updateConfigDoc(payId, currency, "onVpaAddressBlocking", true);
        }
        if(configuration.isOnLimitTxnAmtBlocking()){
            updateConfigDoc(payId, currency, "onLimitCardTxnBlocking", true);

        }
        if(configuration.isOnIssuerCountriesBlocking()){
            updateConfigDoc(payId, currency, "onIssuerCountriesBlocking", true);

        }
        if(configuration.isOnPhoneNoBlocking()){
            updateConfigDoc(payId, currency, "onPhoneNoBlocking", true);

        }
        if(configuration.isOnIpBlocking()){
            updateConfigDoc(payId, currency, "onIpBlocking", true);

        }
         if(configuration.isOnStateBlocking()){
            updateConfigDoc(payId, currency, "onStateBlocking", true);

        }
        if(configuration.isOnMacBlocking()){
            updateConfigDoc(payId, currency, "onMacBlocking", true);

        }
        if(configuration.isOnEmailBlocking()){
            updateConfigDoc(payId, currency, "onEmailBlocking", true);

        }
        if(configuration.isOnUserCountriesBlocking()){
            updateConfigDoc(payId, currency, "onUserCountriesBlocking", true);

        }
        if(configuration.isOnTxnAmountVelocityBlocking()){
            updateConfigDoc(payId, currency, "onTxnAmountVelocityBlocking", true);

        }
        if(configuration.isOnLimitTxnAmtBlocking()){
            updateConfigDoc(payId, currency, "onLimitTxnAmtBlocking", true);

        }
        if(configuration.isOnBlockRepeatedMopTypeForSameDetails()){
            updateConfigDoc(payId, currency, "onBlockRepeatedMopTypeForSameDetails", true);

        }
        if(configuration.isOnCardRangeBlocking()){
            updateConfigDoc(payId, currency, "onCardRangeBlocking", true);

        }
        if(configuration.isOnNotifyRepeatedMopType()){

            updateConfigDoc(payId, currency, "onNotifyRepeatedMopType", true);
        }
        if(configuration.isOnCardMaskBlocking()){
            updateConfigDoc(payId, currency, "onCardMaskBlocking", true);

        }
        if(configuration.isOnCityBlocking()){
            updateConfigDoc(payId, currency, "onCityBlocking", true);

        }

    }

    public void delete(FraudPreventionObj fraudPreventionObj) throws DataAccessLayerException {
        try {
            MongoCollection<Document> coll = getCollection();
            BasicDBObject idQuery = new BasicDBObject("id", fraudPreventionObj.getId());
            coll.deleteOne(idQuery);
            if (StringUtils.equalsIgnoreCase(fraudPreventionObj.getPayId(), "all")) {
                deleteDocForAllActiveMerchants(fraudPreventionObj.getId());
            }
        } catch (Exception e) {
            logger.error("Exception in Deleting Fraud Prevention ", e);
        }

    }

    private void deleteDocForAllActiveMerchants(String parentId) {
        try {
            MongoCollection<Document> coll = getCollection();
            BasicDBObject parentIdQuery = new BasicDBObject("parentId", parentId);
            coll.deleteMany(parentIdQuery);
        } catch (Exception ex) {
            logger.error("deleteDocForAllActiveMerchants:: failed", ex);
        }
    }

    public void update(FraudPreventionObj fraudPreventionObj) throws DataAccessLayerException {
        try {
           // logger.info("get Currency for bulk delete " + fraudPreventionObj.getMerchantCurrency());
            String currentDate = null;
            String currency=fraudPreventionObj.getCurrency();
            String payId=fraudPreventionObj.getPayId();
            logger.info("payId update: "+payId);
            logger.info("currrency: "+currency);
            DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Calendar cal = Calendar.getInstance();
            currentDate = dateFormat.format(cal.getTime());

            MongoDatabase dbIns = mongoInstance.getDB();
            MongoCollection<Document> coll = dbIns.getCollection(PropertiesManager.propertiesMap
                    .get(prefix + Constants.FRAUD_PREVENTION_COLLECTION_NAME.getValue()));

            // Instead of deleting , Make the Object as In Active.
            List<BasicDBObject> filterQuery = new ArrayList<>();
            Document doc = new Document();
            doc.put("id", fraudPreventionObj.getId());
            doc.put("payId", fraudPreventionObj.getPayId());

          //  doc.put("currency", fraudPreventionObj.getMerchantCurrency());
            doc.put("status", TDRStatus.INACTIVE.getName());
            doc.put("updatedBy", fraudPreventionObj.getUpdatedBy());
            BasicDBObject idQuery = new BasicDBObject("id", fraudPreventionObj.getId());
            filterQuery.add(idQuery);
            filterQuery.add(getCurrencyObject(payId,currency));
             BasicDBObject statusQuery = new BasicDBObject("status", TDRStatus.INACTIVE.getName());
            statusQuery.append("updatedBy", fraudPreventionObj.getUpdatedBy());
            statusQuery.append("updateDate", currentDate);
            BasicDBObject updateQuery = new BasicDBObject("$set", statusQuery);
            BasicDBObject finalQuery = new BasicDBObject("$and", filterQuery);
            logger.info("Update Query: " + finalQuery + " " + updateQuery);
            coll.updateOne(finalQuery, updateQuery);

            if (StringUtils.equalsIgnoreCase(fraudPreventionObj.getPayId(), "all")) {
                inactiveRuleForAllActiveMerchants(fraudPreventionObj.getId(), fraudPreventionObj.getUpdatedBy());
            }
          //  logger.info("Take Fraud Type value to Inactive Entry: "+fraudPreventionObj.getFraudType().getValue());
            if (StringUtils.equalsAnyIgnoreCase(fraudPreventionObj.getFraudType().getValue(),
                    FraudRuleType.BLOCK_IP_ADDRESS.getValue(), FraudRuleType.BLOCK_EMAIL_ID.getValue(),
                    FraudRuleType.BLOCK_PHONE_NUMBER.getValue(), FraudRuleType.BLOCK_TXN_AMOUNT_VELOCITY.getValue(),
                    FraudRuleType.BLOCK_CARD_NO.getValue())) {
                return;
            }

            long size = fraudPreventionListbyMerchantRule(fraudPreventionObj.getPayId(),
                    fraudPreventionObj.getFraudType().name()).size();
           // logger.info("update:: existing rules size ={}", size);
            if (size == 0) {
                updateConfigDoc(fraudPreventionObj.getPayId(), fraudPreventionObj.getCurrency(),
                        FraudPreventationConfiguration.getKeyByRuleType(fraudPreventionObj.getFraudType()), false);
            }
        } catch (Exception e) {
            logger.error("Exception in Updating Fraud Prevention ", e);
        }

    }

    private void inactiveRuleForAllActiveMerchants(String parentId, String updatedBy) {
        try {
            logger.info("inactiveRuleForAllActiveMerchants:: initialized. parentId={}", parentId);
            MongoCollection<Document> coll = getCollection();
            BasicDBObject parentIdQuery = new BasicDBObject("parentId", parentId);
            BasicDBObject statusQuery = new BasicDBObject("status", TDRStatus.ACTIVE.getName());
            List<BasicDBObject> filterQueryList = new ArrayList<>();
            filterQueryList.add(parentIdQuery);
            filterQueryList.add(statusQuery);
            BasicDBObject filterQuery = new BasicDBObject("$and", filterQueryList);
            BasicDBObject statusInactiveQuery = new BasicDBObject("status", TDRStatus.INACTIVE.getName());
            DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            statusInactiveQuery.append("updatedBy", updatedBy);
            statusInactiveQuery.append("updateDate", dateFormat.format(new Date()));
            BasicDBObject updateQuery = new BasicDBObject("$set", statusInactiveQuery);
            logger.info("Update Status Inactive for ALL Update Query {}", updateQuery);
            logger.info("update Status Inactive for All Filter Query{}",filterQuery);
            coll.updateMany(filterQuery, updateQuery);
        } catch (Exception ex) {
            logger.error("inactiveRuleForAllActiveMerchants:: failed", ex);
        }
    }

    public void updateAdminFraudRule(FraudPreventionObj fraudPreventionObj) throws DataAccessLayerException {
        try {
            String currentDate = null;
            DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Calendar cal = Calendar.getInstance();
            currentDate = dateFormat.format(cal.getTime());

            MongoDatabase dbIns = mongoInstance.getDB();
            MongoCollection<Document> coll = dbIns.getCollection(PropertiesManager.propertiesMap
                    .get(prefix + Constants.FRAUD_PREVENTION_COLLECTION_NAME.getValue()));

            if (null != fraudPreventionObj.getMaxTransactionAmount()) {
                double maxAmount = Double.parseDouble(fraudPreventionObj.getMaxTransactionAmount());
                DecimalFormat df = new DecimalFormat("#.00");
                fraudPreventionObj.setMaxTransactionAmount(df.format(maxAmount));
            }
            if (null != fraudPreventionObj.getMinTransactionAmount()) {
                double minAmount = Double.parseDouble(fraudPreventionObj.getMinTransactionAmount());
                DecimalFormat df = new DecimalFormat("#.00");
                fraudPreventionObj.setMinTransactionAmount(df.format(minAmount));
            }
             BasicDBObject idQuery = new BasicDBObject("id", fraudPreventionObj.getId());
             BasicDBObject statusQuery = new BasicDBObject("updateDate", currentDate);
            statusQuery.append("updatedBy", fraudPreventionObj.getUpdatedBy());
            statusQuery.append("alwaysOnFlag", fraudPreventionObj.isAlwaysOnFlag());
            statusQuery.append("dateActiveFrom", fraudPreventionObj.getDateActiveFrom());
            statusQuery.append("dateActiveTo", fraudPreventionObj.getDateActiveTo());
            statusQuery.append("email", fraudPreventionObj.getEmail());
            statusQuery.append("endTime", fraudPreventionObj.getEndTime());
            //     statusQuery.append("currency",fraudPreventionObj.getMerchantCurrency());
            statusQuery.append("fraudType", fraudPreventionObj.getFraudType().getValue());
            statusQuery.append("ipAddress", fraudPreventionObj.getIpAddress());
            statusQuery.append("issuerCountry", fraudPreventionObj.getIssuerCountry());
            statusQuery.append("maxTransactionAmount", fraudPreventionObj.getMaxTransactionAmount());
            statusQuery.append("minTransactionAmount", fraudPreventionObj.getMinTransactionAmount());
            statusQuery.append("minutesTxnLimit", fraudPreventionObj.getMinutesTxnLimit());
            statusQuery.append("negativeBin", fraudPreventionObj.getNegativeBin());
            statusQuery.append("negativeCard", fraudPreventionObj.getNegativeCard());
            statusQuery.append("noOfTransactionAllowed", fraudPreventionObj.getNoOfTransactionAllowed());
            statusQuery.append("payId", fraudPreventionObj.getPayId());
            statusQuery.append("perCardTransactionAllowed", fraudPreventionObj.getPerCardTransactionAllowed());
            statusQuery.append("repeatDays", fraudPreventionObj.getRepeatDays());
            statusQuery.append("startTime", fraudPreventionObj.getStartTime());
            statusQuery.append("status", TDRStatus.ACTIVE.getName());
            statusQuery.append("subnetMask", fraudPreventionObj.getSubnetMask());
            statusQuery.append("userCountry", fraudPreventionObj.getUserCountry());
            statusQuery.append("phone", fraudPreventionObj.getPhone());
            statusQuery.append("userIdentifier", fraudPreventionObj.getUserIdentifier());
            statusQuery.put("mackAddress", fraudPreventionObj.getMackAddress());
            statusQuery.put("transactionAmount", fraudPreventionObj.getTransactionAmount());
            statusQuery.put("repetationCount", fraudPreventionObj.getRepetationCount());
            statusQuery.put("blockTimeUnits", fraudPreventionObj.getBlockTimeUnits());
            statusQuery.put("fixedAmountFlag", fraudPreventionObj.isFixedAmountFlag());
            statusQuery.put("emailToNotify", fraudPreventionObj.getEmailToNotify());
            statusQuery.put("paymentType", fraudPreventionObj.getPaymentType());
            statusQuery.put("percentageOfRepeatedMop", fraudPreventionObj.getPercentageOfRepeatedMop());
            statusQuery.put("vpa", fraudPreventionObj.getVpa());
            statusQuery.put("statusVelocity", fraudPreventionObj.getStatusVelocity());
            //statusQuery.put("monitoringType", fraudPreventionObj.getMonitoringType());
            statusQuery.put("notified", fraudPreventionObj.isNotified());
            statusQuery.put("stateCode", fraudPreventionObj.getStateCode());
            statusQuery.put("country", fraudPreventionObj.getCountry());
            statusQuery.put("state", fraudPreventionObj.getState());
            statusQuery.put("city", fraudPreventionObj.getCity());
            // Added By Sweety
            if (fraudPreventionObj.getMerchantProfile() != null) {
                statusQuery.put("blockDaily", fraudPreventionObj.getBlockDaily());
                statusQuery.put("blockWeekly", fraudPreventionObj.getBlockWeekly());
                statusQuery.put("blockMonthly", fraudPreventionObj.getBlockMonthly());
                statusQuery.put("dailyAmount", fraudPreventionObj.getDailyAmount());
                statusQuery.put("weeklyAmount", fraudPreventionObj.getWeeklyAmount());
                statusQuery.put("monthlyAmount", fraudPreventionObj.getMonthlyAmount());
                statusQuery.put("FRM_Merchant_Profile", fraudPreventionObj.getMerchantProfile());
            }
            statusQuery.put("vpaAddress", fraudPreventionObj.getVpaAddress());
            List<String> currencyList=getCurrency(fraudPreventionObj.getPayId(), fraudPreventionObj.getCurrency());
            for (int i = 0; i < currencyList.size(); i++) {
                BasicDBObject currencyQuery=new BasicDBObject("currency", currencyList.get(i));
                List<BasicDBObject>filterIdQuery=new ArrayList<>();
                filterIdQuery.add(idQuery);
                filterIdQuery.add(currencyQuery);
                BasicDBObject filterQuery=new BasicDBObject("$and",filterIdQuery);
                BasicDBObject updateQuery = new BasicDBObject("$set", statusQuery);
                logger.info("Update  Query: " + filterQuery);
                logger.info("Update Filter Query: " + updateQuery);
                 coll.updateOne(filterQuery, updateQuery);
            }
            if (StringUtils.equalsIgnoreCase(fraudPreventionObj.getPayId(), "all")) {
                updateRuleForAllActiveMerchants(fraudPreventionObj.getId(), statusQuery, fraudPreventionObj.getCurrency());
            }
        } catch (Exception e) {
            logger.error("Exception in Updating Fraud Prevention ", e);
        }

    }
    public void updateIndividaulMerchant(FraudPreventionObj fraudPreventionObj) throws DataAccessLayerException {
        try {
            String currentDate = null;
            DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Calendar cal = Calendar.getInstance();
            currentDate = dateFormat.format(cal.getTime());

            MongoDatabase dbIns = mongoInstance.getDB();
            MongoCollection<Document> coll = dbIns.getCollection(PropertiesManager.propertiesMap
                    .get(prefix + Constants.FRAUD_PREVENTION_COLLECTION_NAME.getValue()));

            if (null != fraudPreventionObj.getMaxTransactionAmount()) {
                double maxAmount = Double.parseDouble(fraudPreventionObj.getMaxTransactionAmount());
                DecimalFormat df = new DecimalFormat("#.00");
                fraudPreventionObj.setMaxTransactionAmount(df.format(maxAmount));
            }
            if (null != fraudPreventionObj.getMinTransactionAmount()) {
                double minAmount = Double.parseDouble(fraudPreventionObj.getMinTransactionAmount());
                DecimalFormat df = new DecimalFormat("#.00");
                fraudPreventionObj.setMinTransactionAmount(df.format(minAmount));
            }
            BasicDBObject idQuery = new BasicDBObject("id", fraudPreventionObj.getId());
            BasicDBObject statusQuery = new BasicDBObject("updateDate", currentDate);
            statusQuery.append("updatedBy", fraudPreventionObj.getUpdatedBy());
            statusQuery.append("alwaysOnFlag", fraudPreventionObj.isAlwaysOnFlag());
            statusQuery.append("dateActiveFrom", fraudPreventionObj.getDateActiveFrom());
            statusQuery.append("dateActiveTo", fraudPreventionObj.getDateActiveTo());
            statusQuery.append("email", fraudPreventionObj.getEmail());
            statusQuery.append("endTime", fraudPreventionObj.getEndTime());
            //     statusQuery.append("currency",fraudPreventionObj.getMerchantCurrency());
            statusQuery.append("fraudType", fraudPreventionObj.getFraudType().getValue());
            statusQuery.append("ipAddress", fraudPreventionObj.getIpAddress());
            statusQuery.append("issuerCountry", fraudPreventionObj.getIssuerCountry());
            statusQuery.append("maxTransactionAmount", fraudPreventionObj.getMaxTransactionAmount());
            statusQuery.append("minTransactionAmount", fraudPreventionObj.getMinTransactionAmount());
            statusQuery.append("minutesTxnLimit", fraudPreventionObj.getMinutesTxnLimit());
            statusQuery.append("negativeBin", fraudPreventionObj.getNegativeBin());
            statusQuery.append("negativeCard", fraudPreventionObj.getNegativeCard());
            statusQuery.append("noOfTransactionAllowed", fraudPreventionObj.getNoOfTransactionAllowed());
            statusQuery.append("payId", fraudPreventionObj.getPayId());
            statusQuery.append("perCardTransactionAllowed", fraudPreventionObj.getPerCardTransactionAllowed());
            statusQuery.append("repeatDays", fraudPreventionObj.getRepeatDays());
            statusQuery.append("startTime", fraudPreventionObj.getStartTime());
            statusQuery.append("status", TDRStatus.ACTIVE.getName());
            statusQuery.append("subnetMask", fraudPreventionObj.getSubnetMask());
            statusQuery.append("userCountry", fraudPreventionObj.getUserCountry());
            statusQuery.append("phone", fraudPreventionObj.getPhone());
            statusQuery.append("userIdentifier", fraudPreventionObj.getUserIdentifier());
            statusQuery.put("mackAddress", fraudPreventionObj.getMackAddress());
            statusQuery.put("transactionAmount", fraudPreventionObj.getTransactionAmount());
            statusQuery.put("repetationCount", fraudPreventionObj.getRepetationCount());
            statusQuery.put("blockTimeUnits", fraudPreventionObj.getBlockTimeUnits());
            statusQuery.put("fixedAmountFlag", fraudPreventionObj.isFixedAmountFlag());
            statusQuery.put("emailToNotify", fraudPreventionObj.getEmailToNotify());
            statusQuery.put("paymentType", fraudPreventionObj.getPaymentType());
            statusQuery.put("percentageOfRepeatedMop", fraudPreventionObj.getPercentageOfRepeatedMop());
            statusQuery.put("vpa", fraudPreventionObj.getVpa());
            statusQuery.put("statusVelocity", fraudPreventionObj.getStatusVelocity());
            //statusQuery.put("monitoringType", fraudPreventionObj.getMonitoringType());
            statusQuery.put("notified", fraudPreventionObj.isNotified());
            statusQuery.put("stateCode", fraudPreventionObj.getStateCode());
            statusQuery.put("country", fraudPreventionObj.getCountry());
            statusQuery.put("state", fraudPreventionObj.getState());
            statusQuery.put("city", fraudPreventionObj.getCity());
            // Added By Sweety
            if (fraudPreventionObj.getMerchantProfile() != null) {
                statusQuery.put("blockDaily", fraudPreventionObj.getBlockDaily());
                statusQuery.put("blockWeekly", fraudPreventionObj.getBlockWeekly());
                statusQuery.put("blockMonthly", fraudPreventionObj.getBlockMonthly());
                statusQuery.put("dailyAmount", fraudPreventionObj.getDailyAmount());
                statusQuery.put("weeklyAmount", fraudPreventionObj.getWeeklyAmount());
                statusQuery.put("monthlyAmount", fraudPreventionObj.getMonthlyAmount());
                statusQuery.put("FRM_Merchant_Profile", fraudPreventionObj.getMerchantProfile());
            }
            statusQuery.put("vpaAddress", fraudPreventionObj.getVpaAddress());
            List<String> currencyList=getCurrency(fraudPreventionObj.getPayId(), fraudPreventionObj.getCurrency());
            for (int i = 0; i < currencyList.size(); i++) {
                BasicDBObject currencyQuery=new BasicDBObject("currency", currencyList.get(i));
                List<BasicDBObject>filterIdQuery=new ArrayList<>();
                filterIdQuery.add(idQuery);
                filterIdQuery.add(currencyQuery);
                BasicDBObject filterQuery=new BasicDBObject("$and",filterIdQuery);
                BasicDBObject updateQuery = new BasicDBObject("$set", statusQuery);
                logger.info("Update  Query: " + filterQuery);
                logger.info("Update Filter Query: " + updateQuery);
                coll.updateOne(filterQuery, updateQuery);
            }

        } catch (Exception e) {
            logger.error("Exception in Updating Fraud Prevention ", e);
        }

    }
    private void updateRuleForAllActiveMerchants(String parentId, BasicDBObject updateDataQuery, String currency) {
        try {
            updateDataQuery.remove("payId");
            MongoCollection<Document> coll = getCollection();
            BasicDBObject parentIdQuery = new BasicDBObject("parentId", parentId);
            BasicDBObject statusQuery = new BasicDBObject("status", TDRStatus.ACTIVE.getName());
            //  BasicDBObject currencyQuery=new BasicDBObject("currency",currency);
            List<BasicDBObject> filterQueryList = new ArrayList<>();
            filterQueryList.add(parentIdQuery);
            filterQueryList.add(statusQuery);
            // filterQueryList.add(currencyQuery);
            BasicDBObject filterQuery = new BasicDBObject("$and", filterQueryList);
            BasicDBObject updateQuery = new BasicDBObject("$set", updateDataQuery);
            coll.updateMany(filterQuery, updateQuery);
        } catch (Exception ex) {
            logger.error("updateRuleForAllActiveMerchants:: failed", ex);
        }
    }

    public FraudPreventionObj activefpRulefindById(String payId,String id, String currency) throws DataAccessLayerException {
      // logger.info("ActivefpRuleFind By ID String "+currency);
        FraudPreventionObj fraudPrevention = new FraudPreventionObj();
        String idStr = id.toString();

        MongoDatabase dbIns = mongoInstance.getDB();
        MongoCollection<Document> coll = dbIns.getCollection(
                PropertiesManager.propertiesMap.get(prefix + Constants.FRAUD_PREVENTION_COLLECTION_NAME.getValue()));

        try {

            List<BasicDBObject> paramConditionLst = new ArrayList<BasicDBObject>();
            BasicDBObject idQuery = new BasicDBObject("id", idStr);
            paramConditionLst.add(idQuery);
            paramConditionLst.add(getCurrencyObjectForSelectedMerch(payId,currency));
             BasicDBObject activeTypeQuery = new BasicDBObject("status", TDRStatus.ACTIVE.getName());
            paramConditionLst.add(activeTypeQuery);
            BasicDBObject finalquery = new BasicDBObject("$and", paramConditionLst);
            FindIterable<Document> output = coll.find(finalquery);

            MongoCursor<Document> cursor = output.iterator();
            logger.info("activefpRulefindById:{} ",finalquery);

            while (cursor.hasNext()) {
                 Document doc = cursor.next();
                fraudPrevention = documentToFraudPreventionObj(doc);
            }
          //  logger.info("After ActivefpRuleFind{} ",fraudPrevention.getPayId());

            //logger.info("After ActivefpRuleFind Currency " +fraudPrevention.getCurrency());

            cursor.close();
        } catch (Exception e) {
            logger.error("Exception in getting Fraud Prevention ", e);
        }

        return fraudPrevention;

    }

    public FraudPreventionObj fpRulefindById(String id, String currency) throws DataAccessLayerException {
        FraudPreventionObj fraudPrevention = new FraudPreventionObj();
        String idStr = id.toString();
        MongoDatabase dbIns = mongoInstance.getDB();
        MongoCollection<Document> coll = dbIns.getCollection(
                PropertiesManager.propertiesMap.get(prefix + Constants.FRAUD_PREVENTION_COLLECTION_NAME.getValue()));
        try {
            List<BasicDBObject> paramConditionLst = new ArrayList<BasicDBObject>();
            BasicDBObject idQuery = new BasicDBObject("id", idStr);
             List<BasicDBObject> idAndCurrecyQuery=new ArrayList<>();
            idAndCurrecyQuery.add(getCurrencyObject(idStr,currency));
            idAndCurrecyQuery.add(idQuery);
            paramConditionLst.add(new BasicDBObject("$and",idAndCurrecyQuery));
            logger.info("fpRule By Id {} ",paramConditionLst);
            FindIterable<Document> output = coll.find(idQuery);
            MongoCursor<Document> cursor = output.iterator();

            while (cursor.hasNext()) {
                Document doc = cursor.next();
                fraudPrevention = documentToFraudPreventionObj(doc);
            }

            cursor.close();
        } catch (Exception e) {
            logger.error("Exception in getting Fraud Prevention ", e);
        }

        return fraudPrevention;

    }

    public List<FraudPreventionObj> findFpbyRuleSearch(String payId, String searchString, String rule,String currency) {
        List<FraudPreventionObj> fraudPreventionObjList = new ArrayList<FraudPreventionObj>();

        MongoDatabase dbIns = mongoInstance.getDB();
        MongoCollection<Document> coll = dbIns.getCollection(
                PropertiesManager.propertiesMap.get(prefix + Constants.FRAUD_PREVENTION_COLLECTION_NAME.getValue()));
        try {

            List<BasicDBObject> paramConditionLst = new ArrayList<BasicDBObject>();
            BasicDBObject idQuery = new BasicDBObject("payId", payId);
            BasicDBObject currencyQuery=new BasicDBObject(getCurrencyObject(payId,currency));
            logger.info("currency Query search{}",currencyQuery);

            paramConditionLst.add(idQuery);
            paramConditionLst.add(currencyQuery);
            FraudRuleType fraudType = FraudRuleType.getInstance(rule);
            BasicDBObject fraudTypeQuery = new BasicDBObject("fraudType", fraudType.getValue().toString());
            BasicDBObject activeTypeQuery = new BasicDBObject("status", TDRStatus.ACTIVE.getName());
            paramConditionLst.add(activeTypeQuery);
            paramConditionLst.add(fraudTypeQuery);
            if (StringUtils.isNotBlank(searchString)) {
                switch (fraudType) {
                    case BLOCK_IP_ADDRESS:
                        BasicDBObject searchStringBIAQuery = new BasicDBObject("ipAddress", new BasicDBObject("$regex", searchString).append("$options", "i"));
                        paramConditionLst.add(searchStringBIAQuery);
                        break;
                    case BLOCK_USER_COUNTRY:
                        BasicDBObject searchStringBUCQuery = new BasicDBObject("userCountry",
                                new BasicDBObject("$regex", searchString).append("$options", "i"));
                        paramConditionLst.add(searchStringBUCQuery);
                        break;
                    case BLOCK_CARD_BIN:
                        BasicDBObject searchStringBCBQuery = new BasicDBObject("negativeBin",
                                new BasicDBObject("$regex", searchString).append("$options", "i"));
                        paramConditionLst.add(searchStringBCBQuery);
                        break;
                    case BLOCK_CARD_ISSUER_COUNTRY:
                        BasicDBObject searchStringBCICQuery = new BasicDBObject("issuerCountry",
                                new BasicDBObject("$regex", searchString).append("$options", "i"));
                        paramConditionLst.add(searchStringBCICQuery);
                        break;
                    case BLOCK_CARD_NO:
                        BasicDBObject searchStringBCNQuery = new BasicDBObject("negativeCard", new BasicDBObject("$regex", searchString).append("$options", "i"));
                        paramConditionLst.add(searchStringBCNQuery);
                        break;
                    case BLOCK_EMAIL_ID:
                        BasicDBObject searchStringBEIQuery = new BasicDBObject("email", new BasicDBObject("$regex",searchString).append("$options", "i"));
                        paramConditionLst.add(searchStringBEIQuery);
                        break;
                    case BLOCK_CARD_TXN_THRESHOLD:
                        BasicDBObject searchStringBCTTQuery = new BasicDBObject("perCardTransactionAllowed", new BasicDBObject("$regex", searchString).append("$options", "i"));
                        paramConditionLst.add(searchStringBCTTQuery);
                        break;
                    case BLOCK_NO_OF_TXNS:
                        BasicDBObject searchStringBNTQuery = new BasicDBObject("noOfTransactionAllowed", new BasicDBObject("$regex", searchString).append("$options", "i"));
                        paramConditionLst.add(searchStringBNTQuery);
                        break;
                    case BLOCK_TXN_AMOUNT:
                        // There is no searching functionality for block txn amount
                        BasicDBObject searchStringBTAQuery = new BasicDBObject("payId", new BasicDBObject("$regex", searchString).append("$options", "i"));
                        paramConditionLst.add(searchStringBTAQuery);
                        break;
                    case BLOCK_PHONE_NUMBER:
                        BasicDBObject searchStringBPNQuery = new BasicDBObject("phone", new BasicDBObject("$regex", searchString).append("$options", "i"));
                        paramConditionLst.add(searchStringBPNQuery);
                        break;
                    case BLOCK_TXN_AMOUNT_VELOCITY:
                        BasicDBObject searchStringBTAVQuery = new BasicDBObject("userIdentifier", new BasicDBObject("$regex", searchString).append("$options", "i"));
                        paramConditionLst.add(searchStringBTAVQuery);
                        break;
                    case BLOCK_MACK_ADDRESS:
                        BasicDBObject searchStringMackQuery = new BasicDBObject("mackAddress", new BasicDBObject("$regex", searchString).append("$options", "i"));
                        paramConditionLst.add(searchStringMackQuery);
                        break;
                    case REPEATED_MOP_TYPES:
                        BasicDBObject searchStringRepeatedMopQuery = new BasicDBObject("paymentType",new BasicDBObject("$regex", searchString).append("$options", "i"));
                        paramConditionLst.add(searchStringRepeatedMopQuery);
                        break;
                    case BLOCK_USER_CITY:
                        paramConditionLst.add(new BasicDBObject("city", new BasicDBObject("$regex", searchString).append("$options", "i")));
                        break;
                    case BLOCK_USER_STATE:
                        paramConditionLst.add(new BasicDBObject("stateCode", new BasicDBObject("$regex", searchString).append("$options", "i")));
                        break;
                    case FIRST_TRANSACTIONS_ALERT:
                        break;
                    case BLOCK_REPEATED_MOP_TYPE_FOR_SAME_DETAIL:
                        break;
                    case BLOCK_VPA_ADDRESS:
                        BasicDBObject searchStringVPAQuery = new BasicDBObject("vpaAddress", new BasicDBObject("$regex", searchString).append("$options", "i"));
                        paramConditionLst.add(searchStringVPAQuery);
                        break;
                }
            }


            BasicDBObject finalquery = new BasicDBObject("$and", paramConditionLst);
            logger.info("Fraud Prevention Search Query{}",paramConditionLst);
            FindIterable<Document> output = coll.find(finalquery);
            MongoCursor<Document> cursor = output.iterator();

            while (cursor.hasNext()) {
                Document doc = cursor.next();
                FraudPreventionObj br = documentToFraudPreventionObj(doc);
                fraudPreventionObjList.add(br);
            }

            cursor.close();
        } catch (Exception e) {
            logger.error("Exception while fetching Fraud Prevention using PayId ", e);
        }

        return fraudPreventionObjList;

    }

    public List<FraudPreventionObj> fraudPreventionListbyRule(String payId, String currency) {

        List<FraudPreventionObj> fraudPreventionObjList = new ArrayList<FraudPreventionObj>();

        MongoDatabase dbIns = mongoInstance.getDB();
        MongoCollection<Document> coll = dbIns.getCollection(
                PropertiesManager.propertiesMap.get(prefix + Constants.FRAUD_PREVENTION_COLLECTION_NAME.getValue()));
        try {
            logger.info("FraudPreventionListByRule Currency: " + currency);
            List<BasicDBObject> paramConditionLst = new ArrayList<BasicDBObject>();
            List<BasicDBObject>queryList=new ArrayList<>();
            List<BasicDBObject>currencyQuery=new ArrayList<>();
            BasicDBObject idQuery = new BasicDBObject("payId", payId);
            BasicDBObject idForAllQuery = new BasicDBObject("payId", "ALL");
            queryList.add(idQuery);
            queryList.add(idForAllQuery);
            String payIdAll="ALL";
             paramConditionLst.add(new BasicDBObject("$or",queryList));
             if(!getCurrencyObject(payId, currency).isEmpty()||!getCurrencyObject(payIdAll, currency).isEmpty()){
                 currencyQuery.add(getCurrencyObject(payId,currency));
                 currencyQuery.add(getCurrencyObject(payIdAll,currency));
             }
             paramConditionLst.add(new BasicDBObject("$or",currencyQuery));
            BasicDBObject activeTypeQuery = new BasicDBObject("status", TDRStatus.ACTIVE.getName());
            paramConditionLst.add(activeTypeQuery);
            BasicDBObject finalquery = new BasicDBObject("$and", paramConditionLst);
            logger.info("finalQuery For Listing Query: {}", finalquery);
            FindIterable<Document> output = coll.find(finalquery);
            MongoCursor<Document> cursor = output.iterator();

            while (cursor.hasNext()) {
                Document doc = cursor.next();
                FraudPreventionObj br = documentToFraudPreventionObj(doc);
                fraudPreventionObjList.add(br);
            }

            cursor.close();
        } catch (Exception e) {
            logger.error("Exception while fetching Fraud Prevention using PayId ", e);
        }

        return fraudPreventionObjList;
    }

    public List<FraudPreventionObj> fraudPreventionListbyRulecurrency(String payId, String rule, String currency) {

        List<FraudPreventionObj> fraudPreventionObjList = new ArrayList<FraudPreventionObj>();

        MongoDatabase dbIns = mongoInstance.getDB();
        MongoCollection<Document> coll = dbIns.getCollection(
                PropertiesManager.propertiesMap.get(prefix + Constants.FRAUD_PREVENTION_COLLECTION_NAME.getValue()));
        try {

            List<BasicDBObject> paramConditionLst = new ArrayList<BasicDBObject>();
            BasicDBObject idQuery = new BasicDBObject("payId", payId);
            BasicDBObject currency1 = new BasicDBObject("currency", currency);

            if (StringUtils.isNotBlank(rule)) {
                paramConditionLst.add(new BasicDBObject("fraudType", rule));
            }
            paramConditionLst.add(idQuery);
            paramConditionLst.add(currency1);


            BasicDBObject activeTypeQuery = new BasicDBObject("status", TDRStatus.ACTIVE.getName());
            paramConditionLst.add(activeTypeQuery);
            BasicDBObject finalquery = new BasicDBObject("$and", paramConditionLst);
            FindIterable<Document> output = coll.find(finalquery);
            MongoCursor<Document> cursor = output.iterator();

            while (cursor.hasNext()) {
                Document doc = cursor.next();
                FraudPreventionObj br = documentToFraudPreventionObj(doc);
                fraudPreventionObjList.add(br);
            }

            cursor.close();
        } catch (Exception e) {
            logger.error("Exception while fetching Fraud Prevention using PayId ", e);
        }

        return fraudPreventionObjList;
    }

    public List<FraudPreventionObj> fraudPreventionListbyMerchantRule(String payId, String currency) {

        List<FraudPreventionObj> fraudPreventionObjList = new ArrayList<FraudPreventionObj>();

        MongoDatabase dbIns = mongoInstance.getDB();
        MongoCollection<Document> coll = dbIns.getCollection(
                PropertiesManager.propertiesMap.get(prefix + Constants.FRAUD_PREVENTION_COLLECTION_NAME.getValue()));
        try {

            List<BasicDBObject> paramConditionLst = new ArrayList<BasicDBObject>();
            BasicDBObject idQuery = new BasicDBObject("payId", payId);
            paramConditionLst.add(idQuery);

            // BasicDBObject fraudTypeQuery = new BasicDBObject("currency", currency);
            paramConditionLst.add(new BasicDBObject("$or", getCurrency(payId, currency)));
            BasicDBObject activeTypeQuery = new BasicDBObject("status", TDRStatus.ACTIVE.getName());
            paramConditionLst.add(activeTypeQuery);
            BasicDBObject finalquery = new BasicDBObject("$and", paramConditionLst);
            FindIterable<Document> output = coll.find(finalquery);
            MongoCursor<Document> cursor = output.iterator();
            logger.info("Fraud List for Merchant: {}", paramConditionLst);
            while (cursor.hasNext()) {
                Document doc = cursor.next();
                FraudPreventionObj br = documentToFraudPreventionObj(doc);
                fraudPreventionObjList.add(br);
            }

            cursor.close();
        } catch (Exception e) {
            logger.error("Exception while fetching Fraud Prevention using PayId ", e);
        }

        return fraudPreventionObjList;
    }

    public List<FraudPreventionObj> fraudPreventionListByPayId(String payId,String currency) {

    	List<FraudPreventionObj> fraudPreventionObjList = new ArrayList<FraudPreventionObj>();
    	if(currency == null) {
    		logger.info("========================================");
    		return null;
    	}
        logger.info("Currency for list{}",currency);
        List<String>currencyList=getCurrency(payId,currency);

        
        BasicDBObject currencyQuery=new BasicDBObject(getCurrencyObject(payId,currency));
        logger.info("Currency Query{} ",currencyQuery);
        MongoDatabase dbIns = mongoInstance.getDB();
        MongoCollection<Document> coll = dbIns.getCollection(
                PropertiesManager.propertiesMap.get(prefix + Constants.FRAUD_PREVENTION_COLLECTION_NAME.getValue()));
        try {

            List<BasicDBObject> paramConditionLst = new ArrayList<BasicDBObject>();
            BasicDBObject idQuery = new BasicDBObject("payId", payId);
            BasicDBObject activeTypeQuery = new BasicDBObject("status", TDRStatus.ACTIVE.getName());
            paramConditionLst.add(activeTypeQuery);
            paramConditionLst.add(currencyQuery);
            paramConditionLst.add(idQuery);
            BasicDBObject finalquery = new BasicDBObject("$and", paramConditionLst);
            logger.info("fraudPreventionListByPayId final Query{} ",finalquery);
            FindIterable<Document> output = coll.find(finalquery);
            MongoCursor<Document> cursor = output.iterator();

            while (cursor.hasNext()) {
                Document doc = cursor.next();
                FraudPreventionObj br = documentToFraudPreventionObj(doc);
                fraudPreventionObjList.add(br);
            }

            cursor.close();
        } catch (Exception e) {
            logger.error("Exception while fetching Fraud Prevention using PayId ", e);
        }

        return fraudPreventionObjList;
    }

    /**
     * This method should be remove from code once the migration is executed in
     * prod.
     */
    public List<Document> fraudPreventionListForMigration(String payId) {

        List<Document> fraudPreventionObjList = new ArrayList<>();

        MongoDatabase dbIns = mongoInstance.getDB();
        MongoCollection<Document> coll = dbIns.getCollection(
                PropertiesManager.propertiesMap.get(prefix + Constants.FRAUD_PREVENTION_COLLECTION_NAME.getValue()));
        try {

            List<BasicDBObject> paramConditionLst = new ArrayList<BasicDBObject>();
            BasicDBObject idQuery = new BasicDBObject("payId", payId);
            BasicDBObject activeTypeQuery = new BasicDBObject("status", TDRStatus.ACTIVE.getName());
            paramConditionLst.add(activeTypeQuery);
            paramConditionLst.add(idQuery);
            BasicDBObject finalquery = new BasicDBObject("$and", paramConditionLst);
            FindIterable<Document> output = coll.find(finalquery);
            MongoCursor<Document> cursor = output.iterator();

            while (cursor.hasNext()) {
                Document doc = cursor.next();
                fraudPreventionObjList.add(doc);
            }

            cursor.close();
        } catch (Exception e) {
            logger.error("Exception while fetching Fraud Prevention using PayId ", e);
        }
        return fraudPreventionObjList;

    }

    public String insertAll(List<FraudPreventionObj> fraudPreventionListObj, String payId, String rule,
                            String createdBy, User sessionUser, String currency) {

        StringBuilder message = new StringBuilder();
        long rowCount = 0;
        boolean ruleAlreadyExist = true;

        try {
            for (FraudPreventionObj fraudPreventionObj : fraudPreventionListObj) {

                rowCount++;
                try {
                    fraudPreventionObj.setCurrency(currency);
                    ruleCheckResponse = exists(fraudPreventionObj);
                    if (ruleCheckResponse.getResponseCode().equals(ErrorType.FRAUD_RULE_NOT_EXIST.getResponseCode())
                            || (ruleCheckResponse.getResponseCode()
                            .equals(ErrorType.FRAUD_RULE_ALREADY_EXISTS.getResponseCode())
                            && sessionUser.getUserType().equals(UserType.ADMIN))) {

                        if (sessionUser.getUserType().equals(UserType.ADMIN) && ruleCheckResponse.getResponseCode()
                                .equals(ErrorType.FRAUD_RULE_ALREADY_EXISTS.getResponseCode())) {
                            FraudPreventionObj dbfraudPrevention = getexistingFraudRule(fraudPreventionObj);
                            if (null != dbfraudPrevention && (null != dbfraudPrevention.getCreatedBy())
                                    && !dbfraudPrevention.getCreatedBy().equalsIgnoreCase(sessionUser.getEmailId())) {
                                if (StringUtils.isNotBlank(dbfraudPrevention.getId())) {
                                    fraudPreventionObj.setId(dbfraudPrevention.getId());
                                    fraudPreventionObj.setUpdatedBy(sessionUser.getEmailId().toString());
                                    updateAdminFraudRule(fraudPreventionObj);
                                    ruleAlreadyExist = false;
                                }
                            }
                        } else {
                            fraudPreventionObj.setCreatedBy(createdBy);
                            create(fraudPreventionObj);
                            ruleAlreadyExist = false;
                        }

                    }
                } catch (Exception exception) {
                    message.append("sr. no." + rowCount + " " + ErrorType.COMMON_ERROR.getResponseMessage() + " PayId: "
                            + fraudPreventionObj.getPayId() + "\n");
                    logger.error("Error while processing Fraud prevention:" + exception + rowCount);
                }
            }
            message.append((CrmFieldConstants.PROCESS_INITIATED_SUCCESSFULLY.getValue()));

        } catch (Exception exception) {
            message.append(exception.toString());
            logger.error("Error while processing Fraud prevention:" + exception);
        } finally {
        }

        if (ruleAlreadyExist) {
            message.append((ErrorType.FRAUD_RULE_ALREADY_EXISTS.getInternalMessage()));
        } else {
            message.append((ErrorType.FRAUD_RULE_SUCCESS.getInternalMessage()));
        }

        return message.toString();

    }

    public ResponseObject exists(FraudPreventionObj fraudPrevention) {
        logger.info("CheckExistingFraudRule  ");
        ResponseObject response = new ResponseObject();
        // setting payId according to type of user
        String payId = fraudPrevention.getPayId();
        FraudRuleType fraudType = fraudPrevention.getFraudType();

        List<BasicDBObject> paramConditionLst = new ArrayList<BasicDBObject>();
        BasicDBObject fraudTypeQuery = new BasicDBObject();
        List<BasicDBObject> payIdConditionList = new ArrayList<BasicDBObject>();
        List<BasicDBObject>finalList=new ArrayList<>();
        logger.info("Bulk Insert Already Exist Check: " + fraudPrevention.getCurrency());
         payIdConditionList.add(new BasicDBObject("payId", payId));
        payIdConditionList.add(new BasicDBObject("payId", "ALL"));
        BasicDBObject idQuery = new BasicDBObject("$or", payIdConditionList);
        BasicDBObject currencyQuery=new BasicDBObject(getCurrencyObject(payId,fraudPrevention.getCurrency()));
        finalList.add(currencyQuery);
        finalList.add(idQuery);
        BasicDBObject ruleQuery = new BasicDBObject();

        // Only verify the Active Rules
        BasicDBObject activeTypeQuery = new BasicDBObject("status", TDRStatus.ACTIVE.getName());
        paramConditionLst.add(activeTypeQuery);

        switch (fraudType) {
            case BLOCK_NO_OF_TXNS:
                ruleQuery.append("fraudType", fraudType);
                responseErrorType = ErrorType.FRAUD_RULE_ALREADY_EXISTS;
                break;
            case BLOCK_TXN_AMOUNT:
                ruleQuery.append("maxTransactionAmount", fraudPrevention.getMaxTransactionAmount());
                responseErrorType = ErrorType.FRAUD_RULE_ALREADY_EXISTS;
                break;
            case BLOCK_IP_ADDRESS:
                ruleQuery.append("ipAddress", fraudPrevention.getIpAddress());
                responseErrorType = ErrorType.FRAUD_RULE_ALREADY_EXISTS;
                break;
            case BLOCK_EMAIL_ID:
                ruleQuery.append("email", fraudPrevention.getEmail());
                responseErrorType = ErrorType.FRAUD_RULE_ALREADY_EXISTS;
                break;
            case BLOCK_USER_COUNTRY:
                ruleQuery.append("userCountry", fraudPrevention.getUserCountry());
                responseErrorType = ErrorType.FRAUD_RULE_ALREADY_EXISTS;
                break;
            case BLOCK_CARD_ISSUER_COUNTRY:
                String issuerCountrysuff;
                if (fraudPrevention.getIssuerCountry().equalsIgnoreCase("India")) {
                    issuerCountrysuff = "IN";
                } else {
                    issuerCountrysuff = fraudPrevention.getIssuerCountry();
                }
                ruleQuery.append("issuerCountry", issuerCountrysuff);
                responseErrorType = ErrorType.FRAUD_RULE_ALREADY_EXISTS;
                break;
            case BLOCK_CARD_BIN:
                ruleQuery.append("negativeBin", fraudPrevention.getNegativeBin());
                responseErrorType = ErrorType.FRAUD_RULE_ALREADY_EXISTS;
                break;
            case BLOCK_CARD_NO:
                ruleQuery.append("negativeCard", fraudPrevention.getNegativeCard());
                responseErrorType = ErrorType.FRAUD_RULE_ALREADY_EXISTS;
                break;
            case BLOCK_CARD_TXN_THRESHOLD:
                fraudTypeQuery.append("fraudType", "BLOCK_CARD_TXN_THRESHOLD");
                ruleQuery.append("negativeCard", fraudPrevention.getNegativeCard());
                responseErrorType = ErrorType.FRAUD_RULE_ALREADY_EXISTS;
                break;
            case BLOCK_PHONE_NUMBER:
                ruleQuery.append("phone", fraudPrevention.getPhone());
                responseErrorType = ErrorType.FRAUD_RULE_ALREADY_EXISTS;
                break;
            case BLOCK_TXN_AMOUNT_VELOCITY:
                ruleQuery.append("userIdentifier", fraudPrevention.getUserIdentifier());
                responseErrorType = ErrorType.FRAUD_RULE_ALREADY_EXISTS;
                break;
            case BLOCK_MACK_ADDRESS:
                ruleQuery.append("mackAddress", fraudPrevention.getMackAddress());
                responseErrorType = ErrorType.FRAUD_RULE_ALREADY_EXISTS;
                break;
            case REPEATED_MOP_TYPES:
                ruleQuery.append("paymentType", fraudPrevention.getPaymentType());
                responseErrorType = ErrorType.FRAUD_RULE_ALREADY_EXISTS;
                break;
            case BLOCK_VPA_ADDRESS:
                ruleQuery.append("vpaAddress", fraudPrevention.getVpaAddress());
                responseErrorType = ErrorType.FRAUD_RULE_ALREADY_EXISTS;
                break;
            default:
                logger.error("Something went wrong while checking fraud field values");
                break;
        }
        paramConditionLst.add(new BasicDBObject("$and",finalList));
        paramConditionLst.add(ruleQuery);

        if (!fraudTypeQuery.isEmpty()) {
            paramConditionLst.add(fraudTypeQuery);
        }

        BasicDBObject finalquery = new BasicDBObject("$and", paramConditionLst);
        if (duplicateChecker(finalquery)) {
            logger.info("after CheckExistingFraudRule   duplicateChecker");
            response.setResponseMessage(responseErrorType.getResponseMessage());
            response.setResponseCode(responseErrorType.getCode());
        } else {
            logger.info(" FRAUD_RULE_NOT_EXIST   duplicateChecker");
            response.setResponseMessage(ErrorType.FRAUD_RULE_NOT_EXIST.getResponseMessage());
            response.setResponseCode(ErrorType.FRAUD_RULE_NOT_EXIST.getCode());
        }
        return response;
    }

    public String deleteAll(List<FraudPreventionObj> fraudPreventionListObj, String payId, String rule) {

        MongoDatabase dbIns = mongoInstance.getDB();
        MongoCollection<Document> coll = dbIns.getCollection(
                PropertiesManager.propertiesMap.get(prefix + Constants.FRAUD_PREVENTION_COLLECTION_NAME.getValue()));

        StringBuilder message = new StringBuilder();
        long rowCount = 0;
        try {
            for (FraudPreventionObj fraudPreventionObj : fraudPreventionListObj) {

                rowCount++;
                try {
                    List<BasicDBObject> paramConditionLst = new ArrayList<BasicDBObject>();
                    BasicDBObject idQuery = new BasicDBObject("payId", fraudPreventionObj.getPayId());
                    paramConditionLst.add(idQuery);
                    BasicDBObject ruleQuery = new BasicDBObject();
                    FraudRuleType fraudType = fraudPreventionObj.getFraudType();
                    switch (fraudType) {
                        case BLOCK_NO_OF_TXNS:
                            ruleQuery.append("fraudType", fraudType);
                            break;
                        case BLOCK_TXN_AMOUNT:
                            ruleQuery.append("currency", fraudPreventionObj.getCurrency());
                            break;
                        case BLOCK_IP_ADDRESS:
                            ruleQuery.append("ipAddress", fraudPreventionObj.getIpAddress());
                            break;
                        case BLOCK_EMAIL_ID:
                            ruleQuery.append("email", fraudPreventionObj.getEmail());
                            break;
                        case BLOCK_PHONE_NUMBER:
                            ruleQuery.append("phone", fraudPreventionObj.getPhone());
                            break;
                        case BLOCK_TXN_AMOUNT_VELOCITY:
                            ruleQuery.append("userIdentifier", fraudPreventionObj.getUserIdentifier());
                            break;
                        case BLOCK_USER_COUNTRY:
                            ruleQuery.append("userCountry", fraudPreventionObj.getUserCountry());
                            break;
                        case BLOCK_CARD_ISSUER_COUNTRY:
                            ruleQuery.append("issuerCountry", fraudPreventionObj.getIssuerCountry());
                            break;
                        case BLOCK_CARD_BIN:
                            ruleQuery.append("negativeBin", fraudPreventionObj.getNegativeBin());
                            break;
                        case BLOCK_CARD_NO:
                            ruleQuery.append("negativeCard", fraudPreventionObj.getNegativeCard());
                            break;
                        case BLOCK_CARD_TXN_THRESHOLD:
                            ruleQuery.append("fraudType", fraudType);
                            ruleQuery.append("negativeCard", fraudPreventionObj.getNegativeCard());
                            break;
                        case BLOCK_MACK_ADDRESS:
                            ruleQuery.append("mackAddress", fraudPreventionObj.getMackAddress());
                            break;
                        case REPEATED_MOP_TYPES:
                            ruleQuery.append("paymentType", fraudPreventionObj.getPaymentType());
                            break;
                        default:
                            logger.error("Something went wrong while checking fraud field values");
                            break;
                    }
                    paramConditionLst.add(ruleQuery);
                    BasicDBObject finalquery = new BasicDBObject("$and", paramConditionLst);
                    coll.deleteOne(finalquery);
                } catch (Exception exception) {
                    message.append(
                            "sr. no." + rowCount + " " + ErrorType.CSV_NOT_SUCCESSFULLY_UPLOAD.getResponseMessage()
                                    + " PayId: " + fraudPreventionObj.getPayId() + "\n");
                    logger.error("Error while processing binRange:" + exception + rowCount);
                }
            }
            message.append((CrmFieldConstants.PROCESS_INITIATED_SUCCESSFULLY.getValue()));
        } catch (Exception exception) {
            message.append(exception.toString());
            logger.error("Error while processing binRange:" + exception);
        } finally {
        }
        return message.toString();

    }

    public boolean duplicateChecker(BasicDBObject finalquery) {
        MongoDatabase dbIns = mongoInstance.getDB();
        MongoCollection<Document> coll = dbIns.getCollection(
                PropertiesManager.propertiesMap.get(prefix + Constants.FRAUD_PREVENTION_COLLECTION_NAME.getValue()));
        try {
            int count = (int) coll.count(finalquery);

            if (count > 0) {
                return true;
            } else {
                return false;
            }
        } catch (ObjectNotFoundException exception) {
            logger.error("error" + exception);
        } catch (HibernateException exception) {
            logger.error("error" + exception);
        } finally {
        }
        // if something went wrong ,it will stop from creating new rule
        return true;
    }

    public FraudPreventionObj getexistingFraudRule(FraudPreventionObj fraudPrevention) {
        BasicDBObject finalquery;
        if (StringUtils.isNotBlank(fraudPrevention.getId())) {
            finalquery = new BasicDBObject("_id", fraudPrevention.getId());
            logger.info("Fraud Prevention id is not blank: "+fraudPrevention.getId());
        } else {
            logger.info("Fraud Prevention id is blank: "+fraudPrevention.getId());
            String payId = fraudPrevention.getPayId();
            String currency=fraudPrevention.getCurrency();
            FraudRuleType fraudType = fraudPrevention.getFraudType();
            List<BasicDBObject> paramConditionLst = new ArrayList<BasicDBObject>();
            BasicDBObject idQuery = new BasicDBObject("payId", payId);
            BasicDBObject ruleQuery = new BasicDBObject();
            BasicDBObject activeTypeQuery = new BasicDBObject("status", TDRStatus.ACTIVE.getName());
            paramConditionLst.add(activeTypeQuery);
            switch (fraudType) {
                case BLOCK_NO_OF_TXNS:
                    ruleQuery.append("fraudType", fraudType);
                    responseErrorType = ErrorType.FRAUD_RULE_ALREADY_EXISTS;
                    break;
                case BLOCK_TXN_AMOUNT:
                    ruleQuery.append("maxTransactionAmount", fraudPrevention.getMaxTransactionAmount());
                    responseErrorType = ErrorType.FRAUD_RULE_ALREADY_EXISTS;
                    break;
                case BLOCK_IP_ADDRESS:
                    ruleQuery.append("ipAddress", fraudPrevention.getIpAddress());
                    responseErrorType = ErrorType.FRAUD_RULE_ALREADY_EXISTS;
                    break;
                case BLOCK_EMAIL_ID:
                    ruleQuery.append("email", fraudPrevention.getEmail());
                    responseErrorType = ErrorType.FRAUD_RULE_ALREADY_EXISTS;
                    break;
                case BLOCK_USER_COUNTRY:
                    ruleQuery.append("userCountry", fraudPrevention.getUserCountry());
                    responseErrorType = ErrorType.FRAUD_RULE_ALREADY_EXISTS;
                    break;
                case BLOCK_CARD_ISSUER_COUNTRY:
                    ruleQuery.append("issuerCountry", fraudPrevention.getIssuerCountry());
                    responseErrorType = ErrorType.FRAUD_RULE_ALREADY_EXISTS;
                    break;
                case BLOCK_USER_STATE:
                    ruleQuery.append("stateCode", fraudPrevention.getStateCode());
                    responseErrorType = ErrorType.FRAUD_RULE_ALREADY_EXISTS;
                    break;
                case BLOCK_USER_CITY:
                    ruleQuery.append("city", fraudPrevention.getCity());
                    responseErrorType = ErrorType.FRAUD_RULE_ALREADY_EXISTS;
                    break;
                case BLOCK_CARD_BIN:
                    ruleQuery.append("negativeBin", fraudPrevention.getNegativeBin());
                    responseErrorType = ErrorType.FRAUD_RULE_ALREADY_EXISTS;
                    break;
                case BLOCK_CARD_NO:
                    ruleQuery.append("negativeCard", fraudPrevention.getNegativeCard());
                    responseErrorType = ErrorType.FRAUD_RULE_ALREADY_EXISTS;
                    break;
                case BLOCK_CARD_TXN_THRESHOLD:
                    ruleQuery.append("perCardTransactionAllowed", fraudPrevention.getPerCardTransactionAllowed());
                    responseErrorType = ErrorType.FRAUD_RULE_ALREADY_EXISTS;
                    break;
                case BLOCK_PHONE_NUMBER:
                    ruleQuery.append("phone", fraudPrevention.getPhone());
                    responseErrorType = ErrorType.FRAUD_RULE_ALREADY_EXISTS;
                    break;
                case BLOCK_TXN_AMOUNT_VELOCITY:
                    ruleQuery.append("userIdentifier", fraudPrevention.getUserIdentifier());
                    responseErrorType = ErrorType.FRAUD_RULE_ALREADY_EXISTS;
                    break;
                case BLOCK_MACK_ADDRESS:
                    ruleQuery.append("mackAddress", fraudPrevention.getMackAddress());
                    responseErrorType = ErrorType.FRAUD_RULE_ALREADY_EXISTS;
                    break;
                case REPEATED_MOP_TYPES:
                    ruleQuery.append("paymentType", fraudPrevention.getPaymentType());
                    break;
                case FIRST_TRANSACTIONS_ALERT:
                    ruleQuery.append("fraudType", "FIRST_TRANSACTIONS_ALERT");
                    break;
                case BLOCK_REPEATED_MOP_TYPE_FOR_SAME_DETAIL:
                    ruleQuery.append("fraudType", "BLOCK_REPEATED_MOP_TYPE_FOR_SAME_DETAIL");
                    break;
                case BLOCK_VPA_ADDRESS:
                    ruleQuery.append("vpaAddress", fraudPrevention.getVpaAddress());
                    responseErrorType = ErrorType.FRAUD_RULE_ALREADY_EXISTS;
                    break;
                default:
                    logger.error("Something went wrong while checking fraud field values");
                    break;
            }

            paramConditionLst.add(idQuery);
            paramConditionLst.add(getCurrencyObject(payId,currency));
            paramConditionLst.add(ruleQuery);
            finalquery = new BasicDBObject("$and", paramConditionLst);
        }
        FraudPreventionObj fraudPreventionObj = new FraudPreventionObj();
        MongoDatabase dbIns = mongoInstance.getDB();
        MongoCollection<Document> coll = dbIns.getCollection(
                PropertiesManager.propertiesMap.get(prefix + Constants.FRAUD_PREVENTION_COLLECTION_NAME.getValue()));
        try {

            FindIterable<Document> output = coll.find(finalquery);
            MongoCursor<Document> cursor = output.iterator();

            while (cursor.hasNext()) {
                Document doc = cursor.next();
                fraudPreventionObj = documentToFraudPreventionObj(doc);
            }
            cursor.close();

        } catch (ObjectNotFoundException exception) {
            logger.error("error" + exception);
        } catch (HibernateException exception) {
            logger.error("error" + exception);
        } finally {
        }
        // if something went wrong ,it will stop from creating new rule
        return fraudPreventionObj;
    }

    public List<BasicDBObject> getCurrencyList(String payId, String currency) {
        String[] currencyReplace = (currency.replaceAll("\\[", "").replaceAll("\\]", "").replaceAll("\"", "")).split(",");
        List<BasicDBObject> currencyList = new ArrayList<>();
         for (String s : currencyReplace) {
            if (!s.equalsIgnoreCase("All")) {
                logger.info("CurrencyList for Individual" + s);
                currencyList.add(new BasicDBObject("currency", s));
            } else {
                 List<MultCurrencyCode> currencyByPayId = multCurrencyCodeDao.getCurrencyCode();
                 for (MultCurrencyCode curr : currencyByPayId) {
                     currencyList.add(new BasicDBObject("currency", curr.getCode()));
                }
            }
        }
        return currencyList;
    }

    public List<String> getCurrency(String payId,String currency){
        String[] currencyReplace = (currency.replaceAll("\\[", "").replaceAll("\\]", "").replaceAll("\"", "")).split(",");
        List<String> currencies=new ArrayList<>();
        if(payId.equalsIgnoreCase("All")){
            for(String curr: currencyReplace) {
                if(curr.equalsIgnoreCase("All")){
                    List<MultCurrencyCode> multCurrencyCodes = multCurrencyCodeDao.getCurrencyCode();
                    for (MultCurrencyCode multCurrencyCode : multCurrencyCodes) {
                        currencies.add(multCurrencyCode.getCode());
                    }
                }
                else{
                    currencies.add(curr);
                }
            }
        }
        else{
            for(String curr: currencyReplace) {
                if(curr.equalsIgnoreCase("All")){
                    List<String> multCurrencyCodes = userDao.findCurrencyByPayId(payId);
                    for (String multCurrencyCode : multCurrencyCodes) {
                        currencies.add(multCurrencyCode);
                    }
                }
                else{
                    currencies.add(curr);
                }
            }
        }
        return currencies;
    }

    public List<String> getCurrencyForSelectedMerchant(String payId,String currency){
        String[] currencyReplace = (currency.replaceAll("\\[", "").replaceAll("\\]", "").replaceAll("\"", "")).split(",");
        List<String> currencies=new ArrayList<>();
        if(payId.equalsIgnoreCase("All")){
            for(String curr: currencyReplace) {
                if(curr.equalsIgnoreCase("All")){
                    List<MultCurrencyCode> multCurrencyCodes = multCurrencyCodeDao.getCurrencyCode();
                    for (MultCurrencyCode multCurrencyCode : multCurrencyCodes) {
                        currencies.add(multCurrencyCode.getCode());
                    }
                }
                else{
                    currencies.add(curr);
                }
            }
        }
        else{
            for(String curr: currencyReplace) {
                if(curr.equalsIgnoreCase("All")){
                    List<MultCurrencyCode> multCurrencyCodes = multCurrencyCodeDao.getCurrencyCode();
                    for (MultCurrencyCode multCurrencyCode : multCurrencyCodes) {
                        currencies.add(multCurrencyCode.getCode());
                    }
                }
                else{
                    currencies.add(curr);
                }
            }
        }
        return currencies;
    }

    public List<String> getFraudPreventionCurrencyList(String payId, String currency) {
        String[] currencyReplace = (currency.replaceAll("\\[", "").replaceAll("\\]", "").replaceAll("\"", "")).split(",");
        List<String> currencyList = new ArrayList<>();
        for (String s : currencyReplace) {
            if (!s.equalsIgnoreCase("All")) {
                currencyList.add(s);
            } else {
                List<String> currencyByPayId = userDao.findCurrencyByPayId(payId);
                currencyList.addAll(currencyByPayId);
            }

        }
        return currencyList;
    }

    public FraudPreventionObj documentToFraudPreventionObj(Document doc) {

        FraudPreventionObj fraudPrevention = new FraudPreventionObj();

        if (StringUtils.isNotBlank(doc.getString("id"))) {
            fraudPrevention.setId(doc.getString("id"));
        }
        if (doc.getBoolean("alwaysOnFlag")) {
            fraudPrevention.setAlwaysOnFlag(doc.getBoolean("alwaysOnFlag"));
        }
        if (StringUtils.isNotBlank(doc.getString("createDate"))) {
            fraudPrevention.setCreateDate(doc.getString("createDate"));
        }
        //    logger.info("Currency fPMS: "+doc.getString("currency"));
        if (StringUtils.isNotBlank(doc.getString("currency"))) {
            fraudPrevention.setCurrency(doc.getString("currency"));
        }
        if (StringUtils.isNotBlank(doc.getString("dateActiveFrom"))) {
            fraudPrevention.setDateActiveFrom(doc.getString("dateActiveFrom"));
        } else {
            fraudPrevention.setDateActiveFrom("NA");
        }
        if (StringUtils.isNotBlank(doc.getString("dateActiveTo"))) {
            fraudPrevention.setDateActiveTo(doc.getString("dateActiveTo"));
        } else {
            fraudPrevention.setDateActiveTo("NA");
        }
        if (StringUtils.isNotBlank(doc.getString("email"))) {
            fraudPrevention.setEmail(doc.getString("email"));
        }
        if (StringUtils.isNotBlank(doc.getString("endTime"))) {
            fraudPrevention.setEndTime(doc.getString("endTime"));
        } else {
            fraudPrevention.setEndTime("NA");
        }
        if (StringUtils.isNotBlank(doc.getString("fraudType"))) {
            fraudPrevention.setFraudType(FraudRuleType.getInstance(doc.getString("fraudType")));
        }
        if (StringUtils.isNotBlank(doc.getString("ipAddress"))) {
            fraudPrevention.setIpAddress(doc.getString("ipAddress"));
        }
        if (StringUtils.isNotBlank(doc.getString("issuerCountry"))) {
            fraudPrevention.setIssuerCountry(doc.getString("issuerCountry"));
        }
        if (StringUtils.isNotBlank(doc.getString("maxTransactionAmount"))) {
            fraudPrevention.setMaxTransactionAmount(doc.getString("maxTransactionAmount"));
        }
        if (StringUtils.isNotBlank(doc.getString("minTransactionAmount"))) {
            fraudPrevention.setMinTransactionAmount(doc.getString("minTransactionAmount"));
        }
        if (StringUtils.isNotBlank(doc.getString("minutesTxnLimit"))) {
            fraudPrevention.setMinutesTxnLimit(doc.getString("minutesTxnLimit"));
        }
        if (StringUtils.isNotBlank(doc.getString("negativeBin"))) {
            fraudPrevention.setNegativeBin(doc.getString("negativeBin"));
        }
        if (StringUtils.isNotBlank(doc.getString("negativeCard"))) {
            fraudPrevention.setNegativeCard(doc.getString("negativeCard"));
        }
        if (StringUtils.isNotBlank(doc.getString("noOfTransactionAllowed"))) {
            fraudPrevention.setNoOfTransactionAllowed(doc.getString("noOfTransactionAllowed"));
        }
        if (StringUtils.isNotBlank(doc.getString("payId"))) {
            fraudPrevention.setPayId(doc.getString("payId"));
        }
        if (StringUtils.isNotBlank(doc.getString("perCardTransactionAllowed"))) {
            fraudPrevention.setPerCardTransactionAllowed(doc.getString("perCardTransactionAllowed"));
        }
        if (StringUtils.isNotBlank(doc.getString("repeatDays"))) {
            fraudPrevention.setRepeatDays(doc.getString("repeatDays"));
        } else {
            fraudPrevention.setRepeatDays("NA");
        }
        if (StringUtils.isNotBlank(doc.getString("startTime"))) {
            fraudPrevention.setStartTime(doc.getString("startTime"));
        } else {
            fraudPrevention.setStartTime("NA");
        }
        if (StringUtils.isNotBlank(doc.getString("status"))) {
            fraudPrevention.setStatus(TDRStatus.valueOf(doc.getString("status").toUpperCase()));
        }
        if (StringUtils.isNotBlank(doc.getString("subnetMask"))) {
            fraudPrevention.setSubnetMask(doc.getString("subnetMask"));
        }
        if (StringUtils.isNotBlank(doc.getString("updateDate"))) {
            fraudPrevention.setUpdateDate(doc.getString("updateDate"));
        }
        if (StringUtils.isNotBlank(doc.getString("userCountry"))) {
            fraudPrevention.setUserCountry(doc.getString("userCountry"));
        }
        if (StringUtils.isNotBlank(doc.getString("phone"))) {
            fraudPrevention.setPhone(doc.getString("phone"));
        }
        if (StringUtils.isNotBlank(doc.getString("userIdentifier"))) {
            fraudPrevention.setUserIdentifier(doc.getString("userIdentifier"));
        }
        if (StringUtils.isNotBlank(doc.getString("createdBy"))) {
            fraudPrevention.setCreatedBy(doc.getString("createdBy"));
        }
        if (StringUtils.isNotBlank(doc.getString("updatedBy"))) {
            fraudPrevention.setUpdatedBy(doc.getString("updatedBy"));
        }
        fraudPrevention.setMackAddress(doc.getString("mackAddress"));
        fraudPrevention.setBlockTimeUnits(doc.getString("blockTimeUnits"));
        Object transactionAmt = doc.get("transactionAmount");
        if (transactionAmt != null) {
            fraudPrevention.setTransactionAmount(Double.valueOf(String.valueOf(transactionAmt)));
        }
        fraudPrevention.setRepetationCount(doc.getInteger("repetationCount", 0));
        fraudPrevention.setFixedAmountFlag(doc.getBoolean("fixedAmountFlag", false));
        fraudPrevention.setEmailToNotify(doc.getString("emailToNotify"));
        fraudPrevention.setPaymentType(doc.getString("paymentType"));
        fraudPrevention.setVpa(doc.getString("vpa"));
        fraudPrevention.setStatusVelocity(doc.getString("statusVelocity"));
        fraudPrevention.setMonitoringType(doc.getString("monitoringType"));
        fraudPrevention.setNotified(doc.getBoolean("notified", false));
        Object percentageOfRepetedMop = doc.get("percentageOfRepeatedMop");
        if (percentageOfRepetedMop != null) {
            fraudPrevention.setPercentageOfRepeatedMop(Double.valueOf(
                    String.valueOf(percentageOfRepetedMop)));
        }
        //  fraudPrevention.setMerchantCurrency(doc.get(0));
        fraudPrevention.setStateCode(doc.getString("stateCode"));
        fraudPrevention.setCity(doc.getString("city"));
        fraudPrevention.setParentId(doc.getString("parentId"));
        fraudPrevention.setCountry(doc.getString("country"));
        fraudPrevention.setState(doc.getString("state"));

        if (StringUtils.isNotBlank(doc.getString("blockDaily"))) {
            fraudPrevention.setBlockDaily(doc.getString("blockDaily"));
        }
        if (StringUtils.isNotBlank(doc.getString("blockWeekly"))) {
            fraudPrevention.setBlockWeekly(doc.getString("blockWeekly"));
        }
        if (StringUtils.isNotBlank(doc.getString("blockMonthly"))) {
            fraudPrevention.setBlockMonthly(doc.getString("blockMonthly"));
        }
        if (StringUtils.isNotBlank(doc.getString("dailyAmount"))) {
            fraudPrevention.setDailyAmount(doc.getString("dailyAmount"));
        }
        if (StringUtils.isNotBlank(doc.getString("weeklyAmount"))) {
            fraudPrevention.setWeeklyAmount(doc.getString("weeklyAmount"));
        }
        if (StringUtils.isNotBlank(doc.getString("monthlyAmount"))) {
            fraudPrevention.setMonthlyAmount(doc.getString("monthlyAmount"));
        }

        if (StringUtils.isNotBlank(doc.getString("blockDailyType"))) {
            fraudPrevention.setBlockDailyType(doc.getString("blockDailyType"));
        }
        if (StringUtils.isNotBlank(doc.getString("blockWeeklyType"))) {
            fraudPrevention.setBlockWeeklyType(doc.getString("blockWeeklyType"));
        }
        if (StringUtils.isNotBlank(doc.getString("blockMonthlyType"))) {
            fraudPrevention.setBlockMonthlyType(doc.getString("blockMonthlyType"));
        }
        if (StringUtils.isNotBlank(doc.getString("vpaAddress"))) {
            fraudPrevention.setVpaAddress(doc.getString("vpaAddress"));
        }
        return fraudPrevention;
    }

    public List<FraudPreventionObj> fraudPreventionListbyWhiteListIpRuleByFieldsName(String payId, String fieldName) {

        List<FraudPreventionObj> fraudPreventionObjList = new ArrayList<FraudPreventionObj>();

        MongoDatabase dbIns = mongoInstance.getDB();
        MongoCollection<Document> coll = dbIns.getCollection(
                PropertiesManager.propertiesMap.get(prefix + Constants.FRAUD_PREVENTION_COLLECTION_NAME.getValue()));
        try {

            List<BasicDBObject> paramConditionLst = new ArrayList<BasicDBObject>();
            BasicDBObject idQuery = new BasicDBObject();

            if (!payId.equalsIgnoreCase("ALL")) {
                List<BasicDBObject> payIdConditionList = new ArrayList<BasicDBObject>();
                payIdConditionList.add(new BasicDBObject("payId", payId));
                payIdConditionList.add(new BasicDBObject("payId", "ALL"));
                idQuery = new BasicDBObject("$or", payIdConditionList);
            } else {
                idQuery = new BasicDBObject("payId", "ALL");
            }

            BasicDBObject activeTypeQuery = new BasicDBObject("status", TDRStatus.ACTIVE.getName());
            paramConditionLst.add(activeTypeQuery);
            paramConditionLst.add(idQuery);
            BasicDBObject finalquery = new BasicDBObject("$and", paramConditionLst);
            FindIterable<Document> output = coll.find(finalquery);
            MongoCursor<Document> cursor = output.iterator();

            while (cursor.hasNext()) {
                Document doc = cursor.next();
                FraudPreventionObj br = documentToFraudPreventionObj(doc);
                fraudPreventionObjList.add(br);
            }

            cursor.close();
        } catch (Exception e) {
            logger.error("Exception while fetching Fraud Prevention using PayId ", e);
        }

        return fraudPreventionObjList;

    }

    public List<FraudPreventionObj> fraudPreventionListbyWhiteListDomainNameRuleByFieldsName(String payId,
                                                                                             String fieldName) {

        List<FraudPreventionObj> fraudPreventionObjList = new ArrayList<FraudPreventionObj>();

        MongoDatabase dbIns = mongoInstance.getDB();
        MongoCollection<Document> coll = dbIns.getCollection(
                PropertiesManager.propertiesMap.get(prefix + Constants.FRAUD_PREVENTION_COLLECTION_NAME.getValue()));
        try {

            List<BasicDBObject> paramConditionLst = new ArrayList<BasicDBObject>();
            BasicDBObject idQuery = new BasicDBObject();
            if (!payId.equalsIgnoreCase("ALL")) {
                List<BasicDBObject> payIdConditionList = new ArrayList<BasicDBObject>();
                payIdConditionList.add(new BasicDBObject("payId", payId));
                payIdConditionList.add(new BasicDBObject("payId", "ALL"));
                idQuery = new BasicDBObject("$or", payIdConditionList);
            } else {
                idQuery = new BasicDBObject("payId", "ALL");
            }

            BasicDBObject activeTypeQuery = new BasicDBObject("status", TDRStatus.ACTIVE.getName());
            paramConditionLst.add(activeTypeQuery);
            paramConditionLst.add(idQuery);
            BasicDBObject finalquery = new BasicDBObject("$and", paramConditionLst);
            FindIterable<Document> output = coll.find(finalquery);
            MongoCursor<Document> cursor = output.iterator();

            while (cursor.hasNext()) {
                Document doc = cursor.next();
                FraudPreventionObj br = documentToFraudPreventionObj(doc);
                fraudPreventionObjList.add(br);
            }

            cursor.close();
        } catch (Exception e) {
            logger.error("Exception while fetching Fraud Prevention using PayId ", e);
        }

        return fraudPreventionObjList;

    }

    @SuppressWarnings("unchecked")
    public void createConfigDoc(FraudPreventationConfiguration configuration, String currency) {

        MongoDatabase dbIns = mongoInstance.getDB();
        MongoCollection<Document> coll = dbIns.getCollection(
                PropertiesManager.propertiesMap.get(prefix + Constants.FRAUD_PREVENTION_COLLECTION_NAME.getValue()));
        Document doc = new Document();
        List<String> currencyList=getCurrency(configuration.getId(),currency);
        for(int i=0;i<currencyList.size();i++) {
            String id = StringUtils.join(PREFIX_CONFIG, configuration.getId()+ "_"+currencyList.get(i));
            logger.info("Generating Id by Join: "+id);
            configuration.setId(id);
            doc.put("_id", id);
            Map<String, Boolean> map = mapper.convertValue(configuration, HashMap.class);
            try {
                map.entrySet().forEach(entry -> {
                    doc.put(entry.getKey(), entry.getValue());
                });
                coll.insertOne(doc);
            } catch (Exception e) {
                logger.error("Exception in getting Fraud Prevention ", e);
            }
        }
    }

    public FraudPreventationConfiguration getConfigByPayId(String payId, String currency) {
        MongoDatabase dbIns = mongoInstance.getDB();
        MongoCollection<Document> coll = dbIns.getCollection(
                PropertiesManager.propertiesMap.get(prefix + Constants.FRAUD_PREVENTION_COLLECTION_NAME.getValue()));
        FraudPreventationConfiguration configuration = null;
        try {
            List<BasicDBObject>idQueryList=new ArrayList<>();
            logger.info("Currency for list{}",currency);
            List<String>currencyList=getCurrency(payId,currency);
            logger.info("Currency List Fraud Prevention Mongo Service{}",currencyList);
            for(int i=0;i<currencyList.size();i++) {
                String id = StringUtils.join(PREFIX_CONFIG, payId, "_", currencyList.get(i));
                logger.info("ID for get configuration by pay ID "+id);
                idQueryList.add(new BasicDBObject("id",id));
            }
            BasicDBObject findQuery=null;
            if(idQueryList.size()>1){
                findQuery=new BasicDBObject("$or",idQueryList);
            }
            else if(!idQueryList.isEmpty()){
                findQuery=idQueryList.get(0);
            }
            logger.info("get Configuration by pay Id Final Query{} ", findQuery );

            FindIterable<Document> output = coll.find(findQuery);
                MongoCursor<Document> cursor = output.iterator();
                while (cursor.hasNext()) {
                    Document doc = cursor.next();
                    configuration = documentToConfigurationObj(doc);
                }
                cursor.close();

        } catch (Exception ex) {
            logger.error("Exception while fetching Fraud Prevention configuration using PayId ", ex);
        }
        logger.info("Configuration fraud prevention by payID {}",configuration);
        return configuration;
    }

    public FraudPreventationConfiguration getConfigByPayIdAndAll(String payId, String currency) {
        MongoDatabase dbIns = mongoInstance.getDB();
        MongoCollection<Document> coll = dbIns.getCollection(
                PropertiesManager.propertiesMap.get(prefix + Constants.FRAUD_PREVENTION_COLLECTION_NAME.getValue()));
        FraudPreventationConfiguration configuration = null;
        try {
             logger.info("Currency List Fraud Prevention Mongo Service{} ",currency);
                 String id = StringUtils.join(PREFIX_CONFIG, payId, "_", currency);
                 //Also check entry for all entry
               // String allId=StringUtils.join(PREFIX_CONFIG,"ALL","_",currency);
                logger.info("ID for get configuration by pay ID "+id);
            BasicDBObject idQueryList=new BasicDBObject("id",id);
                // idQueryList.add(new BasicDBObject("id",allId));
            // BasicDBObject findQuery=new BasicDBObject("$and",idQueryList);
             logger.info("get Configuration by pay Id Final Query{} ",idQueryList );

            FindIterable<Document> output = coll.find(idQueryList);
            MongoCursor<Document> cursor = output.iterator();
            while (cursor.hasNext()) {
                Document doc = cursor.next();
                configuration = documentToConfigurationObj(doc);
            }
            cursor.close();

        } catch (Exception ex) {
            logger.error("Exception while fetching Fraud Prevention configuration using PayId ", ex);
        }
        logger.info("Configuration fraud prevention by payID {}",configuration);
        return configuration;
    }

    public FraudPreventationConfiguration documentToConfigurationObj(Document doc) {
        FraudPreventationConfiguration configuration = new FraudPreventationConfiguration();
        configuration.setId(doc.getString("id"));
        configuration.setOnIpBlocking(doc.getBoolean("onIpBlocking", false));
        configuration.setOnIssuerCountriesBlocking(doc.getBoolean("onIssuerCountriesBlocking", false));
        configuration.setOnUserCountriesBlocking(doc.getBoolean("onUserCountriesBlocking", false));
        configuration.setOnEmailBlocking(doc.getBoolean("onEmailBlocking", false));
        configuration.setOnLimitTxnAmtBlocking(doc.getBoolean("onLimitTxnAmtBlocking", false));
        configuration.setOnCardRangeBlocking(doc.getBoolean("onCardRangeBlocking", false));
        configuration.setOnCardMaskBlocking(doc.getBoolean("onCardMaskBlocking", false));
        configuration.setOnLimitCardTxnBlocking(doc.getBoolean("onLimitCardTxnBlocking", false));
        configuration.setOnPhoneNoBlocking(doc.getBoolean("onPhoneNoBlocking", false));
        configuration.setOnTxnAmountVelocityBlocking(doc.getBoolean("onTxnAmountVelocityBlocking", false));
        configuration.setOnMacBlocking(doc.getBoolean("onMacBlocking", false));
        configuration.setOnNotifyRepeatedMopType(doc.getBoolean("onNotifyRepeatedMopType", false));
        configuration.setOnStateBlocking(doc.getBoolean("onStateBlocking", false));
        configuration.setOnCityBlocking(doc.getBoolean("onCityBlocking", false));
        configuration
                .setOnBlockRepeatedMopTypeForSameDetails(doc.getBoolean("onBlockRepeatedMopTypeForSameDetails", false));
        configuration
                .setOnVpaAddressBlocking(doc.getBoolean("onVpaAddressBlocking", false));
        return configuration;
    }

    public void updateConfigDoc(String payId, String currency, String key, boolean value) throws DataAccessLayerException {
        try {
            MongoDatabase dbIns = mongoInstance.getDB();
            MongoCollection<Document> coll = dbIns.getCollection(PropertiesManager.propertiesMap
                    .get(prefix + Constants.FRAUD_PREVENTION_COLLECTION_NAME.getValue()));
            List<String> currencyList=getCurrency(payId,currency);
            for(int i=0;i<currencyList.size();i++) {
                String id = StringUtils.join(PREFIX_CONFIG, payId, "_" + currencyList.get(i));
                BasicDBObject idQuery = new BasicDBObject("id", id);
                BasicDBObject statusQuery = new BasicDBObject(key, value);
                  BasicDBObject updateQuery = new BasicDBObject("$set", statusQuery);
                 logger.info("Status Query: {}", statusQuery);
                UpdateResult result = coll.updateOne(idQuery, updateQuery);
                logger.info("UpdateResult: {} {} {}", result.getMatchedCount(), result.getModifiedCount(), result.getUpsertedId());
            }
        } catch (Exception ex) {
            logger.error("Exception in Updating Fraud Prevention Configurations", ex);
        }

    }
    public BasicDBObject getCurrencyObject(String id,String currency){
        logger.info(id+ "in currency Object "+currency);
         List<String> currencyList=getCurrency(id,currency);
        if(!currencyList.isEmpty()){
            if(currencyList.size()==1) {
                logger.info("1 Selected Currency "+currencyList.get(0));
                return new BasicDBObject("currency",currencyList.get(0));
            }
            else{
                 return new BasicDBObject("$or", getCurrencyListObj(currencyList));
            }
        }
        else {
            logger.info("!!Currency list is Empty!!");
            return new BasicDBObject();
        }
    }

    public BasicDBObject getCurrencyObjectForSelectedMerch(String id,String currency){
        logger.info(id+ "in currency Object "+currency);
        List<String> currencyList=getCurrencyForSelectedMerchant(id,currency);
        if(!currencyList.isEmpty()){
            if(currencyList.size()==1) {
                logger.info("1 Selected Currency "+currencyList.get(0));
                return new BasicDBObject("currency",currencyList.get(0));
            }
            else{
                return new BasicDBObject("$or", getCurrencyListObj(currencyList));
            }
        }
        else {
            logger.info("!!Currency list is Empty!!");
            return new BasicDBObject();
        }
    }
    public List<BasicDBObject> getCurrencyListObj(List<String> currencyList){
        List<BasicDBObject> finalList=new ArrayList<>();
        if(!currencyList.isEmpty()){
            for(int i=0;i<currencyList.size();i++){
                finalList.add(new BasicDBObject("currency",currencyList.get(i)));
            }
            return finalList;
        }
        return new ArrayList<>();
    }

    public void updateConfigDoc(String payId, Map<String, Boolean> updateFields,String currency) throws DataAccessLayerException {
        try {
            MongoDatabase dbIns = mongoInstance.getDB();
            MongoCollection<Document> coll = dbIns.getCollection(PropertiesManager.propertiesMap
                    .get(prefix + Constants.FRAUD_PREVENTION_COLLECTION_NAME.getValue()));
            String id = StringUtils.join(PREFIX_CONFIG, payId);
            List<String> currencyList=getCurrency(payId,currency);
            for(int i=0;i<currencyList.size();i++){
                BasicDBObject idQuery = new BasicDBObject("id", id);
                BasicDBObject currencyQuery=new BasicDBObject("currency",currencyList.get(i));
                List<BasicDBObject>filterQueryAndCurrencyQuery=new ArrayList<>();
                filterQueryAndCurrencyQuery.add(idQuery);
                filterQueryAndCurrencyQuery.add(currencyQuery);
                BasicDBObject filterQuery=new BasicDBObject("$and",filterQueryAndCurrencyQuery);
                BasicDBObject statusQuery = new BasicDBObject(updateFields);
                BasicDBObject updateQuery = new BasicDBObject("$set", statusQuery);
                coll.updateOne(filterQuery, updateQuery);
                logger.info("updateConfigDoc:: existing rules payId ={}, updateFields={}", payId, statusQuery);

            }

        } catch (Exception ex) {
            logger.error("Exception in Updating Fraud Prevention Configurations", ex);
        }

    }

    public List<FraudPreventionObj> findNotifiedRules() {
        List<FraudPreventionObj> notifiedRules = new ArrayList<FraudPreventionObj>();

        MongoDatabase dbIns = mongoInstance.getDB();
        MongoCollection<Document> coll = dbIns.getCollection(
                PropertiesManager.propertiesMap.get(prefix + Constants.FRAUD_PREVENTION_COLLECTION_NAME.getValue()));
        try {
            List<BasicDBObject> paramConditionLst = new ArrayList<BasicDBObject>();
            paramConditionLst.add(new BasicDBObject("status", TDRStatus.ACTIVE.getName()));
            paramConditionLst.add(new BasicDBObject("notified", true));

            BasicDBObject finalquery = new BasicDBObject("$and", paramConditionLst);
            FindIterable<Document> output = coll.find(finalquery);
            MongoCursor<Document> cursor = output.iterator();

            while (cursor.hasNext()) {
                Document doc = cursor.next();
                FraudPreventionObj rule = documentToFraudPreventionObj(doc);
                notifiedRules.add(rule);
            }
            cursor.close();
        } catch (Exception ex) {
            logger.error("findNotifiedRules:: failed.", ex);
        }
        return notifiedRules;
    }

    // Added By Sweety

    public boolean CheckFrmRuleExist(String payId, String currency) {
        boolean ret_Val = false;
        MongoCursor<Document> cursor = null;
        List<String> fraudType = new ArrayList<String>();
        fraudType.add(FraudRuleType.BLOCK_TICKET_LIMIT.getValue());
        fraudType.add(FraudRuleType.BLOCK_TRANSACTION_LIMIT.getValue());
        fraudType.add(FraudRuleType.BLOCK_MOP_LIMIT.getValue());


        try {
            logger.info("Inside findFrmRuleExist (payId): " + payId + "Currency ::" + currency);
            List<BasicDBObject> condList = new ArrayList<BasicDBObject>();
            condList.add(new BasicDBObject("payId", payId));
            condList.add(new BasicDBObject("currency", currency));
            condList.add(new BasicDBObject("fraudType", new BasicDBObject("$in", fraudType)));
            BasicDBObject searchQuery = new BasicDBObject("$and", condList);
            logger.info("Inside findFrmRuleExist PAY_ID={}, Query={} ", payId, searchQuery);
            MongoDatabase dbIns = mongoInstance.getDB();
            MongoCollection<Document> collection = dbIns.getCollection(PropertiesManager.propertiesMap
                    .get(prefix + Constants.FRAUD_PREVENTION_COLLECTION_NAME.getValue()));
            cursor = collection.find(searchQuery).iterator();
            if (cursor.hasNext()) {
                ret_Val = true;
            }
        } catch (Exception exception) {
            String message = "Error while fetching  details based on payId from database";
            logger.error(message, exception);
        } finally {
            cursor.close();
        }
        return ret_Val;
    }

    //NEW Check for FRM from TDR with currency wise
    public String checkFrmFromTdr(String payId) {

        // SQL Data :::
        Map<String, Set<String>> mapData = new HashMap<>();
        List<Object[]> objectDao = userDao.findPaymentTypeForFrm(payId);
        int length = objectDao.size();
        logger.info("Length :" + length);

        for (Object[] objects : objectDao) {
            String key = String.valueOf(objects[0]);
            if (!mapData.containsKey(key)) {
                Set<String> obj = new HashSet<>();
                mapData.put(key, obj);
                obj.add(FraudRuleType.BLOCK_TICKET_LIMIT.getValue());
                obj.add(FraudRuleType.BLOCK_TRANSACTION_LIMIT.getValue());
            }
            PaymentType paymentType = PaymentType.getInstanceUsingStringValue(String.valueOf(objects[1]));
            if (paymentType != null) {
                mapData.get(key).add(paymentType.getCode());
            }
        }
        logger.info("MAP DATA :::" + mapData);


        // Mongo Data :::
        MongoDatabase dbIns = mongoInstance.getDB();
        MongoCollection<Document> collection = dbIns.getCollection(PropertiesManager.propertiesMap
                .get(prefix + Constants.FRAUD_PREVENTION_COLLECTION_NAME.getValue()));

        List<BasicDBObject> findQuery = new ArrayList<BasicDBObject>();
        findQuery.add(new BasicDBObject("payId", payId));
        findQuery.add(new BasicDBObject("status", "Active"));
        BasicDBObject fetchQuery = new BasicDBObject("$and", findQuery);
        logger.info("Query : " + findQuery);
        FindIterable<Document> output = collection.find(fetchQuery);
        for (Document document : output) {
           // logger.info("document:{} ",document);
            String currency = document.getString("currency");
            String fraudType = document.getString("fraudType");
            String paymentType = document.getString("paymentType");

            Set<String> set = mapData.getOrDefault(currency, null);
            if (set != null) {
                switch (fraudType) {
                    case "BLOCK_TICKET_LIMIT":
                    case "BLOCK_TRANSACTION_LIMIT":
                        set.remove(fraudType);
                        break;
                    case "BLOCK_MOP_LIMIT":
                        set.remove(paymentType);
                        break;
                }
            }

        }
        for (Map.Entry<String, Set<String>> entry : mapData.entrySet()) {
            Iterator<String> iterator = entry.getValue().iterator();
            if (iterator.hasNext()) {
                String temp = iterator.next();
                switch (temp) {
                    case "BLOCK_TICKET_LIMIT":
                    case "BLOCK_TRANSACTION_LIMIT":
                        return String.format("Please set %s Fraud Rule before merchant activation", temp);
                    default:
                        PaymentType paymentType = PaymentType.getInstanceUsingCode(temp);
                        if (paymentType != null) {
                            return String.format("Please set BLOCK_PAYMENT_LIMIT Fraud Rule for %s in %s currency before merchant activation", paymentType.getName(), multCurrencyCodeDao.getCurrencyNamebyCode(entry.getKey()));
                        } else {
                            return "There's some issue with Fraud Rule";
                        }
                }
            }
        }
        return "success";
    }

    //Previous FRM Check Code without currency wise.
//    public String checkFraudRuleForMerchant(String payId) {
//        MongoCursor<Document> cursor = null;
//        boolean blockTicketLimit = false;
//        boolean blockTxnAmount = false;
//        boolean blockMopLimitUP = false;
//        boolean blockMopLimitDC = false;
//        boolean blockMopLimitCC = false;
//        boolean blockMopLimitNB = false;
//        // boolean blockMopLimitQR = false;
//        boolean blockMopLimitWL = false;
//        try {
//
//            MongoDatabase dbIns = mongoInstance.getDB();
//            MongoCollection<Document> collection = dbIns.getCollection(PropertiesManager.propertiesMap
//                    .get(prefix + Constants.FRAUD_PREVENTION_COLLECTION_NAME.getValue()));
//
//            List<BasicDBObject> findQuery = new ArrayList<BasicDBObject>();
//            findQuery.add(new BasicDBObject("payId", payId));
//            findQuery.add(new BasicDBObject("status", "Active"));
//            BasicDBObject fetchQuery = new BasicDBObject("$and", findQuery);
//            logger.info("Query : " + findQuery);
//            FindIterable<Document> output = collection.find(fetchQuery);
//            cursor = output.iterator();
//            while (cursor.hasNext()) {
//                Document documentObj = (Document) cursor.next();
//                if (StringUtils.equalsIgnoreCase(documentObj.getString("fraudType"), "BLOCK_TRANSACTION_LIMIT")) {
//                    blockTxnAmount = true;
//                }
//                if (StringUtils.equalsIgnoreCase(documentObj.getString("fraudType"), "BLOCK_TICKET_LIMIT")) {
//                    blockTicketLimit = true;
//                }
//                if (StringUtils.equalsIgnoreCase(documentObj.getString("fraudType"), "BLOCK_MOP_LIMIT")) {
//                    blockMopLimitDC = documentObj.getString("paymentType").equalsIgnoreCase("DC") ? true
//                            : blockMopLimitDC;
//                    blockMopLimitCC = documentObj.getString("paymentType").equalsIgnoreCase("CC") ? true
//                            : blockMopLimitCC;
//                    blockMopLimitNB = documentObj.getString("paymentType").equalsIgnoreCase("NB") ? true
//                            : blockMopLimitNB;
//                    blockMopLimitWL = documentObj.getString("paymentType").equalsIgnoreCase("WL") ? true
//                            : blockMopLimitWL;
//                    blockMopLimitUP = documentObj.getString("paymentType").equalsIgnoreCase("UP") ? true
//                            : blockMopLimitUP;
//                    // blockMopLimitQR = documentObj.getString("paymentType").equalsIgnoreCase("QR")
//                    // ? true : blockMopLimitQR;
//                }
//
//            }
//        } catch (Exception exception) {
//            String message = "Error while fetching  details based on payId from database";
//            logger.error(message, exception);
//        } finally {
//            cursor.close();
//        }
//        if (!blockTicketLimit) {
//            return "Please set BLOCK_TICKET_LIMIT Fraud Rule before merchant activation";
//        }
//        if (!blockTxnAmount) {
//            return "Please set BLOCK_TRANSACTION_LIMIT Fraud Rule before merchant activation";
//        }
//        if (!blockMopLimitDC) {
//            return "Please set BLOCK_MOP_LIMIT Fraud Rule for DEBIT-CARD before merchant activation";
//        }
//        if (!blockMopLimitCC) {
//            return "Please set BLOCK_MOP_LIMIT Fraud Rule for CREDIT-CARD before merchant activation";
//        }
//        if (!blockMopLimitNB) {
//            return "Please set BLOCK_MOP_LIMIT Fraud Rule for NET-BANKING before merchant activation";
//        }
//        if (!blockMopLimitUP) {
//            return "Please set BLOCK_MOP_LIMIT Fraud Rule for UPI before merchant activation";
//        }
//        if (!blockMopLimitWL) {
//            return "Please set BLOCK_MOP_LIMIT Fraud Rule for WALLET before merchant activation";
//        }
//        /*
//         * if(!blockMopLimitQR) { return
//         * "Please set BLOCK_MOP_LIMIT Fraud Rule For QR-CODE before merchant activation"
//         * ; }
//         */
//        return "success";
//    }

    public List<FraudManagementDto> getFrmDashboardData(String dateFrom, String dateTo) {
        logger.info("Date From : " + dateFrom + "\t Date to : " + dateTo);
        List<FraudManagementDto> frmDto = new ArrayList<FraudManagementDto>();
        MongoDatabase dbIns = mongoInstance.getDB();
        MongoCollection<Document> coll = dbIns.getCollection(
                PropertiesManager.propertiesMap.get(prefix + Constants.TRANSACTION_STATUS_COLLECTION.getValue()));
        List<Document> cursor = Arrays.asList(new Document("$match",
                        new Document("DATE_INDEX",
                                new Document("$gte", dateFrom.replaceAll("-", "")).append("$lte", dateTo.replaceAll("-", "")))
                                .append("STATUS",
                                        new Document("$in", Arrays.asList("RNS", "Captured", "Settled")))
                                .append("TXNTYPE",
                                        new Document("$in", Arrays.asList("SALE", "RECO")))),
                new Document("$group",
                        new Document("_id",
                                new Document("PAY_ID", "$PAY_ID")
                                        .append("DATE_INDEX", "$DATE_INDEX"))
                                .append("SUM(AMOUNT)",
                                        new Document("$sum",
                                                new Document("$toDouble", "$AMOUNT")))),
                new Document("$project",
                        new Document("PAY_ID", "$_id.PAY_ID")
                                .append("DATE_INDEX", "$_id.DATE_INDEX")
                                .append("_id", 0L)
                                .append("VOLUME", "$SUM(AMOUNT)")),
                new Document("$sort",
                        new Document("VOLUME", -1L)),
                new Document("$limit", 10L));


        logger.info("Query :" + cursor);
        MongoCursor<Document> mongoCursor = coll.aggregate(cursor).iterator();

        while (mongoCursor.hasNext()) {
            FraudManagementDto fraudManagementDto = new FraudManagementDto();
            Document document = (Document) mongoCursor.next();
            User user = userDao.findPayId("" + document.get("PAY_ID"));
            fraudManagementDto.setPayId(String.valueOf(document.get("PAY_ID") == null ? "NA" : document.get("PAY_ID")));
            fraudManagementDto.setDateIndex(
                    String.valueOf(document.get("DATE_INDEX") == null ? "NA" : document.get("DATE_INDEX")));
            fraudManagementDto
                    .setVolume(String.valueOf(document.get("VOLUME") == null ? "NA" : document.get("VOLUME")));
            if (user != null) {
                fraudManagementDto.setBusinessName((user.getBusinessName() == null) ? "NA" : user.getBusinessName());
            } else {
                fraudManagementDto.setBusinessName("NA");
            }
            frmDto.add(fraudManagementDto);
        }
        logger.info("getFrmDataVolume return Size : " + frmDto.size());
        return frmDto;
    }

    public List<FraudManagementDto> getFrmDataCount(String dateFrom, String dateTo) {
        logger.info("Date From : " + dateFrom + "\t Date to : " + dateTo);
        List<FraudManagementDto> frmDto = new ArrayList<FraudManagementDto>();
        MongoDatabase dbIns = mongoInstance.getDB();
        MongoCollection<Document> coll = dbIns.getCollection(
                PropertiesManager.propertiesMap.get(prefix + Constants.TRANSACTION_STATUS_COLLECTION.getValue()));
        List<Document> cursor = Arrays.asList(
                new Document("$match",
                        new Document("DATE_INDEX", new Document("$gte", dateFrom.replaceAll("-", "")).append("$lte", dateTo.replaceAll("-", "")))
                                .append("STATUS", new Document("$in", Arrays.asList("RNS", "Captured", "Settled")))
                                .append("TXNTYPE", new Document("$in", Arrays.asList("SALE", "RECO")))),
                new Document("$group",
                        new Document("_id", new Document("PAY_ID", "$PAY_ID").append("DATE_INDEX", "$DATE_INDEX"))
                                .append("SUM(COUNT)", new Document("$sum", 1L))),
                new Document("$project", new Document("PAY_ID", "$_id.PAY_ID").append("DATE_INDEX", "$_id.DATE_INDEX")
                        .append("_id", 0L).append("COUNT", "$SUM(COUNT)")),
                new Document("$sort", new Document("COUNT", -1L)),
                new Document("$limit", 10L));
        logger.info("Query :" + cursor);
        MongoCursor<Document> mongoCursor = coll.aggregate(cursor).iterator();

        while (mongoCursor.hasNext()) {
            FraudManagementDto fraudManagementDto = new FraudManagementDto();
            Document document = (Document) mongoCursor.next();
            User user = userDao.findPayId("" + document.get("PAY_ID"));
            fraudManagementDto.setPayId(String.valueOf(document.get("PAY_ID") == null ? "NA" : document.get("PAY_ID")));
            fraudManagementDto.setDateIndex(
                    String.valueOf(document.get("DATE_INDEX") == null ? "NA" : document.get("DATE_INDEX")));
            fraudManagementDto.setCount(String.valueOf(document.get("COUNT") == null ? "NA" : document.get("COUNT")));
            if (user != null) {
                fraudManagementDto.setBusinessName((user.getBusinessName() == null) ? "NA" : user.getBusinessName());
            } else {
                fraudManagementDto.setBusinessName("NA");
            }
            frmDto.add(fraudManagementDto);
        }
        logger.info("getFrmDataCount return Size : " + frmDto.size());
        return frmDto;
    }

    public List<FraudManagementDto> getFrmDataLeastPerformer(String dateFrom, String dateTo) {
        logger.info("Date From : " + dateFrom + "\t Date to : " + dateTo);
        List<FraudManagementDto> frmDto = new ArrayList<FraudManagementDto>();
        MongoDatabase dbIns = mongoInstance.getDB();
        MongoCollection<Document> coll = dbIns.getCollection(
                PropertiesManager.propertiesMap.get(prefix + Constants.TRANSACTION_STATUS_COLLECTION.getValue()));
        List<Document> cursor = Arrays.asList(
                new Document("$match",
                        new Document("DATE_INDEX",
                                new Document("$gte", dateFrom.replaceAll("-", "")).append("$lte",
                                        dateTo.replaceAll("-", ""))).append("STATUS", "Failed").append("TXNTYPE",
                                "SALE")),
                new Document("$group",
                        new Document("_id", new Document("PAY_ID", "$PAY_ID").append("DATE_INDEX", "$DATE_INDEX"))
                                .append("SUM(COUNT)", new Document("$sum", 1L))),
                new Document("$project", new Document("PAY_ID", "$_id.PAY_ID").append("DATE_INDEX", "$_id.DATE_INDEX")
                        .append("_id", 0L).append("COUNT", "$SUM(COUNT)")),
                new Document("$sort", new Document("COUNT", -1L)),
                new Document("$limit", 10L));
        logger.info("Query :" + cursor);
        MongoCursor<Document> mongoCursor = coll.aggregate(cursor).iterator();

        while (mongoCursor.hasNext()) {
            FraudManagementDto fraudManagementDto = new FraudManagementDto();
            Document document = (Document) mongoCursor.next();
            User user = userDao.findPayId("" + document.get("PAY_ID"));
            fraudManagementDto.setPayId(String.valueOf(document.get("PAY_ID") == null ? "NA" : document.get("PAY_ID")));
            fraudManagementDto.setDateIndex(
                    String.valueOf(document.get("DATE_INDEX") == null ? "NA" : document.get("DATE_INDEX")));
            fraudManagementDto.setCount(String.valueOf(document.get("COUNT") == null ? "NA" : document.get("COUNT")));
            if (user != null) {
                fraudManagementDto.setBusinessName((user.getBusinessName() == null) ? "NA" : user.getBusinessName());
            } else {
                fraudManagementDto.setBusinessName("NA");
            }
            frmDto.add(fraudManagementDto);
        }
        logger.info("getFrmLeastPerformer return Size : " + frmDto.size());
        return frmDto;
    }

    public List<FraudManagementDto> getFrmBreachData(String dateFrom, String dateTo) {
        logger.info("Date From : " + dateFrom + "\t Date to : " + dateTo);
        List<FraudManagementDto> frmDto = new ArrayList<FraudManagementDto>();
        MongoDatabase dbIns = mongoInstance.getDB();
        MongoCollection<Document> coll = dbIns.getCollection("fraudTransactions");
        List<Document> cursor = Arrays.asList(new Document("$match",
                        new Document("fraudType",
                                new Document("$exists", true))
                                .append("date",
                                        new Document("$gte", dateFrom)
                                                .append("$lte", dateTo))),
                new Document("$project",
                        new Document("merchantName", 1L)
                                .append("fraudType", 1L)
                                .append("date",
                                        new Document("$substr", Arrays.asList("$date", 0L, 10L)))),
                new Document("$group",
                        new Document("_id",
                                new Document("merchantName", "$merchantName")
                                        .append("date", "$date"))
                                .append("fraudType",
                                        new Document("$push", "$fraudType"))
                                .append("count",
                                        new Document("$sum", 1L))),
                new Document("$project",
                        new Document("_id", 0L)
                                .append("merchantName", "$_id.merchantName")
                                .append("date", "$_id.date")
                                .append("fraudType", 1L)
                                .append("count", 1L)),
                new Document("$sort",
                        new Document("count", -1L)),
                new Document("$limit", 10L));

        logger.info("Query :" + cursor);
        MongoCursor<Document> mongoCursor = coll.aggregate(cursor).iterator();

        while (mongoCursor.hasNext()) {
            FraudManagementDto fraudManagementDto = new FraudManagementDto();
            Document document = (Document) mongoCursor.next();
            fraudManagementDto.setDateIndex(
                    String.valueOf(document.get("DATE_INDEX") == null ? "NA" : document.get("DATE_INDEX")));
            fraudManagementDto.setCount(String.valueOf(document.get("COUNT") == null ? "NA" : document.get("COUNT")));

            fraudManagementDto.setBusinessName(String.valueOf(document.get("merchantName") == null ? "NA" : document.get("merchantName")));
            frmDto.add(fraudManagementDto);
        }
        logger.info("getFrmBreach return Size : " + frmDto.size());
        return frmDto;
    }

    public List<FraudManagementDto> prepareData(String fromDate, String toDate) {
        List<FraudManagementDto> byVolume = getFrmDashboardData(fromDate, toDate);
        List<FraudManagementDto> byCount = getFrmDataCount(fromDate, toDate);
        List<FraudManagementDto> byLeastPerformer = getFrmDataLeastPerformer(fromDate, toDate);
        List<FraudManagementDto> byFrmBreach = getFrmBreachData(fromDate, toDate);
        List<FraudManagementDto> result = new ArrayList<FraudManagementDto>();
        int maxSize = (byVolume.size() > byCount.size() && byVolume.size() > byLeastPerformer.size() && byVolume.size() > byFrmBreach.size()) ? byVolume.size() : ((byCount.size() > byLeastPerformer.size() && byCount.size() > byFrmBreach.size()) ? byCount.size() : (byLeastPerformer.size() > byFrmBreach.size()) ? byLeastPerformer.size() : byFrmBreach.size());
        System.out.println("maxSize" + maxSize);

        for (int index = 0; index < maxSize; index++) {
            FraudManagementDto item = new FraudManagementDto();
            if (byVolume.size() != 0 && byVolume.size() > index) {
                item.setMerchantByVolume(byVolume.get(index).getBusinessName());
            } else {
                item.setMerchantByVolume("NA");
            }
            if (byCount.size() != 0 && byCount.size() > index) {
                item.setMerchantByCount(byCount.get(index).getBusinessName());
            } else {
                item.setMerchantByCount("NA");
            }
            if (byLeastPerformer.size() != 0 && byLeastPerformer.size() > index) {
                item.setLeastPerformerTSR(byLeastPerformer.get(index).getBusinessName());
            } else {
                item.setLeastPerformerTSR("NA");
            }
            if (byFrmBreach.size() != 0 && byFrmBreach.size() > index) {
                item.setFrmBreachValue(byFrmBreach.get(index).getBusinessName());
            } else {
                item.setFrmBreachValue("NA");
            }
            result.add(item);

        }

        return result;
    }

    public List<FraudRiskModel> getFrmDetails(String merchantPayId, String currency) {
        List<FraudRiskModel> fraudObj = new ArrayList<FraudRiskModel>();
        try {
            MongoDatabase dbIns = mongoInstance.getDB();
            MongoCollection<Document> coll = dbIns.getCollection(
                    PropertiesManager.propertiesMap.get(prefix + Constants.FRAUD_PREVENTION_COLLECTION_NAME.getValue()));
            logger.info("Inside findFrmRuleExist (payId): " + merchantPayId);
            List<Document> cursor = Arrays.asList(new Document("$match",
                    new Document("payId", merchantPayId)
                            .append("currency", currency)
                            .append("fraudType", "BLOCK_MOP_LIMIT")
                            .append("status", "Active")));

            logger.info("Query :" + cursor);
            MongoCursor<Document> mongoCursor = coll.aggregate(cursor).iterator();
            while (mongoCursor.hasNext()) {
                Document doc = (Document) mongoCursor.next();
                FraudRiskModel obj = new FraudRiskModel();
                obj.setId(doc.getString("_id"));
                obj.setPayId(doc.getString("payId"));
                obj.setMinTransactionAmount(doc.getString("minTransactionAmount"));
                obj.setMaxTransactionAmount(doc.getString("maxTransactionAmount"));
                obj.setPaymentType(doc.getString("paymentType"));
                obj.setDailyAmount(doc.getString("dailyAmount"));
                obj.setWeeklyAmount(doc.getString("weeklyAmount"));
                obj.setMonthlyAmount(doc.getString("monthlyAmount"));
                obj.setMerchantProfile(doc.getString("FRM_Merchant_Profile"));
                fraudObj.add(obj);
            }

            mongoCursor.close();

        } catch (Exception e) {
            e.printStackTrace();

        }
        return fraudObj;
    }

    public List<FraudRiskModel> getFraudDataByVolume(String merchantPayId, String currency) {
        List<FraudRiskModel> fraudObj = new ArrayList<FraudRiskModel>();
        try {
            MongoDatabase dbIns = mongoInstance.getDB();
            MongoCollection<Document> coll = dbIns.getCollection(
                    PropertiesManager.propertiesMap.get(prefix + Constants.FRAUD_PREVENTION_COLLECTION_NAME.getValue()));
            logger.info("Inside findFrmRuleExist (payId): " + merchantPayId);
            List<Document> cursor = Arrays.asList(new Document("$match",
                    new Document("payId", merchantPayId)
                            .append("currency", currency)
                            .append("fraudType", "BLOCK_TRANSACTION_LIMIT")
                            .append("status", "Active")));

            logger.info("Query :" + cursor);
            MongoCursor<Document> mongoCursor = coll.aggregate(cursor).iterator();
            while (mongoCursor.hasNext()) {
                Document doc = (Document) mongoCursor.next();
                FraudRiskModel obj = new FraudRiskModel();
                obj.setId(doc.getString("_id"));
                obj.setPayId(doc.getString("payId"));
                obj.setDailyAmount(doc.getString("dailyAmount"));
                obj.setWeeklyAmount(doc.getString("weeklyAmount"));
                obj.setMonthlyAmount(doc.getString("monthlyAmount"));
                obj.setMerchantProfile(doc.getString("FRM_Merchant_Profile"));
                fraudObj.add(obj);
            }

            mongoCursor.close();

        } catch (Exception e) {
            e.printStackTrace();

        }
        return fraudObj;
    }

    public List<FraudRiskModel> getFraudDataByTicket(String merchantPayId, String currency) {
        List<FraudRiskModel> fraudObj = new ArrayList<FraudRiskModel>();
        try {
            MongoDatabase dbIns = mongoInstance.getDB();
            MongoCollection<Document> coll = dbIns.getCollection(
                    PropertiesManager.propertiesMap.get(prefix + Constants.FRAUD_PREVENTION_COLLECTION_NAME.getValue()));
            logger.info("Inside findFrmRuleExist (payId): " + merchantPayId);
            List<Document> cursor = Arrays.asList(new Document("$match",
                    new Document("payId", merchantPayId)
                            .append("currency", currency)
                            .append("fraudType", "BLOCK_TICKET_LIMIT")
                            .append("status", "Active")));

            logger.info("Query :" + cursor);
            MongoCursor<Document> mongoCursor = coll.aggregate(cursor).iterator();
            while (mongoCursor.hasNext()) {
                Document doc = (Document) mongoCursor.next();
                FraudRiskModel obj = new FraudRiskModel();
                obj.setId(doc.getString("_id"));
                obj.setPayId(doc.getString("payId"));
                obj.setMinTransactionAmount(doc.getString("minTransactionAmount"));
                obj.setMaxTransactionAmount(doc.getString("maxTransactionAmount"));
                obj.setMerchantProfile(doc.getString("FRM_Merchant_Profile"));
                fraudObj.add(obj);
            }

            mongoCursor.close();

        } catch (Exception e) {
            e.printStackTrace();

        }
        return fraudObj;
    }


}
