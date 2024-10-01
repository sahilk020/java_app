package com.pay10.crm.fraudReports.action;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.apache.struts2.ServletActionContext;
import org.springframework.beans.factory.annotation.Autowired;

import com.pay10.commons.action.AbstractSecureAction;
import com.pay10.commons.dao.FraudTransactionDetailsDao;
import com.pay10.commons.user.FraudTransactionDetails;
import com.pay10.commons.user.Merchants;
import com.pay10.commons.user.User;
import com.pay10.commons.user.UserDao;
import com.pay10.commons.util.Constants;

public class VelocityReportAction extends AbstractSecureAction {
	private static final long serialVersionUID = -1806801325621922073L;

	@Autowired
	private UserDao userDao;

	@Autowired
	private FraudTransactionDetailsDao fraudTransactionDetailsDao;

	List<Merchants> merchantList = new ArrayList<>();
	List<String> statuss = new ArrayList<>();
	List<String> ruleTypee = new ArrayList<>();
	private String merchant;
	private String dateRange;
	private String status;
	private String ruleType;

	HttpServletRequest request = ServletActionContext.getRequest();

	HttpServletResponse response = ServletActionContext.getResponse();
	private List<FraudTransactionDetails> aaData = new ArrayList<FraudTransactionDetails>();

	public List<String> getRuleTypee() {
		return ruleTypee;
	}

	public void setRuleTypee(List<String> ruleTypee) {
		this.ruleTypee = ruleTypee;
	}

	public List<String> getStatuss() {
		return statuss;
	}

	public void setStatuss(List<String> statuss) {
		this.statuss = statuss;
	}

	public List<Merchants> getMerchantList() {
		return merchantList;
	}

	public void setMerchantList(List<Merchants> merchantList) {
		this.merchantList = merchantList;
	}

	public String getMerchant() {
		return merchant;
	}

	public void setMerchant(String merchant) {
		this.merchant = merchant;
	}

	public String getDateRange() {
		return dateRange;
	}

	public void setDateRange(String dateRange) {
		this.dateRange = dateRange;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getRuleType() {
		return ruleType;
	}

	public void setRuleType(String ruleType) {
		this.ruleType = ruleType;
	}

	public String execute() {
		if (merchantList == null || merchantList.size() == 0) {
			String segment = (String) sessionMap.get(Constants.SEGMENT.getValue());
			User user = (User) sessionMap.get(Constants.USER.getValue());
			long roleId = user.getRole().getId();
			setMerchantList(userDao.getActiveMerchant(segment, roleId));
		}

		if (statuss == null || statuss.size() == 0) {
			statuss.add("Success");
			statuss.add("Pending");
			statuss.add("Failed");
		}
		if (ruleTypee == null || ruleTypee.size() == 0) {
			ruleTypee.add("Block");
			ruleTypee.add("Notify");
		}

		if (StringUtils.isNoneBlank(merchant, dateRange, status, ruleType)) {
			setAaData(fraudTransactionDetailsDao.getByFilter(merchant, dateRange, status, ruleType));
			request.setAttribute("dateRange", dateRange);
			request.getAttribute("dateRange");
		}

		return SUCCESS;
	}

	public List<FraudTransactionDetails> getAaData() {
		return aaData;
	}

	public void setAaData(List<FraudTransactionDetails> aaData) {
		this.aaData = aaData;
	}
}
