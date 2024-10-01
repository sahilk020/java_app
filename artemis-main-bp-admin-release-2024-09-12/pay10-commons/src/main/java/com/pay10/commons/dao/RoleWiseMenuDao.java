package com.pay10.commons.dao;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.pay10.commons.user.Menu;
import com.pay10.commons.user.Permission;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.pay10.commons.exception.DataAccessLayerException;
import com.pay10.commons.user.RoleWiseMenu;

@Service
public class RoleWiseMenuDao extends HibernateAbstractDao {
	Logger logger= LoggerFactory.getLogger(RoleWiseMenuDao.class.getName());
	public void save(RoleWiseMenu roleWiseMenu) throws DataAccessLayerException {
		super.save(roleWiseMenu);
	}
	
	public boolean saveNew(RoleWiseMenu roleWiseMenu,Session session) throws DataAccessLayerException {
		return super.saveNew(roleWiseMenu,session);
	}

	@SuppressWarnings("unchecked")
	public List<RoleWiseMenu> getRoleWiseMenus() {
		return super.findAll(RoleWiseMenu.class);
	}

	public RoleWiseMenu getRoleWiseMenu(long id) {
		return (RoleWiseMenu) super.find(RoleWiseMenu.class, id);
	}

	public void update(RoleWiseMenu roleWiseMenu) {
		super.saveOrUpdate(roleWiseMenu);
	}

	public void delete(long id) {
		super.delete(getRoleWiseMenu(id));
	}

	@SuppressWarnings("unchecked")
	public List<RoleWiseMenu> getbyRoleId(long roleId) {
		Session session = HibernateSessionProvider.getSession();
		String query = "FROM RoleWiseMenu R where R.role.id=:roleId";
		return session.createQuery(query).setParameter("roleId", roleId).getResultList();
	}
	
	public boolean deleteByRoleIdNew(long roleId,Session session) {
		boolean status = false;
		String query = "DELETE FROM role_wise_menu where roleId=" + roleId;
		logger.info("before deleteByRoleIdNew update count : " + query);
		int count = session.createNativeQuery(query).executeUpdate();
		logger.info("after deleteByRoleIdNew update count : " + count);
		status = true;
		return status;
	}
	
	public int deleteByRoleId(long roleId) {
		Session session = HibernateSessionProvider.getSession();
		Transaction tx = session.beginTransaction();
		int count = 0;
		try {
			String query = "DELETE FROM RoleWiseMenu R where R.role.id=:roleId";
			count = session.createQuery(query).setParameter("roleId", roleId).executeUpdate();
			tx.commit();
		} catch (HibernateException e) {
			tx.rollback();
			handleException(e, tx);

		} finally {
			autoClose(session);
		}
		return count;
	}


}
