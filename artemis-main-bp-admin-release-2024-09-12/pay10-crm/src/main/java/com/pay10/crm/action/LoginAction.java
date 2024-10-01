package com.pay10.crm.action;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;

import com.pay10.commons.user.*;
import org.apache.commons.lang3.StringUtils;
import org.apache.struts2.dispatcher.SessionMap;
import org.apache.struts2.interceptor.ServletRequestAware;
import org.apache.struts2.util.TokenHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.opensymphony.xwork2.ActionContext;
import com.pay10.commons.dao.RoleWiseMenuDao;
import com.pay10.commons.audittrail.ActionMessageByAction;
import com.pay10.commons.audittrail.ActionMessageByActionDao;
import com.pay10.commons.exception.ErrorType;
import com.pay10.commons.util.Constants;
import com.pay10.commons.util.CrmActions;
import com.pay10.commons.util.CrmFieldConstants;
import com.pay10.commons.util.CrmFieldType;
import com.pay10.commons.util.CrmValidator;
import com.pay10.crm.actionBeans.LoginAuthenticator;
import com.pay10.crm.actionBeans.SessionCleaner;
import com.pay10.crm.menu.service.MenuService;
import com.pay10.crm.util.PasswordDecryptor;;

/**
 * @author Puneet
 *
 */

public class LoginAction extends AbstractSecureAction implements ServletRequestAware {

	@Autowired
	LoginHistoryDao loginHistoryDao;

	@Autowired
	private CrmValidator validator;

	@Autowired
	LoginAuthenticator loginAuthenticator;

	@Autowired
	private RoleWiseMenuDao roleWiseMenuDao;

	@Autowired
	private MenuService menuService;

	@Autowired
	private ActionMessageByActionDao actionMessageByActionDao;

	@Autowired
	private PasswordDecryptor passwordDecryptor;

	private static Logger logger = LoggerFactory.getLogger(LoginAction.class.getName());
	private static final long serialVersionUID = -5127683348802926510L;

	private int passwordAge = 0;
	private String redirectUrl;
	private String emailId;
	private String password;
	// private String captcha;
	// private String captchaCode;
	private String otp;

	private HttpServletRequest request;
	private String loginType;
	private String permissionString = "";

	@Autowired
	private UserDao userDao;

	@Override
	public String execute() {
		logger.info("Login Action");

		logger.info("Login Action , above ip address ");

		logger.info("IP ADDRESS X-Forwarded-For   " + request.getHeader("X-Forwarded-For") + "     port    "
				+ request.getHeader("x-forwarded-port"));

		logger.info("IP ADDRESS TEST remote addr    " + request.getRemoteAddr());
		logger.info("Header value " + request.getHeader("User-Agent"));
		User tempUser=userDao.findByEmailId(emailId);
		if(tempUser!=null){
			UserType userType=tempUser.getUserType();
			logger.info("UserType:"+userType);
			if(!(userType.equals(UserType.ADMIN)||userType.equals(UserType.SUBADMIN)||userType.equals(UserType.SUBSUPERADMIN)||userType.equals(UserType.SUPERADMIN))){
				logger.info("Not Admin logout");
				return INPUT;
			}
		}
		ResponseObject responseObject = new ResponseObject();
		LoginHistory loginHistory = new LoginHistory();
		User user;
		String passwordChange = "passwordChange";

		try {
			passwordAge = loginHistoryDao.findLastPasswordCreateDate(emailId);
			if (passwordAge > 90) {
				return passwordChange;
			}
		} catch (Exception e) {
			logger.error("Password Change Exception "+e);
			return INPUT;
		}

		try {

			if (StringUtils.isBlank(request.getHeader("X-Forwarded-For"))) {
				if (loginType.equals("userOtp")) {
					responseObject = loginAuthenticator.authenticateOtp(getOtp(), getEmailId().toLowerCase(),
							request.getHeader(CrmFieldConstants.USER_AGENT.getValue()), request.getRemoteAddr());
				} else {
					responseObject = loginAuthenticator.authenticate(getEmailId().toLowerCase(), getPassword(),
							request.getHeader(CrmFieldConstants.USER_AGENT.getValue()), request.getRemoteAddr());
				}
			} else {
				if (loginType.equals("userOtp")) {
					responseObject = loginAuthenticator.authenticateOtp(getOtp(), getEmailId().toLowerCase(),
							request.getHeader(CrmFieldConstants.USER_AGENT.getValue()),
							request.getHeader("X-Forwarded-For"));
				} else {
					responseObject = loginAuthenticator.authenticate(getEmailId().toLowerCase(), getPassword(),
							request.getHeader(CrmFieldConstants.USER_AGENT.getValue()),
							request.getHeader("X-Forwarded-For"));
				}
			}

			if (!ErrorType.SUCCESS.getResponseCode().equals(responseObject.getResponseCode())) {
				logger.info("Error Type: "+ErrorType.SUCCESS.getResponseCode());
				logger.info("Error Type: "+responseObject.getResponseCode());

				// setCaptcha("");
				if (responseObject.getResponseMessage()
						.equalsIgnoreCase(ErrorType.USER_NOT_FOUND.getResponseMessage())) {
					addFieldError(CrmFieldType.EMAILID.getName(),
							ErrorType.USER_PASSWORD_INCORRECT.getResponseMessage());
				} else {
					if (responseObject.getResponseCode().equalsIgnoreCase("697")
							|| responseObject.getResponseCode().equalsIgnoreCase("797")) {
						setOtp("");
						addFieldError("invalidOtp", responseObject.getResponseMessage());
					} else {
						addFieldError(CrmFieldType.EMAILID.getName(), responseObject.getResponseMessage());
					}

				}
				logger.info("Response Code is not equal to success code");
				return INPUT;
			}

			// To add custom field to each log for all activities of this user
			MDC.put(Constants.CRM_LOG_USER_PREFIX.getValue(), getEmailId());
			logger.info("logged in : " + getEmailId());

			loginHistory = loginHistoryDao.findLastLoginByUser(getEmailId());

			SessionCleaner.cleanSession(sessionMap);
			sessionMap = (SessionMap<String, Object>) ActionContext.getContext().getSession();

			user = loginAuthenticator.getUser();



			if (user.isPasswordExpired()) {
				return passwordChange;
			}


			sessionMap.put("TOTPMAIL", emailId);
			if(StringUtils.isBlank(user.getGoogle2FASecretkey()) && user.isTfaFlag()) {
				logger.info("Success Return Login action Page QR PAGE");
				return "GoogleTwoFatorAuthenticationQRPage";
			}else if(StringUtils.isNotBlank(user.getGoogle2FASecretkey())  && user.isTfaFlag()) {
				logger.info("Success Return Login action Page TFA PAGE");
				return "GoogleTwoFatorAuthenticationOTPPage";
			}
			sessionMap.put(Constants.USER.getValue(), user);
			sessionMap.put(Constants.LAST_LOGIN.getValue(), loginHistory);
			logger.info("Custom Token "+TokenHelper.generateGUID());
			sessionMap.put(Constants.CUSTOM_TOKEN.getValue(), TokenHelper.generateGUID());
			sessionMap.put(Constants.SEGMENT.getValue(), user.getSegment());
			if (user.getRole() != null) {
				List<RoleWiseMenu> accessMenu = roleWiseMenuDao.getbyRoleId(user.getRole().getId());
				List<Permission> permissions = accessMenu.stream().map(RoleWiseMenu::getPermission)
						.collect(Collectors.toList());
				Map<String, String> accessByMenu = menuService.getMenuWisePermissions(permissions);
				List<Menu> menus = permissions.stream().map(Permission::getMenu).collect(Collectors.toList());
				List<Menu> menuTree = menuService.prepareMenuTree(menus);
				// TODO: remove parent action in case of we add entry in permission table for
				// root action menus.
//				List<String> parentAction = menuTree.stream()
//						.filter(menu -> StringUtils.isNotBlank(menu.getActionName())).map(Menu::getActionName)
//						.collect(Collectors.toList());
//				List<String> accessibleActions = menus.stream().map(Menu::getActionName).collect(Collectors.toList());
//				accessibleActions.addAll(parentAction);
//				sessionMap.put(Constants.ACCESSIBLE_ACTION.getValue(), String.join(",", accessibleActions));
				sessionMap.put(Constants.ACCESSIBLE_MENUS.getValue(), menuTree);
				sessionMap.put(Constants.ACCESSIBLE_MENUS_JSON.getValue(),
						new ObjectMapper().writeValueAsString(menuTree));
				sessionMap.put(Constants.ACCESSIBLE_PERMISSION.getValue(),
						new ObjectMapper().writeValueAsString(accessByMenu));
			}
//			HttpSession session = request.getSession();
//			session.setAttribute("sessionMap", sessionMap);

			List<ActionMessageByAction> actionMessagesByActionList = actionMessageByActionDao
					.getActionMessagesByAction();
			Map<String, ActionMessageByAction> actionMessagesByaction = actionMessagesByActionList.stream()
					.collect(Collectors.toMap(ActionMessageByAction::getAction, Function.identity()));
			sessionMap.put(Constants.ACTION_MESSAGE_BY_ACTION.getValue(), actionMessagesByaction);
			// TODO: remove this porsion of code which is set permission base on user type as it's moved on as per role
			if (user.getUserType().equals(UserType.SUBUSER) || user.getUserType().equals(UserType.SUBACQUIRER)
					|| user.getUserType().equals(UserType.SUBADMIN)
					|| user.getUserType().equals(UserType.SUBSUPERADMIN)) {
				/*
				 * Set<Roles> roles = user.getRoles(); Set<Permissions> permissions =
				 * roles.iterator().next().getPermissions(); if (!permissions.isEmpty()) {
				 * StringBuilder perms = new StringBuilder(); Iterator<Permissions> itr =
				 * permissions.iterator(); while (itr.hasNext()) { PermissionType permissionType
				 * = itr.next().getPermissionType();
				 * perms.append(permissionType.getPermission()); perms.append("-"); }
				 * perms.deleteCharAt(perms.length() - 1);
				 * setPermissionString(perms.toString());
				 * sessionMap.put(Constants.USER_PERMISSION.getValue(), perms.toString()); }
				 */
				sessionMap.put(Constants.USER_PERMISSION.getValue(), "");
			}

			else if (user.getUserType().equals(UserType.SUBADMIN)) {
				logger.info("User Type "+user.getUserType());
				redirectUrl = user.getLastActionName();
			}

			// redirecting to lastActionName
			/*
			 * redirectUrl = user.getLastActionName(); if (redirectUrl != null) {
			 *
			 * // Quick fix for "resellerLists" action (New name is "adminResellers" // that
			 * was refactored --Harpreet if (redirectUrl.equalsIgnoreCase("resellerLists"))
			 * { redirectUrl = CrmActions.ADMIN_RESELLER_LISTS.getValue(); return
			 * "redirect"; } return "redirect"; }
			 */
			logger.info("Return Success Login Action");
			return SUCCESS;
		} catch (Exception exception) {
			logger.error("Exception", exception);
			return ERROR;
		}
	}

	@Override
	public void validate() {
		/*
		 * if (validator.validateBlankField(getCaptcha())) {
		 * addFieldError(CrmFieldType.CAPTCHA.getName(),
		 * validator.getResonseObject().getResponseMessage()); } else { String
		 * sessionCaptcha = (String) sessionMap.get(CaptchaServlet.CAPTCHA_KEY); if
		 * (!captcha.equalsIgnoreCase(sessionCaptcha)) { setCaptcha("");
		 * addFieldError(CrmFieldType.CAPTCHA.getName(),
		 * CrmFieldConstants.INVALID_CAPTCHA.getValue());
		 *
		 * } }
		 */
		// Clean Session Captcha
		// sessionMap.put(CaptchaServlet.CAPTCHA_KEY,"");
		try {
			logger.info("Login Action Validation "+getPassword());
			String plainPassword = passwordDecryptor.decrypt(getPassword(), PasswordDecryptor.SECRET_KEY);
			password = plainPassword;
			setPassword(password);

		} catch (Exception e) {
			logger.error("Failed to decrypt Password "+e);
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		if ((validator.validateBlankFields(getEmailId()))) {
			addFieldError(CrmFieldType.EMAILID.getName(), validator.getResonseObject().getResponseMessage());
			return;
		}

		if (loginType.equals("pwd")) {
			if (validator.validateBlankFields(getPassword())) {
				addFieldError(CrmFieldType.PASSWORD.getName(), validator.getResonseObject().getResponseMessage());
				return;
			}
		} else if (loginType.equals("userOtp")) {
			if (validator.validateBlankFields(getOtp())) {
				addFieldError(CrmFieldType.OTP.getName(), validator.getResonseObject().getResponseMessage());
				return;
			}
		}

	}

	@Override
	public void setServletRequest(HttpServletRequest request) {
		this.request = request;
	}

	public String getEmailId() {
		return emailId.trim();
	}

	public void setEmailId(String emailId) {
		this.emailId = emailId;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	/*
	 * public String getCaptcha() { return captcha; }
	 *
	 * public void setCaptcha(String captcha) { this.captcha = captcha; }
	 *
	 * public String getCaptchaCode() { return captchaCode; }
	 *
	 * public void setCaptchaCode(String captchaCode) { this.captchaCode =
	 * captchaCode; }
	 */

	public String getRedirectUrl() {
		return redirectUrl;
	}

	public void setRedirectUrl(String redirectUrl) {
		this.redirectUrl = redirectUrl;
	}

	public String getPermissionString() {
		return permissionString;
	}

	public void setPermissionString(String permissionString) {
		this.permissionString = permissionString;
	}

	public String getLoginType() {
		return loginType;
	}

	public void setLoginType(String loginType) {
		this.loginType = loginType;
	}

	public String getOtp() {
		return otp;
	}

	public void setOtp(String otp) {
		this.otp = otp;
	}
	public String qrPage() {
		System.out.println("Check");
		return "success";
	}
	public String otpPage() {
		System.out.println("Check2");
		return "success";
	}

}
