package com.crmws.controller;




import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.pay10.commons.user.MultCurrencyCodeDao;
import com.pay10.commons.user.UserDao;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.crmws.dto.ExceptionListWrapper;
import com.crmws.entity.ResponseMessage;
import com.crmws.entity.ResponseMessageExceptionList;
import com.crmws.service.impl.ExceptionListServiceImpl;
import com.pay10.commons.util.CurrencyNumber;






@CrossOrigin
@RestController
@RequestMapping("CurrencyNumber")
public class CurrencyController{

	@Autowired
	private UserDao userDao;

	@Autowired
	MultCurrencyCodeDao multCurrencyCodeDao;

	private static final Logger logger = LoggerFactory.getLogger(CurrencyController.class.getName());
	
	@GetMapping("/currency/{code}")
	public int getDecimalFromCode(@PathVariable String code) {
		return Integer.parseInt(CurrencyNumber.getMultiplyerfromCode(code));
	}
	
	@GetMapping("/decimal/{instance}")
	public int getDecimalFromInstance(@PathVariable String instance) {
		return Integer.parseInt(CurrencyNumber.getDecimalfromInstance(instance));
	}

	@GetMapping("/getCurrencyList")
	public Map<String,String> getCurrencyList(@RequestParam String payId){

		List<String> currencyList=userDao.findCurrencyByPayIdForPO(payId);
		Map<String ,String> finalCurrencyList=new HashMap<>();
		for(String currencyCode: currencyList){
			finalCurrencyList.put(multCurrencyCodeDao.getCurrencyCodeByName(currencyCode),currencyCode);
		}
		return finalCurrencyList;

	}

	@GetMapping("/getCurrencySymbol")
	public String getCurrencySymbol(@RequestParam String currency){

		String currencySymbol = multCurrencyCodeDao.getSymbolbyCode(currency);
		logger.info("Currency Symbol :::"+currencySymbol);
		return currencySymbol;
	}
	
}