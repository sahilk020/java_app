package com.pay10.commons.user;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

import org.hibernate.HibernateException;
import org.hibernate.ObjectNotFoundException;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.pay10.commons.dao.DataAccessObject;
import com.pay10.commons.dao.HibernateAbstractDao;
import com.pay10.commons.dao.HibernateSessionProvider;
import com.pay10.commons.exception.DataAccessLayerException;

@Component("dashboardPreferenceSetDao")
public class DashboardPreferenceSetDao extends HibernateAbstractDao {



	public DashboardPreferenceSetDao() {
		super();
	}


	private Connection getConnection() throws SQLException {
		return DataAccessObject.getBasicConnection();
	}

	public void create(DashboardPreferenceSet dps) throws DataAccessLayerException {
		super.save(dps);
	}

	public void saveOrUpdate(DashboardPreferenceSet dps) throws DataAccessLayerException {
		super.saveOrUpdate(dps);
	}

	public void delete(DashboardPreferenceSet dps) throws DataAccessLayerException {
		super.delete(dps);
	}

	public DashboardPreferenceSet find(String invoiceConstant) throws DataAccessLayerException {
		return (DashboardPreferenceSet) super.find(DashboardPreferenceSet.class, invoiceConstant);
	}

	@SuppressWarnings("finally")
	public DashboardPreferenceSet getDashboardPreferenceSetByEmailId(String emailId) {
		DashboardPreferenceSet dpsResponse = null;
		Session session = HibernateSessionProvider.getSession();
		Transaction tx = session.beginTransaction();

		try {
			List<DashboardPreferenceSet> dps = session
					.createQuery("from DashboardPreferenceSet dps where dps.emailId = :emailId")
					.setParameter("emailId", emailId).getResultList();
			if (dps.size() != 0) {
				dpsResponse = dps.get(dps.size() - 1);
			}
			tx.commit();
			return dpsResponse;
		} catch (ObjectNotFoundException objectNotFound) {
			handleException(objectNotFound, tx);
		} catch (HibernateException hibernateException) {
			handleException(hibernateException, tx);
		} catch (Exception e) {
			return null;
		} finally {
			autoClose(session);
		}
		return dpsResponse;
	}

}
