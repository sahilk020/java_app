package com.pay10.commons.user;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

import org.hibernate.HibernateException;
import org.hibernate.ObjectNotFoundException;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.pay10.commons.dao.DataAccessObject;
import com.pay10.commons.dao.HibernateAbstractDao;
import com.pay10.commons.dao.HibernateSessionProvider;
import com.pay10.commons.exception.DataAccessLayerException;


@Component("invoiceNumberCounterDao")
public class InvoiceNumberCounterDao   extends HibernateAbstractDao {
	private static Logger logger = LoggerFactory.getLogger(InvoiceNumberCounterDao.class.getName());
	
	public InvoiceNumberCounterDao() {
		super();
	}

	private static final String getCompleteInvoiceNumberCounterWithInvoiceConstantQuery = "from InvoiceNumberCounter U where U.invoiceConstant = :invoiceConstant";

	private Connection getConnection() throws SQLException {
		return DataAccessObject.getBasicConnection();
	}
	public void create(InvoiceNumberCounter in) throws DataAccessLayerException {
		super.save(in);
	}
	public void delete(InvoiceNumberCounter in) throws DataAccessLayerException {
		super.delete(in);
	}

	public InvoiceNumberCounter find(String invoiceConstant) throws DataAccessLayerException {
		return (InvoiceNumberCounter) super.find(InvoiceNumberCounter.class, invoiceConstant);
	}
	
	@SuppressWarnings("finally")
	public int getInvoiceNumberByPayId(String payId) {
		InvoiceNumberCounter incResponse = null;
		Session session = HibernateSessionProvider.getSession();
		Transaction tx = session.beginTransaction();

		try {
			List<InvoiceNumberCounter> incs =  session.createQuery("from InvoiceNumberCounter inc where inc.payId = :payId").setParameter("payId", payId).getResultList();
			if(incs.size() != 0){
				incResponse = incs.get(incs.size()-1);				
			}
			/*
			 * for (InvoiceNumberCounter inc : incs) { incResponse = inc; break; }
			 */			
			tx.commit();
			if(incResponse != null) {
				return incResponse.getInvoiceNumber();				
			}else {
				return 0;	
			}

		} catch (ObjectNotFoundException objectNotFound) {
			handleException(objectNotFound,tx);
		} catch (HibernateException hibernateException) {
			handleException(hibernateException,tx);
		}catch(Exception e) {
			return 0;
		}
		finally {
			autoClose(session);
		}
		return incResponse.getInvoiceNumber();
	}
	
	public InvoiceNumberCounter getLatestInvoiceNumberByPayId() {

		InvoiceNumberCounter incResponse = null;
		Session session = HibernateSessionProvider.getSession();
		Transaction tx = session.beginTransaction();

		try {
			List<InvoiceNumberCounter> incs =  session.createQuery("from InvoiceNumberCounter inc ORDER BY inc.invoiceId DESC").setMaxResults(1).getResultList();
			if(incs.size() != 0){
				incResponse = incs.get(incs.size()-1);				
			}
			/*
			 * for (InvoiceNumberCounter inc : incs) { incResponse = inc; break; }
			 */			
			tx.commit();
			return incResponse;
		} catch (ObjectNotFoundException objectNotFound) {
			handleException(objectNotFound,tx);
		} catch (HibernateException hibernateException) {
			handleException(hibernateException,tx);
		}catch(Exception e) {
			return null;
		}
		finally {
			autoClose(session);
		}
		return incResponse;
	
	}
	
}
