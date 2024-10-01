package com.pay10.commons.dao;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.HibernateException;
import org.hibernate.ObjectNotFoundException;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.pay10.commons.api.EmailControllerServiceProvider;
import com.pay10.commons.exception.DataAccessLayerException;
import com.pay10.commons.user.PendingMappingRequest;

@Component
public class PendingMappingRequestDao extends HibernateAbstractDao {
	
	@Autowired
	EmailControllerServiceProvider emailControllerServiceProvider; 

	public void create(PendingMappingRequest pendingMappingRequest) throws DataAccessLayerException {
		super.save(pendingMappingRequest);
		
	}


	@SuppressWarnings("unchecked")
	public List<PendingMappingRequest> getPendingMappingRequest() {
		List<PendingMappingRequest> pendingMappingRequestList = new ArrayList<PendingMappingRequest>();
		pendingMappingRequestList = null;
		Session session = HibernateSessionProvider.getSession();
		Transaction tx = session.beginTransaction();
		
		try {
			
			pendingMappingRequestList = session.createQuery("from PendingMappingRequest PMR where PMR.status='PENDING'")
					.setCacheable(true)
					.getResultList();
			tx.commit();
			return pendingMappingRequestList;
		} catch (ObjectNotFoundException objectNotFound) {
			handleException(objectNotFound,tx);
		} catch (HibernateException hibernateException) {
			handleException(hibernateException,tx);
		} finally {
			autoClose(session);
		}
		return pendingMappingRequestList;
	}
	
	
	public PendingMappingRequest findPendingMappingRequest(String merchantEmailId , String acquirer) {
		PendingMappingRequest pendingMappingRequest = null;
		Session session = HibernateSessionProvider.getSession();
		Transaction tx = session.beginTransaction();
		try {
			pendingMappingRequest = (PendingMappingRequest) session
					.createQuery(
							"from PendingMappingRequest PMR where PMR.merchantEmailId = :merchantEmailId and PMR.acquirer = :acquirer and PMR.status = 'PENDING' ")
					.setParameter("merchantEmailId", merchantEmailId)
					.setParameter("acquirer", acquirer)
					.setCacheable(true).uniqueResult();
			tx.commit();
		} catch (ObjectNotFoundException objectNotFound) {
			handleException(objectNotFound,tx);
		} catch (HibernateException hibernateException) {
			handleException(hibernateException,tx);
		} finally {
			autoClose(session);
			// return responseUser;
		}
		return pendingMappingRequest;

	}
	
	public PendingMappingRequest findActiveMappingRequest(String merchantEmailId , String acquirer) {
		PendingMappingRequest pendingMappingRequest = null;
		Session session = HibernateSessionProvider.getSession();
		Transaction tx = session.beginTransaction();
		try {
			pendingMappingRequest = (PendingMappingRequest) session
					.createQuery(
							"from PendingMappingRequest PMR where PMR.merchantEmailId = :merchantEmailId and PMR.acquirer = :acquirer and PMR.status = 'ACTIVE' ")
					.setParameter("merchantEmailId", merchantEmailId)
					.setParameter("acquirer", acquirer)
					.setCacheable(true).uniqueResult();
			tx.commit();
		} catch (ObjectNotFoundException objectNotFound) {
			handleException(objectNotFound,tx);
		} catch (HibernateException hibernateException) {
			handleException(hibernateException,tx);
		} finally {
			autoClose(session);
			// return responseUser;
		}
		return pendingMappingRequest;

	}

	@SuppressWarnings("unchecked")
	public String findActiveMappingByEmailId(String merchantEmailId) {
		List<PendingMappingRequest> activeMappingList;
		StringBuilder currencySet = new StringBuilder();
		Session session = HibernateSessionProvider.getSession();
		Transaction tx = session.beginTransaction();
		try {
			activeMappingList = session.createQuery("from PendingMappingRequest PMR where PMR.merchantEmailId = :merchantEmailId and PMR.status = 'ACTIVE' ")
					.setParameter("merchantEmailId", merchantEmailId)
					.setCacheable(true)
					.getResultList();
			tx.commit();

			// reformat to this json format [{},{}]
			for (PendingMappingRequest pendingMappingRequest : activeMappingList) {
				if (currencySet.length() != 0) {
					currencySet.append(",");
				}
				String accountCurrencySet = pendingMappingRequest.getAccountCurrencySet();
				if (accountCurrencySet.startsWith("[") && accountCurrencySet.endsWith("]")) {
					accountCurrencySet = accountCurrencySet.substring(1, accountCurrencySet.length() - 1);
				}
				currencySet.append(accountCurrencySet);
			}
			return String.format("[%s]", currencySet);
		} catch (HibernateException hibernateException) {
			handleException(hibernateException, tx);
		} finally {
			autoClose(session);
		}
		return null;
	}
}