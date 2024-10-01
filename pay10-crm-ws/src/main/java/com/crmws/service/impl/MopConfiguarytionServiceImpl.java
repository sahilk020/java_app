package com.crmws.service.impl;

import com.crmws.service.MopConfigurationService;
import com.pay10.commons.dao.QuomoCurrencyConfigurationDao;
import com.pay10.commons.user.QuomoCurrencyConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MopConfiguarytionServiceImpl implements MopConfigurationService {

    @Autowired
    private QuomoCurrencyConfigurationDao quomoCurrencyConfigurationDao;
    @Override
    public List<QuomoCurrencyConfiguration> findMopConfiguration(String acquirer, String currency, String paymentType) {

        List<QuomoCurrencyConfiguration> getConfig = quomoCurrencyConfigurationDao.findByAcquirerCurrencyPaymentType(acquirer,currency,paymentType);

        return getConfig;
    }

    @Override
    public String editMopConfiguration(String id, String bankMopType, String bankName) {
        String status = "";


        return status;
    }

    @Override
    public String saveMopConfiguration(String acquirer, String currency, String currencyCode, String paymentType, String MopType, String bankMopType, String bankName, String username) {
        String status = "Save Successfully";


        return status;
    }
}
