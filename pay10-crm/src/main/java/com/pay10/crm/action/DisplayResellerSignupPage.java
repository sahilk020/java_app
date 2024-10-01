package com.pay10.crm.action;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.pay10.commons.dao.RoleDao;
import com.pay10.commons.dao.UserGroupDao;
import com.pay10.commons.user.Segment;
import com.pay10.commons.user.UserGroup;
import com.pay10.commons.util.BusinessType;


public class DisplayResellerSignupPage  extends AbstractSecureAction {
	
	/**
	 * @author Sweety
	 */
	private static final long serialVersionUID = 5329018721747864017L;
	private static Logger logger = LoggerFactory.getLogger(DisplayResellerSignupPage.class.getName());
	private Map<String, String> industryCategoryList = new TreeMap<String, String>();


	private List<UserGroup> userGroups;

	@Autowired
	private UserGroupDao userGroupDao;
	
	private List<Segment> segments;
	
	@Autowired
	private RoleDao roleDao;
	
	@Override
	public String execute() {
		
		Map<String, String> industryCategoryLinkedMap = BusinessType.getIndustryCategoryList();

		industryCategoryList.putAll(industryCategoryLinkedMap);
		
		List<UserGroup> groups = userGroupDao.getUserGroups();
		logger.info("groups list.....={}",groups);
		groups = groups.stream()
				.filter(group -> StringUtils.equalsAnyIgnoreCase(group.getGroup(), "Reseller","SMA","MA","Agent"))
				.collect(Collectors.toList());
		logger.info("groups list.....={}",groups);
		setUserGroups(groups);
		setSegments(roleDao.getAllSegment());
		return INPUT;
	}
	
	public List<UserGroup> getUserGroups() {
		return userGroups;
	}

	public void setUserGroups(List<UserGroup> userGroups) {
		this.userGroups = userGroups;
	}

	public Map<String, String> getIndustryCategoryList() {
		return industryCategoryList;
	}

	public void setIndustryCategoryList(Map<String, String> industryCategoryList) {
		this.industryCategoryList = industryCategoryList;
	}

	public List<Segment> getSegments() {
		return segments;
	}

	public void setSegments(List<Segment> segments) {
		this.segments = segments;
	}
}
