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
import com.pay10.commons.util.FieldType;
import com.pay10.commons.util.Fields;

@Component
public class VpaValidateMidDao   extends HibernateAbstractDao {
	private static Logger logger = LoggerFactory.getLogger(VpaValidateMidDao.class.getName());

	
	
	@SuppressWarnings("null")
	public void getMidByAcquirer(Fields fields,String acquirer) {
		logger.info("Find  the for acquirer  " + acquirer);
VpaValidateMid vpaMaster = null;
		Session session = HibernateSessionProvider.getSession();
		Transaction tx = session.beginTransaction();
		try {
			vpaMaster = (VpaValidateMid) session
					.createQuery("FROM VpaValidateMid where  acquirer=:acquirer")
					.setParameter("acquirer", acquirer).setCacheable(true).uniqueResult();
			tx.commit();
		} catch (ObjectNotFoundException objectNotFound) {
			handleException(objectNotFound, tx);
		} catch (HibernateException hibernateException) {
			handleException(hibernateException, tx);
		} finally {
			autoClose(session);
		}

		
		fields.put(FieldType.MERCHANT_ID.getName(),vpaMaster.getMid());
		fields.put(FieldType.TXN_KEY.getName(),vpaMaster.getTxnKey());
		fields.put(FieldType.ADF1.getName(),vpaMaster.getAdf1());
		fields.put(FieldType.ADF2.getName(),vpaMaster.getAdf2());
		fields.put(FieldType.ADF3.getName(),vpaMaster.getAdf3());
		fields.put(FieldType.ADF4.getName(),vpaMaster.getAdf4());
		fields.put(FieldType.ADF5.getName(),vpaMaster.getAdf5());
		fields.put(FieldType.ADF6.getName(),vpaMaster.getAdf6());
		fields.put(FieldType.ADF7.getName(),vpaMaster.getAdf7());
		fields.put(FieldType.ADF8.getName(),vpaMaster.getAdf8());
		fields.put(FieldType.ADF9.getName(),vpaMaster.getAdf9());
		fields.put(FieldType.ADF10.getName(),vpaMaster.getAdf10());
		fields.put(FieldType.ADF11.getName(),vpaMaster.getAdf11());
		

		
	}
}
