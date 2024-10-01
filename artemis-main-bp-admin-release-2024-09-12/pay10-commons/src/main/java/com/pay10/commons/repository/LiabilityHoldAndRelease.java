package com.pay10.commons.repository;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.bson.BsonNull;
import org.bson.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.mongodb.BasicDBObject;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.result.UpdateResult;
import com.pay10.commons.dto.BulkUploadLiabilityHoldAndRelease;
import com.pay10.commons.mongo.MongoInstance;
import com.pay10.commons.util.Constants;
import com.pay10.commons.util.FieldType;
import com.pay10.commons.util.PropertiesManager;
import com.pay10.commons.util.TransactionManager;

@Service
public class LiabilityHoldAndRelease {
	@Autowired
	private MongoInstance mongoInstance;
	private static final String prefix = "MONGO_DB_";
	private static Logger logger = LoggerFactory.getLogger(LiabilityHoldAndRelease.class.getName());

	public String insert(BulkUploadLiabilityHoldAndRelease bulkUploadLiabilityHoldAndRelease) {
		String msg="failed";
		try {
			MongoDatabase dbIns = mongoInstance.getDB();
			MongoCollection<Document> collection = dbIns.getCollection(
					PropertiesManager.propertiesMap.get(prefix + Constants.Liability_HOLD_AND_RELEASE.getValue()));
			if (bulkUploadLiabilityHoldAndRelease != null) {
				
					Document document = new Document();
					document.append(FieldType.PG_REF_NUM.getName(), bulkUploadLiabilityHoldAndRelease.getPgRefNum());
					document.append(FieldType.STATUS.getName(), "0");
					document.append(FieldType.ERROR_MESSAGE.getName(), null);
					document.append(FieldType.LIABILITY_TYPE.getName(), bulkUploadLiabilityHoldAndRelease.getLiabilityType());
					document.append(FieldType.UPDATEDBY.getName(), bulkUploadLiabilityHoldAndRelease.getUpdatedby());
					document.append(FieldType.UPDATEDAT.getName(), bulkUploadLiabilityHoldAndRelease.getUpdatedat());

					
					document.append(FieldType.DATE_TIME.getName(), bulkUploadLiabilityHoldAndRelease.getLiablityDateIndex());
					document.append(FieldType.DATE_INDEX.getName(), bulkUploadLiabilityHoldAndRelease.getDateIndex());

					document.append(FieldType.UPDATEDBY.getName(), bulkUploadLiabilityHoldAndRelease.getUpdatedby());
					document.append(FieldType.UPDATEDAT.getName(), bulkUploadLiabilityHoldAndRelease.getUpdatedat());
					
					if (bulkUploadLiabilityHoldAndRelease.getLiabilityType().equalsIgnoreCase("HOLD")) {
						document.append(FieldType.LIABILITYHOLDREMARKS.getName(), bulkUploadLiabilityHoldAndRelease.getRemarks());
						
					}else {
						document.append(FieldType.LIABILITYRELEASEREMARKS.getName(), bulkUploadLiabilityHoldAndRelease.getRemarks());
					}
					
					String id = TransactionManager.getNewTransactionId();
					document.put("_id", id);
					collection.insertOne(document);
				
				msg="Successfull";
			}else {
				msg="list is null";
			}
		} catch (Exception e) {
			logger.info("Exception Occur in insert()",e);
			e.printStackTrace();
		}
		return msg;
	}
	
	public long update(String PgRefNum,String errorMessage,String type,String user) {
		
		
		
		long status=0;
		try {
			MongoDatabase dbIns = mongoInstance.getDB();
			MongoCollection<Document> collection = dbIns.getCollection(
					PropertiesManager.propertiesMap.get(prefix + Constants.Liability_HOLD_AND_RELEASE.getValue()));
			if (!StringUtils.isBlank(PgRefNum)) {
				
				if (errorMessage.equals("sucesss")) {
					
					BasicDBObject newDocument = new BasicDBObject();
					newDocument.append("$set", new BasicDBObject().append(FieldType.ERROR_MESSAGE.getName(), "Founded").append(FieldType.STATUS.getName(), "1"));
							
					BasicDBObject searchQuery = new BasicDBObject().append(FieldType.PG_REF_NUM.getName(),PgRefNum);


					UpdateResult result=collection.updateMany(searchQuery, newDocument);
					status=result.getModifiedCount();
				}else {
					BasicDBObject newDocument = new BasicDBObject();
					newDocument.append("$set", new BasicDBObject().append(FieldType.ERROR_MESSAGE.getName(), "Not Founded").append(FieldType.STATUS.getName(), "0"));
					
					BasicDBObject searchQuery = new BasicDBObject().append(FieldType.PG_REF_NUM.getName(), PgRefNum);

					UpdateResult result=collection.updateMany(searchQuery, newDocument);
					status=result.getModifiedCount();
				}
				
			}
		} catch (Exception e) {
			logger.info("Exception Occur in insert()",e);
			e.printStackTrace();
		}
		return status;
	}
	
public double getTotalAmountFromPgRefNUM(List<String> pgRefnum) {
		
		
		
		double status=0;
		try {
			MongoDatabase dbIns = mongoInstance.getDB();
			MongoCollection<Document> collection = dbIns.getCollection(
					PropertiesManager.propertiesMap.get(prefix + Constants.TRANSACTION_STATUS_COLLECTION.getValue()));
			
			List<Document> query=Arrays.asList(new Document("$match", 
				    new Document("PG_REF_NUM", 
				    new Document("$in", pgRefnum))), 
				    new Document("$group", 
				    new Document("_id", 
				    new BsonNull())
				            .append("SUM(AMOUNT)", 
				    new Document("$sum", 
				    new Document("$toDouble", "$TOTAL_AMOUNT")))));
			
			logger.info("Getting Total Amount From Pg Ref Number "+query);
			MongoCursor<Document>mongoCursor=collection.aggregate(query).iterator();
			while (mongoCursor.hasNext()) {
				Document document = (Document) mongoCursor.next();
				status=Double.parseDouble(String.valueOf(document.get("SUM(AMOUNT)")));
			}
			
		} catch (Exception e) {
			logger.info("Exception Occur in getTotalAmountFromPgRefNUM()",e);
			e.printStackTrace();
		}
		return status;
	}

	public List<BulkUploadLiabilityHoldAndRelease> getAllStatusZero(){
		List<BulkUploadLiabilityHoldAndRelease>lists=new ArrayList<>();
		MongoDatabase dbIns = mongoInstance.getDB();
		MongoCollection<Document> collection = dbIns.getCollection(
				PropertiesManager.propertiesMap.get(prefix + Constants.Liability_HOLD_AND_RELEASE.getValue()));
		
		BasicDBObject match=new BasicDBObject();
		match.put(FieldType.STATUS.getName(), "0");
		match.put(FieldType.ERROR_MESSAGE.getName(), new BsonNull());
		
		List<BasicDBObject> query=Arrays.asList(new BasicDBObject("$match", match));
		logger.info("Query to get All StatusZero : "+query);
		MongoCursor<Document>cursor=collection.aggregate(query).iterator();
		while (cursor.hasNext()) {
			BulkUploadLiabilityHoldAndRelease holdAndRelease=new BulkUploadLiabilityHoldAndRelease();
			Document document = (Document) cursor.next();
			holdAndRelease.setPgRefNum(document.getString(FieldType.PG_REF_NUM.getName()));
			holdAndRelease.setStatus(document.getString(FieldType.STATUS.getName()));
			holdAndRelease.setErrorMsg(document.getString(FieldType.ERROR_MESSAGE.getName()));
			holdAndRelease.setLiabilityType(document.getString(FieldType.LIABILITY_TYPE.getName()));
			holdAndRelease.setDateIndex(document.getString(FieldType.DATE_TIME.getName()));
			holdAndRelease.setDateIndex(document.getString(FieldType.DATE_INDEX.getName()));
			String liabilityType=document.getString(FieldType.LIABILITY_TYPE.getName());
			if (liabilityType.equalsIgnoreCase("HOLD")) {
			holdAndRelease.setRemarks(document.getString(FieldType.LIABILITYHOLDREMARKS.getName()));
			holdAndRelease.setUpdatedby(document.getString(FieldType.UPDATEDBY.getName()));
			holdAndRelease.setUpdatedat(document.getString(FieldType.UPDATEDAT.getName()));


			}else {
				holdAndRelease.setRemarks(document.getString(FieldType.LIABILITYRELEASEREMARKS.getName()));
				holdAndRelease.setUpdatedby(document.getString(FieldType.UPDATEDBY.getName()));
				holdAndRelease.setUpdatedat(document.getString(FieldType.UPDATEDAT.getName()));
			}
			lists.add(holdAndRelease);
		}
		
		return lists;
	}
}
