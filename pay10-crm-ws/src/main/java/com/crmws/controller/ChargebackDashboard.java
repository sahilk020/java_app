package com.crmws.controller;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.crmws.entity.ResponseMessageChargebackDashboard;
import com.crmws.service.impl.ChargebackDashboardServiceImpl;


@CrossOrigin
@RestController
@RequestMapping("ChargebackDashboard")
public class ChargebackDashboard {

	@Autowired
	private ChargebackDashboardServiceImpl chargebackDashboardServiceImpl;
	
	@GetMapping("/getChargebackCount/{merchant}/{acquirer}/{dateFrom}/{dateTo}")
	public ResponseEntity<ResponseMessageChargebackDashboard> getChargebackCount(@PathVariable String merchant,
			@PathVariable String acquirer, @PathVariable String dateFrom, @PathVariable String dateTo) {
		ResponseMessageChargebackDashboard message = chargebackDashboardServiceImpl.getChargebackCount(merchant, acquirer, dateFrom, dateTo);
		return ResponseEntity.status(message.getHttpStatus()).body(message);
	}

}