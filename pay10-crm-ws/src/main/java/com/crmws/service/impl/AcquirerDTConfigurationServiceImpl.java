package com.crmws.service.impl;

import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.crmws.service.AcquirerDTConfigurationService;
import com.crmws.util.DateUtil;
import com.pay10.commons.dao.AcquirerDTConfigurationDao;
import com.pay10.commons.user.AcquirerDownTimeConfiguration;

@Service
public class AcquirerDTConfigurationServiceImpl implements AcquirerDTConfigurationService {
	private static final Logger logger = LoggerFactory.getLogger(AcquirerDTConfigurationServiceImpl.class);
	@Autowired
	AcquirerDTConfigurationDao acquirerDTConfigurationDao;

	@Override
	public boolean checkDuplicate(Map<String, String> acqDownTimeReq) {
		long cnt = acquirerDTConfigurationDao.getAcquirerDTConfig(acqDownTimeReq.get("acquirerName"),
				acqDownTimeReq.get("paymentType"));
		if (cnt > 0) {
			return true;
		}
		return false;
	}

	@Override
	public boolean addAcqDTConfig(Map<String, String> acqDownTimeReq) {
		try {
			AcquirerDownTimeConfiguration requestInfo = new AcquirerDownTimeConfiguration();
			requestInfo.setAcquirerName(acqDownTimeReq.get("acquirerName"));
			requestInfo.setFailedCount(acqDownTimeReq.get("failedCount"));
			requestInfo.setMopType(acqDownTimeReq.get("mopType"));
			requestInfo.setPaymentType(acqDownTimeReq.get("paymentType"));
			requestInfo.setStatus("ACTIVE");
			requestInfo.setTimeSlab(acqDownTimeReq.get("timeSlab"));
			requestInfo.setCreatedBy("ADMIN");
			requestInfo.setCreatedOn(DateUtil.getCurrentTime("YYYY-MM-dd hh:mm:ss"));
			acquirerDTConfigurationDao.save(requestInfo);
			return true;
		} catch (Exception e) {
			logger.info("Exception occured time of  add Acquirer DownTime Configuration!!" + e);
			return false;
		}

	}

	@Override
	public List<AcquirerDownTimeConfiguration> fetchAcquirerDTConfigList() {
		return acquirerDTConfigurationDao.getAllAcquirerDTConfig();
	}
	
	@Override
	public List<AcquirerDownTimeConfiguration> fetchAcquirerDTConfigListSearch(String acquirer,String paymentType) {
		return acquirerDTConfigurationDao.getAllAcquirerDTConfigSearch(acquirer,paymentType);
	}

	@Override
	public boolean checkDuplicateById(Map<String, String> acqDownTimeReq) {
		long cnt = acquirerDTConfigurationDao.getAcquirerDTConfigById(acqDownTimeReq.get("acquirerName"),
				acqDownTimeReq.get("paymentType"), Long.valueOf(acqDownTimeReq.get("id")));
		if (cnt > 0) {
			return true;
		}
		return false;
	}

	@Override
	public boolean updateAcqDTConfig(Map<String, String> acqDownTimeReq) {
		try {
			AcquirerDownTimeConfiguration requestInfo = acquirerDTConfigurationDao
					.getAcquirerDTConfig(Long.valueOf(acqDownTimeReq.get("id")));
			requestInfo.setFailedCount(acqDownTimeReq.get("failedCount"));
			requestInfo.setTimeSlab(acqDownTimeReq.get("timeSlab"));
			requestInfo.setUpdateBy("ADMIN");
			requestInfo.setUpdatedDate(DateUtil.getCurrentTime("YYYY-MM-dd hh:mm:ss"));
			acquirerDTConfigurationDao.update(requestInfo);
			return true;
		} catch (Exception e) {
			logger.info("Exception occured time of  add Acquirer DownTime Configuration!!" + e);
			return false;
		}
	}

	@Override
	public boolean inactiveAcqDTConfigRule(String acquirerRuleId) {
		try {
			acquirerDTConfigurationDao.updateAcquirerDTCongfigRule(acquirerRuleId,
					DateUtil.getCurrentTime("YYYY-MM-dd hh:mm:ss"));
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	@Override
	public boolean checkDuplicateRuleId(String acquirerRuleId) {
		long cnt = acquirerDTConfigurationDao.getAcquirerDTConfigByRuleId(Long.valueOf(acquirerRuleId));
		if (cnt > 0) {
			return true;
		}
		return false;
	}

}
