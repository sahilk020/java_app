package com.crmws.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.crmws.dto.AutorefundDto;
import com.crmws.service.AutoRefundservice;
import com.pay10.commons.user.UserDao;

@RestController
@RequestMapping("/Autorefundcon")
public class AutoRefundController {
	private static Logger logger = LoggerFactory.getLogger(AutoRefundController.class.getName());

	@Autowired
	UserDao userdao;

	@Autowired
	AutoRefundservice autoRefundService;

	@GetMapping("/AutoRefund")
	public String Autorefundintegration(@RequestParam String PG_REF_NUM) {

		AutorefundDto Merchantid = autoRefundService.GetPayidByPgrefno(PG_REF_NUM);
		logger.info("pay Id for merchant ={}", Merchantid.toString());
		boolean refundStatus = autoRefundService.getrefundSatusByPayid(Merchantid);
		logger.info("Refund Status for merchant ={}", refundStatus);
		if (refundStatus) {
			String refundpgref = autoRefundService.refundintiated(PG_REF_NUM);
			return "refunded  done " + PG_REF_NUM;
		}

		return "refund it not done  ;" + PG_REF_NUM;
	}
}
