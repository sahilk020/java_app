package com.pay10.crm.action;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.pay10.commons.user.*;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.pay10.commons.dao.RoleDao;
import com.pay10.commons.dao.UserGroupDao;
import com.pay10.commons.util.CrmFieldType;
import com.pay10.commons.util.CrmValidator;

public class AcquirerEditAction extends AbstractSecureAction {

	@Autowired
	private UserDao userDao;

	@Autowired
	private CrmValidator validator;
	
	@Autowired
	private RoleDao roleDao;
	
	@Autowired
	private UserGroupDao userGroupDao;

	private static final long serialVersionUID = -6368002305712366054L;
	private static Logger logger = LoggerFactory.getLogger(AcquirerEditAction.class.getName());

	private String firstName;
	private String lastName;
	private String mobile;
	private String emailId;
	private String businessName;
	private String accountNo;
	private Boolean isActive;
	private long roleId;
	private List<Role> roles;
	private long userGroupId;
	private List<UserGroup> userGroups;

	@Override
	public String execute() {

		logger.info("Email Id acquirer Edit Action: "+getEmailId());
		try {
			User user = userDao.findByEmailId(getEmailId());
			if (user.getRole() != null) {
				setRoleId(user.getRole().getId());
			}
			if (user.getUserGroup() != null) {
				setUserGroupId(2);
				logger.info("User Group{}",getUserGroupId());
				setRoles(roleDao.getByGroupId(2));
				logger.info("Roles{}",getRoles());
			}
			if(user.getFirstName()!=null){
				setFirstName(user.getFirstName());
			}
			else{
				setFirstName("");
			}
			if(user.getLastName()!=null){
				setLastName(user.getLastName());
			}
			else{
				setLastName("");
			}
			if(user.getBusinessName()!=null){
				setBusinessName(user.getBusinessName());
			}
			else{
				setBusinessName("");
			}
			if(user.getEmailId()!=null){
				setEmailId(user.getEmailId());
			}
			else{
				setEmailId("");
			}


			List<UserGroup> groups=new ArrayList<>();
			UserGroup group=new UserGroup();
			group.setDescription(UserType.ACQUIRER.name());
			group.setGroup(UserType.ACQUIRER.name());
			group.setId(2);
			groups.add(group);
			setUserGroups(groups);
			logger.info("User Group{}",getUserGroups());
		/*	List<UserGroup> groups = userGroupDao.getUserGroups();
			groups = groups.stream()
					.filter(group -> !StringUtils.equalsAnyIgnoreCase(group.getGroup(), "Merchant", "Reseller"))
					.collect(Collectors.toList());
			setUserGroups(groups);*/
		} catch (Exception exception) {
			logger.error("Exception", exception);
			return ERROR;
		}
		return INPUT;
	}


	public void validator() {
		logger.info("validator");
		if ((validator.validateBlankField(getBusinessName()))) {
			addFieldError(CrmFieldType.BUSINESS_NAME.getName(), validator.getResonseObject().getResponseMessage());
		} else if (!(validator.validateField(CrmFieldType.BUSINESS_NAME, getBusinessName()))) {
			addFieldError(CrmFieldType.BUSINESS_NAME.getName(), validator.getResonseObject().getResponseMessage());
		}
		
		if ((validator.validateBlankField(getEmailId()))) {
			addFieldError(CrmFieldType.EMAILID.getName(), validator.getResonseObject().getResponseMessage());
		} else if (!(validator.validateField(CrmFieldType.EMAILID, getEmailId()))) {
			addFieldError(CrmFieldType.EMAILID.getName(), validator.getResonseObject().getResponseMessage());
		}

	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public String getMobile() {
		return mobile;
	}

	public void setMobile(String mobile) {
		this.mobile = mobile;
	}

	public String getEmailId() {
		return emailId;
	}

	public void setEmailId(String emailId) {
		this.emailId = emailId;
	}

	public Boolean getIsActive() {
		return isActive;
	}

	public void setIsActive(Boolean isActive) {
		this.isActive = isActive;
	}


	public String getBusinessName() {
		return businessName;
	}


	public void setBusinessName(String businessName) {
		this.businessName = businessName;
	}


	public String getAccountNo() {
		return accountNo;
	}


	public void setAccountNo(String accountNo) {
		this.accountNo = accountNo;
	}

	public long getRoleId() {
		return roleId;
	}

	public void setRoleId(long roleId) {
		this.roleId = roleId;
	}

	public List<Role> getRoles() {
		return roles;
	}

	public void setRoles(List<Role> roles) {
		this.roles = roles;
	}

	public long getUserGroupId() {
		return userGroupId;
	}

	public void setUserGroupId(long userGroupId) {
		this.userGroupId = userGroupId;
	}

	public List<UserGroup> getUserGroups() {
		return userGroups;
	}

	public void setUserGroups(List<UserGroup> userGroups) {
		this.userGroups = userGroups;
	}
}
