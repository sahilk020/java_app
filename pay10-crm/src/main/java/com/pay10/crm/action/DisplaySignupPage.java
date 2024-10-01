package com.pay10.crm.action;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

import com.pay10.commons.dao.UserGroupDao;
import com.pay10.commons.user.UserGroup;
import com.pay10.commons.util.BusinessType;

/**
 * @author Puneet
 *
 */
public class DisplaySignupPage extends AbstractSecureAction {

	private static final long serialVersionUID = 3472447203584744945L;
	private Map<String, String> industryCategoryList = new TreeMap<String, String>();
	private List<UserGroup> userGroups;

	@Autowired
	private UserGroupDao userGroupDao;

	@Override
	public String execute() {
		Map<String, String> industryCategoryLinkedMap = BusinessType.getIndustryCategoryList();

		industryCategoryList.putAll(industryCategoryLinkedMap);
		List<UserGroup> groups = userGroupDao.getUserGroups();
		groups = groups.stream()
				//.filter(group -> StringUtils.equalsAnyIgnoreCase(group.getGroup(), "Merchant", "Reseller"))
				.filter(group -> StringUtils.equalsAnyIgnoreCase(group.getGroup(), "Merchant"))

				.collect(Collectors.toList());
		setUserGroups(groups);
		return INPUT;
	}

	public Map<String, String> getIndustryCategoryList() {
		return industryCategoryList;
	}

	public void setIndustryCategoryList(Map<String, String> industryCategoryList) {
		this.industryCategoryList = industryCategoryList;
	}

	public List<UserGroup> getUserGroups() {
		return userGroups;
	}

	public void setUserGroups(List<UserGroup> userGroups) {
		this.userGroups = userGroups;
	}
}
