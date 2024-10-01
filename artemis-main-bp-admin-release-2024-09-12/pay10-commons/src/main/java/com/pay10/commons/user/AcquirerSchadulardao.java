package com.pay10.commons.user;

import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.hibernate.HibernateException;
import org.hibernate.ObjectNotFoundException;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.pay10.commons.dao.HibernateAbstractDao;
import com.pay10.commons.dao.HibernateSessionProvider;
import com.pay10.commons.exception.DataAccessLayerException;



@Component("AcquirerSchadulardao")
//@Scope(value = "prototype", proxyMode=ScopedProxyMode.TARGET_CLASS)
public class AcquirerSchadulardao extends HibernateAbstractDao {
	private static final Logger logger = LoggerFactory.getLogger(AcquirerSchadulardao.class.getName());

	public void createAndUpdate(AcquirerSchadular acquirerschadular) throws DataAccessLayerException {
        super.saveOrUpdate(acquirerschadular);

	}
	
	public List<AcquirerSchadular> getAcquirerDetails() {
		Session session = HibernateSessionProvider.getSession();
		try {
		return session.createQuery(
						"FROM AcquirerSchadular where status = :status ",
						AcquirerSchadular.class).setParameter("status", "ACTIVE")
.setCacheable(true).getResultList();
			}

		finally {
			autoClose(session);
		}
	
}
	
	
	public AcquirerSchadular getMatchingRule(AcquirerSchadular acquirerSchadular) {
		Session session = HibernateSessionProvider.getSession();
		try {
		return session.createQuery(
						"FROM AcquirerSchadular where status = :status and acquirer = :acquirer  ",
						AcquirerSchadular.class).setParameter("status", "ACTIVE").setParameter("acquirer",acquirerSchadular.getAcquirer() )
				.setCacheable(true).uniqueResult();
			}

		finally {
			autoClose(session);
		}
	
}
	
	public List<AcquirerSchadular> getSearchDetails( String acquirer) {
		Session session = HibernateSessionProvider.getSession();
		String query="FROM AcquirerSchadular  where status = 'ACTIVE'";
		try {
				
			if ( (!acquirer.equals("")) && (acquirer!=null)) {
				query=query+" and  acquirer ='"+acquirer+"'";
			} 
			logger.info(query);
				return session.createQuery(
						query,
						AcquirerSchadular.class)
						.setCacheable(true).getResultList();
			
			} finally {
			autoClose(session);
		}
	}


}
