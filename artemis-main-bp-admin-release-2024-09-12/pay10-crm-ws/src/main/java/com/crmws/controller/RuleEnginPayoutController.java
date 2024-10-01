package com.crmws.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.pay10.commons.dao.RouterRuleDao;
import com.pay10.commons.user.*;
import com.pay10.commons.util.PaymentType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.crmws.service.impl.RuleEnginService;
import com.pay10.commons.dao.RouterConfigurationDao;

@RestController
@RequestMapping("/ruleEnginPayout")
public class RuleEnginPayoutController {
	
	private static final Logger logger = LoggerFactory.getLogger(RuleEnginPayoutController.class.getName());
	
	@Autowired
	RouterConfigurationPayoutDao routerConfigurationPayoutDao;
	
	@Autowired
	TdrSettingPayoutDao tdrSettingPayoutDao;
	
	@Autowired
	RuleEnginService ruleEnginService;

	@Autowired
	private UserDao userDao;

	@Autowired
	private RouterRuleDaoPayout routerRuleDaoPayout;
	
	
	
	@GetMapping("/getCurrencyList")
	public List<String> getCurrencyList(@RequestParam String acquirer, @RequestParam String payId,@RequestParam String channel) {

		List<String> currencyList = tdrSettingPayoutDao.getCurrencyListByPayId(acquirer, payId,channel);
		logger.info("size for array List" + currencyList.size());

		return currencyList;

	}
	

	@GetMapping("/getAcquirerForSmartRouterPayout")
	public List<String> getAcquirerForSmartRouterPayout(@RequestParam String merchantName, @RequestParam String currency, @RequestParam String channel){

		RouterRulePayout routerRulePayout = new RouterRulePayout();
		List<String> smartRouterAcquirer = new ArrayList<>();
		logger.info("Merchant Name :"+merchantName);
		logger.info("Currency :"+currency);
		logger.info("Channel :"+channel);
		routerRulePayout.setPayId(userDao.getPayIdByMerchantName(merchantName));
		routerRulePayout.setCurrency(currency);
		routerRulePayout.setChannel(channel);
		smartRouterAcquirer = routerRuleDaoPayout.getSmartRouterAcquirerPayout(routerRulePayout);

		return smartRouterAcquirer;
	}
}
