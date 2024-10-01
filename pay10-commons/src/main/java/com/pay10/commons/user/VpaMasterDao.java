package com.pay10.commons.user;

import org.hibernate.HibernateException;
import org.hibernate.ObjectNotFoundException;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.pay10.commons.dao.HibernateAbstractDao;
import com.pay10.commons.dao.HibernateSessionProvider;

@Component
public class VpaMasterDao extends HibernateAbstractDao {
	private static Logger logger = LoggerFactory.getLogger(VpaMasterDao.class.getName());

	@SuppressWarnings("null")
	public VpaMaster getDomainName(String domainName) {
		logger.info("Request Received For getting PSP Name For Domain Name :: " + domainName);
		VpaMaster vpaMaster = null;
		Session session = HibernateSessionProvider.getSession();
		Transaction tx = session.beginTransaction();
		try {
			vpaMaster = (VpaMaster) session
					.createQuery("FROM VpaMaster where  domainName=:domainName")
					.setParameter("domainName", domainName).setCacheable(true).uniqueResult();
			tx.commit();
		} catch (ObjectNotFoundException objectNotFound) {
			handleException(objectNotFound, tx);
		} catch (HibernateException hibernateException) {
			handleException(hibernateException, tx);
		} finally {
			autoClose(session);
		}

		if(null == vpaMaster) {
			vpaMaster = new VpaMaster();
			vpaMaster.setPspName("Others");
		}
		return vpaMaster;
	}

	public static void main(String[] args) {
		VpaMaster vpaMaster = new VpaMasterDao().getDomainName("@".concat("ybl2"));
		System.out.println("vpaMaster "+vpaMaster);
		String pspName = vpaMaster.getPspName();
		System.out.println("pspName "+pspName);
		System.out.println("vpaMaster:"+vpaMaster == null ?  "Others" : vpaMaster.getPspName());

	}
}
