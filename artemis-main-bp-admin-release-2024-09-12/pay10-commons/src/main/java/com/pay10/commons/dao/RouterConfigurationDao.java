package com.pay10.commons.dao;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
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
import com.pay10.commons.exception.DataAccessLayerException;
import com.pay10.commons.mongo.MongoInstance;
import com.pay10.commons.user.AccountCurrencyRegion;
import com.pay10.commons.user.BinCardInfo;
import com.pay10.commons.user.CardHolderType;
import com.pay10.commons.user.RouterConfiguration;
import com.pay10.commons.user.RouterRule;
import com.pay10.commons.user.RouterRuleSearchObject;
import com.pay10.commons.user.User;
import com.pay10.commons.util.Currency;
import com.pay10.commons.util.FieldType;
import com.pay10.commons.util.MopType;
import com.pay10.commons.util.PaymentType;
import com.pay10.commons.util.TDRStatus;

/**
 * @author Rahul
 *
 */

@Service
//@Scope(value = "prototype", proxyMode=ScopedProxyMode.TARGET_CLASS)
public class RouterConfigurationDao extends HibernateAbstractDao {

	private static Logger logger = LoggerFactory.getLogger(RouterConfigurationDao.class.getName());
	
	@Autowired
	private MongoInstance mongoInstance;

	public void create(RouterConfiguration routerConfiguration) throws DataAccessLayerException {
		super.save(routerConfiguration);
	}

	public void delete(Long id) {
		try {
			RouterConfiguration routerConfiguration = findRule(id);
			if (null != routerConfiguration) {
				routerConfiguration.setStatus(TDRStatus.INACTIVE);
				routerConfiguration.setUpdatedDate(new Date());
			}
			super.saveOrUpdate(routerConfiguration);
		} catch (HibernateException e) {
			// handleException(e);
		} finally {
			// autoClose(session);
		}
	}

	public void delete(Long id, User user) {
		try {
			RouterConfiguration routerConfiguration = findRule(id);
			if (null != routerConfiguration) {
				routerConfiguration.setStatus(TDRStatus.INACTIVE);
				routerConfiguration.setUpdatedDate(new Date());
				routerConfiguration.setUpdatedBy(user.getEmailId());
			}
			super.saveOrUpdate(routerConfiguration);
		} catch (HibernateException e) {
			// handleException(e);
		} finally {
			// autoClose(session);
		}
	}

	public void deletePending(Long id, User user) {
		try {
			RouterConfiguration routerConfiguration = findPendingRule(id);
			if (null != routerConfiguration) {
				routerConfiguration.setStatus(TDRStatus.INACTIVE);
				routerConfiguration.setUpdatedDate(new Date());
				routerConfiguration.setUpdatedBy(user.getEmailId());
			}
			super.saveOrUpdate(routerConfiguration);
		} catch (HibernateException e) {
			// handleException(e);
		} finally {
			// autoClose(session);
		}
	}

	public void delete(RouterConfiguration routerConfiguration) {
		try {
			if (null != routerConfiguration) {
				routerConfiguration.setStatus(TDRStatus.INACTIVE);
			}
			super.saveOrUpdate(routerConfiguration);
		} catch (HibernateException e) {
		} finally {
		}
	}

	public RouterConfiguration findRule(Long id) {
		RouterConfiguration routerConfiguration = null;
		Session session = HibernateSessionProvider.getSession();
		Transaction tx = session.beginTransaction();
		try {
			routerConfiguration = (RouterConfiguration) session
					.createQuery("from RouterConfiguration RR where RR.id = :id and RR.status = 'ACTIVE' ")
					.setParameter("id", id).setCacheable(true).uniqueResult();
			tx.commit();
		} catch (ObjectNotFoundException objectNotFound) {
			handleException(objectNotFound, tx);
		} catch (HibernateException hibernateException) {
			handleException(hibernateException, tx);
		} finally {
			autoClose(session);
			// return responseUser;
		}
		return routerConfiguration;

	}

	public RouterConfiguration findPendingRule(Long id) {
		RouterConfiguration routerConfiguration = null;
		Session session = HibernateSessionProvider.getSession();
		Transaction tx = session.beginTransaction();
		try {
			routerConfiguration = (RouterConfiguration) session
					.createQuery("from RouterConfiguration RR where RR.id = :id and RR.status = 'PENDING' ")
					.setParameter("id", id).setCacheable(true).uniqueResult();
			tx.commit();
		} catch (ObjectNotFoundException objectNotFound) {
			handleException(objectNotFound, tx);
		} catch (HibernateException hibernateException) {
			handleException(hibernateException, tx);
		} finally {
			autoClose(session);
			// return responseUser;
		}
		return routerConfiguration;

	}

	@SuppressWarnings("unchecked")
	public List<RouterConfiguration> findRulesByIdentifier(String identifier) {
		List<RouterConfiguration> rulesList = new ArrayList<RouterConfiguration>();
		rulesList = null;
		Session session = HibernateSessionProvider.getSession();
		Transaction tx = session.beginTransaction();
		try {

			rulesList = session
					.createQuery("from RouterConfiguration RC where RC.identifier = :identifier and RC.status='ACTIVE'")
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
	public List<RouterConfiguration> findPendingRulesByIdentifier(String identifier) {
		List<RouterConfiguration> rulesList = new ArrayList<RouterConfiguration>();
		rulesList = null;
		Session session = HibernateSessionProvider.getSession();
		Transaction tx = session.beginTransaction();
		try {

			rulesList = session
					.createQuery(
							"from RouterConfiguration RC where RC.identifier = :identifier and RC.status='PENDING'")
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
	public List<RouterConfiguration> getPendingRouterConfiguration() {
		List<RouterConfiguration> rulesList = new ArrayList<RouterConfiguration>();
		rulesList = null;
		Session session = HibernateSessionProvider.getSession();
		Transaction tx = session.beginTransaction();
		try {

			rulesList = session.createQuery("from RouterConfiguration RC where RC.status='PENDING'").setCacheable(true)
					.getResultList();
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
	public List<RouterConfiguration> findSortedRulesByIdentifier(String identifier) {
		List<RouterConfiguration> rulesList = new ArrayList<RouterConfiguration>();
		rulesList = null;
		boolean currentlyActive = true;
		Session session = HibernateSessionProvider.getSession();
		Transaction tx = session.beginTransaction();
		try {

			rulesList = session.createQuery(
					"from RouterConfiguration RC where RC.identifier = :identifier and RC.status='ACTIVE' and RC.currentlyActive = :currentlyActive order by RC.priority asc")
					.setParameter("identifier", identifier).setParameter("currentlyActive", currentlyActive)
					.setCacheable(true).getResultList();
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
	public RouterConfiguration findNextAvailableConfigurationByIdentifier(String identifier) {
		RouterConfiguration routerConfiguration = new RouterConfiguration();
		routerConfiguration = null;
		boolean isDown = false;
		Session session = HibernateSessionProvider.getSession();
		Transaction tx = session.beginTransaction();
		try {
			routerConfiguration = (RouterConfiguration) session
					.createQuery("from RouterConfiguration RC where RC.identifier = :identifier "
							+ "and RC.isDown := isDown and RC.status = 'ACTIVE' order by RC.priority asc limit 1;")
					.setParameter("identifier", identifier).setCacheable(true).getSingleResult();
			tx.commit();
			return routerConfiguration;
		} catch (ObjectNotFoundException objectNotFound) {
			handleException(objectNotFound, tx);
		} catch (HibernateException hibernateException) {
			handleException(hibernateException, tx);
		} finally {
			autoClose(session);
		}
		return routerConfiguration;
	}

	@SuppressWarnings("unchecked")
	public RouterConfiguration findRulesByIdentifierAcquirer(String identifier, String acquirer) {

		RouterConfiguration routerConfiguration = new RouterConfiguration();
		routerConfiguration = null;
		Session session = HibernateSessionProvider.getSession();
		Transaction tx = session.beginTransaction();
		try {

			routerConfiguration = (RouterConfiguration) session.createQuery(
					"from RouterConfiguration RC where RC.identifier = :identifier and RC.acquirer = :acquirer and RC.status = 'ACTIVE'")
					.setParameter("identifier", identifier).setParameter("acquirer", acquirer).setCacheable(true)
					.getSingleResult();
			tx.commit();
			return routerConfiguration;
		} catch (ObjectNotFoundException objectNotFound) {
			handleException(objectNotFound, tx);
		} catch (HibernateException hibernateException) {
			handleException(hibernateException, tx);
		} finally {
			autoClose(session);
		}
		return routerConfiguration;
	}

	@SuppressWarnings("unchecked")
	public boolean checkAvailableAcquirers(String identifier, String acquirer) {
		List<RouterConfiguration> rulesList = new ArrayList<RouterConfiguration>();
		boolean isDown = false;
		rulesList = null;
		Session session = HibernateSessionProvider.getSession();
		Transaction tx = session.beginTransaction();
		try {

			rulesList = session.createQuery(
					"from RouterConfiguration RC where RC.identifier = :identifier and RC.acquirer != :acquirer and RC.isDown = :isDown and RC.status = 'ACTIVE'")
					.setParameter("identifier", identifier).setParameter("isDown", isDown)
					.setParameter("acquirer", acquirer).setCacheable(true).getResultList();
			tx.commit();

			if (rulesList.size() > 0) {
				return true;
			} else {
				return false;
			}
		} catch (ObjectNotFoundException objectNotFound) {
			handleException(objectNotFound, tx);
		} catch (HibernateException hibernateException) {
			handleException(hibernateException, tx);
		} finally {
			autoClose(session);
		}
		return true;
	}

	@SuppressWarnings("unchecked")
	public List<RouterConfiguration> findActiveAcquirersByIdentifier(String identifier) {
		List<RouterConfiguration> rulesList = new ArrayList<RouterConfiguration>();
		boolean currentlyActive = true;
		Session session = HibernateSessionProvider.getSession();
		Transaction tx = session.beginTransaction();
		rulesList = null;
		try {

			rulesList = session.createQuery(
					"from RouterConfiguration RC where RC.identifier = :identifier and RC.currentlyActive = :currentlyActive and RC.status = 'ACTIVE' order by RC.priority asc")
					.setParameter("identifier", identifier).setParameter("currentlyActive", currentlyActive)
					.getResultList();
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
	public List<RouterConfiguration> findAcquirerByPayId(String payId) {
		List<RouterConfiguration> rulesList = new ArrayList<RouterConfiguration>();
		Session session = HibernateSessionProvider.getSession();
		Transaction tx = session.beginTransaction();
		rulesList = null;
		try {

			rulesList = session.createQuery(
					"from RouterConfiguration RC where RC.merchant = :merchant and RC.acquirer IN('PAYU','SBI','SBICARD') and RC.paymentType IN ('CC','DC') and RC.status = 'ACTIVE'")
					.setParameter("merchant", payId).getResultList();
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

	public RouterConfiguration findNextActiveAcquirer(String identifier) {
		RouterConfiguration routerConfiguration = new RouterConfiguration();
		boolean currentlyActive = true;
		Session session = HibernateSessionProvider.getSession();
		Transaction tx = session.beginTransaction();
		routerConfiguration = null;
		try {

			routerConfiguration = (RouterConfiguration) session.createQuery(
					"from RouterConfiguration RC where RC.identifier = :identifier and RC.currentlyActive = :currentlyActive and RC.status = 'ACTIVE' order by RC.priority asc limit 1")
					.setParameter("identifier", identifier).setParameter("currentlyActive", currentlyActive)
					.setCacheable(true).getSingleResult();
			tx.commit();
			return routerConfiguration;
		} catch (ObjectNotFoundException objectNotFound) {
			handleException(objectNotFound, tx);
		} catch (HibernateException hibernateException) {
			handleException(hibernateException, tx);
		} finally {
			autoClose(session);
		}
		return routerConfiguration;
	}

	public boolean findExistingRule(RouterRuleSearchObject routerRule) {
		RouterRule routerRuleDb = null;

		String paymentType = routerRule.getPaymentType();
		String mopType = routerRule.getMopType();
		String currency = routerRule.getCurrency();
		String acquirer = routerRule.getAcquirer();
		String merchant = routerRule.getMerchant();
		String transactionType = routerRule.getTransactionType();

		Session session = HibernateSessionProvider.getSession();
		Transaction tx = session.beginTransaction();
		try {
			routerRuleDb = (RouterRule) session
					.createQuery("from RouterConfiguration RC where RC.id = :id and RC.status = 'ACTIVE' ")
					.setParameter("id", paymentType).setCacheable(true).uniqueResult();
			tx.commit();
		} catch (ObjectNotFoundException objectNotFound) {
			handleException(objectNotFound, tx);
		} catch (HibernateException hibernateException) {
			handleException(hibernateException, tx);
		} finally {
			autoClose(session);
			// return responseUser;
		}

		if (routerRuleDb != null) {
			return true;
		} else {
			return false;
		}

	}

	@SuppressWarnings("unchecked")
	public List<RouterConfiguration> getActiveRules() {
		List<RouterConfiguration> rulesList = new ArrayList<RouterConfiguration>();
		rulesList = null;
		Session session = HibernateSessionProvider.getSession();
		Transaction tx = session.beginTransaction();
		try {

			rulesList = session.createQuery("from RouterConfiguration RC where RC.status='ACTIVE'").setCacheable(true)
					.getResultList();
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
	public List<RouterConfiguration> getActiveRulesByMerchant(String merchant) {
		List<RouterConfiguration> rulesList = new ArrayList<RouterConfiguration>();
		rulesList = null;
		Session session = HibernateSessionProvider.getSession();
		Transaction tx = session.beginTransaction();
		try {

			rulesList = session
					.createQuery("from RouterConfiguration RC where RC.merchant = :merchant and RC.status='ACTIVE'")
					.setParameter("merchant", merchant).setCacheable(true).getResultList();
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
	public List<RouterConfiguration> getActiveRulesByMerchant(String merchant, String cardHolder, String paymentType, String currency) {
		List<RouterConfiguration> rulesList = new ArrayList<RouterConfiguration>();

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
					"from RouterConfiguration RC where RC.merchant = :merchant and RC.cardHolderType = :cardHolderType and RC.paymentType = :paymentType and RC.currency = :currency and RC.status='ACTIVE'")
					.setParameter("merchant", merchant).setParameter("cardHolderType", cardHolderType)
					.setParameter("paymentType", paymentType).setParameter("currency", currency).setCacheable(true).getResultList();

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
	public List<RouterConfiguration> getActiveRulesByAcquirer(String merchant, String paymentType, int start,
			int length, String type) {
		List<RouterConfiguration> rulesList = new ArrayList<RouterConfiguration>();

		rulesList = null;
		Session session = HibernateSessionProvider.getSession();
		Transaction tx = session.beginTransaction();
		try {
			Query query;
			if (StringUtils.isNotBlank(type)) {
				CardHolderType cardHolderType;
				if (type.equalsIgnoreCase("CONSUMER")) {
					cardHolderType = CardHolderType.CONSUMER;
				} else {
					cardHolderType = CardHolderType.COMMERCIAL;
				}
				query = session.createQuery(
						"from RouterConfiguration RC where RC.merchant = :merchant and RC.paymentType = :paymentType and RC.cardHolderType = :cardHolderType order by id desc")
						.setParameter("merchant", merchant).setParameter("paymentType", paymentType)
						.setParameter("cardHolderType", cardHolderType).setCacheable(true);
			} else {
				query = session.createQuery(
						"from RouterConfiguration RC where RC.merchant = :merchant and RC.paymentType = :paymentType order by id desc")
						.setParameter("merchant", merchant).setParameter("paymentType", paymentType).setCacheable(true);
			}

			query.setFirstResult(start);
			query.setMaxResults(length);
			rulesList = query.getResultList();
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
	public int getActiveRulesCountByAcquirer(String merchant, String paymentType, int start, int length, String type) {
		List<RouterConfiguration> rulesList = new ArrayList<RouterConfiguration>();
		Session session = HibernateSessionProvider.getSession();
		Transaction tx = session.beginTransaction();
		try {
			Query query;
			if (StringUtils.isNotBlank(type)) {
				CardHolderType cardHolderType;
				if (type.equalsIgnoreCase("CONSUMER")) {
					cardHolderType = CardHolderType.CONSUMER;
				} else {
					cardHolderType = CardHolderType.COMMERCIAL;
				}
				query = session.createQuery(
						"from RouterConfiguration RC where RC.merchant = :merchant and RC.paymentType = :paymentType and RC.cardHolderType = :cardHolderType order by id desc")
						.setParameter("merchant", merchant).setParameter("paymentType", paymentType)
						.setParameter("cardHolderType", cardHolderType).setCacheable(true);
			} else {
				query = session.createQuery(
						"from RouterConfiguration RC where RC.merchant = :merchant and RC.paymentType = :paymentType order by id desc")
						.setParameter("merchant", merchant).setParameter("paymentType", paymentType).setCacheable(true);
			}

			rulesList = query.getResultList();
			tx.commit();

			return rulesList.size();
		} catch (ObjectNotFoundException objectNotFound) {
			handleException(objectNotFound, tx);
		} catch (HibernateException hibernateException) {
			handleException(hibernateException, tx);
		} finally {
			autoClose(session);
		}
		return rulesList.size();
	}

	@SuppressWarnings("unchecked")
	public List<Object[]> getRouterConfigurationList(String merchant, String paymentMethod, String cardHolderType) {
		List<Object[]> routerConfigurationList = new ArrayList<Object[]>();
		Session session = HibernateSessionProvider.getSession();
		Transaction tx = session.beginTransaction();
		try {
			if (cardHolderType.equalsIgnoreCase(CardHolderType.ALL.name())) {
				routerConfigurationList = session.createQuery(
						"Select merchant, acquirer, cardHolderType, loadPercentage, maxAmount, minAmount, mopType, paymentType, paymentsRegion from RouterConfiguration RC where RC.merchant = '"
								+ merchant + "' and paymentType= '" + paymentMethod
								+ "' and status='ACTIVE' and currentlyActive='1' order by paymentsRegion, cardHolderType , paymentType, mopType, acquirer, minAmount, maxAmount, loadPercentage")
						.getResultList();
			} else {
				routerConfigurationList = session.createQuery(
						"Select merchant, acquirer, cardHolderType, loadPercentage, maxAmount, minAmount, mopType, paymentType, paymentsRegion from RouterConfiguration RC where RC.merchant = '"
								+ merchant + "' and paymentType= '" + paymentMethod + "' and cardHolderType='"
								+ cardHolderType
								+ "' and status='ACTIVE' and currentlyActive='1' order by paymentsRegion, cardHolderType , paymentType, mopType, acquirer, minAmount, maxAmount, loadPercentage")
						.getResultList();
			}

			tx.commit();

			return routerConfigurationList;
		} catch (ObjectNotFoundException objectNotFound) {
			handleException(objectNotFound, tx);
		} catch (HibernateException hibernateException) {
			handleException(hibernateException, tx);
		} finally {
			autoClose(session);
		}
		return routerConfigurationList;
	}

	@SuppressWarnings("unchecked")
	public List<RouterConfiguration> getActiveRulesByRouterRule(RouterRule routerRule) {
		List<RouterConfiguration> rulesList = new ArrayList<RouterConfiguration>();

		String currency = "";
		if (Currency.getNumericCode(routerRule.getCurrency()) != null) {
			currency = Currency.getNumericCode(routerRule.getCurrency());
		} else {
			currency = routerRule.getCurrency();
		}

		String paymentType = "";

		if (PaymentType.getInstance(routerRule.getPaymentType()) != null) {
			paymentType = PaymentType.getInstance(routerRule.getPaymentType()).getCode();
		}

		else {
			paymentType = routerRule.getPaymentType();
		}

		String mopType = "";

		if (MopType.getInstance(routerRule.getMopType()) != null) {
			mopType = MopType.getInstance(routerRule.getMopType()).getCode();
		}

		else {
			mopType = routerRule.getMopType();
		}
		String merchant = routerRule.getPayId();
		String transactionType = routerRule.getTransactionType();
		AccountCurrencyRegion paymentsRegion = routerRule.getPaymentsRegion();
		CardHolderType cardHolderType = routerRule.getCardHolderType();

		Session session = HibernateSessionProvider.getSession();
		Transaction tx = session.beginTransaction();
		try {

			rulesList = session.createQuery(
					"from RouterConfiguration RC where RC.merchant = :merchant and RC.cardHolderType = :cardHolderType and RC.paymentType = :paymentType "
							+ " and RC.currency = :currency and RC.paymentsRegion = :paymentsRegion  and RC.mopType = :mopType  and RC.transactionType = :transactionType and RC.status='ACTIVE'")
					.setParameter("paymentsRegion", paymentsRegion).setParameter("mopType", mopType)
					.setParameter("transactionType", transactionType).setParameter("merchant", merchant)
					.setParameter("currency", currency).setParameter("cardHolderType", cardHolderType)
					.setParameter("paymentType", paymentType).setCacheable(true).getResultList();
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
	public List<RouterConfiguration> getPendingRulesByRouterRule(RouterRule routerRule) {
		List<RouterConfiguration> rulesList = new ArrayList<RouterConfiguration>();

		String currency = Currency.getNumericCode(routerRule.getCurrency());

		String paymentType = "";
		if (PaymentType.getInstance(routerRule.getPaymentType()) != null) {
			paymentType = PaymentType.getInstance(routerRule.getPaymentType()).getCode();
		}

		else {
			paymentType = routerRule.getPaymentType();
		}

		String mopType = "";
		if (MopType.getInstance(routerRule.getMopType()) != null) {
			mopType = MopType.getInstance(routerRule.getMopType()).getCode();
		}

		else {
			mopType = routerRule.getMopType();
		}

		String merchant = routerRule.getPayId();
		String transactionType = routerRule.getTransactionType();
		AccountCurrencyRegion paymentsRegion = routerRule.getPaymentsRegion();
		CardHolderType cardHolderType = routerRule.getCardHolderType();

		Session session = HibernateSessionProvider.getSession();
		Transaction tx = session.beginTransaction();
		try {

			rulesList = session.createQuery(
					"from RouterConfiguration RC where RC.merchant = :merchant and RC.cardHolderType = :cardHolderType and RC.paymentType = :paymentType "
							+ " and RC.currency = :currency and RC.paymentsRegion = :paymentsRegion  and RC.mopType = :mopType  and RC.transactionType = :transactionType and RC.status='PENDING'")
					.setParameter("paymentsRegion", paymentsRegion).setParameter("mopType", mopType)
					.setParameter("transactionType", transactionType).setParameter("merchant", merchant)
					.setParameter("currency", currency).setParameter("cardHolderType", cardHolderType)
					.setParameter("paymentType", paymentType).setCacheable(true).getResultList();
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

	// added by sonu for minus txn amount from merchant maxAmount.
	/*
	 * public void freezeRouterConfigurationMaxAmountById(RouterConfiguration
	 * routerConfiguration, long id) { Session session =
	 * HibernateSessionProvider.getSession(); Transaction tx =
	 * session.beginTransaction(); try { routerConfiguration.setId(id);
	 * session.update(routerConfiguration); tx.commit(); } catch
	 * (ObjectNotFoundException objectNotFound) { handleException(objectNotFound,
	 * tx); } catch (HibernateException hibernateException) {
	 * handleException(hibernateException, tx); } finally { autoClose(session); } }
	 */
 	// added by sonu for update merchant maxAmount limit
 	public void freezeRouterConfigurationMaxAmountById(double freezeAmount, long id) {
 		
 		logger.info("Request Received for release payment, id :: "+id + " freezeAmount "+freezeAmount);
 		Session session = HibernateSessionProvider.getSession();
 		Transaction tx = session.beginTransaction();
 		try {
 			
 			Query query = session.createSQLQuery("update RouterConfiguration set maxAmount = maxAmount - :freezeAmount where Id = :Id");
 			query.setParameter("freezeAmount", freezeAmount);
 			query.setParameter("Id", id);
 			int result = query.executeUpdate();
 			
 			logger.info("Result Update For Freeze Transaction Amount Is : "+result);
 			tx.commit();
 			
 		} catch (ObjectNotFoundException objectNotFound) {
 			handleException(objectNotFound, tx);
 		} catch (HibernateException hibernateException) {
 			handleException(hibernateException, tx);
 		} finally {
 			autoClose(session);
 		}
 	}
 	
 	public List<String> findLastTransaction(List<String> acquirersName,String paymentMode,String mopType, String payId) {
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
		
		if (!StringUtils.isBlank(paymentMode) && !StringUtils.isBlank(mopType) && !StringUtils.isBlank(payId)) {
			paramConditionLst.add(new BasicDBObject("PAYMENT_TYPE", paymentMode));
			paramConditionLst.add(new BasicDBObject("MOP_TYPE", mopType));
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
 	
	public boolean lastFiveTransaction(String acquirerName, String paymentMode, String mopType, int cntLimit, String payId) {

		logger.info("acquirerName : "+acquirerName+ ", paymentMode : "+paymentMode+ ", mopType : "+mopType+ ", cntLimit : "+cntLimit);
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
		
		if (!StringUtils.isBlank(paymentMode) && !StringUtils.isBlank(mopType)) {
			paramConditionLst.add(new BasicDBObject("PAYMENT_TYPE", paymentMode));
			paramConditionLst.add(new BasicDBObject("MOP_TYPE", mopType));
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
		MongoCollection<Document> collection = dbIns.getCollection("transactionStatus");

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
	
	/*
	 * public int disableRCForFailedCountExceededById(long id) {
	 * 
	 * logger.info("Request Received for disableRCForMaxAmountExceedById, id :: " +
	 * id); Session session = HibernateSessionProvider.getSession(); Transaction tx
	 * = session.beginTransaction(); int result = 0; try {
	 * 
	 * Query query = session.
	 * createSQLQuery("update RouterConfiguration set currentlyActive = :currentlyActive where Id = :Id"
	 * ); query.setParameter("currentlyActive", false); query.setParameter("Id",
	 * id); result = query.executeUpdate();
	 * 
	 * logger.info("Result Update For Freeze Transaction Amount Is : " + result);
	 * tx.commit(); return result; } catch (ObjectNotFoundException objectNotFound)
	 * { handleException(objectNotFound, tx); } catch (HibernateException
	 * hibernateException) { handleException(hibernateException, tx); } finally {
	 * autoClose(session); } return result; }
	 * 
	 * public void resetRouterConfigurationMaxAmountForAll() {
	 * 
	 * logger.
	 * info("Request Received for Reset RouterConfiguration MaxAmount For All Merchant, "
	 * + new Date()); Session session = HibernateSessionProvider.getSession();
	 * Transaction tx = session.beginTransaction(); try {
	 * 
	 * //Query query = session.
	 * createSQLQuery("update RouterConfiguration set maxAmount = :maxAmount where status = 'ACTIVE'"
	 * ); Query query = session.createSQLQuery("UPDATE RouterConfiguration a " +
	 * "JOIN RouterConfiguration b " +
	 * "ON a.merchant = b.merchant and a.mopType = b.mopType and a.paymentType = b.paymentType "
	 * +
	 * "and a.paymentsRegion = b.paymentsRegion and a.acquirer = b.acquirer and a.status = b.status "
	 * + "SET a.maxAmount = b.totalTxn , a.updatedDate = now() " +
	 * "where a.status = :status and a.maxAmount != b.totalTxn");
	 * query.setParameter("status", "ACTIVE"); int result = query.executeUpdate();
	 * 
	 * logger.
	 * info("Reset RouterConfiguration MaxAmount For All Merchant Successfully : " +
	 * result); tx.commit();
	 * 
	 * } catch (ObjectNotFoundException objectNotFound) {
	 * handleException(objectNotFound, tx); } catch (HibernateException
	 * hibernateException) { handleException(hibernateException, tx); } finally {
	 * autoClose(session); } }
	 */

	public int disableRCForFailedCountExceededById(long id) {

		logger.info("Request Received for disableRCForMaxAmountExceedById, id :: " + id);
		Session session = HibernateSessionProvider.getSession();
		Transaction tx = session.beginTransaction();
		int result = 0;
		try {

			Query query = session.createSQLQuery("update RouterConfiguration set currentlyActive = :currentlyActive , isDown = :isDown where Id = :Id");
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
	
	public int disableRCForFailedCountExceededById_again(long id) {

		logger.info("Request Received for disableRCForFailedCountExceededById_again , id :: " + id);
		Session session = HibernateSessionProvider.getSession();
		Transaction tx = session.beginTransaction();
		int result = 0;
		try {

			Query query = session.createSQLQuery("update RouterConfiguration set currentlyActive = :currentlyActive , isDown = :isDown where Id = :Id");
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

			Query query = session.createSQLQuery("update RouterConfiguration set failureCount = failureCount + :failureCount where Id = :Id");
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
	
	public void resetRouterConfigurationMaxAmountForAll() {

		logger.info("Request Received for Reset RouterConfiguration MaxAmount For All Merchant, " + new Date());
		Session session = HibernateSessionProvider.getSession();
		Transaction tx = session.beginTransaction();
		try {

			//Query query = session.createSQLQuery("update RouterConfiguration set maxAmount = :maxAmount where status = 'ACTIVE'");
			Query query = session.createSQLQuery("UPDATE RouterConfiguration a "
					+ "JOIN RouterConfiguration b "
					+ "ON a.merchant = b.merchant and a.mopType = b.mopType and a.paymentType = b.paymentType "
					+ "and a.paymentsRegion = b.paymentsRegion and a.acquirer = b.acquirer and a.status = b.status "
					+ "SET a.maxAmount = b.totalTxn , a.updatedDate = now() "
					+ "where a.status = :status and a.maxAmount != b.totalTxn");
			query.setParameter("status", "ACTIVE");
			int result = query.executeUpdate();

			logger.info("Reset RouterConfiguration MaxAmount For All Merchant Successfully : " + result);
			tx.commit();

		} catch (ObjectNotFoundException objectNotFound) {
			handleException(objectNotFound, tx);
		} catch (HibernateException hibernateException) {
			handleException(hibernateException, tx);
		} finally {
			autoClose(session);
		}
	}
	
	public void reActivateSmartRouterConfiguration() {

		logger.info("Request reActivateSmartRouterConfiguration, " + new Date());
		Session session = HibernateSessionProvider.getSession();
		Transaction tx = session.beginTransaction();
		try {

			//Query query = session.createSQLQuery("update RouterConfiguration set maxAmount = :maxAmount where status = 'ACTIVE'");
			Query query = session.createSQLQuery("UPDATE RouterConfiguration SET currentlyActive = :currentlyActive , vpaCntFlag = :vpaCntFlag , updatedDate = now() "
					+ "where vpaCntFlag = :VPACntFlag and currentlyActive = :CurrentlyActive");
			query.setParameter("currentlyActive", true);
			query.setParameter("vpaCntFlag", "N");
			query.setParameter("VPACntFlag", "Y");
			query.setParameter("CurrentlyActive", false);
			int result = query.executeUpdate();

			logger.info("Reset reActivateSmartRouterConfiguration Successfully : " + result);
			tx.commit();

		} catch (ObjectNotFoundException objectNotFound) {
			handleException(objectNotFound, tx);
		} catch (HibernateException hibernateException) {
			handleException(hibernateException, tx);
		} finally {
			autoClose(session);
		}
	}
	
	public void reActivateSRConfiguration_UpiFailedCnt() {

		logger.info("Request Received for reActivateSRConfiguration_UpiFailedCnt, " + new Date());
		Session session = HibernateSessionProvider.getSession();
		Transaction tx = session.beginTransaction();
		try {

			//Query query = session.createSQLQuery("update RouterConfiguration set maxAmount = :maxAmount where status = 'ACTIVE'");
			Query query = session.createSQLQuery("UPDATE RouterConfiguration a "
					+ "JOIN RouterConfiguration b "
					+ "ON a.merchant = b.merchant and a.mopType = b.mopType and a.paymentType = b.paymentType "
					+ "and a.paymentsRegion = b.paymentsRegion and a.acquirer = b.acquirer and a.status = b.status "
					+ "SET a.currentlyActive = :currentlyActive , a.failureCount = :FailureCount , a.updatedDate = now() "
					+ "where a.status = :status and a.isDown = :isDown and a.currentlyActive = :CurrentlyActive");
			query.setParameter("currentlyActive", true);
			query.setParameter("FailureCount", 0);
			query.setParameter("status", "ACTIVE");
			query.setParameter("isDown", true);
			query.setParameter("CurrentlyActive", false);
			int result = query.executeUpdate();

			logger.info("Reset reActivateSmartRouterConfiguration Successfully : " + result);
			tx.commit();

		} catch (ObjectNotFoundException objectNotFound) {
			handleException(objectNotFound, tx);
		} catch (HibernateException hibernateException) {
			handleException(hibernateException, tx);
		} finally {
			autoClose(session);
		}
	}

	public int disableRCForVPAFailedCountExceededById(long id) {

		logger.info("Request Received for disableRCForMaxAmountExceedById, id :: " + id);
		DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Calendar cal = Calendar.getInstance();
		String currentDate = dateFormat.format(cal.getTime());
		Session session = HibernateSessionProvider.getSession();
		Transaction tx = session.beginTransaction();
		int result = 0;
		try {

			Query query = session.createSQLQuery("update RouterConfiguration set currentlyActive = :currentlyActive, vpaCntDate = :vpaCntDate, vpaCntFlag=:vpaCntFlag where Id = :Id");
			query.setParameter("currentlyActive", false);
			query.setParameter("vpaCntDate", currentDate);
			query.setParameter("vpaCntFlag", "Y");
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
	
	public static void main(String[] args) {
		DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.MINUTE, -30);
		String currentDate = dateFormat.format(cal.getTime());
		System.out.println("currentDate "+currentDate);
	}

	public BinCardInfo getBankName(String binBankName) {
		BinCardInfo binCardInfo = new BinCardInfo();
		Session session = HibernateSessionProvider.getSession();
		Transaction tx = session.beginTransaction();
		try {
			
			binCardInfo = (BinCardInfo) session
					.createQuery("from BinCardInfo where bankName = :bankName")
					.setParameter("bankName", binBankName).setCacheable(true).uniqueResult();
			tx.commit();

			return binCardInfo;
		} catch (ObjectNotFoundException objectNotFound) {
			handleException(objectNotFound, tx);
		} catch (HibernateException hibernateException) {
			handleException(hibernateException, tx);
		} finally {
			autoClose(session);
		}
		return binCardInfo;
	}
	
	public List<RouterConfiguration> getPayIdByEmailId(String acquirerType, String paymentType) {
		List<RouterConfiguration> rulesList = new ArrayList<RouterConfiguration>();

		Session session = HibernateSessionProvider.getSession();
		Transaction tx = session.beginTransaction();
		try {
			
			// ####### Previous Functionality of Acquirer Master Switch
			String sqlQuery = "select * from RouterConfiguration where merchant in(select merchant from (select merchant,mopType,acquirer from RouterConfiguration where status='ACTIVE' and currentlyActive=1 and paymentType =:paymentType group by merchant, mopType having count(distinct acquirer)>1 order by merchant) as foo ) and  status='ACTIVE' and currentlyActive=1 and paymentType =:paymentType and acquirer=:acquirer";
			rulesList = session.createNativeQuery(sqlQuery, RouterConfiguration.class)
					 .setParameter("paymentType", paymentType).setParameter("acquirer", acquirerType)
					 .getResultList();

//			rulesList = session.createQuery(
//							"FROM RouterConfiguration WHERE acquirer=:acquirer AND paymentType=:paymentType AND status='ACTIVE'")
//					.setParameter("acquirer", acquirerType)
//					.setParameter("paymentType",paymentType)
//					.setCacheable(true).getResultList();
			
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
	
	public List<RouterConfiguration> changeStatusActiveSwitch(String acquirerType, String paymentType) {
		List<RouterConfiguration> rulesList = new ArrayList<RouterConfiguration>();

		Session session = HibernateSessionProvider.getSession();
		Transaction tx = session.beginTransaction();
		try {
			rulesList = session.createQuery(
					"from RouterConfiguration RC where RC.AcquirerSwitch =1 and RC.status = 'ACTIVE' and RC.acquirer = :acquirer and RC.paymentType = :paymentType group by merchant, mopType")
					.setParameter("acquirer", acquirerType)
					.setParameter("paymentType", paymentType).setCacheable(true).getResultList();
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

	
	public List<RouterConfiguration> getAcquirerSwitchReport(String acquirerType, String paymentType) {
		List<RouterConfiguration> rulesList = new ArrayList<RouterConfiguration>();

		Session session = HibernateSessionProvider.getSession();
		Transaction tx = session.beginTransaction();
		try {
			rulesList = session.createQuery(
					"from RouterConfiguration RC where RC.acUpdateOn != '' and  RC.acUpdateBy != '' and RC.acquirer = :acquirer and RC.paymentType = :paymentType group by acquirer, paymentType")
					.setParameter("acquirer", acquirerType)
					.setParameter("paymentType", paymentType).setCacheable(true).getResultList();
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

	public void deleteAcquirerMaster(RouterConfiguration routerConfiguration) {
		try {
			
			super.saveOrUpdate(routerConfiguration);
		} catch (HibernateException e) {
		} finally {
		}
	}
	public List<String> getpaymnetlistbyAcquirer(String acquirer) {
		List<String> rulesList = new ArrayList<String>();

		Session session = HibernateSessionProvider.getSession();
		Transaction tx = session.beginTransaction();
		try {
			rulesList = session.createQuery(
					"Select paymentType from RouterConfiguration RC where RC.acquirer = :acquirer group by paymentType")
					.setParameter("acquirer", acquirer)
					.setCacheable(true).getResultList();
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
	
	
	public List<RouterConfiguration> getSchadularlist(String acquirerType, String paymentType) {
		List<RouterConfiguration> rulesList = new ArrayList<RouterConfiguration>();

		Session session = HibernateSessionProvider.getSession();
		Transaction tx = session.beginTransaction();
		try {
			rulesList = session.createQuery(
					"from RouterConfiguration RC where RC.currentlyActive =1 and RC.status = 'ACTIVE' and RC.acquirer = :acquirer and RC.paymentType = :paymentType group by merchant, mopType")
					.setParameter("acquirer", acquirerType)
					.setParameter("paymentType", paymentType).setCacheable(true).getResultList();
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
	
	

	public List<RouterConfiguration> getSchadularListForInactive(String acquirerType, String paymentType) {
		List<RouterConfiguration> rulesList = new ArrayList<RouterConfiguration>();

		Session session = HibernateSessionProvider.getSession();
		Transaction tx = session.beginTransaction();
		try {
			rulesList = session.createQuery(
					"from RouterConfiguration RC where RC.currentlyActive =0 and RC.status = 'ACTIVE' and  RC.AcquirerSwitch = 1 and RC.acquirer = :acquirer and RC.paymentType = :paymentType group by merchant, mopType")
					.setParameter("acquirer", acquirerType)
					.setParameter("paymentType", paymentType).setCacheable(true).getResultList();
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
	public List<RouterConfiguration> findRulesByCurrencyAndPayId(String payId, String currency) {
		List<RouterConfiguration> rulesList = new ArrayList<RouterConfiguration>();
		rulesList = null;
		Session session = HibernateSessionProvider.getSession();
		Transaction tx = session.beginTransaction();
		try {

			rulesList = session
					.createQuery(
							"from RouterConfiguration RC where RC.merchant = :payId and RC.currency = :currency and RC.status='ACTIVE'")
					.setParameter("payId", payId)
					.setParameter("currency", currency)
					.setCacheable(true).getResultList();
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


	//Getting PaymentType for Global Acquirer Switch
	public List<String> getPaymentTypeListByAcquirer(String acquirer) {
		List<String> rulesList = new ArrayList<String>();
		rulesList.add("ALL");

		Session session = HibernateSessionProvider.getSession();
		Transaction tx = session.beginTransaction();
		try {
			rulesList.addAll(session.createQuery(
							"Select paymentType from RouterConfiguration RC where RC.acquirer = :acquirer group by paymentType")
					.setParameter("acquirer", acquirer)
					.setCacheable(true).getResultList());
			tx.commit();
			logger.info("Payment Type List :::"+rulesList);
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

	//Getting Mapped List by Acquirer & PaymentType for Global Acquirer Switch for Display
	public List<RouterConfiguration> getMappedByAcquirer(String acquirer, String paymentType){

		List<RouterConfiguration> mappedData = new ArrayList<>();
		Session session = HibernateSessionProvider.getSession();
		Transaction tx = session.beginTransaction();

		try{
			if(paymentType.equalsIgnoreCase("ALL")){
				mappedData = session.createQuery(
								"FROM RouterConfiguration WHERE acquirer=:acquirer AND status='ACTIVE' AND currentlyActive=1 group by merchant")
						.setParameter("acquirer", acquirer)
						.setCacheable(true).getResultList();
			}else {
				mappedData = session.createQuery(
								"FROM RouterConfiguration WHERE acquirer=:acquirer AND paymentType=:paymentType AND status='ACTIVE' AND currentlyActive=1 group by merchant")
						.setParameter("acquirer", acquirer)
						.setParameter("paymentType", paymentType)
						.setCacheable(true).getResultList();
			}

			tx.commit();
			return mappedData;

		} catch (ObjectNotFoundException objectNotFound) {
			handleException(objectNotFound, tx);
		} catch (HibernateException hibernateException) {
			handleException(hibernateException, tx);
		} finally {
			autoClose(session);
		}
		return mappedData;
	}

	//Getting Mapped List by Acquirer & PaymentType for Global Acquirer Switch to be INACTIVE
	public List<RouterConfiguration> getMappedByAcquirerForDelete(String acquirer, String paymentType){

		List<RouterConfiguration> mappedData = new ArrayList<>();
		Session session = HibernateSessionProvider.getSession();
		Transaction tx = session.beginTransaction();

		try{
			if(paymentType.equalsIgnoreCase("ALL")){
				mappedData = session.createQuery(
								"FROM RouterConfiguration WHERE acquirer=:acquirer AND status='ACTIVE' AND currentlyActive=1")
						.setParameter("acquirer", acquirer)
						.setCacheable(true).getResultList();
			}else {
				mappedData = session.createQuery(
								"FROM RouterConfiguration WHERE acquirer=:acquirer AND paymentType=:paymentType AND status='ACTIVE' AND currentlyActive=1")
						.setParameter("acquirer", acquirer)
						.setParameter("paymentType", paymentType)
						.setCacheable(true).getResultList();
			}

			tx.commit();
			return mappedData;

		} catch (ObjectNotFoundException objectNotFound) {
			handleException(objectNotFound, tx);
		} catch (HibernateException hibernateException) {
			handleException(hibernateException, tx);
		} finally {
			autoClose(session);
		}
		return mappedData;
	}


	//Getting Mapped List by Acquirer & PaymentType for Global Acquirer Switch to be ACTIVE
	public List<RouterConfiguration> changeGlobalStatusActive(String acquirerType, String paymentType) {
		List<RouterConfiguration> rulesList = new ArrayList<RouterConfiguration>();

		Session session = HibernateSessionProvider.getSession();
		Transaction tx = session.beginTransaction();
		try {
			if (paymentType.equalsIgnoreCase("ALL")) {
				rulesList = session.createQuery(
								"from RouterConfiguration RC where RC.AcquirerSwitch =1 and RC.status = 'ACTIVE' and RC.acquirer = :acquirer")
						.setParameter("acquirer", acquirerType)
						.setCacheable(true).getResultList();
				tx.commit();
			}else {
				rulesList = session.createQuery(
								"from RouterConfiguration RC where RC.AcquirerSwitch =1 and RC.status = 'ACTIVE' and RC.acquirer = :acquirer and RC.paymentType = :paymentType")
						.setParameter("acquirer", acquirerType)
						.setParameter("paymentType", paymentType).setCacheable(true).getResultList();
				tx.commit();

			}
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
