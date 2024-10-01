package com.pay10.crm.action;

import java.util.ArrayList;
import java.util.List;


import org.springframework.beans.factory.annotation.Autowired;


import com.pay10.commons.action.AbstractSecureAction;
import com.pay10.commons.user.Merchants;
import com.pay10.commons.user.User;
import com.pay10.commons.user.UserDao;
import com.pay10.commons.util.Constants;


public class TDRBifurcationReport extends AbstractSecureAction {
	private static final long serialVersionUID = -1806801325621922073L;
	List<Merchants> merchantList = new ArrayList<>();

	@Autowired
	private UserDao userDao;

	public String execute() {
		String segment = (String) sessionMap.get(Constants.SEGMENT.getValue());
		User user = (User) sessionMap.get(Constants.USER.getValue());
		long roleId = user.getRole().getId();
		setMerchantList(userDao.getActiveMerchant(segment, roleId));
		return SUCCESS;
	}

	public List<Merchants> getMerchantList() {
		return merchantList;
	}

	public void setMerchantList(List<Merchants> merchantList) {
		this.merchantList = merchantList;
	}

}
