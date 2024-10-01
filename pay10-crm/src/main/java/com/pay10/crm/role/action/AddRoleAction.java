package com.pay10.crm.role.action;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.pay10.commons.dao.UserGroupDao;
import com.pay10.commons.user.Role;
import com.pay10.commons.user.User;
import com.pay10.commons.util.Constants;
import com.pay10.crm.action.AbstractSecureAction;
import com.pay10.crm.role.service.RoleService;

public class AddRoleAction extends AbstractSecureAction {

	private static final long serialVersionUID = -7049028879275327195L;
	private static final Logger logger = LoggerFactory.getLogger(AddRoleAction.class.getName());

	@Autowired
	private RoleService roleService;
	
	@Autowired
	private UserGroupDao userGroupDao;

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
			user = (User) sessionMap.get(Constants.USER.getValue());
			roleService.create(getInstance());
			logger.info("role created. name={}", getRoleName());
			return SUCCESS;

		} catch (Exception ex) {
			logger.error("Exception", ex);
			return ERROR;
		}
	}

	private Role getInstance() {
		
		String formatDateTime = now.format(formatter);
		Role role = new Role();
		role.setRoleName(getRoleName());
		role.setDescription(getDescription());
		role.setActive(isActive());
		role.setCreatedBy(user.getEmailId());
		/* role.setCreatedAt(System.currentTimeMillis()); */
		role.setCreatedAt(formatDateTime);
		role.setUserGroup(userGroupDao.getUserGroup(getUserGroupId()));
		return role;
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
