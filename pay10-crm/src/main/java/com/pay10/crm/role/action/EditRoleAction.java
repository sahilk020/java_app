package com.pay10.crm.role.action;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.google.gson.Gson;
import com.pay10.commons.user.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pay10.commons.dao.UserGroupDao;
import com.pay10.crm.action.AbstractSecureAction;
import com.pay10.crm.menu.service.MenuService;
import com.pay10.crm.role.service.RoleService;

public class EditRoleAction extends AbstractSecureAction {

	private static final long serialVersionUID = -7049028879275327195L;
	private static final Logger logger = LoggerFactory.getLogger(EditRoleAction.class.getName());

	@Autowired
	private MenuService menuService;

	@Autowired
	private RoleService roleService;

	@Autowired
	private UserGroupDao userGroupDao;
	@Autowired
	private UserDao userDao;

	private long id;
	private String roleName;
	private String description;
	private boolean isActive;
	private List<Menu> menus;
	private String menuTree;
	private List<Long> permissionAccess;
	private long userGroupId;
	private List<UserGroup> userGroups;
	private String roleTypeForSelectedRole;
	@Override
	public String execute() {
		try {
			logger.info("Id "+id);
			User user=(User)sessionMap.get("USER");
			Role role = roleService.getRole(getId());
			setRoleName(role.getRoleName());
			setDescription(role.getDescription());
			setActive(role.isActive());
			setMenus(menuService.prepareAllMenuTree(menuService.getAllMenus()));
			User userForRoleName=(User)userDao.find(role.getCreatedBy());
			setRoleTypeForSelectedRole(userForRoleName.getUserType().name());
			if(user.getUserType().equals(UserType.MERCHANT)){
				if(getRoleTypeForSelectedRole().equalsIgnoreCase(UserType.MERCHANT.name())) {
					//List<Long>permissionList=roleService.getAccessPermission(role.getId());
					//logger.info("Role List By Id Edit Role Action{} ",permissionList);
					logger.info("Menu List");
					long roleId = user.getRole().getId();
					logger.info("Role List using Name id{} ", roleId);
				//	logger.info("Menu List Using parent and  Merchat List{}", menuService.getMenuByRoleId(roleId));
					List<Menu> menuList = menuService.getMenuByRoleId(roleId);
					List<Menu> parents = new ArrayList<>();
					Set<Long> mainMenuSet = new HashSet<>();
					for (Menu menu : menuList) {
						logger.info("Menu Parent Id " + menu.getParentId());
						if (!mainMenuSet.contains(menu.getParentId())) {
							mainMenuSet.add(menu.getParentId());
							parents.add(menuService.getMenu(menu.getParentId()));
				//			logger.info("Set for parentID" + menu.getParentId() + " menu{} ", menuService.getParentIdById(menu.getParentId()));
						}
					}
					menuList.addAll(parents);
					setMenus(menuService.prepareAllMenuTree(menuList));
				}
			}
			setMenuTree(new ObjectMapper().writeValueAsString(getMenus()));
			setPermissionAccess(roleService.getAccessPermission(getId()));
			if (role.getUserGroup() != null) {
				setUserGroupId(role.getUserGroup().getId());
			}
			setUserGroups(userGroupDao.getUserGroups());

		} catch (Exception exception) {
			logger.error("Exception", exception);
			return ERROR;
		}
		return INPUT;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getRoleName() {
		return roleName;
	}

	public void setRoleName(String roleName) {
		this.roleName = roleName;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public boolean isActive() {
		return isActive;
	}

	public void setActive(boolean isActive) {
		this.isActive = isActive;
	}

	public List<Menu> getMenus() {
		return menus;
	}

	public void setMenus(List<Menu> menus) {
		this.menus = menus;
	}

	public String getMenuTree() {
		return menuTree;
	}

	public void setMenuTree(String menuTree) {
		this.menuTree = menuTree;
	}

	public List<Long> getPermissionAccess() {
		return permissionAccess;
	}

	public void setPermissionAccess(List<Long> permissionAccess) {
		this.permissionAccess = permissionAccess;
	}

	public long getUserGroupId() {
		return userGroupId;
	}

	public void setUserGroupId(long userGroupId) {
		this.userGroupId = userGroupId;
	}

	public List<UserGroup> getUserGroups() {
		return userGroups;
	}

	public void setUserGroups(List<UserGroup> userGroups) {
		this.userGroups = userGroups;
	}

	public String getRoleTypeForSelectedRole() {
		return roleTypeForSelectedRole;
	}

	public void setRoleTypeForSelectedRole(String roleTypeForSelectedRole) {
		this.roleTypeForSelectedRole = roleTypeForSelectedRole;
	}
}
