package com.pay10.commons.dao;

import java.util.List;

import org.hibernate.Session;
import org.hibernate.Transaction;

import com.pay10.commons.exception.DataAccessLayerException;
import com.pay10.commons.user.SbiToken;
	
public class SbiTokenDao extends HibernateAbstractDao {
	
	
	
	public void save(SbiToken sbiToken) throws DataAccessLayerException {
		super.save(sbiToken);
	}

	public SbiToken getSbiToken(long id) {
		return (SbiToken) super.find(SbiToken.class, id);
	}

	public void update(SbiToken sbiToken) {
		super.saveOrUpdate(sbiToken);
	}
	
	@SuppressWarnings("unchecked")
	public SbiToken getTokenByName(String acquirerName) {
		Session session = HibernateSessionProvider.getSession();
		Transaction tx = session.beginTransaction();
		String query = "FROM SbiToken S where S.acquirerName=:acquirerName";
		List<SbiToken> sbiTokenName = session.createQuery(query).setParameter("acquirerName", acquirerName).getResultList();
		tx.commit();
		SbiToken token = new SbiToken();
		System.out.println("sbiTokenName---->"+sbiTokenName);
		if(sbiTokenName != null && sbiTokenName.size() >0) {
			token = sbiTokenName.get(0);
			System.out.println("token---->"+token);
		}
		
		return token;
	}
}
