package com.crmws.controller;


import com.crmws.service.ChargeBackSchedular;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;



import com.crmws.entity.ResponseMessage;
import com.crmws.service.ChargebackBulkMailService;

import java.sql.SQLException;


@CrossOrigin
@RestController
@RequestMapping("ChargebackMail")
public class ChargebackBulkMail {

	private static final Logger logger = LoggerFactory.getLogger(ChargebackBulkMail.class.getName());

	@Autowired
	private ChargebackBulkMailService bulkMailService;
	@Autowired
	private ChargeBackSchedular chargeBackSchedular;
	@PostMapping("/ChargebackBulkMail")
	public ResponseEntity<ResponseMessage> sendBulkEmailForChargeBack() {

		String status=bulkMailService.sendBulkMail();
		
		return ResponseEntity.status(HttpStatus.OK)
				.body((new ResponseMessage(status, HttpStatus.OK)));
	}
	@GetMapping("/chargebackAutoClose")
	public String getChargeBacks() throws SQLException {
		return chargeBackSchedular.closeChargeback();
	}
}