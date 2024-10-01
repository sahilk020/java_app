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
import org.springframework.stereotype.Service;

import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import com.mongodb.client.AggregateIterable;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import com.pay10.commons.mongo.MongoInstance;
import com.pay10.commons.user.BeneficiaryAccounts;
import com.pay10.commons.util.AccountStatus;
import com.pay10.commons.util.BeneficiaryAccountsUtilities;
import com.pay10.commons.util.Constants;
import com.pay10.commons.util.DateCreater;
import com.pay10.commons.util.FieldType;
import com.pay10.commons.util.NodalPaymentTypes;
import com.pay10.commons.util.PropertiesManager;

@Service
public class BeneficiaryAccountsDao {

	@Autowired
	private MongoInstance mongoInstance;
	private static final String PREFIX = "MONGO_DB_";
	private static Logger logger = LoggerFactory.getLogger(BeneficiaryAccountsDao.class.getName());

	public boolean create(BeneficiaryAccounts beneficiaryAccounts) {

		if (beneficiaryAccounts == null) {
			logger.info("Invalid data : " + beneficiaryAccounts);
			return false;
		}
		try {
			Document doc = BeneficiaryAccountsUtilities.getDocFromBeneficiary(beneficiaryAccounts);
			Date dNow = new Date();
			String dateNow = DateCreater.formatDateForDb(dNow);
			doc.put(FieldType.DATE_INDEX.getName(), dateNow.substring(0, 10).replace("-", ""));
			doc.put(FieldType.INSERTION_DATE.getName(), dNow);
			MongoDatabase dbIns = mongoInstance.getDB();
			MongoCollection<Document> collection = dbIns.getCollection(PropertiesManager.propertiesMap
					.get(PREFIX + Constants.BENEFICIARY_ACCOUNTS_COLLECTION_NAME.getValue()));
			collection.insertOne(doc);
		} catch (Exception e) {
			logger.error("Failed to create beneficiary.");
			logger.error(e.getMessage());
			e.printStackTrace();
			return false;
		}
		return true;
	}

	public BeneficiaryAccounts update(BeneficiaryAccounts beneficiaryAccounts) {
		String oldUpdateDate = beneficiaryAccounts.getUpdatedDate();
		try {
			MongoDatabase dbIns = mongoInstance.getDB();
			MongoCollection<Document> collection = dbIns.getCollection(PropertiesManager.propertiesMap
					.get(PREFIX + Constants.BENEFICIARY_ACCOUNTS_COLLECTION_NAME.getValue()));

			beneficiaryAccounts.setUpdatedDate(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
			Document doc = BeneficiaryAccountsUtilities.getDocFromBeneficiary(beneficiaryAccounts);
			BasicDBObject findQuery = new BasicDBObject();
			findQuery.put(FieldType.BENE_ID.getName(), beneficiaryAccounts.getId());
			collection.replaceOne(findQuery, doc);
			logger.info("Beneficiary updated successfully : " + beneficiaryAccounts.getId());
		} catch (Exception e) {
			logger.info("Failed to update beneficiary : " + beneficiaryAccounts.getId());
			logger.error(e.getMessage());
			beneficiaryAccounts.setUpdatedDate(oldUpdateDate);
		}
		return beneficiaryAccounts;
	}

	public BeneficiaryAccounts updateAfterEditBeneficiary(BeneficiaryAccounts beneficiaryAccounts) {
		String oldUpdateDate = beneficiaryAccounts.getUpdatedDate();
		try {
			MongoDatabase dbIns = mongoInstance.getDB();
			MongoCollection<Document> collection = dbIns.getCollection(PropertiesManager.propertiesMap
					.get(PREFIX + Constants.BENEFICIARY_ACCOUNTS_COLLECTION_NAME.getValue()));

			beneficiaryAccounts.setUpdatedDate(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
			Document doc = BeneficiaryAccountsUtilities.getDocFromBeneficiary(beneficiaryAccounts);
			BasicDBObject findQuery = new BasicDBObject();
			findQuery.put(FieldType.BENE_ID.getName(), beneficiaryAccounts.getId());
			collection.replaceOne(findQuery, doc);
			logger.info("Beneficiary updated successfully : " + beneficiaryAccounts.getId());
		} catch (Exception e) {
			logger.info("Failed to update beneficiary : " + beneficiaryAccounts.getId());
			logger.error(e.getMessage());
			beneficiaryAccounts.setUpdatedDate(oldUpdateDate);
		}
		return beneficiaryAccounts;
	}

	public List<BeneficiaryAccounts> getAllBeneficiaryAccountsList() {
		List<BeneficiaryAccounts> list = new ArrayList<>();

		MongoDatabase dbIns = mongoInstance.getDB();
		MongoCollection<Document> collection = dbIns.getCollection(PropertiesManager.propertiesMap
				.get(PREFIX + Constants.BENEFICIARY_ACCOUNTS_COLLECTION_NAME.getValue()));

		BasicDBObject sortQuery = new BasicDBObject(FieldType.CREATE_DATE.getName(), -1);

		FindIterable<Document> output = collection.find().sort(sortQuery);
		MongoCursor<Document> cursor = output.iterator();

		while (cursor.hasNext()) {
			list.add(BeneficiaryAccountsUtilities.getBeneficiaryFromDoc(cursor.next()));
		}
		cursor.close();
		return list;
	}

	public List<BeneficiaryAccounts> getBeneficiaryAccountsListByAcquirer(String acquirer, String merchantPayId,
			String beneficiaryCd, String beneName, String paymentType, String statusType) {
		List<BeneficiaryAccounts> list = new ArrayList<>();

		MongoDatabase dbIns = mongoInstance.getDB();
		MongoCollection<Document> collection = dbIns.getCollection(PropertiesManager.propertiesMap
				.get(PREFIX + Constants.BENEFICIARY_ACCOUNTS_COLLECTION_NAME.getValue()));

		BasicDBObject findQuery = new BasicDBObject();
		BasicDBObject sortQuery = new BasicDBObject(FieldType.CREATE_DATE.getName(), -1);

		if (!merchantPayId.equalsIgnoreCase("ALL")) {
			findQuery.put(FieldType.PAY_ID.getName(), merchantPayId);
		}
		if (!beneficiaryCd.equalsIgnoreCase("ALL")) {
			findQuery.put(FieldType.BENE_MERCHANT_PROVIDED_ID.getName(), beneficiaryCd);
		}
		if (!beneName.equalsIgnoreCase("ALL")) {
			findQuery.put(FieldType.BENE_MERCHANT_PROVIDED_NAME.getName(), beneName);
		}
		if (!paymentType.equalsIgnoreCase("ALL")) {
			findQuery.put(FieldType.PAYMENT_TYPE.getName(), paymentType);
		}
		if (!acquirer.equalsIgnoreCase("ALL")) {
			findQuery.put(FieldType.ACQUIRER_TYPE.getName(), acquirer);
		}
		if (!statusType.equalsIgnoreCase("ALL")) {
			findQuery.put(FieldType.BENE_STATUS.getName(), statusType);
		}

		FindIterable<Document> output = collection.find(findQuery).sort(sortQuery);
		MongoCursor<Document> cursor = output.iterator();

		try {
			while (cursor.hasNext()) {
				Document doc = cursor.next();
				BeneficiaryAccounts BeneficiaryAccount = BeneficiaryAccountsUtilities.getBeneficiaryFromDoc(doc);

				list.add(BeneficiaryAccount);
			}
		} finally {
			cursor.close();
		}
		return list;
	}

	public List<BeneficiaryAccounts> getAllActiveBeneficiaryAccountsList() {
		List<BeneficiaryAccounts> list = new ArrayList<>();

		MongoDatabase dbIns = mongoInstance.getDB();
		MongoCollection<Document> collection = dbIns.getCollection(PropertiesManager.propertiesMap
				.get(PREFIX + Constants.BENEFICIARY_ACCOUNTS_COLLECTION_NAME.getValue()));

		BasicDBObject findQuery = new BasicDBObject(FieldType.BENE_STATUS.getName(), AccountStatus.ACTIVE.getName());
		BasicDBObject sortQuery = new BasicDBObject(FieldType.CREATE_DATE.getName(), -1);
		FindIterable<Document> output = collection.find(findQuery).sort(sortQuery);
		MongoCursor<Document> cursor = output.iterator();

		while (cursor.hasNext()) {
			list.add(BeneficiaryAccountsUtilities.getBeneficiaryFromDoc(cursor.next()));
		}
		cursor.close();
		return list;
	}

	public List<BeneficiaryAccounts> getAllInProcessBeneficiaryAccountsList() {
		List<BeneficiaryAccounts> list = new ArrayList<>();

		MongoDatabase dbIns = mongoInstance.getDB();
		MongoCollection<Document> collection = dbIns.getCollection(PropertiesManager.propertiesMap
				.get(PREFIX + Constants.BENEFICIARY_ACCOUNTS_COLLECTION_NAME.getValue()));

		BasicDBObject findQuery = new BasicDBObject(FieldType.BENE_STATUS.getName(),
				AccountStatus.IN_PROCESS.getName());
		BasicDBObject sortQuery = new BasicDBObject(FieldType.CREATE_DATE.getName(), -1);
		FindIterable<Document> output = collection.find(findQuery).sort(sortQuery);
		MongoCursor<Document> cursor = output.iterator();

		while (cursor.hasNext()) {
			list.add(BeneficiaryAccountsUtilities.getBeneficiaryFromDoc(cursor.next()));
		}
		cursor.close();
		return list;
	}

	public List<BeneficiaryAccounts> getAllInPendingBeneficiaryAccountsList() {
		List<BeneficiaryAccounts> list = new ArrayList<>();

		MongoDatabase dbIns = mongoInstance.getDB();
		MongoCollection<Document> collection = dbIns.getCollection(PropertiesManager.propertiesMap
				.get(PREFIX + Constants.BENEFICIARY_ACCOUNTS_COLLECTION_NAME.getValue()));

		BasicDBObject findQuery = new BasicDBObject(FieldType.BENE_STATUS.getName(), AccountStatus.PENDING.getName());
		BasicDBObject sortQuery = new BasicDBObject(FieldType.CREATE_DATE.getName(), -1);
		FindIterable<Document> output = collection.find(findQuery).sort(sortQuery);
		MongoCursor<Document> cursor = output.iterator();

		while (cursor.hasNext()) {
			list.add(BeneficiaryAccountsUtilities.getBeneficiaryFromDoc(cursor.next()));
		}
		cursor.close();
		return list;
	}

	public BeneficiaryAccounts findById_EnableAndDisable(String merchantProvidedId, String paymentType, String acquirer,
			String merchantBusinessName) {

		BeneficiaryAccounts transactionList = null;

		BeneficiaryAccounts beneficiaryAccounts = null;
		MongoDatabase dbIns = mongoInstance.getDB();
		MongoCollection<Document> collection = dbIns.getCollection(PropertiesManager.propertiesMap
				.get(PREFIX + Constants.BENEFICIARY_ACCOUNTS_COLLECTION_NAME.getValue()));

		DBObject active = new BasicDBObject(FieldType.BENE_STATUS.getName(), AccountStatus.ACTIVE.getCode());
		DBObject inactive = new BasicDBObject(FieldType.BENE_STATUS.getName(), AccountStatus.INACTIVE.getCode());

		BasicDBList orQuery = new BasicDBList();
		orQuery.add(active);
		orQuery.add(inactive);

		BasicDBObject findQuery = new BasicDBObject();
		findQuery.put(FieldType.BENE_MERCHANT_PROVIDED_ID.getName(), merchantProvidedId);
		findQuery.put(FieldType.ACQUIRER_TYPE.getName(), acquirer);
		findQuery.put(FieldType.BENE_MERCHANT_BUSINESS_NAME.getName(), merchantBusinessName);
		findQuery.put(FieldType.PAYMENT_TYPE.getName(), paymentType);
		findQuery.put("$or", orQuery);
		FindIterable<Document> output = collection.find(findQuery);
		MongoCursor<Document> cursor = output.iterator();
		try {
			if (cursor.hasNext()) {
				beneficiaryAccounts = BeneficiaryAccountsUtilities.getBeneficiaryFromDoc(cursor.next());
			}
		} finally {
			cursor.close();
		}
		return beneficiaryAccounts;
	}

	// Find by internalId and merchant provided bene ID
	public BeneficiaryAccounts findById_Enable(String merchantProvidedId, String acquirer,
			String merchantBusinessName) {

		BeneficiaryAccounts beneficiaryAccounts = null;

		MongoDatabase dbIns = mongoInstance.getDB();
		MongoCollection<Document> collection = dbIns.getCollection(PropertiesManager.propertiesMap
				.get(PREFIX + Constants.BENEFICIARY_ACCOUNTS_COLLECTION_NAME.getValue()));

		BasicDBObject findQuery = new BasicDBObject();
		findQuery.put(FieldType.BENE_MERCHANT_PROVIDED_ID.getName(), merchantProvidedId);
		findQuery.put(FieldType.ACQUIRER_TYPE.getName(), acquirer);
		findQuery.put(FieldType.BENE_MERCHANT_BUSINESS_NAME.getName(), merchantBusinessName);
		findQuery.put(FieldType.BENE_STATUS.getName(), AccountStatus.INACTIVE.getCode());
		FindIterable<Document> output = collection.find(findQuery);
		MongoCursor<Document> cursor = output.iterator();

		if (cursor.hasNext()) {
			beneficiaryAccounts = BeneficiaryAccountsUtilities.getBeneficiaryFromDoc(cursor.next());
		}
		cursor.close();
		return beneficiaryAccounts;

	}

	public BeneficiaryAccounts findById_Disable(String merchantProvidedId, String acquirer,
			String merchantBusinessName) {

		BeneficiaryAccounts beneficiaryAccounts = null;

		MongoDatabase dbIns = mongoInstance.getDB();
		MongoCollection<Document> collection = dbIns.getCollection(PropertiesManager.propertiesMap
				.get(PREFIX + Constants.BENEFICIARY_ACCOUNTS_COLLECTION_NAME.getValue()));

		BasicDBObject findQuery = new BasicDBObject();
		findQuery.put(FieldType.BENE_MERCHANT_PROVIDED_ID.getName(), merchantProvidedId);
		findQuery.put(FieldType.ACQUIRER_TYPE.getName(), acquirer);
		findQuery.put(FieldType.BENE_MERCHANT_BUSINESS_NAME.getName(), merchantBusinessName);
		findQuery.put(FieldType.BENE_STATUS.getName(), AccountStatus.ACTIVE.getCode());
		FindIterable<Document> output = collection.find(findQuery);
		MongoCursor<Document> cursor = output.iterator();

		if (cursor.hasNext()) {
			beneficiaryAccounts = BeneficiaryAccountsUtilities.getBeneficiaryFromDoc(cursor.next());
		}
		cursor.close();
		return beneficiaryAccounts;

	}

	public List<BeneficiaryAccounts> findByBeneficiaryCdAndStatusActive(String beneficiaryCd, String payId) {

		List<BeneficiaryAccounts> transactionList = new ArrayList<BeneficiaryAccounts>();

		BeneficiaryAccounts beneficiaryAccounts = null;
		MongoDatabase dbIns = mongoInstance.getDB();
		MongoCollection<Document> collection = dbIns.getCollection(PropertiesManager.propertiesMap
				.get(PREFIX + Constants.BENEFICIARY_ACCOUNTS_COLLECTION_NAME.getValue()));

		BasicDBObject findQuery = new BasicDBObject();
		findQuery.put(FieldType.BENE_MERCHANT_PROVIDED_ID.getName(), beneficiaryCd);
		findQuery.put(FieldType.PAY_ID.getName(), payId);
		findQuery.put(FieldType.BENE_STATUS.getName(), AccountStatus.ACTIVE.getCode());
		FindIterable<Document> output = collection.find(findQuery);
		MongoCursor<Document> cursor = output.iterator();
		try {
			while (cursor.hasNext()) {
				transactionList.add(BeneficiaryAccountsUtilities.getBeneficiaryFromDoc(cursor.next()));

			}
		} finally {
			cursor.close();
		}
		return transactionList;
	}

	public List<BeneficiaryAccounts> findByBeneficiaryCdAndStatusInActive(String beneficiaryCd, String payId) {

		List<BeneficiaryAccounts> transactionList = new ArrayList<BeneficiaryAccounts>();

		BeneficiaryAccounts beneficiaryAccounts = null;
		MongoDatabase dbIns = mongoInstance.getDB();
		MongoCollection<Document> collection = dbIns.getCollection(PropertiesManager.propertiesMap
				.get(PREFIX + Constants.BENEFICIARY_ACCOUNTS_COLLECTION_NAME.getValue()));

		BasicDBObject findQuery = new BasicDBObject();
		findQuery.put(FieldType.BENE_MERCHANT_PROVIDED_ID.getName(), beneficiaryCd);
		findQuery.put(FieldType.PAY_ID.getName(), payId);
		findQuery.put(FieldType.BENE_STATUS.getName(), AccountStatus.INACTIVE.getCode());
		FindIterable<Document> output = collection.find(findQuery);
		MongoCursor<Document> cursor = output.iterator();
		try {
			while (cursor.hasNext()) {
				transactionList.add(BeneficiaryAccountsUtilities.getBeneficiaryFromDoc(cursor.next()));

			}
		} finally {
			cursor.close();
		}
		return transactionList;
	}

	public List<BeneficiaryAccounts> findByBeneAccNum(String beneAccnumb, String payId) {

		List<BeneficiaryAccounts> transactionList = new ArrayList<BeneficiaryAccounts>();

		MongoDatabase dbIns = mongoInstance.getDB();
		MongoCollection<Document> collection = dbIns.getCollection(PropertiesManager.propertiesMap
				.get(PREFIX + Constants.BENEFICIARY_ACCOUNTS_COLLECTION_NAME.getValue()));

		DBObject active = new BasicDBObject(FieldType.BENE_STATUS.getName(), AccountStatus.ACTIVE.getCode());
		DBObject inactive = new BasicDBObject(FieldType.BENE_STATUS.getName(), AccountStatus.INACTIVE.getCode());

		BasicDBList orQuery = new BasicDBList();
		orQuery.add(active);
		orQuery.add(inactive);

		BasicDBObject findQuery = new BasicDBObject();
		findQuery.put(FieldType.BENE_ACCOUNT_NO.getName(), beneAccnumb);
		findQuery.put(FieldType.PAY_ID.getName(), payId);
		findQuery.put("$or", orQuery);
		FindIterable<Document> output = collection.find(findQuery);
		MongoCursor<Document> cursor = output.iterator();
		try {
			while (cursor.hasNext()) {
				transactionList.add(BeneficiaryAccountsUtilities.getBeneficiaryFromDoc(cursor.next()));
			}
		} finally {
			cursor.close();
		}
		return transactionList;
	}

	public List<BeneficiaryAccounts> findByMerchantPvdCdAndStatusAndPaymentTypeAndPayId(String merchantProvidedCd,
			String payId, String paymentType, String status) {

		List<BeneficiaryAccounts> list = new ArrayList<>();
		MongoDatabase dbIns = mongoInstance.getDB();
		MongoCollection<Document> collection = dbIns.getCollection(PropertiesManager.propertiesMap
				.get(PREFIX + Constants.BENEFICIARY_ACCOUNTS_COLLECTION_NAME.getValue()));
		BasicDBObject findQuery = new BasicDBObject();
		findQuery.put(FieldType.BENE_MERCHANT_PROVIDED_ID.getName(), merchantProvidedCd);
		if (!StringUtils.isEmpty(payId) && !(payId.equalsIgnoreCase("ALL"))) {
			findQuery.put(FieldType.PAY_ID.getName(), payId);
		}
		if (!StringUtils.isEmpty(paymentType) && !(paymentType.equalsIgnoreCase("ALL"))) {
			findQuery.put(FieldType.PAYMENT_TYPE.getName(),
					NodalPaymentTypes.getInstancefromCode(paymentType).getCode());
		}
		if (!StringUtils.isEmpty(status) && !(status.equalsIgnoreCase("ALL"))) {
			findQuery.put(FieldType.BENE_STATUS.getName(), status);
		}
		logger.info("findByMerchantPvdCdAndStatusAndPaymentTypeAndPayId() query:" + findQuery);
		FindIterable<Document> output = collection.find(findQuery);
		MongoCursor<Document> cursor = output.iterator();

		while (cursor.hasNext()) {
			list.add(BeneficiaryAccountsUtilities.getBeneficiaryFromDoc(cursor.next()));
		}
		cursor.close();
		return list;

	}

	public List<BeneficiaryAccounts> findByBeneficiaryCdAndStatusAndPaymentTypeAndPayId(String beneficiaryCd,
			String payId, String paymentType, String status) {

		List<BeneficiaryAccounts> list = new ArrayList<>();
		MongoDatabase dbIns = mongoInstance.getDB();
		MongoCollection<Document> collection = dbIns.getCollection(PropertiesManager.propertiesMap
				.get(PREFIX + Constants.BENEFICIARY_ACCOUNTS_COLLECTION_NAME.getValue()));
		BasicDBObject findQuery = new BasicDBObject();
		findQuery.put(FieldType.BENEFICIARY_CD.getName(), beneficiaryCd);
		if (!StringUtils.isEmpty(payId) && !(payId.equalsIgnoreCase("ALL"))) {
			findQuery.put(FieldType.PAY_ID.getName(), payId);
		}
		if (!StringUtils.isEmpty(paymentType) && !(paymentType.equalsIgnoreCase("ALL"))) {
			findQuery.put(FieldType.PAYMENT_TYPE.getName(),
					NodalPaymentTypes.getInstancefromName(paymentType).getCode());
		}
		if (!StringUtils.isEmpty(status) && !(status.equalsIgnoreCase("ALL"))) {
			findQuery.put(FieldType.BENE_STATUS.getName(), status);
		}
		logger.info("findByBeneficiaryCdAndStatusAndPaymentTypeAndPayId() query:" + findQuery);
		FindIterable<Document> output = collection.find(findQuery);
		MongoCursor<Document> cursor = output.iterator();

		while (cursor.hasNext()) {
			list.add(BeneficiaryAccountsUtilities.getBeneficiaryFromDoc(cursor.next()));
		}
		cursor.close();
		return list;

	}

	public Map<String, Object> getBeneficiaryListGroupedBeneficiaryCode(String acquirer, String merchantPayId,
			String beneficiaryCd, String beneName, String paymentType, String requestedBy, String mobile, String email,
			String statusType, int start, int length, String beneficiaryType) {
		Map<String, Object> map = new HashMap<>();
		MongoDatabase dbIns = mongoInstance.getDB();
		MongoCollection<Document> collection = dbIns.getCollection(PropertiesManager.propertiesMap
				.get(PREFIX + Constants.BENEFICIARY_ACCOUNTS_COLLECTION_NAME.getValue()));

		BasicDBObject findQuery = new BasicDBObject();
		BasicDBObject sortQuery = new BasicDBObject(FieldType.CREATE_DATE.getName(), 1);

		if (!(StringUtils.isEmpty(merchantPayId)) && !merchantPayId.equalsIgnoreCase("ALL")) {
			findQuery.put(FieldType.PAY_ID.getName(), merchantPayId);
		}
		if (!(StringUtils.isEmpty(beneficiaryCd)) && !beneficiaryCd.equalsIgnoreCase("ALL")) {
			findQuery.put(FieldType.BENE_MERCHANT_PROVIDED_ID.getName(), beneficiaryCd);
		}
		if (!(StringUtils.isEmpty(beneName)) && !beneName.equalsIgnoreCase("ALL")) {
			findQuery.put(FieldType.BENE_MERCHANT_PROVIDED_NAME.getName(), beneName);
		}
		if (!(StringUtils.isEmpty(paymentType)) && !paymentType.equalsIgnoreCase("ALL")) {
			findQuery.put(FieldType.PAYMENT_TYPE.getName(), paymentType);
		}
		if (!(StringUtils.isEmpty(acquirer)) && !acquirer.equalsIgnoreCase("ALL")) {
			findQuery.put(FieldType.ACQUIRER_TYPE.getName(), acquirer);
		}
		if (!(StringUtils.isEmpty(statusType)) && !statusType.equalsIgnoreCase("ALL")) {
			findQuery.put(FieldType.BENE_STATUS.getName(), statusType);
		}

		if (!(StringUtils.isEmpty(mobile)) && !mobile.equalsIgnoreCase("ALL")) {
			findQuery.put(FieldType.BENE_MOBILE_NUMBER.getName(), mobile);
		}

		if (!(StringUtils.isEmpty(email)) && !email.equalsIgnoreCase("ALL")) {
			findQuery.put(FieldType.BENE_EMAIL_ID.getName(), email);
		}

		if (!(StringUtils.isEmpty(requestedBy)) && !requestedBy.equalsIgnoreCase("ALL")) {
			findQuery.put(FieldType.BENE_REQUESTED_BY.getName(), requestedBy);
		}
		
		if (!(StringUtils.isEmpty(beneficiaryType)) && !beneficiaryType.equalsIgnoreCase("ALL")) {
			findQuery.put(FieldType.BENE_TYPE.getName(), beneficiaryType);
		}

		Document firstGroup = new Document("_id", new Document("BENEFICIARY_CD", "$BENEFICIARY_CD"));
		BasicDBObject firstGroupObject = new BasicDBObject(firstGroup);
		BasicDBObject match = new BasicDBObject("$match", findQuery);
		BasicDBObject sort = new BasicDBObject("$sort", sortQuery);
		BasicDBObject group = new BasicDBObject("$group", firstGroupObject);
		BasicDBObject skip = new BasicDBObject("$skip", start);
		BasicDBObject limit = new BasicDBObject("$limit", length);
		BasicDBObject count = new BasicDBObject("$count", "count");
		List<BasicDBObject> pipeline = Arrays.asList(match, group, sort, skip, limit);
		List<BasicDBObject> countPipeLine = Arrays.asList(match, group, count);
		logger.info("pipeLine : " + pipeline);
		logger.info("count pipeLine : " + countPipeLine);
		AggregateIterable<Document> output = collection.aggregate(pipeline);
		AggregateIterable<Document> countOutput = collection.aggregate(countPipeLine);
		MongoCursor<Document> cursor = output.iterator();
		MongoCursor<Document> countCursor = countOutput.iterator();
		output.allowDiskUse(true);
		countOutput.allowDiskUse(true);
		int totalRecords = 0;
		if (countCursor.hasNext()) {
			Document doc = countCursor.next();
			totalRecords = doc.getInteger("count");
		}
		countCursor.close();
		List<String> beneCodeList = new ArrayList<>();
		while (cursor.hasNext()) {
			Document doc = cursor.next();
			Document obj = (Document) doc.get("_id");
			beneCodeList.add(obj.get(FieldType.BENEFICIARY_CD.getName()).toString());
		}
		cursor.close();

		map.put("list", beneCodeList);
		map.put("count", totalRecords);
		return map;
	}

	public List<BeneficiaryAccounts> findByMerchantProvidedCodePayIdPaymentTypeStatus(String merchantProvidedCode,
			String payId, List<String> nodalPaymentTypesList, List<AccountStatus> accountStatusList) {

		List<BeneficiaryAccounts> list = new ArrayList<>();

		List<BasicDBObject> paymentTypesList = new ArrayList<>();
		for (String pt : nodalPaymentTypesList) {
			paymentTypesList.add(new BasicDBObject(FieldType.PAYMENT_TYPE.getName(), pt));
		}

		List<BasicDBObject> statusList = new ArrayList<>();
		for (AccountStatus status : accountStatusList) {
			statusList.add(new BasicDBObject(FieldType.STATUS.getName(), status.getCode()));
		}

		BasicDBObject query = new BasicDBObject();
		List<BasicDBObject> andQueryList = new ArrayList<>();
		BasicDBObject paymentTypeOrQuery = new BasicDBObject();
		BasicDBObject accountStatusOrQuery = new BasicDBObject();

		paymentTypeOrQuery.put("$or", paymentTypesList);
		accountStatusOrQuery.put("$or", statusList);
		andQueryList.add(new BasicDBObject(FieldType.BENE_MERCHANT_PROVIDED_ID.getName(), merchantProvidedCode));
		if(StringUtils.isNotBlank(payId)) {
			andQueryList.add(new BasicDBObject(FieldType.PAY_ID.getName(), payId));
		}
		andQueryList.add(paymentTypeOrQuery);
		andQueryList.add(accountStatusOrQuery);

		query.put("$and", andQueryList);

		logger.info("findByStatusPaymentTypePayIdMerchantProvidedCode() query : " + query);
		MongoDatabase dbIns = mongoInstance.getDB();
		MongoCollection<Document> collection = dbIns.getCollection(PropertiesManager.propertiesMap
				.get(PREFIX + Constants.BENEFICIARY_ACCOUNTS_COLLECTION_NAME.getValue()));
		FindIterable<Document> output = collection.find(query);
		MongoCursor<Document> cursor = output.iterator();

		while (cursor.hasNext()) {
			Document doc = cursor.next();
			list.add(BeneficiaryAccountsUtilities.getBeneficiaryFromDoc(doc));
		}
		return list;
	}

	public Long findByPayIdAccountNumberIfscCode(String payId, String accountNumber, String ifscCode,
			List<AccountStatus> accountStatusList, String beneficiaryType) {

		List<BasicDBObject> statusList = new ArrayList<>();
		for (AccountStatus status : accountStatusList) {
			statusList.add(new BasicDBObject(FieldType.STATUS.getName(), status.getCode()));
		}

		BasicDBObject query = new BasicDBObject();
		List<BasicDBObject> andQueryList = new ArrayList<>();
		BasicDBObject accountStatusOrQuery = new BasicDBObject();

		accountStatusOrQuery.put("$or", statusList);

		andQueryList.add(new BasicDBObject(FieldType.BENE_ACCOUNT_NO.getName(), accountNumber));
		andQueryList.add(new BasicDBObject(FieldType.IFSC_CODE.getName(), ifscCode));
		if(StringUtils.isNotBlank(payId)) {
			andQueryList.add(new BasicDBObject(FieldType.PAY_ID.getName(), payId));
		}
		
		if(StringUtils.isNotBlank(beneficiaryType)) {
			andQueryList.add(new BasicDBObject(FieldType.BENE_TYPE.getName(), beneficiaryType));
		}
		
		andQueryList.add(accountStatusOrQuery);

		query.put("$and", andQueryList);

		logger.info("findByPayIdAccountNumberIfscCode() query : " + query);
		MongoDatabase dbIns = mongoInstance.getDB();
		MongoCollection<Document> collection = dbIns.getCollection(PropertiesManager.propertiesMap
				.get(PREFIX + Constants.BENEFICIARY_ACCOUNTS_COLLECTION_NAME.getValue()));
		Long count = collection.count(query);
		return count;
	}

	public List<BeneficiaryAccounts> findMerchantTypeBeneficiary(String merchantPayId, String beneficiaryType, String accountStatus) {
		List<BeneficiaryAccounts> list = new ArrayList<>();
		MongoDatabase dbIns = mongoInstance.getDB();
		MongoCollection<Document> collection = dbIns.getCollection(PropertiesManager.propertiesMap
				.get(PREFIX + Constants.BENEFICIARY_ACCOUNTS_COLLECTION_NAME.getValue()));

		BasicDBObject findQuery = new BasicDBObject();

		findQuery.put(FieldType.PAY_ID.getName(), merchantPayId);
		findQuery.put(FieldType.BENE_TYPE.getName(), beneficiaryType);
		findQuery.put(FieldType.STATUS.getName(), accountStatus);
		logger.info("findMerchantTypeBeneficiary() Query : " + findQuery);
		FindIterable<Document> output = collection.find(findQuery);
		MongoCursor<Document> cursor = output.iterator();
		while (cursor.hasNext()) {
			Document doc = cursor.next();
			list.add(BeneficiaryAccountsUtilities.getBeneficiaryFromDoc(doc));
		}
		cursor.close();
		return list;
	}
}
