package com.pay10.pg.service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import com.pay10.commons.dao.FieldsDao;
import com.pay10.commons.dto.MerchantWalletPODTO;
import com.pay10.commons.dto.PassbookPODTO;
import com.pay10.commons.mongo.MerchantWalletPODao;
import com.pay10.commons.user.*;
import org.apache.commons.lang3.StringUtils;
import org.bson.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.google.gson.Gson;
import com.mongodb.BasicDBObject;
import com.mongodb.client.AggregateIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import com.pay10.commons.exception.ErrorType;
import com.pay10.commons.exception.SystemException;
import com.pay10.commons.mongo.MongoInstance;
import com.pay10.commons.util.AcquirerType;
import com.pay10.commons.util.Amount;
import com.pay10.commons.util.Constants;
import com.pay10.commons.util.FieldType;
import com.pay10.commons.util.Fields;
import com.pay10.commons.util.PropertiesManager;
import com.pay10.commons.util.StatusType;
import com.pay10.commons.util.StatusTypePayout;
import com.pay10.commons.util.TransactionManager;
import com.pay10.commons.util.TransactionType;
import com.pay10.payout.PaytenStatusEnquiryPayoutProcessor;
import com.pay10.pg.core.util.Processor;

@Service
public class StatusEnquiryProcessorPayout {

    private static final String prefix = "MONGO_DB_";
    private static Logger logger = LoggerFactory.getLogger(StatusEnquiryProcessorPayout.class.getName());
    @Autowired
    PropertiesManager propertiesManager;
    @Autowired
    private Fields fieldDao;

    @Autowired
    private PaytenStatusEnquiryPayoutProcessor paytenStatusEnquiryPayoutProcessor;

    @Autowired
    private UserDao userDao;

    @Autowired
    private MultCurrencyCodeDao multCurrencyCodeDao;
    
    @Autowired
    private MerchantWalletPODao merchantWalletPODao;

    @Autowired
    private MongoInstance mongoInstance;

    @SuppressWarnings("incomplete-switch")
    public Map<String, String> process(Fields fields) throws SystemException {

        logger.info("Request received for statusEnquiryProcessor payout");
        boolean iSTxnFound = getTransactionFields(fields);

        if (StringUtils.isBlank(fields.get(FieldType.ACQUIRER_TYPE.getName()))) {

            logger.info(
                    "Acquirer not found for status enquiry , pgRef == " + fields.get(FieldType.PG_REF_NUM.getName()));
            fields.put(FieldType.STATUS.getName(), StatusType.INVALID.getName());
            fields.put(FieldType.RESPONSE_MESSAGE.getName(), "No Such Transaction found");
            fields.put(FieldType.PG_TXN_MESSAGE.getName(), "No Such Transaction found");

            fields.put(FieldType.RESPONSE_CODE.getName(), ErrorType.NO_SUCH_TRANSACTION.getCode());
            return fields.getFields();
        }

        String stausOFTxn = fields.get(FieldType.STATUS.getName());

        if (stausOFTxn.equalsIgnoreCase(StatusType.CAPTURED.getName())
                || stausOFTxn.equalsIgnoreCase(StatusType.SETTLED_SETTLE.getName())
                || stausOFTxn.equalsIgnoreCase(StatusType.SETTLED_RECONCILLED.getName())) {

            logger.info("Transaction not found for status enquiry , pgRef == "
                    + fields.get(FieldType.PG_REF_NUM.getName()));
            fields.put(FieldType.STATUS.getName(), StatusType.PROCESSED.getName());
            fields.put(FieldType.RESPONSE_MESSAGE.getName(), "Transaction already success");

            fields.put(FieldType.RESPONSE_CODE.getName(), ErrorType.ALREADY_CAPTURED_TRANSACTION.getCode());
            return fields.getFields();
        }

        if (!iSTxnFound) {

            logger.info("Transaction not found for status enquiry , pgRef == "
                    + fields.get(FieldType.PG_REF_NUM.getName()));
            fields.put(FieldType.STATUS.getName(), StatusType.INVALID.getName());
            fields.put(FieldType.RESPONSE_MESSAGE.getName(), "No Such Transaction found");
            fields.put(FieldType.PG_TXN_MESSAGE.getName(), "No Such Transaction found");

            fields.put(FieldType.RESPONSE_CODE.getName(), ErrorType.NO_SUCH_TRANSACTION.getCode());
            return fields.getFields();
        }
        AcquirerType acquirerType = AcquirerType.getInstancefromCode(fields.get(FieldType.ACQUIRER_TYPE.getName()));

        switch (acquirerType) {

            case PAY10:
                paytenStatusEnquiryPayoutProcessor.enquiryProcessor(fields);
                break;
            default:

                Map<String, String> failedTxnField = fields.getFields();
                failedTxnField.put(FieldType.STATUS.getName(), "");
                return fields.getFields();
        }

		
        fields.put(FieldType.INTERNAL_ORIG_TXN_TYPE.getName(), fields.get(FieldType.TXNTYPE.getName()));
        fields.removeSecureFields();

        logger.info("StatusEnquiryProcessor, stausOFTxn :" + stausOFTxn);
        logger.info("StatusEnquiryProcessor, STATUS :" + fields.getFields().get(FieldType.STATUS.getName()));
        if (StringUtils.isNotBlank(fields.getFields().get(FieldType.STATUS.getName()))
                && !fields.getFields().get(FieldType.STATUS.getName()).equalsIgnoreCase(stausOFTxn)
                && (!fields.getFields().get(FieldType.STATUS.getName()).equalsIgnoreCase(StatusType.SENT_TO_BANK.getName()))) {
                updateMerchantWallet(fields);
                if (!fields.getFields().get(FieldType.STATUS.getName()).equalsIgnoreCase(StatusType.INVALID.getName())) {
                    updateTxnData(fields);
                }
        }

        return fields.getFields();
    }

    private void updateMerchantWallet(Fields fields) throws SystemException{
    	logger.info("Inside updateMerchantWallet : " + fields.getFieldsAsString());
        if(!fields.get(FieldType.RESPONSE_CODE.getName()).equals(ErrorType.SUCCESS.getResponseCode())){
            MerchantWalletPODTO merchantWalletPODTO = new MerchantWalletPODTO();
            merchantWalletPODTO.setPayId(fields.get(FieldType.PAY_ID.getName()));
            merchantWalletPODTO.setCurrency(multCurrencyCodeDao.getCurrencyNamebyCode(fields.get(FieldType.CURRENCY_CODE.getName())));

            MerchantWalletPODTO merchantWalletPODTO1 = merchantWalletPODao.findByPayId(merchantWalletPODTO);
            if (merchantWalletPODTO1 == null) {
                throw new SystemException(ErrorType.PO_WALLET_IS_NOT_FOUND,
                        ",Merchant wallet is not found for  PayId=" + fields.get(FieldType.PAY_ID.getName()));

            }

            double finalAmount = Double.parseDouble(merchantWalletPODTO1.getFinalBalance());
            double totalTdrAmount = Double.parseDouble(fields.get(FieldType.ACQUIRER_TDR_SC.getName()))
                    + Double.parseDouble(fields.get(FieldType.PG_TDR_SC.getName()));
            double totalAmount = Double.parseDouble(Amount.toDecimal(fields.get(FieldType.AMOUNT.getName()),
                    fields.get(FieldType.CURRENCY_CODE.getName())));
            double finalAmt = finalAmount + totalTdrAmount + totalAmount;

            logger.info("fields in status enquiry processor in payout finalAmount {} ", finalAmount);
            logger.info("fields in status enquiry processor in payout totalTdrAmount {}", totalTdrAmount);
            logger.info("fields in status enquiry processor in payout totalAmount {}", totalAmount);

            logger.info("fields in status enquiry processor in payout {}", merchantWalletPODTO1);
            logger.info("fields in status enquiry processor in payout {}", finalAmt);

            merchantWalletPODTO1.setFinalBalance(String.valueOf(finalAmount+totalTdrAmount));


            double totalBalance = Double.parseDouble(merchantWalletPODTO1.getTotalBalance());
            double totalBalanceAmt = totalBalance + totalTdrAmount + totalAmount;
            logger.info("fields in status enquiry processor in payout totalBalance {}", totalBalance);
            logger.info("fields in status enquiry processor in payout {}", totalBalanceAmt);

            merchantWalletPODTO1.setTotalBalance(String.valueOf(totalBalance+totalTdrAmount));


            LocalDateTime myDateObj = LocalDateTime.now();
            DateTimeFormatter myFormatObj = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

            String formattedDate = myDateObj.format(myFormatObj);
            logger.info("Formatted Date : " + formattedDate);
            
            PassbookPODTO passbookPODTO = new PassbookPODTO();
            passbookPODTO.setPayId(fields.get(FieldType.PAY_ID.getName()));
            passbookPODTO.setType("CREDIT");
            passbookPODTO.setNarration("PAYOUT CHARGES REVERSAL");
            passbookPODTO.setCreateDate(formattedDate);
            passbookPODTO.setTxnId(TransactionManager.getNewTransactionId());
            passbookPODTO.setAmount(String.valueOf(totalTdrAmount));
            passbookPODTO.setCurrency(multCurrencyCodeDao.getCurrencyNamebyCode(String.valueOf(fields.get(FieldType.CURRENCY_CODE.getName()))));
            passbookPODTO.setRespTxn(fields.get(FieldType.ORDER_ID.getName()));
            logger.info("Save the Transaction History in passbook {}", passbookPODTO);
            
            merchantWalletPODao.savePassbookDetailsByPassbook(passbookPODTO);
            
            merchantWalletPODao.SaveAndUpdteWallet(merchantWalletPODTO1);
            
            //another service call here
            
            PassbookPODTO passbookPODTORev = new PassbookPODTO();
            passbookPODTORev.setPayId(fields.get(FieldType.PAY_ID.getName()));
            passbookPODTORev.setType("CREDIT");
            passbookPODTORev.setNarration("PAYOUT REVERSAL");
            passbookPODTORev.setCreateDate(formattedDate);
            passbookPODTORev.setTxnId(TransactionManager.getNewTransactionId());
            passbookPODTORev.setAmount(Amount.toDecimal(fields.get(FieldType.AMOUNT.getName()), fields.get(FieldType.CURRENCY_CODE.getName())));
            passbookPODTORev.setCurrency(multCurrencyCodeDao.getCurrencyNamebyCode(String.valueOf(fields.get(FieldType.CURRENCY_CODE.getName()))));
            passbookPODTORev.setRespTxn(fields.get(FieldType.ORDER_ID.getName()));
            
            logger.info("Save the Transaction History in passbook {}",passbookPODTORev);
            MerchantWalletPODTO merchantWalletPODTORev = merchantWalletPODao.findByPayId(merchantWalletPODTO);
            merchantWalletPODao.savePassbookDetailsByPassbook(passbookPODTORev);
            double MerfinalBal=Double.parseDouble(merchantWalletPODTORev.getFinalBalance());
            String amt=Amount.toDecimal(fields.get(FieldType.AMOUNT.getName()), fields.get(FieldType.CURRENCY_CODE.getName()));
            double totalFinal=Double.parseDouble(amt)+MerfinalBal;
            merchantWalletPODTORev.setFinalBalance(String.valueOf(totalFinal));
            logger.info("MerchantWalletPODTORev : " + new Gson().toJson(merchantWalletPODTORev));
            merchantWalletPODao.SaveAndUpdteWallet(merchantWalletPODTORev);



        }else {
        	logger.info("updateMerchantWallet StatusEnquiryProcessorpayout : " + fields.getFieldsAsString());
        	fieldsDao.updateMerchantTotalBalance(fields);
        }
    }
    
    @Autowired
    private FieldsDao fieldsDao;

    public void updateTxnData(Fields fields) {

        try {
            Fields newFields = createAllForRefund(fields);
            String txnId = TransactionManager.getNewTransactionId();
            newFields.put(FieldType.TXN_ID.getName(), txnId);
            newFields.put("_id", txnId);

            Map<String, String> fieldsMap = fields.getFields();

            for (String key : fieldsMap.keySet()) {

                if (key.equalsIgnoreCase(FieldType.TXN_ID.getName()) || key.equalsIgnoreCase("_id")
                        || key.equalsIgnoreCase(FieldType.TXNTYPE.getName())) {
                    continue;
                }

                newFields.put(key, fieldsMap.get(key));
            }

            newFields.put(FieldType.ORIG_TXNTYPE.getName(), newFields.get(FieldType.TXNTYPE.getName()));

            fieldDao.insertPOUpdate(fields);
        } catch (Exception e) {
            logger.info("Exception while udpating new txn data from status enquiry");
        }
    }

    public boolean getTransactionFields(Fields fields) {

        try {
            BasicDBObject finalquery = new BasicDBObject(FieldType.PG_REF_NUM.getName(),
                    fields.get(FieldType.PG_REF_NUM.getName()));

            MongoDatabase dbIns = mongoInstance.getDB();
            MongoCollection<Document> coll = dbIns.getCollection(propertiesManager.propertiesMap
                    .get(prefix + Constants.TRANSACTION_LEDGER_PO_TransactionStatus.getValue()));
            BasicDBObject match = new BasicDBObject("$match", finalquery);

            BasicDBObject sort = new BasicDBObject("$sort", new BasicDBObject("INSERTION_DATE", -1));
            BasicDBObject limit = new BasicDBObject("$limit", 1);

            List<BasicDBObject> pipeline = Arrays.asList(match, sort, limit);

            AggregateIterable<Document> output = coll.aggregate(pipeline);
            output.allowDiskUse(true);
            MongoCursor<Document> cursor = output.iterator();

            if (!cursor.hasNext()) {
                return false;
            } else {
                while (cursor.hasNext()) {
                    Document documentObj = cursor.next();

                    if (null != documentObj) {
                        for (int j = 0; j < documentObj.size(); j++) {
                            for (String columnName : documentObj.keySet()) {
                                if (documentObj.get(columnName) != null) {

                                    fields.put(columnName, documentObj.get(columnName).toString());
                                } else {

                                }

                            }
                        }
                    }
                    fields.logAllFields("Previous fields");
                    if (fields.contains("_id")) {
                        fields.remove("_id");

                    }
                    if (fields.contains(FieldType.AMOUNT.getName())) {
                        fields.put(FieldType.AMOUNT.getName(), Amount.formatAmount(
                                fields.get(FieldType.AMOUNT.getName()), fields.get(FieldType.CURRENCY_CODE.getName())));

                    }
                    fields.put(FieldType.CARD_HOLDER_TYPE.getName(), CardHolderType.CONSUMER.toString());
                    fields.put(FieldType.PAYMENTS_REGION.getName(), AccountCurrencyRegion.DOMESTIC.toString());

                }

                return true;
            }

        } catch (Exception e) {
            logger.error("Exception while fetching transaction for status enquiry", e);
        }
        return false;
    }

    private Fields createAllForRefund(Fields field) {

        Fields fields = new Fields();

        try {
            BasicDBObject finalquery = new BasicDBObject(FieldType.PG_REF_NUM.getName(),
                    field.get(FieldType.PG_REF_NUM.getName()));

            MongoDatabase dbIns = mongoInstance.getDB();
            MongoCollection<Document> coll = dbIns.getCollection(propertiesManager.propertiesMap
                    .get(prefix + Constants.TRANSACTION_LEDGER_PO_TransactionStatus.getValue()));
            BasicDBObject match = new BasicDBObject("$match", finalquery);

            BasicDBObject sort = new BasicDBObject("$sort", new BasicDBObject("INSERTION_DATE", -1));
            BasicDBObject limit = new BasicDBObject("$limit", 1);

            List<BasicDBObject> pipeline = Arrays.asList(match, sort, limit);

            AggregateIterable<Document> output = coll.aggregate(pipeline);
            output.allowDiskUse(true);
            MongoCursor<Document> cursor = output.iterator();

            if (!cursor.hasNext()) {
            } else {
                while (cursor.hasNext()) {
                    Document documentObj = cursor.next();

                    if (null != documentObj) {
                        for (int j = 0; j < documentObj.size(); j++) {
                            for (String columnName : documentObj.keySet()) {
                                if (documentObj.get(columnName) != null) {

                                    fields.put(columnName, documentObj.get(columnName).toString());
                                } else {

                                }

                            }
                        }
                    }
                    fields.logAllFields("Previous fields");
                    if (fields.contains("_id")) {
                        fields.remove("_id");

                    }
                    if (fields.contains(FieldType.AMOUNT.getName())) {
                        fields.put(FieldType.AMOUNT.getName(), Amount.formatAmount(
                                fields.get(FieldType.AMOUNT.getName()), fields.get(FieldType.CURRENCY_CODE.getName())));

                    }
                    fields.put(FieldType.CARD_HOLDER_TYPE.getName(), CardHolderType.CONSUMER.toString());
                    fields.put(FieldType.PAYMENTS_REGION.getName(), AccountCurrencyRegion.DOMESTIC.toString());

                }

            }
            cursor.close();
            return fields;
        } catch (Exception e) {
            logger.error("Exception while getting previous fields", e);
        }
        return null;

    }

    public Fields getMerchantStatus(Fields fields) {
        User user = userDao.findByPayId(fields.get(FieldType.PAY_ID.getName()));
        if (user == null) {
            fields.put(FieldType.STATUS.getName(), StatusType.INVALID.getName());
            fields.put(FieldType.RESPONSE_MESSAGE.getName(), ErrorType.PAY_ID.getResponseMessage());
            fields.put(FieldType.RESPONSE_CODE.getName(), ErrorType.PAY_ID.getCode());
            return fields;
        }

        return getTransactionFieldsEnquiry(fields);
    }

	private Fields getTransactionFieldsEnquiry(Fields fields) {
		Fields fields2 = new Fields();

		try {
			BasicDBObject finalquery = new BasicDBObject(FieldType.ORDER_ID.getName(), fields.get(FieldType.ORDER_ID.getName()));

			MongoDatabase dbIns = mongoInstance.getDB();
			MongoCollection<Document> coll = dbIns.getCollection(propertiesManager.propertiesMap
					.get(prefix + Constants.TRANSACTION_LEDGER_PO_TransactionStatus.getValue()));
			BasicDBObject match = new BasicDBObject("$match", finalquery);

			BasicDBObject sort = new BasicDBObject("$sort", new BasicDBObject("INSERTION_DATE", -1));
			BasicDBObject limit = new BasicDBObject("$limit", 1);

			List<BasicDBObject> pipeline = Arrays.asList(match, sort, limit);

			AggregateIterable<Document> output = coll.aggregate(pipeline);
			output.allowDiskUse(true);
			MongoCursor<Document> cursor = output.iterator();

			if (cursor.hasNext()) {
				Document documentObj = cursor.next();

				if (null != documentObj) {
					for (String columnName : documentObj.keySet()) {
						if (documentObj.get(columnName) != null) {
							fields2.put(columnName, documentObj.get(columnName).toString());
						}

					}
				}

				if (fields2.contains(FieldType.AMOUNT.getName())) {
					fields2.put(FieldType.AMOUNT.getName(), Amount.formatAmount(fields2.get(FieldType.AMOUNT.getName()),
							fields2.get(FieldType.CURRENCY_CODE.getName())));
				}

			} else {
				fields2.put(FieldType.STATUS.getName(), StatusType.INVALID.getName());
				fields2.put(FieldType.RESPONSE_MESSAGE.getName(), "No Such Transaction found");
				fields2.put(FieldType.PG_TXN_MESSAGE.getName(), "No Such Transaction found");
				fields2.put(FieldType.RESPONSE_CODE.getName(), ErrorType.NO_SUCH_TRANSACTION.getCode());
			}
			cursor.close();
			return fields2;
		} catch (Exception e) {
			logger.error("Exception while fetching transaction for status enquiry", e);
		}
		return fields2;

	}

}
