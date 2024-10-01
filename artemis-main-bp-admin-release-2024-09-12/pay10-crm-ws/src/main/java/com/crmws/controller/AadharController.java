package com.crmws.controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.crmws.service.AadharService;

@RestController
@RequestMapping("/aadhar")
@CrossOrigin("*")
public class AadharController {
	@Autowired
	private AadharService aadharService;

	@PostMapping("/generateAadharOTP")
	public Map<String, Object> generateAadharOTP(@RequestBody Map<String, String> reqMap) {
		return aadharService.generateAadharOTP(reqMap);
	}

	@PostMapping("/verifyAadharOTP")
	public Map<String, Object> verifyAadharOTP(@RequestBody Map<String, String> reqMap) {
		return aadharService.verifyAadharOTP(reqMap);
	}

}
