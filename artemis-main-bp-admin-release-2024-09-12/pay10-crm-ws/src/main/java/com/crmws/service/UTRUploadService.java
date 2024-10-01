package com.crmws.service;

import org.springframework.web.multipart.MultipartFile;

public interface UTRUploadService {
	
	public String save(MultipartFile file);
	public String saveChargeback(MultipartFile file,String type);
	
}
