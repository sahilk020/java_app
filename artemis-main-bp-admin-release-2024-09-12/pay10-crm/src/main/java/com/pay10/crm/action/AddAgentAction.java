package com.pay10.crm.action;

import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.opensymphony.xwork2.interceptor.annotations.InputConfig;
import com.pay10.commons.api.EmailControllerServiceProvider;
import com.pay10.commons.audittrail.ActionMessageByAction;
import com.pay10.commons.audittrail.AuditTrail;
import com.pay10.commons.audittrail.AuditTrailService;
import com.pay10.commons.dao.HibernateSessionProvider;
import com.pay10.commons.dao.RoleDao;
import com.pay10.commons.dao.UserGroupDao;
import com.pay10.commons.exception.ErrorType;
import com.pay10.commons.user.PermissionType;
import com.pay10.commons.user.Permissions;
import com.pay10.commons.user.ResponseObject;
import com.pay10.commons.user.Role;
import com.pay10.commons.user.Roles;
import com.pay10.commons.user.User;
import com.pay10.commons.user.UserDao;
import com.pay10.commons.user.UserGroup;
import com.pay10.commons.user.UserType;
import com.pay10.commons.util.Constants;
import com.pay10.commons.util.CrmFieldConstants;
import com.pay10.commons.util.CrmFieldType;
import com.pay10.commons.util.CrmValidator;
import com.pay10.crm.actionBeans.CreateAgent;

public class AddAgentAction extends AbstractSecureAction {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6812539598761798561L;

	@Autowired
	private EmailControllerServiceProvider emailControllerServiceProvider;

	@Autowired
	private CrmValidator validator;

	@Autowired
	private UserDao userDao;

	@Autowired
	private RoleDao roleDao;

	@Autowired
	private UserGroupDao userGroupDao;

	@Autowired
	private CreateAgent createAgent;

	@Autowired
	private AuditTrailService auditTrailService;

	private static Logger logger = LoggerFactory.getLogger(AddAgentAction.class.getName());

	private String firstName;
	private String lastName;
	private String mobile;
	private String emailId;
	private List<String> lstPermissionType;
	private List<PermissionType> listPermissionType;
	private User user = new User();
	private String permissionString = "";
	private boolean disableButtonFlag;
	private boolean notificationApiEnableFlag;
	private long roleId;
	private List<Role> roles;
	private long userGroupId;
	private List<UserGroup> userGroups;

	private ResponseObject responseObject;

	@SuppressWarnings("unchecked")
	@Override
	@InputConfig(methodName = "execute")
	public String execute() {

		try {
			User sessionUser = (User) sessionMap.get(Constants.USER.getValue());
			setEmailId(getEmailId().toLowerCase());
			setRoles(roleDao.getActiveRoles());
			List<UserGroup> groups = userGroupDao.getUserGroups();
			groups = groups.stream()
					.filter(group -> !StringUtils.equalsAnyIgnoreCase(group.getGroup(), "Merchant", "Reseller"))
					.collect(Collectors.toList());
			setUserGroups(groups);
			if (sessionUser.getUserType().equals(UserType.ADMIN)) {
				setListPermissionType(PermissionType.getSubUserPermissionType());
			} else if (sessionUser.getUserType().equals(UserType.SUBADMIN)) {
				setListPermissionType(PermissionType.getSubUserPermissionType());
			}
			Set<Permissions> permissions = new HashSet<Permissions>();

			if (lstPermissionType == null) {

			} else {
				for (String permissionType : lstPermissionType) {
					Permissions permission = new Permissions();
					permission.setPermissionType(PermissionType.getInstanceFromName(permissionType));
					permissions.add(permission);
				}
			}

			if (sessionUser.getUserType().equals(UserType.ADMIN)
					|| sessionUser.getUserType().equals(UserType.SUBADMIN)) {
				logger.info("Create Agent");
				if (StringUtils.isNotBlank(getEmailId())) {
					responseObject = createAgent.createNewAgent(getUserInstance(), UserType.AGENT, permissions);
				}

			}
			if (!ErrorType.SUCCESS.getResponseCode().equals(responseObject.getResponseCode())) {
				addActionMessage(responseObject.getResponseMessage());
				return INPUT;
			}
			// Sending Email for Email Validation
			logger.info("Create Agent email sending");
			emailControllerServiceProvider.addUser(responseObject, getFirstName());

			Map<String, ActionMessageByAction> actionMessagesByAction = (Map<String, ActionMessageByAction>) sessionMap
					.get(Constants.ACTION_MESSAGE_BY_ACTION.getValue());
			AuditTrail auditTrail = new AuditTrail(mapper.writeValueAsString(user), null,
					actionMessagesByAction.get("addAgent"));
			auditTrailService.saveAudit(request, auditTrail);

			addActionMessage(CrmFieldConstants.DETAILS_AGENT_SUCCESSFULLY.getValue());
			getPermissions();
			disableButtonFlag = true;
			notificationApiEnableFlag = user.isNotificationApiEnableFlag();

			return SUCCESS;
		} catch (Exception exception) {
			logger.error("Exception", exception);
			return ERROR;
		}
	}

	private User getUserInstance() {
		user.setFirstName(getFirstName());
		user.setLastName(getLastName());
		user.setMobile(getMobile());
		user.setEmailId(getEmailId());
		user.setRole(roleDao.getRole(getRoleId()));
		user.setUserGroup(userGroupDao.getUserGroup(getUserGroupId()));
		return user;
	}

	private void getPermissions() {
		Session session = null;
		try {

			User subUser = userDao.find(getEmailId());
			session = HibernateSessionProvider.getSession();
			Transaction tx = session.beginTransaction();
			session.load(subUser, subUser.getEmailId());

			Set<Roles> roles = subUser.getRoles();
			Set<Permissions> permissions = roles.iterator().next().getPermissions();
			if (!permissions.isEmpty()) {
				StringBuilder perms = new StringBuilder();
				Iterator<Permissions> itr = permissions.iterator();
				while (itr.hasNext()) {
					PermissionType permissionType = itr.next().getPermissionType();
					perms.append(permissionType.getPermission());
					perms.append("-");
				}
				perms.deleteCharAt(perms.length() - 1);
				setPermissionString(perms.toString());
			}
			tx.commit();
		} finally {
			HibernateSessionProvider.closeSession(session);
		}
	}

	@Override
	public void validate() {

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
		return emailId == null ? "" : emailId;
	}

	public void setEmailId(String emailId) {
		this.emailId = emailId;
	}

	public List<String> getLstPermissionType() {
		return lstPermissionType;
	}

	public void setLstPermissionType(List<String> lstPermissionType) {
		this.lstPermissionType = lstPermissionType;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public String getPermissionString() {
		return permissionString;
	}

	public void setPermissionString(String permissionString) {
		this.permissionString = permissionString;
	}

	public List<PermissionType> getListPermissionType() {
		return listPermissionType;
	}

	public void setListPermissionType(List<PermissionType> listPermissionType) {
		this.listPermissionType = listPermissionType;
	}

	public boolean isDisableButtonFlag() {
		return disableButtonFlag;
	}

	public void setDisableButtonFlag(boolean disableButtonFlag) {
		this.disableButtonFlag = disableButtonFlag;
	}

	public boolean isNotificationApiEnableFlag() {
		return notificationApiEnableFlag;
	}

	public void setNotificationApiEnableFlag(boolean notificationApiEnableFlag) {
		this.notificationApiEnableFlag = notificationApiEnableFlag;
	}

	public ResponseObject getResponseObject() {
		return responseObject;
	}

	public void setResponseObject(ResponseObject responseObject) {
		this.responseObject = responseObject;
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
