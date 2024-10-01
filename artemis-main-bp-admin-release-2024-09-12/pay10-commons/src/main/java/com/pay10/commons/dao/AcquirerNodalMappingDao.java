package com.pay10.commons.dao;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.pay10.commons.entity.AcqurerNodalMapping;
import com.pay10.commons.entity.BankList;


@Component
public class AcquirerNodalMappingDao extends HibernateAbstractDao{
	@Autowired
	private BankListDao bankListDao;
	private static Logger logger = LoggerFactory.getLogger(AcquirerNodalMappingDao.class.getName());
	@SuppressWarnings({ "rawtypes", "unchecked" })
	private List<AcqurerNodalMapping> findAll(int limit,int skip){
		Session session = HibernateSessionProvider.getSession();
		Query query=session.createQuery("FROM AcqurerNodalMapping");
		query.setFirstResult(skip);
		query.setMaxResults(limit);
		logger.info("Query for findAll : "+query.getQueryString());
		return query.getResultList();
		
		
		
		
		
	}
	
	private int findTotalCount(){
		Session session = HibernateSessionProvider.getSession();
		Query query=session.createQuery("FROM AcqurerNodalMapping");
		logger.info("Query for findTotalCount : "+query.getQueryString());
		return query.getResultList().size();
	}
	
	private void save(AcqurerNodalMapping acqurerNodalMapping) {
		super.save(acqurerNodalMapping);
	}
	
	private boolean getListFromAcquirerAndNodalBank(String acquirer,String nodal) {
		Session session = HibernateSessionProvider.getSession();
		String fetchQuery="FROM AcqurerNodalMapping WHERE acquirer='"+acquirer+"' AND nodal='"+nodal+"' AND activeFlag="+true+"";
		Query query=session.createQuery(fetchQuery);
		List list=query.list();
		logger.info("getListFromAcquirerAndNodalBank Query : "+fetchQuery +"Result Size : "+list.size());
		if(list.size()>0) {
			return false;
		}else {
			return true;
		}
	}
	private List<AcqurerNodalMapping> getListFromAcquirer(String acquirer) {
		Session session = HibernateSessionProvider.getSession();
		String fetchQuery="FROM AcqurerNodalMapping WHERE acquirer='"+acquirer+"'AND activeFlag="+true+"";
		Query query=session.createQuery(fetchQuery);
		List list=query.list();
		logger.info("getListFromAcquirerAndNodalBank Query : "+fetchQuery +"Result Size : "+list.size());
		if(list.size()>0) {
			List<AcqurerNodalMapping> acqurerNodalMappings=(List<AcqurerNodalMapping>)list;
			return acqurerNodalMappings;
		}else {
			return null;
		}
	}
	private AcqurerNodalMapping getListFromAcquirerBank(String acquirer,String date) {
		Session session = HibernateSessionProvider.getSession();
		String fetchQuery="FROM AcqurerNodalMapping WHERE acquirer='"+acquirer+"'AND startAt<='"+date+"' ORDER BY startAt DESC";
		Query query=session.createQuery(fetchQuery);
		List list=query.list();
		logger.info("getListFromAcquirerAndNodalBank Query : "+fetchQuery +"Result Size : "+list.size());
		if(list.size()>0) {
			AcqurerNodalMapping acqurerNodalMappings=(AcqurerNodalMapping)list.get(0);
			return acqurerNodalMappings;
		}else {
			return null;
		}
	}
	private int inactiveTheEntries(List<Long>list,String updateBy,Date updateAt) {
		 Session session = HibernateSessionProvider.getSession();
		 Transaction transaction=session.beginTransaction();
		    String updateQuery = "UPDATE AcqurerNodalMapping SET activeFlag = :activeFlag, updatedAt = :updateAt, updatedBy = :updateBy, endDate = :endDate WHERE sno IN (:list)";
		    Query query = session.createQuery(updateQuery);
		    query.setParameter("activeFlag", false);
		    query.setParameter("updateAt", updateAt);
		    query.setParameter("updateBy", updateBy);
		    query.setParameter("endDate", updateAt);
		    query.setParameterList("list", list);
		    
		    int q= query.executeUpdate();
		    transaction.commit();
		    return q;
	}
	private int updateEntry(long id,String updateBy) {
		 Session session = HibernateSessionProvider.getSession();
		 Transaction transaction=session.beginTransaction();
		    String updateQuery = "UPDATE AcqurerNodalMapping SET status = :status, updatedBy =:updatedBy ,updatedAt =:updatedAt WHERE sno = :sno";
		    Query query = session.createQuery(updateQuery);
		    query.setParameter("status", "Approved");
		    query.setParameter("updatedBy", updateBy);
		    query.setParameter("updatedAt", new Date());
		    query.setParameter("sno", id);
		   
		    
		    int q= query.executeUpdate();
		    transaction.commit();
		    return q;
	}
	public int getAllSize() {
		return findTotalCount();
	}
	
	public List<AcqurerNodalMapping> getAllAcquirerNodalMapping(int limit,int skip) {
		return findAll(limit,skip);
	}
	
	public String saveAcqurerNodalMapping(AcqurerNodalMapping acqurerNodalMapping) {
		String response="Someting Went Wrong Please Try After Some Time";
//		if bank with acquirer exsist and the acquire is changed then update the currecnt mapped entry and insert a new one.
		
//		if a acquire is mapped with a the as parameter bank then give validation.
		if(getListFromAcquirerAndNodalBank(acqurerNodalMapping.getAcquirer(), acqurerNodalMapping.getNodal())) {
			List<AcqurerNodalMapping> acqurerNodalMappings=getListFromAcquirer(acqurerNodalMapping.getAcquirer());
			List<Long>list=new ArrayList<>();
			if (acqurerNodalMappings!=null) {
				for (AcqurerNodalMapping acqurerNodalMapping2 : acqurerNodalMappings) {
					list.add(acqurerNodalMapping2.getSno());
				}
				int totalUpdatedEntries=inactiveTheEntries(list,  acqurerNodalMapping.getCreateBy(),acqurerNodalMapping.getCreateAt());
				BankList bankList=bankListDao.getAllByAccountNumber(acqurerNodalMapping.getNodal()).get(0);
				acqurerNodalMapping.setBankName(bankList.getBankName());
				acqurerNodalMapping.setIfscCode(bankList.getIfscCode());
				acqurerNodalMapping.setStatus("UnApproved");
				save(acqurerNodalMapping);
				response="SucessFully Save";
			}else {
				BankList bankList=bankListDao.getAllByAccountNumber(acqurerNodalMapping.getNodal()).get(0);
				acqurerNodalMapping.setBankName(bankList.getBankName());
				acqurerNodalMapping.setIfscCode(bankList.getIfscCode());
				acqurerNodalMapping.setStatus("UnApproved");
				save(acqurerNodalMapping);
				response="SucessFully Save";
			}
		}else {
			response="Acquirer Already Mapped With Same Nodal Please Change the Acquirer";
		}
		
		return response;
	}
	public String update(String id,String updateBy) { 
		String response="Failed To Approve";
		int i=updateEntry(Long.valueOf(id),updateBy);
		if(i>0) {
			response="Successfully Approve";
		}else {
			response="Failed To Approve";
		}
		return response;
	}
	public AcqurerNodalMapping findListFromAcquirerBank(String acquirer,String date) {
		return getListFromAcquirerBank(acquirer,date);
	}
}
