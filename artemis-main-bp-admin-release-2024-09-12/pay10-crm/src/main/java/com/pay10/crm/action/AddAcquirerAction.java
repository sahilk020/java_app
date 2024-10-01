package com.pay10.crm.action;

import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.pay10.commons.dao.RoleDao;
import com.pay10.commons.dao.UserGroupDao;
import com.pay10.commons.exception.ErrorType;
import com.pay10.commons.user.ResponseObject;
import com.pay10.commons.user.User;
import com.pay10.commons.user.UserGroup;
import com.pay10.commons.user.UserType;
import com.pay10.commons.util.Constants;
import com.pay10.commons.util.CrmFieldConstants;
import com.pay10.crm.actionBeans.CreateAcquirer;

public class AddAcquirerAction extends AbstractSecureAction {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3002089778209549449L;

	@Autowired
	private CreateAcquirer createAcquirer;

	@Autowired
	private RoleDao roleDao;

	@Autowired
	private UserGroupDao userGroupDao;

	/**
	 * @ Shaiwal
	 */

	private static Logger logger = LoggerFactory.getLogger(AddAcquirerAction.class.getName());

	private String firstName;
	private String lastName;
	private String businessName;
	private String emailId;
	private String accountNo;
	private User user = new User();
	private boolean disableButtonFlag;
	private ResponseObject responseObject;
	private long roleId;
	private long userGroupId;
	private List<UserGroup> userGroups;

	@Override
	public String execute() {
		logger.info("called execute method");
		try {
			User sessionUser = (User) sessionMap.get(Constants.USER.getValue());
			List<UserGroup> groups = userGroupDao.getUserGroups();
			groups = groups.stream()
					.filter(group -> !StringUtils.equalsAnyIgnoreCase(group.getGroup(), "Merchant", "Reseller"))
					.collect(Collectors.toList());
			setUserGroups(groups);
			if (getFirstName() == null && getLastName() == null && getBusinessName() == null && getEmailId() == null) {

			} else {

				if (sessionUser.getUserType().equals(UserType.ADMIN)
						|| sessionUser.getUserType().equals(UserType.SUBADMIN)) {
					logger.info("Create Subadmin");
					user.setRole(roleDao.getRole(getRoleId()));
					user.setUserGroup(userGroupDao.getUserGroup(getUserGroupId()));
					responseObject = createAcquirer.createNewAcquirer(getUserInstance());

				}
				if (!ErrorType.SUCCESS.getResponseCode().equals(responseObject.getResponseCode())) {
					addActionMessage(responseObject.getResponseMessage());
					return INPUT;
				}
				// Sending Email for Email Validation
				logger.info("Create Subadmin email sending");

				addActionMessage(CrmFieldConstants.DETAILS_ACQUIRER_SUCCESSFULLY.getValue());
				disableButtonFlag = true;
			}
			return SUCCESS;
		} catch (Exception exception) {
			logger.error("Exception", exception);
			return ERROR;
		}
	}

	private User getUserInstance() {
		user.setFirstName(getFirstName());
		user.setLastName(getLastName());
		user.setBusinessName(getBusinessName());
		user.setEmailId(getEmailId());
		user.setAccountNo(getAccountNo());
		return user;
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
