package com.pay10.commons.mongo;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.pay10.commons.util.FieldType;
import com.pay10.commons.util.Fields;

import org.bson.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.google.gson.Gson;
import com.mongodb.BasicDBObject;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import com.pay10.commons.dto.CashDepositDTOPO;
import com.pay10.commons.dto.MerchantWalletPODTO;
import com.pay10.commons.dto.PassbookPODTO;
import com.pay10.commons.dto.PayoutTxnLedgerDTO;
import com.pay10.commons.exception.ErrorType;
import com.pay10.commons.exception.SystemException;
import com.pay10.commons.user.MultCurrencyCodeDao;
import com.pay10.commons.util.Amount;
import com.pay10.commons.util.Constants;
import com.pay10.commons.util.PropertiesManager;
import com.pay10.commons.util.TransactionManager;

@Component
public class MerchantWalletPODao {
	private static Logger logger = LoggerFactory.getLogger(MerchantWalletPODao.class.getName());
    private static final String prefix = "MONGO_DB_";

    @Autowired
    PropertiesManager propertiesManager;

    @Autowired
    private MongoInstance mongoInstance;
    @Autowired
    private MultCurrencyCodeDao multCurrencyCodeDao;

    public String saveOrUpdate(CashDepositDTOPO cashDepositDTOPO) {
        MerchantWalletPODTO merchantWalletPODTO = new MerchantWalletPODTO();
        try {
            MongoDatabase dbIns = mongoInstance.getDB();
            MongoCollection<Document> collection = dbIns.getCollection("WalletHistory");

            merchantWalletPODTO.setPayId(cashDepositDTOPO.getPayId());
            merchantWalletPODTO.setCurrency(cashDepositDTOPO.getCurrency());

            MerchantWalletPODTO merchantWalletPODTOdb = findByPayId(merchantWalletPODTO);
            logger.info("Check for cash deposit : " + new Gson().toJson(merchantWalletPODTO));

            if (merchantWalletPODTOdb != null) {

                merchantWalletPODTO.setDebit((Double.parseDouble(merchantWalletPODTOdb.getDebit())
                        + 0.0) + "");
                merchantWalletPODTO.setCredit((Double.parseDouble(merchantWalletPODTOdb.getCredit())
                        + Double.parseDouble(cashDepositDTOPO.getAmount())) + "");
                merchantWalletPODTO.setFinalBalance((Double.parseDouble(merchantWalletPODTOdb.getFinalBalance())
                        + Double.parseDouble(cashDepositDTOPO.getAmount())) + "");
                merchantWalletPODTO.setTotalBalance((Double.parseDouble(merchantWalletPODTOdb.getTotalBalance())
                        + Double.parseDouble(cashDepositDTOPO.getAmount())) + "");

                Document query = new Document("payId", merchantWalletPODTO.getPayId()).append("currency", merchantWalletPODTO.getCurrency());
                Document update = new Document("$set", new Document("credit", merchantWalletPODTO.getCredit()).append("debit", merchantWalletPODTO.getDebit())
                        .append("finalBalance", merchantWalletPODTO.getFinalBalance()).append("totalBalance", merchantWalletPODTO.getTotalBalance()).append("currency", merchantWalletPODTO.getCurrency()));

                collection.updateOne(query, update);

            } else {

                merchantWalletPODTO.setCredit(cashDepositDTOPO.getAmount());
                merchantWalletPODTO.setFinalBalance(cashDepositDTOPO.getAmount());
                merchantWalletPODTO.setTotalBalance(cashDepositDTOPO.getAmount());

                Document newDocument = new Document("payId", merchantWalletPODTO.getPayId())
                        .append("credit", merchantWalletPODTO.getCredit()).append("debit", "0")
                        .append("finalBalance", merchantWalletPODTO.getFinalBalance())
                        .append("totalBalance", merchantWalletPODTO.getTotalBalance())
                        .append("currency", merchantWalletPODTO.getCurrency());

                collection.insertOne(newDocument);
            }

            return "Success";

        } catch (Exception exception) {
            exception.printStackTrace();
            return "Fail";
        }
    }

    public String updateMerchantWalletForChargeBack(Fields fields,boolean chargeBackType) {
		logger.info("Request Fields updateMerchantWalletForChargeBack : " + fields.getFieldsAsString());
		String response = "Success";
		try {
			logger.info("Inside updateMerchantWallet : " + fields.getFieldsAsString());
	            MerchantWalletPODTO merchantWalletPODTO = new MerchantWalletPODTO();
	            merchantWalletPODTO.setPayId(fields.get(FieldType.PAY_ID.getName()));
	            merchantWalletPODTO.setCurrency(multCurrencyCodeDao.getCurrencyNamebyCode(fields.get(FieldType.CURRENCY_CODE.getName())));

	            MerchantWalletPODTO merchantWalletPODTO1 = findByPayId(merchantWalletPODTO);
	            if (merchantWalletPODTO1 == null) {
	                throw new SystemException(ErrorType.PO_WALLET_IS_NOT_FOUND,
	                        ",Merchant wallet is not found for  PayId=" + fields.get(FieldType.PAY_ID.getName()));

	            }

	            LocalDateTime myDateObj = LocalDateTime.now();
	            DateTimeFormatter myFormatObj = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

	            String formattedDate = myDateObj.format(myFormatObj);
	            logger.info("Formatted Date : " + formattedDate);
	            //another service call here here chargeback type staus in chargeback initiate true
				if (chargeBackType) {
					logger.info("INITIATE : " + fields
							.get(FieldType.AMOUNT.getName() + " ** " + fields.get(FieldType.CURRENCY_CODE.getName())));
					String amt = Amount.toDecimal(fields.get(FieldType.AMOUNT.getName()),
							fields.get(FieldType.CURRENCY_CODE.getName()));
					logger.info("INITIATE : " + amt);
					logger.info("INITIATE : " + fields.getFieldsAsString());
					double finalAmount = Double.parseDouble(merchantWalletPODTO1.getFinalBalance());
					double totalAmount = Double.parseDouble(amt);

					logger.info("fields in status enquiry processor in chargeback finalAmount {} ", finalAmount);
					logger.info("fields in status enquiry processor in chargeback totalAmount {}", totalAmount);

					logger.info("fields in status enquiry processor in chargeback {}", merchantWalletPODTO1);
					merchantWalletPODTO1.setFinalBalance(String.valueOf(finalAmount - totalAmount));

					double totalBalance = Double.parseDouble(merchantWalletPODTO1.getTotalBalance());
					logger.info("fields in status enquiry processor in chargeback totalBalance {}", totalBalance);

					merchantWalletPODTO1.setTotalBalance(String.valueOf(totalBalance - totalAmount));

					PassbookPODTO passbookPODTO = new PassbookPODTO();
					passbookPODTO.setPayId(fields.get(FieldType.PAY_ID.getName()));
					passbookPODTO.setType("DEBIT");
					passbookPODTO.setNarration("CHARGEBACK");
					passbookPODTO.setCreateDate(formattedDate);
					passbookPODTO.setTxnId(TransactionManager.getNewTransactionId());
					passbookPODTO.setAmount(String.valueOf(totalAmount));
					passbookPODTO.setCurrency(multCurrencyCodeDao
							.getCurrencyNamebyCode(String.valueOf(fields.get(FieldType.CURRENCY_CODE.getName()))));
					passbookPODTO.setRespTxn(fields.get(FieldType.ORDER_ID.getName()));
					logger.info("Save the Transaction History in passbook {}", passbookPODTO);

					savePassbookDetailsByPassbook(passbookPODTO);

					SaveAndUpdteWallet(merchantWalletPODTO1);
				}
	            
	            //another service call here here chargeback type staus in merchantFaver then reverse false
				else {

					double finalAmount = Double.parseDouble(merchantWalletPODTO1.getFinalBalance());
					double totalAmount = Double.parseDouble(Amount.toDecimal(fields.get(FieldType.AMOUNT.getName()),
							fields.get(FieldType.CURRENCY_CODE.getName())));

					logger.info("fields in status enquiry processor in chargeback finalAmount {} ", finalAmount);
					logger.info("fields in status enquiry processor in chargeback totalAmount {}", totalAmount);

					logger.info("fields in status enquiry processor in chargeback {}", merchantWalletPODTO1);
					merchantWalletPODTO1.setFinalBalance(String.valueOf(finalAmount + totalAmount));

					double totalBalance = Double.parseDouble(merchantWalletPODTO1.getTotalBalance());
					logger.info("fields in status enquiry processor in chargeback totalBalance {}", totalBalance);

					merchantWalletPODTO1.setTotalBalance(String.valueOf(totalBalance + totalAmount));

					PassbookPODTO passbookPODTORev = new PassbookPODTO();
					passbookPODTORev.setPayId(fields.get(FieldType.PAY_ID.getName()));
					passbookPODTORev.setType("CREDIT");
					passbookPODTORev.setNarration("CHARGEBACK REVERSAL");
					passbookPODTORev.setCreateDate(formattedDate);
					passbookPODTORev.setTxnId(TransactionManager.getNewTransactionId());
					passbookPODTORev.setAmount(Amount.toDecimal(fields.get(FieldType.AMOUNT.getName()),
							fields.get(FieldType.CURRENCY_CODE.getName())));
					passbookPODTORev.setCurrency(multCurrencyCodeDao
							.getCurrencyNamebyCode(String.valueOf(fields.get(FieldType.CURRENCY_CODE.getName()))));
					passbookPODTORev.setRespTxn(fields.get(FieldType.ORDER_ID.getName()));

					logger.info("Save the Transaction History in passbook {}", passbookPODTORev);
					savePassbookDetailsByPassbook(passbookPODTORev);

					logger.info("MerchantWalletPODTORev : " + new Gson().toJson(merchantWalletPODTO1));
					SaveAndUpdteWallet(merchantWalletPODTO1);
				}

		} catch (Exception e) {
			logger.info("Exception in updateMerchantWalletForChargeBack : " + e);
			e.printStackTrace();
			response = "Failed";
		}
		return response;

	}
    
    public void savePassbookDetailsByPassbook(PassbookPODTO passbookPODTO) {

        MongoDatabase dbIns = mongoInstance.getDB();
        MongoCollection<Document> collection = dbIns.getCollection(
                PropertiesManager.propertiesMap.get(prefix + Constants.PASSBOOK_PO.getValue()));


        Document newDocument = new Document();
        newDocument.put("payId", passbookPODTO.getPayId());
        newDocument.put("type", passbookPODTO.getType());
        newDocument.put("narration", passbookPODTO.getNarration());
        newDocument.put("createDate", passbookPODTO.getCreateDate());
        newDocument.put("txnId", passbookPODTO.getTxnId());
        newDocument.put("respTxn", passbookPODTO.getRespTxn());
        newDocument.put("debitAmt", passbookPODTO.getDebitAmt());
        newDocument.put("amount", passbookPODTO.getAmount());
        newDocument.put("currency", passbookPODTO.getCurrency());

        double availablebalance=getAvailabaleBalance(passbookPODTO.getCurrency(), passbookPODTO.getPayId());
        newDocument.put(FieldType.OPENING_BALANCE.getName(), String.valueOf(availablebalance));
        logger.info("After Getting OPENNING Balance : " + newDocument.toJson());
        double amount=Double.valueOf(passbookPODTO.getAmount());
        if (passbookPODTO.getType().equalsIgnoreCase("CREDIT")) {
            double closingBalance=availablebalance+amount;
            newDocument.put(FieldType.CLOSING_BALANCE.getName(), String.valueOf(closingBalance));
        }else {
            double closingBalance=availablebalance-amount;
            newDocument.put(FieldType.CLOSING_BALANCE.getName(), String.valueOf(closingBalance));
        }
        logger.info("savePassbookDetailsByPassbook :"+newDocument.toJson());
        logger.info("savePassbookDetailsByPassbook payload:"+new Gson().toJson(passbookPODTO));
		LocalDateTime myDateObj = LocalDateTime.now();
		DateTimeFormatter myFormatObj = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss:SSSSSS");
        passbookPODTO.setInsertDate(myFormatObj.format(myDateObj));
        newDocument.put("insertDate", passbookPODTO.getInsertDate());
        logger.info("Final Entry Passbook : " + newDocument);
        collection.insertOne(newDocument);

    }

    public void SaveAndUpdteWallet(MerchantWalletPODTO merchantWalletPODTOdb) {
        MongoDatabase dbIns = mongoInstance.getDB();
        MongoCollection<Document> collection = dbIns.getCollection("WalletHistory");

        Document query = new Document("payId", merchantWalletPODTOdb.getPayId()).append("currency", merchantWalletPODTOdb.getCurrency());
        Document update = new Document("$set", new Document("finalBalance", merchantWalletPODTOdb.getFinalBalance()).append("totalBalance", merchantWalletPODTOdb.getTotalBalance()));

        collection.updateOne(query, update);
    }
    
    public void SaveAndUpdteTotalBalanceWallet(MerchantWalletPODTO merchantWalletPODTOdb) {
        MongoDatabase dbIns = mongoInstance.getDB();
        MongoCollection<Document> collection = dbIns.getCollection("WalletHistory");

        Document query = new Document("payId", merchantWalletPODTOdb.getPayId()).append("currency", merchantWalletPODTOdb.getCurrency());
        Document update = new Document("$set", new Document("totalBalance", merchantWalletPODTOdb.getTotalBalance()));

        collection.updateOne(query, update);
    }
    
    public void SaveAndUpdteAvailableBalanceWallet(MerchantWalletPODTO merchantWalletPODTOdb) {
        MongoDatabase dbIns = mongoInstance.getDB();
        MongoCollection<Document> collection = dbIns.getCollection("WalletHistory");

        Document query = new Document("payId", merchantWalletPODTOdb.getPayId()).append("currency", merchantWalletPODTOdb.getCurrency());
        Document update = new Document("$set", new Document("finalBalance", merchantWalletPODTOdb.getFinalBalance()));

        collection.updateOne(query, update);
    }

    public MerchantWalletPODTO findByPayId(MerchantWalletPODTO merchantWalletPODTOs) {
        MongoDatabase dbIns = mongoInstance.getDB();
        BasicDBObject params = new BasicDBObject();

        params.put("payId", merchantWalletPODTOs.getPayId());
        params.put("currency", merchantWalletPODTOs.getCurrency());
        MongoCollection<Document> coll = dbIns
                .getCollection(PropertiesManager.propertiesMap.get(prefix + Constants.WALLET_HISTORY.getValue()));
        List<BasicDBObject> queryExecute = Arrays.asList(new BasicDBObject("$match", params));
        logger.info("Query to find wllate history :"+queryExecute);
        MongoCursor<Document> cursor = coll.aggregate(queryExecute).iterator();
        MerchantWalletPODTO merchantWalletPODTO = null;
        if (cursor.hasNext()) {
            Document document = (Document) cursor.next();

            merchantWalletPODTO = new Gson().fromJson(document.toJson(), MerchantWalletPODTO.class);
        }
        logger.info("Anubha : " + new Gson().toJson(merchantWalletPODTO));
        return merchantWalletPODTO;
    }
    public double getAvailabaleBalance(String currency, String payid) {
    	 logger.info("getAvailabaleBalance payload  currency :"+currency+"\t PayId : "+payid);
    	MongoDatabase dbIns = mongoInstance.getDB();
        MongoCollection<Document> collection = dbIns.getCollection(
                PropertiesManager.propertiesMap.get(prefix +"WalletHistory"));

        BasicDBObject match =new BasicDBObject();
        match.put("payId", payid);
        match.put("currency", currency);

        List<BasicDBObject> getAvailabaleBalance = Arrays.asList(new BasicDBObject("$match", match));
        logger.info("getAvailabaleBalance query :"+match);
        MongoCursor<Document>cursor=collection.aggregate(getAvailabaleBalance).iterator();
        double response=0;
        if (cursor.hasNext()) {
            Document document = cursor.next();
            response= Double.valueOf(String.valueOf(document.get("finalBalance")));

        }
        logger.info("getAvailabaleBalance finalBalance :"+response);
        return response;
    }
    public List<MerchantWalletPODTO> getAllData() {
        List<MerchantWalletPODTO> merchantWalletPODTOs = new ArrayList<>();
        MongoDatabase dbIns = mongoInstance.getDB();
        MongoCollection<Document> coll = dbIns.getCollection("WalletHistory");
        MongoCursor<Document> cursor = coll.find().iterator();

        while (cursor.hasNext()) {
            Document document = cursor.next();
            MerchantWalletPODTO merchantWalletPODTO = new Gson().fromJson(document.toJson(), MerchantWalletPODTO.class);
            merchantWalletPODTOs.add(merchantWalletPODTO);
        }

        logger.info("Anubha: " + new Gson().toJson(merchantWalletPODTOs));
        return merchantWalletPODTOs;
    }

}