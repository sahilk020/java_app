package com.crmws.service.impl;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import com.pay10.commons.user.UserDao;
import org.apache.commons.lang3.StringUtils;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.crmws.dto.AcquirerRebalanceDTO;
import com.crmws.dto.AcquirerRebalanceTxnDTO;
import com.crmws.service.AcquirerRebalanceService;
import com.crmws.util.DateUtil;
import com.google.gson.Gson;
import com.mongodb.BasicDBObject;
import com.mongodb.BasicDBObjectBuilder;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Sorts;
import com.pay10.commons.mongo.MongoInstance;
import com.pay10.commons.util.FieldType;
import com.pay10.commons.util.TransactionManager;

import lombok.extern.slf4j.Slf4j;

import javax.management.loading.PrivateClassLoader;

@Service
@Slf4j
public class AcquirerRebalanceServiceImpl implements AcquirerRebalanceService {

	@Autowired
	private MongoInstance mongoInstance;

	@Autowired
	private UserDao userDao;

	@Override
	public Optional<List<AcquirerRebalanceDTO>> getAcquirerRebalanceList() {

		List<AcquirerRebalanceDTO> acquirerRebalanceList = new ArrayList<AcquirerRebalanceDTO>();

		MongoDatabase dbIns = mongoInstance.getDB();
		MongoCollection<Document> collection = dbIns.getCollection("AcquirerWalletMaster");

		FindIterable<Document> output = collection.find();
		log.info("findLastTransaction : " + output);
		MongoCursor<Document> result = output.iterator();

		while (result.hasNext()) {
			Document doc = (Document) result.next();
			AcquirerRebalanceDTO acquirerRebalanceInfo = new AcquirerRebalanceDTO();
			if (StringUtils.isNotBlank(doc.getString("acquirerName"))) {
				acquirerRebalanceInfo.setAcquirerName(String.valueOf(doc.get("acquirerName")));
			}
			if (StringUtils.isNotBlank(doc.getString("acquirerCode"))) {
				acquirerRebalanceInfo.setAcquirerCode(String.valueOf(doc.get("acquirerCode")));
			}
			if (StringUtils.isNotBlank(doc.getString("finalBalance"))) {
				acquirerRebalanceInfo.setFinalBalance(String.valueOf(doc.get("finalBalance")));
			}

			if (StringUtils.isNotBlank(doc.getString("payId"))) {
				acquirerRebalanceInfo.setPayId(String.valueOf(doc.get("payId")));
			}

			if (StringUtils.isNotBlank(doc.getString("currency"))) {
				acquirerRebalanceInfo.setCurrency(String.valueOf(doc.get("currency")));
			}

			acquirerRebalanceList.add(acquirerRebalanceInfo);
		}
		return Optional.ofNullable(acquirerRebalanceList);

	}

	public Map<String, Object> findAcquirerWalletByAcquirerName(String acquirerName, String currency) {
		log.info("findAcquirerWalletByAcquirerName, request : acquirerName={}", acquirerName);
		Map<String, Object> acqWalletMap = new HashMap<>();
		try {
			List<BasicDBObject> paramConditionLst = new ArrayList<BasicDBObject>();
			
			paramConditionLst.add(new BasicDBObject("acquirerName", acquirerName));
			paramConditionLst.add(new BasicDBObject("currency", currency));
			BasicDBObject finalquery = new BasicDBObject("$and", paramConditionLst);
			MongoDatabase dbIns = mongoInstance.getDB();
			MongoCollection<Document> coll = dbIns.getCollection("AcquirerWalletMaster");

			MongoCursor<Document> cursor = coll.find(finalquery).iterator();

			if (cursor.hasNext()) {
				Document document = cursor.next();
				log.info("findAcquirerWalletByAcquirerName, Document: {}", document);
				acqWalletMap.put("payId", document.getString("payId"));
				acqWalletMap.put("acquirerName", document.getString("acquirerName"));
				acqWalletMap.put("acquirerCode", document.getString("acquirerCode"));
				acqWalletMap.put("finalBalance", decimalConversion(document.getString("finalBalance")));
				acqWalletMap.put("currency", document.getString("currency"));

			}

		} catch (Exception e) {
			log.error("Exception in : ", e);
		}
		return acqWalletMap;

	}

	public boolean updateAcquirerWallet(String acquirerName, String currency, String updatedBalance) {

		try {
			MongoDatabase dbIns = null;
			List<BasicDBObject> paramConditionLst = new ArrayList<BasicDBObject>();
			
			paramConditionLst.add(new BasicDBObject("acquirerName", acquirerName));
			paramConditionLst.add(new BasicDBObject("currency", currency));
			BasicDBObject finalquery = new BasicDBObject("$and", paramConditionLst);
			
			dbIns = mongoInstance.getDB();
			MongoCollection<Document> collection = dbIns.getCollection("AcquirerWalletMaster");

			FindIterable<Document> output = collection.find(finalquery);
			MongoCursor<Document> cursor = output.iterator();
			Document doc = null;
			int count = 0;
			while (cursor.hasNext()) {
				doc = cursor.next();
				++count;
			}
			cursor.close();
			if (count > 1) {
				return false;
			}

			if (doc == null) {
				return false;
			}

			doc.put("finalBalance", decimalConversion(updatedBalance));
			collection.replaceOne(finalquery, doc);
		} catch (Exception exception) {
			log.error("updateAcquirerWallet, Exception in : ", exception);
		}
		return true;

	}

	@Override
	public String acquirerFundTransfer(Map<String, String> actionRequest) {

		String orderId = TransactionManager.getNewTransactionId();
		actionRequest.put("orderId", orderId);
		Map<String, Object> fromAcquirerWalletResp = findAcquirerWalletByAcquirerName(actionRequest.get("fromAcquirerName"), actionRequest.get("currencyFrom"));
		if (fromAcquirerWalletResp.size() > 0) {
			log.info("acquirerFundTransfer, acquirerWalletResp={}" + fromAcquirerWalletResp);

			if (Double.valueOf(actionRequest.get("amount")) > Double.valueOf(fromAcquirerWalletResp.get("finalBalance").toString())) {
				return "Insufficient amount for funds transfer";
			}
			Map<String, Object> toAcquirerWalletResp = findAcquirerWalletByAcquirerName(actionRequest.get("toAcquirerName"), actionRequest.get("currencyTo"));
			if (toAcquirerWalletResp.size() > 0) {
				// Double requestAmount = 0.00;
				// Double walletAmount = 0.00;
				Double toWalletAmount = Double.valueOf(amountFormatting(String.valueOf(toAcquirerWalletResp.get("finalBalance"))));
				Double fromWalletAmount = Double.valueOf(amountFormatting(String.valueOf(fromAcquirerWalletResp.get("finalBalance"))));
				Double requestAmount = Double.valueOf(amountFormatting(actionRequest.get("amount")));
				fromWalletAmount -= requestAmount;
				toWalletAmount += requestAmount;

				// TODO substract amount from fromAcquirerName acquirer
				boolean fromAcqResp = updateAcquirerWallet(actionRequest.get("fromAcquirerName"), actionRequest.get("currencyFrom"), 
						String.valueOf(fromWalletAmount));
				boolean toAcqResp = false;
				if (fromAcqResp) {
					toAcqResp = updateAcquirerWallet(actionRequest.get("toAcquirerName"), actionRequest.get("currencyTo"), String.valueOf(toWalletAmount));
				}
				if (fromAcqResp && toAcqResp) {
					addAcquirerWalletTransactionEntry(actionRequest, "CREDIT", "FUND TRANSFER", "SUCCESS", "admin@bpgate.com");
					return "success";
				}
			} else {
				return "to acquirer wallet doesn't exist, please check with administrator";
			}
		} else {
			return "from acquirer wallet doesn't exist, please check with administrator";
		}
		return null;
	}

	private void addAcquirerWalletTransactionEntry(Map<String, String> actionRequest, String txnType, String narration,
			String status, String createdBy) {
		try {
			MongoDatabase dbIns = mongoInstance.getDB();
			MongoCollection<Document> coll = dbIns.getCollection("AcquirerWalletTransaction");

			Document document = new Document();
			String fromPayId = userDao.getPayIdByAcquirerNameForWallet(actionRequest.get("fromAcquirerName"));
			String toPayId = userDao.getPayIdByAcquirerNameForWallet(actionRequest.get("toAcquirerName"));
			document.put("fromPayId",fromPayId);
			document.put("fromAcquirerName", String.valueOf(actionRequest.get("fromAcquirerName") != null ? actionRequest.get("fromAcquirerName") : "NA"));
			document.put("toPayId", toPayId);
			document.put("toAcquirerName", String.valueOf(actionRequest.get("toAcquirerName") != null ? actionRequest.get("toAcquirerName") : "NA"));
			document.put("amount", decimalConversion(String.valueOf(actionRequest.get("amount") != null ? actionRequest.get("amount") : "NA")));
			document.put("orderId",String.valueOf(actionRequest.get("orderId") != null ? actionRequest.get("orderId") : "NA"));
			document.put("txnType", txnType);
			document.put("narration", narration);
			document.put("status", status);
			document.put("createdBy", createdBy);
			document.put("createdDate", DateUtil.getCurrentTime("yyyy-MM-dd HH:mm:ss"));
			document.put("fromCurrency", String.valueOf(actionRequest.get("currencyFrom") != null ? actionRequest.get("currencyFrom") : "NA"));
			document.put("toCurrency",	String.valueOf(actionRequest.get("currencyTo") != null ? actionRequest.get("currencyTo") : "NA"));

			log.info("addAcquirerWalletTransactionEntry CREATE : {}", new Gson().toJson(document));
			coll.insertOne(document);
		} catch (Exception e) {
			log.error("addAcquirerWalletTransactionEntry, Exception in : {}", e);
		}
	}

	public String decimalConversion(String data) {
		Double decimalData = Double.parseDouble(data);
		return String.format("%.2f", decimalData);
	}

	public static String amountFormatting(String amount) {

		if (amount.contains(".")) {
			String rupess = amount.split("\\.")[0];
			String decimal = amount.split("\\.")[1];

			if (decimal.length() > 2) {
				return (rupess + "." + (decimal.substring(0, 2)));
			} else {
				return amount;
			}
		} else {
			return amount;
		}
	}

	@Override
	public List<AcquirerRebalanceTxnDTO> getAcquirerTransactions(String type) {
		List<AcquirerRebalanceTxnDTO> acquirerRebalanceList = new ArrayList<AcquirerRebalanceTxnDTO>();

		List<BasicDBObject> paramConditionLst = new ArrayList<BasicDBObject>();

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

		dateQuery.put("createdDate",
				BasicDBObjectBuilder.start("$gte", new SimpleDateFormat(fromDate).toLocalizedPattern())
						.add("$lte", new SimpleDateFormat(currentDate).toLocalizedPattern()).get());

		if (!StringUtils.equalsIgnoreCase(type, "ALL")) {
			paramConditionLst.add(new BasicDBObject("status", type));
		}

		if (!dateQuery.isEmpty()) {
			paramConditionLst.add(dateQuery);
		}

		BasicDBObject finalquery = new BasicDBObject("$and", paramConditionLst);

		log.info("findLastTransaction, finalquery : " + finalquery.toString());
		MongoDatabase dbIns = mongoInstance.getDB();
		MongoCollection<Document> collection = dbIns.getCollection("AcquirerWalletTransaction");

		FindIterable<Document> output = collection.find(finalquery).sort(Sorts.descending("createdDate"));
		log.info("findLastTransaction : " + output);
		MongoCursor<Document> result = output.iterator();

		while (result.hasNext()) {
			Document doc = (Document) result.next();
			AcquirerRebalanceTxnDTO acquirerRebalanceInfo = new AcquirerRebalanceTxnDTO();
			if (StringUtils.isNotBlank(doc.getString("amount"))) {
				acquirerRebalanceInfo.setAmount(String.valueOf(doc.get("amount")));
			}
			if (StringUtils.isNotBlank(doc.getString("createdBy"))) {
				acquirerRebalanceInfo.setCreatedBy(String.valueOf(doc.get("createdBy")));
			}
			if (StringUtils.isNotBlank(doc.getString("createdDate"))) {
				acquirerRebalanceInfo.setCreatedDate(String.valueOf(doc.get("createdDate")));
			}
			if (StringUtils.isNotBlank(doc.getString("fromAcquirerName"))) {
				acquirerRebalanceInfo.setFromAcquirerName(String.valueOf(doc.get("fromAcquirerName")));
			}
			if (StringUtils.isNotBlank(doc.getString("fromPayId"))) {
				acquirerRebalanceInfo.setFromPayId(String.valueOf(doc.get("fromPayId")));
			}
			if (StringUtils.isNotBlank(doc.getString("narration"))) {
				acquirerRebalanceInfo.setNarration(String.valueOf(doc.get("narration")));
			}
			if (StringUtils.isNotBlank(doc.getString("status"))) {
				acquirerRebalanceInfo.setStatus(String.valueOf(doc.get("status")));
			}
			if (StringUtils.isNotBlank(doc.getString("toAcquirerName"))) {
				acquirerRebalanceInfo.setToAcquirerName(String.valueOf(doc.get("toAcquirerName")));
			}
			if (StringUtils.isNotBlank(doc.getString("toPayId"))) {
				acquirerRebalanceInfo.setToPayId(String.valueOf(doc.get("toPayId")));
			}
			if (StringUtils.isNotBlank(doc.getString("txnType"))) {
				acquirerRebalanceInfo.setTxnType(String.valueOf(doc.get("txnType")));
			}

			if (StringUtils.isNotBlank(doc.getString("orderId"))) {
				acquirerRebalanceInfo.setOrderId(String.valueOf(doc.get("orderId")));
			}

			acquirerRebalanceList.add(acquirerRebalanceInfo);
		}
		return acquirerRebalanceList;

	}

	@Override
	public Optional<List<AcquirerRebalanceDTO>> getAcquirerListByAcquirerName(String acquirerName) {

		List<AcquirerRebalanceDTO> acquirerRebalanceList = new ArrayList<AcquirerRebalanceDTO>();
		BasicDBObject finalquery = new BasicDBObject("acquirerName", acquirerName);
		log.info("getAcquirerListByAcquirerName, finalquery : " + finalquery.toString());
		MongoDatabase dbIns = mongoInstance.getDB();
		MongoCollection<Document> collection = dbIns.getCollection("AcquirerWalletMaster");

		FindIterable<Document> output = collection.find(finalquery);
		log.info("findLastTransaction : " + output);
		MongoCursor<Document> result = output.iterator();

		while (result.hasNext()) {
			Document doc = (Document) result.next();
			AcquirerRebalanceDTO acquirerRebalanceInfo = new AcquirerRebalanceDTO();
			if (StringUtils.isNotBlank(doc.getString("acquirerName"))) {
				acquirerRebalanceInfo.setAcquirerName(String.valueOf(doc.get("acquirerName")));
			}
			if (StringUtils.isNotBlank(doc.getString("acquirerCode"))) {
				acquirerRebalanceInfo.setAcquirerCode(String.valueOf(doc.get("acquirerCode")));
			}
			if (StringUtils.isNotBlank(doc.getString("finalBalance"))) {
				acquirerRebalanceInfo.setFinalBalance(String.valueOf(doc.get("finalBalance")));
			}
			if (StringUtils.isNotBlank(doc.getString("payId"))) {
				acquirerRebalanceInfo.setPayId(String.valueOf(doc.get("payId")));
			}

			if (StringUtils.isNotBlank(doc.getString("currency"))) {
				acquirerRebalanceInfo.setCurrency(String.valueOf(doc.get("currency")));
			}

			acquirerRebalanceList.add(acquirerRebalanceInfo);
		}
		return Optional.ofNullable(acquirerRebalanceList);

	}

	@Override
	public String addAcquirerFund(Map<String, String> actionRequest) {

		try {

			String orderId = TransactionManager.getNewTransactionId();
			actionRequest.put("orderId", orderId);

			MongoDatabase dbIns = mongoInstance.getDB();
			MongoCollection<Document> coll = dbIns.getCollection("AcquirerWalletTransaction");

			Document document = new Document();
			String fromPayId = userDao.getPayIdByAcquirerNameForWallet(actionRequest.get("fromAcquirerName"));
			String toPayId = userDao.getPayIdByAcquirerNameForWallet(actionRequest.get("toAcquirerName"));

			document.put("fromPayId", fromPayId);
			document.put("fromAcquirerName", String.valueOf(actionRequest.get("fromAcquirerName") != null ? actionRequest.get("fromAcquirerName") : "NA"));
			document.put("toPayId", toPayId);
			document.put("toAcquirerName", String.valueOf(actionRequest.get("toAcquirerName") != null ? actionRequest.get("toAcquirerName") : "NA"));
			document.put("amount", decimalConversion(String.valueOf(actionRequest.get("amount") != null ? actionRequest.get("amount") : "NA")));
			document.put("orderId", String.valueOf(actionRequest.get("orderId") != null ? actionRequest.get("orderId") : "NA"));
			document.put("txnType", "CREDIT");
			document.put("narration", "TOPUP");
			document.put("status", "PENDING");
			document.put("createdBy", "admin@bpgate.com");
			document.put("createdDate", DateUtil.getCurrentTime("yyyy-MM-dd HH:mm:ss"));
			document.put("fromCurrency", String.valueOf(actionRequest.get("fromCurrency") != null ? actionRequest.get("fromCurrency") : "NA"));
			document.put("toCurrency", String.valueOf(actionRequest.get("toCurrency") != null ? actionRequest.get("toCurrency") : "NA"));

			log.info("addAcquirerWalletTransactionEntry CREATE : {}", new Gson().toJson(document));
			coll.insertOne(document);
		} catch (Exception e) {
			log.error("addAcquirerWalletTransactionEntry, Exception in : {}", e);
			return "Failed";
		}
		return "success";

	}

	@Override
	public boolean actionAcquirerBalanceStatus(Map<String, String> actionRequest, String actionStatus) {
		try {
			MongoDatabase dbIns = null;
			List<BasicDBObject> paramConditionLst = new ArrayList<BasicDBObject>();
			
			paramConditionLst.add(new BasicDBObject("orderId", String.valueOf(actionRequest.get("orderId"))));
			paramConditionLst.add(new BasicDBObject("narration", String.valueOf("TOPUP")));
			paramConditionLst.add(new BasicDBObject("status", "PENDING"));
			paramConditionLst.add(new BasicDBObject("toAcquirerName", String.valueOf(actionRequest.get("acquirerName"))));

			BasicDBObject finalquery = new BasicDBObject("$and", paramConditionLst);
			dbIns = mongoInstance.getDB();
			MongoCollection<Document> collection = dbIns.getCollection("AcquirerWalletTransaction");
			FindIterable<Document> output = collection.find(finalquery);
			MongoCursor<Document> cursor = output.iterator();
			Document doc = null;
			int count = 0;
			String currency = null;
			while (cursor.hasNext()) {
				doc = cursor.next();
				currency = String.valueOf(doc.get("toCurrency"));
				++count;
			}
			cursor.close();
			if (count > 1) {
				return false;
			}

			if (doc == null) {
				return false;
			}

			Map<String, Object> acquirerWalletResp = findAcquirerWalletByAcquirerName(actionRequest.get("acquirerName"), currency);
			doc.put("status", actionStatus);
			collection.replaceOne(finalquery, doc);

			if (StringUtils.equalsIgnoreCase(actionStatus, "APPROVED")) {
				Double walletAmount = Double.valueOf(amountFormatting(String.valueOf(acquirerWalletResp.get("finalBalance"))));
				Double requestAmount = Double.valueOf(amountFormatting(actionRequest.get("amount")));
				walletAmount += requestAmount;

				return updateAcquirerWallet(actionRequest.get("acquirerName"), currency, String.valueOf(walletAmount));
			}
			return true;
		} catch (Exception exception) {
			log.error("updateAcquirerWallet, Exception in : ", exception);
			return false;
		}

	}

}
