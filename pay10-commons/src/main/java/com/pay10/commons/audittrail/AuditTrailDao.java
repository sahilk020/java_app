package com.pay10.commons.audittrail;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.Session;
import org.springframework.stereotype.Service;

import com.pay10.commons.dao.HibernateAbstractDao;
import com.pay10.commons.dao.HibernateSessionProvider;
import com.pay10.commons.exception.DataAccessLayerException;

/**
 * @author Jay gajera
 *
 */
@Service
public class AuditTrailDao extends HibernateAbstractDao {

	public void save(AuditTrail auditTrail) throws DataAccessLayerException {
		super.save(auditTrail);
		//System.out.println("........"+auditTrail);
	}

	@SuppressWarnings("unchecked")
	public List<AuditTrail> getAuditTrails() {
		return super.findAll(AuditTrail.class);
	}

	public long getCountByFilter(String emailId, String fromDate, String toDate) {
		Session session = HibernateSessionProvider.getSession();
		try {
			String query = "SELECT COUNT(*) FROM AuditTrail A where STR_TO_DATE(A.timestamp, '%d-%m-%Y') between STR_TO_DATE(:START, '%d-%m-%Y') AND STR_TO_DATE(:END, '%d-%m-%Y')";
			if (StringUtils.isNotBlank(emailId)) {
				query = StringUtils.join(query, " AND A.emailId='", emailId, "'");
			}

			return (long) session.createQuery(query).setParameter("START", fromDate).setParameter("END", toDate)
					.getSingleResult();
		} finally {
			HibernateSessionProvider.closeSession(session);
		}
	}

	@SuppressWarnings("unchecked")
	public List<AuditTrail> getByFilter(String emailId, String fromDate, String toDate, int start, int length) {
		Session session = HibernateSessionProvider.getSession();
		try {
			String query = "FROM AuditTrail A where STR_TO_DATE(A.timestamp, '%d-%m-%Y') between STR_TO_DATE(:START, '%d-%m-%Y') AND STR_TO_DATE(:END, '%d-%m-%Y')";
			if (StringUtils.isNotBlank(emailId)) {
				query = StringUtils.join(query, " AND A.emailId='", emailId, "'");
			}
			return session.createQuery(query).setParameter("START", fromDate).setParameter("END", toDate)
					.setFirstResult(start).setMaxResults(length).getResultList();
		} finally {
			HibernateSessionProvider.closeSession(session);
		}
	}

	public AuditTrail getAuditTrail(long id) {
		return (AuditTrail) super.find(AuditTrail.class, id);
	}

	public void update(AuditTrail auditTrail) {
		super.saveOrUpdate(auditTrail);
	}

	public void delete(long id) {
		super.delete(getAuditTrail(id));
	}
}
