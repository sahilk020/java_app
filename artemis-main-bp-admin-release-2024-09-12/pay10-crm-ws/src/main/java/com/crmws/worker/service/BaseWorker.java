package com.crmws.worker.service;

import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.ObjectUtils;
import org.springframework.util.CollectionUtils;

import com.crmws.worker.dto.Parameter;
import com.crmws.worker.dto.WorkerRequest;
import com.crmws.worker.dto.WorkerResponse;
import com.crmws.worker.intf.WorkerIntf;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public abstract class BaseWorker<R> implements WorkerIntf{
	
	protected abstract R process(Map<String,Object> params);
	
	List<Parameter> parameters = defineParameters();
	
	@Override
	public WorkerResponse<R> execute(WorkerRequest request) throws Exception {
		// TODO Auto-generated method stub
		validateParams(request.getParams());
		WorkerResponse<R> workerResponse = new WorkerResponse<>();
		log.info("Executing function "+ identifierName());
		R response= process(request.getParams());
		workerResponse.setResponse(response);
		return workerResponse ;
	}

	private void validateParams(Map<String, Object> params) {
		// TODO Auto-generated method stub
		if(!CollectionUtils.isEmpty(params))
		{
			parameters.forEach(parameter -> {
				if(parameter.isMandatory() && (!params.containsKey(parameter.getKey()) 
						|| org.springframework.util.ObjectUtils.isEmpty(params.get(parameter.getKey()))	
						))
				{
					throw new RuntimeException("Missing Mandatory Parameter "+parameter.getKey());
				}
				
			});
		}
	}
	
	

}
