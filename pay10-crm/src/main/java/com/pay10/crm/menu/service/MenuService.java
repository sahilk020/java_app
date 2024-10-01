package com.pay10.crm.menu.service;

import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.pay10.commons.user.Menu;
import com.pay10.commons.user.Permission;

public interface MenuService {

	public void create(Menu menu);

	public List<Menu> getMenus();
	
	public List<Menu> getAllMenus();

	public Menu getMenu(long id);

	public void update(Menu menu);

	public void delete(long id);

	public List<Menu> prepareMenuTree(List<Menu> menus) throws JsonProcessingException;

	public List<Menu> prepareAllMenuTree(List<Menu> menus) throws JsonProcessingException;
	
	public Map<String, String> getMenuWisePermissions(List<Permission> permissions);

	public List<String> getAllActions();
	public List<Menu> getMenuByRoleId(long roleId);

	public Menu getParentIdById(long menuId);
}
