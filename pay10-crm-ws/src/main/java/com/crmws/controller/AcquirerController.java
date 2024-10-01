package com.crmws.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.crmws.dto.AcquirerDTO;
import com.crmws.service.AcquirerService;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestParam;
import com.crmws.service.impl.AcquirerServiceImpl;
import com.pay10.commons.user.UserDao;


@RestController
@RequestMapping("/acquirer")
public class AcquirerController {
	
	private static final Logger logger = LoggerFactory.getLogger(AcquirerController.class.getName());

	@Autowired
	private AcquirerService acquirerService;
    
	@Autowired
	private UserDao userDao;

	@GetMapping("/acquirerList")
	public ResponseEntity<List<AcquirerDTO>> getAcquirers() {
		return ResponseEntity.ok(acquirerService.getAcquirers());
	}
	
	@GetMapping("/getMappedAcquirer")
	public List<String> getAcquirerList(@RequestParam String emailId) {
		logger.info("emailId..={}",emailId);
		String payId= userDao.getPayIdByEmailId(emailId);
		logger.info("payId..={}",payId);
		List<String> acquirerList=acquirerService.getAcquirerList(payId);

		return acquirerList;
		
	}
	
	@GetMapping("/getMappedAcquirerForTdr")
	public List<String> getMappedAcquirerForTdr(@RequestParam String emailId) {
		logger.info("emailId..={}",emailId);
		String payId= userDao.getPayIdByEmailId(emailId);
		logger.info("payId..={}",payId);
		List<String> acquirerList=acquirerService.getMappedAcquirerForTdr(payId);

		return acquirerList;
		
	}
	
	


}
