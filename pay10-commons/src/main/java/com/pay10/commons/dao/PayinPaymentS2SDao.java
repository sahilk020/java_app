package com.pay10.commons.dao;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.commons.lang3.StringUtils;
import org.bson.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.mongodb.BasicDBObject;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import com.pay10.commons.mongo.MongoInstance;
import com.pay10.commons.user.PayinPaymentS2S;
import com.pay10.commons.util.CrmFieldType;
import com.pay10.commons.util.PayinPaymentS2SUtilities;

@Component
public class PayinPaymentS2SDao {
	private static Logger logger = LoggerFactory.getLogger(PayinPaymentS2SDao.class.getName());

	@Autowired
	private MongoInstance mongoInstance;
	private static final String PREFIX = "MONGO_DB_";

	public void create(PayinPaymentS2S paymentLink) {

		Document doc = PayinPaymentS2SUtilities.getDocFromPaymentLink(paymentLink);
		MongoDatabase dbIns = mongoInstance.getDB();
		MongoCollection<Document> collection = dbIns.getCollection("S2SPaymentTxnRedirectStatus");
		collection.insertOne(doc);

	}

	public PayinPaymentS2S update(PayinPaymentS2S paymentLink) {
		String oldUpdateDate = paymentLink.getUpdateDate();
		try {
			MongoDatabase dbIns = mongoInstance.getDB();
			MongoCollection<Document> collection = dbIns.getCollection("S2SPaymentTxnRedirectStatus");

			paymentLink.setUpdateDate(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
			Document doc = PayinPaymentS2SUtilities.getDocFromPaymentLink(paymentLink);
			BasicDBObject findQuery = new BasicDBObject();
			findQuery.put("PAYMENT_REDIRECT_ID", paymentLink.getPaymentRedirectId());
			collection.replaceOne(findQuery, doc);
			logger.info("Intent Payment Link updated successfully : " + paymentLink.getPaymentRedirectId());
		} catch (Exception e) {
			logger.info("Failed to update intent payment link : " + paymentLink.getPaymentRedirectId());
			logger.error(e.getMessage());
			paymentLink.setUpdateDate(oldUpdateDate);
		}
		return paymentLink;
	}

	public PayinPaymentS2S findByOrderId(String orderId) {
		PayinPaymentS2S paymentLink = null;
		if (StringUtils.isEmpty(orderId)) {
			logger.error("Empty order id");
			return null;
		}
		MongoDatabase dbIns = mongoInstance.getDB();
		MongoCollection<Document> collection = dbIns.getCollection("S2SPaymentTxnRedirectStatus");

		BasicDBObject findQuery = new BasicDBObject("ORDER_ID", orderId);
		logger.info("Query for find intent payment by order : " + findQuery);
		FindIterable<Document> output = collection.find(findQuery);
		MongoCursor<Document> cursor = output.iterator();
		Document doc = null;
		if (cursor.hasNext()) {
			doc = cursor.next();
		}
		cursor.close();
		if (doc != null && doc.getString("ORDER_ID").equals(orderId)) {
			paymentLink = PayinPaymentS2SUtilities.getPaymentLinkFromDoc(doc);
		}
		return paymentLink;
	}

	public PayinPaymentS2S findByPaymentLinkId(String paymentLinkId) {
		PayinPaymentS2S paymentLink = null;
		MongoDatabase dbIns = mongoInstance.getDB();
		MongoCollection<Document> collection = dbIns.getCollection("S2SPaymentTxnRedirectStatus");

		BasicDBObject findQuery = new BasicDBObject();
		findQuery.put("PAYMENT_REDIRECT_ID", paymentLinkId);
		logger.info("Query for find invoice by Id : " + findQuery);
		FindIterable<Document> output = collection.find(findQuery);
		MongoCursor<Document> cursor = output.iterator();

		Document doc = null;
		if (cursor.hasNext()) {
			doc = cursor.next();
		}
		cursor.close();
		if (doc != null && doc.getString("PAYMENT_REDIRECT_ID").equals(paymentLinkId)) {
			paymentLink = PayinPaymentS2SUtilities.getPaymentLinkFromDoc(doc);
		}
		return paymentLink;
	}

}
