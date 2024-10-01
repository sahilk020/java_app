package com.crmws.controller;

import com.crmws.service.impl.TdrSettingServiceImpl;
import com.pay10.commons.user.MultCurrencyCodeDao;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.crmws.service.impl.TdrAndSurchargeCalculatorImpl;
import com.pay10.commons.user.TdrSetting;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@CrossOrigin
@RestController
@RequestMapping("TdrAndSurcharge")
public class TdrAndSurchargeCalculator {
	private static Logger logger = LoggerFactory.getLogger(TdrAndSurchargeCalculator.class.getName());

	@Autowired
	private TdrAndSurchargeCalculatorImpl tdrAndSurchargeCalculatorImpl;

	@Autowired
	private TdrSettingServiceImpl tdrSettingService;

	@Autowired
	private MultCurrencyCodeDao multCurrencyCodeDao;

	@GetMapping("/GetTdrAndSurcharge/{payId}/{paymentType}/{acquirerType}/{mopType}/{transactionType}/{currency}/{amount}/{date}")
	public String[] getTdrAndSurcharge(@PathVariable String payId, @PathVariable String paymentType,
									   @PathVariable String acquirerType, @PathVariable String mopType, @PathVariable String transactionType,
									   @PathVariable String currency, @PathVariable String amount, @PathVariable String date) {
		return tdrAndSurchargeCalculatorImpl.getTdrAndSurcharge(payId, paymentType, acquirerType, mopType, transactionType, currency, amount, date);
	}

	@GetMapping("/getCurrencyList")
	public Map<String, String> getTdrCurrencyList(@RequestParam String emailId, @RequestParam String acquirer) {

		List<String> currencyList = tdrSettingService.findTdrCurrencyList(acquirer, emailId);
		Map<String, String> currencyNameList = new HashMap<>();



		for (String currency : currencyList) {
			String cName = multCurrencyCodeDao.getCurrencyNamebyCode(currency);
			currencyNameList.put(currency, cName);
		}
		logger.info("currencyNameList :::::::::::::"+currencyNameList);

		return currencyNameList;
	}

}

	