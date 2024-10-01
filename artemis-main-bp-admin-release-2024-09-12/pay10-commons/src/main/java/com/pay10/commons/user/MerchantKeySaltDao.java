package com.pay10.commons.user;

import java.util.Date;
import java.util.List;

import com.google.api.services.oauth2.Oauth2;
import com.pay10.commons.dao.HibernateSessionProvider;
import com.pay10.commons.entity.ResetMerchantKey;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.NativeQuery;
import org.hibernate.query.Query;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.pay10.commons.dao.HibernateAbstractDao;
import com.pay10.commons.exception.DataAccessLayerException;

@Component("merchantKeySaltDao")
public class MerchantKeySaltDao extends HibernateAbstractDao {
	private static Logger logger = LoggerFactory.getLogger(MerchantKeySaltDao.class.getName());

	public MerchantKeySaltDao() {
		super();
	}
	
	public void addMerchantInfo(MerchantKeySalt merchantKeySalt) throws DataAccessLayerException {
		super.save(merchantKeySalt);
	}
	
	public MerchantKeySalt find(String payId) throws DataAccessLayerException {
		return (MerchantKeySalt) super.find(MerchantKeySalt.class, payId);
	}
	
	@SuppressWarnings("rawtypes")
	public List findAll() throws DataAccessLayerException {
		return super.findAll(MerchantKeySalt.class);
	}
	
	public boolean checkuser(String payId) {
		MerchantKeySalt merchantKeySalt = new MerchantKeySalt();
		merchantKeySalt = new MerchantKeySaltDao().find(payId);
		if (null != merchantKeySalt) {
			return true;
		} else {
			return false;
		}
	}

	public void saveOrUpdate(MerchantKeySalt merchantKeySalt){
		super.saveOrUpdate(merchantKeySalt);
	}
	public void updateMerchantSaltEncryptionKey(MerchantKeySalt merchantKeySalt){
		Session session= HibernateSessionProvider.getSession();
		Transaction tx= session.beginTransaction();
		try{
			NativeQuery nativeQuery= session.createNativeQuery("Update merchantkeysalt set encryptionKey=:encyKey,salt=:salt, keySalt=:keySalt, updatedBy=:updatedBy, updatedOn=:updatedOn where payId=:payId")
					.setParameter("encyKey",merchantKeySalt.getEncryptionKey())
					.setParameter("salt",merchantKeySalt.getSalt())
					.setParameter("keySalt",merchantKeySalt.getKeySalt())
					.setParameter("updatedBy",merchantKeySalt.getUpdatedBy())
					.setParameter("updatedOn",merchantKeySalt.getUpdatedOn())
					.setParameter("payId",merchantKeySalt.getPayId());
			int count=nativeQuery.executeUpdate();
			if(count>0){
				logger.info("Merchant Key Salt updated Successfully for: "+merchantKeySalt.getPayId()+" With Count"+count+" With Encryption Key"+merchantKeySalt.getEncryptionKey());
			}
			else{
				logger.info("Merchant Key Salt updated Successfully for: "+merchantKeySalt.getPayId()+" With Count"+count+" With Encryption Key"+merchantKeySalt.getEncryptionKey());
			}
			tx.commit();
		}catch (HibernateException h){
			logger.info("Update Merhcant Key Salt Exception: "+h);
		}
		finally {
			autoClose(session);
		}
	}

	public Boolean checkMerchantSaltEncryptionKey(MerchantKeySalt merchantKeySalt){
		List<MerchantKeySalt> merchantKeySalt1=null;
 		try(Session session= HibernateSessionProvider.getSession()){
			Query query= session.createQuery("FROM MerchantKeySalt WHERE encryptionKey=:encryptionKey and salt=:salt and keySalt=:keySalt and payId=:payId")
					.setParameter("encryptionKey",merchantKeySalt.getEncryptionKey())
					.setParameter("salt",merchantKeySalt.getSalt())
					.setParameter("keySalt",merchantKeySalt.getKeySalt())
	 				.setParameter("payId",merchantKeySalt.getPayId());
			merchantKeySalt1=query.getResultList();
			if(!merchantKeySalt1.isEmpty()) {
				return true;
			 }
			else{
				return false;
		 	}
	 	}

	}
}
