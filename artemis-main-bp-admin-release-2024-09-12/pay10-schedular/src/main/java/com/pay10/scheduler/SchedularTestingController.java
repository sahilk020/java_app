package com.pay10.scheduler;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class SchedularTestingController {
	
	@Autowired
	private TransactionStatusEnquiryService transactionStatusEnquiryService;
	
	 @GetMapping("/schedularTest")
	 public  void schedularTest() {
		transactionStatusEnquiryService.fetchPendingTxnDataFirstTime();
	}

}
