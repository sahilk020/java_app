package com.pay10.crm.role.action;

import com.pay10.commons.dao.UserGroupDao;
import com.pay10.commons.user.Role;
import com.pay10.commons.user.User;
import com.pay10.commons.user.UserGroup;
import com.pay10.commons.util.Constants;
import com.pay10.crm.action.AbstractSecureAction;
import com.pay10.crm.role.Dto.ResponseEnvelope;
import com.pay10.crm.role.Dto.UserRoleDto;
import com.pay10.crm.role.service.RoleService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class AddUserRoleAction extends AbstractSecureAction {

	private static final long serialVersionUID = -7049028879275327195L;
	private static final Logger logger = LoggerFactory.getLogger(AddUserRoleAction.class.getName());

	@Autowired
	private RoleService roleService;
	
	@Autowired
	private UserGroupDao userGroupDao;

	private String roleName;
	private String description;
	private boolean isActive;
	private User user;
	private long userGroupId;
	private long applicationId;

	LocalDateTime now = LocalDateTime.now();
	DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");
	private UserRoleDto userRoleDto= new UserRoleDto();
	@Override
	public String execute() {
		try {
			logger.info("groupId.....={}",userGroupId);
			logger.info("get applicationId while creating role.....={}",applicationId);
			userRoleDto.setRoleName(roleName);
			userRoleDto.setDescription(description);
			userRoleDto.setActive(isActive);
			userRoleDto.setGroupId(userGroupId);
			userRoleDto.setApplicationId(applicationId);
			//String url="http://10.0.1.165:80/auth-service/api/v1/role";
			String url="https://globaldev.pay10.com/auth-service/api/v1/role";

			HttpHeaders headers = new HttpHeaders();
			headers.set("accept", "application/json");
			HttpEntity requestEntity = new HttpEntity(userRoleDto, headers);
			RestTemplate restTemplate = new RestTemplate();

			ParameterizedTypeReference<ResponseEnvelope<UserRoleDto>> reponse =
					new ParameterizedTypeReference<ResponseEnvelope<UserRoleDto>>() {};
			ResponseEntity<ResponseEnvelope<UserRoleDto>> response = restTemplate.exchange(url, HttpMethod.POST,requestEntity, reponse);

			return SUCCESS;

		} catch (Exception ex) {
			logger.error("Exception", ex);
			return ERROR;
		}
	}

	private Role getInstance() {
		
		String formatDateTime = now.format(formatter);
		Role role = new Role();
		role.setRoleName(getRoleName());
		role.setDescription(getDescription());
		role.setActive(isActive());
		role.setCreatedBy(user.getEmailId());
		/* role.setCreatedAt(System.currentTimeMillis()); */
		role.setCreatedAt(formatDateTime);
		role.setUserGroup(userGroupDao.getUserGroup(getUserGroupId()));
		return role;
	}

	public String getRoleName() {
		return roleName;
	}

	public void setRoleName(String roleName) {
		this.roleName = roleName;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public boolean isActive() {
		return isActive;
	}

	public void setActive(boolean isActive) {
		this.isActive = isActive;
	}

	public long getUserGroupId() {
		return userGroupId;
	}

	public void setUserGroupId(long userGroupId) {
		this.userGroupId = userGroupId;
	}

	public long getApplicationId() {
		return applicationId;
	}

	public void setApplicationId(long applicationId) {
		this.applicationId = applicationId;
	}
}
