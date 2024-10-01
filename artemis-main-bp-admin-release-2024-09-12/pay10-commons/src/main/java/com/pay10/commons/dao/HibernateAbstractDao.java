package com.pay10.commons.dao;

import java.io.Serializable;
import java.util.List;

import org.hibernate.Cache;
import org.hibernate.HibernateException;
import org.hibernate.ObjectNotFoundException;
import org.hibernate.Session;
import org.hibernate.Transaction;

public abstract class HibernateAbstractDao {
	private final boolean autoCloseFlag = true;

	public HibernateAbstractDao() {
	}

	protected void autoClose(Session session) {
		if (autoCloseFlag && null != session && session.isOpen()) {
			session.close();
			session = null;
		}
	}
	
	protected boolean saveNew(Object obj,Session session) {
		//Session session = HibernateSessionProvider.getSession();
		boolean status=false;		
		try {
			session.merge(obj);
			status=true;
		} catch (Exception e) {
			return status;
		}
		return status;
	}
	
	protected void save(Object obj) {
		
		Session session = HibernateSessionProvider.getSession();
		Transaction tx = session.beginTransaction();
		
		try {
			session.save(obj);
			tx.commit();
		} catch (HibernateException e) {
			handleException(e,tx);
		} finally {
			autoClose(session);
		}
	}
	
	protected void saveOrUpdate(Object obj) {
		Session session = HibernateSessionProvider.getSession();
		Transaction tx = session.beginTransaction();
		
		try {
			session.saveOrUpdate(obj);
			tx.commit();
		} catch (HibernateException e)
		{
			tx.rollback();
			handleException(e,tx);
		} finally {
			autoClose(session);
		}
	}
	
	protected boolean saveOrUpdateNew(Object obj,Session session) {
		//Session session = HibernateSessionProvider.getSession();
		boolean status=false;
		try {
			session.saveOrUpdate(obj);
			return status=true;
		} catch (HibernateException e){
			return status;
		}catch(Exception e) {
			return status;
		}
	}

	public void delete(Object obj) {
		Session session = HibernateSessionProvider.getSession();
		Transaction tx = session.beginTransaction();
		
		try {
			session.delete(obj);
			tx.commit();
		} catch (HibernateException e) {
			handleException(e,tx);
		} finally {
			autoClose(session);
		}
	}
	
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	protected Object find(Class clazz, Serializable id) {
		Session session = HibernateSessionProvider.getSession();
		Transaction tx = session.beginTransaction();
		Object obj = null;
		try {
			obj = session.load(clazz, id);
			tx.commit();
		} catch (ObjectNotFoundException objectNotFound) {
			// Return null if object not found
			obj = null;
		} catch (HibernateException e) {
			handleException(e,tx);
		} finally {
			autoClose(session);
		}
		return obj;
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	protected List findAll(Class clazz) {
		
		Session session = HibernateSessionProvider.getSession();
		Transaction tx = session.beginTransaction();
		
		List<Object> objects = null;
		try {
			objects = session.createQuery("from " + clazz.getName()).setCacheable(true).getResultList();
			tx.commit();
		} catch (HibernateException hException) {
			handleException(hException,tx);
		} finally {
			autoClose(session);
		}
		return objects;
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	protected List findAllBy(String hql) {
		
		Session session = HibernateSessionProvider.getSession();
		Transaction tx = session.beginTransaction();
		
		List<Object> objects = null;
		try {
			objects = session.createQuery(hql).getResultList();
			tx.commit();
		} catch (HibernateException hException) {
			handleException(hException,tx);
		} finally {
			autoClose(session);
		}
		return objects;
	}

	protected void handleException(HibernateException hException , Transaction tx) {
		tx.rollback();
		throw hException;
	}

	
	protected void startOperationNew() throws HibernateException {
		Session session = HibernateSessionProvider.getSession();
		Transaction tx = session.beginTransaction();
	}

	protected Cache getCache() throws HibernateException {
		 return HibernateSessionProvider.getSessionFactory().getCache();
	}
	

}