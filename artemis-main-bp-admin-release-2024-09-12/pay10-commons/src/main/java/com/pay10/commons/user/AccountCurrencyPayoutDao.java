package com.pay10.commons.user;

import java.util.List;

import org.hibernate.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.pay10.commons.dao.HibernateAbstractDao;
import com.pay10.commons.dao.HibernateSessionProvider;

@Component
public class AccountCurrencyPayoutDao extends HibernateAbstractDao {
	private static Logger logger = LoggerFactory.getLogger(AccountCurrencyDao.class.getName());

	public AccountCurrencyPayout getAccountCurrencyPayoutDetail(String Acquirer, String currencyCode) {
		try {
			AccountCurrencyPayout accountCurrencyPayout = new AccountCurrencyPayout();
		Session session = HibernateSessionProvider.getSession();
		String sqlQuery = "from AccountCurrencyPayout where Acquirer='" + Acquirer + "'and currencyCode ='" + currencyCode + "'";

		logger.info("Query :" + sqlQuery);
		return (AccountCurrencyPayout) session.createQuery(sqlQuery).getSingleResult();
		}catch(Exception e){
			return null;
	
		}
	}

	
}
