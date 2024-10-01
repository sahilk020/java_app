package com.pay10.commons.user;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.HibernateException;
import org.hibernate.ObjectNotFoundException;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.pay10.commons.dao.HibernateAbstractDao;
import com.pay10.commons.dao.HibernateSessionProvider;
import com.pay10.commons.exception.DataAccessLayerException;


@Component
public class AccountCurrencyDao extends HibernateAbstractDao {
	private static Logger logger = LoggerFactory.getLogger(AccountCurrencyDao.class.getName());

	public AccountCurrencyDao() {
		super();
	}

	public void create(AccountCurrency accountCurrency) throws DataAccessLayerException {
		super.save(accountCurrency);
	}

	public void delete(AccountCurrency accountCurrency) throws DataAccessLayerException {
		super.delete(accountCurrency);
	}

	public void update(AccountCurrency accountCurrency) throws DataAccessLayerException {
		super.saveOrUpdate(accountCurrency);
	}

	@SuppressWarnings("rawtypes")
	public List findAll() throws DataAccessLayerException {
		return super.findAll(AccountCurrency.class);
	}

	public AccountCurrency find(Long id) throws DataAccessLayerException {
		return (AccountCurrency) super.find(AccountCurrency.class, id);
	}

	public AccountCurrency find(String name) throws DataAccessLayerException {
		return (AccountCurrency) super.find(AccountCurrency.class, name);
	}

	public Map<String, String> findCustom(String merchantId, String adf1, String adf2) throws DataAccessLayerException {

		Session session = HibernateSessionProvider.getSession();
		Transaction tx = session.beginTransaction();

		Map<String, String> accountMap = new HashMap<String, String>();
		try {
			List<Object[]> rawList = session
					.createQuery("Select txnKey, password from AccountCurrency AC where ((AC.merchantId = '"
							+ merchantId + "') and (AC.adf1= '" + adf1 + "') ) and AC.adf2='" + adf2 + "'")
					.getResultList();

			for (Object[] objects : rawList) {
				accountMap.put("txnKey", (String) objects[0]);
				accountMap.put("password", (String) objects[1]);
				break;
			}
			tx.commit();

			return accountMap;
		} catch (ObjectNotFoundException objectNotFound) {
			handleException(objectNotFound, tx);
		} catch (HibernateException hibernateException) {
			handleException(hibernateException, tx);
		} finally {
			autoClose(session);
		}
		return accountMap;
	}

	public String findByMid(String merchantId) throws DataAccessLayerException {

		Session session = HibernateSessionProvider.getSession();
		Transaction tx = session.beginTransaction();

		String txnKey = null;
		try {
			List<Object[]> rawList = session.createQuery(
					"Select txnKey, password from AccountCurrency AC where AC.merchantId = '" + merchantId + "'")
					.getResultList();

			for (Object[] objects : rawList) {
				txnKey = (String) objects[0];
				break;
			}
			tx.commit();

			return txnKey;
		} catch (ObjectNotFoundException objectNotFound) {
			handleException(objectNotFound, tx);
		} catch (HibernateException hibernateException) {
			handleException(hibernateException, tx);
		} finally {
			autoClose(session);
		}
		return txnKey;
	}

	public Map<String, String> findCustom(String merchantId) throws DataAccessLayerException {

		Session session = HibernateSessionProvider.getSession();
		Transaction tx = session.beginTransaction();

		Map<String, String> accountMap = new HashMap<String, String>();
		try {
			List<Object[]> rawList = session
					.createQuery(
							"Select adf5, adf8 from AccountCurrency AC where AC.merchantId = '" + merchantId + "'")
					.getResultList();

			for (Object[] objects : rawList) {
				accountMap.put("respIv", (String) objects[0]);
				accountMap.put("respTxnKey", (String) objects[1]);
				break;
			}
			tx.commit();

			return accountMap;
		} catch (ObjectNotFoundException objectNotFound) {
			handleException(objectNotFound, tx);
		} catch (HibernateException hibernateException) {
			handleException(hibernateException, tx);
		} finally {
			autoClose(session);
		}
		return accountMap;
	}
	
	@SuppressWarnings("deprecation")
	public String getHdfcKeyByMid(String AcqId) {
		logger.info("Mid for  hdfc in dao class" + AcqId);

String Key = null;
		Session session = HibernateSessionProvider.getSession();
		Transaction tx = session.beginTransaction();
		List<String> list = new ArrayList<String>();
		try {
			Query query = session
					.createSQLQuery("SELECT distinct adf1 FROM AccountCurrency where  merchantId=:merchantId");
			query.setString("merchantId", AcqId);
			list = query.list();

			tx.commit();
			for (String  data : list) 
			{ 
				if(StringUtils.isNotEmpty(data) ) {
				Key=data;
				break;
			}
						}
			logger.info("key for  hdfc in dao class" + list);
			return Key;

		} catch (ObjectNotFoundException objectNotFound) {
			handleException(objectNotFound, tx);
		} catch (HibernateException hibernateException) {
			handleException(hibernateException, tx);
		} finally {
			autoClose(session);
		}
		return Key;
	}

	public Map<String, String> FindinAndKeyCityUnion(String merchantId) throws DataAccessLayerException {

		Session session = HibernateSessionProvider.getSession();
		Transaction tx = session.beginTransaction();

		Map<String, String> accountMap = new HashMap<String, String>();
		try {
			List<Object[]> rawList = session
					.createQuery(
							"Select adf1, txnKey from AccountCurrency AC where AC.merchantId = '" + merchantId + "'")
					.getResultList();

			for (Object[] objects : rawList) {
				accountMap.put("respIv", (String) objects[0]);
				accountMap.put("respTxnKey", (String) objects[1]);
				break;
			}
			tx.commit();

			return accountMap;
		} catch (ObjectNotFoundException objectNotFound) {
			handleException(objectNotFound, tx);
		} catch (HibernateException hibernateException) {
			handleException(hibernateException, tx);
		} finally {
			autoClose(session);
		}
		return accountMap;
	}

	public Map<String, String> findCustomFromMid(String merchantId) throws DataAccessLayerException {

		Session session = HibernateSessionProvider.getSession();
		Transaction tx = session.beginTransaction();

		Map<String, String> accountMap = new HashMap<String, String>();
		try {
			List<Object[]> rawList = session
					.createQuery(
							"Select txnKey, adf1 from AccountCurrency AC where AC.merchantId = '" + merchantId + "'")
					.getResultList();

			for (Object[] objects : rawList) {
				accountMap.put("txnKey", (String) objects[0]);
				accountMap.put("adf1", (String) objects[1]);
				break;
			}
			tx.commit();

			return accountMap;
		} catch (ObjectNotFoundException objectNotFound) {
			handleException(objectNotFound, tx);
		} catch (HibernateException hibernateException) {
			handleException(hibernateException, tx);
		} finally {
			autoClose(session);
		}
		return accountMap;
	}



}
