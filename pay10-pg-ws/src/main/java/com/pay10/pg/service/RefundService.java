package com.pay10.pg.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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
import com.pay10.commons.util.Constants;
import com.pay10.commons.util.FieldType;
import com.pay10.commons.util.Fields;
import com.pay10.commons.util.PropertiesManager;
import com.pay10.commons.util.TransactionType;

@Service
public class RefundService {
	private static Logger logger = LoggerFactory.getLogger(RefundService.class.getName());

	@Autowired
	private MongoInstance mongoInstance;

	@Autowired
	PropertiesManager propertiesManager;

	private static final String prefix = "MONGO_DB_";

	public Fields checkRefundState(Fields fields) {

		MongoCursor<Document> cursor = null;
		try {

			MongoDatabase dbIns = mongoInstance.getDB();
			MongoCollection<Document> collection = dbIns
					.getCollection(PropertiesManager.propertiesMap.get(prefix + Constants.COLLECTION_NAME.getValue()));

			List<BasicDBObject> findQuery = new ArrayList<BasicDBObject>();
			findQuery.add(new BasicDBObject(FieldType.TXNTYPE.getName(), TransactionType.REFUND.getName()));
			findQuery.add(new BasicDBObject(FieldType.STATUS.getName(), "REFUND_INITIATED"));
			findQuery
					.add(new BasicDBObject(FieldType.PG_REF_NUM.getName(), fields.get(FieldType.PG_REF_NUM.getName())));
			findQuery.add(new BasicDBObject(FieldType.REFUND_ORDER_ID.getName(),
					fields.get(FieldType.REFUND_ORDER_ID.getName())));
			findQuery.add(new BasicDBObject(FieldType.ORDER_ID.getName(), fields.get(FieldType.ORDER_ID.getName())));

			BasicDBObject fetchQuery = new BasicDBObject("$and", findQuery);

			logger.info("Query for find to check refund status by Id : " + fetchQuery);
			FindIterable<Document> output = collection.find(fetchQuery);
			cursor = output.iterator();

			if (cursor.hasNext()) {
				Document documentObj = (Document) cursor.next();
				logger.info("documentObj " + documentObj.toString());
				if (null != documentObj) {
					fields.put(FieldType.STATUS.getName(), "Send to Bank");
					fields.put(FieldType.RESPONSE_CODE.getName(), "026");
					fields.put(FieldType.RESPONSE_MESSAGE.getName(),
							"Refund already initiated, Now is in Send to Bank state !!");
				}
			}

		} catch (Exception exception) {
			String message = "Error while refund from crm portal ";
			logger.error(message, exception);
		} finally {
			cursor.close();
		}
		return fields;

	}

	public Map<String, String> refundStatusInquiry(String orderId, String payId, String refundOrderId) {

		Map<String, String> responseMap = null;
		Fields fields = new Fields();
		List<BasicDBObject> dbObjList = new ArrayList<BasicDBObject>();
		List<BasicDBObject> txnTypeConditionLst = new ArrayList<BasicDBObject>();
		dbObjList.add(new BasicDBObject(FieldType.REFUND_ORDER_ID.getName(), refundOrderId));
		dbObjList.add(new BasicDBObject(FieldType.PAY_ID.getName(), payId));
		dbObjList.add(new BasicDBObject(FieldType.ORDER_ID.getName(), orderId));
		BasicDBObject txnTypeQuery = new BasicDBObject();

		txnTypeConditionLst.add(new BasicDBObject(FieldType.TXNTYPE.getName(), TransactionType.REFUND.getName()));
		txnTypeConditionLst.add(new BasicDBObject(FieldType.TXNTYPE.getName(), TransactionType.REFUNDRECO.getName()));

		txnTypeQuery.append("$or", txnTypeConditionLst);

		dbObjList.add(txnTypeQuery);
		BasicDBObject andQuery = new BasicDBObject("$and", dbObjList);
		MongoDatabase dbIns = mongoInstance.getDB();
		MongoCollection<Document> coll = dbIns.getCollection(
				propertiesManager.propertiesMap.get(prefix + Constants.TRANSACTION_STATUS_COLLECTION.getValue()));

		FindIterable<Document> cursor = coll.find(andQuery).sort(new BasicDBObject("CREATE_DATE", -1)).limit(1);

		MongoCursor<Document> cursor2 = cursor.iterator();

		if (cursor2.hasNext()) {
			Document documentObj = cursor2.next();

			if (null != documentObj) {

				if (documentObj.get(FieldType.RESPONSE_DATE_TIME.getName()) != null) {
					fields.put(FieldType.RESPONSE_DATE_TIME.getName(),
							documentObj.get(FieldType.RESPONSE_DATE_TIME.getName()).toString());
				}

				if (documentObj.get(FieldType.RESPONSE_CODE.getName()) != null) {
					fields.put(FieldType.RESPONSE_CODE.getName(),
							documentObj.get(FieldType.RESPONSE_CODE.getName()).toString());
				}
				if (documentObj.get(FieldType.REFUND_FLAG.getName()) != null) {
					fields.put(FieldType.REFUND_FLAG.getName(),
							documentObj.get(FieldType.REFUND_FLAG.getName()).toString());
				}
				if (documentObj.get(FieldType.MOP_TYPE.getName()) != null) {
					fields.put(FieldType.MOP_TYPE.getName(), documentObj.get(FieldType.MOP_TYPE.getName()).toString());
				}
				if (documentObj.get(FieldType.PG_TXN_MESSAGE.getName()) != null) {
					fields.put(FieldType.PG_TXN_MESSAGE.getName(),
							documentObj.get(FieldType.PG_TXN_MESSAGE.getName()).toString());
				}
				if (documentObj.get(FieldType.STATUS.getName()) != null) {
					fields.put(FieldType.STATUS.getName(), documentObj.get(FieldType.STATUS.getName()).toString());
				}
				if (documentObj.get(FieldType.PG_REF_NUM.getName()) != null) {
					fields.put(FieldType.PG_REF_NUM.getName(),
							documentObj.get(FieldType.PG_REF_NUM.getName()).toString());
				}
				if (documentObj.get(FieldType.AMOUNT.getName()) != null) {
					fields.put(FieldType.AMOUNT.getName(), documentObj.get(FieldType.AMOUNT.getName()).toString());
				}
				if (documentObj.get(FieldType.RESPONSE_MESSAGE.getName()) != null) {
					fields.put(FieldType.RESPONSE_MESSAGE.getName(),
							documentObj.get(FieldType.RESPONSE_MESSAGE.getName()).toString());
				}
				if (documentObj.get(FieldType.REFUND_ORDER_ID.getName()) != null) {
					fields.put(FieldType.REFUND_ORDER_ID.getName(),
							documentObj.get(FieldType.REFUND_ORDER_ID.getName()).toString());
				}
				if (documentObj.get(FieldType.ACQ_ID.getName()) != null) {
					fields.put(FieldType.ACQ_ID.getName(), documentObj.get(FieldType.ACQ_ID.getName()).toString());
				}
				if (documentObj.get(FieldType.TXNTYPE.getName()) != null) {
					fields.put(FieldType.TXNTYPE.getName(), documentObj.get(FieldType.TXNTYPE.getName()).toString());
				}

				if (documentObj.get(FieldType.SURCHARGE_FLAG.getName()) != null) {
					fields.put(FieldType.SURCHARGE_FLAG.getName(),
							documentObj.get(FieldType.SURCHARGE_FLAG.getName()).toString());
				}
				if (documentObj.get(FieldType.PAYMENT_TYPE.getName()) != null) {
					fields.put(FieldType.PAYMENT_TYPE.getName(),
							documentObj.get(FieldType.PAYMENT_TYPE.getName()).toString());
				}
				if (documentObj.get(FieldType.PAY_ID.getName()) != null) {
					fields.put(FieldType.PAY_ID.getName(), documentObj.get(FieldType.PAY_ID.getName()).toString());
				}
				if (documentObj.get(FieldType.ORDER_ID.getName()) != null) {
					fields.put(FieldType.ORDER_ID.getName(), documentObj.get(FieldType.ORDER_ID.getName()).toString());
				}
			}
		}
		cursor2.close();

		responseMap = fields.getFields();
		if (responseMap.isEmpty()) {
			responseMap.put(FieldType.ORDER_ID.getName(), orderId);
			responseMap.put(FieldType.REFUND_ORDER_ID.getName(), refundOrderId);
			responseMap.put(FieldType.PAY_ID.getName(), payId);
			responseMap.put(FieldType.RESPONSE_MESSAGE.getName(), "NO DATA FOUND");
			responseMap.put(FieldType.RESPONSE_CODE.getName(), "300");

		}

		logger.info("Final Refund Status Inquiry ResponseMap :: " + responseMap);
		return responseMap;

	}
}
