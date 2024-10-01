package com.pay10.crm.role.action;

import java.util.List;

import com.pay10.commons.user.User;
import com.pay10.commons.user.UserType;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.pay10.commons.user.Role;
import com.pay10.crm.action.AbstractSecureAction;
import com.pay10.crm.role.service.RoleService;

/**
 * @author Jay
 *
 */
public class RoleDetailAction extends AbstractSecureAction {

	private static final long serialVersionUID = 3255958300818815691L;
	private String emailId;
	private static Logger logger = LoggerFactory.getLogger(RoleDetailAction.class.getName());

	@Autowired
	private RoleService roleService;

	private List<Role> aaData;

	@Override
	public String execute() {
		try {
			User user=(User) sessionMap.get("USER");
			logger.info("Get User Type for Role Details Action table "+user.getUserType());
			if(!user.getUserType().equals(UserType.ADMIN)){
				setAaData(roleService.getRoles(user.getEmailId()));
			}
			else if (StringUtils.isNotBlank(getEmailId())){
					logger.info("EmailId "+getEmailId());

					setAaData(roleService.getRoles(getEmailId()));
			}
			else {
					setAaData(roleService.getAllRole());
					//logger.info("Get Role{} ",roleService.getAllRole());
			}

			return SUCCESS;
		} catch (Exception exception) {
			logger.error("Exception", exception);
			return ERROR;
		}
	}


	public List<Role> getAaData() {
		return aaData;
	}

	public void setAaData(List<Role> aaData) {
		this.aaData = aaData;
	}

	public String getEmailId() {
		return emailId;
	}

	public void setEmailId(String emailId) {
		this.emailId = emailId;
	}
}
