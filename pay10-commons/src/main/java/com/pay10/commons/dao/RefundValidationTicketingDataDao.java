package com.pay10.commons.dao;

import java.util.List;

import org.bson.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.pay10.commons.exception.SystemException;
import com.pay10.commons.mongo.MongoInstance;
import com.pay10.commons.util.Constants;
import com.pay10.commons.util.PropertiesManager;

@Service
public class RefundValidationTicketingDataDao {
	
	private static final String prefix = "MONGO_DB_";
	@Autowired
	private MongoInstance mongoInstance;

	@Autowired
	private PropertiesManager propertiesManager;
	private static Logger logger = LoggerFactory.getLogger(RefundValidationTicketingDataDao.class.getName());
	
	public void insertTransaction(List<Document> lstDoc) throws SystemException {
		logger.info("Inside RefundValidationTicketingDao in insertTransaction !!");
		logger.info("Inside RefundValidationTicketingDao, Document list size : " + lstDoc.size());
		try {
			MongoDatabase dbIns = null;
			
			if(lstDoc.size() > 0) {
				logger.info("Added "+lstDoc.size()+" transactions in mongo document for upload to Refund/Reconciled");
				dbIns = mongoInstance.getDB();
				MongoCollection<Document>  collection = dbIns
						.getCollection(PropertiesManager.propertiesMap.get(prefix + Constants.COLLECTION_NAME.getValue()));
				collection.insertMany(lstDoc);
				lstDoc.clear();
			}		
			
		} catch (Exception e) {
			logger.info("Exception inside RefundValidationTicketingDao in insertTransaction for Refund Validation collection "+ e);
		}
	}
}
