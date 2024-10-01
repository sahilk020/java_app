package com.crmws.service.impl;

import com.crmws.service.TdrSettingService;
import com.pay10.commons.user.TdrSetting;
import com.pay10.commons.user.UserDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TdrSettingServiceImpl implements TdrSettingService{

    @Autowired
    UserDao userDao;

    @Override
    public List<String> findTdrCurrencyList(String acquirer, String emailId) {

        List<String> currencyListDao = userDao.findByAcquireAndPayId(acquirer,emailId);
        return currencyListDao;
    }
}
