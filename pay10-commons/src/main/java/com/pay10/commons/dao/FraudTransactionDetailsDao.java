package com.pay10.commons.dao;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.bson.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mongodb.BasicDBObject;
import com.mongodb.BasicDBObjectBuilder;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import com.pay10.commons.mongo.MongoInstance;
import com.pay10.commons.user.FraudTransactionDetails;
import com.pay10.commons.util.Constants;
import com.pay10.commons.util.PropertiesManager;
import com.pay10.commons.util.StatusType;
import com.pay10.commons.util.TransactionManager;

import bsh.This;

@Component
public class FraudTransactionDetailsDao {

	private static final Logger logger = LoggerFactory.getLogger(This.class.getName());

	@Autowired
	private MongoInstance mongoInstance;

	@Autowired
	private ObjectMapper mapper;

	@SuppressWarnings("unchecked")
	public void save(FraudTransactionDetails details) {
		MongoCollection<Document> coll = getCollection();
		Document doc = new Document();
		String id = TransactionManager.getNewTransactionId();
		details.setId(id);
		Map<String, Boolean> map = mapper.convertValue(details, HashMap.class);
		try {
			doc.put("_id", id);
			map.entrySet().forEach(entry -> {
				doc.put(entry.getKey(), entry.getValue());
			});
			coll.insertOne(doc);
		} catch (Exception e) {
			logger.error("Exception in getting Fraud Prevention ", e);
		}
	}

	private MongoCollection<Document> getCollection() {
		MongoDatabase dbIns = mongoInstance.getDB();
		return dbIns.getCollection(
				PropertiesManager.propertiesMap.get(Constants.FRAUD_TRANSACTION_COLLECTION_NAME.getValue()));
	}

	public boolean isExist(String pgRefNo) {
		MongoCollection<Document> coll = getCollection();
		BasicDBObject pgRefNoQuery = new BasicDBObject("pgRefNo", pgRefNo);
		return coll.count(pgRefNoQuery) > 0;
	}

	// provide data for report of fraud.
	public List<FraudTransactionDetails> getByFilter(String merchant, String dateRange, String status,
			String ruleType) {
		MongoCollection<Document> coll = getCollection();
		List<BasicDBObject> paramConditionLst = getFilterQuery(merchant, dateRange, status, ruleType);
		BasicDBObject finalquery = new BasicDBObject("$and", paramConditionLst);
		FindIterable<Document> output = coll.find(finalquery);
		MongoCursor<Document> cursor = output.iterator();
		List<FraudTransactionDetails> data = new ArrayList<>();
		while (cursor.hasNext()) {
			Document doc = cursor.next();
			data.add(mapper.convertValue(doc, FraudTransactionDetails.class));
		}
		return data;
	}

	private List<BasicDBObject> getFilterQuery(String merchant, String dateRange, String status, String ruleType) {
		List<BasicDBObject> paramConditionLst = new ArrayList<BasicDBObject>();
		// apply date range filter
		String[] duration = getDuration(dateRange);
		paramConditionLst.add(new BasicDBObject("date",
				BasicDBObjectBuilder.start("$gte", new SimpleDateFormat(duration[0]).toLocalizedPattern())
						.add("$lte", new SimpleDateFormat(duration[1]).toLocalizedPattern()).get()));

		if (!StringUtils.equalsIgnoreCase(status, "ALL")) {
			paramConditionLst.add(new BasicDBObject("status",
					new BasicDBObject("$in", StringUtils.split(StatusType.getInternalStatus(status), ","))));
		}

		if (!StringUtils.equalsIgnoreCase(ruleType, "ALL")) {
			paramConditionLst.add(new BasicDBObject("ruleType", ruleType));
		}

		if (!StringUtils.equalsIgnoreCase(merchant, "ALL")) {
			paramConditionLst.add(new BasicDBObject("merchantName", merchant));
		}

		return paramConditionLst;
	}

	private String[] getDuration(String dateRange) {
		String[] duration = new String[2];
		String splitdate[] = dateRange.split(" ");
		SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");
		SimpleDateFormat dateFormat1 = new SimpleDateFormat("yyyy-MM-dd");
		try {
			duration[0] = dateFormat1.format(dateFormat.parse(splitdate[0]));
			String endDate = dateFormat1.format(dateFormat.parse(splitdate[1]));
			endDate = StringUtils.join(endDate, " 23:59:59");
			duration[1] = endDate;
		} catch (Exception ex) {
			logger.warn("getDuration:: failed. message={}", ex.getMessage(), ex);
		}
		return duration;
	}
}
