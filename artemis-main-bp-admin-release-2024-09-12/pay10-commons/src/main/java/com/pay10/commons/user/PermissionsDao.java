package com.pay10.commons.user;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

import org.hibernate.HibernateException;
import org.hibernate.ObjectNotFoundException;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.springframework.stereotype.Component;

import com.pay10.commons.dao.HibernateAbstractDao;
import com.pay10.commons.dao.HibernateSessionProvider;
import com.pay10.commons.exception.DataAccessLayerException;

@Component
public class PermissionsDao extends HibernateAbstractDao  {
	public PermissionsDao() {
        super();
    }

	public void create(Permissions permissions) throws DataAccessLayerException {
        super.saveOrUpdate(permissions);
    }
	
	 public void delete(Permissions permissions) throws DataAccessLayerException {
	        super.delete(permissions);
	    }
	
	 public Permissions find(Long id) throws DataAccessLayerException {
	        return (Permissions) super.find(Permissions.class, id);
	    }

	 public Permissions find(String name) throws DataAccessLayerException {
	        return (Permissions) super.find(Permissions.class, name);
	    }
	 @SuppressWarnings("rawtypes")
	 public  List findAll() throws DataAccessLayerException{
	        return super.findAll(Permissions.class);
	    }
	 
	 public void update(Permissions permissions) throws DataAccessLayerException {
	        super.saveOrUpdate(permissions);
	    }
	 
	 
	 @SuppressWarnings("unchecked")
		public List<String> getAllPermissionsByEmailId(String emailId) {
			List<String> permissionsList = new ArrayList<String>();
			List<String> permissionsIdList = new ArrayList<String>();
			Session session = HibernateSessionProvider.getSession();
			Transaction tx = session.beginTransaction();
			try {
				String role_id_query = "SELECT UR.roles_id FROM user_roles UR WHERE UR.User_emailId =:emailId";
				BigInteger user_role_id =(BigInteger)session.createNativeQuery(role_id_query)
						.setParameter("emailId", emailId)
												 .getSingleResult();
				
				String role_id  = String.valueOf(user_role_id);
				
				String permission_id_query = "SELECT RP.permissions_id FROM roles_permissions RP WHERE RP.Roles_id =:role_id";
				permissionsIdList = session.createNativeQuery(permission_id_query)
						.setParameter("role_id", role_id)
						.getResultList();
				
				String permission_query = "SELECT P.permission FROM permissions P WHERE P.id IN (:permissionsIdList)";
				
				permissionsList = session.createNativeQuery(permission_query)
						.setParameter("permissionsIdList", permissionsIdList)
						.getResultList();
				
				
				tx.commit();
				return permissionsList;
			} catch (ObjectNotFoundException objectNotFound) { 
				handleException(objectNotFound,tx);
			} catch (HibernateException hibernateException) {
				handleException(hibernateException,tx);
			}
			finally {
				autoClose(session);
			}
			return permissionsList;
		}
}
