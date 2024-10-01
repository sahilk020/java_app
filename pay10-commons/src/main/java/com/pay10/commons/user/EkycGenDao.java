package com.pay10.commons.user;

import org.hibernate.HibernateException;
import org.hibernate.ObjectNotFoundException;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.pay10.commons.dao.HibernateAbstractDao;
import com.pay10.commons.dao.HibernateSessionProvider;

@Component
public class EkycGenDao extends HibernateAbstractDao{

	private static Logger logger = LoggerFactory.getLogger(EkycGenDao.class.getName());
	
	public void insertEKyc(EkycGenTable ekycGenTable) {
		super.save(ekycGenTable);
	}

	public void updateResponse(EkycGenTable ekycGenTable) {
		super.saveOrUpdate(ekycGenTable);
	}

	public EkycGenTable findByPayIdAndTxnId(String payId, String txnId) {
		EkycGenTable ekycGenTable = null;
		Session session = HibernateSessionProvider.getSession();
		Transaction tx = session.beginTransaction();
		try {
			
			ekycGenTable =  (EkycGenTable)session.createQuery("from EkycGenTable U where U.payId = :payId and U.txnId = :txnId")
					.setParameter("payId", payId).setParameter("txnId", txnId).setCacheable(true).getSingleResult();
		
			tx.commit();
		} catch (ObjectNotFoundException objectNotFound) {
			handleException(objectNotFound,tx);
		} catch (HibernateException hibernateException) {
			handleException(hibernateException,tx);
		} 
		finally {
			autoClose(session);
		}
		return ekycGenTable;

	}
	
}
