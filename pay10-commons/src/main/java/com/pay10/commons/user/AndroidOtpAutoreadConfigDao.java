package com.pay10.commons.user;

import java.util.ArrayList;
import java.util.List;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.springframework.stereotype.Component;

import com.pay10.commons.dao.HibernateAbstractDao;
import com.pay10.commons.dao.HibernateSessionProvider;
import com.pay10.commons.exception.DataAccessLayerException;
/**
 * @author Rohit
 *
 */

@Component
public class AndroidOtpAutoreadConfigDao extends HibernateAbstractDao {
	
	public AndroidOtpAutoreadConfigDao() {
        super();
    }

	public void create(AndroidOtpAutoreadConfigDao androidOtpAutoreadConfigDao) throws DataAccessLayerException {
        super.save(androidOtpAutoreadConfigDao);
    }
	
	public void delete(AndroidOtpAutoreadConfigDao androidOtpAutoreadConfigDao) throws DataAccessLayerException {
        super.delete(androidOtpAutoreadConfigDao);
    }
	
	public void update(AndroidOtpAutoreadConfigDao androidOtpAutoreadConfigDao) throws DataAccessLayerException {
        super.saveOrUpdate(androidOtpAutoreadConfigDao);
    }
	
	@SuppressWarnings("rawtypes")
	public  List findAll() throws DataAccessLayerException{
	    return super.findAll(AndroidOtpAutoreadConfigDao.class);
	}
	 
	public AndroidOtpAutoreadConfigDao find(Long id) throws DataAccessLayerException {
	    return (AndroidOtpAutoreadConfigDao) super.find(AndroidOtpAutoreadConfigDao.class, id);
	}
	 
	public AndroidOtpAutoreadConfigDao find(String name) throws DataAccessLayerException {
	    return (AndroidOtpAutoreadConfigDao) super.find(AndroidOtpAutoreadConfigDao.class, name);
	}
	
	public static  List<AndroidOtpAutoreadConfig> findLastPasswordCreateDate() throws DataAccessLayerException {
		Session session = HibernateSessionProvider.getSession();
		Transaction tx = session.beginTransaction();
		List<AndroidOtpAutoreadConfig> respdata = new ArrayList<AndroidOtpAutoreadConfig>();
		 
			String sqlQuery = "SELECT * FROM AndroidOtpAutoreadConfig";
			respdata = session.createNativeQuery(sqlQuery, AndroidOtpAutoreadConfig.class).getResultList();
			tx.commit();
			
			if (respdata.size() < 1) {
				return respdata;
			}
			return respdata;
		 
	}
}
