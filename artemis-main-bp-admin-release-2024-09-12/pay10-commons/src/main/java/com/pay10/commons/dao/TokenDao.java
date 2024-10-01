package com.pay10.commons.dao;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.bson.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.mongodb.BasicDBObject;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import com.pay10.commons.dao.HibernateAbstractDao;
import com.pay10.commons.mongo.MongoInstance;
import com.pay10.commons.user.Token;
import com.pay10.commons.util.Constants;
import com.pay10.commons.util.FieldType;
import com.pay10.commons.util.PropertiesManager;
import com.pay10.commons.util.TokenStatus;
import com.pay10.commons.util.TransactionManager;

/**
 * @author Shaiwal Moved Token from MySql to Mongo DB on 22-11-2019
 *
 */
@Service
public class TokenDao extends HibernateAbstractDao {

	@Autowired
	private MongoInstance mongoInstance;

	@Autowired
	private PropertiesManager propertiesManager;

	private static final String prefix = "MONGO_DB_";
	private static Logger logger = LoggerFactory.getLogger(TokenDao.class.getName());

	public void create(Token token) {

		MongoDatabase dbIns = mongoInstance.getDB();
		MongoCollection<Document> coll = dbIns
				.getCollection(PropertiesManager.propertiesMap.get(prefix + Constants.CARDSAVE_TOKEN_NAME.getValue()));

		try {

			Document doc = new Document();
			String txnId = TransactionManager.getNewTransactionId();
			doc.put("_id", txnId);
			doc.put("id", token.getCardToken());
			doc.put("payId", token.getPayId());
			doc.put("mopType", token.getMopType());
			doc.put("paymentType", token.getPaymentType());

			if (token.getStatus().equals(TokenStatus.ACTIVE)) {
				doc.put("status", "ACTIVE");
			} else if (token.getStatus().equals(TokenStatus.INACTIVE)) {
				doc.put("status", "INACTIVE");
			} else {
				doc.put("status", "EXPIRED");
			}

			doc.put("customerName", token.getCustomerName());
			doc.put("cardMask", token.getCardMask());
			doc.put("userCardMask", token.getUserCardMask());
			doc.put("cardLabel", token.getCardLabel());
			doc.put("cardSaveParam", token.getCardSaveParam());
			doc.put("cardToken", token.getCardToken());
			doc.put("networkToken", token.getNetworkToken());
			doc.put("issuerToken", token.getIssuerToken());

			doc.put("paymentsRegion", token.getPaymentsRegion());
			doc.put("cardHolderType", token.getCardHolderType());

			coll.insertOne(doc);

		} catch (Exception e) {
			logger.error("Exception in saving card token", e);
		}
	}

	public void delete(Token token) {

		MongoDatabase dbIns = mongoInstance.getDB();
		MongoCollection<Document> coll = dbIns
				.getCollection(PropertiesManager.propertiesMap.get(prefix + Constants.CARDSAVE_TOKEN_NAME.getValue()));

		BasicDBObject idQuery = new BasicDBObject("id", token.getId());
		coll.deleteOne(idQuery);

	}

	public void deleteTokenById(String tokenId) {

		MongoDatabase dbIns = mongoInstance.getDB();
		MongoCollection<Document> coll = dbIns
				.getCollection(PropertiesManager.propertiesMap.get(prefix + Constants.CARDSAVE_TOKEN_NAME.getValue()));

		BasicDBObject idQuery = new BasicDBObject("id", tokenId);
		coll.deleteOne(idQuery);

	}

	public void deleteByIdPayIdSaveParam(String tokenId,String payId,String cardSaveParam) {

		MongoDatabase dbIns = mongoInstance.getDB();
		MongoCollection<Document> coll = dbIns
				.getCollection(PropertiesManager.propertiesMap.get(prefix + Constants.CARDSAVE_TOKEN_NAME.getValue()));
		List<BasicDBObject> paramConditionLst = new ArrayList<BasicDBObject>();

		BasicDBObject payIdQuery = new BasicDBObject("id", tokenId);
		BasicDBObject cardMaskQuery = new BasicDBObject("payId", payId);
		BasicDBObject cardSavParamQuery = new BasicDBObject("cardSaveParam", cardSaveParam);

		paramConditionLst.add(payIdQuery);
		paramConditionLst.add(cardMaskQuery);
		paramConditionLst.add(cardSavParamQuery);

		BasicDBObject finalquery = new BasicDBObject("$and", paramConditionLst);
		coll.deleteOne(finalquery);
	}

	public Map<String, Token> getAll(String payId, String cardSaveParam) {

		logger.info("Card Token PayId :" + payId);
		logger.info("Card Token Card Token Param :" + cardSaveParam);
		List<Token> tokens = new ArrayList<Token>();
		HashMap<String, Token> tokenMap = new HashMap<String, Token>();

		MongoDatabase dbIns = mongoInstance.getDB();
		MongoCollection<Document> coll = dbIns
				.getCollection(PropertiesManager.propertiesMap.get(prefix + Constants.CARDSAVE_TOKEN_NAME.getValue()));

		try {

			List<BasicDBObject> paramConditionLst = new ArrayList<BasicDBObject>();
			BasicDBObject payIdQuery = new BasicDBObject("payId", payId);
			BasicDBObject cardSaveParamQuery = new BasicDBObject("cardSaveParam", cardSaveParam);

			paramConditionLst.add(payIdQuery);
			paramConditionLst.add(cardSaveParamQuery);

			BasicDBObject finalquery = new BasicDBObject("$and", paramConditionLst);

			FindIterable<Document> output = coll.find(finalquery);

			MongoCursor<Document> cursor = output.iterator();

			while (cursor.hasNext()) {
				Document doc = cursor.next();
				logger.info("Document In token :" + doc.toJson());
				Token token = documentToToken(doc);
				logger.info("Token Obj In token :" + token.toString());
				tokens.add(token);

			}

			for (Token token : tokens) {
				tokenMap.put(token.getId(), token);
			}

			return tokenMap;

		}

		catch (Exception e) {
			logger.error("Exception in getting all token", e);
		}

		return tokenMap;

	}

	public Token getToken(String tokenId) {

		List<Token> tokens = new ArrayList<Token>();
		Map<String, String> tokenMap = new HashMap<String, String>();
		Token token = null;
		MongoDatabase dbIns = mongoInstance.getDB();
		MongoCollection<Document> coll = dbIns
				.getCollection(PropertiesManager.propertiesMap.get(prefix + Constants.CARDSAVE_TOKEN_NAME.getValue()));

		try {

			List<BasicDBObject> paramConditionLst = new ArrayList<BasicDBObject>();
			BasicDBObject idQuery = new BasicDBObject("id", tokenId);

			paramConditionLst.add(idQuery);

			BasicDBObject finalquery = new BasicDBObject("$and", paramConditionLst);

			FindIterable<Document> output = coll.find(finalquery);

			MongoCursor<Document> cursor = output.iterator();

			while (cursor.hasNext()) {
				Document doc = cursor.next();
				token = documentToToken(doc);

			}

			/*
			 * for (Token token : tokens) {
			 * 
			 * tokenMap.put(FieldType.CARD_EXP_DT.getName(), token.getExpiryDate());
			 * tokenMap.put(FieldType.CARD_NUMBER.getName(), token.getCardNumber());
			 * tokenMap.put(FieldType.MOP_TYPE.getName(), token.getMopType());
			 * tokenMap.put(FieldType.CARD_HOLDER_NAME.getName(), token.getCustomerName());
			 * tokenMap.put(FieldType.PAYMENT_TYPE.getName(), token.getPaymentType());
			 * tokenMap.put(FieldType.INTERNAL_CARD_ISSUER_BANK.getName(),
			 * token.getCardIssuerBank());
			 * tokenMap.put(FieldType.INTERNAL_CARD_ISSUER_COUNTRY.getName(),
			 * token.getCardIssuerCountry());
			 * 
			 * return tokenMap;
			 * 
			 * }
			 */
		}

		catch (Exception e) {
			logger.error("Exception in getting all token", e);
		}

		return token;

	}

	public Token findTokenById(String id) {

		MongoDatabase dbIns = mongoInstance.getDB();
		MongoCollection<Document> coll = dbIns
				.getCollection(PropertiesManager.propertiesMap.get(prefix + Constants.CARDSAVE_TOKEN_NAME.getValue()));

		Token token = null;

		try {

			List<BasicDBObject> paramConditionLst = new ArrayList<BasicDBObject>();
			BasicDBObject idQuery = new BasicDBObject("id", id);

			paramConditionLst.add(idQuery);

			BasicDBObject finalquery = new BasicDBObject("$and", paramConditionLst);

			FindIterable<Document> output = coll.find(finalquery);

			MongoCursor<Document> cursor = output.iterator();

			while (cursor.hasNext()) {
				Document doc = cursor.next();
				token = documentToToken(doc);
			}

		}

		catch (Exception e) {
			logger.error("Exception in getting all token", e);
		}

		return token;

	}

	public Token getCardNumber(String cardMask, String payId, String cardSaveParam) {

		MongoDatabase dbIns = mongoInstance.getDB();
		MongoCollection<Document> coll = dbIns
				.getCollection(PropertiesManager.propertiesMap.get(prefix + Constants.CARDSAVE_TOKEN_NAME.getValue()));

		Token token = null;

		try {

			List<BasicDBObject> paramConditionLst = new ArrayList<BasicDBObject>();

			BasicDBObject payIdQuery = new BasicDBObject("payId", payId);
			BasicDBObject cardMaskQuery = new BasicDBObject("userCardMask", cardMask);
			BasicDBObject cardSavParamQuery = new BasicDBObject("cardSaveParam", cardSaveParam);

			paramConditionLst.add(payIdQuery);
			paramConditionLst.add(cardMaskQuery);
			paramConditionLst.add(cardSavParamQuery);

			BasicDBObject finalquery = new BasicDBObject("$and", paramConditionLst);

			FindIterable<Document> output = coll.find(finalquery);

			MongoCursor<Document> cursor = output.iterator();

			while (cursor.hasNext()) {
				Document doc = cursor.next();
				token = documentToToken(doc);
			}

		}

		catch (Exception e) {
			logger.error("Exception in getting all token", e);
		}

		return token;

	}

	public Token documentToToken(Document doc) {

		Token token = new Token();

		if (StringUtils.isNotBlank(doc.getString("id"))) {
			token.setId(doc.getString("id"));
		} else {
			return token;
		}

		if (StringUtils.isNotBlank(doc.getString("payId"))) {

			token.setPayId(doc.getString("payId"));
		} else {
			token.setPayId("NA");
		}

		if (StringUtils.isNotBlank(doc.getString("mopType"))) {
			token.setMopType(doc.getString("mopType"));
		} else {
			token.setMopType("NA");
		}

		if (StringUtils.isNotBlank(doc.getString("paymentType"))) {
			token.setPaymentType(doc.getString("paymentType"));
		} else {
			token.setPaymentType("NA");
		}

		if (StringUtils.isNotBlank(doc.getString("status"))) {

			if (doc.getString("status").equalsIgnoreCase("ACTIVE")) {
				token.setStatus(TokenStatus.ACTIVE);
			} else if ((doc.getString("status")).equalsIgnoreCase("EXPIRED")) {
				token.setStatus(TokenStatus.EXPIRED);
			} else {
				token.setStatus(TokenStatus.INACTIVE);
			}

		} else {
			token.setStatus(TokenStatus.INACTIVE);
		}

		if (StringUtils.isNotBlank(doc.getString("customerName"))) {
			token.setCustomerName(doc.getString("customerName"));
		} else {
			token.setCustomerName("NA");
		}

		if (StringUtils.isNotBlank(doc.getString("cardMask"))) {
			token.setCardMask(doc.getString("cardMask"));
		} else {
			token.setCardMask("NA");
		}

		if (StringUtils.isNotBlank(doc.getString("userCardMask"))) {
			token.setUserCardMask(doc.getString("userCardMask"));
		} else {
			token.setUserCardMask("NA");
		}

		if (StringUtils.isNotBlank(doc.getString("cardLabel"))) {
			token.setUserCardMask(doc.getString("cardLabel"));
		} else {
			token.setUserCardMask("NA");
		}

		if (StringUtils.isNotBlank(doc.getString("cardSaveParam"))) {
			token.setCardSaveParam(doc.getString("cardSaveParam"));
		} else {
			token.setCardSaveParam("NA");
		}

		if (StringUtils.isNotBlank(doc.getString("cardToken"))) {
			token.setCardToken(doc.getString("cardToken"));
		} else {
			token.setCardToken("NA");
		}

		if (StringUtils.isNotBlank(doc.getString("issuerToken"))) {
			token.setIssuerToken(doc.getString("issuerToken"));
		} else {
			token.setIssuerToken("NA");
		}

		if (StringUtils.isNotBlank(doc.getString("networkToken"))) {
			token.setNetworkToken(doc.getString("networkToken"));
		} else {
			token.setNetworkToken("NA");
		}

		if (StringUtils.isNotBlank(doc.getString("paymentsRegion"))) {
			token.setPaymentsRegion(doc.getString("paymentsRegion"));
		} else {
			token.setPaymentsRegion("NA");
		}

		if (StringUtils.isNotBlank(doc.getString("cardHolderType"))) {
			token.setCardHolderType(doc.getString("cardHolderType"));
		} else {
			token.setCardHolderType("NA");
		}

		return token;

	}

}
