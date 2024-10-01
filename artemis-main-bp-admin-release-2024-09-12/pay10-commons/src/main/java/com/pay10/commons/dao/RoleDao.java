package com.pay10.commons.dao;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import org.hibernate.HibernateException;
import org.hibernate.ObjectNotFoundException;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.pay10.commons.exception.DataAccessLayerException;
import com.pay10.commons.user.Role;
import com.pay10.commons.user.Segment;
import com.pay10.commons.user.SegmentInfo;
import com.pay10.commons.user.UserGroup;
import com.pay10.commons.user.UserType;

@Service
public class RoleDao extends HibernateAbstractDao {

	@Autowired
	private RoleWiseMenuDao roleWiseMenuDao;

	Logger logger= LoggerFactory.getLogger(RoleDao.class.getName());
	public void save(Role role) throws DataAccessLayerException {
		super.save(role);
	}

	public long saveAndReturn(Role role) throws DataAccessLayerException {
		super.save(role);
		String hql = "SELECT LAST_INSERT_ID()";
		Session session = HibernateSessionProvider.getSession();
		BigInteger lastId = (BigInteger) (session.createNativeQuery(hql).getSingleResult());
		return lastId.longValue();
	}

	@SuppressWarnings("unchecked")
	public List<Role> getRoles(String createdBy) {
		try(Session session=HibernateSessionProvider.getSession()){
			Query query=session.createQuery("FROM Role where createdBy=:createdBy",Role.class)
					.setParameter("createdBy",createdBy);
			List<Role>roles=query.getResultList();
			if(!roles.isEmpty()){
				return roles.stream().sorted(Comparator.comparingLong(Role::getId)).collect(Collectors.toList());
			}
			else{
				return roles;
			}
		}
 	}
	 public long getIdByName(String roleName){
			logger.info("Role Name in DAO "+roleName);
		try(Session session=HibernateSessionProvider.getSession()){
			Query query=session.createQuery("FROM Role where roleName=:roleName")
					.setParameter("roleName",roleName);

			List<Role> role=query.getResultList();
			if(!role.isEmpty()) {
				logger.info("Get ID BY Name " + role.get(0).getId());
				return role.get(0).getId();
			}
			else{
				return 1L;
			}
		}
	 }

	@SuppressWarnings("unchecked")
	public List<Role> getActiveRoles() {
		Session session = HibernateSessionProvider.getSession();
		String query = "FROM Role R where R.isActive=true";
		List<Role> rolesByGroup = session.createQuery(query).getResultList();
		List<Role> displayRoles = new ArrayList<>();
		rolesByGroup.forEach(role -> {
			if (roleWiseMenuDao.getbyRoleId(role.getId()).size() > 0) {
				displayRoles.add(role);
			}
		});
		return displayRoles;
	}
	
	@SuppressWarnings("unchecked")
	public  List<Role> getActiveRolesByGroupId(int groupId) {
		Session session = HibernateSessionProvider.getSession();
		String query = "FROM Role R where R.isActive=true and userGroup.id="+groupId;
		
		System.out.println(query);
		List<Role> rolesByGroup = session.createQuery(query).getResultList();
		List<Role> displayRoles = new ArrayList<>();
		rolesByGroup.forEach(role -> {
			if (roleWiseMenuDao.getbyRoleId(role.getId()).size() > 0) {
				displayRoles.add(role);
			}
		});
		return displayRoles;
	}

	public Role getRole(long id) {
		return (Role) super.find(Role.class, id);
	}

//	public void update(Role role) {
//		super.saveOrUpdate(role);
//	}
	
	public boolean updateNew(Role role,Session session) {
		return super.saveOrUpdateNew(role,session);
	}

	public void delete(long id) {
		super.delete(getRole(id));
	}

	@SuppressWarnings("unchecked")
	public List<Role> getByGroupId(long groupId) {
		Session session = HibernateSessionProvider.getSession();
		String query = "FROM Role R where R.userGroup.id=:groupId and R.isActive=true";
		List<Role> rolesByGroup = session.createQuery(query).setParameter("groupId", groupId).getResultList();
		List<Role> displayRoles = new ArrayList<>();
		rolesByGroup.forEach(role -> {
			if (roleWiseMenuDao.getbyRoleId(role.getId()).size() > 0) {
				displayRoles.add(role);
			}
		});
		return displayRoles;
	}
	
	@SuppressWarnings("unchecked")
	public List<Segment> getAllSegment() {
		Session session = HibernateSessionProvider.getSession();
		String query = "FROM Segment";
		List<Segment> segmentByGroup = session.createQuery(query).getResultList();
		List<Segment> displaySegments = new ArrayList<>();
		segmentByGroup.forEach(segment -> {
			displaySegments.add(segment);
		});
		return displaySegments;
	}
	public List<Role> getRoleByEmailId(String emailId){
		try(Session session=HibernateSessionProvider.getSession()){
			return session.createQuery("FROM Role where createdBy=:createdBy and isActive=true", Role.class).setParameter("createdBy",emailId).getResultList();
		}
	}
	
	
	@SuppressWarnings("unchecked")
	public List<SegmentInfo> getSegmentInfo(String segmentId) {
		Session session = HibernateSessionProvider.getSession();
		String query = null;
		List<SegmentInfo> segmentByGroup = null;
		if(segmentId.equalsIgnoreCase("ALL")) {
			query = "FROM SegmentInfo S where S.userType =:userType order by registrationDate desc";
			segmentByGroup = session.createQuery(query)
					.setParameter("userType", "MERCHANT").getResultList();
		}else {
			query = "FROM SegmentInfo S where S.userType =:userType and S.segment=:segmentId order by registrationDate desc";
			segmentByGroup = session.createQuery(query)
					.setParameter("userType", "MERCHANT")
					.setParameter("segmentId", segmentId).getResultList();
		}
		return segmentByGroup;
	}
	
	@SuppressWarnings("unchecked")
	public int updateSegment(String segmentName, String emailId, String payId) {
		Session session = HibernateSessionProvider.getSession();
		Query q=session.createQuery("update SegmentInfo set segment=:segmentName where emailId= :emailId and payId=:payId");  
		q.setParameter("segmentName",segmentName);  
		q.setParameter("emailId",emailId);
		q.setParameter("payId",payId);  
		int status=q.executeUpdate();  
		return status;
	}
	
	
	@SuppressWarnings("unchecked")
	public int updateSegments(String segmentName, String emailId, String payId) {
		
	 Session session = HibernateSessionProvider.getSession();
     Transaction tx = session.beginTransaction();
     try {
         int resultCnt = session.createQuery("update SegmentInfo S set S.segment=:segmentName where S.emailId = :emailId and S.payId = :payId ")
                 .setParameter("segmentName", segmentName)
                 .setParameter("emailId", emailId)
                 .setParameter("payId", payId).executeUpdate();
        tx.commit();
        return resultCnt;
     } catch (ObjectNotFoundException objectNotFound) {
         handleException(objectNotFound,tx);
     } catch (HibernateException hibernateException) {
         handleException(hibernateException,tx);
     } finally {
         autoClose(session);
     }
     return 0;
     
	}

	public List<Role> findAll(){
		try(Session session=HibernateSessionProvider.getSession()){
			Query query=session.createQuery("FROM Role",Role.class);
			List<Role> roleList=new ArrayList<>();
			return roleList=query.getResultList();
		}
 	}
	public Role getRoleByUserType(UserType userType) {
		Session session = HibernateSessionProvider.getSession();
		Transaction tx = session.beginTransaction();

		Role responseUser = null;
		try {
			responseUser = (Role) session.createQuery("from Role U where U.roleName = '" + userType.name() + "'")
					.getResultList().get(0);
			
			tx.commit();

			return responseUser;
		} catch (ObjectNotFoundException objectNotFound) {
			handleException(objectNotFound, tx);
		} catch (HibernateException hibernateException) {
			handleException(hibernateException, tx);
		} finally {
			autoClose(session);
		}
		return responseUser;
	}

}