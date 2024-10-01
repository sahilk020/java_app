package com.pay10.commons.user;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.HibernateException;
import org.hibernate.ObjectNotFoundException;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.springframework.stereotype.Component;

import com.pay10.commons.dao.HibernateAbstractDao;
import com.pay10.commons.dao.HibernateSessionProvider;
import com.pay10.commons.exception.DataAccessLayerException;

@Component
public class MerchantStatusLogDao extends HibernateAbstractDao{

	
	public List<MerchantStatusLog> getMerchantByEmailId(String emailId) throws DataAccessLayerException{
		List<MerchantStatusLog> merchantStatusLogs = new ArrayList<MerchantStatusLog>();
		Session session = HibernateSessionProvider.getSession();
		Transaction tx = session.beginTransaction();
		
		try {
			String sqlQuery = "select * from merchant_status_log where emailId=:emailId order by timeStamp DESC";
			merchantStatusLogs = session.createNativeQuery(sqlQuery, MerchantStatusLog.class).setParameter("emailId", emailId)
					.getResultList();
			tx.commit();
		} catch (ObjectNotFoundException objectNotFound) {
			handleException(objectNotFound, tx);
		} catch (HibernateException hibernateException) {
			handleException(hibernateException, tx);
		} finally {
			autoClose(session);
		}
		return merchantStatusLogs;
	}
	
	public void saveMerchantStatusLog(MerchantStatusLog merchantStatusLog) {
		super.save(merchantStatusLog);
	}
	
}
