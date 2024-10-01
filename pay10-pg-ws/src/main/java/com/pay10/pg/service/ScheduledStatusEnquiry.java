package com.pay10.pg.service;

import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.pay10.commons.user.ResponseObject;

@RestController
public class ScheduledStatusEnquiry {

	@RequestMapping(method = RequestMethod.POST, value = "/statusEnquiry")
	public String  fetchData(@RequestBody ResponseObject responseObject) throws Exception {
		// add to do
		return "success";
	}
}
