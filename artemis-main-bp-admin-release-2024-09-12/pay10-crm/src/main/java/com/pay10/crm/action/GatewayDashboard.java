package com.pay10.crm.action;

import java.util.ArrayList;
import java.util.Collections;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts2.ServletActionContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.google.gson.Gson;
import com.pay10.commons.util.AcquirerTypeUI;
import com.pay10.commons.util.PropertiesManager;
import com.pay10.crm.mongoReports.GatewayDashboardService;


public class GatewayDashboard extends AbstractSecureAction{
	private static final long serialVersionUID = 924993160136571311L;
	private static Logger logger = LoggerFactory.getLogger(GatewayDashboard.class.getName());
	HttpServletRequest request = ServletActionContext.getRequest();
	HttpServletResponse response= ServletActionContext.getResponse();
	@Autowired
	GatewayDashboardService gatewayDashboardService;
	@Autowired
	private PropertiesManager propertiesManager;
	@Override
	public String execute() {
		try {
			logger.info("GatewayDashboard in execute()");
			AcquirerTypeUI []acquirerTypeUIs=AcquirerTypeUI.values();
			ArrayList<String>arrayList=new ArrayList<>();
			for (AcquirerTypeUI acquirerTypeUI : acquirerTypeUIs) {
				arrayList.add(acquirerTypeUI.toString());
			}
			Collections.sort(arrayList);
			request.setAttribute("acquirerTypeUIs", arrayList);
			String	 acquirer  = request.getParameter("acquirer");
			logger.info("GatewayDashboard in execute() acquirer : "+acquirer);
			String payment_type=null;
			if (acquirer!=null) {
				payment_type = propertiesManager.propertiesMap.get(acquirer);
				response.getWriter().flush();
				response.setContentType("application/json");
				if (payment_type==null) {
				response.getWriter().println(new Gson().toJson("000"));
				}else {
					String output=gatewayDashboardService.getGatewayDashboardDetails(acquirer);
					logger.info("GatewayDashboard in execute() Final Output: "+output);
						response.getWriter().println(output);
				}
				response.getWriter().close();
			}
		} catch (Exception e) {
			logger.info("Exception in GatewayDashboard in execute() :"+e.getMessage());
			e.printStackTrace();
		}
		return SUCCESS;
	}
}
