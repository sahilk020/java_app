package com.pay10.commons.user;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.pay10.commons.dao.HibernateAbstractDao;
import com.pay10.commons.dao.HibernateSessionProvider;


@Component
public class PayoutBankCodeconfigurationDao extends HibernateAbstractDao {
	private static Logger logger = LoggerFactory.getLogger(PayoutTdrSettingDao.class.getName());

	public PayoutBankCodeconfiguration getCurrencyNamebyCode(String pgbankcode) {
		Session session = HibernateSessionProvider.getSession();
		String query = "FROM PayoutBankCodeconfiguration where pgbankcode=" + pgbankcode;
		try {

			logger.info(query);
			PayoutBankCodeconfiguration codecurr = (PayoutBankCodeconfiguration) session.createQuery(query)
					.setCacheable(true).uniqueResult();

			return codecurr;

		} finally {
			autoClose(session);
		}
	}

	public boolean getbankcodeCheck(String pgbankcode, String currencyCode) {
		boolean flag = false;
		Session session = HibernateSessionProvider.getSession();
		String query = "FROM PayoutBankCodeconfiguration where pgbankcode=" + pgbankcode + " and currency="
				+ currencyCode;
		logger.info(query);
		try {

			logger.info(query);
			List<PayoutBankCodeconfiguration> codecurr = (List<PayoutBankCodeconfiguration>) session.createQuery(query)
					.setCacheable(true).list();

			if (codecurr.size() > 0)
				flag = true;
			return flag;

		} catch (Exception e) {
			e.printStackTrace();
			return flag;

		} finally {
			autoClose(session);

		}

	}
	
	
	@SuppressWarnings("unchecked")
	public List<PayoutBankCodeconfiguration> getDetailForUI() {
		Session session = HibernateSessionProvider.getSession();
		String query = "FROM PayoutBankCodeconfiguration";
		List<PayoutBankCodeconfiguration> list=new ArrayList<>();
		try {

			logger.info("Fetch Detail for front : " + query);
			list =  session.createQuery(query).setCacheable(true).getResultList();

			return list;

		} finally {
			autoClose(session);
		}
	}
}
