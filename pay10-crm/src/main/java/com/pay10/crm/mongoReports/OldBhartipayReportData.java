package com.pay10.crm.mongoReports;

import java.text.SimpleDateFormat;
import java.util.*;

import org.apache.commons.lang3.StringUtils;
import org.bson.BsonNull;
import org.bson.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.mongodb.BasicDBObject;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import com.pay10.commons.mongo.MongoInstance;
import com.pay10.commons.util.Constants;
import com.pay10.commons.util.OldBhartipayDTO;
import com.pay10.commons.util.OldBhartipayStatus;
import com.pay10.commons.util.PropertiesManager;

@Component
public class OldBhartipayReportData {
	@Autowired
	private MongoInstance mongoInstance;
	private static final String prefix = "MONGO_DB_";
	private static Logger logger = LoggerFactory.getLogger(OldBhartipayReportData.class.getName());

	public List<OldBhartipayStatus> getStatusFromOldBhartpay() {
		List<OldBhartipayStatus> status = new ArrayList<>();
		try {
			/*MongoDatabase dbIns = mongoInstance.getDB();
			MongoCollection<Document> coll = dbIns
					.getCollection(PropertiesManager.propertiesMap.get(prefix + Constants.OLDBHARTIPAY.getValue()));

			List<BasicDBObject> query = Arrays.asList(
					new BasicDBObject("$match", new BasicDBObject("STATUS", new BasicDBObject("$ne", new BsonNull()))),
					new BasicDBObject("$group", new BasicDBObject("_id", "$STATUS")),
					new BasicDBObject("$project", new BasicDBObject("STATUS", "$_id").append("_id", 0L)));

			logger.info("getStatusFromOldBhartpay query : " + query);
			MongoCursor<Document> cursor = coll.aggregate(query).iterator();*/

			List<String>statusType=Arrays.asList("Captured","Failed","Refunded");

			OldBhartipayStatus bhartipayStatus = null;
			/*while (cursor.hasNext()) {*/

			for(String s : statusType) {
				bhartipayStatus = new OldBhartipayStatus();
				bhartipayStatus.setStatus(s);
				status.add(bhartipayStatus);
			}
			/*}*/
		} catch (Exception e) {
			logger.error("Exception occur in getStatusFromOldBhartpay", e);
			e.printStackTrace();
		}
		return status;
	}

	public int getOldbhartipayReportCount(String status, String dateFrom, String dateTo) {
		try {
			MongoDatabase dbIns = mongoInstance.getDB();
			MongoCollection<Document> coll = dbIns
					.getCollection(PropertiesManager.propertiesMap.get(prefix + Constants.OLDBHARTIPAY.getValue()));

			BasicDBObject match = new BasicDBObject();
			if (!status.equalsIgnoreCase("ALL")) {
				match.put("STATUS", status);
			}

			if (StringUtils.isNoneBlank(dateFrom) && StringUtils.isNoneBlank(dateTo)) {

				String dateFFrom = dateFrom + " 00:00:00";
				String dateTTo = dateTo + " 23:59:59";

				SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				Date datefrom = dateFormat.parse(dateFFrom);
				Date dateto = dateFormat.parse(dateTTo);

				//SimpleDateFormat dateFormat2 = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.sssZ");

				match.put("CREATE_DATE", new BasicDBObject("$gte", datefrom).append("$lte",
						dateto));
			}
			BasicDBObject matchQ = new BasicDBObject("$match", match);

			List<BasicDBObject> queryExecute = Arrays.asList(matchQ);

			logger.info("getOldbhartipayReportCount Query : " + queryExecute);
			return coll.aggregate(queryExecute).into(new ArrayList<>()).size();
		} catch (Exception e) {
			logger.error("Exception Occur in getOldbhartipayReportCount() : ", e);
			e.printStackTrace();
			return 0;
		}

	}

	public List<OldBhartipayDTO> getOldbhartipayReport(String status, String dateFrom, String dateTo, int start,
			int length) {
		List<OldBhartipayDTO> bhartipayDTOs = new ArrayList<>();
		try {
			MongoDatabase dbIns = mongoInstance.getDB();
			MongoCollection<Document> coll = dbIns
					.getCollection(PropertiesManager.propertiesMap.get(prefix + Constants.OLDBHARTIPAY.getValue()));

			BasicDBObject match = new BasicDBObject();
			if (!status.equalsIgnoreCase("ALL")) {
				match.put("STATUS", status);
			}

			if (StringUtils.isNoneBlank(dateFrom) && StringUtils.isNoneBlank(dateTo)) {
				String dateFFrom = dateFrom + " 00:00:00";
				String dateTTo = dateTo + " 23:59:59";

				SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				Date datefrom = dateFormat.parse(dateFFrom);
				Date dateto = dateFormat.parse(dateTTo);

				//SimpleDateFormat dateFormat2 = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.sssZ");

				match.put("CREATE_DATE", new BasicDBObject("$gte", datefrom).append("$lte",
						dateto));
			}
			BasicDBObject matchQ = new BasicDBObject("$match", match);
			BasicDBObject sort = new BasicDBObject("$sort", new BasicDBObject("CREATE_DATE", -1));
			BasicDBObject skip = new BasicDBObject("$skip", start);
			BasicDBObject limit = new BasicDBObject("$limit", length);
			List<BasicDBObject> queryExecute = Arrays.asList(matchQ, sort, skip, limit);

			logger.info("getOldbhartipayReport Query : " + queryExecute);

			MongoCursor<Document> cursor = coll.aggregate(queryExecute).iterator();
			OldBhartipayDTO bhartipayDTO = null;
			while (cursor.hasNext()) {
				bhartipayDTO = new OldBhartipayDTO();
				Document document = (Document) cursor.next();
				bhartipayDTO.setAcquirerType(String.valueOf(document.get("ACQUIRER_TYPE")));
				bhartipayDTO.setAmount(String.valueOf(document.get("AMOUNT")));
				bhartipayDTO.setCreateDate(String.valueOf(document.get("CREATE_DATE")));
				bhartipayDTO.setCurrencyCode(String.valueOf(document.get("CURRENCY_CODE")));
				bhartipayDTO.setCustomerName(String.valueOf(document.get("CUST_NAME")));
				bhartipayDTO.setMopType(String.valueOf(document.get("MOP_TYPE")));
				bhartipayDTO.setOid(String.valueOf(document.get("OID")));
				bhartipayDTO.setOrderId(String.valueOf(document.get("ORDER_ID")));
				bhartipayDTO.setPayId(String.valueOf(document.get("PAY_ID")));
				bhartipayDTO.setPaymentType(String.valueOf(document.get("PAYMENT_TYPE")));
				bhartipayDTO.setPgrefnum(String.valueOf(document.get("PG_REF_NUM")));
				bhartipayDTO.setStatus(String.valueOf(document.get("STATUS")));
				bhartipayDTO.setSurchargeAmount(String.valueOf(document.get("SURCHARGE_AMOUNT")));
				bhartipayDTO.setTxnId(String.valueOf(document.get("TXN_ID")));
				bhartipayDTO.setTxnType(String.valueOf(document.get("TXNTYPE")));
				bhartipayDTOs.add(bhartipayDTO);
			}

			return bhartipayDTOs;
		} catch (Exception e) {
			logger.error("Exception Occur in getOldbhartipayReport() ", e);
			e.printStackTrace();
			return bhartipayDTOs;
		}
	}
	
	public List<OldBhartipayDTO> getOldbhartipayReportDownload(String status, String dateFrom, String dateTo) {
		List<OldBhartipayDTO> bhartipayDTOs = new ArrayList<>();
		try {

			Map<String,List<String>> stringListHashMap=new HashMap<String,List<String>>();

			stringListHashMap.put("Cancelled",Arrays.asList("Cancelled","Cancelled by user"));
			stringListHashMap.put("Captured",Arrays.asList("Captured","Captured1","Enrolled"));
			stringListHashMap.put("Pending",Arrays.asList("Sent to Bank","Pending","Processing"));
			stringListHashMap.put("Refunded",Arrays.asList("REFUNDED"));
			stringListHashMap.put("SUCCESS",Arrays.asList("Success","SUCCESS"));
			stringListHashMap.put("Rejected",Arrays.asList("Rejected","Rejected by acquirer"));
			stringListHashMap.put("Failed",Arrays.asList("Authentication Failed","Error","FAILED","Failed","Failed at Acquire","Failed at Acquirer","Failed at acquire","Failed at acquirer","NA","Timed out at Acquirer","Timeout","Duplicate","Declined","Denied by risk","Denied due to fraud","Invalid"));


			MongoDatabase dbIns = mongoInstance.getDB();
			MongoCollection<Document> coll = dbIns
					.getCollection(PropertiesManager.propertiesMap.get(prefix + Constants.OLDBHARTIPAY.getValue()));

			BasicDBObject match = new BasicDBObject();
			if (!status.equalsIgnoreCase("ALL")) {
				match.put("STATUS", new BasicDBObject("$in",stringListHashMap.get(status)));
			}
			match.put("TXNTYPE", "SALE");
			if (StringUtils.isNoneBlank(dateFrom) && StringUtils.isNoneBlank(dateTo)) {
				String dateFFrom = dateFrom + " 00:00:00";
				String dateTTo = dateTo + " 23:59:59";

				SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				Date datefrom = dateFormat.parse(dateFFrom);
				Date dateto = dateFormat.parse(dateTTo);

				//SimpleDateFormat dateFormat2 = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.sssZ");

				match.put("CREATE_DATE", new BasicDBObject("$gte", datefrom).append("$lte",
						dateto));
			}
			BasicDBObject matchQ = new BasicDBObject("$match", match);


			List<BasicDBObject> queryExecute = Arrays.asList(matchQ);

			logger.info("getOldbhartipayReport Query : " + queryExecute);

			MongoCursor<Document> cursor = coll.aggregate(queryExecute).iterator();
			OldBhartipayDTO bhartipayDTO = null;
			while (cursor.hasNext()) {
				bhartipayDTO = new OldBhartipayDTO();
				Document document = (Document) cursor.next();
				bhartipayDTO.setAcquirerType(String.valueOf(document.get("ACQUIRER_TYPE")));
				bhartipayDTO.setAmount(String.valueOf(document.get("AMOUNT")));
				bhartipayDTO.setCreateDate(String.valueOf(document.get("CREATE_DATE")));
				bhartipayDTO.setCurrencyCode(String.valueOf(document.get("CURRENCY_CODE")));
				bhartipayDTO.setCustomerName(String.valueOf(document.get("CUST_NAME")));
				bhartipayDTO.setMopType(String.valueOf(document.get("MOP_TYPE")));
				bhartipayDTO.setOid(String.valueOf(document.get("OID")));
				bhartipayDTO.setOrderId(String.valueOf(document.get("ORDER_ID")));
				bhartipayDTO.setPayId(String.valueOf(document.get("PAY_ID")));
				bhartipayDTO.setPaymentType(String.valueOf(document.get("PAYMENT_TYPE")));
				bhartipayDTO.setPgrefnum(String.valueOf(document.get("PG_REF_NUM")));
				bhartipayDTO.setStatus(String.valueOf(document.get("STATUS")));
				bhartipayDTO.setSurchargeAmount(String.valueOf(document.get("SURCHARGE_AMOUNT")));
				bhartipayDTO.setTxnId(String.valueOf(document.get("TXN_ID")));
				bhartipayDTO.setTxnType(String.valueOf(document.get("TXNTYPE")));
				bhartipayDTOs.add(bhartipayDTO);
			}

			return bhartipayDTOs;
		} catch (Exception e) {
			logger.error("Exception Occur in getOldbhartipayReport() ", e);
			e.printStackTrace();
			return bhartipayDTOs;
		}
	}

}
