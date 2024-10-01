package com.pay10.commons.dao;

import java.util.List;

import org.hibernate.Session;
import org.hibernate.query.Query;
import org.springframework.stereotype.Component;

import com.pay10.commons.payoutEntity.PayoutAcquirer;

@Component
public class AcquirerMappingPoDao extends HibernateAbstractDao {
	
	
	
	public List<PayoutAcquirer> findAll() {
		Session session = HibernateSessionProvider.getSession();
		Query query = session.createQuery("FROM PayoutAcquirer");
		return (List<PayoutAcquirer>) query.getSingleResult();
	}
	
	
	public List<String> findDistinctAcquirer() {
	    Session session = HibernateSessionProvider.getSession();
	    Query query = session.createQuery("SELECT DISTINCT acquirerName FROM PayoutAcquirer");
	    return (List<String>) query.list();
	}
	
	public List<String> findDistinctChannel(String acquirer) {
	    Session session = HibernateSessionProvider.getSession();
	    Query query = session.createQuery("SELECT DISTINCT channel FROM PayoutAcquirer WHERE acquirerName='"+acquirer+"'");
	    return (List<String>) query.list();
	}

	public List<PayoutAcquirer> findMappedString(String acquirer, String channel) {
		Session session = HibernateSessionProvider.getSession();
		Query query = session.createQuery("FROM PayoutAcquirer WHERE acquirerName='"+acquirer+"' AND channel='"+channel+"'");
		return (List<PayoutAcquirer>) query.getResultList();
	}

}
