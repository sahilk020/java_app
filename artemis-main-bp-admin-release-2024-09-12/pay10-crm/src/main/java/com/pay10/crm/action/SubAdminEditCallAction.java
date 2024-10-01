package com.pay10.crm.action;

import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.junit.internal.runners.ErrorReportingRunner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.pay10.commons.dao.RoleDao;
import com.pay10.commons.dao.UserGroupDao;
import com.pay10.commons.user.Role;
import com.pay10.commons.user.User;
import com.pay10.commons.user.UserDao;
import com.pay10.commons.user.UserGroup;
import com.pay10.commons.util.CrmFieldType;
import com.pay10.commons.util.CrmValidator;

public class SubAdminEditCallAction extends AbstractSecureAction {

	@Autowired
	private CrmValidator validator;

	@Autowired
	private RoleDao rolesDao;

	@Autowired
	private UserGroupDao userGroupDao;

	@Autowired
	private UserDao userDao;

	private static final long serialVersionUID = -6368002305712366054L;
	private static Logger logger = LoggerFactory.getLogger(SubAdminEditCallAction.class.getName());

	private String firstName;
	private String lastName;
	private String mobile;
	private String emailId;
	private Boolean isActive;
	private Boolean google2FAKey;
	private long roleId;
	private Boolean tfaFlag;
	private List<Role> roles;
	private long userGroupId;
	private List<UserGroup> userGroups;
	private boolean needToShowAcqFieldsInReport;

	@Override
	public String execute() {

		try {
			List<UserGroup> groups = userGroupDao.getUserGroups();
			groups = groups.stream()
					.filter(group -> !StringUtils.equalsAnyIgnoreCase(group.getGroup(), "Merchant", "Reseller"))
					.collect(Collectors.toList());
			setUserGroups(groups);
	 		if (StringUtils.isNotBlank(getEmailId())) {
				User user = userDao.find(getEmailId());
				logger.info(user.getEmailId()+" Email Id: "+user.getGoogle2FASecretkey()+" OnLoad!!!");
				setTfaFlag(user.isTfaFlag());
				//setGoogle2FAKey(!user.getGoogle2FASecretkey().isEmpty());
                setGoogle2FAKey(user.getGoogle2FASecretkey() == null || user.getGoogle2FASecretkey().trim().isEmpty());
                setNeedToShowAcqFieldsInReport(user.isNeedToShowAcqFieldsInReport());
				if (user.getRole() != null) {
					setRoleId(user.getRole().getId());
				}
				if (user.getUserGroup() != null) {
					setUserGroupId(user.getUserGroup().getId());
					setRoles(rolesDao.getByGroupId(user.getUserGroup().getId()));
				} else {
					setRoles(rolesDao.getActiveRoles());
				}
			}
		} catch (Exception exception) {
			logger.error("Exception", exception);
			return ERROR;
		}
		return INPUT;
	}

	public String resetTFAuthentication(){
		User user=userDao.find(getEmailId());
		if(getEmailId().trim().isEmpty())
			return ERROR;
		logger.info("user Email ID: "+user.getEmailId());
		logger.info(getEmailId()+" "+user.getPayId());
 		userDao.resetTwoFactorAuth(getEmailId(),user.getPayId());
		 user.setGoogle2FASecretkey("");
		 user.setTfaFlag(true);
		 setGoogle2FAKey(true);
		 setTfaFlag(true);
		 return SUCCESS;
	}
	public void validator() {

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
		if ((validator.validateBlankField(getMobile()))) {
			addFieldError(CrmFieldType.MOBILE.getName(), validator.getResonseObject().getResponseMessage());
		} else if (!(validator.validateField(CrmFieldType.MOBILE, getMobile()))) {
			addFieldError(CrmFieldType.MOBILE.getName(), validator.getResonseObject().getResponseMessage());
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

	public boolean isNeedToShowAcqFieldsInReport() {
		return needToShowAcqFieldsInReport;
	}

	public void setNeedToShowAcqFieldsInReport(boolean needToShowAcqFieldsInReport) {
		this.needToShowAcqFieldsInReport = needToShowAcqFieldsInReport;
	}

	public Boolean getGoogle2FAKey() {
		return google2FAKey;
	}

	public void setGoogle2FAKey(Boolean google2FAKey) {
		this.google2FAKey = google2FAKey;
	}

	public Boolean getTfaFlag() {
		return tfaFlag;
	}

	public void setTfaFlag(Boolean tfaFlag) {
		this.tfaFlag = tfaFlag;
	}
}
