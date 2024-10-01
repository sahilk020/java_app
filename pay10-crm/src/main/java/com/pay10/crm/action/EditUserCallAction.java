package com.pay10.crm.action;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.pay10.commons.util.Constants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.google.gson.Gson;
import com.pay10.commons.dao.RoleDao;
import com.pay10.commons.dao.UserGroupDao;
import com.pay10.commons.exception.ErrorType;
import com.pay10.commons.user.Role;
import com.pay10.commons.user.User;
import com.pay10.commons.user.UserDao;
import com.pay10.commons.user.UserGroup;
import com.pay10.commons.user.UserType;
import com.pay10.commons.util.CrmFieldType;
import com.pay10.commons.util.CrmValidator;

public class EditUserCallAction extends AbstractSecureAction {

	@Autowired
	private UserDao userDao;
	@Autowired
	private CrmValidator validator;

	@Autowired
	private UserGroupDao userGroupDao;

	@Autowired
	private RoleDao roleDao;

	private static final long serialVersionUID = 6921991472327981800L;

	private static Logger logger = LoggerFactory.getLogger(EditUserCallAction.class.getName());

	private String firstName;
	private String lastName;
	private String mobile;
	private String emailId;
	private Boolean isActive;
	private  boolean google2FAKey;
	private boolean tfaFlag;
	private Boolean isTfaEnable;
	private long userGroupId;
	private List<UserGroup> userGroups;
	private long roleId;
	private List<Role> roles;

	@Override
	public String execute() {
		try {
			User loggerUser=(User)sessionMap.get(Constants.USER.getValue());
			//List<UserGroup> groups = userGroupDao.getUserGroups();
//			groups = groups.stream()
//					.filter(group -> StringUtils.equalsAnyIgnoreCase(group.getGroup(), "Merchant", "Reseller"))
//					.collect(Collectors.toList());
			logger.info("Email ID: "+getEmailId());
			List<UserGroup> groups=new ArrayList<>();
			UserGroup group=new UserGroup();
			group.setDescription(UserType.SUBUSER.name());
			group.setGroup(UserType.SUBUSER.name());
			group.setId(8);
			groups.add(group);
			setUserGroups(groups);


			//setUserGroups(groups);
			User user = userDao.find(getEmailId());
			logger.info("User Info: "+new Gson().toJson(user));
			setIsTfaEnable(user.isTfaFlag());
			setTfaFlag(user.isTfaFlag());
			logger.info("{}",userDao.find(getEmailId()));
			setGoogle2FAKey(user.getGoogle2FASecretkey()==null||user.getGoogle2FASecretkey().trim().isEmpty());
			if (user.getRole() != null) {
				logger.info("User Role Not Null");
				setRoleId(user.getRole().getId());
			}
			logger.info("User Group: {}", user.getUserGroup());
			if (user.getUserGroup() != null) {
				logger.info("User Group Not Null");
				setUserGroupId(user.getUserGroup().getId());
			}
				if(loggerUser.getUserType().equals(UserType.SUBUSER)||loggerUser.getUserType().equals(UserType.MERCHANT)) {
					logger.info("User is SUBUSER");
					setRoles(roleDao.getRoles(loggerUser.getEmailId()));
				}else {
					logger.info("User is not SUBUSER");
					setRoles(roleDao.getByGroupId(loggerUser.getUserGroup().getId()));
				}



			logger.info("User Role: "+new Gson().toJson(getRoles()));
		} catch (Exception exception) {
			logger.error("Exception", exception);
			return ERROR;
		}
		return INPUT;
	}

	@Override
	public void validate() {
		System.out.println(getFirstName());
		System.out.println(getLastName());
		System.out.println(getMobile());
		System.out.println(getEmailId());
		logger.info("TFA Flag: "+getIsTfaEnable());
		if ((validator.validateBlankField(getFirstName()))) {
			addFieldError(CrmFieldType.FIRSTNAME.getName(), validator.getResonseObject().getResponseMessage());
		} else if (!(validator.validateField(CrmFieldType.FIRSTNAME, getFirstName()))) {
			addFieldError(CrmFieldType.FIRSTNAME.getName(), validator.getResonseObject().getResponseMessage());
		}

		if ((validator.validateBlankField(getLastName()))) {
			addFieldError(CrmFieldType.LASTNAME.getName(), validator.getResonseObject().getResponseMessage());
		} else if (!(validator.validateField(CrmFieldType.LASTNAME, getLastName()))) {
			addFieldError(CrmFieldType.LASTNAME.getName(), validator.getResonseObject().getResponseMessage());
		}

//		if (validator.validateBlankField(getMobile())) {
//			addFieldError(CrmFieldType.MOBILE.getName(), validator.getResonseObject().getResponseMessage());
//		} else if (!(validator.validateField(CrmFieldType.MOBILE, getMobile()))) {
//			addFieldError(CrmFieldType.MOBILE.getName(), validator.getResonseObject().getResponseMessage());
//		}
//
//		if (validator.validateBlankField(getEmailId())) {
//			addFieldError(CrmFieldType.EMAILID.getName(), validator.getResonseObject().getResponseMessage());
//		} else if (!(validator.isValidEmailId(getEmailId()))) {
//			addFieldError(CrmFieldType.EMAILID.getName(), ErrorType.INVALID_FIELD_VALUE.getResponseMessage());
//		}
	}
	public String resetTFAuthentication(){
		logger.info(getEmailId());
		User user=userDao.find(getEmailId());
		if(user.getEmailId()==null|| user.getEmailId().isEmpty())
		{
			return ERROR;
		}
		else{
			userDao.resetTwoFactorAuth(user.getEmailId(),user.getPayId());
			user.setGoogle2FASecretkey("");
			user.setTfaFlag(true);
			setIsTfaEnable(true);
			setGoogle2FAKey(true);
		}
		return SUCCESS;
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

	public Boolean getIsActive() {
		return isActive;
	}

	public void setIsActive(Boolean isActive) {
		this.isActive = isActive;
	}

	public String getEmailId() {
		return emailId;
	}

	public void setEmailId(String emailId) {
		this.emailId = emailId;
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

	public Boolean getIsTfaEnable() {
		return isTfaEnable;
	}

	public void setIsTfaEnable(Boolean tfaEnable) {
		isTfaEnable = tfaEnable;
	}

	public boolean isGoogle2FAKey() {
		return google2FAKey;
	}

	public void setGoogle2FAKey(boolean google2FAKey) {
		this.google2FAKey = google2FAKey;
	}

	public boolean isTfaFlag() {
		return tfaFlag;
	}

	public void setTfaFlag(boolean tfaFlag) {
		this.tfaFlag = tfaFlag;
	}
}
