package com.crmws.service;
import com.pay10.commons.user.RouterConfiguration;
import java.util.List;

public interface GlobalAcquirerSwitchService {



    public List<String> getPaymentTypeList(String acquirer);

    public List<RouterConfiguration> getMappedListByAcquirer(String acquirer, String paymentType);

    public List<RouterConfiguration> getMappedListByAcquirerForDelete(String acquirer, String paymentType);

}
