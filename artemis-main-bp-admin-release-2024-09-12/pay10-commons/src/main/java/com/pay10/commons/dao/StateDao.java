package com.pay10.commons.dao;

import java.util.List;

import org.hibernate.Session;
import org.springframework.stereotype.Service;

import com.pay10.commons.entity.State;

@Service
public class StateDao extends HibernateAbstractDao {

	@SuppressWarnings("unchecked")
	public List<State> getAllState() {
		return super.findAll(State.class);
	}

	@SuppressWarnings("unchecked")
	public List<State> getByCountry(String countryName) {
		String hql = "FROM State S where S.country.name=:countryName";
		Session session = HibernateSessionProvider.getSession();
		try {
			return session.createQuery(hql).setParameter("countryName", countryName).getResultList();
		} finally {
			autoClose(session);
		}
	}
}
