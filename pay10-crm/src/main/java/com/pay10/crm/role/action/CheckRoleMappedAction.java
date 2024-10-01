package com.pay10.crm.role.action;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.pay10.commons.user.UserDao;
import com.pay10.crm.action.AbstractSecureAction;

/**
 * @author Jay Gajera
 *
 */
public class CheckRoleMappedAction extends AbstractSecureAction {

	private static final long serialVersionUID = -4302177723930009811L;
	private static final Logger logger = LoggerFactory.getLogger(CheckRoleMappedAction.class.getName());

	@Autowired
	private UserDao userDao;

	private long roleId;
	private String message;

	public String checkIsMapped() {
		try {
			long count = userDao.getUserCountByRole(getRoleId());
			if (count > 0) {
				setMessage("Role mapped with users. So you can not delete.");
				return SUCCESS;
			}
			setMessage("success");
			return SUCCESS;
		} catch (Exception ex) {
			logger.error("failed to check. roleId={}", getRoleId(), ex);
			return ERROR;
		}
	}

	public long getRoleId() {
		return roleId;
	}

	public void setRoleId(long roleId) {
		this.roleId = roleId;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

}
