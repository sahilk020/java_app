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
public class EkycVerDao extends HibernateAbstractDao {

private static Logger logger = LoggerFactory.getLogger(EkycVerDao.class.getName());
	
	public void insertEKyc(EkycVerTable ekycVerTable) {
		super.save(ekycVerTable);
	}

	public void updateResponse(EkycVerTable ekycVerTable) {
		super.saveOrUpdate(ekycVerTable);
	}

	public EkycVerTable findByPayIdAndTxnid(String payId, String txnId) {
		EkycVerTable ekycVerTable = null;
		Session session = HibernateSessionProvider.getSession();
		Transaction tx = session.beginTransaction();
		try {
			
			ekycVerTable =  (EkycVerTable)session.createQuery("from EkycVerTable U where U.payId = :payId and U.txnId = :txnId")
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
		return ekycVerTable;
	}
	
}
