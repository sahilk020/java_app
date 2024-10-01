package com.pay10.crm.menu.service;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import com.pay10.commons.dao.HibernateSessionProvider;
import com.pay10.commons.user.RoleWiseMenu;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.Session;
import org.hibernate.query.Query;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.pay10.commons.dao.MenuDao;
import com.pay10.commons.dao.PermissionDao;
import com.pay10.commons.user.Menu;
import com.pay10.commons.user.Permission;

@Service
public class MenuServiceImpl implements MenuService {

	@Autowired
	private MenuDao menuDao;

	@Autowired
	private PermissionDao permissionDao;

	Logger logger= LoggerFactory.getLogger(MenuServiceImpl.class.getName());

	@Override
	public void create(Menu menu) {
		menuDao.save(menu);
	}

	@Override
	public List<Menu> getMenus() {
		List<Menu> menus = menuDao.getMenus();
		return menus.stream().filter(menu -> menu.getParentId() == 0).sorted(Comparator.comparingLong(Menu::getId))
				.collect(Collectors.toList());
	}

	@Override
	public Menu getMenu(long id) {
		return menuDao.getMenu(id);
	}

	@Override
	public void update(Menu menu) {
		menuDao.update(menu);
	}

	@Override
	public void delete(long id) {
		menuDao.delete(id);
	}

	public static void main(String[] args) throws JsonProcessingException {

		List<Menu> menus = new ArrayList<>();
		Menu menu1 = new Menu();
		menu1.setId(1);
		menu1.setActionName("s");
		menus.add(menu1);
		Map<Long, Set<Menu>> subMenuByParents = menus.stream()
				.filter(menu -> menu.getParentId() > 0 || StringUtils.isNotBlank(menu.getActionName()))
				.collect(Collectors.groupingBy(Menu::getParentId,
						Collectors.mapping(Function.identity(), Collectors.toSet())));
		System.out.println(new ObjectMapper().writeValueAsString(subMenuByParents));
	}

	@Override
	public List<Menu> prepareMenuTree(List<Menu> menus) throws JsonProcessingException {
		Map<Long, Set<Menu>> subMenuByParents = menus.stream()
				.filter(menu -> menu.getParentId() > 0 || StringUtils.isNotBlank(menu.getActionName()))
				.collect(Collectors.groupingBy(Menu::getParentId,
						Collectors.mapping(Function.identity(), Collectors.toSet())));
		Set<Long> parentIds = subMenuByParents.keySet();
		Set<Menu> parentMenu = subMenuByParents.get(0L);
		if (parentMenu != null && parentMenu.size() > 0) {
			parentIds.addAll(parentMenu.stream().map(Menu::getId).collect(Collectors.toList()));
		}
		if(!subMenuByParents.keySet().isEmpty()) {
			List<Menu> parents = menuDao.getMenusById(subMenuByParents.keySet());
			parents.forEach(menu -> {
				SortedSet<Menu> sortedSet = new TreeSet<>(Comparator.comparingInt(Menu::getDisplayOrder));
				sortedSet.addAll(subMenuByParents.get(menu.getId()));
				menu.setSubMenus(sortedSet);
			});
			return parents;
		}
//		logger.info("parents: {}", parents);
		return new ArrayList<>();
	}

	@Override
	public List<Menu> prepareAllMenuTree(List<Menu> menus) throws JsonProcessingException {
		Map<Long, Set<Permission>> permissionByMenu = permissionDao.getPermissionsByMenuId();
		menus.forEach(menu -> {
			if (StringUtils.isNotBlank(menu.getActionName()) || menu.getParentId() > 0) {
				menu.setPermissions(permissionByMenu.get(menu.getId()));
				//logger.info("Menu Permissions Setting{} ",menu.getPermissions());
			}
		});
		Map<Long, Set<Menu>> subMenuByParents = menus.stream().filter(menu -> menu.getParentId() > 0).collect(
				Collectors.groupingBy(Menu::getParentId, Collectors.mapping(Function.identity(), Collectors.toSet())));
		List<Menu> parents = menus.stream().filter(menu -> menu.getParentId() == 0).collect(Collectors.toList());
		parents.forEach(menu -> {
			menu.setSubMenus(subMenuByParents.get(menu.getId()));
			//logger.info("Menu sub menu Setting{} ",menu.getSubMenus());

		});
		//logger.info("Parent Size for menu map: {}",parents);

		return parents;
	}

	@Override
	public List<Menu> getAllMenus() {
		return menuDao.getMenus();
	}


	@Override
	public Map<String, String> getMenuWisePermissions(List<Permission> permissions) {

		Map<String, String> permissionMap = new HashMap<>();
		permissions.forEach(permission -> {
			Menu menu = permission.getMenu();
			if (!permissionMap.containsKey(menu.getActionName())) {
				permissionMap.put(menu.getActionName(), permission.getPermission());
			} else {
				String access = permissionMap.get(menu.getActionName());
				access = StringUtils.join(access, ",", permission.getPermission());
				permissionMap.put(menu.getActionName(), access);
			}

		});
		return permissionMap;
	}

	@Override
	public List<String> getAllActions() {
		return menuDao.getAllActionName();
	}

	@Override
	public List<Menu> getMenuByRoleId(long roleId) {
	 		try(Session session= HibernateSessionProvider.getSession()) {
				Query<RoleWiseMenu> query = session.createQuery("FROM RoleWiseMenu where roleId=:roleId", RoleWiseMenu.class)
						.setParameter("roleId", roleId);
				List<RoleWiseMenu> roleWiseMenus = query.getResultList();
				return roleWiseMenus.stream().map((roleWiseMenu ->
						roleWiseMenu.getPermission().getMenu())).collect(Collectors.toList());
			}
		}

	@Override
	public Menu getParentIdById(long menuId) {
		return menuDao.getParentIdById(menuId);

	}
}
