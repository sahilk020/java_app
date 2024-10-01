package com.crmws.service.impl;

import com.crmws.service.GlobalAcquirerSwitchService;
import com.pay10.commons.dao.RouterConfigurationDao;
import com.pay10.commons.user.RouterConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class GlobalAcquirerSwitchServiceImpl implements GlobalAcquirerSwitchService {

    @Autowired
    RouterConfigurationDao routerConfigurationDao;


    @Override
    public List<String> getPaymentTypeList(String acquirer) {
        return routerConfigurationDao.getPaymentTypeListByAcquirer(acquirer);
    }

    @Override
    public List<RouterConfiguration> getMappedListByAcquirer(String acquirer, String paymentType) {
        return routerConfigurationDao.getMappedByAcquirer(acquirer, paymentType);
    }

    @Override
    public List<RouterConfiguration> getMappedListByAcquirerForDelete(String acquirer, String paymentType) {
        return routerConfigurationDao.getMappedByAcquirerForDelete(acquirer, paymentType);
    }
}
