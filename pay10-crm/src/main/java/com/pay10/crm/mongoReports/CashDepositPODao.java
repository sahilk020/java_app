package com.pay10.crm.mongoReports;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.pay10.commons.user.UserDao;
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
import com.pay10.commons.mongo.MongoInstance;
import com.pay10.commons.util.Constants;
import com.pay10.commons.util.PropertiesManager;

@Component
public class CashDepositPODao {

	private static final String prefix = "MONGO_DB_";
	private static final Logger logger = LoggerFactory.getLogger(CashDepositPODao.class.getName());



	@Autowired
	PropertiesManager propertiesManager;

	@Autowired
	private MongoInstance mongoInstance;
	@Autowired
	private UserDao userDao;

//	public List<CashDepositDTOPO> getPendingList(String payId) {
//		List<CashDepositDTOPO> cashDepositDTOPOs = new ArrayList<>();
//		BasicDBObject params = new BasicDBObject();
//
//		params.put("status", "Pending");
//		if(payId.equalsIgnoreCase("ALL")) {
//
//		}else {
//			params.put("payId", payId);
//		}
//
//		MongoDatabase dbIns = mongoInstance.getDB();
//		MongoCollection<Document> coll = dbIns
//				.getCollection(PropertiesManager.propertiesMap.get(prefix + Constants.CASH_DEPOSIT_PO.getValue()));
//		List<BasicDBObject> queryExecute = Arrays.asList(new BasicDBObject("$match", params));
//		MongoCursor<Document> cursor = coll.aggregate(queryExecute).iterator();
//		CashDepositDTOPO cashDepositDTOPO = null;
//		while (cursor.hasNext()) {
//			Document document = (Document) cursor.next();
//			cashDepositDTOPO=new Gson().fromJson(document.toJson(), CashDepositDTOPO.class);
//			String businessName= userDao.getBusinessNameByPayId(cashDepositDTOPO.getPayId());
//
//			if(businessName!=null) {
//				cashDepositDTOPO.setBusinessName(businessName);
//			}
//			cashDepositDTOPOs.add(cashDepositDTOPO);
//		}
//		System.out.println("Dao Data : " + new Gson().toJson(cashDepositDTOPOs));
//		return cashDepositDTOPOs;
//	}


	public List<CashDepositDTOPO> getPendingList(String payId) {
		List<CashDepositDTOPO> cashDepositDTOPOs = new ArrayList<>();
		BasicDBObject params = new BasicDBObject();


		//params.put("status", "Pending");
		if(payId.equalsIgnoreCase("ALL")) {
			params.put("status", "Pending");
		}else {
			params.put("payId", payId);
		}
		MongoDatabase dbIns = mongoInstance.getDB();
		MongoCollection<Document> coll = dbIns
				.getCollection(PropertiesManager.propertiesMap.get(prefix + Constants.CASH_DEPOSIT_PO.getValue()));
		List<BasicDBObject> queryExecute = Arrays.asList(new BasicDBObject("$match", params));
		MongoCursor<Document> cursor = coll.aggregate(queryExecute).iterator();
		CashDepositDTOPO cashDepositDTOPO = null;
		while (cursor.hasNext()) {
			Document document = (Document) cursor.next();
			String businessName="";
			if(document.getString("payId")!=null||!document.getString("payId").isEmpty()){
				businessName=userDao.getBusinessNameByPayId(document.getString("payId"));
				logger.info("Cash Deposit PO business Name "+businessName);
			}
			cashDepositDTOPO=new Gson().fromJson(document.toJson(), CashDepositDTOPO.class);
			cashDepositDTOPO.setBusinessName(businessName);

			cashDepositDTOPOs.add(cashDepositDTOPO);

		}
		System.out.println("Dao Data : " + new Gson().toJson(cashDepositDTOPOs));

		return cashDepositDTOPOs;
	}

	public String saveCashDeposit(CashDepositDTOPO cashDepositDTOPO) {
		MongoDatabase dbIns = mongoInstance.getDB();
		MongoCollection<Document> coll = dbIns
				.getCollection(PropertiesManager.propertiesMap.get(prefix + Constants.CASH_DEPOSIT_PO.getValue()));
		coll.insertOne(Document.parse(new Gson().toJson(cashDepositDTOPO)));

		return "Success";
	}

	public CashDepositDTOPO findByPayId(CashDepositDTOPO cashDepositDTOPOReq) {
		MongoDatabase dbIns = mongoInstance.getDB();
		BasicDBObject params = new BasicDBObject();

		params.put("status", "Approved");
		params.put("txnId", cashDepositDTOPOReq.getTxnId());
		params.put("payId", cashDepositDTOPOReq.getPayId());
		MongoCollection<Document> coll = dbIns
				.getCollection(PropertiesManager.propertiesMap.get(prefix + Constants.CASH_DEPOSIT_PO.getValue()));
		List<BasicDBObject> queryExecute = Arrays.asList(new BasicDBObject("$match", params));
		MongoCursor<Document> cursor = coll.aggregate(queryExecute).iterator();
		CashDepositDTOPO cashDepositDTOPO = null;
		if (cursor.hasNext()) {
			Document document = (Document) cursor.next();

			cashDepositDTOPO=new Gson().fromJson(document.toJson(), CashDepositDTOPO.class);
		}
		return cashDepositDTOPO;
	}

}
