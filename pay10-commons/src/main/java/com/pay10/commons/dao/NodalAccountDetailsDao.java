package com.pay10.commons.dao;

import java.util.List;

import org.hibernate.HibernateException;
import org.hibernate.ObjectNotFoundException;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.pay10.commons.exception.DataAccessLayerException;
import com.pay10.commons.user.CompanyProfile;
import com.pay10.commons.user.NodalAccountDetails;
import com.pay10.commons.util.AcquirerTypeNodal;
import com.pay10.commons.util.CurrencyTypes;
import com.pay10.commons.util.TransactionManager;

@Component("nodalAccountDetailsDao")
public class NodalAccountDetailsDao extends HibernateAbstractDao {

	private static Logger logger = LoggerFactory.getLogger(NodalAccountDetailsDao.class.getName());
	
	public NodalAccountDetailsDao() {
		super();
	}
	
	public void create(NodalAccountDetails np) throws DataAccessLayerException {
		logger.info("Adding nodal Account");
		super.save(np);
		logger.info("Nodal account added successfully");
	}
	
	public NodalAccountDetails find(String acquirer) throws DataAccessLayerException {
		NodalAccountDetails responseCp = null;
		Session session = HibernateSessionProvider.getSession();
		Transaction tx = session.beginTransaction();
		try {
			List<NodalAccountDetails> cps =  session.createQuery("from NodalAccountDetails cp where cp.acquirer = :acquirer").setParameter("acquirer", acquirer).getResultList();
				for (NodalAccountDetails cp : cps) {
					responseCp = cp;
					break;
				}
			tx.commit();
			return responseCp;
		} catch (ObjectNotFoundException objectNotFound) {
			handleException(objectNotFound,tx);
		} catch (HibernateException hibernateException) {
			handleException(hibernateException,tx);
		} 
		finally {
			autoClose(session);
		}
		return responseCp;
	}
	
}
