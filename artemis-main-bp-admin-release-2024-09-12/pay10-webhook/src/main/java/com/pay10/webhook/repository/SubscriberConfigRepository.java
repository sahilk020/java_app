package com.pay10.webhook.repository;

import com.pay10.commons.dao.HibernateAbstractDao;
import com.pay10.commons.dao.HibernateSessionProvider;
import com.pay10.commons.user.SubscriberConfig;
import org.hibernate.Session;

import org.springframework.stereotype.Repository;

@Repository
public class SubscriberConfigRepository  extends HibernateAbstractDao {


	public void save(SubscriberConfig subscriberConfig)
	{
		super.saveOrUpdate(subscriberConfig);
	}
	
	public SubscriberConfig findBySubscriberId(Long subscriberId)
	{
		Session session = HibernateSessionProvider.getSession();
		try {
			String hql = "FROM SubscriberConfig e where e.subscriberId = :subscriberId";
			SubscriberConfig subscriber = (SubscriberConfig) session.createQuery(hql).setParameter("subscriberId", subscriberId).getSingleResult();
			session.close();
			return subscriber;
		} finally {
			session.close();
		}
	}
	
//	@Query(value = "select sub from SubscriberConfig sub "
//			+ "left join fetch sub.event event "
//			+ "where sub.associationId=?1 and event.eventId=?2")
	public SubscriberConfig findByAssociationIdAndEventId(String associationId,Long eventId)
	{
		Session session = HibernateSessionProvider.getSession();
		try {
			String hql = "FROM SubscriberConfig sub LEFT JOIN FETCH sub.event e where sub.associationId = :associationId and e.eventId= :eventId";
			SubscriberConfig subscriber = (SubscriberConfig) session.createQuery(hql).setParameter("associationId", associationId).setParameter("eventId",eventId).getSingleResult();
			session.close();
			return subscriber;
		} finally {
			session.close();
		}
	}
	
//	@Query(value = "select sub from SubscriberConfig sub "
//			+ "left join fetch sub.event event "
//			+ "where sub.associationId=?1 and event.eventName=?2 and event.eventType=?3")
	public SubscriberConfig findByAssociationIdAndEventDetails(String associationId,String eventName, String type)
	{
		Session session = HibernateSessionProvider.getSession();
		try {
			String hql = "FROM SubscriberConfig sub LEFT JOIN FETCH sub.event e where sub.associationId = :associationId and e.eventName= :eventName and e.eventType= :type";
			SubscriberConfig subscriber = (SubscriberConfig) session.createQuery(hql).setParameter("associationId", associationId).setParameter("eventName",eventName).setParameter("type",type).getSingleResult();
			session.close();
			return subscriber;
		} finally {
			session.close();
		}
	}
}
