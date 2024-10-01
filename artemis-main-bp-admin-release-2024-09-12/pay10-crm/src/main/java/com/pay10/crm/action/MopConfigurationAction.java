package com.pay10.crm.action;

import com.pay10.commons.dao.QuomoCurrencyConfigurationDao;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

public class MopConfigurationAction extends AbstractSecureAction{

    private static Logger logger = LoggerFactory.getLogger(MopConfigurationAction.class.getName());

    @Autowired
    private QuomoCurrencyConfigurationDao quomoCurrencyConfigurationDao;

    private String id;

    private String acquirer;

    private String currency;

    private String paymentType;

    private String currencyCode;

    private String bankMopType;

    private String bankName;




    @Override
    public String execute() {


        return SUCCESS;
    }
}
