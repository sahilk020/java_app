package com.pay10.commons.user;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

import org.hibernate.HibernateException;
import org.hibernate.ObjectNotFoundException;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.pay10.commons.dao.HibernateAbstractDao;
import com.pay10.commons.dao.HibernateSessionProvider;
import com.pay10.commons.exception.DataAccessLayerException;
import com.pay10.commons.util.MopType;
import com.pay10.commons.util.PaymentType;
import com.pay10.commons.util.TransactionType;

@Component
public class ChargingDetailsDao  extends HibernateAbstractDao{
	 private static org.slf4j.Logger logger = LoggerFactory.getLogger(ChargingDetailsDao.class.getName());
	
	public ChargingDetailsDao() {
        super();
    }
	
	public void create(ChargingDetails chargingDetails) throws DataAccessLayerException {
        super.save(chargingDetails);
    }
	
	public void delete(ChargingDetails chargingDetails) throws DataAccessLayerException {
        super.delete(chargingDetails);
    }
	
	public void update(ChargingDetails chargingDetails) throws DataAccessLayerException {
        super.saveOrUpdate(chargingDetails);
    }
	
	@SuppressWarnings("rawtypes")
	public  List findAll() throws DataAccessLayerException{
	    return super.findAll(ChargingDetails.class);
	}
	 
	public ChargingDetails find(Long id) throws DataAccessLayerException {
	    return (ChargingDetails) super.find(ChargingDetails.class, id);
	}
	 
	public ChargingDetails find(String name) throws DataAccessLayerException {
	    return (ChargingDetails) super.find(ChargingDetails.class, name);
	}

	public List<ChargingDetails> findDetail(String date, String payId, String acquirerName, String paymentType, String mopType, String currency){
		List<ChargingDetails> chargingDetailsList = new ArrayList<ChargingDetails>();
		Session session = HibernateSessionProvider.getSession();
		Transaction tx = session.beginTransaction();
		try {
			
			String sqlQuery = "select * from ChargingDetails where (case when updatedDate is null then :date between createdDate and current_timestamp() else :date between createdDate and updatedDate end) and payId=:payId and acquirerName=:acquirerName and paymentType=:paymentType and mopType=:mopType and currency=:currency";
			chargingDetailsList = session.createNativeQuery(sqlQuery, ChargingDetails.class)
					 .setParameter("date", date)
					 .setParameter("payId", payId)
					 .setParameter("acquirerName", acquirerName).setParameter("mopType", mopType)
					 .setParameter("paymentType", paymentType).setParameter("currency", currency)
					 .getResultList();
			tx.commit();
		} catch (ObjectNotFoundException objectNotFound) {
			handleException(objectNotFound,tx);
		} catch (HibernateException hibernateException) {
			handleException(hibernateException,tx);
		} finally {
			autoClose(session);
		}
		return chargingDetailsList;
	}

	
	public List<ChargingDetails> findDetail(String date, String payId, String acquirerName, String paymentType, String mopType, String currency,String transactionType){
		List<ChargingDetails> chargingDetailsList = new ArrayList<ChargingDetails>();
		Session session = HibernateSessionProvider.getSession();
		Transaction tx = session.beginTransaction();
		try {
			
			
			String sqlQuery = "select * from ChargingDetails where (case when updatedDate is null then :date between createdDate and current_timestamp() else"
				+" :date between createdDate and updatedDate end) and payId=:payId and acquirerName=:acquirerName and paymentType=:paymentType and mopType=:mopType "
					+" and currency=:currency and transactionType=:transactionType ";
				chargingDetailsList = session.createNativeQuery(sqlQuery, ChargingDetails.class)
						   						  .setParameter("payId", payId)
						   						  .setParameter("acquirerName", acquirerName)
						   						  .setParameter("mopType", mopType)
						   						  .setParameter("paymentType", paymentType)
						   						  .setParameter("currency", currency)
						   						  .setParameter("transactionType", transactionType)
						   						 .setParameter("date", date)
						   						  .getResultList();
				tx.commit();
		} catch (ObjectNotFoundException objectNotFound) {
			handleException(objectNotFound,tx);
		} catch (HibernateException hibernateException) {
			handleException(hibernateException,tx);
		} finally {
			autoClose(session);
		}
		return chargingDetailsList;
	
	}
	public List<ChargingDetails> findDetailForTDR(String date, String payId, String acquirerName, String paymentType, String mopType, String currency,String transactionType){
		List<ChargingDetails> chargingDetailsList = new ArrayList<ChargingDetails>();
		Session session = HibernateSessionProvider.getSession();
		Transaction tx = session.beginTransaction();
		try {
			
			
			String sqlQuery = "select * from ChargingDetails where (case when updatedDate is null then :date between createdDate and current_timestamp() else"
				+" :date >= createdDate or :date <=updatedDate end) and payId=:payId and acquirerName=:acquirerName and paymentType=:paymentType and mopType=:mopType "
					+" and currency=:currency and transactionType=:transactionType ";
				chargingDetailsList = session.createNativeQuery(sqlQuery, ChargingDetails.class)
						   						  .setParameter("payId", payId)
						   						  .setParameter("acquirerName", acquirerName)
						   						  .setParameter("mopType", mopType)
						   						  .setParameter("paymentType", paymentType)
						   						  .setParameter("currency", currency)
						   						  .setParameter("transactionType", transactionType)
						   						 .setParameter("date", date)
						   						  .getResultList();
				tx.commit();
		} catch (ObjectNotFoundException objectNotFound) {
			handleException(objectNotFound,tx);
		} catch (HibernateException hibernateException) {
			handleException(hibernateException,tx);
		} finally {
			autoClose(session);
		}
		return chargingDetailsList;
	
	}
	
	public boolean isChargingDetailsSet(String payId) {
		BigInteger count= new BigInteger("0");
		Session session = HibernateSessionProvider.getSession();
		Transaction tx = session.beginTransaction();
		try {
			
			//String sqlQuery = "select count(*) from ChargingDetails where payId=:payId and status='ACTIVE' and merchantTDR >= 0";
			String sqlQuery = "select count(*) from TdrSetting where payId=:payId and status='ACTIVE' and tdrStatus='ACTIVE' and merchantTDR >= 0";
			count = (BigInteger) session.createNativeQuery(sqlQuery)
											 .setParameter("payId", payId)
											 .getSingleResult();
			tx.commit();
			if(count.intValue()>0){
				return true;
			}else{
				return false;
			}	
		} catch (ObjectNotFoundException objectNotFound) {
			handleException(objectNotFound,tx);
		} catch (HibernateException hibernateException) {
			handleException(hibernateException,tx);
		} finally {
			autoClose(session);
		}
		return false;	
	}

	@SuppressWarnings("unchecked")
	public List<ChargingDetails> getAllActiveChargingDetails(String payId) {
		List<ChargingDetails> chargingDetailsList = new ArrayList<ChargingDetails>();
		Session session = HibernateSessionProvider.getSession();
		Transaction tx = session.beginTransaction();
		try {
						
			chargingDetailsList = session.createQuery("from ChargingDetails C where C.payId= :payId and C.status='ACTIVE' and C.merchantTDR >= 0")
									.setParameter("payId", payId).setCacheable(true)
									.getResultList();
			tx.commit();
		} catch (ObjectNotFoundException objectNotFound) {
			handleException(objectNotFound,tx);
		} catch (HibernateException hibernateException) {
			handleException(hibernateException,tx);
		} finally {
			autoClose(session);
		}
		return chargingDetailsList;
	}
	
	@SuppressWarnings("unchecked")
	public List<ChargingDetails> getAllActiveSaleChargingDetails(String payId , PaymentType paymentType) {
		List<ChargingDetails> chargingDetailsList = new ArrayList<ChargingDetails>();
		Session session = HibernateSessionProvider.getSession();
		Transaction tx = session.beginTransaction();
		try {
			
			if(paymentType.getName().equalsIgnoreCase(PaymentType.NET_BANKING.getName())){
				chargingDetailsList = session.createQuery("from ChargingDetails C where C.payId= :payId and C.paymentType=:paymentType and C.status='ACTIVE'")
						.setParameter("payId", payId).setParameter("paymentType", paymentType).setCacheable(true)
						.getResultList();
			}
			else
			{
			chargingDetailsList = session.createQuery("from ChargingDetails C where C.payId= :payId and C.paymentType=:paymentType and C.status='ACTIVE' and C.transactionType='SALE'")
									.setParameter("payId", payId).setParameter("paymentType", paymentType).setCacheable(true)
									.getResultList();
			}
			tx.commit();
		} catch (ObjectNotFoundException objectNotFound) {
			handleException(objectNotFound,tx);
		} catch (HibernateException hibernateException) {
			handleException(hibernateException,tx);
		} finally {
			autoClose(session);
		}
		return chargingDetailsList;
	}
	
	
	@SuppressWarnings("unchecked")
	public List<ChargingDetails> getAllActiveSaleSurchargeDetails(String payId , String acquirer) {
		List<ChargingDetails> chargingDetailsList = new ArrayList<ChargingDetails>();
		Session session = HibernateSessionProvider.getSession();
		Transaction tx = session.beginTransaction();
		try {
			
			chargingDetailsList = session.createQuery("from ChargingDetails C where C.payId= :payId and C.acquirerName=:acquirerName and C.status='ACTIVE' and C.transactionType='SALE'")
									.setParameter("payId", payId).setParameter("acquirerName", acquirer).setCacheable(true)
									.getResultList();
			
			tx.commit();
		} catch (ObjectNotFoundException objectNotFound) {
			handleException(objectNotFound,tx);
		} catch (HibernateException hibernateException) {
			handleException(hibernateException,tx);
		} finally {
			autoClose(session);
		}
		return chargingDetailsList;
	}
	
	
	
	@SuppressWarnings("unchecked")
	public List<ChargingDetails> getAllActiveSaleChargingDetails(String payId) {
		Session session = HibernateSessionProvider.getSession();
		Transaction tx = session.beginTransaction();
		List<ChargingDetails> chargingDetailsList = new ArrayList<ChargingDetails>();
		try {
			
			
			chargingDetailsList = session.createQuery("from ChargingDetails C where C.payId=:payId and C.status='ACTIVE' and C.transactionType = '"+ TransactionType.SALE +"' and C.merchantTDR >= 0")
									.setParameter("payId", payId).setCacheable(true)
									.getResultList();
			
			tx.commit();
		} catch (ObjectNotFoundException objectNotFound) {
			handleException(objectNotFound,tx);
		} catch (HibernateException hibernateException) {
			handleException(hibernateException,tx);
		} finally {
			autoClose(session);
		}
		return chargingDetailsList;
	}

	@SuppressWarnings("unchecked")
	public List<ChargingDetails> getAllActiveChargingDetails() {
		List<ChargingDetails> chargingDetailsList = new ArrayList<ChargingDetails>();
		Session session = HibernateSessionProvider.getSession();
		Transaction tx = session.beginTransaction();
		try {
						
			chargingDetailsList = session.createQuery("from ChargingDetails C where C.status='ACTIVE' and C.merchantTDR >= 0")
									.getResultList();
			tx.commit();
		} catch (ObjectNotFoundException objectNotFound) {
			handleException(objectNotFound,tx);
		} catch (HibernateException hibernateException) {
			handleException(hibernateException,tx);
		} finally {
			autoClose(session);
		}
		return chargingDetailsList;
	}

	public List<ChargingDetails> findBankList(String payId){
		List<ChargingDetails> bankList = new ArrayList<ChargingDetails>();
		Session session = HibernateSessionProvider.getSession();
		Transaction tx = session.beginTransaction();

		try {
			
			String sqlQuery = "select * from ChargingDetails where payId=:payId and paymentType='NET_BANKING' and status='ACTIVE'";
			bankList = session.createNativeQuery(sqlQuery, ChargingDetails.class)
								   .setParameter("payId", payId)
								   .getResultList();
			
			tx.commit();
		} catch (ObjectNotFoundException objectNotFound) {
			handleException(objectNotFound,tx);
		} catch (HibernateException hibernateException) {
			handleException(hibernateException,tx);
		} finally {
			autoClose(session);
		}
		return bankList;
	}
	
	public List<ChargingDetails> getPendingChargingDetailList(){
		List<ChargingDetails> pendingChargingDetailsList = new ArrayList<ChargingDetails>();
		Session session = HibernateSessionProvider.getSession();
		Transaction tx = session.beginTransaction();
		try {
			
			String sqlQuery = "select * from ChargingDetails where status='PENDING'";
			pendingChargingDetailsList = session.createNativeQuery(sqlQuery, ChargingDetails.class)
								   .getResultList();
			
			tx.commit();
		} catch (ObjectNotFoundException objectNotFound) {
			handleException(objectNotFound,tx);
		} catch (HibernateException hibernateException) {
			handleException(hibernateException,tx);
		} finally {
			autoClose(session);
		}
		return pendingChargingDetailsList;
	}

	public List<ChargingDetails> findCardList(String payId){
		List<ChargingDetails> bankList = new ArrayList<ChargingDetails>();
		Session session = HibernateSessionProvider.getSession();
		Transaction tx = session.beginTransaction();
		try {
			
			String sqlQuery = "select * from ChargingDetails where payId=:payId and (paymentType='CREDIT_CARD' or paymentType='DEBIT_CARD') and status='ACTIVE'";

			bankList = session.createNativeQuery(sqlQuery, ChargingDetails.class).setParameter("payId", payId).getResultList();
			tx.commit();
		} catch (ObjectNotFoundException objectNotFound) {
			handleException(objectNotFound,tx);
		} catch (HibernateException hibernateException) {
			handleException(hibernateException,tx);
		} finally {
			autoClose(session);
		}
		return bankList;
	}
	
	public  ChargingDetails findPendingChargingDetail (MopType mopType , PaymentType paymentType , TransactionType transactionType , String acquirerName , String currency,
			String payId){
		
		ChargingDetails chargingDetails = null;	
		Session session = HibernateSessionProvider.getSession();
		Transaction tx = session.beginTransaction();
		try {
			
			chargingDetails = (ChargingDetails) session
					.createQuery("from ChargingDetails CD where CD.mopType = :mopType and CD.transactionType =:transactionType and CD.acquirerName = :acquirerName and "
							+ "CD.payId = :payId and CD.paymentType = :paymentType and CD.currency = :currency and CD.status = 'PENDING'")
					.setParameter("payId", payId).setParameter("paymentType", paymentType)
					.setParameter("mopType", mopType)
					.setParameter("transactionType", transactionType)
					.setParameter("acquirerName", acquirerName)
					.setParameter("currency", currency)
					.setParameter("paymentType", paymentType).setCacheable(true)
					.uniqueResult();
			tx.commit();
		} catch (ObjectNotFoundException objectNotFound) {
			handleException(objectNotFound,tx);
		} catch (HibernateException hibernateException) {
			handleException(hibernateException,tx);
		} finally {
			autoClose(session);
			// return responseUser;
		}
		return chargingDetails;
		
	}
	
	
	public  ChargingDetails findActiveChargingDetail (MopType mopType , PaymentType paymentType , TransactionType transactionType , String acquirerName , String currency,
			String payId){
		
		ChargingDetails chargingDetails = null;
		Session session = HibernateSessionProvider.getSession();
		Transaction tx = session.beginTransaction();
		try {
			
			chargingDetails = (ChargingDetails) session
					.createQuery("from ChargingDetails CD where CD.mopType = :mopType and CD.transactionType =:transactionType and CD.acquirerName = :acquirerName and "
							+ "CD.payId = :payId and CD.paymentType = :paymentType and CD.currency = :currency and CD.status = 'ACTIVE'")
					.setParameter("payId", payId).setParameter("paymentType", paymentType)
					.setParameter("mopType", mopType)
					.setParameter("transactionType", transactionType)
					.setParameter("acquirerName", acquirerName)
					.setParameter("currency", currency)
					.setParameter("paymentType", paymentType).setCacheable(true)
					.uniqueResult();
			tx.commit();
		} catch (ObjectNotFoundException objectNotFound) {
			handleException(objectNotFound,tx);
		} catch (HibernateException hibernateException) {
			handleException(hibernateException,tx);
		} finally {
			autoClose(session);
			// return responseUser;
		}
		return chargingDetails;
	}
	
	
	public boolean getruleenginval(String Acquirer , String Mop, String Payment ,String payId) {
		BigInteger count= new BigInteger("0");
		Session session = HibernateSessionProvider.getSession();
		Transaction tx = session.beginTransaction();
		try {
			logger.info("Start in Charging Detial"+ Acquirer+Mop+Payment+payId);
			String sqlQuery = "select COUNT(*) from ChargingDetails where acquirerName=:Acquirer and payId=:payId and mopType =:Mop and paymentType =:Payment  ;";
			count = (BigInteger) session.createNativeQuery(sqlQuery)
											 .setParameter("payId", payId)
											 .setParameter("Acquirer", Acquirer)
											 .setParameter("Mop", Mop)
											 .setParameter("Payment", Payment)
											 .getSingleResult();
			tx.commit();
			logger.info("cout in Charging Detial"+count);
			if(count.intValue() > 0){
				return true;
			}else{
				return false;
			}	
		} catch (ObjectNotFoundException objectNotFound) {
			handleException(objectNotFound,tx);
		} catch (HibernateException hibernateException) {
			handleException(hibernateException,tx);
		} finally {
			autoClose(session);
		}
		return false;	
	}
	
	
	
	
	public Double getBankTDR(String acquirerName,String mopType,String paymentType,String transactionType) throws HibernateException, Exception{
		Double result=null;
		Session session = HibernateSessionProvider.getSession();
		Transaction tx = session.beginTransaction();			
			String sqlQuery = "select bankTDR from ChargingDetails where acquirerName=:acquirerName and mopType=:mopType and paymentType=:paymentType and transactionType=:transactionType";
			result=(Double)session.createNativeQuery(sqlQuery)
					.setParameter("acquirerName", acquirerName)
											.setParameter("mopType", mopType)
											.setParameter("paymentType", paymentType)
											.setParameter("transactionType", transactionType)
											 .getSingleResult();
		return result;	
	}
	
	
	//Added By Sweety
	@SuppressWarnings("unchecked")
	public List<Object[]> findDistinctMopDetails(String payId) {
		Session session = HibernateSessionProvider.getSession();
		try {
			return session.createNativeQuery(
					"SELECT distinct mopType, paymentType FROM ChargingDetails where PayId=:PayId and status='ACTIVE'")
					.setParameter("PayId", payId).getResultList();
		} finally {
			autoClose(session);
		}
	}

	@SuppressWarnings("unchecked")
	public List<String> findPaymentTypeByPayId(String payId) {
		Session session = HibernateSessionProvider.getSession();
		try {
			logger.info("findPaymentTypeByPayId:: payId = {}", payId);
			return session.createNativeQuery("SELECT distinct paymentType FROM ChargingDetails where PayId=:PayId")
					.setParameter("PayId", payId).getResultList();

		} finally {
			autoClose(session);
		}
	}
	
	@SuppressWarnings("unchecked")
	public List<String> findmopbypaymenttype(String payId, String paytype) {
		Session session = HibernateSessionProvider.getSession();
		try {
			logger.info("findmopbypaymenttype:: payId = {}, paymentType={}", payId, paytype);
			return session.createNativeQuery(
					"SELECT distinct mopType FROM ChargingDetails where PayId=:PayId and paymentType=:paytype and status='ACTIVE'")
					.setParameter("PayId", payId).setParameter("paytype", paytype).getResultList();
		} finally {
			autoClose(session);
		}
	}
}
