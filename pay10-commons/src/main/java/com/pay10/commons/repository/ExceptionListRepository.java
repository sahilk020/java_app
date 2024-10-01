package com.pay10.commons.repository;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.bson.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mongodb.BasicDBObject;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.result.UpdateResult;
import com.pay10.commons.action.AbstractSecureAction;
import com.pay10.commons.api.Hasher;
import com.pay10.commons.api.TransactionControllerServiceProvider;
import com.pay10.commons.audittrail.ActionMessageByAction;
import com.pay10.commons.audittrail.AuditTrail;
import com.pay10.commons.audittrail.AuditTrailService;
import com.pay10.commons.dto.RefundPayload;
import com.pay10.commons.exception.SystemException;
import com.pay10.commons.mongo.MongoInstance;
import com.pay10.commons.util.Amount;
import com.pay10.commons.util.Constants;
import com.pay10.commons.util.FieldType;
import com.pay10.commons.util.Fields;
import com.pay10.commons.util.PropertiesManager;
import com.pay10.commons.util.StatusType;
import com.pay10.commons.util.TransactionManager;
import com.pay10.commons.util.TransactionType;

@Service
public class ExceptionListRepository {

	// private static final long serialVersionUID = -7556403361630339573L;
//	@Autowired
//	private AuditTrailService auditTrailService;
//	

	private String orderId;
	private String refundFlag;
	private String refundAmount;
	private String pgRefNum;
	private String refundOrderId;
	private String currencyCode;
	private String payId;
	private String response;
	private String saleAmount;
	private String refundDate;

	public String getRefundDate() {
		return refundDate;
	}

	public void setRefundDate(String refundDate) {
		this.refundDate = refundDate;
	}

	public String getSaleAmount() {
		return saleAmount;
	}

	public void setSaleAmount(String saleAmount) {
		this.saleAmount = saleAmount;
	}

	public String getResponse() {
		return response;
	}

	public void setResponse(String response) {
		this.response = response;
	}

	protected ObjectMapper mapper = new ObjectMapper();
	@Autowired
	private MongoInstance mongoInstance;

	@Autowired
	private TransactionControllerServiceProvider transactionControllerServiceProvider;

	private static final String prefix = "MONGO_DB_";
	private static Logger logger = LoggerFactory.getLogger(ExceptionListRepository.class.getName());

	public String updateRns(String pgRefNum) {
		logger.info("Update Rns status for PgRefNumber : " + pgRefNum);
		Date date = new Date();
		String dateIndex = new SimpleDateFormat("yyyyMMdd").format(date);
		String createDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(date);

		updateTransactionStatusCollection(pgRefNum, dateIndex, createDate);
		updateTransactionCollection(pgRefNum);
		return "Successfully RNS Marked";
	}

	public String refundProcess(String pgRefNum) throws SystemException, JsonProcessingException {

		System.out.println("Refund Request for pg_ref number : " + pgRefNum);
		String status = "";

		MongoDatabase dbIns = mongoInstance.getDB();
		MongoCollection<Document> collection = dbIns.getCollection(
				PropertiesManager.propertiesMap.get(prefix + Constants.TRANSACTION_STATUS_COLLECTION.getValue()));

		// validate refund
		BasicDBObject refund = new BasicDBObject();
		refund.put(FieldType.TXNTYPE.getName(), TransactionType.REFUND.getName());
		refund.put(FieldType.PG_REF_NUM.getName(), pgRefNum.trim());
		List<BasicDBObject> refundquery = Arrays.asList(new BasicDBObject("$match", refund));
		logger.info("Query For fetch Transaction : " + refund);

		MongoCursor<Document> refundCursor = collection.aggregate(refundquery).iterator();

		if (refundCursor.hasNext()) {
			status = "Refund Order Id already present!";
			return status;
		}

		// Refund

		BasicDBObject match = new BasicDBObject();
		match.put(FieldType.TXNTYPE.getName(), TransactionType.SALE.getName());
		match.put(FieldType.PG_REF_NUM.getName(), pgRefNum.trim());
		List<BasicDBObject> query = Arrays.asList(new BasicDBObject("$match", match));
		logger.info("Query For fetch Transaction : " + query);

		MongoCursor<Document> cursor = collection.aggregate(query).iterator();

		while (cursor.hasNext()) {
			Document document = (Document) cursor.next();
			setOrderId(String.valueOf(document.get(FieldType.ORDER_ID.getName())));
			setRefundAmount(String.valueOf(document.get(FieldType.AMOUNT.getName())));
			setPgRefNum(pgRefNum);
			setPayId(String.valueOf(document.get(FieldType.PAY_ID.getName())));
			setCurrencyCode(String.valueOf(document.get(FieldType.CURRENCY_CODE.getName())));
		}

		logger.info("Refund Amount :" + getRefundAmount());
		logger.info("Currency Code :" + getCurrencyCode());

		if (!StringUtils.isBlank(getOrderId()) && !StringUtils.isBlank(getPayId()) && !StringUtils.isBlank(getPayId())
				&& !StringUtils.isBlank(getCurrencyCode())) {
			Fields fields = new Fields();
			fields.put(FieldType.ORDER_ID.getName(), getOrderId());
			fields.put(FieldType.REFUND_FLAG.getName(), "C");
			fields.put(FieldType.AMOUNT.getName(), (Amount.formatAmount(getRefundAmount(), getCurrencyCode())));
			fields.put(FieldType.PG_REF_NUM.getName(), getPgRefNum());
			fields.put(FieldType.REFUND_ORDER_ID.getName(), "Refund" + TransactionManager.getNewTransactionId());
			fields.put(FieldType.CURRENCY_CODE.getName(), getCurrencyCode());
			fields.put(FieldType.TXNTYPE.getName(), TransactionType.REFUND.getName());
			fields.put(FieldType.PAY_ID.getName(), getPayId());

			fields.put(FieldType.HASH.getName(), Hasher.getHash(TransactionManager.getNewTransactionId()));

			logger.info("Crm Refund Request : " + fields.getFieldsAsString());

			String refundurl = Constants.TXN_WS_InterNalRefund.getValue();
			Map<String, String> response = transactionControllerServiceProvider.transact(fields, refundurl);
			logger.info("Crm Refund Response " + response);
			if (response.isEmpty()) {
				status = "Refund not initiated !!";
			} else {
				status = "Response Status is :" + response.get(FieldType.STATUS.getName());
			}

		} else {
			status = "Transaction Not Found";
		}
		logger.info("Return Response");
		return status;
	}

	private String getPayload() throws JsonProcessingException {
		RefundPayload payload = new RefundPayload();
		payload.setOrderId(getOrderId());
		payload.setResponse(getResponse());
		payload.setRefundAmount(getRefundAmount());
		payload.setSaleAmount(getRefundAmount());
		payload.setPgRefNum(getPgRefNum());
		payload.setRefundOrderId(getRefundOrderId());
		payload.setCurrencyCode(getCurrencyCode());
		payload.setRefundFlag(getRefundFlag());
		payload.setPayId(getPayId());
		payload.setRefundDate(new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").format(new Date()));
		return mapper.writeValueAsString(payload);
	}

	String updateTransactionStatusCollection(String pgRefNum, String dateIndex, String createDate) {
		MongoDatabase dbIns = mongoInstance.getDB();
		MongoCollection<Document> collection = dbIns.getCollection(
				PropertiesManager.propertiesMap.get(prefix + Constants.TRANSACTION_STATUS_COLLECTION.getValue()));

		BasicDBObject newDocument = new BasicDBObject();
		newDocument.append("$set",
				new BasicDBObject().append(FieldType.STATUS.getName(), StatusType.SETTLED_RECONCILLED.getName())
						.append(FieldType.ALIAS_STATUS.getName(), StatusType.SETTLED_RECONCILLED.getName())
						.append(FieldType.SETTLEMENT_DATE_INDEX.getName(), dateIndex)
						.append(FieldType.SETTLEMENT_DATE.getName(), createDate));

		BasicDBObject searchQuery = new BasicDBObject().append(FieldType.PG_REF_NUM.getName(), pgRefNum);

		logger.info("Query Of set : " + newDocument);
		logger.info("Query Of where : " + searchQuery);

		UpdateResult result = collection.updateMany(searchQuery, newDocument);

		return "Successfully Force Captured the Transaction";
	}

	String updateTransactionCollection(String pgRefNum) {

		MongoDatabase dbIns = mongoInstance.getDB();
		MongoCollection<Document> collection = dbIns
				.getCollection(PropertiesManager.propertiesMap.get(prefix + Constants.COLLECTION_NAME.getValue()));

		BasicDBObject match = new BasicDBObject();
		match.put(FieldType.PG_REF_NUM.getName(), pgRefNum.trim());
		match.put(FieldType.STATUS.getName(), StatusType.FORCE_CAPTURED.getName());
		List<BasicDBObject> query = Arrays.asList(new BasicDBObject("$match", match));
		logger.info("Query For fetch Transaction : " + query);

		MongoCursor<Document> cursor = collection.aggregate(query).iterator();

		Document updateDocument = null;

		while (cursor.hasNext()) {
			Document document = (Document) cursor.next();
			updateDocument = document;
			break;
		}
		Date date = new Date();
		String dateIndex = new SimpleDateFormat("yyyyMMdd").format(date);
		String createDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(date);

		updateDocument.put(FieldType.STATUS.getName(), StatusType.CAPTURED.getName());
		updateDocument.put(FieldType.ALIAS_STATUS.getName(), StatusType.CAPTURED.getName());
		updateDocument.put(FieldType.DATE_INDEX.getName(), dateIndex);
		updateDocument.put(FieldType.CREATE_DATE.getName(), createDate);
		String id = TransactionManager.getNewTransactionId();
		updateDocument.put("_id", id);
		String newTxnId = TransactionManager.getNewTransactionId();
		updateDocument.put(FieldType.TXN_ID.getName(), newTxnId);

		collection.insertOne(updateDocument);

		Date date1 = new Date();
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date1);
		calendar.add(Calendar.SECOND, 1);

		String dateIndex1 = new SimpleDateFormat("yyyyMMdd").format(calendar.getTime());
		String createDate1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(calendar.getTime());
		updateDocument.put(FieldType.STATUS.getName(), StatusType.SETTLED_RECONCILLED.getName());
		updateDocument.put(FieldType.ALIAS_STATUS.getName(), StatusType.SETTLED_RECONCILLED.getName());
		updateDocument.put(FieldType.DATE_INDEX.getName(), dateIndex1);
		updateDocument.put(FieldType.CREATE_DATE.getName(), createDate1);
		String idd = TransactionManager.getNewTransactionId();
		updateDocument.put("_id", idd);
		String newTxnIdd = TransactionManager.getNewTransactionId();
		updateDocument.put(FieldType.TXN_ID.getName(), newTxnIdd);
		collection.insertOne(updateDocument);

		return "Successfully Force Captured the Transaction";
	}

	public int checkPgRefNumberRNS(List<String> list) {

		MongoDatabase dbIns = mongoInstance.getDB();
		MongoCollection<Document> collection = dbIns
				.getCollection(PropertiesManager.propertiesMap.get(prefix + Constants.COLLECTION_NAME.getValue()));
		BasicDBObject basicDBObject = new BasicDBObject();
		basicDBObject.put(FieldType.PG_REF_NUM.getName(), new BasicDBObject("$in", list));
		basicDBObject.put("$and",
				new BasicDBObject[] {
						new BasicDBObject(FieldType.STATUS.getName(), StatusType.FORCE_CAPTURED.getName()),
						new BasicDBObject(FieldType.STATUS.getName(),
								new BasicDBObject("$ne", StatusType.SETTLED_RECONCILLED.getName())) });

		List<BasicDBObject> query = Arrays.asList(new BasicDBObject("$match", basicDBObject));
		logger.info("Query For fetch Transaction : " + query);
		int size = collection.aggregate(query).into(new ArrayList<>()).size();

		return size;
	}

	public int checkPgRefNumberRefund(List<String> list) {

		List<String> orderID = new ArrayList<>();

		MongoDatabase dbIns = mongoInstance.getDB();
		MongoCollection<Document> collection = dbIns
				.getCollection(PropertiesManager.propertiesMap.get(prefix + Constants.COLLECTION_NAME.getValue()));
		BasicDBObject basicDBObject = new BasicDBObject();
		basicDBObject.put(FieldType.PG_REF_NUM.getName(), new BasicDBObject("$in", list));

		List<BasicDBObject> query = Arrays.asList(new BasicDBObject("$match", basicDBObject),
				new BasicDBObject("$group",
						new BasicDBObject("_id", new BasicDBObject(FieldType.ORDER_ID.getName(), "$ORDER_ID"))),
				new BasicDBObject("$project", new BasicDBObject(FieldType.ORDER_ID.getName(), "$_id.ORDER_ID")));
		logger.info("Query For fetch OrderId from Pgrefnum : " + query);
		MongoCursor<Document> cursor = collection.aggregate(query).iterator();
		while (cursor.hasNext()) {
			Document document = (Document) cursor.next();
			String oid = document.getString("ORDER_ID");
			orderID.add(oid);
		}

		if (orderID.size() > 0) {
			BasicDBObject basicDBObject1 = new BasicDBObject();
			basicDBObject1.put(FieldType.ORDER_ID.getName(), new BasicDBObject("$in", orderID));
			basicDBObject1.put("$and",
					new BasicDBObject[] {
							new BasicDBObject(FieldType.TXNTYPE.getName(), TransactionType.SALE.getName()),
							new BasicDBObject(FieldType.TXNTYPE.getName(),
									new BasicDBObject("$ne", TransactionType.REFUND.getName())) });

			List<BasicDBObject> query1 = Arrays.asList(new BasicDBObject("$match", basicDBObject1));
			logger.info("Query For check orderid not already refunded : " + query1);
			return collection.aggregate(query1).into(new ArrayList<>()).size();

		} else {
			return 0;

		}
	}


	public String getOrderId() {
		return orderId;
	}

	public void setOrderId(String orderId) {
		this.orderId = orderId;
	}

	public String getRefundFlag() {
		return refundFlag;
	}

	public void setRefundFlag(String refundFlag) {
		this.refundFlag = refundFlag;
	}

	public String getRefundAmount() {
		return refundAmount;
	}

	public void setRefundAmount(String refundAmount) {
		this.refundAmount = refundAmount;
	}

	public String getPgRefNum() {
		return pgRefNum;
	}

	public void setPgRefNum(String pgRefNum) {
		this.pgRefNum = pgRefNum;
	}

	public String getRefundOrderId() {
		return refundOrderId;
	}

	public void setRefundOrderId(String refundOrderId) {
		this.refundOrderId = refundOrderId;
	}

	public String getCurrencyCode() {
		return currencyCode;
	}

	public void setCurrencyCode(String currencyCode) {
		this.currencyCode = currencyCode;
	}

	public String getPayId() {
		return payId;
	}

	public void setPayId(String payId) {
		this.payId = payId;
	}

}
