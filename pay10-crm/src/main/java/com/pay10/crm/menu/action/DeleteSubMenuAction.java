package com.pay10.crm.menu.action;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.opensymphony.xwork2.ModelDriven;
import com.pay10.commons.user.Menu;
import com.pay10.commons.util.CrmFieldType;
import com.pay10.commons.util.CrmValidator;
import com.pay10.crm.action.AbstractSecureAction;
import com.pay10.crm.menu.service.SubMenuService;

public class DeleteSubMenuAction extends AbstractSecureAction implements ModelDriven<Menu> {

	private static final long serialVersionUID = -7049028879275327195L;
	private static final Logger logger = LoggerFactory.getLogger(DeleteSubMenuAction.class.getName());

	@Autowired
	private CrmValidator validator;

	@Autowired
	private SubMenuService subMenuService;

	private Menu subMenu;

	@Override
	public String execute() {
		try {
			subMenuService.delete(subMenu.getId());
			logger.info("sub menu deleted. subMenuId={}", subMenu.getId());
		} catch (Exception exception) {
			logger.error("Exception", exception);
			return ERROR;
		}
		return INPUT;
	}

	@Override
	public void validate() {
		if (validator.validateBlankField(subMenu.getId())) {
			addFieldError(CrmFieldType.MENU_ID.getName(), validator.getResonseObject().getResponseMessage());
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
