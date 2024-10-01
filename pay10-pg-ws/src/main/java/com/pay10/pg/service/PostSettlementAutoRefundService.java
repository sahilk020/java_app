package com.pay10.pg.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

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
import com.pay10.commons.mongo.MongoInstance;
import com.pay10.commons.user.User;
import com.pay10.commons.util.Amount;
import com.pay10.commons.util.Constants;
import com.pay10.commons.util.FieldType;
import com.pay10.commons.util.Fields;
import com.pay10.commons.util.PropertiesManager;
import com.pay10.commons.util.StaticDataProvider;
import com.pay10.commons.util.StatusType;
import com.pay10.commons.util.TxnType;
import com.pay10.requestrouter.RequestRouter;

@Service
public class PostSettlementAutoRefundService {

	private static Logger logger = LoggerFactory.getLogger(PostSettlementAutoRefundService.class.getName());

	@Autowired
	private MongoInstance mongoInstance;

	@Autowired
	private RequestRouter router;

	@Autowired
	private StaticDataProvider staticDataProvider;

	private static final String PREFIX = "MONGO_DB_";

	public void initiateAutoRefund(String pgRefNum) {

		Document doc = getDocument(pgRefNum);

		if (doc == null) {
			logger.error("No txn found with sale captured.");
			return;
		}

		boolean autoRefundPostSettlementFlag = getAutoRefundPostSettlementFlag(doc.getString(FieldType.PAY_ID.getName()));

		if (!autoRefundPostSettlementFlag) {
			logger.info("Post settlement auto refund flag is disabled for payId : "
					+ doc.getString(FieldType.PAY_ID.getName()));
			return;
		}

		Map<String, String> requestMap = createRequestMap(doc);

		logger.info("Refund Object : " + requestMap);

		Fields fields = new Fields(requestMap);
		fields.logAllFields("Raw Request:");
		fields.removeInternalFields();
		fields.clean();
		fields.removeExtraFields();
		// To put request blob
		String fieldsAsString = fields.getFieldsAsBlobString();
		fields.put(FieldType.INTERNAL_REQUEST_FIELDS.getName(), fieldsAsString);
		fields.put(FieldType.INTERNAL_VALIDATE_HASH_YN.getName(), "N");
		if (fields.contains(FieldType.UDF6.getName()) && !fields.get(FieldType.UDF6.getName()).isEmpty()) {
			fields.put(FieldType.UDF6.getName(), Constants.Y_FLAG.getValue());
		}
		fields.logAllFields("Refine Request:");
		Map<String, String> responseMap = router.route(fields);
		responseMap.remove(FieldType.INTERNAL_CUSTOM_MDC.getName());
		logger.info("Post settlement auto refund Response : " + responseMap);
	}

	private Map<String, String> createRequestMap(Document doc) {
		Map<String, String> requestMap = new HashMap<>();
		requestMap.put(FieldType.TXNTYPE.getName(), "REFUND");
		requestMap.put(FieldType.PG_REF_NUM.getName(), doc.getString(FieldType.PG_REF_NUM.getName()));
		requestMap.put(FieldType.REFUND_FLAG.getName(), "R");

//		String amount = Amount.removeDecimalAmount(doc.getString(FieldType.AMOUNT.getName()), doc.getString(FieldType.CURRENCY_CODE.getName()));
		requestMap.put(FieldType.AMOUNT.getName(), doc.getString(FieldType.AMOUNT.getName()).replace(".", ""));
		requestMap.put(FieldType.CURRENCY_CODE.getName(), doc.getString(FieldType.CURRENCY_CODE.getName()));
		requestMap.put(FieldType.ORDER_ID.getName(), doc.getString(FieldType.ORDER_ID.getName()));
		requestMap.put(FieldType.REFUND_ORDER_ID.getName(), getNewRefundOrderId(doc.getString(FieldType.ORDER_ID.getName())));
		requestMap.put(FieldType.PAY_ID.getName(), doc.getString(FieldType.PAY_ID.getName()));
		return requestMap;
	}

	private String getNewRefundOrderId(String orderId) {
		Random rand = new Random();
		String refundOrderId = "REF-" + orderId + "-" + rand.nextInt(1000);

		BasicDBObject refundOrderIdObj = new BasicDBObject(FieldType.REFUND_ORDER_ID.getName(), refundOrderId);
		List<BasicDBObject> queryList = new ArrayList<>();
		queryList.add(refundOrderIdObj);
		BasicDBObject findQuery = new BasicDBObject("$and", queryList);
		MongoDatabase dbIns = mongoInstance.getDB();
		MongoCollection<Document> collection = dbIns.getCollection(PropertiesManager.propertiesMap.get(PREFIX + Constants.COLLECTION_NAME.getValue()));
		long count = 0;
		while(true) {
			logger.info("Refund Order Id Find Query : " + findQuery);
			count = collection.countDocuments(findQuery);
			if(count == 0) {
				break;
			}
			refundOrderId = "REF-" + orderId + "-" + rand.nextInt(1000);
			queryList.clear();
			queryList.add(new BasicDBObject(FieldType.REFUND_ORDER_ID.getName(), refundOrderId));
		}
		return refundOrderId;
	}

	private boolean getAutoRefundPostSettlementFlag(String payId) {
		User user = staticDataProvider.getUserData(payId);
		if (user == null) {
			logger.error("Invalid user with payId : " + payId);
			return false;
		}
		return user.isEnableAutoRefundPostSettlement();
	}

	private Document getDocument(String pgRefNum) {
		BasicDBObject pgRefNumObj = new BasicDBObject(FieldType.PG_REF_NUM.getName(), pgRefNum);
		BasicDBObject statusObj = new BasicDBObject(FieldType.STATUS.getName(), StatusType.CAPTURED.getName());
		BasicDBObject txnTypeObj = new BasicDBObject(FieldType.TXNTYPE.getName(), TxnType.SALE.getName());
		List<BasicDBObject> queryList = new ArrayList<>();
		queryList.add(pgRefNumObj);
		queryList.add(statusObj);
		queryList.add(txnTypeObj);
		BasicDBObject findQuery = new BasicDBObject("$and", queryList);
		MongoDatabase dbIns = mongoInstance.getDB();
		MongoCollection<Document> collection = dbIns
				.getCollection(PropertiesManager.propertiesMap.get(PREFIX + Constants.COLLECTION_NAME.getValue()));

		FindIterable<Document> output = collection.find(findQuery);
		MongoCursor<Document> cursor = output.iterator();
		Document doc = null;
		if (cursor.hasNext()) {
			doc = cursor.next();
		}

		return doc;
	}
}
