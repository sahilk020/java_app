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
import com.pay10.crm.mongoReports.TransactionStatus;

public class PreMisSettlementReport extends AbstractSecureAction{

	private static final long serialVersionUID = -3778914629739413709L;
	private static Logger logger = LoggerFactory.getLogger(MerchantWiseReport.class.getName());
	@Autowired
	private UserDao userDao;
	@Autowired
	private TransactionStatus transactionStatus;
	private User sessionUser = null;
	private List<Merchants> merchantList = new ArrayList<Merchants>();
	HttpServletRequest request = ServletActionContext.getRequest();

	private String payId;
	private String dateFrom;
	

	public String getPayId() {
		return payId;
	}

	public void setPayId(String payId) {
		this.payId = payId;
	}

	

	

	public String getDateFrom() {
		return dateFrom;
	}

	public void setDateFrom(String dateFrom) {
		this.dateFrom = dateFrom;
	}

	public List<Merchants> getMerchantList() {
		return merchantList;
	}

	public void setMerchantList(List<Merchants> merchantList) {
		this.merchantList = merchantList;
	}

	List<TransactionStatusBean> transactionStatusBeans = new ArrayList<TransactionStatusBean>();

	public List<TransactionStatusBean> getTransactionStatusBeans() {
		return transactionStatusBeans;
	}

	public void setTransactionStatusBeans(List<TransactionStatusBean> transactionStatusBeans) {
		this.transactionStatusBeans = transactionStatusBeans;
	}

	@SuppressWarnings("unchecked")
	@Override
	public String execute() {
		try {
			sessionUser = (User) sessionMap.get(Constants.USER.getValue());
			logger.info("User Type : " + sessionUser.getUserType());
//			if (sessionUser.getUserType().equals(UserType.ADMIN)
//					|| sessionUser.getUserType().equals(UserType.SUBADMIN)) {
				if (merchantList.size()==0 || merchantList.size()<0) {
					 merchantList = userDao
								.getMerchantList(sessionMap.get(Constants.SEGMENT.getValue()).toString(), sessionUser.getRole().getId());
				}
				logger.info("MERCHANT LIST : \t" + merchantList.size());
//				if (payId != null && dateFrom != null) {
//					setTransactionStatusBeans(
//							transactionStatus.getPreMisSettlementReport(payId, dateFrom));
//					
//				}
				request.setAttribute("dateFrom",dateFrom);
//			}
		} catch (Exception e) {
			logger.info("Exception Occur : " + e.getMessage());
			e.printStackTrace();
		}
		return INPUT;
	}
}
