package com.crmws.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import com.pay10.commons.user.UserDao;
import com.pay10.commons.util.AcquirerType;

import com.crmws.dto.AcquirerDTO;
import com.crmws.service.AcquirerService;
import com.pay10.commons.util.AcquirerTypeUI;

@Service
public class AcquirerServiceImpl implements AcquirerService {

	private static final Logger logger = LoggerFactory.getLogger(AcquirerServiceImpl.class.getName());

	@Autowired
	private UserDao userDao;

	@Override
	public List<AcquirerDTO> getAcquirers() {
		List<AcquirerDTO> acquirers = new ArrayList<>();
		for (AcquirerTypeUI acquirerTypeUI : AcquirerTypeUI.values().clone()) {
			acquirers.add(new AcquirerDTO(acquirerTypeUI.getCode(), acquirerTypeUI.getName()));
			
		}
		
		acquirers.add(new AcquirerDTO("SBIUPI", "SBIUPI"));
		return acquirers;
	}

	@Override
	public List<String> getAcquirerList(String payId) {

		List<String> acqlist = userDao.getAcquirerTypeList(payId);
		List<String> list = new ArrayList<String>();
//		list.add(0, "Select Acquirer");
		logger.info("List of Acquirer..={}", acqlist.toString());
		for (String acquirer : acqlist) {
			list.add(AcquirerType.getInstancefromName(acquirer).getCode());
		}
		list.add("SBIUPI");
	     logger.info("list..={}",list);

		return list;
	}

	@Override
	public List<String> getMappedAcquirerForTdr(String payId) {

		
		List<String> acqlist = userDao.getAcquirerTypeListFromTdrSetting(payId);
		List<String> list = new ArrayList<String>();
//		list.add(0, "Select Acquirer");
		logger.info("List of Acquirer..={}", acqlist.toString());
		for (String acquirer : acqlist) {
			list.add(AcquirerType.getInstancefromName(acquirer).getCode());
		}
		logger.info("list..={}", list);

		return list;
	}

}
