package com.pay10.commons.user;

import org.hibernate.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.pay10.commons.dao.HibernateAbstractDao;
import com.pay10.commons.dao.HibernateSessionProvider;

@Component("ChannelMasterDao")

public class ChannelMasterDao extends HibernateAbstractDao {
	private static final Logger logger = LoggerFactory.getLogger(AcquirerSchadulardao.class.getName());

	public ChannelMaster getMatchingRule(String name) {
		Session session = HibernateSessionProvider.getSession();
		try {
			return session.createQuery("FROM ChannelMaster where name = :name",
					ChannelMaster.class).setParameter("name", name).setCacheable(true).uniqueResult();
		} catch (Exception e) {
			return null;
		} finally {
			autoClose(session);
		}
	}
}
