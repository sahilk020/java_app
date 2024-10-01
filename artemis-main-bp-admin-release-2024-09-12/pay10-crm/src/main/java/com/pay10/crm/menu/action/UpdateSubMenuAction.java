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
import com.pay10.crm.menu.service.SubMenuService;

public class UpdateSubMenuAction extends AbstractSecureAction implements ModelDriven<Menu> {

	private static final long serialVersionUID = -7049028879275327195L;
	private static final Logger logger = LoggerFactory.getLogger(UpdateSubMenuAction.class.getName());

	@Autowired
	private CrmValidator validator;

	@Autowired
	private SubMenuService subMenuService;

	private Menu subMenu;

	@Override
	public String execute() {
		try {
			User user = (User) sessionMap.get(Constants.USER.getValue());
			subMenu.setUpdatedAt(System.currentTimeMillis());
			subMenu.setUpdatedBy(user.getEmailId());
			subMenuService.update(subMenu);
			logger.info("sub menu updated. name={}", subMenu.getMenuName());
			return SUCCESS;

		} catch (Exception ex) {
			logger.error("Exception", ex);
			return ERROR;
		}
	}

	@Override
	public void validate() {
		if (validator.validateBlankField(subMenu.getId())) {
			addFieldError(CrmFieldType.MENU_ID.getName(), validator.getResonseObject().getResponseMessage());
		}
		if (validator.validateBlankField(subMenu.getMenuName())) {
			addFieldError(CrmFieldType.MENU_NAME.getName(), validator.getResonseObject().getResponseMessage());
		}
		if (validator.validateBlankField(subMenu.getParentId())) {
			addFieldError(CrmFieldType.PARENT_ID.getName(), validator.getResonseObject().getResponseMessage());
		}
	}

	@Override
	public Menu getModel() {
		return subMenu;
	}

	public Menu getSubMenu() {
		return subMenu;
	}

	public void setSubMenu(Menu subMenu) {
		this.subMenu = subMenu;
	}
}
