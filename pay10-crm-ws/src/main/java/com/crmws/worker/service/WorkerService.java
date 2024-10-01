package com.crmws.worker.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.crmws.worker.config.WorkerRegistry;
import com.crmws.worker.dto.ResponseEnvelope;
import com.crmws.worker.dto.WorkerRequest;
import com.crmws.worker.dto.WorkerResponse;
import com.crmws.worker.intf.WorkerIntf;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class WorkerService {
	
	@Autowired
	private WorkerRegistry workerRegistry;
	
	
	public ResponseEnvelope<WorkerResponse> executeWorker(String slug,WorkerRequest workerRequest) {

		WorkerIntf worker = workerRegistry.getWorkerBySlug(slug);
		WorkerResponse workerResponse;
		try {
			workerResponse = worker.execute(workerRequest);
		} catch (Exception e) {
			//e.printStackTrace();
			log.error(e.getMessage());
			return new ResponseEnvelope<WorkerResponse>(HttpStatus.INTERNAL_SERVER_ERROR,e.getMessage());
		}
        return new ResponseEnvelope<WorkerResponse>(HttpStatus.OK,workerResponse);
    }

}
