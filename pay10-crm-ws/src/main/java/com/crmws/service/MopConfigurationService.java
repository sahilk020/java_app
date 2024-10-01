package com.crmws.service;

import com.pay10.commons.user.QuomoCurrencyConfiguration;

import java.util.List;

public interface MopConfigurationService {

    public List<QuomoCurrencyConfiguration> findMopConfiguration(String acquirer, String currency, String paymentType);

    public  String editMopConfiguration(String id, String bankMopType, String bankName);

    public String saveMopConfiguration(String acquirer, String currency, String currencyCode, String paymentType, String MopType, String bankMopType, String bankName, String username);
}
