package com.pay10.crm.mongoReports;

import java.util.ArrayList;
import java.util.List;

import org.bson.Document;
import org.bson.conversions.Bson;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.google.gson.Gson;
import com.mongodb.BasicDBObject;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.UpdateOptions;
import com.mongodb.client.model.Updates;
import com.mongodb.client.result.UpdateResult;
import com.pay10.commons.dto.CashDepositDTOPO;
import com.pay10.commons.dto.PassbookPODTO;
import com.pay10.commons.mongo.MongoInstance;
import com.pay10.commons.util.Constants;
import com.pay10.commons.util.PropertiesManager;

@Component
public class ApproverListPODao {

	private static final String prefix = "MONGO_DB_";

	@Autowired
	PropertiesManager propertiesManager;

	@Autowired
	private MongoInstance mongoInstance;

	private CashDepositDTOPO cashDepositDTOPO;
	private PassbookPODTO passbookPODTO;

	public void updateCashDeposit(CashDepositDTOPO cashDepositDTOPO) {

		try {
			MongoDatabase dbIns = mongoInstance.getDB();
			MongoCollection<Document> collection = dbIns
					.getCollection(PropertiesManager.propertiesMap.get(prefix + Constants.CASH_DEPOSIT_PO.getValue()));
			
			List<BasicDBObject> findQuery = new ArrayList<BasicDBObject>();
			findQuery.add(new BasicDBObject("status", "Pending"));
			findQuery.add(new BasicDBObject("payId", cashDepositDTOPO.getPayId()));
			findQuery.add(new BasicDBObject("txnId", cashDepositDTOPO.getTxnId()));
			System.out.println("DB DATA : " + new Gson().toJson(findQuery));
			
			Bson updates = null;
			
			if(!cashDepositDTOPO.getApproverRemark().equalsIgnoreCase("NA")) {
				updates = Updates.combine(Updates.set("status", "Approved"),Updates.set("approverRemark", cashDepositDTOPO.getApproverRemark()));
				
			}else {
				updates =Updates.combine(Updates.set("status", "Reject"),Updates.set("rejectRemark", cashDepositDTOPO.getRejectRemark()));
			}
			
			BasicDBObject searchQuery = new BasicDBObject("$and", findQuery);
			
			UpdateOptions options = new UpdateOptions().upsert(true);
			UpdateResult result=collection.updateOne(searchQuery, updates, options);
			
			System.out.println("SEARCH QUERY : " + new Gson().toJson(result));
			
						
		} catch (Exception exception) {
			exception.printStackTrace();
		}
	}
	
	}
