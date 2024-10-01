package com.crmws.controller;
import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.crmws.dto.EkycOtpRequestDTO;
import com.crmws.dto.EkycOtpResponseDTO;
import com.crmws.dto.EkycOtpVerRequestDTO;
import com.crmws.dto.EkycOtpVerResponseDTO;
import com.crmws.service.KycService;

@RestController
@RequestMapping("/ekyc")
public class KycController {
	
	private static final Logger logger = LoggerFactory.getLogger(KycController.class.getName());
	
	@Autowired
	private KycService kycService;

	@PostMapping("/sendAadharOTP")
	public ResponseEntity<EkycOtpResponseDTO> generateAadharOTP(@RequestBody EkycOtpRequestDTO ekycOtpRequestDTO,HttpServletRequest request) {
		logger.info("generateAadharOTP EKYC Request Data : " + ekycOtpRequestDTO.getHash());
		
		return ResponseEntity.ok(kycService.generateAadharOTP(ekycOtpRequestDTO,request));
	}
	
	@PostMapping("/verifyAadharOTP")
	public ResponseEntity<EkycOtpVerResponseDTO> verifyAadharOTP(@RequestBody EkycOtpVerRequestDTO ekycOtpVerRequestDTO,HttpServletRequest request) {
		logger.info("verifyAadharOTP EKYC Request Data : " + ekycOtpVerRequestDTO.getHash());
		return ResponseEntity.ok(kycService.verifyAadharOTP(ekycOtpVerRequestDTO,request));
	}
	
	
}
