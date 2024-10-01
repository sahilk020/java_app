package com.crmws.service;

import com.pay10.commons.user.TdrSetting;

import java.util.List;

public interface TdrSettingService {

    public List<String> findTdrCurrencyList(String acquirer, String emailId);
}
