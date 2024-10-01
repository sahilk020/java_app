package com.pay10.commons.user;

import org.hibernate.HibernateException;
import org.hibernate.ObjectNotFoundException;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.springframework.stereotype.Component;

import com.pay10.commons.dao.HibernateAbstractDao;
import com.pay10.commons.dao.HibernateSessionProvider;
import com.pay10.commons.exception.DataAccessLayerException;

@Component
public class InvoiceTransactionDao extends HibernateAbstractDao{
	
	public InvoiceTransactionDao() {
        super();
    }
	public void create(Invoice invoiceTransaction) throws DataAccessLayerException {
        super.save(invoiceTransaction);
    }

	public void delete(Invoice invoiceTransaction) throws DataAccessLayerException {
        super.delete(invoiceTransaction);
    }

	public void update(Invoice invoiceTransaction) throws DataAccessLayerException {
        super.saveOrUpdate(invoiceTransaction);
    }

	public Invoice findByInvoiceId(String invoiceId) {
		Invoice invoice = null;
		Session session = HibernateSessionProvider.getSession();
		Transaction tx = session.beginTransaction();
		try {
			invoice = (Invoice) session.createQuery("from Invoice I where I.invoiceId = :invoiceId")
												.setParameter("invoiceId", invoiceId).getSingleResult();
			tx.commit();
			return invoice;
		} catch (ObjectNotFoundException objectNotFound) {
			handleException(objectNotFound,tx);
		} catch (HibernateException hibernateException) {
			handleException(hibernateException,tx);
		} finally {
			autoClose(session);
		}
		return invoice;
	}
}
