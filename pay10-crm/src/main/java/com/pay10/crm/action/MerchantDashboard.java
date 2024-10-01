package com.pay10.crm.action;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.apache.struts2.ServletActionContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;
import com.pay10.commons.user.Merchants;
import com.pay10.commons.user.User;
import com.pay10.commons.user.UserDao;
import com.pay10.commons.util.Acquirer;
import com.pay10.commons.util.Constants;
import com.pay10.crm.mongoReports.MerchantDashboardService;

public class MerchantDashboard extends AbstractSecureAction{

	private static final long serialVersionUID = 4402384578856102111L;
	
	private static Logger logger = LoggerFactory.getLogger(MerchantDashboard.class.getName());
	@Autowired
	private UserDao userDao;
	@Autowired
	private MerchantDashboardService merchantDashboardService;
	
	HttpServletRequest request = ServletActionContext.getRequest();
	HttpServletResponse response= ServletActionContext.getResponse();
	private User sessionUser = null;
	private List<Merchants> merchantList = new ArrayList<Merchants>();
	public List<Acquirer> listAcquirer = new ArrayList<Acquirer>();
	
	
	
	public List<Merchants> getMerchantList() {
		return merchantList;
	}



	public void setMerchantList(List<Merchants> merchantList) {
		this.merchantList = merchantList;
	}



	public List<Acquirer> getListAcquirer() {
		return listAcquirer;
	}



	public void setListAcquirer(List<Acquirer> listAcquirer) {
		this.listAcquirer = listAcquirer;
	}



	@SuppressWarnings("unchecked")
	public String showMerchantDashboardPage() {
		try {
			sessionUser = (User) sessionMap.get(Constants.USER.getValue());
			logger.info("MerchantDashboard in showMerchantDashboardPage() \tUser Type : "+sessionUser.getUserType());
		/*	if(sessionUser.getUserType().equals(UserType.ADMIN)
					|| sessionUser.getUserType().equals(UserType.SUBADMIN)) {*/
				setMerchantList(userDao.getMerchantList(sessionMap.get(Constants.SEGMENT.getValue()).toString(), sessionUser.getRole().getId()));
				logger.info("MerchantDashboard in showMerchantDashboardPage() \tMERCHANT LIST : \t"+merchantList.size());
//				setListAcquirer(userDao.getAcquirers());
				/* } */
		} catch (Exception e) {
			logger.info("Exception Occur in MerchantDashboard in showMerchantDashboardPage(): "+e.getMessage());
			e.printStackTrace();
		}
		return SUCCESS;
	}
	
	public void getTotalTransaction() {
		try {
			sessionUser = (User) sessionMap.get(Constants.USER.getValue());
			logger.info("MerchantDashboard in getTotalTransaction() \t User Type : "+sessionUser.getUserType());
			
			/*if(sessionUser.getUserType().equals(UserType.ADMIN)) {*/
				String date_range     = request.getParameter("dateRange");
				String merchant       = request.getParameter("merchant");
				String acquirer       = request.getParameter("acquirer");
				String paymentMethods = request.getParameter("paymentMethods");
				String moptype = request.getParameter("moptype");
				@SuppressWarnings("unchecked")
				List<Merchants> merchantList=userDao.getMerchantList(sessionMap.get(Constants.SEGMENT.getValue()).toString(), sessionUser.getRole().getId());
				List<String>payIdList=new ArrayList<>();
				for (Merchants merchants : merchantList) {
					payIdList.add(merchants.getPayId());
				}
				String output=merchantDashboardService.getDashBoardDetail(date_range,merchant,acquirer,paymentMethods,payIdList,moptype);
				logger.info("MerchantDashboard in getTotalTransaction() \tFinal Output :\n"+output);
				response.getWriter().flush();
				response.setContentType("application/json");
				response.getWriter().println(new Gson().toJson(output));
				response.getWriter().close();
				/* } */
		} catch (Exception e) {
			logger.info("Exception Occur in MerchantDashboard in getTotalTransaction(): "+e.getMessage());
			e.printStackTrace();
		}
	}
	
}
