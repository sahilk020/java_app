package com.pay10.webhook.repository;

import com.pay10.commons.dao.HibernateAbstractDao;
import com.pay10.commons.dao.HibernateSessionProvider;
import com.pay10.commons.user.Events;
import org.hibernate.Session;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class EventsRepository extends HibernateAbstractDao {

	public Events findByEventId(Long eventId)
	{
		Session session = HibernateSessionProvider.getSession();
		try {
			String hql = "FROM Events e where e.eventId = :eventId";
			Events eventPayload = (Events) session.createQuery(hql).setParameter("eventId", eventId).getSingleResult();
			session.close();
			return eventPayload;
		} finally {
			session.close();
		}
	}

	public List<Events> findAll(){
		return super.findAll(Events.class);
	}
}
