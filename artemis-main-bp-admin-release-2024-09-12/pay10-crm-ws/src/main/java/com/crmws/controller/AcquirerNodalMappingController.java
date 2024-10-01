package com.crmws.controller;

import javax.ws.rs.POST;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.crmws.entity.ResponseMessageExceptionList;
import com.crmws.service.impl.ExceptionListServiceImpl;

@CrossOrigin
@RestController
@RequestMapping("AcquirerNodalMapping")
public class AcquirerNodalMappingController {
	
	@Autowired
	private ExceptionListServiceImpl exceptionListServiceImpl;
	
	@PostMapping("/{acquirerNodalMappingId}")
	public ResponseEntity<ResponseMessageExceptionList> RNS(@PathVariable String acquirerNodalMappingId) {
		ResponseMessageExceptionList message=new ResponseMessageExceptionList();
		message.setHttpStatus(HttpStatus.OK);
		if (StringUtils.isBlank(acquirerNodalMappingId)) {
			message.setRespmessage("Please Select PgRefNumber");
		}else {
		//	message.setRespmessage(exceptionListServiceImpl.RNS(pgRefNum));
		}
		return ResponseEntity.status(message.getHttpStatus()).body(message);
	}
	
	

}
