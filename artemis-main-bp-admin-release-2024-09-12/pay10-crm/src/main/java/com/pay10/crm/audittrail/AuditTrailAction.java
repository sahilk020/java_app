package com.pay10.crm.audittrail;

import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.pay10.commons.user.User;
import com.pay10.commons.user.UserDao;
import com.pay10.commons.util.Constants;
import com.pay10.commons.util.PropertiesManager;
import com.pay10.crm.action.AbstractSecureAction;

public class AuditTrailAction extends AbstractSecureAction {

	private static final long serialVersionUID = 690514249678307821L;
	private static final Logger logger = LoggerFactory.getLogger(AuditTrailAction.class.getName());

	@Autowired
	private UserDao userDao;

	private List<User> users;
	private String diffApiUrl;
	private String diffPdfApiUrl;

	@SuppressWarnings("unchecked")
	@Override
	public String execute() {
		try {
			setDiffApiUrl(PropertiesManager.propertiesMap.get(Constants.DIFF_HIGHLIGHT_API.getValue()));
			setDiffPdfApiUrl(PropertiesManager.propertiesMap.get(Constants.DIFF_PDF_API.getValue()));
//			List<User> adminAndSubAdmin = ((List<User>) userDao.findAll()).stream()
//					.filter(user -> StringUtils.equalsAnyIgnoreCase(user.getUserType().name(), "ADMIN", "SUBADMIN"))
//					.collect(Collectors.toList());
			List<User> adminAndSubAdmin =userDao.getUserForAuditTrail();
			setUsers(adminAndSubAdmin);
			return INPUT;
		} catch (Exception ex) {
			logger.error("Exception", ex);
			return ERROR;
		}
	}

	public List<User> getUsers() {
		return users;
	}

	public void setUsers(List<User> users) {
		this.users = users;
	}

	public String getDiffApiUrl() {
		return diffApiUrl;
	}

	public void setDiffApiUrl(String diffApiUrl) {
		this.diffApiUrl = diffApiUrl;
	}

	public String getDiffPdfApiUrl() {
		return diffPdfApiUrl;
	}

	public void setDiffPdfApiUrl(String diffPdfApiUrl) {
		this.diffPdfApiUrl = diffPdfApiUrl;
	}
}
