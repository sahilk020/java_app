package com.pay10.commons.mongo;

import com.google.gson.Gson;
import com.mongodb.client.AggregateIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.pay10.commons.dto.CashDepositDTOPO;
import com.pay10.commons.dto.PassbookPODTO;
import com.pay10.commons.util.Constants;
import com.pay10.commons.util.FieldType;
import com.pay10.commons.util.PropertiesManager;
import org.apache.commons.lang3.StringUtils;
import org.bson.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

@Service
public class PassbookLedgerDao {
    private static final String prefix = "MONGO_DB_";
    @Autowired
    private MongoInstance mongoInstance;

    private Logger logger = LoggerFactory.getLogger(PassbookLedgerDao.class);

    @Autowired
    private MerchantWalletPODao dao;


    //
    public void savePassbookDetails(CashDepositDTOPO cashDepositDTOPO) {
        PassbookPODTO passbookPODTO = new PassbookPODTO();

        MongoDatabase dbIns = mongoInstance.getDB();
        MongoCollection<Document> collection = dbIns
                .getCollection(PropertiesManager.propertiesMap.get(prefix + Constants.PASSBOOK_PO.getValue()));
        passbookPODTO.setCreateDate(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
        passbookPODTO.setTxnId(cashDepositDTOPO.getTxnId());
        passbookPODTO.setAmount(cashDepositDTOPO.getAmount());
        passbookPODTO.setPayId(cashDepositDTOPO.getPayId());
        passbookPODTO.setType("Credit");
        passbookPODTO.setNarration("Wallet Topup");
        passbookPODTO.setCurrency(cashDepositDTOPO.getCurrency());
        LocalDateTime myDateObj = LocalDateTime.now();
		DateTimeFormatter myFormatObj = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss:SSSSSS");
        passbookPODTO.setInsertDate(myFormatObj.format(myDateObj));

        Document newDocument = new Document("payId", passbookPODTO.getPayId()).append("type", "credit")
                .append("narration", "Wallet Topup").append("createDate", passbookPODTO.getCreateDate())
                .append("txnId", passbookPODTO.getTxnId()).append("amount", passbookPODTO.getAmount())
                .append("currency", passbookPODTO.getCurrency()).append("insertDate", passbookPODTO.getInsertDate());

        double availablebalance = dao.getAvailabaleBalance(passbookPODTO.getCurrency(), passbookPODTO.getPayId());
        newDocument.append(FieldType.OPENING_BALANCE.getName(), String.valueOf(availablebalance));

        double amount = Double.valueOf(passbookPODTO.getAmount());
        if (passbookPODTO.getType().equalsIgnoreCase("CREDIT")) {
            double closingBalance = availablebalance + amount;
            newDocument.append(FieldType.CLOSING_BALANCE.getName(), String.valueOf(closingBalance));
        } else {
            double closingBalance = availablebalance - amount;
            newDocument.append(FieldType.CLOSING_BALANCE.getName(), String.valueOf(closingBalance));
        }

        collection.insertOne(newDocument);

    }

    public List<PassbookPODTO> getPassbookDetails(String payId, String from, String to) {
        List<PassbookPODTO> passbookPODTOs = new ArrayList<PassbookPODTO>();
        MongoDatabase dbIns = mongoInstance.getDB();
        from += " 00:00:00";
        to += " 23:59:59";
        MongoCollection<Document> collection = dbIns
                .getCollection(PropertiesManager.propertiesMap.get(prefix + Constants.PASSBOOK_PO.getValue()));
        List<Document> pipeline = Arrays.asList(new Document("$match",
                        new Document("payId", payId)
                                .append("createDate",
                                        new Document("$gte", from)
                                                .append("$lte", to))),
                new Document("$sort",
                        new Document("insertDate", -1L)));

        AggregateIterable<Document> result = collection.aggregate(pipeline);

        for (Document document : result) {
            PassbookPODTO passbookPODTO = new PassbookPODTO();
            passbookPODTO.setPayId(document.getString("payId"));
            passbookPODTO.setType(document.getString("type").toUpperCase());
            passbookPODTO.setNarration(document.getString("narration"));
            passbookPODTO.setCreateDate(document.getString("createDate"));
            passbookPODTO.setTxnId(document.getString("txnId"));
            passbookPODTO.setCurrency(document.getString("currency"));
            if(!StringUtils.isBlank(document.getString("amount"))){
                passbookPODTO.setAmount(document.getString("amount"));
            }else{
                passbookPODTO.setAmount(document.getString("debitAmt"));
            }
            passbookPODTO.setOpeningBalance(document.getString(FieldType.OPENING_BALANCE.getName())!=null?String.format("%.2f", Double.parseDouble(document.getString(FieldType.OPENING_BALANCE.getName()))):"");
            passbookPODTO.setClosingBalance(document.getString(FieldType.CLOSING_BALANCE.getName())!=null?String.format("%.2f", Double.parseDouble(document.getString(FieldType.CLOSING_BALANCE.getName()))):"");
            passbookPODTOs.add(passbookPODTO);
        }

        logger.info("Passbook Data: {}", passbookPODTOs.size());
        return passbookPODTOs;
    }

    // please use this method for creating passbook entry
    public void createPassbookEntry(PassbookPODTO passbookPODTO) {
    	LocalDateTime myDateObj = LocalDateTime.now();
		DateTimeFormatter myFormatObj = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss:SSSSSS");
        passbookPODTO.setInsertDate(myFormatObj.format(myDateObj));

        try {
            MongoDatabase dbIns = mongoInstance.getDB();
            MongoCollection<Document> collection = dbIns
                    .getCollection(PropertiesManager.propertiesMap.get(prefix + Constants.PASSBOOK_PO.getValue()));
            passbookPODTO.setCreateDate(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));

            Document newDocument = Document.parse(new Gson().toJson(passbookPODTO));
            double availablebalance = dao.getAvailabaleBalance(passbookPODTO.getCurrency(), passbookPODTO.getPayId());
            newDocument.append(FieldType.OPENING_BALANCE.getName(), String.valueOf(availablebalance));

            double amount = Double.valueOf(passbookPODTO.getAmount());
            if (passbookPODTO.getType().equalsIgnoreCase("CREDIT")) {
                double closingBalance = availablebalance + amount;
                newDocument.append(FieldType.CLOSING_BALANCE.getName(), String.valueOf(closingBalance));
            } else {
                double closingBalance = availablebalance - amount;
                newDocument.append(FieldType.CLOSING_BALANCE.getName(), String.valueOf(closingBalance));
            }
            
            newDocument.put("insertDate", passbookPODTO.getInsertDate());
            collection.insertOne(newDocument);

        } catch (Exception e) {
            logger.error("Exception", e);
        }
    }
}
