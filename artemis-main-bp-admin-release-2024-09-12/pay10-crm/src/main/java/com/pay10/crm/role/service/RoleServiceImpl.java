package com.pay10.crm.role.service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.google.gson.Gson;
import com.pay10.commons.dao.HibernateSessionProvider;
import com.pay10.commons.dao.PermissionDao;
import com.pay10.commons.dao.RoleDao;
import com.pay10.commons.dao.RoleWiseMenuDao;
import com.pay10.commons.user.Role;
import com.pay10.commons.user.RoleWiseMenu;

@Service
public class RoleServiceImpl implements RoleService {

	@Autowired
	private RoleDao roleDao;

	@Autowired
	private PermissionDao permissionDao;

//	@Autowired
//	private PermissionWiseMenuDao permissionWiseMenuDao;

	@Autowired
	private RoleWiseMenuDao roleWiseMenuDao;

	Logger logger= LoggerFactory.getLogger(RoleServiceImpl.class.getName());

	@Override
	public void create(Role role) {
		roleDao.save(role);
	}

	@Override
	public List<Role> getRoles(String createdBy) {
		logger.info("Role List{}",roleDao.getRoles(createdBy));
		return roleDao.getRoles(createdBy).stream().sorted(Comparator.comparingLong(Role::getId)).collect(Collectors.toList());
	}



	@Override
	public Role getRole(long id) {
		return roleDao.getRole(id);
	}



	@Override
	public void update(Role role, List<Long> permissionIds) {
//		roleDao.update(role);
//		List<RoleWiseMenu> roleWiseMenus = new ArrayList<>();
//		for (long permissionId : permissionIds) {
//			RoleWiseMenu roleWiseMenu = new RoleWiseMenu();
//			roleWiseMenu.setActive(true);
//			roleWiseMenu.setPermission(permissionDao.find(permissionId));
//			roleWiseMenu.setRole(role);
//			roleWiseMenus.add(roleWiseMenu);
//		}
//		int count = roleWiseMenuDao.deleteByRoleId(role.getId());
//		logger.info("Delete Count : {}", count);
//		for(RoleWiseMenu roleWiseMenu : roleWiseMenus){
//			roleWiseMenuDao.save(roleWiseMenu);
//		}
		//logger.info("Permission idd: " + new Gson().toJson(permissionIds));
		Session session = HibernateSessionProvider.getSession();
		Transaction tx = session.beginTransaction();
		try {
		boolean updateStatus=roleDao.updateNew(role,session);
		List<RoleWiseMenu> roleWiseMenus = new ArrayList<>();
		for (long permissionId : permissionIds) {
			RoleWiseMenu roleWiseMenu = new RoleWiseMenu();
			roleWiseMenu.setActive(true);
			roleWiseMenu.setPermission(permissionDao.find(permissionId));
			roleWiseMenu.setRole(role);
			roleWiseMenus.add(roleWiseMenu);
		}

		boolean deleteStatus = roleWiseMenuDao.deleteByRoleIdNew(role.getId(),session);
		logger.info("Delete Status : {}", deleteStatus);
		boolean saveStatus=false;
		for(RoleWiseMenu roleWiseMenu : roleWiseMenus){
			saveStatus=roleWiseMenuDao.saveNew(roleWiseMenu,session);
			logger.info("Save : " + new Gson().toJson(roleWiseMenu));
		}
		session.flush();
		logger.info("All Status UpdateStatus : " + updateStatus + " saveStatus : " + saveStatus + " deleteStatus : " + deleteStatus);
		if(updateStatus&&saveStatus&&deleteStatus) {
			tx.commit();
			logger.info("Update Successfully");
		}else {
			logger.info("Rollback Successfully else block");
			tx.rollback();
		}
		}catch (Exception e) {
			tx.rollback();
			logger.info("Rollback Successfully exception block");
		}finally {
			logger.info("Session close successfully");
			if (null != session && session.isOpen()) {
				session.close();
				session = null;
			}
		}

	}

	@Override
	public void delete(long id) {
		roleWiseMenuDao.deleteByRoleId(id);
//		permissionWiseMenuDao.deleteByRoleId(id);
		roleDao.delete(id);
	}

	@Override
	public List<Long> getAccessPermission(long roleId) {
		List<RoleWiseMenu> rolewiseMenus = roleWiseMenuDao.getbyRoleId(roleId);
		return rolewiseMenus.stream().map(rolewiseMenu -> rolewiseMenu.getPermission().getId())
				.collect(Collectors.toList());
	}

	@Override
	public List<Role> getRoleByEmailId(String emailId) {
	 	if(StringUtils.isNotBlank(emailId)){
			return  roleDao.getRoleByEmailId(emailId);
		}
		return new ArrayList<>();
	}

	@Override
	public List<Role> getAllRole() {
		return roleDao.findAll().stream().sorted(Comparator.comparingLong(Role::getId)).collect(Collectors.toList());
	}

	@Override
	public long getRoleIdByName(String roleName) {
		return roleDao.getIdByName(roleName);
	}
}
