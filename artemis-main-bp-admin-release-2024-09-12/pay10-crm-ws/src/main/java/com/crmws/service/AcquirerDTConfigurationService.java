package com.crmws.service;

import java.util.List;
import java.util.Map;

import com.pay10.commons.user.AcquirerDownTimeConfiguration;

public interface AcquirerDTConfigurationService {

	boolean checkDuplicate(Map<String, String> acqDownTimeReq);

	boolean addAcqDTConfig(Map<String, String> acqDownTimeReq);

	List<AcquirerDownTimeConfiguration> fetchAcquirerDTConfigList();
	
	List<AcquirerDownTimeConfiguration> fetchAcquirerDTConfigListSearch(String acquirer,String paymentType);


	boolean checkDuplicateById(Map<String, String> acqDownTimeReq);

	boolean updateAcqDTConfig(Map<String, String> acqDownTimeReq);

	boolean inactiveAcqDTConfigRule(String acquirerRuleId);

	boolean checkDuplicateRuleId(String acquirerRuleId);

}
