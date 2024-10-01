package com.pay10.crm.action;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.pay10.commons.api.EmailControllerServiceProvider;
import com.pay10.commons.exception.ErrorType;
import com.pay10.commons.user.ResponseObject;
import com.pay10.commons.user.User;
import com.pay10.commons.user.UserDao;
import com.pay10.commons.user.UserType;
import com.pay10.commons.util.Constants;
import com.pay10.commons.util.CrmFieldConstants;
import com.pay10.commons.util.CrmFieldType;
import com.pay10.commons.util.CrmValidator;
import com.pay10.commons.util.UserStatusType;
import com.pay10.crm.actionBeans.CreateSubAdmin;

/**
 * @ Rajendra
 */

public class AddorEditSubSuperAdminAction   extends AbstractSecureAction {


	@Autowired
	private EmailControllerServiceProvider emailControllerServiceProvider;

	@Autowired
	private CrmValidator validator;

	@Autowired
	private CreateSubAdmin createSubAdmin;

	private static final long serialVersionUID = -3762555013302088094L;

	private static Logger logger = LoggerFactory.getLogger(AddorEditSubSuperAdminAction.class.getName());

	private String firstName;
	private String lastName;
	private String mobile;
	private String emailId;
	private User user = new User();
	private String permissionString = "";
	private boolean disableButtonFlag;
	private ResponseObject responseObject;
	private Boolean isActive;
	private Boolean isEditSubSuperAdmin = false;
	
	@Override
	public String execute() {

		try {
			User sessionUser = (User) sessionMap.get(Constants.USER.getValue());
			if(!isEditSubSuperAdmin) {
			
			if (getFirstName() == null && getLastName() == null && getMobile() == null && getEmailId() == null) {

			} else {
				setEmailId(getEmailId().toLowerCase());
				if (sessionUser.getUserType().equals(UserType.SUPERADMIN)) {
					logger.info("Create Sub Super admin");
					responseObject = createSubAdmin.createNewSubSuperAdmin(getUserInstance(), UserType.SUBSUPERADMIN,
							sessionUser.getPayId());

				}
				if (!ErrorType.SUCCESS.getResponseCode().equals(responseObject.getResponseCode())) {
					addActionMessage(responseObject.getResponseMessage());
					return INPUT;
				}
				// Sending Email for Email Validation
				logger.info("Create Sub Super admin email sending");
				emailControllerServiceProvider.addUser(responseObject, getFirstName());

				addActionMessage(CrmFieldConstants.DETAILS_SUBADMAIN_SUCCESSFULLY.getValue());
				disableButtonFlag = true;
			}
				return SUCCESS;
			}else {
				User user = new User();
				UserDao userDao = new UserDao();
				setEmailId(getEmailId().toLowerCase());
				user = userDao.find(getEmailId());

				user.setFirstName(getFirstName());
				user.setLastName(getLastName());
				user.setMobile(getMobile());

				if (getIsActive()) {
					user.setUserStatus(UserStatusType.ACTIVE);
				} else {
					user.setUserStatus(UserStatusType.PENDING);
				}
				
				userDao.update(user);
				addActionMessage(CrmFieldConstants.USER_DETAILS_UPDATED.getValue());
				return "updated";
			}
			
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
		if (getIsActive()) {
			user.setUserStatus(UserStatusType.ACTIVE);
		} else {
			user.setUserStatus(UserStatusType.PENDING);
		}
		return user;
	}

	@Override
	public void validate() {

			if ((validator.validateBlankField(getFirstName()))) {

			} else if (!(validator.validateField(CrmFieldType.FIRSTNAME, getFirstName()))) {
				addFieldError(CrmFieldType.FIRSTNAME.getName(), validator.getResonseObject().getResponseMessage());
			}

			if ((validator.validateBlankField(getLastName()))) {

			} else if (!(validator.validateField(CrmFieldType.LASTNAME, getLastName()))) {
				addFieldError(CrmFieldType.LASTNAME.getName(), validator.getResonseObject().getResponseMessage());
			}

			if (validator.validateBlankField(getMobile())) {

			} else if (!(validator.validateField(CrmFieldType.MOBILE, getMobile()))) {
				addFieldError(CrmFieldType.MOBILE.getName(), validator.getResonseObject().getResponseMessage());
			}

			if (validator.validateBlankField(getEmailId())) {

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

	public String getPermissionString() {
		return permissionString;
	}

	public void setPermissionString(String permissionString) {
		this.permissionString = permissionString;
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

	public Boolean getIsEditSubSuperAdmin() {
		return isEditSubSuperAdmin;
	}

	public void setIsEditSubSuperAdmin(Boolean isEditSubSuperAdmin) {
		this.isEditSubSuperAdmin = isEditSubSuperAdmin;
	}

	public Boolean getIsActive() {
		return isActive;
	}

	public void setIsActive(Boolean isActive) {
		this.isActive = isActive;
	}
	
}
