package com.pay10.commons.user;

import org.hibernate.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.pay10.commons.dao.HibernateAbstractDao;
import com.pay10.commons.dao.HibernateSessionProvider;

@Component("ErrorCodeMappingPayoutDao")

public class ErrorCodeMappingPayoutDao extends HibernateAbstractDao {
	private static final Logger logger = LoggerFactory.getLogger(ErrorCodeMappingPayoutDao.class.getName());
	
	
	
	public ErrorCodeMappingPayout getErrorCodeByAcquirer(String acquirer, String acqStatuscode) {
		try {
			ErrorCodeMappingPayout errorCodeMappingPayout = new ErrorCodeMappingPayout();
			Session session = HibernateSessionProvider.getSession();
			String sqlQuery = "from ErrorCodeMappingPayout where acqStatuscode='" + acqStatuscode + "' and acquirer='" + acquirer+"'";
					

			logger.info("Query :" + sqlQuery);
			return (ErrorCodeMappingPayout) session.createQuery(sqlQuery).getSingleResult();
		} catch (Exception e) {
			return null;

		}

	}
	
	
	

	
	
}
