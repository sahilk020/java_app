package com.pay10.crm.mongoReports;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import com.pay10.commons.mongo.MerchantWalletPODao;
import com.pay10.commons.util.FieldType;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.mongodb.BasicDBObject;
import com.mongodb.client.AggregateIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import com.pay10.commons.dto.CashDepositDTOPO;
import com.pay10.commons.dto.PassbookPODTO;
import com.pay10.commons.mongo.MongoInstance;
import com.pay10.commons.util.Constants;
import com.pay10.commons.util.PropertiesManager;

@Component
public class PassbookPODao {

	private static final String prefix = "MONGO_DB_";

	@Autowired
	PropertiesManager propertiesManager;

	@Autowired
	private MongoInstance mongoInstance;

    @Autowired
    private MerchantWalletPODao dao;

	public void savePassbookDetails(CashDepositDTOPO cashDepositDTOPO) {
		PassbookPODTO passbookPODTO = new PassbookPODTO();

		MongoDatabase dbIns = mongoInstance.getDB();
		MongoCollection<Document> collection = dbIns.getCollection(
				PropertiesManager.propertiesMap.get(prefix + Constants.PASSBOOK_PO.getValue()));
		passbookPODTO.setCreateDate(new SimpleDateFormat("yyyy-MM-dd").format(new Date()));
		passbookPODTO.setTxnId(cashDepositDTOPO.getTxnId());
		passbookPODTO.setAmount(cashDepositDTOPO.getAmount());
		passbookPODTO.setPayId(cashDepositDTOPO.getPayId());
		passbookPODTO.setType("Credit");
		passbookPODTO.setNarration("Wallet Topup");

		Document newDocument = new Document("payId", passbookPODTO.getPayId()).append("type", "credit")
				.append("narration", "Wallet Topup").append("createDate", passbookPODTO.getCreateDate())
				.append("txnId", passbookPODTO.getTxnId()).append("amount", passbookPODTO.getAmount());

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

        LocalDateTime myDateObj = LocalDateTime.now();
		DateTimeFormatter myFormatObj = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss:SSSSSS");
        passbookPODTO.setInsertDate(myFormatObj.format(myDateObj));
        newDocument.put("insertDate", passbookPODTO.getInsertDate());
        
		collection.insertOne(newDocument);

	}
	
	public PassbookPODTO getPassbookDetails(String payId) {
        MongoDatabase dbIns = mongoInstance.getDB();
        MongoCollection<Document> collection = dbIns.getCollection(
                PropertiesManager.propertiesMap.get(prefix + Constants.PASSBOOK_PO.getValue()));
        Document query = new Document("payId", payId);
        Document matchStage = new Document("$match", query);

        List<Document> pipeline = Arrays.asList(matchStage);

        AggregateIterable<Document> result = collection.aggregate(pipeline);

        MongoCursor<Document> cursor = result.iterator();

        if (cursor.hasNext()) {
            Document document = cursor.next();
            PassbookPODTO passbookPODTO = new PassbookPODTO();
            passbookPODTO.setPayId(document.getString("payId"));
            passbookPODTO.setType(document.getString("type"));
            passbookPODTO.setNarration(document.getString("narration"));
            passbookPODTO.setCreateDate(document.getString("createDate"));
            passbookPODTO.setTxnId(document.getString("txnId"));
            passbookPODTO.setAmount(document.getString("amount"));

            cursor.close();
            return passbookPODTO;
        } else {
            cursor.close();
            return null;
        }
    }
}