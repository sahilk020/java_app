package com.pay10.pg.action.beans;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.bson.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.mongodb.BasicDBObject;
import com.mongodb.client.AggregateIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import com.pay10.commons.mongo.MongoInstance;
import com.pay10.commons.util.Constants;
import com.pay10.commons.util.FieldType;
import com.pay10.commons.util.PropertiesManager;
import com.pay10.commons.util.StatusType;
import com.pay10.commons.util.YesbankCbUpiResultType;

@Service
public class SearchTransactionActionBean {
	private static Logger logger = LoggerFactory.getLogger(SearchTransactionActionBean.class.getName());
	@Autowired
	PropertiesManager propertiesManager;

	@Autowired
	private MongoInstance mongoInstance;

	private static final String prefix = "MONGO_DB_";

	public Map<String, String> searchPayment(String pgRefNum) {
		
		MongoCursor<Document> cursor = null;
		try {
			List<BasicDBObject> paramConditionLst = new ArrayList<BasicDBObject>();
			BasicDBObject allParamQuery = new BasicDBObject();

			if (!(pgRefNum.isEmpty())) {

				paramConditionLst.add(new BasicDBObject(FieldType.PG_REF_NUM.getName(), pgRefNum));
			}

			if (!paramConditionLst.isEmpty()) {
				allParamQuery = new BasicDBObject("$and", paramConditionLst);
			}

			MongoDatabase dbIns = mongoInstance.getDB();
			MongoCollection<Document> coll = dbIns
					.getCollection(PropertiesManager.propertiesMap.get(prefix + Constants.COLLECTION_NAME.getValue()));

			BasicDBObject match = new BasicDBObject("$match", allParamQuery);

			BasicDBObject groupFields = new BasicDBObject("_id", "$PG_REF_NUM").append("entries",
					new BasicDBObject("$push", "$$ROOT"));

			BasicDBObject group = new BasicDBObject("$group", groupFields);
			BasicDBObject sort = new BasicDBObject("$sort", new BasicDBObject("CREATE_DATE", -1));

			List<BasicDBObject> pipeline = Arrays.asList(match, sort, group);
			AggregateIterable<Document> output = coll.aggregate(pipeline);
			output.allowDiskUse(true);
			cursor = output.iterator();

			while (cursor.hasNext()) {
				Document mydata = cursor.next();

				List<Document> courses = (List<Document>) mydata.get("entries");
				Document dbobj = courses.get(0);

				String status = (dbobj).getString(FieldType.STATUS.toString());
				String pgResCode = (dbobj).getString(FieldType.PG_RESP_CODE.toString());
				if (status.equals(StatusType.CANCELLED.getName().toString())
						&& pgResCode.equals(YesbankCbUpiResultType.TRANSACTION_DECLINED_BY_CUSTOMER.getBankCode())) {
					logger.info("Collect API  ENQUIRY Response, if response is declined for yesupi : " + status);
					Map<String, Object> map = new HashMap<>(dbobj);
					Map<String, String> responseMap = new HashMap<String, String>();

					for (Entry<String, Object> mapObj : map.entrySet()) {

						if (mapObj.getValue() != null) {
							responseMap.put(mapObj.getKey(), mapObj.getValue().toString());
						}

					}

					
					return responseMap;
				} else if (status.equals(StatusType.SENT_TO_BANK.getName().toString())) {
					Map<String, Object> map = new HashMap<>(dbobj);
					Map<String, String> responseMap = new HashMap<String, String>();

					for (Entry<String, Object> mapObj : map.entrySet()) {

						if (mapObj.getValue() != null) {
							responseMap.put(mapObj.getKey(), mapObj.getValue().toString());
						}

					}

					
					return responseMap;
//					continue;
				}

				else {

					Map<String, Object> map = new HashMap<>(dbobj);
					Map<String, String> responseMap = new HashMap<String, String>();

					for (Entry<String, Object> mapObj : map.entrySet()) {

						if (mapObj.getValue() != null) {
							responseMap.put(mapObj.getKey(), mapObj.getValue().toString());
						}

					}

					
					return responseMap;
				}
			}

		} catch (Exception e) {
			logger.error("Exception  : " + e.getMessage());
		} finally {
			cursor.close();
		}
		return null;

	}

}