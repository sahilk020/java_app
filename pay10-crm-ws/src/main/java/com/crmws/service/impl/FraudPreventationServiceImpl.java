package com.crmws.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.crmws.service.FraudPreventationService;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.pay10.commons.dao.FraudPreventionMongoService;
import com.pay10.commons.mongo.MongoInstance;
import com.pay10.commons.user.Merchants;
import com.pay10.commons.user.UserDao;
import com.pay10.commons.util.Constants;
import com.pay10.commons.util.FraudPreventationConfiguration;
import com.pay10.commons.util.PropertiesManager;
import com.pay10.commons.util.TransactionManager;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class FraudPreventationServiceImpl implements FraudPreventationService {

	private static final String prefix = "MONGO_DB_";

	@Autowired
	private FraudPreventionMongoService fraudPreventionMongoService;

	@Autowired
	private UserDao userDao;

	@Autowired
	private MongoInstance mongoInstance;

	@Override
	public void migrateConfigurations() {
		List<Merchants> merchants = userDao.getActiveMerchantListPgWs();
		List<String> payIds = merchants.stream().map(Merchants::getPayId).collect(Collectors.toList());
		Set<String> createdDocPayIds = new HashSet<>();
		Set<String> updatedDocPayIds = new HashSet<>();
		log.info("migrateConfigurations:: doc migration initialized for total={}", payIds.size());
		payIds.forEach(payId -> {
			FraudPreventationConfiguration config = fraudPreventionMongoService.getConfigByPayId(payId,"");

			if (config == null) {
				config = new FraudPreventationConfiguration();
				config.setId(payId);
				config.setOnIpBlocking(true);
				config.setOnEmailBlocking(true);
				config.setOnPhoneNoBlocking(true);
				config.setOnTxnAmountVelocityBlocking(true);
				config.setOnCardMaskBlocking(true);
				fraudPreventionMongoService.createConfigDoc(config,"");
				createdDocPayIds.add(payId);
			} else {
				Map<String, Boolean> updateFields = new HashMap<>();
				updateFields.put("onIpBlocking", true);
				updateFields.put("onEmailBlocking", true);
				updateFields.put("onPhoneNoBlocking", true);
				updateFields.put("onTxnAmountVelocityBlocking", true);
				updateFields.put("onCardMaskBlocking", true);
				fraudPreventionMongoService.updateConfigDoc(payId, updateFields,"");
				updatedDocPayIds.add(payId);
			}
		});
		log.info(
				"migrateConfigurations:: doc migration completed for total={}, newlyInsertDocPayIds={}, updatedDocPayIds={}",
				payIds.size(), createdDocPayIds, updatedDocPayIds);
	}

	@Override
	public void migrateRules() {
		List<Merchants> merchants = userDao.getActiveMerchantListPgWs();
		List<String> payIds = merchants.stream().map(Merchants::getPayId).collect(Collectors.toList());
		List<Document> allRules = fraudPreventionMongoService.fraudPreventionListForMigration("ALL");
		List<Document> docs = new ArrayList<>();
		payIds.forEach(payId -> {
			allRules.forEach(ruleActive -> {
				Document doc = new Document();
				doc.putAll(ruleActive);
				String id = TransactionManager.getNewTransactionId();
				doc.put("_id", id);
				doc.put("id", id);
				doc.put("payId", payId);
				docs.add(doc);
			});
		});

		MongoCollection<Document> coll = getCollection();
		coll.insertMany(docs);
		log.info("migrateRules:: doc migration completed for total={}, totalRules={}, totalNewEntry={}", payIds.size(),
				allRules.size(), payIds.size() * allRules.size());
	}

	private MongoCollection<Document> getCollection() {
		MongoDatabase dbIns = mongoInstance.getDB();
		MongoCollection<Document> coll = dbIns.getCollection(
				PropertiesManager.propertiesMap.get(prefix + Constants.FRAUD_PREVENTION_COLLECTION_NAME.getValue()));
		return coll;
	}

}
