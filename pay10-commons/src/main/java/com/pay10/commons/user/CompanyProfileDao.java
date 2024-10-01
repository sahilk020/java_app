package com.pay10.commons.user;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.persistence.NoResultException;

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
import com.pay10.commons.util.CrmFieldConstants;
import com.pay10.commons.util.CrmFieldType;
import com.pay10.commons.util.ModeType;
import com.pay10.commons.util.SubAdmin;
import com.pay10.commons.util.Tenant;
import com.pay10.commons.util.UserStatusType;



@Component("companyProfileDao")
public class CompanyProfileDao  extends HibernateAbstractDao {
	private static Logger logger = LoggerFactory.getLogger(CompanyProfileDao.class.getName());
	
	public CompanyProfileDao() {
		super();
	}
	
	private static final String getCompleteCompanyProfileWithEmailIdQuery = "from CompanyProfile U where U.emailId = :emailId";
	private static final String getCompanyProfileTableWithEmailId = "select new map (emailId as emailId, accHolderName as accHolderName, "
			+ "accountNo as accountNo, companyGstNo as companyGstNo,"
			+ "address as address, bankName as bankName, branchName as branchName, cin as cin, comments as comments, companyName as companyName, "
			+ "mobile as mobile, telephoneNo as telephoneNo, fax as fax, address as address,"
			+ "city as city, state as state, country as country, postalCode as postalCode, ifscCode as ifscCode, currency as currency, panCard as panCard, "
			+ "panName as panName)"
			+ "from CompanyProfile U where U.emailId = :emailId1";

	private Connection getConnection() throws SQLException {
		return DataAccessObject.getBasicConnection();
	}
	public void create(CompanyProfile cp) throws DataAccessLayerException {
		super.save(cp);
	}
	public void delete(CompanyProfile cp) throws DataAccessLayerException {
		super.delete(cp);
	}

	public CompanyProfile find(Long tenantId) throws DataAccessLayerException {
		return (CompanyProfile) super.find(CompanyProfile.class, tenantId);
	}

	public CompanyProfile find(String name) throws DataAccessLayerException {
		return (CompanyProfile) super.find(CompanyProfile.class, name);
	}
	
	@SuppressWarnings("rawtypes")
	public List findAll() throws DataAccessLayerException {
		return super.findAll(CompanyProfile.class);
	}

	public void update(CompanyProfile cp) throws DataAccessLayerException {
		super.saveOrUpdate(cp);
	}
	
	public CompanyProfile getCompanyProfileByEmailId(String emailId) {
		CompanyProfile responseCp = null;
		Session session = HibernateSessionProvider.getSession();
		Transaction tx = session.beginTransaction();

		try {
			List<CompanyProfile> cps =  session.createQuery("from CompanyProfile cp where cp.emailId = :emailId").setParameter("emailId", emailId).getResultList();
			
				for (CompanyProfile cp : cps) {
					responseCp = cp;
					break;
				}

			tx.commit();
			return responseCp;
		} catch (ObjectNotFoundException objectNotFound) {
			handleException(objectNotFound,tx);
		} catch (HibernateException hibernateException) {
			handleException(hibernateException,tx);
		} 
		finally {
			autoClose(session);
		}
		return responseCp;
	}
	
	public CompanyProfile getCompanyProfileByTenantId(String tenantId) {
		CompanyProfile responseCp = null;
		Session session = HibernateSessionProvider.getSession();
		Transaction tx = session.beginTransaction();

		try {
			List<CompanyProfile> cps =  session.createQuery("from CompanyProfile cp where cp.tenantId = :tenantId").setParameter("tenantId", Long.parseLong(tenantId)).getResultList();
			
				for (CompanyProfile cp : cps) {
					responseCp = cp;
					break;
				}

			tx.commit();
			return responseCp;
		} catch (ObjectNotFoundException objectNotFound) {
			handleException(objectNotFound,tx);
		} catch (HibernateException hibernateException) {
			handleException(hibernateException,tx);
		} 
		finally {
			autoClose(session);
		}
		return responseCp;
	}
	
	public CompanyProfile getCompanyProfileClass(String emailId) {
		CompanyProfile responseCompanyProfile = new CompanyProfile();
		Map<String, Object> companyDetailsMap = getCompanyProfileObjMap(emailId);
		if (null == companyDetailsMap) {
			return null;
		} else {
			responseCompanyProfile.setCompanyGstNo((String) companyDetailsMap
					.get(CrmFieldType.COMPANY_GST_NUMBER.getName()));
			responseCompanyProfile.setCompanyName((String) companyDetailsMap
					.get(CrmFieldType.COMPANY_NAME.getName()));
			responseCompanyProfile.setEmailId((String) companyDetailsMap
					.get(CrmFieldType.EMAILID.getName()));
			responseCompanyProfile.setAccHolderName((String) companyDetailsMap
					.get(CrmFieldType.ACC_HOLDER_NAME.getName()));
			responseCompanyProfile.setAccountNo((String) companyDetailsMap
					.get(CrmFieldType.ACCOUNT_NO.getName()));
			responseCompanyProfile.setAddress((String) companyDetailsMap
					.get(CrmFieldType.ADDRESS.getName()));
			responseCompanyProfile.setBankName((String) companyDetailsMap
					.get(CrmFieldType.BANK_NAME.getName()));
			responseCompanyProfile.setBranchName((String) companyDetailsMap
					.get(CrmFieldType.BRANCH_NAME.getName()));
			responseCompanyProfile.setCin((String) companyDetailsMap.get(CrmFieldType.CIN
					.getName()));
			responseCompanyProfile.setCity((String) companyDetailsMap.get(CrmFieldType.CITY
					.getName()));
			responseCompanyProfile.setCompanyName((String) companyDetailsMap
					.get(CrmFieldType.COMPANY_NAME.getName()));
			responseCompanyProfile.setCountry((String) companyDetailsMap
					.get(CrmFieldType.COUNTRY.getName()));
			responseCompanyProfile.setCurrency((String) companyDetailsMap
					.get(CrmFieldType.CURRENCY.getName()));
			responseCompanyProfile.setFax((String) companyDetailsMap.get(CrmFieldType.FAX
					.getName()));
			responseCompanyProfile.setIfscCode((String) companyDetailsMap
					.get(CrmFieldType.IFSC_CODE.getName()));
			responseCompanyProfile.setMobile((String) companyDetailsMap
					.get(CrmFieldType.MOBILE.getName()));
			responseCompanyProfile.setPanCard((String) companyDetailsMap
					.get(CrmFieldType.PANCARD.getName()));
			responseCompanyProfile.setPanName((String) companyDetailsMap
					.get(CrmFieldType.PANNAME.getName()));
			responseCompanyProfile.setPostalCode((String) companyDetailsMap
					.get(CrmFieldType.POSTALCODE.getName()));
			responseCompanyProfile.setState((String) companyDetailsMap
					.get(CrmFieldType.STATE.getName()));
			responseCompanyProfile.setTelephoneNo((String) companyDetailsMap
					.get(CrmFieldType.TELEPHONE_NO.getName()));
			
			responseCompanyProfile.setPgUrl((String) companyDetailsMap
					.get(CrmFieldType.PG_URL.getName()));
			responseCompanyProfile.setCompanyUrl((String) companyDetailsMap
					.get(CrmFieldType.COMPANY_URL.getName()));
			responseCompanyProfile.setTenantNumber((String) companyDetailsMap
					.get(CrmFieldType.TENANT_NUMBER.getName()));
			
		}
		return responseCompanyProfile;
	}

	@SuppressWarnings("unchecked")
	public Map<String, Object> getCompanyProfileObjMap(String emailId){

		Map<String, Object> companyProfileDetailsMap = null;
		Object CompanyProfileObject = getCompanyProfileObj(emailId);
		
		if(null!=CompanyProfileObject){
			companyProfileDetailsMap = (Map<String, Object>) CompanyProfileObject;
		}
		return companyProfileDetailsMap;
	}
	
	// this method I need to change the create query for emailId
	
	protected Object getCompanyProfileObj(String emailId){
		Session session = HibernateSessionProvider.getSession();
		Transaction tx = session.beginTransaction();

		Object companyProfileObject = null;
		try {
			companyProfileObject = session.createQuery(getCompleteCompanyProfileWithEmailIdQuery)			
									 .setParameter("emailId", emailId).setCacheable(true)
									 .getSingleResult();			 			
			tx.commit();			
		} catch (NoResultException noResultException){
			return null;
		} catch (ObjectNotFoundException objectNotFound) {
			handleException(objectNotFound,tx);
		} catch (HibernateException hibernateException) {
			handleException(hibernateException,tx);
		} finally {
			autoClose(session);
		}
		return companyProfileObject;
	}
	
	public long getTenantIdByDomainName(String domainName) {
		long tenantId = 0L;
		CompanyProfile responseCp = null;
		Session session = HibernateSessionProvider.getSession();
		Transaction tx = session.beginTransaction();

		try {
			List<CompanyProfile> cps =  session.createQuery("from CompanyProfile cp where cp.pgUrl = :pgUrl").setParameter("pgUrl", domainName).getResultList();
			
				for (CompanyProfile cp : cps) {
					responseCp = cp;
					break;
				}

			tx.commit();
			if(responseCp == null) {
				tenantId = 0L;
			}else {
				tenantId = responseCp.getTenantId();	
			}
		} catch (ObjectNotFoundException objectNotFound) {
			handleException(objectNotFound,tx);
		} catch (HibernateException hibernateException) {
			handleException(hibernateException,tx);
		} 
		finally {
			autoClose(session);
		}
	
		return tenantId;
	}
	
	// get list of tenants
	public List<Tenant> getAllTenants() {

		Session session = HibernateSessionProvider.getSession();
		Transaction tx = session.beginTransaction();
		List<Tenant> tenantsList = new ArrayList<Tenant>();
		try {
			List<Object[]> tenantListRaw = session.createQuery("Select tenantId, companyName, mobile, emailId, pgUrl, tenantStatus from CompanyProfile cp where cp.emailId != null").getResultList();

			for(Object[] objects : tenantListRaw) { 
				Tenant tenant = new Tenant();
				tenant.setTenantId( objects[0].toString());
				tenant.setTenantCompanyName((String) objects[1]);
				tenant.setTenantMobile((String) objects[2]);
				tenant.setTenantEmailId((String) objects[3]);
				tenant.setTenantPgUrl((String) objects[4]);
				tenant.setTenantStatus((String) objects[5]);
				
				/*
				 * if(((UserStatusType) objects[5]).equals(UserStatusType.ACTIVE)) {
				 * subAdmin.setAgentIsActive(true); } else if(((UserStatusType)
				 * objects[5]).equals(UserStatusType.PENDING)) {
				 * subAdmin.setAgentIsActive(false); }
				 */
				tenantsList.add(tenant);
			}
			tx.commit();
			return tenantsList;
		} catch (ObjectNotFoundException objectNotFound) {
			handleException(objectNotFound,tx);
		} catch (HibernateException hibernateException) {
			handleException(hibernateException,tx);
		} 
		finally {
			autoClose(session);
		}
		return tenantsList;
	
	}
	public List<CompanyProfileUi> getAllCompanyProfileList() {


		Session session = HibernateSessionProvider.getSession();
		Transaction tx = session.beginTransaction();
		List<CompanyProfileUi> cpuList = new ArrayList<CompanyProfileUi>();
		try {
			List<Object[]> tenantListRaw = session.createQuery("Select tenantId, companyName, tenantNumber from CompanyProfile cp where cp.emailId != null").getResultList();

			for(Object[] objects : tenantListRaw) { 
				CompanyProfileUi cp = new CompanyProfileUi();
				cp.setTenantId( objects[0].toString());
				cp.setCompanyName((String) objects[1]);
				cp.setTenantNumber((String) objects[2]);
				
				cpuList.add(cp);
			}
			tx.commit();
			return cpuList;
			
		} catch (ObjectNotFoundException objectNotFound) {
			handleException(objectNotFound,tx);
		} catch (HibernateException hibernateException) {
			handleException(hibernateException,tx);
		} 
		finally {
			autoClose(session);
		}
		
		return cpuList;
	
	}
	
}
