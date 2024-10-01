package com.pay10.crm.role.action;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.pay10.commons.dao.UserGroupDao;
import com.pay10.commons.user.*;
import com.pay10.crm.action.AbstractSecureAction;
import com.pay10.crm.menu.service.MenuService;
import com.pay10.crm.role.Dto.*;
import com.pay10.crm.role.service.RoleService;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

public class UserEditRoleAction extends AbstractSecureAction {

	private static final long serialVersionUID = -7049028879275327195L;
	private static final Logger logger = LoggerFactory.getLogger(UserEditRoleAction.class.getName());

	@Autowired
	private MenuService menuService;

	@Autowired
	private RoleService roleService;

	@Autowired
	private UserGroupDao userGroupDao;

	private long id;
	private String roleName;
	private String description;
	private boolean isActive;
	private List<MenuDto> menus;
	private String menuTree;
	private List<Long> permissionAccess;
	private long userGroupId;
	private List<UserGroup> userGroups;

	private List<ApplicationDto> aaData;

	@Override
	public String execute() {
		try {

			//String url="http://10.0.1.165:80/auth-service/api/v1/role/"+getId();
			String url="https://globaldev.pay10.com/auth-service/api/v1/role/"+getId();

			RestTemplate restTemplate = new RestTemplate();

			ParameterizedTypeReference<ResponseEnvelope<UserRoleDto>> res = new ParameterizedTypeReference<ResponseEnvelope<UserRoleDto>>() {};
			ResponseEntity<ResponseEnvelope<UserRoleDto>> response = restTemplate.exchange(url, HttpMethod.GET,null, res);
			UserRoleDto role = response.getBody().getPayLoad();
		    if(role.getApplicationId()>0){
				//GetApplicationList

				ApplicationDto list = new ApplicationDto();
				List<ApplicationDto> setList= new ArrayList<>();

				//String applicationUrl = "http://10.0.1.165:80/auth-service/api/v1/client/"+role.getApplicationId();
				String applicationUrl = "https://globaldev.pay10.com/auth-service/api/v1/client/"+role.getApplicationId();

				RestTemplate restTemp = new RestTemplate();

				ParameterizedTypeReference<ResponseEnvelope<ApplicationDto>> reponse = new ParameterizedTypeReference<ResponseEnvelope<ApplicationDto>>() {
				};
				ResponseEntity<ResponseEnvelope<ApplicationDto>> resp = restTemp.exchange(applicationUrl, HttpMethod.GET, null, reponse);
				logger.info("Application list ......={}", resp.getBody().getPayLoad());
				list = resp.getBody().getPayLoad();
				setList.add(list);
				setAaData(setList);
			}
            logger.info("role in UserEditRoleAction.....={}",role);
			setRoleName(role.getRoleName());
			setDescription(role.getDescription());
			setActive(role.isActive());

			//setMenus(prepareAllMenuTree(menuService.getAllMenus()));
			setMenus(prepareAllMenuTree());
			setMenuTree(new ObjectMapper().writeValueAsString(getMenus()));
			setPermissionAccess(getAccessPermission(getId()));
//			if (role.getUserGroup() != null) {
//				setUserGroupId(role.getUserGroup().getId());
//			}
			//setUserGroups(userGroupDao.getUserGroups());
		} catch (Exception exception) {
			logger.error("Exception", exception);
			return ERROR;
		}
		return INPUT;
	}


	public Map<Long, Set<PermissionDto>> getPermissionsByMenuId() {
		List<PermissionDto> permissions;
		//String url="http://10.0.1.165:80/auth-service/api/v1/permission";
		String url="https://globaldev.pay10.com/auth-service/api/v1/permission";

		RestTemplate restTemplate = new RestTemplate();

		ParameterizedTypeReference<ResponseEnvelope<List<PermissionDto>>> res=
				new ParameterizedTypeReference<ResponseEnvelope<List<PermissionDto>>>() {};
		ResponseEntity<ResponseEnvelope<List<PermissionDto>>> response = restTemplate.exchange(url, HttpMethod.GET,null, res);

		//List<Permission> permissions = findAll() //api call;
		permissions=response.getBody().getPayLoad();
		logger.info("permissions in UserEditRoleAction....={}",permissions);
		//Map<Long, Set<Permission>> permissionsByMenu = new HashMap<>();
		Map<Long, Set<PermissionDto>> permissionsByMenu = new HashMap<>();

		permissions.forEach(permission -> {
		long menu = permission.getMenuId();
			if (!permissionsByMenu.containsKey(menu)) {
				permissionsByMenu.put(menu, new HashSet<>());
			}
			permissionsByMenu.get(menu).add(permission);
		});
		return permissionsByMenu;
	}

	private List<MenuDto> prepareAllMenuTree() throws JsonProcessingException {
		List<MenuDto> menus = new ArrayList<>();
		//String url="http://10.0.1.165:80/auth-service/api/v1/menu/";
		String url="https://globaldev.pay10.com/auth-service/api/v1/menu/";

		RestTemplate restTemplate = new RestTemplate();

		ParameterizedTypeReference<ResponseEnvelope<List<MenuDto>>> res=
				new ParameterizedTypeReference<ResponseEnvelope<List<MenuDto>>>() {};
		ResponseEntity<ResponseEnvelope<List<MenuDto>>> response = restTemplate.exchange(url, HttpMethod.GET,null, res);

		menus=response.getBody().getPayLoad();
		//api for menu
		logger.info("menu in UserEditRoleAction....={}",menus);
		//Map<Long, Set<Permission>> permissionByMenu = permissionDao.getPermissionsByMenuId();
		Map<Long, Set<PermissionDto>> permissionByMenu = getPermissionsByMenuId();//api call

		menus.forEach(menu -> {
			if (StringUtils.isNotBlank(menu.getActionName()) || menu.getParentId() > 0) {
				menu.setPermissions(permissionByMenu.get(menu.getId()));
			}
		});
		Map<Long, Set<MenuDto>> subMenuByParents = menus.stream().filter(menu -> menu.getParentId() > 0).collect(
				Collectors.groupingBy(MenuDto::getParentId, Collectors.mapping(Function.identity(), Collectors.toSet())));
		List<MenuDto> parents = menus.stream().filter(menu -> menu.getParentId() == 0).collect(Collectors.toList());
		parents.forEach(menu -> {
			menu.setSubMenus(subMenuByParents.get(menu.getId()));
		});
		return parents;
	}

	private List<Long> getAccessPermission(long roleId) {
		//String url="http://10.0.1.165:80/auth-service/api/v1/role/"+roleId+"/permission";
		String url="https://globaldev.pay10.com/auth-service/api/v1/role/"+roleId+"/permission";

		RestTemplate restTemplate = new RestTemplate();

		ParameterizedTypeReference<ResponseEnvelope<List<Long>>> reponse =
				new ParameterizedTypeReference<ResponseEnvelope<List<Long>>>() {};
		ResponseEntity<ResponseEnvelope<List<Long>>> response = restTemplate.exchange(url, HttpMethod.GET,null, reponse);

		List<Long> rolewiseMenus = response.getBody().getPayLoad();
		logger.info("roleWise menu={}",rolewiseMenus);
		return rolewiseMenus;
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

	public List<MenuDto> getMenus() {
		return menus;
	}

	public void setMenus(List<MenuDto> menus) {
		this.menus = menus;
	}

	public String getMenuTree() {
		return menuTree;
	}

	public void setMenuTree(String menuTree) {
		this.menuTree = menuTree;
	}

	public List<Long> getPermissionAccess() {
		return permissionAccess;
	}

	public void setPermissionAccess(List<Long> permissionAccess) {
		this.permissionAccess = permissionAccess;
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
	public List<ApplicationDto> getAaData() {
		return aaData;
	}

	public void setAaData(List<ApplicationDto> aaData) {
		this.aaData = aaData;
	}

}
