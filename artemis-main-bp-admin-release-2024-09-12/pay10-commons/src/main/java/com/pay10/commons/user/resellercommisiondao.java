package com.pay10.commons.user;

import java.util.ArrayList;
import java.util.List;

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

@Component("resellercommisiondao")
//@Scope(value = "prototype", proxyMode=ScopedProxyMode.TARGET_CLASS)
public class resellercommisiondao extends HibernateAbstractDao {

	private static Logger logger = LoggerFactory.getLogger(resellercommisiondao.class.getName());

	public void saveandUpdate(Resellercommision commision) throws DataAccessLayerException {
		super.save(commision);
	}

	public void saveandUpdate1(Resellercommision commision) throws DataAccessLayerException {
		super.saveOrUpdate(commision);
	}

	@SuppressWarnings("unchecked")
	public List<Resellercommision> getchargingdetail(String resellerpayid, String merchantpayid,String currency) {
		logger.info("Inside Reseller get charging detail......." + "resellerpayid...." + resellerpayid
				+ "merchantpayid....." + merchantpayid);
		Session session = HibernateSessionProvider.getSession();
		Transaction tx = session.beginTransaction();
		List<Resellercommision> chargingdata = new ArrayList<Resellercommision>();
		try {
			chargingdata = session.createQuery(
					"FROM Resellercommision RC WHERE resellerpayid=:resellerpayid and merchantpayid=:merchantpayid and currency=:currency")
					.setParameter("resellerpayid", resellerpayid).setParameter("merchantpayid", merchantpayid).setParameter("currency", currency)
					.getResultList();
			tx.commit();
			return chargingdata;

		} catch (ObjectNotFoundException objectNotFound) {
			handleException(objectNotFound, tx);
		} catch (HibernateException hibernateException) {
			handleException(hibernateException, tx);
		} finally {
			autoClose(session);
		}
		return chargingdata;

	}

	@SuppressWarnings("unchecked")
	public List<String> mopamdpayment(String merchantpayid, String resellerpayid, String transactiontype, String mop,String currency) {
		Session session = HibernateSessionProvider.getSession();
		Transaction tx = session.beginTransaction();
		List<String> chargingdata = new ArrayList<String>();
		try {
			chargingdata = session.createQuery(
					"FROM Resellercommision RC WHERE resellerpayid=:resellerpayid and merchantpayid=:merchantpayid and mop=:mop and transactiontype=:transactiontype and currency=:currency")
					.setParameter("merchantpayid", merchantpayid).setParameter("resellerpayid", resellerpayid)
					.setParameter("mop", mop).setParameter("transactiontype", transactiontype).setParameter("currency", currency).getResultList();
			tx.commit();
			return chargingdata;

		} catch (ObjectNotFoundException objectNotFound) {
			handleException(objectNotFound, tx);
		} catch (HibernateException hibernateException) {
			handleException(hibernateException, tx);
		} finally {
			autoClose(session);
		}
		return chargingdata;

	}

//	@SuppressWarnings("unchecked")
//	public Resellercommision findbyid(long id) {
//		Session session = HibernateSessionProvider.getSession();
//		Transaction tx = session.beginTransaction();
//		Resellercommision chargingdata = null ;
//		try {
//			logger.info("@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@"+id);
//			chargingdata = (Resellercommision) session.createQuery("FROM Resellercommision RC WHERE id=:id")
//					.setParameter("id", id).getResultList();
//			logger.info("####################################################");
//			tx.commit();
//			logger.info("@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@"+chargingdata.toString());
//			return chargingdata;
//
//		} catch (ObjectNotFoundException objectNotFound) {
//			objectNotFound.printStackTrace();
//			handleException(objectNotFound, tx);
//		} catch (HibernateException hibernateException) {
//			hibernateException.printStackTrace();
//	handleException(hibernateException, tx);
//		} finally {
//
//			autoClose(session);
//		}
//		return chargingdata;
//
//	}

	public Resellercommision findByPayId(long id) {
		Session session = HibernateSessionProvider.getSession();
		Transaction tx = session.beginTransaction();

		Resellercommision responseUser = null;
		try {
			logger.info("#######################################" + id);
			responseUser = (Resellercommision) session.createQuery("FROM Resellercommision RC WHERE id=:id")
					.setParameter("id", id).setCacheable(true).getSingleResult();

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

	@SuppressWarnings("unchecked")
	public List<Resellercommision> findAll() {
		return super.findAll(Resellercommision.class);
	}

	@SuppressWarnings("unchecked")
	public List<String> getallresellerpayid(String cycle) {
		Session session = HibernateSessionProvider.getSession();
		try {
			return session.createQuery(
					"Select DISTINCT R.resellerpayid FROM Resellercommision R, User u  JOIN R.resellerpayid = u.payId Where R.resellerpayid = u.payId AND u.cycle=:cycle")
					.setParameter("cycle", cycle).getResultList();

		} finally {
			autoClose(session);
		}
	}

	public List<String> getresellerpayidbycycle(String cycle) {
		Session session = HibernateSessionProvider.getSession();
		Transaction tx = session.beginTransaction();

		List<String> resellerpayid = new ArrayList<String>();
		try {
			logger.info("##########################################" + cycle);
			resellerpayid = session.createQuery(" Select U.payId from User U where U.cycle = :cycle")
					.setParameter("cycle", cycle).getResultList();
			tx.commit();

			return resellerpayid;
		} catch (ObjectNotFoundException objectNotFound) {
			handleException(objectNotFound, tx);
		} catch (HibernateException hibernateException) {
			handleException(hibernateException, tx);
		} finally {
			autoClose(session);
		}
		return resellerpayid;
	}

	public boolean isMappedReseller(String payId) {
		Session session = HibernateSessionProvider.getSession();
		try {
			return session.createQuery("FROM Resellercommision R WHERE R.merchantpayid=:payId AND (R.commission_percent !=:Percentage OR R.commission_amount !=:AMOUNT)")
					.setParameter("payId", payId).setParameter("Percentage", "0").setParameter("AMOUNT", "0").getResultList().size() > 0;
		} finally {
			autoClose(session);
		}
	}

	public List<String> findByResellerIdAndPayId(String merchantPayId, String payId) {
		Session session = HibernateSessionProvider.getSession();
		Transaction tx = session.beginTransaction();
		List<String> chargingdata = new ArrayList<String>();
		try {
			chargingdata = session.createQuery(
					"Select distinct currency FROM Resellercommision RC WHERE resellerpayid=:merchantPayId and merchantpayid=:payId")
					.setParameter("merchantPayId", merchantPayId).setParameter("payId", payId)
					.getResultList();
			tx.commit();
			return chargingdata;

		} catch (ObjectNotFoundException objectNotFound) {
			handleException(objectNotFound, tx);
		} catch (HibernateException hibernateException) {
			handleException(hibernateException, tx);
		} finally {
			autoClose(session);
		}
		return chargingdata;
	}
}