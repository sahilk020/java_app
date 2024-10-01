package com.pay10.commons.dao;

import java.util.List;

import org.hibernate.HibernateException;
import org.hibernate.ObjectNotFoundException;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.springframework.stereotype.Service;

import com.pay10.commons.user.User;
import com.pay10.commons.user.UserGroup;
import com.pay10.commons.user.UserType;
import com.pay10.commons.util.UserStatusType;

/**
 * @author Jay
 *
 */
@Service
public class UserGroupDao extends HibernateAbstractDao {

	public void save(UserGroup userGroup) {
		super.save(userGroup);
	}

	@SuppressWarnings("unchecked")
	public List<UserGroup> getUserGroups() {
		return super.findAll(UserGroup.class);
	}

	public UserGroup getUserGroup(long id) {
		return (UserGroup) super.find(UserGroup.class, id);
	}

	public void update(UserGroup userGroup) {
		super.saveOrUpdate(userGroup);
	}

	public void delete(long id) {
		super.delete(getUserGroup(id));
	}

	public UserGroup getUserGroupByUserType(UserType userType) {
		
		Session session = HibernateSessionProvider.getSession();
		Transaction tx = session.beginTransaction();

		UserGroup responseUser = null;
		try {
			responseUser = (UserGroup) session.createQuery("from UserGroup where groupName = '" + userType.name() + "'").getResultList().get(0);
			
			tx.commit();

			return responseUser;
		} catch (ObjectNotFoundException objectNotFound) {
			handleException(objectNotFound, tx);
		} catch (HibernateException hibernateException) {
			handleException(hibernateException, tx);
		} finally {
			autoClose(session);
		}

		return responseUser;
	}
}
