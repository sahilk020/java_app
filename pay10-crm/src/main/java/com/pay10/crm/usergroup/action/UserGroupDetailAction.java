package com.pay10.crm.usergroup.action;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.pay10.commons.user.UserGroup;
import com.pay10.crm.action.AbstractSecureAction;
import com.pay10.crm.usergroup.service.UserGroupService;

/**
 * @author Jay
 *
 */
public class UserGroupDetailAction extends AbstractSecureAction {

	private static final long serialVersionUID = 3255958300818815691L;
	private static Logger logger = LoggerFactory.getLogger(UserGroupDetailAction.class.getName());

	@Autowired
	private UserGroupService userGroupService;

	private List<UserGroup> aaData;

	@Override
	public String execute() {
		try {
			setAaData(userGroupService.getUserGroups());
			return SUCCESS;
		} catch (Exception exception) {
			logger.error("Exception", exception);
			return ERROR;
		}
	}

	public List<UserGroup> getAaData() {
		return aaData;
	}

	public void setAaData(List<UserGroup> aaData) {
		this.aaData = aaData;
	}

}
