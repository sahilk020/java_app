package com.crmws.service;

import java.util.Map;

import org.springframework.web.multipart.MultipartFile;

import com.crmws.dto.ExceptionListWrapper;
import com.pay10.commons.user.User;


public interface ExceptionListService {
	public String RNS(String pgRefNum);
	public String refund(String pgRefNum);
	public Map<String,String> refundbulkUpload(ExceptionListWrapper pgRefNum);
	public String rnsBulkUpload(ExceptionListWrapper pgRefNum);
	public String exceptionListBulkUpload(MultipartFile formData,String type);
}
