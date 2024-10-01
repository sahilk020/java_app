package com.crmws.dao.impl;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.Session;
import org.hibernate.Transaction;
import org.springframework.stereotype.Component;

import com.pay10.commons.dao.HibernateAbstractDao;
import com.pay10.commons.dao.HibernateSessionProvider;
import com.pay10.commons.entity.PGWebHookPostConfigURL;
import com.pay10.commons.exception.DataAccessLayerException;
import com.pay10.commons.repository.DMSEntity;
import com.pay10.commons.user.Merchants;
import com.pay10.commons.user.UserType;
import com.pay10.commons.util.UserStatusType;

@Component("webhookDao")
public class WebhookDaoImpl extends HibernateAbstractDao {

	public void save(PGWebHookPostConfigURL entity) throws DataAccessLayerException {
		super.save(entity);
	}

	public void update(PGWebHookPostConfigURL entity) throws DataAccessLayerException {
		super.saveOrUpdate(entity);
	}

	@SuppressWarnings("unchecked")
	public List<PGWebHookPostConfigURL> findByPayId(String payId) {
		List<PGWebHookPostConfigURL> PGWebHookPostConfigURLList = new ArrayList<PGWebHookPostConfigURL>();
		Session session = HibernateSessionProvider.getSession();

		try {
			PGWebHookPostConfigURLList = session
					.createQuery("FROM PGWebHookPostConfigURL WHERE payId=:payId ORDER BY webhookType")
					.setParameter("payId", payId).setCacheable(true).getResultList();

		} finally {
			autoClose(session);
		}
		return PGWebHookPostConfigURLList;
	}

	@SuppressWarnings("unchecked")
	public PGWebHookPostConfigURL findById(Long id) {
		PGWebHookPostConfigURL PGWebHookPostConfigURLList = new PGWebHookPostConfigURL();
		Session session = HibernateSessionProvider.getSession();

		try {
			PGWebHookPostConfigURLList = (PGWebHookPostConfigURL) session
					.createQuery("FROM PGWebHookPostConfigURL WHERE id=:id").setParameter("id", id).setCacheable(true)
					.uniqueResult();

		} finally {
			autoClose(session);
		}
		return PGWebHookPostConfigURLList;
	}

	@SuppressWarnings("unchecked")
	public List<PGWebHookPostConfigURL> findByProperty(String payId, String webhookUrl, String webhookType) {
		List<PGWebHookPostConfigURL> PGWebHookPostConfigURLList = new ArrayList<PGWebHookPostConfigURL>();
		Session session = HibernateSessionProvider.getSession();

		try {
			PGWebHookPostConfigURLList = session.createQuery(
					"FROM PGWebHookPostConfigURL WHERE payId=:payId  and webhookUrl=:webhookUrl and webhookType=:webhookType")
					.setParameter("payId", payId).setParameter("webhookType", webhookType)
					.setParameter("webhookUrl", webhookUrl).setCacheable(true).getResultList();

		} finally {
			autoClose(session);
		}
		return PGWebHookPostConfigURLList;
	}

}
