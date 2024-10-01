package com.pay10.notification.sms.service;

import java.util.Map;

import org.slf4j.Logger; import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.pay10.commons.util.Fields;

@RestController
public class SmsController {
	private static Logger logger = LoggerFactory.getLogger(SmsController.class.getName());
	
	/*@Autowired
	private SendPerformanceSmsService1 sendPerformanceSmsService1;*/
	@Autowired
	private TransactionStatusUpdateService transactionStatusUpdateService;
	
	@RequestMapping(method = RequestMethod.POST,value = "/sendPerformanceSms", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public void sendPerformanceSms(@RequestBody Map<String, String> reqMap) {
		Fields fields = new Fields(reqMap);
		//sendPerformanceSmsService1.sendPerformanceSms(fields.get("dateFrom").toString(), fields.get("dateTo").toString(), fields.get("merchantEmailId").toString(), fields.get("paymentMethods").toString(), fields.get("acquirer").toString(), fields.get("smsParam").toString());
		logger.info("Inside SendPerformanceSmsAction sendPerformanceSms Method");
		System.out.println("Inside SendPerformanceSmsAction sendPerformanceSms Method");
	}
	
	@RequestMapping(method = RequestMethod.POST, value = "/updateTxnStatus", consumes = MediaType.APPLICATION_JSON_VALUE)
	public void updateTxnStatus(@RequestBody Map<String, String> reqmap) {

		Fields fields = new Fields(reqmap);
		transactionStatusUpdateService.updateTxnStatus(fields);
		
	}
	
	@RequestMapping(method = RequestMethod.POST, value = "/updateOldTxnStatus", consumes = MediaType.APPLICATION_JSON_VALUE)
	public void updateOldTxnStatus(@RequestBody Map<String, String> reqmap) {

		transactionStatusUpdateService.updateOldTxnStatus(reqmap.get("FromDate").toString(),reqmap.get("ToDate").toString());
		
	}
	
	
}
