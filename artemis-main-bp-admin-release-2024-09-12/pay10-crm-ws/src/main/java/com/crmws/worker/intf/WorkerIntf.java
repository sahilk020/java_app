package com.crmws.worker.intf;

import java.util.List;

import com.crmws.worker.dto.Parameter;
import com.crmws.worker.dto.WorkerRequest;
import com.crmws.worker.dto.WorkerResponse;
import com.crmws.worker.util.AppUtil;

public interface WorkerIntf {
	
	default String identifier() {
	    return AppUtil.generateSlug(identifierName());
	 }
	
	String identifierName();
	
	String description();
	
	List<Parameter> defineParameters();
	
	@SuppressWarnings("rawtypes")
	WorkerResponse execute(WorkerRequest request) throws Exception;

}
