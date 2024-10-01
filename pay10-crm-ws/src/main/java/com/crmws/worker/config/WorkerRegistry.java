package com.crmws.worker.config;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.crmws.worker.intf.WorkerIntf;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class WorkerRegistry {
	
	@Autowired
	private List<WorkerIntf> workers;
	
	private Map<String,WorkerIntf> workerStore = new HashMap<>();
	
	
	@PostConstruct
	public void register() {
		
		workers.forEach(worker->{
			log.info("Registring Worker "+ worker.identifierName());
			workerStore.put(worker.identifier(), worker);
			log.info(String.format("Registered Worker %s with slug %s", worker.identifierName(),worker.identifier()));
		});
		
	}
	
	public WorkerIntf getWorkerBySlug(String slug)
	{
		return workerStore.get(slug);
	}

}


	