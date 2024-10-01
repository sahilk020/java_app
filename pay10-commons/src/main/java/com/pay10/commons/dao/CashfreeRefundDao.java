package com.pay10.commons.dao;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.bson.Document;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.mongodb.BasicDBObject;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import com.pay10.commons.exception.ErrorType;
import com.pay10.commons.exception.SystemException;
import com.pay10.commons.mongo.MongoInstance;
import com.pay10.commons.user.CashfreeRefundDTO;
import com.pay10.commons.user.CashfreeTxnRespDTO;
import com.pay10.commons.util.CrmFieldType;
import com.pay10.commons.util.FieldType;
import com.pay10.commons.util.TransactionType;

@Component
public class CashfreeRefundDao {
	private static Logger logger = LoggerFactory.getLogger(CashfreeRefundDao.class.getName());

	@Autowired
	private MongoInstance mongoInstance;
	private static final String PREFIX = "MONGO_DB_";

	public List<CashfreeRefundDTO> getAllPendingRefundTxn() throws SystemException {

		List<CashfreeRefundDTO> refundList = new ArrayList<CashfreeRefundDTO>();
		MongoCursor<Document> cursor = null;
		try {

			MongoDatabase dbIns = mongoInstance.getDB();
			MongoCollection<Document> collection = dbIns.getCollection("transaction");

			List<BasicDBObject> findQuery = new ArrayList<BasicDBObject>();
			findQuery.add(new BasicDBObject(FieldType.ACQUIRER_TYPE.getName(), "CASHFREE"));
			findQuery.add(new BasicDBObject(FieldType.TXNTYPE.getName(), TransactionType.REFUND.getName()));
			findQuery.add(new BasicDBObject(FieldType.STATUS.getName(), "Pending"));
			BasicDBObject fetchQuery = new BasicDBObject("$and", findQuery);

			logger.info("Query for find invoice by Id : " + findQuery);
			FindIterable<Document> output = collection.find(fetchQuery);
			cursor = output.iterator();

			while (cursor.hasNext()) {
				Document documentObj = (Document) cursor.next();
				logger.info("documentObj "+documentObj.toString());
				if (null != documentObj) {
					CashfreeRefundDTO refundInfo = new CashfreeRefundDTO();
				//	refundInfo.setId((Long) documentObj.get("_id"));
					refundInfo.setOrderId(documentObj.get("ORDER_ID").toString());
					refundInfo.setPayId(documentObj.get("PAY_ID").toString());
					refundInfo.setPgRefNo(documentObj.get("PG_REF_NUM").toString());
					refundInfo.setRefundOrderId(documentObj.get("REFUND_ORDER_ID").toString());

					refundList.add(refundInfo);
				}
			}
			logger.info("Got Cashfree Pending Refund count : " + refundList.size());

			return refundList;
		} catch (Exception exception) {
			String message = "Error while reading list of cashfree refund based on TXNTYPE, STATUS from database";
			logger.error(message, exception);
			throw new SystemException(ErrorType.DATABASE_ERROR, exception, message);
		} finally {
			cursor.close();
		}

	}
	
	public CashfreeTxnRespDTO getCashfreeTxnStatus(Map<String , String> reqMap) throws SystemException {

		//Map<String, String> response = new HashMap<String, String>();
		CashfreeTxnRespDTO response = new CashfreeTxnRespDTO();
		MongoCursor<Document> cursor = null;
		try {

			MongoDatabase dbIns = mongoInstance.getDB();
			MongoCollection<Document> collection = dbIns.getCollection("transaction");

			List<BasicDBObject> findQuery = new ArrayList<BasicDBObject>();
			findQuery.add(new BasicDBObject(FieldType.ACQUIRER_TYPE.getName(), "CASHFREE"));
			findQuery.add(new BasicDBObject(FieldType.TXNTYPE.getName(), TransactionType.SALE.getName()));
			findQuery.add(new BasicDBObject(FieldType.PG_REF_NUM.getName(), reqMap.get("pgRefNo")));
			findQuery.add(new BasicDBObject(FieldType.PAY_ID.getName(), reqMap.get("payId")));
			
			BasicDBObject fetchQuery = new BasicDBObject("$and", findQuery);
			
			BasicDBObject sortQuery = new BasicDBObject();
			sortQuery.append(FieldType.CREATE_DATE.getName(), -1);
			
			logger.info("Query for find invoice by Id : " + findQuery);
			FindIterable<Document> output = collection.find(fetchQuery).sort(sortQuery);
			
			//logger.info("Query for find invoice by Id : " + findQuery);
			//FindIterable<Document> output = collection.find(fetchQuery);
			cursor = output.iterator();

			if (cursor.hasNext()) {
				Document documentObj = (Document) cursor.next();
				//logger.info("documentObj "+documentObj.toString());
				if (null != documentObj) {
					
					response.setAmount(documentObj.getString("AMOUNT"));
					response.setAutoCode(documentObj.getString("AUTH_CODE"));
					response.setCustEmail(documentObj.getString("CUST_EMAIL"));
					response.setCustPhone(documentObj.getString("CUST_PHONE"));
					response.setMopType(documentObj.getString("MOP_TYPE"));
					response.setPaymentType(documentObj.getString("PAYMENT_TYPE"));
					//response.setRequestTime(documentObj.getString("CREATE_DATE"));
					response.setResponseCode(documentObj.getString("RESPONSE_CODE"));
					response.setResponseMessage(documentObj.getString("RESPONSE_MESSAGE"));
					response.setResponseTime(documentObj.getString("UPDATE_DATE"));
					response.setStatus(documentObj.getString("STATUS"));
					response.setTotalAmount(documentObj.getString("TOTAL_AMOUNT"));
					response.setOrderId(documentObj.getString("ORDER_ID"));
					response.setPayId(documentObj.getString("PAY_ID"));
					response.setPgRefNo(documentObj.getString("PG_REF_NUM"));
					//response.setRefundOrderId(documentObj.getString("REFUND_ORDER_ID"));
					response.setTxnType(documentObj.getString("TXNTYPE"));
				}
			}
			logger.info("Cashfree status response  : " + response);

			return response;
		} catch (Exception exception) {
			String message = "Error while reading Cashfree status response";
			logger.error(message, exception);
			throw new SystemException(ErrorType.DATABASE_ERROR, exception, message);
		} finally {
			cursor.close();
		}

	}

	public void updateRefundStatus(JSONObject refundResp) throws SystemException {

		try {
			MongoDatabase dbIns = mongoInstance.getDB();
			MongoCollection<Document> collection = dbIns.getCollection("transaction");
			MongoCollection<Document> collectionStatus = dbIns.getCollection("transactionStatus");
			
					
			BasicDBObject updateQuery = new BasicDBObject();
			//List<BasicDBObject> newDocument = new ArrayList<BasicDBObject>();
			if(refundResp.getString("refund_status").equalsIgnoreCase("SUCCESS")) {
				
				updateQuery.append(FieldType.STATUS.getName(), "Captured");
				updateQuery.append(FieldType.RESPONSE_MESSAGE.getName(), "SUCCESS");
				updateQuery.append(FieldType.RESPONSE_CODE.getName(), "000");
				updateQuery.append(FieldType.PG_RESP_CODE.getName(), "OK");
				updateQuery.append(FieldType.PG_TXN_MESSAGE.getName(), "SUCCESS");
			}else {
				updateQuery.append(FieldType.STATUS.getName(), "Failed");
				updateQuery.append(FieldType.RESPONSE_MESSAGE.getName(), "FAILED");
				updateQuery.append(FieldType.RESPONSE_CODE.getName(), "022");
				//newDocument.append("$set", new BasicDBObject().append(FieldType.PG_RESP_CODE.getName(), "OK"));
				updateQuery.append(FieldType.PG_TXN_MESSAGE.getName(), "FAILED");
			}
			

			List<BasicDBObject> findQuery = new ArrayList<BasicDBObject>();
			findQuery.add(new BasicDBObject(FieldType.ACQUIRER_TYPE.getName(), "CASHFREE"));
			findQuery.add(new BasicDBObject(FieldType.TXNTYPE.getName(), TransactionType.REFUND.getName()));
			findQuery.add(new BasicDBObject(FieldType.STATUS.getName(), "Pending"));
			findQuery.add(new BasicDBObject(FieldType.PG_REF_NUM.getName(), refundResp.getString("order_id")));
			findQuery.add(new BasicDBObject(FieldType.REFUND_ORDER_ID.getName(), refundResp.getString("refund_id")));

			
			BasicDBObject setQuery = new BasicDBObject("$set", updateQuery);
			BasicDBObject searchQuery = new BasicDBObject("$and", findQuery);
			logger.info("searchQuery :: "+searchQuery);
			logger.info("newDocument :: "+setQuery);
			collection.updateMany(searchQuery, setQuery);
			collectionStatus.updateMany(searchQuery, setQuery);

		} catch (Exception exception) {
			String message = "Error while updating refund response";
			logger.error(message, exception);
			throw new SystemException(ErrorType.DATABASE_ERROR, exception, message);
		}

	}
}
