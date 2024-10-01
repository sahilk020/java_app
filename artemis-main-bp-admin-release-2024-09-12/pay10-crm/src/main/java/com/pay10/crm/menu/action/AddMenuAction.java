package com.pay10.crm.menu.action;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.opensymphony.xwork2.ModelDriven;
import com.pay10.commons.user.Menu;
import com.pay10.commons.user.User;
import com.pay10.commons.util.Constants;
import com.pay10.commons.util.CrmFieldType;
import com.pay10.commons.util.CrmValidator;
import com.pay10.crm.action.AbstractSecureAction;
import com.pay10.crm.menu.service.MenuService;

public class AddMenuAction extends AbstractSecureAction implements ModelDriven<Menu> {

	private static final long serialVersionUID = -7049028879275327195L;
	private static final Logger logger = LoggerFactory.getLogger(AddMenuAction.class.getName());

	@Autowired
	private CrmValidator validator;

	@Autowired
	private MenuService menuService;

	private Menu menu;

	@Override
	public String execute() {

		try {
			logger.info("add menu details invoked");
			User user = (User) sessionMap.get(Constants.USER.getValue());
			menu.setCreatedAt(System.currentTimeMillis());
			menu.setCreatedBy(user.getEmailId());
			menuService.create(menu);
			logger.info("menu created. name={}", menu.getMenuName());
			return SUCCESS;

		} catch (Exception ex) {
			logger.error("Exception", ex);
			return ERROR;
		}
	}

	@Override
	public void validate() {

		if (validator.validateBlankField(menu.getMenuName())) {
			addFieldError(CrmFieldType.MENU_NAME.getName(), validator.getResonseObject().getResponseMessage());
		}
	}

	@Override
	public Menu getModel() {
		return menu;
	}

	public Menu getMenu() {
		return menu;
	}

	public void setMenu(Menu menu) {
		this.menu = menu;
	}
}
