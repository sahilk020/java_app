package com.pay10.crm.action;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.pay10.commons.api.EmailControllerServiceProvider;
import com.pay10.commons.dao.RoleDao;
import com.pay10.commons.dao.UserGroupDao;
import com.pay10.commons.exception.ErrorType;
import com.pay10.commons.user.MerchantStatusLog;
import com.pay10.commons.user.ResponseObject;
import com.pay10.commons.user.Role;
import com.pay10.commons.user.User;
import com.pay10.commons.user.UserGroup;
import com.pay10.commons.user.UserType;
import com.pay10.commons.util.Constants;
import com.pay10.commons.util.CrmFieldConstants;
import com.pay10.commons.util.CrmFieldType;
import com.pay10.commons.util.CrmValidator;
import com.pay10.commons.util.UserStatusType;
import com.pay10.crm.actionBeans.CreateSubUser;

/**
 * @author Rahul
 *
 */

public class AddSubUserAction extends AbstractSecureAction {
	
	@Autowired
	private EmailControllerServiceProvider emailControllerServiceProvider;
	
	@Autowired
	private CrmValidator validator;
	
	@Autowired
	private CreateSubUser createSubUser;
	
	@Autowired
	private RoleDao roleDao;

	@Autowired
	private UserGroupDao userGroupDao;

	private static final long serialVersionUID = 5867519202316872074L;

	private static Logger logger = LoggerFactory.getLogger(AddSubUserAction.class.getName());

	private String firstName;
	private String lastName;
	private String mobile;
	private String emailId;
	private User user = new User();
	private boolean disableButtonFlag;
	private ResponseObject responseObject;
	private Boolean isActive;
	private Boolean isTfaEnable;
	private long roleId;
	private List<Role> roles;
	private long userGroupId;
	private List<UserGroup> userGroups;

	@Override
	public String execute() {
		try {
			logger.info("Add Sub User Action");
			User sessionUser = (User) sessionMap.get(Constants.USER.getValue());
			setEmailId(getEmailId().toLowerCase());
			setRoles(roleDao.getActiveRolesByGroupId(8));
//			List<UserGroup> groups = userGroupDao.getUserGroups();
//			groups = groups.stream()
//					.filter(group -> !StringUtils.equalsAnyIgnoreCase(group.getGroup(), "Merchant", "Reseller"))
//					.collect(Collectors.toList());
			
			List<UserGroup> groups=new ArrayList<>();
			UserGroup group=new UserGroup();
			group.setDescription(UserType.SUBUSER.name());
			group.setGroup(UserType.SUBUSER.name());
			group.setId(8);
			groups.add(group);
			setUserGroups(groups);

			if (sessionUser.getUserType().equals(UserType.ACQUIRER)) {

				responseObject = createSubUser.createUser(getUserInstance(), UserType.SUBACQUIRER,
						sessionUser.getPayId());
			} else if (sessionUser.getUserType().equals(UserType.MERCHANT)) {
				responseObject = createSubUser.createUser(getUserInstance(), UserType.SUBUSER, sessionUser.getPayId());
				
			}
			if (!ErrorType.SUCCESS.getResponseCode().equals(responseObject.getResponseCode())) {
				addActionMessage(responseObject.getResponseMessage());
				logger.error("Failed While Creating sub user "+responseObject.getResponseCode()+" Error: "+responseObject.getResponseMessage());
				return INPUT;
			}
			// Sending Email for Email Validation
			emailControllerServiceProvider.addUser(responseObject, getFirstName());

			addActionMessage(CrmFieldConstants.DETAILS_SUBUSER_SUCCESSFULLY.getValue());
			disableButtonFlag = true;
			logger.info("Details Save successfully done");
			return SUCCESS;
		} catch (Exception exception) {
			logger.error("Exception", exception);
			return ERROR;
		}
	}

	private User getUserInstance() {
		User sessionUser = (User) sessionMap.get(Constants.USER.getValue());
		user.setFirstName(getFirstName());
		user.setLastName(getLastName());
		user.setMobile(getMobile());
		user.setEmailId(getEmailId());
		user.setBusinessName(sessionUser.getBusinessName());
		logger.info("TFA Flag: "+getIsTfaEnable());
		user.setTfaFlag(getIsTfaEnable());
		
		if (getIsActive()) {
			user.setUserStatus(UserStatusType.ACTIVE);
		} else {
			user.setUserStatus(UserStatusType.PENDING);
		}
		user.setRole(roleDao.getRole(getRoleId()));
		user.setUserGroup(userGroupDao.getUserGroup(getUserGroupId()));
		return user;
	}

	@Override
	public void validate() {
		logger.info("Inside validate");
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

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public boolean isDisableButtonFlag() {
		return disableButtonFlag;
	}

	public void setDisableButtonFlag(boolean disableButtonFlag) {
		this.disableButtonFlag = disableButtonFlag;
	}

	public ResponseObject getResponseObject() {
		return responseObject;
	}

	public void setResponseObject(ResponseObject responseObject) {
		this.responseObject = responseObject;
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

	public Boolean getIsTfaEnable() {
		return isTfaEnable;
	}

	public void setIsTfaEnable(Boolean isTfaEnable) {
		this.isTfaEnable = isTfaEnable;
	}
}
