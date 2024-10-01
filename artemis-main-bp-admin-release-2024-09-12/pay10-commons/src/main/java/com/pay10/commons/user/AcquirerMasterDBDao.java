package com.pay10.commons.user;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javax.persistence.NoResultException;

import org.hibernate.ObjectNotFoundException;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.springframework.stereotype.Component;

import com.pay10.commons.dao.HibernateAbstractDao;
import com.pay10.commons.dao.HibernateSessionProvider;

@Component("acquirerMasterDBDao1")
public class AcquirerMasterDBDao extends HibernateAbstractDao {

	private static final String getCompleteAcquireDetails = "from AcquirerMasterDB";

	private List<AcquirerMasterDB> acquirerMasterDBs = new ArrayList<AcquirerMasterDB>();

	@SuppressWarnings("unchecked")
	public List<AcquirerMasterDB> getAcquirerMasterDetail() {
		List<AcquirerMasterDB> acquirerMasterDBs = new ArrayList<AcquirerMasterDB>();
		Transaction tx = null;
		try (Session session = HibernateSessionProvider.getSession();) {
			tx = session.beginTransaction();
			acquirerMasterDBs = (List<AcquirerMasterDB>) session.createQuery(getCompleteAcquireDetails)
					.setCacheable(true).getResultList();

			tx.commit();
			session.close();
			return acquirerMasterDBs;
		} catch (NoResultException noResultException) {
			noResultException.printStackTrace();
		} catch (ObjectNotFoundException objectNotFound) {
			handleException(objectNotFound, tx);
			objectNotFound.printStackTrace();
		}
		return acquirerMasterDBs;
	}

	public List<AcquirerMasterDB> getAcquirerMasterDBsWithAliasName() {
		return getAcquirerMasterDetail().stream().map(acquire -> {
			acquire.setName(acquire.getName() + "-" + acquire.getAliasName());
			return acquire;
		}).collect(Collectors.toList());
	}

	public String getAcquirerNameByCode(String acquirerFrom) {
		if (acquirerMasterDBs != null && acquirerMasterDBs.size() > 0) {
			return acquirerMasterDBs.stream().filter(acquirer -> acquirer.getCode().equalsIgnoreCase(acquirerFrom))
					.findFirst().get().getName();
		} else {
			acquirerMasterDBs = getAcquirerMasterDetail();
			return acquirerMasterDBs.stream().filter(acquirer -> acquirer.getCode().equalsIgnoreCase(acquirerFrom))
					.findFirst().get().getName();
		}
	}
	
	 @SuppressWarnings("unchecked")
	public List<AcquirerMasterDB> getAcquirerCodeByName(String acquirerName)
	    {
	        Transaction tx=null;
	        try(Session session=HibernateSessionProvider.getSession()){
	            tx= session.beginTransaction();
	            List<AcquirerMasterDB>acquirerMasterDBS=session.createQuery(getCompleteAcquireDetails+" WHERE code=:aliasName")
	                    .setParameter("aliasName",acquirerName)
	                    .getResultList();
	            return  acquirerMasterDBS;
	        }
	    }

}
