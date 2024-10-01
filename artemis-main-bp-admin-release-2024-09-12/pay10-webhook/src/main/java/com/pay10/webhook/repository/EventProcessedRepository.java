package com.pay10.webhook.repository;

import java.util.List;

import com.pay10.commons.dao.HibernateAbstractDao;
import com.pay10.commons.dao.HibernateSessionProvider;
import com.pay10.commons.user.EventProcessedStatus;
import org.hibernate.Session;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public class EventProcessedRepository extends HibernateAbstractDao {


	public void save(EventProcessedStatus eventProcessedStatus)
	{
		super.save(eventProcessedStatus);
	}
	
//	@Query(value = "select event from EventProcessedStatus event "
//			+ "left join fetch event.eventpayload payload "
//			+ "where event.status=?1")
public List<EventProcessedStatus> findByStatus(String status)
	{
		Session session = HibernateSessionProvider.getSession();
		try {
			String hql = "FROM EventProcessedStatus event "
					+ "LEFT JOIN FETCH event.eventpayload payload "
					+ "where event.status=:status";
			List<EventProcessedStatus> eventProcessedStatus = session.createQuery(hql).setParameter("status", status).getResultList();
			session.close();
			return eventProcessedStatus;
		} finally {
			session.close();
		}
	}
	
//	@Query(value = "FROM EventProcessedStatus event "
//			+ "LEFT JOIN FETCH event.eventpayload payload "
//			+ "where event.eventProcessedId=:eventProcessedId")
	public EventProcessedStatus findByEventProcessedId(Long eventProcessId)
	{
		Session session = HibernateSessionProvider.getSession();
		try {
			String hql = "FROM EventProcessedStatus event "
					+ "LEFT JOIN FETCH event.eventpayload payload "
					+ "where event.eventProcessedId= :eventProcessedId";
			EventProcessedStatus eventPayload = (EventProcessedStatus) session.createQuery(hql).setParameter("eventProcessedId", eventProcessId).getSingleResult();
			session.close();
			return eventPayload;
		} finally {
			session.close();
		}
	}
	
	
//	@Query(value = "select event from EventProcessedStatus event "
//			+ "left join fetch event.eventpayload payload "
//			+ "where event.subscriberId=?1")
public List<EventProcessedStatus> findBySubscriberId(Long subscriberId)
	{
		Session session = HibernateSessionProvider.getSession();
		try {
			String hql = "FROM EventProcessedStatus event "
					+ "LEFT JOIN FETCH event.eventpayload payload "
					+ "where event.subscriberId= :subscriberId";
			List<EventProcessedStatus> eventProcessedStatus = session.createQuery(hql).setParameter("subscriberId", subscriberId).getResultList();
			session.close();
			return eventProcessedStatus;
		} finally {
			session.close();
		}
	}
	
//	@Modifying
//	@Query("UPDATE EventProcessedStatus event SET event.status = :status  WHERE event.eventProcessedId = :eventProcessedId")

	public void updateStatusById(String status,Long eventProcessedId)
	{
		Session session = HibernateSessionProvider.getSession();
		try {
			String hql = "UPDATE EventProcessedStatus event SET event.status = :status  WHERE event.eventProcessedId = :eventProcessedId";
			session.createQuery(hql).setParameter("status",status).setParameter("eventProcessedId",eventProcessedId).executeUpdate();
			session.close();
		} finally {
			session.close();
		}
	}

}
