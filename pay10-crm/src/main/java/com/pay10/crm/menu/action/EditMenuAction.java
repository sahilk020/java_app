package com.pay10.crm.menu.action;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.pay10.commons.user.Menu;
import com.pay10.crm.action.AbstractSecureAction;
import com.pay10.crm.menu.service.MenuService;

public class EditMenuAction extends AbstractSecureAction {

	private static final long serialVersionUID = -7049028879275327195L;
	private static final Logger logger = LoggerFactory.getLogger(EditMenuAction.class.getName());

	@Autowired
	private MenuService menuService;

	private long id;
	private String menuName;
	private String description;
	private Boolean isActive;

	@Override
	public String execute() {
		try {
			logger.info("menuId from param={}", getId());
			Menu menu = menuService.getMenu(getId());
			setActive(menu.isActive());
			setMenuName(menu.getMenuName());
			setDescription(menu.getDescription());
		} catch (Exception exception) {
			logger.error("Exception", exception);
			return ERROR;
		}
		return INPUT;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getMenuName() {
		return menuName;
	}

	public void setMenuName(String menuName) {
		this.menuName = menuName;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Boolean isActive() {
		return isActive;
	}

	public void setActive(Boolean isActive) {
		this.isActive = isActive;
	}
}
