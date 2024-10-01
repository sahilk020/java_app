package com.pay10.commons.dao;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.HibernateException;
import org.hibernate.ObjectNotFoundException;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.springframework.stereotype.Service;

import com.pay10.commons.exception.DataAccessLayerException;
import com.pay10.commons.user.TPAPTransactionLimit;

@Service
public class TPAPDao extends HibernateAbstractDao {



	public void save(TPAPTransactionLimit tpapTransactionLimit) throws DataAccessLayerException {
		super.save(tpapTransactionLimit);
	}

	public TPAPTransactionLimit getTpapTxnInfoById(long id) {
		return (TPAPTransactionLimit) super.find(TPAPTransactionLimit.class, id);
	}

	public void update(TPAPTransactionLimit tpapTransactionLimit) {
		super.saveOrUpdate(tpapTransactionLimit);
	}

	public long getTpapTxnInfoAll(String acquirerName, String paymentType) {
		Session session = HibernateSessionProvider.getSession();
		Transaction tx = session.beginTransaction();
		try {
			long count = (long) session.createQuery(
					"Select count(*) from TPAPTransactionLimit a where a.acquirerName=:acquirerName and paymentType=:paymentType and status=:status")
					.setParameter("acquirerName", acquirerName).setParameter("paymentType", paymentType)
					.setParameter("status", "ACTIVE").getSingleResult();
			tx.commit();
			return count;
		} finally {
			autoClose(session);
		}
	}

	public long getAcquirerDTConfigById(String acquirerName, String paymentType, long Id) {
		Session session = HibernateSessionProvider.getSession();
		Transaction tx = session.beginTransaction();
		try {
			long count = (long) session
					.createQuery(
							"Select count(*) from TPAPTransactionLimit a where a.acquirerName=:acquirerName "
									+ "and paymentType=:paymentType and status=:status and id=:id")
					.setParameter("acquirerName", acquirerName).setParameter("paymentType", paymentType)
					.setParameter("status", "ACTIVE").setParameter("id", Id).getSingleResult();
			tx.commit();
			return count;
		} finally {
			autoClose(session);
		}
	}

	public long getTxnLimitById(long Id) {
		Session session = HibernateSessionProvider.getSession();
		Transaction tx = session.beginTransaction();
		try {
			long count = (long) session.createQuery("Select count(*) from TPAPTransactionLimit a where id=:id and status=:status")
					.setParameter("id", Id)
					.setParameter("status", "ACTIVE").getSingleResult();
			tx.commit();
			return count;
		} finally {
			autoClose(session);
		}
	}

	public List<TPAPTransactionLimit> getAllAcquirerDTConfigSearch(String acquirerName,String paymentType) {
		Session session = HibernateSessionProvider.getSession();
		Transaction tx = session.beginTransaction();
		List<TPAPTransactionLimit> tpapTxnInfo = new ArrayList<TPAPTransactionLimit>();
		
		
		try {
			
			String query="FROM TPAPTransactionLimit WHERE status='ACTIVE'";
			if (!acquirerName.isEmpty()) {
				query=query+" and acquirerName='"+acquirerName+"'";
			}
			if (!paymentType.isEmpty()) {
				query=query+" and paymentType='"+paymentType+"'";
			}
			System.out.println("aaaaa"+query);
			
			tpapTxnInfo = session.createQuery(query).getResultList();
			tx.commit();
			return tpapTxnInfo;

		} catch (ObjectNotFoundException objectNotFound) {
			handleException(objectNotFound, tx);
		} catch (HibernateException hibernateException) {
			handleException(hibernateException, tx);
		} finally {
			autoClose(session);
		}
		return tpapTxnInfo;
	}

	public List<TPAPTransactionLimit> getAllTpapTransactionLimit() {
		Session session = HibernateSessionProvider.getSession();
		Transaction tx = session.beginTransaction();
		List<TPAPTransactionLimit> tpapTxnInfo = new ArrayList<TPAPTransactionLimit>();
		try {
			tpapTxnInfo = session.createQuery("FROM TPAPTransactionLimit a WHERE status=:status")
					.setParameter("status", "ACTIVE").getResultList();
			tx.commit();
			return tpapTxnInfo;

		} catch (ObjectNotFoundException objectNotFound) {
			handleException(objectNotFound, tx);
		} catch (HibernateException hibernateException) {
			handleException(hibernateException, tx);
		} finally {
			autoClose(session);
		}
		return tpapTxnInfo;
	}

	public void inactiveTxnLimitById(String txnLimitRuleId, String updatedDate) {
		Session session = HibernateSessionProvider.getSession();
		Transaction tx = session.beginTransaction();
		try {
			session.createQuery(
					"update TPAPTransactionLimit a set a.status=:status, updateBy=:updateBy, updatedDate=:updatedDate where a.id=:id")
					.setParameter("status", "INACTIVE").setParameter("updateBy", "ADMIN")
					.setParameter("updatedDate", updatedDate).setParameter("id", Long.valueOf(txnLimitRuleId))
					.executeUpdate();
			tx.commit();
		} catch (ObjectNotFoundException objectNotFound) {
			handleException(objectNotFound, tx);
		} catch (HibernateException hibernateException) {
			handleException(hibernateException, tx);
		} finally {
			autoClose(session);
		}
	}

}
