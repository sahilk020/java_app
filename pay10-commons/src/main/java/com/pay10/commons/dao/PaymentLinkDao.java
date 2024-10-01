package com.pay10.commons.dao;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.bson.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.mongodb.BasicDBObject;
import com.mongodb.client.AggregateIterable;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import com.pay10.commons.exception.ErrorType;
import com.pay10.commons.exception.SystemException;
import com.pay10.commons.mongo.MongoInstance;
import com.pay10.commons.user.Invoice;
import com.pay10.commons.user.PaymentLink;
import com.pay10.commons.util.Constants;
import com.pay10.commons.util.CrmFieldType;
import com.pay10.commons.util.InvoiceUtilities;
import com.pay10.commons.util.PaymentLinkUtilities;
import com.pay10.commons.util.PropertiesManager;

@Component
public class PaymentLinkDao {
	
	@Autowired
	private MongoInstance mongoInstance;
	private static final String PREFIX = "MONGO_DB_";
	private static Logger logger = LoggerFactory.getLogger(PaymentLinkDao.class.getName());

	public void create(PaymentLink paymentLink) {

		
			Document doc = PaymentLinkUtilities.getDocFromPaymentLink(paymentLink);
			MongoDatabase dbIns = mongoInstance.getDB();
//			MongoCollection<Document> collection = dbIns.getCollection(
//					PropertiesManager.propertiesMap.get(PREFIX + Constants.INVOICE_COLLECTION_NAME.getValue()));
			MongoCollection<Document> collection = dbIns.getCollection("paymentLink");
			collection.insertOne(doc);
		
	}

	public PaymentLink update(PaymentLink paymentLink) {
		String oldUpdateDate = paymentLink.getUpdateDate();
		try {
			MongoDatabase dbIns = mongoInstance.getDB();
			MongoCollection<Document> collection = dbIns.getCollection("paymentLink");
			
			paymentLink.setUpdateDate(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
			Document doc = PaymentLinkUtilities.getDocFromPaymentLink(paymentLink);
			BasicDBObject findQuery = new BasicDBObject();
			findQuery.put(CrmFieldType.PAYMENT_LINK_ID.getName(), paymentLink.getPaymentLinkId());
			collection.replaceOne(findQuery, doc);
			logger.info("Payment Link updated successfully : " + paymentLink.getPaymentLinkId());
		} catch (Exception e) {
			logger.info("Failed to update payment link : " + paymentLink.getPaymentLinkId());
			logger.error(e.getMessage());
			paymentLink.setUpdateDate(oldUpdateDate);
		}
		return paymentLink;
	}

	public PaymentLink findByOrderId(String orderId) {
		PaymentLink paymentLink = null;
		if(StringUtils.isEmpty(orderId)) {
			logger.error("Empty order id");
			return null;
		}
		MongoDatabase dbIns = mongoInstance.getDB();
		MongoCollection<Document> collection = dbIns.getCollection("paymentLink");

		BasicDBObject findQuery = new BasicDBObject(CrmFieldType.ORDER_ID.getName(), orderId);
		logger.info("Query for find payment by order : " + findQuery);
		FindIterable<Document> output = collection.find(findQuery);
		MongoCursor<Document> cursor = output.iterator();

		Document doc = null;
		if (cursor.hasNext()) {
			doc = cursor.next();
		}
		cursor.close();
		if (doc != null && doc.getString(CrmFieldType.ORDER_ID.getName()).equals(orderId)) {
			paymentLink = PaymentLinkUtilities.getPaymentLinkFromDoc(doc);
		}
		return paymentLink;
	}

	public PaymentLink findByPaymentLinkId(String paymentLinkId) {
		PaymentLink paymentLink = null;
		MongoDatabase dbIns = mongoInstance.getDB();
		MongoCollection<Document> collection = dbIns.getCollection("paymentLink");

		BasicDBObject findQuery = new BasicDBObject();
		findQuery.put(CrmFieldType.PAYMENT_LINK_ID.getName(), paymentLinkId);
		logger.info("Query for find invoice by Id : " + findQuery);
		FindIterable<Document> output = collection.find(findQuery);
		MongoCursor<Document> cursor = output.iterator();

		Document doc = null;
		if (cursor.hasNext()) {
			doc = cursor.next();
		}
		cursor.close();
		if (doc != null && doc.getString(CrmFieldType.PAYMENT_LINK_ID.getName()).equals(paymentLinkId)) {
			paymentLink = PaymentLinkUtilities.getPaymentLinkFromDoc(doc);
		}
		return paymentLink;
	}


}
