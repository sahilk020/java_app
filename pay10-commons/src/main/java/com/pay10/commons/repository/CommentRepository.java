package com.pay10.commons.repository;

import java.util.List;

import org.hibernate.Session;
import org.springframework.stereotype.Component;

import com.pay10.commons.dao.HibernateAbstractDao;
import com.pay10.commons.dao.HibernateSessionProvider;
import com.pay10.commons.exception.DataAccessLayerException;

@Component("commentRepo")
public class CommentRepository extends HibernateAbstractDao {

	public void save(Comment chargebackEntity) throws DataAccessLayerException {
		super.save(chargebackEntity);
	}

	public List<Comment> findByCaseId(String caseId) throws DataAccessLayerException {
		Session session = HibernateSessionProvider.getSession();
		try {
			return session.createQuery("FROM Comment WHERE caseId=:caseId", Comment.class)
					.setParameter("caseId", caseId).getResultList();
		} finally {
			autoClose(session);
		}
	}

}
