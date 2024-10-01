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

@Component("UserCommissionDao")
//@Scope(value = "prototype", proxyMode=ScopedProxyMode.TARGET_CLASS)
public class UserCommissionDao extends HibernateAbstractDao {

	private static Logger logger = LoggerFactory.getLogger(UserCommissionDao.class.getName());

	public void saveandUpdate(UserCommision commision) throws DataAccessLayerException {
		super.save(commision);
	}

	public void saveandUpdate1(UserCommision commision) throws DataAccessLayerException {
		super.saveOrUpdate(commision);
	}

	@SuppressWarnings("unchecked")
	public List<UserCommision> getchargingdetail(String smaPayId, String maPayId, String agentPayId, String merchantPayId, String currency) {
		logger.info("Inside User get charging detail......." + "smaPayId...." + smaPayId
				+ "maPayId....." + maPayId+"agentPayId...."+agentPayId+"merchantPayId......"+merchantPayId);
		Session session = HibernateSessionProvider.getSession();
		Transaction tx = session.beginTransaction();
		List<UserCommision> chargingdata = new ArrayList<UserCommision>();
		try {
			chargingdata = session.createQuery(
					"FROM UserCommision WHERE smaPayId=:smaPayId and maPayId=:maPayId and agentPayId=:agentPayId and merchantPayId=:merchantPayId and currency=:currency")
					.setParameter("smaPayId", smaPayId).setParameter("maPayId", maPayId)
					.setParameter("agentPayId", agentPayId).setParameter("merchantPayId", merchantPayId)
					.setParameter("currency", currency)
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
	public List<String> mopandpayment(String smaPayId, String maPayId, String agentPayId, String merchantPayId,
			String transactiontype, String mop) {
		Session session = HibernateSessionProvider.getSession();
		Transaction tx = session.beginTransaction();
		List<String> chargingdata = new ArrayList<String>();
		try {
			chargingdata = session.createQuery(
					"FROM UserCommision  WHERE smaPayId=:smaPayId and maPayId=:maPayId and agentPayId=:agentPayId and merchantPayId=:merchantPayId and mop=:mop and transactiontype=:transactiontype")
					.setParameter("smaPayId", smaPayId)
					.setParameter("maPayId", maPayId)
					.setParameter("agentPayId", agentPayId)
					.setParameter("merchantPayId", merchantPayId)
					.setParameter("mop", mop)
					.setParameter("transactiontype", transactiontype)
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


	public UserCommision findByPayId(long id) {
		Session session = HibernateSessionProvider.getSession();
		Transaction tx = session.beginTransaction();

		UserCommision responseUser = null;
		try {
			logger.info("#######################################" + id);
			responseUser = (UserCommision) session.createQuery("FROM UserCommision RC WHERE id=:id")
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
	public List<UserCommision> findAll() {
		return super.findAll(UserCommision.class);
	}

//	@SuppressWarnings("unchecked")
//	public List<String> getallresellerpayid(String cycle) {
//		Session session = HibernateSessionProvider.getSession();
//		try {
//			return session.createQuery(
//					"Select DISTINCT R.resellerpayid FROM Resellercommision R, User u  JOIN R.resellerpayid = u.payId Where R.resellerpayid = u.payId AND u.cycle=:cycle")
//					.setParameter("cycle", cycle).getResultList();
//
//		} finally {
//			autoClose(session);
//		}
//	}

//	public List<String> getresellerpayidbycycle(String cycle) {
//		Session session = HibernateSessionProvider.getSession();
//		Transaction tx = session.beginTransaction();
//
//		List<String> resellerpayid = new ArrayList<String>();
//		try {
//			logger.info("##########################################" + cycle);
//			resellerpayid = session.createQuery(" Select U.payId from User U where U.cycle = :cycle")
//					.setParameter("cycle", cycle).getResultList();
//			tx.commit();
//
//			return resellerpayid;
//		} catch (ObjectNotFoundException objectNotFound) {
//			handleException(objectNotFound, tx);
//		} catch (HibernateException hibernateException) {
//			handleException(hibernateException, tx);
//		} finally {
//			autoClose(session);
//		}
//		return resellerpayid;
//	}

	public boolean isMappedReseller(String payId) {
		Session session = HibernateSessionProvider.getSession();
		try {
			return session.createQuery("FROM Usercommision R WHERE R.merchantpayid=:payId AND (R.commission_percent !=:Percentage OR R.commission_amount !=:AMOUNT)")
					.setParameter("payId", payId).setParameter("Percentage", "0").setParameter("AMOUNT", "0").getResultList().size() > 0;
		} finally {
			autoClose(session);
		}
	}

	public List<String> findBydiffId(String smaPayId, String maPayId, String agentPayId, String merchantPayId) {
		Session session = HibernateSessionProvider.getSession();
		Transaction tx = session.beginTransaction();
		List<String> chargingdata = new ArrayList<String>();
		try {
			chargingdata = session.createQuery(
					"Select distinct currency FROM UserCommision  WHERE smaPayId=:smaPayId and maPayId=:maPayId and agentPayId=:agentPayId and merchantPayId=:merchantPayId")
					.setParameter("smaPayId", smaPayId).setParameter("maPayId", maPayId)
					.setParameter("agentPayId", agentPayId)
					.setParameter("merchantPayId", merchantPayId)
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

	public List<String> findByUserdiffId(String userType,String resellerId, String merchantPayId) {
		Session session = HibernateSessionProvider.getSession();
		Transaction tx = session.beginTransaction();
		List<String> chargingdata = new ArrayList<String>();
		
		try {
			if(userType.equalsIgnoreCase("SMA")) {
				chargingdata = session.createQuery(
						"Select distinct currency FROM UserCommision  WHERE smaPayId=:resellerId and merchantPayId=:merchantPayId")
						.setParameter("resellerId", resellerId).setParameter("merchantPayId", merchantPayId)
						.getResultList();
			}
			
			if(userType.equalsIgnoreCase("MA")) {
				chargingdata = session.createQuery(
						"Select distinct currency FROM UserCommision  WHERE maPayId=:resellerId and merchantPayId=:merchantPayId")
						.setParameter("resellerId", resellerId).setParameter("merchantPayId", merchantPayId)
						.getResultList();
			}
			
			if(userType.equals("Agent")) {
				chargingdata = session.createQuery(
						"Select distinct currency FROM UserCommision  WHERE agentPayId=:resellerId and merchantPayId=:merchantPayId")
						.setParameter("resellerId", resellerId).setParameter("merchantPayId", merchantPayId)
						.getResultList();
			}
			
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

	public List<String> findBydiffUserId(String userType, String resellerId) {
		Session session = HibernateSessionProvider.getSession();
		Transaction tx = session.beginTransaction();
		List<String> chargingdata = new ArrayList<String>();
		
		try {
			if(userType.equalsIgnoreCase("SMA")) {
				chargingdata = session.createQuery(
						"Select distinct currency FROM UserCommision  WHERE smaPayId=:resellerId")
						.setParameter("resellerId", resellerId)
						.getResultList();
			}
			
			if(userType.equalsIgnoreCase("MA")) {
				chargingdata = session.createQuery(
						"Select distinct currency FROM UserCommision  WHERE maPayId=:resellerId")
						.setParameter("resellerId", resellerId)
						.getResultList();
			}
			
			if(userType.equals("Agent")) {
				chargingdata = session.createQuery(
						"Select distinct currency FROM UserCommision  WHERE agentPayId=:resellerId")
						.setParameter("resellerId", resellerId)
						.getResultList();
			}
			
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