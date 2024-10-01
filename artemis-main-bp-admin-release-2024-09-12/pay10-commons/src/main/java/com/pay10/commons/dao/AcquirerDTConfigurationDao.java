package com.pay10.commons.dao;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.HibernateException;
import org.hibernate.ObjectNotFoundException;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.springframework.stereotype.Service;

import com.pay10.commons.exception.DataAccessLayerException;
import com.pay10.commons.user.AcquirerDownTimeConfiguration;


@Service
public class AcquirerDTConfigurationDao extends HibernateAbstractDao {

	public void save(AcquirerDownTimeConfiguration acquirerDTConfigReq) throws DataAccessLayerException {
		super.save(acquirerDTConfigReq);
	}

	public AcquirerDownTimeConfiguration getAcquirerDTConfig(long id) {
		return (AcquirerDownTimeConfiguration) super.find(AcquirerDownTimeConfiguration.class, id);
	}

	public void update(AcquirerDownTimeConfiguration acquirerDTConfigReq) {
		super.saveOrUpdate(acquirerDTConfigReq);
	}

	public long getAcquirerDTConfig(String acquirerName, String paymentType) {
		Session session = HibernateSessionProvider.getSession();
		Transaction tx = session.beginTransaction();
		try {
			long count = (long) session.createQuery(
					"Select count(*) from AcquirerDownTimeConfiguration a where a.acquirerName=:acquirerName and paymentType=:paymentType and status=:status")
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
							"Select count(*) from AcquirerDownTimeConfiguration a where a.acquirerName=:acquirerName "
									+ "and paymentType=:paymentType and status=:status and id=:id")
					.setParameter("acquirerName", acquirerName).setParameter("paymentType", paymentType)
					.setParameter("status", "ACTIVE").setParameter("id", Id).getSingleResult();
			tx.commit();
			return count;
		} finally {
			autoClose(session);
		}
	}

	public long getAcquirerDTConfigByRuleId(long Id) {
		Session session = HibernateSessionProvider.getSession();
		Transaction tx = session.beginTransaction();
		try {
			long count = (long) session.createQuery("Select count(*) from AcquirerDownTimeConfiguration a where id=:id and status=:status")
					.setParameter("id", Id)
					.setParameter("status", "ACTIVE").getSingleResult();
			tx.commit();
			return count;
		} finally {
			autoClose(session);
		}
	}

	public List<AcquirerDownTimeConfiguration> getAllAcquirerDTConfigSearch(String acquirerName,String paymentType) {
		Session session = HibernateSessionProvider.getSession();
		Transaction tx = session.beginTransaction();
		List<AcquirerDownTimeConfiguration> acquirerInfo = new ArrayList<AcquirerDownTimeConfiguration>();


		try {

			String query="FROM AcquirerDownTimeConfiguration WHERE status='ACTIVE'";
			if (!acquirerName.isEmpty()) {
				query=query+" and acquirerName='"+acquirerName+"'";
			}
			if (!paymentType.isEmpty()) {
				query=query+" and paymentType='"+paymentType+"'";
			}
			//System.out.println("aaaaa"+query);

			acquirerInfo = session.createQuery(query).getResultList();
			tx.commit();
			return acquirerInfo;

		} catch (ObjectNotFoundException objectNotFound) {
			handleException(objectNotFound, tx);
		} catch (HibernateException hibernateException) {
			handleException(hibernateException, tx);
		} finally {
			autoClose(session);
		}
		return acquirerInfo;
	}
	public List<AcquirerDownTimeConfiguration> fetchAllAcquirerDTConfigSearch(String acquirerName,String paymentType) {
		Session session = HibernateSessionProvider.getSession();
		Transaction tx = session.beginTransaction();
		List<AcquirerDownTimeConfiguration> acquirerInfo = new ArrayList<AcquirerDownTimeConfiguration>();


		try {

			String query="FROM AcquirerDownTimeConfiguration WHERE ";
			if (!acquirerName.isEmpty()) {
				query=query+" acquirerName='"+acquirerName+"'";
			}
			if (!paymentType.isEmpty()) {
				query=query+" and paymentType='"+paymentType+"'";
			}
			//System.out.println("aaaaa"+query);

			acquirerInfo = session.createQuery(query).getResultList();
			tx.commit();
			return acquirerInfo;

		} catch (ObjectNotFoundException objectNotFound) {
			handleException(objectNotFound, tx);
		} catch (HibernateException hibernateException) {
			handleException(hibernateException, tx);
		} finally {
			autoClose(session);
		}
		return acquirerInfo;
	}


	public List<AcquirerDownTimeConfiguration> getAllAcquirerDTConfig() {
		Session session = HibernateSessionProvider.getSession();
		Transaction tx = session.beginTransaction();
		List<AcquirerDownTimeConfiguration> acquirerInfo = new ArrayList<AcquirerDownTimeConfiguration>();
		try {
			acquirerInfo = session.createQuery("FROM AcquirerDownTimeConfiguration a WHERE status=:status")
					.setParameter("status", "ACTIVE").getResultList();
			tx.commit();
			return acquirerInfo;

		} catch (ObjectNotFoundException objectNotFound) {
			handleException(objectNotFound, tx);
		} catch (HibernateException hibernateException) {
			handleException(hibernateException, tx);
		} finally {
			autoClose(session);
		}
		return acquirerInfo;
	}

	public void updateAcquirerDTCongfigRule(String acquirerRuleId, String updatedDate) {
		Session session = HibernateSessionProvider.getSession();
		Transaction tx = session.beginTransaction();
		try {
			session.createQuery(
					"update AcquirerDownTimeConfiguration a set a.status=:status, updateBy=:updateBy, updatedDate=:updatedDate where a.id=:id")
					.setParameter("status", "INACTIVE").setParameter("updateBy", "ADMIN")
					.setParameter("updatedDate", updatedDate).setParameter("id", Long.valueOf(acquirerRuleId))
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
