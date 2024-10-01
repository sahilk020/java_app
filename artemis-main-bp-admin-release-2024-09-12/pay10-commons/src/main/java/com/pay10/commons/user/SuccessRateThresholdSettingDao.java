package com.pay10.commons.user;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.bson.Document;
import org.hibernate.HibernateException;
import org.hibernate.ObjectNotFoundException;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.google.common.collect.Iterators;
import com.mongodb.BasicDBObject;
import com.mongodb.BasicDBObjectBuilder;
import com.mongodb.client.AggregateIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import com.pay10.commons.dao.HibernateAbstractDao;
import com.pay10.commons.dao.HibernateSessionProvider;
import com.pay10.commons.dao.SuccessRateThresholdSetting;
import com.pay10.commons.exception.DataAccessLayerException;
import com.pay10.commons.mongo.MongoInstance;
import com.pay10.commons.util.FieldType;
import com.pay10.commons.util.PropertiesManager;
import com.pay10.commons.util.StatusType;


/**
 * This dao class is used to communicate with databases like MySQL and Mongodb in order to fetch information related to success rate calculations.
 */
@Component("successRateThresholdSettingDao")
public class SuccessRateThresholdSettingDao extends HibernateAbstractDao {

    @Autowired
    private MongoInstance mongoInstance;



    private static Logger logger = LoggerFactory.getLogger(SuccessRateThresholdSettingDao.class.getName());

    public SuccessRateThresholdSettingDao() {
        super();

    }


    /**
     * This method save SuccessRateThreshold setting in MySql db table (SuccessRateThresholdSetting).
     */
    public String save(SuccessRateThresholdSetting setting) {

        int x = getAcceptableSuccessRate(setting.getAcquirerName(), setting.getPaymentType(), setting.getMopType(), setting.getPayId());

        if(x != -1){
            logger.info("Entry Already exists");
            throw new RuntimeException("Entry Already Exists");
//            return "Entry Already Exists";
        }
            super.save(setting);
            return "Entry Added Successfully";
    }

    /**
     * This method fetches all the success rate setting present in MySql db Table (SuccessRateThresholdSetting).
     */
    public List<SuccessRateThresholdSetting> getAllSuccessThresholdSettings() {

        Session session = HibernateSessionProvider.getSession();
        Transaction tx = session.beginTransaction();
        List<SuccessRateThresholdSetting> successRateThresholdSettingList = new ArrayList<>();
        try {
            successRateThresholdSettingList = session.createQuery("from SuccessRateThresholdSetting s").getResultList();
            tx.commit();
        } catch (HibernateException objectNotFound) {
            handleException(objectNotFound,tx);
        } finally {
            autoClose(session);
        }
        return successRateThresholdSettingList;
    }


    /**
     * This method fetches all the success rate setting of a acquirer from MySql db Table (SuccessRateThresholdSetting).
     */
    public List<SuccessRateThresholdSetting> getAllSuccessThresholdSettingsOfAcquirer(String acqName) {
        Session session = HibernateSessionProvider.getSession();
        Transaction tx = session.beginTransaction();

        List<SuccessRateThresholdSetting> successRateThresholdSettingList = new ArrayList<>();

        try {
            successRateThresholdSettingList = session.createQuery("from SuccessRateThresholdSetting s where s.acquirerName= :acqName").setParameter("acqName", acqName).getResultList();
            tx.commit();
        } catch (HibernateException objectNotFound) {
            handleException(objectNotFound,tx);
        } finally {
            autoClose(session);
        }
        return successRateThresholdSettingList;
    }

    /**
     * This method fetches success rate specific to acquirer name, payment type and mop type from MySql db Table (SuccessRateThresholdSetting).
     */
    public int getAcceptableSuccessRate(String acqName, String paymentType, String mopType, String payId){
        Session session = HibernateSessionProvider.getSession();
        Transaction tx = session.beginTransaction();
        List<SuccessRateThresholdSetting> successRateThresholdSettingList = new ArrayList<>();

        try {
            successRateThresholdSettingList = session.createQuery("from SuccessRateThresholdSetting S" +
                    " where S.acquirerName = :acqName and S.mopType = :mopType and S.paymentType = :paymentType and S.payId = :payId")
                    .setParameter("acqName", acqName)
                    .setParameter("mopType", mopType)
                    .setParameter("paymentType", paymentType)
                    .setParameter("payId", payId)
                    .getResultList();
            tx.commit();
        } catch (ObjectNotFoundException objectNotFound) {
            handleException(objectNotFound,tx);
        } catch (HibernateException hibernateException) {
            handleException(hibernateException,tx);
        } finally {
            autoClose(session);
        }

        if(successRateThresholdSettingList.size() == 1) return successRateThresholdSettingList.get(0).getSuccessRate();
        return -1;
    }

    /**
     * This method update SuccessRateThreshold setting in MySql db table (SuccessRateThresholdSetting).
     */
    public void update(SuccessRateThresholdSetting setting) throws DataAccessLayerException {
        super.saveOrUpdate(setting);
    }


    /**
     * This method updates the success rate of a particular setting having acqName, payment type and mop type, present in MySql db Table (SuccessRateThresholdSetting).
     */
    public void updateSuccessThresholdSetting(SuccessRateThresholdSetting setting) {

        String acqName = setting.getAcquirerName();
        String paymentType = setting.getPaymentType();
        String mopType = setting.getMopType();
        int successRate = setting.getSuccessRate();
        Date updateDate = setting.getUpdateDate();
        String updatedBy = setting.getUpdatedBy();
        String email = setting.getEmail();



        Session session = HibernateSessionProvider.getSession();
        Transaction tx = session.beginTransaction();
        try {
            session.createQuery("update SuccessRateThresholdSetting S set S.successRate = :successRate, S.email = :email, S.updatedBy = :updatedBy, S.updateDate = :updateDate" +
                    " where S.acquirerName = :acqName and S.mopType = :mopType and S.paymentType = :paymentType")
                    .setParameter("successRate", successRate)
                    .setParameter("updateDate", updateDate)
                    .setParameter("updatedBy", updatedBy)
                    .setParameter("acqName", acqName)
                    .setParameter("mopType", mopType)
                    .setParameter("paymentType", paymentType)
                    .setParameter("email", email)
                    .executeUpdate();
            tx.commit();
        } catch (ObjectNotFoundException objectNotFound) {
            handleException(objectNotFound,tx);
        } catch (HibernateException hibernateException) {
            handleException(hibernateException,tx);
        } finally {
            autoClose(session);
        }
    }


    public void deleteSuccessThresholdSetting(SuccessRateThresholdSetting setting) {

        String acqName = setting.getAcquirerName();
        String paymentType = setting.getPaymentType();
        String mopType = setting.getMopType();
        int successRate = setting.getSuccessRate();
        Date updateDate = setting.getUpdateDate();
        String updatedBy = setting.getUpdatedBy();
        String email = setting.getEmail();



        Session session = HibernateSessionProvider.getSession();
        Transaction tx = session.beginTransaction();
        try {
            session.createQuery("delete SuccessRateThresholdSetting S" +
                    " where S.acquirerName = :acqName and S.mopType = :mopType and S.paymentType = :paymentType")
                    .setParameter("acqName", acqName)
                    .setParameter("mopType", mopType)
                    .setParameter("paymentType", paymentType)
                    .executeUpdate();
            tx.commit();
        } catch (ObjectNotFoundException objectNotFound) {
            handleException(objectNotFound,tx);
        } catch (HibernateException hibernateException) {
            handleException(hibernateException,tx);
        } finally {
            autoClose(session);
        }
    }


    /**
     * This method returns the total number of transactions happened in given start time and end time with the help of mongodb query on collection transactionStatus.
     */
    public int getTotalNumberOfTxns(String startTime, String endTime){
        MongoDatabase  dbIns = mongoInstance.getDB();
        MongoCollection<Document> transactionStatusCollection = dbIns.getCollection(PropertiesManager.propertiesMap.get("MONGO_DB_transactionStatus"));
        BasicDBObject dateTimeQuery = new BasicDBObject();
        dateTimeQuery.put(FieldType.CREATE_DATE.getName(),
                BasicDBObjectBuilder.start("$gte", new SimpleDateFormat(startTime).toLocalizedPattern())
                        .add("$lt", new SimpleDateFormat(endTime).toLocalizedPattern()).get());

        BasicDBObject match = new BasicDBObject("$match", dateTimeQuery);

        List<BasicDBObject> pipeline = Arrays.asList(match);

        AggregateIterable<Document> output = transactionStatusCollection.aggregate(pipeline);
        output.allowDiskUse(true);
        MongoCursor<Document> cursor = output.iterator();
        return Iterators.size(cursor);


//        BasicDBObject count = new BasicDBObject("$count", "totalCount");
//
//        List<BasicDBObject> pipeline = Arrays.asList(match,count);
//        Document output = transactionStatusCollection.aggregate(pipeline).first();
//
//        int total;
//
//        try{
//            total = (int) output.get("totalCount");
//        }
//        catch (NullPointerException e){
//            logger.info("No transaction present");
//            total = 0;
//        }

//        return total;
    }

    /**
     * This method returns the total number of transactions happened in given start time and end time specific to particular acquirerName, paymentType, mopType with the help of mongodb query on collection transactionStatus.
     */
    public int getIndividualAcquirerTotalTxnsCount(String startTime, String endTime, String acqName, String paymentType, String mopType, String payId) {

        MongoDatabase  dbIns = mongoInstance.getDB();
        MongoCollection<Document> transactionStatusCollection = dbIns.getCollection(PropertiesManager.propertiesMap.get("MONGO_DB_transactionStatus"));
        BasicDBObject dateTimeQuery = new BasicDBObject();
        dateTimeQuery.put(FieldType.CREATE_DATE.getName(),
                BasicDBObjectBuilder.start("$gte", new SimpleDateFormat(startTime).toLocalizedPattern())
                        .add("$lt", new SimpleDateFormat(endTime).toLocalizedPattern()).get());

        List<BasicDBObject> allConditionQueryList = new ArrayList<BasicDBObject>();
        allConditionQueryList.add(dateTimeQuery);

        if(!acqName.equals("ALL"))
        {
            BasicDBObject acqQuery = new BasicDBObject(FieldType.ACQUIRER_TYPE.getName(), acqName);
            allConditionQueryList.add(acqQuery);
        }
        if(!paymentType.equals("ALL"))
        {
            BasicDBObject paymentTypeQuery = new BasicDBObject(FieldType.PAYMENT_TYPE.getName(), paymentType);
            allConditionQueryList.add(paymentTypeQuery);
        }
        if(!mopType.equals("ALL"))
        {
            BasicDBObject mopTypeQuery = new BasicDBObject(FieldType.MOP_TYPE.getName(), mopType);
            allConditionQueryList.add(mopTypeQuery);
        }
        if(!payId.equals("ALL"))
        {
            BasicDBObject payIdQuery = new BasicDBObject(FieldType.PAY_ID.getName(), payId);
            allConditionQueryList.add(payIdQuery);
        }

        BasicDBObject finalquery = new BasicDBObject("$and", allConditionQueryList);


        BasicDBObject match = new BasicDBObject("$match", finalquery);

        List<BasicDBObject> pipeline = Arrays.asList(match);
        AggregateIterable<Document> output = transactionStatusCollection.aggregate(pipeline);
        output.allowDiskUse(true);
        MongoCursor<Document> cursor = output.iterator();
        return Iterators.size(cursor);

//        BasicDBObject count = new BasicDBObject("$count", "totalCount");
//
//        List<BasicDBObject> pipeline = Arrays.asList(match,count);
//        Document output = transactionStatusCollection.aggregate(pipeline).first();
//
//        int total;
//
//        try{
//            total = (int) output.get("totalCount");
//        }
//        catch (NullPointerException e){
//            logger.info("No transaction present");
//            total = 0;
//        }
//
//        return total;
    }

    /**
     * This method returns the total number of success transactions (having CAPTURED or SETTLED status) happened in given start time and end time with the help of mongodb query on collection transactionStatus.
     */
    public int getTotalNumberOfSuccessTxns(String startTime, String endTime){
        MongoDatabase  dbIns = mongoInstance.getDB();
        MongoCollection<Document> transactionStatusCollection = dbIns.getCollection(PropertiesManager.propertiesMap.get("MONGO_DB_transactionStatus"));
        BasicDBObject dateTimeQuery = new BasicDBObject();
        dateTimeQuery.put(FieldType.CREATE_DATE.getName(),
                BasicDBObjectBuilder.start("$gte", new SimpleDateFormat(startTime).toLocalizedPattern())
                        .add("$lt", new SimpleDateFormat(endTime).toLocalizedPattern()).get());

        BasicDBObject capturedQuery = new BasicDBObject(FieldType.ALIAS_STATUS.getName(), StatusType.CAPTURED.getName());
        BasicDBObject settledQuery = new BasicDBObject(FieldType.ALIAS_STATUS.getName(), StatusType.SETTLED_SETTLE.getName());
        List<BasicDBObject> allSuccessStatusQueries = new ArrayList<BasicDBObject>();
        allSuccessStatusQueries.add(capturedQuery);
        allSuccessStatusQueries.add(settledQuery);

        BasicDBObject statusQuery = new BasicDBObject("$or", allSuccessStatusQueries);

        List<BasicDBObject> allConditionQueryList = new ArrayList<BasicDBObject>();
        allConditionQueryList.add(dateTimeQuery);
        allConditionQueryList.add(statusQuery);

        BasicDBObject finalQuery = new BasicDBObject("$and", allConditionQueryList);

        BasicDBObject match = new BasicDBObject("$match", finalQuery);
        List<BasicDBObject> pipeline = Arrays.asList(match);
        AggregateIterable<Document> output = transactionStatusCollection.aggregate(pipeline);
        output.allowDiskUse(true);
        MongoCursor<Document> cursor = output.iterator();
        return Iterators.size(cursor);

//        BasicDBObject count = new BasicDBObject("$count", "totalCount");
//
//        List<BasicDBObject> pipeline = Arrays.asList(match,count);
//        Document output = transactionStatusCollection.aggregate(pipeline).first();
//
//        int total;
//
//        try{
//            total = (int) output.get("totalCount");
//        }
//        catch (NullPointerException e){
//            logger.info("No transaction present");
//            total = 0;
//        }
//
//        return total;
    }


    /**
     * This method returns the total number of success transactions (having SUCCESS or SETTLED status) happened in given start time and end time specific to particular acquirerName, paymentType and mopType with the help of mongodb query on collection transactionStatus.
     */
    public int getIndividualAcquirerSuccessTxnCount(String startTime, String endTime, String acqName, String paymentType, String mopType, String payId) {

        MongoDatabase  dbIns = mongoInstance.getDB();
        MongoCollection<Document> transactionStatusCollection = dbIns.getCollection(PropertiesManager.propertiesMap.get("MONGO_DB_transactionStatus"));
        BasicDBObject dateTimeQuery = new BasicDBObject();
        dateTimeQuery.put(FieldType.CREATE_DATE.getName(),
                BasicDBObjectBuilder.start("$gte", new SimpleDateFormat(startTime).toLocalizedPattern())
                        .add("$lt", new SimpleDateFormat(endTime).toLocalizedPattern()).get());

        BasicDBObject capturedQuery = new BasicDBObject(FieldType.ALIAS_STATUS.getName(), StatusType.CAPTURED.getName());
        BasicDBObject settledQuery = new BasicDBObject(FieldType.ALIAS_STATUS.getName(), StatusType.SETTLED_SETTLE.getName());
        List<BasicDBObject> allSuccessStatusQueries = new ArrayList<BasicDBObject>();
        allSuccessStatusQueries.add(capturedQuery);
        allSuccessStatusQueries.add(settledQuery);

        BasicDBObject statusQuery = new BasicDBObject("$or", allSuccessStatusQueries);

        List<BasicDBObject> allConditionQueryList = new ArrayList<BasicDBObject>();
        allConditionQueryList.add(dateTimeQuery);
        allConditionQueryList.add(statusQuery);

        if(!acqName.equals("ALL"))
        {
            BasicDBObject acqQuery = new BasicDBObject(FieldType.ACQUIRER_TYPE.getName(), acqName);
            allConditionQueryList.add(acqQuery);
        }
        if(!paymentType.equals("ALL"))
        {
            BasicDBObject paymentTypeQuery = new BasicDBObject(FieldType.PAYMENT_TYPE.getName(), paymentType);
            allConditionQueryList.add(paymentTypeQuery);
        }
        if(!mopType.equals("ALL"))
        {
            BasicDBObject mopTypeQuery = new BasicDBObject(FieldType.MOP_TYPE.getName(), mopType);
            allConditionQueryList.add(mopTypeQuery);
        }
        if(!payId.equals("ALL"))
        {
            BasicDBObject payIdQuery = new BasicDBObject(FieldType.PAY_ID.getName(), payId);
            allConditionQueryList.add(payIdQuery);
        }


		BasicDBObject finalquery = new BasicDBObject("$and", allConditionQueryList);
		BasicDBObject match = new BasicDBObject("$match", finalquery);

		List<BasicDBObject> pipeline = Arrays.asList(match);
		AggregateIterable<Document> output = transactionStatusCollection.aggregate(pipeline);
		output.allowDiskUse(true);
		MongoCursor<Document> cursor = output.iterator();
		return Iterators.size(cursor);
//        BasicDBObject count = new BasicDBObject("$count", "totalCount");
//        List<BasicDBObject> pipeline = Arrays.asList(match,count);
//        Document output = transactionStatusCollection.aggregate(pipeline).first();
//        int total;
//        try{
//            total = (int) output.get("totalCount");
//        }
//        catch (NullPointerException e){
//            logger.info("No transaction present");
//            total = 0;
//        }
//        return total;
	}

	public SuccessRateThresholdSetting getByMerchantAndAcquirer(String merchant, String acquirer, String paymentType,
			String mopType) {
		Session session = HibernateSessionProvider.getSession();
		Transaction tx = session.beginTransaction();
		SuccessRateThresholdSetting setting;
		try {
			setting = (SuccessRateThresholdSetting) session.createQuery(
					"from SuccessRateThresholdSetting s where s.acquirerName= :acqName and s.merchant= :merchant and s.paymentType=:paymentType and s.mopType=:mopType")
					.setParameter("acqName", acquirer).setParameter("merchant", merchant)
					.setParameter("paymentType", paymentType).setParameter("mopType", mopType).getSingleResult();
			tx.commit();
		} catch (HibernateException objectNotFound) {
			handleException(objectNotFound, tx);
			setting = null;
		} finally {
			autoClose(session);
		}
		return setting;
	}

}
