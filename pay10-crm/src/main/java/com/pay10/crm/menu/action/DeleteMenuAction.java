package com.pay10.crm.menu.action;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.pay10.commons.util.CrmFieldType;
import com.pay10.commons.util.CrmValidator;
import com.pay10.crm.action.AbstractSecureAction;
import com.pay10.crm.menu.service.MenuService;

public class DeleteMenuAction extends AbstractSecureAction {

	private static final long serialVersionUID = -7049028879275327195L;
	private static final Logger logger = LoggerFactory.getLogger(DeleteMenuAction.class.getName());

	@Autowired
	private CrmValidator validator;

	@Autowired
	private MenuService menuService;

	private long id;

	@Override
	public String execute() {
		try {
			menuService.delete(getId());
			logger.info("menu deleted. menuId={}", getId());
		} catch (Exception exception) {
			logger.error("Exception", exception);
			return ERROR;
		}
		return INPUT;
	}

	@Override
	public void validate() {
		if (validator.validateBlankField(getId())) {
			addFieldError(CrmFieldType.MENU_ID.getName(), validator.getResonseObject().getResponseMessage());
		}
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}
}
