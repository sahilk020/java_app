package com.pay10.crm.menu.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;

import com.pay10.commons.dao.MenuDao;
import com.pay10.commons.user.Menu;

public class SubMenuServiceImpl implements SubMenuService {

	@Autowired
	private MenuDao menuDao;

	@Override
	public void create(Menu subMenu) {
		menuDao.save(subMenu);

	}

	@Override
	public List<Menu> getSubMenus() {
		List<Menu> menus = menuDao.getMenus();
		return menus.stream().filter(menu -> menu.getParentId() != 0).collect(Collectors.toList());
	}

	@Override
	public Menu getSubMenu(long id) {
		return menuDao.getMenu(id);
	}

	@Override
	public void update(Menu subMenu) {
		menuDao.update(subMenu);
	}

	@Override
	public void delete(long id) {
		menuDao.delete(id);
	}

}
