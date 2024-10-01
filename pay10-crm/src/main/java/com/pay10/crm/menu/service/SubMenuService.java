package com.pay10.crm.menu.service;

import java.util.List;

import com.pay10.commons.user.Menu;

public interface SubMenuService {

	public void create(Menu subMenu);

	public List<Menu> getSubMenus();

	public Menu getSubMenu(long id);

	public void update(Menu subMenu);

	public void delete(long id);
}
