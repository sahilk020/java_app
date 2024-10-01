package com.pay10.commons.user;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.hibernate.HibernateException;
import org.hibernate.ObjectNotFoundException;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.springframework.stereotype.Component;

import com.pay10.commons.dao.HibernateAbstractDao;
import com.pay10.commons.dao.HibernateSessionProvider;
import com.pay10.commons.exception.DataAccessLayerException;

/**
 * @author Puneet
 * 
 */

@Component
public class UserRecordsDao extends HibernateAbstractDao {

	private UserRecords userRecords = new UserRecords();
	
	public UserRecordsDao() {
		super();
	}

	public void createDetails(String emailId, String password, String payId){
		userRecords.setEmailId(emailId);
		userRecords.setPassword(password);
		userRecords.setPayId(payId);
		userRecords.setCreateDate(new Date());	
		create(userRecords);
	}
	
	public void create(UserRecords userRecords) throws DataAccessLayerException {
		super.save(userRecords);
	}

	public void delete(UserRecords userRecords) throws DataAccessLayerException {
		super.delete(userRecords);
	}

	public UserRecords find(Long id) throws DataAccessLayerException {
		return (UserRecords) super.find(UserRecords.class, id);
	}

	public UserRecords find(String name) throws DataAccessLayerException {
		return (UserRecords) super.find(UserRecords.class, name);
	}

	@SuppressWarnings("rawtypes")
	public List findAll() throws DataAccessLayerException {
		return super.findAll(UserRecords.class);
	}

	public void update(UserRecords userRecords) throws DataAccessLayerException {
		super.saveOrUpdate(userRecords);
	}
	
	@SuppressWarnings("unchecked")
	public List<String> getOldPasswords(String emailId) {
		List<String> userOldPasswords = new ArrayList<String>();
		Session session = HibernateSessionProvider.getSession();
		Transaction tx = session.beginTransaction();
		try {
			userOldPasswords = session.createQuery("Select password from UserRecords UR where UR.emailId = :emailId  order by UR.id desc")
														.setParameter("emailId", emailId)
														.setMaxResults(4).getResultList();
			tx.commit();
	      }
		catch (ObjectNotFoundException objectNotFound) {
			handleException(objectNotFound,tx);
		}
		catch (HibernateException hibernateException) {
			handleException(hibernateException,tx);
		}
		finally {
			autoClose(session);
		}
		return userOldPasswords;
	}
}
