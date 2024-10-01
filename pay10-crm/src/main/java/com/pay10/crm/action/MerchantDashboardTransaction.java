package com.pay10.crm.action;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts2.ServletActionContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.pay10.commons.user.Merchants;
import com.pay10.commons.user.User;
import com.pay10.commons.user.UserDao;
import com.pay10.commons.util.Constants;
import com.pay10.crm.actionBeans.TransactionStatusBean;
import com.pay10.crm.mongoReports.MerchantDashboardService;

public class MerchantDashboardTransaction extends AbstractSecureAction{
	private static final long serialVersionUID = -633674343079079514L;
	private static Logger logger = LoggerFactory.getLogger(MerchantDashboardTransaction.class.getName());
	HttpServletRequest request = ServletActionContext.getRequest();
	@Autowired
	private MerchantDashboardService merchantDashboardService;
	@Autowired
	private UserDao userDao;
	private User sessionUser = null;
	List<TransactionStatusBean> beans = new ArrayList<>();
	private String mode;
	private String dateRange;
	private String merchant;
	private String acquirer;
	private String paymentMethods;
	private String moptype;
	
	
	public String getMoptype() {
		return moptype;
	}

	public void setMoptype(String moptype) {
		this.moptype = moptype;
	}

	public List<TransactionStatusBean> getBeans() {
		return beans;
	}

	public void setBeans(List<TransactionStatusBean> beans) {
		this.beans = beans;
	}

	public String getMode() {
		return mode;
	}

	public void setMode(String mode) {
		this.mode = mode;
	}

	public String getDateRange() {
		return dateRange;
	}

	public void setDateRange(String dateRange) {
		this.dateRange = dateRange;
	}

	public String getMerchant() {
		return merchant;
	}

	public void setMerchant(String merchant) {
		this.merchant = merchant;
	}

	public String getAcquirer() {
		return acquirer;
	}

	public void setAcquirer(String acquirer) {
		this.acquirer = acquirer;
	}

	public String getPaymentMethods() {
		return paymentMethods;
	}

	public void setPaymentMethods(String paymentMethods) {
		this.paymentMethods = paymentMethods;
	}





	@Override
	public String execute() {
		logger.info("Inside MerchantDashboardTransaction in execute()");
		sessionUser = (User) sessionMap.get(Constants.USER.getValue());
		@SuppressWarnings("unchecked")
		List<Merchants> merchantList=userDao.getMerchantList(sessionMap.get(Constants.SEGMENT.getValue()).toString(), sessionUser.getRole().getId());
		List<String>payIdList=new ArrayList<>();
		for (Merchants merchants : merchantList) {
			payIdList.add(merchants.getPayId());
		}
		setBeans(merchantDashboardService.getEachTransactionDetail(mode, merchant, acquirer, paymentMethods, dateRange,payIdList,moptype));
		request.setAttribute("mode", mode);
		
		return SUCCESS;
	}

}
