package com.pay10.crm.action;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.pay10.commons.audittrail.ActionMessageByAction;
import com.pay10.commons.audittrail.AuditTrail;
import com.pay10.commons.audittrail.AuditTrailService;
import com.pay10.commons.dao.RoleDao;
import com.pay10.commons.dao.UserGroupDao;
import com.pay10.commons.exception.ErrorType;
import com.pay10.commons.user.Role;
import com.pay10.commons.user.User;
import com.pay10.commons.user.UserDao;
import com.pay10.commons.user.UserGroup;
import com.pay10.commons.util.Constants;
import com.pay10.commons.util.CrmFieldConstants;
import com.pay10.commons.util.CrmFieldType;
import com.pay10.commons.util.CrmValidator;
import com.pay10.commons.util.UserStatusType;

/**
 * @author Rahul
 *
 */
public class SubAdminEditAction extends AbstractSecureAction {
	@Autowired
	private CrmValidator validator;

	@Autowired
	private RoleDao roleDao;

	@Autowired
	private UserGroupDao userGroupDao;

	@Autowired
	private AuditTrailService auditTrailService;

	private static final long serialVersionUID = 1183250308057362565L;
	private static Logger logger = LoggerFactory.getLogger(SubAdminEditAction.class.getName());
	private String firstName;
	private String lastName;
	private String mobile;
	private String emailId;
	private Boolean isActive;
	private long roleId;
	private long userGroupId;
	private List<Role> roles;
	private boolean tfaFlag;
	private List<UserGroup> userGroups;
	private Boolean google2FAKey;
	private boolean needToShowAcqFieldsInReport;

	@SuppressWarnings("unchecked")
	public String editSubAdmin() {
		try {

			User user = new User();
			UserDao userDao = new UserDao();
			setEmailId(getEmailId().toLowerCase());
			user = userDao.find(getEmailId());

			String prevUser = mapper.writeValueAsString(user);
			user.setFirstName(getFirstName());
			user.setLastName(getLastName());
			logger.info("TFA Flag: "+isTfaFlag());
			user.setTfaFlag(isTfaFlag());
			user.setMobile(getMobile());
			user.setNeedToShowAcqFieldsInReport(isNeedToShowAcqFieldsInReport());

            setGoogle2FAKey(user.getGoogle2FASecretkey() == null || user.getGoogle2FASecretkey().isEmpty());
			if (getIsActive()) {
				user.setUserStatus(UserStatusType.ACTIVE);
			} else {
				user.setUserStatus(UserStatusType.PENDING);
			}
			setRoles(roleDao.getActiveRoles());
			List<UserGroup> groups = userGroupDao.getUserGroups();
			groups = groups.stream()
					.filter(group -> !StringUtils.equalsAnyIgnoreCase(group.getGroup(), "Merchant", "Reseller"))
					.collect(Collectors.toList());
			setUserGroups(groups);
			if (user.getRole() == null || user.getRole().getId() != getRoleId()) {
				user.setRole(roleDao.getRole(getRoleId()));
			}
			if (user.getUserGroup() == null || user.getUserGroup().getId() != getUserGroupId()) {
				user.setUserGroup(userGroupDao.getUserGroup(getUserGroupId()));
			}
			userDao.update(user);
			addActionMessage(CrmFieldConstants.USER_DETAILS_UPDATED.getValue());
			Map<String, ActionMessageByAction> actionMessagesByAction = (Map<String, ActionMessageByAction>) sessionMap
					.get(Constants.ACTION_MESSAGE_BY_ACTION.getValue());
			AuditTrail auditTrail = new AuditTrail(mapper.writeValueAsString(user), prevUser,
					actionMessagesByAction.get("editAgentDetails"));
			auditTrailService.saveAudit(request, auditTrail);
			return INPUT;
		} catch (Exception exception) {
			logger.error("Exception", exception);
			return ERROR;
		}
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

	@Override
	public void validate() {

		CrmValidator validator = new CrmValidator();

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

		if (validator.validateBlankField(getMobile())) {
			addFieldError(CrmFieldType.MOBILE.getName(), validator.getResonseObject().getResponseMessage());
		} else if (!(validator.validateField(CrmFieldType.MOBILE, getMobile()))) {
			addFieldError(CrmFieldType.MOBILE.getName(), validator.getResonseObject().getResponseMessage());
		}

		if (validator.validateBlankField(getEmailId())) {
			addFieldError(CrmFieldType.EMAILID.getName(), validator.getResonseObject().getResponseMessage());
		} else if (!(validator.isValidEmailId(getEmailId()))) {
			addFieldError(CrmFieldType.EMAILID.getName(), ErrorType.INVALID_FIELD_VALUE.getResponseMessage());
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

	public long getUserGroupId() {
		return userGroupId;
	}

	public void setUserGroupId(long userGroupId) {
		this.userGroupId = userGroupId;
	}

	public List<Role> getRoles() {
		return roles;
	}

	public void setRoles(List<Role> roles) {
		this.roles = roles;
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

	public boolean isTfaFlag() {
		return tfaFlag;
	}

	public void setTfaFlag(boolean tfaFlag) {
		this.tfaFlag = tfaFlag;
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
}
