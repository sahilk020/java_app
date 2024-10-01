package com.crmws.service.impl;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.apache.commons.lang3.StringUtils;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.crmws.dto.FraudTransactionDTO;
import com.crmws.dto.FraudTransactionDTO.FraudAction;
import com.crmws.service.FraudTxnService;
import com.mongodb.BasicDBObject;
import com.mongodb.BasicDBObjectBuilder;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Sorts;
import com.pay10.commons.api.EmailControllerServiceProvider;
import com.pay10.commons.mongo.MongoInstance;
import com.pay10.commons.user.UserDao;
import com.pay10.commons.util.PaymentType;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class FraudTxnServiceImpl implements FraudTxnService {

	@Autowired
	private MongoInstance mongoInstance;

	@Autowired
	private EmailControllerServiceProvider emailControllerServiceProvider;

	@Autowired
	private UserDao userDao;

	@Override
	public Optional<List<FraudTransactionDTO>> getFraudCapturedTransaction() {
		return fetchFraudTransaction();
	}

	public Optional<List<FraudTransactionDTO>> fetchFraudTransaction() {
		ArrayList<FraudTransactionDTO> fraudCapturedTxnLst = new ArrayList<FraudTransactionDTO>();
		List<BasicDBObject> paramConditionLst = new ArrayList<BasicDBObject>();
		List<String> fraudTyeps = new ArrayList<>();
		fraudTyeps.add("BLOCK_TICKET_LIMIT");
		fraudTyeps.add("BLOCK_TRANSACTION_LIMIT");
		fraudTyeps.add("BLOCK_MOP_LIMIT");

		BasicDBObject dateQuery = new BasicDBObject();
		String fromDate = null;
		String currentDate = null;

		DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		DateFormat dateFormat1 = new SimpleDateFormat("yyyy-MM-dd");
		Calendar cal = Calendar.getInstance();
		currentDate = dateFormat.format(cal.getTime());
		fromDate = dateFormat1.format(cal.getTime());
		fromDate += " 00:00:00";
		currentDate = dateFormat.format(cal.getTime());

		dateQuery.put("date", BasicDBObjectBuilder.start("$gte", new SimpleDateFormat(fromDate).toLocalizedPattern())
				.add("$lte", new SimpleDateFormat(currentDate).toLocalizedPattern()).get());

		paramConditionLst.add(new BasicDBObject("fraudType", new BasicDBObject("$in", fraudTyeps)));

		if (!dateQuery.isEmpty()) {
			paramConditionLst.add(dateQuery);
		}

		BasicDBObject finalquery = new BasicDBObject("$and", paramConditionLst);

		log.info("findLastTransaction, finalquery : " + finalquery.toString());
		MongoDatabase dbIns = mongoInstance.getDB();
		MongoCollection<Document> collection = dbIns.getCollection("fraudTransactions");

		FindIterable<Document> output = collection.find(finalquery).sort(Sorts.descending("CREATE_DATE"));
		log.info("findLastTransaction : " + output);
		MongoCursor<Document> result = output.iterator();

		while (result.hasNext()) {
			Document document = (Document) result.next();
			if (StringUtils.isNotBlank(document.getString("ruleType"))) {
				if (document.getString("ruleType").equalsIgnoreCase("Ignore")
						|| document.getString("isIgnore").equalsIgnoreCase("Y")) {
					continue;
				}

			}
			FraudTransactionDTO fraudCapturedTxnInfo = documentToFraudCapturedTxnObj(document);
			fraudCapturedTxnLst.add(fraudCapturedTxnInfo);
		}
		return Optional.ofNullable(fraudCapturedTxnLst);
	}

	public FraudTransactionDTO documentToFraudCapturedTxnObj(Document doc) {

		FraudTransactionDTO fraudCapturedTxn = new FraudTransactionDTO();
		FraudAction fraudAction = new FraudAction();

		if (StringUtils.isNotBlank(doc.getString("id"))) {
			fraudCapturedTxn.setId(doc.getString("id"));
			fraudCapturedTxn.setSubject("Suspected Fraud");
		}
		if (StringUtils.isNotBlank(doc.getString("fraudType"))) {
			fraudCapturedTxn.setFraudType(doc.getString("fraudType"));
		}
		if (StringUtils.isNotBlank(doc.getString("merchantName"))) {
			fraudCapturedTxn.setMerchantName(doc.getString("merchantName"));
		}
		if (StringUtils.isNotBlank(doc.getString("orderId"))) {
			fraudCapturedTxn.setOrderId(doc.getString("orderId"));
		}

		if (StringUtils.isNotBlank(doc.getString("txnId"))) {
			fraudCapturedTxn.setTxnId(doc.getString("txnId"));
		}

		if (StringUtils.isNotBlank(doc.getString("paymentType"))) {
			fraudCapturedTxn.setPaymentType(doc.getString("paymentType"));
		}
		if (StringUtils.isNotBlank(doc.getString("blockType"))) {
			fraudCapturedTxn.setBlockType(doc.getString("blockType"));
		}
		if (StringUtils.isNotBlank(doc.getString("mopType"))) {
			fraudCapturedTxn.setMopType(doc.getString("mopType"));
		}
		if (doc.getDouble("baseAmount") != null) {
			fraudCapturedTxn.setBaseAmount(doc.getDouble("baseAmount"));
		}

		if (StringUtils.isNotBlank(doc.getString("txnType"))) {
			fraudCapturedTxn.setTxnType(doc.getString("txnType"));
		}

		if (StringUtils.isNotBlank(doc.getString("date"))) {
			fraudCapturedTxn.setDate(doc.getString("date"));
		}

		if (StringUtils.isNotBlank(doc.getString("isBlock"))) {
			if (doc.getString("isBlock").equalsIgnoreCase("Y")) {
				fraudAction.setBlock(true);
			}
		}
		if (StringUtils.isNotBlank(doc.getString("isIgnore"))) {
			if (doc.getString("isIgnore").equalsIgnoreCase("Y")) {
				fraudAction.setIgnore(true);
			}
		}
		if (StringUtils.isNotBlank(doc.getString("isNotify"))) {
			if (doc.getString("isNotify").equalsIgnoreCase("Y")) {
				fraudAction.setNotifyMerchant(true);
			}
		}

		/*
		 * if (StringUtils.isNotBlank(doc.getString("ruleType"))) { if
		 * (doc.getString("ruleType").equalsIgnoreCase("Ignore")) {
		 * fraudAction.setIgnore(true); } if
		 * (doc.getString("ruleType").equalsIgnoreCase("Block")) {
		 * fraudAction.setBlock(true); } if
		 * (doc.getString("ruleType").equalsIgnoreCase("NotifyMerchant")) {
		 * fraudAction.setNotifyMerchant(true); } }
		 */
		fraudCapturedTxn.setAction(fraudAction);

		return fraudCapturedTxn;
	}

	@Override
	public boolean updateFraudTransactionAction(Map<String, String> actionRequest) {
		log.info("Update FraudTransaction Action = {} ", actionRequest);

		MongoDatabase dbIns = mongoInstance.getDB();
		MongoCollection<Document> coll = dbIns.getCollection("fraudTransactions");

		List<BasicDBObject> dbObjList = new ArrayList<BasicDBObject>();
		// dbObjList.add(new BasicDBObject(FieldType.PG_REF_NUM.getName(), pgRefNum));
		dbObjList.add(new BasicDBObject("orderId", actionRequest.get("orderId")));
		dbObjList.add(new BasicDBObject("fraudType", actionRequest.get("fraudType")));
		dbObjList.add(new BasicDBObject("paymentType", actionRequest.get("paymentType")));
		dbObjList.add(new BasicDBObject("merchantName", actionRequest.get("merchantName")));
		dbObjList.add(new BasicDBObject("id", actionRequest.get("id")));

		BasicDBObject andQuery = new BasicDBObject("$and", dbObjList);
		log.info("updateFraudTransactionAction Query = {} ", andQuery);
		FindIterable<Document> cursor = coll.find(andQuery);

		if (cursor.iterator().hasNext()) {

			MongoCollection<Document> txnStatusColl = dbIns.getCollection("fraudTransactions");

			BasicDBObject searchQuery = andQuery;
			BasicDBObject updateFields = new BasicDBObject();

			if (actionRequest.containsKey("actionType") && actionRequest.get("actionType") != null) {
				if (actionRequest.get("actionType").equalsIgnoreCase("IGNORE")) {
					updateFields.put("ruleType", "IGNORE");
					updateFields.put("isIgnore", "Y");
				}
				if (actionRequest.get("actionType").equalsIgnoreCase("BLOCK")) {
					updateFields.put("ruleType", "BLOCK");
					updateFields.put("isBlock", "Y");
				}
				if (actionRequest.get("actionType").equalsIgnoreCase("NOTIFYMERCHANT")) {
					updateFields.put("ruleType", "NOTIFYMERCHANT");
					updateFields.put("isNotify", "Y");
				}

			}
			txnStatusColl.updateOne(searchQuery, new BasicDBObject("$set", updateFields));
			return true;
		}
		return false;
	}

	@Override
	public void notifyToMerchant(Map<String, String> mailRequest) {
		emailControllerServiceProvider.sendFraudRuleEmail(mailRequest);
	}

	@Override
	public void updateFraudTransactionRule(Map<String, String> actionRequest) {
		log.info("Update FraudTransaction Rule = {} ", actionRequest);

		MongoDatabase dbIns = mongoInstance.getDB();
		MongoCollection<Document> coll = dbIns.getCollection("fraudPreventionCollection");

		List<BasicDBObject> dbObjList = new ArrayList<BasicDBObject>();
		// dbObjList.add(new BasicDBObject(FieldType.PG_REF_NUM.getName(), pgRefNum));
		dbObjList.add(new BasicDBObject("fraudType", actionRequest.get("fraudType")));
		dbObjList.add(new BasicDBObject("payId", userDao.getPayIdByMerchantName(actionRequest.get("merchantName"))));
		dbObjList.add(new BasicDBObject("status", "Active"));
		
		if(StringUtils.isNotBlank(actionRequest.get("paymentType"))
				&& actionRequest.get("fraudType").equalsIgnoreCase("BLOCK_MOP_LIMIT")) {
			dbObjList.add(new BasicDBObject("paymentType", PaymentType.getInstance(actionRequest.get("paymentType")).getCode()));
		}
		
		BasicDBObject andQuery = new BasicDBObject("$and", dbObjList);
		log.info("updateFraudTransactionRule Query = {} ", andQuery);
		FindIterable<Document> cursor = coll.find(andQuery);

		if (cursor.iterator().hasNext()) {

			MongoCollection<Document> txnStatusColl = dbIns.getCollection("fraudPreventionCollection");

			BasicDBObject searchQuery = andQuery;
			BasicDBObject updateFields = new BasicDBObject();

			updateFields.put("monitoringType", "BLOCK");
			if (actionRequest.containsKey("blockType") && actionRequest.get("blockType") != null) {
				if (actionRequest.get("blockType").equalsIgnoreCase("Daily")) {
					updateFields.put("blockDailyType", "Y");
				}
				if (actionRequest.get("blockType").equalsIgnoreCase("Weekly")) {
					updateFields.put("blockWeeklyType", "Y");
				}
				if (actionRequest.get("blockType").equalsIgnoreCase("Monthly")) {
					updateFields.put("blockMonthlyType", "Y");
				}

			}
			txnStatusColl.updateOne(searchQuery, new BasicDBObject("$set", updateFields));
		}

	}

}
