package com.pay10.commons.user;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.NoResultException;

import org.hibernate.HibernateException;
import org.hibernate.ObjectNotFoundException;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.pay10.commons.dao.HibernateAbstractDao;
import com.pay10.commons.dao.HibernateSessionProvider;
import com.pay10.commons.exception.DataAccessLayerException;

@Component("AcquirerDowntimeSchedulingDao")
public class AcquirerDowntimeSchedulingDao extends HibernateAbstractDao {

	private static Logger logger = LoggerFactory.getLogger(AcquirerDowntimeSchedulingDao.class.getName());

	
	public AcquirerDowntimeSchedulingDao() {
		super();
	}
	public void createAndUpdate(AcquirerDowntimeScheduling acquirerschadular) throws DataAccessLayerException {
        super.saveOrUpdate(acquirerschadular);

	}
	public List<AcquirerDowntimeScheduling> getRuleList() {
		List<AcquirerDowntimeScheduling> rulesList = new ArrayList<AcquirerDowntimeScheduling>();
		rulesList = null;
		boolean currentlyActive = true;
		Session session = HibernateSessionProvider.getSession();
		Transaction tx = session.beginTransaction();
		try {

			rulesList = session.createQuery(
					"from AcquirerDowntimeScheduling RC where RC.status='ACTIVE'  order by RC.id desc")
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
		return rulesList;	}
	
	
	public AcquirerDowntimeScheduling findById(long id) {
		Session session = HibernateSessionProvider.getSession();
		Transaction tx = session.beginTransaction();

		AcquirerDowntimeScheduling responseUser = null;
		try {
			responseUser = (AcquirerDowntimeScheduling) session.createQuery("from AcquirerDowntimeScheduling U where U.id = :id").setParameter("id", id)
					.setCacheable(true).getSingleResult();

			tx.commit();
			// userMap.put(payId1, responseUser);
			return responseUser;
		} catch (NoResultException noResultException) {
			return null;
		} catch (ObjectNotFoundException objectNotFound) {
			handleException(objectNotFound, tx);
		} catch (HibernateException hibernateException) {
			logger.error("error" + hibernateException);
			handleException(hibernateException, tx);
		} catch (Exception e) {
			logger.error("error " + e);
		} finally {
			autoClose(session);
		}
		return responseUser;
	}
	public void createAndUpdate(AcquirerSwitchHistory acquirerSwitchHistory) {
        super.saveOrUpdate(acquirerSwitchHistory);
		
	}
	public List<AcquirerSwitchHistory> getAcquirerHistory(String acquirerName, String paymentType) {
		List<AcquirerSwitchHistory> rulesList = new ArrayList<AcquirerSwitchHistory>();

		Session session = HibernateSessionProvider.getSession();
		Transaction tx = session.beginTransaction();
		try {
			rulesList = session.createQuery(
					"from AcquirerSwitchHistory RC where  RC.acquirerName = :acquirerName and RC.paymentType = :paymentType ")
					.setParameter("acquirerName", acquirerName)
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
	
	
	
	public List<AcquirerDowntimeScheduling> checkTimeSlotAcquirer(AcquirerDowntimeScheduling createRule) {
		List<AcquirerDowntimeScheduling> rulesList = new ArrayList<AcquirerDowntimeScheduling>();
		Session session = HibernateSessionProvider.getSession();
		Transaction tx = session.beginTransaction();
		try {
			rulesList = session.createQuery(
					"from AcquirerDowntimeScheduling RC where  RC.acquirerName = :acquirerName and RC.paymentType = :paymentType  and RC.status='ACTIVE'  ")
					.setParameter("acquirerName", createRule.getAcquirerName())
					

					.setParameter("paymentType", createRule.getPaymentType()).setCacheable(true).getResultList();
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
	
	
	public List<AcquirerSwitchHistory> getAcquirerHistoryDEfault() {
		List<AcquirerSwitchHistory> rulesList = new ArrayList<AcquirerSwitchHistory>();

		Session session = HibernateSessionProvider.getSession();
		Transaction tx = session.beginTransaction();
		try {
			rulesList = session.createQuery(
					"from AcquirerSwitchHistory RC  order by id desc ")
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
	public List<AcquirerDowntimeScheduling> getListtoDate() {
		List<AcquirerDowntimeScheduling> rulesList = new ArrayList<AcquirerDowntimeScheduling>();
		rulesList = null;
		boolean currentlyActive = true;
		Session session = HibernateSessionProvider.getSession();
		Transaction tx = session.beginTransaction();
		try {

			rulesList = session.createQuery(
					"from AcquirerDowntimeScheduling RC where RC.status='ACTIVE'  and  RC.toDate between :dateFrom  and :dateTo")
					.setParameter("dateFrom", getCurrentDate().concat("00"))
					.setParameter("dateTo", getCurrentDate().concat("59")).setCacheable(true).getResultList();
			tx.commit();
			return rulesList;
		} catch (ObjectNotFoundException objectNotFound) {
			handleException(objectNotFound, tx);
		} catch (HibernateException hibernateException) {
			handleException(hibernateException, tx);
		} finally {
			autoClose(session);
		}
		return rulesList;	}
	
	
	
	
	public List<AcquirerDowntimeScheduling> getListFromDate() {
		List<AcquirerDowntimeScheduling> rulesList = new ArrayList<AcquirerDowntimeScheduling>();
		rulesList = null;
		boolean currentlyActive = true;
		Session session = HibernateSessionProvider.getSession();
		Transaction tx = session.beginTransaction();
		try {

			rulesList = session.createQuery(
					"from AcquirerDowntimeScheduling RC where RC.status='ACTIVE'  and RC.fromDate between  :dateFrom  and :dateTo")
					.setParameter("dateFrom",  getCurrentDate().concat("00"))
					.setParameter("dateTo",  getCurrentDate().concat("59")).setCacheable(true).getResultList();
			tx.commit();
			return rulesList;
		} catch (ObjectNotFoundException objectNotFound) {
			handleException(objectNotFound, tx);
		} catch (HibernateException hibernateException) {
			handleException(hibernateException, tx);
		} finally {
			autoClose(session);
		}
		return rulesList;	}
	
	
	public String getCurrentDate() {
		DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:");
		LocalDateTime now = LocalDateTime.now();
		return dtf.format(now);
	}
	
	
}
