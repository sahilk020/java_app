
package com.crmws.service.impl;



import java.math.BigDecimal;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;



import com.crmws.service.TDRAndSurchargeCalculatorService;
import com.pay10.commons.user.TdrSetting;
import com.pay10.commons.user.TdrSettingDao;




@Service
public class TdrAndSurchargeCalculatorImpl implements TDRAndSurchargeCalculatorService {
	private static final Logger logger = LoggerFactory.getLogger(TdrAndSurchargeCalculatorImpl.class.getName());


	@Autowired
	private TdrSettingDao tdrSettingDao;

	@Override
	public String[] getTdrAndSurcharge(String payId, String paymentType, String acquirerType,
			String mopType, String transactionType, String currency, String amount,String date) {
		logger.info("Inside getTdrAndSurcharge() ");
		String[] val =null;
		try {
			val=tdrSettingDao.calculateTdrAndSurcharge(payId, paymentType, acquirerType, mopType, transactionType, currency, amount,date,"","");
			
		} catch (Exception e) {
			logger.error("Inside getTdrAndSurcharge()",e);
			e.printStackTrace();
		}
		logger.info("Response : "+val);
		return val;
	}
	}
