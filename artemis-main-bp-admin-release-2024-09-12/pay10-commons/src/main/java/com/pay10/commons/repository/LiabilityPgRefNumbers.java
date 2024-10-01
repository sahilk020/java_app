package com.pay10.commons.repository;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import org.hibernate.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import com.pay10.commons.dao.HibernateAbstractDao;
import com.pay10.commons.dao.HibernateSessionProvider;
import com.pay10.commons.entity.LiabilityPgRefNumbersEntity;
import com.pay10.commons.exception.DataAccessLayerException;


@Repository
public class LiabilityPgRefNumbers extends HibernateAbstractDao {
	private static Logger logger = LoggerFactory.getLogger(DMSRepository.class.getName());

	public List<LiabilityPgRefNumbersEntity> getPgRefNumbersByliabilityType(String liabilityType){
		try(Session session = HibernateSessionProvider.getSession();){
			CriteriaBuilder builder = session.getCriteriaBuilder();
		    CriteriaQuery<LiabilityPgRefNumbersEntity> cq = builder.createQuery(LiabilityPgRefNumbersEntity.class);
		    Root<LiabilityPgRefNumbersEntity> pgrefnum = cq.from(LiabilityPgRefNumbersEntity.class);
		   cq.select(pgrefnum);
		   cq.where(builder.equal(pgrefnum.get("liabilityType"), liabilityType));
		   
		   return session.createQuery(cq).getResultList();
		}catch (Exception e) {
			logger.error("Exception occur in getPgRefNumbersByliabilityType()",e);
			e.printStackTrace();
			return new ArrayList<LiabilityPgRefNumbersEntity>();
		}
	}
	
	public List<LiabilityPgRefNumbersEntity> getliabilityTypeByPgrefnum(String pgRefNum){
		try(Session session = HibernateSessionProvider.getSession();){
			CriteriaBuilder builder = session.getCriteriaBuilder();
		    CriteriaQuery<LiabilityPgRefNumbersEntity> cq = builder.createQuery(LiabilityPgRefNumbersEntity.class);
		    Root<LiabilityPgRefNumbersEntity> pgrefnum = cq.from(LiabilityPgRefNumbersEntity.class);
		   cq.select(pgrefnum);
		   cq.where(builder.equal(pgrefnum.get("pgRefnum"), pgRefNum));
		   return session.createQuery(cq).getResultList();
		}catch (Exception e) {
			logger.error("Exception occur in getliabilityTypeByPgrefnum()",e);
			e.printStackTrace();
			return new ArrayList<LiabilityPgRefNumbersEntity>();
		}
	}
	
	public void save(LiabilityPgRefNumbersEntity liabilityPgRefNumbersEntity) throws DataAccessLayerException {
		super.save(liabilityPgRefNumbersEntity);
	}
	
	
	public List<LiabilityPgRefNumbersEntity> getAllByPgrefnum(){
		try(Session session = HibernateSessionProvider.getSession();){
			CriteriaBuilder cb = session.getCriteriaBuilder();
		    CriteriaQuery<LiabilityPgRefNumbersEntity> cq = cb.createQuery(LiabilityPgRefNumbersEntity.class);
		    Root<LiabilityPgRefNumbersEntity> rootEntry = cq.from(LiabilityPgRefNumbersEntity.class);
		    CriteriaQuery<LiabilityPgRefNumbersEntity> all = cq.select(rootEntry);
		    TypedQuery<LiabilityPgRefNumbersEntity> allQuery = session.createQuery(all);
		    return allQuery.getResultList();
		}catch (Exception e) {
			logger.error("Exception occur in getAllByPgrefnum()",e);
			e.printStackTrace();
			return new ArrayList<LiabilityPgRefNumbersEntity>();
		}
	}
}
