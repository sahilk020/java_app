package com.pay10.commons.repository;

import javax.persistence.NoResultException;
import javax.persistence.NonUniqueResultException;
import javax.persistence.Query;

import org.hibernate.Session;
import org.springframework.stereotype.Service;

import com.pay10.commons.dao.HibernateAbstractDao;
import com.pay10.commons.dao.HibernateSessionProvider;
import com.pay10.commons.entity.VersionControlEntity;
import com.pay10.commons.util.TDRStatus;

@Service
public class VersionControlRepo extends HibernateAbstractDao {

	public VersionControlEntity getVersion() {
		Session session = HibernateSessionProvider.getSession();
		Query query = session
				.createQuery("FROM VersionControlEntity WHERE status='" + TDRStatus.ACTIVE.getName() + "'");
		VersionControlEntity controlEntity = null;

		try {
			controlEntity = (VersionControlEntity) query.getSingleResult();
		} catch (NoResultException e) {
			return controlEntity;
		} catch (NonUniqueResultException e) {
			return controlEntity;
		}

		return controlEntity;
	}

	
	public void save( VersionControlEntity controlEntity) {
		super.save(controlEntity);
	}
	
	

	public void update( VersionControlEntity controlEntity) {
		super.saveOrUpdate(controlEntity);
	}
}
