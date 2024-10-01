package com.pay10.commons.user;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.Query;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.HibernateException;
import org.hibernate.ObjectNotFoundException;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.pay10.commons.dao.HibernateAbstractDao;
import com.pay10.commons.dao.HibernateSessionProvider;
import com.pay10.commons.exception.DataAccessLayerException;
import com.pay10.commons.user.AccountCurrencyRegion;
import com.pay10.commons.user.CardHolderType;
import com.pay10.commons.user.RouterConfigurationPayout;
import com.pay10.commons.user.RouterRulePayout;
import com.pay10.commons.util.Currency;
import com.pay10.commons.util.TDRStatus;

/**
 * @author Rahul
 *
 */
@Service
public class RouterRuleDaoPayout extends HibernateAbstractDao {

	private static Logger logger = LoggerFactory.getLogger(RouterRuleDaoPayout.class.getName());

	public void create(RouterRulePayout routerRule) throws DataAccessLayerException {
		super.save(routerRule);
	}

	public void delete(Long id) {
		try {
			Date date = new Date();
			RouterRulePayout rule = findRule(id);
			if (null != rule) {
				rule.setStatus(TDRStatus.INACTIVE);
				rule.setUpdatedDate(date);
			}
			super.saveOrUpdate(rule);
		} catch (HibernateException e) {
			// handleException(e);
		} finally {
			// autoClose(session);
		}
	}

	public void delete(RouterRulePayout rule, String approvedBy) {
		try {
			Date date = new Date();
			if (null != rule) {
				rule.setStatus(TDRStatus.INACTIVE);
				rule.setUpdatedDate(date);
				rule.setApprovedBy(approvedBy);
			}
			super.saveOrUpdate(rule);
		} catch (HibernateException e) {
			// handleException(e);
		} finally {
			// autoClose(session);
		}
	}

	@SuppressWarnings("unchecked")
	public List<RouterConfigurationPayout> fetchRouterConfigByPayId(String id) {
		RouterConfigurationPayout routerConfiguration = null;
		List<RouterConfigurationPayout> routerConfiguration1 = new ArrayList<RouterConfigurationPayout>();

		Session session = HibernateSessionProvider.getSession();
		Transaction tx = session.beginTransaction();
		try {
			routerConfiguration1 = (List<RouterConfigurationPayout>) session
					.createQuery("from RouterConfigurationPayout RR where RR.merchant = :id and RR.status = 'ACTIVE' ")
					.setParameter("id", id).setCacheable(true).getResultList();
			tx.commit();
		} catch (ObjectNotFoundException objectNotFound) {
			handleException(objectNotFound, tx);
		} catch (HibernateException hibernateException) {
			handleException(hibernateException, tx);
		} finally {
			autoClose(session);
		}
		return routerConfiguration1;

	}

	public RouterRulePayout findRule(Long id) {
		RouterRulePayout routerRule = null;
		Session session = HibernateSessionProvider.getSession();
		Transaction tx = session.beginTransaction();
		try {
			routerRule = (RouterRulePayout) session
					.createQuery("from RouterRulePayout RR where RR.id = :id and RR.status = 'ACTIVE' ")
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
		return routerRule;

	}

	public RouterRulePayout findPendingRule(Long id) {
		RouterRulePayout routerRule = null;
		Session session = HibernateSessionProvider.getSession();
		Transaction tx = session.beginTransaction();
		try {
			routerRule = (RouterRulePayout) session
					.createQuery("from RouterRulePayout RR where RR.id = :id and RR.status = 'PENDING' ")
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
		return routerRule;

	}

	@SuppressWarnings("unchecked")
	public List<RouterRulePayout> findAllRuleByMerchant(String payId) {
		List<RouterRulePayout> routerRuleList = new ArrayList<RouterRulePayout>();
		Session session = HibernateSessionProvider.getSession();
		Transaction tx = session.beginTransaction();
		try {

			routerRuleList = session
					.createQuery("from RouterRulePayout RR where RR.merchant = :payId and RR.status = 'ACTIVE' ")
					.setCacheable(true).setParameter("payId", payId).setCacheable(true).getResultList();
			tx.commit();

		} catch (ObjectNotFoundException objectNotFound) {
			handleException(objectNotFound, tx);
		} catch (HibernateException hibernateException) {
			handleException(hibernateException, tx);
		} finally {
			autoClose(session);
			// return responseUser;
		}
		return routerRuleList;

	}

	public void deleterouterConfiguration(RouterConfigurationPayout rule) {
		try {
			Date date = new Date();
			if (null != rule) {
				rule.setStatus(TDRStatus.INACTIVE);
				rule.setUpdatedDate(date);
			}

			super.saveOrUpdate(rule);
		} catch (HibernateException e) {
			// handleException(e);
		} finally {
			// autoClose(session);
		}
	}

	public List<RouterRulePayout> fetchRouterRulePayoutByPayId(String id) {
		RouterRulePayout routerRule = null;
		List<RouterRulePayout> routerRule1 = new ArrayList<RouterRulePayout>();

		Session session = HibernateSessionProvider.getSession();
		Transaction tx = session.beginTransaction();
		try {
			routerRule1 = (List<RouterRulePayout>) session
					.createQuery("from RouterRulePayout RR where RR.merchant = :id and RR.status = 'ACTIVE' ")
					.setParameter("id", id).setCacheable(true).getResultList();
			tx.commit();
		} catch (ObjectNotFoundException objectNotFound) {
			handleException(objectNotFound, tx);
		} catch (HibernateException hibernateException) {
			handleException(hibernateException, tx);
		} finally {
			autoClose(session);
			// return responseUser;
		}
		return routerRule1;

	}

	@SuppressWarnings("unchecked")
	public List<RouterRulePayout> getActiveRules(String payId) {
		List<RouterRulePayout> rulesList = new ArrayList<RouterRulePayout>();
		rulesList = null;
		Session session = HibernateSessionProvider.getSession();
		Transaction tx = session.beginTransaction();
		try {

			rulesList = session
					.createQuery("from RouterRulePayout RR where RR.merchant = :payId and RR.status='ACTIVE'")
					.setCacheable(true).setParameter("payId", payId).setCacheable(true).getResultList();
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
	public List<RouterRulePayout> getPendingRules() {
		List<RouterRulePayout> rulesList = new ArrayList<RouterRulePayout>();
		rulesList = null;
		Session session = HibernateSessionProvider.getSession();
		Transaction tx = session.beginTransaction();
		try {

			rulesList = session.createQuery("from RouterRulePayout RR where RR.status='PENDING'").setCacheable(true)
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

	public RouterRulePayout findRuleByFields(String acquirer, String channel, String currency, String transactionType) {
		RouterRulePayout routerRule = null;
		Session session = HibernateSessionProvider.getSession();
		Transaction tx = session.beginTransaction();
		try {
			routerRule = (RouterRulePayout) session.createQuery(
					"from RouterRulePayout RR where RR.acquirer = :acquirer and  RR.channel = :channel and RR.currency = :currency  and RR.transactionType = :transactionType and RR.status = 'ACTIVE' ")
					.setParameter("acquirer", acquirer).setParameter("channel", channel)
					.setParameter("currency", currency).setParameter("transactionType", transactionType)
					.setCacheable(true).uniqueResult();
			tx.commit();
		} catch (ObjectNotFoundException objectNotFound) {
			handleException(objectNotFound, tx);
		} catch (HibernateException hibernateException) {
			handleException(hibernateException, tx);
		} finally {
			autoClose(session);
			// return responseUser;
		}
		return routerRule;

	}

	public RouterRulePayout findRuleByFieldsByPayId(String payId, String acquirer, String channel, String currency,
			String transactionType, String paymentsRegion, String cardHolderType) {

		Session session = HibernateSessionProvider.getSession();
		Transaction tx = session.beginTransaction();
		AccountCurrencyRegion acr;

		if (paymentsRegion.equalsIgnoreCase(AccountCurrencyRegion.DOMESTIC.toString())) {
			acr = AccountCurrencyRegion.DOMESTIC;
		} else {
			acr = AccountCurrencyRegion.INTERNATIONAL;
		}

		CardHolderType act;

		if (cardHolderType.equalsIgnoreCase(CardHolderType.COMMERCIAL.toString())) {
			act = CardHolderType.COMMERCIAL;
		} else {
			act = CardHolderType.CONSUMER;
		}

		RouterRulePayout routerRule = null;
		try {
			routerRule = (RouterRulePayout) session.createQuery(
					"from RouterRulePayout RR where RR.merchant = :payId and RR.acquirer = :acquirer and  RR.channel = :channel and RR.currency = :currency  and RR.transactionType = :transactionType"
							+ " and RR.paymentsRegion = :acr and RR.cardHolderType = :act and RR.status = 'ACTIVE' ")
					.setParameter("payId", payId).setParameter("acquirer", acquirer).setParameter("channel", channel)
					.setParameter("currency", currency).setParameter("transactionType", transactionType)
					.setParameter("acr", acr).setParameter("act", act).setCacheable(true).uniqueResult();
			tx.commit();
		} catch (ObjectNotFoundException objectNotFound) {
			handleException(objectNotFound, tx);
		} catch (HibernateException hibernateException) {
			handleException(hibernateException, tx);
		} finally {
			autoClose(session);
			// return responseUser;
		}
		return routerRule;

	}

	public RouterRulePayout findPendingRuleByFieldsByPayId(String payId, String acquirer, String channel,
			String currency, String transactionType, String paymentsRegion, String cardHolderType) {

		Session session = HibernateSessionProvider.getSession();
		Transaction tx = session.beginTransaction();
		AccountCurrencyRegion acr;

		if (paymentsRegion.equalsIgnoreCase(AccountCurrencyRegion.DOMESTIC.toString())) {
			acr = AccountCurrencyRegion.DOMESTIC;
		} else {
			acr = AccountCurrencyRegion.INTERNATIONAL;
		}

		CardHolderType act;

		if (cardHolderType.equalsIgnoreCase(CardHolderType.COMMERCIAL.toString())) {
			act = CardHolderType.COMMERCIAL;
		} else {
			act = CardHolderType.CONSUMER;
		}

		RouterRulePayout routerRule = null;
		try {
			routerRule = (RouterRulePayout) session.createQuery(
					"from RouterRulePayout RR where RR.merchant = :payId and RR.acquirer = :acquirer and  RR.channel = :channel and RR.currency = :currency  and RR.transactionType = :transactionType"
							+ " and RR.paymentsRegion = :acr and RR.cardHolderType = :act and RR.status = 'PENDING' ")
					.setParameter("payId", payId).setParameter("acquirer", acquirer).setParameter("channel", channel)
					.setParameter("currency", currency).setParameter("transactionType", transactionType)
					.setParameter("acr", acr).setParameter("act", act).setCacheable(true).uniqueResult();
			tx.commit();
		} catch (ObjectNotFoundException objectNotFound) {
			handleException(objectNotFound, tx);
		} catch (HibernateException hibernateException) {
			handleException(hibernateException, tx);
		} finally {
			autoClose(session);
			// return responseUser;
		}
		return routerRule;

	}

	public RouterRulePayout findRuleByFieldsByPayId(String payId, String acquirer, String channel, String currency,
			String transactionType) {

		Session session = HibernateSessionProvider.getSession();
		Transaction tx = session.beginTransaction();
		RouterRulePayout routerRule = null;
		try {
			routerRule = (RouterRulePayout) session.createQuery(
					"from RouterRulePayout RR where RR.merchant = :payId and RR.acquirer = :acquirer and  RR.channel = :channel and RR.currency = :currency  and RR.transactionType = :transactionType"
							+ " and RR.status = 'ACTIVE' ")
					.setParameter("payId", payId).setParameter("acquirer", acquirer).setParameter("channel", channel)
					.setParameter("currency", currency).setParameter("transactionType", transactionType)
					.setCacheable(true).uniqueResult();
			tx.commit();
		} catch (ObjectNotFoundException objectNotFound) {
			handleException(objectNotFound, tx);
		} catch (HibernateException hibernateException) {
			handleException(hibernateException, tx);
		} finally {
			autoClose(session);
			// return responseUser;
		}
		return routerRule;

	}

//	public RouterRulePayout getMatchingRule(RouterRulePayout routerRule) {
//
//		String currency = "";
//		String channel = "";
//		if (Currency.getNumericCode(routerRule.getCurrency()) != null ) {
//			currency = Currency.getNumericCode(routerRule.getCurrency());
//		}
//		else{
//			currency = routerRule.getCurrency();
//		}
//		
//		RouterRulePayout routerRule1 = null;
//		Session session = HibernateSessionProvider.getSession();
//		Transaction tx = session.beginTransaction();
//		AccountCurrencyRegion paymentsRegion =  routerRule.getPaymentsRegion();
//		CardHolderType cardHolderType = routerRule.getCardHolderType();
//		
//		try {
//			routerRule1 = (RouterRulePayout) session.createQuery(
//					"from RouterRulePayout RR where RR.acquirer = :acquirer and  RR.channel = :channel and RR.currency = :currency and RR.transactionType = :transactionType and RR.merchant = :merchant and RR.paymentsRegion = :paymentsRegion and RR.cardHolderType = :cardHolderType and RR.status='ACTIVE'  ")
//					.setParameter("currency", currency).setParameter("channel", channel)
//					.setParameter("transactionType", routerRule.getTransactionType())
//					.setParameter("cardHolderType", cardHolderType)
//					.setParameter("paymentsRegion", paymentsRegion)
//					.setParameter("merchant", routerRule.getMerchant()).setCacheable(true).uniqueResult();
//			tx.commit();
//		} catch (ObjectNotFoundException objectNotFound) {
//			handleException(objectNotFound,tx);
//		} catch (HibernateException hibernateException) {
//			handleException(hibernateException,tx);
//		} catch (Exception ex) {
//			System.out.println(ex.getMessage());
//		} finally {
//			autoClose(session);
//			// return responseUser;
//		}
//		return routerRule1;
//
//	}

	public RouterRulePayout getMatchingRule(RouterRulePayout routerRule) {
		logger.info("##----------------------- getMatchingRule ---------------------------##");
//		String currency = "";
//		//String channel = "";
//		if (Currency.getNumericCode(routerRule.getCurrency()) != null ) {
//			currency = Currency.getNumericCode(routerRule.getCurrency());
//		}
//		else{
//			currency = routerRule.getCurrency();
//		}
		RouterRulePayout routerRule1 = null;
		Session session = HibernateSessionProvider.getSession();
		Transaction tx = session.beginTransaction();
		AccountCurrencyRegion paymentsRegion =  routerRule.getPaymentsRegion();
		CardHolderType cardHolderType = routerRule.getCardHolderType();

		logger.info("acquirerMap={}, currency={}, channel={}, transactionType={}, cardHolderType={}, paymentsRegion={}, merchant={}",routerRule.getAcquirerMap(), routerRule.getCurrency(), routerRule.getChannel(), routerRule.getTransactionType(), cardHolderType, paymentsRegion, routerRule.getMerchant());
		try {
			routerRule1 = (RouterRulePayout) session.createQuery("from RouterRulePayout RR where RR.acquirerMap = :acquirerMap and RR.channel = :channel and RR.currency = :currency and RR.transactionType = :transactionType and RR.merchant = :merchant and RR.paymentsRegion = :paymentsRegion and RR.cardHolderType = :cardHolderType and RR.status='ACTIVE'")
					.setParameter("acquirerMap", routerRule.getAcquirerMap())
					.setParameter("currency", routerRule.getCurrency())
					.setParameter("channel", routerRule.getChannel())
					.setParameter("transactionType", routerRule.getTransactionType())
					.setParameter("cardHolderType", cardHolderType)
					.setParameter("paymentsRegion", paymentsRegion)
					.setParameter("merchant", routerRule.getMerchant()).uniqueResult();
			tx.commit();
		} catch (ObjectNotFoundException objectNotFound) {
			logger.info("Exception, objectNotFound="+objectNotFound);
			handleException(objectNotFound,tx);
		} catch (HibernateException hibernateException) {
			logger.info("Exception, hibernateException="+hibernateException);
			handleException(hibernateException,tx);
		} catch (Exception ex) {
			logger.info("Exception, ex="+ex);
		} finally {
			autoClose(session);
			// return responseUser;
		}
		return routerRule1;
 
	}

	public List<String> getAcquirerListEdit(String payId) {

		Session session = HibernateSessionProvider.getSession();
		Transaction tx = session.beginTransaction();
		List<String> list = new ArrayList<String>();
		try {
			org.hibernate.query.Query query = session.createSQLQuery(
					"SELECT distinct acquirerMap FROM RouterRulePayout where merchant=:payId and status='ACTIVE'");
			query.setString("payId", payId);
			list = query.list();
			logger.info("groupList " + list);

			tx.commit();
			logger.info("Acquirer List Edit Data in dao" + list);
			return list;

		} catch (ObjectNotFoundException objectNotFound) {
			handleException(objectNotFound, tx);
		} catch (HibernateException hibernateException) {
			handleException(hibernateException, tx);
		} finally {
			autoClose(session);
		}
		return list;
	}

	public RouterRulePayout getPendingMatchingRule(RouterRulePayout routerRule) {

		String currency = "";

		if (Currency.getNumericCode(routerRule.getCurrency()) != null) {
			currency = Currency.getNumericCode(routerRule.getCurrency());
		} else {
			currency = routerRule.getCurrency();
		}
		RouterRulePayout routerRule1 = null;
		Session session = HibernateSessionProvider.getSession();
		Transaction tx = session.beginTransaction();
		AccountCurrencyRegion paymentsRegion = routerRule.getPaymentsRegion();
		CardHolderType cardHolderType = routerRule.getCardHolderType();

		try {
			routerRule1 = (RouterRulePayout) session.createQuery(
					"from RouterRulePayout RR where RR.channel = :channel and RR.currency = :currency and RR.transactionType = :transactionType and RR.merchant = :merchant and RR.paymentsRegion = :paymentsRegion and RR.cardHolderType = :cardHolderType and RR.status='PENDING'  ")
					.setParameter("channel", routerRule.getChannel()).setParameter("currency", currency)
					.setParameter("transactionType", routerRule.getTransactionType())
					.setParameter("cardHolderType", cardHolderType).setParameter("paymentsRegion", paymentsRegion)
					.setParameter("merchant", routerRule.getMerchant()).setCacheable(true).uniqueResult();
			tx.commit();
		} catch (ObjectNotFoundException objectNotFound) {
			handleException(objectNotFound, tx);
		} catch (HibernateException hibernateException) {
			handleException(hibernateException, tx);
		} catch (Exception ex) {
			System.out.println(ex.getMessage());
		} finally {
			autoClose(session);
			// return responseUser;
		}
		return routerRule1;

	}

	public RouterRulePayout getMatchingRuleByAcquirer(RouterRulePayout routerRule) {

		String currency = "";

		if (Currency.getNumericCode(routerRule.getCurrency()) != null) {
			currency = Currency.getNumericCode(routerRule.getCurrency());
		} else {
			currency = routerRule.getCurrency();
		}
		RouterRulePayout routerRule1 = null;
		Session session = HibernateSessionProvider.getSession();
		Transaction tx = session.beginTransaction();
		try {

			routerRule1 = (RouterRulePayout) session.createQuery(
					"from RouterRulePayout RR where RR.channel = :channel and RR.currency = :currency and RR.acquirerMap = :acquirerMap and RR.transactionType = :transactionType and RR.merchant = :merchant and RR.status='ACTIVE'  ")

					.setParameter("channel", routerRule.getChannel()).setParameter("currency", currency)
					.setParameter("transactionType", routerRule.getTransactionType())
					.setParameter("acquirerMap", routerRule.getAcquirerMap())
					.setParameter("merchant", routerRule.getMerchant()).setCacheable(true).uniqueResult();
			tx.commit();
		} catch (ObjectNotFoundException objectNotFound) {
			handleException(objectNotFound, tx);
		} catch (HibernateException hibernateException) {
			handleException(hibernateException, tx);
		} catch (Exception ex) {
			System.out.println(ex.getMessage());
		} finally {
			autoClose(session);
			// return responseUser;
		}
		return routerRule1;

	}

	public RouterRulePayout getMatchingRuleByAcquirerForEdit(RouterRulePayout routerRule) {

		String currency = "";
		if (Currency.getNumericCode(routerRule.getCurrency()) != null) {
			currency = Currency.getNumericCode(routerRule.getCurrency());
		} else {
			currency = routerRule.getCurrency();
		}

		/*
		 * if (MopType.getInstance(routerRule.getMopType()) != null) { mopType =
		 * MopType.getInstance(routerRule.getMopType()).getCode(); } else { mopType =
		 * routerRule.getMopType(); }
		 */
		Session session = HibernateSessionProvider.getSession();
		Transaction tx = session.beginTransaction();
		RouterRulePayout routerRule1 = null;
		try {

			routerRule1 = (RouterRulePayout) session.createQuery(
					"from RouterRulePayout RR where RR.channel = :channel and RR.currency = :currency and RR.acquirerMap = :acquirerMap and RR.transactionType = :transactionType and RR.merchant = :merchant and RR.status='ACTIVE'  ")
					.setParameter("channel", routerRule.getChannel()).setParameter("currency", currency)
					.setParameter("acquirerMap", routerRule.getAcquirerMap())
					.setParameter("transactionType", routerRule.getTransactionType())
					.setParameter("merchant", routerRule.getMerchant()).setCacheable(true).uniqueResult();
			tx.commit();
		} catch (ObjectNotFoundException objectNotFound) {
			handleException(objectNotFound, tx);
		} catch (HibernateException hibernateException) {
			handleException(hibernateException, tx);
		} catch (Exception ex) {
			System.out.println(ex.getMessage());
		} finally {
			autoClose(session);
			// return responseUser;
		}
		return routerRule1;

	}

	@SuppressWarnings("unchecked")
	public List<RouterRulePayout> getActiveRulesByAcquirer(String merchant, int start, int length, String type) {
		List<RouterRulePayout> rulesList = new ArrayList<RouterRulePayout>();

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
						"from RouterRulePayout RC where RC.merchant = :merchant and  RC.cardHolderType = :cardHolderType order by id desc")
						.setParameter("merchant", merchant).setParameter("cardHolderType", cardHolderType)
						.setCacheable(true);
			} else {
				query = session.createQuery("from RouterRulePayout RC where RC.merchant = :merchant order by id desc")
						.setParameter("merchant", merchant);
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
	public int getActiveRulesCountByAcquirer(String merchant, int start, int length, String type) {
		List<RouterRulePayout> rulesList = new ArrayList<RouterRulePayout>();
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
						"from RouterRulePayout RC where RC.merchant = :merchant and RC.cardHolderType = :cardHolderType order by id desc")
						.setParameter("merchant", merchant).setParameter("cardHolderType", cardHolderType)
						.setCacheable(true);
			} else {
				query = session.createQuery("from RouterRulePayout RC where RC.merchant = :merchant order by id desc")
						.setParameter("merchant", merchant);
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

	public void updateRulelist(RouterRulePayout routerRule) {
		try {
			Session session = HibernateSessionProvider.getSession();
			Transaction tx = session.beginTransaction();
			session.createQuery(
					"update RouterRulePayout RR set RR.acquirerMap=:acquirerMap where RR.id=:id and RR.merchant=:payId")
					.setParameter("acquirerMap", routerRule.getAcquirerMap()).setParameter("id", routerRule.getId())
					.setParameter("payId", routerRule.getMerchant()).executeUpdate();
			tx.commit();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public List<String> getSmartRouterAcquirerPayout(RouterRulePayout routerRulePayout) {
		List<String> acquirerList = new ArrayList<>();
		try {
			Session session = HibernateSessionProvider.getSession();
			Transaction txn = session.beginTransaction();
			acquirerList = session.createNativeQuery(
					"SELECT DISTINCT acquirerName FROM TdrSettingPayout WHERE payId=:payId AND currency=:currency AND channel=:channel AND status='ACTIVE'")
					.setParameter("payId", routerRulePayout.getPayId())
					.setParameter("currency", routerRulePayout.getCurrency())
					.setParameter("channel", routerRulePayout.getChannel()).getResultList();

			txn.commit();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return acquirerList;
	}
}
