package com.pay10.webhook.repository;

import java.util.List;


import com.pay10.commons.dao.HibernateAbstractDao;
import com.pay10.commons.dao.HibernateSessionProvider;
import com.pay10.commons.user.EventPayload;
import com.pay10.commons.user.EventProcessedStatus;
import org.hibernate.Session;

import org.hibernate.Transaction;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@Transactional
public class EventPayloadRepository extends HibernateAbstractDao {

	public void save(EventPayload eventPayload)
	{
		super.save(eventPayload);
	}


	public List<EventPayload> findByStatus(boolean status)
	{
		Session session = HibernateSessionProvider.getSession();
		try {
			String hql = "FROM EventPayload e where e.status = :status";
			List<EventPayload> eventPayload = session.createQuery(hql).setParameter("status", status).getResultList();
			session.close();
			return eventPayload;
		} finally {
			session.close();
		}
	}

	public EventPayload findByEventPayloadId(Long eventPayloadId)
	{
		Session session = HibernateSessionProvider.getSession();
		try {
			String hql = "FROM EventPayload e where e.eventPayloadId = :eventPayloadId";
			EventPayload eventPayload = (EventPayload) session.createQuery(hql).setParameter("eventPayloadId", eventPayloadId).getSingleResult();
			session.close();
			return eventPayload;
		} finally {
			session.close();
		}
	}

	public EventPayload findFirstByOrderByEventTimeDesc()
	{
		Session session = HibernateSessionProvider.getSession();
		try {
			String hql = "FROM EventPayload e ORDER BY eventTime DESC";
			EventPayload eventPayload = (EventPayload) session.createQuery(hql).setMaxResults(1).uniqueResult();
			session.close();
			return eventPayload;
		} finally {
			session.close();
		}
	}
	

	//@Query("UPDATE EventPayload event SET event.status = :status  WHERE event.eventPayloadId = :eventPayloadId")*
	public void updateStatusById(boolean status,Long eventPayloadId)
	{

		Session session = HibernateSessionProvider.getSession();
		Transaction txn = session.beginTransaction();
		try {
			String hql = "UPDATE EventPayload event SET event.status = :status  WHERE event.eventPayloadId = :eventPayloadId";
			session.createQuery(hql).setParameter("status",status).setParameter("eventPayloadId",eventPayloadId).executeUpdate();
			txn.commit();
			session.close();
		} finally {
			session.close();
		}
	}

}
