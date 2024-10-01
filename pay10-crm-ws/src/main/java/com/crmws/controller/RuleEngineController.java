package com.crmws.controller;

import com.pay10.commons.dao.RouterRuleDao;
import com.pay10.commons.user.MultCurrencyCodeDao;
import com.pay10.commons.user.RouterRule;
import com.pay10.commons.user.TdrSetting;
import com.pay10.commons.user.UserDao;
import com.pay10.commons.util.MopType;
import com.pay10.commons.util.PaymentType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/ruleEngin")
public class RuleEngineController {

    public static final Logger logger = LoggerFactory.getLogger(RuleEngineController.class.getName());

    @Autowired
    private RouterRuleDao routerRuleDao;

    @Autowired
    private UserDao userDao;

    @Autowired
    private MultCurrencyCodeDao multCurrencyCodeDao;
    @GetMapping("/getAcquirerForSmartRouter")
    public List<String> getAcquirerForSmartRouter(@RequestParam String merchantName, @RequestParam String currency, @RequestParam String paymentType, @RequestParam String mopType){

        TdrSetting tdrSetting = new TdrSetting();
        List<String> smartRouterAcquirer = new ArrayList<>();
        tdrSetting.setPayId(userDao.getPayIdByMerchantName(merchantName));
        tdrSetting.setCurrency(multCurrencyCodeDao.getCurrencyCodeByName(currency));
        tdrSetting.setPaymentType( PaymentType.getInstance(paymentType).toString());
        tdrSetting.setMopType(MopType.getInstance(mopType).toString());
        logger.info("payId: "+tdrSetting.getPayId());
        logger.info("Currency :"+tdrSetting.getCurrency());
        logger.info("Payment Type :"+tdrSetting.getPaymentType());
        logger.info("Mop Type :"+tdrSetting.getMopType());

        smartRouterAcquirer = routerRuleDao.getSmartRouterAcquirer(tdrSetting);


        return smartRouterAcquirer;
    }

}
