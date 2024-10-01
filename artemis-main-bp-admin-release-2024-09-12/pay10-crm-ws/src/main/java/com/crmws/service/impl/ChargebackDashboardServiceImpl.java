package com.crmws.service.impl;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.crmws.entity.ResponseMessageChargebackDashboard;
import com.crmws.service.ChargebackDashboardService;
import com.pay10.commons.repository.DMSRepository;

@Service
public class ChargebackDashboardServiceImpl implements ChargebackDashboardService {
	private static final Logger logger = LoggerFactory.getLogger(ChargebackDashboardServiceImpl.class.getName());

	@Autowired
	private DMSRepository dmsRepository;

	@Override
	public ResponseMessageChargebackDashboard getChargebackCount(String merchant, String acquirer, String dateFrom,
			String dateTo) {
		logger.info("merchant : " + merchant + "\t acquirer : " + acquirer + "\tdateFrom : " + dateFrom + "\t dateTo :"
				+ dateTo);

		try {
			Long toatlCount = dmsRepository.totalCount(merchant, acquirer,dateFrom,dateTo);
			Long recoveredFromMerchant = dmsRepository.recoveredFromMerchant(merchant, acquirer,dateFrom,dateTo);
			Long debitedFromBank = dmsRepository.debitedFromBank(merchant, acquirer,dateFrom,dateTo);
			Long defended = dmsRepository.defended(merchant, acquirer,dateFrom,dateTo);
			Long accepted = dmsRepository.accepted(merchant, acquirer,dateFrom,dateTo);

			Map<String, Long> map = new HashMap<>();
			map.put("toatlCount", toatlCount);
			map.put("recoveredFromMerchant", recoveredFromMerchant);
			map.put("debitedFromBank", debitedFromBank);
			map.put("defended", defended);
			map.put("accepted", accepted);
			
			logger.info("Map value : "+map);
			return new ResponseMessageChargebackDashboard("Success", HttpStatus.OK, map);
		} catch (Exception e) {
			logger.error("Exception occur in getChargebackCount() ", e);
			e.printStackTrace();

			Map<String, Long> map = new HashMap<>();
			map.put("toatlCount", (long) 0);
			map.put("recoveredFromMerchant", (long) 0);
			map.put("debitedFromBank", (long) 0);
			map.put("defended", (long) 0);
			map.put("accepted", (long) 0);

			return new ResponseMessageChargebackDashboard("Something Went wrong", HttpStatus.BAD_REQUEST, map);
		}
	}
}
