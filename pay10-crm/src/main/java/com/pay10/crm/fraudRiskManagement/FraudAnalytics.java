package com.pay10.crm.fraudRiskManagement;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts2.ServletActionContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.pay10.commons.user.Merchants;
import com.pay10.commons.user.User;
import com.pay10.commons.user.UserDao;
import com.pay10.commons.util.Constants;
import com.pay10.crm.action.AbstractSecureAction;
import com.pay10.crm.action.ChartAction;

public class FraudAnalytics extends AbstractSecureAction {

	/**
	 * Sweety
	 */
	private static final long serialVersionUID = -4995274739666428522L;
	private static Logger logger = LoggerFactory.getLogger(FraudAnalytics.class.getName());
	private List<Merchants> listMerchants = new ArrayList<Merchants>();
	@Autowired
	private UserDao userDao;
	@Autowired
	HttpServletRequest request = ServletActionContext.getRequest();

	//HttpServletResponse response = ServletActionContext.getResponse();
	private User sessionUser = null;
	private String dateRange;
	
	@SuppressWarnings("unchecked")
	public String execute() {
		sessionUser = (User) sessionMap.get(Constants.USER.getValue());
		setListMerchants(userDao.getMerchantList(sessionMap.get(Constants.SEGMENT.getValue()).toString(),
				sessionUser.getRole().getId()));
		return SUCCESS;
	}

	public List<Merchants> getListMerchants() {
		return listMerchants;
	}

	public void setListMerchants(List<Merchants> listMerchants) {
		this.listMerchants = listMerchants;
	}
	
	public String getDateRange() {
		return dateRange;
	}

	public void setDateRange(String dateRange) {
		this.dateRange = dateRange;
	}
}
