package com.pay10.crm.action;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.apache.struts2.interceptor.ServletRequestAware;
import org.apache.struts2.util.TokenHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.pay10.commons.audittrail.ActionMessageByAction;
import com.pay10.commons.audittrail.ActionMessageByActionDao;
import com.pay10.commons.dao.RoleWiseMenuDao;
import com.pay10.commons.user.LoginHistory;
import com.pay10.commons.user.LoginHistoryDao;
import com.pay10.commons.user.Menu;
import com.pay10.commons.user.Permission;
import com.pay10.commons.user.PermissionType;
import com.pay10.commons.user.Permissions;
import com.pay10.commons.user.ResponseObject;
import com.pay10.commons.user.RoleWiseMenu;
import com.pay10.commons.user.Roles;
import com.pay10.commons.user.User;
import com.pay10.commons.user.UserDao;
import com.pay10.commons.user.UserType;
import com.pay10.commons.util.Constants;
import com.pay10.commons.util.CrmValidator;
import com.pay10.crm.actionBeans.LoginAuthenticator;
import com.pay10.crm.menu.service.MenuService;
import com.pay10.crm.util.PasswordDecryptor;

public class Login2FAGoogleAction extends AbstractSecureAction implements ServletRequestAware {
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
	@Autowired
	UserDao userDao;

	private static Logger logger = LoggerFactory.getLogger(Login2FAGoogleAction.class.getName());
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

	public String qrPage() {

		emailId = (String) sessionMap.get("TOTPMAIL");
		if (StringUtils.isBlank(emailId))
			return "faliure";
		else
			return "success";
	}

	public String otpPage() {
		logger.info("2FA Login Action");
		emailId = (String) sessionMap.get("TOTPMAIL");
		if (StringUtils.isBlank(emailId))
			return "faliure";
		else
			return "success";
	}

	public String OTPAunthenticated() {

		User user;
		ResponseObject responseObject = new ResponseObject();
		LoginHistory loginHistory = new LoginHistory();
		String passwordChange = "passwordChange";
		user = loginAuthenticator.getUser();
		emailId = (String) sessionMap.get("TOTPMAIL");
		try {
			if (StringUtils.isBlank(emailId)) {
				return "faliure";
			}

			user = userDao.findByEmailId(emailId);
			sessionMap.remove("TOTPMAIL");
			sessionMap.put(Constants.USER.getValue(), user);
			sessionMap.put(Constants.LAST_LOGIN.getValue(), loginHistory);
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
			// TODO: remove this porsion of code which is set permission base on user type
			// as it's moved on as per role
			if (user.getUserType().equals(UserType.SUBUSER) || user.getUserType().equals(UserType.SUBACQUIRER)
					|| user.getUserType().equals(UserType.SUBADMIN)
					|| user.getUserType().equals(UserType.SUBSUPERADMIN)) {

				Set<Roles> roles = user.getRoles();
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
					sessionMap.put(Constants.USER_PERMISSION.getValue(), perms.toString());
				}

				sessionMap.put(Constants.USER_PERMISSION.getValue(), "");
				System.out.println("Routing to home page");
				return "success";
			}

			else if (user.getUserType().equals(UserType.SUBADMIN)) {
				redirectUrl = user.getLastActionName();
			}
		} catch (Exception e) {
			// TODO: handle exception

			e.printStackTrace();
		}
		System.out.println("1 Routing to home page");
		return "success";
	}

	public LoginHistoryDao getLoginHistoryDao() {
		return loginHistoryDao;
	}

	public void setLoginHistoryDao(LoginHistoryDao loginHistoryDao) {
		this.loginHistoryDao = loginHistoryDao;
	}

	public CrmValidator getValidator() {
		return validator;
	}

	public void setValidator(CrmValidator validator) {
		this.validator = validator;
	}

	public LoginAuthenticator getLoginAuthenticator() {
		return loginAuthenticator;
	}

	public void setLoginAuthenticator(LoginAuthenticator loginAuthenticator) {
		this.loginAuthenticator = loginAuthenticator;
	}

	public RoleWiseMenuDao getRoleWiseMenuDao() {
		return roleWiseMenuDao;
	}

	public void setRoleWiseMenuDao(RoleWiseMenuDao roleWiseMenuDao) {
		this.roleWiseMenuDao = roleWiseMenuDao;
	}

	public MenuService getMenuService() {
		return menuService;
	}

	public void setMenuService(MenuService menuService) {
		this.menuService = menuService;
	}

	public ActionMessageByActionDao getActionMessageByActionDao() {
		return actionMessageByActionDao;
	}

	public void setActionMessageByActionDao(ActionMessageByActionDao actionMessageByActionDao) {
		this.actionMessageByActionDao = actionMessageByActionDao;
	}

	public PasswordDecryptor getPasswordDecryptor() {
		return passwordDecryptor;
	}

	public void setPasswordDecryptor(PasswordDecryptor passwordDecryptor) {
		this.passwordDecryptor = passwordDecryptor;
	}

	public UserDao getUserDao() {
		return userDao;
	}

	public void setUserDao(UserDao userDao) {
		this.userDao = userDao;
	}

	public static Logger getLogger() {
		return logger;
	}

	public static void setLogger(Logger logger) {
		Login2FAGoogleAction.logger = logger;
	}

	public int getPasswordAge() {
		return passwordAge;
	}

	public void setPasswordAge(int passwordAge) {
		this.passwordAge = passwordAge;
	}

	public String getRedirectUrl() {
		return redirectUrl;
	}

	public void setRedirectUrl(String redirectUrl) {
		this.redirectUrl = redirectUrl;
	}

	public String getEmailId() {
		return emailId;
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

	public String getOtp() {
		return otp;
	}

	public void setOtp(String otp) {
		this.otp = otp;
	}

	public HttpServletRequest getRequest() {
		return request;
	}

	public void setRequest(HttpServletRequest request) {
		this.request = request;
	}

	public String getLoginType() {
		return loginType;
	}

	public void setLoginType(String loginType) {
		this.loginType = loginType;
	}

	public String getPermissionString() {
		return permissionString;
	}

	public void setPermissionString(String permissionString) {
		this.permissionString = permissionString;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

}
