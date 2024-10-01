package com.pay10.crm.chargeback_new.action.beans;

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
import com.pay10.commons.user.Chargeback;
import com.pay10.crm.chargeback_new.util.ChargebackType;

// Match from old chargebackDaobefore deleting old chargebackDao.
@Component
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

	public Chargeback findByPgRefNum(String pgRefNum, String chargebackType) {
		Chargeback chargeback = null;
		Session session = HibernateSessionProvider.getSession();
		Transaction tx = session.beginTransaction();
		try {
			if (chargebackType.equals(ChargebackType.CHARGEBACK.getName())) {

				chargeback = (Chargeback) session.createQuery(
						"from Chargeback CH where CH.pg_ref_num = :pg_ref_num and CH.chargebackType = 'Charge Back'")
						.setParameter("pg_ref_num", pgRefNum).setCacheable(true).getSingleResult();
				tx.commit();
			} else if (chargebackType.equals(ChargebackType.PRE_ARBITRATION.getName())) {
				chargeback = (Chargeback) session.createQuery(
						"from Chargeback CH where CH.pg_ref_num = :pg_ref_num and CH.chargebackType = 'Pre Arbitration' order by CH.createDate desc ")
						.setParameter("pg_ref_num", pgRefNum).setCacheable(true).getSingleResult();
				tx.commit();
			}
			return chargeback;
		} catch (NoResultException noResultException) {
			return null;
		} catch (ObjectNotFoundException objectNotFound) {
			handleException(objectNotFound, tx);
		} catch (HibernateException hibernateException) {
			logger.error("error" + hibernateException);
			handleException(hibernateException, tx);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			autoClose(session);
		}
		return chargeback;
	}

	// author shubhamchauhan
	// not in use.
	@SuppressWarnings("unchecked")
	public List<Chargeback> findByPgRefNums(String pgRefNum, String chargebackType) {
		List<Chargeback> list = new ArrayList<>();
		Session session = HibernateSessionProvider.getSession();
		Transaction tx = session.beginTransaction();
		try {
			if (chargebackType.equals(ChargebackType.PRE_ARBITRATION.getName())) {
				list = (List<Chargeback>) session.createQuery(
						"from Chargeback CH where CH.pg_ref_num = :pg_ref_num and CH.chargebackStatus != 'Closed' and CH.chargebackType = 'Pre Arbitration' order by CH.createDate desc ")
						.setParameter("pg_ref_num", pgRefNum).setCacheable(true).list();
				tx.commit();
				return list;
			}
		} catch (NoResultException noResultException) {
			return null;
		} catch (ObjectNotFoundException objectNotFound) {
			handleException(objectNotFound, tx);
		} catch (HibernateException hibernateException) {
			logger.error("error" + hibernateException);
			handleException(hibernateException, tx);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			autoClose(session);
		}
		return list;
	}

	@SuppressWarnings("unchecked")
	public List<Chargeback> getListByPgRefNum(String pgRefNum) {
		List<Chargeback> list = new ArrayList<>();
		Session session = HibernateSessionProvider.getSession();
		Transaction tx = session.beginTransaction();
		try {
			list = (List<Chargeback>) session.createQuery("from Chargeback CH where CH.pg_ref_num = :pg_ref_num ")
					.setParameter("pg_ref_num", pgRefNum).setCacheable(true).list();
			tx.commit();
			return list;
		} catch (NoResultException noResultException) {
			return null;
		} catch (ObjectNotFoundException objectNotFound) {
			handleException(objectNotFound, tx);
		} catch (HibernateException hibernateException) {
			logger.error("error" + hibernateException);
			handleException(hibernateException, tx);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		} finally {
			autoClose(session);
		}
		return list;
	}

	public Chargeback findById(String id) {
		Session session = HibernateSessionProvider.getSession();
		Transaction tx = session.beginTransaction();
		Chargeback responseUser = null;
		try {

			responseUser = (Chargeback) session.createQuery("from Chargeback CH where CH.id = :id")
					.setParameter("id", id).setCacheable(true).getSingleResult();
			tx.commit();
			return responseUser;
		} catch (NoResultException noResultException) {
			return null;
		} catch (ObjectNotFoundException objectNotFound) {
			handleException(objectNotFound, tx);
		} catch (HibernateException hibernateException) {
			logger.error("error" + hibernateException);
			handleException(hibernateException, tx);
		} finally {
			autoClose(session);
		}
		return responseUser;
	}

	@SuppressWarnings("unchecked")
	public List<Chargeback> findByCaseId(String caseId) {
		List<Chargeback> chargeback = null;
		Session session = HibernateSessionProvider.getSession();
		Transaction tx = session.beginTransaction();
		try {
			chargeback = session.createQuery(
					"from Chargeback CH where CH.caseId = :caseid")
					.setParameter("caseid", caseId).setCacheable(true).list();
			tx.commit();
			return chargeback;
		} catch (NoResultException noResultException) {
			return null;
		} catch (ObjectNotFoundException objectNotFound) {
			handleException(objectNotFound, tx);
		} catch (HibernateException hibernateException) {
			logger.error("error" + hibernateException);
			handleException(hibernateException, tx);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			autoClose(session);
		}
		return chargeback;
	}
	
	public List<Chargeback> findChargebackByPayid(String dateFrom, String dateTo, String payId, String chargebackType,
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

	public Chargeback findFraudByPgRefNum(String pgRefNum, String chargebackType) {
		Chargeback chargeback = null;
		Session session = HibernateSessionProvider.getSession();
		Transaction tx = session.beginTransaction();
		try {
			if (chargebackType.equals(ChargebackType.FRAUD_DISPUTES.getName())) {

				chargeback = (Chargeback) session.createQuery(
						"from Chargeback CH where CH.pg_ref_num = :pg_ref_num and CH.chargebackType = :chargebacktype")
						.setParameter("pg_ref_num", pgRefNum).setParameter("chargebacktype", ChargebackType.FRAUD_DISPUTES.getName()).setCacheable(true).getSingleResult();
				tx.commit();
			} else if (chargebackType.equals(ChargebackType.PRE_ARBITRATION.getName())) {
				chargeback = (Chargeback) session.createQuery(
						"from Chargeback CH where CH.pg_ref_num = :pg_ref_num and CH.chargebackType = ':chargebacktype' order by CH.createDate desc ")
						.setParameter("pg_ref_num", pgRefNum).setParameter("chargebacktype", ChargebackType.PRE_ARBITRATION.getName()).setCacheable(true).getSingleResult();
				tx.commit();
			}
			return chargeback;
		} catch (NoResultException noResultException) {
			return null;
		} catch (ObjectNotFoundException objectNotFound) {
			handleException(objectNotFound, tx);
		} catch (HibernateException hibernateException) {
			logger.error("error" + hibernateException);
			handleException(hibernateException, tx);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			autoClose(session);
		}
		return chargeback;
	}

}
