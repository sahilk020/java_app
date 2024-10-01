package com.crmws.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.crmws.entity.ResponseMessage;
import com.crmws.service.FileService;
import com.pay10.commons.repository.DMSRepository;
import com.pay10.commons.repository.Fileentitiy;
import com.pay10.commons.repository.fileRepository;

@Service
public class Fileserviceimpl implements FileService{
	private static final Logger logger = LoggerFactory.getLogger(Fileserviceimpl.class.getName());

	@Autowired
	private fileRepository filepro;
	
	@Override
	public ResponseMessage save(Fileentitiy fileentitiy) {
		logger.info("122222222222222222222222222222222222222"+fileentitiy.toString());
		filepro.save(fileentitiy);
		return new ResponseMessage("data saved successfully", HttpStatus.OK);
	}
	

}
