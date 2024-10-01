package com.pay10.crm.action;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.pay10.commons.dao.UserGroupDao;
import com.pay10.commons.user.User;
import com.pay10.commons.user.UserGroup;
import com.pay10.commons.user.UserType;
import com.pay10.commons.util.Constants;

/**
 * @author Rahul
 *
 */
public class AddUserCallAction extends AbstractSecureAction {

	private static Logger logger = LoggerFactory.getLogger(AddUserCallAction.class.getName());
	private static final long serialVersionUID = 9033493264155370845L;

	@Autowired
	private UserGroupDao userGroupDao;

	private List<UserGroup> userGroups;

	@Override
	public String execute() {
		try {
			User sessionUser = null;
			sessionUser = (User) sessionMap.get(Constants.USER.getValue());
			List<UserGroup> groups = userGroupDao.getUserGroups();
			groups = groups.stream()
					.filter(group -> StringUtils.equalsAnyIgnoreCase(group.getGroup(), "Merchant", "Reseller"))
					.collect(Collectors.toList());
			
			if (sessionUser.getUserType().equals(UserType.MERCHANT)) {
				List<UserGroup> groupsE=new ArrayList<>();
				UserGroup group=new UserGroup();
				group.setDescription(UserType.SUBUSER.name());
				group.setGroup(UserType.SUBUSER.name());
				group.setId(8);
				groupsE.add(group);
				setUserGroups(groupsE);
				return "merchant";
			} else if (sessionUser.getUserType().equals(UserType.ACQUIRER)) {
				setUserGroups(groups);
				return "reseller";
			} else if (sessionUser.getUserType().equals(UserType.ADMIN)) {
				setUserGroups(groups);
				return "subAdmin";
			} else if (sessionUser.getUserType().equals(UserType.SUBADMIN)) {
				setUserGroups(groups);
				return "subAdmin";
			}
		} catch (Exception exception) {
			logger.error("Exception", exception);
			return ERROR;
		}
		;
		return null;
	}

	public List<UserGroup> getUserGroups() {
		return userGroups;
	}

	public void setUserGroups(List<UserGroup> userGroups) {
		this.userGroups = userGroups;
	}
}
