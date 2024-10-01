package com.pay10.crm.role.action;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.pay10.crm.action.AbstractSecureAction;
import com.pay10.crm.role.service.RoleService;

public class DeleteRoleAction extends AbstractSecureAction {

	private static final long serialVersionUID = -7049028879275327195L;
	private static final Logger logger = LoggerFactory.getLogger(DeleteRoleAction.class.getName());

	@Autowired
	private RoleService roleService;

	private long id;

	@Override
	public String execute() {
		try {
			roleService.delete(getId());
			logger.info("role deleted. roleId={}", getId());
		} catch (Exception exception) {
			logger.error("Exception", exception);
			return ERROR;
		}
		return SUCCESS;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}
}
