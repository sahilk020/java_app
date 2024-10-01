package com.pay10.crm.role.action;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.google.gson.Gson;
import com.pay10.commons.dao.UserGroupDao;
import com.pay10.commons.user.Role;
import com.pay10.commons.user.User;
import com.pay10.commons.util.Constants;
import com.pay10.crm.action.AbstractSecureAction;
import com.pay10.crm.role.service.RoleService;

public class UpdateRoleAction extends AbstractSecureAction {

	private static final long serialVersionUID = -7049028879275327195L;
	private static final Logger logger = LoggerFactory.getLogger(UpdateRoleAction.class.getName());

	@Autowired
	private RoleService roleService;

	@Autowired
	private UserGroupDao userGroupDao;

	private String permissionAccess;
	private long id;
	private String roleName;
	private String description;
	private boolean isActive;
	private User user;
	private long userGroupId;
	LocalDateTime now = LocalDateTime.now();
	DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");

	@Override
	public String execute() {
		try {
			logger.info("PermissionsId = {}", getPermissionAccess());
			user = (User) sessionMap.get(Constants.USER.getValue());
			roleService.update(prepareInstance(), getPermissionInLong());
			logger.info("execute:: role updated success. roleId={}, roleName={}", getId(), getRoleName());
			return SUCCESS;

		} catch (Exception ex) {
			logger.error("Exception", ex);
			return ERROR;
		}
	}

	private Role prepareInstance() {
		String formatDateTime = now.format(formatter);
		Role role = roleService.getRole(getId());
		role.setActive(isActive());
		role.setDescription(getDescription());
		role.setRoleName(getRoleName());
		/* role.setUpdatedAt(System.currentTimeMillis()); */
		role.setUpdatedAt(formatDateTime);
		role.setUpdatedBy(user.getEmailId());
		if (role.getUserGroup() == null || role.getUserGroup().getId() != getUserGroupId()) {
			role.setUserGroup(userGroupDao.getUserGroup(getUserGroupId()));
		}
		
		System.out.println(new Gson().toJson("role")+role);
		return role;
	}

	private List<Long> getPermissionInLong() {
		List<Long> permissionIds = new ArrayList<>();
		String[] permissions = getPermissionAccess().split(",");
		for (String permission : permissions) {
			permissionIds.add(Long.valueOf(permission));
		}
		return permissionIds;
	}

	public String getPermissionAccess() {
		return permissionAccess;
	}

	public void setPermissionAccess(String permissionAccess) {
		this.permissionAccess = permissionAccess;
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

	public long getUserGroupId() {
		return userGroupId;
	}

	public void setUserGroupId(long userGroupId) {
		this.userGroupId = userGroupId;
	}
}
