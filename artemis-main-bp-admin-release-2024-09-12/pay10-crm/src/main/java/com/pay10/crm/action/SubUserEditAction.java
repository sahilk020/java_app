package com.pay10.crm.action;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
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
import com.pay10.commons.user.MerchantStatusLog;
import com.pay10.commons.user.MerchantStatusLogDao;
import com.pay10.commons.user.Role;
import com.pay10.commons.user.User;
import com.pay10.commons.user.UserDao;
import com.pay10.commons.user.UserGroup;
import com.pay10.commons.user.UserType;
import com.pay10.commons.util.Constants;
import com.pay10.commons.util.CrmFieldConstants;
import com.pay10.commons.util.CrmFieldType;
import com.pay10.commons.util.CrmValidator;
import com.pay10.commons.util.UserStatusType;

/**
 * @ Neeraj
 */
public class SubUserEditAction extends AbstractSecureAction {

	@Autowired
	private UserDao userDao;

	@Autowired
	private CrmValidator validator;

	@Autowired
	private RoleDao roleDao;

	@Autowired
	private UserGroupDao userGroupDao;

	@Autowired
	private AuditTrailService auditTrailService;

	private static final long serialVersionUID = -2429379754814283308L;
	private static Logger logger = LoggerFactory.getLogger(SubUserEditAction.class.getName());
	private String firstName;
	private String lastName;
	private String mobile;
	private String emailId;
	private Boolean isActive;
	private long roleId;
	private List<Role> roles;
	private long userGroupId;
	private boolean isTfaEnable;
	private List<UserGroup> userGroups;
	private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	@Autowired
	private MerchantStatusLogDao merchantStatusLogDao;

	@SuppressWarnings("unchecked")
	public String editSubUser() {
		try {
		User loggedUser=(User)sessionMap.get("USER");
			User user = new User();
			user.setEmailId(getEmailId().toLowerCase());
			user = userDao.find(getEmailId());
			String prevUser = mapper.writeValueAsString(user);
			user.setFirstName(getFirstName());
			user.setLastName(getLastName());
			user.setMobile(getMobile());
			logger.info("isTfaFlag Status: "+getIsTfaEnable());

			logger.info("isActive Status: "+getIsActive());
			user.setTfaFlag(getIsTfaEnable());
			if (getIsActive()) {
				user.setUserStatus(UserStatusType.ACTIVE);
			} else {
				user.setUserStatus(UserStatusType.PENDING);
			}
			if(loggedUser.getUserType().equals(UserType.SUBUSER)||loggedUser.getUserType().equals(UserType.MERCHANT)) {
				logger.info("User is SUBUSER");
				setRoles(roleDao.getRoles(loggedUser.getEmailId()));
			}else {
				logger.info("User is not SUBUSER");
				setRoles(roleDao.getByGroupId(user.getUserGroup().getId()));
			}
		//	setRoles(roleDao.getActiveRolesByGroupId(8));
			//List<UserGroup> groups = userGroupDao.getUserGroups();
			List<UserGroup> groups=new ArrayList<>();
			UserGroup group=new UserGroup();
			group.setDescription(UserType.SUBUSER.name());
			group.setGroup(UserType.SUBUSER.name());
			groups.add(group);
			setUserGroups(groups);
//			groups = groups.stream()
//					.filter(group -> !StringUtils.equalsAnyIgnoreCase(group.getGroup(), "Merchant", "Reseller"))
//					.collect(Collectors.toList());
			//setUserGroups(groups);
		//	logger.info("Role Id user"+user.getRole().getId());
		//	logger.info("role Id "+getRoleId());
		 		user.setRole(roleDao.getRole(getRoleId()));
		 		user.setUserGroup(userGroupDao.getUserGroup(getUserGroupId()));
		 	userDao.update(user);



			System.out.println("merchantlog create sub User : " + user.getEmailId() + "\t" + getEmailId());
			if(UserType.SUBUSER.equals(user.getUserType().SUBUSER)) {
				//add to log
				MerchantStatusLog merchantStatusLog=new MerchantStatusLog();
				merchantStatusLog.setEmailId(user.getEmailId());
				merchantStatusLog.setTimeStamp(sdf.format(new Date()));
				merchantStatusLog.setMessage("Sub Merchant " + user.getUserStatus().getStatus());
				merchantStatusLog.setStatus(user.getUserStatus().getStatus());
				merchantStatusLogDao.saveMerchantStatusLog(merchantStatusLog);
			}


			Map<String, ActionMessageByAction> actionMessagesByAction = (Map<String, ActionMessageByAction>) sessionMap
					.get(Constants.ACTION_MESSAGE_BY_ACTION.getValue());
			AuditTrail auditTrail = new AuditTrail(mapper.writeValueAsString(user), prevUser,
					actionMessagesByAction.get("editSubUserDetails"));
			auditTrailService.saveAudit(request, auditTrail);

			addActionMessage(CrmFieldConstants.USER_DETAILS_UPDATED.getValue());

			return INPUT;
		} catch (Exception exception) {
			logger.error("Exception", exception);
			return ERROR;
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

	public boolean getIsTfaEnable() {
		return isTfaEnable;
	}

	public void setIsTfaEnable(boolean tfaEnable) {
		isTfaEnable = tfaEnable;
	}
}
