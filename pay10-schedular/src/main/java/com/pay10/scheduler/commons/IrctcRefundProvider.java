package com.pay10.scheduler.commons;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.bson.Document;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.MapType;
import com.mongodb.BasicDBObject;
import com.mongodb.BasicDBObjectBuilder;
import com.mongodb.client.AggregateIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import com.pay10.commons.exception.ErrorType;
import com.pay10.commons.exception.SystemException;
import com.pay10.commons.mongo.MongoInstance;
import com.pay10.commons.util.FieldType;

@Service
public class IrctcRefundProvider {

	@Autowired
	private MongoInstance mongoInstance;

	@Autowired
	private ConfigurationProvider configurationProvider;

	private static final Logger logger = LoggerFactory.getLogger(IrctcRefundProvider.class);

	public void getBulkRefundTrnsaction() throws SystemException {

		String bulkRefundUrl = configurationProvider.getBulkRefundUrl();

		BasicDBObject txnTypQuery = new BasicDBObject();
		txnTypQuery.put("status", "Pending");

		List<BasicDBObject> allConditionQueryList = new ArrayList<BasicDBObject>();
		allConditionQueryList.add(txnTypQuery);

		BasicDBObject finalquery = new BasicDBObject("$and", allConditionQueryList);

		logger.info("Query to get data for  irctc Refund = " + finalquery);
		MongoDatabase dbIns = mongoInstance.getDB();
		MongoCollection<Document> collection = dbIns.getCollection("irctcRefundEntity");

		BasicDBObject match = new BasicDBObject("$match", finalquery);
		logger.info("Query to get data irctc Refund " + match);
		List<BasicDBObject> pipeline = Arrays.asList(match);
		AggregateIterable<Document> output = collection.aggregate(pipeline);
		output.allowDiskUse(true);
		MongoCursor<Document> cursor = output.iterator();

		while (cursor.hasNext()) {
			JSONObject fields = new JSONObject();
			Document dbobj = cursor.next();

			if (null != dbobj.get("orderId") && !dbobj.getString("orderId").equalsIgnoreCase("0")) {
				fields.put(FieldType.ORDER_ID.getName(), dbobj.getString("orderId"));
			}

			if (null != dbobj.get("amount") && !dbobj.getString("amount").equalsIgnoreCase("0")) {
				fields.put(FieldType.AMOUNT.getName(), (dbobj.getString("amount")).replace(".", ""));
			}
			if (null != dbobj.get("pgRefNO") && !dbobj.getString("pgRefNO").equalsIgnoreCase("0")) {
				fields.put(FieldType.PG_REF_NUM.getName(), dbobj.getString("pgRefNO"));
			}
			if (null != dbobj.get("payId") && !dbobj.getString("payId").equalsIgnoreCase("0")) {
				fields.put(FieldType.PAY_ID.getName(), dbobj.getString("payId"));
			}

			fields.put(FieldType.CURRENCY_CODE.getName(), dbobj.getString("currencyCode"));
			fields.put(FieldType.REFUND_FLAG.getName(), "R");
			fields.put(FieldType.TXNTYPE.getName(), dbobj.getString("transactionType"));
			fields.put(FieldType.REFUND_ORDER_ID.getName(), dbobj.getString("RefundOrderId"));
			
			fields.put("IRCTC_REFUND_TYPE", dbobj.getString("refundFlag"));
			fields.put("IRCTC_REFUND_CANCELLED_DATE", dbobj.getString("irctcRefundCancelledDate"));
			fields.put("IRCTC_REFUND_CANCELLED_ID", dbobj.getString("irctcRefundCancelledId"));
			fields.put("IRCTC_REFUND_FILE_TYPE", dbobj.getString("refundFileType"));
			fields.put("IRCTC_REFUND_FILE_DATE", dbobj.getString("fileDate"));
			fields.put("IRCTC_REFUND", "Y");
			
			logger.info("fields inirctcrefund " + fields + bulkRefundUrl);
			Map<String, String> response = null;
			response = bulkRefundCall(fields, bulkRefundUrl);
			logger.info("Irctc Refund Response :: " + response);
			logger.info("Irctc Refund Response Status :: " + response.get("STATUS"));

			BasicDBObject searchQuery = new BasicDBObject("RefundOrderId", dbobj.getString("RefundOrderId"));
			BasicDBObject updateFields = new BasicDBObject();
			updateFields.append("status", response.get("STATUS"));
			updateFields.append("ResponseCode", response.get("RESPONSE_CODE"));
			updateFields.append("ResponseMessage", response.get("RESPONSE_MESSAGE"));

			BasicDBObject setQuery = new BasicDBObject();
			setQuery.append("$set", updateFields);
			collection.updateMany(searchQuery, setQuery);
		}

		cursor.close();
	}

	public String getCurrentDate() {
		DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
		LocalDateTime now = LocalDateTime.now();
		return dtf.format(now);
	}

	public String getStartDate(String BulkRefundConfig) {
		DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
		LocalDateTime now = LocalDateTime.now();
		int data = Integer.parseInt(BulkRefundConfig);
		now = now.plusMinutes(-data);
		return dtf.format(now);
	}

	public static Map<String, String> bulkRefundCall(JSONObject json, String url) throws SystemException {
		String responseBody = "";

		String serviceUrl = url;
		Map<String, String> resMap = new HashMap<String, String>();
		try {

			CloseableHttpClient httpClient = HttpClientBuilder.create().build();
			HttpPost request = new HttpPost(serviceUrl);
			StringEntity params = new StringEntity(json.toString());
			request.addHeader("content-type", "application/json");
			request.setEntity(params);
			HttpResponse resp = httpClient.execute(request);
			responseBody = EntityUtils.toString(resp.getEntity());
			final ObjectMapper mapper = new ObjectMapper();
			final MapType type = mapper.getTypeFactory().constructMapType(Map.class, String.class, Object.class);
			resMap = mapper.readValue(responseBody, type);
		} catch (Exception exception) {
			logger.error("exception is " + exception);
			throw new SystemException(ErrorType.INTERNAL_SYSTEM_ERROR, "Error communicating to PG WS");
		}
		return resMap;
	}
}
