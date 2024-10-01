package com.pay10.commons.dao;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.hibernate.HibernateException;
import org.hibernate.ObjectNotFoundException;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.springframework.stereotype.Service;

import com.pay10.commons.exception.DataAccessLayerException;
import com.pay10.commons.user.SettlementDataUpdate;

@Service
public class SettlementDataUpdateDao extends HibernateAbstractDao {

	public void create(SettlementDataUpdate SettlementDataUpdate) throws DataAccessLayerException {
		super.save(SettlementDataUpdate);
	}

	public SettlementDataUpdate findLastUpload() {
		SettlementDataUpdate SettlementDataUpdate = null;
		Session session = HibernateSessionProvider.getSession();
		Transaction tx = session.beginTransaction();
		try {
			SettlementDataUpdate = (SettlementDataUpdate) session
					.createQuery("from SettlementDataUpdate SDU where SDU.id = (select max(id) from SettlementDataUpdate )")
					.setCacheable(true).uniqueResult();
			tx.commit();
		} catch (ObjectNotFoundException objectNotFound) {
			handleException(objectNotFound,tx);
		} catch (HibernateException hibernateException) {
			handleException(hibernateException,tx);
		} finally {
			autoClose(session);
		}
		return SettlementDataUpdate;

	}
	
	
	@SuppressWarnings("unchecked")
	public List<SettlementDataUpdate> findAll() {
		List<SettlementDataUpdate> SettlementDataUpdateList = new ArrayList<SettlementDataUpdate>();
		SettlementDataUpdateList = null;
		Session session = HibernateSessionProvider.getSession();
		Transaction tx = session.beginTransaction();
		try {

			SettlementDataUpdateList = session.createQuery("from SettlementDataUpdate").setCacheable(true)
					.getResultList();
			tx.commit();
		} catch (ObjectNotFoundException objectNotFound) {
			handleException(objectNotFound,tx);
		} catch (HibernateException hibernateException) {
			handleException(hibernateException,tx);
		} finally {
			autoClose(session);
		}
		
		if (SettlementDataUpdateList.size()<2) {
			return SettlementDataUpdateList;
		}
		
		Comparator<SettlementDataUpdate> comp = (SettlementDataUpdate a, SettlementDataUpdate b) -> {

			if (a.getCreatedDate().compareTo(b.getCreatedDate()) > 0) {
				return -1;
			} else if (a.getCreatedDate().compareTo(b.getCreatedDate()) < 0) {
				return 1;
			} else {
				return 0;
			}
		};

		Collections.sort(SettlementDataUpdateList, comp);
		return SettlementDataUpdateList;
	}
	
	

}
