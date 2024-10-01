package com.pay10.crm.role.action;

import java.util.List;

import com.pay10.commons.user.User;
import com.pay10.commons.user.UserType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.pay10.commons.dao.RoleDao;
import com.pay10.commons.user.Role;
import com.pay10.crm.action.AbstractSecureAction;

public class RoleByGroupId extends AbstractSecureAction {

	private static final long serialVersionUID = 381384537713089428L;
	private static final Logger logger = LoggerFactory.getLogger(RoleByGroupId.class.getName());

	@Autowired
	private RoleDao roleDao;

	private long groupId;
	private String type;
	private List<Role> roles;

	@Override
	public String execute() {

		try {
			logger.info("Group Id"+getGroupId());
			User user =(User) sessionMap.get("USER");

			if(user.getUserType().equals(UserType.ADMIN)){
				setRoles(roleDao.getByGroupId(getGroupId()));
			}else if(user.getUserType().equals(UserType.SUBADMIN)){
				setRoles(roleDao.getByGroupId(getGroupId()));
			}
			else{
				setRoles(roleDao.getRoleByEmailId(user.getEmailId()));
			}
			return INPUT;
		} catch (Exception ex) {
			logger.error("Exception", ex);
			return ERROR;
		}
	}

	public long getGroupId() {
		return groupId;
	}

	public void setGroupId(long groupId) {
		this.groupId = groupId;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public List<Role> getRoles() {
		return roles;
	}

	public void setRoles(List<Role> roles) {
		this.roles = roles;
	}
}
