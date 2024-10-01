package com.crmws.worker.service;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.mongodb.BasicDBObject;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import com.pay10.commons.mongo.MongoInstance;
import com.pay10.commons.user.Account;
import com.pay10.commons.user.User;
import com.pay10.commons.user.UserDao;
import com.pay10.commons.util.Constants;
import com.pay10.commons.util.FieldType;
import com.pay10.commons.util.PropertiesManager;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class OrderToMerchantIdService {
	
	
	@Autowired
	private UserDao userdao;

	@Autowired
	PropertiesManager propertiesManager;

	@Autowired
	private MongoInstance mongoInstance;
	
	private static final String prefix = "MONGO_DB_";
	
	public String getMIDfromOrderAndAcq(String pgRefNum,String acqType) {
		log.info("PG Ref : "+pgRefNum);
		log.info("Acquirer "+ acqType);
		String mid = "";
        
		try
		{
		BasicDBObject andQuery = new BasicDBObject();
		List<BasicDBObject> obj = new ArrayList<BasicDBObject>();
		if (!StringUtils.isBlank(pgRefNum)) {
		obj.add(new BasicDBObject(FieldType.PG_REF_NUM.getName(), pgRefNum));
		}
		if (!StringUtils.isBlank(acqType)) {
		obj.add(new BasicDBObject(FieldType.ACQUIRER_TYPE.getName(), acqType));
		}
		obj.add(new BasicDBObject(FieldType.TXNTYPE.getName(), "SALE"));
		obj.add(new BasicDBObject(FieldType.STATUS.getName(), "Captured"));
		andQuery.put("$and", obj);
		
		MongoDatabase dbIns = mongoInstance.getDB();
		MongoCollection<Document> coll = dbIns.getCollection(PropertiesManager.propertiesMap.get(prefix+Constants.COLLECTION_NAME.getValue()));
		MongoCursor<Document> cursor = coll.find(andQuery).limit(1).iterator();
		while (cursor.hasNext()) {
			Document doc = cursor.next();

			User user = new User();
			log.info(FieldType.PAY_ID.toString() + " : "+doc.getString(FieldType.PAY_ID.toString()));
			user = userdao.findPayId(doc.getString(FieldType.PAY_ID.toString()));
			//log.info(user.toString());
			log.info(acqType);
			Account acc = user.getAccountUsingAcquirerCode(acqType);
			mid=acc.getAccountCurrency("356").getMerchantId();
		}
		
		cursor.close();
		return mid;
	    }
		catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

}
