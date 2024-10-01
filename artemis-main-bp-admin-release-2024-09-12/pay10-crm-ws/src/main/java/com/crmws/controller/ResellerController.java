package com.crmws.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.apache.commons.lang3.StringUtils;
import org.json.JSONObject;


import com.crmws.service.ResellerService;

@RestController
@RequestMapping("/reseller")
public class ResellerController {
	
	@Autowired
	private ResellerService resellerService;
	
	@GetMapping("/mappingExists")
	public ResponseEntity<Boolean> isMappiongExists(@RequestParam("payId") String payId) {
		boolean isalreadyMapped = resellerService.isMappedReseller(payId);
		return ResponseEntity.ok(Boolean.valueOf(isalreadyMapped));
	}

   @GetMapping("/getResellerCommissionReport")
   public String getResellerCommissionData(String dateFrom, String dateTo,
			String resellerId, String merchantId, String paymentType) {
		System.out.println(dateFrom);
		System.out.println(dateTo);
		System.out.println(resellerId);
		System.out.println(merchantId);
		System.out.println(paymentType);
		resellerId=(StringUtils.isBlank(resellerId))?"ALL":resellerId;
		merchantId=(StringUtils.isBlank(merchantId))?"ALL":merchantId;
		paymentType=(StringUtils.isBlank(paymentType))?"ALL":paymentType;
		
		//logger.info(dateFrom,dateTo,resellerId,merchantId,paymentType);
		JSONObject js= resellerService.getTotalCount(dateFrom,dateTo,resellerId,merchantId,paymentType);
		return js.toString();		
		}
   
	@GetMapping("/mappingConfExists")
	public ResponseEntity<Boolean> isMappingConfExists(@RequestParam("payId") String payId) {
		boolean isalreadyMapped = resellerService.isMappedUser(payId);
		return ResponseEntity.ok(Boolean.valueOf(isalreadyMapped));
	}
}
