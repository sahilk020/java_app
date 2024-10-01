package com.pay10.commons.dao;

import java.util.List;

import org.hibernate.Session;
import org.hibernate.query.Query;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.pay10.commons.entity.BankList;

@Component
public class BankListDao extends HibernateAbstractDao{
	private static Logger logger = LoggerFactory.getLogger(BankListDao.class.getName());
	@SuppressWarnings({ "rawtypes", "unchecked" })
	private List<BankList> findAll(){
		Session session = HibernateSessionProvider.getSession();
		Query query=session.createQuery("FROM BankList");
		return query.getResultList();
	}
	
	private List<BankList> findAllByAccountNumber(String accountNumber){
		Session session = HibernateSessionProvider.getSession();
		String q="FROM BankList where accountNumber='"+accountNumber+"'";
		Query query=session.createQuery(q);
		logger.info("findAllByAccountNumber Query : "+q);
		return query.getResultList();
	}
	
	public List<BankList> getAllBankList() {
		return findAll();
	}
	public List<BankList> getAllByAccountNumber(String accountNumber){
		return findAllByAccountNumber(accountNumber);
	}
}
