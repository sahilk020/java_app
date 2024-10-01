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
import com.pay10.commons.api.TransactionControllerServiceProvider;
import com.pay10.commons.mongo.MongoInstance;
import com.pay10.commons.util.AcquirerType;
import com.pay10.commons.util.Constants;
import com.pay10.commons.util.FieldType;
import com.pay10.commons.util.Fields;
import com.pay10.commons.util.PropertiesManager;
import com.pay10.commons.util.StatusType;

@Service
public class TransactionStatusCheckBean {
	private static Logger logger = LoggerFactory.getLogger(TransactionStatusCheckBean.class.getName());
	@Autowired
	PropertiesManager propertiesManager;

	@Autowired
	private TransactionControllerServiceProvider transactionControllerServiceProvider;

	@Autowired
	private MongoInstance mongoInstance;

	private static final String prefix = "MONGO_DB_";

	public Map<String, String> searchPayment(Fields fields) {
		String acquirerType = fields.get(FieldType.ACQUIRER_TYPE.getName());
		String enqUrl = "";
		if (acquirerType.equalsIgnoreCase(AcquirerType.BILLDESK.getCode())) {
			enqUrl = propertiesManager.propertiesMap.get("BilldeskUpiStatusUrl");
		}
		if (acquirerType.equalsIgnoreCase(AcquirerType.FREECHARGE.getCode())) {
			enqUrl = propertiesManager.propertiesMap.get("FreechargeUpiStatusUrl");
		}
		 
		String pgRefNum = fields.get(FieldType.PG_REF_NUM.getName());

		Map<String, String> responseMap = new HashMap<String, String>();

		try {

			Map<String, String> response = transactionControllerServiceProvider.upiEnquiry(fields, enqUrl);
			Fields responseFields = new Fields(response);

			String status = responseFields.get(FieldType.STATUS.getName());
			String responseCode = responseFields.get(FieldType.RESPONSE_CODE.getName());

			// If status is not equal to Captured after billdesk status enquiry , send null
			// response
			if (status.equalsIgnoreCase(StatusType.PROCESSING.getName())) {

				return null;
			} else {

				MongoCursor<Document> cursor = null;
				List<BasicDBObject> paramConditionLst = new ArrayList<BasicDBObject>();
				BasicDBObject allParamQuery = new BasicDBObject();

				if (!(pgRefNum.isEmpty())) {

					paramConditionLst.add(new BasicDBObject(FieldType.PG_REF_NUM.getName(), pgRefNum));
				}

				if (!paramConditionLst.isEmpty()) {
					allParamQuery = new BasicDBObject("$and", paramConditionLst);
				}

				MongoDatabase dbIns = mongoInstance.getDB();
				MongoCollection<Document> coll = dbIns.getCollection(
						PropertiesManager.propertiesMap.get(prefix + Constants.COLLECTION_NAME.getValue()));

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

					status = dbobj.get(FieldType.STATUS.toString()).toString();

					Map<String, Object> map = new HashMap<>(dbobj);
					responseMap = new HashMap<String, String>();

					for (Entry<String, Object> mapObj : map.entrySet()) {

						if (mapObj.getValue() != null) {
							responseMap.put(mapObj.getKey(), mapObj.getValue().toString());
						}

					}

					return responseMap;
				}

			}

			return responseMap;

		} catch (Exception e) {
			logger.error("Exception  : ", e);
		}
		return responseMap;

	}

}