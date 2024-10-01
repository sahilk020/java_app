package com.crmws.controller;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


import com.crmws.entity.ResponseMessage;
import com.crmws.service.impl.VersionControlServicesImpl;
import com.pay10.commons.entity.VersionControlEntity;

@CrossOrigin
@RestController
@RequestMapping("VersionControl")
public class VersionControl {
	@Autowired
	private VersionControlServicesImpl controlServicesImpl;
	
	@PostMapping("/updateVersion/{type}")
	public VersionControlEntity updateVersion(@PathVariable String type) {
		VersionControlEntity controlEntity2= new VersionControlEntity();
		
		if(StringUtils.isBlank(type)){
			controlEntity2.setVersion("Type Is null");
			return controlEntity2;
		}else {
			return controlServicesImpl.updateAndInsertVersion(type);
		}
		
	}
	
	@PostMapping("/getVersion")
	public ResponseEntity<ResponseMessage> getVersion() {
		
		
		return controlServicesImpl.getVersion();
	
	}
	
}
