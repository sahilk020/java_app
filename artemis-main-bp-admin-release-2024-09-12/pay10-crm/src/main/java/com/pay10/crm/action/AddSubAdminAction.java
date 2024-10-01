package com.pay10.crm.action;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.pay10.commons.api.EmailControllerServiceProvider;
import com.pay10.commons.audittrail.ActionMessageByAction;
import com.pay10.commons.audittrail.AuditTrail;
import com.pay10.commons.audittrail.AuditTrailService;
import com.pay10.commons.dao.RoleDao;
import com.pay10.commons.dao.UserGroupDao;
import com.pay10.commons.exception.ErrorType;
import com.pay10.commons.user.ResponseObject;
import com.pay10.commons.user.User;
import com.pay10.commons.user.UserGroup;
import com.pay10.commons.user.UserType;
import com.pay10.commons.util.Constants;
import com.pay10.commons.util.CrmFieldConstants;
import com.pay10.commons.util.CrmFieldType;
import com.pay10.commons.util.CrmValidator;
import com.pay10.crm.actionBeans.CreateSubAdmin;

public class AddSubAdminAction extends AbstractSecureAction {

	@Autowired
	private EmailControllerServiceProvider emailControllerServiceProvider;

	@Autowired
	private CrmValidator validator;

	@Autowired
	private CreateSubAdmin createSubAdmin;

	@Autowired
	private RoleDao rolesDao;

	@Autowired
	private UserGroupDao userGroupDao;

	@Autowired
	private AuditTrailService auditTrailService;

	/**
	 * @ Neeraj
	 */
	private static final long serialVersionUID = -3762555013302088094L;

	private static Logger logger = LoggerFactory.getLogger(AddSubAdminAction.class.getName());

	private String firstName;
	private String lastName;
	private String mobile;
	private String emailId;
	private User user = new User();
	private ResponseObject responseObject;
	private long roleId;
	private long userGroupId;
	private List<UserGroup> userGroups;
	private String segmentName;
	private boolean needToShowAcqFieldsInReport;

	@SuppressWarnings("unchecked")
	@Override
	public String execute() {

		try {
			User sessionUser = (User) sessionMap.get(Constants.USER.getValue());
			List<UserGroup> groups = userGroupDao.getUserGroups();
			groups = groups.stream()
					.filter(group -> !StringUtils.equalsAnyIgnoreCase(group.getGroup(), "Merchant", "Reseller"))
					.collect(Collectors.toList());
			setUserGroups(groups);

			if (getFirstName() == null && getLastName() == null && getMobile() == null && getEmailId() == null) {

			} else {
				setEmailId(getEmailId().toLowerCase());
				if (sessionUser.getUserType().equals(UserType.ADMIN)
						|| sessionUser.getUserType().equals(UserType.SUBADMIN)) {
					logger.info("Create Subadmin");
					responseObject = createSubAdmin.createNewSubAdmin(getUserInstance(), UserType.SUBADMIN,
							sessionUser.getPayId());

				}
				if (!ErrorType.SUCCESS.getResponseCode().equals(responseObject.getResponseCode())) {
					addActionMessage(responseObject.getResponseMessage());
					return INPUT;
				}
				// Sending Email for Email Validation
				logger.info("Create Subadmin email sending");
				emailControllerServiceProvider.addUser(responseObject, getFirstName());
				Map<String, ActionMessageByAction> actionMessagesByAction = (Map<String, ActionMessageByAction>) sessionMap
						.get(Constants.ACTION_MESSAGE_BY_ACTION.getValue());
				AuditTrail auditTrail = new AuditTrail(mapper.writeValueAsString(user), null,
						actionMessagesByAction.get("addSubAdmin"));
				auditTrailService.saveAudit(request, auditTrail);
				addActionMessage(CrmFieldConstants.DETAILS_SUBADMAIN_SUCCESSFULLY.getValue());
			}
			setFirstName("");
			setLastName("");
			setMobile("");
			setEmailId("");
			setUserGroupId(0);
			setRoleId(0);
			setSegmentName("");
			setNeedToShowAcqFieldsInReport(false);
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
		user.setNeedToShowAcqFieldsInReport(isNeedToShowAcqFieldsInReport());
		user.setRole(rolesDao.getRole(getRoleId()));
		user.setSegment(segmentName);
		user.setUserGroup(userGroupDao.getUserGroup(getUserGroupId()));
		return user;
	}

	@Override
	public void validate() {

		if (getFirstName() == null && getLastName() == null && getMobile() == null && getEmailId() == null) {

		}

		else {
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

	public String getSegmentName() {
		return segmentName;
	}

	public void setSegmentName(String segmentName) {
		this.segmentName = segmentName;
	}

	public boolean isNeedToShowAcqFieldsInReport() {
		return needToShowAcqFieldsInReport;
	}

	public void setNeedToShowAcqFieldsInReport(boolean needToShowAcqFieldsInReport) {
		this.needToShowAcqFieldsInReport = needToShowAcqFieldsInReport;
	}
}
