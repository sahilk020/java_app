package com.pay10.commons.mongo;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.google.gson.Gson;
import com.ibm.icu.math.BigDecimal;
import com.mongodb.BasicDBObject;
import com.mongodb.BasicDBObjectBuilder;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import com.pay10.commons.dto.PayoutTxnLedgerDTO;
import com.pay10.commons.util.Constants;
import com.pay10.commons.util.Currency;
import com.pay10.commons.util.PropertiesManager;

@Component
public class PayoutTxnLedgerDao {

	private static final String prefix = "MONGO_DB_";

	@Autowired
	PropertiesManager propertiesManager;
	@Autowired
	private MongoInstance mongoInstance;

	public List<PayoutTxnLedgerDTO> fetchTransactionPOReport(String payId, String fromIndex, String toIndex,
			String txnType) {

		List<PayoutTxnLedgerDTO> payoutTxnLedgerDTOs = new ArrayList<PayoutTxnLedgerDTO>();
		MongoDatabase dbIns = mongoInstance.getDB();
		MongoCollection<Document> coll = dbIns.getCollection(
				PropertiesManager.propertiesMap.get(prefix + Constants.TRANSACTION_LEDGER_PO_TransactionStatus.getValue()));

		List<BasicDBObject> paramConditionLst = new ArrayList<BasicDBObject>();

		if (StringUtils.isNotBlank(payId)) {
			paramConditionLst.add(new BasicDBObject("PAY_ID", payId));
		}

		if (StringUtils.isNotBlank(txnType)) {
			paramConditionLst.add(new BasicDBObject("PAY_TYPE", txnType.toUpperCase()));
		}

		paramConditionLst.add(new BasicDBObject("DATE_INDEX",
				BasicDBObjectBuilder.start("$gte", fromIndex).add("$lte", toIndex).get()));

		BasicDBObject queryExecute = new BasicDBObject("$and", paramConditionLst);
		System.out.println("Final Query : "  + queryExecute);
		
		MongoCursor<Document> cursor = coll.find(queryExecute).iterator();

		Gson gson = new Gson();
		while (cursor.hasNext()) {
			Document document = (Document) cursor.next();
			System.out.println(document);
			PayoutTxnLedgerDTO payoutTxnLedgerDTO=new PayoutTxnLedgerDTO();
			payoutTxnLedgerDTO.setAccCityName(document.getString("ACC_CITY_NAME"));
			payoutTxnLedgerDTO.setAccName(document.getString("ACC_NAME"));
			payoutTxnLedgerDTO.setAccNo(document.getString("ACC_NO"));
			payoutTxnLedgerDTO.setAccProvince(document.getString("ACC_PROVINCE"));
			payoutTxnLedgerDTO.setAccType(document.getString("ACC_TYPE"));
			payoutTxnLedgerDTO.setAmount(document.getString("AMOUNT"));
			payoutTxnLedgerDTO.setBankBranch(document.getString("BANK_BRANCH"));
			payoutTxnLedgerDTO.setBankCode(document.getString("BANK_CODE"));
			payoutTxnLedgerDTO.setCreateDate(document.getString("CREATE_DATE"));
			payoutTxnLedgerDTO.setCurrencyCode(Currency.getAlphabaticCode(document.getString("CURRENCY_CODE")));
			payoutTxnLedgerDTO.setDateIndex(document.getString("DATE_INDEX"));
			payoutTxnLedgerDTO.setIfsc(document.getString("IFSC"));
			payoutTxnLedgerDTO.setMerchantName(document.getString("MERCHANT_NAME"));
			payoutTxnLedgerDTO.setOrderId(document.getString("ORDER_ID"));
			payoutTxnLedgerDTO.setPayerName(document.getString("PAYER_NAME"));
			payoutTxnLedgerDTO.setPayerPhone(document.getString("PAYER_PHONE"));
			payoutTxnLedgerDTO.setPayId(document.getString("PAY_ID"));
			payoutTxnLedgerDTO.setPayType(document.getString("PAY_TYPE"));
			payoutTxnLedgerDTO.setPgRefNum(document.getString("PG_REF_NUM"));
			payoutTxnLedgerDTO.setRemarks(document.getString("REMARKS"));
			payoutTxnLedgerDTO.setSurchargeAmount(document.getString("SURCHARGE_AMOUNT"));
			payoutTxnLedgerDTO.setTotalAmount(document.getString("TOTAL_AMOUNT"));
			payoutTxnLedgerDTO.setStatus(document.getString("STATUS"));
			
			payoutTxnLedgerDTOs.add(payoutTxnLedgerDTO);
		}
		System.out.println("Return Data : " + gson.toJson(payoutTxnLedgerDTOs));
		return payoutTxnLedgerDTOs;
	}
//	public static void main(String[] args) {
//		System.out.println(Currency.getAlphabaticCode("356"));
//		
//	}
}
