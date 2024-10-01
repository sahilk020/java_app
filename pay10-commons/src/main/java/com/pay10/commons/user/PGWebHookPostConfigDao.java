package com.pay10.commons.user;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.google.gson.Gson;
import com.pay10.commons.dao.HibernateAbstractDao;
import com.pay10.commons.dao.HibernateSessionProvider;
import com.pay10.commons.entity.PGWebHookPostConfigURL;

@Component
public class PGWebHookPostConfigDao extends HibernateAbstractDao {
	private static Logger logger = LoggerFactory.getLogger(PGWebHookPostConfigDao.class.getName());

//	@SuppressWarnings("unchecked")
//	public List<PGWebHookPostEntity> fetchPGWebHookPostConfig() {
//		Session session = HibernateSessionProvider.getSession();
//		List<PGWebHookPostEntity> pgWebHookPostEntityEntities = new ArrayList<>();
//		try {
//			pgWebHookPostEntityEntities = session.createQuery("from PGWebHookPostEntity").setCacheable(true)
//					.getResultList();
//
//		} catch (HibernateException exception) {
//			logger.info("Exception in fetchPGWebHookPostConfigByPayId : " + exception);
//			exception.printStackTrace();
//		} finally {
//			autoClose(session);
//		}
//		logger.info("Fething Details : " + new Gson().toJson(pgWebHookPostEntityEntities));
//		return pgWebHookPostEntityEntities;
//	}
	
//	@SuppressWarnings("unchecked")
//	public List<PGWebHookPostConfigURL> fetchPGWebHookPostConfigData(String wehbookType) {
//		Session session = HibernateSessionProvider.getSession();
//		List<PGWebHookPostConfigURL> pgWebHookPostConfigURL = new ArrayList<>();
//		try {
//			pgWebHookPostConfigURL = session.createQuery("from PGWebHookPostConfigURL p where p.webhookType=:webhookType and p.status=:status")
//					.setParameter("webhookType", wehbookType)
//					.setParameter("status", "ACTIVE").setCacheable(true).getResultList();
//
//		} catch (HibernateException exception) {
//			logger.info("Exception in fetchPGWebHookPostConfigByPayId : " + exception);
//			exception.printStackTrace();
//		} finally {
//			autoClose(session);
//		}
//		logger.info("fetchPGWebHookPostConfigData, Fething Details : " + new Gson().toJson(pgWebHookPostConfigURL));
//		return pgWebHookPostConfigURL;
//	}
//
//	public PGWebHookPostConfigURL fetchPGWebHookPostConfigURLByPayId(String payId, String wehbookType) {
//		Session session = HibernateSessionProvider.getSession();
//		PGWebHookPostConfigURL pgWebHookPostConfigURL = null;
//		try {
//			pgWebHookPostConfigURL = (PGWebHookPostConfigURL) session
//					.createQuery("from PGWebHookPostConfigURL p where p.payId=:payId and p.webhookType=:webhookType and p.status=:status")
//					.setParameter("payId", payId)
//					.setParameter("webhookType", wehbookType)
//					.setParameter("status", "ACTIVE").setCacheable(true)
//					.getSingleResult();
//
//		} catch (HibernateException exception) {
//			logger.info("Exception in fetchPGWebHookPostConfigURLByPayId : " + exception);
//			exception.printStackTrace();
//		} finally {
//			autoClose(session);
//		}
//		logger.info(
//				"Fething Details fetchPGWebHookPostConfigURLByPayId : " + new Gson().toJson(pgWebHookPostConfigURL));
//		return pgWebHookPostConfigURL;
//	}
	
	@SuppressWarnings("unchecked")
	 public List<PGWebHookPostConfigURL> fetchPGWebHookPostConfigData(String webhookType) {
	      Session session = HibernateSessionProvider.getSession();
	      List<PGWebHookPostConfigURL> pgWebHookPostConfigURL = new ArrayList<PGWebHookPostConfigURL>();

	      try {
	         pgWebHookPostConfigURL = session.createQuery("from PGWebHookPostConfigURL p where p.webhookType=:webhookType and p.status=:status").setParameter("webhookType", webhookType).setParameter("status", "ACTIVE").setCacheable(true).getResultList();
	      } catch (HibernateException var8) {
	         logger.info("Exception in fetchPGWebHookPostConfigByPayId : " + var8);
	         var8.printStackTrace();
	      } finally {
	         autoClose(session);
	      }

	      logger.info("fetchPGWebHookPostConfigData, Fething Details : " + (new Gson()).toJson(pgWebHookPostConfigURL));
	      return pgWebHookPostConfigURL;
	   }

	   public PGWebHookPostConfigURL fetchPGWebHookPostConfigURLByPayId(String payId, String webhookType) {
	      Session session = HibernateSessionProvider.getSession();
	      PGWebHookPostConfigURL pgWebHookPostConfigURL = null;

	      try {
	         pgWebHookPostConfigURL = (PGWebHookPostConfigURL)session.createQuery("from PGWebHookPostConfigURL p where p.payId=:payId and p.webhookType=:webhookType and p.status=:status").setParameter("payId", payId).setParameter("webhookType", webhookType).setParameter("status", "ACTIVE").setCacheable(true).getSingleResult();
	      } catch (HibernateException var9) {
	         logger.info("Exception in fetchPGWebHookPostConfigURLByPayId : " + var9);
	         var9.printStackTrace();
	      } finally {
	         autoClose(session);
	      }

	      logger.info("Fething Details fetchPGWebHookPostConfigURLByPayId : " + (new Gson()).toJson(pgWebHookPostConfigURL));
	      return pgWebHookPostConfigURL;
	   }

}
