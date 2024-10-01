package com.pay10.commons.repository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.Query;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import org.bson.Document;
import org.hibernate.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.mongodb.BasicDBObject;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import com.pay10.commons.dao.HibernateAbstractDao;
import com.pay10.commons.dao.HibernateSessionProvider;
import com.pay10.commons.mongo.MongoInstance;
import com.pay10.commons.util.Constants;
import com.pay10.commons.util.FieldType;
import com.pay10.commons.util.PropertiesManager;
import com.pay10.commons.util.StatusType;


@Repository
public class ChargebackBulkmailRepository extends HibernateAbstractDao {
	private static Logger logger = LoggerFactory.getLogger(DMSRepository.class.getName());

	@Autowired
	private MongoInstance mongoInstance;
	private static final String prefix = "MONGO_DB_";
	
	public Map<String, ArrayList<Document>> getAllChargebackList(String date){
		try(Session session = HibernateSessionProvider.getSession();){
			Query query=session.createQuery("SELECT merchantPayId From DMSEntity where status='INITIATED' and cbDdlineDate>='"+date+"' GROUP BY merchantPayId");
			List<String> dmsEntities=(List<String>)query.getResultList();
			
			Query query1=session.createQuery("From DMSEntity where status='INITIATED' and cbDdlineDate>='"+date+"'");
			List<DMSEntity> dmsEntities1=(List<DMSEntity>)query1.getResultList();
			
			
			MongoDatabase dbIns = mongoInstance.getDB();
			MongoCollection<Document> collection = dbIns.getCollection(
					PropertiesManager.propertiesMap.get(prefix + Constants.TRANSACTION_STATUS_COLLECTION.getValue()));
			
			Map<String, ArrayList<Document>>map=new HashMap<>();
			
			dmsEntities.parallelStream().forEach(t->{
				map.put(t.trim(), new ArrayList<>());
			});
			
			
			dmsEntities1.parallelStream().forEach(t->{
				String pgRefNum=t.getPgRefNo();
			
				BasicDBObject basicDBObject=new BasicDBObject();
				basicDBObject.put(FieldType.PG_REF_NUM.getName(), pgRefNum);
				basicDBObject.put(FieldType.STATUS.getName(), StatusType.CHARGEBACK_INITIATED.getName());
				
				MongoCursor<Document>cursor=collection.find(basicDBObject).iterator();
				
				if (cursor.hasNext()) {
					Document document=(Document)cursor.next();
					document.append("EMAIL_NOTIFICATION", t.getNemail());
					String payId=document.getString(FieldType.PAY_ID.getName());
					ArrayList<Document>d=map.get(payId);
					d.add(document);
					map.computeIfPresent(payId, (k, v) ->d );
				}				
				
			});
			return map;
		}catch (Exception e) {
			logger.error("Exception Occure getAllChargebackList() ",e);
			e.printStackTrace();
		}
		return null;
	}
	
	public Document getChargebackByPGRefNumber(String pgRefNumber){
		MongoDatabase dbIns = mongoInstance.getDB();
		MongoCollection<Document> collection = dbIns.getCollection(
				PropertiesManager.propertiesMap.get(prefix + Constants.TRANSACTION_STATUS_COLLECTION.getValue()));
		BasicDBObject basicDBObject=new BasicDBObject();
		basicDBObject.put(FieldType.PG_REF_NUM.getName(), pgRefNumber);
		
		MongoCursor<Document>cursor=collection.find(basicDBObject).iterator();
		Document document=null;
		if (cursor.hasNext()) {
			document =cursor.next();
		}
		logger.info("Request Data for getChargebackByPGRefNumber : " + document.toJson());
		return document;
	}
}
