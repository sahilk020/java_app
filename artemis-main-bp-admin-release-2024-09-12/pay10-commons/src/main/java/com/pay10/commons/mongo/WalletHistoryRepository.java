package com.pay10.commons.mongo;

import java.text.DecimalFormat;
import java.util.*;

import com.pay10.commons.dto.AcquirerMerchantWiseDetailDTO;
import com.pay10.commons.user.UserDao;
import org.apache.commons.lang3.StringUtils;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.google.gson.Gson;
import com.mongodb.BasicDBObject;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.UpdateOptions;
import com.mongodb.client.model.Updates;
import com.mongodb.client.result.UpdateResult;
import com.pay10.commons.util.FieldType;
import com.pay10.commons.util.PropertiesManager;
import com.pay10.commons.util.StatusType;
import com.pay10.commons.util.TransactionType;

import static com.mongodb.client.model.Filters.and;
import static com.mongodb.client.model.Filters.eq;

@Component
public class WalletHistoryRepository {

    @SuppressWarnings("unused")
    private static final String prefix = "MONGO_DB_";

    private Logger logger = LoggerFactory.getLogger(WalletHistoryRepository.class);

    @Autowired
    PropertiesManager propertiesManager;
    @Autowired
    private MongoInstance mongoInstance;

    @Autowired
    private UserDao userDao;

    public Map<String, Object> findMerchantFundByPayId(String payId, String currencyName) {
        Map<String, Object> walletMap = new HashMap<>();
        try {
            MongoDatabase dbIns = mongoInstance.getDB();
            MongoCollection<Document> coll = dbIns.getCollection("WalletHistory");

            List<BasicDBObject> paramConditionLst = new ArrayList<BasicDBObject>();

            if (StringUtils.isNotBlank(payId)) {
                paramConditionLst.add(new BasicDBObject("payId", payId));
            }

            if (StringUtils.isNotBlank(currencyName)) {
                paramConditionLst.add(new BasicDBObject("currency", currencyName));
            }

            BasicDBObject queryExecute = new BasicDBObject("$and", paramConditionLst);
            MongoCursor<Document> cursor = coll.find(queryExecute).iterator();

            if (cursor.hasNext()) {
                Document document = cursor.next();
                logger.info("Document: {}", document);
                walletMap.put("payId", document.getString("payId"));
                walletMap.put("credit", document.getString("credit"));
                walletMap.put("currency", document.getString("currency"));
                walletMap.put("debit", document.getString("debit"));
                walletMap.put("finalBalance", decimalConversion(document.getString("finalBalance")));
                walletMap.put("totalBalance", decimalConversion(document.getString("totalBalance")));
            }

        } catch (Exception e) {
            logger.error("Exception in : ", e);
        }
        return walletMap;
    }

    public List<Map<String, Object>> findMerchantFundByPayId(String payId) {
        List<Map<String, Object>> listMaps = new ArrayList<Map<String, Object>>();
        try {
            MongoDatabase dbIns = mongoInstance.getDB();
            MongoCollection<Document> coll = dbIns.getCollection("WalletHistory");

            List<BasicDBObject> paramConditionLst = new ArrayList<BasicDBObject>();

            if (StringUtils.isNotBlank(payId)) {
                paramConditionLst.add(new BasicDBObject("payId", payId));
            }

            BasicDBObject queryExecute = new BasicDBObject("$and", paramConditionLst);
            MongoCursor<Document> cursor = coll.find(queryExecute).iterator();

            while (cursor.hasNext()) {
                Map<String, Object> walletMap = new HashMap<>();
                Document document = cursor.next();
                logger.info("Document : {}", document);
                walletMap.put("payId", document.getString("payId"));
                walletMap.put("credit", document.getString("credit"));
                walletMap.put("currency", document.getString("currency"));
                walletMap.put("debit", document.getString("debit"));
                walletMap.put("finalBalance", decimalConversion(document.getString("finalBalance")));
                walletMap.put("totalBalance", decimalConversion(document.getString("totalBalance")));
                listMaps.add(walletMap);
            }

        } catch (Exception e) {
            logger.error("Exception in : ", e);
        }
        logger.info("List : {}", new Gson().toJson(listMaps));
        return listMaps;
    }

    public List<Object> findMerchantFundByPayIdV2(String payId, String currency) {
        List<Object> list = new ArrayList<Object>();
        MongoDatabase dbIns = mongoInstance.getDB();
        MongoCollection<Document> coll = dbIns.getCollection("WalletHistory");
        try {
            List<Bson> filters = new ArrayList<>();
            if (StringUtils.isNotBlank(payId)) {
                filters.add(eq("payId", payId));
            }
            if (StringUtils.isNotBlank(currency)) {
                filters.add(eq("currency", currency));
            }

            Bson query = filters.isEmpty() ? new Document() : and(filters);
            try (MongoCursor<Document> cursor = coll.find(query).iterator()) {
                while (cursor.hasNext()) {
                    Document document = cursor.next();
                    AcquirerMerchantWiseDetailDTO detailDTO = new AcquirerMerchantWiseDetailDTO();
                    detailDTO.setAcquirer(""); // Consider if this field is necessary or should be populated
                    String businessName = userDao.getBusinessNameByPayId(document.getString("payId"));
                    detailDTO.setBusinessName(businessName == null ? "NA" : businessName);
                    detailDTO.setAvailableBalance(decimalConversion(Optional.ofNullable(document.getString("finalBalance")).orElse("0")));
                    detailDTO.setCurrency(document.getString("currency"));
                    detailDTO.setTotalBalance(decimalConversion(Optional.ofNullable(document.getString("totalBalance")).orElse("0")));
                    detailDTO.setCredit(Optional.ofNullable(document.getString("credit")).orElse("0"));
                    detailDTO.setDebit(Optional.ofNullable(document.getString("debit")).orElse("0"));
                    list.add(detailDTO);
                }
            }

        } catch (Exception e) {
            logger.error("Exception in : ", e);
        }
        logger.info("List : {}", new Gson().toJson(list));
        return list;
    }

    public boolean findMerchantFundByPayIdAndCurrency(String payId, String currencyName) {
        try {
            MongoDatabase dbIns = mongoInstance.getDB();
            MongoCollection<Document> coll = dbIns.getCollection("WalletHistory");

            List<BasicDBObject> paramConditionLst = new ArrayList<BasicDBObject>();

            if (StringUtils.isNotBlank(payId)) {
                paramConditionLst.add(new BasicDBObject("payId", payId));
            }

            if (StringUtils.isNotBlank(currencyName)) {
                paramConditionLst.add(new BasicDBObject("currency", currencyName));
            }

            BasicDBObject queryExecute = new BasicDBObject("$and", paramConditionLst);
            MongoCursor<Document> cursor = coll.find(queryExecute).iterator();

            if (cursor.hasNext()) {
                Document document = cursor.next();
                logger.error("WALLETHISTORY : {}", new Gson().toJson(document));
                return true;
            }

        } catch (Exception e) {
            logger.error("Exception in : ", e);
        }
        return false;
    }

    public boolean createMerchantWalletByPayIdAndCurrencyName(String payId, String currencyName) {
        try {
            MongoDatabase dbIns = mongoInstance.getDB();
            MongoCollection<Document> coll = dbIns.getCollection("WalletHistory");

            Document document = new Document();
            document.put("payId", payId);
            document.put("currency", currencyName);
            document.put("credit", decimalConversion("0.0"));
            document.put("debit", decimalConversion("0.0"));
            document.put("finalBalance", decimalConversion("0.0"));
            document.put("totalBalance", decimalConversion("0.0"));
            System.out.println("WALLETHISTORY CREATE : " + new Gson().toJson(document));
            coll.insertOne(document);
            return true;

        } catch (Exception e) {
            logger.error("Exception in : ", e);
            return false;
        }
    }

    public boolean createMerchantWalletByPayIdAndCurrencyNameWithValues(String payId, String currencyName,
                                                                        String creditVal, String debitVal, String finalBalance, String totalBalance) {
        try {
            MongoDatabase dbIns = mongoInstance.getDB();
            MongoCollection<Document> coll = dbIns.getCollection("WalletHistory");

            Document document = new Document();
            document.put("payId", payId);
            document.put("currency", currencyName);
            document.put("credit", decimalConversion(creditVal));
            document.put("debit", decimalConversion(debitVal));
            document.put("finalBalance", decimalConversion(finalBalance));
            document.put("totalBalance", decimalConversion(totalBalance));
            logger.info("WALLETHISTORY CREATE : {}", new Gson().toJson(document));
            coll.insertOne(document);
            return true;

        } catch (Exception e) {
            logger.error("Exception in : {}", e);
            return false;
        }
    }

    public void updateBalanceByPayIdAndCurrency(String payId, String currencyName, String updatedBalance,
                                                String txnType, String amount, String updatedTotalBalance) {
        try {
            MongoDatabase dbIns = mongoInstance.getDB();
            MongoCollection<Document> coll = dbIns.getCollection("WalletHistory");

            List<BasicDBObject> findQuery = new ArrayList<BasicDBObject>();
            findQuery.add(new BasicDBObject("currency", currencyName));
            findQuery.add(new BasicDBObject("payId", payId));

            logger.info("DB DATA : {}", new Gson().toJson(findQuery));

            Bson updates = Updates.combine(Updates.set("finalBalance", decimalConversion(updatedBalance)),
                    Updates.set("totalBalance", decimalConversion(updatedTotalBalance)),
                    Updates.set(txnType.contains("credit") ? "credit" : "debit", decimalConversion(amount)));

            BasicDBObject searchQuery = new BasicDBObject("$and", findQuery);

            UpdateOptions options = new UpdateOptions().upsert(true);
            UpdateResult result = coll.updateOne(searchQuery, updates, options);

            logger.info("Update Wallet : {}", new Gson().toJson(result));

        } catch (Exception e) {
            logger.error("Exception in : ", e);
        }
    }

    public boolean updateMerchantWallet(String payId, String currencyName, String updatedBalance, String txnType,
                                        String amount, boolean rnsToCaptured) {
        try {
            MongoDatabase dbIns = null;
            dbIns = mongoInstance.getDB();
            MongoCollection<Document> collection = dbIns.getCollection("WalletHistory");
            List<BasicDBObject> query = new ArrayList<>();
            query.add(new BasicDBObject("currency", currencyName));
            query.add(new BasicDBObject("payId", payId));

            BasicDBObject newFieldsObj = new BasicDBObject("$and", query);
            FindIterable<Document> output = collection.find(newFieldsObj);
            MongoCursor<Document> cursor = output.iterator();
            Document doc = null;
            int count = 0;
            while (cursor.hasNext()) {
                doc = cursor.next();
                ++count;
            }
            cursor.close();
            if (count > 1) {
                return false;
            }

            if (doc == null) {
                return false;
            }

            if (rnsToCaptured) {
                doc.put("totalBalance", decimalConversion(updatedBalance));
            } else {
                doc.put("finalBalance", decimalConversion(updatedBalance));
            }

            if (txnType.contains("credit")) {
                doc.put("credit", decimalConversion(amount));
            } else {
                doc.put("debit", decimalConversion(amount));
            }
            // doc.put(FieldType.TXNTYPE.getName(), TransactionType.RECO.getName());
            collection.replaceOne(newFieldsObj, doc);
        } catch (Exception exception) {
            logger.error("Exception in : ", exception);
        }
        return true;
    }

    public void updateBalanceByPayIdAndCurrencyTotalBalance(String payId, String currencyName,
                                                            String txnType, String amount, String updatedTotalBalance) {
        try {
            MongoDatabase dbIns = mongoInstance.getDB();
            MongoCollection<Document> coll = dbIns.getCollection("WalletHistory");

            List<BasicDBObject> findQuery = new ArrayList<BasicDBObject>();
            findQuery.add(new BasicDBObject("currency", currencyName));
            findQuery.add(new BasicDBObject("payId", payId));

            logger.info("DB DATA : {}", new Gson().toJson(findQuery));

            Bson updates = Updates.combine(Updates.set("totalBalance", decimalConversion(updatedTotalBalance)),

                    Updates.set(txnType.contains("credit") ? "credit" : "debit", decimalConversion(amount)));

            BasicDBObject searchQuery = new BasicDBObject("$and", findQuery);

            UpdateOptions options = new UpdateOptions().upsert(true);
            UpdateResult result = coll.updateOne(searchQuery, updates, options);

            logger.info("Update Wallet : {}", new Gson().toJson(result));

        } catch (Exception e) {
            logger.error("Exception in : ", e);
        }
    }

    public void updateBalanceByPayIdAndCurrencyAvailableBalance(String payId, String currencyName,
                                                                String txnType, String amount, String updatedavailableBalance) {
        try {
            MongoDatabase dbIns = mongoInstance.getDB();
            MongoCollection<Document> coll = dbIns.getCollection("WalletHistory");

            List<BasicDBObject> findQuery = new ArrayList<BasicDBObject>();
            findQuery.add(new BasicDBObject("currency", currencyName));
            findQuery.add(new BasicDBObject("payId", payId));

            logger.info("DB DATA : {}", new Gson().toJson(findQuery));

            Bson updates = Updates.combine(Updates.set("finalBalance", decimalConversion(updatedavailableBalance)),

                    Updates.set(txnType.contains("credit") ? "credit" : "debit", decimalConversion(amount)));

            BasicDBObject searchQuery = new BasicDBObject("$and", findQuery);

            UpdateOptions options = new UpdateOptions().upsert(true);
            UpdateResult result = coll.updateOne(searchQuery, updates, options);

            logger.info("Update Wallet : {}", new Gson().toJson(result));

        } catch (Exception e) {
            logger.error("Exception in : ", e);
        }
    }


    public String decimalConversion(String data) {
        Double decimalData = Double.parseDouble(data);
        return String.format("%.2f", decimalData);
    }

    public Map<String, Object> findAcquirerWalletByAcquirerName(String acquirerName) {
        logger.info("findAcquirerWalletByAcquirerName, request : acquirerName={}", acquirerName);
        Map<String, Object> acqWalletMap = new HashMap<>();
        try {
            MongoDatabase dbIns = mongoInstance.getDB();
            MongoCollection<Document> coll = dbIns.getCollection("AcquirerWalletMaster");

            BasicDBObject queryExecute = new BasicDBObject("acquirerName", acquirerName);
            MongoCursor<Document> cursor = coll.find(queryExecute).iterator();

            if (cursor.hasNext()) {
                Document document = cursor.next();
                logger.info("findAcquirerWalletByAcquirerName, Document: {}", document);
                acqWalletMap.put("payId", document.getString("payId"));
                acqWalletMap.put("acquirerName", document.getString("acquirerName"));
                acqWalletMap.put("acquirerCode", document.getString("acquirerCode"));
                acqWalletMap.put("finalBalance", decimalConversion(document.getString("finalBalance")));

            }

        } catch (Exception e) {
            logger.error("Exception in : ", e);
        }
        return acqWalletMap;

    }

    public boolean updateAcquirerWallet(String acquirerName, String updatedBalance) {

        try {
            MongoDatabase dbIns = null;
            dbIns = mongoInstance.getDB();
            MongoCollection<Document> collection = dbIns.getCollection("AcquirerWalletMaster");

            BasicDBObject newFieldsObj = new BasicDBObject("acquirerName", acquirerName);
            FindIterable<Document> output = collection.find(newFieldsObj);
            MongoCursor<Document> cursor = output.iterator();
            Document doc = null;
            int count = 0;
            while (cursor.hasNext()) {
                doc = cursor.next();
                ++count;
            }
            cursor.close();
            if (count > 1) {
                return false;
            }

            if (doc == null) {
                return false;
            }

            doc.put("finalBalance", decimalConversion(updatedBalance));
            collection.replaceOne(newFieldsObj, doc);
        } catch (Exception exception) {
            logger.error("updateAcquirerWallet, Exception in : ", exception);
        }
        return true;

    }
}
