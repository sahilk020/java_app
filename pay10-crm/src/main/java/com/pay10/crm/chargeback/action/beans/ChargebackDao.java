package com.pay10.crm.chargeback.action.beans;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.NoResultException;
import javax.transaction.SystemException;

import org.hibernate.HibernateException;
import org.hibernate.ObjectNotFoundException;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pay10.commons.dao.HibernateAbstractDao;
import com.pay10.commons.dao.HibernateSessionProvider;
import com.pay10.commons.exception.DataAccessLayerException;
import com.pay10.commons.exception.ErrorType;
import com.pay10.commons.user.Chargeback;
import com.pay10.crm.chargeback.util.ChargebackType;

//Match from old chargebackDaobefore deleting old chargebackDao
//@Component
public class ChargebackDao extends HibernateAbstractDao {

	private static Logger logger = LoggerFactory.getLogger(ChargebackDao.class.getName());

	public ChargebackDao() {
		super();
	}

	public void create(Chargeback chargeback) throws DataAccessLayerException {
		super.save(chargeback);
	}

	public void delete(Chargeback chargeback) throws DataAccessLayerException {
		super.delete(chargeback);
	}

	public void update(Chargeback chargeback) throws DataAccessLayerException {
		super.saveOrUpdate(chargeback);
	}
	
	public Chargeback find(String name) throws DataAccessLayerException {
		return (Chargeback) super.find(Chargeback.class, name);
	}

	public List<Chargeback> findChargebackByPayid(String payId, String fromDate, String toDate) {
		List<Chargeback> userChargeback = new ArrayList<Chargeback>();
		Session session = HibernateSessionProvider.getSession();
		Transaction tx = session.beginTransaction();
		try {
			
			String sqlQuery = "select ch.* from Chargeback ch where ch.createDate >= :fromDate and ch.createDate <= :toDate and payId like :payId";
			if (payId.equals("ALL")) {
				payId = "%";
			}
			
			userChargeback = session.createNativeQuery(sqlQuery, Chargeback.class)
										   .setParameter("payId", payId)
										    .setParameter("fromDate", fromDate)
										     .setParameter("toDate", toDate+"23:59:59")
										   .getResultList();
			tx.commit();
		} catch (ObjectNotFoundException objectNotFound) {
			handleException(objectNotFound,tx);
		} catch (HibernateException hibernateException) {
			handleException(hibernateException,tx);
		} finally {
			autoClose(session);
		}
		return userChargeback;
	}

	public List<Chargeback> findAllChargeback(String dateFrom, String dateTo, String payId, String chargebackType,
			String chargebackStatus) {
		List<Chargeback> userChargeback = new ArrayList<Chargeback>();
		Session session = HibernateSessionProvider.getSession();
		Transaction tx = session.beginTransaction();
		try {
			

			String sqlQuery = "select ch.* from Chargeback ch where ch.createDate >= :fromDate and ch.createDate <= :toDate and payId like :payId  and chargebackType like :chargebackType and chargebackStatus like :chargebackStatus ORDER BY ID DESC";
			if (payId.equals("ALL")) {
				payId = "%";
			}
			if (chargebackType.equals("ALL")) {
				chargebackType = "%";
			}
			if (chargebackStatus.equals("ALL")) {
				chargebackStatus = "%";
			}

			userChargeback = session.createNativeQuery(sqlQuery, Chargeback.class)
					.setParameter("fromDate", dateFrom).setParameter("toDate", dateTo + " 23:59:59")
					.setParameter("payId", payId).setParameter("chargebackType", chargebackType)
					.setParameter("chargebackStatus", chargebackStatus).getResultList();
			tx.commit();
		} catch (ObjectNotFoundException objectNotFound) {
			handleException(objectNotFound,tx);
		} catch (HibernateException hibernateException) {
			handleException(hibernateException,tx);
		} finally {
			autoClose(session);
		}
		return userChargeback;
	}

	public Chargeback findByCaseId(String caseID) {
		Session session = HibernateSessionProvider.getSession();
		Transaction tx = session.beginTransaction();
		Chargeback responseUser = null;
		try {
			
			responseUser = (Chargeback) session.createQuery("from Chargeback CH where CH.caseId = :caseId")
					.setParameter("caseId", caseID).setCacheable(true).getSingleResult();
			tx.commit();
			return responseUser;
		} catch (NoResultException noResultException) {
			return null;
		} catch (ObjectNotFoundException objectNotFound) {
			handleException(objectNotFound,tx);
		} catch (HibernateException hibernateException) {
			logger.error("error"+hibernateException);
			handleException(hibernateException,tx);
		} finally {
			autoClose(session);
		}
		return responseUser;
	}
	
	public Chargeback findByTxnId(String txnID , String status , String chargebackType) {

		Chargeback chargeback = null;
		Session session = HibernateSessionProvider.getSession();
		Transaction tx = session.beginTransaction();
		try {
			
			if (chargebackType.equals(ChargebackType.PRE_ARBITRATION.getName())){
				chargeback = (Chargeback) session.createQuery("from Chargeback CH where CH.transactionId = :txnID and CH.status != 'Closed' ")
						.setParameter("txnID", txnID).setCacheable(true).getSingleResult();
				tx.commit();
				return chargeback;
			}
			else{
				chargeback = (Chargeback) session.createQuery("from Chargeback CH where CH.transactionId = :txnID ")
						.setParameter("txnID", txnID).setCacheable(true).getSingleResult();
				tx.commit();
				return chargeback;
			}
		} catch (NoResultException noResultException) {
			return null;
		} catch (ObjectNotFoundException objectNotFound) {
			handleException(objectNotFound,tx);
		} catch (HibernateException hibernateException) {
			logger.error("error"+hibernateException);
			handleException(hibernateException,tx);
		} 
		catch (Exception e){
			e.printStackTrace();
		}
		finally {
			autoClose(session);
		}
		return chargeback;
	}

	public void updateComment(String caseId, byte[] comments) throws SystemException {
		Session session = HibernateSessionProvider.getSession();
		Transaction tx = session.beginTransaction();
		try {
			
			int updateStatus = session
					.createQuery("update Chargeback C set C.comments = :comments where C.caseId = :caseId")
					.setParameter("caseId", caseId).setParameter("comments", comments).executeUpdate();
			tx.commit();
			if (1 != updateStatus) {
				throw new SystemException(
						"Error updating comments with Chargeback: " + caseId + ErrorType.DATABASE_ERROR);
			}
		} catch (ObjectNotFoundException objectNotFound) {
			handleException(objectNotFound,tx);
		} catch (HibernateException hibernateException) {
			handleException(hibernateException,tx);
		} finally {
			autoClose(session);
		}
	}

	public void updateStatus(String chargebackStatus, String caseId) throws SystemException {
		Session session = HibernateSessionProvider.getSession();
		Transaction tx = session.beginTransaction();
		try {
			int updateStatus = session
					.createQuery(
							"update Chargeback C set C.chargebackStatus = :chargebackStatus where C.caseId = :caseId")
					.setParameter("caseId", caseId).setParameter("chargebackStatus", chargebackStatus).executeUpdate();
			tx.commit();
			if (1 != updateStatus) {
				throw new SystemException(
						"Error updating Status with Chargeback: " + caseId + ErrorType.DATABASE_ERROR);
			}
		} catch (ObjectNotFoundException objectNotFound) {
			handleException(objectNotFound,tx);
		} catch (HibernateException hibernateException) {
			handleException(hibernateException,tx);
		} finally {
			autoClose(session);
		}
	}

}
