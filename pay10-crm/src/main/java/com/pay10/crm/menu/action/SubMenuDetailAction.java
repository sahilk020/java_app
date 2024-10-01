package com.pay10.crm.menu.action;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.pay10.commons.user.Menu;
import com.pay10.commons.user.User;
import com.pay10.commons.user.UserType;
import com.pay10.commons.util.Constants;
import com.pay10.commons.util.DataEncoder;
import com.pay10.crm.action.AbstractSecureAction;
import com.pay10.crm.menu.service.MenuService;

/**
 * @author Jay
 *
 */
public class SubMenuDetailAction extends AbstractSecureAction {

	private static final long serialVersionUID = 3255958300818815691L;
	private static Logger logger = LoggerFactory.getLogger(SubMenuDetailAction.class.getName());
	@Autowired
	private DataEncoder encoder;

	@Autowired
	private MenuService menuService;

	private List<Menu> aaData;
	private User sessionUser = new User();

	@Override
	public String execute() {
		sessionUser = (User) sessionMap.get(Constants.USER.getValue());
		try {
			if (sessionUser.getUserType().equals(UserType.ADMIN)
					|| sessionUser.getUserType().equals(UserType.SUBADMIN)) {
				setAaData(encoder.encodeMenu(menuService.getMenus()));
			}
			return SUCCESS;
		} catch (Exception exception) {
			logger.error("Exception", exception);
			return ERROR;
		}
	}

	public List<Menu> getAaData() {
		return aaData;
	}

	public void setAaData(List<Menu> aaData) {
		this.aaData = aaData;
	}

}
