package com.pay10.crm.role.action;

import com.pay10.commons.dao.UserGroupDao;
import com.pay10.commons.user.Permission;
import com.pay10.commons.user.Role;
import com.pay10.commons.user.User;
import com.pay10.commons.util.Constants;
import com.pay10.crm.action.AbstractSecureAction;
import com.pay10.crm.role.Dto.ResponseEnvelope;
import com.pay10.crm.role.Dto.RoleDto;
import com.pay10.crm.role.Dto.RolePermissionDto;
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
import java.util.ArrayList;
import java.util.List;

public class UpdateUserRoleAction extends AbstractSecureAction {

	private static final long serialVersionUID = -7049028879275327195L;
	private static final Logger logger = LoggerFactory.getLogger(UpdateUserRoleAction.class.getName());

	@Autowired
	private RoleService roleService;

	@Autowired
	private UserGroupDao userGroupDao;

	private String permissionAccess;
	private long id;
	private String roleName;
	private String description;
	private boolean isActive;
	private User user;
	private long userGroupId;
	private long applicationId;

	LocalDateTime now = LocalDateTime.now();
	DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");

	@Override
	public String execute() {
		try {
			logger.info("PermissionsId = {}", getPermissionAccess());
			logger.info("applicationId = {}", applicationId);
			user = (User) sessionMap.get(Constants.USER.getValue());
			updateRole(getId(), getPermissionInLong()); //get from api
			logger.info("execute:: role updated success. roleId={}, roleName={}", getId(), getRoleName());
			return SUCCESS;

		} catch (Exception ex) {
			logger.error("Exception", ex);
			return ERROR;
		}
	}

	private void updateRole(Long roleId, List<Long> permissionIds)
	{
	//
		logger.info("roleId...."+roleId+"......permissionIds+"+permissionIds);
		//String url="http://10.0.1.165:80/auth-service/api/v1/role/"+roleId+"/permission";
		String url="https://globaldev.pay10.com/auth-service/api/v1/role/"+roleId+"/permission";

		RestTemplate restTemplate = new RestTemplate();
		RolePermissionDto rolePermissionDto = new RolePermissionDto();
		rolePermissionDto.setPermissionIds(permissionIds);
		rolePermissionDto.setRoleId(roleId);
		ParameterizedTypeReference<ResponseEnvelope<RolePermissionDto>> res=
				new ParameterizedTypeReference<ResponseEnvelope<RolePermissionDto>>() {};
		HttpHeaders headers = new HttpHeaders();
		headers.set("accept", "application/json");
		HttpEntity requestEntity = new HttpEntity(rolePermissionDto, headers);
		ResponseEntity<ResponseEnvelope<RolePermissionDto>> response = restTemplate.exchange(url, HttpMethod.POST,requestEntity, res);
	    logger.info("update response...={}",response.getBody().getPayLoad());
	}

	private RoleDto prepareInstance() {
		String formatDateTime = now.format(formatter);
		//Role role = roleService.getRole(getId());//get from api
		RoleDto role;
		//String url="http://10.0.1.165:80/auth-service/api/v1/role/"+getId();
		String url="https://globaldev.pay10.com/auth-service/api/v1/role/"+getId();

		RestTemplate restTemplate = new RestTemplate();

		ParameterizedTypeReference<ResponseEnvelope<RoleDto>> res=
				new ParameterizedTypeReference<ResponseEnvelope<RoleDto>>() {};
		ResponseEntity<ResponseEnvelope<RoleDto>> response = restTemplate.exchange(url, HttpMethod.GET,null, res);
        role=response.getBody().getPayLoad();
		role.setActive(isActive());
		role.setDescription(getDescription());
		role.setRoleName(getRoleName());
		/* role.setUpdatedAt(System.currentTimeMillis()); */
		role.setUpdatedAt(formatDateTime);
		role.setUpdatedBy(user.getEmailId());
//		if (role.getUserGroup() == null || role.getUserGroup().getId() != getUserGroupId()) {
//			role.setUserGroup(userGroupDao.getUserGroup(getUserGroupId()));
//		}
		return role;
	}

	private List<Long> getPermissionInLong() {
		List<Long> permissionIds = new ArrayList<>();
		String[] permissions = getPermissionAccess().split(",");
		for (String permission : permissions) {
			permissionIds.add(Long.valueOf(permission));
		}
		return permissionIds;
	}

	public String getPermissionAccess() {
		return permissionAccess;
	}

	public void setPermissionAccess(String permissionAccess) {
		this.permissionAccess = permissionAccess;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
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
