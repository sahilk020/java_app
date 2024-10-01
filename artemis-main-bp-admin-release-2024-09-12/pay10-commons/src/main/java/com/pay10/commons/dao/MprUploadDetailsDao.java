package com.pay10.commons.dao;

import org.hibernate.HibernateException;
import org.hibernate.ObjectNotFoundException;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.springframework.stereotype.Service;

import com.pay10.commons.exception.DataAccessLayerException;
import com.pay10.commons.user.MprUploadDetails;

@Service
public class MprUploadDetailsDao extends HibernateAbstractDao {
	
	public void create(MprUploadDetails mpruploadDetails) throws DataAccessLayerException {
		super.save(mpruploadDetails);
	}
	
	
	@SuppressWarnings("unchecked")
	public MprUploadDetails existMprFileUpload(String acquirer,String paymentType,String mprDate, String fileName) {
		Session session = HibernateSessionProvider.getSession();
		Transaction tx = session.beginTransaction();
		MprUploadDetails responseMprDetails = null;
		
	
//			String getNodalAmount="from MprUploadDetails n where n.acquirerName = :acquirerName and n.paymentType = :paymentType and mprDate = :mprDate and fileName = :fileName";
			String getNodalAmount="from MprUploadDetails n where n.acquirerName = :acquirerName and mprDate = :mprDate and fileName = :fileName";
			try {
//				responseMprDetails =(MprUploadDetails) session.createQuery(getNodalAmount).setParameter("acquirerName", acquirer).setParameter("paymentType", paymentType).
//						setParameter("mprDate", mprDate).setParameter("fileName", fileName)
//						.setCacheable(true).getSingleResult();
				responseMprDetails =(MprUploadDetails) session.createQuery(getNodalAmount).setParameter("acquirerName", acquirer).
						setParameter("mprDate", mprDate).setParameter("fileName", fileName)
						.setCacheable(true).getSingleResult();
				tx.commit();
				return responseMprDetails;
			
			} catch (ObjectNotFoundException objectNotFound) {
				handleException(objectNotFound,tx);
			} catch (HibernateException hibernateException) {
				handleException(hibernateException,tx);
			} 
			catch(Exception ex)
			{
				ex.printStackTrace();
			}
			finally {
				autoClose(session);
			}
			return responseMprDetails;		
		
	    
	}
	
	@SuppressWarnings("unchecked")
	public MprUploadDetails existMprFile(String acquirer,String paymentType,String mprDate) {
		Session session = HibernateSessionProvider.getSession();
		Transaction tx = session.beginTransaction();
		MprUploadDetails responseMprDetails = null;
		
	
//			String getNodalAmount="from MprUploadDetails n where n.acquirerName = :acquirerName and n.paymentType = :paymentType and mprDate = :mprDate";
			String getNodalAmount="from MprUploadDetails n where n.acquirerName = :acquirerName and mprDate = :mprDate";
			try {
//				responseMprDetails =(MprUploadDetails) session.createQuery(getNodalAmount).setParameter("acquirerName", acquirer).setParameter("paymentType", paymentType).
//						setParameter("mprDate", mprDate)
//						.setCacheable(true).getSingleResult();
				responseMprDetails =(MprUploadDetails) session.createQuery(getNodalAmount).setParameter("acquirerName", acquirer).
						setParameter("mprDate", mprDate)
						.setCacheable(true).getSingleResult();
				tx.commit();
				return responseMprDetails;
			
			} catch (ObjectNotFoundException objectNotFound) {
				handleException(objectNotFound,tx);
			} catch (HibernateException hibernateException) {
				handleException(hibernateException,tx);
			} 
			catch(Exception ex)
			{
				ex.printStackTrace();
			}
			finally {
				autoClose(session);
			}
			return responseMprDetails;		
		
	    
	}

}
