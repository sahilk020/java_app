package com.pay10.controller;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.pay10.pg.service.UptimeService;

@RestController
public class UptimeController {

	@Autowired
	UptimeService uptimeService;

	// PGWS uptime service : StatusCake will hit this API for monitoring.
	@GetMapping(value = "/uptime", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<String> uptime(HttpServletRequest req) {
		return uptimeService.getUptimeResponse(req);
	}


	// Add nodal Acquirer Accordingly
	@GetMapping(value = "/addNodalAcquirer", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<String> addNodalAcquirer(@RequestBody Map<String, String> map ,HttpServletRequest req) {
		return uptimeService.addNodalAcquirer(req, map);
	}
	
	// PGWS health check service : ALB will hit it for health check
	@GetMapping(value = "/healthCheck")
	public ResponseEntity<String> healthCheck(HttpServletRequest req) {
		return new ResponseEntity<>("ALL OK", HttpStatus.OK);

	}
}
