package com.crmws.service;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;

public interface LiabilityHoldReleaseService {
	
	public void hold(List<?> pgrefNum,String remakrs,String user);
	public void release(List<?> pgrefNum,String remakrs,String user);
	public String bulkUploadLiablityHoldAndRelease(MultipartFile  formData,String type,String user);
	
}
