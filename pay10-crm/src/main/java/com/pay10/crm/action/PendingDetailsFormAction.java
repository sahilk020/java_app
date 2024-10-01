package com.pay10.crm.action;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.pay10.commons.exception.ErrorType;
import com.pay10.commons.user.MapList;
import com.pay10.commons.user.Merchants;
import com.pay10.commons.user.RouterConfiguration;
import com.pay10.commons.user.ServiceTax;
import com.pay10.commons.user.SurchargeDetails;
import com.pay10.commons.user.SurchargeMappingPopulator;
import com.pay10.commons.user.User;
import com.pay10.commons.user.UserDao;
import com.pay10.commons.util.Constants;
import com.pay10.crm.actionBeans.PendingDetailsFactory;
/**
 * @author Rahul, Shaiwal
 *
 */
public class PendingDetailsFormAction extends AbstractSecureAction {
	
	@Autowired
	private UserDao userDao;
	
	@Autowired
	private PendingDetailsFactory pendingDetailsFactory;

	private static Logger logger = LoggerFactory
			.getLogger(PendingDetailsFormAction.class.getName());
	private static final long serialVersionUID = -6879974923614009981L;

	public List<Merchants> listMerchant = new ArrayList<Merchants>();
	private Map<String, List<SurchargeDetails>> surchargeMerchantData = new HashMap<String, List<SurchargeDetails>>();
	private Map<String, List<SurchargeMappingPopulator>> surchargePGData = new HashMap<String, List<SurchargeMappingPopulator>>();
	private Map<String, List<ServiceTax>> serviceTaxData = new HashMap<String, List<ServiceTax>>();
	private Map<String, List<RouterConfiguration>> smartRouterData = new HashMap<String,List<RouterConfiguration>>();
	private Map<String, List<MapList>> testData = new HashMap<String, List<MapList>>();
	private User sessionUser = null;
	
	@Override
	@SuppressWarnings("unchecked")
	public String execute() {
		//setListMerchant(userDao.getMerchantList());
		sessionUser = (User) sessionMap.get(Constants.USER.getValue());
		setListMerchant(userDao.getMerchantList(sessionMap.get(Constants.SEGMENT.getValue()).toString(), sessionUser.getRole().getId()));

		try {
				
				setSurchargeMerchantData(pendingDetailsFactory.getPendingSurchargeDetails());
				setSurchargePGData(pendingDetailsFactory.getPendingPGSurchargeDetails());
				setServiceTaxData(pendingDetailsFactory.getPendingServiceTax());
				setTestData(pendingDetailsFactory.getTestData());
				setSmartRouterData(pendingDetailsFactory.getPendingRouterConfiguration());
				if(surchargeMerchantData.size()==0 && surchargePGData.size() ==0 ){
				}
				
		}catch (Exception exception) {
			logger.error("Exception", exception);
			addActionMessage(ErrorType.UNKNOWN.getResponseMessage());
		}
		return INPUT;
	}

	// To display page without using token
	@SuppressWarnings("unchecked")
	public String displayList() {
		//setListMerchant(userDao.getMerchantList());
		sessionUser = (User) sessionMap.get(Constants.USER.getValue());
		setListMerchant(userDao.getMerchantList(sessionMap.get(Constants.SEGMENT.getValue()).toString(), sessionUser.getRole().getId()));
		return INPUT;
	}

	@Override
	public void validate() {
	}

	public List<Merchants> getListMerchant() {
		return listMerchant;
	}

	public void setListMerchant(List<Merchants> listMerchant) {
		this.listMerchant = listMerchant;
	}

	public Map<String, List<SurchargeDetails>> getSurchargeMerchantData() {
		return surchargeMerchantData;
	}

	public void setSurchargeMerchantData(Map<String, List<SurchargeDetails>> surchargeMerchantData) {
		this.surchargeMerchantData = surchargeMerchantData;
	}

	public Map<String, List<SurchargeMappingPopulator>> getSurchargePGData() {
		return surchargePGData;
	}

	public void setSurchargePGData(Map<String, List<SurchargeMappingPopulator>> surchargePGData) {
		this.surchargePGData = surchargePGData;
	}

	public Map<String, List<ServiceTax>> getServiceTaxData() {
		return serviceTaxData;
	}

	public void setServiceTaxData(Map<String, List<ServiceTax>> serviceTaxData) {
		this.serviceTaxData = serviceTaxData;
	}


	public Map<String, List<MapList>> getTestData() {
		return testData;
	}

	public void setTestData(Map<String, List<MapList>> testData) {
		this.testData = testData;
	}

	public Map<String, List<RouterConfiguration>> getSmartRouterData() {
		return smartRouterData;
	}

	public void setSmartRouterData(Map<String, List<RouterConfiguration>> smartRouterData) {
		this.smartRouterData = smartRouterData;
	}


}
