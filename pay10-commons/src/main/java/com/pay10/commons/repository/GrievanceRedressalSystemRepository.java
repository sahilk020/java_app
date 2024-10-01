package com.pay10.commons.repository;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.bson.BsonNull;
import org.bson.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.itextpdf.io.IOException;
import com.itextpdf.text.pdf.PdfStructTreeController.returnType;
import com.mongodb.BasicDBObject;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.result.UpdateResult;
import com.pay10.commons.dto.BulkUploadLiabilityHoldAndRelease;
import com.pay10.commons.dto.GRS;
import com.pay10.commons.dto.GrievanceRedressalSystemDto;
import com.pay10.commons.dto.GrsIssueHistoryDto;
import com.pay10.commons.mongo.MongoInstance;
import com.pay10.commons.user.User;
import com.pay10.commons.user.UserDao;
import com.pay10.commons.util.Constants;
import com.pay10.commons.util.FieldType;
import com.pay10.commons.util.MopType;
import com.pay10.commons.util.PaymentType;
import com.pay10.commons.util.PropertiesManager;
import com.pay10.commons.util.TransactionManager;

@Service
public class GrievanceRedressalSystemRepository {
	@Autowired
	private MongoInstance mongoInstance;
	private static final String prefix = "MONGO_DB_";
	private static Logger logger = LoggerFactory.getLogger(GrievanceRedressalSystemRepository.class.getName());
	String filePath = PropertiesManager.propertiesMap.get("GRS_PATH");
	//String filePath = "D://bilkpayout//";
	@Autowired
	private UserDao dao;

	public void insert(String uniqueNumber, String grsId, String emailId, Path filePath) {
		String msg = "failed";
		try {
			MongoDatabase dbIns = mongoInstance.getDB();
			MongoCollection<Document> collection = dbIns.getCollection(PropertiesManager.propertiesMap
					.get(prefix + Constants.GrievanceRedressalSystemDocUpload.getValue()));

			String date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());

			Document document = new Document("_id", uniqueNumber).append(FieldType.GRSID.getName(), grsId)
					.append(FieldType.UPDATEDBY.getName(), emailId).append(FieldType.UPDATEDAT.getName(), date)
					.append(FieldType.PATH.getName(), String.valueOf(filePath));

			collection.insertOne(document);

		} catch (Exception e) {
			logger.info("Exception Occur in insert()", e);
			e.printStackTrace();
		}
	}

	public boolean saveRedressal(GrievanceRedressalSystemDto grievanceRedressalSystemDto) {
		boolean flag = false;
		try {
			MongoDatabase dbIns = mongoInstance.getDB();
			MongoCollection<Document> collection = dbIns.getCollection(
					PropertiesManager.propertiesMap.get(prefix + Constants.TRANSACTION_STATUS_COLLECTION.getValue()));

			BasicDBObject match = new BasicDBObject();
			match.put(FieldType.PG_REF_NUM.getName(),
					grievanceRedressalSystemDto.getGrievanceRedressalSystemPgrefNumber());

			List<BasicDBObject> queryExecute = Arrays.asList(new BasicDBObject("$match", match));
			logger.info("Query For find data in transactionStatus Collection :" + queryExecute);

			MongoCursor<Document> cursor = collection.aggregate(queryExecute).iterator();
			GRS grs = null;
			String uniqueNumber = TransactionManager.getNewTransactionId();
			if (cursor.hasNext()) {
				grs = new GRS();
				String date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
				Document document = (Document) cursor.next();
				grs.setAmount(document.getString(FieldType.AMOUNT.getName()));
				grs.setCreatedBy(grievanceRedressalSystemDto.getUserEmailId());
				grs.setCreatedDate(date);
				grs.setGrsTittle(grievanceRedressalSystemDto.getGrievanceRedressalSystemTittle());
				grs.setGrsDesc(grievanceRedressalSystemDto.getGrievanceRedressalSystemDescription());
				grs.setGrsId(grievanceRedressalSystemDto.getGrievanceRedressalSystemId());
				grs.setId(uniqueNumber);
				grs.setFileName(grievanceRedressalSystemDto.getFilename() != null ? grievanceRedressalSystemDto.getFilename() : "");

				String payid = document.getString(FieldType.PAY_ID.getName());
				grs.setPayId(payid);
				User user = dao.findPayId1(payid);

				grs.setMerchantName(user.getBusinessName());
				grs.setOrderId(document.getString(FieldType.ORDER_ID.getName()));
				grs.setPgrefNum(grievanceRedressalSystemDto.getGrievanceRedressalSystemPgrefNumber());
				grs.setStatus("NEW");
				grs.setTotalAmount(document.getString(FieldType.TOTAL_AMOUNT.getName()));
				grs.setTxnDate(document.getString(FieldType.CREATE_DATE.getName()));

				String paymentMthodCode = document.getString(FieldType.PAYMENT_TYPE.getName());
				if (StringUtils.isNotBlank(paymentMthodCode)) {
					grs.setPaymentMethod(PaymentType.getpaymentName(paymentMthodCode));
				} else {
					grs.setPaymentMethod("N/A");
				}

				String mopType = document.getString(FieldType.MOP_TYPE.getName());
				if (StringUtils.isNotBlank(mopType)) {
					grs.setMopType(MopType.getmopName(mopType));
				} else {
					grs.setMopType("N/A");
				}

				grs.setCustomerName(document.getString(FieldType.CUST_NAME.getName()));
				grs.setCustomerPhone(document.getString(FieldType.CUST_PHONE.getName()));
			}
			if (grs != null) {
				insertInGrievanceRedressalSystem(grs);
				insertInGrievanceRedressalSystemHistory(grs);
				flag = true;
			} else {
				flag = false;
			}

		} catch (Exception e) {
			logger.info("Exception Occur in insert()", e);
			e.printStackTrace();
		}
		return flag;
	}

	public void insertInGrievanceRedressalSystem(GRS grs) {
		MongoDatabase dbIns = mongoInstance.getDB();
		MongoCollection<Document> collection = dbIns.getCollection(
				PropertiesManager.propertiesMap.get(prefix + Constants.GrievanceRedressalSystem.getValue()));

		Document document = new Document("_id", grs.getId()).append("GRS_DESCRIPTION", grs.getGrsDesc())
				.append(FieldType.GRSID.getName(), grs.getGrsId()).append("GRS_PRIORITY", "1")
				.append(FieldType.PG_REF_NUM.getName(), grs.getPgrefNum())
				.append(FieldType.AMOUNT.getName(), grs.getAmount())
				.append(FieldType.TOTAL_AMOUNT.getName(), grs.getTotalAmount())
				.append(FieldType.STATUS.getName(), grs.getStatus())
				.append(FieldType.ORDER_ID.getName(), grs.getOrderId()).append("MERCHANT_NAME", grs.getMerchantName())
				.append(FieldType.CREATE_DATE.getName(), grs.getCreatedDate()).append("CREATED_BY", grs.getCreatedBy())
				.append(FieldType.UPDATEDBY.getName(), grs.getCreatedBy())
				.append(FieldType.UPDATEDAT.getName(), grs.getCreatedDate())
				.append("TRANSACTION_DATE", grs.getTxnDate())
				.append(FieldType.PAYMENT_TYPE.getName(), grs.getPaymentMethod())
				.append(FieldType.MOP_TYPE.getName(), grs.getMopType())
				.append(FieldType.CUST_NAME.getName(), grs.getCustomerName())
				.append(FieldType.CUST_PHONE.getName(), grs.getCustomerPhone())
				.append(FieldType.PAY_ID.getName(), grs.getPayId()).append("TITTLE", grs.getGrsTittle());

		collection.insertOne(document);

	}

	public void insertInGrievanceRedressalSystemHistory(GRS grs) {
		MongoDatabase dbIns = mongoInstance.getDB();
		MongoCollection<Document> collection = dbIns.getCollection(
				PropertiesManager.propertiesMap.get(prefix + Constants.GrievanceRedressalSystemHistory.getValue()));

		String uniqueNumber = TransactionManager.getNewTransactionId();

		Document document = new Document("_id", uniqueNumber).append("GRS_DESCRIPTION", grs.getGrsDesc())
				.append(FieldType.GRSID.getName(), grs.getGrsId())
				.append(FieldType.PG_REF_NUM.getName(), grs.getPgrefNum()).append("GRS_PRIORITY", "1")
				.append(FieldType.AMOUNT.getName(), grs.getAmount())
				.append(FieldType.TOTAL_AMOUNT.getName(), grs.getTotalAmount())
				.append(FieldType.STATUS.getName(), grs.getStatus())
				.append(FieldType.ORDER_ID.getName(), grs.getOrderId()).append("MERCHANT_NAME", grs.getMerchantName())
				.append(FieldType.CREATE_DATE.getName(), grs.getCreatedDate()).append("CREATED_BY", grs.getCreatedBy())
				.append(FieldType.UPDATEDBY.getName(), grs.getCreatedBy())
				.append(FieldType.UPDATEDAT.getName(), grs.getCreatedDate())
				.append("TRANSACTION_DATE", grs.getTxnDate())
				.append(FieldType.PAYMENT_TYPE.getName(), grs.getPaymentMethod())
				.append(FieldType.MOP_TYPE.getName(), grs.getMopType())
				.append(FieldType.CUST_NAME.getName(), grs.getCustomerName())
				.append(FieldType.CUST_PHONE.getName(), grs.getCustomerPhone())
				.append(FieldType.PAY_ID.getName(), grs.getPayId()).append("TITTLE", grs.getGrsTittle());

		collection.insertOne(document);

	}

	public boolean checkGrievanceIsExist(String pgrefNum) {
		MongoDatabase dbIns = mongoInstance.getDB();
		MongoCollection<Document> collection = dbIns.getCollection(
				PropertiesManager.propertiesMap.get(prefix + Constants.GrievanceRedressalSystem.getValue()));

		BasicDBObject match = new BasicDBObject();
		match.put(FieldType.PG_REF_NUM.getName(), pgrefNum);

		List<BasicDBObject> queryExecute = Arrays.asList(new BasicDBObject("$match", match));

		MongoCursor<Document> cursor = collection.aggregate(queryExecute).iterator();

		if (cursor.hasNext()) {
			return false;
		} else {
			return true;
		}

	}

	public boolean checkGrievanceIsExistThroughGRSID(String grsId) {
		MongoDatabase dbIns = mongoInstance.getDB();
		MongoCollection<Document> collection = dbIns.getCollection(
				PropertiesManager.propertiesMap.get(prefix + Constants.GrievanceRedressalSystem.getValue()));

		BasicDBObject match = new BasicDBObject();
		match.put(FieldType.GRSID.getName(), grsId);

		List<BasicDBObject> queryExecute = Arrays.asList(new BasicDBObject("$match", match));

		MongoCursor<Document> cursor = collection.aggregate(queryExecute).iterator();

		if (cursor.hasNext()) {
			return false;
		} else {
			return true;
		}

	}

	public boolean saveRedressalOther(GrievanceRedressalSystemDto grievanceRedressalSystemDto) {
		boolean flag = true;
		try {
			User user = null;
			if (StringUtils.isNotBlank(grievanceRedressalSystemDto.getPayId())) {
				user = dao.findByPayId(grievanceRedressalSystemDto.getPayId());
			}
			GRS grs = new GRS();
			String uniqueNumber = TransactionManager.getNewTransactionId();

			String date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
			grs.setPayId(grievanceRedressalSystemDto.getPayId() != null ? grievanceRedressalSystemDto.getPayId() : "");
			grs.setMerchantName(user != null ? user.getBusinessName() : "");
			grs.setCreatedBy(grievanceRedressalSystemDto.getUserEmailId());
			grs.setCreatedDate(date);
			grs.setGrsTittle(grievanceRedressalSystemDto.getGrievanceRedressalSystemTittle());
			grs.setGrsDesc(grievanceRedressalSystemDto.getGrievanceRedressalSystemDescription());
			grs.setGrsId(grievanceRedressalSystemDto.getGrievanceRedressalSystemId());
			grs.setFileName(
					grievanceRedressalSystemDto.getFilename() != null ? grievanceRedressalSystemDto.getFilename() : "");
			if (StringUtils.isNotBlank(grievanceRedressalSystemDto.getCustomerName()))
				grs.setCustomerName(grievanceRedressalSystemDto.getCustomerName());
			if (StringUtils.isNotBlank(grievanceRedressalSystemDto.getCustomerPhone()))
				grs.setCustomerPhone(grievanceRedressalSystemDto.getCustomerPhone());
			grs.setId(uniqueNumber);
			grs.setStatus("NEW");
			insertInGrievanceRedressalSystemOther(grs);
			insertInGrievanceRedressalSystemHistoryOther(grs);

		} catch (Exception e) {
			logger.info("Exception Occur in insert()", e);
			e.printStackTrace();
		}
		return flag;
	}

	public void insertInGrievanceRedressalSystemOther(GRS grs) {
		MongoDatabase dbIns = mongoInstance.getDB();
		MongoCollection<Document> collection = dbIns.getCollection(
				PropertiesManager.propertiesMap.get(prefix + Constants.GrievanceRedressalSystem.getValue()));

		Document document = new Document("_id", grs.getId()).append("GRS_DESCRIPTION", grs.getGrsDesc())
				.append(FieldType.GRSID.getName(), grs.getGrsId())
				.append(FieldType.PG_REF_NUM.getName(), new BsonNull()).append("GRS_PRIORITY", "0")
				.append(FieldType.AMOUNT.getName(), new BsonNull())
				.append(FieldType.TOTAL_AMOUNT.getName(), new BsonNull())
				.append(FieldType.STATUS.getName(), grs.getStatus())
				.append(FieldType.ORDER_ID.getName(), new BsonNull())
				.append("MERCHANT_NAME",
						grs.getMerchantName().equalsIgnoreCase("") ? new BsonNull() : grs.getMerchantName())
				.append(FieldType.CREATE_DATE.getName(), grs.getCreatedDate()).append("CREATED_BY", grs.getCreatedBy())
				.append(FieldType.UPDATEDBY.getName(), grs.getCreatedBy())
				.append(FieldType.UPDATEDAT.getName(), grs.getCreatedDate()).append("TRANSACTION_DATE", new BsonNull())
				.append(FieldType.PAYMENT_TYPE.getName(), new BsonNull())
				.append(FieldType.MOP_TYPE.getName(), new BsonNull())
				.append(FieldType.CUST_NAME.getName(),
						StringUtils.isNotBlank(grs.getCustomerName()) ? grs.getCustomerName() : new BsonNull())
				.append(FieldType.CUST_PHONE.getName(),
						StringUtils.isNotBlank(grs.getCustomerPhone()) ? grs.getCustomerPhone() : new BsonNull())
				.append(FieldType.PAY_ID.getName(),
						grs.getPayId().equalsIgnoreCase("") ? new BsonNull() : grs.getPayId())
				.append("TITTLE", grs.getGrsTittle());
		collection.insertOne(document);

	}

	public void insertInGrievanceRedressalSystemHistoryOther(GRS grs) {
		MongoDatabase dbIns = mongoInstance.getDB();
		MongoCollection<Document> collection = dbIns.getCollection(
				PropertiesManager.propertiesMap.get(prefix + Constants.GrievanceRedressalSystemHistory.getValue()));

		String uniqueNumber = TransactionManager.getNewTransactionId();

		Document document = new Document("_id", uniqueNumber).append("GRS_DESCRIPTION", grs.getGrsDesc())
				.append(FieldType.GRSID.getName(), grs.getGrsId())
				.append(FieldType.PG_REF_NUM.getName(), new BsonNull())
				.append(FieldType.AMOUNT.getName(), new BsonNull()).append("GRS_PRIORITY", "0")
				.append(FieldType.TOTAL_AMOUNT.getName(), new BsonNull())
				.append(FieldType.STATUS.getName(), grs.getStatus())
				.append(FieldType.ORDER_ID.getName(), new BsonNull())
				.append("MERCHANT_NAME",
						grs.getMerchantName().equalsIgnoreCase("") ? new BsonNull() : grs.getMerchantName())
				.append(FieldType.CREATE_DATE.getName(), grs.getCreatedDate()).append("CREATED_BY", grs.getCreatedBy())
				.append(FieldType.UPDATEDBY.getName(), grs.getCreatedBy())
				.append(FieldType.UPDATEDAT.getName(), grs.getCreatedDate()).append("TRANSACTION_DATE", new BsonNull())
				.append(FieldType.PAYMENT_TYPE.getName(), new BsonNull())
				.append(FieldType.MOP_TYPE.getName(), new BsonNull())
				.append(FieldType.CUST_NAME.getName(),
						StringUtils.isNotBlank(grs.getCustomerName()) ? grs.getCustomerName() : new BsonNull())
				.append(FieldType.CUST_PHONE.getName(),
						StringUtils.isNotBlank(grs.getCustomerPhone()) ? grs.getCustomerPhone() : new BsonNull())
				.append(FieldType.PAY_ID.getName(),
						grs.getPayId().equalsIgnoreCase("") ? new BsonNull() : grs.getPayId())
				.append("TITTLE", grs.getGrsTittle());

		collection.insertOne(document);

	}

	public boolean closeGrievance(GrievanceRedressalSystemDto grs) {
		MongoDatabase dbIns = mongoInstance.getDB();
		MongoCollection<Document> collection = dbIns.getCollection(
				PropertiesManager.propertiesMap.get(prefix + Constants.GrievanceRedressalSystem.getValue()));

		BasicDBObject match = new BasicDBObject();
		match.put(FieldType.GRSID.getName(), grs.getGrievanceRedressalSystemId().trim());

		List<BasicDBObject> queryExecute = Arrays.asList(new BasicDBObject("$match", match));

		logger.info("Query For find data in GrievanceRedressalSystem Collection :" + queryExecute);

		MongoCursor<Document> cursor = collection.aggregate(queryExecute).iterator();

		if (cursor.hasNext()) {
			String date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
			BasicDBObject filter = new BasicDBObject(FieldType.GRSID.getName(), grs.getGrievanceRedressalSystemId());
			BasicDBObject update = new BasicDBObject("$set", new BasicDBObject("UPDATED_BY", grs.getUserEmailId())
					.append("UPDATED_AT", date).append("STATUS", "CLOSED"));

			collection.updateOne(filter, update);

			MongoCollection<Document> collection1 = dbIns.getCollection(
					PropertiesManager.propertiesMap.get(prefix + Constants.GrievanceRedressalSystemHistory.getValue()));

			BasicDBObject match1 = new BasicDBObject();
			match1.put(FieldType.GRSID.getName(), grs.getGrievanceRedressalSystemId().trim());

			List<BasicDBObject> queryExecute1 = Arrays.asList(new BasicDBObject("$match", match1));

			logger.info("Query For find data in GrievanceRedressalSystemHistory Collection :" + queryExecute);

			MongoCursor<Document> cursor2 = collection1.aggregate(queryExecute1).iterator();
			Document document = null;
			if (cursor2.hasNext()) {
				document = (Document) cursor2.next();
				String uniqueNumber = TransactionManager.getNewTransactionId();

				document.put("_id", uniqueNumber);
				document.put("UPDATED_BY", grs.getUserEmailId());
				document.put("UPDATED_AT", date);
				document.put("STATUS", "CLOSED");

			}
			collection1.insertOne(document);
			return true;
		} else {
			return false;
		}

	}

	public boolean reOpenGrievance(GrievanceRedressalSystemDto grs) {
		MongoDatabase dbIns = mongoInstance.getDB();
		MongoCollection<Document> collection = dbIns.getCollection(
				PropertiesManager.propertiesMap.get(prefix + Constants.GrievanceRedressalSystem.getValue()));

		BasicDBObject match = new BasicDBObject();
		match.put(FieldType.GRSID.getName(), grs.getGrievanceRedressalSystemId().trim());

		List<BasicDBObject> queryExecute = Arrays.asList(new BasicDBObject("$match", match));

		logger.info("Query For find data in GrievanceRedressalSystem Collection :" + queryExecute);

		MongoCursor<Document> cursor = collection.aggregate(queryExecute).iterator();

		if (cursor.hasNext()) {
			String date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
			BasicDBObject filter = new BasicDBObject(FieldType.GRSID.getName(), grs.getGrievanceRedressalSystemId());
			BasicDBObject update = new BasicDBObject("$set", new BasicDBObject("UPDATED_BY", grs.getUserEmailId())
					.append("UPDATED_AT", date).append("STATUS", "REOPENED"));

			collection.updateOne(filter, update);

			MongoCollection<Document> collection1 = dbIns.getCollection(
					PropertiesManager.propertiesMap.get(prefix + Constants.GrievanceRedressalSystemHistory.getValue()));

			BasicDBObject match1 = new BasicDBObject();
			match1.put(FieldType.GRSID.getName(), grs.getGrievanceRedressalSystemId().trim());
			match1.put("STATUS", "CLOSED");

			List<BasicDBObject> queryExecute1 = Arrays.asList(new BasicDBObject("$match", match1));

			logger.info("Query For find data in GrievanceRedressalSystemHistory Collection :" + queryExecute);

			MongoCursor<Document> cursor2 = collection1.aggregate(queryExecute1).iterator();
			Document document = null;
			if (cursor2.hasNext()) {
				document = (Document) cursor2.next();
				String uniqueNumber = TransactionManager.getNewTransactionId();

				document.put("_id", uniqueNumber);
				document.put("UPDATED_BY", grs.getUserEmailId());
				document.put("UPDATED_AT", date);
				document.put("STATUS", "REOPENED");

			}
			collection1.insertOne(document);
			return true;
		} else {
			return false;
		}

	}

	public List<GRS> getAllGrs(String payId, String status, String dateFrom, String dateTo) {

		List<GRS> list = new ArrayList<>();

		MongoDatabase dbIns = mongoInstance.getDB();
		MongoCollection<Document> coll = dbIns.getCollection(
				PropertiesManager.propertiesMap.get(prefix + Constants.GrievanceRedressalSystem.getValue()));

		BasicDBObject match = new BasicDBObject();

		if (!payId.equalsIgnoreCase("All")) {
			match.put(FieldType.PAY_ID.getName(), payId);
		}

		if (!status.equalsIgnoreCase("All")) {
			match.put(FieldType.STATUS.getName(), status);
		}

		if (StringUtils.isNoneBlank(dateFrom) && StringUtils.isNoneBlank(dateTo)) {
			match.put(FieldType.CREATE_DATE.getName(),
					new BasicDBObject("$gte", dateFrom + " 00:00:00").append("$lte", dateTo + " 23:59:59"));
		}

		List<BasicDBObject> pipeline = Arrays.asList(new BasicDBObject("$match", match),
				new BasicDBObject("$sort", new Document(FieldType.CREATE_DATE.getName(), -1)));

		logger.info("Query : " + pipeline);
		MongoCursor<Document> cursor = coll.aggregate(pipeline).iterator();
		GRS grs = null;
		while (cursor.hasNext()) {
			grs = new GRS();
			Document document = (Document) cursor.next();

			grs.setPgrefNum(document.getString("PG_REF_NUM") != null ? document.getString("PG_REF_NUM") : "N/A");
			grs.setGrsId(document.getString("GRSID") != null ? document.getString("GRSID") : "N/A");
			grs.setMerchantName(
					document.getString("MERCHANT_NAME") != null ? document.getString("MERCHANT_NAME") : "N/A");
			grs.setGrsTittle(document.getString("TITTLE") != null ? document.getString("TITTLE") : "N/A");
			grs.setAmount(document.getString("AMOUNT") != null ? document.getString("AMOUNT") : "N/A");
			grs.setTotalAmount(document.getString("TOTAL_AMOUNT") != null ? document.getString("TOTAL_AMOUNT") : "N/A");
			grs.setStatus(document.getString("STATUS") != null ? document.getString("STATUS") : "N/A");
			grs.setOrderId(document.getString("ORDER_ID") != null ? document.getString("ORDER_ID") : "N/A");
			grs.setCreatedDate(document.getString("CREATE_DATE") != null ? document.getString("CREATE_DATE") : "N/A");
			grs.setCreatedBy(document.getString("CREATED_BY") != null ? document.getString("CREATED_BY") : "N/A");
			grs.setTxnDate(
					document.getString("TRANSACTION_DATE") != null ? document.getString("TRANSACTION_DATE") : "N/A");
			grs.setPaymentMethod(
					document.getString("PAYMENT_TYPE") != null ? document.getString("PAYMENT_TYPE") : "N/A");
			grs.setMopType(document.getString("MOP_TYPE") != null ? document.getString("MOP_TYPE") : "N/A");
			grs.setPayId(document.getString("PAY_ID") != null ? document.getString("PAY_ID") : "N/A");
			grs.setCustomerName(document.getString("CUST_NAME") != null ? document.getString("CUST_NAME") : "N/A");
			grs.setCustomerPhone(document.getString("CUST_PHONE") != null ? document.getString("CUST_PHONE") : "N/A");
			grs.setUpdatedAt(document.getString("UPDATED_AT") != null ? document.getString("UPDATED_AT") : "N/A");
			grs.setUpdatedBy(document.getString("UPDATED_BY") != null ? document.getString("UPDATED_BY") : "N/A");
			grs.setGrsDesc(
					document.getString("GRS_DESCRIPTION") != null ? document.getString("GRS_DESCRIPTION") : "N/A");

			list.add(grs);
		}
		return list;
	}

	public List<GRS> getGrsHistory(String grsId) {

		List<GRS> list = new ArrayList<>();

		MongoDatabase dbIns = mongoInstance.getDB();
		MongoCollection<Document> coll = dbIns.getCollection(
				PropertiesManager.propertiesMap.get(prefix + Constants.GrievanceRedressalSystemHistory.getValue()));

		BasicDBObject match = new BasicDBObject();

		match.put(FieldType.GRSID.getName(), grsId);

		List<BasicDBObject> pipeline = Arrays.asList(new BasicDBObject("$match", match),
				new BasicDBObject("$sort", new Document(FieldType.UPDATE_DATE.getName(), -1)));

		logger.info("Query : " + pipeline);
		MongoCursor<Document> cursor = coll.aggregate(pipeline).iterator();
		GRS grs = null;
		while (cursor.hasNext()) {
			grs = new GRS();
			Document document = (Document) cursor.next();

			grs.setPgrefNum(document.getString("PG_REF_NUM") != null ? document.getString("PG_REF_NUM") : "N/A");
			grs.setGrsId(document.getString("GRSID") != null ? document.getString("GRSID") : "N/A");
			grs.setMerchantName(
					document.getString("MERCHANT_NAME") != null ? document.getString("MERCHANT_NAME") : "N/A");
			grs.setGrsTittle(document.getString("TITTLE") != null ? document.getString("TITTLE") : "N/A");
			grs.setAmount(document.getString("AMOUNT") != null ? document.getString("AMOUNT") : "N/A");
			grs.setTotalAmount(document.getString("TOTAL_AMOUNT") != null ? document.getString("TOTAL_AMOUNT") : "N/A");
			grs.setStatus(document.getString("STATUS") != null ? document.getString("STATUS") : "N/A");
			grs.setOrderId(document.getString("ORDER_ID") != null ? document.getString("ORDER_ID") : "N/A");
			grs.setCreatedDate(document.getString("CREATE_DATE") != null ? document.getString("CREATE_DATE") : "N/A");
			grs.setCreatedBy(document.getString("CREATED_BY") != null ? document.getString("CREATED_BY") : "N/A");
			grs.setTxnDate(
					document.getString("TRANSACTION_DATE") != null ? document.getString("TRANSACTION_DATE") : "N/A");
			grs.setPaymentMethod(
					document.getString("PAYMENT_TYPE") != null ? document.getString("PAYMENT_TYPE") : "N/A");
			grs.setMopType(document.getString("MOP_TYPE") != null ? document.getString("MOP_TYPE") : "N/A");
			grs.setPayId(document.getString("PAY_ID") != null ? document.getString("PAY_ID") : "N/A");
			grs.setCustomerName(document.getString("CUST_NAME") != null ? document.getString("CUST_NAME") : "N/A");
			grs.setCustomerPhone(document.getString("CUST_PHONE") != null ? document.getString("CUST_PHONE") : "N/A");
			grs.setUpdatedAt(document.getString("UPDATED_AT") != null ? document.getString("UPDATED_AT") : "N/A");
			grs.setUpdatedBy(document.getString("UPDATED_BY") != null ? document.getString("UPDATED_BY") : "N/A");
			grs.setGrsDesc(
					document.getString("GRS_DESCRIPTION") != null ? document.getString("GRS_DESCRIPTION") : "N/A");

			list.add(grs);
		}
		return list;
	}

	public boolean inProcess(GrievanceRedressalSystemDto grs) {
		MongoDatabase dbIns = mongoInstance.getDB();
		MongoCollection<Document> collection = dbIns.getCollection(
				PropertiesManager.propertiesMap.get(prefix + Constants.GrievanceRedressalSystem.getValue()));

		BasicDBObject match = new BasicDBObject();
		match.put(FieldType.GRSID.getName(), grs.getGrievanceRedressalSystemId().trim());

		List<BasicDBObject> queryExecute = Arrays.asList(new BasicDBObject("$match", match));

		logger.info("Query For find data in GrievanceRedressalSystem Collection :" + queryExecute);

		MongoCursor<Document> cursor = collection.aggregate(queryExecute).iterator();

		if (cursor.hasNext()) {
			String date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
			BasicDBObject filter = new BasicDBObject(FieldType.GRSID.getName(), grs.getGrievanceRedressalSystemId());
			BasicDBObject update = new BasicDBObject("$set", new BasicDBObject("UPDATED_BY", grs.getUserEmailId())
					.append("UPDATED_AT", date).append("STATUS", "INPROGRESS"));

			collection.updateOne(filter, update);

			MongoCollection<Document> collection1 = dbIns.getCollection(
					PropertiesManager.propertiesMap.get(prefix + Constants.GrievanceRedressalSystemHistory.getValue()));

			BasicDBObject match1 = new BasicDBObject();
			match1.put(FieldType.GRSID.getName(), grs.getGrievanceRedressalSystemId().trim());

			List<BasicDBObject> queryExecute1 = Arrays.asList(new BasicDBObject("$match", match1));

			logger.info("Query For find data in GrievanceRedressalSystemHistory Collection :" + queryExecute);

			MongoCursor<Document> cursor2 = collection1.aggregate(queryExecute1).iterator();
			Document document = null;
			if (cursor2.hasNext()) {
				document = (Document) cursor2.next();
				String uniqueNumber = TransactionManager.getNewTransactionId();

				document.put("_id", uniqueNumber);
				document.put("UPDATED_BY", grs.getUserEmailId());
				document.put("UPDATED_AT", date);
				document.put("STATUS", "INPROGRESS");

			}
			collection1.insertOne(document);
			return true;
		} else {
			return false;
		}

	}

	public boolean saveDesHistory(GrsIssueHistoryDto dto) {

		try {
			List<GRS> list = new ArrayList<>();

			MongoDatabase dbIns = mongoInstance.getDB();
			MongoCollection<Document> coll = dbIns.getCollection//("GrievanceRedressalSystemDescriptionHistory");
					(PropertiesManager.propertiesMap
					.get(prefix + Constants.GrievanceRedressalSystemDescriptionHistory.getValue()));

			BasicDBObject match = new BasicDBObject();
			String uniqueNumber = TransactionManager.getNewTransactionId();
			Document document = new Document("_id", uniqueNumber).append(FieldType.GRSID.getName(), dto.getGrsId())
					.append(FieldType.CREATE_DATE.getName(),
							new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()))
					.append("DESCRIPTION", dto.getDescription())
					.append(FieldType.CREATED_BY.getName(), dto.getCreatedBy())
					.append("FILENAME", dto.getFilename() != null ? dto.getFilename() : new BsonNull());

			coll.insertOne(document);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			// TODO: handle exception
			return false;
		}

	}

	public List<GrsIssueHistoryDto> getGrsDescHistory(String grsId) {

		List<GrsIssueHistoryDto> list = new ArrayList<>();

		MongoDatabase dbIns = mongoInstance.getDB();
		MongoCollection<Document> coll = dbIns.getCollection//("GrievanceRedressalSystemDescriptionHistory");
				(PropertiesManager.propertiesMap
				.get(prefix + Constants.GrievanceRedressalSystemDescriptionHistory.getValue()));

		BasicDBObject match = new BasicDBObject();

		match.put(FieldType.GRSID.getName(), grsId);

		List<BasicDBObject> pipeline = Arrays.asList(new BasicDBObject("$match", match),
				new BasicDBObject("$sort", new Document(FieldType.CREATE_DATE.getName(), -1)));

		logger.info("Query : " + pipeline);
		MongoCursor<Document> cursor = coll.aggregate(pipeline).iterator();
		GrsIssueHistoryDto grs = new GrsIssueHistoryDto();
		while (cursor.hasNext()) {
			grs = new GrsIssueHistoryDto();
			Document document = (Document) cursor.next();
			grs.setGrsId(document.getString("GRSID") != null ? document.getString("GRSID") : "N/A");
			grs.setCreatedBy(document.getString("CREATED_BY") != null ? document.getString("CREATED_BY") : "N/A");
			grs.setCreateDate(document.getString("CREATE_DATE") != null ? document.getString("CREATE_DATE") : "N/A");
			grs.setDescription(document.getString("DESCRIPTION") != null ? document.getString("DESCRIPTION") : "N/A");
			grs.setFilename(document.getString("FILENAME") != null ? document.getString("FILENAME") : "N/A");
			if (document.getString("FILENAME") != null && document.getString("FILENAME").trim().length() > 0) {
				Path filePath1 = Paths.get(filePath + document.getString("FILENAME"));

				try {
					// Read the file into a byte array
					byte[] fileContent = null;
					try {
						fileContent = Files.readAllBytes(filePath1);
					} catch (java.io.IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					// Encode the byte array to Base64
					String base64Encoded = Base64.getEncoder().encodeToString(fileContent);
					grs.setFile(base64Encoded);
					// Print or use the Base64-encoded string

				} catch (IOException e) {
					// Handle the exception if the file is not found or an I/O error occurs
					e.printStackTrace();
				}
			}
			list.add(grs);
		}
		return list;
	}

}
