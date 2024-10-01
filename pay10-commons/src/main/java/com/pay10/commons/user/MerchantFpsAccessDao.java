package com.pay10.commons.user;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

import org.hibernate.HibernateException;
import org.hibernate.ObjectNotFoundException;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.springframework.stereotype.Component;

import com.pay10.commons.dao.DataAccessObject;
import com.pay10.commons.dao.HibernateAbstractDao;
import com.pay10.commons.dao.HibernateSessionProvider;
import com.pay10.commons.exception.DataAccessLayerException;

@Component("merchantFpsAccessDao")
public class MerchantFpsAccessDao  extends HibernateAbstractDao  {

	
	public MerchantFpsAccessDao() {
		super();
	}

	private Connection getConnection() throws SQLException {
		return DataAccessObject.getBasicConnection();
	}
	
	public void create(MerchantFpsAccess mfps) throws DataAccessLayerException {
		super.save(mfps);
	}
	
	public void saveOrUpdate(MerchantFpsAccess mfps) throws DataAccessLayerException {
		super.saveOrUpdate(mfps);
	}
	
	public void delete(MerchantFpsAccess mfps) throws DataAccessLayerException {
		super.delete(mfps);
	}

	public MerchantFpsAccess find(String invoiceConstant) throws DataAccessLayerException {
		return (MerchantFpsAccess) super.find(MerchantFpsAccess.class, invoiceConstant);
	}
	
	@SuppressWarnings("finally")
	public MerchantFpsAccess getMerchantFpsAccessByEmailId(String emailId) {
		MerchantFpsAccess dpsResponse = null;
		Session session = HibernateSessionProvider.getSession();
		Transaction tx = session.beginTransaction();

		try {
			List<MerchantFpsAccess> dps =  session.createQuery("from MerchantFpsAccess mfps where dps.emailId = :emailId").setParameter("emailId", emailId).getResultList();
			if(dps.size() != 0){
				dpsResponse = dps.get(dps.size()-1);				
			}			
			tx.commit();
			return dpsResponse;
		} catch (ObjectNotFoundException objectNotFound) {
			handleException(objectNotFound,tx);
		} catch (HibernateException hibernateException) {
			handleException(hibernateException,tx);
		}catch(Exception e) {
			return null;
		}
		finally {
			autoClose(session);
		}
		return dpsResponse;
	}
	
	@SuppressWarnings("finally")
	public MerchantFpsAccess getMerchantFpsAccessByPayId(String payId) {
		MerchantFpsAccess dpsResponse = null;
		Session session = HibernateSessionProvider.getSession();
		Transaction tx = session.beginTransaction();

		try {
			List<MerchantFpsAccess> dps =  session.createQuery("from MerchantFpsAccess mfps where mfps.payId = :payId").setParameter("payId", payId).getResultList();
			if(dps.size() != 0){
				dpsResponse = dps.get(dps.size()-1);				
			}			
			tx.commit();
			return dpsResponse;
		} catch (ObjectNotFoundException objectNotFound) {
			handleException(objectNotFound,tx);
		} catch (HibernateException hibernateException) {
			handleException(hibernateException,tx);
		}catch(Exception e) {
			return null;
		}
		finally {
			autoClose(session);
		}
		return dpsResponse;
	}
	

}
