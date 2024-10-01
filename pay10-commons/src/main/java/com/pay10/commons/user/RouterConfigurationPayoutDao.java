package com.pay10.commons.user;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import javax.persistence.Query;

import org.apache.commons.lang3.StringUtils;
import org.bson.Document;
import org.hibernate.HibernateException;
import org.hibernate.ObjectNotFoundException;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.mongodb.BasicDBObject;
import com.mongodb.BasicDBObjectBuilder;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Sorts;
import com.pay10.commons.dao.HibernateAbstractDao;
import com.pay10.commons.dao.HibernateSessionProvider;
import com.pay10.commons.dao.RouterConfigurationDao;
import com.pay10.commons.exception.DataAccessLayerException;
import com.pay10.commons.mongo.MongoInstance;
import com.pay10.commons.util.Currency;
import com.pay10.commons.util.FieldType;
import com.pay10.commons.util.MopType;
import com.pay10.commons.util.PaymentType;

@Service

public class RouterConfigurationPayoutDao  extends HibernateAbstractDao {
	
	private static Logger logger = LoggerFactory.getLogger(RouterConfigurationDao.class.getName());
	
	@Autowired
	private MongoInstance mongoInstance;
	
	@Autowired
	private MultCurrencyCodeDao multCurrencyCodeDao;

	public void create(RouterConfigurationPayout routerConfigurationPayout) throws DataAccessLayerException {
		super.save(routerConfigurationPayout);
	}

	@SuppressWarnings("unchecked")
	public List<RouterConfigurationPayout> findActiveAcquirersByIdentifier(String identifier) {
		List<RouterConfigurationPayout> rulesList = new ArrayList<RouterConfigurationPayout>();
		boolean currentlyActive = true;
		Session session = HibernateSessionProvider.getSession();
		Transaction tx = session.beginTransaction();
		rulesList = null;
		try {

			rulesList = session.createQuery(
					"from RouterConfigurationPayout RC where RC.identifier = :identifier and RC.currentlyActive = :currentlyActive and RC.status = 'ACTIVE' order by RC.priority asc")
					.setParameter("identifier", identifier).setParameter("currentlyActive", currentlyActive)
					.getResultList();
			tx.commit();
			return rulesList;
		}catch ( Exception e) {
			
			logger.info("error in identifier "+e);
		}
		 finally {
			autoClose(session);
		}
		return rulesList;
	}

	public List<String> findLastTransaction(List<String> acquirersName,String channel,String currency, String payId) {
		List<BasicDBObject> paramConditionLst = new ArrayList<BasicDBObject>();
		List<String> documents=new ArrayList<String>();

		BasicDBObject dateQuery = new BasicDBObject();
		String fromDate = null;
		String currentDate = null;
		
		DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		DateFormat dateFormat1 = new SimpleDateFormat("yyyy-MM-dd");
		Calendar cal = Calendar.getInstance();
		currentDate = dateFormat.format(cal.getTime());
		fromDate = dateFormat1.format(cal.getTime());
		fromDate += " 00:00:00";
		currentDate = dateFormat.format(cal.getTime());
		
		dateQuery.put(FieldType.CREATE_DATE.getName(),
				BasicDBObjectBuilder.start("$gte", new SimpleDateFormat(fromDate).toLocalizedPattern())
						.add("$lte", new SimpleDateFormat(currentDate).toLocalizedPattern()).get());
		
		if (!StringUtils.isBlank(multCurrencyCodeDao.getCurrencyCodeByName(currency)) && !StringUtils.isBlank(channel) && !StringUtils.isBlank(payId)) {
			paramConditionLst.add(new BasicDBObject("CHANNEL", channel));
			paramConditionLst.add(new BasicDBObject("CURRENCY_CODE", multCurrencyCodeDao.getCurrencyCodeByName(currency)));
			paramConditionLst.add(new BasicDBObject("PAY_ID", payId));
		}

		if (acquirersName != null) {
			paramConditionLst.add(new BasicDBObject("ACQUIRER_TYPE", new BasicDBObject("$in", acquirersName)));
		}

		if (!dateQuery.isEmpty()) {
			paramConditionLst.add(dateQuery);
		}
		
		BasicDBObject finalquery = new BasicDBObject("$and", paramConditionLst);

		logger.info("findLastTransaction, finalquery : "+finalquery.toString());
		MongoDatabase dbIns = mongoInstance.getDB();
		MongoCollection<Document> collection = dbIns.getCollection("transactionStatus");
		
		FindIterable<Document> output = collection.find(finalquery).limit(5).sort(Sorts.descending("CREATE_DATE"));
		logger.info("findLastTransaction : "+output);
		MongoCursor<Document> result = output.iterator();

		while (result.hasNext()) {
			Document document = (Document) result.next();
			// DashBoard dashBoard=new Gson().fromJson(document.toJson(), DashBoard.class);
			//System.out.println(document.getString("_id") + "\t" + document.getString("ACQUIRER_TYPE") + "\t" + document.getString("PAYMENT_TYPE") + "\t" + document.getString("MOP_TYPE") + "\t" + document.getString("CREATE_DATE"));
			documents.add(document.toJson());
		}
		return documents;
	}
 	

	public boolean lastFiveTransaction(String acquirerName, String channel, String currency, int cntLimit, String payId) {

		logger.info("acquirerName : "+acquirerName+ ", channel : "+channel+ ", currency : "+currency+ ", cntLimit : "+cntLimit);
		List<BasicDBObject> paramConditionLst = new ArrayList<BasicDBObject>();
		BasicDBObject dateQuery = new BasicDBObject();
		String fromDate = null;
		String currentDate = null;
		
		DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		DateFormat dateFormat1 = new SimpleDateFormat("yyyy-MM-dd");
		Calendar cal = Calendar.getInstance();
		currentDate = dateFormat.format(cal.getTime());
		fromDate = dateFormat1.format(cal.getTime());
		fromDate += " 00:00:01";
		currentDate = dateFormat.format(cal.getTime());
		
		dateQuery.put(FieldType.CREATE_DATE.getName(),
				BasicDBObjectBuilder.start("$gte", new SimpleDateFormat(fromDate).toLocalizedPattern())
						.add("$lte", new SimpleDateFormat(currentDate).toLocalizedPattern()).get());
		
		if (!StringUtils.isBlank(channel) && !StringUtils.isBlank(currency)) {
			paramConditionLst.add(new BasicDBObject("CURRENCY_CODE", multCurrencyCodeDao.getCurrencyCodeByName(currency)));
			paramConditionLst.add(new BasicDBObject("CHANNEL", channel));
		}
		if (!StringUtils.isBlank(acquirerName)) {
			paramConditionLst.add(new BasicDBObject("ACQUIRER_TYPE", acquirerName));
		}
		if (!StringUtils.isBlank(payId)) {
			paramConditionLst.add(new BasicDBObject("PAY_ID", payId));
		}
		if (!dateQuery.isEmpty()) {
			paramConditionLst.add(dateQuery);
		}
		
		BasicDBObject finalquery = new BasicDBObject("$and", paramConditionLst);
		MongoDatabase dbIns = mongoInstance.getDB();
		MongoCollection<Document> collection = dbIns.getCollection("POtransactionStatus");

		logger.info("lastFiveTransaction , finalquery = " + finalquery);
		
		FindIterable<Document> output = collection.find(finalquery).limit(cntLimit).sort(Sorts.descending("CREATE_DATE"));
		MongoCursor<Document> result = output.iterator();

		int cnt = 0;
		while (result.hasNext()) {
			cnt ++;
			logger.info("Transaction Cnt : "+cnt);
			Document document = (Document) result.next();
			if(StringUtils.equalsAnyIgnoreCase(document.getString("STATUS"), "Captured")) {
				return true;
			}
		}

		if(cnt < cntLimit) {
			return true;
		}
		return false;
	}
	
	public int disableRCForFailedCountExceededById_again(long id) {

		logger.info("Request Received for disableRCForFailedCountExceededById_again , id :: " + id);
		Session session = HibernateSessionProvider.getSession();
		Transaction tx = session.beginTransaction();
		int result = 0;
		try {

			Query query = session.createSQLQuery("update RouterConfigurationPayout set currentlyActive = :currentlyActive , isDown = :isDown where Id = :Id");
			query.setParameter("currentlyActive", false);
			query.setParameter("isDown", true);
			//query.setParameter("failureCount", 0);
			query.setParameter("Id", id);
			result = query.executeUpdate();

			logger.info("Result Update For Freeze Transaction Amount Is : " + result);
			tx.commit();
			return result;
		} catch (ObjectNotFoundException objectNotFound) {
			handleException(objectNotFound, tx);
		} catch (HibernateException hibernateException) {
			handleException(hibernateException, tx);
		} finally {
			autoClose(session);
		}
		return result;
	}
	
	public void increaseFailedCntForRouterConfiguration(long id) {

		logger.info("Request Received for increaseFailedCntForRouterConfiguration , id :: " + id );
		Session session = HibernateSessionProvider.getSession();
		Transaction tx = session.beginTransaction();
		try {

			Query query = session.createSQLQuery("update RouterConfigurationPayout set failureCount = failureCount + :failureCount where Id = :Id");
			query.setParameter("failureCount", 1);
			query.setParameter("Id", id);
			int result = query.executeUpdate();

			logger.info("Result Update For increaseFailedCntForRouterConfiguration : " + result);
			tx.commit();

		} catch (ObjectNotFoundException objectNotFound) {
			handleException(objectNotFound, tx);
		} catch (HibernateException hibernateException) {
			handleException(hibernateException, tx);
		} finally {
			autoClose(session);
		}
	

	}
	public int disableRCForFailedCountExceededById(long id) {

		logger.info("Request Received for disableRCForMaxAmountExceedById, id :: " + id);
		Session session = HibernateSessionProvider.getSession();
		Transaction tx = session.beginTransaction();
		int result = 0;
		try {

			Query query = session.createSQLQuery("update RouterConfigurationPayout set currentlyActive = :currentlyActive , isDown = :isDown where Id = :Id");
			query.setParameter("currentlyActive", false);
			query.setParameter("isDown", true);
			query.setParameter("isDown", true);
			query.setParameter("Id", id);
			result = query.executeUpdate();

			logger.info("Result Update For Freeze Transaction Amount Is : " + result);
			tx.commit();
			return result;
		} catch (ObjectNotFoundException objectNotFound) {
			handleException(objectNotFound, tx);
		} catch (HibernateException hibernateException) {
			handleException(hibernateException, tx);
		} finally {
			autoClose(session);
		}
		return result;
	}

	@SuppressWarnings("unchecked")
	public List<RouterConfigurationPayout> getPendingRulesByRouterRule(RouterRulePayout routerRule) {
		List<RouterConfigurationPayout> rulesList = new ArrayList<RouterConfigurationPayout>();

		String currency = routerRule.getCurrency();
		String	channel = routerRule.getChannel();
		

			String mopType = routerRule.getCurrency();
		

		String merchant = routerRule.getPayId();
		String transactionType = routerRule.getTransactionType();
		AccountCurrencyRegion paymentsRegion = routerRule.getPaymentsRegion();
		CardHolderType cardHolderType = routerRule.getCardHolderType();

		Session session = HibernateSessionProvider.getSession();
		Transaction tx = session.beginTransaction();
		try {

			rulesList = session.createQuery(
					"from RouterConfigurationPayout RC where RC.merchant = :merchant and RC.cardHolderType = :cardHolderType and RC.channel = :channel "
							+ " and RC.currency = :currency and RC.paymentsRegion = :paymentsRegion and RC.transactionType = :transactionType and RC.status='PENDING'")
					.setParameter("paymentsRegion", paymentsRegion)
					.setParameter("transactionType", transactionType).setParameter("merchant", merchant)
					.setParameter("currency", currency).setParameter("cardHolderType", cardHolderType)
					.setParameter("channel", channel).setCacheable(true).getResultList();
			tx.commit();
			return rulesList;
		} catch (ObjectNotFoundException objectNotFound) {
			handleException(objectNotFound, tx);
		} catch (HibernateException hibernateException) {
			handleException(hibernateException, tx);
		} finally {
			autoClose(session);
		}
		return rulesList;
	}

	@SuppressWarnings("unchecked")
	public List<RouterConfigurationPayout> findRulesByIdentifier(String identifier) {
		List<RouterConfigurationPayout> rulesList = new ArrayList<RouterConfigurationPayout>();
		rulesList = null;
		Session session = HibernateSessionProvider.getSession();
		Transaction tx = session.beginTransaction();
		try {

			rulesList = session
					.createQuery("from RouterConfigurationPayout RC where RC.identifier = :identifier and RC.status='ACTIVE'")
					.setParameter("identifier", identifier).setCacheable(true).getResultList();
			tx.commit();
			return rulesList;
		} catch (ObjectNotFoundException objectNotFound) {
			handleException(objectNotFound, tx);
		} catch (HibernateException hibernateException) {
			handleException(hibernateException, tx);
		} finally {
			autoClose(session);
		}
		return rulesList;
	}

	@SuppressWarnings("unchecked")
	public List<RouterConfigurationPayout> getActiveRulesByRouterRule(RouterRulePayout routerRule) {
		List<RouterConfigurationPayout> rulesList = new ArrayList<RouterConfigurationPayout>();

		

		String currency=routerRule.getCurrency();
		String  channel =routerRule.getChannel();

	
		String merchant = routerRule.getPayId();
		String transactionType = routerRule.getTransactionType();
		AccountCurrencyRegion paymentsRegion = routerRule.getPaymentsRegion();
		CardHolderType cardHolderType = routerRule.getCardHolderType();

		Session session = HibernateSessionProvider.getSession();
		Transaction tx = session.beginTransaction();
		try {

			rulesList = session.createQuery(
					"from RouterConfigurationPayout RC where RC.merchant = :merchant and RC.cardHolderType = :cardHolderType and RC.channel = :channel "
							+ " and RC.currency = :currency and RC.paymentsRegion = :paymentsRegion  and RC.transactionType = :transactionType and RC.status='ACTIVE'")
					.setParameter("paymentsRegion", paymentsRegion)
					.setParameter("transactionType", transactionType).setParameter("merchant", merchant)
					.setParameter("currency", currency).setParameter("cardHolderType", cardHolderType)
					.setParameter("channel", channel).setCacheable(true).getResultList();
			tx.commit();
			return rulesList;
		} catch (ObjectNotFoundException objectNotFound) {
			handleException(objectNotFound, tx);
		} catch (HibernateException hibernateException) {
			handleException(hibernateException, tx);
		} finally {
			autoClose(session);
		}
		return rulesList;
	}

	@SuppressWarnings("unchecked")
public List<RouterConfigurationPayout> getActiveRulesByMerchant(String merchant, String cardHolder, String channel, String currency) {

			List<RouterConfigurationPayout> rulesList = new ArrayList<RouterConfigurationPayout>();

			cardHolder="CONSUMER";

			CardHolderType cardHolderType = CardHolderType.CONSUMER;

			if (cardHolder.equalsIgnoreCase("CONSUMER")) {

				cardHolderType = CardHolderType.CONSUMER;

			} else {

				cardHolderType = CardHolderType.COMMERCIAL;

			}

			rulesList = null;

			Session session = HibernateSessionProvider.getSession();

			Transaction tx = session.beginTransaction();

			try {
	 
				rulesList = session.createQuery(

						"from RouterConfigurationPayout RC where RC.merchant = :merchant and RC.cardHolderType = :cardHolderType and RC.channel = :channel and RC.currency = :currency and RC.status='ACTIVE'")

						.setParameter("merchant", merchant).setParameter("cardHolderType", cardHolderType)

						.setParameter("channel", channel).setParameter("currency", currency).setCacheable(true).getResultList();
	 
				tx.commit();

				return rulesList;

			} catch (ObjectNotFoundException objectNotFound) {

				handleException(objectNotFound, tx);

			} catch (HibernateException hibernateException) {

				handleException(hibernateException, tx);

			} finally {

				autoClose(session);

			}

			return rulesList;

		}


}
