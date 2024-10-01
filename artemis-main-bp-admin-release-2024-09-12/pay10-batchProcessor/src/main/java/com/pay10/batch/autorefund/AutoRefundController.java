package com.pay10.batch.autorefund;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
public class AutoRefundController {

	@Autowired
	AutoRefund autoRefund;

	@RequestMapping(value = "/a", method = RequestMethod.GET)
	public ResponseEntity<String> autoRefund(){
		System.out.println("Auto refund controller...");
//		try {
//			JSONObject jsonObj = new JSONObject(map);
//			autoRefund.initiateAutoRefund(jsonObj);
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
		return new ResponseEntity<>("Success...", HttpStatus.OK);
	}
}
