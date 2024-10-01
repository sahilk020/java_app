package com.pay10.commons.user;
import org.slf4j.Logger; import org.slf4j.LoggerFactory;
import org.hibernate.HibernateException;
import org.hibernate.ObjectNotFoundException;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.springframework.stereotype.Service;

import com.pay10.commons.dao.HibernateAbstractDao;
import com.pay10.commons.dao.HibernateSessionProvider;
import com.pay10.commons.exception.DataAccessLayerException;

@Service
public class LoginOtpDao extends HibernateAbstractDao  {

	
	private static Logger logger = LoggerFactory.getLogger(LoginOtpDao.class.getName());
	
	
	public void create(LoginOtp login) throws DataAccessLayerException {
		super.save(login);
	}
	
	
	public void createPin(ForgetPin pin) throws DataAccessLayerException {
		super.save(pin);
	}
	
	public void updatePin(ForgetPin pin) throws DataAccessLayerException {
		super.saveOrUpdate(pin);
	}
	
	public void update(LoginOtp otp) throws DataAccessLayerException {
		super.saveOrUpdate(otp);
	}
	
	
	
	
	public User getUserData(String emailId) {
		
		User user = null;
		Session session = HibernateSessionProvider.getSession();
		Transaction tx = session.beginTransaction();
		try {
			
			user =  (User)session.createQuery("from User U where U.emailId = :emailId")
					.setParameter("emailId", emailId).getSingleResult();
		
			tx.commit();
		} catch (ObjectNotFoundException objectNotFound) {
			handleException(objectNotFound,tx);
		} catch (HibernateException hibernateException) {
			handleException(hibernateException,tx);
		} 
		finally {
			autoClose(session);
		}
		return user;
	}
	
	
	
	
public LoginOtp checkOtp(String email) {
		
	   LoginOtp otp = null;
	   Session session = HibernateSessionProvider.getSession();
		Transaction tx = session.beginTransaction();
		try {
			otp =  (LoginOtp)session.createQuery("from LoginOtp U where U.emailId = :emailId and U.status = :status")
					.setParameter("emailId", email).setParameter("status", "Active").getSingleResult();
		
			tx.commit();
			return otp;
		} catch (ObjectNotFoundException objectNotFound) {
			handleException(objectNotFound,tx);
		} catch (HibernateException hibernateException) {
			handleException(hibernateException,tx);
		} 
		catch(NullPointerException ex)
		{
			logger.error("Null pointer exception in LoginOtpDao , checkOtp",ex);
		}
		finally {
			autoClose(session);
		}
		return otp;
	}



public ForgetPin checkForgetOtp(String emailid) {
	
	 ForgetPin otp = null;
	 Session session = HibernateSessionProvider.getSession();
	 Transaction tx = session.beginTransaction();
		try {
			otp =  (ForgetPin)session.createQuery("from ForgetPin U where U.emailId = :emailId and U.status = :status")
					.setParameter("emailId", emailid).setParameter("status", "Active").getSingleResult();
		
			tx.commit();
		} catch (ObjectNotFoundException objectNotFound) {
			handleException(objectNotFound,tx);
		} catch (HibernateException hibernateException) {
			handleException(hibernateException,tx);
		} 
		catch(NullPointerException ex)
		{
			logger.error("Exception in LoginOtpDao , checkForgetOtp ",ex);
		}
		finally {
			autoClose(session);
			
		}
		return otp;
	}


public LoginOtp checkExpireOtp(String userOtp,String emailId) {
	
	   LoginOtp otp = null;
	   Session session = HibernateSessionProvider.getSession();
	   Transaction tx = session.beginTransaction();
		try {
			otp =  (LoginOtp)session.createQuery("from LoginOtp U where U.otp = :otp and U.emailId = :emailId and U.status = :status")
					.setParameter("otp", userOtp).setParameter("emailId", emailId).setParameter("status", "Active").getSingleResult();
		
			tx.commit();
			return otp;
		} catch (ObjectNotFoundException objectNotFound) {
			handleException(objectNotFound,tx);
		} catch (HibernateException hibernateException) {
			handleException(hibernateException,tx);
		} 
		catch(NullPointerException ex)
		{
			logger.error("Null pointer exception in LoginOtpDao , checkExpireOtp",ex);
		}
		finally {
			autoClose(session);
			
		}
		return otp;
	}




public ForgetPin checkExpirePasswordOtp(String userOtp,String emailId) {
	
	   ForgetPin otp = null;
	   Session session = HibernateSessionProvider.getSession();
	   Transaction tx = session.beginTransaction();
		try {
			otp =  (ForgetPin)session.createQuery("from ForgetPin U where U.otp = :otp and U.emailId = :emailId and U.status = :status")
					.setParameter("otp", userOtp).setParameter("emailId", emailId).setParameter("status", "Active").getSingleResult();
		
			tx.commit();
		} catch (ObjectNotFoundException objectNotFound) {
			handleException(objectNotFound,tx);
		} catch (HibernateException hibernateException) {
			handleException(hibernateException,tx);
		} 
		catch(NullPointerException ex)
		{
			logger.error("Null pointer exception in LoginOtpDao , checkExpireOtp",ex);
		}
		finally {
			autoClose(session);
			
		}
		return otp;
	}




public LoginOtp checkEmail(String emailId) {
	
	   LoginOtp otp = null;
	   Session session = HibernateSessionProvider.getSession();
	   Transaction tx = session.beginTransaction();
	   
		try {
			
			otp =  (LoginOtp)session.createQuery("from LoginOtp U where U.emailId = :emailId and U.status = :status")
					.setParameter("emailId", emailId).setParameter("status", "Active").getSingleResult();
		
			tx.commit();
			return otp;
		} catch (ObjectNotFoundException objectNotFound) {
			handleException(objectNotFound,tx);
		} catch (HibernateException hibernateException) {
			handleException(hibernateException,tx);
		} 
		catch(NullPointerException ex)
		{
			logger.error("Null pointer exception in LoginOtpDao , checkEmail",ex);
		}
		finally {
			autoClose(session);
		}
		return otp;
	}



	



}
