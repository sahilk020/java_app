package com.pay10.crm.action;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.pay10.commons.user.RouterRuleDaoPayout;
import com.pay10.commons.user.RouterRulePayout;
import com.pay10.commons.user.User;
import com.pay10.commons.user.UserDao;

/**
 * @author Rahul
 *
 */
public class AcquirerRouterListPayoutAction extends AbstractSecureAction{
	private static Logger logger = LoggerFactory.getLogger(AcquirerRouterListPayoutAction.class.getName());
	private static final long serialVersionUID = 2396085939269105787L;
	private List<RouterRulePayout> routerRules = new ArrayList<RouterRulePayout>();
	private List<RouterRulePayout> dbrouterRules = new ArrayList<RouterRulePayout>();

	
	@Autowired
	private UserDao userDao;
	
	@Autowired
	private RouterRuleDaoPayout routerRuleDao;
	
	private String payId;
	
	@Override
	public String execute(){
		try{
				dbrouterRules = routerRuleDao.getActiveRules(payId);
				for(RouterRulePayout routerRule : dbrouterRules){
				
				String merchantPayId = routerRule.getMerchant();
				if(!merchantPayId.equals("ALL MERCHANTS")){
				
				User merchantName = userDao.findPayId(routerRule.getMerchant());
				routerRule.setMerchant(merchantName.getBusinessName());
				routerRule.setPayId(merchantName.getPayId());
				}
				routerRule.setCurrency(routerRule.getCurrency());
				routerRule.setChannel(routerRule.getChannel());
				
				routerRules.add(routerRule);
				
				Comparator<RouterRulePayout> comp = (RouterRulePayout a, RouterRulePayout b) -> {

					if (b.getMerchant().compareTo(a.getMerchant()) < 0) {
						return -1;
					} else if (b.getMerchant().compareTo(a.getMerchant()) > 0) {
						return 1;
					}
					else {
						return 0;
					}
					
				};

				Collections.sort(routerRules, comp);
				
				setRouterRules(routerRules);
			}
			
			return SUCCESS;
		}catch(Exception exception){
			logger.error("Exception", exception);
			return ERROR;
		}
		
	}

	public List<RouterRulePayout> getDbrouterRules() {
		return dbrouterRules;
	}

	public void setDbrouterRules(List<RouterRulePayout> dbrouterRules) {
		this.dbrouterRules = dbrouterRules;
	}

	public List<RouterRulePayout> getRouterRules() {
		return routerRules;
	}

	public void setRouterRules(List<RouterRulePayout> routerRules) {
		this.routerRules = routerRules;
	}

	public String getPayId() {
		return payId;
	}

	public void setPayId(String payId) {
		this.payId = payId;
	}
	
}
