package com.pay10.commons.mongo;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import com.google.gson.Gson;
import com.mongodb.BasicDBObject;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import com.pay10.commons.dto.CommissionPODTO;
import com.pay10.commons.user.User;
import com.pay10.commons.user.UserDao;
import com.pay10.commons.util.Constants;
import com.pay10.commons.util.Currency;
import com.pay10.commons.util.PropertiesManager;

@Component
public class CommissionPODao {

	private static final String prefix = "MONGO_DB_";

	@Autowired
	PropertiesManager propertiesManager;

	@Autowired
	private MongoInstance mongoInstance;
	@Autowired
	private UserDao userDao;
	
	private SimpleDateFormat sd = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");

	
	public int getActiveDetailsCount(){
		
		MongoDatabase dbIns = mongoInstance.getDB();

		MongoCollection<Document> coll = dbIns
				.getCollection(PropertiesManager.propertiesMap.get(prefix + Constants.COMMISSION_PO.getValue()));
		
		Document query = new Document("status", "ACTIVE");
		
		return coll.find(query).into(new ArrayList<>()).size();
		

	}
	
	public List<CommissionPODTO> getActiveDetails(int start, int length){
		List<CommissionPODTO> commissionPODTOs=new ArrayList<CommissionPODTO>();
		MongoDatabase dbIns = mongoInstance.getDB();

		MongoCollection<Document> coll = dbIns
				.getCollection(PropertiesManager.propertiesMap.get(prefix + Constants.COMMISSION_PO.getValue()));
		
		BasicDBObject match = new BasicDBObject();
		match.put("status", "ACTIVE");
		
		//MongoCursor<Document> cursor = coll.find(query).iterator();
		
		
		BasicDBObject matchQ = new BasicDBObject("$match", match);
		BasicDBObject sort = new BasicDBObject("$sort", new BasicDBObject("CREATE_DATE", -1));
		BasicDBObject skip = new BasicDBObject("$skip", start);
		BasicDBObject limit = new BasicDBObject("$limit", length);
		List<BasicDBObject> pipeline = Arrays.asList(matchQ, sort, skip, limit);
		
		MongoCursor<Document> cursor=coll.aggregate(pipeline).iterator();
		
		while (cursor.hasNext()) {
			Document document = (Document) cursor.next();
		
			CommissionPODTO commissionPODTO = new CommissionPODTO();
			commissionPODTO.setAcquirer(document.getString("acquirer"));
			commissionPODTO.setCurrency(Currency.getAlphabaticCode(document.getString("currency")));
			commissionPODTO.setCreateDate(document.getString("createDate"));
			commissionPODTO.setMerchantName(document.getString("merchantName"));
			commissionPODTO.setPayId(document.getString("payId"));
			commissionPODTO.setStatus(document.getString("status"));
			commissionPODTO.setType(document.getString("type"));
			commissionPODTO.setUpdateBy(document.getString("updateBy"));
			commissionPODTO.setValue(document.getString("value"));
			commissionPODTOs.add(commissionPODTO);
		}
	

		return commissionPODTOs;
	}

	public CommissionPODTO fetchByPayId(String payId,String currency,String acquirer) {

		MongoDatabase dbIns = mongoInstance.getDB();

		MongoCollection<Document> coll = dbIns
				.getCollection(PropertiesManager.propertiesMap.get(prefix + Constants.COMMISSION_PO.getValue()));

		Document query = new Document("payId", payId).append("status", "ACTIVE").append("acquirer", acquirer).append("currency", currency);

		MongoCursor<Document> cursor = coll.find(query).iterator();
		CommissionPODTO commissionPODTO = null;
		if (cursor.hasNext()) {
			Document document = (Document) cursor.next();
			System.out.println(document);
			commissionPODTO = new CommissionPODTO();
			commissionPODTO.setAcquirer(document.getString("acquirer"));
			commissionPODTO.setCurrency(document.getString("currency"));
			commissionPODTO.setCreateDate(document.getString("createDate"));
			commissionPODTO.setMerchantName(document.getString("merchantName"));
			commissionPODTO.setPayId(document.getString("payId"));
			commissionPODTO.setStatus(document.getString("status"));
			commissionPODTO.setType(document.getString("type"));
			commissionPODTO.setUpdateBy(document.getString("updateBy"));
			commissionPODTO.setValue(document.getString("value"));
		}
		System.out.println("fetchByPayId : " + new Gson().toJson(commissionPODTO));

		return commissionPODTO;
	}

	public String saveAcquirerMapping(String payId, String acquirer, String currency, String type, String value, String updatedBy) {

		//check based on currency and payid if exist then Status INACTIVE 
		CommissionPODTO commissionPODTOExist = fetchByPayId(payId,currency,acquirer);
		User user = userDao.findByPayId(payId);
		
		MongoDatabase dbIns = mongoInstance.getDB();
		System.out.println("saveAcquirerMapping : " + new Gson().toJson(commissionPODTOExist));

		MongoCollection<Document> coll = dbIns
				.getCollection(PropertiesManager.propertiesMap.get(prefix + Constants.COMMISSION_PO.getValue()));

		// update if data is already exist
		if (commissionPODTOExist != null) {
			commissionPODTOExist.setStatus("INACTIVE");
			updateRecordBasedOnPayId(commissionPODTOExist);
		}

		// insert new Data with ACTIVE status
		CommissionPODTO commissionPODTO = new CommissionPODTO();
		commissionPODTO.setAcquirer(acquirer);
		commissionPODTO.setCurrency(currency);
		commissionPODTO.setCreateDate(sd.format(new Date()));
		commissionPODTO.setMerchantName(user.getBusinessName());
		commissionPODTO.setPayId(payId);
		commissionPODTO.setStatus("ACTIVE");
		commissionPODTO.setType(type);
		commissionPODTO.setUpdateBy(updatedBy);
		commissionPODTO.setValue(value);
		Document doc = Document.parse(new Gson().toJson(commissionPODTO));
		coll.insertOne(doc);
		return "SUCCESS";

	}

	public String updateRecordBasedOnPayId(CommissionPODTO commissionPODTO) {
		System.out.println("updateRecordBasedOnPayId : " + new Gson().toJson(commissionPODTO));
		MongoDatabase dbIns = mongoInstance.getDB();
		MongoCollection<Document> coll = dbIns
				.getCollection(PropertiesManager.propertiesMap.get(prefix + Constants.COMMISSION_PO.getValue()));

		Document filter = new Document("payId", commissionPODTO.getPayId()).append("status","ACTIVE").append("acquirer", commissionPODTO.getAcquirer()).append("currency", commissionPODTO.getCurrency());

		Document update = new Document("$set", Document.parse(new Gson().toJson(commissionPODTO))); 
		coll.updateOne(filter, update);
		return "SUCCESS";
	}

}
