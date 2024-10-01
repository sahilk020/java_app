package com.pay10.commons.repository;

import org.hibernate.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import com.pay10.commons.dao.HibernateAbstractDao;
import com.pay10.commons.dao.HibernateSessionProvider;
import com.pay10.commons.user.AadharEntity;

@Repository
public class AadharApiRepository extends HibernateAbstractDao {
    private static Logger logger = LoggerFactory.getLogger(AadharApiRepository.class.getName());
	public AadharEntity findByPayId(String payId) {
		Session session = HibernateSessionProvider.getSession();
		try {
			return (AadharEntity) session.createQuery("from AadharEntity U where U.payId = :payId")
					.setParameter("payId", payId).setCacheable(true).getSingleResult();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (session != null) {
				session.close();
			}
		}
		return null;

	}

	public AadharEntity saveOrUpdate(AadharEntity aadharEntity) {
		super.saveOrUpdate(aadharEntity);
		return aadharEntity;
	}

	public AadharEntity findByPayIdAndTxnId(String payId, String txnId) {
		Session session = HibernateSessionProvider.getSession();
		try {
			logger.info("findByPayIdAndTxnId : " + payId + " : " + txnId);
			AadharEntity aadhar= (AadharEntity) session.createQuery("from AadharEntity U where U.payId = :payId and U.txnId = :txnId")
					.setParameter("payId", payId).setParameter("txnId", txnId).setCacheable(true).getSingleResult();
			logger.info("findByPayIdAndTxnId : " + aadhar);
			return aadhar;
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (session != null) {
				session.close();
			}
		}
		return null;

	}

}
