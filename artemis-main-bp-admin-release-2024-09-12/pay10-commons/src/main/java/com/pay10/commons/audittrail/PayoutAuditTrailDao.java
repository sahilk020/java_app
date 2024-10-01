package com.pay10.commons.audittrail;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import com.google.gson.Gson;
import com.mongodb.BasicDBObject;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.UpdateOptions;
import com.mongodb.client.model.Updates;
import com.mongodb.client.result.UpdateResult;
import com.pay10.commons.mongo.MongoInstance;
import com.pay10.commons.util.Constants;
import com.pay10.commons.util.PropertiesManager;
import com.pay10.commons.util.TransactionManager;

@Component("PayoutAuditTrailPO")
public class PayoutAuditTrailDao {

	private static final String prefix = "MONGO_DB_";

	@Autowired
	private MongoInstance mongoInstance;

	public String saveAuditTrailPO(PayoutAuditTrailDTO auditTrailDTO) {

		try {
			MongoDatabase dbIns = mongoInstance.getDB();
			MongoCollection<Document> collection = dbIns.getCollection(PropertiesManager.propertiesMap.get(prefix + Constants.PAYOUT_AUDIT_TRIAL.getValue()));
			auditTrailDTO.setAuditTxnId(TransactionManager.getNewTransactionId());
			Document newDocument = Document.parse(new Gson().toJson(auditTrailDTO));
			collection.insertOne(newDocument);

		} catch (Exception exception) {
			exception.printStackTrace();
		}
		return auditTrailDTO.getAuditTxnId();
	}

	public PayoutAuditTrailDTO fetchAuditrailByTxnId(String txnId) {
		PayoutAuditTrailDTO payoutAuditTrailDTO = null;
		try {
			MongoDatabase dbIns = mongoInstance.getDB();
			MongoCollection<Document> collection = dbIns.getCollection(PropertiesManager.propertiesMap.get(prefix + Constants.PAYOUT_AUDIT_TRIAL.getValue()));
			BasicDBObject params = new BasicDBObject();

			params.put("auditTxnId", txnId);

			List<BasicDBObject> queryExecute = Arrays.asList(new BasicDBObject("$match", params));
			MongoCursor<Document> cursor = collection.aggregate(queryExecute).iterator();

			if (cursor.hasNext()) {
				Document document = (Document) cursor.next();
				payoutAuditTrailDTO = new PayoutAuditTrailDTO();
				payoutAuditTrailDTO = new Gson().fromJson(document.toJson(), PayoutAuditTrailDTO.class);
			}

		} catch (Exception exception) {
			exception.printStackTrace();
		}
		return payoutAuditTrailDTO;
	}
	
	
	public void updateAuditTrailPO(PayoutAuditTrailDTO auditTrailDTO) {

		try {
			
			MongoDatabase dbIns = mongoInstance.getDB();
			MongoCollection<Document> collection = dbIns.getCollection(PropertiesManager.propertiesMap.get(prefix + Constants.PAYOUT_AUDIT_TRIAL.getValue()));
			Bson updates = null;
		  //auditTrailDTO.setUpdatedDate(new SimpleDateFormat("yyyy-MM-dd").format(new Date(System.currentTimeMillis())));
			updates = Updates.combine(Updates.set("updatedJson", auditTrailDTO.getUpdatedJson()),Updates.set("updatedDate", auditTrailDTO.getUpdatedDate()));
			List<BasicDBObject> findQuery = new ArrayList<BasicDBObject>();
			findQuery.add(new BasicDBObject("auditTxnId", auditTrailDTO.getAuditTxnId()));
			BasicDBObject searchQuery = new BasicDBObject("$and", findQuery);
			UpdateOptions options = new UpdateOptions().upsert(true);
			UpdateResult result=collection.updateOne(searchQuery, updates, options);

		} catch (Exception exception) {
			exception.printStackTrace();
		}
	}

}
