package com.pay10.crm.role.action;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.pay10.commons.user.*;
import com.pay10.commons.util.Constants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.pay10.commons.dao.UserGroupDao;
import com.pay10.crm.action.AbstractSecureAction;

public class RoleListAction extends AbstractSecureAction {

	private static final long serialVersionUID = 3429511096457216583L;
	private static final Logger logger = LoggerFactory.getLogger(RoleListAction.class.getName());

	@Autowired
	private UserGroupDao userGroupDao;

	private List<UserGroup> userGroups;
	private List<Merchants>merchantList=new ArrayList<Merchants>();

	@Override
	public String execute() {
		try {
			//logger.info("SessionMap"+sessionMap);
			//logger.info("SessionMap"+sessionMap.get("USER"));

			User user =(User)sessionMap.get("USER");

			 if(user.getBusinessName().equalsIgnoreCase(UserType.ADMIN.name())){
				merchantList = new UserDao().getMerchantActiveList(sessionMap.get(Constants.SEGMENT.getValue()).toString(), user.getRole().getId());

			}
	 	 	logger.info(user.getUserGroup().getGroup());
			List<UserGroup> groupList = userGroupDao.getUserGroups();
			if(user.getUserGroup().getGroup().equalsIgnoreCase(UserType.MERCHANT.name())){
				setUserGroups(groupList.stream().filter(userGroup -> UserType.SUBUSER.name().equalsIgnoreCase(userGroup.getGroup().replace(" ",""))).collect(Collectors.toList()));
 			}
			else {
				setUserGroups(groupList);
			}
			return INPUT;
		} catch (Exception ex) {
			logger.error("Exception", ex);
			return ERROR;
		}
	}

	public List<UserGroup> getUserGroups() {
		return userGroups;
	}

	public void setUserGroups(List<UserGroup> userGroups) {
		this.userGroups = userGroups;
	}

	public List<Merchants> getMerchantList() {
		return merchantList;
	}

	public void setMerchantList(List<Merchants> merchantList) {
		this.merchantList = merchantList;
	}
}
