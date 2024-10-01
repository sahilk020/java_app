package com.pay10.commons.user;

import java.util.List;

import javax.persistence.TypedQuery;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.NativeQuery;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.pay10.commons.dao.HibernateAbstractDao;
import com.pay10.commons.dao.HibernateSessionProvider;
import com.pay10.commons.exception.DataAccessLayerException;

@Component
public class AccountDao extends HibernateAbstractDao {
	private static Logger logger = LoggerFactory.getLogger(AccountDao.class.getName());
	
	public AccountDao(){
		super();
	}

	public void create(Account account) throws DataAccessLayerException {
        super.save(account);
    }
	
	public void delete(Account account) throws DataAccessLayerException {
        super.delete(account);
    }
	
	public void update(Account account) throws DataAccessLayerException {
        super.saveOrUpdate(account);
    }
	
	@SuppressWarnings("rawtypes")
	public  List findAll() throws DataAccessLayerException{
	    return super.findAll(Account.class);
	}
	 
	public Account find(Long id) throws DataAccessLayerException {
	    return (Account) super.find(Account.class, id);
	}
	 
	public Account find(String name) throws DataAccessLayerException {
	    return (Account) super.find(Account.class, name);
	}

	public void deletAccount(Long id) {
		// TODO Auto-generated method stub
		   Session session = HibernateSessionProvider.getSession();
		   Transaction tx = session.beginTransaction();
		   
		   try {
			   //String query="Delete from account_tdrSetting where tdrSetting_id=?1";
			   String query="Delete from Account_TdrSetting where tdrSetting_id=?1";

			   NativeQuery nativeQuery = session.createNativeQuery(query);
			   nativeQuery.setParameter(1, id);
			   int c = nativeQuery.executeUpdate();
			  logger.info(String.format("%d user_accounts delete", c));
		      tx.commit();
		   } catch (HibernateException e) {
		      handleException(e,tx);
		   } finally {
		      autoClose(session);
		   }
		
	}

	public void addAccount(Long account_id, Long tdrSetting_id) {
		  Session session = HibernateSessionProvider.getSession();
		   Transaction tx = session.beginTransaction();
		   
		   try {
			   String query="Insert into Account_TdrSetting (Account_id,tdrSetting_id) Values(:1,:2)";
			   //String query="Insert into account_tdrSetting (Account_id,tdrSetting_id) Values(:1,:2)";

			   NativeQuery nativeQuery = session.createNativeQuery(query);
			   nativeQuery.setParameter(1, account_id);
			   nativeQuery.setParameter(2, tdrSetting_id);
			   int c = nativeQuery.executeUpdate();
			  logger.info(String.format("%d user_accounts add", c));
		      tx.commit();
		   } catch (HibernateException e) {
		      handleException(e,tx);
		   } finally {
		      autoClose(session);
		   }
		
	}
}
