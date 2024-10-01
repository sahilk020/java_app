package com.pay10.commons.user;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.HibernateException;
import org.hibernate.ObjectNotFoundException;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.pay10.commons.dao.HibernateAbstractDao;
import com.pay10.commons.dao.HibernateSessionProvider;

@Component("AcquirerMasterPayoutDao")
public class AcquirerMasterPayoutDao extends HibernateAbstractDao {
	private static final Logger logger = LoggerFactory.getLogger(AcquirerMasterPayoutDao.class.getName());

	public List<String> getPayoutAcquirer() {
		List<String> acqList = new ArrayList<String>();

		Session session = HibernateSessionProvider.getSession();
		Transaction tx = session.beginTransaction();
		try {
			acqList = session.createQuery(
					"Select acquirer from AcquirerMasterPayout group by acquirer")
					.setCacheable(true).getResultList();
			tx.commit();
			return acqList;
		} catch (ObjectNotFoundException objectNotFound) {
			handleException(objectNotFound, tx);
		} catch (HibernateException hibernateException) {
			handleException(hibernateException, tx);
		} finally {
			autoClose(session);
		}
		return acqList;
	}

	public List<String> getchannelByAcquier(String acquirer) {
		List<String> chanList = new ArrayList<String>();

		Session session = HibernateSessionProvider.getSession();
		Transaction tx = session.beginTransaction();
		try {
			chanList = session.createQuery(
					"Select channel from AcquirerMasterPayout RC where RC.acquirer = :acquirer group by channel")
					.setParameter("acquirer", acquirer)
					.setCacheable(true).getResultList();
			tx.commit();
			return chanList;
		} catch (ObjectNotFoundException objectNotFound) {
			handleException(objectNotFound, tx);
		} catch (HibernateException hibernateException) {
			handleException(hibernateException, tx);
		} finally {
			autoClose(session);
		}
		return chanList;
	
	
	}

	public List<String> getcurrencyByAcquierAndChannel(String acquirer, String channel) {
		List<String> chanList = new ArrayList<String>();

		Session session = HibernateSessionProvider.getSession();
		Transaction tx = session.beginTransaction();
		try {
			chanList = session.createQuery(
					"Select currency from AcquirerMasterPayout RC where RC.acquirer = :acquirer and RC.channel = :channel group by currency")
					.setParameter("acquirer", acquirer)
					.setParameter("channel", channel)
					.setCacheable(true).getResultList();
			tx.commit();
			return chanList;
		} catch (ObjectNotFoundException objectNotFound) {
			handleException(objectNotFound, tx);
		} catch (HibernateException hibernateException) {
			handleException(hibernateException, tx);
		} finally {
			autoClose(session);
		}
		return chanList;
	
	}
	
	
	
}
