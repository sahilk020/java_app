package com.crmws.controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.crmws.worker.dto.ResponseEnvelope;
import com.crmws.worker.dto.WorkerRequest;
import com.crmws.worker.dto.WorkerResponse;
import com.crmws.worker.service.WorkerService;
import com.crmws.worker.util.AppUtil;

@RestController
@RequestMapping(path = "/api/v1/worker", produces = MediaType.APPLICATION_JSON_VALUE)
@CrossOrigin(origins = "*")
public class WorkerController {
	
	
	@Autowired
	private WorkerService resourceService;

	@PostMapping(path = "/execute/{slug}")
	public ResponseEntity<ResponseEnvelope<WorkerResponse>> validateBySlug(@PathVariable(value = "slug") String slug,
			@RequestBody WorkerRequest body
			) {
		ResponseEnvelope<WorkerResponse> outResource = resourceService.executeWorker(slug,(body));
		return new ResponseEntity<>(outResource, outResource.getHttpStatus());
	}

}
