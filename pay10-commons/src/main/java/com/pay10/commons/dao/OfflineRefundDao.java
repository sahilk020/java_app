package com.pay10.commons.dao;

import java.util.List;

import org.hibernate.Session;
import org.hibernate.Transaction;
import org.springframework.stereotype.Service;

import com.pay10.commons.entity.IRCTCRefundFile;
import com.pay10.commons.exception.DataAccessLayerException;
import com.pay10.commons.user.OfflineRefund;
	
@Service
public class OfflineRefundDao extends HibernateAbstractDao {
	
	public void save(OfflineRefund offlineRefund) throws DataAccessLayerException {
		super.save(offlineRefund);
	}

	public OfflineRefund getOfflineRefund(long id) {
		return (OfflineRefund) super.find(OfflineRefund.class, id);
	}

	public void update(OfflineRefund offlineRefund) {
		super.saveOrUpdate(offlineRefund);
	}
	
	@SuppressWarnings("unchecked")
	public List<OfflineRefund> getFilesByDate(String date) {
		Session session = HibernateSessionProvider.getSession();
		Transaction tx = session.beginTransaction();
		String query = "FROM OfflineRefund O where O.date=:date";
		List<OfflineRefund> offlineRefundInfo = session.createQuery(query).setParameter("date", date).getResultList();
		tx.commit();
		return offlineRefundInfo;
	}
	
	@SuppressWarnings("unchecked")
	public List<IRCTCRefundFile> getIRCTCRefundFile(String date) {
		Session session = HibernateSessionProvider.getSession();
		Transaction tx = session.beginTransaction();
		String query = "FROM IRCTCRefundFile O where O.date=:date";
		List<IRCTCRefundFile> irctcRefundFile = session.createQuery(query).setParameter("date", date).getResultList();
		tx.commit();
		return irctcRefundFile;
	}
}
