package com.crmws.service.impl;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.crmws.service.ResellerService;
import com.pay10.commons.user.User;
import com.pay10.commons.user.UserDao;

@Service
public class ResellerServiceImpl implements ResellerService{
	
	
	private static final Logger logger = LoggerFactory.getLogger(ResellerServiceImpl.class.getName());

	
	@Autowired
	UserDao userDao;
	
	@Autowired
	private MongoTransactionDetails resellerdailyUpdate;

	
	@Override
	public boolean isMappedReseller(String payId) {
		boolean isMapped;
		try {
			
			User user = userDao.findByPayId(payId);
			isMapped = StringUtils.isNotBlank(user.getResellerId());
			logger.info("isMappedReseller:: payId={}, isExist={}", payId, isMapped);
			return isMapped;
		} catch (Exception ex) {
			logger.error("isMappedReseller:: failed. payId={}", payId, ex);
			throw new RuntimeException("Error While Mapping Reseller");
		}
	}
	
	@Override
	public JSONObject getTotalCount(String dateFrom, String dateTo, String resellerId, String merchantId,
			String paymentType) {
		JSONObject resellerCommissionReport = resellerdailyUpdate.getResellerCommsionReport(dateFrom,dateTo,resellerId,merchantId,paymentType);
		return resellerCommissionReport;
	}

	@Override
	public boolean isMappedUser(String payId) {
		boolean isMapped;
		try {
			
			User user = userDao.findByPayId(payId);
			isMapped = StringUtils.isNotBlank(user.getAgentId());
			logger.info("isMappedReseller:: payId={}, isExist={}", payId, isMapped);
			return isMapped;
		} catch (Exception ex) {
			logger.error("isMappedReseller:: failed. payId={}", payId, ex);
			throw new RuntimeException("Error While Mapping Reseller");
		}
	}

}
